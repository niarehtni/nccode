package nc.vo.hrwa.wadaysalary;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此處簡要描述此類功能 </b>
 * <p>
 * 此處添加累的描述信息
 * </p>
 * 創建日期:2019/1/28
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class DaySalaryVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8377625563019588778L;
	/**
	 * 日薪主鍵
	 */
	public String pk_daysalary;
	/**
	 * 人力資源組織
	 */
	public String pk_hrorg;
	/**
	 * 薪資方案
	 */
	public String pk_wa_class;
	/**
	 * 薪資項目
	 */
	public String pk_wa_item;
	/**
	 * 薪資變動情況
	 */
	public String pk_psndoc_sub;
	/**
	 * 薪資變動情況時間戳
	 */
	public UFDateTime wadocts;
	/**
	 * 人員基本主鍵
	 */
	public String pk_psndoc;
	/**
	 * 人員任職主鍵
	 */
	public String pk_psnjob;
	/**
	 * 薪資日期
	 */
	public UFLiteralDate salarydate;
	/**
	 * 薪資年
	 */
	public Integer cyear;
	/**
	 * 薪資月
	 */
	public Integer cperiod;
	/**
	 * 日薪
	 */
	public UFDouble daysalary;
	/**
	 * 時薪
	 */
	public UFDouble hoursalary;
	/**
	 * 薪資項目分組
	 */
	public String pk_group_item;
	/**
	 * 薪資項目分組時間戳
	 */
	public UFDateTime groupitemts;
	/**
	 * 是否考勤
	 */
	// 考勤日薪已经合并到定调资日薪
	@Deprecated
	public UFBoolean isattend;
	/**
	 * 是否扣稅
	 */
	public UFBoolean taxflag;
	/**
	 * 時間戳
	 */
	public UFDateTime ts;

	/**
	 * hash值
	 */
	public Integer hashKey;

	public Integer getHashKey() {
		return hashKey;
	}

	public void setHashKey(Integer hashKey) {
		this.hashKey = hashKey;
	}

	/**
	 * 屬性 pk_daysalary的Getter方法.屬性名：日薪主鍵 創建日期:2019/1/28
	 * 
	 * @return java.lang.String
	 */
	public String getPk_daysalary() {
		return this.pk_daysalary;
	}

	/**
	 * 屬性pk_daysalary的Setter方法.屬性名：日薪主鍵 創建日期:2019/1/28
	 * 
	 * @param newPk_daysalary
	 *            java.lang.String
	 */
	public void setPk_daysalary(String pk_daysalary) {
		this.pk_daysalary = pk_daysalary;
	}

	/**
	 * 屬性 pk_hrorg的Getter方法.屬性名：人力資源組織 創建日期:2019/1/28
	 * 
	 * @return nc.vo.org.HROrgVO
	 */
	public String getPk_hrorg() {
		return this.pk_hrorg;
	}

	/**
	 * 屬性pk_hrorg的Setter方法.屬性名：人力資源組織 創建日期:2019/1/28
	 * 
	 * @param newPk_hrorg
	 *            nc.vo.org.HROrgVO
	 */
	public void setPk_hrorg(String pk_hrorg) {
		this.pk_hrorg = pk_hrorg;
	}

	/**
	 * 屬性 pk_wa_class的Getter方法.屬性名：薪資方案 創建日期:2019/1/28
	 * 
	 * @return java.lang.String
	 */
	public String getPk_wa_class() {
		return this.pk_wa_class;
	}

	/**
	 * 屬性pk_wa_class的Setter方法.屬性名：薪資方案 創建日期:2019/1/28
	 * 
	 * @param newPk_wa_class
	 *            java.lang.String
	 */
	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}

	/**
	 * 屬性 pk_wa_item的Getter方法.屬性名：薪資項目 創建日期:2019/1/28
	 * 
	 * @return java.lang.String
	 */
	public String getPk_wa_item() {
		return this.pk_wa_item;
	}

	/**
	 * 屬性pk_wa_item的Setter方法.屬性名：薪資項目 創建日期:2019/1/28
	 * 
	 * @param newPk_wa_item
	 *            java.lang.String
	 */
	public void setPk_wa_item(String pk_wa_item) {
		this.pk_wa_item = pk_wa_item;
	}

	/**
	 * 屬性 pk_psndoc_sub的Getter方法.屬性名：薪資變動情況 創建日期:2019/1/28
	 * 
	 * @return java.lang.String
	 */
	public String getPk_psndoc_sub() {
		return this.pk_psndoc_sub;
	}

	/**
	 * 屬性pk_psndoc_sub的Setter方法.屬性名：薪資變動情況 創建日期:2019/1/28
	 * 
	 * @param newPk_psndoc_sub
	 *            java.lang.String
	 */
	public void setPk_psndoc_sub(String pk_psndoc_sub) {
		this.pk_psndoc_sub = pk_psndoc_sub;
	}

	/**
	 * 屬性 wadocts的Getter方法.屬性名：薪資變動情況時間戳 創建日期:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getWadocts() {
		return this.wadocts;
	}

	/**
	 * 屬性wadocts的Setter方法.屬性名：薪資變動情況時間戳 創建日期:2019/1/28
	 * 
	 * @param newWadocts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setWadocts(UFDateTime wadocts) {
		this.wadocts = wadocts;
	}

	/**
	 * 屬性 pk_psndoc的Getter方法.屬性名：人員基本主鍵 創建日期:2019/1/28
	 * 
	 * @return nc.vo.hi.psndoc.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * 屬性pk_psndoc的Setter方法.屬性名：人員基本主鍵 創建日期:2019/1/28
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.hi.psndoc.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * 屬性 pk_psnjob的Getter方法.屬性名：人員任職主鍵 創建日期:2019/1/28
	 * 
	 * @return nc.vo.hi.psndoc.PsnJobVO
	 */
	public String getPk_psnjob() {
		return this.pk_psnjob;
	}

	/**
	 * 屬性pk_psnjob的Setter方法.屬性名：人員任職主鍵 創建日期:2019/1/28
	 * 
	 * @param newPk_psnjob
	 *            nc.vo.hi.psndoc.PsnJobVO
	 */
	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	/**
	 * 屬性 salarydate的Getter方法.屬性名：薪資日期 創建日期:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public UFLiteralDate getSalarydate() {
		return this.salarydate;
	}

	/**
	 * 屬性salarydate的Setter方法.屬性名：薪資日期 創建日期:2019/1/28
	 * 
	 * @param newSalarydate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setSalarydate(UFLiteralDate salarydate) {
		this.salarydate = salarydate;
	}

	/**
	 * 屬性 cyear的Getter方法.屬性名：薪資年 創建日期:2019/1/28
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getCyear() {
		return this.cyear;
	}

	/**
	 * 屬性cyear的Setter方法.屬性名：薪資年 創建日期:2019/1/28
	 * 
	 * @param newCyear
	 *            java.lang.Integer
	 */
	public void setCyear(Integer cyear) {
		this.cyear = cyear;
	}

	/**
	 * 屬性 cperiod的Getter方法.屬性名：薪資月 創建日期:2019/1/28
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getCperiod() {
		return this.cperiod;
	}

	/**
	 * 屬性cperiod的Setter方法.屬性名：薪資月 創建日期:2019/1/28
	 * 
	 * @param newCperiod
	 *            java.lang.Integer
	 */
	public void setCperiod(Integer cperiod) {
		this.cperiod = cperiod;
	}

	/**
	 * 屬性 daysalary的Getter方法.屬性名：日薪 創建日期:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getDaysalary() {
		return this.daysalary;
	}

	/**
	 * 屬性daysalary的Setter方法.屬性名：日薪 創建日期:2019/1/28
	 * 
	 * @param newDaysalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDaysalary(UFDouble daysalary) {
		this.daysalary = daysalary;
	}

	/**
	 * 屬性 hoursalary的Getter方法.屬性名：時薪 創建日期:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHoursalary() {
		return this.hoursalary;
	}

	/**
	 * 屬性hoursalary的Setter方法.屬性名：時薪 創建日期:2019/1/28
	 * 
	 * @param newHoursalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHoursalary(UFDouble hoursalary) {
		this.hoursalary = hoursalary;
	}

	/**
	 * 屬性 pk_group_item的Getter方法.屬性名：薪資項目分組 創建日期:2019/1/28
	 * 
	 * @return java.lang.String
	 */
	public String getPk_group_item() {
		return this.pk_group_item;
	}

	/**
	 * 屬性pk_group_item的Setter方法.屬性名：薪資項目分組 創建日期:2019/1/28
	 * 
	 * @param newPk_group_item
	 *            java.lang.String
	 */
	public void setPk_group_item(String pk_group_item) {
		this.pk_group_item = pk_group_item;
	}

	/**
	 * 屬性 groupitemts的Getter方法.屬性名：薪資項目分組時間戳 創建日期:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getGroupitemts() {
		return this.groupitemts;
	}

	/**
	 * 屬性groupitemts的Setter方法.屬性名：薪資項目分組時間戳 創建日期:2019/1/28
	 * 
	 * @param newGroupitemts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setGroupitemts(UFDateTime groupitemts) {
		this.groupitemts = groupitemts;
	}

	/**
	 * 屬性 isattend的Getter方法.屬性名：是否考勤 創建日期:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 * @deprecated tank 废弃 考勤日薪已经和定调资日薪合并
	 */
	@Deprecated
	public UFBoolean getIsattend() {
		return this.isattend;
	}

	/**
	 * 屬性isattend的Setter方法.屬性名：是否考勤 創建日期:2019/1/28
	 * 
	 * @param newIsattend
	 *            nc.vo.pub.lang.UFBoolean
	 * @deprecated tank 废弃 考勤日薪已经和定调资日薪合并
	 */
	@Deprecated
	public void setIsattend(UFBoolean isattend) {
		this.isattend = isattend;
	}

	/**
	 * 屬性 taxflag的Getter方法.屬性名：是否扣稅 創建日期:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getTaxflag() {
		return this.taxflag;
	}

	/**
	 * 屬性taxflag的Setter方法.屬性名：是否扣稅 創建日期:2019/1/28
	 * 
	 * @param newTaxflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setTaxflag(UFBoolean taxflag) {
		this.taxflag = taxflag;
	}

	/**
	 * 屬性 生成時間戳的Getter方法.屬性名：時間戳 創建日期:2019/1/28
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 屬性生成時間戳的Setter方法.屬性名：時間戳 創建日期:2019/1/28
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
