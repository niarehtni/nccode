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
*��˾�е����䱣��
*/
public static final String COMPANY_BEAR="company_bear";
/**
*���
*/
public static final String NUM="num";
/**
*��������
*/
public static final String PAY_DATE="pay_date";
/**
*�������
*/
public static final String PAY_MONEY="pay_money";
/**
*��˾���䱣������
*/
public static final String PK_COMPANY="pk_company";
/**
*�ϲ㵥������
*/
public static final String PK_DECLARATION="pk_declaration";
/**
*����
*/
public static final String PK_DEPT="pk_dept";
/**
*����
*/
public static final String PK_GROUP="pk_group";
/**
*��֯
*/
public static final String PK_ORG="pk_org";
/**
*��֯�汾
*/
public static final String PK_ORG_V="pk_org_v";
/**
*��Ա
*/
public static final String PK_PSNDOC="pk_psndoc";
/**
*н�ʷ���
*/
public static final String PK_WA_CLASS="pk_wa_class";
/**
*���䱣�ѷѻ�
*/
public static final String REPLENIS_BASE="replenis_base";
/**
*�к�
*/
public static final String ROWNO="rowno";
/**
*Ͷ���ܶ�
*/
public static final String TOTALINSURE="totalinsure";
/**
*ʱ���
*/
public static final String TS="ts";
/**
*�Զ�����1
*/
public static final String VBDEF1="vbdef1";
/**
*�Զ�����10
*/
public static final String VBDEF10="vbdef10";
/**
*�Զ�����11
*/
public static final String VBDEF11="vbdef11";
/**
*�Զ�����12
*/
public static final String VBDEF12="vbdef12";
/**
*�Զ�����13
*/
public static final String VBDEF13="vbdef13";
/**
*�Զ�����14
*/
public static final String VBDEF14="vbdef14";
/**
*�Զ�����15
*/
public static final String VBDEF15="vbdef15";
/**
*�Զ�����16
*/
public static final String VBDEF16="vbdef16";
/**
*�Զ�����17
*/
public static final String VBDEF17="vbdef17";
/**
*�Զ�����18
*/
public static final String VBDEF18="vbdef18";
/**
*�Զ�����19
*/
public static final String VBDEF19="vbdef19";
/**
*�Զ�����2
*/
public static final String VBDEF2="vbdef2";
/**
*�Զ�����20
*/
public static final String VBDEF20="vbdef20";
/**
*н���ڼ�
*/
public static final String VBDEF3="vbdef3";
/**
*Ա��
*/
public static final String VBDEF4="vbdef4";
/**
*�Զ�����5
*/
public static final String VBDEF5="vbdef5";
/**
*�Զ�����6
*/
public static final String VBDEF6="vbdef6";
/**
*�Զ�����7
*/
public static final String VBDEF7="vbdef7";
/**
*�Զ�����8
*/
public static final String VBDEF8="vbdef8";
/**
*�Զ�����9
*/
public static final String VBDEF9="vbdef9";
/** 
* ��ȡ��˾�е����䱣��
*
* @return ��˾�е����䱣��
*/
public UFDouble getCompany_bear () {
return (UFDouble) this.getAttributeValue( CompanyBVO.COMPANY_BEAR);
 } 

/** 
* ���ù�˾�е����䱣��
*
* @param company_bear ��˾�е����䱣��
*/
public void setCompany_bear ( UFDouble company_bear) {
this.setAttributeValue( CompanyBVO.COMPANY_BEAR,company_bear);
 } 

/** 
* ��ȡ���
*
* @return ���
*/
public Integer getNum () {
return (Integer) this.getAttributeValue( CompanyBVO.NUM);
 } 

