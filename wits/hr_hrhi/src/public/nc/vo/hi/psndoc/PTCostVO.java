package nc.vo.hi.psndoc;

import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

public class PTCostVO extends PsndocDefVO {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = -9213485009742547301L;

	public static final String PAYDATE = "paydate";
	public static final String DECLAREFORMAT = "declareformat";
	public static final String PAYAMOUNT = "payamount";
	public static final String TAXFREEPAYAMOUNT = "taxfreepayamount";
	public static final String FINALAMOUNT = "finalamount";
	public static final String TAXAMOUNT = "taxamount";
	public static final String EXTENDAMOUNT = "extendamount";
	public static final String MEMO = "memo";
	public static final String NETINCOME = "netincome";

	private UFLiteralDate paydate;
	private String declareformat;
	private UFDouble payamount;
	private UFDouble taxfreepayamount;
	private UFDouble finalamount;
	private UFDouble taxamount;
	private UFDouble extendamount;
	private String memo;
	private UFDouble netincome;

	public UFLiteralDate getPaydate() {
		return paydate;
	}

	public void setPaydate(UFLiteralDate paydate) {
		this.paydate = paydate;
	}

	public String getDeclareformat() {
		return declareformat;
	}

	public void setDeclareformat(String declareformat) {
		this.declareformat = declareformat;
	}

	public UFDouble getPayamount() {
		return payamount;
	}

	public void setPayamount(UFDouble payamount) {
		this.payamount = payamount;
	}

	public UFDouble getTaxfreepayamount() {
		return taxfreepayamount;
	}

	public void setTaxfreepayamount(UFDouble taxfreepayamount) {
		this.taxfreepayamount = taxfreepayamount;
	}

	public UFDouble getFinalamount() {
		return finalamount;
	}

	public void setFinalamount(UFDouble finalamount) {
		this.finalamount = finalamount;
	}

	public UFDouble getTaxamount() {
		return taxamount;
	}

	public void setTaxamount(UFDouble taxamount) {
		this.taxamount = taxamount;
	}

	public UFDouble getExtendamount() {
		return extendamount;
	}

	public void setExtendamount(UFDouble extendamount) {
		this.extendamount = extendamount;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public UFDouble getNetincome() {
		return netincome;
	}

	public void setNetincome(UFDouble netincome) {
		this.netincome = netincome;
	}

	@Override
	public String getTableName() {
		return "hi_psndoc_ptcost";
	}
}
