package nc.bs.hrsms.ta.sss.shopleave.ctrl;

import java.util.List;

import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyBaseView;
import nc.bs.hrsms.ta.sss.shopleave.ShopLeaveApplyConsts;
import nc.bs.hrsms.ta.sss.shopleave.prcss.ShopLeaveAddProcessor;
import nc.bs.hrsms.ta.sss.shopleave.prcss.ShopLeaveEditProcessor;
import nc.bs.hrsms.ta.sss.shopleave.prcss.ShopLeaveLineAddProcessor;
import nc.bs.hrsms.ta.sss.shopleave.prcss.ShopLeaveLineDelProcessor;
import nc.bs.hrsms.ta.sss.shopleave.prcss.ShopLeaveSaveAddProcessor;
import nc.bs.hrsms.ta.sss.shopleave.prcss.ShopLeaveSaveProcessor;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.IEditProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineDelProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.hrss.ta.timeapply.IQueryOrgOrDeptVid;
import nc.itf.ta.ITimeItemQueryService;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveConst;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;

import org.apache.commons.lang.StringUtils;

public class ShopLactationApplytCardView extends ShopTaApplyBaseView implements IController{

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	/**
	 * 单据类型
	 * 
	 * @return
	 */
	@Override
	protected String getBillType() {
		return ShopLeaveApplyConsts.BILL_TYPE_CODE;
	}

	/**
	 * 数据集ID
	 * 
	 * @return
	 */
	@Override
	protected String getDatasetId() {
		return ShopLeaveApplyConsts.DS_MAIN_NAME;
	}

	/**
	 * 单据聚合类型VO
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends AggregatedValueObject> getAggVoClazz() {
		return ShopLeaveApplyConsts.CLASS_NAME_AGGVO;
	}

	/**
	 * 修改的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends IEditProcessor> getEditPrcss() {
		return ShopLeaveEditProcessor.class;
	}

	/**
	 * 保存的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSavePrcss() {
		return ShopLeaveSaveProcessor.class;
	}

	/**
	 * 保存并新增的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSaveAddPrcss() {
		return ShopLeaveSaveAddProcessor.class;
	}

	/**
	 * 行新增的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ILineInsertProcessor> getLineAddPrcss() {
		return ShopLeaveLineAddProcessor.class;
	}

	/**
	 * 行删除的PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ILineDelProcessor> getLineDelPrcss() {
		return ShopLeaveLineDelProcessor.class;
	}

	/**
	 * 新增的PROCESSOR
	 */
	@Override
	protected Class<? extends IAddProcessor> getAddPrcss() {
		return ShopLeaveAddProcessor.class;
	}
	
	public void onDataLoad_hrtaleaveh(DataLoadEvent dataLoadEvent) {
		super.onDataLoad(dataLoadEvent);
		
		Dataset ds = dataLoadEvent.getSource();
		Row masterRow = ds.getSelectedRow();
		masterRow.setValue(ds.nameToIndex(LeavehVO.PK_LEAVETYPE), LeaveConst.LEAVETYPE_SUCKLE);
		// 申请单据所属组织
		String pk_org = masterRow.getString(ds.nameToIndex(LeavehVO.PK_ORG));

		// 获得休假类别PK
		String pk_leavetype = masterRow.getString(ds.nameToIndex(LeavehVO.PK_LEAVETYPE));
		if(StringUtils.isEmpty(pk_leavetype)){
			return;
		}
		TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(pk_org, pk_leavetype);
		if(timeItemCopyVO != null){
			masterRow.setValue(ds.nameToIndex(LeavehVO.PK_LEAVETYPECOPY), timeItemCopyVO.getPk_timeitemcopy());
			masterRow.setValue(ds.nameToIndex(LeavehVO.ISLACTATION), UFBoolean.TRUE);
		}else{
			masterRow.setValue(ds.nameToIndex(LeavehVO.PK_LEAVETYPE), null);
			masterRow.setValue(ds.nameToIndex(LeavehVO.ISLACTATION), UFBoolean.TRUE);
		}
	}

