package nc.vo.twhr.twhr_declaration;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> ��̎��Ҫ��������� </b>
 * <p>
 * ��̎����۵�������Ϣ
 * </p>
 * ��������:2020/7/29
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class CompanyAdjustBVO extends SuperVO {

	/**
	 * ��˾�a�䱣�M�{��
	 */
	public String pk_companyadj;
	/**
	 * ���F
	 */
	public String pk_group;
	/**
	 * �M��
	 */
	public String pk_org;
	/**
	 * �M���汾
	 */
	public String pk_org_v;
	/**
	 * ��̖
	 */
	public String rowno;
	/**
	 * �{������
	 */
	public UFLiteralDate adjustdate;
	/**
	 * �ˆT
	 */
	public String pk_psndoc;
	/**
	 * Ͷ���{�����~
	 */
	public UFDouble adjustamount;
	/**
	 * �{��ԭ��
	 */
	public String adjustreason;
	/**
	 * �όӆΓ����I
	 */
	public String pk_declaration;
	/**
	 * �r�g��
	 */
	public UFDateTime ts;

	/**
	 * ���� pk_companyadj��Getter����.����������˾�a�䱣�M�{�� ��������:2020/7/29
	 * 
	 * @return java.lang.String
	 */
	public String getPk_companyadj() {
		return this.pk_companyadj;
	}

	/**
	 * ����pk_companyadj��Setter����.����������˾�a�䱣�M�{�� ��������:2020/7/29
	 * 
	 * @param newPk_companyadj
	 *            java.lang.String
	 */
	public void setPk_companyadj(String pk_companyadj) {
		this.pk_companyadj = pk_companyadj;
	}

	/**
	 * ���� pk_group��Getter����.�����������F ��������:2020/7/29
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * ����pk_group��Setter����.�����������F ��������:2020/7/29
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * ���� pk_org��Getter����.���������M�� ��������:2020/7/29
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * ����pk_org��Setter����.���������M�� ��������:2020/7/29
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * ���� pk_org_v��Getter����.���������M���汾 ��������:2020/7/29
	 * 
	 * @return nc.vo.vorg.OrgVersionVO
	 */
	public String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * ����pk_org_v��Setter����.���������M���汾 ��������:2020/7/29
	 * 
	 * @param newPk_org_v
	 *            nc.vo.vorg.OrgVersionVO
	 */
	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * ���� rowno��Getter����.����������̖ ��������:2020/7/29
	 * 
	 * @return java.lang.String
	 */
	public String getRowno() {
		return this.rowno;
	}

	/**
	 * ����rowno��Setter����.����������̖ ��������:2020/7/29
	 * 
	 * @param newRowno
	 *            java.lang.String
	 */
	public void setRowno(String rowno) {
		this.rowno = rowno;
	}

	/**
	 * ���� adjustdate��Getter����.���������{������ ��������:2020/7/29
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public UFLiteralDate getAdjustdate() {
		return this.adjustdate;
	}

	/**
	 * ����adjustdate��Setter����.���������{������ ��������:2020/7/29
	 * 
	 * @param newAdjustdate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setAdjustdate(UFLiteralDate adjustdate) {
		this.adjustdate = adjustdate;
	}

	/**
	 * ���� pk_psndoc��Getter����.���������ˆT ��������:2020/7/29
	 * 
	 * @return nc.vo.hi.psndoc.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * ����pk_psndoc��Setter����.���������ˆT ��������:2020/7/29
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.hi.psndoc.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * ���� adjustamount��Getter����.��������Ͷ���{�����~ ��������:2020/7/29
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getAdjustamount() {
		return this.adjustamount;
	}

	/**
	 * ����adjustamount��Setter����.��������Ͷ���{�����~ ��������:2020/7/29
	 * 
	 * @param newAdjustamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setAdjustamount(UFDouble adjustamount) {
		this.adjustamount = adjustamount;
	}

	/**
	 * ���� adjustreason��Getter����.���������{��ԭ�� ��������:2020/7/29
	 * 
	 * @return java.lang.String
	 */
	public String getAdjustreason() {
		return this.adjustreason;
	}

	/**
	 * ����adjustreason��Setter����.���������{��ԭ�� ��������:2020/7/29
	 * 
	 * @param newAdjustreason
	 *            java.lang.String
	 */
	public void setAdjustreason(String adjustreason) {
		this.adjustreason = adjustreason;
	}

	/**
	 * ���� �����ό����I��Getter����.���������ό����I ��������:2020/7/29
	 * 
	 * @return String
	 */
	public String getPk_declaration() {
		return this.pk_declaration;
	}

	/**
	 * ���������ό����I��Setter����.���������ό����I ��������:2020/7/29
	 * 
	 * @param newPk_declaration
	 *            String
	 */
	public void setPk_declaration(String pk_declaration) {
		this.pk_declaration = pk_declaration;
	}

	/**
	 * ���� ���ɕr�g����Getter����.���������r�g�� ��������:2020/7/29
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * �������ɕr�g����Setter����.���������r�g�� ��������:2020/7/29
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("twhr.CompanyAdjustBVO");
	}
}
