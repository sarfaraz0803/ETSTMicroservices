package com.mangerservice.controller

import com.mangerservice.client.ManagerClient
import com.mangerservice.dao.IJwtCreDao
import com.mangerservice.dtos.ManagerAuthDto
import com.mangerservice.dtos.ManagerResponseDto
import com.mangerservice.dtos.TaskDto
import com.mangerservice.dtos.UpdateTaskDto
import com.mangerservice.exception.ValidationException
import com.mangerservice.jwtutils.JwtTokenValidation
import com.mangerservice.modal.*
import com.mangerservice.service.ManageServiceImpl
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
@RequestMapping("/manager")
@CrossOrigin(origins = ["*"])
class ManagerController {

    @Autowired
    private lateinit var managerServiceImpl: ManageServiceImpl
    @Autowired
    private lateinit var managerClient: ManagerClient
    @Autowired
    private lateinit var iJwtCreDao: IJwtCreDao
    @Autowired
    private lateinit var jwtTokenValidation: JwtTokenValidation
    private val passwordEncoder = BCryptPasswordEncoder()



    //=================================MANAGER_CREDENTIALS_FUNCTION=================================


    /* This endpoint is used to "REGISTER_MANAGER" */
    @PostMapping("/register")
    fun registerManager(@Valid @RequestBody managerAuth: ManagerAuth, bindingResults: BindingResult ): ResponseEntity<String>{
        if(bindingResults.hasErrors()){
            val errors: List<FieldError> = bindingResults.fieldErrors
            val errorList: MutableList<String?> = mutableListOf()
            for (er in errors){ errorList.add(er.defaultMessage) }
            throw ValidationException(errorList.asReversed())
        }
        if(managerAuth.username != managerAuth.email){
            return ResponseEntity("username should be same as email",HttpStatus.BAD_REQUEST)
        }
        val manReg = managerServiceImpl.registerManager(managerAuth)
        return ResponseEntity("${manReg.username} Successfully Registered ",HttpStatus.OK)
    }


    /* This endpoint is used to "LOGIN_MANAGER" */
    @PostMapping("/login")
    fun managerAuthentication(@Valid @RequestBody managerAuthDto: ManagerAuthDto ,bindingResults: BindingResult, response : HttpServletResponse): ResponseEntity<Any>{
        if(bindingResults.hasErrors()){
            val errors: List<FieldError> = bindingResults.fieldErrors
            val errorList: MutableList<String?> = mutableListOf()
            for (er in errors){ errorList.add(er.defaultMessage) }
            throw ValidationException(errorList.asReversed())
        }
        val manLogin = this.managerServiceImpl.loginManager(managerAuthDto)
        if(manLogin != null){
            if(passwordEncoder.matches(managerAuthDto.password,manLogin.password)){

                val token = jwtTokenValidation.generateToken(manLogin)
                response.addHeader("UserId",manLogin._id.toString())
                response.addHeader("Authorization", token)
                val loggedManager = managerServiceImpl.getLoggedManager(manLogin.username)
                val managerResponseDto = loggedManager?.let {
                        ManagerResponseDto(
                            manLogin._id.toString(),
                            it.username,
                            it.name,
                            it.email,
                            it.password,
                            token
                            )
                    }
                return ResponseEntity(managerResponseDto,HttpStatus.OK)
                //return ResponseEntity("UserId : ${manLogin._id.toString()},Token : $token",HttpStatus.OK)
            }
            return ResponseEntity("Invalid Password",HttpStatus.OK)
        }else{
            return ResponseEntity("Profile Not Exist.",HttpStatus.OK)
        }
    }


