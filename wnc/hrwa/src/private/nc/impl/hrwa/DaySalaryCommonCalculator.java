package nc.impl.hrwa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ProcessorUtils;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.hrwa.wadaysalary.DaySalaryEnum;
import nc.vo.hrwa.wadaysalary.DaySalaryVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.itemgroup.ItemGroupVO;

public class DaySalaryCommonCalculator {

	private BaseDAO dao;

	// ����:н���ڼ� tank key =
	// pk_hrorg+"::"+pk_wa_class+"::"+calculDate.toStdString();
	Map<String, Map<String, UFLiteralDate>> periodCacheMap = new HashMap<>((int) (DaySalaryCache.MAX_ORG_NUM
			* DaySalaryCache.MAX_DAY_NUM * 3 / DaySalaryCache.LOADER_FACTOR));

	// ����:��Ա����������-ƽ�պ͹����������� <pk_org::pk_psndoc::��ʼ����::��������,<CalenderStr>>
	Map<String, Set<String>> psnCalenderSetMap = new HashMap<>((int) (DaySalaryCache.MAX_ORG_NUM
			* DaySalaryCache.MAX_PSN_IN_ORG / DaySalaryCache.LOADER_FACTOR));

	// ����:�ַ��Ӽ�
	StringBuilder sbCache = new StringBuilder();

	// ����:�����ڼ� ���� tank key = pk_hrorg+"::"+calculDate.toStdString()
	// value:�����ڶ�Ӧ�Ŀ����ڼ������
	Map<String, Integer> tbmPeriodCacheMap = new HashMap<>((int) (DaySalaryCache.MAX_ORG_NUM
			* DaySalaryCache.MAX_DAY_NUM * 3 / DaySalaryCache.LOADER_FACTOR));

	// ����:���ˆT(���ڵ�ǰ��֯û�п��ڵ�������Ա) <pkOrg::��ʼʱ��::����ʱ��,<pk_psndoc>>
	Map<String, Set<String>> psnWithoutTbmSetMap = new HashMap<>(
			(int) (DaySalaryCache.MAX_ORG_NUM / DaySalaryCache.LOADER_FACTOR));

	// ����,���μ�����Ա
	String[] pk_psndocs = null;

