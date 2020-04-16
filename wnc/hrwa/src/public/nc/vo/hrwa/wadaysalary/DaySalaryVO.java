package nc.vo.hrwa.wadaysalary;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> ��̎��Ҫ��������� </b>
 * <p>
 * ��̎����۵�������Ϣ
 * </p>
 * ��������:2019/1/28
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class DaySalaryVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8377625563019588778L;
	/**
	 * ��н���I
	 */
	public String pk_daysalary;
	/**
	 * �����YԴ�M��
	 */
	public String pk_hrorg;
	/**
	 * н�Y����
	 */
	public String pk_wa_class;
	/**
	 * н�Y�Ŀ
	 */
	public String pk_wa_item;
	/**
	 * н�Y׃����r
	 */
	public String pk_psndoc_sub;
	/**
	 * н�Y׃����r�r�g��
	 */
	public UFDateTime wadocts;
	/**
	 * �ˆT�������I
	 */
	public String pk_psndoc;
	/**
	 * �ˆT�����I
	 */
	public String pk_psnjob;
	/**
	 * н�Y����
	 */
	public UFLiteralDate salarydate;
	/**
	 * н�Y��
	 */
	public Integer cyear;
	/**
	 * н�Y��
	 */
	public Integer cperiod;
	/**
	 * ��н
	 */
	public UFDouble daysalary;
	/**
	 * �rн
	 */
	public UFDouble hoursalary;
	/**
	 * н�Y�Ŀ�ֽM
	 */
	public String pk_group_item;
	/**
	 * н�Y�Ŀ�ֽM�r�g��
	 */
	public UFDateTime groupitemts;
	/**
	 * �Ƿ���
	 */
	// ������н�Ѿ��ϲ�����������н
	@Deprecated
	public UFBoolean isattend;
	/**
	 * �Ƿ�۶�
	 */
	public UFBoolean taxflag;
	/**
	 * �r�g��
	 */
	public UFDateTime ts;

	/**
	 * hashֵ
	 */
	public Integer hashKey;

	public Integer getHashKey() {
		return hashKey;
	}

	public void setHashKey(Integer hashKey) {
		this.hashKey = hashKey;
	}

	/**
	 * ���� pk_daysalary��Getter����.����������н���I ��������:2019/1/28
	 * 
	 * @return java.lang.String
	 */
	public String getPk_daysalary() {
		return this.pk_daysalary;
	}

	/**
	 * ����pk_daysalary��Setter����.����������н���I ��������:2019/1/28
	 * 
	 * @param newPk_daysalary
	 *            java.lang.String
	 */
	public void setPk_daysalary(String pk_daysalary) {
		this.pk_daysalary = pk_daysalary;
	}

	/**
	 * ���� pk_hrorg��Getter����.�������������YԴ�M�� ��������:2019/1/28
	 * 
	 * @return nc.vo.org.HROrgVO
	 */
	public String getPk_hrorg() {
		return this.pk_hrorg;
	}

	/**
	 * ����pk_hrorg��Setter����.�������������YԴ�M�� ��������:2019/1/28
	 * 
	 * @param newPk_hrorg
	 *            nc.vo.org.HROrgVO
	 */
	public void setPk_hrorg(String pk_hrorg) {
		this.pk_hrorg = pk_hrorg;
	}

	/**
	 * ���� pk_wa_class��Getter����.��������н�Y���� ��������:2019/1/28
	 * 
	 * @return java.lang.String
	 */
	public String getPk_wa_class() {
		return this.pk_wa_class;
	}

	/**
	 * ����pk_wa_class��Setter����.��������н�Y���� ��������:2019/1/28
	 * 
	 * @param newPk_wa_class
	 *            java.lang.String
	 */
	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}

	/**
	 * ���� pk_wa_item��Getter����.��������н�Y�Ŀ ��������:2019/1/28
	 * 
	 * @return java.lang.String
	 */
	public String getPk_wa_item() {
		return this.pk_wa_item;
	}

	/**
	 * ����pk_wa_item��Setter����.��������н�Y�Ŀ ��������:2019/1/28
	 * 
	 * @param newPk_wa_item
	 *            java.lang.String
	 */
	public void setPk_wa_item(String pk_wa_item) {
		this.pk_wa_item = pk_wa_item;
	}

	/**
	 * ���� pk_psndoc_sub��Getter����.��������н�Y׃����r ��������:2019/1/28
	 * 
	 * @return java.lang.String
	 */
	public String getPk_psndoc_sub() {
		return this.pk_psndoc_sub;
	}

	/**
	 * ����pk_psndoc_sub��Setter����.��������н�Y׃����r ��������:2019/1/28
	 * 
	 * @param newPk_psndoc_sub
	 *            java.lang.String
	 */
	public void setPk_psndoc_sub(String pk_psndoc_sub) {
		this.pk_psndoc_sub = pk_psndoc_sub;
	}

	/**
	 * ���� wadocts��Getter����.��������н�Y׃����r�r�g�� ��������:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getWadocts() {
		return this.wadocts;
	}

	/**
	 * ����wadocts��Setter����.��������н�Y׃����r�r�g�� ��������:2019/1/28
	 * 
	 * @param newWadocts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setWadocts(UFDateTime wadocts) {
		this.wadocts = wadocts;
	}

	/**
	 * ���� pk_psndoc��Getter����.���������ˆT�������I ��������:2019/1/28
	 * 
	 * @return nc.vo.hi.psndoc.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * ����pk_psndoc��Setter����.���������ˆT�������I ��������:2019/1/28
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.hi.psndoc.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * ���� pk_psnjob��Getter����.���������ˆT�����I ��������:2019/1/28
	 * 
	 * @return nc.vo.hi.psndoc.PsnJobVO
	 */
	public String getPk_psnjob() {
		return this.pk_psnjob;
	}

	/**
	 * ����pk_psnjob��Setter����.���������ˆT�����I ��������:2019/1/28
	 * 
	 * @param newPk_psnjob
	 *            nc.vo.hi.psndoc.PsnJobVO
	 */
	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	/**
	 * ���� salarydate��Getter����.��������н�Y���� ��������:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public UFLiteralDate getSalarydate() {
		return this.salarydate;
	}

	/**
	 * ����salarydate��Setter����.��������н�Y���� ��������:2019/1/28
	 * 
	 * @param newSalarydate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setSalarydate(UFLiteralDate salarydate) {
		this.salarydate = salarydate;
	}

	/**
	 * ���� cyear��Getter����.��������н�Y�� ��������:2019/1/28
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getCyear() {
		return this.cyear;
	}

	/**
	 * ����cyear��Setter����.��������н�Y�� ��������:2019/1/28
	 * 
	 * @param newCyear
	 *            java.lang.Integer
	 */
	public void setCyear(Integer cyear) {
		this.cyear = cyear;
	}

	/**
	 * ���� cperiod��Getter����.��������н�Y�� ��������:2019/1/28
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getCperiod() {
		return this.cperiod;
	}

	/**
	 * ����cperiod��Setter����.��������н�Y�� ��������:2019/1/28
	 * 
	 * @param newCperiod
	 *            java.lang.Integer
	 */
	public void setCperiod(Integer cperiod) {
		this.cperiod = cperiod;
	}

	/**
	 * ���� daysalary��Getter����.����������н ��������:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getDaysalary() {
		return this.daysalary;
	}

	/**
	 * ����daysalary��Setter����.����������н ��������:2019/1/28
	 * 
	 * @param newDaysalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDaysalary(UFDouble daysalary) {
		this.daysalary = daysalary;
	}

	/**
	 * ���� hoursalary��Getter����.���������rн ��������:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHoursalary() {
		return this.hoursalary;
	}

	/**
	 * ����hoursalary��Setter����.���������rн ��������:2019/1/28
	 * 
	 * @param newHoursalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHoursalary(UFDouble hoursalary) {
		this.hoursalary = hoursalary;
	}

	/**
	 * ���� pk_group_item��Getter����.��������н�Y�Ŀ�ֽM ��������:2019/1/28
	 * 
	 * @return java.lang.String
	 */
	public String getPk_group_item() {
		return this.pk_group_item;
	}

	/**
	 * ����pk_group_item��Setter����.��������н�Y�Ŀ�ֽM ��������:2019/1/28
	 * 
	 * @param newPk_group_item
	 *            java.lang.String
	 */
	public void setPk_group_item(String pk_group_item) {
		this.pk_group_item = pk_group_item;
	}

	/**
	 * ���� groupitemts��Getter����.��������н�Y�Ŀ�ֽM�r�g�� ��������:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getGroupitemts() {
		return this.groupitemts;
	}

	/**
	 * ����groupitemts��Setter����.��������н�Y�Ŀ�ֽM�r�g�� ��������:2019/1/28
	 * 
	 * @param newGroupitemts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setGroupitemts(UFDateTime groupitemts) {
		this.groupitemts = groupitemts;
	}

	/**
	 * ���� isattend��Getter����.���������Ƿ��� ��������:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 * @deprecated tank ���� ������н�Ѿ��Ͷ�������н�ϲ�
	 */
	@Deprecated
	public UFBoolean getIsattend() {
		return this.isattend;
	}

	/**
	 * ����isattend��Setter����.���������Ƿ��� ��������:2019/1/28
	 * 
	 * @param newIsattend
	 *            nc.vo.pub.lang.UFBoolean
	 * @deprecated tank ���� ������н�Ѿ��Ͷ�������н�ϲ�
	 */
	@Deprecated
	public void setIsattend(UFBoolean isattend) {
		this.isattend = isattend;
	}

	/**
	 * ���� taxflag��Getter����.���������Ƿ�۶� ��������:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getTaxflag() {
		return this.taxflag;
	}

	/**
	 * ����taxflag��Setter����.���������Ƿ�۶� ��������:2019/1/28
	 * 
	 * @param newTaxflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setTaxflag(UFBoolean taxflag) {
		this.taxflag = taxflag;
	}

	/**
	 * ���� ���ɕr�g����Getter����.���������r�g�� ��������:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * �������ɕr�g����Setter����.���������r�g�� ��������:2019/1/28
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrwa.DaySalaryVO");
	}
}
