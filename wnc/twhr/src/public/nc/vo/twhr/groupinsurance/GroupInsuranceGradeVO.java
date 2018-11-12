package nc.vo.twhr.groupinsurance;

import nc.vo.pub.*;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此處簡要描述此類功能 </b>
 * <p>
 *   此處添加類的描述信息
 * </p>
 *  創建日期:2018/5/7
 * @author 
 * @version NCPrj ??
 */
public class GroupInsuranceGradeVO extends nc.vo.pub.SuperVO{
	
    private java.lang.String id;
    private java.lang.String code;
    private java.lang.String name;
    private java.lang.String pk_group;
    private java.lang.String pk_org;
    private java.lang.String pk_org_v;
    private java.lang.String creator;
    private nc.vo.pub.lang.UFDateTime creationtime;
    private java.lang.String modifier;
    private nc.vo.pub.lang.UFDateTime modifiedtime;
    private java.lang.String def1;
    private java.lang.String def2;
    private java.lang.String def3;
    private java.lang.String def4;
    private java.lang.String def5;
    private java.lang.String def6;
    private java.lang.String def7;
    private java.lang.String def8;
    private java.lang.String def9;
    private java.lang.String def10;
    private java.lang.String def11;
    private java.lang.String def12;
    private java.lang.String def13;
    private java.lang.String def14;
    private java.lang.String def15;
    private java.lang.String def16;
    private java.lang.String def17;
    private java.lang.String def18;
    private java.lang.String def19;
    private java.lang.String def20;
    private java.lang.String pk_jobrank;
    private java.lang.String pk_groupinsurance;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;    
	
	
    public static final String ID = "id";
    public static final String CODE = "code";
    public static final String NAME = "name";
    public static final String PK_GROUP = "pk_group";
    public static final String PK_ORG = "pk_org";
    public static final String PK_ORG_V = "pk_org_v";
    public static final String CREATOR = "creator";
    public static final String CREATIONTIME = "creationtime";
    public static final String MODIFIER = "modifier";
    public static final String MODIFIEDTIME = "modifiedtime";
    public static final String DEF1 = "def1";
    public static final String DEF2 = "def2";
    public static final String DEF3 = "def3";
    public static final String DEF4 = "def4";
    public static final String DEF5 = "def5";
    public static final String DEF6 = "def6";
    public static final String DEF7 = "def7";
    public static final String DEF8 = "def8";
    public static final String DEF9 = "def9";
    public static final String DEF10 = "def10";
    public static final String DEF11 = "def11";
    public static final String DEF12 = "def12";
    public static final String DEF13 = "def13";
    public static final String DEF14 = "def14";
    public static final String DEF15 = "def15";
    public static final String DEF16 = "def16";
    public static final String DEF17 = "def17";
    public static final String DEF18 = "def18";
    public static final String DEF19 = "def19";
    public static final String DEF20 = "def20";
    public static final String PK_JOBRANK = "pk_jobrank";
    public static final String PK_GROUPINSURANCE = "pk_groupinsurance";

	/**
	 * 屬性 id的Getter方法.屬性名：id
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getId () {
		return id;
	}   
	/**
	 * 屬性id的Setter方法.屬性名：id
	 * 創建日期:2018/5/7
	 * @param newId java.lang.String
	 */
	public void setId (java.lang.String newId ) {
	 	this.id = newId;
	} 	 
	
	/**
	 * 屬性 code的Getter方法.屬性名：code
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getCode () {
		return code;
	}   
	/**
	 * 屬性code的Setter方法.屬性名：code
	 * 創建日期:2018/5/7
	 * @param newCode java.lang.String
	 */
	public void setCode (java.lang.String newCode ) {
	 	this.code = newCode;
	} 	 
	
	/**
	 * 屬性 name的Getter方法.屬性名：name
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getName () {
		return name;
	}   
	/**
	 * 屬性name的Setter方法.屬性名：name
	 * 創建日期:2018/5/7
	 * @param newName java.lang.String
	 */
	public void setName (java.lang.String newName ) {
	 	this.name = newName;
	} 	 
	
	/**
	 * 屬性 pk_group的Getter方法.屬性名：集團
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group () {
		return pk_group;
	}   
	/**
	 * 屬性pk_group的Setter方法.屬性名：集團
	 * 創建日期:2018/5/7
	 * @param newPk_group java.lang.String
	 */
	public void setPk_group (java.lang.String newPk_group ) {
	 	this.pk_group = newPk_group;
	} 	 
	