	/**
	 * ����ʱн����(ʡ����н�ʷ���)
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param waItem2DateMap
	 *            <pk_wa_item,<������>> <��Ҫ�����н����Ŀ,<����н����Ŀ��Ӧ������>>
	 * @param pk_item_group
	 * @return Map<String, Double> <����,��н>
	 * @throws BusinessException
	 */
	public Map<String, Double> doCalculDaySalaryWithDateList(String userPK, String pk_org, String pk_psndoc,
			Map<String, Set<UFLiteralDate>> waItem2DateMap, String pk_item_group) throws BusinessException {
		// ��Ҫ��֯,����,����н����Ŀ,��ά����ȷ��һ����ÿ�����н,���������
		if (pk_org == null || pk_item_group == null || waItem2DateMap == null || waItem2DateMap.size() < 0) {
			return new HashMap<>();
		}
		// ��ȡн����Ŀ������Ϣ
		ItemGroupVO groupVO = getGroupItem(pk_item_group);
		if (groupVO == null || null == groupVO.isdaysalarygroup || !groupVO.isdaysalarygroup.booleanValue()) {
			// ��������н����,ֱ�ӷ���
			return new HashMap<>();
		}
		// ȡֵ��ʽֻ��Ϊ���ڵ�ȡֵ��ʽ
		if (groupVO.getDaysource() == DaySalaryEnum.MIX_DAYNUMTYPE4
				|| DaySalaryEnum.MIX_DAYNUMTYPE3 == groupVO.getDaysource()) {
			throw new BusinessException("н�Y�Ŀ�ֽM�씵��Դ�O���e�`,������춿�����нӋ��!");
		}

		InSQLCreator insql = new InSQLCreator();
		String waitemInsql = insql.getInSQL(waItem2DateMap.keySet().toArray(new String[0]));
		// �ҳ�����ʱ���е�������Сֵ 0��С 1���
		UFLiteralDate[] maxArray = getMaxDate(waItem2DateMap);
		if (maxArray[0] == null || maxArray[1] == null) {
			return new HashMap<>();
		}
		UFLiteralDate begindate = maxArray[0];
		UFLiteralDate enddate = maxArray[1];

		// ��Ҫ����Ķ�����:
		String strSQL = "SELECT " + " pk_org, " + " wadoc.pk_psndoc, " + " wadoc.pk_wa_item, " + " wadoc.nmoney, "
				+ " wadoc.begindate,"
				+ " isnull(wadoc.enddate,'9999-12-31') enddate "
				+ " FROM "
				+ " hi_psndoc_wadoc wadoc "
				+ " WHERE " //
				+ "  wadoc.waflag='Y' " + " and wadoc.pk_wa_item in(" + waitemInsql + ") "
				+ " AND isnull(wadoc.enddate,'9999-12-31') >= '" + begindate.toStdString() + "' "
				+ " and wadoc.begindate <=  '" + enddate.toStdString() + "'" + " AND wadoc.pk_psndoc = '" + pk_psndoc
				+ "'" + " AND wadoc.pk_org = '" + pk_org + "'";
		@SuppressWarnings("unchecked")
		// tank Ч���Ż�
		List<PsndocWadocVO> psndocWadocMapList = (List<PsndocWadocVO>) getDao().executeQuery(strSQL,
				new BeanListProcessor(PsndocWadocVO.class));
		DaySalaryCache cache = DaySalaryCache.getInstance();
		Map<String, Double> rsMap = new HashMap<>(enddate.getDaysAfter(begindate) + 1);
		// �Ȱѱ��μ���Ľ��ȫ����Ϊ0,��Ҫ��ֹн����Ŀ����©,Ҳ�ܷ�ֹ���洩͸
		Set<String> waItemSet = waItem2DateMap.keySet();
		for (String pk_wa_item : waItemSet) {
			Set<UFLiteralDate> dateList = waItem2DateMap.get(pk_wa_item);
			for (UFLiteralDate date : dateList) {
				cache.put(userPK, pk_org, pk_item_group, pk_wa_item, date.toStdString(), pk_psndoc, 0.0d);
				rsMap.put(date.toStdString(), 0.0d);
			}

		}
		if (psndocWadocMapList == null || psndocWadocMapList.size() < 1) {
			return rsMap;
		}
		// ��ʼ��н����:
		for (PsndocWadocVO wadocvo : psndocWadocMapList) {
			Set<UFLiteralDate> dateSet = waItem2DateMap.get(wadocvo.getPk_wa_item());
			// �ж϶����ʰ���������
			Set<UFLiteralDate> needCacuDateSet = getDateWithScope(wadocvo.getBegindate(), wadocvo.getEnddate(),
					dateSet.toArray(new UFLiteralDate[0]));
			for (UFLiteralDate calculdate : needCacuDateSet) {
				// ��н��������ȡֵ��ʽ
				int daynumtype = groupVO.getDaysource();
				UFDouble nMoneyDecrypted = SalaryDecryptUtil.decrypt(wadocvo.getNmoney());
				DaySalaryVO salaryVO = calcuteCore(pk_org, daynumtype, calculdate, pk_psndoc, wadocvo.getPk_wa_item(),
						pk_item_group, nMoneyDecrypted, null);
				Double salary = rsMap.get(calculdate.toStdString());
				if (salary == null) {
					salary = 0.0d;
				}
				// ���뻺��
				cache.put(userPK, pk_org, pk_item_group, wadocvo.getPk_wa_item(), calculdate.toStdString(), pk_psndoc,
						salaryVO.getDaysalary().doubleValue());
				UFDouble dayPay = salaryVO.getDaysalary().add(salary);
				rsMap.put(calculdate.toStdString(), dayPay.toDouble());

			}
		}
		return rsMap;
	}

	/**
	 * 
	 * @param begindate
	 * @param enddate
	 * @param dateList
	 * @return
	 */
	private Set<UFLiteralDate> getDateWithScope(UFLiteralDate begindate, UFLiteralDate enddate, UFLiteralDate[] dateList) {
		Set<UFLiteralDate> rsSet = new HashSet<>();
		for (UFLiteralDate date : dateList) {
			if (date.compareTo(begindate) >= 0 && date.compareTo(enddate) <= 0) {
				rsSet.add(date);
			}
		}
		return rsSet;
	}

