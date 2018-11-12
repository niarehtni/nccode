package nc.bs.hrsms.ta.empleavereg4store.batchAdd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.common.ctrl.TBMQueryPsnJobVOUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrsms.ta.sss.overtime.ctrl.ShopDefaultTimeScope;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.away.lsnr.AwaySaveProcessor;
import nc.hr.utils.ResHelper;
import nc.itf.hrss.ta.timeapply.IQueryOrgOrDeptVid;
import nc.itf.ta.ILeaveRegisterInfoDisplayer;
import nc.itf.ta.ILeaveRegisterManageMaintain;
import nc.itf.ta.ILeaveRegisterQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;

import org.apache.commons.lang.ArrayUtils;

public class BatchAddViewMain implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	/**
	 * beforeShow事件
	 * 
	 * @param dialogEvent
	 */
	public void onBeforeShow(DialogEvent dialogEvent) {
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(), "ds_leavereg");
		Row row = ds.getEmptyRow();
		// 设置默认单据来源
		row.setValue(ds.nameToIndex(LeaveRegVO.BILLSOURCE), 2);
		row.setValue(ds.nameToIndex(LeaveRegVO.PK_ORG), SessionUtil.getPk_org());// 集团
		row.setValue(ds.nameToIndex(LeaveRegVO.PK_GROUP), SessionUtil.getPk_group());// 组织
		ITimeScope defaultScope = ShopDefaultTimeScope.getDefaultTimeScope(SessionUtil.getPk_org(), null, null,
				TimeZone.getDefault());
		row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEBEGINTIME), defaultScope.getScope_start_datetime());
		row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEENDTIME), defaultScope.getScope_end_datetime());

		ds.setCurrentKey(Dataset.MASTER_KEY);
		ds.addRow(row);
		ds.setRowSelectIndex(0);
		ds.setEnabled(true);

	}

	/**
	 * 从卡片界面回到列表界面的查询操作
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys) {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 人员数据集加载事件
	 * 
	 * @param dataLoadEvent
	 */
	public void onDatasetLoad_dsPerson(DataLoadEvent dataLoadEvent) {
		/* 放开下级部门权限，列出所有当前管理部门及下级部门的人员 */
		PsnJobVO[] psnjobVOs = TBMQueryPsnJobVOUtil.getPsnJobs();

		if (null == psnjobVOs || psnjobVOs.length == 0) {
			return;
		}
		Dataset dsPsn = ViewUtil.getDataset(ViewUtil.getCurrentView(), "dsPerson");
		if (!isPagination(dsPsn)) {
			DatasetUtil.clearData(dsPsn);
			dsPsn.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		}
		dsPsn.setVoMeta(PsnJobVO.class.getName());
		SuperVO[] vos = DatasetUtil.paginationMethod(dsPsn, psnjobVOs);// psnJobList.toArray(new
																		// PsnJobVO[0]));
		new SuperVO2DatasetSerializer().serialize(vos, dsPsn, Row.STATE_NORMAL);
	}

	/**
	 * 
	 * 休假类别调整
	 * 
	 * @param dataLoadEvent
	 */
	public void onAfterDataChage_ds_leavereg(DatasetCellEvent datasetCellEvent) {

	}

	/**
	 * 分页操作标志
	 * 
	 * @param ds
	 * @return
	 */
	private boolean isPagination(Dataset ds) {
		PaginationInfo pg = ds.getCurrentRowSet().getPaginationInfo();
		return pg.getRecordsCount() > 0;
	}

	/**
	 * 确定
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onSave(MouseEvent mouseEvent) {
		LfwView wdtMain = ViewUtil.getCurrentView();
		/* 获取更改班别条件信息 */
		Dataset dsClass = wdtMain.getViewModels().getDataset("ds_leavereg");
		HashMap<String, Object> value = DatasetUtil.getValueMap(dsClass);
		UFDateTime begin = (UFDateTime) value.get(LeaveRegVO.LEAVEBEGINTIME);
		UFDateTime end = (UFDateTime) value.get(LeaveRegVO.LEAVEENDTIME);
		// String description = (String) value.get(LeaveRegVO.LEAVEREMARK);
		// UFBoolean byOriClass = (UFBoolean)
		// value.get(BatchChangeShiftPageModel.FLD_BY_ORI_CLASS);
		// String oriClass = (String)
		// value.get(BatchChangeShiftPageModel.FLD_ORI_CLASS);
		// String newClass = (String)
		// value.get(BatchChangeShiftPageModel.FLD_NEW_CLASS);
		// 校验
		if (begin == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0169"),
					ResHelper.getString("c_ta-res", "0c_ta-res0017")/*
																	 * @ res
																	 * "开始日期不能为空！"
																	 */);
		}
		if (end == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0169"), nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0018")/*
																			 * @
																			 * res
																			 * "结束日期不能为空！"
																			 */);
		}
		if (begin.after(end)) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0169"), nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0019")/*
																			 * @
																			 * res
																			 * "开始日期不能晚于结束日期！"
																			 */);
		}
		Dataset dsPsn = wdtMain.getViewModels().getDataset("dsPerson");

		// 选中的人员
		Row[] psnRows = dsPsn.getSelectedRows();
		// 人员ID List
		// List<String> pk_psndocs = new ArrayList<String>();
		if (ArrayUtils.isEmpty(psnRows)) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0169"), nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("c_ta-res", "休假人员不允许为空")/*
																		 * @ res
																		 * "调班人员和部门不能都为空！"
																		 */);
		}

		LeaveRegVO leaveRegVO = new Dataset2SuperVOSerializer<LeaveRegVO>().serialize(dsClass)[0];
		TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(leaveRegVO.getPk_org(), leaveRegVO.getPk_leavetype());
		if (timeItemCopyVO != null) {
			// 设置休假类别copy的PK
			leaveRegVO.setPk_leavetypecopy(timeItemCopyVO.getPk_timeitemcopy());
		} else {
			// 设置休假类别copy的PK
			leaveRegVO.setPk_leavetypecopy(null);
		}
		// List<LeaveRegVO> lrvList = new ArrayList<LeaveRegVO>();
		LeaveRegVO[] vos = new LeaveRegVO[psnRows.length];
		ILeaveRegisterInfoDisplayer displayer = NCLocator.getInstance().lookup(ILeaveRegisterInfoDisplayer.class);
		// 选中的人员
		try {
			if (!ArrayUtils.isEmpty(psnRows)) {
				for (int i = 0; i < psnRows.length; i++) {
					LeaveRegVO lvo = new LeaveRegVO();
					lvo.setBillsource(leaveRegVO.getBillsource());
					lvo.setScope_start_datetime(leaveRegVO.getScope_start_datetime());
					lvo.setScope_end_datetime(leaveRegVO.getScope_end_datetime());
					lvo.setPk_group(leaveRegVO.getPk_group());
					lvo.setPk_timeitem(leaveRegVO.getPk_timeitem());
					lvo.setLeavebegintime(leaveRegVO.getLeavebegintime());
					lvo.setLeaveendtime(leaveRegVO.getLeaveendtime());
					lvo.setPk_org(leaveRegVO.getPk_org());
					lvo.setIslactation(leaveRegVO.getIslactation());
					lvo.setIsleaveoff(leaveRegVO.getIsleaveoff());
					lvo.setLeaveindex(leaveRegVO.getLeaveindex());
					lvo.setDr(leaveRegVO.getDr());
					lvo.setPk_leavetype(leaveRegVO.getPk_leavetype());
					lvo.setLeaveremark(leaveRegVO.getLeaveremark());
					lvo.setPk_psndoc((String) psnRows[i].getValue(dsPsn.nameToIndex(PsnJobVO.PK_PSNDOC)));
					lvo.setPk_psnjob((String) psnRows[i].getValue(dsPsn.nameToIndex(PsnJobVO.PK_PSNJOB)));
					ShopTaAppContextUtil.addTaAppContext((String) psnRows[i].getValue(dsPsn
							.nameToIndex(PsnJobVO.PK_PSNDOC)));
					TBMPsndocVO tbmPsndocVO = ShopTaAppContextUtil.getTBMPsndocVO();
					lvo.setPk_psnorg(tbmPsndocVO.getPk_psnorg());
					// lvo.setLeavehour(new UFDouble("1.00"));
					lvo.setLeaveyear(String.valueOf(new UFLiteralDate()));
					lvo.setPk_leavetypecopy(leaveRegVO.getPk_leavetypecopy());
					lvo = displayer.calculate(lvo, TimeZone.getDefault());
					List<String> list = getVersionIds((String) psnRows[i].getValue(dsPsn
							.nameToIndex(PsnJobVO.PK_PSNJOB)));
					if (list != null && list.size() > 0) {
						// 人员任职所属业务单元的版本id
						lvo.setPk_org_v(list.get(0));
						// 人员任职所属部门的版本pk_dept_v
						lvo.setPk_dept_v(list.get(1));
					}
					vos[i] = lvo;
				}
			}
			ILeaveRegisterManageMaintain service = NCLocator.getInstance().lookup(ILeaveRegisterManageMaintain.class);
			// 保存前先校验一次，如果有单据冲突，但是是被允许的，则显示这些冲突单据，并询问用户是否保存
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkMutextResult = null;
			try {
				checkMutextResult = ServiceLocator.lookup(ILeaveRegisterQueryMaintain.class).check(
						SessionUtil.getPk_org(), vos);
				if (checkMutextResult != null) {
					AwaySaveProcessor.showConflictInfoList(new BillMutexException(null, checkMutextResult),
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0008")/*
																											 * @
																											 * res
																											 * "与下列单据有时间冲突，是否保存?"
																											 */,
							ShopTaApplyConsts.DIALOG_CONFIRM);
					return;
				}
			} catch (HrssException e) {
				e.alert();
			} catch (BillMutexException ex) {
				AwaySaveProcessor.showConflictInfoList(((BillMutexException) ex), nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0007")/*
																				 * @
																				 * res
																				 * "与下列单据有时间冲突，操作不能继续"
																				 */, ShopTaApplyConsts.DIALOG_ALERT);
				return;
			}

			service.insertArrayData(vos);
			// 关闭弹出页面
			CmdInvoker.invoke(new CloseWindowCmd());
			// 执行左侧快捷查询
			CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "closewindow"));
		} catch (BusinessException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 取消
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onCancel(MouseEvent mouseEvent) {
		AppLifeCycleContext.current().getApplicationContext().closeWinDialog();
	}

	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
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
