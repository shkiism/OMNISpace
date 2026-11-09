package amirparsa.shk.omnispace.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    data class JalaliDate(val year: Int, val month: Int, val day: Int) {
        val monthNameFa: String
            get() = when (month) {
                1 -> "فروردین"
                2 -> "اردیبهشت"
                3 -> "خرداد"
                4 -> "تیر"
                5 -> "مرداد"
                6 -> "شهریور"
                7 -> "مهر"
                8 -> "آبان"
                9 -> "آذر"
                10 -> "دی"
                11 -> "بهمن"
                12 -> "اسفند"
                else -> ""
            }

        fun formatFa(): String {
            return "${NumberUtils.toPersianDigits(day)} $monthNameFa ${NumberUtils.toPersianDigits(year)}"
        }

        fun formatCompactFa(): String {
            return "${NumberUtils.toPersianDigits(year)}/${NumberUtils.toPersianDigits(month)}/${NumberUtils.toPersianDigits(day)}"
        }
    }

    /**
     * Converts a Gregorian Date to Persian Solar Hijri (Jalali) date
     */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): JalaliDate {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        var gy2 = gy - 1600
        var gm2 = gm - 1
        val gd2 = gd - 1

        var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400
        for (i in 0 until gm2) {
            gDayNo += gDaysInMonth[i]
        }
        if (gm2 > 1 && ((gy2 % 4 == 0 && gy2 % 100 != 0) || (gy2 % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gd2

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 0..11) {
            if (jDayNo < jDaysInMonth[i]) {
                jm = i + 1
                break
            }
            jDayNo -= jDaysInMonth[i]
        }
        val jd = jDayNo + 1

        return JalaliDate(jy, jm, jd)
    }

    fun getCurrentJalaliDate(): JalaliDate {
        val cal = java.util.Calendar.getInstance()
        return gregorianToJalali(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }

    /**
     * Converts ISO 8601 string (e.g. 2026-11-09T13:00:03.158Z) to relative Persian time
     */
    fun toRelativeTimeFa(isoString: String?): String {
        if (isoString == null) return ""
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val cleanStr = isoString.take(19)
            val date = format.parse(cleanStr) ?: return isoString
            val now = System.currentTimeMillis()
            val diffMs = now - date.time

            val diffSeconds = diffMs / 1000
            val diffMinutes = diffSeconds / 60
            val diffHours = diffMinutes / 60
            val diffDays = diffHours / 24

            when {
                diffMinutes < 1 -> "چند لحظه پیش"
                diffMinutes < 60 -> "${NumberUtils.toPersianDigits(diffMinutes.toInt())} دقیقه پیش"
                diffHours < 24 -> "${NumberUtils.toPersianDigits(diffHours.toInt())} ساعت پیش"
                diffDays == 1L -> "دیروز"
                diffDays < 7 -> "${NumberUtils.toPersianDigits(diffDays.toInt())} روز پیش"
                else -> {
                    val cal = java.util.Calendar.getInstance().apply { time = date }
                    val jalali = gregorianToJalali(
                        cal.get(java.util.Calendar.YEAR),
                        cal.get(java.util.Calendar.MONTH) + 1,
                        cal.get(java.util.Calendar.DAY_OF_MONTH)
                    )
                    jalali.formatFa()
                }
            }
        } catch (e: Exception) {
            isoString
        }
    }
}

