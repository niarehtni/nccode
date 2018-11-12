package nc.vo.twhr.twhr_declaration;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class CompanyBVO extends SuperVO {
/**
*公司承担补充保费
*/
public static final String COMPANY_BEAR="company_bear";
/**
*序号
*/
public static final String NUM="num";
/**
*给付日期
*/
public static final String PAY_DATE="pay_date";
/**
*给付金额
*/
public static final String PAY_MONEY="pay_money";
/**
*公司补充保费主键
*/
public static final String PK_COMPANY="pk_company";
/**
*上层单据主键
*/
public static final String PK_DECLARATION="pk_declaration";
/**
*部门
*/
public static final String PK_DEPT="pk_dept";
/**
*集团
*/
public static final String PK_GROUP="pk_group";
/**
*组织
*/
public static final String PK_ORG="pk_org";
/**
*组织版本
*/
public static final String PK_ORG_V="pk_org_v";
/**
*人员
*/
public static final String PK_PSNDOC="pk_psndoc";
/**
*薪资方案
*/
public static final String PK_WA_CLASS="pk_wa_class";
/**
*补充保费费基
*/
public static final String REPLENIS_BASE="replenis_base";
/**
*行号
*/
public static final String ROWNO="rowno";
/**
*投保总额
*/
public static final String TOTALINSURE="totalinsure";
/**
*时间戳
*/
public static final String TS="ts";
/**
*自定义项1
*/
public static final String VBDEF1="vbdef1";
/**
*自定义项10
*/
public static final String VBDEF10="vbdef10";
/**
*自定义项11
*/
public static final String VBDEF11="vbdef11";
/**
*自定义项12
*/
public static final String VBDEF12="vbdef12";
/**
*自定义项13
*/
public static final String VBDEF13="vbdef13";
/**
*自定义项14
*/
public static final String VBDEF14="vbdef14";
/**
*自定义项15
*/
public static final String VBDEF15="vbdef15";
/**
*自定义项16
*/
public static final String VBDEF16="vbdef16";
/**
*自定义项17
*/
public static final String VBDEF17="vbdef17";
/**
*自定义项18
*/
public static final String VBDEF18="vbdef18";
/**
*自定义项19
*/
public static final String VBDEF19="vbdef19";
/**
*自定义项2
*/
public static final String VBDEF2="vbdef2";
/**
*自定义项20
*/
public static final String VBDEF20="vbdef20";
/**
*薪资期间
*/
public static final String VBDEF3="vbdef3";
/**
*员工
*/
public static final String VBDEF4="vbdef4";
/**
*自定义项5
*/
public static final String VBDEF5="vbdef5";
/**
*自定义项6
*/
public static final String VBDEF6="vbdef6";
/**
*自定义项7
*/
public static final String VBDEF7="vbdef7";
/**
*自定义项8
*/
public static final String VBDEF8="vbdef8";
/**
*自定义项9
*/
public static final String VBDEF9="vbdef9";
/** 
* 获取公司承担补充保费
*
* @return 公司承担补充保费
*/
public UFDouble getCompany_bear () {
return (UFDouble) this.getAttributeValue( CompanyBVO.COMPANY_BEAR);
 } 

/** 
* 设置公司承担补充保费
*
* @param company_bear 公司承担补充保费
*/
public void setCompany_bear ( UFDouble company_bear) {
this.setAttributeValue( CompanyBVO.COMPANY_BEAR,company_bear);
 } 

/** 
* 获取序号
*
* @return 序号
*/
public Integer getNum () {
return (Integer) this.getAttributeValue( CompanyBVO.NUM);
 } 

/** 
* 设置序号
*
* @param num 序号
*/
public void setNum ( Integer num) {
this.setAttributeValue( CompanyBVO.NUM,num);
 } 

/** 
* 获取给付日期
*
* @return 给付日期
*/
public UFDate getPay_date () {
return (UFDate) this.getAttributeValue( CompanyBVO.PAY_DATE);
 } 

/** 
* 设置给付日期
*
* @param pay_date 给付日期
*/
public void setPay_date ( UFDate pay_date) {
this.setAttributeValue( CompanyBVO.PAY_DATE,pay_date);
 } 

/** 
* 获取给付金额
*
* @return 给付金额
*/
public UFDouble getPay_money () {
return (UFDouble) this.getAttributeValue( CompanyBVO.PAY_MONEY);
 } 

/** 
* 设置给付金额
*
* @param pay_money 给付金额
*/
public void setPay_money ( UFDouble pay_money) {
this.setAttributeValue( CompanyBVO.PAY_MONEY,pay_money);
 } 

/** 
* 获取公司补充保费主键
*
* @return 公司补充保费主键
*/
public String getPk_company () {
return (String) this.getAttributeValue( CompanyBVO.PK_COMPANY);
 } 

/** 
* 设置公司补充保费主键
*
* @param pk_company 公司补充保费主键
*/
public void setPk_company ( String pk_company) {
this.setAttributeValue( CompanyBVO.PK_COMPANY,pk_company);
 } 

/** 
* 获取上层单据主键
*
* @return 上层单据主键
*/
public String getPk_declaration () {
return (String) this.getAttributeValue( CompanyBVO.PK_DECLARATION);
 } 

/** 
* 设置上层单据主键
*
* @param pk_declaration 上层单据主键
*/
public void setPk_declaration ( String pk_declaration) {
this.setAttributeValue( CompanyBVO.PK_DECLARATION,pk_declaration);
 } 

/** 
* 获取部门
*
* @return 部门
*/
public String getPk_dept () {
return (String) this.getAttributeValue( CompanyBVO.PK_DEPT);
 } 

/** 
* 设置部门
*
* @param pk_dept 部门
*/
public void setPk_dept ( String pk_dept) {
this.setAttributeValue( CompanyBVO.PK_DEPT,pk_dept);
 } 

/** 
* 获取集团
*
* @return 集团
*/
public String getPk_group () {
return (String) this.getAttributeValue( CompanyBVO.PK_GROUP);
 } 

/** 
* 设置集团
*
* @param pk_group 集团
*/
public void setPk_group ( String pk_group) {
this.setAttributeValue( CompanyBVO.PK_GROUP,pk_group);
 } 

/** 
* 获取组织
*
* @return 组织
*/
public String getPk_org () {
return (String) this.getAttributeValue( CompanyBVO.PK_ORG);
 } 

/** 
* 设置组织
*
* @param pk_org 组织
*/
public void setPk_org ( String pk_org) {
this.setAttributeValue( CompanyBVO.PK_ORG,pk_org);
 } 

/** 
* 获取组织版本
*
* @return 组织版本
*/
public String getPk_org_v () {
return (String) this.getAttributeValue( CompanyBVO.PK_ORG_V);
 } 

/** 
* 设置组织版本
*
* @param pk_org_v 组织版本
*/
public void setPk_org_v ( String pk_org_v) {
this.setAttributeValue( CompanyBVO.PK_ORG_V,pk_org_v);
 } 

/** 
* 获取人员
*
* @return 人员
*/
public String getPk_psndoc () {
return (String) this.getAttributeValue( CompanyBVO.PK_PSNDOC);
 } 

/** 
* 设置人员
*
* @param pk_psndoc 人员
*/
public void setPk_psndoc ( String pk_psndoc) {
this.setAttributeValue( CompanyBVO.PK_PSNDOC,pk_psndoc);
 } 

/** 
* 获取薪资方案
*
* @return 薪资方案
*/
public String getPk_wa_class () {
return (String) this.getAttributeValue( CompanyBVO.PK_WA_CLASS);
 } 

/** 
* 设置薪资方案
*
* @param pk_wa_class 薪资方案
*/
public void setPk_wa_class ( String pk_wa_class) {
this.setAttributeValue( CompanyBVO.PK_WA_CLASS,pk_wa_class);
 } 

/** 
* 获取补充保费费基
*
* @return 补充保费费基
*/
public UFDouble getReplenis_base () {
return (UFDouble) this.getAttributeValue( CompanyBVO.REPLENIS_BASE);
 } 

/** 
* 设置补充保费费基
*
* @param replenis_base 补充保费费基
*/
public void setReplenis_base ( UFDouble replenis_base) {
this.setAttributeValue( CompanyBVO.REPLENIS_BASE,replenis_base);
 } 

/** 
* 获取行号
*
* @return 行号
*/
public String getRowno () {
return (String) this.getAttributeValue( CompanyBVO.ROWNO);
 } 

/** 
* 设置行号
*
* @param rowno 行号
*/
public void setRowno ( String rowno) {
this.setAttributeValue( CompanyBVO.ROWNO,rowno);
 } 

/** 
* 获取投保总额
*
* @return 投保总额
*/
public UFDouble getTotalinsure () {
return (UFDouble) this.getAttributeValue( CompanyBVO.TOTALINSURE);
 } 

/** 
* 设置投保总额
*
* @param totalinsure 投保总额
*/
public void setTotalinsure ( UFDouble totalinsure) {
this.setAttributeValue( CompanyBVO.TOTALINSURE,totalinsure);
 } 

/** 
* 获取时间戳
*
* @return 时间戳
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( CompanyBVO.TS);
 } 

/** 
* 设置时间戳
*
* @param ts 时间戳
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( CompanyBVO.TS,ts);
 } 

/** 
* 获取自定义项1
*
* @return 自定义项1
*/
public String getVbdef1 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF1);
 } 

