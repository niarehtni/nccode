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

	/** 部门DAO */
	// private DeptDao deptDao = null;

	/** 部门查询接口 **/
	private IDeptQueryService deptQueryService = null;

	/** 部門業務接口 **/
	private IDeptManageService deptManageService = null;

	/** baseDao **/
	private BaseDAO baseDAO = null;

	/**
	 * 为新版本部门
	 */
	private static final String PK_VID_FOR_DEPT_VER = "VIRTUAL_PK_DEPT_V";

	// 進行部門版本化后,需要進行修復pk_dept的表名(這些表的pk_dept和pk_dept_v同在,hi_stapply除外)
	private static String[] NEED_FIX_TABLE_NAME = { "hi_psnjob", "om_deptadj", "tbm_leaveplan", "hi_stapply" };
	// 部門pk_dept的字段名
	private static String[] NEED_FIX_TABLE_COLUMN = { "pk_dept", "pk_dept", "pk_dept", "newpk_dept" };

	/** 部门QService **/
	private IDeptQueryService getDeptQueryService() {
		if (deptQueryService == null) {
			deptQueryService = NCLocator.getInstance().lookup(IDeptQueryService.class);
		}
		return deptQueryService;
	}

	/** 部门MService **/
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
	 * 根据部门查询当前部门的版本PK
	 * 
	 * @param pk_dept
	 * @return 当前部门的版本PK 业务逻辑:查询当前部门的版本PK
	 */
	public String queryLastDeptByPk(String pk_dept) {
		String pk_dept_v = null;
		try {
			// ssx MOD 取當前日期生效部門版本 改為 取主檔上指定的最新版本
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
				// 獲取當前的部門信息
				HRDeptVO curVO = (HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class, deptVO.getPk_dept());
				if (!(deptAdjVO.getEnablestate() == 1 && !PK_VID_FOR_DEPT_VER.equals(deptAdjVO.getPk_dept_v()))) {
					checkDeptCanceled(curVO, date);
				}

				// MOD (PM25602：增加部門當前版本開始日期晚於等於生效日期時，不進行版本化)
				// added on 2019-03-28 by ssx
				if (curVO.getVstartdate() != null) {
					if (curVO.getVstartdate().toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).isSameDate(date)
							|| curVO.getVstartdate().toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE)
									.after(deptAdjVO.getEffectivedate())) {
						errMsg.append("部門 [" + curVO.getCode() + "] 已存在早於申請單生效日期的版本，不能進行版本化處理。");
						continue;
					}
				}

				if (curVO.getPk_vid() != null && curVO.getPk_vid().trim().equals(PK_VID_FOR_DEPT_VER)
						&& curVO.getCreatedate() != null && curVO.getCreatedate().after(new UFLiteralDate())) {
					errMsg.append("部門 [" + curVO.getCode() + "] 未達生效日期，不能進行版本化處理。");
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
				// 設置啟用
				deptVO.setEnablestate(2);
				deptVO.setIslastversion(new UFBoolean(true));
				deptAggVO.setParentVO(deptVO);

				// 处理撤销标志
				if (curVO != null && curVO.getHrcanceled() != null && deptAdjVO.getHrcanceled() != null) {
					// 原来是撤销状态,且现在为非撤销状态,先取消撤銷
					if (curVO.getHrcanceled().booleanValue() && !deptAdjVO.getHrcanceled().booleanValue()) {
						DeptHistoryVO historyVO = buildDeptHistoryVO4UnCancel(deptVO);
						// 反撤銷
						AggHRDeptVO[] uncanceledDepts = getDeptManageService().uncancel(deptAggVO, historyVO, false,
								false, true);
						// 部门版本化操作完善 王永文 20190502 begin
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
						 * 调用标准产品的版本化服务，但是标准产品要求版本化时必须修改名称，
						 * 而我们的需求是支持不修改名称的，解决的方案是先把名称修改，再把名称update回来
						 */
						pvo.setName(pvo.getName() + "@@@@TWNC");
						if (pvo.getName2() != null)
							pvo.setName2(pvo.getName2() + "@@@@TWNC");
						if (pvo.getName3() != null)
							pvo.setName3(pvo.getName3() + "@@@@TWNC");
						// 调用标准产品的标准版本化操作
						DeptVO hrDptNewVerVO = (DeptVO) orgManageService.createNewVersionVO("nc.vo.org.DeptVO", pvo,
								"版本更新" + date.getYear() + vno, date.getYear() + vno, new UFDate(date.toString()
										+ " 00:00:00"), new UFDate(date.getDateBefore(1).toString() + " 23:59:59"));
						if (hrDptNewVerVO == null) {
							continue;
						}
						// 把名称再给update回来,同时把部门表和部门版本表的负责人、副主管、所在地点更新了
						updateModifiedInfo(deptAdjVO, hrDptNewVerVO.getPk_vid());
						/*
						 * AggHRDeptVO[] newVOs =
						 * getDeptManageService().createDeptVersion
						 * (uncanceledDepts, new UFDate(date.toDate()));
						 */
						// HR部门更新
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
						// 部门版本化操作完善 王永文 20190502 end
						// 回写标志:
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
					// 如果是新增,那麼先刪除主表上的檔案信息
					getBaseDAO().deleteVO(curVO);
					if (StringUtils.isEmpty((String) deptAggVO.getParentVO().getAttributeValue("innercode"))) {
						deptAggVO.getParentVO().setAttributeValue(
								"innercode",
								getInnerCodeByFatherOrg((String) deptAggVO.getParentVO().getAttributeValue(
										"pk_fatherorg")));
					}
					// 執行新增標準平台新增邏輯
					AggHRDeptVO rtnVO = getDeptManageService().insert(deptAggVO);
					String pk_dept = null;
					if (rtnVO.getParentVO() != null) {
						pk_dept = rtnVO.getParentVO().getPrimaryKey();
					}
					// 替換所有原先引用的值
					dataFix(oldPkDept, pk_dept);
				} else {
					DeptHistoryVO historyVO = buildDeptHistoryVO4Update(deptVO, date);
					// 部门版本化操作完善 王永文 20190502 begin
					HRDeptVO parentVO = (HRDeptVO) deptAggVO.getParentVO();
					IOrgVersionManageService orgManageService = NCLocator.getInstance().lookup(
							IOrgVersionManageService.class);
					String vno = queryNextVersionNO("org_dept_v", "pk_dept", deptVO.getPk_dept(),
							String.valueOf(date.getYear()));
					DeptVO pvo = (DeptVO) new BaseDAO().retrieveByPK(DeptVO.class, parentVO.getPk_dept());
					/*
					 * 调用标准产品的版本化服务，但是标准产品要求版本化时必须修改名称，
					 * 而我们的需求是支持不修改名称的，解决的方案是先把名称修改，再把名称update回来
					 */
					pvo.setName(pvo.getName() + "@@@@TWNC");
					if (pvo.getName2() != null)
						pvo.setName2(pvo.getName2() + "@@@@TWNC");
					if (pvo.getName3() != null)
						pvo.setName3(pvo.getName3() + "@@@@TWNC");
					// 调用标准产品的标准版本化操作
					DeptVO hrDptNewVerVO = (DeptVO) orgManageService.createNewVersionVO("nc.vo.org.DeptVO", pvo, "版本更新"
							+ date.getYear() + vno, date.getYear() + vno, new UFDate(date.toString() + " 00:00:00"),
							new UFDateTime(date.getDateBefore(1).toString() + " 23:59:59").getDate());
					if (hrDptNewVerVO == null) {
						continue;
					}
					// 把名称再给update回来,同时把部门表和部门版本表的负责人、副主管、所在地点更新了
					updateModifiedInfo(deptAdjVO, hrDptNewVerVO.getPk_vid());
					AggHRDeptVO newVO = new AggHRDeptVO();
					parentVO = (HRDeptVO) new BaseDAO().retrieveByPK(HRDeptVO.class, hrDptNewVerVO.getPk_dept());
					newVO.setParentVO(parentVO);
					newPkdeptV = ((HRDeptVO) newVO.getParentVO()).getPk_vid();
					// 插入部门变更历史数据
					getBaseDAO().insertVO(historyVO);
					// 如果部門代碼變更或部門名稱變更,則新增人員任職記錄，
					int renameAndPrincipalChangeFlag = 0;
					if (deptAdjVO.getCode() != null && deptAdjVO.getName() != null) {
						if (!deptAdjVO.getCode().equals(curVO.getCode())
								|| !deptAdjVO.getName().equals(curVO.getName())) {
							// 變更了名稱
							renameAndPrincipalChangeFlag = 1;
						}
					}

					boolean principleChanged = false;
					if ((deptAdjVO.getPrincipal() == null && curVO.getCode() != null)
							|| (deptAdjVO.getPrincipal() != null && curVO.getPrincipal() == null)
							|| (deptAdjVO.getPrincipal() != null && curVO.getPrincipal() != null && !deptAdjVO
									.getPrincipal().equals(curVO.getPrincipal()))) {
						principleChanged = true;

						// 變更了負責人
						if (0 == renameAndPrincipalChangeFlag) {
							historyVO.setChangetype(DeptChangeType.CHANGEPRINCIPAL);
							renameAndPrincipalChangeFlag = 2;
						} else {
							renameAndPrincipalChangeFlag = 3;
						}
					}
					// 判断是不是修改了上级部门，如果修改了，要修改innercode wangywt 20190711
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
					// 更新dept_charge wangywt 20190711
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
					// 部门版本化操作完善 王永文 20190502 end

					// ssx added on 2019-10-25
					// 部門負責人變更時，同步變更下級部門的上級主管
					if (principleChanged) {
						updateChildCharger(deptAdjVO.getPk_dept(), deptAdjVO.getPrincipal());
						updateMemberManager(deptAdjVO.getPk_dept(), deptAdjVO.getPrincipal());
					}
					// end
				}
				// 回写标志:
				sqlStr = "update om_deptadj set iseffective = 'Y' where pk_deptadj = '" + deptAdjVO.getPk_deptadj()
						+ "'";
				getBaseDAO().executeUpdate(sqlStr);
				sqlStr = "update  hi_psnjob set pk_dept_v = '" + newPkdeptV + "'  where begindate <= '"
						+ date.toStdString() + "' and isnull(enddate,'9999-12-31') > '" + date.toStdString()
						+ "' and  pk_dept = '" + oldPkDept + "'";
				getBaseDAO().executeUpdate(sqlStr);

			} catch (Exception e) {
				errMsg.append("部門 [" + deptAdjVO.getCode() + "] 版本化發生錯誤: " + e.getMessage() + "\r\n");
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
	// 部門負責人變更時，同步變更下級部門的上級主管
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
	 * 执行部门版本化（後台任務：先新增再修改）
	 * 
	 * @param date
	 * @throws BusinessException
	 *             业务逻辑:执行生效日期为此日期的所有部门版本化单据,
	 *             1.将此日期的单据vo上存储的字段信息,存储到org_dept,org_dept_v
	 *             ,org_orgs,org_orgs_v,org_reportorg.并且更新关联的十几张表(参考新增和修改逻辑).
	 *             2.部门编码和部门名称更改时, 新增人員任職記錄( 已經結束的任職記錄不更新)((参考新增和修改逻辑))
	 *             3.人员履历记录都要增加
	 *             (参考新增和修改逻辑)4.islastversion打上最新标志,该部门其它记录改为false(参考新增和修改逻辑)
	 *             4.iseffective打上执行标记.iseffective为false的不执行. (5.其它参考新增和修改逻辑)
	 *             6.修改有動撤銷標記時,調用撤銷/取消撤銷邏輯
	 */
	@SuppressWarnings("unchecked")
	public String executeDeptVersion(UFLiteralDate date) throws BusinessException {

		// 查询该生效日期的所有单据
		// MOD(PM25602：修改為早於等於當前日期的所有未執行單據)
		// added on 2019-03-28 by ssx
		// mod tank 2020年3月5日 02:43:48 根据时间排序,处理依赖关系
		String sqlStr = "select * from om_deptadj where effectivedate <= '" + date.toStdString()
				+ "' and isnull(iseffective, 'N') = 'N' and dr = 0 order by effectivedate,om_deptadj.creationtime";
		// MOD END
		List<HRDeptAdjustVO> needExecuteVOs = (List<HRDeptAdjustVO>) getBaseDAO().executeQuery(sqlStr,
				new BeanListProcessor(HRDeptAdjustVO.class));
		HRDeptAdjustVO[] sortedVOs = sortByRely(needExecuteVOs);
		return executeDeptVersion(sortedVOs, date);
	}

	/**
	 * 排序,保證本次執行的單據中,父部門比子部門先執行版本化
	 * 
	 * @param needExecuteVOs
	 */
	private HRDeptAdjustVO[] sortByRely(List<HRDeptAdjustVO> needExecuteVOs) {
		if (needExecuteVOs == null || needExecuteVOs.size() < 0) {
			return new HRDeptAdjustVO[0];
		}
		HRDeptAdjustVO[] rtnVOs = needExecuteVOs.toArray(new HRDeptAdjustVO[0]);
		// 建立索引<主鍵,index>
		Map<String, Integer> pkIndexMap = new HashMap<>();
		for (int index = 0; index < rtnVOs.length; index++) {
			pkIndexMap.put(rtnVOs[index].getPk_dept(), index);
		}
		// 循環掃描,將父部門排在子部門的前面
		for (int index = 0; index < rtnVOs.length; index++) {
			String pk_father = rtnVOs[index].getPk_fatherorg();
			if (pk_father != null) {
				// 查詢父部門是否也在本次版本化之中
				Integer fatherIndex = pkIndexMap.get(pk_father);
				if (fatherIndex == null) {
					continue;
				}
				// 如果父部門在子部門後面,則交換
				if (fatherIndex > index) {
					// 交換
					HRDeptAdjustVO temp = rtnVOs[fatherIndex];
					rtnVOs[fatherIndex] = rtnVOs[index];
					rtnVOs[index] = temp;
					// index更新
					pkIndexMap.put(rtnVOs[fatherIndex].getPk_dept(), fatherIndex);
					pkIndexMap.put(rtnVOs[index].getPk_dept(), index);
					// index回退一步
					index -= 1;
				}
			}
		}
		return rtnVOs;
	}

	/**
	 * 更新组织名称、报表组织名称、部门名称，及对应的版本名称 增加更新负责人、副主管、所在地点
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
		// 更新部门表,更新负责人(principal),副主管(glbdef3),所在地点(glbdef11),創建日期(createdate),部門級別(deptlevel),部門主管(dept_charge)
		String sql_dept = "update org_dept set code='" + deptAdjVO.getCode() + "',name = " + getNmF(name)
				+ ", name2 = " + getNmF(name2) + ",name3=" + getNmF(name3) + ",name4=" + getNmF(name4) + ",name5="
				+ getNmF(name5) + ",name6=" + getNmF(name6) + ",DEPTLEVEL='" + deptAdjVO.getDeptlevel()
				+ "', PRINCIPAL = '" + deptAdjVO.getPrincipal() + "',GLBDEF11 = '" + deptAdjVO.getGlbdef11()
				+ "', GLBDEF3 = '" + deptAdjVO.getGlbdef3() + "',GLBDEF5=" + getNmF(name3) + ", createdate='"
				+ deptAdjVO.getEffectivedate() + "', dept_charge = '" + deptAdjVO.getDept_charge()
				+ "' where pk_dept = '" + deptAdjVO.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_dept);
		// 更新部门版本表,更新负责人(principal),副主管(glbdef3),所在地点(glbdef11),創建日期(createdate),部門級別(deptlevel),部門主管(dept_charge)
		String sql_dept_v = "update org_dept_v set code='" + deptAdjVO.getCode() + "', name=" + getNmF(name)
				+ ", name2 = " + getNmF(name2) + ",name3=" + getNmF(name3) + ",name4=" + getNmF(name4) + ",name5="
				+ getNmF(name5) + ",name6=" + getNmF(name6) + ",DEPTLEVEL='" + deptAdjVO.getDeptlevel()
				+ "',PRINCIPAL = '" + deptAdjVO.getPrincipal() + "',GLBDEF11 = '" + deptAdjVO.getGlbdef11()
				+ "', GLBDEF3 = '" + deptAdjVO.getGlbdef3() + "',GLBDEF5=" + getNmF(name3) + ",createdate='"
				+ deptAdjVO.getEffectivedate() + "',dept_charge = '" + deptAdjVO.getDept_charge()
				+ "' where pk_vid = '" + vidPK + "'";
		getBaseDAO().executeUpdate(sql_dept_v);
		// 更新报表组织表
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

		// 更新报表组织版本表
		String sql_report_v = "update org_reportorg_v set name=" + getNmF(name) + ", name2 = " + getNmF(name2)
				+ ",name3=" + getNmF(name3) + ",name4=" + getNmF(name4) + ",name5=" + getNmF(name5) + ",name6="
				+ getNmF(name6) + " where pk_vid = '" + vidPK + "'";
		getBaseDAO().executeUpdate(sql_report_v);
		// 更新组织表
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
	 * 主鍵修復,替換原有主鍵到新的主鍵
	 * 
	 * @param pk_dept
	 * @param pk_dept2
	 * @throws BusinessException
	 */
	private void dataFix(String old_pk_dept, String new_pk_dept) throws BusinessException {
		if (null == old_pk_dept || null == new_pk_dept || new_pk_dept.equals(old_pk_dept)) {
			return;
		}
		// 將新PK替換舊PK
		for (int i = 0; i < NEED_FIX_TABLE_NAME.length; i++) {
			String sqlStr = "update " + NEED_FIX_TABLE_NAME[i] + " set " + NEED_FIX_TABLE_COLUMN[i] + " = '"
					+ new_pk_dept + "' " + " where " + NEED_FIX_TABLE_COLUMN[i] + " = '" + old_pk_dept + "' and dr = 0";
			getBaseDAO().executeUpdate(sqlStr);
		}
	}

	/*
	 * 查询是否已存在指定部門未生效的調整申請單 pk_dept 返回UFBoolean True存在，False不存在
	 * 业务逻辑:查询是否已存在指定部門未生效的調整申請單即存在生效日期大于当前日期的此部门的调整单. 本身除外
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
	 * 单据校验服務1--部门单据唯一性(nc.impl.pubapp.pattern.rule.IRule<BatchOperateVO>)
	 * 
	 * @param vo
	 * @throws BusinessException
	 *             业务逻辑: 1. 同一部門只能有一張生效日期大于当前日期單據(后台服务)
	 */
	public UFBoolean validateDept(HRDeptAdjustVO vo) throws BusinessException {
		if (null == vo) {
			throw new BusinessException("無效記錄!");
		}
		if (null == vo.getPk_dept()) {
			throw new BusinessException("部門主鍵不得為空!");
		}
		if (null == vo.getEffectivedate()) {
			throw new BusinessException("生效日期不得為空");
		}
		if (!isExistDeptAdj(vo.getPk_dept(), vo.getPk_deptadj()).booleanValue()) {
			throw new BusinessException("該部門已經存在未到生效日期的申請單");
		}
		return new UFBoolean(true);
	}

	/**
	 * 部门信息新增回写
	 * 
	 * @param deptVO
	 * @throws BusinessException
	 *             业务逻辑:部门新增时,如果成立日期大于当前日期, 那么回写一笔单据到本节点,
	 *             将deptVO赋值到HRDeptAdjustVO上的字段,申请人填写当前用户,
	 *             申请日期填写当前日期,生效日期为部门的成立日期,调整部门为VO上的部门.
	 *             如果该部门PK而且生效日期已经存在,那么不能在进行回写,同时在保存部门的时候抛出异常.
	 */
	public AggHRDeptVO writeBack4DeptAdd(AggHRDeptVO aggDeptVO) throws BusinessException {
		if (aggDeptVO == null || aggDeptVO.getParentVO() == null) {
			return null;
		}
		HRDeptVO deptVO = (HRDeptVO) aggDeptVO.getParentVO();
		// 設置狀態為未啟用
		deptVO.setEnablestate(1);
		// 处理日期大于当前日期才可以回写
		if (null != deptVO && deptVO.getCreatedate() != null && isAfterToday(deptVO.getCreatedate())) {
			// 设置PK_VID的值不为null才能存储
			deptVO.setPk_vid(PK_VID_FOR_DEPT_VER);
			deptVO.setCreationtime(new UFDateTime());
			deptVO.setOrgtype13(UFBoolean.TRUE);
			deptVO.setOrgtype17(UFBoolean.FALSE);
			deptVO.setDr(0);
			// 在org_dept上新增一條主檔,其他什麼都不幹
			getBaseDAO().insertVO(deptVO);
			// 在新節點上新增記錄
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
	 * 部门信息新增回写
	 * 
	 * @param deptVO
	 * @throws BusinessException
	 *             业务逻辑:部门新增时,如果成立日期大于当前日期, 那么回写一笔单据到本节点,
	 *             将deptVO赋值到HRDeptAdjustVO上的字段,申请人填写当前用户,
	 *             申请日期填写当前日期,生效日期为部门的成立日期,调整部门为VO上的部门.
	 *             如果该部门PK而且生效日期已经存在,那么不能在进行回写,同时在保存部门的时候抛出异常.
	 */
	public AggHRDeptVO writeBack4DeptUnCancel(AggHRDeptVO aggDeptVO, UFLiteralDate effective) throws BusinessException {
		if (aggDeptVO == null || aggDeptVO.getParentVO() == null) {
			return null;
		}
		if (effective == null) {
			throw new BusinessException("請填入生效日期!");
		}

		HRDeptVO deptVO = (HRDeptVO) this.getBaseDAO().retrieveByPK(HRDeptVO.class,
				aggDeptVO.getParentVO().getPrimaryKey());
		// 設置狀態為未啟用
		deptVO.setEnablestate(1);
		// 处理日期大于当前日期才可以回写
		if (effective != null && isAfterToday(effective)) {
			// 在新節點上新增記錄
			HRDeptAdjustVO saveVO = HRDeptVO2HRDeptAdjust(deptVO);
			saveVO.setEffectivedate(effective);
			saveVO.setBilldate(new UFDate());
			saveVO.setDr(0);
			saveVO.setDisplayorder(deptVO.getDisplayorder());
			saveVO.setIseffective(new UFBoolean(false));
			saveVO.setCreationtime(new UFDateTime());
			saveVO.setAdj_code(String.valueOf(System.currentTimeMillis()));
			saveVO.setPk_dept_v(deptVO.getPk_vid());
			// 取消撤銷標誌
			saveVO.setHrcanceled(UFBoolean.FALSE);
			validateDept(saveVO);
			getBaseDAO().insertVO(saveVO);
		}
		aggDeptVO.setParentVO(deptVO);
		return aggDeptVO;
	}

	/**
	 * pk_org 人力资源组织PK
	 * 
	 * @param pk_org
	 * @returnList<HRDeptVO>
	 * @throws BusinessException
	 * 
	 *             在部门信息节点,需要过滤出还未生效的部门,在此节点查询出此人力资源组织下,
	 *             所有生效日期在当前日期之后的新增并且未生效的部门,,
	 *             封装成HRDeptVO的list返回,供部门信息节点查询未生效部门使用
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
	 *             业务逻辑:在部门节点查询未生效的部门,以及在进行后台任务回写时 ,需要将部门VO转换成单据VO
	 */
	public HRDeptVO HRDeptAdjust2HRDeptVO(HRDeptAdjustVO vo) throws BusinessException {
		// 先查找当前的部门信息
		HRDeptVO resultVO = (HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class, vo.getPk_dept());
		if (null == resultVO) {
			resultVO = new HRDeptVO();
		}
		// 将修改信息覆盖到当前信息里面
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
			// 增加副主管和工作地点 王永文 20190503 begin
			resultVO.setAttributeValue("glbdef3", vo.getGlbdef3());
			resultVO.setAttributeValue("glbdef11", vo.getGlbdef11());
			// 增加副主管和工作地点 王永文 20190503 end
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
	 *             业务逻辑:在部门节点查询未生效的部门,以及在进行后台任务回写时 ,需要将部门VO转换成单据VO
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
			// 获取组织版本
			String sqlStr = "select pk_vid from org_orgs where pk_org = '" + vo.getPk_org() + "' ";
			Object pkvid = getSingleDataBySQL(sqlStr);
			if (null != pkvid) {
				resultVO.setPk_org_v(String.valueOf(pkvid));
			}
		}
		return resultVO;
	}

	/**
	 * 自动任务--部门撤销 检查校验规则2,才能进行删除,删除时联动删除部门信息的主档 部门撤销和取消撤销暂时用标准功能 撤销部门暂时不进行版本化操作
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
	 *             业务逻辑: 2. 校验人员调配申请生效日期是否在部门生效日期之后
	 */
	public UFBoolean validatePsn(HRDeptAdjustVO vo) throws BusinessException {
		if (null == vo) {
			throw new BusinessException("無效記錄!");
		}
		if (null == vo.getPk_dept()) {
			throw new BusinessException("部門主鍵不得為空!");
		}
		if (null == vo.getEffectivedate()) {
			throw new BusinessException("生效日期不得為空");
		}
		// 搜索是否存在在部門成立之前的人员调用记录
		String sqlStr = "select count(*) from hi_stapply where  newpk_dept = '" + vo.getPk_dept()
				+ "'and  EFFECTDATE > '" + vo.getEffectivedate().toStdString() + "' ";
		Integer rtn = getIntegerDataBySQL(sqlStr);
		if (null != rtn && rtn > 0) {
			throw new BusinessException("無效日期!該日期之前已經存在人員調配申請!請選擇稍早的日期.");
		}
		return new UFBoolean(true);
	}

	/**
	 * 从数据库获取一个值
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unused")
	private Object getSingleDataBySQL(String sql) throws DAOException {
		return (Object) getBaseDAO().executeQuery(sql, new ColumnProcessor());
	}

	/**
	 * 从数据库获取一个整数
	 * 
	 * @return 如果为null 则返回 null
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
	 * 只允许删除执行标志未打勾的,并且生效日期在当前日期之后的单据. 删除时,'调配申请'中存在已引用未来新增部门的记录时，删除该新增部门时报错;
	 * 已经有下级部门时,删除报错
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public UFBoolean validateDel(HRDeptAdjustVO vo) throws BusinessException {
		if (null == vo) {
			throw new BusinessException("單據異常!無法判斷當前狀態!");
		}

		if (vo.getIseffective() != null && !vo.getIseffective().booleanValue()) {
			// 未生效的
			// 查詢調配記錄中,是否有有引用此部門的單據
			String sqlStr = "select count(*) from hi_stapply where newpk_dept ='" + vo.getPk_dept() + "' and dr = 0";
			Integer num = getIntegerDataBySQL(sqlStr);
			if (num != null && num > 0) {
				throw new BusinessException("該部門還有未處理的人員調配申請,請取消調配后再刪除!");
			}
			// 已经有下级部门时,删除报错
			sqlStr = "select count(*) from om_deptadj where pk_fatherorg = '" + vo.getPk_dept() + "' and dr = 0";
			num = getIntegerDataBySQL(sqlStr);
			if (num != null && num > 0) {
				throw new BusinessException("該部門已存在下級部門,無法刪除!");
			}
		} else {
			// 已生效的
			throw new BusinessException("單據已經被執行，不能刪除!");
		}

		return new UFBoolean(false);
	}

	/**
	 * 判斷日期是否在當前日期之後
	 * 
	 * @param date
	 * @return
	 */
	private boolean isAfterToday(UFLiteralDate date) {
		// 获取明天日期的简便方法:Thread.sleep(24*60*60*1000); Date date = new Date();
		if (null != date) {
			// UFLiteralDate tomorrow = new UFLiteralDate().getDateAfter(1);
			return date.after(new UFLiteralDate());
		}
		return false;
	}

	/**
	 * 获得部门变更历史<br>
	 * 更新用
	 * 
	 * @param effDate
	 * 
	 * @return
	 */
	private DeptHistoryVO buildDeptHistoryVO4Update(HRDeptVO vo, UFLiteralDate effDate) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// 批准单位
		deptHistoryVO.setApprovedept(null);
		// 批准文号
		deptHistoryVO.setApprovenum(null);
		// 新部门名
		MultiLangText multiLangText = new MultiLangText();
		multiLangText.setText(vo.getName());
		multiLangText.setText2(vo.getName2());
		multiLangText.setText3(vo.getName3());
		multiLangText.setText4(vo.getName4());
		multiLangText.setText5(vo.getName5());
		multiLangText.setText6(vo.getName6());
		SuperVOHelper.copyMultiLangAttribute(multiLangText, deptHistoryVO);
		// 编码
		deptHistoryVO.setCode(vo.getCode());
		// 部门级别
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// 负责人
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// 生效日期--默認是今天
		// ssx modified on 2019-05-02
		// 由於可以進行前置日期的單據錄入，所以默認不能是今天，而是發生變更的日期
		deptHistoryVO.setEffectdate(effDate);
		// --end
		// 备注
		deptHistoryVO.setMemo("自動版本化");
		// 变更类型-更名
		deptHistoryVO.setChangetype(DeptChangeType.RENAME);
		// 部门主键
		deptHistoryVO.setPk_dept(vo.getPk_dept());
		// 组织主键
		deptHistoryVO.setPk_org(vo.getPk_org());

		return deptHistoryVO;
	}

	/**
	 * 获得部门变更历史<br>
	 * 取消撤銷用
	 * 
	 * @return
	 */
	private DeptHistoryVO buildDeptHistoryVO4UnCancel(HRDeptVO vo) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// 批准单位
		deptHistoryVO.setApprovedept(null);
		// 批准文号
		deptHistoryVO.setApprovenum(null);
		// 编码
		deptHistoryVO.setCode(vo.getCode());
		// 部门名
		SuperVOHelper.copyMultiLangAttribute(vo, deptHistoryVO);
		// 部门级别
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// 负责人
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// 生效日期
		deptHistoryVO.setEffectdate(new UFLiteralDate());
		// 备注
		deptHistoryVO.setMemo(null);
		// 变更类型-反撤销
		deptHistoryVO.setChangetype(DeptChangeType.HRUNCANCELED);
		// 部门主键
		deptHistoryVO.setPk_dept(vo.getPk_dept());
		// 组织主键
		deptHistoryVO.setPk_org(vo.getPk_org());
		// 接收部门
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
			throw new BusinessException("部門主檔不能為空!");
		}

		HRDeptVO deptVO = (HRDeptVO) saveVO.getParentVO();
		deptVO.setName(deptVO.getName() == null ? null : deptVO.getName().replaceAll("'", ""));
		deptVO.setName2(deptVO.getName2() == null ? null : deptVO.getName2().replaceAll("'", ""));
		deptVO.setName3(deptVO.getName3() == null ? null : deptVO.getName3().replaceAll("'", ""));
		// 獲取當前的部門信息
		HRDeptVO curVO = (HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class, deptVO.getPk_dept());

		checkDeptCanceled(curVO, date);

		String newPkdeptV = curVO.getPk_vid();

		if (deptVO.getPk_vid() == null || deptVO.getPk_vid().equals("~") || deptVO.getPk_vid().equals("null")) {
			deptVO.setPk_vid(curVO.getPk_vid());
		}
		// 設置啟用
		deptVO.setEnablestate(2);
		deptVO.setIslastversion(new UFBoolean(true));
		saveVO.setParentVO(deptVO);
		// 处理撤销标志
		if (curVO != null && curVO.getHrcanceled() != null && deptVO.getHrcanceled() != null) {
			// 原来是撤销状态,且现在为非撤销状态,先取消撤銷
			if (curVO.getHrcanceled().booleanValue() && !deptVO.getHrcanceled().booleanValue()) {
				DeptHistoryVO historyVO = buildDeptHistoryVO4ImportUnCancel(deptVO, date);
				// 反撤銷
				AggHRDeptVO[] uncanceledDepts = getDeptManageService().uncancel(saveVO, historyVO, false, false, true);
				// /部门版本化操作完善 王永文 20190502 begin
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
				 * 调用标准产品的版本化服务，但是标准产品要求版本化时必须修改名称，
				 * 而我们的需求是支持不修改名称的，解决的方案是先把名称修改，再把名称update回来
				 */
				pvo.setName(pvo.getName() + "@@@@TWNC");
				if (pvo.getName2() != null)
					pvo.setName2(pvo.getName2() + "@@@@TWNC");
				if (pvo.getName3() != null)
					pvo.setName3(pvo.getName3() + "@@@@TWNC");
				// 调用标准产品的标准版本化操作
				DeptVO hrDptNewVerVO = (DeptVO) orgManageService.createNewVersionVO("nc.vo.org.DeptVO", pvo, "版本更新"
						+ date.getYear() + vno, date.getYear() + vno, new UFDate(date.toString() + " 00:00:00"),
						new UFDate(date.getDateBefore(1).toString() + " 23:59:59"));
				if (hrDptNewVerVO == null) {
					return;
				}
				// 把名称再给update回来,同时把部门表和部门版本表的负责人、副主管、所在地点更新了
				updateOrgName(name, name2, name3, hrDptNewVerVO);
				/*
				 * AggHRDeptVO[] newVOs =
				 * getDeptManageService().createDeptVersion(uncanceledDepts, new
				 * UFDate(date.toDate()));
				 */
				// 更新HR部门表
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
				// 回写工作记录
				String sqlStr = "update  hi_psnjob set pk_dept_v = '" + newPkdeptV + "'  where begindate <= '"
						+ date.toStdString() + "' and isnull(enddate,'9999-12-31') > '" + date.toStdString()
						+ "' and  pk_dept = '" + deptVO.getPk_dept() + "'";
				getBaseDAO().executeUpdate(sqlStr);
				return;
			}
		}
		// 修改邏輯

		DeptHistoryVO historyVO = buildDeptHistoryVO4ImportUpdate(deptVO, date);
		// 部门版本化操作完善 王永文 20190502 begin
		HRDeptVO parentVO = (HRDeptVO) saveVO.getParentVO();
		IOrgVersionManageService orgManageService = NCLocator.getInstance().lookup(IOrgVersionManageService.class);
		String vno = queryNextVersionNO("org_dept_v", "pk_dept", deptVO.getPk_dept(), String.valueOf(date.getYear()));
		DeptVO pvo = (DeptVO) new BaseDAO().retrieveByPK(DeptVO.class, parentVO.getPk_dept());
		String name = pvo.getName();
		String name2 = pvo.getName2();
		String name3 = pvo.getName3();
		/*
		 * 调用标准产品的版本化服务，但是标准产品要求版本化时必须修改名称，
		 * 而我们的需求是支持不修改名称的，解决的方案是先把名称修改，再把名称update回来
		 */
		pvo.setName(pvo.getName() + "@@@@TWNC");
		if (pvo.getName2() != null)
			pvo.setName2(pvo.getName2() + "@@@@TWNC");
		if (pvo.getName3() != null)
			pvo.setName3(pvo.getName3() + "@@@@TWNC");
		// 调用标准产品的标准版本化操作
		DeptVO hrDptNewVerVO = (DeptVO) orgManageService.createNewVersionVO("nc.vo.org.DeptVO", pvo,
				"版本更新" + date.getYear() + vno, date.getYear() + vno, new UFDate(date.toString() + " 00:00:00"),
				new UFDate(date.getDateBefore(1).toString() + " 23:59:59"));
		if (hrDptNewVerVO == null) {
			return;
		}
		// 把名称再给update回来,同时把部门表和部门版本表的负责人、副主管、所在地点更新了
		updateOrgName(name, name2, name3, hrDptNewVerVO);
		// 更新HR部门表
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
		// 如果部門代碼變更或部門名稱變更,則新增人員任職記錄，
		int renameAndPrincipalChangeFlag = 0;
		if (deptVO.getCode() != null && curVO.getName() != null) {
			if (!deptVO.getCode().equals(curVO.getCode()) || !deptVO.getName().equals(curVO.getName())) {
				// 變更了名稱
				renameAndPrincipalChangeFlag = 1;
			}
		}
		if ((deptVO.getPrincipal() == null && curVO.getCode() != null)
				|| (deptVO.getPrincipal() != null && curVO.getPrincipal() == null)
				|| (deptVO.getPrincipal() != null && curVO.getPrincipal() != null && !deptVO.getPrincipal().equals(
						curVO.getPrincipal()))) {
			// 變更了負責人
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
			// 插入历史数据
			getBaseDAO().insertVO(historyVO);
			newPkdeptV = ((HRDeptVO) newVO.getParentVO()).getPk_vid();
		}

		// ssx added on 2019-10-25
		// 部門負責人變更時，同步變更下級部門的上級主管
		if (renameAndPrincipalChangeFlag == 2) {
			updateChildCharger(deptVO.getPk_dept(), deptVO.getPrincipal());
		}
		// end

		// 回写工作记录表
		String sqlStr = "update  hi_psnjob set pk_dept_v = '" + newPkdeptV + "'  where begindate <= '"
				+ date.toStdString() + "' and isnull(enddate,'9999-12-31') > '" + date.toStdString()
				+ "' and  pk_dept = '" + deptVO.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sqlStr);

	}

	private void checkDeptCanceled(HRDeptVO deptVO, UFLiteralDate date) throws BusinessException {
		if (UFBoolean.TRUE.equals(deptVO.getHrcanceled())) {
			throw new BusinessException("已撤銷部門無法進行版本作業。");
		}
	}

	private void updateOrgName(String name, String name2, String name3, DeptVO vo) throws DAOException {
		// 更新部门表,更新负责人、副主管、所在地点
		String sql_dept = "update org_dept set name = " + getNmF(name) + ", name2 = " + getNmF(name2) + ",name3="
				+ getNmF(name3) + " where pk_dept = '" + vo.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_dept);
		// 更新部门版本表,更新负责人、副主管、所在地点
		String sql_dept_v = "update org_dept_v set name = " + getNmF(name) + ", name2 = " + getNmF(name2) + ",name3="
				+ getNmF(name3) + " where pk_vid = '" + vo.getPk_vid() + "'";
		getBaseDAO().executeUpdate(sql_dept_v);
		// 更新报表组织表
		String sql_report = "update org_reportorg set name = " + getNmF(name) + ", name2 = " + getNmF(name2)
				+ ",name3=" + getNmF(name3) + " where pk_reportorg = '" + vo.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_report);
		// 更新报表组织版本表
		String sql_report_v = "update org_reportorg_v set name = " + getNmF(name) + ", name2 = " + getNmF(name2)
				+ ",name3=" + getNmF(name3) + " where pk_vid = '" + vo.getPk_vid() + "'";
		getBaseDAO().executeUpdate(sql_report_v);
		// 更新组织表
		String sql_org = "update org_orgs set name = " + getNmF(name) + ", name2 = " + getNmF(name2) + ",name3="
				+ getNmF(name3) + " where pk_org = '" + vo.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_org);

	}

	private DeptHistoryVO buildDeptHistoryVO4ImportUpdate(HRDeptVO vo, UFLiteralDate date) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// 批准单位
		deptHistoryVO.setApprovedept(null);
		// 批准文号
		deptHistoryVO.setApprovenum(null);
		// 新部门名
		MultiLangText multiLangText = new MultiLangText();
		multiLangText.setText(vo.getName());
		multiLangText.setText2(vo.getName2());
		multiLangText.setText3(vo.getName3());
		multiLangText.setText4(vo.getName4());
		multiLangText.setText5(vo.getName5());
		multiLangText.setText6(vo.getName6());
		SuperVOHelper.copyMultiLangAttribute(multiLangText, deptHistoryVO);
		// 编码
		deptHistoryVO.setCode(vo.getCode());
		// 部门级别
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// 负责人
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// 生效日期--默認是今天
		deptHistoryVO.setEffectdate(date);
		// 备注
		deptHistoryVO.setMemo("自動版本化");
		// 变更类型-更名
		deptHistoryVO.setChangetype(DeptChangeType.RENAME);
		// 部门主键
		deptHistoryVO.setPk_dept(vo.getPk_dept());
		// 组织主键
		deptHistoryVO.setPk_org(vo.getPk_org());

		return deptHistoryVO;
	}

	private DeptHistoryVO buildDeptHistoryVO4ImportUnCancel(HRDeptVO vo, UFLiteralDate date) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// 批准单位
		deptHistoryVO.setApprovedept(null);
		// 批准文号
		deptHistoryVO.setApprovenum(null);
		// 编码
		deptHistoryVO.setCode(vo.getCode());
		// 部门名
		SuperVOHelper.copyMultiLangAttribute(vo, deptHistoryVO);
		// 部门级别
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// 负责人
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// 生效日期
		deptHistoryVO.setEffectdate(date);
		// 备注
		deptHistoryVO.setMemo(null);
		// 变更类型-反撤销
		deptHistoryVO.setChangetype(DeptChangeType.HRUNCANCELED);
		// 部门主键
		deptHistoryVO.setPk_dept(vo.getPk_dept());
		// 组织主键
		deptHistoryVO.setPk_org(vo.getPk_org());
		// 接收部门
		deptHistoryVO.setIsreceived(UFBoolean.FALSE);

		return deptHistoryVO;
	}

	private char[] ran = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * 获得随机四个字符的字符串
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
	 * 更新当前部门的innercode
	 * 
	 * @param innercode
	 *            上级部门的innercode
	 * @param pk_dept
	 *            更新的部门
	 * @return
	 * @throws DAOException
	 */
	private String updateCode(String innercode, String pk_dept) throws DAOException {
		StringBuffer sql = new StringBuffer();
		String s = get4RandomCode();
		// 更新部门表
		sql.append("update org_dept set innercode = '").append(innercode).append(s).append("' where pk_dept = '")
				.append(pk_dept).append("'");
		this.getBaseDAO().executeUpdate(sql.toString());
		// 更新部门版本表
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
