package nc.vo.ta.overtime;

import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> ��̎��Ҫ��������� </b>
 * <p>
 * ��̎�����������Ϣ
 * </p>
 * ��������:2018/9/7
 * 
 * @author
 * @version NCPrj ??
 */
public class SegRuleTermVO extends nc.vo.pub.SuperVO {

    private java.lang.String pk_segrule;
    private java.lang.String pk_segruleterm;
    private java.lang.String pk_group;
    private java.lang.String pk_org;
    private java.lang.String pk_org_v;
    private java.lang.String creator;
    private nc.vo.pub.lang.UFDateTime creationtime;
    private java.lang.String modifier;
    private nc.vo.pub.lang.UFDateTime modifiedtime;
    private java.lang.Integer taxflag;
    private java.lang.Integer segno;
    private nc.vo.pub.lang.UFDouble startpoint;
    private nc.vo.pub.lang.UFDouble endpoint;
    private nc.vo.pub.lang.UFDouble taxableotrate;
    private nc.vo.pub.lang.UFDouble taxfreeotrate;
    private nc.vo.pub.lang.UFDouble torestrate;
    private nc.vo.pub.lang.UFDouble additionaldays;
    private nc.vo.pub.lang.UFBoolean islimitscope;
    private java.lang.String memo;
    private java.lang.String rowno;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;

    public static final String PK_SEGRULE = "pk_segrule";
    public static final String PK_SEGRULETERM = "pk_segruleterm";
    public static final String PK_GROUP = "pk_group";
    public static final String PK_ORG = "pk_org";
    public static final String PK_ORG_V = "pk_org_v";
    public static final String CREATOR = "creator";
    public static final String CREATIONTIME = "creationtime";
    public static final String MODIFIER = "modifier";
    public static final String MODIFIEDTIME = "modifiedtime";
    public static final String TAXFLAG = "taxflag";
    public static final String SEGNO = "segno";
    public static final String STARTPOINT = "startpoint";
    public static final String ENDPOINT = "endpoint";
    public static final String TAXABLEOTRATE = "taxableotrate";
    public static final String TAXFREEOTRATE = "taxfreeotrate";
    public static final String TORESTRATE = "torestrate";
    public static final String ADDITIONALDAYS = "additionaldays";
    public static final String ISLIMITSCOPE = "islimitscope";
    public static final String MEMO = "memo";
    public static final String ROWNO = "rowno";

    /**
     * ���� pk_segrule��Getter����.��������parentPK ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_segrule() {
	return pk_segrule;
    }

    /**
     * ����pk_segrule��Setter����.��������parentPK ��������:2018/9/7
     * 
     * @param newPk_segrule
     *            java.lang.String
     */
    public void setPk_segrule(java.lang.String newPk_segrule) {
	this.pk_segrule = newPk_segrule;
    }

    /**
     * ���� pk_segruleterm��Getter����.���������Ӱ�ֶ�Ҏ�t���I ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_segruleterm() {
	return pk_segruleterm;
    }

    /**
     * ����pk_segruleterm��Setter����.���������Ӱ�ֶ�Ҏ�t���I ��������:2018/9/7
     * 
     * @param newPk_segruleterm
     *            java.lang.String
     */
    public void setPk_segruleterm(java.lang.String newPk_segruleterm) {
	this.pk_segruleterm = newPk_segruleterm;
    }

    /**
     * ���� pk_group��Getter����.�����������F ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_group() {
	return pk_group;
    }

    /**
     * ����pk_group��Setter����.�����������F ��������:2018/9/7
     * 
     * @param newPk_group
     *            java.lang.String
     */
    public void setPk_group(java.lang.String newPk_group) {
	this.pk_group = newPk_group;
    }

    /**
     * ���� pk_org��Getter����.���������M�� ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_org() {
	return pk_org;
    }

    /**
     * ����pk_org��Setter����.���������M�� ��������:2018/9/7
     * 
     * @param newPk_org
     *            java.lang.String
     */
    public void setPk_org(java.lang.String newPk_org) {
	this.pk_org = newPk_org;
    }

    /**
     * ���� pk_org_v��Getter����.���������M���汾 ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_org_v() {
	return pk_org_v;
    }

    /**
     * ����pk_org_v��Setter����.���������M���汾 ��������:2018/9/7
     * 
     * @param newPk_org_v
     *            java.lang.String
     */
    public void setPk_org_v(java.lang.String newPk_org_v) {
	this.pk_org_v = newPk_org_v;
    }

