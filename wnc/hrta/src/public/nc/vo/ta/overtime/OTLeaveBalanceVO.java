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
public class OTLeaveBalanceVO extends nc.vo.pub.SuperVO {

    private java.lang.String pk_otleavebalance;
    private java.lang.String pk_psndoc;
    private java.lang.String pk_group;
    private java.lang.String pk_org;
    private java.lang.String pk_org_v;
    private nc.vo.pub.lang.UFDouble totalhours;
    private nc.vo.pub.lang.UFDouble consumedhours;
    private nc.vo.pub.lang.UFDouble remainhours;
    private nc.vo.pub.lang.UFDouble frozenhours;
    private nc.vo.pub.lang.UFDouble freehours;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;

    private nc.vo.ta.overtime.OTBalanceDetailVO[] pk_balancedetail;

    public static final String PK_OTLEAVEBALANCE = "pk_otleavebalance";
    public static final String PK_PSNDOC = "pk_psndoc";
    public static final String PK_GROUP = "pk_group";
    public static final String PK_ORG = "pk_org";
    public static final String PK_ORG_V = "pk_org_v";
    public static final String TOTALHOURS = "totalhours";
    public static final String CONSUMEDHOURS = "consumedhours";
    public static final String REMAINHOURS = "remainhours";
    public static final String FROZENHOURS = "frozenhours";
    public static final String FREEHOURS = "freehours";

    /**
     * ���� pk_otleavebalance��Getter����.���������������I ��������:2018/10/12
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_otleavebalance() {
	return pk_otleavebalance;
    }

    /**
     * ����pk_otleavebalance��Setter����.���������������I ��������:2018/10/12
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
     * @return nc.vo.ta.overtime.OTBalanceDetailVO[]
     */
    public nc.vo.ta.overtime.OTBalanceDetailVO[] getPk_balancedetail() {
	return pk_balancedetail;
    }

    /**
     * ����pk_balancedetail��Setter����.���������ӱ����I ��������:2018/10/12
     * 
     * @param newPk_balancedetail
     *            nc.vo.ta.overtime.OTBalanceDetailVO[]
     */
    public void setPk_balancedetail(nc.vo.ta.overtime.OTBalanceDetailVO[] newPk_balancedetail) {
	this.pk_balancedetail = newPk_balancedetail;
    }

    /**
     * ���� pk_psndoc��Getter����.���������T����Ϣ ��������:2018/10/12
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_psndoc() {
	return pk_psndoc;
    }

    /**
     * ����pk_psndoc��Setter����.���������T����Ϣ ��������:2018/10/12
     * 
     * @param newPk_psndoc
     *            java.lang.String
     */
    public void setPk_psndoc(java.lang.String newPk_psndoc) {
	this.pk_psndoc = newPk_psndoc;
    }

    /**
     * ���� pk_group��Getter����.�����������F ��������:2018/10/12
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_group() {
	return pk_group;
    }

    /**
     * ����pk_group��Setter����.�����������F ��������:2018/10/12
     * 
     * @param newPk_group
     *            java.lang.String
     */
    public void setPk_group(java.lang.String newPk_group) {
	this.pk_group = newPk_group;
    }

    /**
     * ���� pk_org��Getter����.���������M�� ��������:2018/10/12
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_org() {
	return pk_org;
    }

    /**
     * ����pk_org��Setter����.���������M�� ��������:2018/10/12
     * 
     * @param newPk_org
     *            java.lang.String
     */
    public void setPk_org(java.lang.String newPk_org) {
	this.pk_org = newPk_org;
    }

    /**
     * ���� pk_org_v��Getter����.���������M���汾 ��������:2018/10/12
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_org_v() {
	return pk_org_v;
    }

    /**
     * ����pk_org_v��Setter����.���������M���汾 ��������:2018/10/12
     * 
     * @param newPk_org_v
     *            java.lang.String
     */
    public void setPk_org_v(java.lang.String newPk_org_v) {
	this.pk_org_v = newPk_org_v;
    }

    /**
     * ���� totalhours��Getter����.�����������Еr�� ��������:2018/10/12
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getTotalhours() {
	return totalhours;
    }

    /**
     * ����totalhours��Setter����.�����������Еr�� ��������:2018/10/12
     * 
     * @param newTotalhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setTotalhours(nc.vo.pub.lang.UFDouble newTotalhours) {
	this.totalhours = newTotalhours;
    }

    /**
     * ���� consumedhours��Getter����.�����������ݕr�� ��������:2018/10/12
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getConsumedhours() {
	return consumedhours;
    }

    /**
     * ����consumedhours��Setter����.�����������ݕr�� ��������:2018/10/12
     * 
     * @param newConsumedhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setConsumedhours(nc.vo.pub.lang.UFDouble newConsumedhours) {
	this.consumedhours = newConsumedhours;
    }

    /**
     * ���� remainhours��Getter����.���������Y�N�r�� ��������:2018/10/12
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getRemainhours() {
	return remainhours;
    }

    /**
     * ����remainhours��Setter����.���������Y�N�r�� ��������:2018/10/12
     * 
     * @param newRemainhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setRemainhours(nc.vo.pub.lang.UFDouble newRemainhours) {
	this.remainhours = newRemainhours;
    }

    /**
     * ���� frozenhours��Getter����.�����������Y�r�� ��������:2018/10/12
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getFrozenhours() {
	return frozenhours;
    }

    /**
     * ����frozenhours��Setter����.�����������Y�r�� ��������:2018/10/12
     * 
     * @param newFrozenhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setFrozenhours(nc.vo.pub.lang.UFDouble newFrozenhours) {
	this.frozenhours = newFrozenhours;
    }

    /**
     * ���� freehours��Getter����.�����������Õr�� ��������:2018/10/12
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getFreehours() {
	return freehours;
    }

    /**
     * ����freehours��Setter����.�����������Õr�� ��������:2018/10/12
     * 
     * @param newFreehours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setFreehours(nc.vo.pub.lang.UFDouble newFreehours) {
	this.freehours = newFreehours;
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
	return null;
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

	return "pk_otleavebalance";
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
	return "hrta_otleavebalance";
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
	return "hrta_otleavebalance";
    }

    /**
     * ����Ĭ�J��ʽ����������.
     * 
     * ��������:2018/10/12
     */
    public OTLeaveBalanceVO() {
	super();
    }

    @nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.ta.overtime.OTLeaveBalanceVO")
    public IVOMeta getMetaData() {
	return VOMetaFactory.getInstance().getVOMeta("overtime.otleavebalance");

    }

}
