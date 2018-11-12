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
 * �˴�����۵�������Ϣ
 * </p>
 * ��������:2018-9-10
 * 
 * @author
 * @version NCPrj ??
 */

public class NhiCalcVO extends SuperVO {

	/**
	 * �ͽ���������Ŀ���I
	 */
	public String pk_nhicalc;
	/**
	 * �к�
	 */
	public String rowno;
	/**
	 * ����
	 */
	public String pk_group;
	/**
	 * ��֯
	 */
	public String pk_org;
	/**
	 * ��֯�汾
	 */
	public String pk_org_v;
	/**
	 * ������
	 */
	public String creator;
	/**
	 * ����ʱ��
	 */
	public UFDateTime creationtime;
	/**
	 * �޸���
	 */
	public String modifier;
	/**
	 * �޸�ʱ��
	 */
	public UFDateTime modifiedtime;
	/**
	 * Ա����Ϣ
	 */
	public String pk_psndoc;
	/**
	 * �����־
	 */
	public UFBoolean iscalculated;
	/**
	 * �Ƿ����
	 */
	public UFBoolean isaudit;
	/**
	 * Ͷ�����
	 */
	public String cyear;
	/**
	 * Ͷ���ڼ�
	 */
	public String cperiod;
	/**
	 * ��ʼ����
	 */
	public UFDate begindate;
	/**
	 * ��ֹ����
	 */
	public UFDate enddate;
	/**
	 * ԭ�ͱ�Ͷ��н��
	 */
	public UFDouble oldlaborsalary;
	/**
	 * �ͱ�Ͷ��н��
	 */
	public UFDouble laborsalary;
	/**
	 * ԭ�ͱ�����
	 */
	public UFDouble oldlaborrange;
	/**
	 * �ͱ�����
	 */
	public UFDouble laborrange;
	/**
	 * ԭ���˼���
	 */
	public UFDouble oldretirerange;
	/**
	 * ���˼���
	 */
	public UFDouble retirerange;
	/**
	 * ԭ����Ͷ��н��
	 */
	public UFDouble oldhealthsalary;
	/**
	 * ����Ͷ��н��
	 */
	public UFDouble healthsalary;
	/**
	 * ԭ��������
	 */
	public UFDouble oldhealthrange;
	/**
	 * ��������
	 */
	public UFDouble healthrange;
	/**
	 * �ͱ�������Ϣʱ���
	 */
	public String labortsmd5;
	/**
	 * ������Ϣʱ���
	 */
	public String healthtsmd5;
	/**
	 * �Ƿ��������
	 */
	public UFBoolean includelastmonth;
	/**
	 * �����ͱ���Ч����
	 */
	public Integer lastmonthlabordays;
	/**
	 * �������˽���ɽ��(����)
	 */
	public UFDouble lastmonthretirehirer;
	/**
	 * �������˽���ɽ��(����)
	 */
	public UFDouble lastmonthretirestuff;
	/**
	 * �����ͱ��е����(����)
	 */
	public UFDouble lastmonthlaborhirer;
	/**
	 * �����ͱ��е����(����)
	 */
	public UFDouble lastmonthllaborstuff;
	/**
	 * ���¾�ҵ���շѳе����(����)
	 */
	public UFDouble lastmonthemphirer;
	/**
	 * ���¾�ҵ���շѳе����(����)
	 */
	public UFDouble lastmonthempstuff;
	/**
	 * ����ְҵ�ֺ����շѳе����(����)
	 */
	public UFDouble lastmonthdishirer;
	/**
	 * ����ְҵ�ֺ����շѳе����(����)
	 */
	public UFDouble lastmonthdisstuff;
	/**
	 * ������ͨ�¹ʱ��շѳе����(����)
	 */
	public UFDouble lastmonthcomhirer;
	/**
	 * ������ͨ�¹ʱ��շѳе����(����)
	 */
	public UFDouble lastmonthcomstuff;
	/**
	 * �ͱ���Ч����
	 */
	public Integer labordays;
	/**
	 * ���˽���ɽ��(����)
	 */
	public UFDouble retirehirer;
	/**
	 * ���˽���ɽ��(����)
	 */
	public UFDouble retirestuff;
	/**
	 * �ͱ��е����(����)
	 */
	public UFDouble laborhirer;
	/**
	 * �ͱ��е����(����)
	 */
	public UFDouble laborstuff;
	/**
	 * ��ҵ���շѳе����(����)
	 */
	public UFDouble emphirer;
	/**
	 * ��ҵ���շѳе����(����)
	 */
	public UFDouble empstuff;
	/**
	 * ְҵ�ֺ����շѳе����(����)
	 */
	public UFDouble dishirer;
	/**
	 * ְҵ�ֺ����շѳе����(����)
	 */
	public UFDouble disstuff;
	/**
	 * ��ͨ�¹ʱ��շѳе����(����)
	 */
	public UFDouble comhirer;
	/**
	 * ��ͨ�¹ʱ��շѳе����(����)
	 */
	public UFDouble comstuff;
	/**
	 * ����˔�(������)
	 */
	public Integer dependentcount;
	/**
	 * �����M�Г����~(����)
	 */
	public UFDouble healthstuff;
	/**
	 * �����M�Г����~(�l��)
	 */
	public UFDouble healthhirer;
	/**
	 * �����a�������M
	 */
	public UFDouble healthgov;
	/**
	 * �����M���U���~(�p��ǰ)
	 */
	public UFDouble healthstuffact;
	/**
	 * �eǷ���Y�|�����~
	 */
	public UFDouble repayfund;
	/**
	 * ���·eǷ���Y�|�����~
	 */
	public UFDouble lastmonthrepayfund;
	/**
	 * ���½����M�Г����~(����)
	 */
	public UFDouble lastmonthhealthstuff;
	/**
	 * ���½����M�Г����~(�l��)
	 */
	public UFDouble lastmonthhealthhirer;
	/**
	 * ���������a�������M
	 */
	public UFDouble lastmonthhealthgov;
	/**
	 * ���½����M���U���~(�p��ǰ)
	 */
	public UFDouble lastmonthhealthstuffact;
	/**
	 * ������Ч�씵
	 */
	public Integer retiredays;
	/**
	 * ��������Ч�씵
	 */
	public Integer lastmonthretiredays;
	/**
	 * ���˹�˾
	 */
	public String pk_corp;
	/**
	 * �������˳Г����~(���~)
	 */
	public UFDouble healthpersamount;
	/**
	 * �����M�Г����~(���)
	 */
	public UFDouble healthcommitamount;
	/**
	 * �Ƶ�ʱ��
	 */
	public UFDate billdate;
	/**
	 * ������
	 */
	public String biiltype;
	/**
	 * ʱ���
	 */
	public UFDateTime ts;

	/**
	 * ���Ě��ϳ̶�
	 */
	public UFDouble disablegrade;

	/**
	 * ��ͨ�¹ʱ��U�M��
	 */
	public UFDouble comrate;

	/**
	 * ��ͨ�¹ʱ��U�M�е����������ˣ�
	 */
	public UFDouble comstuffrate;

	/**
	 * ��ͨ�¹ʱ��U�M�е�������������
	 */
	public UFDouble comhirerrate;

	/**
	 * �ڱ�����]ӛ
	 */
	public UFDouble labortype;

	/**
	 * �I�ĺ����U�M��
	 */
	public UFDouble disrate;

	/**
	 * �I�ĺ����U�M�е����������ˣ�
	 */
	public UFDouble disstuffrate;

	/**
	 * �I�ĺ����U�M�е�������������
	 */
	public UFDouble dishirerrate;

	/**
	 * �͘I���U�M��
	 */
	public UFDouble emprate;

	/**
	 * �͘I���U�M�е����������ˣ�
	 */
	public UFDouble empstuffrate;

	/**
	 * ��ҵ���շѳе�������������
	 */
	public UFDouble emphirerrate;

	/**
	 * ���˽���ɱ��������ˣ�
	 */
	public UFDouble retirestuffrate;

	/**
	 * ���˽���ɱ�����������
	 */
	public UFDouble retirehirerrate;

	/**
	 * �ڱ�Ͷ��н�Y�a����~
	 */
	public UFDouble laborsalaryextend;

	/**
	 * ����Ͷ��н�Y�a����~
	 */
	public UFDouble healthsalaryextend;
	
	private java.lang.Integer dr = 0;
	
	public static final String PK_ORG = "pk_org";

	/**
	 * ���� pk_nhicalc��Getter����.���������ͽ���������Ŀ���I ��������:2018-9-10
	 * 
	 * @return java.lang.String
	 */
	public String getPk_nhicalc() {
		return this.pk_nhicalc;
	}

	/**
	 * ����pk_nhicalc��Setter����.���������ͽ���������Ŀ���I ��������:2018-9-10
	 * 
	 * @param newPk_nhicalc
	 *            java.lang.String
	 */
	public void setPk_nhicalc(String pk_nhicalc) {
		this.pk_nhicalc = pk_nhicalc;
	}

	/**
	 * ���� rowno��Getter����.���������к� ��������:2018-9-10
	 * 
	 * @return java.lang.String
	 */
	public String getRowno() {
		return this.rowno;
	}

	/**
	 * ����rowno��Setter����.���������к� ��������:2018-9-10
	 * 
	 * @param newRowno
	 *            java.lang.String
	 */
	public void setRowno(String rowno) {
		this.rowno = rowno;
	}

	/**
	 * ���� pk_group��Getter����.������������ ��������:2018-9-10
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * ����pk_group��Setter����.������������ ��������:2018-9-10
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * ���� pk_org��Getter����.����������֯ ��������:2018-9-10
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * ����pk_org��Setter����.����������֯ ��������:2018-9-10
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * ���� pk_org_v��Getter����.����������֯�汾 ��������:2018-9-10
	 * 
	 * @return nc.vo.vorg.OrgVersionVO
	 */
	public String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * ����pk_org_v��Setter����.����������֯�汾 ��������:2018-9-10
	 * 
	 * @param newPk_org_v
	 *            nc.vo.vorg.OrgVersionVO
	 */
	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * ���� creator��Getter����.�������������� ��������:2018-9-10
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getCreator() {
		return this.creator;
	}

