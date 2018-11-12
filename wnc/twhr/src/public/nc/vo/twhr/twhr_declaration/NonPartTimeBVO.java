package nc.vo.twhr.twhr_declaration;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class NonPartTimeBVO extends SuperVO {
/**
*����������֤��
*/
public static final String BENEFICIARY_ID="beneficiary_id";
/**
*����������
*/
public static final String BENEFICIARY_NAME="beneficiary_name";
/**
*�۷ѵ���Ͷ�����
*/
public static final String DEDUCTIONS_MONTH_INSURE="deductions_month_insure";
/**
*Ͷ����λ����
*/
public static final String INSURANCE_UNIT_CODE="insurance_unit_code";
/**
*���
*/
public static final String NUM="num";
/**
*��������
*/
public static final String PAY_DATE="pay_date";
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
*�Ǽ�ְ��Ա���䱣������
*/
public static final String PK_NONPARTTIME="pk_nonparttime";
/**
*��֯
*/
public static final String PK_ORG="pk_org";
/**
*��֯�汾
*/
public static final String PK_ORG_V="pk_org_v";
/**
*�к�
*/
public static final String ROWNO="rowno";
/**
*���θ���������
*/
public static final String SINGLE_PAY="single_pay";
/**
*���ο۽ɲ��䱣�շѽ��
*/
public static final String SINGLE_WITHHOLDING="single_withholding";
/**
*ͬ����ۼƽ�����
*/
public static final String TOTALBONUSFORYEAR="totalbonusforyear";
/**
*����ڼ�
*/
public static final String TEMPDATE="tempdate";
/**
*������Դ��֯
*/
public static final String TEMPORG="temporg";
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
*н�ʷ���
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
* ��ȡ����������֤��
*
* @return ����������֤��
*/
public String getBeneficiary_id () {
return (String) this.getAttributeValue( NonPartTimeBVO.BENEFICIARY_ID);
 } 

/** 
* ��������������֤��
*
* @param beneficiary_id ����������֤��
*/
public void setBeneficiary_id ( String beneficiary_id) {
this.setAttributeValue( NonPartTimeBVO.BENEFICIARY_ID,beneficiary_id);
 } 

/** 
* ��ȡ����������
*
* @return ����������
*/
public String getBeneficiary_name () {
return (String) this.getAttributeValue( NonPartTimeBVO.BENEFICIARY_NAME);
 } 

/** 
* ��������������
*
* @param beneficiary_name ����������
*/
public void setBeneficiary_name ( String beneficiary_name) {
this.setAttributeValue( NonPartTimeBVO.BENEFICIARY_NAME,beneficiary_name);
 } 

/** 
* ��ȡ�۷ѵ���Ͷ�����
*
* @return �۷ѵ���Ͷ�����
*/
public UFDouble getDeductions_month_insure () {
return (UFDouble) this.getAttributeValue( NonPartTimeBVO.DEDUCTIONS_MONTH_INSURE);
 } 

/** 
* ���ÿ۷ѵ���Ͷ�����
*
* @param deductions_month_insure �۷ѵ���Ͷ�����
*/
public void setDeductions_month_insure ( UFDouble deductions_month_insure) {
this.setAttributeValue( NonPartTimeBVO.DEDUCTIONS_MONTH_INSURE,deductions_month_insure);
 } 

/** 
* ��ȡͶ����λ����
*
* @return Ͷ����λ����
*/
public String getInsurance_unit_code () {
return (String) this.getAttributeValue( NonPartTimeBVO.INSURANCE_UNIT_CODE);
 } 

/** 
* ����Ͷ����λ����
*
* @param insurance_unit_code Ͷ����λ����
*/
public void setInsurance_unit_code ( String insurance_unit_code) {
this.setAttributeValue( NonPartTimeBVO.INSURANCE_UNIT_CODE,insurance_unit_code);
 } 

/** 
* ��ȡ���
*
* @return ���
*/
public Integer getNum () {
return (Integer) this.getAttributeValue( NonPartTimeBVO.NUM);
 } 

/** 
* �������
*
* @param num ���
*/
public void setNum ( Integer num) {
this.setAttributeValue( NonPartTimeBVO.NUM,num);
 } 

/** 
* ��ȡ��������
*
* @return ��������
*/
public UFDate getPay_date () {
return (UFDate) this.getAttributeValue( NonPartTimeBVO.PAY_DATE);
 } 

/** 
* ���ø�������
*
* @param pay_date ��������
*/
public void setPay_date ( UFDate pay_date) {
this.setAttributeValue( NonPartTimeBVO.PAY_DATE,pay_date);
 } 

/** 
* ��ȡ�ϲ㵥������
*
* @return �ϲ㵥������
*/
public String getPk_declaration () {
return (String) this.getAttributeValue( NonPartTimeBVO.PK_DECLARATION);
 } 

/** 
* �����ϲ㵥������
*
* @param pk_declaration �ϲ㵥������
*/
public void setPk_declaration ( String pk_declaration) {
this.setAttributeValue( NonPartTimeBVO.PK_DECLARATION,pk_declaration);
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getPk_dept () {
return (String) this.getAttributeValue( NonPartTimeBVO.PK_DEPT);
 } 

/** 
* ���ò���
*
* @param pk_dept ����
*/
public void setPk_dept ( String pk_dept) {
this.setAttributeValue( NonPartTimeBVO.PK_DEPT,pk_dept);
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getPk_group () {
return (String) this.getAttributeValue( NonPartTimeBVO.PK_GROUP);
 } 

/** 
* ���ü���
*
* @param pk_group ����
*/
public void setPk_group ( String pk_group) {
this.setAttributeValue( NonPartTimeBVO.PK_GROUP,pk_group);
 } 

/** 
* ��ȡ�Ǽ�ְ��Ա���䱣������
*
* @return �Ǽ�ְ��Ա���䱣������
*/
public String getPk_nonparttime () {
return (String) this.getAttributeValue( NonPartTimeBVO.PK_NONPARTTIME);
 } 

/** 
* ���÷Ǽ�ְ��Ա���䱣������
*
* @param pk_nonparttime �Ǽ�ְ��Ա���䱣������
*/
public void setPk_nonparttime ( String pk_nonparttime) {
this.setAttributeValue( NonPartTimeBVO.PK_NONPARTTIME,pk_nonparttime);
 } 

/** 
* ��ȡ��֯
*
* @return ��֯
*/
public String getPk_org () {
return (String) this.getAttributeValue( NonPartTimeBVO.PK_ORG);
 } 

/** 
* ������֯
*
* @param pk_org ��֯
*/
public void setPk_org ( String pk_org) {
this.setAttributeValue( NonPartTimeBVO.PK_ORG,pk_org);
 } 

/** 
* ��ȡ��֯�汾
*
* @return ��֯�汾
*/
public String getPk_org_v () {
return (String) this.getAttributeValue( NonPartTimeBVO.PK_ORG_V);
 } 

/** 
* ������֯�汾
*
* @param pk_org_v ��֯�汾
*/
public void setPk_org_v ( String pk_org_v) {
this.setAttributeValue( NonPartTimeBVO.PK_ORG_V,pk_org_v);
 } 

/** 
* ��ȡ�к�
*
* @return �к�
*/
public String getRowno () {
return (String) this.getAttributeValue( NonPartTimeBVO.ROWNO);
 } 

/** 
* �����к�
*
* @param rowno �к�
*/
public void setRowno ( String rowno) {
this.setAttributeValue( NonPartTimeBVO.ROWNO,rowno);
 } 

/** 
* ��ȡ���θ���������
*
* @return ���θ���������
*/
public UFDouble getSingle_pay () {
return (UFDouble) this.getAttributeValue( NonPartTimeBVO.SINGLE_PAY);
 } 

/** 
* ���õ��θ���������
*
* @param single_pay ���θ���������
*/
public void setSingle_pay ( UFDouble single_pay) {
this.setAttributeValue( NonPartTimeBVO.SINGLE_PAY,single_pay);
 } 

/** 
* ��ȡ���ο۽ɲ��䱣�շѽ��
*
* @return ���ο۽ɲ��䱣�շѽ��
*/
public UFDouble getSingle_withholding () {
return (UFDouble) this.getAttributeValue( NonPartTimeBVO.SINGLE_WITHHOLDING);
 } 

/** 
* ���õ��ο۽ɲ��䱣�շѽ��
*
* @param single_withholding ���ο۽ɲ��䱣�շѽ��
*/
public void setSingle_withholding ( UFDouble single_withholding) {
this.setAttributeValue( NonPartTimeBVO.SINGLE_WITHHOLDING,single_withholding);
 } 

/** 
* ��ȡͬ����ۼƽ�����
*
* @return ͬ����ۼƽ�����
*/
public UFDouble getTotalbonusforyear () {
return (UFDouble) this.getAttributeValue( NonPartTimeBVO.TOTALBONUSFORYEAR);
 } 

/** 
* ����ͬ����ۼƽ�����
*
* @param totalbonusforyear ͬ����ۼƽ�����
*/
public void setTotalbonusforyear ( UFDouble totalbonusforyear) {
this.setAttributeValue( NonPartTimeBVO.TOTALBONUSFORYEAR,totalbonusforyear);
 } 

/** 
* ��ȡ����ڼ�
*
* @return ����ڼ�
*/
public String getTempdate () {
return (String) this.getAttributeValue( NonPartTimeBVO.TEMPDATE);
 } 

/** 
* ���û���ڼ�
*
* @param tempdate ����ڼ�
*/
public void setTempdate ( String tempdate) {
this.setAttributeValue( NonPartTimeBVO.TEMPDATE,tempdate);
 } 

/** 
* ��ȡ������Դ��֯
*
* @return ������Դ��֯
*/
public String getTemporg () {
return (String) this.getAttributeValue( NonPartTimeBVO.TEMPORG);
 } 

/** 
* ����������Դ��֯
*
* @param temporg ������Դ��֯
*/
public void setTemporg ( String temporg) {
this.setAttributeValue( NonPartTimeBVO.TEMPORG,temporg);
 } 

/** 
* ��ȡʱ���
*
* @return ʱ���
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( NonPartTimeBVO.TS);
 } 

/** 
* ����ʱ���
*
* @param ts ʱ���
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( NonPartTimeBVO.TS,ts);
 } 

/** 
* ��ȡ�Զ�����1
*
* @return �Զ�����1
*/
public String getVbdef1 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF1);
 } 

/** 
* �����Զ�����1
*
* @param vbdef1 �Զ�����1
*/
public void setVbdef1 ( String vbdef1) {
this.setAttributeValue( NonPartTimeBVO.VBDEF1,vbdef1);
 } 

/** 
* ��ȡ�Զ�����10
*
* @return �Զ�����10
*/
public String getVbdef10 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF10);
 } 

/** 
* �����Զ�����10
*
* @param vbdef10 �Զ�����10
*/
public void setVbdef10 ( String vbdef10) {
this.setAttributeValue( NonPartTimeBVO.VBDEF10,vbdef10);
 } 

/** 
* ��ȡ�Զ�����11
*
* @return �Զ�����11
*/
public String getVbdef11 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF11);
 } 

/** 
* �����Զ�����11
*
* @param vbdef11 �Զ�����11
*/
public void setVbdef11 ( String vbdef11) {
this.setAttributeValue( NonPartTimeBVO.VBDEF11,vbdef11);
 } 

/** 
* ��ȡ�Զ�����12
*
* @return �Զ�����12
*/
public String getVbdef12 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF12);
 } 

/** 
* �����Զ�����12
*
* @param vbdef12 �Զ�����12
*/
public void setVbdef12 ( String vbdef12) {
this.setAttributeValue( NonPartTimeBVO.VBDEF12,vbdef12);
 } 

/** 
* ��ȡ�Զ�����13
*
* @return �Զ�����13
*/
public String getVbdef13 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF13);
 } 

/** 
* �����Զ�����13
*
* @param vbdef13 �Զ�����13
*/
public void setVbdef13 ( String vbdef13) {
this.setAttributeValue( NonPartTimeBVO.VBDEF13,vbdef13);
 } 

