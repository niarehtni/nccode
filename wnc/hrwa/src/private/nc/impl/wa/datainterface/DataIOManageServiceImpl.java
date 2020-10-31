package nc.impl.wa.datainterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.hr.datainterface.IDataIOManageService;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.pub.wa.datainterface.DataItfConst;
import nc.pub.wa.datainterface.DataItfFileReader;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.hr.tools.pub.HRAggVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.hrdept.HRDeptVersionVO;
import nc.vo.om.orginfo.HROrgVersionVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.datainterface.BonusOthBuckVO;
import nc.vo.wa.datainterface.DataItfFileVO;
import nc.vo.wa.datainterface.MappingFieldVO;
import nc.vo.wa.datainterface.SalaryOthBuckVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 批量维护 Service实现
 * 
 * @author xuanlt
 * 
 */
public class DataIOManageServiceImpl implements IDataIOManageService {

	private final String DOC_NAME = "datainterface";
	private SimpleDocServiceTemplate serviceTemplate;
	private WaifsetDao dao = null;

	/**
	 * @author xuanlt on 2010-1-26
	 * @see nc.itf.hr.datainterface.IDataIOManageService#add(nc.vo.hr.tools.pub.HRAggVO)
	 */
	@Override
	public HrIntfaceVO add(HRAggVO vo) throws BusinessException {

		// 保存数据接口
		return null;
	}

	/**
	 * @author xuanlt on 2010-1-26
	 * @see nc.itf.hr.datainterface.IDataIOManageService#update(nc.vo.hr.tools.pub.HRAggVO)
	 */
	@Override
	public HrIntfaceVO update(HRAggVO vo) throws BusinessException {
		// 更新数据接口
		return null;
	}

	/**
	 * @author xuanlt on 2010-1-12
	 * @see nc.itf.hr.datainterface.IDataIOQueryService#queryByCondition(nc.vo.uif2.LoginContext,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public AggHrIntfaceVO[] queryByCondition(LoginContext context, String condition, String strOrderBy)
			throws BusinessException {

		return getWaifsetDao().queryByCondition((WaLoginContext) context, condition, strOrderBy);

	}

	@Override
	public AggHrIntfaceVO update(AggHrIntfaceVO vo) throws BusinessException {
		if (vo.getParentVO().getAttributeValue("classid") != null) {
			String sql = "delete from hr_dataintface_b where ifid = '" + vo.getParentVO().getPrimaryKey() + "'";
			getWaifsetDao().getBaseDao().executeUpdate(sql);
		}
		return getServiceTemplate().update(vo, true);
	}

	@Override
	public void delete(AggHrIntfaceVO vo) throws BusinessException {
		getServiceTemplate().delete(vo);

	}

	@Override
	public AggHrIntfaceVO insert(AggHrIntfaceVO vo) throws BusinessException {
		return getServiceTemplate().insert(vo);
	}

	private WaifsetDao getWaifsetDao() {
		try {
			if (dao == null) {
				dao = new WaifsetDao();
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return dao;
	}

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (null == serviceTemplate) {
			serviceTemplate = new SimpleDocServiceTemplate(DOC_NAME);
		}
		return serviceTemplate;
	}

	// MOD {薪资、奖金明细代扣项导入} kevin.nie 2018-01-30 start
	@Override
	public <T extends SuperVO> T[] queryDataByConditon(String condition, String[] psnPks, Class<T> classz)
			throws BusinessException {
		if (psnPks != null && psnPks.length > 0) {
			condition += " and pk_psndoc in (select in_pk from ";
			String tmpTableName = "hr_wadata_import_" + String.valueOf(new UFDouble(Math.random() * 100000).intValue());
			condition += new InSQLCreator().createTempTable(tmpTableName, psnPks);
			condition += ") ";
		}
		return CommonUtils.retrieveByClause(classz, getWaifsetDao().getBaseDao(), condition);
	}

	@Override
	public <T extends SuperVO> T[] insertPayDetail(T[] vos, T[] delVos) throws BusinessException {
		if (!ArrayUtils.isEmpty(delVos)) {
			getWaifsetDao().getBaseDao().deleteVOArray(delVos);
		}
		if (!ArrayUtils.isEmpty(vos)) {
			String[] pks = getWaifsetDao().getBaseDao().insertVOArray(vos);
			for (int i = 0; i < vos.length; i++) {
				vos[i].setPrimaryKey(pks[i]);
			}
			return vos;
		}
		return null;
	}

	@Override
	public void importPayDataSD(DataVO[] SDVos) throws BusinessException {
		// 组装薪资明细数据存储逻辑
		if (ArrayUtils.isEmpty(SDVos)) {
			return;
		}
		convertData(SDVos);

		String[] psnPks = StringPiecer.getStrArrayDistinct(SDVos, DataVO.PK_PSNDOC);
		SalaryOthBuckVO[] SODVos = queryDataByConditon("pk_wa_class='" + SDVos[0].getPk_wa_class() + "'", psnPks,
				SalaryOthBuckVO.class);

		// 批量期間導入 導入201910 員工郭育培部門應為召幕任用部 但導入時看到員工關心部 by George 20200521 缺陷Bug
		// #35316
		// 加一個布林值判斷要不要查詢 组织_部门版本信息(org_dept_v)
		addOtherToMainVO(SDVos, SODVos, false);

		List<String> pklist = new ArrayList<String>();
		// 驗重
		for (DataVO vo : SDVos) {
			String key = vo.getPk_wa_class() + vo.getCyear() + vo.getCperiod() + vo.getPk_psndoc();
			if (pklist.contains(key)) {
				throw new BusinessException("員工 [" + vo.getPsncode() + "] 的當期資料發生重覆。");
			} else {
				pklist.add(key);
			}
		}

		SalaryEncryptionUtil.encryption4Array(SDVos);
		getWaifsetDao().getBaseDao().insertVOArray(SDVos);
		// 清除组织薪资方案内存储的数据.
		if (!ArrayUtils.isEmpty(SODVos)) {
			getWaifsetDao().getBaseDao().deleteVOArray(SODVos);
		}
	}

	@SuppressWarnings("unchecked")
	private void convertData(DataVO[] sDVos) throws BusinessException {
		Set<String> deptCodes = new HashSet<String>();
		Set<String> psnCodes = new HashSet<String>();

		StringBuilder errDeptMsg = new StringBuilder();
		StringBuilder errPsnMsg = new StringBuilder();
		StringBuilder errJobMsg = new StringBuilder();

		if (!ArrayUtils.isEmpty(sDVos)) {
			for (DataVO vo : sDVos) {
				deptCodes.add(vo.getWorkdept());
				psnCodes.add(vo.getPsncode());
			}

			BaseDAO dao = new BaseDAO();

			// 加載人員VO
			Collection<PsndocVO> psndocVOs = dao.retrieveByClause(PsndocVO.class, "", new String[] {
					PsndocVO.PK_PSNDOC, PsndocVO.CODE, PsndocVO.NAME, PsndocVO.NAME2, PsndocVO.NAME3, PsndocVO.NAME4,
					PsndocVO.NAME5, PsndocVO.NAME6 });
			Map<String, PsndocVO> psnMap = CommonUtils.toMap(PsndocVO.CODE, psndocVOs.toArray(new PsndocVO[0]));

			String deptCode = "";
			String pk_dept = "";
			Map<String, String[]> deptMap = new HashMap<String, String[]>();

			for (DataVO vo : sDVos) {
				if (deptMap.containsKey(vo.getWorkdept())) {
					vo.setWorkdeptvid(deptMap.get(vo.getWorkdept())[1]);
					vo.setDeptname(deptMap.get(vo.getWorkdept())[2]);
					vo.setWorkdept(deptMap.get(vo.getWorkdept())[0]);
				} else {
					Collection<HRDeptVersionVO> deptvVOs = dao
							.retrieveByClause(
									HRDeptVersionVO.class,
									"code ='"
											+ vo.getWorkdept()
											+ "'"
											+ " and vstartdate <= (select cenddate || ' 23:59:59' from wa_period wp inner join wa_periodscheme ps on wp.pk_periodscheme = ps.pk_periodscheme inner join wa_waclass wc on wc.pk_periodscheme = wp.pk_periodscheme where wp.cyear='"
											+ vo.getCyear()
											+ "' and wp.cperiod='"
											+ vo.getCperiod()
											+ "' and wc.pk_wa_class = '"
											+ vo.getPk_wa_class()
											+ "') and venddate >= (select cstartdate || '00:00:00' from wa_period wp inner join wa_periodscheme ps on wp.pk_periodscheme = ps.pk_periodscheme inner join wa_waclass wc on wc.pk_periodscheme = wp.pk_periodscheme where wp.cyear='"
											+ vo.getCyear() + "' and wp.cperiod='" + vo.getCperiod()
											+ "' and wc.pk_wa_class = '" + vo.getPk_wa_class() + "')",
									"vstartdate desc", new String[] { HRDeptVersionVO.PK_DEPT, HRDeptVersionVO.PK_VID,
											HRDeptVersionVO.CODE, HRDeptVersionVO.NAME, HRDeptVersionVO.NAME2,
											HRDeptVersionVO.NAME3, HRDeptVersionVO.NAME4, HRDeptVersionVO.NAME5,
											HRDeptVersionVO.NAME6 });
					if (deptvVOs != null && deptvVOs.size() > 0) {
						HRDeptVersionVO deptvvo = deptvVOs.toArray(new HRDeptVersionVO[0])[0];
						pk_dept = deptvvo.getPk_dept();
						deptCode = deptvvo.getCode();
						vo.setWorkdept(deptvvo.getPk_dept());
						vo.setWorkdeptvid(deptvvo.getPk_vid());
						// 批量期間導入 導入201910 員工郭育培部門應為召幕任用部 但導入時看到員工關心部 by George
						// 20200521 缺陷Bug #35316
						// 批量期間導入 所需要 组织_部门版本信息(org_dept_v) 相關值提前放入
						vo.setFipdeptvid(deptvvo.getPk_vid());
						vo.setLibdeptvid(deptvvo.getPk_vid());
						vo.setDeptname(MultiLangHelper.getName(deptvvo));
					} else {
						Collection<HRDeptVO> deptVOs = dao.retrieveByClause(HRDeptVO.class, "code='" + vo.getWorkdept()
								+ "'");
						if (deptVOs != null || deptVOs.size() > 0) {
							HRDeptVO deptvo = deptVOs.toArray(new HRDeptVO[0])[0];
							pk_dept = deptvo.getPk_dept();
							deptCode = deptvo.getCode();
							vo.setWorkdept(deptvo.getPk_dept());
							vo.setWorkdeptvid(deptvo.getPk_vid());
							// 批量期間導入 導入201910 員工郭育培部門應為召幕任用部 但導入時看到員工關心部 by
							// George 20200521 缺陷Bug #35316
							// 批量期間導入 所需要 组织_部门版本信息(org_dept_v) 相關值提前放入
							vo.setFipdeptvid(deptvo.getPk_vid());
							vo.setLibdeptvid(deptvo.getPk_vid());
							vo.setDeptname(MultiLangHelper.getName(deptvo));
						} else {
							String err = "[" + vo.getWorkdept() + "]: "
									+ ResHelper.getString("6013dataitf_01", "dataitf-01-0023") + "\r\n";// 部門编码在系统中找不到!
							if (errDeptMsg.indexOf(err) < 0) {
								errDeptMsg.append(err);
							}
							continue;
						}
					}

					deptMap.put(deptCode, new String[] { vo.getWorkdept(), vo.getWorkdeptvid(), vo.getDeptname() });
				}

				String pk_psndoc = psnMap.containsKey(vo.getPsncode()) ? psnMap.get(vo.getPsncode()).getPk_psndoc()
						: null;
				if (!StringUtils.isEmpty(pk_psndoc)) {
					vo.setPk_psndoc(pk_psndoc);
					vo.setPsnname(MultiLangHelper.getName(psnMap.get(vo.getPsncode())));
				} else {
					errPsnMsg.append("[" + vo.getPsncode() + "]: "
							+ ResHelper.getString("6013dataitf_01", "dataitf-01-0025") + "\r\n");// 人員编码在系统中找不到!
					continue;
				}

				Collection<PsnJobVO> jobVOs = dao
						.retrieveByClause(
								PsnJobVO.class,
								"pk_psndoc = '"
										+ pk_psndoc
										+ "' "
										// +"and pk_dept = '"
										// + pk_dept
										// + "' "
										+ " and begindate <= (select cenddate from wa_period wp inner join wa_periodscheme ps on wp.pk_periodscheme = ps.pk_periodscheme inner join wa_waclass wc on wc.pk_periodscheme = wp.pk_periodscheme where wp.cyear='"
										+ vo.getCyear()
										+ "' and wp.cperiod='"
										+ vo.getCperiod()
										+ "' and wc.pk_wa_class = '"
										+ vo.getPk_wa_class()
										+ "') and nvl(enddate, '9999-12-31') >= (select cstartdate from wa_period wp inner join wa_periodscheme ps on wp.pk_periodscheme = ps.pk_periodscheme inner join wa_waclass wc on wc.pk_periodscheme = wp.pk_periodscheme where wp.cyear='"
										+ vo.getCyear() + "' and wp.cperiod='" + vo.getCperiod()
										+ "' and wc.pk_wa_class = '" + vo.getPk_wa_class() + "') and trnsevent <> 4",
								"begindate", new String[] { PsnJobVO.PK_PSNJOB, PsnJobVO.PK_PSNORG, PsnJobVO.PK_DEPT_V,
										PsnJobVO.PK_ORG, PsnJobVO.CLERKCODE });
				if (jobVOs != null && jobVOs.size() > 0) {
					PsnJobVO jobvo = jobVOs.toArray(new PsnJobVO[0])[0];
					vo.setPk_psnjob(jobvo.getPk_psnjob());
					vo.setPk_psnorg(jobvo.getPk_psnorg());
					vo.setWorkdeptvid(jobvo.getPk_dept_v());
					// 批量期間導入 導入201910 員工郭育培部門應為召幕任用部 但導入時看到員工關心部 by George
					// 20200521 缺陷Bug #35316
					// 批量期間導入 所需要 组织_部门版本信息(org_dept_v) 相關值提前放入
					// 這裡是由 工作记录(hi_psnjob) 查詢 组织_部门版本信息(org_dept_v)，才能查到對的部門
					vo.setFipdeptvid(jobvo.getPk_dept_v());
					vo.setLibdeptvid(jobvo.getPk_dept_v());
					vo.setWorkorg(jobvo.getPk_org());
					vo.setClerkcode(jobvo.getClerkcode());
				} else {
					// 人员[" + psnCode + "]
					// 在部门[" + deptCode + "]下的任职记录在系统中找不到!
					errJobMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0026", null,
							new String[] { vo.getPsncode(), deptCode })
							+ "\r\n");
					continue;
				}
			}
		}

		if (errDeptMsg.length() > 0 || errPsnMsg.length() > 0 || errJobMsg.length() > 0) {
			throw new BusinessException(errDeptMsg.append(errPsnMsg).append(errJobMsg).toString());
		}
	}

	@Override
	public void importPayDataBD(DataVO[] BDVos, BonusOthBuckVO[] BODVos) throws BusinessException {
		// 组装奖金明细数据存储逻辑
		if (ArrayUtils.isEmpty(BDVos)) {
			return;
		}
		// 批量期間導入 導入201910 員工郭育培部門應為召幕任用部 但導入時看到員工關心部 by George 20200521 缺陷Bug
		// #35316
		// 加一個布林值判斷要不要查詢 组织_部门版本信息(org_dept_v)，這裡先按照原來的需要查詢
		addOtherToMainVO(BDVos, BODVos, true);
		SalaryEncryptionUtil.encryption4Array(BDVos);
		getWaifsetDao().getBaseDao().insertVOArray(BDVos);
		// 清除组织薪资方案内存储的数据.
		if (!ArrayUtils.isEmpty(BODVos)) {
			getWaifsetDao().getBaseDao().deleteVOArray(BODVos);
		}
	}

	@Override
	public Map<String, PsndocVO> queryPsnByOrgConditionn(String pk_org, String pk_wa_class, String cyear,
			String cperiod, boolean includeJob) throws BusinessException {
		// String condition =
		// " (pk_psndoc in (select distinct pk_psndoc from hi_psnjob pj inner join (select cstartdate, cenddate from wa_period wp inner join wa_periodscheme ps on wp.pk_periodscheme = ps.pk_periodscheme inner join wa_waclass wc on wc.pk_periodscheme = wp.pk_periodscheme where wp.cyear='"
		// + cyear
		// + "' and wp.cperiod='"
		// + cperiod
		// + "' and wc.pk_wa_class = '"
		// + pk_wa_class
		// +
		// "') tmp on tmp.cstartdate <= nvl(pj.enddate,'9999-12-31') and pj.trnsevent<>4)) ";
		String condition = " (pk_psndoc in (select pk_psndoc from wa_data where pk_wa_class = '" + pk_wa_class
				+ "' and cyear = '" + cyear + "' and cperiod = '" + cperiod + "')) ";
		return queryPsnByOrgConditionn(pk_org, condition, includeJob);
	}

	@Override
	public Map<String, PsndocVO> queryPsnByOrgConditionn(String pk_org, String condition, boolean includeJob)
			throws BusinessException {
		SqlBuilder where = new SqlBuilder();
		where.append("pk_org", pk_org);
		if (StringUtils.isNotBlank(condition)) {
			where.append(" and ");
			where.append(condition);
		} else {
			where.append("and pk_psndoc in (select distinct pk_psndoc from hi_psnorg where indocflag='Y') ");
		}
		PsndocVO[] psnVos = CommonUtils.retrieveByClause(PsndocVO.class, where.toString());
		if (includeJob && !ArrayUtils.isEmpty(psnVos)) {
			String[] psnPks = StringPiecer.getStrArrayDistinct(psnVos, PsndocVO.PK_PSNDOC);
			InSQLCreator isc = new InSQLCreator();
			PsnJobVO[] jobVos = CommonUtils.retrieveByClause(
					PsnJobVO.class,
					"ismainjob='Y' and "
							+ (StringUtils.isNotBlank(condition) ? condition.replaceAll("pk_psndoc", "pk_psnjob")
									: ("pk_psndoc in (" + isc.getInSQL(psnPks) + ") ")));
			if (!ArrayUtils.isEmpty(jobVos)) {
				Map<String, PsnJobVO[]> psnJobArrMap = CommonUtils.group2ArrayByField(PsnJobVO.PK_PSNDOC, jobVos);

				String[] psnorgPks = StringPiecer.getStrArrayDistinct(jobVos, PsnJobVO.PK_PSNORG);
				PsnOrgVO[] psnorgVos = CommonUtils.queryByPks(PsnOrgVO.class, psnorgPks);
				Map<String, PsnOrgVO> pkPsnorgVOMap = CommonUtils.toMap(PsnOrgVO.PK_PSNORG, psnorgVos);

				String[] deptPks = StringPiecer.getStrArrayDistinct(jobVos, PsnJobVO.PK_DEPT);
				HRDeptVO[] deptVos = CommonUtils.queryByPks(HRDeptVO.class, deptPks);
				Map<String, HRDeptVO> pkDeptVOMap = CommonUtils.toMap(HRDeptVO.PK_DEPT, deptVos);

				Map<String, PsndocVO> resultMap = new HashMap<String, PsndocVO>();
				for (PsndocVO psnVO : psnVos) {
					PsnJobVO[] jobs = psnJobArrMap.get(psnVO.getPk_psndoc());
					if (ArrayUtils.isEmpty(psnVos)) {
						resultMap.put(psnVO.getCode(), psnVO);
					} else {
						if (jobs.length > 1) {
							Arrays.sort(jobs, new Comparator<PsnJobVO>() {
								@Override
								public int compare(PsnJobVO o1, PsnJobVO o2) {
									return o1.getBegindate().compareTo(o2.getBegindate());
								}
							});
						}
						for (PsnJobVO psnjob : jobs) {
							PsndocVO baseVO = (PsndocVO) psnVO.clone();
							baseVO.setPsnJobVO(psnjob);
							if (null != pkPsnorgVOMap) {
								baseVO.setPsnOrgVO(pkPsnorgVOMap.get(psnjob.getPk_psnorg()));
							}

							String key = psnVO.getCode();
							if (null != pkDeptVOMap) {
								HRDeptVO deptVO = pkDeptVOMap.get(psnjob.getPk_dept());
								if (null != deptVO && StringUtils.isNotBlank(deptVO.getCode())) {
									key += deptVO.getCode();
								}
							}
							resultMap.put(key, baseVO);
						}
					}
				}
				return resultMap;
			}
		}

		return CommonUtils.toMap(PsndocVO.CODE, psnVos);
	}

	/**
	 * 批量期間導入 導入201910 員工郭育培部門應為召幕任用部 但導入時看到員工關心部 by George 20200521 缺陷Bug
	 * #35316 加一個布林值判斷要不要查詢 组织_部门版本信息(org_dept_v)
	 */
	private void addOtherToMainVO(DataVO[] vos, DataItfFileVO[] detailVos, boolean reloadDeptV)
			throws BusinessException {
		// TODO处理必要字段及加扣明细-奖金
		String[] orgpks = StringPiecer.getStrArrayDistinct(vos, DataVO.WORKORG);

		// 批量期間導入 導入201910 員工郭育培部門應為召幕任用部 但導入時看到員工關心部 by George 20200521 缺陷Bug
		// #35316
		// 批量期間導入 不查詢 组织_部门版本信息(org_dept_v)，因為查詢沒有看 工作记录 (hi_psnjob) 的
		// 部门版本信息(pk_dept_v)，會抓錯部門
		String[] deptpks = reloadDeptV ? StringPiecer.getStrArrayDistinct(vos, DataVO.WORKDEPT) : new String[0];
		Map<String, String> deptVMap = reloadDeptV ? qryVersionPks(HRDeptVersionVO.class, HRDeptVersionVO.PK_DEPT,
				deptpks, HRDeptVersionVO.PK_VID) : new HashMap<String, String>();

		Map<String, String> orgVMap = qryVersionPks(HROrgVersionVO.class, HROrgVersionVO.PK_ORG, orgpks,
				HROrgVersionVO.PK_VID);
		Map<String, Map<String, DataItfFileVO>> uidCodeVOMap = null;
		Integer type = null;
		if (!ArrayUtils.isEmpty(detailVos)) {
			uidCodeVOMap = transferDetailToMap(detailVos);
			DataItfFileVO typeVO = detailVos[0];
			if (typeVO instanceof SalaryOthBuckVO) {
				type = MappingFieldVO.TYPE_SOD;
			} else if (typeVO instanceof BonusOthBuckVO) {
				type = MappingFieldVO.TYPE_BOD;
			}
		}
		Map<String, MappingFieldVO> codeMappVOMap = DataItfFileReader.getMappingByType(type);
		Map<String, DataVO> cyearperidVOMap = new HashMap<String, DataVO>();
		for (DataVO vo : vos) {
			// 处理加扣明细
			if (null != uidCodeVOMap && !uidCodeVOMap.isEmpty()) {
				dealWithDetail(vo, uidCodeVOMap, codeMappVOMap, type);
			}
			// 设置默认值
			setDefaultDate(vo, deptVMap, orgVMap);
			if (!cyearperidVOMap.containsKey(vo.getCyearperiod())) {
				cyearperidVOMap.put(vo.getCyearperiod(), vo);
			}
		}
		dealWithPeriodState(vos[0], cyearperidVOMap);
	}

