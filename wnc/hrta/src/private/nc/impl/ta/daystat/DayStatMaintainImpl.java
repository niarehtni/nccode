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
		// Ȩ��sql 2013-03-29 modified ��̨�����Զ������޷���ȡȨ�ޣ��ᱨ����˺�̨����Ȩ�޹���
		if (!PubEnv.UAP_USER.equalsIgnoreCase(PubEnv.getPk_user()))// �ձ�������Ȩ�޺Ϳ��ڵ�����ά��Ȩ��һ��
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
		// //����Щ�˵��ձ���ס����ϸӦ���������죬����������������̫�����п��ǣ�ֻ����+��֯��
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
		// /*@res "�����������ɿ����ձ������Ժ�����!"*/);
		// DayStatServiceImpl serviceImpl =new DayStatServiceImpl();
		// //�����յ���Ա�ձ���¼
		// serviceImpl.createDayStatRecord(pk_org, fromWhereSQL, beginDate,
		// endDate);
		// //�����ձ�
		// generate0(pk_org, pk_psndocs, beginDate, endDate);
		// // ���ɺ��¼�
		// PsnInSQLDateScope psnInSql = new PsnInSQLDateScope();
		// psnInSql.setPk_org(pk_org);
		// psnInSql.setPsndocInSQL(isc.getInSQL(pk_psndocs));
		// psnInSql.setBeginDate(beginDate);
		// psnInSql.setEndDate(endDate);
		// EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.DAYSTAT,
		// "2001", psnInSql));
		//
		// //ҵ����־
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
		// //ά��Ȩ����
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
		// //����Щ�˵��ձ���ס����ϸӦ���������죬����������������̫�����п��ǣ�ֻ����+��֯��
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
		// /*@res "�����������ɿ����ձ������Ժ�����!"*/);
		// FromWhereSQL fromWhereSQL =
		// TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
		// DayStatServiceImpl serviceImpl =new DayStatServiceImpl();
		// //�����յ���Ա�ձ���¼
		// serviceImpl.createDayStatRecord(pk_org, fromWhereSQL, beginDate,
		// endDate);
		// TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
		// //�����ձ�
		// generate0(pk_org, pk_psndocs, beginDate, endDate);
		// // ���ɺ��¼�
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
		// �ձ�����ȡ���ڵ�����ά��Ȩ��
		FromWhereSQL powerWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
		powerWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT,
				powerWhereSQL);
		String[] powerPks = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryLatestPsndocPksByCondition(pk_org, powerWhereSQL, beginDate, endDate);

		generateBatch(pk_org, powerPks, beginDate, endDate);
	}

	/**
	 * �еĿͻ�����Ա�ǳ��࣬��������ʱ���������ܴ󣬵����ڴ�������ߴﵽ�����ݿ��ѯ�����ƣ�������������ʧ�� �ڴ˽��з�������ÿ1000��ִ��һ��
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
		// ����һ�²�����������
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

		// �ռ���Ϣ
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
	 * �ձ����ɵ����������е�һ��ִ��
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 *            һǧ��һ��
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	@Override
	public void generate4Once(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs) || StringUtils.isEmpty(pk_org) || beginDate.afterDate(endDate))
			return;
		// //ά��Ȩ����
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
		// ����Щ�˵��ձ���ס����ϸӦ���������죬����������������̫�����п��ǣ�ֻ����+��֯��
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
				/* @res "�����������ɿ����ձ������Ժ�����!" */);
			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
			DayStatServiceImpl serviceImpl = new DayStatServiceImpl();
			// �����յ���Ա�ձ���¼
			serviceImpl.createDayStatRecord(pk_org, fromWhereSQL, beginDate, endDate);
			TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
			// �����ձ�
			generate0(pk_org, pk_psndocs, beginDate, endDate);
			// ���ɺ��¼�
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
	 * �����¼������̣߳���Ӱ���ձ��������Ӧʱ��
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
				// �߳��л�����Ϣ�ᶪʧ������������һ��
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
	 * ��¼��־�����̣߳���Ӱ���ձ��������Ӧʱ��
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
				// �߳��л�����Ϣ�ᶪʧ������������һ��
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
		// Java�����������Ŀ�ļ������
		DayCalParam dayCalParam = DayStatCalculationHelper.createPara(pk_org);
		InSQLCreator isc = new InSQLCreator();
		try {
			// ׼���������
			CalSumParam calSumParam = prepareCalSumParam(pk_org, pk_psndocs, beginDate, endDate, isc, dayCalParam);
			// ���㵥��ʱ��
			processBills(calSumParam);
			// ����ձ���ĿҪ��ѯ����������Ϣ������Ҫ��ǰ������������ϸ���뵽tbm_holidayenjoyh��tbm_holidayenjoyb��
			IHRHolidayManageService holidaysService = NCLocator.getInstance().lookup(IHRHolidayManageService.class);
			if (calSumParam.containsHolidayFunc)
				holidaysService.createEnjoyDetail(pk_org, pk_psndocs, beginDate.getDateBefore(365 * 3),
						endDate.getDateAfter(365 * 3));
			IPeriodManageService periodService = NCLocator.getInstance().lookup(IPeriodManageService.class);
			if (calSumParam.containsDatePeriodVar)
				periodService.createDatePeriod(pk_org, beginDate, endDate);

			// �����ձ���Ŀ
			processItems(calSumParam);
			if (calSumParam.containsHolidayFunc)
				holidaysService.clearEnjoyDetail();
			if (calSumParam.containsDatePeriodVar)
				periodService.clearDatePeriod();

			// �����ձ���Ŀ���õ��Ӱ࿪ʼ�ͽ���ʱ�䣬Ҫ������ʱ��ʹ����Ҫ���
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
	 * �ձ����ɵ�Ч���Ż��������ʼ�֪ͨ�����̴߳���
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
	 * ׼�����������������˵ĵ��ݣ�������Ŀ�ȵ�
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
		// �������ڼ��Ӧ��ϵ��map��key�����ڣ�value�����飬��һ��Ԫ�������������ڼ䣬�ڶ���Ԫ���������ڼ����һ���ڼ�
		Map<String, String[]> datePeriodMap = new HashMap<String, String[]>();
		processPeriodAndNextPeriodOfDate(
				pk_org,
				CommonUtils.createDateArray(allUFDates[0].getDateBefore(300),
						allUFDates[allUFDates.length - 1].getDateAfter(300)), datePeriodMap);
		calSumParam.datePeriodMap = datePeriodMap;
		// ������Ŀ���Ƿ����java�����Ŀ�����������java����Ŀ��������һЩ�������Բ���������߼���Ч��
		boolean existsJavaItem = false;
		for (ItemCopyVO itemVO : calSumParam.dayItemVOs) {
			if (itemVO.getSrc_flag().intValue() == ItemCopyVO.SRC_FLAG_JAVA) {
				existsJavaItem = true;
				break;
			}
		}
		// �������飬����update java���͵���Ŀֵ�����ݿ��ʱ��Ҫ�õ�
		// String[] allDates = CommonUtils.toStringArray(calSumParam.allDates,
		// calSumParam.dateBeginIndex, calSumParam.dateEndIndex);
		// ������Ա���ձ�����������,��һ��string����Ա����pk_psndoc,�ڶ���string�����ڣ�������string��pk_daystat�����map����Ҫ������Ϊ�����daystatb�ӱ�������׼��
		// Logger.error("��ѯ�ձ�����map��ʼ��"+System.currentTimeMillis());
		long time = System.currentTimeMillis();
		calSumParam.daystatPKMap = DayStatCalculationHelper.getDaystatPKMap(calSumParam.pk_org,
				calSumParam.psndocInSQL, beginDate.toString(), endDate.toString());
		Logger.debug("�����ձ�����map��ʱ��" + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		// ������Ա�Ĺ�������map,Ҫ����ǰ������2��(���û����ѡ�����10�ŵ�11�ŵ��ձ�����ô�˴�Ӧ�ò�ѯ8�ŵ�13�ŵĹ�������),��������dateArray�Ѿ��Ǳ��ձ����ɷ�ΧҪ��ǰ���������ˣ�
		// ֮������ǰ������ô�࣬����Ϊ����Ӱ൥�ĳ��ȣ��ڵ��Ӻܳ���ʱ�򣬿��ܻ��õ��ܶ���Ĺ������������п��ܴ�󳬹��û���������ڷ�Χ
		calSumParam.calendarMap = NCLocator
				.getInstance()
				.lookup(IPsnCalendarQueryService.class)
				.queryCalendarVOByPsnInSQL(calSumParam.pk_org, beginDate.getDateBefore(1), endDate.getDateAfter(1),
						calSumParam.psndocInSQL);
		time = System.currentTimeMillis();
		// Logger.error("��ѯ��������������"+System.currentTimeMillis());
		calSumParam.aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		if (!MapUtils.isEmpty(calSumParam.aggShiftMap)) {
			calSumParam.shiftMap = new HashMap<String, ShiftVO>();
			for (String key : calSumParam.aggShiftMap.keySet()) {
				calSumParam.shiftMap.put(key, calSumParam.aggShiftMap.get(key).getShiftVO());
			}
		}
		// ������Ա�Ŀ��ڵ�������,key����Ա������value�������ʱ���ڵĿ��ڵ���vo(һ�������ֻ��һ��������ж�������ô�Ѿ���ʱ���Ⱥ��ź���)
		calSumParam.tbmPsndocMap = NCLocator
				.getInstance()
				.lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocInSQL(calSumParam.pk_org, calSumParam.psndocInSQL, beginDate, endDate, true,
						true);
		calSumParam.allDateOrgMap = TBMPsndocVO.createDateOrgMapByTbmPsndocVOMap(calSumParam.tbmPsndocMap, beginDate,
				endDate);
		// ��ȡjava����Ŀ�ļ�����ʵ��
		time = System.currentTimeMillis();
		if (existsJavaItem) {
			// �������Щmapֻ��java��Ŀ����ʱ���ã����ֻ�ڴ���java��Ŀ��ʱ���ʼ��
			// ������Ա��timedata����,��һ��string����Ա�������ڶ���UFLiteralDate�����ڣ�value��timedatavo
			calSumParam.timedataMap = NCLocator.getInstance().lookup(ITimeDataQueryService.class)
					.queryVOMapByPsndocInSQL(calSumParam.pk_org, beginDate, endDate, calSumParam.psndocInSQL);
			// ������Ա��lateearly����
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
		Logger.debug("��ѯtimedata��lateearly�����ڵ�����ʱ��" + (System.currentTimeMillis() - time));
		// ȡ��������Ա���������ڣ�startDate��ǰ������endDate�ĺ����죩�ĵ�������
		// key����Ա������value����Ա���ݼٵ��ӱ����飬����ļӰ൥���ͣ������һ��
		// ssx modified on 2019-10-18
		// ��ȡǰһ�����һ��Γ��������Ű����r�z©�Γ�
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
		Logger.debug("��ѯ���ݺ�ʱ��" + (System.currentTimeMillis() - time));
		calSumParam.billMutexRule = BillMutexRule.createBillMutexRule(calSumParam.timeRuleVO.getBillmutexrule());
		// һ�������յ�ʱ��,�ڿ��ڹ����ж���
		calSumParam.workDayLength = calSumParam.timeRuleVO == null ? 8 : calSumParam.timeRuleVO.getDaytohour()
				.doubleValue();
		return calSumParam;
	}

	/**
	 * �������ͣ�ӵ��ݵ��ձ�ʱ�� ���У�����ͣ���ݵ��ձ�ʱ���ǵ���ʱ������ÿһ��֮�ڵ�ʱ�� �Ӱ൥���ձ�ʱ���������ŵ��ݵ�ʱ���鵽�Ӱ൥��������
	 * 
	 * @param calSumParam
	 * @throws DAOException
	 */
	private void processBills(CalSumParam calSumParam) throws BusinessException {
		// TODO:���˰���ѭ����������������ļӰࡢ����ݼ١�ͣ������
		// Logger.error("��ʼ���ɼӰ��������ݣ�"+System.currentTimeMillis());
		// ��Ҫ���뵽���ݵ��ӱ�vo
		List<DayStatbVO> statbVOList = new ArrayList<DayStatbVO>();
		int psnCount = calSumParam.pk_psndocs.length;
		Map<String, TimeZone> timeZoneMap = calSumParam.timeRuleVO.getTimeZoneMap();

		// ����������������ռӰ�ʱʹ�ã����Ż�
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

		// ����Աѭ������
		for (int psnIndex = 0; psnIndex < psnCount; psnIndex++) {
			String pk_psndoc = calSumParam.pk_psndocs[psnIndex];
			Map<UFLiteralDate, String> dateOrgMap = calSumParam.allDateOrgMap.get(pk_psndoc);
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
			List<TBMPsndocVO> psndocList = calSumParam.tbmPsndocMap.get(pk_psndoc);
			if (CollectionUtils.isEmpty(psndocList))
				continue;
			// ���е��ݼٵ����������������ڷ�Χ���н���������2008.07-23��2008.08.22�����ܻ���ǰ����Ų���죩
			LeaveRegVO[] leaveBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.leaveMap);
			AwayRegVO[] awayBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.awayMap);// ���еĳ��
			OvertimeRegVO[] overtimeBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.overMap);// ���еļӰ൥
			ShutdownRegVO[] shutdownBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.shutMap);// ���е�ͣ����
			// �Ȱ����е���������һ�ν�����
			ITimeScopeWithBillType[] processedBills = BillProcessHelper.crossAllBills(leaveBills, awayBills,
					overtimeBills, shutdownBills);
			// ������еĽ��Ϊ�գ�����Ҫ�κδ���continue����
			if (processedBills == null || processedBills.length == 0) {
				continue;
			}

			// 2013-06-14���forѭ�����й̻��� filter����ʱ��Ҫ�Ĺ���ʱ�����Ҫ�̻���������˳��ļӰ൥�Ǽ�ȥ����ʱ��κ�ģ�
			for (int dateIndex = calSumParam.dateBeginIndex; dateIndex <= calSumParam.dateEndIndex; dateIndex++) {
				// ��ǰ��
				UFLiteralDate curDate = calSumParam.allDates[dateIndex];
				if (!TBMPsndocVO.isIntersect(psndocList, curDate.toString()))// ��������޿��ڵ�����¼����������
					continue;
				// ��ǰ��İ���Լ�ǰһ��ͺ�һ��İ��
				AggPsnCalendar curCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, curDate);// ��ǰ��İ��

				ShiftVO curShift = curCalendar == null ? null : curCalendar.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, curCalendar.getPsnCalendarVO()
								.getPk_shift());
				AggPsnCalendar preCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc,
						calSumParam.allDates[dateIndex - 1]);// ǰһ��İ��
				ShiftVO preShift = preCalendar == null ? null : preCalendar.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, preCalendar.getPsnCalendarVO()
								.getPk_shift());
				AggPsnCalendar nextCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc,
						calSumParam.allDates[dateIndex + 1]);// ��һ��İ��
				ShiftVO nextShift = nextCalendar == null ? null : nextCalendar.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, nextCalendar.getPsnCalendarVO()
								.getPk_shift());
				TimeZone curTimeZone = CommonUtils.ensureTimeZone(calSumParam.timeRuleVO.getTimeZoneMap().get(
						dateOrgMap.get(curDate)));
				TimeZone preTimeZone = CommonUtils.ensureTimeZone(calSumParam.timeRuleVO.getTimeZoneMap().get(
						dateOrgMap.get(calSumParam.allDates[dateIndex - 1])));
				TimeZone nextTimeZone = CommonUtils.ensureTimeZone(calSumParam.timeRuleVO.getTimeZoneMap().get(
						dateOrgMap.get(calSumParam.allDates[dateIndex + 1])));
				// ����İ������ǵ��԰࣬����Ҫ�ȹ̻��������������㵥��ʱ��
				if (curCalendar != null && curCalendar.getPsnCalendarVO().isFlexibleFinal()) {
					// ����ʱ���
					ITimeScope kqScope = ShiftVO.toKqScope(curShift, preShift, nextShift, curDate.toString(),
							curTimeZone, preTimeZone, nextTimeZone);
					// �����̻��Ĳ���
					SolidifyPara solidifyPara = calSumParam.toSolidifyPara(pk_psndoc, curDate, kqScope);
					// ���й̻�
					curCalendar.setPsnWorkTimeVO(SolidifyUtils.solidify(solidifyPara));
				}
			}

			BillMutexRule billMutexRule = calSumParam.billMutexRule;
			// ȡ���ݼ١��Ӱࡢ���ͣ�������ʱ���
			List<ITimeScopeWithBillType> leaveBillList = BillProcessHelper.filterBills(processedBills,
					BillMutexRule.BILL_LEAVE, billMutexRule);
			List<ITimeScopeWithBillType> awayBillList = BillProcessHelper.filterBills(processedBills,
					BillMutexRule.BILL_AWAY, billMutexRule);
			// �Ӱ൥��ʱ��δ������Ը��ӣ���ΪҪ�������ڵĹ��ݲ��ƼӰ�ʱ�ļӰ൥��¼�������5.5
			List<ITimeScopeWithBillType> overtimeBillList = BillProcessHelper.filterOvertimeBills(processedBills,
					calSumParam.leaveCopyVOs,
					calSumParam.calendarMap == null ? null : calSumParam.calendarMap.get(pk_psndoc),
					calSumParam.aggShiftMap, dateTimeZoneMap, calSumParam.allDates, calSumParam.dateBeginIndex,
					calSumParam.dateEndIndex, billMutexRule, psnEnjoyHolidayScope.get(pk_psndoc));
			List<ITimeScopeWithBillType> shutdownBillList = BillProcessHelper.filterBills(processedBills,
					BillMutexRule.BILL_SHUTDOWN, billMutexRule);
			// �洢�Ӱ൥�������map��key�ǼӰ൥overtimeb��������value�����ڡ����ڼӰ൥���������ݲ�һ�������ǲ��ù�ķ�ʽ���㣬��ĳ���繤���յļӰ൥��ʱ�������ձ�ʱ����
			// �㵽������Ĺ������ϵģ������Ƿ�̯������ģ����������ձ���ʱ�򣬴������������㷨��
			// ��1�죬�ҳ����мӰ൥��ѭ��������Щ�Ӱ൥���ҳ���Щ�Ӱ൥�Ĺ����գ�����������ǽ���ģ������£��������ʱ����
			// ��2�죬�ҳ����мӰ൥��ѭ��������Щ�Ӱ൥���ҳ���Щ�Ӱ൥�Ĺ����գ�����������ǽ���ģ������£��������ʱ����
			// ................
			// ��n�죬�ҳ����мӰ൥��ѭ��������Щ�Ӱ൥���ҳ���Щ�Ӱ൥�Ĺ����գ�����������ǽ���ģ������£��������ʱ����
			// ���Կ�����ÿ����ձ����㶼Ҫ��һ�����飺ѭ���ҳ����мӰ൥�Ĺ����գ���ʵ���ϵ�һ����Ѿ�������мӰ൥�Ĺ������ˣ����������ʵ�������ظ��Ĺ���������
			// Ѱ�ҼӰ൥�Ĺ�������һ���Ƚϸ��ӵ��㷨���ظ������������ʵ���˷ѡ���ˣ���һ��map����Щ�Ӱ൥�Ĺ����մ�����������ÿ�춼�ظ��㣬��һ���������������
			Map<String, UFLiteralDate> overtimeBelongDateMap = new HashMap<String, UFLiteralDate>();// ���ܴ����Ҳ��������յ�����������Ļ���value�����ڣ��ǿգ�����Ҫ��hashmap��������hashtable
			BillProcessHelper.findBelongtoDate(overtimeBills, overtimeBelongDateMap,
					calSumParam.calendarMap == null ? null : calSumParam.calendarMap.get(pk_psndoc),
					calSumParam.shiftMap, calSumParam.allDates, dateTimeZoneMap);

			// ����ձ���Ŀ����Ҫʹ�üӰ�Ŀ�ʼʱ��ͽ���ʱ�䣬��Ҫ�����Ӱ൥��������ʱ��tbm_overtimebelong�������ǿ�ʼ�ͽ���ʱ���ʱ�����⡣
			if (calSumParam.containsOvertimeBeginEndTimeVar) {
				IOvertimeManageService otservice = NCLocator.getInstance().lookup(IOvertimeManageService.class);
				otservice.createOvertimeBelongData(overtimeBills, overtimeBelongDateMap);
			}

			for (int dateIndex = calSumParam.dateBeginIndex; dateIndex <= calSumParam.dateEndIndex; dateIndex++) {
				// ��ǰ��
				UFLiteralDate curDate = calSumParam.allDates[dateIndex];
				if (!TBMPsndocVO.isIntersect(psndocList, curDate.toString()))// ��������޿��ڵ�����¼����������
					continue;
				// ���˴�����ձ�����
				String pk_daystat = calSumParam.daystatPKMap.get(pk_psndoc) == null ? null : calSumParam.daystatPKMap
						.get(pk_psndoc).get(curDate.toString());
				// ��ǰ��İ���Լ�ǰһ��ͺ�һ��İ��
				AggPsnCalendar curCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, curDate);// ��ǰ��İ��

				ShiftVO curShift = curCalendar == null ? null : curCalendar.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, curCalendar.getPsnCalendarVO()
								.getPk_shift());
				AggPsnCalendar preCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc,
						calSumParam.allDates[dateIndex - 1]);// ǰһ��İ��
				ShiftVO preShift = preCalendar == null ? null : preCalendar.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, preCalendar.getPsnCalendarVO()
								.getPk_shift());
				AggPsnCalendar nextCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc,
						calSumParam.allDates[dateIndex + 1]);// ��һ��İ��
				ShiftVO nextShift = nextCalendar == null ? null : nextCalendar.getPsnCalendarVO() == null ? null
						: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, nextCalendar.getPsnCalendarVO()
								.getPk_shift());
				TimeZone curTimeZone = CommonUtils.ensureTimeZone(calSumParam.timeRuleVO.getTimeZoneMap().get(
						dateOrgMap.get(curDate)));
				TimeZone preTimeZone = CommonUtils.ensureTimeZone(calSumParam.timeRuleVO.getTimeZoneMap().get(
						dateOrgMap.get(calSumParam.allDates[dateIndex - 1])));
				TimeZone nextTimeZone = CommonUtils.ensureTimeZone(calSumParam.timeRuleVO.getTimeZoneMap().get(
						dateOrgMap.get(calSumParam.allDates[dateIndex + 1])));
				// //����İ������ǵ��԰࣬����Ҫ�ȹ̻��������������㵥��ʱ��
				// 2013-06-14�̻��ᵽ���ݹ���ǰ�棨�Ӱ൥Ҫ�ù̻���Ľ����
				// if(curCalendar!=null&&curCalendar.getPsnCalendarVO().isFlexibleFinal()){
				// //����ʱ���
				// ITimeScope kqScope = ShiftVO.toKqScope(curShift, preShift,
				// nextShift, curDate.toString(),
				// curTimeZone,preTimeZone,nextTimeZone);
				// //�����̻��Ĳ���
				// SolidifyPara solidifyPara =
				// calSumParam.toSolidifyPara(pk_psndoc, curDate, kqScope);
				// //���й̻�
				// curCalendar.setPsnWorkTimeVO(SolidifyUtils.solidify(solidifyPara));
				// }
				// �Գ�������ݼ���𡢼Ӱ����ͣ���ֱ���д���
				// ���ڳ����������Ȼ�ս��д�����2008-07-24�յĳ���ʱ��������2008-07-24��0�㵽23:59:59֮���ʱ��
				// �����ݼ�����ͣ�����ð���ս��д�����2008-07-24�յ��ݼ٣�ͣ����ʱ�������ݼ٣�ͣ��������2008-07-24�����Ű�εĹ���ʱ���ڵ�ʱ�������ڹ����գ�
				// ��Ҫ�������Ƿ��Ϊ�ݼ٣���������ݼ٣��򲻴��������Ϊ�ݼ٣���Ҫ������������ʱ��������û�й���ʱ��εģ�
				// �ݼ��ڹ����յĴ���Ƚ����⣺�����Сʱ���㣬�����ʵ�ʵ��ݼ�ʱ�����������8����ȡΪ8������Ϊʵ��ʱ����
				// ���������㣬������м���
				// ��������12��ǰ����������12�㣩����죬12���ʼ������12�㣩����죬���������һ�죬��ͬһ�ŵ���һ���ڵ�ʱ�����ܳ���һ��
				// ����������Ĺ����գ���Сʱ����ܼ򵥣��Ͱ��ȡ���������ˡ�����ǰ�����㣬�������¹�����ȡ�빤��ʱ��εĽ���ʱ����Ȼ��
				// ���ݼ������־�����ݼ�������涨�壩�ǰ����������㻹�ǰ�������㣬��������������㣬����Թ�����ʱ�����ڿ��ڹ������涨�壩������ǰ�������㣬����԰��ʱ��
				// ���ȼ����ݼ����
				BillProcessHelper.processLeaveLength(calSumParam.leaveCopyVOs, leaveBillList, curCalendar, preShift,
						curShift, nextShift, preTimeZone, curTimeZone, nextTimeZone, statbVOList,
						calSumParam.datePeriodMap, calSumParam.paramValues, pk_daystat, calSumParam.timeRuleVO);
				// Ȼ�����������
				BillProcessHelper.processAwayLength(calSumParam.awayCopyVOs, awayBillList, curDate.toString(),
						curCalendar, preShift, curShift, nextShift, preTimeZone, curTimeZone, nextTimeZone,
						statbVOList, calSumParam.datePeriodMap, calSumParam.paramValues, pk_daystat,
						calSumParam.timeRuleVO);
				// Ȼ�����Ӱ����
				BillProcessHelper.processOvertiemLength(calSumParam.overCopyVOs, overtimeBillList, curDate.toString(),
						overtimeBelongDateMap, statbVOList, pk_daystat, calSumParam.timeRuleVO);
				// Ȼ�����ͣ��
				BillProcessHelper.processShutdownLength(calSumParam.shutCopyVOs, shutdownBillList, curCalendar,
						statbVOList, pk_daystat, calSumParam.timeRuleVO);
			}
		}
		// ��������ӱ���д�����ݿ⡣���е��ӱ��������ʼ�Ѿ�ɾ���ˣ�����������ɾ
		if (statbVOList.size() > 0) {
			String pk_org = calSumParam.pk_org;
			String pk_group = calSumParam.timeRuleVO.getPk_group();
			for (DayStatbVO vo : statbVOList) {
				vo.setPk_org(pk_org);
				vo.setPk_group(pk_group);
			}
			new BaseDAO().insertVOList(statbVOList);
		}

		/* �Ӱ���ʱ���ͼӰ�ת������ʱ�� �м��tbm_daystat_cacu�������� yejk #21266 start */
		String pk_org = calSumParam.pk_org;
		String cuserid = InvocationInfoProxy.getInstance().getUserId();
		// ��ͨ�����ݡ����� �Ӱ� pk_timeitem
		String[] pk_timeitems = { "1002Z710000000021ZLT", "1002Z710000000021ZLV", "1002Z710000000021ZLX" };
		String[] pk_psndocs = calSumParam.pk_psndocs;
		BaseDAO dao = new BaseDAO();
		StringBuffer sqlInsert = new StringBuffer(
				"insert into tbm_daystat_cacu(pk_daystat_cacu,pk_org,cuserid,pk_psndoc,pk_timeitem,hourcount,calendar,calType) values ");

		ISegDetailService segDetailService = NCLocator.getInstance().lookup(ISegDetailService.class);

		try {
			// OidGenerator.getInstance().nextOid() NC 20λ����
			// ɾ����ǰ�û����ڼ���Ļ�������
			// dao.executeUpdate("delete from tbm_daystat_cacu where cuserid = '"
			// + cuserid + "'");
			dao.executeUpdate("delete from tbm_daystat_cacu where pk_org = '" + pk_org + "' and pk_psndoc in("
					+ calSumParam.psndocInSQL + ")");
			for (int dateIndex = calSumParam.dateBeginIndex; dateIndex <= calSumParam.dateEndIndex; dateIndex++) {
				// ��ǰ��
				UFLiteralDate curDate = calSumParam.allDates[dateIndex];
				for (int i = 0; i < pk_timeitems.length; i++) {
					// ���ýӿڻ�ȡ�Ӱ����� ����MAP��key����Ϊ �����pk_psndocs����
					Map<String, UFDouble> ovtData = segDetailService.getOvertimeHoursByType(pk_org, pk_psndocs, null,
							null, curDate, pk_timeitems[i]);
					// ���ýӿڻ�ȡ�Ӱ�ת�������� ����MAP��key����Ϊ �����pk_psndocs����
					Map<String, UFDouble> ovtToRestData = segDetailService.getOvertimeToRestHoursByType(pk_org,
							pk_psndocs, null, null, curDate, pk_timeitems[i]);
					/*
					 * ���ovtData��ovtToRestData�е�key������ͬ �ɹ�������forѭ���������ݣ�
					 * ��ISegDetailService����ʵ��ϸ����δ֪���ȼٶ���ͬ �����б��ٴ��� yejk 180904
					 */
					for (String key : ovtData.keySet()) {
						String pk_psndoc = key;
						// ���м�����ɼӰ�����
						UFDouble overTime = ovtData.get(key);
						String valuesSql = "('" + OidGenerator.getInstance().nextOid() + "'" + "," + "'" + pk_org
								+ "'," + "'" + cuserid + "'" + "," + "'" + pk_psndoc + "'" + "," + "'"
								+ pk_timeitems[i] + "'" + "," + overTime.getDouble() + "," + "'" + curDate.toString()
								+ "','0')";// 0Ϊ�Ӱ�����
						dao.executeUpdate(sqlInsert.toString() + valuesSql);
						// ���м�����ɼӰ�ת��������
						overTime = ovtToRestData.get(key);
						valuesSql = "('" + OidGenerator.getInstance().nextOid() + "'" + "," + "'" + pk_org + "'," + "'"
								+ cuserid + "'" + "," + "'" + pk_psndoc + "'" + "," + "'" + pk_timeitems[i] + "'" + ","
								+ overTime.getDouble() + "," + "'" + curDate.toString() + "','1')";// 0Ϊ�Ӱ�ת��������
						dao.executeUpdate(sqlInsert.toString() + valuesSql);
					}
				}
			}
		} catch (DAOException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		/* �Ӱ���ʱ���ͼӰ�ת������ʱ�� �м��tbm_daystat_cacu�������� yejk #21266 end */
	}

	/**
	 * �����ձ���Ŀ�ļ��㣬������ʽ��java�Զ���
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
	 * һ��һ��ؼ���
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
			// ����ѭ��
			for (int i = calSumParam.dateBeginIndex; i <= calSumParam.dateEndIndex; i++) {
				UFLiteralDate date = calSumParam.allDates[i];
				// ���ձ���Ŀѭ��
				for (ItemCopyVO itemVO : dayItemVOs) {
					if (itemVO.getSrc_flag() == ItemVO.SRC_FLAG_MANUAL)// �ֹ�¼��
						continue;
					if (itemVO.getSrc_flag() == ItemVO.SRC_FLAG_FORMULA) {// ��ʽ����
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
							/* @res "�ձ���Ŀ��" */+ itemVO.getMultilangName()
									+ ResHelper.getString("6017hrta", "06017hrta0081")
									/* @res "�������,���鹫ʽ�����Ƿ���ȷ�� ���飺" */+ e.getMessage());

						}
						continue;
					}
					// java�����
					IDayDataCreator creator = calSumParam.dataCreatorMap.get(itemVO.getItem_code());
					if (creator == null)
						continue;
					// ����Աѭ��
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
	 * һ�μ���������
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
			// ���ձ���Ŀѭ��
			for (ItemCopyVO itemVO : dayItemVOs) {
				if (itemVO.getSrc_flag() == ItemVO.SRC_FLAG_MANUAL)// �ֹ�¼��
					continue;
				if (itemVO.getSrc_flag() == ItemVO.SRC_FLAG_FORMULA) {// ��ʽ����
					String updateSql = itemVO.getParsedFormula() + " where " + IBaseServiceConst.PK_ORG + "=?"
							+ " and " + DayStatVO.PK_PSNDOC + " in (" + psndocInSQL + ") and " + DayStatVO.CALENDAR
							+ " between ? and ?";
					try {
						dao.executeUpdate(updateSql, para);
					} catch (Exception e) {
						Logger.error(e.getMessage());
						throw new BusinessException(itemVO.getMultilangName()
								+ ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0098")
								/* @res "����ʱ�����������£�" */+ e.getMessage());
					}
					continue;
				}
				// java�����
				IDayDataCreator creator = calSumParam.dataCreatorMap.get(itemVO.getItem_code());
				if (creator == null)
					continue;
				if (session == null)
					session = new JdbcSession();
				// ����Աѭ��
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
		// ���������־û�
		para.clearParams();
		para.addParam(calSumParam.pk_org);
		para.addParam(pk_psndoc);
		para.addParam(date.toString());
		session.addBatch(updateSQL, para);// ����������
	}

	/**
	 * ��CalSumParam��ȡ��ĳ��ĳ������ձ���Ҫ�ĸ������������DayCalParam��
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
		// ���ڵ���
		param.psndocVO = TBMPsndocVO.findIntersectionVO(calSumParam.tbmPsndocMap.get(pk_psndoc), date.toString());
		// timedata
		Map<UFLiteralDate, TimeDataVO> timeDataMap = calSumParam.timedataMap.get(pk_psndoc);
		if (!MapUtils.isEmpty(timeDataMap))
			param.timeDataVO = timeDataMap.get(date);
		// �ֹ�����
		Map<UFLiteralDate, LateEarlyVO> lateEarlyMap = calSumParam.lateearlyMap.get(pk_psndoc);
		if (!MapUtils.isEmpty(lateEarlyMap)) {
			param.lateearlyVO = lateEarlyMap.get(date);
		}
		Map<UFLiteralDate, String> dateOrgMap = calSumParam.allDateOrgMap.get(pk_psndoc);
		// ǰһ�죬���죬��һ��İ���Լ���������
		AggPsnCalendar curCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, date);// ��ǰ��İ��
		ShiftVO curShift = curCalendar == null ? null : curCalendar.getPsnCalendarVO() == null ? null
				: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, curCalendar.getPsnCalendarVO()
						.getPk_shift());
		AggPsnCalendar preCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, preDate);// ǰһ��İ��
		ShiftVO preShift = preCalendar == null ? null : preCalendar.getPsnCalendarVO() == null ? null
				: ShiftServiceFacade.getShiftVOFromMap(calSumParam.shiftMap, preCalendar.getPsnCalendarVO()
						.getPk_shift());
		AggPsnCalendar nextCalendar = getAggPsnCalendarVO(calSumParam.calendarMap, pk_psndoc, nextDate);// ��һ��İ��
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
		// �ݼٵ�����Ϣ
		param.awayBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.awayMap, kqScope);
		param.leaveBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.leaveMap, kqScope);
		param.overtimeBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.overMap, kqScope);
		param.shutdownBills = DataFilterUtils.filterRegVOs(pk_psndoc, calSumParam.shutMap, kqScope);
		param.mergeTimeScopes = TimeScopeUtils.mergeTimeScopes(
				TimeScopeUtils.mergeTimeScopes(param.awayBills, param.leaveBills),
				TimeScopeUtils.mergeTimeScopes(param.overtimeBills, param.shutdownBills));
		param.lactationholidayVO = DataFilterUtils.filterDateScopeVO(pk_psndoc, calSumParam.lactationMap,
				date.toString());
		// ˢǩ����¼
		if (MapUtils.isNotEmpty(calSumParam.checkTimesMap)) {
			ICheckTime[] checkTimes = calSumParam.checkTimesMap.get(pk_psndoc);// ��
			param.checkTimes = DataFilterUtils.filterCheckTimes(kqScope, checkTimes);
			// ��Ȼ����ˢ����¼
			ITimeScope natualScope = TimeScopeUtils.toFull3Day(date.toString(), param.curTimeZone);
			param.naturalCheckTimes = DataFilterUtils.filterCheckTimes(natualScope, checkTimes);
		}

	}

	private static String getUpdateJavaItemSQL() {
		return "update tbm_daystat set {0}=? where " + DayStatVO.PK_ORG + "=? and " + DayStatVO.PK_PSNDOC + "=? and "
				+ DayStatVO.CALENDAR + "=?";
	}

	/**
	 * ��map��ȡ��ĳ��ĳ��İ��
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

		// ����ڼ���ձ��������޸�
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
				/* @res "�����޸��ѷ���ڼ�����ݣ�" */);
			}

			DayStatVO[] oldvos = null;
			InSQLCreator isc = new InSQLCreator();
			String condition = DayStatVO.PK_DAYSTAT + " in (" + isc.getInSQL(vos, DayStatVO.PK_DAYSTAT) + ") ";
			Collection oldc = new BaseDAO().retrieveByClause(DayStatVO.class, condition);
			if (CollectionUtils.isNotEmpty(oldc))
				oldvos = (DayStatVO[]) oldc.toArray(new DayStatVO[0]);
			new DayStatDAO().save(pk_org, vos);
			EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.DAYSTAT, IEventType.TYPE_INSERT_AFTER, vos));
			// ҵ����־
			TaBusilogUtil.writeDayStatEditBusiLog(vos, oldvos);
			return vos;
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	/*
	 * �����ձ��Ĳ�ѯ�����ǣ� ���Ȱ�����Ա������ȷʵ��ˣ����ǲ�����������ѯ��������Ա���ձ���Ȼ����Ա�������ŷ��飬��������֮��sum
	 * ע�⣬��������Ĳ�������Ҫ��deptPKs��Χ�ڡ������Ա����ϸ�����У�ĳ������ĳ��һ����ϸ��û�У���ô�˲��Ŵ����ڷ��ص�������������
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
		// �����������ӵ��ͻ��˵�������ȥ
		if (!ArrayUtils.isEmpty(deptPKs))
			fromWhereSQL = TBMPsndocSqlPiecer.addDeptPkArrayQuerySQL(deptPKs, fromWhereSQL);
		// return null;
		// ����Ȩ��
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		// �����ѯÿ�����Ա�ձ���Ȼ�󰴲��Ż���
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		DeptDayStatVO[] retArray = null;

		// ��ѯ����֯���е���/�±����ͣ�������ͣ�õ�,�Ȳ������Ϊ��һ����ѯ�Ĳ�������ʽÿ����ʱ�򶼲�һ��Ӱ��Ч��
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

		// ��ѯ����֯���е���/�±����ͣ�������ͣ�õ�,�Ȳ������Ϊ��һ����ѯ�Ĳ�������ʽÿ����ʱ�򶼲�һ��Ӱ��Ч��
		int itemClass = Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		ItemVO[] allItemVOs = NCLocator.getInstance().lookup(IItemQueryService.class)
				.queryItemByOrg(pk_org, itemClass, true);

		// ÿһ����˶��п��ܲ�һ�������Ҫһ��һ��ز�ѯ
		for (UFLiteralDate date : allDates) {
			retArray = (DayStatVO[]) ArrayUtils.addAll(retArray,
					queryByCondition(pk_org, fromWhereSQL, date, showNoDataRecord, allItemVOs));
		}
		return retArray;
	}

	/**
	 * ������������ѯһ����ձ�����
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
		// ���Ȳ�ѯ����һ�쿼�ڵ����ڱ���֯����Ա
		TBMPsndocVO[] psndocVOsInOrg = psndocService
				.queryByCondition(pk_org, fromWhereSQL, date, false, isforDeptQuery);
		// Ȼ���ѯ��һ�쿼�ڵ������ڱ���֯�����ǹ�����֯���ڿ��ڵ����ڵ����ã�������ί�У��ڱ���֯����Ա
		TBMPsndocVO[] psndocVOsInAdminOrg = psndocService.queryByCondition2(pk_org, fromWhereSQL, date, false,
				isforDeptQuery);
		if (ArrayUtils.isEmpty(psndocVOsInOrg) && ArrayUtils.isEmpty(psndocVOsInAdminOrg))
			return null;
		// ��ѯ��Щ��Ա���е��ձ���¼
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
			// ����֯���ձ�����
			DayStatVO[] retVOsInOrg = processDBVOs(psndocVOsInOrg, dbVOsInOrg, date, showNoDataRecord, isforDeptQuery);
			// ������֯�ڱ���֯���ձ�����
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
		// ����Ա���ݰ����ŷ��飬����˳���ܸı䡣�������ص�DeptDayStatVO[]��vo��˳��Ҫ��psnStatVOs�в��ŵ�˳��һ��
		List<DeptDayStatVO> retList = new ArrayList<DeptDayStatVO>();// list�������Ǳ�֤����vo��˳��
		Map<String, DeptDayStatVO> statMap = new HashMap<String, DeptDayStatVO>();// map��������ȡvo����
		for (DayStatVO psnVO : psnStatVOs) {
			String pk_dept = psnVO.getPk_dept();
			DeptDayStatVO deptVO = statMap.get(pk_dept);
			if (deptVO == null) {
				deptVO = new DeptDayStatVO();
				deptVO.setPk_dept(pk_dept);
				deptVO.setCalendar(date);

				// ���ð汾��Ϣ
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
	 * ��dbVOs���鴦��󷵻ء�dbVOs���������ݿ������е�����
	 * ���showNoDataRecordΪtrue����֤һ��һ����¼���������ݿ����Ƿ��У�û�о�newһ��daystatvo��
	 * ���showNoDataRecordΪfalse����Ҫ��dbVOs�������ֶ�Ϊ�յļ�¼ȥ����
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
		// dbVO��map��key��pk_psndoc
		Map<String, DayStatVO> dbVOMap = CommonUtils.toMap(DayStatVO.PK_PSNDOC, dbVOs);
		if (dbVOMap == null)
			dbVOMap = new HashMap<String, DayStatVO>();
		List<DayStatVO> retList = new ArrayList<DayStatVO>();
		// ��������ռ�¼
		if (!showNoDataRecord) {
			for (int i = 0; i < psndocVOs.length; i++) {
				TBMPsndocVO psndocVO = psndocVOs[i];
				String pk_psndoc = psndocVO.getPk_psndoc();
				DayStatVO dbVO = dbVOMap.get(pk_psndoc);
				if (dbVO == null)
					continue;
				if ((!dbVO.isNoDataRecord() && !isforDeptQuery) || (!dbVO.isNoDecimalDataRecord() && isforDeptQuery)) {
					retList.add(dbVO);
					// ������ְ����(�ձ����в�û�д洢��Ԫ��������)
					dbVO.setPk_psnjob(psndocVO.getPk_psnjob());
					dbVO.setPk_tbm_psndoc(psndocVO.getPrimaryKey());
					if (isforDeptQuery)
						dbVO.setPk_dept(psndocVO.getPk_dept());

					// ���ð汾��Ϣ
					dbVO.setPk_org_v(psndocVO.getPk_org_v());
					dbVO.setPk_dept_v(psndocVO.getPk_dept_v());
				}
			}
			return retList.size() == 0 ? null : retList.toArray(new DayStatVO[0]);
		}
		// ������ռ�¼����Ҫ��ÿ�˶�Ҫ��vo��û�еĻ�����Ҫnewһ��
		DayStatVO[] retArray = new DayStatVO[psndocVOs.length];
		for (int i = 0; i < psndocVOs.length; i++) {
			TBMPsndocVO psndocVO = psndocVOs[i];
			DayStatVO daystatVO = dbVOMap.get(psndocVO.getPk_psndoc());
			if (daystatVO != null) {
				retArray[i] = daystatVO;
				daystatVO.setPk_psnjob(psndocVO.getPk_psnjob());
				daystatVO.setPk_tbm_psndoc(psndocVO.getPrimaryKey());

				// ���ð汾��Ϣ
				daystatVO.setPk_org_v(psndocVO.getPk_org_v());
				daystatVO.setPk_dept_v(psndocVO.getPk_dept_v());

				if (isforDeptQuery)
					daystatVO.setPk_dept(psndocVO.getPk_dept());
				continue;
			}
			// ���û����newһ��
			daystatVO = new DayStatVO();
			retArray[i] = daystatVO;
			daystatVO.setCalendar(date);
			daystatVO.setPk_psndoc(psndocVO.getPk_psndoc());
			daystatVO.setPk_psnjob(psndocVO.getPk_psnjob());
			daystatVO.setPk_group(psndocVO.getPk_group());
			daystatVO.setPk_org(psndocVO.getPk_org());

			// ���ð汾��Ϣ
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
		// ���ά��Ȩ�ޣ�δ����ͳ�Ƹ�Ϊά��Ȩ��
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
		// ������ڷ�Χ���ձ�����������Ա
		SQLParamWrapper wrapper = TBMPsndocSqlPiecer.selectUnCompleteDailyDataByPsndocFieldAndDateFieldAndDateArea(
				context.getPk_org(), new String[] { TBMPsndocVO.PK_PSNDOC, TBMPsndocVO.PK_PSNJOB, TBMPsndocVO.PK_ORG },
				otherTableSelFields, "tbm_daystat daystat", "daystat.pk_org", "daystat.pk_psndoc", "daystat.calendar",
				beginDate.toString(), endDate.toString(), null, "daystat.dirty_flag='N'", fromWhereSQL);
		String sql = wrapper.getSql();
		SQLParameter para = wrapper.getParam();
		TBMPsndocVO[] retvos = CommonUtils.toArray(TBMPsndocVO.class,
				(List<TBMPsndocVO>) new BaseDAO().executeQuery(sql, para, new BeanListProcessor(TBMPsndocVO.class)));
		// ҵ����־
		TaBusilogUtil.writeDayStatUngenBusiLog(retvos, beginDate, endDate);
		return retvos;
	}

	/**
	 * �����ڶ�Ӧ���ڼ估��һ���ڼ��������ŵ�map�С�map��key�����ڣ�
	 * value��string���飬��һ��Ԫ�������������ڼ䣬�ڶ���Ԫ�������������ڼ����һ���ڼ�
	 * �������map��Ҫ��Ϊ�˴����ݼٺͳ���Ĳ��������ݿ��ڼ�ʱ���Ǽ��ڸ����ڼ仹�ǵ�һ���ڼ���ڵڶ����ڼ�
	 * 
	 * @param pkCorp
	 * @param dates
	 *            �����е����ڣ�Ҫ��ʱ���Ⱥ�˳������
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
		// �洢һ���ڼ����һ�ڼ��map,key��201101���֣�value��201102����
		// ���յõ���map������{2011-01-01=[201101,201102]}������{2011-01-01=null}
		for (UFLiteralDate date : dates) {
			for (int i = 0; i < periods.length; i++) {
				if ((date.before(periods[i].getEnddate()) || date.equals(periods[i].getEnddate()))// С�ڻ��ߵ����ڼ��������
						&& (date.after(periods[i].getBegindate()) || date.equals(periods[i].getBegindate()))) {// ���ڻ��ߵ����ڼ俪ʼ����
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
		// PeriodVO>();// key��200808�����ڼ䣬value��vo
		// if (firstPeriodVO != null) {
		// periodVOMap.put(firstPeriodVO.getTimeyear() +
		// firstPeriodVO.getTimemonth(), firstPeriodVO);
		// }
		// if (lastPeriodVO != null) {
		// periodVOMap.put(lastPeriodVO.getTimeyear() +
		// lastPeriodVO.getTimemonth(), lastPeriodVO);
		// }
		// // �洢һ���ڼ����һ�ڼ��map,key��201101���֣�value��201102����
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
	 * ��¼���л��ܲ������࣬�����е������Ŀ�������˵ĵ��ݵȵ�
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
		String psndocInSQL;// ��Ա������in
		// sql��ע�⣬psndocInSQL��pk_psndocsʵ�����ǵ�ͬ�ģ�ֻ���������ֻ��pk_psndocs������psndocInSQL�Ļ����ܶ�ط���Ҫ����pk_psndocs������ʱ���Ƚ��˷�
		UFLiteralDate[] allDates;
		int dateBeginIndex;
		int dateEndIndex;
		boolean containsPreviousDayFunc;// �ձ���Ŀ�Ĺ�ʽ�У��Ƿ�ʹ����ǰN���ձ��ĺ��������ʹ���ˣ����ձ���Ҫһ��һ����㡣���û��ʹ�ã������һ������
		boolean containsHolidayFunc;// �ձ���Ŀ�У��Ƿ�����ĿҪ��ѯ�������е���ϸ������еĻ�����Ҫ��ǰ����ϸ׼������ϸ����
		boolean containsDatePeriodVar;// �ձ���Ŀ�У��Ƿ������ڡ��������Ϣ������еĻ�����Ҫ���������ڼ�Ķ�Ӧ��ϵ׼����tbm_dateperiod����
		boolean containsOvertimeBeginEndTimeVar;// �ձ���Ŀ�У��Ƿ�ʹ���˼Ӱ�Ŀ�ʼʱ�䡢�Ӱ����ʱ�䣬��Ҫ������Ӱ൥�Ĺ����պͿ��ǿ�ʼ������ʱ���ʱ������׼����tbm_overtimebelong����

		LeaveTypeCopyVO[] leaveCopyVOs;
		AwayTypeCopyVO[] awayCopyVOs;
		OverTimeTypeCopyVO[] overCopyVOs;
		ShutDownTypeCopyVO[] shutCopyVOs;

		Map<String, Object> paramValues;// ����������ֵ��key�ǲ����ı���

		Map<String, IDayDataCreator> dataCreatorMap;// javaʵ�����map��key���ձ���Ŀ��code��ֻ��java�����Ŀput��ȥ����value��ʵ����

		ItemCopyVO[] dayItemVOs;// �ձ���Ŀ��Ҫ�󰴼���˳������
		Map<String, AggShiftVO> aggShiftMap;// ���а�ε�aggvo��map
		Map<String, ShiftVO> shiftMap;// ���а�ε�map
		Map<String, String[]> datePeriodMap;// ���ں͵��ڡ����ڶ�Ӧ��map
		BillMutexRule billMutexRule;// ���ݳ�ͻ����

		Map<String, LeaveRegVO[]> leaveMap;// ������Ա�����ڷ�Χ�ڵ��ݼټ�¼,���������
		Map<String, LeaveRegVO[]> lactationMap;// ������Ա�����ڷ�Χ�ڵĲ���ټ�¼
		Map<String, OvertimeRegVO[]> overMap;// ������Ա�����ڷ�Χ�ڵ��ݼټ�¼
		Map<String, AwayRegVO[]> awayMap;// ������Ա�����ڷ�Χ�ڵĳ����¼
		Map<String, ShutdownRegVO[]> shutMap;// ������Ա�����ڷ�Χ�ڵ�ͣ�����ϼ�¼
		Map<String, ICheckTime[]> checkTimesMap;// �����˵�ˢǩ����¼��Ҫ��ʱ���Ⱥ�˳������
		// ������Ա���Ű�
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap;
		// ������Ա��timedata����,��һ��string����Ա�������ڶ���UFLiteralDate�����ڣ�value��timedatavo
		Map<String, Map<UFLiteralDate, TimeDataVO>> timedataMap = null;
		// ������Ա��lateearly����
		Map<String, Map<UFLiteralDate, LateEarlyVO>> lateearlyMap = null;
		// ������Ա�Ŀ��ڵ�������,key����Ա������value�������ʱ���ڵĿ��ڵ���vo(һ�������ֻ��һ��������ж�������ô�Ѿ���ʱ���Ⱥ��ź���)
		Map<String, List<TBMPsndocVO>> tbmPsndocMap = null;
		// V6.1������������Ա����ְ��֯����map��key����Ա������value��key�����ڣ�value�Ǵ��˴�����ְ������ҵ��Ԫ������
		public Map<String, Map<UFLiteralDate, String>> allDateOrgMap;
		// ������Ա���ձ�����������,��һ��string����Ա����pk_psndoc,�ڶ���string�����ڣ�������string��pk_daystat�����map����Ҫ������Ϊ�����daystatb�ӱ�������׼��
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
				else {// ��̨����ʹ��ufliteralDate��key����������ڴ˴���һ��
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
	 * ���ݵ���
	 */
	@SuppressWarnings("unchecked")
	public <T extends IVOWithDynamicAttributes> DaystatImportParam importDatas(List<List<GeneralVO>> vosList,
			DaystatImportParam paramvo) throws BusinessException {
		if (CollectionUtils.isEmpty(vosList)) {
			return null;
		}
		// ���鿪ʼ�������ڷ�Χ�Ƿ�����ѷ��Ŀ����ڼ�,��������ѷ����ڼ�����ʾ������
		PeriodVO[] periodvos = PeriodServiceFacade.queryPeriodsByDateScope(paramvo.getContext().getPk_org(),
				paramvo.getBegindate(), paramvo.getEnddate());
		if (ArrayUtils.isEmpty(periodvos)) {
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0071")
			/* @res "��Ǹ,��ѡ��Ŀ������ڷ�Χ�ڲ����ڿ����ڼ�,������ѡ��!" */);
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
			/* @res "�ѷ��Ŀ����ڼ䲻�ܽ��е������!{0}" */, invalidPeroidMes.toString()));
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
		// �д�Ļ���ֱ�ӷ���
		if (!paramvo.isRightFormat())
			return paramvo;
		// ִ�е���ĳ־û�����
		try {
			// Ȩ�޹��ˣ��������ڵ�����ά��Ȩ��
			// ���ݶ�Ӧ�Ŀ��ڵ�����
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
			if (ViewOrderVO.FUN_TYPE_MONTH == paramvo.getReport_type()) {// �Ѿ���˵Ŀ����±����������޸�����
				TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
				if (timeRuleVO.getMreportapproveflag().booleanValue()) {// ��Ҫ��ˣ���Ҫ��������˵��±�
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
		// ����ǵ����ձ���Ҫ���¼�
		if (ViewOrderVO.FUN_TYPE_DAY == paramvo.getReport_type())
			EventDispatcher
					.fireEvent(new BusinessEvent(IMetaDataIDConst.DAYSTAT, IEventType.TYPE_INSERT_AFTER, datavos));
		succMes = ResHelper.getString("6017dayandmonthstat", "06017dayandmonthstat0073"
		/* @res "�ɹ������¼{0}��!" */, ArrayUtils.isEmpty(datavos) ? 0 + "" : datavos.length + "");
		paramvo.setSuccessMsg(succMes);
		// ҵ����־
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

		// ��ѯ����֯���е���/�±����ͣ�������ͣ�õ�,�Ȳ������Ϊ��һ����ѯ�Ĳ�������ʽÿ����ʱ�򶼲�һ��Ӱ��Ч��
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
		// ʹ��map��keysetû��˳�򲻺��ã�����һ��list
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

		// ��ѯ����֯���е���/�±����ͣ�������ͣ�õ�,�Ȳ������Ϊ��һ����ѯ�Ĳ�������ʽÿ����ʱ�򶼲�һ��Ӱ��Ч��
		int itemClass = Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		ItemVO[] allItemVOs = NCLocator.getInstance().lookup(IItemQueryService.class)
				.queryItemByOrg(pk_org, itemClass, true);
		DayStatVO[] retArray = null;
		String[] dates = days.toArray(new String[0]);
		// ÿһ����˶��п��ܲ�һ�������Ҫһ��һ��ز�ѯ,Ч�ʲ��ð�
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
		// true);//�������½�����ؿ�����Ŀ�����ڲ���Ӧ��û�ҵ����ش����ԭ��
		// �������ݺʹ����������˳��һ�µ������ݴ��󣨷�ҳmodel���µģ�
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

		// Ч���Ż���������Ĳ�ѯ����
		// // DayStatVO[] queryByCondition = queryByCondition(context,
		// fromWhereSQL, beginDate, endDate, showNoDataRecord);
		// UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate,
		// endDate);
		// fromWhereSQL =
		// TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		// DayStatVO[] dayStatVOs = null;
		//
		// // //��ѯ����֯���е���/�±����ͣ�������ͣ�õ�,�Ȳ������Ϊ��һ����ѯ�Ĳ�������ʽÿ����ʱ�򶼲�һ��Ӱ��Ч��
		// int itemClass =
		// Integer.parseInt(DayStatVO.class.getAnnotation(ItemClass.class).itemClass());
		// ItemVO[] allItemVOs =
		// NCLocator.getInstance().lookup(IItemQueryService.class).queryItemByOrg(context.getPk_org(),
		// itemClass, true);
		// //
		// //ÿһ����˶��п��ܲ�һ�������Ҫһ��һ��ز�ѯ
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
	 * �����ѯ�������Ż���ֻ��ѯpsndoc+����
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
		// ���Ȩ��
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);

		String pk_org = context.getPk_org();
		BaseDAO dao = new BaseDAO();
		// //��ѯ���еı���֯�Ŀ��ڵ���
		// String orgCondition = " begindate <= '" + endDate.toString() +
		// "' and enddate >= '" + beginDate.toString() + "' and pk_org = '" +
		// pk_org + "' ";
		//
		// String orgSql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL,
		// TBMPsndocVO.TABLE_NAME, orgCondition, null);
		// Collection<TBMPsndocVO> orgc = (Collection<TBMPsndocVO>)
		// dao.executeQuery(orgSql, null, new BeanListProcessor(
		// TBMPsndocVO.class));
		// //������֯����
		// String adminCondition = " begindate <= '" + endDate.toString() +
		// "' and enddate >= '" + beginDate.toString() + "' and pk_org <> '" +
		// pk_org + "' and pk_adminorg = '" + pk_org+ "' ";
		// String adminSql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL,
		// TBMPsndocVO.TABLE_NAME, adminCondition, null);
		// Collection<TBMPsndocVO> adminc = (Collection<TBMPsndocVO>)
		// dao.executeQuery(adminSql, null, new BeanListProcessor(
		// TBMPsndocVO.class));

		// ����֯�͹�����֯һ���ѯ
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

		if (showNoDataRecord) {// �����ʾ�����ݼ�¼����Ҫ��ѯ�ձ����ݣ�ֱ�ӹ���������pk_psndoc,date)����
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

		// �����ʾ�����ݼ�¼����Ҫ��ѯ�ձ����ݣ�ֱ�ӹ���������pk_psndoc,date)����
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
		// ֻ��ʾ�����ݵļ�¼����Ҫ��ѯ���ݿ���
	}

	// �ձ��쳣������Ϣ
	private void sendMessage(String pk_org, String[] pk_psndocs, CalSumParam calSumParam, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if (!calSumParam.timeRuleVO.isDayNotice() || !isHrssStarted())
			return;
		Set<String> noticePsnPkset = getExceptionPsns(pk_org, pk_psndocs, calSumParam, beginDate, endDate);// �쳣��Ա��������
		if (noticePsnPkset.size() <= 0) {
			return;
		}
		String[] noticePsnPks = (String[]) noticePsnPkset.toArray(new String[0]);
		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(noticePsnPks);
		String cond = "pk_psndoc in (" + insql + " )";
		// ��ѯ�����е�psndocvo
		PsndocVO[] psndocvos = (PsndocVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, PsndocVO.class, cond);
		if (ArrayUtils.isEmpty(psndocvos))
			return;
		// ��ѯ��֯
		OrgVO org = (OrgVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByPk(null, OrgVO.class, pk_org);
		// ��ѯ�±�
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(noticePsnPks);
		DayStatVO[] dayStatVOs = queryByCondition(pk_org, fromWhereSQL, beginDate, endDate, false);
		Map<String, DayStatVO[]> daystatMap = CommonUtils.group2ArrayByField(DayStatVO.PK_PSNDOC, dayStatVOs);

		HashMap<String, UserVO[]> userMap = NCLocator.getInstance().lookup(IUserPubService.class)
				.batchQueryUserVOsByPsnDocID(pk_psndocs, null);
		IURLGenerator IurlDirect = NCLocator.getInstance().lookup(IURLGenerator.class);
		IHRMessageSend messageSendServer = NCLocator.getInstance().lookup(IHRMessageSend.class);
		// ����Աѭ��������Ϣ
		for (PsndocVO psndocVO : psndocvos) {
			HRBusiMessageVO messageVO = new HRBusiMessageVO();
			DayStatVO[] dayStats = daystatMap.get(psndocVO.getPk_psndoc());
			messageVO.setBillVO(ArrayUtils.isEmpty(dayStats) ? null : dayStats[0]);// Ԫ�������Խ���
			messageVO.setMsgrescode(TaMessageConst.DAYSTATEXCPMSG);
			// ҵ�����
			Hashtable<String, Object> busiVarValues = new Hashtable<String, Object>();
			UserVO[] users = userMap.get(psndocVO.getPk_psndoc());
			SSOInfo ssinfo = new SSOInfo();
			if (!ArrayUtils.isEmpty(users)) {
				ssinfo.setUserPK(users[0].getCuserid());
			}
			ssinfo.setTtl(PubEnv.getServerTime().getDateTimeAfter(30));
			ssinfo.setFuncode("E20200910");// E20200910 �����±��Ĳ����ڵĹ��ܽڵ��

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

	// �õ��±��쳣��Ա
	private Set<String> getExceptionPsns(String pk_org, String[] pk_psndocs, CalSumParam calSumParam,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {

		Set<String> exceptionPsnPkSet = new HashSet<String>();
		ITBMPsndocQueryService tbmPsnQueryS = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);

		// ��ѯ���ڵ��� �ҵ���Ӧ�Ŀ��ڷ�ʽ����������or�ֹ����ڣ�
		Map<String, List<TBMPsndocVO>> tbmPsnMap = tbmPsnQueryS.queryTBMPsndocMapByPsndocs(pk_org, pk_psndocs,
				beginDate, endDate, true);
		if (MapUtils.isEmpty(tbmPsnMap)) {
			return null;
		}
		ITimeDataQueryService timDataQuery = NCLocator.getInstance().lookup(ITimeDataQueryService.class);
		// ������������
		Map<String, Map<UFLiteralDate, TimeDataVO>> machTimeDateMap = timDataQuery.queryVOMapByPsndocInSQLForMonth(
				pk_org, beginDate, endDate, calSumParam.psndocInSQL);
		// ѭ�����һ����������� ���쳣����������к��±��쳣�����ʼ�������ƥ��� ��� ����Ա��pk����ķ����ʼ��ļ�����
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
		// �ֹ���������
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
						// �ֹ�����û����;�����һ��,������һ�β�����Ҫ
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
	 * //�ж�һ�������Ƿ� �гٵ����� true Ϊ�гٵ�����
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
	 * //�ж�һ�������Ƿ� ���������� true Ϊ����������
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
	 * //�ж�һ�������Ƿ� �п����� true Ϊ������
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
		// �޸�˵���� Ӧ��ʹ������Ĵ��룬�����޸��� ICommonConst.MODULECODE_HRSS�Ĳ����������ã�ֻ����ʱʹ������Ĵ���
		// ע��v63Ҫ������ʹ������Ĵ���
		// �ж�����ģ���Ƿ�����
		// isstart= PubEnv.isModuleStarted(PubEnv.getPk_group(),"E202");
		return isstart;
	}
}