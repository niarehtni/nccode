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
 * <b> 此处简要描述此类功能 </b>
 * <p>
 * 此处添加累的描述信息
 * </p>
 * 创建日期:2018-5-3
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
	 * 主鍵
	 */
	public String pk_leaveregdetail;
	/**
	 * 來源單據主鍵
	 */
	public String pk_leavereg_history;
	/**
	 * 生效日期
	 */
	public UFLiteralDate effectivedate;
	/**
	 * 日薪
	 */
	public UFDouble daysalary;
	/**
	 * 日薪日
	 */
	public UFLiteralDate salarydate;
	/**
	 * 休假開始日期
	 */
	public UFDateTime begindate;
	/**
	 * 休假結束日期
	 */
	public UFDateTime enddate;
	/**
	 * 休假時長
	 */
	public UFDouble leavehour;
	/**
	 * 扣款費用
	 */
	public UFDouble leavecharge;
	/**
	 * 扣款費率
	 */
	public Integer rate;
	/**
	 * 假期類別
	 */
	public String pk_leavetype;
	/**
	 * 假期類別copy
	 */
	public String pk_leavetypecopy;
	/**
	 * 是否銷假
	 */
	public UFBoolean isleaveoff;
	/**
	 * 人员基本主键
	 */
	public String pk_psndoc;
	/**
	 * 人员任职主键
	 */
	public String pk_psnjob;
	/**
	 * 人力資源組織
	 */
	public String pk_hrorg;
	/**
	 * 上层单据主键
	 */
	public String pk_daysalary;
	/**
	 * 时间戳
	 */
	public UFDateTime ts;
	
	

	public UFLiteralDate getSalarydate() {
		return salarydate;
	}

	public void setSalarydate(UFLiteralDate salarydate) {
		this.salarydate = salarydate;
	}

	/**
	 * 属性 pk_leaveregdetail的Getter方法.属性名：主鍵 创建日期:2018-5-3
	 * 
	 * @return java.lang.String
	 */
	public String getPk_leaveregdetail() {
		return this.pk_leaveregdetail;
	}

	/**
	 * 属性pk_leaveregdetail的Setter方法.属性名：主鍵 创建日期:2018-5-3
	 * 
	 * @param newPk_leaveregdetail
	 *            java.lang.String
	 */
	public void setPk_leaveregdetail(String pk_leaveregdetail) {
		this.pk_leaveregdetail = pk_leaveregdetail;
	}

	/**
	 * 属性 pk_leavereg_history的Getter方法.属性名：來源單據主鍵 创建日期:2018-5-3
	 * 
	 * @return java.lang.String
	 */
	public String getPk_leavereg_history() {
		return this.pk_leavereg_history;
	}

	/**
	 * 属性pk_leavereg_history的Setter方法.属性名：來源單據主鍵 创建日期:2018-5-3
	 * 
	 * @param newPk_leavereg_history
	 *            java.lang.String
	 */
	public void setPk_leavereg_history(String pk_leavereg_history) {
		this.pk_leavereg_history = pk_leavereg_history;
	}

	/**
	 * 属性 effectivedate的Getter方法.属性名：生效日期 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public UFLiteralDate getEffectivedate() {
		return this.effectivedate;
	}

	/**
	 * 属性effectivedate的Setter方法.属性名：生效日期 创建日期:2018-5-3
	 * 
	 * @param newEffectivedate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setEffectivedate(UFLiteralDate effectivedate) {
		this.effectivedate = effectivedate;
	}

	/**
	 * 属性 daysalary的Getter方法.属性名：日薪 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getDaysalary() {
		return this.daysalary;
	}

	/**
	 * 属性daysalary的Setter方法.属性名：日薪 创建日期:2018-5-3
	 * 
	 * @param newDaysalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDaysalary(UFDouble daysalary) {
		this.daysalary = daysalary;
	}

	/**
	 * 属性 begindate的Getter方法.属性名：休假開始日期 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getBegindate() {
		return this.begindate;
	}

	/**
	 * 属性begindate的Setter方法.属性名：休假開始日期 创建日期:2018-5-3
	 * 
	 * @param newBegindate
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setBegindate(UFDateTime begindate) {
		this.begindate = begindate;
	}

	/**
	 * 属性 enddate的Getter方法.属性名：休假結束日期 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getEnddate() {
		return this.enddate;
	}

	/**
	 * 属性enddate的Setter方法.属性名：休假結束日期 创建日期:2018-5-3
	 * 
	 * @param newEnddate
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setEnddate(UFDateTime enddate) {
		this.enddate = enddate;
	}

	/**
	 * 属性 leavehour的Getter方法.属性名：休假時長 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLeavehour() {
		return this.leavehour;
	}

	/**
	 * 属性leavehour的Setter方法.属性名：休假時長 创建日期:2018-5-3
	 * 
	 * @param newLeavehour
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLeavehour(UFDouble leavehour) {
		this.leavehour = leavehour;
	}

	/**
	 * 属性 leavecharge的Getter方法.属性名：扣款費用 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLeavecharge() {
		return this.leavecharge;
	}

	/**
	 * 属性leavecharge的Setter方法.属性名：扣款費用 创建日期:2018-5-3
	 * 
	 * @param newLeavecharge
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLeavecharge(UFDouble leavecharge) {
		this.leavecharge = leavecharge;
	}

	/**
	 * 属性 rate的Getter方法.属性名：扣款費率 创建日期:2018-5-3
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getRate() {
		return this.rate;
	}

	/**
	 * 属性rate的Setter方法.属性名：扣款費率 创建日期:2018-5-3
	 * 
	 * @param newRate
	 *            java.lang.Integer
	 */
	public void setRate(Integer rate) {
		this.rate = rate;
	}

	/**
	 * 属性 pk_leavetype的Getter方法.属性名：假期類別 创建日期:2018-5-3
	 * 
	 * @return java.lang.String
	 */
	public String getPk_leavetype() {
		return this.pk_leavetype;
	}

	/**
	 * 属性pk_leavetype的Setter方法.属性名：假期類別 创建日期:2018-5-3
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
	 * 属性 isleaveoff的Getter方法.属性名：是否銷假 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getIsleaveoff() {
		return this.isleaveoff;
	}

	/**
	 * 属性isleaveoff的Setter方法.属性名：是否銷假 创建日期:2018-5-3
	 * 
	 * @param newIsleaveoff
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIsleaveoff(UFBoolean isleaveoff) {
		this.isleaveoff = isleaveoff;
	}

	/**
	 * 属性 pk_psndoc的Getter方法.属性名：人员基本主键 创建日期:2018-5-3
	 * 
	 * @return nc.vo.hi.psndoc.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * 属性pk_psndoc的Setter方法.属性名：人员基本主键 创建日期:2018-5-3
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.hi.psndoc.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * 属性 pk_psnjob的Getter方法.属性名：人员任职主键 创建日期:2018-5-3
	 * 
	 * @return nc.vo.hi.psndoc.PsnJobVO
	 */
	public String getPk_psnjob() {
		return this.pk_psnjob;
	}

	/**
	 * 属性pk_psnjob的Setter方法.属性名：人员任职主键 创建日期:2018-5-3
	 * 
	 * @param newPk_psnjob
	 *            nc.vo.hi.psndoc.PsnJobVO
	 */
	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	/**
	 * 属性 pk_hrorg的Getter方法.属性名：人力資源組織 创建日期:2018-5-3
	 * 
	 * @return nc.vo.org.HROrgVO
	 */
	public String getPk_hrorg() {
		return this.pk_hrorg;
	}

	/**
	 * 属性pk_hrorg的Setter方法.属性名：人力資源組織 创建日期:2018-5-3
	 * 
	 * @param newPk_hrorg
	 *            nc.vo.org.HROrgVO
	 */
	public void setPk_hrorg(String pk_hrorg) {
		this.pk_hrorg = pk_hrorg;
	}

	/**
	 * 属性 生成上层主键的Getter方法.属性名：上层主键 创建日期:2018-5-3
	 * 
	 * @return String
	 */
	public String getPk_daysalary() {
		return this.pk_daysalary;
	}

	/**
	 * 属性生成上层主键的Setter方法.属性名：上层主键 创建日期:2018-5-3
	 * 
	 * @param newPk_daysalary
	 *            String
	 */
	public void setPk_daysalary(String pk_daysalary) {
		this.pk_daysalary = pk_daysalary;
	}

	/**
	 * 属性 生成时间戳的Getter方法.属性名：时间戳 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 属性生成时间戳的Setter方法.属性名：时间戳 创建日期:2018-5-3
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
