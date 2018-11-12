package nc.itf.org;

import java.util.HashMap;

import nc.ui.om.hrdept.action.DeptManager;
import nc.vo.org.OrgManagerVO;
import nc.vo.pub.BusinessException;

public abstract interface IOrgManagerQryService
{
  public abstract HashMap<String, OrgManagerVO> queryOrgManagerMapByOrgIDSAndCuserID(String[] paramArrayOfString, String paramString)
    throws BusinessException;
  
  public abstract OrgManagerVO queryOrgManagerVOByOrgIDAndCuserID(String paramString1, String paramString2)
    throws BusinessException;
  
  public abstract OrgManagerVO[] queryOrgManagerVOSByOrgID(String paramString)
    throws BusinessException;
  
  public abstract OrgManagerVO[] queryOrgManagerVOSByOrgIDAndClause(String paramString1, String paramString2)
    throws BusinessException;
  
  public abstract String[] getOrgPKSWithPrincipalByPsndocPKAndGroupPK(String paramString1, String paramString2)
    throws BusinessException;
  
  public abstract String[] getOrgPKSWithPrincipalByUserPKAndGroupPK(String paramString1, String paramString2)
    throws BusinessException;
  
  public abstract void insertOrgManagerBySql(String sql)
	throws BusinessException;
  
//  public abstract void doimportDeptManagerbySQL(DeptManager deptManager)
//	throws BusinessException;

  public abstract void doimportDeptManagerbySQL(String usercode, String deptcode,
		String orgdept, String deptmanager, String principalflag, String ts);
  

}

/* Location:           C:\ncProjects\6.5\COCO-PRO\home201712131929\home\modules\baseapp\lib\pubbaseapp_apporgLevel-1.jar
 * Qualified Name:     nc.itf.org.IOrgManagerQryService
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */