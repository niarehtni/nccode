package nc.vo.twhr.twhr_declaration;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此處簡要描述此類功能 </b>
 * <p>
 * 此處添加累的描述信息
 * </p>
 * 創建日期:2020/7/29
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class CompanyAdjustBVO extends SuperVO {

	/**
	 * 公司補充保費調整
	 */
	public String pk_companyadj;
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
	 * 行號
	 */
	public String rowno;
	/**
	 * 調整日期
	 */
	public UFLiteralDate adjustdate;
	/**
	 * 人員
	 */
	public String pk_psndoc;
	/**
	 * 投保調整金額
	 */
	public UFDouble adjustamount;
	/**
	 * 調整原因
	 */
	public String adjustreason;
	/**
	 * 上層單據主鍵
	 */
	public String pk_declaration;
	/**
	 * 時間戳
	 */
	public UFDateTime ts;

	/**
	 * 屬性 pk_companyadj的Getter方法.屬性名：公司補充保費調整 創建日期:2020/7/29
	 * 
	 * @return java.lang.String
	 */
	public String getPk_companyadj() {
		return this.pk_companyadj;
	}

	/**
	 * 屬性pk_companyadj的Setter方法.屬性名：公司補充保費調整 創建日期:2020/7/29
	 * 
	 * @param newPk_companyadj
	 *            java.lang.String
	 */
	public void setPk_companyadj(String pk_companyadj) {
		this.pk_companyadj = pk_companyadj;
	}

	/**
	 * 屬性 pk_group的Getter方法.屬性名：集團 創建日期:2020/7/29
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * 屬性pk_group的Setter方法.屬性名：集團 創建日期:2020/7/29
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * 屬性 pk_org的Getter方法.屬性名：組織 創建日期:2020/7/29
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * 屬性pk_org的Setter方法.屬性名：組織 創建日期:2020/7/29
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * 屬性 pk_org_v的Getter方法.屬性名：組織版本 創建日期:2020/7/29
	 * 
	 * @return nc.vo.vorg.OrgVersionVO
	 */
	public String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * 屬性pk_org_v的Setter方法.屬性名：組織版本 創建日期:2020/7/29
	 * 
	 * @param newPk_org_v
	 *            nc.vo.vorg.OrgVersionVO
	 */
	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * 屬性 rowno的Getter方法.屬性名：行號 創建日期:2020/7/29
	 * 
	 * @return java.lang.String
	 */
	public String getRowno() {
		return this.rowno;
	}

	/**
	 * 屬性rowno的Setter方法.屬性名：行號 創建日期:2020/7/29
	 * 
	 * @param newRowno
	 *            java.lang.String
	 */
	public void setRowno(String rowno) {
		this.rowno = rowno;
	}

	/**
	 * 屬性 adjustdate的Getter方法.屬性名：調整日期 創建日期:2020/7/29
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public UFLiteralDate getAdjustdate() {
		return this.adjustdate;
	}

	/**
	 * 屬性adjustdate的Setter方法.屬性名：調整日期 創建日期:2020/7/29
	 * 
	 * @param newAdjustdate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setAdjustdate(UFLiteralDate adjustdate) {
		this.adjustdate = adjustdate;
	}

	/**
	 * 屬性 pk_psndoc的Getter方法.屬性名：人員 創建日期:2020/7/29
	 * 
	 * @return nc.vo.hi.psndoc.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * 屬性pk_psndoc的Setter方法.屬性名：人員 創建日期:2020/7/29
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.hi.psndoc.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * 屬性 adjustamount的Getter方法.屬性名：投保調整金額 創建日期:2020/7/29
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getAdjustamount() {
		return this.adjustamount;
	}

	/**
	 * 屬性adjustamount的Setter方法.屬性名：投保調整金額 創建日期:2020/7/29
	 * 
	 * @param newAdjustamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setAdjustamount(UFDouble adjustamount) {
		this.adjustamount = adjustamount;
	}

	/**
	 * 屬性 adjustreason的Getter方法.屬性名：調整原因 創建日期:2020/7/29
	 * 
	 * @return java.lang.String
	 */
	public String getAdjustreason() {
		return this.adjustreason;
	}

	/**
	 * 屬性adjustreason的Setter方法.屬性名：調整原因 創建日期:2020/7/29
	 * 
	 * @param newAdjustreason
	 *            java.lang.String
	 */
	public void setAdjustreason(String adjustreason) {
		this.adjustreason = adjustreason;
	}

	/**
	 * 屬性 生成上層主鍵的Getter方法.屬性名：上層主鍵 創建日期:2020/7/29
	 * 
	 * @return String
	 */
	public String getPk_declaration() {
		return this.pk_declaration;
	}

	/**
	 * 屬性生成上層主鍵的Setter方法.屬性名：上層主鍵 創建日期:2020/7/29
	 * 
	 * @param newPk_declaration
	 *            String
	 */
	public void setPk_declaration(String pk_declaration) {
		this.pk_declaration = pk_declaration;
	}

	/**
	 * 屬性 生成時間戳的Getter方法.屬性名：時間戳 創建日期:2020/7/29
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 屬性生成時間戳的Setter方法.屬性名：時間戳 創建日期:2020/7/29
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("twhr.CompanyAdjustBVO");
	}
}
