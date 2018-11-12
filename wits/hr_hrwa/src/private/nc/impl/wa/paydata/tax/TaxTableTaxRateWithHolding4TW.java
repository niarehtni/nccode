package nc.impl.wa.paydata.tax;

/**
 * 变动税率表代扣税（台灣）
 * 
 * @author: ssx
 * @date: 2014-03-27
 * @since: eHR V6.3
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("restriction")
public class TaxTableTaxRateWithHolding4TW extends TaxTableTaxRate {

	@Override
	public String getTaxableIncome() {

		final String base = taxFormulaVO.getBase();
		String inCome_tax = null;

		inCome_tax = " (" + base + " -  " + getdeduction() + ")";

		return inCome_tax;

	}

	private String getdeduction() {
		return null;
	}

}
