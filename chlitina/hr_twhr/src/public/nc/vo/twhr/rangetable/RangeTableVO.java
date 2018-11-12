package nc.vo.twhr.rangetable;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class RangeTableVO extends SuperVO {
/**
*?��??
*/
public UFDateTime creationtime;
/**
*?�ؤH
*/
public String creator;
/**
*?�u??
*/
public String docno;
/**
*�ظm���
*/
public UFDate doctime;
/**
*�ͮ�
*/
public UFBoolean enabled;
/**
*�I����
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
*��?
*/
public String pk_group;
/**
*??
*/
public String pk_org;
/**
*??����
*/
public String pk_org_v;
/**
*?�Z��D?
*/
public String pk_rangetable;
/**
*�_�l���
*/
public UFDate startdate;
/**
*?�Z��?��
*/
public Integer tabletype;
/**
*??�W
*/
public UFDateTime ts;
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
* ?��?�u??
*
* @return ?�u??
*/
public String getDocno () {
return this.docno;
 } 

/** 
* ?�m?�u??
*
* @param docno ?�u??
*/
public void setDocno ( String docno) {
this.docno=docno;
 } 

/** 
* ?���ظm���
*
* @return �ظm���
*/
public UFDate getDoctime () {
return this.doctime;
 } 

/** 
* ?�m�ظm���
*
* @param doctime �ظm���
*/
public void setDoctime ( UFDate doctime) {
this.doctime=doctime;
 } 

/** 
* ?���ͮ�
*
* @return �ͮ�
*/
public UFBoolean getEnabled () {
return this.enabled;
 } 

/** 
* ?�m�ͮ�
*
* @param enabled �ͮ�
*/
public void setEnabled ( UFBoolean enabled) {
this.enabled=enabled;
 } 

/** 
* ?���I����
*
* @return �I����
*/
public UFDate getEnddate () {
return this.enddate;
 } 

/** 
* ?�m�I����
*
* @param enddate �I����
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
* ?��??����
*
* @return ??����
*/
public String getPk_org_v () {
return this.pk_org_v;
 } 

/** 
* ?�m??����
*
* @param pk_org_v ??����
*/
public void setPk_org_v ( String pk_org_v) {
this.pk_org_v=pk_org_v;
 } 

/** 
* ?��?�Z��D?
*
* @return ?�Z��D?
*/
public String getPk_rangetable () {
return this.pk_rangetable;
 } 

/** 
* ?�m?�Z��D?
*
* @param pk_rangetable ?�Z��D?
*/
public void setPk_rangetable ( String pk_rangetable) {
this.pk_rangetable=pk_rangetable;
 } 

/** 
* ?���_�l���
*
* @return �_�l���
*/
public UFDate getStartdate () {
return this.startdate;
 } 

/** 
* ?�m�_�l���
*
* @param startdate �_�l���
*/
public void setStartdate ( UFDate startdate) {
this.startdate=startdate;
 } 

/** 
* ?��?�Z��?��
*
* @return ?�Z��?��
* @see String
*/
public Integer getTabletype () {
return this.tabletype;
 } 

/** 
* ?�m?�Z��?��
*
* @param tabletype ?�Z��?��
* @see String
*/
public void setTabletype ( Integer tabletype) {
this.tabletype=tabletype;
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
    return VOMetaFactory.getInstance().getVOMeta("twhr.rangetable");
  }
}