package nc.bs.hrsms.ta.sss.leaveoff.ctrl;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaListBasePageModel;
import nc.bs.hrsms.ta.sss.leaveoff.ShopLeaveOffConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ILeaveOffApplyQueryMaintain;
import nc.uap.lfw.core.comp.ButtonComp;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.MdDataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.ArrayUtils;

import uap.web.bd.pub.AppUtil;

public class ShopLeaveRegListView implements IController {

	/** 休假登记记录的ViewID */
	public static final String VIEW_ID = "reglist";
	
	/** 休假登记记录的DatasetID */
	public static final String DS_ID = "dsLeaveRegList";
	
	/** 传递参数 */
	public static final String APP_ID_PK_LEAVEREG = "app_pk_leavereg";
	public static final String APP_ID_PK_LEAVETYPE = "app_pk_leavetype";
	public static final String APP_ID_PK_LEAVETYPECOPY = "app_pk_leavetypecopy";
	public static final String APP_ID_PK_PSNDOC = "app_pk_psndoc";
	public static final String APP_ID_PK_PSNJOB = "app_pk_psnjob";

	
	public void onBeforeShow(DialogEvent dialogEvent) {
		
		Dataset ds = ViewUtil.getDataset(ViewUtil.getCurrentView(),"dsPsnInfo");
		Row row = ds.getEmptyRow();
		ds.setCurrentKey(Dataset.MASTER_KEY);
		ds.addRow(row);
		ds.setRowSelectIndex(0);
		ds.setEnabled(true);
	}
	