	protected void dealWithDetail(DataVO vo, Map<String, Map<String, DataItfFileVO>> uidCodeVOMap,
			Map<String, MappingFieldVO> codeMappVOMap, Integer type) throws BusinessException {
		if (MapUtils.isEmpty(uidCodeVOMap)) {
			return;
		}
		String uid = new StringBuilder(vo.getPk_psndoc()).append(vo.getWorkdept()).append(vo.getCyearperiod())
				.toString();
		Map<String, DataItfFileVO> codeVOMap = uidCodeVOMap.get(uid);
		if (MapUtils.isEmpty(codeVOMap) || type == null) {
			return;
		}
		if (MapUtils.isEmpty(codeMappVOMap)) {
			// Mapping表(wa_imp_fieldmapping)中没有找到类型[{0}]的薪资项目对应字段信息!
			throw new BusinessException(ResHelper.getString("6013dataitf_01", "dataitf-01-0041", null,
					new String[] { String.valueOf(type) }));
		}
		int len = codeVOMap.size();
		int cnt = 0;
		for (String code : codeVOMap.keySet()) {
			cnt++;
			MappingFieldVO mappVO = codeMappVOMap.get(code);
			if (null == mappVO) {
				continue;
			}
			String itemkey = mappVO.getItemkey();
			DataItfFileVO difVO = codeVOMap.get(code);
			String vitemkey = difVO.getTotalSum();
			StringBuilder vRemark = new StringBuilder(StringUtils.isBlank(vo.getVpaycomment()) ? ""
					: vo.getVpaycomment());
			if (StringUtils.isNotBlank(difVO.getRemark())) {
				vRemark.append(",").append(difVO.getRemark());
			}
			if (StringUtils.isNotBlank(itemkey)) {
				Object obj = vo.getAttributeValue(itemkey);
				UFDouble ufd = (null == obj ? UFDouble.ZERO_DBL : new UFDouble(String.valueOf(obj)));
				ufd = ufd.add(new UFDouble(vitemkey));
				vitemkey = DataItfConst.DF_NUM.format(ufd.doubleValue());
				vo.setAttributeValue(itemkey, vitemkey);
			}
			if (cnt == len) {
				if (vRemark.length() > 0) {
					vRemark.deleteCharAt(0);
				}
				vo.setAttributeValue(DataVO.VPAYCOMMENT, vRemark.toString());
			}
		}
	}

