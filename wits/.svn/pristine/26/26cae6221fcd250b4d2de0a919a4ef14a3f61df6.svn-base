package nc.impl.wa.func;

import nc.impl.wa.paydata.precacu.TaxFormulaPreExcutor;
import nc.impl.wa.paydata.tax.TaxFormulaUtil;
import nc.impl.wa.paydata.tax.TaxFormulaVO;
import nc.impl.wa.paydata.tax.YearTaxTotalInfPreProcess;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 
 * @author: zhangg
 * @date: 2010-5-12 下午02:40:32
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class TaxRateData extends AbstractPreExcutorFormulaParse {

	/**
	 * @author zhangg on 2010-5-14
	 * @see nc.vo.wa.paydata.IFormula#excute(java.lang.Object,
	 *      nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public void excute(Object formula, WaLoginContext context) throws BusinessException {
		TaxFormulaVO taxFormulaVO = TaxFormulaUtil.translate2FormulaVO(getFunctionVO(),formula.toString());
// {MOD:新个税补丁}
// begin
		// IFormula excutor = new TaxFormulaPreExcutor();
		// excutor.excute(taxFormulaVO, getContext());
		
		if (taxFormulaVO != null) {
			String classType =	taxFormulaVO.getClass_type();
			//年度累计ishuilvbiao计税
			if (TaxFormulaVO.CLASS_TYPE_TOTAL_YEAR.equals(classType)) {
				IFormula excutor = new YearTaxTotalInfPreProcess();
				excutor.excute(taxFormulaVO, getContext());
			}else {
				IFormula excutor = new TaxFormulaPreExcutor();
				excutor.excute(taxFormulaVO, getContext());
			}
		}
// end
	}

}
