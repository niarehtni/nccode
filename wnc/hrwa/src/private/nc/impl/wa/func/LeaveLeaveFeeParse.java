package nc.impl.wa.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.impl.wa.paydata.PaydataDAO;
import nc.itf.hrwa.IWadaysalaryQueryService;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 按日合计离职考勤扣款项函数解析器
 * 
 * @author ssx
 * @date 2019-4-7
 */
@SuppressWarnings({ "serial" })
public class LeaveLeaveFeeParse extends AbstractPreExcutorFormulaParse {
	int INTCOMP = -1; // -1: 金額, -2: 時數

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

		// MOD (按日合计离职考勤扣款)
		// add by ssx on 2019-04-07
		startDate = endDate.getDateAfter(1);
		endDate = startDate.getDateAfter(startDate.getDaysMonth() - startDate.getDay()); // 月末最後一天
		// MOD end

		// String[] formulaArg = formula.toString().split(",");
		String[] arguments = getArguments(formula.toString());
		// 考勤月报项目pk formulaArg[0].substring(10).trim()
		// 去掉第一个字母A
		String pk_timeitem = String.valueOf(arguments[0].substring(1)).replaceAll("\'", "");
		// flag 是否免税 0否 1是 formulaArg[1].trim().substring(0,1)
		int flag = Integer.valueOf(String.valueOf(arguments[1]).replaceAll("\'", ""));
		// 分组参数
		String pk_item_group = String.valueOf(arguments[3]).replaceAll("\'", "");
		INTCOMP = arguments[2].equals("'1'") ? -2 : -1;
		/* 获取计算人员集合 start */
		String psndocsSql = PaydataDAO.getLeavePsndocSQL(waLoginContext.getPk_org(), waLoginContext.getPk_wa_class(),
				waLoginContext.getCyear(), waLoginContext.getCperiod(), startDate, endDate, this.getContext()
						.getPk_loginUser());

		int rows = (int) basedao.executeQuery("select count(*) from wa_cacu_leavefee where pk_wa_class='" + pk_wa_class
				+ "' and creator='" + this.getContext().getPk_loginUser() + "' and pk_item_group='" + pk_item_group
				+ "' and pk_timeitem='" + pk_timeitem + "' and intComp=" + String.valueOf(INTCOMP),
				new ColumnProcessor());
		if (rows > 0) {
			return;
		}

