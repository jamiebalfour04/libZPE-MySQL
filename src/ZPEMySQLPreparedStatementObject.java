import java.io.Serial;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import jamiebalfour.zpe.core.ZPERuntimeEnvironment;
import jamiebalfour.zpe.types.ZPEMap;
import jamiebalfour.zpe.core.ZPE;
import jamiebalfour.zpe.core.ZPEObject;
import jamiebalfour.zpe.interfaces.ZPEPropertyWrapper;

public class ZPEMySQLPreparedStatementObject extends ZPEObject {

  @Serial
  private static final long serialVersionUID = 2761046969467723101L;

  PreparedStatement preparedStatement;
  ArrayList<String> placeholders;

  //The connection object that this object is bound to
  ZPEMySQLObject sqlConn = null;

  public ZPEMySQLPreparedStatementObject(ZPERuntimeEnvironment z, ZPEPropertyWrapper parent) {
    super(z, parent, "ZPEMySQLPreparedStatement");
    addNativeMethod("prepare", new prepare_Command());
    addNativeMethod("execute", new execute_Command());
  }



  //This is here so that it can be set when the prepared query object is created
  void prepare(String query) {
    placeholders = new ArrayList<String>();
    StringBuilder output_query = new StringBuilder();
    try {

      //Split on : so that we can replace them with our values
      for (int i = 0; i < query.length(); i++) {
        if(query.charAt(i) == ':') {
          int x = 1;
          StringBuilder word = new StringBuilder(":");
          while(i + x < query.length() && (query.charAt(i + x) != ' ' && query.charAt(i + x) != ';' && query.charAt(i + x) != ':')){
            word.append(query.charAt(i + x));
            x++;

          }

          //We need to store the placeholders then replace them with ?
          placeholders.add(word.toString());
          i = i + x;
          output_query.append("? ");
        } else {
          output_query.append(query.charAt(i));
        }

      }


      preparedStatement = sqlConn.sql.connection.prepareStatement(output_query.toString());
    } catch (Exception e) {
      System.out.println(e.getMessage());
      ZPE.PrintWarning("Statement could not be prepared.");
    }
  }

  //A native method to reprepare a query
  class prepare_Command implements jamiebalfour.zpe.interfaces.ZPEObjectNativeMethod {

    @Override
    public String[] getParameterNames() {
      return new String[]{"query_str"};
    }

    @Override
    public Object MainMethod(HashMap<String, Object> parameters, ZPEObject parent) {

      String query = parameters.get("query_str").toString();


      prepare(query);

      return this;
    }

    @Override
    public int getRequiredPermissionLevel() {
      return 0;
    }

    @Override
    public String getName() {
      return "prepare";
    }

  }


  class execute_Command implements jamiebalfour.zpe.interfaces.ZPEObjectNativeMethod {

    @Override
    public String[] getParameterNames() {
      return new String[]{"values"};
    }

    @Override
    public Object MainMethod(HashMap<String, Object> parameters, ZPEObject parent) {

      try {


        if(!(parameters.get("values") instanceof ZPEMap)) {
          ZPE.PrintError("Not type of ZPEAssociativeArray in prepare query.");
        }

        ZPEMap values = (ZPEMap) parameters.get("values");

        //For every word
        int index = 1;
        for(String word : placeholders) {
          System.out.println(values.get(word));
          preparedStatement.setObject(index, values.get(word));
          index++;
        }

        return sqlConn.sql.executePreparedStatement(preparedStatement);

      } catch (Exception e) {
        System.out.println(e.getMessage());
        return false;
      }
    }

    @Override
    public int getRequiredPermissionLevel() {
      return 0;
    }

    @Override
    public String getName() {
      return "execute";
    }

  }

}