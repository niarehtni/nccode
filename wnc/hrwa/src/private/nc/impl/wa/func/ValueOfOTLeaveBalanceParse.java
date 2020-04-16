package nc.impl.wa.func;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.ta.leaveextrarest.ILeaveExtraRestService;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OTLeaveBalanceVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.wa.pub.WaLoginContext;

public class ValueOfOTLeaveBalanceParse extends AbstractPreExcutorFormulaParse {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 6327598121254156470L;
	private BaseDAO baseDao;

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr(" wa_cacu_data.cacu_value ");
		excute(formula, getContext());
		return fvo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void excute(Object formula, WaLoginContext waLoginContext) throws BusinessException {
		// 先清空數據
		String sql = "update wa_cacu_data set cacu_value = 0 where  " + "pk_wa_class = '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";
		getBaseDao().executeUpdate(sql);

		// 人員列表
		sql = "select DISTINCT pk_psndoc from wa_cacu_data where pk_wa_class= '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";
		ArrayList<String> pk_psndocs = (ArrayList<String>) getBaseDao().executeQuery(sql, new ColumnListProcessor());

		// 公式參數
		String[] arguments = getArguments(formula.toString());

		// 休假類別
		Collection<LeaveTypeCopyVO> leaveTypeCopys = (Collection<LeaveTypeCopyVO>) this.getBaseDao().retrieveByClause(
				LeaveTypeCopyVO.class,
				"pk_timeitem = (select pk_timeitem from tbm_timeitem where timeitemcode='" + arguments[0]
						+ "') and pk_org = '" + waLoginContext.getPk_org() + "'");

		if (leaveTypeCopys == null || leaveTypeCopys.size() == 0) {
			throw new BusinessException("無法找到休假類別 [" + arguments[0] + "]。");
		}

		String propName = "";
		switch (arguments[1]) {
		case "0": // "享有"
			propName = "totalhours";
			break;
		case "1":// "已休"
			propName = "consumedhours";
			break;
		case "2":// "結餘"
			propName = "remainhours";
			break;
		case "3": // "凍結"
			propName = "frozenhours";
			break;
		case "4": // "可用"
			propName = "freehours";
			break;
		}

		// 取數的年度，根據薪資期間對應的考勤期間， 顯示在考勤期間結束間結束日時，未到期的那個年度的時數
		Collection<PeriodVO> periodVos = this.getBaseDao().retrieveByClause(
				PeriodVO.class,
				"pk_org = '" + waLoginContext.getPk_org() + "' and accyear='"
						+ waLoginContext.getWaLoginVO().getPeriodVO().getCaccyear() + "' and accmonth='"
						+ waLoginContext.getWaLoginVO().getPeriodVO().getCaccperiod() + "'");

		if (periodVos != null && periodVos.size() > 0) {
			// 考勤期間結束日期
			UFLiteralDate endDate = periodVos.toArray(new PeriodVO[0])[0].getEnddate();

			Map<String, UFDouble> leaveHours = new HashMap<String, UFDouble>();

			// 取任職記錄（不含離職及留停）
			Collection<PsnJobVO> jobvos = getPsnJobsWithNoLeaveByPsnSQL(sql, periodVos);

			// 取組織關係（進入日期及年資起算日）
			List<Map<String, Object>> psnDates = getNewestPsnOrgByPsnSQL(sql);

			for (String pk_psndoc : pk_psndocs) {
				if (jobvos != null && jobvos.size() > 0) {
					for (PsnJobVO jobvo : jobvos) {
						if (jobvo.getPk_psndoc().equals(pk_psndoc)) {
							UFLiteralDate jobenddate = jobvo.getEnddate();
							endDate = endDate.before(jobenddate == null ? new UFLiteralDate("9999-12-31") : jobenddate) ? endDate
									: jobenddate;

							if (psnDates != null && psnDates.size() > 0) {
								for (Map<String, Object> psndates : psnDates) {
									if (pk_psndoc.equals(psndates.get("pk_psndoc"))) {
										UFLiteralDate[] scopeDates = new UFLiteralDate[2];
										LeaveTypeCopyVO leaveTypeCopy = leaveTypeCopys.toArray(new LeaveTypeCopyVO[0])[0];
										String checkYear = getCheckYear(endDate, psndates, scopeDates, leaveTypeCopy);
										if (arguments[0].equals("F00")) {
											// 年度補休
											ILeaveExtraRestService service = NCLocator.getInstance().lookup(
													ILeaveExtraRestService.class);
											OTLeaveBalanceVO[] vos = service.getLeaveExtHoursByType(
													waLoginContext.getPk_org(), new String[] { pk_psndoc }, null, null,
													checkYear, leaveTypeCopy.getPk_timeitemcopy(), false);

											if (vos != null && vos.length == 1) {
												leaveHours
														.put(pk_psndoc,
																new UFDouble(String.valueOf(vos[0]
																		.getAttributeValue(propName))));
											} else {
												leaveHours.put(pk_psndoc, UFDouble.ZERO_DBL);
											}
										} else if (arguments[0].equals("F01")) {
											// 加班轉補休
											ISegDetailService service = NCLocator.getInstance().lookup(
													ISegDetailService.class);
											OTLeaveBalanceVO[] vos = service.getOvertimeToRestHoursByType(
													waLoginContext.getPk_org(), new String[] { pk_psndoc }, null, null,
													checkYear, leaveTypeCopy.getPk_timeitemcopy());

											if (vos != null && vos.length == 1) {
												leaveHours
														.put(pk_psndoc,
																new UFDouble(String.valueOf(vos[0]
																		.getAttributeValue(propName))));
											} else {
												leaveHours.put(pk_psndoc, UFDouble.ZERO_DBL);
											}
										}
									}
								}
							}
						}
					}
				}
				if (!leaveHours.containsKey(pk_psndoc)) {
					leaveHours.put(pk_psndoc, UFDouble.ZERO_DBL);
				}
			}

			for (Entry<String, UFDouble> entry : leaveHours.entrySet()) {
				String updatesql = "update wa_cacu_data  set " + " cacu_value='" + entry.getValue()
						+ "' where pk_psndoc='" + entry.getKey() + "' and pk_wa_class = '"
						+ waLoginContext.getPk_wa_class() + "'";
				getBaseDao().executeUpdate(updatesql);
			}
		}
	}

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}
}
