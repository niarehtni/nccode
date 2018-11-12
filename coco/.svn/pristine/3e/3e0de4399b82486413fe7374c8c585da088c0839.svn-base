package nc.itf.hi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.pubitf.org.BusiReportStruMemberStru;
import nc.ui.hr.pub.FromWhereSQL;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.om.pub.AbilityMatchVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;

public abstract interface IPsndocQryService {
	public abstract String[] getPsndocUniqueRule() throws BusinessException;

	public abstract void isInBlacklist(PsndocAggVO paramPsndocAggVO) throws BusinessException;

	public abstract boolean isInJob(String paramString) throws BusinessException;

	public abstract Map<String, Boolean> isInJob(String[] paramArrayOfString) throws BusinessException;

	public abstract AbilityMatchVO[] queryAbilityMatchVOsByJob(String paramString1, String paramString2)
			throws BusinessException;

	public abstract AbilityMatchVO[] queryAbilityMatchVOsByPost(String paramString1, String paramString2)
			throws BusinessException;

	public abstract AbilityMatchVO[] queryAbilityMatchVOsByPsnJob(String paramString) throws BusinessException;

	public abstract PsnJobVO[] queryAvailablePsnjobByCondition(String paramString1, String paramString2)
			throws BusinessException;

	public abstract nc.vo.hi.psndoc.PsndocVO[] queryPsnBudgetVOs(String paramString, int paramInt)
			throws BusinessException;

	public abstract String[] queryPsndocPks(LoginContext paramLoginContext, String[] paramArrayOfString,
			String paramString1, String paramString2, String paramString3, HashMap<String, String> paramHashMap,
			String paramString4) throws BusinessException;

	public abstract nc.vo.hi.psndoc.PsndocVO[] queryPsndocVOByCondition(LoginContext paramLoginContext,
			List<String> paramList, String[] paramArrayOfString, String paramString1, String paramString2)
			throws BusinessException;

	public abstract nc.vo.hi.psndoc.PsndocVO[] queryPsndocVOByCondition(LoginContext paramLoginContext,
			String[] paramArrayOfString, String paramString1, String paramString2) throws BusinessException;

	public abstract nc.vo.hi.psndoc.PsndocVO[] queryPsndocVOByDeptPK(String[] paramArrayOfString,
			boolean paramBoolean1, boolean paramBoolean2) throws BusinessException;

