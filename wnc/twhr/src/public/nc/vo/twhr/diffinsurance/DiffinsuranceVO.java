package nc.vo.twhr.diffinsurance;

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
 
public class DiffinsuranceVO extends SuperVO {
	

//�a�۽������˳Г����~
public static final String HEATH_SUPPLEINS="heath_suppleins";
//��߀�������˳Г����~
public static final String HEATH_RETURNNINS="heath_returnnins";
//�a�۽����M
public static final String HEATH_SYS_SUPPLEINS="heath_sys_suppleins";
//��߀�����M
public static final String HEATH_SYS_RETURNINS="heath_sys_returnins";
//�a�ۄڱ����˳Г����~
public static final String LABOR_SUPPLEINS="labor_suppleins";
//��߀�ڱ����˳Г����~
public static final String LABOR_RETURNNINS="labor_returnnins";
//�a�ۄ��˂��˳Г����~
public static final String RETIRE_SUPPLEINS="retire_suppleins";
//��߀���˂��˳Г����~
public static final String RETIRE_RETURNNINS="retire_returnnins";


/**
*�������������
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
*н���ڼ�
*/
public String pk_period;
/**
*��Ա����
*/
public String pk_psndoc;
/**
*Ͷ��������
*/
public String psnname;
/**
*��ν
*/
public String appellation;
/**
*���֤��
*/
public String idno;
/**
*����Ͷ������
*/
public UFDouble heath_grade;
/**
*�������˳Г����~_ϵ�y_���~
*/
public UFDouble heath_psnins;
/**
*�������˳Г����~_������_���~
*/
public UFDouble heath_psnins_import;
/**
*�a�۽������˳Г����~
*/
public UFDouble heath_suppleins;
/**
*��߀�������˳Г����~
*/
public UFDouble heath_returnnins;
/**
*������˾�Г����~_ϵ�y
*/
public UFDouble heath_orgins;
/**
*������˾�Г����~_������
*/
public UFDouble heath_orgins_import;
/**
*�����M_ϵ�y
*/
public UFDouble heath_sys_ins;
/**
*�a�۽����M
*/
public UFDouble heath_sys_suppleins;
/**
*��߀�����M
*/
public UFDouble heath_sys_returnins;
/**
*�����M_������
*/
public UFDouble heath_sys_ins_import;
/**
*�ڱ�Ͷ������
*/
public UFDouble labor_grade;
/**
*�ڱ����˳Г����~_ϵ�y
*/
public UFDouble labor_psnins;
/**
*�ڱ����˳Г����~_������
*/
public UFDouble labor_psnins_import;
/**
*�a�ۄڱ����˳Г����~
*/
public UFDouble labor_suppleins;
/**
*��߀�ڱ����˳Г����~
*/
public UFDouble labor_returnnins;
/**
*�ڱ���˾�Г����~_ϵ�y
*/
public UFDouble labor_orgins;
/**
*�ڱ���˾�Г����~_������
*/
public UFDouble labor_orgins_import;
/**
*����Ͷ������
*/
public UFDouble retire_grade;
/**
*���˂��˳Г����~_ϵ�y
*/
public UFDouble retire_psnins;
/**
*���˂��˳Г����~_������
*/
public UFDouble retire_psnins_import;
/**
*�a�ۄ��˂��˳Г����~
*/
public UFDouble retire_suppleins;
/**
*��߀���˂��˳Г����~
*/
public UFDouble retire_returnnins;
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
*ʱ���
*/
public UFDateTime ts;
    
private int dr;


public int getDr() {
	return dr;
}

public void setDr(int dr) {
	this.dr = dr;
}

/**
* ���� id��Getter����.���������������������
*  ��������:2019-10-22
* @return java.lang.String
*/
public String getId() {
return this.id;
} 

/**
* ����id��Setter����.���������������������
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
* ���� psnname��Getter����.��������Ͷ��������
*  ��������:2019-10-22
* @return java.lang.String
*/
public String getPsnname() {
return this.psnname;
} 

/**
* ����psnname��Setter����.��������Ͷ��������
* ��������:2019-10-22
* @param newPsnname java.lang.String
*/
public void setPsnname ( String psnname) {
this.psnname=psnname;
} 
 
/**
* ���� appellation��Getter����.����������ν
*  ��������:2019-10-22
* @return java.lang.String
*/
public String getAppellation() {
return this.appellation;
} 

/**
* ����appellation��Setter����.����������ν
* ��������:2019-10-22
* @param newAppellation java.lang.String
*/
public void setAppellation ( String appellation) {
this.appellation=appellation;
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
* ���� heath_grade��Getter����.������������Ͷ������
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_grade() {
return this.heath_grade;
} 

/**
* ����heath_grade��Setter����.������������Ͷ������
* ��������:2019-10-22
* @param newHeath_grade nc.vo.pub.lang.UFDouble
*/
public void setHeath_grade ( UFDouble heath_grade) {
this.heath_grade=heath_grade;
} 
 
/**
* ���� heath_psnins��Getter����.���������������˳Г����~_ϵ�y_���~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_psnins() {
return this.heath_psnins;
} 

/**
* ����heath_psnins��Setter����.���������������˳Г����~_ϵ�y_���~
* ��������:2019-10-22
* @param newHeath_psnins nc.vo.pub.lang.UFDouble
*/
public void setHeath_psnins ( UFDouble heath_psnins) {
this.heath_psnins=heath_psnins;
} 
 
/**
* ���� heath_psnins_import��Getter����.���������������˳Г����~_������_���~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_psnins_import() {
return this.heath_psnins_import;
} 

/**
* ����heath_psnins_import��Setter����.���������������˳Г����~_������_���~
* ��������:2019-10-22
* @param newHeath_psnins_import nc.vo.pub.lang.UFDouble
*/
public void setHeath_psnins_import ( UFDouble heath_psnins_import) {
this.heath_psnins_import=heath_psnins_import;
} 
 
/**
* ���� heath_suppleins��Getter����.���������a�۽������˳Г����~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_suppleins() {
return this.heath_suppleins;
} 

/**
* ����heath_suppleins��Setter����.���������a�۽������˳Г����~
* ��������:2019-10-22
* @param newHeath_suppleins nc.vo.pub.lang.UFDouble
*/
public void setHeath_suppleins ( UFDouble heath_suppleins) {
this.heath_suppleins=heath_suppleins;
} 
 
/**
* ���� heath_returnnins��Getter����.����������߀�������˳Г����~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_returnnins() {
return this.heath_returnnins;
} 

/**
* ����heath_returnnins��Setter����.����������߀�������˳Г����~
* ��������:2019-10-22
* @param newHeath_returnnins nc.vo.pub.lang.UFDouble
*/
public void setHeath_returnnins ( UFDouble heath_returnnins) {
this.heath_returnnins=heath_returnnins;
} 
 
/**
* ���� heath_orgins��Getter����.��������������˾�Г����~_ϵ�y
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_orgins() {
return this.heath_orgins;
} 

/**
* ����heath_orgins��Setter����.��������������˾�Г����~_ϵ�y
* ��������:2019-10-22
* @param newHeath_orgins nc.vo.pub.lang.UFDouble
*/
public void setHeath_orgins ( UFDouble heath_orgins) {
this.heath_orgins=heath_orgins;
} 
 
/**
* ���� heath_orgins_import��Getter����.��������������˾�Г����~_������
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_orgins_import() {
return this.heath_orgins_import;
} 

/**
* ����heath_orgins_import��Setter����.��������������˾�Г����~_������
* ��������:2019-10-22
* @param newHeath_orgins_import nc.vo.pub.lang.UFDouble
*/
public void setHeath_orgins_import ( UFDouble heath_orgins_import) {
this.heath_orgins_import=heath_orgins_import;
} 
 
/**
* ���� heath_sys_ins��Getter����.�������������M_ϵ�y
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_sys_ins() {
return this.heath_sys_ins;
} 

/**
* ����heath_sys_ins��Setter����.�������������M_ϵ�y
* ��������:2019-10-22
* @param newHeath_sys_ins nc.vo.pub.lang.UFDouble
*/
public void setHeath_sys_ins ( UFDouble heath_sys_ins) {
this.heath_sys_ins=heath_sys_ins;
} 
 
/**
* ���� heath_sys_suppleins��Getter����.���������a�۽����M
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_sys_suppleins() {
return this.heath_sys_suppleins;
} 

/**
* ����heath_sys_suppleins��Setter����.���������a�۽����M
* ��������:2019-10-22
* @param newHeath_sys_suppleins nc.vo.pub.lang.UFDouble
*/
public void setHeath_sys_suppleins ( UFDouble heath_sys_suppleins) {
this.heath_sys_suppleins=heath_sys_suppleins;
} 
 
/**
* ���� heath_sys_returnins��Getter����.����������߀�����M
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_sys_returnins() {
return this.heath_sys_returnins;
} 

/**
* ����heath_sys_returnins��Setter����.����������߀�����M
* ��������:2019-10-22
* @param newHeath_sys_returnins nc.vo.pub.lang.UFDouble
*/
public void setHeath_sys_returnins ( UFDouble heath_sys_returnins) {
this.heath_sys_returnins=heath_sys_returnins;
} 
 
/**
* ���� heath_sys_ins_import��Getter����.�������������M_������
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getHeath_sys_ins_import() {
return this.heath_sys_ins_import;
} 

/**
* ����heath_sys_ins_import��Setter����.�������������M_������
* ��������:2019-10-22
* @param newHeath_sys_ins_import nc.vo.pub.lang.UFDouble
*/
public void setHeath_sys_ins_import ( UFDouble heath_sys_ins_import) {
this.heath_sys_ins_import=heath_sys_ins_import;
} 
 
/**
* ���� labor_grade��Getter����.���������ڱ�Ͷ������
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_grade() {
return this.labor_grade;
} 

/**
* ����labor_grade��Setter����.���������ڱ�Ͷ������
* ��������:2019-10-22
* @param newLabor_grade nc.vo.pub.lang.UFDouble
*/
public void setLabor_grade ( UFDouble labor_grade) {
this.labor_grade=labor_grade;
} 
 
/**
* ���� labor_psnins��Getter����.���������ڱ����˳Г����~_ϵ�y
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_psnins() {
return this.labor_psnins;
} 

/**
* ����labor_psnins��Setter����.���������ڱ����˳Г����~_ϵ�y
* ��������:2019-10-22
* @param newLabor_psnins nc.vo.pub.lang.UFDouble
*/
public void setLabor_psnins ( UFDouble labor_psnins) {
this.labor_psnins=labor_psnins;
} 
 
/**
* ���� labor_psnins_import��Getter����.���������ڱ����˳Г����~_������
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_psnins_import() {
return this.labor_psnins_import;
} 

/**
* ����labor_psnins_import��Setter����.���������ڱ����˳Г����~_������
* ��������:2019-10-22
* @param newLabor_psnins_import nc.vo.pub.lang.UFDouble
*/
public void setLabor_psnins_import ( UFDouble labor_psnins_import) {
this.labor_psnins_import=labor_psnins_import;
} 
 
/**
* ���� labor_suppleins��Getter����.���������a�ۄڱ����˳Г����~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_suppleins() {
return this.labor_suppleins;
} 

/**
* ����labor_suppleins��Setter����.���������a�ۄڱ����˳Г����~
* ��������:2019-10-22
* @param newLabor_suppleins nc.vo.pub.lang.UFDouble
*/
public void setLabor_suppleins ( UFDouble labor_suppleins) {
this.labor_suppleins=labor_suppleins;
} 
 
/**
* ���� labor_returnnins��Getter����.����������߀�ڱ����˳Г����~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_returnnins() {
return this.labor_returnnins;
} 

/**
* ����labor_returnnins��Setter����.����������߀�ڱ����˳Г����~
* ��������:2019-10-22
* @param newLabor_returnnins nc.vo.pub.lang.UFDouble
*/
public void setLabor_returnnins ( UFDouble labor_returnnins) {
this.labor_returnnins=labor_returnnins;
} 
 
/**
* ���� labor_orgins��Getter����.���������ڱ���˾�Г����~_ϵ�y
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_orgins() {
return this.labor_orgins;
} 

/**
* ����labor_orgins��Setter����.���������ڱ���˾�Г����~_ϵ�y
* ��������:2019-10-22
* @param newLabor_orgins nc.vo.pub.lang.UFDouble
*/
public void setLabor_orgins ( UFDouble labor_orgins) {
this.labor_orgins=labor_orgins;
} 
 
/**
* ���� labor_orgins_import��Getter����.���������ڱ���˾�Г����~_������
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getLabor_orgins_import() {
return this.labor_orgins_import;
} 

/**
* ����labor_orgins_import��Setter����.���������ڱ���˾�Г����~_������
* ��������:2019-10-22
* @param newLabor_orgins_import nc.vo.pub.lang.UFDouble
*/
public void setLabor_orgins_import ( UFDouble labor_orgins_import) {
this.labor_orgins_import=labor_orgins_import;
} 
 
/**
* ���� retire_grade��Getter����.������������Ͷ������
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_grade() {
return this.retire_grade;
} 

/**
* ����retire_grade��Setter����.������������Ͷ������
* ��������:2019-10-22
* @param newRetire_grade nc.vo.pub.lang.UFDouble
*/
public void setRetire_grade ( UFDouble retire_grade) {
this.retire_grade=retire_grade;
} 
 
/**
* ���� retire_psnins��Getter����.�����������˂��˳Г����~_ϵ�y
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_psnins() {
return this.retire_psnins;
} 

/**
* ����retire_psnins��Setter����.�����������˂��˳Г����~_ϵ�y
* ��������:2019-10-22
* @param newRetire_psnins nc.vo.pub.lang.UFDouble
*/
public void setRetire_psnins ( UFDouble retire_psnins) {
this.retire_psnins=retire_psnins;
} 
 
/**
* ���� retire_psnins_import��Getter����.�����������˂��˳Г����~_������
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_psnins_import() {
return this.retire_psnins_import;
} 

/**
* ����retire_psnins_import��Setter����.�����������˂��˳Г����~_������
* ��������:2019-10-22
* @param newRetire_psnins_import nc.vo.pub.lang.UFDouble
*/
public void setRetire_psnins_import ( UFDouble retire_psnins_import) {
this.retire_psnins_import=retire_psnins_import;
} 
 
/**
* ���� retire_suppleins��Getter����.���������a�ۄ��˂��˳Г����~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_suppleins() {
return this.retire_suppleins;
} 

/**
* ����retire_suppleins��Setter����.���������a�ۄ��˂��˳Г����~
* ��������:2019-10-22
* @param newRetire_suppleins nc.vo.pub.lang.UFDouble
*/
public void setRetire_suppleins ( UFDouble retire_suppleins) {
this.retire_suppleins=retire_suppleins;
} 
 
/**
* ���� retire_returnnins��Getter����.����������߀���˂��˳Г����~
*  ��������:2019-10-22
* @return nc.vo.pub.lang.UFDouble
*/
public UFDouble getRetire_returnnins() {
return this.retire_returnnins;
} 

/**
* ����retire_returnnins��Setter����.����������߀���˂��˳Г����~
* ��������:2019-10-22
* @param newRetire_returnnins nc.vo.pub.lang.UFDouble
*/
public void setRetire_returnnins ( UFDouble retire_returnnins) {
this.retire_returnnins=retire_returnnins;
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
    return VOMetaFactory.getInstance().getVOMeta("twhr.diffinsurance");
    }
   }
    