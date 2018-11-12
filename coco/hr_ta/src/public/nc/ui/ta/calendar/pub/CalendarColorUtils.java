/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package nc.ui.ta.calendar.pub;
import java.awt.Color;

import nc.ui.ta.pub.IColorConst;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

public class CalendarColorUtils {

	/**
	 * ȡ��ĳ��ĳһ��ĵ�Ԫ�����ɫ��Ϣ
	 * @return
	 */
	public static Color getDateColor(String date,PsnJobCalendarVO jobCalendarVO){
		if(!jobCalendarVO.isEffectiveDate(date)){//������޿��ڵ���������
			return IColorConst.COLOR_NONTBMPSNDOC;
		}
		Integer dateType = jobCalendarVO.getDayTypeMap().get(date);
		if(dateType==null||dateType==HolidayVO.DAY_TYPE_WORKDAY){//������--0
			return nc.ui.bd.holiday.IColorConst.COLOR_WORKDAY;
		}
		if(dateType==HolidayVO.DAY_TYPE_NONWORKDAY){//�ǹ�����--1
			return nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY;
		}
		if(dateType==HolidayVO.DAY_TYPE_HOLIDAY){//����--2 (��Ӧ��������)
			return nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY;
		}
		//Ares.Tank 2018-9-3 15:16:49  ���������Ͷ�Ӧ���������� ������
		if(dateType==HolidayVO.DAY_TYPE_OFFICAL_HOLIDAY){//������
			return nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY;
		}
		return null;
	} 
	
	/**
	 * ȡ��ĳ����ĳһ��ĵ�Ԫ�����ɫ��Ϣ
	 * @return
	 */
	public static Color getDateColor(String date,TeamInfoCalendarVO teamCalendarVO){
		Integer dateType = teamCalendarVO.getDayTypeMap().get(date);
		if(dateType==null || HolidayVO.DAY_TYPE_WORKDAY==dateType.intValue()){ //������
			return nc.ui.bd.holiday.IColorConst.COLOR_WORKDAY;
		}
		if(dateType==HolidayVO.DAY_TYPE_NONWORKDAY){//����
			return nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY;
		}
		if(dateType==HolidayVO.DAY_TYPE_HOLIDAY){//����
			return nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY;
		}
		if(dateType==HolidayVO.DAY_TYPE_HOLIDAY_NOTALL){//��ȫԱ����
			return nc.ui.bd.holiday.IColorConst.COLOR_NOTALLHOLIDAY;
		}
		// Ares.Tank 2018-9-3 15:16:49 ���������Ͷ�Ӧ���������� ������
		if (dateType == HolidayVO.DAY_TYPE_OFFICAL_HOLIDAY) {// ������
			return nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY;
		}
		return null;
	}
}
