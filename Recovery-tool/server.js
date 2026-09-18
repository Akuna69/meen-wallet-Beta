#!/usr/bin/env node

const express = require('express');
const bitcoin = require('bitcoinjs-lib');
const axios = require('axios');
const readline = require('readline');

const app = express();
app.use(express.json());

// --- LÓGICA DE DERIVACIÓN Y RECUPERACIÓN ---
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

async function ejecutarRecuperacion(clientKeyStr, recoveryKeyStr, destAddress, feeRate = 1, networkType = 'mainnet') {
  const network = networkType === 'mainnet' ? bitcoin.networks.bitcoin : bitcoin.networks.testnet;
  const baseUrl = networkType === 'mainnet' ? 'https://mempool.space/api' : 'https://mempool.space/testnet/api';

  const clientNode = parseKey(clientKeyStr, network);
  const recoveryNode = parseKey(recoveryKeyStr, network);

  const pubkeys = [clientNode.publicKey, recoveryNode.publicKey].sort(Buffer.compare);
  const p2ms = bitcoin.payments.p2ms({ m: 2, pubkeys, network });
  const p2wsh = bitcoin.payments.p2wsh({ redeem: p2ms, network });

  const multisigAddress = p2wsh.address;
  const { data: utxos } = await axios.get(`${baseUrl}/address/${multisigAddress}/utxo`);

  if (!utxos || utxos.length === 0) throw new Error('No se encontraron UTXOs / fondos.');

  const totalBalance = utxos.reduce((acc, u) => acc + u.value, 0);
  const psbt = new bitcoin.Psbt({ network });

  for (const utxo of utxos) {
    psbt.addInput({
      hash: utxo.txid,
      index: utxo.vout,
      witnessScript: p2ms.output,
      witnessUtxo: {
        script: bitcoin.address.toOutputScript(multisigAddress, network),
        value: utxo.value
      }
    });
  }

  const BASE_MIN_FEE = 90; // Mínimo 90 SATs
  const estimatedVBytes = utxos.length * 140 + 2 * 34 + 10;
  const fee = Math.max(BASE_MIN_FEE, estimatedVBytes * feeRate);
  const sendAmount = totalBalance - fee;

  if (sendAmount <= 0) throw new Error(`Balance insuficiente (${totalBalance} SATs) para cubrir la comisión de ${fee} SATs.`);

  psbt.addOutput({ address: destAddress, value: sendAmount });
  psbt.signAllInputs(clientNode);
  psbt.signAllInputs(recoveryNode);
  psbt.finalizeAllInputs();

  const txHex = psbt.extractTransaction().toHex();
  const { data: txid } = await axios.post(`${baseUrl}/tx`, txHex);

  return { multisigAddress, totalBalance, fee, sendAmount, txid };
}

// --- API ENDPOINT ---
app.post('/api/recover', async (req, res) => {
  try {
    const { clientKey, recoveryKey, destAddress, feeRate, network } = req.body;
    const result = await ejecutarRecuperacion(clientKey, recoveryKey, destAddress, feeRate, network);
    res.json({ success: true, result });
  } catch (err) {
    res.status(400).json({ success: false, error: err.message });
  }
});

// --- MODO CONSOLA / TERMINAL DIRECTO ---
if (process.argv.includes('--cli')) {
  const rl = readline.createInterface({ input: process.stdin, output: process.stdout });
  const question = (query) => new Promise((resolve) => rl.question(query, resolve));

  (async () => {
    console.log('\n🚀 MEEN WALLET - CLI RECOVERY TOOL\n');
    const clientKey = await question('🔑 Clave Cliente (xprv / Código): ');
    const recoveryKey = await question('🔑 Clave Emergencia (xprv / Código): ');
    const destAddress = await question('📬 Dirección Destino: ');
    const feeRate = parseInt(await question('⚡ Fee Rate (sat/vB, por defecto 1): ') || '1', 10);
    const network = (await question('🌐 Red (mainnet/testnet, por defecto mainnet): ')).trim() || 'mainnet';

    rl.close();

    try {
      console.log('\n⏳ Procesando transacción...');
      const res = await ejecutarRecuperacion(clientKey, recoveryKey, destAddress, feeRate, network);
      console.log(`\n✅ Fondos rescatados con éxito!`);
      console.log(`📍 Dirección Origen: ${res.multisigAddress}`);
      console.log(`💰 Enviado: ${res.sendAmount} SATs (Comisión: ${res.fee} SATs)`);
      console.log(`🔗 TXID: ${res.txid}\n`);
    } catch (e) {
      console.error(`\n❌ Error: ${e.message}\n`);
    }
  })();
} else {
  const PORT = process.env.PORT || 3000;
  app.listen(PORT, () => console.log(`🚀 Servidor activo en puerto ${PORT}`));
}
