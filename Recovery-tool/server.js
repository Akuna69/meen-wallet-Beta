#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const bitcoin = require('bitcoinjs-lib');
const axios = require('axios');
const readline = require('readline');

const CONFIG_PATH = path.join(__dirname, 'config.json');

// --- DECODIFICADOR DE LLAVES Y CÓDIGOS ---
function parseKey(inputStr, network) {
  const clean = inputStr.trim().replace(/[-_]/g, ' ');
  if (clean.startsWith('xprv') || clean.startsWith('tprv')) {
    return bitcoin.bip32.fromBase58(clean.replace(/\s+/g, ''), network);
  }
  const formatted = clean.replace(/\s+/g, '').toUpperCase();
  if (formatted.length >= 32) {
    const encoder = new TextEncoder();
    const hash = bitcoin.crypto.sha256(encoder.encode(formatted));
    return bitcoin.bip32.fromSeed(hash, network);
  }
  return bitcoin.bip32.fromBase58(clean.replace(/\s+/g, ''), network);
}

// --- GESTIÓN DE DIRECCIÓN FIJA ---
function obtenerOGuardarDireccionFija(rl) {
  return new Promise((resolve) => {
    if (fs.existsSync(CONFIG_PATH)) {
      try {
        const config = JSON.parse(fs.readFileSync(CONFIG_PATH, 'utf8'));
        if (config.destAddress) {
          return resolve(config.destAddress);
        }
      } catch (e) {}
    }

    rl.question('\n⚙️ Ingresa tu dirección de destino BTC fija: ', (direccion) => {
      const destAddress = direccion.trim();
      fs.writeFileSync(CONFIG_PATH, JSON.stringify({ destAddress }, null, 2));
      console.log('✅ Dirección guardada permanentemente.\n');
      resolve(destAddress);
    });
  });
}

// --- ESCANEO CON CONTADOR DINÁMICO ESTILO CAPTURA ---
async function escaneoConEstadisticas(clientKeyStr, recoveryKeyStr, destAddress, userFeeRate = 1, networkType = 'mainnet', gapLimit = 20) {
  const network = networkType === 'mainnet' ? bitcoin.networks.bitcoin : bitcoin.networks.testnet;
  const baseUrl = networkType === 'mainnet' ? 'https://mempool.space/api' : 'https://mempool.space/testnet/api';

  const clientRoot = parseKey(clientKeyStr, network);
  const recoveryRoot = parseKey(recoveryKeyStr, network);

  const foundUtxos = [];
  let totalBalance = 0;
  let totalAddressesChecked = 0;

  for (const change of [0, 1]) {
    let unusedCount = 0;
    let index = 0;

    while (unusedCount < gapLimit) {
      totalAddressesChecked++;
      
      // Actualizar contador en pantalla en la misma línea (estilo dinámico)
      process.stdout.write(`\r  i  ${totalAddressesChecked} addresses | ${foundUtxos.length} UTXOs | ${totalBalance} sats`);

      const clientChild = clientRoot.derivePath(`m/48'/0'/0'/${change}/${index}`);
      const recoveryChild = recoveryRoot.derivePath(`m/48'/0'/0'/${change}/${index}`);

      const pubkeys = [clientChild.publicKey, recoveryChild.publicKey].sort(Buffer.compare);
      const p2ms = bitcoin.payments.p2ms({ m: 2, pubkeys, network });
      const p2wsh = bitcoin.payments.p2wsh({ redeem: p2ms, network });

      const address = p2wsh.address;

      try {
        const { data: utxos } = await axios.get(`${baseUrl}/address/${address}/utxo`);
        if (utxos && utxos.length > 0) {
          unusedCount = 0;
          for (const u of utxos) {
            foundUtxos.push({
              txid: u.txid,
              vout: u.vout,
              value: u.value,
              witnessScript: p2ms.output,
              clientChild,
              recoveryChild,
              address
            });
            totalBalance += u.value;
          }
        } else {
          unusedCount++;
        }
      } catch (e) {
        unusedCount++;
      }
      index++;
    }
  }

  console.log(`\n\n  i  Escaneo finalizado: ${totalAddressesChecked} direcciones revisadas.`);

  if (foundUtxos.length === 0) {
    console.log('  ❌ No se encontraron fondos (0 UTXOs). Verifica tus claves o código de emergencia.');
    return;
  }

  console.log(`\n💰 ¡Fondos encontrados! Balance total: ${totalBalance} SATs`);
  console.log('⚡ Construyendo y firmando transacción...');

  const psbt = new bitcoin.Psbt({ network });
  for (const utxo of foundUtxos) {
    psbt.addInput({
      hash: utxo.txid,
      index: utxo.vout,
      witnessScript: utxo.witnessScript,
      witnessUtxo: {
        script: bitcoin.address.toOutputScript(utxo.address, network),
        value: utxo.value
      }
    });
  }

  const BASE_MIN_FEE = 90; // Mínimo estricto de 90 SATs
  const estimatedVBytes = foundUtxos.length * 140 + 2 * 34 + 10;
  const fee = Math.max(BASE_MIN_FEE, estimatedVBytes * userFeeRate);
  const sendAmount = totalBalance - fee;

  if (sendAmount <= 0) {
    console.log(`❌ El balance (${totalBalance} SATs) es insuficiente para cubrir la comisión mínima (${fee} SATs).`);
    return;
  }

  psbt.addOutput({ address: destAddress, value: sendAmount });

  foundUtxos.forEach((utxo, i) => {
    psbt.signInput(i, utxo.clientChild);
    psbt.signInput(i, utxo.recoveryChild);
  });

  psbt.finalizeAllInputs();
  const txHex = psbt.extractTransaction().toHex();

  console.log('🚀 Transmitiendo transacción a la red...');
  const { data: txid } = await axios.post(`${baseUrl}/tx`, txHex);

  console.log('\n================================================');
  console.log('✅ ¡RECUPERACIÓN COMPLETADA CON ÉXITO!');
  console.log('================================================');
  console.log(`📥 Recibido en destino: ${sendAmount} SATs`);
  console.log(`💸 Comisión pagada:    ${fee} SATs`);
  console.log(`🔗 TXID: ${txid}\n`);
}

// --- INTERFAZ DE USUARIO ---
const rl = readline.createInterface({ input: process.stdin, output: process.stdout });
const question = (query) => new RegExp(query) && new Promise((resolve) => rl.question(query, resolve));

(async () => {
  console.clear();
  console.log('┌──────────────────────────────────────────────┐');
  console.log('│                 MUUN WALLET                          │');
  console.log('│                 RECOVERY               Yerandys      │');
  console.log('│                 v49 . 6 .8                           │');
  console.log('└──────────────────────────────────────────────┘');

  try {
    const destAddress = await obtenerOGuardarDireccionFija(rl);
    console.log(`► [ADDR] → ${destAddress.substring(0, 8)}...${destAddress.slice(-6)}`);

    const clientKey = await question('\n► [CODE] Kit / Semilla Cliente: ');
    const recoveryKey = await question('► [RECO] Kit de Emergencia:     ');
    
    rl.close();

    await escaneoConEstadisticas(clientKey, recoveryKey, destAddress, 1, 'mainnet');

  } catch (err) {
    console.error(`\n❌ Error crítico: ${err.message}\n`);
    rl.close();
  }
})();
