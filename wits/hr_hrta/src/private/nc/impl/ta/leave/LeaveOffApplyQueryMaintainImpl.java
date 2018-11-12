package nc.impl.ta.leave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.SQLHelper;
import nc.impl.ta.algorithm.BillValidatorAtServer;
import nc.itf.hr.pf.HrPfHelper;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.ILeaveOffApplyQueryMaintain;
import nc.itf.ta.ILeaveRegisterQueryMaintain;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.mddb.baseutil.MDDAOUtil;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hr.pf.PFQueryParams;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.leave.LeaveConst;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.leaveoff.AggLeaveoffVO;
import nc.vo.ta.leaveoff.LeaveoffVO;
import nc.vo.ta.pub.TAPFBillQueryParams;
import nc.vo.ta.pub.TaNormalQueryUtils;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author wangdca
 *
 */
public class LeaveOffApplyQueryMaintainImpl implements ILeaveOffApplyQueryMaintain {
	
	private SimpleDocServiceTemplate serviceTemplate;
	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate("99bb6604-fb63-471f-b598-20a0e430a5a9");
		}
		return serviceTemplate;
	}
	@Override
	public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(
			AggLeaveoffVO aggVO) throws BusinessException, BillMutexException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(
			String pkOrg, LeaveoffVO[] vos) throws BusinessException,
			BillMutexException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AggLeaveoffVO[] defaultQuery(LoginContext context)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AggLeaveoffVO[] queryByPsndoc(String pk_psndoc,
			FromWhereSQL fromWhereSQL, Object extraConds)
			throws BusinessException {
		return queryByCond(null, pk_psndoc, fromWhereSQL, extraConds, false);
	}

	
	@Override
	public AggLeaveoffVO[] queryByCond(LoginContext context,FromWhereSQL fromWhereSQL, Object extraConds)
			throws BusinessException {
		return queryByCond(context.getPk_org(), null, fromWhereSQL, extraConds, false);
	}

	@Override
	public AggLeaveoffVO queryByPk(String pk) throws BusinessException {
		return getServiceTemplate().queryByPk(AggLeaveoffVO.class, pk);
	}
	@Override
	public LeaveRegVO[] getRegVos4LeaveOff(String pk_org, String pk_psnjob,String pk_leavetype)
			throws BusinessException {
//		Logger.error("start --->");
		//SQL先过滤掉与已封存期间有交集的休假记录不可以销假，存在对应的销假单且审批未完成的不可以销假,2015-09-17调整，需求为哺乳假开始时间已经封存但是应该允许销假，修改为只要结束日期没有封存则可以销假
//		String cond= " pk_psnjob ='"+pk_psnjob+"' and  "+ (pk_leavetype==null? "1=1" : "pk_leavetype ='"+pk_leavetype+"' ")+ "  "+
//				     " and pk_leavereg  not in (select pk_leavereg from TBM_LEAVEOFF where APPROVE_STATE  not in ("+IPfRetCheckInfo.NOPASS+","+IPfRetCheckInfo.PASSING+")) " +
//					" and leavebegindate> ( select isnull(MAX(enddate),'1971') from tbm_period where sealflag='Y' and pk_org = '" + pk_org +"' ) and billsource = "+LeaveConst.BILL_SOURCE_APP+" " ;
		//MOD 取消已封存期间限制，以薪资期间限制 James
//		String cond= " pk_psndoc in (select pk_psndoc from hi_psnjob where pk_psndoc=(select pk_psndoc from bd_psnjob where pk_psnjob ='"+pk_psnjob+"')) and  "+ (pk_leavetype==null? "1=1" : "pk_leavetype ='"+pk_leavetype+"' ")+ "  "+
//	     " and pk_leavereg  not in (select pk_leavereg from TBM_LEAVEOFF where APPROVE_STATE  not in ("+IPfRetCheckInfo.NOPASS+","+IPfRetCheckInfo.PASSING+")) " +
//		" and leaveenddate> ( select isnull(MAX(enddate),'1971') from tbm_period where sealflag='Y' and pk_org = '" + pk_org +"' ) and billsource = "+LeaveConst.BILL_SOURCE_APP+" " ;
		
		
		String cond= " pk_psndoc in (select pk_psndoc from hi_psnjob where pk_psndoc=(select pk_psndoc from bd_psnjob where pk_psnjob ='"+pk_psnjob+"')) and  "+ (pk_leavetype==null? "1=1" : "pk_leavetype ='"+pk_leavetype+"' ")+ "  "+
			     " and pk_leavereg  not in (select pk_leavereg from TBM_LEAVEOFF where APPROVE_STATE  not in ("+IPfRetCheckInfo.NOPASS+","+IPfRetCheckInfo.PASSING+")) " +
				" and pk_leavereg  in (select pk_leavereg from tbm_leavereg where dr=0 and (waperiod is null or waperiod='')) and billsource = "+LeaveConst.BILL_SOURCE_APP+" " ;
		
//		Logger.error(cond+"--->"+cond);
		LeaveRegVO[] regvos=getServiceTemplate().queryByCondition(LeaveRegVO.class, cond);
		if(ArrayUtils.isEmpty(regvos))
			return null;
		//再过滤掉--时间段所在期间或年度已经进行假期结算的休假记录不可进行销假申请，
		ILeaveBalanceManageService balanceService = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class);
		Map<String, LeaveBalanceVO> balanceMap = balanceService.queryAndCalLeaveBalanceVO(pk_org, regvos);
		if(MapUtils.isEmpty(balanceMap)){
			return regvos;
		}
		//离职再入职人员，离职前存在已结算单据，入职后休假类型相同时，销假查询不到该条休假登记
		List<LeaveRegVO> resultRegvos= new ArrayList<LeaveRegVO>();
		  for(LeaveRegVO regvo: regvos){
		   LeaveBalanceVO balanceVO = balanceMap.get(regvo.getPk_psnorg()+regvo.getPk_leavetype()+regvo.getYearmonth());
		   if(balanceVO!=null&&balanceVO.getIssettlement().booleanValue())//过滤离职再入职人员已结算休假单据
		    continue;
		   resultRegvos.add(regvo);
		//   if(balanceVO==null)
		//   if(balanceVO.getIssettlement().booleanValue()||balanceVO.getIsuse().booleanValue())
		  }
		  if(CollectionUtils.isEmpty(resultRegvos))
		   return null;
		  return resultRegvos.toArray(new LeaveRegVO[0]);

