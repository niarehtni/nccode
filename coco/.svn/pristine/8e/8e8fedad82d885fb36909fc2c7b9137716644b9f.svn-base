package nc.bs.uap.oid;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import nc.bp.impl.uap.oid.OidBaseAlgorithm;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.ds.DataSourceMetaMgr;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Logger;
import nc.itf.uap.IOidGeneratorBS;














public class OidGenerator
{
  private static String INTERN_FIXER = "AA";
  
  private static DataSourceMetaMgr dsMgr = DataSourceMetaMgr.getInstance();
  
  public int amount=0;

  private Map<String, Lock> locks = new HashMap();
  


  private OidGenerator()
  {
    try
    {
      lock = new Object();
      oidGeneratorBS = ((IOidGeneratorBS)NCLocator.getInstance().lookup(IOidGeneratorBS.class.getName()));
    }
    catch (ComponentException e)
    {
      oidGeneratorBS = null;
      Logger.error("Cannot initialize OidGenerator.", e);
    }
  }
  




  private static Map oidMap = new ConcurrentHashMap();
  

  private static OidGenerator instance = new OidGenerator();
  public static final int OID_AMOUNT = 1000;
  public static final String OID_BASE_INITIAL_VAL = "10000000000000";
  
  public static OidGenerator getInstance() {
    return instance;
  }
  




  public final String nextOid()
  {
    return nextOid("0001");
  }
  




  public final String[] nextOid(String groupNumber, int count)
  {
    String[] oids = new String[count];
    for (int i = 0; i < count; i++) {
      String oid = nextOid(groupNumber);
      oids[i] = oid;
    }
    return oids;
  }
  
  public final String[] nextOid(String dataSource, String groupNumber, int count)
  {
    String[] oids = new String[count];
    for (int i = 0; i < count; i++) {
      String oid = nextOid(dataSource, groupNumber);
      oids[i] = oid;
    }
    return oids;
  }
  
  public String nextOid(String dataSource, String groupNumber) {
    String ds = dataSource;
    

    if (dataSource == null) {
      ds = getDataSource();
    }
    OidCounter oidCounter = null;
    String oidBase = null;
    


    String oidMark = dsMgr.getOIDMark(ds);
    if ((oidMark == null) || (oidMark.length() != 2)) {
      oidMark = INTERN_FIXER;
    }
    
    Lock l = getLock(ds, groupNumber);
    
    l.lock();
    String nextOid;
    try { String key = groupNumber + ds;
      oidCounter = (OidCounter)oidMap.get(key);
      if (oidCounter == null) {
        oidCounter = new OidCounter();
        oidMap.put(key, oidCounter);
      }
      
//      if ((amount % 1000 == 0) || (0 == amount))
//      {
        oidBase = oidGeneratorBS.createOidBase_RequiresNew(ds, groupNumber);
//      }
//      else
//      {
//        oidBase = oidBase;
//      }
      

      nextOid = OidBaseAlgorithm.getInstance(oidBase).nextOidBase();
      
      oidBase = nextOid;
      amount += 1;
    } finally {
      l.unlock();
    }
    return getWholeOid(groupNumber, oidMark, nextOid);
  }
  

  public static final String GROUP_PK_CORP = "0001";
  
  private Object lock;
  
  protected IOidGeneratorBS oidGeneratorBS;
  public String nextOid(String groupNumber)
  {
    String ds = getDataSource();
    return nextOid(ds, groupNumber);
  }
  
  private String getDataSource() {
    return InvocationInfoProxy.getInstance().getUserDataSource();
  }
  




  private final String getWholeOid(String groupNumber, String oidMark, String oidBase)
  {
    return groupNumber + oidMark + oidBase;
  }
  

  public class OidCounter
  {
    public String oidBase;
    
    public int amount;
    
    public OidCounter()
    {
      amount = 0;
      oidBase = "10000000000000";
    }
  }
  
  private Lock getLock(String ds, String groupNumber) {
    String lockKey = "" + ds + ":" + groupNumber;
    Lock l = (Lock)locks.get(lockKey);
    if (l == null) {
      synchronized (lock) {
        l = (Lock)locks.get(lockKey);
        if (l == null) {
          l = new ReentrantLock();
          locks.put(lockKey, l);
        }
      }
    }
    
    return l;
  }
}
