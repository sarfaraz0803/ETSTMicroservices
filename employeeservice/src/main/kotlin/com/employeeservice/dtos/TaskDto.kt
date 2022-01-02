package com.employeeservice.dtos

import java.time.LocalDate

data class TaskDto(
    var empId:Int,
    var date:String,
    var taskId:String,
)