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
	 * ����ѭ���Ű�ҳ��
	 * 
	 */
	public static final void doCircleArrangeShift(String funCode) {
		AppLifeCycleContext.current().getApplicationContext().addAppAttribute(WorkCalendarConsts.FUNCODE_CIRCLEARRANGESHIFT, funCode);
		CommonUtil.showWindowDialog("BatchArrangeShift", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0165")
				/* @res "ѭ���Ű�"*/, "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}

	/**
	 * ҳ���ʼ��
	 * 
	 * @param dataLoadEvent
	 */

	public void onDataLoad_dsCircleArrangeShift(DataLoadEvent dataLoadEvent) {
		// ��ʼ�Ű���Ϣ
		Dataset ds = dataLoadEvent.getSource();
		DatasetUtil.initWithEmptyRow(ds, Row.STATE_NORMAL);
		ds.setEnabled(Boolean.TRUE);
		
		String funCode = (String)AppLifeCycleContext.current().getApplicationContext().getAppAttribute(WorkCalendarConsts.FUNCODE_CIRCLEARRANGESHIFT);
		// ��ѯ����
		if(WorkCalendarConsts.TEAMCALENDAR_FUN_CODE.equals(funCode)){
			onDataLoad_dsTeamMaintain(null);
		}
		// ��ѯ��Ա
		else{
			onDataLoad_dsPsn(null);
		}
		onDataLoad_dsWorkPeriod(null);
	}
	
	/**
	 * �Ű���Ա���ݼ������¼�
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_dsPsn(DataLoadEvent dataLoadEvent) {
		/*�ſ��¼�����Ȩ�ޣ��г����е�ǰ�����ż��¼����ŵ���Ա */
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
	 * �Ű�������ݼ������¼�
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
	 * ��ҳ������־
	 * 
	 * @param ds
	 * @return
	 */
	private boolean isPagination(Dataset ds) {
		PaginationInfo pg = ds.getCurrentRowSet().getPaginationInfo();
		return pg.getRecordsCount() > 0;
	}
	
	/**
	 * �����������ݼ�����
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
	 * ѭ���Űౣ�淽��
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onSave(MouseEvent mouseEvent){
		LfwView widget = ViewUtil.getCurrentView();
		// �Ű����Ϣ
		Dataset dsCircleArrangeShift = widget.getViewModels().getDataset(WorkCalendarConsts.DS_CIRCLEARRANGESHIFT);
		HashMap<String, Object> value = DatasetUtil.getValueMap(dsCircleArrangeShift);
		// ��ʼ����
		UFLiteralDate beginDate = (UFLiteralDate) value.get(WorkCalendarConsts.FD_BEGINDATE);
		// ��������
		UFLiteralDate endDate = (UFLiteralDate) value.get(WorkCalendarConsts.FD_ENDDATE);
		// �������й�������
		UFBoolean overrideExistCalendar = (UFBoolean)value.get("isCoverOldShift");
		// �����������Ű�ȡ��
		UFBoolean isHolidayCancel = (UFBoolean)value.get("isHolidayCancel");
		
		if (beginDate == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
					ResHelper.getString("c_ta-res", "0c_ta-res0017")/*
																															 * @
																															 * res
																															 * "��ʼ���ڲ���Ϊ�գ�"
																															 */);
		}
		if (endDate == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0018")/*
																															 * @
																															 * res
																															 * "�������ڲ���Ϊ�գ�"
																															 */);
		}
		
		// ��������
		Dataset dsWorkPeriod = widget.getViewModels().getDataset(WorkCalendarConsts.DS_WORKPERIOD);
		Row[] periodRows = dsWorkPeriod.getCurrentRowData().getRows();
		List<String> calendarPks = new ArrayList<String>();
		if(periodRows == null){
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0168")/*@ res"�����ù������ڣ�"*/);
		}
		String pk_shift = null;
		int rowCount = 0;
		for(Row row : periodRows){
			rowCount += 1;
			pk_shift = (String) row.getValue(dsWorkPeriod.nameToIndex(ShiftVO.PK_SHIFT));
			if(StringUtils.isEmpty(pk_shift)){
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0175")/**@ res"��"*/
						+ rowCount + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0169")/**@ res"��δ���ð�Σ�"*/);
			}else{
				calendarPks.add(pk_shift);
			}
		}
		if(calendarPks.isEmpty()){
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0168")/*@ res"�����ù������ڣ�"*/);
		}
		// ��ǰ�ڵ�Funcode
		String funcode = (String) AppLifeCycleContext.current().getApplicationContext().getAppAttribute(WorkCalendarConsts.FUNCODE_CIRCLEARRANGESHIFT);
		// ���鹤�������ڵ�
		if(WorkCalendarConsts.TEAMCALENDAR_FUN_CODE.equals(funcode)){
			Dataset dsTeam = widget.getViewModels().getDataset(WorkCalendarConsts.DS_TEAM);
			Row[] teamRows = dsTeam.getSelectedRows();
			String pk_org = SessionUtil. getHROrg(WorkCalendarConsts.TEAMCALENDAR_FUN_CODE, true);
			List<String> pk_teams = new ArrayList<String>();
			if(teamRows == null){
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0166")/*@ res"��ѡ���Ű���飡"*/);
			}
			for(Row row : teamRows){
				pk_teams.add((String) row.getValue(dsTeam.nameToIndex(TeamHeadVO.CTEAMID)));
			}
			TeamMngUtils.circularArrange(pk_org, pk_teams.toArray(new String[0]), beginDate, endDate, 
					calendarPks.toArray(new String[0]), isHolidayCancel.booleanValue(), overrideExistCalendar.booleanValue(), UFBoolean.FALSE.booleanValue());
		}
		// Ա�����������ڵ�
		else if(WorkCalendarConsts.FUNC_CODE.equals(funcode)){
			Dataset dsPsn = widget.getViewModels().getDataset(WorkCalendarConsts.DS_PSN);
			Row[] psnRows = dsPsn.getSelectedRows();
//			String pk_org = SessionUtil. getHROrg(CalendarConsts.MNG_FUNC_CODE, true);
			String pk_org = SessionUtil.getPk_mng_org();
			List<String> pk_psndocs = new ArrayList<String>();
			if(psnRows == null){
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0169"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0167")/*@ res"��ѡ���Ű���Ա��"*/);
			}
			for(Row row : psnRows){
				pk_psndocs.add((String) row.getValue(dsPsn.nameToIndex(PsnJobVO.PK_PSNDOC)));
			}
			TeamMngUtils.circularArrange(pk_org, pk_psndocs.toArray(new String[0]), beginDate, endDate,
					calendarPks.toArray(new String[0]), isHolidayCancel.booleanValue(), overrideExistCalendar.booleanValue());
		}
		
		// �Ű�ɹ���رյ�ǰ���ڣ�ˢ�¸�ҳ��
		// �رյ�ǰ����
		AppLifeCycleContext.current().getApplicationContext().closeWinDialog();
		// ˢ�¸�ҳ��
		UifPlugoutCmd cmd = new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "circleArrangeShift_outId");
		cmd.execute();
	}
	
	/**
	 * ȡ��
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onCancel(MouseEvent mouseEvent){
		AppLifeCycleContext.current().getApplicationContext().closeWinDialog();
	}
	
	/**
	 * �ӱ�����
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onLineAdd(MouseEvent mouseEvent) {
		// ��ʱ֧��һ������31��
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(), WorkCalendarConsts.DS_WORKPERIOD);
		if(ds.getCurrentRowCount() >= 31){
			return;
		}
		CmdInvoker.invoke(new LineAddCmd(WorkCalendarConsts.DS_WORKPERIOD,null));
	}

	/**
	 * 
	 * ������
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onLineInsert(MouseEvent mouseEvent) {
		CmdInvoker.invoke(new LineInsertCmd(WorkCalendarConsts.DS_WORKPERIOD, null));
	}

	/**
	 * �ӱ���ɾ��
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onLineDel(MouseEvent mouseEvent) {
		CmdInvoker.invoke(new LineDelCmd(WorkCalendarConsts.DS_WORKPERIOD,null));
	}
	
	/**
	 * ����
	 * 
	 * @param mouseEvent
	 */
	public void moveUp(MouseEvent<WebElement> mouseEvent) {
		CmdInvoker.invoke(new LineUpCmd(WorkCalendarConsts.DS_WORKPERIOD, null));
	}

	/**
	 * ����
	 * 
	 * @param mouseEvent
	 */
	public void moveDown(MouseEvent<WebElement> mouseEvent) {
		CmdInvoker.invoke(new LineDownCmd(WorkCalendarConsts.DS_WORKPERIOD, null));
	}
}
