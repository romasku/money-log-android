package romasku.moneylog.services

import romasku.moneylog.GetDate
import romasku.moneylog.lib.makeEffector
import java.time.LocalDate

fun makeTimeEffector() =
    makeEffector { _: GetDate -> LocalDate.now() }
