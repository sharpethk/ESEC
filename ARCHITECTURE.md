# ESEC Exam Prep — Architecture Blueprint

Fully offline Android exam training app. No internet permission. No cloud dependency.

---

## Tech Stack

| Layer            | Technology                                      |
|------------------|-------------------------------------------------|
| Language         | Kotlin 2.0                                      |
| UI               | Jetpack Compose + Material Design 3             |
| Architecture     | Clean Architecture + MVI (StateFlow)            |
| DI               | Hilt 2.52                                       |
| Database         | Room 2.6 (SQLite)                               |
| Preferences      | DataStore Preferences                           |
| Serialization    | Gson                                            |
| Async            | Kotlin Coroutines + Flow                        |
| Encryption       | AES-256-GCM (Android Keystore + PBKDF2)         |
| Navigation       | Navigation Compose                              |
| Fonts            | Poppins (bundled in assets)                     |
| Min SDK          | 26 (Android 8.0)                                |
| Target SDK       | 35 (Android 15)                                 |

---

## Folder Structure

```
app/src/main/
├── kotlin/com/esec/examprep/
│   ├── ESECApplication.kt                  ← Hilt entry point
│   ├── data/
│   │   ├── crypto/
│   │   │   ├── AesEncryption.kt            ← AES-256-GCM primitives
│   │   │   ├── AssetKeyDerivation.kt       ← PBKDF2 key from compile-time passphrase
│   │   │   └── QuestionBankDecryptor.kt    ← Decrypts the bundled .enc asset
│   │   ├── json/
│   │   │   └── QuestionBankDto.kt          ← Gson DTOs matching JSON schema
│   │   ├── local/
│   │   │   ├── db/AppDatabase.kt           ← Room database (3 entities)
│   │   │   ├── dao/SubjectDao.kt
│   │   │   ├── dao/QuestionDao.kt
│   │   │   └── dao/ExamResultDao.kt
│   │   ├── mapper/
│   │   │   └── EntityMapper.kt             ← DTO↔Entity↔Domain conversions
│   │   └── repository/
│   │       ├── QuestionRepositoryImpl.kt
│   │       └── ExamSessionRepositoryImpl.kt
│   ├── di/
│   │   ├── AppModule.kt                    ← Gson singleton
│   │   ├── DatabaseModule.kt               ← Room + DAOs
│   │   └── RepositoryModule.kt             ← Interface → Impl bindings
│   ├── domain/
│   │   ├── model/                          ← Pure Kotlin data classes (no Android deps)
│   │   │   ├── Subject.kt
│   │   │   ├── Question.kt  Option.kt
│   │   │   ├── ExamSession.kt  ExamResult.kt
│   │   │   └── UserProgress.kt
│   │   ├── repository/                     ← Interfaces only
│   │   │   ├── QuestionRepository.kt
│   │   │   └── ExamSessionRepository.kt
│   │   └── usecase/
│   │       ├── EnsureDataLoadedUseCase.kt  ← Decrypt + seed DB on first launch
│   │       ├── GetSubjectsUseCase.kt
│   │       ├── GetQuestionsForExamUseCase.kt
│   │       ├── SubmitExamUseCase.kt
│   │       ├── CalculateScoreUseCase.kt
│   │       └── GetProgressUseCase.kt
│   └── presentation/
│       ├── MainActivity.kt
│       ├── theme/
│       │   ├── Color.kt    ← Brand palette + semantic colors
│       │   ├── Type.kt     ← Poppins typography scale
│       │   └── Theme.kt    ← Material3 light/dark + dynamic color
│       ├── navigation/
│       │   ├── Screen.kt   ← Type-safe route definitions
│       │   └── AppNavGraph.kt ← Slide + fade transitions
│       ├── components/
│       │   ├── QuestionCard.kt  ← Question + options, reveal mode
│       │   ├── OptionItem.kt    ← Animated correct/wrong color states
│       │   ├── TimerBar.kt      ← Animated countdown + color shift
│       │   └── ScoreRing.kt     ← Animated arc canvas
│       ├── home/         HomeScreen + HomeViewModel
│       ├── subject/      SubjectScreen + SubjectViewModel
│       ├── exam/         ExamScreen + ExamViewModel + ExamState
│       ├── result/       ResultScreen + ResultViewModel
│       └── dashboard/    DashboardScreen + DashboardViewModel
└── assets/
    └── questions_bank.enc   ← AES-256-GCM encrypted JSON (build-time artifact)
```

---

## Encrypted Question Bank Pipeline

```
BUILD TIME
──────────
  questions_bank.json
        │
        ▼  (offline Python/Kotlin tool in /tools/)
  encrypt_bank.py  ←  passphrase (env var, never committed)
        │  PBKDF2-SHA256(passphrase, salt, 100k iter) → AES-256-GCM key
        │  encrypt(json_bytes) → [IV(12)] + [ciphertext + GCM tag(16)]
        ▼
  questions_bank.enc  →  bundled into app/src/main/assets/

RUNTIME (first launch)
──────────────────────
  QuestionBankDecryptor.decryptToJson()
        │  open asset → read bytes (never writes plaintext to disk)
        │  AssetKeyDerivation.derive(passphrase) → SecretKey (in-memory only)
        │  AesEncryption.decrypt(bytes, key) → UTF-8 string
        ▼
  Gson.fromJson → QuestionBankDto
        │
        ▼
  Room DB (SubjectEntity, QuestionEntity)
        │
        ▼  (all subsequent launches read directly from Room — no re-decryption)
  QuestionRepository → Domain layer
```

