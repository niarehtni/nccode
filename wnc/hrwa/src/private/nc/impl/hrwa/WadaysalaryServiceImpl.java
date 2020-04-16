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
import nc.bs.framework.common.InvocationInfoProxy;
import nc.hr.utils.InSQLCreator;
import nc.itf.hrwa.IWadaysalaryService;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.category.WaClassVO;

public class WadaysalaryServiceImpl implements IWadaysalaryService {

	// <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	/*
	 * @Override public void calculSalaryByHrorg(String pk_hrorg, UFLiteralDate
	 * calculDate) throws BusinessException { // 获取需要计算的人员的数组 String
	 * calculDateStr = calculDate.toStdString(); String pk_psndocs[] =
	 * getPkPsndocs(pk_hrorg, calculDateStr); if (pk_psndocs == null ||
	 * pk_psndocs.length < 1) { nc.bs.logging.Logger.warn(pk_hrorg +
	 * "：该组织下不存在考勤人员"); return; } // 查询当前组织下需要固定周期计算日薪的薪资方案 String[]
	 * pk_wa_classs = getWaClassByOrg(pk_hrorg); // 查询满足条件的定调资项目 //
	 * Ares.Tank查询的同时,带出每个薪资项目的分组信息(天数取值方式),并且过滤掉没有分组信息薪资项目,和不需要进行日薪计算的分组 String
	 * inpsndocsql = new InSQLCreator().getInSQL(pk_psndocs, true); String
	 * qrySql = "SELECT\n" + "	wadoc.pk_psndoc,\n" + "	wadoc.pk_psnjob,\n" +
	 * "	wadoc.pk_psndoc_sub,\n" + "	wadoc.ts as wadocts,\n" +
	 * "	wadoc.pk_wa_item,\n" + "	wadoc.nmoney,\n" +
	 * " itemgroup.pk_itemgroup, itemgroup.ts itemgroupts, itemgroup.daysource "
	 * + "FROM\n" + "	hi_psndoc_wadoc wadoc\n" +
	 * "LEFT JOIN wa_item waitem ON wadoc.pk_wa_item = waitem.pk_wa_item\n" +
	 * " inner join wa_itemgroupmember groupmember on (waitem.pk_wa_item = groupmember.pk_waitem and groupmember.dr = 0) "
	 * +
	 * " inner join wa_itemgroup itemgroup on (itemgroup.pk_itemgroup = groupmember.pk_itemgroup "
	 * + " and itemgroup.dr = 0 and itemgroup.isdaysalarygroup = 'Y') " +
	 * "WHERE\n" + "	wadoc.pk_psndoc IN (" + inpsndocsql + ")\n" +
	 * "AND wadoc.waflag = 'Y'\n"// 发放标志为Y + "AND wadoc.begindate <= '" +
	 * calculDateStr + "'\n" + "AND (\n" + "	wadoc.enddate >= '" + calculDateStr
	 * + "'\n" + "	OR wadoc.enddate IS NULL\n" + ")"; HashMap<Object,
	 * List<GeneralVO>> psndocWadocMap = executeQuery(qrySql); if
	 * (psndocWadocMap == null) { nc.bs.logging.Logger.warn("查询结果为空"); return; }
	 * // 查询参数设置-日薪计算天数取值方式 //初始化参数缓存 tank 2019年10月14日14:57:44
	 * //Map<String,Integer> paramCacheMap = new HashMap<>(); //初始化 期间缓存 tank
	 * 2019年10月14日14:52:02 Map<String,Map<String, UFLiteralDate>> periodCacheMap
	 * = new HashMap<>(); // 日薪计算结果 List<DaySalaryVO> listDaySalaryVOs = new
	 * ArrayList<DaySalaryVO>(); // 对薪资方案进行遍历 for (int i = 0; i <
	 * pk_wa_classs.length; i++) { String pk_wa_class = pk_wa_classs[i];
	 * 
	 * // 按照人员遍历 for (Map.Entry<Object, List<GeneralVO>> e :
	 * psndocWadocMap.entrySet()) { String pk_psndoc = e.getKey().toString();
	 * List<GeneralVO> listGeneralVOs = e.getValue(); // 对每个人的定调资项目遍历 for (int j
	 * = 0, size = listGeneralVOs.size(); j < size; j++) { DaySalaryVO salaryVO
	 * = new DaySalaryVO(); GeneralVO generalVO = listGeneralVOs.get(j);
	 * salaryVO.setSalarydate(calculDate); salaryVO.setPk_wa_class(pk_wa_class);
	 * salaryVO.setCyear(calculDate.getYear());
	 * salaryVO.setCperiod(calculDate.getMonth());
	 * salaryVO.setPk_hrorg(pk_hrorg); salaryVO.setPk_psndoc(pk_psndoc);
	 * salaryVO
	 * .setPk_psndoc_sub(generalVO.getAttributeValue("pk_psndoc_sub").toString
	 * ()); salaryVO.setWadocts(new
	 * UFDateTime(generalVO.getAttributeValue("wadocts").toString()));
	 * salaryVO.setPk_psnjob
	 * (generalVO.getAttributeValue("pk_psnjob").toString());
	 * salaryVO.setPk_wa_item
	 * (generalVO.getAttributeValue("pk_wa_item").toString()); UFDouble nmoney =
	 * generalVO.getAttributeValue("nmoney") != null ? new UFDouble(generalVO
	 * .getAttributeValue("nmoney").toString()) : UFDouble.ZERO_DBL; // Ares
	 * Tank 区别计算每个分组的薪资项 // 查询参数设置-日薪计算天数取值方式 每个薪资项会有多个取值方式 int daynumtype =
	 * Integer.valueOf(generalVO.getAttributeValue("daysource").toString()); //
	 * 获取日薪计算天数 double daysalarynum = getDaySalaryNum(pk_hrorg, pk_wa_class,
	 * calculDate, daynumtype,periodCacheMap); // 存入分组信息 ares.tank
	 * salaryVO.setPk_group_item
	 * (generalVO.getAttributeValue("pk_itemgroup").toString()); Object gourpTs
	 * = generalVO.getAttributeValue("itemgroupts");
	 * salaryVO.setGroupitemts(gourpTs == null ? null : new
	 * UFDateTime(gourpTs.toString())); // 定调资日薪、时薪 UFDouble daysalary =
	 * nmoney.div(daysalarynum); UFDouble hoursalary =
	 * daysalary.div(DaySalaryEnum.HOURSALARYNUM); if (daynumtype ==
	 * DaySalaryEnum.MIX_DAYNUMTYPE4) {// 薪资期间计薪日天数 if
	 * (!checkWorkCalendar(calculDate, pk_hrorg)) { // 如果计算日期是“休”，则日薪为0
	 * daysalary = UFDouble.ZERO_DBL; hoursalary = UFDouble.ZERO_DBL; } }
	 * salaryVO.setDaysalary(daysalary); salaryVO.setHoursalary(hoursalary);
	 * listDaySalaryVOs.add(salaryVO); } } } // 在插入數據之前，做清空處理
	 * getDao().deleteByClause(DaySalaryVO.class, "pk_hrorg='" + pk_hrorg +
	 * "' and salarydate='" + calculDate.toStdString() + "'");
	 * getDao().insertVOList(listDaySalaryVOs); // 检查是否有需要重算的数据，并重算
	 * checkDaysalaryAndRecalculate(pk_hrorg, calculDate);
	 * 
	 * }
	 */

	// <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	/*
	 * @Override public void calculSalaryByWaItem(String pk_hrorg, String
	 * pk_wa_class, UFLiteralDate calculDate, String pk_psndoc, String[]
	 * pk_wa_items) throws BusinessException { // 获取需要计算的人员的数组 String
	 * calculDateStr = calculDate.toStdString(); //初始化参数缓存 tank
	 * 2019年10月14日14:57:44 //Map<String,Integer> paramCacheMap = new
	 * HashMap<>(); //int daynumtype = getSysintValue(pk_hrorg,
	 * DaySalaryEnum.DAYSYSINT,paramCacheMap); //初始化 期间缓存 tank
	 * 2019年10月14日14:52:02 Map<String,Map<String, UFLiteralDate>> periodCacheMap
	 * = new HashMap<>(); //double daysalarynum = getDaySalaryNum(pk_hrorg,
	 * pk_wa_class, calculDate, daynumtype,periodCacheMap); // 查询满足条件的定调资项目
	 * String inwaitemsql = new InSQLCreator().getInSQL(pk_wa_items, true);
	 * String qrySql = "SELECT\n" + "	wadoc.pk_psndoc,\n" +
	 * "	wadoc.pk_psnjob,\n" + "	wadoc.pk_psndoc_sub,\n" +
	 * "	wadoc.ts as wadocts,\n" + "	wadoc.pk_wa_item,\n" + "	wadoc.nmoney,\n" +
	 * " itemgroup.pk_itemgroup,  itemgroup.ts itemgroupts,  itemgroup.daysource "
	 * + " FROM\n" + "	hi_psndoc_wadoc wadoc\n" +
	 * "LEFT JOIN wa_item waitem ON wadoc.pk_wa_item = waitem.pk_wa_item\n" +
	 * " inner join wa_itemgroupmember groupmember on (waitem.pk_wa_item = groupmember.pk_waitem and groupmember.dr = 0) "
	 * +
	 * " inner join wa_itemgroup itemgroup on (itemgroup.pk_itemgroup = groupmember.pk_itemgroup "
	 * + " and itemgroup.dr = 0 and itemgroup.isdaysalarygroup = 'Y') " +
	 * "WHERE\n" + "	wadoc.pk_psndoc = '" + pk_psndoc + "'\n" +
	 * "AND wadoc.waflag = 'Y'\n"// 发放标志为Y + "AND wadoc.begindate <= '" +
	 * calculDateStr + "'\n" + "AND (\n" + "	wadoc.enddate >= '" + calculDateStr
	 * + "'\n" + "	OR wadoc.enddate IS NULL\n" + ")\n" +
	 * "AND wadoc.pk_wa_item in (" + inwaitemsql + ")"; HashMap<Object,
	 * List<GeneralVO>> psndocWadocMap = executeQuery(qrySql); if
	 * (psndocWadocMap == null) { nc.bs.logging.Logger.warn("查询结果为空"); return; }
	 * List<DaySalaryVO> listDaySalaryVOs = new ArrayList<DaySalaryVO>(); for
	 * (Map.Entry<Object, List<GeneralVO>> e : psndocWadocMap.entrySet()) {
	 * List<GeneralVO> listGeneralVOs = e.getValue(); for (int i = 0, size =
	 * listGeneralVOs.size(); i < size; i++) { GeneralVO generalVO =
	 * listGeneralVOs.get(i); DaySalaryVO salaryVO = new DaySalaryVO();
	 * salaryVO.setSalarydate(calculDate); salaryVO.setPk_wa_class(pk_wa_class);
	 * salaryVO.setCyear(calculDate.getYear());
	 * salaryVO.setCperiod(calculDate.getMonth());
	 * salaryVO.setPk_hrorg(pk_hrorg); salaryVO.setPk_psndoc(pk_psndoc);
	 * salaryVO
	 * .setPk_psndoc_sub(generalVO.getAttributeValue("pk_psndoc_sub").toString
	 * ()); salaryVO.setWadocts(new
	 * UFDateTime(generalVO.getAttributeValue("wadocts").toString()));
	 * salaryVO.setPk_psnjob
	 * (generalVO.getAttributeValue("pk_psnjob").toString());
	 * salaryVO.setPk_wa_item
	 * (generalVO.getAttributeValue("pk_wa_item").toString()); UFDouble nmoney =
	 * generalVO.getAttributeValue("nmoney") != null ? new UFDouble(generalVO
	 * .getAttributeValue("nmoney").toString()) : UFDouble.ZERO_DBL; // 定调资日薪、时薪
	 * // 因为每个薪资项目可能对应不同的分组,而且一个薪资项目可能对应多个分组,所以放在循环中查询,这段可以优化 int daynumtype =
	 * Integer.valueOf(generalVO.getAttributeValue("daysource").toString());
	 * double daysalarynum = getDaySalaryNum(pk_hrorg, pk_wa_class, calculDate,
	 * daynumtype,periodCacheMap); UFDouble daysalary =
	 * nmoney.div(daysalarynum); UFDouble hoursalary =
	 * daysalary.div(DaySalaryEnum.HOURSALARYNUM); // 存入分组信息 ares.tank
	 * salaryVO.setPk_group_item
	 * (generalVO.getAttributeValue("pk_itemgroup").toString()); Object gourpTs
	 * = generalVO.getAttributeValue("itemgroupts");
	 * salaryVO.setGroupitemts(gourpTs == null ? null : new
	 * UFDateTime(gourpTs.toString())); if (daynumtype ==
	 * DaySalaryEnum.MIX_DAYNUMTYPE4) {// 薪资期间计薪日天数 if
	 * (!checkWorkCalendar(calculDate, pk_hrorg)) { // 如果计算日期是“休”，则日薪为0
	 * daysalary = UFDouble.ZERO_DBL; hoursalary = UFDouble.ZERO_DBL; } }
	 * salaryVO.setDaysalary(daysalary); salaryVO.setHoursalary(hoursalary);
	 * listDaySalaryVOs.add(salaryVO); } } // 在插入數據之前，做清空處理
	 * getDao().deleteByClause( DaySalaryVO.class, "pk_hrorg='" + pk_hrorg +
	 * "' and pk_wa_class='" + pk_wa_class + "' and pk_psndoc = '" + pk_psndoc +
	 * "' and salarydate='" + calculDate.toStdString() + "' and pk_wa_item in ("
	 * + inwaitemsql + ")"); getDao().insertVOList(listDaySalaryVOs);
	 * 
	 * }
	 */

	// <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	/**
	 * 检查某个组织下面的日薪数据是否需要重算，并重算
	 * 
	 * @param pk_org
	 * @param calculdate
	 * @throws BusinessException
	 *             <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	 */
	/*
	 * public void checkDaysalaryAndRecalculate(String pk_org, UFLiteralDate
	 * calculdate) throws BusinessException { String checkSql =
	 * " SELECT daysalary.salarydate, daysalary.pk_wa_class, daysalary.pk_psndoc, "
	 * + " daysalary.pk_wa_item  " + " FROM wa_daysalary daysalary " +
	 * " LEFT JOIN hi_psndoc_wadoc wadoc ON ( " +
	 * " daysalary.pk_psndoc = wadoc.pk_psndoc " +
	 * " AND daysalary.pk_wa_item = wadoc.pk_wa_item " +
	 * " AND daysalary.salarydate >= wadoc.begindate " +
	 * " AND ( daysalary.salarydate <= wadoc.enddate " +
	 * " OR wadoc.enddate IS NULL ) ) " +
	 * " inner join wa_itemgroupmember groupmember on " +
	 * " (daysalary.pk_wa_item = groupmember.pk_waitem and groupmember.dr = 0) "
	 * +
	 * " inner join wa_itemgroup itemgroup on (itemgroup.pk_itemgroup = groupmember.pk_itemgroup "
	 * + " and itemgroup.dr = 0 and itemgroup.isdaysalarygroup = 'Y')  " +
	 * " WHERE" + "	daysalary.salarydate < '" + calculdate.toStdString() + "' "
	 * +
	 * " AND (daysalary.wadocts <> wadoc.ts OR daysalary.wadocts IS NULL or itemgroup.ts "
	 * + " <> daysalary.groupitemts or daysalary.groupitemts is null) " +
	 * " AND daysalary.pk_hrorg = '" + pk_org + "'\n" + " UNION ALL " +
	 * " SELECT daysalary.salarydate, daysalary.pk_wa_class, " +
	 * " daysalary.pk_psndoc, daysalary.pk_wa_item " +
	 * " FROM wa_daysalary daysalary " +
	 * " LEFT JOIN hi_psndoc_wadoc wadoc ON daysalary.pk_psndoc_sub = wadoc.pk_psndoc_sub "
	 * +
	 * " inner join wa_itemgroupmember groupmember on (daysalary.pk_wa_item = groupmember.pk_waitem and groupmember.dr = 0) "
	 * +
	 * " inner join wa_itemgroup itemgroup on (itemgroup.pk_itemgroup = groupmember.pk_itemgroup "
	 * + " and itemgroup.dr = 0 and itemgroup.isdaysalarygroup = 'Y') " +
	 * " WHERE daysalary.pk_hrorg = '" + pk_org + "'\n" +
	 * "	AND daysalary.salarydate < '" + calculdate.toStdString() + "'\n" +
	 * "	AND (wadoc.pk_psndoc_sub IS NULL or itemgroup.ts <> daysalary.groupitemts or daysalary.groupitemts is null) "
	 * ; HashMap<String, HashMap<String, HashMap<String, List<String>>>>
	 * checkresultHashMap = executeQuery2(checkSql); if
	 * (checkresultHashMap.size() < 1) { return; } for (Map.Entry<String,
	 * HashMap<String, HashMap<String, List<String>>>> e :
	 * checkresultHashMap.entrySet()) { String key = e.getKey(); UFLiteralDate
	 * reCalculdate = new UFLiteralDate(key); HashMap<String, HashMap<String,
	 * List<String>>> reWaClassMap = e.getValue(); for (Map.Entry<String,
	 * HashMap<String, List<String>>> e2 : reWaClassMap.entrySet()) { String
	 * pk_wa_class = e2.getKey(); HashMap<String, List<String>>
	 * reRsndocAndWaitems = e2.getValue(); for (Map.Entry<String, List<String>>
	 * e3 : reRsndocAndWaitems.entrySet()) { String pk_psndoc = e3.getKey();
	 * String[] waitems = e3.getValue().toArray(new String[0]);
	 * calculSalaryByWaItem(pk_org, pk_wa_class, reCalculdate, pk_psndoc,
	 * waitems); } } } }
	 */

	/*	*//**
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
	 * dataVO.getDatetype().intValue())) {
	 * 
	 * return true; } } return false; }
	 */

	/**
	 * 
	 * 获取需要计算日薪的人员：根据考勤档案进行查询
	 * 
	 * @param pk_hrorg
	 * @param calculDate
	 * @return
	 * @throws BusinessException
	 *             <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	 */
	/*
	 * public String[] getPkPsndocs(String pk_hrorg, String calculDate) throws
	 * BusinessException { String condition = "pk_org ='" + pk_hrorg +
	 * "' and begindate<='" + calculDate +
	 * "' and isnull(enddate, '9999-12-31')>='" + calculDate +
	 * "' and isnull(dr,0)=0"; ITBMPsndocQueryMaintain service =
	 * NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class);
	 * TBMPsndocVO[] tbmPsndocVOs = service.queryByCondition(condition);
	 * String[] psndocpks = SQLHelper.getStrArray(tbmPsndocVOs,
	 * TBMPsndocVO.PK_PSNDOC); return psndocpks; }
	 */

	// <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	/**
	 * 根据组织，查询出需要计算日薪的薪资方案
	 * 
	 * @param pk_hrorg
	 * @return
	 * @throws BusinessException
	 *             <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	 */
	/*
	 * public String[] getWaClassByOrg(String pk_hrorg) throws BusinessException
	 * { String qrySql = "SELECT\n" + "	pk_wa_class\n" + "FROM\n" +
	 * "	wa_waclass\n" + "WHERE\n" + "	pk_org = '" + pk_hrorg + "'\n" +
	 * "AND stopflag = 'N'\n" + "AND isdaysalary = 'Y'\n" +
	 * "AND isnull(dr, 0) = 0";
	 * 
	 * @SuppressWarnings("unchecked") List<String> waClassList = (List<String>)
	 * getDao().executeQuery(qrySql, new ColumnListProcessor()); if (waClassList
	 * == null || waClassList.size() < 1) { throw new BusinessException("組織：" +
	 * pk_hrorg + ",未設置需要固定週期計算日薪的薪資方案"); } return waClassList.toArray(new
	 * String[0]); }
	 */

	/*
	 * public Map<String, UFLiteralDate> getPeriodDate(String pk_hrorg, String
	 * pk_wa_class, UFLiteralDate calculdate) throws BusinessException { String
	 * qrySql = "SELECT\n" + "	period.cstartdate,\n" + "	period.cenddate\n" +
	 * "FROM\n" + "	wa_waclass waclass\n" +
	 * "LEFT JOIN wa_period period ON period.pk_periodscheme = waclass.pk_periodscheme\n"
	 * + "WHERE\n" + "	waclass.pk_wa_class = '" + pk_wa_class + "'\n" +
	 * "AND period.cstartdate <= '" + calculdate.toStdString() + "'\n" +
	 * "AND period.cenddate >= '" + calculdate.toStdString() + "'";
	 * 
	 * @SuppressWarnings("unchecked") List<HashMap<String, Object>> listMaptemp
	 * = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(qrySql, new
	 * MapListProcessor()); if (listMaptemp != null && listMaptemp.size() > 0) {
	 * HashMap<String, Object> hashMap = listMaptemp.get(0); String begindate =
	 * hashMap.get("cstartdate").toString(); String enddate =
	 * hashMap.get("cenddate").toString(); Map<String, UFLiteralDate> map = new
	 * HashMap<String, UFLiteralDate>(); map.put("begindate", new
	 * UFLiteralDate(begindate)); map.put("enddate", new
	 * UFLiteralDate(enddate)); return map; } else { StringBuffer message = new
	 * StringBuffer(); message.append("組織：" + pk_hrorg + "\n");
	 * message.append("薪资方案：" + pk_wa_class + "\n"); message.append("計算日期：" +
	 * calculdate.toStdString() + "\n"); message.append("為維護薪资期间"); throw new
	 * BusinessException(message.toString()); }
	 * 
	 * }
	 */

	/**
	 * 查询参数值
	 * 
	 * @param pk_org
	 * @param initcode
	 * @return
	 * @throws DAOException
	 *             tank 注释 未使用方法
	 */
	/*
	 * public int getSysintValue(String pk_org, String
	 * initcode,Map<String,Integer> paramCacheMap) throws DAOException { String
	 * key = pk_org+"::"+initcode; if(paramCacheMap.containsKey(key)){ return
	 * paramCacheMap.get(key); } String qrySql =
	 * "select value from pub_sysinit where initcode='" + initcode +
	 * "' and pk_org ='" + pk_org + "' and isnull(dr,0)=0"; Object object =
	 * getDao().executeQuery(qrySql, new ColumnProcessor()); int sysValue = 0;
	 * try { sysValue = Integer.valueOf(object.toString()); } catch (Exception
	 * e) {
	 * 
	 * nc.bs.logging.Logger.error("sql:" + qrySql + "\n result:" + object +
	 * "\n initcode:" + initcode + "\n 考勤时薪天数或者日薪天数错误！"); }
	 * paramCacheMap.put(key, sysValue); return sysValue; }
	 */

	/**
	 * 取日薪计算天数取值
	 * 
	 * @param pk_hrorg
	 *            组织
	 * @param pk_wa_class
	 *            薪资方案
	 * @param calculDate
	 *            计算日期
	 * @param daysalarynumtype
	 *            控制参数
	 * @param periodCacheMap
	 *            期间缓存 key:pk_hrorg::pk_wa_class::calculDate.toStdString
	 *            value:Map<String, UFLiteralDate> 期间的开始和结束日期
	 * @return
	 * @throws BusinessException
	 */
	/*
	 * public double getDaySalaryNum(String pk_hrorg, String pk_wa_class,
	 * UFLiteralDate calculDate, int daysalarynumtype, Map<String,Map<String,
	 * UFLiteralDate>> periodCacheMap) throws BusinessException { //
	 * 薪资期间计薪日天数（节日+平日） if (daysalarynumtype == DaySalaryEnum.MIX_DAYNUMTYPE4) {
	 * String key = pk_hrorg+"::"+pk_wa_class+"::"+calculDate.toStdString();
	 * Map<String, UFLiteralDate> periodMap = periodCacheMap.get(key);
	 * //第一次没命中,尝试初始化 if(periodMap==null){ periodCacheMap =
	 * initPeriodCache(pk_hrorg,pk_wa_class,calculDate,periodCacheMap);
	 * periodMap = periodCacheMap.get(key); } //第二次没命中,单独进行查询
	 * if(periodMap==null){ periodMap = getPeriodDate(pk_hrorg, pk_wa_class,
	 * calculDate); } UFDouble temp =
	 * getCalendarPubService().getWorkCalndPsnWageDays(pk_hrorg,
	 * periodMap.get("begindate"), periodMap.get("enddate")); return
	 * temp.toDouble(); } // 薪资期间天数 if (daysalarynumtype ==
	 * DaySalaryEnum.MIX_DAYNUMTYPE3) { String key =
	 * pk_hrorg+"::"+pk_wa_class+"::"+calculDate.toStdString(); Map<String,
	 * UFLiteralDate> periodMap = periodCacheMap.get(key); //第一次没命中,尝试初始化
	 * if(periodMap==null){ periodCacheMap =
	 * initPeriodCache(pk_hrorg,pk_wa_class,calculDate,periodCacheMap);
	 * periodMap = periodCacheMap.get(key); } //第二次没命中,单独进行查询
	 * if(periodMap==null){ periodMap = getPeriodDate(pk_hrorg, pk_wa_class,
	 * calculDate); } return
	 * UFLiteralDate.getDaysBetween(periodMap.get("begindate"),
	 * periodMap.get("enddate")) + 1; } // 固定30天 if (daysalarynumtype ==
	 * DaySalaryEnum.MIX_DAYNUMTYPE1) { return DaySalaryEnum.DAYSAYSALARYNUM03;
	 * } // 固定21.75天 if (daysalarynumtype == DaySalaryEnum.MIX_DAYNUMTYPE2) {
	 * return DaySalaryEnum.TBMSALARYNUM02; } //考勤计薪天数 if (daysalarynumtype ==
	 * DaySalaryEnum.MIX_DAYNUMTYPE5) { String sqlsys = "SELECT\n" +
	 * "	begindate,\n" + "	enddate\n" + "FROM\n" + "	tbm_period\n" + "WHERE\n" +
	 * "	begindate <= '" + calculDate + "'\n" + "AND enddate >= '" + calculDate
	 * + "'\n" + "AND pk_org = '" + pk_hrorg + "'\n" + "AND isnull(dr, 0) = 0";
	 * 
	 * @SuppressWarnings("unchecked") List<HashMap<String, Object>> listMaptemp
	 * = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(
	 * sqlsys.toString(), new MapListProcessor()); String begindate = null;
	 * String enddate = null; if (listMaptemp != null && listMaptemp.size() > 0)
	 * { HashMap<String, Object> hashMap = listMaptemp.get(0); begindate =
	 * hashMap.get("begindate").toString(); enddate =
	 * hashMap.get("enddate").toString(); } else { StringBuffer message = new
	 * StringBuffer(); message.append("組織：" + pk_hrorg + "\n");
	 * message.append("計算日期：" + calculDate.toStdString() + "\n");
	 * message.append("為維護考勤期間"); throw new
	 * BusinessException(message.toString()); } return
	 * UFLiteralDate.getDaysBetween(new UFLiteralDate(begindate), new
	 * UFLiteralDate(enddate)) + 1; } return DaySalaryEnum.DAYSAYSALARYNUM03; }
	 */

	/*
	 * private Map<String, Map<String, UFLiteralDate>> initPeriodCache( String
	 * pk_hrorg, String pk_wa_class, UFLiteralDate calculdate, Map<String,
	 * Map<String, UFLiteralDate>> periodCacheMap) throws DAOException { String
	 * qrySql = "SELECT\n" + "	period.cstartdate,\n" + "	period.cenddate\n" +
	 * "FROM\n" + "	wa_waclass waclass\n" +
	 * "LEFT JOIN wa_period period ON period.pk_periodscheme = waclass.pk_periodscheme\n"
	 * + "WHERE\n" + "	waclass.pk_wa_class = '" + pk_wa_class + "'\n" +
	 * "AND period.cstartdate <= '" + calculdate.toStdString() + "'\n" +
	 * "AND period.cenddate >= '" + calculdate.toStdString() + "'";
	 * 
	 * @SuppressWarnings("unchecked") List<HashMap<String, Object>> listMaptemp
	 * = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(qrySql, new
	 * MapListProcessor()); if (listMaptemp != null && listMaptemp.size() > 0) {
	 * 
	 * HashMap<String, Object> hashMap = listMaptemp.get(0); String begindate =
	 * hashMap.get("cstartdate").toString(); String enddate =
	 * hashMap.get("cenddate").toString(); UFLiteralDate begindateUlt = new
	 * UFLiteralDate(begindate); UFLiteralDate enddateUlt = new
	 * UFLiteralDate(enddate);
	 * 
	 * Map<String, UFLiteralDate> map = new HashMap<String, UFLiteralDate>();
	 * map.put("begindate", begindateUlt); map.put("enddate", enddateUlt);
	 * 
	 * UFLiteralDate[] allDates = CommonUtils.createDateArray(begindateUlt,
	 * enddateUlt); for(UFLiteralDate date:allDates){
	 * periodCacheMap.put(pk_hrorg+"::"+pk_wa_class+"::"+date.toStdString(),
	 * map); }
	 * 
	 * } return periodCacheMap; }
	 */
	private BaseDAO dao;

	private BaseDAO getDao() {
		if (null == dao) {
			dao = new BaseDAO();
		}
		return dao;
	}

	/*
	 * private IWorkCalendarPubService calendarPubService;
	 * 
	 * private IWorkCalendarPubService getCalendarPubService() { if (null ==
	 * calendarPubService) { calendarPubService =
	 * NCLocator.getInstance().lookup(IWorkCalendarPubService.class); } return
	 * calendarPubService; }
	 */

	/*
	 * <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	 * 
	 * @SuppressWarnings({ "unchecked" }) private HashMap<Object,
	 * List<GeneralVO>> executeQuery(String qrysql) throws DAOException {
	 * 
	 * HashMap<Object, List<GeneralVO>> param1ParamsMap = (HashMap<Object,
	 * List<GeneralVO>>) getDao().executeQuery( qrysql, new ResultSetProcessor()
	 * {
	 *//**
					 * 
					 */
	/*
	 * private static final long serialVersionUID = -6223698366816831149L;
	 * 
	 * @Override public Object handleResultSet(ResultSet rs) throws SQLException
	 * { HashMap<Object, List<GeneralVO>> retMap = new HashMap<Object,
	 * List<GeneralVO>>();
	 * 
	 * while (rs.next()) {
	 * 
	 * List<GeneralVO> listGeneralVOs = retMap.get(rs.getObject(1));
	 * 
	 * if (listGeneralVOs == null) {
	 * 
	 * listGeneralVOs = new ArrayList<GeneralVO>();
	 * 
	 * listGeneralVOs.add(processorToGeneralVO(rs)); retMap.put(rs.getObject(1),
	 * listGeneralVOs); } else {
	 * 
	 * listGeneralVOs.add(processorToGeneralVO(rs)); }
	 * 
	 * }
	 * 
	 * return retMap; } });
	 * 
	 * return param1ParamsMap; }
	 */

	/**
	 * HashMap<String,HashMap<String, List<String>>>
	 * HashMap<计算时间,HashMap<pk_wa_class,HashMap<pk_psndoc, List<pk_wa_item>>>>
	 * 
	 * @param qrysql
	 * @return
	 * @throws DAOException
	 */
	/*
	 * <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a>
	 * 
	 * @SuppressWarnings("unchecked") private HashMap<String, HashMap<String,
	 * HashMap<String, List<String>>>> executeQuery2(String qrysql) throws
	 * DAOException {
	 * 
	 * HashMap<String, HashMap<String, HashMap<String, List<String>>>>
	 * param1ParamsMap = (HashMap<String, HashMap<String, HashMap<String,
	 * List<String>>>>) getDao() .executeQuery(qrysql, new ResultSetProcessor()
	 * {
	 *//**
			 * 
			 */
	/*
	 * private static final long serialVersionUID = -4700240988126059729L;
	 * 
	 * @Override public Object handleResultSet(ResultSet rs) throws SQLException
	 * { HashMap<String, HashMap<String, HashMap<String, List<String>>>> retMap
	 * = new HashMap<String, HashMap<String, HashMap<String, List<String>>>>();
	 * while (rs.next()) { HashMap<String, HashMap<String, List<String>>>
	 * param2VoMap = retMap.get(rs.getObject(1)); if (param2VoMap == null) {
	 * param2VoMap = new HashMap<String, HashMap<String, List<String>>>();
	 * HashMap<String, List<String>> param3VoMap = new HashMap<String,
	 * List<String>>(); if (null == rs.getObject(2)) { // ares.tank
	 * 考勤日薪是没有新增方案的,跳过即可 continue; } param2VoMap.put(rs.getObject(2).toString(),
	 * param3VoMap); List<String> list = new ArrayList<>();
	 * list.add(rs.getObject(4).toString());
	 * param3VoMap.put(rs.getObject(3).toString(), list);
	 * retMap.put(rs.getObject(1).toString(), param2VoMap); } else {
	 * HashMap<String, List<String>> param3VoMap =
	 * param2VoMap.get(rs.getObject(2)); if (param3VoMap == null) { param3VoMap
	 * = new HashMap<String, List<String>>(); List<String> list = new
	 * ArrayList<>(); list.add(rs.getObject(4).toString());
	 * param3VoMap.put(rs.getObject(3).toString(), list); } else { List<String>
	 * list = param3VoMap.get(rs.getObject(3).toString()); if (list == null) {
	 * list = new ArrayList<>(); list.add(rs.getObject(4).toString());
	 * param3VoMap.put(rs.getObject(3).toString(), list); } else {
	 * list.add(rs.getObject(4).toString());
	 * param3VoMap.put(rs.getObject(3).toString(), list); } } }
	 * 
	 * } return retMap; } });
	 * 
	 * return param1ParamsMap; }
	 */

	/*
	 * <a> 后台任务方法暂不使用 tank 2019年10月15日21:35:19 </a> protected GeneralVO
	 * processorToGeneralVO(ResultSet rs) throws SQLException {
	 * 
	 * ResultSetMetaData meta = rs.getMetaData(); int cols =
	 * meta.getColumnCount(); GeneralVO result = new GeneralVO();
	 * 
	 * for (int i = 1; i <= cols; i++) {
	 * 
	 * String strRealName = StringUtils.isNotEmpty(meta.getColumnLabel(i)) ?
	 * meta.getColumnLabel(i) : meta .getColumnName(i);
	 * result.setAttributeValue(strRealName.toLowerCase(), rs.getObject(i)); }
	 * 
	 * return result; }
	 */

	/*
	 * @Override public void deleteDaySalary(String pk_hrorg, UFLiteralDate
	 * calculdate, int continueTime) throws BusinessException { UFLiteralDate
	 * continuedate = calculdate.getDateBefore(continueTime); String deleteSql =
	 * "delete from wa_daysalary where pk_hrorg='" + pk_hrorg +
	 * "' and salarydate<'" + continuedate.toStdString() + "'";
	 * getDao().executeUpdate(deleteSql); }
	 */

	/*
	 * @Override public void checkDaySalaryAndCalculSalary(String pk_hrorg,
	 * UFLiteralDate calculdate, int checkrange) throws BusinessException { for
	 * (int i = 1; i <= checkrange; i++) { UFLiteralDate checkDate =
	 * calculdate.getDateBefore(i); // 查询参数设置-日薪计算天数取值方式 //初始化参数缓存 tank
	 * 2019年10月14日14:57:44 Map<String,Integer> paramCacheMap = new HashMap<>();
	 * int daynumtype = getSysintValue(pk_hrorg,
	 * DaySalaryEnum.DAYSYSINT,paramCacheMap); String checkSql =
	 * "select count(*) from wa_daysalary where pk_hrorg='" + pk_hrorg +
	 * "' and salarydate='" + checkDate.toStdString() + "' and isnull(dr,0)=0 ";
	 * int count = (int) getDao().executeQuery(checkSql, new ColumnProcessor());
	 * if (count == 0) { calculSalaryByHrorg(pk_hrorg, checkDate); } } }
	 */

	/*
	 * @Override public void checkDaySalaryAndCalculSalary(String pk_wa_class,
	 * String[] pk_psndocs, UFLiteralDate begindate, UFLiteralDate enddate,
	 * String pk_wa_item, String pk_group_item) throws BusinessException { //
	 * 薪资方案 WaClassVO waclassvo = (WaClassVO)
	 * getDao().retrieveByPK(WaClassVO.class, pk_wa_class); new
	 * DaySalaryCommonCalculator
	 * ().doCalculDaySalaryWithOneWaItem(waclassvo.getPk_org(), pk_psndocs,
	 * begindate, enddate, pk_wa_item, pk_group_item, pk_wa_class); }
	 */

	@SuppressWarnings("unchecked")
	@Override
	public void calculDaySalaryWithWaClass(String pk_wa_class, String[] pk_psndocs, String cyear, String cperiod)
			throws BusinessException {
		String userPK = InvocationInfoProxy.getInstance().getUserId();
		// 查询薪资方案对应的薪资期间的开始日期与结束日期
		String qrySql = //
		"           SELECT "//
				+ "    CASE WHEN period.cstartdate < timeperiod.begindate THEN period.cstartdate ELSE timeperiod.begindate END cstartdate, "//
				+ "    CASE WHEN period.cenddate > timeperiod.enddate THEN period.cenddate ELSE timeperiod.enddate END cenddate "//
				+ "FROM "//
				+ "    wa_waclass waclass "//
				+ "LEFT JOIN "//
				+ "    wa_period period "//
				+ "ON "//
				+ "    period.pk_periodscheme = waclass.pk_periodscheme "//
				+ "INNER JOIN "//
				+ "    tbm_period timeperiod "//
				+ "ON "//
				+ "    period.caccyear=timeperiod.accyear "//
				+ "AND period.caccperiod=timeperiod.accmonth "//
				+ "WHERE "//
				+ "    waclass.pk_wa_class = '" + pk_wa_class + "' "//
				+ "AND period.caccyear = '" + cyear + "' "//
				+ "AND period.caccperiod = '" + cperiod + "'; ";//

		List<HashMap<String, Object>> listMaptemp = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(qrySql,
				new MapListProcessor());
		if (listMaptemp.size() < 1) {
			return;
		}
		HashMap<String, Object> hashMap = listMaptemp.get(0);
		String begindate = hashMap.get("cstartdate").toString();
		String enddate = hashMap.get("cenddate").toString();
		WaClassVO waclassvo = (WaClassVO) getDao().retrieveByPK(WaClassVO.class, pk_wa_class);
		// 获取和该薪资方案有关的所有薪资项目分组和分组下的公共薪资项目
		Map<String, Set<String>> needCaculatorItemGoup = getAllGroupItemFromWaClass(pk_wa_class, cyear, cperiod);
		DaySalaryCommonCalculator calculator = new DaySalaryCommonCalculator();
		Set<String> keySet = needCaculatorItemGoup.keySet();
		if (keySet != null && keySet.size() > 0) {
			for (String pk_itemgroup : keySet) {
				Set<String> itemSet = needCaculatorItemGoup.get(pk_itemgroup);
				if (itemSet != null) {
					for (String pk_waitem : itemSet) {
						calculator.doCalculDaySalaryWithOneWaItem(userPK, waclassvo.getPk_org(), pk_psndocs,
								(new UFLiteralDate(begindate)), new UFLiteralDate(enddate), pk_waitem, pk_itemgroup,
								pk_wa_class);
					}
				}
			}
		}
	}

	@Override
	public Map<String, UFDouble> statisticLeavecharge(String[] pk_psndocs, String cyear, String cperiod)
			throws BusinessException {
		Map<String, UFDouble> leavechargeMap = new HashMap<String, UFDouble>();
		String insql = new InSQLCreator().getInSQL(pk_psndocs, true);
		String qrySql = "SELECT\n" + "	pk_psndoc,\n" + "	SUM (leavecharge) as totalleavecharge\n" + "FROM\n"
				+ "	wa_daysalary\n" + "WHERE\n" + "	pk_psndoc IN (\n" + insql + "	)\n" + "AND cyear = '" + cyear
				+ "'\n" + "AND cperiod = '" + cperiod + "'\n" + "GROUP BY\n" + "	pk_psndoc";
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> listMaps = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(
				qrySql.toString(), new MapListProcessor());
		for (int i = 0; i < listMaps.size(); i++) {
			HashMap<String, Object> hashMap = listMaps.get(i);
			String pk_psndoc = hashMap.get("pk_psndoc").toString();
			UFDouble totalleavecharge = hashMap.get("totalleavecharge") != null ? new UFDouble(hashMap.get(
					"totalleavecharge").toString()) : UFDouble.ZERO_DBL;
			leavechargeMap.put(pk_psndoc, totalleavecharge);
		}
		return leavechargeMap;
	}

	/**
	 * 获取该薪资方案中,所有相关的薪资项目分组和公共薪资项目
	 * 
	 * @return <公共薪资项目,薪资项目分组pk>
	 * @throws DAOException
	 */
	private Map<String, Set<String>> getAllGroupItemFromWaClass(String pk_wa_class, String cyear, String cperiod)
			throws DAOException {
		// 查出所有formula
		String sql = "select vformula from wa_classitem where pk_wa_class = '" + pk_wa_class + "' and " + " cyear = '"
				+ cyear + "' and cperiod = '" + cperiod
				+ "' and dr = 0 and vformula is not null and len(vformula) > 0 ";
		@SuppressWarnings("unchecked")
		Set<String> vformulaSet = (Set<String>) getDao().executeQuery(sql, new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;

			private Set<String> vformulaSet = new HashSet<>();

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while (rs.next()) {
					if (rs.getString(1) != null) {
						vformulaSet.add(rs.getString(1));
					}
				}
				return vformulaSet;
			}
		});
		// 所有有效的itemGroup
		sql = " select mb.pk_waitem,gp.pk_itemgroup from wa_itemgroup gp "
				+ " inner join wa_itemgroupmember mb on (mb.dr = 0 and mb.pk_itemgroup = gp.pk_itemgroup) "
				+ " where gp.dr = 0 and gp.isenabled = 'Y' and gp.pk_org = 'GLOBLE00000000000000'";
		@SuppressWarnings("unchecked")
		Map<String, Set<String>> itemGroup2ItemMap = (Map<String, Set<String>>) getDao().executeQuery(sql,
				new ResultSetProcessor() {
					private static final long serialVersionUID = 1L;

					private Map<String, Set<String>> itemGroup2ItemMap = new HashMap<>();

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						while (rs.next()) {
							if (rs.getString(1) != null && rs.getString(2) != null) {
								Set<String> menberSet = itemGroup2ItemMap.get(rs.getString(2));
								if (menberSet == null) {
									menberSet = new HashSet<>();
								}
								menberSet.add(rs.getString(1));
								itemGroup2ItemMap.put(rs.getString(2), menberSet);
							}
						}
						return itemGroup2ItemMap;
					}
				});
		// 是否包含
		Map<String, Set<String>> rsMap = new HashMap<>();
		Set<String> itemGroupSet = itemGroup2ItemMap.keySet();
		for (String formular : vformulaSet) {
			if (formular == null || formular.length() <= 0) {
				continue;
			}
			for (String itemGroup : itemGroupSet) {
				if (itemGroup != null && itemGroup.length() > 0) {
					if (formular.contains(itemGroup)) {
						rsMap.put(itemGroup, itemGroup2ItemMap.get(itemGroup));
					}
				}
			}
		}
		return rsMap;
	}

}
