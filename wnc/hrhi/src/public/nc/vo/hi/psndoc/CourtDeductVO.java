package nc.vo.hi.psndoc;

import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class CourtDeductVO extends PsndocDefVO {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 414532573440568725L;

	public final static String PK_CREDITORDOC = "pk_creditordoc";
	public final static String DEDUCTBASE = "deductbase";
	public final static String MONTHRATE = "monthrate";
	public final static String MONTHAMOUNT = "monthamount";
	public final static String BSTOPPED = "bstopped";
	public final static String MEMO = "memo";

	private String pk_creditordoc;
	private UFDouble deductbase;
	private UFDouble monthrate;
	private UFDouble monthamount;
	private UFBoolean bstopped;
	private String memo;

	public String getPk_creditordoc() {
		return pk_creditordoc;
	}

	public void setPk_creditordoc(String pk_creditordoc) {
		this.pk_creditordoc = pk_creditordoc;
	}

	public UFDouble getDeductbase() {
		return deductbase;
	}

	public void setDeductbase(UFDouble deductbase) {
		this.deductbase = deductbase;
	}

	public UFDouble getMonthrate() {
		return monthrate;
	}

	public void setMonthrate(UFDouble monthrate) {
		this.monthrate = monthrate;
	}

	public UFDouble getMonthamount() {
		return monthamount;
	}

	public void setMonthamount(UFDouble monthamount) {
		this.monthamount = monthamount;
	}

	public UFBoolean getBstopped() {
		return bstopped;
	}

	public void setBstopped(UFBoolean bstopped) {
		this.bstopped = bstopped;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Override
	public String getTableName() {
		return "hi_psndoc_courtdeduct";
	}

}
