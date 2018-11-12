package nc.itf.ta;

import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.teamcalendar.AggTeamCalendarVO;
import nc.vo.ta.teamcalendar.QueryScopeEnum;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

public interface ITeamCalendarQueryMaintain {
	/**
	 * 根据班组主键，考勤日历日查询工作日历
	 * @param pk_team
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	AggTeamCalendarVO queryByTeamDate(String pk_team,UFLiteralDate date) throws BusinessException;
	
	/**
	 * 根据开始结束日期，班组条件查询工作日历
	 * @param pk_org,HR组织主键
	 * @param beginDate,开始日期
	 * @param endDate,结束日期
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	TeamInfoCalendarVO[] 
	queryCalendarVOByCondition(String pk_hrorg,FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate) throws BusinessException;
	
	/**
	 * 根据日期范围，班组条件，以及查询范围查询班组工作日历
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @param condition
	 * @param queryScope，查询范围：所有人员，尚未排班人员，排班完毕人员，部分排班人员
	 * @return
	 * @throws BusinessException
	 */
	TeamInfoCalendarVO[]
	queryCalendarVOByCondition(String pk_hrorg,
			FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate,QueryScopeEnum queryScope) throws BusinessException;
	
	/**
	 * 根据日期范围，班组条件，是否覆盖已有工作日历查询班组，用于循环排班和默认排班(暂时无默认排班功能)选中条件后的班组列表确认
	 * @param pk_org,业务单元主键
	 * @param qs
	 * @param beginDate
	 * @param endDate
	 * @param isOverrideExistCalendar，为true，表示需要覆盖已有工作日历，则查出所有符合条件的班组，为false，
	 * 表示不覆盖已有工作日历，则只查出日期范围内工作日历不完整的班组，完整的班组不查出来
	 * @return，符合条件的班组VO
	 * @throws BusinessException
	 */
	TeamHeadVO[] queryTeamVOsByConditionAndOverride(String pk_org,
			FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate,boolean isOverrideExistCalendar) throws BusinessException;
	/**
	 * 查询最新的班组信息
	 * @param pk_org,业务单元主键
	 * @param qs
	 * @param isOverrideExistCalendar，为true，表示需要覆盖已有工作日历，则查出所有符合条件的班组，为false，
	 * 表示不覆盖已有工作日历，则只查出日期范围内工作日历不完整的班组，完整的班组不查出来
	 * @return，符合条件的班组VO
	 * @author Ares.Tank 2018-9-8 12:18:02
	 * @throws BusinessException
	 */
	TeamHeadVO[] queryTeamVOsByConditionAndOverrideWithOutDate(String pk_org,
			FromWhereSQL fromWhereSQL) throws BusinessException;
	
	/**
	 * 根据日期范围，班组主键数组，以及查询范围查询班组工作日历
	 * @param pk_org
	 * @param pk_teams
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	TeamInfoCalendarVO[] queryCalendarVOByPKTeams(String pk_hrorg,String[] pk_teams,UFLiteralDate beginDate,UFLiteralDate endDate) throws BusinessException;
	
	/**
	 * 根据部门主键，日期范围，班组条件，以及查询范围查询班组工作日历
	 * @param pk_dept
	 * @param containsSubDept
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @param queryScope
	 * @return
	 * @throws BusinessException
	 */
	TeamInfoCalendarVO[] queryCalendarVOByDeptPk(String pk_dept, boolean containsSubDept,
			FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate,QueryScopeEnum queryScope) throws BusinessException;
	
	/**
	 * 获取导出数据
	 * @param pk_org,HR组织主键
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	GeneralVO[] getExportDatas(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException;
	
	/**
	 * 获取导出数据
	 * @param pk_org,HR组织主键
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	GeneralVO[] getExportDatas(String pk_hrorg, TeamInfoCalendarVO[] calendarVOs, UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException;
}
