package nc.vo.hi.wadoc;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * 團保批量退保數據類型
 * 
 * @author sunsx
 * 
 */
public class BatchGroupInsuranceExitVO extends SuperVO {

	/**
	 * serail no
	 */
	private static final long serialVersionUID = 9051934155016676523L;
	private int lineno;

	public int getLineno() {
		return lineno;
	}

	public void setLineno(int lineno) {
		this.lineno = lineno;
	}

	public String getPsncode() {
		return psncode;
	}

	public void setPsncode(String psncode) {
		this.psncode = psncode;
	}

	public String getClerkcode() {
		return clerkcode;
	}

	public void setClerkcode(String clerkcode) {
		this.clerkcode = clerkcode;
	}

	public String getIdno() {
		return idno;
	}

	public void setIdno(String idno) {
		this.idno = idno;
	}

	public String getInsurancecode() {
		return insurancecode;
	}

	public void setInsurancecode(String insurancecode) {
		this.insurancecode = insurancecode;
	}

	public UFLiteralDate getExitdate() {
		return exitdate;
	}

	public void setExitdate(UFLiteralDate exitdate) {
		this.exitdate = exitdate;
	}

	public String getErrmessage() {
		return errmessage;
	}

	public void setErrmessage(String errmessage) {
		this.errmessage = errmessage;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String psncode;
	private String clerkcode;
	private String idno;
	private String insurancecode;
	private UFLiteralDate exitdate;
	private String errmessage;

}
