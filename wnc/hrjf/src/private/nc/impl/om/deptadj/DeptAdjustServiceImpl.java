package nc.impl.om.deptadj;

import java.util.ArrayList;
import java.util.List;

import nc.ui.pub.beans.UIMultiLangCombox;
import nc.vo.logging.Debug;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.DeptChangeType;
import nc.vo.om.hrdept.DeptHistoryVO;
import nc.vo.om.hrdept.HRDeptAdjustVO;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.itf.om.IDeptAdjustService;
import nc.itf.om.IDeptManageService;
import nc.itf.om.IDeptQueryService;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.pub.SuperVOHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;

public class DeptAdjustServiceImpl implements IDeptAdjustService {

	/** ����DAO */
	// private DeptDao deptDao = null;

	/** ���Ų�ѯ�ӿ� **/
	private IDeptQueryService deptQueryService = null;

	/** ���T�I�սӿ� **/
	private IDeptManageService deptManageService = null;

	/** baseDao **/
	private BaseDAO baseDAO = null;

	/**
	 * Ϊ�°汾����
	 */
	private static final String PK_VID_FOR_DEPT_VER = "VIRTUAL_PK_DEPT_V";

	// �M�в��T�汾����,��Ҫ�M���ޏ�pk_dept�ı���(�@Щ����pk_dept��pk_dept_vͬ��,hi_stapply����)
	private static String[] NEED_FIX_TABLE_NAME = { "hi_psnjob", "om_deptadj",
			"tbm_leaveplan", "hi_stapply" };
	// ���Tpk_dept���ֶ���
	private static String[] NEED_FIX_TABLE_COLUMN = { "pk_dept", "pk_dept",
			"pk_dept", "newpk_dept" };

	/** ����QService **/
	private IDeptQueryService getDeptQueryService() {
		if (deptQueryService == null) {
			deptQueryService = NCLocator.getInstance().lookup(
					IDeptQueryService.class);
		}
		return deptQueryService;
	}

	/** ����MService **/
	private IDeptManageService getDeptManageService() {
		if (deptManageService == null) {
			deptManageService = NCLocator.getInstance().lookup(
					IDeptManageService.class);
		}
		return deptManageService;
	}

