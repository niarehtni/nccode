package nc.vo.hi.wadoc;

import nc.vo.annotation.MDEntityInfo;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;

public class SalarycalculationVO extends SuperVO {
	private String pk_psndoc;
	private String pk_salarycalculation;
	private nc.vo.pub.lang.UFDouble pvalue;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;
	private String pk_user;

	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String PVALUE = "PVALUE";
//	public static final String PK_SALARYCALATION = "pk_Salarycalculation";

	public String getParentPKFieldName() {
		return null;
	}
	
	
	








	public String getPk_user() {
		return pk_user;
	}











	public void setPk_user(String pk_user) {
		this.pk_user = pk_user;
	}











	public String getPk_psndoc() {
		return pk_psndoc;
	}











	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}











	public String getPk_salarycalculation() {
		return pk_salarycalculation;
	}




	public void setPk_salarycalculation(String pk_salarycalculation) {
		this.pk_salarycalculation = pk_salarycalculation;
	}




	public nc.vo.pub.lang.UFDouble getPvalue() {
		return pvalue;
	}




	public void setPvalue(nc.vo.pub.lang.UFDouble pvalue) {
		this.pvalue = pvalue;
	}




	public java.lang.Integer getDr() {
		return dr;
	}




	public void setDr(java.lang.Integer dr) {
		this.dr = dr;
	}




	public nc.vo.pub.lang.UFDateTime getTs() {
		return ts;
	}




	public void setTs(nc.vo.pub.lang.UFDateTime ts) {
		this.ts = ts;
	}




	public String getPKFieldName() {
		return "pk_salarycalculation";
	}

	public String getTableName() {
		return "salarycalculation";
	}

	public static String getDefaultTableName() {
		return "salarycalculation";
	}

	public SalarycalculationVO() {
	}

	@MDEntityInfo(beanFullclassName = "nc.vo.hi.wadoc.SalarycalculationVO")
	public IVOMeta getMetaData() {
		return null;
	}

}
