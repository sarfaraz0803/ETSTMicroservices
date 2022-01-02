package com.accountservice.service

import com.accountservice.client.AccountClient
import com.accountservice.dao.IAccountDao
import com.accountservice.dao.ITimeSheetDao
import com.accountservice.dtos.*
import com.accountservice.messages.Message
import com.accountservice.modal.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.swing.text.DateFormatter


@Service
class AccountServiceImpl: IAccountService {

    @Autowired
    private lateinit var iAccountDao: IAccountDao
    @Autowired
    private lateinit var accountClient: AccountClient
    @Autowired
    private lateinit var iTimeSheetDao: ITimeSheetDao

    private val passwordEncoder = BCryptPasswordEncoder()


    //===============================================Crud_Operations===============================================

    override fun addAccount(account: Account): Any {
        if (!iAccountDao.existsById(account._id)) {
            if(!iAccountDao.existsByUsername(account.username)){
                // If EmployeeCredential Exists in EmpCre collection
                if(!accountClient.empCre(account._id,account.username)) {
                    return Message("These Credential Already Exists [either Id or Username].")
                }
                accountClient.registerCre(account._id,account.username,passwordEncoder.encode(account.password))
                val newEmpAcc = Account(
                    _id = account._id,
                    username = account.username,
                    password = passwordEncoder.encode(account.password),
                    employee = Employee(
                        name = account.employee.name,
                        email = account.username
                    )
                )
                iAccountDao.save(newEmpAcc)
                return Message("Added")
                }else{
                    return Message("Account already exists by username")
                }
            }else{
                return Message("Account already exists by this Id")
            }
    }

    override fun getAllAccount():Any{
        var finalAccounts = mutableListOf<AccountDto>()
        if(iAccountDao.findAll().isNotEmpty()) {
            for(a in iAccountDao.findAll()){
                var accountDto = AccountDto()
                accountDto._id = a._id
                accountDto.username = a.username
                accountDto.password = a.password
                accountDto.employee = a.employee
                accountDto.timeSheet = getAllTimeSheets(a._id)
                finalAccounts.add(accountDto)
            }
            return finalAccounts
        }
        return Message("No Account Exists")

    }

    override fun getAccountById(id: Int): Any {
        var accountDto = AccountDto()
        if(iAccountDao.existsById(id)){
            val acc = iAccountDao.findById(id)
            val getAccountList = getAllTimeSheets(id)
            accountDto._id = acc.get()._id
            accountDto.username = acc.get().username
            accountDto.password = acc.get().password
            accountDto.employee = acc.get().employee
            accountDto.timeSheet = getAccountList
            return accountDto
        }
        return Message("Id not found")
    }

    override fun updateAccountById(id: Int, account: Account): Any{
        if(!iAccountDao.existsById(id)) {
            return Message("No Account at this Id to update.")
        }
        val getAcc = iAccountDao.findById(id)
        //accountClient.updateEmpCre(id,getAcc.get().username,passwordEncoder.encode(account.password))
        return iAccountDao.findById(id).map { oldAcc ->
            val updateAccount: Account =
                oldAcc.copy(
                    _id = id,
                    username = oldAcc.username,
                    password = passwordEncoder.encode(account.password),
                    employee = Employee(
                        name = account.employee.name,
                        address = account.employee.address,
                        age = account.employee.age,
                        email = oldAcc.username,
                        mobile = account.employee.mobile,
                        gender = account.employee.gender,
                        department = account.employee.department,
                        socialCategory = account.employee.socialCategory,
                        physicallyChallenged = account.employee.physicallyChallenged,
                        religion = account.employee.religion,
                        dateOfBirth = account.employee.dateOfBirth,
                        maritalStatus = account.employee.maritalStatus,
                        profileStatus = account.employee.profileStatus,
                        fatherName = account.employee.fatherName
                    )
                )
            iAccountDao.save(updateAccount)
        }
    }

    override fun deleteAccountById(id: Int): Any {
        val sheets = iTimeSheetDao.findAll()
        if(iAccountDao.existsById(id)){
            iAccountDao.deleteById(id)
            accountClient.deleteEmpCre(id)
            for( item in sheets){
                if(item.employeeId == id){
                    iTimeSheetDao.deleteById(item.sheetId)
                }
            }
            return Message("Deleted")
        }
        return Message("No Account for this Id to delete")
    }


    //===============================================Time_Sheet_Functions=============================================


