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
 *  ��������:2019-10-22
 * @author YONYOU NC
 * @version NCPrj ??
 */
 
public class BaoAccountVO extends SuperVO {
	
/**
*�����������I
*/
public String id;
/**
*���F
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
*������
*/
public String creator;
/**
*����ʱ��
*/
public UFDateTime creationtime;
/**
*�޸���
*/
public String modifier;
/**
*�޸�ʱ��
*/
public UFDateTime modifiedtime;
/**
*��ʼ����
*/
public String begindate;
/**
*��ֹ����
*/
public String enddate;
/**
*н���ڼ�
*/
public String pk_period;
/**
*���֤��
*/
public String idno;
/**
*�ڱ�Ͷ�����~
*/
public UFDouble labor_amount;
/**
*�ڱ��T��ؓ�����~
*/
public UFDouble labor_psnamount;
/**
*�ڱ���˾ؓ�����~
*/
public UFDouble labor_orgamount;
/**
*�������Uн�Y���~
*/
public UFDouble retire_amount;
/**
*���ˆT��������~
*/
public UFDouble retire_psnamount;
/**
*���˹������U���~
*/
public UFDouble retire_orgamount;
/**
*����Ͷ�����~
*/
public UFDouble health_amount;
/**
*�����T�����M
*/
public UFDouble health_psnamount;
/**
*�����������M
*/
public UFDouble health_orgamount;
/**
*��Ա����
*/
public String code;
/**
*��Ա����
*/
public String name;
/**
*����Ͷ��֤����
*/
public String healthid;
/**
*�ͱ�Ͷ��֤����
*/
public String laborid;
/**
*����Ͷ��֤����
*/
public String retiredid;
/**
*��Ա����
*/
public String pk_psndoc;
/**
*��Y��н�Y��
*/
public String diffsettlemonth;
/**
*��Y��н�Y����
*/
public String diffsettlewaclass;
/**
 * 
 */
public int dr;

/**
*ʱ���
*/
public UFDateTime ts;
    

public int getDr() {
	return dr;
}

public void setDr(int dr) {
	this.dr = dr;
}

/**
* ���� id��Getter����.�������������������I
*  ��������:2019-10-22
* @return java.lang.String
*/
public String getId() {
return this.id;
} 

/**
* ����id��Setter����.�������������������I
* ��������:2019-10-22
* @param newId java.lang.String
*/
public void setId ( String id) {
this.id=id;
} 
 
/**
* ���� pk_group��Getter����.�����������F
*  ��������:2019-10-22
* @return nc.vo.org.GroupVO
*/
public String getPk_group() {
return this.pk_group;
} 

/**
* ����pk_group��Setter����.�����������F
* ��������:2019-10-22
* @param newPk_group nc.vo.org.GroupVO
*/
public void setPk_group ( String pk_group) {
this.pk_group=pk_group;
} 
 
/**
* ���� pk_org��Getter����.����������֯
*  ��������:2019-10-22
* @return nc.vo.org.HROrgVO
*/
public String getPk_org() {
return this.pk_org;
} 

/**
* ����pk_org��Setter����.����������֯
* ��������:2019-10-22
* @param newPk_org nc.vo.org.HROrgVO
*/
public void setPk_org ( String pk_org) {
this.pk_org=pk_org;
} 
 
/**
* ���� pk_org_v��Getter����.����������֯�汾
*  ��������:2019-10-22
* @return nc.vo.vorg.OrgVersionVO
*/
public String getPk_org_v() {
return this.pk_org_v;
} 

/**
* ����pk_org_v��Setter����.����������֯�汾
* ��������:2019-10-22
* @param newPk_org_v nc.vo.vorg.OrgVersionVO
*/
public void setPk_org_v ( String pk_org_v) {
this.pk_org_v=pk_org_v;
} 
 
/**
* ���� creator��Getter����.��������������
*  ��������:2019-10-22
* @return nc.vo.sm.UserVO
*/
public String getCreator() {
return this.creator;
} 

/**
* ����creator��Setter����.��������������
* ��������:2019-10-22
* @param newCreator nc.vo.sm.UserVO
*/
public void setCreator ( String creator) {
this.creator=creator;
} 
 
/**
* ���� creationtime��Getter����.������������ʱ��
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getCreationtime() {
return this.creationtime;
} 

/**
* ����creationtime��Setter����.������������ʱ��
* ��������:2019-10-22
* @param newCreationtime nc.vo.pub.lang.UFDateTime
*/
public void setCreationtime ( UFDateTime creationtime) {
this.creationtime=creationtime;
} 
 
/**
* ���� modifier��Getter����.���������޸���
*  ��������:2019-10-22
* @return nc.vo.sm.UserVO
*/
public String getModifier() {
return this.modifier;
} 

/**
* ����modifier��Setter����.���������޸���
* ��������:2019-10-22
* @param newModifier nc.vo.sm.UserVO
*/
public void setModifier ( String modifier) {
this.modifier=modifier;
} 
 
/**
* ���� modifiedtime��Getter����.���������޸�ʱ��
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getModifiedtime() {
return this.modifiedtime;
} 

/**
* ����modifiedtime��Setter����.���������޸�ʱ��
* ��������:2019-10-22
* @param newModifiedtime nc.vo.pub.lang.UFDateTime
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.modifiedtime=modifiedtime;
} 
 
/**
* ���� begindate��Getter����.����������ʼ����
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDate
*/
public String getBegindate() {
return this.begindate;
} 

/**
* ����begindate��Setter����.����������ʼ����
* ��������:2019-10-22
* @param newBegindate nc.vo.pub.lang.UFDate
*/
public void setBegindate ( String begindate) {
this.begindate=begindate;
} 
 
/**
* ���� enddate��Getter����.����������ֹ����
*  ��������:2019-10-22
* @return java.lang.Integer
*/
public String getEnddate() {
return this.enddate;
} 

/**
* ����enddate��Setter����.����������ֹ����
* ��������:2019-10-22
* @param newEnddate java.lang.Integer
*/
public void setEnddate ( String enddate) {
this.enddate=enddate;
} 
 
/**
* ���� pk_period��Getter����.��������н���ڼ�
*  ��������:2019-10-22
* @return java.lang.String
*/
public String getPk_period() {
return this.pk_period;
} 

/**
* ����pk_period��Setter����.��������н���ڼ�
* ��������:2019-10-22
* @param newPk_period java.lang.String
*/
public void setPk_period ( String pk_period) {
this.pk_period=pk_period;
} 
 
/**
* ���� idno��Getter����.�����������֤��
*  ��������:2019-10-22
* @return java.lang.String
*/
public String getIdno() {
return this.idno;
} 

/**
* ����idno��Setter����.�����������֤��
* ��������:2019-10-22
* @param newIdno java.lang.String
*/
public void setIdno ( String idno) {
this.idno=idno;
} 
 
/**
* ���� labor_amount��Getter����.���������ڱ�Ͷ�����~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_amount() {
return this.labor_amount;
} 

/**
* ����labor_amount��Setter����.���������ڱ�Ͷ�����~
* ��������:2019-10-22
* @param newLabor_amount nc.vo.pub.lang.UFDouble
*/
public void setLabor_amount ( UFDouble labor_amount) {
this.labor_amount=labor_amount;
} 
 
/**
* ���� labor_psnamount��Getter����.���������ڱ��T��ؓ�����~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_psnamount() {
return this.labor_psnamount;
} 

/**
* ����labor_psnamount��Setter����.���������ڱ��T��ؓ�����~
* ��������:2019-10-22
* @param newLabor_psnamount nc.vo.pub.lang.UFDouble
*/
public void setLabor_psnamount ( UFDouble labor_psnamount) {
this.labor_psnamount=labor_psnamount;
} 
 
/**
* ���� labor_orgamount��Getter����.���������ڱ���˾ؓ�����~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_orgamount() {
return this.labor_orgamount;
} 

/**
* ����labor_orgamount��Setter����.���������ڱ���˾ؓ�����~
* ��������:2019-10-22
* @param newLabor_orgamount nc.vo.pub.lang.UFDouble
*/
public void setLabor_orgamount ( UFDouble labor_orgamount) {
this.labor_orgamount=labor_orgamount;
} 
 
/**
* ���� retire_amount��Getter����.���������������Uн�Y���~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_amount() {
return this.retire_amount;
} 

/**
* ����retire_amount��Setter����.���������������Uн�Y���~
* ��������:2019-10-22
* @param newRetire_amount nc.vo.pub.lang.UFDouble
*/
public void setRetire_amount ( UFDouble retire_amount) {
this.retire_amount=retire_amount;
} 
 
/**
* ���� retire_psnamount��Getter����.�����������ˆT��������~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_psnamount() {
return this.retire_psnamount;
} 

/**
* ����retire_psnamount��Setter����.�����������ˆT��������~
* ��������:2019-10-22
* @param newRetire_psnamount nc.vo.pub.lang.UFDouble
*/
public void setRetire_psnamount ( UFDouble retire_psnamount) {
this.retire_psnamount=retire_psnamount;
} 
 
/**
* ���� retire_orgamount��Getter����.�����������˹������U���~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_orgamount() {
return this.retire_orgamount;
} 

/**
* ����retire_orgamount��Setter����.�����������˹������U���~
* ��������:2019-10-22
* @param newRetire_orgamount nc.vo.pub.lang.UFDouble
*/
public void setRetire_orgamount ( UFDouble retire_orgamount) {
this.retire_orgamount=retire_orgamount;
} 
 
/**
* ���� health_amount��Getter����.������������Ͷ�����~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHealth_amount() {
return this.health_amount;
} 

/**
* ����health_amount��Setter����.������������Ͷ�����~
* ��������:2019-10-22
* @param newHealth_amount nc.vo.pub.lang.UFDouble
*/
public void setHealth_amount ( UFDouble health_amount) {
this.health_amount=health_amount;
} 
 
/**
* ���� health_psnamount��Getter����.�������������T�����M
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHealth_psnamount() {
return this.health_psnamount;
} 

/**
* ����health_psnamount��Setter����.�������������T�����M
* ��������:2019-10-22
* @param newHealth_psnamount nc.vo.pub.lang.UFDouble
*/
public void setHealth_psnamount ( UFDouble health_psnamount) {
this.health_psnamount=health_psnamount;
} 
 
/**
* ���� health_orgamount��Getter����.�������������������M
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHealth_orgamount() {
return this.health_orgamount;
} 

/**
* ����health_orgamount��Setter����.�������������������M
* ��������:2019-10-22
* @param newHealth_orgamount nc.vo.pub.lang.UFDouble
*/
public void setHealth_orgamount ( UFDouble health_orgamount) {
this.health_orgamount=health_orgamount;
} 
 
/**
* ���� code��Getter����.����������Ա����
*  ��������:2019-10-22
* @return java.lang.String
*/
public String getCode() {
return this.code;
} 

/**
* ����code��Setter����.����������Ա����
* ��������:2019-10-22
* @param newCode java.lang.String
*/
public void setCode ( String code) {
this.code=code;
} 
 
/**
* ���� name��Getter����.����������Ա����
*  ��������:2019-10-22
* @return java.lang.String
*/
public String getName() {
return this.name;
} 

/**
* ����name��Setter����.����������Ա����
* ��������:2019-10-22
* @param newName java.lang.String
*/
public void setName ( String name) {
this.name=name;
} 
 
/**
* ���� healthid��Getter����.������������Ͷ��֤����
*  ��������:2019-10-22
* @return java.lang.String
*/
public String getHealthid() {
return this.healthid;
} 

/**
* ����healthid��Setter����.������������Ͷ��֤����
* ��������:2019-10-22
* @param newHealthid java.lang.String
*/
public void setHealthid ( String healthid) {
this.healthid=healthid;
} 
 
/**
* ���� laborid��Getter����.���������ͱ�Ͷ��֤����
*  ��������:2019-10-22
* @return java.lang.String
*/
public String getLaborid() {
return this.laborid;
} 

/**
* ����laborid��Setter����.���������ͱ�Ͷ��֤����
* ��������:2019-10-22
* @param newLaborid java.lang.String
*/
public void setLaborid ( String laborid) {
this.laborid=laborid;
} 
 
/**
* ���� retiredid��Getter����.������������Ͷ��֤����
*  ��������:2019-10-22
* @return java.lang.String
*/
public String getRetiredid() {
return this.retiredid;
} 

/**
* ����retiredid��Setter����.������������Ͷ��֤����
* ��������:2019-10-22
* @param newRetiredid java.lang.String
*/
public void setRetiredid ( String retiredid) {
this.retiredid=retiredid;
} 
 
/**
* ���� pk_psndoc��Getter����.����������Ա����
*  ��������:2019-10-22
* @return nc.vo.bd.psn.PsndocVO
*/
public String getPk_psndoc() {
return this.pk_psndoc;
} 

/**
* ����pk_psndoc��Setter����.����������Ա����
* ��������:2019-10-22
* @param newPk_psndoc nc.vo.bd.psn.PsndocVO
*/
public void setPk_psndoc ( String pk_psndoc) {
this.pk_psndoc=pk_psndoc;
} 
 
/**
* ���� diffsettlemonth��Getter����.����������Y��н�Y��
*  ��������:2019-10-22
* @return java.lang.String
*/
public String getDiffsettlemonth() {
return this.diffsettlemonth;
} 

/**
* ����diffsettlemonth��Setter����.����������Y��н�Y��
* ��������:2019-10-22
* @param newDiffsettlemonth java.lang.String
*/
public void setDiffsettlemonth ( String diffsettlemonth) {
this.diffsettlemonth=diffsettlemonth;
} 
 
/**
* ���� diffsettlewaclass��Getter����.����������Y��н�Y����
*  ��������:2019-10-22
* @return nc.vo.wa.category.WaClassVO
*/
public String getDiffsettlewaclass() {
return this.diffsettlewaclass;
} 

/**
* ����diffsettlewaclass��Setter����.����������Y��н�Y����
* ��������:2019-10-22
* @param newDiffsettlewaclass nc.vo.wa.category.WaClassVO
*/
public void setDiffsettlewaclass ( String diffsettlewaclass) {
this.diffsettlewaclass=diffsettlewaclass;
} 
 
/**
* ���� ����ʱ�����Getter����.��������ʱ���
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getTs() {
return this.ts;
}
/**
* ��������ʱ�����Setter����.��������ʱ���
* ��������:2019-10-22
* @param newts nc.vo.pub.lang.UFDateTime
*/
public void setTs(UFDateTime ts){
this.ts=ts;
} 
     
    @Override
    public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.baoaccount");
    }
   }
    