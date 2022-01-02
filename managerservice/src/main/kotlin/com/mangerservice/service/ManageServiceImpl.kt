package com.mangerservice.service


import com.mangerservice.dao.IManagerDao
import com.mangerservice.dtos.ManagerAuthDto
import com.mangerservice.modal.ManagerAuth
import com.mangerservice.exception.Warning
import org.apache.logging.log4j.message.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class ManageServiceImpl:IManagerService{

    @Autowired
    private lateinit var iManagerDao: IManagerDao

    private val passwordEncoder = BCryptPasswordEncoder()


    // To "REGISTER_MANAGER"

    override fun registerManager(managerAuth: ManagerAuth): ManagerAuth {
        if(!iManagerDao.existsByUsername(managerAuth.username)){
            val newManagerAuth = ManagerAuth(
                _id = managerAuth._id,
                username = managerAuth.username,
                name = managerAuth.name,
                email = managerAuth.email,
                password = passwordEncoder.encode(managerAuth.password)
            )
            return iManagerDao.save(newManagerAuth)
        }else{
            throw Warning("Username/Email Already Exist.")
        }
    }



    // TO "LOGIN_MANAGER"

    override fun loginManager(managerAuthDto: ManagerAuthDto): ManagerAuth? {
        if(iManagerDao.existsByUsername(managerAuthDto.username)) {
            return iManagerDao.findByUsername(managerAuthDto.username)
        }
        return null
    }



    // TO "GET_LOGGED_MANAGER"

    fun getLoggedManager(user:String): ManagerAuth? {
        val logManager = iManagerDao.findByUsername(user)
        val cusManager = logManager?.let {
            ManagerAuth(
                _id = it._id,
                username = it.username,
                name = it.name,
                email = it.email,
                password = "********"
            )
        }
        return cusManager
    }



    // TO "DELETE_MANAGER_CREDENTIALS"

    fun deleteCredentialsDetails(username : String): String {
        val delUser = iManagerDao.findByUsername(username)
        if(delUser != null){
            delUser._id?.let { iManagerDao.deleteById(it) }
        }else{
            throw Warning("No Credentials To Delete")
        }
        return "Deleted"
    }


}