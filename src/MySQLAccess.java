import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import jamiebalfour.zpe.types.ZPEMap;
import jamiebalfour.zpe.types.ZPEList;

public class MySQLAccess {
  Connection connection = null;

  private ResultSet resultSet = null;

  public MySQLAccess() throws ClassNotFoundException {
    // This will load the MySQL driver, each DB has its own driver
    Class.forName("com.mysql.jdbc.Driver");
  }

  public void connect(String host, String database, String user, String password) throws SQLException {
    // Setup the connection with the DB
    connection = DriverManager.getConnection("jdbc:mysql://"+host+"/"+database+"?user="+user+"&password="+password);
  }

  public void connect(String host, int port, String database, String user, String password) throws SQLException {
    // Setup the connection with the DB
    connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?user="+user+"&password="+password);
  }

  public ZPEList query(String q) throws SQLException {
    Statement statement = connection.createStatement();
    resultSet = statement.executeQuery(q);

    ZPEList o = prepareResults(resultSet);
    statement.close();

    return o;
  }


  public void readDataBase() throws Exception {
        /*try {



            // Statements allow to issue SQL queries to the database
        	Statement statement = connection.createStatement();

            // Result set get the result of the SQL query
            resultSet = statement.executeQuery("select * from feedback.comments");


            // PreparedStatements can use variables and are more efficient
            preparedStatement = connection
                    .prepareStatement("insert into  feedback.comments values (default, ?, ?, ?, ? , ?, ?)");
            // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
            // Parameters start with 1
            preparedStatement.setString(1, "Test");
            preparedStatement.setString(2, "TestEmail");
            preparedStatement.setString(3, "TestWebpage");
            preparedStatement.setLong(4, 1000);
            preparedStatement.setString(5, "TestSummary");
            preparedStatement.setString(6, "TestComment");
            preparedStatement.executeUpdate();

            preparedStatement = connection
                    .prepareStatement("SELECT myuser, webpage, date, summary, comment from feedback.comments");
            resultSet = preparedStatement.executeQuery();


            // Remove again the insert comment
            preparedStatement = connection
            .prepareStatement("delete from feedback.comments where myuser= ? ; ");
            preparedStatement.setString(1, "Test");
            preparedStatement.executeUpdate();

            resultSet = statement
            .executeQuery("select * from feedback.comments");
            writeMetaData(resultSet);

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }*/

  }

  public ZPEList getTableNames() throws SQLException {
    ZPEList names = new ZPEList();

    String query = "SHOW TABLES";

    Statement statement = connection.createStatement();
    resultSet = statement.executeQuery(query);

    while (resultSet.next()) {
      names.add(resultSet.getObject(1).toString());
    }

    return names;
  }

  public ZPEList getColumnNames(String table) throws SQLException {
    ZPEList names = new ZPEList();

    String query = "SELECT * FROM " + table + " LIMIT 0, 1";

    Statement statement = connection.createStatement();
    resultSet = statement.executeQuery(query);

    ResultSetMetaData rsmd = (ResultSetMetaData) resultSet.getMetaData();

    for(int i = 0; i < rsmd.getColumnCount(); i++) {
      names.add(rsmd.getColumnName(i + 1));
    }

    return names;
  }

    /*private void writeMetaData(ResultSet resultSet) throws SQLException {
        //  Now get some metadata from the database
        // Result set get the result of the SQL query

        System.out.println("The columns in the table are: ");

        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
            System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
        }
    }*/

  public ZPEList executePreparedStatement(PreparedStatement preparedStatement) throws SQLException {
    preparedStatement.execute();

    return prepareResults(preparedStatement.getResultSet());
  }

  private ZPEList prepareResults(ResultSet resultSet) throws SQLException {
    ZPEList out = new ZPEList();

    ResultSetMetaData rsmd = (ResultSetMetaData) resultSet.getMetaData();
    while (resultSet.next()) {
      ZPEMap row = new ZPEMap();
      for(int i = 0; i < rsmd.getColumnCount(); i++) {
        String name = rsmd.getColumnName(i + 1);
        row.put(name, resultSet.getObject(i + 1));
      }
      out.add(row);
    }

    return out;
  }

  // You need to close the resultSet
    /*private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {

        }
    }*/
}