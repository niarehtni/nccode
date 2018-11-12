package nc.bs.hrsms.ta.sss.calendar.ctrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.hrsms.ta.sss.calendar.pagemodel.BatchChangeShiftPageModel;
import nc.bs.hrsms.ta.sss.common.ShopTAUtil;
import nc.bs.hrss.pub.DialogSize;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.LinkEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.model.plug.TranslatedRows;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;

import org.apache.commons.lang.ArrayUtils;

public class BatchChangeShiftViewMain implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	/**
	 * 人员数据集加载事件
	 * 
	 * @param dataLoadEvent
	 */
	public void onDatasetLoad_dsPerson(DataLoadEvent dataLoadEvent) {
		/* 放开下级部门权限，列出所有当前管理部门及下级部门的人员 */
		List<PsnJobVO> psnJobList = ShopTAUtil.queryPsnJobVOlist(true);
		if (null == psnJobList || psnJobList.size() == 0) {
			return;
		}
		Dataset dsPsn = ViewUtil.getDataset(ViewUtil.getCurrentView(), BatchChangeShiftPageModel.DS_PERSON);
		if (!isPagination(dsPsn)) {
			DatasetUtil.clearData(dsPsn);
			dsPsn.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		}
		dsPsn.setVoMeta(PsnJobVO.class.getName());
		SuperVO[] vos = DatasetUtil.paginationMethod(dsPsn, psnJobList.toArray(new PsnJobVO[0]));
		new SuperVO2DatasetSerializer().serialize(vos, dsPsn, Row.STATE_NORMAL);
	}

	/**
	 * 部门数据集加载事件
	 * 
	 * @param dataLoadEvent
	 */
	@SuppressWarnings("unchecked")
	public void onDatasetLoad_dsDept(DataLoadEvent dataLoadEvent) {
		Dataset dsDept = ViewUtil.getDataset(ViewUtil.getCurrentView(), BatchChangeShiftPageModel.DS_DEPT);
		HRDeptVO[] hrDeptVOs = null;
		/* 放开下级部门权限，列出所有的下级部门 */
		Collection<HRDeptVO> collectionVOs = null;
		ArrayList<HRDeptVO> collectionVOs_canceled = new ArrayList<HRDeptVO>();
		try {
			collectionVOs = new BaseDAO().retrieveByClause(HRDeptVO.class, ShopTAUtil.getMngDeptWherePartSql());
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		if (!isPagination(dsDept)) {
			DatasetUtil.clearData(dsDept);
			dsDept.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		}
		if (collectionVOs != null && collectionVOs.size() != 0) {
			Iterator<HRDeptVO> it = collectionVOs.iterator();
			while (it.hasNext()) {
				HRDeptVO deptVO = it.next();
				if (deptVO.getHrcanceled().booleanValue()) {
					collectionVOs_canceled.add(deptVO);
				}
			}
		}
		collectionVOs.removeAll(collectionVOs_canceled);
		hrDeptVOs = collectionVOs.toArray(new HRDeptVO[0]);
		SuperVO[] vos = DatasetUtil.paginationMethod(dsDept, hrDeptVOs);
		new SuperVO2DatasetSerializer().serialize(vos, dsDept, Row.STATE_NORMAL);
	}

	/**
	 * 调班条件的初始化
	 * 
	 * @param dataLoadEvent
	 */
	public void onDatasetLoad_dsChangeClassInfo(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		DatasetUtil.initWithEmptyRow(ds, Row.STATE_NORMAL);

		// 是否按原班别调整
		int oriClassIdx = ds.nameToIndex(BatchChangeShiftPageModel.FLD_BY_ORI_CLASS);
		ds.getSelectedRow().setValue(oriClassIdx, UFBoolean.FALSE);

		ds.setEnabled(true);
		onDatasetLoad_dsDept(null);
		onDatasetLoad_dsPerson(null);

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
		Dataset dsClass = wdtMain.getViewModels().getDataset(BatchChangeShiftPageModel.DS_CLASS_INFO);
		HashMap<String, Object> value = DatasetUtil.getValueMap(dsClass);
		UFLiteralDate begin = (UFLiteralDate) value.get(BatchChangeShiftPageModel.FLD_BEGIN);
		UFLiteralDate end = (UFLiteralDate) value.get(BatchChangeShiftPageModel.FLD_END);
		UFBoolean byOriClass = (UFBoolean) value.get(BatchChangeShiftPageModel.FLD_BY_ORI_CLASS);
		String oriClass = (String) value.get(BatchChangeShiftPageModel.FLD_ORI_CLASS);
		String newClass = (String) value.get(BatchChangeShiftPageModel.FLD_NEW_CLASS);
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
		Dataset dsPsn = wdtMain.getViewModels().getDataset(BatchChangeShiftPageModel.DS_PERSON);
		Dataset dsDept = wdtMain.getViewModels().getDataset(BatchChangeShiftPageModel.DS_DEPT);
		// 选中的部门
		Row[] deptRows = dsDept.getSelectedRows();

		// 选中的人员
		Row[] psnRows = dsPsn.getSelectedRows();
		// 人员ID List
		List<String> pk_psndocs = new ArrayList<String>();
		if (ArrayUtils.isEmpty(deptRows) && ArrayUtils.isEmpty(psnRows)) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0169"), nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0013")/*
																			 * @
																			 * res
																			 * "调班人员和部门不能都为空！"
																			 */);
		}
		// 选中的人员
		if (!ArrayUtils.isEmpty(psnRows)) {
			for (Row row : psnRows) {
				pk_psndocs.add((String) row.getValue(dsPsn.nameToIndex(PsnJobVO.PK_PSNDOC)));
			}
		}
		// 选中的部门中的人员
		List<PsnJobCalendarVO> deptToPsnVOLists = new ArrayList<PsnJobCalendarVO>();
		String[] deptToPsns = null;

		if (!ArrayUtils.isEmpty(deptRows)) {
			HRDeptVO[] hrDeptVOs = new Dataset2SuperVOSerializer<HRDeptVO>().serialize(dsDept, deptRows);
			deptToPsnVOLists = ShopTAUtil.getPsnjobPks(hrDeptVOs, false);
			deptToPsns = StringPiecer.getStrArray(deptToPsnVOLists.toArray(new PsnJobCalendarVO[0]),
					BatchChangeShiftPageModel.FLD_PK_PSNDOC);
			if (!ArrayUtils.isEmpty(deptToPsns)) {
				pk_psndocs.addAll(Arrays.asList(deptToPsns));
			}

		}

		// 管理部门
		String pk_dept = SessionUtil.getPk_mng_dept();

		/* 执行更新 */
		try {
			ServiceLocator.lookup(IPsnCalendarManageMaintain.class).batchChangeShift4Mgr(pk_dept,
					pk_psndocs.toArray(new String[0]), begin, end, byOriClass.booleanValue(), oriClass, newClass);
		} catch (HrssException e) {
			e.alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}

		// 关闭当前窗口
		AppLifeCycleContext.current().getApplicationContext().closeWinDialog();
		// 刷新父页面
		UifPlugoutCmd cmd = new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "scty_outid");
		cmd.execute();

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

	/**
	 * 弹出批量调班页面
	 * 
	 */
	public static final void doBatchChange() {
		// 显示批量排班对话框
		CommonUtil.showWindowDialog("BatchChangeShift",
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0014")/*
																								 * @
																								 * res
																								 * "批量调班"
																								 */, "80%", "100%",
				null, ApplicationContext.TYPE_DIALOG, false, false);
	}

	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

	/**
	 * 按原班次调整的选中取消事件
	 * 
	 * @param datasetCellEvent
	 */
	public void onAfterClassInfoDataChange(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		Row row = ds.getSelectedRow();
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		FormComp frmChangeClassInfo = (FormComp) widget.getViewComponents().getComponent(
				BatchChangeShiftPageModel.COMP_FRM_CHANGE_CLASS_INFO);
		// 选中之后，只调整原班次为指定班次的排班
		UFBoolean byOriClass = row.getUFBoolean(ds.nameToIndex(BatchChangeShiftPageModel.FLD_BY_ORI_CLASS));
		if (byOriClass.booleanValue()) {// 选中之后原班次变为可编辑
			frmChangeClassInfo.getElementById(BatchChangeShiftPageModel.COMP_FE_ORI_CLASS_NAME).setEnabled(true);
		} else {// 不选中原班次不可编辑
			frmChangeClassInfo.getElementById(BatchChangeShiftPageModel.COMP_FE_ORI_CLASS_NAME).setEnabled(false);
		}
	}

	/**
	 * 选择人员链接
	 * 
	 * @param linkEvent
	 */
	public void onChangeClassByDept(LinkEvent linkEvent) {
		CommonUtil.showViewDialog(BatchChangeShiftPageModel.PAGE_DEPTLIST_WIDGET, nc.vo.ml.NCLangRes4VoTransl
				.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0015")/*
																		 * @ res
																		 * "按部门调班"
																		 */, DialogSize.SMALL);
	}

	/**
	 * 选择部门链接
	 * 
	 * @param linkEvent
	 */
	public void onChangeClassByPsn(LinkEvent linkEvent) {
		CommonUtil.showViewDialogNoLine(BatchChangeShiftPageModel.PAGE_PSNLIST_WIDGET,
				ResHelper.getString("c_ta-res", "0c_ta-res0016") /*
																 * @ res "按人员调班"
																 */, "620", "480", false, true);

	}

	public void pluginpsnList(Map<String, Object> keys) {
		Dataset ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels()
				.getDataset(BatchChangeShiftPageModel.DS_PERSON);
		TranslatedRows rows = (TranslatedRows) keys.get(BatchChangeShiftPageModel.PLUGINID_PSN);
		List<PsnJobVO> vos = CommonUtil.getSuperVOByTranslatedRows(PsnJobVO.class, rows);
		if (vos == null) {
			return;
		} else {
			new SuperVO2DatasetSerializer().serialize(vos.toArray(new PsnJobVO[0]), ds);
			DatasetUtil.runFieldRelation(ds, null);
		}
		BatchChangeShiftPageModel.refreshPsnLink();
		AppLifeCycleContext.current().getWindowContext().closeView(BatchChangeShiftPageModel.PAGE_PSNLIST_WIDGET);

	}

	public void plugindeptList(Map<String, Object> keys) {
		Dataset ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels()
				.getDataset(BatchChangeShiftPageModel.DS_DEPT);
		TranslatedRows rows = (TranslatedRows) keys.get(BatchChangeShiftPageModel.PLUGINID_DEPT);
		List<HRDeptVO> vos = CommonUtil.getSuperVOByTranslatedRows(HRDeptVO.class, rows);
		if (vos == null) {
			return;
		} else {
			new SuperVO2DatasetSerializer().serialize(vos.toArray(new HRDeptVO[0]), ds);
			DatasetUtil.runFieldRelation(ds, null);
		}
		BatchChangeShiftPageModel.refreshDeptLink();
		AppLifeCycleContext.current().getWindowContext().closeView(BatchChangeShiftPageModel.PAGE_DEPTLIST_WIDGET);

	}

}
