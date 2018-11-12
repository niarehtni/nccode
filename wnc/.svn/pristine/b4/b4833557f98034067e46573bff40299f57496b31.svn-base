/**
 * @(#)DataItfFileVO.java 1.0 2018Äê1ÔÂ30ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.vo.wa.datainterface;

import nc.pub.wa.datainterface.DataItfConst;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings("serial")
public class DataItfFileVO extends SuperVO {
	private String deptcode;
	private String deptname;
	private String psndoccode;
	private String psndocname;
	private String cyearperiod;
	private String pldecode;
	private String pldename;
	private UFDouble taxadd;
	private UFDouble taxsub;
	private UFDouble notaxadd;
	private UFDouble notaxsub;
	private String remark;
	private String paydate;

	public String getPaydate() {
		return paydate;
	}

	public void setPaydate(String paydate) {
		this.paydate = paydate;
	}

	public String getDeptcode() {
		return deptcode;
	}

	public void setDeptcode(String deptcode) {
		this.deptcode = deptcode;
	}

	public String getDeptname() {
		return deptname;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	public String getPsndoccode() {
		return psndoccode;
	}

	public void setPsndoccode(String psndoccode) {
		this.psndoccode = psndoccode;
	}

	public String getPsndocname() {
		return psndocname;
	}

	public void setPsndocname(String psndocname) {
		this.psndocname = psndocname;
	}

	public String getCyearperiod() {
		return cyearperiod;
	}

	public void setCyearperiod(String cyearperiod) {
		this.cyearperiod = cyearperiod;
	}

	public String getPldecode() {
		return pldecode;
	}

	public void setPldecode(String pldecode) {
		this.pldecode = pldecode;
	}

	public String getPldename() {
		return pldename;
	}

	public void setPldename(String pldename) {
		this.pldename = pldename;
	}

	public UFDouble getTaxadd() {
		return taxadd;
	}

	public void setTaxadd(UFDouble taxadd) {
		this.taxadd = taxadd;
	}

	public UFDouble getTaxsub() {
		return taxsub;
	}

	public void setTaxsub(UFDouble taxsub) {
		this.taxsub = taxsub;
	}

	public UFDouble getNotaxadd() {
		return notaxadd;
	}

	public void setNotaxadd(UFDouble notaxadd) {
		this.notaxadd = notaxadd;
	}

	public UFDouble getNotaxsub() {
		return notaxsub;
	}

	public void setNotaxsub(UFDouble notaxsub) {
		this.notaxsub = notaxsub;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTotalSum() {
		UFDouble ufd = getTaxadd().add(getTaxsub()).add(getNotaxadd()).add(getNotaxsub());
		return DataItfConst.DF_NUM.format(ufd.doubleValue());
	}

	private String dataid;
	private String pk_group;
	private String pk_org;
	private String pk_wa_class;
	private String pk_psndoc;
	private String pk_dept;

	public String getDataid() {
		return dataid;
	}

	public void setDataid(String dataid) {
		this.dataid = dataid;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_wa_class() {
		return pk_wa_class;
	}

	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	public String getPk_dept() {
		return pk_dept;
	}

	public void setPk_dept(String pk_dept) {
		this.pk_dept = pk_dept;
	}

	@Override
	public String getPKFieldName() {
		return DataItfConst.PKFIELD;
	}

}
