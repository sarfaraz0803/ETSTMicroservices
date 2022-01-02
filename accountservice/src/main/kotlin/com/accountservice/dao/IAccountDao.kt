package com.accountservice.dao



import com.accountservice.modal.Account
import com.accountservice.modal.TimeSheet
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IAccountDao:MongoRepository<Account, Int> {
    fun existsByUsername(username:String):Boolean
    fun findByUsername(userName:String): Account

    @Query(value = "{'_id':?0}", fields = "{'timeSheet':1}")
    fun getTimeSheetById(id:Int):TimeSheet


    /*@Query(value = "{'_id' : ?0}", fields = "{'username':1}")
    fun usernameById(id:Int):String
    @Query(value = "{'_id' : ?0}", fields = "{'username' : 0, 'employee' : 0}")
    fun passwordById(id:Int):String*/

}