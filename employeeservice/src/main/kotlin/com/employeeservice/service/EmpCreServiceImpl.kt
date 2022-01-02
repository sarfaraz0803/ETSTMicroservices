package com.employeeservice.service

import com.employeeservice.dao.IEmpCreDao
import com.employeeservice.exception.Warning
import com.employeeservice.modal.EmpCre
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class EmpCreServiceImpl {

    @Autowired
    private lateinit var iEmpCreDao: IEmpCreDao
    var passwordEncoder = BCryptPasswordEncoder()


    fun empLogIn(empCre: EmpCre): EmpCre? {
        if(iEmpCreDao.existsByUsername(empCre.username)) {
            return iEmpCreDao.findByUsername(empCre.username)
        }
        return null
    }

    fun registerEmployee(id:Int, username:String, pass:String) {
        iEmpCreDao.save(EmpCre(id,username,pass))
    }

    fun employeeCheck(id:Int,username:String):Boolean{
        if(!iEmpCreDao.existsById(id)){
            if(!iEmpCreDao.existsByUsername(username))
                return true
        }
        return false
    }

    fun deleteEmpCre(id:Int){
        if(iEmpCreDao.existsById(id)){
            iEmpCreDao.deleteById(id)
        }

    }

    fun update(id:Int, username:String, pass:String){
        iEmpCreDao.findById(id).map { oldCre->
            val newCre = oldCre.copy(
                _id = id,
                username = oldCre.username,
                password = passwordEncoder.encode(pass)
            )
            iEmpCreDao.save(newCre)
        }
    }
}
