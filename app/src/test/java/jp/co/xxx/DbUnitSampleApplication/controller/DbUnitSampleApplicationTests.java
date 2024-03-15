package jp.co.xxx.DbUnitSampleApplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import common.util.DbUnitUtil;
import common.util.TableConstant;
import common.util.TestCommonUtil;
import common.util.TestConfiguration;
import jp.co.xxx.DbUnitSampleApplication.entity.Book;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.RegularExpressionValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
  private List<String> tableList = Arrays.asList(TableConstant.BOOK);


	/**
	 *
	 * DBUnitで除外後に、チェックが必要なカラム
	 */
	private final String CREATED = "created";
	private final String UPDATED = "updated";

  /**
   * DBUnit上で除外するためのフィルター項目
   */
  private String[] excludedFilter =
          new String[]{
                  "CREATED",
                  "UPDATED"
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
            iDatabaseConnection, TableConstant.DB, TableConstant.SCHEMA, tableList
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

		// 日時は正規表現で比較
		JSONAssert.assertEquals(responseJson, actual,
						new CustomComparator(JSONCompareMode.STRICT,
										new Customization("current_time", new RegularExpressionValueMatcher<>())
						)
		);

		ReplacementDataSet expectedDataSet = DbUnitUtil.xmlTableLoader(getClass().getResourceAsStream(outputXml));
		for (String t : tableList){
			ITable actualTable = targetDataSet.getTable(t);
			ITable expectedTable = expectedDataSet.getTable(t);

			ITable filteredActualTable = DbUnitUtil.excludeFilterTable(actualTable, excludedFilter);
			ITable filteredExpectedTable = DbUnitUtil.excludeFilterTable(expectedTable, excludedFilter);

			Assertion.assertEquals(filteredExpectedTable, filteredActualTable);
		}
	}

	/**
	 *
	 * @param outputXml
	 * @throws IOException
	 * @throws JSONException
	 * @throws DatabaseUnitException
	 */
	private void testAssertion(String outputXml) throws DatabaseUnitException {
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
		InputStream expectResponse = getClass().getResourceAsStream("【正常系】ステータスのURLを叩いたときのレスポンス.json");
		String inputXml  = "【準備データ】statusにアクセス.xml";
		String outputXml = "【結果データ】statusにアクセス.xml";
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
	@DisplayName("【正常系】データを1件挿入して、DBに登録できること。")
	public void insertDataTest() throws Exception {
		Book testData = new Book();
		testData.setTitle("ゴーストハント");
		testData.setAuthor("小野不由美");

		ObjectMapper mapper= new ObjectMapper();
		// Sequenceのリセット
		DbUnitUtil.resetSeqId(iDatabaseConnection, TableConstant.DB, TableConstant.SCHEMA, TableConstant.BOOK, "id", 2);
		String inputXml  = "【準備データ】DBにデータを1件挿入する場合.xml";
		String outputXml = "【結果データ】DBにデータを1件挿入する場合.xml";
		prepareTest(inputXml);

		// execute
		MvcResult result = mvc.perform(post("/insert_bookdata")
																.contentType(MediaType.APPLICATION_JSON)
																.content(mapper.writeValueAsString(testData)))
											.andExpect(status().isOk())
											.andReturn();

		// assert
		testAssertion(outputXml);

		// 追加されたデータのcreatedとupdatedカラムの中身がnullではないことの確認
		Assertions.assertTrue(DbUnitUtil.isColumnNotNull(targetDataSet, TableConstant.BOOK, CREATED, 1));
		Assertions.assertTrue(DbUnitUtil.isColumnNotNull(targetDataSet, TableConstant.BOOK, UPDATED, 1));
	}

	@Test
	@DisplayName("【正常系】データを1件更新して、データを更新できること。")
	public void updateDataTest() throws Exception {
		Book testData = new Book();
		testData.setId(1);
		testData.setTitle("容疑者Xの献身");
		testData.setAuthor("東野圭吾");

		ObjectMapper mapper= new ObjectMapper();
		// Sequenceのリセット
		DbUnitUtil.resetSeqId(iDatabaseConnection, TableConstant.DB, TableConstant.SCHEMA, TableConstant.BOOK, "id", 2);
		String inputXml  = "【準備データ】DBのデータを1件更新する場合.xml";
		String outputXml = "【結果データ】DBのデータを1件更新する場合.xml";
		prepareTest(inputXml);

		// execute
		MvcResult result = mvc.perform(put("/update_bookdata")
										.contentType(MediaType.APPLICATION_JSON)
										.content(mapper.writeValueAsString(testData)))
						.andExpect(status().isOk())
						.andReturn();

		// assert
		testAssertion(outputXml);

		// 追加されたデータのcreatedとupdatedカラムの中身がnullではないことの確認
		Assertions.assertTrue(DbUnitUtil.isColumnNotNull(targetDataSet, TableConstant.BOOK, CREATED, 0));
		Assertions.assertTrue(DbUnitUtil.isColumnNotNull(targetDataSet, TableConstant.BOOK, UPDATED, 0));
	}

	@Test
	@DisplayName("【正常系】データを1件削除して、データを削除できること。")
	public void deleteDataTest() throws Exception {
		Book testData = new Book();
		testData.setId(1);

		ObjectMapper mapper= new ObjectMapper();
		// Sequenceのリセット
		DbUnitUtil.resetSeqId(iDatabaseConnection, TableConstant.DB, TableConstant.SCHEMA, TableConstant.BOOK, "id", 2);
		String inputXml  = "【準備データ】DBのデータを1件削除する場合.xml";
		String outputXml = "【結果データ】DBのデータを1件削除する場合.xml";
		prepareTest(inputXml);

		// execute
		MvcResult result = mvc.perform(delete("/delete_bookdata/1")
										.contentType(MediaType.APPLICATION_JSON)
										.content(mapper.writeValueAsString(testData)))
						.andExpect(status().isOk())
						.andReturn();

		// assert
		testAssertion(outputXml);

		// 追加されたデータのcreatedとupdatedカラムの中身がnullではないことの確認
		Assertions.assertTrue(DbUnitUtil.isColumnNotNull(targetDataSet, TableConstant.BOOK, CREATED, 0));
		Assertions.assertTrue(DbUnitUtil.isColumnNotNull(targetDataSet, TableConstant.BOOK, UPDATED, 0));
	}
}
