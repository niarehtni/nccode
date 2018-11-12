package nc.impl.hi;

import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
/**
 * 眷属与员工团保投保明细子集
 * @author he
 *
 */
public class FamEmpGroupInsDetailVO {
	private String pk_org;
	private String pk_psndoc;
	private UFLiteralDate dStartDate;
	private UFLiteralDate dEndDate;
	//险种
	private String insurancecode;
	//身份
	private String identitycode;
	//是否以户计算
	private UFBoolean ishousehold;
	//金额
	private UFDouble insuranceamount; 
	//姓名
	private String name;
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
	public String getInsurancecode() {
		return insurancecode;
	}
	public void setInsurancecode(String insurancecode) {
		this.insurancecode = insurancecode;
	}
	public String getIdentitycode() {
		return identitycode;
	}
	public void setIdentitycode(String identitycode) {
		this.identitycode = identitycode;
	}
	public UFBoolean getIshousehold() {
		return ishousehold;
	}
	public void setIshousehold(UFBoolean ishousehold) {
		this.ishousehold = ishousehold;
	}
	public UFDouble getInsuranceamount() {
		return insuranceamount;
	}
	public void setInsuranceamount(UFDouble insuranceamount) {
		this.insuranceamount = insuranceamount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
