package com.accountservice.dtos

import java.time.LocalDate
import java.util.*

data class TaskDto(
    var empId:Int,
    var date: String,
    var taskId:String,
)