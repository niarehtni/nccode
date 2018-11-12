package nc.bs.hrsms.ta.sss.lateearly.ctrl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.lateearly.ShopLateEarlyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.qry.QueryUtil;
import nc.itf.bd.shift.IShiftQueryService;
import nc.itf.ta.ILateEarlyManageMaintain;
import nc.itf.ta.ILateEarlyQueryMaintain;
import nc.uap.ctrl.tpl.qry.FromWhereSQLImpl;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.RowData;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.hrcm.agreement.PsnjobVO;
import nc.vo.hrsms.ta.sss.shop.ShopQryConditionVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.lateearly.LateEarlyVO;

import org.apache.commons.lang.StringUtils;

public class ShopLateEarlyListCtrl implements IController{

	
	/**
	 * 获得当前片段
	 * 
	 * @return
	 */
	protected LfwView getCurrentActiveView() {
		return AppLifeCycleContext.current().getViewContext().getView();
	}
	/**
	 * 获得当前WindowContext
	 */
	private WindowContext getCurrentWindowContext() {
		return AppLifeCycleContext.current().getWindowContext();
	}
	/**
	 * 部门切换
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginDeptChange(Map<String, Object> keys) throws BusinessException{
		LfwView simpQryView = getCurrentWindowContext().getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		if (dsSearch != null) {
//			Row selRow = dsSearch.getSelectedRow();
//			// 管理部门所在的HR组织
//			ExtAttribute vo = SessionUtil.getSessionBean().getExtendAttribute(ShopLateEarlyConsts.SESSION_QRY_CONDITIONS);
//			vo.getValue();
//			//			String pk_hr_org = SessionUtil.getPsndocVO().getPk_group();
////			UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDateByPkOrg(pk_hr_org);
//			if (dsSearch.nameToIndex(ShopLateEarlyConsts.FD_LATEDATE) > -1) {
//				// 日期
//				selRow.setValue(dsSearch.nameToIndex(ShopLateEarlyConsts.FD_LATEDATE), vo.getBeginDate);
//			}
//			// 清空保存的查询条件
//			SessionUtil.getSessionBean().setExtendAttribute(ShopLateEarlyConsts.SESSION_QRY_CONDITIONS, null);
			// 执行左侧快捷查询
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
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
	
	/**
	 * 从批量新增卡片界面回到列表界面的查询操作
	 * 
	 * @param keys
	 */
	public void pluginBatch_ReSearch(Map<String, Object> keys){
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	/**
	 * 查询操作
	 * 
	 * @param keys
	 * @throws BusinessException 
	 */
	public void pluginSearch(Map<String, Object> keys) throws BusinessException {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopLateEarlyConsts.DS_MAIN_NAME);
		if (keys == null || keys.size() == 0) {
			return;
		}
		// 是否包含下级部门
		boolean isContainSub = SessionUtil.isIncludeSubDept();
		// 查询条件-部门
		String pk_dept = SessionUtil.getPk_mng_dept();
		if (StringUtils.isEmpty(pk_dept)) {
			return;
		}
		ShopQryConditionVO vo = this.getConditions(keys);
		try {
			LateEarlyVO[] vos = getLateEarlyVO(vo.getBeginDate(),vo.getFromWhereSQL(),pk_dept,isContainSub);
			SuperVO[] svos = DatasetUtil.paginationMethod(ds, vos);
			new SuperVO2DatasetSerializer().serialize(svos, ds, Row.STATE_NORMAL);
			//默认选中第一行
			ds.setRowSelectIndex(CommonUtil.getAppAttriSelectedIndex());
			
			Row[] rows = ds.getAllRow();
			if(rows==null){
				return;
			}
			for(Row row:rows){
				String time = row.getValue(ds.nameToIndex("calendar")).toString();
				try {
					String str = this.getDayForWeek(time);
					row.setValue(ds.nameToIndex("calendar1"), time+" "+str);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			new HrssException(e).deal();
		}
	}
	/**
	 * 
	 * @param date
	 * @param fromWhereSQL
	 * @param pk_dept
	 * @param containsSubDepts
	 * @return
	 */
	public LateEarlyVO[] getLateEarlyVO(UFLiteralDate date,
			FromWhereSQL fromWhereSQL,String pk_dept,boolean containsSubDepts){
		LateEarlyVO[] vos = null;
		try {
			// 获得默认的组织
			String hrOrg = SessionUtil.getPk_org();
			vos = NCLocator.getInstance().lookup(ILateEarlyQueryMaintain.class).queryByDate(hrOrg ,date, fromWhereSQL, false);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return vos;
	}
	/**
	 * onDataLoad事件
	 * @param dataLoadEvent
	 * @throws BusinessException 
	 */
	public void onDataLoad_dsManualData(DataLoadEvent dataLoadEvent) throws BusinessException{
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	
	/**
	 * 获得查询条件.
	 * 
	 * @param keys
	 * @return
	 */
	public ShopQryConditionVO getConditions(Map<String, Object> keys) {
		SessionBean sess = SessionUtil.getSessionBean();

		ShopQryConditionVO vo = new ShopQryConditionVO();
		FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL);
		nc.ui.querytemplate.querytree.FromWhereSQLImpl fromWhereSQL = (nc.ui.querytemplate.querytree.FromWhereSQLImpl) CommonUtil.getUAPFromWhereSQL((nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL));
		// 获得自定义条件
		Map<String, String> selfDefMap = whereSql.getFieldAndSqlMap();
		
		// 查询条件-日期
		String lateDate = selfDefMap.get(ShopLateEarlyConsts.FD_LATEDATE);
		UFLiteralDate queryDate = new UFLiteralDate(lateDate);
		vo.setBeginDate(queryDate);
		String psnScopeSqlPart = null;
		try {
			psnScopeSqlPart = QueryUtil.getDeptPsnCondition();
			psnScopeSqlPart +=" and hi_psnjob."+PsnjobVO.ISMAINJOB+" = 'Y' and (( hi_psnjob.lastflag='N' and hi_psnjob.endflag='Y' and '"+queryDate.toString()+"' >= hi_psnjob." +PsnjobVO.BEGINDATE+" and '"+queryDate.toString()+"' <=hi_psnjob." +PsnjobVO.ENDDATE+")";
			psnScopeSqlPart +=" or ( hi_psnjob.lastflag='Y' and hi_psnjob.endflag='N' and '"+queryDate.toString()+"' >= hi_psnjob." +PsnjobVO.BEGINDATE+"))";
		} catch (BusinessException e) {
			throw new LfwRuntimeException(e.getMessage(),e.getCause());
		}

		if (fromWhereSQL != null && !StringUtils.isEmpty(psnScopeSqlPart)) {
			(fromWhereSQL).setWhere(fromWhereSQL.getWhere() + " and tbm_psndoc.pk_psndoc in (" + psnScopeSqlPart + ") ");
		}
		vo.setFromWhereSQL(fromWhereSQL);
		// 记录本次查询条件
		sess.setExtendAttribute(ShopLateEarlyConsts.SESSION_QRY_CONDITIONS, vo);

		return vo;
	}
	/**
	 * 修改
	 * @param mouseEvent
	 */
	public void editInfo(MouseEvent<MenuItem> mouseEvent) {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopLateEarlyConsts.DS_MAIN_NAME);
		setMenuItemVisible("list_edit",false);
		setMenuItemVisible("list_batch",false); // 批改 zhangjie
		setMenuItemVisible("list_save",true);
		setMenuItemVisible("list_cancel",true);
		ds.setEnabled(true);
	}
	
	/**
	 * 批改
	 * :批量设置成正常
	 * @param mouseEvent
	 */
	public void batchInfo(MouseEvent<MenuItem> mouseEvent) {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopLateEarlyConsts.DS_MAIN_NAME);
		setMenuItemVisible("list_edit",false);
		setMenuItemVisible("list_batch",false); // 批改 zhangjie
		setMenuItemVisible("list_save",true);
		setMenuItemVisible("list_cancel",true);
		ds.setEnabled(true);
		
		SuperVO[] vos = DatasetUtil.getUpdatedDataInVO(ds);
		if (vos == null || vos.length == 0) {
			return;
		}
		
		RowData rowData = ds.getCurrentRowData();
		Row[] rows = rowData.getRows();
		if(rows == null || rows.length == 0){
			return;
		}
		for(Row row : rows){
			String pk_shift = (String) row.getValue(ds.nameToIndex(LateEarlyVO.PK_SHIFT));
			if(StringUtils.isEmpty(pk_shift)){
				continue;
			}
			row.setValue(ds.nameToIndex("onebeginstatus"), 0);
			row.setValue(ds.nameToIndex("twoendstatus"), 0);
		}
	}
	
	/**
	 * 保存
	 * @param mouseEvent
	 */
	public void saveInfo(MouseEvent<MenuItem> mouseEvent) {
		Dataset dsManual = getCurrentActiveView().getViewModels().getDataset(ShopLateEarlyConsts.DS_MAIN_NAME);
		dsManual.setVoMeta(LateEarlyVO.class.getName());
		
		SuperVO[] vos = DatasetUtil.getUpdatedDataInVO(dsManual);
		
		if (vos == null || vos.length == 0) {
			return;
		}
		List<SuperVO> list = new ArrayList<SuperVO>();
		for(SuperVO vo : vos){
			list.add(vo);
		}
		vos = list.toArray(new SuperVO[0]);
		if (vos == null || vos.length == 0) {
			return;
		}
		LateEarlyVO[] levos = new LateEarlyVO[vos.length];
		String defaultOrg = SessionUtil.getPk_mng_org();
		String defaultGroup = SessionUtil.getPk_mng_group();
		for (int i = 0; i < vos.length; i++) {
			LateEarlyVO vo = (LateEarlyVO) vos[i];
			if (StringUtil.isEmpty(vo.getPk_org())) {
				/* 为新增记录增加默认组织、集团 */
				vo.setPk_org(defaultOrg);
			}
			if (StringUtil.isEmptyWithTrim(vo.getPk_group())) {
				vo.setPk_group(defaultGroup);
			}
			levos[i] = vo;
		}
		try {
			ILateEarlyManageMaintain lemm = ServiceLocator.lookup(ILateEarlyManageMaintain.class);
			lemm.update(levos);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		cancelInfo(mouseEvent);
	}
	
	/**
	 * 
	 * @param mouseEvent
	 */
	public void cancelInfo(MouseEvent<MenuItem> mouseEvent) {
		Dataset ds = getCurrentActiveView().getViewModels().getDataset(ShopLateEarlyConsts.DS_MAIN_NAME);
		ds.setEnabled(false);
		setMenuItemVisible("list_edit",true);
		setMenuItemVisible("list_batch",true); // 批改 zhangjie
		setMenuItemVisible("list_save",false);
		setMenuItemVisible("list_cancel",false);
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}
	/**
	 * 
	 * @param itemID
	 * @param visible
	 */
	private void setMenuItemVisible(String itemID,boolean visible){
		MenubarComp  menuBar = getCurrentActiveView().getViewMenus().getMenuBar("menu_list");
		if(menuBar!=null){
			MenuItem edit = menuBar.getItem(itemID);
			edit.setVisible(visible);
		}
	}
	
	/**
	 * 数据发生变化，对输入数据校验
	 * 
	 * @param datasetCellEvent
	 */
	public void dataChanged(DatasetCellEvent datasetCellEvent) {
		SessionBean bean = SessionUtil.getSessionBean();
		bean.setExtendAttribute(ShopLateEarlyConsts.ISBATCHEDIT, false);
		
		Dataset dsManual = getCurrentActiveView().getViewModels().getDataset(ShopLateEarlyConsts.DS_MAIN_NAME);
		// 获取行索引
		int rowIndex = datasetCellEvent.getRowIndex();
		// 获取列索引
		int colIndex = datasetCellEvent.getColIndex();
		RowData rowData = dsManual.getCurrentRowData();
		Row[] rows = rowData.getRows();
		Row row = rows[rowIndex];
		String pk_shift = (String) row.getValue(dsManual.nameToIndex(LateEarlyVO.PK_SHIFT));
//		UFLiteralDate calendar=(UFLiteralDate) row.getValue(dsManual.nameToIndex(LateEarlyVO.CALENDAR));
		if(StringUtils.isEmpty(pk_shift)){
			CommonUtil.showErrorDialog("提示", "该人员尚未排班，请先排班再操作!");
			return;
		}
		// 班次AggVO
		AggShiftVO aggShiftVO = null;
		try {
			IShiftQueryService shiftService = ServiceLocator.lookup(IShiftQueryService.class);
			aggShiftVO = shiftService.queryShiftAggVOByPk(pk_shift);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		} catch (HrssException e) {
			e.alert();
		}

		// 班次时长
		UFDouble gzsj = aggShiftVO.getShiftVO().getGzsj();
		// // 是否有夜班
		// UFBoolean includenightshift =
		// aggShiftVO.getShiftVO().getIncludenightshift();
		// 夜班时长
		UFDouble nightgzsj = aggShiftVO.getShiftVO().getNightgzsj();

		// 迟到时长
		UFDouble latelength = (UFDouble) row.getValue(dsManual.nameToIndex(LateEarlyVO.LATELENGTH));

		// 早退时长
		UFDouble earlylength = (UFDouble) row.getValue(dsManual.nameToIndex(LateEarlyVO.EARLYLENGTH));
		// 旷工时长
		UFDouble absenthour = (UFDouble) row.getValue(dsManual.nameToIndex(LateEarlyVO.ABSENTHOUR));
		// 夜班旷工时长
		UFDouble nightabsenthour = (UFDouble) row.getValue(dsManual.nameToIndex(LateEarlyVO.NIGHTABSENTHOUR));
		if (latelength == null) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.LATELENGTH), UFDouble.ZERO_DBL);
			return;
		}
		if (earlylength == null) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.EARLYLENGTH), UFDouble.ZERO_DBL);
			return;
		}
		if (absenthour == null) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.ABSENTHOUR), UFDouble.ZERO_DBL);
			return;
		}
		if (nightabsenthour == null) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.NIGHTABSENTHOUR), UFDouble.ZERO_DBL);
			return;
		}
		if (latelength.doubleValue() < 0) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.LATELENGTH), UFDouble.ZERO_DBL);
			return;
		}
		if (earlylength.doubleValue() < 0) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.EARLYLENGTH), UFDouble.ZERO_DBL);
			return;
		}
		if (absenthour.doubleValue() < 0) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.ABSENTHOUR), UFDouble.ZERO_DBL);
			return;
		}
		if (nightabsenthour.doubleValue() < 0) {
			row.setValue(dsManual.nameToIndex(LateEarlyVO.NIGHTABSENTHOUR), UFDouble.ZERO_DBL);
			return;
		}
		boolean flag = false;
		if (latelength.doubleValue() + earlylength.doubleValue() + absenthour.doubleValue() * 60 > gzsj.doubleValue() * 60) {
			flag = true;
		}

		// 修改迟到时长
		if (colIndex == dsManual.nameToIndex(LateEarlyVO.LATELENGTH)) {
			if (flag) {
				latelength = new UFDouble(gzsj.doubleValue() * 60 - (absenthour.doubleValue() * 60 + earlylength.doubleValue()));
				row.setValue(dsManual.nameToIndex(LateEarlyVO.LATELENGTH), latelength);
			}
			// 早退
		} else if (colIndex == dsManual.nameToIndex(LateEarlyVO.EARLYLENGTH)) {
			if (flag) {
				earlylength = new UFDouble(gzsj.doubleValue() * 60 - (absenthour.doubleValue() * 60 + latelength.doubleValue()));
				row.setValue(dsManual.nameToIndex(LateEarlyVO.EARLYLENGTH), earlylength);
			}
			// 旷工
		} else if (colIndex == dsManual.nameToIndex(LateEarlyVO.ABSENTHOUR)) {
			if (flag) {
				absenthour = new UFDouble((gzsj.doubleValue() * 60 - (earlylength.doubleValue() + latelength.doubleValue())) / 60);
				row.setValue(dsManual.nameToIndex(LateEarlyVO.ABSENTHOUR), absenthour);
			}
			// 修改夜班旷工时长
		} else if (colIndex == dsManual.nameToIndex(LateEarlyVO.NIGHTABSENTHOUR)) {
			if (nightabsenthour.doubleValue() > nightgzsj.doubleValue()) {
				row.setValue(dsManual.nameToIndex(LateEarlyVO.NIGHTABSENTHOUR), nightgzsj);
			}
		}else if(colIndex == dsManual.nameToIndex(LateEarlyVO.ONEBEGINSTATUS)){
//			bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_BEGIN, calendar);
//			bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_END, calendar);
			if(row.getValue(dsManual.nameToIndex(LateEarlyVO.ONEBEGINSTATUS))!=null){
				Integer status=(Integer) row.getValue(dsManual.nameToIndex(LateEarlyVO.ONEBEGINSTATUS));
				if(status==0){
					bean.setExtendAttribute(ShopLateEarlyConsts.FLD_DATESTATUS,"正常" );
				}else if(status==1){
					bean.setExtendAttribute(ShopLateEarlyConsts.FLD_DATESTATUS,"迟到或早退" );
				}else if(status==2){
					bean.setExtendAttribute(ShopLateEarlyConsts.FLD_DATESTATUS,"未出勤" );
				}
			}
		}else if(colIndex == dsManual.nameToIndex(LateEarlyVO.TWOENDSTATUS)){
//			bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_BEGIN, calendar);
//			bean.setExtendAttribute(ShopAttendanceForBatchPageModel.FLD_END, calendar);
			if(row.getValue(dsManual.nameToIndex(LateEarlyVO.TWOENDSTATUS))!=null){
				Integer status=(Integer) row.getValue(dsManual.nameToIndex(LateEarlyVO.TWOENDSTATUS));
				if(status==0){
					bean.setExtendAttribute(ShopLateEarlyConsts.FLD_DATESTATUS,"正常" );
				}else if(status==1){
					bean.setExtendAttribute(ShopLateEarlyConsts.FLD_DATESTATUS,"迟到或早退" );
				}else if(status==2){
					bean.setExtendAttribute(ShopLateEarlyConsts.FLD_DATESTATUS,"未出勤" );
				}
			}
			
		}
	}
	
	/**
	 * 根据日期获取星期几
	 * @param time
	 * @return
	 * @throws Throwable
	 */
	private String getDayForWeek(String time) throws Throwable {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
	    Date tmpDate = format.parse(time);  
	    Calendar cal = new GregorianCalendar();  
//	    cal.set(tmpDate.getYear(), tmpDate.getMonth(), tmpDate.getDay());
	    cal.setTime(tmpDate);
	    int wk = cal.get(Calendar.DAY_OF_WEEK)-1;
	    if(wk<0){  
	    	wk = 0;  
        }
	    String[] weeks = {"周日","周一","周二","周三","周四","周五","周六"};  
		return weeks[wk];
	}
}
