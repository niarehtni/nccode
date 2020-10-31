package nc.vo.wa.itemgroup;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
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

public class ItemGroupMemberVO extends SuperVO {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 8982133699442264995L;
	/**
	 * 方案項目主鍵
	 */
	public String pk_itemgroupmember;
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
	 * 薪資發放項目
	 */
	public String pk_waitem;
	/**
	 * 上層單據主鍵
	 */
	public String pk_itemgroup;
	/**
	 * 显示次序
	 */
	public Integer orderno;
	/**
	 * 時間戳
	 */
	public UFDateTime ts;

	/**
	 * 屬性 pk_itemgroupmember的Getter方法.屬性名：方案項目主鍵 創建日期:2019/1/17
	 * 
	 * @return java.lang.String
	 */
	public String getPk_itemgroupmember() {
		return this.pk_itemgroupmember;
	}

	/**
	 * 屬性pk_itemgroupmember的Setter方法.屬性名：方案項目主鍵 創建日期:2019/1/17
	 * 
	 * @param newPk_itemgroupmember
	 *            java.lang.String
	 */
	public void setPk_itemgroupmember(String pk_itemgroupmember) {
		this.pk_itemgroupmember = pk_itemgroupmember;
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
	 * 屬性 pk_waitem的Getter方法.屬性名：薪資發放項目 創建日期:2019/1/17
	 * 
	 * @return nc.vo.wa.item.WaItemVO
	 */
	public String getPk_waitem() {
		return this.pk_waitem;
	}

	/**
	 * 屬性pk_waitem的Setter方法.屬性名：薪資發放項目 創建日期:2019/1/17
	 * 
	 * @param newPk_waitem
	 *            nc.vo.wa.item.WaItemVO
	 */
	public void setPk_waitem(String pk_waitem) {
		this.pk_waitem = pk_waitem;
	}

	/**
	 * 屬性 生成上層主鍵的Getter方法.屬性名：上層主鍵 創建日期:2019/1/17
	 * 
	 * @return String
	 */
	public String getPk_itemgroup() {
		return this.pk_itemgroup;
	}

	/**
	 * 屬性生成上層主鍵的Setter方法.屬性名：上層主鍵 創建日期:2019/1/17
	 * 
	 * @param newPk_itemgroup
	 *            String
	 */
	public void setPk_itemgroup(String pk_itemgroup) {
		this.pk_itemgroup = pk_itemgroup;
	}

	/**
	 * 屬性 生成上層主鍵的Getter方法.屬性名：顯示次序 創建日期:2020/2/29
	 * 
	 * @return int
	 */
	public Integer getOrderno() {
		return orderno;
	}

	/**
	 * 屬性生成上層主鍵的Setter方法.屬性名：顯示次序 創建日期:2020/2/29
	 * 
	 * @param orderno
	 *            orderno int
	 */
	public void setOrderno(Integer orderno) {
		this.orderno = orderno;
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
		return VOMetaFactory.getInstance().getVOMeta("wa.itemgroupmember");
	}
}
