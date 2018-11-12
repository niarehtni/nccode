package nc.vo.ta.overtime;

import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> ��̎��Ҫ��������� </b>
 * <p>
 * ��̎����������Ϣ
 * </p>
 * ��������:2018/9/7
 * 
 * @author
 * @version NCPrj ??
 */
public class SegRuleVO extends nc.vo.pub.SuperVO {

    private java.lang.String pk_segrule;
    private java.lang.String pk_group;
    private java.lang.String pk_org;
    private java.lang.String pk_org_v;
    private java.lang.String creator;
    private nc.vo.pub.lang.UFDateTime creationtime;
    private java.lang.String modifier;
    private nc.vo.pub.lang.UFDateTime modifiedtime;
    private java.lang.String code;
    private java.lang.String name = "";
    private java.lang.String name2;
    private java.lang.String name3;
    private java.lang.String name4;
    private java.lang.String name5;
    private java.lang.String name6;
    private java.lang.Integer datetype;
    private nc.vo.pub.lang.UFBoolean isdefault;
    private nc.vo.pub.lang.UFBoolean isenabled;
    private nc.vo.pub.lang.UFDouble additionaldays;
    private java.lang.String memo;
    private nc.vo.pub.lang.UFDate makedate;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;

    private nc.vo.ta.overtime.SegRuleTermVO[] pk_segruleterm;

    public static final String PK_SEGRULE = "pk_segrule";
    public static final String PK_GROUP = "pk_group";
    public static final String PK_ORG = "pk_org";
    public static final String PK_ORG_V = "pk_org_v";
    public static final String CREATOR = "creator";
    public static final String CREATIONTIME = "creationtime";
    public static final String MODIFIER = "modifier";
    public static final String MODIFIEDTIME = "modifiedtime";
    public static final String CODE = "code";
    public static final String NAME = "name";
    public static final String NAME2 = "name2";
    public static final String NAME3 = "name3";
    public static final String NAME4 = "name4";
    public static final String NAME5 = "name5";
    public static final String NAME6 = "name6";
    public static final String DATETYPE = "datetype";
    public static final String ISDEFAULT = "isdefault";
    public static final String ISENABLED = "isenabled";
    public static final String ADDITIONALDAYS = "additionaldays";
    public static final String MEMO = "memo";
    public static final String MAKEDATE = "makedate";

    /**
     * ���� pk_segrule��Getter����.���������Ӱ�ֶ��������I ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_segrule() {
	return pk_segrule;
    }

    /**
     * ����pk_segrule��Setter����.���������Ӱ�ֶ��������I ��������:2018/9/7
     * 
     * @param newPk_segrule
     *            java.lang.String
     */
    public void setPk_segrule(java.lang.String newPk_segrule) {
	this.pk_segrule = newPk_segrule;
    }

    /**
     * ���� pk_segruleterm��Getter����.���������Ӱ�ֶ�Ҏ�t ��������:2018/9/7
     * 
     * @return nc.vo.ta.overtime.SegRuleTermVO[]
     */
    public nc.vo.ta.overtime.SegRuleTermVO[] getPk_segruleterm() {
	return pk_segruleterm;
    }

