package nc.impl.ta.daystat;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.naming.NamingException;

import nc.bs.bd.baseservice.IBaseServiceConst;
import nc.bs.bd.pub.distribution.util.BDDistTokenUtil;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.IEventType;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfo;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.execute.Executor;
import nc.bs.framework.execute.RunnableItem;
import nc.bs.logging.Logger;
import nc.bs.uap.lock.PKLock;
import nc.bs.uap.oid.OidGenerator;
import nc.bs.uif2.IActionCode;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.hr.devitf.IDevItfQueryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.hrss.IURLGenerator;
import nc.itf.hr.message.IHRMessageSend;
import nc.itf.ta.AwayServiceFacade;
import nc.itf.ta.CheckTimeServiceFacade;
import nc.itf.ta.IDayStatManageMaintain;
import nc.itf.ta.IDayStatQueryMaintain;
import nc.itf.ta.IHRHolidayManageService;
import nc.itf.ta.IItemQueryService;
import nc.itf.ta.ILateEarlyQueryService;
import nc.itf.ta.IOvertimeManageService;
import nc.itf.ta.IPeriodManageService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeDataQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.LeaveServiceFacade;
import nc.itf.ta.OverTimeServiceFacade;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.ShutdownServiceFacade;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.itf.ta.algorithm.DataFilterUtils;
import nc.itf.ta.algorithm.ICheckTime;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.ITimeScopeWithBillType;
import nc.itf.ta.algorithm.SolidifyUtils;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.customization.IDayDataCreator;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.pubitf.rbac.IUserPubService;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hr.hrss.SSOInfo;
import nc.vo.hr.message.HRBusiMessageVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.sm.UserVO;
import nc.vo.ta.algorithm.SolidifyPara;
import nc.vo.ta.away.AwayRegVO;
import nc.vo.ta.bill.BillMutexRule;
import nc.vo.ta.bill.TaMessageConst;
import nc.vo.ta.customization.DayCalParam;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.daystat.DayStatVO;
import nc.vo.ta.daystat.DayStatbVO;
import nc.vo.ta.daystat.DaystatImportParam;
import nc.vo.ta.daystat.DeptDayStatVO;
import nc.vo.ta.holiday.HRHolidayVO;
import nc.vo.ta.item.DatePeriodFormulaUtils;
import nc.vo.ta.item.HolidayFormulaUtils;
import nc.vo.ta.item.ItemCopyVO;
import nc.vo.ta.item.ItemVO;
import nc.vo.ta.item.OverTimeFormulaUtils;
import nc.vo.ta.item.PreviousDayFormulaUtils;
import nc.vo.ta.lateearly.LateEarlyVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.pub.IMetaDataIDConst;
import nc.vo.ta.pub.PsnInSQLDateScope;
import nc.vo.ta.pub.SQLParamWrapper;
import nc.vo.ta.shutdown.ShutdownRegVO;
import nc.vo.ta.statistic.IVOWithDynamicAttributes;
import nc.vo.ta.statistic.annotation.ItemClass;
import nc.vo.ta.timeitem.AwayTypeCopyVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timeitem.ShutDownTypeCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.ta.vieworder.ViewOrderVO;
import nc.vo.uif2.LoginContext;
import nc.vo.vorg.AdminOrgVersionVO;
import nc.vo.vorg.DeptVersionVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class DayStatMaintainImpl implements IDayStatManageMaintain, IDayStatQueryMaintain {
	@Override
	public void generate_RequiresNew(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		generate(pk_org, fromWhereSQL, beginDate, endDate);
	}

	@Override
	public void generate(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		// 权限sql 2013-03-29 modified 后台任务自动生成无法获取权限，会报错，因此后台不加权限过滤
		if (!PubEnv.UAP_USER.equalsIgnoreCase(PubEnv.getPk_user()))// 日报的生成权限和考勤档案的维护权限一致
			fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT,
					fromWhereSQL);
		// fromWhereSQL =
		// TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc",
		// "DayStatGenerate", fromWhereSQL);
		// String[] pk_psndocs =
		// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocsByCondition(pk_org,
		// fromWhereSQL, beginDate, endDate);
		String[] pk_psndocs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryLatestPsndocPksByCondition(pk_org, fromWhereSQL, beginDate, endDate);
		if (ArrayUtils.isEmpty(pk_psndocs))
			return;
		// //将这些人的日报锁住（最细应该锁到人天，但这样锁的数据量太大，折中考虑，只锁人+组织）
		// PKLock lock = PKLock.getInstance();
		// String[] pk_psndocsLockacble = new String[pk_psndocs.length];
		// for(int i=0;i<pk_psndocs.length;i++){
		// pk_psndocsLockacble[i]= "daystat"+pk_org+pk_psndocs[i];
		// }
		// InSQLCreator isc = new InSQLCreator();
		// try{
		// boolean acquired = lock.acquireBatchLock(pk_psndocsLockacble,
		// PubEnv.getPk_user(), null);
		// if(!acquired)
		// throw new
		// BusinessException(ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0089")
		// /*@res "他人正在生成考勤日报，请稍候再试!"*/);
		// DayStatServiceImpl serviceImpl =new DayStatServiceImpl();
		// //创建空的人员日报记录
		// serviceImpl.createDayStatRecord(pk_org, fromWhereSQL, beginDate,
		// endDate);
		// //计算日报
		// generate0(pk_org, pk_psndocs, beginDate, endDate);
		// // 生成后事件
		// PsnInSQLDateScope psnInSql = new PsnInSQLDateScope();
		// psnInSql.setPk_org(pk_org);
		// psnInSql.setPsndocInSQL(isc.getInSQL(pk_psndocs));
		// psnInSql.setBeginDate(beginDate);
		// psnInSql.setEndDate(endDate);
		// EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.DAYSTAT,
		// "2001", psnInSql));
		//
		// //业务日志
		// TaBusilogUtil.writeDayStatGenerate(pk_org, pk_psndocs, beginDate,
		// endDate);
		// }
		// finally{
		// isc.clear();
		// lock.releaseBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(),
		// null);
		// }

		generateBatch(pk_org, pk_psndocs, beginDate, endDate);
	}

	@Override
	public void generate(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		// if(ArrayUtils.isEmpty(pk_psndocs))
		// return;
		// //维护权过滤
		// FromWhereSQL powerWhereSQL =
		// TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc",
		// "DayStatGenerate", null);
		// String[] powerPks =
		// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocsByCondition(pk_org,
		// powerWhereSQL, beginDate, endDate);
		// if(ArrayUtils.isEmpty(powerPks))
		// return;
		// List<String> pkList = new ArrayList<String>();
		// for(int i=0;i<powerPks.length;i++){
		// for(int j=0;j<pk_psndocs.length;j++){
		// if(powerPks[i].equals(pk_psndocs[j]))
		// pkList.add(pk_psndocs[j]);
		// }
		// }
		// pk_psndocs=CollectionUtils.isEmpty(pkList)?null:pkList.toArray(new
		// String[0]);
		//
		// if(ArrayUtils.isEmpty(pk_psndocs))
		// return;
		// //将这些人的日报锁住（最细应该锁到人天，但这样锁的数据量太大，折中考虑，只锁人+组织）
		// PKLock lock = PKLock.getInstance();
		// String[] pk_psndocsLockacble = new String[pk_psndocs.length];
		// for(int i=0;i<pk_psndocs.length;i++){
		// pk_psndocsLockacble[i]= "daystat"+pk_org+pk_psndocs[i];
		// }
		// InSQLCreator isc = new InSQLCreator();
		// try{
		// boolean acquired = lock.acquireBatchLock(pk_psndocsLockacble,
		// PubEnv.getPk_user(), null);
		// if(!acquired)
		// throw new
		// BusinessException(ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0089")
		// /*@res "他人正在生成考勤日报，请稍候再试!"*/);
		// FromWhereSQL fromWhereSQL =
		// TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
		// DayStatServiceImpl serviceImpl =new DayStatServiceImpl();
		// //创建空的人员日报记录
		// serviceImpl.createDayStatRecord(pk_org, fromWhereSQL, beginDate,
		// endDate);
		// TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
		// //计算日报
		// generate0(pk_org, pk_psndocs, beginDate, endDate);
		// // 生成后事件
		// PsnInSQLDateScope psnInSql = new PsnInSQLDateScope();
		// psnInSql.setPk_org(pk_org);
		// psnInSql.setPsndocInSQL(isc.getInSQL(pk_psndocs));
		// psnInSql.setBeginDate(beginDate);
		// psnInSql.setEndDate(endDate);
		// EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.DAYSTAT,
		// "2001", psnInSql));
		// }
		// finally{
		// isc.clear();
		// lock.releaseBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(),
		// null);
		// }
		// 日报生成取考勤档案的维护权限
		FromWhereSQL powerWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
		powerWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT,
				powerWhereSQL);
		String[] powerPks = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryLatestPsndocPksByCondition(pk_org, powerWhereSQL, beginDate, endDate);

		generateBatch(pk_org, powerPks, beginDate, endDate);
	}

	/**
	 * 有的客户的人员非常多，数据生成时可能数量很大，导致内存溢出或者达到了数据库查询的限制，导致数据生成失败 在此进行分批处理，每1000人执行一次
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	private void generateBatch(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs) || StringUtils.isEmpty(pk_org) || beginDate.afterDate(endDate))
			return;
		int length = pk_psndocs.length;
		if (length < 500) {
			generate4Once(pk_org, pk_psndocs, beginDate, endDate);
			return;
		}
		List<String> psnList = new ArrayList<String>();
		List<DayStatGenerateThread> threadList = new ArrayList<DayStatGenerateThread>();
		String errorMsg = "";
		InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		int count = 0;
		for (int i = 0; i < length; i++) {
			count++;
			psnList.add(pk_psndocs[i]);
			if (count >= 199) {
				psnList.toArray(new String[0]);
				String[] array = psnList.toArray(new String[0]);
				DayStatGenerateThread dayThread = new DayStatGenerateThread(pk_org, array, beginDate, endDate,
						invocationInfo);
				dayThread.start();
				threadList.add(dayThread);
				count = 0;
				psnList.clear();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					Logger.error(e.getMessage(), e);
					errorMsg += e.getMessage();
				}
			}
		}
		// 处理一下不足数的数据
		String[] array = psnList.toArray(new String[0]);
		DayStatGenerateThread dayThread = new DayStatGenerateThread(pk_org, array, beginDate, endDate, invocationInfo);
		dayThread.start();
		threadList.add(dayThread);

		for (int t = 0; t < threadList.size();) {
			if (RunnableItem.FINISHED != threadList.get(t).getDayThreadStatus()) {
				try {
					Thread.sleep(200);
					continue;
				} catch (InterruptedException e) {
					Logger.error(e.getMessage(), e);
					errorMsg += e.getMessage();
				}
			}
			t++;
		}

		// 收集信息
		for (DayStatGenerateThread thread : threadList) {
			if (StringUtils.isNotBlank(thread.getErrMsg())) {
				errorMsg += thread.getErrMsg();
			}
		}

		if (StringUtils.isNotBlank(errorMsg)) {
			throw new BusinessException(errorMsg);
		}

	}

	/**
	 * 日报生成的批量处理中的一次执行
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 *            一千人一次
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	@Override
	public void generate4Once(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs) || StringUtils.isEmpty(pk_org) || beginDate.afterDate(endDate))
			return;
		// //维护权过滤
		// FromWhereSQL powerWhereSQL =
		// TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
		// powerWhereSQL =
		// TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc",
		// "DayStatGenerate", powerWhereSQL);
		// // long b1 = System.currentTimeMillis();
		// // String[] powerPks =
		// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocsByCondition(pk_org,
		// powerWhereSQL, beginDate, endDate);
		// // long b2 = System.currentTimeMillis();
		// // long b11 = b2 - b1;
		// String[] powerPks =
		// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocPksByCondition(pk_org,
		// powerWhereSQL, beginDate, endDate);
		// // long longb22 = System.currentTimeMillis() - b2;
		// if(ArrayUtils.isEmpty(powerPks))
		// return;
		// List<String> pkList = new ArrayList<String>();
		// for(int i=0;i<powerPks.length;i++){
		// for(int j=0;j<pk_psndocs.length;j++){
		// if(powerPks[i].equals(pk_psndocs[j]))
		// pkList.add(pk_psndocs[j]);
		// }
		// }
		// pk_psndocs=CollectionUtils.isEmpty(pkList)?null:pkList.toArray(new
		// String[0]);

		// if(ArrayUtils.isEmpty(pk_psndocs))
		// return;
		// 将这些人的日报锁住（最细应该锁到人天，但这样锁的数据量太大，折中考虑，只锁人+组织）
		PKLock lock = PKLock.getInstance();
		String[] pk_psndocsLockacble = new String[pk_psndocs.length];
		for (int i = 0; i < pk_psndocs.length; i++) {
			pk_psndocsLockacble[i] = "daystat" + pk_org + pk_psndocs[i];
		}
		// InSQLCreator isc = new InSQLCreator();
		try {
			boolean acquired = lock.acquireBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
			if (!acquired)
				throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0089")
				/* @res "他人正在生成考勤日报，请稍候再试!" */);
			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
			DayStatServiceImpl serviceImpl = new DayStatServiceImpl();
			// 创建空的人员日报记录
			serviceImpl.createDayStatRecord(pk_org, fromWhereSQL, beginDate, endDate);
			TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
			// 计算日报
			generate0(pk_org, pk_psndocs, beginDate, endDate);
			// 生成后事件
			// PsnInSQLDateScope psnInSql = new PsnInSQLDateScope();
			// psnInSql.setPk_org(pk_org);
			// psnInSql.setPsndocInSQL(isc.getInSQL(pk_psndocs));
			// psnInSql.setBeginDate(beginDate);
			// psnInSql.setEndDate(endDate);
			// EventDispatcher.fireEvent(new
			// BusinessEvent(IMetaDataIDConst.DAYSTAT, "2001", psnInSql));
			fireEvent(pk_org, pk_psndocs, beginDate, endDate);
			dayStatLog(pk_org, pk_psndocs, beginDate, endDate);
		} finally {
			lock.releaseBatchLock(pk_psndocsLockacble, PubEnv.getPk_user(), null);
		}
	}

	/**
	 * 发送事件另起线程，不影响日报计算的相应时间
	 * 
	 * @param psnInSql
	 */
	private void fireEvent(final String pk_org, final String[] pk_psndocs, final UFLiteralDate beginDate,
			final UFLiteralDate endDate) {

		// final String pk_group = PubEnv.getPk_group();
		// final String bizCenterCode =
		// InvocationInfoProxy.getInstance().getBizCenterCode();
		// final long bizDateTime =
		// InvocationInfoProxy.getInstance().getBizDateTime();
		// final String langCode =
		// InvocationInfoProxy.getInstance().getLangCode();
		// final String userDataSource =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// final String userId = InvocationInfoProxy.getInstance().getUserId();
		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				// 线程中环境信息会丢失，主动的设置一下
				// InvocationInfoProxy.getInstance().setGroupId(pk_group);
				// InvocationInfoProxy.getInstance().setBizCenterCode(bizCenterCode);
				// InvocationInfoProxy.getInstance().setBizDateTime(bizDateTime);
				// InvocationInfoProxy.getInstance().setLangCode(langCode);
				// InvocationInfoProxy.getInstance().setUserDataSource(userDataSource);
				// InvocationInfoProxy.getInstance().setUserId(userId);
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				try {
					InSQLCreator isc = new InSQLCreator();
					PsnInSQLDateScope psnInSql = new PsnInSQLDateScope();
					psnInSql.setPk_org(pk_org);
					psnInSql.setPsndocInSQL(isc.getInSQL(pk_psndocs));
					psnInSql.setBeginDate(beginDate);
					psnInSql.setEndDate(endDate);
					EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.DAYSTAT, "2001", psnInSql));
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
				}
			}
		}).start();
	}

	/**
	 * 记录日志另起线程，不影响日报计算的相应时间
	 * 
	 * @param psnInSql
	 */
	private void dayStatLog(final String pk_org, final String[] pk_psndocs, final UFLiteralDate beginDate,
			final UFLiteralDate endDate) {

		// final String pk_group = PubEnv.getPk_group();
		// final String bizCenterCode =
		// InvocationInfoProxy.getInstance().getBizCenterCode();
		// final long bizDateTime =
		// InvocationInfoProxy.getInstance().getBizDateTime();
		// final String langCode =
		// InvocationInfoProxy.getInstance().getLangCode();
		// final String userDataSource =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// final String userId = InvocationInfoProxy.getInstance().getUserId();
		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				// 线程中环境信息会丢失，主动的设置一下
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				// InvocationInfoProxy.getInstance().setGroupId(pk_group);
				// InvocationInfoProxy.getInstance().setBizCenterCode(bizCenterCode);
				// InvocationInfoProxy.getInstance().setBizDateTime(bizDateTime);
				// InvocationInfoProxy.getInstance().setLangCode(langCode);
				// InvocationInfoProxy.getInstance().setUserDataSource(userDataSource);
				// InvocationInfoProxy.getInstance().setUserId(userId);

				TaBusilogUtil.writeDayStatGenerate(pk_org, pk_psndocs, beginDate, endDate);
			}
		}).start();
	}

	private void generate0(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		// Java类计算类型项目的计算参数
		DayCalParam dayCalParam = DayStatCalculationHelper.createPara(pk_org);
		InSQLCreator isc = new InSQLCreator();
		try {
			// 准备计算参数
			CalSumParam calSumParam = prepareCalSumParam(pk_org, pk_psndocs, beginDate, endDate, isc, dayCalParam);
			// 计算单据时长
			processBills(calSumParam);
			// 如果日报项目要查询假日享有信息，则需要提前将假日享有明细插入到tbm_holidayenjoyh和tbm_holidayenjoyb中
			IHRHolidayManageService holidaysService = NCLocator.getInstance().lookup(IHRHolidayManageService.class);
			if (calSumParam.containsHolidayFunc)
				holidaysService.createEnjoyDetail(pk_org, pk_psndocs, beginDate.getDateBefore(365 * 3),
						endDate.getDateAfter(365 * 3));
			IPeriodManageService periodService = NCLocator.getInstance().lookup(IPeriodManageService.class);
			if (calSumParam.containsDatePeriodVar)
				periodService.createDatePeriod(pk_org, beginDate, endDate);

			// 计算日报项目
			processItems(calSumParam);
			if (calSumParam.containsHolidayFunc)
				holidaysService.clearEnjoyDetail();
			if (calSumParam.containsDatePeriodVar)
				periodService.clearDatePeriod();

			// 若在日报项目中用到加班开始和结束时间，要创建临时表，使用完要清空
			if (calSumParam.containsOvertimeBeginEndTimeVar) {
				IOvertimeManageService otservice = NCLocator.getInstance().lookup(IOvertimeManageService.class);
				otservice.clearOvertimeBelongData();
			}

			sendMessgThread(pk_org, pk_psndocs, beginDate, endDate, calSumParam);

		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	/**
	 * 日报生成的效率优化，发生邮件通知另起线程处理
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param calSumParam
	 * @throws BusinessException
	 */
	private void sendMessgThread(final String pk_org, final String[] pk_psndocs, final UFLiteralDate beginDate,
			final UFLiteralDate endDate, final CalSumParam calSumParam) throws BusinessException {

		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();

		new Executor(new Runnable() {
			@Override
			public void run() {
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				try {
					sendMessage(pk_org, pk_psndocs, calSumParam, beginDate, endDate);
				} catch (BusinessException e) {
					Logger.error(" daystat generate error for sendMessge " + e.getMessage(), e);
				}
			}
		}).start();
	}

	/**
	 * 准备参数，例如所有人的单据，考勤项目等等
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	private CalSumParam prepareCalSumParam(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, InSQLCreator isc, DayCalParam dayCalParam) throws BusinessException {
		CalSumParam calSumParam = new CalSumParam();
		calSumParam.dayCalParam = dayCalParam;
		calSumParam.pk_org = pk_org;
		calSumParam.pk_psndocs = pk_psndocs;
		calSumParam.psndocInSQL = isc.getInSQL(pk_psndocs);
		calSumParam.containsPreviousDayFunc = PreviousDayFormulaUtils.existsPreviousItem(dayCalParam.itemVOs);
		calSumParam.containsHolidayFunc = HolidayFormulaUtils.existsHolidayItem(dayCalParam.itemVOs);
		calSumParam.containsDatePeriodVar = DatePeriodFormulaUtils.existsDatePeriodItem(dayCalParam.itemVOs);
		calSumParam.containsOvertimeBeginEndTimeVar = OverTimeFormulaUtils
				.existsBeginOrEndTimeItem(dayCalParam.itemVOs);

		calSumParam.timeRuleVO = dayCalParam.timeruleVO;
		calSumParam.paramValues = dayCalParam.paramValues;
		if (dayCalParam.leaveItemMap != null) {
			calSumParam.leaveCopyVOs = dayCalParam.leaveItemMap.values().toArray(new LeaveTypeCopyVO[0]);
		}
		if (dayCalParam.awayItemMap != null) {
			calSumParam.awayCopyVOs = dayCalParam.awayItemMap.values().toArray(new AwayTypeCopyVO[0]);
		}
		if (dayCalParam.overtimeItemMap != null) {
			calSumParam.overCopyVOs = dayCalParam.overtimeItemMap.values().toArray(new OverTimeTypeCopyVO[0]);
		}
		if (dayCalParam.shutdownItemMap != null) {
			calSumParam.shutCopyVOs = dayCalParam.shutdownItemMap.values().toArray(new ShutDownTypeCopyVO[0]);
		}
		calSumParam.dayItemVOs = dayCalParam.itemVOs;
		UFLiteralDate[] allUFDates = CommonUtils.createDateArray(beginDate, endDate, 2, 2);
		calSumParam.allDates = allUFDates;
		calSumParam.dateBeginIndex = 2;
		calSumParam.dateEndIndex = allUFDates.length - 3;
		// 日期与期间对应关系的map，key是日期，value是数组，第一个元素是日期所属期间，第二个元素是所属期间的下一个期间
		Map<String, String[]> datePeriodMap = new HashMap<String, String[]>();
		processPeriodAndNextPeriodOfDate(
				pk_org,
				CommonUtils.createDateArray(allUFDates[0].getDateBefore(300),
						allUFDates[allUFDates.length - 1].getDateAfter(300)), datePeriodMap);
		calSumParam.datePeriodMap = datePeriodMap;
		// 考勤项目中是否存在java类的项目。如果不存在java类项目，则后面的一些操作可以不做，以提高计算效率
		boolean existsJavaItem = false;
		for (ItemCopyVO itemVO : calSumParam.dayItemVOs) {
			if (itemVO.getSrc_flag().intValue() == ItemCopyVO.SRC_FLAG_JAVA) {
				existsJavaItem = true;
				break;
			}
		}
		// 日期数组，后面update java类型的项目值到数据库的时候要用到
		// String[] allDates = CommonUtils.toStringArray(calSumParam.allDates,
		// calSumParam.dateBeginIndex, calSumParam.dateEndIndex);
		// 所有人员的日报的主表主键,第一个string是人员主键pk_psndoc,第二个string是日期，第三个string是pk_daystat。这个map的主要作用是为后面的daystatb子表生成做准备
		// Logger.error("查询日报主键map开始："+System.currentTimeMillis());
		long time = System.currentTimeMillis();
		calSumParam.daystatPKMap = DayStatCalculationHelper.getDaystatPKMap(calSumParam.pk_org,
				calSumParam.psndocInSQL, beginDate.toString(), endDate.toString());
		Logger.debug("构造日报主键map耗时：" + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		// 所有人员的工作日历map,要求往前往后都推2天(即用户如果选择计算10号到11号的日报，那么此处应该查询8号到13号的工作日历),传进来的dateArray已经是比日报生成范围要往前往后两天了：
		// 之所以往前后推这么多，是因为计算加班单的长度，在单子很长的时候，可能会用到很多天的工作日历，很有可能大大超过用户输入的日期范围
		calSumParam.calendarMap = NCLocator
				.getInstance()
				.lookup(IPsnCalendarQueryService.class)
				.queryCalendarVOByPsnInSQL(calSumParam.pk_org, beginDate.getDateBefore(1), endDate.getDateAfter(1),
						calSumParam.psndocInSQL);
		time = System.currentTimeMillis();
		// Logger.error("查询工作日历结束："+System.currentTimeMillis());
		calSumParam.aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		if (!MapUtils.isEmpty(calSumParam.aggShiftMap)) {
			calSumParam.shiftMap = new HashMap<String, ShiftVO>();
			for (String key : calSumParam.aggShiftMap.keySet()) {
				calSumParam.shiftMap.put(key, calSumParam.aggShiftMap.get(key).getShiftVO());
			}
		}
		// 所有人员的考勤档案数据,key是人员主键，value是在这段时间内的考勤档案vo(一般情况下只有一条。如果有多条，那么已经按时间先后排好序)
		calSumParam.tbmPsndocMap = NCLocator
				.getInstance()
				.lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocInSQL(calSumParam.pk_org, calSumParam.psndocInSQL, beginDate, endDate, true,
						true);
		calSumParam.allDateOrgMap = TBMPsndocVO.createDateOrgMapByTbmPsndocVOMap(calSumParam.tbmPsndocMap, beginDate,
				endDate);
		// 获取java类项目的计算类实例
		time = System.currentTimeMillis();
		if (existsJavaItem) {
			// 下面的这些map只在java项目计算时有用，因此只在存在java项目的时候初始化
			// 所有人员的timedata数据,第一个string是人员主键，第二个UFLiteralDate是日期，value是timedatavo
			calSumParam.timedataMap = NCLocator.getInstance().lookup(ITimeDataQueryService.class)
					.queryVOMapByPsndocInSQL(calSumParam.pk_org, beginDate, endDate, calSumParam.psndocInSQL);
			// 所有人员的lateearly数据
			calSumParam.lateearlyMap = NCLocator.getInstance().lookup(ILateEarlyQueryService.class)
					.queryVOMapByPsndocInSQL(calSumParam.pk_org, beginDate, endDate, calSumParam.psndocInSQL);
			IDevItfQueryService service = NCLocator.getInstance().lookup(IDevItfQueryService.class);
			calSumParam.dataCreatorMap = new HashMap<String, IDayDataCreator>();
			for (ItemCopyVO vo : calSumParam.dayItemVOs) {
				if (vo.getSrc_flag() == ItemVO.SRC_FLAG_JAVA) {
					IDayDataCreator creator = (IDayDataCreator) service.queryByCodeAndObj(ICommonConst.ITF_CODE_DAY,
							vo.getPrimaryKey());
					calSumParam.dataCreatorMap.put(vo.getItem_code(), creator);
				}
			}
		}
		Logger.debug("查询timedata、lateearly、考勤档案耗时：" + (System.currentTimeMillis() - time));
		// 取得所有人员、所有日期（startDate的前两天至endDate的后两天）的单据数据
		// key是人员主键，value是人员的休假单子表数组，下面的加班单出差单停工单都一样
		// ssx modified on 2019-10-18
		// 多取前一天和後一天單據，避免排班跨天時遺漏單據
		time = System.currentTimeMillis();
		calSumParam.leaveMap = LeaveServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(calSumParam.pk_org,
				calSumParam.psndocInSQL, beginDate.getDateBefore(1), endDate.getDateAfter(1));
		calSumParam.lactationMap = LeaveServiceFacade.queryAllLactationVOIncEffictiveByPsndocInSQLDate(
				calSumParam.pk_org, calSumParam.psndocInSQL, beginDate.getDateBefore(1), endDate.getDateAfter(1));
		calSumParam.awayMap = AwayServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(calSumParam.pk_org,
				calSumParam.psndocInSQL, beginDate.getDateBefore(1), endDate.getDateAfter(1));
		calSumParam.shutMap = ShutdownServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(calSumParam.pk_org,
				calSumParam.psndocInSQL, beginDate.getDateBefore(1), endDate.getDateAfter(1));
		calSumParam.overMap = OverTimeServiceFacade.queryAllSuperVOIncEffectiveByPsndocInSQLDate(calSumParam.pk_org,
				calSumParam.psndocInSQL, beginDate.getDateBefore(2), endDate.getDateAfter(2));
		calSumParam.checkTimesMap = CheckTimeServiceFacade.queryCheckTimeMapByPsndocInSQLAndDateScope(
				calSumParam.pk_org, calSumParam.psndocInSQL, beginDate.getDateBefore(1), endDate.getDateAfter(1));
		// end
		Logger.debug("查询单据耗时：" + (System.currentTimeMillis() - time));
		calSumParam.billMutexRule = BillMutexRule.createBillMutexRule(calSumParam.timeRuleVO.getBillmutexrule());
		// 一个工作日的时长,在考勤规则中定义
		calSumParam.workDayLength = calSumParam.timeRuleVO == null ? 8 : calSumParam.timeRuleVO.getDaytohour()
				.doubleValue();
		return calSumParam;
	}

	/**
	 * 计算出休停加单据的日报时长 其中，出休停单据的日报时长是单据时间落在每一天之内的时长 加班单的日报时长，是整张单据的时长归到加班单归属日上
	 * 
	 * @param calSumParam
	 * @throws DAOException
	 */
	private void processBills(CalSumParam calSumParam) throws BusinessException {
		// TODO:按人按天循环，生成所有人天的加班、出差、休假、停工数据
		// Logger.error("开始生成加班出差等数据："+System.currentTimeMillis());
		// 需要插入到数据的子表vo
		List<DayStatbVO> statbVOList = new ArrayList<DayStatbVO>();
		int psnCount = calSumParam.pk_psndocs.length;
		Map<String, TimeZone> timeZoneMap = calSumParam.timeRuleVO.getTimeZoneMap();

		// 假日享有情况，假日加班时使用，待优化
		Map<String, HRHolidayVO[]> psnEnjoyHolidayScope = new HashMap<String, HRHolidayVO[]>();
		Map<String, OvertimeRegVO[]> overMap = calSumParam.overMap;
		List<OvertimeRegVO> overList = new ArrayList<OvertimeRegVO>();
		if (MapUtils.isNotEmpty(overMap)) {
			for (String pk_psndoc : overMap.keySet()) {
				OvertimeRegVO[] overtimeRegVOs = overMap.get(pk_psndoc);
				if (ArrayUtils.isEmpty(overtimeRegVOs))
					continue;
				for (OvertimeRegVO overvo : overtimeRegVOs) {
					overList.add(overvo);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(overList)) {
			psnEnjoyHolidayScope = BillProcessHelperAtServer.getOverTimeHolidayScope(overList
					.toArray(new OvertimeRegVO[0]));
		}

		// 按人员循环处理
		for (int psnIndex = 0; psnIndex < psnCount; psnIndex++) {
			String pk_psndoc = calSumParam.pk_psndocs[psnIndex];
			Map<UFLiteralDate, String> dateOrgMap = calSumParam.allDateOrgMap.get(pk_psndoc);
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
			List<TBMPsndocVO> psndocList = calSumParam.tbmPsndocMap.get(pk_psndoc);
			if (CollectionUtils.isEmpty(psndocList))
				continue;
			// 所有的休假单（在整个计算日期范围内有交集，比如2008.07-23到2008.08.22，可能会往前往后挪几天）
			LeaveRegVO[] leaveBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.leaveMap);
			AwayRegVO[] awayBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.awayMap);// 所有的出差单
			OvertimeRegVO[] overtimeBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.overMap);// 所有的加班单
			ShutdownRegVO[] shutdownBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.shutMap);// 所有的停工单
			// 先把所有单据整个做一次交、切
			ITimeScopeWithBillType[] processedBills = BillProcessHelper.crossAllBills(leaveBills, awayBills,
					overtimeBills, shutdownBills);
			// 如果交切的结果为空，则不需要任何处理，continue即可
			if (processedBills == null || processedBills.length == 0) {
				continue;
			}

			// 2013-06-14添加for循环进行固化， filter单据时需要的工作时间段需要固化（例如过滤出的加班单是减去工作时间段后的）
			for (int dateIndex = calSumParam.dateBeginIndex; dateIndex <= calSumParam.dateEndIndex; dateIndex++) {
				// 当前天
				UFLiteralDate curDate = calSumParam.allDates[dateIndex];
				if (!TBMPsndocVO.isIntersect(psndocList, curDate.toString()))// 如果这天无考勤档案记录，则不用生成
					continue;
				// 当前天的班次以及前一天和后一天的班次
				AggPsnCalendar curCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, curDate);// 当前天的班次

				ShiftVO curShift = curCalendar == null ? null : curCalendar.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, curCalendar.getPsnCalendarVO()
								.getPk_shift());
				AggPsnCalendar preCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc,
						calSumParam.allDates[dateIndex - 1]);// 前一天的班次
				ShiftVO preShift = preCalendar == null ? null : preCalendar.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, preCalendar.getPsnCalendarVO()
								.getPk_shift());
				AggPsnCalendar nextCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc,
						calSumParam.allDates[dateIndex + 1]);// 后一天的班次
				ShiftVO nextShift = nextCalendar == null ? null : nextCalendar.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, nextCalendar.getPsnCalendarVO()
								.getPk_shift());
				TimeZone curTimeZone = CommonUtils.ensureTimeZone(calSumParam.timeRuleVO.getTimeZoneMap().get(
						dateOrgMap.get(curDate)));
				TimeZone preTimeZone = CommonUtils.ensureTimeZone(calSumParam.timeRuleVO.getTimeZoneMap().get(
						dateOrgMap.get(calSumParam.allDates[dateIndex - 1])));
				TimeZone nextTimeZone = CommonUtils.ensureTimeZone(calSumParam.timeRuleVO.getTimeZoneMap().get(
						dateOrgMap.get(calSumParam.allDates[dateIndex + 1])));
				// 当天的班次如果是弹性班，则需要先固化，才能用来计算单据时长
				if (curCalendar != null && curCalendar.getPsnCalendarVO().isFlexibleFinal()) {
					// 考勤时间段
					ITimeScope kqScope = ShiftVO.toKqScope(curShift, preShift, nextShift, curDate.toString(),
							curTimeZone, preTimeZone, nextTimeZone);
					// 用来固化的参数
					SolidifyPara solidifyPara = calSumParam.toSolidifyPara(pk_psndoc, curDate, kqScope);
					// 进行固化
					curCalendar.setPsnWorkTimeVO(SolidifyUtils.solidify(solidifyPara));
				}
			}

			BillMutexRule billMutexRule = calSumParam.billMutexRule;
			// 取得休假、加班、出差、停工的相关时间段
			List<ITimeScopeWithBillType> leaveBillList = BillProcessHelper.filterBills(processedBills,
					BillMutexRule.BILL_LEAVE, billMutexRule);
			List<ITimeScopeWithBillType> awayBillList = BillProcessHelper.filterBills(processedBills,
					BillMutexRule.BILL_AWAY, billMutexRule);
			// 加班单的时间段处理稍显复杂，因为要满足南孚的公休不计加班时的加班单能录入的需求5.5
			List<ITimeScopeWithBillType> overtimeBillList = BillProcessHelper.filterOvertimeBills(processedBills,
					calSumParam.leaveCopyVOs,
					calSumParam.calendarMap == null ? null : calSumParam.calendarMap.get(pk_psndoc),
					calSumParam.aggShiftMap, dateTimeZoneMap, calSumParam.allDates, calSumParam.dateBeginIndex,
					calSumParam.dateEndIndex, billMutexRule, psnEnjoyHolidayScope.get(pk_psndoc));
			List<ITimeScopeWithBillType> shutdownBillList = BillProcessHelper.filterBills(processedBills,
					BillMutexRule.BILL_SHUTDOWN, billMutexRule);
			// 存储加班单归属天的map，key是加班单overtimeb的主键，value是日期。由于加班单和其他单据不一样，它是采用归的方式计算，即某个跨工作日的加班单的时长，算日报时，是
			// 算到其归属的工作日上的，而不是分摊到各天的；这样，算日报的时候，大致是这样的算法：
			// 第1天，找出所有加班单，循环处理这些加班单，找出这些加班单的归属日，如果归属日是今天的，就留下，并且算出时长，
			// 第2天，找出所有加班单，循环处理这些加班单，找出这些加班单的归属日，如果归属日是今天的，就留下，并且算出时长，
			// ................
			// 第n天，找出所有加班单，循环处理这些加班单，找出这些加班单的归属日，如果归属日是今天的，就留下，并且算出时长，
			// 可以看出，每天的日报计算都要做一件事情：循环找出所有加班单的归属日；而实际上第一天就已经算出所有加班单的归属日了，后面的天其实是在做重复的工作，并且
			// 寻找加班单的归属日是一个比较复杂的算法，重复做这件事情其实很浪费。因此，用一个map把这些加班单的归属日存起来，不用每天都重复算，是一件很有意义的事情
			Map<String, UFLiteralDate> overtimeBelongDateMap = new HashMap<String, UFLiteralDate>();// 可能存在找不到归属日的情况，这样的话，value（日期）是空，所以要用hashmap，不能用hashtable
			BillProcessHelper.findBelongtoDate(overtimeBills, overtimeBelongDateMap,
					calSumParam.calendarMap == null ? null : calSumParam.calendarMap.get(pk_psndoc),
					calSumParam.shiftMap, calSumParam.allDates, dateTimeZoneMap);

			// 如果日报项目中需要使用加班的开始时间和结束时间，需要创建加班单归属日临时表tbm_overtimebelong，并考虑开始和结束时间的时区问题。
			if (calSumParam.containsOvertimeBeginEndTimeVar) {
				IOvertimeManageService otservice = NCLocator.getInstance().lookup(IOvertimeManageService.class);
				otservice.createOvertimeBelongData(overtimeBills, overtimeBelongDateMap);
			}

			for (int dateIndex = calSumParam.dateBeginIndex; dateIndex <= calSumParam.dateEndIndex; dateIndex++) {
				// 当前天
				UFLiteralDate curDate = calSumParam.allDates[dateIndex];
				if (!TBMPsndocVO.isIntersect(psndocList, curDate.toString()))// 如果这天无考勤档案记录，则不用生成
					continue;
				// 此人此天的日报主键
				String pk_daystat = calSumParam.daystatPKMap.get(pk_psndoc) == null ? null : calSumParam.daystatPKMap
						.get(pk_psndoc).get(curDate.toString());
				// 当前天的班次以及前一天和后一天的班次
				AggPsnCalendar curCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, curDate);// 当前天的班次

				ShiftVO curShift = curCalendar == null ? null : curCalendar.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, curCalendar.getPsnCalendarVO()
								.getPk_shift());
				AggPsnCalendar preCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc,
						calSumParam.allDates[dateIndex - 1]);// 前一天的班次
				ShiftVO preShift = preCalendar == null ? null : preCalendar.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, preCalendar.getPsnCalendarVO()
								.getPk_shift());
				AggPsnCalendar nextCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc,
						calSumParam.allDates[dateIndex + 1]);// 后一天的班次
				ShiftVO nextShift = nextCalendar == null ? null : nextCalendar.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, nextCalendar.getPsnCalendarVO()
								.getPk_shift());
				TimeZone curTimeZone = CommonUtils.ensureTimeZone(calSumParam.timeRuleVO.getTimeZoneMap().get(
						dateOrgMap.get(curDate)));
				TimeZone preTimeZone = CommonUtils.ensureTimeZone(calSumParam.timeRuleVO.getTimeZoneMap().get(
						dateOrgMap.get(calSumParam.allDates[dateIndex - 1])));
				TimeZone nextTimeZone = CommonUtils.ensureTimeZone(calSumParam.timeRuleVO.getTimeZoneMap().get(
						dateOrgMap.get(calSumParam.allDates[dateIndex + 1])));
				// //当天的班次如果是弹性班，则需要先固化，才能用来计算单据时长
				// 2013-06-14固化提到单据过滤前面（加班单要用固化后的结果）
				// if(curCalendar!=null&&curCalendar.getPsnCalendarVO().isFlexibleFinal()){
				// //考勤时间段
				// ITimeScope kqScope = ShiftVO.toKqScope(curShift, preShift,
				// nextShift, curDate.toString(),
				// curTimeZone,preTimeZone,nextTimeZone);
				// //用来固化的参数
				// SolidifyPara solidifyPara =
				// calSumParam.toSolidifyPara(pk_psndoc, curDate, kqScope);
				// //进行固化
				// curCalendar.setPsnWorkTimeVO(SolidifyUtils.solidify(solidifyPara));
				// }
				// 对出差类别、休假类别、加班类别、停工分别进行处理
				// 对于出差类别，用自然日进行处理，即2008-07-24日的出差时长，是在2008-07-24日0点到23:59:59之间的时长
				// 对于休假类别和停工，用班次日进行处理，即2008-07-24日的休假（停工）时长，是休假（停工）单在2008-07-24日所排班次的工作时段内的时长。对于公休日，
				// 则要看公休是否计为休假，如果不计休假，则不处理。如果计为休假，则要处理。而定义班次时，公休是没有工作时间段的，
				// 休假在公休日的处理比较特殊：如果按小时计算，先算出实际的休假时长，如果大于8，则取为8，否则为实际时长。
				// 如果按天计算，如果是中间天
				// 则在中午12点前结束（包括12点）算半天，12点后开始（包括12点）算半天，其他情况算一天，但同一张单子一天内的时长不能超过一天
				// 如果是正常的工作日，则按小时计算很简单，和班次取交集就行了。如果是按天计算，就用如下规则：先取与工作时间段的交集时长，然后
				// 看休假折算标志（在休假类别里面定义）是按工作日折算还是按班次折算，如果按工作日折算，则除以工作日时长（在考勤规则里面定义），如果是按班次折算，则除以班次时长
				// 首先计算休假类别：
				BillProcessHelper.processLeaveLength(calSumParam.leaveCopyVOs, leaveBillList, curCalendar, preShift,
						curShift, nextShift, preTimeZone, curTimeZone, nextTimeZone, statbVOList,
						calSumParam.datePeriodMap, calSumParam.paramValues, pk_daystat, calSumParam.timeRuleVO);
				// 然后计算出差类别
				BillProcessHelper.processAwayLength(calSumParam.awayCopyVOs, awayBillList, curDate.toString(),
						curCalendar, preShift, curShift, nextShift, preTimeZone, curTimeZone, nextTimeZone,
						statbVOList, calSumParam.datePeriodMap, calSumParam.paramValues, pk_daystat,
						calSumParam.timeRuleVO);
				// 然后计算加班类别
				BillProcessHelper.processOvertiemLength(calSumParam.overCopyVOs, overtimeBillList, curDate.toString(),
						overtimeBelongDateMap, statbVOList, pk_daystat, calSumParam.timeRuleVO);
				// 然后计算停工
				BillProcessHelper.processShutdownLength(calSumParam.shutCopyVOs, shutdownBillList, curCalendar,
						statbVOList, pk_daystat, calSumParam.timeRuleVO);
			}
		}
		// 将计算的子表结果写入数据库。已有的子表数据在最开始已经删除了，不用在这里删
		if (statbVOList.size() > 0) {
			String pk_org = calSumParam.pk_org;
			String pk_group = calSumParam.timeRuleVO.getPk_group();
			for (DayStatbVO vo : statbVOList) {
				vo.setPk_org(pk_org);
				vo.setPk_group(pk_group);
			}
			new BaseDAO().insertVOList(statbVOList);
		}

		/* 加班总时长和加班转调休总时长 中间表tbm_daystat_cacu数据生成 yejk #21266 start */
		String pk_org = calSumParam.pk_org;
		String cuserid = InvocationInfoProxy.getInstance().getUserId();
		// 普通、公休、节日 加班 pk_timeitem
		String[] pk_timeitems = { "1002Z710000000021ZLT", "1002Z710000000021ZLV", "1002Z710000000021ZLX" };
		String[] pk_psndocs = calSumParam.pk_psndocs;
		BaseDAO dao = new BaseDAO();
		StringBuffer sqlInsert = new StringBuffer(
				"insert into tbm_daystat_cacu(pk_daystat_cacu,pk_org,cuserid,pk_psndoc,pk_timeitem,hourcount,calendar,calType) values ");

		ISegDetailService segDetailService = NCLocator.getInstance().lookup(ISegDetailService.class);

		try {
			// OidGenerator.getInstance().nextOid() NC 20位主键
			// 删除当前用户用于计算的缓存数据
			// dao.executeUpdate("delete from tbm_daystat_cacu where cuserid = '"
			// + cuserid + "'");
			dao.executeUpdate("delete from tbm_daystat_cacu where pk_org = '" + pk_org + "' and pk_psndoc in("
					+ calSumParam.psndocInSQL + ")");
			for (int dateIndex = calSumParam.dateBeginIndex; dateIndex <= calSumParam.dateEndIndex; dateIndex++) {
				// 当前天
				UFLiteralDate curDate = calSumParam.allDates[dateIndex];
				for (int i = 0; i < pk_timeitems.length; i++) {
					// 调用接口获取加班数据 返回MAP的key集合为 传入的pk_psndocs数组
					Map<String, UFDouble> ovtData = segDetailService.getOvertimeHoursByType(pk_org, pk_psndocs, null,
							null, curDate, pk_timeitems[i]);
					// 调用接口获取加班转调休数据 返回MAP的key集合为 传入的pk_psndocs数组
					Map<String, UFDouble> ovtToRestData = segDetailService.getOvertimeToRestHoursByType(pk_org,
							pk_psndocs, null, null, curDate, pk_timeitems[i]);
					/*
					 * 如果ovtData和ovtToRestData中的key集合相同 可共用下面for循环处理数据，
					 * 因ISegDetailService具体实现细节暂未知，先假定相同 后续有变再处理 yejk 180904
					 */
					for (String key : ovtData.keySet()) {
						String pk_psndoc = key;
						// 向中间表生成加班数据
						UFDouble overTime = ovtData.get(key);
						String valuesSql = "('" + OidGenerator.getInstance().nextOid() + "'" + "," + "'" + pk_org
								+ "'," + "'" + cuserid + "'" + "," + "'" + pk_psndoc + "'" + "," + "'"
								+ pk_timeitems[i] + "'" + "," + overTime.getDouble() + "," + "'" + curDate.toString()
								+ "','0')";// 0为加班数据
						dao.executeUpdate(sqlInsert.toString() + valuesSql);
						// 向中间表生成加班转调休数据
						overTime = ovtToRestData.get(key);
						valuesSql = "('" + OidGenerator.getInstance().nextOid() + "'" + "," + "'" + pk_org + "'," + "'"
								+ cuserid + "'" + "," + "'" + pk_psndoc + "'" + "," + "'" + pk_timeitems[i] + "'" + ","
								+ overTime.getDouble() + "," + "'" + curDate.toString() + "','1')";// 0为加班转调休数据
						dao.executeUpdate(sqlInsert.toString() + valuesSql);
					}
				}
			}
		} catch (DAOException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		/* 加班总时长和加班转调休总时长 中间表tbm_daystat_cacu数据生成 yejk #21266 end */
	}

	/**
	 * 处理日报项目的计算，包括公式、java自定义
	 * 
	 * @param calSumParam
	 * @throws DbException
	 * @throws BusinessException
	 */
	private void processItems(CalSumParam calSumParam) throws DbException, BusinessException {
		if (calSumParam.containsPreviousDayFunc || calSumParam.containsDatePeriodVar) {
			processItemsDayAfterDay(calSumParam);
			return;
		}
		processItemsAllDaysOnce(calSumParam);
	}

	/**
	 * 一天一天地计算
	 * 
	 * @param calSumParam
	 * @throws DbException
	 * @throws BusinessException
	 */
	private void processItemsDayAfterDay(CalSumParam calSumParam) throws DbException, BusinessException {
		String pk_org = calSumParam.pk_org;
		String psndocInSQL = calSumParam.psndocInSQL;
		BaseDAO dao = new BaseDAO();
		SQLParameter para = new SQLParameter();
		ItemCopyVO[] dayItemVOs = calSumParam.dayItemVOs;
		JdbcSession session = null;
		try {
			// 按天循环
			for (int i = calSumParam.dateBeginIndex; i <= calSumParam.dateEndIndex; i++) {
				UFLiteralDate date = calSumParam.allDates[i];
				// 按日报项目循环
				for (ItemCopyVO itemVO : dayItemVOs) {
					if (itemVO.getSrc_flag() == ItemVO.SRC_FLAG_MANUAL)// 手工录入
						continue;
					if (itemVO.getSrc_flag() == ItemVO.SRC_FLAG_FORMULA) {// 公式计算
						para.clearParams();
						para.addParam(pk_org);
						para.addParam(date.toString());
						String updateSql = itemVO.getParsedFormula() + " where " + IBaseServiceConst.PK_ORG + "=?"
								+ " and " + DayStatVO.PK_PSNDOC + " in (" + psndocInSQL + ") and " + DayStatVO.CALENDAR
								+ "=?";
						try {
							dao.executeUpdate(updateSql, para);
						} catch (Exception e) {
							Logger.error(e.getMessage(), e);
							throw new BusinessException(ResHelper.getString("6017hrta", "06017hrta0080")
							/* @res "日报项目：" */+ itemVO.getMultilangName()
									+ ResHelper.getString("6017hrta", "06017hrta0081")
									/* @res "计算错误,请检查公式设置是否正确。 详情：" */+ e.getMessage());

						}
						continue;
					}
					// java类计算
					IDayDataCreator creator = calSumParam.dataCreatorMap.get(itemVO.getItem_code());
					if (creator == null)
						continue;
					// 按人员循环
					String[] pk_psndocs = calSumParam.pk_psndocs;
					String updateSql = MessageFormat.format(getUpdateJavaItemSQL(), itemVO.getItem_db_code());
					if (session == null)
						session = new JdbcSession();
					for (String pk_psndoc : pk_psndocs) {
						processJavaItemForOnePersonOneDay(calSumParam, pk_psndoc, calSumParam.allDates[i - 1], date,
								calSumParam.allDates[i + 1], session, para, itemVO, creator, updateSql);
					}
				}
			}
			if (session != null)
				session.executeBatch();
		} finally {
			if (session != null)
				session.closeAll();
		}
	}

	/**
	 * 一次计算所有天
	 * 
	 * @param calSumParam
	 * @throws DbException
	 * @throws BusinessException
	 */
	private void processItemsAllDaysOnce(CalSumParam calSumParam) throws DbException, BusinessException {
		String psndocInSQL = calSumParam.psndocInSQL;
		BaseDAO dao = new BaseDAO();
		SQLParameter para = new SQLParameter();
		UFLiteralDate beginDate = calSumParam.allDates[calSumParam.dateBeginIndex];
		UFLiteralDate endDate = calSumParam.allDates[calSumParam.dateEndIndex];
		para.addParam(calSumParam.pk_org);
		para.addParam(beginDate.toString());
		para.addParam(endDate.toString());

		ItemCopyVO[] dayItemVOs = calSumParam.dayItemVOs;
		JdbcSession session = null;
		SQLParameter javaItemUpdatePara = new SQLParameter();
		try {
			// 按日报项目循环
			for (ItemCopyVO itemVO : dayItemVOs) {
				if (itemVO.getSrc_flag() == ItemVO.SRC_FLAG_MANUAL)// 手工录入
					continue;
				if (itemVO.getSrc_flag() == ItemVO.SRC_FLAG_FORMULA) {// 公式计算
					String updateSql = itemVO.getParsedFormula() + " where " + IBaseServiceConst.PK_ORG + "=?"
							+ " and " + DayStatVO.PK_PSNDOC + " in (" + psndocInSQL + ") and " + DayStatVO.CALENDAR
							+ " between ? and ?";
					try {
						dao.executeUpdate(updateSql, para);
					} catch (Exception e) {
						Logger.error(e.getMessage());
						throw new BusinessException(itemVO.getMultilangName()
								+ ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0098")
								/* @res "计算时出错，错误如下：" */+ e.getMessage());
					}
					continue;
				}
				// java类计算
				IDayDataCreator creator = calSumParam.dataCreatorMap.get(itemVO.getItem_code());
				if (creator == null)
					continue;
				if (session == null)
					session = new JdbcSession();
				// 按人员循环
				String[] pk_psndocs = calSumParam.pk_psndocs;
				String updateSql = MessageFormat.format(getUpdateJavaItemSQL(), itemVO.getItem_db_code());
				for (String pk_psndoc : pk_psndocs) {
					for (int i = calSumParam.dateBeginIndex; i <= calSumParam.dateEndIndex; i++) {
						UFLiteralDate date = calSumParam.allDates[i];
						processJavaItemForOnePersonOneDay(calSumParam, pk_psndoc, calSumParam.allDates[i - 1], date,
								calSumParam.allDates[i + 1], session, javaItemUpdatePara, itemVO, creator, updateSql);
					}
				}
			}
			if (session != null)
				session.executeBatch();
		} finally {
			if (session != null)
				session.closeAll();
		}
	}

	private void processJavaItemForOnePersonOneDay(CalSumParam calSumParam, String pk_psndoc, UFLiteralDate preDate,
			UFLiteralDate date, UFLiteralDate nextDate, JdbcSession session, SQLParameter para, ItemCopyVO itemVO,
			IDayDataCreator creator, String updateSQL) throws DbException, BusinessException {
		processDayCalParamByPsndocDate(calSumParam, pk_psndoc, preDate, date, nextDate);
		DayCalParam param = calSumParam.dayCalParam;
		param.itemCode = itemVO.getItem_code();
		creator.process(param);
		// 将计算结果持久化
		para.clearParams();
		para.addParam(calSumParam.pk_org);
		para.addParam(pk_psndoc);
		para.addParam(date.toString());
		session.addBatch(updateSQL, para);// 加入批处理
	}

	/**
	 * 从CalSumParam中取出某人某天计算日报需要的各项参数，放入DayCalParam中
	 * 
	 * @param param
	 * @param calSumParam
	 * @param pk_psndoc
	 * @param date
	 * @throws BusinessException
	 */
	private void processDayCalParamByPsndocDate(CalSumParam calSumParam, String pk_psndoc, UFLiteralDate preDate,
			UFLiteralDate date, UFLiteralDate nextDate) throws BusinessException {
		DayCalParam param = calSumParam.dayCalParam;
		param.date = date;
		// 考勤档案
		param.psndocVO = TBMPsndocVO.findIntersectionVO(calSumParam.tbmPsndocMap.get(pk_psndoc), date.toString());
		// timedata
		Map<UFLiteralDate, TimeDataVO> timeDataMap = calSumParam.timedataMap.get(pk_psndoc);
		if (!MapUtils.isEmpty(timeDataMap))
			param.timeDataVO = timeDataMap.get(date);
		// 手工考勤
		Map<UFLiteralDate, LateEarlyVO> lateEarlyMap = calSumParam.lateearlyMap.get(pk_psndoc);
		if (!MapUtils.isEmpty(lateEarlyMap)) {
			param.lateearlyVO = lateEarlyMap.get(date);
		}
		Map<UFLiteralDate, String> dateOrgMap = calSumParam.allDateOrgMap.get(pk_psndoc);
		// 前一天，当天，后一天的班次以及工作日历
		AggPsnCalendar curCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, date);// 当前天的班次
		ShiftVO curShift = curCalendar == null ? null : curCalendar.getPsnCalendarVO() == null ? null
				: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, curCalendar.getPsnCalendarVO()
						.getPk_shift());
		AggPsnCalendar preCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, preDate);// 前一天的班次
		ShiftVO preShift = preCalendar == null ? null : preCalendar.getPsnCalendarVO() == null ? null
				: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, preCalendar.getPsnCalendarVO()
						.getPk_shift());
		AggPsnCalendar nextCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, nextDate);// 后一天的班次
		ShiftVO nextShift = nextCalendar == null ? null : nextCalendar.getPsnCalendarVO() == null ? null
				: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, nextCalendar.getPsnCalendarVO()
						.getPk_shift());
		TimeZone curTimeZone = CommonUtils.ensureTimeZone(param.timeruleVO.getTimeZoneMap().get(dateOrgMap.get(date)));
		TimeZone preTimeZone = CommonUtils.ensureTimeZone(param.timeruleVO.getTimeZoneMap().get(preDate));
		TimeZone nextTimeZone = CommonUtils.ensureTimeZone(param.timeruleVO.getTimeZoneMap().get(
				dateOrgMap.get(nextDate)));
		param.curCalendarVO = curCalendar;
		param.curShiftVO = curShift;
		param.curTimeZone = curTimeZone;

		param.preCalendarVO = preCalendar;
		param.preShiftVO = preShift;
		param.preTimeZone = preTimeZone;

		param.nextCalendarVO = nextCalendar;
		param.nextShiftVO = nextShift;
		param.nextTimeZone = nextTimeZone;
		ITimeScope kqScope = ShiftVO.toKqScope(curShift, preShift, nextShift, date.toString(), curTimeZone,
				preTimeZone, nextTimeZone);
		// 休假单等信息
		param.awayBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.awayMap, kqScope);
		param.leaveBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.leaveMap, kqScope);
		param.overtimeBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.overMap, kqScope);
		param.shutdownBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.shutMap, kqScope);
		param.mergeTimeScopes = TimeScopeUtils.mergeTimeScopes(
				TimeScopeUtils.mergeTimeScopes(param.awayBills, param.leaveBills),
				TimeScopeUtils.mergeTimeScopes(param.overtimeBills, param.shutdownBills));
		param.lactationholidayVO = DataFilterUtils.filterDateScopeVO(pk_psndoc, calSumParam.lactationMap,
				date.toString());
		// 刷签卡记录
		if (MapUtils.isNotEmpty(calSumParam.checkTimesMap)) {
			ICheckTime[] checkTimes = calSumParam.checkTimesMap.get(pk_psndoc);// 此
			param.checkTimes = DataFilterUtils.filterCheckTimes(kqScope, checkTimes);
			// 自然日内刷卡记录
			ITimeScope natualScope = TimeScopeUtils.toFull3Day(date.toString(), param.curTimeZone);
			param.naturalCheckTimes = DataFilterUtils.filterCheckTimes(natualScope, checkTimes);
		}

	}

	private static String getUpdateJavaItemSQL() {
		return "update tbm_daystat set {0}=? where " + DayStatVO.PK_ORG + "=? and " + DayStatVO.PK_PSNDOC + "=? and "
				+ DayStatVO.CALENDAR + "=?";
	}

	/**
	 * 从map中取出某人某天的班次
	 * 
	 * @param calendarMap
	 * @param pk_psndoc
	 * @param date
	 * @return
	 */
	private AggPsnCalendar getAggPsnCalendarVO(Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap,
			String pk_psndoc, UFLiteralDate date) {
		if (MapUtils.isEmpty(calendarMap))
			return null;
		Map<UFLiteralDate, AggPsnCalendar> map = calendarMap.get(pk_psndoc);
		if (MapUtils.isEmpty(map))
			return null;
		Map<String, AggPsnCalendar> stringDateMap = new HashMap<String, AggPsnCalendar>();
		for (UFLiteralDate key : map.keySet()) {
			stringDateMap.put(key.toString(), map.get(key));
		}
		return stringDateMap.get(date.toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public DayStatVO[] save(String pk_org, DayStatVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;

		// 封存期间的日报不允许修改
		List<UFLiteralDate> modefiedDate = new ArrayList<UFLiteralDate>();
		for (DayStatVO vo : vos) {
			modefiedDate.add(vo.getCalendar());
		}
		if (!CollectionUtils.isEmpty(modefiedDate)) {
			UFLiteralDate[] dates = modefiedDate.toArray(new UFLiteralDate[0]);
			NCLocator.getInstance().lookup(IPeriodQueryService.class).checkDateB4Modify(pk_org, dates);
		}

		try {

			UFLiteralDate[] dates = new UFLiteralDate[vos.length];
			for (int i = 0; i < vos.length; i++) {
				dates[i] = vos[i].getDate();
			}
			Arrays.sort(dates);
			try {
				NCLocator.getInstance().lookup(IPeriodQueryService.class)
						.checkDateScope(pk_org, dates[0], dates[dates.length - 1]);
			} catch (Exception e) {
				throw new BusinessException(ResHelper.getString("6017hrta", "06017hrta0082")
				/* @res "不能修改已封存期间的数据！" */);
			}

			DayStatVO[] oldvos = null;
			InSQLCreator isc = new InSQLCreator();
			String condition = DayStatVO.PK_DAYSTAT + " in (" + isc.getInSQL(vos, DayStatVO.PK_DAYSTAT) + ") ";
			Collection oldc = new BaseDAO().retrieveByClause(DayStatVO.class, condition);
			if (CollectionUtils.isNotEmpty(oldc))
				oldvos = (DayStatVO[]) oldc.toArray(new DayStatVO[0]);
			new DayStatDAO().save(pk_org, vos);
			EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.DAYSTAT, IEventType.TYPE_INSERT_AFTER, vos));
			// 业务日志
			TaBusilogUtil.writeDayStatEditBusiLog(vos, oldvos);
			return vos;
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	/*
	 * 部门日报的查询规则是： 搜先按照人员条件（确实如此，不是部门条件）查询出所有人员的日报，然后按人员所属部门分组，分组完了之后sum
	 * 注意，用来分组的部门首先要在deptPKs范围内。如果人员的明细数据中，某个部门某天一条明细都没有，那么此部门此天在返回的数组中无数据
	 * (non-Javadoc)
	 * 
	 * @see
	 * nc.itf.ta.IDayStatQueryMaintain#querryDeptDayStatByCondition(nc.vo.uif2
	 * .LoginContext, java.lang.String[],
	 * nc.ui.querytemplate.querytree.FromWhereSQL, nc.vo.pub.lang.UFLiteralDate,
	 * nc.vo.pub.lang.UFLiteralDate, boolean)
	 */
	@Override
	public DeptDayStatVO[] queryDeptDayStatByCondition(LoginContext context, String[] deptPKs,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, boolean showNoDataRecord)
			throws BusinessException {
		// 将部门条件加到客户端的条件中去
		if (!ArrayUtils.isEmpty(deptPKs))
			fromWhereSQL = TBMPsndocSqlPiecer.addDeptPkArrayQuerySQL(deptPKs, fromWhereSQL);
		// return null;
		// 增加权限
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		// 按天查询每天的人员日报，然后按部门汇总
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		DeptDayStatVO[] retArray = null;

		// 查询本组织所有的日/月报类型，包含已停用的,先查出来作为下一步查询的参数，方式每天查的时候都查一遍影响效率
		int itemClass = Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		ItemVO[] allItemVOs = NCLocator.getInstance().lookup(IItemQueryService.class)
				.queryItemByOrg(context.getPk_org(), itemClass, true);

		for (UFLiteralDate date : allDates) {
			retArray = (DeptDayStatVO[]) ArrayUtils.addAll(retArray,
					queryDeptStatVOByCondition(context, fromWhereSQL, date, showNoDataRecord, allItemVOs));
		}
		return retArray;
	}

	@Override
	public DayStatVO[] queryByCondition(LoginContext context, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean showNoDataRecord) throws BusinessException {
		return queryByCondition(context.getPk_org(), fromWhereSQL, beginDate, endDate, showNoDataRecord);
	}

	protected DayStatVO[] queryByCondition(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean showNoDataRecord) throws BusinessException {
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		DayStatVO[] retArray = null;

		// 查询本组织所有的日/月报类型，包含已停用的,先查出来作为下一步查询的参数，方式每天查的时候都查一遍影响效率
		int itemClass = Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		ItemVO[] allItemVOs = NCLocator.getInstance().lookup(IItemQueryService.class)
				.queryItemByOrg(pk_org, itemClass, true);

		// 每一天的人都有可能不一样，因此要一天一天地查询
		for (UFLiteralDate date : allDates) {
			retArray = (DayStatVO[]) ArrayUtils.addAll(retArray,
					queryByCondition(pk_org, fromWhereSQL, date, showNoDataRecord, allItemVOs));
		}
		return retArray;
	}

	/**
	 * 根据条件，查询一天的日报数据
	 * 
	 * @param context
	 * @param fromWhereSQL
	 * @param date
	 * @param showNoDataRecord
	 * @return
	 * @throws BusinessException
	 */
	protected DayStatVO[] queryByCondition(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate date,
			boolean showNoDataRecord, boolean isforDeptQuery, ItemVO[] allItemVOs) throws BusinessException {
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		// 首先查询出这一天考勤档案在本组织的人员
		TBMPsndocVO[] psndocVOsInOrg = psndocService
				.queryByCondition(pk_org, fromWhereSQL, date, false, isforDeptQuery);
		// 然后查询这一天考勤档案不在本组织，但是管理组织（在考勤档案节点设置，类似于委托）在本组织的人员
		TBMPsndocVO[] psndocVOsInAdminOrg = psndocService.queryByCondition2(pk_org, fromWhereSQL, date, false,
				isforDeptQuery);
		if (ArrayUtils.isEmpty(psndocVOsInOrg) && ArrayUtils.isEmpty(psndocVOsInAdminOrg))
			return null;
		// 查询这些人员已有的日报记录
		InSQLCreator isc = new InSQLCreator();
		try {
			SQLParameter para = new SQLParameter();
			para.addParam(date.toString());
			para.addParam(pk_org);
			DayStatVO[] dbVOsInOrg = null;
			DayStatVO[] dbVOsInAdminOrg = null;
			if (!ArrayUtils.isEmpty(psndocVOsInOrg)) {
				String cond = DayStatVO.PK_PSNDOC + " in(" + isc.getInSQL(psndocVOsInOrg, DayStatVO.PK_PSNDOC)
						+ ") and " + DayStatVO.CALENDAR + "=? and " + DayStatVO.PK_ORG + "=? ";
				dbVOsInOrg = new DayStatDAO().query2(pk_org, DayStatVO.class, new String[] { DayStatVO.PK_PSNDOC,
						DayStatVO.CALENDAR, DayStatVO.PK_GROUP, DayStatVO.PK_ORG }, cond, null, para, allItemVOs);
			}
			if (!ArrayUtils.isEmpty(psndocVOsInAdminOrg)) {
				String cond = DayStatVO.PK_PSNDOC + " in(" + isc.getInSQL(psndocVOsInAdminOrg, DayStatVO.PK_PSNDOC)
						+ ") and " + DayStatVO.CALENDAR + "=? and " + DayStatVO.PK_ORG + "<>? ";
				dbVOsInAdminOrg = new DayStatDAO().query2(pk_org, DayStatVO.class, new String[] { DayStatVO.PK_PSNDOC,
						DayStatVO.CALENDAR, DayStatVO.PK_GROUP, DayStatVO.PK_ORG }, cond, null, para, allItemVOs);
			}
			// 本组织的日报数据
			DayStatVO[] retVOsInOrg = processDBVOs(psndocVOsInOrg, dbVOsInOrg, date, showNoDataRecord, isforDeptQuery);
			// 管理组织在本组织的日报数据
			DayStatVO[] retVOsInAdminOrg = processDBVOs(psndocVOsInAdminOrg, dbVOsInAdminOrg, date, showNoDataRecord,
					isforDeptQuery);
			return (DayStatVO[]) ArrayUtils.addAll(retVOsInOrg, retVOsInAdminOrg);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
		// finally{
		// isc.clear();
		// }
	}

	protected DayStatVO[] queryByCondition(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate date,
			boolean showNoDataRecord, ItemVO[] allItemVOs) throws BusinessException {
		return queryByCondition(pk_org, fromWhereSQL, date, showNoDataRecord, false, allItemVOs);
	}

	protected DeptDayStatVO[] queryDeptStatVOByCondition(LoginContext context, FromWhereSQL fromWhereSQL,
			UFLiteralDate date, boolean showNoDataRecord, ItemVO[] allItemVOs) throws BusinessException {

		DayStatVO[] psnStatVOs = queryByCondition(context.getPk_org(), fromWhereSQL, date, showNoDataRecord, true,
				allItemVOs);
		if (ArrayUtils.isEmpty(psnStatVOs))
			return null;
		// 将人员数据按部门分组，并且顺序不能改变。即，返回的DeptDayStatVO[]中vo的顺序，要与psnStatVOs中部门的顺序一致
		List<DeptDayStatVO> retList = new ArrayList<DeptDayStatVO>();// list的作用是保证部门vo的顺序
		Map<String, DeptDayStatVO> statMap = new HashMap<String, DeptDayStatVO>();// map的作用是取vo方便
		for (DayStatVO psnVO : psnStatVOs) {
			String pk_dept = psnVO.getPk_dept();
			DeptDayStatVO deptVO = statMap.get(pk_dept);
			if (deptVO == null) {
				deptVO = new DeptDayStatVO();
				deptVO.setPk_dept(pk_dept);
				deptVO.setCalendar(date);

				// 设置版本信息
				deptVO.setPk_dept_v(psnVO.getPk_dept_v());
				deptVO.setPk_org_v(psnVO.getPk_org_v());

				statMap.put(pk_dept, deptVO);
				retList.add(deptVO);
			}
			deptVO.mergePsnDayStatVO(psnVO);
		}
		return retList.toArray(new DeptDayStatVO[0]);
	}

	/**
	 * 将dbVOs数组处理后返回。dbVOs数组是数据库中已有的数据
	 * 如果showNoDataRecord为true，则保证一人一条记录，不管数据库中是否有（没有就new一个daystatvo）
	 * 如果showNoDataRecord为false，则要将dbVOs中所有字段为空的记录去除掉
	 * 
	 * @param psndocVOs
	 * @param aggVOs
	 * @param date
	 * @param showNoDataRecord
	 * @return
	 */
	protected DayStatVO[] processDBVOs(TBMPsndocVO[] psndocVOs, DayStatVO[] dbVOs, UFLiteralDate date,
			boolean showNoDataRecord, boolean isforDeptQuery) throws BusinessException {
		if (ArrayUtils.isEmpty(dbVOs) && !showNoDataRecord)
			return null;
		if (ArrayUtils.isEmpty(psndocVOs))
			return null;
		// dbVO的map，key是pk_psndoc
		Map<String, DayStatVO> dbVOMap = CommonUtils.toMap(DayStatVO.PK_PSNDOC, dbVOs);
		if (dbVOMap == null)
			dbVOMap = new HashMap<String, DayStatVO>();
		List<DayStatVO> retList = new ArrayList<DayStatVO>();
		// 如果不含空记录
		if (!showNoDataRecord) {
			for (int i = 0; i < psndocVOs.length; i++) {
				TBMPsndocVO psndocVO = psndocVOs[i];
				String pk_psndoc = psndocVO.getPk_psndoc();
				DayStatVO dbVO = dbVOMap.get(pk_psndoc);
				if (dbVO == null)
					continue;
				if ((!dbVO.isNoDataRecord() && !isforDeptQuery) || (!dbVO.isNoDecimalDataRecord() && isforDeptQuery)) {
					retList.add(dbVO);
					// 设置任职主键(日报表中并没有存储，元数据上有)
					dbVO.setPk_psnjob(psndocVO.getPk_psnjob());
					dbVO.setPk_tbm_psndoc(psndocVO.getPrimaryKey());
					if (isforDeptQuery)
						dbVO.setPk_dept(psndocVO.getPk_dept());

					// 设置版本信息
					dbVO.setPk_org_v(psndocVO.getPk_org_v());
					dbVO.setPk_dept_v(psndocVO.getPk_dept_v());
				}
			}
			return retList.size() == 0 ? null : retList.toArray(new DayStatVO[0]);
		}
		// 如果含空记录，则要求每人都要有vo，没有的话，需要new一个
		DayStatVO[] retArray = new DayStatVO[psndocVOs.length];
		for (int i = 0; i < psndocVOs.length; i++) {
			TBMPsndocVO psndocVO = psndocVOs[i];
			DayStatVO daystatVO = dbVOMap.get(psndocVO.getPk_psndoc());
			if (daystatVO != null) {
				retArray[i] = daystatVO;
				daystatVO.setPk_psnjob(psndocVO.getPk_psnjob());
				daystatVO.setPk_tbm_psndoc(psndocVO.getPrimaryKey());

				// 设置版本信息
				daystatVO.setPk_org_v(psndocVO.getPk_org_v());
				daystatVO.setPk_dept_v(psndocVO.getPk_dept_v());

				if (isforDeptQuery)
					daystatVO.setPk_dept(psndocVO.getPk_dept());
				continue;
			}
			// 如果没有则new一个
			daystatVO = new DayStatVO();
			retArray[i] = daystatVO;
			daystatVO.setCalendar(date);
			daystatVO.setPk_psndoc(psndocVO.getPk_psndoc());
			daystatVO.setPk_psnjob(psndocVO.getPk_psnjob());
			daystatVO.setPk_group(psndocVO.getPk_group());
			daystatVO.setPk_org(psndocVO.getPk_org());

			// 设置版本信息
			daystatVO.setPk_org_v(psndocVO.getPk_org_v());
			daystatVO.setPk_dept_v(psndocVO.getPk_dept_v());
			if (isforDeptQuery)
				daystatVO.setPk_dept(psndocVO.getPk_dept());
		}
		return retArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TBMPsndocVO[] queryUnGenerateByCondition(LoginContext context, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		// 添加维护权限，未生成统计改为维护权限
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		// fromWhereSQL =
		// TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc",
		// IActionCode.EDIT, fromWhereSQL);
		fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, endDate.toStdString());
		String orgversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_org_v"
				+ FromWhereSQLUtils.getAttPathPostFix());
		String deptversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_dept_v"
				+ FromWhereSQLUtils.getAttPathPostFix());
		String[] otherTableSelFields = new String[] {
				orgversionAlias + "." + AdminOrgVersionVO.PK_VID + " as " + TBMPsndocVO.PK_ORG_V,
				deptversionAlias + "." + DeptVersionVO.PK_VID + " as " + TBMPsndocVO.PK_DEPT_V };
		// 查出日期范围内日报不完整的人员
		SQLParamWrapper wrapper = TBMPsndocSqlPiecer.selectUnCompleteDailyDataByPsndocFieldAndDateFieldAndDateArea(
				context.getPk_org(), new String[] { TBMPsndocVO.PK_PSNDOC, TBMPsndocVO.PK_PSNJOB, TBMPsndocVO.PK_ORG },
				otherTableSelFields, "tbm_daystat daystat", "daystat.pk_org", "daystat.pk_psndoc", "daystat.calendar",
				beginDate.toString(), endDate.toString(), null, "daystat.dirty_flag='N'", fromWhereSQL);
		String sql = wrapper.getSql();
		SQLParameter para = wrapper.getParam();
		TBMPsndocVO[] retvos = CommonUtils.toArray(TBMPsndocVO.class,
				(List<TBMPsndocVO>) new BaseDAO().executeQuery(sql, para, new BeanListProcessor(TBMPsndocVO.class)));
		// 业务日志
		TaBusilogUtil.writeDayStatUngenBusiLog(retvos, beginDate, endDate);
		return retvos;
	}

	/**
	 * 将日期对应的期间及下一个期间查出来，放到map中。map的key是日期，
	 * value是string数组，第一个元素是日期所属期间，第二个元素是日期所属期间的下一个期间
	 * 构造这个map主要是为了处理休假和出差的参数：单据跨期间时，是记在各个期间还是第一个期间记在第二个期间
	 * 
	 * @param pkCorp
	 * @param dates
	 *            ，所有的日期，要求按时间先后顺序排列
	 * @param periodMap
	 * @throws SQLException
	 * @throws NamingException
	 */
	private void processPeriodAndNextPeriodOfDate(String pk_org, UFLiteralDate[] dates, Map<String, String[]> periodMap)
			throws BusinessException {
		// PeriodVO[] periods =service.queryByDate(pk_org, dates[0],
		// dates[dates.length-1]);
		PeriodVO[] periods = PeriodServiceFacade.queryPeriodsByDateScope(pk_org, dates[0], dates[dates.length - 1]);
		if (ArrayUtils.isEmpty(periods)) {
			return;
		}
		// 存储一个期间的下一期间的map,key是201101这种，value是201102这种
		// 最终得到的map类型是{2011-01-01=[201101,201102]}或者是{2011-01-01=null}
		for (UFLiteralDate date : dates) {
			for (int i = 0; i < periods.length; i++) {
				if ((date.before(periods[i].getEnddate()) || date.equals(periods[i].getEnddate()))// 小于或者等于期间结束日期
						&& (date.after(periods[i].getBegindate()) || date.equals(periods[i].getBegindate()))) {// 大于或者等于期间开始日期
					String period = periods[i].getTimeyear() + periods[i].getTimemonth();
					if (i < periods.length - 1) {
						String nextPeriod = periods[i + 1].getTimeyear() + periods[i + 1].getTimemonth();
						periodMap.put(date.toString(), new String[] { period, nextPeriod });
					} else {
						periodMap.put(date.toString(), new String[] { period, null });
					}
					break;
				} else {
					periodMap.put(date.toString(), null);
				}
			}
			// if(periodMap.containsKey(date)){
			// continue;
			// }
		}

		// PeriodVO firstPeriodVO = service.queryByDate(pk_org, dates[0]);
		// PeriodVO lastPeriodVO = service.queryByDate(pk_org,
		// dates[dates.length-1]);
		// Map<String, PeriodVO> periodVOMap = new HashMap<String,
		// PeriodVO>();// key是200808这种期间，value是vo
		// if (firstPeriodVO != null) {
		// periodVOMap.put(firstPeriodVO.getTimeyear() +
		// firstPeriodVO.getTimemonth(), firstPeriodVO);
		// }
		// if (lastPeriodVO != null) {
		// periodVOMap.put(lastPeriodVO.getTimeyear() +
		// lastPeriodVO.getTimemonth(), lastPeriodVO);
		// }
		// // 存储一个期间的下一期间的map,key是201101这种，value是201102这种
		// Map<String, String> nextPeriodMap = new HashMap<String, String>();
		// for (UFLiteralDate date : dates) {
		// String period = null;
		// Iterator<PeriodVO> periodIterator = periodVOMap.values().iterator();
		// while (periodIterator.hasNext()) {
		// PeriodVO periodVO = periodIterator.next();
		// if (DateScopeUtils.contains(periodVO, date)) {
		// period = periodVO.getTimeyear() + periodVO.getTimemonth();
		// }
		// }
		// if (period == null) {
		// PeriodVO periodVO = service.queryByDate(pk_org, date);
		// if (periodVO != null) {
		// period = periodVO.getTimeyear() + periodVO.getTimemonth();
		// periodVOMap.put(period, periodVO);
		// }
		// }
		// if (period == null) {
		// periodMap.put(date.toString(), null);
		// continue;
		// }
		// String nextPeriod = null;
		// if (nextPeriodMap.containsKey(period)) {
		// nextPeriod = nextPeriodMap.get(period);
		// } else {
		// PeriodVO nextPeriodVO = service.queryNextPeriod(pk_org,
		// period.substring(0, 4), period.substring(4, 6));
		// if(nextPeriodVO!=null)
		// nextPeriodMap.put(period,
		// nextPeriodVO.getTimeyear()+nextPeriodVO.getTimemonth());
		// }
		// periodMap.put(date.toString(), new String[] { period, nextPeriod });
		// }
	}

	/**
	 * 记录所有汇总参数的类，即所有的类别、项目、所有人的单据等等
	 * 
	 * @author zengcheng
	 * 
	 */
	private static class CalSumParam {

		SolidifyPara solidifyPara = new SolidifyPara();
		DayCalParam dayCalParam;
		String pk_org;
		TimeRuleVO timeRuleVO;
		String[] pk_psndocs;
		String psndocInSQL;// 人员主键的in
		// sql。注意，psndocInSQL和pk_psndocs实际上是等同的，只不过，如果只传pk_psndocs，不存psndocInSQL的话，很多地方都要根据pk_psndocs再造临时表，比较浪费
		UFLiteralDate[] allDates;
		int dateBeginIndex;
		int dateEndIndex;
		boolean containsPreviousDayFunc;// 日报项目的公式中，是否使用了前N天日报的函数。如果使用了，则日报需要一天一天地算。如果没有使用，则可以一次算完
		boolean containsHolidayFunc;// 日报项目中，是否有项目要查询假日享有的明细，如果有的话，需要提前将明细准备到明细表中
		boolean containsDatePeriodVar;// 日报项目中，是否有上期、上年等信息，如果有的话，需要将日期与期间的对应关系准备到tbm_dateperiod表中
		boolean containsOvertimeBeginEndTimeVar;// 日报项目中，是否使用了加班的开始时间、加班结束时间，需要计算出加班单的归属日和考虑开始及结束时间的时区，并准备到tbm_overtimebelong表中

		LeaveTypeCopyVO[] leaveCopyVOs;
		AwayTypeCopyVO[] awayCopyVOs;
		OverTimeTypeCopyVO[] overCopyVOs;
		ShutDownTypeCopyVO[] shutCopyVOs;

		Map<String, Object> paramValues;// 各个参数的值，key是参数的编码

		Map<String, IDayDataCreator> dataCreatorMap;// java实现类的map，key是日报项目的code（只有java类的项目put进去），value是实现类

		ItemCopyVO[] dayItemVOs;// 日报项目，要求按计算顺序排列
		Map<String, AggShiftVO> aggShiftMap;// 所有班次的aggvo的map
		Map<String, ShiftVO> shiftMap;// 所有班次的map
		Map<String, String[]> datePeriodMap;// 日期和当期、下期对应的map
		BillMutexRule billMutexRule;// 单据冲突规则

		Map<String, LeaveRegVO[]> leaveMap;// 所有人员在日期范围内的休假记录,不含哺乳假
		Map<String, LeaveRegVO[]> lactationMap;// 所有人员在日期范围内的哺乳假记录
		Map<String, OvertimeRegVO[]> overMap;// 所有人员在日期范围内的休假记录
		Map<String, AwayRegVO[]> awayMap;// 所有人员在日期范围内的出差记录
		Map<String, ShutdownRegVO[]> shutMap;// 所有人员在日期范围内的停工待料记录
		Map<String, ICheckTime[]> checkTimesMap;// 所有人的刷签卡记录，要求按时间先后顺序排列
		// 所有人员的排班
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap;
		// 所有人员的timedata数据,第一个string是人员主键，第二个UFLiteralDate是日期，value是timedatavo
		Map<String, Map<UFLiteralDate, TimeDataVO>> timedataMap = null;
		// 所有人员的lateearly数据
		Map<String, Map<UFLiteralDate, LateEarlyVO>> lateearlyMap = null;
		// 所有人员的考勤档案数据,key是人员主键，value是在这段时间内的考勤档案vo(一般情况下只有一条。如果有多条，那么已经按时间先后排好序)
		Map<String, List<TBMPsndocVO>> tbmPsndocMap = null;
		// V6.1新增，所有人员的任职组织主键map，key是人员主键，value的key是日期，value是此人此人任职所属的业务单元的主键
		public Map<String, Map<UFLiteralDate, String>> allDateOrgMap;
		// 所有人员的日报的主表主键,第一个string是人员主键pk_psndoc,第二个string是日期，第三个string是pk_daystat。这个map的主要作用是为后面的daystatb子表生成做准备
		Map<String, Map<String, String>> daystatPKMap;
		@SuppressWarnings("unused")
		double workDayLength;

		protected SolidifyPara toSolidifyPara(String pk_psndoc, UFLiteralDate date, ITimeScope kqScope)
				throws BusinessException {
			solidifyPara.timeruleVO = timeRuleVO;
			solidifyPara.date = date;
			Map<UFLiteralDate, AggPsnCalendar> psnCalendarMap = calendarMap == null ? null : calendarMap.get(pk_psndoc);
			solidifyPara.calendarVO = null;
			if (psnCalendarMap != null) {
				AggPsnCalendar dateCalendar = psnCalendarMap.get(date);
				if (dateCalendar != null)
					solidifyPara.calendarVO = dateCalendar;
				else {// 后台任务使用ufliteralDate做key会出现问题在此处理一下
					for (UFLiteralDate udate : psnCalendarMap.keySet()) {
						if (udate.isSameDate(date))
							solidifyPara.calendarVO = psnCalendarMap.get(udate);
					}
				}
			}
			// solidifyPara.calendarVO=calendarMap==null?null:calendarMap.get(pk_psndoc)==null?null:calendarMap.get(pk_psndoc).get(date);
			String pk_shift = solidifyPara.calendarVO == null ? null : solidifyPara.calendarVO.getPsnCalendarVO()
					.getPk_shift();
			solidifyPara.shiftVO = ShiftServiceFacade.getAggShiftVOFromMap(aggShiftMap, pk_shift);
			Map<UFLiteralDate, String> dateOrgMap = allDateOrgMap.get(pk_psndoc);
			solidifyPara.timeZone = MapUtils.isEmpty(dateOrgMap) ? ICalendar.BASE_TIMEZONE : timeRuleVO
					.getTimeZoneMap().get(dateOrgMap.get(date));
			solidifyPara.leaveBills = DataFilterUtils.filterRegVOs(kqScope,
					DataFilterUtils.filterRegVOs(pk_psndoc, leaveMap));
			solidifyPara.awayBills = DataFilterUtils.filterRegVOs(kqScope,
					DataFilterUtils.filterRegVOs(pk_psndoc, awayMap));
			solidifyPara.shutdownBills = DataFilterUtils.filterRegVOs(kqScope,
					DataFilterUtils.filterRegVOs(pk_psndoc, shutMap));
			solidifyPara.mergeLASScopes = TimeScopeUtils.mergeTimeScopes(
					TimeScopeUtils.mergeTimeScopes(solidifyPara.awayBills, solidifyPara.leaveBills),
					solidifyPara.shutdownBills);
			solidifyPara.lactationholidayVO = DataFilterUtils.filterDateScopeVO(date.toString(),
					DataFilterUtils.filterRegVOs(pk_psndoc, lactationMap));
			solidifyPara.checkTimes = DataFilterUtils.filterCheckTimes(kqScope,
					DataFilterUtils.filterRegVOs(pk_psndoc, checkTimesMap));
			return solidifyPara;
		}
	}

	/**
	 * 数据导入
	 */
	@SuppressWarnings("unchecked")
	public <T extends IVOWithDynamicAttributes> DaystatImportParam importDatas(List<List<GeneralVO>> vosList,
			DaystatImportParam paramvo) throws BusinessException {
		if (CollectionUtils.isEmpty(vosList)) {
			return null;
		}
		// 检验开始结束日期范围是否存在已封存的考勤期间,如果存在已封存的期间则提示并返回
		PeriodVO[] periodvos = PeriodServiceFacade.queryPeriodsByDateScope(paramvo.getContext().getPk_org(),
				paramvo.getBegindate(), paramvo.getEnddate());
		if (ArrayUtils.isEmpty(periodvos)) {
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0071")
			/* @res "抱歉,您选择的考勤日期范围内不存在考勤期间,请重新选择!" */);
		}
		StringBuilder invalidPeroidMes = new StringBuilder();
		for (int i = 0; i < periodvos.length; i++) {
			if (true == periodvos[i].isSeal()) {
				invalidPeroidMes.append(" [" + periodvos[i].getBegindate().toString());
				invalidPeroidMes.append("," + periodvos[i].getEnddate().toString() + "]");
			}
		}
		if (!StringUtils.isBlank(invalidPeroidMes.toString())) {
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0072"
			/* @res "已封存的考勤期间不能进行导入操作!{0}" */, invalidPeroidMes.toString()));
		}
		String succMes = "";
		T[] datavos;
		Class<T> clz = (Class<T>) (ViewOrderVO.FUN_TYPE_DAY == paramvo.getReport_type() ? DayStatVO.class
				: MonthStatVO.class);
		try {
			if (DaystatImportParam.TYPE_EXCEL_FILE == paramvo.getFile_type()) {
				datavos = (T[]) DayStatImportHelper.changeGeneralVOToDayStatVOforExcel(vosList, paramvo, clz);
			} else {
				datavos = (T[]) DayStatImportHelper.changeGeneralVOToDayStatVOforTxt(vosList, paramvo, clz);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		// 有错的话就直接返回
		if (!paramvo.isRightFormat())
			return paramvo;
		// 执行导入的持久化操作
		try {
			// 权限过滤，依赖考勤档案的维护权限
			// 数据对应的考勤档案的
			List<String> pk_tbm_psndocs = new ArrayList<String>();
			for (T vo : datavos) {
				if (ViewOrderVO.FUN_TYPE_MONTH == paramvo.getReport_type()) {
					pk_tbm_psndocs.add(((MonthStatVO) vo).getPk_tbm_psndoc());
				} else {
					pk_tbm_psndocs.add(((DayStatVO) vo).getPk_tbm_psndoc());
				}
			}
			IDataPermissionPubService perimssionService = NCLocator.getInstance().lookup(
					IDataPermissionPubService.class);
			Map<String, UFBoolean> operMap = perimssionService.isUserhasPermissionByMetaDataOperation("60170psndoc",
					pk_tbm_psndocs.toArray(new String[0]), IActionCode.EDIT, InvocationInfoProxy.getInstance()
							.getGroupId(), InvocationInfoProxy.getInstance().getUserId());
			String pk_org = paramvo.getContext().getPk_org();
			if (ViewOrderVO.FUN_TYPE_MONTH == paramvo.getReport_type()) {// 已经审核的考勤月报不允许导入修改数据
				TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
				if (timeRuleVO.getMreportapproveflag().booleanValue()) {// 需要审核，需要过滤已审核的月报
					String sql = " select tbm_psndoc.pk_tbm_psndoc from tbm_psndoc inner join tbm_monthstat "
							+ " on tbm_psndoc.pk_psndoc = tbm_monthstat.pk_psndoc and tbm_psndoc.pk_org = tbm_monthstat.pk_org "
							+ " where tbm_monthstat.isapprove = 'Y' and tbm_monthstat.pk_org = '" + pk_org + "' "
							+ " and tbm_monthstat.tbmyear = '" + paramvo.getTbmyear()
							+ "' and tbm_monthstat.tbmmonth = '" + paramvo.getTbmmonth() + "'";
					ArrayList<String> pkList = (ArrayList<String>) new BaseDAO().executeQuery(sql,
							new ArrayListProcessor());
					for (Object pk : pkList) {
						Object[] pks = (Object[]) pk;
						String pk_tbm = (String) pks[0];
						UFBoolean uf = operMap.get(pk_tbm);
						if (null == uf || !uf.booleanValue())
							continue;
						operMap.put(pk_tbm, UFBoolean.FALSE);
					}
				}
			}
			List<T> saveList = new ArrayList<T>();
			for (T vo : datavos) {
				String pk_tbm_psndoc = null;
				if (ViewOrderVO.FUN_TYPE_MONTH == paramvo.getReport_type()) {
					pk_tbm_psndoc = ((MonthStatVO) vo).getPk_tbm_psndoc();
				} else {
					pk_tbm_psndoc = ((DayStatVO) vo).getPk_tbm_psndoc();
				}
				if (operMap.get(pk_tbm_psndoc).booleanValue())
					saveList.add(vo);
			}
			datavos = saveList.toArray((T[]) Array.newInstance(clz, 0));

			new DayStatDAO().save4Import(paramvo.getContext().getPk_org(), datavos, paramvo.isIgnoreNullCell());
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		// 如果是导入日报需要发事件
		if (ViewOrderVO.FUN_TYPE_DAY == paramvo.getReport_type())
			EventDispatcher
					.fireEvent(new BusinessEvent(IMetaDataIDConst.DAYSTAT, IEventType.TYPE_INSERT_AFTER, datavos));
		succMes = ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0073"
		/* @res "成功导入记录{0}条!" */, ArrayUtils.isEmpty(datavos) ? 0 + "" : datavos.length + "");
		paramvo.setSuccessMsg(succMes);
		// 业务日志
		if (ViewOrderVO.FUN_TYPE_DAY == paramvo.getReport_type())
			TaBusilogUtil
					.writeDayStatImportBusiLog((DayStatVO[]) datavos, paramvo.getBegindate(), paramvo.getEnddate());
		else
			TaBusilogUtil.writeMonthStatImportBusiLog((MonthStatVO[]) datavos,
					paramvo.getTbmyear() + paramvo.getTbmmonth());
		return paramvo;
	}

	@Override
	public DayStatVO[] queryByDeptAndDate(LoginContext context, String pk_dept, UFLiteralDate date)
			throws BusinessException {
		if (StringUtils.isBlank(pk_dept) || date == null)
			return null;
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, null);

		// 查询本组织所有的日/月报类型，包含已停用的,先查出来作为下一步查询的参数，方式每天查的时候都查一遍影响效率
		int itemClass = Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		ItemVO[] allItemVOs = NCLocator.getInstance().lookup(IItemQueryService.class)
				.queryItemByOrg(context.getPk_org(), itemClass, true);
		return queryByCondition(context.getPk_org(), fromWhereSQL, date, true, allItemVOs);
	}

	@Override
	public DayStatVO[] generate(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean showNoDataRecord) throws BusinessException {
		generate(pk_org, fromWhereSQL, beginDate, endDate);
		return queryByCondition(pk_org, fromWhereSQL, beginDate, endDate, showNoDataRecord);
	}

	@Override
	public DayStatVO[] queryByCondition(String pk_org, String[] pks) throws BusinessException {
		if (StringUtils.isBlank(pk_org) || ArrayUtils.isEmpty(pks))
			return null;
		Map<String, List<String>> psnMap = new HashMap<String, List<String>>();
		// 使用map的keyset没有顺序不好用，采用一个list
		List<String> days = new ArrayList<String>();
		for (String pk : pks) {
			String[] split = pk.split(",");
			String pk_psndoc = split[0];
			String date = split[1];
			if (psnMap.get(date) == null) {
				List<String> psnList = new ArrayList<String>();
				psnList.add(pk_psndoc);
				psnMap.put(date, psnList);
				days.add(date);
			} else {
				psnMap.get(date).add(pk_psndoc);
			}
		}

		// 查询本组织所有的日/月报类型，包含已停用的,先查出来作为下一步查询的参数，方式每天查的时候都查一遍影响效率
		int itemClass = Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		ItemVO[] allItemVOs = NCLocator.getInstance().lookup(IItemQueryService.class)
				.queryItemByOrg(pk_org, itemClass, true);
		DayStatVO[] retArray = null;
		String[] dates = days.toArray(new String[0]);
		// 每一天的人都有可能不一样，因此要一天一天地查询,效率不好啊
		for (String calendar : dates) {
			List<String> list = psnMap.get(calendar);
			if (CollectionUtils.isEmpty(list))
				continue;
			String[] pk_psndocs = list.toArray(new String[0]);
			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
			UFLiteralDate date = new UFLiteralDate(calendar);
			retArray = (DayStatVO[]) ArrayUtils.addAll(retArray,
					queryByCondition(pk_org, fromWhereSQL, date, true, allItemVOs));
		}
		if (ArrayUtils.isEmpty(retArray))
			return null;
		// SuperVOUtil.sortByAttributeName(retArray, DayStatVO.CALENDAR,
		// true);//不排序导致界面加载考勤项目和日期不对应，没找到加载错误的原因
		// 返回数据和传入的主键的顺序不一致导致数据错误（分页model导致的）
		Map<String, DayStatVO> voMap = new HashMap<String, DayStatVO>();
		for (DayStatVO vo : retArray) {
			voMap.put(vo.getPk_psndoc() + "," + vo.getCalendar().toString(), vo);
		}
		List<DayStatVO> reList = new ArrayList<DayStatVO>();
		for (String pk : pks) {
			reList.add(voMap.get(pk));
		}
		return reList.toArray(new DayStatVO[0]);
		// return retArray;
	}

	@Override
	public String[] queryPksByCondition(LoginContext context, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean showNoDataRecord) throws BusinessException {

		return queryOnlyPksByCondition(context, fromWhereSQL, beginDate, endDate, showNoDataRecord);

		// 效率优化改用上面的查询处理
		// // DayStatVO[] queryByCondition = queryByCondition(context,
		// fromWhereSQL, beginDate, endDate, showNoDataRecord);
		// UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate,
		// endDate);
		// fromWhereSQL =
		// TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		// DayStatVO[] dayStatVOs = null;
		//
		// // //查询本组织所有的日/月报类型，包含已停用的,先查出来作为下一步查询的参数，方式每天查的时候都查一遍影响效率
		// int itemClass =
		// Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		// ItemVO[] allItemVOs =
		// NCLocator.getInstance().lookup(IItemQueryService.class).queryItemByOrg(context.getPk_org(),
		// itemClass, true);
		// //
		// //每一天的人都有可能不一样，因此要一天一天地查询
		// for(UFLiteralDate date:allDates){
		// dayStatVOs = (DayStatVO[])ArrayUtils.addAll(dayStatVOs,
		// queryByCondition(context.getPk_org(), fromWhereSQL, date,
		// showNoDataRecord,allItemVOs));
		// }
		// if(ArrayUtils.isEmpty(dayStatVOs))
		// return null;
		// String[] pks = new String[dayStatVOs.length];
		// for(int i=0;i<dayStatVOs.length;i++){
		// pks[i] = dayStatVOs[i].getPk_psndoc() + "," +
		// dayStatVOs[i].getCalendar().toString();
		// }
		// return pks;
	}

	/**
	 * 上面查询方法的优化，只查询psndoc+日期
	 * 
	 * @param context
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @param showNoDataRecord
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String[] queryOnlyPksByCondition(LoginContext context, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean showNoDataRecord) throws BusinessException {
		// 添加权限
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);

		String pk_org = context.getPk_org();
		BaseDAO dao = new BaseDAO();
		// //查询所有的本组织的考勤档案
		// String orgCondition = " begindate <= '" + endDate.toString() +
		// "' and enddate >= '" + beginDate.toString() + "' and pk_org = '" +
		// pk_org + "' ";
		//
		// String orgSql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL,
		// TBMPsndocVO.TABLE_NAME, orgCondition, null);
		// Collection<TBMPsndocVO> orgc = (Collection<TBMPsndocVO>)
		// dao.executeQuery(orgSql, null, new BeanListProcessor(
		// TBMPsndocVO.class));
		// //管理组织数据
		// String adminCondition = " begindate <= '" + endDate.toString() +
		// "' and enddate >= '" + beginDate.toString() + "' and pk_org <> '" +
		// pk_org + "' and pk_adminorg = '" + pk_org+ "' ";
		// String adminSql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL,
		// TBMPsndocVO.TABLE_NAME, adminCondition, null);
		// Collection<TBMPsndocVO> adminc = (Collection<TBMPsndocVO>)
		// dao.executeQuery(adminSql, null, new BeanListProcessor(
		// TBMPsndocVO.class));

		// 本组织和管理组织一块查询
		String condition = TBMPsndocSqlPiecer.TBM_PSNDOC_NAME + ".begindate <= ? and "
				+ TBMPsndocSqlPiecer.TBM_PSNDOC_NAME + ".enddate >= ? and (" + TBMPsndocSqlPiecer.TBM_PSNDOC_NAME
				+ ".pk_org = ? or " + TBMPsndocSqlPiecer.TBM_PSNDOC_NAME + ".pk_adminorg = ? ) ";
		SQLParameter para = new SQLParameter();
		para.addParam(endDate.toString());
		para.addParam(beginDate.toString());
		para.addParam(pk_org);
		para.addParam(pk_org);

		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, TBMPsndocVO.TABLE_NAME, condition, null);
		sql = sql + " order by " + TBMPsndocVO.TABLE_NAME + ".pk_psndoc," + TBMPsndocVO.TABLE_NAME + ".begindate ";
		Collection<TBMPsndocVO> c = (Collection<TBMPsndocVO>) dao.executeQuery(sql, para, new BeanListProcessor(
				TBMPsndocVO.class));
		if (CollectionUtils.isEmpty(c))
			return null;

		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		List<String> retpks = new ArrayList<String>();

		if (showNoDataRecord) {// 如果显示无数据记录则不需要查询日报数据，直接构造主键（pk_psndoc,date)即可
			for (TBMPsndocVO vo : c) {
				for (UFLiteralDate date : allDates) {
					if (date.getDaysAfter(vo.getBegindate()) >= 0 && vo.getEnddate().getDaysAfter(date) >= 0) {
						retpks.add(vo.getPk_psndoc() + "," + date.toString());
					}
				}
			}
			return retpks.toArray(new String[0]);
		}

		InSQLCreator is = new InSQLCreator();
		String insql = is.getInSQL(c.toArray(new TBMPsndocVO[0]), TBMPsndocVO.PK_TBM_PSNDOC);

		String daystatSql = " select daystat.pk_psndoc, daystat.calendar from tbm_daystat daystat inner join tbm_psndoc psn  "
				+ " on daystat.pk_org = psn.pk_org and daystat.calendar between psn.begindate and psn.enddate and daystat.pk_psndoc = psn.pk_psndoc "
				+ " where psn.pk_tbm_psndoc in ("
				+ insql
				+ ") and daystat.calendar between '"
				+ beginDate.toString()
				+ "' and '" + endDate.toString() + "' ";

		Collection<DayStatVO> statc = (Collection<DayStatVO>) dao.executeQuery(daystatSql, null, new BeanListProcessor(
				DayStatVO.class));
		if (CollectionUtils.isEmpty(statc))
			return null;
		for (DayStatVO vo : statc) {
			retpks.add(vo.getPk_psndoc() + "," + vo.getCalendar().toString());
		}
		return retpks.toArray(new String[0]);

		// 如果显示无数据记录则不需要查询日报数据，直接构造主键（pk_psndoc,date)即可
		// if(showNoDataRecord){
		// if(!CollectionUtils.isEmpty(orgc)){
		// for(TBMPsndocVO vo:orgc){
		// for(UFLiteralDate date:allDates){
		// if(date.getDaysAfter(vo.getBegindate())>=0&&vo.getEnddate().getDaysAfter(date)>=0){
		// retpks.add(vo.getPk_psndoc() + "," + date.toString());
		// }
		// }
		// }
		// }
		// if(!CollectionUtils.isEmpty(adminc)){
		// for(TBMPsndocVO vo:adminc){
		// for(UFLiteralDate date:allDates){
		// if(date.getDaysAfter(vo.getBegindate())>=0&&vo.getEnddate().getDaysAfter(date)>=0){
		// retpks.add(vo.getPk_psndoc() + "," + date.toString());
		// }
		// }
		// }
		// }
		// return retpks.toArray(new String[0]);
		// }
		// 只显示有数据的记录，需要查询数据库了
	}

	// 日报异常发送消息
	private void sendMessage(String pk_org, String[] pk_psndocs, CalSumParam calSumParam, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if (!calSumParam.timeRuleVO.isDayNotice() || !isHrssStarted())
			return;
		Set<String> noticePsnPkset = getExceptionPsns(pk_org, pk_psndocs, calSumParam, beginDate, endDate);// 异常人员主键集合
		if (noticePsnPkset.size() <= 0) {
			return;
		}
		String[] noticePsnPks = (String[]) noticePsnPkset.toArray(new String[0]);
		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(noticePsnPks);
		String cond = "pk_psndoc in (" + insql + " )";
		// 查询出所有的psndocvo
		PsndocVO[] psndocvos = (PsndocVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, PsndocVO.class, cond);
		if (ArrayUtils.isEmpty(psndocvos))
			return;
		// 查询组织
		OrgVO org = (OrgVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByPk(null, OrgVO.class, pk_org);
		// 查询月报
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(noticePsnPks);
		DayStatVO[] dayStatVOs = queryByCondition(pk_org, fromWhereSQL, beginDate, endDate, false);
		Map<String, DayStatVO[]> daystatMap = CommonUtils.group2ArrayByField(DayStatVO.PK_PSNDOC, dayStatVOs);

		HashMap<String, UserVO[]> userMap = NCLocator.getInstance().lookup(IUserPubService.class)
				.batchQueryUserVOsByPsnDocID(pk_psndocs, null);
		IURLGenerator IurlDirect = NCLocator.getInstance().lookup(IURLGenerator.class);
		IHRMessageSend messageSendServer = NCLocator.getInstance().lookup(IHRMessageSend.class);
		// 按人员循环发送消息
		for (PsndocVO psndocVO : psndocvos) {
			HRBusiMessageVO messageVO = new HRBusiMessageVO();
			DayStatVO[] dayStats = daystatMap.get(psndocVO.getPk_psndoc());
			messageVO.setBillVO(ArrayUtils.isEmpty(dayStats) ? null : dayStats[0]);// 元数据属性解析
			messageVO.setMsgrescode(TaMessageConst.DAYSTATEXCPMSG);
			// 业务变量
			Hashtable<String, Object> busiVarValues = new Hashtable<String, Object>();
			UserVO[] users = userMap.get(psndocVO.getPk_psndoc());
			SSOInfo ssinfo = new SSOInfo();
			if (!ArrayUtils.isEmpty(users)) {
				ssinfo.setUserPK(users[0].getCuserid());
			}
			ssinfo.setTtl(PubEnv.getServerTime().getDateTimeAfter(30));
			ssinfo.setFuncode("E20200910");// E20200910 考勤月报的补考勤的功能节点号

			String urlTitle = IurlDirect.buildURLString(ssinfo);
			busiVarValues.put("url", urlTitle);
			busiVarValues.put("CURRUSERNAME", MultiLangHelper.getName(psndocVO));
			busiVarValues.put("CURRCORPNAME", MultiLangHelper.getName(org));
			messageVO.setBusiVarValues(busiVarValues);
			messageVO.setPkorgs(new String[] { pk_org });
			messageVO.setReceiverPkUsers(ArrayUtils.isEmpty(users) ? null : new String[] { users[0].getPrimaryKey() });

			messageSendServer.sendBuziMessage_RequiresNew(messageVO);
		}
	}

	// 得到月报异常人员
	private Set<String> getExceptionPsns(String pk_org, String[] pk_psndocs, CalSumParam calSumParam,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {

		Set<String> exceptionPsnPkSet = new HashSet<String>();
		ITBMPsndocQueryService tbmPsnQueryS = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);

		// 查询考勤档案 找到对应的考勤方式（机器考勤or手工考勤）
		Map<String, List<TBMPsndocVO>> tbmPsnMap = tbmPsnQueryS.queryTBMPsndocMapByPsndocs(pk_org, pk_psndocs,
				beginDate, endDate, true);
		if (MapUtils.isEmpty(tbmPsnMap)) {
			return null;
		}
		ITimeDataQueryService timDataQuery = NCLocator.getInstance().lookup(ITimeDataQueryService.class);
		// 机器考勤数据
		Map<String, Map<UFLiteralDate, TimeDataVO>> machTimeDateMap = timDataQuery.queryVOMapByPsndocInSQLForMonth(
				pk_org, beginDate, endDate, calSumParam.psndocInSQL);
		// 循环查找机器考勤数据 的异常情况，若含有和月报异常发送邮件设置相匹配的 则把 该人员的pk加入的发送邮件的集合内
		if (MapUtils.isNotEmpty(machTimeDateMap)) {
			for (String pk_psndoc : machTimeDateMap.keySet()) {
				Map<UFLiteralDate, TimeDataVO> dataMap = machTimeDateMap.get(pk_psndoc);
				for (UFLiteralDate date : dataMap.keySet()) {
					TimeDataVO timeDataVO = dataMap.get(date);
					if (!timeDataVO.isNormal()) {
						if (calSumParam.timeRuleVO.getDayabsentnotice().booleanValue()) {
							if (this.isAbsent(timeDataVO)) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						if (calSumParam.timeRuleVO.getDaylatenotice().booleanValue()) {
							if (this.isLate(timeDataVO)) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						if (calSumParam.timeRuleVO.getDayearlynotice().booleanValue()) {
							if (this.isEarly(timeDataVO)) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						if (calSumParam.timeRuleVO.getDaymidoutnotice().booleanValue()) {
							if (timeDataVO.getIsMidOut()) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
					}
				}
			}
		}

		ILateEarlyQueryService handTimeDataQuery = NCLocator.getInstance().lookup(ILateEarlyQueryService.class);
		// 手工考勤数据
		Map<String, Map<UFLiteralDate, LateEarlyVO>> handTimeDateMap = handTimeDataQuery
				.queryVOMapByPsndocInSQLForMonth(pk_org, beginDate, endDate, calSumParam.psndocInSQL);
		if (MapUtils.isNotEmpty(handTimeDateMap)) {
			for (String pk_psndoc : handTimeDateMap.keySet()) {
				Map<UFLiteralDate, LateEarlyVO> dataMap = handTimeDateMap.get(pk_psndoc);
				for (UFLiteralDate date : dataMap.keySet()) {
					LateEarlyVO handDataVO = dataMap.get(date);
					if (!handDataVO.isNormal()) {
						if (calSumParam.timeRuleVO.getDayabsentnotice().booleanValue()) {
							if (this.isAbsent(handDataVO)) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						if (calSumParam.timeRuleVO.getDaylatenotice().booleanValue()) {
							if (this.isLate(handDataVO)) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						if (calSumParam.timeRuleVO.getDayearlynotice().booleanValue()) {
							if (this.isEarly(handDataVO)) {
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						// 手工考勤没有中途外出这一项,所以这一段不再需要
						// if(calSumParam.timeRuleVO.isMidOutNotice()){
						// if(handDataVO.getAbsenthour().toDouble()>0){
						// exceptionPsnPkSet.add(pk_psndoc);
						// }
						// }

					}
				}
			}

		}
		return exceptionPsnPkSet;
	}

	/**
	 * //判断一条考勤是否 有迟到现象 true 为有迟到现象
	 */
	private boolean isLate(Object timeDataVO) {
		if (timeDataVO instanceof TimeDataVO) {
			TimeDataVO timedata = (TimeDataVO) timeDataVO;
			for (int i = 0; i < 4; i++) {
				if (timedata.getIslate(i) == 1)
					return true;
			}
		}
		if (timeDataVO instanceof LateEarlyVO) {
			LateEarlyVO timedata = (LateEarlyVO) timeDataVO;
			if (timedata.getLatecount() > 0 || timedata.getLatelength().toDouble() >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * //判断一条考勤是否 有早退现象 true 为有早退现象
	 */
	private boolean isEarly(Object timeDataVO) {
		if (timeDataVO instanceof TimeDataVO) {
			TimeDataVO timedata = (TimeDataVO) timeDataVO;
			for (int i = 0; i < 4; i++) {
				if (timedata.getIsearly(i) == 1)
					return true;
			}
		}
		if (timeDataVO instanceof LateEarlyVO) {
			LateEarlyVO timedata = (LateEarlyVO) timeDataVO;
			if (timedata.getEarlycount() > 0 || timedata.getEarlylength().toDouble() >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * //判断一条考勤是否 有矿工现象 true 为包含矿工
	 */
	private boolean isAbsent(Object timeDataVO) {
		if (timeDataVO instanceof TimeDataVO) {
			TimeDataVO timedata = (TimeDataVO) timeDataVO;
			for (int i = 0; i < 4; i++) {
				if (timedata.getIsabsent(i) == 1 || timedata.getIsearlyabsent(i) == 1
						|| timedata.getIslateabsent(i) == 1)
					return true;
			}
		}
		if (timeDataVO instanceof LateEarlyVO) {
			LateEarlyVO timedata = (LateEarlyVO) timeDataVO;
			if (timedata.getAbsenthour().toDouble() > 0) {
				return true;
			}
		}
		return false;
	}

	private Boolean isHrssStarted() {
		Boolean isstart = false;
		isstart = PubEnv.isModuleStarted(PubEnv.getPk_group(), PubEnv.MODULE_HRES);
		// 修改说明： 应该使用上面的代码，但是修改了 ICommonConst.MODULECODE_HRSS的补丁不起作用，只好暂时使用下面的代码
		// 注意v63要换回来使用上面的代码
		// 判断自助模块是否启用
		// isstart= PubEnv.isModuleStarted(PubEnv.getPk_group(),"E202");
		return isstart;
	}
}