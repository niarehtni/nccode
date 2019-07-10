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

	// Ares.Tank 2018-9-3 16:12:47 ����֮��д����������,������ˮƽ����,�����´�����˵��û��ʼ��
	// ssx 2019-01-04 д�˾�����ˮƽ���У���д�Ļ��ǵ����ߵ�ˮƽ���У����ٺú�����ò��÷����Լ��ļ��
	// Ares.Tank 2019-1-4 ����Ǯ�Ļ��ǿ��Լ�ֵ�,˭��Ǯ����ȥ��~
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
		// ��ѯ��һ���ʱ��
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
	 * 2011.9.23������������ҵ��Ԫ��������ҵ��Ԫ������
	 * 
	 * @param pk_org
	 *            ��HR��֯����
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @param is4Mgr
	 *            ,�Ƿ����ھ�������
	 * @return
	 * @throws BusinessException
	 */
	protected PsnJobCalendarVO[] queryCalendarVOByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate, boolean is4Mgr, String pk_dept, boolean containsSubDepts)
			throws BusinessException {
		// 2013-04-24���Ȩ�� //��ѯȨ��
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		if (is4Mgr)
			fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		// ��ѯ�����ڷ�Χ�ڵ����п��ڵ�����¼�����ڷ�Χ�����µ�һ����Ϊ����ƥ�䣬Ȼ���ҳ���Ա�����ڷ�Χ�����еĿ��ڵ�����¼
		Map<String, List<TBMPsndocVO>> psndocMap = psndocService.queryTBMPsndocMapByCondition(is4Mgr ? null : pk_hrorg,
				fromWhereSQL, beginDate, endDate, true, true, null);
		if (psndocMap == null || psndocMap.isEmpty())
			return null;
		String[] pk_psndocs = psndocMap.keySet().toArray(new String[0]);
		IHRHolidayQueryService holidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		// ����������map��key����Ա������value��key�����ڣ�value�ǹ��������ͣ���holidayvo��
		Map<String, Map<String, Integer>> holidayInfo = holidayService.queryPsnWorkDayTypeInfo(pk_hrorg, pk_psndocs,
				beginDate, endDate);
		// ��Ա�Ĺ���������key��pk_psndoc,value�ǹ�������list��ֻ�������ڵ�����Ч���ڵĹ����������ڿ��ڵ�����Ч�����������psncalendar�����ݣ�Ҳ�����������map��
		Map<String, List<PsnCalendarVO>> calendarMap = new PsnCalendarDAO().queryCalendarVOListMapByPsndocs(
				is4Mgr ? null : pk_hrorg, pk_psndocs, beginDate, endDate);
		List<PsnJobCalendarVO> returnList = new ArrayList<PsnJobCalendarVO>();
		UFLiteralDate[] dateScope = CommonUtils.createDateArray(beginDate, endDate);
		Map<String, TBMPsndocVO[]> hrOrgDeptPsndocMap = null;// ����Ǿ�������������Ҫ��ÿ����Ա��pk_org�Ҳ�����pk_dept(���¼�)���ŵ��춼��ѯ����
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
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);// ������Ч�Ŀ��ڵ���
			TBMPsndocVO[] psndocsInHrOrgAndDept = is4Mgr ? (hrOrgDeptPsndocMap != null ? hrOrgDeptPsndocMap
					.get(pk_psndoc) : null) : null;
			// ���һ��Ϊ��Ч�ģ�������ʾʱ�����һ��Ϊ׼
			TBMPsndocVO psndocVO = psndocList.get(psndocList.size() - 1);
			PsnJobCalendarVO vo = new PsnJobCalendarVO();
			returnList.add(vo);
			vo.setPk_psnjob(psndocVO.getPk_psnjob());
			vo.setPk_psndoc(psndocVO.getPk_psndoc());
			vo.setPk_org_v(psndocVO.getPk_org_v());
			vo.setPk_dept_v(psndocVO.getPk_dept_v());
			// װ�뿼�ڵ�����Ч����
			for (UFLiteralDate date : dateScope) {
				String dateStr = date.toString();
				if (TBMPsndocVO.isIntersect(psndocList, dateStr)) {
					vo.getPsndocEffectiveDateSet().add(dateStr);
					vo.getOrgMap().put(dateStr, TBMPsndocVO.findIntersectionVO(psndocList, dateStr).getPk_joborg());
				}
				if (is4Mgr && TBMPsndocVO.isIntersect(psndocsInHrOrgAndDept, dateStr))// ����Ǿ�����������Ҫװ����hr��֯�ڡ��ڲ��Ź�Ͻ��Χ�ڵ���Ա
					vo.getPsndocEffectiveDateSetInHROrgAndDept().add(dateStr);
			}
			// װ����
			if (calendarMap != null) {
				List<PsnCalendarVO> calList = calendarMap.get(psndocVO.getPk_psndoc());
				if (!org.apache.commons.collections.CollectionUtils.isEmpty(calList))
					for (PsnCalendarVO calVO : calList)
						vo.getCalendarMap().put(calVO.getCalendar().toString(), calVO.getPk_shift());
			}
			// װ�빤��������
			// TODO ?
			Map<String, Integer> dayType = holidayInfo != null ? holidayInfo.get(vo.getPk_psndoc()) : null;
			if (dayType != null)
				vo.getDayTypeMap().putAll(holidayInfo.get(vo.getPk_psndoc()));
		}
		return returnList.toArray(new PsnJobCalendarVO[0]);
	}

	/*
	 * 2011.9.28����ɰ��ҵ��Ԫ���ͼ���ҵ��Ԫ������ (non-Javadoc)
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
		// ��ѯ�����ڷ�Χ�ڵ����п��ڵ�����¼�����ڷ�Χ�����µ�һ����Ϊ����ƥ�䣬Ȼ���ҳ���Ա�����ڷ�Χ�����еĿ��ڵ�����¼
		Map<String, List<TBMPsndocVO>> psndocMap = psndocService.queryTBMPsndocMapByConditionForTeam(is4Mgr ? null
				: pk_hrorg, fromWhereSQL, beginDate, endDate, true, true, null);
		if (psndocMap == null || psndocMap.isEmpty())
			return null;
		String[] pk_psndocs = psndocMap.keySet().toArray(new String[0]);
		IHRHolidayQueryService holidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		// ����������map��key����Ա������value��key�����ڣ�value�ǹ��������ͣ���holidayvo��
		Map<String, Map<String, Integer>> holidayInfo = holidayService.queryPsnWorkDayTypeInfo(pk_hrorg, pk_psndocs,
				beginDate, endDate);
		// ��Ա�Ĺ���������key��pk_psndoc,value�ǹ�������list��ֻ�������ڵ�����Ч���ڵĹ����������ڿ��ڵ�����Ч�����������psncalendar�����ݣ�Ҳ�����������map��
		Map<String, List<PsnCalendarVO>> calendarMap = new PsnCalendarDAO().queryCalendarVOListMapByPsndocs(
				is4Mgr ? null : pk_hrorg, pk_psndocs, beginDate, endDate);
		// Map<String, List<PsnCalendarVO>> calendarMap = new
		// PsnCalendarDAO().queryCalendarVOListMapByPsndocsForTeam(is4Mgr?null:pk_hrorg,
		// pk_psndocs, beginDate, endDate,pk_team);
		List<PsnJobCalendarVO> returnList = new ArrayList<PsnJobCalendarVO>();
		UFLiteralDate[] dateScope = CommonUtils.createDateArray(beginDate, endDate);
		Map<String, TBMPsndocVO[]> hrOrgDeptPsndocMap = null;// ����Ǿ�������������Ҫ��ÿ����Ա��pk_org�Ҳ�����pk_dept(���¼�)���ŵ��춼��ѯ����
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
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);// ������Ч�Ŀ��ڵ���
			TBMPsndocVO[] psndocsInHrOrgAndDept = is4Mgr ? (hrOrgDeptPsndocMap != null ? hrOrgDeptPsndocMap
					.get(pk_psndoc) : null) : null;
			// ���һ��Ϊ��Ч�ģ�������ʾʱ�����һ��Ϊ׼
			TBMPsndocVO psndocVO = psndocList.get(psndocList.size() - 1);
			PsnJobCalendarVO vo = new PsnJobCalendarVO();
			returnList.add(vo);
			vo.setPk_psnjob(psndocVO.getPk_psnjob());
			vo.setPk_psndoc(psndocVO.getPk_psndoc());
			vo.setPk_org_v(psndocVO.getPk_org_v());
			vo.setPk_dept_v(psndocVO.getPk_dept_v());
			// װ�뿼�ڵ�����Ч����
			for (UFLiteralDate date : dateScope) {
				String dateStr = date.toString();
				if (TBMPsndocVO.isIntersect(psndocList, dateStr)) {
					vo.getPsndocEffectiveDateSet().add(dateStr);
					vo.getOrgMap().put(dateStr, TBMPsndocVO.findIntersectionVO(psndocList, dateStr).getPk_joborg());
				}
				if (is4Mgr && TBMPsndocVO.isIntersect(psndocsInHrOrgAndDept, dateStr))// ����Ǿ�����������Ҫװ����hr��֯�ڡ��ڲ��Ź�Ͻ��Χ�ڵ���Ա
					vo.getPsndocEffectiveDateSetInHROrgAndDept().add(dateStr);
			}
			// װ����
			if (calendarMap != null) {
				List<PsnCalendarVO> calList = calendarMap.get(psndocVO.getPk_psndoc());
				if (!org.apache.commons.collections.CollectionUtils.isEmpty(calList))
					for (PsnCalendarVO calVO : calList)
						vo.getCalendarMap().put(calVO.getCalendar().toString(), calVO.getPk_shift());
			}
			// װ�빤��������
			Map<String, Integer> dayType = holidayInfo != null ? holidayInfo.get(vo.getPk_psndoc()) : null;
			if (dayType != null)
				vo.getDayTypeMap().putAll(holidayInfo.get(vo.getPk_psndoc()));
		}
		return returnList.toArray(new PsnJobCalendarVO[0]);
	}

	/*
	 * 2011.9.23����ɰ��ҵ��Ԫ���ͼ���ҵ��Ԫ������ (non-Javadoc)
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
		// ����ǲ�ѯ���У���ֱ�ӷ���
		if (vos == null || queryScope == QueryScopeEnum.all)
			return vos;
		List<PsnJobCalendarVO> returnList = new ArrayList<PsnJobCalendarVO>();
		// Ϊ��Ч�ʣ��ֳ�����forѭ�������д��һ��forѭ�������жϴ�������
		// ����ǲ�ѯ�����Ű���Ա����Ҫ�ж��Ű������Ƿ�С�ڿ��ڵ�����Ч�������Ű���������0
		if (queryScope == QueryScopeEnum.part) {
			for (PsnJobCalendarVO vo : vos) {
				if (vo.getCalendarMap().size() < vo.getPsndocEffectiveDateSet().size()
						&& vo.getCalendarMap().size() > 0)
					returnList.add(vo);
			}
			return returnList.toArray(new PsnJobCalendarVO[0]);
		}
		// ����ǲ�ѯ��δ�Ű���Ա����Ҫ�ж��Ű������Ƿ����0
		if (queryScope == QueryScopeEnum.not) {
			for (PsnJobCalendarVO vo : vos) {
				if (vo.getCalendarMap().size() == 0)
					returnList.add(vo);
			}
			return returnList.toArray(new PsnJobCalendarVO[0]);
		}
		// �ߵ�����϶��ǲ�ѯ��ȫ�Ű���Ա��ֻ���ж��Ű������Ƿ��뿼�ڵ�����Ч������ȼ���
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
						if (1 == typeVO.getGxcomtype()) {// ���ݼ��ݼٵĲ��ڹ���ʱ��ʾ�ݼ�
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
		if (!withOldShift) {// ���������ԭ���
			if (StringUtils.isEmpty(newShift)) {// ����°��Ϊ�գ���ɾ�����ڷ�Χ�ڵ����й�������
				new PsnCalendarDAO().deleteByFromWhereAndDateArea(pk_org, fromWhereSQL, beginDate, endDate);
				return;
			}
			// ����°�β��գ������������Ű�������һ���ѭ���Ű࣬�Ҹ���ԭ�а�Σ����������վ�
			String[] pk_psndocs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
					.queryLatestPsndocsByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate);
			if (ArrayUtils.isEmpty(pk_psndocs))
				return;
			// ������ڵ��������������⣬���ܻᵼ��pk_psndoc���ظ��ģ�����ȥ��
			Set<String> psndocSet = new HashSet<String>(Arrays.asList(pk_psndocs));
			pk_psndocs = psndocSet.toArray(new String[0]);
			circularArrange(hrorg.getPk_group(), pk_hrorg, pk_org, pk_psndocs, beginDate, endDate,
					new String[] { newShift }, false, true, false);
			return;
		}
		// �����ߵ������ʾҪ����ԭ���
		// ���oldShift��newShift��ͬ����ֱ�ӷ��ز�������
		oldShift = StringUtils.isEmpty(oldShift) ? "" : oldShift;
		newShift = StringUtils.isEmpty(newShift) ? "" : newShift;
		if (oldShift.equals(newShift))
			return;
		// ���ս�����ת��Ϊsave�����ܽ��ܵĲ�����Ȼ��ֱ�ӵ���save����
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
				// ���date�����Ű࣬�Ұ����������oldShift������date��δ�Ű࣬��oldShiftΪ�գ�����Ϊ����oldShift��ƥ������
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
		if (!withOldShift) {// ���������ԭ���
			if (StringUtils.isEmpty(newShift)) {// ����°��Ϊ�գ���ɾ�����ڷ�Χ�ڵ����й�������
				// new PsnCalendarDAO().deleteByFromWhereAndDateArea(pk_org,
				// fromWhereSQL, beginDate, endDate);
				// new PsnCalendarDAO().deleteByPsndocsAndDateArea(pk_hrorg,
				// pk_psndocs, beginDate, endDate);//����������Ӧ����ҵ��Ԫ����hr��֯
				new PsnCalendarDAO().deleteByPsndocsAndDateArea(pk_org, pk_psndocs, beginDate, endDate);
				return;
			}
			// ����°�β��գ������������Ű�������һ���ѭ���Ű࣬�Ҹ���ԭ�а�Σ����������վ�
			// String[] pk_psndocs =
			// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocsByCondition(pk_hrorg,
			// fromWhereSQL, beginDate, endDate);
			if (ArrayUtils.isEmpty(pk_psndocs))
				return;
			// ������ڵ��������������⣬���ܻᵼ��pk_psndoc���ظ��ģ�����ȥ��
			Set<String> psndocSet = new HashSet<String>(Arrays.asList(pk_psndocs));
			pk_psndocs = psndocSet.toArray(new String[0]);
			circularArrange(hrorg.getPk_group(), pk_hrorg, pk_org, pk_psndocs, beginDate, endDate,
					new String[] { newShift }, false, true, false);
			return;
		}
		// �����ߵ������ʾҪ����ԭ���
		// ���oldShift��newShift��ͬ����ֱ�ӷ��ز�������
		oldShift = StringUtils.isEmpty(oldShift) ? "" : oldShift;
		newShift = StringUtils.isEmpty(newShift) ? "" : newShift;
		// if(oldShift.equals(newShift))
		// return;
		// ���ս�����ת��Ϊsave�����ܽ��ܵĲ�����Ȼ��ֱ�ӵ���save����
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
				// ���date�����Ű࣬�Ұ����������oldShift������date��δ�Ű࣬��oldShiftΪ�գ�����Ϊ����oldShift��ƥ������
				if ((oldShift.equals(calendarMap.get(date.toString())))
						|| (oldShift.equals("") && !calendarMap.containsKey(date.toString()))) {
					vo.getModifiedCalendarMap().put(date.toString(), newShift);
				}
			}
		}
		save(pk_hrorg, psnjobVOs);
	}

	/*
	 * pk_org:ҵ��Ԫ���� V61��ʼ����Ա����������Ϊҵ��Ԫ���ĵ������Ź�������ʱ��Ҫָ��ҵ��Ԫ��ʹ�ô�ҵ��Ԫ�µİ�Ρ�ʱ���ͼ���
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
		// �˴���pk_org��ҵ��Ԫ�����ڼ�ʹ�õ���hr��֯�������Ҫ����һ��
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
		// ���ȼ�У��һ��ѭ���Ű�İ���Ƿ��ͻ��
		new CalendarShiftMutexChecker().simpleCheckCircularArrange(pk_org, beginDate, endDate, calendarPks, false);
		// �����У��ͨ��������Խ����Ű�
		// OrgVO orgVO = (OrgVO) new BaseDAO().retrieveByPK(OrgVO.class,
		// pk_org);
		// String pk_group = orgVO.getPk_group();
		// ����������վɣ���˵������Ҫ���Ǽ���
		if (!isHolidayCancel) {
			circularArrangeIgnoreHolidayBatch(pk_group, pk_hrorg, pk_org, pk_psndocs, beginDate, endDate, calendarPks,
					false, overrideExistCalendar);
		} else {
			// ���������ȡ������Ҫ��ѯҵ��Ԫ�ڵļ��ռ���
			// HolidayInfo holidayInfo =
			// NCLocator.getInstance().lookup(IHolidayQueryService.class).queryHolidayInfo(pk_org,
			// beginDate, endDate);
			HolidayInfo<HRHolidayVO> holidayInfo = NCLocator.getInstance().lookup(IHRHolidayQueryService.class)
					.queryHolidayInfo(pk_org, beginDate, endDate);
			// ����˷�Χ��û�м��գ�����Ȼ���ò��ÿ��Ǽ��յ��Ű෽��
			if (holidayInfo == null) {
				circularArrangeIgnoreHolidayBatch(pk_group, pk_hrorg, pk_org, pk_psndocs, beginDate, endDate,
						calendarPks, true, overrideExistCalendar);
			} else {
				// ���������ȡ�������м��գ�������Ƚϸ���
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
		// ���ȼ�У��һ��ѭ���Ű�İ���Ƿ��ͻ��
		new CalendarShiftMutexChecker().simpleCheckCircularArrange(pk_org, beginDate, endDate, calendarPks, false);
		// �����У��ͨ��������Խ����Ű�
		OrgVO orgVO = (OrgVO) new BaseDAO().retrieveByPK(OrgVO.class, pk_org);
		String pk_group = orgVO.getPk_group();
		// ����������վɣ���˵������Ҫ���Ǽ���
		if (!isHolidayCancel)
			return circularArrangeIgnoreHolidayForTeam(pk_group, pk_org, pk_psndocs, beginDate, endDate, calendarPks,
					false, overrideExistCalendar, pk_team);
		// ���������ȡ������Ҫ��ѯҵ��Ԫ�ڵļ��ռ���
		// HolidayInfo holidayInfo =
		// NCLocator.getInstance().lookup(IHolidayQueryService.class).queryHolidayInfo(pk_org,
		// beginDate, endDate);
		HolidayInfo<HRHolidayVO> holidayInfo = NCLocator.getInstance().lookup(IHRHolidayQueryService.class)
				.queryHolidayInfo(pk_org, beginDate, endDate);
		// ����˷�Χ��û�м��գ�����Ȼ���ò��ÿ��Ǽ��յ��Ű෽��
		if (holidayInfo == null)
			return circularArrangeIgnoreHolidayForTeam(pk_group, pk_org, pk_psndocs, beginDate, endDate, calendarPks,
					true, overrideExistCalendar, pk_team);
		// ����������վɣ����м��գ�������Ƚϸ���
		return circularArrangeWithHolidayForTeam(pk_group, pk_org, pk_psndocs, beginDate, endDate, calendarPks,
				overrideExistCalendar, holidayInfo, pk_team);
	}

	/**
	 * Ϊ�˷�ֹ���ݲ�ѯ������������ݵ����ƣ��ڴ˶���Ա���з�������500��һ����
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
		// Ĭ�ϰ�
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// ҵ��Ԫ�����а��
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		// ҵ��Ԫ��ʱ��
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
	 * ���Ǽ��յ�ѭ���Ű�
	 * 
	 * @param pk_org
	 *            ,ҵ��Ԫ����
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
		// �洢Ҫ���õİ�ε�map��key����Ա������value��key��date��value�ǰ������.���ǵ��п��ܲ�����εĶԵ����������modifiedCalendarMap������ڷ�Χ���ܻᳬ��begindate��enddate�ķ�Χ
		Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String, String>>();
		// ��ѭ���İ�ΰ����ڷ�Χȫ��չ����key�����ڣ�value�ǰ������
		Map<String, String> originalExpandedDatePkShiftMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate,
				endDate, calendarPks);
		UFLiteralDate[] dateArea = CommonUtils.createDateArray(beginDate, endDate);
		// String pk_hrorg =
		// NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		// Ĭ�ϰ�
		// AggShiftVO defaultShift =
		// ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// //ҵ��Ԫ�����а��
		// Map<String, AggShiftVO> shiftMap =
		// ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		// ���յ��������,key����Ա������value��key�Ǽ���������value���Ƿ�����
		Map<String, Map<String, Boolean>> psnEnjoyHolidayMap = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_org,
				pk_psndocs, holidayInfo.getHolidayVOs());
		Map<String, Map<String, AggPsnCalendar>> holidayCutMap = new HashMap<String, Map<String, AggPsnCalendar>>();
		// ��¼�Ե�ǰ��ε�map��key����Ա��value��key�����ڣ�value�ǶԵ�ǰ�İ��
		Map<String, Map<String, String>> psnBeforeExgPkShiftMap = new HashMap<String, Map<String, String>>();
		// //ҵ��Ԫ��ʱ��
		// TimeZone timeZone =
		// NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		// ��Ա�Ѿ��źõİ��,��Χ��min(beginDate,�Ե���,����).getDateBefore(1)��max(endDate,�Ե��գ�����).getDateAfter(1)
		Set<String> holidayDateSet = new HashSet<String>();// �洢���ա��Ե��յ�map��key�����ڣ���������������ȷ����ѯ���������ķ�Χ
		holidayDateSet.addAll(holidayInfo.getHolidayMap().keySet());
		holidayDateSet.addAll(holidayInfo.getSwitchMap().keySet());
		String[] allDates = holidayDateSet.toArray(new String[0]);// �Լ��ա��Ե�������
		Arrays.sort(allDates);
		//�����յ�����Ű���Ϊ����  wangywt 20190624 begin
		if(holidayInfo.getHolidayMap()!=null){
			for(String holiday: holidayInfo.getHolidayMap().keySet()){
				if(originalExpandedDatePkShiftMap.keySet().contains(holiday)){
					originalExpandedDatePkShiftMap.put(holiday, ShiftVO.PK_GX);
				}
			}
		}
		//�����յ�����Ű���Ϊ����  wangywt 20190624 end
		// ��ѯ����������������ڷ�Χ
		UFLiteralDate calendarQueryBeginDate = allDates[0].compareTo(beginDate.toString()) < 0 ? UFLiteralDate.getDate(
				allDates[0]).getDateBefore(1) : beginDate.getDateBefore(1);
		UFLiteralDate calendarQueryEndDate = allDates[allDates.length - 1].compareTo(endDate.toString()) > 0 ? UFLiteralDate
				.getDate(allDates[allDates.length - 1]).getDateAfter(1) : endDate.getDateAfter(1);
		Map<String, Map<String, PsnCalendarVO>> psnExistsCalendarMap = new PsnCalendarDAO()
				.queryCalendarVOMapByPsndocs(pk_hrorg, pk_psndocs, calendarQueryBeginDate, calendarQueryEndDate);
		// ҵ��Ԫ�ڿ��ڵ���Map��key����Ա����,��map��Ҫ��������Ա���ڵ�����Ч���ڷ�Χ
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_org, pk_psndocs, calendarQueryBeginDate, calendarQueryEndDate, true);
		// ����ѭ������
		if (psndocMap == null) {
			return;
		}
		for (int i = 0; i < pk_psndocs.length; i++) {
			Map<String, String> cloneDatePkShiftMap = new HashMap<String, String>(originalExpandedDatePkShiftMap);// ÿ���˵�������һ�ݣ���Ϊ��Ȼ��ѭ���Ű࣬��������Ű���������ÿ���˶���ͬ
			Map<String, String> beforeExgPkShiftMap = new HashMap<String, String>();
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndocs[i]);
			modifiedCalendarMap.put(pk_psndocs[i], cloneDatePkShiftMap);
			psnBeforeExgPkShiftMap.put(pk_psndocs[i], beforeExgPkShiftMap);
			// �Լ����������(��Ϊ�漰���Ե���������Щ���������ǰ��ʹ������)
			Set<String> processedDateSet = new HashSet<String>();
			// ѭ�������û����õ�ѭ�����
			for (int dateIndex = 0; dateIndex < dateArea.length; dateIndex++) {
				String date = dateArea[dateIndex].toString();// ����
				if (processedDateSet.contains(date))// ��������ٴ���
					continue;
				processedDateSet.add(date);
				String pk_shift = originalExpandedDatePkShiftMap.get(date);// ��Σ���Ϊ��ѭ���Ű࣬��˲�����Ϊ��
				// ����ǹ���
				if (ShiftVO.PK_GX.equals(pk_shift)) {// ֱ�ӵ��ù��ݵ��Ű෽��
					circularArrangeWithHolidayGX(pk_psndocs[i], psndocList, beginDate, endDate, cloneDatePkShiftMap,
							originalExpandedDatePkShiftMap, beforeExgPkShiftMap, psnExistsCalendarMap == null ? null
									: psnExistsCalendarMap.get(pk_psndocs[i]), shiftMap,
							psnEnjoyHolidayMap == null ? null : psnEnjoyHolidayMap.get(pk_psndocs[i]), holidayCutMap,
							processedDateSet, holidayInfo, defaultShift, date, timeZone);
					continue;
				}
				// ������ǹ��ݣ�ֱ�ӵ��÷ǹ��ݵ��Ű෽��
				circularArrangeWithHolidayNonGX(pk_psndocs[i], psndocList, calendarQueryBeginDate,
						calendarQueryEndDate, cloneDatePkShiftMap, originalExpandedDatePkShiftMap, beforeExgPkShiftMap,
						psnExistsCalendarMap == null ? null : psnExistsCalendarMap.get(pk_psndocs[i]), shiftMap,
						psnEnjoyHolidayMap == null ? null : psnEnjoyHolidayMap.get(pk_psndocs[i]), holidayCutMap,
						processedDateSet, holidayInfo, defaultShift, date, timeZone);
			}
			// �ݴ���cloneDatePkShiftMap���治Ӧ�ô洢���ڵ�����Ч���죬�����Ҫ�ڴ˴�ȥ��
			String[] dates = cloneDatePkShiftMap.keySet().toArray(new String[0]);
			if (!ArrayUtils.isEmpty(dates))
				for (String date : dates) {
					if (!TBMPsndocVO.isIntersect(psndocList, date))
						cloneDatePkShiftMap.remove(date);
				}
		}
		// ���ˣ����еİ඼���꣬��ص��Ű���Ϣ�����ˣ�
		// 1.modifiedCalendarMap���û�Ҫ���õİ�ε�map��
		// 2.holidayCutMap������Ű�Ĺ�������Ϊ���յ���psncalendarvoҪ���⴦��������map
		// ����У�飺
		new CalendarShiftMutexChecker().checkCalendar(pk_org, modifiedCalendarMap, overrideExistCalendar, true, false);
		// У��ͨ�����־û������ݿ�
		circularArrangeWithHolidayPersistence(pk_group, pk_org, shiftMap, pk_psndocs, beginDate, endDate,
				originalExpandedDatePkShiftMap, modifiedCalendarMap, psnBeforeExgPkShiftMap, holidayCutMap,
				psnExistsCalendarMap, overrideExistCalendar);
	}

	protected PsnJobCalendarVO[] circularArrangeWithHolidayForTeam(String pk_group, String pk_org, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate, String[] calendarPks, boolean overrideExistCalendar,
			HolidayInfo<HRHolidayVO> holidayInfo, String pk_team) throws BusinessException {
		// �洢Ҫ���õİ�ε�map��key����Ա������value��key��date��value�ǰ������.���ǵ��п��ܲ�����εĶԵ����������modifiedCalendarMap������ڷ�Χ���ܻᳬ��begindate��enddate�ķ�Χ
		Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String, String>>();
		// ��ѭ���İ�ΰ����ڷ�Χȫ��չ����key�����ڣ�value�ǰ������
		Map<String, String> originalExpandedDatePkShiftMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate,
				endDate, calendarPks);
		UFLiteralDate[] dateArea = CommonUtils.createDateArray(beginDate, endDate);
		String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		// Ĭ�ϰ�
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// ҵ��Ԫ�����а��
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		// ���յ��������,key����Ա������value��key�Ǽ���������value���Ƿ�����
		Map<String, Map<String, Boolean>> psnEnjoyHolidayMap = HRHolidayServiceFacade.queryHolidayEnjoyInfo2(pk_org,
				pk_psndocs, holidayInfo.getHolidayVOs());
		// ������и�İ�ε�map��key����Ա��value��key�����ڣ�value��psncalendar�ľۺ�vo.ֻ�м��ն��Ű������Ӱ�죬�����и��ˣ����ߵ��µ��԰�̻��ˣ��Ŵ浽��map��ȥ���������죬���߱���ȫ�и�ɹ��ݵĲ����
		Map<String, Map<String, AggPsnCalendar>> holidayCutMap = new HashMap<String, Map<String, AggPsnCalendar>>();
		// ��¼�Ե�ǰ��ε�map��key����Ա��value��key�����ڣ�value�ǶԵ�ǰ�İ��
		Map<String, Map<String, String>> psnBeforeExgPkShiftMap = new HashMap<String, Map<String, String>>();
		// ҵ��Ԫ��ʱ��
		TimeZone timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		// ��Ա�Ѿ��źõİ��,��Χ��min(beginDate,�Ե���,����).getDateBefore(1)��max(endDate,�Ե��գ�����).getDateAfter(1)
		Set<String> holidayDateSet = new HashSet<String>();// �洢���ա��Ե��յ�map��key�����ڣ���������������ȷ����ѯ���������ķ�Χ
		holidayDateSet.addAll(holidayInfo.getHolidayMap().keySet());
		holidayDateSet.addAll(holidayInfo.getSwitchMap().keySet());
		String[] allDates = holidayDateSet.toArray(new String[0]);// �Լ��ա��Ե�������
		Arrays.sort(allDates);
		// ��ѯ����������������ڷ�Χ
		UFLiteralDate calendarQueryBeginDate = allDates[0].compareTo(beginDate.toString()) < 0 ? UFLiteralDate.getDate(
				allDates[0]).getDateBefore(1) : beginDate.getDateBefore(1);
		UFLiteralDate calendarQueryEndDate = allDates[allDates.length - 1].compareTo(endDate.toString()) > 0 ? UFLiteralDate
				.getDate(allDates[allDates.length - 1]).getDateAfter(1) : endDate.getDateAfter(1);
		Map<String, Map<String, PsnCalendarVO>> psnExistsCalendarMap = new PsnCalendarDAO()
				.queryCalendarVOMapByPsndocs(pk_hrorg, pk_psndocs, calendarQueryBeginDate, calendarQueryEndDate);
		// ҵ��Ԫ�ڿ��ڵ���Map��key����Ա����,��map��Ҫ��������Ա���ڵ�����Ч���ڷ�Χ
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_org, pk_psndocs, calendarQueryBeginDate, calendarQueryEndDate, true);
		// ����ѭ������
		if (psndocMap == null) {
			return null;
		}
		for (int i = 0; i < pk_psndocs.length; i++) {
			Map<String, String> cloneDatePkShiftMap = new HashMap<String, String>(originalExpandedDatePkShiftMap);// ÿ���˵�������һ�ݣ���Ϊ��Ȼ��ѭ���Ű࣬��������Ű���������ÿ���˶���ͬ
			Map<String, String> beforeExgPkShiftMap = new HashMap<String, String>();
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndocs[i]);
			modifiedCalendarMap.put(pk_psndocs[i], cloneDatePkShiftMap);
			psnBeforeExgPkShiftMap.put(pk_psndocs[i], beforeExgPkShiftMap);
			// �Լ����������(��Ϊ�漰���Ե���������Щ���������ǰ��ʹ������)
			Set<String> processedDateSet = new HashSet<String>();
			// ѭ�������û����õ�ѭ�����
			for (int dateIndex = 0; dateIndex < dateArea.length; dateIndex++) {
				String date = dateArea[dateIndex].toString();// ����
				if (processedDateSet.contains(date))// ��������ٴ���
					continue;
				processedDateSet.add(date);
				String pk_shift = originalExpandedDatePkShiftMap.get(date);// ��Σ���Ϊ��ѭ���Ű࣬��˲�����Ϊ��
				// ����ǹ���
				if (ShiftVO.PK_GX.equals(pk_shift)) {// ֱ�ӵ��ù��ݵ��Ű෽��
					circularArrangeWithHolidayGX(pk_psndocs[i], psndocList, beginDate, endDate, cloneDatePkShiftMap,
							originalExpandedDatePkShiftMap, beforeExgPkShiftMap, psnExistsCalendarMap == null ? null
									: psnExistsCalendarMap.get(pk_psndocs[i]), shiftMap,
							psnEnjoyHolidayMap.get(pk_psndocs[i]), holidayCutMap, processedDateSet, holidayInfo,
							defaultShift, date, timeZone);
					continue;
				}
				// ������ǹ��ݣ�ֱ�ӵ��÷ǹ��ݵ��Ű෽��
				circularArrangeWithHolidayNonGX(pk_psndocs[i], psndocList, calendarQueryBeginDate,
						calendarQueryEndDate, cloneDatePkShiftMap, originalExpandedDatePkShiftMap, beforeExgPkShiftMap,
						psnExistsCalendarMap == null ? null : psnExistsCalendarMap.get(pk_psndocs[i]), shiftMap,
						psnEnjoyHolidayMap.get(pk_psndocs[i]), holidayCutMap, processedDateSet, holidayInfo,
						defaultShift, date, timeZone);
			}
			// �ݴ���cloneDatePkShiftMap���治Ӧ�ô洢���ڵ�����Ч���죬�����Ҫ�ڴ˴�ȥ��
			String[] dates = cloneDatePkShiftMap.keySet().toArray(new String[0]);
			if (!ArrayUtils.isEmpty(dates))
				for (String date : dates) {
					if (!TBMPsndocVO.isIntersect(psndocList, date))
						cloneDatePkShiftMap.remove(date);
				}
		}
		// ���ˣ����еİ඼���꣬��ص��Ű���Ϣ�����ˣ�
		// 1.modifiedCalendarMap���û�Ҫ���õİ�ε�map��
		// 2.holidayCutMap������Ű�Ĺ�������Ϊ���յ���psncalendarvoҪ���⴦��������map
		// ����У�飺
		new CalendarShiftMutexChecker().checkCalendar(pk_org, modifiedCalendarMap, overrideExistCalendar, true, false);
		// У��ͨ�����־û������ݿ�
		return circularArrangeWithHolidayPersistenceForTeam(pk_group, pk_org, shiftMap, pk_psndocs, beginDate, endDate,
				originalExpandedDatePkShiftMap, modifiedCalendarMap, psnBeforeExgPkShiftMap, holidayCutMap,
				psnExistsCalendarMap, overrideExistCalendar, pk_team);
	}

	/**
	 * ��ѭ���Ű�Ľ���־û������ݿ⣬ѭ���Ű��ʱ���Ǽ����� Ӧ�ý�ѭ���Ű�Ľ�����Ѿ������ݿ��д��ڵ�����ɾ������insertѭ���Ű�Ľ��
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
			Map<String, AggShiftVO> shiftMap, // ���еİ��
			String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate,
			Map<String, String> originalExpandedDatePkShiftMap, // �û��ŵ�ԭʼ�İ��map��key�����ڣ�value�ǰ���������Ѿ��������ڷ�Χչ��
			Map<String, Map<String, String>> modifiedCalendarMap, // �����û����������ɵ��Ű����ݣ�key��pk_psndoc,value��key��date��value�ǰ������
			Map<String, Map<String, String>> psnBeforeExgPkShiftMap, // ��¼�Ե�ǰ��ε�map��key����Ա������value��key��date��value�ǰ������
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, // ������ղ��������õ��²�����Ĭ�Ϲ�������psncalendarvo����Ҫ�����map�����psncalendarvo
			Map<String, Map<String, PsnCalendarVO>> psnExistsCalendarMap, // ���ݿ������Ѿ��źõİ��
			boolean isOverrideExistsCalendar) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs))
			return;
		List<String> toDelPsnCalendarPk = new ArrayList<String>();// ��Ҫ�����ݿ���ɾ���Ĺ�������
		List<AggPsnCalendar> toInsertPsnCalendarVOList = new ArrayList<AggPsnCalendar>();// ��Ҫinsert�Ĺ�������
		// ���˴���
		for (String pk_psndoc : pk_psndocs) {
			Map<String, String> mdfdCalendarMap = modifiedCalendarMap.get(pk_psndoc);// ���˵��Ű���
			Map<String, PsnCalendarVO> existsCalendarMap = psnExistsCalendarMap == null ? null : psnExistsCalendarMap
					.get(pk_psndoc);
			Map<String, String> beforeExgPkShiftMap = psnBeforeExgPkShiftMap.get(pk_psndoc);
			Map<String, AggPsnCalendar> hldCutMap = holidayCutMap == null ? null : holidayCutMap.get(pk_psndoc);
			if (MapUtils.isEmpty(mdfdCalendarMap))
				continue;
			// ѭ������ÿһ��
			String[] dates = mdfdCalendarMap.keySet().toArray(new String[0]);
			for (String date : dates) {
				if (existsCalendarMap != null && existsCalendarMap.get(date) != null) {// �����һ���Ѿ����Ű࣬��Ҫ���û���ѡ�񸲸ǻ��ǲ�����
					if (!isOverrideExistsCalendar) {// ��������ǣ���Ҫ��mdfdCalendarMap��remove����һ��İ�
						mdfdCalendarMap.remove(date);
						continue;
					}
					// ������ǣ�����Ҫ�����ݿ���ɾ������һ����Ű�
					PsnCalendarVO existsCalendar = existsCalendarMap.get(date);
					toDelPsnCalendarPk.add(existsCalendar.getPk_psncalendar());
				}
				// ���湹���������ݿ��vo
				AggPsnCalendar aggVO = hldCutMap == null ? null : hldCutMap.get(date);
				if (aggVO != null) {
					PsnCalendarUtils.setGroupOrgPk2AggVO(pk_group, pk_org, aggVO, true);
					toInsertPsnCalendarVOList.add(aggVO);
					continue;
				}
				// �����ߵ�������������������ӱ�϶���û���ݵ�
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
				// ҵ����־
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
	 * �־û������ݿ�֮ǰ,��Ҫ����ĳ�������������
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
		// ����������map��key����Ա������value��key�����ڣ�value�ǹ��������ͣ���holidayvo��
		// <pk_psndoc,<date,����������>>
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
				// �߳��л�����Ϣ�ᶪʧ������������һ��
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				TaBusilogUtil.writeCircularlyArrangePsnCalendarBusiLog(pk_org,
						toInsertPsnCalendarVOList.toArray(new AggPsnCalendar[0]));
			}
		}).start();
	}

	/**
	 * ��ѭ���Ű�Ľ���־û������ݿ⣬ѭ���Ű��ʱ���Ǽ����� Ӧ�ý�ѭ���Ű�Ľ�����Ѿ������ݿ��д��ڵ�����ɾ������insertѭ���Ű�Ľ��
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
			Map<String, AggShiftVO> shiftMap, // ���еİ��
			String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate,
			Map<String, String> originalExpandedDatePkShiftMap, // �û��ŵ�ԭʼ�İ��map��key�����ڣ�value�ǰ���������Ѿ��������ڷ�Χչ��
			Map<String, Map<String, String>> modifiedCalendarMap, // �����û����������ɵ��Ű����ݣ�key��pk_psndoc,value��key��date��value�ǰ������
			Map<String, Map<String, String>> psnBeforeExgPkShiftMap, // ��¼�Ե�ǰ��ε�map��key����Ա������value��key��date��value�ǰ������
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, // ������ղ��������õ��²�����Ĭ�Ϲ�������psncalendarvo����Ҫ�����map�����psncalendarvo
			Map<String, Map<String, PsnCalendarVO>> psnExistsCalendarMap, // ���ݿ������Ѿ��źõİ��
			boolean isOverrideExistsCalendar, String pk_team) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs))
			return null;
		List<String> toDelPsnCalendarPk = new ArrayList<String>();// ��Ҫ�����ݿ���ɾ���Ĺ�������
		List<AggPsnCalendar> toInsertPsnCalendarVOList = new ArrayList<AggPsnCalendar>();// ��Ҫinsert�Ĺ�������
		// ���˴���

		// ���ڷ�Χ����Щ�˵Ŀ��ڵ������������
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_org, pk_psndocs, beginDate, endDate, true);
		if (psndocMap == null) {
			return null;
		}
		for (String pk_psndoc : pk_psndocs) {
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);
			Map<String, String> mdfdCalendarMap = modifiedCalendarMap.get(pk_psndoc);// ���˵��Ű���
			Map<String, PsnCalendarVO> existsCalendarMap = psnExistsCalendarMap == null ? null : psnExistsCalendarMap
					.get(pk_psndoc);
			Map<String, String> beforeExgPkShiftMap = psnBeforeExgPkShiftMap.get(pk_psndoc);
			Map<String, AggPsnCalendar> hldCutMap = holidayCutMap == null ? null : holidayCutMap.get(pk_psndoc);
			if (MapUtils.isEmpty(mdfdCalendarMap))
				continue;
			// ѭ������ÿһ��
			String[] dates = mdfdCalendarMap.keySet().toArray(new String[0]);
			for (String date : dates) {
				// ������ڵ�����pk_team �����ڴ�������Pk_team��insert
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
				if (existsCalendarMap != null && existsCalendarMap.get(date) != null) {// �����һ���Ѿ����Ű࣬��Ҫ���û���ѡ�񸲸ǻ��ǲ�����
					if (!isOverrideExistsCalendar) {// ��������ǣ���Ҫ��mdfdCalendarMap��remove����һ��İ�
						mdfdCalendarMap.remove(date);
						continue;
					}
					// ������ǣ�����Ҫ�����ݿ���ɾ������һ����Ű�
					PsnCalendarVO existsCalendar = existsCalendarMap.get(date);
					toDelPsnCalendarPk.add(existsCalendar.getPk_psncalendar());
				}
				// ���湹���������ݿ��vo
				AggPsnCalendar aggVO = hldCutMap == null ? null : hldCutMap.get(date);
				if (aggVO != null) {
					PsnCalendarUtils.setGroupOrgPk2AggVO(pk_group, pk_org, aggVO, true);
					toInsertPsnCalendarVOList.add(aggVO);
					continue;
				}
				// �����ߵ�������������������ӱ�϶���û���ݵ�
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
	 * ���Ǽ��յ�ѭ���Ű࣬����İ���û���Ϊ�˷ǹ���
	 * 
	 * @throws BusinessException
	 */
	private void circularArrangeWithHolidayNonGX(String pk_psndoc,
			List<TBMPsndocVO> psndocList, // ���ڵ�����list
			UFLiteralDate beginDate, UFLiteralDate endDate, Map<String, String> cloneDatePkShiftMap,
			Map<String, String> originalExpandedDatePkShiftMap, // �û��ŵ�ԭʼ�İ��map��key�����ڣ�value�ǰ���������Ѿ��������ڷ�Χչ��
			Map<String, String> beforeExgPkShiftMap, // ��������˶Ե�����map��¼�Ե�ǰ�İ�Σ�key��date
			Map<String, PsnCalendarVO> existsCalendarMap, // ���ݿ����Ѿ��ŵĹ�������
			Map<String, AggShiftVO> shiftMap, // ҵ��Ԫ�����еİ��
			Map<String, Boolean> enjoyHolidayMap, // ��Ա���м��յ������key�Ǽ�������
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, Set<String> processedDateSet, // �Ѿ������������set
			HolidayInfo<HRHolidayVO> holidayInfo, AggShiftVO defaultAggShiftVO, String date, TimeZone timeZone)
			throws BusinessException {

		String pk_shift = originalExpandedDatePkShiftMap.get(date);
		// ������첻�ǶԵ��գ������ǶԵ��յ����˲����ܼ��գ����û������Ű࣬�������⴦��
		if (!holidayInfo.getSwitchMap().containsKey(date)) {
			processHolidayCut(pk_psndoc, cloneDatePkShiftMap,
					ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, pk_shift), holidayInfo.getHolidayVOs(),
					enjoyHolidayMap, holidayCutMap, date, timeZone);
			return;
		}
		HolidayVO holidayVO = holidayInfo.getSwitchHolidayMap().get(date);
		if (!holidayVO.isAllEnjoy() && !enjoyHolidayMap.get(holidayVO.getPk_holiday())) {// ��ǰ��Ա�����ܴ˼���
			processHolidayCut(pk_psndoc, cloneDatePkShiftMap,
					ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, pk_shift), holidayInfo.getHolidayVOs(),
					enjoyHolidayMap, holidayCutMap, date, timeZone);
			return;
		}
		// �����ߵ��������϶��ǶԵ���

		String switchDate = holidayInfo.getSwitchMap().get(date);// �Ե���
		processedDateSet.add(switchDate);// �Ե��մ��ϴ����־
		// ���Ե����Ƿ������ڷ�Χ��,����ڷ�Χ�ڣ���ô�Ե��ǿ϶�Ҫ�����ģ�
		if (switchDate.compareTo(beginDate.toString()) >= 0 && switchDate.compareTo(endDate.toString()) <= 0) {
			exchangeDateInDateAreaNonGX(pk_psndoc, cloneDatePkShiftMap, pk_shift,
					originalExpandedDatePkShiftMap.get(switchDate), beforeExgPkShiftMap, shiftMap, enjoyHolidayMap,
					holidayCutMap, holidayInfo, date, switchDate, timeZone);
			return;
		}
		// ����Ե����ڴ˴����ð�ε����ڷ�Χ֮�⣬����Ҫ���Ե����ڵ��Ű�״�������
		// 1.���Ű࣬���Ű�Ϊ������ȡ�����������Ե����߼�һ��
		// 2.���Ű࣬���Ű�Ϊ������վɣ��򲻶Ե�
		// 3.���Ű࣬���Զ��Ե�������һ��Ĭ�ϰࣨ��һ�����壩�����߹��ݣ������գ���Ȼ��ִ�жԵ����Ե��Ľ�������춼��������ȡ��
		// ע�⣺����Ե������ڿ��ڵ�����Ч�����ڣ����ͬ�����3
		// 1,3���Ժϲ�����3���Կ���1��һ������
		// ��������ŵİ����ȡ���Ե��յİ�
		PsnCalendarVO switchCalendar = existsCalendarMap == null ? null : existsCalendarMap.get(switchDate);
		exchangeDateOutDateAreaNonGX(pk_psndoc, cloneDatePkShiftMap, pk_shift, switchCalendar, beforeExgPkShiftMap,
				psndocList, shiftMap, enjoyHolidayMap, holidayCutMap, holidayInfo, defaultAggShiftVO, date, switchDate,
				timeZone);
	}

	/**
	 * ����Ե����Ե�����ѭ���Ű�����ڷ�Χ֮�⣬���û����������˷ǹ���
	 * ��exchangeDateInDateAreaNonGX������һ�ԣ�һ������Ե����ڷ�Χ�ڣ�һ������Ե����ڷ�Χ��
	 * 
	 * @throws BusinessException
	 */
	private void exchangeDateOutDateAreaNonGX(String pk_psndoc, Map<String, String> cloneDatePkShiftMap,
			String originalPkShift, PsnCalendarVO switchCalendar,
			Map<String, String> beforeExgPkShiftMap, // ��������˶Ե�����map��¼�Ե�ǰ�İ�Σ�key��date
			List<TBMPsndocVO> psndocList, Map<String, AggShiftVO> shiftMap,
			Map<String, Boolean> enjoyHolidayMap, // ��Ա���м��յ������key�Ǽ�������
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, // ���PsnCalendarVO��Ҫ���⴦���򽫴������������map
			HolidayInfo<HRHolidayVO> holidayInfo, AggShiftVO defaultAggShiftVO, String date, String switchDate,
			TimeZone timeZone) throws BusinessException {
		// ����Ե����Ű��ˣ��ҶԵ���������Ч�Ŀ��ڵ������ҶԵ����������վɣ�����������Ե�
		if (switchCalendar != null && TBMPsndocVO.isIntersect(psndocList, switchDate)
				&& !switchCalendar.isHolidayCancel()) {
			processHolidayCut(pk_psndoc, cloneDatePkShiftMap,
					ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, originalPkShift), holidayInfo.getHolidayVOs(),
					enjoyHolidayMap, holidayCutMap, date, timeZone);
			return;
		}
		beforeExgPkShiftMap.put(date, originalPkShift);// ���նԵ�ǰ�İ��
		// ���û�Ű�������ڲ�������Ч�Ŀ��ڵ������ڻ��߶Ե���������ȡ��
		// �Ե��յİ�϶��ŵ��յİ�,��������Ե�
		processHolidayCut(pk_psndoc, cloneDatePkShiftMap,
				ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, originalPkShift), holidayInfo.getHolidayVOs(),
				enjoyHolidayMap, holidayCutMap, switchDate, timeZone);
		String switchPkShift = null;// �Ե��յİ��
		if (switchCalendar == null || !TBMPsndocVO.isIntersect(psndocList, switchDate)) {
			// ����Ե�������һ�����壬��Ե���ο�����ΪĬ�ϰ࣬������Ϊ����(Ĭ�ϰ�Ϊ�յĻ���Ҳ��Ϊ����)
			switchPkShift = TACalendarUtils.getPkShiftByDate(switchDate, defaultAggShiftVO);
		} else {// �Ե����Ű��ˣ���������Ч����������ȡ��
			switchPkShift = switchCalendar.getPk_shift();
		}
		beforeExgPkShiftMap.put(switchDate, switchPkShift);// �Ե��նԵ�ǰ�İ��
		if (ShiftVO.PK_GX.equals(switchPkShift)) {
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);// �Ե��յİ���ǹ��ݣ���ô����Ҳ�϶��ǹ�����
			return;
		}
		// �Ե��հ�ηǹ��ݣ���Ҫ���Ե��յİ�εĹ���ʱ��Σ����ڵ��԰࣬�������ʱ��Σ�������Ƿ��н��������ڵ��԰࣬�����ʱ���������н����ᵼ�¹̻�������һ���ᵼ�¹���ʱ��α��и
		AggShiftVO switchShiftAggVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, switchPkShift);
		processHolidayCut(pk_psndoc, cloneDatePkShiftMap, switchShiftAggVO, holidayInfo.getHolidayVOs(),
				enjoyHolidayMap, holidayCutMap, switchDate, timeZone);
	}

	/**
	 * ����Ե����Ե�����ѭ���Ű�����ڷ�Χ�ڣ����û����������˷ǹ���
	 * ��exchangeDateOutDateAreaNonGX������һ�ԣ�һ������Ե����ڷ�Χ�ڣ�һ������Ե����ڷ�Χ��
	 * 
	 * @throws BusinessException
	 */
	private void exchangeDateInDateAreaNonGX(String pk_psndoc,
			Map<String, String> cloneDatePkShiftMap,
			String originalPkShift, // �����û��ŵ�ԭʼ�İ��
			String originalSwitchDayPkShift, // �Ե����û��ŵ�ԭʼ�İ��
			Map<String, String> beforeExgPkShiftMap, // ��������˶Ե�����map��¼�Ե�ǰ�İ�Σ�key��date
			Map<String, AggShiftVO> shiftMap,
			Map<String, Boolean> enjoyHolidayMap, // ��Ա���м��յ������key�Ǽ�������
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, HolidayInfo<HRHolidayVO> holidayInfo, String date,
			String switchDate, TimeZone timeZone) throws BusinessException {
		beforeExgPkShiftMap.put(date, originalPkShift);// ��¼���նԵ�ǰ�İ��
		beforeExgPkShiftMap.put(switchDate, originalSwitchDayPkShift);// ��¼�Ե��նԵ�ǰ�İ��
		// ����Ե��յİ���ǹ��ݣ���ô����Ҳ�Ź��ݣ�
		if (ShiftVO.PK_GX.equals(originalSwitchDayPkShift)) {
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);
		} else {// �Ե��ղ��ǹ��ݣ���ô���Ե��յİ�ε������գ�Ȼ�����հ������յĽ�������

			processHolidayCut(pk_psndoc, cloneDatePkShiftMap,
					ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, originalSwitchDayPkShift),
					holidayInfo.getHolidayVOs(), enjoyHolidayMap, holidayCutMap, date, timeZone);
		}
		// �Ե��յİ��ҲҪ����
		processHolidayCut(pk_psndoc, cloneDatePkShiftMap,
				ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, originalPkShift), holidayInfo.getHolidayVOs(),
				enjoyHolidayMap, holidayCutMap, switchDate, timeZone);
		return;
	}

	/**
	 * ���Ǽ��յ�ѭ���Ű࣬����İ���û���Ϊ�˹���
	 * 
	 * @throws BusinessException
	 */
	private void circularArrangeWithHolidayGX(String pk_psndoc, List<TBMPsndocVO> psndocList, // ���ڵ�����list.��������ȡֵ������ֵ
			UFLiteralDate beginDate, UFLiteralDate endDate, Map<String, String> cloneDatePkShiftMap, // ���������ֵ
			Map<String, String> originalExpandedDatePkShiftMap, // �û��ŵ�ԭʼ�İ��map��key�����ڣ�value�ǰ���������Ѿ��������ڷ�Χչ������������ȡֵ
			Map<String, String> beforeExgPkShiftMap, // ��������˶Ե�����map��¼�Ե�ǰ�İ�Σ�key��date�����������ֵ
			Map<String, PsnCalendarVO> existsCalendarMap, // ���ݿ����Ѿ��ŵĹ�����������������ȡֵ
			Map<String, AggShiftVO> shiftMap, // ��֯�����еİ�Σ���������ȡֵ
			Map<String, Boolean> enjoyHolidayMap, // ��Ա���м��յ������key�Ǽ�������������ȡֵ
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, // ���������ֵ
			Set<String> processedDateSet, // �Ѿ������������set������ȡֵ��Ҳ��ֵ
			HolidayInfo<HRHolidayVO> holidayInfo, AggShiftVO defaultShift, String date, TimeZone timeZone)
			throws BusinessException {

		// ������첻�漰���Ե��������漰�Ե������˲����ܴ˼��գ�������Ȼ�ǹ���,����Ӱ��
		if (!holidayInfo.getSwitchMap().containsKey(date))// ���첻�漰�Ե�
			return;
		HolidayVO holidayVO = holidayInfo.getSwitchHolidayMap().get(date);
		if (!holidayVO.isAllEnjoy() && !enjoyHolidayMap.get(holidayVO.getPk_holiday()))// ��ǰ��Ա�����ܴ˼���
			return;
		// �ߵ����date�϶��漰�Ե�������Ա���ܴ˼���
		// ����漰���Ե�����Ҫ���Ե��յİ�Σ�
		String switchDate = holidayInfo.getSwitchMap().get(date);
		processedDateSet.add(switchDate);
		// ����Ե����ڴ˴����ð�ε����ڷ�Χ�ڣ��򣺵����ŶԵ��յİࣨ����ǹ�������������У���Ҫ��ȥ���գ����Ե����Ź���
		if (switchDate.compareTo(beginDate.toString()) >= 0 && switchDate.compareTo(endDate.toString()) <= 0) {
			exchangeDateInDateAreaGX(pk_psndoc, cloneDatePkShiftMap, originalExpandedDatePkShiftMap.get(switchDate),
					beforeExgPkShiftMap, shiftMap, enjoyHolidayMap, holidayCutMap, holidayInfo, date, switchDate,
					timeZone);
			return;
		}
		// ����Ե����ڴ˴����ð�ε����ڷ�Χ֮�⣬����Ҫ���Ե����ڵ��Ű�״�������
		// 1.���Ű࣬���Ű�Ϊ������ȡ�����������Ե����߼�һ��
		// 2.���Ű࣬���Ű�Ϊ������վɣ��򲻶Ե�
		// 3.���Ű࣬���Զ��Ե�������һ��Ĭ�ϰࣨ��һ�����壩�����߹��ݣ������գ���Ȼ��ִ�жԵ����Ե��Ľ�������춼��������ȡ��
		// ע�⣺����Ե������ڿ��ڵ�����Ч�����ڣ����ͬ�����3
		// 1,3���Ժϲ�����3���Կ���1��һ������
		// ��������ŵİ����ȡ���Ե��յİ�
		PsnCalendarVO switchCalendar = existsCalendarMap == null ? null : existsCalendarMap.get(switchDate);
		exchangeDateOutDateAreaGX(pk_psndoc, cloneDatePkShiftMap, switchCalendar, beforeExgPkShiftMap, psndocList,
				shiftMap, enjoyHolidayMap, holidayCutMap, holidayInfo, defaultShift, date, switchDate, timeZone);
	}

	/**
	 * ����һ�������ĳ��ļ����и����
	 * �п��ܰ���������ȫ������ʱ��psncalendarvo�������⴦���п���psncalendarvo����Ҫ���⴦����������и��˹����Σ�
	 * ��������յ��µ��԰�̻��� ���psncalendarvo�������⴦���򷵻�null�����򷵻�AggPsnCalendar
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
		// �ҳ��빤��ʱ���н����ļ���
		HolidayVO[] crossedHolidayVOs = TACalendarUtils.findCrossedHolidayVOs(aggShiftVO, holidayVOs, date, timeZone,
				enjoyHolidayMap);
		// ��������޽���������������
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
	 * ����һ�������ĳ��ļ����и�������˷�������ʱ��Ӧ�ñ�֤����ʱ���������н���
	 */
	private void processHolidayCut2(String pk_psndoc, Map<String, String> cloneDatePkShiftMap, AggShiftVO shiftVO,
			HolidayVO[] holidayVOs, Map<String, Boolean> enjoyHolidayMap, Map<String, AggPsnCalendar> cutMap,
			String date, TimeZone timeZone) {
		AggPsnCalendar cutCalendar = createHolidayCutAggPsnCalendarVO(pk_psndoc, cloneDatePkShiftMap, shiftVO,
				holidayVOs, enjoyHolidayMap, date, timeZone);
		cutMap.put(date, cutCalendar);
	}

	/**
	 * ����һ�������ĳ��ļ����и�������˷�������ʱ��Ӧ�ñ�֤����ʱ���������н���
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
	 * ����Ե����Ե�����ѭ���Ű�����ڷ�Χ�ڣ����û����������˹���
	 * ��exchangeDateOutDateAreaGX������һ�ԣ�һ������Ե����ڷ�Χ�ڣ�һ������Ե����ڷ�Χ��
	 * 
	 * @throws BusinessException
	 */
	private void exchangeDateInDateAreaGX(String pk_psndoc,
			Map<String, String> cloneDatePkShiftMap,
			String originalSwitchDayPkShift, // �Ե����û��ŵ�ԭʼ�İ��
			Map<String, String> beforeExgPkShiftMap, // ��������˶Ե�����map��¼�Ե�ǰ�İ�Σ�key��date
			Map<String, AggShiftVO> shiftMap,
			Map<String, Boolean> enjoyHolidayMap, // ��Ա���м��յ������key�Ǽ�������
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, HolidayInfo<HRHolidayVO> holidayInfo, String date,
			String switchDate, TimeZone timeZone) throws BusinessException {
		beforeExgPkShiftMap.put(date, ShiftVO.PK_GX);// ��¼���նԵ�ǰ�İ��
		beforeExgPkShiftMap.put(switchDate, originalSwitchDayPkShift);// ��¼�Ե��նԵ�ǰ�İ��
		cloneDatePkShiftMap.put(switchDate, ShiftVO.PK_GX);// �Ե��տ϶��ǹ�����
		if (ShiftVO.PK_GX.equals(originalSwitchDayPkShift)) {
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);// �Ե��յİ���ǹ��ݣ���ô����Ҳ�϶��ǹ�����
			return;
		}
		// �Ե��հ�ηǹ��ݣ���Ҫ���Ե��յİ�εĹ���ʱ��Σ����ڵ��԰࣬�������ʱ��Σ�������Ƿ��н��������ڵ��԰࣬�����ʱ���������н����ᵼ�¹̻�������һ���ᵼ�¹���ʱ��α��и
		AggShiftVO switchShiftAggVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, originalSwitchDayPkShift);
		processHolidayCut(pk_psndoc, cloneDatePkShiftMap, switchShiftAggVO, holidayInfo.getHolidayVOs(),
				enjoyHolidayMap, holidayCutMap, date, timeZone);
	}

	/**
	 * ����Ե����Ե�����ѭ���Ű�����ڷ�Χ֮�⣬���û����������˹���
	 * ��exchangeDateInDateAreaGX������һ�ԣ�һ������Ե����ڷ�Χ�ڣ�һ������Ե����ڷ�Χ��
	 * 
	 * @throws BusinessException
	 */
	private void exchangeDateOutDateAreaGX(String pk_psndoc, Map<String, String> cloneDatePkShiftMap,
			PsnCalendarVO switchCalendar,
			Map<String, String> beforeExgPkShiftMap, // ��������˶Ե�����map��¼�Ե�ǰ�İ�Σ�key��date
			List<TBMPsndocVO> psndocList, Map<String, AggShiftVO> shiftMap,
			Map<String, Boolean> enjoyHolidayMap, // ��Ա���м��յ������key�Ǽ�������
			Map<String, Map<String, AggPsnCalendar>> holidayCutMap, // ���PsnCalendarVO��Ҫ���⴦���򽫴������������map
			HolidayInfo<HRHolidayVO> holidayInfo, AggShiftVO defaultAggShiftVO, String date, String switchDate,
			TimeZone timeZone) throws BusinessException {

		// ����Ե����Ű��ˣ��ҶԵ���������Ч�Ŀ��ڵ������ҶԵ����������վɣ�����������Ե�
		if (switchCalendar != null && TBMPsndocVO.isIntersect(psndocList, switchDate)
				&& !switchCalendar.isHolidayCancel()) {
			return;
		}
		// ���û�Ű�������ڲ�������Ч�Ŀ��ڵ������ڻ��߶Ե���������ȡ�������������Ҫ�Ե�
		beforeExgPkShiftMap.put(date, ShiftVO.PK_GX);// ��¼���նԵ�ǰ�İ��
		// �Ե��յİ�϶��ǹ���
		cloneDatePkShiftMap.put(switchDate, ShiftVO.PK_GX);// �Ե��տ϶��ǹ�����
		String switchPkShift = null;
		if (switchCalendar == null || !TBMPsndocVO.isIntersect(psndocList, switchDate)) {
			// ����Ե�������һ�����壬��Ե���ο�����ΪĬ�ϰ࣬������Ϊ����(Ĭ�ϰ�Ϊ�յĻ���Ҳ��Ϊ����)
			switchPkShift = TACalendarUtils.getPkShiftByDate(switchDate, defaultAggShiftVO);
		} else {// �Ե����Ű��ˣ���������Ч����������ȡ��
			switchPkShift = switchCalendar.getPk_shift();
		}
		beforeExgPkShiftMap.put(switchDate, switchPkShift);// ��¼�Ե��նԵ�ǰ�İ��
		if (ShiftVO.PK_GX.equals(switchPkShift)) {
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);// �Ե��յİ���ǹ��ݣ���ô����Ҳ�϶��ǹ�����
			return;
		}
		// �Ե��հ�ηǹ��ݣ���Ҫ���Ե��յİ�εĹ���ʱ��Σ����ڵ��԰࣬�������ʱ��Σ�������Ƿ��н��������ڵ��԰࣬�����ʱ���������н����ᵼ�¹̻�������һ���ᵼ�¹���ʱ��α��и
		AggShiftVO switchShiftAggVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, switchPkShift);
		processHolidayCut(pk_psndoc, cloneDatePkShiftMap, switchShiftAggVO, holidayInfo.getHolidayVOs(),
				enjoyHolidayMap, holidayCutMap, switchDate, timeZone);
	}

	/**
	 * ������ķ�������ִ��
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
		// ҵ��Ԫ�����а��
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
	 * 2011.9.28,��������ҵ��Ԫ���޸���� ѭ���Ű࣬�����Ǽ���
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

		// ��ѭ���İ�ΰ����ڷ�Χȫ��չ��
		Map<String, String> pkMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate, endDate, calendarPks);
		// ���������˵��Ű�map��У�鷽����Ҫ
		Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String, String>>();
		for (String pk_psndoc : pk_psndocs) {
			modifiedCalendarMap.put(pk_psndoc, pkMap);
		}
		new CalendarShiftMutexChecker().checkCalendar(pk_org, modifiedCalendarMap, overrideExistCalendar, true, false);
		// ���ˣ�У��ͨ�������Գ־û������ݿ���
		// ��֯�����а��
		// Map<String, AggBclbDefVO> shiftMap =
		// NCLocator.getInstance().lookup(IBclbQueryService.class).queryBclbAggVOMap(pk_org);
		// Map<String, AggShiftVO> shiftMap =
		// NCLocator.getInstance().lookup(IShiftQueryService.class).queryShiftAggVOMapByOrg(pk_org);
		// ҵ��Ԫ�ڿ��ڵ���Map��key����Ա����,��map��Ҫ��������Ա���ڵ�����Ч���ڷ�Χ
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_org, pk_psndocs, beginDate, endDate, true);
		if (MapUtils.isEmpty(psndocMap))
			return;
		// String pk_hrorg =
		// NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		// ���й�������
		Map<String, Map<String, String>> existsPsnCalendarMap = new PsnCalendarDAO().queryPkShiftMapByPsndocs(pk_hrorg,
				pk_psndocs, beginDate, endDate);
		List<AggPsnCalendar> insertList = new ArrayList<AggPsnCalendar>();
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		for (String pk_psndoc : pk_psndocs) {
			List<TBMPsndocVO> psndocList = psndocMap.get(pk_psndoc);
			for (UFLiteralDate date : allDates) {
				if (!TBMPsndocVO.isIntersect(psndocList, date.toString()))// ������ղ��ڿ��ڵ�����Χ�ڣ���insert
					continue;
				// ����û�ѡ�񲻸��ǣ��ҵ������й�����������Ҳ��insert
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
		if (overrideExistCalendar)// ����û�ѡ�񸲸������Ű࣬����Ҫ�����ڷ�Χ�ڵĹ�������ɾ����
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

		// ��ѭ���İ�ΰ����ڷ�Χȫ��չ��
		Map<String, String> pkMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate, endDate, calendarPks);
		// ���������˵��Ű�map��У�鷽����Ҫ
		Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String, String>>();
		for (String pk_psndoc : pk_psndocs) {
			modifiedCalendarMap.put(pk_psndoc, pkMap);
		}
		new CalendarShiftMutexChecker().checkCalendar(pk_org, modifiedCalendarMap, overrideExistCalendar, true, false);
		// ���ˣ�У��ͨ�������Գ־û������ݿ���
		// ��֯�����а��
		// Map<String, AggBclbDefVO> shiftMap =
		// NCLocator.getInstance().lookup(IBclbQueryService.class).queryBclbAggVOMap(pk_org);
		Map<String, AggShiftVO> shiftMap = NCLocator.getInstance().lookup(IShiftQueryService.class)
				.queryShiftAggVOMapByOrg(pk_org);
		// ҵ��Ԫ�ڿ��ڵ���Map��key����Ա����,��map��Ҫ��������Ա���ڵ�����Ч���ڷ�Χ
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocsBU(pk_org, pk_psndocs, beginDate, endDate, true);
		if (MapUtils.isEmpty(psndocMap))
			return null;
		String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		// ���й�������
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
					if (!TBMPsndocVO.isIntersect(psndocList, date.toString()))// ������ղ��ڿ��ڵ�����Χ�ڣ���insert
						continue;
					// ������ڵ�����pk_team �����ڴ�������Pk_team��insert
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
					// ����û�ѡ�񲻸��ǣ��ҵ������й�����������Ҳ��insert
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
			// ��ѯ���������������,set��Ա����������������
			if (overrideExistCalendar) {// ����û�ѡ�񸲸������Ű࣬����Ҫ�����ڷ�Χ�ڵĹ�������ɾ����
				dao.deleteByPsndocsAndDateArea(pk_org, pk_psndocs, beginDate, endDate);
				// ��һ����ɾ������Ѵ����ڱ�İ�����Ű�Ҳɾ�����Ǵ���ģ�Ҫ���¼���
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
	 * ��ѯ���鹤������������������set��Ա������������
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
		// ����ĳЩ��Ա��ĳЩ�������Ӧ��datetype
		String sql = "select bd_teamcalendar.calendar,bd_teamcalendar.date_daytype " + "from bd_teamcalendar  "
				+ "where bd_teamcalendar.calendar >='" + beginDate + "'  " + "and bd_teamcalendar.calendar <='"
				+ endDate + "'  " + "and  bd_teamcalendar.pk_org ='" + pk_org + "' " + "and dr = 0 "
				+ "and bd_teamcalendar.pk_team = '" + pk_team + "' ";
		List<HashMap<String, Object>> result = new ArrayList<>();// ���ݿⷵ�ؽ��
		// ��װ��Ľ�� Map<date,datetype>,����ѭ������,��ֻ��ȫ���ڴ���,
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
		// ������Ĺ�������������setԱ����������������
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
	 * ѭ���Ű�ļ�У�飬���漰���������
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
	// //�����ų�ͻ��
	// Set<ShiftMutexBUVO> mutexSet0 = allMutexSet[0];
	// //�����ŵߵ���
	// Set<ShiftMutexBUVO> reverseSet0 = allMutexSet[1];
	// //��һ���ͻ��
	// Set<ShiftMutexBUVO> mutexSet1 = allMutexSet[2];
	// //��һ��ߵ���
	// Set<ShiftMutexBUVO> reverseSet1 = allMutexSet[3];
	// //������������û�����ݣ����ʾ��ҵ��Ԫ��������ô�Ű඼�����ͻ��У�鲻�ؽ���
	// if(
	// CollectionUtils.isEmpty(mutexSet0)||
	// CollectionUtils.isEmpty(reverseSet0)||
	// CollectionUtils.isEmpty(mutexSet1)||
	// CollectionUtils.isEmpty(reverseSet1))
	// return;
	// //��ѭ���İ�ΰ����ڷ�Χȫ��չ��
	// String[] pks = new String[endDate.getDaysAfter(beginDate)+1];
	// for(int i=0;i<pks.length;i++){
	// pks[i]=calendarPks[i%calendarPks.length];
	// }
	// String errMsg ="{1}�İ��Ϊ{2},{3}�İ��Ϊ{4},�����˰�γ�ͻ!";
	// //��֯�����а��
	// // Map<String, AggBclbDefVO> shiftMap =
	// NCLocator.getInstance().lookup(IBclbQueryService.class).queryBclbAggVOMap(pk_org);
	// Map<String, AggShiftVO> shiftMap =
	// NCLocator.getInstance().lookup(IShiftQueryService.class).queryShiftAggVOMapByOrg(pk_org);
	// for(int i=0;i<pks.length;i++){
	// String curPk=pks[i];
	// if(StringHelper.isEmpty(curPk)||ShiftVO.PK_GX.equals(curPk))
	// continue;
	// //ǰһ����
	// String former1 = i==0?null:pks[i-1];
	// if(!StringHelper.isEmpty(former1)&&!ShiftVO.PK_GX.equals(former1)&&!isValid(former1,
	// curPk, mutexSet0, reverseSet0))
	// throw new ValidationException(getErrMsg(errMsg, null,
	// beginDate.getDateAfter(i-1).toString(), former1,
	// beginDate.getDateAfter(i).toString(), curPk, shiftMap));
	// //ǰ������
	// String former2 = i<=1?null:pks[i-2];
	// if(!StringHelper.isEmpty(former2)&&!ShiftVO.PK_GX.equals(former2)&&!isValid(former2,
	// curPk, mutexSet1, reverseSet1))
	// throw new ValidationException(getErrMsg(errMsg, null,
	// beginDate.getDateAfter(i-2).toString(), former2,
	// beginDate.getDateAfter(i).toString(), curPk, shiftMap));
	// //��һ����
	// String next1 = i==(pks.length-1)?null:pks[i+1];
	// if(!StringHelper.isEmpty(next1)&&!ShiftVO.PK_GX.equals(next1)&&!isValid(curPk,
	// next1, mutexSet0, reverseSet0))
	// throw new ValidationException(getErrMsg(errMsg, null,
	// beginDate.getDateAfter(i).toString(), curPk,
	// beginDate.getDateAfter(i+1).toString(), next1, shiftMap));
	// //��������
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
	 * ������������
	 * 
	 * @param pk_org
	 * @param vos
	 * @param log
	 * @param checkPsndocEffective
	 *            ���Ƿ�У�鿼�ڵ���
	 * @return
	 * @throws BusinessException
	 */
	protected PsnJobCalendarVO[] save(String pk_hrorg, PsnJobCalendarVO[] vos, boolean checkPsndocEffective, boolean log)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return vos;
		checkCalendarWhenSave(pk_hrorg, vos);// У��
		PsnCalendarDAO dao = new PsnCalendarDAO();
		dao.deleteExistsCalendarWhenSave(vos);// ɾ�����м�¼
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		List<AggPsnCalendar> insertList = new ArrayList<AggPsnCalendar>();
		// HR��֯�����а��
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
				// set����������
				calendarVO.getPsnCalendarVO().setDate_daytype(vo.getDayTypeMap().get(date));
				insertList.add(calendarVO);
			}
			vo.getModifiedCalendarMap().clear();// ��մ洢�޸����ݵ�map
		}
		if (insertList.size() > 0) {
			AggPsnCalendar[] aggvos = insertList.toArray(new AggPsnCalendar[0]);
			PsnCalendarVO[] psnCalvos = new PsnCalendarVO[aggvos.length];
			for (int i = 0; i < aggvos.length; i++) {
				psnCalvos[i] = aggvos[i].getPsnCalendarVO();
			}
			dao.insert(aggvos);
			// ҵ����־
			if (log)
				TaBusilogUtil.writeEditPsnCalendarBusiLog(psnCalvos, null);
		}
		return vos;
	}

	/**
	 * ����ʱ��У�� �˷�������Ա���������ڵ㱣��ʱ���ã����ڽ���ѡ�����HR��֯������п���ͬʱ�޸Ķ��ҵ��Ԫ�İ��
	 * �˷���ȡ�����е�ҵ��Ԫ����ҵ��Ԫ���д���
	 * 
	 * @param pk_hrorg
	 * @param vos
	 * @throws BusinessException
	 */
	private void checkCalendarWhenSave(String pk_hrorg, PsnJobCalendarVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;

		// ����ڼ�������������޸�
		List<UFLiteralDate> modefiedDate = new ArrayList<UFLiteralDate>();
		// ȡ��vos�����е�ҵ��Ԫ
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

		// //ȡ��vos�����е�ҵ��Ԫ
		// Set<String> orgSet = new HashSet<String>();
		// for(PsnJobCalendarVO vo:vos){
		// if(vo.getOrgMap().size()>0)
		// orgSet.addAll(vo.getOrgMap().values());
		// }
		// ��ҵ��Ԫ���ν���У�顣��������Ǳ̨���ǣ�����֯�İ�γ�ͻ�ǲ��ܵģ�����������A��֯��������B��֯��������������İ�γ�ͻ�ˣ��ǲ������쳣��
		CalendarShiftMutexChecker checker = new CalendarShiftMutexChecker();
		for (String pk_org : orgSet) {
			Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String, String>>();
			for (PsnJobCalendarVO vo : vos) {
				if (vo.getModifiedCalendarMap() == null || vo.getModifiedCalendarMap().size() == 0)
					continue;
				// �����ڽ����ϱ��޸ĵĹ�������
				Map<String, String> modifiedMap = vo.getModifiedCalendarMap();
				// ������pk_orgҵ��Ԫ���޸ĵĹ�������
				Map<String, String> modifiedMapInOrg = new HashMap<String, String>();
				for (String date : modifiedMap.keySet()) {
					if (pk_org.equals(vo.getOrgMap().get(date)))
						modifiedMapInOrg.put(date, modifiedMap.get(date));
				}
				if (modifiedMapInOrg.size() > 0)
					modifiedCalendarMap.put(vo.getPk_psndoc(), modifiedMapInOrg);
			}
			checker.checkCalendar(pk_org, modifiedCalendarMap, true, true, false);// ����У��
		}
	}

	@Override
	public PsnJobCalendarVO save(String pk_hrorg, PsnJobCalendarVO vo) throws BusinessException {
		return save(pk_hrorg, new PsnJobCalendarVO[] { vo })[0];
	}

	/*
	 * �������������ڷ�Χ��ѯ��Ա�б�isOverrideExistCalendar��ʾ�û��Ƿ�ϣ���������а�Ρ������Y,
	 * �򷵻����ڷ�Χ�ڿ��ڵ�����Ч����Ա�������N,����˿��ڵ�����Ч֮�⣬��Ҫ�������������� (non-Javadoc)
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
				isOverrideExistCalendar, isHROrg, false);// Ĭ�ϲ��޶�ҵ��Ԫ
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
		// ʹ��Ĭ�ϰ�Σ����Կ�����һ��������ȡ����ѭ���Ű࣬������������Ϊ���죬����һ��������Ĭ�ϰ࣬�������ǹ���
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		OrgVO[] orgVOs = psndocService.queryAllBUsByPsndocs(pk_psndocs, beginDate, endDate);
		// IAOSQueryService aosService =
		// NCLocator.getInstance().lookup(IAOSQueryService.class);
		// ��hr��֯���������ҵ��Ԫ
		// OrgVO[] orgVOsUnderHrOrg =
		// aosService.queryAOSMembersByHROrgPK(pk_hrorg, false, true);
		OrgVO[] orgVOsUnderHrOrg = ShiftServiceFacade.queryOrgsByHROrg(pk_hrorg);

		Set<String> pkOrgsSetUnderHrOrg = new HashSet<String>(Arrays.asList(StringPiecer.getStrArray(orgVOsUnderHrOrg,
				OrgVO.PK_ORG)));
		// ��ѯ�˴β����漰����Ա+���ڷ�Χ��ǣ����������ҵ��Ԫ��Ȼ�����Щҵ��Ԫѭ������
		for (OrgVO orgVO : orgVOs) {
			String pk_org = orgVO.getPk_org();
			// �����ǰҵ��Ԫ���ڴ�HR��֯�Ĺ�Ͻ��Χ�ڣ��򲻴���
			if (!pkOrgsSetUnderHrOrg.contains(pk_org))
				continue;
			AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
			if (defaultShift == null) {
				if (throwExcpWhenNoDefaultShift)
					throw new BusinessException(ResHelper.getString("6017psncalendar", "06017psncalendar0091"
					/* @res "ҵ��Ԫ{0}δ����Ĭ�ϰ��!" */, MultiLangHelper.getName(orgVO)));
				continue;
			}
			// useDefault(pk_org, pk_psndocs, beginDate, endDate,
			// overrideExistCalendar, defaultShift);
			// useDefaultBatch(orgVO.getPk_group(),pk_hrorg,pk_org, pk_psndocs,
			// beginDate, endDate, overrideExistCalendar,
			// defaultShift);//Ч���Ż�����ִ������
			useDefault(orgVO.getPk_group(), pk_hrorg, pk_org, pk_psndocs, beginDate, endDate, overrideExistCalendar,
					defaultShift, false);
		}
		return queryCalendarVOByPsndocs(pk_hrorg, pk_psndocs, beginDate, endDate);
	}

	// /**//Ч���Ż�����ִ������
	// * �еĿͻ�����Ա�ǳ��࣬��������ʱ���������ܴ󣬵����ڴ�������ߴﵽ�����ݿ��ѯ�����ƣ�������������ʧ��
	// * �ڴ˽��з�������ÿ1000��ִ��һ��
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
		// ����ѭ��
		String[] pks = new String[7];
		for (int i = 0; i < pks.length; i++) {
			int week = beginDate.getDateAfter(i).getWeek();
			pks[i] = (week == 0 || week == 6) ? ShiftVO.PK_GX : pk_default;
		}
		return circularArrange(pk_group, pk_hrorg, pk_org, pk_psndocs, beginDate, endDate, pks, true,
				overrideExistCalendar, withRturn);
	}

	/**
	 * ��ͬһҵ��Ԫ���Ű�ʱ��У���Ű�ĺϷ��ԣ� 1.У������ŵ�����İ�εĿ���ʱ��Σ�����ε�ˢ����ʼʱ�䵽ˢ������ʱ��֮���ʱ��Σ��Ƿ��н���
	 * 2.У������ŵ�����İ�εĿ���ʱ����Ƿ�ߵ�(��T+1�յĿ���ʱ����ŵ���T�յĿ���ʱ���֮ǰ)
	 * 3.У���м��һ�������İ�εĿ���ʱ����Ƿ��н��� 4.У���м��һ�������İ�εĿ���ʱ����Ƿ�ߵ�
	 * ��˳��У�飬������������κ�һ������У�鲻ͨ�����׳��쳣��ʾ�û� �˷���Ҫ��ֿ���Ч�ʣ���ΪУ����������ܴ󣬴����û�������ص�Ч������
	 * 
	 * @param pk_org
	 * @param modifiedCalendarMap
	 *            ����Ҫ���õİ�ε���Ϣ��key��pk_psndoc��value��key�����ڣ� value�ǰ��������
	 *            ���ĳ��İ�α�����Ϊnull����valueΪnull,map�в���û����һ��
	 * @param throwsException
	 *            ,�Ƿ�Ҫ�׳��쳣�����Ϊtrue��������ͻʱ���׳��쳣�����Ϊfalse��������ͻʱ�������쳣��
	 *            ����ͻ�����ڼ���map�������շ���
	 *            ���쳣�ķ�ʽ����ѭ���ŰࡢĬ���Ű�Ȳ���������map�ķ�ʽ���ڵ���Ȳ���������ʱ����а�γ�ͻ��
	 *            ��Ҫ�ѳ�ͻ��Ϣ��ӳ��excel�ļ��У�
	 *            ������÷���map�ķ�ʽ����װ��ͻ��Ϣ����ômap�ĽṹΪ��key����Ա������value��Set
	 *            <String>���飬����set���涼�����˰�γ�ͻ�����ڣ�������
	 *            ��һ��set���������û��������þͳ�ͻ�����ڣ��ڶ���set���������û����������й���������ͻ����Ϣ
	 *            ���磬�û�����1.1��1.2����İ࣬����1.3�İ౾��Ҳ���ڣ�����1.1��1.2��ͻ��1.2��1.3��ͻ��
	 *            ��ô���շ��ص�set�����ǣ� set1��1.1��1.2 set2��1.2
	 *            ������װset�������Ƿ��㵼��ʱ��excel�ĳ�ͻ��ʾ��ɫ����ʾ
	 * @param pk_org
	 *            ҵ��Ԫ����
	 */
	// protected Map<String, Set<String>[]> checkCalendar(String pk_org,
	// Map<String, Map<String, String>>
	// modifiedCalendarMap,//key����Ա������value��key�����ڣ�value�ǰ���������洢����Ҫ���õİ�ε���Ϣ
	// boolean overrideExists,boolean throwsException)
	// throws BusinessException{
	// if(modifiedCalendarMap==null||modifiedCalendarMap.size()==0)
	// return null;
	// Set<ShiftMutexBUVO>[] allMutextSet =
	// PsnCalendarUtils.splitMutexVOSet(NCLocator.getInstance().lookup(IShiftQueryService.class).queryShiftMutexByOrg(pk_org));
	// //�����ų�ͻ��
	// Set<ShiftMutexBUVO> mutexSet0 = allMutextSet[0];
	// //�����ŵߵ���
	// Set<ShiftMutexBUVO> reverseSet0 = allMutextSet[1];
	// //��һ���ͻ��
	// Set<ShiftMutexBUVO> mutexSet1 = allMutextSet[2];
	// //��һ��ߵ���
	// Set<ShiftMutexBUVO> reverseSet1 = allMutextSet[3];
	// //������������û�����ݣ����ʾ����֯��������ô�Ű඼�����ͻ��У�鲻�ؽ���
	// if(
	// CollectionUtils.isEmpty(mutexSet0)&&
	// CollectionUtils.isEmpty(reverseSet0)&&
	// CollectionUtils.isEmpty(mutexSet1)&&
	// CollectionUtils.isEmpty(reverseSet1))
	// return null;
	// String[] pk_psndocs = modifiedCalendarMap.keySet().toArray(new
	// String[0]);
	// //Ϊ�˷�ֹ�ڴ������һ�δ�������500��
	// int maxProcessCount = 500;
	// //ѭ������Ļ�����Ҫ���ٴ�ѭ��
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
	// //��ѯ��һ���˵����а������
	// //�ҳ����õİ�ε��������ں��������ڣ���Ϊ��ѯ��������������һ�����ڷ�Χ��
	// Set<String> allDateSet = new HashSet<String>();//�洢�������ڵ�date�����ں��������
	// for(int i=0;i<pk_psndocs.length;i++){
	// Map<String, String> pkMap = modifiedCalendarMap.get(pk_psndocs[i]);
	// if(pkMap==null||pkMap.size()==0)
	// continue;
	// allDateSet.addAll(pkMap.keySet());
	// }
	// String[] allDate = allDateSet.toArray(new String[0]);
	// Arrays.sort(allDate);
	// //����������ǰ�����죬���������죬��������˵��Ű������key��pk_psndoc,value��key�����ڣ�value�ǰ������
	// Map<String, Map<String, String>> existsCalendarMap =
	// new PsnCalendarDAO().queryPkShiftMapByPsndocs(pk_org, pk_psndocs,
	// UFLiteralDate.getDate(allDate[0]).getDateBefore(2),
	// UFLiteralDate.getDate(allDate[allDate.length-1]).getDateAfter(2));
	// if(existsCalendarMap==null)
	// existsCalendarMap = new HashMap<String, Map<String,String>>();
	//
	// //����Ҫ�޸ĵİ�Σ�modifiedCalendarMap����İ�β�һ�����ջ��޸ĵ����ݿ⣬��Ϊ�п����û�ѡ����ǲ����ǣ�.��mapֻ�洢Ҫ�־û������ݿ�ģ����־û��Ĳ���
	// Map<String, Map<String, String>> realModifiedCalendarMap = null;
	// //���յİ�Σ������費У�飬���־û������ݿ���ɺ��������Ű��������finalCalendarMap�ǰ���realModifiedCalendarMap��,У���ʱ����Ҫ�����map��
	// Map<String, Map<String, String>> finalCalendarMap = new HashMap<String,
	// Map<String,String>>();
	// if(overrideExists){//����û�ѡ�񸲸�
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
	// //����û�ѡ�񲻸��������Ű࣬����Ҫ��ԭ�а�ν��бȶԣ���modifiedCalendarMap�����ŵİ�(��ԭ��û���ŵİ�)�ŵ�realModifiedCalendarMap��
	// else{
	// realModifiedCalendarMap = new HashMap<String, Map<String,String>>();
	// //����ѭ��
	// for(int i=0;i<pk_psndocs.length;i++){
	// Map<String, String> existMap = existsCalendarMap.get(pk_psndocs[i]);
	// if(existMap==null){
	// existMap = new HashMap<String, String>();
	// existsCalendarMap.put(pk_psndocs[i], existMap);
	// }
	// Map<String, String> finalMap = existsCalendarMap.get(pk_psndocs[i]);
	// finalCalendarMap.put(pk_psndocs[i], finalMap);
	// //�������Ƿ񸲸ǣ���Ҫ���õİ�ε�map
	// Map<String, String> modifiedMap = modifiedCalendarMap.get(pk_psndocs[i]);
	// if(modifiedMap==null||modifiedMap.size()==0)
	// continue;
	// Iterator<String> dateIterator = modifiedMap.keySet().iterator();
	// Map<String, String> realCalendarMap = new HashMap<String, String>();
	// realModifiedCalendarMap.put(pk_psndocs[i], realCalendarMap);
	// while(dateIterator.hasNext()){
	// String date = dateIterator.next();
	// //�����һ��İ�β����ڣ���˵����һ��İ����Ҫ����
	// if(StringHelper.isEmpty(existMap.get(date)))
	// realCalendarMap.put(date, modifiedMap.get(date));
	// }
	// finalMap.putAll(realCalendarMap);
	// }
	// }
	// //�ߵ����realModifiedCalendarMap��洢����Ҫ����Ҫ�־û��İ�Σ��Ѿ��������Ƿ񸲸�ԭ���ѡ���finalCalendarMap�洢���ǲ����ǳ�ͻ���־û���������Ű����
	// //�����������ѭ������realModifiedCalendarMap����Ҫ���õİ�Σ�����Щ����Ƿ�Υ���˳�ͻ����У��ķ����ǣ��ҵ�
	// //realModifiedCalendarMap���ÿ����ε�ǰһ�졢ǰ���졢��һ�졢������İ�Σ���finalCalendarMap���ң���Ȼ�������ж�����֮���Ƿ��ͻ
	// //��ˣ�����realModifiedCalendarMap������m����Ա��ÿ����ԱҪ����n��İ�Σ���ô�����ҪУ��m*n*4��T�հ���T-1��T-2��T+1��T+2��ҪУ�飩��
	// //��֯�����а��
	// // Map<String, AggBclbDefVO> shiftMap =
	// NCLocator.getInstance().lookup(IBclbQueryService.class).queryBclbAggVOMap(pk_org);
	// Map<String, AggShiftVO> shiftMap =
	// NCLocator.getInstance().lookup(IShiftQueryService.class).queryShiftAggVOMapByOrg(pk_org);
	// Map<String, Set<String>[]> retMap = new HashMap<String, Set<String>[]>();
	// for(int i=0;i<pk_psndocs.length;i++){
	// Map<String, String> realMap = realModifiedCalendarMap.get(pk_psndocs[i]);
	// //Ҫ�����İ��Ϊ�գ�����˲���У��
	// if(realMap==null||realMap.size()==0)
	// continue;
	// //�����Ű���ɺ����ݿ���Ӧ�ô�ŵİ��
	// Map<String, String> finalMap = finalCalendarMap.get(pk_psndocs[i]);
	// Iterator<String> dateIterator = realMap.keySet().iterator();
	// String errMsg ="��Ա{0}��{1}�İ��Ϊ{2},{3}�İ��Ϊ{4},�����˰�γ�ͻ!";
	// while(dateIterator.hasNext()){
	// String date = dateIterator.next();
	// //date�ն�Ӧ�İ��
	// String pk_shift = realMap.get(date);
	// //������հ��Ϊ�գ������ǹ��ݣ�����У�飬��Ϊ��������²����ܲ�����ͻ
	// if(pk_shift==null||ShiftVO.PK_GX.equals(pk_shift))
	// continue;
	// //ǰһ���Σ���finalMap��ȡ��
	// String dateBefore1 =
	// UFLiteralDate.getDate(date).getDateBefore(1).toString();
	// String former1Shift = finalMap.get(dateBefore1);
	// //ǰһ����Ϊ�ջ����ǹ��ݣ�����У��
	// if(former1Shift!=null&&!ShiftVO.PK_GX.equals(former1Shift)&&
	// !isValid(former1Shift, pk_shift, mutexSet0, reverseSet0)){
	// if(throwsException)
	// throw new ValidationException(getErrMsg(errMsg, pk_psndocs[i],
	// UFLiteralDate.getDate(date).getDateBefore(1).toString(), former1Shift,
	// date, pk_shift, shiftMap));
	// processMutexReturnMap(retMap, pk_psndocs[i], date,
	// !realMap.containsKey(dateBefore1));
	// }
	// //ǰ������
	// String dateBefore2 =
	// UFLiteralDate.getDate(date).getDateBefore(2).toString();
	// String former2Shift = finalMap.get(dateBefore2);
	// //ǰ������Ϊ�ջ����ǹ��ݣ�����У��
	// if(former2Shift!=null&&!ShiftVO.PK_GX.equals(former2Shift)&&
	// !isValid(former2Shift, pk_shift,mutexSet1, reverseSet1)){
	// if(throwsException)
	// throw new ValidationException(getErrMsg(errMsg, pk_psndocs[i],
	// UFLiteralDate.getDate(date).getDateBefore(2).toString(), former2Shift,
	// date, pk_shift, shiftMap));
	// processMutexReturnMap(retMap, pk_psndocs[i], date,
	// !realMap.containsKey(dateBefore2));
	// }
	// //��һ����
	// String dateNext1 =
	// UFLiteralDate.getDate(date).getDateAfter(1).toString();
	// String next1Shift = finalMap.get(dateNext1);
	// //��һ����Ϊ�ջ����ǹ��ݣ�����У��
	// if(next1Shift!=null&&!ShiftVO.PK_GX.equals(next1Shift)&&
	// !isValid(pk_shift, next1Shift, mutexSet0, reverseSet0)){
	// if(throwsException)
	// throw new ValidationException(getErrMsg(errMsg, pk_psndocs[i],
	// date, pk_shift, UFLiteralDate.getDate(date).getDateAfter(1).toString(),
	// next1Shift, shiftMap));
	// processMutexReturnMap(retMap, pk_psndocs[i], date,
	// !realMap.containsKey(dateNext1));
	// }
	// //��������
	// String dateNext2 =
	// UFLiteralDate.getDate(date).getDateAfter(2).toString();
	// String next2Shift = finalMap.get(dateNext2);
	// //��������Ϊ�ջ����ǹ��ݣ�����У��
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
	// isMutexWithExistsCalendar?1:0;//��0��set��¼�û��������õİ�γ�ͻ����1��set��¼�����а�εĳ�ͻ
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
	 * У��ǰ����������Ƿ��ͻ�����У�鲻��ͻ������true������false
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
	 * ������Ա�������顢���ڷ�Χ��ѯ�Ű���� (non-Javadoc)
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
			// ����Ĵ���������Ա��˳��������Ҫ����pkPsndocs��������
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
			int beginRowNum = 5;// ���������ڼ��У���Ϊ5��ʼ
			List<String> psnVec = new ArrayList<String>(); // ��¼�ļ��г��ֵ���Ա��Ϣ
			List<String> psncodeVec = new ArrayList<String>(); // ��¼�ļ��г��ֵ���Ա����
			List<String> wrongFormatVec = new ArrayList<String>(); // ���ݸ�ʽ����ȷ�Ĵ��󼯺�
			List<String> samePsnVec = new ArrayList<String>(); // ͬһ���ж�����¼�Ĵ��󼯺�
			List<String> wrongShiftVec = new ArrayList<String>(); // ������ƴ���Ĵ��󼯺�
			List<String> notFoundPsnVec = new ArrayList<String>(); // �Ҳ�����Ӧ����Ա����Ĵ��󼯺�
			List<String> mutextInFileVec = new ArrayList<String>(); // �ļ����Ű��ͻ�Ĵ��󼯺�
			List<String> mutextInDBVec = new ArrayList<String>(); // �������Ű��ͻ�Ĵ��󼯺�

			// ����漰��Ա�Ŀ��ڵ���
			for (int i = 0; i < vos.length; i++) {
				String psncode = (String) vos[i].getAttributeValue(PsnCalendarCommonValue.LISTCODE_PSNCODE);
				if (StringUtils.isEmpty(psncode))
					continue;
				// ����������������
				psncodeVec.add(psncode);
			}
			// ��ȥ���ո���ΪInSQLCreator���Զ���λ
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
			// ���漰��Ա�Ŀ��ڵ�����map�洢
			Map<String, GeneralVO> psncode_tbmPsndocMap = new HashMap<String, GeneralVO>();
			if (!CollectionUtils.isEmpty(tmpPsndocVOs)) {
				for (GeneralVO psndocVO : tmpPsndocVOs) {
					String psncode = (String) psndocVO.getAttributeValue(TBMPsndocCommonValue.LISTCODE_PSNCODE);
					String psnname = (String) psndocVO.getAttributeValue(TBMPsndocCommonValue.LISTCODE_PSNNAME);
					// �����һ�������������ڼ�¼����ȡ���µ�
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
			// ��ȡ��ǰ��֯���а����Ϣ������map�洢,key�ǰ������,value��AggBclbDefVO
			AggShiftVO[] shiftVOs = ShiftServiceFacade.queryAllByHROrg(pk_org);
			Map<String, AggShiftVO> shiftMap = new HashMap<String, AggShiftVO>();
			if (!ArrayUtils.isEmpty(shiftVOs)) {
				for (AggShiftVO shiftVO : shiftVOs) {
					// �˴�ʹ�õ�nameӦΪ���ﻷ���µ�ǰ���ֵ�name������ֱ��ʹ��getname����ƥ�䣬��ͬ��ҵ��Ԫ�����ظ������ƣ�����ƥ��ʧ��
					shiftMap.put(MultiLangHelper.getName(shiftVO.getShiftVO()) + shiftVO.getShiftVO().getPk_org(),
							shiftVO);
				}
			}

			Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String, String>>();// key����Ա������value��key�����ڣ�value�ǰ���������洢����Ҫ���õİ�ε���Ϣ
			// У�鵼����Ϣ
			for (int i = 0; i < vos.length; i++) {
				// ����Ƿ�Ϊ����,Ϊ����������
				if (((UFBoolean) vos[i].getAttributeValue(PsnCalendarCommonValue.LISTCODE_ISNULLROW)).booleanValue())
					continue;
				vos[i].removeAttributeName(PsnCalendarCommonValue.LISTCODE_ISNULLROW);

				String psncode = (String) vos[i].getAttributeValue(PsnCalendarCommonValue.LISTCODE_PSNCODE);
				String psnname = (String) vos[i].getAttributeValue(PsnCalendarCommonValue.LISTCODE_PSNNAME);

				// У�����ݸ�ʽ
				if (StringUtils.isEmpty(psncode))
					wrongFormatVec.add((i + beginRowNum) + "_1");
				if (StringUtils.isEmpty(psnname))
					wrongFormatVec.add((i + beginRowNum) + "_2");

				if (StringUtils.isNotEmpty(psncode) && StringUtils.isNotEmpty(psnname)) {
					// У����Ա����
					if (psncode_tbmPsndocMap.get(psncode + "_" + psnname) == null) {
						notFoundPsnVec.add(String.valueOf((i + beginRowNum)));
						continue;
					}
					// У���ظ���¼
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
				// ���б�ͷ��Ϣ
				String[] fields = vos[i].getAttributeNames();
				Map<String, String> modifyMap = new HashMap<String, String>();
				// У��������
				for (int j = 3; j < fields.length; j++) {
					String shiftName = (String) vos[i].getAttributeValue(fields[j]);
					// ���õİ��Ϊ��
					if (StringUtils.isEmpty(shiftName)) {
						if (isClearNull)
							modifyMap.put(fields[j], null);
						continue;
					}
					// ����ֱ�ӱ���
					if (shiftName.equals(ResHelper.getString("6017psncalendar", "06017psncalendar0092")/*
																										 * @
																										 * res
																										 * "����"
																										 */)) {
						modifyMap.put(fields[j], ShiftVO.PK_GX);
						continue;
					}
					GeneralVO generalVO = psncode_tbmPsndocMap.get(psncode + "_" + psnname);
					// �����ڴ˰����������������ƴ�����Ϣ
					String shiftkey = shiftName + generalVO.getAttributeValue("psnjoborg");
					if (shiftMap.get(shiftkey) == null) {
						wrongShiftVec.add(String.valueOf((i + beginRowNum)) + "_" + j);
						continue;
					}
					ShiftVO shiftVO = shiftMap.get(shiftkey).getShiftVO();
					String pk_shiftorg = shiftVO.getPk_org();
					if (!pk_shiftorg.equalsIgnoreCase((String) generalVO.getAttributeValue("psnjoborg"))
							&& !"DEFAULT".equalsIgnoreCase(shiftVO.getCode())) {// 633��ӣ������ҵ��Ԫ�ģ����жϺ���Ա��ҵ��Ԫ�Ƿ�ƥ��
						wrongShiftVec.add(String.valueOf((i + beginRowNum)) + "_" + j);
						continue;
					}
					// V65������ͣ�õİ�β��ܵ���
					if (null == shiftVO.getEnablestate()
							|| EnableStateEnum.ENABLESTATE_DISABLE.toIntValue() == shiftVO.getEnablestate()) {
						wrongShiftVec.add(String.valueOf((i + beginRowNum)) + "_" + j);
						continue;
					}
					modifyMap.put(fields[j], shiftVO.getPk_shift());
				}
				// ������������Ϊ�ղ���Ҫ�������޸�Map
				if (StringUtils.isEmpty(psncode) || StringUtils.isEmpty(psnname))
					continue;
				String pk_psndoc = (String) psncode_tbmPsndocMap.get(psncode + "_" + psnname).getAttributeValue(
						PsnCalendarCommonValue.LISTCODE_PKPSNDOC);
				if (!modifyMap.isEmpty())
					modifiedCalendarMap.put(pk_psndoc, modifyMap);
				// У�������pk_psndoc���ԣ�Ϊ����У���γ�ͻ��׼��
				vos[i].setAttributeValue(PsnCalendarCommonValue.LISTCODE_PKPSNDOC, pk_psndoc);
			}

			// У���Ű�Ϸ���
			Map<String, Set<String>[]> mutextResultMap = new CalendarShiftMutexChecker().checkCalendar(pk_org,
					modifiedCalendarMap, true, false, false);
			for (int i = 0; i < vos.length; i++) {
				String pk_psndoc = (String) vos[i].getAttributeValue(PsnCalendarCommonValue.LISTCODE_PKPSNDOC);
				if (StringUtils.isEmpty(pk_psndoc))
					continue;
				// ������д��ڳ�ͻ
				Set<String>[] mutextSet = mutextResultMap == null ? null : mutextResultMap.get(pk_psndoc);
				if (ArrayUtils.isEmpty(mutextSet))
					continue;
				// �ļ������õİ�δ��ڳ�ͻ
				if (!CollectionUtils.isEmpty(mutextSet[0])) {
					for (String mutextShift : mutextSet[0]) {
						int index = ArrayUtils.indexOf(vos[i].getAttributeNames(), mutextShift);
						if (index < 0)
							continue;
						mutextInFileVec.add(String.valueOf((i + beginRowNum)) + "_" + index);
					}
				}
				// �����а�δ��ڳ�ͻ
				if (!CollectionUtils.isEmpty(mutextSet[1])) {
					for (String mutextShift : mutextSet[1]) {
						int index = ArrayUtils.indexOf(vos[i].getAttributeNames(), mutextShift);
						if (index < 0)
							continue;
						mutextInDBVec.add(String.valueOf((i + beginRowNum)) + "_" + index);
					}
				}
			}

			// ������쳣���򷵻��쳣��Ϣ
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

			// ���쳣����󱣴�
			// ���б�ͷ��Ϣ
			String[] fields = vos[0].getAttributeNames();
			// ȡ��������
			String[] dateFields = (String[]) ArrayUtils.subarray(fields, 3, fields.length - 1);
			Arrays.sort(dateFields);
			// ȡ�����޸ĵĹ������������ݿ��е���Ϣ
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

			// ���쳣ֱ�ӱ���
			save(pk_org, saveVOs, true, false);
			// ҵ����־
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
		// Ϊ��Ч�ʣ��ֳ�����forѭ�������д��һ��forѭ�������жϴ�������
		// ����ǲ�ѯ�����Ű���Ա����Ҫ�ж��Ű������Ƿ�С�ڿ��ڵ�����Ч�������Ű���������0
		if (queryScope == QueryScopeEnum.part) {
			for (PsnJobCalendarVO vo : calendarVOs) {
				if (vo.getCalendarMap().size() < vo.getPsndocEffectiveDateSet().size()
						&& vo.getCalendarMap().size() > 0)
					returnList.add(vo);
			}
			return returnList.toArray(new PsnJobCalendarVO[0]);
		}
		// ����ǲ�ѯ��δ�Ű���Ա����Ҫ�ж��Ű������Ƿ����0
		if (queryScope == QueryScopeEnum.not) {
			for (PsnJobCalendarVO vo : calendarVOs) {
				if (vo.getCalendarMap().size() == 0)
					returnList.add(vo);
			}
			return returnList.toArray(new PsnJobCalendarVO[0]);
		}
		// �ߵ�����϶��ǲ�ѯ��ȫ�Ű���Ա��ֻ���ж��Ű������Ƿ��뿼�ڵ�����Ч������ȼ���
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
		// ��ȡ��Ա��Ϣ
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
		// ��ȡ��ǰ��֯���а����Ϣ������map�洢,key�ǰ������,value�ǰ������
		// AggShiftVO[] shiftVOs =
		// NCLocator.getInstance().lookup(IBclbQueryService.class).queryAllBclbAggVO(pk_org);
		AggShiftVO[] shiftVOs = ShiftServiceFacade.queryAllByHROrg(pk_org);
		Map<String, String> shiftMap = new HashMap<String, String>();
		shiftMap.put(ShiftVO.PK_GX, ResHelper.getString("6017psncalendar", "06017psncalendar0092")
		/* @res "����" */);
		if (!ArrayUtils.isEmpty(shiftVOs)) {
			for (AggShiftVO shiftVO : shiftVOs) {
				shiftMap.put(shiftVO.getShiftVO().getPk_shift(), shiftVO.getShiftVO().getMultiLangName().toString());
			}
		}

		List<GeneralVO> returnVOs = new ArrayList<GeneralVO>();
		for (int i = 0; i < calendarVOs.length; i++) {
			GeneralVO exportVO = new GeneralVO();
			// ������Ա��Ϣ
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
	 * 2011-07-28���������ۺ�ȷ�ϣ�����ÿһ��Ĳ�ͬ����������߼����£�
	 * 1.�п��ڵ��������ڲ�������HR��֯���ҿ��ڵ�����¼����ְ���ڴ˲��ţ����¼����ţ�
	 * 2.�п��ڵ��������ڲ�������HR��֯�������ڵ�����¼����ְ�����ڴ˲��ţ����¼����ţ�
	 * 3.�п��ڵ����������ڲ�������HR��֯���ҿ��ڵ�����¼����ְ���ڴ˲��ţ����¼����ţ�
	 * 4.�п��ڵ����������ڲ�������HR��֯�������ڵ�����¼����ְ�����ڴ˲��ţ����¼����ţ� 5.���κ���֯�Ŀ��ڵ���
	 * ����1��2��3��4������ʾ���ŵİ�Σ���ֻ���޸�1,Ҳ����˵��ֻҪ���˴���Ŀ��ڵ�����Ч���������ĸ�HR��֯�ģ������ڲ��ڴ˲��ţ�����ʾ����
	 * ����5���û�ɫ��ʾ���� (non-Javadoc)
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
		// 2013-04-27�޸ģ���ѯ���ŷ�Χ�����п�����Ա
		PsnJobCalendarVO[] vos = queryCalendarVOByConditionForTeam(orgVO.getPk_org(), fromWhereSQL, beginDate, endDate,
				true, pk_dept, containsSubDepts);
		if (ArrayUtils.isEmpty(vos) || queryScope == QueryScopeEnum.all)
			return vos;

		List<PsnJobCalendarVO> returnList = new ArrayList<PsnJobCalendarVO>();
		// Ϊ��Ч�ʣ��ֳ�����forѭ�������д��һ��forѭ�������жϴ�������
		// ����ǲ�ѯ�����Ű���Ա����Ҫ�ж��Ű������Ƿ�С�ڿ��ڵ�����Ч�������Ű���������0
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
		// ����ǲ�ѯ��δ�Ű���Ա����Ҫ�ж��Ű������Ƿ����0
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
		// �ߵ�����϶��ǲ�ѯ��ȫ�Ű���Ա��ֻ���ж��Ű������Ƿ��뿼�ڵ�����Ч������ȼ���
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
		// һ����Աĳһ��İ�ο����޸ĵ�ǰ���ǣ�
		// 1.���˴����п��ڵ������ҿ��ڵ�����pk_org��pk_dept����HR��֯��pk_org
		// 2.���˴���Ŀ��ڵ����ϵ���Ա��ְ�Ĳ�����pk_dept���������ﲿ��
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
		// ���µ�һ�����ڵ���
		TBMPsndocVO[] psndocvos = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocVOsByPsndocDate(pk_psndoc, beginDate, endDate);
		if (ArrayUtils.isEmpty(psndocvos))
			return null;
		retVO.setPk_psnjob(psndocvos[psndocvos.length - 1].getPk_psnjob());
		// ���ô�����Ч�Ŀ��ڵ�����Χ
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
		// ��ѯ���˵Ĺ�������
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
		// ��ѯ���˵Ĺ���������
		Map<String, Integer> workDayTypeMap = HRHolidayServiceFacade.queryPsnWorkDayTypeInfo(pk_psndoc, beginDate,
				endDate);
		if (MapUtils.isNotEmpty(workDayTypeMap))
			retVO.getDayTypeMap().putAll(workDayTypeMap);
		return retVO;
	}

	// ��ѯ�õ�ĳ����εĹ�������
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
		// ��ȡ��Ա��Ϣ
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
		// ��ȡ��ǰ��֯���а����Ϣ������map�洢,key�ǰ������,value�ǰ������
		// AggShiftVO[] shiftVOs =
		// NCLocator.getInstance().lookup(IBclbQueryService.class).queryAllBclbAggVO(pk_org);
		AggShiftVO[] shiftVOs = ShiftServiceFacade.queryAllByHROrg(pk_org);
		Map<String, String> shiftMap = new HashMap<String, String>();
		shiftMap.put(ShiftVO.PK_GX, ResHelper.getString("6017psncalendar", "06017psncalendar0092")
		/* @res "����" */);
		if (!ArrayUtils.isEmpty(shiftVOs)) {
			for (AggShiftVO shiftVO : shiftVOs) {
				shiftMap.put(shiftVO.getShiftVO().getPk_shift(), shiftVO.getShiftVO().getMultiLangName().toString());
			}
		}

		List<GeneralVO> returnVOs = new ArrayList<GeneralVO>();
		for (int i = 0; i < calendarVOs.length; i++) {
			GeneralVO exportVO = new GeneralVO();
			// ������Ա��Ϣ
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
		// 2013-04-24���Ȩ��
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
		// ����Ǹ������еĹ�����������ܼ򵥣���ѯ�����µĿ��ڵ�������
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
		// ������ڷ�Χ�ڹ�����������������Ա
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
	 * ����������������Ű�
	 * 
	 * @param psndocs
	 *            ��Ա��Ϣ
	 * @param firstDate
	 *            ��������1
	 * @param secondDate
	 *            ��������2
	 * @author Ares.Tank 2018-9-6 15:15:10
	 */
	@Override
	public void batchChangeDateType(String pk_hrorg, String[] psndocs, UFLiteralDate firstDate,
			UFLiteralDate secondDate, String changedayorhourStr) throws BusinessException {

		if (firstDate.equals(secondDate) || psndocs.length <= 0) {// ��ȵĻ�,ûʲô���
			return;

		}
		PsnCalendarDAO psnCalendarDAO = new PsnCalendarDAO();
		UFLiteralDate[] ufLDates = { firstDate, secondDate };
		List<AggPsnCalendar> resultList = new ArrayList<>();
		// List<String> resultPkList = new ArrayList<>();//pk��һ��,����ɾ����
		// �����Щ�˵Ĺ���������Ϣ
		Map<String, Map<String, PsnCalendarVO>> forChangeMap = psnCalendarDAO.queryCalendarVOMapByPsndocs(pk_hrorg,
				psndocs, ufLDates);

		// BEGIN �ź�{21997} ���춯��Ա��������Ϣ�浽list 2018/9/12
		String changetype = "";
		UFDouble changedayorhour = new UFDouble(0.00);
		if (!StringUtils.isEmpty(changedayorhourStr) && new Double(changedayorhourStr) > 0) {
			changetype = firstDate.toString();
			changedayorhour = new UFDouble(changedayorhourStr);
		}
		List<AggLeaveExtraRestVO> extraRestList = new ArrayList<AggLeaveExtraRestVO>();
		// END �ź�{21997} ���춯��Ա��������Ϣ�浽list 2018/9/12

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
		 * BaseDAO bsDao = new BaseDAO(); // �����Ҫ�������Q����,�����漰��������,��drop���� try {
		 * bsDao.executeUpdate("drop INDEX i_psncalendar ON tbm_psncalendar"); }
		 * catch (Exception e) { Debug.debug(e.getMessage(), e);
		 * 
		 * }
		 * 
		 * 
		 * String[] attrs = { PsnCalendarVO.CALENDAR };
		 * service.updateBillWithAttrs(resultList.toArray(new
		 * AggPsnCalendar[0]), attrs); // �؆��������� String sqlStr =
		 * "CREATE UNIQUE INDEX " + "i_psncalendar " + "ON " +
		 * "tbm_psncalendar " + "( " + "pk_psndoc, " + "calendar, " + "pk_org "
		 * + ") "; try { bsDao.executeUpdate(sqlStr); } catch (Exception e) {
		 * Debug.debug(e.getMessage(), e); }
		 */
		// Ares.Tank v2.0 �洢��ʽ����drop/create����,�ý����ķ�ʽ,�ܿ�����������
		// ������������������ֿ�
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
		// ����Ҫ�޸ĵ�����ȫ���ĵ���������
		service.updateBillWithAttrs(firstDaySet.toArray(new AggPsnCalendar[0]), attrs);
		service.updateBillWithAttrs(secondDaySet.toArray(new AggPsnCalendar[0]), attrs);

		// ������ȷ������

		for (AggPsnCalendar temp : firstDaySet) {
			temp.getPsnCalendarVO().setCalendar(firstDate);
		}
		for (AggPsnCalendar temp : secondDaySet) {
			temp.getPsnCalendarVO().setCalendar(secondDate);
		}
		service.updateBillWithAttrs(firstDaySet.toArray(new AggPsnCalendar[0]), attrs);
		service.updateBillWithAttrs(secondDaySet.toArray(new AggPsnCalendar[0]), attrs);

		// MOD �ź� {21997} ����Ӳ��ݵ������������ݿ� 2018/9/12
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
	 * ��ԃһ�»�������Ϣ
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
		// �����Ա����������Ϣ
		PsnCalendarDAO psnCalendarDAO = new PsnCalendarDAO();
		Map<String, Map<String, PsnCalendarVO>> dateTypeMap = psnCalendarDAO.queryCalendarVOMapByPsndocs(pk_hrorg,
				new String[] { pk_psndoc }, new UFLiteralDate[] { checkDate });
		if (dateTypeMap == null) {
			PsndocVO psndocvo = (PsndocVO) this.getBaseDAO().retrieveByPK(PsndocVO.class, pk_psndoc);
			throw new BusinessException("Ա�� [" + psndocvo.getCode() + "] �� [" + checkDate.toString() + "] δ�ҵ���Ч�Űࡣ");
		}
		PsnCalendarVO psnVo = dateTypeMap.get(pk_psndoc).get(checkDate.toString());
		return psnVo.getDate_daytype();
	}

	/**
	 * ���������������Ű�
	 * 
	 * @param psndocs
	 *            ��Ա��Ϣ
	 * @param date
	 *            ��Ҫ���������
	 * @param ����������
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
		// List<String> resultPkList = new ArrayList<>();//pk��һ��,����ɾ����
		// �����Щ�˵Ĺ���������Ϣ
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
		// ��������a�݆Γ�
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
	 * ����������������Ű�
	 * 
	 * @param psndocs
	 *            ��Ա��Ϣ
	 * @param firstDate
	 *            ��������1
	 * @param secondDate
	 *            ��������2
	 * @author he 2018-9-6 15:15:10
	 */
	@Override
	public List<AggPsnCalendar> changeDateType(String pk_hrorg, String[] psndocs, UFLiteralDate firstDate,
			UFLiteralDate secondDate) throws BusinessException {

		if (firstDate.equals(secondDate)) {// ��ȵĻ�,ûʲô���
			return null;

		}
		PsnCalendarDAO psnCalendarDAO = new PsnCalendarDAO();
		UFLiteralDate[] ufLDates = { firstDate, secondDate };
		List<AggPsnCalendar> resultList = new ArrayList<>();
		// List<String> resultPkList = new ArrayList<>();//pk��һ��,����ɾ����
		// �����Щ�˵Ĺ���������Ϣ
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
	 * ���������������Ű�
	 * 
	 * @param psndocs
	 *            ��Ա��Ϣ
	 * @param date
	 *            ��Ҫ���������
	 * @param ����������
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
		// List<String> resultPkList = new ArrayList<>();//pk��һ��,����ɾ����
		// �����Щ�˵Ĺ���������Ϣ
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