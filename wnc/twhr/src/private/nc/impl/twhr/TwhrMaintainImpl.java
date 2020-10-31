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
import nc.bs.dao.DAOException;
import nc.hr.utils.InSQLCreator;
import nc.impl.pub.ace.AceTwhrPubServiceImpl;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.itf.twhr.ITwhrMaintain;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.nhicalc.BaoAccountVO;

public class TwhrMaintainImpl extends AceTwhrPubServiceImpl implements ITwhrMaintain {
	private BaseDAO basedao = new BaseDAO();

	public BaseDAO getBasedao() {
		if (null == basedao) {
			return new BaseDAO();
		}
		return basedao;
	}

	@Override
	public BaoAccountVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException {
		BatchSaveAction<BaoAccountVO> saveAction = new BatchSaveAction<BaoAccountVO>();
		//一些校验
		check(batchVO.getUpdObjs());
		check(batchVO.getDelObjs());
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		deleteChild(retData.getDelObjs());
		return retData;
	}
	
	private void check(Object[] objects) throws BusinessException {
		// 已经结算的行,不能被修改
		if(objects!=null && objects.length > 0){
			for(Object obj : objects){
				if(obj instanceof BaoAccountVO){
					BaoAccountVO batVO = (BaoAccountVO)obj; 
					if(batVO.getDiffsettlemonth()!=null || batVO.getDiffsettlewaclass()!=null){
						throw new BusinessException("已經結算的數據無法被修改或刪除!");
					}
				}
			}
		}
	}

	/**
	 * 刪除主表的時候,刪除子表
	 * @param delObjs
	 * @throws BusinessException 
	 * @throws DAOException 
	 */
	private void deleteChild(Object[] delObjs) throws DAOException, BusinessException {
		//根據區間和组织人员删除子表信息
		Map<String,Set<String>> delMap = new HashMap<>();
		if(delObjs!=null && delObjs.length > 0){
			for(Object vo : delObjs){
				BaoAccountVO batVO = (BaoAccountVO)vo; 
				Set<String> psnSet = delMap.get(batVO.getPk_org()+"::"+batVO.getPk_period());
				if(psnSet==null){
					psnSet = new HashSet<>();
				}
				psnSet.add(batVO.getIdno());
				delMap.put(batVO.getPk_org()+"::"+batVO.getPk_period(), psnSet);
			}
			//开始删除
			InSQLCreator insql = new InSQLCreator();
			if(delMap.size() > 0){
				Set<String> keySet = delMap.keySet();
				for(String key : keySet){
					Set<String> psnSet = delMap.get(key);
					getBasedao().executeUpdate("update twhr_diffinsurance set dr = 1 where pk_org = '"+key.split("::")[0]+"'"
							+ " and pk_period = '"+key.split("::")[1]+"' and idno in ("
							+insql.getInSQL(psnSet.toArray(new String[0]))+")");
				}
			}
		}
	}

