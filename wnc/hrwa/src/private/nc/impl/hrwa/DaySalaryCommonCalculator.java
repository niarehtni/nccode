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

	// 缓存:薪资期间 tank key =
	// pk_hrorg+"::"+pk_wa_class+"::"+calculDate.toStdString();
	Map<String, Map<String, UFLiteralDate>> periodCacheMap = new HashMap<>((int) (DaySalaryCache.MAX_ORG_NUM
			* DaySalaryCache.MAX_DAY_NUM * 3 / DaySalaryCache.LOADER_FACTOR));

	// 缓存:人员工作日历天-平日和国定假日类型 <pk_org::pk_psndoc::开始日期::结束日期,<CalenderStr>>
	Map<String, Set<String>> psnCalenderSetMap = new HashMap<>((int) (DaySalaryCache.MAX_ORG_NUM
			* DaySalaryCache.MAX_PSN_IN_ORG / DaySalaryCache.LOADER_FACTOR));

	// 缓存:字符加减
	StringBuilder sbCache = new StringBuilder();

	// 缓存:考勤期间 天数 tank key = pk_hrorg+"::"+calculDate.toStdString()
	// value:该日期对应的考勤期间的天数
	Map<String, Integer> tbmPeriodCacheMap = new HashMap<>((int) (DaySalaryCache.MAX_ORG_NUM
			* DaySalaryCache.MAX_DAY_NUM * 3 / DaySalaryCache.LOADER_FACTOR));

	// 緩存:兼職人員(即在当前组织没有考勤档案的人员) <pkOrg::开始时间::结束时间,<pk_psndoc>>
	Map<String, Set<String>> psnWithoutTbmSetMap = new HashMap<>(
			(int) (DaySalaryCache.MAX_ORG_NUM / DaySalaryCache.LOADER_FACTOR));

	// 缓存,本次计算人员
	String[] pk_psndocs = null;

	/**
	 * 考勤时薪计算(省略了薪资方案)
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param waItem2DateMap
	 *            <pk_wa_item,<日历天>> <需要计算的薪资项目,<公共薪资项目对应的日期>>
	 * @param pk_item_group
	 * @return Map<String, Double> <日期,日薪>
	 * @throws BusinessException
	 */
	public Map<String, Double> doCalculDaySalaryWithDateList(String userPK, String pk_org, String pk_psndoc,
			Map<String, Set<UFLiteralDate>> waItem2DateMap, String pk_item_group) throws BusinessException {
		// 需要组织,分组,公共薪资项目,来维度来确定一个人每天的日薪,否则不予计算
		if (pk_org == null || pk_item_group == null || waItem2DateMap == null || waItem2DateMap.size() < 0) {
			return new HashMap<>();
		}
		// 获取薪资项目分组信息
		ItemGroupVO groupVO = getGroupItem(pk_item_group);
		if (groupVO == null || null == groupVO.isdaysalarygroup || !groupVO.isdaysalarygroup.booleanValue()) {
			// 不参与日薪计算,直接返回
			return new HashMap<>();
		}
		// 取值方式只能为考勤的取值方式
		if (groupVO.getDaysource() == DaySalaryEnum.MIX_DAYNUMTYPE4
				|| DaySalaryEnum.MIX_DAYNUMTYPE3 == groupVO.getDaysource()) {
			throw new BusinessException("薪資項目分組天數來源設置錯誤,不能用於考勤日薪計算!");
		}

		InSQLCreator insql = new InSQLCreator();
		String waitemInsql = insql.getInSQL(waItem2DateMap.keySet().toArray(new String[0]));
		// 找出本次时间中的最大和最小值 0最小 1最大
		UFLiteralDate[] maxArray = getMaxDate(waItem2DateMap);
		if (maxArray[0] == null || maxArray[1] == null) {
			return new HashMap<>();
		}
		UFLiteralDate begindate = maxArray[0];
		UFLiteralDate enddate = maxArray[1];

		// 需要计算的定调资:
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
		// tank 效率优化
		List<PsndocWadocVO> psndocWadocMapList = (List<PsndocWadocVO>) getDao().executeQuery(strSQL,
				new BeanListProcessor(PsndocWadocVO.class));
		DaySalaryCache cache = DaySalaryCache.getInstance();
		Map<String, Double> rsMap = new HashMap<>(enddate.getDaysAfter(begindate) + 1);
		// 先把本次计算的结果全部置为0,既要防止薪资项目的遗漏,也能防止缓存穿透
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
		// 开始日薪计算:
		for (PsndocWadocVO wadocvo : psndocWadocMapList) {
			Set<UFLiteralDate> dateSet = waItem2DateMap.get(wadocvo.getPk_wa_item());
			// 判断定调资包含的日期
			Set<UFLiteralDate> needCacuDateSet = getDateWithScope(wadocvo.getBegindate(), wadocvo.getEnddate(),
					dateSet.toArray(new UFLiteralDate[0]));
			for (UFLiteralDate calculdate : needCacuDateSet) {
				// 日薪计算天数取值方式
				int daynumtype = groupVO.getDaysource();
				UFDouble nMoneyDecrypted = SalaryDecryptUtil.decrypt(wadocvo.getNmoney());
				DaySalaryVO salaryVO = calcuteCore(pk_org, daynumtype, calculdate, pk_psndoc, wadocvo.getPk_wa_item(),
						pk_item_group, nMoneyDecrypted, null);
				Double salary = rsMap.get(calculdate.toStdString());
				if (salary == null) {
					salary = 0.0d;
				}
				// 放入缓存
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
		// 获取所有日期
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
	 *            *可选 薪资方案 如果pk_item_group的中的取值方式需要用到薪资,那么需要传进薪资方案,否则可为空
	 * @throws BusinessException
	 */
	public void doCalculDaySalaryWithOneWaItem(String userPK, String pk_org, String[] pk_psndocs,
			UFLiteralDate begindate, UFLiteralDate enddate, String pk_wa_item, String pk_item_group, String pk_wa_class)
			throws BusinessException {
		// 需要组织,分组,公共薪资项目,来维度来确定一个人每天的日薪,否则不予计算
		if (pk_org == null || pk_item_group == null || pk_wa_item == null) {
			return;
		}
		// 获取薪资项目分组信息
		ItemGroupVO groupVO = getGroupItem(pk_item_group);
		if (groupVO == null || null == groupVO.isdaysalarygroup || !groupVO.isdaysalarygroup.booleanValue()) {
			// 不参与日薪计算,直接返回
			return;
		}
		String inpsndocsql = new InSQLCreator().getInSQL(pk_psndocs, true);
		// 查询默认工作日历(只为获取日期),不涉及业务逻辑
		String pk_workcalendar = (String) getDao().executeQuery(
				"select workcalendar from org_orgs where pk_org = '" + pk_org + "'", new ColumnProcessor());
		// 需要计算的数据:
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
		// tank 效率优化
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
		// 考勤日薪计算结果
		List<DaySalaryVO> listTbmDaySalaryVOs = new ArrayList<DaySalaryVO>();
		// 开始日薪计算:
		for (Map<String, Object> psndocWadocMap : psndocWadocMapList) {
			// 日薪计算天数取值方式
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
		// 放入缓存
		DaySalaryCache.getInstance().put(listTbmDaySalaryVOs, userPK);
	}

	/**
	 * 日薪计算核心逻辑
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private DaySalaryVO calcuteCore(String pk_org, int daynumtype, UFLiteralDate calculdate, String pk_psndoc,
			String pk_wa_item, String pk_item_group, UFDouble nmoney, String pk_wa_class) throws BusinessException {

		DaySalaryVO salaryVO = new DaySalaryVO();

		// 唯一标识: (人员,组织,薪资项目,薪资项目分组,日期)
		salaryVO.setPk_psndoc(pk_psndoc);
		salaryVO.setPk_hrorg(pk_org);
		salaryVO.setPk_wa_item(pk_wa_item);
		salaryVO.setPk_group_item(pk_item_group);
		salaryVO.setSalarydate(calculdate);
		// 计算日薪
		double daysalarynum = getDaySalaryNum(salaryVO.getPk_psndoc(), pk_org, pk_wa_class, calculdate, daynumtype);
		UFDouble daysalary = nmoney.div(daysalarynum);
		if (daynumtype == DaySalaryEnum.MIX_DAYNUMTYPE4) {// 薪资期间计薪日天数
			// 對應期間的開始結束日期
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
	 * 检查该计算日期是“平/休”
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
	 * Logger.error("未查询到有效的工作日历"); return false; } for (WorkCalendarDateVO
	 * dataVO : calendarVO.getCalendardates()) { if
	 * (dataVO.getCalendardate().isSameDate(calculDate) &&
	 * (CalendarDateType.WEEKENDDAY.ordinal() !=
	 * dataVO.getDatetype().intValue())) { return true; } } return false; }
	 */

	/**
	 * 取日薪计算天数取值
	 * 
	 * @param 人员
	 * 
	 * @param pk_hrorg
	 *            组织
	 * @param pk_wa_class
	 *            薪资方案
	 * @param calculDate
	 *            计算日期
	 * @param daysalarynumtype
	 *            控制参数
	 * @param 本次计算的全部人员
	 * @return
	 * @throws BusinessException
	 */
	private double getDaySalaryNum(String pk_psndoc, String pk_hrorg, String pk_wa_class, UFLiteralDate calculDate,
			int daysalarynumtype) throws BusinessException {
		// 薪资期间计薪日天数（节日+平日）
		if (daysalarynumtype == DaySalaryEnum.MIX_DAYNUMTYPE4) {
			Map<String, UFLiteralDate> periodMap = periodCacheMap.get(getKey(pk_hrorg, pk_wa_class,
					calculDate.toStdString()));
			// 第一次没命中,尝试初始化
			if (periodMap == null) {
				periodCacheMap = initPeriodCache(pk_hrorg, pk_wa_class, calculDate, periodCacheMap);
				periodMap = periodCacheMap.get(getKey(pk_hrorg, pk_wa_class, calculDate.toStdString()));

			}
			// 第二次没命中,单独进行查询
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

			// 没命中,尝试初始化
			if (calenderSet == null) {
				// 正职员工工作日历初始化
				psnCalenderSetMap = initPeriodCache(pk_hrorg, periodMap.get("begindate"), periodMap.get("enddate"));
				// 兼职人员列表初始化
				if (psnWithoutTbmSetMap.get(getKey(pk_hrorg, periodMap.get("begindate").toStdString(),
						periodMap.get("enddate").toStdString())) == null) {
					// 初始化本次的兼职人员
					initPsnWithoutTbm(pk_hrorg, pk_psndocs, periodMap.get("begindate"), periodMap.get("enddate"));
				}
				// 兼职员工工作日历初始化
				// 兼职人员
				psnCalenderSetMap = initOrgDefaultCalender(pk_hrorg, periodMap.get("begindate"),
						periodMap.get("enddate"));
				calenderSet = psnCalenderSetMap.get(getKey(pk_hrorg, pk_psndoc, periodMap.get("begindate")
						.toStdString(), periodMap.get("enddate").toStdString()));
				// 还为空,则此人有问题
				if (calenderSet == null) {
					throw new BusinessException("未找到人員:["
							+ (String) getDao().executeQuery(
									"select code from bd_psndoc where pk_psndoc = '" + pk_psndoc + "'",
									new ColumnProcessor()) + "],在日期[" + calculDate.toStdString() + "]的工作日曆,日薪計算失敗!");
				}
			}
			// 平日+固定假日
			return calenderSet.size();
		}
		// 薪资期间天数
		if (daysalarynumtype == DaySalaryEnum.MIX_DAYNUMTYPE3) {
			Map<String, UFLiteralDate> periodMap = periodCacheMap.get(getKey(pk_hrorg, pk_wa_class,
					calculDate.toStdString()));
			// 第一次没命中,尝试初始化
			if (periodMap == null) {
				periodCacheMap = initPeriodCache(pk_hrorg, pk_wa_class, calculDate, periodCacheMap);
				periodMap = periodCacheMap.get(getKey(pk_hrorg, pk_wa_class, calculDate.toStdString()));
			}
			// 第二次没命中,单独进行查询
			if (periodMap == null) {
				periodMap = getPeriodDate(pk_hrorg, pk_wa_class, calculDate);
			}
			return UFLiteralDate.getDaysBetween(periodMap.get("begindate"), periodMap.get("enddate")) + 1;
		}
		// 固定30天
		if (daysalarynumtype == DaySalaryEnum.MIX_DAYNUMTYPE1) {
			return DaySalaryEnum.DAYSAYSALARYNUM03;
		}
		// 固定21.75天
		if (daysalarynumtype == DaySalaryEnum.MIX_DAYNUMTYPE2) {
			return DaySalaryEnum.TBMSALARYNUM02;
		}
		// 考勤计薪天数
		if (daysalarynumtype == DaySalaryEnum.MIX_DAYNUMTYPE5) {
			Integer day = tbmPeriodCacheMap.get(getKey(pk_hrorg, calculDate.toStdString()));
			if (day == null) {
				// 尝试初始化
				tbmPeriodCacheMap = initTbmPeriodCache(pk_hrorg, calculDate);
				day = tbmPeriodCacheMap.get(getKey(pk_hrorg, calculDate.toStdString()));
				if (day == null) {
					StringBuffer message = new StringBuffer();
					message.append("組織：" + pk_hrorg + "\n");
					message.append("計算日期：" + calculDate.toStdString() + "\n");
					message.append("未維護考勤期間");
					throw new BusinessException(message.toString());
				}
			}
			return day;
		}
		return DaySalaryEnum.DAYSAYSALARYNUM03;
	}

	/**
	 * 查询这个组织,这个日期段所有的人员对应的国定假日和平日,兼职人员使用组织默认的工作日历
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
	 * 初始化兼职人员列表
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
		// 这个组织这个日期段所有有考勤的人员
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
				// 是兼职人员
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
	 * @return 缓存:人员工作日历天-平日和国定假日类型
	 *         <pk_org::pk_psndoc::begindate::enddate,<CalenderStr>>
	 * @throws DAOException
	 */
	private Map<String, Set<String>> initOrgDefaultCalender(String pk_hrorg, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws DAOException {
		Set<String> psnNoTbmSet = psnWithoutTbmSetMap.get(getKey(pk_hrorg, beginDate.toStdString(),
				endDate.toStdString()));
		// 兼职人员为空,直接返回
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
	 * 初始化薪资期间缓存
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
	 * 获取薪资期间开始和结束日期
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
			message.append("組織：" + pk_hrorg + "\n");
			message.append("薪资方案：" + pk_wa_class + "\n");
			message.append("計算日期：" + calculdate.toStdString() + "\n");
			message.append("為維護薪资期间");
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
	 *            获取key
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
