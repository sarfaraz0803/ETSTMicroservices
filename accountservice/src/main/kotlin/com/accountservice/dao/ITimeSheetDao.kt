package com.accountservice.dao

import com.accountservice.modal.TimeSheet
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ITimeSheetDao : MongoRepository<TimeSheet, String> {

    @Query(value = "{'employeeId':?0, 'sheetDate':?1}", fields = "{'sheetId':1}")
    fun getTimeSheetId(id:Int,date:String):TimeSheet




}