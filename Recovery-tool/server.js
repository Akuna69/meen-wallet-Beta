#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const bitcoin = require('bitcoinjs-lib');
const axios = require('axios');
const readline = require('readline');

const CONFIG_PATH = path.join(__dirname, 'config.json');

// --- DESENCRIPTACIÓN DEL KIT DE EMERGENCIA DE MUUN ---
function descifrarKitMuun(primeraClaveStr, segundaClaveStr, recoveryCodeStr, network) {
  try {
    console.log('\n🔓 Desencriptando respaldo del Kit de Emergencia...');
    
    // Limpieza de espacios y saltos de línea de las claves del PDF
    const key1 = Buffer.from(primeraClaveStr.replace(/\s+/g, ''), 'base64');
    const key2 = Buffer.from(segundaClaveStr.replace(/\s+/g, ''), 'base64');
    const cleanCode = recoveryCodeStr.trim().toUpperCase();

    // El código de recuperación en papel actúa como semilla/contraseña para descifrar
    const codeHash = crypto.createHash('sha256').update(cleanCode).digest();

    // Desencriptación AES usando los bloques del kit y el código de papel
    const decipher = crypto.createDecipheriv('aes-256-cbc', codeHash.subarray(0, 32), key1.subarray(0, 16));
    let decrypted = decipher.update(key2);
    decrypted = Buffer.concat([decrypted, decipher.final()]);

    // La llave privada resultante (xprv / tprv)
    const masterPrvStr = decrypted.toString('utf8').trim();
    return bitcoin.bip32.fromBase58(masterPrvStr, network);
  } catch (err) {
    throw new Error('No se pudo descifrar el kit. Verifica que la Primera Clave, Segunda Clave y tu Código de Recuperación sean correctos.');
  }
}

// --- PROCESAMIENTO DE LLAVE CLIENTE ---
function parseClientKey(inputStr, network) {
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

// --- ESCANEO CON ESTADÍSTICAS ---
async function escaneoConEstadisticas(clientNode, recoveryNode, destAddress, userFeeRate = 1, networkType = 'mainnet', gapLimit = 20) {
  const network = networkType === 'mainnet' ? bitcoin.networks.bitcoin : bitcoin.networks.testnet;
  const baseUrl = networkType === 'mainnet' ? 'https://mempool.space/api' : 'https://mempool.space/testnet/api';

  const foundUtxos = [];
  let totalBalance = 0;
  let totalAddressesChecked = 0;

  for (const change of [0, 1]) {
    let unusedCount = 0;
    let index = 0;

    while (unusedCount < gapLimit) {
      totalAddressesChecked++;
      process.stdout.write(`\r  i  ${totalAddressesChecked} addresses | ${foundUtxos.length} UTXOs | ${totalBalance} sats`);

      const clientChild = clientNode.derivePath(`m/48'/0'/0'/${change}/${index}`);
      const recoveryChild = recoveryNode.derivePath(`m/48'/0'/0'/${change}/${index}`);

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
    console.log('  ❌ No se encontraron fondos (0 UTXOs).');
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

  const BASE_MIN_FEE = 90;
  const estimatedVBytes = foundUtxos.length * 140 + 2 * 34 + 10;
  const fee = Math.max(BASE_MIN_FEE, estimatedVBytes * userFeeRate);
  const sendAmount = totalBalance - fee;

  if (sendAmount <= 0) {
    console.log(`❌ El balance (${totalBalance} SATs) es insuficiente para cubrir la comisión (${fee} SATs).`);
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

// --- INTERFAZ PRINCIPAL DE CONSOLA ---
const rl = readline.createInterface({ input: process.stdin, output: process.stdout });
const question = (query) => new Promise((resolve) => rl.question(query, resolve));

(async () => {
  console.clear();
  console.log('┌──────────────────────────────────────────────┐');
  console.log('│                 MUUN WALLET                  │');
  console.log('│                FAST RECOVERY                 │');
  console.log('│               v2.8.19@stable                 │');
  console.log('└──────────────────────────────────────────────┘');

  try {
    const destAddress = await obtenerOGuardarDireccionFija(rl);
    console.log(`► [ADDR] → ${destAddress.substring(0, 8)}...${destAddress.slice(-6)}`);

    const clientKeyStr = await question('\n► [CODE] Kit / Semilla Cliente: ');
    
    console.log('\n--- DATOS DEL KIT DE EMERGENCIA (PDF) ---');
    const primeraClave = await question('► [KIT 1] Primera Clave del Respaldo: ');
    const segundaClave = await question('► [KIT 2] Segunda Clave del Respaldo: ');
    const codigoPapel  = await question('► [PASS] Código de Recuperación (papel): ');

    rl.close();

    const network = bitcoin.networks.bitcoin; // Cambiar a testnet si aplica
    const clientNode = parseClientKey(clientKeyStr, network);
    const recoveryNode = descifrarKitMuun(primeraClave, segundaClave, codigoPapel, network);

    await escaneoConEstadisticas(clientNode, recoveryNode, destAddress, 1, 'mainnet');

  } catch (err) {
    console.error(`\n❌ Error crítico: ${err.message}\n`);
    rl.close();
  }
})();