	/**
	 * ����creator��Setter����.�������������� ��������:2018-9-10
	 * 
	 * @param newCreator
	 *            nc.vo.sm.UserVO
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * ���� creationtime��Getter����.������������ʱ�� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * ����creationtime��Setter����.������������ʱ�� ��������:2018-9-10
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * ���� modifier��Getter����.���������޸��� ��������:2018-9-10
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getModifier() {
		return this.modifier;
	}

	/**
	 * ����modifier��Setter����.���������޸��� ��������:2018-9-10
	 * 
	 * @param newModifier
	 *            nc.vo.sm.UserVO
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * ���� modifiedtime��Getter����.���������޸�ʱ�� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * ����modifiedtime��Setter����.���������޸�ʱ�� ��������:2018-9-10
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * ���� pk_psndoc��Getter����.��������Ա����Ϣ ��������:2018-9-10
	 * 
	 * @return nc.vo.hi.psndoc.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * ����pk_psndoc��Setter����.��������Ա����Ϣ ��������:2018-9-10
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.hi.psndoc.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * ���� iscalculated��Getter����.�������������־ ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getIscalculated() {
		return this.iscalculated;
	}

	/**
	 * ����iscalculated��Setter����.�������������־ ��������:2018-9-10
	 * 
	 * @param newIscalculated
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIscalculated(UFBoolean iscalculated) {
		this.iscalculated = iscalculated;
	}

	/**
	 * ���� isaudit��Getter����.���������Ƿ���� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getIsaudit() {
		return this.isaudit;
	}

	/**
	 * ����isaudit��Setter����.���������Ƿ���� ��������:2018-9-10
	 * 
	 * @param newIsaudit
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIsaudit(UFBoolean isaudit) {
		this.isaudit = isaudit;
	}

	/**
	 * ���� cyear��Getter����.��������Ͷ����� ��������:2018-9-10
	 * 
	 * @return java.lang.String
	 */
	public String getCyear() {
		return this.cyear;
	}

	/**
	 * ����cyear��Setter����.��������Ͷ����� ��������:2018-9-10
	 * 
	 * @param newCyear
	 *            java.lang.String
	 */
	public void setCyear(String cyear) {
		this.cyear = cyear;
	}

	/**
	 * ���� cperiod��Getter����.��������Ͷ���ڼ� ��������:2018-9-10
	 * 
	 * @return java.lang.String
	 */
	public String getCperiod() {
		return this.cperiod;
	}

	/**
	 * ����cperiod��Setter����.��������Ͷ���ڼ� ��������:2018-9-10
	 * 
	 * @param newCperiod
	 *            java.lang.String
	 */
	public void setCperiod(String cperiod) {
		this.cperiod = cperiod;
	}

	/**
	 * ���� begindate��Getter����.����������ʼ���� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getBegindate() {
		return this.begindate;
	}

	/**
	 * ����begindate��Setter����.����������ʼ���� ��������:2018-9-10
	 * 
	 * @param newBegindate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setBegindate(UFDate begindate) {
		this.begindate = begindate;
	}

	/**
	 * ���� enddate��Getter����.����������ֹ���� ��������:2018-9-10
	 * 
	 * @return java.lang.Integer
	 */
	public UFDate getEnddate() {
		return this.enddate;
	}

	/**
	 * ����enddate��Setter����.����������ֹ���� ��������:2018-9-10
	 * 
	 * @param newEnddate
	 *            java.lang.Integer
	 */
	public void setEnddate(UFDate enddate) {
		this.enddate = enddate;
	}