	/**
	 * 屬性 pk_org的Getter方法.屬性名：組織
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org () {
		return pk_org;
	}   
	/**
	 * 屬性pk_org的Setter方法.屬性名：組織
	 * 創建日期:2018/5/7
	 * @param newPk_org java.lang.String
	 */
	public void setPk_org (java.lang.String newPk_org ) {
	 	this.pk_org = newPk_org;
	} 	 
	
	/**
	 * 屬性 pk_org_v的Getter方法.屬性名：組織版本
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org_v () {
		return pk_org_v;
	}   
	/**
	 * 屬性pk_org_v的Setter方法.屬性名：組織版本
	 * 創建日期:2018/5/7
	 * @param newPk_org_v java.lang.String
	 */
	public void setPk_org_v (java.lang.String newPk_org_v ) {
	 	this.pk_org_v = newPk_org_v;
	} 	 
	
	/**
	 * 屬性 creator的Getter方法.屬性名：創建人
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getCreator () {
		return creator;
	}   
	/**
	 * 屬性creator的Setter方法.屬性名：創建人
	 * 創建日期:2018/5/7
	 * @param newCreator java.lang.String
	 */
	public void setCreator (java.lang.String newCreator ) {
	 	this.creator = newCreator;
	} 	 
	
	/**
	 * 屬性 creationtime的Getter方法.屬性名：創建時間
	 *  創建日期:2018/5/7
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getCreationtime () {
		return creationtime;
	}   
	/**
	 * 屬性creationtime的Setter方法.屬性名：創建時間
	 * 創建日期:2018/5/7
	 * @param newCreationtime nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime (nc.vo.pub.lang.UFDateTime newCreationtime ) {
	 	this.creationtime = newCreationtime;
	} 	 
	
	/**
	 * 屬性 modifier的Getter方法.屬性名：修改人
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getModifier () {
		return modifier;
	}   
	/**
	 * 屬性modifier的Setter方法.屬性名：修改人
	 * 創建日期:2018/5/7
	 * @param newModifier java.lang.String
	 */
	public void setModifier (java.lang.String newModifier ) {
	 	this.modifier = newModifier;
	} 	 
	
	/**
	 * 屬性 modifiedtime的Getter方法.屬性名：修改時間
	 *  創建日期:2018/5/7
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getModifiedtime () {
		return modifiedtime;
	}   
	/**
	 * 屬性modifiedtime的Setter方法.屬性名：修改時間
	 * 創建日期:2018/5/7
	 * @param newModifiedtime nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime (nc.vo.pub.lang.UFDateTime newModifiedtime ) {
	 	this.modifiedtime = newModifiedtime;
	} 	 
	
	/**
	 * 屬性 def1的Getter方法.屬性名：自定義項1
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef1 () {
		return def1;
	}   
	/**
	 * 屬性def1的Setter方法.屬性名：自定義項1
	 * 創建日期:2018/5/7
	 * @param newDef1 java.lang.String
	 */
	public void setDef1 (java.lang.String newDef1 ) {
	 	this.def1 = newDef1;
	} 	 
	
	/**
	 * 屬性 def2的Getter方法.屬性名：自定義項2
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef2 () {
		return def2;
	}   
	/**
	 * 屬性def2的Setter方法.屬性名：自定義項2
	 * 創建日期:2018/5/7
	 * @param newDef2 java.lang.String
	 */
	public void setDef2 (java.lang.String newDef2 ) {
	 	this.def2 = newDef2;
	} 	 
	
	/**
	 * 屬性 def3的Getter方法.屬性名：自定義項3
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef3 () {
		return def3;
	}   
	/**
	 * 屬性def3的Setter方法.屬性名：自定義項3
	 * 創建日期:2018/5/7
	 * @param newDef3 java.lang.String
	 */
	public void setDef3 (java.lang.String newDef3 ) {
	 	this.def3 = newDef3;
	} 	 
	
	/**
	 * 屬性 def4的Getter方法.屬性名：自定義項4
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef4 () {
		return def4;
	}   
	/**
	 * 屬性def4的Setter方法.屬性名：自定義項4
	 * 創建日期:2018/5/7
	 * @param newDef4 java.lang.String
	 */
	public void setDef4 (java.lang.String newDef4 ) {
	 	this.def4 = newDef4;
	} 	 
	
	/**
	 * 屬性 def5的Getter方法.屬性名：自定義項5
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef5 () {
		return def5;
	}   
	/**
	 * 屬性def5的Setter方法.屬性名：自定義項5
	 * 創建日期:2018/5/7
	 * @param newDef5 java.lang.String
	 */
	public void setDef5 (java.lang.String newDef5 ) {
	 	this.def5 = newDef5;
	} 	 
	