/** 
* �������
*
* @param num ���
*/
public void setNum ( Integer num) {
this.setAttributeValue( CompanyBVO.NUM,num);
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public UFDate getPay_date () {
return (UFDate) this.getAttributeValue( CompanyBVO.PAY_DATE);
 } 

/** 
* ���ø�������
*
* @param pay_date ��������
*/
public void setPay_date ( UFDate pay_date) {
this.setAttributeValue( CompanyBVO.PAY_DATE,pay_date);
 } 

/** 
* ��ȡ�������
*
* @return �������
*/
public UFDouble getPay_money () {
return (UFDouble) this.getAttributeValue( CompanyBVO.PAY_MONEY);
 } 

/** 
* ���ø������
*
* @param pay_money �������
*/
public void setPay_money ( UFDouble pay_money) {
this.setAttributeValue( CompanyBVO.PAY_MONEY,pay_money);
 } 

/** 
* ��ȡ��˾���䱣������
*
* @return ��˾���䱣������
*/
public String getPk_company () {
return (String) this.getAttributeValue( CompanyBVO.PK_COMPANY);
 } 

/** 
* ���ù�˾���䱣������
*
* @param pk_company ��˾���䱣������
*/
public void setPk_company ( String pk_company) {
this.setAttributeValue( CompanyBVO.PK_COMPANY,pk_company);
 } 

/** 
* ��ȡ�ϲ㵥������
*
* @return �ϲ㵥������
*/
public String getPk_declaration () {
return (String) this.getAttributeValue( CompanyBVO.PK_DECLARATION);
 } 

/** 
* �����ϲ㵥������
*
* @param pk_declaration �ϲ㵥������
*/
public void setPk_declaration ( String pk_declaration) {
this.setAttributeValue( CompanyBVO.PK_DECLARATION,pk_declaration);
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getPk_dept () {
return (String) this.getAttributeValue( CompanyBVO.PK_DEPT);
 } 

/** 
* ���ò���
*
* @param pk_dept ����
*/
public void setPk_dept ( String pk_dept) {
this.setAttributeValue( CompanyBVO.PK_DEPT,pk_dept);
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getPk_group () {
return (String) this.getAttributeValue( CompanyBVO.PK_GROUP);
 } 

/** 
* ���ü���
*
* @param pk_group ����
*/
public void setPk_group ( String pk_group) {
this.setAttributeValue( CompanyBVO.PK_GROUP,pk_group);
 } 

/** 
* ��ȡ��֯
*
* @return ��֯
*/
public String getPk_org () {
return (String) this.getAttributeValue( CompanyBVO.PK_ORG);
 } 

/** 
* ������֯
*
* @param pk_org ��֯
*/
public void setPk_org ( String pk_org) {
this.setAttributeValue( CompanyBVO.PK_ORG,pk_org);
 } 

/** 
* ��ȡ��֯�汾
*
* @return ��֯�汾
*/
public String getPk_org_v () {
return (String) this.getAttributeValue( CompanyBVO.PK_ORG_V);
 } 

/** 
* ������֯�汾
*
* @param pk_org_v ��֯�汾
*/
public void setPk_org_v ( String pk_org_v) {
this.setAttributeValue( CompanyBVO.PK_ORG_V,pk_org_v);
 } 

/** 
* ��ȡ��Ա
*
* @return ��Ա
*/
public String getPk_psndoc () {
return (String) this.getAttributeValue( CompanyBVO.PK_PSNDOC);
 } 

/** 
* ������Ա
*
* @param pk_psndoc ��Ա
*/
public void setPk_psndoc ( String pk_psndoc) {
this.setAttributeValue( CompanyBVO.PK_PSNDOC,pk_psndoc);
 } 

/** 
* ��ȡн�ʷ���
*
* @return н�ʷ���
*/
public String getPk_wa_class () {
return (String) this.getAttributeValue( CompanyBVO.PK_WA_CLASS);
 } 

/** 
* ����н�ʷ���
*
* @param pk_wa_class н�ʷ���
*/
public void setPk_wa_class ( String pk_wa_class) {
this.setAttributeValue( CompanyBVO.PK_WA_CLASS,pk_wa_class);
 } 

/** 
* ��ȡ���䱣�ѷѻ�
*
* @return ���䱣�ѷѻ�
*/
public UFDouble getReplenis_base () {
return (UFDouble) this.getAttributeValue( CompanyBVO.REPLENIS_BASE);
 } 

/** 
* ���ò��䱣�ѷѻ�
*
* @param replenis_base ���䱣�ѷѻ�
*/
public void setReplenis_base ( UFDouble replenis_base) {
this.setAttributeValue( CompanyBVO.REPLENIS_BASE,replenis_base);
 } 

/** 
* ��ȡ�к�
*
* @return �к�
*/
public String getRowno () {
return (String) this.getAttributeValue( CompanyBVO.ROWNO);
 } 

/** 
* �����к�
*
* @param rowno �к�
*/
public void setRowno ( String rowno) {
this.setAttributeValue( CompanyBVO.ROWNO,rowno);
 } 

/** 
* ��ȡͶ���ܶ�
*
* @return Ͷ���ܶ�
*/
public UFDouble getTotalinsure () {
return (UFDouble) this.getAttributeValue( CompanyBVO.TOTALINSURE);
 } 

/** 
* ����Ͷ���ܶ�
*
* @param totalinsure Ͷ���ܶ�
*/
public void setTotalinsure ( UFDouble totalinsure) {
this.setAttributeValue( CompanyBVO.TOTALINSURE,totalinsure);
 } 

/** 
* ��ȡʱ���
*
* @return ʱ���
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( CompanyBVO.TS);
 } 

/** 
* ����ʱ���
*
* @param ts ʱ���
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( CompanyBVO.TS,ts);
 } 

/** 
* ��ȡ�Զ�����1
*
* @return �Զ�����1
*/
public String getVbdef1 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF1);
 } 

/** 
* �����Զ�����1
*
* @param vbdef1 �Զ�����1
*/
public void setVbdef1 ( String vbdef1) {
this.setAttributeValue( CompanyBVO.VBDEF1,vbdef1);
 } 

/** 
* ��ȡ�Զ�����10
*
* @return �Զ�����10
*/
public String getVbdef10 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF10);
 } 

