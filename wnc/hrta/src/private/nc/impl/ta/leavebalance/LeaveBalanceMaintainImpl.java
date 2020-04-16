package nc.impl.ta.leavebalance;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uap.lock.PKLock;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.hr.formula.parser.IFormulaParser;
import nc.impl.hr.formula.parser.XMLFormulaParser;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.impl.ta.statistic.pub.ParaHelper;
import nc.itf.om.IAOSQueryService;
import nc.itf.ta.ILeaveBalanceManageMaintain;
import nc.itf.ta.ILeaveBalanceQueryMaintain;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeItemQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.LeaveServiceFacade;
import nc.itf.ta.PeriodServiceFacade;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hr.temptable.TempTableVO;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.GeneralVOProcessor;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.leavebalance.LeaveFormulaCalParam;
import nc.vo.ta.leavebalance.SettlementResult;
import nc.vo.ta.leavebalance.UnSettlementResult;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.IMetaDataIDConst;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class LeaveBalanceMaintainImpl implements ILeaveBalanceManageMaintain, ILeaveBalanceQueryMaintain {

	private SimpleDocServiceTemplate serviceTemplate;
	private BaseDAO baseDao = new BaseDAO();

	public SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate(IMetaDataIDConst.LEAVEBALANCE);
		}
		return serviceTemplate;
	}

	@Override
	public LeaveBalanceVO[] calculate(String pk_org, String pk_leavetype, String year, String month,
			LeaveBalanceVO[] vos) throws BusinessException {
		UFDateTime calTime = PubEnv.getServerTime();
		// 根据计算时间校验期间的合法性
		UFLiteralDate periodCheckDate = UFLiteralDate.getDate(calTime.getDate().toString());
		// PeriodServiceFacade.checkDateScope(pk_org, periodCheckDate,
		// periodCheckDate);
		PeriodServiceFacade.queryByDateWithCheck(pk_org, periodCheckDate);
		if (ArrayUtils.isEmpty(vos)) {
			return vos;
		}
		LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) NCLocator.getInstance().lookup(ITimeItemQueryService.class)
				.queryCopyTypesByDefPK(pk_org, pk_leavetype, TimeItemVO.LEAVE_TYPE);
		// 排除掉无权计算的
		List<LeaveBalanceVO> canCalList = new ArrayList<LeaveBalanceVO>();
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		IDataPermissionPubService perimssionService = NCLocator.getInstance().lookup(IDataPermissionPubService.class);
		Map<String, UFBoolean> perimssionMap = perimssionService.isUserhasPermissionByMetaDataOperation("60170psndoc",
				StringPiecer.getStrArrayDistinct(vos, LeaveBalanceVO.PK_TBM_PSNDOC), "CalcLeaveBalance", pk_group,
				pk_user);
		for (LeaveBalanceVO vo : vos) {
			// 没有计算权限的，刨除掉
			if (perimssionMap != null && perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()) != null
					&& !perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()).booleanValue())
				continue;
			canCalList.add(vo);
		}
		if (canCalList.size() == 0)
			return vos;

		// 生成 部分工时特休年假计算 临时表 数据 需在调用 calculate()方法之前生成 yejk 2018-9-19 #21390
		try {
			generatePartWhLeaveTemp(pk_org, pk_leavetype, year, month, vos);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		// begin-双百支持-zhaozhaob-数据量过大假期计算问题-20161117（20170111NC67合并补丁）
		List<LeaveBalanceVO> tempCalList = new ArrayList<LeaveBalanceVO>();
		for (int i = 0; i < canCalList.size(); i++) {
			tempCalList.add(canCalList.get(i));
			if (tempCalList.size() == 150) {
				calculate(pk_org, typeVO, year, month, tempCalList.toArray(new LeaveBalanceVO[0]), calTime);
				tempCalList = new ArrayList<LeaveBalanceVO>();
			}

		}
		calculate(pk_org, typeVO, year, month, tempCalList.toArray(new LeaveBalanceVO[0]), calTime);

		// int i = 0;
		// List<LeaveBalanceVO> oncecal = new ArrayList<LeaveBalanceVO>();
		// for(LeaveBalanceVO vo:canCalList){
		// i++;
		// oncecal.add(vo);
		// if(i>200){
		// calculate(pk_org, typeVO, year, month, oncecal.toArray(new
		// LeaveBalanceVO[0]), calTime);
		// i = 0;
		// oncecal.clear();
		// }
		// }
		// calculate(pk_org, typeVO, year, month, oncecal.toArray(new
		// LeaveBalanceVO[0]), calTime);
		// end
		LeaveBalanceVO[] retvos = setDefaultTimeitemCopy(vos);
		// 业务日志
		TaBusilogUtil.writeLeaveBalanceCalculateBusiLog(retvos);
		return retvos;
	}

	/**
	 * #21390
	 * 
	 * @Description: 生成 部分工时特休年假计算 临时表 数据
	 * @author yejk
	 * @date 2018-9-19
	 * @param pk_org
	 * @param pk_leavetype
	 * @param year
	 * @param month
	 * @param vos
	 * @return void
	 * @throws BusinessException
	 * @throws ParseException
	 */
	protected void generatePartWhLeaveTemp(String pk_org, String pk_leavetype, String year, String month,
			LeaveBalanceVO[] vos) throws BusinessException, ParseException {
		BaseDAO basedao = new BaseDAO();
		// 生成数据前，删除缓存表旧数据
		String deleteTempSql = "delete from tbm_partWhLeave_temp";
		basedao.executeUpdate(deleteTempSql);
		String creator = InvocationInfoProxy.getInstance().getUserId();
		// UFDateTime date = new UFDateTime();
		/**
		 * // 1 取日期 算工龄 按 年月到 考勤期间 取结束日期减去 workagestartdate 薪资起算日（入职日期） // 2
		 * 取特休身份标志 // 3 计算 每个人 应获取数据的 工作日历开始时间和结束时间 // 4 特休人员计算百分比，非特休人员 返回1
		 * （100%）
		 * 
		 */

		// 获取当前计算人员PK List
		List<String> psnList = new ArrayList<String>();
		for (LeaveBalanceVO vo : vos) {
			psnList.add(vo.getPk_psndoc());
		}
		// InSQLCreator isc = new InSQLCreator() getInSQL new MapListProcessor()
		// 获取 人员是否特休标志 薪资起算日（入职日期）
		InSQLCreator isc = new InSQLCreator();
		String inSql = isc.getInSQL(psnList.toArray(new String[0]));
		String queryFlagSql = "select t1.pk_psndoc,t1.is_special_timer,t2.workagestartdate from bd_psndoc "
				+ "t1,hi_psnorg t2 where t1.pk_psndoc = t2.pk_psndoc and t2.begindate = (select max(t3.begindate) from hi_psnorg t3 "
				+ "where t2.pk_psndoc = t3.pk_psndoc) and t1.pk_psndoc in (" + inSql + ")";
		@SuppressWarnings("unchecked")
		List<Map<String, String>> resultList = (List<Map<String, String>>) basedao.executeQuery(queryFlagSql,
				new MapListProcessor());
		// 将结果转成 人员pk与 是否特休标志和薪资起算日的MAP String[0]为标志 String[1]为日期
		Map<String, String[]> psnToFlagMap = new HashMap<String, String[]>();
		for (int i = 0; i < resultList.size(); i++) {
			Map<String, String> map = resultList.get(i);
			String[] strs = new String[2];
			strs[0] = map.get("is_special_timer");
			strs[1] = map.get("workagestartdate");
			psnToFlagMap.put(map.get("pk_psndoc"), strs);
		}
		// 获取当前计算考勤期间 结束日期
		month = month.length() < 2 ? "0" + month : month;
		String kaoqinEndDateStr = year + "-" + month + "-25";// 设置个默认值为 当前计算年月 的
																// 25 号
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String queryEndDateSql = "select t.enddate from tbm_period t where t.pk_org = '" + pk_org
				+ "' and t.accyear = '" + year + "' and t.accmonth = '" + month + "'";
		String str = (String) basedao.executeQuery(queryEndDateSql, new ColumnProcessor());
		if (null != str) {
			kaoqinEndDateStr = str;
		}
		Date kaoqinEndDate = sdf.parse(kaoqinEndDateStr);
		/*
		 * 计算 每个人 特休假 比例 并将结果 批量插入临时表tbm_partWhLeave_temp
		 */
		// Map<String,Long> psnToWorkAge = new HashMap<String,Long>();
		PersistenceManager sessionManager = null;
		try {
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			for (int i = 0; i < psnList.size(); i++) {
				String pk_psndoc = psnList.get(i);
				String insertTempSql = "insert into tbm_partWhLeave_temp(creator,pk_psndoc,cpercent,cyear,cmonth) values (?,?,?,?,?)";
				SQLParameter parameter = new SQLParameter();
				// parameter.addParam(new UFDateTime().toString());//ts NC
				// JdbcSession 在 insert时 自动添加插入字段 ts
				parameter.addParam(creator);
				parameter.addParam(pk_psndoc);
				String workStartDateStr = psnToFlagMap.get(pk_psndoc)[1];

				if ("Y".equals(psnToFlagMap.get(pk_psndoc)[0])) {// 是特休人员身份
					Date workStartDate = sdf.parse(workStartDateStr);
					Calendar calStartDate = Calendar.getInstance();
					calStartDate.setTime(workStartDate);

					// calStrartDate（workStartDate） 加上 6个月
					calStartDate.add(Calendar.MONTH, 6);
					// 得到6个月后的日期
					Date addSixWorkStartDate = calStartDate.getTime();
					String addSixWorkStartDateStr = sdf.format(addSixWorkStartDate);

					// calStrartDate（workStartDate） 再加上 6个月
					calStartDate.add(Calendar.MONTH, 6);
					// 得到12个月后的日期
					Date addTwelveWorkStartDate = calStartDate.getTime();
					String addTwelveWorkStartDateStr = sdf.format(addTwelveWorkStartDate);

					if (addSixWorkStartDate.after(kaoqinEndDate)) {
						// 1、薪资起算日期加上6个月后大于当前考勤期间结束日期，即入职时间小于6个月，比例为0
						// 加入 insert 语句参数中
						parameter.addParam(0);
					} else if (addSixWorkStartDate.before(kaoqinEndDate) && addTwelveWorkStartDate.after(kaoqinEndDate)) {
						/*
						 * 2、薪资起算日期加上6个月后小于当前考勤期间结束日期，且薪资起算日期加上12个月大于当前考勤期间结束日期
						 * 即入职时间大于6个月且小于1年，则汇总工时开始日期为
						 * 薪资起算日期workStartDate，结束日期为addSixWorkStartDate
						 * 到员工工作日历中进行工时汇总，然后计算出比例
						 */
						// 获取工时汇总
						String sumSql = "select sum(t1.gzsj) from tbm_psncalendar t1 where t1.pk_psndoc = '"
								+ pk_psndoc + "' and t1.calendar >= '" + workStartDateStr + "' and t1.calendar <= '"
								+ addSixWorkStartDateStr + "' and t1.pk_org = '" + pk_org + "'";
						double sumHour = 0;
						Object obj = basedao.executeQuery(sumSql, new ColumnProcessor());
						if (null != obj) {
							sumHour = Double.valueOf(obj.toString());
						}
						// 计算比例 半年工时比例(半年约26周，每周40工时)
						double percent = sumHour / (26 * 40);
						// 比例保留两位小数
						DecimalFormat dFormat = new DecimalFormat("#.00");
						String cStr = dFormat.format(percent);
						percent = Double.valueOf(cStr);
						// 加入 insert 语句参数中
						parameter.addParam(percent);
					} else {
						// calStrartDate（workStartDate） 再加上 12个月 （前面已加6+6=12个月）
						calStartDate.add(Calendar.MONTH, 12);
						// 得到24个月（2年）后的日期
						Date addTwentyWorkStartDate = calStartDate.getTime();
						String addTwentyWorkStartDateStr = sdf.format(addTwentyWorkStartDate);
						if (addTwelveWorkStartDate.before(kaoqinEndDate) && addTwentyWorkStartDate.after(kaoqinEndDate)) {
							/*
							 * 3.1
							 * 薪资起算日期加上12个月后小于当前考勤期间结束日期，且薪资起算日期加上24个月大于当前考勤期间结束日期
							 * 即入职时间大于1年但小于2年，汇总工时开始日期为 薪资起算日期workStartDate
							 * 结束日期为addTwelveWorkStartDate（一年后）
							 */
							// 获取工时汇总
							String sumSql = "select sum(t1.gzsj) from tbm_psncalendar t1 where t1.pk_psndoc = '"
									+ pk_psndoc + "' and t1.calendar >= '" + workStartDateStr
									+ "' and t1.calendar <= '" + addTwelveWorkStartDateStr + "' and t1.pk_org = '"
									+ pk_org + "'";
							double sumHour = 0;
							Object obj = basedao.executeQuery(sumSql, new ColumnProcessor());
							if (null != obj) {
								sumHour = Double.valueOf(obj.toString());
							}
							// 计算比例 一年工时比例(一年约52周，每周40工时)
							double percent = sumHour / (52 * 40);
							// 比例保留两位小数
							DecimalFormat dFormat = new DecimalFormat("#.00");
							String cStr = dFormat.format(percent);
							percent = Double.valueOf(cStr);
							// 加入 insert 语句参数中
							parameter.addParam(percent);
						} else {
							/*
							 * 3.2 最后一种情况 ：入职时间 大于 2 年 汇总结束日期为 当前考勤期间结束日期 对应年的
							 * 入职日期 例如：薪资起算日（入职日）2016-03-21 当前考勤期间结束日期 为
							 * 2018-05-25 则 汇总结束日期为 2018-03-21 开始日期为 此日期 往前一年 即
							 * 2017-03-21
							 */
							// 计算结束日期
							String[] args = sdf.format(workStartDate).split("-");
							String sumEndDateStr = year + "-" + args[1] + "-" + args[2];
							// 计算开始日期
							int startYear = Integer.valueOf(year) - 1;
							String sumStartDateStr = String.valueOf(startYear) + "-" + args[1] + "-" + args[2];

							// 获取工时汇总
							String sumSql = "select sum(t1.gzsj) from tbm_psncalendar t1 where t1.pk_psndoc = '"
									+ pk_psndoc + "' and t1.calendar >= '" + sumStartDateStr + "' and t1.calendar <= '"
									+ sumEndDateStr + "' and t1.pk_org = '" + pk_org + "'";
							double sumHour = 0;
							Object obj = basedao.executeQuery(sumSql, new ColumnProcessor());
							if (null != obj) {
								sumHour = Double.valueOf(obj.toString());
							}
							// 计算比例 一年工时比例(一年约52周，每周40工时)
							double percent = sumHour / (52 * 40);
							// 比例保留两位小数
							DecimalFormat dFormat = new DecimalFormat("#.00");
							String cStr = dFormat.format(percent);
							percent = Double.valueOf(cStr);
							// 加入 insert 语句参数中
							parameter.addParam(percent);
						}

					}
				} else {// 不是特休人员身份 比例 为1 （100%）
					parameter.addParam(1);
				}
				parameter.addParam(year);
				parameter.addParam(month);
				session.addBatch(insertTempSql, parameter);
			}
			session.executeBatch();
		} catch (DbException e) {
			e.printStackTrace();
		} finally {
			if (sessionManager != null) {
				sessionManager.release();
			}
		}

	}

	protected UFLiteralDate getHireStartDate(String pk_psnorg) throws BusinessException {
		PsnOrgVO psnOrgVO = (PsnOrgVO) new BaseDAO().retrieveByPK(PsnOrgVO.class, pk_psnorg);
		return getHireStartDate(psnOrgVO);
	}

	protected UFLiteralDate getHireStartDate(PsnOrgVO psnOrgVO) throws BusinessException {
		// ssx added on 2018-03-16
		// for changes of start date of company age
		// 是否啟用年資起算日
		UFBoolean refEnableWorkAgeFunc = SysInitQuery.getParaBoolean(psnOrgVO.getPk_hrorg(), "TWHR10");

		if (refEnableWorkAgeFunc != null && refEnableWorkAgeFunc.booleanValue()) {
			UFLiteralDate beginDate = (UFLiteralDate) psnOrgVO.getAttributeValue("workagestartdate");

			if (beginDate != null) {
				return beginDate;
			}
		}
		return getHireDate(psnOrgVO);
	}

	protected UFLiteralDate getHireDate(PsnOrgVO psnOrgVO) {
		// 一般情况下，psnorg表中的入职日期是有值的，但也不排除没有值的情况，如果发生了这种情况，则使用此组织关系记录的creationtime作为入职日期(这样是不对的，但是必须要有入职日期，只能这么做)
		UFLiteralDate beginDate = psnOrgVO.getBegindate();
		if (beginDate != null) {
			return beginDate;
		}
		return UFLiteralDate.getDate(psnOrgVO.getCreationtime().toStdString().substring(0, 10));
	}

	protected UFLiteralDate getHireDate(String pk_psnorg) throws DAOException {
		PsnOrgVO psnOrgVO = (PsnOrgVO) new BaseDAO().retrieveByPK(PsnOrgVO.class, pk_psnorg);
		return getHireDate(psnOrgVO);
	}

	protected LeaveBalanceVO[] calculate(String pk_org, LeaveTypeCopyVO typeVO, String year, String month,
			LeaveBalanceVO[] vos, UFDateTime calTime) throws BusinessException {
		return calculate(pk_org, typeVO, year, month, vos, calTime, true);
	}

	protected LeaveBalanceVO calculate(String pk_org, LeaveTypeCopyVO typeVO, LeaveBalanceVO vo, UFDateTime calTime,
			boolean needEnsure) throws BusinessException {
		return calculate(pk_org, typeVO, vo.getCuryear(), vo.getCurmonth(), new LeaveBalanceVO[] { vo }, calTime,
				needEnsure)[0];
	}

	/**
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param month
	 * @param vos
	 * @param calTime
	 * @param withEnsure
	 *            ，true，表示传入的vos有可能数据库中还没有，需要ensure一下，false，表示vos肯定有，不需要ensure
	 * @return
	 * @throws BusinessException
	 */
	protected LeaveBalanceVO[] calculate(String pk_org, LeaveTypeCopyVO typeVO, String year, String month,
			LeaveBalanceVO[] vos, UFDateTime calTime, boolean needEnsure) throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return vos;
		}
		if (typeVO.getIslactation() != null && typeVO.getIslactation().booleanValue()) {// 如果是哺乳假，则不需要计算
			return vos;
			// throw new
			// BusinessException("lactation holiday can not be computed!");
		}
		// 如果是按年或者入职日结算，则一组织关系一年一条记录；如果是按期间结算，则一组织关系一期间一条记录
		// 如果数据库中无数据，则insert
		String[] lockables = new String[vos.length];
		// 将此HR组织内，这些人的这个休假类别锁住
		for (int i = 0; i < vos.length; i++) {
			lockables[i] = "leavebalance" + pk_org + typeVO.getPk_timeitem() + vos[i].getPk_psnorg();
		}
		PKLock lock = PKLock.getInstance();

		try {
			// modify xw 审批时总报数据被锁住，未查找到此原因，故改为只有一条数据时不加锁 20171205
			boolean acquired = vos.length == 1 ? true : lock.acquireBatchLock(lockables, PubEnv.getPk_user(), null);
			// boolean acquired = lock.acquireBatchLock(lockables,
			// PubEnv.getPk_user(), null);
			if (!acquired)
				throw new BusinessException(ResHelper.getString("6017leave", "06017leave0253")
				/* @res "数据正被他人修改或他人正在进行假期计算，请稍候再试!" */);
			LeaveBalanceDAO balanceDAO = new LeaveBalanceDAO();
			LeaveBalanceVO[] filteredVOs = null;
			if (needEnsure) {
				balanceDAO.ensureData(pk_org, typeVO, year, month, vos);
				filteredVOs = filterSettlementVOs(vos);
			} else {
				filteredVOs = vos;
			}
			// PeriodVO periodVO =
			// NCLocator.getInstance().lookup(IPeriodQueryService.class).queryByYearMonth(pk_org,
			// year, month);
			String[] pks = SQLHelper.getStrArray(filteredVOs, LeaveBalanceVO.PK_LEAVEBALANCE);
			// InSQLCreator isc = null;
			UFLiteralDate periodBeginDate = null;// 期间开始日和期间结束日，对于按年、期间结算的有效，对于按入职日结算的无效
			UFLiteralDate periodEndDate = null;
			// ssx added on 2018-03-16
			// for changes of start date of company age
			if (typeVO.getLeavesetperiod().intValue() == LeaveTypeCopyVO.LEAVESETPERIOD_DATE
					|| typeVO.getLeavesetperiod().intValue() == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
				//
				for (LeaveBalanceVO vo : vos) {
					vo.setPeriodbegindate(vo.getHirebegindate());
					vo.setPeriodenddate(vo.getHireenddate());
					vo.setPeriodextendenddate(typeVO.getExtendDaysCount() == 0 ? vo.getHireenddate() : vo
							.getHireenddate().getDateAfter(typeVO.getExtendDaysCount()));
				}
			} else {
				UFLiteralDate[] periodBeginEndDates = queryPeriodBeginEndDate(pk_org, typeVO, year, month);
				if (periodBeginEndDates != null && periodBeginEndDates.length == 2) {
					periodBeginDate = periodBeginEndDates[0];
					periodEndDate = periodBeginEndDates[1];
					UFLiteralDate periodExtendDate = typeVO.getExtendDaysCount() == 0 ? periodEndDate : periodEndDate
							.getDateAfter(typeVO.getExtendDaysCount());
					for (LeaveBalanceVO vo : vos) {
						vo.setPeriodbegindate(periodBeginDate);
						vo.setPeriodenddate(periodEndDate);
						vo.setPeriodextendenddate(periodExtendDate);
					}
				}
			}
			if (ArrayUtils.isEmpty(filteredVOs))
				return vos;
			InSQLCreator isc = new InSQLCreator();
			try {
				String inSQL = isc.getInSQL(pks);
				// 如果是否加班转调休，则需要计算享有和实际享有（加班转调休的享有和实际享有都是在转调休、反转调休时生成）
				if (!typeVO.getTimeitemcode().equals(TimeItemCopyVO.OVERTIMETOLEAVETYPE))
					calCurRealDayOrHour(pk_org, typeVO, year, month, periodBeginDate, periodEndDate, filteredVOs,
							inSQL, calTime);
				else
					balanceDAO.syncFromDB4DataExists(filteredVOs);// 如果是加班转调休，则享有和实际享有也要从数据库中同步一下，防止后台和界面不一致
				calYiRestFreezeDayOrHour(pk_org, typeVO, year, month, periodBeginDate, periodEndDate, filteredVOs,
						inSQL, calTime);

				// 容错处理，因为一些原因导致休假计算数据中的hirebegindate和hireenddate为空，此处补齐
				if (LeaveTypeCopyVO.LEAVESETPERIOD_DATE == typeVO.getLeavesetperiod().intValue()) {
					new BaseDAO().updateVOArray(filteredVOs, new String[] { LeaveBalanceVO.HIREBEGINDATE,
							LeaveBalanceVO.HIREENDDATE });
				}
			} catch (BusinessException ex) {
				Logger.error(ex.getMessage());
			} finally {
				isc.clear();
			}
			return vos;
		} finally {
			if (vos.length != 1) {
				lock.releaseBatchLock(lockables, PubEnv.getPk_user(), null);
			}
		}
	}

	/**
	 * 双百支持-zhaozhaob-数据量过大假期计算问题（定时任务执行）-20161117
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param month
	 * @param vos
	 * @param calTime
	 * @param needEnsure
	 * @return
	 * @throws BusinessException
	 */
	protected LeaveBalanceVO[] splitCalculate(String pk_org, LeaveTypeCopyVO typeVO, String year, String month,
			LeaveBalanceVO[] vos, UFDateTime calTime, boolean needEnsure) throws BusinessException {
		List<LeaveBalanceVO> returnvos = new ArrayList<LeaveBalanceVO>();
		List<LeaveBalanceVO> tempCalList = new ArrayList<LeaveBalanceVO>();
		try {
			for (int i = 0; i < vos.length; i++) {
				tempCalList.add(vos[i]);
				if (tempCalList.size() == 300) {
					LeaveBalanceVO[] tempvos = calculate(pk_org, typeVO, year, month,
							tempCalList.toArray(new LeaveBalanceVO[0]), calTime);
					returnvos.addAll(Arrays.asList(tempvos));
					tempCalList = new ArrayList<LeaveBalanceVO>();
				}

			}
			LeaveBalanceVO[] tempvos = calculate(pk_org, typeVO, year, month,
					tempCalList.toArray(new LeaveBalanceVO[0]), calTime);
			returnvos.addAll(Arrays.asList(tempvos));
		} catch (Exception e) {
			Logger.error(e);
		}
		return returnvos.toArray(new LeaveBalanceVO[0]);
	}

	/**
	 * 过滤数据，返回的结果中只有未结算的
	 * 
	 * @param vos
	 * @return
	 */
	protected LeaveBalanceVO[] filterSettlementVOs(LeaveBalanceVO[] vos) {
		List<LeaveBalanceVO> retList = new ArrayList<LeaveBalanceVO>();
		for (LeaveBalanceVO vo : vos) {
			if (!vo.isSettlement())
				retList.add(vo);
		}
		return retList.size() == 0 ? null : retList.toArray(new LeaveBalanceVO[0]);
	}

	@Override
	public LeaveBalanceVO[] save(LoginContext context, String pk_leavetype, String year, String month,
			LeaveBalanceVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return vos;
		}
		LeaveBalanceVO[] oldvos = getServiceTemplate().queryByPks(LeaveBalanceVO.class,
				StringPiecer.getStrArray(vos, LeaveBalanceVO.PK_LEAVEBALANCE));
		LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) NCLocator.getInstance().lookup(ITimeItemQueryService.class)
				.queryCopyTypesByDefPK(context.getPk_org(), pk_leavetype, TimeItemCopyVO.LEAVE_TYPE);
		// int leavesetperiod = typeVO.getLeavesetperiod().intValue();
		// boolean isYear =
		// leavesetperiod==LeaveTypeCopyVO.LEAVESETPERIOD_YEAR;//是否按年计算

		for (int i = 0; vos != null && i < vos.length; i++) {
			vos[i].setPk_timeitem(pk_leavetype);
			vos[i].setCuryear(year);
			if (TimeItemCopyVO.LEAVESETPERIOD_MONTH == typeVO.getLeavesetperiod().intValue()) {
				vos[i].setCurmonth(month);
			} else {
				vos[i].setCurmonth(null);
			}

			// 默认值
			if (vos[i].getIsabnormalset() == null)
				vos[i].setIsabnormalset(UFBoolean.FALSE);

			if (vos[i].getLastdayorhour() == null)
				vos[i].setLastdayorhour(UFDouble.ZERO_DBL);
			if (vos[i].getCurdayorhour() == null)
				vos[i].setCurdayorhour(UFDouble.ZERO_DBL);
			if (vos[i].getRealdayorhour() == null)
				vos[i].setRealdayorhour(UFDouble.ZERO_DBL);
			if (vos[i].getYidayorhour() == null)
				vos[i].setYidayorhour(UFDouble.ZERO_DBL);
			if (vos[i].getRestdayorhour() == null)
				vos[i].setRestdayorhour(UFDouble.ZERO_DBL);
			if (vos[i].getFreezedayorhour() == null)
				vos[i].setFreezedayorhour(UFDouble.ZERO_DBL);

			if (vos[i].getPk_leavebalance() == null || vos[i].getPk_leavebalance().equals("")) {
				getServiceTemplate().insert(vos[i]);
			} else {
				getServiceTemplate().update(vos[i], true);
			}

		}
		// 业务日志
		TaBusilogUtil.writeLeaveBalanceEditBusiLog(vos, oldvos);
		return setDefaultTimeitemCopy(vos);
	}

	/**
	 * 构造公式计算所需要的参数
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param month
	 * @param calDate
	 * @param calculateTime
	 * @return
	 * @throws BusinessException
	 */
	private LeaveFormulaCalParam createFormulaPara(String pk_org, LeaveTypeCopyVO typeVO, String year, String month,
			UFLiteralDate calDate, UFDateTime calculateTime) throws BusinessException {
		LeaveFormulaCalParam para = new LeaveFormulaCalParam();
		para.setTypeVO(typeVO);
		para.setCalDate(calDate);
		para.setCalTime(calculateTime);
		PeriodVO periodVO = PeriodServiceFacade.queryByDate(pk_org, calDate);// 计算日所属考勤期间
		para.setCalDateBelongToPeriod(periodVO);
		PeriodVO previousPeriodVO = PeriodServiceFacade.queryPreviousPeriod(pk_org, calDate);// 计算日所属考勤的上一个考勤期间
		para.setPreviousCalDateBelongToPeriod(previousPeriodVO);
		int settlePeriod = typeVO.getLeavesetperiod().intValue();
		if (settlePeriod == TimeItemCopyVO.LEAVESETPERIOD_MONTH) {// 如果是按期间结算，则要查出year,month对应的期间
			PeriodVO calPeriodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
			para.setCalPeriod(calPeriodVO);
			PeriodVO previousCalPeriodVO = PeriodServiceFacade.queryPreviousPeriod(pk_org, year, month);
			para.setPreviousCalPeriod(previousCalPeriodVO);
		}
		// 查出year对应的所有期间
		para.setCalYearPeriods(PeriodServiceFacade.queryByYear(pk_org, year));
		para.setPreviousCalYearPeriods(PeriodServiceFacade.queryByYear(pk_org,
				Integer.toString(Integer.parseInt(year) - 1)));
		para.setCalYear(year);
		para.setCalMonth(month);
		// 计算时刻的所计算期间的人员状态的时间点
		UFLiteralDate calPsnDate = null;
		UFLiteralDate periodEndDate = null;

		if (settlePeriod == TimeItemCopyVO.LEAVESETPERIOD_MONTH) {// 按期间结算的
			PeriodVO calPeriodVO = para.getCalPeriod();
			periodEndDate = calPeriodVO.getEnddate();
		} else {
			periodEndDate = para.getCalYearEndDate();
		}
		if (periodEndDate == null) {
			calPsnDate = calDate;
		} else if (periodEndDate.after(calDate)) {// 若计算日期在期间的后面则人员的状态取期间的最后一天的状态
			calPsnDate = calDate;
		} else {
			calPsnDate = periodEndDate;
		}
		para.setCalPsnDate(calPsnDate);
		return para;
	}

	/**
	 * 计算某个休假类别的享有时长和实际享有时长 传入的vo肯定是未结算的
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param month
	 * @param vos
	 * @param calculateTime
	 * @throws BusinessException
	 */
	protected void calCurRealDayOrHour(String pk_org, LeaveTypeCopyVO typeVO, String year, String month,
			UFLiteralDate periodBeginDate, UFLiteralDate periodEndDate, LeaveBalanceVO[] vos, String inSQL,
			UFDateTime calculateTime) throws BusinessException {
		InputStream is = null;
		try {
			String where = " where " + LeaveBalanceVO.PK_LEAVEBALANCE + " in(" + inSQL + ")";
			String formula = CommonUtils.toStringObject(typeVO.getFormula());
			// 2012.05.16，与需求讨论后确定，如果公式没有任何内容，则不计算享有、当前享有，以用户在假期计算处填写的数据为准
			boolean isBlank = StringUtils.isBlank(formula);
			if (isBlank)
				return;
			boolean isDecimal = false;// 公式是否是一个纯数字
			try {
				Double.parseDouble(formula.trim());
				isDecimal = true;
			} catch (NumberFormatException nfe) {

			}
			String parsedFormula = null;
			boolean isZero = isDecimal && Double.parseDouble(formula.trim()) == 0.0;
			// 计算日期，需要将计算时间与HR组织的时区进行比对
			TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
			TimeZone timeZone = timeRuleVO.getTimeZone();
			UFLiteralDate calDate = UFLiteralDate.getDate(calculateTime.toStdString(timeZone).substring(0, 10));
			// 如果计算时间还没到期间第一天，则设置为期间第一天，否则没法提前请假
			if (periodBeginDate != null && calDate.before(periodBeginDate))
				calDate = periodBeginDate;
			if (isZero)
				parsedFormula = "0.0";
			else if (isDecimal)
				parsedFormula = formula;
			else {// 有公式的时候，采使用公式解析
				String parserFilePath = "/hr/ta/formula/parserfiles/leaveruleparserfiles.xml";
				is = ParaHelper.class.getResource(parserFilePath).openStream();
				IFormulaParser parser = new XMLFormulaParser(is);
				// 准备公式计算的参数，例如计算日/时间/期间/上期/当前年度/上一年度等信息
				LeaveFormulaCalParam param = createFormulaPara(pk_org, typeVO, year, month, calDate, calculateTime);
				// ssx added on 2017-11-24
				// 標準產品未處理按入職日期結算、按年計算的計算日期
				// 計算日期大於年度入職年度結束日期，即應處理為入職年度結束日期
				// 如果小於年度入職年度結束日期，即為系統日期
				if (typeVO.getLeavesetperiod().equals(LeaveTypeCopyVO.LEAVESETPERIOD_DATE) // 結算週期：按入職日期結算
						&& typeVO.getLeavescale().equals(LeaveTypeCopyVO.LEAVESCALE_YEAR // 假期計算方式：按年計算
								)) {
					formula = formula
							.replace("CALCULATIONINFO.CALCULATIONDATE",
									"(case when CALCULATIONINFO.CALCULATIONDATE > hireenddate then hireenddate else CALCULATIONINFO.CALCULATIONDATE end)");
				}
				// end
				parsedFormula = parser.parse(pk_org, formula, param);
			}
			BaseDAO dao = new BaseDAO();
			if (dao.getDBType() == DBUtil.SQLSERVER) {
				if (parsedFormula != null && parsedFormula.toLowerCase().contains("convert")) {

					parsedFormula = parsedFormula.replaceAll("convert\\(NUMBER", "convert\\(int");
					parsedFormula = parsedFormula.replaceAll("convert\\( NUMBER", "convert\\(int");
					parsedFormula = parsedFormula.replaceAll("convert\\(number", "convert\\(int");
					parsedFormula = parsedFormula.replaceAll("convert\\( number", "convert\\(int");
				}

			}
			// 如果是按期间结算，或者是“按年/入职日结算 and 按年计算”，则当前享有=享有；否则需要按照当前月份折算当前实际享有
			String updateSQL = "update " + LeaveBalanceVO.getDefaultTableName() + " set " + LeaveBalanceVO.CURDAYORHOUR
					+ "= isnull(" + parsedFormula + ",0)";
			if (isZero)
				updateSQL += "," + LeaveBalanceVO.REALDAYORHOUR + "=0.0 ";
			updateSQL += where;

			dao.executeUpdate(updateSQL);

			// v63添加，若有转入时长，及选中了转移标示，则享有为空（零）
			List<LeaveBalanceVO> transList = new ArrayList<LeaveBalanceVO>();
			for (LeaveBalanceVO vo : vos) {
				if (!vo.getTransFlagValue())
					continue;
				vo.setCurdayorhour(UFDouble.ZERO_DBL);
				transList.add(vo);
			}
			if (CollectionUtils.isNotEmpty(transList))
				dao.updateVOArray(transList.toArray(new LeaveBalanceVO[0]),
						new String[] { LeaveBalanceVO.CURDAYORHOUR });

			LeaveBalanceDAO balanceDAO = new LeaveBalanceDAO();
			if (isZero) {
				balanceDAO.syncFromDB4DataExists(vos);
				return;
			}
			if (typeVO.isRealEqualsCur() && !isZero) {
				String updateSQL2 = "update " + LeaveBalanceVO.getDefaultTableName() + " set "
						+ LeaveBalanceVO.REALDAYORHOUR + "=" + LeaveBalanceVO.CURDAYORHOUR + where;
				dao.executeUpdate(updateSQL2);
				balanceDAO.syncFromDB4DataExists(vos);
				return;
			}
			// 代码走到这里，说明享有!=0，且需要按月来折算当前实际享有
			balanceDAO.syncFromDB4DataExists(vos);
			calRealDayORHour(pk_org, typeVO, year, month, periodBeginDate, periodEndDate, vos, inSQL, calculateTime,
					calDate);
		} catch (IOException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					Logger.error(e.getMessage(), e);
					throw new BusinessException(e.getMessage(), e);
				}
		}
	}

	/**
	 * 计算当前实际享有时长。
	 * 使用折算法，大致逻辑是，实际享有时长=(length(later(期间第一天，入职日),earlier(期间最后一天，计算日
	 * )))/length(整期间)*享有 对于按入职日结算的情况，期间第一天不可能比入职日早
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param month
	 * @param vos
	 * @param calculateTime
	 */
	protected void calRealDayORHour(String pk_org, LeaveTypeCopyVO typeVO, String year, String month,
			UFLiteralDate periodBeginDate, UFLiteralDate periodEndDate, LeaveBalanceVO[] vos, String pkInSQL,
			UFDateTime calculateTime, UFLiteralDate calculateDate) throws BusinessException {
		// 如果是按年结算，且计算日还未到期间第一天，则当前实际享有肯定是0
		int setPeriod = typeVO.getLeavesetperiod();
		BaseDAO dao = new BaseDAO();
		if (setPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR && periodBeginDate != null
				&& calculateDate.before(periodBeginDate)) {
			String updateSQL = "update " + LeaveBalanceVO.getDefaultTableName() + " set "
					+ LeaveBalanceVO.REALDAYORHOUR + "=0.0" + " where " + LeaveBalanceVO.PK_LEAVEBALANCE + " in ("
					+ pkInSQL + ")";
			dao.executeUpdate(updateSQL);
			LeaveBalanceDAO balanceDAO = new LeaveBalanceDAO();
			balanceDAO.syncFromDB4DataExists(vos);
			return;
		}
		if (setPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR) {
			Map<String, UFLiteralDate> hireDateMap = queryHireDate(pk_org, vos);
			calRealDayORHour4YearPeriod(pk_org, typeVO, periodBeginDate, periodEndDate, vos, pkInSQL, calculateTime,
					calculateDate, hireDateMap);
			return;
		}
		// 按入职日结算
		calRealDayORHour4HirePeriod(pk_org, typeVO, year, vos, pkInSQL, calculateTime, calculateDate);
	}

	/**
	 * 计算已休时长、结余时长、冻结时长
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param periodVO
	 * @param vos
	 * @param pkInSQL
	 * @param isc
	 * @param calculateTime
	 * @param calculateDate
	 * @throws BusinessException
	 */
	protected void calYiRestFreezeDayOrHour(String pk_org, LeaveTypeCopyVO typeVO, String year, String month,
			UFLiteralDate periodBeginDate, UFLiteralDate periodEndDate, LeaveBalanceVO[] vos, String pkInSQL,
			UFDateTime calculateTime) throws BusinessException {
		// 考勤规则
		// TimeRuleVO timeRuleVO =
		// NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		// BillMutexRule billMutexRule =
		// BillMutexRule.createBillMutexRule(timeRuleVO.getBillmutexrule());
		// //所有班次
		// Map<String,AggShiftVO> aggShiftMap =
		// ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		// Map<String, LeaveTypeCopyVO> typeMap = new HashMap<String,
		// LeaveTypeCopyVO>();
		// typeMap.put(typeVO.getPk_timeitem(), typeVO);

		// 优化：查询按leaveindex分组，批量查询
		Map<Integer, LeaveBalanceVO[]> indexMap = CommonUtils.group2ArrayByField(LeaveBalanceVO.LEAVEINDEX, vos);
		for (Integer leaveindex : indexMap.keySet()) {
			LeaveBalanceVO[] indexvos = indexMap.get(leaveindex);
			if (ArrayUtils.isEmpty(indexvos))
				continue;
			// 查询出此期间内的所有审批通过的单据和登记单据，
			String[] pk_psnorgs = StringPiecer.getStrArray(indexvos, LeaveBalanceVO.PK_PSNORG);
			LeaveRegVO[] regVOs = null;
			try {
				regVOs = LeaveServiceFacade.queryByPsnsLeaveTypePeriod(pk_org, pk_psnorgs, typeVO, year, month,
						leaveindex);
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
				throw new BusinessException("假期計算報錯wangywt：" + e.getMessage(), e);
			}
			// 查询出此期间内的自由态，审批中的申请单
			LeavebVO[] leavebVOs = LeaveServiceFacade.queryBeforePassWithoutNoPassByPsnsLeaveTypePeriod(pk_org,
					pk_psnorgs, typeVO, year, month, leaveindex);
			// LeaveCommonVO[] leaveVOs = CommonUtils.merge2Array(regVOs,
			// leavebVOs);
			List<LeaveCommonVO> leaveVOList = new ArrayList<LeaveCommonVO>();
			if (!ArrayUtils.isEmpty(regVOs))
				CollectionUtils.addAll(leaveVOList, regVOs);
			if (!ArrayUtils.isEmpty(leavebVOs))
				CollectionUtils.addAll(leaveVOList, leavebVOs);
			LeaveCommonVO[] leaveVOs = CollectionUtils.isEmpty(leaveVOList) ? null : leaveVOList
					.toArray(new LeaveCommonVO[0]);
			BillProcessHelperAtServer.calLeaveLength(pk_org, leaveVOs);
			// BillProcessHelperAtServer.calculateLengths(pk_org,
			// BillMutexRule.BILL_LEAVE, leaveVOs, timeRuleVO, billMutexRule,
			// typeMap, aggShiftMap, null);
			// Map<String, LeaveRegVO[]> regMap =
			// CommonUtils.group2ArrayByField(LeaveRegVO.PK_PSNORG, regVOs);
			// Map<String, LeavebVO[]> leavebMap =
			// CommonUtils.group2ArrayByField(LeavebVO.PK_PSNORG, leavebVOs);
			Map<String, List<LeaveRegVO>> regMap = new HashMap<String, List<LeaveRegVO>>();
			Map<String, List<LeavebVO>> leavebMap = new HashMap<String, List<LeavebVO>>();
			for (int i = 0, j = ArrayUtils.getLength(leaveVOs); i < j; i++) {
				String pk_psnorg = leaveVOs[i].getPk_psnorg();
				if (leaveVOs[i] instanceof LeaveRegVO) {
					List<LeaveRegVO> regList = regMap.get(pk_psnorg);
					if (CollectionUtils.isEmpty(regList)) {
						regList = new ArrayList<LeaveRegVO>();
						regMap.put(pk_psnorg, regList);
					}
					regList.add((LeaveRegVO) leaveVOs[i]);
					continue;
				}
				List<LeavebVO> lbList = leavebMap.get(pk_psnorg);
				if (CollectionUtils.isEmpty(lbList)) {
					lbList = new ArrayList<LeavebVO>();
					leavebMap.put(pk_psnorg, lbList);
				}
				lbList.add((LeavebVO) leaveVOs[i]);
			}
			for (LeaveBalanceVO vo : indexvos) {
				LeaveRegVO[] leaveRegVOs = CollectionUtils.isEmpty(regMap.get(vo.getPk_psnorg())) ? null : regMap.get(
						vo.getPk_psnorg()).toArray(new LeaveRegVO[0]);
				LeavebVO[] leavebVOs2 = CollectionUtils.isEmpty(leavebMap.get(vo.getPk_psnorg())) ? null : leavebMap
						.get(vo.getPk_psnorg()).toArray(new LeavebVO[0]);
				// double result =
				// BillProcessHelperAtServer.calConsumedLeaveLength(leaveRegVOs,
				// pk_org, typeVO, timeRuleVO, billMutexRule, aggShiftMap);
				double result = getSumValue(leaveRegVOs);
				vo.setYidayorhour(result == 0 ? UFDouble.ZERO_DBL : new UFDouble(result));
				// 结余=上期结余+当前实际享有-已休
				// vo.setRestdayorhour(new
				// UFDouble(vo.getLastdayorhour().doubleValue()+vo.getRealdayorhour().doubleValue()-result));
				// 根据港华需求：结余=上期结余+当前实际享有-已休 + 调整时长
				double changeLength = vo.getChangelength() == null ? 0.0 : vo.getChangelength().doubleValue();
				vo.setRestdayorhour(new UFDouble(vo.getLastdayorhour().doubleValue()
						+ vo.getRealdayorhour().doubleValue() - result + changeLength));
				// v63添加如果结余是可转移的还要计算上转入转出时长，2013-03-04修改根据转移标识判断
				// if(typeVO.getIsleavetransfer()!=null&&typeVO.getIsleavetransfer().booleanValue()){
				if (vo.getTransflag() != null && vo.getTransflag().booleanValue()) {
					double in = vo.getTranslatein() == null ? 0 : vo.getTranslatein().doubleValue();
					double out = vo.getTranslateout() == null ? 0 : vo.getTranslateout().doubleValue();
					vo.setRestdayorhour(new UFDouble(vo.getRestdayorhour().doubleValue() + in - out));
				}
				// 计算冻结时长
				// double freeze =
				// BillProcessHelperAtServer.calFreezeLeaveLength(leavebVOs2,
				// pk_org, typeVO, timeRuleVO, billMutexRule, aggShiftMap);
				double freeze = getSumValue(leavebVOs2);
				vo.setFreezedayorhour(new UFDouble(freeze));
				vo.setCalculatetime(calculateTime);
			}
		}
		// 优化时删除，采用上面的方法分组处理
		// for(LeaveBalanceVO vo:vos){
		// double result =
		// BillProcessHelperAtServer.calConsumedLeaveLength(pk_org,
		// vo.getPk_psnorg(), typeVO,
		// year, month,vo.getLeaveindex(), timeRuleVO, billMutexRule,
		// aggShiftMap);
		// vo.setYidayorhour(result==0?UFDouble.ZERO_DBL:new UFDouble(result));
		// //结余=上期结余+当前实际享有-已休
		// vo.setRestdayorhour(new
		// UFDouble(vo.getLastdayorhour().doubleValue()+vo.getRealdayorhour().doubleValue()-result));
		// //计算冻结时长
		// double freeze =
		// BillProcessHelperAtServer.calFreezeLeaveLength(pk_org,
		// vo.getPk_psnorg(), typeVO, year, month, vo.getLeaveindex(),
		// timeRuleVO, billMutexRule, aggShiftMap);
		// vo.setFreezedayorhour(new UFDouble(freeze));
		// vo.setCalculatetime(calculateTime);
		// }
		new BaseDAO().updateVOArray(vos, new String[] { LeaveBalanceVO.TRANSLATEIN, LeaveBalanceVO.TRANSLATEOUT,
				LeaveBalanceVO.YIDAYORHOUR, LeaveBalanceVO.RESTDAYORHOUR, LeaveBalanceVO.FREEZEDAYORHOUR,
				LeaveBalanceVO.CALCULATETIME });
		new LeaveBalanceDAO().syncFromDB4DataExists(vos);
	}

	/**
	 * 计算休假单据休假时长总和
	 * 
	 * @param vos
	 * @param field
	 * @return
	 */
	private double getSumValue(LeaveCommonVO[] vos) {
		double value = 0;
		if (ArrayUtils.isEmpty(vos))
			return value;
		for (LeaveCommonVO vo : vos) {
			value += vo.getLeaveHourValue();
		}
		return value;
	}

	/**
	 * 计算按年结算-按月计算的当前实际享有
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param periodVO
	 * @param vos
	 * @param whereSQL
	 * @param isc
	 * @param calculateTime
	 * @param calculateDate
	 * @param hireDateMap
	 * @throws BusinessException
	 */
	protected void calRealDayORHour4YearPeriod(String pk_org, LeaveTypeCopyVO typeVO, UFLiteralDate periodBeginDate,
			UFLiteralDate periodEndDate, LeaveBalanceVO[] vos, String pkInSQL, UFDateTime calculateTime,
			UFLiteralDate calculateDate, Map<String, UFLiteralDate> hireDateMap) throws BusinessException {
		// 根据港华的需求进行变更了
		double monthDiff = !calculateDate.before(periodEndDate) ? 12 : (calMonthDiff(periodBeginDate, calculateDate));
		if (typeVO.getLeavesetperiod() == TimeItemCopyVO.LEAVESETPERIOD_YEAR
				&& typeVO.getLeavescale() == TimeItemCopyVO.LEAVESCALE_MONTH) {
			IPeriodQueryService periodSer = NCLocator.getInstance().lookup(IPeriodQueryService.class);
			PeriodVO culPeriod = periodSer.queryByDate(pk_org, calculateDate.before(periodEndDate) ? calculateDate
					: periodEndDate);
			PeriodVO[] periods = periodSer.queryByYear(pk_org, vos[0].getCuryear());
			int months = periods.length;
			int curMonth = 0;
			for (PeriodVO period : periods) {
				curMonth++;
				if (culPeriod.getTimemonth().equalsIgnoreCase(period.getTimemonth()))
					break;
			}
			monthDiff = curMonth * 12.0 / months;
		}
		monthDiff = Math.min(12, monthDiff);
		for (LeaveBalanceVO vo : vos) {
			String pk_psnorg = vo.getPk_psnorg();
			UFLiteralDate hireDate = hireDateMap.get(pk_psnorg);
			double psnMonthDiff = monthDiff;
			if (hireDate.after(periodBeginDate)) {
				psnMonthDiff -= calMonthDiff(periodBeginDate, hireDate);
				psnMonthDiff++;// 员工如果在考勤期间第一天所在自然月入职，则享有全假，下一月入职才扣除一个月
				psnMonthDiff = Math.max(0, psnMonthDiff);
				psnMonthDiff = Math.min(12, psnMonthDiff);
			}
			if (psnMonthDiff == 12) {
				vo.setRealdayorhour(vo.getCurdayorhour());
				continue;
			}
			if (psnMonthDiff == 0) {
				vo.setRealdayorhour(UFDouble.ZERO_DBL);
				continue;
			}
			vo.setRealdayorhour(new UFDouble(vo.getCurdayorhour().doubleValue() * psnMonthDiff / 12.0));
		}
		new BaseDAO().updateVOArray(vos, new String[] { LeaveBalanceVO.REALDAYORHOUR });
	}

	private int calMonthDiff(UFLiteralDate beginDate, UFLiteralDate endDate) {
		// 2012.4与需求讨论后决定，按月计算时，改“下月开始才享有上月额度”为“当月开始就享有当月额度”
		return (endDate.getYear() - beginDate.getYear()) * 12 + endDate.getMonth() - beginDate.getMonth() + 1;
	}

	/**
	 * 计算按入职日结算-按月计算的当前实际享有
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param periodVO
	 * @param vos
	 * @param whereSQL
	 * @param isc
	 * @param calculateTime
	 * @param calculateDate
	 * @param hireDateMap
	 * @throws BusinessException
	 */
	protected void calRealDayORHour4HirePeriod(String pk_org, LeaveTypeCopyVO typeVO, String year,
			LeaveBalanceVO[] vos, String pkInSQL, UFDateTime calculateTime, UFLiteralDate calculateDate)
			throws BusinessException {
		for (LeaveBalanceVO vo : vos) {
			// 按入职日结算时，每个人员的期间都需要单独计算：
			// 期间第一天：期间年度=year,月日部分=入职日月日部分；
			// 期间最后一天：期间第一天一年后
			// 例如，张三入职日为2011-03-01，那么张三的2011年度就是2011-03-01-2012-02-29
			UFLiteralDate periodBeginDate = vo.getHirebegindate();
			UFLiteralDate periodEndDate = vo.getHireenddate();
			int psnMonthDiff = !calculateDate.before(periodEndDate) ? 12
					: (calMonthDiff(periodBeginDate, calculateDate));
			psnMonthDiff = Math.max(0, psnMonthDiff);
			psnMonthDiff = Math.min(12, psnMonthDiff);
			if (psnMonthDiff == 12) {
				vo.setRealdayorhour(vo.getCurdayorhour());
				continue;
			}
			if (psnMonthDiff == 0) {
				vo.setRealdayorhour(UFDouble.ZERO_DBL);
				continue;
			}
			vo.setRealdayorhour(new UFDouble(vo.getCurdayorhour().doubleValue() * psnMonthDiff / 12.0));
		}
		new BaseDAO().updateVOArray(vos, new String[] { LeaveBalanceVO.REALDAYORHOUR });
	}

	/**
	 * 根据入职日期、年度，计算出入职年的第一天 例如，入职日期是2008-03-03，那么2011年的入职年的第一天就是2011-03-03
	 * 
	 * @param year
	 * @param hireDate
	 * @return
	 */
	protected UFLiteralDate getHireBeginDate(String year, UFLiteralDate hireDate) {
		return getDateInYear(year, hireDate);
	}

	/**
	 * 根据入职日期、年度，计算出入职年的最后一天 例如，入职日期是2008-03-03，那么2011年的入职年的最后一天就是2012-03-02
	 * 
	 * @param year
	 * @param hireDate
	 * @return
	 */
	protected UFLiteralDate getHireEndDate(String year, UFLiteralDate hireDate) {
		String hireDateStr = hireDate.toString();
		// 组合日期之前进行 日期校验 因为 平年+2月29的组合会报错。
		if (!UFLiteralDate.isLeapYear(Integer.valueOf(year) + 1)
				&& "-02-29".equals(hireDateStr.substring(4, hireDateStr.length()))) {
			return UFLiteralDate.getDate(
					(Integer.parseInt(year) + 1)
							+ "-"
							+ hireDate.getDateBefore(1).toString()
									.substring(4, (hireDate.getDateBefore(1).toString()).length())).getDateBefore(1);
		} else {
			return UFLiteralDate.getDate(
					(Integer.parseInt(year) + 1) + "-" + hireDateStr.substring(4, hireDateStr.length())).getDateBefore(
					1);
		}

	}

	/**
	 * 将date的日期部分与year拼接
	 * 
	 * @param year
	 * @param date
	 * @return
	 */
	protected UFLiteralDate getDateInYear(String year, UFLiteralDate date) {
		String dateStr = date.toString();
		// 组合日期之前进行 日期校验 因为 平年+2月29的组合会报错。
		if (!UFLiteralDate.isLeapYear(Integer.valueOf(year)) && "-02-29".equals(dateStr.substring(4, dateStr.length()))) {
			return UFLiteralDate.getDate(year + "-"
					+ date.getDateBefore(1).toString().substring(5, (date.getDateBefore(1).toString()).length()));
		} else {
			return UFLiteralDate.getDate(year + "-" + dateStr.substring(5, dateStr.length()));
		}
	}

	/**
	 * 查询入职日期，用map返回，key是pk_psnorg，value是人员组织关系的入职日
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param periodVO
	 * @param vos
	 * @param isc
	 * @param calculateTime
	 * @param calculateDate
	 * @return
	 */
	private Map<String, UFLiteralDate> queryHireDate(String pk_org, LeaveBalanceVO[] vos) throws BusinessException {
		// 首先查询这些人员的入职日
		String[] psnorgs = SQLHelper.getStrArray(vos, LeaveBalanceVO.PK_PSNORG);
		return queryHireDate(psnorgs);
	}

	/**
	 * 查询一批人员的入职日期，
	 * 
	 * @param psnorgInSQL
	 *            ，pk_psnorg的insql
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, UFLiteralDate> queryHireDate(String[] pk_psnorgs) throws BusinessException {
		Map<String, UFLiteralDate> retMap = new HashMap<String, UFLiteralDate>();
		if (ArrayUtils.isEmpty(pk_psnorgs))
			return retMap;
		PsnOrgVO[] psnorgVOs = null;
		InSQLCreator isc = new InSQLCreator();
		try {
			String psnorgInSQL = isc.createTempTable(pk_psnorgs);
			// 人员的组织关系vo，用来取入职日期。
			psnorgVOs = CommonUtils.retrieveByClause(PsnOrgVO.class, PsnOrgVO.PK_PSNORG + " in(select "
					+ TempTableVO.IN_PK + " from " + psnorgInSQL + ")");
		} finally {
			isc.clear();
		}
		if (ArrayUtils.isEmpty(psnorgVOs))
			return retMap;
		for (PsnOrgVO psnOrgVO : psnorgVOs) {
			// 一般情况下，psnorg表中的入职日期是有值的，但也不排除没有值的情况，如果发生了这种情况，则使用此组织关系记录的creationtime作为入职日期(这样是不对的，但是必须要有入职日期，只能这么做)
			retMap.put(psnOrgVO.getPk_psnorg(), getHireStartDate(psnOrgVO));
		}
		return retMap;
	}

	/*
	 * 反结算 需要校验： 如果下期已经结算，或者存在同期大leaveindex的情况，则不能反结算 即，可以反结算的记录，应该满足条件：
	 * 1.下期未结算，且 2.不存在同期更大的leaveindex记录 (non-Javadoc)
	 * 
	 * @see nc.itf.ta.ILeaveBalanceManageMaintain#unSettlement(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * nc.vo.ta.leavebalance.LeaveBalanceVO[])
	 */
	@Override
	public UnSettlementResult unSettlement(String pk_org, String pk_leavetype, String year, String month,
			LeaveBalanceVO[] vos) throws BusinessException {
		UnSettlementResult result = new UnSettlementResult();
		result.setUnSettledVOs(vos);
		if (ArrayUtils.isEmpty(vos))
			return result;
		LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) NCLocator.getInstance().lookup(ITimeItemQueryService.class)
				.queryCopyTypesByDefPK(pk_org, pk_leavetype, TimeItemVO.LEAVE_TYPE);
		LeaveBalanceDAO balanceDAO = new LeaveBalanceDAO();
		balanceDAO.syncFromDB(typeVO, year, month, vos);
		LeaveBalanceVO[] canUnSettlementVOs = filterCanUnSettlementVOs(vos);
		if (ArrayUtils.isEmpty(canUnSettlementVOs))
			return result;
		// 不能反结算的标准：同一pk_psnorg的后期已经有结算了的,或者存在同期但leaveindex更大的记录（同一个pk_psnorg，在同期间内的多条结算记录，
		// 应该是顺次结算的，即，一条未结算的记录的后面，不可能存在index更大的同期记录）.这种属于严重错误，不该发生的，如果发生了，需要联系管理员解决
		String[] nextYearMonth = queryNextPeriod(pk_org, typeVO, year, month);
		String nextYear = nextYearMonth[0];
		String nextMonth = nextYearMonth.length == 2 ? nextYearMonth[1] : null;
		if (ArrayUtils.isEmpty(nextYearMonth))
			throw new BusinessException(getNoNextPeriodError());
		int leaveSetPeriod = typeVO.getLeavesetperiod().intValue();
		boolean isMonth = leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_MONTH;
		String pk_timeitem = typeVO.getPk_timeitem();
		String periodField = isMonth ? "curyear||curmonth" : "curyear";
		String badCond = "pk_org=? and pk_psnorg=? and pk_timeitem=? and ((" + periodField + ">? and "
				+ LeaveBalanceVO.ISSETTLEMENT + "='Y') or (" + periodField + "=? and " + LeaveBalanceVO.LEAVEINDEX
				+ ">?))";
		String nextPeriodCond = "pk_org=? and pk_psnorg=? and pk_timeitem=? and " + periodField + "=? and "
				+ LeaveBalanceVO.LEAVEINDEX + "=1";
		SQLParameter para = new SQLParameter();
		StringBuilder errMsg = new StringBuilder();
		List<LeaveBalanceVO> updateList = new ArrayList<LeaveBalanceVO>();
		Map<String, Integer> lineNoMap = createLineNoMap(vos);
		List<Integer> nextPeriodSealedLine = new ArrayList<Integer>();// 下期已结算的行
		for (LeaveBalanceVO vo : canUnSettlementVOs) {
			para.clearParams();
			para.addParam(pk_org);
			para.addParam(vo.getPk_psnorg());
			para.addParam(pk_timeitem);
			para.addParam(isMonth ? (year + month) : year);
			para.addParam(isMonth ? (year + month) : year);
			para.addParam(vo.getLeaveindex());
			LeaveBalanceVO[] badVOs = CommonUtils.retrieveByClause(LeaveBalanceVO.class, badCond, para);
			if (!ArrayUtils.isEmpty(badVOs)) {
				nextPeriodSealedLine.add(lineNoMap.get(vo.getPk_psnorg() + vo.getLeaveindex()));
				// errMsg.append(MessageFormat.format(getNextPeriodSealedError(),
				// Integer.toString(lineNoMap.get(vo.getPk_psnorg()+vo.getLeaveindex())+1)));
				continue;
			}
			Integer settlementmethod = vo.getSettlementmethod();
			if (settlementmethod == null) {
				settlementmethod = typeVO.getLeavesettlement();
			}
			int leaveSet = settlementmethod.intValue();// 上次结算此记录时的结算方式：作废，转下期，转工资
			// 走到这里，说明后期没有结算，如果当时本期结算时的结算参数是转下期，需要查询后期的结算记录，并设置其上期结余和结余
			if (leaveSet == LeaveTypeCopyVO.LEAVESETTLEMENT_NEXT) {
				para.clearParams();
				para.addParam(pk_org);
				para.addParam(vo.getPk_psnorg());
				para.addParam(pk_timeitem);
				para.addParam(isMonth ? (nextYear + nextMonth) : nextYear);
				LeaveBalanceVO[] nextPeriodVOs = CommonUtils.retrieveByClause(LeaveBalanceVO.class, nextPeriodCond,
						para);
				// 如果数据库有，则需要将上期结余设置为0，并且从结余中扣除
				if (!ArrayUtils.isEmpty(nextPeriodVOs)) {
					LeaveBalanceVO updateVO = nextPeriodVOs[0];
					updateList.add(updateVO);
					updateVO.setLastdayorhour(UFDouble.ZERO_DBL);// 将下期的“上期结余”set为0
					if (vo.getRestdayorhour() != null && vo.getRestdayorhour().doubleValue() != 0) {// 如果本期结余不为0，才有必要重算下期的结余
						updateVO.setRestdayorhour(updateVO.getRestdayorhour() == null ? UFDouble.ZERO_DBL.sub(vo
								.getRestdayorhour()) : updateVO.getRestdayorhour().sub(vo.getRestdayorhour()));
					}
				}
			}
			vo.setSettlementdate(null);
			vo.setSettlementmethod(null);
			vo.setIssettlement(UFBoolean.FALSE);
			vo.setSalaryyear(null);
			vo.setSalarymonth(null);
			updateList.add(vo);
		}
		if (updateList.size() > 0)
			new BaseDAO().updateVOList(updateList);
		if (nextPeriodSealedLine.size() > 0) {
			String errLineNo = getMultiLineNo(nextPeriodSealedLine);
			errMsg.append(MessageFormat.format(getNextPeriodSealedError(), errLineNo));
		}
		if (errMsg.length() > 0)
			result.setErrMsg(errMsg.toString());
		// 业务日志
		TaBusilogUtil.writeLeaveBalanceUnSettlementBusiLog(canUnSettlementVOs);
		return result;
	}

	private String getMultiLineNo(List<Integer> lines) {
		if (CollectionUtils.isEmpty(lines))
			return "";
		StringBuilder errLines = new StringBuilder();
		for (int line : lines) {
			errLines.append(line + 1).append(",");
		}
		errLines.deleteCharAt(errLines.length() - 1);
		return errLines.toString();
	}

	/**
	 * 从数组中找出可以反结算的vo： 如果还未结算，不能反结 如果被薪酬使用了，不能反结
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	private LeaveBalanceVO[] filterCanUnSettlementVOs(LeaveBalanceVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return vos;
		List<LeaveBalanceVO> retList = new ArrayList<LeaveBalanceVO>();
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		IDataPermissionPubService perimssionService = NCLocator.getInstance().lookup(IDataPermissionPubService.class);
		Map<String, UFBoolean> perimssionMap = perimssionService.isUserhasPermissionByMetaDataOperation("60170psndoc",
				StringPiecer.getStrArrayDistinct(vos, LeaveBalanceVO.PK_TBM_PSNDOC), "UnLeaveBalanceAction", pk_group,
				pk_user);
		for (LeaveBalanceVO vo : vos) {
			if (!vo.isSettlement() || vo.isUse())
				continue;
			// if(!DataPermissionFacade.isUserHasPermissionByMetaDataOperation(
			// pk_user, "60170psndoc", "UnLeaveBalanceAction",
			// pk_group, vo.toTBMPsndocVO()))
			// continue;
			if (perimssionMap != null && perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()) != null
					&& !perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()).booleanValue())
				continue;// 没有反结算权限的，刨除掉
			retList.add(vo);
		}
		return retList.size() == 0 ? null : retList.toArray(new LeaveBalanceVO[0]);
	}

	/**
	 * 适用于按入职日结算的查询 要求的查询结果是：如果pk_psnorg在year的入职年范围内与考勤档案有交集，则至少有一条结算记录
	 * 如果数据库中还没有，则应该在内存中生成一条 如果数据库中有，且存在未结算的，则返回数据库中的
	 * 如果数据库中有，且都结算了，则看最后一个结算的日期，若>=入职年最后一天，则返回数据中的
	 * 如果数据库中有，且都结算了，若最后一个结算的日期<入职年最后一天，则看结算日+1到入职年最后一天中此pk_psnorg是否
	 * 有考勤档案记录，若有，则内存中生成一条结算记录，否则只返回数据库中的
	 * 
	 * 此查询很困难，困难在于每个pk_psnorg的在year的入职年的开始结束日期都不一样
	 * 
	 * @param context
	 * @param pk_leavetype
	 * @param year
	 * @param month
	 * @param fromWhereSQL
	 * @return
	 * @throws BusinessException
	 */
	protected LeaveBalanceVO[] queryByCondition4HireDateSettlement(String pk_org, LeaveTypeCopyVO typeVO, String year,
			FromWhereSQL fromWhereSQL) throws BusinessException {
		// 首先，按照入职年的最大日期范围，查出一个大概的人员范围：
		UFLiteralDate yearEarliestDay = UFLiteralDate.getDate(year + "-01-01");
		UFLiteralDate yearLatesDay = UFLiteralDate.getDate(Integer.toString(Integer.parseInt(year) + 1) + "-12-31");
		ITBMPsndocQueryService tbmpsndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		// 以pk_psnorg分组，查询每组中最新的
		TBMPsndocVO[] psndocVOs = tbmpsndocService.queryPsnorgLatestByCondition(pk_org, fromWhereSQL, yearEarliestDay,
				yearLatesDay);
		// 如果没有考勤档案，则返回null
		if (ArrayUtils.isEmpty(psndocVOs))
			return null;
		// 否则查询这些人员的入职日期
		Map<String, UFLiteralDate> hireDateMap = queryHireDate(SQLHelper.getStrArray(psndocVOs, TBMPsndocVO.PK_PSNORG));// key-pk_psnorg,value-入职日期
		// 然后将这些记录插入临时表
		LeaveBalanceDAO leaveBalanceDAO = new LeaveBalanceDAO();
		leaveBalanceDAO.initHireDateTempTable(year, psndocVOs, hireDateMap);
		LeaveBalanceVO[] vos = leaveBalanceDAO.queryByCondition4HireDateSettlementWithHireDateTempTable(pk_org, typeVO,
				year, fromWhereSQL);
		return processMultiRecordsPerPsnorg(pk_org, typeVO, year, null, null, null, vos);
	}

	/**
	 * 处理一个pk_psnorg的所有可能的结算记录 逻辑是：首先将psnorg的vos按pk_psnorg分组，然后按pk_psnorg循环处理
	 * 如果存在未结算记录
	 * ，则此pk_psnorg的结算数据是完整的，不用再处理（如果此未结算的记录pk_leavebalance为空，则将其leaveindex set
	 * 为1） 如果所有记录都已结算，则分两种情况： 1.如果最晚的结算日期>=期间最后一天，则不处理
	 * 2.如果最晚的结算日期<期间最后一天，则从最晚的结算日期
	 * +1天到期间最后一天内，pk_psnorg有考勤档案记录，则需要用代码new一条，且此条的leaveindex=最晚的结算日期的index+1
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param month
	 *            ，月份，如果是按年结算或者按入职日结算，此参数可以为null
	 * @param periodBeginDate
	 *            ,如果是按年结算或按月结算，则是年或者期间的第一天；若是按入职日结算，则传空即可
	 * @param periodEndDate
	 *            ,如果是按年结算或按月结算，则是年或者期间的最后一天；若是按入职日结算，则传空即可
	 * @param vos
	 * @throws BusinessException
	 */
	private LeaveBalanceVO[] processMultiRecordsPerPsnorg(String pk_org, LeaveTypeCopyVO typeVO, String year,
			String month, UFLiteralDate periodBeginDate, UFLiteralDate periodEndDate, LeaveBalanceVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return vos;
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		String pk_leavetype = typeVO.getPk_timeitem();
		// 按pk_psnorg分组处理
		Map<String, LeaveBalanceVO[]> psnorgGroupMap = CommonUtils.group2ArrayByField(LeaveBalanceVO.PK_PSNORG, vos);
		// List<LeaveBalanceVO> voList = Arrays.asList(vos);
		ITBMPsndocQueryService tbmpsndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		boolean isHireDateSet = typeVO.getLeavesetperiod().intValue() == TimeItemCopyVO.LEAVESETPERIOD_DATE
		// ssx added on 2018-03-16
		// for changes of start date of company age
				|| typeVO.getLeavesetperiod().intValue() == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE;

		// BEGIN ssx 根据年资起算日重置hirebegindate, hireenddate
		if (typeVO.getLeavesetperiod().intValue() == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE) {
			for (LeaveBalanceVO vo : vos) {
				resetHirePeriodDates(year, vo);
			}
		}
		// end

		// END added on 2019-03-07
		for (String pk_psnorg : psnorgGroupMap.keySet()) {
			LeaveBalanceVO[] psnorgBalanceVOs = psnorgGroupMap.get(pk_psnorg);
			// 下面的循环中，找出两个信息：1.未结算的记录(有一条，或者没有)。2.最晚的一条结算记录（有一条，或者没有）
			// 如果1没有，那么2肯定有；如果2没有，那么1肯定有；
			LeaveBalanceVO unSetVO = null;
			LeaveBalanceVO lastSetVO = null;
			for (LeaveBalanceVO vo : psnorgBalanceVOs) {
				if (vo.getChangelength() == null)
					vo.setChangelength(UFDouble.ZERO_DBL);
				if (!vo.isSettlement()) {
					unSetVO = vo;
					break;
				}
				if (lastSetVO == null) {
					lastSetVO = vo;
					continue;
				}
				if (lastSetVO.getSettlementdate().before(vo.getSettlementdate()))
					lastSetVO = vo;
			}
			if (unSetVO != null
			// ssx added on 2018-03-16
			// for changes of start date of company age
					|| (lastSetVO != null && lastSetVO.isSettlement()) // 容錯處理：存在已結算的數據也不需要new新的
			//
			) {// 如果存在未结算的数据，则不用考虑new一个新的
				continue;
			}
			UFLiteralDate psnorgPeriodEndDate = isHireDateSet ? lastSetVO.getHireenddate() : periodEndDate;
			// 如果所有的都结算了，那么需要将最晚的结算日期与期间结束日比较（对于按入职日结算的，期间结束日每个人都可能不一样）
			// 如果最晚结算日在期间最后一天之后，则视为正常
			if (lastSetVO.getSettlementdate().after(psnorgPeriodEndDate))
				continue;
			// 如果最晚结算的一条记录，是在期间结束之前结算的，则要看结算日到期间结束日之间是否有pk_psnorg的考勤档案记录，如果有，则需要new一条记录
			UFLiteralDate beginDate = lastSetVO.getSettlementdate().getDateAfter(1);
			TBMPsndocVO latestTbmPsndocVO = tbmpsndocService.queryLatestByPsnorgDate(pk_org, pk_psnorg, beginDate,
					psnorgPeriodEndDate);
			if (latestTbmPsndocVO == null)
				continue;
			LeaveBalanceVO newVO = new LeaveBalanceVO();
			// voList.add(newVO);
			vos = (LeaveBalanceVO[]) ArrayUtils.add(vos, newVO);
			newVO.setPk_group(pk_group);
			newVO.setPk_org(pk_org);
			newVO.setPk_psndoc(latestTbmPsndocVO.getPk_psndoc());
			newVO.setPk_tbm_psndoc(latestTbmPsndocVO.getPk_tbm_psndoc());
			newVO.setPk_psnorg(pk_psnorg);
			newVO.setPk_psnjob(latestTbmPsndocVO.getPk_psnjob());
			newVO.setPk_timeitem(pk_leavetype);
			newVO.setLeaveindex(psnorgBalanceVOs.length + 1);
		}
		// return vos.length==voList.size()?vos:voList.toArray(new
		// LeaveBalanceVO[0]);
		return vos;
	}

	// ssx created on 2019-03-09
	// for reset hirebegindate, hireenddate, periodbegindate, periodenddate,
	// periodextendenddate of LeaveBalanceVO
	public void resetHirePeriodDates(String year, LeaveBalanceVO vo) throws BusinessException {
		UFLiteralDate[] beginenddates = getSpecialBeginEnd(vo.getPk_psndoc(), year, vo.getPk_psnorg());
		vo.setHirebegindate(beginenddates[0]);
		vo.setHireenddate(beginenddates[1]);
		int days = UFLiteralDate.getDaysBetween(vo.getPeriodenddate(), vo.getPeriodextendenddate());
		vo.setPeriodbegindate(beginenddates[0]);
		vo.setPeriodenddate(beginenddates[1]);
		vo.setPeriodextendenddate(beginenddates[1].getDateAfter(days));
	}

	// end

	/**
	 * 适用于按年、期间结算的查询 查询的逻辑是，首先确定期间的开始、结束日期
	 * 数据规则是：pk_psnorg在期间范围内存在考勤档案记录，则此pk_psnorg将至少拥有一条结算记录
	 * ，且最多有一条未结算的记录，且未结算的记录只能是最后一条（leaveindex最大的一条） 然后使用 select from 考勤档案 left
	 * join 假期结余表 来查询，查询的结果，肯定是每个pk_psnorg至少有一条记录（因为是left join）
	 * 然后按pk_psnorg分组，对分组数据进行处理
	 * 如果存在未结算记录，则此pk_psnorg的结算数据是完整的，不用再处理（如果此未结算的记录pk_leavebalance为空
	 * ，则将其leaveindex set 为1） 如果所有记录都已结算，则分两种情况： 1.如果最晚的结算日期>=期间最后一天，则不处理
	 * 2.如果最晚的结算日期 <期间最后一天，则从最晚的结算日期+1天到期间最后一天内，pk_psnorg有考勤档案记录，则需要用代码new一条，
	 * 且此条的leaveindex =最晚的结算日期的index+1
	 * 
	 * @param context
	 * @param pk_leavetype
	 * @param year
	 * @param month
	 * @param fromWhereSQL
	 * @return
	 * @throws BusinessException
	 */
	protected LeaveBalanceVO[] queryByCondition4YearMonthSettlement(String pk_org, LeaveTypeCopyVO typeVO, String year,
			String month, FromWhereSQL fromWhereSQL) throws BusinessException {

		UFLiteralDate periodBeginDate = null;
		UFLiteralDate periodEndDate = null;
		UFLiteralDate[] periodBeginEndDates = queryPeriodBeginEndDate(pk_org, typeVO, year, month);
		if (periodBeginEndDates != null && periodBeginEndDates.length == 2) {
			periodBeginDate = periodBeginEndDates[0];
			periodEndDate = periodBeginEndDates[1];
		}
		String pk_leavetype = typeVO.getPk_timeitem();

		int leavesetperiod = typeVO.getLeavesetperiod().intValue();
		boolean isYear = leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR;
		LeaveBalanceVO[] vos = new LeaveBalanceDAO().queryByDateScope(pk_org, fromWhereSQL, periodBeginDate,
				periodEndDate, pk_leavetype, year, isYear ? null : month);
		if (!ArrayUtils.isEmpty(vos)) {
			UFLiteralDate priodExtendEndDate = typeVO.getExtendDaysCount() == 0 ? periodEndDate : periodEndDate
					.getDateAfter(typeVO.getExtendDaysCount());
			for (LeaveBalanceVO vo : vos) {
				vo.setPeriodbegindate(periodBeginDate);
				vo.setPeriodenddate(periodEndDate);
				vo.setPeriodextendenddate(priodExtendEndDate);
				if (vo.getChangelength() == null)
					vo.setChangelength(UFDouble.ZERO_DBL);
			}
		}
		return processMultiRecordsPerPsnorg(pk_org, typeVO, year, month, periodBeginDate, periodEndDate, vos);
	}

	/**
	 * 查询按年结算或按期间结算时，结算周期的第一天和最后一天
	 * 如果是按入职日结算，则返回null，因为按入职日结算时，每个人的结算周期都有可能不一样，需要按具体的人来处理
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	private UFLiteralDate[] queryPeriodBeginEndDate(String pk_org, LeaveTypeCopyVO typeVO, String year, String month,
			boolean throwsException) throws BusinessException {
		int leavesetperiod = typeVO.getLeavesetperiod().intValue();
		if (leavesetperiod == TimeItemCopyVO.LEAVESETPERIOD_DATE
		// ssx added on 2018-03-16
		// for changes of start date of company age
				|| leavesetperiod == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE)
			//
			return null;
		boolean isYear = leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR;
		UFLiteralDate periodBeginDate = null;
		UFLiteralDate periodEndDate = null;
		if (!isYear) {
			PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
			if (periodVO == null) {
				throw new BusinessException(ResHelper.getString("6017leave", "06017leave0266"), "perioderror");
			}
			periodBeginDate = periodVO.getBegindate();
			periodEndDate = periodVO.getEnddate();
		} else {
			PeriodVO[] periodVOs = PeriodServiceFacade.queryByYear(pk_org, year);
			// if(periodVOs==null||periodVOs.length<1)return null;
			if (ArrayUtils.isEmpty(periodVOs)) {
				if (throwsException)
					throw new BusinessException(ResHelper.getString("6017leave", "06017leave0231"
					/* @res "{0}年考勤期间未定义!" */, year), "perioderror");
				return null;
			}
			// // MOD 按自然年结算 James
			// periodBeginDate = new UFLiteralDate(year + "-01-01");
			// periodEndDate = new UFLiteralDate(year + "-12-31");
			periodBeginDate = periodVOs[0].getBegindate();
			periodEndDate = periodVOs[periodVOs.length - 1].getEnddate();
		}
		return new UFLiteralDate[] { periodBeginDate, periodEndDate };
	}

	/**
	 * 查询按年结算或按期间结算时，结算周期的第一天和最后一天
	 * 如果是按入职日结算，则返回null，因为按入职日结算时，每个人的结算周期都有可能不一样，需要按具体的人来处理
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	private UFLiteralDate[] queryPeriodBeginEndDate(String pk_org, LeaveTypeCopyVO typeVO, String year, String month)
			throws BusinessException {
		return queryPeriodBeginEndDate(pk_org, typeVO, year, month, true);
	}

	// /**
	// * 简单查询已计算的假期数据
	// */
	// @SuppressWarnings("unchecked")
	// @Override
	// public LeaveBalanceVO[] queryByCondition(String pk_leavetype,
	// String year,String pk_psndoc)throws BusinessException{
	// String cond = LeaveBalanceVO.PK_TIMEITEM+"=? and "
	// +LeaveBalanceVO.CURYEAR+"=? and "+LeaveBalanceVO.PK_PSNDOC+"=? ";
	// SQLParameter para = new SQLParameter();
	// para.addParam(pk_leavetype);
	// para.addParam(year);
	// para.addParam(pk_psndoc);
	//
	// LeaveBalanceVO[] vos = (LeaveBalanceVO[])
	// CommonMethods.toArray(LeaveBalanceVO.class, new
	// BaseDAO().retrieveByClause(LeaveBalanceVO.class, cond, para));
	//
	// return setDefaultTimeitemCopy(vos);
	// }

	@Override
	public LeaveBalanceVO[] queryByCondition(String pk_org, String pk_leavetype, String year, String month,
			FromWhereSQL fromWhereSQL, String pk_dept, boolean containsSubDepts) throws BusinessException {
		ITimeItemQueryService service = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) service.queryCopyTypesByDefPK(pk_org, pk_leavetype,
				TimeItemCopyVO.LEAVE_TYPE);
		return queryByCondition(pk_org, typeVO, year, month,
				TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL));
	}

	// @Override
	// public LeaveBalanceVO[] queryByCondition(LoginContext context,
	// String pk_leavetype,String year,String month,String pk_psndoc)
	// throws BusinessException {
	// FromWhereSQL fromWhereSQL =
	// TBMPsndocSqlPiecer.createPsndocQuerySQL(pk_psndoc);
	// return queryByCondition(context, pk_leavetype, year, month,
	// fromWhereSQL);
	// }

	@Override
	public LeaveBalanceVO[] queryByCondition(LoginContext context, String pk_leavetype, String year, String month,
			FromWhereSQL fromWhereSQL) throws BusinessException {

		ITimeItemQueryService service = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) service.queryCopyTypesByDefPK(context.getPk_org(), pk_leavetype,
				TimeItemCopyVO.LEAVE_TYPE);
		// 考虑权限
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		return queryByCondition(context.getPk_org(), typeVO, year, month, fromWhereSQL);

	}

	protected LeaveBalanceVO[] queryByCondition(String pk_org, LeaveTypeCopyVO typeVO, String year, String month,
			FromWhereSQL fromWhereSQL) throws BusinessException {
		LeaveBalanceVO[] leaveBalanceVOs = null;
		int leavesetperiod = typeVO.getLeavesetperiod().intValue();
		if (leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE
		// ssx added on 2018-03-16
		// for changes of start date of company age
				|| leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE//
		) {
			leaveBalanceVOs = setDefaultTimeitemCopy(queryByCondition4HireDateSettlement(pk_org, typeVO, year,
					fromWhereSQL));
		} else {
			leaveBalanceVOs = setDefaultTimeitemCopy(queryByCondition4YearMonthSettlement(pk_org, typeVO, year, month,
					fromWhereSQL));
		}
		return leaveBalanceVOs;
	}

	@Override
	public SettlementResult firstSettlement(String pk_org, String pk_leavetype, String year, String month,
			LeaveBalanceVO[] vos, String pk_salaryPeriod) throws BusinessException {
		UFLiteralDate settlementDate = PubEnv.getServerLiteralDate();
		// 首先查询是否有：
		// 1.结算日>=结算周期最后一天，但未到有效期天（只针对按入职日结算的情形，按年按期间结算的不用此查询，因为所有人的期间最后一天都是一样的，如果提示明细就比较怪）
		// 2.未到期间最后一天，但已无考勤档案
		// 如果有，则先不执行结算，将这些需要提示的信息返回到客户端
		// 否则，直接执行结算（结算可以结算的，不能结算的跳过，但是要有描述信息）
		ITimeItemQueryService service = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) service.queryCopyTypesByDefPK(pk_org, pk_leavetype,
				TimeItemCopyVO.LEAVE_TYPE);
		LeaveBalanceDAO balanceDAO = new LeaveBalanceDAO();
		balanceDAO.syncFromDB(typeVO, year, month, vos);
		int leavesetPeriod = typeVO.getLeavesetperiod().intValue();

		PeriodVO salaryPeriodVO = null;
		String salaryYear = null;
		String salaryMonth = null;
		IPeriodQueryService periodQuery = NCLocator.getInstance().lookup(IPeriodQueryService.class);
		if (TimeItemCopyVO.LEAVESETTLEMENT_MONEY == typeVO.getLeavesettlement()) {// 若是转薪资的，需要确认把薪资转移到哪个考勤期间，若期间为空默认当前期间
			if (StringUtils.isBlank(pk_salaryPeriod)) {
				salaryPeriodVO = periodQuery.queryCurPeriod(pk_org);
			} else {
				salaryPeriodVO = (PeriodVO) new BaseDAO().retrieveByPK(PeriodVO.class, pk_salaryPeriod);
			}
			salaryYear = salaryPeriodVO.getYear();
			salaryMonth = salaryPeriodVO.getMonth();
		} else {
			salaryYear = year;
			salaryMonth = month;
		}
		// ssx added on 2018-03-16
		// for changes of start date of company age
		if (leavesetPeriod == TimeItemCopyVO.LEAVESETPERIOD_DATE
				|| leavesetPeriod == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE)
			//
			return firstSettlement4HireDate(pk_org, typeVO, year, vos, settlementDate, salaryYear, salaryMonth);
		return firstSettlement4YearMonth(pk_org, typeVO, year, month, vos, settlementDate, salaryYear, salaryMonth);
	}

	/**
	 * 针对按年或者期间结算的休假类别的第一次结算调用 这两种都是固定期间 如果结算日<期间首天，则抛异常 如果结算日>=有效期，则不用任何提示
	 * 也即，只有在结算日在期间首天至有效期的范围内，才有可能需要提示用户 如果结算日<=期间末天，则只可能提示考勤档案在结算日到期间末天不存在的人员
	 * 如果结算日在期间末天到有效期之内
	 * ，则可能需要提示两种人员：a.结算日到有效期内无考勤档案的人员b.结算日到有效期内有考勤档案的人员，这两种人员用户可能要区别对待，因此要分别提示
	 * 综上所述，可能提示的有三种人员，但三种不可能同时出现
	 * 
	 * @param salaryMonth
	 * @param salaryYear
	 */
	protected SettlementResult firstSettlement4YearMonth(String pk_org, LeaveTypeCopyVO typeVO, String year,
			String month, LeaveBalanceVO[] vos, UFLiteralDate settlementDate, String salaryYear, String salaryMonth)
			throws BusinessException {
		UFLiteralDate[] periodBeginEndDate = queryPeriodBeginEndDate(pk_org, typeVO, year, month);
		UFLiteralDate periodBeginDate = periodBeginEndDate[0];
		UFLiteralDate periodEndDate = periodBeginEndDate[1];
		SettlementGroupVO sgv = groupSettlementVOs4YearMonth(pk_org, typeVO, year, periodBeginDate, periodEndDate, vos,
				settlementDate);
		SettlementResult result = new SettlementResult();
		result.setQueryUserVOsNotToEffectiveDate4ExistTbmPsndoc(sgv.notToEffectiveDateExistTbmPsndocVOs);
		result.setQueryUserVOsNotToEffectiveDate4NoTbmPsndoc(sgv.notToEffectiveDateNoTbmPsndocVOs);
		result.setQueryUserVOsNotToPeriodEndDateNoTbmPsndoc(sgv.notToPeriodEndDateNoTbmPsndocVOs);
		result.setSettledVOs(vos);
		if (result.needQueryUser())// 如果有需要询问用户的情况，则将需要询问的数据返回到前台，不做后续处理
			return result;
		String[] nextYearMonth = queryNextPeriod(pk_org, typeVO, year, month);
		if (ArrayUtils.isEmpty(nextYearMonth))
			throw new BusinessException(getNoNextPeriodError());
		LeaveBalanceVO[] canSettleVOs = sgv.toCanSettleVOs();
		if (ArrayUtils.isEmpty(canSettleVOs))
			return result;
		UFLiteralDate[] nextPeriodBeginEndDate = queryPeriodBeginEndDate(pk_org, typeVO, nextYearMonth[0],
				nextYearMonth.length == 2 ? nextYearMonth[1] : null);
		PeriodVO nextPeriod = new PeriodVO();
		nextPeriod.setTimeyear(nextYearMonth[0]);
		nextPeriod.setTimemonth(nextYearMonth.length == 2 ? nextYearMonth[1] : null);
		nextPeriod.setBegindate(nextPeriodBeginEndDate[0]);
		nextPeriod.setEnddate(nextPeriodBeginEndDate[1]);
		// 前一个期间/年度
		String[] previousYearMonth = queryPreviousPeriod(pk_org, typeVO, year, month);
		PeriodVO previousPeriod = null;
		if (!ArrayUtils.isEmpty(previousYearMonth)) {
			UFLiteralDate[] previousPeriodBeginEndDate = queryPeriodBeginEndDate(pk_org, typeVO, previousYearMonth[0],
					previousYearMonth.length == 2 ? previousYearMonth[1] : null, false);
			if (!ArrayUtils.isEmpty(previousPeriodBeginEndDate)) {
				previousPeriod = new PeriodVO();
				previousPeriod.setTimeyear(previousYearMonth[0]);
				previousPeriod.setTimemonth(previousYearMonth.length == 2 ? previousYearMonth[1] : null);
				previousPeriod.setBegindate(previousPeriodBeginEndDate[0]);
				previousPeriod.setEnddate(previousPeriodBeginEndDate[1]);
			}
		}
		settlement(pk_org, typeVO, year, month, previousPeriod, nextPeriod, canSettleVOs,
				createLineNoMap(canSettleVOs), settlementDate, result, salaryYear, salaryMonth);
		return result;
	}

	/**
	 * 针对按入职日结算的休假类别的第一次结算调用 如果结算日<year的首天，则抛异常 如果结算日>year+1的末天，则不用任何提示
	 * 也即，只有在year首天到year+1的末天范围内，才有可能有提示 提示的人员分为三种： 1.还未到期末天，但结算日到期末天内已无考勤档案的人员
	 * 2.还未到有效期，但结算日到有效期已无考勤档案的人员 3.还未到有效期，结算日到有效期内还有考勤档案的人员
	 * 
	 * @param salaryMonth
	 * @param salaryYear
	 */
	protected SettlementResult firstSettlement4HireDate(String pk_org, LeaveTypeCopyVO typeVO, String year,
			LeaveBalanceVO[] vos, UFLiteralDate settlementDate, String salaryYear, String salaryMonth)
			throws BusinessException {
		SettlementGroupVO sgv = groupSettlementVOs4HireDate(pk_org, typeVO, year, vos, settlementDate);
		SettlementResult result = new SettlementResult();
		result.setQueryUserVOsNotToEffectiveDate4ExistTbmPsndoc(sgv.notToEffectiveDateExistTbmPsndocVOs);
		result.setQueryUserVOsNotToEffectiveDate4NoTbmPsndoc(sgv.notToEffectiveDateNoTbmPsndocVOs);
		result.setQueryUserVOsNotToPeriodEndDateNoTbmPsndoc(sgv.notToPeriodEndDateNoTbmPsndocVOs);

		// //MOD 张恒｛21481｝获取特休假人员 2018/8/21
		// LeaveBalanceVO[] specialPsnVOs = sgv.specialPsnVOs;

		result.setSettledVOs(vos);
		if (result.needQueryUser())// 如果有需要询问用户的情况，则将需要询问的数据返回到前台，不做后续处理
			return result;
		LeaveBalanceVO[] canSettleVOs = sgv.toCanSettleVOs();
		if (ArrayUtils.isEmpty(canSettleVOs)) {
			result.setErrMsg("结算失败");
			return result;
		}
		String nextYear = Integer.toString(Integer.parseInt(year) + 1);
		PeriodVO nextPeriod = new PeriodVO();
		nextPeriod.setTimeyear(nextYear);
		String previousYear = Integer.toString(Integer.parseInt(year) - 1);
		PeriodVO previousPeriod = new PeriodVO();
		previousPeriod.setTimeyear(previousYear);
		settlement(pk_org, typeVO, year, null, previousPeriod, nextPeriod, canSettleVOs, createLineNoMap(canSettleVOs),
				settlementDate, result, salaryYear, salaryMonth);
		return result;
	}

	private Map<String, Integer> createLineNoMap(LeaveBalanceVO[] vos) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < vos.length; i++) {
			LeaveBalanceVO vo = vos[i];
			map.put(vo.getPk_psnorg() + vo.getLeaveindex(), i);
		}
		return map;
	}

	// BEGIN 张恒{21481} 新增特休假人员（递延次年） specialPsnVOs为特休假人员集合 2018/8/21
	@SuppressWarnings("unchecked")
	private void settlement(String pk_org, LeaveTypeCopyVO typeVO, String year, String month, PeriodVO previousPeriod,
			PeriodVO nextPeriod, LeaveBalanceVO[] vos, Map<String, Integer> lineNoMap,// 由于此方法传入的vos是界面vos的子集，为了正确地提示行号，需要将vo的行号放入map，key是pk_psnorg+leaveindex
			UFLiteralDate settlementDate, SettlementResult result, String salaryYear, String salaryMonth)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;

		// //BEGIN 张恒｛21481｝ 把特休假人员和入职日人员融合在一起 2018/8/21
		// if(null == vos && null != specialPsnVOs){
		// vos = specialPsnVOs;
		// }else if(null != vos && null != specialPsnVOs){
		// int index = 0;
		// int vosIndex = vos.length;
		// int specialIndex = specialPsnVOs.length;
		// vos = Arrays.copyOf(vos,vosIndex+specialIndex);
		// for (int i = vosIndex; i < vosIndex+specialIndex; i++) {
		// vos[i] = specialPsnVOs[index];
		// index++;
		// }
		// }
		// //END 张恒｛21481｝ 把特休假人员和入职日人员融合在一起 2018/8/21

		// 结算前，需要先计算
		UFDateTime calTime = new UFDateTime(settlementDate.toString() + " 00:00:00");
		UFDateTime now = new UFDateTime();
		if (calTime.before(now))
			calTime = now;
		vos = calculate(pk_org, typeVO, year, month, vos, calTime, true);
		// 不能结算的标准：同一pk_psnorg的后期已经有结算了的,或者存在同期但leaveindex更大的记录（同一个pk_psnorg，在同期间内的多条结算记录，
		// 应该是顺次结算的，即，一条未结算的记录的后面，不可能存在index更大的同期记录）.这种属于严重错误，不该发生的，如果发生了，需要联系管理员解决
		int leaveSetPeriod = typeVO.getLeavesetperiod().intValue();
		boolean isMonth = leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_MONTH;
		int leaveSet = typeVO.getLeavesettlement().intValue();// 结算方式：作废，转下期，转工资
		String pk_timeitem = typeVO.getPk_timeitem();
		String periodField = isMonth ? "curyear||curmonth" : "curyear";
		String nextPeriodBadCond = "pk_org=? and pk_psnorg=? and pk_timeitem=? and ((" + periodField + ">? and "
				+ LeaveBalanceVO.ISSETTLEMENT + "='Y') or (" + periodField + "=? and " + LeaveBalanceVO.LEAVEINDEX
				+ ">?))";
		String nextPeriodCond = "pk_org=? and pk_psnorg in ("
				+ new InSQLCreator().getInSQL(vos, LeaveBalanceVO.PK_PSNORG) + ") and pk_timeitem=? and " + periodField
				+ "=? and " + LeaveBalanceVO.LEAVEINDEX + "=1";

		String preLeaveIndexBadCond = "pk_org=? and pk_psnorg=? and pk_timeitem=? and " + LeaveBalanceVO.ISSETTLEMENT
				+ "='Y' and " + periodField + "=? and " + LeaveBalanceVO.LEAVEINDEX + "=?";
		String prePeriodCond = "pk_org=? and pk_psnorg=? and pk_timeitem=? and " + periodField + "=?";

		BaseDAO dao = new BaseDAO();
		SQLParameter para = new SQLParameter();
		StringBuilder errMsg = new StringBuilder();
		List<LeaveBalanceVO> updateList = new ArrayList<LeaveBalanceVO>();
		List<LeaveBalanceVO> insertList = new ArrayList<LeaveBalanceVO>();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		List<Integer> nextPeriodSealedLine = new ArrayList<Integer>();// 下期已结算的行
		List<Integer> samePreRecordNotSealedLine = new ArrayList<Integer>();// 同期更早记录还未结算的行
		List<Integer> previousPeriodNotSealedLine = new ArrayList<Integer>();// 上期还未结算的行
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);

		// 前期为空时不需要查询考勤档案，因为后续也不会使用
		TBMPsndocVO[] existtbmpsndocs = previousPeriod == null ? null : psndocService.queryTBMPsndocByPsnorgs(pk_org,
				StringPiecer.getStrArray(vos, LeaveBalanceVO.PK_PSNORG), previousPeriod.getBegindate(),
				previousPeriod.getEnddate());
		Map<Object, TBMPsndocVO[]> tbmpsnExistMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_PSNORG,
				existtbmpsndocs);
		boolean existMapIsNull = MapUtils.isEmpty(tbmpsnExistMap);

		// BEGIN 张恒 当为年资起算日的时候，需要取年资的日期 2018/9/6
		// 取入职日期 或者年资日期
		Map<String, UFLiteralDate> psnOrgDateMap = new HashMap<String, UFLiteralDate>();
		InSQLCreator isc = new InSQLCreator();
		try {
			String psnOrgInSql = isc.getInSQL(StringPiecer.getStrArray(vos, LeaveBalanceVO.PK_PSNORG));
			PsnOrgVO[] psnOrgVOs = CommonUtils.retrieveByClause(PsnOrgVO.class, " pk_psnorg in (" + psnOrgInSql + ") ");
			if (!ArrayUtils.isEmpty(psnOrgVOs)) {
				for (PsnOrgVO psnOrgVO : psnOrgVOs)

					// MOD 张恒 如果按照年资起算日结算，需要取到年资的起始日期
					if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
						UFLiteralDate[] specialBeginEnd = getSpecialBeginEnd(psnOrgVO.getPk_psndoc(), year,
								psnOrgVO.getPk_psnorg());
						UFLiteralDate beginDate = specialBeginEnd[0];
						psnOrgDateMap.put(psnOrgVO.getPk_psnorg(), beginDate);
					} else {
						psnOrgDateMap.put(psnOrgVO.getPk_psnorg(), psnOrgVO.getBegindate());
					}
			}
		} finally {
			isc.clear();
		}
		// END 张恒 当为年资起算日的时候，需要取年资的日期 2018/9/6

		para.clearParams();
		para.addParam(pk_org);
		para.addParam(pk_timeitem);
		para.addParam(isMonth ? (nextPeriod.getTimeyear() + nextPeriod.getTimemonth()) : nextPeriod.getTimeyear());
		LeaveBalanceVO[] nextPeriodVOs = (LeaveBalanceVO[]) CommonUtils.toArray(LeaveBalanceVO.class,
				dao.retrieveByClause(LeaveBalanceVO.class, nextPeriodCond, para));
		Map<String, List<LeaveBalanceVO>> balanceMap = new HashMap<String, List<LeaveBalanceVO>>();
		if (!ArrayUtils.isEmpty(nextPeriodVOs)) {
			for (LeaveBalanceVO vo : nextPeriodVOs) {
				if (!balanceMap.containsKey(vo.getPk_psnorg())) {
					balanceMap.put(vo.getPk_psnorg(), new ArrayList<LeaveBalanceVO>());
				}
				balanceMap.get(vo.getPk_psnorg()).add(vo);
			}
		}

		// MOD 张恒 本地化新增 是否个别递延与结算假期 2018/9/6
		UFBoolean isSpecialrest = typeVO.getIsspecialrest();

		for (LeaveBalanceVO vo : vos) {

			// BEGIN 张恒 找到年资起算日和年资结束日 2018/9/6
			UFLiteralDate[] specialBeginEnd = null;
			UFLiteralDate beginDate = null;
			UFLiteralDate endDate = null;
			if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
				specialBeginEnd = getSpecialBeginEnd(vo.getPk_psndoc(), year, vo.getPk_psnorg());
			}
			if (!ArrayUtils.isEmpty(specialBeginEnd)) {
				if (null != specialBeginEnd[0] && null != specialBeginEnd[1]) {
					beginDate = specialBeginEnd[0];
					endDate = specialBeginEnd[1];
				} else {
					throw new BusinessException("人员主键为" + vo.getPk_psndoc() + "的员工年资起始日期为空");
				}
			}

			// MOD 张恒 本地化找到当前人在考勤期间是否为递延次年 2018/09/06
			Object specialrest = null;
			if (null != isSpecialrest && isSpecialrest.booleanValue()) {
				String startDate = "";
				String overDate = "";
				// 按照年资结算
				if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
					startDate = beginDate.toString();
					overDate = endDate.toString();
					// 按照入职结算
				} else if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE) {
					startDate = vo.getHirebegindate().toString();
					overDate = vo.getHireenddate().toString();
					// 按照年结算
				} else if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_YEAR) {
					String tbmPeriodSql = " select min(begindate)begindate,max(enddate)enddate from tbm_period where accyear = '"
							+ year + "' and pk_org = '" + vo.getPk_org() + "' ";
					List<Object[]> tbmPeriodList = (List<Object[]>) baseDao.executeQuery(tbmPeriodSql,
							new ArrayListProcessor());
					if (!CollectionUtils.isEmpty(tbmPeriodList)) {
						if (null != tbmPeriodList.get(0)) {
							startDate = tbmPeriodList.get(0)[0] == null ? "" : tbmPeriodList.get(0)[0].toString();
							overDate = tbmPeriodList.get(0)[1] == null ? "" : tbmPeriodList.get(0)[1].toString();
							if ("".equals(startDate) || "".equals(overDate)) {
								throw new BusinessException(year + "年考勤档案日期为空");
							}
						}
					}
				}
				if (!"".equals(startDate) && !"".equals(overDate)) {
					String specialSql = " select specialrest from tbm_psndoc where pk_psndoc = '" + vo.getPk_psndoc()
							+ "' and pk_org" + " = '" + vo.getPk_org() + "' and isnull(enddate,'9999-01-01') >= '"
							+ overDate + "' order by begindate desc";
					specialrest = baseDao.executeQuery(specialSql, new ColumnProcessor());
					specialrest = specialrest == null ? "" : specialrest.toString();
				}
			}
			// END 张恒 找到年资起算日和年资结束日 2018/9/6

			para.clearParams();
			para.addParam(pk_org);
			para.addParam(vo.getPk_psnorg());
			para.addParam(pk_timeitem);
			para.addParam(isMonth ? (year + month) : year);
			para.addParam(isMonth ? (year + month) : year);
			para.addParam(vo.getLeaveindex());
			LeaveBalanceVO[] badVOs = (LeaveBalanceVO[]) CommonUtils.toArray(LeaveBalanceVO.class,
					dao.retrieveByClause(LeaveBalanceVO.class, nextPeriodBadCond, para));
			if (!ArrayUtils.isEmpty(badVOs)) {
				// errMsg.append(MessageFormat.format(getNextPeriodSealedError(),
				// Integer.toString(lineNoMap.get(vo.getPk_psnorg()+vo.getLeaveindex())+1)));
				nextPeriodSealedLine.add(lineNoMap.get(vo.getPk_psnorg() + vo.getLeaveindex()));
				continue;
			}
			// 如果此条记录是在同期/同年还有更老的记录，则要求上一条必须已经结算
			if (vo.getLeaveindex().intValue() > 1) {
				para.clearParams();
				para.addParam(pk_org);
				para.addParam(vo.getPk_psnorg());
				para.addParam(pk_timeitem);
				para.addParam(isMonth ? (year + month) : year);
				para.addParam(vo.getLeaveindex().intValue() - 1);
				badVOs = (LeaveBalanceVO[]) CommonUtils.toArray(LeaveBalanceVO.class,
						dao.retrieveByClause(LeaveBalanceVO.class, preLeaveIndexBadCond, para));
				if (ArrayUtils.isEmpty(badVOs) || !badVOs[0].isSettlement()) {
					// errMsg.append(MessageFormat.format(getSamePreRecordNotSealedError(),
					// Integer.toString(lineNoMap.get(vo.getPk_psnorg()+vo.getLeaveindex())+1)));
					samePreRecordNotSealedLine.add(lineNoMap.get(vo.getPk_psnorg() + vo.getLeaveindex()));
					continue;
				}
			}
			// 如果leaveindex=1，且考勤期间有上期/或者上一年度，则还要检查此人在上期/上一年度是否有考勤档案，若有，则要求：若数据库没有上期结算记录，则抛异常；若有记录，要求全部都已结算
			else if (previousPeriod != null) {
				boolean existsTBMPsndoc = false;
				if (leaveSetPeriod != LeaveTypeCopyVO.LEAVESETPERIOD_DATE // 按年和期间结算的，下期的日期范围是固定的
						// ssx added on 2018-03-16
						// for changes of start date of company age
						&& leaveSetPeriod != LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE//
				) {
					// 这一部分条件比较固定，可以优化提取到循环外面
					// existsTBMPsndoc = psndocService.existsTBMPsndoc(pk_org,
					// vo.getPk_psnorg(), previousPeriod.getBegindate(),
					// previousPeriod.getEnddate());
					existsTBMPsndoc = !existMapIsNull && !ArrayUtils.isEmpty(tbmpsnExistMap.get(vo.getPk_psnorg()));
				} else {

					// BEGIN 张恒｛21481｝ 将入职和年资分开计算 2018/8/21
					// 按照入职来算
					if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE) {
						// 如果为按入职日期结算，且当前单据开始日期与入职日期一样，则不再找上期（为了排除工作记录开始日期比组织关系开始日期早的特殊情况）
						if (psnOrgDateMap.get(vo.getPk_psnorg()) == null
								|| !psnOrgDateMap.get(vo.getPk_psnorg()).equals(vo.getHirebegindate()))
							existsTBMPsndoc = psndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(),
									getHireBeginDate(previousPeriod.getYear(), vo.getHirebegindate()), vo
											.getHirebegindate().getDateBefore(1));
						// MOD 张恒 按照年资起算日来计算 2018/8/23
					} else if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
						// typeVO.getLeaveextendcount() 为有限延长期限
						// if (psnOrgDateMap.get(vo.getPk_psnorg()) == null
						// ||
						// !psnOrgDateMap.get(vo.getPk_psnorg()).equals(beginDate))
						// {
						existsTBMPsndoc = psndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(),
								getHireBeginDate(previousPeriod.getYear(), beginDate), beginDate.getDateBefore(1));
						// }
					}
					// END 张恒｛21481｝ 将入职和年资分开计算 2018/8/21

				}
				if (existsTBMPsndoc) {
					para.clearParams();
					para.addParam(pk_org);
					para.addParam(vo.getPk_psnorg());
					para.addParam(pk_timeitem);
					para.addParam(isMonth ? (previousPeriod.getTimeyear() + previousPeriod.getTimemonth())
							: previousPeriod.getTimeyear());
					badVOs = (LeaveBalanceVO[]) CommonUtils.toArray(LeaveBalanceVO.class,
							dao.retrieveByClause(LeaveBalanceVO.class, prePeriodCond, para));
					boolean prePeriodNotSealed = false;
					if (ArrayUtils.isEmpty(badVOs))
						prePeriodNotSealed = true;
					else {
						for (LeaveBalanceVO badVO : badVOs) {
							if (!badVO.isSettlement()) {
								prePeriodNotSealed = true;
								break;
							}
						}
					}
					if (prePeriodNotSealed) {
						// errMsg.append(MessageFormat.format(getPreviousPeriodNotSealedError(),
						// Integer.toString(lineNoMap.get(vo.getPk_psnorg()+vo.getLeaveindex())+1)));
						previousPeriodNotSealedLine.add(lineNoMap.get(vo.getPk_psnorg() + vo.getLeaveindex()));
						continue;
					}
				}
			}
			// 2013-03-20 添加调配出组织，休假转移的数据不需要处理下期
			if (nextPeriod == null)
				continue;
			// 走到这里，说明后期没有结算，如果要转下期的话，需要查询后期的结算记录，并设置其上期结余和结余
			// MOD 张恒 specialrest等于1代表当前人员为递延次年 2018/8/23
			if (leaveSet == LeaveTypeCopyVO.LEAVESETTLEMENT_NEXT || "1".equals(specialrest)) {
				// para.clearParams();
				// para.addParam(pk_org);
				// para.addParam(vo.getPk_psnorg());
				// para.addParam(pk_timeitem);
				// para.addParam(isMonth?(nextPeriod.getTimeyear()+nextPeriod.getTimemonth()):nextPeriod.getTimeyear());

				// 2015-09-10修改，若下期没有考勤档案则结余不转下期，解决离职再入职时会把离职前的结余数据带进来（离职时可能已经结算工资了）
				// 是否要insert，需要看下期是否有考勤档案记录，如果没有，则不需要insert
				boolean needInsert = false;
				if (leaveSetPeriod != LeaveTypeCopyVO.LEAVESETPERIOD_DATE
				// ssx added on 2018-03-16
				// for changes of start date of company age
						&& leaveSetPeriod != LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE //
				) {// 按年和期间结算的，下期的日期范围是固定的
					needInsert = psndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(), nextPeriod.getBegindate(),
							nextPeriod.getEnddate());
				} else {// 按入职日结算的，下期的日期范围因人而异
					if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE) {
						needInsert = psndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(), vo.getHireenddate()
								.getDateAfter(1), getHireEndDate(nextPeriod.getTimeyear(), vo.getHirebegindate()));
						// MOD 张恒 按照年资起算日来计算
					} else if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
						// typeVO.getLeaveextendcount() 为有限延长期限
						needInsert = psndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(), endDate.getDateAfter(1),
								getHireEndDate(nextPeriod.getTimeyear(), beginDate));
					}

				}

				// LeaveBalanceVO[] nextPeriodVOs = (LeaveBalanceVO[])
				// CommonUtils.toArray(LeaveBalanceVO.class,
				// dao.retrieveByClause(LeaveBalanceVO.class, nextPeriodCond,
				UFDouble toLeave = vo.getRestdayorhour();// 转下期的数据，根据需求，转下期是有上限的，因此在此处理。
				if (typeVO.getLeavemax() != null && toLeave != null && typeVO.getLeavemax().doubleValue() > 0
						&& toLeave.doubleValue() > typeVO.getLeavemax().doubleValue()) {
					toLeave = typeVO.getLeavemax();
				}
				// 如果数据库有，则需要update，否则需要insert
				// if(!ArrayUtils.isEmpty(nextPeriodVOs)){
				if (!CollectionUtils.isEmpty(balanceMap.get(vo.getPk_psnorg())) && needInsert) {// 2015-09-10修改，若下期没有考勤档案则结余不转下期，解决离职再入职时会把离职前的结余数据带进来（离职时可能已经结算工资了）
					LeaveBalanceVO updateVO = balanceMap.get(vo.getPk_psnorg()).get(0);
					updateList.add(updateVO);
					updateVO.setLastdayorhour(toLeave);// 将本期结余更新到下期的“上期结余”
					if (toLeave != null && toLeave.doubleValue() != 0) {// 如果本期结余不为0，才有必要重算下期的结余
						updateVO.setRestdayorhour(updateVO.getRestdayorhour() == null ? toLeave : toLeave.add(updateVO
								.getRestdayorhour()));
					}
				} else {
					// //是否要insert，需要看下期是否有考勤档案记录，如果没有，则不需要insert
					// boolean needInsert = false;
					// if(leaveSetPeriod!=LeaveTypeCopyVO.LEAVESETPERIOD_DATE){//按年和期间结算的，下期的日期范围是固定的
					// needInsert = psndocService.existsTBMPsndoc(pk_org,
					// vo.getPk_psnorg(), nextPeriod.getBegindate(),
					// nextPeriod.getEnddate());
					// }
					// else{//按入职日结算的，下期的日期范围因人而异
					// needInsert = psndocService.existsTBMPsndoc(pk_org,
					// vo.getPk_psnorg(), vo.getHireenddate().getDateAfter(1),
					// getHireEndDate(nextPeriod.getTimeyear(),
					// vo.getHirebegindate()));
					// }
					if (needInsert) {
						LeaveBalanceVO nextPeriodVO = new LeaveBalanceVO();
						insertList.add(nextPeriodVO);
						nextPeriodVO.setPk_group(pk_group);
						nextPeriodVO.setPk_org(pk_org);
						nextPeriodVO.setPk_psnorg(vo.getPk_psnorg());
						nextPeriodVO.setPk_psndoc(vo.getPk_psndoc());
						nextPeriodVO.setLeaveindex(1);
						// nextPeriodVO.setLastdayorhour(vo.getRestdayorhour());
						// nextPeriodVO.setRestdayorhour(vo.getRestdayorhour());
						nextPeriodVO.setLastdayorhour(toLeave);
						nextPeriodVO.setRestdayorhour(toLeave);
						nextPeriodVO.setCurdayorhour(UFDouble.ZERO_DBL);
						nextPeriodVO.setYidayorhour(UFDouble.ZERO_DBL);
						nextPeriodVO.setRealdayorhour(UFDouble.ZERO_DBL);
						nextPeriodVO.setFreezedayorhour(UFDouble.ZERO_DBL);
						nextPeriodVO.setCuryear(nextPeriod.getTimeyear());
						nextPeriodVO.setCurmonth(nextPeriod.getTimemonth());
						nextPeriodVO.setPk_timeitem(pk_timeitem);
						nextPeriodVO.setIsabnormalset(UFBoolean.FALSE);
						nextPeriodVO.setIssettlement(UFBoolean.FALSE);
						nextPeriodVO.setIsuse(UFBoolean.FALSE);
					}
				}
			}
			vo.setSettlementdate(settlementDate);
			vo.setSettlementmethod(typeVO.getLeavesettlement());
			vo.setIssettlement(UFBoolean.TRUE);
			vo.setSalaryyear(salaryYear);
			vo.setSalarymonth(salaryMonth);
			updateList.add(vo);
		}
		if (updateList.size() > 0) {
			dao.updateVOList(updateList);
		}
		if (insertList.size() > 0) {
			dao.insertVOList(insertList);
		}
		if (nextPeriodSealedLine.size() > 0) {
			String errLineNo = getMultiLineNo(nextPeriodSealedLine);
			errMsg.append(MessageFormat.format(getNextPeriodSealedError(), errLineNo));
		}
		if (samePreRecordNotSealedLine.size() > 0) {
			String errLineNo = getMultiLineNo(samePreRecordNotSealedLine);
			errMsg.append(MessageFormat.format(getSamePreRecordNotSealedError(), errLineNo));
		}
		if (previousPeriodNotSealedLine.size() > 0) {
			String errLineNo = getMultiLineNo(previousPeriodNotSealedLine);
			errMsg.append(MessageFormat.format(getPreviousPeriodNotSealedError(), errLineNo));
		}
		if (errMsg.length() > 0)
			result.setErrMsg(errMsg.toString());
		// 业务日志
		TaBusilogUtil.writeLeaveBalanceSettlementBusiLog(vos);
	}

	// END 张恒{21481} 新增特休假人员（递延次年） 2018/8/21

	/**
	 * 查询下期。如果是按年结算，或者按入职日结算，则返回下一年。如果是按期间结算，则返回下一期间的年、月
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	private String[] queryNextPeriod(String pk_org, LeaveTypeCopyVO typeVO, String year, String month)
			throws BusinessException {
		int leaveSetPeriod = typeVO.getLeavesetperiod().intValue();
		if (leaveSetPeriod == TimeItemCopyVO.LEAVESETPERIOD_MONTH) {
			PeriodVO nextPeriod = PeriodServiceFacade.queryNextPeriod(pk_org, year, month);
			if (nextPeriod != null)
				return new String[] { nextPeriod.getTimeyear(), nextPeriod.getTimemonth() };
			return null;
		}
		return new String[] { Integer.toString(Integer.parseInt(year) + 1), null };
	}

	/**
	 * 查询上期。如果是按年结算，或者按入职日结算，则返回上一年。如果是按期间结算，则返回上一期间的年、月
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	private String[] queryPreviousPeriod(String pk_org, LeaveTypeCopyVO typeVO, String year, String month)
			throws BusinessException {
		int leaveSetPeriod = typeVO.getLeavesetperiod().intValue();
		if (leaveSetPeriod == TimeItemCopyVO.LEAVESETPERIOD_MONTH) {
			PeriodVO previousPeriod = PeriodServiceFacade.queryPreviousPeriod(pk_org, year, month);
			if (previousPeriod != null)
				return new String[] { previousPeriod.getTimeyear(), previousPeriod.getTimemonth() };
			return null;
		}
		return new String[] { Integer.toString(Integer.parseInt(year) - 1), null };
	}

	/*
	 * 调用到这个方法的时候，说明界面上一定有未到结算有效期，或者未到结算日但已无考勤档案的人员，已经在界面上提示过 (non-Javadoc)
	 * 
	 * @see
	 * nc.itf.ta.ILeaveBalanceManageMaintain#secondSettlement(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * nc.vo.ta.leavebalance.LeaveBalanceVO[], nc.vo.pub.lang.UFLiteralDate,
	 * boolean, boolean)
	 */
	@Override
	public SettlementResult secondSettlement(String pk_org, String pk_leavetype, String year, String month,
			LeaveBalanceVO[] vos, boolean isSettleNotToPeriodEndDateNoTbmPsndoc,
			boolean isSettleNotToEffectiveDateNoTbmPsndocVOs, boolean isSettleNotToEffectiveDateExistTbmPsndocVOs,
			boolean needCheckNextPeriod, String pk_salaryPeriod) throws BusinessException {
		UFLiteralDate settlementDate = PubEnv.getServerLiteralDate();
		return secondSettlement(pk_org, pk_leavetype, year, month, vos, settlementDate,
				isSettleNotToPeriodEndDateNoTbmPsndoc, isSettleNotToEffectiveDateNoTbmPsndocVOs,
				isSettleNotToEffectiveDateExistTbmPsndocVOs, needCheckNextPeriod, pk_salaryPeriod);
	}

	private SettlementResult secondSettlement(String pk_org, String pk_leavetype, String year, String month,
			LeaveBalanceVO[] vos, UFLiteralDate settlementDate, boolean isSettleNotToPeriodEndDateNoTbmPsndoc,
			boolean isSettleNotToEffectiveDateNoTbmPsndocVOs, boolean isSettleNotToEffectiveDateExistTbmPsndocVOs,
			boolean needCheckNextPeriod, String pk_salaryPeriod) throws BusinessException {
		ITimeItemQueryService service = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) service.queryCopyTypesByDefPK(pk_org, pk_leavetype,
				TimeItemCopyVO.LEAVE_TYPE);
		LeaveBalanceDAO balanceDAO = new LeaveBalanceDAO();
		balanceDAO.syncFromDB(typeVO, year, month, vos);
		int leavesetPeriod = typeVO.getLeavesetperiod().intValue();

		PeriodVO salaryPeriodVO = null;
		String salaryYear = null;
		String salaryMonth = null;
		IPeriodQueryService periodQuery = NCLocator.getInstance().lookup(IPeriodQueryService.class);
		if (TimeItemCopyVO.LEAVESETTLEMENT_MONEY == typeVO.getLeavesettlement()) {// 若是转薪资的，需要确认把薪资转移到哪个考勤期间，若期间为空默认当前期间
			if (StringUtils.isBlank(pk_salaryPeriod)) {
				salaryPeriodVO = periodQuery.queryCurPeriod(pk_org);
			} else {
				salaryPeriodVO = (PeriodVO) new BaseDAO().retrieveByPK(PeriodVO.class, pk_salaryPeriod);
			}
			salaryYear = salaryPeriodVO.getYear();
			salaryMonth = salaryPeriodVO.getMonth();
		} else {
			salaryYear = year;
			salaryMonth = month;
		}

		if (leavesetPeriod == TimeItemCopyVO.LEAVESETPERIOD_DATE
		// ssx added on 2018-03-16
		// for changes of start date of company age
				|| leavesetPeriod == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE)
			//
			return secondSettlement4HireDate(pk_org, typeVO, year, vos, settlementDate,
					isSettleNotToPeriodEndDateNoTbmPsndoc, isSettleNotToEffectiveDateNoTbmPsndocVOs,
					isSettleNotToEffectiveDateExistTbmPsndocVOs, needCheckNextPeriod, salaryYear, salaryMonth);
		return secondSettlement4YearMonth(pk_org, typeVO, year, month, vos, settlementDate,
				isSettleNotToPeriodEndDateNoTbmPsndoc, isSettleNotToEffectiveDateNoTbmPsndocVOs,
				isSettleNotToEffectiveDateExistTbmPsndocVOs, needCheckNextPeriod, salaryYear, salaryMonth);
	}

	protected SettlementResult secondSettlement4YearMonth(String pk_org, LeaveTypeCopyVO typeVO, String year,
			String month, LeaveBalanceVO[] vos, UFLiteralDate settlementDate,
			boolean isSettleNotToPeriodEndDateNoTbmPsndoc, boolean isSettleNotToEffectiveDateNoTbmPsndocVOs,
			boolean isSettleNotToEffectiveDateExistTbmPsndocVOs, boolean needCheckNextPeriod, String salaryYear,
			String salaryMonth) throws BusinessException {
		UFLiteralDate[] periodBeginEndDate = queryPeriodBeginEndDate(pk_org, typeVO, year, month);
		UFLiteralDate periodBeginDate = periodBeginEndDate[0];
		UFLiteralDate periodEndDate = periodBeginEndDate[1];
		SettlementGroupVO sgv = groupSettlementVOs4YearMonth(pk_org, typeVO, year, periodBeginDate, periodEndDate, vos,
				settlementDate);
		LeaveBalanceVO[] canSettleVOs = sgv.toCanSettleVOs(isSettleNotToPeriodEndDateNoTbmPsndoc,
				isSettleNotToEffectiveDateNoTbmPsndocVOs, isSettleNotToEffectiveDateExistTbmPsndocVOs);
		SettlementResult result = new SettlementResult();
		result.setSettledVOs(vos);
		if (ArrayUtils.isEmpty(canSettleVOs))
			return result;
		PeriodVO nextPeriod = null;
		// 2013-03-20 添加调配出组织，休假转移的数据不需要处理下期
		if (needCheckNextPeriod) {
			String[] nextYearMonth = queryNextPeriod(pk_org, typeVO, year, month);
			if (ArrayUtils.isEmpty(nextYearMonth))
				throw new BusinessException(ResHelper.getString("6017leave", "06017leave0232")
				/* @res "系统未定义下期!" */);
			UFLiteralDate[] nextPeriodBeginEndDate = queryPeriodBeginEndDate(pk_org, typeVO, nextYearMonth[0],
					nextYearMonth.length == 2 ? nextYearMonth[1] : null);
			nextPeriod = new PeriodVO();
			nextPeriod.setTimeyear(nextYearMonth[0]);
			nextPeriod.setTimemonth(nextYearMonth.length == 2 ? nextYearMonth[1] : null);
			nextPeriod.setBegindate(nextPeriodBeginEndDate[0]);
			nextPeriod.setEnddate(nextPeriodBeginEndDate[1]);
		}

		String[] previousYearMonth = queryPreviousPeriod(pk_org, typeVO, year, month);
		PeriodVO previousPeriod = null;
		if (!ArrayUtils.isEmpty(previousYearMonth)) {
			UFLiteralDate[] previousPeriodBeginEndDate = queryPeriodBeginEndDate(pk_org, typeVO, previousYearMonth[0],
					previousYearMonth.length == 2 ? previousYearMonth[1] : null, false);
			if (!ArrayUtils.isEmpty(previousPeriodBeginEndDate)) {
				previousPeriod = new PeriodVO();
				previousPeriod.setTimeyear(previousYearMonth[0]);
				previousPeriod.setTimemonth(previousYearMonth.length == 2 ? previousYearMonth[1] : null);
				previousPeriod.setBegindate(previousPeriodBeginEndDate[0]);
				previousPeriod.setEnddate(previousPeriodBeginEndDate[1]);
			}
		}
		settlement(pk_org, typeVO, year, month, previousPeriod, nextPeriod, canSettleVOs,
				createLineNoMap(canSettleVOs), settlementDate, result, salaryYear, salaryMonth);
		return result;
	}

	protected SettlementResult secondSettlement4HireDate(String pk_org, LeaveTypeCopyVO typeVO, String year,
			LeaveBalanceVO[] vos, UFLiteralDate settlementDate, boolean isSettleNotToPeriodEndDateNoTbmPsndoc,
			boolean isSettleNotToEffectiveDateNoTbmPsndocVOs, boolean isSettleNotToEffectiveDateExistTbmPsndocVOs,
			boolean needCheckNextPeriod, String salaryYear, String salaryMonth) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return new SettlementResult();
		SettlementGroupVO sgv = groupSettlementVOs4HireDate(pk_org, typeVO, year, vos, settlementDate);
		LeaveBalanceVO[] canSettleVOs = sgv.toCanSettleVOs(isSettleNotToPeriodEndDateNoTbmPsndoc,
				isSettleNotToEffectiveDateNoTbmPsndocVOs, isSettleNotToEffectiveDateExistTbmPsndocVOs);
		SettlementResult result = new SettlementResult();
		result.setSettledVOs(vos);

		// //MOD 张恒｛21481｝获取特休假人员 2018/8/21
		// LeaveBalanceVO[] specialPsnVOs = sgv.specialPsnVOs;

		if (ArrayUtils.isEmpty(canSettleVOs))
			return result;

		String nextYear = Integer.toString(Integer.parseInt(year) + 1);
		// 2013-03-20 添加调配出组织，休假转移的数据不需要处理下期
		PeriodVO nextPeriod = null;
		if (needCheckNextPeriod) {
			nextPeriod = new PeriodVO();
			nextPeriod.setTimeyear(nextYear);
		}

		String previousYear = Integer.toString(Integer.parseInt(year) - 1);
		PeriodVO previousPeriod = new PeriodVO();
		previousPeriod.setTimeyear(previousYear);
		settlement(pk_org, typeVO, year, null, previousPeriod, nextPeriod, canSettleVOs, createLineNoMap(canSettleVOs),
				settlementDate, result, salaryYear, salaryMonth);
		return result;

	}

	/**
	 * 对于按入职日结算的，界面传来的结算vo数组，将其中可以结算的分组
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param vos
	 * @param settlementDate
	 * @return
	 * @throws BusinessException
	 */
	private SettlementGroupVO groupSettlementVOs4HireDate(String pk_org, LeaveTypeCopyVO typeVO, String year,
			LeaveBalanceVO[] vos,// 要求传入的vo已经和数据库中的同步过
			UFLiteralDate settlementDate) throws BusinessException {
		// 如果结算日期早于year 01-01，则抛异常
		if (settlementDate.toString().substring(0, 4).compareTo(year) < 0)
			throw new BusinessException(MessageFormat.format(getSealDateTooEarlyError(), year + "-01-01"));
		List<LeaveBalanceVO> notToPeriodEndDateNoTbmPsndocList = new ArrayList<LeaveBalanceVO>();// 未结算，且未到期末日，且结算日到期末日无考勤档案的人员，需要提示是否结算
		List<LeaveBalanceVO> notToEffectiveDateNoTbmPsndocList = new ArrayList<LeaveBalanceVO>();// 未结算，且过了期末日未到有效期，且结算日到有效期内无考勤档案的人员，需要提示是否结算
		List<LeaveBalanceVO> notToEffectiveDateExistTbmPsndocList = new ArrayList<LeaveBalanceVO>();// 未结算，且过了期末日未到有效期，且结算日到有效期内有考勤档案的人员，需要提示是否结算
		List<LeaveBalanceVO> exceedEffectiveDateList = new ArrayList<LeaveBalanceVO>();// 未结算，且已过了有效期的人员，无需提示，肯定能结算

		// //BEGIN 张恒｛21481｝ 满足特休假的员工，可以结算 2018/8/21
		// List<LeaveBalanceVO> specialPsnList = new
		// ArrayList<LeaveBalanceVO>();
		// //END 张恒｛21481｝ 满足特休假的员工，可以结算 2018/8/21

		ITBMPsndocQueryService tbmpsndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		// MOD by ssx on 2020-03-25
		// 判斷有效期遞延時，應先判斷是否允許有效期遞延，以免不允許時天數有值，造成錯誤
		int extendCount = typeVO.getIsleave() == null || !typeVO.getIsleave().booleanValue() ? 0 : typeVO
				.getExtendDaysCount();// 有效期能延长多少天
		// end ssx
		SettlementGroupVO retVO = new SettlementGroupVO();
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		IDataPermissionPubService perimssionService = NCLocator.getInstance().lookup(IDataPermissionPubService.class);
		Map<String, UFBoolean> perimssionMap = null;
		if (!PubEnv.UAP_USER.equalsIgnoreCase(pk_user))// 2013-03-29后台任务查询权限报错
			perimssionMap = perimssionService.isUserhasPermissionByMetaDataOperation("60170psndoc",
					StringPiecer.getStrArrayDistinct(vos, LeaveBalanceVO.PK_TBM_PSNDOC), "LeaveBalanceAction",
					pk_group, pk_user);
		for (LeaveBalanceVO vo : vos) {
			if (vo.isSettlement()) {// 已经结算了的，刨除掉
				continue;
			}

			// // 没有权限的刨除掉 #2012-10-10修改，使用map减少sql量
			if (!PubEnv.UAP_USER.equalsIgnoreCase(pk_user)) {
				if (perimssionMap != null && perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()) != null
						&& !perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()).booleanValue())
					continue;
			}
			// if(!perimssionService.isUserhasPermissionByMetaDataOperation("60170psndoc",
			// vo.toTBMPsndocVO().getPrimaryKey(), "LeaveBalanceAction",
			// pk_group, pk_user))
			// continue;//没有结算权限的，刨除掉，按照入职日
			if (typeVO.getLeavesetperiod() == LeaveTypeCopyVO.LEAVESETPERIOD_DATE) {

				if (!settlementDate.before(vo.getHirebegindate()) && !settlementDate.after(vo.getHireenddate())) {
					boolean existPsndoc = tbmpsndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(),
							settlementDate.getDateAfter(1), vo.getHireenddate());
					if (!existPsndoc) {
						notToPeriodEndDateNoTbmPsndocList.add(vo);
						continue;
					}
					continue;
				}

				// BEGIN 张恒{21481} 特休假薪資計算 2018/8/21
				// 按照年资起算日
			} else if (typeVO.getLeavesetperiod() == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
				// 获取年资起算日，年资结算日
				UFLiteralDate[] specialBeginEnd = getSpecialBeginEnd(vo.getPk_psndoc(), year, vo.getPk_psnorg());
				if (!ArrayUtils.isEmpty(specialBeginEnd)) {
					UFLiteralDate beginDate = specialBeginEnd[0];
					UFLiteralDate endDate = specialBeginEnd[1];
					if (settlementDate.before(beginDate)) {// 结算日期在入职日之前，刨除掉
						continue;
					}

					UFLiteralDate effectiveDate = extendCount == 0 ? endDate : endDate.getDateAfter(extendCount);
					if (settlementDate.after(endDate) && !settlementDate.after(effectiveDate)) {
						boolean existPsndoc = tbmpsndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(),
								settlementDate.getDateAfter(1), effectiveDate);
						if (existPsndoc) {
							notToEffectiveDateExistTbmPsndocList.add(vo);
							continue;
						}
						notToEffectiveDateNoTbmPsndocList.add(vo);
						continue;
					}
					// MOD by ssx for #33982
					// 未到到期日或未到有效日，且結算日到有效日之間有考勤檔案的，需要提示是否結算
					else if (settlementDate.before(endDate.getDateAfter(1))
							|| settlementDate.before(effectiveDate.getDateAfter(1))) {
						boolean existPsndoc = tbmpsndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(),
								settlementDate.getDateAfter(1), effectiveDate);
						if (existPsndoc) {
							notToEffectiveDateExistTbmPsndocList.add(vo);
							continue;
						}
					}
					// end #33982

					// 離職留停人員可以結算本年假結算 wangywt 20190424 begin
					if (settlementDate.after(effectiveDate) || getPsnStateFlag(vo))
						exceedEffectiveDateList.add(vo);
					// wangywt 20190424 end
				}
				// END 张恒{21481} 特休假薪資計算 2018/8/21
			} else {
				if (settlementDate.before(vo.getHirebegindate())) {// 结算日期在入职日之前，刨除掉
					continue;
				}

				UFLiteralDate effectiveDate = extendCount == 0 ? vo.getHireenddate() : vo.getHireenddate()
						.getDateAfter(extendCount);
				if (settlementDate.after(vo.getHireenddate()) && !settlementDate.after(effectiveDate)) {
					boolean existPsndoc = tbmpsndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(),
							settlementDate.getDateAfter(1), effectiveDate);
					if (existPsndoc) {
						notToEffectiveDateExistTbmPsndocList.add(vo);
						continue;
					}
					notToEffectiveDateNoTbmPsndocList.add(vo);
					continue;
				}
				if (settlementDate.after(effectiveDate))
					exceedEffectiveDateList.add(vo);
			}
		}
		if (notToPeriodEndDateNoTbmPsndocList.size() > 0)
			retVO.notToPeriodEndDateNoTbmPsndocVOs = notToPeriodEndDateNoTbmPsndocList.toArray(new LeaveBalanceVO[0]);
		if (notToEffectiveDateNoTbmPsndocList.size() > 0)
			retVO.notToEffectiveDateNoTbmPsndocVOs = notToEffectiveDateNoTbmPsndocList.toArray(new LeaveBalanceVO[0]);
		if (notToEffectiveDateExistTbmPsndocList.size() > 0)
			retVO.notToEffectiveDateExistTbmPsndocVOs = notToEffectiveDateExistTbmPsndocList
					.toArray(new LeaveBalanceVO[0]);
		if (exceedEffectiveDateList.size() > 0)
			retVO.exceedEffectiveDateVOs = exceedEffectiveDateList.toArray(new LeaveBalanceVO[0]);

		// if (specialPsnList.size() > 0)
		// retVO.specialPsnVOs = specialPsnList
		// .toArray(new LeaveBalanceVO[0]);

		return retVO;
	}

	/**
	 * 判斷離職留停人員是否可進行結算
	 * 
	 * @author 王永文
	 * @since 2019-04-24
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private boolean getPsnStateFlag(LeaveBalanceVO vo) throws BusinessException {
		BaseDAO baseDao = new BaseDAO();
		// MOD by ssx #33564
		// 離職人員可以結算
		Collection<PsnOrgVO> psnorgvos = baseDao.retrieveByClause(PsnOrgVO.class, "pk_psnorg = '" + vo.getPk_psnorg()
				+ "' ");
		if (psnorgvos.toArray(new PsnOrgVO[0])[0].getEndflag().booleanValue()) {
			return true;
		}

		// 留停人員可以結算
		String jobsql = "select TRNSTYPE from hi_psnjob where pk_psndoc = '" + vo.getPk_psndoc() + "' and pk_psnorg='"
				+ psnorgvos.toArray(new PsnOrgVO[0])[0].getPk_psnorg() + "'";
		// end #33564
		List<Object[]> jobList = (List<Object[]>) baseDao.executeQuery(jobsql, new ArrayListProcessor());
		// 留停異動類型
		String refTransType = SysInitQuery.getParaString(vo.getPk_org(), "TWHR11").toString();
		// 複職異動類型
		String refReturnType = SysInitQuery.getParaString(vo.getPk_org(), "TWHR12").toString();
		// 留停記錄
		int refTrans = 0;
		// 復職記錄
		int refReturn = 0;
		if (!CollectionUtils.isEmpty(jobList)) {
			for (Object[] objs : jobList) {
				if (refTransType.equals(objs[0])) {
					refTrans++;
				} else if (refReturnType.equals(objs[0])) {
					refReturn++;
				}
			}
		}
		if (refTrans != refReturn) {
			return true;
		}
		return false;
	}

	/**
	 * 对于按年、期间结算的，界面传来的结算的vo数组，将可以结算的分组
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param periodBeginDate
	 * @param periodEndDate
	 * @param vos
	 * @param settlementDate
	 * @return
	 * @throws BusinessException
	 */
	private SettlementGroupVO groupSettlementVOs4YearMonth(String pk_org, LeaveTypeCopyVO typeVO, String year,
			UFLiteralDate periodBeginDate, UFLiteralDate periodEndDate, LeaveBalanceVO[] vos,// 要求传入的vo已经和数据库中的同步过
			UFLiteralDate settlementDate) throws BusinessException {
		if (settlementDate.before(periodBeginDate))
			throw new BusinessException(MessageFormat.format(getSealDateTooEarlyError(), periodBeginDate));
		int extendCount = typeVO.getExtendDaysCount();// 有效期能延长多少天
		UFLiteralDate effectiveDate = extendCount == 0 ? periodEndDate : periodEndDate.getDateAfter(extendCount);
		SettlementGroupVO retVO = new SettlementGroupVO();
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		IDataPermissionPubService perimssionService = NCLocator.getInstance().lookup(IDataPermissionPubService.class);
		Map<String, UFBoolean> perimssionMap = perimssionService.isUserhasPermissionByMetaDataOperation("60170psndoc",
				StringPiecer.getStrArrayDistinct(vos, LeaveBalanceVO.PK_TBM_PSNDOC), "LeaveBalanceAction", pk_group,
				pk_user);
		if (settlementDate.after(effectiveDate)) {// 如果结算日已过有效期，则只可能有两种：结算了的，和剩下的
			List<LeaveBalanceVO> leftList = new ArrayList<LeaveBalanceVO>();
			for (LeaveBalanceVO vo : vos) {
				if (vo.isSettlement())
					continue;
				if (perimssionMap != null && perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()) != null
						&& !perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()).booleanValue())
					continue;// 没有结算权限的，刨除掉
				leftList.add(vo);
			}
			if (leftList.size() > 0)
				retVO.exceedEffectiveDateVOs = leftList.toArray(new LeaveBalanceVO[0]);
			return retVO;
		}
		ITBMPsndocQueryService tbmpsndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		// 如果结算日未过期末日，则只能有三种：结算了的，和未过期末日无/有考勤档案的
		if (!settlementDate.after(periodEndDate)) {
			List<LeaveBalanceVO> noPsndocList = new ArrayList<LeaveBalanceVO>();

			// 优化时添加
			TBMPsndocVO[] tbmpsndocs = tbmpsndocService.queryTBMPsndocByPsnorgs(pk_org,
					StringPiecer.getStrArray(vos, LeaveBalanceVO.PK_PSNORG), settlementDate.getDateAfter(1),
					periodEndDate);
			Map<Object, TBMPsndocVO[]> tbmpsnMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_PSNORG, tbmpsndocs);
			boolean mapIsNull = MapUtils.isEmpty(tbmpsnMap);

			for (LeaveBalanceVO vo : vos) {
				if (vo.isSettlement()) {
					continue;
				}
				if (perimssionMap != null && perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()) != null
						&& !perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()).booleanValue())
					continue;// 没有结算权限的，刨除掉
				// 不再循环中查询了，提取出来
				// boolean existPsndoc =
				// tbmpsndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(),
				// settlementDate.getDateAfter(1), periodEndDate);
				// if(!existPsndoc){
				// noPsndocList.add(vo);
				// continue;
				// }
				if (mapIsNull || ArrayUtils.isEmpty(tbmpsnMap.get(vo.getPk_psnorg()))) {
					noPsndocList.add(vo);
					continue;
				}
			}
			if (noPsndocList.size() > 0)
				retVO.notToPeriodEndDateNoTbmPsndocVOs = noPsndocList.toArray(new LeaveBalanceVO[0]);
			return retVO;
		}
		// 如果结算日过了期末日，但未到有效期日，则只能有三种：结算了的，和未到有效期无/有考勤档案的
		if (settlementDate.after(periodEndDate) && !settlementDate.after(effectiveDate)) {
			List<LeaveBalanceVO> noPsndocList = new ArrayList<LeaveBalanceVO>();
			List<LeaveBalanceVO> existPsndocList = new ArrayList<LeaveBalanceVO>();

			// 优化时添加
			TBMPsndocVO[] tbmpsndocs = tbmpsndocService.queryTBMPsndocByPsnorgs(pk_org,
					StringPiecer.getStrArray(vos, LeaveBalanceVO.PK_PSNORG), settlementDate.getDateAfter(1),
					effectiveDate);
			Map<Object, TBMPsndocVO[]> tbmpsnMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_PSNORG, tbmpsndocs);
			boolean mapIsNull = MapUtils.isEmpty(tbmpsnMap);

			for (LeaveBalanceVO vo : vos) {
				if (vo.isSettlement()) {
					continue;
				}
				// 优化时删除，避免在循环中查询
				// boolean existPsndoc =
				// tbmpsndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(),
				// settlementDate.getDateAfter(1), effectiveDate);
				// if(existPsndoc){
				// existPsndocList.add(vo);
				// continue;
				// }

				if (!mapIsNull && !ArrayUtils.isEmpty(tbmpsnMap.get(vo.getPk_psnorg()))) {
					existPsndocList.add(vo);
					continue;
				}

				noPsndocList.add(vo);
			}
			if (existPsndocList.size() > 0)
				retVO.notToEffectiveDateExistTbmPsndocVOs = existPsndocList.toArray(new LeaveBalanceVO[0]);
			if (noPsndocList.size() > 0)
				retVO.notToEffectiveDateNoTbmPsndocVOs = noPsndocList.toArray(new LeaveBalanceVO[0]);
			return retVO;
		}
		return retVO;
	}

	/**
	 * 将用户提交的结算vo数组分组，将其中可以结算的分为4个部分，详见下面的注释。肯定不能结算的，例如已结算的，不包含在分组结果中
	 * 
	 * MOD 张恒 新增 满足特休假的员工 2018/8/23
	 * 
	 * @author zengcheng
	 * 
	 */
	private static class SettlementGroupVO {
		// LeaveBalanceVO[] settlementedVOs;//已结算的。无需提示客户端，肯定不能结算
		// LeaveBalanceVO[] notToPeriodBeginDateVOs;//未结算，且未到期首日，无需提示，肯定不能结算
		LeaveBalanceVO[] notToPeriodEndDateNoTbmPsndocVOs;// 未结算，且未到期末日，且结算日到期末日无考勤档案的人员，需要提示是否结算
		// LeaveBalanceVO[]
		// notToPeriodEndDateExistTbmPsndocVOs;//未结算，且未到期末日，且结算日到期末日有考勤档案的人员，无需提示，肯定不能结算
		LeaveBalanceVO[] notToEffectiveDateNoTbmPsndocVOs;// 未结算，且过了期末日未到有效期，且结算日到有效期内无考勤档案的人员，需要提示是否结算
		LeaveBalanceVO[] notToEffectiveDateExistTbmPsndocVOs;// 未结算，且过了期末日未到有效期，且结算日到有效期内有考勤档案的人员，需要提示是否结算
		LeaveBalanceVO[] exceedEffectiveDateVOs;// 未结算，且已过了有效期的人员，无需提示，肯定能结算

		// //BEGIN 张恒｛21481｝ 满足特休假的员工，可以结算 2018/8/21
		// LeaveBalanceVO[] specialPsnVOs;
		// //END 张恒｛21481｝ 满足特休假的员工，可以结算 2018/8/21

		LeaveBalanceVO[] toCanSettleVOs() {// 可以结算的记录，默认返回过了有效期的人员
			return exceedEffectiveDateVOs;
		}

		LeaveBalanceVO[] toCanSettleVOs(boolean isSettleNotToPeriodEndDateNoTbmPsndoc,
				boolean isSettleNotToEffectiveDateNoTbmPsndocVOs, boolean isSettleNotToEffectiveDateExistTbmPsndocVOs) {// 根据界面用户的选择，返回可以结算的人员
			LeaveBalanceVO[] retVOs = exceedEffectiveDateVOs;
			if (isSettleNotToPeriodEndDateNoTbmPsndoc && !ArrayUtils.isEmpty(notToPeriodEndDateNoTbmPsndocVOs))
				retVOs = (LeaveBalanceVO[]) org.apache.commons.lang.ArrayUtils.addAll(retVOs,
						notToPeriodEndDateNoTbmPsndocVOs);
			if (isSettleNotToEffectiveDateNoTbmPsndocVOs && !ArrayUtils.isEmpty(notToEffectiveDateNoTbmPsndocVOs))
				retVOs = (LeaveBalanceVO[]) org.apache.commons.lang.ArrayUtils.addAll(retVOs,
						notToEffectiveDateNoTbmPsndocVOs);
			if (isSettleNotToEffectiveDateExistTbmPsndocVOs && !ArrayUtils.isEmpty(notToEffectiveDateExistTbmPsndocVOs))
				retVOs = (LeaveBalanceVO[]) org.apache.commons.lang.ArrayUtils.addAll(retVOs,
						notToEffectiveDateExistTbmPsndocVOs);
			// if (!ArrayUtils.isEmpty(notToEffectiveDateExistTbmPsndocVOs))
			// retVOs = (LeaveBalanceVO[]) org.apache.commons.lang.ArrayUtils
			// .addAll(retVOs, specialPsnVOs);
			return retVOs;
		}
	}

	private LeaveBalanceVO[] setDefaultTimeitemCopy(LeaveBalanceVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		ITimeItemQueryService queryService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		// 休假类别Map, Key: 组织主键 value-key: 休假类别定义主键 value: 休假类别
		Map<String, Map<String, LeaveTypeCopyVO>> allTimeitemMap = new HashMap<String, Map<String, LeaveTypeCopyVO>>();
		for (int i = 0; i < vos.length; i++) {
			String pk_org = vos[i].getPk_org();
			if (allTimeitemMap.get(pk_org) == null)
				allTimeitemMap.put(pk_org, queryService.queryLeaveCopyTypeMapByOrg(pk_org));
			String pk_timeitemcopy = allTimeitemMap.get(pk_org) == null ? null : allTimeitemMap.get(pk_org)
					.get(vos[i].getPk_timeitem()).getPk_timeitemcopy();
			vos[i].setPk_timeitemcopy(pk_timeitemcopy);
		}
		return vos;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LeaveBalanceVO[] queryByPsn(String pk_psndoc, String pk_leavetype, String year, boolean containsHis,
			UFBoolean issettlement) throws BusinessException {
		TBMPsndocVO latestVO = NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class)
				.queryByPsndocAndDateTime(pk_psndoc, new UFDateTime());
		List<LeaveBalanceVO> listVO = null;
		if (latestVO == null)
			return null;
		ITimeItemQueryService timeItemService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		// 需要根据结算方式过滤一下数据，比如假期计算默认按年结算，假期计算后，在自助假期查询可以查询到计算的数据。将参数改为按期间结算，在期间计算后，在自助假期查询能查到期间的数据，但之前年的数据还在，应该删掉
		LeaveTypeCopyVO typeCopyVO = (LeaveTypeCopyVO) timeItemService.queryCopyTypesByDefPK(latestVO.getPk_org(),
				pk_leavetype, TimeItemVO.LEAVE_TYPE);
		String cond = LeaveBalanceVO.PK_PSNDOC + "=? and " + LeaveBalanceVO.PK_TIMEITEM + "=? ";
		if (StringUtils.isNotBlank(year)) {// 自助查询可以查询所有年度的，年度为空
			cond += "and " + LeaveBalanceVO.CURYEAR + "=? ";
		}
		// 如果不包含历史数据，则只过滤当前HR组织的数据
		if (!containsHis)
			cond += " and " + LeaveBalanceVO.PK_ORG + "=? ";
		// 若按月结算的需过滤一下
		if (TimeItemCopyVO.LEAVESETPERIOD_MONTH == typeCopyVO.getLeavesetperiod()) {
			cond += " and curmonth is not null";
		} else if (TimeItemCopyVO.LEAVESETPERIOD_YEAR == typeCopyVO.getLeavesetperiod()) {// 按年结算修改为按期间结算后产生垃圾数据，需要过滤一下
			cond += " and curmonth is null";
		}
		if (issettlement != null && issettlement.booleanValue()) {
			cond += " and issettlement = 'Y'";
		} else if (issettlement != null && !issettlement.booleanValue()) {
			cond += " and issettlement = 'N'";
		}
		SQLParameter para = new SQLParameter();
		para.addParam(pk_psndoc);
		para.addParam(pk_leavetype);
		if (StringUtils.isNotBlank(year)) {
			para.addParam(year);
		}
		if (!containsHis)
			para.addParam(latestVO.getPk_org());
		String order = LeaveBalanceVO.CURMONTH + "," + LeaveBalanceVO.PK_PSNORG + "," + LeaveBalanceVO.LEAVEINDEX;
		LeaveBalanceVO[] vos = (LeaveBalanceVO[]) CommonUtils.toArray(LeaveBalanceVO.class,
				new BaseDAO().retrieveByClause(LeaveBalanceVO.class, cond, order, para));
		if (ArrayUtils.isEmpty(vos))
			return null;
		String pk_psnorg = processPsnjobAndLeaveTypeCopy4Psn(pk_psndoc, pk_leavetype, vos);
		// 过滤掉不是最新考勤档案的休假数据
		if (pk_psnorg != null) {
			listVO = new ArrayList<LeaveBalanceVO>();
			for (LeaveBalanceVO vo : vos) {
				if (pk_psnorg.equals(vo.getPk_psnorg())) {
					listVO.add(vo);
				}
			}
			vos = new LeaveBalanceVO[listVO.size()];
			for (int i = 0; i < listVO.size(); i++) {
				vos[i] = listVO.get(i);
			}
		}
		return vos;
	}

	/**
	 * 处理员工自助的结算信息的pk_psnjob和pk_timeitemcopy字段，因为这两个字段并没有存储在数据库中
	 * 
	 * @param pk_psndoc
	 * @param pk_leavetype
	 * @param leaveBalanceVOs
	 * @throws BusinessException
	 */
	private String processPsnjobAndLeaveTypeCopy4Psn(String pk_psndoc, String pk_leavetype,
			LeaveBalanceVO[] leaveBalanceVOs) throws BusinessException {
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		ITimeItemQueryService timeItemService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		Map<String, String> typeCopyMap = new HashMap<String, String>();// key是pk_org,value是leavetypecopy
		String pk_psnorg = null;
		String Sql = "select pk_timeitem from tbm_timeitem where (timeitemname='Annual Leave' or timeitemname='年假') and (enablestate='2') ";
		GeneralVO[] pk_timeitems = (GeneralVO[]) new BaseDAO().executeQuery(Sql, new GeneralVOProcessor<GeneralVO>(
				GeneralVO.class));
		for (LeaveBalanceVO leaveBalanceVO : leaveBalanceVOs) {
			String pk_org = leaveBalanceVO.getPk_org();
			String year = leaveBalanceVO.getCuryear();
			String month = leaveBalanceVO.getCurmonth();
			UFLiteralDate beginDate = null;
			UFLiteralDate endDate = null;
			if (org.apache.commons.lang.StringUtils.isNotEmpty(month)) {
				PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
				if (periodVO == null)
					continue;
				beginDate = periodVO.getBegindate();
				endDate = periodVO.getEnddate();
			} else {
				PeriodVO[] periodVOs = PeriodServiceFacade.queryByYear(pk_org, year);
				if (ArrayUtils.isEmpty(periodVOs))
					continue;
				beginDate = periodVOs[0].getBegindate();
				endDate = periodVOs[periodVOs.length - 1].getEnddate();
			}
			TBMPsndocVO psndocVO = psndocService.queryLatestByPsndocDate(pk_org, pk_psndoc, beginDate, endDate);
			if (psndocVO == null)
				continue;
			leaveBalanceVO.setPk_psnjob(psndocVO.getPk_psnjob());
			if (typeCopyMap.containsKey(pk_org)) {
				leaveBalanceVO.setPk_timeitemcopy(typeCopyMap.get(pk_org));
				continue;
			}
			// 如果休假类别为年假，通过pk_psnorg过滤掉离职再入职人员的离职前数据
			if (pk_leavetype != null
					&& pk_leavetype.equals(pk_timeitems == null ? null : pk_timeitems[0]
							.getAttributeValue("pk_timeitem"))) {
				pk_psnorg = psndocVO.getPk_psnorg();
			}
			LeaveTypeCopyVO typeCopyVO = (LeaveTypeCopyVO) timeItemService.queryCopyTypesByDefPK(pk_org, pk_leavetype,
					TimeItemVO.LEAVE_TYPE);
			String pk_leavetypecopy = typeCopyVO == null ? null : typeCopyVO.getPk_timeitemcopy();
			typeCopyMap.put(pk_org, pk_leavetypecopy);
			leaveBalanceVO.setPk_timeitemcopy(pk_leavetypecopy);
		}
		return pk_psnorg;
	}

	private String getNextPeriodSealedError() {
		return ResHelper.getString("6017leave", "06017leave0233")
		/* @res "{0}行，后期已经结算,或者存在同期结算记录。严重错误，请联系管理员。" */;
	}

	private String getPreviousPeriodNotSealedError() {
		return ResHelper.getString("6017leave", "06017leave0251")
		/* @res "{0}行，前期结算记录还未结算。" */;
	}

	private String getSamePreRecordNotSealedError() {
		return ResHelper.getString("6017leave", "06017leave0252")
		/* @res "{0}行，存在更早的同期未结算记录。严重错误，请联系管理员" */;
	}

	private String getSealDateTooEarlyError() {
		return ResHelper.getString("6017leave", "06017leave0234")
		/* @res "结算日期不能早于{0}" */;
	}

	private String getNoNextPeriodError() {
		return ResHelper.getString("6017leave", "06017leave0232")
		/* @res "系统未定义下期!" */;
	}

	/**
	 * 处理结余时长跨组织转移,调用此方法的前提是原组织的考勤档案已经结束，新组织的考勤档案已生成
	 * 
	 * @param oldJobvo
	 * @param newJobvo
	 * @throws BusinessException
	 */
	@Override
	public void translateLeave(PsnJobVO oldJobvo, PsnJobVO newJobvo) throws BusinessException {
		if (oldJobvo == null || newJobvo == null)
			return;
		// 所属组织不一定是hr组织，需要处理一下
		OrgVO oldhr = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(oldJobvo.getPk_org());
		OrgVO newhr = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(newJobvo.getPk_org());
		String oldhrorg = oldhr.getPk_org();
		String newhrorg = newhr.getPk_org();
		// 所属人力资源组织没有发生变化不用转移结余
		if (oldhrorg.equals(newhrorg))
			return;
		// 查询需要转移的休假类别
		ITimeItemQueryMaintain leaveTypeQuery = NCLocator.getInstance().lookup(ITimeItemQueryMaintain.class);
		String condition = LeaveTypeCopyVO.ISLEAVETRANSFER + " = 'Y' ";
		LeaveTypeCopyVO[] oldTypes = leaveTypeQuery.queryLeaveCopyTypesByOrg(oldhrorg, condition);
		if (ArrayUtils.isEmpty(oldTypes))
			return;
		LeaveTypeCopyVO[] newTypes = leaveTypeQuery.queryLeaveCopyTypesByOrg(newhrorg, condition);
		if (ArrayUtils.isEmpty(newTypes))
			return;

		TimeRuleVO newTimeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(newhrorg);
		IPeriodQueryService periodQuery = NCLocator.getInstance().lookup(IPeriodQueryService.class);
		PeriodVO newPeriod = periodQuery.queryByDate(newhrorg, newJobvo.getBegindate());
		UFLiteralDate enddate = newJobvo.getBegindate();
		PeriodVO oldPeriod = periodQuery.queryByDate(oldhrorg, enddate);
		// 解决跨组织单据调配无法执行 start
		if (oldPeriod == null || newPeriod == null) {// 20160415
			return;
		}
		// 找出共同引用全局或集团的类别的进行处理转移结余时长
		Map<String, LeaveTypeCopyVO> oldMap = CommonUtils.toMap(LeaveTypeCopyVO.PK_TIMEITEM, oldTypes);
		Map<String, LeaveTypeCopyVO> newMap = CommonUtils.toMap(LeaveTypeCopyVO.PK_TIMEITEM, newTypes);
		// LeaveBalanceVO的查询条件
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinPsnjobTable(null);// 首先要保证关联了工作记录表
		String psnjobTableName = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob");
		String oldPsnjobCond = psnjobTableName + ".pk_psnjob = '" + oldJobvo.getPk_psnjob() + "' ";
		FromWhereSQL oldFromWhereSQL = TBMPsndocSqlPiecer.addPsnjobCond2QuerySQL(oldPsnjobCond, fromWhereSQL);
		String newPsnjobCond = psnjobTableName + ".pk_psnjob = '" + newJobvo.getPk_psnjob() + "' ";
		FromWhereSQL newFromWhereSQL = TBMPsndocSqlPiecer.addPsnjobCond2QuerySQL(newPsnjobCond, fromWhereSQL);
		List<LeaveBalanceVO> updateList = new ArrayList<LeaveBalanceVO>();
		// 此处for循环中有数据库操作，但是不需要优化，因为循环次数一般<=1,且不一定走完，若是都提取到循环外，则一定要查询几次数据库，有可能适得其反了
		for (String pk_timeitem : oldMap.keySet()) {
			// 合并补丁NCdp205587553
			// 哺乳假除外
			if (LeaveTypeCopyVO.LEAVETYPE_LACTATION.equalsIgnoreCase(pk_timeitem)) {
				continue;
			}
			if (newMap.get(pk_timeitem) == null)
				continue;
			LeaveTypeCopyVO oldTypeVO = oldMap.get(pk_timeitem);
			LeaveTypeCopyVO newTypeVO = newMap.get(pk_timeitem);
			// 查询原组织记录
			LeaveBalanceVO[] oldLeaveBalanceVOs = queryByCondition(oldhrorg, oldTypeVO, oldPeriod.getTimeyear(),
					oldPeriod.getTimemonth(), oldFromWhereSQL);
			if (ArrayUtils.isEmpty(oldLeaveBalanceVOs))
				continue;
			LeaveBalanceVO oldLeaveVO = null;
			// 原组织的假期记录可能有多条，取未结算的,2013-03-27与需求确定 若都已经结算了则不再转移
			for (LeaveBalanceVO vo : oldLeaveBalanceVOs) {
				if (vo.getIssettlement() != null && vo.getIssettlement().booleanValue())
					continue;
				if (oldLeaveVO == null) {
					oldLeaveVO = vo;
					continue;
				}
			}
			if (oldLeaveVO == null)
				continue;

			// 不需要计算，因为下面的结算中回重新按照结算日期进行计算
			// calculate(oldhrorg, oldTypeVO, oldPeriod.getTimeyear(),
			// oldPeriod.getTimemonth(),oldLeaveBalanceVOs, new
			// UFDateTime(enddate.toDate()));
			// 结算，获取结余时长
			SettlementResult secondSettlement = secondSettlement(oldhrorg, pk_timeitem, oldPeriod.getTimeyear(),
					oldPeriod.getTimemonth(), new LeaveBalanceVO[] { oldLeaveVO }, enddate, true, true, true, false,
					null);
			LeaveBalanceVO[] oldSettlementVos = secondSettlement.getSettledVOs();
			if (ArrayUtils.isEmpty(oldSettlementVos))
				continue;
			// 找出本次结算的那条数据作为转出数据
			// LeaveBalanceVO oldSettVO = null;
			// if(oldSettlementVos.length == 1){
			LeaveBalanceVO oldSettVO = oldSettlementVos[0];
			// }else{
			// for(LeaveBalanceVO vo:oldSettlementVos){
			// if(vo.getSettlementdate().equals(enddate))
			// oldSettVO = vo;
			// }
			// }
			if (oldSettVO == null)
				continue;
			UFDouble outLength = oldSettVO.getRestdayorhour();
			// 转出组织的转出时长,转出后结余为0,当前享有也清零
			oldSettVO.setTranslateout(outLength);
			oldSettVO.setRestdayorhour(new UFDouble(0));
			// oldSettVO.setRealdayorhour(new UFDouble(0));
			// 此时结算后不应该可以再反结算
			oldSettVO.setIsuse(UFBoolean.TRUE);
			// 计算时间的不能大于结算时间
			// if(oldSettVO.getCalculatetime().after(new
			// UFDateTime(oldSettVO.getSettlementdate().toDate()))){
			// oldSettVO.setCalculatetime(new
			// UFDateTime(oldSettVO.getSettlementdate().toDate()));
			// }
			oldSettVO.setCalculatetime(new UFDateTime(enddate.toDate()));
			if (oldSettVO.getIssettlement() == null || !oldSettVO.getIssettlement().booleanValue()) {
				oldSettVO.setIssettlement(UFBoolean.TRUE);
				oldSettVO.setSettlementdate(enddate);
				oldSettVO.setSettlementmethod(3);
			}

			// 根据计量单位进行转换
			UFDouble inLength = new UFDouble(outLength.doubleValue());
			if (!(oldTypeVO.getTimeItemUnit() == newTypeVO.getTimeItemUnit())) {
				// 天转换为小时
				if (oldTypeVO.getTimeitemunit() == TimeItemCopyVO.TIMEITEMUNIT_DAY
						&& newTypeVO.getTimeItemUnit() == TimeItemCopyVO.TIMEITEMUNIT_HOUR) {
					inLength = outLength.multiply(newTimeRuleVO.getDaytohour());
				} else if (oldTypeVO.getTimeitemunit() == TimeItemCopyVO.TIMEITEMUNIT_HOUR
						&& newTypeVO.getTimeItemUnit() == TimeItemCopyVO.TIMEITEMUNIT_DAY) {
					inLength = outLength.div(newTimeRuleVO.getDaytohour());
				}
			}
			// 获取转入组织的记录
			LeaveBalanceVO[] newLeaveBalanceVOs = queryByCondition(newhrorg, newTypeVO, newPeriod.getTimeyear(),
					newPeriod.getTimemonth(), newFromWhereSQL);
			// 计算
			// newLeaveBalanceVOs = calculate(newhrorg, newTypeVO,
			// newPeriod.getTimeyear(), newPeriod.getTimemonth(),
			// newLeaveBalanceVOs, new UFDateTime(enddate.toDate()));
			if (ArrayUtils.isEmpty(newLeaveBalanceVOs))
				continue;
			// 找到没有结算的作为转入数据
			LeaveBalanceVO newVo = null;
			if (newLeaveBalanceVOs.length == 1) {
				newVo = newLeaveBalanceVOs[0];
			} else {
				for (LeaveBalanceVO vo : newLeaveBalanceVOs) {
					if (vo.isSettlement())
						continue;
					newVo = vo;
				}
			}
			// 设置转入时长
			newVo.setTranslatein(inLength);
			newVo.setTransflag(UFBoolean.TRUE);
			// 重新计算结余时长，由系统计算，此处不处理了
			// newVo.setRestdayorhour(newVo.getRestdayorhour().add(inLength));
			calculate(newhrorg, newTypeVO, newPeriod.getTimeyear(), newPeriod.getTimemonth(),
					new LeaveBalanceVO[] { newVo }, new UFDateTime(enddate.toDate()));

			// 保存处理后的数据
			updateList.add(oldSettVO);
			// updateList.add(newVo);
			// newList.add(newVo);
		}
		if (CollectionUtils.isEmpty(updateList))
			return;
		new BaseDAO().updateVOArray(updateList.toArray(new LeaveBalanceVO[0]));
	}

	@Override
	public void translateLeaves(PsnJobVO[] oldJobvos, PsnJobVO[] newJobvos) throws BusinessException {
		if (ArrayUtils.isEmpty(newJobvos))
			return;
		for (int i = 0; i < newJobvos.length; i++) {
			translateLeave(oldJobvos[i], newJobvos[i]);
		}
	}

	@Override
	public LeaveBalanceVO[] queryByPsnPks(String[] pk_psnOrgs, LoginContext context, String pk_leavetype, String year,
			String month) throws BusinessException {
		// 使用pk_psndocs对离职再入职人员会出现一个Pk_psndoc对应两条数据的情况，修改为使用pk_psnorg
		// FromWhereSQL createPsndocArrayQuerySQL =
		// TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
		FromWhereSQL fromWhereSql = TBMPsndocSqlPiecer.addTBMPsndocCond2QuerySQL(" pk_psnorg in(select "
				+ TempTableVO.IN_PK + " from " + new InSQLCreator().createTempTable(pk_psnOrgs) + ") ", null);
		return queryByCondition(context, pk_leavetype, year, month, fromWhereSql);
	}

	@Override
	public String[] queryPsnPksByCondition(LoginContext context, String pk_leavetype, String year, String month,
			FromWhereSQL fromWhereSQL) throws BusinessException {
		LeaveBalanceVO[] queryByCondition = queryByCondition(context, pk_leavetype, year, month, fromWhereSQL);
		return StringPiecer.getStrArray(queryByCondition, LeaveBalanceVO.PK_PSNORG);// 此处无法使用主键，因为有新构造的vo，数据库中不存在
	}

	// BEGIN 张恒{21746} 特休假薪資計算 2018/8/29
	// mod tank 加入组织关系过滤
	public UFLiteralDate[] getSpecialBeginEnd(String pk_psndoc, String year, String pk_psnorg) throws BusinessException {

		BaseDAO baseDao = new BaseDAO();
		// 获取员工维护里面组织关系hi_psnorg里面的年资起算日
		String specialSql = " select workagestartdate,begindate from hi_psnorg where pk_psnorg = '" + pk_psnorg + "' ";
		List<Object[]> specialList = (List<Object[]>) baseDao.executeQuery(specialSql, new ArrayListProcessor());
		UFLiteralDate[] specialBeginEnd = new UFLiteralDate[2];
		if (!CollectionUtils.isEmpty(specialList)) {
			String calculateDate = "";
			if (null != specialList.get(0)) {
				// 获取年资起算日
				calculateDate = specialList.get(0)[0] == null ? specialList.get(0)[1].toString().substring(4, 10)
						: specialList.get(0)[0].toString().substring(4, 10);
				if ("-02-29".equals(calculateDate)) {
					int yearf = Integer.valueOf(year);
					if (!((yearf % 4 == 0 && yearf % 100 != 0) || yearf % 400 == 0)) {
						// 如果不是闰年 calculateDate 置为 28 号 yejk 181228 #23932
						calculateDate = "-02-28";
					}
				}
				// 获取当前年资起算日
				UFLiteralDate beginCalculateDate = new UFLiteralDate(year + calculateDate);

				if ("-02-29".equals(calculateDate)) {
					int yearf = Integer.valueOf(year) + 1;
					if (!((yearf % 4 == 0 && yearf % 100 != 0) || yearf % 400 == 0)) {
						// 如果不是闰年 calculateDate 置为 28 号 yejk 181228 #23932
						calculateDate = "-02-28";
					}
				}
				// 获取当前年资起算日结束日期
				UFLiteralDate endCalculateDate = new UFLiteralDate(String.valueOf(Integer.valueOf(year) + 1)
						+ calculateDate).getDateBefore(1);
				specialBeginEnd[0] = beginCalculateDate;
				specialBeginEnd[1] = endCalculateDate;
			}
		}
		return specialBeginEnd;
	}
	// END 张恒{21746} 特休假薪資計算 2018/8/29

	// //判断是否为年休假
	// public Boolean isSpecialHoliday(LeaveTypeCopyVO typeVO) throws
	// BusinessException{
	//
	// if(null == typeVO){
	// return false;
	// }
	//
	// //获取系统认定的年休假的编码
	// String itemcode =
	// String.valueOf(SysInitQuery.getParaString(typeVO.getPk_org(),
	// "TBMTWHR01"));
	// //获取当前假期类别的编码
	// String timeitemcode = String.valueOf(typeVO.getTimeitemcode());
	//
	// if(itemcode.equals(timeitemcode)){
	// return true;
	// }else{
	// return false;
	// }
	//
	// }
	// //END 张恒{21481} 特休假薪資計算 2018/8/20

}
