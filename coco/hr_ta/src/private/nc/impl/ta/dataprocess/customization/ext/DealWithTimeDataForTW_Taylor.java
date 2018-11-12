package nc.impl.ta.dataprocess.customization.ext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.itf.ta.algorithm.ICheckTime;
import nc.vo.om.orginfo.HROrgVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.importdata.ImportDataVO;
import nc.vo.ta.psncalendar.PsnWorkTimeVO;

/**
 * @author Taylor Duan
 * @version 创建时间：2014-7-15 上午11:49:13 类说明
 */
public class DealWithTimeDataForTW_Taylor {
	public String OrgAllowLateFileName = "glbdef4";
	public String OrgArriveEarlyFileName = "glbdef5";
	public String OrgLeaveLateFileName = "glbdef6";
	
	private UFDateTime firstBeginTime; // 第一个Shift时间段上班时间
	private UFDateTime lastEndTime; // 最后一个Shift时间段下班时间
	private UFDateTime machineBeginTime; // 考勤机开始接受刷卡时间
	private UFDateTime machineEndTime; // 考勤机结束接受刷卡时间
	private String pk_psndoc;
	private boolean isAllDayLeave; // 是否是全天假期
	private boolean isSeparateOT = false; // 是否是不链接的OT
	private final String pk_typhoon_ot = "1001AA10000000037W4I"; // 颱風加班類型pk
	private boolean hasTyphoonOT = false;
	private UFDateTime arriveOTorLeaveTime;// 上班OT打卡時間
	private UFDateTime departOTorLeaveTime;// 下班OT打卡時間

	
	/**
	 * 获取中间时间点
	 * 
	 * @author Taylor Duan
	 * @version 创建时间：2014-7-15 上午11:39:02
	 * @param formerTime
	 * @param seconds
	 * @return
	 */
	private UFDateTime getMiddleTime(UFDateTime formerTime, int seconds, String operation) {
		Calendar c = Calendar.getInstance();
		setCalendar(formerTime.toString(), c);
		if ("add".equals(operation)) {
			c.add(Calendar.SECOND, seconds);
		} else if ("minus".equals(operation)) {
			c.add(Calendar.SECOND, -seconds);
		}

		UFDateTime middleTime = new UFDateTime(c.getTime());
		return middleTime;

	}

	/**
	 * 设置时间
	 * 
	 * @author Taylor Duan
	 * @version 创建时间：2014-7-15 上午11:33:45
	 * @param time
	 * @param c
	 * @return
	 */
	private void setCalendar(String time, Calendar c) {
		int year = Integer.parseInt(time.substring(0, 4));
		int month = Integer.parseInt(time.substring(5, 7)) - 1;
		int date = Integer.parseInt(time.substring(8, 10));
		int hour = Integer.parseInt(time.substring(11, 13));
		int minute = Integer.parseInt(time.substring(14, 16));
		int second = Integer.parseInt(time.substring(17, 19));
		c.set(year, month, date, hour, minute, second);
	}

	/**
	 * 获取搜索时间段 List
	 * 
	 * @author Taylor Duan
	 * @version 创建时间：2014-7-15 下午12:25:28
	 * @param midTimes
	 * @return
	 */
	private List<UFDateTime> getSearchPeriodList(List<UFDateTime> midTimes) {
		List<UFDateTime> searchPeriods = new ArrayList<UFDateTime>();
		searchPeriods.add(machineBeginTime);
		searchPeriods.addAll(midTimes);
		searchPeriods.add(machineEndTime);
		return searchPeriods;

	}

	

	

	/**
	 * 初始化考勤机开始结束接收打卡时间
	 * 
	 * @author Taylor Duan
	 * @version 创建时间：2014-7-15 下午4:19:08
	 * @param startTime
	 * @param endTime
	 */
	private void initMachineTimes(ShiftVO shiftVO) {
		machineBeginTime = new UFDateTime(shiftVO.getTimebeginday() == 0?firstBeginTime.toString().substring(0, 10) + " "
				+ shiftVO.getTimebegintime():firstBeginTime.getDateTimeBefore(1).toString().substring(0, 10) + " "
						+ shiftVO.getTimebegintime());
		machineEndTime = new UFDateTime(shiftVO.getTimeendday() == 0 ? firstBeginTime.toString().substring(0,
				10) + " " + shiftVO.getTimeendtime(): firstBeginTime.getDateTimeAfter(1).toString().substring(0, 10) + " " + shiftVO.getTimeendtime());
		// Calendar beginC = Calendar.getInstance();
		// setCalendar(startTime.toString(), beginC);
		// beginC.add(Calendar.HOUR, -3);
		// machineBeginTime = new UFDateTime(beginC.getTime());
		//
		// Calendar endC = Calendar.getInstance();
		// setCalendar(endTime.toString(), endC);
		// endC.add(Calendar.HOUR, 3);
		// machineEndTime = new UFDateTime(endC.getTime());
	}

	

	private int getMinutesBetweenForTW(UFDateTime begin, UFDateTime end)
	{
		//翠华计算时只精确到分钟
		String beginStr = begin.toString().trim();
		String endStr = end.toString().trim();
		if(beginStr.length()>=2 && endStr.length()>=2)
		{
			int index = beginStr.length() - 2;
			String minuteStr = beginStr.substring(0, index);
			UFDateTime minuteBegin = new UFDateTime(minuteStr + "00");

			int endIndex = endStr.length() - 2;
			String minuteEndStr = endStr.substring(0, endIndex);
			UFDateTime minuteEnd = new UFDateTime(minuteEndStr + "00");
			return UFDateTime.getMinutesBetween(minuteBegin, minuteEnd);
		}
		
	    return UFDateTime.getMinutesBetween(begin, end);
	}
	
	

	/**
	 * 异常报告逻辑统一入口
	 * 
	 * @param workVOs
	 * @param pk_psndoc
	 * @return
	 */
	public void dealWithCheckTimes(String pk_org, ICheckTime[][] checkTimes) {
		if(null==pk_org || null==checkTimes || checkTimes.length<=0){
			return;
		}
//		boolean isHKOrg = false;
//		// “香港”国家地区PK为'0001Z010000000079UIP'
//		String orgSql = "select pk_org from org_orgs where pk_org='" + pk_org + "' and countryzone='0001Z010000000079UIP'";
//		DataAccessUtils dutil = new DataAccessUtils();
//		IRowSet rs = dutil.query(orgSql);
//		while (rs.next()) {
//			if (null != rs.getString(0)) {
//				isHKOrg = true;
//			}
//		}
//		if(!isHKOrg){
//			return;
//		}
		for(ICheckTime[] checkTimeArr : checkTimes){
			if(null==checkTimeArr || checkTimeArr.length<=0){
				continue;
			}
			for(ICheckTime checkTime : checkTimeArr){
				//翠华计算时只精确到分钟
				String beginStr = checkTime.getDatetime().toString().trim();
				if(beginStr.length()>=2 )
				{
					int index = beginStr.length() - 2;
					if(beginStr.substring(index, index+2).equals("00")){
						continue;
					}
					else{
						String minuteStr = beginStr.substring(0, index);
						UFDateTime minuteTime = new UFDateTime(minuteStr + "00");
//						checkTime.setDatetime(minuteTime);
						if(checkTime instanceof ImportDataVO){
							ImportDataVO dataVO = (ImportDataVO)checkTime;
							dataVO.setCalendartime(minuteTime);
						}
					}
					
				}
			}
		}
	}
}