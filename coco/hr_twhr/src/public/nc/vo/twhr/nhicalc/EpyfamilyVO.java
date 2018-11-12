package nc.vo.twhr.nhicalc;

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
 *  创建日期:2018-9-13
 * @author 
 * @version NCPrj ??
 */
 
public class EpyfamilyVO extends SuperVO {
	
/**
*员工眷属主键
*/
public String pk_epyfamily;
/**
*行号
*/
public String rowno;
/**
*员工信息
*/
public String pk_psndoc;
/**
*眷属信息PK
*/
public String pk_psndoc_sub;
/**
*补助身份1
*/
public String sub_identity1;
/**
*补助身份2
*/
public String sub_identity2;
/**
*健保费
*/
public UFDouble healthamount;
/**
*姓名
*/
public String name;
/**
*称谓
*/
public String appellation;
/**
*上层单据主键
*/
public String pk_nhicalc;
/**
*时间戳
*/
public UFDateTime ts;
    
    
/**
* 属性 pk_epyfamily的Getter方法.属性名：员工眷属主键
*  创建日期:2018-9-13
* @return java.lang.String
*/
public String getPk_epyfamily() {
return this.pk_epyfamily;
} 

/**
* 属性pk_epyfamily的Setter方法.属性名：员工眷属主键
* 创建日期:2018-9-13
* @param newPk_epyfamily java.lang.String
*/
public void setPk_epyfamily ( String pk_epyfamily) {
this.pk_epyfamily=pk_epyfamily;
} 
 
/**
* 属性 rowno的Getter方法.属性名：行号
*  创建日期:2018-9-13
* @return java.lang.String
*/
public String getRowno() {
return this.rowno;
} 

/**
* 属性rowno的Setter方法.属性名：行号
* 创建日期:2018-9-13
* @param newRowno java.lang.String
*/
public void setRowno ( String rowno) {
this.rowno=rowno;
} 
 
/**
* 属性 pk_psndoc的Getter方法.属性名：员工信息
*  创建日期:2018-9-13
* @return nc.vo.bd.psn.PsndocVO
*/
public String getPk_psndoc() {
return this.pk_psndoc;
} 

/**
* 属性pk_psndoc的Setter方法.属性名：员工信息
* 创建日期:2018-9-13
* @param newPk_psndoc nc.vo.bd.psn.PsndocVO
*/
public void setPk_psndoc ( String pk_psndoc) {
this.pk_psndoc=pk_psndoc;
} 
 
/**
* 属性 pk_psndoc_sub的Getter方法.属性名：眷属信息
*  创建日期:2018-9-13
* @return nc.vo.hi.psndoc.FamilyVO
*/
public String getPk_psndoc_sub() {
return this.pk_psndoc_sub;
} 

/**
* 属性pk_psndoc_sub的Setter方法.属性名：眷属信息
* 创建日期:2018-9-13
* @param newPk_psndoc_sub nc.vo.hi.psndoc.FamilyVO
*/
public void setPk_psndoc_sub ( String pk_psndoc_sub) {
this.pk_psndoc_sub=pk_psndoc_sub;
} 
 
/**
* 属性 sub_identity1的Getter方法.属性名：补助身份1
*  创建日期:2018-9-13
* @return nc.vo.twhr.allowance.AllowanceVO
*/
public String getSub_identity1() {
return this.sub_identity1;
} 

/**
* 属性sub_identity1的Setter方法.属性名：补助身份1
* 创建日期:2018-9-13
* @param newSub_identity1 nc.vo.twhr.allowance.AllowanceVO
*/
public void setSub_identity1 ( String sub_identity1) {
this.sub_identity1=sub_identity1;
} 
 
/**
* 属性 sub_identity2的Getter方法.属性名：补助身份2
*  创建日期:2018-9-13
* @return nc.vo.twhr.allowance.AllowanceVO
*/
public String getSub_identity2() {
return this.sub_identity2;
} 

/**
* 属性sub_identity2的Setter方法.属性名：补助身份2
* 创建日期:2018-9-13
* @param newSub_identity2 nc.vo.twhr.allowance.AllowanceVO
*/
public void setSub_identity2 ( String sub_identity2) {
this.sub_identity2=sub_identity2;
} 
 
/**
* 属性 healthamount的Getter方法.属性名：健保费
*  创建日期:2018-9-13
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHealthamount() {
return this.healthamount;
} 

/**
* 属性healthamount的Setter方法.属性名：健保费
* 创建日期:2018-9-13
* @param newHealthamount nc.vo.pub.lang.UFDouble
*/
public void setHealthamount ( UFDouble healthamount) {
this.healthamount=healthamount;
} 
 
/**
* 属性 name的Getter方法.属性名：姓名
*  创建日期:2018-9-13
* @return java.lang.String
*/
public String getName() {
return this.name;
} 

/**
* 属性name的Setter方法.属性名：姓名
* 创建日期:2018-9-13
* @param newName java.lang.String
*/
public void setName ( String name) {
this.name=name;
} 
 
/**
* 属性 appellation的Getter方法.属性名：称谓
*  创建日期:2018-9-13
* @return java.lang.String
*/
public String getAppellation() {
return this.appellation;
} 

/**
* 属性appellation的Setter方法.属性名：称谓
* 创建日期:2018-9-13
* @param newAppellation java.lang.String
*/
public void setAppellation ( String appellation) {
this.appellation=appellation;
} 
 
/**
* 属性 生成上层主键的Getter方法.属性名：上层主键
*  创建日期:2018-9-13
* @return String
*/
public String getPk_nhicalc(){
return this.pk_nhicalc;
}
/**
* 属性生成上层主键的Setter方法.属性名：上层主键
* 创建日期:2018-9-13
* @param newPk_nhicalc String
*/
public void setPk_nhicalc(String pk_nhicalc){
this.pk_nhicalc=pk_nhicalc;
} 
/**
* 属性 生成时间戳的Getter方法.属性名：时间戳
*  创建日期:2018-9-13
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getTs() {
return this.ts;
}
/**
* 属性生成时间戳的Setter方法.属性名：时间戳
* 创建日期:2018-9-13
* @param newts nc.vo.pub.lang.UFDateTime
*/
public void setTs(UFDateTime ts){
this.ts=ts;
} 
     
    @Override
    public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.epyfamily");
    }
   }
    