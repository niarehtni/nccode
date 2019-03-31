package nc.vo.twhr.diffinsurance;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> �˴���Ҫ�������๦�� </b>
 * <p>
 * �˴������۵�������Ϣ
 * </p>
 * ��������:2018-4-8
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class DiffinsuranceVO extends SuperVO {

	/**
	 * �������������
	 */
	public String id;
	/**
	 * ���F
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
	 * ��ʼ����
	 */
	public String begindate;
	/**
	 * ��ֹ����
	 */
	public String enddate;
	/**
	 * н���ڼ�
	 */
	public String pk_period;
	/**
	 * ��Ա����
	 */
	public String pk_psndoc;
	/**
	 * Ա������
	 */
	public String name;
	/**
	 * ����֤��
	 */
	public String idno;
	/**
	 * �Ա�����
	 */
	public int ichecktype;
	/**
	 * ����ԭ��
	 */
	public int idifftype;
	/**
	 * ����Ͷ����λ
	 */
	public UFDouble org_amount;
	/**
	 * Ա������Ͷ����λ
	 */
	public UFDouble org_psnamount;
	/**
	 * ��������Ͷ����λ
	 */
	public UFDouble org_orgamount;
	/**
	 * ������˵�
	 */
	public UFDouble diff_amount;
	/**
	 * Ա���������˵�
	 */
	public UFDouble check_psnamount;
	/**
	 * �����������˵�
	 */
	public UFDouble check_orgamount;
	/**
	 * ������Ա��
	 */
	public UFDouble diff_psnamount;
	/**
	 * ���������
	 */
	public UFDouble diff_orgamount;
	/**
	 * �Ƿ�ͬ��н�ʵ���
	 */
	public String isadjusted;
	/**
	 * ����
	 */
	public String code;
	/**
	 * ����2
	 */
	public String namea;
	/**
	 * ʱ���
	 */
	public UFDateTime ts;
	public Integer dr;

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	/**
	 * ���� id��Getter����.��������������������� ��������:2018-4-8
	 * 
	 * @return java.lang.String
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * ����id��Setter����.��������������������� ��������:2018-4-8
	 * 
	 * @param newId
	 *            java.lang.String
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * ���� pk_group��Getter����.�����������F ��������:2018-4-8
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * ����pk_group��Setter����.�����������F ��������:2018-4-8
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * ���� pk_org��Getter����.����������֯ ��������:2018-4-8
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * ����pk_org��Setter����.����������֯ ��������:2018-4-8
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * ���� pk_org_v��Getter����.����������֯�汾 ��������:2018-4-8
	 * 
	 * @return nc.vo.vorg.OrgVersionVO
	 */
	public String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * ����pk_org_v��Setter����.����������֯�汾 ��������:2018-4-8
	 * 
	 * @param newPk_org_v
	 *            nc.vo.vorg.OrgVersionVO
	 */
	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * ���� creator��Getter����.�������������� ��������:2018-4-8
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getCreator() {
		return this.creator;
	}

	/**
	 * ����creator��Setter����.�������������� ��������:2018-4-8
	 * 
	 * @param newCreator
	 *            nc.vo.sm.UserVO
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * ���� creationtime��Getter����.������������ʱ�� ��������:2018-4-8
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * ����creationtime��Setter����.������������ʱ�� ��������:2018-4-8
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * ���� modifier��Getter����.���������޸��� ��������:2018-4-8
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getModifier() {
		return this.modifier;
	}

	/**
	 * ����modifier��Setter����.���������޸��� ��������:2018-4-8
	 * 
	 * @param newModifier
	 *            nc.vo.sm.UserVO
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * ���� modifiedtime��Getter����.���������޸�ʱ�� ��������:2018-4-8
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * ����modifiedtime��Setter����.���������޸�ʱ�� ��������:2018-4-8
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * ���� begindate��Getter����.����������ʼ���� ��������:2018-4-8
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public String getBegindate() {
		return this.begindate;
	}

	/**
	 * ����begindate��Setter����.����������ʼ���� ��������:2018-4-8
	 * 
	 * @param newBegindate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setBegindate(String begindate) {
		this.begindate = begindate;
	}

	/**
	 * ���� enddate��Getter����.����������ֹ���� ��������:2018-4-8
	 * 
	 * @return java.lang.Integer
	 */
	public String getEnddate() {
		return this.enddate;
	}

	/**
	 * ����enddate��Setter����.����������ֹ���� ��������:2018-4-8
	 * 
	 * @param newEnddate
	 *            java.lang.Integer
	 */
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	/**
	 * ���� pk_period��Getter����.��������н���ڼ� ��������:2018-4-8
	 * 
	 * @return java.lang.String
	 */
	public String getPk_period() {
		return this.pk_period;
	}

	/**
	 * ����pk_period��Setter����.��������н���ڼ� ��������:2018-4-8
	 * 
	 * @param newPk_period
	 *            java.lang.String
	 */
	public void setPk_period(String pk_period) {
		this.pk_period = pk_period;
	}

	/**
	 * ���� pk_psndoc��Getter����.����������Ա���� ��������:2018-4-8
	 * 
	 * @return nc.vo.bd.psn.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * ����pk_psndoc��Setter����.����������Ա���� ��������:2018-4-8
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.bd.psn.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * ���� name��Getter����.��������Ա������ ��������:2018-4-8
	 * 
	 * @return java.lang.String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * ����name��Setter����.��������Ա������ ��������:2018-4-8
	 * 
	 * @param newName
	 *            java.lang.String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * ���� idno��Getter����.������������֤�� ��������:2018-4-8
	 * 
	 * @return java.lang.String
	 */
	public String getIdno() {
		return this.idno;
	}

	/**
	 * ����idno��Setter����.������������֤�� ��������:2018-4-8
	 * 
	 * @param newIdno
	 *            java.lang.String
	 */
	public void setIdno(String idno2) {
		this.idno = idno2;
	}

	/**
	 * ���� ichecktype��Getter����.���������Ա����� ��������:2018-4-8
	 * 
	 * @return nc.vo.twhr.diffinsurancea.ChecktEnume
	 */
	public int getIchecktype() {
		return this.ichecktype;
	}

	/**
	 * ����ichecktype��Setter����.���������Ա����� ��������:2018-4-8
	 * 
	 * @param newIchecktype
	 *            nc.vo.twhr.diffinsurancea.ChecktEnume
	 */
	public void setIchecktype(int labor) {
		this.ichecktype = labor;
	}

	/**
	 * ���� idifftype��Getter����.������������ԭ�� ��������:2018-4-8
	 * 
	 * @return nc.vo.twhr.diffinsurancea.CauseDiffEnume
	 */
	public int getIdifftype() {
		return this.idifftype;
	}

	/**
	 * ����idifftype��Setter����.������������ԭ�� ��������:2018-4-8
	 * 
	 * @param newIdifftype
	 *            nc.vo.twhr.diffinsurancea.CauseDiffEnume
	 */
	public void setIdifftype(int idifftype) {
		this.idifftype = idifftype;
	}

	/**
	 * ���� org_amount��Getter����.������������Ͷ����λ ��������:2018-4-8
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getOrg_amount() {
		return this.org_amount;
	}

	/**
	 * ����org_amount��Setter����.������������Ͷ����λ ��������:2018-4-8
	 * 
	 * @param newOrg_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOrg_amount(UFDouble org_amount) {
		this.org_amount = org_amount;
	}

	/**
	 * ���� org_psnamount��Getter����.��������Ա������Ͷ����λ ��������:2018-4-8
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getOrg_psnamount() {
		return this.org_psnamount;
	}

	/**
	 * ����org_psnamount��Setter����.��������Ա������Ͷ����λ ��������:2018-4-8
	 * 
	 * @param newOrg_psnamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOrg_psnamount(UFDouble org_psnamount) {
		this.org_psnamount = org_psnamount;
	}

	/**
	 * ���� org_orgamount��Getter����.����������������Ͷ����λ ��������:2018-4-8
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getOrg_orgamount() {
		return this.org_orgamount;
	}

	/**
	 * ����org_orgamount��Setter����.����������������Ͷ����λ ��������:2018-4-8
	 * 
	 * @param newOrg_orgamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setOrg_orgamount(UFDouble org_orgamount) {
		this.org_orgamount = org_orgamount;
	}

	/**
	 * ���� diff_amount��Getter����.��������������˵� ��������:2018-4-8
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getDiff_amount() {
		return this.diff_amount;
	}

	/**
	 * ����diff_amount��Setter����.��������������˵� ��������:2018-4-8
	 * 
	 * @param newDiff_amount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDiff_amount(UFDouble diff_amount) {
		this.diff_amount = diff_amount;
	}

	/**
	 * ���� check_psnamount��Getter����.��������Ա���������˵� ��������:2018-4-8
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getCheck_psnamount() {
		return this.check_psnamount;
	}

	/**
	 * ����check_psnamount��Setter����.��������Ա���������˵� ��������:2018-4-8
	 * 
	 * @param newCheck_psnamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setCheck_psnamount(UFDouble check_psnamount) {
		this.check_psnamount = check_psnamount;
	}

	/**
	 * ���� check_orgamount��Getter����.�������������������˵� ��������:2018-4-8
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getCheck_orgamount() {
		return this.check_orgamount;
	}

	/**
	 * ����check_orgamount��Setter����.�������������������˵� ��������:2018-4-8
	 * 
	 * @param newCheck_orgamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setCheck_orgamount(UFDouble check_orgamount) {
		this.check_orgamount = check_orgamount;
	}

	/**
	 * ���� diff_psnamount��Getter����.��������������Ա�� ��������:2018-4-8
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getDiff_psnamount() {
		return this.diff_psnamount;
	}

	/**
	 * ����diff_psnamount��Setter����.��������������Ա�� ��������:2018-4-8
	 * 
	 * @param newDiff_psnamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDiff_psnamount(UFDouble diff_psnamount) {
		this.diff_psnamount = diff_psnamount;
	}

	/**
	 * ���� diff_orgamount��Getter����.����������������� ��������:2018-4-8
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getDiff_orgamount() {
		return this.diff_orgamount;
	}

	/**
	 * ����diff_orgamount��Setter����.����������������� ��������:2018-4-8
	 * 
	 * @param newDiff_orgamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDiff_orgamount(UFDouble diff_orgamount) {
		this.diff_orgamount = diff_orgamount;
	}

	/**
	 * ���� isadjusted��Getter����.���������Ƿ�ͬ��н�ʵ��� ��������:2018-4-8
	 * 
	 * @return java.lang.String
	 */
	public String getIsadjusted() {
		return this.isadjusted;
	}

	/**
	 * ����isadjusted��Setter����.���������Ƿ�ͬ��н�ʵ��� ��������:2018-4-8
	 * 
	 * @param newIsadjusted
	 *            java.lang.String
	 */
	public void setIsadjusted(String isadjusted) {
		this.isadjusted = isadjusted;
	}

	/**
	 * ���� code��Getter����.������������ ��������:2018-4-8
	 * 
	 * @return java.lang.String
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * ����code��Setter����.������������ ��������:2018-4-8
	 * 
	 * @param newCode
	 *            java.lang.String
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * ���� namea��Getter����.������������2 ��������:2018-4-8
	 * 
	 * @return java.lang.String
	 */
	public String getNamea() {
		return this.namea;
	}

	/**
	 * ����namea��Setter����.������������2 ��������:2018-4-8
	 * 
	 * @param newNamea
	 *            java.lang.String
	 */
	public void setNamea(String namea) {
		this.namea = namea;
	}

	/**
	 * ���� ����ʱ�����Getter����.��������ʱ��� ��������:2018-4-8
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * ��������ʱ�����Setter����.��������ʱ��� ��������:2018-4-8
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("twhr.diffinsurance");
	}
}