package nc.impl.wa.specialleave.precacu;

import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.impl.wa.func.AbstractPreExcutorFormulaParse;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.WaLoginContext;

public class ALPaidHoursFormulaPreExcutor extends AbstractPreExcutorFormulaParse{
	
	@SuppressWarnings("all")
	@Override
	public void excute(Object formula, WaLoginContext context)
			throws BusinessException {
		// 先清空數據
		String sql = "update wa_cacu_data set cacu_value = 0 where  "
				+ "pk_wa_class = '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";
		getBaseDao().executeUpdate(sql);

		sql = "select DISTINCT pk_psndoc from wa_cacu_data where pk_wa_class= '"
				+ context.getPk_wa_class()
				+ "' and creator = '"
				+ context.getPk_loginUser() + "'";

		@SuppressWarnings("unchecked")
		ArrayList<String> pk_psndocs = (ArrayList<String>) getBaseDao()
				.executeQuery(sql, new ColumnListProcessor());
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		String[] arguments = getArguments(formula.toString());
		//假期计算
		List<LeaveBalanceVO> lbvolist = 
				(List<LeaveBalanceVO>)this.baseDao.retrieveByClause(LeaveBalanceVO.class, "issettlement='Y' and "
						+ "salaryyear ='"+cyear+"' and salarymonth ='"+cperiod+"' and pk_timeitem="
								+ "(select pk_timeitem from tbm_timeitem where timeitemcode='"+arguments[0]+"')");
		//薪资期间信息
		List<PeriodVO> periodlist = (List<PeriodVO>)this.getBaseDao().retrieveByClause(PeriodVO.class, "PK_PERIODSCHEME in (select PK_PERIODSCHEME "
				+ "from WA_WACLASS where PK_WA_CLASS='"+context.getPk_wa_class()+"') "
				+ "and CYEAR='"+cyear+"'" + 
				"and WA_PERIOD.CPERIOD='"+cperiod+"'");
		UFLiteralDate enddate = null;
		if(periodlist.size() > 0){
			enddate = periodlist.get(0).getCenddate();
		}
		
		//获取考勤档案
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(pk_psndocs.toArray(new String[0]));
		List<TBMPsndocVO> tbmlist = (List<TBMPsndocVO>)this.getBaseDao().retrieveByClause(TBMPsndocVO.class,"pk_psndoc in("+psndocsInSQL+") and begindate < '"+enddate+"' and (enddate > '"+enddate+"' or "
				+ "enddate is null or enddate= '~')");
		for(String pk_psndoc : pk_psndocs){
			UFDouble lastdayorhour = UFDouble.ZERO_DBL;
			UFDouble curdayorhour = UFDouble.ZERO_DBL;
			UFDouble yidayorhour = UFDouble.ZERO_DBL;
			UFDouble restdayorhour = UFDouble.ZERO_DBL;
			//实际转薪资时数
			UFDouble actsalaryhour = UFDouble.ZERO_DBL;
			if(lbvolist.size() > 0){
				for(LeaveBalanceVO leavebalvo : lbvolist){
					if(leavebalvo.getPk_psndoc().equals(pk_psndoc)){
						//上期结余
						if(null !=leavebalvo.getLastdayorhour()){
							lastdayorhour = leavebalvo.getLastdayorhour();
						}
						//享有
						if(null !=leavebalvo.getCurdayorhour()){
							curdayorhour = leavebalvo.getCurdayorhour();
						}
						//已休
						if(null != leavebalvo.getYidayorhour()){
							yidayorhour = leavebalvo.getYidayorhour();
						}
						//结余
						if(null != leavebalvo.getRestdayorhour()){
							restdayorhour = leavebalvo.getRestdayorhour();
						}
					}
				}
			}
			if(tbmlist.size() > 0){
				for(TBMPsndocVO psnvo : tbmlist){
					//取特休结算方式
					if(pk_psndoc.equals(psnvo.getPk_psndoc())){
						String specialrest = psnvo.getSpecialrest();
						//是否结算
						
						if(specialrest.equals("1")){
							//递延次年
							//如果上期结余>已休=上期结余-已休
							if(lastdayorhour.toDouble() - yidayorhour.toDouble() > 0){
								actsalaryhour = lastdayorhour.sub(yidayorhour);
							}
						}else if(specialrest.equals("2")){
							//本年结算
							actsalaryhour = restdayorhour;
						}else{
							actsalaryhour = UFDouble.ZERO_DBL;
						}
					}
				}
			}
			//更新临时表wa_cacu_data
			String updatesql = "update wa_cacu_data set cacu_value = "+actsalaryhour+" where  "
					+ "pk_wa_class = '" + context.getPk_wa_class()
					+ "' and creator = '" + context.getPk_loginUser() + "' and pk_psndoc='"+pk_psndoc+"'";
			getBaseDao().executeUpdate(updatesql);
		}
		
	}
	private BaseDAO baseDao;
	
	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

}
