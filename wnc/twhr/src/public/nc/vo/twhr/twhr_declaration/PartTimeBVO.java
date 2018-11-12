package nc.vo.twhr.twhr_declaration;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class PartTimeBVO extends SuperVO {
/**
*所得人身份证号
*/
public static final String BENEFICIARY_ID="beneficiary_id";
/**
*所得人姓名
*/
public static final String BENEFICIARY_NAME="beneficiary_name";
/**
*序号
*/
public static final String NUM="num";
/**
*给付日期
*/
public static final String PAY_DATE="pay_date";
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
*兼职人员补充保费主键
*/
public static final String PK_PARTTIME="pk_parttime";
/**
*行号
*/
public static final String ROWNO="rowno";
/**
*单次所得给付金额
*/
public static final String SINGLE_PAY="single_pay";
/**
*单次扣缴补充保险费金额
*/
public static final String SINGLE_WITHHOLDING="single_withholding";
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
*薪资方案
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
* 获取所得人身份证号
*
* @return 所得人身份证号
*/
public String getBeneficiary_id () {
return (String) this.getAttributeValue( PartTimeBVO.BENEFICIARY_ID);
 } 

/** 
* 设置所得人身份证号
*
* @param beneficiary_id 所得人身份证号
*/
public void setBeneficiary_id ( String beneficiary_id) {
this.setAttributeValue( PartTimeBVO.BENEFICIARY_ID,beneficiary_id);
 } 

/** 
* 获取所得人姓名
*
* @return 所得人姓名
*/
public String getBeneficiary_name () {
return (String) this.getAttributeValue( PartTimeBVO.BENEFICIARY_NAME);
 } 

/** 
* 设置所得人姓名
*
* @param beneficiary_name 所得人姓名
*/
public void setBeneficiary_name ( String beneficiary_name) {
this.setAttributeValue( PartTimeBVO.BENEFICIARY_NAME,beneficiary_name);
 } 

/** 
* 获取序号
*
* @return 序号
*/
public Integer getNum () {
return (Integer) this.getAttributeValue( PartTimeBVO.NUM);
 } 

/** 
* 设置序号
*
* @param num 序号
*/
public void setNum ( Integer num) {
this.setAttributeValue( PartTimeBVO.NUM,num);
 } 

/** 
* 获取给付日期
*
* @return 给付日期
*/
public UFDate getPay_date () {
return (UFDate) this.getAttributeValue( PartTimeBVO.PAY_DATE);
 } 

/** 
* 设置给付日期
*
* @param pay_date 给付日期
*/
public void setPay_date ( UFDate pay_date) {
this.setAttributeValue( PartTimeBVO.PAY_DATE,pay_date);
 } 

/** 
* 获取上层单据主键
*
* @return 上层单据主键
*/
public String getPk_declaration () {
return (String) this.getAttributeValue( PartTimeBVO.PK_DECLARATION);
 } 

/** 
* 设置上层单据主键
*
* @param pk_declaration 上层单据主键
*/
public void setPk_declaration ( String pk_declaration) {
this.setAttributeValue( PartTimeBVO.PK_DECLARATION,pk_declaration);
 } 

/** 
* 获取部门
*
* @return 部门
*/
public String getPk_dept () {
return (String) this.getAttributeValue( PartTimeBVO.PK_DEPT);
 } 

/** 
* 设置部门
*
* @param pk_dept 部门
*/
public void setPk_dept ( String pk_dept) {
this.setAttributeValue( PartTimeBVO.PK_DEPT,pk_dept);
 } 

/** 
* 获取集团
*
* @return 集团
*/
public String getPk_group () {
return (String) this.getAttributeValue( PartTimeBVO.PK_GROUP);
 } 

/** 
* 设置集团
*
* @param pk_group 集团
*/
public void setPk_group ( String pk_group) {
this.setAttributeValue( PartTimeBVO.PK_GROUP,pk_group);
 } 

/** 
* 获取组织
*
* @return 组织
*/
public String getPk_org () {
return (String) this.getAttributeValue( PartTimeBVO.PK_ORG);
 } 

/** 
* 设置组织
*
* @param pk_org 组织
*/
public void setPk_org ( String pk_org) {
this.setAttributeValue( PartTimeBVO.PK_ORG,pk_org);
 } 

/** 
* 获取组织版本
*
* @return 组织版本
*/
public String getPk_org_v () {
return (String) this.getAttributeValue( PartTimeBVO.PK_ORG_V);
 } 

/** 
* 设置组织版本
*
* @param pk_org_v 组织版本
*/
public void setPk_org_v ( String pk_org_v) {
this.setAttributeValue( PartTimeBVO.PK_ORG_V,pk_org_v);
 } 

/** 
* 获取兼职人员补充保费主键
*
* @return 兼职人员补充保费主键
*/
public String getPk_parttime () {
return (String) this.getAttributeValue( PartTimeBVO.PK_PARTTIME);
 } 

/** 
* 设置兼职人员补充保费主键
*
* @param pk_parttime 兼职人员补充保费主键
*/
public void setPk_parttime ( String pk_parttime) {
this.setAttributeValue( PartTimeBVO.PK_PARTTIME,pk_parttime);
 } 

/** 
* 获取行号
*
* @return 行号
*/
public String getRowno () {
return (String) this.getAttributeValue( PartTimeBVO.ROWNO);
 } 

/** 
* 设置行号
*
* @param rowno 行号
*/
public void setRowno ( String rowno) {
this.setAttributeValue( PartTimeBVO.ROWNO,rowno);
 } 

/** 
* 获取单次所得给付金额
*
* @return 单次所得给付金额
*/
public UFDouble getSingle_pay () {
return (UFDouble) this.getAttributeValue( PartTimeBVO.SINGLE_PAY);
 } 

/** 
* 设置单次所得给付金额
*
* @param single_pay 单次所得给付金额
*/
public void setSingle_pay ( UFDouble single_pay) {
this.setAttributeValue( PartTimeBVO.SINGLE_PAY,single_pay);
 } 

/** 
* 获取单次扣缴补充保险费金额
*
* @return 单次扣缴补充保险费金额
*/
public UFDouble getSingle_withholding () {
return (UFDouble) this.getAttributeValue( PartTimeBVO.SINGLE_WITHHOLDING);
 } 

/** 
* 设置单次扣缴补充保险费金额
*
* @param single_withholding 单次扣缴补充保险费金额
*/
public void setSingle_withholding ( UFDouble single_withholding) {
this.setAttributeValue( PartTimeBVO.SINGLE_WITHHOLDING,single_withholding);
 } 

/** 
* 获取时间戳
*
* @return 时间戳
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( PartTimeBVO.TS);
 } 

/** 
* 设置时间戳
*
* @param ts 时间戳
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( PartTimeBVO.TS,ts);
 } 

/** 
* 获取自定义项1
*
* @return 自定义项1
*/
public String getVbdef1 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF1);
 } 

