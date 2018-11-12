package nc.itf.hr.hi;

import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * ∂®’{–ΩŸY”ç≤È‘ÉVO
 * 
 * @author SSX
 * @since 2014-10-10
 */
public class WadocQueryVO {
	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 7914878008553996973L;

	private String pk_psndoc;
	private UFDate begindate;
	private UFDate enddate;
	private UFDouble nmoney;

	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String BEGINDATE = "begindate";
	public static final String ENDDATE = "enddate";
	public static final String NMONEY = "nmoney";

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public UFDate getBegindate() {
		return begindate;
	}

	public void setBegindate(UFDate begindate) {
		this.begindate = begindate;
	}

	public UFDate getEnddate() {
		return enddate;
	}

	public void setEnddate(UFDate enddate) {
		this.enddate = enddate;
	}

	public UFDouble getNmoney() {
		return nmoney;
	}

	public void setNmoney(UFDouble nmoney) {
		this.nmoney = nmoney;
	}
}
