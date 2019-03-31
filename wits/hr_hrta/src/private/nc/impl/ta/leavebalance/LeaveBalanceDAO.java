package nc.impl.ta.leavebalance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.bd.baseservice.IBaseServiceConst;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.generator.SequenceGenerator;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.SQLParamWrapper;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.vorg.AdminOrgVersionVO;
import nc.vo.vorg.DeptVersionVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class LeaveBalanceDAO {
	
	/**
	 * 将一批人员的在year年的入职年开始日期和结束日期插入临时表
	 * 要求psndocVOs的pk_psnorg没有重复的
	 * @param year
	 * @param psndocVOs
	 * @param hireDateMap
	 * @throws BusinessException 
	 */
	public void initHireDateTempTable(String year,TBMPsndocVO[] psndocVOs,Map<String, UFLiteralDate> hireDateMap) throws BusinessException{
		if(ArrayUtils.isEmpty(psndocVOs)||MapUtils.isEmpty(hireDateMap))
			return;
		String pk_group = psndocVOs[0].getPk_group();
		String pk_org = psndocVOs[0].getPk_org();
		
		clearTableData();

		String insertSQL = "insert into tbm_hiredate(pk_hiredate,"+IBaseServiceConst.PK_GROUP+","+
		IBaseServiceConst.PK_ORG+",pk_psndoc,pk_psnorg,curyear,hirebegindate,hireenddate,pk_user) "+
		"values(?,?,?,?,?,?,?,?,?)";
		JdbcSession session = null;
		LeaveBalanceMaintainImpl impl = new LeaveBalanceMaintainImpl();
		try {
			session = new JdbcSession();
			SequenceGenerator sg = new SequenceGenerator();
			String[] pks =sg.generate(psndocVOs.length);
			String pk_user=PubEnv.getPk_user();
			for(int i = 0;i<pks.length;i++){
				TBMPsndocVO tbmpsndocVO = psndocVOs[i];
				String pk_psnorg = tbmpsndocVO.getPk_psnorg();
				UFLiteralDate hireDate = hireDateMap.get(pk_psnorg);
				String hireYear = hireDate.toString().substring(0, 4);
				if(hireYear.compareTo(year)>0)
					continue;
				String pk=pks[i];
				String pk_psndoc = tbmpsndocVO.getPk_psndoc();
				UFLiteralDate beginDate = impl.getHireBeginDate(year, hireDate);
				UFLiteralDate endDate = impl.getHireEndDate(year, hireDate);
				SQLParameter para = new SQLParameter();
				para.addParam(pk);
				para.addParam(pk_group);
				para.addParam(pk_org);
				para.addParam(pk_psndoc);
				para.addParam(pk_psnorg);
				para.addParam(year);
				para.addParam(beginDate.toString());
				para.addParam(endDate.toString());
				para.addParam(pk_user);
				session.addBatch(insertSQL, para);
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
	
	public void clearTableData() throws DAOException{
		BaseDAO dao = new BaseDAO();
		//不用truncate的原因是truncate会清除其他事务的数据
//		dao.executeUpdate("truncate table tbm_hiredate");
		dao.executeUpdate("delete from tbm_hiredate");
	}
	
	@SuppressWarnings("unchecked")
	public LeaveBalanceVO[] queryByCondition4HireDateSettlementWithHireDateTempTable(String pk_org,LeaveTypeCopyVO typeVO, String year, 
			FromWhereSQL fromWhereSQL) throws DAOException{
		//需要查询组织和部门版本
		fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, "hiredate.hireenddate");
		String orgversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob."+PsnJobVO.PK_ORG_V+FromWhereSQLUtils.getAttPathPostFix());
		String deptversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob."+PsnJobVO.PK_DEPT_V+FromWhereSQLUtils.getAttPathPostFix());
		String[] extraSelFields = new String[]{
				"hiredate.hirebegindate",
				"hiredate.hireenddate",
				"leavebalance.pk_timeitem",
				"leavebalance.pk_leavebalance",
				"leavebalance.curyear",
				"leavebalance.translatein",
				"leavebalance.translateout",
				"leavebalance.transflag",
				"leavebalance.lastdayorhour",
				"leavebalance.curdayorhour",
				"leavebalance.realdayorhour",
				"leavebalance.restdayorhour",
				"leavebalance.yidayorhour",
				"leavebalance.freezedayorhour",
				"leavebalance.calculatetime",
				"leavebalance.issettlement",
				"leavebalance.settlementdate",
				"leavebalance.leaveindex",
				"leavebalance.isuse",
				"leavebalance.ts",
				"leavebalance.changelength",
				"leavebalance.salaryyear",
				"leavebalance.salarymonth",
				orgversionAlias+"."+AdminOrgVersionVO.PK_VID+" as "+LeaveBalanceVO.PK_ORG_V,
				deptversionAlias+"."+DeptVersionVO.PK_VID+" as "+LeaveBalanceVO.PK_DEPT_V
				};
		String[] extraJoins = 
			new String[]{
				" inner join tbm_hiredate hiredate on hiredate.pk_org={0}.pk_org and hiredate.pk_psnorg={0}.pk_psnorg ",
				" left join tbm_leavebalance leavebalance on leavebalance.pk_org={0}.pk_org and leavebalance.pk_psnorg={0}.pk_psnorg and "+
				" leavebalance.pk_org=? and leavebalance.pk_timeitem = ? and leavebalance.curyear=? "};
		String extraConds = "{0}.pk_org=? and {0}.begindate<=hiredate.hireenddate and {0}.enddate>=hiredate.hirebegindate and "+
		"not exists(select 1 from tbm_psndoc t where t.pk_psnorg=tbm_psndoc.pk_psnorg and t.pk_org=tbm_psndoc.pk_org and "+
		"t.begindate<=hiredate.hireenddate and t.enddate>=hiredate.hirebegindate and t.begindate>{0}.begindate)";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(typeVO.getPk_timeitem());
		para.addParam(year);
		para.addParam(pk_org);
		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, 
				TBMPsndocVO.getDefaultTableName(), null, extraSelFields, extraJoins, extraConds, null);
		LeaveBalanceVO[] vos =  (LeaveBalanceVO[]) CommonUtils.toArray(LeaveBalanceVO.class, (Collection)new BaseDAO().
				executeQuery(sql,para, new BeanListProcessor(
					LeaveBalanceVO.class)));
		if(!ArrayUtils.isEmpty(vos)){
			String pk_timeitem = typeVO.getPk_timeitem();
			int extendDateCounts = typeVO.getExtendDaysCount();
			for(LeaveBalanceVO vo:vos){
				vo.setPeriodbegindate(vo.getHirebegindate());
				vo.setPeriodenddate(vo.getHireenddate());
				vo.setPeriodextendenddate(extendDateCounts==0?vo.getHireenddate():vo.getHireenddate().getDateAfter(extendDateCounts));
				if(org.apache.commons.lang.StringUtils.isEmpty(vo.getPrimaryKey())){
					vo.setLeaveindex(1);
					vo.setPk_timeitem(pk_timeitem);
					vo.setCuryear(year);
					vo.setTranslatein(UFDouble.ZERO_DBL);
					vo.setTranslateout(UFDouble.ZERO_DBL);
					vo.setTransflag(UFBoolean.FALSE);
					vo.setCurdayorhour(UFDouble.ZERO_DBL);
					vo.setLastdayorhour(UFDouble.ZERO_DBL);
					vo.setRealdayorhour(UFDouble.ZERO_DBL);
					vo.setFreezedayorhour(UFDouble.ZERO_DBL);
					vo.setYidayorhour(UFDouble.ZERO_DBL);
					vo.setRestdayorhour(UFDouble.ZERO_DBL);
					vo.setIsabnormalset(UFBoolean.FALSE);
					vo.setIssettlement(UFBoolean.FALSE);
					vo.setIsuse(UFBoolean.FALSE);
					vo.setChangelength(UFDouble.ZERO_DBL);
				}
			}
		}
		return vos;
	}
	
	/**
	 * 关联考勤档案表，查出在beginDate和endDate之间有考勤档案的LeaveBalanceVO记录
	 * 关联条件是pk_psnorg,不是pk_psndoc
	 * 由于是考勤档案left join leavebalance，因此即使leavebalance中无记录，也会查出一条来。如果已经存在，则有几条就查询出几条
	 * @param pk_org
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @param pk_timeitem
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	public LeaveBalanceVO[] queryByDateScope(String pk_org,FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate,
			String pk_timeitem,String year,String month) throws BusinessException{
		//需要查询组织和部门版本
		fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, endDate.toStdString());
		String orgversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob."+PsnJobVO.PK_ORG_V+FromWhereSQLUtils.getAttPathPostFix());
		String deptversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob."+PsnJobVO.PK_DEPT_V+FromWhereSQLUtils.getAttPathPostFix());
		SQLParamWrapper wrapper = TBMPsndocSqlPiecer.selectJoinByPsnorgFieldAndDateArea(
				pk_org, 
				new String[]{TBMPsndocVO.PK_GROUP,TBMPsndocVO.PK_ORG,TBMPsndocVO.PK_PSNDOC,TBMPsndocVO.PK_PSNORG,TBMPsndocVO.PK_PSNJOB,TBMPsndocVO.PK_TBM_PSNDOC}, 
				new String[]{
					"tbm_leavebalance.pk_timeitem",
					"tbm_leavebalance.pk_leavebalance",
					"tbm_leavebalance.curyear",
					"tbm_leavebalance.curmonth",
					"tbm_leavebalance.translatein",
					"tbm_leavebalance.translateout",
					"tbm_leavebalance.transflag",
					"tbm_leavebalance.lastdayorhour",
					"tbm_leavebalance.curdayorhour",
					"tbm_leavebalance.realdayorhour",
					"tbm_leavebalance.restdayorhour",
					"tbm_leavebalance.yidayorhour",
					"tbm_leavebalance.freezedayorhour",
					"tbm_leavebalance.calculatetime",
					"tbm_leavebalance.issettlement",
					"tbm_leavebalance.settlementdate",
					"tbm_leavebalance.leaveindex",
					"tbm_leavebalance.isuse",
					"tbm_leavebalance.ts",
					"tbm_leavebalance.changelength",
					"tbm_leavebalance.salaryyear",
					"tbm_leavebalance.salarymonth",
					orgversionAlias+"."+AdminOrgVersionVO.PK_VID+" as "+LeaveBalanceVO.PK_ORG_V,
					deptversionAlias+"."+DeptVersionVO.PK_VID+" as "+LeaveBalanceVO.PK_DEPT_V
					}, 
				"tbm_leavebalance",
				"tbm_leavebalance.pk_org",
				"tbm_leavebalance.pk_psnorg" ,
				"tbm_leavebalance.pk_org=? and tbm_leavebalance.pk_timeitem = ? and tbm_leavebalance.curyear=? "+
				(month==null?" and tbm_leavebalance.curmonth is null ":("and tbm_leavebalance.curmonth=?")),//2014-04-26修改按年度结算的月份必须为空
				beginDate.toString(),
				endDate.toString(),
				null,
				false, 
				fromWhereSQL);
		BaseDAO dao = new BaseDAO();
		String sql = wrapper.getSql();
		SQLParameter para = wrapper.getParam();
		if(org.apache.commons.lang.StringUtils.isNotBlank(month))
			para.getParameters().add(0, month);
		para.getParameters().add(0,year);
		para.getParameters().add(0,pk_timeitem);
		para.getParameters().add(0,pk_org);
		LeaveBalanceVO[] vos =  (LeaveBalanceVO[]) CommonUtils.toArray(LeaveBalanceVO.class, (Collection)dao.
			executeQuery(sql,para, new BeanListProcessor(
				LeaveBalanceVO.class)));
		if(ArrayUtils.isEmpty(vos))
			return vos;
		//处理结算的部门版本信息
		List<LeaveBalanceVO> settlementList = new ArrayList<LeaveBalanceVO>();
		for(LeaveBalanceVO vo:vos){
			if(StringUtils.isBlank(vo.getPrimaryKey())){
				vo.setCuryear(year);
				vo.setCurmonth(month);
				vo.setPk_timeitem(pk_timeitem);
				vo.setLeaveindex(1);
				vo.setTranslatein(UFDouble.ZERO_DBL);
				vo.setTranslateout(UFDouble.ZERO_DBL);
				vo.setTransflag(UFBoolean.FALSE);
				vo.setCurdayorhour(UFDouble.ZERO_DBL);
				vo.setLastdayorhour(UFDouble.ZERO_DBL);
				vo.setRealdayorhour(UFDouble.ZERO_DBL);
				vo.setFreezedayorhour(UFDouble.ZERO_DBL);
				vo.setYidayorhour(UFDouble.ZERO_DBL);
				vo.setRestdayorhour(UFDouble.ZERO_DBL);
				vo.setIsabnormalset(UFBoolean.FALSE);
				vo.setIssettlement(UFBoolean.FALSE);
				vo.setIsuse(UFBoolean.FALSE);
				vo.setChangelength(UFDouble.ZERO_DBL);
			}
			if(vo.isSettlement()){
				settlementList.add(vo);
			}
		}
		//2013-03-07 上面关联了部门版本，但是若是一个期间内有两条记录，且部门不同的话，就都按最新的显示，
		//不妥当，主要在一条结算了另一条还没有结算的情况下发生 
		if(settlementList.size()<1)
			return vos;
		LeaveBalanceVO[] settvos = settlementList.toArray(new LeaveBalanceVO[0]);
		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(settvos, LeaveBalanceVO.PK_LEAVEBALANCE);
		String condition = LeaveBalanceVO.PK_LEAVEBALANCE + " in (" + insql + ") ";
		Collection sett = dao.retrieveByClause(LeaveBalanceVO.class, condition);
		if(CollectionUtils.isEmpty(sett))
			return vos;
		LeaveBalanceVO[] dbsettvos = (LeaveBalanceVO[]) sett.toArray(new LeaveBalanceVO[0]);
		Map<String, LeaveBalanceVO> settmap = CommonUtils.toMap(LeaveBalanceVO.PK_LEAVEBALANCE, dbsettvos);
		for(LeaveBalanceVO vo : settvos){
			LeaveBalanceVO dbvo = settmap.get(vo.getPk_leavebalance());
			if(dbvo==null||StringUtils.isBlank(dbvo.getPk_dept_v()))
				continue;
			vo.setPk_dept_v(dbvo.getPk_dept_v());
		}
		return vos;
	}
	
	/**
	 * 从数据库中同步数据
	 * @param vos 需要同步的vos，要求已经存在于数据库中
	 * @param whereSQL
	 */
	protected void syncFromDB4DataExists(LeaveBalanceVO[] vos)throws BusinessException{
		InSQLCreator isc = new InSQLCreator();
		try{
			String pkInSQL = isc.getInSQL(StringPiecer.getStrArray(vos, LeaveBalanceVO.PK_LEAVEBALANCE));
			LeaveBalanceVO[] dbVOs = CommonUtils.retrieveByClause(LeaveBalanceVO.class, LeaveBalanceVO.PK_LEAVEBALANCE+" in("+pkInSQL+")");
			Map<String, LeaveBalanceVO> dbVOMap = new HashMap<String, LeaveBalanceVO>();
			for(LeaveBalanceVO dbVO:dbVOs){
				dbVOMap.put(dbVO.getPk_leavebalance(), dbVO);
			}
			for(LeaveBalanceVO vo:vos){
				vo.sync(dbVOMap.get(vo.getPk_leavebalance()));
			}
		}finally {
			isc.clear();
		}
	}
	
	/**
	 * 从数据库中同步数据
	 * @param vos 需要同步的vos，不要求已经存在于数据库中
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	protected void syncFromDB(LeaveTypeCopyVO typeVO,String year,String month,LeaveBalanceVO[] vos) throws BusinessException{
		if(ArrayUtils.isEmpty(vos))
			return;
		String[] pk_psnorgs = SQLHelper.getStrArray(vos, LeaveBalanceVO.PK_PSNORG);
		InSQLCreator isc = null;
		SQLParameter para = new SQLParameter();
		String cond = "pk_org=? and pk_timeitem=? and "+LeaveBalanceVO.CURYEAR+"=? ";
		para.addParam(typeVO.getPk_org());
		para.addParam(typeVO.getPk_timeitem());
		para.addParam(year);
		if(typeVO.getLeavesetperiod().intValue()==TimeItemCopyVO.LEAVESETPERIOD_MONTH){
			cond+=" and "+LeaveBalanceVO.CURMONTH+"=? ";
			para.addParam(month);
		}
		LeaveBalanceVO[] dbVOs = null;
		try{
			isc = new InSQLCreator();
			cond+=" and "+LeaveBalanceVO.PK_PSNORG+" in("+isc.getInSQL(pk_psnorgs)+")";
			dbVOs = (LeaveBalanceVO[]) CommonUtils.toArray(LeaveBalanceVO.class, new BaseDAO().retrieveByClause(LeaveBalanceVO.class, cond, para));
		}
		finally{
			if(isc!=null)
				isc.clear();
		}
		if(ArrayUtils.isEmpty(dbVOs))
			return;
		//key是pk_psnorg+leaveindex
		Map<String, LeaveBalanceVO> dbVOMap = new HashMap<String, LeaveBalanceVO>();
		for(LeaveBalanceVO vo:dbVOs){
			dbVOMap.put(vo.getPk_psnorg()+vo.getLeaveindex(), vo);
		}
		for(int i=0;i<vos.length;i++){
			LeaveBalanceVO vo = vos[i];
			String uniqueKey = vo.getPk_psnorg()+vo.getLeaveindex();
			LeaveBalanceVO dbVO = dbVOMap.get(uniqueKey);
			if(dbVO!=null)
				vo.sync(dbVO);
		}
	}
	/**
	 * 确保vos中的人员组织关系在指定类别、指定年度(期间)、指定leaveindex下有一条唯一的记录
	 * 如果某条记录在数据库中已经存在，且已经结算，则用数据库中的记录来代替vos中的记录
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param month
	 * @param vos
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	protected void ensureData(String pk_org, LeaveTypeCopyVO typeVO,
			String year, String month, LeaveBalanceVO[] vos) throws BusinessException{
		String[] pk_psnorgs = SQLHelper.getStrArray(vos, LeaveBalanceVO.PK_PSNORG);
		BaseDAO dao = new BaseDAO();
		String cond = "pk_org=? and "+LeaveBalanceVO.PK_TIMEITEM+"=? "+
		"and "+LeaveBalanceVO.CURYEAR+"=? ";
		boolean isMonthSetPeirod = typeVO.getLeavesetperiod().intValue()==LeaveTypeCopyVO.LEAVESETPERIOD_MONTH;
		if(isMonthSetPeirod){
			cond+="and "+LeaveBalanceVO.CURMONTH+"=? ";
		}
		if(LeaveTypeCopyVO.LEAVESETPERIOD_YEAR == typeVO.getLeavesetperiod().intValue()){//2014-04-26修改按年度结算的月份必须为空
			cond+=" and "+LeaveBalanceVO.CURMONTH+" is null ";
		}
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		//确保vos中字段值的正确
		for(LeaveBalanceVO vo:vos){
			vo.setPk_group(pk_group);
			vo.setPk_org(pk_org);
			vo.setPk_timeitem(typeVO.getPk_timeitem());
			vo.setCuryear(year);
			vo.setCurmonth(isMonthSetPeirod?month:null);
		}
		InSQLCreator isc = null;
		LeaveBalanceVO[] result = null;
		String psnorgInSQL = null;
		try{
			isc = new InSQLCreator();
			psnorgInSQL = isc.getInSQL(pk_psnorgs);
			cond+=" and "+LeaveBalanceVO.PK_PSNORG+" in("+psnorgInSQL+")";
			SQLParameter para = new SQLParameter();
			para.addParam(pk_org);
			para.addParam(typeVO.getPk_timeitem());
			para.addParam(year);
			if(isMonthSetPeirod){
				para.addParam(month);
			}
			result = (LeaveBalanceVO[]) CommonUtils.toArray(LeaveBalanceVO.class, dao.retrieveByClause(LeaveBalanceVO.class, cond, para));
		}
		finally{
			if(isc!=null)
				isc.clear();
		}
		//找出数据库中没有的，进行insert，key是pk_psnorg+leaveindex
		Map<String, LeaveBalanceVO> map = new HashMap<String, LeaveBalanceVO>();
		if(!ArrayUtils.isEmpty(result))
			for(LeaveBalanceVO vo:result){
				map.put(vo.getPk_psnorg()+vo.getLeaveindex(), vo);
			}
		List<LeaveBalanceVO> insertList = new ArrayList<LeaveBalanceVO>();
		for(int i=0;i<vos.length;i++){
			LeaveBalanceVO vo = vos[i];
			String uniqueKey = vo.getPk_psnorg()+vo.getLeaveindex();
			if(!map.containsKey(uniqueKey)){
				insertList.add(vo);
				vo.setIssettlement(UFBoolean.FALSE);
				vo.setIsuse(UFBoolean.FALSE);
				vo.setIsabnormalset(UFBoolean.FALSE);
				vo.setTranslatein(vo.getTranslatein()==null?UFDouble.ZERO_DBL:vo.getTranslatein());
				vo.setTranslateout(vo.getTranslateout()==null?UFDouble.ZERO_DBL:vo.getTranslateout());
				vo.setTransflag(vo.getTransflag()==null?UFBoolean.FALSE:vo.getTransflag());
				vo.setLastdayorhour(UFDouble.ZERO_DBL);
				vo.setCurdayorhour(UFDouble.ZERO_DBL);
				vo.setYidayorhour(UFDouble.ZERO_DBL);
				vo.setRealdayorhour(UFDouble.ZERO_DBL);
				vo.setRestdayorhour(UFDouble.ZERO_DBL);
				vo.setFreezedayorhour(UFDouble.ZERO_DBL);
				vo.setChangelength(UFDouble.ZERO_DBL);
				continue;
			}
			//如果数据库中已有记录，且已结算，则用数据库的记录来替换数组中的vo
			LeaveBalanceVO dbVO = map.get(uniqueKey);
			vos[i].setPk_leavebalance(dbVO.getPk_leavebalance());
			if(dbVO.isSettlement())
				vos[i].sync(dbVO);
		}
		if(insertList.size()==0)
			return;
		String[] pks = dao.insertVOList(insertList);
		for(int i=0;i<pks.length;i++){
			insertList.get(i).setPrimaryKey(pks[i]);
		}
	}

	/**
	 * 查询假期计算数据
	 * @param pk_hrorg
	 * @param typeVO
	 * @param pk_psnorgs
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, List<LeaveBalanceVO>> queryLeaveBalanceMapByPsnOrgs(String pk_hrorg, LeaveTypeCopyVO typeVO, String[] pk_psnorgs, String year, String month) throws BusinessException {
		int leavesetPeriod = typeVO.getLeavesetperiod().intValue();
		InSQLCreator isc = new InSQLCreator();
		Map<String, List<LeaveBalanceVO>> balanceMap = new HashMap<String, List<LeaveBalanceVO>>();
		try {
			
			String cond = "pk_org=? and pk_timeitem=? and pk_psnorg in ("+isc.getInSQL(pk_psnorgs)+") and curyear =? ";
			SQLParameter para = new SQLParameter();
			para.addParam(pk_hrorg);
			para.addParam(typeVO.getPk_timeitem());
			para.addParam(year);
			if(leavesetPeriod==LeaveTypeCopyVO.LEAVESETPERIOD_MONTH){
				cond+="and curmonth=?";
				para.addParam(month);
			}
			LeaveBalanceVO[] dbVOs = CommonUtils.retrieveByClause(LeaveBalanceVO.class, cond, para);
			if(ArrayUtils.isEmpty(dbVOs))
				return balanceMap;
			for (LeaveBalanceVO vo : dbVOs) {
				List<LeaveBalanceVO> voList = balanceMap.get(vo.getPk_psnorg());
				if (CollectionUtils.isEmpty(voList)){
					voList = new ArrayList<LeaveBalanceVO>();
					balanceMap.put(vo.getPk_psnorg(), voList);
				}
				vo.setLeavesetperiod(typeVO.getLeavesetperiod());
				voList.add(vo);
			}
			return balanceMap;
		} finally {
			isc.clear();
		}
	}
}
