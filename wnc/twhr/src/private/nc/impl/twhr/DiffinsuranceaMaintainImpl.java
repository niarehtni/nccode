package nc.impl.twhr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.impl.pub.ace.AceDiffinsuranceaPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.itf.twhr.IDiffinsuranceaMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ProcessorUtils;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.twhr.diffinsurance.DiffinsuranceVO;
import nc.vo.twhr.nhicalc.BaoAccountVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

public class DiffinsuranceaMaintainImpl extends AceDiffinsuranceaPubServiceImpl implements IDiffinsuranceaMaintain {
	private BaseDAO dao;

	public BaseDAO getBaseDao() {
		if (null == dao) {
			dao = new BaseDAO();
		}
		return dao;
	}

	@Override
	public DiffinsuranceVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException {
		BatchSaveAction<DiffinsuranceVO> saveAction = new BatchSaveAction<DiffinsuranceVO>();
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		return retData;
	}

	@Override
	public void Blanace(String pk_org, String pk_period) throws BusinessException {
		// Boolean vois = this.voExist(pk_org, pk_period);
		// if (!vois) {

		// }

		// ExceptionUtils.wrappBusinessException("已存在差异分析数据");
		// else {
		// 法人组织
		Map<String, String> pk_legal_orgMap = LegalOrgUtilsEX.getLegalOrgByOrgs(new String[] { pk_org });
		if (pk_legal_orgMap == null || pk_legal_orgMap.get(pk_org) == null) {
			throw new BusinessException("未找到本組織的法人組織!");
		}
		String pk_legal_org = pk_legal_orgMap.get(pk_org);
		// String pk_period1 = pk_period.replace("-", "");
		String sql = "pk_org='" + pk_org + "' and pk_period='" + pk_period + "' and dr=0";
		// 读对账单
		@SuppressWarnings("unchecked")
		List<BaoAccountVO> accVOs = (List<BaoAccountVO>) this.getDao().retrieveByClause(BaoAccountVO.class, sql);
		if (accVOs == null || accVOs.size() < 0) {
			return;
		}
		// 主表人员列表
		Set<String> accPsnSet = new HashSet<>();
		for (BaoAccountVO accVO : accVOs) {
			accPsnSet.add(accVO.getPk_psndoc());
		}
		// 勞健保投保匯總
		sql = "select * from " + PsndocDefTableUtil.getPsnNHISumTablename() + " where pk_hrorg = '" + pk_org
				+ "' and legalpersonorg = '" + pk_legal_org + "' and glbdef1 = '" + pk_period.split("-")[0]
				+ "' and glbdef2 = '" + pk_period.split("-")[1] + "' and dr = 0";
		@SuppressWarnings("unchecked")
		List<PsndocDefVO> totalList = (List<PsndocDefVO>) getBaseDao().executeQuery(sql,
				new BeanListProcessor(PsndocDefTableUtil.getPsnNHISumClass()));
		Map<String, PsndocDefVO> psnDegMap = new HashMap<>();
		// 主表缺少的人员
		Map<String, PsndocDefVO> diffPsnMap = new HashMap<>();
		if (totalList.size() > 0) {
			for (PsndocDefVO vo : totalList) {
				psnDegMap.put(vo.getPk_psndoc(), vo);
				if (!accPsnSet.contains(vo.getPk_psndoc())) {
					diffPsnMap.put(vo.getPrimaryKey(), vo);
				}
			}
		}
		// 补齐缺少的人员
		accVOs = addDiffPsn(accVOs, diffPsnMap, pk_org, pk_period);

		// 健保投保明细(汇总)
		sql = "select pk_psndoc,sum(heainspre) rate from hi_psndoc_headetail "
				+ " where dr = 0 and  legalpersonorg = '" + pk_legal_org + "' ANd heayear = '"
				+ pk_period.split("-")[0] + "' and heamonth = '" + pk_period.split("-")[1] + "' and pk_hrorg = '"
				+ pk_org + "' " + " group by pk_psndoc ";
		@SuppressWarnings("unchecked")
		Map<String, Double> psn2healthMap = (Map<String, Double>) getBaseDao().executeQuery(sql,
				new ResultSetProcessor() {
					private static final long serialVersionUID = 190241326954128264L;
					private Map<String, Double> psnHealthMap = new HashMap<>();

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						while (rs.next()) {
							psnHealthMap.put(rs.getString(1), rs.getDouble(2));
						}
						return psnHealthMap;
					}
				});
		// 称谓关系
		@SuppressWarnings("unchecked")
		List<Map<String, String>> personlist = (List<Map<String, String>>) NCLocator
				.getInstance()
				.lookup(IUAPQueryBS.class)
				.executeQuery(
						" select id idnumber,'本人' name from hi_psndoc_cert " + " union all "
								+ " select idnumber idnumber,doc.name2 name from hi_psndoc_family fa "
								+ " left join bd_defdoc doc on doc.pk_defdoc = fa. mem_relation "
								+ " where fa.idnumber is not null and fa.dr = 0 ", new MapListProcessor());
		Map<String, String> id2TitleMap = new HashMap<>();
		for (Map<String, String> map : personlist) {
			id2TitleMap.put(map.get("idnumber"), map.get("name"));
		}
		// 对账,生成差异分析结果
		List<DiffinsuranceVO> voList = new ArrayList<DiffinsuranceVO>();
		for (BaoAccountVO vo : accVOs) {
			DiffinsuranceVO diffivo = new DiffinsuranceVO();
			// 对应劳健保投保汇总
			PsndocDefVO defVO = psnDegMap.get(vo.getPk_psndoc());
			if (defVO == null) {
				try {
					defVO = (PsndocDefVO) PsndocDefTableUtil.getPsnNHISumClass().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new BusinessException(e);
				}
			}

			/**
			 * 基础数据
			 */
			diffivo.setPk_group(vo.getPk_group());
			diffivo.setPk_org(vo.getPk_org());
			diffivo.setPk_org_v(vo.getPk_org_v());
			diffivo.setPk_period(vo.getPk_period());
			diffivo.setPk_psndoc(vo.getPk_psndoc());
			diffivo.setIdno(vo.getIdno());
			diffivo.setPsnname(vo.getName());
			diffivo.setAppellation(id2TitleMap.get(vo.getIdno()));

			/**
			 * 导入数据
			 */
			// 健保個人承擔金額_對帳單_總額<-健保員工保費
			diffivo.setHeath_psnins_import(vo.getHealth_psnamount());
			// 健保公司承擔金額_對帳單<-健保僱主保費
			diffivo.setHeath_orgins_import(vo.getHealth_orgamount());
			// 勞保個人承擔金額_對帳單<-勞保員工負擔金額
			diffivo.setLabor_psnins_import(vo.getLabor_psnamount());
			// 勞保公司承擔金額_對帳單<-勞保公司負擔金額
			diffivo.setLabor_orgins_import(vo.getLabor_orgamount());
			// 勞退個人承擔金額_對帳單 <-勞退員工自提金額」
			diffivo.setRetire_psnins_import(vo.getRetire_psnamount());
			// 健保費_對帳單
			diffivo.setHeath_sys_ins_import(vo.getHealth_psnamount());

			/**
			 * 系统数据
			 */
			// 健保投保級距
			diffivo.setHeath_grade((UFDouble) defVO.getAttributeValue("glbdef17"));
			// 健保個人承擔金額(系統-總額)
			diffivo.setHeath_psnins((UFDouble) defVO.getAttributeValue("glbdef22"));
			// 健保公司承擔金額(系統)
			diffivo.setHeath_orgins((UFDouble) defVO.getAttributeValue("glbdef20"));
			// 勞保投保級距
			diffivo.setLabor_grade((UFDouble) defVO.getAttributeValue("glbdef4"));
			// 勞保個人承擔金額(系統)
			diffivo.setLabor_psnins((UFDouble) defVO.getAttributeValue("glbdef12"));
			// 勞保公司承擔金額(系統)
			diffivo.setLabor_orgins((UFDouble) defVO.getAttributeValue("glbdef13"));
			// 勞退投保級距
			diffivo.setRetire_grade((UFDouble) defVO.getAttributeValue("glbdef14"));
			// 勞退個人承擔金額(系統)
			diffivo.setRetire_psnins((UFDouble) defVO.getAttributeValue("glbdef15"));

			// 健保費(系統)
			Double healthIns = psn2healthMap.get(vo.getPk_psndoc());
			diffivo.setHeath_sys_ins(healthIns == null ? UFDouble.ZERO_DBL : new UFDouble(healthIns));

			/**
			 * 计算差异
			 */
			// 補扣健保個人承擔金額 = 健保個人承擔金額(對帳單-總額) 減 健保個人承擔金額(系統-總額) PS為負數時則等於 0
			diffivo.setHeath_suppleins(getResultZero(diffivo.getHeath_psnins_import(), diffivo.getHeath_psnins()));
			// 退還健保個人承擔金額 = 健保個人承擔金額(對帳單-總額) 減 健保個人承擔金額(系統-總額) PS絕對值
			diffivo.setHeath_returnnins(getResultZero(diffivo.getHeath_psnins(), diffivo.getHeath_psnins_import()));
			// 補扣健保費 = 健保費(對帳單) 減 健保費(系統) PS 為負數時則等於 0
			diffivo.setHeath_sys_suppleins(getResultZero(diffivo.getHeath_sys_ins(), diffivo.getHeath_sys_ins_import()));
			// 退還健保費=健保費(對帳單) 減 健保費(系統) PS 絕對值
			diffivo.setHeath_sys_returnins(getResultZero(diffivo.getHeath_sys_ins_import(), diffivo.getHeath_sys_ins()));
			// 補扣勞保個人承擔金額 = 勞保個人承擔金額(對帳單)減 勞保個人承擔金額(系統) PS為負數時則等於 0
			diffivo.setLabor_suppleins(getResultZero(diffivo.getLabor_psnins_import(), diffivo.getLabor_psnins()));
			// 退還勞保個人承擔金額 = 勞保個人承擔金額(對帳單) 減 勞保個人承擔金額(系統) PS 絕對值
			diffivo.setLabor_returnnins(getResultZero(diffivo.getLabor_psnins(), diffivo.getLabor_psnins_import()));
			// 補扣勞退個人承擔金額 = 勞保個人承擔金額(對帳單)減 勞保個人承擔金額(系統) PS為負數時則等於 0
			diffivo.setRetire_suppleins(getResultZero(diffivo.getRetire_psnins_import(), diffivo.getRetire_psnins()));
			// 退還勞退個人承擔金額 = 勞保個人承擔金額(對帳單) 減 勞保個人承擔金額(系統) PS绝对值
			diffivo.setRetire_returnnins(getResultZero(diffivo.getRetire_psnins(), diffivo.getRetire_psnins_import()));

			diffivo.setDr(0);
			voList.add(diffivo);
		}