/** 
* ��ȡ�Զ�����14
*
* @return �Զ�����14
*/
public String getVbdef14 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF14);
 } 

/** 
* �����Զ�����14
*
* @param vbdef14 �Զ�����14
*/
public void setVbdef14 ( String vbdef14) {
this.setAttributeValue( NonPartTimeBVO.VBDEF14,vbdef14);
 } 

/** 
* ��ȡ�Զ�����15
*
* @return �Զ�����15
*/
public String getVbdef15 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF15);
 } 

/** 
* �����Զ�����15
*
* @param vbdef15 �Զ�����15
*/
public void setVbdef15 ( String vbdef15) {
this.setAttributeValue( NonPartTimeBVO.VBDEF15,vbdef15);
 } 

/** 
* ��ȡ�Զ�����16
*
* @return �Զ�����16
*/
public String getVbdef16 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF16);
 } 

/** 
* �����Զ�����16
*
* @param vbdef16 �Զ�����16
*/
public void setVbdef16 ( String vbdef16) {
this.setAttributeValue( NonPartTimeBVO.VBDEF16,vbdef16);
 } 

/** 
* ��ȡ�Զ�����17
*
* @return �Զ�����17
*/
public String getVbdef17 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF17);
 } 

/** 
* �����Զ�����17
*
* @param vbdef17 �Զ�����17
*/
public void setVbdef17 ( String vbdef17) {
this.setAttributeValue( NonPartTimeBVO.VBDEF17,vbdef17);
 } 

