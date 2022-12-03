package jp.co.xxx.DbUnitSampleApplication;

import jp.co.xxx.DbUnitSampleApplication.controller.DbUnitController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DbUnitSampleApplicationTests {
	@Autowired
	DbUnitController controller;

	@Autowired
	private MockMvc mvc;

	@BeforeEach
	public void setup() throws Exception {
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	@DisplayName("【正常系】statusにアクセスした時にjsonでステータスを返すこと")
	public void returnStatusTest() throws Exception {
		MvcResult result = mvc.perform(get("/status"))
						.andExpect(status().isOk())
						.andReturn();
	}
}
