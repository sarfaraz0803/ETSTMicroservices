package com.accountservice.modal

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.NotBlank


@Document("account")
data class Account(
    @Id
    var _id: Int,        // employeeAccount number
    var username: String,
    @field:NotBlank
    var password: String,
    var employee: Employee,
)
