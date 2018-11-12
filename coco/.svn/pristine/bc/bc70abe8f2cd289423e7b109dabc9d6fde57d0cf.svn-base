package nc.ui.hi.ref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;


/**
 * 搜索自己管理门店下的人员
 * @author Ares
 * 2018-6-25 09:54:17
 */
public class PsndocRefORGModel
  extends AbstractRefModel
{
	
  public PsndocRefORGModel()
  {
    reset();
  }

  public void filterValueChanged(ValueChangedEvent changedValue)
  {
    super.filterValueChanged(changedValue);
    
    super.filterValueChanged(changedValue);
    String pk_group = "";
    Object newValue = changedValue.getNewValue();
    
    if (newValue == null)
    {
      return;
    }
    
    if (newValue.getClass().isArray())
    {
      pk_group = (String)((Object[])(Object[])newValue)[0];
    }
    else
    {
      pk_group = (String)newValue;
    }
    setPk_group(pk_group);
  }
  
  protected String getEnvWherePart()
  {
    String envWherePart = null;
    String pk_group = getPk_group();
    String sql ="select bd_psndoc.pk_psndoc "
    		+"from bd_psndoc "
    		+"inner join hi_psnorg on hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc "
    		+"inner join ( select max ( orgrelaid ) as orgrelaid , pk_psndoc from hi_psnorg where indocflag = 'Y' group by pk_psndoc ) tmp on hi_psnorg.pk_psndoc = tmp.pk_psndoc and hi_psnorg.orgrelaid = tmp.orgrelaid " 
    		+"inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg and hi_psnjob.lastflag = 'Y' and hi_psnjob.ismainjob = 'Y' "
    		+"left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org "
    		+"left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept "
    		+"left outer join om_post on om_post.pk_post = hi_psnjob.pk_post where ( hi_psnorg.indocflag = 'Y' and hi_psnjob.pk_org in ( select pk_adminorg from org_admin_enable ) ) and "
    		+"( hi_psnjob.pk_group = '"+pk_group+"' "
    			+"and hi_psnjob.pk_dept in(select org_dept.pk_dept from sm_user INNER JOIN org_dept on org_dept.principal=sm_user.pk_psndoc " 
    			+"where cuserid='"+InvocationInfoProxy.getInstance().getUserId()+"') "
    			+"and hi_psnjob.pk_job <> '~' "
    			+"and hi_psnjob.pk_job is not NULL "
    			+") order by bd_psndoc.code";
    //搜索门店下人员的pk_psndoc
    IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName()); 
    List<Map<String,String>> result = null;
    try {
    	
    	 result = (ArrayList)iUAPQueryBS. executeQuery(sql,new MapListProcessor());
	} catch (BusinessException e) {
		// TODO 自动生成的 catch 块
		e.printStackTrace();
	}
    StringBuilder resultsql = new StringBuilder();
    resultsql.append("(");
    if(result!=null){
    	for(Map<String,String> map:result){
    		resultsql.append("'");
    		resultsql.append(map.get(new PsnJobVO().PK_PSNDOC));
    		resultsql.append("'");
    		resultsql.append(",");
    	}
    }
    resultsql.setLength(resultsql.length()-1);
    resultsql.append(")");
      envWherePart = "hi_psnjob.pk_group = '" + pk_group + "'" 
    		  +" AND  hi_psnjob.pk_psndoc in "+resultsql;
    
    return envWherePart;
  }
  

  public void reset()
  {
    setRefNodeName("HR人员(门店)");
    setFieldCode(new String[] { "bd_psndoc.code", HiSQLHelper.getLangNameColume("bd_psndoc.name") + " as name", HiSQLHelper.getLangNameColume("org_orgs.name"), HiSQLHelper.getLangNameColume("org_dept.name"), HiSQLHelper.getLangNameColume("om_post.postname") });
    

    setFieldName(new String[] { ResHelper.getString("common", "UC000-0000147"), ResHelper.getString("common", "UC000-0001403"), ResHelper.getString("6007psn", "06007psn0074"), ResHelper.getString("common", "UC000-0004064"), ResHelper.getString("common", "UC000-0001653") });
    










    setDefaultFieldCount(2);
    setRefTitle(ResHelper.getString("common", "UC000-0000129"));
    setHiddenFieldCode(new String[] { "hi_psnjob.pk_dept", "hi_psnjob.pk_psnjob", "bd_psndoc.pk_psndoc", "hi_psnjob.pk_org", "hi_psnjob.pk_post", "hi_psnjob.pk_job", "hi_psnjob.pk_psncl", "idtype", "id" });
    
    setTableName(" bd_psndoc inner join hi_psnorg on hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc   inner join (select max( orgrelaid) as orgrelaid,pk_psndoc from hi_psnorg where indocflag='Y' group by pk_psndoc  ) tmp  on hi_psnorg.pk_psndoc = tmp.pk_psndoc and hi_psnorg.orgrelaid = tmp.orgrelaid inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg and hi_psnjob.lastflag = 'Y' and hi_psnjob.ismainjob = 'Y'  left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org  left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post  ");
    











    setPkFieldCode("bd_psndoc.pk_psndoc");
    

    setWherePart(" hi_psnorg.indocflag = 'Y' and hi_psnjob.pk_org in (select pk_adminorg from org_admin_enable) ");
    setFilterRefNodeName(new String[] { "集团" });
    resetFieldName();
    Hashtable content = new Hashtable();
    content.put("0", ResHelper.getString("6001ref", "06001ref0004"));
    content.put("1", ResHelper.getString("6001ref", "06001ref0005"));
    content.put("2", ResHelper.getString("6001ref", "06001ref0006"));
    content.put("3", ResHelper.getString("10140psn", "2psndoc-000026"));
    content.put("4", ResHelper.getString("10140psn", "2psndoc-000027"));
    content.put("5", ResHelper.getString("10140psn", "2psndoc-000028"));
    content.put("6", ResHelper.getString("10140psn", "2psndoc-000029"));
    content.put("7", ResHelper.getString("10140psn", "2psndoc-000030"));
    content.put("8", ResHelper.getString("10140psn", "2psndoc-000031"));
    Hashtable convert = new Hashtable();
    convert.put("idtype", content);
    setDispConvertor(convert);
    setMutilLangNameRef(false);
  }
}

/* Location:           C:\yonyou\Projects\coco\environment\home\modules\hrhi\lib\pubhrhi_personnelmgt.jar
 * Qualified Name:     nc.ui.hi.ref.PsndocGlobalRefModel
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.0.1
 */