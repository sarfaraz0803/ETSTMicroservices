package com.mangerservice.modal

import org.springframework.data.annotation.Id
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TimeSheet(
    @Id
    var sheetId:String = "",
    var employeeId:Int = 0,
    var sheetDate: String = "", // = LocalDate.now().dayOfMonth,
    var sheetCreatedAt:String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
    var lastLogInTime:String = "",
    var lastLogOutTime:String = "",
    var empTask:MutableList<EmpTask>? = mutableListOf(),
)