	/**
	 * ���� oldlaborsalary��Getter����.��������ԭ�ͱ�Ͷ��н�� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getOldlaborsalary() {
		return this.oldlaborsalary;
	}

	/**
	 * ����oldlaborsalary��Setter����.��������ԭ�ͱ�Ͷ��н�� ��������:2018-9-10
	 * 
	 * @param newOldlaborsalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOldlaborsalary(UFDouble oldlaborsalary) {
		this.oldlaborsalary = oldlaborsalary;
	}

	/**
	 * ���� laborsalary��Getter����.���������ͱ�Ͷ��н�� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLaborsalary() {
		return this.laborsalary;
	}

	/**
	 * ����laborsalary��Setter����.���������ͱ�Ͷ��н�� ��������:2018-9-10
	 * 
	 * @param newLaborsalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLaborsalary(UFDouble laborsalary) {
		this.laborsalary = laborsalary;
	}

	/**
	 * ���� oldlaborrange��Getter����.��������ԭ�ͱ����� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getOldlaborrange() {
		return this.oldlaborrange;
	}

	/**
	 * ����oldlaborrange��Setter����.��������ԭ�ͱ����� ��������:2018-9-10
	 * 
	 * @param newOldlaborrange
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOldlaborrange(UFDouble oldlaborrange) {
		this.oldlaborrange = oldlaborrange;
	}

	/**
	 * ���� laborrange��Getter����.���������ͱ����� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLaborrange() {
		return this.laborrange;
	}

	/**
	 * ����laborrange��Setter����.���������ͱ����� ��������:2018-9-10
	 * 
	 * @param newLaborrange
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLaborrange(UFDouble laborrange) {
		this.laborrange = laborrange;
	}

	/**
	 * ���� oldretirerange��Getter����.��������ԭ���˼��� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getOldretirerange() {
		return this.oldretirerange;
	}

	/**
	 * ����oldretirerange��Setter����.��������ԭ���˼��� ��������:2018-9-10
	 * 
	 * @param newOldretirerange
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOldretirerange(UFDouble oldretirerange) {
		this.oldretirerange = oldretirerange;
	}

	/**
	 * ���� retirerange��Getter����.�����������˼��� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getRetirerange() {
		return this.retirerange;
	}

	/**
	 * ����retirerange��Setter����.�����������˼��� ��������:2018-9-10
	 * 
	 * @param newRetirerange
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRetirerange(UFDouble retirerange) {
		this.retirerange = retirerange;
	}

	/**
	 * ���� oldhealthsalary��Getter����.��������ԭ����Ͷ��н�� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getOldhealthsalary() {
		return this.oldhealthsalary;
	}

	/**
	 * ����oldhealthsalary��Setter����.��������ԭ����Ͷ��н�� ��������:2018-9-10
	 * 
	 * @param newOldhealthsalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOldhealthsalary(UFDouble oldhealthsalary) {
		this.oldhealthsalary = oldhealthsalary;
	}

	/**
	 * ���� healthsalary��Getter����.������������Ͷ��н�� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHealthsalary() {
		return this.healthsalary;
	}

	/**
	 * ����healthsalary��Setter����.������������Ͷ��н�� ��������:2018-9-10
	 * 
	 * @param newHealthsalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHealthsalary(UFDouble healthsalary) {
		this.healthsalary = healthsalary;
	}

	/**
	 * ���� oldhealthrange��Getter����.��������ԭ�������� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getOldhealthrange() {
		return this.oldhealthrange;
	}

	/**
	 * ����oldhealthrange��Setter����.��������ԭ�������� ��������:2018-9-10
	 * 
	 * @param newOldhealthrange
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOldhealthrange(UFDouble oldhealthrange) {
		this.oldhealthrange = oldhealthrange;
	}

	/**
	 * ���� healthrange��Getter����.���������������� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHealthrange() {
		return this.healthrange;
	}

	/**
	 * ����healthrange��Setter����.���������������� ��������:2018-9-10
	 * 
	 * @param newHealthrange
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHealthrange(UFDouble healthrange) {
		this.healthrange = healthrange;
	}

	/**
	 * ���� labortsmd5��Getter����.���������ͱ�������Ϣʱ��� ��������:2018-9-10
	 * 
	 * @return java.lang.String
	 */
	public String getLabortsmd5() {
		return this.labortsmd5;
	}

	/**
	 * ����labortsmd5��Setter����.���������ͱ�������Ϣʱ��� ��������:2018-9-10
	 * 
	 * @param newLabortsmd5
	 *            java.lang.String
	 */
	public void setLabortsmd5(String labortsmd5) {
		this.labortsmd5 = labortsmd5;
	}

	/**
	 * ���� healthtsmd5��Getter����.��������������Ϣʱ��� ��������:2018-9-10
	 * 
	 * @return java.lang.String
	 */
	public String getHealthtsmd5() {
		return this.healthtsmd5;
	}

	/**
	 * ����healthtsmd5��Setter����.��������������Ϣʱ��� ��������:2018-9-10
	 * 
	 * @param newHealthtsmd5
	 *            java.lang.String
	 */
	public void setHealthtsmd5(String healthtsmd5) {
		this.healthtsmd5 = healthtsmd5;
	}

	/**
	 * ���� includelastmonth��Getter����.���������Ƿ�������� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getIncludelastmonth() {
		return this.includelastmonth;
	}

	/**
	 * ����includelastmonth��Setter����.���������Ƿ�������� ��������:2018-9-10
	 * 
	 * @param newIncludelastmonth
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIncludelastmonth(UFBoolean includelastmonth) {
		this.includelastmonth = includelastmonth;
	}

	/**
	 * ���� lastmonthlabordays��Getter����.�������������ͱ���Ч���� ��������:2018-9-10
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getLastmonthlabordays() {
		return this.lastmonthlabordays;
	}

	/**
	 * ����lastmonthlabordays��Setter����.�������������ͱ���Ч���� ��������:2018-9-10
	 * 
	 * @param newLastmonthlabordays
	 *            java.lang.Integer
	 */
	public void setLastmonthlabordays(Integer lastmonthlabordays) {
		this.lastmonthlabordays = lastmonthlabordays;
	}

