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
 * @version v1.0
 * @�������� �걨��ϸ�����ܵ����ӱ�VO
 * 
 */
public class CIncomeTaxVO extends SuperVO {
	/**
	 * �걨��ϸ������
	 */
	public String pk_incometaxdetail;
	/**
	 * Ա����������˰�걨����
	 */
	public String pk_incometax;
	/**
	 * Ա�����
	 */
	public String code;
	/**
	 * Ա������
	 */
	public String pk_psndoc;
	/**
	 * ���֤��
	 */
	public String id;
	/**
	 * �ڼ�
	 */
	public String cperiod;
	/**
	 * ���
	 */
	public String cyear;
	/**
	 * н���ڼ�
	 */
	public String cyearperiod;
	/**
	 * н�ʷ���
	 */
	public String pk_wa_class;
	/**
	 * �����ܶ�
	 */
	public UFDouble taxbase;
	/**
	 * �۽�˰��
	 */
	public UFDouble cacu_value;
	/**
	 * ��������
	 */
	public UFDouble netincome;
	/**
	 * Ա��������
	 */
	public UFDouble pickedup;
	/**
	 * �Ƿ��걨
	 */
	public UFBoolean isdeclare;
	/**
	 * ͳһ���
	 */
	public String unifiednumber;
	/**
	 * �걨ƾ����ʽ
	 */
	public String declaretype;
	/**
	 * �Ƿ��⼮Ա�������걨
	 */
	public UFBoolean isforeignmonthdec;
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
	 * ��������
	 */
	public UFDate billdate;
	/**
	 * н�����ݱ�����
	 */
	public String pk_wa_data;
	/**
	 * ��������
	 */
	public UFDate cpaydate;
	/**
	 * ���ű�־
	 */
	public UFBoolean payoffflag;
	/**
	 * ������Դ��֯
	 */
	public String pk_hrorg;
	/**
	 * �ϲ㵥������
	 */
	public String pk_sumincometax;
	/**
	 * ʱ���
	 */
	public UFDateTime ts;

	/**
	 * �Ƿ��⼮Ա���뾳�걨
	 */
	public UFBoolean isforeigndeprdec;

	public UFBoolean getIsforeigndeprdec() {
		return isforeigndeprdec;
	}

	public void setIsforeigndeprdec(UFBoolean isforeigndeprdec) {
		this.isforeigndeprdec = isforeigndeprdec;
	}

	public String getPk_incometaxdetail() {
		return pk_incometaxdetail;
	}

	public void setPk_incometaxdetail(String pk_incometaxdetail) {
		this.pk_incometaxdetail = pk_incometaxdetail;
	}

