package nc.impl.wa.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.itf.hrwa.IWadaysalaryQueryService;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.hrwa.wadaysalary.DaySalaryEnum;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.pub.WaLoginContext;

/**
 * #21266 按日合计考勤扣款项函数解析器
 * 
 * @author yejk
 * @date 2018-9-11
 */
@SuppressWarnings({ "serial", "restriction" })
public class LeaveFeeParse extends AbstractPreExcutorFormulaParse {

    /**
     * @Description: 执行解析
     * @author yejk
     * @date 2018-9-11
     * @param formula
     * @param waLoginContext
     * @throws BusinessException
     * @return
     */
    @Override
    public void excute(Object formula, WaLoginContext waLoginContext) throws BusinessException {
	BaseDAO basedao = new BaseDAO();
	// 薪资方案主键
	String pk_wa_class = waLoginContext.getWaLoginVO().getPk_wa_class();
	// 组织
	String pk_org = waLoginContext.getPk_org();
	// 薪资期间
	// UFLiteralDate startDate =
	// waLoginContext.getWaLoginVO().getPeriodVO().getCstartdate();
	// UFLiteralDate endDate =
	// waLoginContext.getWaLoginVO().getPeriodVO().getCenddate();
	// 薪资期间年份
	String caccyear = waLoginContext.getWaLoginVO().getPeriodVO().getCaccyear();
	// 薪资期间月份
	String cperiod = waLoginContext.getWaLoginVO().getPeriodVO().getCperiod();

	/* 通过薪资期间获取考勤期间的起止日期 start */
	String queryDateSql = "select tbm_period.begindate,tbm_period.enddate from tbm_period where tbm_period.accyear = ?  and tbm_period.accmonth  = ? and tbm_period.pk_org = ?";
	SQLParameter params = new SQLParameter();
	params.addParam(caccyear);
	params.addParam(cperiod);
	params.addParam(pk_org);
	@SuppressWarnings("unchecked")
	List<Map<String, Object>> dateListMap = (List<Map<String, Object>>) basedao.executeQuery(queryDateSql, params,
		new MapListProcessor());
	if (null == dateListMap) {
	    throw new BusinessException("通过薪资期间获取考勤期间起止日期为空");
	}
	UFLiteralDate startDate = new UFLiteralDate(dateListMap.get(0).get("begindate").toString());
	UFLiteralDate endDate = new UFLiteralDate(dateListMap.get(0).get("enddate").toString());
	/* 通过薪资期间获取考勤期间的起止日期 end */

	// String[] formulaArg = formula.toString().split(",");
	String[] arguments = getArguments(formula.toString());
	// 考勤月报项目pk formulaArg[0].substring(10).trim()
	// 去掉第一个字母A
	String pk_timeitem = arguments[0].substring(1);
	// flag 是否免税 0否 1是 formulaArg[1].trim().substring(0,1)
	int flag = Integer.valueOf(arguments[1]);

	/* 获取计算人员集合 start */
	String psndocsSql = "select wa_cacu_data.pk_psndoc from wa_cacu_data where wa_cacu_data.pk_wa_class = '"
		+ pk_wa_class + "'";

	List<String> psndocList = new ArrayList<String>();
	@SuppressWarnings("unchecked")
	List<Map<String, Object>> result = (List<Map<String, Object>>) basedao.executeQuery(psndocsSql,
		new MapListProcessor());
	if (null == result) {
	    throw new BusinessException("按日合计考勤扣款项计算-获取人员pk为空");
	}
	for (int i = 0; i < result.size(); i++) {
	    Map<String, Object> map = result.get(i);
	    String pk_psndoc1 = map.get("pk_psndoc").toString();
	    psndocList.add(pk_psndoc1);
	}
	String[] psndocArr = psndocList.toArray(new String[0]);
	/* 获取计算人员集合 end */

	// 调用接口获取应税(免税)日薪
	IWadaysalaryQueryService wadaysalaryQueryService = NCLocator.getInstance().lookup(
		IWadaysalaryQueryService.class);
	Map<String, HashMap<UFLiteralDate, UFDouble>> daySalaryResult = null;

	if (1 == flag) {// 1 是 免税日薪
	    daySalaryResult = wadaysalaryQueryService.getTotalTbmDaySalaryMap(psndocArr, startDate, endDate,
		    DaySalaryEnum.TAXFREEDAYSALARY);
	} else {// 否则 0 是应税日薪
	    daySalaryResult = wadaysalaryQueryService.getTotalTbmDaySalaryMap(psndocArr, startDate, endDate,
		    DaySalaryEnum.TAXABLEDAYSALARY);
	}

	if (null == daySalaryResult) {
	    throw new BusinessException("调用接口IWadaysalaryQueryService获取应税(免税)日薪为空");
	}

	/* 获取單據時數 start */
	// 获取休假(请假)时长 假设每人每天每个休假类别 一条数据 此处后期完善 mark
	InSQLCreator isc = new InSQLCreator();
	String inSql = isc.getInSQL(psndocArr);
	String queryHourSql = "select tbm_leavereg.pk_psndoc as pcsndoc,tbm_leavereg.leavebegindate as date,sum(tbm_leavereg.leavehour) as hour from tbm_leavereg,tbm_period where tbm_period.accyear='"
		+ caccyear
		+ "' and tbm_period.accmonth='"
		+ cperiod
		+ "' and tbm_period.pk_org='"
		+ pk_org
		+ "' "
		+ "and tbm_leavereg.pk_psndoc in ("
		+ inSql
		+ ") and tbm_leavereg.pk_leavetype ='"
		+ pk_timeitem
		+ "' and ("
		+ "(tbm_leavereg.leavebegindate<=tbm_period.begindate and tbm_leavereg.leaveenddate>=tbm_period.begindate) or "
		+ "(tbm_leavereg.leaveenddate>=tbm_period.enddate and tbm_leavereg.leavebegindate<=tbm_period.enddate) or "
		+ "(tbm_leavereg.leavebegindate>=tbm_period.begindate  and tbm_leavereg.leaveenddate<tbm_period.enddate ) or (tbm_leavereg.leaveenddate<tbm_period.begindate and tbm_leavereg.approve_time >=tbm_period.begindate and tbm_leavereg.approve_time <=tbm_period.enddate )) group by tbm_leavereg.pk_psndoc,tbm_leavereg.leavebegindate,tbm_leavereg.pk_leavetype";

	@SuppressWarnings("unchecked")
	List<Map<String, Object>> psnDateHourResult = (List<Map<String, Object>>) basedao.executeQuery(queryHourSql,
		new MapListProcessor());
	if (null == psnDateHourResult || 0 == psnDateHourResult.size()) {
	    // throw new BusinessException("获取休假(请假)时长为空");
	    // 造测试数据
	    psnDateHourResult = new ArrayList<Map<String, Object>>();
	    Map<String, Object> mapData = new HashMap<String, Object>();
	    mapData.put("pcsndoc", "0001A110000000000SOI");
	    mapData.put("date", "2018-06-01");
	    mapData.put("hour", 10.0);
	    psnDateHourResult.add(mapData);

	    mapData = new HashMap<String, Object>();
	    mapData.put("pcsndoc", "0001A110000000000SS4");
	    mapData.put("date", "2018-06-02");
	    mapData.put("hour", 11.0);
	    psnDateHourResult.add(mapData);

	    mapData = new HashMap<String, Object>();
	    mapData.put("pcsndoc", "0001A110000000000SSA");
	    mapData.put("date", "2018-06-03");
	    mapData.put("hour", 12.0);
	    psnDateHourResult.add(mapData);
	}
	// psnDateHourMap 主键为人员pk+日期，值为时长
	Map<String, UFDouble> psnDateHourMap = transListToMap(psnDateHourResult);
	/* 获取單據時數 end */

	// 批量更新
	PersistenceManager sessionManager = null;
	try {
	    sessionManager = PersistenceManager.getInstance();
	    JdbcSession session = sessionManager.getJdbcSession();
	    // 计算每一个人 当前计算期间内 的 扣款总额
	    for (int i = 0; i < psndocArr.length; i++) {
		String psndoc = psndocArr[i];
		// Map<String, HashMap<UFLiteralDate, UFDouble>> daySalaryResult
		// 当前人当前计算期间每一天日薪Map
		double sum = 0.00;
		HashMap<UFLiteralDate, UFDouble> curPsnPeriodDateDailyFee = daySalaryResult.get(psndoc);
		//MOD 增加判空处理 James
		if(null !=curPsnPeriodDateDailyFee &&curPsnPeriodDateDailyFee.size()>0){
			for (UFLiteralDate dateKey : curPsnPeriodDateDailyFee.keySet()) {
			    // 取时薪 即日薪的 1/8
			    double hourSalary = curPsnPeriodDateDailyFee.get(dateKey).getDouble() / 8;
			    double hour = psnDateHourMap.get(psndoc + dateKey) == null ? 0.00 : psnDateHourMap.get(
				    psndoc + dateKey).getDouble();
			    sum += (hour * hourSalary);
			}
		}
		String updateSql = "update wa_cacu_data set cacu_value = ? where pk_wa_class = ? and pk_psndoc = ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(sum);
		parameter.addParam(pk_wa_class);
		parameter.addParam(psndoc);
		session.addBatch(updateSql, parameter);
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

    /**
     * 
     * @Description: 将获取的时长List<Map> 转换成 key为 人员pk+日期的MAP，便于接下来的计算
     * @author yejk
     * @date 2018-9-12
     * @param psnDateHourResult
     * @return Map<String,UFDouble>
     */
    public Map<String, UFDouble> transListToMap(List<Map<String, Object>> psnDateHourResult) {
	Map<String, UFDouble> mapData = new HashMap<String, UFDouble>();
	for (int i = 0; i < psnDateHourResult.size(); i++) {
	    Map<String, Object> map = psnDateHourResult.get(i);
	    mapData.put(map.get("pcsndoc").toString() + map.get("date").toString(), new UFDouble(map.get("hour")
		    .toString()));
	}
	return mapData;
    }
}