/** 
* 设置自定义项1
*
* @param vbdef1 自定义项1
*/
public void setVbdef1 ( String vbdef1) {
this.setAttributeValue( CompanyBVO.VBDEF1,vbdef1);
 } 

/** 
* 获取自定义项10
*
* @return 自定义项10
*/
public String getVbdef10 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF10);
 } 

/** 
* 设置自定义项10
*
* @param vbdef10 自定义项10
*/
public void setVbdef10 ( String vbdef10) {
this.setAttributeValue( CompanyBVO.VBDEF10,vbdef10);
 } 

/** 
* 获取自定义项11
*
* @return 自定义项11
*/
public String getVbdef11 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF11);
 } 

/** 
* 设置自定义项11
*
* @param vbdef11 自定义项11
*/
public void setVbdef11 ( String vbdef11) {
this.setAttributeValue( CompanyBVO.VBDEF11,vbdef11);
 } 

/** 
* 获取自定义项12
*
* @return 自定义项12
*/
public String getVbdef12 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF12);
 } 

/** 
* 设置自定义项12
*
* @param vbdef12 自定义项12
*/
public void setVbdef12 ( String vbdef12) {
this.setAttributeValue( CompanyBVO.VBDEF12,vbdef12);
 } 

/** 
* 获取自定义项13
*
* @return 自定义项13
*/
public String getVbdef13 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF13);
 } 

