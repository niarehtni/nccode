package nc.vo.twhr.rangetable;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class RangeLineVO extends SuperVO {
/**
*?建??
*/
public UFDateTime creationtime;
/**
*?建人
*/
public String creator;
/**
*?工承?金?
*/
public UFDouble employeeamount;
/**
*雇主承?金?
*/
public UFDouble employeramount;
/**
*?注
*/
public String memo;
/**
*修改??
*/
public UFDateTime modifiedtime;
/**
*修改人
*/
public String modifier;
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
*?距指?行主?
*/
public String pk_rangeline;
/**
*上??据主?
*/
public String pk_rangetable;
/**
*?距??
*/
public String rangeclass;
/**
*?距??
*/
public String rangegrade;
/**
*投保薪?下限
*/
public UFDouble rangelower;
/**
*?率
*/
public UFDouble rangerate;
/**
*修正金?
*/
public UFDouble rangerevise;
/**
*投保薪?上限
*/
public UFDouble rangeupper;
/**
*行?
*/
public String rowno;
/**
*??戳
*/
public UFDateTime ts;
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
* ?取?工承?金?
*
* @return ?工承?金?
*/
public UFDouble getEmployeeamount () {
return this.employeeamount;
 } 

/** 
* ?置?工承?金?
*
* @param employeeamount ?工承?金?
*/
public void setEmployeeamount ( UFDouble employeeamount) {
this.employeeamount=employeeamount;
 } 

/** 
* ?取雇主承?金?
*
* @return 雇主承?金?
*/
public UFDouble getEmployeramount () {
return this.employeramount;
 } 

/** 
* ?置雇主承?金?
*
* @param employeramount 雇主承?金?
*/
public void setEmployeramount ( UFDouble employeramount) {
this.employeramount=employeramount;
 } 

/** 
* ?取?注
*
* @return ?注
*/
public String getMemo () {
return this.memo;
 } 

/** 
* ?置?注
*
* @param memo ?注
*/
public void setMemo ( String memo) {
this.memo=memo;
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
* ?取?距指?行主?
*
* @return ?距指?行主?
*/
public String getPk_rangeline () {
return this.pk_rangeline;
 } 

/** 
* ?置?距指?行主?
*
* @param pk_rangeline ?距指?行主?
*/
public void setPk_rangeline ( String pk_rangeline) {
this.pk_rangeline=pk_rangeline;
 } 

/** 
* ?取上??据主?
*
* @return 上??据主?
*/
public String getPk_rangetable () {
return this.pk_rangetable;
 } 

/** 
* ?置上??据主?
*
* @param pk_rangetable 上??据主?
*/
public void setPk_rangetable ( String pk_rangetable) {
this.pk_rangetable=pk_rangetable;
 } 

/** 
* ?取?距??
*
* @return ?距??
*/
public String getRangeclass () {
return this.rangeclass;
 } 

/** 
* ?置?距??
*
* @param rangeclass ?距??
*/
public void setRangeclass ( String rangeclass) {
this.rangeclass=rangeclass;
 } 

/** 
* ?取?距??
*
* @return ?距??
*/
public String getRangegrade () {
return this.rangegrade;
 } 

/** 
* ?置?距??
*
* @param rangegrade ?距??
*/
public void setRangegrade ( String rangegrade) {
this.rangegrade=rangegrade;
 } 

/** 
* ?取投保薪?下限
*
* @return 投保薪?下限
*/
public UFDouble getRangelower () {
return this.rangelower;
 } 

/** 
* ?置投保薪?下限
*
* @param rangelower 投保薪?下限
*/
public void setRangelower ( UFDouble rangelower) {
this.rangelower=rangelower;
 } 

/** 
* ?取?率
*
* @return ?率
*/
public UFDouble getRangerate () {
return this.rangerate;
 } 

/** 
* ?置?率
*
* @param rangerate ?率
*/
public void setRangerate ( UFDouble rangerate) {
this.rangerate=rangerate;
 } 

/** 
* ?取修正金?
*
* @return 修正金?
*/
public UFDouble getRangerevise () {
return this.rangerevise;
 } 

/** 
* ?置修正金?
*
* @param rangerevise 修正金?
*/
public void setRangerevise ( UFDouble rangerevise) {
this.rangerevise=rangerevise;
 } 

/** 
* ?取投保薪?上限
*
* @return 投保薪?上限
*/
public UFDouble getRangeupper () {
return this.rangeupper;
 } 

/** 
* ?置投保薪?上限
*
* @param rangeupper 投保薪?上限
*/
public void setRangeupper ( UFDouble rangeupper) {
this.rangeupper=rangeupper;
 } 

/** 
* ?取行?
*
* @return 行?
*/
public String getRowno () {
return this.rowno;
 } 

/** 
* ?置行?
*
* @param rowno 行?
*/
public void setRowno ( String rowno) {
this.rowno=rowno;
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


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.rangeline");
  }
}