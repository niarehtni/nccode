package nc.impl.ta.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.hr.utils.InSQLCreator;
import nc.impl.hi.ComdateUtils;
import nc.impl.ta.algorithm.ITWHolidayOffdayValidate;
import nc.impl.ta.algorithm.TWHolidayOffdayValidatorFactory;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.logging.Debug;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.psndoc.WorkWeekFormEnum;
import nc.vo.ta.teamcalendar.AggTeamCalendarVO;
import nc.vo.ta.teamcalendar.TeamCalendarVO;
import nc.vo.pub.BusinessException;

public class ValidateUtils{
	private BaseDAO dao = new BaseDAO();
	public Map<Map<Map<String, String>, String>,String> getweekform(String pk_org, String[] pk_psndocs, String begindate, String enddate, List<AggPsnCalendar> volists)throws BusinessException{
		//获取开始日期与结束日期之间的天数
		Map<Map<Map<String, String>, String>,String>strlist = new HashMap<Map<Map<String, String>, String>, String>(); 
		//筛选出人员的期间
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(pk_psndocs);
		Collection<TBMPsndocVO> psndocs = new BaseDAO().retrieveByClause(TBMPsndocVO.class, "pk_psndoc in(" + psndocsInSQL
				+ ")");
		Collection<PsndocVO> psndoclists = new BaseDAO().retrieveByClause(PsndocVO.class, "pk_psndoc in(" + psndocsInSQL
				+ ")");
		for(PsndocVO psndocvos: psndoclists){
			
			List<String> datelist = new ArrayList<String>();
			List<TBMPsndocVO> tbmlist = new ArrayList<TBMPsndocVO>();
			for(TBMPsndocVO psndoc : psndocs){
				if(psndoc.getPk_psndoc().equals(psndocvos.getPk_psndoc())){
					tbmlist.add(psndoc);
					String datestr = ComdateUtils.getAlphalDate(psndoc.getBegindate().toString(), psndoc.getEnddate().toString(), begindate, enddate);
					if(null != datestr){
						datelist.add(datestr);
					}
				}
			}
			
			for(String dates : datelist){
				int days = ComdateUtils.getDaySub(dates.split(":")[0], dates.split(":")[1]);
				// 根據員工考勤檔案上工型設置進行一例一休校驗
				for(int i =0; i<days+1; i++ ){
					UFLiteralDate checkdate = new UFLiteralDate(ComdateUtils.getcheckdate(dates.split(":")[0], i));
					WorkWeekFormEnum weekForm = getWeekFormFromTBMPsndoc(psndocvos.getPk_psndoc(), checkdate, tbmlist);
					if(null != weekForm){
						ITWHolidayOffdayValidate validator = TWHolidayOffdayValidatorFactory.getValidator(weekForm);
						Map<Map<String, String>, String> returnmap = validator.validate(pk_org, psndocvos, checkdate, volists);
						if(null != returnmap){
							strlist.put(returnmap, null);
						}
					}
				}
			}
		}
		return strlist;
	}
	//批改的一例一休校验
	public Map<Map<Map<String, String>, String>,String> getweekformdate(String pk_org, String[] pk_psndocs,
			String firstdate, String senconddate, List<AggPsnCalendar> infomaps) throws BusinessException{
			//获取开始日期与结束日期之间的天数
		Map<Map<Map<String, String>, String>,String>strlists = new HashMap<Map<Map<String, String>, String>, String>();
			try {
				//筛选出人员的期间
				InSQLCreator insql = new InSQLCreator();
				String psndocsInSQL = insql.getInSQL(pk_psndocs);
				Collection<TBMPsndocVO> psndocs = new BaseDAO().retrieveByClause(TBMPsndocVO.class, "pk_psndoc in(" + psndocsInSQL
						+ ")");
				Collection<PsndocVO> psndoclists = new BaseDAO().retrieveByClause(PsndocVO.class, "pk_psndoc in(" + psndocsInSQL
						+ ")");
				String[] str = null;
				if(firstdate.equals(senconddate)){
					str = new String[1];
					str[0] = firstdate;
				}else{
					str = new String[2];
					str[0] = firstdate;
					str[1] = senconddate;
				}
				for(PsndocVO psndocvos: psndoclists){
					List<TBMPsndocVO> tbmlist = new ArrayList<TBMPsndocVO>();
					for(TBMPsndocVO tbmvo : psndocs){
						if(tbmvo.getPk_psndoc().equals(psndocvos.getPk_psndoc())){
							tbmlist.add(tbmvo);
						}
					}
					// 根據員工考勤檔案上工型設置進行一例一休校驗
					for(int i =0; i<str.length; i++ ){
						UFLiteralDate checkdate = new UFLiteralDate(str[i]);
						WorkWeekFormEnum weekForm = getWeekFormFromTBMPsndoc(psndocvos.getPk_psndoc(), checkdate, tbmlist);
						ITWHolidayOffdayValidate validator = TWHolidayOffdayValidatorFactory.getValidator(weekForm);
						Map<Map<String, String>, String> returnmaps = validator.validate(pk_org, psndocvos, checkdate, infomaps);
						if(null != returnmaps){
							strlists.put(returnmaps, null);
						}
					}
				}
				} catch (BusinessException e) {
					Debug.error(e.getMessage(), e);
					e.printStackTrace();
					
				}
					
			return strlists;
	}


	@SuppressWarnings("unchecked")
	private WorkWeekFormEnum getWeekFormFromTBMPsndoc (String pk_psndoc,
			UFLiteralDate date, List<TBMPsndocVO> tbmlist) throws BusinessException {
		//List<PsndocVO> psndoclist = (List<PsndocVO>)dao.retrieveByClause(PsndocVO.class, "pk_psndoc='"+pk_psndoc+"'");
		//Collection<TBMPsndocVO> psndocs = new BaseDAO().retrieveByClause(TBMPsndocVO.class, "pk_psndoc='" + pk_psndoc
		//		+ "'");
		WorkWeekFormEnum weekForm = null;
		//try{
			for (TBMPsndocVO vo : tbmlist) {
				if ((vo.getBegindate().isSameDate(date) || vo.getBegindate().before(date))
						&& (vo.getEnddate().isSameDate(date) || vo.getEnddate().after(date))) {
					weekForm = WorkWeekFormEnum.valueOf(WorkWeekFormEnum.class, vo.getWeekform() == null ? 99 : vo.getWeekform());
				}
			}
		/*}catch(Exception e){
			throw new BusinessException("员工编号："+psndoclist.get(0).getCode()+"未选工时形态");
		}*/
		return weekForm;
	}
	
}