    fun createTimeSheet(timeSheet: TimeSheet):Message{
        if(iAccountDao.existsById(timeSheet.employeeId)){          // #line->Checks Account Exists or not with given EmployeeId
            if(!iTimeSheetDao.existsById(timeSheet.sheetId)){
                val sheetByIdDate = getOneSheet(timeSheet.employeeId,timeSheet.sheetDate)
                if(sheetByIdDate == null){
                    val autoDate = DateTimeFormatter.ofPattern("yyyyMMdd")
                    val date = LocalDate.parse(timeSheet.sheetDate)
                    val autoSheetId = timeSheet.employeeId.toString()+date.format(autoDate)
                    println(autoSheetId)
                    val customTimeSheet = TimeSheet(
                        sheetId = autoSheetId,
                        employeeId = timeSheet.employeeId,
                        sheetDate = timeSheet.sheetDate,
                        lastLogInTime = timeSheet.lastLogInTime,
                        lastLogOutTime = timeSheet.lastLogOutTime,
                        empTask = timeSheet.empTask
                    )
                    iTimeSheetDao.save(customTimeSheet)
                    return Message("Created : $customTimeSheet")
                }
                return Message("Sheet already exist with this date")
            }
            return Message("SheetId must be unique recommended as [EmpId+yyyyMMdd]")
        }
        return Message("Account Does Not Exist for This EmployeeId")
    }

    fun assignTask(empId:Int, sheetDate:String, empTask: EmpTask):Any{
        if(!iAccountDao.existsById(empId)){
            return Message("Given empId Not Exist")
        }
        val sheet1 = getOneSheet(empId,sheetDate)
        val newList = mutableListOf<EmpTask>()
        val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
        val autoSheetId = empId.toString()+LocalDate.parse(sheetDate).format(dateFormat)
        val newEmpTask = EmpTask(
            taskId = autoSheetId+empTask.taskId,
            taskName = empTask.taskName,
            description = empTask.description,
            durationOfTask = empTask.durationOfTask,
            taskExpiryDate = empTask.taskExpiryDate,
            comment = empTask.comment,
            status = empTask.status,
            remainingHours = empTask.durationOfTask
        )
        if (sheet1 == null) {
            newList.add(newEmpTask)
            val createSheet = TimeSheet(
                sheetId = "",
                employeeId = empId,
                sheetDate = sheetDate,
                lastLogInTime = "",
                lastLogOutTime = "",
                empTask = newList
            )
            createTimeSheet(createSheet)
            return Message("Task Created")
        }
        else if(sheet1.empTask?.isNotEmpty() == true){
            for ( i in sheet1.empTask!!){
                if(i.taskId == autoSheetId+empTask.taskId){
                    return Message("TaskId Exist try another ")
                }else{
                    newList.add(i)
                }
            }
            newList.add(newEmpTask)
            sheet1.empTask = newList
        }else{
            newList.add(newEmpTask)
            sheet1.empTask = newList
        }
        iTimeSheetDao.save(sheet1)
        return Message("Task Created")
    }

    fun getAllTimeSheets(id:Int):MutableList<TimeSheet>{
        val sheets = iTimeSheetDao.findAll()
        val getSheets = mutableListOf<TimeSheet>()
        for(i in sheets){
            if(i.employeeId == id) {
                getSheets.add(i)
            }
        }
        return getSheets
    }

    fun getOneSheet(id:Int,date: String):TimeSheet?{
        val sheets = getAllTimeSheets(id)
        if(sheets.isNotEmpty()){
            for(i in sheets){
                if(i.sheetDate == date){
                    return i
                }
            }
        }
        return null
    }

    fun getOneEmpTask(taskDto: TaskDto):Any{
        if(!iAccountDao.existsById(taskDto.empId)){
            return Message("EmployeeId Not Exist")
        }
        val empSheets = getAllTimeSheets(taskDto.empId)
        val reqTask = mutableListOf<EmpTask>()
        if(empSheets.isNotEmpty()){
            for (item in empSheets){
                if(item.sheetDate == taskDto.date){
                    for( task in item.empTask!!){
                        if(task.taskId == taskDto.taskId)
                            reqTask.add(task)
                    }
                    if(reqTask.isEmpty())
                        return Message("Given taskId not exist")
                }
            }
            if(reqTask.size > 1)
                return reqTask
            else if(reqTask.isEmpty())
                return Message("No timeSheet for this date")
            return reqTask[0]
        }
        return Message("TimeSheets not exist for given EmpId")
    }

    fun deleteTask(taskDto: TaskDto):Message{
        if(!iAccountDao.existsById(taskDto.empId)){
            return Message("EmpId Not Exist")
        }
        val sheet1 = getOneSheet(taskDto.empId,taskDto.date)
        val newList = mutableListOf<EmpTask>()
        var flag = 0
        if (sheet1 == null ) {
            return Message("Sheet Not Exist with given empId and date")
        }
        if(sheet1.empTask?.isNotEmpty() == true){
            for ( i in sheet1.empTask!!){
                if(i.taskId != taskDto.taskId){
                    newList.add(i)
                }else{
                    flag = 1
                }
            }
            sheet1.empTask = newList
        }
        iTimeSheetDao.save(sheet1)
        if(flag == 0){
            return Message("No Task with this taskId to delete")
        }
        return Message("Deleted")
    }


