package com.accountservice.controller

import com.accountservice.dao.ITimeSheetDao
import com.accountservice.dtos.SetLogTimeDto
import com.accountservice.dtos.TaskDto
import com.accountservice.dtos.SetTaskStatusDto
import com.accountservice.dtos.UpdateTaskDto
import com.accountservice.messages.Message
import com.accountservice.modal.Account
import com.accountservice.modal.EmpTask
import com.accountservice.modal.TimeSheet
import com.accountservice.service.AccountServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.config.RepositoryConfigurationSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import javax.validation.Valid


@RestController
@RequestMapping("/account")
@CrossOrigin(origins = ["*"])
class AccountController {

    @Autowired
    private lateinit var accountServiceImpl: AccountServiceImpl


    //==========================================Account_Crud_Operations=============================================


    @PostMapping("/addAccount")
    fun addNewAccount(@Valid @RequestBody account: Account): ResponseEntity<Any> {
        val acc = accountServiceImpl.addAccount(account)
        return ResponseEntity(acc, HttpStatus.OK)
    }

    @GetMapping("/getAllAccounts")
    fun getAccounts(): ResponseEntity<Any> {
        val accList = accountServiceImpl.getAllAccount()
        return ResponseEntity(accList, HttpStatus.OK)
    }

    @GetMapping("/getAccountById/{id}")
    fun getAccountById(@PathVariable id:Int): ResponseEntity<Any> {
        return ResponseEntity(accountServiceImpl.getAccountById(id), HttpStatus.OK)
    }

   @PutMapping("/updateAccountById/{id}")
   fun updateAccountById(@PathVariable id:Int, @RequestBody account: Account): ResponseEntity<Any>{
       val updateAcc = accountServiceImpl.updateAccountById(id,account)

       return ResponseEntity(updateAcc,HttpStatus.OK)

   }

    @DeleteMapping("/deleteAccountById/{id}")
    fun deleteAccountById(@PathVariable id: Int): ResponseEntity<Any> {
        return ResponseEntity(accountServiceImpl.deleteAccountById(id), HttpStatus.OK)
    }




    //==============================================TimeSheet_Methods===============================================


    @PostMapping("/createSheet")
    fun createTimeSheet(@RequestBody empTimeSheet : TimeSheet):ResponseEntity<Any>{
        return ResponseEntity(accountServiceImpl.createTimeSheet(empTimeSheet),HttpStatus.OK)
    }

    @PutMapping("/assignTask/{id}/{date}")
    fun assignTask(@PathVariable id:Int, @PathVariable date:String, @RequestBody empTask: EmpTask):ResponseEntity<Any>{
        val result = accountServiceImpl.assignTask(id,date,empTask)
        return ResponseEntity(result,HttpStatus.OK)
    }

    @GetMapping("/getTimeSheetById/{id}")
    fun getAllTimeSheets(@PathVariable id:Int):ResponseEntity<Any>{
        val result = accountServiceImpl.getAllTimeSheets(id)
        return ResponseEntity(result,HttpStatus.OK)
    }

    @GetMapping("/getOneSheet/{id}/{date}")
    fun getOneSheet(@PathVariable id:Int, @PathVariable date:String):ResponseEntity<Any>{
        val result = accountServiceImpl.getOneSheet(id,date)
        if(result != null) {
            return ResponseEntity(result, HttpStatus.OK)
        }
        return ResponseEntity("No Sheet for given empId of given date",HttpStatus.NOT_FOUND)
    }

    @GetMapping("/getOneEmpTask")
    fun getOneEmpTask(@RequestBody taskDto: TaskDto):ResponseEntity<Any>{
        return ResponseEntity(accountServiceImpl.getOneEmpTask(taskDto),HttpStatus.OK)
    }


    // This Method is used By MANAGER to delete any task
    @DeleteMapping("/deleteTask/{id}/{date}/{taskId}")
    fun deleteTask(@PathVariable id:Int, @PathVariable date:String, @PathVariable taskId:String): ResponseEntity<Message>{
        val taskDto = TaskDto(id,date,taskId)
        return ResponseEntity(accountServiceImpl.deleteTask(taskDto),HttpStatus.OK)
    }


    // Below Method is for EMPLOYEE to update his task fields
    @PutMapping("/setTaskFields")
    fun setTaskFields(@RequestBody setTaskStatusDto: SetTaskStatusDto):ResponseEntity<String>{
        return ResponseEntity(accountServiceImpl.setTaskFields(setTaskStatusDto),HttpStatus.OK)

    }


    // This Method is used by MANAGER to update task for any employee
    @PutMapping("/updateTask")
    fun updateTask(@RequestBody updateTaskDto: UpdateTaskDto):ResponseEntity<Message>{
        return ResponseEntity(accountServiceImpl.updateTask(updateTaskDto),HttpStatus.OK)

    }

    @PutMapping("/loginTime/{id}/{date}/{time}")
    fun setLoginTime(@PathVariable id: Int, @PathVariable date:String, @PathVariable time:String):ResponseEntity<Any>{
        accountServiceImpl.setLogInTime(id,date,time)
        return ResponseEntity(HttpStatus.OK)
    }

    @PutMapping("/logoutTime/{id}/{date}/{time}")
    fun setLogOutTime(@PathVariable id: Int, @PathVariable date:String, @PathVariable time:String):ResponseEntity<Any>{
        accountServiceImpl.setLogOutTime(id,date,time)
        return ResponseEntity(HttpStatus.OK)
    }



}