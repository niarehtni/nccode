package nc.impl.wa.func;

import java.util.Calendar;

import nc.impl.hr.formula.parser.AbstractFormulaParser;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.pub.WaLoginContext;

public class Nhi2ndHelpData extends AbstractFormulaParser implements
		nc.vo.wa.paydata.IFormula {

	@Override
	public String parseAfterValidate(String pk_org, String formula,
			Object... params) throws BusinessException {
		WaLoginContext context = (WaLoginContext) params[0];
		FunctionVO funcVo = (FunctionVO) params[1];
		String strCustomSelect = "isnull((select top 1 doc.code "
				+ " from hi_psndoc_glbdef8  def "
				+ " left join bd_defdoc doc on doc.pk_defdoc = glbdef2 "
				+ " inner join bd_defdoclist list on doc.pk_defdoclist = list.pk_defdoclist and list.code = 'JFS008' "
				+ " where def.dr = 0 and def.begindate<='"
				+ this.getLastDayOfMonth(context.getWaYear(),
						context.getWaPeriod())
				+ "' and isnull(def.enddate, '9999-12-31')>='"
				+ this.getFirstDayOfMonth(context.getWaYear(),
						context.getWaPeriod())
				+ "' and def.pk_psndoc = wa_data.pk_psndoc), '~')";
		return strCustomSelect;
	}

	@Override
	public boolean validate(String formula) throws BusinessException {
		return true;
	}

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
	public void excute(Object paramObject, WaLoginContext paramWaLoginContext)
			throws BusinessException {
		// TODO 自動產生的方法 Stub

	}
}
