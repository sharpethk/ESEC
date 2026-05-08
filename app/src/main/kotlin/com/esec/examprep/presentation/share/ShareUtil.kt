package com.esec.examprep.presentation.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.Grade
import java.io.File
import java.io.FileOutputStream

object ShareUtil {

    fun shareExamResult(context: Context, result: ExamResult) {
        val grade = Grade.fromPercent(result.scorePercent)
        val text = buildString {
            appendLine("📚 ESEC Exam Result")
            appendLine()
            appendLine("Subject: ${result.subjectName}")
            result.year?.let { appendLine("Past paper: $it") }
            appendLine("Score: ${"%.1f".format(result.scorePercent)}%  (Grade ${grade.letter}, GPA ${"%.2f".format(grade.gpa)})")
            appendLine("Correct: ${result.correctAnswers}  Wrong: ${result.incorrectAnswers}  Skipped: ${result.skippedAnswers}")
            appendLine("Time: ${formatDuration(result.durationSeconds)}")
            appendLine()
            appendLine(if (result.passed) "✅ Passed" else "❌ Try again")
        }
        val image = runCatching { createScoreBitmap(result, grade) }.getOrNull()
        val imageUri = image?.let { saveBitmapToCache(context, it, "esec_result_${result.sessionId}.png") }

        val send = Intent(Intent.ACTION_SEND).apply {
            type = if (imageUri != null) "image/png" else "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "ESEC Exam Result")
            if (imageUri != null) {
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
        context.startActivity(Intent.createChooser(send, "Share result").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun createScoreBitmap(result: ExamResult, grade: Grade): Bitmap {
        val w = 1080; val h = 1080
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.parseColor("#0F172A"))

        val title = Paint().apply {
            color = Color.WHITE; textSize = 64f; isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val sub = Paint(title).apply { textSize = 40f; color = Color.parseColor("#94A3B8"); typeface = Typeface.DEFAULT }
        val big = Paint(title).apply { textSize = 220f }
        val accent = Paint(title).apply {
            color = if (result.passed) Color.parseColor("#22C55E") else Color.parseColor("#EF4444")
            textSize = 56f
        }
        val small = Paint(title).apply { textSize = 36f; color = Color.parseColor("#CBD5E1"); typeface = Typeface.DEFAULT }

        val cx = w / 2f
        canvas.drawText("ESEC", cx, 140f, title)
        canvas.drawText(result.subjectName, cx, 220f, sub)
        canvas.drawText("${"%.0f".format(result.scorePercent)}%", cx, 520f, big)
        canvas.drawText("Grade ${grade.letter}  •  GPA ${"%.2f".format(grade.gpa)}", cx, 600f, sub)
        canvas.drawText(if (result.passed) "PASSED" else "TRY AGAIN", cx, 700f, accent)
        canvas.drawText(
            "✓ ${result.correctAnswers}    ✗ ${result.incorrectAnswers}    ⊘ ${result.skippedAnswers}",
            cx, 820f, small,
        )
        canvas.drawText("Time ${formatDuration(result.durationSeconds)}", cx, 880f, small)
        canvas.drawText("Eritrean Grade 8 Exam Prep", cx, 1020f, sub)
        return bmp
    }

    private fun saveBitmapToCache(context: Context, bmp: Bitmap, name: String): android.net.Uri {
        val dir = File(context.cacheDir, "shared").apply { mkdirs() }
        val file = File(dir, name)
        FileOutputStream(file).use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun formatDuration(seconds: Long): String {
        val m = seconds / 60; val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }
}