	/**
	 * ���� lastmonthretirehirer��Getter����.���������������˽���ɽ��(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthretirehirer() {
		return this.lastmonthretirehirer;
	}

	/**
	 * ����lastmonthretirehirer��Setter����.���������������˽���ɽ��(����) ��������:2018-9-10
	 * 
	 * @param newLastmonthretirehirer
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthretirehirer(UFDouble lastmonthretirehirer) {
		this.lastmonthretirehirer = lastmonthretirehirer;
	}

	/**
	 * ���� lastmonthretirestuff��Getter����.���������������˽���ɽ��(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthretirestuff() {
		return this.lastmonthretirestuff;
	}

	/**
	 * ����lastmonthretirestuff��Setter����.���������������˽���ɽ��(����) ��������:2018-9-10
	 * 
	 * @param newLastmonthretirestuff
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthretirestuff(UFDouble lastmonthretirestuff) {
		this.lastmonthretirestuff = lastmonthretirestuff;
	}

	/**
	 * ���� lastmonthlaborhirer��Getter����.�������������ͱ��е����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthlaborhirer() {
		return this.lastmonthlaborhirer;
	}

	/**
	 * ����lastmonthlaborhirer��Setter����.�������������ͱ��е����(����) ��������:2018-9-10
	 * 
	 * @param newLastmonthlaborhirer
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthlaborhirer(UFDouble lastmonthlaborhirer) {
		this.lastmonthlaborhirer = lastmonthlaborhirer;
	}

	/**
	 * ���� lastmonthllaborstuff��Getter����.�������������ͱ��е����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthllaborstuff() {
		return this.lastmonthllaborstuff;
	}

	/**
	 * ����lastmonthllaborstuff��Setter����.�������������ͱ��е����(����) ��������:2018-9-10
	 * 
	 * @param newLastmonthllaborstuff
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthllaborstuff(UFDouble lastmonthllaborstuff) {
		this.lastmonthllaborstuff = lastmonthllaborstuff;
	}

	/**
	 * ���� lastmonthemphirer��Getter����.�����������¾�ҵ���շѳе����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthemphirer() {
		return this.lastmonthemphirer;
	}

	/**
	 * ����lastmonthemphirer��Setter����.�����������¾�ҵ���շѳе����(����) ��������:2018-9-10
	 * 
	 * @param newLastmonthemphirer
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthemphirer(UFDouble lastmonthemphirer) {
		this.lastmonthemphirer = lastmonthemphirer;
	}

	/**
	 * ���� lastmonthempstuff��Getter����.�����������¾�ҵ���շѳе����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthempstuff() {
		return this.lastmonthempstuff;
	}

	/**
	 * ����lastmonthempstuff��Setter����.�����������¾�ҵ���շѳе����(����) ��������:2018-9-10
	 * 
	 * @param newLastmonthempstuff
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthempstuff(UFDouble lastmonthempstuff) {
		this.lastmonthempstuff = lastmonthempstuff;
	}

	/**
	 * ���� lastmonthdishirer��Getter����.������������ְҵ�ֺ����շѳе����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthdishirer() {
		return this.lastmonthdishirer;
	}

	/**
	 * ����lastmonthdishirer��Setter����.������������ְҵ�ֺ����շѳе����(����) ��������:2018-9-10
	 * 
	 * @param newLastmonthdishirer
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthdishirer(UFDouble lastmonthdishirer) {
		this.lastmonthdishirer = lastmonthdishirer;
	}

	/**
	 * ���� lastmonthdisstuff��Getter����.������������ְҵ�ֺ����շѳе����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthdisstuff() {
		return this.lastmonthdisstuff;
	}

	/**
	 * ����lastmonthdisstuff��Setter����.������������ְҵ�ֺ����շѳе����(����) ��������:2018-9-10
	 * 
	 * @param newLastmonthdisstuff
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthdisstuff(UFDouble lastmonthdisstuff) {
		this.lastmonthdisstuff = lastmonthdisstuff;
	}

	/**
	 * ���� lastmonthcomhirer��Getter����.��������������ͨ�¹ʱ��շѳе����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthcomhirer() {
		return this.lastmonthcomhirer;
	}

	/**
	 * ����lastmonthcomhirer��Setter����.��������������ͨ�¹ʱ��շѳе����(����) ��������:2018-9-10
	 * 
	 * @param newLastmonthcomhirer
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthcomhirer(UFDouble lastmonthcomhirer) {
		this.lastmonthcomhirer = lastmonthcomhirer;
	}

	/**
	 * ���� lastmonthcomstuff��Getter����.��������������ͨ�¹ʱ��շѳе����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthcomstuff() {
		return this.lastmonthcomstuff;
	}

	/**
	 * ����lastmonthcomstuff��Setter����.��������������ͨ�¹ʱ��շѳе����(����) ��������:2018-9-10
	 * 
	 * @param newLastmonthcomstuff
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthcomstuff(UFDouble lastmonthcomstuff) {
		this.lastmonthcomstuff = lastmonthcomstuff;
	}

	/**
	 * ���� labordays��Getter����.���������ͱ���Ч���� ��������:2018-9-10
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getLabordays() {
		return this.labordays;
	}

	/**
	 * ����labordays��Setter����.���������ͱ���Ч���� ��������:2018-9-10
	 * 
	 * @param newLabordays
	 *            java.lang.Integer
	 */
	public void setLabordays(Integer labordays) {
		this.labordays = labordays;
	}

