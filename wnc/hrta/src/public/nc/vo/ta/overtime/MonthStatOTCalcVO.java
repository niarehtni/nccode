package nc.vo.ta.overtime;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

public class MonthStatOTCalcVO extends SuperVO {

    /**
     * serial no
     */
    private static final long serialVersionUID = 6229883637570849470L;

    private String pk_org;
    private String pk_psndoc;
    private String cyear;
    private String cperiod;
    private OvertimeSettleTypeEnum settleType;
    private CalendarDateTypeEnum dateType;
    private UFDouble hours;

    public String getPk_org() {
	return pk_org;
    }

    public void setPk_org(String pk_org) {
	this.pk_org = pk_org;
    }

    public String getPk_psndoc() {
	return pk_psndoc;
    }

    public void setPk_psndoc(String pk_psndoc) {
	this.pk_psndoc = pk_psndoc;
    }

    public String getCyear() {
	return cyear;
    }

    public void setCyear(String cyear) {
	this.cyear = cyear;
    }

    public String getCperiod() {
	return cperiod;
    }

    public void setCperiod(String cperiod) {
	this.cperiod = cperiod;
    }

    public OvertimeSettleTypeEnum getSettleType() {
	return settleType;
    }

    public void setSettleType(OvertimeSettleTypeEnum settleType) {
	this.settleType = settleType;
    }

    public CalendarDateTypeEnum getDateType() {
	return dateType;
    }

    public void setDateType(CalendarDateTypeEnum dateType) {
	this.dateType = dateType;
    }

    public UFDouble getHours() {
	return hours;
    }

    public void setHours(UFDouble hours) {
	this.hours = hours;
    }
}