	/**
	 * 屬性 def6的Getter方法.屬性名：自定義項6
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef6 () {
		return def6;
	}   
	/**
	 * 屬性def6的Setter方法.屬性名：自定義項6
	 * 創建日期:2018/5/7
	 * @param newDef6 java.lang.String
	 */
	public void setDef6 (java.lang.String newDef6 ) {
	 	this.def6 = newDef6;
	} 	 
	
	/**
	 * 屬性 def7的Getter方法.屬性名：自定義項7
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef7 () {
		return def7;
	}   
	/**
	 * 屬性def7的Setter方法.屬性名：自定義項7
	 * 創建日期:2018/5/7
	 * @param newDef7 java.lang.String
	 */
	public void setDef7 (java.lang.String newDef7 ) {
	 	this.def7 = newDef7;
	} 	 
	
	/**
	 * 屬性 def8的Getter方法.屬性名：自定義項8
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef8 () {
		return def8;
	}   
	/**
	 * 屬性def8的Setter方法.屬性名：自定義項8
	 * 創建日期:2018/5/7
	 * @param newDef8 java.lang.String
	 */
	public void setDef8 (java.lang.String newDef8 ) {
	 	this.def8 = newDef8;
	} 	 
	
	/**
	 * 屬性 def9的Getter方法.屬性名：自定義項9
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef9 () {
		return def9;
	}   
	/**
	 * 屬性def9的Setter方法.屬性名：自定義項9
	 * 創建日期:2018/5/7
	 * @param newDef9 java.lang.String
	 */
	public void setDef9 (java.lang.String newDef9 ) {
	 	this.def9 = newDef9;
	} 	 
	
	/**
	 * 屬性 def10的Getter方法.屬性名：自定義項10
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef10 () {
		return def10;
	}   
	/**
	 * 屬性def10的Setter方法.屬性名：自定義項10
	 * 創建日期:2018/5/7
	 * @param newDef10 java.lang.String
	 */
	public void setDef10 (java.lang.String newDef10 ) {
	 	this.def10 = newDef10;
	} 	 
	
	/**
	 * 屬性 def11的Getter方法.屬性名：自定義項11
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef11 () {
		return def11;
	}   
	/**
	 * 屬性def11的Setter方法.屬性名：自定義項11
	 * 創建日期:2018/5/7
	 * @param newDef11 java.lang.String
	 */
	public void setDef11 (java.lang.String newDef11 ) {
	 	this.def11 = newDef11;
	} 	 
	
	/**
	 * 屬性 def12的Getter方法.屬性名：自定義項12
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef12 () {
		return def12;
	}   
	/**
	 * 屬性def12的Setter方法.屬性名：自定義項12
	 * 創建日期:2018/5/7
	 * @param newDef12 java.lang.String
	 */
	public void setDef12 (java.lang.String newDef12 ) {
	 	this.def12 = newDef12;
	} 	 
	
	/**
	 * 屬性 def13的Getter方法.屬性名：自定義項13
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef13 () {
		return def13;
	}   
	/**
	 * 屬性def13的Setter方法.屬性名：自定義項13
	 * 創建日期:2018/5/7
	 * @param newDef13 java.lang.String
	 */
	public void setDef13 (java.lang.String newDef13 ) {
	 	this.def13 = newDef13;
	} 	 
	
	/**
	 * 屬性 def14的Getter方法.屬性名：自定義項14
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef14 () {
		return def14;
	}   
	/**
	 * 屬性def14的Setter方法.屬性名：自定義項14
	 * 創建日期:2018/5/7
	 * @param newDef14 java.lang.String
	 */
	public void setDef14 (java.lang.String newDef14 ) {
	 	this.def14 = newDef14;
	} 	 
	
	/**
	 * 屬性 def15的Getter方法.屬性名：自定義項15
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef15 () {
		return def15;
	}   
	/**
	 * 屬性def15的Setter方法.屬性名：自定義項15
	 * 創建日期:2018/5/7
	 * @param newDef15 java.lang.String
	 */
	public void setDef15 (java.lang.String newDef15 ) {
	 	this.def15 = newDef15;
	} 	 
	
	/**
	 * 屬性 def16的Getter方法.屬性名：自定義項16
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef16 () {
		return def16;
	}   
	/**
	 * 屬性def16的Setter方法.屬性名：自定義項16
	 * 創建日期:2018/5/7
	 * @param newDef16 java.lang.String
	 */
	public void setDef16 (java.lang.String newDef16 ) {
	 	this.def16 = newDef16;
	} 	 
	
