package nc.bs.hrsms.ta.sss.calendar.ctrl;

import java.util.TimeZone;

import nc.bs.hrsms.ta.sss.calendar.WorkCalendarConsts;
import nc.bs.hrsms.ta.sss.common.ShopTAUtil;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.calendar.ctrl.CalendarForPsnViewMain;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.hr.utils.ResHelper;
import nc.itf.bd.shift.IShiftQueryService;
import nc.itf.hi.IPsndocQryService;
import nc.itf.ta.IPsnCalendarQueryMaintain;
import nc.itf.ta.algorithm.RelativeTimeUtils;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.jsp.uimeta.UIConstant;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UIPanel;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.RTVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalHoliday;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.psncalendar.PsnWorkTimeVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

public class ViewShiftDetailViewMain implements IController {
	// 参数--日期
	public static final String PARAM_CI_CALENDAR = "ci_calendar";

	/**
	 * 按时段/日历查看,显示班别详细
	 */
	public void showShift4Calendar(ScriptEvent event) {
		String pk_psndoc = AppLifeCycleContext.current().getParameter(CalendarForPsnViewMain.PARAM_CI_PK_PSNDOC);
		String calendar = AppLifeCycleContext.current().getParameter(PARAM_CI_CALENDAR);
		if (StringUtils.isEmpty(calendar)) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0168"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0022")/* @res "当前日期未排班!" */);
		}
		showClassDetail(AppUtil.getCntAppCtx(), pk_psndoc, new UFLiteralDate(calendar));
	}

	/**
	 * 显示班次详细
	 * 
	 * @param ctx
	 * @param pk_psndoc
	 * @param date
	 * @author haoy 2011-8-1
	 */
	public void showClassDetail(ApplicationContext ctx, String pk_psndoc, UFLiteralDate date) {

		PsndocAggVO psndocAggVO = null;
		try {
			IPsndocQryService psndocQryServ = ServiceLocator.lookup(IPsndocQryService.class);
			psndocAggVO = psndocQryServ.queryPsndocVOByPk(pk_psndoc);
			if (psndocAggVO == null) {
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0168"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0023")/* @res "指定人员已被删除！" */);
			}
		} catch (HrssException e) {
			e.alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}

		// 获取人员信息查询接口
		AggPsnCalendar psnCalendar = null;
		AggShiftVO aggShiftVO = null;
		try {
			IPsnCalendarQueryMaintain calendarQryServ = ServiceLocator.lookup(IPsnCalendarQueryMaintain.class);
			psnCalendar = calendarQryServ.queryByPsnDate(pk_psndoc, date);
			if (psnCalendar == null) {
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0168"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0024")/* @res "当前工作日历已被修改，请刷新界面！" */);
			}
			PsnCalendarVO calendarVO = psnCalendar.getPsnCalendarVO();
			IShiftQueryService shiftService = ServiceLocator.lookup(IShiftQueryService.class);
			aggShiftVO = shiftService.queryShiftAggVOByPk(calendarVO.getPk_shift());
			if (aggShiftVO == null) {
				CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0168"),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0025")/* @res "未找到指定班别！" */);
			}
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}

		// 人员姓名
		ctx.addAppAttribute(WorkCalendarConsts.SESSION_PSN_NAME, psndocAggVO.getParentVO().getMultiLangName());
		// 日期
		ctx.addAppAttribute(WorkCalendarConsts.SESSION_SELECTED_DATE, date);
		// 人员工作日历
		ctx.addAppAttribute(WorkCalendarConsts.SESSION_PSNCALENDAR_VO, psnCalendar);
		// 班次
		ctx.addAppAttribute(WorkCalendarConsts.SESSION_SHIFT_AGGVO, aggShiftVO);
		CommonUtil.showWindowDialog("ViewShiftDetail", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0026")/* @res "班次详情" */, "802", "700", null, ApplicationContext.TYPE_DIALOG, false, false);
	}

	/**
	 * 班次基本信息的数据集
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_dsShift(DataLoadEvent dataLoadEvent) {
		Dataset dsShift = dataLoadEvent.getSource();
		onDataLoad(dsShift);
	}

	private void onDataLoad(Dataset dsShift) {

		ApplicationContext appCtx = AppUtil.getCntAppCtx();
		// 人员姓名
		String psnName = (String) appCtx.getAppAttribute(WorkCalendarConsts.SESSION_PSN_NAME);
		// 日期
		UFLiteralDate date = (UFLiteralDate) appCtx.getAppAttribute(WorkCalendarConsts.SESSION_SELECTED_DATE);
		// 班次
		AggShiftVO aggShiftVO = (AggShiftVO) appCtx.getAppAttribute(WorkCalendarConsts.SESSION_SHIFT_AGGVO);
		// 人员工作日历
		AggPsnCalendar aggPsnCalendarVO = (AggPsnCalendar) appCtx.getAppAttribute(WorkCalendarConsts.SESSION_PSNCALENDAR_VO);

		LfwView viewDetal = ViewUtil.getCurrentView();
		
		String pk_org = aggShiftVO.getShiftVO().getPk_org();//班次业务单元
//		// 班次业务单元对应的人力资源组织
//		OrgVO hrorgVO = null;
//		try {
//			IAOSQueryService service = ServiceLocator.lookup(IAOSQueryService.class);
//			hrorgVO = service.queryHROrgByOrgPK(pk_org);
//		} catch (HrssException e) {
//			e.alert();
//		} catch (BusinessException e) {
//			new HrssException(e).deal();
//		}
//		String pk_hrorg = hrorgVO.getPk_org();
//		// 考勤规则
//		TimeRuleVO timeRuleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_hrorg);
		// 固定2位，不需要根据考勤规则变化
		Integer timedecimal = 2;
		
		// 设置班次基本信息
		setDsShiftBaseValue(viewDetal, dsShift, timedecimal, psnName, date, aggShiftVO, aggPsnCalendarVO.getPsnCalendarVO());

		// 获得休息时间VO
		RTVO[] rtVOs = aggShiftVO.getRTVOs();
		setDsRestTimeValue(rtVOs, viewDetal, pk_org, timedecimal);

		// 与假日冲突列表
		PsnCalHoliday[] holidayVOs = aggPsnCalendarVO.getPsnCalHolidayVO();
		seDstHolidayValue(holidayVOs, viewDetal);

		// 最终作息时间列表显示的值
		PsnWorkTimeVO[] wtVOs = null;
		if (!ArrayUtils.isEmpty(holidayVOs)) {
			wtVOs = aggPsnCalendarVO.getPsnWorkTimeVO();
		}
		setDsFinalWorkTimeValue(wtVOs, viewDetal);

		UIMeta uimeta = (UIMeta) AppUtil.getCntWindowCtx().getUiMeta();
		// 工休时间列表
		UIPanel panelvRestTime = (UIPanel) uimeta.findChildById("panellayout4174");
		if (ArrayUtils.isEmpty(rtVOs)) {
			if (panelvRestTime != null) {
				panelvRestTime.setExpand(UIConstant.FALSE);
			}
		} else {
			if (panelvRestTime != null) {
				panelvRestTime.setExpand(UIConstant.TRUE);
			}
		}
		// 与正常作息时间冲突的假日时间列表
		UIPanel panelvHoliday = (UIPanel) uimeta.findChildById("panellayout4661");
		if (ArrayUtils.isEmpty(holidayVOs)) {
			if (panelvHoliday != null) {
				panelvHoliday.setExpand(UIConstant.FALSE);
			}
		} else {
			if (panelvHoliday != null) {
				panelvHoliday.setExpand(UIConstant.TRUE);
			}
		}
		
		// 最终作息时间列表
		UIPanel panelvWorkTime = (UIPanel) uimeta.findChildById("panellayout0241");
		if (ArrayUtils.isEmpty(wtVOs) || ArrayUtils.isEmpty(holidayVOs)) {
			if (panelvWorkTime != null) {
				panelvWorkTime.setExpand(UIConstant.FALSE);
			}
		} else {
			if (panelvWorkTime != null) {
				panelvWorkTime.setExpand(UIConstant.TRUE);
			}
		}
	}

	/**
	 * 根据班次VO,设置班次数据集
	 * 
	 * @param dsShift
	 * @param shiftVO
	 * @return
	 */
	private void setDsShiftBaseValue(LfwView viewDetal, Dataset dsShift, Integer timedecimal, String psnName, UFLiteralDate date, AggShiftVO aggShiftVO, PsnCalendarVO calendarVO) {
		ShiftVO shiftVO = aggShiftVO.getShiftVO();
		
		String pk_org = shiftVO.getPk_org(); //业务单元
	
		Row row = dsShift.getEmptyRow();
		/** 基本信息 */
		// 人员姓名
		row.setValue(dsShift.nameToIndex("psnname"), psnName);
		// 日期
		row.setValue(dsShift.nameToIndex("calendar"), date);
		// 班次编码
		row.setValue(dsShift.nameToIndex("shiftcode"), shiftVO.getCode());
		// 班次名称
		row.setValue(dsShift.nameToIndex("shiftname"), shiftVO.getMultiLangName());
		// 班次类别
		row.setValue(dsShift.nameToIndex("pk_shifttype"), shiftVO.getPk_shifttype());
		// 业务单元
		row.setValue(dsShift.nameToIndex("pk_org"), pk_org);

		/** 考勤信息 */
		if (ShiftVO.PK_GX.equals(shiftVO.getPk_shift())) {// 班次等于公休
			//2013-7-30 最新需求：查看班次详情, 公休时不显示刷卡开始和结束的时间
			// 刷卡开始时间
//			row.setValue(dsShift.nameToIndex("timebegintime"), getDateTimeFormRelative(shiftVO.getTimebeginday(), "", pk_org));
			// 刷卡结束时间
//			row.setValue(dsShift.nameToIndex("timeendtime"), getDateTimeFormRelative(shiftVO.getTimeendday(), "", pk_org));
			// 上下班时间是否弹性
			row.setValue(dsShift.nameToIndex("isotflexible"), transferBoolen2String(shiftVO.getIsotflexible()));
			// 工作时长
			row.setValue(dsShift.nameToIndex("gzsj"), null);
			// 上班时间
			row.setValue(dsShift.nameToIndex("begintime"), null);
			// 下班时间
			row.setValue(dsShift.nameToIndex("endtime"), null);
			// 是否支持单次刷卡
			row.setValue(dsShift.nameToIndex("issinglecard"), null);
		} else {
			// 刷卡开始时间
			row.setValue(dsShift.nameToIndex("timebegintime"), getDateTimeFormRelative(shiftVO.getTimebeginday(), shiftVO.getTimebegintime(), pk_org));
			// 刷卡结束时间
			row.setValue(dsShift.nameToIndex("timeendtime"), getDateTimeFormRelative(shiftVO.getTimeendday(), shiftVO.getTimeendtime(), pk_org));
			// 上下班时间是否弹性
			row.setValue(dsShift.nameToIndex("isotflexible"), transferBoolen2String(shiftVO.getIsotflexible()));
			// 工作时长
			row.setValue(dsShift.nameToIndex("gzsj"), shiftVO.getGzsj().setScale(timedecimal, UFDouble.ROUND_HALF_UP));
			// 上班时间
			row.setValue(dsShift.nameToIndex("begintime"), getDateTimeFormRelative(shiftVO.getBeginday(), shiftVO.getBegintime(), pk_org));
			// 下班时间
			row.setValue(dsShift.nameToIndex("endtime"), getDateTimeFormRelative(shiftVO.getEndday(), shiftVO.getEndtime(), pk_org));
			// 是否支持单次刷卡
			row.setValue(dsShift.nameToIndex("issinglecard"), ShopTAUtil.transferBoolen2String(shiftVO));
		}

		// 工休时间是否弹性
		row.setValue(dsShift.nameToIndex("isrttimeflexible"), transferBoolen2String(shiftVO.getIsrttimeflexible()));
		// 遇假日排班是否取消
		row.setValue(dsShift.nameToIndex("cancelflag"), transferBoolen2String(UFBoolean.valueOf(calendarVO.isHolidayCancel())));

		FormComp frmshift = (FormComp) viewDetal.getViewComponents().getComponent("frmshift");
		FormElement earlyBeginTimeElem = frmshift.getElementById("earlyBeginTime");
		FormElement lateEndTimeElem = frmshift.getElementById("lateEndTime");
		FormElement lateBeginTimeElem = frmshift.getElementById("lateBeginTime");
		FormElement earlyEndTimeElem = frmshift.getElementById("earlyEndTime");
		FormElement begintimeElem = frmshift.getElementById("begintime");
		FormElement endtimeElem = frmshift.getElementById("endtime");
		if (shiftVO.getIsotflexible().booleanValue()) {// 上下班时间弹性是否弹性
			earlyBeginTimeElem.setVisible(true);
			lateEndTimeElem.setVisible(true);
			lateBeginTimeElem.setVisible(true);
			earlyEndTimeElem.setVisible(true);
			begintimeElem.setVisible(false);
			endtimeElem.setVisible(false);
		} else {
			earlyBeginTimeElem.setVisible(false);
			lateEndTimeElem.setVisible(false);
			lateBeginTimeElem.setVisible(false);
			earlyEndTimeElem.setVisible(false);
			begintimeElem.setVisible(true);
			endtimeElem.setVisible(true);
		}

		// 最早上班时间
		row.setValue(dsShift.nameToIndex("earlyBeginTime"), getDateTimeFormRelative(shiftVO.getBeginday(), shiftVO.getBegintime(), pk_org));
		// 最晚下班时间
		row.setValue(dsShift.nameToIndex("lateEndTime"), getDateTimeFormRelative(shiftVO.getEndday(), shiftVO.getEndtime(), pk_org));
		// 最晚上班时间
		row.setValue(dsShift.nameToIndex("lateBeginTime"), getDateTimeFormRelative(shiftVO.getLatestbeginday(), shiftVO.getLatestbegintime(), pk_org));
		// 最早下班时间
		row.setValue(dsShift.nameToIndex("earlyEndTime"), getDateTimeFormRelative(shiftVO.getEarliestendday(), shiftVO.getEarliestendtime(), pk_org));

		// 是否夜班
		row.setValue(dsShift.nameToIndex("includenightshift"), transferBoolen2String(shiftVO.getIncludenightshift()));
		// 夜班开始时间
		row.setValue(dsShift.nameToIndex("nightbegintime"), getDateTimeFormRelative(shiftVO.getNightbeginday(), shiftVO.getNightbegintime(), pk_org));
		// 夜班结束时间
		row.setValue(dsShift.nameToIndex("nightendtime"), getDateTimeFormRelative(shiftVO.getNightendday(), shiftVO.getNightendtime(), pk_org));
		dsShift.addRow(row);
		dsShift.setRowSelectIndex(0);
	}

	/**
	 * 根据工休时间VO,设置工休时间数据集的值
	 * 
	 * @param rtVOs
	 * @param viewDetal
	 * @param pk_org
	 *            班次所在业务单元
	 * @param timedecimal 考勤规则定义的小数位数
	 */
	private void setDsRestTimeValue(RTVO[] rtVOs, LfwView viewDetal, String pk_org, Integer timedecimal) {
		// 班次数据集
		Dataset dsRestTime = viewDetal.getViewModels().getDataset("dsRestTime");
		Row rtRow = null;
		if (rtVOs != null && rtVOs.length > 0) {
			for (int j = 0; j < rtVOs.length; j++) {
				RTVO rtVO = rtVOs[j];
				rtRow = dsRestTime.getEmptyRow();
				// 开始时间
				rtRow.setValue(dsRestTime.nameToIndex("begintime"), getDateTimeFormRelative(rtVO.getBeginday(), rtVO.getBegintime(), pk_org));
				// 结束时间
				rtRow.setValue(dsRestTime.nameToIndex("endtime"), getDateTimeFormRelative(rtVO.getEndday(), rtVO.getEndtime(), pk_org));
				// 休息时长
				rtRow.setValue(dsRestTime.nameToIndex("resttime"), rtVO.getResttime().setScale(timedecimal, UFDouble.ROUND_HALF_UP));
				// 是否刷卡
				rtRow.setValue(dsRestTime.nameToIndex("checkflag"), transferBoolen2String(rtVO.getCheckflag()));
				dsRestTime.addRow(rtRow);
			}
		}
	}

	/**
	 * 根据冲突假日VO,设置冲突假日数据集的值
	 * 
	 * @param holidayVOs
	 * @param dsHoliday
	 */
	private void seDstHolidayValue(PsnCalHoliday[] holidayVOs, LfwView viewDetal) {

		Dataset dsHoliday = viewDetal.getViewModels().getDataset("dsPsnCalHoliday");

		Row holidayRow = null;
		if (holidayVOs != null && holidayVOs.length > 0) {
			for (PsnCalHoliday holidayVO : holidayVOs) {
				holidayRow = dsHoliday.getEmptyRow();
				// 开始时间
				holidayRow.setValue(dsHoliday.nameToIndex("beginTime"), holidayVO.getBeginTime());
				// 结束时间
				holidayRow.setValue(dsHoliday.nameToIndex("endTime"), holidayVO.getEndTime());
				// 节日名称
				holidayRow.setValue(dsHoliday.nameToIndex("holidayName"), holidayVO.getHolidayName());
				dsHoliday.addRow(holidayRow);
			}
		}
	}

	/**
	 * 根据员工工作时间VO,设置最终员工工作时间数据集的值
	 * 
	 * @param wtVOs
	 * @param dsWorkTime
	 */
	private void setDsFinalWorkTimeValue(PsnWorkTimeVO[] wtVOs, LfwView viewDetal) {
		// 最终作息时间列表
		Dataset dsWorkTime = viewDetal.getViewModels().getDataset("dsPsnWorkTime");

		Row workTimeRow = null;
		if (wtVOs != null && wtVOs.length > 0) {
			for (PsnWorkTimeVO workTimeVO : wtVOs) {
				workTimeRow = dsWorkTime.getEmptyRow();
				// 开始时间
				workTimeRow.setValue(dsWorkTime.nameToIndex("kssj"), workTimeVO.getKssj());
				// 结束时间
				workTimeRow.setValue(dsWorkTime.nameToIndex("jssj"), workTimeVO.getJssj());
				// 上班刷卡
				workTimeRow.setValue(dsWorkTime.nameToIndex("checkinflag"), transferBoolen2String(workTimeVO.getCheckinflag()));
				// 下班刷卡
				workTimeRow.setValue(dsWorkTime.nameToIndex("checkoutflag"), transferBoolen2String(workTimeVO.getCheckoutflag()));
				dsWorkTime.addRow(workTimeRow);
			}
		}
	}

	/**
	 * 由相对时间和当前日期获取绝对时间
	 * 
	 * @param day
	 *            日期
	 * @param time
	 *            时间
	 * @param pk_org
	 *            业务单元
	 * @return
	 */
	private UFDateTime getDateTimeFormRelative(Integer day, String time, String pk_org) {
		if (day == null || StringUtils.isEmpty(time) || StringUtils.isEmpty(pk_org)) {
			return null;
		}
		// 业务单元所在时区
		TimeZone timeZone = TBMPsndocUtil.getTimeZoneByOrg(pk_org);
		// 日期
		UFLiteralDate selectdate = (UFLiteralDate) AppUtil.getAppAttr(WorkCalendarConsts.SESSION_SELECTED_DATE);
		return RelativeTimeUtils.toDateTime(day, time, selectdate.toStdString(), timeZone);
	}

	/**
	 * 将布尔类型转换为是/否
	 * 
	 * @param isotflexible
	 * @return
	 */
	private String transferBoolen2String(UFBoolean isotflexible) {
		if (isotflexible.booleanValue()) {
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0027")/* @res "是" */;
		}
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0028")/* @res "否" */;
	}


}