/** 
* 设置自定义项1
*
* @param vbdef1 自定义项1
*/
public void setVbdef1 ( String vbdef1) {
this.setAttributeValue( PartTimeBVO.VBDEF1,vbdef1);
 } 

/** 
* 获取自定义项10
*
* @return 自定义项10
*/
public String getVbdef10 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF10);
 } 

/** 
* 设置自定义项10
*
* @param vbdef10 自定义项10
*/
public void setVbdef10 ( String vbdef10) {
this.setAttributeValue( PartTimeBVO.VBDEF10,vbdef10);
 } 

/** 
* 获取自定义项11
*
* @return 自定义项11
*/
public String getVbdef11 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF11);
 } 

/** 
* 设置自定义项11
*
* @param vbdef11 自定义项11
*/
public void setVbdef11 ( String vbdef11) {
this.setAttributeValue( PartTimeBVO.VBDEF11,vbdef11);
 } 

/** 
* 获取自定义项12
*
* @return 自定义项12
*/
public String getVbdef12 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF12);
 } 

/** 
* 设置自定义项12
*
* @param vbdef12 自定义项12
*/
public void setVbdef12 ( String vbdef12) {
this.setAttributeValue( PartTimeBVO.VBDEF12,vbdef12);
 } 

/** 
* 获取自定义项13
*
* @return 自定义项13
*/
public String getVbdef13 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF13);
 } 

/** 
* 设置自定义项13
*
* @param vbdef13 自定义项13
*/
public void setVbdef13 ( String vbdef13) {
this.setAttributeValue( PartTimeBVO.VBDEF13,vbdef13);
 } 

/** 
* 获取自定义项14
*
* @return 自定义项14
*/
public String getVbdef14 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF14);
 } 

/** 
* 设置自定义项14
*
* @param vbdef14 自定义项14
*/
public void setVbdef14 ( String vbdef14) {
this.setAttributeValue( PartTimeBVO.VBDEF14,vbdef14);
 } 

/** 
* 获取自定义项15
*
* @return 自定义项15
*/
public String getVbdef15 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF15);
 } 

/** 
* 设置自定义项15
*
* @param vbdef15 自定义项15
*/
public void setVbdef15 ( String vbdef15) {
this.setAttributeValue( PartTimeBVO.VBDEF15,vbdef15);
 } 

