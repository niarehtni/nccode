package nc.vo.hi.psndoc;

import nc.vo.pub.lang.UFDouble;

public class CourtDeductRecVO extends PsndocDefVO {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 7993960296611165510L;

	public final static String PK_CREDITORDOC = "pk_creditordoc";
	public final static String CYEARPERIOD = "cyearperiod";
	public final static String PK_WACLASS = "pk_waclass";
	public final static String DEDUCTAMOUNT = "deductamount";
	public final static String REMAINAMOUNT = "remainamount";

	private String pk_creditordoc;
	private String cyearperiod;
	private String pk_waclass;
	private UFDouble deductamount;
	private UFDouble remainamount;

	public String getPk_creditordoc() {
		return pk_creditordoc;
	}

	public void setPk_creditordoc(String pk_creditordoc) {
		this.pk_creditordoc = pk_creditordoc;
	}

	public String getCyearperiod() {
		return cyearperiod;
	}

	public void setCyearperiod(String cyearperiod) {
		this.cyearperiod = cyearperiod;
	}

	public String getPk_waclass() {
		return pk_waclass;
	}

	public void setPk_waclass(String pk_waclass) {
		this.pk_waclass = pk_waclass;
	}

	public UFDouble getDeductamount() {
		return deductamount;
	}

	public void setDeductamount(UFDouble deductamount) {
		this.deductamount = deductamount;
	}

	public UFDouble getRemainamount() {
		return remainamount;
	}

	public void setRemainamount(UFDouble remainamount) {
		this.remainamount = remainamount;
	}

	@Override
	public String getTableName() {
		return "hi_psndoc_courtdetail";
	}

}
