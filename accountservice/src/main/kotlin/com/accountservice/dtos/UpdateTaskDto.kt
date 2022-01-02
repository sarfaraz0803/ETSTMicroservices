package com.accountservice.dtos

import com.accountservice.modal.EmpTask
import java.time.LocalDate

data class UpdateTaskDto(
    var empId:Int,
    var date: String,
    var taskId:String,
    var empTask: EmpTask
)