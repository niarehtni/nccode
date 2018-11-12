package nc.vo.hrwa.sumincometax;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * 
 * @author ward.wong
 * @date 20180126
 * @date 20180227 �����o�����~�{�������U���~�{�����T��������~�{���ֶ�
 * @�������� �걨��ϸ�����ܵ�������VO
 * 
 */
public class SumIncomeTaxVO extends SuperVO {

	/**
	 * �걨��ϸ����������
	 */
	public String pk_sumincometax;
	/**
	 * Ա�����
	 */
	public String code;
	/**
	 * Ա������
	 */
	public String pk_psndoc;
	/**
	 * ����֤��
	 */
	public String id;
	/**
	 * �걨ƾ����ʽ
	 */
	public String declaretype;
	/**
	 * ƾ�����ʽ
	 */
	public String granttype;
	/**
	 * �걨����
	 */
	public String declarenum;
	/**
	 * �ظ��걨ԭ��
	 */
	public String reason;
	/**
	 * ����������
	 */
	public String contactname;
	/**
	 * �����˵绰
	 */
	public String contacttel;
	/**
	 * �걨��λ�����ʼ�
	 */
	public String contactemail;
	/**
	 * ҵ�����
	 */
	public String businessno;
	/**
	 * ���ñ����
	 */
	public String costno;
	/**
	 * ��Ŀ�����
	 */
	public String projectno;
	/**
	 * ֤���
	 */
	public String idtypeno;
	/**
	 * ��ʼ�ڼ�
	 */
	public String beginperiod;
	/**
	 * �����ڼ�
	 */
	public String endperiod;
	/**
	 * �Ƿ��걨
	 */
	public UFBoolean isdeclare;
	/**
	 * �����ܶ�
	 */
	public UFDouble taxbase;
	/**
	 * �����ܶ����
	 */
	public UFDouble taxbaseadjust;
	/**
	 * �۽�˰��
	 */
	public UFDouble cacu_value;
	/**
	 * �۽�˰�����
	 */
	public UFDouble cacu_valueadjust;
	/**
	 * ��������
	 */
	public UFDouble netincome;
	/**
	 * Ա��������
	 */
	public UFDouble pickedup;
	/**
	 * Ա�����������
	 */
	public UFDouble pickedupadjust;
	/**
	 * ������Դ��֯
	 */
	public String pk_hrorg;
	/**
	 * ��������
	 */
	public UFDate billdate;
	/**
	 * ������
	 */
	public String creator;
	/**
	 * ����ʱ��
	 */
	public UFDateTime creationtime;
	/**
	 * �޸���
	 */
	public String modifier;
	/**
	 * �޸�ʱ��
	 */
	public UFDateTime modifiedtime;
	/**
	 * ����
	 */
	public String pk_group;
	/**
	 * ��֯
	 */
	public String pk_org;
	/**
	 * ͳһ���
	 */
	public String unifiednumber;
	/**
	 * ʱ���
	 */
	public UFDateTime ts;

	/**
	 * ���� pk_sumincometax��Getter����.���������걨��ϸ���������� ��������:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getPk_sumincometax() {
		return this.pk_sumincometax;
	}

	/**
	 * ����pk_sumincometax��Setter����.���������걨��ϸ���������� ��������:2018-1-24
	 * 
	 * @param newPk_sumincometax
	 *            java.lang.String
	 */
	public void setPk_sumincometax(String pk_sumincometax) {
		this.pk_sumincometax = pk_sumincometax;
	}

	/**
	 * ���� code��Getter����.��������Ա����� ��������:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * ����code��Setter����.��������Ա����� ��������:2018-1-24
	 * 
	 * @param newCode
	 *            java.lang.String
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * ���� pk_psndoc��Getter����.��������Ա������ ��������:2018-1-24
	 * 
	 * @return nc.vo.hi.psndoc.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * ����pk_psndoc��Setter����.��������Ա������ ��������:2018-1-24
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.hi.psndoc.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * ���� id��Getter����.������������֤�� ��������:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * ����id��Setter����.������������֤�� ��������:2018-1-24
	 * 
	 * @param newId
	 *            java.lang.String
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * ���� declaretype��Getter����.���������걨ƾ����ʽ ��������:2018-1-24
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public String getDeclaretype() {
		return this.declaretype;
	}

	/**
	 * ����declaretype��Setter����.���������걨ƾ����ʽ ��������:2018-1-24
	 * 
	 * @param newDeclaretype
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setDeclaretype(String declaretype) {
		this.declaretype = declaretype;
	}

	/**
	 * ���� granttype��Getter����.��������ƾ�����ʽ ��������:2018-1-25
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public String getGranttype() {
		return this.granttype;
	}

	/**
	 * ����granttype��Setter����.��������ƾ�����ʽ ��������:2018-1-25
	 * 
	 * @param newGranttype
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setGranttype(String granttype) {
		this.granttype = granttype;
	}

	/**
	 * ���� declarenum��Getter����.���������걨���� ��������:2018-1-25
	 * 
	 * @return java.lang.String
	 */
	public String getDeclarenum() {
		return this.declarenum;
	}

	/**
	 * ����declarenum��Setter����.���������걨���� ��������:2018-1-25
	 * 
	 * @param newDeclarenum
	 *            java.lang.String
	 */
	public void setDeclarenum(String declarenum) {
		this.declarenum = declarenum;
	}

	/**
	 * ���� reason��Getter����.���������ظ��걨ԭ�� ��������:2018-1-25
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public String getReason() {
		return this.reason;
	}

	/**
	 * ����reason��Setter����.���������ظ��걨ԭ�� ��������:2018-1-25
	 * 
	 * @param newReason
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * ���� contactname��Getter����.������������������ ��������:2018-1-25
	 * 
	 * @return java.lang.String
	 */
	public String getContactname() {
		return this.contactname;
	}

	/**
	 * ����contactname��Setter����.������������������ ��������:2018-1-25
	 * 
	 * @param newContactname
	 *            java.lang.String
	 */
	public void setContactname(String contactname) {
		this.contactname = contactname;
	}

	/**
	 * ���� contacttel��Getter����.�������������˵绰 ��������:2018-1-25
	 * 
	 * @return java.lang.String
	 */
	public String getContacttel() {
		return this.contacttel;
	}

	/**
	 * ����contacttel��Setter����.�������������˵绰 ��������:2018-1-25
	 * 
	 * @param newContacttel
	 *            java.lang.String
	 */
	public void setContacttel(String contacttel) {
		this.contacttel = contacttel;
	}

	/**
	 * ���� contactemail��Getter����.���������걨��λ�����ʼ� ��������:2018-1-25
	 * 
	 * @return java.lang.String
	 */
	public String getContactemail() {
		return this.contactemail;
	}

	/**
	 * ����contactemail��Setter����.���������걨��λ�����ʼ� ��������:2018-1-25
	 * 
	 * @param newContactemail
	 *            java.lang.String
	 */
	public void setContactemail(String contactemail) {
		this.contactemail = contactemail;
	}

	/**
	 * ���� businessno��Getter����.��������ҵ����� ��������:2018-1-24
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public String getBusinessno() {
		return this.businessno;
	}

	/**
	 * ����businessno��Setter����.��������ҵ����� ��������:2018-1-24
	 * 
	 * @param newBusinessno
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setBusinessno(String businessno) {
		this.businessno = businessno;
	}

	/**
	 * ���� costno��Getter����.�����������ñ���� ��������:2018-1-24
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public String getCostno() {
		return this.costno;
	}

	/**
	 * ����costno��Setter����.�����������ñ���� ��������:2018-1-24
	 * 
	 * @param newCostno
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setCostno(String costno) {
		this.costno = costno;
	}

	/**
	 * ���� projectno��Getter����.����������Ŀ����� ��������:2018-1-24
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public String getProjectno() {
		return this.projectno;
	}

	/**
	 * ����projectno��Setter����.����������Ŀ����� ��������:2018-1-24
	 * 
	 * @param newProjectno
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setProjectno(String projectno) {
		this.projectno = projectno;
	}

	/**
	 * ���� idtypeno��Getter����.��������֤��� ��������:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getIdtypeno() {
		return this.idtypeno;
	}

	/**
	 * ����idtypeno��Setter����.��������֤��� ��������:2018-1-24
	 * 
	 * @param newIdtypeno
	 *            java.lang.String
	 */
	public void setIdtypeno(String idtypeno) {
		this.idtypeno = idtypeno;
	}

	/**
	 * ���� beginperiod��Getter����.����������ʼ�ڼ� ��������:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getBeginperiod() {
		return this.beginperiod;
	}

	/**
	 * ����beginperiod��Setter����.����������ʼ�ڼ� ��������:2018-1-24
	 * 
	 * @param newBeginperiod
	 *            java.lang.String
	 */
	public void setBeginperiod(String beginperiod) {
		this.beginperiod = beginperiod;
	}

	/**
	 * ���� endperiod��Getter����.�������������ڼ� ��������:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getEndperiod() {
		return this.endperiod;
	}

	/**
	 * ����endperiod��Setter����.�������������ڼ� ��������:2018-1-24
	 * 
	 * @param newEndperiod
	 *            java.lang.String
	 */
	public void setEndperiod(String endperiod) {
		this.endperiod = endperiod;
	}

	/**
	 * ���� isdeclare��Getter����.���������Ƿ��걨 ��������:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getIsdeclare() {
		return this.isdeclare;
	}

	/**
	 * ����isdeclare��Setter����.���������Ƿ��걨 ��������:2018-1-24
	 * 
	 * @param newIsdeclare
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIsdeclare(UFBoolean isdeclare) {
		this.isdeclare = isdeclare;
	}

	/**
	 * ���� taxbase��Getter����.�������������ܶ� ��������:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getTaxbase() {
		return this.taxbase;
	}

	/**
	 * ����taxbase��Setter����.�������������ܶ� ��������:2018-1-24
	 * 
	 * @param newTaxbase
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setTaxbase(UFDouble taxbase) {
		this.taxbase = taxbase;
	}

	/**
	 * ���� cacu_value��Getter����.���������۽�˰�� ��������:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getCacu_value() {
		return this.cacu_value;
	}

	/**
	 * ����cacu_value��Setter����.���������۽�˰�� ��������:2018-1-24
	 * 
	 * @param newCacu_value
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setCacu_value(UFDouble cacu_value) {
		this.cacu_value = cacu_value;
	}

	/**
	 * ���� netincome��Getter����.���������������� ��������:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getNetincome() {
		return this.netincome;
	}

	/**
	 * ����netincome��Setter����.���������������� ��������:2018-1-24
	 * 
	 * @param newNetincome
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setNetincome(UFDouble netincome) {
		this.netincome = netincome;
	}

	/**
	 * ���� pickedup��Getter����.��������Ա�������� ��������:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getPickedup() {
		return this.pickedup;
	}

	/**
	 * ����pickedup��Setter����.��������Ա�������� ��������:2018-1-24
	 * 
	 * @param newPickedup
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setPickedup(UFDouble pickedup) {
		this.pickedup = pickedup;
	}

	/**
	 * ���� pk_hrorg��Getter����.��������������Դ��֯ ��������:2018-1-24
	 * 
	 * @return nc.vo.org.HROrgVO
	 */
	public String getPk_hrorg() {
		return this.pk_hrorg;
	}

	/**
	 * ����pk_hrorg��Setter����.��������������Դ��֯ ��������:2018-1-24
	 * 
	 * @param newPk_hrorg
	 *            nc.vo.org.HROrgVO
	 */
	public void setPk_hrorg(String pk_hrorg) {
		this.pk_hrorg = pk_hrorg;
	}

	/**
	 * ���� billdate��Getter����.���������������� ��������:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getBilldate() {
		return this.billdate;
	}

	/**
	 * ����billdate��Setter����.���������������� ��������:2018-1-24
	 * 
	 * @param newBilldate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setBilldate(UFDate billdate) {
		this.billdate = billdate;
	}

	/**
	 * ���� creator��Getter����.�������������� ��������:2018-1-24
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getCreator() {
		return this.creator;
	}

	/**
	 * ����creator��Setter����.�������������� ��������:2018-1-24
	 * 
	 * @param newCreator
	 *            nc.vo.sm.UserVO
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * ���� creationtime��Getter����.������������ʱ�� ��������:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * ����creationtime��Setter����.������������ʱ�� ��������:2018-1-24
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * ���� modifier��Getter����.���������޸��� ��������:2018-1-24
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getModifier() {
		return this.modifier;
	}

	/**
	 * ����modifier��Setter����.���������޸��� ��������:2018-1-24
	 * 
	 * @param newModifier
	 *            nc.vo.sm.UserVO
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * ���� modifiedtime��Getter����.���������޸�ʱ�� ��������:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * ����modifiedtime��Setter����.���������޸�ʱ�� ��������:2018-1-24
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * ���� pk_group��Getter����.������������ ��������:2018-1-24
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * ����pk_group��Setter����.������������ ��������:2018-1-24
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * ���� pk_org��Getter����.����������֯ ��������:2018-1-24
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * ����pk_org��Setter����.����������֯ ��������:2018-1-24
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getUnifiednumber() {
		return unifiednumber;
	}

	public void setUnifiednumber(String unifiednumber) {
		this.unifiednumber = unifiednumber;
	}

	public UFDouble getTaxbaseadjust() {
		return taxbaseadjust;
	}

	public void setTaxbaseadjust(UFDouble taxbaseadjust) {
		this.taxbaseadjust = taxbaseadjust;
	}

	public UFDouble getCacu_valueadjust() {
		return cacu_valueadjust;
	}

	public void setCacu_valueadjust(UFDouble cacu_valueadjust) {
		this.cacu_valueadjust = cacu_valueadjust;
	}

	public UFDouble getPickedupadjust() {
		return pickedupadjust;
	}

	public void setPickedupadjust(UFDouble pickedupadjust) {
		this.pickedupadjust = pickedupadjust;
	}

	/**
	 * ���� ����ʱ�����Getter����.��������ʱ��� ��������:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * ��������ʱ�����Setter����.��������ʱ��� ��������:2018-1-24
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrwa.SumIncomeTaxVO");
	}
}