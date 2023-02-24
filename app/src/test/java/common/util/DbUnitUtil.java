package common.util;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DbUnitUtil {
  /**
   * DBUnitでデータベースのセットアップをするメソッド
   */
  public static void setUpDatabaseWithCustom(IDatabaseConnection con, InputStream xmlPathStream) throws DatabaseUnitException, SQLException {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
    DateTimeFormatter dtfc = DateTimeFormatter.ofPattern("yyyyMMdd");
    IDataSet xmlDataSet = new FlatXmlDataSetBuilder().build(xmlPathStream);

    ReplacementDataSet dataSet = new ReplacementDataSet(xmlDataSet);
    dataSet.addReplacementObject("[null]", null);
    dataSet.addReplacementSubstring("[now]", LocalDateTime.now().format(dtf));
    dataSet.addReplacementSubstring("[now_yyyyMMdd]", LocalDateTime.now().format(dtfc));
    dataSet.addReplacementSubstring("[now-1d]", LocalDateTime.now().minusDays(1).format(dtf));

    DatabaseOperation.CLEAN_INSERT.execute(con, dataSet);
  }

  /**
   * XMLからDBUnit用のデータを作成
   * @param xmlPathStream
   * @return
   * @throws DataSetException
   */
  public static ReplacementDataSet xmlTableLoader(InputStream xmlPathStream) throws DataSetException {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00:0");
    DateTimeFormatter dtfc = DateTimeFormatter.ofPattern("yyyyMMdd");
    DateTimeFormatter dtfNow = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    IDataSet xmlDataSet = new FlatXmlDataSetBuilder().build(xmlPathStream);
    ReplacementDataSet replacementDataSet = new ReplacementDataSet(xmlDataSet);
    replacementDataSet.addReplacementObject("[null]", null);
    replacementDataSet.addReplacementSubstring("[now]", LocalDateTime.now().format(dtfNow));

    return replacementDataSet;
  }

  /**
   * DbUnitのテーブルから比較しない項目を除外
   */
  public static ITable excludeFilterTable(ITable iTable, String[] excludeColumns) throws DataSetException {
    return DefaultColumnFilter.excludedColumnsTable(iTable, excludeColumns);
  }

  /**
   * テーブルソート
   * column除外前のものをソート
   */
  public static ITable sortTable(ITable iTable, String[] sort) throws DataSetException {
    if(sort == null){
      return iTable;
    }

    if(iTable.getRowCount() > 0){
      var sortedTable = new SortedTable(iTable, sort);
      sortedTable.setUseComparable(true);
      return sortedTable;
    }else{
      return iTable;
    }
  }

  @Contract("_, _, _ -> new")
  public static @NotNull ITable sortedAndExcludedColumnsTable(ITable iTable, String[] sortColumns, String[] excludeColumns) throws DataSetException {
    return DefaultColumnFilter.excludedColumnsTable(sortTable(iTable, sortColumns), excludeColumns);
  }
}
