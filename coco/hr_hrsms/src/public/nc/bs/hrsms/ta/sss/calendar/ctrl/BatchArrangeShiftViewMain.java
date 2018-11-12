package nc.bs.hrsms.ta.sss.calendar.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.hrsms.ta.sss.calendar.WorkCalendarConsts;
import nc.bs.hrsms.ta.sss.common.ShopTAUtil;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.LineAddCmd;
import nc.bs.hrss.pub.cmd.LineDelCmd;
import nc.bs.hrss.pub.cmd.LineDownCmd;
import nc.bs.hrss.pub.cmd.LineInsertCmd;
import nc.bs.hrss.pub.cmd.LineUpCmd;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.team.TeamMngUtils;
import nc.hr.utils.ResHelper;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.comp.WebElement;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class BatchArrangeShiftViewMain implements IController {
	
	/**
	 * 弹出循环排班页面
	 * 
	 */
	public static final void doCircleArrangeShift(String funCode) {
		AppLifeCycleContext.current().getApplicationContext().addAppAttribute(WorkCalendarConsts.FUNCODE_CIRCLEARRANGESHIFT, funCode);
		CommonUtil.showWindowDialog("BatchArrangeShift", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0165")
				/* @res "循环排班"*/, "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}

	/**
	 * 页面初始化
	 * 
	 * @param dataLoadEvent
	 */

	public void onDataLoad_dsCircleArrangeShift(DataLoadEvent dataLoadEvent) {
		// 初始排班信息
		Dataset ds = dataLoadEvent.getSource();
		DatasetUtil.initWithEmptyRow(ds, Row.STATE_NORMAL);
		ds.setEnabled(Boolean.TRUE);
		
		String funCode = (String)AppLifeCycleContext.current().getApplicationContext().getAppAttribute(WorkCalendarConsts.FUNCODE_CIRCLEARRANGESHIFT);
		// 查询班组
		if(WorkCalendarConsts.TEAMCALENDAR_FUN_CODE.equals(funCode)){
			onDataLoad_dsTeamMaintain(null);
		}
		// 查询人员
		else{
			onDataLoad_dsPsn(null);
		}
		onDataLoad_dsWorkPeriod(null);
	}
	
	/**
	 * 排班人员数据集加载事件
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_dsPsn(DataLoadEvent dataLoadEvent) {
		/*放开下级部门权限，列出所有当前管理部门及下级部门的人员 */
		List<PsnJobVO> psnJobList = ShopTAUtil.queryPsnJobVOlist(true);
		if (null == psnJobList || psnJobList.size() == 0) {
			return;
		}
		Dataset dsPsn = ViewUtil.getDataset(ViewUtil.getCurrentView(), WorkCalendarConsts.DS_PSN);
		if(!isPagination(dsPsn)){
			DatasetUtil.clearData(dsPsn);
			dsPsn.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		}
		
		SuperVO[] vos = DatasetUtil.paginationMethod(dsPsn, psnJobList.toArray(new PsnJobVO[0]));
		new SuperVO2DatasetSerializer().serialize(vos, dsPsn, Row.STATE_NORMAL);
	}
	
	/**
	 * 排班班组数据集加载事件
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_dsTeamMaintain(DataLoadEvent dataLoadEvent) {
		LfwView widget = ViewUtil.getCurrentView();
		Dataset dsTeam = widget.getViewModels().getDataset(WorkCalendarConsts.DS_TEAM);
		if(!isPagination(dsTeam)){
			DatasetUtil.clearData(dsTeam);
			dsTeam.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		}
		TeamMngUtils.onTeamSearch(widget);
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
	 * 工作周期数据集加载
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_dsWorkPeriod(DataLoadEvent dataLoadEvent) {
		LfwView widget = ViewUtil.getCurrentView();
		Dataset ds = widget.getViewModels().getDataset(
				WorkCalendarConsts.DS_WORKPERIOD);
		DatasetUtil.clearData(ds);
		ds.setCurrentKey(Dataset.MASTER_KEY);
		Row row = ds.getEmptyRow();
		for (int i = 0; i < 7; i++) {
			row = ds.getEmptyRow();
			ds.addRow(row);
		}
		ds.setRowSelectIndex(0);
	}
	
	/**
	 * 循环排班保存方法
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onSave(MouseEvent mouseEvent){
		LfwView widget = ViewUtil.getCurrentView();
		// 排班表单信息
		Dataset dsCircleArrangeShift = widget.getViewModels().getDataset(WorkCalendarConsts.DS_CIRCLEARRANGESHIFT);
		HashMap<String, Object> value = DatasetUtil.getValueMap(dsCircleArrangeShift);
		// 开始日期
		UFLiteralDate beginDate = (UFLiteralDate) value.get(WorkCalendarConsts.FD_BEGINDATE);
		// 结束日期
		UFLiteralDate endDate = (UFLiteralDate) value.get(WorkCalendarConsts.FD_ENDDATE);
		// 覆盖已有工作日历
		UFBoolean overrideExistCalendar = (UFBoolean)value.get("isCoverOldShift");
		// 遇公共假日排班取消
		UFBoolean isHolidayCancel = (UFBoolean)value.get("isHolidayCancel");
		
		if (beginDate == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
					ResHelper.getString("c_ta-res", "0c_ta-res0017")/*
																															 * @
																															 * res
																															 * "开始日期不能为空！"
																															 */);
		}
		if (endDate == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0018")/*
																															 * @
																															 * res
																															 * "结束日期不能为空！"
																															 */);
		}
		
		// 工作周期
		Dataset dsWorkPeriod = widget.getViewModels().getDataset(WorkCalendarConsts.DS_WORKPERIOD);
		Row[] periodRows = dsWorkPeriod.getCurrentRowData().getRows();
		List<String> calendarPks = new ArrayList<String>();
		if(periodRows == null){
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0168")/*@ res"请设置工作周期！"*/);
		}
		String pk_shift = null;
		int rowCount = 0;
		for(Row row : periodRows){
			rowCount += 1;
			pk_shift = (String) row.getValue(dsWorkPeriod.nameToIndex(ShiftVO.PK_SHIFT));
			if(StringUtils.isEmpty(pk_shift)){
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0175")/**@ res"第"*/
						+ rowCount + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0169")/**@ res"行未设置班次！"*/);
			}else{
				calendarPks.add(pk_shift);
			}
		}
		if(calendarPks.isEmpty()){
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0168")/*@ res"请设置工作周期！"*/);
		}
		// 当前节点Funcode
		String funcode = (String) AppLifeCycleContext.current().getApplicationContext().getAppAttribute(WorkCalendarConsts.FUNCODE_CIRCLEARRANGESHIFT);
		// 班组工作日历节点
		if(WorkCalendarConsts.TEAMCALENDAR_FUN_CODE.equals(funcode)){
			Dataset dsTeam = widget.getViewModels().getDataset(WorkCalendarConsts.DS_TEAM);
			Row[] teamRows = dsTeam.getSelectedRows();
			String pk_org = SessionUtil. getHROrg(WorkCalendarConsts.TEAMCALENDAR_FUN_CODE, true);
			List<String> pk_teams = new ArrayList<String>();
			if(teamRows == null){
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0166")/*@ res"请选择排班班组！"*/);
			}
			for(Row row : teamRows){
				pk_teams.add((String) row.getValue(dsTeam.nameToIndex(TeamHeadVO.CTEAMID)));
			}
			TeamMngUtils.circularArrange(pk_org, pk_teams.toArray(new String[0]), beginDate, endDate, 
					calendarPks.toArray(new String[0]), isHolidayCancel.booleanValue(), overrideExistCalendar.booleanValue(), UFBoolean.FALSE.booleanValue());
		}
		// 员工工作日历节点
		else if(WorkCalendarConsts.FUNC_CODE.equals(funcode)){
			Dataset dsPsn = widget.getViewModels().getDataset(WorkCalendarConsts.DS_PSN);
			Row[] psnRows = dsPsn.getSelectedRows();
//			String pk_org = SessionUtil. getHROrg(CalendarConsts.MNG_FUNC_CODE, true);
			String pk_org = SessionUtil.getPk_mng_org();
			List<String> pk_psndocs = new ArrayList<String>();
			if(psnRows == null){
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0167")/*@ res"请选择排班人员！"*/);
			}
			for(Row row : psnRows){
				pk_psndocs.add((String) row.getValue(dsPsn.nameToIndex(PsnJobVO.PK_PSNDOC)));
			}
			TeamMngUtils.circularArrange(pk_org, pk_psndocs.toArray(new String[0]), beginDate, endDate,
					calendarPks.toArray(new String[0]), isHolidayCancel.booleanValue(), overrideExistCalendar.booleanValue());
		}
		
		// 排班成功后关闭当前窗口，刷新父页面
		// 关闭当前窗口
		AppLifeCycleContext.current().getApplicationContext().closeWinDialog();
		// 刷新父页面
		UifPlugoutCmd cmd = new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "circleArrangeShift_outId");
		cmd.execute();
	}
	
	/**
	 * 取消
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onCancel(MouseEvent mouseEvent){
		AppLifeCycleContext.current().getApplicationContext().closeWinDialog();
	}
	
	/**
	 * 子表增行
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onLineAdd(MouseEvent mouseEvent) {
		// 暂时支持一个周期31天
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(), WorkCalendarConsts.DS_WORKPERIOD);
		if(ds.getCurrentRowCount() >= 31){
			return;
		}
		CmdInvoker.invoke(new LineAddCmd(WorkCalendarConsts.DS_WORKPERIOD,null));
	}

	/**
	 * 
	 * 插入行
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onLineInsert(MouseEvent mouseEvent) {
		CmdInvoker.invoke(new LineInsertCmd(WorkCalendarConsts.DS_WORKPERIOD, null));
	}

	/**
	 * 子表行删除
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onLineDel(MouseEvent mouseEvent) {
		CmdInvoker.invoke(new LineDelCmd(WorkCalendarConsts.DS_WORKPERIOD,null));
	}
	
	/**
	 * 上移
	 * 
	 * @param mouseEvent
	 */
	public void moveUp(MouseEvent<WebElement> mouseEvent) {
		CmdInvoker.invoke(new LineUpCmd(WorkCalendarConsts.DS_WORKPERIOD, null));
	}

	/**
	 * 下移
	 * 
	 * @param mouseEvent
	 */
	public void moveDown(MouseEvent<WebElement> mouseEvent) {
		CmdInvoker.invoke(new LineDownCmd(WorkCalendarConsts.DS_WORKPERIOD, null));
	}
}
