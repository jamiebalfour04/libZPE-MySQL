import jamiebalfour.HelperFunctions;
import jamiebalfour.zpe.core.YASSByteCodes;
import jamiebalfour.zpe.core.YASSInteractiveInterpreter;
import jamiebalfour.zpe.core.ZPERuntimeEnvironment;
import jamiebalfour.zpe.core.ZPEStructure;
import jamiebalfour.zpe.interfaces.ZPECustomFunction;
import jamiebalfour.zpe.interfaces.ZPELibrary;

import java.util.HashMap;
import java.util.Map;

public class Plugin implements ZPELibrary {

  @Override
  public Map<String, ZPECustomFunction> getFunctions() {
    HashMap<String, ZPECustomFunction> arr = new HashMap<String, ZPECustomFunction>();
    arr.put("mysql_connect", new MySQLConnect());
    return arr;
  }

  @Override
  public Map<String, Class<? extends ZPEStructure>> getObjects() {
    return null;
  }

  @Override
  public String getName() {
    return "libZPE-MySQL";
  }

  @Override
  public String getVersionInfo() {
    return "1.0";
  }

  public static class MySQLConnect implements jamiebalfour.zpe.interfaces.ZPECustomFunction{

    @Override
    public String getManualEntry() {
      return "Creates a new MySQL connection.";
    }

    @Override
    public String getManualHeader() {
      return "mysql_connect";
    }

    @Override
    public int getMinimumParameters() {
      return 4;
    }

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
    public Object MainMethod(HashMap<String, Object> params, ZPERuntimeEnvironment runtime) {

      String host = params.get("host").toString();
      String db = params.get("database").toString();
      String user = params.get("user").toString();
      String password = params.get("password").toString();

      int port = 8889;
      if(params.containsKey("port")) {
        port = HelperFunctions.StringToInteger(params.get("port").toString());
      }

      ZPEMySQLObject o = new ZPEMySQLObject(runtime, runtime.GetCurrentZPEFunction());
      if(o.connect(host, port, db, user, password)) {
        return o;
      } else {
        return false;
      }
    }

    @Override
    public int getRequiredPermissionLevel() {
      return 3;
    }

    @Override
    public byte getReturnType() {
      return YASSByteCodes.MIXED_TYPE;
    }

  }

}