	public abstract nc.vo.hi.psndoc.PsndocVO[] queryPsndocVOByJobPK(String paramString, boolean paramBoolean1,
			boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
			throws BusinessException;

	public abstract <T extends SuperVO> nc.vo.hi.psndoc.PsndocVO queryPsndocVOByJobPostFamily(T paramT)
			throws BusinessException;

	public abstract <T extends SuperVO> nc.vo.hi.psndoc.PsndocVO queryPsndocVOByJobPostFamily(T paramT,
			String paramString) throws BusinessException;

	public abstract nc.vo.hi.psndoc.PsndocVO[] queryPsndocVOByJobWhetherInPost(String paramString, boolean paramBoolean)
			throws BusinessException;

	public abstract PsndocAggVO[] queryPsndocVOByPks(String[] paramArrayOfString) throws BusinessException;

	public abstract Map<String, PsndocAggVO> queryPsndocVOByCondition(String[] paramArrayOfString1,
			String[] paramArrayOfString2) throws BusinessException;

	public abstract PsndocAggVO queryPsndocVOByPk(String paramString) throws BusinessException;

	public abstract PsndocAggVO queryPsndocVOByPsnjobPk(String paramString) throws BusinessException;

	public abstract PsndocAggVO queryPsndocVOByPk(String paramString, boolean paramBoolean1, boolean paramBoolean2)
			throws BusinessException;

	public abstract nc.vo.hi.psndoc.PsndocVO[] queryPsndocVOByPostPK(String paramString, boolean paramBoolean1,
			boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
			throws BusinessException;

	public abstract nc.vo.hi.psndoc.PsndocVO[] queryPsndocVOByPostPkAndLevel(String paramString, boolean paramBoolean,
			int paramInt) throws BusinessException;

	public abstract nc.vo.hi.psndoc.PsndocVO[] queryPsndocVOByPostWhetherInPost(String paramString, boolean paramBoolean)
			throws BusinessException;

	public abstract nc.vo.hi.psndoc.PsndocVO[] queryPsndocVOByWhetherInPost(String paramString1, String paramString2,
			boolean paramBoolean) throws BusinessException;

	public abstract List<PsnJobVO> queryPsninfoByCondition(String paramString) throws BusinessException;

	public abstract List<PsnJobVO> queryPsninfoByPks(String[] paramArrayOfString) throws BusinessException;

	public abstract PsnJobVO[] queryPsnJobs(String paramString) throws BusinessException;

	public abstract PsnJobVO queryPsnMainJob(String paramString, int paramInt) throws BusinessException;

	public abstract PsnJobVO[] queryPsnPartTimeJobs(String paramString1, int paramInt, String paramString2)
			throws BusinessException;

	public abstract SuperVO[] querySubVO(Class paramClass, String paramString1, String paramString2)
			throws BusinessException;

	public abstract String[] queryHiddenKeys(LoginContext paramLoginContext) throws BusinessException;

	public abstract UFLiteralDate queryIndutyDate(String paramString) throws BusinessException;

	public abstract BusiReportStruMemberStru getFatherMemberFromBusiReportStru(String paramString1,
			String paramString2, String paramString3, String paramString4, String paramString5)
			throws BusinessException;

	public abstract PsnJobVO[] queryPsndocVOsByDeptPK(String paramString, boolean paramBoolean,
			FromWhereSQL paramFromWhereSQL) throws BusinessException;

	public abstract PsnJobVO[] queryPsnjobVOsByDeptPK(String paramString, boolean paramBoolean)
			throws BusinessException;

	public abstract String getDeptPsnCondition(String paramString1, String paramString2, boolean paramBoolean1,
			boolean paramBoolean2) throws BusinessException;

	public abstract PsnJobVO[] queryPsndocVOsByPsnInfo(String paramString1, String paramString2, String paramString3,
			String paramString4, String paramString5, String paramString6) throws BusinessException;

	public abstract nc.vo.hi.psndoc.PsndocVO[] queryPsnPhotoInfo(String[] paramArrayOfString) throws BusinessException;

	public abstract Map<String, PsnJobVO[]> queryJobHistroy(String paramString) throws BusinessException;

	public abstract String checkOrgPower(String paramString) throws BusinessException;

	public abstract String checkDeptPower(String paramString) throws BusinessException;

	public abstract void isCreateUser(nc.vo.bd.psn.PsndocVO[] paramArrayOfPsndocVO) throws BusinessException;

	public abstract PsnJobVO[] queryPsnjobVOByOrgAndDate(String paramString1, String paramString2,
			UFLiteralDate paramUFLiteralDate1, UFLiteralDate paramUFLiteralDate2) throws BusinessException;

	public abstract PsndocAggVO queryPsndocByNameID(String paramString1, String paramString2, String paramString3)
			throws BusinessException;

	public abstract boolean isMustUploadAttachment(String paramString1, String paramString2, String paramString3)
			throws BusinessException;

	public abstract Object[] queryKeyPsnVOByPks(String[] paramArrayOfString, Object paramObject)
			throws BusinessException;

	public abstract byte[] mergeTemplate(String paramString1, String paramString2) throws BusinessException;

	public abstract HashMap<String, byte[]> batchMergeTemplate(String[] paramArrayOfString, String paramString)
			throws BusinessException;

	public abstract PsnJobVO[] queryPsnjobByPKs(String[] paramArrayOfString) throws BusinessException;

	public abstract HashMap<String, String> validateRegDate(HashMap<String, String> paramHashMap,
			HashMap<String, UFLiteralDate> paramHashMap1) throws BusinessException;

	public abstract String[] queryNotNullSubset(PsndocAggVO paramPsndocAggVO, HashMap<String, String> paramHashMap)
			throws BusinessException;

	public abstract String[] validateSubNotNull(PsndocAggVO paramPsndocAggVO, HashMap<String, String> paramHashMap)
			throws BusinessException;

	public abstract HashMap<String, GeneralVO[]> queryPsnInfo(String[] paramArrayOfString1,
			String[] paramArrayOfString2, int paramInt, boolean paramBoolean, String paramString,
			HashMap<String, ArrayList<String>> paramHashMap) throws BusinessException;

	public abstract String getRefItemName(String paramString1, Object paramObject, String paramString2)
			throws BusinessException;

	public abstract HashMap<String, GeneralVO> getWorkSyncMap(String paramString1, String paramString2,
			boolean paramBoolean) throws BusinessException;

	public abstract ArrayList<String[]> queryInfosetMetaID() throws BusinessException;

	public abstract String[] convertPks(ArrayList<String> paramArrayList) throws BusinessException;

	public abstract nc.vo.hi.psndoc.PsndocVO[] queryPsndocVOByCondition(String[] paramArrayOfString)
			throws BusinessException;

	public abstract Boolean getValueOFParaMaintainCadre(LoginContext paramLoginContext) throws BusinessException;

	public abstract Boolean getValueOFParaDeployCadre(LoginContext paramLoginContext) throws BusinessException;

	public abstract Map<String, Object> getLevelRankCondition(String paramString1, String paramString2,
			String paramString3, String paramString4) throws BusinessException;

	public abstract Map<String, String> getDefaultLevelRank(String paramString1, String paramString2,
			String paramString3, String paramString4, String paramString5) throws BusinessException;

	public abstract ArrayList orderbyPks(String[] paramArrayOfString) throws BusinessException;
	public abstract Integer queryLJdaysinFeb2018(String psncode)
			  throws BusinessException;

	  public abstract Integer queryXXRdaysinFeb2018(String psncode)
			  throws BusinessException;
	  public abstract String getPsnJobByPsncode(String psncode)
			  throws BusinessException;
	  
	  public abstract UFLiteralDate queryEndDate(String paramString)
			    throws BusinessException;

	  public abstract UFLiteralDate queryFirstIndutyDate(String paramString)
			    throws BusinessException;

}