"""Encrypt questions_bank.json into questions_bank.enc for the ESEC app.

Output format (matches AesEncryption.decrypt in app):
    [IV(12 bytes)] || [ciphertext + GCM tag (16 bytes)]

Key derivation must match AssetKeyDerivation.kt:
    PBKDF2-HMAC-SHA256, 100_000 iterations, 256-bit key, fixed 16-byte salt.

Passphrase must match QuestionBankDecryptor.kt (part1 + part2).

Usage:
    python tools/encrypt_bank.py
    python tools/encrypt_bank.py --in questions_bank.json --out app/src/main/assets/questions_bank.enc
    python tools/encrypt_bank.py --validate-only
"""
from __future__ import annotations

import argparse
import json
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

VALID_DIFFICULTIES = {"EASY", "MEDIUM", "HARD"}


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


def validate(bank: dict) -> tuple[list[str], list[str]]:
    """Return (errors, warnings). Errors block encryption; warnings are informational.

    The split mirrors runtime behavior in QuestionRepositoryImpl + EntityMapper:
      - blank correct_option_id  -> question silently dropped at seed time (warning)
      - blank/unknown difficulty -> defaults to MEDIUM at runtime (warning)
      - missing subject FK / duplicate ids / malformed options -> hard error
    """
    errors: list[str] = []
    warnings: list[str] = []

    subjects = bank.get("subjects")
    questions = bank.get("questions")
    if not isinstance(subjects, list):
        errors.append("top-level 'subjects' must be a list")
        return errors, warnings
    if not isinstance(questions, list):
        errors.append("top-level 'questions' must be a list")
        return errors, warnings

    subject_ids: set[str] = set()
    for i, s in enumerate(subjects):
        sid = s.get("id")
        if not isinstance(sid, str) or not sid.strip():
            errors.append(f"subjects[{i}]: missing or blank 'id'")
            continue
        if sid in subject_ids:
            errors.append(f"subjects[{i}]: duplicate id '{sid}'")
        subject_ids.add(sid)
        for field in ("name", "description", "category"):
            if not isinstance(s.get(field), str) or not s[field].strip():
                errors.append(f"subjects[{i}] ({sid}): missing or blank '{field}'")

    seen_qids: set[str] = set()
    per_subject_total: dict[str, int] = {sid: 0 for sid in subject_ids}
    per_subject_valid: dict[str, int] = {sid: 0 for sid in subject_ids}
    blank_correct = 0
    blank_difficulty = 0
    too_few_options = 0
    blank_option_text = 0

    for i, q in enumerate(questions):
        qid = q.get("id")
        loc = f"questions[{i}]" + (f" ({qid})" if isinstance(qid, str) else "")
        if not isinstance(qid, str) or not qid.strip():
            errors.append(f"{loc}: missing or blank 'id'")
            continue
        if qid in seen_qids:
            errors.append(f"{loc}: duplicate id")
        seen_qids.add(qid)

        sid = q.get("subject_id")
        if not isinstance(sid, str) or sid not in subject_ids:
            errors.append(f"{loc}: subject_id '{sid}' not in subjects[]")
            sid = None
        else:
            per_subject_total[sid] += 1

        if not isinstance(q.get("text"), str) or not q["text"].strip():
            errors.append(f"{loc}: missing or blank 'text'")

        year = q.get("year")
        if not isinstance(year, int) or year < 0:
            errors.append(f"{loc}: 'year' must be a non-negative integer (got {year!r})")

        difficulty = q.get("difficulty")
        if difficulty in (None, "") or (isinstance(difficulty, str) and not difficulty.strip()):
            blank_difficulty += 1
        elif difficulty not in VALID_DIFFICULTIES:
            errors.append(
                f"{loc}: 'difficulty' must be one of {sorted(VALID_DIFFICULTIES)} or blank (got {difficulty!r})"
            )

        options = q.get("options")
        question_drop = False
        if not isinstance(options, list):
            errors.append(f"{loc}: 'options' must be a list")
            continue
        if len(options) < 2:
            too_few_options += 1
            question_drop = True

        opt_ids: set[str] = set()
        for j, opt in enumerate(options):
            oid = opt.get("id") if isinstance(opt, dict) else None
            if not isinstance(oid, str) or not oid.strip():
                errors.append(f"{loc}.options[{j}]: missing or blank 'id'")
                continue
            if oid in opt_ids:
                errors.append(f"{loc}.options[{j}]: duplicate option id '{oid}'")
            opt_ids.add(oid)
            if not isinstance(opt.get("text"), str) or not opt["text"].strip():
                blank_option_text += 1
                question_drop = True

        correct = q.get("correct_option_id")
        if not isinstance(correct, str) or not correct.strip():
            blank_correct += 1
            question_drop = True
        elif correct not in opt_ids:
            errors.append(
                f"{loc}: 'correct_option_id' '{correct}' does not match any option id ({sorted(opt_ids)})"
            )
        elif sid is not None and not question_drop:
            per_subject_valid[sid] += 1

    if blank_correct:
        warnings.append(
            f"{blank_correct} question(s) have a blank 'correct_option_id' "
            f"and will be skipped at seed time"
        )
    if blank_difficulty:
        warnings.append(
            f"{blank_difficulty} question(s) have a blank 'difficulty' "
            f"and will default to MEDIUM at runtime"
        )
    if too_few_options:
        warnings.append(
            f"{too_few_options} question(s) have fewer than 2 options "
            f"and will be skipped at seed time"
        )
    if blank_option_text:
        warnings.append(
            f"{blank_option_text} option(s) have blank text; "
            f"their parent question(s) will be skipped at seed time"
        )
    for sid in subject_ids:
        if per_subject_valid[sid] == 0:
            warnings.append(
                f"subject '{sid}' has no usable questions ({per_subject_total[sid]} total, "
                f"all dropped) and will be hidden by SubjectDao"
            )

    return errors, warnings