/** 
* �����Զ�����10
*
* @param vbdef10 �Զ�����10
*/
public void setVbdef10 ( String vbdef10) {
this.setAttributeValue( CompanyBVO.VBDEF10,vbdef10);
 } 

/** 
* ��ȡ�Զ�����11
*
* @return �Զ�����11
*/
public String getVbdef11 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF11);
 } 

/** 
* �����Զ�����11
*
* @param vbdef11 �Զ�����11
*/
public void setVbdef11 ( String vbdef11) {
this.setAttributeValue( CompanyBVO.VBDEF11,vbdef11);
 } 

/** 
* ��ȡ�Զ�����12
*
* @return �Զ�����12
*/
public String getVbdef12 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF12);
 } 

/** 
* �����Զ�����12
*
* @param vbdef12 �Զ�����12
*/
public void setVbdef12 ( String vbdef12) {
this.setAttributeValue( CompanyBVO.VBDEF12,vbdef12);
 } 

/** 
* ��ȡ�Զ�����13
*
* @return �Զ�����13
*/
public String getVbdef13 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF13);
 } 

/** 
* �����Զ�����13
*
* @param vbdef13 �Զ�����13
*/
public void setVbdef13 ( String vbdef13) {
this.setAttributeValue( CompanyBVO.VBDEF13,vbdef13);
 } 

/** 
* ��ȡ�Զ�����14
*
* @return �Զ�����14
*/
public String getVbdef14 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF14);
 } 

/** 
* �����Զ�����14
*
* @param vbdef14 �Զ�����14
*/
public void setVbdef14 ( String vbdef14) {
this.setAttributeValue( CompanyBVO.VBDEF14,vbdef14);
 } 

/** 
* ��ȡ�Զ�����15
*
* @return �Զ�����15
*/
public String getVbdef15 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF15);
 } 

/** 
* �����Զ�����15
*
* @param vbdef15 �Զ�����15
*/
public void setVbdef15 ( String vbdef15) {
this.setAttributeValue( CompanyBVO.VBDEF15,vbdef15);
 } 

/** 
* ��ȡ�Զ�����16
*
* @return �Զ�����16
*/
public String getVbdef16 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF16);
 } 

/** 
* �����Զ�����16
*
* @param vbdef16 �Զ�����16
*/
public void setVbdef16 ( String vbdef16) {
this.setAttributeValue( CompanyBVO.VBDEF16,vbdef16);
 } 

/** 
* ��ȡ�Զ�����17
*
* @return �Զ�����17
*/
public String getVbdef17 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF17);
 } 

