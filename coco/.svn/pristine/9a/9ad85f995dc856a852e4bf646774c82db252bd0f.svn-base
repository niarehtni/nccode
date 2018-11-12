package nc.bs.hrsms.ta.sss.ShopAttendance.ctrl;

import java.util.List;
import java.util.Map;

import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttForEmpPageModel;
import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttendanceForBatchPageModel;
import nc.bs.hrsms.ta.sss.calendar.pagemodel.BatchChangeShiftPageModel;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.ComboDataUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.model.plug.TranslatedRows;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.lateearly.LateEarlyVO;

public class ShopAttendanceForBatchViewMain implements IController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	    
	/**
	 * 调班条件的初始化
	 * 
	 * @param dataLoadEvent
	 */
	public void onDatasetLoad_dsChangeClassInfo(DataLoadEvent dataLoadEvent) {
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		ComboData datestatus = widget.getViewModels().getComboData("combo_dsManualData_datestatus");
		ComboData editdate = widget.getViewModels().getComboData("combo_dsManualData_editdate");
		
		Dataset ds = dataLoadEvent.getSource();
		Row row=ds.getEmptyRow();
		// 管理部门所在的HR组织
		String pk_hr_org = SessionUtil.getPsndocVO().getPk_group();
		UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDateByPkOrg(pk_hr_org);
		String[] datests = {"正常","迟到或早退","未出勤"};
		String[] editdates={"所有时段"};
    	ComboDataUtil.addCombItemsAfterClean(datestatus, datests);
    	ComboDataUtil.addCombItemsAfterClean(editdate, editdates);
//    	row.setValue(ds.nameToIndex("begindate"),new UFLiteralDate() );
    	row.setValue(ds.nameToIndex("begindate"),dates[0] );
    	row.setValue(ds.nameToIndex("enddate"), dates[1]);
    	
//		DatasetUtil.initWithEmptyRow(ds, Row.STATE_NORMAL);
		ds.addRow(row);
		ds.setRowSelectIndex(0);
		ds.setEnabled(true);
		

	}
	
	public void onDataLoad_dsManualData(DataLoadEvent dataLoadEvent){
//		Dataset dsMaual = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset(ShopAttForEmpPageModel.DATASET_MANUAL);
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	
	}
	
	/**
	 * 分页操作标志
	 * 
	 * @param ds
	 * @return
	 */
	@SuppressWarnings("unused")
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
 		Dataset ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels()
				.getDataset("dsChangeClassInfo");
		Row row = ds.getSelectedRow();
		UFLiteralDate begindate = (UFLiteralDate) row.getValue(ds.nameToIndex("begindate"));
    	UFLiteralDate enddate=(UFLiteralDate) row.getValue(ds.nameToIndex("enddate"));
    	@SuppressWarnings("unused")
		String newClassName=(String)row.getValue(ds.nameToIndex("newClassName"));
    	String newClass=(String)row.getValue(ds.nameToIndex("newClass"));
    	String editdate=(String)row.getValue(ds.nameToIndex("editdate"));
    	String datestatus=(String)row.getValue(ds.nameToIndex("datestatus"));
		//获取付页面的数据集
    	LfwView main = AppLifeCycleContext.current().getViewContext().getView();
    	
		Dataset dsManual = main.getViewModels().getDataset(ShopAttForEmpPageModel.DATASET_MANUAL);
		dsManual.setVoMeta(LateEarlyVO.class.getName());
//		SuperVO[] vos = DatasetUtil.getSelectedRowVOs(dsManual);
//		Row[]rows=dsManual.getAllRow();
		SessionBean bean = SessionUtil.getSessionBean();
		bean.setExtendAttribute(ShopAttendanceForBatchPageModel.ISBATCHEDIT, true);
		bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_BEGIN, begindate);
		bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_END, enddate);
		bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_NEW_CLASS, newClass);
		bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_EDITDATE, editdate);
		bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_DATESTATUS, datestatus);

		/* 执行更新 */

		// 关闭弹出页面
		CmdInvoker.invoke(new CloseWindowCmd());
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET,"closewindow"));
		

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
	 * 弹出批量修改页面
	 * 
	 */
	public static final void doBatchEdit() {
		// 显示批量排班对话框
		CommonUtil.showWindowDialog("ShopAttendanceForBatch",
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "批量修改")/*
																								 * @
																								 * res
																								 * "批量修改"
																								 */, "47%", "50%",
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
		// 字段顺序
	    int colIndex = datasetCellEvent.getColIndex();
	    if(colIndex == ds.nameToIndex("begindate")||colIndex == ds.nameToIndex("enddate")){
	    	UFLiteralDate begindate = (UFLiteralDate) row.getValue(ds.nameToIndex("begindate"));
	    	UFLiteralDate enddate=(UFLiteralDate) row.getValue(ds.nameToIndex("enddate"));
		
			if(begindate.after(enddate)){
				CommonUtil.showErrorDialog("提示", "开始日期不能晚于结束日期，请重新输入！");
			}
			
		}
	    if(colIndex == ds.nameToIndex("newClassName")){
	    	if(row.getValue(ds.nameToIndex("newClassName"))==null){
	    		CommonUtil.showErrorDialog("提示", "班次不能为空，请重新输入！");
	    	}
	    }
	    if(colIndex == ds.nameToIndex("editdate")){
	    	if(row.getValue(ds.nameToIndex("editdate"))==null){
	    		CommonUtil.showErrorDialog("提示", "修改时段为空，请重新输入！");
	    	}
	    }
	    if(colIndex == ds.nameToIndex("datestatus")){
	    	if(row.getValue(ds.nameToIndex("datestatus"))==null){
	    		CommonUtil.showErrorDialog("提示", "时段状态为空，请重新输入！");
	    	}
	    }
	    
	    
	}

	/**
	 * 从卡片界面回到列表界面的查询操作
	 * 
	 * @param keys
	 */
	public void pluginReSearch(Map<String, Object> keys){
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
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