/** 
* 获取自定义项16
*
* @return 自定义项16
*/
public String getVbdef16 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF16);
 } 

/** 
* 设置自定义项16
*
* @param vbdef16 自定义项16
*/
public void setVbdef16 ( String vbdef16) {
this.setAttributeValue( PartTimeBVO.VBDEF16,vbdef16);
 } 

/** 
* 获取自定义项17
*
* @return 自定义项17
*/
public String getVbdef17 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF17);
 } 

/** 
* 设置自定义项17
*
* @param vbdef17 自定义项17
*/
public void setVbdef17 ( String vbdef17) {
this.setAttributeValue( PartTimeBVO.VBDEF17,vbdef17);
 } 

/** 
* 获取自定义项18
*
* @return 自定义项18
*/
public String getVbdef18 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF18);
 } 

/** 
* 设置自定义项18
*
* @param vbdef18 自定义项18
*/
public void setVbdef18 ( String vbdef18) {
this.setAttributeValue( PartTimeBVO.VBDEF18,vbdef18);
 } 

/** 
* 获取自定义项19
*
* @return 自定义项19
*/
public String getVbdef19 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF19);
 } 

/** 
* 设置自定义项19
*
* @param vbdef19 自定义项19
*/
public void setVbdef19 ( String vbdef19) {
this.setAttributeValue( PartTimeBVO.VBDEF19,vbdef19);
 } 

/** 
* 获取薪资方案
*
* @return 薪资方案
*/
public String getVbdef2 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF2);
 } 

/** 
* 设置薪资方案
*
* @param vbdef2 薪资方案
*/
public void setVbdef2 ( String vbdef2) {
this.setAttributeValue( PartTimeBVO.VBDEF2,vbdef2);
 } 

/** 
* 获取自定义项20
*
* @return 自定义项20
*/
public String getVbdef20 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF20);
 } 

/** 
* 设置自定义项20
*
* @param vbdef20 自定义项20
*/
public void setVbdef20 ( String vbdef20) {
this.setAttributeValue( PartTimeBVO.VBDEF20,vbdef20);
 } 

/** 
* 获取薪资期间
*
* @return 薪资期间
*/
public String getVbdef3 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF3);
 } 

/** 
* 设置薪资期间
*
* @param vbdef3 薪资期间
*/
public void setVbdef3 ( String vbdef3) {
this.setAttributeValue( PartTimeBVO.VBDEF3,vbdef3);
 } 

/** 
* 获取员工
*
* @return 员工
*/
public String getVbdef4 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF4);
 } 

/** 
* 设置员工
*
* @param vbdef4 员工
*/
public void setVbdef4 ( String vbdef4) {
this.setAttributeValue( PartTimeBVO.VBDEF4,vbdef4);
 } 

/** 
* 获取自定义项5
*
* @return 自定义项5
*/
public String getVbdef5 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF5);
 } 

/** 
* 设置自定义项5
*
* @param vbdef5 自定义项5
*/
public void setVbdef5 ( String vbdef5) {
this.setAttributeValue( PartTimeBVO.VBDEF5,vbdef5);
 } 

/** 
* 获取自定义项6
*
* @return 自定义项6
*/
public String getVbdef6 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF6);
 } 

/** 
* 设置自定义项6
*
* @param vbdef6 自定义项6
*/
public void setVbdef6 ( String vbdef6) {
this.setAttributeValue( PartTimeBVO.VBDEF6,vbdef6);
 } 

/** 
* 获取自定义项7
*
* @return 自定义项7
*/
public String getVbdef7 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF7);
 } 

/** 
* 设置自定义项7
*
* @param vbdef7 自定义项7
*/
public void setVbdef7 ( String vbdef7) {
this.setAttributeValue( PartTimeBVO.VBDEF7,vbdef7);
 } 

/** 
* 获取自定义项8
*
* @return 自定义项8
*/
public String getVbdef8 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF8);
 } 

/** 
* 设置自定义项8
*
* @param vbdef8 自定义项8
*/
public void setVbdef8 ( String vbdef8) {
this.setAttributeValue( PartTimeBVO.VBDEF8,vbdef8);
 } 

/** 
* 获取自定义项9
*
* @return 自定义项9
*/
public String getVbdef9 () {
return (String) this.getAttributeValue( PartTimeBVO.VBDEF9);
 } 

/** 
* 设置自定义项9
*
* @param vbdef9 自定义项9
*/
public void setVbdef9 ( String vbdef9) {
this.setAttributeValue( PartTimeBVO.VBDEF9,vbdef9);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.PartTimeBVO");
  }
}