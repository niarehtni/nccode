package nc.impl.org;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.uap.oid.OidGenerator;
import nc.itf.org.IOrgManagerQryService;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.ui.om.hrdept.action.DeptManager;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgManagerVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.trade.sqlutil.IInSqlBatchCallBack;
import nc.vo.trade.sqlutil.InSqlBatchCaller;








public class OrgManagerQryServiceImpl
  implements IOrgManagerQryService
{
  public HashMap<String, OrgManagerVO> queryOrgManagerMapByOrgIDSAndCuserID(String[] pk_orgs, final String cuserid)
    throws BusinessException
  {
    InSqlBatchCaller call = new InSqlBatchCaller(pk_orgs);
    Collection<OrgManagerVO> c = new ArrayList();
    try
    {
      c = (Collection)call.execute(new IInSqlBatchCallBack()
      {
        Collection<OrgManagerVO> results = new ArrayList();
        
        public Object doWithInSql(String inSql)
          throws BusinessException, SQLException
        {
          String condition = "pk_org in " + inSql + " and " + "cuserid" + " = '" + cuserid + "'";
          Collection<OrgManagerVO> each_result = OrgManagerQryServiceImpl.this.getBaseDAO().retrieveByClause(OrgManagerVO.class, condition);
          

          results.addAll(each_result);
          return results;
        }
      });
    } catch (SQLException e) {
      throw new BusinessException(e);
    }
    if ((c != null) && (c.size() > 0)) {
      HashMap<String, OrgManagerVO> orgManagerMap = new HashMap();
      for (Iterator iterator = c.iterator(); iterator.hasNext();) {
        OrgManagerVO orgManagerVO = (OrgManagerVO)iterator.next();
        orgManagerMap.put(orgManagerVO.getPk_org() + orgManagerVO.getCuserid(), orgManagerVO);
      }
      return orgManagerMap;
    }
    
    return null;
  }
  
  public OrgManagerVO[] queryOrgManagerVOSByOrgID(String pk_org)
    throws BusinessException
  {
    String condition = "pk_org = '" + pk_org + "'";
    Collection<OrgManagerVO> c = getBaseDAO().retrieveByClause(OrgManagerVO.class, condition);
    if ((c != null) && (c.size() > 0)) {
      return (OrgManagerVO[])c.toArray(new OrgManagerVO[0]);
    }
    return null;
  }
  

  public OrgManagerVO[] queryOrgManagerVOSByOrgIDAndClause(String pk_org, String sqlwhere)
    throws BusinessException
  {
    String condition = "pk_org = '" + pk_org + "'";
    if (StringUtil.isEmpty(sqlwhere)) {
      condition = condition + " and " + sqlwhere;
    }
    Collection<OrgManagerVO> c = getBaseDAO().retrieveByClause(OrgManagerVO.class, condition);
    if ((c != null) && (c.size() > 0)) {
      return (OrgManagerVO[])c.toArray(new OrgManagerVO[0]);
    }
    return null;
  }
  
  public String[] getOrgPKSWithPrincipalByPsndocPKAndGroupPK(String pk_psndoc, String pk_group)
    throws BusinessException
  {
    if ((StringUtil.isEmpty(pk_psndoc)) || (StringUtil.isEmpty(pk_group))) {
      return null;
    }
    
    String sql = "select distinct pk_org pk_org from " + OrgVO.getDefaultTableName() + " " + "where " + "principal" + " = '" + pk_psndoc + "' " + "and " + "pk_group" + " = '" + pk_group + "' " + "union " + "select distinct " + "pk_dept" + " pk_org from " + DeptVO.getDefaultTableName() + " " + "where " + "principal" + " = '" + pk_psndoc + "' " + "and " + "pk_group" + " = '" + pk_group + "' ";
    






    Collection<String> pk_orgs = new ArrayList();
    
    pk_orgs = (Collection)getBaseDAO().executeQuery(sql, new ResultSetProcessor()
    {
      private static final long serialVersionUID = -3416332658710667901L;
      
      public Object handleResultSet(ResultSet rs)
        throws SQLException
      {
        Collection<String> pk_orgs = new ArrayList();
        while ((rs != null) && (rs.next())) {
          pk_orgs.add(rs.getString(1).trim());
        }
        return pk_orgs;
      }
      

    });
    return (pk_orgs == null) || (pk_orgs.size() == 0) ? null : (String[])pk_orgs.toArray(new String[0]);
  }
  
  public String[] getOrgPKSWithPrincipalByUserPKAndGroupPK(String cuserid, String pk_group)
    throws BusinessException
  {
    if ((StringUtil.isEmpty(cuserid)) || (StringUtil.isEmpty(pk_group))) {
      return null;
    }
    String pk_psndoc = getUserManageQry().queryPsndocByUserid(cuserid);
    
    if (!StringUtil.isEmpty(pk_psndoc)) {
      return getOrgPKSWithPrincipalByPsndocPKAndGroupPK(pk_psndoc, pk_group);
    }
    return null;
  }
  
  public OrgManagerVO queryOrgManagerVOByOrgIDAndCuserID(String pk_org, String cuserid)
    throws BusinessException
  {
    String condition = "pk_org = '" + pk_org + "' and " + "cuserid" + " ='" + cuserid + "'";
    Collection<OrgManagerVO> c = getBaseDAO().retrieveByClause(OrgManagerVO.class, condition);
    if ((c != null) && (c.size() > 0)) {
      return ((OrgManagerVO[])c.toArray(new OrgManagerVO[0]))[0];
    }
    return null;
  }
  
  
  public void insertOrgManagerBySql(String sql) throws DAOException{
	  getBaseDAO().executeUpdate(sql);
  }
  
  
  public void doimportDeptManagerbySQL(String usercode,String deptcode,String orgdept,String deptmanager,String principalflag,String ts){
	  String pk_orgmanager=OidGenerator.getInstance().nextOid();
	  
	  String sql="INSERT INTO org_orgmanager (ts, cuserid , dr , pk_dept , pk_group , pk_org  , pk_psndoc , pk_psnjob , principalflag , viewotherdirector , viewprincipal,pk_orgmanager ) " +
				" VALUES ('"+ts+"'," +
						" (select cuserid from sm_user where user_code='"+usercode+"') , 0 ," +
						" (select pk_dept from org_dept where code='"+deptcode+"') ," +
						" '0001A1100000000011TO' ," +
						" (select pk_org from org_dept where code='"+orgdept+"')  ," +
						" (select pk_psndoc from bd_psndoc where code='"+deptmanager+"') , null ," +
						" '"+principalflag+"' , null , null ,'"+pk_orgmanager.toString()+"')";
	  
	  try {
		getBaseDAO().executeUpdate(sql);
	} catch (DAOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  
  
  public void updateOrgManagerBySql(String sql) throws DAOException{
	  getBaseDAO().executeUpdate(sql);
  }
  
  private IUserManageQuery getUserManageQry() {
    return (IUserManageQuery)NCLocator.getInstance().lookup(IUserManageQuery.class);
  }
  
  private BaseDAO baseDAO = null;
  
  private BaseDAO getBaseDAO() { if (baseDAO == null) {
      baseDAO = new BaseDAO();
    }
    return baseDAO;
  }
}

/* Location:           C:\ncProjects\6.5\COCO-PRO\home201712131929\home\modules\baseapp\META-INF\lib\baseapp_apporgLevel-1.jar
 * Qualified Name:     nc.impl.org.OrgManagerQryServiceImpl
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */