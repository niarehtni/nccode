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
 * н�ʱ�׼����
 * 
 * @author: xuhw
 * @date: 2009-11-12 ����01:56:16
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class WaGradeAppImpl implements IWaGradeService {
	private final String DOC_NAME = "grade";
	private WaGradeDAO gradeDao;
	private SimpleDocServiceTemplate serviceTemplate;
	/** ���������ļ��� */
	private List<String> lisNewPrmlvVoFlag = new ArrayList<String>();
	/** ���������ĵ��� */
	private List<String> lisNewSeclvVoFlag = new ArrayList<String>();
	/** ����ɾ���ļ��� */
	private List<String> lisDelPrmlvVoFlag = new ArrayList<String>();
	/** ����ɾ���ĵ��� */
	private List<String> lisDelSeclvVoFlag = new ArrayList<String>();
	/** ������� */
	String strGrdpk = null;
	/** �Ƿ�൵ */
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
	 * ɾ��н�ʱ�׼���ñ�<BR>
	 * ɾ��֮ǰҪ�����Ƿ�Ӧ�õ�У�飬������������ܱ�ɾ��<BR>
	 * 
	 * @author xuhw on 2009-11-12
	 */
	@Override
	public void deleteWaGradeVO(AggWaGradeVO billVO) throws BusinessException {
		// ��ҪУ���Ǳ��������ű�����
		// н�ʱ�׼��
		// ��Աн�ʵȼ���
		// ������
		String strPKGrd = billVO.getParentVO().getPk_wa_grd();
		validateGradeHaveReference(strPKGrd);
		// // ����
		// WaPrmlvVO[] prmlvvos = (WaPrmlvVO[])
		// billVO.getTableVO(IWaGradeCommonDef.WA_PRMLV);
		// // ����
		// WaSeclvVO[] seclvvos = (WaSeclvVO[])
		// billVO.getTableVO(IWaGradeCommonDef.WA_SECLV);
		//
		// if (prmlvvos != null)
		// {
		// for (WaPrmlvVO prmlvvo : prmlvvos)
		// {
		// // У��Ҫ��ɾ���ļ����Ƿ�����
		// this.validatePrmlvHaveRefresh(prmlvvo);
		// }
		// }
		// if (seclvvos != null)
		// {
		// for (WaSeclvVO seclvvo : seclvvos)
		// {
		// // У��Ҫ��ɾ���ļ����Ƿ�����
		// this.validateSeclvHaveRefresh(seclvvo);
		// }
		// }

		String psnhi_b = "select pk_wa_psnhi_b from wa_psnhi_b where pk_wa_grdlv in (select pk_wa_grdlv from wa_psnhi_b where pk_wa_psnhi in "
				+ "(select pk_wa_psnhi from wa_psnhi where pk_wa_grd = '" + strPKGrd + "' ) )";

		// zhoumxc ͬ������ 20140902
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

		// ͬ������ �����⣬Ҫ��ɾ��֮ǰ�建�� zhoumxc
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
	 * ����н�ʱ�׼
	 * 
	 * @author xuhw on 2009-11-12
	 */
	@Override
	public AggWaGradeVO insertWaGradeVO(AggWaGradeVO vo) throws BusinessException {
		AggWaGradeVO aggWagradevo = this.getServiceTemplate().insert(vo);
		// ���ӱ��¼��������
		this.sortBillVO(aggWagradevo);
		// ---------------------------��汾ɾ��-----------------------------
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
	 * ����н�ʱ�׼����
	 * 
	 * @author xuhw on 2009-11-12
	 */
	@Override
	public AggWaGradeVO updateWaGradeVO(AggWaGradeVO vo) throws BusinessException {
		initValues();
		// ��������ʱΪ����������������Ϣ
		AuditInfoUtil.updateData(vo.getParentVO());
		// �������
		strGrdpk = vo.getParentVO().getPk_wa_grd();
		blnIsMul = vo.getParentVO().getIsmultsec().booleanValue();
		// ����ǰ�ļ��𵵱�
		WaPrmlvVO[] prmlvvos = (WaPrmlvVO[]) vo.getTableVO(IWaGradeCommonDef.WA_PRMLV);
		WaSeclvVO[] seclvvos = (WaSeclvVO[]) vo.getTableVO(IWaGradeCommonDef.WA_SECLV);
		// ��н�ʼ��𵵱�����У��
		validatePrmlv(prmlvvos);
		validateSeclv(seclvvos);

		// ���漶�𣬵���н�ʱ�׼�������
		// zhoumxc 20140826 ������ԣ�н�ʼ����н�ʵ����
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
		// ���ӱ��¼��������
		this.sortBillVO(aggWagradevo);
		// ����ɾ���ļ��𵵱�ɾ��н�ʱ�׼���ֵ���е�����
		this.deleteCrtByPrmlvPk(lisDelPrmlvVoFlag);
		this.deleteCrtBySeclvPk(lisDelSeclvVoFlag);
		WaCacheUtils.synCache(WaPsnhiBVO.TABLENAME, WaPsnhiVO.TABLENAME, WaPrmlvVO.TABLENAME, WaSeclvVO.TABLENAME,
				WaGradeVO.TABLENAME);
		// ���º����²�ѯ���ݿ��еļ���͵���
		WaPrmlvVO[] prmlvvosSaveAfts = (WaPrmlvVO[]) aggWagradevo.getTableVO(IWaGradeCommonDef.WA_PRMLV);
		WaSeclvVO[] seclvvosSaveAfts = (WaSeclvVO[]) aggWagradevo.getTableVO(IWaGradeCommonDef.WA_SECLV);
		lisCrtVOs = new ArrayList<WaCriterionVO>();
		if (prmlvvosSaveAfts == null || prmlvvosSaveAfts.length == 0) {
			return aggWagradevo;
		}
		// ----------------��汾�޸�----------------------------
		// ���¼��𵵱�ɹ�����н�ʱ�׼���в�������
		// ���1��
		// �������ģ�н�ʼ�������е��������ݿ������е�н�ʵ�������
		// getCrtVosCaseOne(prmlvvosSaveAfts, seclvvosSaveAfts);
		// ���2��
		// �����еģ�н�ʼ���ͱ��β���������н�ʵ�������
		// getCrtVosCaseTwo(prmlvvosSaveAfts, seclvvosSaveAfts);

		// WaCriterionVO[] crterionvos = new WaCriterionVO[lisCrtVOs.size()];
		// lisCrtVOs.toArray(crterionvos);
		// this.getWaGradeDao().getBaseDao().insertVOArray(crterionvos);

		return aggWagradevo;
	}

	/**
	 * ��ʼ��
	 * 
	 * @author xuhw on 2009-12-17
	 */
	private void initValues() {
		// ���������ļ���
		lisNewPrmlvVoFlag = new ArrayList<String>();
		// ���������ĵ���
		lisNewSeclvVoFlag = new ArrayList<String>();
		// ����ɾ���ļ���
		lisDelPrmlvVoFlag = new ArrayList<String>();
		// ����ɾ���ĵ���
		lisDelSeclvVoFlag = new ArrayList<String>();
	}

	@Override
	public Object[] updateCriterionArray(WaGradeVO wagradevo, WaCriterionVO[] criterions) throws BusinessException {
		if (org.apache.commons.lang.ArrayUtils.isEmpty(criterions)) {
			return new CrtVO[0];
		}

		// �汾У�飨ʱ���У�飩
		BDVersionValidationUtil.validateSuperVO(wagradevo);

		if (StringUtils.isBlank(criterions[0].getPk_wa_crt())) {
			this.getWaGradeDao().getBaseDao().insertVOArray(criterions);
		} else {
			this.getWaGradeDao().getBaseDao().updateVOArray(criterions);
		}

		// ���������Ϣ
		this.getWaGradeDao().getBaseDao().updateVO(wagradevo);

		WaGradeQueryImpl gradeQueryImpl = new WaGradeQueryImpl();

		// �������
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
		// �汾У�飨ʱ���У�飩
		BDVersionValidationUtil.validateSuperVO(wagradevo);
		StringBuffer sbQuerysql = new StringBuffer();
		// zhoumxc ֮ǰ��ɾ�����������
		sbQuerysql
				.append("select pk_wa_psnhi_b from wa_psnhi_b where pk_wa_grdlv in (select pk_wa_grdlv from wa_psnhi_b where pk_wa_psnhi in "
						+ "(select pk_wa_psnhi from wa_psnhi where pk_wa_grd = '"
						+ wagradevo.getPk_wa_grd()
						+ "' and classtype = " + classType + " ) )");
		WaPsnhiBVO[] psnhibvos = getWaGradeDao().executeQueryVOs(sbQuerysql.toString(), WaPsnhiBVO.class);
		if (psnhibvos != null && psnhibvos.length != 0) {
			// zhoumxc 20140829 ������
			CacheProxy.fireDataDeletedByWhereClause(WaPsnhiBVO.TABLENAME, WaPsnhiBVO.PK_WA_PSNHI_B,
					"pk_wa_psnhi_b in (" + sbQuerysql.toString() + ") ");
			getWaGradeDao().getBaseDao().deleteVOArray(psnhibvos);
		}

		if (waStdHiVOs == null) {
			return null;
		}

		this.getWaGradeDao().getBaseDao().insertVOArray(waStdHiVOs);
		CacheProxy.fireDataInserted(WaPsnhiBVO.TABLENAME);
		// ���������Ϣ
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
	 * ��н�ʵ�������У�� 1��У��Ҫ��ɾ���ĵ����Ƿ����� 2��У��ͬһ������£�����ͬ��������
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
			// У��Ҫ��ɾ���ļ����Ƿ�����
			if (seclvvo.getStatus() == VOStatus.DELETED) {
				this.validateSeclvHaveRefresh(seclvvo);
				lisDelSeclvVoFlag.add(seclvvo.getPk_wa_seclv());
			} else if (seclvvo.getStatus() == VOStatus.NEW) {
				// У��ͬһ������£�����ͬ��������
				seclvvo.setPk_wa_grd(strGrdpk);
				lisNewSeclvVoFlag.add(seclvvo.getLevelname());
			}
		}
	}

	/**
	 * ��н�ʼ�������У�� 1��У��Ҫ��ɾ���ļ����Ƿ����� 2��У��ͬһ������£�����ͬ��������
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
			// У��Ҫ��ɾ���ļ����Ƿ�����
			if (prmlvvo.getStatus() == VOStatus.DELETED) {
				this.validatePrmlvHaveRefresh(prmlvvo);
				lisDelPrmlvVoFlag.add(prmlvvo.getPk_wa_prmlv());
			} else if (prmlvvo.getStatus() == VOStatus.NEW) {
				// У��ͬһ������£�����ͬ��������
				prmlvvo.setPk_wa_grd(strGrdpk);
				lisNewPrmlvVoFlag.add(prmlvvo.getLevelname());
			}
		}
	}

	/**
	 * ���¼��𵵱�ɹ�����н�ʱ�׼���в�������<BR>
	 * <BR>
	 * �������ģ�н�ʼ�������е��������ݿ������е�н�ʵ�������<BR>
	 * 
	 * @author xuhw on 2009-12-16
	 * @param prmlvvosSaveAfts
	 * @param seclvvosSaveAfts
	 */
	private void getCrtVosCaseOne(WaPrmlvVO[] prmlvvosSaveAfts, WaSeclvVO[] seclvvosSaveAfts) {
		for (WaPrmlvVO prmlvVO : prmlvvosSaveAfts) {
			String strPrmlvPK = prmlvVO.getPk_wa_prmlv();
			// �ж��Ƿ���֮ǰ�����Ķ���
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
	 * ���¼��𵵱�ɹ�����н�ʱ�׼���в�������<BR>
	 * <BR>
	 * �����еģ�н�ʼ���ͱ��β���������н�ʵ�������<BR>
	 * 
	 * @author xuhw on 2009-12-16
	 * @param prmlvvosSaveAfts
	 * @param seclvvosSaveAfts
	 */
	private void getCrtVosCaseTwo(WaPrmlvVO[] prmlvvosSaveAfts, WaSeclvVO[] seclvvosSaveAfts) {
		// ���2��
		for (WaPrmlvVO prmlvVO : prmlvvosSaveAfts) {
			String strPrmlvPK = prmlvVO.getPk_wa_prmlv();
			if (seclvvosSaveAfts == null || seclvvosSaveAfts.length == 0) {
				break;
			}
			// �ж��Ƿ��Ǳ��������Ķ���
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
	 * ���ݼ���PKɾ��н�ʱ�׼���ֵ������
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
	 * ���ݵ���PKɾ��н�ʱ�׼���ֵ������
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
	 * �������޸�ʱ����<BR>
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
		// ��н�ʱ�׼���ñ��ӱ������Ž�������
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
	 * ����н�ʱ�׼ֵ��VO
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
	 * �ж�н������Ƿ�����
	 * 
	 * @author xuhw on 2009-11-28
	 * @param strGradepk
	 * @return
	 * @throws BusinessException
	 */
	private void validateGradeHaveReference(String strGradepk) throws BusinessException {
		// if (getWaGradeDao().gradeHaveCrtReference(strGradepk))
		// {
		// Logger.debug("������Ѿ���н�ʱ�׼������!");
		// throw new
		// BusinessException(ResHelper.getString("60130paystd","060130paystd0207")/*@res
		// "������Ѿ���н�ʱ�׼������!"*/);
		// }
		// if (getWaGradeDao().gradeHavePsnappaproveReference(strGradepk))
		// {
		// Logger.debug("н�ʱ�׼�Ѿ������������ã�");
		// throw new
		// BusinessException(ResHelper.getString("60130paystd","060130paystd0208")/*@res
		// "н�ʱ�׼�Ѿ������������ã�"*/);
		// }
		if (getWaGradeDao().gradeHaveWaItemReference(strGradepk)) {
			Logger.debug("��н�ʱ�׼����Ѿ�������н����Ŀ���ã�����ɾ����");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0241")/*
																							 * @
																							 * res
																							 * "��н�ʱ�׼����Ѿ�������н����Ŀ���ã�����ɾ����"
																							 */);
		}
		if (getWaGradeDao().gradeHaveWaClassitemReference(strGradepk)) {
			Logger.debug("��н�ʱ�׼����Ѿ���н�ʷ�����Ŀ���ã�����ɾ����");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0242")/*
																							 * @
																							 * res
																							 * "��н�ʱ�׼����Ѿ���н�ʷ�����Ŀ���ã�����ɾ����"
																							 */);
		}
		if (getWaGradeDao().gradeHavePsnappaproveReference(strGradepk)) {
			Logger.debug("��н�ʱ�׼����Ѿ��������������������ã�����ɾ����");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0243")/*
																							 * @
																							 * res
																							 * "��н�ʱ�׼����Ѿ��������������������ã�����ɾ����"
																							 */);
		}
		if (getWaGradeDao().gradeHavePsndocWadocReference(strGradepk)) {
			Logger.debug("��н�ʱ�׼����Ѿ��������ʹ����������ã�����ɾ����");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0244")/*
																							 * @
																							 * res
																							 * "��н�ʱ�׼����Ѿ��������ʹ����������ã�����ɾ����"
																							 */);
		}
	}

	/**
	 * �ж�н�ʼ����Ƿ�����
	 * 
	 * @author xuhw on 2009-11-28
	 * @param strPrmlvPK
	 * @throws BusinessException
	 */
	private void validatePrmlvHaveRefresh(WaPrmlvVO prmlvvo) throws BusinessException {
		String strPrmlvPK = prmlvvo.getPk_wa_prmlv();
		if (getWaGradeDao().prmlvHaveCrtReference(strPrmlvPK)) {
			Logger.debug("����:" + prmlvvo.getLevelname() + " �Ѿ���н�ʱ�׼������!");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0209")/*
																							 * @
																							 * res
																							 * "����:"
																							 */+ prmlvvo.getLevelname()
					+ ResHelper.getString("60130paystd", "060130paystd0210")/*
																			 * @res
																			 * " �Ѿ���н�ʱ�׼������!"
																			 */);
		}
	}

	/**
	 * �ж�н�ʵ�����Ƿ�����
	 * 
	 * @author xuhw on 2009-11-28
	 * @param strPrmlvPK
	 * @throws BusinessException
	 */
	private void validateSeclvHaveRefresh(WaSeclvVO seclvvo) throws BusinessException {
		String strSeclvPK = seclvvo.getPk_wa_seclv();
		if (getWaGradeDao().seclvHaveCrtReference(strSeclvPK)) {
			Logger.debug("����:" + seclvvo.getLevelname() + " �Ѿ���н�ʱ�׼������!");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0211")/*
																							 * @
																							 * res
																							 * "����:"
																							 */+ seclvvo.getLevelname()
					+ ResHelper.getString("60130paystd", "060130paystd0210")/*
																			 * @res
																			 * " �Ѿ���н�ʱ�׼������!"
																			 */);
		}
	}

	// --------------add for ��汾 start --------------
	/**
	 * ����н�ʱ�׼������
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

		// н�ʱ�׼�汾��Ϣ
		CrtVO[] crtvos = gradeQueryImpl.queryCriterionByClassid(wagradevo.getPk_wa_grd(), vervo.getPk_wa_gradever(),
				wagradevo.getIsmultsec().booleanValue());
		vervo.setCrtVOs(crtvos);
		WaCacheUtils.synCache(WaGradeVerVO.TABLENAME);
		return vervo;
	}

	/**
	 * ���´���н�ʱ�׼����
	 * 
	 * @param wagradevo
	 * @param vervo
	 * @return
	 * @throws BusinessException
	 */
	private WaGradeVerVO updateCriterions(WaGradeVO wagradevo, WaGradeVerVO vervo) throws BusinessException {
		if (org.apache.commons.lang.ArrayUtils.isEmpty(vervo.getCriterionvos())) {

			// ������Ч��־
			getWaGradeDao().updateEffectFlag(vervo);

			return vervo;
		}

		// �汾У�飨ʱ���У�飩
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

		// ������Ч��־
		getWaGradeDao().updateEffectFlag(vervo);

		// ���������Ϣ
		wagradevo.setModifier(PubEnv.getPk_user());
		wagradevo.setModifiedtime(PubEnv.getServerTime());
		// ���������Ϣ
		this.getWaGradeDao().getBaseDao().updateVO(wagradevo);

		WaGradeQueryImpl gradeQueryImpl = new WaGradeQueryImpl();

		// н�ʱ�׼�汾��Ϣ
		CrtVO[] crtvos = gradeQueryImpl.queryCriterionByClassid(wagradevo.getPk_wa_grd(), vervo.getPk_wa_gradever(),
				wagradevo.getIsmultsec().booleanValue());
		vervo.setCrtVOs(crtvos);
		return vervo;
	}

	/**
	 * У��н�ʱ�׼�汾�����Ƿ��ظ�
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
																					 * "����"
																					 */+ vervo.getGradever_name()
					+ ResHelper.getString("60130paystd", "060130paystd0212")/*
																			 * @res
																			 * "�Ѿ�����,������������"
																			 */);
		}
	}

	/**
	 * ɾ��н�ʱ�׼������
	 * 
	 * @param vervo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public void deleteGradeVerVO(WaGradeVerVO gradevervo) throws BusinessException {
		if (gradevervo == null || StringUtils.isBlank(gradevervo.getPk_wa_gradever())) {
			Logger.debug("��ѡ��Ҫɾ��н�ʱ�׼�汾!");
			throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0213")/*
																							 * @
																							 * res
																							 * "��ѡ��Ҫɾ��н�ʱ�׼�汾!"
																							 */);
		}

		// У�鱻ɾ���汾�Ƿ񱻶������������
		if (gradevervo.getEffect_flag().booleanValue()) {
			if (getWaGradeDao().isWaGradeverHasReferenced(gradevervo.getPk_wa_grd())) {
				throw new BusinessException(ResHelper.getString("60130paystd", "060130paystd0214")/*
																								 * @
																								 * res
																								 * "�ð汾�Ѿ����������������,������ɾ��!"
																								 */);
			}
		}
		getWaGradeDao().getBaseDao().deleteVO(gradevervo);
		// zhoumxc 20140823 ������ԣ�н�ʱ�׼���汾��
		CacheProxy.fireDataDeleted("wa_grade_ver", gradevervo.getPk_wa_gradever());
		getWaGradeDao().getBaseDao().deleteByClause(WaCriterionVO.class,
				WaGradeVerVO.PK_WA_GRADEVER + " = '" + gradevervo.getPk_wa_gradever() + "'");

	}

	/**
	 * ��ѯн�ʱ�׼�汾��Ϣ
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
	 * ����н�ʱ�׼���������ѯн�ʱ�׼�汾�����汾��
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
	 * н�ʱ䶯���õ���ȡн�ʱ�׼���
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
	 * н�ʱ䶯���õ���ȡн�ʱ�׼���
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
	 * ����н�ʱ�׼���PK��ѯ��Ч��н�ʱ�׼����
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
	 * н�ʱ�׼������Ӽ��𵵱�ʱ�����֤
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
																								 * "��н�ʱ�׼����Ѵ�����Ч�汾���������ӻ���ټ��𵵱�"
																								 */);
			}
		}
	}

	/**
	 * ɾ��н�ʱ�׼�汾��¼����PK
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
																		 * "��н�ʱ�׼����Ѿ�������н����Ŀ���ã���Ч��ǲ���Ϊ�գ�"
																		 */;
		}
		if (getWaGradeDao().gradeHaveWaClassitemReference(strGradepk)) {
			return ResHelper.getString("60130paystd", "060130paystd0232")/*
																		 * @res
																		 * "��н�ʱ�׼����Ѿ���н�ʷ�����Ŀ���ã���Ч��ǲ���Ϊ�գ�"
																		 */;
		}
		if (getWaGradeDao().gradeHavePsnappaproveReference(strGradepk)) {
			return ResHelper.getString("60130paystd", "060130paystd0233")/*
																		 * @res
																		 * "��н�ʱ�׼����Ѿ��������������������ã���Ч��ǲ���Ϊ�գ�"
																		 */;
		}
		if (getWaGradeDao().gradeHavePsndocWadocReference(strGradepk)) {
			return ResHelper.getString("60130paystd", "060130paystd0234")/*
																		 * @res
																		 * "��н�ʱ�׼����Ѿ��������ʹ����������ã���Ч��ǲ���Ϊ�գ�"
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