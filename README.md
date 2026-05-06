# ESEC Exam Prep

Offline Android app for the Eritrean Grade 8 End-of-Basic-Education
National Examination (Social Studies). No internet permission; the
question bank is bundled as an AES-256-GCM encrypted asset.

## Build prerequisites

- JDK 17
- Android SDK with API 35
- Python 3 with `cryptography` (`pip install cryptography`) — only needed
  when the question bank changes

## First-time setup

The app reads `app/src/main/assets/questions_bank.enc` at runtime. That
file is generated from `questions_bank.json` at the repo root and is not
committed-friendly to edit by hand. Regenerate it any time the JSON
changes:

```bash
python tools/encrypt_bank.py
```

This produces `app/src/main/assets/questions_bank.enc` using the same
PBKDF2 + AES-GCM parameters baked into `AssetKeyDerivation.kt` and
`AesEncryption.kt`. Run it before `./gradlew assembleDebug` — the build
does not invoke it automatically.

## Running

```bash
./gradlew assembleDebug          # debug APK
./gradlew installDebug           # install on connected device
./gradlew test                   # JVM unit tests
./gradlew connectedAndroidTest   # instrumented tests (DAO + UI)
```

## Project layout

See `ARCHITECTURE.md` for the full layered design. Top level:

- `app/src/main/kotlin/com/esec/examprep/` — `data`, `domain`, `presentation`, `di`
- `app/src/main/assets/questions_bank.enc` — encrypted bank (generated)
- `tools/encrypt_bank.py` — encryption pipeline
- `questions_bank.json` — source of truth for questions (plaintext)

## Security notes

- No `INTERNET` permission, `allowBackup=false`, custom data extraction rules.
- Passphrase split across two `String` constants and obfuscated by R8 in release.
- Never persist the derived key; it is wiped after each decrypt call.
