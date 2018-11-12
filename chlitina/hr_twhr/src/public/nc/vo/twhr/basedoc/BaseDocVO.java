package nc.vo.twhr.basedoc;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class BaseDocVO extends SuperVO {
/**
*????
*/
public String code;
/**
*?建??
*/
public UFDateTime creationtime;
/**
*?建人
*/
public String creator;
/**
*自定??1
*/
public String def1;
/**
*自定??10
*/
public String def10;
/**
*自定??11
*/
public String def11;
/**
*自定??12
*/
public String def12;
/**
*自定??13
*/
public String def13;
/**
*自定??14
*/
public String def14;
/**
*自定??15
*/
public String def15;
/**
*自定??16
*/
public String def16;
/**
*自定??17
*/
public String def17;
/**
*自定??18
*/
public String def18;
/**
*自定??19
*/
public String def19;
/**
*自定??2
*/
public String def2;
/**
*自定??20
*/
public String def20;
/**
*自定??3
*/
public String def3;
/**
*自定??4
*/
public String def4;
/**
*自定??5
*/
public String def5;
/**
*自定??6
*/
public String def6;
/**
*自定??7
*/
public String def7;
/**
*自定??8
*/
public String def8;
/**
*自定??9
*/
public String def9;
/**
*????
*/
public Integer doccategory;
/**
*???型
*/
public Integer doctype;
/**
*id
*/
public String id;
/**
*修改??
*/
public UFDateTime modifiedtime;
/**
*修改人
*/
public String modifier;
/**
*??名?
*/
public String name;
/**
*??名?2
*/
public String name2;
/**
*??名?3
*/
public String name3;
/**
*??名?4
*/
public String name4;
/**
*??名?5
*/
public String name5;
/**
*??名?6
*/
public String name6;
/**
*???值
*/
public UFDouble numbervalue;
/**
*集?
*/
public String pk_group;
/**
*??
*/
public String pk_org;
/**
*??版本
*/
public String pk_org_v;
/**
*??戳
*/
public UFDateTime ts;
/**
*薪??放?目
*/
public String waitemvalue;
/**
 * 文本值
 */
public String textvalue;
/**
 * 参照名称
 */
public String reftype;
/**
 * 参照值
 */
public String refvalue;
/**
 * 逻辑值
 * @return
 */
public UFBoolean logicvalue;

public UFBoolean getLogicvalue() {
	return logicvalue;
}

public void setLogicvalue(UFBoolean logicvalue) {
	this.logicvalue = logicvalue;
}

public String getTextvalue() {
	return textvalue;
}

public void setTextvalue(String textvalue) {
	this.textvalue = textvalue;
}

public String getReftype() {
	return reftype;
}

public void setReftype(String reftype) {
	this.reftype = reftype;
}

public String getRefvalue() {
	return refvalue;
}

public void setRefvalue(String refvalue) {
	this.refvalue = refvalue;
}

/** 
* ?取????
*
* @return ????
*/
public String getCode () {
return this.code;
 } 

/** 
* ?置????
*
* @param code ????
*/
public void setCode ( String code) {
this.code=code;
 } 

/** 
* ?取?建??
*
* @return ?建??
*/
public UFDateTime getCreationtime () {
return this.creationtime;
 } 

/** 
* ?置?建??
*
* @param creationtime ?建??
*/
public void setCreationtime ( UFDateTime creationtime) {
this.creationtime=creationtime;
 } 

/** 
* ?取?建人
*
* @return ?建人
*/
public String getCreator () {
return this.creator;
 } 

/** 
* ?置?建人
*
* @param creator ?建人
*/
public void setCreator ( String creator) {
this.creator=creator;
 } 

/** 
* ?取自定??1
*
* @return 自定??1
*/
public String getDef1 () {
return this.def1;
 } 

/** 
* ?置自定??1
*
* @param def1 自定??1
*/
public void setDef1 ( String def1) {
this.def1=def1;
 } 

/** 
* ?取自定??10
*
* @return 自定??10
*/
public String getDef10 () {
return this.def10;
 } 

/** 
* ?置自定??10
*
* @param def10 自定??10
*/
public void setDef10 ( String def10) {
this.def10=def10;
 } 

/** 
* ?取自定??11
*
* @return 自定??11
*/
public String getDef11 () {
return this.def11;
 } 

/** 
* ?置自定??11
*
* @param def11 自定??11
*/
public void setDef11 ( String def11) {
this.def11=def11;
 } 

/** 
* ?取自定??12
*
* @return 自定??12
*/
public String getDef12 () {
return this.def12;
 } 

/** 
* ?置自定??12
*
* @param def12 自定??12
*/
public void setDef12 ( String def12) {
this.def12=def12;
 } 

/** 
* ?取自定??13
*
* @return 自定??13
*/
public String getDef13 () {
return this.def13;
 } 

/** 
* ?置自定??13
*
* @param def13 自定??13
*/
public void setDef13 ( String def13) {
this.def13=def13;
 } 

/** 
* ?取自定??14
*
* @return 自定??14
*/
public String getDef14 () {
return this.def14;
 } 

/** 
* ?置自定??14
*
* @param def14 自定??14
*/
public void setDef14 ( String def14) {
this.def14=def14;
 } 

/** 
* ?取自定??15
*
* @return 自定??15
*/
public String getDef15 () {
return this.def15;
 } 

/** 
* ?置自定??15
*
* @param def15 自定??15
*/
public void setDef15 ( String def15) {
this.def15=def15;
 } 

/** 
* ?取自定??16
*
* @return 自定??16
*/
public String getDef16 () {
return this.def16;
 } 

/** 
* ?置自定??16
*
* @param def16 自定??16
*/
public void setDef16 ( String def16) {
this.def16=def16;
 } 

/** 
* ?取自定??17
*
* @return 自定??17
*/
public String getDef17 () {
return this.def17;
 } 

/** 
* ?置自定??17
*
* @param def17 自定??17
*/
public void setDef17 ( String def17) {
this.def17=def17;
 } 

/** 
* ?取自定??18
*
* @return 自定??18
*/
public String getDef18 () {
return this.def18;
 } 

/** 
* ?置自定??18
*
* @param def18 自定??18
*/
public void setDef18 ( String def18) {
this.def18=def18;
 } 

/** 
* ?取自定??19
*
* @return 自定??19
*/
public String getDef19 () {
return this.def19;
 } 

/** 
* ?置自定??19
*
* @param def19 自定??19
*/
public void setDef19 ( String def19) {
this.def19=def19;
 } 

/** 
* ?取自定??2
*
* @return 自定??2
*/
public String getDef2 () {
return this.def2;
 } 

/** 
* ?置自定??2
*
* @param def2 自定??2
*/
public void setDef2 ( String def2) {
this.def2=def2;
 } 

/** 
* ?取自定??20
*
* @return 自定??20
*/
public String getDef20 () {
return this.def20;
 } 

/** 
* ?置自定??20
*
* @param def20 自定??20
*/
public void setDef20 ( String def20) {
this.def20=def20;
 } 

/** 
* ?取自定??3
*
* @return 自定??3
*/
public String getDef3 () {
return this.def3;
 } 

/** 
* ?置自定??3
*
* @param def3 自定??3
*/
public void setDef3 ( String def3) {
this.def3=def3;
 } 

/** 
* ?取自定??4
*
* @return 自定??4
*/
public String getDef4 () {
return this.def4;
 } 

/** 
* ?置自定??4
*
* @param def4 自定??4
*/
public void setDef4 ( String def4) {
this.def4=def4;
 } 

/** 
* ?取自定??5
*
* @return 自定??5
*/
public String getDef5 () {
return this.def5;
 } 

/** 
* ?置自定??5
*
* @param def5 自定??5
*/
public void setDef5 ( String def5) {
this.def5=def5;
 } 

/** 
* ?取自定??6
*
* @return 自定??6
*/
public String getDef6 () {
return this.def6;
 } 

/** 
* ?置自定??6
*
* @param def6 自定??6
*/
public void setDef6 ( String def6) {
this.def6=def6;
 } 

/** 
* ?取自定??7
*
* @return 自定??7
*/
public String getDef7 () {
return this.def7;
 } 

/** 
* ?置自定??7
*
* @param def7 自定??7
*/
public void setDef7 ( String def7) {
this.def7=def7;
 } 

/** 
* ?取自定??8
*
* @return 自定??8
*/
public String getDef8 () {
return this.def8;
 } 

/** 
* ?置自定??8
*
* @param def8 自定??8
*/
public void setDef8 ( String def8) {
this.def8=def8;
 } 

/** 
* ?取自定??9
*
* @return 自定??9
*/
public String getDef9 () {
return this.def9;
 } 

/** 
* ?置自定??9
*
* @param def9 自定??9
*/
public void setDef9 ( String def9) {
this.def9=def9;
 } 

/** 
* ?取????
*
* @return ????
* @see String
*/
public Integer getDoccategory () {
return this.doccategory;
 } 

/** 
* ?置????
*
* @param doccategory ????
* @see String
*/
public void setDoccategory ( Integer doccategory) {
this.doccategory=doccategory;
 } 

/** 
* ?取???型
*
* @return ???型
* @see String
*/
public Integer getDoctype () {
return this.doctype;
 } 

/** 
* ?置???型
*
* @param doctype ???型
* @see String
*/
public void setDoctype ( Integer doctype) {
this.doctype=doctype;
 } 

/** 
* ?取id
*
* @return id
*/
public String getId () {
return this.id;
 } 

/** 
* ?置id
*
* @param id id
*/
public void setId ( String id) {
this.id=id;
 } 

/** 
* ?取修改??
*
* @return 修改??
*/
public UFDateTime getModifiedtime () {
return this.modifiedtime;
 } 

/** 
* ?置修改??
*
* @param modifiedtime 修改??
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.modifiedtime=modifiedtime;
 } 

/** 
* ?取修改人
*
* @return 修改人
*/
public String getModifier () {
return this.modifier;
 } 

/** 
* ?置修改人
*
* @param modifier 修改人
*/
public void setModifier ( String modifier) {
this.modifier=modifier;
 } 

/** 
* ?取??名?
*
* @return ??名?
*/
public String getName () {
return this.name;
 } 

/** 
* ?置??名?
*
* @param name ??名?
*/
public void setName ( String name) {
this.name=name;
 } 

/** 
* ?取??名?2
*
* @return ??名?2
*/
public String getName2 () {
return this.name2;
 } 

/** 
* ?置??名?2
*
* @param name2 ??名?2
*/
public void setName2 ( String name2) {
this.name2=name2;
 } 

/** 
* ?取??名?3
*
* @return ??名?3
*/
public String getName3 () {
return this.name3;
 } 

/** 
* ?置??名?3
*
* @param name3 ??名?3
*/
public void setName3 ( String name3) {
this.name3=name3;
 } 

/** 
* ?取??名?4
*
* @return ??名?4
*/
public String getName4 () {
return this.name4;
 } 

/** 
* ?置??名?4
*
* @param name4 ??名?4
*/
public void setName4 ( String name4) {
this.name4=name4;
 } 

/** 
* ?取??名?5
*
* @return ??名?5
*/
public String getName5 () {
return this.name5;
 } 

/** 
* ?置??名?5
*
* @param name5 ??名?5
*/
public void setName5 ( String name5) {
this.name5=name5;
 } 

/** 
* ?取??名?6
*
* @return ??名?6
*/
public String getName6 () {
return this.name6;
 } 

/** 
* ?置??名?6
*
* @param name6 ??名?6
*/
public void setName6 ( String name6) {
this.name6=name6;
 } 

/** 
* ?取???值
*
* @return ???值
*/
public UFDouble getNumbervalue () {
return this.numbervalue;
 } 

/** 
* ?置???值
*
* @param numbervalue ???值
*/
public void setNumbervalue ( UFDouble numbervalue) {
this.numbervalue=numbervalue;
 } 

/** 
* ?取集?
*
* @return 集?
*/
public String getPk_group () {
return this.pk_group;
 } 

/** 
* ?置集?
*
* @param pk_group 集?
*/
public void setPk_group ( String pk_group) {
this.pk_group=pk_group;
 } 

/** 
* ?取??
*
* @return ??
*/
public String getPk_org () {
return this.pk_org;
 } 

/** 
* ?置??
*
* @param pk_org ??
*/
public void setPk_org ( String pk_org) {
this.pk_org=pk_org;
 } 

/** 
* ?取??版本
*
* @return ??版本
*/
public String getPk_org_v () {
return this.pk_org_v;
 } 

/** 
* ?置??版本
*
* @param pk_org_v ??版本
*/
public void setPk_org_v ( String pk_org_v) {
this.pk_org_v=pk_org_v;
 } 

/** 
* ?取??戳
*
* @return ??戳
*/
public UFDateTime getTs () {
return this.ts;
 } 

/** 
* ?置??戳
*
* @param ts ??戳
*/
public void setTs ( UFDateTime ts) {
this.ts=ts;
 } 

/** 
* ?取薪??放?目
*
* @return 薪??放?目
*/
public String getWaitemvalue () {
return this.waitemvalue;
 } 

/** 
* ?置薪??放?目
*
* @param waitemvalue 薪??放?目
*/
public void setWaitemvalue ( String waitemvalue) {
this.waitemvalue=waitemvalue;
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.basedoc");
  }
}