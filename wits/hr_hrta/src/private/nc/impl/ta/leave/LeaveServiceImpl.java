package nc.impl.ta.leave;

import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ArrayHelper;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.ta.ILeaveQueryService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.PeriodServiceFacade;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leaveoff.LeaveoffVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.trade.pub.IBillStatus;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class LeaveServiceImpl implements ILeaveQueryService {

	@Override
	public LeaveRegVO[] queryByPsnLeaveTypePeriod(String pk_org,
			String pk_psnorg, String pk_leaveType, String year, String month,int leaveIndex)
			throws BusinessException {
		ITimeItemQueryService itemService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		TimeItemCopyVO typeVO = itemService.queryCopyTypesByDefPK(pk_org, pk_leaveType, TimeItemCopyVO.LEAVE_TYPE);
		return queryByPsnLeaveTypePeriod(pk_org, pk_psnorg, (LeaveTypeCopyVO)typeVO, year, month,leaveIndex);
	}
	@SuppressWarnings("unchecked")
	@Override
	public LeaveRegVO[] queryByPsnLeaveTypePeriod(String pk_org,
			String pk_psnorg, LeaveTypeCopyVO leaveTypeVO, String year,
			String month,int leaveIndex) throws BusinessException {
		if(leaveTypeVO==null)return null;
		int leavesetperiod = leaveTypeVO.getLeavesetperiod().intValue();
		boolean isYear = leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE
				// ssx added on 2018-03-16
				// for changes of start date of company age
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE;
		//
		String where=" pk_org=? and pk_leavetype=? and pk_psnorg=? ";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(leaveTypeVO.getPk_timeitem());
		para.addParam(pk_psnorg);
		if(leaveTypeVO.getIslactation()==null||!leaveTypeVO.getIslactation().booleanValue())
		{
			where+=" and leaveyear =? ";
			para.addParam(year);
		}

		if(!isYear){
			where+=" and leavemonth=? ";
			para.addParam(month);
		}
		where+=" and "+LeaveRegVO.LEAVEINDEX+"=? ";
		para.addParam(leaveIndex);
		return (LeaveRegVO[]) CommonUtils.toArray(LeaveRegVO.class, new BaseDAO().retrieveByClause(LeaveRegVO.class, where, para));
	}
	
	@SuppressWarnings("unchecked")
	public LeaveRegVO[] queryByPsnsLeaveTypePeriod(String pk_org,
			String[] pk_psnorgs, LeaveTypeCopyVO leaveTypeVO, String year,
			String month,int leaveIndex) throws BusinessException {
		if(leaveTypeVO==null||ArrayUtils.isEmpty(pk_psnorgs))
			return null;
		int leavesetperiod = leaveTypeVO.getLeavesetperiod().intValue();
		boolean isYear = leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE
				// ssx added on 2018-03-16
				// for changes of start date of company age
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE;
		//
		InSQLCreator isc = new InSQLCreator();
		try{
			
			String where=" pk_org=? and pk_leavetype=? and pk_psnorg in (" + isc.getInSQL(pk_psnorgs) + ") ";
			SQLParameter para = new SQLParameter();
			para.addParam(pk_org);
			para.addParam(leaveTypeVO.getPk_timeitem());
			if(leaveTypeVO.getIslactation()==null||!leaveTypeVO.getIslactation().booleanValue())
			{
				where+=" and leaveyear =? ";
				para.addParam(year);
			}
			
			if(!isYear){
				where+=" and leavemonth=? ";
				para.addParam(month);
			}
			where+=" and "+LeaveRegVO.LEAVEINDEX+"=? ";
			para.addParam(leaveIndex);
			return (LeaveRegVO[]) CommonUtils.toArray(LeaveRegVO.class, new BaseDAO().retrieveByClause(LeaveRegVO.class, where, para));
		}finally{
			if(isc != null)
				isc.clear();
		}
		
	}

	@Override
	public String[] queryOverToRestPeriod(String pk_org, UFDate busiDate)
			throws BusinessException {
		TimeItemCopyVO copyvo = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryCopyTypesByDefPK(pk_org, TimeItemCopyVO.LEAVETYPE_OVERTOREST, TimeItemCopyVO.LEAVE_TYPE);
		PeriodVO period = PeriodServiceFacade.queryByDate(pk_org, new UFLiteralDate(busiDate.toDate()));
		if(period==null||period.isSeal())
			throw new BusinessException(ResHelper.getString("6017leave","06017leave0224")
/*@res "ҵ���������������ڼ䲻���ڻ��ѷ�棡"*/);
		if(copyvo==null)
			throw new BusinessException(ResHelper.getString("6017leave","06017leave0248")
					/*@res "��ǰ��֯δ�����ݼ����--�Ӱ�ת����!"*/);
			//return null;
		if(copyvo.getLeavesetperiod().intValue()==TimeItemCopyVO.LEAVESETPERIOD_MONTH)
		{
			return new String[]{period.getTimeyear(),period.getTimemonth()};
		}
		return new String[]{period.getTimeyear()};
	}

	@Override
	public Map<String, LeaveCommonVO[]> queryAllSuperVOExcNoPassByCondDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return null;
	}

	@Override
	public LeaveCommonVO[] queryAllSuperVOExcNoPassByPsn(String pk_org,
			String pk_psndoc) throws BusinessException {
		return queryAllSuperVOExcNoPassByPsnDate(pk_org, pk_psndoc, null, null);
	}

	@Override
	public LeaveCommonVO[] queryAllSuperVOExcNoPassByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		Map<String, LeaveCommonVO[]> map = queryAllSuperVOExcNoPassByPsndocInSQLDate(pk_org, "'"+pk_psndoc+"'", beginDate, endDate);
		if(MapUtils.isEmpty(map))
			return null;
		return map.get(pk_psndoc);
	}

	@Override
	public Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByCondDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LeaveRegVO[] queryAllSuperVOIncEffectiveByPsn(String pk_org,
			String pk_psndoc) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LeaveRegVO[] queryAllSuperVOIncEffectiveByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, LeaveRegVO[]> queryAllLactationVOIncEffictiveByPsnDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByPsndocsDate(
			String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(StringUtils.isBlank(pk_org) || ArrayUtils.isEmpty(pk_psndocs)){
			return null;
		}
		//����pk_psndocs����������pk_psndocs��������ʱ��
		InSQLCreator isc = new InSQLCreator();
		try{
			return queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, isc.getInSQL(pk_psndocs), beginDate, endDate);
		}finally{
			isc.clear();
		}
	}

	@Override
	public Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return BillMethods.queryRegVOMapByPsndocInSQLAndDateScope(LeaveRegVO.class, pk_org, psndocInSQL, beginDate, endDate,SQLHelper.getBoolNullSql(LeaveRegVO.ISLACTATION));
	}
	
	//MOD James
	public Map<String, LeaveRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDateWithApprovedDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return BillMethods.queryRegVOMapByPsndocInSQLAndDateScope(LeaveRegVO.class, pk_org, psndocInSQL, beginDate, endDate, " "+LeaveRegVO.APPROVE_TIME +"<'"+endDate.getDateAfter(1).toString()+"' and " + SQLHelper.getBoolNullSql(LeaveRegVO.ISLACTATION));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, LeaveRegVO[]> queryAllLactationVOIncEffictiveByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		String cond =LeaveRegVO.PK_ORG+"=? and "+LeaveRegVO.ISLACTATION+"='Y' and "+
		LeaveRegVO.PK_PSNDOC+" in ("+psndocInSQL+") and "+LeaveRegVO.LEAVEBEGINDATE+"<=? and "+
		LeaveRegVO.LEAVEENDDATE+">=?";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(endDate.toString());
		para.addParam(beginDate.toString());
		LeaveRegVO[] results = (LeaveRegVO[]) CommonUtils.toArray(LeaveRegVO.class, new BaseDAO().retrieveByClause(LeaveRegVO.class, cond, para));
		return nc.hr.utils.CommonUtils.group2ArrayByField(LeaveRegVO.PK_PSNDOC, results);
	}

	@SuppressWarnings("unchecked")
	@Override
	public LeaveRegVO[] queryAllLactationVOIncEffictiveByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		String cond =LeaveRegVO.PK_ORG+"=? and "+LeaveRegVO.ISLACTATION+"='Y' and "+
		LeaveRegVO.PK_PSNDOC+" =? and "+LeaveRegVO.LEAVEBEGINDATE+"<=? and "+
		LeaveRegVO.LEAVEENDDATE+">=?";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(pk_psndoc);
		para.addParam(endDate.toString());
		para.addParam(beginDate.toString());
		LeaveRegVO[] results = (LeaveRegVO[]) CommonUtils.toArray(LeaveRegVO.class, new BaseDAO().retrieveByClause(LeaveRegVO.class, cond, para));
		return results;
	}


	/**
	 * 2013-08-06�޸ģ���Ϊ���ٵ�ԭ�����뵥�е����ݿɲ������ã���˸�Ϊ��ѯ���뵥���ٹ��˵�����ͨ���ģ���һ����ʹ�õǼǵ�����,�Ǽǵ����ù������뵫�е������ˡ�
	 */
	@Override
	public Map<String, LeaveCommonVO[]> queryAllSuperVOExcNoPassByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		// ���Ȳ�ѯ�����е����뵥(��ȥnopass���Լ������)
		Map<String, LeavebVO[]> approveMap = BillMethods.queryApproveBodyVOMapByPsndocInSQLAndDateScopeExcNoPass(LeavebVO.class, pk_org, psndocInSQL, beginDate, endDate, SQLHelper.getBoolNullSql(LeavehVO.ISLACTATION));
