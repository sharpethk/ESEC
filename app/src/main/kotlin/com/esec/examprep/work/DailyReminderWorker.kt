package com.esec.examprep.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.esec.examprep.R
import com.esec.examprep.domain.repository.DailyChallengeRepository
import com.esec.examprep.domain.repository.ProfileRepository
import com.esec.examprep.presentation.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate

const val REMINDER_CHANNEL_ID = "study_reminders"
private const val REMINDER_NOTIFICATION_ID = 1001

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val profileRepository: ProfileRepository,
    private val dailyChallengeRepository: DailyChallengeRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val profile = profileRepository.getActiveProfile() ?: return Result.success()
        val today = LocalDate.now()
        val challenge = runCatching {
            dailyChallengeRepository.observeTodayChallenge(profile.id).first()
        }.getOrNull()

        if (challenge?.isCompleted == true) return Result.success()

        val streak = runCatching {
            dailyChallengeRepository.observeStreak(profile.id).first()
        }.getOrDefault(0)

        showNotification(streak)
        return Result.success()
    }

    private fun showNotification(streak: Int) {
        val ctx = applicationContext
        ensureChannel(ctx)

        val title = "Today's Challenge is ready"
        val body = if (streak > 0) "You're on a ${streak}-day streak — keep it going!"
                   else "Build your streak. Tap to start today's challenge."

        val intent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pi = PendingIntent.getActivity(
            ctx, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notif = NotificationCompat.Builder(ctx, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        runCatching {
            NotificationManagerCompat.from(ctx).notify(REMINDER_NOTIFICATION_ID, notif)
        }
    }

    private fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Study reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Daily challenge reminders"
            }
            mgr.createNotificationChannel(channel)
        }
    }
}
