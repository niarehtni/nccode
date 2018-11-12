package nc.jdbc.framework.generator;

import nc.bp.impl.uap.oid.OidBaseAlgorithm;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.uap.oid.OidGenerator;
import nc.jdbc.framework.MockDataSource;





public class SequenceGenerator
  implements IdGenerator
{
  public static long OID_BASE_INITIAL_VAL = 19000000000000L;
  
  String dataSource;
  

  public SequenceGenerator() {}
  
  public SequenceGenerator(String dataSource)
  {
    this.dataSource = dataSource;
  }
  


  public String generate()
  {
    if (MockDataSource.isMockDataBase()) {
      OID_BASE_INITIAL_VAL += 1L;
      return OidBaseAlgorithm.getInstance(String.valueOf(OID_BASE_INITIAL_VAL)).nextOidBase();
    }
    


    String groupNumber = InvocationInfoProxy.getInstance().getGroupNumber();
    if ((groupNumber == null) || (groupNumber.isEmpty())) {
      groupNumber = "0001";
    }
    
    String ds = dataSource;
    if (ds == null) {
      ds = InvocationInfoProxy.getInstance().getUserDataSource();
    }
    
    return OidGenerator.getInstance().nextOid(ds, groupNumber);
  }
  



  public String generate(String pk_corp)
  {
    if (pk_corp == null) {
      throw new IllegalArgumentException("Can't generate primary key with null pk_corp");
    }
    if (MockDataSource.isMockDataBase()) {
      OID_BASE_INITIAL_VAL += 1L;
      return OidBaseAlgorithm.getInstance(String.valueOf(OID_BASE_INITIAL_VAL)).nextOidBase();
    }
    
    if (dataSource == null) {
      return OidGenerator.getInstance().nextOid(pk_corp);
    }
    return OidGenerator.getInstance().nextOid(dataSource, pk_corp);
  }
  



  public String[] generate(String pk_corp, int amount)
  {
    if (pk_corp == null) {
      throw new IllegalArgumentException("Can't generate primary key with null pk_corp");
    }
    String[] newOids = new String[amount];
    if (MockDataSource.isMockDataBase()) {
      for (int i = 0; i < amount; i++) {
        OID_BASE_INITIAL_VAL += 1L;
        newOids[i] = OidBaseAlgorithm.getInstance(String.valueOf(OID_BASE_INITIAL_VAL)).nextOidBase();
      }
      
      return newOids;
    }
    OidGenerator oidGen = OidGenerator.getInstance();
    if (dataSource == null) {
      for (int i = 0; i < amount; i++) {
        newOids[i] = oidGen.nextOid(pk_corp);
      }
    } else {
      for (int i = 0; i < amount; i++) {
        newOids[i] = oidGen.nextOid(dataSource, pk_corp);
      }
    }
    return newOids;
  }
  



  public String[] generate(int amount)
  {
    String[] newOids = new String[amount];
    if (MockDataSource.isMockDataBase()) {
      for (int i = 0; i < amount; i++) {
        OID_BASE_INITIAL_VAL += 1L;
        newOids[i] = OidBaseAlgorithm.getInstance(String.valueOf(OID_BASE_INITIAL_VAL)).nextOidBase();
      }
      
      return newOids;
    }
    
    String groupNumber = InvocationInfoProxy.getInstance().getGroupNumber();
    if ((groupNumber == null) || (groupNumber.isEmpty())) {
      groupNumber = "0001";
    }
    
    String ds = dataSource;
    if (ds == null) {
      ds = InvocationInfoProxy.getInstance().getUserDataSource();
    }
    
    for (int i = 0; i < amount; i++) {
      newOids[i] = OidGenerator.getInstance().nextOid(ds, groupNumber);
    }
    
    return newOids;
  }
}