def main() -> int:
    repo_root = Path(__file__).resolve().parent.parent
    parser = argparse.ArgumentParser()
    parser.add_argument("--in", dest="src", default=str(repo_root / "questions_bank.json"))
    parser.add_argument("--out", dest="dst",
                        default=str(repo_root / "app" / "src" / "main" / "assets" / "questions_bank.enc"))
    parser.add_argument("--validate-only", action="store_true",
                        help="Validate the JSON schema and exit without writing the encrypted file.")
    parser.add_argument("--no-validate", action="store_true",
                        help="Skip validation (not recommended).")
    args = parser.parse_args()

    src = Path(args.src)
    dst = Path(args.dst)
    if not src.is_file():
        print(f"ERROR: input not found: {src}", file=sys.stderr)
        return 1

    plaintext = src.read_bytes()

    if not args.no_validate:
        try:
            bank = json.loads(plaintext)
        except json.JSONDecodeError as e:
            print(f"ERROR: {src} is not valid JSON: {e}", file=sys.stderr)
            return 2
        errors, warnings = validate(bank)
        for w in warnings:
            print(f"WARN: {w}", file=sys.stderr)
        if errors:
            print(f"VALIDATION FAILED ({len(errors)} error(s)):", file=sys.stderr)
            for p in errors:
                print(f"  - {p}", file=sys.stderr)
            return 3
        n_subjects = len(bank["subjects"])
        n_questions = len(bank["questions"])
        print(f"OK validate: {n_subjects} subject(s), {n_questions} question(s), {len(warnings)} warning(s)")

    if args.validate_only:
        return 0

    blob = encrypt(plaintext)
    dst.parent.mkdir(parents=True, exist_ok=True)
    dst.write_bytes(blob)
    print(f"OK encrypt: {src} ({len(plaintext)} bytes) -> {dst} ({len(blob)} bytes)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