//		List<LeaveRegVO> resultRegvos= Arrays.asList(regvos);
//		for(LeaveRegVO regvo: regvos){
//			LeaveBalanceVO balanceVO = balanceMap.get(regvo.getPk_psnorg()+regvo.getPk_leavetype()+regvo.getYearmonth());
//			if(balanceVO==null)
//				continue;
//			if(balanceVO.getIssettlement().booleanValue()||balanceVO.getIsuse().booleanValue())
//				resultRegvos.remove(regvo);
//		}
//		return resultRegvos.toArray(new LeaveRegVO[0]);
	}
	
	public  AggLeaveoffVO[] queryByCond(String pk_org, String pk_psndoc,
			FromWhereSQL fromWhereSQL, Object extraConds,boolean blApproveSite)throws BusinessException {
//		if(extraConds == null)
//			extraConds = TAPFBillQueryParams.getDefaultParams(blApproveSite);
//		if(!(extraConds instanceof PFQueryParams))
//			return null;
//		String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, LeaveoffVO.getDefaultTableName());
//		String strNormalSQL =
//			HrPfHelper.getQueryCondition(AggLeaveoffVO.class, alias, blApproveSite, pk_org, blApproveSite?((PFQueryParams)extraConds).getBillState():((TAPFBillQueryParams)extraConds).getStateCode());
//
//		if(!StringUtils.isEmpty(pk_psndoc))//人员主键不空，表示是适用于员工自助
//			strNormalSQL = SQLHelper.appendExtraCond(strNormalSQL, alias+"."+LeaveoffVO.PK_PSNDOC+"='"+pk_psndoc+"'");
//		     
//
//		String dateFilter = blApproveSite?TaNormalQueryUtils.getApproveDatePeriod(HrPfHelper.getFlowBizItf(AggLeaveoffVO.class),alias, ((PFQueryParams)extraConds).getApproveDateParam(),((PFQueryParams)extraConds).getBillState())
//				: TaNormalQueryUtils.getDateScopeSql(alias,(TAPFBillQueryParams)extraConds);
//
//		strNormalSQL = SQLHelper.appendExtraCond(strNormalSQL, dateFilter);
//
//		//加上pk_org 要不然在切换组织时候也会将不属于本组织的单据查询出来
//		String othercond = "";
//		if(StringUtils.isNotEmpty(pk_org))
//			othercond=" and "+alias+".pk_org='"+ pk_org+"' " ;
//		
//		String order = " order by " + LeaveoffVO.APPLY_DATE + " desc, " + LeaveoffVO.BILL_CODE;
//		if (fromWhereSQL == null || fromWhereSQL.getWhere() == null)
//			return getServiceTemplate().queryByCondition(AggLeaveoffVO.class, strNormalSQL+ othercond +order);
//		// 添加FromWhereSQL部分条件
//		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, LeaveoffVO.getDefaultTableName(),
//				new String[]{LeaveoffVO.PK_LEAVEOFF}, null, null, null, null);
//		strNormalSQL += " and " + LeaveoffVO.PK_LEAVEOFF + " in ( " + sql + " )" + othercond+order;
		String strNormalSQL=getSQLCondByFromWhereSQL(pk_org, pk_psndoc,fromWhereSQL, extraConds, blApproveSite);
		AggLeaveoffVO[] aggVOs =  getServiceTemplate().queryByCondition(AggLeaveoffVO.class, strNormalSQL);
		return aggVOs;
	}
	@Override
	public LeaveRegVO[] getRegVos4Hrss(String pkPsnjob)
			throws BusinessException {
		PsnJobVO psnjobvo=getServiceTemplate().queryByPk(PsnJobVO.class, pkPsnjob);
		return getRegVos4LeaveOff(psnjobvo.getPk_hrorg(),pkPsnjob,null);
	}
	@Override
	public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkMutextWhenSave(
			AggLeaveoffVO aggvo) throws BusinessException {
		LeaveoffVO vo=aggvo.getLeaveoffVO();
		String pk_org = vo.getPk_org();
		ILeaveRegisterQueryMaintain regQuery = NCLocator.getInstance().lookup(ILeaveRegisterQueryMaintain.class);
		LeaveRegVO regvo = regQuery.queryByPk(vo.getPk_leavereg());
		//销假的校验方法和登记的一致
		regvo.setLeavebegindate(vo.getLeavebegindate());
		regvo.setLeavebegintime(vo.getLeavebegintime());
		regvo.setLeaveenddate(vo.getLeaveenddate());
		regvo.setLeaveendtime(vo.getLeaveendtime());
		
		if(!StringUtil.isEmpty(regvo.getWaperiod())){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
					"leave", "2leaveoffExt-00001")
			/*
			 * @res 已結薪的假勤不能銷假
			 */);
		}
		
		LeaveRegVO[] regvos = new LeaveRegVO[]{regvo};
		return BillValidatorAtServer.checkLeave(pk_org, regvos);
	}
	@Override
	public String[] queryPKsByFromWhereSQL(LoginContext context,
			FromWhereSQL fromWhereSQL, Object etraConds,
			boolean approveSite) throws BusinessException {
		String cond = getSQLCondByFromWhereSQL(context.getPk_org(),null, fromWhereSQL, etraConds, approveSite);
		String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL,LeaveoffVO.getDefaultTableName());
		List<String> result = excuteQueryPksBycond(cond, alias);
		return CollectionUtils.isEmpty(result) ? null : (String[])result.toArray(new String[0]);
	}
	@Override
	public AggLeaveoffVO[] queryByPks(String[] pks)
			throws BusinessException {
		if(ArrayUtils.isEmpty(pks))
			return null;
		String order = " order by " + LeaveoffVO.APPLY_DATE + " desc, " + LeaveoffVO.BILL_CODE;
		String insql=LeaveoffVO.PK_LEAVEOFF+" in "+MDDAOUtil.getInSql(pks);
		return getServiceTemplate().queryByCondition(AggLeaveoffVO.class, insql+order);
//		return getServiceTemplate().queryByPks(AggLeaveoffVO.class, paramArrayOfString);
	}
	
	private String getSQLCondByFromWhereSQL(String pk_org, String pk_psndoc,
			FromWhereSQL fromWhereSQL, Object extraConds,boolean blApproveSite)
			throws BusinessException{
		if(extraConds == null)
			extraConds = TAPFBillQueryParams.getDefaultParams(blApproveSite);
		if(!(extraConds instanceof PFQueryParams))
			return null;
		String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, LeaveoffVO.getDefaultTableName());
		String strNormalSQL =
			HrPfHelper.getQueryCondition(AggLeaveoffVO.class, alias, blApproveSite, pk_org, blApproveSite?((PFQueryParams)extraConds).getBillState():((TAPFBillQueryParams)extraConds).getStateCode());

		if(!StringUtils.isEmpty(pk_psndoc))//人员主键不空，表示是适用于员工自助
			strNormalSQL = SQLHelper.appendExtraCond(strNormalSQL, alias+"."+LeaveoffVO.PK_PSNDOC+"='"+pk_psndoc+"'");
		     

		String dateFilter = blApproveSite?TaNormalQueryUtils.getApproveDatePeriod(HrPfHelper.getFlowBizItf(AggLeaveoffVO.class),alias, ((PFQueryParams)extraConds).getApproveDateParam(),((PFQueryParams)extraConds).getBillState())
				: TaNormalQueryUtils.getDateScopeSql(alias,(TAPFBillQueryParams)extraConds);

		strNormalSQL = SQLHelper.appendExtraCond(strNormalSQL, dateFilter);

		//加上pk_org 要不然在切换组织时候也会将不属于本组织的单据查询出来
		String othercond = "";
		if(StringUtils.isNotEmpty(pk_org))
			othercond=" and "+alias+".pk_org='"+ pk_org+"' " ;
		
		String order = " order by " + LeaveoffVO.APPLY_DATE + " desc, " + LeaveoffVO.BILL_CODE;
		if (fromWhereSQL == null || fromWhereSQL.getWhere() == null)
			return strNormalSQL+ othercond +order;
		// 添加FromWhereSQL部分条件
		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, LeaveoffVO.getDefaultTableName(),
				new String[]{LeaveoffVO.PK_LEAVEOFF}, null, null, null, null);
		strNormalSQL += " and " + LeaveoffVO.PK_LEAVEOFF + " in ( " + sql + " )" + othercond+order;
		return strNormalSQL;
	}
	
	private List<String> excuteQueryPksBycond(String cond, String alias)
			throws BusinessException{
		String sql = "select "+(StringUtils.isEmpty(alias)?"":alias+".")+LeaveoffVO.PK_LEAVEOFF+
				" from "+LeaveoffVO.getDefaultTableName()+" "+(StringUtils.isEmpty(alias)?"":alias);
		if(!StringUtils.isEmpty(cond))
			sql = sql +" where "+ cond;        
		List<String> result = (List<String>) new BaseDAO().executeQuery(sql, new ColumnListProcessor());//主键集合
		return result;
	}
	
}
