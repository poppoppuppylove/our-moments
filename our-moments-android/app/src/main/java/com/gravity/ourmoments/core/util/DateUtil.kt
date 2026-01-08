package com.gravity.ourmoments.core.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object DateUtil {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun formatToServerString(localDateTime: LocalDateTime): String {
        return localDateTime.format(formatter)
    }

    fun parseFromServerString(dateString: String): LocalDateTime {
        return LocalDateTime.parse(dateString, formatter)
    }

    fun getRelativeTime(timeString: String): String {
        return try {
            val date = parseFromServerString(timeString)
            val now = LocalDateTime.now()
            val diffMinutes = java.time.Duration.between(date, now).toMinutes()

            when {
                diffMinutes < 1 -> "刚刚"
                diffMinutes < 60 -> "${diffMinutes}分钟前"
                diffMinutes < 1440 -> "${diffMinutes / 60}小时前"
                diffMinutes < 43200 -> "${diffMinutes / 1440}天前"
                else -> date.format(DateTimeFormatter.ofPattern("MM月dd日"))
            }
        } catch (e: Exception) {
            timeString
        }
    }
}