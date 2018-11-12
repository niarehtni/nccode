package nc.vo.hrwa.watbmdaysalary;

import nc.vo.pub.*;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 *   此处添加类的描述信息
 * </p>
 *  创建日期:2018-9-14
 * @author YONYOU NC
 * @version NCPrj ??
 */
public class TbmDaySalaryVO extends nc.vo.pub.SuperVO{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -5423032304696183920L;
	
	private java.lang.String pk_tbm_daysalary;
    private java.lang.String pk_hrorg;
    private java.lang.String pk_wa_item;
    private nc.vo.pub.lang.UFBoolean isattend=UFBoolean.FALSE;
    private nc.vo.pub.lang.UFBoolean taxflag=UFBoolean.FALSE;
    private java.lang.String pk_psndoc_sub;
    private nc.vo.pub.lang.UFDateTime wadocts;
    private java.lang.String pk_psndoc;
    private java.lang.String pk_psnjob;
    private nc.vo.pub.lang.UFLiteralDate salarydate;
    private java.lang.Integer cyear;
    private java.lang.Integer cperiod;
    private nc.vo.pub.lang.UFDouble tbmdaysalary=UFDouble.ZERO_DBL;
    private nc.vo.pub.lang.UFDouble tbmhoursalary=UFDouble.ZERO_DBL;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;    
	
	
    public static final String PK_TBM_DAYSALARY = "pk_tbm_daysalary";
    public static final String PK_HRORG = "pk_hrorg";
    public static final String PK_WA_ITEM = "pk_wa_item";
    public static final String ISATTEND = "isattend";
    public static final String TAXFLAG = "taxflag";
    public static final String PK_PSNDOC_SUB = "pk_psndoc_sub";
    public static final String WADOCTS = "wadocts";
    public static final String PK_PSNDOC = "pk_psndoc";
    public static final String PK_PSNJOB = "pk_psnjob";
    public static final String SALARYDATE = "salarydate";
    public static final String CYEAR = "cyear";
    public static final String CPERIOD = "cperiod";
    public static final String TBMDAYSALARY = "tbmdaysalary";
    public static final String TBMHOURSALARY = "tbmhoursalary";

	/**
	 * 属性 pk_tbm_daysalary的Getter方法.属性名：日薪主键
	 *  创建日期:2018-9-14
	 * @return java.lang.String
	 */
	public java.lang.String getPk_tbm_daysalary () {
		return pk_tbm_daysalary;
	}   
	/**
	 * 属性pk_tbm_daysalary的Setter方法.属性名：日薪主键
	 * 创建日期:2018-9-14
	 * @param newPk_tbm_daysalary java.lang.String
	 */
	public void setPk_tbm_daysalary (java.lang.String newPk_tbm_daysalary ) {
	 	this.pk_tbm_daysalary = newPk_tbm_daysalary;
	} 	 
	
	/**
	 * 属性 pk_hrorg的Getter方法.属性名：人力資源組織
	 *  创建日期:2018-9-14
	 * @return java.lang.String
	 */
	public java.lang.String getPk_hrorg () {
		return pk_hrorg;
	}   
	/**
	 * 属性pk_hrorg的Setter方法.属性名：人力資源組織
	 * 创建日期:2018-9-14
	 * @param newPk_hrorg java.lang.String
	 */
	public void setPk_hrorg (java.lang.String newPk_hrorg ) {
	 	this.pk_hrorg = newPk_hrorg;
	} 	 
	
	/**
	 * 属性 pk_wa_item的Getter方法.属性名：薪资项目
	 *  创建日期:2018-9-14
	 * @return java.lang.String
	 */
	public java.lang.String getPk_wa_item () {
		return pk_wa_item;
	}   
	/**
	 * 属性pk_wa_item的Setter方法.属性名：薪资项目
	 * 创建日期:2018-9-14
	 * @param newPk_wa_item java.lang.String
	 */
	public void setPk_wa_item (java.lang.String newPk_wa_item ) {
	 	this.pk_wa_item = newPk_wa_item;
	} 	 
	
	/**
	 * 属性 isattend的Getter方法.属性名：是否考勤
	 *  创建日期:2018-9-14
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIsattend () {
		return isattend;
	}   
	/**
	 * 属性isattend的Setter方法.属性名：是否考勤
	 * 创建日期:2018-9-14
	 * @param newIsattend nc.vo.pub.lang.UFBoolean
	 */
	public void setIsattend (nc.vo.pub.lang.UFBoolean newIsattend ) {
	 	this.isattend = newIsattend;
	} 	 
	
	/**
	 * 属性 taxflag的Getter方法.属性名：是否扣税
	 *  创建日期:2018-9-14
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getTaxflag () {
		return taxflag;
	}   
	/**
	 * 属性taxflag的Setter方法.属性名：是否扣税
	 * 创建日期:2018-9-14
	 * @param newTaxflag nc.vo.pub.lang.UFBoolean
	 */
	public void setTaxflag (nc.vo.pub.lang.UFBoolean newTaxflag ) {
	 	this.taxflag = newTaxflag;
	} 	 
	
	/**
	 * 属性 pk_psndoc_sub的Getter方法.属性名：薪资变动情况
	 *  创建日期:2018-9-14
	 * @return java.lang.String
	 */
	public java.lang.String getPk_psndoc_sub () {
		return pk_psndoc_sub;
	}   
	/**
	 * 属性pk_psndoc_sub的Setter方法.属性名：薪资变动情况
	 * 创建日期:2018-9-14
	 * @param newPk_psndoc_sub java.lang.String
	 */
	public void setPk_psndoc_sub (java.lang.String newPk_psndoc_sub ) {
	 	this.pk_psndoc_sub = newPk_psndoc_sub;
	} 	 
	
