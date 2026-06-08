package com.example.splitbuddy.ui.util


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Converts an ISO 8601 date string to a human-readable "time ago" format.
 * Example: "2025-04-28T10:30:00.000Z" → "2 days ago"
 */
fun String.toTimeAgo(): String {
    if (this.isBlank()) return ""

    return try {
        // Try multiple formats the API might return
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd HH:mm:ss"
        )

        var date: Date? = null
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                date = sdf.parse(this)
                if (date != null) break
            } catch (_: Exception) { }
        }

        if (date == null) return ""

        val diff    = System.currentTimeMillis() - date.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours   = minutes / 60
        val days    = hours / 24
        val weeks   = days / 7
        val months  = days / 30
        val years   = days / 365

        when {
            seconds < 60  -> "just now"
            minutes < 60  -> "${minutes}m ago"
            hours   < 24  -> "${hours}h ago"
            days    == 1L -> "yesterday"
            days    < 7   -> "$days days ago"
            weeks   == 1L -> "1 week ago"
            weeks   < 4   -> "$weeks weeks ago"
            months  == 1L -> "1 month ago"
            months  < 12  -> "$months months ago"
            years   == 1L -> "1 year ago"
            else          -> "$years years ago"
        }
    } catch (_: Exception) {
        ""
    }
}