package nc.impl.ta.psncalendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.impl.ta.common.utils.ValidateUtils;
import nc.itf.ta.IHRHolidayQueryService;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.itf.ta.IPsnCalendarManageValidate;
import nc.itf.ta.ITeamCalendarManageMaintain;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.teamcalendar.AggTeamCalendarVO;
import nc.vo.ta.teamcalendar.TeamCalendarVO;



public class PsnCalendarValidateImpl implements IPsnCalendarManageValidate	 {

	@Override
	public List<String> validate(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		IHRHolidayQueryService managequery = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		Map<String, Map<String, Integer>>infomaps = managequery.queryPsnWorkDayTypeInfo(pk_org, pk_psndocs, beginDate, endDate);
		ValidateUtils validates = new ValidateUtils();
		//获取到页面数据的datetype,pk_psndoc,day拼装成psncalendarvo
		List<AggPsnCalendar> psnCalendarVOList = new ArrayList<AggPsnCalendar>();
		for(String pk_psndoc:infomaps.keySet()){
			for(String daydate : infomaps.get(pk_psndoc).keySet()){
				AggPsnCalendar aggVO = new AggPsnCalendar();
				PsnCalendarVO calendarVO=new PsnCalendarVO();
				aggVO.setParentVO(calendarVO);
				calendarVO.setPk_psndoc(pk_psndoc);
				calendarVO.setDate_daytype(infomaps.get(pk_psndoc).get(daydate));
				calendarVO.setCalendar(new UFLiteralDate(daydate));
				psnCalendarVOList.add(aggVO);
			}
		}
		List<String> list = validates.getweekform(pk_org, pk_psndocs, beginDate.toString(), endDate.toString(), psnCalendarVOList);
		//违反一例情况
		Map<String,String>map = new HashMap<String,String>();
		//违反一例一休情况
		Map<String,String>maps = new HashMap<String,String>();
		List<String>arraylist = new ArrayList<String>();
		for(String str : list){
			if(null != str){
				if(str.contains(":")){
					map.put(str.split(":")[0], str.split(":")[1]);
				}else{
					maps.put(str.split("=")[0], str.split("=")[1]);
				}
			}
		}
		//例假日天數低於勞基法規定
		if(map.size()>0){
			String strs = "[";
			String value="";
			for(String key : map.keySet()){
				strs += key +",";
				value = map.get(key);
			}
			strs +="]";
			arraylist.add(strs+" "+value);
		}

		//例假日及休息日天數低於勞基法規定
		if(maps.size()>0){
			String strss = "[";
			String values="";
			for(String key : maps.keySet()){
				strss += key +",";
				values = maps.get(key);
			}
			strss +="]";
			arraylist.add(strss+" "+values);
		}
		return arraylist;
	}
	//批改（交换）日历天校验
	@Override
	public List<String> updateValidate(String pk_org, String[] pk_psndocs,
			UFLiteralDate firstDate, UFLiteralDate sencodeDate)
			throws BusinessException {

		IPsnCalendarManageMaintain psnmanage = NCLocator.getInstance().lookup(IPsnCalendarManageMaintain.class);
		List<AggPsnCalendar>infomaps = psnmanage.changeDateType(pk_org, pk_psndocs, firstDate, sencodeDate);
		ValidateUtils validates = new ValidateUtils();


		List<String>arraylist = new ArrayList<String>();
		try {
			List<String> list = validates.getweekformdate(pk_org, pk_psndocs, firstDate.toString(), sencodeDate.toString(), infomaps);

			//违反一例情况
			Map<String,String>map = new HashMap<String,String>();
			//违反一例一休情况
			Map<String,String>maps = new HashMap<String,String>();
			for(String str : list){
				if(null != str){
					if(str.contains(":")){
						map.put(str.split(":")[0], str.split(":")[1]);
					}else{
						maps.put(str.split("=")[0], str.split("=")[1]);
					}
				}
			}
			//例假日天數低於勞基法規定
			if(map.size()>0){
				String strs = "[";
				String value="";
				for(String key : map.keySet()){
					strs += key +",";
					value = map.get(key);
				}
				strs +="]";
				arraylist.add(strs+" "+value);
			}

			//例假日及休息日天數低於勞基法規定
			if(maps.size()>0){
				String strss = "[";
				String values="";
				for(String key : maps.keySet()){
					strss += key +",";
					values = maps.get(key);
				}
				strss +="]";
				arraylist.add(strss+" "+values);
			}
		} catch (BusinessException e) {
			Debug.error(e.getMessage(), e);
			e.printStackTrace();

		}
		return arraylist;
	}
	//批改（修改）日历天校验
		@Override
		public List<String> updatedayValidate(String pk_org, String[] pk_psndocs,
				UFLiteralDate changeDay, Integer date_daytype)
				throws BusinessException {

			ValidateUtils validates = new ValidateUtils();
			List<AggPsnCalendar> psnCalendarVOList = new ArrayList<AggPsnCalendar>();
			for(String pk_psndoc : pk_psndocs){
				AggPsnCalendar aggVO = new AggPsnCalendar();
				PsnCalendarVO calendarVO=new PsnCalendarVO();
				calendarVO.setPk_psndoc(pk_psndoc);
				calendarVO.setDate_daytype(date_daytype);
				calendarVO.setCalendar(changeDay);
				psnCalendarVOList.add(aggVO);
				aggVO.setParentVO(calendarVO);
			}

			List<String>arraylist = new ArrayList<String>();
			List<String> list = validates.getweekform(pk_org, pk_psndocs, changeDay.toString(), changeDay.toString(), psnCalendarVOList);

			//违反一例情况
			Map<String,String>map = new HashMap<String,String>();
			//违反一例一休情况
			Map<String,String>maps = new HashMap<String,String>();
			for(String str : list){
				if(null != str){
					if(str.contains(":")){
						map.put(str.split(":")[0], str.split(":")[1]);
					}else{
						maps.put(str.split("=")[0], str.split("=")[1]);
					}
				}
			}
			//例假日天數低於勞基法規定
			if(map.size()>0){
				String strs = "[";
				String value="";
				for(String key : map.keySet()){
					strs += key +",";
					value = map.get(key);
				}
				strs +="]";
				arraylist.add(strs+" "+value);
			}

			//例假日及休息日天數低於勞基法規定
			if(maps.size()>0){
				String strss = "[";
				String values="";
				for(String key : maps.keySet()){
					strss += key +",";
					values = maps.get(key);
				}
				strss +="]";
				arraylist.add(strss+" "+values);
			}
			return arraylist;
		}
	/**
	 * 循环排班（班组）
	 */
	@Override
	public List<String> teamvalidate(String pk_org, String[] pk_teams,
			UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {

		IHRHolidayQueryService managequery = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		String[] array = { pk_org };
		Map<String, Map<String, Integer>>infomaps = managequery.queryTeamWorkDayTypeInfos4View(pk_teams, array, beginDate, endDate);
		ValidateUtils validates = new ValidateUtils();
		//获取到页面数据的datetype,pk_psndoc,day拼装成psncalendarvo
		List<AggPsnCalendar> psnCalendarVOList = new ArrayList<AggPsnCalendar>();
		List<String>pk_psndocs = new ArrayList<String>();
		for(String pk_team: infomaps.keySet()){
			Collection<TBMPsndocVO> psndocs = new BaseDAO().retrieveByClause(TBMPsndocVO.class, "pk_team='" + pk_team
					+ "' and BEGINDATE <= '"+beginDate+"' and （ENDDATE > ='"+endDate+"' or ENDDATE is null)");
			for (TBMPsndocVO vo : psndocs) {
				for(String day_date:infomaps.get(pk_team).keySet()){

					AggPsnCalendar aggVO = new AggPsnCalendar();
					PsnCalendarVO calendarVO=new PsnCalendarVO();
					aggVO.setParentVO(calendarVO);
					calendarVO.setPk_psndoc(vo.getPk_psndoc());
					calendarVO.setDate_daytype(infomaps.get(pk_team).get(day_date));
					calendarVO.setCalendar(new UFLiteralDate(day_date));
					psnCalendarVOList.add(aggVO);
				}
				pk_psndocs.add(vo.getPk_psndoc());
			}

		}
		List<String> list = validates.getweekform(pk_org, pk_psndocs.toArray(new String[pk_psndocs.size()]), beginDate.toString(), endDate.toString(), psnCalendarVOList);
		//违反一例情况
		Map<String,String>map = new HashMap<String,String>();
		//违反一例一休情况
		Map<String,String>maps = new HashMap<String,String>();
		List<String>arraylist = new ArrayList<String>();
		for(String str : list){
			if(null != str){
				if(str.contains(":")){
					map.put(str.split(":")[0], str.split(":")[1]);
				}else{
					maps.put(str.split("=")[0], str.split("=")[1]);
				}
			}
		}
		//例假日天數低於勞基法規定
		if(map.size()>0){
			String strs = "[";
			String value="";
			for(String key : map.keySet()){
				strs += key +",";
				value = map.get(key);
			}
			strs +="]";
			arraylist.add(strs+" "+value);
		}

		//例假日及休息日天數低於勞基法規定
		if(maps.size()>0){
			String strss = "[";
			String values="";
			for(String key : maps.keySet()){
				strss += key +",";
				values = maps.get(key);
			}
			strss +="]";
			arraylist.add(strss+" "+values);
		}
		return arraylist;
	}
	@Override
	public List<String> updateteamValidate(String pk_org, String[] pk_teams,
			UFLiteralDate firstDate, UFLiteralDate sencodeDate)
			throws BusinessException {
			ITeamCalendarManageMaintain psnmanage = NCLocator.getInstance().lookup(ITeamCalendarManageMaintain.class);
			List<AggPsnCalendar> psnCalendarVOList = new ArrayList<AggPsnCalendar>();
			List<AggTeamCalendarVO>infomaps = psnmanage.changeDateType(pk_org, pk_teams, firstDate, sencodeDate);
			ValidateUtils validates = new ValidateUtils();
			List<String>pk_psndocs = new ArrayList<String>();
			List<String>arraylist = new ArrayList<String>();
			try {
				for(AggTeamCalendarVO aggvo: infomaps){
					Collection<TBMPsndocVO> psndocs = new BaseDAO().retrieveByClause(TBMPsndocVO.class, "pk_team='" + ((TeamCalendarVO)aggvo.getParentVO()).getPk_team()
							+ "'");
					for (TBMPsndocVO vo : psndocs) {
							AggPsnCalendar aggVO = new AggPsnCalendar();
							PsnCalendarVO calendarVO=new PsnCalendarVO();
							aggVO.setParentVO(calendarVO);
							calendarVO.setPk_psndoc(vo.getPk_psndoc());
							calendarVO.setDate_daytype(((TeamCalendarVO)aggvo.getParentVO()).getDate_daytype());
							calendarVO.setCalendar(((TeamCalendarVO)aggvo.getParentVO()).getCalendar());
							pk_psndocs.add(vo.getPk_psndoc());
							psnCalendarVOList.add(aggVO);
						}
				}
				List<String> list = validates.getweekformdate(pk_org, pk_psndocs.toArray(new String[pk_psndocs.size()]), firstDate.toString(), sencodeDate.toString(), psnCalendarVOList);


				//违反一例情况
				Map<String,String>map = new HashMap<String,String>();
				//违反一例一休情况
				Map<String,String>maps = new HashMap<String,String>();
				for(String str : list){
					if(null != str){
						if(str.contains(":")){
							map.put(str.split(":")[0], str.split(":")[1]);
						}else{
							maps.put(str.split("=")[0], str.split("=")[1]);
						}
					}
				}
				//例假日天數低於勞基法規定
				if(map.size()>0){
					String strs = "[";
					String value="";
					for(String key : map.keySet()){
						strs += key +",";
						value = map.get(key);
					}
					strs +="]";
					arraylist.add(strs+" "+value);
				}

				//例假日及休息日天數低於勞基法規定
				if(maps.size()>0){
					String strss = "[";
					String values="";
					for(String key : maps.keySet()){
						strss += key +",";
						values = maps.get(key);
					}
					strss +="]";
					arraylist.add(strss+" "+values);
				}
			} catch (BusinessException e) {
				Debug.debug(e.getMessage(), e);
				e.printStackTrace();
			}
			return arraylist;



			}

	@Override
	public List<String> updateteamValidate(String pk_org, String[] pk_teams,
			UFLiteralDate changeDate, Integer date_daytype) {

		ValidateUtils validates = new ValidateUtils();
		List<String> psndocslist = new ArrayList<String>();
		List<AggPsnCalendar> psnCalendarVOList = new ArrayList<AggPsnCalendar>();
		List<String>arraylist = new ArrayList<String>();
		try{
			for(String pk_team:pk_teams){
				Collection<TBMPsndocVO> psndocs = new BaseDAO().retrieveByClause(TBMPsndocVO.class, "pk_team='" + pk_team
						+ "'");
				for (TBMPsndocVO vo : psndocs) {
					AggPsnCalendar aggVO = new AggPsnCalendar();
					PsnCalendarVO calendarVO=new PsnCalendarVO();
					aggVO.setParentVO(calendarVO);
					calendarVO.setPk_psndoc(vo.getPk_psndoc());
					calendarVO.setDate_daytype(date_daytype);
					calendarVO.setCalendar(changeDate);
					psndocslist.add(vo.getPk_psndoc());
					psnCalendarVOList.add(aggVO);
				}		}


			List<String> list = validates.getweekform(pk_org, psndocslist.toArray(new String[psndocslist.size()]), changeDate.toString(), changeDate.toString(), psnCalendarVOList);

			//违反一例情况
			Map<String,String>map = new HashMap<String,String>();
			//违反一例一休情况
			Map<String,String>maps = new HashMap<String,String>();
			for(String str : list){
				if(null != str){
					if(str.contains(":")){
						map.put(str.split(":")[0], str.split(":")[1]);
					}else{
						maps.put(str.split("=")[0], str.split("=")[1]);
					}
				}
			}
			//例假日天數低於勞基法規定
			if(map.size()>0){
				String strs = "[";
				String value="";
				for(String key : map.keySet()){
					strs += key +",";
					value = map.get(key);
				}
				strs +="]";
				arraylist.add(strs+" "+value);
			}

			//例假日及休息日天數低於勞基法規定
			if(maps.size()>0){
				String strss = "[";
				String values="";
				for(String key : maps.keySet()){
					strss += key +",";
					values = maps.get(key);
				}
				strss +="]";
				arraylist.add(strss+" "+values);
			}
		} catch (BusinessException e) {
			Debug.debug(e.getMessage(), e);
			e.printStackTrace();
		}
		return arraylist;
	}
}