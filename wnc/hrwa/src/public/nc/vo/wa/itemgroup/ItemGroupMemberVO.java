package nc.vo.wa.itemgroup;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> ��̎��Ҫ��������� </b>
 * <p>
 * ��̎����۵�������Ϣ
 * </p>
 * ��������:2019/1/17
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class ItemGroupMemberVO extends SuperVO {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 8982133699442264995L;
	/**
	 * �����Ŀ���I
	 */
	public String pk_itemgroupmember;
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
	 * ������
	 */
	public String creator;
	/**
	 * �����r�g
	 */
	public UFDateTime creationtime;
	/**
	 * �޸���
	 */
	public String modifier;
	/**
	 * �޸ĕr�g
	 */
	public UFDateTime modifiedtime;
	/**
	 * н�Y�l���Ŀ
	 */
	public String pk_waitem;
	/**
	 * �όӆΓ����I
	 */
	public String pk_itemgroup;
	/**
	 * ��ʾ����
	 */
	public Integer orderno;
	/**
	 * �r�g��
	 */
	public UFDateTime ts;

	/**
	 * ���� pk_itemgroupmember��Getter����.�������������Ŀ���I ��������:2019/1/17
	 * 
	 * @return java.lang.String
	 */
	public String getPk_itemgroupmember() {
		return this.pk_itemgroupmember;
	}

	/**
	 * ����pk_itemgroupmember��Setter����.�������������Ŀ���I ��������:2019/1/17
	 * 
	 * @param newPk_itemgroupmember
	 *            java.lang.String
	 */
	public void setPk_itemgroupmember(String pk_itemgroupmember) {
		this.pk_itemgroupmember = pk_itemgroupmember;
	}

	/**
	 * ���� pk_group��Getter����.�����������F ��������:2019/1/17
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * ����pk_group��Setter����.�����������F ��������:2019/1/17
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * ���� pk_org��Getter����.���������M�� ��������:2019/1/17
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * ����pk_org��Setter����.���������M�� ��������:2019/1/17
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * ���� pk_org_v��Getter����.���������M���汾 ��������:2019/1/17
	 * 
	 * @return nc.vo.vorg.OrgVersionVO
	 */
	public String getPk_org_v() {
		return this.pk_org_v;
	}

	/**
	 * ����pk_org_v��Setter����.���������M���汾 ��������:2019/1/17
	 * 
	 * @param newPk_org_v
	 *            nc.vo.vorg.OrgVersionVO
	 */
	public void setPk_org_v(String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	/**
	 * ���� creator��Getter����.�������������� ��������:2019/1/17
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getCreator() {
		return this.creator;
	}

	/**
	 * ����creator��Setter����.�������������� ��������:2019/1/17
	 * 
	 * @param newCreator
	 *            nc.vo.sm.UserVO
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * ���� creationtime��Getter����.�������������r�g ��������:2019/1/17
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * ����creationtime��Setter����.�������������r�g ��������:2019/1/17
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * ���� modifier��Getter����.���������޸��� ��������:2019/1/17
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getModifier() {
		return this.modifier;
	}

	/**
	 * ����modifier��Setter����.���������޸��� ��������:2019/1/17
	 * 
	 * @param newModifier
	 *            nc.vo.sm.UserVO
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * ���� modifiedtime��Getter����.���������޸ĕr�g ��������:2019/1/17
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * ����modifiedtime��Setter����.���������޸ĕr�g ��������:2019/1/17
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * ���� pk_waitem��Getter����.��������н�Y�l���Ŀ ��������:2019/1/17
	 * 
	 * @return nc.vo.wa.item.WaItemVO
	 */
	public String getPk_waitem() {
		return this.pk_waitem;
	}

	/**
	 * ����pk_waitem��Setter����.��������н�Y�l���Ŀ ��������:2019/1/17
	 * 
	 * @param newPk_waitem
	 *            nc.vo.wa.item.WaItemVO
	 */
	public void setPk_waitem(String pk_waitem) {
		this.pk_waitem = pk_waitem;
	}

	/**
	 * ���� �����ό����I��Getter����.���������ό����I ��������:2019/1/17
	 * 
	 * @return String
	 */
	public String getPk_itemgroup() {
		return this.pk_itemgroup;
	}

	/**
	 * ���������ό����I��Setter����.���������ό����I ��������:2019/1/17
	 * 
	 * @param newPk_itemgroup
	 *            String
	 */
	public void setPk_itemgroup(String pk_itemgroup) {
		this.pk_itemgroup = pk_itemgroup;
	}

	/**
	 * ���� �����ό����I��Getter����.���������@ʾ���� ��������:2020/2/29
	 * 
	 * @return int
	 */
	public Integer getOrderno() {
		return orderno;
	}

	/**
	 * ���������ό����I��Setter����.���������@ʾ���� ��������:2020/2/29
	 * 
	 * @param orderno
	 *            orderno int
	 */
	public void setOrderno(Integer orderno) {
		this.orderno = orderno;
	}

	/**
	 * ���� ���ɕr�g����Getter����.���������r�g�� ��������:2019/1/17
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * �������ɕr�g����Setter����.���������r�g�� ��������:2019/1/17
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("wa.itemgroupmember");
	}
}
