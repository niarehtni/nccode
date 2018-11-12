package nc.impl.wa.func;

import java.util.Calendar;

import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

public class NhiIsHealthData extends AbstractWAFormulaParse {
	public NhiIsHealthData(){}
	private UFDate getFirstDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return new UFDate(calendar.getTime()).asBegin();
	}

	private UFDate getLastDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
		int lastday = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, lastday);
		return new UFDate(calendar.getTime()).asEnd();
	}

	@Override
	public FunctionReplaceVO getReplaceStr(String arg0)
			throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("");
		String strCustomSelect = "(select isnull((select top 1 glbdef14 "
				+ " from "
				+ PsndocDefTableUtil.getPsnHealthTablename()
				+ "  where dr = 0 and begindate<='"
				+ this.getLastDayOfMonth(context.getWaYear(),
						context.getWaPeriod())
				+ "' and isnull(enddate, '9999-12-31')>='"
				+ this.getFirstDayOfMonth(context.getWaYear(),
						context.getWaPeriod())
				+ "' and pk_psndoc = wa_data.pk_psndoc and glbdef3 = (select id from bd_psndoc where pk_psndoc = wa_data.pk_psndoc)), 'N')) ";
		fvo.setReplaceStr(strCustomSelect);
		return fvo;
	}
}