/** 
* 设置自定义项13
*
* @param vbdef13 自定义项13
*/
public void setVbdef13 ( String vbdef13) {
this.setAttributeValue( CompanyBVO.VBDEF13,vbdef13);
 } 

/** 
* 获取自定义项14
*
* @return 自定义项14
*/
public String getVbdef14 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF14);
 } 

/** 
* 设置自定义项14
*
* @param vbdef14 自定义项14
*/
public void setVbdef14 ( String vbdef14) {
this.setAttributeValue( CompanyBVO.VBDEF14,vbdef14);
 } 

/** 
* 获取自定义项15
*
* @return 自定义项15
*/
public String getVbdef15 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF15);
 } 

/** 
* 设置自定义项15
*
* @param vbdef15 自定义项15
*/
public void setVbdef15 ( String vbdef15) {
this.setAttributeValue( CompanyBVO.VBDEF15,vbdef15);
 } 

/** 
* 获取自定义项16
*
* @return 自定义项16
*/
public String getVbdef16 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF16);
 } 

/** 
* 设置自定义项16
*
* @param vbdef16 自定义项16
*/
public void setVbdef16 ( String vbdef16) {
this.setAttributeValue( CompanyBVO.VBDEF16,vbdef16);
 } 

/** 
* 获取自定义项17
*
* @return 自定义项17
*/
public String getVbdef17 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF17);
 } 

