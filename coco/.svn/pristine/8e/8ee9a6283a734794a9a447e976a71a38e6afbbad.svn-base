package nc.bp.impl.uap.oid;








public class OidBaseAlgorithm
{
  private static final byte MIN_CODE = 48;
  






  private static final byte MAX_CODE = 90;
  





  private final int CODE_LENGTH = 14;
  

  private byte[] oidBaseCodes = new byte[14];
  










  private void setOidBaseCodes(byte[] oidBaseCodes)
  {
    if (oidBaseCodes.length != 14)
      return;
    System.arraycopy(oidBaseCodes, 0, this.oidBaseCodes, 0, 14);
  }
  




  public static synchronized OidBaseAlgorithm getInstance()
  {
    OidBaseAlgorithm oidBase = new OidBaseAlgorithm();
    return oidBase;
  }
  





  public static OidBaseAlgorithm getInstance(String strOidBase)
  {
    OidBaseAlgorithm oidBase = new OidBaseAlgorithm();
    oidBase.setOidBaseCodes(strOidBase.getBytes());
    
    return oidBase;
  }
  




  public String nextOidBase()
  {
    for (int i = oidBaseCodes.length - 1; i >= 0; i--) {
      byte code = (byte)(oidBaseCodes[i] + 1);
      boolean carryUp = false;
      byte newCode = code;
      if (code > 90) {
        newCode = 48;
        carryUp = true;
      }
      
      if (newCode == 58) {
        newCode = 65;
      }
      oidBaseCodes[i] = newCode;
      
      if (!carryUp) {
        break;
      }
    }
    return new String(oidBaseCodes);
  }
}
