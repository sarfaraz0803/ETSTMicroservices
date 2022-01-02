package com.mangerservice.dtos

import com.mangerservice.modal.EmpTask
import java.time.LocalDate

data class UpdateTaskDto(
    var empId:Int,
    var date:String,
    var taskId:String,
    var empTask: EmpTask
)