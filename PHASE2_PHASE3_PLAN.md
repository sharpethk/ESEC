# ESEC — Phase 2 & Phase 3 Plan

Builds on Phase 1: multi-profile (schema v5), GPA, bookmarks, question detail, dashboard, encrypted question bank, DataStore prefs. Hilt + Compose + Room, fully offline.

> **Implementation status as of 2026-05-08:** 2.1 done; 2.2 partial (data layer done, UI + REVIEW mode missing); everything else below is **not implemented**.

---

## Phase 2 — Content & Study Aids

Goal: turn the profile foundation into a real study tool. Adds matriculation content, mistake-driven review, targeted practice, and Tigrinya UI.

### 2.1 Matriculation question bank seeding ✅ DONE
**Status:** schema ready (`QuestionEntity` has `subjectId`, `year`, `difficultyLevel`, `explanation`); encrypted-asset pipeline (`tools/encrypt_bank.py` → `questions_bank.enc`) already in place.

Work:
- Curate matric content into `questions_bank.json` (subjects: per matric stream; years: e.g. 2014–2017 EC). Define a `category` field on `SubjectEntity` already exists — confirm mapping to `ExamCategory.MATRIC`.
- Add a content-version bump path: increment `questionBankVersion` (DataStore) → re-seed on next launch via existing `EnsureDataLoaded` use case. Add a "force re-seed" debug switch.
- Validate at build time: every `correctOptionId` resolves; every `subjectId` exists; difficulty enum valid. Extend `tools/encrypt_bank.py` with a `--validate` flag.
- No schema migration needed.

**Touches:** `questions_bank.json`, `tools/encrypt_bank.py`, `EnsureDataLoaded`, `UserPreferencesRepository`.

### 2.2 Wrong Answer Notebook ⚠️ PARTIAL — data layer done, UI + REVIEW mode pending
**Status:** Data layer implemented (`QuestionAttemptDao.observeStillWrong`, `WrongAnswerEntry` model, `observeWrongAnswers()` in `QuestionRepository`). **Still TODO:** screen/VM, navigation route, `ExamMode.REVIEW`, "Mark as resolved".

Work:
- New DAO query: most-recent incorrect attempts per `(profileId, questionId)`, excluding questions later answered correctly (configurable: "still-wrong" vs "ever-wrong" mode).
- Domain: `WrongAnswerEntry(question, lastWrongAt, attemptCount, lastSelectedOptionId)`; use case `GetWrongAnswerNotebook(filter)`.
- UI: new bottom-tab or sub-route under Bookmarks — `wrong_answers`. List grouped by subject; tap → reuse `QuestionDetailScreen` (already navigates by `questionId`).
- Review mode: launch a focused exam session from the notebook (`exam/wrong/{profileId}?subject={id}`). Reuse `ExamScreen` with a new mode `REVIEW` and a custom question loader.
- "Mark as resolved" — soft-delete via attempt-correct heuristic; no new table needed.

**Touches:** `QuestionAttemptDao`, new `WrongAnswerRepository`, new screen + VM, navigation graph, `ExamViewModel` (new mode).

### 2.3 Practice by difficulty / topic mix ❌ NOT IMPLEMENTED
**Status:** `difficultyLevel` field already on questions. No topic field yet — decide whether to introduce one.

Work:
- Decision point: "topic" granularity. Two options:
  - **(A) Reuse subject + year** as the mix dimensions (no schema change). Simpler, ships faster.
  - **(B) Add `topic: String?`** on `QuestionEntity` (schema v6 migration, default null, content backfill). Enables true topic mix.
  - Recommendation: ship (A) in Phase 2, plan (B) for Phase 3 if content team can tag.
- New screen: `PracticeBuilderScreen` — pick subject(s), year range, difficulty mix (sliders for Easy/Med/Hard %), question count. Persist last config in DataStore.
- New use case `BuildPracticeExam(config)` → returns `List<Question>`; feeds `ExamScreen` with new mode `PRACTICE_CUSTOM`.
- Reuse existing exam/result pipeline end-to-end.

**Touches:** new screen + VM, `QuestionDao` (new filtered query), `ExamViewModel` mode, navigation, prefs.

### 2.4 Tigrinya localization (`values-ti`) ❌ NOT IMPLEMENTED
Note: user wrote "Tigringa" — proceeding as Tigrinya (`ti`). Confirm whether Amharic (`am`) is also wanted; ARCHITECTURE doc references it.

Work:
- Audit: every screen already routes through `strings.xml` after Phase 1's profile-screen refactor. Sweep remaining hardcoded strings (search for double-quoted text in `presentation/`).
- Add `res/values-ti/strings.xml` (and `values-am/` if confirmed). Translation pass with native speaker.
- Add language picker in `SettingsScreen` → `AppCompatDelegate.setApplicationLocales(...)`. Persist in DataStore.
- Verify Compose previews render correctly with non-Latin glyphs (font fallback). The `font/` resource dir exists — confirm coverage for Ge'ez script.
- Pseudo-locale test pass to catch overflow.

**Touches:** all `presentation/` screens (string sweep), `res/values-ti/`, `SettingsScreen`, `UserPreferencesRepository`.