	public void onAfterDataChange_dsPsnInfo(DatasetCellEvent datasetCellEvent) throws BusinessException {
		Dataset ds = datasetCellEvent.getSource();
		int colIndex = datasetCellEvent.getColIndex();
		if(colIndex!=ds.nameToIndex("pk_psnjob")&&colIndex!=ds.nameToIndex("pk_leavetype")){
			return;
		}
		Row row =  ds.getSelectedRow();
		String pk_psnjob = null;
		String pk_leavetype = null;
		if(colIndex==ds.nameToIndex("pk_psnjob")){
			pk_psnjob = (String) row.getValue(ds.nameToIndex("pk_psnjob"));
			String pk_psndoc = (String) row.getValue(ds.nameToIndex("pk_psndoc"));
			getApplicationContext().addAppAttribute(ShopLeaveOffConsts.PK_PSNJOB, pk_psnjob);
			ShopTaAppContextUtil.addTaAppContext(pk_psndoc);
			LfwView viewMain = ViewUtil.getCurrentView();
			FormComp formComp = (FormComp) viewMain.getViewComponents().getComponent("frmpsninfo");
			FormElement realElement = formComp.getElementById("pk_leavetype_timeitemname");
			realElement.setEnabled(true);
			onDataLoad(null);
		}
		if(colIndex==ds.nameToIndex("pk_leavetype")){
			pk_leavetype = (String) row.getValue(ds.nameToIndex("pk_leavetype"));
			getApplicationContext().addAppAttribute(ShopLeaveOffConsts.PK_LEAVETYPE, pk_leavetype);
			onDataLoad(null);
		}
		/*if(StringUtils.isNotEmpty(pk_psnjob)){
			try {
				LeaveRegVO[] regvos  = NCLocator.getInstance().lookup(ILeaveOffApplyQueryMaintain.class)
						.getRegVos4LeaveOff(SessionUtil.getPk_org(), pk_psnjob,pk_leavetype);
				getApplicationContext().addAppAttribute(ShopLeaveOffConsts.LEAVEREG_VO, regvos);
				onDataLoad(null);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
	/**
	 * 休假登记数据的加载
	 * 
	 * @param dataLoadEvent
	 * @throws BusinessException 
	 */
	public void onDataLoad(DataLoadEvent dataLoadEvent) {
		String pk_psnjob1 = (String) getApplicationContext().getAppAttribute(ShopLeaveOffConsts.PK_PSNJOB);
		getApplicationContext().addAppAttribute(ShopLeaveOffConsts.PK_PSNJOB, null);
		if(StringUtil.isEmpty(pk_psnjob1)){
			return;
		}
		TBMPsndocVO tbmPsndocVO = ShopTaAppContextUtil.getTBMPsndocVO();
		if(tbmPsndocVO == null){
			return;
		}
		String pk_psnjob = tbmPsndocVO.getPk_psnjob();
		String pk_leavetype = (String) getApplicationContext().getAppAttribute(ShopLeaveOffConsts.PK_LEAVETYPE);
		
		LeaveRegVO[] vos = null;
		try {
			vos = NCLocator.getInstance().lookup(ILeaveOffApplyQueryMaintain.class)
					.getRegVos4LeaveOff(SessionUtil.getPk_org(), pk_psnjob,pk_leavetype);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Dataset ds = dataLoadEvent.getSource();
		LfwView view = ViewUtil.getCurrentView();
		
		setTimeDatasPrecision(view);
		
		Dataset ds = view.getViewModels().getDataset(DS_ID);
		if (ArrayUtils.isEmpty(vos)) {
			DatasetUtil.clearData(ds);
			return;
		}
		new SuperVO2DatasetSerializer().serialize(DatasetUtil.paginationMethod(ds, vos), ds, Row.STATE_NORMAL);
		ds.setRowSelectIndex(0);
		
		// 哺乳假显示开始日期和结束日期，非哺乳假显示开始时间和结束时间
		if(ds.getCurrentRowData() == null){
			return;
		}
		Row[] rows = ds.getCurrentRowData().getRows();
		if(rows == null){
			return;
		}
		UFBoolean islactation = UFBoolean.FALSE;
		for(Row row : rows){
			islactation = (UFBoolean) row.getValue(ds.nameToIndex(LeaveRegVO.ISLACTATION));
			if(UFBoolean.TRUE.equals(islactation)){
				row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEBEGINTIME), row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEBEGINDATE)));
				row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEENDTIME), row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEENDDATE)));
			}
		}
	}

	/**
	 * 确定按钮
	 * 
	 * @param mouseEvent
	 */
	public void onConfirm(MouseEvent<ButtonComp> mouseEvent) {
		
		LfwView view = ViewUtil.getCurrentView();
		Dataset ds = view.getViewModels().getDataset(DS_ID);
		Row row = ds.getSelectedRow();
		if(row == null){
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0167")/* @res "错误" */,
					ResHelper.getString("c_ta-res", "0c_ta-res0056")/* @res "请选择一条记录！" */);	
		}
		//休假登记的主键
		String pk_leavereg = row.getString(ds.nameToIndex(LeaveRegVO.PK_LEAVEREG));
		String pk_leavetype = row.getString(ds.nameToIndex(LeaveRegVO.PK_LEAVETYPE));
		String pk_leavetypecopy = row.getString(ds.nameToIndex(LeaveRegVO.PK_LEAVETYPECOPY));
		
		String pk_psndoc = row.getString(ds.nameToIndex("pk_psnjob_pk_psndoc"));
		String pk_psnjob = row.getString(ds.nameToIndex("pk_psnjob"));
		//传递休假时间、日期
		@SuppressWarnings("unused")
		UFBoolean islactation = UFBoolean.FALSE;
		islactation = (UFBoolean) row.getValue(ds.nameToIndex(LeaveRegVO.ISLACTATION));
		UFDouble leavehour = (UFDouble) row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEHOUR));
		AppUtil.addAppAttr(LeaveRegVO.LEAVEHOUR, leavehour);
//		if (UFBoolean.TRUE.equals(islactation)) {
			UFLiteralDate leavebegindate = (UFLiteralDate) row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEBEGINDATE));
			UFLiteralDate leaveenddate = (UFLiteralDate) row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEENDDATE));
			AppUtil.addAppAttr(LeaveRegVO.LEAVEBEGINDATE, leavebegindate);
			AppUtil.addAppAttr(LeaveRegVO.LEAVEENDDATE, leaveenddate);
