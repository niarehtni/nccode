package nc.vo.qcco.task;

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
 *  创建日期:2019-3-12
 * @author yonyouBQ
 * @version NCPrj ??
 */
 
public class TaskSVO extends SuperVO {
	
/**
*测试条件主键
*/
public String pk_task_s;
/**
*测试条件项
*/
public String pk_testconditionitem;
/**
*状态
*/
public UFBoolean conditionstatus;
/**
*是否可选
*/
public UFBoolean isoptional;
/**
*是否可报告
*/
public UFBoolean isallow_out;
/**
*仪器
*/
public String instrument;
/**
*值类型
*/
public String valuetype;
/**
*值
*/
public String entry;
/**
*单位
*/
public String unit;
/**
*格式化值
*/
public String formatted_entry;
/**
*最小值
*/
public UFDouble min_limit;
/**
*最大值
*/
public UFDouble max_limit;
/**
*英文说明
*/
public String englishdescription;
/**
*上层单据主键
*/
public String pk_task_b;
/**
*时间戳
*/
public UFDateTime ts;
public Integer Dr;

public Integer getDr() {
		return Dr;
	}

	public void setDr(Integer dr) {
		Dr = dr;
	}
    

 
public String getPk_task_s() {
		return pk_task_s;
	}

	public void setPk_task_s(String pk_task_s) {
		this.pk_task_s = pk_task_s;
	}

/**
* 属性 pk_testconditionitem的Getter方法.属性名：测试条件项
*  创建日期:2019-3-12
* @return nc.vo.bd.defdoc.DefdocVO
*/
public String getPk_testconditionitem() {
return this.pk_testconditionitem;
} 

/**
* 属性pk_testconditionitem的Setter方法.属性名：测试条件项
* 创建日期:2019-3-12
* @param newPk_testconditionitem nc.vo.bd.defdoc.DefdocVO
*/
public void setPk_testconditionitem ( String pk_testconditionitem) {
this.pk_testconditionitem=pk_testconditionitem;
} 
 
/**
* 属性 conditionstatus的Getter方法.属性名：状态
*  创建日期:2019-3-12
* @return nc.vo.pub.lang.UFUFBoolean
*/
public UFBoolean getConditionstatus() {
return this.conditionstatus;
} 

/**
* 属性conditionstatus的Setter方法.属性名：状态
* 创建日期:2019-3-12
* @param newConditionstatus nc.vo.pub.lang.UFUFBoolean
*/
public void setConditionstatus ( UFBoolean conditionstatus) {
this.conditionstatus=conditionstatus;
} 
 
/**
* 属性 isoptional的Getter方法.属性名：是否可选
*  创建日期:2019-3-12
* @return nc.vo.pub.lang.UFUFBoolean
*/
public UFBoolean getIsoptional() {
return this.isoptional;
} 

/**
* 属性isoptional的Setter方法.属性名：是否可选
* 创建日期:2019-3-12
* @param newIsoptional nc.vo.pub.lang.UFUFBoolean
*/
public void setIsoptional ( UFBoolean isoptional) {
this.isoptional=isoptional;
} 
 
/**
* 属性 isallow_out的Getter方法.属性名：是否可报告
*  创建日期:2019-3-12
* @return nc.vo.pub.lang.UFUFBoolean
*/
public UFBoolean getIsallow_out() {
return this.isallow_out;
} 

/**
* 属性isallow_out的Setter方法.属性名：是否可报告
* 创建日期:2019-3-12
* @param newIsallow_out nc.vo.pub.lang.UFUFBoolean
*/
public void setIsallow_out ( UFBoolean isallow_out) {
this.isallow_out=isallow_out;
} 
 
/**
* 属性 instrument的Getter方法.属性名：仪器
*  创建日期:2019-3-12
* @return nc.vo.bd.defdoc.DefdocVO
*/
public String getInstrument() {
return this.instrument;
} 

/**
* 属性instrument的Setter方法.属性名：仪器
* 创建日期:2019-3-12
* @param newInstrument nc.vo.bd.defdoc.DefdocVO
*/
public void setInstrument ( String instrument) {
this.instrument=instrument;
} 
 
/**
* 属性 valuetype的Getter方法.属性名：值类型
*  创建日期:2019-3-12
* @return nc.vo.bd.defdoc.DefdocVO
*/
public String getValuetype() {
return this.valuetype;
} 

/**
* 属性valuetype的Setter方法.属性名：值类型
* 创建日期:2019-3-12
* @param newValuetype nc.vo.bd.defdoc.DefdocVO
*/
public void setValuetype ( String valuetype) {
this.valuetype=valuetype;
} 
 
/**
* 属性 entry的Getter方法.属性名：值
*  创建日期:2019-3-12
* @return java.lang.String
*/
public String getEntry() {
return this.entry;
} 

/**
* 属性entry的Setter方法.属性名：值
* 创建日期:2019-3-12
* @param newEntry java.lang.String
*/
public void setEntry ( String entry) {
this.entry=entry;
} 
 
/**
* 属性 unit的Getter方法.属性名：单位
*  创建日期:2019-3-12
* @return java.lang.String
*/
public String getUnit() {
return this.unit;
} 

/**
* 属性unit的Setter方法.属性名：单位
* 创建日期:2019-3-12
* @param newUnit java.lang.String
*/
public void setUnit ( String unit) {
this.unit=unit;
} 
 
/**
* 属性 formatted_entry的Getter方法.属性名：格式化值
*  创建日期:2019-3-12
* @return java.lang.String
*/
public String getFormatted_entry() {
return this.formatted_entry;
} 

/**
* 属性formatted_entry的Setter方法.属性名：格式化值
* 创建日期:2019-3-12
* @param newFormatted_entry java.lang.String
*/
public void setFormatted_entry ( String formatted_entry) {
this.formatted_entry=formatted_entry;
} 
 
/**
* 属性 min_limit的Getter方法.属性名：最小值
*  创建日期:2019-3-12
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getMin_limit() {
return this.min_limit;
} 

/**
* 属性min_limit的Setter方法.属性名：最小值
* 创建日期:2019-3-12
* @param newMin_limit nc.vo.pub.lang.UFDouble
*/
public void setMin_limit ( UFDouble min_limit) {
this.min_limit=min_limit;
} 
 
/**
* 属性 max_limit的Getter方法.属性名：最大值
*  创建日期:2019-3-12
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getMax_limit() {
return this.max_limit;
} 

/**
* 属性max_limit的Setter方法.属性名：最大值
* 创建日期:2019-3-12
* @param newMax_limit nc.vo.pub.lang.UFDouble
*/
public void setMax_limit ( UFDouble max_limit) {
this.max_limit=max_limit;
} 
 
/**
* 属性 englishdescription的Getter方法.属性名：英文说明
*  创建日期:2019-3-12
* @return java.lang.String
*/
public String getEnglishdescription() {
return this.englishdescription;
} 

/**
* 属性englishdescription的Setter方法.属性名：英文说明
* 创建日期:2019-3-12
* @param newEnglishdescription java.lang.String
*/
public void setEnglishdescription ( String englishdescription) {
this.englishdescription=englishdescription;
} 
 
/**
* 属性 生成上层主键的Getter方法.属性名：上层主键
*  创建日期:2019-3-12
* @return String
*/
public String getPk_task_b(){
return this.pk_task_b;
}
/**
* 属性生成上层主键的Setter方法.属性名：上层主键
* 创建日期:2019-3-12
* @param newPk_task_b String
*/
public void setPk_task_b(String pk_task_b){
this.pk_task_b=pk_task_b;
} 
/**
* 属性 生成时间戳的Getter方法.属性名：时间戳
*  创建日期:2019-3-12
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getTs() {
return this.ts;
}
/**
* 属性生成时间戳的Setter方法.属性名：时间戳
* 创建日期:2019-3-12
* @param newts nc.vo.pub.lang.UFDateTime
*/
public void setTs(UFDateTime ts){
this.ts=ts;
} 
     
    @Override
    public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("qcco.task_s");
    }
    @Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_task_b";
	}
   }
    