package nc.impl.hrwa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.hr.utils.InSQLCreator;
import nc.impl.pub.ace.AceProjsalaryPubServiceImpl;
import nc.impl.pub.util.db.InSqlManager;
import nc.impl.wa.paydata.PaydataDAO;
import nc.itf.hrwa.IProjsalaryMaintain;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.vo.wa.projsalary.ProjSalaryHVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.util.WaConstant;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class ProjsalaryMaintainImpl extends AceProjsalaryPubServiceImpl implements IProjsalaryMaintain {

	@Override
	public void delete(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggProjSalaryVO[] insert(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggProjSalaryVO[] update(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggProjSalaryVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggProjSalaryVO[] save(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggProjSalaryVO[] unsave(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggProjSalaryVO[] approve(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggProjSalaryVO[] unapprove(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	@Override
	public DataVO[] qryPayDataByCondition(WaLoginContext waContext, String whereCondition) throws BusinessException {
		String[] pk_wa_datas = getPaydataService().queryPKSByCondition(waContext, whereCondition, null);
		if (ArrayUtils.isEmpty(pk_wa_datas)) {
			return new DataVO[0];
		}

		InSQLCreator inSQLCreator = new InSQLCreator();
		try {
			String conditon = inSQLCreator.getInSQL(pk_wa_datas);
			DataVO[] dataVOArrays = getPaydataService().queryByPKSCondition(conditon, "");
			List<DataVO> dataVOList = new ArrayList<DataVO>();
			Map<String, DataVO> dataVOMap = new HashMap<String, DataVO>();
			for (DataVO dataVO : dataVOArrays) {
				dataVOMap.put(dataVO.getPk_wa_data(), dataVO);
			}
			for (String str_pk_wa_data : pk_wa_datas) {
				dataVOList.add(dataVOMap.get(str_pk_wa_data));
			}

			return dataVOList.toArray(new DataVO[0]);
		} finally {
			inSQLCreator.clear();
		}
	}

	private PaydataDAO paydataService;
	private BaseDAO dao;

	protected PaydataDAO getPaydataService() throws DAOException {
		if (null == paydataService) {
			paydataService = new PaydataDAO();
		}
		return paydataService;
	}

	protected BaseDAO getBaseDAO() {
		if (null == dao) {
			dao = new BaseDAO();
		}
		return dao;
	}

	/**
	 * @Description: 查询方案期间内容所有的专案薪资数据.
	 * @param condition
	 *            方案期间条件
	 * @return Map<String,ProjSalaryHVO>
	 * @throws BusinessException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, ProjSalaryHVO> qryClassItemProjHVOMap(String condition) throws BusinessException {
		Map<String, ProjSalaryHVO> resultMap = new HashMap<String, ProjSalaryHVO>();
		if (StringUtils.isNotEmpty(condition)) {
			List<ProjSalaryHVO> tempList = (List<ProjSalaryHVO>) getBaseDAO().retrieveByClause(ProjSalaryHVO.class,
					condition + " and dr<>1 ");
			if (null != tempList && !tempList.isEmpty()) {
				for (ProjSalaryHVO hvo : tempList) {
					resultMap.put(hvo.getID(), hvo);
				}
			}
		}
		return resultMap;
	}

	/**
	 * @Description: 检查方案期间内薪资项目的专案唯一性.
	 */
	@Override
	public boolean isClassItemUnionProj(Map<String, ProjSalaryHVO> periodItemMap, ProjSalaryHVO hvo) {
		if (null != hvo) {
			if (null != periodItemMap.get(hvo.getID())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Description: 查询薪资项目信息.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, WaClassItemVO> qryClassItemByPeriod(String whereCondition, String[] keyFields)
			throws BusinessException {
		Map<String, WaClassItemVO> itemVOMap = new HashMap<String, WaClassItemVO>();
		if (StringUtils.isNotEmpty(whereCondition)) {
			List<WaClassItemVO> tempList = (List<WaClassItemVO>) getBaseDAO().retrieveByClause(WaClassItemVO.class,
					whereCondition);
			if (null != tempList && !tempList.isEmpty()) {
				if (!ArrayUtils.isEmpty(keyFields)) {
					for (WaClassItemVO vo : tempList) {
						StringBuilder keySB = new StringBuilder();
						for (String attr : keyFields) {
							keySB.append(String.valueOf(vo.getAttributeValue(attr)));
						}
						itemVOMap.put(keySB.toString(), vo);
					}
				}
			}
		}
		return itemVOMap;
	}

	/**
	 * @Description: 查询专案信息.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, DefdocVO> qryProjectMap(String whereCondition, String[] keyFields) throws BusinessException {
		Map<String, DefdocVO> projVOMap = new HashMap<String, DefdocVO>();
		StringBuilder filter = new StringBuilder(" pk_defdoclist in (select pk_defdoclist from bd_defdoclist ");
		filter.append(" where code='").append(WaConstant.DEF_CODE_PROJ).append("') ");
		filter.append(" and enablestate=2 ");
		if (StringUtils.isNotEmpty(whereCondition)) {
			filter.append(" and ").append(whereCondition);
		}
		List<DefdocVO> tempList = (List<DefdocVO>) getBaseDAO().retrieveByClause(DefdocVO.class, filter.toString());
		if (null != tempList && !tempList.isEmpty()) {
			if (!ArrayUtils.isEmpty(keyFields)) {
				for (DefdocVO vo : tempList) {
					StringBuilder keySB = new StringBuilder();
					for (String attr : keyFields) {
						keySB.append(String.valueOf(vo.getAttributeValue(attr)));
					}
					projVOMap.put(keySB.toString(), vo);
				}
			}
		}
		return projVOMap;
	}

	/**
	 * @Description: 查询人员信息.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, PsndocVO> qryPsndocVOMap(String whereCondition, String[] keyFields) throws BusinessException {
		Map<String, PsndocVO> psndocMap = new HashMap<String, PsndocVO>();
		if (StringUtils.isNotEmpty(whereCondition)) {
			List<PsndocVO> tempList = (List<PsndocVO>) getBaseDAO().retrieveByClause(PsndocVO.class, whereCondition);
			if (null != tempList && !tempList.isEmpty()) {
				if (!ArrayUtils.isEmpty(keyFields)) {
					for (PsndocVO vo : tempList) {
						StringBuilder keySB = new StringBuilder();
						for (String attr : keyFields) {
							keySB.append(String.valueOf(vo.getAttributeValue(attr)));
						}
						psndocMap.put(keySB.toString(), vo);
					}
				}
			}
		}

		return psndocMap;
	}

	@Override
	public AggProjSalaryVO[] importProjSalary(AggProjSalaryVO[] impAggVO, String[] delPks) throws BusinessException {
		if (!ArrayUtils.isEmpty(delPks)) {
			StringBuilder whereSql = new StringBuilder(" delete from wa_projsalary where ");
			whereSql.append(" pk_projsalary in ").append(InSqlManager.getInSQLValue(delPks));
			getBaseDAO().executeUpdate(whereSql.toString());
		}

		return insert(impAggVO, null);
	}
}
