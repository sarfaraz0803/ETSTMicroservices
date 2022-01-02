package com.employeeservice.modal


import org.hibernate.validator.constraints.UniqueElements
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("employeeCredentials")
data class EmpCre (
    @Id
    var _id: Int,
    @field:UniqueElements
    var username: String,
    var password: String
)
