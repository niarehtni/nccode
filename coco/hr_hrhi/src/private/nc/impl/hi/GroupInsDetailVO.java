package nc.impl.hi;

import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

public class GroupInsDetailVO {
	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public UFLiteralDate getdStartDate() {
		return dStartDate;
	}

	public void setdStartDate(UFLiteralDate dStartDate) {
		this.dStartDate = dStartDate;
	}

	public UFLiteralDate getdEndDate() {
		return dEndDate;
	}

	public void setdEndDate(UFLiteralDate dEndDate) {
		this.dEndDate = dEndDate;
	}

	public UFDouble getiStuffPay() {
		return iStuffPay;
	}

	public void setiStuffPay(UFDouble iStuffPay) {
		this.iStuffPay = iStuffPay;
	}

	public UFDouble getiCompanyPay() {
		return iCompanyPay;
	}

	public void setiCompanyPay(UFDouble iCompanyPay) {
		this.iCompanyPay = iCompanyPay;
	}
	
	public UFDouble getFayinsurancemoney() {
		return fayinsurancemoney;
	}

	public void setFayinsurancemoney(UFDouble fayinsurancemoney) {
		this.fayinsurancemoney = fayinsurancemoney;
	}

	public UFDouble getEmpinsurancemoney() {
		return empinsurancemoney;
	}

	public void setEmpinsurancemoney(UFDouble empinsurancemoney) {
		this.empinsurancemoney = empinsurancemoney;
	}

	private String pk_org;
	private String pk_psndoc;
	private UFLiteralDate dStartDate;
	private UFLiteralDate dEndDate;
	private UFDouble iStuffPay;
	private UFDouble iCompanyPay;
	//增加眷属团保投保费和员工团保投保费
	private UFDouble fayinsurancemoney;
	private UFDouble empinsurancemoney;
}
