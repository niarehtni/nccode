package nc.vo.wa.itemgroup;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此處簡要描述此類功能 </b>
 * <p>
 * 此處添加累的描述信息
 * </p>
 * 創建日期:2019/1/17
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class ItemGroupVO extends SuperVO {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = -8617671579163700417L;
	/**
	 * 主鍵
	 */
	public String pk_itemgroup;
	/**
	 * 集團
	 */
	public String pk_group;
	/**
	 * 組織
	 */
	public String pk_org;
	/**
	 * 組織版本
	 */
	public String pk_org_v;
	/**
	 * 創建人
	 */
	public String creator;
	/**
	 * 創建時間
	 */
	public UFDateTime creationtime;
	/**
	 * 修改人
	 */
	public String modifier;
	/**
	 * 修改時間
	 */
	public UFDateTime modifiedtime;
	/**
	 * 方案編碼
	 */
	public String groupcode;
	/**
	 * 方案名稱
	 */
	public String groupname;
	/**
	 * 參与日薪計算
	 */
	public UFBoolean isdaysalarygroup;
	/**
	 * 建置日期
	 */
	public UFDate billdate;
	/**
	 * 月薪天數來源
	 */
	public Integer daysource;
	/**
	 * 是否啟用
	 */
	public UFBoolean isenabled;
	/**
	 * 時間戳
	 */
	public UFDateTime ts;

	/**
	 * 屬性 pk_itemgroup的Getter方法.屬性名：主鍵 創建日期:2019/1/17
	 * 
	 * @return java.lang.String
	 */
	public String getPk_itemgroup() {
		return this.pk_itemgroup;
	}

	/**
	 * 屬性pk_itemgroup的Setter方法.屬性名：主鍵 創建日期:2019/1/17
	 * 
	 * @param newPk_itemgroup
	 *            java.lang.String
	 */
	public void setPk_itemgroup(String pk_itemgroup) {
		this.pk_itemgroup = pk_itemgroup;
	}

	/**
	 * 屬性 pk_group的Getter方法.屬性名：集團 創建日期:2019/1/17
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * 屬性pk_group的Setter方法.屬性名：集團 創建日期:2019/1/17
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * 屬性 pk_org的Getter方法.屬性名：組織 創建日期:2019/1/17
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * 屬性pk_org的Setter方法.屬性名：組織 創建日期:2019/1/17
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * 屬性 pk_org_v的Getter方法.屬性名：組織版本 創建日期:2019/1/17
	 * 
	 * @return nc.vo.vorg.OrgVersionVO
	 */
	public String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * 屬性pk_org_v的Setter方法.屬性名：組織版本 創建日期:2019/1/17
	 * 
	 * @param newPk_org_v
	 *            nc.vo.vorg.OrgVersionVO
	 */
	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * 屬性 creator的Getter方法.屬性名：創建人 創建日期:2019/1/17
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getCreator() {
		return this.creator;
	}

	/**
	 * 屬性creator的Setter方法.屬性名：創建人 創建日期:2019/1/17
	 * 
	 * @param newCreator
	 *            nc.vo.sm.UserVO
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 屬性 creationtime的Getter方法.屬性名：創建時間 創建日期:2019/1/17
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * 屬性creationtime的Setter方法.屬性名：創建時間 創建日期:2019/1/17
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * 屬性 modifier的Getter方法.屬性名：修改人 創建日期:2019/1/17
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getModifier() {
		return this.modifier;
	}

	/**
	 * 屬性modifier的Setter方法.屬性名：修改人 創建日期:2019/1/17
	 * 
	 * @param newModifier
	 *            nc.vo.sm.UserVO
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * 屬性 modifiedtime的Getter方法.屬性名：修改時間 創建日期:2019/1/17
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * 屬性modifiedtime的Setter方法.屬性名：修改時間 創建日期:2019/1/17
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * 屬性 groupcode的Getter方法.屬性名：方案編碼 創建日期:2019/1/17
	 * 
	 * @return java.lang.String
	 */
	public String getGroupcode() {
		return this.groupcode;
	}

	/**
	 * 屬性groupcode的Setter方法.屬性名：方案編碼 創建日期:2019/1/17
	 * 
	 * @param newGroupcode
	 *            java.lang.String
	 */
	public void setGroupcode(String groupcode) {
		this.groupcode = groupcode;
	}

	/**
	 * 屬性 groupname的Getter方法.屬性名：方案名稱 創建日期:2019/1/17
	 * 
	 * @return java.lang.String
	 */
	public String getGroupname() {
		return this.groupname;
	}

	/**
	 * 屬性groupname的Setter方法.屬性名：方案名稱 創建日期:2019/1/17
	 * 
	 * @param newGroupname
	 *            java.lang.String
	 */
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	/**
	 * 屬性 isdaysalarygroup的Getter方法.屬性名：參与日薪計算 創建日期:2019/1/17
	 * 
	 * @return nc.vo.pub.lang.UFUFBoolean
	 */
	public UFBoolean getIsdaysalarygroup() {
		return this.isdaysalarygroup;
	}

	/**
	 * 屬性isdaysalarygroup的Setter方法.屬性名：參与日薪計算 創建日期:2019/1/17
	 * 
	 * @param newIsdaysalarygroup
	 *            nc.vo.pub.lang.UFUFBoolean
	 */
	public void setIsdaysalarygroup(UFBoolean isdaysalarygroup) {
		this.isdaysalarygroup = isdaysalarygroup;
	}

	/**
	 * 屬性 billdate的Getter方法.屬性名：建置日期 創建日期:2019/1/17
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getBilldate() {
		return this.billdate;
	}

	/**
	 * 屬性billdate的Setter方法.屬性名：建置日期 創建日期:2019/1/17
	 * 
	 * @param newBilldate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setBilldate(UFDate billdate) {
		this.billdate = billdate;
	}

	/**
	 * 屬性 daysource的Getter方法.屬性名：月薪天數來源 創建日期:2019/1/17
	 * 
	 * @return nc.vo.wa.waitemgroup.DayScopeEnum
	 */
	public Integer getDaysource() {
		return this.daysource;
	}

	/**
	 * 屬性daysource的Setter方法.屬性名：月薪天數來源 創建日期:2019/1/17
	 * 
	 * @param newDaysource
	 *            nc.vo.wa.waitemgroup.DayScopeEnum
	 */
	public void setDaysource(Integer daysource) {
		this.daysource = daysource;
	}

	/**
	 * 屬性 isenabled的Getter方法.屬性名：是否啟用 創建日期:2019/1/17
	 * 
	 * @return nc.vo.pub.lang.UFUFBoolean
	 */
	public UFBoolean getIsenabled() {
		return this.isenabled;
	}

	/**
	 * 屬性isenabled的Setter方法.屬性名：是否啟用 創建日期:2019/1/17
	 * 
	 * @param newIsenabled
	 *            nc.vo.pub.lang.UFUFBoolean
	 */
	public void setIsenabled(UFBoolean isenabled) {
		this.isenabled = isenabled;
	}

	/**
	 * 屬性 生成時間戳的Getter方法.屬性名：時間戳 創建日期:2019/1/17
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 屬性生成時間戳的Setter方法.屬性名：時間戳 創建日期:2019/1/17
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("wa.itemgroup");
	}
}