	protected void setDefaultDate(DataVO vo, Map<String, String> deptVMap, Map<String, String> orgVMap) {
		vo.setPk_wa_data(null);
		vo.setAssgid(Integer.valueOf(1));
		// vo.setAttributeValue("C_11", "postname");
		// vo.setAttributeValue("C_7", "xing");
		// vo.setAttributeValue("C_8", "ming");
		// vo.setAttributeValue("C_9", "engname");
		vo.setCaculateflag(UFBoolean.TRUE);
		vo.setCheckflag(UFBoolean.TRUE);
		// vo.setCpaydate(null);
		// vo.setCperiod(null);
		vo.setAttributeValue("currencyrate", 1);
		// vo.setCyear(null);
		vo.setCyearperiod(vo.getCyear() + vo.getCperiod());
		vo.setAttributeValue("D_1", "日期");
		vo.setDerateptg(null);
		vo.setExpense_deduction(UFDouble.ZERO_DBL.setScale(0, 0));
		// 批量期間導入 導入201910 員工郭育培部門應為召幕任用部 但導入時看到員工關心部 by George 20200521 缺陷Bug
		// #35316
		// 批量期間導入 不查詢 组织_部门版本信息(org_dept_v)，在前面代碼先行set了
		if (deptVMap.size() > 0) {
			vo.setFipdeptvid(deptVMap.get(vo.getWorkdept()));
			vo.setLibdeptvid(deptVMap.get(vo.getWorkdept()));
			vo.setWorkdeptvid(deptVMap.get(vo.getWorkdept()));
		}
		vo.setFipendflag(UFBoolean.FALSE);
		vo.setFiporgvid(orgVMap.get(vo.getWorkorg()));
		vo.setIsderate(UFBoolean.FALSE);
		vo.setIsndebuct(UFBoolean.FALSE);
		vo.setIsrulehint(UFBoolean.TRUE);
		vo.setNquickdebuct(UFDouble.ZERO_DBL.setScale(0, 0));
		vo.setPartflag(UFBoolean.FALSE);
		// vo.setPk_bankaccbas1(null);
		// vo.setPk_bankaccbas2(null);
		// vo.setPk_bankaccbas3(null);
		// vo.setPk_banktype1(null);
		// vo.setPk_banktype2(null);
		// vo.setPk_banktype3(null);
		vo.setPk_financedept(vo.getWorkdept());
		vo.setPk_financeorg(vo.getWorkorg());
		// vo.setPk_group(null);
		// vo.setPk_liabilitydept(null);
		// vo.setPk_liabilityorg(null);
		// vo.setPk_org(null);
		// vo.setPk_psndoc(null);
		// vo.setPk_psnjob(null);
		// vo.setPk_psnorg(null);
		// vo.setPk_wa_class(null);
		// vo.setAttributeValue("prewadata", null);
		vo.setRedtotal(UFDouble.ZERO_DBL.setScale(0, 0));
		vo.setStopflag(UFBoolean.FALSE);
		vo.setTaxable_income(UFDouble.ZERO_DBL.setScale(0, 0));
		vo.setTaxrate(UFDouble.ZERO_DBL.setScale(0, 0));
		// vo.setTaxtableid(null);
		vo.setTaxtype(Integer.valueOf(2));
		// vo.setVpaycomment(null);
		// vo.setWorkdept(null);
		// vo.setWorkorg(null);
		vo.setWorkorgvid(orgVMap.get(vo.getWorkorg()));
	}

	protected <T extends SuperVO> Map<String, String> qryVersionPks(Class<T> classz, String pkFieldName, String[] pks,
			String vfieldName) throws BusinessException {
		Map<String, String> result = new HashMap<String, String>();
		StringBuilder condition = new StringBuilder();
		if (!ArrayUtils.isEmpty(pks)) {
			InSQLCreator isc = new InSQLCreator();
			condition.append(pkFieldName).append(" in (").append(isc.getInSQL(pks)).append(") ");
		}
		T[] ts = CommonUtils.retrieveByClause(classz, getWaifsetDao().getBaseDao(), condition.toString());
		if (!ArrayUtils.isEmpty(ts)) {
			for (T t : ts) {
				String key = (String) t.getAttributeValue(pkFieldName);
				String value = (String) t.getAttributeValue(vfieldName);
				result.put(key, value);
			}
		}

		return result;
	}

	@Override
	public Map<Integer, MappingFieldVO[]> qryImpFieldMappingVO(String conditon) throws BusinessException {
		MappingFieldVO[] mappingVos = CommonUtils.retrieveByClause(MappingFieldVO.class, getWaifsetDao().getBaseDao(),
				conditon);
		Map<Integer, MappingFieldVO[]> typeVOMap = CommonUtils.group2ArrayByField(MappingFieldVO.IMPTYPE, mappingVos);
		return typeVOMap;
	}

	protected PeriodStateVO createVOByDataVO(DataVO vo) {
		PeriodStateVO pstateVO = new PeriodStateVO();
		pstateVO.setClasstype(Integer.valueOf(0));
		pstateVO.setPk_group(vo.getPk_group());
		pstateVO.setPk_org(vo.getPk_org());
		pstateVO.setPk_wa_class(vo.getPk_wa_class());
		pstateVO.setPk_wa_period(null);
		pstateVO.setVpaycomment(null);
		pstateVO.setIsapporve(UFBoolean.TRUE);
		pstateVO.setStatus(VOStatus.NEW);
		pstateVO.setPk_periodstate(null);
		setPeriodStateVO(pstateVO, vo.getCpaydate());

		return pstateVO;
	}

	protected void setPeriodStateVO(PeriodStateVO pstateVO, UFDate paydate) {
		pstateVO.setAccountmark(UFBoolean.FALSE);
		pstateVO.setCaculateflag(UFBoolean.TRUE);
		pstateVO.setCheckflag(UFBoolean.TRUE);
		pstateVO.setEnableflag(UFBoolean.TRUE);
		pstateVO.setIsapproved(UFBoolean.FALSE);
		pstateVO.setPayoffflag(UFBoolean.TRUE);
		pstateVO.setCpaydate(paydate);
	}

