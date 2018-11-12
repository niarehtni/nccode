package nc.vo.hrwa.wadaysalary;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> �˴���Ҫ�������๦�� </b>
 * <p>
 * �˴������۵�������Ϣ
 * </p>
 * ��������:2018-9-12
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class DaySalaryVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2688504682185934163L;
	/**
	 * ��н����
	 */
	public String pk_daysalary;
	/**
	 * н�ʷ�������
	 */
	public String pk_wa_class;
	/**
	 * н����Ŀ
	 */
	public String pk_wa_item;
	/**
	 * н�ʱ䶯���
	 */
	public String pk_psndoc_sub;
	/**
	 * н�ʱ䶯���ʱ���
	 */
	public UFDateTime wadocts;
	/**
	 * ��Ա��������
	 */
	public String pk_psndoc;
	/**
	 * ��Ա��ְ����
	 */
	public String pk_psnjob;
	/**
	 * �����YԴ�M��
	 */
	public String pk_hrorg;
	/**
	 * н������
	 */
	public UFLiteralDate salarydate;
	/**
	 * н����
	 */
	public Integer cyear;
	/**
	 * н����
	 */
	public Integer cperiod;
	/**
	 * ��������н
	 */
	public UFDouble daysalary;
	/**
	 * ������ʱн
	 */
	public UFDouble hoursalary;
	/**
	 * ʱ���
	 */
	public UFDateTime ts;

	/**
	 * ���� pk_daysalary��Getter����.����������н���� ��������:2018-9-12
	 * 
	 * @return java.lang.String
	 */
	public String getPk_daysalary() {
		return this.pk_daysalary;
	}

	/**
	 * ����pk_daysalary��Setter����.����������н���� ��������:2018-9-12
	 * 
	 * @param newPk_daysalary
	 *            java.lang.String
	 */
	public void setPk_daysalary(String pk_daysalary) {
		this.pk_daysalary = pk_daysalary;
	}

	/**
	 * ���� pk_wa_item��Getter����.��������н����Ŀ ��������:2018-9-12
	 * 
	 * @return java.lang.String
	 */
	public String getPk_wa_item() {
		return this.pk_wa_item;
	}

	/**
	 * ����pk_wa_item��Setter����.��������н����Ŀ ��������:2018-9-12
	 * 
	 * @param newPk_wa_item
	 *            java.lang.String
	 */
	public void setPk_wa_item(String pk_wa_item) {
		this.pk_wa_item = pk_wa_item;
	}

	/**
	 * ���� pk_psndoc_sub��Getter����.��������н�ʱ䶯��� ��������:2018-9-12
	 * 
	 * @return java.lang.String
	 */
	public String getPk_psndoc_sub() {
		return this.pk_psndoc_sub;
	}

	/**
	 * ����pk_psndoc_sub��Setter����.��������н�ʱ䶯��� ��������:2018-9-12
	 * 
	 * @param newPk_psndoc_sub
	 *            java.lang.String
	 */
	public void setPk_psndoc_sub(String pk_psndoc_sub) {
		this.pk_psndoc_sub = pk_psndoc_sub;
	}

	/**
	 * ���� pk_psndoc��Getter����.����������Ա�������� ��������:2018-9-12
	 * 
	 * @return nc.vo.hi.psndoc.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * ����pk_psndoc��Setter����.����������Ա�������� ��������:2018-9-12
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.hi.psndoc.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * ���� pk_psnjob��Getter����.����������Ա��ְ���� ��������:2018-9-12
	 * 
	 * @return nc.vo.hi.psndoc.PsnJobVO
	 */
	public String getPk_psnjob() {
		return this.pk_psnjob;
	}

	/**
	 * ����pk_psnjob��Setter����.����������Ա��ְ���� ��������:2018-9-12
	 * 
	 * @param newPk_psnjob
	 *            nc.vo.hi.psndoc.PsnJobVO
	 */
	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	/**
	 * ���� pk_hrorg��Getter����.�������������YԴ�M�� ��������:2018-9-12
	 * 
	 * @return nc.vo.org.HROrgVO
	 */
	public String getPk_hrorg() {
		return this.pk_hrorg;
	}

	/**
	 * ����pk_hrorg��Setter����.�������������YԴ�M�� ��������:2018-9-12
	 * 
	 * @param newPk_hrorg
	 *            nc.vo.org.HROrgVO
	 */
	public void setPk_hrorg(String pk_hrorg) {
		this.pk_hrorg = pk_hrorg;
	}

	/**
	 * ���� salarydate��Getter����.��������н������ ��������:2018-9-12
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public UFLiteralDate getSalarydate() {
		return this.salarydate;
	}

	/**
	 * ����salarydate��Setter����.��������н������ ��������:2018-9-12
	 * 
	 * @param newSalarydate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setSalarydate(UFLiteralDate salarydate) {
		this.salarydate = salarydate;
	}

	/**
	 * ���� cyear��Getter����.��������н���� ��������:2018-9-12
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getCyear() {
		return this.cyear;
	}

	/**
	 * ����cyear��Setter����.��������н���� ��������:2018-9-12
	 * 
	 * @param newCyear
	 *            java.lang.Integer
	 */
	public void setCyear(Integer cyear) {
		this.cyear = cyear;
	}

	/**
	 * ���� cperiod��Getter����.��������н���� ��������:2018-9-12
	 * 
	 * @return java.lang.String
	 */
	public Integer getCperiod() {
		return this.cperiod;
	}

	/**
	 * ����cperiod��Setter����.��������н���� ��������:2018-9-12
	 * 
	 * @param newCperiod
	 *            java.lang.String
	 */
	public void setCperiod(Integer cperiod) {
		this.cperiod = cperiod;
	}

	/**
	 * ���� daysalary��Getter����.����������������н ��������:2018-9-12
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getDaysalary() {
		return this.daysalary;
	}

	/**
	 * ����daysalary��Setter����.����������������н ��������:2018-9-12
	 * 
	 * @param newDaysalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDaysalary(UFDouble daysalary) {
		this.daysalary = daysalary;
	}

	/**
	 * ���� hoursalary��Getter����.��������������ʱн ��������:2018-9-12
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHoursalary() {
		return this.hoursalary;
	}

	/**
	 * ����hoursalary��Setter����.��������������ʱн ��������:2018-9-12
	 * 
	 * @param newHoursalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHoursalary(UFDouble hoursalary) {
		this.hoursalary = hoursalary;
	}
	
	

	public String getPk_wa_class() {
		return pk_wa_class;
	}

	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}

	public UFDateTime getWadocts() {
		return wadocts;
	}

	public void setWadocts(UFDateTime wadocts) {
		this.wadocts = wadocts;
	}

	/**
	 * ���� ����ʱ�����Getter����.��������ʱ��� ��������:2018-9-12
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * ��������ʱ�����Setter����.��������ʱ��� ��������:2018-9-12
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