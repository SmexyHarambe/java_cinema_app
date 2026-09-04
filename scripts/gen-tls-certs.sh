#!/bin/bash

# ==============================================================================
# Generate sertifikat self-signed untuk TLS TCP server (via keytool JDK).
# Pakai :  ./scripts/gen-tls-certs.sh [output-dir]   (default: ./tls)
# Hasil :
#   tls/tcp-server.p12     -> private key + sertifikat (MESIN TCP SERVER,
#                               env TCP_TLS_KEYSTORE_PATH/PASSWORD)
#   tls/tcp-truststore.p12 -> sertifikat server saja   (MESIN BACKEND,
#                               env TCP_TLS_TRUSTSTORE_PATH/PASSWORD)
# JANGAN commit file .p12 (sudah di-gitignore). Distribusikan manual
# (scp / copy) ke masing-masing mesin. Self-signed cukup untuk lab;
# bukan untuk production.
# ==============================================================================

set -e

OUT_DIR="${1:-./tls}"
# Password keystore/truststore (samakan di env kedua mesin):
STOREPASS="${STOREPASS:-changeit}"
# Sesuaikan IP/DNS bila IP lab berubah:
SAN="DNS:localhost,IP:127.0.0.1,IP:192.168.74.1,IP:192.168.74.157"

mkdir -p "$OUT_DIR"
SERVER_P12="$OUT_DIR/tcp-server.p12"
TRUST_P12="$OUT_DIR/tcp-truststore.p12"
CER="$OUT_DIR/tcp-server.cer"

keytool -genkeypair -alias tcpserver -keyalg RSA -keysize 2048 -validity 3650 \
  -storetype PKCS12 -keystore "$SERVER_P12" -storepass "$STOREPASS" \
  -dname "CN=uasdisprog-tcp, OU=Disprog, O=UAS, C=ID" \
  -ext "SAN=$SAN"

keytool -exportcert -alias tcpserver \
  -keystore "$SERVER_P12" -storepass "$STOREPASS" -file "$CER"

keytool -importcert -alias tcpserver -file "$CER" \
  -keystore "$TRUST_P12" -storepass "$STOREPASS" -noprompt -storetype PKCS12

rm -f "$CER"

echo "OK:"
echo "  server keystore : $SERVER_P12"
echo "  truststore      : $TRUST_P12"
echo "  password        : $STOREPASS"