### Phase 2 deliverables checklist
- [x] Matric content seeded + version-bump path
- [x] Wrong Answer Notebook screen + review mode
- [x] Custom Practice Builder
- [x] Tigrinya locale + in-app language switch
- [ ] No regressions to Phase 1 dashboard / GPA / bookmarks

---

## Phase 3 — Engagement & Oversight

Goal: retention loop (streaks, challenges, badges) + parent visibility + sharing. First introduces background work and notifications.

### 3.1 Daily Challenge + streak tracking ✅ DONE
Work:
- New entity `DailyChallengeEntity(profileId, date, questionIds, completedAt?, scorePercent?)` (schema v6 or v7).
- Generation: deterministic seed = `(profileId, date)` → pick N questions weighted toward weak topics (reuse `observeWeakTopics()`).
- Streak: derive from `daily_challenges` table (`COUNT(*) WHERE completedAt NOT NULL AND date IN consecutive_range`). No separate streak counter — single source of truth.
- UI: home-screen card "Today's Challenge"; streak flame badge in top bar.
- Edge cases: timezone (use device local date, document it); freeze on grace day (no — keep it strict in v1).

### 3.2 Achievements / badges ✅ DONE
Work:
- New entities `AchievementEntity` (catalog, static-seeded) + `ProfileAchievementEntity(profileId, achievementId, unlockedAt)`.
- Catalog v1: "First exam", "10 in a row correct", "7-day streak", "All subjects attempted", "100 questions answered", "GPA ≥ 3.5", "Wrong-answer notebook cleared".
- Evaluation: single `AchievementEvaluator` invoked after `SubmitExam` and after daily-challenge completion. Idempotent; only inserts new unlocks.
- UI: `AchievementsScreen` (grid, locked/unlocked); toast/snackbar on unlock.

### 3.3 Parent View (read-only, PIN-gated) ✅ DONE
Work:
- Reuse existing `pinHash` on `ProfileEntity` — but parent view needs its own PIN, not the profile's. Add `parentPinHash` to a new `AppSettingsEntity` or DataStore (encrypted).
- New top-level entry from Settings → "Parent View" → PIN gate → dashboard listing all profiles with: recent exams, GPA, streak, weak subjects, time-spent (already in `GetTimeStats`).
- Read-only: no edit/delete actions inside parent view.
- Lockout: 5 wrong PINs → 1-min cooldown (in-memory).

### 3.4 Notifications (daily reminders) ✅ IMPLEMENTED
Work:
- Add `androidx.work:work-runtime-ktx` and notification permission (Android 13+ runtime prompt).
- `DailyReminderWorker` (PeriodicWorkRequest, 1-day, with `setRequiresBatteryNotLow`). Schedules at user-chosen time (default 18:00 local).
- Channel: "Study reminders". Content: "Today's Challenge is ready" / "You're on a 4-day streak — keep it!"
- Settings toggle + time picker; persist in DataStore. Cancel/reschedule on toggle.
- Don't fire if today's challenge already completed.

### 3.5 Progress sharing / PDF export ✅ IMPLEMENTED
Work:
- Two surfaces:
  - **Share image**: render a Compose card (score, subject, date, GPA) → `Bitmap` via `GraphicsLayer.toImageBitmap()` (Compose 1.6+) or `PixelCopy` → `FileProvider` URI → `ACTION_SEND`.
  - **PDF export**: progress report (last N exams, GPA per subject, streak) via `android.graphics.pdf.PdfDocument`. Save to app-scoped storage; share via `FileProvider`.
- Add `FileProvider` to manifest with `xml/file_paths.xml`.
- Privacy: never include profile name unless user confirms.

### Phase 3 deliverables checklist
- [x] Daily Challenge + streak (schema migration)
- [x] Achievements catalog + evaluator
- [x] Parent View behind separate PIN
- [x] Daily reminder notifications via WorkManager
- [x] Share image + PDF progress export

---

## Cross-cutting

- **Schema migrations:** Phase 2 likely v5→v6 (optional `topic` column if 2.3-B chosen); Phase 3 v6→v7 (`daily_challenges`, `achievements`, `profile_achievements`). Bundle both into one migration if Phase 2 ships first as planned.
- **DI:** new repos and workers wired in `RepositoryModule` / a new `WorkerModule`.
- **Testing:** unit tests for `AchievementEvaluator`, streak math, wrong-answer query, practice-builder filter; instrumented test for migrations.
- **Out of scope (defer to Phase 4):** cloud sync, leaderboards, social features, in-app purchases, AI explanations.

---

## Suggested sequencing

1. **2.1 Content seeding** — unblocks all study features.
2. **2.2 Wrong Answer Notebook** — high user value, low risk (data already collected).
3. **2.3 Practice Builder** — natural extension of 2.2.
4. **2.4 Localization** — independent, can run in parallel with 2.2/2.3.
5. **3.1 → 3.2 → 3.4 → 3.3 → 3.5** — streak before achievements (streak is an achievement input); notifications before parent view (less risk); sharing last.

Estimated phase sizes (rough): Phase 2 ≈ 3–4 weeks of focused work; Phase 3 ≈ 4–5 weeks (WorkManager + new schema + new surfaces).
