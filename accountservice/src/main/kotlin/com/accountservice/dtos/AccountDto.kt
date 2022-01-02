package com.accountservice.dtos

import com.accountservice.modal.Employee
import com.accountservice.modal.TimeSheet

class AccountDto {
        var _id: Int=0
        var username: String=""
        var password: String=""
        var employee: Employee = Employee()
        var timeSheet: MutableList<TimeSheet> = mutableListOf()
}