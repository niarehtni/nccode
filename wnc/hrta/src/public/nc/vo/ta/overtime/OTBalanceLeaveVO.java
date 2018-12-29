package nc.vo.ta.overtime;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

public class OTBalanceLeaveVO extends SuperVO {
    /**
     * serial no
     */
    private static final long serialVersionUID = -5054134673971129321L;

    private String pk_leavereg;
    private UFDouble leavehours;
    private UFDouble spenthours;
    private UFLiteralDate leavedate;

    public String getPk_leavereg() {
	return pk_leavereg;
    }

    public void setPk_leavereg(String pk_leavereg) {
	this.pk_leavereg = pk_leavereg;
    }

    public UFDouble getLeavehours() {
	return leavehours;
    }

    public void setLeavehours(UFDouble leavehours) {
	this.leavehours = leavehours;
    }

    public UFDouble getSpenthours() {
	return spenthours;
    }

    public void setSpenthours(UFDouble spenthours) {
	this.spenthours = spenthours;
    }

    public UFLiteralDate getLeavedate() {
	return leavedate;
    }

    public void setLeavedate(UFLiteralDate leavedate) {
	this.leavedate = leavedate;
    }
}
