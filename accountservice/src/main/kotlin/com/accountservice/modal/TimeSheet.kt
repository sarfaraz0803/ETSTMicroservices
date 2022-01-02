package com.accountservice.modal

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.annotation.Id
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Document("timeSheets")
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

/*@Id
    var _id:ObjectId?=null,*/