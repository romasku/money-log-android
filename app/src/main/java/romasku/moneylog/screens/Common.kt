package romasku.moneylog.screens

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun dateToStr(date: LocalDate): String {
    fun Int.pad() = this.toString().padStart(2, '0')
    return "${date.dayOfMonth.pad()}/${date.monthValue.pad()}/${date.year}"
}


fun LocalDate.toUTCEpochMillisecond() =
    atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()

fun millisecondUTCToLocalDate(millis: Long): LocalDate {
    ZoneId.systemDefault()
    val instant = Instant.ofEpochMilli(millis)
    val datetime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
    return datetime.toLocalDate()
}