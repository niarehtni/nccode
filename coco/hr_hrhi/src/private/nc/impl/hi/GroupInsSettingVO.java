package nc.impl.hi;

import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

public class GroupInsSettingVO {
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

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public String getsID() {
		return sID;
	}

	public void setsID(String sID) {
		this.sID = sID;
	}

	public UFLiteralDate getdBirthday() {
		return dBirthday;
	}

	public void setdBirthday(UFLiteralDate dBirthday) {
		this.dBirthday = dBirthday;
	}

	public String getPk_GroupInsurance() {
		return pk_GroupInsurance;
	}

	public void setPk_GroupInsurance(String pk_GroupInsurance) {
		this.pk_GroupInsurance = pk_GroupInsurance;
	}

	public String getPk_RelationType() {
		return pk_RelationType;
	}

	public void setPk_RelationType(String pk_RelationType) {
		this.pk_RelationType = pk_RelationType;
	}

	public UFDouble getiSalaryBase() {
		return iSalaryBase;
	}

	public void setiSalaryBase(UFDouble iSalaryBase) {
		this.iSalaryBase = iSalaryBase;
	}

	public UFBoolean getbIsEnd() {
		return bIsEnd;
	}

	public void setbIsEnd(UFBoolean bIsEnd) {
		this.bIsEnd = bIsEnd;
	}
	public String getInsurancecompany() {
		return insurancecompany;
	}

	public void setInsurancecompany(String insurancecompany) {
		this.insurancecompany = insurancecompany;
	}
	public UFLiteralDate dStartDate;
	public UFLiteralDate dEndDate;
	public String sName;
	public String sID;
	public UFLiteralDate dBirthday;
	public String pk_GroupInsurance;
	public String pk_RelationType;
	public UFDouble iSalaryBase;
	public UFBoolean bIsEnd;
	public String insurancecompany;
	public GroupInsSettingVO() {
	}
}