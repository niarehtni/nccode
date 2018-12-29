package nc.impl.twhr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.hr.utils.InSQLCreator;
import nc.impl.pub.ace.AceDiffinsuranceaPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.itf.twhr.IDiffinsuranceaMaintain;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.twhr.diffinsurance.DiffinsuranceVO;
import nc.vo.twhr.nhicalc.BaoAccountVO;

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
		String pk_period1 = pk_period.replace("-", "");
		String sql = "pk_org='" + pk_org + "' and pk_period='" + pk_period + "' and dr=0";
		// 1. 读对账单
		Collection<BaoAccountVO> accVOs = this.getDao().retrieveByClause(BaoAccountVO.class, sql);
		String sql2 = "select wa_data.pk_psndoc,isnull( SALARY_DECRYPT(max(f_22)),0) as f_22 ,isnull( SALARY_DECRYPT(sum(f_23)),0) as f_23,isnull( SALARY_DECRYPT(sum(f_24)),0) as f_24,isnull( SALARY_DECRYPT(max(f_27)),0) as f_27 ,isnull( SALARY_DECRYPT(max(f_28)),0) as f_28,isnull( SALARY_DECRYPT(sum(f_29)),0) as f_29,isnull( SALARY_DECRYPT(max(f_46)),0) as f_46,isnull( SALARY_DECRYPT(sum(f_47)),0) as f_47,isnull( SALARY_DECRYPT(sum(f_50)),0) as f_50,bd_psndoc.id idno, wa_data.pk_org, wa_data.pk_group"
				+ " from wa_data wa_data inner join bd_psndoc bd_psndoc on wa_data.PK_PSNDOC=bd_psndoc.PK_PSNDOC"
				+ " where  wa_data.cyearperiod='"
				+ pk_period1
				+ "' and wa_data.pk_org='"
				+ pk_org
				+ "'  group  by id, wa_data.pk_org, wa_data.pk_group,wa_data.pk_psndoc";
		// 2. 读薪资数据
		List<Map> waDatas = (List<Map>) this.getDao().executeQuery(sql2, new MapListProcessor());
		// 读取劳健退子集的数据，判断是否有投保记录
		List<String> pk_psndocs = new ArrayList<String>();
		for (Map maps : waDatas) {
			pk_psndocs.add(String.valueOf(maps.get("pk_psndoc")));
		}
		Map<String, String> recodermap = getrecoder(pk_psndocs);
		// 3. 对账,生成差异分析结果
		List<DiffinsuranceVO> voList = new ArrayList<DiffinsuranceVO>();
		for (BaoAccountVO vo : accVOs) {
			/*
			 * DiffinsuranceVO diffivo = new DiffinsuranceVO();
			 * diffivo.setDr(0); diffivo.setPk_period(vo.getPk_period());
			 * diffivo.setPk_org(vo.getPk_org()); diffivo.setName(vo.getName());
			 */
			Map wadata = this.getWaDataByID(waDatas, vo.getIdno());
			// 劳保费比较
			if (null != vo.getLaborid()) {
				DiffinsuranceVO diffivo = new DiffinsuranceVO();
				diffivo.setDr(0);
				diffivo.setPk_period(vo.getPk_period());
				diffivo.setPk_org(vo.getPk_org());
				diffivo.setName(vo.getName());
				diffivo.setIdno(vo.getLaborid());
				// 对比类型
				diffivo.setIchecktype(1);
				if (null != wadata && wadata.size() > 0) {
					diffivo.setPk_psndoc(String.valueOf(wadata.get("pk_psndoc")));
					// 劳保级距投保单位
					diffivo.setOrg_amount(null == wadata.get("f_22") ? UFDouble.ZERO_DBL : new UFDouble(String
							.valueOf(wadata.get("f_22"))));
					// 工负担投保单位
					diffivo.setOrg_psnamount(null == wadata.get("f_46") ? UFDouble.ZERO_DBL : new UFDouble(String
							.valueOf(wadata.get("f_46"))));
					// 雇主负担投保单位
					diffivo.setOrg_orgamount(null == wadata.get("f_27") ? UFDouble.ZERO_DBL : new UFDouble(String
							.valueOf(wadata.get("f_27"))));
				}
				// 级距对账单
				diffivo.setDiff_amount(vo.getLabor_amount());
				//
				diffivo.setCheck_orgamount(vo.getLabor_orgamount());
				diffivo.setCheck_psnamount(vo.getLabor_psnamount());
				// 差异金额员工
				if (null != diffivo.getCheck_psnamount()) {
					diffivo.setDiff_psnamount(diffivo.getCheck_psnamount().sub(
							diffivo.getOrg_psnamount() == null ? UFDouble.ZERO_DBL : diffivo.getOrg_psnamount()));
				} else {
					diffivo.setDiff_psnamount(UFDouble.ZERO_DBL.sub(diffivo.getOrg_psnamount() == null ? UFDouble.ZERO_DBL
							: diffivo.getOrg_psnamount()));
				}
				// 差异金额雇主
				if (null != diffivo.getCheck_orgamount()) {
					diffivo.setDiff_orgamount(diffivo.getCheck_orgamount().sub(
							diffivo.getOrg_orgamount() == null ? UFDouble.ZERO_DBL : diffivo.getOrg_orgamount()));
				} else {
					diffivo.setDiff_orgamount(UFDouble.ZERO_DBL.sub(diffivo.getOrg_orgamount() == null ? UFDouble.ZERO_DBL
							: diffivo.getOrg_orgamount()));
				}
				// 投保记录
				UFBoolean flag = IsInsurance(vo.getIdno(), recodermap, "labor");
				// 差异原因
				// 无此员工
				if (null == wadata) {
					DiffinsuranceVO diffivono = (DiffinsuranceVO) diffivo.clone();
					diffivono.setName(vo.getName());
					diffivono.setIdno(vo.getLaborid());
					diffivono.setIdifftype(4);
					voList.add(diffivono);
					// 无投保记录
				} else if (!flag.booleanValue()) {
					DiffinsuranceVO diffivoinsurance = (DiffinsuranceVO) diffivo.clone();
					diffivoinsurance.setIdifftype(4);
					voList.add(diffivoinsurance);
				} else {
					// 金额不符
					if (diffivo.getDiff_orgamount().doubleValue() != 0
							&& diffivo.getDiff_psnamount().doubleValue() != 0) {
						DiffinsuranceVO diffivoamount = (DiffinsuranceVO) diffivo.clone();
						diffivoamount.setIdifftype(3);
						voList.add(diffivoamount);
					}
					// 级距不符
					if (diffivo.getCheck_orgamount()
							.sub(diffivo.getDiff_amount() == null ? UFDouble.ZERO_DBL : diffivo.getDiff_amount())
							.doubleValue() != 0) {
						DiffinsuranceVO diffivoSpacing = (DiffinsuranceVO) diffivo.clone();
						diffivoSpacing.setIdifftype(2);
						voList.add(diffivoSpacing);
					}

				}

			}
			// 健保费对账
			if (null != vo.getHealthid()) {
				DiffinsuranceVO diffivo = new DiffinsuranceVO();
				diffivo.setDr(0);
				diffivo.setPk_period(vo.getPk_period());
				diffivo.setPk_org(vo.getPk_org());
				diffivo.setName(vo.getName());
				diffivo.setIdno(vo.getHealthid());
				// 对比类型
				diffivo.setIchecktype(3);
				// 投保记录
				UFBoolean flag = IsInsurance(vo.getIdno(), recodermap, "health");
				if (null != wadata && wadata.size() > 0) {
					diffivo.setPk_psndoc(String.valueOf(wadata.get("pk_psndoc")));
					// 劳保级距投保单位
					diffivo.setOrg_amount(null == wadata.get("f_23") ? UFDouble.ZERO_DBL : new UFDouble(String
							.valueOf(wadata.get("f_23"))));
					// 工负担投保单位
					diffivo.setOrg_psnamount(null == wadata.get("f_47") ? UFDouble.ZERO_DBL : new UFDouble(String
							.valueOf(wadata.get("f_47"))));
					// 雇主负担投保单位
					diffivo.setOrg_orgamount(null == wadata.get("f_28") ? UFDouble.ZERO_DBL : new UFDouble(String
							.valueOf(wadata.get("f_28"))));
				}
				// 级距对账单
				diffivo.setDiff_amount(vo.getHealth_amount());
				//
				diffivo.setCheck_orgamount(vo.getHealth_orgamount());
				diffivo.setCheck_psnamount(vo.getHealth_psnamount());
				// 差异金额员工
				if (null != diffivo.getCheck_psnamount()) {
					diffivo.setDiff_psnamount(diffivo.getCheck_psnamount().sub(
							diffivo.getOrg_psnamount() == null ? UFDouble.ZERO_DBL : diffivo.getOrg_psnamount()));
				} else {
					diffivo.setDiff_psnamount(UFDouble.ZERO_DBL.sub(diffivo.getOrg_psnamount() == null ? UFDouble.ZERO_DBL
							: diffivo.getOrg_psnamount()));
				}
				// 差异金额雇主
				if (null != diffivo.getCheck_orgamount()) {
					diffivo.setDiff_orgamount(diffivo.getCheck_orgamount().sub(
							diffivo.getOrg_orgamount() == null ? UFDouble.ZERO_DBL : diffivo.getOrg_orgamount()));
				} else {
					diffivo.setDiff_orgamount(UFDouble.ZERO_DBL.sub(diffivo.getOrg_orgamount() == null ? UFDouble.ZERO_DBL
							: diffivo.getOrg_orgamount()));
				}
				// 差异原因
				// 无此员工
				if (null == wadata) {
					DiffinsuranceVO diffivono = (DiffinsuranceVO) diffivo.clone();
					diffivono.setName(vo.getName());
					diffivono.setIdno(vo.getHealthid());
					diffivono.setIdifftype(4);
					voList.add(diffivono);
					// 无投保记录
				} else if (!flag.booleanValue()) {
					DiffinsuranceVO diffivoinsurance = (DiffinsuranceVO) diffivo.clone();
					diffivoinsurance.setIdifftype(4);
					voList.add(diffivoinsurance);
				} else {
					// 金额不符
					if (diffivo.getDiff_orgamount().doubleValue() != 0
							&& diffivo.getDiff_psnamount().doubleValue() != 0) {
						DiffinsuranceVO diffivoamount = (DiffinsuranceVO) diffivo.clone();
						diffivoamount.setIdifftype(3);
						voList.add(diffivoamount);
					}
					// 级距不符
					if (diffivo.getCheck_orgamount()
							.sub(diffivo.getDiff_amount() == null ? UFDouble.ZERO_DBL : diffivo.getDiff_amount())
							.doubleValue() != 0) {
						DiffinsuranceVO diffivoSpacing = (DiffinsuranceVO) diffivo.clone();
						diffivoSpacing.setIdifftype(2);
						voList.add(diffivoSpacing);
					}

				}
			}
			// 劳退费对账
			if (null != vo.getRetiredid()) {
				DiffinsuranceVO diffivo = new DiffinsuranceVO();
				diffivo.setDr(0);
				diffivo.setPk_period(vo.getPk_period());
				diffivo.setPk_org(vo.getPk_org());
				diffivo.setName(vo.getName());
				diffivo.setIdno(vo.getRetiredid());
				// 对比类型
				diffivo.setIchecktype(2);
				// 投保记录
				UFBoolean flag = IsInsurance(vo.getIdno(), recodermap, "retire");
				if (null != wadata && wadata.size() > 0) {
					diffivo.setPk_psndoc(String.valueOf(wadata.get("pk_psndoc")));
					// 劳保级距投保单位
					diffivo.setOrg_amount(null == wadata.get("f_24") ? UFDouble.ZERO_DBL : new UFDouble(String
							.valueOf(wadata.get("f_24"))));
					// 工负担投保单位
					diffivo.setOrg_psnamount(null == wadata.get("f_50") ? UFDouble.ZERO_DBL : new UFDouble(String
							.valueOf(wadata.get("f_50"))));
					// 雇主负担投保单位
					diffivo.setOrg_orgamount(null == wadata.get("f_29") ? UFDouble.ZERO_DBL : new UFDouble(String
							.valueOf(wadata.get("f_29"))));
				}
				// 级距对账单
				diffivo.setDiff_amount(vo.getRetire_amount());
				//
				diffivo.setCheck_orgamount(vo.getRetire_orgamount());
				diffivo.setCheck_psnamount(vo.getRetire_psnamount());
				// 差异金额员工
				if (null == diffivo.getCheck_psnamount()) {
					diffivo.setDiff_psnamount(UFDouble.ZERO_DBL.sub(diffivo.getOrg_psnamount() == null ? UFDouble.ZERO_DBL
							: diffivo.getOrg_psnamount()));
				} else {
					diffivo.setDiff_psnamount(diffivo.getCheck_psnamount().sub(
							diffivo.getOrg_psnamount() == null ? UFDouble.ZERO_DBL : diffivo.getOrg_psnamount()));
				}
				// 差异金额雇主
				if (null != diffivo.getCheck_orgamount()) {
					diffivo.setDiff_orgamount(diffivo.getCheck_orgamount().sub(
							diffivo.getOrg_orgamount() == null ? UFDouble.ZERO_DBL : diffivo.getOrg_orgamount()));
				} else {
					diffivo.setDiff_orgamount(UFDouble.ZERO_DBL.sub(diffivo.getOrg_orgamount() == null ? UFDouble.ZERO_DBL
							: diffivo.getOrg_psnamount() == null ? UFDouble.ZERO_DBL : diffivo.getOrg_psnamount()));
				}

				// 差异原因
				// 无此员工
				if (null == wadata) {
					DiffinsuranceVO diffivono = (DiffinsuranceVO) diffivo.clone();
					diffivono.setName(vo.getName());
					diffivono.setIdno(vo.getRetiredid());
					diffivono.setIdifftype(4);
					voList.add(diffivono);
					// 无投保记录
				} else if (!flag.booleanValue()) {
					DiffinsuranceVO diffivoinsurance = (DiffinsuranceVO) diffivo.clone();
					diffivoinsurance.setIdifftype(4);
					voList.add(diffivoinsurance);
				} else {
					// 金额不符
					if (diffivo.getDiff_orgamount().doubleValue() != 0
							&& diffivo.getDiff_psnamount().doubleValue() != 0) {
						DiffinsuranceVO diffivoamount = (DiffinsuranceVO) diffivo.clone();
						diffivoamount.setIdifftype(3);
						voList.add(diffivoamount);
					}
					// 级距不符
					if (diffivo.getCheck_orgamount()
							.sub(diffivo.getDiff_amount() == null ? UFDouble.ZERO_DBL : diffivo.getDiff_amount())
							.doubleValue() != 0) {
						DiffinsuranceVO diffivoSpacing = (DiffinsuranceVO) diffivo.clone();
						diffivoSpacing.setIdifftype(2);
						voList.add(diffivoSpacing);
					}

				}
			}

		}

		// 4. 保存差异单
		dao.insertVOList(voList);

		// 5 刷新在前台
	}

	private UFBoolean IsInsurance(String idno, Map<String, String> recodermap, String form) {
		UFBoolean healthinsurance = UFBoolean.FALSE;
		UFBoolean laborinsurance = UFBoolean.FALSE;
		UFBoolean retireinsurance = UFBoolean.FALSE;
		for (String map : recodermap.keySet()) {
			// 劳保
			if (map.split(":")[1].equals("labor")) {
				if (null != idno && idno.equals(map.split(":")[0])) {
					laborinsurance = recodermap.get(map) == null ? UFBoolean.FALSE : new UFBoolean(recodermap.get(map));
				}
			}
			// 健保
			if (null != idno && map.split(":")[1].equals("health")) {
				if (idno.equals(map.split(":")[0])) {
					healthinsurance = recodermap.get(map) == null ? UFBoolean.FALSE
							: new UFBoolean(recodermap.get(map));
				}
			}
			// 劳退
			if (null != idno && map.split(":")[1].equals("retire")) {
				if (idno.equals(map.split(":")[0])) {
					retireinsurance = recodermap.get(map) == null ? UFBoolean.FALSE
							: new UFBoolean(recodermap.get(map));
				}
			}

		}
		if (form.equals("labor")) {
			return laborinsurance;
		} else if (form.equals("health")) {
			return healthinsurance;
		} else {
			return retireinsurance;
		}
	}

	private Map<String, String> getrecoder(List<String> pk_psndocs) {
		Map<String, String> maps = new HashMap<String, String>();
		// 查询两个子集
		InSQLCreator insql = new InSQLCreator();
		// IUAPQueryBS iUAPQueryBS =
		// (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		try {
			// 劳保
			List<Map<String, String>> laborlist = (List<Map<String, String>>) this
					.getBaseDao()
					.executeQuery(
							"select hg.glbdef10,hg.glbdef11,bp.id from HI_PSNDOC_GLBDEF1 hg ,bd_psndoc bp where hg.pk_psndoc=bp.pk_psndoc and hg.pk_psndoc in("
									+ insql.getInSQL(pk_psndocs.toArray(new String[0])) + ") and hg.dr <> 1;",
							new MapListProcessor());
			// 健保
			List<Map<String, String>> healthlist = (List<Map<String, String>>) this.getBaseDao().executeQuery(
					"select hg.glbdef14,bp.id from HI_PSNDOC_GLBDEF2 hg ,bd_psndoc bp where hg.pk_psndoc=bp.pk_psndoc and hg.pk_psndoc in("
							+ insql.getInSQL(pk_psndocs.toArray(new String[0])) + ") and hg.dr <> 1;",
					new MapListProcessor());

			for (Map<String, String> healthvo : healthlist) {
				if (maps.size() <= 0 || null == maps.get(healthvo.get("id") + ":health")
						|| !maps.get(healthvo.get("id") + ":health").equals("Y")) {
					maps.put(healthvo.get("id") + ":health",
							healthvo.get("glbdef14") == null ? "N" : String.valueOf(healthvo.get("glbdef14")));
				}
			}
			for (Map<String, String> laborvo : laborlist) {
				if (maps.size() <= 0 || null == maps.get(laborvo.get("id") + ":labor")
						|| !maps.get(laborvo.get("id") + ":labor").equals("Y")) {
					maps.put(laborvo.get("id") + ":labor",
							laborvo.get("glbdef10") == null ? "N" : String.valueOf(laborvo.get("glbdef10")));
				}
				if (maps.size() <= 0 || null == maps.get(laborvo.get("id") + ":retire")
						|| !maps.get(laborvo.get("id") + ":retire").equals("Y")) {
					maps.put(laborvo.get("id") + ":retire",
							laborvo.get("glbdef11") == null ? "N" : String.valueOf(laborvo.get("glbdef11")));
				}
			}
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maps;
	}

	// }
	private boolean idExist(Object idno, Collection<BaoAccountVO> accVOs) {
		for (BaoAccountVO vo : accVOs) {
			String idnos = idno.toString();
			String idnoacc = vo.getIdno().toString();
			if (idnos.equals(idnoacc)) {
				return true;
			}
		}
		return false;
	}

	private boolean voExist(String pk_org, String pk_period) {

		String sql4 = "select * from twhr_diffinsurance where pk_org='" + pk_org + "' and pk_period='" + pk_period
				+ "'";

		try {
			List<Map> waDatas = (List<Map>) this.getDao().executeQuery(sql4, new MapListProcessor());
			if (waDatas.size() == 0) {
				return true;

			}
		} catch (DAOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		return false;
	}

	// 页面上的身份证
	private Map getWaDataByID(List<Map> waDatas, String idno) {
		if (null == idno) {
			return null;
		}
		for (Map data : waDatas) {
			if (idno.equals(data.get("idno"))) {
				return data;
			}
		}
		return null;
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

		// TODO 自动生成的方法存根

	}

	@Override
	public Boolean ifexist(String pk_org, String pk_period) throws BusinessException {
		String isexistsql = "select * from twhr_diffinsurance where pk_org='" + pk_org + "' and pk_period='"
				+ pk_period + "'";
		List<Map> waDatas = (List<Map>) this.getDao().executeQuery(isexistsql, new MapListProcessor());
		if (waDatas.size() == 0) {
			return true;
		}

		// TODO 自动生成的方法存根
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