	/**
	 * ���� retirehirer��Getter����.�����������˽���ɽ��(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getRetirehirer() {
		return this.retirehirer;
	}

	/**
	 * ����retirehirer��Setter����.�����������˽���ɽ��(����) ��������:2018-9-10
	 * 
	 * @param newRetirehirer
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRetirehirer(UFDouble retirehirer) {
		this.retirehirer = retirehirer;
	}

	/**
	 * ���� retirestuff��Getter����.�����������˽���ɽ��(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getRetirestuff() {
		return this.retirestuff;
	}

	/**
	 * ����retirestuff��Setter����.�����������˽���ɽ��(����) ��������:2018-9-10
	 * 
	 * @param newRetirestuff
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRetirestuff(UFDouble retirestuff) {
		this.retirestuff = retirestuff;
	}

	/**
	 * ���� laborhirer��Getter����.���������ͱ��е����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLaborhirer() {
		return this.laborhirer;
	}

	/**
	 * ����laborhirer��Setter����.���������ͱ��е����(����) ��������:2018-9-10
	 * 
	 * @param newLaborhirer
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLaborhirer(UFDouble laborhirer) {
		this.laborhirer = laborhirer;
	}

	/**
	 * ���� laborstuff��Getter����.���������ͱ��е����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLaborstuff() {
		return this.laborstuff;
	}

	/**
	 * ����laborstuff��Setter����.���������ͱ��е����(����) ��������:2018-9-10
	 * 
	 * @param newLaborstuff
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLaborstuff(UFDouble laborstuff) {
		this.laborstuff = laborstuff;
	}

	/**
	 * ���� emphirer��Getter����.����������ҵ���շѳе����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getEmphirer() {
		return this.emphirer;
	}

	/**
	 * ����emphirer��Setter����.����������ҵ���շѳе����(����) ��������:2018-9-10
	 * 
	 * @param newEmphirer
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setEmphirer(UFDouble emphirer) {
		this.emphirer = emphirer;
	}

	/**
	 * ���� empstuff��Getter����.����������ҵ���շѳе����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getEmpstuff() {
		return this.empstuff;
	}

	/**
	 * ����empstuff��Setter����.����������ҵ���շѳе����(����) ��������:2018-9-10
	 * 
	 * @param newEmpstuff
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setEmpstuff(UFDouble empstuff) {
		this.empstuff = empstuff;
	}

	/**
	 * ���� dishirer��Getter����.��������ְҵ�ֺ����շѳе����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getDishirer() {
		return this.dishirer;
	}

	/**
	 * ����dishirer��Setter����.��������ְҵ�ֺ����շѳе����(����) ��������:2018-9-10
	 * 
	 * @param newDishirer
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDishirer(UFDouble dishirer) {
		this.dishirer = dishirer;
	}

	/**
	 * ���� disstuff��Getter����.��������ְҵ�ֺ����շѳе����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getDisstuff() {
		return this.disstuff;
	}

	/**
	 * ����disstuff��Setter����.��������ְҵ�ֺ����շѳе����(����) ��������:2018-9-10
	 * 
	 * @param newDisstuff
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDisstuff(UFDouble disstuff) {
		this.disstuff = disstuff;
	}

	/**
	 * ���� comhirer��Getter����.����������ͨ�¹ʱ��շѳе����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getComhirer() {
		return this.comhirer;
	}

	/**
	 * ����comhirer��Setter����.����������ͨ�¹ʱ��շѳе����(����) ��������:2018-9-10
	 * 
	 * @param newComhirer
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setComhirer(UFDouble comhirer) {
		this.comhirer = comhirer;
	}

	/**
	 * ���� comstuff��Getter����.����������ͨ�¹ʱ��շѳе����(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getComstuff() {
		return this.comstuff;
	}

	/**
	 * ����comstuff��Setter����.����������ͨ�¹ʱ��շѳе����(����) ��������:2018-9-10
	 * 
	 * @param newComstuff
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setComstuff(UFDouble comstuff) {
		this.comstuff = comstuff;
	}

	/**
	 * ���� dependentcount��Getter����.������������˔�(������) ��������:2018-9-10
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getDependentcount() {
		return this.dependentcount;
	}

	/**
	 * ����dependentcount��Setter����.������������˔�(������) ��������:2018-9-10
	 * 
	 * @param newDependentcount
	 *            java.lang.Integer
	 */
	public void setDependentcount(Integer dependentcount) {
		this.dependentcount = dependentcount;
	}

