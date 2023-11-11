package common.util;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConfiguration {
  private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";
  private static final String DB_URL = "jdbc:postgresql://0.0.0.0:5432/db_books";
  private static final String USERNAME = "postgres";
  private static final String PASSWORD = "passw0rd";

  public DataSource dataSource(){
    // MySQLとかではいけるが、PostgreSQLは厳しい？
//    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//    dataSourceBuilder.driverClassName(DRIVER_CLASS_NAME);
//    dataSourceBuilder.url(DB_URL);
//    dataSourceBuilder.username(USERNAME);
//    dataSourceBuilder.username(PASSWORD);
//    return dataSourceBuilder.build();

    return new TransactionAwareDataSourceProxy(
            DataSourceBuilder
                    .create()
                    .username(USERNAME)
                    .password(PASSWORD)
                    .url(DB_URL)
                    .driverClassName(DRIVER_CLASS_NAME)
                    .build());
  }

  public IDatabaseConnection connection(DataSource dataSource) throws SQLException, DatabaseUnitException {
    return new CustomDatabaseConnection(dataSource.getConnection(), TableConstant.SCHEMA_POSTGRES);
  }

  public class CustomDatabaseConnection extends DatabaseConnection{
    public CustomDatabaseConnection(Connection connection, String schema) throws DatabaseUnitException {
      super(connection, schema);
    }

    @Override
    public void close() {
      // don't close the connection
    }

    public void lastClose() throws SQLException{
      super.close();
    }
  }
}
