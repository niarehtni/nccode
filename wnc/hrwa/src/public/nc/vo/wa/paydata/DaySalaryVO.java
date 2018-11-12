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

public class DaySalaryVO extends SuperVO {

	/**
	 * 日薪計算主鍵
	 */
	public String pk_daysalary;
	/**
	 * 全薪
	 */
	public UFDouble fullsalary = UFDouble.ZERO_DBL;
	/**
	 * 日薪
	 */
	public UFDouble daysalary = UFDouble.ZERO_DBL;
	/**
	 * 時薪
	 */
	public UFDouble hoursalary = UFDouble.ZERO_DBL;
	/**
	 * 加班時長
	 */
	public UFDouble overtime = UFDouble.ZERO_DBL;
	/**
	 * 平日加班时长
	 */
	public UFDouble dailyovertime = UFDouble.ZERO_DBL;
	/**
	 * 休息日加班時長
	 */
	public UFDouble restovertime = UFDouble.ZERO_DBL;
	/**
	 * 國定假日加班時長
	 */
	public UFDouble holidayovertime = UFDouble.ZERO_DBL;
	/**
	 * 加班費
	 */
	public UFDouble overtimesalary = UFDouble.ZERO_DBL;
	/**
	 * 請假扣款
	 */
	public UFDouble leavecharge = UFDouble.ZERO_DBL;
	/**
	 * 人员基本主键
	 */
	public String pk_psndoc;
	/**
	 * 人员任职主键
	 */
	public String pk_psnjob;
	/**
	 * 组织关系主键
	 */
	public String pk_psnorg;
	/**
	 * 部门
	 */
	public String pk_dept;
	/**
	 * 部门版本信息
	 */
	public String pk_dept_v;
	/**
	 * 人力資源組織
	 */
	public String pk_hrorg;
	/**
	 * 會計年
	 */
	public String cyear;
	/**
	 * 會計月
	 */
	public String cperiod;
	/**
	 * 薪資日期
	 */
	public UFLiteralDate salarydate;
	/**
	 * 製單人
	 */
	public String billmaker;
	/**
	 * 制單時間
	 */
	public UFDateTime maketime;
	/**
	 * 所属集团
	 */
	public String pk_group;
	/**
	 * 所属组织
	 */
	public String pk_org;

	public String pk_org_v;
	/**
	 * 时间戳
	 */
	public UFDateTime ts;

	/**
	 * 属性 pk_daysalary的Getter方法.属性名：日薪計算主鍵 创建日期:2018-5-3
	 * 
	 * @return java.lang.String
	 */
	public String getPk_daysalary() {
		return this.pk_daysalary;
	}

	/**
	 * 属性pk_daysalary的Setter方法.属性名：日薪計算主鍵 创建日期:2018-5-3
	 * 
	 * @param newPk_daysalary
	 *            java.lang.String
	 */
	public void setPk_daysalary(String pk_daysalary) {
		this.pk_daysalary = pk_daysalary;
	}

	/**
	 * 属性 fullsalary的Getter方法.属性名：全薪 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getFullsalary() {
		return this.fullsalary;
	}

	/**
	 * 属性fullsalary的Setter方法.属性名：全薪 创建日期:2018-5-3
	 * 
	 * @param newFullsalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setFullsalary(UFDouble fullsalary) {
		this.fullsalary = fullsalary;
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
	 * 属性 hoursalary的Getter方法.属性名：時薪 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHoursalary() {
		return this.hoursalary;
	}

	/**
	 * 属性hoursalary的Setter方法.属性名：時薪 创建日期:2018-5-3
	 * 
	 * @param newHoursalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHoursalary(UFDouble hoursalary) {
		this.hoursalary = hoursalary;
	}

	/**
	 * 属性 overtime的Getter方法.属性名：加班時長 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getOvertime() {
		return this.overtime;
	}

	/**
	 * 属性overtime的Setter方法.属性名：加班時長 创建日期:2018-5-3
	 * 
	 * @param newOvertime
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOvertime(UFDouble overtime) {
		this.overtime = overtime;
	}

	/**
	 * 属性 dailyovertime的Getter方法.属性名：平日加班时长 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getDailyovertime() {
		return this.dailyovertime;
	}

	/**
	 * 属性dailyovertime的Setter方法.属性名：平日加班时长 创建日期:2018-5-3
	 * 
	 * @param newDailyovertime
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDailyovertime(UFDouble dailyovertime) {
		this.dailyovertime = dailyovertime;
	}

	/**
	 * 属性 restovertime的Getter方法.属性名：休息日加班時長 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getRestovertime() {
		return this.restovertime;
	}

	/**
	 * 属性restovertime的Setter方法.属性名：休息日加班時長 创建日期:2018-5-3
	 * 
	 * @param newRestovertime
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRestovertime(UFDouble restovertime) {
		this.restovertime = restovertime;
	}

	/**
	 * 属性 holidayovertime的Getter方法.属性名：國定假日加班時長 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHolidayovertime() {
		return this.holidayovertime;
	}

	/**
	 * 属性holidayovertime的Setter方法.属性名：國定假日加班時長 创建日期:2018-5-3
	 * 
	 * @param newHolidayovertime
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHolidayovertime(UFDouble holidayovertime) {
		this.holidayovertime = holidayovertime;
	}

	/**
	 * 属性 overtimesalary的Getter方法.属性名：加班費 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getOvertimesalary() {
		return this.overtimesalary;
	}

	/**
	 * 属性overtimesalary的Setter方法.属性名：加班費 创建日期:2018-5-3
	 * 
	 * @param newOvertimesalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOvertimesalary(UFDouble overtimesalary) {
		this.overtimesalary = overtimesalary;
	}

	/**
	 * 属性 leavecharge的Getter方法.属性名：請假扣款 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLeavecharge() {
		return this.leavecharge;
	}

	/**
	 * 属性leavecharge的Setter方法.属性名：請假扣款 创建日期:2018-5-3
	 * 
	 * @param newLeavecharge
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLeavecharge(UFDouble leavecharge) {
		this.leavecharge = leavecharge;
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
	 * 属性 pk_psnorg的Getter方法.属性名：组织关系主键 创建日期:2018-5-3
	 * 
	 * @return java.lang.String
	 */
	public String getPk_psnorg() {
		return this.pk_psnorg;
	}