	/**
	 * ���� healthstuff��Getter����.�������������M�Г����~(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHealthstuff() {
		return this.healthstuff;
	}

	/**
	 * ����healthstuff��Setter����.�������������M�Г����~(����) ��������:2018-9-10
	 * 
	 * @param newHealthstuff
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHealthstuff(UFDouble healthstuff) {
		this.healthstuff = healthstuff;
	}

	/**
	 * ���� healthhirer��Getter����.�������������M�Г����~(�l��) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHealthhirer() {
		return this.healthhirer;
	}

	/**
	 * ����healthhirer��Setter����.�������������M�Г����~(�l��) ��������:2018-9-10
	 * 
	 * @param newHealthhirer
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHealthhirer(UFDouble healthhirer) {
		this.healthhirer = healthhirer;
	}

	/**
	 * ���� healthgov��Getter����.�������������a�������M ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHealthgov() {
		return this.healthgov;
	}

	/**
	 * ����healthgov��Setter����.�������������a�������M ��������:2018-9-10
	 * 
	 * @param newHealthgov
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHealthgov(UFDouble healthgov) {
		this.healthgov = healthgov;
	}

	/**
	 * ���� healthstuffact��Getter����.�������������M���U���~(�p��ǰ) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHealthstuffact() {
		return this.healthstuffact;
	}

	/**
	 * ����healthstuffact��Setter����.�������������M���U���~(�p��ǰ) ��������:2018-9-10
	 * 
	 * @param newHealthstuffact
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHealthstuffact(UFDouble healthstuffact) {
		this.healthstuffact = healthstuffact;
	}

	/**
	 * ���� repayfund��Getter����.���������eǷ���Y�|�����~ ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getRepayfund() {
		return this.repayfund;
	}

	/**
	 * ����repayfund��Setter����.���������eǷ���Y�|�����~ ��������:2018-9-10
	 * 
	 * @param newRepayfund
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRepayfund(UFDouble repayfund) {
		this.repayfund = repayfund;
	}

	/**
	 * ���� lastmonthrepayfund��Getter����.�����������·eǷ���Y�|�����~ ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthrepayfund() {
		return this.lastmonthrepayfund;
	}

	/**
	 * ����lastmonthrepayfund��Setter����.�����������·eǷ���Y�|�����~ ��������:2018-9-10
	 * 
	 * @param newLastmonthrepayfund
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthrepayfund(UFDouble lastmonthrepayfund) {
		this.lastmonthrepayfund = lastmonthrepayfund;
	}

	/**
	 * ���� lastmonthhealthstuff��Getter����.�����������½����M�Г����~(����) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthhealthstuff() {
		return this.lastmonthhealthstuff;
	}

	/**
	 * ����lastmonthhealthstuff��Setter����.�����������½����M�Г����~(����) ��������:2018-9-10
	 * 
	 * @param newLastmonthhealthstuff
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthhealthstuff(UFDouble lastmonthhealthstuff) {
		this.lastmonthhealthstuff = lastmonthhealthstuff;
	}

	/**
	 * ���� lastmonthhealthhirer��Getter����.�����������½����M�Г����~(�l��) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthhealthhirer() {
		return this.lastmonthhealthhirer;
	}

	/**
	 * ����lastmonthhealthhirer��Setter����.�����������½����M�Г����~(�l��) ��������:2018-9-10
	 * 
	 * @param newLastmonthhealthhirer
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthhealthhirer(UFDouble lastmonthhealthhirer) {
		this.lastmonthhealthhirer = lastmonthhealthhirer;
	}

	/**
	 * ���� lastmonthhealthgov��Getter����.�����������������a�������M ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthhealthgov() {
		return this.lastmonthhealthgov;
	}

	/**
	 * ����lastmonthhealthgov��Setter����.�����������������a�������M ��������:2018-9-10
	 * 
	 * @param newLastmonthhealthgov
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthhealthgov(UFDouble lastmonthhealthgov) {
		this.lastmonthhealthgov = lastmonthhealthgov;
	}

	/**
	 * ���� lastmonthhealthstuffact��Getter����.�����������½����M���U���~(�p��ǰ) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getLastmonthhealthstuffact() {
		return this.lastmonthhealthstuffact;
	}

	/**
	 * ����lastmonthhealthstuffact��Setter����.�����������½����M���U���~(�p��ǰ) ��������:2018-9-10
	 * 
	 * @param newLastmonthhealthstuffact
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setLastmonthhealthstuffact(UFDouble lastmonthhealthstuffact) {
		this.lastmonthhealthstuffact = lastmonthhealthstuffact;
	}

	/**
	 * ���� retiredays��Getter����.��������������Ч�씵 ��������:2018-9-10
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getRetiredays() {
		return this.retiredays;
	}

	/**
	 * ����retiredays��Setter����.��������������Ч�씵 ��������:2018-9-10
	 * 
	 * @param newRetiredays
	 *            java.lang.Integer
	 */
	public void setRetiredays(Integer retiredays) {
		this.retiredays = retiredays;
	}

	/**
	 * ���� lastmonthretiredays��Getter����.����������������Ч�씵 ��������:2018-9-10
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getLastmonthretiredays() {
		return this.lastmonthretiredays;
	}

	/**
	 * ����lastmonthretiredays��Setter����.����������������Ч�씵 ��������:2018-9-10
	 * 
	 * @param newLastmonthretiredays
	 *            java.lang.Integer
	 */
	public void setLastmonthretiredays(Integer lastmonthretiredays) {
		this.lastmonthretiredays = lastmonthretiredays;
	}

	/**
	 * ���� pk_corp��Getter����.�����������˹�˾ ��������:2018-9-10
	 * 
	 * @return nc.vo.org.CorpVO
	 */
	public String getPk_corp() {
		return this.pk_corp;
	}