/** 
* 设置自定义项17
*
* @param vbdef17 自定义项17
*/
public void setVbdef17 ( String vbdef17) {
this.setAttributeValue( CompanyBVO.VBDEF17,vbdef17);
 } 

/** 
* 获取自定义项18
*
* @return 自定义项18
*/
public String getVbdef18 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF18);
 } 

/** 
* 设置自定义项18
*
* @param vbdef18 自定义项18
*/
public void setVbdef18 ( String vbdef18) {
this.setAttributeValue( CompanyBVO.VBDEF18,vbdef18);
 } 

/** 
* 获取自定义项19
*
* @return 自定义项19
*/
public String getVbdef19 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF19);
 } 

/** 
* 设置自定义项19
*
* @param vbdef19 自定义项19
*/
public void setVbdef19 ( String vbdef19) {
this.setAttributeValue( CompanyBVO.VBDEF19,vbdef19);
 } 

/** 
* 获取自定义项2
*
* @return 自定义项2
*/
public String getVbdef2 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF2);
 } 

/** 
* 设置自定义项2
*
* @param vbdef2 自定义项2
*/
public void setVbdef2 ( String vbdef2) {
this.setAttributeValue( CompanyBVO.VBDEF2,vbdef2);
 } 

/** 
* 获取自定义项20
*
* @return 自定义项20
*/
public String getVbdef20 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF20);
 } 

/** 
* 设置自定义项20
*
* @param vbdef20 自定义项20
*/
public void setVbdef20 ( String vbdef20) {
this.setAttributeValue( CompanyBVO.VBDEF20,vbdef20);
 } 

/** 
* 获取薪资期间
*
* @return 薪资期间
*/
public String getVbdef3 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF3);
 } 

/** 
* 设置薪资期间
*
* @param vbdef3 薪资期间
*/
public void setVbdef3 ( String vbdef3) {
this.setAttributeValue( CompanyBVO.VBDEF3,vbdef3);
 } 

/** 
* 获取员工
*
* @return 员工
*/
public String getVbdef4 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF4);
 } 

/** 
* 设置员工
*
* @param vbdef4 员工
*/
public void setVbdef4 ( String vbdef4) {
this.setAttributeValue( CompanyBVO.VBDEF4,vbdef4);
 } 

/** 
* 获取自定义项5
*
* @return 自定义项5
*/
public String getVbdef5 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF5);
 } 

/** 
* 设置自定义项5
*
* @param vbdef5 自定义项5
*/
public void setVbdef5 ( String vbdef5) {
this.setAttributeValue( CompanyBVO.VBDEF5,vbdef5);
 } 

/** 
* 获取自定义项6
*
* @return 自定义项6
*/
public String getVbdef6 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF6);
 } 

/** 
* 设置自定义项6
*
* @param vbdef6 自定义项6
*/
public void setVbdef6 ( String vbdef6) {
this.setAttributeValue( CompanyBVO.VBDEF6,vbdef6);
 } 

/** 
* 获取自定义项7
*
* @return 自定义项7
*/
public String getVbdef7 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF7);
 } 

/** 
* 设置自定义项7
*
* @param vbdef7 自定义项7
*/
public void setVbdef7 ( String vbdef7) {
this.setAttributeValue( CompanyBVO.VBDEF7,vbdef7);
 } 

/** 
* 获取自定义项8
*
* @return 自定义项8
*/
public String getVbdef8 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF8);
 } 

/** 
* 设置自定义项8
*
* @param vbdef8 自定义项8
*/
public void setVbdef8 ( String vbdef8) {
this.setAttributeValue( CompanyBVO.VBDEF8,vbdef8);
 } 

/** 
* 获取自定义项9
*
* @return 自定义项9
*/
public String getVbdef9 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF9);
 } 

/** 
* 设置自定义项9
*
* @param vbdef9 自定义项9
*/
public void setVbdef9 ( String vbdef9) {
this.setAttributeValue( CompanyBVO.VBDEF9,vbdef9);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.CompanyBVO");
  }
}