	/**
	 * 属性 wadocts的Getter方法.属性名：薪资变动情况时间戳
	 *  创建日期:2018-9-14
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getWadocts () {
		return wadocts;
	}   
	/**
	 * 属性wadocts的Setter方法.属性名：薪资变动情况时间戳
	 * 创建日期:2018-9-14
	 * @param newWadocts nc.vo.pub.lang.UFDateTime
	 */
	public void setWadocts (nc.vo.pub.lang.UFDateTime newWadocts ) {
	 	this.wadocts = newWadocts;
	} 	 
	
	/**
	 * 属性 pk_psndoc的Getter方法.属性名：人员基本主键
	 *  创建日期:2018-9-14
	 * @return java.lang.String
	 */
	public java.lang.String getPk_psndoc () {
		return pk_psndoc;
	}   
	/**
	 * 属性pk_psndoc的Setter方法.属性名：人员基本主键
	 * 创建日期:2018-9-14
	 * @param newPk_psndoc java.lang.String
	 */
	public void setPk_psndoc (java.lang.String newPk_psndoc ) {
	 	this.pk_psndoc = newPk_psndoc;
	} 	 
	
	/**
	 * 属性 pk_psnjob的Getter方法.属性名：人员任职主键
	 *  创建日期:2018-9-14
	 * @return java.lang.String
	 */
	public java.lang.String getPk_psnjob () {
		return pk_psnjob;
	}   
	/**
	 * 属性pk_psnjob的Setter方法.属性名：人员任职主键
	 * 创建日期:2018-9-14
	 * @param newPk_psnjob java.lang.String
	 */
	public void setPk_psnjob (java.lang.String newPk_psnjob ) {
	 	this.pk_psnjob = newPk_psnjob;
	} 	 
	
	/**
	 * 属性 salarydate的Getter方法.属性名：薪资日期
	 *  创建日期:2018-9-14
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getSalarydate () {
		return salarydate;
	}   
	/**
	 * 属性salarydate的Setter方法.属性名：薪资日期
	 * 创建日期:2018-9-14
	 * @param newSalarydate nc.vo.pub.lang.UFLiteralDate
	 */
	public void setSalarydate (nc.vo.pub.lang.UFLiteralDate newSalarydate ) {
	 	this.salarydate = newSalarydate;
	} 	 
	
	/**
	 * 属性 cyear的Getter方法.属性名：薪资年
	 *  创建日期:2018-9-14
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getCyear () {
		return cyear;
	}   
	/**
	 * 属性cyear的Setter方法.属性名：薪资年
	 * 创建日期:2018-9-14
	 * @param newCyear java.lang.Integer
	 */
	public void setCyear (java.lang.Integer newCyear ) {
	 	this.cyear = newCyear;
	} 	 
	
	/**
	 * 属性 cperiod的Getter方法.属性名：薪资月
	 *  创建日期:2018-9-14
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getCperiod () {
		return cperiod;
	}   
	/**
	 * 属性cperiod的Setter方法.属性名：薪资月
	 * 创建日期:2018-9-14
	 * @param newCperiod java.lang.Integer
	 */
	public void setCperiod (java.lang.Integer newCperiod ) {
	 	this.cperiod = newCperiod;
	} 	 
	
	/**
	 * 属性 tbmdaysalary的Getter方法.属性名：考勤日薪
	 *  创建日期:2018-9-14
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getTbmdaysalary () {
		return tbmdaysalary;
	}   
	/**
	 * 属性tbmdaysalary的Setter方法.属性名：考勤日薪
	 * 创建日期:2018-9-14
	 * @param newTbmdaysalary nc.vo.pub.lang.UFDouble
	 */
	public void setTbmdaysalary (nc.vo.pub.lang.UFDouble newTbmdaysalary ) {
	 	this.tbmdaysalary = newTbmdaysalary;
	} 	 
	
	/**
	 * 属性 tbmhoursalary的Getter方法.属性名：考勤时薪
	 *  创建日期:2018-9-14
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getTbmhoursalary () {
		return tbmhoursalary;
	}   
	/**
	 * 属性tbmhoursalary的Setter方法.属性名：考勤时薪
	 * 创建日期:2018-9-14
	 * @param newTbmhoursalary nc.vo.pub.lang.UFDouble
	 */
	public void setTbmhoursalary (nc.vo.pub.lang.UFDouble newTbmhoursalary ) {
	 	this.tbmhoursalary = newTbmhoursalary;
	} 	
	
	/**
	 * 属性 dr的Getter方法.属性名：dr
	 *  创建日期:2018-9-14
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.属性名：dr
	 * 创建日期:2018-9-14
	 * @param newDr java.lang.Integer
	 */
	public void setDr (java.lang.Integer newDr ) {
	 	this.dr = newDr;
	} 	 
	
	/**
	 * 属性 ts的Getter方法.属性名：ts
	 *  创建日期:2018-9-14
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.属性名：ts
	 * 创建日期:2018-9-14
	 * @param newTs nc.vo.pub.lang.UFDateTime
	 */
	public void setTs (nc.vo.pub.lang.UFDateTime newTs ) {
	 	this.ts = newTs;
	} 	 
	
	
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2018-9-14
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2018-9-14
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
			
		return "pk_tbm_daysalary";
	}
    
	/**
	 * <p>返回表名称
	 * <p>
	 * 创建日期:2018-9-14
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "wa_tbmdaysalary";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2018-9-14
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "wa_tbmdaysalary";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2018-9-14
	  */
     public TbmDaySalaryVO() {
		super();	
	}    
	
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.hrwa.watbmdaysalary.TbmDaySalaryVO" )
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrwa.TbmDaySalaryVO");
		
   	}
     
}