//		Ȼ���ѯ�����еĵǼǵ�(��ȥ��Դ�����뵥���Լ�����٣��������ȥ�Ļ�������ͨ���Ļᱻ��ѯ������)
//		Map<String, LeaveRegVO[]> regMap = BillMethods.queryRegVOMapByPsndocInSQLAndDateScope(LeaveRegVO.class, pk_org, psndocInSQL, beginDate, endDate,"("+SQLHelper.getBoolNullSql(LeavehVO.ISLACTATION)+") and "+LeaveRegVO.BILLSOURCE+"="+ICommonConst.BILL_SOURCE_REG);
		Map<String, LeaveRegVO[]> regMap = BillMethods.queryRegVOMapByPsndocInSQLAndDateScope(LeaveRegVO.class, pk_org, psndocInSQL, beginDate, endDate,null);
		//�ϲ�������map
		Map<String, LeaveCommonVO[]> leaveMap = nc.vo.ta.pub.CommonMethods.mergeGroupedTimeScopeMap(LeaveCommonVO.class, approveMap, regMap);
		//��ѯ��δ���������̣�ȥ������ͨ����δͨ���ģ����������뵥�������ٵ�û���ӱ�����˲������ݼٹ���һ�����뵥��ѯ��������Ҫ����дһ����
		Map<String, LeaveoffVO[]> leaveOffMap = BillMethods.queryApproveBodyVOMapByPsndocInSQLAndDateScopeExcNoPassForLeaveOff(LeaveoffVO.class, pk_org, psndocInSQL, beginDate, endDate,null);
		//���ϲ�������map
		return nc.vo.ta.pub.CommonMethods.mergeGroupedTimeScopeMap(LeaveCommonVO.class, leaveMap, leaveOffMap);
	}

	@Override
	public LeaveCommonVO[] queryIntersectionBills(LeaveCommonVO bill)
	throws BusinessException {
		// ���Ȳ�ѯ����˵����н��������������뵥�ӱ�
		LeavebVO[] approveVOs = BillMethods.queryIntersetionApproveBodyVOsByPsndocAndTimeScopeExcNoPass(LeavebVO.class, bill.getPk_org(), bill);
		//Ȼ���ѯ�����еĵǼǵ�
		LeaveRegVO[] regVOs = BillMethods.queryIntersetionRegVOsByPsndocAndTimeScope(LeaveRegVO.class, bill.getPk_org(), bill/**, LeaveRegVO.BILLSOURCE+"="+ICommonConst.BILL_SOURCE_REG*/);
		//��󷵻غϲ��������
		return (LeaveCommonVO[])ArrayHelper.addAll(approveVOs, regVOs, LeaveCommonVO.class);
	}

	@Override
	public Map<String, LeaveCommonVO[]> queryIntersectionBillsMap(
			LeaveCommonVO[] bills) throws BusinessException {
		if(ArrayUtils.isEmpty(bills))
			return null;
		String pk_org = bills[0].getPk_org();
		// ���Ȳ�ѯ����˵����н��������������뵥�ӱ�
		LeavebVO[] approveVOs = BillMethods.queryIntersetionApproveBodyVOsByPsndocAndTimeScopeExcNoPass(LeavebVO.class, pk_org, bills);
		//Ȼ���ѯ�����еĵǼǵ�
		LeaveRegVO[] regVOs = BillMethods.queryIntersetionRegVOsByPsndocAndTimeScope(LeaveRegVO.class, pk_org, bills);
		//��󷵻غϲ��������
		LeaveCommonVO[] retVOs = (LeaveCommonVO[])ArrayHelper.addAll(approveVOs, regVOs, LeaveCommonVO.class);
		return CommonUtils.group2ArrayByField(LeaveCommonVO.PK_PSNDOC, retVOs);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public LeavebVO[] queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(
			String pk_org, String pk_psnorg, LeaveTypeCopyVO leaveTypeVO,
			String year, String month, int leaveIndex) throws BusinessException {

		if(leaveTypeVO==null)return null;
		int leavesetperiod = leaveTypeVO.getLeavesetperiod().intValue();
		boolean isYear = leavesetperiod==LeaveTypeCopyVO.LEAVESETPERIOD_YEAR||leavesetperiod==LeaveTypeCopyVO.LEAVESETPERIOD_DATE;
		String where=" h.pk_org=? and h.pk_leavetype=? and h.pk_psnorg=? ";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(leaveTypeVO.getPk_timeitem());
		para.addParam(pk_psnorg);
		if(leaveTypeVO.getIslactation()==null||!leaveTypeVO.getIslactation().booleanValue())
		{
			where+=" and leaveyear =? ";
			para.addParam(year);
		}

		if(!isYear){
			where+=" and leavemonth=? ";
			para.addParam(month);
		}
		where+=" and "+LeaveRegVO.LEAVEINDEX+"=? and approve_state in("+IBillStatus.FREE+","+IBillStatus.COMMIT+","+IBillStatus.CHECKGOING+")";
		para.addParam(leaveIndex);
		String sql = "select b.*,h.pk_psndoc,h.pk_psnorg,h.pk_psnjob,h.pk_leavetype from "+LeavebVO.getDefaultTableName()+" b inner join " +
				LeavehVO.getDefaultTableName()+" h on b.pk_leaveh=h.pk_leaveh "+
				" where "+where;
		return (LeavebVO[]) CommonUtils.toArray(LeavebVO.class, (List<LeavebVO>)new BaseDAO().executeQuery(sql, para, new BeanListProcessor(LeavebVO.class)));

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public LeavebVO[] queryBeforePassWithoutNoPassByPsnsLeaveTypePeriod(
			String pk_org, String[] pk_psnorgs, LeaveTypeCopyVO leaveTypeVO,
			String year, String month, int leaveIndex) throws BusinessException {

		if(leaveTypeVO==null)return null;
		int leavesetperiod = leaveTypeVO.getLeavesetperiod().intValue();
		boolean isYear = leavesetperiod==LeaveTypeCopyVO.LEAVESETPERIOD_YEAR||leavesetperiod==LeaveTypeCopyVO.LEAVESETPERIOD_DATE;
		InSQLCreator isc = new InSQLCreator();
		try{
			String where=" h.pk_org=? and h.pk_leavetype=? and h.pk_psnorg in (" + isc.getInSQL(pk_psnorgs) + ") ";
			SQLParameter para = new SQLParameter();
			para.addParam(pk_org);
			para.addParam(leaveTypeVO.getPk_timeitem());
			if(leaveTypeVO.getIslactation()==null||!leaveTypeVO.getIslactation().booleanValue())
			{
				where+=" and leaveyear =? ";
				para.addParam(year);
			}
			
			if(!isYear){
				where+=" and leavemonth=? ";
				para.addParam(month);
			}
			where+=" and "+LeaveRegVO.LEAVEINDEX+"=? and approve_state in("+IBillStatus.FREE+","+IBillStatus.COMMIT+","+IBillStatus.CHECKGOING+")";
			para.addParam(leaveIndex);
			String sql = "select b.*,h.pk_psndoc,h.pk_psnorg,h.pk_psnjob,h.pk_leavetype from "+LeavebVO.getDefaultTableName()+" b inner join " +
			LeavehVO.getDefaultTableName()+" h on b.pk_leaveh=h.pk_leaveh "+
			" where "+where;
			return (LeavebVO[]) CommonUtils.toArray(LeavebVO.class, (List<LeavebVO>)new BaseDAO().executeQuery(sql, para, new BeanListProcessor(LeavebVO.class)));
		}finally{
			if(isc!=null)
				isc.clear();
		}

	}
	
	@Override
	public LeavebVO[] queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(
			String pk_org, String pk_psnorg, String pk_leaveType, String year,
			String month, int leaveIndex) throws BusinessException {
		ITimeItemQueryService itemService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) itemService.queryCopyTypesByDefPK(pk_org, pk_leaveType, TimeItemCopyVO.LEAVE_TYPE);
		return queryBeforePassWithoutNoPassByPsnLeaveTypePeriod(pk_org, pk_psnorg, typeVO, year, month, leaveIndex);
	}
	@Override
	public AggLeaveVO queryByPk(String key) throws BusinessException {
		return new LeaveApplyQueryMaintainImpl().queryByPk(key);
	}
	@SuppressWarnings("unchecked")
	@Override
	public LeaveRegVO[] queryLeaveRegByPsnAndDateScope(String pk_org,String pk_psndoc,String tbmYear,String tbmMonth,String pk_timeitem) throws BusinessException {
		PeriodVO period = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryByYearMonth(pk_org, tbmYear, tbmMonth);
		UFLiteralDate begindate = period.getBegindate();
		UFLiteralDate enddate = period.getEnddate();
		String condition = LeaveRegVO.PK_PSNDOC + " =? and " + LeaveRegVO.LEAVEBEGINDATE + " <=? and " + 
			LeaveRegVO.LEAVEENDDATE + " >=? and " + LeaveRegVO.PK_LEAVETYPE + " =? and " + LeaveRegVO.PK_ORG + " =? ";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_psndoc);
		para.addParam(enddate.toString());
		para.addParam(begindate.toString());
		para.addParam(pk_timeitem);
		para.addParam(pk_org);
		LeaveRegVO[] results = (LeaveRegVO[]) CommonUtils.toArray(LeaveRegVO.class, new BaseDAO().retrieveByClause(LeaveRegVO.class, condition, para));
		return results;
	}
	@Override
	public Map<String, LeaveRegVO[]> queryAllSuperVOByApproveDateByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		String cond =LeaveRegVO.PK_ORG+"=? and dr='0' and leavehour<>0 and "+
				LeaveRegVO.PK_PSNDOC+" in ("+psndocInSQL+") and ("+LeaveRegVO.APPROVE_TIME+"<? and "+
				LeaveRegVO.APPROVE_TIME+">=?) and "+LeaveRegVO.LEAVEBEGINDATE+ "<(select begindate from tbm_period where begindate<="+
				LeaveRegVO.APPROVE_TIME+" and enddate>="+LeaveRegVO.APPROVE_TIME+" and pk_org='"+pk_org+"')";
				SQLParameter para = new SQLParameter();
				para.addParam(pk_org);
				para.addParam(endDate.getDateAfter(1).toString());
				para.addParam(beginDate.toString());
				LeaveRegVO[] results = (LeaveRegVO[]) CommonUtils.toArray(LeaveRegVO.class, new BaseDAO().retrieveByClause(LeaveRegVO.class, cond, para));
				return nc.hr.utils.CommonUtils.group2ArrayByField(LeaveRegVO.PK_PSNDOC, results);
	}
}