	private BaseDAO getBaseDAO() {
		if (null == baseDAO) {
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}

	/*
	 * private DeptDao getDeptDao() { if (deptDao == null) { deptDao = new
	 * DeptDao(); } return deptDao; }
	 */

	/**
	 * ���ݲ��Ų�ѯ��ǰ���ŵİ汾PK
	 * 
	 * @param pk_dept
	 * @return ��ǰ���ŵİ汾PK ҵ���߼�:��ѯ��ǰ���ŵİ汾PK
	 */
	public String queryLastDeptByPk(String pk_dept) {
		String pk_dept_v = null;
		try {
			pk_dept_v = getDeptQueryService().getDeptVid(pk_dept, new UFDate());
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return pk_dept_v;
	}

	/**
	 * ִ�в��Ű汾������̨�΄գ����������޸ģ�
	 * 
	 * @param date
	 * @throws BusinessException
	 *             ҵ���߼�:ִ����Ч����Ϊ�����ڵ����в��Ű汾������,
	 *             1.�������ڵĵ���vo�ϴ洢���ֶ���Ϣ,�洢��org_dept,org_dept_v
	 *             ,org_orgs,org_orgs_v,org_reportorg.���Ҹ��¹�����ʮ���ű�(�ο��������޸��߼�).
	 *             2.���ű���Ͳ������Ƹ���ʱ, �����ˆT��ӛ�( �ѽ��Y������ӛ䛲�����)((�ο��������޸��߼�))
	 *             3.��Ա������¼��Ҫ����
	 *             (�ο��������޸��߼�)4.islastversion�������±�־,�ò���������¼��Ϊfalse(�ο��������޸��߼�)
	 *             4.iseffective����ִ�б��.iseffectiveΪfalse�Ĳ�ִ��. (5.�����ο��������޸��߼�)
	 *             6.�޸��Єӳ��N��ӛ�r,�{�ó��N/ȡ�����N߉݋
	 */
	@SuppressWarnings("unchecked")
	public void executeDeptVersion(UFLiteralDate date) throws BusinessException {

		// ��ѯ����Ч���ڵ����е���
		String sqlStr = "select * from om_deptadj where effectivedate = '"
				+ date.toStdString() + "' and iseffective = 'N' and dr = 0";
		List<HRDeptAdjustVO> needExecuteVOs = (List<HRDeptAdjustVO>) getBaseDAO()
				.executeQuery(sqlStr,
						new BeanListProcessor(HRDeptAdjustVO.class));
		if (null != needExecuteVOs && needExecuteVOs.size() > 0) {
			// ִ�а汾��
			for (HRDeptAdjustVO vo : needExecuteVOs) {
				try {
					if (null == vo) {
						continue;
					}
					AggHRDeptVO saveVO = new AggHRDeptVO();
					HRDeptVO deptVO = HRDeptAdjust2HRDeptVO(vo);
					// �@ȡ��ǰ�Ĳ��T��Ϣ
					sqlStr = "select * from org_dept where pk_dept = '"
							+ deptVO.getPk_dept() + "' ";
					HRDeptVO curVO = (HRDeptVO) getBaseDAO().executeQuery(
							sqlStr, new BeanProcessor(HRDeptVO.class));
					if(deptVO.getPk_vid()==null ||deptVO.getPk_vid().equals("~")||deptVO.getPk_vid().equals("null")){
						deptVO.setPk_vid(curVO.getPk_vid());
					}
					String oldPkDept = deptVO.getPk_dept();
					// �O�Æ���
					deptVO.setEnablestate(2);
					deptVO.setIslastversion(new UFBoolean(true));
					saveVO.setParentVO(deptVO);
					//����������־ TODO
					if(curVO != null && curVO.getHrcanceled() != null && vo.getHrcanceled() != null){
						//ԭ���ǳ���״̬,������Ϊ�ǳ���״̬,��ȡ�����N
						if(curVO.getHrcanceled().booleanValue() && !vo.getHrcanceled().booleanValue()){
							DeptHistoryVO historyVO = buildDeptHistoryVO4UnCancel(deptVO);
							//�����N
							AggHRDeptVO[] uncanceledDepts = getDeptManageService().uncancel(saveVO, historyVO, false,
									false, true);
							// ���������°汾
							AggHRDeptVO[] newVOs = getDeptManageService().createDeptVersion(uncanceledDepts);
							if(newVOs != null && newVOs.length >= 1 && newVOs[0] != null){
								saveVO = getDeptManageService().update(newVOs[0], false);
								deptVO = HRDeptAdjust2HRDeptVO(vo);
								// �@ȡ��ǰ�Ĳ��T��Ϣ
								sqlStr = "select * from org_dept where pk_dept = '"
										+ deptVO.getPk_dept() + "' ";
								curVO = (HRDeptVO) getBaseDAO().executeQuery(
										sqlStr, new BeanProcessor(HRDeptVO.class));
							}
							
						}
					}
					
					if (vo.getPk_dept_v() != null
							&& vo.getPk_dept_v().equals(PK_VID_FOR_DEPT_VER)) {
						// ���������,���N�Ȅh�������ϵęn����Ϣ
						getBaseDAO().deleteVO(deptVO);
						// ���������˜�ƽ̨����߉݋
						AggHRDeptVO rtnVO = getDeptManageService().insert(
								saveVO);
						String pk_dept = null;
						if (rtnVO.getParentVO() != null) {
							pk_dept = rtnVO.getParentVO().getPrimaryKey();
						}
						// ��Q����ԭ�����õ�ֵ
						dataFix(oldPkDept, pk_dept);
					} else {
						DeptHistoryVO historyVO = buildDeptHistoryVO4Update(deptVO);
						
						
						AggHRDeptVO newVO = getDeptManageService()
								.createDeptVersion(saveVO);
						if (null == newVO) {
							continue;
						}
						// ������޸�,ֱ���޸�������Ϣ
						getBaseDAO().updateVO((HRDeptVO) newVO.getParentVO());
						// ������T���a׃�����T���Q׃��,�t�����ˆT��ӛ䛣�
						if (vo.getCode() != null && vo.getName() != null) {
							if (!vo.getCode().equals(curVO.getCode())
									|| !vo.getName().equals(curVO.getName())) {
								getDeptManageService().rename(newVO, historyVO,
										true);
							}
						}
					}
					// ��д��־:
					sqlStr = "update om_deptadj set iseffective = 'Y' where pk_deptadj = '"
							+ vo.getPk_deptadj() + "'";
					getBaseDAO().executeUpdate(sqlStr);
				} catch (Exception e) {
					Debug.debug(e.getMessage());
					continue;
				}
			
				
				
				
			}
		}
	}

	/**
	 * ���I�ޏ�,��Qԭ�����I���µ����I
	 * 
	 * @param pk_dept
	 * @param pk_dept2
	 * @throws BusinessException
	 */
	private void dataFix(String old_pk_dept, String new_pk_dept)
			throws BusinessException {
		if (null == old_pk_dept || null == new_pk_dept || new_pk_dept.equals(old_pk_dept)) {
			return;
		}
		// ����PK��Q�fPK
		for (int i = 0; i < NEED_FIX_TABLE_NAME.length; i++) {
			String sqlStr = "update " + NEED_FIX_TABLE_NAME[i] + " set "
					+ NEED_FIX_TABLE_COLUMN[i] + " = '" + new_pk_dept + "' "
					+ " where " + NEED_FIX_TABLE_COLUMN[i] + " = '"
					+ old_pk_dept + "' and dr = 0";
			getBaseDAO().executeUpdate(sqlStr);
		}
	}

	/*
	 * ��ѯ�Ƿ��Ѵ���ָ�����Tδ��Ч���{����Ո�� pk_dept ����UFBoolean True���ڣ�False������
	 * ҵ���߼�:��ѯ�Ƿ��Ѵ���ָ�����Tδ��Ч���{����Ո�μ�������Ч���ڴ��ڵ�ǰ���ڵĴ˲��ŵĵ�����.
	 *  ��������
	 */
	public UFBoolean isExistDeptAdj(String pk_dept,String pk_deptadj) throws BusinessException {
		String sqlStr = null;
		if(pk_deptadj != null){
			sqlStr = "select count(*) from om_deptadj where pk_dept ='"
					+ pk_dept + "' and effectivedate > '"
					+ new UFDate().toStdString() + "' and pk_deptadj <> '"+pk_deptadj
					+"' and dr = 0 and isnull(iseffective,'N') = 'N'";
		}else{
			sqlStr = "select count(*) from om_deptadj where pk_dept ='"
					+ pk_dept + "' and effectivedate > '"
					+ new UFDate().toStdString() + "' and dr = 0 and isnull(iseffective,'N') = 'N'";
		}
		
		Integer result = getIntegerDataBySQL(sqlStr);
		if (null != result && result > 0) {
			return new UFBoolean(false);
		}
		return new UFBoolean(true);
	}

	/**
	 * ����У�����1--���ŵ���Ψһ��(nc.impl.pubapp.pattern.rule.IRule<BatchOperateVO>)
	 * 
	 * @param vo
	 * @throws BusinessException
	 *             ҵ���߼�: 1. ͬһ���Tֻ����һ����Ч���ڴ��ڵ�ǰ���چΓ�(��̨����)
	 */
	public UFBoolean validateDept(HRDeptAdjustVO vo) throws BusinessException {
		if (null == vo) {
			throw new BusinessException("�oЧӛ�!");
		}
		if (null == vo.getPk_dept()) {
			throw new BusinessException("���T���I���Þ��!");
		}
		if (null == vo.getEffectivedate()) {
			throw new BusinessException("��Ч���ڲ��Þ��");
		}
		if (!isExistDeptAdj(vo.getPk_dept(),vo.getPk_deptadj()).booleanValue()) {
			throw new BusinessException("ԓ���T�ѽ�����δ����Ч���ڵ���Ո��");
		}
		return new UFBoolean(true);
	}

	/**
	 * ������Ϣ������д
	 * 
	 * @param deptVO
	 * @throws BusinessException
	 *             ҵ���߼�:��������ʱ,����������ڴ��ڵ�ǰ����, ��ô��дһ�ʵ��ݵ����ڵ�,
	 *             ��deptVO��ֵ��HRDeptAdjustVO�ϵ��ֶ�,��������д��ǰ�û�,
	 *             ����������д��ǰ����,��Ч����Ϊ���ŵĳ�������,��������ΪVO�ϵĲ���.
	 *             ����ò���PK������Ч�����Ѿ�����,��ô�����ڽ��л�д,ͬʱ�ڱ��沿�ŵ�ʱ���׳��쳣.
	 */
	public AggHRDeptVO writeBack4DeptAdd(AggHRDeptVO aggDeptVO)
			throws BusinessException {
		if (aggDeptVO == null || aggDeptVO.getParentVO() == null) {
			return null;
		}
		HRDeptVO deptVO = (HRDeptVO) aggDeptVO.getParentVO();
		// �O�à�B��δ����
		deptVO.setEnablestate(1);
		// �������ڴ��ڵ�ǰ���ڲſ��Ի�д
		if (null != deptVO && deptVO.getCreatedate() != null
				&& isAfterToday(deptVO.getCreatedate())) {
			// ����PK_VID��ֵ��Ϊnull���ܴ洢
			deptVO.setPk_vid(PK_VID_FOR_DEPT_VER);
			deptVO.setCreationtime(new UFDateTime());
			deptVO.setDr(0);
			// ��org_dept������һ�l���n,����ʲ�N������
			getBaseDAO().insertVO(deptVO);
			// ���¹��c������ӛ�
			HRDeptAdjustVO saveVO = HRDeptVO2HRDeptAdjust(deptVO);
			saveVO.setEffectivedate(deptVO.getCreatedate());
			saveVO.setBilldate(new UFDate());
			saveVO.setDr(0);
			saveVO.setDisplayorder(999999);
			saveVO.setIseffective(new UFBoolean(false));
			saveVO.setCreationtime(new UFDateTime());
			saveVO.setAdj_code(String.valueOf(System.currentTimeMillis()));
			saveVO.setPk_dept_v(PK_VID_FOR_DEPT_VER);
			validateDept(saveVO);
			getBaseDAO().insertVO(saveVO);
		}
		aggDeptVO.setParentVO(deptVO);
		return aggDeptVO;
	}

	/**
	 * ������Ϣ������д
	 * 
	 * @param deptVO
	 * @throws BusinessException
	 *             ҵ���߼�:��������ʱ,����������ڴ��ڵ�ǰ����, ��ô��дһ�ʵ��ݵ����ڵ�,
	 *             ��deptVO��ֵ��HRDeptAdjustVO�ϵ��ֶ�,��������д��ǰ�û�,
	 *             ����������д��ǰ����,��Ч����Ϊ���ŵĳ�������,��������ΪVO�ϵĲ���.
	 *             ����ò���PK������Ч�����Ѿ�����,��ô�����ڽ��л�д,ͬʱ�ڱ��沿�ŵ�ʱ���׳��쳣.
	 */
	public AggHRDeptVO writeBack4DeptUnCancel(AggHRDeptVO aggDeptVO,UFLiteralDate effective)
			throws BusinessException {
		if (aggDeptVO == null || aggDeptVO.getParentVO() == null ) {
			return null;
		}
		if(effective == null){
			throw new BusinessException("Ո������Ч����!");
		}
		
		HRDeptVO deptVO = (HRDeptVO) aggDeptVO.getParentVO();
		// �O�à�B��δ����
		deptVO.setEnablestate(1);
		// �������ڴ��ڵ�ǰ���ڲſ��Ի�д
		if (effective != null && isAfterToday(effective)) {
			// ���¹��c������ӛ�
			HRDeptAdjustVO saveVO = HRDeptVO2HRDeptAdjust(deptVO);
			saveVO.setEffectivedate(effective);
			saveVO.setBilldate(new UFDate());
			saveVO.setDr(0);
			saveVO.setDisplayorder(deptVO.getDisplayorder());
			saveVO.setIseffective(new UFBoolean(false));
			saveVO.setCreationtime(new UFDateTime());
			saveVO.setAdj_code(String.valueOf(System.currentTimeMillis()));
			saveVO.setPk_dept_v(deptVO.getPk_vid());
			//ȡ�����N���I
			saveVO.setHrcanceled(UFBoolean.FALSE);
			validateDept(saveVO);
			getBaseDAO().insertVO(saveVO);
		}
		aggDeptVO.setParentVO(deptVO);
		return aggDeptVO;
	}

	/**
	 * pk_org ������Դ��֯PK
	 * 
	 * @param pk_org
	 * @returnList<HRDeptVO>
	 * @throws BusinessException
	 * 
	 *             �ڲ�����Ϣ�ڵ�,��Ҫ���˳���δ��Ч�Ĳ���,�ڴ˽ڵ��ѯ����������Դ��֯��,
	 *             ������Ч�����ڵ�ǰ����֮�����������δ��Ч�Ĳ���,,
	 *             ��װ��HRDeptVO��list����,��������Ϣ�ڵ��ѯδ��Ч����ʹ��
	 */
	@SuppressWarnings("unchecked")
	public List<HRDeptVO> queryOFutureDept(String pk_org, String whereSql)
			throws BusinessException {
		if (whereSql == null) {
			whereSql = " 1 = 1 ";
		}
		String sqlStr = " select * from org_dept where pk_org = '" + pk_org
				+ "' and  createdate > '" + (new UFDate()).toStdString()
				+ "' and " + whereSql;

		List<HRDeptVO> result = (List<HRDeptVO>) getBaseDAO().executeQuery(
				sqlStr, new BeanListProcessor(HRDeptVO.class));
		if (result == null) {
			result = new ArrayList<>();
		}
		return result;
	}

	/**
	 * 
	 * @param HRDeptVO
	 * @return
	 * @throws BusinessException
	 *             ҵ���߼�:�ڲ��Žڵ��ѯδ��Ч�Ĳ���,�Լ��ڽ��к�̨�����дʱ ,��Ҫ������VOת���ɵ���VO
	 */
	public HRDeptVO HRDeptAdjust2HRDeptVO(HRDeptAdjustVO vo)
			throws BusinessException {
		// �Ȳ��ҵ�ǰ�Ĳ�����Ϣ
		String sqlStr = "select * from org_dept where pk_dept = '"
				+ vo.getPk_dept() + "'";
		HRDeptVO resultVO = (HRDeptVO) getBaseDAO().executeQuery(sqlStr,
				new BeanProcessor(HRDeptVO.class));
		if (null == resultVO) {
			resultVO = new HRDeptVO();
		}
		// ���޸���Ϣ���ǵ���ǰ��Ϣ����
		if (null != vo) {
			resultVO.setPk_vid(vo.getPk_dept_v());
			resultVO.setPk_dept(vo.getPk_dept());
			resultVO.setCode(vo.getCode());
			resultVO.setName(vo.getName());
			resultVO.setInnercode(vo.getInnercode());
			resultVO.setPk_fatherorg(vo.getPk_fatherorg());
			resultVO.setPk_group(vo.getPk_group());
			resultVO.setPk_org(vo.getPk_org());
			resultVO.setDepttype(vo.getDepttype());
			resultVO.setDeptlevel(vo.getDeptlevel());
			resultVO.setDeptduty(vo.getDeptduty());
			resultVO.setCreatedate(vo.getCreatedate());
			resultVO.setShortname(vo.getShortname());
			resultVO.setMnecode(vo.getMnecode());
			resultVO.setHrcanceled(vo.getHrcanceled());
			resultVO.setDeptcanceldate(vo.getDeptcanceldate());
			resultVO.setEnablestate(vo.getEnablestate());
			resultVO.setDisplayorder(vo.getDisplayorder());
			resultVO.setDataoriginflag(vo.getDataoriginflag());
			resultVO.setOrgtype13(vo.getOrgtype13());
			resultVO.setOrgtype17(vo.getOrgtype17());
			resultVO.setPrincipal(vo.getPrincipal());
			resultVO.setTel(vo.getTel());
			resultVO.setAddress(vo.getAddress());
			resultVO.setMemo(vo.getMemo());
			resultVO.setIslastversion(vo.getIslastversion());
			resultVO.setCreator(vo.getCreator());
			resultVO.setCreationtime(vo.getCreationtime());
			resultVO.setModifiedtime(vo.getModifiedtime());
			resultVO.setModifier(vo.getModifier());
		}
		return resultVO;
	}

	/**
	 * 
	 * @param HRDeptVO
	 * @return
	 * @throws BusinessException
	 *             ҵ���߼�:�ڲ��Žڵ��ѯδ��Ч�Ĳ���,�Լ��ڽ��к�̨�����дʱ ,��Ҫ������VOת���ɵ���VO
	 */
	public HRDeptAdjustVO HRDeptVO2HRDeptAdjust(HRDeptVO vo)
			throws BusinessException {
		HRDeptAdjustVO resultVO = new HRDeptAdjustVO();
		if (null != vo) {
			resultVO.setPk_dept_v(null);
			resultVO.setPk_dept(vo.getPk_dept());
			resultVO.setCode(vo.getCode());
			resultVO.setName(vo.getName());
			resultVO.setInnercode(vo.getInnercode());
			resultVO.setPk_fatherorg(vo.getPk_fatherorg());
			resultVO.setPk_group(vo.getPk_group());
			resultVO.setPk_org(vo.getPk_org());
			resultVO.setDepttype(vo.getDepttype());
			resultVO.setDeptlevel(vo.getDeptlevel());
			resultVO.setDeptduty(vo.getDeptduty());
			resultVO.setCreatedate(vo.getCreatedate());
			resultVO.setShortname(vo.getShortname());
			resultVO.setMnecode(vo.getMnecode());
			resultVO.setHrcanceled(vo.getHrcanceled());
			resultVO.setDeptcanceldate(vo.getDeptcanceldate());
			resultVO.setEnablestate(vo.getEnablestate());
			resultVO.setDisplayorder(vo.getDisplayorder());
			resultVO.setDataoriginflag(vo.getDataoriginflag());
			resultVO.setOrgtype13(vo.getOrgtype13());
			resultVO.setOrgtype17(vo.getOrgtype17());
			resultVO.setPrincipal(vo.getPrincipal());
			resultVO.setTel(vo.getTel());
			resultVO.setAddress(vo.getAddress());
			resultVO.setMemo(vo.getMemo());
			resultVO.setIslastversion(new UFBoolean(true));
			resultVO.setCreator(vo.getCreator());
			resultVO.setCreationtime(vo.getCreationtime());
			resultVO.setModifiedtime(vo.getModifiedtime());
			resultVO.setModifier(vo.getModifier());
			//��ȡ��֯�汾
			String sqlStr = "select pk_vid from org_orgs where pk_org = '"+vo.getPk_org()+"' ";
			Object pkvid = getSingleDataBySQL(sqlStr);
			if(null != pkvid){
				resultVO.setPk_org_v(String.valueOf(pkvid));
			}
		}
		return resultVO;
	}

	/**
	 * �Զ�����--���ų��� ���У�����2,���ܽ���ɾ��,ɾ��ʱ����ɾ��������Ϣ������ ���ų�����ȡ��������ʱ�ñ�׼����
	 * ����������ʱ�����а汾������
	 * @param date
	 * @throws BusinessException
	 */
	public void executeDeptCancel(UFLiteralDate date) throws BusinessException {
		return;
	}

	/**
	 * void validatePsn(HRDeptAdjustVO vo) throws BusinessException;
	 * 
	 * @param vo
	 * @throws BusinessException
	 *             ҵ���߼�: 2. У����Ա����������Ч�����Ƿ��ڲ�����Ч����֮��
	 */
	public UFBoolean validatePsn(HRDeptAdjustVO vo) throws BusinessException {
		if (null == vo) {
			throw new BusinessException("�oЧӛ�!");
		}
		if (null == vo.getPk_dept()) {
			throw new BusinessException("���T���I���Þ��!");
		}
		if (null == vo.getEffectivedate()) {
			throw new BusinessException("��Ч���ڲ��Þ��");
		}
		// �����Ƿ�����ڲ��T����֮ǰ����Ա���ü�¼
		String sqlStr = "select count(*) from hi_stapply where  newpk_dept = '"
		+vo.getPk_dept()+"'and  EFFECTDATE > '"+vo.getEffectivedate().toStdString()+"' ";
		Integer rtn = getIntegerDataBySQL(sqlStr);
		if (null != rtn && rtn > 0) {
			throw new BusinessException("�oЧ����!ԓ����֮ǰ�ѽ������ˆT�{����Ո!Ո�x�����������.");
		}
		return new UFBoolean(true);
	}

	/**
	 * �����ݿ��ȡһ��ֵ
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unused")
	private Object getSingleDataBySQL(String sql) throws DAOException {
		return (Object) getBaseDAO().executeQuery(sql, new ColumnProcessor());
	}

	/**
	 * �����ݿ��ȡһ������
	 * 
	 * @return ���Ϊnull �򷵻� null
	 * @throws DAOException
	 */
	private Integer getIntegerDataBySQL(String sql) throws DAOException {
		Object result = getBaseDAO().executeQuery(sql, new ColumnProcessor());
		Integer rtn = null;
		try {
			rtn = Integer.parseInt(String.valueOf(result));
		} catch (Exception e) {
			Debug.debug(e.getMessage());
		}
		return rtn;
	}

	/**
	 * ֻ����ɾ��ִ�б�־δ�򹴵�,������Ч�����ڵ�ǰ����֮��ĵ���. ɾ��ʱ,'��������'�д���������δ���������ŵļ�¼ʱ��ɾ������������ʱ����;
	 * �Ѿ����¼�����ʱ,ɾ������
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public UFBoolean validateDel(HRDeptAdjustVO vo) throws BusinessException {
		if (null == vo) {
			throw new BusinessException("�Γ�����!�o���Дஔǰ��B!");
		}
		// δ�򹴵�,������Ч�����ڵ�ǰ����֮���
		if (isAfterToday(vo.getEffectivedate()) && vo.getIseffective() != null
				&& !vo.getIseffective().booleanValue()) {
			// ��ԃ�{��ӛ���,�Ƿ��������ô˲��T�ĆΓ�
			String sqlStr = "select count(*) from hi_stapply where newpk_dept ='"
					+ vo.getPk_dept() + "' and dr = 0";
			Integer num = getIntegerDataBySQL(sqlStr);
			if (num != null && num > 0) {
				throw new BusinessException("ԓ���T߀��δ̎�����ˆT�{����Ո,Ոȡ���{����لh��!");
			}
			// �Ѿ����¼�����ʱ,ɾ������
			sqlStr = "select count(*) from om_deptadj where pk_fatherorg = '"
					+ vo.getPk_dept() + "' and dr = 0";
			num = getIntegerDataBySQL(sqlStr);
			if (num != null && num > 0) {
				throw new BusinessException("ԓ���T�Ѵ����¼����T,�o���h��!");
			}
		} else {
			throw new BusinessException("�Γ��ѽ������л������^��ǰ����,���܄h��!");
		}
		return new UFBoolean(false);
	}

	/**
	 * �Д������Ƿ��ڮ�ǰ����֮��
	 * 
	 * @param date
	 * @return
	 */
	private boolean isAfterToday(UFLiteralDate date) {
		// ��ȡ�������ڵļ�㷽��:Thread.sleep(24*60*60*1000); Date date = new Date();
		if (null != date) {
			// UFLiteralDate tomorrow = new UFLiteralDate().getDateAfter(1);
			return date.after(new UFLiteralDate());
		}
		return false;
	}

	/**
	 * ��ò��ű����ʷ<br>
	 * ������
	 * @return
	 */
	private DeptHistoryVO buildDeptHistoryVO4Update(HRDeptVO vo) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// ��׼��λ
		deptHistoryVO.setApprovedept(null);
		// ��׼�ĺ�
		deptHistoryVO.setApprovenum(null);
		UIMultiLangCombox lang = new UIMultiLangCombox();
		MultiLangText multiLangText = new MultiLangText();
		multiLangText.setText(vo.getName());
		multiLangText.setText2(vo.getName());
		lang.setMultiLangText(multiLangText);
		// �²�����
		MultiLangText mutiLangText = lang.getMultiLangText();
		SuperVOHelper.copyMultiLangAttribute(mutiLangText, deptHistoryVO);
		// ����
		deptHistoryVO.setCode(vo.getCode());
		// ���ż���
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// ������
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// ��Ч����--Ĭ�J�ǽ���
		deptHistoryVO.setEffectdate(new UFLiteralDate());
		// ��ע
		deptHistoryVO.setMemo("�ԄӰ汾��");
		// �������-����
		deptHistoryVO.setChangetype(DeptChangeType.RENAME);
		// ��������
		deptHistoryVO.setPk_dept(vo.getPk_dept());
		// ��֯����
		deptHistoryVO.setPk_org(vo.getPk_org());

		return deptHistoryVO;
	}

	/**
	 * ��ò��ű����ʷ<br>
	 * ȡ�����N��
	 * @return
	 */
	private DeptHistoryVO buildDeptHistoryVO4UnCancel(HRDeptVO vo) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// ��׼��λ
		deptHistoryVO.setApprovedept(null);
		// ��׼�ĺ�
		deptHistoryVO.setApprovenum(null);
		// ����
		deptHistoryVO.setCode(vo.getCode());
		// ������
		SuperVOHelper.copyMultiLangAttribute(vo, deptHistoryVO);
		// ���ż���
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// ������
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// ��Ч����
		deptHistoryVO.setEffectdate(new UFLiteralDate());
		// ��ע
		deptHistoryVO.setMemo(null);
		// �������-������
		deptHistoryVO.setChangetype(DeptChangeType.HRUNCANCELED);
		// ��������
		deptHistoryVO.setPk_dept(vo.getPk_dept());
		// ��֯����
		deptHistoryVO.setPk_org(vo.getPk_org());
		// ���ղ���
		deptHistoryVO.setIsreceived(UFBoolean.FALSE);

		return deptHistoryVO;
	}
	@Override
	public String getNewDeptVerPK() {
		return PK_VID_FOR_DEPT_VER;
	}

}