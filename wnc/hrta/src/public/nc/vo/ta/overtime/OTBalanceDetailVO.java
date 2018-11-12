package nc.vo.ta.overtime;

import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> ��̎��Ҫ��������� </b>
 * <p>
 * ��̎����������Ϣ
 * </p>
 * ��������:2018/10/12
 * 
 * @author
 * @version NCPrj ??
 */
public class OTBalanceDetailVO extends nc.vo.pub.SuperVO {

    private java.lang.String pk_otleavebalance;
    private java.lang.String pk_balancedetail;
    private java.lang.Integer sourcetype;
    private java.lang.String pk_sourcebill;
    private nc.vo.pub.lang.UFLiteralDate calendar;
    private nc.vo.pub.lang.UFDouble billhours;
    private nc.vo.pub.lang.UFDouble consumedhours;
    private nc.vo.pub.lang.UFDouble frozenhours;
    private nc.vo.pub.lang.UFBoolean closeflag;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;

    public static final String PK_OTLEAVEBALANCE = "pk_otleavebalance";
    public static final String PK_BALANCEDETAIL = "pk_balancedetail";
    public static final String SOURCETYPE = "sourcetype";
    public static final String PK_SOURCEBILL = "pk_sourcebill";
    public static final String CALENDAR = "calendar";
    public static final String BILLHOURS = "billhours";
    public static final String CONSUMEDHOURS = "consumedhours";
    public static final String FROZENHOURS = "frozenhours";
    public static final String CLOSEFLAG = "closeflag";

    /**
     * ���� pk_otleavebalance��Getter����.��������parentPK ��������:2018/10/12
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_otleavebalance() {
	return pk_otleavebalance;
    }

    /**
     * ����pk_otleavebalance��Setter����.��������parentPK ��������:2018/10/12
     * 
     * @param newPk_otleavebalance
     *            java.lang.String
     */
    public void setPk_otleavebalance(java.lang.String newPk_otleavebalance) {
	this.pk_otleavebalance = newPk_otleavebalance;
    }

    /**
     * ���� pk_balancedetail��Getter����.���������ӱ����I ��������:2018/10/12
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_balancedetail() {
	return pk_balancedetail;
    }

    /**
     * ����pk_balancedetail��Setter����.���������ӱ����I ��������:2018/10/12
     * 
     * @param newPk_balancedetail
     *            java.lang.String
     */
    public void setPk_balancedetail(java.lang.String newPk_balancedetail) {
	this.pk_balancedetail = newPk_balancedetail;
    }

    /**
     * ���� sourcetype��Getter����.���������Γ���� ��������:2018/10/12
     * 
     * @return java.lang.Integer
     */
    public java.lang.Integer getSourcetype() {
	return sourcetype;
    }

    /**
     * ����sourcetype��Setter����.���������Γ���� ��������:2018/10/12
     * 
     * @param newSourcetype
     *            java.lang.Integer
     */
    public void setSourcetype(java.lang.Integer newSourcetype) {
	this.sourcetype = newSourcetype;
    }

    /**
     * ���� pk_sourcebill��Getter����.��������Դ�ΆΓ�PK ��������:2018/10/12
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_sourcebill() {
	return pk_sourcebill;
    }

    /**
     * ����pk_sourcebill��Setter����.��������Դ�ΆΓ�PK ��������:2018/10/12
     * 
     * @param newPk_sourcebill
     *            java.lang.String
     */
    public void setPk_sourcebill(java.lang.String newPk_sourcebill) {
	this.pk_sourcebill = newPk_sourcebill;
    }

    /**
     * ���� calendar��Getter����.���������Γ����� ��������:2018/10/12
     * 
     * @return nc.vo.pub.lang.UFLiteralDate
     */
    public nc.vo.pub.lang.UFLiteralDate getCalendar() {
	return calendar;
    }

    /**
     * ����calendar��Setter����.���������Γ����� ��������:2018/10/12
     * 
     * @param newCalendar
     *            nc.vo.pub.lang.UFLiteralDate
     */
    public void setCalendar(nc.vo.pub.lang.UFLiteralDate newCalendar) {
	this.calendar = newCalendar;
    }