	protected void dealWithPeriodState(DataVO vo, Map<String, DataVO> cyearperidVOMap) throws BusinessException {
		List<PeriodStateVO> insertStateVOList = new ArrayList<PeriodStateVO>();
		List<PeriodStateVO> updateStateVOList = new ArrayList<PeriodStateVO>();
		Map<String, PeriodStateVO> yearmonthStateVOMap = qryDBPeriodState(vo.getPk_group(), vo.getPk_org(),
				vo.getPk_wa_class());
		if (!MapUtils.isEmpty(cyearperidVOMap)) {
			Map<String, String> cyearperiodPKMap = qryYearmonthPKMap();
			for (String yearmonth : cyearperidVOMap.keySet()) {
				DataVO dVO = cyearperidVOMap.get(yearmonth);
				PeriodStateVO stateVO = yearmonthStateVOMap.get(yearmonth);
				if (null == stateVO) {
					stateVO = createVOByDataVO(dVO);
					if (!MapUtils.isEmpty(cyearperiodPKMap)) {
						stateVO.setPk_wa_period(cyearperiodPKMap.get(yearmonth));
					}
					insertStateVOList.add(stateVO);
				} else {
					setPeriodStateVO(stateVO, dVO.getCpaydate());
					updateStateVOList.add(stateVO);
				}
			}
		}
		if (!insertStateVOList.isEmpty()) {
			getWaifsetDao().getBaseDao().insertVOArray(insertStateVOList.toArray(new PeriodStateVO[0]));
		}
		if (!updateStateVOList.isEmpty()) {
			getWaifsetDao().getBaseDao().updateVOArray(
					updateStateVOList.toArray(new PeriodStateVO[0]),
					new String[] { "accountmark", "caculateflag", "checkflag", "enableflag", "isapproved",
							"payoffflag", "cpaydate" });
		}
	}