    /**
     * ���� creator��Getter����.�������������� ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getCreator() {
	return creator;
    }

    /**
     * ����creator��Setter����.�������������� ��������:2018/9/7
     * 
     * @param newCreator
     *            java.lang.String
     */
    public void setCreator(java.lang.String newCreator) {
	this.creator = newCreator;
    }

    /**
     * ���� creationtime��Getter����.�������������r�g ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getCreationtime() {
	return creationtime;
    }

    /**
     * ����creationtime��Setter����.�������������r�g ��������:2018/9/7
     * 
     * @param newCreationtime
     *            nc.vo.pub.lang.UFDateTime
     */
    public void setCreationtime(nc.vo.pub.lang.UFDateTime newCreationtime) {
	this.creationtime = newCreationtime;
    }

    /**
     * ���� modifier��Getter����.���������޸��� ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getModifier() {
	return modifier;
    }

    /**
     * ����modifier��Setter����.���������޸��� ��������:2018/9/7
     * 
     * @param newModifier
     *            java.lang.String
     */
    public void setModifier(java.lang.String newModifier) {
	this.modifier = newModifier;
    }

    /**
     * ���� modifiedtime��Getter����.���������޸ĕr�g ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getModifiedtime() {
	return modifiedtime;
    }

    /**
     * ����modifiedtime��Setter����.���������޸ĕr�g ��������:2018/9/7
     * 
     * @param newModifiedtime
     *            nc.vo.pub.lang.UFDateTime
     */
    public void setModifiedtime(nc.vo.pub.lang.UFDateTime newModifiedtime) {
	this.modifiedtime = newModifiedtime;
    }

    /**
     * ���� taxflag��Getter����.�����������ⶐ�ֶ� ��������:2018/9/7
     * 
     * @return java.lang.Integer
     */
    public java.lang.Integer getTaxflag() {
	return taxflag;
    }

    /**
     * ����taxflag��Setter����.�����������ⶐ�ֶ� ��������:2018/9/7
     * 
     * @param newTaxflag
     *            java.lang.Integer
     */
    public void setTaxflag(java.lang.Integer newTaxflag) {
	this.taxflag = newTaxflag;
    }

    /**
     * ���� segno��Getter����.���������ֶ���̖ ��������:2018/9/7
     * 
     * @return java.lang.Integer
     */
    public java.lang.Integer getSegno() {
	return segno;
    }

    /**
     * ����segno��Setter����.���������ֶ���̖ ��������:2018/9/7
     * 
     * @param newSegno
     *            java.lang.Integer
     */
    public void setSegno(java.lang.Integer newSegno) {
	this.segno = newSegno;
    }

    /**
     * ���� startpoint��Getter����.���������ֶ����c ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getStartpoint() {
	return startpoint;
    }

    /**
     * ����startpoint��Setter����.���������ֶ����c ��������:2018/9/7
     * 
     * @param newStartpoint
     *            nc.vo.pub.lang.UFDouble
     */
    public void setStartpoint(nc.vo.pub.lang.UFDouble newStartpoint) {
	this.startpoint = newStartpoint;
    }

    /**
     * ���� endpoint��Getter����.���������ֶνK�c ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getEndpoint() {
	return endpoint;
    }

    /**
     * ����endpoint��Setter����.���������ֶνK�c ��������:2018/9/7
     * 
     * @param newEndpoint
     *            nc.vo.pub.lang.UFDouble
     */
    public void setEndpoint(nc.vo.pub.lang.UFDouble newEndpoint) {
	this.endpoint = newEndpoint;
    }

    /**
     * ���� taxableotrate��Getter����.�������������Ӱ��M�� ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getTaxableotrate() {
	return taxableotrate;
    }

    /**
     * ����taxableotrate��Setter����.�������������Ӱ��M�� ��������:2018/9/7
     * 
     * @param newTaxableotrate
     *            nc.vo.pub.lang.UFDouble
     */
    public void setTaxableotrate(nc.vo.pub.lang.UFDouble newTaxableotrate) {
	this.taxableotrate = newTaxableotrate;
    }

