package nc.impl.ta.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.impl.hi.ComdateUtils;
import nc.impl.ta.algorithm.ITWHolidayOffdayValidate;
import nc.impl.ta.algorithm.TWHolidayOffdayValidatorFactory;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.logging.Debug;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.psndoc.WorkWeekFormEnum;
import nc.vo.ta.teamcalendar.AggTeamCalendarVO;
import nc.vo.ta.teamcalendar.TeamCalendarVO;
import nc.vo.pub.BusinessException;

public class ValidateUtils {
	private BaseDAO dao = new BaseDAO();
	public List<String> getweekform(String pk_org, String[] pk_psndocs, String begindate, String enddate, List<AggPsnCalendar> volists)throws BusinessException{
		//获取开始日期与结束日期之间的天数
		List<String>strlists = new ArrayList<String>(); 
		
		for(String pk_psndoc: pk_psndocs){
			//筛选出人员的期间
			Collection<TBMPsndocVO> psndocs = new BaseDAO().retrieveByClause(TBMPsndocVO.class, "pk_psndoc='" + pk_psndoc
					+ "'");
			List<String> datelist = new ArrayList<String>();
			for(TBMPsndocVO psndoc : psndocs){
				String datestr = ComdateUtils.getAlphalDate(psndoc.getBegindate().toString(), psndoc.getEnddate().toString(), begindate, enddate);
				if(null != datestr){
					datelist.add(datestr);
				}
			}
			for(String dates : datelist){
				int days = ComdateUtils.getDaySub(dates.split(":")[0], dates.split(":")[1]);
				// 根據員工考勤檔案上工型設置進行一例一休校驗
				for(int i =0; i<days+1; i++ ){
					UFLiteralDate checkdate = new UFLiteralDate(ComdateUtils.getcheckdate(dates.split(":")[0], i));
					WorkWeekFormEnum weekForm = getWeekFormFromTBMPsndoc(pk_psndoc, checkdate);
					if(null != weekForm){
						ITWHolidayOffdayValidate validator = TWHolidayOffdayValidatorFactory.getValidator(weekForm);
						strlists.add(validator.validate(pk_org, pk_psndoc, checkdate, volists));
					}
				}
			}
		}
		return strlists;
	}
	//批改的一例一休校验
	public List<String> getweekformdate(String pk_org, String[] pk_psndocs,
			String firstdate, String senconddate, List<AggPsnCalendar> infomaps) throws BusinessException{
			//获取开始日期与结束日期之间的天数
			List<String>strlists = new ArrayList<String>(); 
			try {
				String[] str = new String[2];
				str[0] = firstdate;
				str[1] = senconddate;
				for(String pk_psndoc: pk_psndocs){
					// 根據員工考勤檔案上工型設置進行一例一休校驗
					for(int i =0; i<str.length; i++ ){
						UFLiteralDate checkdate = new UFLiteralDate(str[i]);
						WorkWeekFormEnum weekForm = getWeekFormFromTBMPsndoc(pk_psndoc, checkdate);
						ITWHolidayOffdayValidate validator = TWHolidayOffdayValidatorFactory.getValidator(weekForm);
						strlists.add(validator.validate(pk_org, pk_psndoc, checkdate, infomaps));
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
			UFLiteralDate date) throws BusinessException {
		List<PsndocVO> psndoclist = (List<PsndocVO>)dao.retrieveByClause(PsndocVO.class, "pk_psndoc='"+pk_psndoc+"'");
		Collection<TBMPsndocVO> psndocs = new BaseDAO().retrieveByClause(TBMPsndocVO.class, "pk_psndoc='" + pk_psndoc
				+ "'");
		WorkWeekFormEnum weekForm = null;
		try{
			for (TBMPsndocVO vo : psndocs) {
				if ((vo.getBegindate().isSameDate(date) || vo.getBegindate().before(date))
						&& (vo.getEnddate().isSameDate(date) || vo.getEnddate().after(date))) {
					weekForm = WorkWeekFormEnum.valueOf(WorkWeekFormEnum.class, vo.getWeekform());
				}
			}
		}catch(Exception e){
			throw new BusinessException("员工编号："+psndoclist.get(0).getCode()+"未选工时形态");
		}
		return weekForm;
	}
	
}
