import jamiebalfour.zpe.core.ZPE;
import jamiebalfour.zpe.core.ZPEObject;
import jamiebalfour.zpe.core.ZPERuntimeEnvironment;
import jamiebalfour.zpe.interfaces.ZPEPropertyWrapper;

import java.io.Serial;
import java.sql.SQLException;
import java.util.HashMap;

public class ZPEMySQLObject extends ZPEObject {

  @Serial
  private static final long serialVersionUID = 5811011685776858084L;

  MySQLAccess sql;
  ZPEMySQLObject _this = this;

  public ZPEMySQLObject(ZPERuntimeEnvironment z, ZPEPropertyWrapper parent) {
    super(z, parent, "ZPEMySQLConnection");
    System.out.println("MySQL loaded");
    addNativeMethod("connect", new connect_Command());
    addNativeMethod("query", new query_Command());
    addNativeMethod("prepare", new prepare_Command());
    addNativeMethod("get_columns", new get_columns_Command());
    addNativeMethod("get_tables", new get_tables_Command());
    addNativeMethod("query_to_json", new query_to_json_Command());
  }

  public boolean connect(String host, int port, String db, String user, String password) {
    try {
      sql = new MySQLAccess();
    } catch(Exception e) {
      ZPE.Log(e.getMessage());
      System.err.println("Cannot create MySQL connection. Please refer to ZPE log for more information.");
      return false;
    }

    try {
      sql.connect(host, port, db, user, password);
      return true;
    } catch (SQLException e) {
      ZPE.Log(e.getMessage());
      System.err.println("Cannot connect to MySQL database. Please refer to ZPE log for more information.");
    }

    return false;

  }

  class connect_Command implements jamiebalfour.zpe.interfaces.ZPEObjectNativeMethod {


    @Override
    public String[] getParameterNames() {
      String[] params = new String[5];
      params[0] = "host";
      params[1] = "database";
      params[2] = "user";
      params[3] = "password";
      params[4] = "port";

      return params;
    }

    @Override
    public Object MainMethod(HashMap<String, Object> parameters, ZPEObject parent) {
      try {
        String host = parameters.get("host").toString();
        int port = jamiebalfour.HelperFunctions.StringToInteger(parameters.get("port").toString());
        String database = parameters.get("database").toString();
        String username = parameters.get("user").toString();
        String password = parameters.get("password").toString();

        return ((ZPEMySQLObject) parent).connect(host, port, database, username, password);
      } catch (Exception e) {
        return false;
      }
    }

    @Override
    public int getRequiredPermissionLevel() {
      return 0;
    }

    @Override
    public String getName() {
      return "connect";
    }

  }


  class get_tables_Command implements jamiebalfour.zpe.interfaces.ZPEObjectNativeMethod {

    @Override
    public String[] getParameterNames() {
      return new String[]{};
    }

    @Override
    public Object MainMethod(HashMap<String, Object> parameters, ZPEObject parent) {

      try {
        return sql.getTableNames();
      } catch (Exception e) {
        return false;
      }
    }

    @Override
    public int getRequiredPermissionLevel() {
      return 3;
    }

    @Override
    public String getName() {
      return "get_tables";
    }

  }

  class query_to_json_Command implements jamiebalfour.zpe.interfaces.ZPEObjectNativeMethod {

    @Override
    public String[] getParameterNames() {
      return new String[]{"query_str"};
    }

    @Override
    public Object MainMethod(HashMap<String, Object> parameters, ZPEObject parent) {

      try {
        jamiebalfour.zpe.types.ZPEList l = sql.query(parameters.get("query_str").toString());

        jamiebalfour.parsers.json.ZenithJSONParser parser = new jamiebalfour.parsers.json.ZenithJSONParser();

        return parser.jsonEncode(l);
      } catch (Exception e) {
        return false;
      }
    }

    @Override
    public int getRequiredPermissionLevel() {
      return 0;
    }

    @Override
    public String getName() {
      return "query_to_json";
    }

  }

  class get_columns_Command implements jamiebalfour.zpe.interfaces.ZPEObjectNativeMethod {

    @Override
    public String[] getParameterNames() {
      return new String[]{"table"};
    }

    @Override
    public Object MainMethod(HashMap<String, Object> parameters, ZPEObject parent) {

      try {
        return sql.getColumnNames(parameters.get("table").toString());
      } catch (Exception e) {
        return false;
      }
    }

    @Override
    public int getRequiredPermissionLevel() {
      return 0;
    }

    @Override
    public String getName() {
      return "get_columns";
    }

  }

  class query_Command implements jamiebalfour.zpe.interfaces.ZPEObjectNativeMethod {

    @Override
    public String[] getParameterNames() {
      return new String[]{"query_str"};
    }

    @Override
    public Object MainMethod(HashMap<String, Object> parameters, ZPEObject parent) {

      try {
        return sql.query(parameters.get("query_str").toString());
      } catch (Exception e) {
        return false;
      }
    }

    @Override
    public int getRequiredPermissionLevel() {
      return 0;
    }

    @Override
    public String getName() {
      return "query";
    }

  }

  class prepare_Command implements jamiebalfour.zpe.interfaces.ZPEObjectNativeMethod {

    @Override
    public String[] getParameterNames() {
      return new String[]{"query_str"};
    }

    @Override
    public Object MainMethod(HashMap<String, Object> parameters, ZPEObject parent) {

      try {
        ZPEMySQLPreparedStatementObject prep = new ZPEMySQLPreparedStatementObject(getRuntime(), parent);
        prep.sqlConn = _this;

        prep.prepare(parameters.get("query_str").toString());

        return prep;
      } catch (Exception e) {
        return false;
      }
    }

    @Override
    public int getRequiredPermissionLevel() {
      return 0;
    }

    @Override
    public String getName() {
      // TODO Auto-generated method stub
      return "prepare";
    }

  }

}