	/**
	 * 属性pk_psnorg的Setter方法.属性名：组织关系主键 创建日期:2018-5-3
	 * 
	 * @param newPk_psnorg
	 *            java.lang.String
	 */
	public void setPk_psnorg(String pk_psnorg) {
		this.pk_psnorg = pk_psnorg;
	}

	/**
	 * 属性 pk_dept的Getter方法.属性名：部门 创建日期:2018-5-3
	 * 
	 * @return nc.vo.om.hrdept.HRDeptVO
	 */
	public String getPk_dept() {
		return this.pk_dept;
	}

	/**
	 * 属性pk_dept的Setter方法.属性名：部门 创建日期:2018-5-3
	 * 
	 * @param newPk_dept
	 *            nc.vo.om.hrdept.HRDeptVO
	 */
	public void setPk_dept(String pk_dept) {
		this.pk_dept = pk_dept;
	}

	/**
	 * 属性 pk_dept_v的Getter方法.属性名：部门版本信息 创建日期:2018-5-3
	 * 
	 * @return nc.vo.om.hrdept.HRDeptVersionVO
	 */
	public String getPk_dept_v() {
		return this.pk_dept_v;
	}

	/**
	 * 属性pk_dept_v的Setter方法.属性名：部门版本信息 创建日期:2018-5-3
	 * 
	 * @param newPk_dept_v
	 *            nc.vo.om.hrdept.HRDeptVersionVO
	 */
	public void setPk_dept_v(String pk_dept_v) {
		this.pk_dept_v = pk_dept_v;
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
	 * 属性 salarydate的Getter方法.属性名：薪資日期 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public UFLiteralDate getSalarydate() {
		return this.salarydate;
	}

	/**
	 * 属性salarydate的Setter方法.属性名：薪資日期 创建日期:2018-5-3
	 * 
	 * @param newSalarydate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setSalarydate(UFLiteralDate salarydate) {
		this.salarydate = salarydate;
	}

	/**
	 * 属性 billmaker的Getter方法.属性名：製單人 创建日期:2018-5-3
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getBillmaker() {
		return this.billmaker;
	}

	/**
	 * 属性billmaker的Setter方法.属性名：製單人 创建日期:2018-5-3
	 * 
	 * @param newBillmaker
	 *            nc.vo.sm.UserVO
	 */
	public void setBillmaker(String billmaker) {
		this.billmaker = billmaker;
	}

	/**
	 * 属性 maketime的Getter方法.属性名：制單時間 创建日期:2018-5-3
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getMaketime() {
		return this.maketime;
	}

	/**
	 * 属性maketime的Setter方法.属性名：制單時間 创建日期:2018-5-3
	 * 
	 * @param newMaketime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setMaketime(UFDateTime maketime) {
		this.maketime = maketime;
	}

	/**
	 * 属性 pk_group的Getter方法.属性名：所属集团 创建日期:2018-5-3
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * 属性pk_group的Setter方法.属性名：所属集团 创建日期:2018-5-3
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * 属性 pk_org的Getter方法.属性名：所属组织 创建日期:2018-5-3
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * 属性pk_org的Setter方法.属性名：所属组织 创建日期:2018-5-3
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_org_v() {
		return pk_org_v;
	}

	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}
	
	

	public String getCyear() {
		return cyear;
	}

	public void setCyear(String cyear) {
		this.cyear = cyear;
	}

	public String getCperiod() {
		return cperiod;
	}

	public void setCperiod(String cperiod) {
		this.cperiod = cperiod;
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
		return VOMetaFactory.getInstance().getVOMeta("hrwa.DaySalaryVO");
	}
}
