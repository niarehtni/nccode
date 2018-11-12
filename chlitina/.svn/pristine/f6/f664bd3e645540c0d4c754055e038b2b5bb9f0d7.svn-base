package nc.itf.ta;

import java.util.Map;

import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

public interface IPsnCalendarManageService {

	/**
	 * 新增假日后排班。如果循环排班时，选择的是遇假日取消，则这些取消的排班需要根据新的假日情况重排
	 * @param pk_org
	 * @param pk_holiday
	 * @throws BusinessException
	 */
	void arrangeAfterHolidayInsert(String pk_org,String pk_holiday) throws BusinessException;
	
	/**
	 * 新增假日后排班。如果循环排班时，选择的是遇假日取消，则这些取消的排班需要根据新的假日情况重排
	 * @param orgVO
	 * @param newHoliday
	 * @throws BusinessException
	 */
	void arrangeAfterHolidayInsert(OrgVO orgVO,HolidayVO newHoliday) throws BusinessException;

	/**
	 * 修改假日后排班
	 * @param pk_org
	 * @param pk_holiday
	 * @throws BusinessException
	 */
	void arrangeAfterHolidayUpdate(String pk_org, HolidayVO oldHolidayVO,String pk_holiday) throws BusinessException;
	
	/**
	 * 修改假日后排班
	 * @param orgVO
	 * @param newHoliday
	 * @throws BusinessException
	 */
	void arrangeAfterHolidayUpdate(OrgVO orgVO, HolidayVO oldHolidayVO,HolidayVO newHoliday) throws BusinessException;

	/**
	 * 删除假日前排班
	 * @param pk_org
	 * @param pk_holiday
	 * @throws BusinessException
	 */
	void arrangeBeforeHolidayDelete(String pk_org,String pk_holiday) throws BusinessException;
	
	/**
	 * 删除假日前排班
	 * @param orgVO
	 * @param deleteHoliday
	 * @throws BusinessException
	 */
	void arrangeBeforeHolidayDelete(OrgVO orgVO,HolidayVO deleteHoliday) throws BusinessException;

	/**
	 * 自动排班
	 * @param pk_hrorg
	 * @throws BusinessException
	 */
	String autoArrange_RequiresNew(String pk_hrorg) throws BusinessException;

	/**
	 * 对新加入考勤档案的人员进行自动排班
	 * @param pk_hrorg
	 * @param pk_psndoc
	 * @throws BusinessException
	 */
	void autoArrange(String pk_hrorg,String pk_psndoc) throws BusinessException;
	
	void autoArrange(String pk_hrorg, String pk_psndoc,TBMPsndocVO[] psndocVOs) throws BusinessException;

	/**
	 * 对新加入考勤档案的人员进行自动排班
	 * @param pk_org
	 * @param pk_psndocs
	 * @throws BusinessException
	 */
	void autoArrange(String pk_hrorg,String[] pk_psndocs) throws BusinessException;
	
	/**
	 * 调班单据审批通过后调整排班。因为工作日历组件的编译顺序在调班组件之前，为了避免循环依赖，将参数
	 * 声明为AggregatedValueObject，而不是AggChangeClassVO
	 * @param pk_hrorg
	 * @param pk_changechasshs
	 * @throws BusinessException
	 */
	void arrangeAfterApprove(String pk_hrorg,AggregatedValueObject[] aggBills)throws BusinessException;
	
	/**
	 * 新增多个调班登记后调整排班，用于批量新增调班登记
	 * 因为工作日历组件的编译顺序在调班组件之前，为了避免循环依赖，将参数声明为supervo，而不是ChangeClassRegVO
	 * @param pk_hrorg
	 * @param regVOs
	 * @throws BusinessException
	 */
	void arrangeAfterRegister(String pk_hrorg,SuperVO[] regVOs)throws BusinessException;
	
	/**
	 * 新增调班登记后调整排班
	 * @param pk_hrorg
	 * @param regVO
	 * @throws BusinessException
	 */
	void arrangeAfterRegister(String pk_hrorg,SuperVO regVO)throws BusinessException;
	
	/**
	 * 修改考勤档案保存前，调用此方法
	 * @param pk_hrorg
	 * @param beforeUpdateVOs
	 * @throws BusinessException
	 */
	void processBeforeUpdateTBMPsndoc(String pk_hrorg,SuperVO[] updateVOs)throws BusinessException;
	
	/**
	 * 删除考勤档案前，调用此方法
	 * @param pk_hrorg
	 * @param pk_tbmpsndocs
	 * @throws BusinessException
	 */
	void processBeforeDeleteTBMPsnodc(String pk_hrorg,String[] pk_tbmpsndocs)throws BusinessException;
	
	/**
	 * 在删除班次前调用此方法，用于删除引用此班次的工作日历
	 * @param pk_shift
	 * @throws BusinessException
	 */
	void processBeforeDeleteShift(String pk_shift)throws BusinessException;
	
	/**
	 * 在修改班次后修改此方法，用于调整引用此班次的工作日历
	 * @param pk_org
	 * @param oldShiftVO
	 * @param pk_shift
	 * @throws BusinessException
	 */
	void processAfterUpdateShift(AggShiftVO oldShiftVO,String pk_shift)throws BusinessException;
	
	/**
	 * 根据日期范围，人员条件删除工作日历
	 * @param pk_org
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	void deleteByCondAndDateScope(String pk_org,FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 将人员的工作日历与班组日历同步，用于班组排班节点的同步按钮
	 * @param pk_team
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	PsnJobCalendarVO[] sync2TeamCalendar(String pk_hrorg, String pk_team,String[] pk_psndocs,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
	
	/**
	 * 自助使用，同步班组下所有人员
	 * @param pk_team
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	PsnJobCalendarVO[] sync2TeamCalendar(String pk_hrorg, String pk_team,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;

	/**
	 * 班组排班修改保存后，同步人员工作日历
	 * @param teamVOs
	 * @param syncPrivateDates，是否同步在班组排班后又修改了的人员日历
	 * @throws BusinessException
	 */
	void sync2TeamCalendarAfterSave(String pk_hrorg, TeamInfoCalendarVO[] teamVOs)throws BusinessException;
	
	/**
	 * 班组循环排班后，同步人员工作日历
	 * @param pk_org，业务单元主键
	 * @param modifiedMap <班组主键, <日期, 班次主键>>
	 * @param beginDate
	 * @param endDate
	 * @param calendarPks
	 * @param isHolidayCancel
	 * @param syncPrivateDates，是否同步在班组排班后又修改了的人员日历
	 * @throws BusinessException
	 */
	void sync2TeamCalendarAfterCircularlyArrange(String pk_org,Map<String, Map<String, String>> modifiedMap,
			UFLiteralDate beginDate,UFLiteralDate endDate,boolean isHolidayCancel)throws BusinessException;

}