    // Employee to Update Task fields
    fun setTaskFields(setTaskStatusDto: SetTaskStatusDto):String{
        if(!iAccountDao.existsById(setTaskStatusDto.empId)){
            return "EmpId Not Exist"
        }
        val sheet1 = getOneSheet(setTaskStatusDto.empId,setTaskStatusDto.date)
        //println("Pehle Wali:$sheet1")
        val newList = mutableListOf<EmpTask>()
        var flag = 0
        if (sheet1 == null) {
            return "Sheet Not Exist with given empId and date"
        }
        if(sheet1.empTask?.isNotEmpty() == true){
            for ( i in sheet1.empTask!!){
                if(i.taskId == setTaskStatusDto.taskId){
                    val upTask = EmpTask(
                        taskId = i.taskId,
                        taskName = i.taskName,
                        description = i.description,
                        taskCreation = i.taskCreation,
                        durationOfTask = i.durationOfTask,
                        taskExpiryDate = i.taskExpiryDate,
                        comment = setTaskStatusDto.comment,
                        status = setTaskStatusDto.status,
                        consumedHours = setTaskStatusDto.consumedHours,
                        remainingHours = "%.3f".format(((i.durationOfTask)-(setTaskStatusDto.consumedHours))).toDouble(),
                        progress = "%.3f".format((((setTaskStatusDto.consumedHours) / (i.durationOfTask)) * 100)).toDouble(),
                    )
                    newList.add(upTask)
                    flag = 1
                }else{
                    newList.add(i)
                }
            }
            sheet1.empTask = newList
        }
        //println("After Wali : $sheet1")
        if(flag == 0)
            return "TaskId Not Exist"
        iTimeSheetDao.save(sheet1)
        return "Status Updated"

    }


    // Only Manager can update the task
    fun updateTask(updateTaskDto: UpdateTaskDto):Message{
        if(!iAccountDao.existsById(updateTaskDto.empId)){
            return Message("EmpId Not Exist")
        }
        val sheet1 = getOneSheet(updateTaskDto.empId,updateTaskDto.date)
        val newList = mutableListOf<EmpTask>()
        var flag = 0
        if (sheet1 == null) {
            return Message("Sheet Not Exist with given empId and date")
        }
        if(sheet1.empTask?.isNotEmpty() == true){
            for ( i in sheet1.empTask!!){
                if(i.taskId == updateTaskDto.taskId){
                    val upTask = EmpTask(
                        taskId = i.taskId,              //   No need to give taskId in EmpTask in requestBody
                        taskName = updateTaskDto.empTask.taskName,
                        description = updateTaskDto.empTask.description,
                        taskCreation = i.taskCreation,
                        durationOfTask = updateTaskDto.empTask.durationOfTask,
                        taskExpiryDate = updateTaskDto.empTask.taskExpiryDate,
                        comment = updateTaskDto.empTask.comment,
                        status = i.status,
                        consumedHours = i.consumedHours,
                        remainingHours = "%.3f".format(((updateTaskDto.empTask.durationOfTask)-(i.consumedHours))).toDouble(),
                        progress = "%.3f".format((((i.consumedHours)/(updateTaskDto.empTask.durationOfTask))*100)).toDouble(),
                    )
                    newList.add(upTask)
                    flag = 1
                }else{
                    newList.add(i)
                }
            }
            sheet1.empTask = newList
        }
        if(flag == 0)
            return Message("TaskId Not Exist")
        iTimeSheetDao.save(sheet1)
        return Message("Task Updated")
    }


    // This method sets the login time of the employee time sheet of particular date whenever he logs in to his account
    fun setLogInTime(empId: Int,date: String,time:String){
        val setSheetTime = getOneSheet(empId,date)
        val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
        val date1 = LocalDate.parse(date)
        val autoSheetId = empId.toString()+date1.format(dateFormat)
        if(setSheetTime == null){
            val createSheet = TimeSheet(
                sheetId = autoSheetId,
                employeeId = empId,
                sheetDate = date,
                lastLogInTime = time,
            )
            iTimeSheetDao.save(createSheet)
        }else{
            val updateTime = TimeSheet(
                sheetId = autoSheetId,
                employeeId = setSheetTime.employeeId,
                sheetDate = setSheetTime.sheetDate,
                sheetCreatedAt = setSheetTime.sheetCreatedAt,
                lastLogInTime = time,
                lastLogOutTime = setSheetTime.lastLogOutTime,
                empTask = setSheetTime.empTask
            )
            //println("Before : $setSheetTime")
            iTimeSheetDao.save(updateTime)
            //println(iTimeSheetDao.findById(autoSheetId))
        }
    }


    // This method sets the logout time of the employee time sheet of particular date whenever he logs Out to his account
    fun setLogOutTime(empId: Int,date: String,time:String){
        val setSheetTime = getOneSheet(empId,date)
        if(setSheetTime != null){
            val updateOutTime = TimeSheet(
                sheetId = setSheetTime.sheetId,
                employeeId = setSheetTime.employeeId,
                sheetDate = setSheetTime.sheetDate,
                sheetCreatedAt = setSheetTime.sheetCreatedAt,
                lastLogInTime = setSheetTime.lastLogInTime,
                lastLogOutTime = time,
                empTask = setSheetTime.empTask
            )
            //println("Before : $setSheetTime")
            iTimeSheetDao.save(updateOutTime)
            //println("After : $updateOutTime")
        }
    }






}