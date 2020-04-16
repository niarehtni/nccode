package nc.impl.wa.func;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.NCLocator;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.overtime.CalendarDateTypeEnum;
import nc.vo.ta.period.PeriodVO;
import nc.vo.wa.pub.WaLoginContext;

public class ValueOfOTSalaryHourParse extends AbstractPreExcutorFormulaParse {

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

		CalendarDateTypeEnum dateType = null;
		switch (arguments[0]) {
		case "0": // "平日"
			dateType = CalendarDateTypeEnum.NORMAL;
			break;
		case "1":// "休息日"
			dateType = CalendarDateTypeEnum.OFFDAY;
			break;
		case "2":// "例假日"
			dateType = CalendarDateTypeEnum.HOLIDAY;
			break;
		case "3": // "國假"
			dateType = CalendarDateTypeEnum.NATIONALDAY;
			break;
		case "4": // "事件假"
			dateType = CalendarDateTypeEnum.EVENTDAY;
			break;
		}

		// 取數的年度，根據薪資期間對應的考勤期間， 顯示在考勤期間結束間結束日時，未到期的那個年度的時數
		Collection<PeriodVO> periodVos = this.getBaseDao().retrieveByClause(
				PeriodVO.class,
				"pk_org = '" + waLoginContext.getPk_org() + "' and accyear='"
						+ waLoginContext.getWaLoginVO().getPeriodVO().getCaccyear() + "' and accmonth='"
						+ waLoginContext.getWaLoginVO().getPeriodVO().getCaccperiod() + "'");

		ISegDetailService service = NCLocator.getInstance().lookup(ISegDetailService.class);
		Map<String, UFDouble> otHours = service.calculateOvertimeHoursByType(waLoginContext.getPk_org(),
				pk_psndocs.toArray(new String[0]), periodVos.toArray(new PeriodVO[0])[0].getBegindate(),
				periodVos.toArray(new PeriodVO[0])[0].getEnddate(), dateType);

		for (Entry<String, UFDouble> entry : otHours.entrySet()) {
			String updatesql = "update wa_cacu_data  set " + " cacu_value='" + entry.getValue() + "' where pk_psndoc='"
					+ entry.getKey() + "' and pk_wa_class = '" + waLoginContext.getPk_wa_class() + "'";
			getBaseDao().executeUpdate(updatesql);
		}
	}
}
