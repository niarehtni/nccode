package nc.bs.hrsms.ta.sss.away.ctrl;

import java.util.List;

import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.itf.hrss.ta.timeapply.IQueryOrgOrDeptVid;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.vo.pub.BusinessException;
import nc.vo.ta.psndoc.TBMPsndocVO;

public class ShopAwayAfterDataChange {

	/**
	 * 
	 */
	public static void onAfterDataChange(Dataset ds, Row row){
		
		TBMPsndocVO tbmPsndocVO = ShopTaAppContextUtil.getTBMPsndocVO();
		if(tbmPsndocVO == null){
//			throw new LfwRuntimeException("该人员还没有设置考勤档案，不能进行新增操作！");
			CommonUtil.showMessageDialog("当前人员的考勤档案已经结束，只能新增档案结束前的数据！");
			return;
		}
		String pk_psnjob = tbmPsndocVO.getPk_psnjob();
		String pk_psnorg = tbmPsndocVO.getPk_psnorg();
		
		// 人员任职主键
		row.setValue(ds.nameToIndex("pk_psnjob"), pk_psnjob);
		// 人员组织关系组件
		row.setValue(ds.nameToIndex("pk_psnorg"), pk_psnorg);
		
		List<String> list = getVersionIds(pk_psnjob);
		if (list != null && list.size() > 0) {
			// 人员任职所属业务单元的版本id
			row.setValue(ds.nameToIndex("pk_org_v"), list.get(0));
			// 人员任职所属部门的版本pk_dept_v
			row.setValue(ds.nameToIndex("pk_dept_v"), list.get(1));
		}
	}
	
	/**
	 * 获得人员任职所属业务单元/部门的版本id
	 * 
	 * @param pk_psnjob
	 * @return
	 */
	private static List<String> getVersionIds(String pk_psnjob) {
		List<String> list = null;
		IQueryOrgOrDeptVid service;
		try {
			service = ServiceLocator.lookup(IQueryOrgOrDeptVid.class);
			list = service.getOrgOrDeptVidByPsnjob(pk_psnjob);
		} catch (HrssException ex) {
			ex.alert();
		} catch (BusinessException ex) {
			new HrssException(ex).deal();
		}
		return list;
	}
}
