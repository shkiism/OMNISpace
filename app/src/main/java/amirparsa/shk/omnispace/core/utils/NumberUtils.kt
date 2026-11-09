package amirparsa.shk.omnispace.core.utils

object NumberUtils {

    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun toPersianDigits(text: String): String {
        val sb = java.lang.StringBuilder()
        for (char in text) {
            if (char in '0'..'9') {
                sb.append(persianDigits[char - '0'])
            } else {
                sb.append(char)
            }
        }
        return sb.toString()
    }

    fun toPersianDigits(number: Int): String {
        return toPersianDigits(number.toString())
    }

    fun toPersianDigits(number: Long): String {
        return toPersianDigits(number.toString())
    }

    fun formatDurationFa(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when {
            hours > 0 && minutes > 0 -> "${toPersianDigits(hours)} ساعت و ${toPersianDigits(minutes)} دقیقه"
            hours > 0 -> "${toPersianDigits(hours)} ساعت"
            else -> "${toPersianDigits(minutes)} دقیقه"
        }
    }
}