---

## Screen Flow

```
Splash ──► Home
              ├──► Subject Selection ──► Exam (TIMED or PRACTICE)
              │                              └──► Result Review ──► Home / Retry
              └──► Progress Dashboard
```

---

## State Management (MVI)

Each screen owns a single immutable `State` data class.
ViewModels expose `StateFlow<State>` — never mutable state directly.

```
UI Event (click / timer tick)
        │
        ▼
ViewModel.someIntent()
        │  updates via _state.update { it.copy(...) }
        ▼
StateFlow<ExamState>
        │
        ▼
Composable (collectAsState) → re-compose only changed nodes
```

**Session persistence across process death:**
- `SavedStateHandle` stores `subjectId` and `mode` (primitive types only)
- Answers in-flight are stored in `ViewModel._state` — if process dies mid-exam the session is abandoned (acceptable UX; timed exams are short)
- Completed results are immediately persisted to Room inside `SubmitExamUseCase`

---

## Performance Optimizations

| Concern                  | Solution                                                        |
|--------------------------|-----------------------------------------------------------------|
| Question loading         | `QuestionDao.getRandomBySubject` — SQLite RANDOM() LIMIT, no full scan |
| Large question list      | `LazyColumn` with `key = { it.id }` — stable identity, minimal recomposition |
| Decryption cost          | Done once on first launch on `Dispatchers.IO`; result cached in Room |
| Timer                    | Single coroutine with `delay(1_000)` — no `Handler` or `AlarmManager` |
| Image assets             | Vector drawables only — no bitmaps in question bank             |
| Recomposition            | `animateColorAsState` scoped to leaf composables; `@Stable` on domain models |
| Cold start               | SplashScreen API — data loading starts in `HomeViewModel.init` behind splash |

---

## Testing Strategy

### Unit Tests (`src/test/`)
| File                          | What it covers                                     |
|-------------------------------|----------------------------------------------------|
| `AesEncryptionTest`           | Encrypt/decrypt roundtrip, IV randomness, wrong key |
| `CalculateScoreUseCaseTest`   | 100%, 0%, partial, skipped question scoring        |
| `ExamViewModelTest`           | Answer selection, navigation, timer expiry          |

### Instrumented Tests (`src/androidTest/`)
- Room DAO tests with in-memory database
- End-to-end Compose UI test: subject → exam → result navigation

### Running
```bash
./gradlew test                    # unit tests
./gradlew connectedAndroidTest    # on-device / emulator tests
```

---

## Preparing the Encrypted Asset (build tool)

Place `tools/encrypt_bank.py` alongside your plaintext JSON:

```python
# tools/encrypt_bank.py
import os, json, struct
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.ciphers.aead import AESGCM

SALT = bytes([0x45,0x53,0x45,0x43,0x51,0x42,0x41,0x4E,
              0x4B,0x32,0x30,0x32,0x35,0x21,0x40,0x23])
PASSPHRASE = os.environ["ESEC_PASSPHRASE"].encode()

kdf = PBKDF2HMAC(algorithm=hashes.SHA256(), length=32, salt=SALT, iterations=100_000)
key = kdf.derive(PASSPHRASE)

with open("questions_bank.json", "rb") as f:
    plaintext = f.read()

iv  = os.urandom(12)
ct  = AESGCM(key).encrypt(iv, plaintext, None)   # includes GCM tag
out = iv + ct

with open("../app/src/main/assets/questions_bank.enc", "wb") as f:
    f.write(out)

print(f"Encrypted {len(plaintext)} bytes → {len(out)} bytes")
```

Run before each build:
```bash
ESEC_PASSPHRASE="ESEC_EXAM_PASSPHRASE_2025" python tools/encrypt_bank.py
```

---

## questions_bank.json Schema

```json
{
  "subjects": [
    {
      "id": "math",
      "name": "Mathematics",
      "description": "Algebra, geometry, calculus",
      "category": "Core"
    }
  ],
  "questions": [
    {
      "id": "math_2023_001",
      "subject_id": "math",
      "year": 2023,
      "text": "What is the derivative of x²?",
      "options": [
        { "id": "a", "text": "x" },
        { "id": "b", "text": "2x" },
        { "id": "c", "text": "2" },
        { "id": "d", "text": "x²" }
      ],
      "correct_option_id": "b",
      "explanation": "Power rule: d/dx(xⁿ) = n·xⁿ⁻¹",
      "difficulty": "EASY"
    }
  ]
}
```

---

## Security Checklist

- [x] No `INTERNET` permission in manifest
- [x] Plaintext JSON never written to disk — decrypted in-memory only
- [x] Passphrase split across two string constants + ProGuard string encryption
- [x] AES-256-GCM with random IV per encryption — no IV reuse
- [x] GCM authentication tag detects tampered ciphertext
- [x] `android:allowBackup="false"` — no cloud backup of DB
- [x] `data_extraction_rules.xml` excludes all domains
- [x] Room DB stored in app private storage (inaccessible without root)
