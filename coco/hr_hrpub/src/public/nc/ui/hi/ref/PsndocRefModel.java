package nc.ui.hi.ref;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.om.IAOSQueryService;
import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.om.aos.AOSSQLHelper;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import org.apache.commons.lang.StringUtils;


public class PsndocRefModel
  extends AbstractRefModel
{
  public PsndocRefModel()
  {
    reset();
  }
  

  protected String getEnvWherePart()
  {
    String envWherePart = null;
    String pk_org = getPk_org();
    String pk_group = getPk_group();
    
    if (("GLOBLE00000000000000".equals(pk_org)) || (StringUtils.isBlank(pk_group)))
    {

      envWherePart = null;
    }
    else if (pk_org.equals(pk_group))
    {

      envWherePart = " bd_psndoc.pk_psndoc in (select hi_psnjob.pk_psndoc from hi_psnjob  inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg where hi_psnorg.indocflag = 'Y' and hi_psnjob.pk_group = '" + pk_group + "')";


    }
    else
    {

      envWherePart = " bd_psndoc.pk_psndoc in (select hi_psnjob.pk_psndoc from hi_psnjob  inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg where hi_psnorg.indocflag = 'Y' and hi_psnjob.pk_org in (" + AOSSQLHelper.getChildrenBUInSQLByHROrgPK(getPkhrorg(getPk_org())) + ") )";
    }
    

    return (envWherePart == null ? "" : new StringBuilder().append(envWherePart).append(" and ").toString()) + " hi_psnorg.indocflag = 'Y' and hi_psnjob.pk_org in (select pk_adminorg from org_admin_enable) ";
  }
  

  private String getPkhrorg(String pkOrg)
  {
    try
    {
      OrgVO hrorgVO = ((IAOSQueryService)NCLocator.getInstance().lookup(IAOSQueryService.class)).queryHROrgByOrgPK(getPk_org());
      return hrorgVO == null ? pkOrg : hrorgVO.getPk_org();
    }
    catch (BusinessException e)
    {
      Logger.error(e.getMessage()); }
    return pkOrg;
  }
  


  public void reset()
  {
    setRefNodeName("HR¤H?");
    setFieldCode(new String[] { "bd_psndoc.code", SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + " as name", "bd_psnidtype.name as idtypename", "id", SQLHelper.getMultiLangNameColumn("org_orgs.name"), SQLHelper.getMultiLangNameColumn("org_dept.name"), SQLHelper.getMultiLangNameColumn("om_post.postname"), " case when ismainjob = 'Y' then '" + ResHelper.getString("6001pub", "06001pub0041") + "' else '" + ResHelper.getString("6001pub", "06001pub0042") + "' end " });
    








    setFieldName(new String[] { ResHelper.getString("common", "UC000-0000147"), ResHelper.getString("common", "UC000-0001403"), ResHelper.getString("6001ref", "06001ref0007"), ResHelper.getString("6001ref", "06001ref0008"), ResHelper.getString("6001ref", "06001ref0009"), ResHelper.getString("common", "UC000-0004064"), ResHelper.getString("common", "UC000-0001653"), ResHelper.getString("6007psn", "16007psn0005") });
    






    setDefaultFieldCount(2);
    setRefTitle(ResHelper.getString("common", "UC000-0000129"));
    
    setHiddenFieldCode(new String[] { "hi_psnjob.pk_dept", "hi_psnjob.pk_psnjob", "bd_psndoc.pk_psndoc", "hi_psnjob.pk_org", "hi_psnjob.pk_post", "hi_psnjob.pk_job", "hi_psnjob.pk_psncl" });
    






    setTableName(" bd_psndoc inner join hi_psnorg on hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc   inner join (select max( orgrelaid) as orgrelaid,pk_psndoc from hi_psnorg where indocflag='Y' group by pk_psndoc  ) tmp  on hi_psnorg.pk_psndoc = tmp.pk_psndoc and hi_psnorg.orgrelaid = tmp.orgrelaid inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg and hi_psnjob.lastflag = 'Y' and hi_psnjob.ismainjob = 'Y'  left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org  left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post   left outer join bd_psnidtype on bd_psndoc.idtype = bd_psnidtype.pk_identitype ");
    






    setPkFieldCode("bd_psndoc.pk_psndoc");
    


    resetFieldName();
    











    setMutilLangNameRef(false);
  }
}

/* Location:           C:\ncProjects\6.5\COCO_SZ\nchome\home20170910\modules\hrpub\lib\pubhrpub_busishare.jar
 * Qualified Name:     nc.ui.hi.ref.PsndocRefModel
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */