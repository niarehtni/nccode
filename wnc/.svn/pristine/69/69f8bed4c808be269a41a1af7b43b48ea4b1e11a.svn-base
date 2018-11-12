package nc.impl.ta.period;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.bd.baseservice.IBaseServiceConst;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.om.IAOSQueryService;
import nc.itf.ta.IPeriodManageService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.generator.SequenceGenerator;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.bill.DateScopeCheckResult;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.util.AuditInfoUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;

public class PeriodServiceImpl implements IPeriodQueryService,IPeriodManageService {

	@Override
	public void checkDateScope(String pk_org, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		PeriodVO beginPeriod = queryByDate(pk_org, beginDate);
		if(beginPeriod==null)
			throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1627")
/*@res "开始日期没有所属期间!"*/);
		if(beginPeriod.getSealflag()!=null&&beginPeriod.getSealflag().booleanValue())
			throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1628")
/*@res "开始日期所在期间已封存!"*/);
		PeriodVO endPeriod = queryByDate(pk_org, endDate);
		if(endPeriod==null)
			throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1629")
/*@res "结束日期没有所属期间!"*/);
		if(endPeriod.getSealflag()!=null&&endPeriod.getSealflag().booleanValue())
			throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1630")
/*@res "结束日期所在期间已封存!"*/);
	}

	@Override
	public void checkDateB4Modify(String pk_org, UFLiteralDate[] dates) throws BusinessException {
		if(ArrayUtils.isEmpty(dates))
			return;
		Arrays.sort(dates);
		UFLiteralDate beginDate = dates[0];
		UFLiteralDate endDate = dates[dates.length-1];

		PeriodVO beginPeriod = queryByDate(pk_org, beginDate);
		if(beginPeriod==null)
			throw new BusinessException(ResHelper.getString("6017hrta","0hrta0051")
/*@res "日期超出了的考勤范围（期间）！"*/);
		if(beginPeriod.getSealflag()!=null&&beginPeriod.getSealflag().booleanValue())
			throw new BusinessException(ResHelper.getString("6017hrta","0hrta0052")
/*@res "日期所在考勤期间已封存！"*/);
		PeriodVO endPeriod = queryByDate(pk_org, endDate);
		if(endPeriod==null)
			throw new BusinessException(ResHelper.getString("6017hrta","0hrta0051")
/*@res "日期超出了的考勤范围（期间）！"*/);
		if(endPeriod.getSealflag()!=null&&endPeriod.getSealflag().booleanValue())
			throw new BusinessException(ResHelper.getString("6017hrta","0hrta0052")
/*@res "日期所在考勤期间已封存！"*/);
	}

	@Override
	public boolean checkDateScopeReturnsBoolean(String pk_org,
			UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		PeriodVO beginPeriod = queryByDate(pk_org, beginDate);
		if(beginPeriod==null)
			return false;
		if(beginPeriod.getSealflag()!=null&&beginPeriod.getSealflag().booleanValue())
			return false;
		PeriodVO endPeriod = queryByDate(pk_org, endDate);
		if(endPeriod==null)
			return false;
		if(endPeriod.getSealflag()!=null&&endPeriod.getSealflag().booleanValue())
			return false;
		return true;
	}
	@Override
	public PeriodVO queryByDate(String pk_org, UFLiteralDate date)
			throws BusinessException {
		return new PeriodDAO().queryByDate(pk_org, date);
	}

	@Override
	public PeriodVO queryByYearMonth(String pk_org, String year, String month)
			throws BusinessException {
		return new PeriodDAO().queryByYearMonth(pk_org, year, month);
	}

	@Override
	public PeriodVO queryNextPeriod(String pk_org, String year, String month)
			throws BusinessException {
		PeriodVO vo = queryByYearMonth(pk_org, year, month);
		if(vo==null)
			return null;
		return new PeriodMaintainImpl().queryNextPeriod(vo);
	}

	@Override
	public PeriodVO queryNextPeriod(String pk_org, UFLiteralDate date)
			throws BusinessException {
		PeriodVO vo = queryByDate(pk_org, date);
		if(vo==null)
			return null;
		return new PeriodMaintainImpl().queryNextPeriod(vo);
	}

	@Override
	public PeriodVO queryPreviousPeriod(String pk_org, String year, String month)
			throws BusinessException {
		PeriodVO vo = queryByYearMonth(pk_org, year, month);
		if(vo==null)
			return null;
		return new PeriodMaintainImpl().queryPreviousPeriod(vo);
	}

	@Override
	public PeriodVO queryPreviousPeriod(String pk_org, UFLiteralDate date)
			throws BusinessException {
		PeriodVO vo = queryByDate(pk_org, date);
		if(vo==null)
			return null;
		return new PeriodMaintainImpl().queryPreviousPeriod(vo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String[] queryPeriodYearsByOrg(String pk_org)
			throws BusinessException {
		String sql = "select "+PeriodVO.TIMEYEAR+" from "+PeriodVO.getDefaultTableName()+" where "+IBaseServiceConst.PK_ORG_FIELD+"='"+pk_org+"' group by "+ PeriodVO.TIMEYEAR+" order by "+PeriodVO.TIMEYEAR;
		List<String> list = (ArrayList<String>) new BaseDAO().executeQuery(sql, new ColumnListProcessor());
		if(CollectionUtils.isEmpty(list))
			return null;
		return list.toArray(new String[0]);
	}

	@Override
	public PeriodVO[] queryPeriodsByDateScope(String pk_org,
			UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		return new PeriodDAO().queryPeriodsByDateScope(pk_org, beginDate, endDate);
	}

	@Override
	public Map<String, String[]> queryPeriodYearAndMonthByOrg(String pk_org)
			throws BusinessException {
		Map<String, String[]> periodMap = new HashMap<String, String[]>();
		String[] years =  queryPeriodYearsByOrg(pk_org);
		if(ArrayUtils.isEmpty(years))
			return null;
		for(String year:years){
			PeriodVO[] periodvos = new PeriodDAO().queryByYear(pk_org, year);
			if(ArrayUtils.isEmpty(periodvos))
				continue;
			String[] months = new String[periodvos.length];
			for(int i = 0; i<periodvos.length; i++){
				months[i] = periodvos[i].getTimemonth();
			}
			Arrays.sort(months);
			periodMap.put(year, months);
		}
		return periodMap;
	}

	@Override
	public PeriodVO[] queryByYear(String pk_org, String year)
			throws BusinessException {
		return new PeriodDAO().queryByYear(pk_org, year);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PeriodVO queryCurPeriod(String pk_org) throws BusinessException {
		String cond = IBaseServiceConst.PK_ORG_FIELD+"=? and "+PeriodVO.CURFLAG+"='Y'";
		String orderBy = PeriodVO.TIMEYEAR+","+PeriodVO.TIMEMONTH;
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		BaseDAO dao = new BaseDAO();
		PeriodVO[] periodVOs = (PeriodVO[]) CommonUtils.toArray(PeriodVO.class, dao.retrieveByClause(PeriodVO.class, cond, orderBy,para));
		if(!ArrayUtils.isEmpty(periodVOs))
			return periodVOs[0];
		//如果没有当期的期间，则有两种可能：一是组织内没有期间，二是系统产生了错误：第一个未封存的期间应该就是当期
		//如果是第一种，则直接返回null；如果是第二种，则要执行补救措施：将第一个未封存的期间设置为当期
		cond = IBaseServiceConst.PK_ORG_FIELD+"=?";
		periodVOs = (PeriodVO[]) CommonUtils.toArray(PeriodVO.class, dao.retrieveByClause(PeriodVO.class, cond, orderBy,para));
		if(ArrayUtils.isEmpty(periodVOs))
			return null;
		for(PeriodVO vo:periodVOs){
			if(vo.getSealflag()!=null&&vo.getSealflag().booleanValue())
				continue;
			vo.setCurflag(UFBoolean.TRUE);
			AuditInfoUtil.updateData(vo);
			dao.updateVO(vo);
			//初始化月报
			new PeriodMaintainImpl().initMonthData(vo);
			return vo;
		}
		return null;
	}

	@Override
	public PeriodVO queryPreviousPeriod(String pk_org) throws BusinessException {
		PeriodVO curPeriod = queryCurPeriod(pk_org);
		return queryPreviousPeriod(pk_org, curPeriod.getTimeyear(), curPeriod.getTimemonth());
	}

	@Override
	public void checkBeforeDeleteTBMPsnodc(String pk_org, String[] pkTbmpsndocs)
			throws BusinessException {
		if(ArrayUtils.isEmpty(pkTbmpsndocs))
			return;
		//要求被删除的考勤档案的日期范围与被封存的考勤期间无交集
		PeriodVO[] allSealedPeriodVOs = new PeriodDAO().querySealedPeriodsByOrg(pk_org);
		if(ArrayUtils.isEmpty(allSealedPeriodVOs))
			return;
		//查出数据库中的考勤档案
		TBMPsndocVO[] tbmpsndocVOs = CommonUtils.queryByPks(TBMPsndocVO.class, pkTbmpsndocs);
		List<String> sealedPeriodPsnList = new ArrayList<String>();//存储考勤档案所在期间已封存的人员主键的list
		for(TBMPsndocVO psndocVO:tbmpsndocVOs){
			if(!ArrayUtils.isEmpty(DateScopeUtils.intersectionDateScopes(new TBMPsndocVO[]{psndocVO}, allSealedPeriodVOs))){
				sealedPeriodPsnList.add(psndocVO.getPk_psndoc());
			}
		}
		if(sealedPeriodPsnList.size()==0)
			return;
		throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1631"
/*@res "下列人员的考勤档案所在考勤期间已经封存,不能删除:{0}。可以将考勤期间解封后再删除。"*/, getPsnNames(sealedPeriodPsnList)));
	}


	@Override
	public void checkDateScope(String pk_org, IDateScope[] dateScopes)
			throws BusinessException {
		if(ArrayUtils.isEmpty(dateScopes))
			return;
		PeriodVO[] allPeriodVOs = new PeriodDAO().queryPeriodsByOrg(pk_org);
		if(ArrayUtils.isEmpty(allPeriodVOs)){
			String orgName=MultiLangHelper.getName(NCLocator.getInstance().lookup(IAOSQueryService.class).queryByPK(pk_org));
			throw new BusinessException(ResHelper.getString("6017basedoc", "06017basedoc1861"/*@res"组织{0}未设置考勤期间"*/,orgName));
//			throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1632")
//			/*@res "未定义考勤期间!"*/);
		}
		IDateScope[] dateScopeHaveNotPeriod = DateScopeUtils.minusDateScopes(dateScopes, allPeriodVOs);
		if(!ArrayUtils.isEmpty(dateScopeHaveNotPeriod)){
			IDateScope[] allScope = DateScopeUtils.mergeDateScopes(dateScopes);
			String allScopeDesc = allScope[0].getBegindate()+" - "+allScope[allScope.length-1].getEnddate();
			String periodScopeDesc = allPeriodVOs[0].getBegindate()+" - "+allPeriodVOs[allPeriodVOs.length-1].getEnddate();
			throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1633"/*@res "日期范围【{0}】超过了考勤期间的范围【{1}】!"*/, allScopeDesc,periodScopeDesc));
		}
		//过滤出已经封存了的期间
		List<PeriodVO> sealedPeriodList = new ArrayList<PeriodVO>();
		for(PeriodVO periodVO:allPeriodVOs){
			if(periodVO.getSealflag()!=null&&periodVO.getSealflag().booleanValue())
				sealedPeriodList.add(periodVO);
		}
		if(sealedPeriodList.size()==0)
			return;
		IDateScope[] intersectionScopes = DateScopeUtils.intersectionDateScopes(dateScopes, sealedPeriodList.toArray(new PeriodVO[0]));
		if(!ArrayUtils.isEmpty(intersectionScopes))
			throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1634")/*@res "日期范围所在的考勤期间已封存!"*/);
	}

	@Override
	public void checkBeforeInsertTBMPsndoc(SuperVO tbmPsndocVO)
			throws BusinessException {
		if(!(tbmPsndocVO instanceof TBMPsndocVO))
			throw new IllegalArgumentException("the parameter must be type of TBMPsndocVO!");
		TBMPsndocVO psndocVO =(TBMPsndocVO)tbmPsndocVO;
		checkBeforeInsertTBMPsndoc(psndocVO.getPk_org(), new TBMPsndocVO[]{psndocVO});

	}

	public void checkBeforeInsertTBMPsndoc(PsndocVO[] psndocvos,SuperVO tbmPsndocVO)
			throws BusinessException {

		String pk_org = ((TBMPsndocVO) tbmPsndocVO).getPk_org();
		TBMPsndocVO[] tbmPsndocVOs = new TBMPsndocVO[]{(TBMPsndocVO)tbmPsndocVO};
		if(ArrayUtils.isEmpty(tbmPsndocVOs))
			return;
		PeriodVO[] allPeriodVOs = new PeriodDAO().queryPeriodsByOrg(pk_org);
		if(ArrayUtils.isEmpty(allPeriodVOs)) {
			String orgName=MultiLangHelper.getName(NCLocator.getInstance().lookup(IAOSQueryService.class).queryByPK(pk_org));
			throw new BusinessException(ResHelper.getString("6017basedoc", "06017basedoc1861"/*@res"组织{0}未设置考勤期间"*/,orgName));
//			throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1632")
//			/*@res "未定义考勤期间!"*/);
		}
		List<PeriodVO> sealedPeriodList = new ArrayList<PeriodVO>();//已封存的考勤期间
		for(PeriodVO periodVO:allPeriodVOs){
			if(periodVO.getSealflag()!=null&&periodVO.getSealflag().booleanValue())
				sealedPeriodList.add(periodVO);
		}
		PeriodVO[] sealedPeriodVOs = sealedPeriodList.size()==0?null:sealedPeriodList.toArray(new PeriodVO[0]);
		UFLiteralDate periodBeginDate = allPeriodVOs[0].getBegindate();
		List<String> noPeriodPsnList = new ArrayList<String>();//开始日期无考勤期间的人员主键的list
		List<String> sealedPeriodPsnList = new ArrayList<String>();//考勤档案与已封存的考勤期间有交集的人员主键的list
//		for(SuperVO vo:tbmPsndocVOs){
//			TBMPsndocVO psndocVO = (TBMPsndocVO)vo;
//			String pk_psndoc = psndocVO.getPk_psndoc();
//			if(psndocVO.getBegindate().before(periodBeginDate)){
//				noPeriodPsnList.add(pk_psndoc);
//			}
//			//考勤档案与封存期间的交集
//			IDateScope[] intersectionScopes = DateScopeUtils.intersectionDateScopes(new TBMPsndocVO[]{psndocVO}, sealedPeriodVOs);
//			if(!ArrayUtils.isEmpty(intersectionScopes))
//				sealedPeriodPsnList.add(pk_psndoc);
//		}
		for(SuperVO vo:tbmPsndocVOs){
			TBMPsndocVO psndocVO = (TBMPsndocVO)vo;
			String pk_psndoc = psndocVO.getPk_psndoc();
			if(psndocVO.getBegindate().before(periodBeginDate)){
				noPeriodPsnList.add(pk_psndoc);
			}
			//考勤档案与封存期间的交集
			IDateScope[] intersectionScopes = DateScopeUtils.intersectionDateScopes(new TBMPsndocVO[]{psndocVO}, sealedPeriodVOs);
			if(!ArrayUtils.isEmpty(intersectionScopes) && !noPeriodPsnList.contains(pk_psndoc))//有交集，并且在noPeriodPsnList没出现过的
				sealedPeriodPsnList.add(pk_psndoc);
		}
		if(noPeriodPsnList.size()==0&&sealedPeriodPsnList.size()==0)
			return;
		String noPeriodMessage = null;

		HashMap<String ,String> nameMap = new HashMap<String,String>();
		if(psndocvos != null) {
			for(int i = 0;i < psndocvos.length;i++) {
				nameMap.put(psndocvos[i].getPk_psndoc(), psndocvos[i].getName());
			}
		}
		
		if(noPeriodPsnList.size()>0){
			String name = null;
			if(psndocvos.length > 0) {
				StringBuilder builder = new StringBuilder();
				for(String pk : noPeriodPsnList) {
					builder.append(nameMap.get(pk)).append(",");
				}
				if(builder.length() > 0)
					builder.deleteCharAt(builder.length() - 1);
				name = builder.toString();
			}
			noPeriodMessage = ResHelper.getString("6017basedoc","06017basedoc1635"
/*@res "下列人员的考勤档案开始日期无考勤期间,不能新增考勤档案:{0}"*/, (name == null || name.length() == 0) ? getPsnNames(noPeriodPsnList) : name);
		}


		String sealedPeriodMessage = null;
		if(sealedPeriodPsnList.size()>0){
			String name = null;
			if(psndocvos.length > 0) {
				StringBuilder builder = new StringBuilder();
				for(String pk : sealedPeriodPsnList) {
					builder.append(nameMap.get(pk)).append(",");
				}
				if(builder.length() > 0)
					builder.deleteCharAt(builder.length() - 1);
				name = builder.toString();
			}
			sealedPeriodMessage = ResHelper.getString("6017basedoc","06017basedoc1636"
/*@res "下列人员的考勤档案所在考勤期间已封存,不能新增考勤档案:{0}。可以将考勤期间解封后再新增。"*/, (name == null || name.length() == 0) ? getPsnNames(sealedPeriodPsnList) : name);
		}
		String message = noPeriodMessage==null?sealedPeriodMessage:(sealedPeriodMessage==null?noPeriodMessage:(noPeriodMessage+"\r\n"+sealedPeriodMessage));
		throw new ValidationException(message);

	
	}
	
	@Override
	public void checkBeforeInsertTBMPsndoc(String pk_org, SuperVO[] tbmPsndocVOs)
			throws BusinessException {
		if(ArrayUtils.isEmpty(tbmPsndocVOs))
			return;
		PeriodVO[] allPeriodVOs = new PeriodDAO().queryPeriodsByOrg(pk_org);
		if(ArrayUtils.isEmpty(allPeriodVOs)) {
			String orgName=MultiLangHelper.getName(NCLocator.getInstance().lookup(IAOSQueryService.class).queryByPK(pk_org));
			throw new BusinessException(ResHelper.getString("6017basedoc", "06017basedoc1861"/*@res"组织{0}未设置考勤期间"*/,orgName));
//			throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1632")
//			/*@res "未定义考勤期间!"*/);
		}
		List<PeriodVO> sealedPeriodList = new ArrayList<PeriodVO>();//已封存的考勤期间
		for(PeriodVO periodVO:allPeriodVOs){
			if(periodVO.getSealflag()!=null&&periodVO.getSealflag().booleanValue())
				sealedPeriodList.add(periodVO);
		}
		PeriodVO[] sealedPeriodVOs = sealedPeriodList.size()==0?null:sealedPeriodList.toArray(new PeriodVO[0]);
		UFLiteralDate periodBeginDate = allPeriodVOs[0].getBegindate();
		List<String> noPeriodPsnList = new ArrayList<String>();//开始日期无考勤期间的人员主键的list
		List<String> sealedPeriodPsnList = new ArrayList<String>();//考勤档案与已封存的考勤期间有交集的人员主键的list
//		for(SuperVO vo:tbmPsndocVOs){
//			TBMPsndocVO psndocVO = (TBMPsndocVO)vo;
//			String pk_psndoc = psndocVO.getPk_psndoc();
//			if(psndocVO.getBegindate().before(periodBeginDate)){
//				noPeriodPsnList.add(pk_psndoc);
//			}
//			//考勤档案与封存期间的交集
//			IDateScope[] intersectionScopes = DateScopeUtils.intersectionDateScopes(new TBMPsndocVO[]{psndocVO}, sealedPeriodVOs);
//			if(!ArrayUtils.isEmpty(intersectionScopes))
//				sealedPeriodPsnList.add(pk_psndoc);
//		}
		for(SuperVO vo:tbmPsndocVOs){
			TBMPsndocVO psndocVO = (TBMPsndocVO)vo;
			String pk_psndoc = psndocVO.getPk_psndoc();
			if(psndocVO.getBegindate().before(periodBeginDate)){
				noPeriodPsnList.add(pk_psndoc);
			}
			//考勤档案与封存期间的交集
			IDateScope[] intersectionScopes = DateScopeUtils.intersectionDateScopes(new TBMPsndocVO[]{psndocVO}, sealedPeriodVOs);
			if(!ArrayUtils.isEmpty(intersectionScopes) && !noPeriodPsnList.contains(pk_psndoc))//有交集，并且在noPeriodPsnList没出现过的
				sealedPeriodPsnList.add(pk_psndoc);
		}
		if(noPeriodPsnList.size()==0&&sealedPeriodPsnList.size()==0)
			return;
		String noPeriodMessage = null;

		if(noPeriodPsnList.size()>0){
			noPeriodMessage = ResHelper.getString("6017basedoc","06017basedoc1635"
/*@res "下列人员的考勤档案开始日期无考勤期间,不能新增考勤档案:{0}"*/, getPsnNames(noPeriodPsnList));
		}


		String sealedPeriodMessage = null;
		if(sealedPeriodPsnList.size()>0){
			sealedPeriodMessage = ResHelper.getString("6017basedoc","06017basedoc1636"
/*@res "下列人员的考勤档案所在考勤期间已封存,不能新增考勤档案:{0}。可以将考勤期间解封后再新增。"*/, getPsnNames(sealedPeriodPsnList));
		}
		String message = noPeriodMessage==null?sealedPeriodMessage:(sealedPeriodMessage==null?noPeriodMessage:(noPeriodMessage+"\r\n"+sealedPeriodMessage));
		throw new ValidationException(message);

	}

	@Override
	public void checkBeforeUpdateTBMPsndoc(String pk_org, SuperVO[] updatePsndocVOs)
			throws BusinessException {
		if(ArrayUtils.isEmpty(updatePsndocVOs))
			return;
		//查出修改前的考勤档案，装配成数组，且顺序与tbmPsndocVOs一致
		String[] pk_tbmpsndocs = SQLHelper.getStrArray(updatePsndocVOs, TBMPsndocVO.PK_TBM_PSNDOC);
		TBMPsndocVO[] dbVOs = CommonUtils.queryByPks(TBMPsndocVO.class, pk_tbmpsndocs);
		//查询出所有的考勤期间，以及封存了的考勤期间
		PeriodDAO dao = new PeriodDAO();
		PeriodVO[] allPeriodVOs = dao.queryPeriodsByOrg(pk_org);

		if(ArrayUtils.isEmpty(allPeriodVOs)){
			String orgName=MultiLangHelper.getName(NCLocator.getInstance().lookup(IAOSQueryService.class).queryByPK(pk_org));
			throw new BusinessException(ResHelper.getString("6017basedoc", "06017basedoc1861"/*@res"组织{0}未设置考勤期间"*/,orgName));
		}
		UFLiteralDate periodBeginDate = allPeriodVOs[0].getBegindate();
		PeriodVO[] allSealedPeriodVOs = dao.querySealedPeriodsByOrg(pk_org);
		List<String> beginDateNoPeriodPsnList = new ArrayList<String>();//考勤档案开始日期提前，且提前到了无考勤期间日期的人员list
		List<String> changedDateInSealedPeriodPsnList = new ArrayList<String>();//考勤档案变动日期与封存期间有交集的人员list

		for(int i=0;i<updatePsndocVOs.length;i++){
			TBMPsndocVO updateVO = (TBMPsndocVO)updatePsndocVOs[i];
			TBMPsndocVO dbVO = dbVOs[i];
			if(DateScopeUtils.equals(updateVO, dbVO))
				continue;
			//找出考勤档案的日期变化部分：
			//1.如果开始日期提前了，且提前到了第一个考勤期间之前，则抛异常
			//2.如果新增的日期部分与封存的考勤期间有交集，则抛异常；
			//3.如果减少的日期部分与封存的考勤期间有交集，则也抛异常
			if(updateVO.getBegindate().before(dbVO.getBegindate())&&updateVO.getBegindate().before(periodBeginDate))
				beginDateNoPeriodPsnList.add(updateVO.getPk_psndoc());
			TBMPsndocVO[] newDateScopes = new TBMPsndocVO[]{updateVO};
			TBMPsndocVO[] oldDateScopes = new TBMPsndocVO[]{dbVO};
			//新增的日期部分
			IDateScope[] addScopes = DateScopeUtils.minusDateScopes(newDateScopes,oldDateScopes);
			//减少的日期部分
			IDateScope[] subScopes = DateScopeUtils.minusDateScopes(oldDateScopes,newDateScopes);
			if(!ArrayUtils.isEmpty(DateScopeUtils.intersectionDateScopes(addScopes, allSealedPeriodVOs))||
					!ArrayUtils.isEmpty(DateScopeUtils.intersectionDateScopes(subScopes, allSealedPeriodVOs)))
				changedDateInSealedPeriodPsnList.add(updateVO.getPk_psndoc());
		}
		if(beginDateNoPeriodPsnList.size()==0&&changedDateInSealedPeriodPsnList.size()==0)
			return;
		String noPeriodMessage = null;
		if(beginDateNoPeriodPsnList.size()>0){
			noPeriodMessage = ResHelper.getString("6017basedoc","06017basedoc1637"
/*@res "下列人员修改后的考勤档案开始日期无考勤期间,不能修改考勤档案:{0}"*/, getPsnNames(beginDateNoPeriodPsnList));
		}
		String sealedPeriodMessage = null;
		if(changedDateInSealedPeriodPsnList.size()>0){
			sealedPeriodMessage = ResHelper.getString("6017basedoc","06017basedoc1638"
/*@res "下列人员考勤档案变动的日期所在考勤期间已封存,不能修改考勤档案:{0}。可以将考勤期间解封后再修改。"*/, getPsnNames(changedDateInSealedPeriodPsnList));
		}
		String message = noPeriodMessage==null?sealedPeriodMessage:(sealedPeriodMessage==null?noPeriodMessage:(noPeriodMessage+"\r\n"+sealedPeriodMessage));
		throw new ValidationException(message);
	}

	@SuppressWarnings("unchecked")
	private String getPsnNames(List<String> psndocList) throws BusinessException{
		//首先做过滤，将重复出现的人员去掉：
		List<String> filteredPsndocList = new ArrayList<String>();
		Set<String> psndocSet = new HashSet<String>();
		for(String pk_psndoc:psndocList){
			if(psndocSet.contains(pk_psndoc))
				continue;
			filteredPsndocList.add(pk_psndoc);
			psndocSet.add(pk_psndoc);
		}
		String[] filteredPsndocs = filteredPsndocList.toArray(new String[0]);
		InSQLCreator isc = new InSQLCreator();
		try {
			String inSQL = isc.getInSQL(filteredPsndocs);
			PsndocVO[] psndocVOs = (PsndocVO[]) CommonUtils.toArray(PsndocVO.class, new BaseDAO().retrieveByClause(PsndocVO.class, PsndocVO.PK_PSNDOC + " in("+ inSQL + ")")) ;
			if (ArrayUtils.isEmpty(psndocVOs))
				return null;
			//为了让提示语中的人员姓名保持在filteredPsndocs中的顺序，因此要按照filteredPsndocs的顺序来拼接。首先构造一个map
			Map<String, PsndocVO> psndocVOMap = new HashMap<String, PsndocVO>();
			for(PsndocVO psndocVO:psndocVOs){
				psndocVOMap.put(psndocVO.getPk_psndoc(), psndocVO);
			}
			StringBuilder sb = new StringBuilder();
			for(String pk_psndoc:filteredPsndocs){
				sb.append(psndocVOMap.get(pk_psndoc).getMultiLangName()).append(",");
			}
			if(sb.length()>0)
				sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		} finally{
			isc.clear();
		}
	}

	@Override
	public PeriodVO checkCurPeriod(String pk_org, String year, String month)
			throws BusinessException {
		PeriodVO curPeriod = queryCurPeriod(pk_org);
		if(curPeriod==null)
			throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1639")
/*@res "没有有效的当期!"*/);
		if(!year.equals(curPeriod.getTimeyear())||!month.equals(curPeriod.getTimemonth()))
			throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1640"
/*@res "期间{0}不是当期，当期是{1}!"*/, year+"-"+month,curPeriod.getTimeyear()+"-"+curPeriod.getTimemonth()));
		return curPeriod;
	}

	@Override
	public String[] queryPsnAllYears(String pk_psndoc) throws BusinessException {
		//查询此人员在多少个组织的考勤档案中呆过，然后查询每个组织中的最早考勤年和最晚考勤年，
		//然后取区间范围--min(所有组织的最早考勤年)到max(所有组织的最晚考勤年)
		String condition = " exists (select 1 from tbm_psndoc where tbm_psndoc.pk_psndoc='"+pk_psndoc+"' and tbm_psndoc.pk_org=tbm_period.pk_org "
			+ " and tbm_period.begindate<=tbm_psndoc.enddate and tbm_period.enddate>=tbm_psndoc.begindate)";
		PeriodVO[] periodVOs = CommonUtils.retrieveByClause(PeriodVO.class, condition);
		String[] years = StringPiecer.getStrArrayDistinct(periodVOs, PeriodVO.TIMEYEAR);
		if(ArrayUtils.isEmpty(years))
			return null;
		Arrays.sort(years);
		return years;
//		String cond = TBMPsndocVO.PK_PSNDOC+"=?";
//		String order = TBMPsndocVO.BEGINDATE;
//		SQLParameter para = new SQLParameter();
//		para.addParam(pk_psndoc);
//		BaseDAO dao = new BaseDAO();
//		TBMPsndocVO[] psndocVOs = (TBMPsndocVO[]) CommonUtils.toArray(TBMPsndocVO.class,
//				dao.retrieveByClause(TBMPsndocVO.class, cond, order,para));
//		if(ArrayUtils.isEmpty(psndocVOs))
//			return null;
//		//按pk_org分组
//		Map<String, TBMPsndocVO[]> psndocVOMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_ORG, psndocVOs);
//		String periodCond = PeriodVO.BEGINDATE+"<=? and "+PeriodVO.ENDDATE+">=?";
//		String periodOrder = PeriodVO.BEGINDATE;
//		String minYear = "9999";
//		String maxYear = "0000";
//		for(String pk_org:psndocVOMap.keySet()){
//			TBMPsndocVO[] vos = psndocVOMap.get(pk_org);
//			//需要查询在此组织内，和此人员的所有考勤档案有交集的考勤年，取一个最早年和一个最晚年
//			para.clearParams();
//			para.addParam(vos[vos.length-1].getEnddate());
//			para.addParam(vos[0].getBegindate());
//			PeriodVO[] periodVOs = (PeriodVO[]) CommonUtils.toArray(PeriodVO.class, dao.retrieveByClause(PeriodVO.class, periodCond, periodOrder, para));
//			if(ArrayUtils.isEmpty(periodVOs))
//				continue;
//			if(minYear.compareTo(periodVOs[0].getAccyear())>0)
//				minYear = periodVOs[0].getAccyear();
//			if(maxYear.compareTo(periodVOs[periodVOs.length-1].getAccyear())<0)
//				maxYear = periodVOs[periodVOs.length-1].getAccyear();
//		}
//		if(minYear.equals(maxYear))
//			return new String[]{minYear};
//		int intMinYear = Integer.parseInt(minYear);
//		int count = Integer.parseInt(maxYear)-intMinYear+1;
//		if(count==2)
//			return new String[]{minYear,maxYear};
//		String[] retYears = new String[count];
//		retYears[0]=minYear;
//		retYears[retYears.length-1]=maxYear;
//		for(int i=1;i<=retYears.length-2;i++){
//			retYears[i]=Integer.toString(intMinYear+i);
//		}
//		return retYears;
	}

	@Override
	public void clearDatePeriod() throws BusinessException {
		BaseDAO dao = new BaseDAO();
		//不用truncate的原因是truncate会清除掉其他事务的数据
//		dao.executeUpdate("truncate table tbm_dateperiod");
		dao.executeUpdate("delete from tbm_dateperiod where pk_user='"+PubEnv.getPk_user()+"'");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createDatePeriod(String pk_org, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		clearDatePeriod();
		String cond = PeriodVO.PK_ORG+"=? and "+PeriodVO.BEGINDATE+"<=? and "+PeriodVO.ENDDATE+">=?";
		SQLParameter paras = new SQLParameter();
		paras.addParam(pk_org);
		paras.addParam(endDate.toString());
		paras.addParam(beginDate.toString());
		PeriodVO[] periodVOs = (PeriodVO[]) CommonUtils.toArray(PeriodVO.class, new BaseDAO().retrieveByClause(PeriodVO.class, cond,PeriodVO.BEGINDATE, paras));
		if(ArrayUtils.isEmpty(periodVOs))
			return;
		//涉及到的所有考勤年度
		int firstYear = Integer.parseInt(periodVOs[0].getTimeyear())-1;//因为需要上年的信息，因此要把第一年往前推一年
		int lastYear = Integer.parseInt(periodVOs[periodVOs.length-1].getTimeyear());
		Map<String, UFLiteralDate[]> yearDateMap = new HashMap<String, UFLiteralDate[]>();//年度的开始日期结束日期的map，key是2011这种，value的第一个值是开始日期，第二个值是结束日期
		for(int year=firstYear;year<=lastYear;year++){
			PeriodVO[] yearPeriodVOs = queryByYear(pk_org, Integer.toString(year));
			if(ArrayUtils.isEmpty(yearPeriodVOs))
				continue;
			yearDateMap.put(Integer.toString(year), new UFLiteralDate[]{yearPeriodVOs[0].getBegindate(),yearPeriodVOs[yearPeriodVOs.length-1].getEnddate()});
		}
		//往前多查一个期间,因为要查上期的相关信息
		PeriodVO prePeriod = queryPreviousPeriod(pk_org, periodVOs[0].getTimeyear(), periodVOs[0].getTimemonth());
		if(prePeriod!=null){
			PeriodVO[] oldPeriodVOs = periodVOs;
			periodVOs = new PeriodVO[oldPeriodVOs.length+1];
			periodVOs[0]=prePeriod;
			System.arraycopy(oldPeriodVOs, 0, periodVOs, 1, oldPeriodVOs.length);
		}
		String insert = "insert into tbm_dateperiod(pk_dateperiod,pk_group,pk_org,calendar,tbmyear,tbmmonth,periodbegindate,periodenddate," +
				"yearbegindate,yearenddate,preyear,premonth,preperiodbegindate,preperiodenddate,preyearbegindate,preyearenddate,pk_user) "+
		"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		JdbcSession session = null;
		try {
			session = new JdbcSession();
			SequenceGenerator sg = new SequenceGenerator();
			String pk_user = PubEnv.getPk_user();
			for(int i=0;i<allDates.length;i++){
				UFLiteralDate date = allDates[i];
				PeriodVO periodVO = null;
				PeriodVO prePeriodVO = null;
				//for循环所有的期间，查找这个日期所属的期间，以及上期
				for(int j=0;j<periodVOs.length;j++){
					if(DateScopeUtils.contains(periodVOs[j], date)){
						periodVO = periodVOs[j];
						prePeriodVO = j>0?periodVOs[j-1]:null;
					}
				}
				if(periodVO==null)
					continue;
				String year = periodVO.getTimeyear();
				String month = periodVO.getTimemonth();
				SQLParameter para = new SQLParameter();
				para.addParam(sg.generate());
				para.addParam(pk_group);
				para.addParam(pk_org);
				para.addParam(date.toString());
				para.addParam(year);
				para.addParam(month);
				para.addParam(periodVO.getBegindate().toString());
				para.addParam(periodVO.getEnddate().toString());
				UFLiteralDate[] yearDates = yearDateMap.get(year);
				para.addParam(yearDates[0].toString());
				para.addParam(yearDates[1].toString());
				if(prePeriodVO!=null){
					para.addParam(prePeriodVO.getTimeyear());
					para.addParam(prePeriodVO.getTimemonth());
					para.addParam(prePeriodVO.getBegindate().toString());
					para.addParam(prePeriodVO.getEnddate().toString());
				}
				else{
					para.addNullParam(Types.CHAR);
					para.addNullParam(Types.CHAR);
					para.addNullParam(Types.CHAR);
					para.addNullParam(Types.CHAR);
				}
				String preYear = Integer.toString(Integer.parseInt(year)-1);
				UFLiteralDate[] preYearDates = yearDateMap.get(preYear);
				if(ArrayUtils.isEmpty(preYearDates)){
					para.addNullParam(Types.CHAR);
					para.addNullParam(Types.CHAR);
				}
				else{
					para.addParam(preYearDates[0].toString());
					para.addParam(preYearDates[1].toString());
				}
				para.addParam(pk_user);
				session.addBatch(insert, para);

			}
			session.executeBatch();
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		finally{
			session.closeAll();
		}
	}

	@Override
	public PeriodVO queryByDateWithCheck(String pk_org, UFLiteralDate date)
			throws BusinessException {
		PeriodVO periodVO = queryByDate(pk_org, date);
		if(periodVO!=null)
			return periodVO;
		throw new BusinessException(ResHelper.getString("6017basedoc","06017basedoc1641"
/*@res "{0}没有对应的考勤期间!"*/, date.toStdString()));
	}

	@Override
	public PeriodVO queryByAccYearMonth(String pk_org, String accYear,
			String accMonth) throws BusinessException {
		return new PeriodDAO().queryByAccYearMonth(pk_org, accYear, accMonth);
	}

	@Override
	public Map<UFLiteralDate, PeriodVO> queryPeriodMapByDateScope(String pk_org,
			UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		PeriodVO[] periodVOs = queryPeriodsByDateScope(pk_org, beginDate, endDate);
		if(ArrayUtils.isEmpty(periodVOs))
			return null;
		Map<UFLiteralDate, PeriodVO> map = new HashMap<UFLiteralDate, PeriodVO>();
		for(PeriodVO periodVO:periodVOs){
			UFLiteralDate[] allDates = CommonUtils.createDateArray(periodVO.getBegindate(), periodVO.getEnddate());
			for(UFLiteralDate date:allDates){
				map.put(date, periodVO);
			}
		}
		return map;
	}



	@Override
	public Map<UFLiteralDate, PeriodVO> queryPeriodMapByDateScopes(String pk_org,IDateScope[] dateScopes)throws BusinessException{
		if(ArrayUtils.isEmpty(dateScopes))
			return null;
		IDateScope[] mergedScopes = DateScopeUtils.mergeDateScopes(dateScopes);
		Map<UFLiteralDate, PeriodVO> map = null;
		for(IDateScope mergedScope:mergedScopes){
			Map<UFLiteralDate, PeriodVO> tempMap = queryPeriodMapByDateScope(pk_org, mergedScope.getBegindate(), mergedScope.getEnddate());
			if(MapUtils.isEmpty(tempMap))
				continue;
			if(MapUtils.isEmpty(map)){
				map = tempMap;
				continue;
			}
			map.putAll(tempMap);
		}
		return map;
	}

	@Override
	public Map<String, PeriodVO[]> queryByYear(String pk_org, String[] years)
			throws BusinessException {
		if(ArrayUtils.isEmpty(years))
			return null;
		return new PeriodDAO().queryByYear(pk_org, years);
	}

	@Override
	public Map<String, PeriodVO[]> queryByYear(String pk_org, String beginYear,
			String endYear) throws BusinessException {

		return new PeriodDAO().queryByYear(pk_org, beginYear,endYear);
	}

	@Override
	public DateScopeCheckResult checkDateScopeEnable(String pk_org,
			UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		DateScopeCheckResult result = new DateScopeCheckResult();
		PeriodVO beginPeriod = queryByDate(pk_org, beginDate);
		if(beginPeriod==null||beginPeriod.getSealflag()==null||beginPeriod.getSealflag().booleanValue()){
			result.setBeginDateEnable(false);
		}else{
			result.setBeginDateEnable(true);
		}
		PeriodVO endPeriod = queryByDate(pk_org, endDate);
		if(endPeriod==null||endPeriod.getSealflag()==null||endPeriod.getSealflag().booleanValue()){
			result.setEndDateEnable(false);
		}else{
			result.setEndDateEnable(true);
		}
		return result;
	}

}