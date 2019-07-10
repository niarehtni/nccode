package nc.impl.ta.psncalendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import nc.bs.bd.baseservice.ArrayClassConvertUtil;
import nc.bs.bd.pub.distribution.util.BDDistTokenUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfo;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.execute.Executor;
import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.calendar.CalendarShiftMutexChecker;
import nc.impl.ta.calendar.TACalendarUtils;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.bd.shift.IShiftQueryService;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.bd.timezone.TimezoneUtil;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hrta.ILeaveextrarestMaintain;
import nc.itf.om.IAOSQueryService;
import nc.itf.ta.HRHolidayServiceFacade;
import nc.itf.ta.IHRHolidayQueryService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.itf.ta.IPsnCalendarQueryMaintain;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.holiday.HolidayInfo;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.bd.pub.EnableStateEnum;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hrcm.utils.ColumnsListProcessor;
import nc.vo.org.AdminOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.holiday.HRHolidayVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;
import nc.vo.ta.leaveextrarest.LeaveExtraRestVO;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalHoliday;
import nc.vo.ta.psncalendar.PsnCalendarCommonValue;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.psncalendar.QueryScopeEnum;
import nc.vo.ta.psndoc.TBMPsndocCommonValue;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.SQLParamWrapper;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.vorg.AdminOrgVersionVO;
import nc.vo.vorg.DeptVersionVO;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

public class PsnCalendarMaintainImpl implements IPsnCalendarQueryMaintain, IPsnCalendarManageMaintain {

	// Ares.Tank 2018-9-3 16:12:47 无奈之下写个饿汉单例,不是我水平不行,我是怕代码抽查说我没初始化
	// ssx 2019-01-04 写了就是你水平不行，不写的话是调用者的水平不行，你再好好想想该不该放弃自己的坚持
	// Ares.Tank 2019-1-4 不扣钱的话是可以坚持的,谁跟钱过不去呢~
	private BaseDAO baseDAO = new BaseDAO();

	public BaseDAO getBaseDAO() {
		return baseDAO;
	}

	@Override
	public AggPsnCalendar queryByPsnDate(String pk_psndoc, UFLiteralDate date) throws BusinessException {
		AggPsnCalendar returnVO = new PsnCalendarServiceImpl().queryByPsnDate(pk_psndoc, date);
		if (returnVO == null)
			return null;
		String pk_org = returnVO.getPsnCalendarVO().getPk_org();
		PsnCalHoliday[] holidays = returnVO.getPsnCalHolidayVO();
		if (ArrayUtils.isEmpty(holidays))
			return returnVO;

		// TimeZone timeZone =
		// NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		// 查询这一天的时区
		OrgVO orgVO = (OrgVO) new BaseDAO().retrieveByPK(OrgVO.class, pk_org);
		TimeZone timeZone = StringUtils.isEmpty(orgVO.getPk_timezone()) ? ICalendar.BASE_TIMEZONE : TimezoneUtil
				.getTimeZone(orgVO.getPk_timezone());
		InSQLCreator isc = null;
		HolidayVO[] holidayVOs = null;
		try {
			isc = new InSQLCreator();
			String inSQL = isc.getInSQL(holidays, PsnCalHoliday.PK_HOLIDAY);
			holidayVOs = CommonUtils.retrieveByClause(HolidayVO.class, HolidayVO.PK_HOLIDAY + " in(" + inSQL + ")");
			if (ArrayUtils.isEmpty(holidayVOs))
				return returnVO;
		} finally {
			if (isc != null)
				isc.clear();
		}
		Map<String, HolidayVO> holidayVOMap = CommonUtils.toMap(HolidayVO.PK_HOLIDAY, holidayVOs);
		for (int i = 0; i < holidays.length; i++) {
			HolidayVO holidayVO = holidayVOMap.get(holidays[i].getPk_holiday());
			if (holidayVO == null)
				continue;
			holidays[i].setBeginTime(new UFDateTime(holidayVO.getStarttime(), timeZone));
			holidays[i].setEndTime(new UFDateTime(holidayVO.getEndtime(), timeZone));
			holidays[i].setHolidayName(holidayVO.getHolidayMultiLangName());
		}
		returnVO.setPsnCalHolidayVO(holidays);
		return returnVO;
	}