/** 
* ��ȡ�Զ�����18
*
* @return �Զ�����18
*/
public String getVbdef18 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF18);
 } 

/** 
* �����Զ�����18
*
* @param vbdef18 �Զ�����18
*/
public void setVbdef18 ( String vbdef18) {
this.setAttributeValue( NonPartTimeBVO.VBDEF18,vbdef18);
 } 

/** 
* ��ȡ�Զ�����19
*
* @return �Զ�����19
*/
public String getVbdef19 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF19);
 } 

/** 
* �����Զ�����19
*
* @param vbdef19 �Զ�����19
*/
public void setVbdef19 ( String vbdef19) {
this.setAttributeValue( NonPartTimeBVO.VBDEF19,vbdef19);
 } 

/** 
* ��ȡн�ʷ���
*
* @return н�ʷ���
*/
public String getVbdef2 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF2);
 } 

/** 
* ����н�ʷ���
*
* @param vbdef2 н�ʷ���
*/
public void setVbdef2 ( String vbdef2) {
this.setAttributeValue( NonPartTimeBVO.VBDEF2,vbdef2);
 } 

/** 
* ��ȡ�Զ�����20
*
* @return �Զ�����20
*/
public String getVbdef20 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF20);
 } 

/** 
* �����Զ�����20
*
* @param vbdef20 �Զ�����20
*/
public void setVbdef20 ( String vbdef20) {
this.setAttributeValue( NonPartTimeBVO.VBDEF20,vbdef20);
 } 

/** 
* ��ȡн���ڼ�
*
* @return н���ڼ�
*/
public String getVbdef3 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF3);
 } 

/** 
* ����н���ڼ�
*
* @param vbdef3 н���ڼ�
*/
public void setVbdef3 ( String vbdef3) {
this.setAttributeValue( NonPartTimeBVO.VBDEF3,vbdef3);
 } 

/** 
* ��ȡԱ��
*
* @return Ա��
*/
public String getVbdef4 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF4);
 } 

/** 
* ����Ա��
*
* @param vbdef4 Ա��
*/
public void setVbdef4 ( String vbdef4) {
this.setAttributeValue( NonPartTimeBVO.VBDEF4,vbdef4);
 } 

/** 
* ��ȡ�Զ�����5
*
* @return �Զ�����5
*/
public String getVbdef5 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF5);
 } 

/** 
* �����Զ�����5
*
* @param vbdef5 �Զ�����5
*/
public void setVbdef5 ( String vbdef5) {
this.setAttributeValue( NonPartTimeBVO.VBDEF5,vbdef5);
 } 

/** 
* ��ȡ�Զ�����6
*
* @return �Զ�����6
*/
public String getVbdef6 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF6);
 } 

/** 
* �����Զ�����6
*
* @param vbdef6 �Զ�����6
*/
public void setVbdef6 ( String vbdef6) {
this.setAttributeValue( NonPartTimeBVO.VBDEF6,vbdef6);
 } 

/** 
* ��ȡ�Զ�����7
*
* @return �Զ�����7
*/
public String getVbdef7 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF7);
 } 

/** 
* �����Զ�����7
*
* @param vbdef7 �Զ�����7
*/
public void setVbdef7 ( String vbdef7) {
this.setAttributeValue( NonPartTimeBVO.VBDEF7,vbdef7);
 } 

/** 
* ��ȡ�Զ�����8
*
* @return �Զ�����8
*/
public String getVbdef8 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF8);
 } 

/** 
* �����Զ�����8
*
* @param vbdef8 �Զ�����8
*/
public void setVbdef8 ( String vbdef8) {
this.setAttributeValue( NonPartTimeBVO.VBDEF8,vbdef8);
 } 

/** 
* ��ȡ�Զ�����9
*
* @return �Զ�����9
*/
public String getVbdef9 () {
return (String) this.getAttributeValue( NonPartTimeBVO.VBDEF9);
 } 

/** 
* �����Զ�����9
*
* @param vbdef9 �Զ�����9
*/
public void setVbdef9 ( String vbdef9) {
this.setAttributeValue( NonPartTimeBVO.VBDEF9,vbdef9);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.NonPartTimeBVO");
  }
}