	/**
	 * 屬性 def17的Getter方法.屬性名：自定義項17
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef17 () {
		return def17;
	}   
	/**
	 * 屬性def17的Setter方法.屬性名：自定義項17
	 * 創建日期:2018/5/7
	 * @param newDef17 java.lang.String
	 */
	public void setDef17 (java.lang.String newDef17 ) {
	 	this.def17 = newDef17;
	} 	 
	
	/**
	 * 屬性 def18的Getter方法.屬性名：自定義項18
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef18 () {
		return def18;
	}   
	/**
	 * 屬性def18的Setter方法.屬性名：自定義項18
	 * 創建日期:2018/5/7
	 * @param newDef18 java.lang.String
	 */
	public void setDef18 (java.lang.String newDef18 ) {
	 	this.def18 = newDef18;
	} 	 
	
	/**
	 * 屬性 def19的Getter方法.屬性名：自定義項19
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef19 () {
		return def19;
	}   
	/**
	 * 屬性def19的Setter方法.屬性名：自定義項19
	 * 創建日期:2018/5/7
	 * @param newDef19 java.lang.String
	 */
	public void setDef19 (java.lang.String newDef19 ) {
	 	this.def19 = newDef19;
	} 	 
	
	/**
	 * 屬性 def20的Getter方法.屬性名：自定義項20
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getDef20 () {
		return def20;
	}   
	/**
	 * 屬性def20的Setter方法.屬性名：自定義項20
	 * 創建日期:2018/5/7
	 * @param newDef20 java.lang.String
	 */
	public void setDef20 (java.lang.String newDef20 ) {
	 	this.def20 = newDef20;
	} 	 
	
	/**
	 * 屬性 pk_jobrank的Getter方法.屬性名：職等
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getPk_jobrank () {
		return pk_jobrank;
	}   
	/**
	 * 屬性pk_jobrank的Setter方法.屬性名：職等
	 * 創建日期:2018/5/7
	 * @param newPk_jobrank java.lang.String
	 */
	public void setPk_jobrank (java.lang.String newPk_jobrank ) {
	 	this.pk_jobrank = newPk_jobrank;
	} 	 
	
	/**
	 * 屬性 pk_groupinsurance的Getter方法.屬性名：團保投保設定
	 *  創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getPk_groupinsurance () {
		return pk_groupinsurance;
	}   
	/**
	 * 屬性pk_groupinsurance的Setter方法.屬性名：團保投保設定
	 * 創建日期:2018/5/7
	 * @param newPk_groupinsurance java.lang.String
	 */
	public void setPk_groupinsurance (java.lang.String newPk_groupinsurance ) {
	 	this.pk_groupinsurance = newPk_groupinsurance;
	} 	 
	
	/**
	 * 屬性 dr的Getter方法.屬性名：dr
	 *  創建日期:2018/5/7
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr () {
		return dr;
	}   
	/**
	 * 屬性dr的Setter方法.屬性名：dr
	 * 創建日期:2018/5/7
	 * @param newDr java.lang.Integer
	 */
	public void setDr (java.lang.Integer newDr ) {
	 	this.dr = newDr;
	} 	 
	
	/**
	 * 屬性 ts的Getter方法.屬性名：ts
	 *  創建日期:2018/5/7
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs () {
		return ts;
	}   
	/**
	 * 屬性ts的Setter方法.屬性名：ts
	 * 創建日期:2018/5/7
	 * @param newTs nc.vo.pub.lang.UFDateTime
	 */
	public void setTs (nc.vo.pub.lang.UFDateTime newTs ) {
	 	this.ts = newTs;
	} 	 
	
	
	/**
	  * <p>取得父VO主鍵字段.
	  * <p>
	  * 創建日期:2018/5/7
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主鍵.
	  * <p>
	  * 創建日期:2018/5/7
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
			
		return "id";
	}
    
	/**
	 * <p>返回表名稱
	 * <p>
	 * 創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "twhr_groupinsgrade";
	}    
	
	/**
	 * <p>返回表名稱.
	 * <p>
	 * 創建日期:2018/5/7
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "twhr_groupinsgrade";
	}    
    
    /**
	  * 按照默認方式創建構造子.
	  *
	  * 創建日期:2018/5/7
	  */
     public GroupInsuranceGradeVO() {
		super();	
	}    
	
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.twhr.groupinsurance.GroupInsuranceGradeVO" )
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("twhr.groupinsgrade");
		
   	}
     
}
