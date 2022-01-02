package com.accountservice.dtos

import java.time.LocalDate
import java.util.*

data class SetLogTimeDto(
    var empId:Int,
    var date:String,
    var time:String
)