	private UFLiteralDate[] getMaxDate(Map<String, Set<UFLiteralDate>> waItem2DateMap) {
		// ��ȡ��������
		Set<UFLiteralDate> allDateSet = new HashSet<>();
		for (Set<UFLiteralDate> dateSet : waItem2DateMap.values()) {
			allDateSet.addAll(dateSet);
		}
		UFLiteralDate[] dateList = allDateSet.toArray(new UFLiteralDate[0]);
		UFLiteralDate[] array = new UFLiteralDate[2];
		if (dateList != null && dateList.length > 0) {
			UFLiteralDate minDate = dateList[0];
			UFLiteralDate maxDate = dateList[0];
			for (int i = 0; i < dateList.length; i++) {
				if (dateList[i] != null) {
					if (dateList[i].before(minDate)) {
						minDate = dateList[i];
					}
					if (dateList[i].after(maxDate)) {
						maxDate = dateList[i];
					}
				}
			}
			array[0] = minDate;
			array[1] = maxDate;
		}
		return array;
	}

	/**
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param begindate
	 * @param enddate
	 * @param pk_wa_item
	 * @param pk_item_group
	 * @param pk_wa_class
	 *            *��ѡ н�ʷ��� ���pk_item_group���е�ȡֵ��ʽ��Ҫ�õ�н��,��ô��Ҫ����н�ʷ���,�����Ϊ��
	 * @throws BusinessException
	 */
	public void doCalculDaySalaryWithOneWaItem(String userPK, String pk_org, String[] pk_psndocs,
			UFLiteralDate begindate, UFLiteralDate enddate, String pk_wa_item, String pk_item_group, String pk_wa_class)
			throws BusinessException {
		// ��Ҫ��֯,����,����н����Ŀ,��ά����ȷ��һ����ÿ�����н,���������
		if (pk_org == null || pk_item_group == null || pk_wa_item == null) {
			return;
		}
		// ��ȡн����Ŀ������Ϣ
		ItemGroupVO groupVO = getGroupItem(pk_item_group);
		if (groupVO == null || null == groupVO.isdaysalarygroup || !groupVO.isdaysalarygroup.booleanValue()) {
			// ��������н����,ֱ�ӷ���
			return;
		}
		String inpsndocsql = new InSQLCreator().getInSQL(pk_psndocs, true);
		// ��ѯĬ�Ϲ�������(ֻΪ��ȡ����),���漰ҵ���߼�
		String pk_workcalendar = (String) getDao().executeQuery(
				"select workcalendar from org_orgs where pk_org = '" + pk_org + "'", new ColumnProcessor());
		// ��Ҫ���������:
		String strSQL = "SELECT " + " calendar.calendardate, " + " wadoc.pk_psndoc, " + " wadoc.nmoney " + " FROM "
				+ " hi_psndoc_wadoc wadoc " + " LEFT JOIN bd_workcalendardate calendar ON "
				+ " (calendar.pk_workcalendar='"
				+ pk_workcalendar
				+ "' "
				+ " and calendar.calendardate >= wadoc.begindate "
				+ " AND ( calendar.calendardate <= wadoc.enddate "
				+ " OR  wadoc.enddate IS NULL )"
				+ " and calendar.calendardate <= '"
				+ enddate.toStdString()
				+ "' "
				+ " AND calendar.calendardate >= '"
				+ begindate.toStdString()
				+ "') "
				// +
				// " LEFT JOIN tbm_psncalendar tpc on (tpc.dr = 0 and tpc.pk_org = '"+pk_org
				// +"' and tpc.calendar = calendar.calendardate and tpc.pk_psndoc = wadoc.pk_psndoc) "
				+ " WHERE " //
				+ " wadoc.pk_wa_item='"
				+ pk_wa_item
				+ "' "
				+ " AND wadoc.waflag='Y' "
				+ " AND wadoc.pk_psndoc in ("
				+ inpsndocsql + ")" + " AND wadoc.pk_org = '" + pk_org + "'" + " and calendar.calendardate is not null";
		@SuppressWarnings("unchecked")
		// tank Ч���Ż�
		List<Map<String, Object>> psndocWadocMapList = (List<Map<String, Object>>) getDao().executeQuery(strSQL,
				new ResultSetProcessor() {
					/**
					 * 
					 */
					private static final long serialVersionUID = -7258285624805654876L;

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						List<Map<String, Object>> results = new ArrayList<>(DaySalaryCache.MAX_PSN_IN_ORG
								* DaySalaryCache.MAX_DAY_NUM);
						while (rs.next()) {
							results.add(ProcessorUtils.toMap(rs));
						}
						return results;
					}
				});
		if (psndocWadocMapList == null || psndocWadocMapList.size() < 1) {
			return;
		}
		this.pk_psndocs = pk_psndocs;
		// ������н������
		List<DaySalaryVO> listTbmDaySalaryVOs = new ArrayList<DaySalaryVO>();
		// ��ʼ��н����:
		for (Map<String, Object> psndocWadocMap : psndocWadocMapList) {
			// ��н��������ȡֵ��ʽ
			int daynumtype = groupVO.getDaysource();
			UFLiteralDate calculdate = new UFLiteralDate((String) psndocWadocMap.get("calendardate"));
			UFDouble nmoney = psndocWadocMap.get("nmoney") != null ? new UFDouble(String.valueOf(psndocWadocMap
					.get("nmoney"))) : UFDouble.ZERO_DBL;
			UFDouble nMoneyDecrypted = SalaryDecryptUtil.decrypt(nmoney);
			String pk_psndoc = (String) psndocWadocMap.get("pk_psndoc");

			DaySalaryVO vo = calcuteCore(pk_org, daynumtype, calculdate, pk_psndoc, pk_wa_item, pk_item_group,
					nMoneyDecrypted, pk_wa_class);

			listTbmDaySalaryVOs.add(vo);
		}
		// ���뻺��
		DaySalaryCache.getInstance().put(listTbmDaySalaryVOs, userPK);
	}

	/**
	 * ��н��������߼�
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private DaySalaryVO calcuteCore(String pk_org, int daynumtype, UFLiteralDate calculdate, String pk_psndoc,
			String pk_wa_item, String pk_item_group, UFDouble nmoney, String pk_wa_class) throws BusinessException {

		DaySalaryVO salaryVO = new DaySalaryVO();

		// Ψһ��ʶ: (��Ա,��֯,н����Ŀ,н����Ŀ����,����)
		salaryVO.setPk_psndoc(pk_psndoc);
		salaryVO.setPk_hrorg(pk_org);
		salaryVO.setPk_wa_item(pk_wa_item);
		salaryVO.setPk_group_item(pk_item_group);
		salaryVO.setSalarydate(calculdate);
		// ������н
		double daysalarynum = getDaySalaryNum(salaryVO.getPk_psndoc(), pk_org, pk_wa_class, calculdate, daynumtype);
		UFDouble daysalary = nmoney.div(daysalarynum);
		if (daynumtype == DaySalaryEnum.MIX_DAYNUMTYPE4) {// н���ڼ��н������
			// �������g���_ʼ�Y������
			Map<String, UFLiteralDate> periodMap = periodCacheMap.get(getKey(pk_org, pk_wa_class,
					calculdate.toStdString()));
			Set<String> calenderSet = psnCalenderSetMap.get(getKey(pk_org, salaryVO.getPk_psndoc(),
					periodMap.get("begindate").toStdString(), periodMap.get("enddate").toStdString()));
			if (!calenderSet.contains(calculdate.toStdString())) {
				daysalary = UFDouble.ZERO_DBL;
			}
		}
		salaryVO.setDaysalary(daysalary);
		return salaryVO;
	}

	private ItemGroupVO getGroupItem(String pk_group_item) throws DAOException {
		String sqlStr = " select * from wa_itemgroup where pk_itemgroup = '" + pk_group_item + "' ";
		ItemGroupVO result = (ItemGroupVO) getDao().executeQuery(sqlStr, new BeanProcessor(ItemGroupVO.class));

		return result;
	}

	/**
	 * ���ü��������ǡ�ƽ/�ݡ�
	 * 
	 * @param calculDate
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	/*
	 * private boolean checkWorkCalendar(UFLiteralDate calculDate, String
	 * pk_org) throws BusinessException { WorkCalendarVO calendarVO =
	 * WorkCalendarPubUtil.getInstance().getWorkCalendarVOByPkOrg(pk_org); if
	 * ((calendarVO == null) ||
	 * (BDValueCheckUtil.isNullORZeroLength(calendarVO.getCalendardates()))) {
	 * Logger.error("δ��ѯ����Ч�Ĺ�������"); return false; } for (WorkCalendarDateVO
	 * dataVO : calendarVO.getCalendardates()) { if
	 * (dataVO.getCalendardate().isSameDate(calculDate) &&
	 * (CalendarDateType.WEEKENDDAY.ordinal() !=
	 * dataVO.getDatetype().intValue())) { return true; } } return false; }
	 */

	/**
	 * ȡ��н��������ȡֵ
	 * 
	 * @param ��Ա
	 * 
	 * @param pk_hrorg
	 *            ��֯
	 * @param pk_wa_class
	 *            н�ʷ���
	 * @param calculDate
	 *            ��������
	 * @param daysalarynumtype
	 *            ���Ʋ���
	 * @param ���μ����ȫ����Ա
	 * @return
	 * @throws BusinessException
	 */
	private double getDaySalaryNum(String pk_psndoc, String pk_hrorg, String pk_wa_class, UFLiteralDate calculDate,
			int daysalarynumtype) throws BusinessException {
		// н���ڼ��н������������+ƽ�գ�
		if (daysalarynumtype == DaySalaryEnum.MIX_DAYNUMTYPE4) {
			Map<String, UFLiteralDate> periodMap = periodCacheMap.get(getKey(pk_hrorg, pk_wa_class,
					calculDate.toStdString()));
			// ��һ��û����,���Գ�ʼ��
			if (periodMap == null) {
				periodCacheMap = initPeriodCache(pk_hrorg, pk_wa_class, calculDate, periodCacheMap);
				periodMap = periodCacheMap.get(getKey(pk_hrorg, pk_wa_class, calculDate.toStdString()));

			}
			// �ڶ���û����,�������в�ѯ
			if (periodMap == null) {
				periodMap = getPeriodDate(pk_hrorg, pk_wa_class, calculDate);
			}

			/*
			 * UFDouble temp =
			 * getCalendarPubService().getWorkCalndPsnWageDays(pk_hrorg,
			 * periodMap.get("begindate"), periodMap.get("enddate"));
			 */
			/*
			 * Integer temp2 =
			 * UFLiteralDate.getDaysBetween(periodMap.get("begindate"),
			 * periodMap.get("enddate")) + 1;
			 */
			Set<String> calenderSet = psnCalenderSetMap.get(getKey(pk_hrorg, pk_psndoc, periodMap.get("begindate")
					.toStdString(), periodMap.get("enddate").toStdString()));

			// û����,���Գ�ʼ��
			if (calenderSet == null) {
				// ��ְԱ������������ʼ��
				psnCalenderSetMap = initPeriodCache(pk_hrorg, periodMap.get("begindate"), periodMap.get("enddate"));
				// ��ְ��Ա�б��ʼ��
				if (psnWithoutTbmSetMap.get(getKey(pk_hrorg, periodMap.get("begindate").toStdString(),
						periodMap.get("enddate").toStdString())) == null) {
					// ��ʼ�����εļ�ְ��Ա
					initPsnWithoutTbm(pk_hrorg, pk_psndocs, periodMap.get("begindate"), periodMap.get("enddate"));
				}
				// ��ְԱ������������ʼ��
				// ��ְ��Ա
				psnCalenderSetMap = initOrgDefaultCalender(pk_hrorg, periodMap.get("begindate"),
						periodMap.get("enddate"));
				calenderSet = psnCalenderSetMap.get(getKey(pk_hrorg, pk_psndoc, periodMap.get("begindate")
						.toStdString(), periodMap.get("enddate").toStdString()));
				// ��Ϊ��,�����������
				if (calenderSet == null) {
					throw new BusinessException("δ�ҵ��ˆT:["
							+ (String) getDao().executeQuery(
									"select code from bd_psndoc where pk_psndoc = '" + pk_psndoc + "'",
									new ColumnProcessor()) + "],������[" + calculDate.toStdString() + "]�Ĺ����Օ�,��нӋ��ʧ��!");
				}
			}
			// ƽ��+�̶�����
			return calenderSet.size();
		}
		// н���ڼ�����
		if (daysalarynumtype == DaySalaryEnum.MIX_DAYNUMTYPE3) {
			Map<String, UFLiteralDate> periodMap = periodCacheMap.get(getKey(pk_hrorg, pk_wa_class,
					calculDate.toStdString()));
			// ��һ��û����,���Գ�ʼ��
			if (periodMap == null) {
				periodCacheMap = initPeriodCache(pk_hrorg, pk_wa_class, calculDate, periodCacheMap);
				periodMap = periodCacheMap.get(getKey(pk_hrorg, pk_wa_class, calculDate.toStdString()));
			}
			// �ڶ���û����,�������в�ѯ
			if (periodMap == null) {
				periodMap = getPeriodDate(pk_hrorg, pk_wa_class, calculDate);
			}
			return UFLiteralDate.getDaysBetween(periodMap.get("begindate"), periodMap.get("enddate")) + 1;
		}
		// �̶�30��
		if (daysalarynumtype == DaySalaryEnum.MIX_DAYNUMTYPE1) {
			return DaySalaryEnum.DAYSAYSALARYNUM03;
		}
		// �̶�21.75��
		if (daysalarynumtype == DaySalaryEnum.MIX_DAYNUMTYPE2) {
			return DaySalaryEnum.TBMSALARYNUM02;
		}
		// ���ڼ�н����
		if (daysalarynumtype == DaySalaryEnum.MIX_DAYNUMTYPE5) {
			Integer day = tbmPeriodCacheMap.get(getKey(pk_hrorg, calculDate.toStdString()));
			if (day == null) {
				// ���Գ�ʼ��
				tbmPeriodCacheMap = initTbmPeriodCache(pk_hrorg, calculDate);
				day = tbmPeriodCacheMap.get(getKey(pk_hrorg, calculDate.toStdString()));
				if (day == null) {
					StringBuffer message = new StringBuffer();
					message.append("�M����" + pk_hrorg + "\n");
					message.append("Ӌ�����ڣ�" + calculDate.toStdString() + "\n");
					message.append("δ�S�o�������g");
					throw new BusinessException(message.toString());
				}
			}
			return day;
		}
		return DaySalaryEnum.DAYSAYSALARYNUM03;
	}

	/**
	 * ��ѯ�����֯,������ڶ����е���Ա��Ӧ�Ĺ������պ�ƽ��,��ְ��Աʹ����֯Ĭ�ϵĹ�������
	 * 
	 * @param pk_hrorg
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws DAOException
	 */
	private Map<String, Set<String>> initPeriodCache(String pk_hrorg, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws DAOException {
		String sql = "select pk_psndoc,calendar,'" + pk_hrorg + "' pk_org, '" + beginDate.toStdString() + "' bgdate ,'"
				+ endDate.toStdString() + "' enddate" + " from tbm_psncalendar "
				+ " where dr = 0 and (date_daytype = 0 or date_daytype = 2) and " + " pk_org = '" + pk_hrorg + "' "
				+ " and calendar BETWEEN '" + beginDate.toStdString() + "' and '" + endDate.toStdString() + "'";
		@SuppressWarnings("unchecked")
		Map<String, Set<String>> rs = (Map<String, Set<String>>) getDao().executeQuery(sql, new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;

			// Map<String, Set<String>> rsMap = new
			// HashMap<>((int)(DaySalaryCache.MAX_PSN_IN_ORG*DaySalaryCache.MAX_DAY_NUM/DaySalaryCache.LOADER_FACTOR));
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					if (rs.getString(1) != null && rs.getString(2) != null) {
						Set<String> tempSet = psnCalenderSetMap.get(getKey(rs.getString(3), rs.getString(1),
								rs.getString(4), rs.getString(5)));
						if (tempSet == null) {
							tempSet = new HashSet<>();
						}
						tempSet.add(rs.getString(2));
						psnCalenderSetMap.put(
								getKey(rs.getString(3), rs.getString(1), rs.getString(4), rs.getString(5)), tempSet);
					}
				}
				return psnCalenderSetMap;
			}
		});
		return rs;
	}

	/**
	 * ��ʼ����ְ��Ա�б�
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param ufLiteralDate2
	 * @param ufLiteralDate
	 * @throws DAOException
	 */
	private void initPsnWithoutTbm(String pk_org, String[] pk_psndocs, UFLiteralDate begindate, UFLiteralDate enddate)
			throws DAOException {
		if (pk_psndocs == null || pk_psndocs.length <= 0) {
			psnWithoutTbmSetMap.put(getKey(pk_org, begindate.toStdString(), enddate.toStdString()),
					new HashSet<String>());
		}
		// �����֯������ڶ������п��ڵ���Ա
		String sql = "select DISTINCT pk_psndoc from tbm_psndoc where dr = 0 and pk_org = '" + pk_org + "' "
				+ " and begindate <= '" + enddate.toStdString() + "' and isnull(enddate,'9999-12-31') >= '"
				+ begindate.toStdString() + "' ";
		@SuppressWarnings("unchecked")
		Set<String> psntbmSet = (Set<String>) getDao().executeQuery(sql, new ResultSetProcessor() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -9209718504160460433L;
			private Set<String> psnSet = new HashSet<>();

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					psnSet.add(rs.getString(1));
				}
				return psnSet;
			}
		});
		for (String pk_psndoc : pk_psndocs) {
			if (!psntbmSet.contains(pk_psndoc)) {
				// �Ǽ�ְ��Ա
				Set<String> psnSet = psnWithoutTbmSetMap.get(getKey(pk_org, begindate.toStdString(),
						enddate.toStdString()));
				if (psnSet == null) {
					psnSet = new HashSet<String>();
				}
				psnSet.add(pk_psndoc);
				psnWithoutTbmSetMap.put(getKey(pk_org, begindate.toStdString(), enddate.toStdString()), psnSet);
			}
		}

	}

	/**
	 * @param pk_hrorg
	 * @param beginDate
	 * @param endDate
	 * @return ����:��Ա����������-ƽ�պ͹�����������
	 *         <pk_org::pk_psndoc::begindate::enddate,<CalenderStr>>
	 * @throws DAOException
	 */
	private Map<String, Set<String>> initOrgDefaultCalender(String pk_hrorg, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws DAOException {
		Set<String> psnNoTbmSet = psnWithoutTbmSetMap.get(getKey(pk_hrorg, beginDate.toStdString(),
				endDate.toStdString()));
		// ��ְ��ԱΪ��,ֱ�ӷ���
		if (psnNoTbmSet == null || psnNoTbmSet.size() <= 0) {
			return psnCalenderSetMap;
		}
		String sql = "select calendardate from bd_workcalendardate dat "
				+ " inner join org_orgs org on (org.pk_org = '" + pk_hrorg + "' and org.dr = 0) "
				+ " inner join bd_workcalendar cal on (cal.pk_workcalendar = org.workcalendar and cal.dr = 0 "
				+ " and dat.pk_workcalendar = cal.pk_workcalendar) "
				+ " where dat.dr = 0 and (datetype = 2 or datetype = 0) and calendardate BETWEEN '"
				+ beginDate.toStdString() + "' and '" + endDate.toStdString() + "'";
		@SuppressWarnings("unchecked")
		Set<String> rs = (Set<String>) getDao().executeQuery(sql, new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;
			Set<String> rsSet = new HashSet<>(
					(int) (DaySalaryCache.MAX_PSN_IN_ORG * DaySalaryCache.MAX_DAY_NUM / DaySalaryCache.LOADER_FACTOR));

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					if (rs.getString(1) != null) {
						rsSet.add(rs.getString(1));
					}
				}
				return rsSet;
			}
		});
		for (String pk_psndoc : psnNoTbmSet) {
			psnCalenderSetMap.put(getKey(pk_hrorg, pk_psndoc, beginDate.toStdString(), endDate.toStdString()), rs);
		}
		return psnCalenderSetMap;
	}

	/**
	 * ��ʼ��н���ڼ仺��
	 * 
	 * @param pk_hrorg
	 * @param pk_wa_class
	 * @param calculdate
	 * @param periodCacheMap
	 * @return
	 * @throws DAOException
	 */
	private Map<String, Map<String, UFLiteralDate>> initPeriodCache(String pk_hrorg, String pk_wa_class,
			UFLiteralDate calculdate, Map<String, Map<String, UFLiteralDate>> periodCacheMap) throws DAOException {
		String qrySql = "SELECT\n" + "	period.cstartdate,\n" + "	period.cenddate\n" + "FROM\n"
				+ "	wa_waclass waclass\n"
				+ "LEFT JOIN wa_period period ON period.pk_periodscheme = waclass.pk_periodscheme\n" + "WHERE\n"
				+ "	waclass.pk_wa_class = '" + pk_wa_class + "'\n" + "AND period.cstartdate <= '"
				+ calculdate.toStdString() + "'\n" + "AND period.cenddate >= '" + calculdate.toStdString() + "'";
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> listMaptemp = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(qrySql,
				new MapListProcessor());
		if (listMaptemp != null && listMaptemp.size() > 0) {

			HashMap<String, Object> hashMap = listMaptemp.get(0);
			String begindate = hashMap.get("cstartdate").toString();
			String enddate = hashMap.get("cenddate").toString();
			UFLiteralDate begindateUlt = new UFLiteralDate(begindate);
			UFLiteralDate enddateUlt = new UFLiteralDate(enddate);

			Map<String, UFLiteralDate> map = new HashMap<String, UFLiteralDate>();
			map.put("begindate", begindateUlt);
			map.put("enddate", enddateUlt);

			UFLiteralDate[] allDates = CommonUtils.createDateArray(begindateUlt, enddateUlt);
			for (UFLiteralDate date : allDates) {
				periodCacheMap.put(getKey(pk_hrorg, pk_wa_class, date.toStdString()), map);
			}

		}
		return periodCacheMap;
	}

	private Map<String, Integer> initTbmPeriodCache(String pk_hrorg, UFLiteralDate calculDate) throws DAOException {
		String sqlsys = "SELECT\n" + "	begindate,\n" + "	enddate\n" + "FROM\n" + "	tbm_period\n" + "WHERE\n"
				+ "	begindate <= '" + calculDate + "'\n" + "AND enddate >= '" + calculDate + "'\n" + "AND pk_org = '"
				+ pk_hrorg + "'\n" + "AND isnull(dr, 0) = 0";
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> listMaptemp = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(
				sqlsys.toString(), new MapListProcessor());
		String begindate = null;
		String enddate = null;
		if (listMaptemp != null && listMaptemp.size() > 0) {
			HashMap<String, Object> hashMap = listMaptemp.get(0);
			begindate = hashMap.get("begindate").toString();
			enddate = hashMap.get("enddate").toString();
			UFLiteralDate beginDateUlt = new UFLiteralDate(begindate);
			UFLiteralDate endDateUlt = new UFLiteralDate(enddate);
			UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDateUlt, endDateUlt);
			for (UFLiteralDate date : allDates) {
				tbmPeriodCacheMap.put(getKey(pk_hrorg, date.toStdString()),
						UFLiteralDate.getDaysBetween(new UFLiteralDate(begindate), new UFLiteralDate(enddate)) + 1);
			}
		}
		return tbmPeriodCacheMap;
	}

	/**
	 * ��ȡн���ڼ俪ʼ�ͽ�������
	 * 
	 * @param pk_hrorg
	 * @param pk_wa_class
	 * @param calculdate
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, UFLiteralDate> getPeriodDate(String pk_hrorg, String pk_wa_class, UFLiteralDate calculdate)
			throws BusinessException {
		String qrySql = "SELECT\n" + "	period.cstartdate,\n" + "	period.cenddate\n" + "FROM\n"
				+ "	wa_waclass waclass\n"
				+ "LEFT JOIN wa_period period ON period.pk_periodscheme = waclass.pk_periodscheme\n" + "WHERE\n"
				+ "	waclass.pk_wa_class = '" + pk_wa_class + "'\n" + "AND period.cstartdate <= '"
				+ calculdate.toStdString() + "'\n" + "AND period.cenddate >= '" + calculdate.toStdString() + "'";
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> listMaptemp = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(qrySql,
				new MapListProcessor());
		if (listMaptemp != null && listMaptemp.size() > 0) {
			HashMap<String, Object> hashMap = listMaptemp.get(0);
			String begindate = hashMap.get("cstartdate").toString();
			String enddate = hashMap.get("cenddate").toString();
			Map<String, UFLiteralDate> map = new HashMap<String, UFLiteralDate>();
			map.put("begindate", new UFLiteralDate(begindate));
			map.put("enddate", new UFLiteralDate(enddate));
			return map;
		} else {
			StringBuffer message = new StringBuffer();
			message.append("�M����" + pk_hrorg + "\n");
			message.append("н�ʷ�����" + pk_wa_class + "\n");
			message.append("Ӌ�����ڣ�" + calculdate.toStdString() + "\n");
			message.append("��S�oн���ڼ�");
			throw new BusinessException(message.toString());
		}

	}

	private BaseDAO getDao() {
		if (null == dao) {
			dao = new BaseDAO();
		}
		return dao;
	}

	/*
	 * private IWorkCalendarPubService getCalendarPubService() { if (null ==
	 * calendarPubService) { calendarPubService =
	 * NCLocator.getInstance().lookup(IWorkCalendarPubService.class); } return
	 * calendarPubService; }
	 */

	/**
	 * 
	 * @param keys
	 *            ��ȡkey
	 * @return
	 */
	private String getKey(String... keys) {
		int flag = 0;
		sbCache.delete(0, sbCache.length());
		if (keys != null && keys.length > 0) {
			for (String key : keys) {
				if (flag != 0) {
					sbCache.append("::");
				}
				flag += 1;
				sbCache.append(key);
			}
		}
		return sbCache.toString();
	}

}
