package nc.impl.ta.overtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.ArrayHelper;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.DataPermissionUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.ta.IOvertimeApplyQueryMaintain;
import nc.itf.ta.IOvertimeManageService;
import nc.itf.ta.IOvertimeQueryService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.generator.SequenceGenerator;
import nc.md.data.access.NCObject;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeGenVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.uap.pf.PFBatchExceptionInfo;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 加班服务组件实现
 * @author zengcheng
 *
 */
public class OvertimeServiceImpl implements IOvertimeQueryService,IOvertimeManageService {
	private SimpleDocServiceTemplate serviceTemplate;
	@Override
	public UFLiteralDate queryBelongToDate(OvertimeCommonVO vo)
			throws BusinessException {
		String pk_org = vo.getPk_org();
		//取当前组织所有班次信息
		Map<String, ShiftVO> shiftMap = ShiftServiceFacade.queryShiftVOMapByHROrg(pk_org);
		TimeRuleVO rimeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<UFLiteralDate, String> dateOrgMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
			queryDateJobOrgMap(vo.getPk_psndoc(), vo.getBegindate().getDateBefore(2), vo.getEnddate().getDateAfter(2));

		Map<UFLiteralDate, TimeZone> dateTimeZoneMap=
			nc.vo.ta.pub.CommonMethods.createDateTimeZoneMap(dateOrgMap, rimeRuleVO.getTimeZoneMap());
		return new OvertimeDAO().getBelongDate(vo, shiftMap, dateTimeZoneMap);
	}

	@Override
	public Map<String, Map<UFLiteralDate, OvertimeRegVO[]>> queryOvertimeVOsByCondDate(
			String pk_org, String psnCond, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<UFLiteralDate, OvertimeRegVO[]> queryOvertimeVOsByPsnDate(
			String pk_org, String pk_psndoc, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, OvertimeCommonVO[]> queryAllSuperVOExcNoPassByCondDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OvertimeCommonVO[] queryAllSuperVOExcNoPassByPsn(String pk_org,
			String pk_psndoc) throws BusinessException {
		return queryAllSuperVOExcNoPassByPsnDate(pk_org, pk_psndoc, null, null);
	}

	@Override
	public OvertimeCommonVO[] queryAllSuperVOExcNoPassByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		Map<String, OvertimeCommonVO[]> map = queryAllSuperVOExcNoPassByPsndocInSQLDate(pk_org, "'"+pk_psndoc+"'", beginDate, endDate);
		if(MapUtils.isEmpty(map))
			return null;
		return map.get(pk_psndoc);
	}
	

	@Override
	public Map<String, OvertimeRegVO[]> queryAllSuperVOIncEffectiveByCondDate(
			String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OvertimeRegVO[] queryAllSuperVOIncEffectiveByPsn(String pk_org,
			String pk_psndoc) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OvertimeRegVO[] queryAllSuperVOIncEffectiveByPsnDate(String pk_org,
			String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, OvertimeRegVO[]> queryAllSuperVOIncEffectiveByPsndocsDate(
			String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(StringUtils.isBlank(pk_org) || ArrayUtils.isEmpty(pk_psndocs)){
			return null;
		}
		//根据pk_psndocs的数量构建pk_psndocs串或者临时表
		InSQLCreator isc = new InSQLCreator();
		try{
			return queryAllSuperVOIncEffectiveByPsndocInSQLDate(pk_org, isc.getInSQL(pk_psndocs), beginDate, endDate);
		}finally{
			isc.clear();
		}
	}

	@Override
	public Map<String, OvertimeRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return BillMethods.queryRegVOMapByPsndocInSQLAndDateScope(OvertimeRegVO.class, pk_org, psndocInSQL, beginDate, endDate);
	}
	
	//MOD James
	public Map<String, OvertimeRegVO[]> queryAllSuperVOIncEffectiveByPsndocInSQLDateWithApprovedDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return BillMethods.queryRegVOMapByPsndocInSQLAndDateScope(OvertimeRegVO.class, pk_org, psndocInSQL, beginDate, endDate," "+OvertimeRegVO.APPROVE_TIME +"<'"+endDate.getDateAfter(1).toString()+"' ");
	}

	@Override
	public Map<String, OvertimeCommonVO[]> queryAllSuperVOExcNoPassByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		// 首先查询出所有的申请单(除去nopass的)
		Map<String, OvertimebVO[]> approveMap = BillMethods.queryApproveBodyVOMapByPsndocInSQLAndDateScopeExcNoPass(OvertimebVO.class, pk_org, psndocInSQL, beginDate, endDate);
		//然后查询出所有的登记单
		Map<String, OvertimeRegVO[]> regMap = BillMethods.queryRegVOMapByPsndocInSQLAndDateScope(OvertimeRegVO.class, pk_org, psndocInSQL, beginDate, endDate,OvertimeRegVO.BILLSOURCE+"="+ICommonConst.BILL_SOURCE_REG);
		//然后查询出所有的加班单据生成单
		Map<String, OvertimeGenVO[]> genMap = BillMethods.queryRegVOMapByPsndocInSQLAndDateScope(OvertimeGenVO.class, pk_org, psndocInSQL, beginDate, endDate);
		//最后合并这三个map
		Map<String, OvertimeCommonVO[]> retMap =  nc.vo.ta.pub.CommonMethods.mergeGroupedTimeScopeMap(OvertimeCommonVO.class, approveMap, regMap);
		return nc.vo.ta.pub.CommonMethods.mergeGroupedTimeScopeMap(OvertimeCommonVO.class, retMap, genMap);
	}
	