	@Override
	public Map<Integer,Set<String>> insertupdatehealth(BaoAccountVO[] baoaccountvos,String pk_legal_org,String waperiod) throws BusinessException {
		Map<Integer,Set<String>> errorMap = new HashMap<>();
		if(baoaccountvos==null || baoaccountvos.length <= 0){
			return errorMap;
		}
		//获取法人组织下所有的人力资源组织
		Set<String> hrorgSet = LegalOrgUtilsEX.getHROrgsByLegalOrg(pk_legal_org);
		if (hrorgSet == null || hrorgSet.size() == 0) {
			throw new BusinessException("該法人組織下沒有人力資源組織,無法導入!");
		}
		//本次導入的相關人員
		Set<String> psnSet = new HashSet<String>();
		for(BaoAccountVO vo : baoaccountvos){
			psnSet.add(vo.getPk_psndoc());
		}
		InSQLCreator insql = new InSQLCreator();
		//组织insql
		String pk_psndocInSQL = insql.getInSQL(psnSet.toArray(new String[0]));
		//人员pkinsql
		String orgInsql = insql.getInSQL(hrorgSet.toArray(new String[0]));
		//相關的組織信息
		Map<String, OrgVO> hrorgMap = LegalOrgUtilsEX.getOrgInfo(hrorgSet.toArray(new String[0]));
		//人員對應的組織 <psndoc,set<pk_org>>
		Map<String,Set<String>> psn2OrgMap = getPsn2OrgInfo(waperiod,orgInsql,pk_psndocInSQL);
		
		
		List<BaoAccountVO> insertlist = new ArrayList<BaoAccountVO>();
		List<BaoAccountVO> updatelist = new ArrayList<BaoAccountVO>();
		//為插入的vo填入組織信息
		insertlist.addAll(addOrgInfo(baoaccountvos,errorMap,psn2OrgMap,hrorgMap));
		// 查询出表中已有的vo <pk_psndoc::pk_org,BaoAccountVO>
		Map<String,BaoAccountVO> existVOMap = getExistVO(pk_psndocInSQL,orgInsql,waperiod);
		List<BaoAccountVO> rmList = new ArrayList<>();
		if (null != existVOMap && existVOMap.size() > 0) {
			for (BaoAccountVO baovo : insertlist) {
				BaoAccountVO vo = existVOMap.get(baovo.getPk_psndoc() + "::" + baovo.getPk_org());
				if (vo == null) {
					continue;
				}
				rmList.add(baovo);
				// 如果已經結算,那麼不再更新
				if (vo.getDiffsettlemonth() == null && vo.getDiffsettlewaclass() == null) {
					vo.setHealth_amount(baovo.getHealth_amount());
					vo.setHealth_orgamount(baovo.getHealth_orgamount());
					vo.setHealth_psnamount(baovo.getHealth_psnamount());
					vo.setPk_group(baovo.getPk_group());
					vo.setPk_org(baovo.getPk_org());
					vo.setPk_org_v(baovo.getPk_org_v());
					vo.setAttributeValue("dr", 0);
					vo.setHealthid(baovo.getHealthid());
					vo.setPk_period(baovo.getPk_period());
					vo.setName(baovo.getName());
					vo.setPk_psndoc(baovo.getPk_psndoc());
					updatelist.add(vo);
				}
			}
			insertlist.removeAll(rmList);
		}
		if (null != insertlist && insertlist.size() > 0) {
			this.basedao.insertVOArray(insertlist.toArray(new BaoAccountVO[0]));
		}
		if (null != updatelist && updatelist.size() > 0) {
			this.basedao.updateVOArray(updatelist.toArray(new BaoAccountVO[0]));
		}
		return errorMap;
	}

	private Map<String, BaoAccountVO> getExistVO(String pk_psndocInSQL, String orgInsql, String waperiod)
			throws DAOException {
		@SuppressWarnings("unchecked")
		List<BaoAccountVO> volist = (List<BaoAccountVO>) this.basedao.retrieveByClause(BaoAccountVO.class,
				"pk_psndoc in(" + pk_psndocInSQL + ") and pk_period='" + waperiod + "' and dr=0 and pk_org in ("
						+ orgInsql + ")");
		Map<String, BaoAccountVO> existVOMap = new HashMap<>();
		for (BaoAccountVO vo : volist) {
			existVOMap.put(vo.getPk_psndoc() + "::" + vo.getPk_org(), vo);
		}
		return existVOMap;
	}

	private List<BaoAccountVO> addOrgInfo(BaoAccountVO[] baoaccountvos, Map<Integer,Set<String>> errorMap,
			Map<String, Set<String>> psn2OrgMap, Map<String, OrgVO> hrorgMap) {
		List<BaoAccountVO> insertlist = new ArrayList<>();
		for (BaoAccountVO baovo : baoaccountvos) {
			Set<String> pk_orgSet = psn2OrgMap.get(baovo.getPk_psndoc());
			if (pk_orgSet == null || pk_orgSet.size() <= 0) {
				Set<String> psnIDSet = errorMap.get(ERROR_NO_MATCH_ORG);
				if(psnIDSet==null){
					psnIDSet = new HashSet<>();
				}
				// 未匹配到組織,不會被存儲
				psnIDSet.add(baovo.getIdno());
				errorMap.put(ERROR_NO_MATCH_ORG, psnIDSet);
			} else {
				for (String pk_org : pk_orgSet) {
					baovo.setPk_org(pk_org);
					baovo.setPk_group(hrorgMap.get(pk_org).getPk_group());
					baovo.setPk_org_v(hrorgMap.get(pk_org).getPk_vid());
					insertlist.add((BaoAccountVO) baovo.clone());
				}
			}
		}
		return insertlist;
	}

