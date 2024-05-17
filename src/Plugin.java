import jamiebalfour.HelperFunctions;
import jamiebalfour.zpe.core.YASSByteCodes;
import jamiebalfour.zpe.core.YASSInteractiveInterpreter;
import jamiebalfour.zpe.core.ZPERuntimeEnvironment;
import jamiebalfour.zpe.interfaces.ZPECustomFunction;
import jamiebalfour.zpe.interfaces.ZPELibrary;

import java.util.HashMap;
import java.util.Map;

public class Plugin implements ZPELibrary {

  @Override
  public ZPECustomFunction[] functions() {

    ZPECustomFunction[] arr = new ZPECustomFunction[1];
    arr[0] = new MySQLConnect();
    return arr;
  }

  public class MySQLConnect implements jamiebalfour.zpe.interfaces.ZPECustomFunction{

    @Override
    public String CommandString() {
      return "mysql_connect";
    }

    @Override
    public String ConvertToLanguage(String arg0, String arg1) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String ImportLines(String arg0) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String ManualEntry() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String ManualHeader() {
      return "mysql_connect";
    }

    @Override
    public int MinimumParameters() {
      return 4;
    }

    @Override
    public String[] ParameterNames() {
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
    public int RequiredPermissionLevel() {
      return 3;
    }

    @Override
    public byte ReturnType() {
      return YASSByteCodes.MIXED_TYPE;
    }

  }

  @Override
  public Map<String, Class<?>> objects() {
    return null;
  }

  @Override
  public String GetName() {
    return "LibPrint";
  }

  @Override
  public String GetVersionInfo() {
    return "1.0";
  }
}
