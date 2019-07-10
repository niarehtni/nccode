package nc.itf.ta;
 
import java.util.List;

import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.teamcalendar.AggTeamCalendarVO;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

public interface ITeamCalendarManageMaintain {
	/**
	 * 批量调班
	 * @param pk_org,HR组织主键
	 * @param beginDate
	 * @param endDate
	 * @param condition
	 * @param oldShift，原班次。如果为空，则将符合条件的所有班次都调整为新班次；否则只是将符合条件的且原班次为oldShift的调整为新班次
	 * @param newShift，新班次
	 * @param withOldShift,是否考虑原班次。为true，表示原班次为oldShift的才调整，为false，表示不管原班次是什么，都调整
	 */
	void batchChangeShift(String pk_org,FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate,boolean withOldShift,String oldShift,String newShift) throws BusinessException;
	
	//修改后带向导的批量调班
	void batchChangeShiftNew(String pk_org, String[] bz_pks,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			boolean withOldShift, String oldShift, String newShift)
			throws BusinessException;
	
	/**
	 * 循环排班
	 * @param pk_org,HR组织主键
	 * @param beginDate，开始日期
	 * @param endDate，结束日期
	 * @param condition，循环排班人员条件
	 * @param calendarPks，班次主键数组
	 * @param isHolidayCancel，遇假日排班是否取消，true取消，false照旧
	 * @return
	 */
	TeamInfoCalendarVO[]
	circularArrange(String pk_org,
			String[] pk_teams,UFLiteralDate beginDate,UFLiteralDate endDate,String[] calendarPks,boolean isHolidayCancel,boolean overrideExistCalendar, boolean needLog) throws BusinessException;
	/**
	 * 保存工作日历，用于工作日历的首届面保存
	 * ,TeamInfoCalendarVO[]中只存储变化了的班组
	 * TeamInfoCalendarVO的map中只存储变化了的日历
	 * 
	 * @param pk_org,HR组织主键
	 * @param busilog,是否记录业务日志
	 */
	TeamInfoCalendarVO[]   
	save(String pk_org,TeamInfoCalendarVO[] vos,boolean busilog) throws BusinessException;

	
	/**
	 * 保存工作日历，用于班组工作日历设置节点的修改保存
	 * ,TeamInfoCalendarVO[]中只存储变化了的班组
	 * TeamInfoCalendarVO的map中只存储变化了的日历</br>
	 * 在班组工作日历设置节点，需求是点击修改按钮，只修改班组排版，不修改日历天类型</br>
	 * 此方法仿造Save方法，不同的是此方法把班组工作日历里原来的日历天类型重新插入了表里，
	 * 相当于不更新日历天类型
	 * @param pk_org,HR组织主键
	 * @param busilog,是否记录业务日志
	 */
	TeamInfoCalendarVO[]   
			saveNODateype(String pk_org,TeamInfoCalendarVO[] vos,boolean busilog) throws BusinessException;
	
	
	/**
	 * 保存工作日历，用于按日历排班
	 * @param pk_org,HR组织主键
	 * TeamInfoCalendarVO的map中只存储变化了的日历
	 */
	TeamInfoCalendarVO
	save(String pk_org,TeamInfoCalendarVO vo) throws BusinessException;
	
	/**
	 * 批量调换日历天和排班
	 * @param psndocs 班组信息
	 * @param firstDate 调换日期1
	 * @param secondDate 调换日期2
	 * @author Ares.Tank 2018-9-6 15:15:10
	 */
	void batchChangeDateType(String pk_hrorg,String[] pkTeams,UFLiteralDate firstDate,UFLiteralDate secondDate) throws BusinessException ;
	/**
	 * 批量调换日历天和排班
	 * @param psndocs 班组信息
	 * @param firstDate 调换日期1
	 * @param secondDate 调换日期2
	 * @author he 2018-9-6 15:15:10
	 */
	List<AggTeamCalendarVO>changeDateType(String pk_hrorg,String[] pkTeams,UFLiteralDate firstDate,UFLiteralDate secondDate) throws BusinessException ;
	/**
	 * 批量变更日历天和排班
	 * @param psndocs 班组信息
	 * @param date 需要变更的日期
	 * @param 日历天类型,@see HolidayVo 
	 * @author Ares.Tank 2018-9-6 15:15:10
	 */
	void batchChangeDateType4OneDay(String pk_hrorg,String[] pkTeams,UFLiteralDate ChangeDate,Integer dateType) throws BusinessException ;
	


	
}
