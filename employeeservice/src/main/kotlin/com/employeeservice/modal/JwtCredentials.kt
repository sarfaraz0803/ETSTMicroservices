package com.employeeservice.modal

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.annotation.Id

@Document("tokenCredential")
data class JwtCredentials(
    @Id
    var _id:String,
    var username:String,
    var secretkey:String,
    var token:String
)
