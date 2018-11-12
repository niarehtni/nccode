package nc.bs.hrsms.ta.SignReg.ctrl;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrss.pub.BillCoderUtils;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.hrss.ta.timeapply.IQueryOrgOrDeptVid;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.trade.pub.IBillStatus;

public class SignRegAfterDataChange {

	/**
	 * 
	 */
	public static void onAfterDataChange(Dataset ds, Row row){
		
		TBMPsndocVO tbmPsndocVO = ShopTaAppContextUtil.getTBMPsndocVO();
		if(tbmPsndocVO == null){
//			throw new LfwRuntimeException("您还没有设置考勤档案，不能进行新增操作！");
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
	 * 编辑后处理事件
	 * Ares.tank 2018-7-20 15:20:32
	 * ds 
	 * row 
	 * changeRow 变化值的索引
	 */
	public static void onAfterDataPsnChange(Dataset ds, Row row,int changeRow){
		
		TBMPsndocVO tbmPsndocVO = ShopTaAppContextUtil.getTBMPsndocVO();
		String pk_psndoc = (String)row.getValue(ds.nameToIndex("pk_psndoc"));
		try {
			 tbmPsndocVO=NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class)
				.queryByPsndocAndDate(pk_psndoc,(UFLiteralDate)row.getValue(ds.nameToIndex("apply_date")));
			 if(tbmPsndocVO == null){
					throw new BusinessException();
					//CommonUtil.showMessageDialog("当前人员的考勤档案已经结束，只能新增档案结束前的数据！");
					//return;
				}
		} catch (BusinessException e) {
			if((ds.nameToIndex("pk_psndoc")==changeRow)||(ds.nameToIndex("pk_psndoc")==(changeRow-1))){//只在第一次的提示
				CommonUtil.showMessageDialog("该人员的考勤档案已经结束，只能新增档案结束前的数据！");
			}
			
			return;
		}
		
		//String pk_psnjob = tbmPsndocVO.getPk_psnjob();
		//String pk_psnorg = tbmPsndocVO.getPk_psnorg();
		//pk_psndoc = (String)row.getValue(ds.nameToIndex("pk_psndoc"));
		try {
			 tbmPsndocVO=NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class)
				.queryByPsndocAndDate(pk_psndoc,(UFLiteralDate)row.getValue(ds.nameToIndex("apply_date")));
		} catch (BusinessException e) {
			CommonUtil.showMessageDialog("没有找到该人员的考勤档案！");
			return;
		}
		String pk_psnjob = tbmPsndocVO.getPk_psnjob();
		String pk_psnorg = tbmPsndocVO.getPk_psnorg();
		String pk_tbm_group = tbmPsndocVO.getPk_group();
		String pk_tbm_org = tbmPsndocVO.getPk_org();
		
		
		/*// 人员任职主键
		if(!row.getValue(ds.nameToIndex("pk_psnjob")).equals(pk_psnjob)){
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+row.getValue(ds.nameToIndex("pk_psnjob_pk_psndoc_name")));
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+row.getValue(ds.nameToIndex("pk_psnjob_pk_psndoc_code")));
			
		}*/
		row.setValue(ds.nameToIndex("pk_psndoc"), pk_psndoc);
		row.setValue(ds.nameToIndex("pk_psnjob"), pk_psnjob);
		row.setValue(ds.nameToIndex("pk_psnorg"), pk_psnorg);
		// 单据所属集团
		row.setValue(ds.nameToIndex("pk_group"), pk_tbm_group);
		// 单据所属组织
		row.setValue(ds.nameToIndex("pk_org"), pk_tbm_org);
		
		//new TBMPsndocDAO().queryByPsndocAndDate(pk_psndoc, date);
		List<String> list = getVersionIds(pk_psnjob);
		if (list != null && list.size() > 0) {
			// 人员任职所属业务单元的版本id
			row.setValue(ds.nameToIndex("pk_org_v"), list.get(0));
			// 人员任职所属部门的版本pk_dept_v
			row.setValue(ds.nameToIndex("pk_dept_v"), list.get(1));
		}
		String userPK = SessionUtil.getPk_user();
		
		//String billTypeCode = (String)row.getValue(ds.nameToIndex("billTypeCode"));
		// 是否自助单据
		row.setValue(ds.nameToIndex("ishrssbill"), UFBoolean.TRUE);
		//单据状态
		row.setValue(ds.nameToIndex("signstatus"), UFBoolean.TRUE);
		// 单据类型
		//row.setValue(ds.nameToIndex("pk_billtype"), billTypeCode);
		// 申请单编码
		//row.setValue(ds.nameToIndex("bill_code"), BillCoderUtils.getBillCode(pk_tbm_group, pk_tbm_org, billTypeCode));
		// 申请日期
		row.setValue(ds.nameToIndex("apply_date"), new UFLiteralDate());
		// 申请人
		row.setValue(ds.nameToIndex("billmaker"), userPK);
		// 审批状态
		row.setValue(ds.nameToIndex("approve_state"), IBillStatus.FREE);
		// 人员任职主键
		//row.setValue(ds.nameToIndex("pk_psnjob"), pk_psnjob);
		// 人员基本档案主键
		//row.setValue(ds.nameToIndex("pk_psndoc"), SessionUtil.getPk_psndoc());
		// 人员组织关系组件
		//row.setValue(ds.nameToIndex("pk_psnorg"), pk_psnorg);
		// 创建人
		//row.setValue(ds.nameToIndex("creator"), userPK);
		// 创建时间
		//row.setValue(ds.nameToIndex("creationtime"), new UFDateTime());

		
		
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
