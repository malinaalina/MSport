package m.alina.msport.util

import java.util.Calendar

private val RU_DAY_ABBREVIATIONS = mapOf(
    Calendar.MONDAY to "ПН",
    Calendar.TUESDAY to "ВТ",
    Calendar.WEDNESDAY to "СР",
    Calendar.THURSDAY to "ЧТ",
    Calendar.FRIDAY to "ПТ",
    Calendar.SATURDAY to "СБ",
    Calendar.SUNDAY to "ВС",
)

fun russianDayAbbrev(dayOfWeek: Int): String = RU_DAY_ABBREVIATIONS.getValue(dayOfWeek)
