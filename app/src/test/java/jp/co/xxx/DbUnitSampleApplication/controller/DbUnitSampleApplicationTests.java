package jp.co.xxx.DbUnitSampleApplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import common.util.DbUnitUtil;
import common.util.TableConstant;
import common.util.TestCommonUtil;
import common.util.TestConfiguration;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
class DbUnitSampleApplicationTests {
	@Autowired
	DbUnitController controller;

	@Autowired
	private MockMvc mvc;

	private static TestConfiguration testConfiguration;
	private static DataSource dataSource;
	private static IDatabaseConnection iDatabaseConnection;
	private static IDataSet targetDataSet;

  /**
   * テスト対象のテーブル群
   */
  private List<String> tableList = Arrays.asList(TableConstant.Book);

  /**
   * DBUnit上で除外するためのフィルター項目
   */
  private String[] excludedFilter =
          new String[]{
                  "CREATE_DATETIME",
                  "UPDATE_DATETIME"
          };


  @BeforeAll
  public static void init() throws Exception{
    testConfiguration = new TestConfiguration();
    dataSource = testConfiguration.dataSource();
    iDatabaseConnection = testConfiguration.connection(dataSource);
    targetDataSet = iDatabaseConnection.createDataSet();
  }

	@BeforeEach
	public void setup() throws Exception {
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@AfterEach
  public void after()throws Exception{
    TestCommonUtil.clearTableRecords(
            iDatabaseConnection, TableConstant.DB, TableConstant.SCHEMA_POSTGRES, tableList
    );
  }

  @AfterAll
  public static void cleanUp() throws Exception{
    if(iDatabaseConnection != null){
      iDatabaseConnection.close();
      iDatabaseConnection = null;
    }
  }

  /**
   * 準備データの挿入
   * @param imputXml
   * @throws DatabaseUnitException
   * @throws SQLException
   */
  private void prepareTest(String imputXml) throws DatabaseUnitException, SQLException {
    DbUnitUtil.setUpDatabaseWithCustom(iDatabaseConnection, getClass().getResourceAsStream(imputXml));
  }

	/**
	 * テストのアサーションを実施するメソッド
	 *
	 * @param expectResponse
	 */
	private void testAssertion(InputStream expectResponse, MvcResult result, String outputXml) throws IOException, JSONException, DatabaseUnitException {
		String responseJson = StreamUtils.copyToString(expectResponse, StandardCharsets.UTF_8);
		String actual = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		JSONAssert.assertEquals(responseJson, actual, JSONCompareMode.STRICT);

		ReplacementDataSet expectedDataSet = DbUnitUtil.xmlTableLoader(getClass().getResourceAsStream(outputXml));
		for (String t : tableList){
			ITable actualTable = targetDataSet.getTable(t);
			ITable expectedTable = expectedDataSet.getTable(t);

			ITable filteredActualTable = DbUnitUtil.excludeFilterTable(actualTable, excludedFilter);
			ITable filteredExpectedTable = DbUnitUtil.excludeFilterTable(expectedTable, excludedFilter);

			Assertion.assertEquals(filteredExpectedTable, filteredActualTable);
		}
	}

	@Test
	@DisplayName("【正常系】statusにアクセスした時にDBにもアクセスし、jsonでステータスを返すこと")
	public void returnStatusTest() throws Exception {
		InputStream expectResponse = getClass().getResourceAsStream("【正常系】ステータスのURLを叩いたときに取得データが3件の場合のレスポンス.json");
		String inputXml  = "【準備データ】statusにアクセス_取得データが3件の場合.xml";
		String outputXml = "【結果データ】statusにアクセス_取得データが3件の場合.xml";
		prepareTest(inputXml);

		// execute
		MvcResult result = mvc.perform(get("/status"))
						.andExpect(status().isOk())
						.andReturn();

		// assert
		testAssertion(expectResponse, result, outputXml);
	}

	@Test
	@DisplayName("【正常系】get_allにアクセスした時にDBにあるデータを全て返却すること")
	public void returnGetAllTest() throws Exception {
		InputStream expectResponse = getClass().getResourceAsStream("【正常系】全取得のURLにアクセスしたときの取得データが1件の場合.json");
		String inputXml  = "【準備データ】全取得のURLにアクセス_取得データが1件の場合.xml";
		String outputXml = "【結果データ】全取得のURLにアクセス_取得データが1件の場合.xml";
		prepareTest(inputXml);

		// execute
		MvcResult result = mvc.perform(get("/get_all"))
						.andExpect(status().isOk())
						.andReturn();

		// assert
		testAssertion(expectResponse, result, outputXml);
	}

	@Test
	@Disabled
	// inputbodyをjsonにした場合ってどうするんだっけ？？
	@DisplayName("【正常系】データを1件挿入して、DBに登録できること。")
	public void insertDataTest() throws Exception {
		ObjectMapper mapper= new ObjectMapper();
		String inputBody = mapper.writeValueAsString(getClass().getResourceAsStream("【正常系】DBにデータを1件挿入する場合.json"));
		InputStream expectResponse = getClass().getResourceAsStream("【正常系】全取得のURLにアクセスしたときの取得データが1件の場合.json");
		String inputXml  = "【準備データ】DBにデータを1件挿入する場合.xml";
		String outputXml = "【結果データ】DBにデータを1件挿入する場合.xml";
		prepareTest(inputXml);

		// execute
		MvcResult result = mvc.perform(post("/insert_bookdata")
																	.contentType(MediaType.APPLICATION_JSON)
																	.content(inputBody))
												.andExpect(status().isCreated())
												.andReturn();

		// assert
		testAssertion(expectResponse, result, outputXml);
	}
}