	/**
	 * 休假申请子表的值变化事件<br/>
	 * 
	 * @param datasetCellEvent
	 */
	public void onAfterDataChage_hrtaleaveb(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		int colIndex = datasetCellEvent.getColIndex();
		// 开始时间|结束时间
		if (colIndex != ds.nameToIndex(LeavebVO.LEAVEBEGINTIME) && colIndex != ds.nameToIndex(LeavebVO.LEAVEENDTIME)) {
			return;
		}
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			return;
		}
		UFDateTime beiginTime = (UFDateTime) selRow.getValue(ds.nameToIndex(LeavebVO.LEAVEBEGINTIME));
		UFDateTime endTime = (UFDateTime) selRow.getValue(ds.nameToIndex(LeavebVO.LEAVEENDTIME));
		if (colIndex == ds.nameToIndex(LeavebVO.LEAVEBEGINTIME)) {
			if (beiginTime != null) {
				// 设置了开始日期
				selRow.setValue(ds.nameToIndex(LeavebVO.LEAVEBEGINDATE), new UFLiteralDate(beiginTime.getDate().toString()));
			} else {
				// 设置了开始日期
				selRow.setValue(ds.nameToIndex(LeavebVO.LEAVEBEGINDATE), null);
			}
		}
		if (colIndex == ds.nameToIndex(LeavebVO.LEAVEENDTIME)) {
			if (endTime != null) {
				// 设置了结束日期
				selRow.setValue(ds.nameToIndex(LeavebVO.LEAVEENDDATE), new UFLiteralDate(endTime.getDate().toString()));
			} else {
				// 设置了结束日期
				selRow.setValue(ds.nameToIndex(LeavebVO.LEAVEENDDATE), null);
			}
		}
//		LfwView view = getLifeCycleContext().getViewContext().getView();
//		Dataset dsMaster = view.getViewModels().getDataset(ShopLeaveApplyConsts.DS_MAIN_NAME);
//		Row rowMaster = dsMaster.getSelectedRow();
		// 调用后台计算时长
//		calculate(dsMaster, ds, rowMaster);
	}

	/**
	 * 休假申请主表的值变化事件<br/>
	 * 休假类别发生变化影响:1.考勤单位2.期间是否可编辑3.已休时长 |享有时长 |结余时长4.休假总时长5.子表休假时长<br/>
	 * 年度发生变化影响:1.期间的下拉数据集,2.已休时长 |享有时长 |结余时长<br/>
	 * 期间发生变化影响:1.已休时长 |享有时长 |结余时长<br/>
	 * 
	 * @param datasetCellEvent
	 */
	public void onAfterDataChange_hrtaleaveh(DatasetCellEvent datasetCellEvent) {
//		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		int colIndex = datasetCellEvent.getColIndex();
		if (colIndex != ds.nameToIndex(LeavehVO.PK_LEAVETYPE) && colIndex != ds.nameToIndex("pk_psnjob")) {
			return;
		}
		Row selRow = ds.getSelectedRow();
		if(colIndex == ds.nameToIndex("pk_psnjob")){
			String pk_psndoc =  (String) selRow.getValue(ds.nameToIndex("pk_psndoc"));
			// 在applicationContext中添加属性考勤档案和考勤规则
			ShopTaAppContextUtil.addTaAppContext(pk_psndoc);
			TBMPsndocVO tbmPsndocVO = ShopTaAppContextUtil.getTBMPsndocVO();
			if(tbmPsndocVO == null){
//				throw new LfwRuntimeException("您还没有设置考勤档案，不能进行新增操作！");
				CommonUtil.showMessageDialog("当前人员的考勤档案已经结束，只能新增档案结束前的数据！");
				return;
			}
			String pk_psnjob = tbmPsndocVO.getPk_psnjob();
			String pk_psnorg = tbmPsndocVO.getPk_psnorg();
			
			// 人员任职主键
			selRow.setValue(ds.nameToIndex("pk_psnjob"), pk_psnjob);
			// 人员组织关系组件
			selRow.setValue(ds.nameToIndex("pk_psnorg"), pk_psnorg);
			
			List<String> list = getVersionIds(pk_psnjob);
			if (list != null && list.size() > 0) {
				// 人员任职所属业务单元的版本id
				selRow.setValue(ds.nameToIndex("pk_org_v"), list.get(0));
				// 人员任职所属部门的版本pk_dept_v
				selRow.setValue(ds.nameToIndex("pk_dept_v"), list.get(1));
			}
		}else if(colIndex == ds.nameToIndex(LeavehVO.PK_LEAVETYPE)){
			// 申请单据所属组织
			String pk_org = selRow.getString(ds.nameToIndex(LeavehVO.PK_ORG));
			// 获得休假类别PK
			String pk_leavetype = selRow.getString(ds.nameToIndex(LeavehVO.PK_LEAVETYPE));
			TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(pk_org, pk_leavetype);
			selRow.setValue(ds.nameToIndex(LeavehVO.PK_LEAVETYPECOPY), timeItemCopyVO.getPk_timeitemcopy());
		}
	}

	
	
	

	/**
	 * 根据休假类别PK和组织PK, 获得休假类别copy的PK
	 * 
	 * @param pk_org
	 * @param pk_leavetype
	 * @return
	 */
	public static TimeItemCopyVO getTimeItemCopyVO(String pk_org, String pk_leavetype) {
		TimeItemCopyVO timeItemCopyVO = null;
		// 查询休假类别copy的PK
		try {
			ITimeItemQueryService service = ServiceLocator.lookup(ITimeItemQueryService.class);
			timeItemCopyVO = service.queryCopyTypesByDefPK(pk_org, pk_leavetype, TimeItemVO.LEAVE_TYPE);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return timeItemCopyVO;
	}

	
	@Override
	protected String getDetailDsId() {
		return ShopLeaveApplyConsts.DS_SUB_NAME;
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