		// 4. 保存差异单
		for (DiffinsuranceVO vo : voList) {
			dao.insertVO(vo);
		}
	}

	/**
	 * 将汇总记录作为对比的一部分
	 * 
	 * @param accVOs
	 * @param diffPsnMap
	 *            <pk_psndoc,劳健保汇总记录>
	 * @param pk_period
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	private List<BaoAccountVO> addDiffPsn(List<BaoAccountVO> accVOs, Map<String, PsndocDefVO> diffPsnMap,
			String pk_org, String pk_period) throws BusinessException {
		if (diffPsnMap != null && diffPsnMap.size() > 0) {
			Set<String> keySet = diffPsnMap.keySet();
			// 查询相关信息
			InSQLCreator insql = new InSQLCreator();
			String pkInsql = insql.getInSQL(keySet.toArray(new String[0]));
			String sql = "select DISTINCT psn.pk_psndoc pk_psndoc,psn.name2 name,cert.id id,org.pk_group pk_group,org.pk_vid pk_org_v "
					+ " from "
					+ PsndocDefTableUtil.getPsnNHISumTablename()
					+ " g4 "
					+ " left join bd_psndoc psn on g4.pk_psndoc = psn.pk_psndoc  "
					+ " left join hi_psndoc_cert cert on (cert.pk_psndoc = psn.pk_psndoc and cert.dr = 0)  "
					+ " inner join bd_psnidtype type on (type.dr = 0 and type.pk_identitype = cert.idtype and type.code = 'TW01')  "
					+ " left join org_orgs org on org.pk_org = '"
					+ pk_org
					+ "'  where g4.pk_psndoc_sub in ("
					+ pkInsql
					+ ")";
			@SuppressWarnings("unchecked")
			Map<String, Map<String, Object>> psnInfoMap = (Map<String, Map<String, Object>>) getBaseDao().executeQuery(
					sql, new ResultSetProcessor() {
						private static final long serialVersionUID = 1L;
						Map<String, Map<String, Object>> rsMap = new HashMap<>();

						@Override
						public Object handleResultSet(ResultSet rs) throws SQLException {
							while (rs.next()) {
								rsMap.put(rs.getString(1), ProcessorUtils.toMap(rs));
							}
							return rsMap;
						}
					});
			for (String pk_def : keySet) {
				BaoAccountVO diffivo = new BaoAccountVO();
				// 对应劳健保投保汇总
				PsndocDefVO defVO = diffPsnMap.get(pk_def);

				if (defVO == null) {
					try {
						defVO = (PsndocDefVO) PsndocDefTableUtil.getPsnNHISumClass().newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						throw new BusinessException(e);
					}
				}
				/**
				 * 基础数据
				 */
				// 数据
				Map<String, Object> dateMap = psnInfoMap.get(defVO.getPk_psndoc());
				if (dateMap == null) {
					dateMap = new HashMap<>();
				}
				diffivo.setPk_group((String) dateMap.get("pk_group"));
				diffivo.setPk_org(pk_org);
				diffivo.setPk_org_v((String) dateMap.get("pk_vid"));
				diffivo.setPk_period(pk_period);
				diffivo.setPk_psndoc(defVO.getPk_psndoc());
				diffivo.setIdno((String) dateMap.get("id"));
				diffivo.setName((String) dateMap.get("name"));
				// diffivo.setAppellation("本人");
				accVOs.add(diffivo);
			}
		}
		return accVOs;
	}

	/**
	 * 两数相减的绝对值
	 * 
	 * @param minuend
	 *            被减数
	 * @param subtrahend
	 *            减数
	 * @return
	 */
	private UFDouble getResultAbs(UFDouble minuend, UFDouble subtrahend) {
		if (minuend == null) {
			minuend = UFDouble.ZERO_DBL;
		}
		if (subtrahend == null) {
			subtrahend = UFDouble.ZERO_DBL;
		}
		return minuend.sub(subtrahend).abs();
	}

	/**
	 * 两数相减,为负数则为0
	 * 
	 * @param minuend
	 *            被减数
	 * @param subtrahend
	 *            减数
	 * @return
	 */
	private UFDouble getResultZero(UFDouble minuend, UFDouble subtrahend) {
		if (minuend == null) {
			minuend = UFDouble.ZERO_DBL;
		}
		if (subtrahend == null) {
			subtrahend = UFDouble.ZERO_DBL;
		}
		UFDouble rs = minuend.sub(subtrahend);
		if (rs.compareTo(UFDouble.ZERO_DBL) < 0) {
			rs = UFDouble.ZERO_DBL;
		}
		return rs;
	}

	public BaseDAO getDao() {
		if (dao == null) {
			dao = new BaseDAO();
		}
		return dao;
	}

	// 删除数据
	@Override
	public void Delete(String pk_org, String pk_period) throws BusinessException {

		String deletesql = "delete  from twhr_diffinsurance where pk_org='" + pk_org + "' and pk_period='" + pk_period
				+ "'";
		dao.executeUpdate(deletesql);
	}

	@Override
	public Boolean ifexist(String pk_org, String pk_period) throws BusinessException {
		String isexistsql = "select count(*) from twhr_diffinsurance where pk_org='" + pk_org + "' and pk_period='"
				+ pk_period + "'";
		Integer wacount = (Integer) this.getDao().executeQuery(isexistsql, new ColumnProcessor());
		if (wacount > 0) {
			return true;
		}

		return false;
	}

	/*
	 * @Override public ISuperVO[] queryByDataVisibilitySetting(LoginContext
	 * arg0, Class<? extends ISuperVO> arg1) throws BusinessException { // TODO
	 * Auto-generated method stub return null; }
	 */
	@Override
	public DiffinsuranceVO[] selectByWhereSql(String sql, Class<? extends ISuperVO> vo) throws BusinessException {
		List<DiffinsuranceVO> list = (List<DiffinsuranceVO>) this.getBaseDao().retrieveByClause(vo, "1=1 " + sql);
		return list.toArray(new DiffinsuranceVO[0]);
	}

}
