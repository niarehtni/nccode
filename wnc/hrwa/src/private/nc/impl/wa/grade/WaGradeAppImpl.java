package nc.impl.wa.grade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import nc.bs.bd.cache.CacheProxy;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IWaGradeService;
import nc.jdbc.framework.SQLParameter;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.wa.adjust.AdjustWadocVO;
import nc.vo.wa.adjust.PsnappaproveBVO;
import nc.vo.wa.grade.AggWaGradeVO;
import nc.vo.wa.grade.CrtVO;
import nc.vo.wa.grade.IWaGradeCommonDef;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeComparator;
import nc.vo.wa.grade.WaGradeVO;
import nc.vo.wa.grade.WaGradeVerVO;
import nc.vo.wa.grade.WaPrmlvVO;
import nc.vo.wa.grade.WaPsnhiBVO;
import nc.vo.wa.grade.WaPsnhiVO;
import nc.vo.wa.grade.WaSeclvVO;
import nc.vo.wa.grade.WaStdHiSetVO;
import nc.vo.wabm.util.WaCacheUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 薪资标准设置
 * 
 * @author: xuhw
 * @date: 2009-11-12 下午01:56:16
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaGradeAppImpl implements IWaGradeService {
	private final String DOC_NAME = "grade";
	private WaGradeDAO gradeDao;
	private SimpleDocServiceTemplate serviceTemplate;
	/** 保存新增的级别 */
	private List<String> lisNewPrmlvVoFlag = new ArrayList<String>();
	/** 保存新增的档别 */
	private List<String> lisNewSeclvVoFlag = new ArrayList<String>();
	/** 保存删除的级别 */
	private List<String> lisDelPrmlvVoFlag = new ArrayList<String>();
	/** 保存删除的档别 */
	private List<String> lisDelSeclvVoFlag = new ArrayList<String>();
	/** 类别主键 */
	String strGrdpk = null;
	/** 是否多档 */
	boolean blnIsMul = false;
	List<WaCriterionVO> lisCrtVOs = null;

	/***************************************************************************
	 * <br>
	 * Created on 2012-2-5 11:09:54<br>
	 * 
	 * @param docValidatorFactory
	 * @author daicy
	 ***************************************************************************/
	public void setValidatorFactory(nc.hr.frame.persistence.IValidatorFactory docValidatorFactory) {
		getServiceTemplate().setValidatorFactory(docValidatorFactory);
	}

	/**
	 * 删除薪资标准设置表<BR>
	 * 删除之前要进行是否被应用得校验，如果被引用则不能被删除<BR>
	 * 
	 * @author xuhw on 2009-11-12
	 */
	@Override
	public void deleteWaGradeVO(AggWaGradeVO billVO) throws BusinessException {
		// 需要校验是被下面三张表引用
		// 薪资标准表
		// 人员薪资等级表
		// 定调资
		String strPKGrd = billVO.getParentVO().getPk_wa_grd();
		validateGradeHaveReference(strPKGrd);
		// // 级别
		// WaPrmlvVO[] prmlvvos = (WaPrmlvVO[])
		// billVO.getTableVO(IWaGradeCommonDef.WA_PRMLV);
		// // 档别
		// WaSeclvVO[] seclvvos = (WaSeclvVO[])
		// billVO.getTableVO(IWaGradeCommonDef.WA_SECLV);
		//
		// if (prmlvvos != null)
		// {
		// for (WaPrmlvVO prmlvvo : prmlvvos)
		// {
		// // 校验要被删除的级别是否被引用
		// this.validatePrmlvHaveRefresh(prmlvvo);
		// }
		// }
		// if (seclvvos != null)
		// {
		// for (WaSeclvVO seclvvo : seclvvos)
		// {
		// // 校验要被删除的级别是否被引用
		// this.validateSeclvHaveRefresh(seclvvo);
		// }
		// }

		String psnhi_b = "select pk_wa_psnhi_b from wa_psnhi_b where pk_wa_grdlv in (select pk_wa_grdlv from wa_psnhi_b where pk_wa_psnhi in "
				+ "(select pk_wa_psnhi from wa_psnhi where pk_wa_grd = '" + strPKGrd + "' ) )";

		// zhoumxc 同步缓存 20140902
		CacheProxy.fireDataDeletedByWhereClause("wa_grade_ver", "pk_wa_gradever",
				"pk_wa_gradever in (select pk_wa_gradever from wa_grade_ver where pk_wa_grd ='" + strPKGrd + "')");
		// CacheProxy.fireDataDeletedByWhereClause(WaPsnhiBVO.TABLENAME,
		// "pk_wa_psnhi_b","pk_wa_psnhi_b in (select pk_wa_psnhi_b from wa_psnhi_b where pk_wa_psnhi  in ( select pk_wa_psnhi from wa_psnhi where pk_wa_grd = '"+strPKGrd+"'))"
		// );
		CacheProxy.fireDataDeletedByWhereClause(WaPsnhiBVO.TABLENAME, "pk_wa_psnhi_b", "pk_wa_psnhi_b in (" + psnhi_b
				+ ")");
		CacheProxy.fireDataDeletedByWhereClause(WaPsnhiVO.TABLENAME, "pk_wa_psnhi",
				"pk_wa_psnhi in (select pk_wa_psnhi from wa_psnhi where pk_wa_grd = '" + strPKGrd + "')");
		CacheProxy.fireDataDeletedByWhereClause(WaPrmlvVO.TABLENAME, "pk_wa_prmlv",
				"pk_wa_prmlv in (select pk_wa_prmlv from wa_prmlv where pk_wa_grd ='" + strPKGrd + "')");
		CacheProxy.fireDataDeletedByWhereClause(WaSeclvVO.TABLENAME, "pk_wa_seclv",
				"pk_wa_seclv in (select pk_wa_seclv from wa_seclv where pk_wa_grd ='" + strPKGrd + "')");
		CacheProxy.fireDataDeleted(WaGradeVO.TABLENAME, strPKGrd);

		this.getWaGradeDao().getBaseDao().deleteByClause(WaGradeVerVO.class, "pk_wa_grd ='" + strPKGrd + "'");
		this.getWaGradeDao().getBaseDao().deleteByClause(WaCriterionVO.class, "pk_wa_grd ='" + strPKGrd + "'");
		// this.getWaGradeDao().deletePsnHiBByGradePK(strPKGrd);
		this.getWaGradeDao().getBaseDao().deleteByClause(WaPsnhiBVO.class, "pk_wa_psnhi_b in (" + psnhi_b + ")");
		this.getWaGradeDao().getBaseDao().deleteByClause(WaPsnhiVO.class, "pk_wa_grd ='" + strPKGrd + "'");
		getServiceTemplate().delete(billVO);

		// 同步缓存 有问题，要在删除之前清缓存 zhoumxc
		// CacheProxy.fireDataDeleted(WaPsnhiBVO.TABLENAME,
		// "pk_wa_psnhi in ( select pk_wa_psnhi from wa_psnhi where pk_wa_grd = '"+strPKGrd+"')"
		// );
		// CacheProxy.fireDataDeleted(WaPsnhiVO.TABLENAME, "pk_wa_grd ='" +
		// strPKGrd + "'");
		// CacheProxy.fireDataDeleted(WaPrmlvVO.TABLENAME, "pk_wa_grd ='" +
		// strPKGrd + "'");
		// CacheProxy.fireDataDeleted(WaSeclvVO.TABLENAME, "pk_wa_grd ='" +
		// strPKGrd + "'");
		// CacheProxy.fireDataDeleted(WaGradeVO.TABLENAME, strPKGrd);
		// WaCacheUtils.synCache(WaPsnhiBVO.TABLENAME,WaPsnhiVO.TABLENAME,WaPrmlvVO.TABLENAME,WaSeclvVO.TABLENAME,WaGradeVO.TABLENAME);

	}

	/**
	 * 新增薪资标准
	 * 
	 * @author xuhw on 2009-11-12
	 */
	@Override
	public AggWaGradeVO insertWaGradeVO(AggWaGradeVO vo) throws BusinessException {
		AggWaGradeVO aggWagradevo = this.getServiceTemplate().insert(vo);
		// 对子表记录进行排序
		this.sortBillVO(aggWagradevo);
		// ---------------------------多版本删除-----------------------------
		// strGrdpk = ((WaGradeVO) aggWagradevo.getParentVO()).getPk_wa_grd();
		// WaPrmlvVO[] prmlvvosSaveAft = (WaPrmlvVO[])
		// aggWagradevo.getTableVO(IWaGradeCommonDef.WA_PRMLV);
		// WaSeclvVO[] seclvvosSaveAft = (WaSeclvVO[])
		// aggWagradevo.getTableVO(IWaGradeCommonDef.WA_SECLV);
		// lisCrtVOs = new ArrayList<WaCriterionVO>();
		// if (prmlvvosSaveAft != null)
		// {
		// for (WaPrmlvVO prmlvVO : prmlvvosSaveAft)
		// {
		// String strPrmlvPK = prmlvVO.getPk_wa_prmlv();
		// if (seclvvosSaveAft == null || seclvvosSaveAft.length == 0)
		// {
		// setCriterionVo(lisCrtVOs, strPrmlvPK, null);
		// }
		// else
		// {
		// for (WaSeclvVO seclvvo : seclvvosSaveAft)
		// {
		// setCriterionVo(lisCrtVOs, strPrmlvPK, seclvvo.getPk_wa_seclv());
		// }
		// }
		// }
		// }
		//
		// WaCriterionVO[] crterionvos = new WaCriterionVO[lisCrtVOs.size()];
		// lisCrtVOs.toArray(crterionvos);
		//
		// this.getWaGradeDao().getBaseDao().insertVOArray(crterionvos);

		WaCacheUtils.synCache(WaPsnhiBVO.TABLENAME, WaPsnhiVO.TABLENAME, WaPrmlvVO.TABLENAME, WaSeclvVO.TABLENAME,
				WaGradeVO.TABLENAME);
		return aggWagradevo;
	}

	/**
	 * 更新薪资标准设置
	 * 
	 * @author xuhw on 2009-11-12
	 */
	@Override
	public AggWaGradeVO updateWaGradeVO(AggWaGradeVO vo) throws BusinessException {
		initValues();
		// 更新数据时为参数对象添加审计信息
		AuditInfoUtil.updateData(vo.getParentVO());
		// 类别主键
		strGrdpk = vo.getParentVO().getPk_wa_grd();
		blnIsMul = vo.getParentVO().getIsmultsec().booleanValue();
		// 保存前的级别档别
		WaPrmlvVO[] prmlvvos = (WaPrmlvVO[]) vo.getTableVO(IWaGradeCommonDef.WA_PRMLV);
		WaSeclvVO[] seclvvos = (WaSeclvVO[]) vo.getTableVO(IWaGradeCommonDef.WA_SECLV);
		// 对薪资级别档别的相关校验
		validatePrmlv(prmlvvos);
		validateSeclv(seclvvos);

		// 保存级别，档别，薪资标准类别主表
		// zhoumxc 20140826 缓存测试：薪资级别表、薪资档别表
		for (int i = 0; i < prmlvvos.length; i++) {
			if (prmlvvos[i].getStatus() == 3) {
				CacheProxy.fireDataDeleted("wa_prmlv", prmlvvos[i].getPk_wa_prmlv());
			}

		}
		for (int i = 0; i < seclvvos.length; i++) {
			if (seclvvos[i].getStatus() == 3) {
				CacheProxy.fireDataDeleted("wa_seclv", seclvvos[i].getPk_wa_seclv());
			}
		}

		AggWaGradeVO aggWagradevo = getServiceTemplate().update(vo, true);
		// 对子表记录进行排序
		this.sortBillVO(aggWagradevo);
		// 根据删除的级别档别删除薪资标准类别值表中的内容
		this.deleteCrtByPrmlvPk(lisDelPrmlvVoFlag);
		this.deleteCrtBySeclvPk(lisDelSeclvVoFlag);
		WaCacheUtils.synCache(WaPsnhiBVO.TABLENAME, WaPsnhiVO.TABLENAME, WaPrmlvVO.TABLENAME, WaSeclvVO.TABLENAME,
				WaGradeVO.TABLENAME);
		// 更新后重新查询数据库中的级别和档别
		WaPrmlvVO[] prmlvvosSaveAfts = (WaPrmlvVO[]) aggWagradevo.getTableVO(IWaGradeCommonDef.WA_PRMLV);
		WaSeclvVO[] seclvvosSaveAfts = (WaSeclvVO[]) aggWagradevo.getTableVO(IWaGradeCommonDef.WA_SECLV);
		lisCrtVOs = new ArrayList<WaCriterionVO>();
		if (prmlvvosSaveAfts == null || prmlvvosSaveAfts.length == 0) {
			return aggWagradevo;
		}
		// ----------------多版本修改----------------------------
		// 更新级别档别成功后，向薪资标准表中插入数据
		// 情况1）
		// （新增的）薪资级别和所有的现在数据库中所有的薪资档别的组合
		// getCrtVosCaseOne(prmlvvosSaveAfts, seclvvosSaveAfts);
		// 情况2）
		// （既有的）薪资级别和本次操作新增的薪资档别的组合
		// getCrtVosCaseTwo(prmlvvosSaveAfts, seclvvosSaveAfts);

		// WaCriterionVO[] crterionvos = new WaCriterionVO[lisCrtVOs.size()];
		// lisCrtVOs.toArray(crterionvos);
		// this.getWaGradeDao().getBaseDao().insertVOArray(crterionvos);

		return aggWagradevo;
	}

	/**
	 * 初始化
	 * 
	 * @author xuhw on 2009-12-17
	 */
	private void initValues() {
		// 保存新增的级别
		lisNewPrmlvVoFlag = new ArrayList<String>();
		// 保存新增的档别
		lisNewSeclvVoFlag = new ArrayList<String>();
		// 保存删除的级别
		lisDelPrmlvVoFlag = new ArrayList<String>();
		// 保存删除的档别
		lisDelSeclvVoFlag = new ArrayList<String>();
	}

	@Override
	public Object[] updateCriterionArray(WaGradeVO wagradevo, WaCriterionVO[] criterions) throws BusinessException {
		if (org.apache.commons.lang.ArrayUtils.isEmpty(criterions)) {
			return new CrtVO[0];
		}

		// 版本校验（时间戳校验）
		BDVersionValidationUtil.validateSuperVO(wagradevo);

		if (StringUtils.isBlank(criterions[0].getPk_wa_crt())) {
			this.getWaGradeDao().getBaseDao().insertVOArray(criterions);
		} else {
			this.getWaGradeDao().getBaseDao().updateVOArray(criterions);
		}

		// 更新审计信息
		this.getWaGradeDao().getBaseDao().updateVO(wagradevo);

		WaGradeQueryImpl gradeQueryImpl = new WaGradeQueryImpl();

		// 画面回现
		CrtVO[] crtvos = gradeQueryImpl.queryCriterionByClassid(wagradevo.getPk_wa_grd(), null, wagradevo
				.getIsmultsec().booleanValue());// TODO
		Object[] retObj = new Object[2];
		retObj[0] = crtvos;
		retObj[1] = gradeQueryImpl.queryAggWagradeByGrdPK(wagradevo.getPk_wa_grd());
		return retObj;
	}

	@Override
	public Object[] updateStdHiVOArray(WaGradeVO wagradevo, WaPsnhiBVO[] waStdHiVOs, int classType)
			throws BusinessException {
		// 版本校验（时间戳校验）
		BDVersionValidationUtil.validateSuperVO(wagradevo);
		StringBuffer sbQuerysql = new StringBuffer();
		// zhoumxc 之前的删除语句有问题
		sbQuerysql
				.append("select pk_wa_psnhi_b from wa_psnhi_b where pk_wa_grdlv in (select pk_wa_grdlv from wa_psnhi_b where pk_wa_psnhi in "
						+ "(select pk_wa_psnhi from wa_psnhi where pk_wa_grd = '"
						+ wagradevo.getPk_wa_grd()
						+ "' and classtype = " + classType + " ) )");
		WaPsnhiBVO[] psnhibvos = getWaGradeDao().executeQueryVOs(sbQuerysql.toString(), WaPsnhiBVO.class);
		if (psnhibvos != null && psnhibvos.length != 0) {
			// zhoumxc 20140829 清理缓存
			CacheProxy.fireDataDeletedByWhereClause(WaPsnhiBVO.TABLENAME, WaPsnhiBVO.PK_WA_PSNHI_B,
					"pk_wa_psnhi_b in (" + sbQuerysql.toString() + ") ");
			getWaGradeDao().getBaseDao().deleteVOArray(psnhibvos);
		}

		if (waStdHiVOs == null) {
			return null;
		}

		this.getWaGradeDao().getBaseDao().insertVOArray(waStdHiVOs);
		CacheProxy.fireDataInserted(WaPsnhiBVO.TABLENAME);
		// 更新审计信息
		this.getWaGradeDao().getBaseDao().updateVO(wagradevo);

		WaGradeQueryImpl gradeQueryImpl = new WaGradeQueryImpl();
		WaStdHiSetVO[] stdHisetVO = gradeQueryImpl.queryStdHiByGrdPk(wagradevo.getPk_wa_grd(), classType);
		Object[] retObj = new Object[2];
		retObj[0] = stdHisetVO;
		retObj[1] = gradeQueryImpl.queryAggWagradeByGrdPK(wagradevo.getPk_wa_grd());
		// CacheProxy.fireDataDeleted(tableName, pk)
		// WaCacheUtils.synCache(WaPsnhiBVO.TABLENAME);
		return retObj;
	}

	/**
	 * 对薪资档别的相关校验 1）校验要被删除的档别是否被引用 2）校验同一个类别下，档别同名的问题
	 * 
	 * @author xuhw on 2009-12-15
	 * @param seclvvos
	 * @throws BusinessException
	 */
	private void validateSeclv(WaSeclvVO[] seclvvos) throws BusinessException {
		if (seclvvos == null) {
			return;
		}
		for (WaSeclvVO seclvvo : seclvvos) {
			// 校验要被删除的级别是否被引用
			if (seclvvo.getStatus() == VOStatus.DELETED) {
				this.validateSeclvHaveRefresh(seclvvo);
				lisDelSeclvVoFlag.add(seclvvo.getPk_wa_seclv());
			} else if (seclvvo.getStatus() == VOStatus.NEW) {
				// 校验同一个类别下，档别同名的问题
				seclvvo.setPk_wa_grd(strGrdpk);
				lisNewSeclvVoFlag.add(seclvvo.getLevelname());
			}
		}
	}

	/**
	 * 对薪资级别的相关校验 1）校验要被删除的级别是否被引用 2）校验同一个类别下，级别同名的问题
	 * 
	 * @author xuhw on 2009-12-15
	 * @param prmlvvos
	 * @throws BusinessException
	 */
	private void validatePrmlv(WaPrmlvVO[] prmlvvos) throws BusinessException {
		if (prmlvvos == null) {
			return;
		}
		for (WaPrmlvVO prmlvvo : prmlvvos) {
			// 校验要被删除的级别是否被引用
			if (prmlvvo.getStatus() == VOStatus.DELETED) {
				this.validatePrmlvHaveRefresh(prmlvvo);
				lisDelPrmlvVoFlag.add(prmlvvo.getPk_wa_prmlv());
			} else if (prmlvvo.getStatus() == VOStatus.NEW) {
				// 校验同一个类别下，级别同名的问题
				prmlvvo.setPk_wa_grd(strGrdpk);
				lisNewPrmlvVoFlag.add(prmlvvo.getLevelname());
			}
		}
	}

	/**
	 * 更新级别档别成功后，向薪资标准表中插入数据<BR>
	 * <BR>
	 * （新增的）薪资级别和所有的现在数据库中所有的薪资档别的组合<BR>
	 * 
	 * @author xuhw on 2009-12-16
	 * @param prmlvvosSaveAfts
	 * @param seclvvosSaveAfts
	 */
	private void getCrtVosCaseOne(WaPrmlvVO[] prmlvvosSaveAfts, WaSeclvVO[] seclvvosSaveAfts) {
		for (WaPrmlvVO prmlvVO : prmlvvosSaveAfts) {
			String strPrmlvPK = prmlvVO.getPk_wa_prmlv();
			// 判断是否是之前新增的对象
			if (!lisNewPrmlvVoFlag.contains(prmlvVO.getLevelname())) {
				continue;
			}
			if ((seclvvosSaveAfts == null || seclvvosSaveAfts.length == 0) && this.blnIsMul) {
				break;
			} else if ((seclvvosSaveAfts == null || seclvvosSaveAfts.length == 0) && !this.blnIsMul) {
				setCriterionVo(lisCrtVOs, strPrmlvPK, null);
			} else {
				for (WaSeclvVO seclvvo : seclvvosSaveAfts) {
					setCriterionVo(lisCrtVOs, strPrmlvPK, seclvvo.getPk_wa_seclv());
				}
			}

		}
	}

	/**
	 * 更新级别档别成功后，向薪资标准表中插入数据<BR>
	 * <BR>
	 * （既有的）薪资级别和本次操作新增的薪资档别的组合<BR>
	 * 
	 * @author xuhw on 2009-12-16
	 * @param prmlvvosSaveAfts
	 * @param seclvvosSaveAfts
	 */
	private void getCrtVosCaseTwo(WaPrmlvVO[] prmlvvosSaveAfts, WaSeclvVO[] seclvvosSaveAfts) {
		// 情况2）
		for (WaPrmlvVO prmlvVO : prmlvvosSaveAfts) {
			String strPrmlvPK = prmlvVO.getPk_wa_prmlv();
			if (seclvvosSaveAfts == null || seclvvosSaveAfts.length == 0) {
				break;
			}
			// 判断是否不是本次新增的对象
			else if (!lisNewPrmlvVoFlag.contains(prmlvVO.getLevelname())) {
				for (WaSeclvVO seclvvo : seclvvosSaveAfts) {
					if (lisNewSeclvVoFlag.contains(seclvvo.getLevelname())) {
						setCriterionVo(lisCrtVOs, strPrmlvPK, seclvvo.getPk_wa_seclv());
					}
				}
			}
		}
	}

	/**
	 * 根据级别PK删除薪资标准类别值表内容
	 * 
	 * @author xuhw on 2009-12-15
	 * @param lisDelPrmlvVoPK
	 * @throws BusinessException
	 */
	private void deleteCrtByPrmlvPk(List<String> lisDelPrmlvVoPK) throws BusinessException {
		int intCnt = lisDelPrmlvVoPK.size();
		if (intCnt == 0) {
			return;
		}
		this.getWaGradeDao().deleteCrtByPrmlvPk(lisDelPrmlvVoPK);
	}

	/**
	 * 根据档别PK删除薪资标准类别值表内容
	 * 
	 * @author xuhw on 2009-12-15
	 * @param lisDelPrmlvVoPK
	 * @throws BusinessException
	 */
	private void deleteCrtBySeclvPk(List<String> lisDelSeclvVoPK) throws BusinessException {
		int intCnt = lisDelSeclvVoPK.size();
		if (intCnt == 0) {
			return;
		}
		this.getWaGradeDao().deleteCrtBySeclvPk(lisDelSeclvVoPK);
	}

	/**
	 * 新增或修改时保存<BR>
	 * <BR>
	 * 
	 * @author xuhw on 2009-11-11
	 * @param vo
	 * @param isUpdate
	 * @param String
	 * @return
	 * @throws BusinessException
	 */
	private AggWaGradeVO sortBillVO(AggWaGradeVO billVO) throws BusinessException {
		// 对薪资标准设置标子表根据序号进行排序
		WaPrmlvVO[] prmlvs = null;
		WaSeclvVO[] seclvs = null;
		prmlvs = (WaPrmlvVO[]) billVO.getTableVO(IWaGradeCommonDef.WA_PRMLV);
		seclvs = (WaSeclvVO[]) billVO.getTableVO(IWaGradeCommonDef.WA_SECLV);
		if (prmlvs != null) {
			Arrays.sort(prmlvs, new WaGradeComparator());
		}
		if (seclvs != null) {
			Arrays.sort(seclvs, new WaGradeComparator());
		}
		billVO.setTableVO(IWaGradeCommonDef.WA_PRMLV, prmlvs);
		billVO.setTableVO(IWaGradeCommonDef.WA_SECLV, seclvs);

		return billVO;
	}

	/**
	 * 设置薪资标准值表VO
	 * 
	 * @author xuhw on 2009-12-15
	 * @param lisCrtVos
	 * @param strGrdpk
	 * @param strPrmlvPK
	 * @param strPkWaSeclv
	 */
	private void setCriterionVo(List<WaCriterionVO> lisCrtVos, String strPrmlvPK, String strPkWaSeclv) {
		WaCriterionVO criterionVO = new WaCriterionVO();
		criterionVO.setPk_wa_grd(strGrdpk);
		criterionVO.setPk_wa_prmlv(strPrmlvPK);
		criterionVO.setMax_value(new UFDouble(0));
		criterionVO.setMin_value(new UFDouble(0));
		criterionVO.setCriterionvalue(new UFDouble(0));
		criterionVO.setPk_wa_seclv(strPkWaSeclv);
		lisCrtVos.add(criterionVO);
	}

	/**
	 * 判断薪资类别是否被引用
	 * 
	 * @author xuhw on 2009-11-28
	 * @param strGradepk
	 * @return
	 * @throws BusinessException
	 */
	private void validateGradeHaveReference(String strGradepk) throws BusinessException {
		// if (getWaGradeDao().gradeHaveCrtReference(strGradepk))
		// {
		// Logger.debug("该类别已经被薪资标准表引用!");
		// throw new
		// BusinessException(ResHelper.getString("60130paystd","060130paystd0207")/*@res
		// "该类别已经被薪资标准表引用!"*/);
		// }
		// if (getWaGradeDao().gradeHavePsnappaproveReference(strGradepk))
		// {
		// Logger.debug("薪资标准已经被定调资引用！");
		// throw new
		// BusinessException(ResHelper.getString("60130paystd","060130paystd0208")/*@res
		// "薪资标准已经被定调资引用！"*/);
		// }
		if (getWaGradeDao().gradeHaveWaItemReference(strGradepk)) {
			Logger.debug("该薪资标准类别已经被公共薪资项目引用，不能删除！");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0241")/*
																							 * @
																							 * res
																							 * "该薪资标准类别已经被公共薪资项目引用，不能删除！"
																							 */);
		}
		if (getWaGradeDao().gradeHaveWaClassitemReference(strGradepk)) {
			Logger.debug("该薪资标准类别已经被薪资发放项目引用，不能删除！");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0242")/*
																							 * @
																							 * res
																							 * "该薪资标准类别已经被薪资发放项目引用，不能删除！"
																							 */);
		}
		if (getWaGradeDao().gradeHavePsnappaproveReference(strGradepk)) {
			Logger.debug("该薪资标准类别已经被定调资申请数据引用，不能删除！");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0243")/*
																							 * @
																							 * res
																							 * "该薪资标准类别已经被定调资申请数据引用，不能删除！"
																							 */);
		}
		if (getWaGradeDao().gradeHavePsndocWadocReference(strGradepk)) {
			Logger.debug("该薪资标准类别已经被定调资管理数据引用，不能删除！");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0244")/*
																							 * @
																							 * res
																							 * "该薪资标准类别已经被定调资管理数据引用，不能删除！"
																							 */);
		}
	}

	/**
	 * 判断薪资级别是否被引用
	 * 
	 * @author xuhw on 2009-11-28
	 * @param strPrmlvPK
	 * @throws BusinessException
	 */
	private void validatePrmlvHaveRefresh(WaPrmlvVO prmlvvo) throws BusinessException {
		String strPrmlvPK = prmlvvo.getPk_wa_prmlv();
		if (getWaGradeDao().prmlvHaveCrtReference(strPrmlvPK)) {
			Logger.debug("级别:" + prmlvvo.getLevelname() + " 已经被薪资标准表引用!");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0209")/*
																							 * @
																							 * res
																							 * "级别:"
																							 */+ prmlvvo.getLevelname()
					+ ResHelper.getString("60130paystd", "060130paystd0210")/*
																			 * @res
																			 * " 已经被薪资标准表引用!"
																			 */);
		}
	}

	/**
	 * 判断薪资档别别是否被引用
	 * 
	 * @author xuhw on 2009-11-28
	 * @param strPrmlvPK
	 * @throws BusinessException
	 */
	private void validateSeclvHaveRefresh(WaSeclvVO seclvvo) throws BusinessException {
		String strSeclvPK = seclvvo.getPk_wa_seclv();
		if (getWaGradeDao().seclvHaveCrtReference(strSeclvPK)) {
			Logger.debug("档别:" + seclvvo.getLevelname() + " 已经被薪资标准表引用!");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0211")/*
																							 * @
																							 * res
																							 * "档别:"
																							 */+ seclvvo.getLevelname()
					+ ResHelper.getString("60130paystd", "060130paystd0210")/*
																			 * @res
																			 * " 已经被薪资标准表引用!"
																			 */);
		}
	}

	// --------------add for 多版本 start --------------
	/**
	 * 处理薪资标准表数据
	 * 
	 * @param vervo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public WaGradeVerVO processCriterionArray(WaGradeVO gradevo, WaGradeVerVO vervo) throws BusinessException {

		validateName(vervo);
		if (!StringUtils.isBlank(vervo.getPk_wa_gradever())) {
			getWaGradeDao().getBaseDao().updateVO(vervo);
		} else {
			String vervoPk = getWaGradeDao().getBaseDao().insertVO(vervo);
			vervo.setPk_wa_gradever(vervoPk);
		}
		vervo = updateCriterions(gradevo, vervo);
		WaCacheUtils.synCache(WaGradeVerVO.TABLENAME);

		return vervo;
	}

	@Override
	public WaGradeVerVO processCriterionArray4Copy(WaGradeVO wagradevo, WaGradeVerVO vervo) throws BusinessException {
		getWaGradeDao().getBaseDao().insertVO(vervo);
		WaCriterionVO[] criterionvos = vervo.getCriterionvos4Copy();
		if (ArrayUtils.isEmpty(criterionvos)) {
			return vervo;
		}
		for (WaCriterionVO criterionvo : criterionvos) {
			criterionvo.setStatus(VOStatus.NEW);
			criterionvo.setPk_wa_gradever(vervo.getPk_wa_gradever());
		}
		getWaGradeDao().getBaseDao().insertVOArray(criterionvos);
		WaGradeQueryImpl gradeQueryImpl = new WaGradeQueryImpl();

		// 薪资标准版本信息
		CrtVO[] crtvos = gradeQueryImpl.queryCriterionByClassid(wagradevo.getPk_wa_grd(), vervo.getPk_wa_gradever(),
				wagradevo.getIsmultsec().booleanValue());
		vervo.setCrtVOs(crtvos);
		WaCacheUtils.synCache(WaGradeVerVO.TABLENAME);
		return vervo;
	}

	/**
	 * 更新处理薪资标准数据
	 * 
	 * @param wagradevo
	 * @param vervo
	 * @return
	 * @throws BusinessException
	 */
	private WaGradeVerVO updateCriterions(WaGradeVO wagradevo, WaGradeVerVO vervo) throws BusinessException {
		if (org.apache.commons.lang.ArrayUtils.isEmpty(vervo.getCriterionvos())) {

			// 处理生效标志
			getWaGradeDao().updateEffectFlag(vervo);

			return vervo;
		}

		// 版本校验（时间戳校验）
		// BDVersionValidationUtil.validateSuperVO(wagradevo);
		WaCriterionVO[] criterionvos = vervo.getCriterionvos();
		if (ArrayUtils.isEmpty(criterionvos)) {
			return vervo;
		}
		for (WaCriterionVO criterionvo : criterionvos) {
			criterionvo.setPk_wa_gradever(vervo.getPk_wa_gradever());
		}
		// if (vervo.getCriterionvos()[0].getPk_wa_crt() == null) {
		if (vervo.getStatus() == VOStatus.NEW) {
			this.getWaGradeDao().getBaseDao().insertVOArray(vervo.getCriterionvos());
		} else {
			this.getWaGradeDao().getBaseDao().updateVOArray(vervo.getCriterionvos());
		}

		// 处理生效标志
		getWaGradeDao().updateEffectFlag(vervo);

		// 更新审计信息
		wagradevo.setModifier(PubEnv.getPk_user());
		wagradevo.setModifiedtime(PubEnv.getServerTime());
		// 更新审计信息
		this.getWaGradeDao().getBaseDao().updateVO(wagradevo);

		WaGradeQueryImpl gradeQueryImpl = new WaGradeQueryImpl();

		// 薪资标准版本信息
		CrtVO[] crtvos = gradeQueryImpl.queryCriterionByClassid(wagradevo.getPk_wa_grd(), vervo.getPk_wa_gradever(),
				wagradevo.getIsmultsec().booleanValue());
		vervo.setCrtVOs(crtvos);
		return vervo;
	}

	/**
	 * 校验薪资标准版本名称是否重复
	 * 
	 * @param vervo
	 * @throws BusinessException
	 */
	private void validateName(WaGradeVerVO vervo) throws BusinessException {
		String strSql = " select 1 from wa_grade_ver where wa_grade_ver.pk_wa_grd = '" + vervo.getPk_wa_grd()
				+ "' and wa_grade_ver.gradever_name = '" + vervo.getGradever_name() + "'";

		if (vervo.getPk_wa_gradever() != null) {
			strSql += " and wa_grade_ver.pk_wa_gradever <> '" + vervo.getPk_wa_gradever() + "'";
		}

		if (getWaGradeDao().isValueExist(strSql)) {
			throw new BusinessException(ResHelper.getString("common", "UC000-0001155")/*
																					 * @
																					 * res
																					 * "名称"
																					 */+ vervo.getGradever_name()
					+ ResHelper.getString("60130paystd", "060130paystd0212")/*
																			 * @res
																			 * "已经存在,请重新命名！"
																			 */);
		}
	}

	/**
	 * 删除薪资标准表数据
	 * 
	 * @param vervo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public void deleteGradeVerVO(WaGradeVerVO gradevervo) throws BusinessException {
		if (gradevervo == null || StringUtils.isBlank(gradevervo.getPk_wa_gradever())) {
			Logger.debug("请选则要删的薪资标准版本!");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0213")/*
																							 * @
																							 * res
																							 * "请选则要删的薪资标准版本!"
																							 */);
		}

		// 校验被删除版本是否被定调资组件引用
		if (gradevervo.getEffect_flag().booleanValue()) {
			if (getWaGradeDao().isWaGradeverHasReferenced(gradevervo.getPk_wa_grd())) {
				throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0214")/*
																								 * @
																								 * res
																								 * "该版本已经被定调资组件引用,不允许删除!"
																								 */);
			}
		}
		getWaGradeDao().getBaseDao().deleteVO(gradevervo);
		// zhoumxc 20140823 缓存测试：薪资标准类别版本表
		CacheProxy.fireDataDeleted("wa_grade_ver", gradevervo.getPk_wa_gradever());
		getWaGradeDao().getBaseDao().deleteByClause(WaCriterionVO.class,
				WaGradeVerVO.PK_WA_GRADEVER + " = '" + gradevervo.getPk_wa_gradever() + "'");

	}

	/**
	 * 查询薪资标准版本信息
	 */
	@Override
	public WaGradeVerVO[] queryGradeVerByGradePK(String clsPk) throws BusinessException {
		return getWaGradeDao().queryVerVOByGradePk(clsPk);
	}

	@Override
	public WaGradeVerVO queryEffectGradeVerByGradePK(String strGradePk) throws BusinessException {
		return getWaGradeDao().queryEffectGradeVerByGradePK(strGradePk);
	}

	/**
	 * 跟据薪资标准类别主键查询薪资标准版本的最大版本号
	 * 
	 * @param strPKWaGrd
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public UFDouble getMaxVerNum(String strPKWaGrd) throws BusinessException {
		String querySQL = "select max(gradever_num) as gradever_num from wa_grade_ver where pk_wa_grd = ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strPKWaGrd);
		WaGradeVerVO gradevo = getWaGradeDao().executeQueryVO(querySQL, parameter, WaGradeVerVO.class);
		if (gradevo != null) {
			return gradevo.getGradever_num();
		}
		return null;
	}

	@Override
	public WaCriterionVO getCrierionVOByPrmSec(String strPKPrmlv, String strPKSeclv) throws BusinessException {
		return getWaGradeDao().getCrierionVOByPrmSec(strPKPrmlv, strPKSeclv);
	}

	@Override
	public WaCriterionVO getCrierionVOByPrmSec(UFLiteralDate checkDate, String strPKPrmlv, String strPKSeclv)
			throws BusinessException {
		return getWaGradeDao().getCrierionVOByPrmSec(checkDate, strPKPrmlv, strPKSeclv);
	}

	/**
	 * 薪资变动中用到获取薪资标准类别
	 * 
	 * @param AdjustWadocVO
	 *            []
	 * @throws BusinessException
	 */
	@Override
	public HashMap<String, WaCriterionVO> getCrierionVOMapByPrmSec(AdjustWadocVO[] adjustWadocPsnInfoVOs)
			throws BusinessException {
		return getWaGradeDao().getCrierionVOMapByPrmSec(adjustWadocPsnInfoVOs);
	}

	/**
	 * 薪资变动中用到获取薪资标准类别
	 * 
	 * @param AdjustWadocVO
	 *            []
	 * @throws BusinessException
	 */
	@Override
	public HashMap<String, WaCriterionVO> getCrierionVOMapByPrmSec(PsnappaproveBVO[] psnappaproveBVOs, boolean isApprove)
			throws BusinessException {
		return getWaGradeDao().getCrierionVOMapByPrmSec(psnappaproveBVOs, isApprove);
	}

	/**
	 * 根据薪资标准类别PK查询生效的薪资标准表集合
	 * 
	 * @param strPKGrade
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public WaCriterionVO[] getEffectCrierionsVOByGradePK(String strPKGrade) throws BusinessException {
		return getWaGradeDao().getEffectCrierionsVOByGradePK(strPKGrade);
	}

	/**
	 * 薪资标准类别增加级别档别时候的验证
	 * 
	 * @param gradeVO
	 * @throws BusinessException
	 */
	@Override
	public void validatorHasGradeVer(String strGradePK) throws BusinessException {
		if (!StringUtils.isBlank(strGradePK)) {
			String strSql = " select 1 from wa_grade_ver where pk_wa_grd = '" + strGradePK + "' and effect_flag = 'Y'";

			if (getWaGradeDao().isValueExist(strSql)) {
				throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0215")/*
																								 * @
																								 * res
																								 * "该薪资标准类别已存在生效版本，不能增加或减少级别档别！"
																								 */);
			}
		}
	}

	/**
	 * 删除薪资标准版本记录根据PK
	 * 
	 * @param strGradeVerPK
	 * @throws BusinessException
	 */
	@Override
	public void deleteGradeVerByPk(String strGradeVerPK) throws BusinessException {
		getWaGradeDao().getBaseDao().deleteByPK(WaGradeVerVO.class, strGradeVerPK);
	}

	private WaGradeDAO getWaGradeDao() {
		if (gradeDao == null) {
			gradeDao = new WaGradeDAO();
		}
		return gradeDao;
	}

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate(DOC_NAME);
		}
		return serviceTemplate;
	}

	@Override
	public String validateGradeHaveReferenceByBusiness(String strGradepk) throws BusinessException {
		if (getWaGradeDao().gradeHaveWaItemReference(strGradepk)) {
			return ResHelper.getString("60130paystd", "060130paystd0231")/*
																		 * @res
																		 * "该薪资标准类别已经被公共薪资项目引用，生效标记不能为空！"
																		 */;
		}
		if (getWaGradeDao().gradeHaveWaClassitemReference(strGradepk)) {
			return ResHelper.getString("60130paystd", "060130paystd0232")/*
																		 * @res
																		 * "该薪资标准类别已经被薪资发放项目引用，生效标记不能为空！"
																		 */;
		}
		if (getWaGradeDao().gradeHavePsnappaproveReference(strGradepk)) {
			return ResHelper.getString("60130paystd", "060130paystd0233")/*
																		 * @res
																		 * "该薪资标准类别已经被定调资申请数据引用，生效标记不能为空！"
																		 */;
		}
		if (getWaGradeDao().gradeHavePsndocWadocReference(strGradepk)) {
			return ResHelper.getString("60130paystd", "060130paystd0234")/*
																		 * @res
																		 * "该薪资标准类别已经被定调资管理数据引用，生效标记不能为空！"
																		 */;
		}
		return "";
	}

	@Override
	public boolean validateEffectVersion(String strGradepk) throws BusinessException {
		return getWaGradeDao().validateEffectVersion(strGradepk);
	}

	@Override
	public void deleteWaPsnhiByPK(String strGradepk, int classType) throws BusinessException {
		getWaGradeDao().deleteWaPsnhiByPK(strGradepk, classType);
	}

	@Override
	public void deleteVersionByGradePK(String strGradepk) throws BusinessException {
		getWaGradeDao().deleteVersionByGradePK(strGradepk);
	}

	@Override
	public void insertCopyStdHiVOArray(WaPsnhiBVO[] waStdHiVOs) throws BusinessException {
		this.getWaGradeDao().getBaseDao().insertVOArray(waStdHiVOs);
		CacheProxy.fireDataInserted(WaPsnhiBVO.TABLENAME);
	}

}