package nc.vo.twhr.allowance;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class AllowanceVO extends SuperVO {
/**
*?�U?�q
*/
public UFDouble allowanceamount;
/**
*?�U�覡
*/
public Integer allowancetype;
/**
*??
*/
public String code;
/**
*?��??
*/
public UFDateTime creationtime;
/**
*?�ؤH
*/
public String creator;
/**
*�۩w??1
*/
public String def1;
/**
*�۩w??10
*/
public String def10;
/**
*�۩w??11
*/
public String def11;
/**
*�۩w??12
*/
public String def12;
/**
*�۩w??13
*/
public String def13;
/**
*�۩w??14
*/
public String def14;
/**
*�۩w??15
*/
public String def15;
/**
*�۩w??16
*/
public String def16;
/**
*�۩w??17
*/
public String def17;
/**
*�۩w??18
*/
public String def18;
/**
*�۩w??19
*/
public String def19;
/**
*�۩w??2
*/
public String def2;
/**
*�۩w??20
*/
public String def20;
/**
*�۩w??3
*/
public String def3;
/**
*�۩w??4
*/
public String def4;
/**
*�۩w??5
*/
public String def5;
/**
*�۩w??6
*/
public String def6;
/**
*�۩w??7
*/
public String def7;
/**
*�۩w??8
*/
public String def8;
/**
*�۩w??9
*/
public String def9;
/**
*���Ĥ��
*/
public UFDate enddate;
/**
*�ק�??
*/
public UFDateTime modifiedtime;
/**
*�ק�H
*/
public String modifier;
/**
*�W?
*/
public String name;
/**
*�W?2
*/
public String name2;
/**
*�W?3
*/
public String name3;
/**
*�W?4
*/
public String name4;
/**
*�W?5
*/
public String name5;
/**
*�W?6
*/
public String name6;
/**
*?�U??�إD?
*/
public String pk_allowance;
/**
*��?
*/
public String pk_group;
/**
*??
*/
public String pk_org;
/**
*??�����H��
*/
public String pk_org_v;
/**
*�ͮĤ��
*/
public UFDate startdate;
/**
*??�W
*/
public UFDateTime ts;
/** 
* ?��?�U?�q
*
* @return ?�U?�q
*/
public UFDouble getAllowanceamount () {
return this.allowanceamount;
 } 

/** 
* ?�m?�U?�q
*
* @param allowanceamount ?�U?�q
*/
public void setAllowanceamount ( UFDouble allowanceamount) {
this.allowanceamount=allowanceamount;
 } 

/** 
* ?��?�U�覡
*
* @return ?�U�覡
* @see String
*/
public Integer getAllowancetype () {
return this.allowancetype;
 } 

/** 
* ?�m?�U�覡
*
* @param allowancetype ?�U�覡
* @see String
*/
public void setAllowancetype ( Integer allowancetype) {
this.allowancetype=allowancetype;
 } 

/** 
* ?��??
*
* @return ??
*/
public String getCode () {
return this.code;
 } 

/** 
* ?�m??
*
* @param code ??
*/
public void setCode ( String code) {
this.code=code;
 } 

/** 
* ?��?��??
*
* @return ?��??
*/
public UFDateTime getCreationtime () {
return this.creationtime;
 } 

/** 
* ?�m?��??
*
* @param creationtime ?��??
*/
public void setCreationtime ( UFDateTime creationtime) {
this.creationtime=creationtime;
 } 

/** 
* ?��?�ؤH
*
* @return ?�ؤH
*/
public String getCreator () {
return this.creator;
 } 

/** 
* ?�m?�ؤH
*
* @param creator ?�ؤH
*/
public void setCreator ( String creator) {
this.creator=creator;
 } 

/** 
* ?���۩w??1
*
* @return �۩w??1
*/
public String getDef1 () {
return this.def1;
 } 

/** 
* ?�m�۩w??1
*
* @param def1 �۩w??1
*/
public void setDef1 ( String def1) {
this.def1=def1;
 } 

/** 
* ?���۩w??10
*
* @return �۩w??10
*/
public String getDef10 () {
return this.def10;
 } 

/** 
* ?�m�۩w??10
*
* @param def10 �۩w??10
*/
public void setDef10 ( String def10) {
this.def10=def10;
 } 

/** 
* ?���۩w??11
*
* @return �۩w??11
*/
public String getDef11 () {
return this.def11;
 } 

/** 
* ?�m�۩w??11
*
* @param def11 �۩w??11
*/
public void setDef11 ( String def11) {
this.def11=def11;
 } 

/** 
* ?���۩w??12
*
* @return �۩w??12
*/
public String getDef12 () {
return this.def12;
 } 

/** 
* ?�m�۩w??12
*
* @param def12 �۩w??12
*/
public void setDef12 ( String def12) {
this.def12=def12;
 } 

/** 
* ?���۩w??13
*
* @return �۩w??13
*/
public String getDef13 () {
return this.def13;
 } 

/** 
* ?�m�۩w??13
*
* @param def13 �۩w??13
*/
public void setDef13 ( String def13) {
this.def13=def13;
 } 

/** 
* ?���۩w??14
*
* @return �۩w??14
*/
public String getDef14 () {
return this.def14;
 } 

/** 
* ?�m�۩w??14
*
* @param def14 �۩w??14
*/
public void setDef14 ( String def14) {
this.def14=def14;
 } 

/** 
* ?���۩w??15
*
* @return �۩w??15
*/
public String getDef15 () {
return this.def15;
 } 

/** 
* ?�m�۩w??15
*
* @param def15 �۩w??15
*/
public void setDef15 ( String def15) {
this.def15=def15;
 } 

/** 
* ?���۩w??16
*
* @return �۩w??16
*/
public String getDef16 () {
return this.def16;
 } 

/** 
* ?�m�۩w??16
*
* @param def16 �۩w??16
*/
public void setDef16 ( String def16) {
this.def16=def16;
 } 

/** 
* ?���۩w??17
*
* @return �۩w??17
*/
public String getDef17 () {
return this.def17;
 } 

/** 
* ?�m�۩w??17
*
* @param def17 �۩w??17
*/
public void setDef17 ( String def17) {
this.def17=def17;
 } 

/** 
* ?���۩w??18
*
* @return �۩w??18
*/
public String getDef18 () {
return this.def18;
 } 

/** 
* ?�m�۩w??18
*
* @param def18 �۩w??18
*/
public void setDef18 ( String def18) {
this.def18=def18;
 } 

/** 
* ?���۩w??19
*
* @return �۩w??19
*/
public String getDef19 () {
return this.def19;
 } 

/** 
* ?�m�۩w??19
*
* @param def19 �۩w??19
*/
public void setDef19 ( String def19) {
this.def19=def19;
 } 

/** 
* ?���۩w??2
*
* @return �۩w??2
*/
public String getDef2 () {
return this.def2;
 } 

/** 
* ?�m�۩w??2
*
* @param def2 �۩w??2
*/
public void setDef2 ( String def2) {
this.def2=def2;
 } 

/** 
* ?���۩w??20
*
* @return �۩w??20
*/
public String getDef20 () {
return this.def20;
 } 

/** 
* ?�m�۩w??20
*
* @param def20 �۩w??20
*/
public void setDef20 ( String def20) {
this.def20=def20;
 } 

/** 
* ?���۩w??3
*
* @return �۩w??3
*/
public String getDef3 () {
return this.def3;
 } 

/** 
* ?�m�۩w??3
*
* @param def3 �۩w??3
*/
public void setDef3 ( String def3) {
this.def3=def3;
 } 

/** 
* ?���۩w??4
*
* @return �۩w??4
*/
public String getDef4 () {
return this.def4;
 } 

/** 
* ?�m�۩w??4
*
* @param def4 �۩w??4
*/
public void setDef4 ( String def4) {
this.def4=def4;
 } 

/** 
* ?���۩w??5
*
* @return �۩w??5
*/
public String getDef5 () {
return this.def5;
 } 

/** 
* ?�m�۩w??5
*
* @param def5 �۩w??5
*/
public void setDef5 ( String def5) {
this.def5=def5;
 } 

/** 
* ?���۩w??6
*
* @return �۩w??6
*/
public String getDef6 () {
return this.def6;
 } 

/** 
* ?�m�۩w??6
*
* @param def6 �۩w??6
*/
public void setDef6 ( String def6) {
this.def6=def6;
 } 

/** 
* ?���۩w??7
*
* @return �۩w??7
*/
public String getDef7 () {
return this.def7;
 } 

/** 
* ?�m�۩w??7
*
* @param def7 �۩w??7
*/
public void setDef7 ( String def7) {
this.def7=def7;
 } 

/** 
* ?���۩w??8
*
* @return �۩w??8
*/
public String getDef8 () {
return this.def8;
 } 

/** 
* ?�m�۩w??8
*
* @param def8 �۩w??8
*/
public void setDef8 ( String def8) {
this.def8=def8;
 } 

/** 
* ?���۩w??9
*
* @return �۩w??9
*/
public String getDef9 () {
return this.def9;
 } 

/** 
* ?�m�۩w??9
*
* @param def9 �۩w??9
*/
public void setDef9 ( String def9) {
this.def9=def9;
 } 

/** 
* ?�����Ĥ��
*
* @return ���Ĥ��
*/
public UFDate getEnddate () {
return this.enddate;
 } 

/** 
* ?�m���Ĥ��
*
* @param enddate ���Ĥ��
*/
public void setEnddate ( UFDate enddate) {
this.enddate=enddate;
 } 

/** 
* ?���ק�??
*
* @return �ק�??
*/
public UFDateTime getModifiedtime () {
return this.modifiedtime;
 } 

/** 
* ?�m�ק�??
*
* @param modifiedtime �ק�??
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.modifiedtime=modifiedtime;
 } 

/** 
* ?���ק�H
*
* @return �ק�H
*/
public String getModifier () {
return this.modifier;
 } 

/** 
* ?�m�ק�H
*
* @param modifier �ק�H
*/
public void setModifier ( String modifier) {
this.modifier=modifier;
 } 

/** 
* ?���W?
*
* @return �W?
*/
public String getName () {
return this.name;
 } 

/** 
* ?�m�W?
*
* @param name �W?
*/
public void setName ( String name) {
this.name=name;
 } 

/** 
* ?���W?2
*
* @return �W?2
*/
public String getName2 () {
return this.name2;
 } 

/** 
* ?�m�W?2
*
* @param name2 �W?2
*/
public void setName2 ( String name2) {
this.name2=name2;
 } 

/** 
* ?���W?3
*
* @return �W?3
*/
public String getName3 () {
return this.name3;
 } 

/** 
* ?�m�W?3
*
* @param name3 �W?3
*/
public void setName3 ( String name3) {
this.name3=name3;
 } 

/** 
* ?���W?4
*
* @return �W?4
*/
public String getName4 () {
return this.name4;
 } 

/** 
* ?�m�W?4
*
* @param name4 �W?4
*/
public void setName4 ( String name4) {
this.name4=name4;
 } 

/** 
* ?���W?5
*
* @return �W?5
*/
public String getName5 () {
return this.name5;
 } 

/** 
* ?�m�W?5
*
* @param name5 �W?5
*/
public void setName5 ( String name5) {
this.name5=name5;
 } 

/** 
* ?���W?6
*
* @return �W?6
*/
public String getName6 () {
return this.name6;
 } 

/** 
* ?�m�W?6
*
* @param name6 �W?6
*/
public void setName6 ( String name6) {
this.name6=name6;
 } 

/** 
* ?��?�U??�إD?
*
* @return ?�U??�إD?
*/
public String getPk_allowance () {
return this.pk_allowance;
 } 

/** 
* ?�m?�U??�إD?
*
* @param pk_allowance ?�U??�إD?
*/
public void setPk_allowance ( String pk_allowance) {
this.pk_allowance=pk_allowance;
 } 

/** 
* ?����?
*
* @return ��?
*/
public String getPk_group () {
return this.pk_group;
 } 

/** 
* ?�m��?
*
* @param pk_group ��?
*/
public void setPk_group ( String pk_group) {
this.pk_group=pk_group;
 } 

/** 
* ?��??
*
* @return ??
*/
public String getPk_org () {
return this.pk_org;
 } 

/** 
* ?�m??
*
* @param pk_org ??
*/
public void setPk_org ( String pk_org) {
this.pk_org=pk_org;
 } 

/** 
* ?��??�����H��
*
* @return ??�����H��
*/
public String getPk_org_v () {
return this.pk_org_v;
 } 

/** 
* ?�m??�����H��
*
* @param pk_org_v ??�����H��
*/
public void setPk_org_v ( String pk_org_v) {
this.pk_org_v=pk_org_v;
 } 

/** 
* ?���ͮĤ��
*
* @return �ͮĤ��
*/
public UFDate getStartdate () {
return this.startdate;
 } 

/** 
* ?�m�ͮĤ��
*
* @param startdate �ͮĤ��
*/
public void setStartdate ( UFDate startdate) {
this.startdate=startdate;
 } 

/** 
* ?��??�W
*
* @return ??�W
*/
public UFDateTime getTs () {
return this.ts;
 } 

/** 
* ?�m??�W
*
* @param ts ??�W
*/
public void setTs ( UFDateTime ts) {
this.ts=ts;
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.allowance");
  }
}