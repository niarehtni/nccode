package nc.vo.wa.paydata;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> �˴���Ҫ�������๦�� </b>
 * <p>
 * �˴�����۵�������Ϣ
 * </p>
 * ��������:2018-5-3
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class LeaveRegDetailVO extends SuperVO {
	
	

	public LeaveRegDetailVO() {
		super();
	}

	public LeaveRegDetailVO(DaySalaryVO daySalaryVO) {
		super();
		this.effectivedate = daySalaryVO.getSalarydate();
		this.pk_psndoc = daySalaryVO.getPk_psndoc();
		this.pk_psnjob = daySalaryVO.getPk_psnjob();
		this.pk_hrorg = daySalaryVO.getPk_hrorg();
	}

	/**
	 * ���I
	 */
	public String pk_leaveregdetail;
	/**
	 * ��Դ�Γ����I
	 */
	public String pk_leavereg_history;
	/**
	 * ��Ч����
	 */
	public UFLiteralDate effectivedate;
	/**
	 * ��н
	 */
	public UFDouble daysalary;
	/**
	 * ��н��
	 */
	public UFLiteralDate salarydate;
	/**
	 * �ݼ��_ʼ����
	 */
	public UFDateTime begindate;
	/**
	 * �ݼٽY������
	 */
	public UFDateTime enddate;
	/**
	 * �ݼٕr�L
	 */
	public UFDouble leavehour;
	/**
	 * �ۿ��M��
	 */
	public UFDouble leavecharge;
	/**
	 * �ۿ��M��
	 */
	public Integer rate;
	/**
	 * ����e
	 */
	public String pk_leavetype;
	/**
	 * ����ecopy
	 */
	public String pk_leavetypecopy;
	/**
	 * �Ƿ��N��
	 */
	public UFBoolean isleaveoff;
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
	 * �ϲ㵥������
	 */
	public String pk_daysalary;
	/**
	 * ʱ���
	 */
	public UFDateTime ts;
	
	

	public UFLiteralDate getSalarydate() {
		return salarydate;
	}

	public void setSalarydate(UFLiteralDate salarydate) {
		this.salarydate = salarydate;
	}

	/**
	 * ���� pk_leaveregdetail��Getter����.�����������I ��������:2018-5-3
	 * 
	 * @return java.lang.String
	 */
	public String getPk_leaveregdetail() {
		return this.pk_leaveregdetail;
	}

	/**
	 * ����pk_leaveregdetail��Setter����.�����������I ��������:2018-5-3
	 * 
	 * @param newPk_leaveregdetail
	 *            java.lang.String
	 */
	public void setPk_leaveregdetail(String pk_leaveregdetail) {
		this.pk_leaveregdetail = pk_leaveregdetail;
	}

	/**
	 * ���� pk_leavereg_history��Getter����.����������Դ�Γ����I ��������:2018-5-3
	 * 
	 * @return java.lang.String
	 */
	public String getPk_leavereg_history() {
		return this.pk_leavereg_history;
	}

	/**
	 * ����pk_leavereg_history��Setter����.����������Դ�Γ����I ��������:2018-5-3
	 * 
	 * @param newPk_leavereg_history
	 *            java.lang.String
	 */
	public void setPk_leavereg_history(String pk_leavereg_history) {
		this.pk_leavereg_history = pk_leavereg_history;
	}

	/**
	 * ���� effectivedate��Getter����.����������Ч���� ��������:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public UFLiteralDate getEffectivedate() {
		return this.effectivedate;
	}

	/**
	 * ����effectivedate��Setter����.����������Ч���� ��������:2018-5-3
	 * 
	 * @param newEffectivedate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setEffectivedate(UFLiteralDate effectivedate) {
		this.effectivedate = effectivedate;
	}

	/**
	 * ���� daysalary��Getter����.����������н ��������:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getDaysalary() {
		return this.daysalary;
	}

	/**
	 * ����daysalary��Setter����.����������н ��������:2018-5-3
	 * 
	 * @param newDaysalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDaysalary(UFDouble daysalary) {
		this.daysalary = daysalary;
	}

	/**
	 * ���� begindate��Getter����.���������ݼ��_ʼ���� ��������:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getBegindate() {
		return this.begindate;
	}

	/**
	 * ����begindate��Setter����.���������ݼ��_ʼ���� ��������:2018-5-3
	 * 
	 * @param newBegindate
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setBegindate(UFDateTime begindate) {
		this.begindate = begindate;
	}

	/**
	 * ���� enddate��Getter����.���������ݼٽY������ ��������:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getEnddate() {
		return this.enddate;
	}

	/**
	 * ����enddate��Setter����.���������ݼٽY������ ��������:2018-5-3
	 * 
	 * @param newEnddate
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setEnddate(UFDateTime enddate) {
		this.enddate = enddate;
	}

	/**
	 * ���� leavehour��Getter����.���������ݼٕr�L ��������:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLeavehour() {
		return this.leavehour;
	}

	/**
	 * ����leavehour��Setter����.���������ݼٕr�L ��������:2018-5-3
	 * 
	 * @param newLeavehour
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLeavehour(UFDouble leavehour) {
		this.leavehour = leavehour;
	}

	/**
	 * ���� leavecharge��Getter����.���������ۿ��M�� ��������:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLeavecharge() {
		return this.leavecharge;
	}

	/**
	 * ����leavecharge��Setter����.���������ۿ��M�� ��������:2018-5-3
	 * 
	 * @param newLeavecharge
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLeavecharge(UFDouble leavecharge) {
		this.leavecharge = leavecharge;
	}

	/**
	 * ���� rate��Getter����.���������ۿ��M�� ��������:2018-5-3
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getRate() {
		return this.rate;
	}

	/**
	 * ����rate��Setter����.���������ۿ��M�� ��������:2018-5-3
	 * 
	 * @param newRate
	 *            java.lang.Integer
	 */
	public void setRate(Integer rate) {
		this.rate = rate;
	}

	/**
	 * ���� pk_leavetype��Getter����.������������e ��������:2018-5-3
	 * 
	 * @return java.lang.String
	 */
	public String getPk_leavetype() {
		return this.pk_leavetype;
	}

	/**
	 * ����pk_leavetype��Setter����.������������e ��������:2018-5-3
	 * 
	 * @param newPk_leavetype
	 *            java.lang.String
	 */
	public void setPk_leavetype(String pk_leavetype) {
		this.pk_leavetype = pk_leavetype;
	}

	

	public String getPk_leavetypecopy() {
		return pk_leavetypecopy;
	}

	public void setPk_leavetypecopy(String pk_leavetypecopy) {
		this.pk_leavetypecopy = pk_leavetypecopy;
	}

	/**
	 * ���� isleaveoff��Getter����.���������Ƿ��N�� ��������:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getIsleaveoff() {
		return this.isleaveoff;
	}

	/**
	 * ����isleaveoff��Setter����.���������Ƿ��N�� ��������:2018-5-3
	 * 
	 * @param newIsleaveoff
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIsleaveoff(UFBoolean isleaveoff) {
		this.isleaveoff = isleaveoff;
	}

	/**
	 * ���� pk_psndoc��Getter����.����������Ա�������� ��������:2018-5-3
	 * 
	 * @return nc.vo.hi.psndoc.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * ����pk_psndoc��Setter����.����������Ա�������� ��������:2018-5-3
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.hi.psndoc.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * ���� pk_psnjob��Getter����.����������Ա��ְ���� ��������:2018-5-3
	 * 
	 * @return nc.vo.hi.psndoc.PsnJobVO
	 */
	public String getPk_psnjob() {
		return this.pk_psnjob;
	}

	/**
	 * ����pk_psnjob��Setter����.����������Ա��ְ���� ��������:2018-5-3
	 * 
	 * @param newPk_psnjob
	 *            nc.vo.hi.psndoc.PsnJobVO
	 */
	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	/**
	 * ���� pk_hrorg��Getter����.�������������YԴ�M�� ��������:2018-5-3
	 * 
	 * @return nc.vo.org.HROrgVO
	 */
	public String getPk_hrorg() {
		return this.pk_hrorg;
	}

	/**
	 * ����pk_hrorg��Setter����.�������������YԴ�M�� ��������:2018-5-3
	 * 
	 * @param newPk_hrorg
	 *            nc.vo.org.HROrgVO
	 */
	public void setPk_hrorg(String pk_hrorg) {
		this.pk_hrorg = pk_hrorg;
	}

	/**
	 * ���� �����ϲ�������Getter����.���������ϲ����� ��������:2018-5-3
	 * 
	 * @return String
	 */
	public String getPk_daysalary() {
		return this.pk_daysalary;
	}

	/**
	 * ���������ϲ�������Setter����.���������ϲ����� ��������:2018-5-3
	 * 
	 * @param newPk_daysalary
	 *            String
	 */
	public void setPk_daysalary(String pk_daysalary) {
		this.pk_daysalary = pk_daysalary;
	}

	/**
	 * ���� ����ʱ�����Getter����.��������ʱ��� ��������:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * ��������ʱ�����Setter����.��������ʱ��� ��������:2018-5-3
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrwa.LeaveRegDetailVO");
	}
}
