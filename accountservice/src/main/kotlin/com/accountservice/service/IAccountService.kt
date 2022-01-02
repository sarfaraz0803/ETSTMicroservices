package com.accountservice.service

import com.accountservice.modal.Account
import com.accountservice.modal.TimeSheet
import java.util.*

interface IAccountService {

    fun addAccount(account: Account): Any
    fun getAllAccount(): Any
    fun getAccountById(id:Int): Any
    fun updateAccountById(id: Int, account: Account): Any
    fun deleteAccountById(id:Int): Any

    // TimeSheet operations

    //fun createTimeSheet(timeSheet: TimeSheet):TimeSheet?
    //fun getAllTimeSheetByEmpId(empId:Int):MutableList<TimeSheet>?
    //fun getTimeSheetById(id:Int):Optional<TimeSheet>?

}