package nc.vo.hrpub.mdmapping;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 * 此处添加累的描述信息
 * </p>
 * 创建日期:2017-9-28
 * 
 * @author
 * @version NCPrj ??
 */

public class MDClassVO extends SuperVO {

	/**
	 * 主键
	 */
	public String pk_mdclass;
	/**
	 * 语义元数据
	 */
	public String pk_class;
	/**
	 * 是否启用
	 */
	public UFBoolean isenabled;
	/**
	 * 导入导出方案
	 */
	public String pk_ioschema;
	/**
	 * 建置日期
	 */
	public UFDate doctime;
	/**
	 * 单据编码
	 */
	public String docno;
	/**
	 * 集团
	 */
	public String pk_group;
	/**
	 * 组织
	 */
	public String pk_org;
	/**
	 * 组织版本
	 */
	public String pk_org_v;
	/**
	 * 创建人
	 */
	public String creator;
	/**
	 * 创建时间
	 */
	public UFDateTime creationtime;
	/**
	 * 修改人
	 */
	public String modifier;
	/**
	 * 修改时间
	 */
	public UFDateTime modifiedtime;
	/**
	 * 时间戳
	 */
	public UFDateTime ts;

	/**
	 * 属性 pk_mdclass的Getter方法.属性名：主键 创建日期:2017-9-28
	 * 
	 * @return java.lang.String
	 */
	public String getPk_mdclass() {
		return this.pk_mdclass;
	}

	/**
	 * 属性pk_mdclass的Setter方法.属性名：主键 创建日期:2017-9-28
	 * 
	 * @param newPk_mdclass
	 *            java.lang.String
	 */
	public void setPk_mdclass(String pk_mdclass) {
		this.pk_mdclass = pk_mdclass;
	}

	/**
	 * 属性 pk_class的Getter方法.属性名：语义元数据 创建日期:2017-9-28
	 * 
	 * @return nc.md.model.impl.BusinessEntity
	 */
	public String getPk_class() {
		return this.pk_class;
	}

	/**
	 * 属性pk_class的Setter方法.属性名：语义元数据 创建日期:2017-9-28
	 * 
	 * @param newPk_class
	 *            nc.md.model.impl.BusinessEntity
	 */
	public void setPk_class(String pk_class) {
		this.pk_class = pk_class;
	}

	/**
	 * 属性 isenabled的Getter方法.属性名：是否启用 创建日期:2017-9-28
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getIsenabled() {
		return this.isenabled;
	}

	/**
	 * 属性isenabled的Setter方法.属性名：是否启用 创建日期:2017-9-28
	 * 
	 * @param newIsenabled
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIsenabled(UFBoolean isenabled) {
		this.isenabled = isenabled;
	}

	/**
	 * 属性 pk_ioschema的Getter方法.属性名：导入导出方案 创建日期:2017-9-28
	 * 
	 * @return nc.vo.hrpub.mdmapping.IOSchemaVO
	 */
	public String getPk_ioschema() {
		return this.pk_ioschema;
	}

	/**
	 * 属性pk_ioschema的Setter方法.属性名：导入导出方案 创建日期:2017-9-28
	 * 
	 * @param newPk_ioschema
	 *            nc.vo.hrpub.mdmapping.IOSchemaVO
	 */
	public void setPk_ioschema(String pk_ioschema) {
		this.pk_ioschema = pk_ioschema;
	}

	/**
	 * 属性 doctime的Getter方法.属性名：建置日期 创建日期:2017-9-28
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getDoctime() {
		return this.doctime;
	}

	/**
	 * 属性doctime的Setter方法.属性名：建置日期 创建日期:2017-9-28
	 * 
	 * @param newDoctime
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setDoctime(UFDate doctime) {
		this.doctime = doctime;
	}

	/**
	 * 属性 docno的Getter方法.属性名：单据编码 创建日期:2017-9-28
	 * 
	 * @return java.lang.String
	 */
	public String getDocno() {
		return this.docno;
	}

	/**
	 * 属性docno的Setter方法.属性名：单据编码 创建日期:2017-9-28
	 * 
	 * @param newDocno
	 *            java.lang.String
	 */
	public void setDocno(String docno) {
		this.docno = docno;
	}

	/**
	 * 属性 pk_group的Getter方法.属性名：集团 创建日期:2017-9-28
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * 属性pk_group的Setter方法.属性名：集团 创建日期:2017-9-28
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * 属性 pk_org的Getter方法.属性名：组织 创建日期:2017-9-28
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * 属性pk_org的Setter方法.属性名：组织 创建日期:2017-9-28
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * 属性 pk_org_v的Getter方法.属性名：组织版本 创建日期:2017-9-28
	 * 
	 * @return nc.vo.vorg.OrgVersionVO
	 */
	public String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * 属性pk_org_v的Setter方法.属性名：组织版本 创建日期:2017-9-28
	 * 
	 * @param newPk_org_v
	 *            nc.vo.vorg.OrgVersionVO
	 */
	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * 属性 creator的Getter方法.属性名：创建人 创建日期:2017-9-28
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getCreator() {
		return this.creator;
	}

	/**
	 * 属性creator的Setter方法.属性名：创建人 创建日期:2017-9-28
	 * 
	 * @param newCreator
	 *            nc.vo.sm.UserVO
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 属性 creationtime的Getter方法.属性名：创建时间 创建日期:2017-9-28
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * 属性creationtime的Setter方法.属性名：创建时间 创建日期:2017-9-28
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * 属性 modifier的Getter方法.属性名：修改人 创建日期:2017-9-28
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getModifier() {
		return this.modifier;
	}

	/**
	 * 属性modifier的Setter方法.属性名：修改人 创建日期:2017-9-28
	 * 
	 * @param newModifier
	 *            nc.vo.sm.UserVO
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * 属性 modifiedtime的Getter方法.属性名：修改时间 创建日期:2017-9-28
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * 属性modifiedtime的Setter方法.属性名：修改时间 创建日期:2017-9-28
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * 属性 生成时间戳的Getter方法.属性名：时间戳 创建日期:2017-9-28
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 属性生成时间戳的Setter方法.属性名：时间戳 创建日期:2017-9-28
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrpub.mdclass");
	}
}
