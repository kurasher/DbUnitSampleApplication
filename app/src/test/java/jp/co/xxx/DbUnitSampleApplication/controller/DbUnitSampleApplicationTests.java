package jp.co.xxx.DbUnitSampleApplication.controller;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DbUnitSampleApplicationTests {
	@Autowired
	DbUnitController controller;

	@Autowired
	private MockMvc mvc;

	@BeforeEach
	public void setup() throws Exception {
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	/**
	 * テストのアサーションを実施するメソッド
	 *
	 * @param expectResponse
	 */
	private void testAssertion(InputStream expectResponse, MvcResult result) throws IOException, JSONException {
		String responseJson = StreamUtils.copyToString(expectResponse, StandardCharsets.UTF_8);
		String actual = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		JSONAssert.assertEquals(responseJson, actual, JSONCompareMode.STRICT);
	}

	@Test
	@DisplayName("【正常系】statusにアクセスした時にDBにもアクセスし、jsonでステータスを返すこと")
	public void returnStatusTest() throws Exception {
		InputStream expectResponse = getClass().getResourceAsStream("【正常系】ステータスのURLを叩いたときに取得データが3件の場合のレスポンス.json");
		String inputXml  = "";
		String outputXml = "";
		MvcResult result = mvc.perform(get("/status"))
						.andExpect(status().isOk())
						.andReturn();

		testAssertion(expectResponse, result);
	}
}
