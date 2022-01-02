package com.accountservice.dtos

data class SetTaskStatusDto(
    var empId:Int,
    var date:String,
    var taskId:String,
    var comment:String,
    var status:String,
    var consumedHours:Double
)