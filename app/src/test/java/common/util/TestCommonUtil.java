package common.util;

import org.dbunit.database.IDatabaseConnection;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class TestCommonUtil {
  /**
   * データをリセットする
   */
  public static void clearTableRecords(IDatabaseConnection con, String schemaName, List<String> tableList) throws SQLException {
    Statement statement = con.getConnection().createStatement();
    for(String t : tableList){
      int count = statement.executeUpdate("delete from " + schemaName + "." + t);
      System.out.println(t + "を「" + count + "」件削除");
    }
  }
}