    /**
     * ���� billhours��Getter����.���������Γ��r�� ��������:2018/10/12
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getBillhours() {
	return billhours;
    }

    /**
     * ����billhours��Setter����.���������Γ��r�� ��������:2018/10/12
     * 
     * @param newBillhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setBillhours(nc.vo.pub.lang.UFDouble newBillhours) {
	this.billhours = newBillhours;
    }

    /**
     * ���� consumedhours��Getter����.�����������Õr�� ��������:2018/10/12
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getConsumedhours() {
	return consumedhours;
    }

    /**
     * ����consumedhours��Setter����.�����������Õr�� ��������:2018/10/12
     * 
     * @param newConsumedhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setConsumedhours(nc.vo.pub.lang.UFDouble newConsumedhours) {
	this.consumedhours = newConsumedhours;
    }

    /**
     * ���� frozenhours��Getter����.�����������Y�r�� ��������:2018/10/12
     * 
     * @return nc.vo.pub.lang.UFBoolean
     */
    public nc.vo.pub.lang.UFDouble getFrozenhours() {
	return frozenhours;
    }

    /**
     * ����frozenhours��Setter����.�����������Y�r�� ��������:2018/10/12
     * 
     * @param newFrozenhours
     *            nc.vo.pub.lang.UFBoolean
     */
    public void setFrozenhours(nc.vo.pub.lang.UFDouble newFrozenhours) {
	this.frozenhours = newFrozenhours;
    }

    /**
     * ���� closeflag��Getter����.���������Ƿ�Y�� ��������:2018/10/12
     * 
     * @return nc.vo.pub.lang.UFBoolean
     */
    public nc.vo.pub.lang.UFBoolean getCloseflag() {
	return closeflag;
    }

    /**
     * ����closeflag��Setter����.���������Ƿ�Y�� ��������:2018/10/12
     * 
     * @param newCloseflag
     *            nc.vo.pub.lang.UFBoolean
     */
    public void setCloseflag(nc.vo.pub.lang.UFBoolean newCloseflag) {
	this.closeflag = newCloseflag;
    }

    /**
     * ���� dr��Getter����.��������dr ��������:2018/10/12
     * 
     * @return java.lang.Integer
     */
    public java.lang.Integer getDr() {
	return dr;
    }

    /**
     * ����dr��Setter����.��������dr ��������:2018/10/12
     * 
     * @param newDr
     *            java.lang.Integer
     */
    public void setDr(java.lang.Integer newDr) {
	this.dr = newDr;
    }

    /**
     * ���� ts��Getter����.��������ts ��������:2018/10/12
     * 
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getTs() {
	return ts;
    }

    /**
     * ����ts��Setter����.��������ts ��������:2018/10/12
     * 
     * @param newTs
     *            nc.vo.pub.lang.UFDateTime
     */
    public void setTs(nc.vo.pub.lang.UFDateTime newTs) {
	this.ts = newTs;
    }

    /**
     * <p>
     * ȡ�ø�VO���I�ֶ�.
     * <p>
     * ��������:2018/10/12
     * 
     * @return java.lang.String
     */
    public java.lang.String getParentPKFieldName() {
	return "pk_otleavebalance";
    }

    /**
     * <p>
     * ȡ�ñ����I.
     * <p>
     * ��������:2018/10/12
     * 
     * @return java.lang.String
     */
    public java.lang.String getPKFieldName() {

	return "pk_balancedetail";
    }

    /**
     * <p>
     * ���ر����Q
     * <p>
     * ��������:2018/10/12
     * 
     * @return java.lang.String
     */
    public java.lang.String getTableName() {
	return "hrta_otbalancedetail";
    }

    /**
     * <p>
     * ���ر����Q.
     * <p>
     * ��������:2018/10/12
     * 
     * @return java.lang.String
     */
    public static java.lang.String getDefaultTableName() {
	return "hrta_otbalancedetail";
    }

    /**
     * ����Ĭ�J��ʽ����������.
     * 
     * ��������:2018/10/12
     */
    public OTBalanceDetailVO() {
	super();
    }

    @nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.ta.overtime.OTBalanceDetailVO")
    public IVOMeta getMetaData() {
	return VOMetaFactory.getInstance().getVOMeta("overtime.otbalancedetail");

    }

}