    /**
     * ����pk_segruleterm��Setter����.���������Ӱ�ֶ�Ҏ�t ��������:2018/9/7
     * 
     * @param newPk_segruleterm
     *            nc.vo.ta.overtime.SegRuleTermVO[]
     */
    public void setPk_segruleterm(nc.vo.ta.overtime.SegRuleTermVO[] newPk_segruleterm) {
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
     * ���� code��Getter����.�����������a ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getCode() {
	return code;
    }

    /**
     * ����code��Setter����.�����������a ��������:2018/9/7
     * 
     * @param newCode
     *            java.lang.String
     */
    public void setCode(java.lang.String newCode) {
	this.code = newCode;
    }

    /**
     * ���� name��Getter����.��������null ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getName() {
	return name;
    }

    /**
     * ����name��Setter����.��������null ��������:2018/9/7
     * 
     * @param newName
     *            java.lang.String
     */
    public void setName(java.lang.String newName) {
	this.name = newName;
    }

    /**
     * ���� name2��Getter����.��������null ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getName2() {
	return name2;
    }

    /**
     * ����name2��Setter����.��������null ��������:2018/9/7
     * 
     * @param newName2
     *            java.lang.String
     */
    public void setName2(java.lang.String newName2) {
	this.name2 = newName2;
    }

    /**
     * ���� name3��Getter����.��������null ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getName3() {
	return name3;
    }

    /**
     * ����name3��Setter����.��������null ��������:2018/9/7
     * 
     * @param newName3
     *            java.lang.String
     */
    public void setName3(java.lang.String newName3) {
	this.name3 = newName3;
    }

    /**
     * ���� name4��Getter����.��������null ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getName4() {
	return name4;
    }

    /**
     * ����name4��Setter����.��������null ��������:2018/9/7
     * 
     * @param newName4
     *            java.lang.String
     */
    public void setName4(java.lang.String newName4) {
	this.name4 = newName4;
    }

    /**
     * ���� name5��Getter����.��������null ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getName5() {
	return name5;
    }

    /**
     * ����name5��Setter����.��������null ��������:2018/9/7
     * 
     * @param newName5
     *            java.lang.String
     */
    public void setName5(java.lang.String newName5) {
	this.name5 = newName5;
    }

    /**
     * ���� name6��Getter����.��������null ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getName6() {
	return name6;
    }

    /**
     * ����name6��Setter����.��������null ��������:2018/9/7
     * 
     * @param newName6
     *            java.lang.String
     */
    public void setName6(java.lang.String newName6) {
	this.name6 = newName6;
    }

    /**
     * ���� datetype��Getter����.���������Օ������ ��������:2018/9/7
     * 
     * @return java.lang.Integer
     */
    public java.lang.Integer getDatetype() {
	return datetype;
    }

    /**
     * ����datetype��Setter����.���������Օ������ ��������:2018/9/7
     * 
     * @param newDatetype
     *            java.lang.Integer
     */
    public void setDatetype(java.lang.Integer newDatetype) {
	this.datetype = newDatetype;
    }

    /**
     * ���� isdefault��Getter����.���������Ƿ�Ĭ�J ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFBoolean
     */
    public nc.vo.pub.lang.UFBoolean getIsdefault() {
	return isdefault;
    }

    /**
     * ����isdefault��Setter����.���������Ƿ�Ĭ�J ��������:2018/9/7
     * 
     * @param newIsdefault
     *            nc.vo.pub.lang.UFBoolean
     */
    public void setIsdefault(nc.vo.pub.lang.UFBoolean newIsdefault) {
	this.isdefault = newIsdefault;
    }

    /**
     * ���� isenabled��Getter����.���������Ƿ��� ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFBoolean
     */
    public nc.vo.pub.lang.UFBoolean getIsenabled() {
	return isenabled;
    }

    /**
     * ����isenabled��Setter����.���������Ƿ��� ��������:2018/9/7
     * 
     * @param newIsenabled
     *            nc.vo.pub.lang.UFBoolean
     */
    public void setIsenabled(nc.vo.pub.lang.UFBoolean newIsenabled) {
	this.isenabled = newIsenabled;
    }

    /**
     * ���� additionaldays��Getter����.������������a���씵 ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getAdditionaldays() {
	return additionaldays;
    }

    /**
     * ����additionaldays��Setter����.������������a���씵 ��������:2018/9/7
     * 
     * @param newAdditionaldays
     *            nc.vo.pub.lang.UFDouble
     */
    public void setAdditionaldays(nc.vo.pub.lang.UFDouble newAdditionaldays) {
	this.additionaldays = newAdditionaldays;
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
     * ���� makedate��Getter����.���������ӿچΓ����� ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDate
     */
    public nc.vo.pub.lang.UFDate getMakedate() {
	return makedate;
    }

    /**
     * ����makedate��Setter����.���������ӿچΓ����� ��������:2018/9/7
     * 
     * @param newMakedate
     *            nc.vo.pub.lang.UFDate
     */
    public void setMakedate(nc.vo.pub.lang.UFDate newMakedate) {
	this.makedate = newMakedate;
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
	return null;
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

	return "pk_segrule";
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
	return "hrta_segrule";
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
	return "hrta_segrule";
    }

    /**
     * ����Ĭ�J��ʽ����������.
     * 
     * ��������:2018/9/7
     */
    public SegRuleVO() {
	super();
    }

    @nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.ta.overtime.SegRuleVO")
    public IVOMeta getMetaData() {
	return VOMetaFactory.getInstance().getVOMeta("overtime.segrule");

    }

}
