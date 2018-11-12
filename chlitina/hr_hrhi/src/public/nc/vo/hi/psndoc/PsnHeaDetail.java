package nc.vo.hi.psndoc;

import nc.vo.pub.lang.UFDouble;

/**
 * ����Ͷ����ϸ
 * @author Yonyou
 *
 */
public class PsnHeaDetail extends PsndocDefVO {
	
	/**
	 * ���˹�˾
	 */
	private String legalpersonorg;
	
	/**
	 * ����
	 */
	private UFDouble spacing;
	
	/**
	 * ����
	 */
	private String surname;
	
	/**
	 * ��ν
	 */
	private String appellation;
	
	
	/**
	 * �a�����
	 */
	private String subsidyid;
	
	/**
	 * �������M
	 */
	private UFDouble heainspre;
	
	/**
	 * �������
	 */
	private String heayear;
	
	/**
	 * �����·�
	 */
	private String heamonth;
	
	
	
	public String getLegalpersonorg() {
		return legalpersonorg;
	}



	public void setLegalpersonorg(String legalpersonorg) {
		this.legalpersonorg = legalpersonorg;
	}



	public UFDouble getSpacing() {
		return spacing;
	}



	public void setSpacing(UFDouble spacing) {
		this.spacing = spacing;
	}



	public String getSurname() {
		return surname;
	}



	public void setSurname(String surname) {
		this.surname = surname;
	}



	public String getAppellation() {
		return appellation;
	}



	public void setAppellation(String appellation) {
		this.appellation = appellation;
	}



	public String getSubsidyid() {
		return subsidyid;
	}



	public void setSubsidyid(String subsidyid) {
		this.subsidyid = subsidyid;
	}



	public UFDouble getHeainspre() {
		return heainspre;
	}



	public void setHeainspre(UFDouble heainspre) {
		this.heainspre = heainspre;
	}



	public String getHeayear() {
		return heayear;
	}



	public void setHeayear(String heayear) {
		this.heayear = heayear;
	}



	public String getHeamonth() {
		return heamonth;
	}



	public void setHeamonth(String heamonth) {
		this.heamonth = heamonth;
	}



	public String getTableName() {
		return "hi_psndoc_headetail";
	}
}
