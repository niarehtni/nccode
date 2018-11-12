package nc.impl.ta.psncalendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.impl.ta.calendar.TACalendarUtils;
import nc.impl.ta.common.utils.ValidateUtils;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.ta.IHRHolidayQueryService;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.itf.ta.IPsnCalendarManageValidate;
import nc.itf.ta.IPsnCalendarQueryMaintain;
import nc.itf.ta.ITeamCalendarManageMaintain;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.teamcalendar.AggTeamCalendarVO;
import nc.vo.ta.teamcalendar.TeamCalendarVO;

public class PsnCalendarValidateImpl implements IPsnCalendarManageValidate {
	private BaseDAO dao = new BaseDAO();

	@Override
	public List<List<String>> validate(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		// 校验天数的
		Map<String, String> daymap = new HashMap<String, String>();
		// 校验小时数的
		Map<String, String> hoursmap = new HashMap<String, String>();
		// 总message
		List<List<String>> alllist = new ArrayList<List<String>>();

		IHRHolidayQueryService managequery = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		Map<String, Map<String, Integer>> infomaps = managequery.queryPsnWorkDayTypeInfo(pk_org, pk_psndocs, beginDate,
				endDate);
		ValidateUtils validates = new ValidateUtils();
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		// 获取到页面数据的datetype,pk_psndoc,day拼装成psncalendarvo
		List<AggPsnCalendar> psnCalendarVOList = new ArrayList<AggPsnCalendar>();
		for (String pk_psndoc : infomaps.keySet()) {
			for (String daydate : infomaps.get(pk_psndoc).keySet()) {
				AggPsnCalendar aggVO = new AggPsnCalendar();
				PsnCalendarVO calendarVO = new PsnCalendarVO();
				aggVO.setParentVO(calendarVO);
				calendarVO.setPk_psndoc(pk_psndoc);
				calendarVO.setDate_daytype(infomaps.get(pk_psndoc).get(daydate));
				calendarVO.setCalendar(new UFLiteralDate(daydate));
				if (null != defaultShift) {
					calendarVO.setGzsj(((ShiftVO) defaultShift.getParentVO()).getGzsj());
				}
				psnCalendarVOList.add(aggVO);
			}
		}
		Map<Map<Map<String, String>, String>, String> maps = validates.getweekform(pk_org, pk_psndocs,
				beginDate.toString(), endDate.toString(), psnCalendarVOList);
		for (Map<Map<String, String>, String> maplist : maps.keySet()) {
			if (maplist.size() > 0) {
				for (Map<String, String> mapstr : maplist.keySet()) {
					if (maplist.get(mapstr).equals("holidaymessage")) {
						for (String str : mapstr.keySet()) {
							daymap.put(mapstr.get(str) + ":" + str, null);
						}

					} else {
						for (String str : mapstr.keySet()) {
							hoursmap.put(mapstr.get(str) + ":" + str, null);
						}
					}
				}
			}
		}
		if (daymap.size() > 0) {
			List<String> daymaplist = new ArrayList<String>();
			for (String str : daymap.keySet()) {
				daymaplist.add(str);
			}
			alllist.add(daymaplist);
		}
		if (hoursmap.size() > 0) {
			List<String> hoursmapmaplist = new ArrayList<String>();
			for (String str : hoursmap.keySet()) {
				hoursmapmaplist.add(str);
			}
			alllist.add(hoursmapmaplist);
		}
		return alllist;

	}

