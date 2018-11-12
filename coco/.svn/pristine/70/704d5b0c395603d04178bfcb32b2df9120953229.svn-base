/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package nc.ui.ta.calendar.pub;
import java.awt.Color;

import nc.ui.ta.pub.IColorConst;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

public class CalendarColorUtils {

	/**
	 * 取得某人某一天的单元格的颜色信息
	 * @return
	 */
	public static Color getDateColor(String date,PsnJobCalendarVO jobCalendarVO){
		if(!jobCalendarVO.isEffectiveDate(date)){//如果是无考勤档案的日期
			return IColorConst.COLOR_NONTBMPSNDOC;
		}
		Integer dateType = jobCalendarVO.getDayTypeMap().get(date);
		if(dateType==null||dateType==HolidayVO.DAY_TYPE_WORKDAY){//工作日--0
			return nc.ui.bd.holiday.IColorConst.COLOR_WORKDAY;
		}
		if(dateType==HolidayVO.DAY_TYPE_NONWORKDAY){//非工作日--1
			return nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY;
		}
		if(dateType==HolidayVO.DAY_TYPE_HOLIDAY){//假日--2 (对应国定假日)
			return nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY;
		}
		//Ares.Tank 2018-9-3 15:16:49  日历天类型对应的日期类型 例假日
		if(dateType==HolidayVO.DAY_TYPE_OFFICAL_HOLIDAY){//例假日
			return nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY;
		}
		return null;
	} 
	
	/**
	 * 取得某班组某一天的单元格的颜色信息
	 * @return
	 */
	public static Color getDateColor(String date,TeamInfoCalendarVO teamCalendarVO){
		Integer dateType = teamCalendarVO.getDayTypeMap().get(date);
		if(dateType==null || HolidayVO.DAY_TYPE_WORKDAY==dateType.intValue()){ //工作日
			return nc.ui.bd.holiday.IColorConst.COLOR_WORKDAY;
		}
		if(dateType==HolidayVO.DAY_TYPE_NONWORKDAY){//公休
			return nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY;
		}
		if(dateType==HolidayVO.DAY_TYPE_HOLIDAY){//假日
			return nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY;
		}
		if(dateType==HolidayVO.DAY_TYPE_HOLIDAY_NOTALL){//非全员假日
			return nc.ui.bd.holiday.IColorConst.COLOR_NOTALLHOLIDAY;
		}
		// Ares.Tank 2018-9-3 15:16:49 日历天类型对应的日期类型 例假日
		if (dateType == HolidayVO.DAY_TYPE_OFFICAL_HOLIDAY) {// 例假日
			return nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY;
		}
		return null;
	}
}
