package com.mangerservice.modal
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


@Document("Account")
data class Account(
    @Id
    var _id: Int,        // employeeAccount number
    var username: String,
    @field:NotBlank(message = "Password should not be blank")
    @field:Size(min = 6, max = 12, message = "Password length must be between 6-12")
    var password: String,
    var employee: Employee,
)
