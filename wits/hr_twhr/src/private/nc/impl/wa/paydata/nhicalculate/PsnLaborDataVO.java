package nc.impl.wa.paydata.nhicalculate;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class PsnLaborDataVO extends SuperVO {
	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 6893625326832903108L;

	private String pk_org; // ��ְ��֯
	private String pk_hrorg; // ������֯
	private String pk_psndoc; // ��Ա��ϢPK
	private UFDouble basicSalary = UFDouble.ZERO_DBL; // ����н��
	private int year; // ���
	private int month; // �·�
	private UFBoolean isMainMonth; // �Ƿ���������
	private UFBoolean ifCalculateLastMonth; // �Ƿ���������ͱ�

	private UFDate nhiBeginDate; // Ͷ����ʼ����
	private UFDate nhiEndDate; // Ͷ����������
	private UFDouble validLaborDays = UFDouble.ZERO_DBL; // �ͱ���Ч����
	private String disableType; // ���Ĳ��ϳ̶�
	private String nhiPsnType; // Ͷ�����ע��
	private String psnName; // Ա������
	private String psnCode; // Ա�����

	private UFDouble laborRange = UFDouble.ZERO_DBL; // �ͱ�����
	private UFDouble commonAccRate = UFDouble.ZERO_DBL; // ��ͨ�¹ʷ���
	private UFDouble commonAccRate_Psn = UFDouble.ZERO_DBL; // ��ͨ�¹ʸ��˳е�����
	private UFDouble commonAccAmount_Psn = UFDouble.ZERO_DBL; // ��ͨ�¹ʸ��˳е����
	private UFDouble commonAccRate_Org = UFDouble.ZERO_DBL; // ��ͨ�¹ʹ�˾�е�����
	private UFDouble commonAccAmount_Org = UFDouble.ZERO_DBL; // ��ͨ�¹ʹ�˾�е����
	private UFDouble occAccRate = UFDouble.ZERO_DBL; // ְ�ַ���
	private UFDouble occAccRate_Psn = UFDouble.ZERO_DBL; // ְ�ָ��˳е�����
	private UFDouble occAccAmount_Psn = UFDouble.ZERO_DBL; // ְ�ָ��˳е����
	private UFDouble occAccRate_Org = UFDouble.ZERO_DBL; // ְ�ֹ�˾�е�����
	private UFDouble occAccAmount_Org = UFDouble.ZERO_DBL; // ְ�ֹ�˾�е�����
	private UFDouble empInsRate = UFDouble.ZERO_DBL; // ��ҵ����
	private UFDouble empInsRate_Psn = UFDouble.ZERO_DBL; // ��ҵ���˳е�����
	private UFDouble empInsAmount_Psn = UFDouble.ZERO_DBL; // ��ҵ���˳е����
	private UFDouble empInsRate_Org = UFDouble.ZERO_DBL; // ��ҵ��˾�е�����
	private UFDouble empInsAmount_Org = UFDouble.ZERO_DBL; // ��ҵ��˾�е����
	private UFDouble laborInsAmount_Psn = UFDouble.ZERO_DBL; // �ͱ����˳е����
	private UFDouble laborInsAmount_Org = UFDouble.ZERO_DBL; // �ͱ���˾�е����

	private UFDouble retireRange = UFDouble.ZERO_DBL; // ���˼���
	private UFDouble retireWthRate_Psn = UFDouble.ZERO_DBL; // �����������
	private UFDouble retireWthRate_Org = UFDouble.ZERO_DBL; // ���˹������
	private UFDouble retireWthAmount_Psn = UFDouble.ZERO_DBL; // ����������
	private UFDouble retireWthAmount_Org = UFDouble.ZERO_DBL; // ���˹�����

	private UFBoolean ifLabor; // �Ƿ�Ͷ���ڱ�
	private UFBoolean ifRetire; // �Ƿ�Ͷ������
	private UFBoolean ifOldRetire; // �Ƿ�����f��
	private UFDouble laborDays = UFDouble.ZERO_DBL; // �A�O�ڱ��씵
	private UFDouble retireDays = UFDouble.ZERO_DBL; // �A�O�����씵
	private UFDouble repayFundRate = UFDouble.ZERO_DBL; // ���Y�|�������M��
	private UFDouble repayFundAmount = UFDouble.ZERO_DBL;// ���Y�|��������~

	private UFDate retireBegin; // ����Ͷ���_ʼ����
	private UFDate retireEnd; // ����Ͷ���Y������


	public UFDate getRetireBegin() {
		return retireBegin;
	}

	public void setRetireBegin(UFDate retireBegin) {
		this.retireBegin = retireBegin;
	}

	public UFDate getRetireEnd() {
		return retireEnd;
	}

	public void setRetireEnd(UFDate retireEnd) {
		this.retireEnd = retireEnd;
	}

	public UFBoolean getIfLabor() {
		return ifLabor;
	}

	public void setIfLabor(UFBoolean ifLabor) {
		this.ifLabor = ifLabor;
	}

	public UFBoolean getIfRetire() {
		return ifRetire;
	}

	public void setIfRetire(UFBoolean ifRetire) {
		this.ifRetire = ifRetire;
	}

	public UFDouble getLaborDays() {
		return laborDays;
	}

	public void setLaborDays(UFDouble laborDays) {
		this.laborDays = laborDays;
	}

	public UFDouble getRetireDays() {
		return retireDays;
	}

	public void setRetireDays(UFDouble retireDays) {
		this.retireDays = retireDays;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_hrorg() {
		return pk_hrorg;
	}

	public void setPk_hrorg(String pk_hrorg) {
		this.pk_hrorg = pk_hrorg;
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public UFDouble getBasicSalary() {
		if (basicSalary == null) {
			basicSalary = UFDouble.ZERO_DBL;
		}

		return basicSalary;
	}

	public void setBasicSalary(UFDouble basicSalary) {
		this.basicSalary = basicSalary;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public UFDate getNhiBeginDate() {
		return nhiBeginDate;
	}

	public void setNhiBeginDate(UFDate nhiBeginDate) {
		this.nhiBeginDate = nhiBeginDate;
	}

	public UFDate getNhiEndDate() {
		return nhiEndDate;
	}

	public void setNhiEndDate(UFDate nhiEndDate) {
		this.nhiEndDate = nhiEndDate;
	}

	public UFDouble getValidLaborDays() {
		if (validLaborDays == null) {
			validLaborDays = UFDouble.ZERO_DBL;
		}
		return validLaborDays;
	}

	public void setValidLaborDays(UFDouble validLaborDays) {
		this.validLaborDays = validLaborDays;
	}

	public String getDisableType() {
		return disableType;
	}

	public void setDisableType(String disableType) {
		this.disableType = disableType;
	}

	public String getNhiPsnType() {
		return nhiPsnType;
	}

	public void setNhiPsnType(String nhiPsnType) {
		this.nhiPsnType = nhiPsnType;
	}

	public String getPsnName() {
		return psnName;
	}

	public void setPsnName(String psnName) {
		this.psnName = psnName;
	}

	public String getPsnCode() {
		return psnCode;
	}

	public void setPsnCode(String psnCode) {
		this.psnCode = psnCode;
	}

	public UFDouble getLaborRange() {
		if (laborRange == null) {
			laborRange = UFDouble.ZERO_DBL;
		}
		return laborRange;
	}

	public void setLaborRange(UFDouble laborRange) {
		this.laborRange = laborRange;
	}

	public UFDouble getCommonAccRate() {
		if (commonAccRate == null) {
			commonAccRate = UFDouble.ZERO_DBL;
		}
		return commonAccRate;
	}

	public void setCommonAccRate(UFDouble commonAccRate) {
		this.commonAccRate = commonAccRate;
	}

	public UFDouble getCommonAccRate_Psn() {
		if (commonAccRate_Psn == null) {
			commonAccRate_Psn = UFDouble.ZERO_DBL;
		}
		return commonAccRate_Psn;
	}

	public void setCommonAccRate_Psn(UFDouble commonAccRate_Psn) {
		this.commonAccRate_Psn = commonAccRate_Psn;
	}

	public UFDouble getCommonAccAmount_Psn() {
		if (commonAccAmount_Psn == null) {
			commonAccAmount_Psn = UFDouble.ZERO_DBL;
		}
		return commonAccAmount_Psn;
	}

	public void setCommonAccAmount_Psn(UFDouble commonAccAmount_Psn) {
		this.commonAccAmount_Psn = commonAccAmount_Psn;
	}

	public UFDouble getCommonAccRate_Org() {
		if (commonAccRate_Org == null) {
			commonAccRate_Org = UFDouble.ZERO_DBL;
		}
		return commonAccRate_Org;
	}

	public void setCommonAccRate_Org(UFDouble commonAccRate_Org) {
		this.commonAccRate_Org = commonAccRate_Org;
	}

	public UFDouble getCommonAccAmount_Org() {
		if (commonAccAmount_Org == null) {
			commonAccAmount_Org = UFDouble.ZERO_DBL;
		}
		return commonAccAmount_Org;
	}

	public void setCommonAccAmount_Org(UFDouble commonAccAmount_Org) {
		this.commonAccAmount_Org = commonAccAmount_Org;
	}

	public UFDouble getOccAccRate() {
		if (occAccRate == null) {
			occAccRate = UFDouble.ZERO_DBL;
		}
		return occAccRate;
	}

	public void setOccAccRate(UFDouble occAccRate) {
		this.occAccRate = occAccRate;
	}

	public UFDouble getOccAccRate_Psn() {
		if (occAccRate_Psn == null) {
			occAccRate_Psn = UFDouble.ZERO_DBL;
		}
		return occAccRate_Psn;
	}

	public void setOccAccRate_Psn(UFDouble occAccRate_Psn) {
		this.occAccRate_Psn = occAccRate_Psn;
	}

	public UFDouble getOccAccAmount_Psn() {
		if (occAccAmount_Psn == null) {
			occAccAmount_Psn = UFDouble.ZERO_DBL;
		}
		return occAccAmount_Psn;
	}

	public void setOccAccAmount_Psn(UFDouble occAccAmount_Psn) {
		this.occAccAmount_Psn = occAccAmount_Psn;
	}

	public UFDouble getOccAccRate_Org() {
		if (occAccRate_Org == null) {
			occAccRate_Org = UFDouble.ZERO_DBL;
		}
		return occAccRate_Org;
	}

	public void setOccAccRate_Org(UFDouble occAccRate_Org) {
		this.occAccRate_Org = occAccRate_Org;
	}

	public UFDouble getOccAccAmount_Org() {
		if (occAccAmount_Org == null) {
			occAccAmount_Org = UFDouble.ZERO_DBL;
		}
		return occAccAmount_Org;
	}

	public void setOccAccAmount_Org(UFDouble occAccAmount_Org) {
		this.occAccAmount_Org = occAccAmount_Org;
	}

	public UFDouble getEmpInsRate() {
		if (empInsRate == null) {
			empInsRate = UFDouble.ZERO_DBL;
		}
		return empInsRate;
	}

	public void setEmpInsRate(UFDouble empInsRate) {
		this.empInsRate = empInsRate;
	}

	public UFDouble getEmpInsRate_Psn() {
		if (empInsRate_Psn == null) {
			empInsRate_Psn = UFDouble.ZERO_DBL;
		}
		return empInsRate_Psn;
	}

	public void setEmpInsRate_Psn(UFDouble empInsRate_Psn) {
		this.empInsRate_Psn = empInsRate_Psn;
	}

	public UFDouble getEmpInsAmount_Psn() {
		if (empInsAmount_Psn == null) {
			empInsAmount_Psn = UFDouble.ZERO_DBL;
		}
		return empInsAmount_Psn;
	}

	public void setEmpInsAmount_Psn(UFDouble empInsAmount_Psn) {
		this.empInsAmount_Psn = empInsAmount_Psn;
	}

	public UFDouble getEmpInsRate_Org() {
		if (empInsRate_Org == null) {
			empInsRate_Org = UFDouble.ZERO_DBL;
		}
		return empInsRate_Org;
	}

	public void setEmpInsRate_Org(UFDouble empInsRate_Org) {
		this.empInsRate_Org = empInsRate_Org;
	}

	public UFDouble getEmpInsAmount_Org() {
		if (empInsAmount_Org == null) {
			empInsAmount_Org = UFDouble.ZERO_DBL;
		}
		return empInsAmount_Org;
	}

	public void setEmpInsAmount_Org(UFDouble empInsAmount_Org) {
		this.empInsAmount_Org = empInsAmount_Org;
	}

	public UFDouble getLaborInsAmount_Psn() {
		if (laborInsAmount_Psn == null) {
			laborInsAmount_Psn = UFDouble.ZERO_DBL;
		}
		return laborInsAmount_Psn;
	}

	public void setLaborInsAmount_Psn(UFDouble laborInsAmount_Psn) {
		this.laborInsAmount_Psn = laborInsAmount_Psn;
	}

	public UFDouble getLaborInsAmount_Org() {
		if (laborInsAmount_Org == null) {
			laborInsAmount_Org = UFDouble.ZERO_DBL;
		}
		return laborInsAmount_Org;
	}

	public void setLaborInsAmount_Org(UFDouble laborInsAmount_Org) {
		this.laborInsAmount_Org = laborInsAmount_Org;
	}

	public UFDouble getRetireRange() {
		if (retireRange == null) {
			retireRange = UFDouble.ZERO_DBL;
		}
		return retireRange;
	}

	public void setRetireRange(UFDouble retireRange) {
		this.retireRange = retireRange;
	}

	public UFDouble getRetireWthRate_Psn() {
		if (retireWthRate_Psn == null) {
			retireWthRate_Psn = UFDouble.ZERO_DBL;
		}
		return retireWthRate_Psn;
	}

	public void setRetireWthRate_Psn(UFDouble retireWthRate_Psn) {
		this.retireWthRate_Psn = retireWthRate_Psn;
	}

	public UFDouble getRetireWthRate_Org() {
		if (retireWthRate_Org == null) {
			retireWthRate_Org = UFDouble.ZERO_DBL;
		}
		return retireWthRate_Org;
	}

	public void setRetireWthRate_Org(UFDouble retireWthRate_Org) {
		this.retireWthRate_Org = retireWthRate_Org;
	}

	public UFDouble getRetireWthAmount_Psn() {
		if (retireWthAmount_Psn == null) {
			retireWthAmount_Psn = UFDouble.ZERO_DBL;
		}
		return retireWthAmount_Psn;
	}

	public void setRetireWthAmount_Psn(UFDouble retireWthAmount_Psn) {
		this.retireWthAmount_Psn = retireWthAmount_Psn;
	}

	public UFDouble getRetireWthAmount_Org() {
		if (retireWthAmount_Org == null) {
			retireWthAmount_Org = UFDouble.ZERO_DBL;
		}
		return retireWthAmount_Org;
	}

	public void setRetireWthAmount_Org(UFDouble retireWthAmount_Org) {
		this.retireWthAmount_Org = retireWthAmount_Org;
	}

	public UFBoolean getIsMainMonth() {
		return isMainMonth;
	}

	public void setIsMainMonth(UFBoolean isMainMonth) {
		this.isMainMonth = isMainMonth;
	}

	public UFBoolean getIfCalculateLastMonth() {
		return ifCalculateLastMonth;
	}

	public void setIfCalculateLastMonth(UFBoolean ifCalculateLastMonth) {
		this.ifCalculateLastMonth = ifCalculateLastMonth;
	}

	public UFDouble getRepayFundRate() {
		return repayFundRate;
	}

	public void setRepayFundRate(UFDouble repayFundRate) {
		this.repayFundRate = repayFundRate;
	}

	public UFDouble getRepayFundAmount() {
		return repayFundAmount;
	}

	public void setRepayFundAmount(UFDouble repayFundAmount) {
		this.repayFundAmount = repayFundAmount;
	}

	public UFBoolean getIfOldRetire() {
		return ifOldRetire;
	}

	public void setIfOldRetire(UFBoolean ifOldRetire) {
		this.ifOldRetire = ifOldRetire;
	}

}