	private Map<String,Set<String>> getPsn2OrgInfo(String waperiod,String orgInsql,String psnInsql) throws BusinessException {
		
		String sql = "select pk_psndoc,pk_org from hi_psnjob "
				+ " where dr = 0 and '"+waperiod+"-31'>= begindate "
				+ " and '"+waperiod+"-01' <= isnull(enddate,'9999-12-31') "
				+ " and pk_org in ("+orgInsql+")"
				+ " and pk_psndoc in ("+psnInsql+")";
		@SuppressWarnings("unchecked")
		Map<String,Set<String>> psn2OrgMap = (Map<String, Set<String>>) basedao.executeQuery(sql, new ResultSetProcessor() {
			private static final long serialVersionUID = 1L;
			Map<String,Set<String>> rsMap = new HashMap<>();
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				while(rs.next()){
					Set<String> orgSet = rsMap.get(rs.getString(1));
					if(orgSet==null){
						orgSet = new HashSet<>();
					}
					orgSet.add(rs.getString(2));
					rsMap.put(rs.getString(1), orgSet);
				}
				return rsMap;
			}
		});
		return psn2OrgMap;
	}

	@Override
	public Map<Integer,Set<String>> insertupdatelabor(BaoAccountVO[] baoaccountvos,String pk_legal_org,String waperiod) 
			throws BusinessException {
		Map<Integer,Set<String>> errorMap = new HashMap<>();
		if(baoaccountvos==null || baoaccountvos.length <= 0){
			return errorMap;
		}
		// 获取法人组织下所有的人力资源组织
		Set<String> hrorgSet = LegalOrgUtilsEX.getHROrgsByLegalOrg(pk_legal_org);
		if (hrorgSet == null || hrorgSet.size() == 0) {
			throw new BusinessException("該法人組織下沒有人力資源組織,無法導入!");
		}
		// 本次導入的相關人員
		Set<String> psnSet = new HashSet<String>();
		for (BaoAccountVO vo : baoaccountvos) {
			psnSet.add(vo.getPk_psndoc());
		}
		InSQLCreator insql = new InSQLCreator();
		//组织insql
		String pk_psndocInSQL = insql.getInSQL(psnSet.toArray(new String[0]));
		//人员pkinsql
		String orgInsql = insql.getInSQL(hrorgSet.toArray(new String[0]));
		//相關的組織信息
		Map<String, OrgVO> hrorgMap = LegalOrgUtilsEX.getOrgInfo(hrorgSet.toArray(new String[0]));
		// 人員對應的組織 <psndoc,set<pk_org>>
		Map<String, Set<String>> psn2OrgMap = getPsn2OrgInfo(waperiod, orgInsql, pk_psndocInSQL);

		List<BaoAccountVO> insertlist = new ArrayList<BaoAccountVO>();
		List<BaoAccountVO> updatelist = new ArrayList<BaoAccountVO>();
		//為插入的vo填入組織信息
		insertlist.addAll(addOrgInfo(baoaccountvos,errorMap,psn2OrgMap,hrorgMap));
		// 查询出表中已有的vo <pk_psndoc::pk_org,BaoAccountVO>
		Map<String,BaoAccountVO> existVOMap = getExistVO(pk_psndocInSQL,orgInsql,waperiod);
		List<BaoAccountVO> rmList = new ArrayList<>();
		if (null != existVOMap && existVOMap.size() > 0) {
			for (BaoAccountVO baovo : insertlist) {
				BaoAccountVO vo = existVOMap.get(baovo.getPk_psndoc() + "::" + baovo.getPk_org());
				if (vo == null) {
					continue;
				}
				rmList.add(baovo);

				// 如果已經結算,那麼不再更新
				if (vo.getDiffsettlemonth() == null && vo.getDiffsettlewaclass() == null) {
					vo.setLabor_orgamount(baovo.getLabor_orgamount());
					vo.setLabor_psnamount(baovo.getLabor_psnamount());
					vo.setPk_group(baovo.getPk_group());
					vo.setPk_org(baovo.getPk_org());
					vo.setPk_org_v(baovo.getPk_org_v());
					vo.setLaborid(baovo.getLaborid());
					vo.setPk_period(baovo.getPk_period());
					vo.setName(baovo.getName());
					vo.setAttributeValue("dr", 0);
					vo.setPk_psndoc(baovo.getPk_psndoc());

					updatelist.add(vo);
				}

			}
			insertlist.removeAll(rmList);
		}
		if (null != insertlist && insertlist.size() > 0) {
			this.basedao.insertVOArray(insertlist.toArray(new BaoAccountVO[0]));
		}
		if (null != updatelist && updatelist.size() > 0) {
			this.basedao.updateVOArray(updatelist.toArray(new BaoAccountVO[0]));
		}
		return errorMap;
	}

	@Override
	public Map<Integer, Set<String>> insertupdateretire(BaoAccountVO[] baoaccountvos, String pk_legal_org,
			String waperiod) throws BusinessException {
		Map<Integer,Set<String>> errorMap = new HashMap<>();
		if(baoaccountvos==null || baoaccountvos.length <= 0){
			return errorMap;
		}
		//获取法人组织下所有的人力资源组织
		Set<String> hrorgSet = LegalOrgUtilsEX.getHROrgsByLegalOrg(pk_legal_org);
		if (hrorgSet == null || hrorgSet.size() == 0) {
			throw new BusinessException("該法人組織下沒有人力資源組織,無法導入!");
		}
		//本次導入的相關人員
		Set<String> psnSet = new HashSet<String>();
		for(BaoAccountVO vo : baoaccountvos){
			psnSet.add(vo.getPk_psndoc());
		}
		InSQLCreator insql = new InSQLCreator();
		//组织insql
		String pk_psndocInSQL = insql.getInSQL(psnSet.toArray(new String[0]));
		//人员pkinsql
		String orgInsql = insql.getInSQL(hrorgSet.toArray(new String[0]));
		//相關的組織信息
		Map<String, OrgVO> hrorgMap = LegalOrgUtilsEX.getOrgInfo(hrorgSet.toArray(new String[0]));
		//人員對應的組織 <psndoc,set<pk_org>>
		Map<String,Set<String>> psn2OrgMap = getPsn2OrgInfo(waperiod,orgInsql,pk_psndocInSQL);
		
		
		List<BaoAccountVO> insertlist = new ArrayList<BaoAccountVO>();
		List<BaoAccountVO> updatelist = new ArrayList<BaoAccountVO>();
		//為插入的vo填入組織信息
		insertlist.addAll(addOrgInfo(baoaccountvos,errorMap,psn2OrgMap,hrorgMap));
		// 查询出表中已有的vo <pk_psndoc::pk_org,BaoAccountVO>
		Map<String,BaoAccountVO> existVOMap = getExistVO(pk_psndocInSQL,orgInsql,waperiod);
		List<BaoAccountVO> rmList = new ArrayList<>();
		if (null != existVOMap && existVOMap.size() > 0) {
			for (BaoAccountVO baovo : insertlist) {
				BaoAccountVO vo = existVOMap.get(baovo.getPk_psndoc() + "::" + baovo.getPk_org());
				if (vo == null) {
					continue;
				}
				rmList.add(baovo);
				// 如果已經結算,那麼不再更新
				if (vo.getDiffsettlemonth() == null && vo.getDiffsettlewaclass() == null) {
					if (null != baovo.getRetire_orgamount()) {
						vo.setRetire_orgamount(baovo.getRetire_orgamount());
					}
					if (null != baovo.getRetire_psnamount()) {
						vo.setRetire_psnamount(baovo.getRetire_psnamount());
					}
					vo.setRetire_amount(baovo.getRetire_amount());
					vo.setPk_group(baovo.getPk_group());
					vo.setPk_org(baovo.getPk_org());
					vo.setPk_org_v(baovo.getPk_org_v());
					vo.setRetiredid(baovo.getRetiredid());
					vo.setPk_period(baovo.getPk_period());
					vo.setName(baovo.getName());
					vo.setAttributeValue("dr", 0);
					vo.setPk_psndoc(baovo.getPk_psndoc());
					updatelist.add(vo);
				}
			}
			insertlist.removeAll(rmList);
		}
		
		if (null != insertlist && insertlist.size() > 0) {
			this.basedao.insertVOArray(insertlist.toArray(new BaoAccountVO[0]));
		}
		if (null != updatelist && updatelist.size() > 0) {
			this.basedao.updateVOArray(updatelist.toArray(new BaoAccountVO[0]));
		}
		return errorMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void Settle(BaoAccountVO[] baoaccountvos,String pk_wa_class,String wa_period) throws BusinessException {
		if(baoaccountvos==null || baoaccountvos.length == 0 || pk_wa_class ==null || wa_period == null){
			return;
		}
		//找出所有原有的薪资期间和方案
		Set<String> waPeriodSet = new HashSet<>();
		for (BaoAccountVO vo : baoaccountvos) {
			if (vo.getDiffsettlewaclass() != null && vo.getDiffsettlemonth() != null) {
				waPeriodSet.add(vo.getDiffsettlewaclass() + "::" + vo.getDiffsettlemonth());
			}
		}
		Map<String,Boolean> waIsPayMap = new HashMap<>();
		if(waPeriodSet.size() > 0){
			InSQLCreator insql = new InSQLCreator();
			String keyInsql = insql.getInSQL(waPeriodSet.toArray(new String[0]));
			//查询薪资期间和方案是否审核,发放,结算
			String sql = "select states.pk_wa_class+'::'+period.cyear+period.cperiod keys,isnull(states.checkflag,'N'), "
					+ " isnull(states.payoffflag,'N'),isnull(states.accountmark,'N')  "
					+ " from wa_periodstate states "
					+" left join wa_period period on (period.dr = 0 and period.pk_wa_period = states.pk_wa_period) "
					+" where states.pk_wa_class+'::'+period.cyear+period.cperiod in ("+keyInsql+") ";
			waIsPayMap = (Map<String,Boolean>)getBasedao().executeQuery(sql, new ResultSetProcessor() {
				private static final long serialVersionUID = -7067168393458385493L;
				Map<String,Boolean> rsMap = new HashMap<>();
				@Override
				public Object handleResultSet(ResultSet rs) throws SQLException {
					while(rs.next()){
						if(rs.getString(1)!=null){
							if(rs.getBoolean(2)||rs.getBoolean(3)||rs.getBoolean(4)){
								//已經審核或者...了
								rsMap.put(rs.getString(1), true);
							}else{
								//還在計算或者沒有計算的
								rsMap.put(rs.getString(1), false);
							}
						}
						
					}
					return rsMap;
				}
			});
			
		}
		//settle
		List<BaoAccountVO> baoVOList =new ArrayList<>();
		for (BaoAccountVO vo : baoaccountvos) {
			if (vo.getDiffsettlewaclass() != null && vo.getDiffsettlemonth() != null) {
				String key = vo.getDiffsettlewaclass() + "::" + vo.getDiffsettlemonth();
				Boolean isPay = waIsPayMap.get(key);
				if(isPay!=null && isPay){
					//已經結算,跳過
					;
				}else{
					//未結算,set
					vo.setDiffsettlemonth(wa_period);
					vo.setDiffsettlewaclass(pk_wa_class);
					baoVOList.add(vo);
				}
			}else{
				//為空則直接結算
				vo.setDiffsettlemonth(wa_period);
				vo.setDiffsettlewaclass(pk_wa_class);
				baoVOList.add(vo);
			}
		}
		//更新
		BatchOperateVO batchVO = new BatchOperateVO();
		int[] indexArray = new int[baoVOList.size()];
		for(int i = 0;i<baoVOList.size();i++){
			indexArray[i] = i;
		}
		batchVO.setUpdIndexs(indexArray);
		batchVO.setUpdObjs(baoVOList.toArray(new BaoAccountVO[0]));
		BatchSaveAction<BaoAccountVO> saveAction = new BatchSaveAction<BaoAccountVO>();
		saveAction.batchSave(batchVO);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