//		} else {
			UFDateTime leavebegintime = (UFDateTime) row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEBEGINTIME));
			UFDateTime leaveendtime = (UFDateTime) row.getValue(ds.nameToIndex(LeaveRegVO.LEAVEENDTIME));
			AppUtil.addAppAttr(LeaveRegVO.LEAVEBEGINTIME, leavebegintime);
			AppUtil.addAppAttr(LeaveRegVO.LEAVEENDTIME, leaveendtime);
//		}
		
		//关闭当前的View
		CommonUtil.closeViewDialog(VIEW_ID);
		//跳转至申请单卡片界面
		String operate_status = HrssConsts.POPVIEW_OPERATE_ADD;
		AppUtil.addAppAttr(HrssConsts.POPVIEW_OPERATE_STATUS, operate_status);
		AppUtil.addAppAttr(HrssConsts.POPVIEW_OPERATE_PARAM, null);
		AppUtil.addAppAttr(APP_ID_PK_LEAVEREG, pk_leavereg);
		AppUtil.addAppAttr(APP_ID_PK_LEAVETYPE, pk_leavetype);
		AppUtil.addAppAttr(APP_ID_PK_LEAVETYPECOPY, pk_leavetypecopy);
		
		AppUtil.addAppAttr(APP_ID_PK_PSNDOC, pk_psndoc);
		AppUtil.addAppAttr(APP_ID_PK_PSNJOB, pk_psnjob);
		CommonUtil.showWindowDialog(ShopLeaveOffApplyCardWin.WIN_ID, "店员销假新增", "80%", "100%", null, ApplicationContext.TYPE_DIALOG, false, false);
	}

	/**
	 * 取消按钮
	 * 
	 * @param mouseEvent
	 */
	public void onCancel(MouseEvent<ButtonComp> mouseEvent) {
		CommonUtil.closeViewDialog(VIEW_ID);
	}
	

	/**
	 * 获得ApplicationContext
	 * 
	 * @return
	 */
	private static ApplicationContext getApplicationContext() {
		return AppLifeCycleContext.current().getApplicationContext();
	}


	/**
	 * 根据考勤规则设置考勤数据的小时位数
	 * 
	 */
	private static void setTimeDatasPrecision(LfwView viewMain) {
		// 考勤数据
		String[] timeDatas = getTimeDataFields();
		if (timeDatas == null || timeDatas.length == 0) {
			return;
		}
		Dataset[] dss = viewMain.getViewModels().getDatasets();
		if (dss == null || dss.length == 0) {
			return;
		}
		// 考勤位数
		int pointNum = getPointNum();
		for (Dataset ds : dss) {
			if (ds instanceof MdDataset) {
				for (String filedId : timeDatas) {
					int index = ds.getFieldSet().nameToIndex(filedId);
					if (index >= 0) {
						FieldSet fieldSet = ds.getFieldSet();
						Field field = fieldSet.getField(filedId);
						if(field instanceof UnmodifiableMdField) 
							field = ((UnmodifiableMdField) field).getMDField();
						fieldSet.updateField(filedId, field);
						
						field.setPrecision(String.valueOf(pointNum));
						
					}
				}
			}
		}
	}

	/**
	 * 获得考勤位数
	 * 
	 * @return
	 */
	private static int getPointNum() {
		TimeRuleVO timeRuleVO = ShopTaAppContextUtil.getTimeRuleVO();
		if (timeRuleVO == null) {
			// 没有考勤规则的情况，设置默认值
			return ShopTaListBasePageModel.DEFAULT_PRECISION;
		}
		int pointNum = Math.abs(timeRuleVO.getTimedecimal());
		return pointNum;
	}
	/**
	 * 设置考勤数据的小时位数<br/>
	 * String[]待设置的考勤数据字段数组<br/>
	 * 
	 * @return
	 */
	protected static String[] getTimeDataFields() {
		return new String[] { LeavehVO.SUMHOUR, LeavehVO.REALDAYORHOUR, LeavehVO.RESTEDDAYORHOUR, LeavehVO.RESTDAYORHOUR, LeavehVO.FREEZEDAYORHOUR, LeavehVO.USEFULDAYORHOUR, LeavebVO.LEAVEHOUR,LeavehVO.LACTATIONHOUR };
	}
}