	/**
	 * ����pk_corp��Setter����.�����������˹�˾ ��������:2018-9-10
	 * 
	 * @param newPk_corp
	 *            nc.vo.org.CorpVO
	 */
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	/**
	 * ���� healthpersamount��Getter����.���������������˳Г����~(���~) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHealthpersamount() {
		return this.healthpersamount;
	}

	/**
	 * ����healthpersamount��Setter����.���������������˳Г����~(���~) ��������:2018-9-10
	 * 
	 * @param newHealthpersamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHealthpersamount(UFDouble healthpersamount) {
		this.healthpersamount = healthpersamount;
	}

	/**
	 * ���� healthcommitamount��Getter����.�������������M�Г����~(���) ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHealthcommitamount() {
		return this.healthcommitamount;
	}

	/**
	 * ����healthcommitamount��Setter����.�������������M�Г����~(���) ��������:2018-9-10
	 * 
	 * @param newHealthcommitamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHealthcommitamount(UFDouble healthcommitamount) {
		this.healthcommitamount = healthcommitamount;
	}

	/**
	 * ���� billdate��Getter����.���������Ƶ�ʱ�� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getBilldate() {
		return this.billdate;
	}

	/**
	 * ����billdate��Setter����.���������Ƶ�ʱ�� ��������:2018-9-10
	 * 
	 * @param newBilldate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setBilldate(UFDate billdate) {
		this.billdate = billdate;
	}

	/**
	 * ���� biiltype��Getter����.�������������� ��������:2018-9-10
	 * 
	 * @return java.lang.String
	 */
	public String getBiiltype() {
		return this.biiltype;
	}

	/**
	 * ����biiltype��Setter����.�������������� ��������:2018-9-10
	 * 
	 * @param newBiiltype
	 *            java.lang.String
	 */
	public void setBiiltype(String biiltype) {
		this.biiltype = biiltype;
	}

	/**
	 * ���� ����ʱ�����Getter����.��������ʱ��� ��������:2018-9-10
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * ��������ʱ�����Setter����.��������ʱ��� ��������:2018-9-10
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public UFDouble getDisablegrade() {
		return disablegrade;
	}

	public void setDisablegrade(UFDouble disablegrade) {
		this.disablegrade = disablegrade;
	}

	public UFDouble getComrate() {
		return comrate;
	}

	public void setComrate(UFDouble comrate) {
		this.comrate = comrate;
	}

	public UFDouble getComstuffrate() {
		return comstuffrate;
	}

	public void setComstuffrate(UFDouble comstuffrate) {
		this.comstuffrate = comstuffrate;
	}

	public UFDouble getComhirerrate() {
		return comhirerrate;
	}

	public void setComhirerrate(UFDouble comhirerrate) {
		this.comhirerrate = comhirerrate;
	}

	public UFDouble getLabortype() {
		return labortype;
	}

	public void setLabortype(UFDouble labortype) {
		this.labortype = labortype;
	}

	public UFDouble getDisrate() {
		return disrate;
	}

	public void setDisrate(UFDouble disrate) {
		this.disrate = disrate;
	}

	public UFDouble getDisstuffrate() {
		return disstuffrate;
	}

	public void setDisstuffrate(UFDouble disstuffrate) {
		this.disstuffrate = disstuffrate;
	}

	public UFDouble getDishirerrate() {
		return dishirerrate;
	}

	public void setDishirerrate(UFDouble dishirerrate) {
		this.dishirerrate = dishirerrate;
	}

	public UFDouble getEmprate() {
		return emprate;
	}

	public void setEmprate(UFDouble emprate) {
		this.emprate = emprate;
	}

	public UFDouble getEmpstuffrate() {
		return empstuffrate;
	}

	public void setEmpstuffrate(UFDouble empstuffrate) {
		this.empstuffrate = empstuffrate;
	}

	public UFDouble getEmphirerrate() {
		return emphirerrate;
	}

	public void setEmphirerrate(UFDouble emphirerrate) {
		this.emphirerrate = emphirerrate;
	}

	public UFDouble getRetirestuffrate() {
		return retirestuffrate;
	}

	public void setRetirestuffrate(UFDouble retirestuffrate) {
		this.retirestuffrate = retirestuffrate;
	}

	public UFDouble getRetirehirerrate() {
		return retirehirerrate;
	}

	public void setRetirehirerrate(UFDouble retirehirerrate) {
		this.retirehirerrate = retirehirerrate;
	}

	public UFDouble getLaborsalaryextend() {
		return laborsalaryextend;
	}

	public void setLaborsalaryextend(UFDouble laborsalaryextend) {
		this.laborsalaryextend = laborsalaryextend;
	}

	public UFDouble getHealthsalaryextend() {
		return healthsalaryextend;
	}

	public void setHealthsalaryextend(UFDouble healthsalaryextend) {
		this.healthsalaryextend = healthsalaryextend;
	}
	
	@Override
	public String getTableName() {
		return "twhr_nhicalc";
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("twhr.nhicalc");
	}

	public static String getDefaultTableName() {
		return "twhr_nhicalc";
	}

	public java.lang.Integer getDr() {
		return dr;
	}

	public void setDr(java.lang.Integer dr) {
		this.dr = dr;
	}
	
	

}
