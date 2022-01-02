package com.accountservice

import com.accountservice.dao.IAccountDao
import com.accountservice.dao.ITimeSheetDao
import com.accountservice.dtos.AccountDto
import com.accountservice.modal.Account
import com.accountservice.modal.EmpTask
import com.accountservice.modal.Employee
import com.accountservice.modal.TimeSheet
import com.accountservice.service.AccountServiceImpl
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDateTime

@SpringBootTest
class AccountserviceApplicationTests {

	@Autowired
	private lateinit var accountServiceImpl: AccountServiceImpl
	@MockBean
	private lateinit var iAccountDao: IAccountDao
	@MockBean
	private lateinit var iTimeSheetDao: ITimeSheetDao

	/*@Test
	fun getAllAccountTest(){
		var oneAccount = Account(201,"arbaaz201","arbaaz201",
			Employee("arbaaz","mumbai",55,"arbaaz@gmail.com",235146987,"male","acting","celebrity",false,"acting","01022001","married","active","saleem"))
		var oneTimeSheet = TimeSheet("20120211214",201,"2021-12-14",LocalDateTime.now().toString(),"time","time",
			mutableListOf(EmpTask("201202112141","task1","Task1Description","timeCreation","pending","this is comment",4),
				EmpTask("201202112142","task2","Task2Description","timeCreation","pending","this is comment",4)))
		var oneAccountDto = AccountDto()
		oneAccountDto._id = oneAccount._id
		oneAccountDto.username = oneAccount.username
		oneAccountDto.password = oneAccount.password
		oneAccountDto.employee = oneAccount.employee
		oneAccountDto.timeSheet = mutableListOf(oneTimeSheet)
		var oneAccoutList = mutableListOf<AccountDto>(oneAccountDto)

		given(iAccountDao.findAll()).willReturn(mutableListOf(oneAccount))
		assert(accountServiceImpl.getAllAccount() == mutableListOf<Account>())

	}*/


	@Test
	fun contextLoads() {
	}

}
