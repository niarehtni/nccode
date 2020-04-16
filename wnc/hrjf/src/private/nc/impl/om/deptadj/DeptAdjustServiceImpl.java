package nc.impl.om.deptadj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.om.IDeptAdjustService;
import nc.itf.om.IDeptManageService;
import nc.itf.om.IDeptQueryService;
import nc.itf.org.IOrgVersionManageService;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.logging.Debug;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.DeptChangeType;
import nc.vo.om.hrdept.DeptHistoryVO;
import nc.vo.om.hrdept.HRDeptAdjustVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.pub.SQLHelper;
import nc.vo.om.pub.SuperVOHelper;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.vorg.DeptVersionVO;

import org.apache.commons.lang.StringUtils;

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

	// �M�в��T�汾����,��Ҫ�M���ޏ�pk_dept�ı���(�@Щ���pk_dept��pk_dept_vͬ��,hi_stapply����)
	private static String[] NEED_FIX_TABLE_NAME = { "hi_psnjob", "om_deptadj", "tbm_leaveplan", "hi_stapply" };
	// ���Tpk_dept���ֶ���
	private static String[] NEED_FIX_TABLE_COLUMN = { "pk_dept", "pk_dept", "pk_dept", "newpk_dept" };

	/** ����QService **/
	private IDeptQueryService getDeptQueryService() {
		if (deptQueryService == null) {
			deptQueryService = NCLocator.getInstance().lookup(IDeptQueryService.class);
		}
		return deptQueryService;
	}

	/** ����MService **/
	private IDeptManageService getDeptManageService() {
		if (deptManageService == null) {
			deptManageService = NCLocator.getInstance().lookup(IDeptManageService.class);
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
			// ssx MOD ȡ��ǰ������Ч���T�汾 �Ğ� ȡ���n��ָ�������°汾
			// on 2019-06-28
			if (!StringUtils.isEmpty(pk_dept)) {
				pk_dept_v = ((HRDeptVO) this.getBaseDAO().retrieveByPK(HRDeptVO.class, pk_dept)).getPk_vid();
			}
			// end
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return pk_dept_v;
	}

	@Override
	public String executeDeptVersion(HRDeptAdjustVO[] deptAdjVOs, UFLiteralDate date) throws BusinessException {
		StringBuilder errMsg = new StringBuilder();

		if (deptAdjVOs == null || deptAdjVOs.length == 0) {
			return errMsg.toString();
		}

		for (HRDeptAdjustVO deptAdjVO : deptAdjVOs) {
			if (deptAdjVO == null) {
				continue;
			}
			String sqlStr = "";
			try {
				AggHRDeptVO deptAggVO = new AggHRDeptVO();
				HRDeptVO deptVO = HRDeptAdjust2HRDeptVO(deptAdjVO);
				// �@ȡ��ǰ�Ĳ��T��Ϣ
				HRDeptVO curVO = (HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class, deptVO.getPk_dept());
				if (!(deptAdjVO.getEnablestate() == 1 && !PK_VID_FOR_DEPT_VER.equals(deptAdjVO.getPk_dept_v()))) {
					checkDeptCanceled(curVO, date);
				}

				// MOD (PM25602�����Ӳ��T��ǰ�汾�_ʼ������춵����Ч���ڕr�����M�а汾��)
				// added on 2019-03-28 by ssx
				if (curVO.getVstartdate() != null) {
					if (curVO.getVstartdate().toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).isSameDate(date)
							|| curVO.getVstartdate().toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE)
									.after(deptAdjVO.getEffectivedate())) {
						errMsg.append("���T [" + curVO.getCode() + "] �Ѵ��������Ո����Ч���ڵİ汾�������M�а汾��̎��");
						continue;
					}
				}

				if (curVO.getPk_vid() != null && curVO.getPk_vid().trim().equals(PK_VID_FOR_DEPT_VER)
						&& curVO.getCreatedate() != null && curVO.getCreatedate().after(new UFLiteralDate())) {
					errMsg.append("���T [" + curVO.getCode() + "] δ�_��Ч���ڣ������M�а汾��̎��");
					continue;
				}

				if (date.after(deptAdjVO.getEffectivedate())) {
					date = deptAdjVO.getEffectivedate();
				}
				// MOD END

				if (deptVO.getPk_vid() == null || deptVO.getPk_vid().equals("~") || deptVO.getPk_vid().equals("null")) {
					deptVO.setPk_vid(curVO.getPk_vid());
				}
				String oldPkDept = deptVO.getPk_dept();

				String newPkdeptV = curVO.getPk_vid();
				// �O�Æ���
				deptVO.setEnablestate(2);
				deptVO.setIslastversion(new UFBoolean(true));
				deptAggVO.setParentVO(deptVO);

				// ��������־
				if (curVO != null && curVO.getHrcanceled() != null && deptAdjVO.getHrcanceled() != null) {
					// ԭ���ǳ���״̬,������Ϊ�ǳ���״̬,��ȡ�����N
					if (curVO.getHrcanceled().booleanValue() && !deptAdjVO.getHrcanceled().booleanValue()) {
						DeptHistoryVO historyVO = buildDeptHistoryVO4UnCancel(deptVO);
						// �����N
						AggHRDeptVO[] uncanceledDepts = getDeptManageService().uncancel(deptAggVO, historyVO, false,
								false, true);
						// ���Ű汾���������� ������ 20190502 begin
						if (uncanceledDepts != null && uncanceledDepts.length > 0) {
							deptAggVO = uncanceledDepts[0];
						}
						HRDeptVO parentVO = (HRDeptVO) deptAggVO.getParentVO();
						IOrgVersionManageService orgManageService = NCLocator.getInstance().lookup(
								IOrgVersionManageService.class);
						String vno = queryNextVersionNO("org_dept_v", "pk_dept", deptVO.getPk_dept(),
								String.valueOf(date.getYear()));
						DeptVO pvo = (DeptVO) getBaseDAO().retrieveByPK(DeptVO.class, parentVO.getPk_dept());
						if (pvo != null) {
							pvo.setOrgtype13(UFBoolean.TRUE);
							pvo.setOrgtype17(UFBoolean.FALSE);
						}
						/*
						 * ���ñ�׼��Ʒ�İ汾�����񣬵��Ǳ�׼��ƷҪ��汾��ʱ�����޸����ƣ�
						 * �����ǵ�������֧�ֲ��޸����Ƶģ�����ķ������Ȱ������޸ģ��ٰ�����update����
						 */
						pvo.setName(pvo.getName() + "@@@@TWNC");
						if (pvo.getName2() != null)
							pvo.setName2(pvo.getName2() + "@@@@TWNC");
						if (pvo.getName3() != null)
							pvo.setName3(pvo.getName3() + "@@@@TWNC");
						// ���ñ�׼��Ʒ�ı�׼�汾������
						DeptVO hrDptNewVerVO = (DeptVO) orgManageService.createNewVersionVO("nc.vo.org.DeptVO", pvo,
								"�汾����" + date.getYear() + vno, date.getYear() + vno, new UFDate(date.toString()
										+ " 00:00:00"), new UFDate(date.getDateBefore(1).toString() + " 23:59:59"));
						if (hrDptNewVerVO == null) {
							continue;
						}
						// �������ٸ�update����,ͬʱ�Ѳ��ű�Ͳ��Ű汾��ĸ����ˡ������ܡ����ڵص������
						updateModifiedInfo(deptAdjVO, hrDptNewVerVO.getPk_vid());
						/*
						 * AggHRDeptVO[] newVOs =
						 * getDeptManageService().createDeptVersion
						 * (uncanceledDepts, new UFDate(date.toDate()));
						 */
						// HR���Ÿ���
						if (hrDptNewVerVO != null) {
							AggHRDeptVO newVO = new AggHRDeptVO();
							HRDeptVO hrdeptvo = (HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class,
									hrDptNewVerVO.getPk_dept());
							hrdeptvo.setFatherDeptChanged(parentVO.isFatherDeptChanged());
							hrdeptvo.setApprovedept(parentVO.getApprovedept());
							hrdeptvo.setApprovenum(parentVO.getApprovenum());
							hrdeptvo.setDeptduty(parentVO.getDeptduty());
							hrdeptvo.setManagescope(parentVO.isManagescope());
							hrdeptvo.setDept_charge(deptAdjVO.getPrincipal());
							newVO.setParentVO(hrdeptvo);
							deptAggVO = getDeptManageService().update(newVO, false);
						}
						// ���Ű汾���������� ������ 20190502 end
						// ��д��־:
						sqlStr = "update om_deptadj set iseffective = 'Y' where pk_deptadj = '"
								+ deptAdjVO.getPk_deptadj() + "'";
						getBaseDAO().executeUpdate(sqlStr);
						newPkdeptV = ((HRDeptVO) deptAggVO.getParentVO()).getPk_vid();
						sqlStr = "update  hi_psnjob set pk_dept_v = '" + newPkdeptV + "'  where begindate <= '"
								+ date.toStdString() + "' and isnull(enddate,'9999-12-31') > '" + date.toStdString()
								+ "' and  pk_dept = '" + oldPkDept + "'";
						getBaseDAO().executeUpdate(sqlStr);
						continue;
					}
				}

				if (curVO.getPk_vid() != null && curVO.getPk_vid().trim().equals(PK_VID_FOR_DEPT_VER)) {
					// ���������,���N�Ȅh�������ϵęn����Ϣ
					getBaseDAO().deleteVO(curVO);
					if (StringUtils.isEmpty((String) deptAggVO.getParentVO().getAttributeValue("innercode"))) {
						deptAggVO.getParentVO().setAttributeValue(
								"innercode",
								getInnerCodeByFatherOrg((String) deptAggVO.getParentVO().getAttributeValue(
										"pk_fatherorg")));
					}
					// ���������˜�ƽ̨����߉݋
					AggHRDeptVO rtnVO = getDeptManageService().insert(deptAggVO);
					String pk_dept = null;
					if (rtnVO.getParentVO() != null) {
						pk_dept = rtnVO.getParentVO().getPrimaryKey();
					}
					// ��Q����ԭ�����õ�ֵ
					dataFix(oldPkDept, pk_dept);
				} else {
					DeptHistoryVO historyVO = buildDeptHistoryVO4Update(deptVO, date);
					// ���Ű汾���������� ������ 20190502 begin
					HRDeptVO parentVO = (HRDeptVO) deptAggVO.getParentVO();
					IOrgVersionManageService orgManageService = NCLocator.getInstance().lookup(
							IOrgVersionManageService.class);
					String vno = queryNextVersionNO("org_dept_v", "pk_dept", deptVO.getPk_dept(),
							String.valueOf(date.getYear()));
					DeptVO pvo = (DeptVO) new BaseDAO().retrieveByPK(DeptVO.class, parentVO.getPk_dept());
					/*
					 * ���ñ�׼��Ʒ�İ汾�����񣬵��Ǳ�׼��ƷҪ��汾��ʱ�����޸����ƣ�
					 * �����ǵ�������֧�ֲ��޸����Ƶģ�����ķ������Ȱ������޸ģ��ٰ�����update����
					 */
					pvo.setName(pvo.getName() + "@@@@TWNC");
					if (pvo.getName2() != null)
						pvo.setName2(pvo.getName2() + "@@@@TWNC");
					if (pvo.getName3() != null)
						pvo.setName3(pvo.getName3() + "@@@@TWNC");
					// ���ñ�׼��Ʒ�ı�׼�汾������
					DeptVO hrDptNewVerVO = (DeptVO) orgManageService.createNewVersionVO("nc.vo.org.DeptVO", pvo, "�汾����"
							+ date.getYear() + vno, date.getYear() + vno, new UFDate(date.toString() + " 00:00:00"),
							new UFDateTime(date.getDateBefore(1).toString() + " 23:59:59").getDate());
					if (hrDptNewVerVO == null) {
						continue;
					}
					// �������ٸ�update����,ͬʱ�Ѳ��ű�Ͳ��Ű汾��ĸ����ˡ������ܡ����ڵص������
					updateModifiedInfo(deptAdjVO, hrDptNewVerVO.getPk_vid());
					AggHRDeptVO newVO = new AggHRDeptVO();
					parentVO = (HRDeptVO) new BaseDAO().retrieveByPK(HRDeptVO.class, hrDptNewVerVO.getPk_dept());
					newVO.setParentVO(parentVO);
					newPkdeptV = ((HRDeptVO) newVO.getParentVO()).getPk_vid();
					// ���벿�ű����ʷ����
					getBaseDAO().insertVO(historyVO);
					// ������T���a׃�����T���Q׃��,�t�����ˆT��ӛ䛣�
					int renameAndPrincipalChangeFlag = 0;
					if (deptAdjVO.getCode() != null && deptAdjVO.getName() != null) {
						if (!deptAdjVO.getCode().equals(curVO.getCode())
								|| !deptAdjVO.getName().equals(curVO.getName())) {
							// ׃�������Q
							renameAndPrincipalChangeFlag = 1;
						}
					}

					boolean principleChanged = false;
					if ((deptAdjVO.getPrincipal() == null && curVO.getCode() != null)
							|| (deptAdjVO.getPrincipal() != null && curVO.getPrincipal() == null)
							|| (deptAdjVO.getPrincipal() != null && curVO.getPrincipal() != null && !deptAdjVO
									.getPrincipal().equals(curVO.getPrincipal()))) {
						principleChanged = true;

						// ׃����ؓ؟��
						if (0 == renameAndPrincipalChangeFlag) {
							historyVO.setChangetype(DeptChangeType.CHANGEPRINCIPAL);
							renameAndPrincipalChangeFlag = 2;
						} else {
							renameAndPrincipalChangeFlag = 3;
						}
					}
					// �ж��ǲ����޸����ϼ����ţ�����޸��ˣ�Ҫ�޸�innercode wangywt 20190711
					String deptIncodesql = "";
					String randcode = "";
					if (deptAdjVO.getPk_fatherorg() == null && curVO.getPk_fatherorg() != null) {
						randcode = get4RandomCode();
						deptIncodesql = "update org_dept set innercode ='" + randcode
								+ "',pk_fatherorg='~',dept_charge = '" + deptAdjVO.getDept_charge()
								+ "' where pk_dept = '" + deptAdjVO.getPk_dept() + "'";
						getBaseDAO().executeUpdate(deptIncodesql);
						this.updateDeptInnercode(randcode, deptAdjVO.getPk_dept());
					} else if (deptAdjVO.getPk_fatherorg() != null
							&& !deptAdjVO.getPk_fatherorg().equals(curVO.getPk_fatherorg())) {
						randcode = get4RandomCode();
						deptIncodesql = "select innercode from org_dept where pk_dept = '"
								+ deptAdjVO.getPk_fatherorg() + "'";
						List<Object> list = (List<Object>) getBaseDAO().executeQuery(deptIncodesql,
								new ArrayListProcessor());
						Object[] obj = (Object[]) list.get(0);
						deptIncodesql = "update org_dept set innercode ='" + obj[0].toString() + randcode
								+ "',pk_fatherorg='" + deptAdjVO.getPk_fatherorg() + "' where pk_dept = '"
								+ deptAdjVO.getPk_dept() + "'";
						getBaseDAO().executeUpdate(deptIncodesql);
						this.updateDeptInnercode(obj[0].toString() + randcode, deptAdjVO.getPk_dept());
					}
					// ����dept_charge wangywt 20190711
					if (deptAdjVO.getDept_charge() == null && curVO.getDept_charge() != null) {
						getBaseDAO().executeUpdate(
								"update org_dept set dept_charge = '~'  where pk_dept = '" + deptAdjVO.getPk_dept()
										+ "'");
					} else if (deptAdjVO.getDept_charge() != null
							&& !deptAdjVO.getDept_charge().equals(curVO.getDept_charge())) {
						getBaseDAO().executeUpdate(
								"update org_dept set dept_charge = '" + deptAdjVO.getDept_charge()
										+ "' where pk_dept = '" + deptAdjVO.getPk_dept() + "'");
					}

					newPkdeptV = ((HRDeptVO) newVO.getParentVO()).getPk_vid();
					// ���Ű汾���������� ������ 20190502 end

					// ssx added on 2019-10-25
					// ���Tؓ؟��׃���r��ͬ��׃���¼����T���ϼ�����
					if (principleChanged) {
						updateChildCharger(deptAdjVO.getPk_dept(), deptAdjVO.getPrincipal());
						updateMemberManager(deptAdjVO.getPk_dept(), deptAdjVO.getPrincipal());
					}
					// end
				}
				// ��д��־:
				sqlStr = "update om_deptadj set iseffective = 'Y' where pk_deptadj = '" + deptAdjVO.getPk_deptadj()
						+ "'";
				getBaseDAO().executeUpdate(sqlStr);
				sqlStr = "update  hi_psnjob set pk_dept_v = '" + newPkdeptV + "'  where begindate <= '"
						+ date.toStdString() + "' and isnull(enddate,'9999-12-31') > '" + date.toStdString()
						+ "' and  pk_dept = '" + oldPkDept + "'";
				getBaseDAO().executeUpdate(sqlStr);

			} catch (Exception e) {
				errMsg.append("���T [" + deptAdjVO.getCode() + "] �汾���l���e�`: " + e.getMessage() + "\r\n");
				Logger.error(e.getMessage());
				continue;
			}
		}

		return errMsg.toString();
	}

	private String getInnerCodeByFatherOrg(String pk_fatherOrg) throws BusinessException {
		String fatherInnerCode = (String) this.getBaseDAO().executeQuery(
				"select innercode from org_dept where pk_dept = '" + pk_fatherOrg + "'", new ColumnProcessor());
		return fatherInnerCode + this.get4RandomCode();
	}

	private void updateMemberManager(String pk_dept, String principal) throws BusinessException {
		Collection<PsnJobVO> psnJobVOs = this.getBaseDAO().retrieveByClause(
				PsnJobVO.class,
				"pk_psnorg=(select pk_psnorg from hi_psnorg where pk_psndoc=hi_psnjob.pk_psndoc and lastflag='Y') and pk_dept='"
						+ pk_dept + "' and trnsevent<>4 and lastflag='Y' and isnull(jobglbdef9, '~')<> "
						+ (StringUtils.isEmpty(principal) ? "'~'" : "'" + principal + "'"));
		for (PsnJobVO psnJobVO : psnJobVOs) {
			psnJobVO.setAttributeValue("jobglbdef9", principal);
			this.getBaseDAO().updateVO(psnJobVO);
		}
	}

	// ssx added on 2019-10-25
	// ���Tؓ؟��׃���r��ͬ��׃���¼����T���ϼ�����
	private void updateChildCharger(String pk_fatherorg, String pk_psndoc) throws BusinessException {
		Collection<HRDeptVO> childrenVos = this.getBaseDAO().retrieveByClause(HRDeptVO.class,
				"pk_fatherorg='" + pk_fatherorg + "'");

		if (childrenVos != null && childrenVos.size() > 0) {
			for (HRDeptVO vo : childrenVos) {
				if (!pk_psndoc.equals(vo.getAttributeValue("dept_charge"))) {
					vo.setAttributeValue("dept_charge", pk_psndoc);
					this.getBaseDAO().updateVO(vo);

					DeptVersionVO lastVersionVo = (DeptVersionVO) this.getBaseDAO().retrieveByPK(DeptVersionVO.class,
							vo.getPk_vid());
					lastVersionVo.setAttributeValue("dept_charge", pk_psndoc);
					this.getBaseDAO().updateVO(lastVersionVo);
				}
			}
		}
	}// end

	private String queryNextVersionNO(String table, String pkField, String pkValue, String cyear)
			throws BusinessException {
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("select ");
		querySQL.append("max(right(vno, 2)) ");
		querySQL.append("from ");
		querySQL.append(table);
		querySQL.append(" where ");
		querySQL.append(SQLHelper.genSQLEqualClause(pkField, pkValue));
		querySQL.append(" and ");

		querySQL.append(SQLHelper.genSQLEqualClause("LEFT(vno, 4)", cyear));

		String maxNO = null;

		ColumnProcessor processor = new ColumnProcessor();
		Object object = getBaseDAO().executeQuery(querySQL.toString(), processor);
		if (object != null)
			maxNO = object.toString();
		if (maxNO == null)
			maxNO = "00";
		String nextNO = String.valueOf(Integer.parseInt(maxNO) + 1);
		while (nextNO.length() < 2) {
			nextNO = "0" + nextNO;
		}
		return nextNO;
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
	public String executeDeptVersion(UFLiteralDate date) throws BusinessException {

		// ��ѯ����Ч���ڵ����е���
		// MOD(PM25602���޸Ğ���춵�춮�ǰ���ڵ�����δ���ІΓ�)
		// added on 2019-03-28 by ssx
		// mod tank 2020��3��5�� 02:43:48 ����ʱ������,����������ϵ
		String sqlStr = "select * from om_deptadj where effectivedate <= '" + date.toStdString()
				+ "' and isnull(iseffective, 'N') = 'N' and dr = 0 order by effectivedate,om_deptadj.creationtime";
		// MOD END
		List<HRDeptAdjustVO> needExecuteVOs = (List<HRDeptAdjustVO>) getBaseDAO().executeQuery(sqlStr,
				new BeanListProcessor(HRDeptAdjustVO.class));
		HRDeptAdjustVO[] sortedVOs = sortByRely(needExecuteVOs);
		return executeDeptVersion(sortedVOs, date);
	}

	/**
	 * ����,���C���Έ��еĆΓ���,�����T���Ӳ��T�Ȉ��а汾��
	 * 
	 * @param needExecuteVOs
	 */
	private HRDeptAdjustVO[] sortByRely(List<HRDeptAdjustVO> needExecuteVOs) {
		if (needExecuteVOs == null || needExecuteVOs.size() < 0) {
			return new HRDeptAdjustVO[0];
		}
		HRDeptAdjustVO[] rtnVOs = needExecuteVOs.toArray(new HRDeptAdjustVO[0]);
		// ��������<���I,index>
		Map<String, Integer> pkIndexMap = new HashMap<>();
		for (int index = 0; index < rtnVOs.length; index++) {
			pkIndexMap.put(rtnVOs[index].getPk_dept(), index);
		}
		// ѭ�h����,�������T�����Ӳ��T��ǰ��
		for (int index = 0; index < rtnVOs.length; index++) {
			String pk_father = rtnVOs[index].getPk_fatherorg();
			if (pk_father != null) {
				// ��ԃ�����T�Ƿ�Ҳ�ڱ��ΰ汾��֮��
				Integer fatherIndex = pkIndexMap.get(pk_father);
				if (fatherIndex == null) {
					continue;
				}
				// ��������T���Ӳ��T����,�t���Q
				if (fatherIndex > index) {
					// ���Q
					HRDeptAdjustVO temp = rtnVOs[fatherIndex];
					rtnVOs[fatherIndex] = rtnVOs[index];
					rtnVOs[index] = temp;
					// index����
					pkIndexMap.put(rtnVOs[fatherIndex].getPk_dept(), fatherIndex);
					pkIndexMap.put(rtnVOs[index].getPk_dept(), index);
					// index����һ��
					index -= 1;
				}
			}
		}
		return rtnVOs;
	}

	/**
	 * ������֯���ơ�������֯���ơ��������ƣ�����Ӧ�İ汾���� ���Ӹ��¸����ˡ������ܡ����ڵص�
	 * 
	 * @param deptAdjVO
	 * @throws DAOException
	 */
	private void updateModifiedInfo(HRDeptAdjustVO deptAdjVO, String vidPK) throws DAOException {
		String name = deptAdjVO.getName();
		String name2 = deptAdjVO.getName2();
		String name3 = deptAdjVO.getName3();
		String name4 = deptAdjVO.getName4();
		String name5 = deptAdjVO.getName5();
		String name6 = deptAdjVO.getName6();
		// ���²��ű�,���¸�����(principal),������(glbdef3),���ڵص�(glbdef11),��������(createdate),���T���e(deptlevel),���T����(dept_charge)
		String sql_dept = "update org_dept set code='" + deptAdjVO.getCode() + "',name = " + getNmF(name)
				+ ", name2 = " + getNmF(name2) + ",name3=" + getNmF(name3) + ",name4=" + getNmF(name4) + ",name5="
				+ getNmF(name5) + ",name6=" + getNmF(name6) + ",DEPTLEVEL='" + deptAdjVO.getDeptlevel()
				+ "', PRINCIPAL = '" + deptAdjVO.getPrincipal() + "',GLBDEF11 = '" + deptAdjVO.getGlbdef11()
				+ "', GLBDEF3 = '" + deptAdjVO.getGlbdef3() + "',GLBDEF5=" + getNmF(name3) + ", createdate='"
				+ deptAdjVO.getEffectivedate() + "', dept_charge = '" + deptAdjVO.getDept_charge()
				+ "' where pk_dept = '" + deptAdjVO.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_dept);
		// ���²��Ű汾��,���¸�����(principal),������(glbdef3),���ڵص�(glbdef11),��������(createdate),���T���e(deptlevel),���T����(dept_charge)
		String sql_dept_v = "update org_dept_v set code='" + deptAdjVO.getCode() + "', name=" + getNmF(name)
				+ ", name2 = " + getNmF(name2) + ",name3=" + getNmF(name3) + ",name4=" + getNmF(name4) + ",name5="
				+ getNmF(name5) + ",name6=" + getNmF(name6) + ",DEPTLEVEL='" + deptAdjVO.getDeptlevel()
				+ "',PRINCIPAL = '" + deptAdjVO.getPrincipal() + "',GLBDEF11 = '" + deptAdjVO.getGlbdef11()
				+ "', GLBDEF3 = '" + deptAdjVO.getGlbdef3() + "',GLBDEF5=" + getNmF(name3) + ",createdate='"
				+ deptAdjVO.getEffectivedate() + "',dept_charge = '" + deptAdjVO.getDept_charge()
				+ "' where pk_vid = '" + vidPK + "'";
		getBaseDAO().executeUpdate(sql_dept_v);
		// ���±�����֯��
		String sql_report = "update org_reportorg set name=" + getNmF(name) + ", name2 = " + getNmF(name2) + ",name3="
				+ getNmF(name3) + ",name4=" + getNmF(name4) + ",name5=" + getNmF(name5) + ",name6=" + getNmF(name6)
				+ " where pk_reportorg = '" + deptAdjVO.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_report);

		String sql_create_org_v = "INSERT INTO ORG_ORGS_V (ADDRESS, CODE, COUNTRYZONE, CREATIONTIME, CREATOR, DATAORIGINFLAG, DEF1, DEF10, DEF11, DEF12, DEF13, DEF14, DEF15, DEF16, DEF17, DEF18, DEF19, DEF2, DEF20, DEF3, DEF4, DEF5, DEF6, DEF7, DEF8, DEF9, DR, ENABLESTATE, INNERCODE, ISBUSINESSUNIT, ISLASTVERSION, MEMO, MNECODE, MODIFIEDTIME, MODIFIER, NAME, NAME2, NAME3, NAME4, NAME5, NAME6, NCINDUSTRY, ORGANIZATIONCODE, ORGTYPE1, ORGTYPE10, ORGTYPE11, ORGTYPE12, ORGTYPE13, ORGTYPE14, ORGTYPE15, ORGTYPE16, ORGTYPE17, ORGTYPE18, ORGTYPE19, ORGTYPE2, ORGTYPE20, ORGTYPE21, ORGTYPE22, ORGTYPE23, ORGTYPE24, ORGTYPE25, ORGTYPE26, ORGTYPE27, ORGTYPE28, ORGTYPE29, ORGTYPE3, ORGTYPE30, ORGTYPE31, ORGTYPE32, ORGTYPE33, ORGTYPE34, ORGTYPE35, ORGTYPE36, ORGTYPE37, ORGTYPE38, ORGTYPE39, ORGTYPE4, ORGTYPE40, ORGTYPE41, ORGTYPE42, ORGTYPE43, ORGTYPE44, ORGTYPE45, ORGTYPE46, ORGTYPE47, ORGTYPE48, ORGTYPE49, ORGTYPE5, ORGTYPE50, ORGTYPE6, ORGTYPE7, ORGTYPE8, ORGTYPE9, PK_FATHERORG, PK_FORMAT, PK_GROUP, PK_ORG, PK_OWNORG, PK_TIMEZONE, PK_VID, PRINCIPAL, SHORTNAME, SHORTNAME2, SHORTNAME3, SHORTNAME4, SHORTNAME5, SHORTNAME6, TEL, TS, VENDDATE, VNAME, VNAME2, VNAME3, VNAME4, VNAME5, VNAME6, VNO, VSTARTDATE, CHARGELEADER, ENTITYTYPE, ISBALANCEUNIT, ISRETAIL, PK_ACCPERIODSCHEME, PK_CONTROLAREA, PK_CORP, PK_CURRTYPE, PK_EXRATESCHEME, REPORTCONFIRM, WORKCALENDAR, TAXCODE) "
				+ " select ADDRESS,CODE,'~',CREATIONTIME,CREATOR,DATAORIGINFLAG,DEF1,DEF10,DEF11,DEF12,DEF13,DEF14,DEF15,DEF16,DEF17,DEF18,DEF19,DEF2,DEF20,DEF3,DEF4,DEF5,DEF6,DEF7,DEF8,DEF9,DR,ENABLESTATE,INNERCODE,'N',ISLASTVERSION,MEMO,MNECODE,MODIFIEDTIME,MODIFIER,NAME,NAME2,NAME3,NAME4,NAME5,NAME6,'~',null,'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', PK_FATHERORG,'~',PK_GROUP,PK_DEPT,PK_ORG,'~', PK_VID,PRINCIPAL, SHORTNAME,SHORTNAME2,SHORTNAME3,SHORTNAME4,SHORTNAME5,SHORTNAME6,TEL,TS,VENDDATE,VNAME,VNAME2,VNAME3,VNAME4,VNAME5,VNAME6,VNO,VSTARTDATE,CHARGELEADER,'~',null,null,'~','~',PK_ORG,'~','~',null,'~',null "
				+ "from org_dept_v where pk_vid = '"
				+ vidPK
				+ "' and '"
				+ vidPK
				+ "' not in (select pk_vid from org_orgs_v );";
		getBaseDAO().executeUpdate(sql_create_org_v);

		String sql_create_report_v = "INSERT INTO ORG_REPORTORG_V (CODE, CREATIONTIME, CREATOR, DATAORIGINFLAG, DEF1, DEF10, DEF11, DEF12, DEF13, DEF14, DEF15, DEF16, DEF17, DEF18, DEF19, DEF2, DEF20, DEF21, DEF22, DEF23, DEF24, DEF25, DEF26, DEF27, DEF28, DEF29, DEF3, DEF30, DEF31, DEF32, DEF33, DEF34, DEF35, DEF36, DEF37, DEF38, DEF39, DEF4, DEF40, DEF5, DEF6, DEF7, DEF8, DEF9, DR, ENABLESTATE, ISLASTVERSION, MNECODE, MODIFIEDTIME, MODIFIER, NAME, NAME2, NAME3, NAME4, NAME5, NAME6, PK_GROUP, PK_ORG, PK_REPORTORG, PK_VID, SHORTNAME, SHORTNAME2, SHORTNAME3, SHORTNAME4, SHORTNAME5, SHORTNAME6, SOURCEORGTYPE, TS, VENDDATE, VNAME, VNO, VSTARTDATE) "
				+ " select oo.code || '_' || og.code code, og.creationtime, og.creator, og.dataoriginflag,  og.DEF1, og.DEF10, og.DEF11, og.DEF12, og.DEF13, og.DEF14, og.DEF15, og.DEF16, og.DEF17, og.DEF18, og.DEF19, og.DEF2, og.DEF20, '~', '~', '~', '~', '~', '~', '~', '~', '~', og.DEF3, '~', '~', '~', '~', '~', '~', '~', '~', '~', '~', og.DEF4, '~', og.DEF5, og.DEF6, og.DEF7, og.DEF8, og.DEF9, og.DR, og.ENABLESTATE, og.ISLASTVERSION, og.MNECODE, og.MODIFIEDTIME, og.MODIFIER, og.NAME, og.NAME2, og.NAME3, og.NAME4, og.NAME5, og.NAME6, og.PK_GROUP, og.PK_GROUP, og.PK_ORG, og.PK_VID, og.SHORTNAME, og.SHORTNAME2, og.SHORTNAME3, og.SHORTNAME4, og.SHORTNAME5, og.SHORTNAME6, 2 SOURCEORGTYPE, og.TS, og.VENDDATE, og.VNAME, og.VNO, og.VSTARTDATE "
				+ " from org_orgs_v og inner join org_orgs oo on og.pk_ownorg = oo.pk_org  where og.pk_vid = '"
				+ vidPK
				+ "' and og.orgtype13 = 'Y' and og.pk_vid not in (select pk_vid from org_reportorg_v)";
		getBaseDAO().executeUpdate(sql_create_report_v);

		// ���±�����֯�汾��
		String sql_report_v = "update org_reportorg_v set name=" + getNmF(name) + ", name2 = " + getNmF(name2)
				+ ",name3=" + getNmF(name3) + ",name4=" + getNmF(name4) + ",name5=" + getNmF(name5) + ",name6="
				+ getNmF(name6) + " where pk_vid = '" + vidPK + "'";
		getBaseDAO().executeUpdate(sql_report_v);
		// ������֯��
		String sql_org = "update org_orgs set code='" + deptAdjVO.getCode() + "', name=" + getNmF(name) + ", name2 = "
				+ getNmF(name2) + ",name3=" + getNmF(name3) + ",name4=" + getNmF(name4) + ",name5=" + getNmF(name5)
				+ ",name6=" + getNmF(name6) + " where pk_org = '" + deptAdjVO.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_org);
	}

	private String getNmF(String name) {
		if (name == null || name.trim().length() == 0) {
			return null;
		}
		return "'" + name + "'";
	}

	/**
	 * ���I�ޏ�,��Qԭ�����I���µ����I
	 * 
	 * @param pk_dept
	 * @param pk_dept2
	 * @throws BusinessException
	 */
	private void dataFix(String old_pk_dept, String new_pk_dept) throws BusinessException {
		if (null == old_pk_dept || null == new_pk_dept || new_pk_dept.equals(old_pk_dept)) {
			return;
		}
		// ����PK��Q�fPK
		for (int i = 0; i < NEED_FIX_TABLE_NAME.length; i++) {
			String sqlStr = "update " + NEED_FIX_TABLE_NAME[i] + " set " + NEED_FIX_TABLE_COLUMN[i] + " = '"
					+ new_pk_dept + "' " + " where " + NEED_FIX_TABLE_COLUMN[i] + " = '" + old_pk_dept + "' and dr = 0";
			getBaseDAO().executeUpdate(sqlStr);
		}
	}

	/*
	 * ��ѯ�Ƿ��Ѵ���ָ�����Tδ��Ч���{����Ո�� pk_dept ����UFBoolean True���ڣ�False������
	 * ҵ���߼�:��ѯ�Ƿ��Ѵ���ָ�����Tδ��Ч���{����Ո�μ�������Ч���ڴ��ڵ�ǰ���ڵĴ˲��ŵĵ�����. �������
	 */
	public UFBoolean isExistDeptAdj(String pk_dept, String pk_deptadj) throws BusinessException {
		String sqlStr = null;
		if (pk_deptadj != null) {
			sqlStr = "select count(*) from om_deptadj where pk_dept ='" + pk_dept + "' and effectivedate > '"
					+ new UFDate().toStdString() + "' and pk_deptadj <> '" + pk_deptadj
					+ "' and dr = 0 and isnull(iseffective,'N') = 'N'";
		} else {
			sqlStr = "select count(*) from om_deptadj where pk_dept ='" + pk_dept + "' and effectivedate > '"
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
		if (!isExistDeptAdj(vo.getPk_dept(), vo.getPk_deptadj()).booleanValue()) {
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
	public AggHRDeptVO writeBack4DeptAdd(AggHRDeptVO aggDeptVO) throws BusinessException {
		if (aggDeptVO == null || aggDeptVO.getParentVO() == null) {
			return null;
		}
		HRDeptVO deptVO = (HRDeptVO) aggDeptVO.getParentVO();
		// �O�à�B��δ����
		deptVO.setEnablestate(1);
		// �������ڴ��ڵ�ǰ���ڲſ��Ի�д
		if (null != deptVO && deptVO.getCreatedate() != null && isAfterToday(deptVO.getCreatedate())) {
			// ����PK_VID��ֵ��Ϊnull���ܴ洢
			deptVO.setPk_vid(PK_VID_FOR_DEPT_VER);
			deptVO.setCreationtime(new UFDateTime());
			deptVO.setOrgtype13(UFBoolean.TRUE);
			deptVO.setOrgtype17(UFBoolean.FALSE);
			deptVO.setDr(0);
			// ��org_dept������һ�l���n,����ʲ�N������
			getBaseDAO().insertVO(deptVO);
			// ���¹��c������ӛ�
			HRDeptAdjustVO saveVO = HRDeptVO2HRDeptAdjust(deptVO);
			saveVO.setEffectivedate(deptVO.getCreatedate());
			saveVO.setOrgtype13(deptVO.getOrgtype13());
			saveVO.setOrgtype17(deptVO.getOrgtype17());
			saveVO.setPrincipal(deptVO.getPrincipal());
			saveVO.setName2(deptVO.getName2());
			saveVO.setName3(deptVO.getName3());
			saveVO.setName4(deptVO.getName4());
			saveVO.setName5(deptVO.getName5());
			saveVO.setName6(deptVO.getName6());
			saveVO.setTel(deptVO.getTel());
			saveVO.setAddress(deptVO.getAddress());
			saveVO.setGlbdef3((String) deptVO.getAttributeValue("glbdef3"));
			saveVO.setGlbdef11((String) deptVO.getAttributeValue("glbdef11"));
			saveVO.setDept_charge((String) deptVO.getAttributeValue("dept_charge"));
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
	public AggHRDeptVO writeBack4DeptUnCancel(AggHRDeptVO aggDeptVO, UFLiteralDate effective) throws BusinessException {
		if (aggDeptVO == null || aggDeptVO.getParentVO() == null) {
			return null;
		}
		if (effective == null) {
			throw new BusinessException("Ո������Ч����!");
		}

		HRDeptVO deptVO = (HRDeptVO) this.getBaseDAO().retrieveByPK(HRDeptVO.class,
				aggDeptVO.getParentVO().getPrimaryKey());
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
			// ȡ�����N���I
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
	public List<HRDeptVO> queryOFutureDept(String pk_org, String whereSql) throws BusinessException {
		if (whereSql == null) {
			whereSql = " 1 = 1 ";
		}
		String sqlStr = " select * from org_dept where pk_org = '" + pk_org + "' and  createdate > '"
				+ (new UFDate()).toStdString() + "' and " + whereSql;

		List<HRDeptVO> result = (List<HRDeptVO>) getBaseDAO().executeQuery(sqlStr,
				new BeanListProcessor(HRDeptVO.class));
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
	public HRDeptVO HRDeptAdjust2HRDeptVO(HRDeptAdjustVO vo) throws BusinessException {
		// �Ȳ��ҵ�ǰ�Ĳ�����Ϣ
		HRDeptVO resultVO = (HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class, vo.getPk_dept());
		if (null == resultVO) {
			resultVO = new HRDeptVO();
		}
		// ���޸���Ϣ���ǵ���ǰ��Ϣ����
		if (null != vo) {
			resultVO.setPk_vid(vo.getPk_dept_v());
			resultVO.setPk_dept(vo.getPk_dept());
			resultVO.setCode(vo.getCode());
			resultVO.setName(vo.getName());
			// ssx added on 2019-05-02
			// for MultiLangText
			resultVO.setName2(vo.getName2());
			resultVO.setName3(vo.getName3());
			resultVO.setName4(vo.getName4());
			resultVO.setName5(vo.getName5());
			resultVO.setName6(vo.getName6());
			resultVO.setAttributeValue("glbdef5", vo.getName3());
			// end
			resultVO.setInnercode(vo.getInnercode());
			resultVO.setPk_fatherorg(vo.getPk_fatherorg());
			resultVO.setPk_group(vo.getPk_group());
			resultVO.setPk_org(vo.getPk_org());
			resultVO.setDepttype(vo.getDepttype());
			resultVO.setDeptlevel(vo.getDeptlevel());
			resultVO.setDeptduty(vo.getDeptduty());
			resultVO.setCreatedate(vo.getCreatedate());
			resultVO.setShortname(vo.getShortname());
			// ssx added on 2019-05-02
			// for MultiLangText
			resultVO.setShortname2(vo.getShortname2());
			resultVO.setShortname3(vo.getShortname3());
			resultVO.setShortname4(vo.getShortname4());
			resultVO.setShortname5(vo.getShortname5());
			resultVO.setShortname6(vo.getShortname6());
			// end
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
			// ���Ӹ����ܺ͹����ص� ������ 20190503 begin
			resultVO.setAttributeValue("glbdef3", vo.getGlbdef3());
			resultVO.setAttributeValue("glbdef11", vo.getGlbdef11());
			// ���Ӹ����ܺ͹����ص� ������ 20190503 end
			resultVO.setAttributeValue("dept_charge", vo.getDept_charge());
			resultVO.setCreatedate(vo.getEffectivedate());
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
	public HRDeptAdjustVO HRDeptVO2HRDeptAdjust(HRDeptVO vo) throws BusinessException {
		HRDeptAdjustVO resultVO = new HRDeptAdjustVO();
		if (null != vo) {
			resultVO.setPk_dept_v(null);
			resultVO.setPk_dept(vo.getPk_dept());
			resultVO.setCode(vo.getCode());
			resultVO.setName(vo.getName());
			resultVO.setName2(vo.getName2());
			resultVO.setName3(vo.getName3());
			resultVO.setName4(vo.getName4());
			resultVO.setName5(vo.getName5());
			resultVO.setName6(vo.getName6());
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
			resultVO.setGlbdef11((String) vo.getAttributeValue("glbdef11"));
			resultVO.setGlbdef3((String) vo.getAttributeValue("glbdef3"));
			resultVO.setDept_charge(vo.getDept_charge());
			// ��ȡ��֯�汾
			String sqlStr = "select pk_vid from org_orgs where pk_org = '" + vo.getPk_org() + "' ";
			Object pkvid = getSingleDataBySQL(sqlStr);
			if (null != pkvid) {
				resultVO.setPk_org_v(String.valueOf(pkvid));
			}
		}
		return resultVO;
	}

	/**
	 * �Զ�����--���ų��� ���У�����2,���ܽ���ɾ��,ɾ��ʱ����ɾ��������Ϣ������ ���ų�����ȡ��������ʱ�ñ�׼���� ����������ʱ�����а汾������
	 * 
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
		String sqlStr = "select count(*) from hi_stapply where  newpk_dept = '" + vo.getPk_dept()
				+ "'and  EFFECTDATE > '" + vo.getEffectivedate().toStdString() + "' ";
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

		if (vo.getIseffective() != null && !vo.getIseffective().booleanValue()) {
			// δ��Ч��
			// ��ԃ�{��ӛ���,�Ƿ��������ô˲��T�ĆΓ�
			String sqlStr = "select count(*) from hi_stapply where newpk_dept ='" + vo.getPk_dept() + "' and dr = 0";
			Integer num = getIntegerDataBySQL(sqlStr);
			if (num != null && num > 0) {
				throw new BusinessException("ԓ���T߀��δ̎����ˆT�{����Ո,Ոȡ���{����لh��!");
			}
			// �Ѿ����¼�����ʱ,ɾ������
			sqlStr = "select count(*) from om_deptadj where pk_fatherorg = '" + vo.getPk_dept() + "' and dr = 0";
			num = getIntegerDataBySQL(sqlStr);
			if (num != null && num > 0) {
				throw new BusinessException("ԓ���T�Ѵ����¼����T,�o���h��!");
			}
		} else {
			// ����Ч��
			throw new BusinessException("�Γ��ѽ������У����܄h��!");
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
	 * 
	 * @param effDate
	 * 
	 * @return
	 */
	private DeptHistoryVO buildDeptHistoryVO4Update(HRDeptVO vo, UFLiteralDate effDate) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// ��׼��λ
		deptHistoryVO.setApprovedept(null);
		// ��׼�ĺ�
		deptHistoryVO.setApprovenum(null);
		// �²�����
		MultiLangText multiLangText = new MultiLangText();
		multiLangText.setText(vo.getName());
		multiLangText.setText2(vo.getName2());
		multiLangText.setText3(vo.getName3());
		multiLangText.setText4(vo.getName4());
		multiLangText.setText5(vo.getName5());
		multiLangText.setText6(vo.getName6());
		SuperVOHelper.copyMultiLangAttribute(multiLangText, deptHistoryVO);
		// ����
		deptHistoryVO.setCode(vo.getCode());
		// ���ż���
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// ������
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// ��Ч����--Ĭ�J�ǽ���
		// ssx modified on 2019-05-02
		// ��춿����M��ǰ�����ڵĆΓ���룬����Ĭ�J�����ǽ��죬���ǰl��׃��������
		deptHistoryVO.setEffectdate(effDate);
		// --end
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
	 * 
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

	@Override
	public void executeDeptVersion(AggHRDeptVO saveVO, UFLiteralDate date) throws BusinessException {
		if (null == saveVO) {
			return;
		}
		if (null == saveVO.getParentVO()) {
			throw new BusinessException("���T���n���ܞ��!");
		}

		HRDeptVO deptVO = (HRDeptVO) saveVO.getParentVO();
		deptVO.setName(deptVO.getName() == null ? null : deptVO.getName().replaceAll("'", ""));
		deptVO.setName2(deptVO.getName2() == null ? null : deptVO.getName2().replaceAll("'", ""));
		deptVO.setName3(deptVO.getName3() == null ? null : deptVO.getName3().replaceAll("'", ""));
		// �@ȡ��ǰ�Ĳ��T��Ϣ
		HRDeptVO curVO = (HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class, deptVO.getPk_dept());

		checkDeptCanceled(curVO, date);

		String newPkdeptV = curVO.getPk_vid();

		if (deptVO.getPk_vid() == null || deptVO.getPk_vid().equals("~") || deptVO.getPk_vid().equals("null")) {
			deptVO.setPk_vid(curVO.getPk_vid());
		}
		// �O�Æ���
		deptVO.setEnablestate(2);
		deptVO.setIslastversion(new UFBoolean(true));
		saveVO.setParentVO(deptVO);
		// ��������־
		if (curVO != null && curVO.getHrcanceled() != null && deptVO.getHrcanceled() != null) {
			// ԭ���ǳ���״̬,������Ϊ�ǳ���״̬,��ȡ�����N
			if (curVO.getHrcanceled().booleanValue() && !deptVO.getHrcanceled().booleanValue()) {
				DeptHistoryVO historyVO = buildDeptHistoryVO4ImportUnCancel(deptVO, date);
				// �����N
				AggHRDeptVO[] uncanceledDepts = getDeptManageService().uncancel(saveVO, historyVO, false, false, true);
				// /���Ű汾���������� ������ 20190502 begin
				if (uncanceledDepts != null && uncanceledDepts.length > 0) {
					saveVO = uncanceledDepts[0];
				}
				HRDeptVO parentVO = (HRDeptVO) saveVO.getParentVO();
				IOrgVersionManageService orgManageService = NCLocator.getInstance().lookup(
						IOrgVersionManageService.class);
				String vno = queryNextVersionNO("org_dept_v", "pk_dept", deptVO.getPk_dept(),
						String.valueOf(date.getYear()));
				DeptVO pvo = (DeptVO) getBaseDAO().retrieveByPK(DeptVO.class, parentVO.getPk_dept());
				String name = pvo.getName();
				String name2 = pvo.getName2();
				String name3 = pvo.getName3();
				/*
				 * ���ñ�׼��Ʒ�İ汾�����񣬵��Ǳ�׼��ƷҪ��汾��ʱ�����޸����ƣ�
				 * �����ǵ�������֧�ֲ��޸����Ƶģ�����ķ������Ȱ������޸ģ��ٰ�����update����
				 */
				pvo.setName(pvo.getName() + "@@@@TWNC");
				if (pvo.getName2() != null)
					pvo.setName2(pvo.getName2() + "@@@@TWNC");
				if (pvo.getName3() != null)
					pvo.setName3(pvo.getName3() + "@@@@TWNC");
				// ���ñ�׼��Ʒ�ı�׼�汾������
				DeptVO hrDptNewVerVO = (DeptVO) orgManageService.createNewVersionVO("nc.vo.org.DeptVO", pvo, "�汾����"
						+ date.getYear() + vno, date.getYear() + vno, new UFDate(date.toString() + " 00:00:00"),
						new UFDate(date.getDateBefore(1).toString() + " 23:59:59"));
				if (hrDptNewVerVO == null) {
					return;
				}
				// �������ٸ�update����,ͬʱ�Ѳ��ű�Ͳ��Ű汾��ĸ����ˡ������ܡ����ڵص������
				updateOrgName(name, name2, name3, hrDptNewVerVO);
				/*
				 * AggHRDeptVO[] newVOs =
				 * getDeptManageService().createDeptVersion(uncanceledDepts, new
				 * UFDate(date.toDate()));
				 */
				// ����HR���ű�
				AggHRDeptVO newVO = new AggHRDeptVO();
				HRDeptVO hrdeptvo = (HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class, hrDptNewVerVO.getPk_dept());
				hrdeptvo.setFatherDeptChanged(parentVO.isFatherDeptChanged());
				hrdeptvo.setApprovedept(parentVO.getApprovedept());
				hrdeptvo.setApprovenum(parentVO.getApprovenum());
				hrdeptvo.setDeptduty(parentVO.getDeptduty());
				hrdeptvo.setManagescope(parentVO.isManagescope());
				newVO.setParentVO(hrdeptvo);
				getDeptManageService().update(newVO, false);
				newPkdeptV = ((HRDeptVO) newVO.getParentVO()).getPk_vid();
				// ��д������¼
				String sqlStr = "update  hi_psnjob set pk_dept_v = '" + newPkdeptV + "'  where begindate <= '"
						+ date.toStdString() + "' and isnull(enddate,'9999-12-31') > '" + date.toStdString()
						+ "' and  pk_dept = '" + deptVO.getPk_dept() + "'";
				getBaseDAO().executeUpdate(sqlStr);
				return;
			}
		}
		// �޸�߉݋

		DeptHistoryVO historyVO = buildDeptHistoryVO4ImportUpdate(deptVO, date);
		// ���Ű汾���������� ������ 20190502 begin
		HRDeptVO parentVO = (HRDeptVO) saveVO.getParentVO();
		IOrgVersionManageService orgManageService = NCLocator.getInstance().lookup(IOrgVersionManageService.class);
		String vno = queryNextVersionNO("org_dept_v", "pk_dept", deptVO.getPk_dept(), String.valueOf(date.getYear()));
		DeptVO pvo = (DeptVO) new BaseDAO().retrieveByPK(DeptVO.class, parentVO.getPk_dept());
		String name = pvo.getName();
		String name2 = pvo.getName2();
		String name3 = pvo.getName3();
		/*
		 * ���ñ�׼��Ʒ�İ汾�����񣬵��Ǳ�׼��ƷҪ��汾��ʱ�����޸����ƣ�
		 * �����ǵ�������֧�ֲ��޸����Ƶģ�����ķ������Ȱ������޸ģ��ٰ�����update����
		 */
		pvo.setName(pvo.getName() + "@@@@TWNC");
		if (pvo.getName2() != null)
			pvo.setName2(pvo.getName2() + "@@@@TWNC");
		if (pvo.getName3() != null)
			pvo.setName3(pvo.getName3() + "@@@@TWNC");
		// ���ñ�׼��Ʒ�ı�׼�汾������
		DeptVO hrDptNewVerVO = (DeptVO) orgManageService.createNewVersionVO("nc.vo.org.DeptVO", pvo,
				"�汾����" + date.getYear() + vno, date.getYear() + vno, new UFDate(date.toString() + " 00:00:00"),
				new UFDate(date.getDateBefore(1).toString() + " 23:59:59"));
		if (hrDptNewVerVO == null) {
			return;
		}
		// �������ٸ�update����,ͬʱ�Ѳ��ű�Ͳ��Ű汾��ĸ����ˡ������ܡ����ڵص������
		updateOrgName(name, name2, name3, hrDptNewVerVO);
		// ����HR���ű�
		AggHRDeptVO newVO = new AggHRDeptVO();
		HRDeptVO hrdeptvo = (HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class, hrDptNewVerVO.getPk_dept());
		hrdeptvo.setFatherDeptChanged(parentVO.isFatherDeptChanged());
		hrdeptvo.setApprovedept(parentVO.getApprovedept());
		hrdeptvo.setApprovenum(parentVO.getApprovenum());
		hrdeptvo.setDeptduty(parentVO.getDeptduty());
		hrdeptvo.setManagescope(parentVO.isManagescope());
		newVO.setParentVO(hrdeptvo);
		getDeptManageService().update(newVO, false);

		// AggHRDeptVO newVO = saveVO;
		// ������T���a׃�����T���Q׃��,�t�����ˆT��ӛ䛣�
		int renameAndPrincipalChangeFlag = 0;
		if (deptVO.getCode() != null && curVO.getName() != null) {
			if (!deptVO.getCode().equals(curVO.getCode()) || !deptVO.getName().equals(curVO.getName())) {
				// ׃�������Q
				renameAndPrincipalChangeFlag = 1;
			}
		}
		if ((deptVO.getPrincipal() == null && curVO.getCode() != null)
				|| (deptVO.getPrincipal() != null && curVO.getPrincipal() == null)
				|| (deptVO.getPrincipal() != null && curVO.getPrincipal() != null && !deptVO.getPrincipal().equals(
						curVO.getPrincipal()))) {
			// ׃����ؓ؟��
			if (0 == renameAndPrincipalChangeFlag) {
				historyVO.setChangetype(DeptChangeType.CHANGEPRINCIPAL);
				renameAndPrincipalChangeFlag = 2;
			} else {
				renameAndPrincipalChangeFlag = 3;
			}
		}
		if (renameAndPrincipalChangeFlag != 0) {
			// getDeptManageService().renameAndPrincipalChange(newVO, historyVO,
			// true, renameAndPrincipalChangeFlag);
			// ������ʷ����
			getBaseDAO().insertVO(historyVO);
			newPkdeptV = ((HRDeptVO) newVO.getParentVO()).getPk_vid();
		}

		// ssx added on 2019-10-25
		// ���Tؓ؟��׃���r��ͬ��׃���¼����T���ϼ�����
		if (renameAndPrincipalChangeFlag == 2) {
			updateChildCharger(deptVO.getPk_dept(), deptVO.getPrincipal());
		}
		// end

		// ��д������¼��
		String sqlStr = "update  hi_psnjob set pk_dept_v = '" + newPkdeptV + "'  where begindate <= '"
				+ date.toStdString() + "' and isnull(enddate,'9999-12-31') > '" + date.toStdString()
				+ "' and  pk_dept = '" + deptVO.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sqlStr);

	}

	private void checkDeptCanceled(HRDeptVO deptVO, UFLiteralDate date) throws BusinessException {
		if (UFBoolean.TRUE.equals(deptVO.getHrcanceled())) {
			throw new BusinessException("�ѳ��N���T�o���M�а汾���I��");
		}
	}

	private void updateOrgName(String name, String name2, String name3, DeptVO vo) throws DAOException {
		// ���²��ű�,���¸����ˡ������ܡ����ڵص�
		String sql_dept = "update org_dept set name = " + getNmF(name) + ", name2 = " + getNmF(name2) + ",name3="
				+ getNmF(name3) + " where pk_dept = '" + vo.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_dept);
		// ���²��Ű汾��,���¸����ˡ������ܡ����ڵص�
		String sql_dept_v = "update org_dept_v set name = " + getNmF(name) + ", name2 = " + getNmF(name2) + ",name3="
				+ getNmF(name3) + " where pk_vid = '" + vo.getPk_vid() + "'";
		getBaseDAO().executeUpdate(sql_dept_v);
		// ���±�����֯��
		String sql_report = "update org_reportorg set name = " + getNmF(name) + ", name2 = " + getNmF(name2)
				+ ",name3=" + getNmF(name3) + " where pk_reportorg = '" + vo.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_report);
		// ���±�����֯�汾��
		String sql_report_v = "update org_reportorg_v set name = " + getNmF(name) + ", name2 = " + getNmF(name2)
				+ ",name3=" + getNmF(name3) + " where pk_vid = '" + vo.getPk_vid() + "'";
		getBaseDAO().executeUpdate(sql_report_v);
		// ������֯��
		String sql_org = "update org_orgs set name = " + getNmF(name) + ", name2 = " + getNmF(name2) + ",name3="
				+ getNmF(name3) + " where pk_org = '" + vo.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_org);

	}

	private DeptHistoryVO buildDeptHistoryVO4ImportUpdate(HRDeptVO vo, UFLiteralDate date) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// ��׼��λ
		deptHistoryVO.setApprovedept(null);
		// ��׼�ĺ�
		deptHistoryVO.setApprovenum(null);
		// �²�����
		MultiLangText multiLangText = new MultiLangText();
		multiLangText.setText(vo.getName());
		multiLangText.setText2(vo.getName2());
		multiLangText.setText3(vo.getName3());
		multiLangText.setText4(vo.getName4());
		multiLangText.setText5(vo.getName5());
		multiLangText.setText6(vo.getName6());
		SuperVOHelper.copyMultiLangAttribute(multiLangText, deptHistoryVO);
		// ����
		deptHistoryVO.setCode(vo.getCode());
		// ���ż���
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// ������
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// ��Ч����--Ĭ�J�ǽ���
		deptHistoryVO.setEffectdate(date);
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

	private DeptHistoryVO buildDeptHistoryVO4ImportUnCancel(HRDeptVO vo, UFLiteralDate date) {
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
		deptHistoryVO.setEffectdate(date);
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

	private char[] ran = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * �������ĸ��ַ����ַ���
	 * 
	 * @return
	 */
	private String get4RandomCode() {
		Random rom = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			char r1 = ran[rom.nextInt(35)];
			sb.append(r1);
		}
		return sb.toString();
	}

	/**
	 * ���µ�ǰ���ŵ�innercode
	 * 
	 * @param innercode
	 *            �ϼ����ŵ�innercode
	 * @param pk_dept
	 *            ���µĲ���
	 * @return
	 * @throws DAOException
	 */
	private String updateCode(String innercode, String pk_dept) throws DAOException {
		StringBuffer sql = new StringBuffer();
		String s = get4RandomCode();
		// ���²��ű�
		sql.append("update org_dept set innercode = '").append(innercode).append(s).append("' where pk_dept = '")
				.append(pk_dept).append("'");
		this.getBaseDAO().executeUpdate(sql.toString());
		// ���²��Ű汾��
		sql = new StringBuffer();
		sql.append("update org_dept_v v set v.innercode = '").append(innercode).append(s)
				.append("' where v.pk_vid =(select d.pk_vid from org_dept d where d.pk_dept ='").append(pk_dept)
				.append("' )");
		this.getBaseDAO().executeUpdate(sql.toString());
		return innercode + s;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateDeptInnercode(String innercode, String pk_fatherorg) throws DAOException {
		String sql = "select innercode,pk_dept,pk_fatherorg from org_dept where dr=0 and pk_fatherorg = '"
				+ pk_fatherorg + "'";
		List<Map<String, String>> list = (List<Map<String, String>>) this.getBaseDAO().executeQuery(sql,
				new MapListProcessor());
		if (list.size() == 0) {
			return;
		}
		for (Map<String, String> map : list) {
			String s = updateCode(innercode, map.get("pk_dept"));
			updateDeptInnercode(s, map.get("pk_dept"));
		}
	}
}
