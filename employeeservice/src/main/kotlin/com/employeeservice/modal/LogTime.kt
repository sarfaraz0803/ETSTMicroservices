package com.employeeservice.modal

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.annotation.Id
import java.time.LocalDate

@Document("logTimeDetail")
data class LogTime(
    @Id
    var _id: ObjectId?=null,
    var empId:Int,
    var date:String,
    var loginTime:String,
    var logoutTime:String
)
