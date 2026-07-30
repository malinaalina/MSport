package m.alina.msport.util

private const val PHONE_PREFIX = "+7"
const val PHONE_MAX_DIGITS = 10

fun formatRussianPhone(phone: String): String {
    val digits = phone.removePrefix(PHONE_PREFIX).filter { it.isDigit() }.take(PHONE_MAX_DIGITS)
    if (digits.isEmpty()) return phone
    return buildString {
        append(PHONE_PREFIX)
        append('(')
        digits.forEachIndexed { index, digit ->
            append(digit)
            when (index) {
                2 -> append(')')
                5 -> append('-')
                7 -> append('-')
            }
        }
    }
}
