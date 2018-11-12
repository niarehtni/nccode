package nc.bs.hrsms.ta.common.ctrl;

import nc.bs.dao.DAOException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.hr.utils.CommonUtils;
import nc.uap.lfw.core.log.LfwLogger;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.lang.UFLiteralDate;
/**
 * 查询有考勤档案的人员最新工作信息
 * @author  shaochj
 * @date Sep 8, 2015
 */
public class TBMQueryPsnJobVOUtil {

	/**
	 * 获取有考勤档案的人员工作记录
	 * @return
	 */
	public static PsnJobVO[] getPsnJobs(){
		/* 放开下级部门权限，列出所有当前管理部门及下级部门的人员 */
		HRDeptVO mngDeptVO = SessionUtil.getMngDept();
		PsnJobVO[] psnjobVOs = null;
		try {
			/**
			 * 默认包含下级部门
			 */
			StringBuffer condition = new StringBuffer(); //" lastflag='Y' and ismainjob='Y' and poststat='Y' and endflag='N' ";//1001Q410000000009Y05
			condition.append(" hi_psnjob.pk_psnjob in (");
			condition.append(" select tbm_psndoc.pk_psnjob from tbm_psndoc inner join hi_psnjob on tbm_psndoc.pk_psnjob = hi_psnjob.pk_psnjob ");
			condition.append(" and hi_psnjob.lastflag='Y' and hi_psnjob.ismainjob='Y' and hi_psnjob.poststat='Y' and hi_psnjob.endflag='N' ");
			if(mngDeptVO.getInnercode().trim()!=null){
				condition.append(" and hi_psnjob.pk_dept in(select dept.pk_dept from org_dept dept where dept.innercode like '%"+mngDeptVO.getInnercode()+"%')" +
						" where tbm_psndoc.enddate>='"+new UFLiteralDate()+"')");
			}else{
				condition.append(" and hi_psnjob.pk_dept = '"+mngDeptVO.getPk_dept()+"' where tbm_psndoc.enddate>='"+new UFLiteralDate()+"')");
			}
			psnjobVOs = CommonUtils.retrieveByClause(PsnJobVO.class, condition.toString());
		} catch (DAOException e) {
			LfwLogger.error(e.getMessage(), e);
		}
		return psnjobVOs;
	}
}
