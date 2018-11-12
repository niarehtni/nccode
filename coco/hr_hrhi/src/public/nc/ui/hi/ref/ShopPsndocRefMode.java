package nc.ui.hi.ref;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.logging.Logger;
import nc.itf.om.IAOSQueryService;
import nc.vo.om.aos.AOSSQLHelper;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

/**
 * @author changleia
 * 
 *         门店人员model
 * 
 */
public class ShopPsndocRefMode extends PsndocRefModel {

	/*
	 * @Override protected String getEnvWherePart() {//较父类加了一个工作记录的异动类型不能为离职
	 * String envWherePart = null; String pk_org = getPk_org(); String pk_group
	 * = getPk_group();
	 * 
	 * if (("GLOBLE00000000000000".equals(pk_org)) ||
	 * (StringUtils.isBlank(pk_group))) {
	 * 
	 * envWherePart = null; } else if (pk_org.equals(pk_group)) {
	 * 
	 * envWherePart =
	 * " bd_psndoc.pk_psndoc in (select hi_psnjob.pk_psndoc from hi_psnjob  inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg where hi_psnorg.indocflag = 'Y' and trnsevent != '4'  and hi_psnjob.pk_group = '"
	 * + pk_group + "')";
	 * 
	 * } else {
	 * 
	 * envWherePart =
	 * " bd_psndoc.pk_psndoc in (select hi_psnjob.pk_psndoc from hi_psnjob  inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg where hi_psnorg.indocflag = 'Y' and trnsevent != '4' and hi_psnjob.pk_org in ("
	 * + AOSSQLHelper.getChildrenBUInSQLByHROrgPK(getPkhrorg(getPk_org())) +
	 * ") )"; } // envWherePart +=
	 * " bd_psndoc.pk_psndoc in (select PK_PSNDOC from BD_PSNJOB where BD_PSNJOB.PK_DEPT='"
	 * + SessionUtil.getPk_mng_dept() + //
	 * "' and BD_PSNJOB.pk_psndoc in (select PK_PSNDOC from HI_PSNJOB where HI_PSNJOB.LASTFLAG='Y' and HI_PSNJOB.ENDFLAG='N')) "
	 * ;
	 * 
	 * return (envWherePart == null ? "" : new
	 * StringBuilder().append(envWherePart).append(" and ").toString()) +
	 * " hi_psnorg.indocflag = 'Y' and hi_psnjob.pk_org in (select pk_adminorg from org_admin_enable) "
	 * ; }
	 * 
	 * private String getPkhrorg(String pkOrg) { try { OrgVO hrorgVO =
	 * ((IAOSQueryService
	 * )NCLocator.getInstance().lookup(IAOSQueryService.class))
	 * .queryHROrgByOrgPK(getPk_org()); return hrorgVO == null ? pkOrg :
	 * hrorgVO.getPk_org(); } catch (BusinessException e) {
	 * Logger.error(e.getMessage()); } return pkOrg; }
	 */
	@Override
	public void reset(){
		super.reset();
		setTableName(" bd_psndoc inner join hi_psnorg on hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc   inner join (select max( orgrelaid) as orgrelaid,pk_psndoc from hi_psnorg where indocflag='Y' group by pk_psndoc  ) tmp  on hi_psnorg.pk_psndoc = tmp.pk_psndoc and hi_psnorg.orgrelaid = tmp.orgrelaid inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg and hi_psnjob.lastflag = 'Y' and hi_psnjob.ismainjob = 'Y' and trnsevent != 4 left outer join org_orgs on org_orgs.pk_org = hi_psnjob.pk_org  left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post   left outer join bd_psnidtype on bd_psndoc.idtype = bd_psnidtype.pk_identitype ");
	}
}
