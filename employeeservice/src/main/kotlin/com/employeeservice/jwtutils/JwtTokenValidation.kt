package com.employeeservice.jwtutils

import com.employeeservice.modal.JwtCredentials
import com.employeeservice.dao.IJwtCreDao
import com.employeeservice.modal.EmpCre
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenValidation {

    @Autowired
    private lateinit var iJwtCreDao: IJwtCreDao

    fun generateToken(empCre: EmpCre):String{
        val issuer = empCre._id.toString()
        val appSecret = empCre.username
        val token = Jwts.builder()
            .setIssuer(issuer)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))   // 1 day
            .signWith(SignatureAlgorithm.HS512, appSecret)
            .compact()
        val jwtCre = JwtCredentials(empCre._id.toString(),empCre.username,appSecret,token)
        iJwtCreDao.save(jwtCre)
        return token
    }

    fun validateUserToken(userId:String, token:String):Any{
        val getJwtCre = iJwtCreDao.findById(userId)
        if(getJwtCre != null){
            val tokenIssuerFromDb = Jwts.parser().setSigningKey(getJwtCre.get().secretkey).parseClaimsJws(getJwtCre.get().token).body.issuer
            val tokenIssuerFromRequest = iJwtCreDao.credentialFromToken(token)?._id
            if(tokenIssuerFromDb == tokenIssuerFromRequest){
                return true
            }
            return false
        }
        return false
    }

    fun deleteJwtCre(delToken: String){
        val delCre = iJwtCreDao.credentialFromToken(delToken)?._id
        if(delCre != null) { iJwtCreDao.deleteById(delCre) }
    }

    fun isTokenExpired(token:String):Boolean{
        val tokenCre = iJwtCreDao.credentialFromToken(token)
        if(tokenCre != null){
            val tokenBody = Jwts.parser().setSigningKey(tokenCre.secretkey).parseClaimsJws(token).body
            return tokenBody.expiration.before(Date(System.currentTimeMillis()))
        }
        return true
    }


}