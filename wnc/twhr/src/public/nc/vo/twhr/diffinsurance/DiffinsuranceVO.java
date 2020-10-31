package nc.vo.twhr.diffinsurance;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 *   此处添加累的描述信息
 * </p>
 *  创建日期:2019-10-22
 * @author YONYOU NC
 * @version NCPrj ??
 */
 
public class DiffinsuranceVO extends SuperVO {
	

//補扣健保個人承擔金額
public static final String HEATH_SUPPLEINS="heath_suppleins";
//退還健保個人承擔金額
public static final String HEATH_RETURNNINS="heath_returnnins";
//補扣健保費
public static final String HEATH_SYS_SUPPLEINS="heath_sys_suppleins";
//退還健保費
public static final String HEATH_SYS_RETURNINS="heath_sys_returnins";
//補扣勞保個人承擔金額
public static final String LABOR_SUPPLEINS="labor_suppleins";
//退還勞保個人承擔金額
public static final String LABOR_RETURNNINS="labor_returnnins";
//補扣勞退個人承擔金額
public static final String RETIRE_SUPPLEINS="retire_suppleins";
//退還勞退個人承擔金額
public static final String RETIRE_RETURNNINS="retire_returnnins";


/**
*差异分析行主键
*/
public String id;
/**
*集團
*/
public String pk_group;
/**
*组织
*/
public String pk_org;
/**
*组织版本
*/
public String pk_org_v;
/**
*薪资期间
*/
public String pk_period;
/**
*人员档案
*/
public String pk_psndoc;
/**
*投保人姓名
*/
public String psnname;
/**
*称谓
*/
public String appellation;
/**
*身份证号
*/
public String idno;
/**
*健保投保級距
*/
public UFDouble heath_grade;
/**
*健保個人承擔金額_系統_總額
*/
public UFDouble heath_psnins;
/**
*健保個人承擔金額_對帳單_總額
*/
public UFDouble heath_psnins_import;
/**
*補扣健保個人承擔金額
*/
public UFDouble heath_suppleins;
/**
*退還健保個人承擔金額
*/
public UFDouble heath_returnnins;
/**
*健保公司承擔金額_系統
*/
public UFDouble heath_orgins;
/**
*健保公司承擔金額_對帳單
*/
public UFDouble heath_orgins_import;
/**
*健保費_系統
*/
public UFDouble heath_sys_ins;
/**
*補扣健保費
*/
public UFDouble heath_sys_suppleins;
/**
*退還健保費
*/
public UFDouble heath_sys_returnins;
/**
*健保費_對帳單
*/
public UFDouble heath_sys_ins_import;
/**
*勞保投保級距
*/
public UFDouble labor_grade;
/**
*勞保個人承擔金額_系統
*/
public UFDouble labor_psnins;
/**
*勞保個人承擔金額_對帳單
*/
public UFDouble labor_psnins_import;
/**
*補扣勞保個人承擔金額
*/
public UFDouble labor_suppleins;
/**
*退還勞保個人承擔金額
*/
public UFDouble labor_returnnins;
/**
*勞保公司承擔金額_系統
*/
public UFDouble labor_orgins;
/**
*勞保公司承擔金額_對帳單
*/
public UFDouble labor_orgins_import;
/**
*勞退投保級距
*/
public UFDouble retire_grade;
/**
*勞退個人承擔金額_系統
*/
public UFDouble retire_psnins;
/**
*勞退個人承擔金額_對帳單
*/
public UFDouble retire_psnins_import;
/**
*補扣勞退個人承擔金額
*/
public UFDouble retire_suppleins;
/**
*退還勞退個人承擔金額
*/
public UFDouble retire_returnnins;
/**
*创建人
*/
public String creator;
/**
*创建时间
*/
public UFDateTime creationtime;
/**
*修改人
*/
public String modifier;
/**
*修改时间
*/
public UFDateTime modifiedtime;
/**
*时间戳
*/
public UFDateTime ts;
    
private int dr;


public int getDr() {
	return dr;
}

public void setDr(int dr) {
	this.dr = dr;
}

/**
* 属性 id的Getter方法.属性名：差异分析行主键
*  创建日期:2019-10-22
* @return java.lang.String
*/
public String getId() {
return this.id;
} 

/**
* 属性id的Setter方法.属性名：差异分析行主键
* 创建日期:2019-10-22
* @param newId java.lang.String
*/
public void setId ( String id) {
this.id=id;
} 
 
/**
* 属性 pk_group的Getter方法.属性名：集團
*  创建日期:2019-10-22
* @return nc.vo.org.GroupVO
*/
public String getPk_group() {
return this.pk_group;
} 

/**
* 属性pk_group的Setter方法.属性名：集團
* 创建日期:2019-10-22
* @param newPk_group nc.vo.org.GroupVO
*/
public void setPk_group ( String pk_group) {
this.pk_group=pk_group;
} 
 
/**
* 属性 pk_org的Getter方法.属性名：组织
*  创建日期:2019-10-22
* @return nc.vo.org.HROrgVO
*/
public String getPk_org() {
return this.pk_org;
} 

/**
* 属性pk_org的Setter方法.属性名：组织
* 创建日期:2019-10-22
* @param newPk_org nc.vo.org.HROrgVO
*/
public void setPk_org ( String pk_org) {
this.pk_org=pk_org;
} 
 
/**
* 属性 pk_org_v的Getter方法.属性名：组织版本
*  创建日期:2019-10-22
* @return nc.vo.vorg.OrgVersionVO
*/
public String getPk_org_v() {
return this.pk_org_v;
} 

/**
* 属性pk_org_v的Setter方法.属性名：组织版本
* 创建日期:2019-10-22
* @param newPk_org_v nc.vo.vorg.OrgVersionVO
*/
public void setPk_org_v ( String pk_org_v) {
this.pk_org_v=pk_org_v;
} 
 
/**
* 属性 pk_period的Getter方法.属性名：薪资期间
*  创建日期:2019-10-22
* @return java.lang.String
*/
public String getPk_period() {
return this.pk_period;
} 

/**
* 属性pk_period的Setter方法.属性名：薪资期间
* 创建日期:2019-10-22
* @param newPk_period java.lang.String
*/
public void setPk_period ( String pk_period) {
this.pk_period=pk_period;
} 
 
/**
* 属性 pk_psndoc的Getter方法.属性名：人员档案
*  创建日期:2019-10-22
* @return nc.vo.bd.psn.PsndocVO
*/
public String getPk_psndoc() {
return this.pk_psndoc;
} 

/**
* 属性pk_psndoc的Setter方法.属性名：人员档案
* 创建日期:2019-10-22
* @param newPk_psndoc nc.vo.bd.psn.PsndocVO
*/
public void setPk_psndoc ( String pk_psndoc) {
this.pk_psndoc=pk_psndoc;
} 
 
/**
* 属性 psnname的Getter方法.属性名：投保人姓名
*  创建日期:2019-10-22
* @return java.lang.String
*/
public String getPsnname() {
return this.psnname;
} 

/**
* 属性psnname的Setter方法.属性名：投保人姓名
* 创建日期:2019-10-22
* @param newPsnname java.lang.String
*/
public void setPsnname ( String psnname) {
this.psnname=psnname;
} 
 
/**
* 属性 appellation的Getter方法.属性名：称谓
*  创建日期:2019-10-22
* @return java.lang.String
*/
public String getAppellation() {
return this.appellation;
} 

/**
* 属性appellation的Setter方法.属性名：称谓
* 创建日期:2019-10-22
* @param newAppellation java.lang.String
*/
public void setAppellation ( String appellation) {
this.appellation=appellation;
} 
 
/**
* 属性 idno的Getter方法.属性名：身份证号
*  创建日期:2019-10-22
* @return java.lang.String
*/
public String getIdno() {
return this.idno;
} 

/**
* 属性idno的Setter方法.属性名：身份证号
* 创建日期:2019-10-22
* @param newIdno java.lang.String
*/
public void setIdno ( String idno) {
this.idno=idno;
} 
 
/**
* 属性 heath_grade的Getter方法.属性名：健保投保級距
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_grade() {
return this.heath_grade;
} 

/**
* 属性heath_grade的Setter方法.属性名：健保投保級距
* 创建日期:2019-10-22
* @param newHeath_grade nc.vo.pub.lang.UFDouble
*/
public void setHeath_grade ( UFDouble heath_grade) {
this.heath_grade=heath_grade;
} 
 
/**
* 属性 heath_psnins的Getter方法.属性名：健保個人承擔金額_系統_總額
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_psnins() {
return this.heath_psnins;
} 

/**
* 属性heath_psnins的Setter方法.属性名：健保個人承擔金額_系統_總額
* 创建日期:2019-10-22
* @param newHeath_psnins nc.vo.pub.lang.UFDouble
*/
public void setHeath_psnins ( UFDouble heath_psnins) {
this.heath_psnins=heath_psnins;
} 
 
/**
* 属性 heath_psnins_import的Getter方法.属性名：健保個人承擔金額_對帳單_總額
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_psnins_import() {
return this.heath_psnins_import;
} 

/**
* 属性heath_psnins_import的Setter方法.属性名：健保個人承擔金額_對帳單_總額
* 创建日期:2019-10-22
* @param newHeath_psnins_import nc.vo.pub.lang.UFDouble
*/
public void setHeath_psnins_import ( UFDouble heath_psnins_import) {
this.heath_psnins_import=heath_psnins_import;
} 
 
/**
* 属性 heath_suppleins的Getter方法.属性名：補扣健保個人承擔金額
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_suppleins() {
return this.heath_suppleins;
} 

/**
* 属性heath_suppleins的Setter方法.属性名：補扣健保個人承擔金額
* 创建日期:2019-10-22
* @param newHeath_suppleins nc.vo.pub.lang.UFDouble
*/
public void setHeath_suppleins ( UFDouble heath_suppleins) {
this.heath_suppleins=heath_suppleins;
} 
 
/**
* 属性 heath_returnnins的Getter方法.属性名：退還健保個人承擔金額
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_returnnins() {
return this.heath_returnnins;
} 

/**
* 属性heath_returnnins的Setter方法.属性名：退還健保個人承擔金額
* 创建日期:2019-10-22
* @param newHeath_returnnins nc.vo.pub.lang.UFDouble
*/
public void setHeath_returnnins ( UFDouble heath_returnnins) {
this.heath_returnnins=heath_returnnins;
} 
 
/**
* 属性 heath_orgins的Getter方法.属性名：健保公司承擔金額_系統
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_orgins() {
return this.heath_orgins;
} 

/**
* 属性heath_orgins的Setter方法.属性名：健保公司承擔金額_系統
* 创建日期:2019-10-22
* @param newHeath_orgins nc.vo.pub.lang.UFDouble
*/
public void setHeath_orgins ( UFDouble heath_orgins) {
this.heath_orgins=heath_orgins;
} 
 
/**
* 属性 heath_orgins_import的Getter方法.属性名：健保公司承擔金額_對帳單
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_orgins_import() {
return this.heath_orgins_import;
} 

/**
* 属性heath_orgins_import的Setter方法.属性名：健保公司承擔金額_對帳單
* 创建日期:2019-10-22
* @param newHeath_orgins_import nc.vo.pub.lang.UFDouble
*/
public void setHeath_orgins_import ( UFDouble heath_orgins_import) {
this.heath_orgins_import=heath_orgins_import;
} 
 
/**
* 属性 heath_sys_ins的Getter方法.属性名：健保費_系統
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_sys_ins() {
return this.heath_sys_ins;
} 

/**
* 属性heath_sys_ins的Setter方法.属性名：健保費_系統
* 创建日期:2019-10-22
* @param newHeath_sys_ins nc.vo.pub.lang.UFDouble
*/
public void setHeath_sys_ins ( UFDouble heath_sys_ins) {
this.heath_sys_ins=heath_sys_ins;
} 
 
/**
* 属性 heath_sys_suppleins的Getter方法.属性名：補扣健保費
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_sys_suppleins() {
return this.heath_sys_suppleins;
} 

/**
* 属性heath_sys_suppleins的Setter方法.属性名：補扣健保費
* 创建日期:2019-10-22
* @param newHeath_sys_suppleins nc.vo.pub.lang.UFDouble
*/
public void setHeath_sys_suppleins ( UFDouble heath_sys_suppleins) {
this.heath_sys_suppleins=heath_sys_suppleins;
} 
 
/**
* 属性 heath_sys_returnins的Getter方法.属性名：退還健保費
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_sys_returnins() {
return this.heath_sys_returnins;
} 

/**
* 属性heath_sys_returnins的Setter方法.属性名：退還健保費
* 创建日期:2019-10-22
* @param newHeath_sys_returnins nc.vo.pub.lang.UFDouble
*/
public void setHeath_sys_returnins ( UFDouble heath_sys_returnins) {
this.heath_sys_returnins=heath_sys_returnins;
} 
 
/**
* 属性 heath_sys_ins_import的Getter方法.属性名：健保費_對帳單
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_sys_ins_import() {
return this.heath_sys_ins_import;
} 

/**
* 属性heath_sys_ins_import的Setter方法.属性名：健保費_對帳單
* 创建日期:2019-10-22
* @param newHeath_sys_ins_import nc.vo.pub.lang.UFDouble
*/
public void setHeath_sys_ins_import ( UFDouble heath_sys_ins_import) {
this.heath_sys_ins_import=heath_sys_ins_import;
} 
 
/**
* 属性 labor_grade的Getter方法.属性名：勞保投保級距
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_grade() {
return this.labor_grade;
} 

/**
* 属性labor_grade的Setter方法.属性名：勞保投保級距
* 创建日期:2019-10-22
* @param newLabor_grade nc.vo.pub.lang.UFDouble
*/
public void setLabor_grade ( UFDouble labor_grade) {
this.labor_grade=labor_grade;
} 
 
/**
* 属性 labor_psnins的Getter方法.属性名：勞保個人承擔金額_系統
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_psnins() {
return this.labor_psnins;
} 

/**
* 属性labor_psnins的Setter方法.属性名：勞保個人承擔金額_系統
* 创建日期:2019-10-22
* @param newLabor_psnins nc.vo.pub.lang.UFDouble
*/
public void setLabor_psnins ( UFDouble labor_psnins) {
this.labor_psnins=labor_psnins;
} 
 
/**
* 属性 labor_psnins_import的Getter方法.属性名：勞保個人承擔金額_對帳單
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_psnins_import() {
return this.labor_psnins_import;
} 

/**
* 属性labor_psnins_import的Setter方法.属性名：勞保個人承擔金額_對帳單
* 创建日期:2019-10-22
* @param newLabor_psnins_import nc.vo.pub.lang.UFDouble
*/
public void setLabor_psnins_import ( UFDouble labor_psnins_import) {
this.labor_psnins_import=labor_psnins_import;
} 
 
/**
* 属性 labor_suppleins的Getter方法.属性名：補扣勞保個人承擔金額
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_suppleins() {
return this.labor_suppleins;
} 

/**
* 属性labor_suppleins的Setter方法.属性名：補扣勞保個人承擔金額
* 创建日期:2019-10-22
* @param newLabor_suppleins nc.vo.pub.lang.UFDouble
*/
public void setLabor_suppleins ( UFDouble labor_suppleins) {
this.labor_suppleins=labor_suppleins;
} 
 
/**
* 属性 labor_returnnins的Getter方法.属性名：退還勞保個人承擔金額
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_returnnins() {
return this.labor_returnnins;
} 

/**
* 属性labor_returnnins的Setter方法.属性名：退還勞保個人承擔金額
* 创建日期:2019-10-22
* @param newLabor_returnnins nc.vo.pub.lang.UFDouble
*/
public void setLabor_returnnins ( UFDouble labor_returnnins) {
this.labor_returnnins=labor_returnnins;
} 
 
/**
* 属性 labor_orgins的Getter方法.属性名：勞保公司承擔金額_系統
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_orgins() {
return this.labor_orgins;
} 

/**
* 属性labor_orgins的Setter方法.属性名：勞保公司承擔金額_系統
* 创建日期:2019-10-22
* @param newLabor_orgins nc.vo.pub.lang.UFDouble
*/
public void setLabor_orgins ( UFDouble labor_orgins) {
this.labor_orgins=labor_orgins;
} 
 
/**
* 属性 labor_orgins_import的Getter方法.属性名：勞保公司承擔金額_對帳單
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_orgins_import() {
return this.labor_orgins_import;
} 

/**
* 属性labor_orgins_import的Setter方法.属性名：勞保公司承擔金額_對帳單
* 创建日期:2019-10-22
* @param newLabor_orgins_import nc.vo.pub.lang.UFDouble
*/
public void setLabor_orgins_import ( UFDouble labor_orgins_import) {
this.labor_orgins_import=labor_orgins_import;
} 
 
/**
* 属性 retire_grade的Getter方法.属性名：勞退投保級距
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_grade() {
return this.retire_grade;
} 

/**
* 属性retire_grade的Setter方法.属性名：勞退投保級距
* 创建日期:2019-10-22
* @param newRetire_grade nc.vo.pub.lang.UFDouble
*/
public void setRetire_grade ( UFDouble retire_grade) {
this.retire_grade=retire_grade;
} 
 
/**
* 属性 retire_psnins的Getter方法.属性名：勞退個人承擔金額_系統
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_psnins() {
return this.retire_psnins;
} 

/**
* 属性retire_psnins的Setter方法.属性名：勞退個人承擔金額_系統
* 创建日期:2019-10-22
* @param newRetire_psnins nc.vo.pub.lang.UFDouble
*/
public void setRetire_psnins ( UFDouble retire_psnins) {
this.retire_psnins=retire_psnins;
} 
 
/**
* 属性 retire_psnins_import的Getter方法.属性名：勞退個人承擔金額_對帳單
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_psnins_import() {
return this.retire_psnins_import;
} 

/**
* 属性retire_psnins_import的Setter方法.属性名：勞退個人承擔金額_對帳單
* 创建日期:2019-10-22
* @param newRetire_psnins_import nc.vo.pub.lang.UFDouble
*/
public void setRetire_psnins_import ( UFDouble retire_psnins_import) {
this.retire_psnins_import=retire_psnins_import;
} 
 
/**
* 属性 retire_suppleins的Getter方法.属性名：補扣勞退個人承擔金額
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_suppleins() {
return this.retire_suppleins;
} 

/**
* 属性retire_suppleins的Setter方法.属性名：補扣勞退個人承擔金額
* 创建日期:2019-10-22
* @param newRetire_suppleins nc.vo.pub.lang.UFDouble
*/
public void setRetire_suppleins ( UFDouble retire_suppleins) {
this.retire_suppleins=retire_suppleins;
} 
 
/**
* 属性 retire_returnnins的Getter方法.属性名：退還勞退個人承擔金額
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_returnnins() {
return this.retire_returnnins;
} 

/**
* 属性retire_returnnins的Setter方法.属性名：退還勞退個人承擔金額
* 创建日期:2019-10-22
* @param newRetire_returnnins nc.vo.pub.lang.UFDouble
*/
public void setRetire_returnnins ( UFDouble retire_returnnins) {
this.retire_returnnins=retire_returnnins;
} 
 
/**
* 属性 creator的Getter方法.属性名：创建人
*  创建日期:2019-10-22
* @return nc.vo.sm.UserVO
*/
public String getCreator() {
return this.creator;
} 

/**
* 属性creator的Setter方法.属性名：创建人
* 创建日期:2019-10-22
* @param newCreator nc.vo.sm.UserVO
*/
public void setCreator ( String creator) {
this.creator=creator;
} 
 
/**
* 属性 creationtime的Getter方法.属性名：创建时间
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getCreationtime() {
return this.creationtime;
} 

/**
* 属性creationtime的Setter方法.属性名：创建时间
* 创建日期:2019-10-22
* @param newCreationtime nc.vo.pub.lang.UFDateTime
*/
public void setCreationtime ( UFDateTime creationtime) {
this.creationtime=creationtime;
} 
 
/**
* 属性 modifier的Getter方法.属性名：修改人
*  创建日期:2019-10-22
* @return nc.vo.sm.UserVO
*/
public String getModifier() {
return this.modifier;
} 

/**
* 属性modifier的Setter方法.属性名：修改人
* 创建日期:2019-10-22
* @param newModifier nc.vo.sm.UserVO
*/
public void setModifier ( String modifier) {
this.modifier=modifier;
} 
 
/**
* 属性 modifiedtime的Getter方法.属性名：修改时间
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getModifiedtime() {
return this.modifiedtime;
} 

/**
* 属性modifiedtime的Setter方法.属性名：修改时间
* 创建日期:2019-10-22
* @param newModifiedtime nc.vo.pub.lang.UFDateTime
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.modifiedtime=modifiedtime;
} 
 
/**
* 属性 生成时间戳的Getter方法.属性名：时间戳
*  创建日期:2019-10-22
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getTs() {
return this.ts;
}
/**
* 属性生成时间戳的Setter方法.属性名：时间戳
* 创建日期:2019-10-22
* @param newts nc.vo.pub.lang.UFDateTime
*/
public void setTs(UFDateTime ts){
this.ts=ts;
} 
     
    @Override
    public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.diffinsurance");
    }
   }
    