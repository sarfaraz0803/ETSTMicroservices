package com.mangerservice.modal

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class EmpTask(
    var taskId:String = "",
    var taskName:String = "",
    var description:String = "",
    var taskCreation:String  = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
    var durationOfTask:Double = 0.0,     // duration of task (in hrs)
    var taskExpiryDate:String = "",
    var comment:String = "",
    var status:String = "",
    //var startTime:Double = 0.0,              // in seconds
    //var stopTime:Double = 0.0,               // in seconds
    var consumedHours:Double = 0.0,
    var remainingHours:Double = 0.0,
    var progress:Double = 0.0,              // in percent age
)