	/**
	 * ���� pk_incometax��Getter����.��������Ա����������˰�걨���� ��������:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getPk_incometax() {
		return this.pk_incometax;
	}

	/**
	 * ����pk_incometax��Setter����.��������Ա����������˰�걨���� ��������:2018-1-24
	 * 
	 * @param newPk_incometax
	 *            java.lang.String
	 */
	public void setPk_incometax(String pk_incometax) {
		this.pk_incometax = pk_incometax;
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
	 * ���� id��Getter����.�����������֤�� ��������:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * ����id��Setter����.�����������֤�� ��������:2018-1-24
	 * 
	 * @param newId
	 *            java.lang.String
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * ���� cperiod��Getter����.���������ڼ� ��������:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getCperiod() {
		return this.cperiod;
	}

	/**
	 * ����cperiod��Setter����.���������ڼ� ��������:2018-1-24
	 * 
	 * @param newCperiod
	 *            java.lang.String
	 */
	public void setCperiod(String cperiod) {
		this.cperiod = cperiod;
	}

	/**
	 * ���� cyear��Getter����.����������� ��������:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getCyear() {
		return this.cyear;
	}

	/**
	 * ����cyear��Setter����.����������� ��������:2018-1-24
	 * 
	 * @param newCyear
	 *            java.lang.String
	 */
	public void setCyear(String cyear) {
		this.cyear = cyear;
	}

	/**
	 * ���� cyearperiod��Getter����.��������н���ڼ� ��������:2018-1-24
	 * 
	 * @return nc.vo.wa.period.PeriodVO
	 */
	public String getCyearperiod() {
		return this.cyearperiod;
	}

	/**
	 * ����cyearperiod��Setter����.��������н���ڼ� ��������:2018-1-24
	 * 
	 * @param newCyearperiod
	 *            nc.vo.wa.period.PeriodVO
	 */
	public void setCyearperiod(String cyearperiod) {
		this.cyearperiod = cyearperiod;
	}

	/**
	 * ���� pk_wa_class��Getter����.��������н�ʷ��� ��������:2018-1-24
	 * 
	 * @return nc.vo.wa.category.WaClassVO
	 */
	public String getPk_wa_class() {
		return this.pk_wa_class;
	}

	/**
	 * ����pk_wa_class��Setter����.��������н�ʷ��� ��������:2018-1-24
	 * 
	 * @param newPk_wa_class
	 *            nc.vo.wa.category.WaClassVO
	 */
	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
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
	 * ���� unifiednumber��Getter����.��������ͳһ��� ��������:2018-1-24
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public String getUnifiednumber() {
		return this.unifiednumber;
	}

	/**
	 * ����unifiednumber��Setter����.��������ͳһ��� ��������:2018-1-24
	 * 
	 * @param newUnifiednumber
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setUnifiednumber(String unifiednumber) {
		this.unifiednumber = unifiednumber;
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
	 * ���� isforeignmonthdec��Getter����.���������Ƿ��⼮Ա�������걨 ��������:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getIsforeignmonthdec() {
		return this.isforeignmonthdec;
	}

	/**
	 * ����isforeignmonthdec��Setter����.���������Ƿ��⼮Ա�������걨 ��������:2018-1-24
	 * 
	 * @param newIsforeignmonthdec
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIsforeignmonthdec(UFBoolean isforeignmonthdec) {
		this.isforeignmonthdec = isforeignmonthdec;
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
	 * ���� pk_wa_data��Getter����.��������н�����ݱ����� ��������:2018-1-24
	 * 
	 * @return nc.vo.wa.payslip.WaDataVO
	 */
	public String getPk_wa_data() {
		return this.pk_wa_data;
	}

	/**
	 * ����pk_wa_data��Setter����.��������н�����ݱ����� ��������:2018-1-24
	 * 
	 * @param newPk_wa_data
	 *            nc.vo.wa.payslip.WaDataVO
	 */
	public void setPk_wa_data(String pk_wa_data) {
		this.pk_wa_data = pk_wa_data;
	}

	/**
	 * ���� cpaydate��Getter����.���������������� ��������:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getCpaydate() {
		return this.cpaydate;
	}

	/**
	 * ����cpaydate��Setter����.���������������� ��������:2018-1-24
	 * 
	 * @param newCpaydate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setCpaydate(UFDate cpaydate) {
		this.cpaydate = cpaydate;
	}

	/**
	 * ���� payoffflag��Getter����.�����������ű�־ ��������:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getPayoffflag() {
		return this.payoffflag;
	}

	/**
	 * ����payoffflag��Setter����.�����������ű�־ ��������:2018-1-24
	 * 
	 * @param newPayoffflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setPayoffflag(UFBoolean payoffflag) {
		this.payoffflag = payoffflag;
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
	 * ���� �����ϲ�������Getter����.���������ϲ����� ��������:2018-1-24
	 * 
	 * @return String
	 */
	public String getPk_sumincometax() {
		return this.pk_sumincometax;
	}

	/**
	 * ���������ϲ�������Setter����.���������ϲ����� ��������:2018-1-24
	 * 
	 * @param newPk_sumincometax
	 *            String
	 */
	public void setPk_sumincometax(String pk_sumincometax) {
		this.pk_sumincometax = pk_sumincometax;
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
		return VOMetaFactory.getInstance().getVOMeta("hrwa.CIncomeTaxVO");
	}
}
