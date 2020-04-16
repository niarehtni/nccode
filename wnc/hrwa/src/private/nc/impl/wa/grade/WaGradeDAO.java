package nc.impl.wa.grade;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.adjust.AdjustWadocVO;
import nc.vo.wa.adjust.PsnappaproveBVO;
import nc.vo.wa.grade.GradeSortEnum;
import nc.vo.wa.grade.PsnhiHeaderVO;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVerVO;
import nc.vo.wa.grade.WaPrmlvVO;
import nc.vo.wa.grade.WaPsnhiBVO;
import nc.vo.wa.grade.WaPsnhiVO;
import nc.vo.wa.grade.WaSeclvVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wabm.util.WaCacheUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * н�ʱ�׼����Dao
 * 
 * @author: xuhw
 * @date: 2009-11-26 ����09:27:04
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class WaGradeDAO extends BaseDAOManager {
	public WaCriterionVO[] queryByClsPkAndVerPk(String clsPk, String strVerPK, boolean isMultSec) throws DAOException {
		if (StringUtils.isBlank(clsPk)) {
			return null;
		}
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("       select  ");
		sbSql.append("          wc.pk_wa_crt,  ");
		sbSql.append("          wc.pk_wa_grd,  ");
		sbSql.append("          wc.pk_wa_prmlv,  ");
		sbSql.append("          wc.max_value, ");
		sbSql.append("          wc.min_value, ");
		sbSql.append("          wc.pk_wa_seclv,  ");
		sbSql.append("          wc.criterionvalue,  ");
		sbSql.append("           " + SQLHelper.getMultiLangNameColumn("wg.name") + "  as grdName ,  ");
		sbSql.append("          wp.levelname as prmlvName,  ");
		sbSql.append("          ver.pk_wa_gradever ");
		if (isMultSec) {
			sbSql.append(" ,    ws.levelname as seclvName    ");
		}
		sbSql.append("      from  ");
		if (isMultSec) {
			sbSql.append("      wa_seclv ws,   ");
		}
		sbSql.append("          wa_grade wg,  ");
		sbSql.append("          wa_prmlv wp ,  ");
		sbSql.append("          wa_criterion wc , wa_grade_ver ver");
		sbSql.append("      where  ");
		sbSql.append("      1=1 and  ");
		sbSql.append("          wc.pk_wa_grd = wg.pk_wa_grd and  ");
		sbSql.append("          wc.pk_wa_prmlv = wp.pk_wa_prmlv and  ");
		if (!StringUtils.isBlank(strVerPK)) {
			sbSql.append("          wc.pk_wa_gradever = ver.pk_wa_gradever  and ver.pk_wa_gradever = '" + strVerPK
					+ "'");
		} else {
			sbSql.append("              wc.pk_wa_gradever = ver.pk_wa_gradever  and ver.effect_flag = 'Y'");
		}

		sbSql.append("          and wc.pk_wa_grd= '" + clsPk + "' and  ");
		if (isMultSec) {
			sbSql.append("      wc.pk_wa_seclv = ws.pk_wa_seclv    ");
		} else {
			sbSql.append("           pk_wa_seclv ='~'   ");
		}
		sbSql.append("      order by  ");
		sbSql.append("          wc.pk_wa_grd,  ");
		sbSql.append("          wp.displayindex ");
		if (isMultSec) {
			sbSql.append("          ,ws.displayindex ");
		}

		WaCriterionVO[] criterionVOs = executeQueryVOs(sbSql.toString(), WaCriterionVO.class);

		if (!ArrayUtils.isEmpty(criterionVOs)) {
			return criterionVOs;
		}
		return createCriterionVOsByGrade(clsPk, strVerPK);
	}

	public WaCriterionVO[] createCriterionVOsByGrade(String clsPk, String strVerPK) throws DAOException {
		List<WaCriterionVO> lisCrtVos = null;
		try {
			WaPrmlvVO[] prmlvvos = null;
			WaSeclvVO[] seclvVOs = null;
			List<WaPrmlvVO> ddd = (List<WaPrmlvVO>) getBaseDao().retrieveByClause(WaPrmlvVO.class,
					" pk_wa_grd = '" + clsPk + "'", " displayindex ");
			prmlvvos = ddd.toArray(new WaPrmlvVO[0]);
			if (ArrayUtils.isEmpty(prmlvvos)) {
				return null;
			}
			List<WaSeclvVO> ccc = (List<WaSeclvVO>) getBaseDao().retrieveByClause(WaSeclvVO.class,
					" pk_wa_grd = '" + clsPk + "'", " displayindex ");
			seclvVOs = ccc.toArray(new WaSeclvVO[0]);

			lisCrtVos = new ArrayList<WaCriterionVO>();
			for (int i = 0; i < prmlvvos.length; i++) {
				if (ArrayUtils.isEmpty(seclvVOs)) {
					WaCriterionVO criterionvo = new WaCriterionVO();
					criterionvo.setPk_wa_grd(clsPk);
					criterionvo.setPk_wa_prmlv(prmlvvos[i].getPk_wa_prmlv());
					criterionvo.setCriterionvalue(new UFDouble(0));
					criterionvo.setMax_value(new UFDouble(0));
					criterionvo.setMin_value(new UFDouble(0));
					criterionvo.setPk_wa_gradever(strVerPK);
					criterionvo.setPrmlvName(prmlvvos[i].getLevelname());
					criterionvo.setSeclvName(ResHelper.getString("60130paystd", "060130paystd0216")/*
																									 * @
																									 * res
																									 * "��ֵ"
																									 */);
					criterionvo.setGrdName(prmlvvos[i].getLevelname());
					criterionvo.setLevName(ResHelper.getString("60130paystd", "060130paystd0216")/*
																								 * @
																								 * res
																								 * "��ֵ"
																								 */);

					lisCrtVos.add(criterionvo);
				} else {
					for (int j = 0; j < seclvVOs.length; j++) {
						WaCriterionVO criterionvo = new WaCriterionVO();
						criterionvo.setPk_wa_grd(clsPk);
						criterionvo.setPk_wa_prmlv(prmlvvos[i].getPk_wa_prmlv());
						criterionvo.setCriterionvalue(new UFDouble(0));
						criterionvo.setMax_value(new UFDouble(0));
						criterionvo.setMin_value(new UFDouble(0));
						criterionvo.setPk_wa_gradever(strVerPK);
						criterionvo.setPk_wa_seclv(seclvVOs[j].getPk_wa_seclv());
						criterionvo.setPrmlvName(prmlvvos[i].getLevelname());
						criterionvo.setGrdName(prmlvvos[i].getLevelname());
						criterionvo.setSeclvName(seclvVOs[j].getLevelname());
						criterionvo.setLevName(seclvVOs[j].getLevelname());
						lisCrtVos.add(criterionvo);
					}
				}
			}

		} catch (Exception e) {
			throw new DAOException(e.getMessage());
		}
		return lisCrtVos.toArray(new WaCriterionVO[0]);
	}

	// ---------------------------------------------------------------------------------------
	/**
	 * �ж�н�ʱ�׼�Ƿ�н�ʱ�׼������
	 * 
	 * @author xuhw on 2009-11-28
	 * @param strGrdPk
	 * @return
	 * @throws DAOException
	 */
	public boolean gradeHaveCrtReference(String strGrdPk) throws DAOException {
		String sql = " select 1 from WA_GRADE_VER where pk_wa_grd=? and EFFECT_FLAG = 'Y' ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strGrdPk);
		return isValueExist(sql, parameter);
	}

	/**
	 * �ж�н�ʱ�׼����Ƿ񱻶���������
	 * 
	 * @author xuhw on 2009-11-28
	 * @param strGrdPk
	 * @return
	 * @throws DAOException
	 */
	public boolean gradeHavePsnappaproveReference(String strGrdPk) throws DAOException {
		String sql = "select 1 from wa_psnappaprove_b where pk_wa_grd = ? and negotiation = 'N' ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strGrdPk);
		return isValueExist(sql, parameter);
	}

	/**
	 * �ж�н�ʱ�׼����Ƿ�н�ʱ䶯���������
	 * 
	 * @param strGrdPk
	 * @return
	 * @throws DAOException
	 */
	public boolean gradeHavePsndocWadocReference(String strGrdPk) throws DAOException {
		String sql = "select 1 from hi_psndoc_wadoc where pk_wa_grd = ? and negotiation_wage = 'N' ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strGrdPk);
		return isValueExist(sql, parameter);
	}

	/**
	 * �ж�н�ʱ�׼����Ƿ񱻹�����Ŀ������
	 * 
	 * @param strGrdPk
	 * @return
	 * @throws DAOException
	 */
	public boolean gradeHaveWaItemReference(String strGrdPk) throws DAOException {
		String sql = "select 1 from wa_item where vformula like '%" + strGrdPk + "%' ";
		return isValueExist(sql, null);
	}

	/**
	 * �ж�н�ʱ�׼����Ƿ�н�ʷ�����Ŀ������
	 * 
	 * @param strGrdPk
	 * @return
	 * @throws DAOException
	 */
	public boolean gradeHaveWaClassitemReference(String strGrdPk) throws DAOException {
		String sql = "select 1 from wa_classitem where vformula like '%" + strGrdPk + "%' ";
		return isValueExist(sql, null);
	}

	/**
	 * �ж�н�ʱ�׼�����Ƿ�н�ʱ�׼������
	 * 
	 * @author xuhw on 2009-11-28
	 * @param strGrdPk
	 * @return
	 * @throws DAOException
	 */
	public boolean prmlvHaveCrtReference(String strPrmlvPK) throws DAOException {
		String sql = " select 1 from wa_criterion where pk_wa_prmlv=? and criterionvalue>0 ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strPrmlvPK);
		return isValueExist(sql, parameter);
	}

	/**
	 * �ж�н�ʱ�׼�����Ƿ�н�ʱ�׼������
	 * 
	 * @author xuhw on 2009-11-28
	 * @param strGrdPk
	 * @return
	 * @throws DAOException
	 */
	public boolean seclvHaveCrtReference(String strSeclv) throws DAOException {
		String sql = " select 1 from wa_criterion where  pk_wa_seclv=? and criterionvalue>0 ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strSeclv);
		return isValueExist(sql, parameter);
	}

	/**
	 * ��֤��ͬһн�ʶ���������Ƿ����ͬ���ĵ���
	 * 
	 * @author xuhw on 2009-11-28
	 * @param seclvvo
	 * @param isUpdate
	 *            - �Ƿ��Ǹ��²���
	 * @return
	 * @throws DAOException
	 */
	public boolean secHaveLevNameRepeat(WaSeclvVO seclvvo, boolean isUpdate) throws DAOException {
		String sql = " select 1 from wa_seclv where levelname=? and pk_wa_grd=? ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(seclvvo.getLevelname());
		parameter.addParam(seclvvo.getPk_wa_grd());
		if (isUpdate) {
			sql += " and pk_wa_seclv <> ? ";
			parameter.addParam(seclvvo.getPk_wa_seclv());
		}
		return isValueExist(sql, parameter);
	}

	/**
	 * ��֤��ͬһн�ʶ���������Ƿ����ͬ���ļ���
	 * 
	 * @author xuhw on 2009-11-28
	 * @param seclvvo
	 * @param isUpdate
	 *            - �Ƿ��Ǹ��²���
	 * @return
	 * @throws DAOException
	 */
	public boolean prmlvHaveLevNameRepeat(WaPrmlvVO prmlvvo, boolean isUpdate) throws DAOException {
		String sql = "select 1 from wa_prmlv where  levelname=? and pk_wa_grd=?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(prmlvvo.getLevelname());
		parameter.addParam(prmlvvo.getPk_wa_grd());
		if (isUpdate) {
			sql += " and pk_wa_prmlv<> ? ";
			parameter.addParam(prmlvvo.getPk_wa_prmlv());
		}
		return isValueExist(sql, parameter);
	}

	/**
	 * ȡ��н����Ա����ֵ�趨���VO
	 * 
	 * 
	 * @author xuhw on 2009-11-28
	 * @return
	 * @throws DAOException
	 */
	public PsnhiHeaderVO[] queryPsnHiByStdPK(String strGrdPk) throws DAOException

	{
		// shenliangc 20140902
		// ����wa_psnhi����û��vfldname�Ķ����ֶΣ�ֻ�ܴ�hr_infoset_item��ȡ�÷�������vfldname�ֶΡ�
		// ����Զ�����Ϣ�������˶����������֮�������ĵ�½��н�ʱ�׼�ӱ�����Ա����ҳǩ��ͷ��ʾ�������ĵ����⡣
		String sql = "select wp.pk_flddict, wp.vfldcode, wp.pk_wa_grd,wp.pk_wa_psnhi,"
				+ SQLHelper.getMultiLangNameColumn("hf.item_name")
				+ " as vfldname,hf.data_type,hf.ref_model_name , infoset.meta_data_id , wp.classtype,hf.RESPATH, hf.RESID "
				+ " from wa_psnhi wp,hr_infoset_item hf inner join hr_infoset infoset on hf.pk_infoset = infoset.pk_infoset"
				+ " where hf.pk_infoset_item =wp.pk_flddict and pk_wa_grd = ?  ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strGrdPk);
		// parameter.addParam(classType);
		return executeQueryVOs(sql, parameter, PsnhiHeaderVO.class);

	}

	/**
	 * ����н�ʱ�׼���������ȡ�ù�����Ŀ��Ϣ
	 * 
	 * @param strGrdPk
	 * @return
	 * @throws DAOException
	 */
	public WaItemVO getItemVOByGrdPk(String strGrdPk) throws DAOException {
		String sql = "select wa_item.pk_wa_item,wa_item.code, " + SQLHelper.getMultiLangNameColumn("wa_item.name")
				+ " ,wa_item.itemkey,wa_item.iitemtype,wa_item.iflddecimal "
				+ "from wa_item inner join wa_grade on wa_grade.pk_wa_item = wa_item.pk_wa_item where pk_wa_grd = ? ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strGrdPk);
		return executeQueryVO(sql, parameter, WaItemVO.class);
	}

	/**
	 * ȡ��н����Ա����ֵ�趨���VO
	 * 
	 * @author xuhw on 2009-11-28
	 * @return
	 * @throws DAOException
	 */
	public WaPsnhiBVO[] queryStdHiByGrdPk(String strGrdPk, int classType) throws DAOException {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" select pb.pk_wa_psnhi_b, pb.pk_wa_grdlv,psnhi.pk_wa_grd, psnhi.vfldcode, pb.pk_wa_psnhi, pb.vfldvalue, ");
		sbSql.append(" pb.sortgroup from wa_psnhi_b pb inner join wa_psnhi psnhi on psnhi.pk_wa_psnhi = pb.pk_wa_psnhi ");
		sbSql.append(" where psnhi.pk_wa_grd=? and psnhi.classtype = ? order by pb.sortgroup");

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strGrdPk);
		parameter.addParam(classType);
		return executeQueryVOs(sbSql.toString(), parameter, WaPsnhiBVO.class);
	}

	/**
	 * ����н�ʱ�׼���ֵ���� ��ѯ����
	 * 
	 * @author xuhw on 2009-12-7
	 * @param strCrtkey
	 * @param isMultSec
	 * @return
	 * @throws DAOException
	 */
	public WaCriterionVO queryCriterionVOByCrtKey(String strCrtkey, boolean isMultSec) throws DAOException {

		StringBuffer sbSql = new StringBuffer();
		if (isMultSec) {
			sbSql.append(" select wc.pk_wa_grd,wc.pk_wa_prmlv,wc.pk_wa_seclv, wc.criterionvalue,wp.levelname , ws.levelname ");
			sbSql.append(" from wa_criterion wc inner join ");
			sbSql.append(" wa_prmlv wp on wc.pk_wa_prmlv = wp.pk_wa_prmlv left outer join ");
			sbSql.append(" wa_seclv ws on wc.pk_wa_seclv = ws.pk_wa_seclv ");
			sbSql.append(" where wc.pk_wa_crt = ?");
		} else {
			sbSql.append(" select wc.pk_wa_grd,wc.pk_wa_prmlv,wc.pk_wa_seclv, wc.criterionvalue,wp.levelname , wp.levelname ");
			sbSql.append(" from wa_criterion wc inner join ");
			sbSql.append(" wa_prmlv wp on wc.pk_wa_prmlv = wp.pk_wa_prmlv  ");
			sbSql.append(" where wc.pk_wa_crt = ? ");
		}

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strCrtkey);

		return executeQueryVO(sbSql.toString(), parameter, WaCriterionVO.class);
	}

	/**
	 * ����н�ʱ�׼���ֵ���� ��ѯ����
	 * 
	 * @author xuhw on 2009-12-7
	 * @param strCrtkey
	 * @param isMultSec
	 * @return
	 * @throws DAOException
	 */
	public WaCriterionVO queryCriterionByCrtPk(String strCrtkey) throws DAOException {
		StringBuffer sbSql = new StringBuffer();

		sbSql.append(" select t.pk_wa_crt, t.criterionvalue, t.max_value, t.min_value ");
		sbSql.append(" from wa_criterion t  ");
		sbSql.append(" where t.pk_wa_crt = ?");

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strCrtkey);

		return executeQueryVO(sbSql.toString(), parameter, WaCriterionVO.class);
	}

	/**
	 * ����н�ʱ�׼���ֵ���� ��ѯ���� ����
	 * 
	 * @author xuhw on 2009-12-7
	 * @param strCrtkey
	 * @param isMultSec
	 * @return
	 * @throws DAOException
	 */
	public WaCriterionVO[] queryCriterionVOsByCrtKey(String strPkWaGrd, boolean isMultSec) throws DAOException {

		StringBuffer sbsql = new StringBuffer();
		if (isMultSec) {
			sbsql.append(" select wc.*,wp.levelname , ws.levelname ");
			sbsql.append(" from wa_criterion wc inner join ");
			sbsql.append(" wa_grade_ver ver on wc.pk_wa_gradever = ver.pk_wa_gradever and ver.effect_flag = 'Y'");
			sbsql.append("  inner join ");
			sbsql.append(" wa_prmlv wp on wc.pk_wa_prmlv = wp.pk_wa_prmlv left outer join ");
			sbsql.append(" wa_seclv ws on wc.pk_wa_seclv = ws.pk_wa_seclv ");
			sbsql.append(" where wc.pk_wa_grd = ? ");
		} else {
			sbsql.append(" select wc.*,wp.levelname , wp.levelname ");
			sbsql.append(" from wa_criterion wc inner join ");
			sbsql.append(" wa_grade_ver ver on wc.pk_wa_gradever = ver.pk_wa_gradever and ver.effect_flag = 'Y'");
			sbsql.append("  inner join ");
			sbsql.append(" wa_prmlv wp on wc.pk_wa_prmlv = wp.pk_wa_prmlv  ");
			sbsql.append(" where wc.pk_wa_grd = ? ");
		}

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strPkWaGrd);

		return executeQueryVOs(sbsql.toString(), parameter, WaCriterionVO.class);
	}

	/**
	 * ���ݼ���PKɾ��н�ʱ�׼���ֵ������
	 * 
	 * @author xuhw on 2009-12-15
	 * @param lisDelPrmlvVoPK
	 * @throws BusinessException
	 */
	public void deleteCrtByPrmlvPk(List<String> lisDelPrmlvVoPK) throws BusinessException {
		int intCnt = lisDelPrmlvVoPK.size();
		StringBuffer sbWhere = new StringBuffer();
		SQLParameter sqlParmeter = new SQLParameter();
		sbWhere.append(" pk_wa_prmlv in ( ");
		for (int i = 0; i < intCnt; i++) {
			if (i != (intCnt - 1)) {
				sbWhere.append(" ? ,");
			} else {
				sbWhere.append(" ? ");
			}
			sqlParmeter.addParam(lisDelPrmlvVoPK.get(i));
		}
		sbWhere.append(" )");
		getBaseDao().deleteByClause(WaCriterionVO.class, sbWhere.toString(), sqlParmeter);
	}

	/**
	 * ���ݵ���PKɾ��н�ʱ�׼���ֵ������
	 * 
	 * @author xuhw on 2009-12-15
	 * @param lisDelPrmlvVoPK
	 * @throws BusinessException
	 */
	public void deleteCrtBySeclvPk(List<String> lisDelSeclvVoPK) throws BusinessException {
		int intCnt = lisDelSeclvVoPK.size();
		StringBuffer sbWhere = new StringBuffer();
		SQLParameter sqlParmeter = new SQLParameter();
		sbWhere.append(" pk_wa_seclv in ( ");
		for (int i = 0; i < intCnt; i++) {
			if (i != (intCnt - 1)) {
				sbWhere.append(" ? ,");
			} else {
				sbWhere.append(" ? ");
			}
			sqlParmeter.addParam(lisDelSeclvVoPK.get(i));
		}
		sbWhere.append(" )");
		getBaseDao().deleteByClause(WaCriterionVO.class, sbWhere.toString(), sqlParmeter);
	}

	/**
	 * �������PK��Ա�����ӱ�����
	 * 
	 * @author xuhw on 2009-12-15
	 * @param lisDelPrmlvVoPK
	 * @throws BusinessException
	 */
	public void deletePsnHiBByGradePK(String strGrdPK) throws BusinessException {
		StringBuffer sbsql = new StringBuffer();
		sbsql.append(" pk_wa_psnhi in ( select pk_wa_psnhi from wa_psnhi where pk_wa_grd = ?) ");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strGrdPK);
		getBaseDao().deleteByClause(WaPsnhiBVO.class, sbsql.toString(), parameter);
		WaCacheUtils.synCache(WaPsnhiBVO.TABLENAME);
	}

	/**
	 * ͨ����λ���뷵��ָ����˾���м�¼VO���顣�����λ����Ϊ�շ������м�¼��
	 * 
	 * ��֪���⣺��ע�����ɵ�sql��䣺where�Ӿ��м��蹫˾�����ֶ�Ϊpk_corp�� �����Ҫ��Թ�˾���в�ѯ����ôӦ�������ʵ���ֶ������ֹ��޸�
	 * sql��䡣
	 */
	public WaCriterionVO[] queryAllClsPk() throws DAOException {
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select wa_criterion.pk_wa_crt, ");
		sqlB.append("       wa_criterion.pk_wa_grd, ");
		sqlB.append("       wa_criterion.pk_wa_prmlv, ");
		sqlB.append("       wa_criterion.pk_wa_seclv, ");
		sqlB.append("       wa_criterion.criterionvalue, ");
		sqlB.append("       wa_grade.wagradename, ");
		sqlB.append("       wa_prmlv.levelname as prmlvname, ");
		sqlB.append("       wa_seclv.levelname as seclvname ");
		sqlB.append("  from wa_criterion  ");
		sqlB.append("  inner join wa_grade on wa_criterion.pk_wa_grd = wa_grade.pk_wa_grd  ");
		sqlB.append("  inner join wa_prmlv on wa_criterion.pk_wa_prmlv = wa_prmlv.pk_wa_prmlv  ");
		sqlB.append("  left outer join wa_seclv on wa_criterion.pk_wa_seclv = wa_seclv.pk_wa_seclv ");
		sqlB.append(" order by wa_criterion.pk_wa_grd, ");
		sqlB.append("          wa_prmlv.displayindex, ");
		sqlB.append("          wa_seclv.displayindex ");
		return executeQueryVOs(sqlB.toString(), WaCriterionVO.class);
	}

	/**
	 * ��ѯ��Ϣ��������͵�Ҷ�ӱ��
	 * 
	 * @author xuhw on 2010-4-17
	 * @param tableCode
	 * @param fldCode
	 * @return
	 * @throws DAOException
	 */
	public InfoItemVO queryInfoItemByInf(String tableCode, String fldCode) throws DAOException {
		String sql;
		sql = "select item.ref_leaf_flag from hr_infoset_item item inner join hr_infoset infoset on item.pk_infoset_item = infoset.pk_infoset "
				+ "where infoset.table_code = ? and item.code = ? ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(tableCode);
		parameter.addParam(fldCode);
		return executeQueryVO(sql, parameter, InfoItemVO.class);
	}

	/**
	 * ȡ�ò�����Ϣ
	 * 
	 * @author xuhw on 2010-6-2
	 * @throws DAOException
	 * @see nc.itf.hr.wa.IWaGradeQueryService#queryBdRefInfoList()
	 */
	public Map<String, RefInfoVO> queryBdRefInfoList() throws DAOException {
		Map<String, RefInfoVO> refinfoMap = new HashMap<String, RefInfoVO>();
		// String sql =
		// " select distinct pk_refinfo,refclass,name,para1 from bd_refinfo where modulename like 'uap%' or   modulename like 'hr%'  ";
		String sql = " select distinct pk_refinfo,refclass,name,para1,resid,residpath from bd_refinfo where modulename like 'uap%' or   modulename like 'hr%'  ";
		// 20151029 xiejie3 NCdp205514609 н�ʱ�׼�����ü�����Ա����Ϊ��Ա���ʱ���Ӽ�������Ա���Ĳ��մ򲻿�
		// ԭ��uap�Ĳ���modulename��uap��Ϊ��riart..,��ѯ����ʱ��Ҫ�����ⲿ�֡�
		sql += " or  modulename like 'riart%' ";
		// end

		RefInfoVO[] refinfovos = executeQueryVOs(sql, RefInfoVO.class);
		if (refinfovos == null) {
			return refinfoMap;
		}
		for (RefInfoVO refinfovo : refinfovos) {
			refinfoMap.put(refinfovo.getPrimaryKey(), refinfovo);
		}
		return refinfoMap;
	}

	/**
	 * ȡ��ö��ֵ
	 * 
	 * @author suihang on 2011-10-26
	 * @throws DAOException
	 * @see nc.itf.hr.wa.IWaGradeQueryService#queryBdRefInfoList()
	 */
	public GeneralVO[] queryBdEnumValues(String strPk) throws DAOException {
		GeneralVO[] enumValues = new GeneralVO[] {};
		String sql = " select md_enumvalue.enumsequence,md_enumvalue.id,md_enumvalue.name,md_enumvalue.resid,md_enumvalue.value"
				+ ",md_component.resmodule from hr_infoset_item "
				+ "inner join md_enumvalue on hr_infoset_item.enum_id=md_enumvalue.id "
				+ "inner join md_class on md_class.id =md_enumvalue.id  "
				+ "inner join md_component on  md_class.componentid = md_component.id"
				+ " where hr_infoset_item.pk_infoset_item=? ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strPk);

		enumValues = executeQueryVOs(sql, parameter, GeneralVO.class);

		return enumValues;
	}

	/**
	 * ��ö�Ӧ���ڼ� 0--year;1-->period
	 * 
	 * @return java.lang.String[]
	 * @param usedate
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                �쳣˵����
	 */
	public String[] getperiod(String usedate) throws DAOException {
		String sql = " select distinct pk_refinfo,refclass,name from bd_refinfo ";
		String[] re = new String[2];
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(usedate);
		parameter.addParam(usedate);
		re = (String[]) this.getBaseDao().executeQuery(sql, parameter, new ResultSetProcessor() {
			public Object handleResultSet(ResultSet rs) throws SQLException {
				String[] re = new String[2];
				if (rs.next()) {
					re[0] = rs.getString(1);
					re[1] = rs.getString(2);
				}
				return re;
			}
		});

		return re;
	}

	/**
	 * ��ѯн�ʱ�׼���ֵ
	 * 
	 * @author xuhw on 2009-11-28
	 * @param clsPk
	 * @param isMultSec
	 * @return
	 * @throws DAOException
	 */
	public WaCriterionVO[] queryByClsPk(String clsPk, boolean isMultSec, int sortTpye) throws DAOException {

		/*************************************************************/
		// ������ϵͳ����ӿڣ�
		/*************************************************************/
		StringBuffer sbSql = new StringBuffer();
		if (!isMultSec) {
			// ����
			sbSql.append(" select wc.pk_wa_crt, ");
			sbSql.append("        wc.pk_wa_grd, ");
			sbSql.append("        wc.pk_wa_prmlv, ");
			sbSql.append("        wc.max_value, ");
			sbSql.append("        wc.min_value, ");
			sbSql.append("        wc.pk_wa_seclv, ");
			sbSql.append("        wc.criterionvalue, ");
			sbSql.append("         " + SQLHelper.getMultiLangNameColumn("wg.name") + " , ");
			sbSql.append("        wp.levelname as prmlvname ");
			sbSql.append("   from wa_criterion wc, wa_grade wg, wa_prmlv wp ,wa_grade_ver ver ");
			sbSql.append("  where ");
			sbSql.append("    wc.pk_wa_grd = wg.pk_wa_grd ");
			sbSql.append("    and wc.pk_wa_prmlv = wp.pk_wa_prmlv ");
			sbSql.append("     and wg.pk_wa_grd = ver.pk_wa_grd and ver.effect_flag = 'Y' and ");
			sbSql.append("     wc.pk_wa_gradever = ver.pk_wa_gradever  ");
			sbSql.append("    and wc.pk_wa_grd = ? ");
			sbSql.append("    and  pk_wa_seclv ='~'  ");
			sbSql.append("  order by wp.displayindex ");
		} else {
			// �൵
			sbSql.append("  select wc.pk_wa_crt, ");
			sbSql.append("         wc.pk_wa_grd, ");
			sbSql.append("         wc.pk_wa_prmlv, ");
			sbSql.append("         wc.max_value, ");
			sbSql.append("         wc.min_value, ");
			sbSql.append("         wc.pk_wa_seclv, ");
			sbSql.append("         wc.criterionvalue, ");
			sbSql.append("          " + SQLHelper.getMultiLangNameColumn("wg.name") + " , ");
			sbSql.append("         wp.levelname as prmlvname, ");
			sbSql.append("         ws.levelname as levName ");
			sbSql.append("    from wa_criterion wc, wa_grade wg, wa_prmlv wp, wa_seclv ws ,wa_grade_ver ver ");
			sbSql.append("   where ");
			sbSql.append("     wc.pk_wa_grd = wg.pk_wa_grd ");
			sbSql.append("     and wg.pk_wa_grd = ver.pk_wa_grd and ver.effect_flag = 'Y' and ");
			sbSql.append("     wc.pk_wa_gradever = ver.pk_wa_gradever  ");
			sbSql.append("     and wc.pk_wa_prmlv = wp.pk_wa_prmlv ");
			sbSql.append("     and wc.pk_wa_seclv = ws.pk_wa_seclv ");
			sbSql.append("     and wc.pk_wa_grd = ? ");
			if (sortTpye == GradeSortEnum.PRM_UP_SEC_UP) {
				// �������򣬵�������
				sbSql.append("   order by wp.displayindex  ");
				if (isMultSec) {
					sbSql.append("   , ws.displayindex ");
				}
			} else if (sortTpye == GradeSortEnum.PRM_UP_SEC_DOWN) {
				// �������򣬵�����
				sbSql.append("   order by wp.displayindex ");
				if (isMultSec) {
					sbSql.append("    , ws.displayindex desc ");
				}
			} else if (sortTpye == GradeSortEnum.PRM_DOWN_SEC_DOWN) {
				// �����򣬵�����
				sbSql.append("   order by wp.displayindex desc   ");
				if (isMultSec) {
					sbSql.append("     , ws.displayindex desc ");
				}
			} else if (sortTpye == GradeSortEnum.PRM_DOWN_SEC_UP) {
				// �����򣬵�������
				sbSql.append("   order by wp.displayindex desc  ");
				if (isMultSec) {
					sbSql.append("   , ws.displayindex ");
				}
			}
		}

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(clsPk);

		return this.executeQueryVOs(sbSql.toString(), parameter, WaCriterionVO.class);
	}

	/**
	 * ����н�ʱ�׼���ֵ���� ��ѯ����
	 * 
	 * @author xuhw on 2009-12-7
	 * @param strCrtkey
	 * @return
	 * @throws DAOException
	 */
	public WaCriterionVO queryCriterionVOByCrtKey(String strCrtkey) throws DAOException {

		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" select  wc.pk_wa_crt, wc.criterionvalue,wc.min_value, wc.max_value ");
		sbSql.append(" from wa_criterion wc");
		sbSql.append(" where wc.pk_wa_crt = ?");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strCrtkey);

		return this.executeQueryVO(sbSql.toString(), parameter, WaCriterionVO.class);
	}

	// ----------��汾����-------------
	/**
	 * ����н�ʱ�׼���������ѯн�ʱ�׼�汾��Ϣ
	 * 
	 * @param clsPk
	 * @return
	 * @throws DAOException
	 */
	public WaGradeVerVO[] queryVerVOByGradePk(String clsPk) throws DAOException {

		Collection<WaGradeVerVO> gradeverVOs = getBaseDao().retrieveByClause(WaGradeVerVO.class,
				WaGradeVerVO.PK_WA_GRD + " = '" + clsPk + "'", " gradever_num ");
		return gradeverVOs == null ? null : gradeverVOs.toArray(new WaGradeVerVO[0]);
	}

	/**
	 * ����н�ʱ�׼���������ѯ��Ч��н�ʱ�׼�汾��Ϣ
	 * 
	 * @param clsPk
	 * @return
	 * @throws DAOException
	 */
	public WaGradeVerVO queryEffectGradeVerByGradePK(String clsPk) throws DAOException {

		Collection<WaGradeVerVO> gradeverVOs = getBaseDao().retrieveByClause(WaGradeVerVO.class,
				WaGradeVerVO.PK_WA_GRD + " = '" + clsPk + "' and " + WaGradeVerVO.EFFECT_FLAG + " = 'Y' ",
				" gradever_num ");
		WaGradeVerVO[] vos = gradeverVOs.toArray(new WaGradeVerVO[0]);
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		} else {
			return vos[0];
		}
	}

	/**
	 * ����н�ʱ�׼�汾��Ϣ
	 * 
	 * @param clsPk
	 * @return
	 * @throws DAOException
	 */
	public void updateGradeVerVOs(WaGradeVerVO[] gradeervos) throws DAOException {
		getBaseDao().updateVOArray(gradeervos);
	}

	/**
	 * ����н�ʱ�׼�汾��Ϣ
	 * 
	 * @param clsPk
	 * @return
	 * @throws DAOException
	 */
	public void insertGradeVerVOs(WaGradeVerVO[] gradeervos) throws DAOException {
		getBaseDao().insertVOArray(gradeervos);
	}

	/**
	 * ɾ��н�ʱ�׼�汾��Ϣ
	 * 
	 * @param clsPk
	 * @return
	 * @throws DAOException
	 */
	public void deleeGradeVerVOs(WaGradeVerVO[] gradeervos) throws DAOException {
		getBaseDao().deleteVOArray(gradeervos);
	}

	/**
	 * ������Ч��־
	 * 
	 * @param vervo
	 * @throws DAOException
	 */
	public void updateEffectFlag(WaGradeVerVO vervo) throws DAOException {
		StringBuffer sbSql = new StringBuffer();
		if (vervo.getEffect_flag() != null && vervo.getEffect_flag().booleanValue()) {
			sbSql.append(" update wa_grade_ver set effect_flag= 'N' where pk_wa_grd = '" + vervo.getPk_wa_grd() + "'");
			sbSql.append(" and pk_wa_gradever <>  '" + vervo.getPk_wa_gradever() + "'");
			getBaseDao().executeUpdate(sbSql.toString());
		}
	}

	/**
	 * ����н�ʱ���PK�͵���PK��ѯ��Ч��н�ʱ�׼��
	 * 
	 * @param strPKPrmlv
	 * @param strPKSeclv
	 * @return
	 * @throws DAOException
	 */
	public WaCriterionVO getCrierionVOByPrmSec(String strPKPrmlv, String strPKSeclv) throws DAOException {
		StringBuffer sbSql = new StringBuffer();

		sbSql.append(" select ");
		sbSql.append("  *  ");
		sbSql.append("  from ");
		sbSql.append("     wa_criterion ");
		sbSql.append("         inner join wa_grade_ver ");
		sbSql.append("         on wa_criterion.pk_wa_gradever = wa_grade_ver.pk_wa_gradever and ");
		sbSql.append("         wa_grade_ver.effect_flag = 'Y' ");
		sbSql.append("  where ");
		sbSql.append("      wa_criterion.pk_wa_prmlv = ? and ");
		sbSql.append("     (wa_criterion.pk_wa_seclv = ? or  wa_criterion.pk_wa_seclv ='~') ");

		SQLParameter para = new SQLParameter();
		para.addParam(strPKPrmlv);
		para.addParam(strPKSeclv);

		return executeQueryVO(sbSql.toString(), para, WaCriterionVO.class);
	}

	/**
	 * ����н�ʱ���PK�͵���PK���z�����ڲ�ѯ�����汾��Ч��н�ʱ�׼��
	 * 
	 * @param strPKPrmlv
	 * @param strPKSeclv
	 * @return
	 * @throws DAOException
	 * 
	 * @author sunsx
	 * @since 2020-01-16
	 */
	@SuppressWarnings("unchecked")
	public WaCriterionVO getCrierionVOByPrmSec(UFLiteralDate checkDate, String strPKPrmlv, String strPKSeclv)
			throws DAOException {
		SQLParameter para = new SQLParameter();
		para.addParam(strPKPrmlv);
		para.addParam(strPKSeclv);

		Collection<WaGradeVerVO> gradeVerVOs = this
				.getBaseDao()
				.retrieveByClause(
						WaGradeVerVO.class,
						"pk_wa_grd in (select distinct pk_wa_grd from wa_criterion where  pk_wa_prmlv = ? and (pk_wa_seclv = ? or  pk_wa_seclv ='~'))",
						para);

		String pkVer = "";
		int verTerm = Integer.MAX_VALUE;
		for (WaGradeVerVO verVO : gradeVerVOs) {
			UFLiteralDate verDate = verVO.getVer_create_date().toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE);
			if (StringUtils.isEmpty(pkVer)) {
				pkVer = verVO.getPk_wa_gradever();
				verTerm = UFLiteralDate.getDaysBetween(verDate, checkDate);
			} else {
				if (UFLiteralDate.getDaysBetween(verDate, checkDate) >= 0
						&& UFLiteralDate.getDaysBetween(verDate, checkDate) < verTerm) {
					verTerm = UFLiteralDate.getDaysBetween(verDate, checkDate);
					pkVer = verVO.getPk_wa_gradever();
				}
			}
		}

		StringBuffer sbSql = new StringBuffer();

		sbSql.append(" select ");
		sbSql.append("  *  ");
		sbSql.append("  from ");
		sbSql.append("     wa_criterion ");
		sbSql.append("         inner join wa_grade_ver ");
		sbSql.append("         on wa_criterion.pk_wa_gradever = wa_grade_ver.pk_wa_gradever and ");
		sbSql.append("         wa_grade_ver.pk_wa_gradever = ?  ");
		sbSql.append("  where ");
		sbSql.append("      wa_criterion.pk_wa_prmlv = ? and ");
		sbSql.append("     (wa_criterion.pk_wa_seclv = ? or  wa_criterion.pk_wa_seclv ='~') ");

		para = new SQLParameter();
		para.addParam(pkVer);
		para.addParam(strPKPrmlv);
		para.addParam(strPKSeclv);

		return executeQueryVO(sbSql.toString(), para, WaCriterionVO.class);
	}

	// end

	/**
	 * ����н�ʱ���PK�͵���PK��ѯ��Ч��н�ʱ�׼��
	 * 
	 * @param strPKPrmlv
	 * @param strPKSeclv
	 * @return
	 * @throws DAOException
	 */
	public HashMap<String, WaCriterionVO> getCrierionVOMapByPrmSec(AdjustWadocVO[] adjustWadocPsnInfoVOs)
			throws DAOException {
		StringBuffer sbSql = new StringBuffer();
		if (ArrayUtils.isEmpty(adjustWadocPsnInfoVOs)) {
			return null;
		}
		HashMap<String, WaCriterionVO> CrierionVOsMap = new HashMap<String, WaCriterionVO>();
		for (AdjustWadocVO adjustWadocVO : adjustWadocPsnInfoVOs) {
			if (!adjustWadocVO.getNegotiation().booleanValue()) {
				CrierionVOsMap.put(adjustWadocVO.getPk_wa_prmlv() + adjustWadocVO.getPk_wa_seclv(), null);
			}
		}
		if (CrierionVOsMap.size() == 0) {
			return CrierionVOsMap;
		}
		for (String crt : CrierionVOsMap.keySet()) {
			sbSql.append(" union ");
			sbSql.append(" select ");
			sbSql.append("  *  ");
			sbSql.append("  from ");
			sbSql.append("     wa_criterion ");
			sbSql.append("         inner join wa_grade_ver ");
			sbSql.append("         on wa_criterion.pk_wa_gradever = wa_grade_ver.pk_wa_gradever and ");
			sbSql.append("         wa_grade_ver.effect_flag = 'Y' ");
			sbSql.append("  where ");
			sbSql.append("      wa_criterion.pk_wa_prmlv = '" + crt.substring(0, 20) + "' and ");
			sbSql.append("     (wa_criterion.pk_wa_seclv = '" + crt.substring(20)
					+ "' or  wa_criterion.pk_wa_seclv ='~') ");

		}
		CrierionVOsMap.clear();
		WaCriterionVO[] waCriterionVOs = executeQueryVOs(sbSql.toString().substring(6), WaCriterionVO.class);
		for (WaCriterionVO waCriterionVO : waCriterionVOs) {
			CrierionVOsMap.put(waCriterionVO.getPk_wa_prmlv() + waCriterionVO.getPk_wa_seclv(), waCriterionVO);
		}
		return CrierionVOsMap;
	}

	/**
	 * ����н�ʱ���PK�͵���PK��ѯ��Ч��н�ʱ�׼��
	 * 
	 * @param strPKPrmlv
	 * @param strPKSeclv
	 * @return
	 * @throws DAOException
	 */
	public HashMap<String, WaCriterionVO> getCrierionVOMapByPrmSec(PsnappaproveBVO[] psnappaproveBVOs, boolean isApprove)
			throws DAOException {
		StringBuffer sbSql = new StringBuffer();
		if (ArrayUtils.isEmpty(psnappaproveBVOs)) {
			return null;
		}
		HashMap<String, WaCriterionVO> CrierionVOsMap = new HashMap<String, WaCriterionVO>();
		for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
			if (!psnappaproveBVO.getNegotiation().booleanValue()) {
				if (isApprove) {
					CrierionVOsMap.put(psnappaproveBVO.getPk_wa_prmlv_cofm() + psnappaproveBVO.getPk_wa_seclv_cofm(),
							null);
				} else {
					CrierionVOsMap.put(psnappaproveBVO.getPk_wa_prmlv_apply() + psnappaproveBVO.getPk_wa_seclv_apply(),
							null);
				}
			}
		}
		if (CrierionVOsMap.size() == 0) {
			return CrierionVOsMap;
		}
		// 2015-10-09 zhousze ������������������У�飺������һ��������ǣ�����н�ʱ�׼��������ʱ�����û��ѡ�С�н�ʱ�׼����
		// ֱ����д���ᱨ��������CrierionVOsMap�е�key��value��Ϊnull����substring��ȡ��ʱ��û��20λ�����±������ڵ�
		// �޸ķ����������֮ǰCrierionVOsMapû�и�keyֵ������sbSqlΪ�գ����������forѭ�� begin
		if (CrierionVOsMap.toString().length() >= 20) {
			for (String crt : CrierionVOsMap.keySet()) {
				sbSql.append(" union ");
				sbSql.append(" select ");
				sbSql.append("  *  ");
				sbSql.append("  from ");
				sbSql.append("     wa_criterion ");
				sbSql.append("         inner join wa_grade_ver ");
				sbSql.append("         on wa_criterion.pk_wa_gradever = wa_grade_ver.pk_wa_gradever and ");
				sbSql.append("         wa_grade_ver.effect_flag = 'Y' ");
				sbSql.append("  where ");
				sbSql.append("      wa_criterion.pk_wa_prmlv = '" + crt.substring(0, 20) + "' and ");
				sbSql.append("     (wa_criterion.pk_wa_seclv = '" + crt.substring(20)
						+ "' or  wa_criterion.pk_wa_seclv ='~') ");

			}
			CrierionVOsMap.clear();
			WaCriterionVO[] waCriterionVOs = executeQueryVOs(sbSql.toString().substring(6), WaCriterionVO.class);
			for (WaCriterionVO waCriterionVO : waCriterionVOs) {
				CrierionVOsMap.put(waCriterionVO.getPk_wa_prmlv() + waCriterionVO.getPk_wa_seclv(), waCriterionVO);
			}
		}
		// end
		return CrierionVOsMap;
	}

	/**
	 * ����н�ʱ�׼���PK��ѯ��Ч��н�ʱ�׼����
	 * 
	 * @param strPKGrade
	 * @return
	 * @throws DAOException
	 */
	public WaCriterionVO[] getEffectCrierionsVOByGradePK(String strPKGrade) throws DAOException {
		StringBuffer sbSql = new StringBuffer();

		sbSql.append(" select ");
		sbSql.append("  *  ");
		sbSql.append("  from ");
		sbSql.append("     wa_criterion ");
		sbSql.append("         inner join wa_grade_ver ");
		sbSql.append("         on wa_criterion.pk_wa_gradever = wa_grade_ver.pk_wa_gradever and ");
		sbSql.append("         wa_grade_ver.effect_flag = 'Y' ");
		sbSql.append("  where ");
		sbSql.append("      wa_criterion.pk_wa_grd = ? ");

		SQLParameter para = new SQLParameter();
		para.addParam(strPKGrade);

		return executeQueryVOs(sbSql.toString(), para, WaCriterionVO.class);
	}

	/**
	 * �ж�н�ʱ�׼�Ƿ񱻶���������
	 * 
	 * @param strPKWaGrade
	 * @return
	 */
	public boolean isWaGradeverHasReferenced(String strPKWaGrade) throws DAOException {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" select 1 from hi_psndoc_wadoc where pk_wa_grd = ? ");
		sbSql.append(" union  ");
		sbSql.append(" select 1 from wa_psnappaprove_b where pk_wa_grd = ? ");

		SQLParameter para = new SQLParameter();
		para.addParam(strPKWaGrade);
		para.addParam(strPKWaGrade);
		return isValueExist(sbSql.toString(), para);
	}

	public boolean validateEffectVersion(String strPKWaGrade) throws DAOException {
		String sql = "select 1 from wa_grade_ver where pk_wa_grd = ? and effect_flag = 'Y'";
		SQLParameter para = new SQLParameter();
		para.addParam(strPKWaGrade);
		return isValueExist(sql, para);
	}

	public void deleteWaPsnhiByPK(String strPKWaGrade, int classType) throws DAOException {
		// ɾ����Ա��������
		String sql = "  wa_psnhi_b.pk_wa_psnhi in (select pk_wa_psnhi from wa_psnhi where pk_wa_grd = ? and classtype = ? )";
		SQLParameter para = new SQLParameter();
		para.addParam(strPKWaGrade);
		para.addParam(classType);
		getBaseDao().deleteByClause(WaPsnhiBVO.class, sql.toString(), para);
		// ɾ����Ա����
		sql = "  pk_wa_grd = ?  and classtype = ? ";
		para = new SQLParameter();
		para.addParam(strPKWaGrade);
		para.addParam(classType);
		getBaseDao().deleteByClause(WaPsnhiVO.class, sql.toString(), para);
		// WaCacheUtils.synCache(WaPsnhiVO.TABLENAME);
	}

	public void deleteVersionByGradePK(String strPKWaGrade) throws DAOException {

		// ɾ���汾��
		String sql = " pk_wa_grd = ? ";
		SQLParameter para = new SQLParameter();
		para.addParam(strPKWaGrade);
		getBaseDao().deleteByClause(WaCriterionVO.class, sql.toString(), para);

		// н�ʱ�׼���ֵ��
		sql = " pk_wa_grd = ? ";
		para = new SQLParameter();
		para.addParam(strPKWaGrade);
		getBaseDao().deleteByClause(WaGradeVerVO.class, sql.toString(), para);

	}

	// 20150902 shenliangc ���SONAR���⣬��ְ�������Ͷ�Ӧ��ְ���б�����ͳһ��ѯ������
	@SuppressWarnings("unchecked")
	public HashMap<String, String> queryJoblevelsysNameByJoblevelPk() throws DAOException {
		String sql = "select om_joblevel.pk_joblevel, om_joblevelsys.name "
				+ "from om_joblevelsys inner join om_joblevel on om_joblevelsys.pk_joblevelsys = om_joblevel.pk_joblevelsys ";
		return (HashMap<String, String>) this.getBaseDao().executeQuery(sql, new ResultSetProcessor() {
			public Object handleResultSet(ResultSet rs) throws SQLException {
				HashMap<String, String> nameMap = new HashMap<String, String>();
				while (rs.next()) {
					nameMap.put(rs.getString(1), rs.getString(2));
				}
				return nameMap;
			}
		});
	}

}