/** 
* �����Զ�����17
*
* @param vbdef17 �Զ�����17
*/
public void setVbdef17 ( String vbdef17) {
this.setAttributeValue( CompanyBVO.VBDEF17,vbdef17);
 } 

/** 
* ��ȡ�Զ�����18
*
* @return �Զ�����18
*/
public String getVbdef18 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF18);
 } 

/** 
* �����Զ�����18
*
* @param vbdef18 �Զ�����18
*/
public void setVbdef18 ( String vbdef18) {
this.setAttributeValue( CompanyBVO.VBDEF18,vbdef18);
 } 

/** 
* ��ȡ�Զ�����19
*
* @return �Զ�����19
*/
public String getVbdef19 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF19);
 } 

/** 
* �����Զ�����19
*
* @param vbdef19 �Զ�����19
*/
public void setVbdef19 ( String vbdef19) {
this.setAttributeValue( CompanyBVO.VBDEF19,vbdef19);
 } 

/** 
* ��ȡ�Զ�����2
*
* @return �Զ�����2
*/
public String getVbdef2 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF2);
 } 

/** 
* �����Զ�����2
*
* @param vbdef2 �Զ�����2
*/
public void setVbdef2 ( String vbdef2) {
this.setAttributeValue( CompanyBVO.VBDEF2,vbdef2);
 } 

/** 
* ��ȡ�Զ�����20
*
* @return �Զ�����20
*/
public String getVbdef20 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF20);
 } 

/** 
* �����Զ�����20
*
* @param vbdef20 �Զ�����20
*/
public void setVbdef20 ( String vbdef20) {
this.setAttributeValue( CompanyBVO.VBDEF20,vbdef20);
 } 

/** 
* ��ȡн���ڼ�
*
* @return н���ڼ�
*/
public String getVbdef3 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF3);
 } 

/** 
* ����н���ڼ�
*
* @param vbdef3 н���ڼ�
*/
public void setVbdef3 ( String vbdef3) {
this.setAttributeValue( CompanyBVO.VBDEF3,vbdef3);
 } 

/** 
* ��ȡԱ��
*
* @return Ա��
*/
public String getVbdef4 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF4);
 } 

/** 
* ����Ա��
*
* @param vbdef4 Ա��
*/
public void setVbdef4 ( String vbdef4) {
this.setAttributeValue( CompanyBVO.VBDEF4,vbdef4);
 } 

/** 
* ��ȡ�Զ�����5
*
* @return �Զ�����5
*/
public String getVbdef5 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF5);
 } 

/** 
* �����Զ�����5
*
* @param vbdef5 �Զ�����5
*/
public void setVbdef5 ( String vbdef5) {
this.setAttributeValue( CompanyBVO.VBDEF5,vbdef5);
 } 

/** 
* ��ȡ�Զ�����6
*
* @return �Զ�����6
*/
public String getVbdef6 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF6);
 } 

/** 
* �����Զ�����6
*
* @param vbdef6 �Զ�����6
*/
public void setVbdef6 ( String vbdef6) {
this.setAttributeValue( CompanyBVO.VBDEF6,vbdef6);
 } 

/** 
* ��ȡ�Զ�����7
*
* @return �Զ�����7
*/
public String getVbdef7 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF7);
 } 

/** 
* �����Զ�����7
*
* @param vbdef7 �Զ�����7
*/
public void setVbdef7 ( String vbdef7) {
this.setAttributeValue( CompanyBVO.VBDEF7,vbdef7);
 } 

/** 
* ��ȡ�Զ�����8
*
* @return �Զ�����8
*/
public String getVbdef8 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF8);
 } 

/** 
* �����Զ�����8
*
* @param vbdef8 �Զ�����8
*/
public void setVbdef8 ( String vbdef8) {
this.setAttributeValue( CompanyBVO.VBDEF8,vbdef8);
 } 

/** 
* ��ȡ�Զ�����9
*
* @return �Զ�����9
*/
public String getVbdef9 () {
return (String) this.getAttributeValue( CompanyBVO.VBDEF9);
 } 

/** 
* �����Զ�����9
*
* @param vbdef9 �Զ�����9
*/
public void setVbdef9 ( String vbdef9) {
this.setAttributeValue( CompanyBVO.VBDEF9,vbdef9);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.CompanyBVO");
  }
}