	@Override
	public OvertimeCommonVO[] queryIntersectionBills(OvertimeCommonVO bill)
			throws BusinessException {
		// 首先查询出与此单据有交集的流程中申请单子表
		OvertimebVO[] approveVOs = BillMethods.queryIntersetionApproveBodyVOsByPsndocAndTimeScopeExcNoPass(OvertimebVO.class, bill.getPk_org(), bill);
		//然后查询出所有的登记单
		OvertimeRegVO[] regVOs = BillMethods.queryIntersetionRegVOsByPsndocAndTimeScope(OvertimeRegVO.class, bill.getPk_org(), bill);
		//然后查询出所有的未生效加班单据生成单
		OvertimeGenVO[] genVOs = BillMethods.queryIntersetionRegVOsByPsndocAndTimeScope(OvertimeGenVO.class, bill.getPk_org(), bill, OvertimeGenVO.ISEFFECT+"='N' ");
		//最后返回合并后的数组
		OvertimeCommonVO[] retVOs = (OvertimeCommonVO[])ArrayHelper.addAll(approveVOs, regVOs, OvertimeCommonVO.class);
		return (OvertimeCommonVO[])ArrayHelper.addAll(retVOs, genVOs, OvertimeCommonVO.class);
	}

	@Override
	public Map<String, OvertimeCommonVO[]> queryIntersectionBillsMap(
			OvertimeCommonVO[] bills) throws BusinessException {
		if(ArrayUtils.isEmpty(bills))
			return null;
		String pk_org = bills[0].getPk_org();
		// 首先查询出与此单据有交集的流程中申请单子表
		OvertimebVO[] approveVOs = BillMethods.queryIntersetionApproveBodyVOsByPsndocAndTimeScopeExcNoPass(OvertimebVO.class, pk_org, bills);
		//然后查询出所有的登记单
		OvertimeRegVO[] regVOs = BillMethods.queryIntersetionRegVOsByPsndocAndTimeScope(OvertimeRegVO.class, pk_org, bills);
		//然后查询出所有的未生效加班单据生成单
		OvertimeGenVO[] genVOs = BillMethods.queryIntersetionRegVOsByPsndocAndTimeScope(OvertimeGenVO.class, pk_org, bills, "maintable."+OvertimeGenVO.ISEFFECT+"='N' ");
		//最后返回合并后的数组
		OvertimeCommonVO[] retVOs = (OvertimeCommonVO[]) ArrayHelper.addAll(approveVOs, ArrayHelper.addAll(regVOs, genVOs, OvertimeCommonVO.class), OvertimeCommonVO.class);
		return CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_PSNDOC, retVOs);
	}
	
	@Deprecated
	public Map<String, OvertimeCommonVO[]> queryIntersectionBillsMap1(
			OvertimeCommonVO[] bills) throws BusinessException {
		if(ArrayUtils.isEmpty(bills))
			return null;
		Map<String, OvertimeCommonVO[]> retMap = new HashMap<String, OvertimeCommonVO[]>();
		for(OvertimeCommonVO bill:bills){
			OvertimeCommonVO[] intersectionBills = queryIntersectionBills(bill);
			if(ArrayUtils.isEmpty(intersectionBills))
				continue;
			OvertimeCommonVO[] existBills = retMap.get(bill.getPk_psndoc());
			retMap.put(bill.getPk_psndoc(), (OvertimeCommonVO[])ArrayHelper.addAll(intersectionBills, existBills, OvertimeCommonVO.class));
		}
		return retMap;
	}

	@Override
	public void clearOvertimeBelongData() throws BusinessException {
		BaseDAO dao = new BaseDAO();
		//不用truncate的原因是truncate可能会删除其他事务的数据
		dao.executeUpdate("delete from tbm_overtimebelong");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createOvertimeBelongData(OvertimeRegVO[] overtimeBills,
			Map<String, UFLiteralDate> overtimeBelongDateMap)
			throws BusinessException {
		if(ArrayUtils.isEmpty(overtimeBills) || MapUtils.isEmpty(overtimeBelongDateMap)){
			return;
		}
		String insert = "insert into tbm_overtimebelong(pk_otbelong,pk_group,pk_org,pk_otreg,pk_psndoc,belongtodate,overtimebegintime,overtimeendtime) "+
		"values(?,?,?,?,?,?,?,?)";
		JdbcSession session = null;
		BaseDAO dao = new BaseDAO();
		InSQLCreator isc = null;
		SequenceGenerator sg = new SequenceGenerator();
		try {
			isc = new InSQLCreator();
			String inSQL = isc.getInSQL(overtimeBills, OvertimeRegVO.PK_PSNJOB);
			PsnJobVO[] psnjobVOs = (PsnJobVO[]) CommonUtils.toArray(PsnJobVO.class, dao.retrieveByClause(PsnJobVO.class, PsnJobVO.PK_PSNJOB+" in("+inSQL+")"));
			Map<String, PsnJobVO> psnjobMap = nc.hr.utils.CommonUtils.toMap(PsnJobVO.PK_PSNJOB, psnjobVOs);
			Map<String, TimeZone> timeZoneMap = new HashMap<String, TimeZone>();//<pk_org,timezone>
			session = new JdbcSession();
			ITimeRuleQueryService timeruleService = NCLocator.getInstance().lookup(ITimeRuleQueryService.class);
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			for(OvertimeRegVO vo : overtimeBills){
				SQLParameter paras = new SQLParameter();
				UFLiteralDate belongtoDate = overtimeBelongDateMap.get(vo.getPrimaryKey());
				if(belongtoDate == null){
					continue;
				}
				String pk = sg.generate();
				String pk_org = psnjobMap.get(vo.getPk_psnjob()).getPk_org();//业务单元
				//业务单元的时区
				TimeZone timeZone = timeZoneMap.get(pk_org);
				if(timeZone==null){
					timeZone = timeruleService.queryTimeZone(pk_org);
					timeZoneMap.put(pk_org, timeZone);
				}
				paras.addParam(pk);
				paras.addParam(pk_group);
				paras.addParam(vo.getPk_org());
				paras.addParam(vo.getPrimaryKey());
				paras.addParam(vo.getPk_psndoc());
				paras.addParam(belongtoDate.toString());
				paras.addParam(vo.getOvertimebegintime().toStdString(timeZone));
				paras.addParam(vo.getOvertimeendtime().toStdString(timeZone));
				
				session.addBatch(insert, paras);
			}
			session.executeBatch();
			
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			if(e instanceof BusinessException)
				throw (BusinessException)e;
			throw new BusinessException(e.getMessage(), e);
		} finally {
			if(isc!=null)
				isc.clear();
			session.closeAll();
		}
	}
	
	public SimpleDocServiceTemplate getServiceTemplate() {
		if(serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate("hrtaovertime");
		}
		return serviceTemplate;
	}

	@Override
	//自助用批审
	public PfProcessBatchRetObject approveValidate(
			AggregatedValueObject[] aggvos, LoginContext context,
			String operateCode, String mdOperateCode, String resourceCode)
			throws BusinessException {
		
		ArrayList<AggregatedValueObject> al = new ArrayList<AggregatedValueObject>();
		PFBatchExceptionInfo errInfo = new PFBatchExceptionInfo();
	    String checkLength=null;
	    IOvertimeApplyQueryMaintain overtimeQuery = NCLocator.getInstance().lookup(IOvertimeApplyQueryMaintain.class);
	      for(int i=0;i<aggvos.length;i++){
	    	  //获取子表信息
	    	  List<OvertimebVO> subVOs = new ArrayList<OvertimebVO>();
	    		AggregatedValueObject dbVO = getServiceTemplate().queryByPk(AggOvertimeVO.class, aggvos[i].getParentVO().getPrimaryKey(), true);
	            if (dbVO == null)
	            {
	            	errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6001pf", "06001pf0053")
	                /* @res "单据不存在" */);
	                continue;
	            }
	            if (!checkDataPermission(operateCode, mdOperateCode, resourceCode, dbVO))
	            {
	            	errInfo.putErrorMessage(i, aggvos[i], ResHelper.getString("6001pf", "06001pf0054")
	                /* @res "没有权限审批单据" */);
	                continue;
	            }
	            
	            NCObject dbObj = NCObject.newInstance(dbVO);
	            
	            IFlowBizItf flowBiz = dbObj.getBizInterface(IFlowBizItf.class);
	            
	            
	            if (!ArrayUtils.contains(new int[]{IPfRetCheckInfo.COMMIT, IPfRetCheckInfo.GOINGON}, flowBiz.getApproveStatus()))
	            {
	            	errInfo.putErrorMessage(i, dbVO, ResHelper.getString("6001pf", "06001pf0055")
	                /* @res "单据的状态不为“已提交”或“审批中”" */);
	                continue;
	            }
	            if (!isDirectApprove(flowBiz) && !isCheckman(flowBiz))
	            {
	            	errInfo.putErrorMessage(i, dbVO, ResHelper.getString("6001pf", "06001pf0056")
	                /* @res "您不是单据的当前审批人" */);
	                continue;
	            }
	    	  
	    	OvertimehVO headVO = ((AggOvertimeVO) aggvos[i]).getOvertimehVO();
	    	OvertimebVO[] thisVOs = ((AggOvertimeVO) aggvos[i]).getOvertimebVOs();
	    	for(OvertimebVO subVO:thisVOs){
	    		subVO.setPk_overtimetype(headVO.getPk_overtimetype());
	    		subVO.setPk_psndoc(headVO.getPk_psndoc());
	    		subVO.setPk_psnjob(headVO.getPk_psnjob());
	    		subVO.setPk_overtimetypecopy(headVO.getPk_overtimetypecopy());
	    		subVOs.add(subVO);
	    	}
	    	// 校验加班时长是否超过考勤期间内最大时长限制
	    	OvertimebVO[] subvos = subVOs.toArray(new OvertimebVO[0]);
			String pk_org = context.getPk_org();
			checkLength = overtimeQuery.checkOvertimeLength(pk_org, subvos);
	    	if(!StringUtils.isBlank(checkLength)){
	    		errInfo.putErrorMessage(i, aggvos[i],checkLength);//加班时长超过考勤期间内最大时长限制,不能审批
	    		continue;
	    	}
	    	//加班校验标识
			String checkIsNeed = overtimeQuery.checkIsNeed(pk_org, subvos);
	    	if(StringUtils.isNotBlank(checkIsNeed)){
	    		errInfo.putErrorMessage(i, aggvos[i],checkIsNeed);
	    		continue;
	    	}
	    	//加班类别和假日规则中定义的是否一致
	    	String holidayMsg = overtimeQuery.checkOverTimeHolidayMsg(pk_org, subvos);
	    	if(StringUtils.isNotBlank(holidayMsg)){
	    		errInfo.putErrorMessage(i, aggvos[i],holidayMsg);
	    		continue;
	    	}
	    	al.add(aggvos[i]);
	    	}
	     return new PfProcessBatchRetObject(al.toArray(new AggOvertimeVO[0]),errInfo);
	}	

	/**
	 * 检查操作权限
	 * 
	 * @param operateCode
	 * @param mdOperateCode
	 * @param resourceCode
	 * @param aggVO
	 * @return boolean
	 * @throws BusinessException
	 */
	private boolean checkDataPermission(String operateCode, String mdOperateCode, String resourceCode, AggregatedValueObject aggVO)
			throws BusinessException
	{
		if (StringUtils.isBlank(operateCode) && StringUtils.isBlank(mdOperateCode) || StringUtils.isBlank(resourceCode))
		{
			return true;
		}
	
		boolean blHasDataPermission = true;
	
		String resDataId = aggVO.getParentVO().getPrimaryKey();
		if (!StringUtils.isBlank(mdOperateCode))
		{
			blHasDataPermission = DataPermissionUtils.isUserhasPermissionByMetaDataOperation(resourceCode, resDataId, mdOperateCode);
		}
		else
		{
			blHasDataPermission = DataPermissionUtils.isUserhasPermission(resourceCode, resDataId, operateCode);
		}
	
		return blHasDataPermission;
	}
	
	private boolean isDirectApprove(IFlowBizItf itf) throws BusinessException
	{
		String strBillType = StringUtils.isBlank(itf.getTranstype()) ? itf.getBilltype() : itf.getTranstype();
		return !getIPFWorkflowQry().isApproveFlowStartup(itf.getBillId(), strBillType);
	}
	private IPFWorkflowQry getIPFWorkflowQry()
	{
	
		return NCLocator.getInstance().lookup(IPFWorkflowQry.class);
	}
	
	private boolean isCheckman(IFlowBizItf itf) throws BusinessException
	{
		String strBillType = StringUtils.isBlank(itf.getTranstype()) ? itf.getBilltype() : itf.getTranstype();
		// 如果是需要审批的，需要根据当前的审批人来决定是否可以修改单据
		return getIPFWorkflowQry().isCheckman(itf.getBillId(), strBillType, PubEnv.getPk_user());
	}
	
	@Override
	public OvertimeCommonVO[] processJobOrg(OvertimeCommonVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		InSQLCreator isc = new InSQLCreator();
		try{
			String cond = PsnJobVO.PK_PSNJOB + " in (" + isc.getInSQL(vos, OvertimeCommonVO.PK_PSNJOB) + ") ";
			PsnJobVO[] psnjobs = (PsnJobVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, PsnJobVO.class, cond);
			if(ArrayUtils.isEmpty(psnjobs))
				return vos;
			Map<String, PsnJobVO> psnjobmap = CommonUtils.toMap(PsnJobVO.PK_PSNJOB, psnjobs);
			for(OvertimeCommonVO vo:vos){
				PsnJobVO psnJobVO = psnjobmap.get(vo.getPk_psnjob());
				vo.setPk_joborg(psnJobVO==null?null:psnJobVO.getPk_org());
			}
			return vos;
		}finally{
			if(isc!=null)
				isc.clear();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public OvertimeRegVO[] queryRegByPsnAndDateScope(String pk_org,String pk_psndoc,String tbmYear,String tbmMonth,String pk_timeitem) throws BusinessException {
		PeriodVO period = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryByYearMonth(pk_org, tbmYear, tbmMonth);
		UFLiteralDate begindate = period.getBegindate();
		UFLiteralDate enddate = period.getEnddate();
		String condition = OvertimeRegVO.PK_PSNDOC + " =? and " + OvertimeRegVO.OVERTIMEBEGINDATE + " <=? and " + 
			OvertimeRegVO.OVERTIMEENDDATE+ " >=? and " + OvertimeRegVO.PK_OVERTIMETYPE + " =? and " + OvertimeRegVO.PK_ORG + " =? ";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_psndoc);
		para.addParam(enddate.toString());
		para.addParam(begindate.toString());
		para.addParam(pk_timeitem);
		para.addParam(pk_org);
		OvertimeRegVO[] results = (OvertimeRegVO[]) CommonUtils.toArray(OvertimeRegVO.class, new BaseDAO().retrieveByClause(OvertimeRegVO.class, condition, para));
		return results;
	}

	@Override
	public Map<String, OvertimeRegVO[]> queryAllSuperVOByApproveDateByPsndocInSQLDate(
			String pk_org, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		String cond =OvertimeRegVO.PK_ORG+"=? and dr='0' and "+
				OvertimeRegVO.PK_PSNDOC+" in ("+psndocInSQL+") and "+OvertimeRegVO.APPROVE_TIME+"<=? and "+
				OvertimeRegVO.APPROVE_TIME+">=? and " + OvertimeRegVO.OVERTIMEBEGINDATE+"<(select begindate from tbm_period where begindate<=approve_time and enddate>=approve_time and pk_org='"+pk_org+"')";
				SQLParameter para = new SQLParameter();
				para.addParam(pk_org);
				para.addParam(endDate.getDateAfter(1).toString());
				para.addParam(beginDate.toString());
				OvertimeRegVO[] results = (OvertimeRegVO[]) CommonUtils.toArray(OvertimeRegVO.class, new BaseDAO().retrieveByClause(OvertimeRegVO.class, cond, para));
				return nc.hr.utils.CommonUtils.group2ArrayByField(OvertimeRegVO.PK_PSNDOC, results);
	}
}