		List<String> psndocList = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) basedao.executeQuery(psndocsSql,
				new MapListProcessor());
		if (null == result || result.size() == 0) {
			return; // MOD by ssx, 離職未必每次都有人，不能報錯，直接返回
		}
		for (int i = 0; i < result.size(); i++) {
			Map<String, Object> map = result.get(i);
			String pk_psndoc1 = map.get("pk_psndoc").toString();
			psndocList.add(pk_psndoc1);
		}
		String[] psndocArr = psndocList.toArray(new String[0]);
		/* 获取计算人员集合 end */

		/* 获取單據時數 start */
		// 获取休假(请假)时长 假设每人每天每个休假类别 一条数据 此处后期完善 mark
		String queryHourSql = "select tbm_leavereg.pk_psndoc as pcsndoc,tbm_leavereg.effectivedate as begindate,sum(tbm_leavereg.leavehour) as leavehour from tbm_leavereg,tbm_period where tbm_leavereg.effectivedate >='"
				+ startDate
				+ "' and tbm_leavereg.effectivedate <='"
				+ endDate
				+ "' and tbm_period.pk_org='"
				+ pk_org
				+ "' "
				+ "and tbm_leavereg.pk_psndoc in ("
				+ psndocsSql
				+ ") and tbm_leavereg.pk_leavetype ='"
				+ pk_timeitem
				+ "' and ("
				+ "(tbm_leavereg.effectivedate<=tbm_period.begindate and tbm_leavereg.leaveenddate>=tbm_period.begindate) or "
				+ "(tbm_leavereg.leaveenddate>=tbm_period.enddate and tbm_leavereg.effectivedate<=tbm_period.enddate) or "
				+ "(tbm_leavereg.effectivedate>=tbm_period.begindate  and tbm_leavereg.leaveenddate<tbm_period.enddate ) or (tbm_leavereg.leaveenddate<tbm_period.begindate and tbm_leavereg.approve_time >=tbm_period.begindate and tbm_leavereg.approve_time <=tbm_period.enddate )) group by tbm_leavereg.pk_psndoc,tbm_leavereg.effectivedate,tbm_leavereg.pk_leavetype";

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> psnDateHourResult = (List<Map<String, Object>>) basedao.executeQuery(queryHourSql,
				new MapListProcessor());

		// psnDateHourMap 主键为人员pk+日期，值为时长
		Map<String, UFDouble> psnDateHourMap = transListToMap(psnDateHourResult);
		// 获取所有时间
		Set<UFLiteralDate> mixDateSet = getMIXDate(psnDateHourResult);
		/* 获取單據時數 end */

		// 调用接口获取应税(免税)日薪
		IWadaysalaryQueryService wadaysalaryQueryService = NCLocator.getInstance().lookup(
				IWadaysalaryQueryService.class);
		Map<String, Map<String, Double>> daySalaryResult = null;

		daySalaryResult = wadaysalaryQueryService.getTotalDaySalaryMapWithoutRecalculate(pk_org, psndocArr,
				mixDateSet.toArray(new UFLiteralDate[0]), 1 != flag, pk_item_group);

		if (null == daySalaryResult) {
			// Mod Tank 为空只能说明日薪为0,并不是错误.
			daySalaryResult = new HashMap<String, Map<String, Double>>();
			// throw new
			// BusinessException("调用接口IWadaysalaryQueryService获取应税(免税)日薪为空");
		}

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
				Map<String, Double> curPsnPeriodDateDailyFee = daySalaryResult.get(psndoc);
				// MOD 增加判空处理 James
				if (null != curPsnPeriodDateDailyFee && curPsnPeriodDateDailyFee.size() > 0) {
					if (arguments[2].equals("'1'")) {
						for (String psndateKey : psnDateHourMap.keySet()) {
							if (!psndoc.equals(psndateKey.split("::")[0])) {
								continue;
							}
							// 取时薪 即日薪的 1/8
							double hour = psnDateHourMap.get(psndateKey) == null ? 0.00 : psnDateHourMap
									.get(psndateKey).getDouble();
							sum += hour;
						}
					} else {
						for (String psndateKey : psnDateHourMap.keySet()) {
							if (!psndoc.equals(psndateKey.split("::")[0])) {
								continue;
							}
							// 取时薪 即日薪的 1/8
							Double hourSalary = curPsnPeriodDateDailyFee.get(psndateKey.split("::")[1]);
							double hour = psnDateHourMap.get(psndateKey) == null ? 0.00 : psnDateHourMap
									.get(psndateKey).getDouble();
							sum += (hour * (hourSalary == null ? 0.0 : hourSalary.doubleValue()));
						}
					}
				}
				String updateSql = "insert into wa_cacu_leavefee (intcomp, pk_timeitem, amount, pk_wa_class, creator, pk_psndoc, pk_item_group) values (?,?,?,?,?,?,?)";
				SQLParameter parameter = new SQLParameter();
				parameter.addParam(INTCOMP);
				parameter.addParam(pk_timeitem);
				parameter.addParam(sum);
				parameter.addParam(pk_wa_class);
				parameter.addParam(this.getContext().getPk_loginUser());
				parameter.addParam(psndoc);
				parameter.addParam(pk_item_group);
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
			mapData.put(map.get("pcsndoc").toString() + "::" + map.get("begindate").toString(),
					new UFDouble(map.get("leavehour").toString()));
		}
		return mapData;
	}

	/**
	 * 
	 * @Description: 将获取的时长List<Map> 转换成 key为 人员pk+日期的MAP，便于接下来的计算
	 * @author yejk
	 * @date 2018-9-12
	 * @param psnDateHourResult
	 * @return Map<String,UFDouble>
	 */
	public Set<UFLiteralDate> getMIXDate(List<Map<String, Object>> psnDateHourResult) {
		Set<UFLiteralDate> mapData = new HashSet<UFLiteralDate>();
		for (int i = 0; i < psnDateHourResult.size(); i++) {
			Map<String, Object> map = psnDateHourResult.get(i);
			mapData.add(new UFLiteralDate(map.get("begindate").toString()));
		}
		return mapData;
	}

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		excute(formula, getContext());
		// String[] formulaArg = formula.toString().split(",");
		String[] arguments = getArguments(formula.toString());
		// 考勤月报项目pk formulaArg[0].substring(10).trim()
		// 去掉第一个字母A
		String pk_timeitem = String.valueOf(arguments[0].substring(1)).replaceAll("\'", "");
		// 分组参数
		String pk_item_group = String.valueOf(arguments[3]).replaceAll("\'", "");
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr("coalesce(" + "(select amount "
				+ " from wa_cacu_leavefee where pk_wa_class=wa_cacu_data.pk_wa_class and creator='"
				+ this.getContext().getPk_loginUser() + "' and pk_timeitem='" + pk_timeitem + "' and pk_item_group='"
				+ pk_item_group + "' and intcomp=" + String.valueOf(INTCOMP) + " and pk_psndoc=wa_cacu_data.pk_psndoc)"
				+ ", 0)");
		return fvo;
	}
}
