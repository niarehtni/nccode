package nc.vo.twhr.nhicalc;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> �˴���Ҫ�������๦�� </b>
 * <p>
 *   �˴�����۵�������Ϣ
 * </p>
 *  ��������:2018-9-13
 * @author 
 * @version NCPrj ??
 */
 
public class EpyfamilyVO extends SuperVO {
	
/**
*Ա����������
*/
public String pk_epyfamily;
/**
*�к�
*/
public String rowno;
/**
*Ա����Ϣ
*/
public String pk_psndoc;
/**
*������ϢPK
*/
public String pk_psndoc_sub;
/**
*�������1
*/
public String sub_identity1;
/**
*�������2
*/
public String sub_identity2;
/**
*������
*/
public UFDouble healthamount;
/**
*����
*/
public String name;
/**
*��ν
*/
public String appellation;
/**
*�ϲ㵥������
*/
public String pk_nhicalc;
/**
*ʱ���
*/
public UFDateTime ts;
    
    
/**
* ���� pk_epyfamily��Getter����.��������Ա����������
*  ��������:2018-9-13
* @return java.lang.String
*/
public String getPk_epyfamily() {
return this.pk_epyfamily;
} 

/**
* ����pk_epyfamily��Setter����.��������Ա����������
* ��������:2018-9-13
* @param newPk_epyfamily java.lang.String
*/
public void setPk_epyfamily ( String pk_epyfamily) {
this.pk_epyfamily=pk_epyfamily;
} 
 
/**
* ���� rowno��Getter����.���������к�
*  ��������:2018-9-13
* @return java.lang.String
*/
public String getRowno() {
return this.rowno;
} 

/**
* ����rowno��Setter����.���������к�
* ��������:2018-9-13
* @param newRowno java.lang.String
*/
public void setRowno ( String rowno) {
this.rowno=rowno;
} 
 
/**
* ���� pk_psndoc��Getter����.��������Ա����Ϣ
*  ��������:2018-9-13
* @return nc.vo.bd.psn.PsndocVO
*/
public String getPk_psndoc() {
return this.pk_psndoc;
} 

/**
* ����pk_psndoc��Setter����.��������Ա����Ϣ
* ��������:2018-9-13
* @param newPk_psndoc nc.vo.bd.psn.PsndocVO
*/
public void setPk_psndoc ( String pk_psndoc) {
this.pk_psndoc=pk_psndoc;
} 
 
/**
* ���� pk_psndoc_sub��Getter����.��������������Ϣ
*  ��������:2018-9-13
* @return nc.vo.hi.psndoc.FamilyVO
*/
public String getPk_psndoc_sub() {
return this.pk_psndoc_sub;
} 

/**
* ����pk_psndoc_sub��Setter����.��������������Ϣ
* ��������:2018-9-13
* @param newPk_psndoc_sub nc.vo.hi.psndoc.FamilyVO
*/
public void setPk_psndoc_sub ( String pk_psndoc_sub) {
this.pk_psndoc_sub=pk_psndoc_sub;
} 
 
/**
* ���� sub_identity1��Getter����.���������������1
*  ��������:2018-9-13
* @return nc.vo.twhr.allowance.AllowanceVO
*/
public String getSub_identity1() {
return this.sub_identity1;
} 

/**
* ����sub_identity1��Setter����.���������������1
* ��������:2018-9-13
* @param newSub_identity1 nc.vo.twhr.allowance.AllowanceVO
*/
public void setSub_identity1 ( String sub_identity1) {
this.sub_identity1=sub_identity1;
} 
 
/**
* ���� sub_identity2��Getter����.���������������2
*  ��������:2018-9-13
* @return nc.vo.twhr.allowance.AllowanceVO
*/
public String getSub_identity2() {
return this.sub_identity2;
} 

/**
* ����sub_identity2��Setter����.���������������2
* ��������:2018-9-13
* @param newSub_identity2 nc.vo.twhr.allowance.AllowanceVO
*/
public void setSub_identity2 ( String sub_identity2) {
this.sub_identity2=sub_identity2;
} 
 
/**
* ���� healthamount��Getter����.��������������
*  ��������:2018-9-13
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHealthamount() {
return this.healthamount;
} 

/**
* ����healthamount��Setter����.��������������
* ��������:2018-9-13
* @param newHealthamount nc.vo.pub.lang.UFDouble
*/
public void setHealthamount ( UFDouble healthamount) {
this.healthamount=healthamount;
} 
 
/**
* ���� name��Getter����.������������
*  ��������:2018-9-13
* @return java.lang.String
*/
public String getName() {
return this.name;
} 

/**
* ����name��Setter����.������������
* ��������:2018-9-13
* @param newName java.lang.String
*/
public void setName ( String name) {
this.name=name;
} 
 
/**
* ���� appellation��Getter����.����������ν
*  ��������:2018-9-13
* @return java.lang.String
*/
public String getAppellation() {
return this.appellation;
} 

/**
* ����appellation��Setter����.����������ν
* ��������:2018-9-13
* @param newAppellation java.lang.String
*/
public void setAppellation ( String appellation) {
this.appellation=appellation;
} 
 
/**
* ���� �����ϲ�������Getter����.���������ϲ�����
*  ��������:2018-9-13
* @return String
*/
public String getPk_nhicalc(){
return this.pk_nhicalc;
}
/**
* ���������ϲ�������Setter����.���������ϲ�����
* ��������:2018-9-13
* @param newPk_nhicalc String
*/
public void setPk_nhicalc(String pk_nhicalc){
this.pk_nhicalc=pk_nhicalc;
} 
/**
* ���� ����ʱ�����Getter����.��������ʱ���
*  ��������:2018-9-13
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getTs() {
return this.ts;
}
/**
* ��������ʱ�����Setter����.��������ʱ���
* ��������:2018-9-13
* @param newts nc.vo.pub.lang.UFDateTime
*/
public void setTs(UFDateTime ts){
this.ts=ts;
} 
     
    @Override
    public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.epyfamily");
    }
   }
    