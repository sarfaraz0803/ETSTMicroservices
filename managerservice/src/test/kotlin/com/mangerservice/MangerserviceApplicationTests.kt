package com.mangerservice

import com.mangerservice.dao.IManagerDao
import com.mangerservice.modal.ManagerAuth
import com.mangerservice.service.ManageServiceImpl
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class MangerserviceApplicationTests {

	/*@Autowired
	private lateinit var managerServiceImpl: ManageServiceImpl

	@MockBean
	private lateinit var iManagerDao: IManagerDao

	@Test
	fun registerManagerTest(){
		val newManager = ManagerAuth(_id = null, username = "Bobby@gmail.com", name = "Bobby", email = "Bobby@gmail.com", password = "Bobbynull")
		//println(newManager)
		given(iManagerDao.save(newManager)).willReturn(newManager)
		assert(managerServiceImpl.registerManager(newManager) == newManager)
	}*/


	@Test
	fun contextLoads() {
	}



}
