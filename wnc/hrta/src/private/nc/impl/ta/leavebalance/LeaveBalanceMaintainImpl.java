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
		// ���ݼ���ʱ��У���ڼ�ĺϷ���
		UFLiteralDate periodCheckDate = UFLiteralDate.getDate(calTime.getDate().toString());
		// PeriodServiceFacade.checkDateScope(pk_org, periodCheckDate,
		// periodCheckDate);
		PeriodServiceFacade.queryByDateWithCheck(pk_org, periodCheckDate);
		if (ArrayUtils.isEmpty(vos)) {
			return vos;
		}
		LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) NCLocator.getInstance().lookup(ITimeItemQueryService.class)
				.queryCopyTypesByDefPK(pk_org, pk_leavetype, TimeItemVO.LEAVE_TYPE);
		// �ų�����Ȩ�����
		List<LeaveBalanceVO> canCalList = new ArrayList<LeaveBalanceVO>();
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		IDataPermissionPubService perimssionService = NCLocator.getInstance().lookup(IDataPermissionPubService.class);
		Map<String, UFBoolean> perimssionMap = perimssionService.isUserhasPermissionByMetaDataOperation("60170psndoc",
				StringPiecer.getStrArrayDistinct(vos, LeaveBalanceVO.PK_TBM_PSNDOC), "CalcLeaveBalance", pk_group,
				pk_user);
		for (LeaveBalanceVO vo : vos) {
			// û�м���Ȩ�޵ģ��ٳ���
			if (perimssionMap != null && perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()) != null
					&& !perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()).booleanValue())
				continue;
			canCalList.add(vo);
		}
		if (canCalList.size() == 0)
			return vos;

		// ���� ���ֹ�ʱ������ټ��� ��ʱ�� ���� ���ڵ��� calculate()����֮ǰ���� yejk 2018-9-19 #21390
		try {
			generatePartWhLeaveTemp(pk_org, pk_leavetype, year, month, vos);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		// begin-˫��֧��-zhaozhaob-������������ڼ�������-20161117��20170111NC67�ϲ�������
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
		// ҵ����־
		TaBusilogUtil.writeLeaveBalanceCalculateBusiLog(retvos);
		return retvos;
	}

	/**
	 * #21390
	 * 
	 * @Description: ���� ���ֹ�ʱ������ټ��� ��ʱ�� ����
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
		// ��������ǰ��ɾ������������
		String deleteTempSql = "delete from tbm_partWhLeave_temp";
		basedao.executeUpdate(deleteTempSql);
		String creator = InvocationInfoProxy.getInstance().getUserId();
		// UFDateTime date = new UFDateTime();
		/**
		 * // 1 ȡ���� �㹤�� �� ���µ� �����ڼ� ȡ�������ڼ�ȥ workagestartdate н�������գ���ְ���ڣ� // 2
		 * ȡ������ݱ�־ // 3 ���� ÿ���� Ӧ��ȡ���ݵ� ����������ʼʱ��ͽ���ʱ�� // 4 ������Ա����ٷֱȣ���������Ա ����1
		 * ��100%��
		 * 
		 */

		// ��ȡ��ǰ������ԱPK List
		List<String> psnList = new ArrayList<String>();
		for (LeaveBalanceVO vo : vos) {
			psnList.add(vo.getPk_psndoc());
		}
		// InSQLCreator isc = new InSQLCreator() getInSQL new MapListProcessor()
		// ��ȡ ��Ա�Ƿ����ݱ�־ н�������գ���ְ���ڣ�
		InSQLCreator isc = new InSQLCreator();
		String inSql = isc.getInSQL(psnList.toArray(new String[0]));
		String queryFlagSql = "select t1.pk_psndoc,t1.is_special_timer,t2.workagestartdate from bd_psndoc "
				+ "t1,hi_psnorg t2 where t1.pk_psndoc = t2.pk_psndoc and t2.begindate = (select max(t3.begindate) from hi_psnorg t3 "
				+ "where t2.pk_psndoc = t3.pk_psndoc) and t1.pk_psndoc in (" + inSql + ")";
		@SuppressWarnings("unchecked")
		List<Map<String, String>> resultList = (List<Map<String, String>>) basedao.executeQuery(queryFlagSql,
				new MapListProcessor());
		// �����ת�� ��Աpk�� �Ƿ����ݱ�־��н�������յ�MAP String[0]Ϊ��־ String[1]Ϊ����
		Map<String, String[]> psnToFlagMap = new HashMap<String, String[]>();
		for (int i = 0; i < resultList.size(); i++) {
			Map<String, String> map = resultList.get(i);
			String[] strs = new String[2];
			strs[0] = map.get("is_special_timer");
			strs[1] = map.get("workagestartdate");
			psnToFlagMap.put(map.get("pk_psndoc"), strs);
		}
		// ��ȡ��ǰ���㿼���ڼ� ��������
		month = month.length() < 2 ? "0" + month : month;
		String kaoqinEndDateStr = year + "-" + month + "-25";// ���ø�Ĭ��ֵΪ ��ǰ�������� ��
																// 25 ��
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String queryEndDateSql = "select t.enddate from tbm_period t where t.pk_org = '" + pk_org
				+ "' and t.accyear = '" + year + "' and t.accmonth = '" + month + "'";
		String str = (String) basedao.executeQuery(queryEndDateSql, new ColumnProcessor());
		if (null != str) {
			kaoqinEndDateStr = str;
		}
		Date kaoqinEndDate = sdf.parse(kaoqinEndDateStr);
		/*
		 * ���� ÿ���� ���ݼ� ���� ������� ����������ʱ��tbm_partWhLeave_temp
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
				// JdbcSession �� insertʱ �Զ���Ӳ����ֶ� ts
				parameter.addParam(creator);
				parameter.addParam(pk_psndoc);
				String workStartDateStr = psnToFlagMap.get(pk_psndoc)[1];

				if ("Y".equals(psnToFlagMap.get(pk_psndoc)[0])) {// ��������Ա���
					Date workStartDate = sdf.parse(workStartDateStr);
					Calendar calStartDate = Calendar.getInstance();
					calStartDate.setTime(workStartDate);

					// calStrartDate��workStartDate�� ���� 6����
					calStartDate.add(Calendar.MONTH, 6);
					// �õ�6���º������
					Date addSixWorkStartDate = calStartDate.getTime();
					String addSixWorkStartDateStr = sdf.format(addSixWorkStartDate);

					// calStrartDate��workStartDate�� �ټ��� 6����
					calStartDate.add(Calendar.MONTH, 6);
					// �õ�12���º������
					Date addTwelveWorkStartDate = calStartDate.getTime();
					String addTwelveWorkStartDateStr = sdf.format(addTwelveWorkStartDate);

					if (addSixWorkStartDate.after(kaoqinEndDate)) {
						// 1��н���������ڼ���6���º���ڵ�ǰ�����ڼ�������ڣ�����ְʱ��С��6���£�����Ϊ0
						// ���� insert ��������
						parameter.addParam(0);
					} else if (addSixWorkStartDate.before(kaoqinEndDate) && addTwelveWorkStartDate.after(kaoqinEndDate)) {
						/*
						 * 2��н���������ڼ���6���º�С�ڵ�ǰ�����ڼ�������ڣ���н���������ڼ���12���´��ڵ�ǰ�����ڼ��������
						 * ����ְʱ�����6������С��1�꣬����ܹ�ʱ��ʼ����Ϊ
						 * н����������workStartDate����������ΪaddSixWorkStartDate
						 * ��Ա�����������н��й�ʱ���ܣ�Ȼ����������
						 */
						// ��ȡ��ʱ����
						String sumSql = "select sum(t1.gzsj) from tbm_psncalendar t1 where t1.pk_psndoc = '"
								+ pk_psndoc + "' and t1.calendar >= '" + workStartDateStr + "' and t1.calendar <= '"
								+ addSixWorkStartDateStr + "' and t1.pk_org = '" + pk_org + "'";
						double sumHour = 0;
						Object obj = basedao.executeQuery(sumSql, new ColumnProcessor());
						if (null != obj) {
							sumHour = Double.valueOf(obj.toString());
						}
						// ������� ���깤ʱ����(����Լ26�ܣ�ÿ��40��ʱ)
						double percent = sumHour / (26 * 40);
						// ����������λС��
						DecimalFormat dFormat = new DecimalFormat("#.00");
						String cStr = dFormat.format(percent);
						percent = Double.valueOf(cStr);
						// ���� insert ��������
						parameter.addParam(percent);
					} else {
						// calStrartDate��workStartDate�� �ټ��� 12���� ��ǰ���Ѽ�6+6=12���£�
						calStartDate.add(Calendar.MONTH, 12);
						// �õ�24���£�2�꣩�������
						Date addTwentyWorkStartDate = calStartDate.getTime();
						String addTwentyWorkStartDateStr = sdf.format(addTwentyWorkStartDate);
						if (addTwelveWorkStartDate.before(kaoqinEndDate) && addTwentyWorkStartDate.after(kaoqinEndDate)) {
							/*
							 * 3.1
							 * н���������ڼ���12���º�С�ڵ�ǰ�����ڼ�������ڣ���н���������ڼ���24���´��ڵ�ǰ�����ڼ��������
							 * ����ְʱ�����1�굫С��2�꣬���ܹ�ʱ��ʼ����Ϊ н����������workStartDate
							 * ��������ΪaddTwelveWorkStartDate��һ���
							 */
							// ��ȡ��ʱ����
							String sumSql = "select sum(t1.gzsj) from tbm_psncalendar t1 where t1.pk_psndoc = '"
									+ pk_psndoc + "' and t1.calendar >= '" + workStartDateStr
									+ "' and t1.calendar <= '" + addTwelveWorkStartDateStr + "' and t1.pk_org = '"
									+ pk_org + "'";
							double sumHour = 0;
							Object obj = basedao.executeQuery(sumSql, new ColumnProcessor());
							if (null != obj) {
								sumHour = Double.valueOf(obj.toString());
							}
							// ������� һ�깤ʱ����(һ��Լ52�ܣ�ÿ��40��ʱ)
							double percent = sumHour / (52 * 40);
							// ����������λС��
							DecimalFormat dFormat = new DecimalFormat("#.00");
							String cStr = dFormat.format(percent);
							percent = Double.valueOf(cStr);
							// ���� insert ��������
							parameter.addParam(percent);
						} else {
							/*
							 * 3.2 ���һ����� ����ְʱ�� ���� 2 �� ���ܽ�������Ϊ ��ǰ�����ڼ�������� ��Ӧ���
							 * ��ְ���� ���磺н�������գ���ְ�գ�2016-03-21 ��ǰ�����ڼ�������� Ϊ
							 * 2018-05-25 �� ���ܽ�������Ϊ 2018-03-21 ��ʼ����Ϊ ������ ��ǰһ�� ��
							 * 2017-03-21
							 */
							// �����������
							String[] args = sdf.format(workStartDate).split("-");
							String sumEndDateStr = year + "-" + args[1] + "-" + args[2];
							// ���㿪ʼ����
							int startYear = Integer.valueOf(year) - 1;
							String sumStartDateStr = String.valueOf(startYear) + "-" + args[1] + "-" + args[2];

							// ��ȡ��ʱ����
							String sumSql = "select sum(t1.gzsj) from tbm_psncalendar t1 where t1.pk_psndoc = '"
									+ pk_psndoc + "' and t1.calendar >= '" + sumStartDateStr + "' and t1.calendar <= '"
									+ sumEndDateStr + "' and t1.pk_org = '" + pk_org + "'";
							double sumHour = 0;
							Object obj = basedao.executeQuery(sumSql, new ColumnProcessor());
							if (null != obj) {
								sumHour = Double.valueOf(obj.toString());
							}
							// ������� һ�깤ʱ����(һ��Լ52�ܣ�ÿ��40��ʱ)
							double percent = sumHour / (52 * 40);
							// ����������λС��
							DecimalFormat dFormat = new DecimalFormat("#.00");
							String cStr = dFormat.format(percent);
							percent = Double.valueOf(cStr);
							// ���� insert ��������
							parameter.addParam(percent);
						}

					}
				} else {// ����������Ա��� ���� Ϊ1 ��100%��
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
		// �Ƿ������Y������
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
		// һ������£�psnorg���е���ְ��������ֵ�ģ���Ҳ���ų�û��ֵ���������������������������ʹ�ô���֯��ϵ��¼��creationtime��Ϊ��ְ����(�����ǲ��Եģ����Ǳ���Ҫ����ְ���ڣ�ֻ����ô��)
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
	 *            ��true����ʾ�����vos�п������ݿ��л�û�У���Ҫensureһ�£�false����ʾvos�϶��У�����Ҫensure
	 * @return
	 * @throws BusinessException
	 */
	protected LeaveBalanceVO[] calculate(String pk_org, LeaveTypeCopyVO typeVO, String year, String month,
			LeaveBalanceVO[] vos, UFDateTime calTime, boolean needEnsure) throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return vos;
		}
		if (typeVO.getIslactation() != null && typeVO.getIslactation().booleanValue()) {// ����ǲ���٣�����Ҫ����
			return vos;
			// throw new
			// BusinessException("lactation holiday can not be computed!");
		}
		// ����ǰ��������ְ�ս��㣬��һ��֯��ϵһ��һ����¼������ǰ��ڼ���㣬��һ��֯��ϵһ�ڼ�һ����¼
		// ������ݿ��������ݣ���insert
		String[] lockables = new String[vos.length];
		// ����HR��֯�ڣ���Щ�˵�����ݼ������ס
		for (int i = 0; i < vos.length; i++) {
			lockables[i] = "leavebalance" + pk_org + typeVO.getPk_timeitem() + vos[i].getPk_psnorg();
		}
		PKLock lock = PKLock.getInstance();

		try {
			// modify xw ����ʱ�ܱ����ݱ���ס��δ���ҵ���ԭ�򣬹ʸ�Ϊֻ��һ������ʱ������ 20171205
			boolean acquired = vos.length == 1 ? true : lock.acquireBatchLock(lockables, PubEnv.getPk_user(), null);
			// boolean acquired = lock.acquireBatchLock(lockables,
			// PubEnv.getPk_user(), null);
			if (!acquired)
				throw new BusinessException(ResHelper.getString("6017leave", "06017leave0253")
				/* @res "�������������޸Ļ��������ڽ��м��ڼ��㣬���Ժ�����!" */);
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
			UFLiteralDate periodBeginDate = null;// �ڼ俪ʼ�պ��ڼ�����գ����ڰ��ꡢ�ڼ�������Ч�����ڰ���ְ�ս������Ч
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
				// ����Ƿ�Ӱ�ת���ݣ�����Ҫ�������к�ʵ�����У��Ӱ�ת���ݵ����к�ʵ�����ж�����ת���ݡ���ת����ʱ���ɣ�
				if (!typeVO.getTimeitemcode().equals(TimeItemCopyVO.OVERTIMETOLEAVETYPE))
					calCurRealDayOrHour(pk_org, typeVO, year, month, periodBeginDate, periodEndDate, filteredVOs,
							inSQL, calTime);
				else
					balanceDAO.syncFromDB4DataExists(filteredVOs);// ����ǼӰ�ת���ݣ������к�ʵ������ҲҪ�����ݿ���ͬ��һ�£���ֹ��̨�ͽ��治һ��
				calYiRestFreezeDayOrHour(pk_org, typeVO, year, month, periodBeginDate, periodEndDate, filteredVOs,
						inSQL, calTime);

				// �ݴ�����ΪһЩԭ�����ݼټ��������е�hirebegindate��hireenddateΪ�գ��˴�����
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
	 * ˫��֧��-zhaozhaob-������������ڼ������⣨��ʱ����ִ�У�-20161117
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
	 * �������ݣ����صĽ����ֻ��δ�����
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
		// leavesetperiod==LeaveTypeCopyVO.LEAVESETPERIOD_YEAR;//�Ƿ������

		for (int i = 0; vos != null && i < vos.length; i++) {
			vos[i].setPk_timeitem(pk_leavetype);
			vos[i].setCuryear(year);
			if (TimeItemCopyVO.LEAVESETPERIOD_MONTH == typeVO.getLeavesetperiod().intValue()) {
				vos[i].setCurmonth(month);
			} else {
				vos[i].setCurmonth(null);
			}

			// Ĭ��ֵ
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
		// ҵ����־
		TaBusilogUtil.writeLeaveBalanceEditBusiLog(vos, oldvos);
		return setDefaultTimeitemCopy(vos);
	}

	/**
	 * ���칫ʽ��������Ҫ�Ĳ���
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
		PeriodVO periodVO = PeriodServiceFacade.queryByDate(pk_org, calDate);// ���������������ڼ�
		para.setCalDateBelongToPeriod(periodVO);
		PeriodVO previousPeriodVO = PeriodServiceFacade.queryPreviousPeriod(pk_org, calDate);// �������������ڵ���һ�������ڼ�
		para.setPreviousCalDateBelongToPeriod(previousPeriodVO);
		int settlePeriod = typeVO.getLeavesetperiod().intValue();
		if (settlePeriod == TimeItemCopyVO.LEAVESETPERIOD_MONTH) {// ����ǰ��ڼ���㣬��Ҫ���year,month��Ӧ���ڼ�
			PeriodVO calPeriodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
			para.setCalPeriod(calPeriodVO);
			PeriodVO previousCalPeriodVO = PeriodServiceFacade.queryPreviousPeriod(pk_org, year, month);
			para.setPreviousCalPeriod(previousCalPeriodVO);
		}
		// ���year��Ӧ�������ڼ�
		para.setCalYearPeriods(PeriodServiceFacade.queryByYear(pk_org, year));
		para.setPreviousCalYearPeriods(PeriodServiceFacade.queryByYear(pk_org,
				Integer.toString(Integer.parseInt(year) - 1)));
		para.setCalYear(year);
		para.setCalMonth(month);
		// ����ʱ�̵��������ڼ����Ա״̬��ʱ���
		UFLiteralDate calPsnDate = null;
		UFLiteralDate periodEndDate = null;

		if (settlePeriod == TimeItemCopyVO.LEAVESETPERIOD_MONTH) {// ���ڼ�����
			PeriodVO calPeriodVO = para.getCalPeriod();
			periodEndDate = calPeriodVO.getEnddate();
		} else {
			periodEndDate = para.getCalYearEndDate();
		}
		if (periodEndDate == null) {
			calPsnDate = calDate;
		} else if (periodEndDate.after(calDate)) {// �������������ڼ�ĺ�������Ա��״̬ȡ�ڼ�����һ���״̬
			calPsnDate = calDate;
		} else {
			calPsnDate = periodEndDate;
		}
		para.setCalPsnDate(calPsnDate);
		return para;
	}

	/**
	 * ����ĳ���ݼ���������ʱ����ʵ������ʱ�� �����vo�϶���δ�����
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
			// 2012.05.16�����������ۺ�ȷ���������ʽû���κ����ݣ��򲻼������С���ǰ���У����û��ڼ��ڼ��㴦��д������Ϊ׼
			boolean isBlank = StringUtils.isBlank(formula);
			if (isBlank)
				return;
			boolean isDecimal = false;// ��ʽ�Ƿ���һ��������
			try {
				Double.parseDouble(formula.trim());
				isDecimal = true;
			} catch (NumberFormatException nfe) {

			}
			String parsedFormula = null;
			boolean isZero = isDecimal && Double.parseDouble(formula.trim()) == 0.0;
			// �������ڣ���Ҫ������ʱ����HR��֯��ʱ�����бȶ�
			TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
			TimeZone timeZone = timeRuleVO.getTimeZone();
			UFLiteralDate calDate = UFLiteralDate.getDate(calculateTime.toStdString(timeZone).substring(0, 10));
			// �������ʱ�仹û���ڼ��һ�죬������Ϊ�ڼ��һ�죬����û����ǰ���
			if (periodBeginDate != null && calDate.before(periodBeginDate))
				calDate = periodBeginDate;
			if (isZero)
				parsedFormula = "0.0";
			else if (isDecimal)
				parsedFormula = formula;
			else {// �й�ʽ��ʱ�򣬲�ʹ�ù�ʽ����
				String parserFilePath = "/hr/ta/formula/parserfiles/leaveruleparserfiles.xml";
				is = ParaHelper.class.getResource(parserFilePath).openStream();
				IFormulaParser parser = new XMLFormulaParser(is);
				// ׼����ʽ����Ĳ��������������/ʱ��/�ڼ�/����/��ǰ���/��һ��ȵ���Ϣ
				LeaveFormulaCalParam param = createFormulaPara(pk_org, typeVO, year, month, calDate, calculateTime);
				// ssx added on 2017-11-24
				// �˜ʮaƷδ̎�������ڽY�㡢����Ӌ���Ӌ������
				// Ӌ�����ڴ���������ȽY�����ڣ�����̎�������ȽY������
				// ���С��������ȽY�����ڣ�����ϵ�y����
				if (typeVO.getLeavesetperiod().equals(LeaveTypeCopyVO.LEAVESETPERIOD_DATE) // �Y���L�ڣ��������ڽY��
						&& typeVO.getLeavescale().equals(LeaveTypeCopyVO.LEAVESCALE_YEAR // ����Ӌ�㷽ʽ������Ӌ��
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
			// ����ǰ��ڼ���㣬�����ǡ�����/��ְ�ս��� and ������㡱����ǰ����=���У�������Ҫ���յ�ǰ�·����㵱ǰʵ������
			String updateSQL = "update " + LeaveBalanceVO.getDefaultTableName() + " set " + LeaveBalanceVO.CURDAYORHOUR
					+ "= isnull(" + parsedFormula + ",0)";
			if (isZero)
				updateSQL += "," + LeaveBalanceVO.REALDAYORHOUR + "=0.0 ";
			updateSQL += where;

			dao.executeUpdate(updateSQL);

			// v63��ӣ�����ת��ʱ������ѡ����ת�Ʊ�ʾ��������Ϊ�գ��㣩
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
			// �����ߵ����˵������!=0������Ҫ���������㵱ǰʵ������
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
	 * ���㵱ǰʵ������ʱ����
	 * ʹ�����㷨�������߼��ǣ�ʵ������ʱ��=(length(later(�ڼ��һ�죬��ְ��),earlier(�ڼ����һ�죬������
	 * )))/length(���ڼ�)*���� ���ڰ���ְ�ս����������ڼ��һ�첻���ܱ���ְ����
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
		// ����ǰ�����㣬�Ҽ����ջ�δ���ڼ��һ�죬��ǰʵ�����п϶���0
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
		// ����ְ�ս���
		calRealDayORHour4HirePeriod(pk_org, typeVO, year, vos, pkInSQL, calculateTime, calculateDate);
	}

	/**
	 * ��������ʱ��������ʱ��������ʱ��
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
		// ���ڹ���
		// TimeRuleVO timeRuleVO =
		// NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		// BillMutexRule billMutexRule =
		// BillMutexRule.createBillMutexRule(timeRuleVO.getBillmutexrule());
		// //���а��
		// Map<String,AggShiftVO> aggShiftMap =
		// ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		// Map<String, LeaveTypeCopyVO> typeMap = new HashMap<String,
		// LeaveTypeCopyVO>();
		// typeMap.put(typeVO.getPk_timeitem(), typeVO);

		// �Ż�����ѯ��leaveindex���飬������ѯ
		Map<Integer, LeaveBalanceVO[]> indexMap = CommonUtils.group2ArrayByField(LeaveBalanceVO.LEAVEINDEX, vos);
		for (Integer leaveindex : indexMap.keySet()) {
			LeaveBalanceVO[] indexvos = indexMap.get(leaveindex);
			if (ArrayUtils.isEmpty(indexvos))
				continue;
			// ��ѯ�����ڼ��ڵ���������ͨ���ĵ��ݺ͵Ǽǵ��ݣ�
			String[] pk_psnorgs = StringPiecer.getStrArray(indexvos, LeaveBalanceVO.PK_PSNORG);
			LeaveRegVO[] regVOs = null;
			try {
				regVOs = LeaveServiceFacade.queryByPsnsLeaveTypePeriod(pk_org, pk_psnorgs, typeVO, year, month,
						leaveindex);
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
				throw new BusinessException("����Ӌ����ewangywt��" + e.getMessage(), e);
			}
			// ��ѯ�����ڼ��ڵ�����̬�������е����뵥
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
				// ����=���ڽ���+��ǰʵ������-����
				// vo.setRestdayorhour(new
				// UFDouble(vo.getLastdayorhour().doubleValue()+vo.getRealdayorhour().doubleValue()-result));
				// ���ݸۻ����󣺽���=���ڽ���+��ǰʵ������-���� + ����ʱ��
				double changeLength = vo.getChangelength() == null ? 0.0 : vo.getChangelength().doubleValue();
				vo.setRestdayorhour(new UFDouble(vo.getLastdayorhour().doubleValue()
						+ vo.getRealdayorhour().doubleValue() - result + changeLength));
				// v63�����������ǿ�ת�ƵĻ�Ҫ������ת��ת��ʱ����2013-03-04�޸ĸ���ת�Ʊ�ʶ�ж�
				// if(typeVO.getIsleavetransfer()!=null&&typeVO.getIsleavetransfer().booleanValue()){
				if (vo.getTransflag() != null && vo.getTransflag().booleanValue()) {
					double in = vo.getTranslatein() == null ? 0 : vo.getTranslatein().doubleValue();
					double out = vo.getTranslateout() == null ? 0 : vo.getTranslateout().doubleValue();
					vo.setRestdayorhour(new UFDouble(vo.getRestdayorhour().doubleValue() + in - out));
				}
				// ���㶳��ʱ��
				// double freeze =
				// BillProcessHelperAtServer.calFreezeLeaveLength(leavebVOs2,
				// pk_org, typeVO, timeRuleVO, billMutexRule, aggShiftMap);
				double freeze = getSumValue(leavebVOs2);
				vo.setFreezedayorhour(new UFDouble(freeze));
				vo.setCalculatetime(calculateTime);
			}
		}
		// �Ż�ʱɾ������������ķ������鴦��
		// for(LeaveBalanceVO vo:vos){
		// double result =
		// BillProcessHelperAtServer.calConsumedLeaveLength(pk_org,
		// vo.getPk_psnorg(), typeVO,
		// year, month,vo.getLeaveindex(), timeRuleVO, billMutexRule,
		// aggShiftMap);
		// vo.setYidayorhour(result==0?UFDouble.ZERO_DBL:new UFDouble(result));
		// //����=���ڽ���+��ǰʵ������-����
		// vo.setRestdayorhour(new
		// UFDouble(vo.getLastdayorhour().doubleValue()+vo.getRealdayorhour().doubleValue()-result));
		// //���㶳��ʱ��
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
	 * �����ݼٵ����ݼ�ʱ���ܺ�
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
	 * ���㰴�����-���¼���ĵ�ǰʵ������
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
		// ���ݸۻ���������б����
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
				psnMonthDiff++;// Ա������ڿ����ڼ��һ��������Ȼ����ְ��������ȫ�٣���һ����ְ�ſ۳�һ����
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
		// 2012.4���������ۺ���������¼���ʱ���ġ����¿�ʼ���������¶�ȡ�Ϊ�����¿�ʼ�����е��¶�ȡ�
		return (endDate.getYear() - beginDate.getYear()) * 12 + endDate.getMonth() - beginDate.getMonth() + 1;
	}

	/**
	 * ���㰴��ְ�ս���-���¼���ĵ�ǰʵ������
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
			// ����ְ�ս���ʱ��ÿ����Ա���ڼ䶼��Ҫ�������㣺
			// �ڼ��һ�죺�ڼ����=year,���ղ���=��ְ�����ղ��֣�
			// �ڼ����һ�죺�ڼ��һ��һ���
			// ���磬������ְ��Ϊ2011-03-01����ô������2011��Ⱦ���2011-03-01-2012-02-29
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
	 * ������ְ���ڡ���ȣ��������ְ��ĵ�һ�� ���磬��ְ������2008-03-03����ô2011�����ְ��ĵ�һ�����2011-03-03
	 * 
	 * @param year
	 * @param hireDate
	 * @return
	 */
	protected UFLiteralDate getHireBeginDate(String year, UFLiteralDate hireDate) {
		return getDateInYear(year, hireDate);
	}

	/**
	 * ������ְ���ڡ���ȣ��������ְ������һ�� ���磬��ְ������2008-03-03����ô2011�����ְ������һ�����2012-03-02
	 * 
	 * @param year
	 * @param hireDate
	 * @return
	 */
	protected UFLiteralDate getHireEndDate(String year, UFLiteralDate hireDate) {
		String hireDateStr = hireDate.toString();
		// �������֮ǰ���� ����У�� ��Ϊ ƽ��+2��29����ϻᱨ��
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
	 * ��date�����ڲ�����yearƴ��
	 * 
	 * @param year
	 * @param date
	 * @return
	 */
	protected UFLiteralDate getDateInYear(String year, UFLiteralDate date) {
		String dateStr = date.toString();
		// �������֮ǰ���� ����У�� ��Ϊ ƽ��+2��29����ϻᱨ��
		if (!UFLiteralDate.isLeapYear(Integer.valueOf(year)) && "-02-29".equals(dateStr.substring(4, dateStr.length()))) {
			return UFLiteralDate.getDate(year + "-"
					+ date.getDateBefore(1).toString().substring(5, (date.getDateBefore(1).toString()).length()));
		} else {
			return UFLiteralDate.getDate(year + "-" + dateStr.substring(5, dateStr.length()));
		}
	}

	/**
	 * ��ѯ��ְ���ڣ���map���أ�key��pk_psnorg��value����Ա��֯��ϵ����ְ��
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
		// ���Ȳ�ѯ��Щ��Ա����ְ��
		String[] psnorgs = SQLHelper.getStrArray(vos, LeaveBalanceVO.PK_PSNORG);
		return queryHireDate(psnorgs);
	}

	/**
	 * ��ѯһ����Ա����ְ���ڣ�
	 * 
	 * @param psnorgInSQL
	 *            ��pk_psnorg��insql
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
			// ��Ա����֯��ϵvo������ȡ��ְ���ڡ�
			psnorgVOs = CommonUtils.retrieveByClause(PsnOrgVO.class, PsnOrgVO.PK_PSNORG + " in(select "
					+ TempTableVO.IN_PK + " from " + psnorgInSQL + ")");
		} finally {
			isc.clear();
		}
		if (ArrayUtils.isEmpty(psnorgVOs))
			return retMap;
		for (PsnOrgVO psnOrgVO : psnorgVOs) {
			// һ������£�psnorg���е���ְ��������ֵ�ģ���Ҳ���ų�û��ֵ���������������������������ʹ�ô���֯��ϵ��¼��creationtime��Ϊ��ְ����(�����ǲ��Եģ����Ǳ���Ҫ����ְ���ڣ�ֻ����ô��)
			retMap.put(psnOrgVO.getPk_psnorg(), getHireStartDate(psnOrgVO));
		}
		return retMap;
	}

	/*
	 * ������ ��ҪУ�飺 ��������Ѿ����㣬���ߴ���ͬ�ڴ�leaveindex����������ܷ����� �������Է�����ļ�¼��Ӧ������������
	 * 1.����δ���㣬�� 2.������ͬ�ڸ����leaveindex��¼ (non-Javadoc)
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
		// ���ܷ�����ı�׼��ͬһpk_psnorg�ĺ����Ѿ��н����˵�,���ߴ���ͬ�ڵ�leaveindex����ļ�¼��ͬһ��pk_psnorg����ͬ�ڼ��ڵĶ��������¼��
		// Ӧ����˳�ν���ģ�����һ��δ����ļ�¼�ĺ��棬�����ܴ���index�����ͬ�ڼ�¼��.�����������ش��󣬲��÷����ģ���������ˣ���Ҫ��ϵ����Ա���
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
		List<Integer> nextPeriodSealedLine = new ArrayList<Integer>();// �����ѽ������
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
			int leaveSet = settlementmethod.intValue();// �ϴν���˼�¼ʱ�Ľ��㷽ʽ�����ϣ�ת���ڣ�ת����
			// �ߵ����˵������û�н��㣬�����ʱ���ڽ���ʱ�Ľ��������ת���ڣ���Ҫ��ѯ���ڵĽ����¼�������������ڽ���ͽ���
			if (leaveSet == LeaveTypeCopyVO.LEAVESETTLEMENT_NEXT) {
				para.clearParams();
				para.addParam(pk_org);
				para.addParam(vo.getPk_psnorg());
				para.addParam(pk_timeitem);
				para.addParam(isMonth ? (nextYear + nextMonth) : nextYear);
				LeaveBalanceVO[] nextPeriodVOs = CommonUtils.retrieveByClause(LeaveBalanceVO.class, nextPeriodCond,
						para);
				// ������ݿ��У�����Ҫ�����ڽ�������Ϊ0�����Ҵӽ����п۳�
				if (!ArrayUtils.isEmpty(nextPeriodVOs)) {
					LeaveBalanceVO updateVO = nextPeriodVOs[0];
					updateList.add(updateVO);
					updateVO.setLastdayorhour(UFDouble.ZERO_DBL);// �����ڵġ����ڽ��ࡱsetΪ0
					if (vo.getRestdayorhour() != null && vo.getRestdayorhour().doubleValue() != 0) {// ������ڽ��಻Ϊ0�����б�Ҫ�������ڵĽ���
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
		// ҵ����־
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
	 * ���������ҳ����Է������vo�� �����δ���㣬���ܷ��� �����н��ʹ���ˣ����ܷ���
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
				continue;// û�з�����Ȩ�޵ģ��ٳ���
			retList.add(vo);
		}
		return retList.size() == 0 ? null : retList.toArray(new LeaveBalanceVO[0]);
	}

	/**
	 * �����ڰ���ְ�ս���Ĳ�ѯ Ҫ��Ĳ�ѯ����ǣ����pk_psnorg��year����ְ�귶Χ���뿼�ڵ����н�������������һ�������¼
	 * ������ݿ��л�û�У���Ӧ�����ڴ�������һ�� ������ݿ����У��Ҵ���δ����ģ��򷵻����ݿ��е�
	 * ������ݿ����У��Ҷ������ˣ������һ����������ڣ���>=��ְ�����һ�죬�򷵻������е�
	 * ������ݿ����У��Ҷ������ˣ������һ�����������<��ְ�����һ�죬�򿴽�����+1����ְ�����һ���д�pk_psnorg�Ƿ�
	 * �п��ڵ�����¼�����У����ڴ�������һ�������¼������ֻ�������ݿ��е�
	 * 
	 * �˲�ѯ�����ѣ���������ÿ��pk_psnorg����year����ְ��Ŀ�ʼ�������ڶ���һ��
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
		// ���ȣ�������ְ���������ڷ�Χ�����һ����ŵ���Ա��Χ��
		UFLiteralDate yearEarliestDay = UFLiteralDate.getDate(year + "-01-01");
		UFLiteralDate yearLatesDay = UFLiteralDate.getDate(Integer.toString(Integer.parseInt(year) + 1) + "-12-31");
		ITBMPsndocQueryService tbmpsndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		// ��pk_psnorg���飬��ѯÿ�������µ�
		TBMPsndocVO[] psndocVOs = tbmpsndocService.queryPsnorgLatestByCondition(pk_org, fromWhereSQL, yearEarliestDay,
				yearLatesDay);
		// ���û�п��ڵ������򷵻�null
		if (ArrayUtils.isEmpty(psndocVOs))
			return null;
		// �����ѯ��Щ��Ա����ְ����
		Map<String, UFLiteralDate> hireDateMap = queryHireDate(SQLHelper.getStrArray(psndocVOs, TBMPsndocVO.PK_PSNORG));// key-pk_psnorg,value-��ְ����
		// Ȼ����Щ��¼������ʱ��
		LeaveBalanceDAO leaveBalanceDAO = new LeaveBalanceDAO();
		leaveBalanceDAO.initHireDateTempTable(year, psndocVOs, hireDateMap);
		LeaveBalanceVO[] vos = leaveBalanceDAO.queryByCondition4HireDateSettlementWithHireDateTempTable(pk_org, typeVO,
				year, fromWhereSQL);
		return processMultiRecordsPerPsnorg(pk_org, typeVO, year, null, null, null, vos);
	}

	/**
	 * ����һ��pk_psnorg�����п��ܵĽ����¼ �߼��ǣ����Ƚ�psnorg��vos��pk_psnorg���飬Ȼ��pk_psnorgѭ������
	 * �������δ�����¼
	 * �����pk_psnorg�Ľ��������������ģ������ٴ��������δ����ļ�¼pk_leavebalanceΪ�գ�����leaveindex set
	 * Ϊ1�� ������м�¼���ѽ��㣬������������ 1.�������Ľ�������>=�ڼ����һ�죬�򲻴���
	 * 2.�������Ľ�������<�ڼ����һ�죬�������Ľ�������
	 * +1�쵽�ڼ����һ���ڣ�pk_psnorg�п��ڵ�����¼������Ҫ�ô���newһ�����Ҵ�����leaveindex=����Ľ������ڵ�index+1
	 * 
	 * @param pk_org
	 * @param typeVO
	 * @param year
	 * @param month
	 *            ���·ݣ�����ǰ��������߰���ְ�ս��㣬�˲�������Ϊnull
	 * @param periodBeginDate
	 *            ,����ǰ��������½��㣬����������ڼ�ĵ�һ�죻���ǰ���ְ�ս��㣬�򴫿ռ���
	 * @param periodEndDate
	 *            ,����ǰ��������½��㣬����������ڼ�����һ�죻���ǰ���ְ�ս��㣬�򴫿ռ���
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
		// ��pk_psnorg���鴦��
		Map<String, LeaveBalanceVO[]> psnorgGroupMap = CommonUtils.group2ArrayByField(LeaveBalanceVO.PK_PSNORG, vos);
		// List<LeaveBalanceVO> voList = Arrays.asList(vos);
		ITBMPsndocQueryService tbmpsndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		boolean isHireDateSet = typeVO.getLeavesetperiod().intValue() == TimeItemCopyVO.LEAVESETPERIOD_DATE
		// ssx added on 2018-03-16
		// for changes of start date of company age
				|| typeVO.getLeavesetperiod().intValue() == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE;

		// BEGIN ssx ������������������hirebegindate, hireenddate
		if (typeVO.getLeavesetperiod().intValue() == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE) {
			for (LeaveBalanceVO vo : vos) {
				resetHirePeriodDates(year, vo);
			}
		}
		// end

		// END added on 2019-03-07
		for (String pk_psnorg : psnorgGroupMap.keySet()) {
			LeaveBalanceVO[] psnorgBalanceVOs = psnorgGroupMap.get(pk_psnorg);
			// �����ѭ���У��ҳ�������Ϣ��1.δ����ļ�¼(��һ��������û��)��2.�����һ�������¼����һ��������û�У�
			// ���1û�У���ô2�϶��У����2û�У���ô1�϶��У�
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
					|| (lastSetVO != null && lastSetVO.isSettlement()) // ���e̎�������ѽY��Ĕ���Ҳ����Ҫnew�µ�
			//
			) {// �������δ��������ݣ����ÿ���newһ���µ�
				continue;
			}
			UFLiteralDate psnorgPeriodEndDate = isHireDateSet ? lastSetVO.getHireenddate() : periodEndDate;
			// ������еĶ������ˣ���ô��Ҫ������Ľ����������ڼ�����ձȽϣ����ڰ���ְ�ս���ģ��ڼ������ÿ���˶����ܲ�һ����
			// ���������������ڼ����һ��֮������Ϊ����
			if (lastSetVO.getSettlementdate().after(psnorgPeriodEndDate))
				continue;
			// �����������һ����¼�������ڼ����֮ǰ����ģ���Ҫ�������յ��ڼ������֮���Ƿ���pk_psnorg�Ŀ��ڵ�����¼������У�����Ҫnewһ����¼
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
	 * �����ڰ��ꡢ�ڼ����Ĳ�ѯ ��ѯ���߼��ǣ�����ȷ���ڼ�Ŀ�ʼ����������
	 * ���ݹ����ǣ�pk_psnorg���ڼ䷶Χ�ڴ��ڿ��ڵ�����¼�����pk_psnorg������ӵ��һ�������¼
	 * ���������һ��δ����ļ�¼����δ����ļ�¼ֻ�������һ����leaveindex����һ���� Ȼ��ʹ�� select from ���ڵ��� left
	 * join ���ڽ���� ����ѯ����ѯ�Ľ�����϶���ÿ��pk_psnorg������һ����¼����Ϊ��left join��
	 * Ȼ��pk_psnorg���飬�Է������ݽ��д���
	 * �������δ�����¼�����pk_psnorg�Ľ��������������ģ������ٴ��������δ����ļ�¼pk_leavebalanceΪ��
	 * ������leaveindex set Ϊ1�� ������м�¼���ѽ��㣬������������ 1.�������Ľ�������>=�ڼ����һ�죬�򲻴���
	 * 2.�������Ľ������� <�ڼ����һ�죬�������Ľ�������+1�쵽�ڼ����һ���ڣ�pk_psnorg�п��ڵ�����¼������Ҫ�ô���newһ����
	 * �Ҵ�����leaveindex =����Ľ������ڵ�index+1
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
	 * ��ѯ���������ڼ����ʱ���������ڵĵ�һ������һ��
	 * ����ǰ���ְ�ս��㣬�򷵻�null����Ϊ����ְ�ս���ʱ��ÿ���˵Ľ������ڶ��п��ܲ�һ������Ҫ���������������
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
					/* @res "{0}�꿼���ڼ�δ����!" */, year), "perioderror");
				return null;
			}
			// // MOD ����Ȼ����� James
			// periodBeginDate = new UFLiteralDate(year + "-01-01");
			// periodEndDate = new UFLiteralDate(year + "-12-31");
			periodBeginDate = periodVOs[0].getBegindate();
			periodEndDate = periodVOs[periodVOs.length - 1].getEnddate();
		}
		return new UFLiteralDate[] { periodBeginDate, periodEndDate };
	}

	/**
	 * ��ѯ���������ڼ����ʱ���������ڵĵ�һ������һ��
	 * ����ǰ���ְ�ս��㣬�򷵻�null����Ϊ����ְ�ս���ʱ��ÿ���˵Ľ������ڶ��п��ܲ�һ������Ҫ���������������
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
	// * �򵥲�ѯ�Ѽ���ļ�������
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
		// ����Ȩ��
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
		// ���Ȳ�ѯ�Ƿ��У�
		// 1.������>=�����������һ�죬��δ����Ч���죨ֻ��԰���ְ�ս�������Σ����갴�ڼ����Ĳ��ô˲�ѯ����Ϊ�����˵��ڼ����һ�춼��һ���ģ������ʾ��ϸ�ͱȽϹ֣�
		// 2.δ���ڼ����һ�죬�����޿��ڵ���
		// ����У����Ȳ�ִ�н��㣬����Щ��Ҫ��ʾ����Ϣ���ص��ͻ���
		// ����ֱ��ִ�н��㣨������Խ���ģ����ܽ��������������Ҫ��������Ϣ��
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
		if (TimeItemCopyVO.LEAVESETTLEMENT_MONEY == typeVO.getLeavesettlement()) {// ����תн�ʵģ���Ҫȷ�ϰ�н��ת�Ƶ��ĸ������ڼ䣬���ڼ�Ϊ��Ĭ�ϵ�ǰ�ڼ�
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
	 * ��԰�������ڼ������ݼ����ĵ�һ�ν������ �����ֶ��ǹ̶��ڼ� ���������<�ڼ����죬�����쳣 ���������>=��Ч�ڣ������κ���ʾ
	 * Ҳ����ֻ���ڽ��������ڼ���������Ч�ڵķ�Χ�ڣ����п�����Ҫ��ʾ�û� ���������<=�ڼ�ĩ�죬��ֻ������ʾ���ڵ����ڽ����յ��ڼ�ĩ�첻���ڵ���Ա
	 * ������������ڼ�ĩ�쵽��Ч��֮��
	 * ���������Ҫ��ʾ������Ա��a.�����յ���Ч�����޿��ڵ�������Աb.�����յ���Ч�����п��ڵ�������Ա����������Ա�û�����Ҫ����Դ������Ҫ�ֱ���ʾ
	 * ����������������ʾ����������Ա�������ֲ�����ͬʱ����
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
		if (result.needQueryUser())// �������Ҫѯ���û������������Ҫѯ�ʵ����ݷ��ص�ǰ̨��������������
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
		// ǰһ���ڼ�/���
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
	 * ��԰���ְ�ս�����ݼ����ĵ�һ�ν������ ���������<year�����죬�����쳣 ���������>year+1��ĩ�죬�����κ���ʾ
	 * Ҳ����ֻ����year���쵽year+1��ĩ�췶Χ�ڣ����п�������ʾ ��ʾ����Ա��Ϊ���֣� 1.��δ����ĩ�죬�������յ���ĩ�������޿��ڵ�������Ա
	 * 2.��δ����Ч�ڣ��������յ���Ч�����޿��ڵ�������Ա 3.��δ����Ч�ڣ������յ���Ч���ڻ��п��ڵ�������Ա
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

		// //MOD �ź��21481����ȡ���ݼ���Ա 2018/8/21
		// LeaveBalanceVO[] specialPsnVOs = sgv.specialPsnVOs;

		result.setSettledVOs(vos);
		if (result.needQueryUser())// �������Ҫѯ���û������������Ҫѯ�ʵ����ݷ��ص�ǰ̨��������������
			return result;
		LeaveBalanceVO[] canSettleVOs = sgv.toCanSettleVOs();
		if (ArrayUtils.isEmpty(canSettleVOs)) {
			result.setErrMsg("����ʧ��");
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

	// BEGIN �ź�{21481} �������ݼ���Ա�����Ӵ��꣩ specialPsnVOsΪ���ݼ���Ա���� 2018/8/21
	@SuppressWarnings("unchecked")
	private void settlement(String pk_org, LeaveTypeCopyVO typeVO, String year, String month, PeriodVO previousPeriod,
			PeriodVO nextPeriod, LeaveBalanceVO[] vos, Map<String, Integer> lineNoMap,// ���ڴ˷��������vos�ǽ���vos���Ӽ���Ϊ����ȷ����ʾ�кţ���Ҫ��vo���кŷ���map��key��pk_psnorg+leaveindex
			UFLiteralDate settlementDate, SettlementResult result, String salaryYear, String salaryMonth)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;

		// //BEGIN �ź��21481�� �����ݼ���Ա����ְ����Ա�ں���һ�� 2018/8/21
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
		// //END �ź��21481�� �����ݼ���Ա����ְ����Ա�ں���һ�� 2018/8/21

		// ����ǰ����Ҫ�ȼ���
		UFDateTime calTime = new UFDateTime(settlementDate.toString() + " 00:00:00");
		UFDateTime now = new UFDateTime();
		if (calTime.before(now))
			calTime = now;
		vos = calculate(pk_org, typeVO, year, month, vos, calTime, true);
		// ���ܽ���ı�׼��ͬһpk_psnorg�ĺ����Ѿ��н����˵�,���ߴ���ͬ�ڵ�leaveindex����ļ�¼��ͬһ��pk_psnorg����ͬ�ڼ��ڵĶ��������¼��
		// Ӧ����˳�ν���ģ�����һ��δ����ļ�¼�ĺ��棬�����ܴ���index�����ͬ�ڼ�¼��.�����������ش��󣬲��÷����ģ���������ˣ���Ҫ��ϵ����Ա���
		int leaveSetPeriod = typeVO.getLeavesetperiod().intValue();
		boolean isMonth = leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_MONTH;
		int leaveSet = typeVO.getLeavesettlement().intValue();// ���㷽ʽ�����ϣ�ת���ڣ�ת����
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
		List<Integer> nextPeriodSealedLine = new ArrayList<Integer>();// �����ѽ������
		List<Integer> samePreRecordNotSealedLine = new ArrayList<Integer>();// ͬ�ڸ����¼��δ�������
		List<Integer> previousPeriodNotSealedLine = new ArrayList<Integer>();// ���ڻ�δ�������
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);

		// ǰ��Ϊ��ʱ����Ҫ��ѯ���ڵ�������Ϊ����Ҳ����ʹ��
		TBMPsndocVO[] existtbmpsndocs = previousPeriod == null ? null : psndocService.queryTBMPsndocByPsnorgs(pk_org,
				StringPiecer.getStrArray(vos, LeaveBalanceVO.PK_PSNORG), previousPeriod.getBegindate(),
				previousPeriod.getEnddate());
		Map<Object, TBMPsndocVO[]> tbmpsnExistMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_PSNORG,
				existtbmpsndocs);
		boolean existMapIsNull = MapUtils.isEmpty(tbmpsnExistMap);

		// BEGIN �ź� ��Ϊ���������յ�ʱ����Ҫȡ���ʵ����� 2018/9/6
		// ȡ��ְ���� ������������
		Map<String, UFLiteralDate> psnOrgDateMap = new HashMap<String, UFLiteralDate>();
		InSQLCreator isc = new InSQLCreator();
		try {
			String psnOrgInSql = isc.getInSQL(StringPiecer.getStrArray(vos, LeaveBalanceVO.PK_PSNORG));
			PsnOrgVO[] psnOrgVOs = CommonUtils.retrieveByClause(PsnOrgVO.class, " pk_psnorg in (" + psnOrgInSql + ") ");
			if (!ArrayUtils.isEmpty(psnOrgVOs)) {
				for (PsnOrgVO psnOrgVO : psnOrgVOs)

					// MOD �ź� ����������������ս��㣬��Ҫȡ�����ʵ���ʼ����
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
		// END �ź� ��Ϊ���������յ�ʱ����Ҫȡ���ʵ����� 2018/9/6

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

		// MOD �ź� ���ػ����� �Ƿ��������������� 2018/9/6
		UFBoolean isSpecialrest = typeVO.getIsspecialrest();

		for (LeaveBalanceVO vo : vos) {

			// BEGIN �ź� �ҵ����������պ����ʽ����� 2018/9/6
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
					throw new BusinessException("��Ա����Ϊ" + vo.getPk_psndoc() + "��Ա��������ʼ����Ϊ��");
				}
			}

			// MOD �ź� ���ػ��ҵ���ǰ���ڿ����ڼ��Ƿ�Ϊ���Ӵ��� 2018/09/06
			Object specialrest = null;
			if (null != isSpecialrest && isSpecialrest.booleanValue()) {
				String startDate = "";
				String overDate = "";
				// �������ʽ���
				if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
					startDate = beginDate.toString();
					overDate = endDate.toString();
					// ������ְ����
				} else if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE) {
					startDate = vo.getHirebegindate().toString();
					overDate = vo.getHireenddate().toString();
					// ���������
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
								throw new BusinessException(year + "�꿼�ڵ�������Ϊ��");
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
			// END �ź� �ҵ����������պ����ʽ����� 2018/9/6

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
			// ���������¼����ͬ��/ͬ�껹�и��ϵļ�¼����Ҫ����һ�������Ѿ�����
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
			// ���leaveindex=1���ҿ����ڼ�������/������һ��ȣ���Ҫ������������/��һ����Ƿ��п��ڵ��������У���Ҫ�������ݿ�û�����ڽ����¼�������쳣�����м�¼��Ҫ��ȫ�����ѽ���
			else if (previousPeriod != null) {
				boolean existsTBMPsndoc = false;
				if (leaveSetPeriod != LeaveTypeCopyVO.LEAVESETPERIOD_DATE // ������ڼ����ģ����ڵ����ڷ�Χ�ǹ̶���
						// ssx added on 2018-03-16
						// for changes of start date of company age
						&& leaveSetPeriod != LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE//
				) {
					// ��һ���������ȽϹ̶��������Ż���ȡ��ѭ������
					// existsTBMPsndoc = psndocService.existsTBMPsndoc(pk_org,
					// vo.getPk_psnorg(), previousPeriod.getBegindate(),
					// previousPeriod.getEnddate());
					existsTBMPsndoc = !existMapIsNull && !ArrayUtils.isEmpty(tbmpsnExistMap.get(vo.getPk_psnorg()));
				} else {

					// BEGIN �ź��21481�� ����ְ�����ʷֿ����� 2018/8/21
					// ������ְ����
					if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE) {
						// ���Ϊ����ְ���ڽ��㣬�ҵ�ǰ���ݿ�ʼ��������ְ����һ�������������ڣ�Ϊ���ų�������¼��ʼ���ڱ���֯��ϵ��ʼ����������������
						if (psnOrgDateMap.get(vo.getPk_psnorg()) == null
								|| !psnOrgDateMap.get(vo.getPk_psnorg()).equals(vo.getHirebegindate()))
							existsTBMPsndoc = psndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(),
									getHireBeginDate(previousPeriod.getYear(), vo.getHirebegindate()), vo
											.getHirebegindate().getDateBefore(1));
						// MOD �ź� �������������������� 2018/8/23
					} else if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
						// typeVO.getLeaveextendcount() Ϊ�����ӳ�����
						// if (psnOrgDateMap.get(vo.getPk_psnorg()) == null
						// ||
						// !psnOrgDateMap.get(vo.getPk_psnorg()).equals(beginDate))
						// {
						existsTBMPsndoc = psndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(),
								getHireBeginDate(previousPeriod.getYear(), beginDate), beginDate.getDateBefore(1));
						// }
					}
					// END �ź��21481�� ����ְ�����ʷֿ����� 2018/8/21

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
			// 2013-03-20 ��ӵ������֯���ݼ�ת�Ƶ����ݲ���Ҫ��������
			if (nextPeriod == null)
				continue;
			// �ߵ����˵������û�н��㣬���Ҫת���ڵĻ�����Ҫ��ѯ���ڵĽ����¼�������������ڽ���ͽ���
			// MOD �ź� specialrest����1����ǰ��ԱΪ���Ӵ��� 2018/8/23
			if (leaveSet == LeaveTypeCopyVO.LEAVESETTLEMENT_NEXT || "1".equals(specialrest)) {
				// para.clearParams();
				// para.addParam(pk_org);
				// para.addParam(vo.getPk_psnorg());
				// para.addParam(pk_timeitem);
				// para.addParam(isMonth?(nextPeriod.getTimeyear()+nextPeriod.getTimemonth()):nextPeriod.getTimeyear());

				// 2015-09-10�޸ģ�������û�п��ڵ�������಻ת���ڣ������ְ����ְʱ�����ְǰ�Ľ������ݴ���������ְʱ�����Ѿ����㹤���ˣ�
				// �Ƿ�Ҫinsert����Ҫ�������Ƿ��п��ڵ�����¼�����û�У�����Ҫinsert
				boolean needInsert = false;
				if (leaveSetPeriod != LeaveTypeCopyVO.LEAVESETPERIOD_DATE
				// ssx added on 2018-03-16
				// for changes of start date of company age
						&& leaveSetPeriod != LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE //
				) {// ������ڼ����ģ����ڵ����ڷ�Χ�ǹ̶���
					needInsert = psndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(), nextPeriod.getBegindate(),
							nextPeriod.getEnddate());
				} else {// ����ְ�ս���ģ����ڵ����ڷ�Χ���˶���
					if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE) {
						needInsert = psndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(), vo.getHireenddate()
								.getDateAfter(1), getHireEndDate(nextPeriod.getTimeyear(), vo.getHirebegindate()));
						// MOD �ź� ��������������������
					} else if (leaveSetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
						// typeVO.getLeaveextendcount() Ϊ�����ӳ�����
						needInsert = psndocService.existsTBMPsndoc(pk_org, vo.getPk_psnorg(), endDate.getDateAfter(1),
								getHireEndDate(nextPeriod.getTimeyear(), beginDate));
					}

				}

				// LeaveBalanceVO[] nextPeriodVOs = (LeaveBalanceVO[])
				// CommonUtils.toArray(LeaveBalanceVO.class,
				// dao.retrieveByClause(LeaveBalanceVO.class, nextPeriodCond,
				UFDouble toLeave = vo.getRestdayorhour();// ת���ڵ����ݣ���������ת�����������޵ģ�����ڴ˴���
				if (typeVO.getLeavemax() != null && toLeave != null && typeVO.getLeavemax().doubleValue() > 0
						&& toLeave.doubleValue() > typeVO.getLeavemax().doubleValue()) {
					toLeave = typeVO.getLeavemax();
				}
				// ������ݿ��У�����Ҫupdate��������Ҫinsert
				// if(!ArrayUtils.isEmpty(nextPeriodVOs)){
				if (!CollectionUtils.isEmpty(balanceMap.get(vo.getPk_psnorg())) && needInsert) {// 2015-09-10�޸ģ�������û�п��ڵ�������಻ת���ڣ������ְ����ְʱ�����ְǰ�Ľ������ݴ���������ְʱ�����Ѿ����㹤���ˣ�
					LeaveBalanceVO updateVO = balanceMap.get(vo.getPk_psnorg()).get(0);
					updateList.add(updateVO);
					updateVO.setLastdayorhour(toLeave);// �����ڽ�����µ����ڵġ����ڽ��ࡱ
					if (toLeave != null && toLeave.doubleValue() != 0) {// ������ڽ��಻Ϊ0�����б�Ҫ�������ڵĽ���
						updateVO.setRestdayorhour(updateVO.getRestdayorhour() == null ? toLeave : toLeave.add(updateVO
								.getRestdayorhour()));
					}
				} else {
					// //�Ƿ�Ҫinsert����Ҫ�������Ƿ��п��ڵ�����¼�����û�У�����Ҫinsert
					// boolean needInsert = false;
					// if(leaveSetPeriod!=LeaveTypeCopyVO.LEAVESETPERIOD_DATE){//������ڼ����ģ����ڵ����ڷ�Χ�ǹ̶���
					// needInsert = psndocService.existsTBMPsndoc(pk_org,
					// vo.getPk_psnorg(), nextPeriod.getBegindate(),
					// nextPeriod.getEnddate());
					// }
					// else{//����ְ�ս���ģ����ڵ����ڷ�Χ���˶���
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
		// ҵ����־
		TaBusilogUtil.writeLeaveBalanceSettlementBusiLog(vos);
	}

	// END �ź�{21481} �������ݼ���Ա�����Ӵ��꣩ 2018/8/21

	/**
	 * ��ѯ���ڡ�����ǰ�����㣬���߰���ְ�ս��㣬�򷵻���һ�ꡣ����ǰ��ڼ���㣬�򷵻���һ�ڼ���ꡢ��
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
	 * ��ѯ���ڡ�����ǰ�����㣬���߰���ְ�ս��㣬�򷵻���һ�ꡣ����ǰ��ڼ���㣬�򷵻���һ�ڼ���ꡢ��
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
	 * ���õ����������ʱ��˵��������һ����δ��������Ч�ڣ�����δ�������յ����޿��ڵ�������Ա���Ѿ��ڽ�������ʾ�� (non-Javadoc)
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
		if (TimeItemCopyVO.LEAVESETTLEMENT_MONEY == typeVO.getLeavesettlement()) {// ����תн�ʵģ���Ҫȷ�ϰ�н��ת�Ƶ��ĸ������ڼ䣬���ڼ�Ϊ��Ĭ�ϵ�ǰ�ڼ�
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
		// 2013-03-20 ��ӵ������֯���ݼ�ת�Ƶ����ݲ���Ҫ��������
		if (needCheckNextPeriod) {
			String[] nextYearMonth = queryNextPeriod(pk_org, typeVO, year, month);
			if (ArrayUtils.isEmpty(nextYearMonth))
				throw new BusinessException(ResHelper.getString("6017leave", "06017leave0232")
				/* @res "ϵͳδ��������!" */);
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

		// //MOD �ź��21481����ȡ���ݼ���Ա 2018/8/21
		// LeaveBalanceVO[] specialPsnVOs = sgv.specialPsnVOs;

		if (ArrayUtils.isEmpty(canSettleVOs))
			return result;

		String nextYear = Integer.toString(Integer.parseInt(year) + 1);
		// 2013-03-20 ��ӵ������֯���ݼ�ת�Ƶ����ݲ���Ҫ��������
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
	 * ���ڰ���ְ�ս���ģ����洫���Ľ���vo���飬�����п��Խ���ķ���
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
			LeaveBalanceVO[] vos,// Ҫ�����vo�Ѿ������ݿ��е�ͬ����
			UFLiteralDate settlementDate) throws BusinessException {
		// ���������������year 01-01�������쳣
		if (settlementDate.toString().substring(0, 4).compareTo(year) < 0)
			throw new BusinessException(MessageFormat.format(getSealDateTooEarlyError(), year + "-01-01"));
		List<LeaveBalanceVO> notToPeriodEndDateNoTbmPsndocList = new ArrayList<LeaveBalanceVO>();// δ���㣬��δ����ĩ�գ��ҽ����յ���ĩ���޿��ڵ�������Ա����Ҫ��ʾ�Ƿ����
		List<LeaveBalanceVO> notToEffectiveDateNoTbmPsndocList = new ArrayList<LeaveBalanceVO>();// δ���㣬�ҹ�����ĩ��δ����Ч�ڣ��ҽ����յ���Ч�����޿��ڵ�������Ա����Ҫ��ʾ�Ƿ����
		List<LeaveBalanceVO> notToEffectiveDateExistTbmPsndocList = new ArrayList<LeaveBalanceVO>();// δ���㣬�ҹ�����ĩ��δ����Ч�ڣ��ҽ����յ���Ч�����п��ڵ�������Ա����Ҫ��ʾ�Ƿ����
		List<LeaveBalanceVO> exceedEffectiveDateList = new ArrayList<LeaveBalanceVO>();// δ���㣬���ѹ�����Ч�ڵ���Ա��������ʾ���϶��ܽ���

		// //BEGIN �ź��21481�� �������ݼٵ�Ա�������Խ��� 2018/8/21
		// List<LeaveBalanceVO> specialPsnList = new
		// ArrayList<LeaveBalanceVO>();
		// //END �ź��21481�� �������ݼٵ�Ա�������Խ��� 2018/8/21

		ITBMPsndocQueryService tbmpsndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		// MOD by ssx on 2020-03-25
		// �Д���Ч���f�ӕr�������Д��Ƿ����S��Ч���f�ӣ����ⲻ���S�r�씵��ֵ������e�`
		int extendCount = typeVO.getIsleave() == null || !typeVO.getIsleave().booleanValue() ? 0 : typeVO
				.getExtendDaysCount();// ��Ч�����ӳ�������
		// end ssx
		SettlementGroupVO retVO = new SettlementGroupVO();
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		IDataPermissionPubService perimssionService = NCLocator.getInstance().lookup(IDataPermissionPubService.class);
		Map<String, UFBoolean> perimssionMap = null;
		if (!PubEnv.UAP_USER.equalsIgnoreCase(pk_user))// 2013-03-29��̨�����ѯȨ�ޱ���
			perimssionMap = perimssionService.isUserhasPermissionByMetaDataOperation("60170psndoc",
					StringPiecer.getStrArrayDistinct(vos, LeaveBalanceVO.PK_TBM_PSNDOC), "LeaveBalanceAction",
					pk_group, pk_user);
		for (LeaveBalanceVO vo : vos) {
			if (vo.isSettlement()) {// �Ѿ������˵ģ��ٳ���
				continue;
			}

			// // û��Ȩ�޵��ٳ��� #2012-10-10�޸ģ�ʹ��map����sql��
			if (!PubEnv.UAP_USER.equalsIgnoreCase(pk_user)) {
				if (perimssionMap != null && perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()) != null
						&& !perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()).booleanValue())
					continue;
			}
			// if(!perimssionService.isUserhasPermissionByMetaDataOperation("60170psndoc",
			// vo.toTBMPsndocVO().getPrimaryKey(), "LeaveBalanceAction",
			// pk_group, pk_user))
			// continue;//û�н���Ȩ�޵ģ��ٳ�����������ְ��
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

				// BEGIN �ź�{21481} ���ݼ�н�YӋ�� 2018/8/21
				// ��������������
			} else if (typeVO.getLeavesetperiod() == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
				// ��ȡ���������գ����ʽ�����
				UFLiteralDate[] specialBeginEnd = getSpecialBeginEnd(vo.getPk_psndoc(), year, vo.getPk_psnorg());
				if (!ArrayUtils.isEmpty(specialBeginEnd)) {
					UFLiteralDate beginDate = specialBeginEnd[0];
					UFLiteralDate endDate = specialBeginEnd[1];
					if (settlementDate.before(beginDate)) {// ������������ְ��֮ǰ���ٳ���
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
					// δ�������ջ�δ����Ч�գ��ҽY���յ���Ч��֮�g�п��ڙn���ģ���Ҫ��ʾ�Ƿ�Y��
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

					// �x��ͣ�ˆT���ԽY�㱾��ٽY�� wangywt 20190424 begin
					if (settlementDate.after(effectiveDate) || getPsnStateFlag(vo))
						exceedEffectiveDateList.add(vo);
					// wangywt 20190424 end
				}
				// END �ź�{21481} ���ݼ�н�YӋ�� 2018/8/21
			} else {
				if (settlementDate.before(vo.getHirebegindate())) {// ������������ְ��֮ǰ���ٳ���
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
	 * �Д��x��ͣ�ˆT�Ƿ���M�нY��
	 * 
	 * @author ������
	 * @since 2019-04-24
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private boolean getPsnStateFlag(LeaveBalanceVO vo) throws BusinessException {
		BaseDAO baseDao = new BaseDAO();
		// MOD by ssx #33564
		// �x�ˆT���ԽY��
		Collection<PsnOrgVO> psnorgvos = baseDao.retrieveByClause(PsnOrgVO.class, "pk_psnorg = '" + vo.getPk_psnorg()
				+ "' ");
		if (psnorgvos.toArray(new PsnOrgVO[0])[0].getEndflag().booleanValue()) {
			return true;
		}

		// ��ͣ�ˆT���ԽY��
		String jobsql = "select TRNSTYPE from hi_psnjob where pk_psndoc = '" + vo.getPk_psndoc() + "' and pk_psnorg='"
				+ psnorgvos.toArray(new PsnOrgVO[0])[0].getPk_psnorg() + "'";
		// end #33564
		List<Object[]> jobList = (List<Object[]>) baseDao.executeQuery(jobsql, new ArrayListProcessor());
		// ��ͣ�������
		String refTransType = SysInitQuery.getParaString(vo.getPk_org(), "TWHR11").toString();
		// �}�������
		String refReturnType = SysInitQuery.getParaString(vo.getPk_org(), "TWHR12").toString();
		// ��ͣӛ�
		int refTrans = 0;
		// ��ӛ�
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
	 * ���ڰ��ꡢ�ڼ����ģ����洫���Ľ����vo���飬�����Խ���ķ���
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
			UFLiteralDate periodBeginDate, UFLiteralDate periodEndDate, LeaveBalanceVO[] vos,// Ҫ�����vo�Ѿ������ݿ��е�ͬ����
			UFLiteralDate settlementDate) throws BusinessException {
		if (settlementDate.before(periodBeginDate))
			throw new BusinessException(MessageFormat.format(getSealDateTooEarlyError(), periodBeginDate));
		int extendCount = typeVO.getExtendDaysCount();// ��Ч�����ӳ�������
		UFLiteralDate effectiveDate = extendCount == 0 ? periodEndDate : periodEndDate.getDateAfter(extendCount);
		SettlementGroupVO retVO = new SettlementGroupVO();
		String pk_user = InvocationInfoProxy.getInstance().getUserId();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		IDataPermissionPubService perimssionService = NCLocator.getInstance().lookup(IDataPermissionPubService.class);
		Map<String, UFBoolean> perimssionMap = perimssionService.isUserhasPermissionByMetaDataOperation("60170psndoc",
				StringPiecer.getStrArrayDistinct(vos, LeaveBalanceVO.PK_TBM_PSNDOC), "LeaveBalanceAction", pk_group,
				pk_user);
		if (settlementDate.after(effectiveDate)) {// ����������ѹ���Ч�ڣ���ֻ���������֣������˵ģ���ʣ�µ�
			List<LeaveBalanceVO> leftList = new ArrayList<LeaveBalanceVO>();
			for (LeaveBalanceVO vo : vos) {
				if (vo.isSettlement())
					continue;
				if (perimssionMap != null && perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()) != null
						&& !perimssionMap.get(vo.toTBMPsndocVO().getPrimaryKey()).booleanValue())
					continue;// û�н���Ȩ�޵ģ��ٳ���
				leftList.add(vo);
			}
			if (leftList.size() > 0)
				retVO.exceedEffectiveDateVOs = leftList.toArray(new LeaveBalanceVO[0]);
			return retVO;
		}
		ITBMPsndocQueryService tbmpsndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		// ���������δ����ĩ�գ���ֻ�������֣������˵ģ���δ����ĩ����/�п��ڵ�����
		if (!settlementDate.after(periodEndDate)) {
			List<LeaveBalanceVO> noPsndocList = new ArrayList<LeaveBalanceVO>();

			// �Ż�ʱ���
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
					continue;// û�н���Ȩ�޵ģ��ٳ���
				// ����ѭ���в�ѯ�ˣ���ȡ����
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
		// ��������չ�����ĩ�գ���δ����Ч���գ���ֻ�������֣������˵ģ���δ����Ч����/�п��ڵ�����
		if (settlementDate.after(periodEndDate) && !settlementDate.after(effectiveDate)) {
			List<LeaveBalanceVO> noPsndocList = new ArrayList<LeaveBalanceVO>();
			List<LeaveBalanceVO> existPsndocList = new ArrayList<LeaveBalanceVO>();

			// �Ż�ʱ���
			TBMPsndocVO[] tbmpsndocs = tbmpsndocService.queryTBMPsndocByPsnorgs(pk_org,
					StringPiecer.getStrArray(vos, LeaveBalanceVO.PK_PSNORG), settlementDate.getDateAfter(1),
					effectiveDate);
			Map<Object, TBMPsndocVO[]> tbmpsnMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_PSNORG, tbmpsndocs);
			boolean mapIsNull = MapUtils.isEmpty(tbmpsnMap);

			for (LeaveBalanceVO vo : vos) {
				if (vo.isSettlement()) {
					continue;
				}
				// �Ż�ʱɾ����������ѭ���в�ѯ
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
	 * ���û��ύ�Ľ���vo������飬�����п��Խ���ķ�Ϊ4�����֣���������ע�͡��϶����ܽ���ģ������ѽ���ģ��������ڷ�������
	 * 
	 * MOD �ź� ���� �������ݼٵ�Ա�� 2018/8/23
	 * 
	 * @author zengcheng
	 * 
	 */
	private static class SettlementGroupVO {
		// LeaveBalanceVO[] settlementedVOs;//�ѽ���ġ�������ʾ�ͻ��ˣ��϶����ܽ���
		// LeaveBalanceVO[] notToPeriodBeginDateVOs;//δ���㣬��δ�������գ�������ʾ���϶����ܽ���
		LeaveBalanceVO[] notToPeriodEndDateNoTbmPsndocVOs;// δ���㣬��δ����ĩ�գ��ҽ����յ���ĩ���޿��ڵ�������Ա����Ҫ��ʾ�Ƿ����
		// LeaveBalanceVO[]
		// notToPeriodEndDateExistTbmPsndocVOs;//δ���㣬��δ����ĩ�գ��ҽ����յ���ĩ���п��ڵ�������Ա��������ʾ���϶����ܽ���
		LeaveBalanceVO[] notToEffectiveDateNoTbmPsndocVOs;// δ���㣬�ҹ�����ĩ��δ����Ч�ڣ��ҽ����յ���Ч�����޿��ڵ�������Ա����Ҫ��ʾ�Ƿ����
		LeaveBalanceVO[] notToEffectiveDateExistTbmPsndocVOs;// δ���㣬�ҹ�����ĩ��δ����Ч�ڣ��ҽ����յ���Ч�����п��ڵ�������Ա����Ҫ��ʾ�Ƿ����
		LeaveBalanceVO[] exceedEffectiveDateVOs;// δ���㣬���ѹ�����Ч�ڵ���Ա��������ʾ���϶��ܽ���

		// //BEGIN �ź��21481�� �������ݼٵ�Ա�������Խ��� 2018/8/21
		// LeaveBalanceVO[] specialPsnVOs;
		// //END �ź��21481�� �������ݼٵ�Ա�������Խ��� 2018/8/21

		LeaveBalanceVO[] toCanSettleVOs() {// ���Խ���ļ�¼��Ĭ�Ϸ��ع�����Ч�ڵ���Ա
			return exceedEffectiveDateVOs;
		}

		LeaveBalanceVO[] toCanSettleVOs(boolean isSettleNotToPeriodEndDateNoTbmPsndoc,
				boolean isSettleNotToEffectiveDateNoTbmPsndocVOs, boolean isSettleNotToEffectiveDateExistTbmPsndocVOs) {// ���ݽ����û���ѡ�񣬷��ؿ��Խ������Ա
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
		// �ݼ����Map, Key: ��֯���� value-key: �ݼ���������� value: �ݼ����
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
		// ��Ҫ���ݽ��㷽ʽ����һ�����ݣ�������ڼ���Ĭ�ϰ�����㣬���ڼ�������������ڲ�ѯ���Բ�ѯ����������ݡ���������Ϊ���ڼ���㣬���ڼ��������������ڲ�ѯ�ܲ鵽�ڼ�����ݣ���֮ǰ������ݻ��ڣ�Ӧ��ɾ��
		LeaveTypeCopyVO typeCopyVO = (LeaveTypeCopyVO) timeItemService.queryCopyTypesByDefPK(latestVO.getPk_org(),
				pk_leavetype, TimeItemVO.LEAVE_TYPE);
		String cond = LeaveBalanceVO.PK_PSNDOC + "=? and " + LeaveBalanceVO.PK_TIMEITEM + "=? ";
		if (StringUtils.isNotBlank(year)) {// ������ѯ���Բ�ѯ������ȵģ����Ϊ��
			cond += "and " + LeaveBalanceVO.CURYEAR + "=? ";
		}
		// �����������ʷ���ݣ���ֻ���˵�ǰHR��֯������
		if (!containsHis)
			cond += " and " + LeaveBalanceVO.PK_ORG + "=? ";
		// �����½���������һ��
		if (TimeItemCopyVO.LEAVESETPERIOD_MONTH == typeCopyVO.getLeavesetperiod()) {
			cond += " and curmonth is not null";
		} else if (TimeItemCopyVO.LEAVESETPERIOD_YEAR == typeCopyVO.getLeavesetperiod()) {// ��������޸�Ϊ���ڼ���������������ݣ���Ҫ����һ��
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
		// ���˵��������¿��ڵ������ݼ�����
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
	 * ����Ա�������Ľ�����Ϣ��pk_psnjob��pk_timeitemcopy�ֶΣ���Ϊ�������ֶβ�û�д洢�����ݿ���
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
		Map<String, String> typeCopyMap = new HashMap<String, String>();// key��pk_org,value��leavetypecopy
		String pk_psnorg = null;
		String Sql = "select pk_timeitem from tbm_timeitem where (timeitemname='Annual Leave' or timeitemname='���') and (enablestate='2') ";
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
			// ����ݼ����Ϊ��٣�ͨ��pk_psnorg���˵���ְ����ְ��Ա����ְǰ����
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
		/* @res "{0}�У������Ѿ�����,���ߴ���ͬ�ڽ����¼�����ش�������ϵ����Ա��" */;
	}

	private String getPreviousPeriodNotSealedError() {
		return ResHelper.getString("6017leave", "06017leave0251")
		/* @res "{0}�У�ǰ�ڽ����¼��δ���㡣" */;
	}

	private String getSamePreRecordNotSealedError() {
		return ResHelper.getString("6017leave", "06017leave0252")
		/* @res "{0}�У����ڸ����ͬ��δ�����¼�����ش�������ϵ����Ա" */;
	}

	private String getSealDateTooEarlyError() {
		return ResHelper.getString("6017leave", "06017leave0234")
		/* @res "�������ڲ�������{0}" */;
	}

	private String getNoNextPeriodError() {
		return ResHelper.getString("6017leave", "06017leave0232")
		/* @res "ϵͳδ��������!" */;
	}

	/**
	 * �������ʱ������֯ת��,���ô˷�����ǰ����ԭ��֯�Ŀ��ڵ����Ѿ�����������֯�Ŀ��ڵ���������
	 * 
	 * @param oldJobvo
	 * @param newJobvo
	 * @throws BusinessException
	 */
	@Override
	public void translateLeave(PsnJobVO oldJobvo, PsnJobVO newJobvo) throws BusinessException {
		if (oldJobvo == null || newJobvo == null)
			return;
		// ������֯��һ����hr��֯����Ҫ����һ��
		OrgVO oldhr = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(oldJobvo.getPk_org());
		OrgVO newhr = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(newJobvo.getPk_org());
		String oldhrorg = oldhr.getPk_org();
		String newhrorg = newhr.getPk_org();
		// ����������Դ��֯û�з����仯����ת�ƽ���
		if (oldhrorg.equals(newhrorg))
			return;
		// ��ѯ��Ҫת�Ƶ��ݼ����
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
		// �������֯���ݵ����޷�ִ�� start
		if (oldPeriod == null || newPeriod == null) {// 20160415
			return;
		}
		// �ҳ���ͬ����ȫ�ֻ��ŵ����Ľ��д���ת�ƽ���ʱ��
		Map<String, LeaveTypeCopyVO> oldMap = CommonUtils.toMap(LeaveTypeCopyVO.PK_TIMEITEM, oldTypes);
		Map<String, LeaveTypeCopyVO> newMap = CommonUtils.toMap(LeaveTypeCopyVO.PK_TIMEITEM, newTypes);
		// LeaveBalanceVO�Ĳ�ѯ����
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinPsnjobTable(null);// ����Ҫ��֤�����˹�����¼��
		String psnjobTableName = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob");
		String oldPsnjobCond = psnjobTableName + ".pk_psnjob = '" + oldJobvo.getPk_psnjob() + "' ";
		FromWhereSQL oldFromWhereSQL = TBMPsndocSqlPiecer.addPsnjobCond2QuerySQL(oldPsnjobCond, fromWhereSQL);
		String newPsnjobCond = psnjobTableName + ".pk_psnjob = '" + newJobvo.getPk_psnjob() + "' ";
		FromWhereSQL newFromWhereSQL = TBMPsndocSqlPiecer.addPsnjobCond2QuerySQL(newPsnjobCond, fromWhereSQL);
		List<LeaveBalanceVO> updateList = new ArrayList<LeaveBalanceVO>();
		// �˴�forѭ���������ݿ���������ǲ���Ҫ�Ż�����Ϊѭ������һ��<=1,�Ҳ�һ�����꣬���Ƕ���ȡ��ѭ���⣬��һ��Ҫ��ѯ�������ݿ⣬�п����ʵ��䷴��
		for (String pk_timeitem : oldMap.keySet()) {
			// �ϲ�����NCdp205587553
			// ����ٳ���
			if (LeaveTypeCopyVO.LEAVETYPE_LACTATION.equalsIgnoreCase(pk_timeitem)) {
				continue;
			}
			if (newMap.get(pk_timeitem) == null)
				continue;
			LeaveTypeCopyVO oldTypeVO = oldMap.get(pk_timeitem);
			LeaveTypeCopyVO newTypeVO = newMap.get(pk_timeitem);
			// ��ѯԭ��֯��¼
			LeaveBalanceVO[] oldLeaveBalanceVOs = queryByCondition(oldhrorg, oldTypeVO, oldPeriod.getTimeyear(),
					oldPeriod.getTimemonth(), oldFromWhereSQL);
			if (ArrayUtils.isEmpty(oldLeaveBalanceVOs))
				continue;
			LeaveBalanceVO oldLeaveVO = null;
			// ԭ��֯�ļ��ڼ�¼�����ж�����ȡδ�����,2013-03-27������ȷ�� �����Ѿ�����������ת��
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

			// ����Ҫ���㣬��Ϊ����Ľ����л����°��ս������ڽ��м���
			// calculate(oldhrorg, oldTypeVO, oldPeriod.getTimeyear(),
			// oldPeriod.getTimemonth(),oldLeaveBalanceVOs, new
			// UFDateTime(enddate.toDate()));
			// ���㣬��ȡ����ʱ��
			SettlementResult secondSettlement = secondSettlement(oldhrorg, pk_timeitem, oldPeriod.getTimeyear(),
					oldPeriod.getTimemonth(), new LeaveBalanceVO[] { oldLeaveVO }, enddate, true, true, true, false,
					null);
			LeaveBalanceVO[] oldSettlementVos = secondSettlement.getSettledVOs();
			if (ArrayUtils.isEmpty(oldSettlementVos))
				continue;
			// �ҳ����ν��������������Ϊת������
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
			// ת����֯��ת��ʱ��,ת�������Ϊ0,��ǰ����Ҳ����
			oldSettVO.setTranslateout(outLength);
			oldSettVO.setRestdayorhour(new UFDouble(0));
			// oldSettVO.setRealdayorhour(new UFDouble(0));
			// ��ʱ�����Ӧ�ÿ����ٷ�����
			oldSettVO.setIsuse(UFBoolean.TRUE);
			// ����ʱ��Ĳ��ܴ��ڽ���ʱ��
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

			// ���ݼ�����λ����ת��
			UFDouble inLength = new UFDouble(outLength.doubleValue());
			if (!(oldTypeVO.getTimeItemUnit() == newTypeVO.getTimeItemUnit())) {
				// ��ת��ΪСʱ
				if (oldTypeVO.getTimeitemunit() == TimeItemCopyVO.TIMEITEMUNIT_DAY
						&& newTypeVO.getTimeItemUnit() == TimeItemCopyVO.TIMEITEMUNIT_HOUR) {
					inLength = outLength.multiply(newTimeRuleVO.getDaytohour());
				} else if (oldTypeVO.getTimeitemunit() == TimeItemCopyVO.TIMEITEMUNIT_HOUR
						&& newTypeVO.getTimeItemUnit() == TimeItemCopyVO.TIMEITEMUNIT_DAY) {
					inLength = outLength.div(newTimeRuleVO.getDaytohour());
				}
			}
			// ��ȡת����֯�ļ�¼
			LeaveBalanceVO[] newLeaveBalanceVOs = queryByCondition(newhrorg, newTypeVO, newPeriod.getTimeyear(),
					newPeriod.getTimemonth(), newFromWhereSQL);
			// ����
			// newLeaveBalanceVOs = calculate(newhrorg, newTypeVO,
			// newPeriod.getTimeyear(), newPeriod.getTimemonth(),
			// newLeaveBalanceVOs, new UFDateTime(enddate.toDate()));
			if (ArrayUtils.isEmpty(newLeaveBalanceVOs))
				continue;
			// �ҵ�û�н������Ϊת������
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
			// ����ת��ʱ��
			newVo.setTranslatein(inLength);
			newVo.setTransflag(UFBoolean.TRUE);
			// ���¼������ʱ������ϵͳ���㣬�˴���������
			// newVo.setRestdayorhour(newVo.getRestdayorhour().add(inLength));
			calculate(newhrorg, newTypeVO, newPeriod.getTimeyear(), newPeriod.getTimemonth(),
					new LeaveBalanceVO[] { newVo }, new UFDateTime(enddate.toDate()));

			// ���洦��������
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
		// ʹ��pk_psndocs����ְ����ְ��Ա�����һ��Pk_psndoc��Ӧ�������ݵ�������޸�Ϊʹ��pk_psnorg
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
		return StringPiecer.getStrArray(queryByCondition, LeaveBalanceVO.PK_PSNORG);// �˴��޷�ʹ����������Ϊ���¹����vo�����ݿ��в�����
	}

	// BEGIN �ź�{21746} ���ݼ�н�YӋ�� 2018/8/29
	// mod tank ������֯��ϵ����
	public UFLiteralDate[] getSpecialBeginEnd(String pk_psndoc, String year, String pk_psnorg) throws BusinessException {

		BaseDAO baseDao = new BaseDAO();
		// ��ȡԱ��ά��������֯��ϵhi_psnorg���������������
		String specialSql = " select workagestartdate,begindate from hi_psnorg where pk_psnorg = '" + pk_psnorg + "' ";
		List<Object[]> specialList = (List<Object[]>) baseDao.executeQuery(specialSql, new ArrayListProcessor());
		UFLiteralDate[] specialBeginEnd = new UFLiteralDate[2];
		if (!CollectionUtils.isEmpty(specialList)) {
			String calculateDate = "";
			if (null != specialList.get(0)) {
				// ��ȡ����������
				calculateDate = specialList.get(0)[0] == null ? specialList.get(0)[1].toString().substring(4, 10)
						: specialList.get(0)[0].toString().substring(4, 10);
				if ("-02-29".equals(calculateDate)) {
					int yearf = Integer.valueOf(year);
					if (!((yearf % 4 == 0 && yearf % 100 != 0) || yearf % 400 == 0)) {
						// ����������� calculateDate ��Ϊ 28 �� yejk 181228 #23932
						calculateDate = "-02-28";
					}
				}
				// ��ȡ��ǰ����������
				UFLiteralDate beginCalculateDate = new UFLiteralDate(year + calculateDate);

				if ("-02-29".equals(calculateDate)) {
					int yearf = Integer.valueOf(year) + 1;
					if (!((yearf % 4 == 0 && yearf % 100 != 0) || yearf % 400 == 0)) {
						// ����������� calculateDate ��Ϊ 28 �� yejk 181228 #23932
						calculateDate = "-02-28";
					}
				}
				// ��ȡ��ǰ���������ս�������
				UFLiteralDate endCalculateDate = new UFLiteralDate(String.valueOf(Integer.valueOf(year) + 1)
						+ calculateDate).getDateBefore(1);
				specialBeginEnd[0] = beginCalculateDate;
				specialBeginEnd[1] = endCalculateDate;
			}
		}
		return specialBeginEnd;
	}
	// END �ź�{21746} ���ݼ�н�YӋ�� 2018/8/29

	// //�ж��Ƿ�Ϊ���ݼ�
	// public Boolean isSpecialHoliday(LeaveTypeCopyVO typeVO) throws
	// BusinessException{
	//
	// if(null == typeVO){
	// return false;
	// }
	//
	// //��ȡϵͳ�϶������ݼٵı���
	// String itemcode =
	// String.valueOf(SysInitQuery.getParaString(typeVO.getPk_org(),
	// "TBMTWHR01"));
	// //��ȡ��ǰ�������ı���
	// String timeitemcode = String.valueOf(typeVO.getTimeitemcode());
	//
	// if(itemcode.equals(timeitemcode)){
	// return true;
	// }else{
	// return false;
	// }
	//
	// }
	// //END �ź�{21481} ���ݼ�н�YӋ�� 2018/8/20

}