    /**
     * ���� taxfreeotrate��Getter����.���������ⶐ�Ӱ��M�� ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getTaxfreeotrate() {
	return taxfreeotrate;
    }

    /**
     * ����taxfreeotrate��Setter����.���������ⶐ�Ӱ��M�� ��������:2018/9/7
     * 
     * @param newTaxfreeotrate
     *            nc.vo.pub.lang.UFDouble
     */
    public void setTaxfreeotrate(nc.vo.pub.lang.UFDouble newTaxfreeotrate) {
	this.taxfreeotrate = newTaxfreeotrate;
    }

    /**
     * ���� torestrate��Getter����.���������D�ݱ��� ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getTorestrate() {
	return torestrate;
    }

    /**
     * ����torestrate��Setter����.���������D�ݱ��� ��������:2018/9/7
     * 
     * @param newTorestrate
     *            nc.vo.pub.lang.UFDouble
     */
    public void setTorestrate(nc.vo.pub.lang.UFDouble newTorestrate) {
	this.torestrate = newTorestrate;
    }

    /**
     * ���� additionaldays��Getter����.������������a�ݕr�� ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getAdditionaldays() {
	return additionaldays;
    }

    /**
     * ����additionaldays��Setter����.������������a�ݕr�� ��������:2018/9/7
     * 
     * @param newAdditionaldays
     *            nc.vo.pub.lang.UFDouble
     */
    public void setAdditionaldays(nc.vo.pub.lang.UFDouble newAdditionaldays) {
	this.additionaldays = newAdditionaldays;
    }

    /**
     * ���� islimitscope��Getter����.���������{��Ӱ����޽yӋ ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFBoolean
     */
    public nc.vo.pub.lang.UFBoolean getIslimitscope() {
	return islimitscope;
    }

    /**
     * ����islimitscope��Setter����.���������{��Ӱ����޽yӋ ��������:2018/9/7
     * 
     * @param newIslimitscope
     *            nc.vo.pub.lang.UFBoolean
     */
    public void setIslimitscope(nc.vo.pub.lang.UFBoolean newIslimitscope) {
	this.islimitscope = newIslimitscope;
    }

    /**
     * ���� memo��Getter����.�����������] ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getMemo() {
	return memo;
    }

    /**
     * ����memo��Setter����.�����������] ��������:2018/9/7
     * 
     * @param newMemo
     *            java.lang.String
     */
    public void setMemo(java.lang.String newMemo) {
	this.memo = newMemo;
    }

    /**
     * ���� rowno��Getter����.���������ӿ���̖ ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getRowno() {
	return rowno;
    }

    /**
     * ����rowno��Setter����.���������ӿ���̖ ��������:2018/9/7
     * 
     * @param newRowno
     *            java.lang.String
     */
    public void setRowno(java.lang.String newRowno) {
	this.rowno = newRowno;
    }

    /**
     * ���� dr��Getter����.��������dr ��������:2018/9/7
     * 
     * @return java.lang.Integer
     */
    public java.lang.Integer getDr() {
	return dr;
    }

    /**
     * ����dr��Setter����.��������dr ��������:2018/9/7
     * 
     * @param newDr
     *            java.lang.Integer
     */
    public void setDr(java.lang.Integer newDr) {
	this.dr = newDr;
    }

    /**
     * ���� ts��Getter����.��������ts ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getTs() {
	return ts;
    }

    /**
     * ����ts��Setter����.��������ts ��������:2018/9/7
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
     * ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getParentPKFieldName() {
	return "pk_segrule";
    }

    /**
     * <p>
     * ȡ�ñ����I.
     * <p>
     * ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPKFieldName() {

	return "pk_segruleterm";
    }

    /**
     * <p>
     * ���ر����Q
     * <p>
     * ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getTableName() {
	return "hrta_segruleterm";
    }

    /**
     * <p>
     * ���ر����Q.
     * <p>
     * ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public static java.lang.String getDefaultTableName() {
	return "hrta_segruleterm";
    }

    /**
     * ����Ĭ�J��ʽ����������.
     * 
     * ��������:2018/9/7
     */
    public SegRuleTermVO() {
	super();
    }

    @nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.ta.overtime.SegRuleTermVO")
    public IVOMeta getMetaData() {
	return VOMetaFactory.getInstance().getVOMeta("overtime.segruleterm");

    }

}