	/**
	 * 2011.9.23已做工作日历业务单元化、假日业务单元化处理
	 * 
	 * @param pk_org
	 *            ，HR组织主键
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @param is4Mgr
	 *            ,是否用于经理自助
	 * @return
	 * @throws BusinessException
	 */
	protected PsnJobCalendarVO[] queryCalendarVOByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate, boolean is4Mgr, String pk_dept, boolean containsSubDepts)
			throws BusinessException {
		// 2013-04-24添加权限 //查询权限
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		if (is4Mgr)
			fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		// 查询出日期范围内的所有考勤档案记录，日期范围内最新的一条作为条件匹配，然后找出人员在日期范围内所有的考勤档案记录
		Map<String, List<TBMPsndocVO>> psndocMap = psndocService.queryTBMPsndocMapByCondition(is4Mgr ? null : pk_hrorg,
				fromWhereSQL, beginDate, endDate, true, true, null);
		if (psndocMap == null || psndocMap.isEmpty())
			return null;
		String[] pk_psndocs = psndocMap.keySet().toArray(new String[0]);
		IHRHolidayQueryService holidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		// 工作日类型map，key是人员主键，value的key是日期，value是工作日类型，见holidayvo。
		Map<String, Map<String, Integer>> holidayInfo = holidayService.queryPsnWorkDayTypeInfo(pk_hrorg, pk_psndocs,
				beginDate, endDate);
		// 人员的工作日历，key是pk_psndoc,value是工作日历list。只包含考勤档案有效日期的工作日历，在考勤档案无效的日期里，就算psncalendar有数据，也不包含在这个map里
		Map<String, List<PsnCalendarVO>> calendarMap = new PsnCalendarDAO().queryCalendarVOListMapByPsndocs(
				is4Mgr ? null : pk_hrorg, pk_psndocs, beginDate, endDate);
		List<PsnJobCalendarVO> returnList = new ArrayList<PsnJobCalendarVO>();
		UFLiteralDate[] dateScope = CommonUtils.createDateArray(beginDate, endDate);
		Map<String, TBMPsndocVO[]> hrOrgDeptPsndocMap = null;// 如果是经理自助，则需要把每个人员在pk_org且部门在pk_dept(及下级)部门的天都查询出来
		if (is4Mgr) {
			FromWhereSQL fws = null;
			try {
				fws = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
				fws = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, true, fws);
				hrOrgDeptPsndocMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_PSNDOC,
						psndocService.queryByCondition(pk_hrorg, fws));
			} finally {
				TBMPsndocSqlPiecer.clearQuerySQL(fws);
			}
		}
		for (String pk_psndoc : pk_psndocs) {
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);// 此人有效的考勤档案
			TBMPsndocVO[] psndocsInHrOrgAndDept = is4Mgr ? (hrOrgDeptPsndocMap != null ? hrOrgDeptPsndocMap
					.get(pk_psndoc) : null) : null;
			// 最后一条为有效的，界面显示时以最后一条为准
			TBMPsndocVO psndocVO = psndocList.get(psndocList.size() - 1);
			PsnJobCalendarVO vo = new PsnJobCalendarVO();
			returnList.add(vo);
			vo.setPk_psnjob(psndocVO.getPk_psnjob());
			vo.setPk_psndoc(psndocVO.getPk_psndoc());
			vo.setPk_org_v(psndocVO.getPk_org_v());
			vo.setPk_dept_v(psndocVO.getPk_dept_v());
			// 装入考勤档案有效日期
			for (UFLiteralDate date : dateScope) {
				String dateStr = date.toString();
				if (TBMPsndocVO.isIntersect(psndocList, dateStr)) {
					vo.getPsndocEffectiveDateSet().add(dateStr);
					vo.getOrgMap().put(dateStr, TBMPsndocVO.findIntersectionVO(psndocList, dateStr).getPk_joborg());
				}
				if (is4Mgr && TBMPsndocVO.isIntersect(psndocsInHrOrgAndDept, dateStr))// 如果是经理自助，则还要装入在hr组织内、在部门管辖范围内的人员
					vo.getPsndocEffectiveDateSetInHROrgAndDept().add(dateStr);
			}
			// 装入班次
			if (calendarMap != null) {
				List<PsnCalendarVO> calList = calendarMap.get(psndocVO.getPk_psndoc());
				if (!org.apache.commons.collections.CollectionUtils.isEmpty(calList))
					for (PsnCalendarVO calVO : calList)
						vo.getCalendarMap().put(calVO.getCalendar().toString(), calVO.getPk_shift());
			}
			// 装入工作日类型
			// TODO ?
			Map<String, Integer> dayType = holidayInfo != null ? holidayInfo.get(vo.getPk_psndoc()) : null;
			if (dayType != null)
				vo.getDayTypeMap().putAll(holidayInfo.get(vo.getPk_psndoc()));
		}
		return returnList.toArray(new PsnJobCalendarVO[0]);
	}

	/*
	 * 2011.9.28已完成班次业务单元化和假日业务单元化处理 (non-Javadoc)
	 * 
	 * @see
	 * nc.itf.ta.IPsnCalendarQueryMaintain#queryCalendarVOByCondition(java.lang.
	 * String, nc.ui.querytemplate.querytree.FromWhereSQL,
	 * nc.vo.pub.lang.UFLiteralDate, nc.vo.pub.lang.UFLiteralDate)
	 */
	@Override
	public PsnJobCalendarVO[] queryCalendarVOByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		return queryCalendarVOByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate, false, null, false);
	}

	public PsnJobCalendarVO[] queryCalendarVOByConditionForTeam(String pk_hrorg, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		return queryCalendarVOByConditionForTeam(pk_hrorg, fromWhereSQL, beginDate, endDate, false, null, false);
	}

	protected PsnJobCalendarVO[] queryCalendarVOByConditionForTeam(String pk_hrorg, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate, boolean is4Mgr, String pk_dept, boolean containsSubDepts)
			throws BusinessException {
		if (is4Mgr)
			fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		// 查询出日期范围内的所有考勤档案记录，日期范围内最新的一条作为条件匹配，然后找出人员在日期范围内所有的考勤档案记录
		Map<String, List<TBMPsndocVO>> psndocMap = psndocService.queryTBMPsndocMapByConditionForTeam(is4Mgr ? null
				: pk_hrorg, fromWhereSQL, beginDate, endDate, true, true, null);
		if (psndocMap == null || psndocMap.isEmpty())
			return null;
		String[] pk_psndocs = psndocMap.keySet().toArray(new String[0]);
		IHRHolidayQueryService holidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		// 工作日类型map，key是人员主键，value的key是日期，value是工作日类型，见holidayvo。
		Map<String, Map<String, Integer>> holidayInfo = holidayService.queryPsnWorkDayTypeInfo(pk_hrorg, pk_psndocs,
				beginDate, endDate);
		// 人员的工作日历，key是pk_psndoc,value是工作日历list。只包含考勤档案有效日期的工作日历，在考勤档案无效的日期里，就算psncalendar有数据，也不包含在这个map里
		Map<String, List<PsnCalendarVO>> calendarMap = new PsnCalendarDAO().queryCalendarVOListMapByPsndocs(
				is4Mgr ? null : pk_hrorg, pk_psndocs, beginDate, endDate);
		// Map<String, List<PsnCalendarVO>> calendarMap = new
		// PsnCalendarDAO().queryCalendarVOListMapByPsndocsForTeam(is4Mgr?null:pk_hrorg,
		// pk_psndocs, beginDate, endDate,pk_team);
		List<PsnJobCalendarVO> returnList = new ArrayList<PsnJobCalendarVO>();
		UFLiteralDate[] dateScope = CommonUtils.createDateArray(beginDate, endDate);
		Map<String, TBMPsndocVO[]> hrOrgDeptPsndocMap = null;// 如果是经理自助，则需要把每个人员在pk_org且部门在pk_dept(及下级)部门的天都查询出来
		if (is4Mgr) {
			FromWhereSQL fws = null;
			try {
				fws = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
				fws = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, true, fws);
				hrOrgDeptPsndocMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_PSNDOC,
						psndocService.queryByCondition(pk_hrorg, fws));
			} finally {
				TBMPsndocSqlPiecer.clearQuerySQL(fws);
			}
		}
		for (String pk_psndoc : pk_psndocs) {
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);// 此人有效的考勤档案
			TBMPsndocVO[] psndocsInHrOrgAndDept = is4Mgr ? (hrOrgDeptPsndocMap != null ? hrOrgDeptPsndocMap
					.get(pk_psndoc) : null) : null;
			// 最后一条为有效的，界面显示时以最后一条为准
			TBMPsndocVO psndocVO = psndocList.get(psndocList.size() - 1);
			PsnJobCalendarVO vo = new PsnJobCalendarVO();
			returnList.add(vo);
			vo.setPk_psnjob(psndocVO.getPk_psnjob());
			vo.setPk_psndoc(psndocVO.getPk_psndoc());
			vo.setPk_org_v(psndocVO.getPk_org_v());
			vo.setPk_dept_v(psndocVO.getPk_dept_v());
			// 装入考勤档案有效日期
			for (UFLiteralDate date : dateScope) {
				String dateStr = date.toString();
				if (TBMPsndocVO.isIntersect(psndocList, dateStr)) {
					vo.getPsndocEffectiveDateSet().add(dateStr);
					vo.getOrgMap().put(dateStr, TBMPsndocVO.findIntersectionVO(psndocList, dateStr).getPk_joborg());
				}
				if (is4Mgr && TBMPsndocVO.isIntersect(psndocsInHrOrgAndDept, dateStr))// 如果是经理自助，则还要装入在hr组织内、在部门管辖范围内的人员
					vo.getPsndocEffectiveDateSetInHROrgAndDept().add(dateStr);
			}
			// 装入班次
			if (calendarMap != null) {
				List<PsnCalendarVO> calList = calendarMap.get(psndocVO.getPk_psndoc());
				if (!org.apache.commons.collections.CollectionUtils.isEmpty(calList))
					for (PsnCalendarVO calVO : calList)
						vo.getCalendarMap().put(calVO.getCalendar().toString(), calVO.getPk_shift());
			}
			// 装入工作日类型
			Map<String, Integer> dayType = holidayInfo != null ? holidayInfo.get(vo.getPk_psndoc()) : null;
			if (dayType != null)
				vo.getDayTypeMap().putAll(holidayInfo.get(vo.getPk_psndoc()));
		}
		return returnList.toArray(new PsnJobCalendarVO[0]);
	}

	/*
	 * 2011.9.23已完成班次业务单元化和假日业务单元化处理 (non-Javadoc)
	 * 
	 * @see
	 * nc.itf.ta.IPsnCalendarQueryMaintain#queryCalendarVOByCondition(java.lang.
	 * String, nc.ui.querytemplate.querytree.FromWhereSQL,
	 * nc.vo.pub.lang.UFLiteralDate, nc.vo.pub.lang.UFLiteralDate,
	 * nc.vo.ta.psncalendar.QueryScopeEnum)
	 */
	@Override
	public PsnJobCalendarVO[] queryCalendarVOByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate, QueryScopeEnum queryScope) throws BusinessException {
		PsnJobCalendarVO[] vos = queryCalendarVOByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate);
		getDayLeaveType(vos, pk_hrorg, beginDate, endDate);
		// 如果是查询所有，则直接返回
		if (vos == null || queryScope == QueryScopeEnum.all)
			return vos;
		List<PsnJobCalendarVO> returnList = new ArrayList<PsnJobCalendarVO>();
		// 为了效率，分成三个for循环；如果写成一个for循环，则判断次数大增
		// 如果是查询部分排班人员，则要判断排班天数是否小于考勤档案有效天数且排班天数大于0
		if (queryScope == QueryScopeEnum.part) {
			for (PsnJobCalendarVO vo : vos) {
				if (vo.getCalendarMap().size() < vo.getPsndocEffectiveDateSet().size()
						&& vo.getCalendarMap().size() > 0)
					returnList.add(vo);
			}
			return returnList.toArray(new PsnJobCalendarVO[0]);
		}
		// 如果是查询尚未排班人员，则要判断排班天数是否等于0
		if (queryScope == QueryScopeEnum.not) {
			for (PsnJobCalendarVO vo : vos) {
				if (vo.getCalendarMap().size() == 0)
					returnList.add(vo);
			}
			return returnList.toArray(new PsnJobCalendarVO[0]);
		}
		// 走到这里肯定是查询完全排班人员，只需判断排班天数是否与考勤档案有效天数相等即可
		for (PsnJobCalendarVO vo : vos) {
			if (vo.getCalendarMap().size() == vo.getPsndocEffectiveDateSet().size())
				returnList.add(vo);
		}
		return returnList.toArray(new PsnJobCalendarVO[0]);
	}

	/**
	 * 
	 * @param vos
	 * @param pk_hrorg
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getDayLeaveType(PsnJobCalendarVO[] vos, String pk_hrorg, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return;
		}
		LeaveTypeCopyVO[] leavetypes = NCLocator.getInstance().lookup(ITimeItemQueryService.class)
				.queryLeaveCopyTypesByOrg(pk_hrorg);
		if (ArrayUtils.isEmpty(leavetypes)) {
			return;
		}
		Map<String, LeaveTypeCopyVO> typeMap = CommonUtils.toMap(LeaveTypeCopyVO.PK_TIMEITEMCOPY, leavetypes);
		InSQLCreator isc = new InSQLCreator();
		String inSql = isc.getInSQL(vos, PsnJobCalendarVO.PK_PSNDOC);
		String leaveCond = LeaveRegVO.PK_PSNDOC + " in (" + inSql + ") and " + LeaveRegVO.LEAVEBEGINDATE + " <= '"
				+ endDate.toString() + "' and " + LeaveRegVO.LEAVEENDDATE + " >= '" + beginDate.toString()
				+ "' and pk_leavetype != '1002Z710000000021ZM3' order by " + LeaveRegVO.LEAVEBEGINTIME;
		Collection c = new BaseDAO().retrieveByClause(LeaveRegVO.class, leaveCond);
		if (CollectionUtils.isEmpty(c)) {
			return;
		}
		LeaveRegVO[] leaveRegVOs = (LeaveRegVO[]) c.toArray(new LeaveRegVO[0]);

		Map<String, LeaveRegVO[]> psnLeaveMap = CommonUtils.group2ArrayByField(LeaveRegVO.PK_PSNDOC, leaveRegVOs);
		for (PsnJobCalendarVO vo : vos) {
			LeaveRegVO[] psnLeaveVOs = psnLeaveMap.get(vo.getPk_psndoc());
			if (ArrayUtils.isEmpty(psnLeaveVOs)) {
				continue;
			}
			for (LeaveRegVO regVO : psnLeaveVOs) {
				LeaveTypeCopyVO typeVO = typeMap.get(regVO.getPk_leavetypecopy());
				if (typeVO == null) {
					continue;
				}
				UFLiteralDate leavebegindate = regVO.getLeavebegindate();
				UFLiteralDate leaveenddate = regVO.getLeaveenddate();
				UFLiteralDate[] dates = CommonUtils.createDateArray(leavebegindate, leaveenddate);
				for (UFLiteralDate date : dates) {
					if (!ShiftVO.PK_GX.equalsIgnoreCase(vo.getCalendarMap().get(date.toString()))) {
						vo.getLeaveMap().put(date.toString(), typeVO.getMultilangName());
					} else {
						if (1 == typeVO.getGxcomtype()) {// 公休记休假的才在公休时显示休假
							vo.getLeaveMap().put(date.toString(), typeVO.getMultilangName());
						}
					}
				}
			}
		}
	}

	@Override
	public void batchChangeShift(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean withOldShift, String oldShift, String newShift) throws BusinessException {
		OrgVO hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org);
		String pk_hrorg = hrorg.getPk_org();
		if (!withOldShift) {// 如果不考虑原班次
			if (StringUtils.isEmpty(newShift)) {// 如果新班次为空，则删除日期范围内的所有工作日历
				new PsnCalendarDAO().deleteByFromWhereAndDateArea(pk_org, fromWhereSQL, beginDate, endDate);
				return;
			}
			// 如果新班次不空，则可以理解是排班周期是一天的循环排班，且覆盖原有班次，且遇假日照旧
			String[] pk_psndocs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
					.queryLatestPsndocsByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate);
			if (ArrayUtils.isEmpty(pk_psndocs))
				return;
			// 如果考勤档案的数据有问题，可能会导致pk_psndoc有重复的，将其去掉
			Set<String> psndocSet = new HashSet<String>(Arrays.asList(pk_psndocs));
			pk_psndocs = psndocSet.toArray(new String[0]);
			circularArrange(hrorg.getPk_group(), pk_hrorg, pk_org, pk_psndocs, beginDate, endDate,
					new String[] { newShift }, false, true, false);
			return;
		}
		// 代码走到这里，表示要考虑原班次
		// 如果oldShift和newShift相同，则直接返回不做处理
		oldShift = StringUtils.isEmpty(oldShift) ? "" : oldShift;
		newShift = StringUtils.isEmpty(newShift) ? "" : newShift;
		if (oldShift.equals(newShift))
			return;
		// 最终将数据转换为save方法能接受的参数，然后直接调用save方法
		PsnJobCalendarVO[] psnjobVOs = queryCalendarVOByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate);
		if (ArrayUtils.isEmpty(psnjobVOs))
			return;
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		for (PsnJobCalendarVO vo : psnjobVOs) {
			Map<String, String> calendarMap = vo.getCalendarMap();
			// if(MapUtils.isEmpty(calendarMap))
			// continue;
			for (UFLiteralDate date : allDates) {
				if (!vo.getPsndocEffectiveDateSet().contains(date.toString()))
					continue;
				// 如果date日已排班，且班次主键等于oldShift，或者date日未排班，且oldShift为空，则都视为满足oldShift的匹配条件
				if (((oldShift.equals(calendarMap.get(date.toString()))) || oldShift.equals("")
						&& !calendarMap.containsKey(date.toString()))) {
					vo.getModifiedCalendarMap().put(date.toString(), newShift);
				}
			}
		}
		save(pk_hrorg, psnjobVOs);
	}

	public void batchChangeShiftNew(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate,
			boolean withOldShift, String oldShift, String newShift) throws BusinessException {
		// String pk_hrorg =
		// NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		OrgVO hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org);
		String pk_hrorg = hrorg.getPk_org();
		if (!withOldShift) {// 如果不考虑原班次
			if (StringUtils.isEmpty(newShift)) {// 如果新班次为空，则删除日期范围内的所有工作日历
				// new PsnCalendarDAO().deleteByFromWhereAndDateArea(pk_org,
				// fromWhereSQL, beginDate, endDate);
				// new PsnCalendarDAO().deleteByPsndocsAndDateArea(pk_hrorg,
				// pk_psndocs, beginDate, endDate);//工作日历对应的是业务单元不是hr组织
				new PsnCalendarDAO().deleteByPsndocsAndDateArea(pk_org, pk_psndocs, beginDate, endDate);
				return;
			}
			// 如果新班次不空，则可以理解是排班周期是一天的循环排班，且覆盖原有班次，且遇假日照旧
			// String[] pk_psndocs =
			// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocsByCondition(pk_hrorg,
			// fromWhereSQL, beginDate, endDate);
			if (ArrayUtils.isEmpty(pk_psndocs))
				return;
			// 如果考勤档案的数据有问题，可能会导致pk_psndoc有重复的，将其去掉
			Set<String> psndocSet = new HashSet<String>(Arrays.asList(pk_psndocs));
			pk_psndocs = psndocSet.toArray(new String[0]);
			circularArrange(hrorg.getPk_group(), pk_hrorg, pk_org, pk_psndocs, beginDate, endDate,
					new String[] { newShift }, false, true, false);
			return;
		}
		// 代码走到这里，表示要考虑原班次
		// 如果oldShift和newShift相同，则直接返回不做处理
		oldShift = StringUtils.isEmpty(oldShift) ? "" : oldShift;
		newShift = StringUtils.isEmpty(newShift) ? "" : newShift;
		// if(oldShift.equals(newShift))
		// return;
		// 最终将数据转换为save方法能接受的参数，然后直接调用save方法
		// PsnJobCalendarVO[] psnjobVOs = queryCalendarVOByCondition(pk_hrorg,
		// fromWhereSQL, beginDate, endDate);
		PsnJobCalendarVO[] psnjobVOs = queryCalendarVOByPsndocs(pk_hrorg, pk_psndocs, beginDate, endDate);
		if (ArrayUtils.isEmpty(psnjobVOs))
			return;
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		for (PsnJobCalendarVO vo : psnjobVOs) {
			Map<String, String> calendarMap = vo.getCalendarMap();
			// if(MapUtils.isEmpty(calendarMap))
			// continue;
			for (UFLiteralDate date : allDates) {
				if (!vo.getPsndocEffectiveDateSet().contains(date.toString()))
					continue;
				// 如果date日已排班，且班次主键等于oldShift，或者date日未排班，且oldShift为空，则都视为满足oldShift的匹配条件
				if ((oldShift.equals(calendarMap.get(date.toString())))
						|| (oldShift.equals("") && !calendarMap.containsKey(date.toString()))) {
					vo.getModifiedCalendarMap().put(date.toString(), newShift);
				}
			}
		}
		save(pk_hrorg, psnjobVOs);
	}

	/*
	 * pk_org:业务单元主键 V61开始，人员工作日历改为业务单元级的档案，排工作日历时，要指定业务单元，使用此业务单元下的班次、时区和假日
	 * (non-Javadoc)
	 * 
	 * @see
	 * nc.itf.ta.IPsnCalendarManageMaintain#circularArrange(java.lang.String,
	 * java.lang.String[], nc.vo.pub.lang.UFLiteralDate,
	 * nc.vo.pub.lang.UFLiteralDate, java.lang.String[], boolean, boolean)
	 */
	@Override
	public PsnJobCalendarVO[] circularArrange(String pk_group, String pk_hrorg, String pk_org, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate, String[] calendarPks, boolean isHolidayCancel,
			boolean overrideExistCalendar, boolean withRturn) throws BusinessException {
		// 此处的pk_org是业务单元，但期间使用的是hr组织，因此需要调整一下
		if (StringUtils.isBlank(pk_group) || StringUtils.isBlank(pk_hrorg)) {
			OrgVO hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org);
			pk_group = hrorg.getPk_group();
			pk_hrorg = hrorg.getPk_org();
		}
		// OrgVO hrorg =
		// NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org);
		NCLocator.getInstance().lookup(IPeriodQueryService.class).checkDateScope(pk_hrorg, beginDate, endDate);
		// NCLocator.getInstance().lookup(IPeriodQueryService.class).checkDateScope(hrorg.getPk_org(),
		// beginDate,endDate);
		// 首先简单校验一下循环排班的班次是否冲突。
		new CalendarShiftMutexChecker().simpleCheckCircularArrange(pk_org, beginDate, endDate, calendarPks, false);
		// 如果简单校验通过，则可以进行排班
		// OrgVO orgVO = (OrgVO) new BaseDAO().retrieveByPK(OrgVO.class,
		// pk_org);
		// String pk_group = orgVO.getPk_group();
		// 如果遇假日照旧，则说明不需要考虑假日
		if (!isHolidayCancel) {
			circularArrangeIgnoreHolidayBatch(pk_group, pk_hrorg, pk_org, pk_psndocs, beginDate, endDate, calendarPks,
					false, overrideExistCalendar);
		} else {
			// 如果遇假日取消，则要查询业务单元内的假日假日
			// HolidayInfo holidayInfo =
			// NCLocator.getInstance().lookup(IHolidayQueryService.class).queryHolidayInfo(pk_org,
			// beginDate, endDate);
			HolidayInfo<HRHolidayVO> holidayInfo = NCLocator.getInstance().lookup(IHRHolidayQueryService.class)
					.queryHolidayInfo(pk_org, beginDate, endDate);
			// 如果此范围内没有假日，则依然调用不用考虑假日的排班方法
			if (holidayInfo == null) {
				circularArrangeIgnoreHolidayBatch(pk_group, pk_hrorg, pk_org, pk_psndocs, beginDate, endDate,
						calendarPks, true, overrideExistCalendar);
			} else {
				// 如果遇假日取消，且有假日，则情况比较复杂
				circularArrangeWithHolidayBatch(pk_group, pk_hrorg, pk_org, pk_psndocs, beginDate, endDate,
						calendarPks, overrideExistCalendar, holidayInfo);
			}
		}
		if (withRturn) {
			return queryCalendarVOByPsndocs(pk_hrorg, pk_psndocs, beginDate, endDate);
		}
		return null;
	}

	public PsnJobCalendarVO[] circularArrangeForTeam(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, String[] calendarPks, boolean isHolidayCancel, boolean overrideExistCalendar,
			String pk_team) throws BusinessException {
		// 首先简单校验一下循环排班的班次是否冲突。
		new CalendarShiftMutexChecker().simpleCheckCircularArrange(pk_org, beginDate, endDate, calendarPks, false);
		// 如果简单校验通过，则可以进行排班
		OrgVO orgVO = (OrgVO) new BaseDAO().retrieveByPK(OrgVO.class, pk_org);
		String pk_group = orgVO.getPk_group();
		// 如果遇假日照旧，则说明不需要考虑假日
		if (!isHolidayCancel)
			return circularArrangeIgnoreHolidayForTeam(pk_group, pk_org, pk_psndocs, beginDate, endDate, calendarPks,
					false, overrideExistCalendar, pk_team);
		// 如果遇假日取消，则要查询业务单元内的假日假日
		// HolidayInfo holidayInfo =
		// NCLocator.getInstance().lookup(IHolidayQueryService.class).queryHolidayInfo(pk_org,
		// beginDate, endDate);
		HolidayInfo<HRHolidayVO> holidayInfo = NCLocator.getInstance().lookup(IHRHolidayQueryService.class)
				.queryHolidayInfo(pk_org, beginDate, endDate);
		// 如果此范围内没有假日，则依然调用不用考虑假日的排班方法
		if (holidayInfo == null)
			return circularArrangeIgnoreHolidayForTeam(pk_group, pk_org, pk_psndocs, beginDate, endDate, calendarPks,
					true, overrideExistCalendar, pk_team);
		// 如果遇假日照旧，且有假日，则情况比较复杂
		return circularArrangeWithHolidayForTeam(pk_group, pk_org, pk_psndocs, beginDate, endDate, calendarPks,
				overrideExistCalendar, holidayInfo, pk_team);
	}

	/**
	 * 为了防止数据查询溢出，超出数据的限制，在此对人员进行分批处理（500人一批）
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param calendarPks
	 * @param overrideExistCalendar
	 * @param holidayInfo
	 * @return
	 * @throws BusinessException
	 */
	protected void circularArrangeWithHolidayBatch(String pk_group, String pk_hrorg, String pk_org,
			String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate, String[] calendarPks,
			boolean overrideExistCalendar, HolidayInfo<HRHolidayVO> holidayInfo) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs)) {
			return;
		}
		// 默认班
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// 业务单元内所有班次
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		// 业务单元的时区
		TimeZone timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		Set<String> pkset = new HashSet<String>();
		int count = 0;
		for (String pk_psndoc : pk_psndocs) {
			count++;
			pkset.add(pk_psndoc);
			if (count > 499) {
				circularArrangeWithHoliday(pk_group, pk_hrorg, pk_org, pkset.toArray(new String[0]), beginDate,
						endDate, defaultShift, calendarPks, overrideExistCalendar, holidayInfo, shiftMap, timeZone);
				count = 0;
				pkset.clear();
			}
		}
		circularArrangeWithHoliday(pk_group, pk_hrorg, pk_org, pkset.toArray(new String[0]), beginDate, endDate,
				defaultShift, calendarPks, overrideExistCalendar, holidayInfo, shiftMap, timeZone);
	}

	/**
	 * 考虑假日的循环排班
	 * 
	 * @param pk_org
	 *            ,业务单元主键
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param calendarPks
	 * @param overrideExistCalendar
	 * @return
	 * @throws BusinessException
	 */
	protected void circularArrangeWithHoliday(String pk_group, String pk_hrorg, String pk_org, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate, AggShiftVO defaultShift, String[] calendarPks,
			boolean overrideExistCalendar, HolidayInfo<HRHolidayVO> holidayInfo, Map<String, AggShiftVO> shiftMap,
			TimeZone timeZone) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs)) {
			return;
		}
		// 存储要设置的班次的map，key是人员主键，value的key是date，value是班次主键.考虑到有可能产生班次的对调，因此最终modifiedCalendarMap里的日期范围可能会超出begindate和enddate的范围
		Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String, String>>();
		// 将循环的班次按日期范围全部展开，key是日期，value是班次主键
		Map<String, String> originalExpandedDatePkShiftMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate,
				endDate, calendarPks);
		UFLiteralDate[] dateArea = CommonUtils.createDateArray(beginDate, endDate);
		// String pk_hrorg =
		// NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		// 默认班
		// AggShiftVO defaultShift =
		// ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// //业务单元内所有班次
		// Map<String, AggShiftVO> shiftMap =
		// ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		// 假日的享有情况,key是人员主键，value的key是假日主键，value是是否享有
		Map<String, Map<String, Boolean>> psnEnjoyHolidayMap = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_org,
				pk_psndocs, holidayInfo.getHolidayVOs());
		Map<String, Map<String, AggPsnCalendar>> holidayCutMap = new HashMap<String, Map<String, AggPsnCalendar>>();
		// 记录对调前班次的map，key是人员，value的key是日期，value是对调前的班次
		Map<String, Map<String, String>> psnBeforeExgPkShiftMap = new HashMap<String, Map<String, String>>();
		// //业务单元的时区
		// TimeZone timeZone =
		// NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		// 人员已经排好的班次,范围是min(beginDate,对调日,假日).getDateBefore(1)到max(endDate,对调日，假日).getDateAfter(1)
		Set<String> holidayDateSet = new HashSet<String>();// 存储假日、对调日的map，key是日期，用于日期排序，以确定查询工作日历的范围
		holidayDateSet.addAll(holidayInfo.getHolidayMap().keySet());
		holidayDateSet.addAll(holidayInfo.getSwitchMap().keySet());
		String[] allDates = holidayDateSet.toArray(new String[0]);// 对假日、对调日排序
		Arrays.sort(allDates);
		//将假日当天的排班排为公休  wangywt 20190624 begin
		if(holidayInfo.getHolidayMap()!=null){
			for(String holiday: holidayInfo.getHolidayMap().keySet()){
				if(originalExpandedDatePkShiftMap.keySet().contains(holiday)){
					originalExpandedDatePkShiftMap.put(holiday, ShiftVO.PK_GX);
				}
			}
		}
		//将假日当天的排班排为公休  wangywt 20190624 end
		// 查询工作日历的最大日期范围
		UFLiteralDate calendarQueryBeginDate = allDates[0].compareTo(beginDate.toString()) < 0 ? UFLiteralDate.getDate(
				allDates[0]).getDateBefore(1) : beginDate.getDateBefore(1);
		UFLiteralDate calendarQueryEndDate = allDates[allDates.length - 1].compareTo(endDate.toString()) > 0 ? UFLiteralDate
				.getDate(allDates[allDates.length - 1]).getDateAfter(1) : endDate.getDateAfter(1);
		Map<String, Map<String, PsnCalendarVO>> psnExistsCalendarMap = new PsnCalendarDAO()
				.queryCalendarVOMapByPsndocs(pk_hrorg, pk_psndocs, calendarQueryBeginDate, calendarQueryEndDate);
		// 业务单元内考勤档案Map，key是人员主键,此map主要来描述人员考勤档案有效日期范围
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_org, pk_psndocs, calendarQueryBeginDate, calendarQueryEndDate, true);
		// 按人循环处理
		if (psndocMap == null) {
			return;
		}
		for (int i = 0; i < pk_psndocs.length; i++) {
			Map<String, String> cloneDatePkShiftMap = new HashMap<String, String>(originalExpandedDatePkShiftMap);// 每个人单独复制一份，因为虽然是循环排班，但是最后排班的情况可能每个人都不同
			Map<String, String> beforeExgPkShiftMap = new HashMap<String, String>();
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndocs[i]);
			modifiedCalendarMap.put(pk_psndocs[i], cloneDatePkShiftMap);
			psnBeforeExgPkShiftMap.put(pk_psndocs[i], beforeExgPkShiftMap);
			// 以及处理过的天(因为涉及到对调，可能有些后面的天在前面就处理过了)
			Set<String> processedDateSet = new HashSet<String>();
			// 循环处理用户设置的循环班次
			for (int dateIndex = 0; dateIndex < dateArea.length; dateIndex++) {
				String date = dateArea[dateIndex].toString();// 日期
				if (processedDateSet.contains(date))// 处理过则不再处理
					continue;
				processedDateSet.add(date);
				String pk_shift = originalExpandedDatePkShiftMap.get(date);// 班次，因为是循环排班，因此不可能为空
				// 如果是公休
				if (ShiftVO.PK_GX.equals(pk_shift)) {// 直接调用公休的排班方法
					circularArrangeWithHolidayGX(pk_psndocs[i], psndocList, beginDate, endDate, cloneDatePkShiftMap,
							originalExpandedDatePkShiftMap, beforeExgPkShiftMap, psnExistsCalendarMap == null ? null
									: psnExistsCalendarMap.get(pk_psndocs[i]), shiftMap,
							psnEnjoyHolidayMap == null ? null : psnEnjoyHolidayMap.get(pk_psndocs[i]), holidayCutMap,
							processedDateSet, holidayInfo, defaultShift, date, timeZone);
					continue;
				}
				// 如果不是公休，直接调用非公休的排班方法
				circularArrangeWithHolidayNonGX(pk_psndocs[i], psndocList, calendarQueryBeginDate,
						calendarQueryEndDate, cloneDatePkShiftMap, originalExpandedDatePkShiftMap, beforeExgPkShiftMap,
						psnExistsCalendarMap == null ? null : psnExistsCalendarMap.get(pk_psndocs[i]), shiftMap,
						psnEnjoyHolidayMap == null ? null : psnEnjoyHolidayMap.get(pk_psndocs[i]), holidayCutMap,
						processedDateSet, holidayInfo, defaultShift, date, timeZone);
			}
			// 容错处理：cloneDatePkShiftMap里面不应该存储考勤档案无效的天，因此需要在此处去掉
			String[] dates = cloneDatePkShiftMap.keySet().toArray(new String[0]);
			if (!ArrayUtils.isEmpty(dates))
				for (String date : dates) {
					if (!TBMPsndocVO.isIntersect(psndocList, date))
						cloneDatePkShiftMap.remove(date);
				}
		}
		// 至此，所有的班都排完，相关的排班信息存入了：
		// 1.modifiedCalendarMap：用户要设置的班次的map；
		// 2.holidayCutMap：如果排班的过程中因为假日导致psncalendarvo要特殊处理，则放入此map
		// 进行校验：
		new CalendarShiftMutexChecker().checkCalendar(pk_org, modifiedCalendarMap, overrideExistCalendar, true, false);
		// 校验通过，持久化到数据库
		circularArrangeWithHolidayPersistence(pk_group, pk_org, shiftMap, pk_psndocs, beginDate, endDate,
				originalExpandedDatePkShiftMap, modifiedCalendarMap, psnBeforeExgPkShiftMap, holidayCutMap,
				psnExistsCalendarMap, overrideExistCalendar);
	}

	protected PsnJobCalendarVO[] circularArrangeWithHolidayForTeam(String pk_group, String pk_org, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate, String[] calendarPks, boolean overrideExistCalendar,
			HolidayInfo<HRHolidayVO> holidayInfo, String pk_team) throws BusinessException {
		// 存储要设置的班次的map，key是人员主键，value的key是date，value是班次主键.考虑到有可能产生班次的对调，因此最终modifiedCalendarMap里的日期范围可能会超出begindate和enddate的范围
		Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String, String>>();
		// 将循环的班次按日期范围全部展开，key是日期，value是班次主键
		Map<String, String> originalExpandedDatePkShiftMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate,
				endDate, calendarPks);
		UFLiteralDate[] dateArea = CommonUtils.createDateArray(beginDate, endDate);
		String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		// 默认班
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// 业务单元内所有班次
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		// 假日的享有情况,key是人员主键，value的key是假日主键，value是是否享有
		Map<String, Map<String, Boolean>> psnEnjoyHolidayMap = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_org,
				pk_psndocs, holidayInfo.getHolidayVOs());
		// 与假日切割的班次的map，key是人员，value的key是日期，value是psncalendar的聚合vo.只有假日对排班产生了影响，例如切割了，或者导致弹性班固化了，才存到此map里去，正常的天，或者被完全切割成公休的不会存
		Map<String, Map<String, AggPsnCalendar>> holidayCutMap = new HashMap<String, Map<String, AggPsnCalendar>>();
		// 记录对调前班次的map，key是人员，value的key是日期，value是对调前的班次
		Map<String, Map<String, String>> psnBeforeExgPkShiftMap = new HashMap<String, Map<String, String>>();
		// 业务单元的时区
		TimeZone timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		// 人员已经排好的班次,范围是min(beginDate,对调日,假日).getDateBefore(1)到max(endDate,对调日，假日).getDateAfter(1)
		Set<String> holidayDateSet = new HashSet<String>();// 存储假日、对调日的map，key是日期，用于日期排序，以确定查询工作日历的范围
		holidayDateSet.addAll(holidayInfo.getHolidayMap().keySet());
		holidayDateSet.addAll(holidayInfo.getSwitchMap().keySet());
		String[] allDates = holidayDateSet.toArray(new String[0]);// 对假日、对调日排序
		Arrays.sort(allDates);
		// 查询工作日历的最大日期范围
		UFLiteralDate calendarQueryBeginDate = allDates[0].compareTo(beginDate.toString()) < 0 ? UFLiteralDate.getDate(
				allDates[0]).getDateBefore(1) : beginDate.getDateBefore(1);
		UFLiteralDate calendarQueryEndDate = allDates[allDates.length - 1].compareTo(endDate.toString()) > 0 ? UFLiteralDate
				.getDate(allDates[allDates.length - 1]).getDateAfter(1) : endDate.getDateAfter(1);
		Map<String, Map<String, PsnCalendarVO>> psnExistsCalendarMap = new PsnCalendarDAO()
				.queryCalendarVOMapByPsndocs(pk_hrorg, pk_psndocs, calendarQueryBeginDate, calendarQueryEndDate);
		// 业务单元内考勤档案Map，key是人员主键,此map主要来描述人员考勤档案有效日期范围
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_org, pk_psndocs, calendarQueryBeginDate, calendarQueryEndDate, true);
		// 按人循环处理
		if (psndocMap == null) {
			return null;
		}
		for (int i = 0; i < pk_psndocs.length; i++) {
			Map<String, String> cloneDatePkShiftMap = new HashMap<String, String>(originalExpandedDatePkShiftMap);// 每个人单独复制一份，因为虽然是循环排班，但是最后排班的情况可能每个人都不同
			Map<String, String> beforeExgPkShiftMap = new HashMap<String, String>();
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndocs[i]);
			modifiedCalendarMap.put(pk_psndocs[i], cloneDatePkShiftMap);
			psnBeforeExgPkShiftMap.put(pk_psndocs[i], beforeExgPkShiftMap);
			// 以及处理过的天(因为涉及到对调，可能有些后面的天在前面就处理过了)
			Set<String> processedDateSet = new HashSet<String>();
			// 循环处理用户设置的循环班次
			for (int dateIndex = 0; dateIndex < dateArea.length; dateIndex++) {
				String date = dateArea[dateIndex].toString();// 日期
				if (processedDateSet.contains(date))// 处理过则不再处理
					continue;
				processedDateSet.add(date);
				String pk_shift = originalExpandedDatePkShiftMap.get(date);// 班次，因为是循环排班，因此不可能为空
				// 如果是公休
				if (ShiftVO.PK_GX.equals(pk_shift)) {// 直接调用公休的排班方法
					circularArrangeWithHolidayGX(pk_psndocs[i], psndocList, beginDate, endDate, cloneDatePkShiftMap,
							originalExpandedDatePkShiftMap, beforeExgPkShiftMap, psnExistsCalendarMap == null ? null
									: psnExistsCalendarMap.get(pk_psndocs[i]), shiftMap,
							psnEnjoyHolidayMap.get(pk_psndocs[i]), holidayCutMap, processedDateSet, holidayInfo,
							defaultShift, date, timeZone);
					continue;
				}
				// 如果不是公休，直接调用非公休的排班方法
				circularArrangeWithHolidayNonGX(pk_psndocs[i], psndocList, calendarQueryBeginDate,
						calendarQueryEndDate, cloneDatePkShiftMap, originalExpandedDatePkShiftMap, beforeExgPkShiftMap,
						psnExistsCalendarMap == null ? null : psnExistsCalendarMap.get(pk_psndocs[i]), shiftMap,
						psnEnjoyHolidayMap.get(pk_psndocs[i]), holidayCutMap, processedDateSet, holidayInfo,
						defaultShift, date, timeZone);
			}
			// 容错处理：cloneDatePkShiftMap里面不应该存储考勤档案无效的天，因此需要在此处去掉
			String[] dates = cloneDatePkShiftMap.keySet().toArray(new String[0]);
			if (!ArrayUtils.isEmpty(dates))
				for (String date : dates) {
					if (!TBMPsndocVO.isIntersect(psndocList, date))
						cloneDatePkShiftMap.remove(date);
				}
		}
		// 至此，所有的班都排完，相关的排班信息存入了：
		// 1.modifiedCalendarMap：用户要设置的班次的map；
		// 2.holidayCutMap：如果排班的过程中因为假日导致psncalendarvo要特殊处理，则放入此map
		// 进行校验：
		new CalendarShiftMutexChecker().checkCalendar(pk_org, modifiedCalendarMap, overrideExistCalendar, true, false);
		// 校验通过，持久化到数据库
		return circularArrangeWithHolidayPersistenceForTeam(pk_group, pk_org, shiftMap, pk_psndocs, beginDate, endDate,
				originalExpandedDatePkShiftMap, modifiedCalendarMap, psnBeforeExgPkShiftMap, holidayCutMap,
				psnExistsCalendarMap, overrideExistCalendar, pk_team);
	}

	/**
	 * 将循环排班的结果持久化到数据库，循环排班的时候考虑假日了 应该将循环排班的结果中已经在数据库中存在的数据删除，再insert循环排班的结果
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param modifiedCalendarMap
	 * @param holidayCutMap
	 * @return
	 * @throws BusinessException
	 */
	private void circularArrangeWithHolidayPersistence(String pk_group,
			String pk_org,
			Map<String, AggShiftVO> shiftMap, // 所有的班次
			String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate,
			Map<String, String> originalExpandedDatePkShiftMap, // 用户排的原始的班次map，key是日期，value是班次主键，已经按照日期范围展开
			Map<String, Map<String, String>> modifiedCalendarMap, // 根据用户的设置生成的排班数据，key是pk_psndoc,value的key是date，value是班次主键
			Map<String, Map<String, String>> psnBeforeExgPkShiftMap, // 记录对调前班次的map，key是人员主键，value的key是date，value是班次主键
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, // 如果假日产生了作用导致不能用默认规则生成psncalendarvo，则要用这个map里面的psncalendarvo
			Map<String, Map<String, PsnCalendarVO>> psnExistsCalendarMap, // 数据库里面已经排好的班次
			boolean isOverrideExistsCalendar) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs))
			return;
		List<String> toDelPsnCalendarPk = new ArrayList<String>();// 需要在数据库中删除的工作日历
		List<AggPsnCalendar> toInsertPsnCalendarVOList = new ArrayList<AggPsnCalendar>();// 需要insert的工作日历
		// 按人处理
		for (String pk_psndoc : pk_psndocs) {
			Map<String, String> mdfdCalendarMap = modifiedCalendarMap.get(pk_psndoc);// 此人的排班结果
			Map<String, PsnCalendarVO> existsCalendarMap = psnExistsCalendarMap == null ? null : psnExistsCalendarMap
					.get(pk_psndoc);
			Map<String, String> beforeExgPkShiftMap = psnBeforeExgPkShiftMap.get(pk_psndoc);
			Map<String, AggPsnCalendar> hldCutMap = holidayCutMap == null ? null : holidayCutMap.get(pk_psndoc);
			if (MapUtils.isEmpty(mdfdCalendarMap))
				continue;
			// 循环处理每一天
			String[] dates = mdfdCalendarMap.keySet().toArray(new String[0]);
			for (String date : dates) {
				if (existsCalendarMap != null && existsCalendarMap.get(date) != null) {// 如果这一天已经有排班，则要看用户是选择覆盖还是不覆盖
					if (!isOverrideExistsCalendar) {// 如果不覆盖，则要从mdfdCalendarMap中remove掉这一天的班
						mdfdCalendarMap.remove(date);
						continue;
					}
					// 如果覆盖，则需要在数据库中删除掉这一天的排班
					PsnCalendarVO existsCalendar = existsCalendarMap.get(date);
					toDelPsnCalendarPk.add(existsCalendar.getPk_psncalendar());
				}
				// 下面构建插入数据库的vo
				AggPsnCalendar aggVO = hldCutMap == null ? null : hldCutMap.get(date);
				if (aggVO != null) {
					PsnCalendarUtils.setGroupOrgPk2AggVO(pk_group, pk_org, aggVO, true);
					toInsertPsnCalendarVOList.add(aggVO);
					continue;
				}
				// 代码走到这里，工作日历的两个子表肯定是没数据的
				aggVO = new AggPsnCalendar();
				PsnCalendarVO calendarVO = new PsnCalendarVO();
				aggVO.setParentVO(calendarVO);
				calendarVO.setPk_psndoc(pk_psndoc);
				calendarVO.setCalendar(UFLiteralDate.getDate(date));
				calendarVO.setPk_shift(mdfdCalendarMap.get(date));
				calendarVO.setOriginal_shift_b4exg(beforeExgPkShiftMap.get(date));
				if (ShiftVO.PK_GX.equals(calendarVO.getPk_shift()) || StringUtils.isEmpty(calendarVO.getPk_shift())) {
					PsnCalendarUtils.setGX(calendarVO);
				} else {
					PsnCalendarUtils.setNonGX(calendarVO,
							ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, calendarVO.getPk_shift()).getShiftVO());
				}
				PsnCalendarUtils.setGroupOrgPk2AggVO(pk_group, pk_org, aggVO, true);
				toInsertPsnCalendarVOList.add(aggVO);
			}
		}
		try {
			PsnCalendarDAO dao = new PsnCalendarDAO();
			if (toDelPsnCalendarPk.size() > 0)
				dao.deleteByPkArray(toDelPsnCalendarPk.toArray(new String[0]));
			if (toInsertPsnCalendarVOList.size() > 0) {
				dao.insert(dealDateDayType4Insert(toInsertPsnCalendarVOList.toArray(new AggPsnCalendar[0]), pk_org,
						pk_psndocs, beginDate, endDate));
				// 业务日志
				circularlyArrangelog(pk_org, toInsertPsnCalendarVOList);
			}
		} catch (MetaDataException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		// return queryCalendarVOByPsndocs(pk_org, pk_psndocs, beginDate,
		// endDate);
	}

	/**
	 * 持久化到数据库之前,先要存入某天的日历天类型
	 * 
	 * @param vos
	 * @return
	 */
	private AggPsnCalendar[] dealDateDayType4Insert(AggPsnCalendar[] vos, String pk_Hrorg, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {

		if (null == vos || vos.length <= 0) {
			return vos;
		}
		IHRHolidayQueryService holidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		// 工作日类型map，key是人员主键，value的key是日期，value是工作日类型，见holidayvo。
		// <pk_psndoc,<date,工作日类型>>
		Map<String, Map<String, Integer>> holidayInfo = holidayService.queryPsnWorkDayTypeInfo4PsnCalenderInsert(
				pk_Hrorg, pk_psndocs, beginDate, endDate, false);
		for (AggPsnCalendar aggPsnCalendar : vos) {
			Map<String, Integer> psnDateTypeMap = holidayInfo.get(aggPsnCalendar.getPk_psndoc());
			if (null != psnDateTypeMap) {
				Integer dayType = psnDateTypeMap.get(aggPsnCalendar.getDate().toString());
				if (null != dayType) {
					aggPsnCalendar.getPsnCalendarVO().setDate_daytype(dayType);
				}
			}

		}
		return vos;
	}

	private void circularlyArrangelog(final String pk_org, final List<AggPsnCalendar> toInsertPsnCalendarVOList) {
		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				// 线程中环境信息会丢失，主动的设置一下
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				TaBusilogUtil.writeCircularlyArrangePsnCalendarBusiLog(pk_org,
						toInsertPsnCalendarVOList.toArray(new AggPsnCalendar[0]));
			}
		}).start();
	}

	/**
	 * 将循环排班的结果持久化到数据库，循环排班的时候考虑假日了 应该将循环排班的结果中已经在数据库中存在的数据删除，再insert循环排班的结果
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param modifiedCalendarMap
	 * @param holidayCutMap
	 * @return
	 * @throws BusinessException
	 */
	private PsnJobCalendarVO[] circularArrangeWithHolidayPersistenceForTeam(String pk_group,
			String pk_org,
			Map<String, AggShiftVO> shiftMap, // 所有的班次
			String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate,
			Map<String, String> originalExpandedDatePkShiftMap, // 用户排的原始的班次map，key是日期，value是班次主键，已经按照日期范围展开
			Map<String, Map<String, String>> modifiedCalendarMap, // 根据用户的设置生成的排班数据，key是pk_psndoc,value的key是date，value是班次主键
			Map<String, Map<String, String>> psnBeforeExgPkShiftMap, // 记录对调前班次的map，key是人员主键，value的key是date，value是班次主键
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, // 如果假日产生了作用导致不能用默认规则生成psncalendarvo，则要用这个map里面的psncalendarvo
			Map<String, Map<String, PsnCalendarVO>> psnExistsCalendarMap, // 数据库里面已经排好的班次
			boolean isOverrideExistsCalendar, String pk_team) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs))
			return null;
		List<String> toDelPsnCalendarPk = new ArrayList<String>();// 需要在数据库中删除的工作日历
		List<AggPsnCalendar> toInsertPsnCalendarVOList = new ArrayList<AggPsnCalendar>();// 需要insert的工作日历
		// 按人处理

		// 日期范围内这些人的考勤档案，查班组用
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_org, pk_psndocs, beginDate, endDate, true);
		if (psndocMap == null) {
			return null;
		}
		for (String pk_psndoc : pk_psndocs) {
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);
			Map<String, String> mdfdCalendarMap = modifiedCalendarMap.get(pk_psndoc);// 此人的排班结果
			Map<String, PsnCalendarVO> existsCalendarMap = psnExistsCalendarMap == null ? null : psnExistsCalendarMap
					.get(pk_psndoc);
			Map<String, String> beforeExgPkShiftMap = psnBeforeExgPkShiftMap.get(pk_psndoc);
			Map<String, AggPsnCalendar> hldCutMap = holidayCutMap == null ? null : holidayCutMap.get(pk_psndoc);
			if (MapUtils.isEmpty(mdfdCalendarMap))
				continue;
			// 循环处理每一天
			String[] dates = mdfdCalendarMap.keySet().toArray(new String[0]);
			for (String date : dates) {
				// 如果考勤档案的pk_team 不等于传过来的Pk_team则不insert
				TBMPsndocVO vo = TBMPsndocVO.getvoForTeam(psndocList, date.toString());
				if (vo == null)
					continue;
				// if(vo.getPk_team()==null)
				// continue;
				// if(!vo.getPk_team().equals(pk_team)){
				// mdfdCalendarMap.remove(date);
				// continue;
				// }
				if (vo.getPk_team() == null || !vo.getPk_team().equals(pk_team)) {
					mdfdCalendarMap.remove(date);
					continue;
				}
				if (existsCalendarMap != null && existsCalendarMap.get(date) != null) {// 如果这一天已经有排班，则要看用户是选择覆盖还是不覆盖
					if (!isOverrideExistsCalendar) {// 如果不覆盖，则要从mdfdCalendarMap中remove掉这一天的班
						mdfdCalendarMap.remove(date);
						continue;
					}
					// 如果覆盖，则需要在数据库中删除掉这一天的排班
					PsnCalendarVO existsCalendar = existsCalendarMap.get(date);
					toDelPsnCalendarPk.add(existsCalendar.getPk_psncalendar());
				}
				// 下面构建插入数据库的vo
				AggPsnCalendar aggVO = hldCutMap == null ? null : hldCutMap.get(date);
				if (aggVO != null) {
					PsnCalendarUtils.setGroupOrgPk2AggVO(pk_group, pk_org, aggVO, true);
					toInsertPsnCalendarVOList.add(aggVO);
					continue;
				}
				// 代码走到这里，工作日历的两个子表肯定是没数据的
				aggVO = new AggPsnCalendar();
				PsnCalendarVO calendarVO = new PsnCalendarVO();
				aggVO.setParentVO(calendarVO);
				calendarVO.setPk_psndoc(pk_psndoc);
				calendarVO.setCalendar(UFLiteralDate.getDate(date));
				calendarVO.setPk_shift(mdfdCalendarMap.get(date));
				calendarVO.setOriginal_shift_b4exg(beforeExgPkShiftMap.get(date));
				if (ShiftVO.PK_GX.equals(calendarVO.getPk_shift()) || StringUtils.isEmpty(calendarVO.getPk_shift())) {
					PsnCalendarUtils.setGX(calendarVO);
				} else {
					PsnCalendarUtils.setNonGX(calendarVO,
							ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, calendarVO.getPk_shift()).getShiftVO());
				}
				PsnCalendarUtils.setGroupOrgPk2AggVO(pk_group, pk_org, aggVO, true);
				toInsertPsnCalendarVOList.add(aggVO);
			}
		}
		try {
			PsnCalendarDAO dao = new PsnCalendarDAO();

			if (toDelPsnCalendarPk.size() > 0)
				dao.deleteByPkArray(toDelPsnCalendarPk.toArray(new String[0]));
			if (toInsertPsnCalendarVOList.size() > 0) {
				AggPsnCalendar[] insertArray = dealDateType4Team2Psncalendar(
						toInsertPsnCalendarVOList.toArray(new AggPsnCalendar[0]), pk_team, pk_org, beginDate, endDate);
				dao.insert(insertArray);
			}

		} catch (MetaDataException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		return queryCalendarVOByPsndocs(pk_org, pk_psndocs, beginDate, endDate);
	}

	/**
	 * 考虑假日的循环排班，当天的班次用户排为了非公休
	 * 
	 * @throws BusinessException
	 */
	private void circularArrangeWithHolidayNonGX(String pk_psndoc,
			List<TBMPsndocVO> psndocList, // 考勤档案的list
			UFLiteralDate beginDate, UFLiteralDate endDate, Map<String, String> cloneDatePkShiftMap,
			Map<String, String> originalExpandedDatePkShiftMap, // 用户排的原始的班次map，key是日期，value是班次主键，已经按照日期范围展开
			Map<String, String> beforeExgPkShiftMap, // 如果产生了对调，此map记录对调前的班次，key是date
			Map<String, PsnCalendarVO> existsCalendarMap, // 数据库中已经排的工作日历
			Map<String, AggShiftVO> shiftMap, // 业务单元内所有的班次
			Map<String, Boolean> enjoyHolidayMap, // 人员享有假日的情况，key是假日主键
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, Set<String> processedDateSet, // 已经处理过的日期set
			HolidayInfo<HRHolidayVO> holidayInfo, AggShiftVO defaultAggShiftVO, String date, TimeZone timeZone)
			throws BusinessException {

		String pk_shift = originalExpandedDatePkShiftMap.get(date);
		// 如果此天不是对调日，或者是对调日但此人不享受假日，则按用户设置排班，不用特殊处理
		if (!holidayInfo.getSwitchMap().containsKey(date)) {
			processHolidayCut(pk_psndoc, cloneDatePkShiftMap,
					ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, pk_shift), holidayInfo.getHolidayVOs(),
					enjoyHolidayMap, holidayCutMap, date, timeZone);
			return;
		}
		HolidayVO holidayVO = holidayInfo.getSwitchHolidayMap().get(date);
		if (!holidayVO.isAllEnjoy() && !enjoyHolidayMap.get(holidayVO.getPk_holiday())) {// 当前人员不享受此假日
			processHolidayCut(pk_psndoc, cloneDatePkShiftMap,
					ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, pk_shift), holidayInfo.getHolidayVOs(),
					enjoyHolidayMap, holidayCutMap, date, timeZone);
			return;
		}
		// 代码走到这里，此天肯定是对调日

		String switchDate = holidayInfo.getSwitchMap().get(date);// 对调日
		processedDateSet.add(switchDate);// 对调日打上处理标志
		// 看对调日是否在日期范围内,如果在范围内，那么对调是肯定要发生的，
		if (switchDate.compareTo(beginDate.toString()) >= 0 && switchDate.compareTo(endDate.toString()) <= 0) {
			exchangeDateInDateAreaNonGX(pk_psndoc, cloneDatePkShiftMap, pk_shift,
					originalExpandedDatePkShiftMap.get(switchDate), beforeExgPkShiftMap, shiftMap, enjoyHolidayMap,
					holidayCutMap, holidayInfo, date, switchDate, timeZone);
			return;
		}
		// 如果对调日在此次设置班次的日期范围之外，则需要看对调日期的排班状况，如果
		// 1.有排班，且排班为遇假日取消，则跟上面对调的逻辑一样
		// 2.有排班，且排班为与假日照旧，则不对调
		// 3.无排班，则自动对调日排上一个默认班（周一到周五），或者公休（周六日），然后执行对调，对调的结果是两天都是遇假日取消
		// 注意：如果对调日属于考勤档案无效的日期，则等同于情况3
		// 1,3可以合并处理，3可以看作1的一种特例
		// 下面从已排的班次中取出对调日的班
		PsnCalendarVO switchCalendar = existsCalendarMap == null ? null : existsCalendarMap.get(switchDate);
		exchangeDateOutDateAreaNonGX(pk_psndoc, cloneDatePkShiftMap, pk_shift, switchCalendar, beforeExgPkShiftMap,
				psndocList, shiftMap, enjoyHolidayMap, holidayCutMap, holidayInfo, defaultAggShiftVO, date, switchDate,
				timeZone);
	}

	/**
	 * 处理对调，对调日在循环排班的日期范围之外，且用户给当日排了非公休
	 * 与exchangeDateInDateAreaNonGX方法是一对，一个处理对调日在范围内，一个处理对调日在范围外
	 * 
	 * @throws BusinessException
	 */
	private void exchangeDateOutDateAreaNonGX(String pk_psndoc, Map<String, String> cloneDatePkShiftMap,
			String originalPkShift, PsnCalendarVO switchCalendar,
			Map<String, String> beforeExgPkShiftMap, // 如果产生了对调，此map记录对调前的班次，key是date
			List<TBMPsndocVO> psndocList, Map<String, AggShiftVO> shiftMap,
			Map<String, Boolean> enjoyHolidayMap, // 人员享有假日的情况，key是假日主键
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, // 如果PsnCalendarVO需要特殊处理，则将处理结果放入这个map
			HolidayInfo<HRHolidayVO> holidayInfo, AggShiftVO defaultAggShiftVO, String date, String switchDate,
			TimeZone timeZone) throws BusinessException {
		// 如果对调日排班了，且对调日属于有效的考勤档案，且对调日遇假日照旧，这种情况不对调
		if (switchCalendar != null && TBMPsndocVO.isIntersect(psndocList, switchDate)
				&& !switchCalendar.isHolidayCancel()) {
			processHolidayCut(pk_psndoc, cloneDatePkShiftMap,
					ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, originalPkShift), holidayInfo.getHolidayVOs(),
					enjoyHolidayMap, holidayCutMap, date, timeZone);
			return;
		}
		beforeExgPkShiftMap.put(date, originalPkShift);// 当日对调前的班次
		// 如果没排班或者日期不属于有效的考勤档案日期或者对调日遇假日取消
		// 对调日的班肯定排当日的班,即两个班对调
		processHolidayCut(pk_psndoc, cloneDatePkShiftMap,
				ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, originalPkShift), holidayInfo.getHolidayVOs(),
				enjoyHolidayMap, holidayCutMap, switchDate, timeZone);
		String switchPkShift = null;// 对调日的班次
		if (switchCalendar == null || !TBMPsndocVO.isIntersect(psndocList, switchDate)) {
			// 如果对调日是周一到周五，则对调班次可以视为默认班，否则视为公休(默认班为空的话，也视为公休)
			switchPkShift = TACalendarUtils.getPkShiftByDate(switchDate, defaultAggShiftVO);
		} else {// 对调日排班了，且日期有效，且遇假日取消
			switchPkShift = switchCalendar.getPk_shift();
		}
		beforeExgPkShiftMap.put(switchDate, switchPkShift);// 对调日对调前的班次
		if (ShiftVO.PK_GX.equals(switchPkShift)) {
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);// 对调日的班次是公休，那么当日也肯定是公休了
			return;
		}
		// 对调日班次非公休，则要看对调日的班次的工作时间段（对于弹性班，是最大工作时间段）与假日是否有交集（对于弹性班，最大工作时间段与假日有交集会导致固化，但不一定会导致工作时间段被切割）
		AggShiftVO switchShiftAggVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, switchPkShift);
		processHolidayCut(pk_psndoc, cloneDatePkShiftMap, switchShiftAggVO, holidayInfo.getHolidayVOs(),
				enjoyHolidayMap, holidayCutMap, switchDate, timeZone);
	}

	/**
	 * 处理对调，对调日在循环排班的日期范围内，且用户给当日排了非公休
	 * 与exchangeDateOutDateAreaNonGX方法是一对，一个处理对调日在范围内，一个处理对调日在范围外
	 * 
	 * @throws BusinessException
	 */
	private void exchangeDateInDateAreaNonGX(String pk_psndoc,
			Map<String, String> cloneDatePkShiftMap,
			String originalPkShift, // 当日用户排的原始的班次
			String originalSwitchDayPkShift, // 对调日用户排的原始的班次
			Map<String, String> beforeExgPkShiftMap, // 如果产生了对调，此map记录对调前的班次，key是date
			Map<String, AggShiftVO> shiftMap,
			Map<String, Boolean> enjoyHolidayMap, // 人员享有假日的情况，key是假日主键
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, HolidayInfo<HRHolidayVO> holidayInfo, String date,
			String switchDate, TimeZone timeZone) throws BusinessException {
		beforeExgPkShiftMap.put(date, originalPkShift);// 记录当日对调前的班次
		beforeExgPkShiftMap.put(switchDate, originalSwitchDayPkShift);// 记录对调日对调前的班次
		// 如果对调日的班次是公休，那么此天也排公休，
		if (ShiftVO.PK_GX.equals(originalSwitchDayPkShift)) {
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);
		} else {// 对调日不是公休，那么将对调日的班次掉到当日，然后处理当日班次与假日的交集即可

			processHolidayCut(pk_psndoc, cloneDatePkShiftMap,
					ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, originalSwitchDayPkShift),
					holidayInfo.getHolidayVOs(), enjoyHolidayMap, holidayCutMap, date, timeZone);
		}
		// 对调日的班次也要处理
		processHolidayCut(pk_psndoc, cloneDatePkShiftMap,
				ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, originalPkShift), holidayInfo.getHolidayVOs(),
				enjoyHolidayMap, holidayCutMap, switchDate, timeZone);
		return;
	}

	/**
	 * 考虑假日的循环排班，当天的班次用户排为了公休
	 * 
	 * @throws BusinessException
	 */
	private void circularArrangeWithHolidayGX(String pk_psndoc, List<TBMPsndocVO> psndocList, // 考勤档案的list.方法从中取值，不放值
			UFLiteralDate beginDate, UFLiteralDate endDate, Map<String, String> cloneDatePkShiftMap, // 方法往里放值
			Map<String, String> originalExpandedDatePkShiftMap, // 用户排的原始的班次map，key是日期，value是班次主键，已经按照日期范围展开，方法从里取值
			Map<String, String> beforeExgPkShiftMap, // 如果产生了对调，此map记录对调前的班次，key是date，方法往里放值
			Map<String, PsnCalendarVO> existsCalendarMap, // 数据库中已经排的工作日历，方法从里取值
			Map<String, AggShiftVO> shiftMap, // 组织内所有的班次，方法从里取值
			Map<String, Boolean> enjoyHolidayMap, // 人员享有假日的情况，key是假日主键，从里取值
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, // 方法往里放值
			Set<String> processedDateSet, // 已经处理过的日期set，方法取值，也放值
			HolidayInfo<HRHolidayVO> holidayInfo, AggShiftVO defaultShift, String date, TimeZone timeZone)
			throws BusinessException {

		// 如果此天不涉及到对调，或者涉及对调但此人不享受此假日，则公休依然是公休,不受影响
		if (!holidayInfo.getSwitchMap().containsKey(date))// 此天不涉及对调
			return;
		HolidayVO holidayVO = holidayInfo.getSwitchHolidayMap().get(date);
		if (!holidayVO.isAllEnjoy() && !enjoyHolidayMap.get(holidayVO.getPk_holiday()))// 当前人员不享受此假日
			return;
		// 走到这里，date肯定涉及对调，且人员享受此假日
		// 如果涉及到对调，则要看对调日的班次：
		String switchDate = holidayInfo.getSwitchMap().get(date);
		processedDateSet.add(switchDate);
		// 如果对调日在此次设置班次的日期范围内，则：当日排对调日的班（如果非公休且与假日相切，还要减去假日），对调日排公休
		if (switchDate.compareTo(beginDate.toString()) >= 0 && switchDate.compareTo(endDate.toString()) <= 0) {
			exchangeDateInDateAreaGX(pk_psndoc, cloneDatePkShiftMap, originalExpandedDatePkShiftMap.get(switchDate),
					beforeExgPkShiftMap, shiftMap, enjoyHolidayMap, holidayCutMap, holidayInfo, date, switchDate,
					timeZone);
			return;
		}
		// 如果对调日在此次设置班次的日期范围之外，则需要看对调日期的排班状况，如果
		// 1.有排班，且排班为遇假日取消，则跟上面对调的逻辑一样
		// 2.有排班，且排班为与假日照旧，则不对调
		// 3.无排班，则自动对调日排上一个默认班（周一到周五），或者公休（周六日），然后执行对调，对调的结果是两天都是遇假日取消
		// 注意：如果对调日属于考勤档案无效的日期，则等同于情况3
		// 1,3可以合并处理，3可以看作1的一种特例
		// 下面从已排的班次中取出对调日的班
		PsnCalendarVO switchCalendar = existsCalendarMap == null ? null : existsCalendarMap.get(switchDate);
		exchangeDateOutDateAreaGX(pk_psndoc, cloneDatePkShiftMap, switchCalendar, beforeExgPkShiftMap, psndocList,
				shiftMap, enjoyHolidayMap, holidayCutMap, holidayInfo, defaultShift, date, switchDate, timeZone);
	}

	/**
	 * 处理一个班次在某天的假日切割情况
	 * 有可能班次与假日完全不搭，这个时候psncalendarvo不用特殊处理。有可能psncalendarvo又需要特殊处理，例如假日切割了工作段，
	 * 又例如假日导致弹性班固化了 如果psncalendarvo不用特殊处理，则返回null，否则返回AggPsnCalendar
	 * 
	 * @param aggShiftVO
	 * @param holidayVOs
	 * @param enjoyHolidayMap
	 * @param date
	 * @param timeZone
	 */
	private void processHolidayCut(String pk_psndoc, Map<String, String> cloneDatePkShiftMap, AggShiftVO aggShiftVO,
			HolidayVO[] holidayVOs, Map<String, Boolean> enjoyHolidayMap,
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, String date, TimeZone timeZone) {
		// 找出与工作时间有交集的假日
		HolidayVO[] crossedHolidayVOs = TACalendarUtils.findCrossedHolidayVOs(aggShiftVO, holidayVOs, date, timeZone,
				enjoyHolidayMap);
		// 如果假日无交集，则正常处理
		if (ArrayUtils.isEmpty(crossedHolidayVOs)) {
			cloneDatePkShiftMap.put(date, aggShiftVO == null ? null : aggShiftVO.getShiftVO().getPrimaryKey());
			return;
		}
		Map<String, AggPsnCalendar> cutMap = holidayCutMap.get(pk_psndoc);
		if (cutMap == null) {
			cutMap = new HashMap<String, AggPsnCalendar>();
			holidayCutMap.put(pk_psndoc, cutMap);
		}
		processHolidayCut2(pk_psndoc, cloneDatePkShiftMap, aggShiftVO, crossedHolidayVOs, enjoyHolidayMap, cutMap,
				date, timeZone);
	}

	/**
	 * 处理一个班次在某天的假日切割情况。此方法调用时，应该保证工作时间段与假日有交集
	 */
	private void processHolidayCut2(String pk_psndoc, Map<String, String> cloneDatePkShiftMap, AggShiftVO shiftVO,
			HolidayVO[] holidayVOs, Map<String, Boolean> enjoyHolidayMap, Map<String, AggPsnCalendar> cutMap,
			String date, TimeZone timeZone) {
		AggPsnCalendar cutCalendar = createHolidayCutAggPsnCalendarVO(pk_psndoc, cloneDatePkShiftMap, shiftVO,
				holidayVOs, enjoyHolidayMap, date, timeZone);
		cutMap.put(date, cutCalendar);
	}

	/**
	 * 处理一个班次在某天的假日切割情况。此方法调用时，应该保证工作时间段与假日有交集
	 */
	private AggPsnCalendar createHolidayCutAggPsnCalendarVO(String pk_psndoc, Map<String, String> cloneDatePkShiftMap,
			AggShiftVO shiftVO, HolidayVO[] holidayVOs, Map<String, Boolean> enjoyHolidayMap, String date,
			TimeZone timeZone) {
		AggPsnCalendar cutCalendar = PsnCalendarUtils.createHolidayCutAggPsnCalendarVO(pk_psndoc, shiftVO, holidayVOs,
				date, timeZone);
		cloneDatePkShiftMap.put(date, cutCalendar.getPsnCalendarVO().getPk_shift());
		return cutCalendar;
	}

	/**
	 * 处理对调，对调日在循环排班的日期范围内，且用户给当日排了公休
	 * 与exchangeDateOutDateAreaGX方法是一对，一个处理对调日在范围内，一个处理对调日在范围外
	 * 
	 * @throws BusinessException
	 */
	private void exchangeDateInDateAreaGX(String pk_psndoc,
			Map<String, String> cloneDatePkShiftMap,
			String originalSwitchDayPkShift, // 对调日用户排的原始的班次
			Map<String, String> beforeExgPkShiftMap, // 如果产生了对调，此map记录对调前的班次，key是date
			Map<String, AggShiftVO> shiftMap,
			Map<String, Boolean> enjoyHolidayMap, // 人员享有假日的情况，key是假日主键
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, HolidayInfo<HRHolidayVO> holidayInfo, String date,
			String switchDate, TimeZone timeZone) throws BusinessException {
		beforeExgPkShiftMap.put(date, ShiftVO.PK_GX);// 记录当日对调前的班次
		beforeExgPkShiftMap.put(switchDate, originalSwitchDayPkShift);// 记录对调日对调前的班次
		cloneDatePkShiftMap.put(switchDate, ShiftVO.PK_GX);// 对调日肯定是公休了
		if (ShiftVO.PK_GX.equals(originalSwitchDayPkShift)) {
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);// 对调日的班次是公休，那么当日也肯定是公休了
			return;
		}
		// 对调日班次非公休，则要看对调日的班次的工作时间段（对于弹性班，是最大工作时间段）与假日是否有交集（对于弹性班，最大工作时间段与假日有交集会导致固化，但不一定会导致工作时间段被切割）
		AggShiftVO switchShiftAggVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, originalSwitchDayPkShift);
		processHolidayCut(pk_psndoc, cloneDatePkShiftMap, switchShiftAggVO, holidayInfo.getHolidayVOs(),
				enjoyHolidayMap, holidayCutMap, date, timeZone);
	}

	/**
	 * 处理对调，对调日在循环排班的日期范围之外，且用户给当日排了公休
	 * 与exchangeDateInDateAreaGX方法是一对，一个处理对调日在范围内，一个处理对调日在范围外
	 * 
	 * @throws BusinessException
	 */
	private void exchangeDateOutDateAreaGX(String pk_psndoc, Map<String, String> cloneDatePkShiftMap,
			PsnCalendarVO switchCalendar,
			Map<String, String> beforeExgPkShiftMap, // 如果产生了对调，此map记录对调前的班次，key是date
			List<TBMPsndocVO> psndocList, Map<String, AggShiftVO> shiftMap,
			Map<String, Boolean> enjoyHolidayMap, // 人员享有假日的情况，key是假日主键
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, // 如果PsnCalendarVO需要特殊处理，则将处理结果放入这个map
			HolidayInfo<HRHolidayVO> holidayInfo, AggShiftVO defaultAggShiftVO, String date, String switchDate,
			TimeZone timeZone) throws BusinessException {

		// 如果对调日排班了，且对调日属于有效的考勤档案，且对调日遇假日照旧，这种情况不对调
		if (switchCalendar != null && TBMPsndocVO.isIntersect(psndocList, switchDate)
				&& !switchCalendar.isHolidayCancel()) {
			return;
		}
		// 如果没排班或者日期不属于有效的考勤档案日期或者对调日遇假日取消，这种情况下要对调
		beforeExgPkShiftMap.put(date, ShiftVO.PK_GX);// 记录当日对调前的班次
		// 对调日的班肯定是公休
		cloneDatePkShiftMap.put(switchDate, ShiftVO.PK_GX);// 对调日肯定是公休了
		String switchPkShift = null;
		if (switchCalendar == null || !TBMPsndocVO.isIntersect(psndocList, switchDate)) {
			// 如果对调日是周一到周五，则对调班次可以视为默认班，否则视为公休(默认班为空的话，也视为公休)
			switchPkShift = TACalendarUtils.getPkShiftByDate(switchDate, defaultAggShiftVO);
		} else {// 对调日排班了，且日期有效，且遇假日取消
			switchPkShift = switchCalendar.getPk_shift();
		}
		beforeExgPkShiftMap.put(switchDate, switchPkShift);// 记录对调日对调前的班次
		if (ShiftVO.PK_GX.equals(switchPkShift)) {
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);// 对调日的班次是公休，那么当日也肯定是公休了
			return;
		}
		// 对调日班次非公休，则要看对调日的班次的工作时间段（对于弹性班，是最大工作时间段）与假日是否有交集（对于弹性班，最大工作时间段与假日有交集会导致固化，但不一定会导致工作时间段被切割）
		AggShiftVO switchShiftAggVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, switchPkShift);
		processHolidayCut(pk_psndoc, cloneDatePkShiftMap, switchShiftAggVO, holidayInfo.getHolidayVOs(),
				enjoyHolidayMap, holidayCutMap, switchDate, timeZone);
	}

	/**
	 * 对下面的方法分批执行
	 * 
	 * @param pk_group
	 * @param pk_hrorg
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param calendarPks
	 * @param isHolidayCancel
	 * @param overrideExistCalendar
	 * @throws BusinessException
	 */
	protected void circularArrangeIgnoreHolidayBatch(String pk_group, String pk_hrorg, String pk_org,
			String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate, String[] calendarPks,
			boolean isHolidayCancel, boolean overrideExistCalendar) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs)) {
			return;
		}
		// 业务单元内所有班次
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		Set<String> pkset = new HashSet<String>();
		int count = 0;
		for (String pk_psndoc : pk_psndocs) {
			count++;
			pkset.add(pk_psndoc);
			if (count > 499) {
				circularArrangeIgnoreHoliday(pk_group, pk_hrorg, pk_org, pkset.toArray(new String[0]), beginDate,
						endDate, calendarPks, isHolidayCancel, overrideExistCalendar, shiftMap);
				count = 0;
				pkset.clear();
			}
		}
		circularArrangeIgnoreHoliday(pk_group, pk_hrorg, pk_org, pkset.toArray(new String[0]), beginDate, endDate,
				calendarPks, isHolidayCancel, overrideExistCalendar, shiftMap);
	}

	/**
	 * 2011.9.28,工作日历业务单元化修改完成 循环排班，不考虑假日
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param calendarPks
	 * @param overrideExistCalendar
	 * @return
	 * @throws BusinessException
	 */
	protected void circularArrangeIgnoreHoliday(String pk_group, String pk_hrorg, String pk_org, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate, String[] calendarPks, boolean isHolidayCancel,
			boolean overrideExistCalendar, Map<String, AggShiftVO> shiftMap) throws BusinessException {

		// 将循环的班次按日期范围全部展开
		Map<String, String> pkMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate, endDate, calendarPks);
		// 构造所有人的排班map，校验方法需要
		Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String, String>>();
		for (String pk_psndoc : pk_psndocs) {
			modifiedCalendarMap.put(pk_psndoc, pkMap);
		}
		new CalendarShiftMutexChecker().checkCalendar(pk_org, modifiedCalendarMap, overrideExistCalendar, true, false);
		// 至此，校验通过，可以持久化到数据库了
		// 组织内所有班次
		// Map<String, AggBclbDefVO> shiftMap =
		// NCLocator.getInstance().lookup(IBclbQueryService.class).queryBclbAggVOMap(pk_org);
		// Map<String, AggShiftVO> shiftMap =
		// NCLocator.getInstance().lookup(IShiftQueryService.class).queryShiftAggVOMapByOrg(pk_org);
		// 业务单元内考勤档案Map，key是人员主键,此map主要来描述人员考勤档案有效日期范围
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_org, pk_psndocs, beginDate, endDate, true);
		if (MapUtils.isEmpty(psndocMap))
			return;
		// String pk_hrorg =
		// NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		// 已有工作日历
		Map<String, Map<String, String>> existsPsnCalendarMap = new PsnCalendarDAO().queryPkShiftMapByPsndocs(pk_hrorg,
				pk_psndocs, beginDate, endDate);
		List<AggPsnCalendar> insertList = new ArrayList<AggPsnCalendar>();
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		for (String pk_psndoc : pk_psndocs) {
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);
			for (UFLiteralDate date : allDates) {
				if (!TBMPsndocVO.isIntersect(psndocList, date.toString()))// 如果当日不在考勤档案范围内，则不insert
					continue;
				// 如果用户选择不覆盖，且当日已有工作日历，则也不insert
				if (!overrideExistCalendar) {
					if (existsPsnCalendarMap != null && existsPsnCalendarMap.containsKey(pk_psndoc)
							&& existsPsnCalendarMap.get(pk_psndoc).containsKey(date.toString()))
						continue;
				}
				AggPsnCalendar calendarVO = PsnCalendarUtils.createAggVOByShiftVO(pk_psndoc, pk_group, pk_org,
						date.toString(), pkMap.get(date.toString()), shiftMap, isHolidayCancel);
				insertList.add(calendarVO);
			}
		}
		if (insertList.size() == 0)
			return;
		PsnCalendarDAO dao = new PsnCalendarDAO();
		if (overrideExistCalendar)// 如果用户选择覆盖已有排班，则需要将日期范围内的工作日历删除掉
			dao.deleteByPsndocsAndDateArea(pk_org, pk_psndocs, beginDate, endDate);

		dao.insert(dealDateDayType4Insert(insertList.toArray(new AggPsnCalendar[0]), pk_hrorg, pk_psndocs, beginDate,
				endDate));
		circularlyArrangelog(pk_hrorg, insertList);
		// return queryCalendarVOByPsndocs(pk_hrorg, pk_psndocs, beginDate,
		// endDate);
	}

	protected PsnJobCalendarVO[] circularArrangeIgnoreHolidayForTeam(String pk_group, String pk_org,
			String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate, String[] calendarPks,
			boolean isHolidayCancel, boolean overrideExistCalendar, String pk_team) throws BusinessException {

		// 将循环的班次按日期范围全部展开
		Map<String, String> pkMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate, endDate, calendarPks);
		// 构造所有人的排班map，校验方法需要
		Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String, String>>();
		for (String pk_psndoc : pk_psndocs) {
			modifiedCalendarMap.put(pk_psndoc, pkMap);
		}
		new CalendarShiftMutexChecker().checkCalendar(pk_org, modifiedCalendarMap, overrideExistCalendar, true, false);
		// 至此，校验通过，可以持久化到数据库了
		// 组织内所有班次
		// Map<String, AggBclbDefVO> shiftMap =
		// NCLocator.getInstance().lookup(IBclbQueryService.class).queryBclbAggVOMap(pk_org);
		Map<String, AggShiftVO> shiftMap = NCLocator.getInstance().lookup(IShiftQueryService.class)
				.queryShiftAggVOMapByOrg(pk_org);
		// 业务单元内考勤档案Map，key是人员主键,此map主要来描述人员考勤档案有效日期范围
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_org, pk_psndocs, beginDate, endDate, true);
		if (MapUtils.isEmpty(psndocMap))
			return null;
		String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		// 已有工作日历
		Map<String, Map<String, String>> existsPsnCalendarMap = new PsnCalendarDAO().queryPkShiftMapByPsndocs(pk_hrorg,
				pk_psndocs, beginDate, endDate);
		List<AggPsnCalendar> insertList = new ArrayList<AggPsnCalendar>();
		List<AggPsnCalendar> noDeleteList = new ArrayList<AggPsnCalendar>();
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		InSQLCreator isc = null;
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> existAggPsnCalendarMap = null;
		try {
			isc = new InSQLCreator();
			String pk_psndocsinsql = isc.getInSQL(pk_psndocs);
			existAggPsnCalendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class)
					.queryCalendarVOByCondition(pk_org, beginDate, endDate, pk_psndocsinsql);

			for (String pk_psndoc : pk_psndocs) {
				List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);
				for (UFLiteralDate date : allDates) {
					if (!TBMPsndocVO.isIntersect(psndocList, date.toString()))// 如果当日不在考勤档案范围内，则不insert
						continue;
					// 如果考勤档案的pk_team 不等于传过来的Pk_team则不insert
					TBMPsndocVO vo = TBMPsndocVO.getvoForTeam(psndocList, date.toString());
					if (vo == null)
						continue;
					if (existsPsnCalendarMap != null && existsPsnCalendarMap.containsKey(pk_psndoc)
							&& existsPsnCalendarMap.get(pk_psndoc).containsKey(date.toString())) {
						if (vo.getPk_team() == null || !vo.getPk_team().equals(pk_team)) {
							AggPsnCalendar oldAggVO = existAggPsnCalendarMap.get(pk_psndoc).get(date);
							oldAggVO.getPsnCalendarVO().setStatus(VOStatus.NEW);
							oldAggVO.getPsnCalendarVO().setPk_psncalendar(null);
							noDeleteList.add(oldAggVO);
							continue;
						}
					}
					// 如果用户选择不覆盖，且当日已有工作日历，则也不insert
					if (!overrideExistCalendar) {
						if (existsPsnCalendarMap != null && existsPsnCalendarMap.containsKey(pk_psndoc)
								&& existsPsnCalendarMap.get(pk_psndoc).containsKey(date.toString()))
							continue;
					}
					AggPsnCalendar calendarVO = PsnCalendarUtils.createAggVOByShiftVO(pk_psndoc, pk_group, pk_org,
							date.toString(), pkMap.get(date.toString()), shiftMap, isHolidayCancel);

					insertList.add(calendarVO);
				}
			}

			if (insertList.size() == 0)
				return null;
			PsnCalendarDAO dao = new PsnCalendarDAO();
			// 查询班组的日历天类型,set到员工的日历天类型上
			if (overrideExistCalendar) {// 如果用户选择覆盖已有排班，则需要将日期范围内的工作日历删除掉
				dao.deleteByPsndocsAndDateArea(pk_org, pk_psndocs, beginDate, endDate);
				// 上一步的删除，会把从属于别的班组的排班也删掉，是错误的，要重新加上
				AggPsnCalendar[] noDeleteArray = dealDateType4Team2Psncalendar(
						noDeleteList.toArray(new AggPsnCalendar[0]), pk_team, pk_org, beginDate, endDate);
				dao.insert(noDeleteArray);
			}
			AggPsnCalendar[] insertArray = dealDateType4Team2Psncalendar(insertList.toArray(new AggPsnCalendar[0]),
					pk_team, pk_org, beginDate, endDate);
			dao.insert(insertArray);
			return queryCalendarVOByPsndocs(pk_hrorg, pk_psndocs, beginDate, endDate);
		} finally {
			if (isc != null)
				isc.clear();
		}
	}

	/**
	 * 查询班组工作日历的日历天类型set到员工工作日历上
	 * 
	 * @param array
	 * @param pk_team
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	private AggPsnCalendar[] dealDateType4Team2Psncalendar(AggPsnCalendar[] array, String pk_team, String pk_org,
			UFLiteralDate beginDate, UFLiteralDate endDate) {
		if (null == array || array.length <= 0 || null == pk_team) {
			return array;
		}
		// 查找某些人员和某些日期相对应的datetype
		String sql = "select bd_teamcalendar.calendar,bd_teamcalendar.date_daytype " + "from bd_teamcalendar  "
				+ "where bd_teamcalendar.calendar >='" + beginDate + "'  " + "and bd_teamcalendar.calendar <='"
				+ endDate + "'  " + "and  bd_teamcalendar.pk_org ='" + pk_org + "' " + "and dr = 0 "
				+ "and bd_teamcalendar.pk_team = '" + pk_team + "' ";
		List<HashMap<String, Object>> result = new ArrayList<>();// 数据库返回结果
		// 封装后的结果 Map<date,datetype>,不让循环调用,就只能全丢内存了,
		Map<String, Integer> resultPackage = new HashMap<>();
		try {
			result = (ArrayList<HashMap<String, Object>>) getBaseDAO().executeQuery(sql, new MapListProcessor());
			for (HashMap<String, Object> colMap : result) {
				if (null != colMap.get("calendar") && null != colMap.get("date_daytype")) {
					resultPackage.put(colMap.get("calendar").toString(), (Integer) colMap.get("date_daytype"));
				}
			}

		} catch (BusinessException e) {
			result = new ArrayList<>();
			e.printStackTrace();
		}
		// 将班组的工作日历天类型set员工的日历天类型上
		// for(int i = 0 ;i<array.length;i++){
		for (AggPsnCalendar apc : array) {

			if (null != apc && null != apc.getPsnCalendarVO().getCalendar()
					&& null != resultPackage.get(apc.getPsnCalendarVO().getCalendar().toString())) {

				apc.getPsnCalendarVO().setDate_daytype(
						resultPackage.get(apc.getPsnCalendarVO().getCalendar().toString()));
			}

		}
		return array;
	}

	/**
	 * 循环排班的简单校验，不涉及到具体的人
	 * 
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @param calendarPks
	 * @throws BusinessException
	 */
	// private void simpleCheckCircularArrange(String pk_org,UFLiteralDate
	// beginDate, UFLiteralDate endDate,
	// String[] calendarPks)throws BusinessException{
	// Set<ShiftMutexBUVO>[] allMutexSet =
	// PsnCalendarUtils.splitMutexVOSet((ShiftMutexBUVO[])null);
	// //紧挨着冲突的
	// Set<ShiftMutexBUVO> mutexSet0 = allMutexSet[0];
	// //紧挨着颠倒的
	// Set<ShiftMutexBUVO> reverseSet0 = allMutexSet[1];
	// //隔一天冲突的
	// Set<ShiftMutexBUVO> mutexSet1 = allMutexSet[2];
	// //隔一天颠倒的
	// Set<ShiftMutexBUVO> reverseSet1 = allMutexSet[3];
	// //如果四种情况都没有数据，则表示此业务单元内无论怎么排班都不会冲突，校验不必进行
	// if(
	// CollectionUtils.isEmpty(mutexSet0)||
	// CollectionUtils.isEmpty(reverseSet0)||
	// CollectionUtils.isEmpty(mutexSet1)||
	// CollectionUtils.isEmpty(reverseSet1))
	// return;
	// //将循环的班次按日期范围全部展开
	// String[] pks = new String[endDate.getDaysAfter(beginDate)+1];
	// for(int i=0;i<pks.length;i++){
	// pks[i]=calendarPks[i%calendarPks.length];
	// }
	// String errMsg ="{1}的班次为{2},{3}的班次为{4},发生了班次冲突!";
	// //组织内所有班次
	// // Map<String, AggBclbDefVO> shiftMap =
	// NCLocator.getInstance().lookup(IBclbQueryService.class).queryBclbAggVOMap(pk_org);
	// Map<String, AggShiftVO> shiftMap =
	// NCLocator.getInstance().lookup(IShiftQueryService.class).queryShiftAggVOMapByOrg(pk_org);
	// for(int i=0;i<pks.length;i++){
	// String curPk=pks[i];
	// if(StringHelper.isEmpty(curPk)||ShiftVO.PK_GX.equals(curPk))
	// continue;
	// //前一天班次
	// String former1 = i==0?null:pks[i-1];
	// if(!StringHelper.isEmpty(former1)&&!ShiftVO.PK_GX.equals(former1)&&!isValid(former1,
	// curPk, mutexSet0, reverseSet0))
	// throw new ValidationException(getErrMsg(errMsg, null,
	// beginDate.getDateAfter(i-1).toString(), former1,
	// beginDate.getDateAfter(i).toString(), curPk, shiftMap));
	// //前两天班次
	// String former2 = i<=1?null:pks[i-2];
	// if(!StringHelper.isEmpty(former2)&&!ShiftVO.PK_GX.equals(former2)&&!isValid(former2,
	// curPk, mutexSet1, reverseSet1))
	// throw new ValidationException(getErrMsg(errMsg, null,
	// beginDate.getDateAfter(i-2).toString(), former2,
	// beginDate.getDateAfter(i).toString(), curPk, shiftMap));
	// //后一天班次
	// String next1 = i==(pks.length-1)?null:pks[i+1];
	// if(!StringHelper.isEmpty(next1)&&!ShiftVO.PK_GX.equals(next1)&&!isValid(curPk,
	// next1, mutexSet0, reverseSet0))
	// throw new ValidationException(getErrMsg(errMsg, null,
	// beginDate.getDateAfter(i).toString(), curPk,
	// beginDate.getDateAfter(i+1).toString(), next1, shiftMap));
	// //后两天班次
	// String next2 = i>=(pks.length-2)?null:pks[i+2];
	// if(!StringHelper.isEmpty(next2)&&!ShiftVO.PK_GX.equals(next2)&&!isValid(curPk,
	// next2, mutexSet1, reverseSet1))
	// throw new ValidationException(getErrMsg(errMsg, null,
	// beginDate.getDateAfter(i).toString(), curPk,
	// beginDate.getDateAfter(i+2).toString(), next2, shiftMap));
	// }
	// }

	@Override
	public PsnJobCalendarVO[] save(String pk_hrorg, PsnJobCalendarVO[] vos) throws BusinessException {
		return save(pk_hrorg, vos, true, true);
	}

	/**
	 * 工作日历保存
	 * 
	 * @param pk_org
	 * @param vos
	 * @param log
	 * @param checkPsndocEffective
	 *            ，是否校验考勤档案
	 * @return
	 * @throws BusinessException
	 */
	protected PsnJobCalendarVO[] save(String pk_hrorg, PsnJobCalendarVO[] vos, boolean checkPsndocEffective, boolean log)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return vos;
		checkCalendarWhenSave(pk_hrorg, vos);// 校验
		PsnCalendarDAO dao = new PsnCalendarDAO();
		dao.deleteExistsCalendarWhenSave(vos);// 删除已有记录
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		List<AggPsnCalendar> insertList = new ArrayList<AggPsnCalendar>();
		// HR组织内所有班次
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_hrorg);
		for (PsnJobCalendarVO vo : vos) {
			if (vo.getModifiedCalendarMap() == null || vo.getModifiedCalendarMap().size() == 0)
				continue;
			for (String date : vo.getModifiedCalendarMap().keySet()) {
				String pk_shift = vo.getModifiedCalendarMap().get(date);
				String pk_org = vo.getOrgMap().get(date);
				vo.getCalendarMap().put(date, pk_shift);
				if (StringUtils.isEmpty(pk_shift)
						|| (checkPsndocEffective && !vo.getPsndocEffectiveDateSet().contains(date)))
					continue;
				AggPsnCalendar calendarVO = PsnCalendarUtils.createAggVOByShiftVO(vo.getPk_psndoc(), pk_group, pk_org,
						date, pk_shift, shiftMap, false);
				// set日历天类型
				calendarVO.getPsnCalendarVO().setDate_daytype(vo.getDayTypeMap().get(date));
				insertList.add(calendarVO);
			}
			vo.getModifiedCalendarMap().clear();// 清空存储修改数据的map
		}
		if (insertList.size() > 0) {
			AggPsnCalendar[] aggvos = insertList.toArray(new AggPsnCalendar[0]);
			PsnCalendarVO[] psnCalvos = new PsnCalendarVO[aggvos.length];
			for (int i = 0; i < aggvos.length; i++) {
				psnCalvos[i] = aggvos[i].getPsnCalendarVO();
			}
			dao.insert(aggvos);
			// 业务日志
			if (log)
				TaBusilogUtil.writeEditPsnCalendarBusiLog(psnCalvos, null);
		}
		return vos;
	}

	/**
	 * 保存时的校验 此方法在人员工作日历节点保存时调用，由于界面选择的是HR组织，因此有可能同时修改多个业务单元的班次
	 * 此方法取出所有的业务单元，按业务单元进行处理
	 * 
	 * @param pk_hrorg
	 * @param vos
	 * @throws BusinessException
	 */
	private void checkCalendarWhenSave(String pk_hrorg, PsnJobCalendarVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;

		// 封存期间的日历不允许修改
		List<UFLiteralDate> modefiedDate = new ArrayList<UFLiteralDate>();
		// 取出vos中所有的业务单元
		Set<String> orgSet = new HashSet<String>();
		for (PsnJobCalendarVO vo : vos) {
			if (vo.getOrgMap().size() > 0)
				orgSet.addAll(vo.getOrgMap().values());
			Map<String, String> modifiedMap = vo.getModifiedCalendarMap();
			if (MapUtils.isNotEmpty(modifiedMap)) {
				for (String date : modifiedMap.keySet()) {
					modefiedDate.add(UFLiteralDate.getDate(date));
				}
			}
		}
		if (!CollectionUtils.isEmpty(modefiedDate)) {
			UFLiteralDate[] dates = modefiedDate.toArray(new UFLiteralDate[0]);
			NCLocator.getInstance().lookup(IPeriodQueryService.class).checkDateB4Modify(pk_hrorg, dates);
		}

		// //取出vos中所有的业务单元
		// Set<String> orgSet = new HashSet<String>();
		// for(PsnJobCalendarVO vo:vos){
		// if(vo.getOrgMap().size()>0)
		// orgSet.addAll(vo.getOrgMap().values());
		// }
		// 按业务单元依次进行校验。这样做的潜台词是，跨组织的班次冲突是不管的，即，今天在A组织，明天在B组织，如果今天和明天的班次冲突了，是不会抛异常的
		CalendarShiftMutexChecker checker = new CalendarShiftMutexChecker();
		for (String pk_org : orgSet) {
			Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String, String>>();
			for (PsnJobCalendarVO vo : vos) {
				if (vo.getModifiedCalendarMap() == null || vo.getModifiedCalendarMap().size() == 0)
					continue;
				// 此人在界面上被修改的工作日历
				Map<String, String> modifiedMap = vo.getModifiedCalendarMap();
				// 此人在pk_org业务单元被修改的工作日历
				Map<String, String> modifiedMapInOrg = new HashMap<String, String>();
				for (String date : modifiedMap.keySet()) {
					if (pk_org.equals(vo.getOrgMap().get(date)))
						modifiedMapInOrg.put(date, modifiedMap.get(date));
				}
				if (modifiedMapInOrg.size() > 0)
					modifiedCalendarMap.put(vo.getPk_psndoc(), modifiedMapInOrg);
			}
			checker.checkCalendar(pk_org, modifiedCalendarMap, true, true, false);// 进行校验
		}
	}

	@Override
	public PsnJobCalendarVO save(String pk_hrorg, PsnJobCalendarVO vo) throws BusinessException {
		return save(pk_hrorg, new PsnJobCalendarVO[] { vo })[0];
	}

	/*
	 * 根据条件、日期范围查询人员列表。isOverrideExistCalendar表示用户是否希望覆盖已有班次。如果是Y,
	 * 则返回日期范围内考勤档案有效的人员。如果是N,则除了考勤档案有效之外，还要求工作日历不完整 (non-Javadoc)
	 * 
	 * @see
	 * nc.itf.ta.IPsnCalendarQueryMaintain#queryPsnJobVOsByConditionAndOverride(
	 * java.lang.String, nc.ui.querytemplate.querytree.FromWhereSQL,
	 * nc.vo.pub.lang.UFLiteralDate, nc.vo.pub.lang.UFLiteralDate, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PsnJobVO[] queryPsnJobVOsByConditionAndOverrideOrg(String pk_org, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate, boolean isOverrideExistCalendar, boolean isHROrg)
			throws BusinessException {
		return queryPsnJobVOsByConditionAndOverrideOrgWithUnit(pk_org, fromWhereSQL, beginDate, endDate,
				isOverrideExistCalendar, isHROrg, false);// 默认不限定业务单元
	}

	@Override
	public PsnJobCalendarVO[] useDefault(String pkOrg, String[] pkPsndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean overrideExistCalendar) throws BusinessException {
		return useDefault(pkOrg, pkPsndocs, beginDate, endDate, overrideExistCalendar, true);
	}

	@Override
	public PsnJobCalendarVO[] useDefault(String pk_hrorg, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean overrideExistCalendar, boolean throwExcpWhenNoDefaultShift)
			throws BusinessException {
		NCLocator.getInstance().lookup(IPeriodQueryService.class).checkDateScope(pk_hrorg, beginDate, endDate);
		// 使用默认班次，可以看作是一种遇假日取消的循环排班，其班次设置周期为七天，且周一到周五是默认班，周六日是公休
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		OrgVO[] orgVOs = psndocService.queryAllBUsByPsndocs(pk_psndocs, beginDate, endDate);
		// IAOSQueryService aosService =
		// NCLocator.getInstance().lookup(IAOSQueryService.class);
		// 此hr组织管理的所有业务单元
		// OrgVO[] orgVOsUnderHrOrg =
		// aosService.queryAOSMembersByHROrgPK(pk_hrorg, false, true);
		OrgVO[] orgVOsUnderHrOrg = ShiftServiceFacade.queryOrgsByHROrg(pk_hrorg);

		Set<String> pkOrgsSetUnderHrOrg = new HashSet<String>(Arrays.asList(StringPiecer.getStrArray(orgVOsUnderHrOrg,
				OrgVO.PK_ORG)));
		// 查询此次操作涉及的人员+日期范围，牵扯到的所有业务单元，然后对这些业务单元循环处理
		for (OrgVO orgVO : orgVOs) {
			String pk_org = orgVO.getPk_org();
			// 如果当前业务单元不在此HR组织的管辖范围内，则不处理
			if (!pkOrgsSetUnderHrOrg.contains(pk_org))
				continue;
			AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
			if (defaultShift == null) {
				if (throwExcpWhenNoDefaultShift)
					throw new BusinessException(ResHelper.getString("6017psncalendar", "06017psncalendar0091"
					/* @res "业务单元{0}未定义默认班次!" */, MultiLangHelper.getName(orgVO)));
				continue;
			}
			// useDefault(pk_org, pk_psndocs, beginDate, endDate,
			// overrideExistCalendar, defaultShift);
			// useDefaultBatch(orgVO.getPk_group(),pk_hrorg,pk_org, pk_psndocs,
			// beginDate, endDate, overrideExistCalendar,
			// defaultShift);//效率优化分批执行下移
			useDefault(orgVO.getPk_group(), pk_hrorg, pk_org, pk_psndocs, beginDate, endDate, overrideExistCalendar,
					defaultShift, false);
		}
		return queryCalendarVOByPsndocs(pk_hrorg, pk_psndocs, beginDate, endDate);
	}

	// /**//效率优化分批执行下移
	// * 有的客户的人员非常多，数据生成时可能数量很大，导致内存溢出或者达到了数据库查询的限制，导致数据生成失败
	// * 在此进行分批处理，每1000人执行一次
	// * @param pk_hrorg
	// * @param pk_psndocs
	// * @param beginDate
	// * @param endDate
	// * @param overrideExistCalendar
	// * @param throwExcpWhenNoDefaultShift
	// */
	// private void useDefaultBatch(String pk_group,String pk_hrorg,String
	// pk_org, String[] pk_psndocs,
	// UFLiteralDate beginDate, UFLiteralDate endDate,boolean
	// overrideExistCalendar,
	// AggShiftVO defShiftAggVO)throws BusinessException {
	// if(ArrayUtils.isEmpty(pk_psndocs)||StringUtils.isEmpty(pk_org)||beginDate.afterDate(endDate))
	// return;
	// int length = pk_psndocs.length;
	// if(length<500){
	// useDefault(pk_group,pk_hrorg,pk_org, pk_psndocs, beginDate, endDate,
	// overrideExistCalendar, defShiftAggVO);
	// return;
	// }
	// List<String> psnList = new ArrayList<String>();
	// int count = 0;
	// for(int i=0;i<length;i++){
	// count++;
	// psnList.add(pk_psndocs[i]);
	// if(count>=499){
	// useDefault(pk_group,pk_hrorg,pk_org, psnList.toArray(new String[0]),
	// beginDate, endDate, overrideExistCalendar, defShiftAggVO);
	// count = 0;
	// psnList.clear();
	// }
	// }
	// useDefault(pk_group,pk_hrorg,pk_org, psnList.toArray(new String[0]),
	// beginDate, endDate, overrideExistCalendar, defShiftAggVO);
	// }

	public PsnJobCalendarVO[] useDefault(String pk_group, String pk_hrorg, String pk_org, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate, boolean overrideExistCalendar, AggShiftVO defShiftAggVO,
			boolean withRturn) throws BusinessException {
		String pk_default = defShiftAggVO.getParentVO().getPrimaryKey();
		// 七天循环
		String[] pks = new String[7];
		for (int i = 0; i < pks.length; i++) {
			int week = beginDate.getDateAfter(i).getWeek();
			pks[i] = (week == 0 || week == 6) ? ShiftVO.PK_GX : pk_default;
		}
		return circularArrange(pk_group, pk_hrorg, pk_org, pk_psndocs, beginDate, endDate, pks, true,
				overrideExistCalendar, withRturn);
	}

	/**
	 * 在同一业务单元内排班时，校验排班的合法性： 1.校验紧挨着的两天的班次的考勤时间段（即班次的刷卡开始时间到刷卡结束时间之间的时间段）是否有交集
	 * 2.校验紧挨着的两天的班次的考勤时间段是否颠倒(即T+1日的考勤时间段排到了T日的考勤时间段之前)
	 * 3.校验中间隔一天的两天的班次的考勤时间段是否有交集 4.校验中间隔一天的两天的班次的考勤时间段是否颠倒
	 * 按顺序校验，如果满足其中任何一条，则校验不通过，抛出异常提示用户 此方法要充分考虑效率，因为校验的数据量很大，处理不好会造成严重的效率问题
	 * 
	 * @param pk_org
	 * @param modifiedCalendarMap
	 *            ，需要设置的班次的信息，key是pk_psndoc，value的key是日期， value是班次主键。
	 *            如果某天的班次被设置为null，则value为null,map中不能没有这一天
	 * @param throwsException
	 *            ,是否要抛出异常。如果为true，则发生冲突时，抛出异常；如果为false，则发生冲突时，不抛异常，
	 *            将冲突的日期记入map，并最终返回
	 *            抛异常的方式用于循环排班、默认排班等操作，返回map的方式用于导入等操作（导入时如果有班次冲突，
	 *            需要把冲突信息反映在excel文件中）
	 *            如果采用返回map的方式来包装冲突信息，那么map的结构为：key是人员主键，value是Set
	 *            <String>数组，两个set里面都包含了班次冲突的日期，区别是
	 *            第一个set包含的是用户本身设置就冲突的日期，第二个set包含的是用户设置与已有工作日历冲突的信息
	 *            例如，用户排了1.1，1.2两天的班，并且1.3的班本来也存在，现在1.1和1.2冲突，1.2和1.3冲突，
	 *            那么最终返回的set数组是： set1：1.1，1.2 set2：1.2
	 *            这样包装set的作用是方便导入时，excel的冲突提示颜色的显示
	 * @param pk_org
	 *            业务单元主键
	 */
	// protected Map<String, Set<String>[]> checkCalendar(String pk_org,
	// Map<String, Map<String, String>>
	// modifiedCalendarMap,//key是人员主键，value的key是日期，value是班次主键。存储了需要设置的班次的信息
	// boolean overrideExists,boolean throwsException)
	// throws BusinessException{
	// if(modifiedCalendarMap==null||modifiedCalendarMap.size()==0)
	// return null;
	// Set<ShiftMutexBUVO>[] allMutextSet =
	// PsnCalendarUtils.splitMutexVOSet(NCLocator.getInstance().lookup(IShiftQueryService.class).queryShiftMutexByOrg(pk_org));
	// //紧挨着冲突的
	// Set<ShiftMutexBUVO> mutexSet0 = allMutextSet[0];
	// //紧挨着颠倒的
	// Set<ShiftMutexBUVO> reverseSet0 = allMutextSet[1];
	// //隔一天冲突的
	// Set<ShiftMutexBUVO> mutexSet1 = allMutextSet[2];
	// //隔一天颠倒的
	// Set<ShiftMutexBUVO> reverseSet1 = allMutextSet[3];
	// //如果四种情况都没有数据，则表示此组织内无论怎么排班都不会冲突，校验不必进行
	// if(
	// CollectionUtils.isEmpty(mutexSet0)&&
	// CollectionUtils.isEmpty(reverseSet0)&&
	// CollectionUtils.isEmpty(mutexSet1)&&
	// CollectionUtils.isEmpty(reverseSet1))
	// return null;
	// String[] pk_psndocs = modifiedCalendarMap.keySet().toArray(new
	// String[0]);
	// //为了防止内存溢出，一次处理不超过500人
	// int maxProcessCount = 500;
	// //循环处理的话，需要多少次循环
	// int forCount =
	// (pk_psndocs.length%maxProcessCount)==0?(pk_psndocs.length/maxProcessCount):((pk_psndocs.length/maxProcessCount)+1);
	// Map<String, Set<String>[]> retMap = new HashMap<String, Set<String>[]>();
	// for(int i=0;i<forCount;i++){
	// int arrLenth = Math.min(pk_psndocs.length,
	// (i+1)*maxProcessCount)-i*maxProcessCount;
	// String[] pks = new String[arrLenth];
	// System.arraycopy(pk_psndocs, i*maxProcessCount, pks, 0, arrLenth);
	// Map<String, Set<String>[]> map = checkCalendar(pk_org, pks,
	// modifiedCalendarMap, mutexSet0, reverseSet0, mutexSet1, reverseSet1,
	// overrideExists,throwsException);
	// if(!MapUtils.isEmpty(map))
	// retMap.putAll(map);
	// }
	// return retMap;
	// }
	//
	// private Map<String, Set<String>[]> checkCalendar(String pk_org,String[]
	// pk_psndocs,
	// Map<String, Map<String, String>> modifiedCalendarMap,//
	// Set<ShiftMutexBUVO> mutexSet0,
	// Set<ShiftMutexBUVO> reverseSet0,
	// Set<ShiftMutexBUVO> mutexSet1,
	// Set<ShiftMutexBUVO> reverseSet1,
	// boolean overrideExists,
	// boolean throwsException)throws BusinessException{
	// //查询这一批人的现有班次主键
	// //找出设置的班次的最早日期和最晚日期（因为查询工作日历必须有一个日期范围）
	// Set<String> allDateSet = new HashSet<String>();//存储所有日期的date，用于后面的排序
	// for(int i=0;i<pk_psndocs.length;i++){
	// Map<String, String> pkMap = modifiedCalendarMap.get(pk_psndocs[i]);
	// if(pkMap==null||pkMap.size()==0)
	// continue;
	// allDateSet.addAll(pkMap.keySet());
	// }
	// String[] allDate = allDateSet.toArray(new String[0]);
	// Arrays.sort(allDate);
	// //最早日期往前推两天，往后推两天，查出这批人的排班情况，key是pk_psndoc,value的key是日期，value是班次主键
	// Map<String, Map<String, String>> existsCalendarMap =
	// new PsnCalendarDAO().queryPkShiftMapByPsndocs(pk_org, pk_psndocs,
	// UFLiteralDate.getDate(allDate[0]).getDateBefore(2),
	// UFLiteralDate.getDate(allDate[allDate.length-1]).getDateAfter(2));
	// if(existsCalendarMap==null)
	// existsCalendarMap = new HashMap<String, Map<String,String>>();
	//
	// //最终要修改的班次（modifiedCalendarMap里面的班次不一定最终会修改到数据库，因为有可能用户选择的是不覆盖）.此map只存储要持久化到数据库的，不持久化的不记
	// Map<String, Map<String, String>> realModifiedCalendarMap = null;
	// //最终的班次（即假设不校验，最后持久化到数据库完成后完整的排班情况，即finalCalendarMap是包含realModifiedCalendarMap的,校验的时候主要靠这个map）
	// Map<String, Map<String, String>> finalCalendarMap = new HashMap<String,
	// Map<String,String>>();
	// if(overrideExists){//如果用户选择覆盖
	// realModifiedCalendarMap = modifiedCalendarMap;
	// for(int i=0;i<pk_psndocs.length;i++){
	// Map<String, String> finalMap = existsCalendarMap.get(pk_psndocs[i]);
	// if(finalMap==null){
	// finalMap = new HashMap<String, String>();
	// }
	// finalCalendarMap.put(pk_psndocs[i], finalMap);
	// if(modifiedCalendarMap.get(pk_psndocs[i])!=null)
	// finalMap.putAll(modifiedCalendarMap.get(pk_psndocs[i]));
	// }
	// }
	// //如果用户选择不覆盖已有排班，则需要跟原有班次进行比对，将modifiedCalendarMap中新排的班(即原来没有排的班)放到realModifiedCalendarMap中
	// else{
	// realModifiedCalendarMap = new HashMap<String, Map<String,String>>();
	// //按人循环
	// for(int i=0;i<pk_psndocs.length;i++){
	// Map<String, String> existMap = existsCalendarMap.get(pk_psndocs[i]);
	// if(existMap==null){
	// existMap = new HashMap<String, String>();
	// existsCalendarMap.put(pk_psndocs[i], existMap);
	// }
	// Map<String, String> finalMap = existsCalendarMap.get(pk_psndocs[i]);
	// finalCalendarMap.put(pk_psndocs[i], finalMap);
	// //不考虑是否覆盖，需要设置的班次的map
	// Map<String, String> modifiedMap = modifiedCalendarMap.get(pk_psndocs[i]);
	// if(modifiedMap==null||modifiedMap.size()==0)
	// continue;
	// Iterator<String> dateIterator = modifiedMap.keySet().iterator();
	// Map<String, String> realCalendarMap = new HashMap<String, String>();
	// realModifiedCalendarMap.put(pk_psndocs[i], realCalendarMap);
	// while(dateIterator.hasNext()){
	// String date = dateIterator.next();
	// //如果这一天的班次不存在，则说明这一天的班次需要设置
	// if(StringHelper.isEmpty(existMap.get(date)))
	// realCalendarMap.put(date, modifiedMap.get(date));
	// }
	// finalMap.putAll(realCalendarMap);
	// }
	// }
	// //走到这里，realModifiedCalendarMap里存储的是要最终要持久化的班次（已经考虑了是否覆盖原班次选项），finalCalendarMap存储的是不考虑冲突，持久化后的所有排班情况
	// //下面的任务是循环遍历realModifiedCalendarMap里面要设置的班次，看这些班次是否违反了冲突规则，校验的方法是，找到
	// //realModifiedCalendarMap里的每个班次的前一天、前两天、后一天、后两天的班次（在finalCalendarMap里找），然后依次判断他们之间是否冲突
	// //因此，假设realModifiedCalendarMap里面有m个人员，每个人员要设置n天的班次，那么最多需要校验m*n*4（T日班与T-1、T-2、T+1、T+2都要校验）次
	// //组织内所有班次
	// // Map<String, AggBclbDefVO> shiftMap =
	// NCLocator.getInstance().lookup(IBclbQueryService.class).queryBclbAggVOMap(pk_org);
	// Map<String, AggShiftVO> shiftMap =
	// NCLocator.getInstance().lookup(IShiftQueryService.class).queryShiftAggVOMapByOrg(pk_org);
	// Map<String, Set<String>[]> retMap = new HashMap<String, Set<String>[]>();
	// for(int i=0;i<pk_psndocs.length;i++){
	// Map<String, String> realMap = realModifiedCalendarMap.get(pk_psndocs[i]);
	// //要调整的班次为空，则此人不用校验
	// if(realMap==null||realMap.size()==0)
	// continue;
	// //此人排班完成后，数据库中应该存放的班次
	// Map<String, String> finalMap = finalCalendarMap.get(pk_psndocs[i]);
	// Iterator<String> dateIterator = realMap.keySet().iterator();
	// String errMsg ="人员{0}在{1}的班次为{2},{3}的班次为{4},发生了班次冲突!";
	// while(dateIterator.hasNext()){
	// String date = dateIterator.next();
	// //date日对应的班次
	// String pk_shift = realMap.get(date);
	// //如果当日班次为空，或者是公休，则不用校验，因为这种情况下不可能产生冲突
	// if(pk_shift==null||ShiftVO.PK_GX.equals(pk_shift))
	// continue;
	// //前一天班次，从finalMap中取出
	// String dateBefore1 =
	// UFLiteralDate.getDate(date).getDateBefore(1).toString();
	// String former1Shift = finalMap.get(dateBefore1);
	// //前一天班次为空或者是公休，则不用校验
	// if(former1Shift!=null&&!ShiftVO.PK_GX.equals(former1Shift)&&
	// !isValid(former1Shift, pk_shift, mutexSet0, reverseSet0)){
	// if(throwsException)
	// throw new ValidationException(getErrMsg(errMsg, pk_psndocs[i],
	// UFLiteralDate.getDate(date).getDateBefore(1).toString(), former1Shift,
	// date, pk_shift, shiftMap));
	// processMutexReturnMap(retMap, pk_psndocs[i], date,
	// !realMap.containsKey(dateBefore1));
	// }
	// //前两天班次
	// String dateBefore2 =
	// UFLiteralDate.getDate(date).getDateBefore(2).toString();
	// String former2Shift = finalMap.get(dateBefore2);
	// //前两天班次为空或者是公休，则不用校验
	// if(former2Shift!=null&&!ShiftVO.PK_GX.equals(former2Shift)&&
	// !isValid(former2Shift, pk_shift,mutexSet1, reverseSet1)){
	// if(throwsException)
	// throw new ValidationException(getErrMsg(errMsg, pk_psndocs[i],
	// UFLiteralDate.getDate(date).getDateBefore(2).toString(), former2Shift,
	// date, pk_shift, shiftMap));
	// processMutexReturnMap(retMap, pk_psndocs[i], date,
	// !realMap.containsKey(dateBefore2));
	// }
	// //后一天班次
	// String dateNext1 =
	// UFLiteralDate.getDate(date).getDateAfter(1).toString();
	// String next1Shift = finalMap.get(dateNext1);
	// //后一天班次为空或者是公休，则不用校验
	// if(next1Shift!=null&&!ShiftVO.PK_GX.equals(next1Shift)&&
	// !isValid(pk_shift, next1Shift, mutexSet0, reverseSet0)){
	// if(throwsException)
	// throw new ValidationException(getErrMsg(errMsg, pk_psndocs[i],
	// date, pk_shift, UFLiteralDate.getDate(date).getDateAfter(1).toString(),
	// next1Shift, shiftMap));
	// processMutexReturnMap(retMap, pk_psndocs[i], date,
	// !realMap.containsKey(dateNext1));
	// }
	// //后两天班次
	// String dateNext2 =
	// UFLiteralDate.getDate(date).getDateAfter(2).toString();
	// String next2Shift = finalMap.get(dateNext2);
	// //后两天班次为空或者是公休，则不用校验
	// if(next2Shift!=null&&!ShiftVO.PK_GX.equals(next2Shift)&&!
	// isValid(pk_shift, next2Shift, mutexSet1, reverseSet1)){
	// if(throwsException)
	// throw new ValidationException(getErrMsg(errMsg, pk_psndocs[i],
	// date, pk_shift, UFLiteralDate.getDate(date).getDateAfter(2).toString(),
	// next2Shift, shiftMap));
	// processMutexReturnMap(retMap, pk_psndocs[i], date,
	// !realMap.containsKey(dateNext2));
	// }
	// }
	// }
	// return retMap;
	// }

	// @SuppressWarnings("unchecked")
	// private void processMutexReturnMap(Map<String, Set<String>[]>
	// retMap,String pk_psndoc,String date,boolean isMutexWithExistsCalendar){
	// if(!retMap.containsKey(pk_psndoc)){
	// Set<String>[] setList = new Set[2];
	// retMap.put(pk_psndoc, setList);
	// }
	// Set<String>[] setList = retMap.get(pk_psndoc);
	// int index =
	// isMutexWithExistsCalendar?1:0;//第0个set记录用户自身设置的班次冲突，第1个set记录与已有班次的冲突
	// if(setList[index]==null){
	// setList[index] = new HashSet<String>();
	// }
	// setList[index].add(date);
	// }

	// private String getErrMsg(String oriMsg,String pk_psndoc,String
	// date1,String pk_shift1,String date2,String pk_shift2,Map<String,
	// AggShiftVO> shiftMap) throws DAOException{
	// if(StringHelper.isNotEmpty(pk_psndoc)){
	// PsndocVO psndocVO = (PsndocVO)new BaseDAO().retrieveByPK(PsndocVO.class,
	// pk_psndoc);
	// oriMsg = StringHelper.replace(oriMsg, "{0}",psndocVO.getMultiLangName());
	// }
	// oriMsg = StringHelper.replace(oriMsg, "{1}", date1);
	// oriMsg = StringHelper.replace(oriMsg, "{2}",
	// shiftMap.get(pk_shift1).getShiftVO().getMultiLangName());
	// oriMsg = StringHelper.replace(oriMsg, "{3}", date2);
	// oriMsg = StringHelper.replace(oriMsg, "{4}",
	// shiftMap.get(pk_shift2).getShiftVO().getMultiLangName());
	// return oriMsg;
	// }

	/**
	 * 校验前后两个班次是否冲突，如果校验不冲突，返回true，否则false
	 * 
	 * @return
	 */
	// private boolean isValid(String formerShift,String nextShift,
	// Set<ShiftMutexBUVO> mutexSet,
	// Set<ShiftMutexBUVO> reverseSet){
	// ShiftMutexBUVO mutexVO = new ShiftMutexBUVO();
	// mutexVO.setFirstshiftid(formerShift);
	// mutexVO.setNextshiftid(nextShift);
	// if(mutexSet.contains(mutexVO))
	// return false;
	// if(reverseSet.contains(mutexVO))
	// return false;
	// return true;
	// }

	/*
	 * 根据人员主键数组、日期范围查询排班情况 (non-Javadoc)
	 * 
	 * @see
	 * nc.itf.ta.IPsnCalendarQueryMaintain#queryCalendarVOByPsndocs(java.lang.
	 * String, java.lang.String[], nc.vo.pub.lang.UFLiteralDate,
	 * nc.vo.pub.lang.UFLiteralDate)
	 */
	@Override
	public PsnJobCalendarVO[] queryCalendarVOByPsndocs(String pk_hrorg, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		FromWhereSQL fws = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
		try {
			PsnJobCalendarVO[] vos = queryCalendarVOByCondition(pk_hrorg, fws, beginDate, endDate);
			// 上面的处理会打乱人员的顺序，下面需要按照pkPsndocs重新排序
			if (ArrayUtils.isEmpty(vos))
				return vos;
			Map<String, PsnJobCalendarVO> map = new HashMap<String, PsnJobCalendarVO>();
			for (PsnJobCalendarVO vo : vos) {
				map.put(vo.getPk_psndoc(), vo);
			}
			Set<PsnJobCalendarVO> retList = new HashSet<PsnJobCalendarVO>();
			for (int i = 0; i < pk_psndocs.length; i++) {
				if (map.containsKey(pk_psndocs[i]))
					retList.add(map.get(pk_psndocs[i]));
			}
			return retList.toArray(new PsnJobCalendarVO[0]);
		} finally {
			TBMPsndocSqlPiecer.clearQuerySQL(fws);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String>[] importDatas(String pk_org, GeneralVO[] vos, boolean isClearNull) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		String inSql = null;
		InSQLCreator isc = new InSQLCreator();
		try {
			// int beginRowNum = 4;
			int beginRowNum = 5;// 增加了星期几行，改为5开始
			List<String> psnVec = new ArrayList<String>(); // 记录文件中出现的人员信息
			List<String> psncodeVec = new ArrayList<String>(); // 记录文件中出现的人员编码
			List<String> wrongFormatVec = new ArrayList<String>(); // 数据格式不正确的错误集合
			List<String> samePsnVec = new ArrayList<String>(); // 同一人有多条记录的错误集合
			List<String> wrongShiftVec = new ArrayList<String>(); // 班次名称错误的错误集合
			List<String> notFoundPsnVec = new ArrayList<String>(); // 找不到对应的人员编码的错误集合
			List<String> mutextInFileVec = new ArrayList<String>(); // 文件中排班冲突的错误集合
			List<String> mutextInDBVec = new ArrayList<String>(); // 与已有排班冲突的错误集合

			// 查出涉及人员的考勤档案
			for (int i = 0; i < vos.length; i++) {
				String psncode = (String) vos[i].getAttributeValue(PsnCalendarCommonValue.LISTCODE_PSNCODE);
				if (StringUtils.isEmpty(psncode))
					continue;
				// 将编码存入编码数列
				psncodeVec.add(psncode);
			}
			// 需去掉空格，因为InSQLCreator会自动补位
			inSql = isc.getInSQL(psncodeVec.toArray(new String[0]), true);
			String oriWhere = " bd_psndoc.code in(" + inSql + ")";
			// String sql = " select
			// tbm_psndoc.begindate,tbm_psndoc.pk_psndoc,pk_tbm_psndoc,timecardid,bd_psndoc.code
			// psncode, bd_psndoc.name psnname ,bd_psnjob.pk_org psnjoborg " +
			// " from tbm_psndoc inner join bd_psndoc on tbm_psndoc.pk_psndoc =
			// bd_psndoc.pk_psndoc inner join bd_psnjob on bd_psnjob.pk_psnjob =
			// tbm_psndoc.pk_psnjob " +
			// " where tbm_psndoc.pk_org = ? and "+oriWhere;
			String sql = " select tbm_psndoc.begindate,tbm_psndoc.pk_psndoc,pk_tbm_psndoc,timecardid,bd_psndoc.code psncode, bd_psndoc.name psnname ,hi_psnjob.pk_org psnjoborg "
					+ " from tbm_psndoc inner join bd_psndoc on tbm_psndoc.pk_psndoc = bd_psndoc.pk_psndoc inner join hi_psnjob on hi_psnjob.pk_psnjob = tbm_psndoc.pk_psnjob "
					+ " where tbm_psndoc.pk_org = ? and  " + oriWhere;
			SQLParameter parameter = new SQLParameter();
			parameter.addParam(pk_org);
			List<GeneralVO> tmpPsndocVOs = (ArrayList<GeneralVO>) new BaseDAO().executeQuery(sql, parameter,
					new BeanListProcessor(GeneralVO.class));
			// 将涉及人员的考勤档案用map存储
			Map<String, GeneralVO> psncode_tbmPsndocMap = new HashMap<String, GeneralVO>();
			if (!CollectionUtils.isEmpty(tmpPsndocVOs)) {
				for (GeneralVO psndocVO : tmpPsndocVOs) {
					String psncode = (String) psndocVO.getAttributeValue(TBMPsndocCommonValue.LISTCODE_PSNCODE);
					String psnname = (String) psndocVO.getAttributeValue(TBMPsndocCommonValue.LISTCODE_PSNNAME);
					// 如果有一个人有两条考勤记录，则取最新的
					if (psncode_tbmPsndocMap.get(psncode + "_" + psnname) == null) {
						psncode_tbmPsndocMap.put(psncode + "_" + psnname, psndocVO);
					} else {
						UFLiteralDate begindatetime = UFLiteralDate.getDate((String) psncode_tbmPsndocMap.get(
								psncode + "_" + psnname).getAttributeValue(TBMPsndocVO.BEGINDATE));
						if (begindatetime.compareTo(UFLiteralDate.getDate((String) psndocVO
								.getAttributeValue(TBMPsndocVO.BEGINDATE))) < 0) {
							psncode_tbmPsndocMap.put(psncode + "_" + psnname, psndocVO);
						}
					}
				}
			}
			// 获取当前组织所有班次信息，并用map存储,key是班次名称,value是AggBclbDefVO
			AggShiftVO[] shiftVOs = ShiftServiceFacade.queryAllByHROrg(pk_org);
			Map<String, AggShiftVO> shiftMap = new HashMap<String, AggShiftVO>();
			if (!ArrayUtils.isEmpty(shiftVOs)) {
				for (AggShiftVO shiftVO : shiftVOs) {
					// 此处使用的name应为多语环境下当前语种的name，不能直接使用getname否则不匹配，不同的业务单元下有重复的名称，导致匹配失败
					shiftMap.put(MultiLangHelper.getName(shiftVO.getShiftVO()) + shiftVO.getShiftVO().getPk_org(),
							shiftVO);
				}
			}

			Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String, String>>();// key是人员主键，value的key是日期，value是班次主键。存储了需要设置的班次的信息
			// 校验导入信息
			for (int i = 0; i < vos.length; i++) {
				// 检查是否为空行,为空行则跳过
				if (((UFBoolean) vos[i].getAttributeValue(PsnCalendarCommonValue.LISTCODE_ISNULLROW)).booleanValue())
					continue;
				vos[i].removeAttributeName(PsnCalendarCommonValue.LISTCODE_ISNULLROW);

				String psncode = (String) vos[i].getAttributeValue(PsnCalendarCommonValue.LISTCODE_PSNCODE);
				String psnname = (String) vos[i].getAttributeValue(PsnCalendarCommonValue.LISTCODE_PSNNAME);

				// 校验数据格式
				if (StringUtils.isEmpty(psncode))
					wrongFormatVec.add((i + beginRowNum) + "_1");
				if (StringUtils.isEmpty(psnname))
					wrongFormatVec.add((i + beginRowNum) + "_2");

				if (StringUtils.isNotEmpty(psncode) && StringUtils.isNotEmpty(psnname)) {
					// 校验人员编码
					if (psncode_tbmPsndocMap.get(psncode + "_" + psnname) == null) {
						notFoundPsnVec.add(String.valueOf((i + beginRowNum)));
						continue;
					}
					// 校验重复记录
					if (psnVec.contains(psncode + "_" + psnname)) {
						int index = psnVec.indexOf(psncode + "_" + psnname);
						if (!samePsnVec.contains(Integer.valueOf(index + beginRowNum))) {
							samePsnVec.add(String.valueOf(index + beginRowNum));
						}
						samePsnVec.add(String.valueOf((i + beginRowNum)));
						continue;
					}
					psnVec.add(psncode + "_" + psnname);
				}
				// 所有表头信息
				String[] fields = vos[i].getAttributeNames();
				Map<String, String> modifyMap = new HashMap<String, String>();
				// 校验班次名称
				for (int j = 3; j < fields.length; j++) {
					String shiftName = (String) vos[i].getAttributeValue(fields[j]);
					// 设置的班次为空
					if (StringUtils.isEmpty(shiftName)) {
						if (isClearNull)
							modifyMap.put(fields[j], null);
						continue;
					}
					// 公休直接保存
					if (shiftName.equals(ResHelper.getString("6017psncalendar", "06017psncalendar0092")/*
																										 * @
																										 * res
																										 * "公休"
																										 */)) {
						modifyMap.put(fields[j], ShiftVO.PK_GX);
						continue;
					}
					GeneralVO generalVO = psncode_tbmPsndocMap.get(psncode + "_" + psnname);
					// 不存在此班次名称则加入班次名称错误信息
					String shiftkey = shiftName + generalVO.getAttributeValue("psnjoborg");
					if (shiftMap.get(shiftkey) == null) {
						wrongShiftVec.add(String.valueOf((i + beginRowNum)) + "_" + j);
						continue;
					}
					ShiftVO shiftVO = shiftMap.get(shiftkey).getShiftVO();
					String pk_shiftorg = shiftVO.getPk_org();
					if (!pk_shiftorg.equalsIgnoreCase((String) generalVO.getAttributeValue("psnjoborg"))
							&& !"DEFAULT".equalsIgnoreCase(shiftVO.getCode())) {// 633添加，班次是业务单元的，需判断和人员的业务单元是否匹配
						wrongShiftVec.add(String.valueOf((i + beginRowNum)) + "_" + j);
						continue;
					}
					// V65新增，停用的班次不能导入
					if (null == shiftVO.getEnablestate()
							|| EnableStateEnum.ENABLESTATE_DISABLE.toIntValue() == shiftVO.getEnablestate()) {
						wrongShiftVec.add(String.valueOf((i + beginRowNum)) + "_" + j);
						continue;
					}
					modifyMap.put(fields[j], shiftVO.getPk_shift());
				}
				// 如果编码或名称为空不需要存入班次修改Map
				if (StringUtils.isEmpty(psncode) || StringUtils.isEmpty(psnname))
					continue;
				String pk_psndoc = (String) psncode_tbmPsndocMap.get(psncode + "_" + psnname).getAttributeValue(
						PsnCalendarCommonValue.LISTCODE_PKPSNDOC);
				if (!modifyMap.isEmpty())
					modifiedCalendarMap.put(pk_psndoc, modifyMap);
				// 校验后设置pk_psndoc属性，为后续校验班次冲突做准备
				vos[i].setAttributeValue(PsnCalendarCommonValue.LISTCODE_PKPSNDOC, pk_psndoc);
			}

			// 校验排班合法性
			Map<String, Set<String>[]> mutextResultMap = new CalendarShiftMutexChecker().checkCalendar(pk_org,
					modifiedCalendarMap, true, false, false);
			for (int i = 0; i < vos.length; i++) {
				String pk_psndoc = (String) vos[i].getAttributeValue(PsnCalendarCommonValue.LISTCODE_PKPSNDOC);
				if (StringUtils.isEmpty(pk_psndoc))
					continue;
				// 如果此行存在冲突
				Set<String>[] mutextSet = mutextResultMap == null ? null : mutextResultMap.get(pk_psndoc);
				if (ArrayUtils.isEmpty(mutextSet))
					continue;
				// 文件中设置的班次存在冲突
				if (!CollectionUtils.isEmpty(mutextSet[0])) {
					for (String mutextShift : mutextSet[0]) {
						int index = ArrayUtils.indexOf(vos[i].getAttributeNames(), mutextShift);
						if (index < 0)
							continue;
						mutextInFileVec.add(String.valueOf((i + beginRowNum)) + "_" + index);
					}
				}
				// 与已有班次存在冲突
				if (!CollectionUtils.isEmpty(mutextSet[1])) {
					for (String mutextShift : mutextSet[1]) {
						int index = ArrayUtils.indexOf(vos[i].getAttributeNames(), mutextShift);
						if (index < 0)
							continue;
						mutextInDBVec.add(String.valueOf((i + beginRowNum)) + "_" + index);
					}
				}
			}

			// 如果有异常，则返回异常信息
			if (samePsnVec.size() > 0 || wrongShiftVec.size() > 0 || wrongFormatVec.size() > 0
					|| notFoundPsnVec.size() > 0 || mutextInFileVec.size() > 0 || mutextInDBVec.size() > 0) {
				List<List<String>> lists = new ArrayList<List<String>>();
				lists.add(wrongFormatVec);
				lists.add(samePsnVec);
				lists.add(wrongShiftVec);
				lists.add(notFoundPsnVec);
				lists.add(mutextInFileVec);
				lists.add(mutextInDBVec);
				return ArrayClassConvertUtil.convert(lists.toArray(), ArrayList.class);
			}

			// 无异常则处理后保存
			// 所有表头信息
			String[] fields = vos[0].getAttributeNames();
			// 取日期数组
			String[] dateFields = (String[]) ArrayUtils.subarray(fields, 3, fields.length - 1);
			Arrays.sort(dateFields);
			// 取所有修改的工作日历在数据库中的信息
			PsnJobCalendarVO[] saveVOs = queryCalendarVOByPsndocs(pk_org,
					modifiedCalendarMap.keySet().toArray(new String[0]), UFLiteralDate.getDate(dateFields[0]),
					UFLiteralDate.getDate(dateFields[dateFields.length - 1]));
			if (ArrayUtils.isEmpty(saveVOs))
				return null;
			List<String> pk_psndocs = new ArrayList<String>();
			for (PsnJobCalendarVO saveVO : saveVOs) {
				saveVO.getModifiedCalendarMap().putAll(modifiedCalendarMap.get(saveVO.getPk_psndoc()));
				pk_psndocs.add(saveVO.getPk_psndoc());
			}

			// 无异常直接保存
			save(pk_org, saveVOs, true, false);
			// 业务日志
			TaBusilogUtil.writeImportPsnCalendarBusiLog(pk_org, pk_psndocs.toArray(new String[0]));
			return null;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			isc.clear();
		}
	}

	public PsnJobCalendarVO[] getcalendarVOs(PsnJobCalendarVO[] calendarVOs, QueryScopeEnum queryScope) {

		List<PsnJobCalendarVO> returnList = new ArrayList<PsnJobCalendarVO>();
		// 为了效率，分成三个for循环；如果写成一个for循环，则判断次数大增
		// 如果是查询部分排班人员，则要判断排班天数是否小于考勤档案有效天数且排班天数大于0
		if (queryScope == QueryScopeEnum.part) {
			for (PsnJobCalendarVO vo : calendarVOs) {
				if (vo.getCalendarMap().size() < vo.getPsndocEffectiveDateSet().size()
						&& vo.getCalendarMap().size() > 0)
					returnList.add(vo);
			}
			return returnList.toArray(new PsnJobCalendarVO[0]);
		}
		// 如果是查询尚未排班人员，则要判断排班天数是否等于0
		if (queryScope == QueryScopeEnum.not) {
			for (PsnJobCalendarVO vo : calendarVOs) {
				if (vo.getCalendarMap().size() == 0)
					returnList.add(vo);
			}
			return returnList.toArray(new PsnJobCalendarVO[0]);
		}
		// 走到这里肯定是查询完全排班人员，只需判断排班天数是否与考勤档案有效天数相等即可
		if (queryScope == QueryScopeEnum.complete) {
			for (PsnJobCalendarVO vo : calendarVOs) {
				if (vo.getCalendarMap().size() == vo.getPsndocEffectiveDateSet().size())
					returnList.add(vo);
			}
			return returnList.toArray(new PsnJobCalendarVO[0]);
		}

		return calendarVOs;

	}

	@Override
	public GeneralVO[] getExportDatas(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, QueryScopeEnum queryScope) throws BusinessException {
		PsnJobCalendarVO[] calendarVOs = queryCalendarVOByCondition(pk_org, fromWhereSQL, beginDate, endDate);
		if (calendarVOs == null || ArrayUtils.isEmpty(calendarVOs))
			return null;

		calendarVOs = getcalendarVOs(calendarVOs, queryScope);

		String[] fields = PsnCalendarCommonValue.createExportFields(beginDate, endDate);
		// 获取人员信息
		TBMPsndocVO[] tbmPsnVOs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryLatestByCondition(pk_org, fromWhereSQL, beginDate, endDate);
		String[] pk_psnjobs = new String[tbmPsnVOs.length];
		for (int i = 0; i < tbmPsnVOs.length; i++)
			pk_psnjobs[i] = tbmPsnVOs[i].getPk_psnjob();
		PsndocAggVO[] psnDocVOs = NCLocator.getInstance().lookup(IPsndocQryService.class)
				.queryPsndocVOByPks(pk_psnjobs);
		Map<String, PsndocVO> psnDocMap = new HashMap<String, PsndocVO>();
		if (ArrayUtils.isEmpty(psnDocVOs))
			return null;
		for (PsndocAggVO psnDocVO : psnDocVOs) {
			psnDocMap.put(psnDocVO.getParentVO().getPk_psndoc(), psnDocVO.getParentVO());
		}
		// 获取当前组织所有班次信息，并用map存储,key是班次主键,value是班次名称
		// AggShiftVO[] shiftVOs =
		// NCLocator.getInstance().lookup(IBclbQueryService.class).queryAllBclbAggVO(pk_org);
		AggShiftVO[] shiftVOs = ShiftServiceFacade.queryAllByHROrg(pk_org);
		Map<String, String> shiftMap = new HashMap<String, String>();
		shiftMap.put(ShiftVO.PK_GX, ResHelper.getString("6017psncalendar", "06017psncalendar0092")
		/* @res "公休" */);
		if (!ArrayUtils.isEmpty(shiftVOs)) {
			for (AggShiftVO shiftVO : shiftVOs) {
				shiftMap.put(shiftVO.getShiftVO().getPk_shift(), shiftVO.getShiftVO().getMultiLangName().toString());
			}
		}

		List<GeneralVO> returnVOs = new ArrayList<GeneralVO>();
		for (int i = 0; i < calendarVOs.length; i++) {
			GeneralVO exportVO = new GeneralVO();
			// 设置人员信息
			PsndocVO psnDocVO = psnDocMap.get(calendarVOs[i].getPk_psndoc());
			if (psnDocVO == null)
				continue;
			exportVO.setAttributeValue(PsnCalendarCommonValue.LISTCODE_CLERKCODE, psnDocVO.getPsnJobVO().getClerkcode());
			exportVO.setAttributeValue(PsnCalendarCommonValue.LISTCODE_PSNCODE, psnDocVO.getCode());
			exportVO.setAttributeValue(PsnCalendarCommonValue.LISTCODE_PSNNAME, psnDocVO.getName());
			for (int j = 3; j < fields.length; j++) {
				String pk_shift = calendarVOs[i].getCalendarMap().get(fields[j]);
				if (StringUtils.isEmpty(pk_shift))
					continue;
				exportVO.setAttributeValue(fields[j], shiftMap.get(pk_shift));
			}
			returnVOs.add(exportVO);
		}

		return returnVOs.toArray(new GeneralVO[0]);
	}

	/*
	 * 2011-07-28与需求讨论后确认：对于每一天的不同情况，处理逻辑如下：
	 * 1.有考勤档案，属于部门所属HR组织，且考勤档案记录的任职属于此部门（或下级部门）
	 * 2.有考勤档案，属于部门所属HR组织，但考勤档案记录的任职不属于此部门（或下级部门）
	 * 3.有考勤档案，不属于部门所属HR组织，且考勤档案记录的任职属于此部门（或下级部门）
	 * 4.有考勤档案，不属于部门所属HR组织，但考勤档案记录的任职不属于此部门（或下级部门） 5.无任何组织的考勤档案
	 * 对于1，2，3，4，都显示其排的班次，但只能修改1,也就是说，只要此人此天的考勤档案有效，不管是哪个HR组织的，不管在不在此部门，都显示其班次
	 * 对于5，用灰色显示即可 (non-Javadoc)
	 * 
	 * @see
	 * nc.itf.ta.IPsnCalendarQueryMaintain#queryCalendarVOByConditionAndDept(
	 * java.lang.String, boolean, nc.ui.querytemplate.querytree.FromWhereSQL,
	 * nc.vo.pub.lang.UFLiteralDate, nc.vo.pub.lang.UFLiteralDate,
	 * nc.vo.ta.psncalendar.QueryScopeEnum)
	 */
	@Override
	public PsnJobCalendarVO[] queryCalendarVOByConditionAndDept(String pk_dept, boolean containsSubDepts,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, QueryScopeEnum queryScope)
			throws BusinessException {
		IAOSQueryService aosService = NCLocator.getInstance().lookup(IAOSQueryService.class);
		OrgVO orgVO = aosService.queryHROrgByDeptPK(pk_dept);
		if (orgVO == null)
			throw new BusinessException("the dept has no hr org!");
		// 2013-04-27修改，查询部门范围内所有考勤人员
		PsnJobCalendarVO[] vos = queryCalendarVOByConditionForTeam(orgVO.getPk_org(), fromWhereSQL, beginDate, endDate,
				true, pk_dept, containsSubDepts);
		if (ArrayUtils.isEmpty(vos) || queryScope == QueryScopeEnum.all)
			return vos;

		List<PsnJobCalendarVO> returnList = new ArrayList<PsnJobCalendarVO>();
		// 为了效率，分成三个for循环；如果写成一个for循环，则判断次数大增
		// 如果是查询部分排班人员，则要判断排班天数是否小于考勤档案有效天数且排班天数大于0
		if (queryScope == QueryScopeEnum.part) {
			for (PsnJobCalendarVO vo : vos) {
				Set<String> calendarDateInHrOrgAndDept = CommonUtils.getIntersectionSet(vo.getCalendarMap().keySet(),
						vo.getPsndocEffectiveDateSetInHROrgAndDept());
				int calendarDateInHrOrgAndDeptSize = org.apache.commons.collections.CollectionUtils
						.isEmpty(calendarDateInHrOrgAndDept) ? 0 : calendarDateInHrOrgAndDept.size();
				if (calendarDateInHrOrgAndDeptSize < vo.getPsndocEffectiveDateSetInHROrgAndDept().size()
						&& calendarDateInHrOrgAndDeptSize > 0)
					returnList.add(vo);
			}
			return returnList.toArray(new PsnJobCalendarVO[0]);
		}
		// 如果是查询尚未排班人员，则要判断排班天数是否等于0
		if (queryScope == QueryScopeEnum.not) {
			for (PsnJobCalendarVO vo : vos) {
				Set<String> calendarDateInHrOrgAndDept = CommonUtils.getIntersectionSet(vo.getCalendarMap().keySet(),
						vo.getPsndocEffectiveDateSetInHROrgAndDept());
				int calendarDateInHrOrgAndDeptSize = org.apache.commons.collections.CollectionUtils
						.isEmpty(calendarDateInHrOrgAndDept) ? 0 : calendarDateInHrOrgAndDept.size();
				if (calendarDateInHrOrgAndDeptSize == 0)
					returnList.add(vo);
			}
			return returnList.toArray(new PsnJobCalendarVO[0]);
		}
		// 走到这里肯定是查询完全排班人员，只需判断排班天数是否与考勤档案有效天数相等即可
		for (PsnJobCalendarVO vo : vos) {
			Set<String> calendarDateInHrOrgAndDept = CommonUtils.getIntersectionSet(vo.getCalendarMap().keySet(),
					vo.getPsndocEffectiveDateSetInHROrgAndDept());
			int calendarDateInHrOrgAndDeptSize = org.apache.commons.collections.CollectionUtils
					.isEmpty(calendarDateInHrOrgAndDept) ? 0 : calendarDateInHrOrgAndDept.size();
			if (calendarDateInHrOrgAndDeptSize == vo.getPsndocEffectiveDateSetInHROrgAndDept().size())
				returnList.add(vo);
		}
		return returnList.toArray(new PsnJobCalendarVO[0]);
	}

	@Override
	public PsnJobCalendarVO[] save4Mgr(String pk_dept, PsnJobCalendarVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		IAOSQueryService aosService = NCLocator.getInstance().lookup(IAOSQueryService.class);
		OrgVO orgVO = aosService.queryHROrgByDeptPK(pk_dept);
		if (orgVO == null)
			throw new BusinessException("the dept has no hr org!");
		String pk_hrorg = orgVO.getPk_org();
		// 一个人员某一天的班次可以修改的前提是：
		// 1.此人此天有考勤档案，且考勤档案的pk_org是pk_dept所属HR组织的pk_org
		// 2.此人此天的考勤档案上的人员任职的部门是pk_dept或者其子孙部门
		ITBMPsndocQueryService tbmpsndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		FromWhereSQL fromWhereSQL = null;
		try {
			fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(SQLHelper.getStrArray(vos, PsnJobVO.PK_PSNDOC));
			fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, true, fromWhereSQL);
			TBMPsndocVO[] psndocVOs = tbmpsndocService.queryByCondition(pk_hrorg, fromWhereSQL);
			if (ArrayUtils.isEmpty(psndocVOs)) {
				for (PsnJobCalendarVO vo : vos) {
					if (vo.getModifiedCalendarMap() != null)
						vo.getModifiedCalendarMap().clear();
				}
				return vos;
			}
			Map<String, TBMPsndocVO[]> groupedVOs = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_PSNDOC, psndocVOs);
			for (PsnJobCalendarVO vo : vos) {
				if (MapUtils.isEmpty(vo.getModifiedCalendarMap()))
					continue;
				TBMPsndocVO[] psndocVOs2 = groupedVOs.get(vo.getPk_psndoc());
				if (ArrayUtils.isEmpty(psndocVOs2)) {
					vo.getModifiedCalendarMap().clear();
					continue;
				}
				for (String date : vo.getModifiedCalendarMap().keySet()) {
					if (!TBMPsndocVO.isIntersect(psndocVOs2, date))
						vo.getModifiedCalendarMap().remove(date);
				}
			}
		} finally {
			TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
		}
		return save(orgVO.getPk_org(), vos, false, true);
	}

	@Override
	public PsnJobCalendarVO queryCalendarVOByConditionAndDept(String pk_dept, String pk_psndoc,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocQuerySQL(pk_psndoc);
		PsnJobCalendarVO[] vos = queryCalendarVOByConditionAndDept(pk_dept, true, fromWhereSQL, beginDate, endDate,
				QueryScopeEnum.all);
		if (ArrayUtils.isEmpty(vos))
			return null;
		return vos[0];
	}

	@Override
	public void batchChangeShift4Mgr(String pk_dept, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean withOldShift, String oldShift, String newShift) throws BusinessException {
		oldShift = StringUtils.isEmpty(oldShift) ? "" : oldShift;
		newShift = StringUtils.isEmpty(newShift) ? "" : newShift;
		if (withOldShift && oldShift.equals(newShift))
			return;
		FromWhereSQL fromWhereSQL = null;
		PsnJobCalendarVO[] calendarVOs = null;
		try {
			fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
			calendarVOs = queryCalendarVOByConditionAndDept(pk_dept, true, fromWhereSQL, beginDate, endDate,
					QueryScopeEnum.all);
		} finally {
			TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
		}
		if (ArrayUtils.isEmpty(calendarVOs))
			return;
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		for (PsnJobCalendarVO calendarVO : calendarVOs) {
			if (calendarVO.getPsndocEffectiveDateSetInHROrgAndDept().isEmpty())
				continue;
			for (UFLiteralDate date : allDates) {
				if (!calendarVO.getPsndocEffectiveDateSetInHROrgAndDept().contains(date.toString()))
					continue;
				if (withOldShift) {
					String psnOldShift = calendarVO.getCalendarMap().get(date.toString());
					if (psnOldShift == null)
						psnOldShift = "";
					if (psnOldShift.equals(oldShift))
						calendarVO.getModifiedCalendarMap().put(date.toString(), newShift);
				} else
					calendarVO.getModifiedCalendarMap().put(date.toString(), newShift);
			}
		}
		save4Mgr(pk_dept, calendarVOs);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PsndocVO[] queryPsndocVOsByConditionAndDept(String pk_dept, boolean containsSubDepts,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, QueryScopeEnum queryScope)
			throws BusinessException {
		IAOSQueryService aosService = NCLocator.getInstance().lookup(IAOSQueryService.class);
		OrgVO orgVO = aosService.queryHROrgByDeptPK(pk_dept);
		if (orgVO == null)
			throw new BusinessException("the dept has no hr org!");
		fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL);
		int queryScopeInt = getQueryScope(queryScope);
		SQLParamWrapper wrapper = TBMPsndocSqlPiecer.selectDailyDataByPsndocFieldAndDateFieldAndDateArea(
				orgVO.getPk_org(), new String[] { TBMPsndocVO.PK_PSNDOC }, null, "tbm_psncalendar psncalendar",
				"psncalendar.pk_org", "psncalendar.pk_psndoc", "psncalendar.calendar", beginDate.toString(),
				endDate.toString(), null, null, fromWhereSQL, false, queryScopeInt);
		String sql = wrapper.getSql();
		SQLParameter para = wrapper.getParam();
		return (PsndocVO[]) CommonUtils.toArray(PsndocVO.class,
				new BaseDAO().retrieveByClause(PsndocVO.class, PsndocVO.PK_PSNDOC + " in(" + sql + ")", para));
	}

	private int getQueryScope(QueryScopeEnum queryScope) {
		if (queryScope == QueryScopeEnum.all)
			return TBMPsndocSqlPiecer.DAILYDATAQUERYMODE_ALL;
		if (queryScope == QueryScopeEnum.not)
			return TBMPsndocSqlPiecer.DAILYDATAQUERYMODE_EMPTY;
		if (queryScope == QueryScopeEnum.part)
			return TBMPsndocSqlPiecer.DAILYDATAQUERYMODE_PART;
		return TBMPsndocSqlPiecer.DAILYDATAQUERYMODE_COMPLETE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PsnJobCalendarVO queryByPsnDates(String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		if (StringUtils.isEmpty(pk_psndoc))
			return null;
		PsnJobCalendarVO retVO = new PsnJobCalendarVO();
		retVO.setPk_psndoc(pk_psndoc);
		// 最新的一条考勤档案
		TBMPsndocVO[] psndocvos = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocVOsByPsndocDate(pk_psndoc, beginDate, endDate);
		if (ArrayUtils.isEmpty(psndocvos))
			return null;
		retVO.setPk_psnjob(psndocvos[psndocvos.length - 1].getPk_psnjob());
		// 设置此人有效的考勤档案范围
		IDateScope dateScope = new DefaultDateScope(beginDate, endDate);
		for (TBMPsndocVO psndocvo : psndocvos) {
			IDateScope interScope = DateScopeUtils.intersectionDateScope(dateScope, psndocvo);
			if (interScope == null)
				continue;
			UFLiteralDate[] allDates = CommonUtils.createDateArray(interScope.getBegindate(), interScope.getEnddate());
			for (UFLiteralDate date : allDates) {
				retVO.getPsndocEffectiveDateSet().add(date.toString());
			}
		}
		// 查询此人的工作日历
		String cond = PsnCalendarVO.PK_PSNDOC + "=? and " + PsnCalendarVO.CALENDAR + " between ? and ?";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_psndoc);
		para.addParam(beginDate);
		para.addParam(endDate);
		PsnCalendarVO[] calendarVOs = (PsnCalendarVO[]) CommonUtils.toArray(PsnCalendarVO.class,
				new BaseDAO().retrieveByClause(PsnCalendarVO.class, cond, para));
		if (!ArrayUtils.isEmpty(calendarVOs))
			for (PsnCalendarVO calendarVO : calendarVOs) {
				retVO.getCalendarMap().put(calendarVO.getCalendar().toStdString(), calendarVO.getPk_shift());
			}
		// 查询此人的工作日类型
		Map<String, Integer> workDayTypeMap = HRHolidayServiceFacade.queryPsnWorkDayTypeInfo(pk_psndoc, beginDate,
				endDate);
		if (MapUtils.isNotEmpty(workDayTypeMap))
			retVO.getDayTypeMap().putAll(workDayTypeMap);
		return retVO;
	}

	// 查询用到某个班次的工作日历
	@SuppressWarnings("unchecked")
	public PsnJobCalendarVO[] queryByShiftPK(String shiftpk, boolean allField) throws BusinessException {
		String sql = " select top 1 * from TBM_PSNCALENDAR where pk_shift = ? ";
		if (allField)
			sql += "or original_shift_b4cut = ? or original_shift_b4exg = ? ";
		SQLParameter para = new SQLParameter();
		para.addParam(shiftpk);
		if (allField) {
			para.addParam(shiftpk);
			para.addParam(shiftpk);
		}
		Collection<PsnJobCalendarVO> result = (Collection<PsnJobCalendarVO>) new BaseDAO().executeQuery(sql, para,
				new BeanListProcessor(PsnJobCalendarVO.class));
		return CollectionUtils.isEmpty(result) ? null : result.toArray(new PsnJobCalendarVO[0]);
	}

	@Override
	public GeneralVO[] getExportDatas(String pk_org, PsnJobCalendarVO[] calendarVOs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if (ArrayUtils.isEmpty(calendarVOs))
			return null;
		String[] fields = PsnCalendarCommonValue.createExportFields(beginDate, endDate);
		// 获取人员信息
		TBMPsndocVO[] tbmPsnVOs = NCLocator
				.getInstance()
				.lookup(ITBMPsndocQueryService.class)
				.queryLatestByCondition(pk_org, null,
						StringPiecer.getStrArray(calendarVOs, PsnJobCalendarVO.PK_PSNDOC), beginDate, endDate);
		String[] pk_psnjobs = new String[tbmPsnVOs.length];
		for (int i = 0; i < tbmPsnVOs.length; i++)
			pk_psnjobs[i] = tbmPsnVOs[i].getPk_psnjob();
		PsndocAggVO[] psnDocVOs = NCLocator.getInstance().lookup(IPsndocQryService.class)
				.queryPsndocVOByPks(pk_psnjobs);
		Map<String, PsndocVO> psnDocMap = new HashMap<String, PsndocVO>();
		if (ArrayUtils.isEmpty(psnDocVOs))
			return null;
		for (PsndocAggVO psnDocVO : psnDocVOs) {
			psnDocMap.put(psnDocVO.getParentVO().getPk_psndoc(), psnDocVO.getParentVO());
		}
		// 获取当前组织所有班次信息，并用map存储,key是班次主键,value是班次名称
		// AggShiftVO[] shiftVOs =
		// NCLocator.getInstance().lookup(IBclbQueryService.class).queryAllBclbAggVO(pk_org);
		AggShiftVO[] shiftVOs = ShiftServiceFacade.queryAllByHROrg(pk_org);
		Map<String, String> shiftMap = new HashMap<String, String>();
		shiftMap.put(ShiftVO.PK_GX, ResHelper.getString("6017psncalendar", "06017psncalendar0092")
		/* @res "公休" */);
		if (!ArrayUtils.isEmpty(shiftVOs)) {
			for (AggShiftVO shiftVO : shiftVOs) {
				shiftMap.put(shiftVO.getShiftVO().getPk_shift(), shiftVO.getShiftVO().getMultiLangName().toString());
			}
		}

		List<GeneralVO> returnVOs = new ArrayList<GeneralVO>();
		for (int i = 0; i < calendarVOs.length; i++) {
			GeneralVO exportVO = new GeneralVO();
			// 设置人员信息
			PsndocVO psnDocVO = psnDocMap.get(calendarVOs[i].getPk_psndoc());
			if (psnDocVO == null)
				continue;
			exportVO.setAttributeValue(PsnCalendarCommonValue.LISTCODE_CLERKCODE, psnDocVO.getPsnJobVO().getClerkcode());
			exportVO.setAttributeValue(PsnCalendarCommonValue.LISTCODE_PSNCODE, psnDocVO.getCode());
			exportVO.setAttributeValue(PsnCalendarCommonValue.LISTCODE_PSNNAME, psnDocVO.getName());
			for (int j = 3; j < fields.length; j++) {
				String pk_shift = calendarVOs[i].getCalendarMap().get(fields[j]);
				if (StringUtils.isEmpty(pk_shift))
					continue;
				exportVO.setAttributeValue(fields[j], shiftMap.get(pk_shift));
			}
			returnVOs.add(exportVO);
		}

		return returnVOs.toArray(new GeneralVO[0]);
	}

	@Override
	public PsnJobVO[] queryPsnJobVOsByConditionAndOverrideOrgWithUnit(String pk_org, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate, boolean isOverrideExistCalendar, boolean isHROrg,
			boolean businessUnitFlag) throws BusinessException {
		// 2013-04-24添加权限
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT,
				fromWhereSQL);
		String pk_hrorg = null;
		if (isHROrg)
			pk_hrorg = pk_org;
		else {
			IAOSQueryService aosService = NCLocator.getInstance().lookup(IAOSQueryService.class);
			OrgVO hrorgVO = aosService.queryHROrgByOrgPK(pk_org);
			pk_hrorg = hrorgVO.getPk_org();
		}
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		// 如果是覆盖已有的工作日历，则很简单，查询出最新的考勤档案即可
		if (isOverrideExistCalendar) {
			if (!isHROrg) {
				fromWhereSQL = TBMPsndocSqlPiecer.addOrgCond2QuerySQL(AdminOrgVO.PK_ADMINORG + "='" + pk_org + "'",
						fromWhereSQL);
			}
			TBMPsndocVO[] tbmpsndocvos = null;
			if (businessUnitFlag) {
				tbmpsndocvos = psndocService.queryLatestByConditionWithUnit(pk_hrorg, fromWhereSQL, beginDate, endDate,
						businessUnitFlag);
			} else {
				tbmpsndocvos = psndocService.queryLatestByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate);
			}
			if (ArrayUtils.isEmpty(tbmpsndocvos))
				return null;
			PsnJobVO[] returnVOs = new PsnJobVO[tbmpsndocvos.length];
			for (int i = 0; i < tbmpsndocvos.length; i++) {
				returnVOs[i] = new PsnJobVO();
				returnVOs[i].setPk_psndoc(tbmpsndocvos[i].getPk_psndoc());
				returnVOs[i].setPk_psnjob(tbmpsndocvos[i].getPk_psnjob());
				returnVOs[i].setPk_dept_v(tbmpsndocvos[i].getPk_dept_v());
				returnVOs[i].setPk_org_v(tbmpsndocvos[i].getPk_org_v());
			}
			return returnVOs;
		}
		// 查出日期范围内工作日历不完整的人员
		fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, endDate.toStdString());
		String[] tbmpsndocSelFields = new String[] { TBMPsndocVO.PK_PSNDOC, TBMPsndocVO.PK_PSNJOB };
		String orgversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_org_v"
				+ FromWhereSQLUtils.getAttPathPostFix());
		String deptversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_dept_v"
				+ FromWhereSQLUtils.getAttPathPostFix());
		String[] otherTableSelFields = new String[] {
				orgversionAlias + "." + AdminOrgVersionVO.PK_VID + " as " + TBMPsndocVO.PK_ORG_V,
				deptversionAlias + "." + DeptVersionVO.PK_VID + " as " + TBMPsndocVO.PK_DEPT_V };
		SQLParamWrapper wrapper = isHROrg ? TBMPsndocSqlPiecer
				.selectUnCompleteDailyDataByPsndocFieldAndDateFieldAndDateArea(pk_hrorg, tbmpsndocSelFields,
						otherTableSelFields, "tbm_psncalendar psncalendar", "psncalendar.pk_org",
						"psncalendar.pk_psndoc", "psncalendar.calendar", beginDate.toString(), endDate.toString(),
						null, null, fromWhereSQL) :

		TBMPsndocSqlPiecer.selectUnCompleteDailyDataByPsndocFieldAndDateFieldAndDateAreaOrg(pk_org, tbmpsndocSelFields,
				otherTableSelFields, "tbm_psncalendar psncalendar", "psncalendar.pk_org", "psncalendar.pk_psndoc",
				"psncalendar.calendar", beginDate.toString(), endDate.toString(), null, null, fromWhereSQL);
		String sql = wrapper.getSql();
		SQLParameter para = wrapper.getParam();
		List<PsnJobVO> returnList = (List<PsnJobVO>) new BaseDAO().executeQuery(sql, para, new BeanListProcessor(
				PsnJobVO.class));
		if (CollectionUtils.isEmpty(returnList))
			return null;
		return returnList.toArray(new PsnJobVO[0]);
	}

	/**
	 * 批量调换日历天和排班
	 * 
	 * @param psndocs
	 *            人员信息
	 * @param firstDate
	 *            调换日期1
	 * @param secondDate
	 *            调换日期2
	 * @author Ares.Tank 2018-9-6 15:15:10
	 */
	@Override
	public void batchChangeDateType(String pk_hrorg, String[] psndocs, UFLiteralDate firstDate,
			UFLiteralDate secondDate, String changedayorhourStr) throws BusinessException {

		if (firstDate.equals(secondDate) || psndocs.length <= 0) {// 相等的话,没什么变的
			return;

		}
		PsnCalendarDAO psnCalendarDAO = new PsnCalendarDAO();
		UFLiteralDate[] ufLDates = { firstDate, secondDate };
		List<AggPsnCalendar> resultList = new ArrayList<>();
		// List<String> resultPkList = new ArrayList<>();//pk存一下,用来删除的
		// 查出这些人的工作日历信息
		Map<String, Map<String, PsnCalendarVO>> forChangeMap = psnCalendarDAO.queryCalendarVOMapByPsndocs(pk_hrorg,
				psndocs, ufLDates);

		// BEGIN 张恒{21997} 将异动人员日历天信息存到list 2018/9/12
		String changetype = "";
		UFDouble changedayorhour = new UFDouble(0.00);
		if (!StringUtils.isEmpty(changedayorhourStr) && new Double(changedayorhourStr) > 0) {
			changetype = firstDate.toString();
			changedayorhour = new UFDouble(changedayorhourStr);
		}
		List<AggLeaveExtraRestVO> extraRestList = new ArrayList<AggLeaveExtraRestVO>();
		// END 张恒{21997} 将异动人员日历天信息存到list 2018/9/12

		for (String psndocStr : psndocs) {
			if (null != forChangeMap && null != forChangeMap.get(psndocStr)
					&& null != forChangeMap.get(psndocStr).get(firstDate.toString())
					&& null != forChangeMap.get(psndocStr).get(secondDate.toString())) {
				AggPsnCalendar temp = new AggPsnCalendar();
				// resultPkList.add(forChangeMap.get(psndocStr).get(firstDate.toString()).getPk_psncalendar());
				forChangeMap.get(psndocStr).get(firstDate.toString()).setCalendar(secondDate);
				// forChangeMap.get(psndocStr).get(firstDate.toString()).setPk_psncalendar(null);
				// forChangeMap.get(psndocStr).get(firstDate.toString()).setTs(null);
				temp.setParentVO(forChangeMap.get(psndocStr).get(firstDate.toString()));
				resultList.add(temp);

				temp = new AggPsnCalendar();
				// resultPkList.add(forChangeMap.get(psndocStr).get(secondDate.toString()).getPk_psncalendar());
				forChangeMap.get(psndocStr).get(secondDate.toString()).setCalendar(firstDate);
				// forChangeMap.get(psndocStr).get(secondDate.toString()).setPk_psncalendar(null);
				// forChangeMap.get(psndocStr).get(secondDate.toString()).setTs(null);

				temp.setParentVO(forChangeMap.get(psndocStr).get(secondDate.toString()));
				resultList.add(temp);

			}

			if (null != firstDate && null != secondDate && changedayorhour.compareTo(UFDouble.ZERO_DBL) > 0) {
				AggLeaveExtraRestVO saveVO = new AggLeaveExtraRestVO();
				LeaveExtraRestVO extraRestVO = new LeaveExtraRestVO();
				extraRestVO.setPk_psndoc(psndocStr);
				extraRestVO.setPk_org(pk_hrorg);
				Map<String, String> baseInfo = getPsnBaseInfo(psndocStr, pk_hrorg);
				extraRestVO.setPk_org_v(baseInfo.get("pk_org_v"));
				extraRestVO.setPk_dept_v(baseInfo.get("pk_dept_v"));
				extraRestVO.setPk_group(baseInfo.get("pk_group"));
				extraRestVO.setBilldate(new UFLiteralDate());
				extraRestVO.setDatebeforechange(firstDate);
				extraRestVO.setTypebeforechange(getDateTypeByPsnDate(pk_hrorg, psndocStr, firstDate));
				extraRestVO.setDateafterchange(secondDate);
				extraRestVO.setTypeafterchange(getDateTypeByPsnDate(pk_hrorg, psndocStr, secondDate));
				extraRestVO.setChangetype(extraRestVO.getTypebeforechange());
				extraRestVO.setChangedayorhour(changedayorhour);
				extraRestVO.setCreationtime(new UFDateTime());
				extraRestVO.setExpiredate(getExpiredate(psndocStr, firstDate, extraRestVO.getBilldate()));
				saveVO.setParent(extraRestVO);
				extraRestList.add(saveVO);
			}

		}
		IMDPersistenceService service = MDPersistenceService.lookupPersistenceService();
		/*
		 * if (resultList.size() > 0){
		 * psnCalendarDAO.deleteByPkArray(resultPkList.toArray(new String[0]));
		 * }
		 */
		/*
		 * BaseDAO bsDao = new BaseDAO(); // 由於需要批量交換數據,而且涉及到索引列,先drop索引 try {
		 * bsDao.executeUpdate("drop INDEX i_psncalendar ON tbm_psncalendar"); }
		 * catch (Exception e) { Debug.debug(e.getMessage(), e);
		 * 
		 * }
		 * 
		 * 
		 * String[] attrs = { PsnCalendarVO.CALENDAR };
		 * service.updateBillWithAttrs(resultList.toArray(new
		 * AggPsnCalendar[0]), attrs); // 重啟啟用索引 String sqlStr =
		 * "CREATE UNIQUE INDEX " + "i_psncalendar " + "ON " +
		 * "tbm_psncalendar " + "( " + "pk_psndoc, " + "calendar, " + "pk_org "
		 * + ") "; try { bsDao.executeUpdate(sqlStr); } catch (Exception e) {
		 * Debug.debug(e.getMessage(), e); }
		 */
		// Ares.Tank v2.0 存储方式不再drop/create索引,用交换的方式,避开索引的限制
		// 将两个调换天的日历分开
		Set<AggPsnCalendar> firstDaySet = new HashSet<>();
		Set<AggPsnCalendar> secondDaySet = new HashSet<>();

		for (AggPsnCalendar temp : resultList) {
			if (0 == temp.getPsnCalendarVO().getCalendar().compareTo(firstDate)) {

				temp.getPsnCalendarVO().setCalendar(new UFLiteralDate("9999-12-30"));
				firstDaySet.add(temp);
			} else if (0 == temp.getPsnCalendarVO().getCalendar().compareTo(secondDate)) {

				temp.getPsnCalendarVO().setCalendar(new UFLiteralDate("9999-12-29"));
				secondDaySet.add(temp);
			}
		}
		String[] attrs = { PsnCalendarVO.CALENDAR };
		// 把需要修改的数据全部改到地球毁灭后
		service.updateBillWithAttrs(firstDaySet.toArray(new AggPsnCalendar[0]), attrs);
		service.updateBillWithAttrs(secondDaySet.toArray(new AggPsnCalendar[0]), attrs);

		// 换回正确的日期

		for (AggPsnCalendar temp : firstDaySet) {
			temp.getPsnCalendarVO().setCalendar(firstDate);
		}
		for (AggPsnCalendar temp : secondDaySet) {
			temp.getPsnCalendarVO().setCalendar(secondDate);
		}
		service.updateBillWithAttrs(firstDaySet.toArray(new AggPsnCalendar[0]), attrs);
		service.updateBillWithAttrs(secondDaySet.toArray(new AggPsnCalendar[0]), attrs);

		// MOD 张恒 {21997} 将外加补休单据新增到数据库 2018/9/12
		if (extraRestList != null && extraRestList.size() > 0) {
			NCLocator.getInstance().lookup(ILeaveextrarestMaintain.class)
					.insert(extraRestList.toArray(new AggLeaveExtraRestVO[0]));
		}

		// psnCalendarDAO.insert(resultList.toArray(new AggPsnCalendar[0]));
		// int i = new BaseDAO().updateVOArray(resultList.toArray(new
		// PsnCalendarVO[0]));
		// Debug.print(i);
	}

	private UFLiteralDate getExpiredate(String psndocStr, UFLiteralDate firstDate, UFLiteralDate billDate)
			throws BusinessException {
		UFLiteralDate maxLeaveDate = NCLocator.getInstance().lookup(ILeaveextrarestMaintain.class)
				.calculateExpireDateByWorkAge(psndocStr, firstDate, billDate);
		return maxLeaveDate;
	}

	/**
	 * 查詢一下基本的信息
	 * 
	 * @param psndocStr
	 * @param pk_hrorg
	 * @return pk_org_v pk_dept_v pk_group
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getPsnBaseInfo(String psndocStr, String pk_hrorg) throws BusinessException {
		Map<String, String> resultMap = new HashMap<>();
		IPsndocQueryService psnQuery = NCLocator.getInstance().lookup(IPsndocQueryService.class);
		PsnjobVO psnjobvo = psnQuery.queryPsnJobVOByPsnDocPK(psndocStr);
		String pk_dept = psnjobvo.getPk_dept();
		String sqlStr = "select dept.pk_vid pk_dept_v, orgs.pk_vid pk_org_v ,orgs.pk_group pk_group"
				+ " from org_dept dept " + " left join org_orgs orgs on orgs.pk_org = '" + pk_hrorg
				+ "' where dept.pk_dept = '" + pk_dept + "'";
		resultMap = (Map<String, String>) getBaseDAO().executeQuery(sqlStr, new MapProcessor());

		return resultMap == null ? new HashMap<String, String>() : resultMap;
	}

	private Integer getDateTypeByPsnDate(String pk_hrorg, String pk_psndoc, UFLiteralDate checkDate)
			throws BusinessException {
		// 查出人员工作日历信息
		PsnCalendarDAO psnCalendarDAO = new PsnCalendarDAO();
		Map<String, Map<String, PsnCalendarVO>> dateTypeMap = psnCalendarDAO.queryCalendarVOMapByPsndocs(pk_hrorg,
				new String[] { pk_psndoc }, new UFLiteralDate[] { checkDate });
		if (dateTypeMap == null) {
			PsndocVO psndocvo = (PsndocVO) this.getBaseDAO().retrieveByPK(PsndocVO.class, pk_psndoc);
			throw new BusinessException("员工 [" + psndocvo.getCode() + "] 在 [" + checkDate.toString() + "] 未找到有效排班。");
		}
		PsnCalendarVO psnVo = dateTypeMap.get(pk_psndoc).get(checkDate.toString());
		return psnVo.getDate_daytype();
	}

	/**
	 * 批量变更日历天和排班
	 * 
	 * @param psndocs
	 *            人员信息
	 * @param date
	 *            需要变更的日期
	 * @param 日历天类型
	 *            ,@see HolidayVo
	 * @author Ares.Tank 2018-9-6 15:15:10
	 */
	@Override
	public void batchChangeDateType4OneDay(String pk_hrorg, String[] psndocs, UFLiteralDate changeDate,
			Integer dateType, String changedayorhourStr) throws BusinessException {
		if (null == psndocs || null == changeDate || psndocs.length <= 0) {//
			return;

		}
		PsnCalendarDAO psnCalendarDAO = new PsnCalendarDAO();
		UFLiteralDate[] ufLDates = { changeDate };
		List<AggPsnCalendar> resultList = new ArrayList<>();
		// List<String> resultPkList = new ArrayList<>();//pk存一下,用来删除的
		// 查出这些人的工作日历信息
		Map<String, Map<String, PsnCalendarVO>> forChangeMap = psnCalendarDAO.queryCalendarVOMapByPsndocs(pk_hrorg,
				psndocs, ufLDates);
		for (String psndocStr : psndocs) {
			if (null != forChangeMap && null != forChangeMap.get(psndocStr)
					&& null != forChangeMap.get(psndocStr).get(changeDate.toString())) {
				AggPsnCalendar temp = new AggPsnCalendar();
				forChangeMap.get(psndocStr).get(changeDate.toString()).setDate_daytype(dateType);
				temp.setParentVO(forChangeMap.get(psndocStr).get(changeDate.toString()));
				resultList.add(temp);
				// resultPkList.add(forChangeMap.get(psndocStr).get(changeDate).getPk_psncalendar());
			}
		}
		// 生成外加補休單據
		List<AggLeaveExtraRestVO> extraRestList = new ArrayList<AggLeaveExtraRestVO>();
		UFDouble changedayorhour = new UFDouble(0.00);
		if (!StringUtils.isEmpty(changedayorhourStr) && new Double(changedayorhourStr) > 0) {
			changedayorhour = new UFDouble(changedayorhourStr);
		}
		if (changedayorhour.compareTo(UFDouble.ZERO_DBL) >= 0) {
			for (String psndocStr : psndocs) {
				if (null != changeDate) {
					AggLeaveExtraRestVO saveVO = new AggLeaveExtraRestVO();
					LeaveExtraRestVO extraRestVO = new LeaveExtraRestVO();
					extraRestVO.setPk_psndoc(psndocStr);
					extraRestVO.setPk_org(pk_hrorg);
					Map<String, String> baseInfo = getPsnBaseInfo(psndocStr, pk_hrorg);
					extraRestVO.setPk_org_v(baseInfo.get("pk_org_v"));
					extraRestVO.setPk_dept_v(baseInfo.get("pk_dept_v"));
					extraRestVO.setPk_group(baseInfo.get("pk_group"));
					extraRestVO.setBilldate(new UFLiteralDate());
					extraRestVO.setDatebeforechange(changeDate);
					extraRestVO.setTypebeforechange(getDateTypeByPsnDate(pk_hrorg, psndocStr, changeDate));
					extraRestVO.setDateafterchange(changeDate);
					extraRestVO.setTypeafterchange(dateType);
					extraRestVO.setChangetype(extraRestVO.getTypebeforechange());
					extraRestVO.setChangedayorhour(changedayorhour);
					extraRestVO.setCreationtime(new UFDateTime());
					extraRestVO.setExpiredate(getExpiredate(psndocStr, changeDate, extraRestVO.getBilldate()));
					saveVO.setParent(extraRestVO);
					extraRestList.add(saveVO);
				}
			}
		}

		/*
		 * if (resultList.size() > 0){
		 * psnCalendarDAO.deleteByPkArray(resultPkList.toArray(new String[0]));
		 * }
		 */

		IMDPersistenceService service = MDPersistenceService.lookupPersistenceService();
		String[] attrs = { PsnCalendarVO.DATE_DAYTYPE };
		service.updateBillWithAttrs(resultList.toArray(new AggPsnCalendar[0]), attrs);
		NCLocator.getInstance().lookup(ILeaveextrarestMaintain.class)
				.insert(extraRestList.toArray(new AggLeaveExtraRestVO[0]));
		// psnCalendarDAO.insert(resultList.toArray(new AggPsnCalendar[0]));

	}

	/**
	 * 批量调换日历天和排班
	 * 
	 * @param psndocs
	 *            人员信息
	 * @param firstDate
	 *            调换日期1
	 * @param secondDate
	 *            调换日期2
	 * @author he 2018-9-6 15:15:10
	 */
	@Override
	public List<AggPsnCalendar> changeDateType(String pk_hrorg, String[] psndocs, UFLiteralDate firstDate,
			UFLiteralDate secondDate) throws BusinessException {

		if (firstDate.equals(secondDate)) {// 相等的话,没什么变的
			return null;

		}
		PsnCalendarDAO psnCalendarDAO = new PsnCalendarDAO();
		UFLiteralDate[] ufLDates = { firstDate, secondDate };
		List<AggPsnCalendar> resultList = new ArrayList<>();
		// List<String> resultPkList = new ArrayList<>();//pk存一下,用来删除的
		// 查出这些人的工作日历信息
		Map<String, Map<String, PsnCalendarVO>> forChangeMap = psnCalendarDAO.queryCalendarVOMapByPsndocs(pk_hrorg,
				psndocs, ufLDates);
		for (String psndocStr : psndocs) {
			if (null != forChangeMap && null != forChangeMap.get(psndocStr)
					&& null != forChangeMap.get(psndocStr).get(firstDate.toString())
					&& null != forChangeMap.get(psndocStr).get(secondDate.toString())) {
				AggPsnCalendar temp = new AggPsnCalendar();
				// resultPkList.add(forChangeMap.get(psndocStr).get(firstDate.toString()).getPk_psncalendar());
				forChangeMap.get(psndocStr).get(firstDate.toString()).setCalendar(secondDate);
				// forChangeMap.get(psndocStr).get(firstDate.toString()).setPk_psncalendar(null);
				// forChangeMap.get(psndocStr).get(firstDate.toString()).setTs(null);
				temp.setParentVO(forChangeMap.get(psndocStr).get(firstDate.toString()));
				resultList.add(temp);

				temp = new AggPsnCalendar();
				// resultPkList.add(forChangeMap.get(psndocStr).get(secondDate.toString()).getPk_psncalendar());
				forChangeMap.get(psndocStr).get(secondDate.toString()).setCalendar(firstDate);
				// forChangeMap.get(psndocStr).get(secondDate.toString()).setPk_psncalendar(null);
				// forChangeMap.get(psndocStr).get(secondDate.toString()).setTs(null);

				temp.setParentVO(forChangeMap.get(psndocStr).get(secondDate.toString()));
				resultList.add(temp);

			}
		}
		return resultList;
	}

	/**
	 * 批量变更日历天和排班
	 * 
	 * @param psndocs
	 *            人员信息
	 * @param date
	 *            需要变更的日期
	 * @param 日历天类型
	 *            ,@see HolidayVo
	 * @author he 2018-9-6 15:15:10
	 */
	@Override
	public List<AggPsnCalendar> changeDateType4OneDay(String pk_hrorg, String[] psndocs, UFLiteralDate changeDate,
			Integer dateType) throws BusinessException {
		if (null == psndocs || null == changeDate) {//
			return null;

		}
		PsnCalendarDAO psnCalendarDAO = new PsnCalendarDAO();
		UFLiteralDate[] ufLDates = { changeDate };
		List<AggPsnCalendar> resultList = new ArrayList<>();
		// List<String> resultPkList = new ArrayList<>();//pk存一下,用来删除的
		// 查出这些人的工作日历信息
		Map<String, Map<String, PsnCalendarVO>> forChangeMap = psnCalendarDAO.queryCalendarVOMapByPsndocs(pk_hrorg,
				psndocs, ufLDates);
		for (String psndocStr : psndocs) {
			if (null != forChangeMap && null != forChangeMap.get(psndocStr)
					&& null != forChangeMap.get(psndocStr).get(changeDate.toString())) {
				AggPsnCalendar temp = new AggPsnCalendar();
				forChangeMap.get(psndocStr).get(changeDate.toString()).setDate_daytype(dateType);
				temp.setParentVO(forChangeMap.get(psndocStr).get(changeDate.toString()));
				resultList.add(temp);
				// resultPkList.add(forChangeMap.get(psndocStr).get(changeDate).getPk_psncalendar());
			}
		}
		return resultList;

	}

	@Override
	public String getPsnCodeByPk(Set<String> pk_psndocSet) throws BusinessException {
		if (pk_psndocSet == null || pk_psndocSet.size() <= 0) {
			return null;
		}
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(pk_psndocSet.toArray(new String[0]));
		String sql = "select code from bd_psndoc where pk_psndoc in (" + psndocsInSQL + ")";
		@SuppressWarnings("unchecked")
		List<Object> resultList = (ArrayList<Object>) getBaseDAO().executeQuery(sql, new ColumnsListProcessor());
		StringBuilder sb = new StringBuilder();
		if (resultList != null && resultList.size() > 0) {
			for (Object code : resultList) {
				sb.append(((String[]) code)[0]).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}
}