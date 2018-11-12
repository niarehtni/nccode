package nc.bs.hrsms.ta.sss.calendar;

import nc.hr.utils.ResHelper;

public class WorkCalendarConsts {

	// ���ܽڵ��
	public static final String FUNC_CODE = "E20600903";
	
	// ҳ��-�����ݼ�����
	public static final String PAGE_MAIN_DS = "dsCalendar";
	// ҳ��-��������
	public static final String PAGE_MAIN_GRID = "gridPsnCalendar";

	// ��ѯ����-��ʼ�����ֶ�
	public static final String FD_BEGINDATE = "begindate";
	// ��ѯ����-���������ֶ�
	public static final String FD_ENDDATE = "enddate";
	
	// ��ѯ����-�Ű������ֶ�
	public static final String FD_ARRANGEFLAG = "arrangeflag";
	// ���ڷ����仯�Ĳ���
	public static final String SESSION_DATE_CHANGE = "ssscalendar_mng_datechange";
	// �鿴��ʽ�л�ʱ�����ѯ����
	public static final String SESSION_QRY_CONDITIONS = "ssscalendar_mng_qry_conditions";
	// ʹ�ò鿴��ʽ�л���ģʽ����ҳ��ı�ʶ
	public static final String SESSION_CATAGORY_ACCESS = "ssscatagory_access";

	// ��ѯ��Χ��������Ա����δ�Ű���Ա���Ű������Ա�������Ű���Ա
	public static final int QUERYSCOPE_ALL = 1;
	public static final int QUERYSCOPE_NOT = 2;
	public static final int QUERYSCOPE_COMPLETE = 3;
	public static final int QUERYSCOPE_PART = 4;

	// �鿴�����Session key
	public static final String SESSION_PSNCALENDAR_VO = "sess_psncalendar_vo";
	public static final String SESSION_SHIFT_AGGVO = "sess_shift_aggvo";
	public static final String SESSION_SELECTED_DATE = "sess_selected_date";
	public static final String SESSION_PSN_NAME = "sess_psn_name";
	/**��ʱ�β鿴app*/
	public static final String WORKCALENDARAPP_TIME = "WorkCalendarApp";
	/**�������鿴app*/
	public static final String WORKCALENDARAPP_CAL = "WorkCalendarForPsnApp";
	
	
	// ѭ���Ű���Ա���ݼ�
	public static final String DS_PSN = "dsPsn";
	// ѭ���Ű�ڵ��
	public static final String FUNCODE_CIRCLEARRANGESHIFT = "Funcode_CircleArrangeShift";
	// �Ű�����ݼ�ID
	public static final String DS_CIRCLEARRANGESHIFT = "dsCircleArrangeShift";
	// �����������ݼ�ID
	public static final String DS_WORKPERIOD = "dsWorkPeriod";
	
	// ���鶨��ά���ڵ��
	public static final String TEAMMAINTAIN_FUNCODE = "E20400913";
	// ���鹤�������ڵ��
	public static final String TEAMCALENDAR_FUN_CODE = "E20400916";
	
	// �������ݼ�
	public static final String DS_TEAM = "dsTeamMaintain"; 
	// ��������Ա���ݼ�
	public static final String DS_TEAM_PSN = "dsTeamPsn";
	
	
	public static final String SHIFTPK_GX = "0001Z7000000000000GX";
	
	public static final String SHIFTPK_KB = "0001Z7000000000000EM";
	
	public static final String SHIFTPK_GDJ = "0001Z7000000000000NH";
	
	public static final String SHIFTPK_LJ = "0001Z7000000000000HD";
	
	public static final String SHIFTPK_XXR = "0001Z7000000000000OD";
	
}