	@SuppressWarnings("unchecked")
	protected Map<String, PeriodStateVO> qryDBPeriodState(String pk_group, String pk_org, String pk_wa_class)
			throws BusinessException {
		PeriodVO[] periodVOs = CommonUtils.retrieveByClause(PeriodVO.class, getWaifsetDao().getBaseDao(), null);
		Map<String, PeriodVO> pkPeriodVOMap = CommonUtils.toMap(PeriodVO.PK_WA_PERIOD, periodVOs);

		Map<String, PeriodStateVO> resultMap = new HashMap<String, PeriodStateVO>();
		SqlBuilder sql = new SqlBuilder();
		sql.append("select * from wa_periodstate where 1=1 ");
		sql.append("and pk_group", pk_group);
		sql.append("and pk_org", pk_org);
		sql.append("and pk_wa_class", pk_wa_class);

		List<PeriodStateVO> psvoList = (List<PeriodStateVO>) getWaifsetDao().getBaseDao().executeQuery(sql.toString(),
				new BeanListProcessor(PeriodStateVO.class));
		PeriodStateVO[] pstateVOs = CommonUtils.toArray(PeriodStateVO.class, psvoList);
		Map<String, PeriodStateVO> periodPKStateVOMap = CommonUtils.toMap("pk_wa_period", pstateVOs);
		if (!MapUtils.isEmpty(periodPKStateVOMap)) {
			for (String key : periodPKStateVOMap.keySet()) {
				if (!MapUtils.isEmpty(pkPeriodVOMap) && null != pkPeriodVOMap.get(key)) {
					PeriodVO vo = pkPeriodVOMap.get(key);
					resultMap.put(vo.getCyear() + vo.getCperiod(), periodPKStateVOMap.get(key));
				}
			}
		}

		return resultMap;
	}

	protected Map<String, String> qryYearmonthPKMap() throws BusinessException {
		Map<String, String> yearmonthPKMap = new HashMap<String, String>();
		PeriodVO[] periodVOs = CommonUtils.retrieveByClause(PeriodVO.class, getWaifsetDao().getBaseDao(), null);
		if (!ArrayUtils.isEmpty(periodVOs)) {
			for (PeriodVO vo : periodVOs) {
				yearmonthPKMap.put(vo.getCyear() + vo.getCperiod(), vo.getPk_wa_period());
			}
		}
		return yearmonthPKMap;
	}

	protected Map<String, Map<String, DataItfFileVO>> transferDetailToMap(DataItfFileVO[] detailVos) {
		// <unionID,<plcode,vo>>
		Map<String, Map<String, DataItfFileVO>> unionidPlcodeVOMap = new HashMap<String, Map<String, DataItfFileVO>>();
		for (DataItfFileVO vo : detailVos) {
			String kStr = new StringBuilder(vo.getPk_psndoc()).append(vo.getPk_dept()).append(vo.getCyearperiod())
					.toString();
			Map<String, DataItfFileVO> codeVOMap = unionidPlcodeVOMap.get(kStr);
			if (null == codeVOMap) {
				codeVOMap = new HashMap<String, DataItfFileVO>();
				unionidPlcodeVOMap.put(kStr, codeVOMap);
			}
			codeVOMap.put(vo.getPldecode(), vo);
		}

		return unionidPlcodeVOMap;
	}
	// {薪资、奖金明细代扣项导入} kevin.nie 2018-01-30 end

}
