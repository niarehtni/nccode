package nc.bs.hrsms.ta.sss.calendar;

import nc.hr.utils.ResHelper;

public class WorkCalendarConsts {

	// 功能节点号
	public static final String FUNC_CODE = "E20600903";
	
	// 页面-主数据集名称
	public static final String PAGE_MAIN_DS = "dsCalendar";
	// 页面-表格的名称
	public static final String PAGE_MAIN_GRID = "gridPsnCalendar";

	// 查询条件-开始日期字段
	public static final String FD_BEGINDATE = "begindate";
	// 查询条件-结束日期字段
	public static final String FD_ENDDATE = "enddate";
	
	// 查询条件-排班条件字段
	public static final String FD_ARRANGEFLAG = "arrangeflag";
	// 日期发生变化的参数
	public static final String SESSION_DATE_CHANGE = "ssscalendar_mng_datechange";
	// 查看方式切换时保存查询条件
	public static final String SESSION_QRY_CONDITIONS = "ssscalendar_mng_qry_conditions";
	// 使用查看方式切换的模式访问页面的标识
	public static final String SESSION_CATAGORY_ACCESS = "ssscatagory_access";

	// 查询范围：所有人员，尚未排班人员，排班完毕人员，部分排班人员
	public static final int QUERYSCOPE_ALL = 1;
	public static final int QUERYSCOPE_NOT = 2;
	public static final int QUERYSCOPE_COMPLETE = 3;
	public static final int QUERYSCOPE_PART = 4;

	// 查看详情的Session key
	public static final String SESSION_PSNCALENDAR_VO = "sess_psncalendar_vo";
	public static final String SESSION_SHIFT_AGGVO = "sess_shift_aggvo";
	public static final String SESSION_SELECTED_DATE = "sess_selected_date";
	public static final String SESSION_PSN_NAME = "sess_psn_name";
	/**按时段查看app*/
	public static final String WORKCALENDARAPP_TIME = "WorkCalendarApp";
	/**按日历查看app*/
	public static final String WORKCALENDARAPP_CAL = "WorkCalendarForPsnApp";
	
	
	// 循环排班人员数据集
	public static final String DS_PSN = "dsPsn";
	// 循环排班节点号
	public static final String FUNCODE_CIRCLEARRANGESHIFT = "Funcode_CircleArrangeShift";
	// 排班表单数据集ID
	public static final String DS_CIRCLEARRANGESHIFT = "dsCircleArrangeShift";
	// 工作周期数据集ID
	public static final String DS_WORKPERIOD = "dsWorkPeriod";
	
	// 班组定义维护节点号
	public static final String TEAMMAINTAIN_FUNCODE = "E20400913";
	// 班组工作日历节点号
	public static final String TEAMCALENDAR_FUN_CODE = "E20400916";
	
	// 班组数据集
	public static final String DS_TEAM = "dsTeamMaintain"; 
	// 班组下人员数据集
	public static final String DS_TEAM_PSN = "dsTeamPsn";
	
	
	public static final String SHIFTPK_GX = "0001Z7000000000000GX";
	
	public static final String SHIFTPK_KB = "0001Z7000000000000EM";
	
	public static final String SHIFTPK_GDJ = "0001Z7000000000000NH";
	
	public static final String SHIFTPK_LJ = "0001Z7000000000000HD";
	
	public static final String SHIFTPK_XXR = "0001Z7000000000000OD";
	
}