	/**
	 * 员工工作日历循环排班校验
	 */
	@Override
	public List<List<String>> curvalidate(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, String[] pk_shifts) throws BusinessException {
		// 校验天数的
		Map<String, String> daymap = new HashMap<String, String>();
		// 校验小时数的
		Map<String, String> hoursmap = new HashMap<String, String>();
		// 总message
		List<List<String>> alllist = new ArrayList<List<String>>();

		IHRHolidayQueryService managequery = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		Map<String, Map<String, Integer>> infomaps = managequery.queryPsnWorkDayTypeInfo(pk_org, pk_psndocs, beginDate,
				endDate);

		IPsnCalendarQueryMaintain managequerys = NCLocator.getInstance().lookup(IPsnCalendarQueryMaintain.class);
		ValidateUtils validates = new ValidateUtils();
		// 将循环的班次按日期范围全部展开，key是日期，value是班次主键
		Map<String, String> originalExpandedDatePkShiftMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate,
				endDate, pk_shifts);
		List<String> list = new ArrayList<String>();
		for (String str : originalExpandedDatePkShiftMap.keySet()) {
			list.add(originalExpandedDatePkShiftMap.get(str));
		}
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(list.toArray(new String[0]));
		List<ShiftVO> shfitlists = (List<ShiftVO>) dao.retrieveByClause(ShiftVO.class, "pk_shift in (" + psndocsInSQL
				+ ")");
		// 获取到页面数据的datetype,pk_psndoc,day拼装成psncalendarvo
		List<AggPsnCalendar> psnCalendarVOList = new ArrayList<AggPsnCalendar>();
		for (String pk_psndoc : infomaps.keySet()) {
			for (String daydate : infomaps.get(pk_psndoc).keySet()) {
				// for(String str : originalExpandedDatePkShiftMap.keySet()){
				for (ShiftVO shfit : shfitlists) {
					if (originalExpandedDatePkShiftMap.get(daydate).equals(shfit.getPk_shift())) {
						AggPsnCalendar aggVO = new AggPsnCalendar();
						PsnCalendarVO calendarVO = new PsnCalendarVO();
						aggVO.setParentVO(calendarVO);
						calendarVO.setPk_psndoc(pk_psndoc);
						calendarVO.setDate_daytype(infomaps.get(pk_psndoc).get(daydate));
						calendarVO.setCalendar(new UFLiteralDate(daydate));
						if (null != originalExpandedDatePkShiftMap) {
							calendarVO.setGzsj(shfit.getGzsj());
						}
						psnCalendarVOList.add(aggVO);
					}
					// }
				}
			}
		}
		Map<Map<Map<String, String>, String>, String> maps = validates.getweekform(pk_org, pk_psndocs,
				beginDate.toString(), endDate.toString(), psnCalendarVOList);
		for (Map<Map<String, String>, String> maplist : maps.keySet()) {
			if (maplist.size() > 0) {
				for (Map<String, String> mapstr : maplist.keySet()) {
					if (maplist.get(mapstr).equals("holidaymessage")) {
						for (String str : mapstr.keySet()) {
							daymap.put(mapstr.get(str) + ":" + str, null);
						}

					} else {
						for (String str : mapstr.keySet()) {
							hoursmap.put(mapstr.get(str) + ":" + str, null);
						}
					}
				}
			}
		}
		if (daymap.size() > 0) {
			List<String> daymaplist = new ArrayList<String>();
			for (String str : daymap.keySet()) {
				daymaplist.add(str);
			}
			alllist.add(daymaplist);
		}
		if (hoursmap.size() > 0) {
			List<String> hoursmapmaplist = new ArrayList<String>();
			for (String str : hoursmap.keySet()) {
				hoursmapmaplist.add(str);
			}
			alllist.add(hoursmapmaplist);
		}
		return alllist;

	}

	// 员工工作日历批改（交换）日历天校验
	@Override
	public List<List<String>> updateValidate(String pk_org, String[] pk_psndocs, UFLiteralDate firstDate,
			UFLiteralDate sencodeDate) throws BusinessException {
		// 校验天数的
		Map<String, String> daymap = new HashMap<String, String>();
		// 校验小时数的
		Map<String, String> hoursmap = new HashMap<String, String>();
		// 总message
		List<List<String>> alllist = new ArrayList<List<String>>();
		IPsnCalendarManageMaintain psnmanage = NCLocator.getInstance().lookup(IPsnCalendarManageMaintain.class);
		List<AggPsnCalendar> infomaps = psnmanage.changeDateType(pk_org, pk_psndocs, firstDate, sencodeDate);
		ValidateUtils validates = new ValidateUtils();

		Map<Map<Map<String, String>, String>, String> maps = validates.getweekformdate(pk_org, pk_psndocs,
				firstDate.toString(), sencodeDate.toString(), infomaps);

		for (Map<Map<String, String>, String> maplist : maps.keySet()) {
			if (maplist.size() > 0) {
				for (Map<String, String> mapstr : maplist.keySet()) {
					if (maplist.get(mapstr).equals("holidaymessage")) {
						for (String str : mapstr.keySet()) {
							daymap.put(mapstr.get(str) + ":" + str, null);
						}

					} else {
						for (String str : mapstr.keySet()) {
							hoursmap.put(mapstr.get(str) + ":" + str, null);
						}
					}
				}
			}
		}
		if (daymap.size() > 0) {
			List<String> daymaplist = new ArrayList<String>();
			for (String str : daymap.keySet()) {
				daymaplist.add(str);
			}
			alllist.add(daymaplist);
		}
		if (hoursmap.size() > 0) {
			List<String> hoursmapmaplist = new ArrayList<String>();
			for (String str : hoursmap.keySet()) {
				hoursmapmaplist.add(str);
			}
			alllist.add(hoursmapmaplist);
		}
		return alllist;
	}

	/**
	 * 循环排班（班组）
	 */
	@Override
	public List<List<String>> teamvalidate(String pk_org, String[] pk_teams, UFLiteralDate beginDate,
			UFLiteralDate endDate, String[] pk_shifts) throws BusinessException {
		// 校验天数的
		Map<String, String> daymap = new HashMap<String, String>();
		// 校验小时数的
		Map<String, String> hoursmap = new HashMap<String, String>();
		// 总message
		List<List<String>> alllist = new ArrayList<List<String>>();
		IHRHolidayQueryService managequery = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		String[] array = { pk_org };
		Map<String, Map<String, Integer>> infomaps = managequery.queryTeamWorkDayTypeInfos4View(pk_teams, array,
				beginDate, endDate);
		ValidateUtils validates = new ValidateUtils();
		// 将循环的班次按日期范围全部展开，key是日期，value是班次主键
		Map<String, String> originalExpandedDatePkShiftMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate,
				endDate, pk_shifts);
		ShiftVO shiftvo = new ShiftVO();
		List<String> lists = new ArrayList<String>();
		for (String str : originalExpandedDatePkShiftMap.keySet()) {
			lists.add(originalExpandedDatePkShiftMap.get(str));
		}
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(lists.toArray(new String[0]));
		List<ShiftVO> shfitlists = (List<ShiftVO>) dao.retrieveByClause(ShiftVO.class, "pk_shift in (" + psndocsInSQL
				+ ")");
		// 获取到页面数据的datetype,pk_psndoc,day拼装成psncalendarvo
		List<AggPsnCalendar> psnCalendarVOList = new ArrayList<AggPsnCalendar>();
		Collection<TBMPsndocVO> psndocs = new BaseDAO().retrieveByClause(TBMPsndocVO.class,
				"pk_team in(" + insql.getInSQL(pk_teams) + ") and BEGINDATE <= '" + beginDate + "' and （ENDDATE > ='"
						+ endDate + "' or ENDDATE is null)");
		List<String> pk_psndocs = new ArrayList<String>();
		for (String pk_team : infomaps.keySet()) {
			List<TBMPsndocVO> tbmlist = new ArrayList<TBMPsndocVO>();
			for (TBMPsndocVO tbmpsndocvo : psndocs) {
				if (tbmpsndocvo.getPk_team().equals(pk_team)) {
					tbmlist.add(tbmpsndocvo);
				}
			}
			for (TBMPsndocVO vo : tbmlist) {
				for (String day_date : infomaps.get(pk_team).keySet()) {
					for (ShiftVO shift : shfitlists) {
						if (shift.getPk_shift().equals(originalExpandedDatePkShiftMap.get(day_date))) {
							AggPsnCalendar aggVO = new AggPsnCalendar();
							PsnCalendarVO calendarVO = new PsnCalendarVO();
							aggVO.setParentVO(calendarVO);
							calendarVO.setPk_psndoc(vo.getPk_psndoc());
							calendarVO.setDate_daytype(infomaps.get(pk_team).get(day_date));
							calendarVO.setCalendar(new UFLiteralDate(day_date));
							calendarVO.setGzsj(shift.getGzsj());
							psnCalendarVOList.add(aggVO);
						}
					}
				}
				pk_psndocs.add(vo.getPk_psndoc());
			}

		}
		Map<Map<Map<String, String>, String>, String> maps = validates.getweekform(pk_org,
				pk_psndocs.toArray(new String[pk_psndocs.size()]), beginDate.toString(), endDate.toString(),
				psnCalendarVOList);
		for (Map<Map<String, String>, String> maplist : maps.keySet()) {
			if (maplist.size() > 0) {
				for (Map<String, String> mapstr : maplist.keySet()) {
					if (maplist.get(mapstr).equals("holidaymessage")) {
						for (String str : mapstr.keySet()) {
							daymap.put(mapstr.get(str) + ":" + str, null);
						}

					} else {
						for (String str : mapstr.keySet()) {
							hoursmap.put(mapstr.get(str) + ":" + str, null);
						}
					}
				}
			}
		}
		if (daymap.size() > 0) {
			List<String> daymaplist = new ArrayList<String>();
			for (String str : daymap.keySet()) {
				daymaplist.add(str);
			}
			alllist.add(daymaplist);
		}
		if (hoursmap.size() > 0) {
			List<String> hoursmapmaplist = new ArrayList<String>();
			for (String str : hoursmap.keySet()) {
				hoursmapmaplist.add(str);
			}
			alllist.add(hoursmapmaplist);
		}
		return alllist;
	}

	/**
	 * 班组批改交换日历天校验
	 */
	@Override
	public List<List<String>> updateteamValidate(String pk_org, String[] pk_teams, UFLiteralDate firstDate,
			UFLiteralDate sencodeDate) throws BusinessException {
		// 校验天数的
		Map<String, String> daymap = new HashMap<String, String>();
		// 校验小时数的
		Map<String, String> hoursmap = new HashMap<String, String>();
		// 总message
		List<List<String>> alllist = new ArrayList<List<String>>();
		ITeamCalendarManageMaintain psnmanage = NCLocator.getInstance().lookup(ITeamCalendarManageMaintain.class);
		List<AggPsnCalendar> psnCalendarVOList = new ArrayList<AggPsnCalendar>();
		List<AggTeamCalendarVO> infomaps = psnmanage.changeDateType(pk_org, pk_teams, firstDate, sencodeDate);
		ValidateUtils validates = new ValidateUtils();
		List<String> pk_psndocs = new ArrayList<String>();
		List<String> arraylist = new ArrayList<String>();
		for (AggTeamCalendarVO aggvo : infomaps) {
			Collection<TBMPsndocVO> psndocs = new BaseDAO().retrieveByClause(TBMPsndocVO.class, "pk_team='"
					+ ((TeamCalendarVO) aggvo.getParentVO()).getPk_team() + "'");
			for (TBMPsndocVO vo : psndocs) {
				AggPsnCalendar aggVO = new AggPsnCalendar();
				PsnCalendarVO calendarVO = new PsnCalendarVO();
				aggVO.setParentVO(calendarVO);
				calendarVO.setPk_psndoc(vo.getPk_psndoc());
				calendarVO.setDate_daytype(((TeamCalendarVO) aggvo.getParentVO()).getDate_daytype());
				calendarVO.setCalendar(((TeamCalendarVO) aggvo.getParentVO()).getCalendar());
				pk_psndocs.add(vo.getPk_psndoc());
				calendarVO.setGzsj(((TeamCalendarVO) aggvo.getParentVO()).getGzsj());
				psnCalendarVOList.add(aggVO);
			}
		}
		Map<Map<Map<String, String>, String>, String> maps = validates.getweekformdate(pk_org,
				pk_psndocs.toArray(new String[pk_psndocs.size()]), firstDate.toString(), sencodeDate.toString(),
				psnCalendarVOList);

		for (Map<Map<String, String>, String> maplist : maps.keySet()) {
			if (maplist.size() > 0) {
				for (Map<String, String> mapstr : maplist.keySet()) {
					if (maplist.get(mapstr).equals("holidaymessage")) {
						for (String str : mapstr.keySet()) {
							daymap.put(mapstr.get(str) + ":" + str, null);
						}

					} else {
						for (String str : mapstr.keySet()) {
							hoursmap.put(mapstr.get(str) + ":" + str, null);
						}
					}
				}
			}
		}
		if (daymap.size() > 0) {
			List<String> daymaplist = new ArrayList<String>();
			for (String str : daymap.keySet()) {
				daymaplist.add(str);
			}
			alllist.add(daymaplist);
		}
		if (hoursmap.size() > 0) {
			List<String> hoursmapmaplist = new ArrayList<String>();
			for (String str : hoursmap.keySet()) {
				hoursmapmaplist.add(str);
			}
			alllist.add(hoursmapmaplist);
		}

		return alllist;

	}

	/**
	 * 班组批改日历天校验
	 */
	@Override
	public List<List<String>> updateteamValidate(String pk_org, String[] pk_teams, UFLiteralDate changeDate,
			Integer date_daytype) throws BusinessException {
		// 校验天数的
		Map<String, String> daymap = new HashMap<String, String>();
		// 校验小时数的
		Map<String, String> hoursmap = new HashMap<String, String>();
		// 总message
		List<List<String>> alllist = new ArrayList<List<String>>();
		ValidateUtils validates = new ValidateUtils();
		List<String> psndocslist = new ArrayList<String>();
		List<AggPsnCalendar> psnCalendarVOList = new ArrayList<AggPsnCalendar>();
		List<String> arraylist = new ArrayList<String>();
		for (String pk_team : pk_teams) {
			Collection<TBMPsndocVO> psndocs = new BaseDAO().retrieveByClause(TBMPsndocVO.class, "pk_team='" + pk_team
					+ "'");
			for (TBMPsndocVO vo : psndocs) {
				AggPsnCalendar aggVO = new AggPsnCalendar();
				PsnCalendarVO calendarVO = new PsnCalendarVO();
				aggVO.setParentVO(calendarVO);
				calendarVO.setPk_psndoc(vo.getPk_psndoc());
				calendarVO.setDate_daytype(date_daytype);
				calendarVO.setCalendar(changeDate);
				psndocslist.add(vo.getPk_psndoc());
				psnCalendarVOList.add(aggVO);
			}
		}

		Map<Map<Map<String, String>, String>, String> maps = validates.getweekform(pk_org,
				psndocslist.toArray(new String[psndocslist.size()]), changeDate.toString(), changeDate.toString(),
				psnCalendarVOList);

		for (Map<Map<String, String>, String> maplist : maps.keySet()) {
			if (maplist.size() > 0) {
				for (Map<String, String> mapstr : maplist.keySet()) {
					if (maplist.get(mapstr).equals("holidaymessage")) {
						for (String str : mapstr.keySet()) {
							daymap.put(mapstr.get(str) + ":" + str, null);
						}

					} else {
						for (String str : mapstr.keySet()) {
							hoursmap.put(mapstr.get(str) + ":" + str, null);
						}
					}
				}
			}
		}
		if (daymap.size() > 0) {
			List<String> daymaplist = new ArrayList<String>();
			for (String str : daymap.keySet()) {
				daymaplist.add(str);
			}
			alllist.add(daymaplist);
		}
		if (hoursmap.size() > 0) {
			List<String> hoursmapmaplist = new ArrayList<String>();
			for (String str : hoursmap.keySet()) {
				hoursmapmaplist.add(str);
			}
			alllist.add(hoursmapmaplist);
		}

		return alllist;

	}

	/**
	 * 员工工作日历修改某一天校验
	 */
	@Override
	public List<List<String>> updatedayValidate(String pk_org, String[] pk_psndocs, UFLiteralDate changeDate,
			Integer date_daytype) throws BusinessException {

		// 校验天数的
		Map<String, String> daymap = new HashMap<String, String>();
		// 校验小时数的
		Map<String, String> hoursmap = new HashMap<String, String>();
		// 总message
		List<List<String>> alllist = new ArrayList<List<String>>();
		List<AggPsnCalendar> psnCalendarVOList = new ArrayList<AggPsnCalendar>();
		ValidateUtils validates = new ValidateUtils();
		for (String pk_psndoc : pk_psndocs) {
			AggPsnCalendar aggVO = new AggPsnCalendar();
			PsnCalendarVO calendarVO = new PsnCalendarVO();
			aggVO.setParentVO(calendarVO);
			calendarVO.setPk_psndoc(pk_psndoc);
			calendarVO.setDate_daytype(date_daytype);
			calendarVO.setCalendar(changeDate);
			psnCalendarVOList.add(aggVO);
		}
		Map<Map<Map<String, String>, String>, String> maps = validates.getweekformdate(pk_org, pk_psndocs,
				changeDate.toString(), changeDate.toString(), psnCalendarVOList);

		for (Map<Map<String, String>, String> maplist : maps.keySet()) {
			if (maplist.size() > 0) {
				for (Map<String, String> mapstr : maplist.keySet()) {
					if (maplist.get(mapstr).equals("holidaymessage")) {
						for (String str : mapstr.keySet()) {
							daymap.put(mapstr.get(str) + ":" + str, null);
						}

					} else {
						for (String str : mapstr.keySet()) {
							hoursmap.put(mapstr.get(str) + ":" + str, null);
						}
					}
				}
			}
		}
		if (daymap.size() > 0) {
			List<String> daymaplist = new ArrayList<String>();
			for (String str : daymap.keySet()) {
				daymaplist.add(str);
			}
			alllist.add(daymaplist);
		}
		if (hoursmap.size() > 0) {
			List<String> hoursmapmaplist = new ArrayList<String>();
			for (String str : hoursmap.keySet()) {
				hoursmapmaplist.add(str);
			}
			alllist.add(hoursmapmaplist);
		}
		return alllist;
	}

}