    /* This endpoint is used to "LOGOUT_MANAGER" */
    @PostMapping("/logout")
    fun logOutManager(request: HttpServletRequest):ResponseEntity<Any>{
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        iJwtCreDao.deleteById(userId)
                        return ResponseEntity("Successfully LoggedOut",HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & userId",HttpStatus.OK)
    }


    /* This endpoint is used to "DELETE_MANAGER_CREDENTIALS" */
    @DeleteMapping("/deleteCredentials/{username}")
    fun deleteCredentialDetails(@PathVariable username: String):ResponseEntity<Any>{
        return ResponseEntity(managerServiceImpl.deleteCredentialsDetails(username),HttpStatus.OK)
    }




    // ======================================EMPLOYEE_ACCOUNT_OPERATIONS===============================================


    /* This endpoint is used to "ADD_EMPLOYEE_ACCOUNT OR REGISTER_EMPLOYEE" */
    @PostMapping("/addAccount")
    fun addNewAccount(request:HttpServletRequest,@Valid @RequestBody account: Account, bindingResults: BindingResult): ResponseEntity<Any>{
        if(bindingResults.hasErrors()){
            val errors: List<FieldError> = bindingResults.fieldErrors
            val errorList: MutableList<String?> = mutableListOf()
            for (er in errors){ errorList.add(er.defaultMessage) }
            throw ValidationException(errorList.asReversed())
        }
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        val acc = managerClient.addAccount(account)
                        return ResponseEntity(acc, HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Unauthorized!!! Please provide token & userId",HttpStatus.UNAUTHORIZED)

    }


    /* This endpoint is used to "GET_ALL_EMPLOYEE_ACCOUNTS" */
    @GetMapping("/getAllAccounts")
    fun getAccounts(request: HttpServletRequest): ResponseEntity<Any>{
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        val accList = managerClient.getAllEmpAccounts()
                        return ResponseEntity(accList, HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.NO_CONTENT)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Unauthorized!!! Please provide token & userId",HttpStatus.UNAUTHORIZED)

    }


    /* This endpoint is used to "GET_EMPLOYEE_ACCOUNT_BY_ID" */
    @GetMapping("/getAccountById/{id}")
    fun getAccountById(request: HttpServletRequest,@PathVariable id:Int): ResponseEntity<Any> {
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(managerClient.getAccountById(id), HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.NO_CONTENT)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Unauthorized!!! Please provide token & userId",HttpStatus.UNAUTHORIZED)
    }


    /* This endpoint is used to "UPDATE_EMPLOYEE_ACCOUNT" by manager */
    @PutMapping("/updateAccountById/{id}")
    fun updateAccount(request: HttpServletRequest ,@PathVariable id: Int, @Valid @RequestBody account: Any): ResponseEntity<Any> {
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(managerClient.updateAccountById(id,account), HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.NO_CONTENT)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Unauthorized!!! Please provide token & userId",HttpStatus.UNAUTHORIZED)
    }


    /* This endpoint is used to "DELETE_EMPLOYEE_ACCOUNT_BY_ID" by manager only manager can delete employee account */
    @DeleteMapping("/deleteAccount/{id}")
    fun deleteAccountById(request: HttpServletRequest ,@PathVariable id: Int): ResponseEntity<Any>{
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(managerClient.deleteAccountById(id), HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.NO_CONTENT)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Unauthorized!!! Please provide token & userId",HttpStatus.UNAUTHORIZED)
    }



    // ============================================TIME_SHEET_OPERATIONS===============================================


    /* This endpoint is used to "CREATE_TIME_SHEET" for employee, if manager wants to.  */
    @PostMapping("/createSheet")
    fun createTimeSheet(request: HttpServletRequest ,@RequestBody timeSheet: TimeSheet):ResponseEntity<Any>{
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        val result1 = managerClient.createTimeSheet(timeSheet)
                        if(result1 != null){
                            return ResponseEntity(result1,HttpStatus.OK)
                        }
                        return ResponseEntity("Sheets Not Exist.",HttpStatus.NOT_FOUND)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.NO_CONTENT)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Unauthorized!!! Please provide token & userId",HttpStatus.UNAUTHORIZED)
    }


    /* This endpoint is used to "ASSIGN_TASK" to employee by manager.  */
    @PutMapping("/assignTask/{id}/{date}")
    fun assignTask(request: HttpServletRequest ,@PathVariable id:Int, @PathVariable date:String, @RequestBody empTask: EmpTask):ResponseEntity<Any>{
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(managerClient.assignTask(id,date,empTask),HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.NO_CONTENT)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Unauthorized!!! Please provide token & userId",HttpStatus.UNAUTHORIZED)
    }


    /* This endpoint is used to "GET_EMPLOYEE_TIMESHEET_BY_ID" */
    @GetMapping("/getTimeSheetById/{id}")
    fun getSheet(request: HttpServletRequest,@PathVariable id:Int):ResponseEntity<Any>{
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        val res1 = managerClient.getAllTimeSheets(id)
                        if(res1.isEmpty()){
                            return ResponseEntity("TimeSheets Not Exist",HttpStatus.OK)
                        }
                        return ResponseEntity(res1,HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Unauthorized!!! Please provide token & userId",HttpStatus.UNAUTHORIZED)

    }


    /* This endpoint is used to "GET_TIMESHEET_BY_ID_AND_DATE" for any employee */
    @GetMapping("/getOneSheet/{id}/{date}")
    fun getOneSheet(request: HttpServletRequest ,@PathVariable id:Int, @PathVariable date:String):ResponseEntity<Any>{
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        try {
                            return ResponseEntity(managerClient.getOneSheet(id,date),HttpStatus.OK)
                        }catch (e:Exception){
                            return ResponseEntity("No Sheet for given empId of given date",HttpStatus.OK)
                        }
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.NO_CONTENT)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Unauthorized!!! Please provide token & userId",HttpStatus.UNAUTHORIZED)
    }

    @GetMapping("/getTask")
    fun getOneEmpTask(request: HttpServletRequest,@RequestBody taskDto: TaskDto):ResponseEntity<Any>{
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(managerClient.getOneEmpTask(taskDto),HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & userId",HttpStatus.OK)
    }


    /* This endpoint is used to "UPDATE_TASK" for any employee, if manager wants to update something in the task. */
    @PutMapping("/updateTask")
    fun updateTask(request: HttpServletRequest,@RequestBody updateTaskDto: UpdateTaskDto):ResponseEntity<Any>{
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(managerClient.updateTask(updateTaskDto),HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.NO_CONTENT)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Unauthorized!!! Please provide token & userId",HttpStatus.UNAUTHORIZED)
    }


    /* This endpoint is used to "DELETE_TASK", if manager assigned it to wrong employee or no longer needed.*/
    @DeleteMapping("/deleteTask/{id}/{date}/{taskId}")
    fun deleteAnyTask(request: HttpServletRequest,@PathVariable id:Int, @PathVariable date:String, @PathVariable taskId:String):ResponseEntity<Any>{
        val userId = request.getHeader("UserId")
        val token = request.getHeader("Authorization")
        if(token != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, token)
                    if( result == true){
                        return ResponseEntity(managerClient.deleteTask(id,date,taskId),HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(token)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.NO_CONTENT)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Unauthorized!!! Please provide token & userId",HttpStatus.UNAUTHORIZED)

    }

}