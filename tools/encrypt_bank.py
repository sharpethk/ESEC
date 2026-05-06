"""Encrypt questions_bank.json into questions_bank.enc for the ESEC app.

Output format (matches AesEncryption.decrypt in app):
    [IV(12 bytes)] || [ciphertext + GCM tag (16 bytes)]

Key derivation must match AssetKeyDerivation.kt:
    PBKDF2-HMAC-SHA256, 100_000 iterations, 256-bit key, fixed 16-byte salt.

Passphrase must match QuestionBankDecryptor.kt (part1 + part2).

Usage:
    python tools/encrypt_bank.py
    python tools/encrypt_bank.py --in questions_bank.json --out app/src/main/assets/questions_bank.enc
"""
from __future__ import annotations

import argparse
import os
import sys
from pathlib import Path

from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC

PASSPHRASE = b"ESEC_EXAM" + b"_PASSPHRASE_2025"
SALT = bytes([0x45, 0x53, 0x45, 0x43, 0x51, 0x42, 0x41, 0x4E,
              0x4B, 0x32, 0x30, 0x32, 0x35, 0x21, 0x40, 0x23])
ITERATIONS = 100_000
KEY_LEN = 32
IV_LEN = 12


def derive_key(passphrase: bytes) -> bytes:
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=KEY_LEN,
        salt=SALT,
        iterations=ITERATIONS,
    )
    return kdf.derive(passphrase)


def encrypt(plaintext: bytes) -> bytes:
    key = derive_key(PASSPHRASE)
    iv = os.urandom(IV_LEN)
    ct = AESGCM(key).encrypt(iv, plaintext, associated_data=None)
    return iv + ct


def main() -> int:
    repo_root = Path(__file__).resolve().parent.parent
    parser = argparse.ArgumentParser()
    parser.add_argument("--in", dest="src", default=str(repo_root / "questions_bank.json"))
    parser.add_argument("--out", dest="dst",
                        default=str(repo_root / "app" / "src" / "main" / "assets" / "questions_bank.enc"))
    args = parser.parse_args()

    src = Path(args.src)
    dst = Path(args.dst)
    if not src.is_file():
        print(f"ERROR: input not found: {src}", file=sys.stderr)
        return 1

    plaintext = src.read_bytes()
    blob = encrypt(plaintext)
    dst.parent.mkdir(parents=True, exist_ok=True)
    dst.write_bytes(blob)
    print(f"OK: {src} ({len(plaintext)} bytes) -> {dst} ({len(blob)} bytes)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
