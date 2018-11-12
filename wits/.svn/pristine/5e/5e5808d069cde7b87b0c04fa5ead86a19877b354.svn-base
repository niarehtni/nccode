package nc.impl.wa.func;

import nc.bs.dao.BaseDAO;
import nc.impl.wa.paydata.precacu.TaxFormulaPreExcutor;
import nc.impl.wa.paydata.precacu.TaxFormulaPreExcutor4TW;
import nc.impl.wa.paydata.precacu.TaxFormulaPreExcutor4TWForeign;
import nc.impl.wa.paydata.tax.TaxFormulaUtil;
import nc.impl.wa.paydata.tax.TaxFormulaVO;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;
import nc.vo.wa.pub.WaLoginContext;

public class TaxRateData4TW extends AbstractPreExcutorFormulaParse {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 9263441577185272L;

	@Override
	public void excute(Object formula, WaLoginContext context)
			throws BusinessException {
		TaxFormulaVO taxFormulaVO = TaxFormulaUtil.translate2FormulaVO(
				getFunctionVO(), formula.toString());
		IFormula excutor = null;
		BaseDAO baseDao = new BaseDAO();
		String strCountryZone = (String) baseDao
				.executeQuery(
						"select ct.code from wa_waclass wc "
								+ "inner join hr_globalcountry gc on wc.pk_country = gc.pk_country "
								+ " inner join bd_countryzone ct on gc.pk_country = ct.pk_country where wc.pk_wa_class='"
								+ context.getPk_wa_class() + "'",
						new ColumnProcessor());

		if ("TW".equals(strCountryZone)) {
			excutor = new TaxFormulaPreExcutor4TW();
			excutor.excute(taxFormulaVO, getContext());
			//add by ward 2018-01-18 增加外籍员工扣税方式 begin
			excutor=new TaxFormulaPreExcutor4TWForeign();
			excutor.excute(taxFormulaVO, getContext());
			//end
			
		} else {
			excutor = new TaxFormulaPreExcutor();
			excutor.excute(taxFormulaVO, getContext());
		}
		
	}
}
