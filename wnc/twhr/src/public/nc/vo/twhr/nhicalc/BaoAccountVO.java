package nc.vo.twhr.nhicalc;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 * 此处添加累的描述信息
 * </p>
 * 创建日期:2018-8-6
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class BaoAccountVO extends SuperVO {

	/**
	 * 對帳單行主鍵
	 */
	public java.lang.String id;
	/**
	 * 集團
	 */
	public java.lang.String pk_group;
	/**
	 * 组织
	 */
	public java.lang.String pk_org;
	/**
	 * 组织版本
	 */
	public java.lang.String pk_org_v;
	/**
	 * 创建人
	 */
	public java.lang.String creator;
	/**
	 * 创建时间
	 */
	public UFDateTime creationtime;
	/**
	 * 修改人
	 */
	public java.lang.String modifier;
	/**
	 * 修改时间
	 */
	public UFDateTime modifiedtime;
	/**
	 * 起始日期
	 */
	public nc.vo.pub.lang.UFDate begindate;
	/**
	 * 截止日期
	 */
	public java.lang.Integer enddate;
	/**
	 * 薪资期间
	 */
	public java.lang.String pk_period;
	/**
	 * 身份证号
	 */
	public java.lang.String idno;
	/**
	 * 勞保投保金額
	 */
	public nc.vo.pub.lang.UFDouble labor_amount;
	/**
	 * 勞保員工負擔金額
	 */
	public nc.vo.pub.lang.UFDouble labor_psnamount;
	/**
	 * 勞保公司負擔金額
	 */
	public nc.vo.pub.lang.UFDouble labor_orgamount;
	/**
	 * 勞退提繳薪資總額
	 */
	public nc.vo.pub.lang.UFDouble retire_amount;
	/**
	 * 勞退員工自提金額
	 */
	public nc.vo.pub.lang.UFDouble retire_psnamount;
	/**
	 * 勞退雇主提繳金額
	 */
	public nc.vo.pub.lang.UFDouble retire_orgamount;
	/**
	 * 健保投保金額
	 */
	public nc.vo.pub.lang.UFDouble health_amount;
	/**
	 * 健保員工保費
	 */
	public nc.vo.pub.lang.UFDouble health_psnamount;
	/**
	 * 健保雇主保費
	 */
	public nc.vo.pub.lang.UFDouble health_orgamount;
	/**
	 * 表名
	 */
	public java.lang.String code;
	/**
	 * 表名2
	 */
	public java.lang.String name;
	/**
	 * 时间戳
	 */
	public UFDateTime ts;
	/**
	 * dr
	 */
	public Integer dr;

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	/**
	 * 属性 id的Getter方法.属性名：對帳單行主鍵 创建日期:2018-8-6
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getId() {
		return this.id;
	}

	/**
	 * 属性id的Setter方法.属性名：對帳單行主鍵 创建日期:2018-8-6
	 * 
	 * @param newId
	 *            java.lang.String
	 */
	public void setId(java.lang.String id) {
		this.id = id;
	}

	/**
	 * 属性 pk_group的Getter方法.属性名：集團 创建日期:2018-8-6
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public java.lang.String getPk_group() {
		return this.pk_group;
	}

	/**
	 * 属性pk_group的Setter方法.属性名：集團 创建日期:2018-8-6
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(java.lang.String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * 属性 pk_org的Getter方法.属性名：组织 创建日期:2018-8-6
	 * 
	 * @return nc.vo.org.HROrgVO
	 */
	public java.lang.String getPk_org() {
		return this.pk_org;
	}

	/**
	 * 属性pk_org的Setter方法.属性名：组织 创建日期:2018-8-6
	 * 
	 * @param newPk_org
	 *            nc.vo.org.HROrgVO
	 */
	public void setPk_org(java.lang.String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * 属性 pk_org_v的Getter方法.属性名：组织版本 创建日期:2018-8-6
	 * 
	 * @return nc.vo.vorg.OrgVersionVO
	 */
	public java.lang.String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * 属性pk_org_v的Setter方法.属性名：组织版本 创建日期:2018-8-6
	 * 
	 * @param newPk_org_v
	 *            nc.vo.vorg.OrgVersionVO
	 */
	public void setPk_org_v(java.lang.String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * 属性 creator的Getter方法.属性名：创建人 创建日期:2018-8-6
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public java.lang.String getCreator() {
		return this.creator;
	}

	/**
	 * 属性creator的Setter方法.属性名：创建人 创建日期:2018-8-6
	 * 
	 * @param newCreator
	 *            nc.vo.sm.UserVO
	 */
	public void setCreator(java.lang.String creator) {
		this.creator = creator;
	}

	/**
	 * 属性 creationtime的Getter方法.属性名：创建时间 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * 属性creationtime的Setter方法.属性名：创建时间 创建日期:2018-8-6
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * 属性 modifier的Getter方法.属性名：修改人 创建日期:2018-8-6
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public java.lang.String getModifier() {
		return this.modifier;
	}

	/**
	 * 属性modifier的Setter方法.属性名：修改人 创建日期:2018-8-6
	 * 
	 * @param newModifier
	 *            nc.vo.sm.UserVO
	 */
	public void setModifier(java.lang.String modifier) {
		this.modifier = modifier;
	}

	/**
	 * 属性 modifiedtime的Getter方法.属性名：修改时间 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * 属性modifiedtime的Setter方法.属性名：修改时间 创建日期:2018-8-6
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * 属性 begindate的Getter方法.属性名：起始日期 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getBegindate() {
		return this.begindate;
	}

	/**
	 * 属性begindate的Setter方法.属性名：起始日期 创建日期:2018-8-6
	 * 
	 * @param newBegindate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setBegindate(nc.vo.pub.lang.UFDate begindate) {
		this.begindate = begindate;
	}

	/**
	 * 属性 enddate的Getter方法.属性名：截止日期 创建日期:2018-8-6
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getEnddate() {
		return this.enddate;
	}

	/**
	 * 属性enddate的Setter方法.属性名：截止日期 创建日期:2018-8-6
	 * 
	 * @param newEnddate
	 *            java.lang.Integer
	 */
	public void setEnddate(java.lang.Integer enddate) {
		this.enddate = enddate;
	}

	/**
	 * 属性 pk_period的Getter方法.属性名：薪资期间 创建日期:2018-8-6
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_period() {
		return this.pk_period;
	}

	/**
	 * 属性pk_period的Setter方法.属性名：薪资期间 创建日期:2018-8-6
	 * 
	 * @param newPk_period
	 *            java.lang.String
	 */
	public void setPk_period(java.lang.String pk_period) {
		this.pk_period = pk_period;
	}

	/**
	 * 属性 idno的Getter方法.属性名：身份证号 创建日期:2018-8-6
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getIdno() {
		return this.idno;
	}

	/**
	 * 属性idno的Setter方法.属性名：身份证号 创建日期:2018-8-6
	 * 
	 * @param newIdno
	 *            java.lang.String
	 */
	public void setIdno(java.lang.String idno) {
		this.idno = idno;
	}

	/**
	 * 属性 labor_amount的Getter方法.属性名：勞保投保金額 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getLabor_amount() {
		return this.labor_amount;
	}

	/**
	 * 属性labor_amount的Setter方法.属性名：勞保投保金額 创建日期:2018-8-6
	 * 
	 * @param newLabor_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLabor_amount(nc.vo.pub.lang.UFDouble labor_amount) {
		this.labor_amount = labor_amount;
	}

	/**
	 * 属性 labor_psnamount的Getter方法.属性名：勞保員工負擔金額 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getLabor_psnamount() {
		return this.labor_psnamount;
	}

	/**
	 * 属性labor_psnamount的Setter方法.属性名：勞保員工負擔金額 创建日期:2018-8-6
	 * 
	 * @param newLabor_psnamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLabor_psnamount(nc.vo.pub.lang.UFDouble labor_psnamount) {
		this.labor_psnamount = labor_psnamount;
	}

	/**
	 * 属性 labor_orgamount的Getter方法.属性名：勞保公司負擔金額 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getLabor_orgamount() {
		return this.labor_orgamount;
	}

	/**
	 * 属性labor_orgamount的Setter方法.属性名：勞保公司負擔金額 创建日期:2018-8-6
	 * 
	 * @param newLabor_orgamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLabor_orgamount(nc.vo.pub.lang.UFDouble labor_orgamount) {
		this.labor_orgamount = labor_orgamount;
	}

	/**
	 * 属性 retire_amount的Getter方法.属性名：勞退提繳薪資總額 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRetire_amount() {
		return this.retire_amount;
	}

	/**
	 * 属性retire_amount的Setter方法.属性名：勞退提繳薪資總額 创建日期:2018-8-6
	 * 
	 * @param newRetire_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRetire_amount(nc.vo.pub.lang.UFDouble retire_amount) {
		this.retire_amount = retire_amount;
	}

	/**
	 * 属性 retire_psnamount的Getter方法.属性名：勞退員工自提金額 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRetire_psnamount() {
		return this.retire_psnamount;
	}

	/**
	 * 属性retire_psnamount的Setter方法.属性名：勞退員工自提金額 创建日期:2018-8-6
	 * 
	 * @param newRetire_psnamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRetire_psnamount(nc.vo.pub.lang.UFDouble retire_psnamount) {
		this.retire_psnamount = retire_psnamount;
	}

	/**
	 * 属性 retire_orgamount的Getter方法.属性名：勞退雇主提繳金額 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRetire_orgamount() {
		return this.retire_orgamount;
	}

	/**
	 * 属性retire_orgamount的Setter方法.属性名：勞退雇主提繳金額 创建日期:2018-8-6
	 * 
	 * @param newRetire_orgamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRetire_orgamount(nc.vo.pub.lang.UFDouble retire_orgamount) {
		this.retire_orgamount = retire_orgamount;
	}

	/**
	 * 属性 health_amount的Getter方法.属性名：健保投保金額 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getHealth_amount() {
		return this.health_amount;
	}

	/**
	 * 属性health_amount的Setter方法.属性名：健保投保金額 创建日期:2018-8-6
	 * 
	 * @param newHealth_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHealth_amount(nc.vo.pub.lang.UFDouble health_amount) {
		this.health_amount = health_amount;
	}

	/**
	 * 属性 health_psnamount的Getter方法.属性名：健保員工保費 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getHealth_psnamount() {
		return this.health_psnamount;
	}

	/**
	 * 属性health_psnamount的Setter方法.属性名：健保員工保費 创建日期:2018-8-6
	 * 
	 * @param newHealth_psnamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHealth_psnamount(nc.vo.pub.lang.UFDouble health_psnamount) {
		this.health_psnamount = health_psnamount;
	}

	/**
	 * 属性 health_orgamount的Getter方法.属性名：健保雇主保費 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getHealth_orgamount() {
		return this.health_orgamount;
	}

	/**
	 * 属性health_orgamount的Setter方法.属性名：健保雇主保費 创建日期:2018-8-6
	 * 
	 * @param newHealth_orgamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHealth_orgamount(nc.vo.pub.lang.UFDouble health_orgamount) {
		this.health_orgamount = health_orgamount;
	}

	/**
	 * 属性 code的Getter方法.属性名：表名 创建日期:2018-8-6
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCode() {
		return this.code;
	}

	/**
	 * 属性code的Setter方法.属性名：表名 创建日期:2018-8-6
	 * 
	 * @param newCode
	 *            java.lang.String
	 */
	public void setCode(java.lang.String code) {
		this.code = code;
	}

	/**
	 * 属性 name的Getter方法.属性名：表名2 创建日期:2018-8-6
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getName() {
		return this.name;
	}

	/**
	 * 属性name的Setter方法.属性名：表名2 创建日期:2018-8-6
	 * 
	 * @param newName
	 *            java.lang.String
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * 属性 生成时间戳的Getter方法.属性名：时间戳 创建日期:2018-8-6
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 属性生成时间戳的Setter方法.属性名：时间戳 创建日期:2018-8-6
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("twhr.baoaccount");
	}
}
