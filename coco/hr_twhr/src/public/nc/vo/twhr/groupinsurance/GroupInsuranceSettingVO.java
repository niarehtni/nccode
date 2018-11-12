package nc.vo.twhr.groupinsurance;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class GroupInsuranceSettingVO extends SuperVO {
/**
*��ְ�Զ��ӱ�
*/
public UFBoolean bautoreg;
/**
*�Է�
*/
public UFBoolean bselfpay;
/**
*��ʽ
*/
public String cformular;
/**
*��ʽ����
*/
public String cformularstr;
/**
*�ű�����
*/
public String cgrpinsid;
/**
*�ű����
*/
public String cgrpinsrelid;
/**
*����
*/
public String code;
/**
*����ʱ��
*/
public UFDateTime creationtime;
/**
*������
*/
public String creator;
/**
*�Զ�����1
*/
public String def1;
/**
*�Զ�����10
*/
public String def10;
/**
*�Զ�����11
*/
public String def11;
/**
*�Զ�����12
*/
public String def12;
/**
*�Զ�����13
*/
public String def13;
/**
*�Զ�����14
*/
public String def14;
/**
*�Զ�����15
*/
public String def15;
/**
*�Զ�����16
*/
public String def16;
/**
*�Զ�����17
*/
public String def17;
/**
*�Զ�����18
*/
public String def18;
/**
*�Զ�����19
*/
public String def19;
/**
*�Զ�����2
*/
public String def2;
/**
*�Զ�����20
*/
public String def20;
/**
*�Զ�����3
*/
public String def3;
/**
*�Զ�����4
*/
public String def4;
/**
*�Զ�����5
*/
public String def5;
/**
*�Զ�����6
*/
public String def6;
/**
*�Զ�����7
*/
public String def7;
/**
*�Զ�����8
*/
public String def8;
/**
*�Զ�����9
*/
public String def9;
/**
*����
*/
public UFDouble dfixamount;
/**
*����
*/
public UFDouble drate;
/**
*����
*/
public UFDouble dtimes;
/**
*����
*/
public UFDouble dupper;
/**
*���㷽ʽ
*/
public String icalmode;
/**
*ID
*/
public String id;
/**
*����޸�ʱ��
*/
public UFDateTime lastmaketime;
/**
*�Ƶ�ʱ��
*/
public UFDateTime maketime;
/**
*�޸�ʱ��
*/
public UFDateTime modifiedtime;
/**
*�޸���
*/
public String modifier;
/**
*����
*/
public String name;
/**
*����
*/
public String pk_group;
/**
*��֯
*/
public String pk_org;
/**
*��֯�汾
*/
public String pk_org_v;
/**
*�к�
*/
public String rowno;
/**
*ʱ���
*/
public UFDateTime ts;
/**
 * �Ƿ��Ի�����
 */
public UFBoolean ishousehold;
/**
 * �Ƿ�����ű��ӱ�
 */
public UFBoolean familygroupinsurance;
/**
*���չ�˾
*/
public String insurancecompany;
/** 
* ��ȡ���չ�˾
*
* @return ���չ�˾
*/
public String getInsurancecompany () {
return this.insurancecompany;
 } 

/** 
* ���ñ��չ�˾
*
* @param cgrpinsid ���չ�˾
*/
public void setInsurancecompany ( String insurancecompany) {
this.insurancecompany=insurancecompany;
 } 
/** 
* ��ȡ�Ƿ�����ű��ӱ�
*
* @return �Ƿ�����ű��ӱ�
*/
public UFBoolean getFamilygroupinsurance () {
return this.familygroupinsurance;
 } 

/** 
* �����Ƿ�����ű��ӱ�
*
* @param familygroupinsurance �Ƿ�����ű��ӱ�
*/
public void setFamilygroupinsurance ( UFBoolean familygroupinsurance) {
this.familygroupinsurance=familygroupinsurance;
 } 
/** 
* ��ȡ�Ƿ��Ի�����
*
* @return �Ƿ��Ի�����
*/
public UFBoolean getIshousehold () {
return this.ishousehold;
 } 

/** 
* �����Ƿ��Ի�����
*
* @param ishousehold �Ƿ��Ի�����
*/
public void setIshousehold ( UFBoolean ishousehold) {
this.ishousehold=ishousehold;
 } 
/** 
* ��ȡ��ְ�Զ��ӱ�
*
* @return ��ְ�Զ��ӱ�
*/
public UFBoolean getBautoreg () {
return this.bautoreg;
 } 

/** 
* ������ְ�Զ��ӱ�
*
* @param bautoreg ��ְ�Զ��ӱ�
*/
public void setBautoreg ( UFBoolean bautoreg) {
this.bautoreg=bautoreg;
 } 

/** 
* ��ȡ�Է�
*
* @return �Է�
*/
public UFBoolean getBselfpay () {
return this.bselfpay;
 } 

/** 
* �����Է�
*
* @param bselfpay �Է�
*/
public void setBselfpay ( UFBoolean bselfpay) {
this.bselfpay=bselfpay;
 } 

/** 
* ��ȡ��ʽ
*
* @return ��ʽ
*/
public String getCformular () {
return this.cformular;
 } 

/** 
* ���ù�ʽ
*
* @param cformular ��ʽ
*/
public void setCformular ( String cformular) {
this.cformular=cformular;
 } 

/** 
* ��ȡ��ʽ����
*
* @return ��ʽ����
*/
public String getCformularstr () {
return this.cformularstr;
 } 

/** 
* ���ù�ʽ����
*
* @param cformularstr ��ʽ����
*/
public void setCformularstr ( String cformularstr) {
this.cformularstr=cformularstr;
 } 

/** 
* ��ȡ�ű�����
*
* @return �ű�����
*/
public String getCgrpinsid () {
return this.cgrpinsid;
 } 

/** 
* �����ű�����
*
* @param cgrpinsid �ű�����
*/
public void setCgrpinsid ( String cgrpinsid) {
this.cgrpinsid=cgrpinsid;
 } 

/** 
* ��ȡ�ű����
*
* @return �ű����
*/
public String getCgrpinsrelid () {
return this.cgrpinsrelid;
 } 

/** 
* �����ű����
*
* @param cgrpinsrelid �ű����
*/
public void setCgrpinsrelid ( String cgrpinsrelid) {
this.cgrpinsrelid=cgrpinsrelid;
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getCode () {
return this.code;
 } 

/** 
* ���ñ���
*
* @param code ����
*/
public void setCode ( String code) {
this.code=code;
 } 

/** 
* ��ȡ����ʱ��
*
* @return ����ʱ��
*/
public UFDateTime getCreationtime () {
return this.creationtime;
 } 

/** 
* ���ô���ʱ��
*
* @param creationtime ����ʱ��
*/
public void setCreationtime ( UFDateTime creationtime) {
this.creationtime=creationtime;
 } 

/** 
* ��ȡ������
*
* @return ������
*/
public String getCreator () {
return this.creator;
 } 

/** 
* ���ô�����
*
* @param creator ������
*/
public void setCreator ( String creator) {
this.creator=creator;
 } 

/** 
* ��ȡ�Զ�����1
*
* @return �Զ�����1
*/
public String getDef1 () {
return this.def1;
 } 

/** 
* �����Զ�����1
*
* @param def1 �Զ�����1
*/
public void setDef1 ( String def1) {
this.def1=def1;
 } 

/** 
* ��ȡ�Զ�����10
*
* @return �Զ�����10
*/
public String getDef10 () {
return this.def10;
 } 

/** 
* �����Զ�����10
*
* @param def10 �Զ�����10
*/
public void setDef10 ( String def10) {
this.def10=def10;
 } 

/** 
* ��ȡ�Զ�����11
*
* @return �Զ�����11
*/
public String getDef11 () {
return this.def11;
 } 

/** 
* �����Զ�����11
*
* @param def11 �Զ�����11
*/
public void setDef11 ( String def11) {
this.def11=def11;
 } 

/** 
* ��ȡ�Զ�����12
*
* @return �Զ�����12
*/
public String getDef12 () {
return this.def12;
 } 

/** 
* �����Զ�����12
*
* @param def12 �Զ�����12
*/
public void setDef12 ( String def12) {
this.def12=def12;
 } 

/** 
* ��ȡ�Զ�����13
*
* @return �Զ�����13
*/
public String getDef13 () {
return this.def13;
 } 

/** 
* �����Զ�����13
*
* @param def13 �Զ�����13
*/
public void setDef13 ( String def13) {
this.def13=def13;
 } 

/** 
* ��ȡ�Զ�����14
*
* @return �Զ�����14
*/
public String getDef14 () {
return this.def14;
 } 

/** 
* �����Զ�����14
*
* @param def14 �Զ�����14
*/
public void setDef14 ( String def14) {
this.def14=def14;
 } 

/** 
* ��ȡ�Զ�����15
*
* @return �Զ�����15
*/
public String getDef15 () {
return this.def15;
 } 

/** 
* �����Զ�����15
*
* @param def15 �Զ�����15
*/
public void setDef15 ( String def15) {
this.def15=def15;
 } 

/** 
* ��ȡ�Զ�����16
*
* @return �Զ�����16
*/
public String getDef16 () {
return this.def16;
 } 

/** 
* �����Զ�����16
*
* @param def16 �Զ�����16
*/
public void setDef16 ( String def16) {
this.def16=def16;
 } 

/** 
* ��ȡ�Զ�����17
*
* @return �Զ�����17
*/
public String getDef17 () {
return this.def17;
 } 

/** 
* �����Զ�����17
*
* @param def17 �Զ�����17
*/
public void setDef17 ( String def17) {
this.def17=def17;
 } 

/** 
* ��ȡ�Զ�����18
*
* @return �Զ�����18
*/
public String getDef18 () {
return this.def18;
 } 

/** 
* �����Զ�����18
*
* @param def18 �Զ�����18
*/
public void setDef18 ( String def18) {
this.def18=def18;
 } 

/** 
* ��ȡ�Զ�����19
*
* @return �Զ�����19
*/
public String getDef19 () {
return this.def19;
 } 

/** 
* �����Զ�����19
*
* @param def19 �Զ�����19
*/
public void setDef19 ( String def19) {
this.def19=def19;
 } 

/** 
* ��ȡ�Զ�����2
*
* @return �Զ�����2
*/
public String getDef2 () {
return this.def2;
 } 

/** 
* �����Զ�����2
*
* @param def2 �Զ�����2
*/
public void setDef2 ( String def2) {
this.def2=def2;
 } 

/** 
* ��ȡ�Զ�����20
*
* @return �Զ�����20
*/
public String getDef20 () {
return this.def20;
 } 

/** 
* �����Զ�����20
*
* @param def20 �Զ�����20
*/
public void setDef20 ( String def20) {
this.def20=def20;
 } 

/** 
* ��ȡ�Զ�����3
*
* @return �Զ�����3
*/
public String getDef3 () {
return this.def3;
 } 

/** 
* �����Զ�����3
*
* @param def3 �Զ�����3
*/
public void setDef3 ( String def3) {
this.def3=def3;
 } 

/** 
* ��ȡ�Զ�����4
*
* @return �Զ�����4
*/
public String getDef4 () {
return this.def4;
 } 

/** 
* �����Զ�����4
*
* @param def4 �Զ�����4
*/
public void setDef4 ( String def4) {
this.def4=def4;
 } 

/** 
* ��ȡ�Զ�����5
*
* @return �Զ�����5
*/
public String getDef5 () {
return this.def5;
 } 

/** 
* �����Զ�����5
*
* @param def5 �Զ�����5
*/
public void setDef5 ( String def5) {
this.def5=def5;
 } 

/** 
* ��ȡ�Զ�����6
*
* @return �Զ�����6
*/
public String getDef6 () {
return this.def6;
 } 

/** 
* �����Զ�����6
*
* @param def6 �Զ�����6
*/
public void setDef6 ( String def6) {
this.def6=def6;
 } 

/** 
* ��ȡ�Զ�����7
*
* @return �Զ�����7
*/
public String getDef7 () {
return this.def7;
 } 

/** 
* �����Զ�����7
*
* @param def7 �Զ�����7
*/
public void setDef7 ( String def7) {
this.def7=def7;
 } 

/** 
* ��ȡ�Զ�����8
*
* @return �Զ�����8
*/
public String getDef8 () {
return this.def8;
 } 

/** 
* �����Զ�����8
*
* @param def8 �Զ�����8
*/
public void setDef8 ( String def8) {
this.def8=def8;
 } 

/** 
* ��ȡ�Զ�����9
*
* @return �Զ�����9
*/
public String getDef9 () {
return this.def9;
 } 

/** 
* �����Զ�����9
*
* @param def9 �Զ�����9
*/
public void setDef9 ( String def9) {
this.def9=def9;
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public UFDouble getDfixamount () {
return this.dfixamount;
 } 

/** 
* ���ö���
*
* @param dfixamount ����
*/
public void setDfixamount ( UFDouble dfixamount) {
this.dfixamount=dfixamount;
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public UFDouble getDrate () {
return this.drate;
 } 

/** 
* ���÷���
*
* @param drate ����
*/
public void setDrate ( UFDouble drate) {
this.drate=drate;
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public UFDouble getDtimes () {
return this.dtimes;
 } 

/** 
* ���ñ���
*
* @param dtimes ����
*/
public void setDtimes ( UFDouble dtimes) {
this.dtimes=dtimes;
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public UFDouble getDupper () {
return this.dupper;
 } 

/** 
* ��������
*
* @param dupper ����
*/
public void setDupper ( UFDouble dupper) {
this.dupper=dupper;
 } 

/** 
* ��ȡ���㷽ʽ
*
* @return ���㷽ʽ
* @see String
*/
public String getIcalmode () {
return this.icalmode;
 } 

/** 
* ���ü��㷽ʽ
*
* @param icalmode ���㷽ʽ
* @see String
*/
public void setIcalmode ( String icalmode) {
this.icalmode=icalmode;
 } 

/** 
* ��ȡID
*
* @return ID
*/
public String getId () {
return this.id;
 } 

/** 
* ����ID
*
* @param id ID
*/
public void setId ( String id) {
this.id=id;
 } 

/** 
* ��ȡ����޸�ʱ��
*
* @return ����޸�ʱ��
*/
public UFDateTime getLastmaketime () {
return this.lastmaketime;
 } 

/** 
* ��������޸�ʱ��
*
* @param lastmaketime ����޸�ʱ��
*/
public void setLastmaketime ( UFDateTime lastmaketime) {
this.lastmaketime=lastmaketime;
 } 

/** 
* ��ȡ�Ƶ�ʱ��
*
* @return �Ƶ�ʱ��
*/
public UFDateTime getMaketime () {
return this.maketime;
 } 

/** 
* �����Ƶ�ʱ��
*
* @param maketime �Ƶ�ʱ��
*/
public void setMaketime ( UFDateTime maketime) {
this.maketime=maketime;
 } 

/** 
* ��ȡ�޸�ʱ��
*
* @return �޸�ʱ��
*/
public UFDateTime getModifiedtime () {
return this.modifiedtime;
 } 

/** 
* �����޸�ʱ��
*
* @param modifiedtime �޸�ʱ��
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.modifiedtime=modifiedtime;
 } 

/** 
* ��ȡ�޸���
*
* @return �޸���
*/
public String getModifier () {
return this.modifier;
 } 

/** 
* �����޸���
*
* @param modifier �޸���
*/
public void setModifier ( String modifier) {
this.modifier=modifier;
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getName () {
return this.name;
 } 

/** 
* ��������
*
* @param name ����
*/
public void setName ( String name) {
this.name=name;
 } 

/** 
* ��ȡ����
*
* @return ����
*/
public String getPk_group () {
return this.pk_group;
 } 

/** 
* ���ü���
*
* @param pk_group ����
*/
public void setPk_group ( String pk_group) {
this.pk_group=pk_group;
 } 

/** 
* ��ȡ��֯
*
* @return ��֯
*/
public String getPk_org () {
return this.pk_org;
 } 

/** 
* ������֯
*
* @param pk_org ��֯
*/
public void setPk_org ( String pk_org) {
this.pk_org=pk_org;
 } 

/** 
* ��ȡ��֯�汾
*
* @return ��֯�汾
*/
public String getPk_org_v () {
return this.pk_org_v;
 } 

/** 
* ������֯�汾
*
* @param pk_org_v ��֯�汾
*/
public void setPk_org_v ( String pk_org_v) {
this.pk_org_v=pk_org_v;
 } 

/** 
* ��ȡ�к�
*
* @return �к�
*/
public String getRowno () {
return this.rowno;
 } 

/** 
* �����к�
*
* @param rowno �к�
*/
public void setRowno ( String rowno) {
this.rowno=rowno;
 } 

/** 
* ��ȡʱ���
*
* @return ʱ���
*/
public UFDateTime getTs () {
return this.ts;
 } 

/** 
* ����ʱ���
*
* @param ts ʱ���
*/
public void setTs ( UFDateTime ts) {
this.ts=ts;
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.groupinsurancesetting");
  }
}