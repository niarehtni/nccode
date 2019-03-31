package nc.impl.hi.psndoc;

import java.io.File;
import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nc.bs.bd.cache.CacheProxy;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uap.oid.OidGenerator;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.ArrayHelper;
import nc.hr.utils.BillCodeHelper;
import nc.hr.utils.HRCMTermUnitUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.TimerLogger;
import nc.impl.hi.repdef.HIMergeFilter;
import nc.itf.bd.pubinfo.IAddressService_C;
import nc.itf.hi.IBlacklistManageService;
import nc.itf.hi.IPersonRecordService;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hi.IRptQueryService;
import nc.itf.hr.IHRLicenseChecker;
import nc.itf.hr.frame.IPersistenceHome;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.infoset.IInfoSetQry;
import nc.itf.hr.psnclrule.IPsnclruleQueryService;
import nc.itf.hrp.psnbudget.IBudgetSetQueryService;
import nc.itf.hrp.psnbudget.IOrgBudgetQueryService;
import nc.itf.org.IOrgConst;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.md.data.access.NCObject;
import nc.md.model.MetaDataException;
import nc.md.model.type.IType;
import nc.md.model.type.impl.EnumType;
import nc.md.persist.framework.MDPersistenceService;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pub.tools.HIQueryLogUtils;
import nc.pub.tools.HiCacheUtils;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.VOUtils;
import nc.pubitf.org.BusiReportStruMemberStru;
import nc.pubitf.org.IHrOrgPubService;
import nc.pubitf.para.SysInitQuery;
import nc.uap.oba.word.generator.nchr.NCHRMergeEngine;
import nc.ui.bd.pubinfo.address.AddressFormater;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.pub.FromWhereSQL;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.vo.bd.address.AddressFormatVO;
import nc.vo.bd.psn.PsndocExtend;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.hi.blacklist.BlacklistVO;
import nc.vo.hi.psndoc.Attribute;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.KeyPsnGrpVO;
import nc.vo.hi.psndoc.KeyPsnVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnBudgetVO;
import nc.vo.hi.psndoc.PsnChgVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.PsntypeBudgeVO;
import nc.vo.hi.psndoc.ReqVO;
import nc.vo.hi.pub.FldreftypeVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hi.pub.HiEventValueObject;
import nc.vo.hi.pub.IHiEventType;
import nc.vo.hi.repdef.RepDefVO;
import nc.vo.hr.combinesort.SortVO;
import nc.vo.hr.combinesort.SortconVO;
import nc.vo.hr.competencyindex.CompetencyindexGradeVO;
import nc.vo.hr.competencyindex.CompetencyindexTypeVO;
import nc.vo.hr.competencyindex.CompetencyindexVO;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.psnclrule.PsnclinfosetVO;
import nc.vo.hr.psnclrule.PsnclruleVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.GeneralVOProcessor;
import nc.vo.hr.tools.pub.Pair;
import nc.vo.hr.tools.rtf.RefDataTypeHelper;
import nc.vo.hr.tools.rtf.RefSetFieldParameter;
import nc.vo.hrp.budgetset.BudgetPsnTypeRegVO;
import nc.vo.hrp.psnorgbudget.ValidateResultVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.job.JobNeedVO;
import nc.vo.om.job.JobTypeVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.joblevelsys.FilterTypeEnum;
import nc.vo.om.joblevelsys.LevelRelationVO;
import nc.vo.om.joblevelsys.RankRelationVO;
import nc.vo.om.post.PostNeedVO;
import nc.vo.om.post.PostSeriesVO;
import nc.vo.om.pub.AbilityMatchVO;
import nc.vo.om.pub.IAbilityMatch;
import nc.vo.om.pub.IJobNeed;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.org.PostVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.bill.BillOperaterEnvVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.sm.UserVO;
import nc.vo.uif2.LoginContext;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.util.SqlWhereUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/***************************************************************************
 * Created on 2010-2-3 10:14:33<br>
 * @author Rocex Wang
 ***************************************************************************/
public class PsndocServiceImpl implements IPsndocService, IPsndocQryService
{
    private PsndocDAO psndocDao;
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-8 11:35:24<br>
     * @see nc.itf.hi.IPsndocService#adjustPsnSort(nc.vo.hi.psndoc.PsnJobVO[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsnJobVO[] adjustPsnSort(PsnJobVO... psnJobVOs) throws BusinessException
    {
        NCLocator.getInstance().lookup(IPersistenceUpdate.class).updateVOArray(null, psnJobVOs, new String[]{PsnJobVO.SHOWORDER}, null);
        
        PsnjobVO[] bdPsnjobVOs = new PsnjobVO[psnJobVOs.length];
        for (int i = 0; i < psnJobVOs.length; ++i)
        {
            bdPsnjobVOs[i] = new PsnjobVO();
            bdPsnjobVOs[i].setPk_psnjob(psnJobVOs[i].getPk_psnjob());
            bdPsnjobVOs[i].setShoworder(psnJobVOs[i].getShoworder());
        }
        NCLocator.getInstance().lookup(IPersistenceUpdate.class).updateVOArray(null, bdPsnjobVOs, new String[]{PsnJobVO.SHOWORDER}, null);
        return null;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-11 13:37:24<br>
     * @see nc.itf.hi.IPsndocService#batchAddSubVO(nc.vo.hi.psndoc.PsndocAggVO[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public <T extends SuperVO> PsndocAggVO[] batchAddSubVO(Class T, String curTabCode, Object resultVO, PsndocAggVO[] psndocAggVOs)
            throws BusinessException
    {
        String[] ctrtCode = null;
        String billtype = null;
        if (CtrtVO.getDefaultTableName().equals(curTabCode))
        {
            SuperVO vo = psndocAggVOs[0].getTableVO(curTabCode)[0];
            int type = (Integer) vo.getAttributeValue(CtrtVO.CONTTYPE);
            billtype = "620" + type;
            BillCodeHelper.lockBillCodeRule("hr_auto_billcode" + billtype, 150);
            String prefix = "ZD" + billtype + PubEnv.getServerDate().toStdString();
            ctrtCode = SQLHelper.getBillCodesByPrefix(prefix, CtrtVO.CONTRACTNUM, CtrtVO.class, psndocAggVOs.length);
        }
        ArrayList<String> pk_psndoc_List = new ArrayList<String>();
        ArrayList<String> pk_psnorg_List = new ArrayList<String>();
        for (int i = 0; i < psndocAggVOs.length; i++)
        {
            pk_psndoc_List.add(psndocAggVOs[i].getParentVO().getPk_psndoc());
            if (!pk_psnorg_List.contains(psndocAggVOs[i].getParentVO().getPsnOrgVO().getPk_psnorg()))
            {
                pk_psnorg_List.add(psndocAggVOs[i].getParentVO().getPsnOrgVO().getPk_psnorg());
            }
            
        }
        InSQLCreator isc = new InSQLCreator();
        String strInSql = isc.getInSQL(pk_psndoc_List.toArray(new String[0]));
        String where = " pk_psndoc in(" + strInSql + ") ";
        if (PsndocAggVO.hashBusinessInfoSet.contains(curTabCode))
        {
            where += " and pk_psnorg in(" + isc.getInSQL(pk_psnorg_List.toArray(new String[0])) + ") ";
        }
        try
        {
            SuperVO[] alldbSubVOs = querySubVO(T, where, " pk_psndoc asc,recordnum desc ");
            Map<String, List<SuperVO>> dbSubVOMap = null;
            if (PsndocAggVO.hashBusinessInfoSet.contains(curTabCode))
            {
                dbSubVOMap = ArrayHelper.group(alldbSubVOs, new String[]{"pk_psndoc", "pk_psnorg"});
            }
            else
            {
                dbSubVOMap = ArrayHelper.group(alldbSubVOs, new String[]{"pk_psndoc"});
            }
            for (int j = 0; j < psndocAggVOs.length; j++)
            {
                String key = psndocAggVOs[j].getParentVO().getPk_psndoc();
                if (PsndocAggVO.hashBusinessInfoSet.contains(curTabCode))
                {
                    key = psndocAggVOs[j].getParentVO().getPk_psndoc() + psndocAggVOs[j].getParentVO().getPsnOrgVO().getPk_psnorg();
                }
                SuperVO[] dbSubVOs = null;
                if (dbSubVOMap.containsKey(key))
                {
                    List<SuperVO> dbSubVOList = dbSubVOMap.get(key);
                    dbSubVOs = dbSubVOList.toArray((T[]) Array.newInstance(T, dbSubVOList.size()));
                }
                SuperVO svo = psndocAggVOs[j].getTableVO(curTabCode)[0];
                if (CtrtVO.getDefaultTableName().equals(curTabCode))
                {
                    svo.setAttributeValue(CtrtVO.CONTRACTNUM, ctrtCode[j]);
                }
                psndocAggVOs[j].setTableVO(curTabCode, (T[]) ArrayUtils.add(dbSubVOs, svo));
                psndocAggVOs[j] = savePsndoc(psndocAggVOs[j], false);
                
                // SuperVO[] newSubVOs = querySubVO(T, where, " recordnum desc ");
                // SuperVO vo = null;
                // for (int i = 0; i < newSubVOs.length; i++)
                // {
                // if (isExist(newSubVOs[i], dbSubVOs))
                // {
                // continue;
                // }
                // vo = newSubVOs[i];
                // }
                //
                // psndocAggVOs[j].setTableVO(curTabCode, (T[]) ArrayUtils.add(null, vo));
            }
            return psndocAggVOs;
        }
        finally
        {
            if (CtrtVO.getDefaultTableName().equals(curTabCode))
            {
                BillCodeHelper.unlockBillCodeRule("hr_auto_billcode" + billtype);
            }
        }
    }
    
    private boolean isExist(SuperVO superVO, SuperVO[] dbSubVOs)
    {
        for (int i = 0; dbSubVOs != null && i < dbSubVOs.length; i++)
        {
            if (superVO.getPrimaryKey().equals(dbSubVOs[i].getPrimaryKey()))
            {
                return true;
            }
        }
        return false;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-13 10:44:07<br>
     * @see nc.itf.hi.IPsndocService#batchUpdatePsn(nc.vo.pub.SuperVO, java.lang.String, String[],
     *      java.lang.String[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsndocAggVO[] batchUpdatePsn(SuperVO superVO, String strFieldCode, String[] strPk_psnjob, String[] strPk_psnorgs,
            String[] strPk_psndocs, PsndocAggVO[] agg) throws BusinessException
    {
        ArrayList<String> pk_sub = new ArrayList<String>();
        for (int i = 0; i < agg.length; i++)
        {
            SuperVO[] vos = agg[i].getTableVO(superVO.getTableName());
            for (int j = 0; vos != null && j < vos.length; j++)
            {
                pk_sub.add(vos[j].getPrimaryKey());
            }
        }
        return getPsndocDAO().batchUpdatePsndoc(superVO, strFieldCode, strPk_psnjob, strPk_psnorgs, strPk_psndocs,
            pk_sub.toArray(new String[0]));
    }
    
    @Override
    public <T extends SuperVO> HashMap<String, ArrayList<T>> querySubPKs(Class<T> className, String tableName, String[] strPK_psnorgs,
            String[] strPk_psndocs) throws BusinessException
    {
        if (PsndocVO.getDefaultTableName().equals(tableName))
        {
            return null;
        }
        
        InSQLCreator ttu = new InSQLCreator();
        String inSql = "";
        if (PsndocAggVO.hashBusinessInfoSet.contains(tableName))
        {
            inSql = ttu.getInSQL(strPK_psnorgs);
        }
        else
        {
            inSql = ttu.getInSQL(strPk_psndocs);
        }
        
        String where = "";
        
        if (PsndocAggVO.hashBusinessInfoSet.contains(tableName))// 业务子集
        {
            where = " pk_psnorg in ( " + inSql + " ) and lastflag = 'Y' ";
        }
        else
        {
            where = " pk_psndoc in ( " + inSql + " ) and lastflag = 'Y' ";
        }
        
        T[] vos = (T[]) getPsndocDAO().querySubByCondition(className, where, null);
        if (vos != null && vos.length > 0)
        {
            HashMap<String, ArrayList<T>> hm = new HashMap<String, ArrayList<T>>();
            
            for (int i = 0; i < vos.length; i++)
            {
                String pk_psndoc = (String) vos[i].getAttributeValue("pk_psndoc");
                if (!hm.containsKey(pk_psndoc))
                {
                    hm.put(pk_psndoc, new ArrayList<T>());
                }
                hm.get(pk_psndoc).add(vos[i]);
            }
            return hm;
        }
        else
        {
            return null;
        }
        
    }
    
    @Override
    public PsndocAggVO[] batchUpdatePsnMain(SuperVO superVO, String strFieldCode, String[] strPk_psnjob, String[] strPk_psndocs)
            throws BusinessException
    {
        return getPsndocDAO().batchUpdatePsndocMain(superVO, strFieldCode, strPk_psnjob, strPk_psndocs);
    }
    
    /***************************************************************************
     * 检查新增人员，若在黑名单中则报异常，如果在psndoc中已存在，则返回psndocvo全集。
     * @param psndocVO
     * @return PsndocVO
     * @throws BusinessException
     **************************************************************************/
    @Override
    public PsndocAggVO checkPsnUnique(PsndocVO psndocVO, boolean isInSelf) throws BusinessException
    {
        // 首先检查是否在黑名单
        PsndocAggVO aggVO = new PsndocAggVO();
        aggVO.setParentVO(psndocVO);
        isInBlacklist(aggVO);
        // 检查人员编码全局唯一
        getPsndocDAO().checkPsnCodeUnique(psndocVO);
        // 获得所有规则PK
        String[] rulePks = getPsndocDAO().getPsnUniqueRulePks(true);
        
        if (null == rulePks) return null;
        
        // 检查组织关系
        PsndocAggVO psndocAggVO = null;
        Map<String, HashMap<String, String>> rules = getPsndocDAO().getPsnUserUniqueRule(rulePks);
        for (String rulePk : rulePks)
        {
            HashMap<String, String> rule = rules.get(rulePk);
            String[] strUniqueFields = (String[]) rule.keySet().toArray(new String[0]);
            psndocAggVO = getPsndocDAO().getPsndocVOByUniqueVO(psndocVO, strUniqueFields, isInSelf);
            // 当一条规则校验不为空时中断校验
            if (psndocAggVO != null) break;
        }
        
        if (psndocAggVO == null)
        {
            return null;
        }
        
        String orgname = VOUtils.getDocName(OrgVO.class, psndocAggVO.getParentVO().getPsnJobVO().getPk_org());
        String deptname = VOUtils.getDocName(DeptVO.class, psndocAggVO.getParentVO().getPsnJobVO().getPk_dept());
        String postname = VOUtils.getDocName(PostVO.class, psndocAggVO.getParentVO().getPsnJobVO().getPk_post());
        psndocAggVO.getParentVO().getPsnJobVO().setOrgname(orgname);
        psndocAggVO.getParentVO().getPsnJobVO().setDeptname(deptname);
        psndocAggVO.getParentVO().getPsnJobVO().setJobname(postname);
        
        // 如果人员已存在，根据任职组织判断是否为UAP管理人员
        String pk_org = psndocAggVO.getParentVO().getPsnJobVO().getPk_org();
        int count =
            NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                .getCountByCondition("org_admin_enable", "pk_adminorg = '" + pk_org + "'");
        boolean isUAPManage = count == 0 ? Boolean.TRUE : Boolean.FALSE;
        psndocAggVO.getParentVO().setIsuapmanage(UFBoolean.valueOf(isUAPManage));
        return psndocAggVO;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-1-30 11:27:04<br>
     * @see nc.itf.hi.IPsndocService#deletePsndoc(PsndocAggVO...)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void deletePsndoc(PsndocAggVO... psndocAggVOs) throws BusinessException
    {
        if (psndocAggVOs == null || psndocAggVOs.length == 0)
        {
            return;
        }
        
        String pk_group = psndocAggVOs[0].getParentVO().getPk_group();
        String pk_org = psndocAggVOs[0].getParentVO().getPk_org();
        for (PsndocAggVO psndocAggVO : psndocAggVOs)
        {
            if (isAutoGenerateBillCode(HICommonValue.NBCR_PSNDOC_CODE, pk_group, pk_org))
            {
                String bill_code = psndocAggVO.getParentVO().getCode();
                getIBillcodeManage().returnBillCodeOnDelete(HICommonValue.NBCR_PSNDOC_CODE, pk_group, pk_org, bill_code, null);
            }
            if (isAutoGenerateBillCode(HICommonValue.NBCR_PSNDOC_CLERKCODE, pk_group, pk_org))
            {
                String clerkcode = psndocAggVO.getParentVO().getPsnJobVO().getClerkcode();
                getIBillcodeManage().returnBillCodeOnDelete(HICommonValue.NBCR_PSNDOC_CLERKCODE, pk_group, pk_org, clerkcode, null);
            }
            
            getPsndocDAO().delete(psndocAggVO);
        }
        HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
    }
    
    private boolean isAutoGenerateBillCode(String billType, String pk_group, String pk_org) throws BusinessException
    {
        BillCodeContext billCodeContext = HiCacheUtils.getBillCodeContext(billType, pk_group, pk_org);
        return billCodeContext != null;
    }
    
    private IBillcodeManage getIBillcodeManage()
    {
        return NCLocator.getInstance().lookup(IBillcodeManage.class);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-3-8 16:53:26<br>
     * @see nc.itf.hi.IPsndocService#deleteSub(nc.vo.pub.SuperVO[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void deleteSub(SuperVO... subVOs) throws BusinessException
    {
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-4-15 10:53:07<br>
     * @return PsndocDAO
     * @author Rocex Wang
     ***************************************************************************/
    private PsndocDAO getPsndocDAO()
    {
        if (psndocDao == null)
        {
            psndocDao = new PsndocDAO();
        }
        return psndocDao;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-15 10:53:04<br>
     * @see nc.itf.hi.IPsndocQryService#getPsndocUniqueRule()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public String[] getPsndocUniqueRule() throws BusinessException
    {
        return (String[]) getPsndocDAO().getPsndocUniqueRule().keySet().toArray(new String[0]);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-3-8 16:53:26<br>
     * @see nc.itf.hi.IPsndocService#insertSub(nc.vo.pub.SuperVO[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public SuperVO[] insertSub(SuperVO... subVOs) throws BusinessException
    {
        return null;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-2-3 10:10:32<br>
     * @see nc.itf.hi.IPsndocService#intoDoc(PsndocVO[], String, String[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void intoDoc(PsndocVO[] arrayPsndocVO, boolean issyncwork, String strPk_hrorg, String... strPk_psnjobs) throws BusinessException
    {
        if (strPk_psnjobs == null || strPk_psnjobs.length == 0)
        {
            return;
        }
        
        boolean bl = NCLocator.getInstance().lookup(IHRLicenseChecker.class).checkPsnCountOnSwitchToDoc(strPk_psnjobs.length);
        
        if (bl)
        {
            throw new BusinessException(ResHelper.getString("6007psn", "06007psn0433")/* "当前操作将导致系统员工数超过最大授权数!" */);
        }
        
        if (arrayPsndocVO != null && arrayPsndocVO.length > 0)
        {
            BDVersionValidationUtil.validateSuperVO(arrayPsndocVO);
        }
        
        getPsndocDAO().intoDoc(issyncwork, strPk_hrorg, strPk_psnjobs);
        
    }
    
    /***************************************************************************
     * 检查是否在黑名单<br>
     * Created on 2010-5-6 18:31:57<br>
     * @param aggVO
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    @Override
    public void isInBlacklist(PsndocAggVO aggVO) throws BusinessException
    {
        
        CertVO[] cert = (CertVO[]) aggVO.getTableVO(CertVO.getDefaultTableName());
        ArrayList<CertVO> al = new ArrayList<CertVO>();
        for (int i = 0; cert != null && i < cert.length; i++)
        {
            if (cert[i].getStatus() == VOStatus.DELETED || cert[i].getIseffect() == null || !cert[i].getIseffect().booleanValue())
            {
                // 删除的/非默认的不进行校验
                continue;
            }
            al.add(cert[i]);
        }
        
        if (al.size() == 0)
        {
            CertVO temp = new CertVO();
            temp.setIdtype(aggVO.getParentVO().getIdtype());
            temp.setId(aggVO.getParentVO().getId());
            al.add(temp);
        }
        
        for (CertVO vo : al)
        {
            BlacklistVO blacklistVO = new BlacklistVO();
            blacklistVO.setId(vo.getId());
            blacklistVO.setIdtype(vo.getIdtype());
            blacklistVO.setPsnname(aggVO.getParentVO().getName());
            blacklistVO.setPsnname2(aggVO.getParentVO().getName2());
            blacklistVO.setPsnname3(aggVO.getParentVO().getName3());
            blacklistVO.setPsnname4(aggVO.getParentVO().getName4());
            blacklistVO.setPsnname5(aggVO.getParentVO().getName5());
            blacklistVO.setPsnname6(aggVO.getParentVO().getName6());
            blacklistVO.setPk_org(aggVO.getParentVO().getPk_org());
            blacklistVO.setPk_group(aggVO.getParentVO().getPk_group());
            boolean blInBlacklist = NCLocator.getInstance().lookup(IBlacklistManageService.class).isInBlacklist(blacklistVO);
            if (blInBlacklist)
            {
                throw new BusinessException(ResHelper.getString("6007psn", "06007psn0235")/*
                                                                                           * @res
                                                                                           * "该人员已经在黑名单中存在！"
                                                                                           */);
            }
        }
    }
    
    /***************************************************************************
     * Created on 2010-6-12 13:46:47<br>
     * @param strPk_psndoc
     * @return String
     * @author Rocex Wang
     * @throws BusinessException
     ***************************************************************************/
    @Override
    public boolean isInJob(String strPk_psndoc) throws BusinessException
    {
        return getPsndocDAO().isInJob(strPk_psndoc);
    }
    
    /**
     * 查询某个人员针对于某个岗位（职务）的能力匹配结果
     * @param type
     *            类型，是岗位还是职务，见IAbilityMatch
     * @param pk
     *            职务或者岗位的pk
     * @param pk_psndoc
     *            人员基本信息主键
     * @return AbilityMatchVO[]
     * @throws BusinessException
     */
    protected AbilityMatchVO[] queryAbilityMatchResult(String type, String pk, String pk_psndoc) throws BusinessException
    {
        // 首先根据岗位或者职务pk，查询出岗位（职务）的素质指标
        Class<? extends SuperVO> clz = IAbilityMatch.MATCH_OBJ_POST.equals(type) ? PostNeedVO.class : JobNeedVO.class;
        String field = IAbilityMatch.MATCH_OBJ_POST.equals(type) ? PostNeedVO.PK_POST : JobNeedVO.PK_JOB;
        NCObject[] objects = null;
        try
        {
            objects = MDPersistenceService.lookupPersistenceQueryService().queryBillOfNCObjectByCond(clz, field + "='" + pk + "'", false);
        }
        catch (MetaDataException e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage(), e);
        }
        if (ArrayUtils.isEmpty(objects))
        {
            return null;
        }
        IJobNeed[] needVOs = new IJobNeed[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            needVOs[i] = (IJobNeed) objects[i].getContainmentObject();
        }
        // 然后查询出人员的指标情况
        try
        {
            objects =
                MDPersistenceService.lookupPersistenceQueryService().queryBillOfNCObjectByCond(ReqVO.class,
                    ReqVO.PK_PSNDOC + "='" + pk_psndoc + "'", false);
        }
        catch (MetaDataException e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage(), e);
        }
        // 人员素质指标的map，key是指标的主键，value是ReqVO
        Map<String, ReqVO> psnIndexMap = new HashMap<String, ReqVO>();
        if (!ArrayUtils.isEmpty(objects))
        {
            for (NCObject obj : objects)
            {
                ReqVO vo = (ReqVO) obj.getContainmentObject();
                psnIndexMap.put(vo.getPk_postrequire_h(), vo);
            }
        }
        // 然后将岗位（职务）的要求的指标与人员的指标进行比对
        List<AbilityMatchVO> returnList = new ArrayList<AbilityMatchVO>();
        BaseDAO dao = new BaseDAO();
        for (IJobNeed need : needVOs)
        {
            // 岗位（职务）的指标信息
            AbilityMatchVO matchVO = new AbilityMatchVO();
            returnList.add(matchVO);
            matchVO.setMatch_result(AbilityMatchVO.UNKNOWN);
            matchVO.setPk_competencytype(need.getPk_competencytype());
            matchVO.setPk_competency_h(need.getPk_competency_h());
            matchVO.setPk_competency_b(need.getPk_competency_b());
            matchVO.setWeight(need.getWeight());
            // 指标类别的vo
            CompetencyindexTypeVO typeVO =
                (CompetencyindexTypeVO) dao.retrieveByPK(CompetencyindexTypeVO.class, need.getPk_competencytype());
            // 指标vo
            CompetencyindexVO indexVO = (CompetencyindexVO) dao.retrieveByPK(CompetencyindexVO.class, need.getPk_competency_h());
            // 指标等级vo
            Collection<CompetencyindexGradeVO> gradeColl =
                dao.retrieveByClause(CompetencyindexGradeVO.class, CompetencyindexGradeVO.PK_CINDEX + "='" + need.getPk_competency_h()
                    + "'");
            CompetencyindexGradeVO[] gradeVOs =
                gradeColl == null || gradeColl.size() == 0 ? null : gradeColl.toArray(new CompetencyindexGradeVO[0]);
            // 岗位（职务）要求的等级的vo
            CompetencyindexGradeVO requestGradeVO =
                (CompetencyindexGradeVO) dao.retrieveByPK(CompetencyindexGradeVO.class, need.getPk_competency_b());
            matchVO.setTypeVO(typeVO);
            matchVO.setIndexVO(indexVO);
            matchVO.setGradeVOs(gradeVOs);
            matchVO.setRequestLevel(requestGradeVO);
            // 人员实际的指标信息
            ReqVO reqVO = psnIndexMap.get(need.getPk_competency_h());
            // 如果人员没有这个指标，或者人员的实际等级为空，或者指标没有等级，则匹配结果为未知，因为这种情况下无法判断
            if (reqVO == null || StringUtils.isEmpty(reqVO.getPk_postrequire_b()) || ArrayUtils.isEmpty(gradeVOs))
            {
                continue;
            }
            matchVO.setPk_achievelevel(reqVO.getPk_postrequire_b());
            for (CompetencyindexGradeVO vo : gradeVOs)
            {
                if (vo.getPk_cindex_grade().equals(reqVO.getPk_postrequire_b()))
                {
                    matchVO.setAchieveLevel(vo);
                    // 人员实际等级的序号
                    int achieve = vo.getCode();
                    // 要求等级的序号
                    int request = requestGradeVO.getCode();
                    // 进行比较
                    Integer result =
                        achieve == request ? AbilityMatchVO.ACHIEVE : achieve < request ? AbilityMatchVO.NOT_ACHIEVE
                            : AbilityMatchVO.EXCEED;
                    matchVO.setMatch_result(result);
                }
            }
        }
        return returnList.toArray(new AbilityMatchVO[0]);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-6 18:32:34<br>
     * @see nc.itf.hi.IPsndocQryService#queryAbilityMatchVOsByJob(java.lang.String,java.lang.String)
     ****************************************************************************/
    @Override
    public AbilityMatchVO[] queryAbilityMatchVOsByJob(String pkJob, String pk_psndoc) throws BusinessException
    {
        return queryAbilityMatchResult(IAbilityMatch.MATCH_OBJ_JOB, pkJob, pk_psndoc);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-6 18:32:43<br>
     * @see nc.itf.hi.IPsndocQryService#queryAbilityMatchVOsByPost(java.lang.String, java.lang.String)
     ****************************************************************************/
    @Override
    public AbilityMatchVO[] queryAbilityMatchVOsByPost(String pkPost, String pk_psndoc) throws BusinessException
    {
        return queryAbilityMatchResult(IAbilityMatch.MATCH_OBJ_POST, pkPost, pk_psndoc);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-6-2 11:24:40<br>
     * @see nc.itf.hi.IPsndocQryService#queryAbilityMatchVOsByPsnJob(java.lang.String)
     ****************************************************************************/
    @Override
    public AbilityMatchVO[] queryAbilityMatchVOsByPsnJob(String pkPsnjob) throws BusinessException
    {
        // 首先查询人员的任职
        PsnJobVO jobVO = new PsndocDAO().queryByPk(PsnJobVO.class, pkPsnjob);
        if (StringUtils.isNotEmpty(jobVO.getPk_post()))
        {
            return queryAbilityMatchVOsByPost(jobVO.getPk_post(), jobVO.getPk_psndoc());
        }
        if (StringUtils.isNotEmpty(jobVO.getPk_job()))
        {
            return queryAbilityMatchVOsByJob(jobVO.getPk_job(), jobVO.getPk_psndoc());
        }
        return null;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-30 9:21:50<br>
     * @see nc.itf.hi.IPsndocQryService#queryAvailablePsnjobByCondition(java.lang.String, java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsnJobVO[] queryAvailablePsnjobByCondition(String condition, String orderby) throws BusinessException
    {
        if (StringUtils.isNotBlank(condition))
        {
            condition += " and " + PsnJobVO.LASTFLAG + " = 'Y' ";
        }
        else
        {
            condition = PsnJobVO.LASTFLAG + " = 'Y' ";
        }
        if (StringUtils.isNotBlank(orderby))
        {
            condition += " order by " + orderby;
        }
        return queryPsnJobs(condition);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsnBudgetVOs(java.lang.String, int)
     ****************************************************************************/
    @Override
    public PsndocVO[] queryPsnBudgetVOs(String pkPost, int year) throws BusinessException
    {
        // 首先查询人员：
        PsndocVO[] psnVOs = queryPsndocVOByPostWhetherInPost(pkPost, true);
        if (ArrayUtils.isEmpty(psnVOs))
        {
            return null;
        }
        // 查询岗位所属集团
        PostVO postVO = (PostVO) new BaseDAO().retrieveByPK(PostVO.class, pkPost);
        // 如果有人员，则要对人员的占编信息进行处理
        IBudgetSetQueryService budgetService = NCLocator.getInstance().lookup(IBudgetSetQueryService.class);
        BudgetPsnTypeRegVO[] psnClSetVOs = budgetService.queryBudgetPsnTypeRegVOs(postVO.getPk_group());
        // 是否定义了人员类别占编设置
        boolean isDefineBudget = !ArrayUtils.isEmpty(psnClSetVOs);
        // 占编的人员类别主键set
        Set<String> holdBudgetPsnClsSet = new HashSet<String>();
        if (isDefineBudget)
        {
            for (BudgetPsnTypeRegVO vo : psnClSetVOs)
            {
                holdBudgetPsnClsSet.add(vo.getPk_psncl());
            }
        }
        // 循环处理每个人
        PsntypeBudgeVO[] psnbudgetVO = new PsntypeBudgeVO[psnVOs.length];
        for (int i = 0; i < psnVOs.length; i++)
        {
            // 复制psnVO的属性到psnbudgetVO
            psnbudgetVO[i] = new PsntypeBudgeVO();
            String[] attrs = psnVOs[i].getAttributeNames();
            for (int j = 0; j < attrs.length; j++)
            {
                psnbudgetVO[i].setAttributeValue(attrs[j], psnVOs[i].getAttributeValue(attrs[j]));
                psnbudgetVO[i].setPsnJobVO(psnVOs[i].getPsnJobVO());
            }
            
            if (!isDefineBudget)
            {
                psnbudgetVO[i].setWorkouttype(PsntypeBudgeVO.WORKOUTTYPE_UNDEFINE);
                continue;
            }
            if (psnbudgetVO[i].getPsnJobVO().getIsmainjob().booleanValue()
                && holdBudgetPsnClsSet.contains(psnbudgetVO[i].getPsnJobVO().getPk_psncl()))
            {
                psnbudgetVO[i].setWorkouttype(PsntypeBudgeVO.WORKOUTTYPE_WORKOUT);
                continue;
            }
            psnbudgetVO[i].setWorkouttype(PsntypeBudgeVO.WORKOUTTYPE_UNWORKOUT);
        }
        return psnbudgetVO;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-6-17 20:24:43<br>
     * nc.itf.hi.IPsndocQryService#queryPsndocPks(nc.vo.uif2.LoginContext, java.lang.String, java.lang.String,
     * java.lang.String, java.util.HashMap)<br>
     * <b>此方法慎用</b>
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public String[] queryPsndocPks(LoginContext context, String[] strSelectFields, String strFromPart, String strWhere, String strOrder,
            HashMap<String, String> hash, String resourceCode) throws BusinessException
    {
        HIQueryLogUtils.writeQueryLog("64820b0f-38a2-4906-8d04-15f24ba7cbe6", context.getPk_org(), "QueryOperation");
        
        strFromPart = strFromPart == null ? PsndocVO.getDefaultTableName() : strFromPart;
        if (strSelectFields == null || strSelectFields.length == 0)
        {
            strSelectFields = new String[]{PsnJobVO.getDefaultTableName() + "." + PsnJobVO.PK_PSNJOB};
        }
        String strSelectField = "";
        for (String strField : strSelectFields)
        {
            strSelectField += "," + strField;
        }
        String strSQL = "select " + strSelectField.substring(1) + " from " + strFromPart + " where 1=1";
        SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(strSQL);
        sqlWhereUtil.and(strWhere);
        String alias = getTableAlias(hash, PsnJobVO.getDefaultTableName());
        
        // 组织权限
        String orgSql =
            HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE, alias);
        if (StringUtils.isNotBlank(orgSql))
        {
            sqlWhereUtil.and(orgSql);
        }
        
        // 部门权限
        String deptSql =
            HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, alias);
        if (StringUtils.isNotBlank(deptSql))
        {
            sqlWhereUtil.and(deptSql);
        }
        
        // 人员类别权限
        String psnclSql =
            HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_PSNCL, IRefConst.DATAPOWEROPERATION_CODE, alias);
        if (StringUtils.isNotBlank(psnclSql))
        {
            sqlWhereUtil.and(psnclSql);
        }
        
        strSQL = sqlWhereUtil.getSQLWhere();
        
        if (StringUtils.isNotBlank(strOrder))
        {
            strSQL += " order by " + strOrder;
        }
        else
        {
            
            String orderby = " order by org_orgs.code ,org_dept.displayorder, " + alias + ".showorder , bd_psndoc.code";
            // 如果没有排序字段 先到数据库中查询有没有当前用户的排序设置
            SortVO sortVOs[] = null;
            SortconVO sortconVOs[] = null;
            String strCondition =
                " func_code='" + context.getNodeCode() + "'" + " and group_code= 'TableCode' and ((pk_corp='" + PubEnv.getPk_group()
                    + "' and pk_user='" + PubEnv.getPk_user() + "') or pk_corp ='@@@@') order by pk_corp";
            
            sortVOs =
                (SortVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, SortVO.class, strCondition);
            Vector<Attribute> vectSortField = new Vector<Attribute>();
            if (sortVOs != null && sortVOs.length > 0)
            {
                strCondition = "pk_hr_sort='" + sortVOs[0].getPrimaryKey() + "' order by field_seq ";
                sortconVOs =
                    (SortconVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                        .retrieveByClause(null, SortconVO.class, strCondition);
                for (int i = 0; sortconVOs != null && i < sortconVOs.length; i++)
                {
                    Pair<String> field = new Pair<String>(sortconVOs[i].getField_name(), sortconVOs[i].getField_code());
                    Attribute attribute = new Attribute(field, sortconVOs[i].getAscend_flag().booleanValue());
                    vectSortField.addElement(attribute);
                }
                orderby = getOrderby(vectSortField, hash);
                if (StringUtils.isNotBlank(orderby))
                {
                    orderby = " order by " + orderby;
                }
            }
            
            // end
            // 没有设置排序字段,按照默认排序
            strSQL += orderby;
        }
        
        List<String> list = (List<String>) new BaseDAO().executeQuery(strSQL, new ColumnListProcessor());
        
        if (list != null && list.size() > 0)
        {
        	/**modify start:去重 yunan 2014-1-8*/
        	HashSet<String> set = new HashSet<String>();
        	for (String string : list) {
        		set.add(string);
        	}
        	/**modify end:yunana 2014-1-8*/
            return set.toArray(new String[0]);
        }
        return null;
    }
    
    protected String getOrderby(Vector<Attribute> vectSortField, HashMap<String, String> hash)
    {
        if (vectSortField == null || vectSortField.size() == 0)
        {
            return "";
        }
        String strOrderBy = "";
        for (Attribute attr : vectSortField)
        {
            String strFullCode = attr.getAttribute().getValue();
            String strTableName = "";
            String strCode = strFullCode;
            int iDotIndex = strFullCode.indexOf(".");
            if (iDotIndex > 0)
            {
                strTableName = strFullCode.substring(0, iDotIndex);
                strCode = strFullCode.substring(iDotIndex);
            }
            strFullCode = getTableAlias(hash, strTableName) + strCode;
            strOrderBy = strOrderBy + "," + strFullCode + (attr.isAscend() ? "" : " desc");
        }
        return strOrderBy.length() > 0 ? strOrderBy.substring(1) : "";
    }
    
    private String getTableAlias(HashMap<String, String> hash, String strTableName)
    {
        return hash.containsKey(strTableName) ? hash.get(strTableName) : strTableName;
    }
    
    /***************************************************************************
     * 查询人员列表，按照传入的字段进行查询，以提高查询速度
     * @param context
     * @param listField
     * @param strTableNames
     * @param strCondition
     * @param strOrder
     * @return PsndocVO[]
     * @throws BusinessException
     **************************************************************************/
    @Override
    public PsndocVO[] queryPsndocVOByCondition(LoginContext context, List<String> listField, String[] strTableNames, String strCondition,
            String strOrder) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByCondition(context, listField, strTableNames, strCondition, strOrder);
    }
    
    /***************************************************************************
     * 主查询 s * @param context
     * @param strTableNames
     * @param strCondition
     * @param strOrder
     * @return PsndocVO[]
     * @throws BusinessException
     **************************************************************************/
    @Override
    public PsndocVO[] queryPsndocVOByCondition(LoginContext context, String[] strTableNames, String strCondition, String strOrder)
            throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByCondition(context, strTableNames, strCondition, strOrder);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-22 10:37:05<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsndocVOByDeptPK(java.lang.String[], boolean, boolean)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsndocVO[] queryPsndocVOByDeptPK(String[] pk_depts, boolean incumbencyOnly, boolean includeNoneIndoc) throws BusinessException
    {
        PsndocVO[] psndocVOs = getPsndocDAO().retrievePsndocVOByDeptPK(pk_depts, incumbencyOnly, includeNoneIndoc);
        if (includeNoneIndoc)
        {
            return psndocVOs;
        }
        if (ArrayUtils.isEmpty(psndocVOs))
        {
            return new PsndocVO[0];
        }
        List<PsndocVO> result = new ArrayList<PsndocVO>();
        for (PsndocVO psndocVO : psndocVOs)
        {
            if (UFBoolean.TRUE.equals(psndocVO.getPsnOrgVO().getIndocflag()))
            {
                result.add(psndocVO);
            }
        }
        return result.toArray(new PsndocVO[0]);
    }
    
    @Override
    public String getDeptPsnCondition(String curDeptPk, String curDeptMgrPsndoc, boolean isIncludeChief, boolean isIncludeOtherMgr)
            throws BusinessException
    {
        DeptVO dept = (DeptVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null, DeptVO.class, curDeptPk);
        String deptSql = " select pk_dept from org_dept where innercode like '" + dept.getInnercode() + "%' ";
        
        // 所有人员的sql片段
        String sql =
            "select distinct bd_psndoc.pk_psndoc from bd_psndoc bd_psndoc "
                + " inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc and hi_psnorg.lastflag ='Y'  "
                + " and hi_psnorg.psntype = 0 and hi_psnorg.indocflag ='Y' "
                + " inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg and hi_psnjob.lastflag ='Y'  "
                + " where hi_psnjob.pk_dept in ( " + deptSql + " )";
        
        int i =
            NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                .getCountByCondition("org_dept", " pk_dept = '" + curDeptPk + "' and principal = '" + curDeptMgrPsndoc + "' ");
        
        if (i > 0)
        {
            // 当前人员是部门负责人,可以查看所有
            return sql;
        }
        
        if (!isIncludeChief)
        {
            // 不包含部门负责人
            String sql1 = " select principal from org_dept where pk_dept in (" + deptSql + ") ";
            sql += " and bd_psndoc.pk_psndoc not in ( " + sql1 + " ) ";
        }
        
        // if (!isIncludeOtherMgr) {
        // //不包含其他部门主管
        // String sql2 = " select pk_psndoc from org_orgmanager where pk_dept in ( " + deptSql +
        // " ) and pk_psndoc <> '"
        // + curDeptMgrPsndoc + "' ";
        // sql += " and bd_psndoc.pk_psndoc not in ( " + sql2 + " ) ";
        // }
        if (!isIncludeOtherMgr)
        {
            String sql1 = " select principal from org_dept where pk_dept in (" + deptSql + ") ";
            // 不包含其他部门主管
            String sql2 =
                " select pk_psndoc from org_orgmanager where pk_dept in ( " + deptSql + " ) and pk_psndoc <> '" + curDeptMgrPsndoc
                    + "' and pk_psndoc not in (" + sql1 + " ) ";
            sql += " and bd_psndoc.pk_psndoc not in ( " + sql2 + " ) ";
        }
        
        return sql;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-6 13:28:04<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsndocVOByJobPK(java.lang.String, boolean, boolean, boolean,
     *      boolean, boolean)
     ****************************************************************************/
    @Override
    public PsndocVO[] queryPsndocVOByJobPK(String pk_job, boolean includeInPos, boolean includeOutPos, boolean inCludeMainJob,
            boolean includePartTimeJob, boolean includeNoneIndoc) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByJobPK(pk_job, includeInPos, includeOutPos, inCludeMainJob, includePartTimeJob,
            includeNoneIndoc);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-15 14:20:51<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsndocVOByJobPostFamily(nc.vo.pub.SuperVO)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public <T extends SuperVO> PsndocVO queryPsndocVOByJobPostFamily(T vo) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByJobPostFamily(vo);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-7-8 14:57:10<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsndocVOByJobPostFamily(nc.vo.pub.SuperVO, java.lang.String)
     ****************************************************************************/
    @Override
    public <T extends SuperVO> PsndocVO queryPsndocVOByJobPostFamily(T vo, String psnjobcond) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByJobPostFamily(vo, psnjobcond);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-7-8 14:57:00<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsndocVOByJobWhetherInPost(java.lang.String, boolean)
     ****************************************************************************/
    @Override
    public PsndocVO[] queryPsndocVOByJobWhetherInPost(String pkJob, boolean inPos) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByWhetherInPost(PsnJobVO.class, PsnjobVO.PK_JOB, pkJob, inPos);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-6-17 20:43:36<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsndocVOByPks(java.lang.String[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsndocAggVO[] queryPsndocVOByPks(String[] strPks) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByPks(true, strPks);
    }
    
    @Override
    public PsndocAggVO queryPsndocVOByPk(String pk_psndoc) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByPk(pk_psndoc);
    }
    
    @Override
    public PsndocAggVO queryPsndocVOByPk(String pk_psndoc, boolean includePhoto, boolean includePreviewPhoto) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByPk(pk_psndoc, includePhoto, includePreviewPhoto);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-5-6 13:28:04<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsndocVOByPostPK(java.lang.String, boolean, boolean, boolean,
     *      boolean, boolean)
     ****************************************************************************/
    @Override
    public PsndocVO[] queryPsndocVOByPostPK(String pk_post, boolean includeInPos, boolean includeOutPos, boolean inCludeMainJob,
            boolean includePartTimeJob, boolean includeNoneIndoc) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByPostPK(pk_post, includeInPos, includeOutPos, inCludeMainJob, includePartTimeJob,
            includeNoneIndoc);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-7-8 14:57:16<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsndocVOByPostPkAndLevel(java.lang.String, boolean, int)
     ****************************************************************************/
    @Override
    public PsndocVO[] queryPsndocVOByPostPkAndLevel(String pk_post, boolean inPos, int level) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByPostPkAndLevel(pk_post, inPos, level);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-7-8 14:57:18<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsndocVOByPostWhetherInPost(java.lang.String, boolean)
     ****************************************************************************/
    @Override
    public PsndocVO[] queryPsndocVOByPostWhetherInPost(String pkPost, boolean inPos) throws BusinessException
    {
        PsndocVO psndocVOs[] = getPsndocDAO().queryPsndocVOByWhetherInPost(PsnBudgetVO.class, PsnJobVO.PK_POST, pkPost, inPos);
        return psndocVOs;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-7-8 14:57:21<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsndocVOByWhetherInPost(java.lang.String, java.lang.String,
     *      boolean)
     ****************************************************************************/
    @Override
    public PsndocVO[] queryPsndocVOByWhetherInPost(String fieldName, String fieldValue, boolean inPos) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByWhetherInPost(PsnJobVO.class, fieldName, fieldValue, inPos);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-22 10:37:16<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsninfoByCondition(java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public List<PsnJobVO> queryPsninfoByCondition(String strWhere) throws BusinessException
    {
        return getPsndocDAO().queryPsninfoByCondition(strWhere);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-22 10:37:16<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsninfoByPks(java.lang.String[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public List<PsnJobVO> queryPsninfoByPks(String[] pks) throws BusinessException
    {
        return getPsndocDAO().queryPsninfoByPks(pks);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-26 14:08:47<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsnJobs(java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsnJobVO[] queryPsnJobs(String strCondition) throws BusinessException
    {
        return getPsndocDAO().queryByCondition(PsnJobVO.class, strCondition);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-26 11:14:14<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsnMainJob(java.lang.String, int)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsnJobVO queryPsnMainJob(String strPkPsnorg, int iAssigId) throws BusinessException
    {
        PsnJobVO psnJobVOs[] = getPsndocDAO().queryPsnMainJob(strPkPsnorg, iAssigId);
        return psnJobVOs == null || psnJobVOs.length == 0 ? null : psnJobVOs[0];
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-26 13:21:55<br>
     * @see nc.itf.hi.IPsndocQryService#queryPsnPartTimeJobs(java.lang.String, int, java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsnJobVO[] queryPsnPartTimeJobs(String strPkPsnorg, int iAssigId, String strCondition) throws BusinessException
    {
        PsnJobVO psnJobVOs[] = getPsndocDAO().queryPsnPartTimeJobs(strPkPsnorg, iAssigId, strCondition);
        return psnJobVOs;
    }
    
    @Override
    public SuperVO[] querySubVO(Class clazz, String strWhere, String strOrder) throws BusinessException
    {
        return getPsndocDAO().querySubByCondition(clazz, strWhere, strOrder);
    }
    
    
    /***
     * 导入工具专用接口， savePsndoc()方法的瘦身版 与原方法相比去掉了： 编制校验 黑名单校验 撤销部门校验 撤销岗位校验
     * @throws BusinessException
     */
    @Override
    public void savePsndocForImport(PsndocAggVO psndocAggVO) throws BusinessException
    {
        setCodeAndName(psndocAggVO);
        
        TimerLogger timerLogger = new TimerLogger("导入工具效率测试——人员", null);
        timerLogger.addLog("工作记录日期校验开始");
        validateDate(psndocAggVO);
        timerLogger.addLog("工作记录日期校验结束");
        
        timerLogger.addLog("编码、员工号唯一性校验开始");
        validateCode(psndocAggVO);
        timerLogger.addLog("编码、员工号唯一性校验结束");
        
        timerLogger.addLog("用户自定义规则校验开始");
        // 检查用户自定义所有规则
        getPsndocDAO().checkPsnUserUniqueRules(psndocAggVO.getParentVO());
        timerLogger.addLog("用户自定义规则校验结束");
        
        timerLogger.addLog("设置审计信息开始");
        // 设置审计信息/此处vo的状态是正确的,可以设置审计信息
        getPsndocDAO().setAuditInfoAndTs(psndocAggVO);
        timerLogger.addLog("设置审计信息结束");
        
        timerLogger.addLog("子集业务校验开始");
        // 后台校验数据
        ValidationFailure failure = new PsndocValidator().validate(psndocAggVO);
        timerLogger.addLog("子集业务校验结束");
        if (failure != null)
        {
            throw new BusinessException(failure.getMessage());
        }
        
        timerLogger.addLog("DAO数据保存开始");
        // 保存
        getPsndocDAO().savePsndocForImport(psndocAggVO,timerLogger);
        timerLogger.addLog("DAO数据保存结束");
        timerLogger.log();
    }
    
    private void setCodeAndName(PsndocAggVO psndocAggVO)
    {
        // 处理人员编码、姓名前后不能有空格
        PsndocVO psndoc = psndocAggVO.getParentVO();
        if (StringUtils.isNotBlank(psndoc.getCode()))
        {
            psndoc.setCode(psndoc.getCode().trim());
        }
        if (StringUtils.isNotBlank(psndoc.getName()))
        {
            psndoc.setName(psndoc.getName().trim());
        }
        if (StringUtils.isNotBlank(psndoc.getName2()))
        {
            psndoc.setName2(psndoc.getName2().trim());
        }
        if (StringUtils.isNotBlank(psndoc.getName3()))
        {
            psndoc.setName3(psndoc.getName3().trim());
        }
        if (StringUtils.isNotBlank(psndoc.getName4()))
        {
            psndoc.setName4(psndoc.getName4().trim());
        }
        if (StringUtils.isNotBlank(psndoc.getName5()))
        {
            psndoc.setName5(psndoc.getName5().trim());
        }
        if (StringUtils.isNotBlank(psndoc.getName6()))
        {
            psndoc.setName6(psndoc.getName6().trim());
        }
    }
    
    /**
     * 日期校验，去掉了原方法的编制校验 去掉了黑名单校验
     * @param psndocAggVO
     * @throws BusinessException
     */
    private void validateDate(PsndocAggVO psndocAggVO) throws BusinessException
    {
        UFLiteralDate begindate = psndocAggVO.getParentVO().getPsnJobVO().getBegindate();
        PsnJobVO psnjob[] = (PsnJobVO[]) psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName());
        for (int i = 0; psnjob != null && i < psnjob.length; i++)
        {
            if (VOStatus.DELETED == psnjob[i].getStatus())
            {
                continue;
            }
            begindate = psnjob[i].getBegindate();
            break;
        }
        
        // 日期校验
        NCLocator.getInstance().lookup(IPersonRecordService.class).checkBeginDate(psndocAggVO.getParentVO().getPsnJobVO(), begindate,
            psndocAggVO.getParentVO().getPsnOrgVO().getBegindate());
        
    }
    
    /**
     * 校验人员编码和员工号
     * @throws BusinessException
     */
    private void validateCode(PsndocAggVO psndocAggVO) throws BusinessException
    {
        // 检查人员编码全局唯一
        getPsndocDAO().checkPsnCodeUnique(psndocAggVO.getParentVO());
        
        // 检查前，将兼职记录的员工号改为最新工作记录的员工号
        SuperVO[] partjobVOs = psndocAggVO.getTableVO(PartTimeVO.getDefaultTableName());
        if (partjobVOs != null && partjobVOs.length > 0)
        {
            String clerkcode = psndocAggVO.getParentVO().getPsnJobVO().getClerkcode();
            for (int i = 0; i < partjobVOs.length; i++)
            {
                partjobVOs[i].setAttributeValue(PsnJobVO.CLERKCODE, clerkcode);
            }
        }
        
        PsnJobVO job[] = (PsnJobVO[]) psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName());
        for (int i = 0; job != null && i < job.length; i++)
        {
            if (VOStatus.DELETED == job[i].getStatus())
            {
                continue;
            }
            
            // 设置员工号前后不为空格
            if (StringUtils.isNotBlank(job[i].getClerkcode()))
            {
                job[i].setClerkcode(job[i].getClerkcode().trim());
            }
            
        }
        
        PartTimeVO partTimeVOs[] = (PartTimeVO[]) psndocAggVO.getTableVO(PartTimeVO.getDefaultTableName());
        for (int i = 0; partTimeVOs != null && i < partTimeVOs.length; i++)
        {
            if (VOStatus.DELETED == partTimeVOs[i].getStatus())
            {
                continue;
            }
            if (StringUtils.isNotBlank(partTimeVOs[i].getClerkcode()))
            {
                partTimeVOs[i].setClerkcode(partTimeVOs[i].getClerkcode().trim());
            }
        }
        
        // 检查员工号在所在任职组织是否唯一
        getPsndocDAO().checkClerkCodeUnique(psndocAggVO);
    }

    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-15 10:53:41<br>
     * @see nc.itf.hi.IPsndocService#savePsndoc(PsndocAggVO)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsndocAggVO savePsndoc(PsndocAggVO psndocAggVO, boolean isValidateBudget) throws BusinessException
    {
        // 处理人员编码、姓名前后不能有空格
        PsndocVO psndoc = psndocAggVO.getParentVO();
        if (StringUtils.isNotBlank(psndoc.getCode()))
        {
            psndoc.setCode(psndoc.getCode().trim());
        }
        if (StringUtils.isNotBlank(psndoc.getName()))
        {
            psndoc.setName(psndoc.getName().trim());
        }
        if (StringUtils.isNotBlank(psndoc.getName2()))
        {
            psndoc.setName2(psndoc.getName2().trim());
        }
        if (StringUtils.isNotBlank(psndoc.getName3()))
        {
            psndoc.setName3(psndoc.getName3().trim());
        }
        if (StringUtils.isNotBlank(psndoc.getName4()))
        {
            psndoc.setName4(psndoc.getName4().trim());
        }
        if (StringUtils.isNotBlank(psndoc.getName5()))
        {
            psndoc.setName5(psndoc.getName5().trim());
        }
        if (StringUtils.isNotBlank(psndoc.getName6()))
        {
            psndoc.setName6(psndoc.getName6().trim());
        }
        
        UFLiteralDate begindate = psndocAggVO.getParentVO().getPsnJobVO().getBegindate();
        PsnJobVO psnjob[] = (PsnJobVO[]) psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName());
        for (int i = 0; psnjob != null && i < psnjob.length; i++)
        {
            if (VOStatus.DELETED == psnjob[i].getStatus())
            {
                continue;
            }
            begindate = psnjob[i].getBegindate();
            break;
        }
        
        if (isValidateBudget && psndocAggVO != null && psndocAggVO.getParentVO() != null && psndocAggVO.getParentVO().getPsnOrgVO() != null
            && psndocAggVO.getParentVO().getPsnOrgVO().getIndocflag() != null
            && psndocAggVO.getParentVO().getPsnOrgVO().getIndocflag().booleanValue()
            && psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName()) != null
            && psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName()).length > 0)
        {
            // 当是已转档的人员才校验
            // 校验编制
            LoginContext context = new LoginContext();
            context.setPk_group(psndocAggVO.getParentVO().getPsnJobVO().getPk_group());
            ValidateResultVO resultVOs[] =
                NCLocator
                    .getInstance()
                    .lookup(IOrgBudgetQueryService.class)
                    .validateBudgetValue(context, new PsnJobVO[]{psndocAggVO.getParentVO().getPsnJobVO()},
                        new PsnJobVO[]{psndocAggVO.getParentVO().getPsnJobVO()});
            
            String strErrorMsg = "";
            
            if (resultVOs != null)
            {
                for (ValidateResultVO resultVO : resultVOs)
                {
                    if (!resultVO.isValid())
                    {
                        strErrorMsg += "\n" + resultVO.getHintMsg();
                    }
                }
                
                if (strErrorMsg.length() > 0)
                {
                    throw new BusinessException(ResHelper.getString("6007psn", "06007psn0236")/* @res "保存失败:" */+ strErrorMsg);
                }
            }
        }
        
        // 日期校验
        NCLocator.getInstance().lookup(IPersonRecordService.class)
            .checkBeginDate(psndocAggVO.getParentVO().getPsnJobVO(), begindate, psndocAggVO.getParentVO().getPsnOrgVO().getBegindate());
        
        // 首先检查是否在黑名单
        isInBlacklist(psndocAggVO);
        // 检查人员编码全局唯一
        getPsndocDAO().checkPsnCodeUnique(psndocAggVO.getParentVO());
        
        // 检查前，将兼职记录的员工号改为最新工作记录的员工号
        SuperVO[] partjobVOs = psndocAggVO.getTableVO(PartTimeVO.getDefaultTableName());
        if (partjobVOs != null && partjobVOs.length > 0)
        {
            String clerkcode = psndocAggVO.getParentVO().getPsnJobVO().getClerkcode();
            for (int i = 0; i < partjobVOs.length; i++)
            {
                partjobVOs[i].setAttributeValue(PsnJobVO.CLERKCODE, clerkcode);
            }
        }
        
        PsnJobVO job[] = (PsnJobVO[]) psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName());
        for (int i = 0; job != null && i < job.length; i++)
        {
            if (VOStatus.DELETED == job[i].getStatus())
            {
                continue;
            }
            
            // 设置员工号前后不为空格
            if (StringUtils.isNotBlank(job[i].getClerkcode()))
            {
                job[i].setClerkcode(job[i].getClerkcode().trim());
            }
            
        }
        
        PartTimeVO partTimeVOs[] = (PartTimeVO[]) psndocAggVO.getTableVO(PartTimeVO.getDefaultTableName());
        for (int i = 0; partTimeVOs != null && i < partTimeVOs.length; i++)
        {
            if (VOStatus.DELETED == partTimeVOs[i].getStatus())
            {
                continue;
            }
            if (StringUtils.isNotBlank(partTimeVOs[i].getClerkcode()))
            {
                partTimeVOs[i].setClerkcode(partTimeVOs[i].getClerkcode().trim());
            }
        }
        
        // 检查员工号在所在任职组织是否唯一
        getPsndocDAO().checkClerkCodeUnique(psndocAggVO);
        
        // 首先检查人员唯一性。
        // getPsndocDAO().checkPsnUnique(psndocAggVO.getParentVO());
        
        // 检查用户自定义所有规则
        getPsndocDAO().checkPsnUserUniqueRules(psndocAggVO.getParentVO());
        
        // 设置审计信息/此处vo的状态是正确的,可以设置审计信息
        getPsndocDAO().setAuditInfoAndTs(psndocAggVO);
        
        // 后台校验数据
        ValidationFailure failure = new PsndocValidator().validate(psndocAggVO);
        if (failure != null)
        {
            throw new BusinessException(failure.getMessage());
        }
        // //回写职称信息 获得职称字段 到 主表 专业技术职务字段
        // TitleVO[] titleVOs = (TitleVO[]) psndocAggVO.getTableVO(TitleVO.getDefaultTableName());
        // if( titleVOs != null )
        // {
        // for(TitleVO titleVO : titleVOs)
        // {
        // if(titleVO.getTiptop_flag().booleanValue() == true)
        // {
        // psndocAggVO.getParentVO().setTitletechpost(titleVO.getPk_techposttitle());
        // }
        // }
        // }
        
        // //回写学历子集中的 学位字段
        // EduVO[] eduVOs = (EduVO[]) psndocAggVO.getTableVO(EduVO.getDefaultTableName());
        // if(eduVOs != null)
        // {
        // for(EduVO eduVO : eduVOs)
        // {
        // if(eduVO.getLasteducation().booleanValue() == true)
        // {
        // psndocAggVO.getParentVO().setPk_degree(eduVO.getPk_degree());
        // psndocAggVO.getParentVO().setEdu(eduVO.getEducation());
        // }
        // }
        // }
        // 由于岗位或者部门撤销后，这个节点不会刷新，所以进行部门岗位撤销校验
        PsnJobVO psnJobVO = psndocAggVO.getParentVO().getPsnJobVO();
        if (!StringUtils.isEmpty(psnJobVO.getPk_dept()))
        {
            HRDeptVO HRDeptVO =
                (HRDeptVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                    .retrieveByPk(HRDeptVO.class, psnJobVO.getPk_dept(), null);
            if (HRDeptVO.getHrcanceled().booleanValue())
            {
                throw new BusinessException(ResHelper.getString("6007psn", "06007psn0502")/*
                                                                                           * @res
                                                                                           * "任职部门为已撤销部门，请刷新重新选择"
                                                                                           */);
            }
        }
        if (!StringUtils.isEmpty(psnJobVO.getPk_post()))
        {
            nc.vo.om.post.PostVO postVO =
                (nc.vo.om.post.PostVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                    .retrieveByPk(nc.vo.om.post.PostVO.class, psnJobVO.getPk_post(), null);
            if (postVO.getHrcanceled().booleanValue())
            {
                throw new BusinessException(ResHelper.getString("6007psn", "06007psn0503")/*
                                                                                           * @res
                                                                                           * "任职岗位为已撤销岗位，请刷新重新选择"
                                                                                           */);
            }
        }
        
        //BEGIN 张恒 {21746} 入职登记新增的时候，将入职日期初始化到年资起算日 2018/9/4
        psndocAggVO.getParentVO().getPsnOrgVO().setAttributeValue("workagestartdate", psndocAggVO.getParentVO().getPsnOrgVO().getAttributeValue("workagestartdate"));
        //END 张恒 {21746} 入职登记新增的时候，将入职日期初始化到年资起算日 2018/9/4
        
        // 保存
        return getPsndocDAO().savePsndoc(psndocAggVO);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-4-15 10:53:46<br>
     * @see nc.itf.hi.IPsndocService#updateDataAfterSubDataChanged(java.lang.String, java.lang.String)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void updateDataAfterSubDataChanged(String strSubTableName, String... pk_psndoc) throws BusinessException
    {
        getPsndocDAO().updateDataAfterSubDataChanged(strSubTableName, pk_psndoc);
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-3-8 16:53:26<br>
     * @see nc.itf.hi.IPsndocService#updateSub(nc.vo.pub.SuperVO[])
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public SuperVO[] updateSub(SuperVO... subVOs) throws BusinessException
    {
        return null;
    }
    
    @Override
    public String[] queryHiddenKeys(LoginContext context) throws BusinessException
    {
        String condition =
            " pk_infoset in ( select pk_infoset from hr_infoset where pk_infoset_sort='" + HICommonValue.PSNDOC_INFOSET_SORT_PK
                + "' and main_table_flag = 'N' and pk_org in ( '" + IOrgConst.GLOBEORG + "' , '" + context.getPk_group()
                // + "' ) ) and pk_main_item is not  null ";
                + "' ) ) and pk_main_item <> '~' ";
        InfoItemVO[] items =
            (InfoItemVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, InfoItemVO.class, condition);
        HashMap<String, String> hm = new HashMap<String, String>();
        IPersistenceRetrieve service = NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
        for (int i = 0; items != null && i < items.length; i++)
        {
            if (StringUtils.isBlank(items[i].getPk_main_item()))
            {
                continue;
            }
            InfoItemVO mainitem = (InfoItemVO) service.retrieveByPk(null, InfoItemVO.class, items[i].getPk_main_item());
            if (mainitem == null || mainitem.getItem_code() == null)
            {
                continue;
            }
            if (hm.get(mainitem.getItem_code()) == null)
            {
                hm.put(mainitem.getItem_code(), mainitem.getItem_code());
            }
        }
        return hm.keySet() == null || hm.keySet().size() == 0 ? new String[0] : hm.keySet().toArray(new String[0]);
    }
    
    @Override
    public <T extends SuperVO> T[] fillDateFormula(T... vos) throws BusinessException
    {
        return getPsndocDAO().fillDateFormula(vos);
    }
    
    @Override
    public Object fillDateFormula4Psndoc(Object selectedData) throws BusinessException
    {
        if (selectedData == null)
        {
            return null;
        }
        PsndocAggVO agg = (PsndocAggVO) selectedData;
        
        // 检查人员是否还存在
        String pk_psndoc = agg.getParentVO().getPk_psndoc();
        int count =
            NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                .getCountByCondition(PsndocVO.getDefaultTableName(), " pk_psndoc = '" + pk_psndoc + "' ");
        if (count <= 0)
        {
            return null;
        }
        
        agg.setParentVO(fillDateFormula(agg.getParentVO())[0]);
        agg.getParentVO().setPsnJobVO(fillDateFormula(agg.getParentVO().getPsnJobVO())[0]);
        agg.getParentVO().setPsnOrgVO(fillDateFormula(agg.getParentVO().getPsnOrgVO())[0]);
        return agg;
    }
    
    @Override
    public UFLiteralDate queryIndutyDate(String pkPsnjob) throws BusinessException
    {
        
        PsnChgVO[] psnchg =
            (PsnChgVO[]) NCLocator
                .getInstance()
                .lookup(IPersistenceRetrieve.class)
                .retrieveByClause(null, PsnChgVO.class,
                    "pk_psnorg in ( select pk_psnorg from hi_psnjob where pk_psnjob = '" + pkPsnjob + "') and lastflag = 'Y'");
        if (psnchg == null || psnchg.length == 0)
        {
            return null;
        }
        return psnchg[0].getBegindate();
    }
    
    @Override
    public Object fillData4Psndoc(Object selectedData) throws BusinessException
    {
        if (selectedData == null)
        {
            return null;
        }
        Object obj = fillDateFormula4Psndoc(selectedData);
        if (obj == null)
        {
            return selectedData;
        }
        PsndocAggVO agg = (PsndocAggVO) obj;
        PsndocVO doc =
            (PsndocVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                .retrieveByPk(null, PsndocVO.class, agg.getParentVO().getPrimaryKey());
        agg.getParentVO().setPhoto(doc.getPhoto());
        return obj;
    }
    
    @Override
    public PsndocAggVO stopPoiRelation(PsndocVO psndocVO, UFLiteralDate endDate) throws BusinessException
    {
        // 界面的终止关系操作
        // IPersonRecordService recordService = NCLocator.getInstance().lookup(IPersonRecordService.class);
        
        BDVersionValidationUtil.validateSuperVO(psndocVO);
        
        HiEventValueObject.fireEvent(psndocVO.getPsnJobVO(), psndocVO.getPsnJobVO(), psndocVO.getPsnJobVO().getPk_hrorg(),
            HICommonValue.MD_ID_PSNDOC, IHiEventType.SAVE_POI_BEFORE);
        
        PsnJobVO jobvo = psndocVO.getPsnJobVO();
        jobvo.setEndflag(UFBoolean.TRUE);
        jobvo.setEnddate(endDate);
        jobvo.setPoststat(UFBoolean.FALSE);
        jobvo = getPsndocDAO().update4SubSet(jobvo, true, true);
        
        PsnOrgVO orgvo = psndocVO.getPsnOrgVO();
        orgvo.setEndflag(UFBoolean.TRUE);
        orgvo.setEnddate(endDate);
        getPsndocDAO().update4SubSet(orgvo, true, false);
        
        HiEventValueObject.fireEvent(jobvo, jobvo, jobvo.getPk_hrorg(), HICommonValue.MD_ID_PSNDOC, IHiEventType.SAVE_POI_AFTER);
        HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
        
        // 6)同步基本信息的pk_org,相关人员不用同步主表pk_org
        // recordService.synPkorgOfPsndoc(jobvo.getPk_org(), jobvo.getPk_psndoc());
        // 7)同步人员基本信息
        NCLocator.getInstance().lookup(IPsndocService.class)
            .updateDataAfterSubDataChanged(PsnJobVO.getDefaultTableName(), jobvo.getPk_psndoc());
        // 同步缓存
        HiCacheUtils.synCache(PsndocVO.getDefaultTableName(), PsnJobVO.getDefaultTableName(), PsnOrgVO.getDefaultTableName());
        
        return queryPsndocVOByPk(psndocVO.getPk_psndoc());
    }
    
    @Override
    public BusiReportStruMemberStru getFatherMemberFromBusiReportStru(String pk_psndoc, String pk_post, String pk_dept, String pk_job,
            String pk_stru) throws BusinessException
    {
        BusiReportStruMemberStru stru = new BusiReportStruMemberStru();
        stru.setPk_psndoc(pk_psndoc);
        stru.setPk_post(pk_post);
        stru.setPk_dept(pk_dept);
        stru.setPk_job(pk_job);
        stru.setPk_stru(pk_stru);
        return NCLocator.getInstance().lookup(IHrOrgPubService.class).getFatherMemberFromBusiReportStru(stru);
    }
    
    @Override
    public PsnJobVO[] queryPsndocVOsByDeptPK(String pkDept, boolean includeSubDept, FromWhereSQL fromWhereSQL) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOsByDeptPK(pkDept, includeSubDept, fromWhereSQL);
    }
    
    @Override
    public PsnJobVO[] queryPsnjobVOsByDeptPK(String pkDept, boolean includeSubDept) throws BusinessException
    {
        
        String deptSql = "";
        if (!includeSubDept)
        {
            deptSql = " select pk_dept from org_dept where pk_dept = '" + pkDept + "'";
        }
        else
        {
            DeptVO dept = getPsndocDAO().queryByPk(DeptVO.class, pkDept, true);
            deptSql = " select pk_dept from org_dept where innercode like '" + dept.getInnercode() + "%' ";
        }
        
        String sql =
            "select hi_psnjob.* from bd_psndoc inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc "
                + " and hi_psnorg.indocflag ='Y' inner join hi_psnjob on hi_psnjob.pk_psnorg = hi_psnorg.pk_psnorg ";
        sql += " where hi_psnjob.pk_dept in (" + deptSql + ") ";
        
        ArrayList<PsnJobVO> al = (ArrayList<PsnJobVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(PsnJobVO.class));
        return al == null ? null : al.toArray(new PsnJobVO[0]);
        
    }
    
    @Override
    public PsnJobVO[] queryPsndocVOsByPsnInfo(String psnCode, String psnName, String eMail, String phoneNo, String deptName, String pkGroup)
            throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOsByPsnInfo(psnCode, psnName, eMail, phoneNo, deptName, pkGroup);
    }
    
    @Override
    public PsndocVO[] queryPsnPhotoInfo(String[] pks) throws BusinessException
    {
        if (pks == null || pks.length == 0)
        {
            return null;
        }
        
        InSQLCreator util = null;
        try
        {
            util = new InSQLCreator();
            
            String inSql = " pk_psndoc in ( " + util.getInSQL(pks) + " ) ";
            
            String sql = " select code , name ,photo from bd_psndoc where " + inSql;
            
            List<PsndocVO> psnList = (List<PsndocVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(PsndocVO.class));
            if (psnList == null || psnList.size() == 0)
            {
                return null;
            }
            
            return psnList.toArray(new PsndocVO[0]);
            
        }
        catch (Exception e)
        {
            Logger.error(e.getMessage(), e);
        }
        
        return null;
    }
    
    @Override
    public void poiDeptChange(PsnJobVO... jobVOs) throws BusinessException
    {
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; jobVOs != null && i < jobVOs.length; i++)
        {
            HiEventValueObject.fireEvent(jobVOs[i], jobVOs[i], jobVOs[i].getPk_hrorg(), HICommonValue.MD_ID_PSNDOC,
                IHiEventType.SAVE_POI_BEFORE);
            al.add(jobVOs[i].getPk_psndoc());
            jobVOs[i].setStatus(VOStatus.UPDATED);
        }
        String[] pks = MDPersistenceService.lookupPersistenceService().saveBill(jobVOs);
        Object[] newVOs = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKsWithOrder(PsnJobVO.class, pks, true);
        for (int i = 0; jobVOs != null && i < jobVOs.length; i++)
        {
            HiEventValueObject.fireEvent(jobVOs[i], (PsnJobVO) newVOs[i], ((PsnJobVO) newVOs[i]).getPk_hrorg(), HICommonValue.MD_ID_PSNDOC,
                IHiEventType.SAVE_POI_AFTER);
        }
        HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
        updateDataAfterSubDataChanged(PsnJobVO.getDefaultTableName(), al.toArray(new String[0]));
        // 同步缓存
        HiCacheUtils.synCache(PsndocVO.getDefaultTableName(), PsnJobVO.getDefaultTableName(), PsnOrgVO.getDefaultTableName());
    }
    
    @Override
    public Map<String, PsnJobVO[]> queryJobHistroy(String pkPsndoc) throws BusinessException
    {
        PsnOrgVO[] orgs =
            getPsndocDAO().queryByCondition(PsnOrgVO.class, " pk_psndoc = '" + pkPsndoc + "' and indocflag = 'Y' order by orgrelaid desc ");
        if (orgs == null || orgs.length == 0)
        {
            return null;
        }
        String curSql = "";
        String hisSql = "";
        for (int i = 0; i < orgs.length; i++)
        {
            if (i == 0)
            {
                curSql += ",'" + orgs[i].getPk_psnorg() + "'";
            }
            else
            {
                hisSql += ",'" + orgs[i].getPk_psnorg() + "'";
            }
        }
        
        HashMap<String, PsnJobVO[]> map = new HashMap<String, PsnJobVO[]>();
        
        if (curSql.length() > 0)
        {
            PsnJobVO[] curJobs =
                getPsndocDAO().queryByCondition(PsnJobVO.class,
                    " pk_psnorg in ( " + curSql.substring(1) + " ) and ismainjob = 'Y' order by recordnum desc ");
            map.put("cur", curJobs);
        }
        
        if (hisSql.length() > 0)
        {
            PsnJobVO[] hisJobs =
                getPsndocDAO().queryByCondition(PsnJobVO.class,
                    " pk_psnorg in ( " + hisSql.substring(1) + " ) and ismainjob = 'Y' order by begindate ");
            map.put("his", hisJobs);
        }
        
        return map;
    }
    
    @Override
    public PsndocAggVO queryPsndocVOByPsnjobPk(String pk_psnjob) throws BusinessException
    {
        PsnJobVO job = getPsndocDAO().queryByPk(PsnJobVO.class, pk_psnjob);
        if (job == null)
        {
            return null;
        }
        PsnOrgVO org = getPsndocDAO().queryByPk(PsnOrgVO.class, job.getPk_psnorg());
        PsndocAggVO doc = getPsndocDAO().queryByPk(PsndocAggVO.class, job.getPk_psndoc(), true);
        doc.getParentVO().setPsnJobVO(job);
        doc.getParentVO().setPsnOrgVO(org);
        return doc;
    }
    
    @Override
    public String checkOrgPower(String strPk_org) throws BusinessException
    {
        if (StringUtils.isBlank(strPk_org))
        {
            return null;
        }
        
        // 组织不为空
        String powerSql =
            HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE, "org_orgs");
        powerSql = StringUtils.isBlank(powerSql) ? " 1 = 1 " : powerSql;
        int count =
            NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                .getCountByCondition("org_orgs", powerSql + " and org_orgs.pk_org = '" + strPk_org + "' ");
        if (count <= 0)
        {
            return null;
        }
        
        // 组织启用HR
        count =
            NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                .getCountByCondition("org_admin_enable", " pk_adminorg ='" + strPk_org + "' ");
        if (count <= 0)
        {
            return null;
        }
        
        return strPk_org;
    }
    
    @Override
    public String checkDeptPower(String strPK_dept) throws BusinessException
    {
        if (StringUtils.isBlank(strPK_dept))
        {
            return null;
        }
        
        // 检查是否是虚拟部门 ，如果是虚拟部门也返回空
        DeptVO dept = (DeptVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null, DeptVO.class, strPK_dept);
        if (dept != null && dept.getDepttype() != null && dept.getDepttype() == 1)
        {
            return null;
        }
        
        // 部门不为空
        String powerSql =
            HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, "org_dept");
        powerSql = StringUtils.isBlank(powerSql) ? " 1 = 1 " : powerSql;
        int count =
            NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                .getCountByCondition("org_dept", powerSql + " and org_dept.pk_dept = '" + strPK_dept + "' ");
        if (count <= 0)
        {
            return null;
        }
        return strPK_dept;
    }
    
    @Override
    public void isCreateUser(nc.vo.bd.psn.PsndocVO[] vos) throws BusinessException
    {
        if (vos == null || vos.length == 0)
        {
            throw new BusinessException(ResHelper.getString("10140psn", "010140psn0027")/* @res"您选择的人员已经关联用户！" */);
        }
        
        ArrayList<String> al = new ArrayList<String>();
        for (nc.vo.bd.psn.PsndocVO vo : vos)
        {
            if (!al.contains(vo.getPrimaryKey()))
            {
                al.add(vo.getPrimaryKey());
            }
        }
        
        InSQLCreator isc = new InSQLCreator();
        
        String insql = isc.getInSQL(al.toArray(new String[0]));
        String cond = " base_doc_type = 0 and pk_base_doc in  (" + insql + ") ";
        int count = NCLocator.getInstance().lookup(IPersistenceRetrieve.class).getCountByCondition("sm_user", cond);
        // if (count > 0)
        // {
        // throw new BusinessException(ResHelper.getString("10140psn", "010140psn0027")/* @res"您选择的人员已经关联用户！"
        // */);
        // }
        
    }
    
    @Override
    public List<String> queryUserDocPK(String[] array) throws BusinessException
    {
        InSQLCreator isc = new InSQLCreator();
        
        String insql = isc.getInSQL(array);
        String cond = " pk_base_doc in (" + insql + ") ";
        UserVO[] vos = (UserVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, UserVO.class, cond);
        
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; vos != null && i < vos.length; i++)
        {
            if (!al.contains(vos[i].getPk_base_doc()))
            {
                al.add(vos[i].getPk_base_doc());
            }
        }
        
        return al;
        
    }
    
    @Override
    public PsndocExtend[] createUser(nc.vo.bd.psn.PsndocVO[] doc, String pk_userGroup) throws BusinessException
    {
        if (doc == null)
        {
            return null;
        }
        ArrayList<nc.vo.bd.psn.PsndocVO> list = new ArrayList<nc.vo.bd.psn.PsndocVO>();
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; i < doc.length; i++)
        {
            if (!al.contains(doc[i].getPk_psndoc()))
            {
                al.add(doc[i].getPk_psndoc());
            }
        }
        
        List<String> pkList = this.queryUserDocPK(al.toArray(new String[0]));
        for (int i = 0; i < doc.length; i++)
        {
            if (!pkList.contains(doc[i].getPk_psndoc()))
            {
                list.add(doc[i]);
            }
        }
        PsndocExtend[] vos = null;
        // 捕获自助弹出的异常
        try
        {
            vos =
                NCLocator.getInstance().lookup(nc.itf.bd.psn.psndoc.IPsndocService.class)
                    .createUser(list.toArray(new nc.vo.bd.psn.PsndocVO[0]), pk_userGroup);
        }
        catch (Exception e)
        {
            if (e instanceof nc.vo.util.bizlock.BizLockFailedException)
            {
                throw new BusinessException("你操作的数据正被他人修改，请刷新数据！");
            }
            throw new BusinessException(e.getMessage());
        }
        return vos;
        
    }
    
    @Override
    public PsnJobVO[] queryPsnjobVOByOrgAndDate(String pk_org, String pk_psndoc, UFLiteralDate begin, UFLiteralDate end)
            throws BusinessException
    {
        String sql =
            " select a.* from hi_psnjob a inner join hi_psnorg b on a.pk_psnorg = b.pk_psnorg "
                + "where b.psntype = 0 and b.indocflag = 'Y' and b.pk_psndoc = '" + pk_psndoc + "' and a.pk_org = '" + pk_org
                + "' and a.begindate <= '" + end + "' and ( a.enddate >= '" + begin + "' or isnull( a.enddate , '~' ) = '~' ) ";
        
        List<PsnJobVO> list = (List<PsnJobVO>) new BaseDAO().executeQuery(sql, new BeanListProcessor(PsnJobVO.class));
        return list.toArray(new PsnJobVO[0]);
    }
    
    @Override
    public KeyPsnGrpVO insert(KeyPsnGrpVO vo) throws BusinessException
    {
        
        String code = vo.getGroup_code();
        
        int i =
            NCLocator
                .getInstance()
                .lookup(IPersistenceRetrieve.class)
                .getCountByCondition(KeyPsnGrpVO.getDefaultTableName(),
                    " group_code = '" + code + "' and pk_org = '" + vo.getPk_org() + "' ");
        if (i > 0)
        {
            throw new BusinessException(MessageFormat.format(ResHelper.getString("6007psn", "06007psn0366")/*
                                                                                                            * @res
                                                                                                            * "当前组织下已存在编码为[{0}]的关键人员组"
                                                                                                            */, code));
        }
        
        KeyPsnGrpVO grp = getPsndocDAO().insert(vo);
        CacheProxy.fireDataInserted(KeyPsnGrpVO.getDefaultTableName());
        HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_KEYPSNGRP);
        return grp;
    }
    
    @Override
    public KeyPsnGrpVO update(KeyPsnGrpVO vo) throws BusinessException
    {
        String code = vo.getGroup_code();
        int i =
            NCLocator
                .getInstance()
                .lookup(IPersistenceRetrieve.class)
                .getCountByCondition(
                    KeyPsnGrpVO.getDefaultTableName(),
                    " group_code = '" + code + "' and pk_org = '" + vo.getPk_org() + "' and pk_keypsn_group <> '" + vo.getPk_keypsn_group()
                        + "'");
        if (i > 0)
        {
            throw new BusinessException(MessageFormat.format(ResHelper.getString("6007psn", "06007psn0366")/*
                                                                                                            * @res
                                                                                                            * "当前组织下已存在编码为[{0}]的关键人员组"
                                                                                                            */, code));
        }
        
        KeyPsnGrpVO grp = getPsndocDAO().update(vo, true);
        CacheProxy.fireDataUpdated(KeyPsnGrpVO.getDefaultTableName());
        HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_KEYPSNGRP);
        return grp;
    }
    
    @Override
    public KeyPsnGrpVO enable(KeyPsnGrpVO obj) throws BusinessException
    {
        KeyPsnGrpVO grp = getPsndocDAO().unSeal(obj, true);
        CacheProxy.fireDataUpdated(KeyPsnGrpVO.getDefaultTableName());
        HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_KEYPSNGRP);
        return grp;
    }
    
    @Override
    public KeyPsnGrpVO disable(KeyPsnGrpVO obj) throws BusinessException
    {
        // 人员是否结束校验
        int i =
            NCLocator
                .getInstance()
                .lookup(IPersistenceRetrieve.class)
                .getCountByCondition(KeyPsnVO.getDefaultTableName(),
                    " pk_keypsn_grp ='" + obj.getPk_keypsn_group() + "' and (endflag ='N' or isnull(endflag ,'~') = '~') ");
        
        if (i > 0)
        {
            throw new BusinessException(ResHelper.getString("6007psn", "06007psn0367")/*
                                                                                       * @res
                                                                                       * "当前关键人员组中存在未结束的关键人员,不能停用."
                                                                                       */);
        }
        
        KeyPsnGrpVO grp = getPsndocDAO().seal(obj, true);
        CacheProxy.fireDataUpdated(KeyPsnGrpVO.getDefaultTableName());
        HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_KEYPSNGRP);
        return grp;
    }
    
    @Override
    public PsndocAggVO[] saveKeyPsn(String[] refPKs, KeyPsnGrpVO keyPsnGrpVO) throws BusinessException
    {
        if (refPKs == null || refPKs.length == 0)
        {
            return null;
        }
        
        InSQLCreator isc = new InSQLCreator();
        
        String inSql = isc.getInSQL(refPKs);
        
        PsnJobVO[] jobVOs =
            (PsnJobVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                .retrieveByClause(null, PsnJobVO.class, " pk_psnjob in (" + inSql + ") ");
        KeyPsnVO[] keyVOs = new KeyPsnVO[jobVOs.length];
        for (int i = 0; i < jobVOs.length; i++)
        {
            keyVOs[i] = new KeyPsnVO();
            keyVOs[i].setBegindate(PubEnv.getServerLiteralDate());
            keyVOs[i].setEndflag(UFBoolean.FALSE);
            keyVOs[i].setPk_keypsn_grp(keyPsnGrpVO.getPk_keypsn_group());
            keyVOs[i].setPk_group(PubEnv.getPk_group());
            keyVOs[i].setPk_org(keyPsnGrpVO.getPk_org());
            keyVOs[i].setPk_psndoc(jobVOs[i].getPk_psndoc());
            keyVOs[i].setPk_psnorg(jobVOs[i].getPk_psnorg());
            keyVOs[i].setLastflag(UFBoolean.TRUE);
            keyVOs[i].setRecordnum(0);
            keyVOs[i].setCreator(PubEnv.getPk_user());
            keyVOs[i].setCreationtime(PubEnv.getServerTime());
        }
        NCLocator.getInstance().lookup(IPersistenceUpdate.class).insertVOArray(null, keyVOs, null);
        
        return queryPsndocVOByPks(refPKs);
        
    }
    
    @Override
    public PsndocAggVO[] saveKeyPsnByCondition(String wherePart, KeyPsnGrpVO keyPsnGrpVO) throws BusinessException
    {
        String sql =
            " select hi_psnjob.* from bd_psndoc inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc "
                + "inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg  where " + wherePart;
        List<PsnJobVO> jobVOs =
            (List<PsnJobVO>) NCLocator.getInstance().lookup(IPersistenceHome.class)
                .executeQuery(sql, new BeanListProcessor(PsnJobVO.class));
        
        if (jobVOs == null || jobVOs.size() == 0)
        {
            return null;
        }
        
        KeyPsnVO[] keyVOs = new KeyPsnVO[jobVOs.size()];
        ArrayList<String> al = new ArrayList<String>();
        for (int i = 0; i < jobVOs.size(); i++)
        {
            keyVOs[i] = new KeyPsnVO();
            keyVOs[i].setBegindate(PubEnv.getServerLiteralDate());
            keyVOs[i].setEndflag(UFBoolean.FALSE);
            keyVOs[i].setPk_keypsn_grp(keyPsnGrpVO.getPk_keypsn_group());
            keyVOs[i].setPk_group(PubEnv.getPk_group());
            keyVOs[i].setPk_org(keyPsnGrpVO.getPk_org());
            keyVOs[i].setPk_psndoc(jobVOs.get(i).getPk_psndoc());
            keyVOs[i].setPk_psnorg(jobVOs.get(i).getPk_psnorg());
            keyVOs[i].setLastflag(UFBoolean.TRUE);
            keyVOs[i].setRecordnum(0);
            keyVOs[i].setCreator(PubEnv.getPk_user());
            keyVOs[i].setCreationtime(PubEnv.getServerTime());
            al.add(jobVOs.get(i).getPk_psnjob());
        }
        NCLocator.getInstance().lookup(IPersistenceUpdate.class).insertVOArray(null, keyVOs, null);
        
        return queryPsndocVOByPks(al.toArray(new String[0]));
    }
    
    @Override
    public void deleteKeyPsn(PsndocVO[] psndocVOs, String[] pks, String pk_keypsn_group) throws BusinessException
    {
        // 删除关键人员是校验所选人员的ts,删除完后更新所选人员的ts
        BDVersionValidationUtil.validateSuperVO(psndocVOs);
        
        String sql = " delete from hi_psndoc_keypsn  where pk_psnorg = ? and pk_keypsn_grp = ? ";
        SQLParameter[] parameters = new SQLParameter[pks.length];
        String[] sqls = new String[pks.length];
        for (int i = 0; i < pks.length; i++)
        {
            sqls[i] = sql;
            parameters[i] = new SQLParameter();
            parameters[i].addParam(pks[i]);
            parameters[i].addParam(pk_keypsn_group);
        }
        
        NCLocator.getInstance().lookup(IPersistenceUpdate.class).executeSQLs(sqls, parameters);
        NCLocator.getInstance().lookup(IPersistenceUpdate.class).updateVOArray(null, psndocVOs, null, null);
        HiCacheUtils.synCache(PsndocVO.getDefaultTableName());
    }
    
    @Override
    public Object[] stopKeyPsn(Object[] operateDates, String pk_keypsn_grp, UFLiteralDate stopdate) throws BusinessException
    {
        
        ArrayList<String> al = new ArrayList<String>();
        IPersistenceRetrieve queryService = NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
        IPersistenceUpdate updateService = NCLocator.getInstance().lookup(IPersistenceUpdate.class);
        for (int i = 0; i < operateDates.length; i++)
        {
            String pk_psnorg = ((PsndocAggVO) operateDates[i]).getParentVO().getPsnOrgVO().getPk_psnorg();
            String whereSql = " endflag <> 'Y' and pk_psnorg ='" + pk_psnorg + "' and pk_keypsn_grp ='" + pk_keypsn_grp + "' ";
            KeyPsnVO[] keyVOs = (KeyPsnVO[]) queryService.retrieveByClause(null, KeyPsnVO.class, whereSql);
            if (keyVOs == null || keyVOs.length == 0)
            {
                continue;
            }
            
            // 存在未结束的关键人员记录
            // 检查结束日期是否晚于开始日期
            for (int j = 0; j < keyVOs.length; j++)
            {
                if (keyVOs[j].getBegindate() != null && keyVOs[j].getBegindate().afterDate(stopdate))
                {
                    throw new BusinessException(ResHelper.getString("6007psn", "06007psn0368")/*
                                                                                               * @res
                                                                                               * "关键人员结束日期不能早于开始日期,请重新选择"
                                                                                               */);
                }
                
                keyVOs[j].setEndflag(UFBoolean.TRUE);
                keyVOs[j].setEnddate(stopdate);
                keyVOs[j].setModifier(PubEnv.getPk_user());
                keyVOs[j].setModifiedtime(PubEnv.getServerTime());
                keyVOs[j].setStatus(VOStatus.UPDATED);
            }
            updateService.updateVOArray(null, keyVOs, null, null);
            al.add(pk_psnorg);
        }
        
        return al.toArray();
    }
    
    @Override
    public void deleteKeyPsnGroup(KeyPsnGrpVO group) throws BusinessException
    {
        new SimpleDocServiceTemplate("KeyPsnGrpVO").delete(group);
        CacheProxy.fireDataDeleted(KeyPsnGrpVO.getDefaultTableName(), group.getPk_keypsn_group());
        HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_KEYPSNGRP);
    }
    
    @Override
    public PsndocAggVO queryPsndocByNameID(String name, String idtype, String id) throws BusinessException
    {
        
        PsndocVO doc = new PsndocVO();
        doc.setName(name);
        doc.setIdtype(idtype);
        doc.setId(id);
        
        PsndocAggVO agg = getPsndocDAO().getPsndocVOByUniqueVO(doc);
        if (agg == null)
        {
            return null;
        }
        
        PsndocAggVO all = new SimpleDocServiceTemplate("PsndocAggVO").queryByPk(PsndocAggVO.class, agg.getParentVO().getPk_psndoc());
        all.setParentVO(agg.getParentVO());
        return all;
    }
    
    @Override
    public void saveKeyPsn4MS(String groupPK, String hrorgPK, String... psnjobPKs) throws BusinessException
    {
        if (psnjobPKs == null || psnjobPKs.length == 0)
        {
            return;
        }
        
        InSQLCreator isc = new InSQLCreator();
        PsnJobVO[] jobVOs =
            (PsnJobVO[]) NCLocator
                .getInstance()
                .lookup(IPersistenceRetrieve.class)
                .retrieveByClause(
                    null,
                    PsnJobVO.class,
                    " pk_psnjob in (" + isc.getInSQL(psnjobPKs)
                        + ") and pk_psnorg not in ( select pk_psnorg from hi_psndoc_keypsn where pk_keypsn_grp = '" + groupPK
                        + "' and endflag ='N' ) ");
        KeyPsnVO[] keyVOs = new KeyPsnVO[jobVOs.length];
        for (int i = 0; i < jobVOs.length; i++)
        {
            keyVOs[i] = new KeyPsnVO();
            keyVOs[i].setBegindate(PubEnv.getServerLiteralDate());
            keyVOs[i].setEndflag(UFBoolean.FALSE);
            keyVOs[i].setPk_keypsn_grp(groupPK);
            keyVOs[i].setPk_group(PubEnv.getPk_group());
            keyVOs[i].setPk_org(hrorgPK);
            keyVOs[i].setPk_psndoc(jobVOs[i].getPk_psndoc());
            keyVOs[i].setPk_psnorg(jobVOs[i].getPk_psnorg());
            keyVOs[i].setLastflag(UFBoolean.TRUE);
            keyVOs[i].setRecordnum(0);
            keyVOs[i].setCreator(PubEnv.getPk_user());
            keyVOs[i].setCreationtime(PubEnv.getServerTime());
        }
        NCLocator.getInstance().lookup(IPersistenceUpdate.class).insertVOArray(null, keyVOs, null);
        
    }
    
    @Override
    public boolean isMustUploadAttachment(String billtype, String pkUser, String pkGroup) throws BusinessException
    {
        String strBillType = null;
        String nodeKey = null;
        if (HICommonValue.BillTYPE_REG.equals(billtype))
        {
            strBillType = "60090regapply";
            nodeKey = "6009reg";
        }
        else if (HICommonValue.BillTYPE_TRANS.equals(billtype))
        {
            strBillType = "60090transapply";
            nodeKey = "BJ";
        }
        else if (HICommonValue.BillTYPE_DIMISSION.equals(billtype))
        {
            strBillType = "60090dimissionapply";
            nodeKey = "BK";
        }
        
        BillOperaterEnvVO envvo = new BillOperaterEnvVO();
        envvo.setCorp(pkGroup);
        envvo.setBilltype(strBillType);
        envvo.setBusitype(null);
        envvo.setNodekey(nodeKey);
        envvo.setOperator(pkUser);
        envvo.setOrgtype(null);
        envvo.setBilltemplateid(null);
        BillTempletVO vo = NCLocator.getInstance().lookup(IBillTemplateQry.class).findBillTempletData(envvo);
        if (vo == null || vo.getBodyVO() == null || vo.getBodyVO().length == 0)
        {
            return false;
        }
        for (BillTempletBodyVO bvo : vo.getBodyVO())
        {
            if (bvo.getItemkey().equals("isneedfile"))
            {
                return new Boolean(bvo.getDefaultvalue());
            }
        }
        return false;
    }
    
    @Override
    public Object[] queryKeyPsnVOByPks(String[] pks, Object treeObj) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByPks(true, pks, treeObj);
    }
    
    @Override
    public byte[] mergeTemplate(String pk_psnjob, String pk_rpt_def) throws BusinessException
    {
        RepDefVO repDefVO = NCLocator.getInstance().lookup(IRptQueryService.class).queryByPk(pk_rpt_def);
        byte[] content = (byte[]) repDefVO.getObj_rpt_def();
        String id = OidGenerator.getInstance().nextOid();
        File docFile = new File(id + ".docx");
        String id2 = OidGenerator.getInstance().nextOid();
        File output = new File(id2 + ".docx");
        try
        {
            FileUtils.writeByteArrayToFile(docFile, content);
            NCHRMergeEngine engine = new NCHRMergeEngine(docFile);
            HIMergeFilter filter = new HIMergeFilter();
            filter.setPk_psnjob(pk_psnjob);
            engine.generate(filter, output);
            return FileUtils.readFileToByteArray(output);
        }
        catch (Exception e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
        finally
        {
            docFile.delete();
            output.delete();
        }
    }
    
    @Override
    public HashMap<String, byte[]> batchMergeTemplate(String[] jobPKs, String pk_rpt_def) throws BusinessException
    {
        HashMap<String, byte[]> map = new HashMap<String, byte[]>();
        for (int i = 0; jobPKs != null && i < jobPKs.length; i++)
        {
            byte[] data = this.mergeTemplate(jobPKs[i], pk_rpt_def);
            map.put(jobPKs[i], data);
        }
        return map;
    }
    
    @Override
    public void checkStopKeyPsn(String[] pk_psnorg, String pk_keypsn_grp) throws BusinessException
    {
        InSQLCreator isc = new InSQLCreator();
        String condition = " endflag <> 'Y' and pk_psnorg in (" + isc.getInSQL(pk_psnorg) + ") and pk_keypsn_grp ='" + pk_keypsn_grp + "' ";
        
        int count =
            NCLocator.getInstance().lookup(IPersistenceRetrieve.class).getCountByCondition(KeyPsnVO.getDefaultTableName(), condition);
        if (count == 0)
        {
            // 所选的人员都是历史关键人员不需要进行终止操作
            throw new BusinessException(ResHelper.getString("6007psn", "06007psn0422")/*
                                                                                       * @res
                                                                                       * "当前选择的人员都是历史关键人员,不需要进行终止操作,请选择未终止的关键人员."
                                                                                       */);
        }
        
    }
    
    @Override
    public PsnJobVO[] queryPsnjobByPKs(String[] pks) throws BusinessException
    {
        InSQLCreator isc = new InSQLCreator();
        
        String insql = isc.getInSQL(pks);
        return queryPsnJobs(" pk_psnjob in ( " + insql + " )");
        
    }
    
    /**
     * 新增时校验子集非空
     * @pare PsndocAggVO
     * @pare HashMap 表名->表显示名称
     * @return 空子集显示名称
     * @author yangshuo
     * @Created on 2012-12-7 下午01:14:54
     */
    @Override
    public String[] queryNotNullSubset(PsndocAggVO psndocAggVO, HashMap<String, String> showTab) throws BusinessException
    {
        return getPsndocDAO().queryNotNullSubset(psndocAggVO, showTab);
    }
    
    /**
     * 修改时校验子集非空
     * @pare PsndocAggVO
     * @pare HashMap 表名->表显示名称
     * @return 空子集显示名称
     * @author yangshuo
     * @Created on 2012-12-7 下午01:14:54
     */
    @Override
    public String[] validateSubNotNull(PsndocAggVO psndocAggVO, HashMap<String, String> showTab) throws BusinessException
    {
        return getPsndocDAO().validateSubNotNull(psndocAggVO, showTab);
    }
    
    /**
     * 查询人员信息集，服务于员工信息维护/员工信息查询【导出】按钮 （6.3增加） autor:yunana
     */
    @SuppressWarnings("rawtypes")
	@Override
    public HashMap<String, GeneralVO[]> queryPsnInfo(String[] psndocPKs, String[] infosetPKs, int queryMode, String pk_org,
            HashMap<String, ArrayList<String>> tNameVScolumsMap) throws BusinessException
    {
        
        // 通过infosetPK得到其对应的元数据
        ArrayList infoList = this.getInfosetTCodebyPKs(infosetPKs);
        // 转换参照类型map
        Map<String, RefSetFieldParameter> reftypeMap = getReftype(infosetPKs);
        // 转换枚举类型map
        Map<String, FldreftypeVO> enumTypeMap = getEnumType(infosetPKs);
        // 以下两行代码用以得到要显示的列名，并且将每个列名与其对应的元数据储存起来
        HashMap<String, String> columNameVSmetadataproperty = new HashMap<String, String>();
        // HashMap<String, ArrayList<String>> tableItemMap = getListShowFields(metaDatas,
        // columNameVSmetadataproperty, tNameVScolumsMap);
        // 得到需要导出的VOs(没有通过人员类别进行字段属性值过滤)
        HashMap<String, GeneralVO[]> generalVOsMap =
            this.getChildVOs(infoList, psndocPKs, tNameVScolumsMap, reftypeMap, enumTypeMap, queryMode);
        // 通过人员类别进行字段属性值过滤
        this.filterByPsnclrule(generalVOsMap, pk_org, psndocPKs, columNameVSmetadataproperty);
        return generalVOsMap;
    }
    
    /**
     * 查找引用类型的展示名称
     * @param info
     * @return Map<String, RefSetFieldParameter> K：表名.字段名 V：RefSetFieldParameter
     * @throws BusinessException
     */
    private Map<String, RefSetFieldParameter> getReftype(String[] infosetPKs) throws BusinessException
    {
        Map<String, RefSetFieldParameter> map = new HashMap<String, RefSetFieldParameter>();
        InSQLCreator isc = new InSQLCreator();
        String inSql = isc.getInSQL(infosetPKs);
        String sql =
            "select infoset_code, item_code, ref_model_name "
                + "from hr_infoset_item inner join hr_infoset on hr_infoset.pk_infoset = hr_infoset_item.pk_infoset "
                + "where hr_infoset.pk_infoset in (" + inSql + ") and data_type = 5";// and hided = 'N'";
        ArrayList<HashMap<String, String>> infoList =
            (ArrayList<HashMap<String, String>>) NCLocator.getInstance().lookup(IPersistenceHome.class)
                .executeQuery(sql, new MapListProcessor());
        if (infoList != null && !infoList.isEmpty())
        {
            for (HashMap<String, String> hashMap : infoList)
            {
                // 组织关系不会输出在Excel表中，所以不需处理
                if ("pk_psnorg".equals(hashMap.get("item_code")))
                {
                    continue;
                }
                RefSetFieldParameter para =
                    RefDataTypeHelper.getRefSetFieldName(new String[]{hashMap.get("ref_model_name")}).values()
                        .toArray(new RefSetFieldParameter[0])[0];
                
                // 将表名和字段名做成一个解析类;
                // 兼职记录进行特殊处理，兼职记录与工作记录是同一张表不同元数据
                if (hashMap.get("infoset_code").equals("hi_psndoc_parttime"))
                {
                    map.put("hi_psnjob" + "." + hashMap.get("item_code"), para);
                    continue;
                }
                map.put(hashMap.get("infoset_code") + "." + hashMap.get("item_code"), para);
            }
        }
        return map;
    }
    
    /**
     * 查找枚举类型的展示名称
     * @param infosetPKs
     * @return Map<String,String> K：表名.字段名 V：enum_id
     */
    private Map<String, FldreftypeVO> getEnumType(String[] infosetPKs) throws BusinessException
    {
        Map<String, FldreftypeVO> fldMap = new HashMap<String, FldreftypeVO>();
        // Map<String, String> map = new HashMap<String, String>();
        InSQLCreator isc = new InSQLCreator();
        String inSql = isc.getInSQL(infosetPKs);
        String strCondition = "pk_infoset in (" + inSql + "  ) ";
        InfoSetVO[] infoSetVOs = NCLocator.getInstance().lookup(IInfoSetQry.class).queryInfoSet(null, strCondition);
        fldMap = RefDataTypeHelper.getComboboxDataTypeMap(infoSetVOs);
        // 如果该字段为枚举类型（data_type=6）则放入map中
        // String sql = "select infoset_code, item_code, enum_id "
        // +
        // "from hr_infoset_item inner join hr_infoset on hr_infoset.pk_infoset = hr_infoset_item.pk_infoset "
        // + "where hr_infoset.pk_infoset in (" + inSql
        // + ") and data_type = 6";
        // ArrayList<HashMap<String, String>> infoList = (ArrayList<HashMap<String, String>>) NCLocator
        // .getInstance().lookup(IPersistenceHome.class)
        // .executeQuery(sql, new MapListProcessor());
        // if (infoList != null && !infoList.isEmpty()) {
        // for (HashMap<String, String> hashMap : infoList) {
        // map.put(hashMap.get("infoset_code") + "."
        // + hashMap.get("item_code"), hashMap.get("enum_id"));
        // }
        // }
        return fldMap;
    }
    
    /**
     * 查找每个组织下的每种人员类别所对应的PsnclinfosetVO 并将得到的若干VO(V)与其持有的元数据属性（K）做成map
     * @param pk_org
     * @param pk_psncl
     * @return
     * @throws BusinessException
     */
    private HashMap<String, PsnclinfosetVO> getMetaVSPsnclinfosetVO(String pk_org, String pk_psncl) throws BusinessException
    {
        HashMap<String, PsnclinfosetVO> getConfigMap = new HashMap<String, PsnclinfosetVO>();
        // String strCondition = " pk_psnclrule in (select pk_psnclrule from hr_psnclrule where pk_org = '" +
        // pk_org + "' and pk_psncl = '"
        // + pk_psncl + "') ";
        
        PsnclruleVO resultVO = NCLocator.getInstance().lookup(IPsnclruleQueryService.class).queryPsnclConfig(pk_psncl, pk_org);
        
        PsnclinfosetVO[] psnclinfosetVOs = resultVO.getInfosets();
        // PsnclinfosetVO[] psnclinfosetVOs = (PsnclinfosetVO[]) NCLocator.getInstance()
        // .lookup(IPersistenceRetrieve.class)
        // .retrieveByClause(null, PsnclinfosetVO.class, strCondition);
        if (psnclinfosetVOs == null || psnclinfosetVOs.length == 0)
        {
            return null;
        }
        for (int i = 0; i < psnclinfosetVOs.length; i++)
        {
            getConfigMap.put(psnclinfosetVOs[i].getMetadata(), psnclinfosetVOs[i]);
        }
        return getConfigMap;
    }
    
    /**
     * 通过人员类别过滤不需要显示的字段，将不需要显示的字段的显示值设置为空 为了避免在循环中产生不确定的数据库连接数，下个方法用了若干的Map来代替。
     * 设计逻辑：
     * 通过pk_psndoc找到pk_psncl，通过pk_psncl找到HashMap<String,PsnclinfosetVO>>（String代表元数据metadataproperty）
     * 通过metadataproperty找到对应的PsnclinfosetVO，然后检查PsnclinfosetVO中是否对相应metadataproperty进行了设置
     * @throws BusinessException
     */
    private void filterByPsnclrule(HashMap<String, GeneralVO[]> generalVOsMap, String pk_org, String[] psndocPKs,
            HashMap<String, String> columNameVSmetadataproperty) throws BusinessException
    {
        InSQLCreator isc = new InSQLCreator();
        String inSql = isc.getInSQL(psndocPKs);
        // 查询当前人员的所有人员类别
        String sql = "select distinct pk_psncl from hi_psnjob where pk_psndoc in (" + inSql + ") and lastflag = 'Y'";
        ArrayList<HashMap<String, String>> psnclruleList =
            (ArrayList<HashMap<String, String>>) NCLocator.getInstance().lookup(IPersistenceHome.class)
                .executeQuery(sql, new MapListProcessor());
        // 将每种人员类别和其对应的HashMap<String, PsnclinfosetVO>做成一个map，方便以后使用
        HashMap<String, HashMap<String, PsnclinfosetVO>> pkpsnclMdataInfovoMap = new HashMap<String, HashMap<String, PsnclinfosetVO>>();
        if (psnclruleList != null && !psnclruleList.isEmpty())
        {
            // 此循环中存在数据库连接，难以避免。但是这个数据库连接数很小。用于查询人员类别
            for (int i = 0; i < psnclruleList.size(); i++)
            {
                HashMap<String, String> pkpsnclValueMap = psnclruleList.get(i);
                String pk_psncl = pkpsnclValueMap.get("pk_psncl");
                // 与数据库交互语句,K:metadata V:PsnclinfosetVO;
                // 每个组织下的每种人员类别都对应若干PsnclinfosetVO，控制着若干字段，所以下面这段代码的用意在于：
                // 通过当前人员的pk_org和pk_psncl找对应的PsnclinfosetVO（若干），然后确定被PsnclinfosetVO控制的字段（若干）。(后续会通过pk_psndoc来确定pk_psncl)
                HashMap<String, PsnclinfosetVO> getConfigMap = this.getMetaVSPsnclinfosetVO(pk_org, pk_psncl);
                pkpsnclMdataInfovoMap.put(pk_psncl, getConfigMap);
            }
        }
        else
        {
            return;
        }
        
        // 将每个pk_psndoc和其对应的psncl做成一个map，方便以后查询
        String sql2 = "select pk_psndoc,pk_psncl from hi_psnjob where pk_psndoc in (" + inSql + ") and lastflag = 'Y'";
        ArrayList<HashMap<String, String>> psndocPkList =
            (ArrayList<HashMap<String, String>>) NCLocator.getInstance().lookup(IPersistenceHome.class)
                .executeQuery(sql2, new MapListProcessor());
        HashMap<String, String> psndocPkVSpsnclPkMap = new HashMap<String, String>();
        if (psndocPkList != null && !psndocPkList.isEmpty())
        {
            for (int i = 0; i < psndocPkList.size(); i++)
            {
                HashMap map = psndocPkList.get(i);
                String pk_psndoc = (String) map.get("pk_psndoc");
                String pk_psncl = (String) map.get("pk_psncl");
                psndocPkVSpsnclPkMap.put(pk_psndoc, pk_psncl);
            }
        }
        else
        {
            return;
        }
        
        // 开始过滤掉在Excel表中不显示值的字段的值,开始遍历所有要输出在Excel表上的VO,首先遍历generalVOsMap
        Iterator iter = generalVOsMap.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            String metaData = (String) entry.getKey();
            GeneralVO[] genvos = (GeneralVO[]) entry.getValue();
            // 因为每个人的人员类别都不同，因此以pk_psndoc为索引，为每个VO进行过滤，首先得到人员主键
            if (genvos == null)
            {
                continue;
            }
            // 然后遍历每个map中的GeneralVO[]
            for (int k = 0; k < genvos.length; k++)
            {
                String pk_psndoc = (String) genvos[k].getAttributeValue("pk_psndoc");
                // 得到每个人的人员类别，
                String pk_psncl = psndocPkVSpsnclPkMap.get(pk_psndoc);
                // 得到每种人员类别对应的 【k:元数据 v:PsnclinfosetVO】map
                HashMap<String, PsnclinfosetVO> getConfigMap = pkpsnclMdataInfovoMap.get(pk_psncl);
                // 没有取到则不用通过人员类别过滤
                if (getConfigMap == null || getConfigMap.isEmpty())
                {
                    continue;
                }
                // 每个VO所持有的属性数目和名称都有可能不同，因此无法进行批量处理
                String[] columNames = genvos[k].getAttributeNames();
                // 最后遍历每个VO的字段名数组
                for (int i = 0; i < columNames.length; i++)
                {
                    
                    // 因为pk_psndoc并未给它储存元数据属性，而且pk_psndoc未来也不会显示在Excel表中，因此不需要对pk_psndoc进行过滤
                    if (columNames[i].equals("pk_psndoc"))
                    {
                        continue;
                    }
                    // 得到每个列名对应的元数据属性
                    String metadataproperty = columNameVSmetadataproperty.get(metaData.split("\\.")[1] + "." + columNames[i]);
                    // 通过元数据属性得到对应PsnclinfosetVO
                    PsnclinfosetVO configVO = getConfigMap.get(metadataproperty);
                    if (configVO == null)
                    {
                        continue;
                    }
                    // 如果userflag为false,则将其属性值设置为NULL
                    if (configVO.getUsedflag() == null || !configVO.getUsedflag().booleanValue())
                    {
                        genvos[k].setAttributeValue(columNames[i], null);
                    }
                }
            }
        }
    }
    
    /**
     * 得到需要导出的VO,因为要查询若干个表，每个表又有若干条数据，因此得到结果是这种形式：HashMap<String,GeneralVO[]>
     * K:元数据属性 V：对应VO
     * @param metaDatas 每张表对应的元数据，来源自hr_infoset
     * @param psndocPKs 人员主键
     * @param tableItemMap K：元数据属性 V：字段集合 ; 每张表可以被导出的属性
     * @param refMap 引用类型字段和其解析类
     * @param enumMap 枚举类型字段和其解析类
     * @param queryMode 导出范围 1：最新/有效记录；2：全部
     * @return HashMap<String,GeneralVO[]> K:元数据属性 V：对应VO
     * @throws BusinessException
     */
	private HashMap<String, GeneralVO[]> getChildVOs( ArrayList infoList, String[] psndocPKs,
            HashMap<String, ArrayList<String>> tableItemMap, Map<String, RefSetFieldParameter> refMap, Map<String, FldreftypeVO> enumMap,
            int queryMode) throws BusinessException
    {
    	String[] metaDatas = (String[]) infoList.get(0);
    	HashMap<String,String> sbMap = (HashMap<String, String>) infoList.get(1);
        // K:元数据属性 V：对应VO 用于返回
        HashMap<String, GeneralVO[]> tNameVsVOsMap = new HashMap<String, GeneralVO[]>();
        String[] tableCodes = new String[metaDatas.length];
        // 在循环中与数据库连接，但连接数永远会很小，tableCodes代表要导出的信息集
        for (int i = 0; i < metaDatas.length; i++)
        {
            tableCodes[i] = metaDatas[i].split("\\.")[1];
         
            ArrayList<String> fieldList = tableItemMap.get(metaDatas[i]);
            // 得到要查询的列名，每张表要查询的列名都不同
            List<String> list = combineFields(tableCodes[i], fieldList, refMap);
            String fields = list.get(0);// 可以被导出的字段
            InSQLCreator isc = new InSQLCreator();
            String inSql = isc.getInSQL(psndocPKs);
            String joinSql = list.get(1);// 连接其他表语句，用于翻译引用类型
            String lastFlagCondition = " ";
            int last = 1;// 最新记录
            if (queryMode == last)
            {
                if (tableCodes[i] != null && tableCodes[i].equals("hi_psndoc_parttime"))
                {
                    lastFlagCondition = " and hi_psnjob.lastflag = 'Y'";
                }
                else
                {
                    lastFlagCondition = " and " + tableCodes[i] + ".lastflag = 'Y'";
                }
            }
            String sql;
            // bd_psndoc本身具有人员编码和姓名属性，不必像其它表一样去连接bd_psndoc
            if (!tableCodes[i].equals("bd_psndoc"))
            {
                // 兼职记录和工作记录用的是同一张表hi_psnjob，但是元数据属性不相同，在这里加以区分
                String ismainjobCondition = "";
                if (metaDatas[i].equals("hrhi.hi_psndoc_parttime"))
                {
                    tableCodes[i] = "hi_psnjob";
                    ismainjobCondition = " and " + tableCodes[i] + ".ismainjob = 'N' ";
                }
                else if (metaDatas[i].equals("hrhi.hi_psnjob"))
                {
                    ismainjobCondition = " and " + tableCodes[i] + ".ismainjob = 'Y' ";
                }
                sql =
                    "select " + tableCodes[i] + "." + fields + " from " + tableCodes[i] + " inner join bd_psndoc on bd_psndoc.pk_psndoc="
                        + tableCodes[i] + ".pk_psndoc " + joinSql + "where " + tableCodes[i] + ".pk_psndoc in (" + inSql + ")"
                        + lastFlagCondition + ismainjobCondition;
            }
            else
            {
                sql =
                    "select " + tableCodes[i] + "." + fields + " from " + tableCodes[i] + " " + joinSql + " where " + tableCodes[i]
                        + ".pk_psndoc in (" + inSql + ")";
            }
            // 按人员编码排序，使导出结果看起来整齐些
            sql += " order by bd_psndoc.code ";
            // 如果日期概念明显，则用开始日期排序
            if (fields.contains("begindate"))
            {
                String orderBeginDate = "," + tableCodes[i] + ".begindate";
                sql += orderBeginDate;
            }
            
            if(!sbMap.isEmpty()){
            	String str = sbMap.get(tableCodes[i]);
            	if(!StringUtils.isEmpty(str)){            		
            		sql = sql.replaceAll(tableCodes[i], str);
            	}
            }
            
            
            GeneralVO[] vos =
                (GeneralVO[]) NCLocator.getInstance().lookup(IPersistenceHome.class)
                    .executeQuery(sql, new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
            
            if (tableCodes[i].equals("bd_psndoc"))
            {
                translateAddr(vos);
            }
            
            // 当未查询到结果时，为了保留VO的是属性名称，手动赋给其一行空值
            if (vos == null || vos.length < 0)
            {
                vos = new GeneralVO[1];
                vos[0] = new GeneralVO();
                vos[0].setAttributeValue("pk_psndoc", null);
                vos[0].setAttributeValue("bd_psndoc.code", null);
                vos[0].setAttributeValue("bd_psndoc.name", null);
                for (int k = 0; k < fieldList.size(); k++)
                {
                    vos[0].setAttributeValue(fieldList.get(k), null);
                }
            }
            
            // 翻译枚举类型字段的值
            for (String string : fieldList)
            {
                if (enumMap.get(string) != null)
                {
                    FldreftypeVO vo = enumMap.get(string);
                    IType dataType = vo.getDataType();
                    for (GeneralVO generalVO : vos)
                    {
                        String[] str = string.split("\\.");
                        IConstEnum constEnum = ((EnumType) dataType).getConstEnum(generalVO.getAttributeValue(str[1]));
                        if (constEnum == null)
                        {
                            continue;
                        }
                        String value = constEnum.getName();
                        generalVO.setAttributeValue(str[1], value);
                    }
                }
            }
            tNameVsVOsMap.put(metaDatas[i], vos);
        }
        return tNameVsVOsMap;
    }
    
    private void translateAddr(GeneralVO[] vos) throws BusinessException
    {
        for (int i = 0; i < vos.length; i++)
        {
            GeneralVO vo = vos[i];
            String pkValue = (String) vo.getAttributeValue("addr");
            if (StringUtils.isEmpty(pkValue))
            {
                continue;
            }
            String addr = getAddress(pkValue);
            vo.setAttributeValue("addr", addr);
        }
    }
    
    private String getAddress(String pkValue) throws BusinessException
    {
        // 处理地址簿
        AddressFormatVO[] formatVOs = NCLocator.getInstance().lookup(IAddressService_C.class).format(new String[]{pkValue});
        if (ArrayUtils.isEmpty(formatVOs))
        {
            return null;
        }
        return AddressFormater.format(new AddressFormatVO[]{formatVOs[0]})[0];
    }
    
    /**
     * 组合sql语句要查询的列名
     * @param fieldList
     * @return
     */
    private List<String> combineFields(String tablecode, ArrayList<String> fieldList, Map<String, RefSetFieldParameter> refMap)
    {
        List<String> list = new ArrayList<String>();
        StringBuffer fields = new StringBuffer();
        StringBuffer joinSql = new StringBuffer();
        // 添加人员编码和姓名属性
        if (!tablecode.equals("bd_psndoc"))
        {
            fields.append("pk_psndoc,bd_psndoc.code, bd_psndoc.name");
        }
        else
        {
            // bd_psndoc本身具有人员编码和姓名属性，不用再额外加code和name
            fields.append("pk_psndoc");
        }
        int refIndex = 0; // 连接引用类型数据所属表的别名
        // int enumIndex = 0;// 连接枚举类型数据所属表的别名
        for (Iterator iterator = fieldList.iterator(); iterator.hasNext();)
        {
            String field = (String) iterator.next();
            if (field.split("\\.")[0].equals("hi_psndoc_parttime"))
            {
                field = field.replace("hi_psndoc_parttime", "hi_psnjob");
            }
            // 翻译引用类型的字段
            RefSetFieldParameter para = refMap.get(field);
            if (para != null)
            {
                String tableName = para.getTableName();
                String pkFieldName = para.getPkFieldName();
                String displayFieldName = para.getDisplayFieldName();
                String asTableName = "a" + refIndex;
                String[] strs = field.split("\\.");
                fields.append(", " + asTableName + "." + displayFieldName + " as " + strs[1]);
                joinSql.append(" left outer join " + tableName + " " + asTableName + " on " + asTableName + "." + pkFieldName + " = "
                    + field + " ");
                refIndex++;
                continue;
            }
            // 翻译枚举类型的数据
            // String emunID = enumMap.get(field);
            // if (!StringUtils.isEmpty(emunID)) {
            // String asTableName = "b" + enumIndex;
            // String[] strs = field.split("\\.");
            // fields.append(", " + asTableName + ".name" + " as " + strs[1]);
            // joinSql.append(" left outer join md_enumvalue " + asTableName
            // + " on " + asTableName + ".value = " + field + " and "
            // + asTableName + ".id = '" + emunID + "'");
            // enumIndex++;
            // continue;
            // }
            
            fields.append("," + field);
        }
        list.add(fields.toString());
        list.add(joinSql.toString());
        return list;
    }
    
    /**
     * 得到每个表可以显示在列表界面的列名
     * @param metaDates
     *            元数据属性
     * @param columNameVSmetadataproperty
     *            用于储存列名和其对应的元数据据属性
     * @param tNameVScolumsMap
     *            信息集表名和其对应元数据上非隐藏属性集合组成的键值对
     * @return HashMap<表名，列集合>
     * @throws BusinessException
     *             ps:由于pub_billtemplate_b表提供的信息不准确，已于2013年3月27日起，不再调用此方法
     */
    public HashMap<String, ArrayList<String>> getListShowFields(String[] metaDatas, HashMap<String, String> columNameVSmetadataproperty,
            HashMap<String, ArrayList<String>> tNameVScolumsMap) throws BusinessException
    {
        
        // 通过元数据得到表名
        String[] tableCodes = new String[metaDatas.length];
        for (int k = 0; k < metaDatas.length; k++)
        {
            tableCodes[k] = metaDatas[k].split("\\.")[1];
        }
        
        String PsnInfoPreserveBilltempletPk = "1001Z71000000000QJAL";// 员工信息维护/员工信息查询所用单据模板pk_billtempletPk
        InSQLCreator isc = new InSQLCreator();
        String inSql = isc.getInSQL(tableCodes);
        String sql =
            "select metadataproperty from pub_billtemplet_b where pk_billtemplet = '" + PsnInfoPreserveBilltempletPk
                + "' and table_code in (" + inSql + ") and showflag = 1 order by showorder";
        // 从员工信息维护/员工信息查询单据模板查出所有可以显示在列表界面值列名
        ArrayList<HashMap<String, String>> list =
            (ArrayList<HashMap<String, String>>) NCLocator.getInstance().lookup(IPersistenceHome.class)
                .executeQuery(sql, new MapListProcessor());
        HashMap<String, ArrayList<String>> tableItemMap = new HashMap<String, ArrayList<String>>();
        
        // 首先遍历所有表名，将每个表对应的元数据和其对应的列名集合做成一个map，
        for (int j = 0; j < tableCodes.length; j++)
        {
            String tableCode = tableCodes[j];
            ArrayList<String> itemList = new ArrayList<String>();
            for (int i = 0; i < list.size(); i++)
            {
                HashMap<String, String> map = list.get(i);
                // metadataproperty的一般形式为hrhi.表名.列名
                String metadataproperty = map.get("metadataproperty");
                // 用正则表达式将metadataproperty拆分成表名和列名
                String[] strs = metadataproperty.split("\\.");
                String itemCode = strs[2];// 列名
                String tableName = strs[1];// 表名
                // tableCode是来源自信息集的表名，tableName是来源自单据模板的表名，tableCode控制哪些表要被导出，tableName控制一张表的哪些字段能被导出，这里通过两次循环组合出哪些表的哪些字段能被导出
                if (tableCode.equals(tableName))
                {
                    // 因为后续要用表名加字段名唯一确定一条元数据属性，所以在组合查询列的时候，索性用表名加列名的形式查询
                    String tnameAndIname = tableName + "." + itemCode;
                    ArrayList<String> notHideitemList = tNameVScolumsMap.get(metaDatas[j]);
                    // 元数据的非空信息项集合和通过pub_billtemplet_b表shoulistflag='Y'信息项集合取交集
                    if (notHideitemList != null && !notHideitemList.isEmpty() && notHideitemList.contains(tnameAndIname))
                    {
                        itemList.add(tnameAndIname);
                    }
                    else
                    {
                        // 理论上notHideitemList永远不会为空，这行代码永远不会走到，但是为保险起见，还是保留continue
                        continue;
                    }
                    columNameVSmetadataproperty.put(tnameAndIname, metadataproperty);
                }
            }
            // 将表名和其对应的列放在一个map里
            tableItemMap.put(metaDatas[j], itemList);
        }
        return tableItemMap;
    }
    
    /**
     * 通过信息集主键pk_infoset得到其对应元数据
     * @param infosetPKs
     * @return
     * @throws BusinessException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList getInfosetTCodebyPKs(String[] infosetPKs) throws BusinessException
    {
        InSQLCreator isc = new InSQLCreator();
        String inSql = isc.getInSQL(infosetPKs);
        String strCondition = "pk_infoset in (" + inSql + "  ) ";
        InfoSetVO[] infoSetVOs =
            (InfoSetVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, InfoSetVO.class, strCondition);
        String[] metaDatas = new String[infoSetVOs.length];
        HashMap<String,String> map = new HashMap<String, String>();
        for (int i = 0; i < metaDatas.length; i++)
        {
            // tableCodes[i] = infoSetVOs[i].getTable_code();
            metaDatas[i] = infoSetVOs[i].getMeta_data();
            String tableCode = infoSetVOs[i].getTable_code();
            String mataCode =  metaDatas[i].split("\\.")[1];
            if(!mataCode.equals("hi_psndoc_parttime") && !tableCode.equals(mataCode)){
            	map.put(mataCode, tableCode);
            }
        }
        ArrayList list = new ArrayList();
        list.add(metaDatas);
        list.add(map);
        
        return list;
    }
    
    
    /**
     * 根据引用信息PK和参照对应值，返回该值对应的名称（pk转成name）,pk_infoset_item用于格式化抛出的错误信息
     * @author yangshuo
     * @throws BusinessException
     * @Created on 2012-12-25 下午03:49:59
     */
    @Override
    public String getRefItemName(String pk_refinfo, Object value, String pk_infoset_item) throws BusinessException
    {
        return getPsndocDAO().getRefItemName(pk_refinfo, value, pk_infoset_item);
    }
    
    /**
     * 获取工作记录到履历记录映射关系
     * @param pk_org 当前工作记录组织
     * @param pk_group 当前工作记录集团
     * @param isMainJob 是否主职，作用是区分源信息集是工作记录还是兼职记录
     * @author yangshuo
     * @throws BusinessException
     * @Created on 2012-12-17 上午09:46:31
     */
    @Override
    public HashMap<String, GeneralVO> getWorkSyncMap(String pk_org, String pk_group, boolean isMainJob) throws BusinessException
    {
        return getPsndocDAO().getWorkSyncMap(pk_org, pk_group, isMainJob);
    }
    
    /**
     * 根据离职人员pk得到已离职人员的离职日期，仅在返聘再聘时调用，查不到则返回null
     * @author yangshuo
     * @throws BusinessException
     * @Created on 2012-12-3 下午03:52:54
     */
    private HashMap<String, UFLiteralDate> getEnddateOfDimStaff(String... pk_psndoc) throws BusinessException
    {
        InSQLCreator isc = new InSQLCreator();
        
        PsnOrgVO[] psnorg =
            (PsnOrgVO[]) NCLocator
                .getInstance()
                .lookup(IPersistenceRetrieve.class)
                .retrieveByClause(null, PsnOrgVO.class,
                    " lastflag = 'Y' and endflag = 'Y' and pk_psndoc in  (" + isc.getInSQL(pk_psndoc) + ") ");
        if (psnorg == null || psnorg.length == 0)
        {
            return null;
        }
        
        HashMap<String, UFLiteralDate> map = new HashMap<String, UFLiteralDate>();
        for (PsnOrgVO org : psnorg)
        {
            map.put(org.getPk_psndoc(), org.getEnddate());
        }
        return map;
        
    }
    
    @Override
    public HashMap<String, String> validateRegDate(HashMap<String, String> orgPKMap, HashMap<String, UFLiteralDate> dateMap)
            throws BusinessException
    {
        
        if (orgPKMap == null || orgPKMap.size() == 0)
        {
            return null;
        }
        
        String[] psndocPKs = orgPKMap.keySet().toArray(new String[0]);
        
        InSQLCreator isc = new InSQLCreator();
        String insql = isc.getInSQL(psndocPKs);
        // 查询人员姓名
        PsndocVO[] psn =
            (PsndocVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                .retrieveByClause(null, PsndocVO.class, " pk_psndoc in (" + insql + ") ");
        
        HashMap<String, String> nameMap = new HashMap<String, String>();
        for (int i = 0; psn != null && i < psn.length; i++)
        {
            nameMap.put(psn[i].getPk_psndoc(), MultiLangHelper.getName(psn[i]));
        }
        
        // 查询人员最新离职日期
        HashMap<String, UFLiteralDate> dimiDateMap = getEnddateOfDimStaff(psndocPKs);
        // UAP 不停用组织关系，当dimiDateMap为空时候直接返回
        if (null == dimiDateMap)
        {
            return null;
        }
        
        HashMap<String, String> map = new HashMap<String, String>();
        for (String pk : psndocPKs)
        {
            // 获得离职后再入职时间间隔
            Integer para = SysInitQuery.getParaInt(orgPKMap.get(pk), HICommonValue.PARA_7);
            if (para == null || para == 0)
            {
                continue;
            }
            // 现在的入职日期
            UFLiteralDate currBegindate = dateMap.get(pk);
            
            // 最新离职日期
            UFLiteralDate currEnddate = dimiDateMap.get(pk);
            
            if (currEnddate == null)
            {
                continue;
            }
            // 离职日期+时间间隔后的日期
            UFLiteralDate endDateAfterIntrrpt = HRCMTermUnitUtils.getDateAfterMonth(currEnddate, para, HRCMTermUnitUtils.TERMUNIT_MONTH);
            // 入职离职时间间隔
            // Integer term = HRCMTermUnitUtils.getTermByDateAndUnit( currEnddate , currBegindate,
            // HRCMTermUnitUtils.TERMUNIT_MONTH);
            // 入职日期在endDateAfterIntrrpt之前 ，则不满足条件
            if (currBegindate.before(endDateAfterIntrrpt))
            {
                map.put(pk, nameMap.get(pk));
            }
        }
        
        return map.size() == 0 ? null : map;
    }
    
    @Override
    public ArrayList<String[]> queryInfosetMetaID() throws BusinessException
    {
        InfoSetVO[] set =
            (InfoSetVO[]) NCLocator
                .getInstance()
                .lookup(IPersistenceRetrieve.class)
                .retrieveByClause(null, InfoSetVO.class,
                    " pk_infoset_sort in ( '1001Z710000000002XPP', '1001Z71000000000300Q', '1001Z71000000000301Q', '1002Z710000000011ZA9' ) ");
        
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        for (InfoSetVO s : set)
        {
            String sortPK = s.getPk_infoset_sort();
            if (!map.containsKey(sortPK))
            {
                map.put(sortPK, new ArrayList<String>());
            }
            map.get(sortPK).add(s.getMeta_data_id());
        }
        
        ArrayList<String[]> result = new ArrayList<String[]>();
        for (ArrayList<String> al : map.values())
        {
            result.add(al.toArray(new String[0]));
        }
        
        return result;
    }
    
    /**
     * 通过工作记录主键查找人员主键
     */
    public String[] convertPks(ArrayList<String> psnjobPkList) throws BusinessException
    {
        String[] psnjobPks = psnjobPkList.toArray(new String[psnjobPkList.size()]);
        InSQLCreator isc = new InSQLCreator();
        String inSql = isc.getInSQL(psnjobPks);
        String strCondition = "pk_psnjob in (" + inSql + ")";
        PsnJobVO[] psnjobVOs =
            (PsnJobVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, PsnJobVO.class, strCondition);
        String[] psndocPks = new String[psnjobVOs.length];
        for (int i = 0; i < psnjobVOs.length; i++)
        {
            psndocPks[i] = psnjobVOs[i].getPk_psndoc();
        }
        return psndocPks;
    }
    
    @Override
    public Map<String, Boolean> isInJob(String[] pk_psndocs) throws BusinessException
    {
        return getPsndocDAO().isInJob(pk_psndocs);
    }
    
    public Map<String, PsndocAggVO> queryPsndocVOByCondition(String[] psndocPks, String[] className) throws BusinessException
    {
        return getPsndocDAO().queryPsndocVOByCondition(psndocPks, className);
    }
    
    @Override
    public PsndocVO[] queryPsndocVOByCondition(String[] psnCodes) throws BusinessException
    {
        InSQLCreator isc = new InSQLCreator();
        String insql = isc.getInSQL(psnCodes);
        String condition = "code in (" + insql + ")";
        PsndocAggVO[] psndocAggVOs = getPsndocDAO().queryByCondition(PsndocAggVO.class, condition);
        
        if (ArrayUtils.isEmpty(psndocAggVOs))
        {
            return null;
        }
        PsndocVO[] psndocVOs = new PsndocVO[psndocAggVOs.length];
        for (int i = 0; i < psndocAggVOs.length; i++)
        {
            psndocVOs[i] = psndocAggVOs[i].getParentVO();
        }
        return psndocVOs;
    }
    
    private Boolean isModuleStarted()
    {
        return PubEnv.isModuleStarted(null, HICommonValue.HRMM_MODULE_CODE);
    }
    
    /**
     * {@inheritDoc}<br>
     * Created on 2013-11-13 11:25:35<br>
     * @see nc.itf.hi.IPsndocQryService#getValueOFParaMaintainCadre()
     * @author caiqm
     */
    @Override
    public Boolean getValueOFParaMaintainCadre(LoginContext context) throws BusinessException
    {
        // 没启用干部管理模块 或者 启用干部管理模块并且参数值为Y时，可以选到干部人员（不过滤干部人员）
        // 启用干部管理模块并且参数值为N时,不可选到干部人员
        if (!isModuleStarted()
            || (isModuleStarted() && "Y".equals(SysInitQuery.getParaString(context.getPk_group(), HICommonValue.HRMM_PARAM_MAINTAIN))))
            return true;
        return false;
    }
    
    /**
     * {@inheritDoc}<br>
     * Created on 2013-11-13 11:25:35<br>
     * @see nc.itf.hi.IPsndocQryService#getValueOFParaDeployCadre()
     * @author caiqm
     */
    @Override
    public Boolean getValueOFParaDeployCadre(LoginContext context) throws BusinessException
    {
        // 没启用干部管理模块 或者 启用干部管理模块并且参数值为Y时，可以选到干部人员（不过滤干部人员）
        // 启用干部管理模块并且参数值为N时,不可选到干部人员
        if (!isModuleStarted()
            || (isModuleStarted() && "Y".equals(SysInitQuery.getParaString(context.getPk_group(), HICommonValue.HRMM_PARAM_DEPLOY))))
            return true;
        return false;
    }
    
    @Override
    public Map<String, Object> getLevelRankCondition(String pk_jobType, String pk_job, String pk_postSeries, String pk_post)
            throws BusinessException
    {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        FilterTypeEnum filterType = null;
        String gradeSource = "";
        String defaultJob = "";
        if (StringUtils.isNotBlank(pk_post))
        {
            PostVO postVO = getPsndocDAO().queryByPk(PostVO.class, pk_post, true);
            defaultJob = postVO.getPk_job() == null ? "" : postVO.getPk_job();
        }
        
        if (StringUtils.isNotBlank(pk_job) && !defaultJob.equals(pk_job))
        {// 职务不为空
            JobVO job = getPsndocDAO().queryByPk(JobVO.class, pk_job, true);
            if (job.getInheritflag() != null && job.getInheritflag().booleanValue())
            {
                if (job.getSourcetype() == 0) 
                {
                    filterType = FilterTypeEnum.JOBTYPE;
                }
                else if (job.getSourcetype() == 1)
                {
                    filterType = FilterTypeEnum.JOB;
                }
                gradeSource = job.getPk_level_source();
            }
            else
            {
                filterType = FilterTypeEnum.JOB;
                gradeSource = pk_job;
            }
        }
        else if (StringUtils.isNotBlank(pk_post))
        {// 职务为空，岗位不为空
            nc.vo.om.post.PostVO postVO = getPsndocDAO().queryByPk(nc.vo.om.post.PostVO.class, pk_post, true);
            if (postVO.getInheritflag() != null && postVO.getInheritflag().booleanValue())
            {
                if (postVO.getSourcetype() == 0) 
                {
                    filterType = FilterTypeEnum.JOBTYPE;
                }
                else if (postVO.getSourcetype() == 1)
                {
                    filterType = FilterTypeEnum.JOB;
                }
                else if (postVO.getSourcetype() == 2)
                {
                    filterType = FilterTypeEnum.POSTSERIESE;
                }
                else if (postVO.getSourcetype() == 3)
                {
                    filterType = FilterTypeEnum.POST;
                }
                gradeSource = postVO.getPk_level_source();
            }
            else
            {
                filterType = FilterTypeEnum.POST;
                gradeSource = pk_post;
            }
        }
        else if (StringUtils.isNotBlank(pk_jobType))
        {// 职务类别不为空
            JobTypeVO jobTypeVO = getPsndocDAO().queryByPk(JobTypeVO.class, pk_jobType, true);
            filterType = FilterTypeEnum.JOBTYPE;
            gradeSource = jobTypeVO.getPk_level_source();
        }
        else if (StringUtils.isNotBlank(pk_postSeries))
        {// 岗位序列不为空
            PostSeriesVO postSeriesVO = getPsndocDAO().queryByPk(PostSeriesVO.class, pk_postSeries, true);
            filterType = FilterTypeEnum.POSTSERIESE;
            gradeSource = postSeriesVO.getPk_level_source();
        }
        if (filterType != null)
        {
            resultMap.put("filterType", filterType);
            resultMap.put("gradeSource", gradeSource);
        }
        return resultMap;
    }
    
    @Override
    public Map<String, String> getDefaultLevelRank(String pk_jobType, String pk_job, String pk_postSeries, String pk_post,
            String pk_joblevel) throws BusinessException
    {
        Map<String, String> resultMap = new HashMap<String, String>();
        LevelRelationVO[] levelVOs = null;
        RankRelationVO[] rankVOs = null;
        String defaultlevel = "";
        String defaultrank = "";
        
        if (StringUtils.isBlank(pk_joblevel))
        {
            pk_joblevel = "";
        }
        String defaultJob = "";
        if (StringUtils.isNotBlank(pk_post))
        {
            PostVO postVO = getPsndocDAO().queryByPk(PostVO.class, pk_post, true);
            defaultJob = postVO.getPk_job() == null ? "" : postVO.getPk_job();
        }
        String coindition = "";
        if (StringUtils.isNotBlank(pk_job) && !defaultJob.equals(pk_job))
        {
            // 职务不为空
            JobVO job = getPsndocDAO().queryByPk(JobVO.class, pk_job, true);
            if (StringUtils.isBlank(pk_joblevel))
            {
                if (job.getInheritflag() != null && job.getInheritflag().booleanValue())
                {
                    if (job.getSourcetype() == 0) 
                    {
                        coindition = "pk_jobtype = '" + job.getPk_level_source() + "'";
                    }
                    else
                    {
                        coindition = "pk_job = '" + job.getPk_level_source() + "'";
                    }
                }
                else
                {
                    coindition = "pk_job = '" + job.getPk_job() + "'";
                }
                levelVOs =
                    (LevelRelationVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                        .retrieveByClause(null, LevelRelationVO.class, "defaultlevel = 'Y' and " + coindition);
                if (!ArrayUtils.isEmpty(levelVOs))
                {
                    defaultlevel = levelVOs[0].getPk_joblevel();
                }
            }
            else
            {
                defaultlevel = pk_joblevel;
                if (job.getInheritflag() != null && job.getInheritflag().booleanValue())
                {
                    if (job.getSourcetype() == 0) 
                    {
                        coindition = "pk_jobtype = '" + job.getPk_level_source() + "'";
                    }
                    else
                    {
                        coindition = "pk_job = '" + job.getPk_level_source() + "'";
                    }
                }
                else
                {
                    coindition = "pk_job = '" + job.getPk_job() + "'";
                }
            }
            
            if (StringUtils.isNotBlank(defaultlevel))
            {
                rankVOs =
                    (RankRelationVO[]) getPsndocDAO().queryByCondition(RankRelationVO.class,
                        "pk_joblevel = '" + defaultlevel + "' and defaultrank = 'Y' and " + coindition);
                if (!ArrayUtils.isEmpty(rankVOs))
                {
                    defaultrank = rankVOs[0].getPk_jobrank();
                }
            }
        }
        else if (StringUtils.isNotBlank(pk_post))
        {   
            // 职务为空，岗位不为空
            nc.vo.om.post.PostVO postVO = getPsndocDAO().queryByPk(nc.vo.om.post.PostVO.class, pk_post, true);
            if (StringUtils.isBlank(pk_joblevel))
            {
                if (postVO.getInheritflag() != null && postVO.getInheritflag().booleanValue())
                {
                    if (postVO.getSourcetype() == 0) 
                    {
                        coindition = "pk_jobtype = '" + postVO.getPk_level_source() + "'";
                    }
                    else if (postVO.getSourcetype() == 1)
                    {
                        coindition = "pk_job = '" + postVO.getPk_level_source() + "'";
                    }
                    else if (postVO.getSourcetype() == 2)
                    {
                        coindition = "pk_postseries = '" + postVO.getPk_level_source() + "'";
                    }
                    else if (postVO.getSourcetype() == 3)
                    {
                        coindition = "pk_post = '" + postVO.getPk_level_source() + "'";
                    }
                }
                else
                {
                    coindition = "pk_post = '" + postVO.getPk_post() + "'";
                }
                levelVOs =
                    (LevelRelationVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                        .retrieveByClause(null, LevelRelationVO.class, "defaultlevel = 'Y' and " + coindition);
                
                if (!ArrayUtils.isEmpty(levelVOs))
                {
                    defaultlevel = levelVOs[0].getPk_joblevel();
                }
            }
            else
            {
                defaultlevel = pk_joblevel;
                if (postVO.getInheritflag() != null && postVO.getInheritflag().booleanValue())
                {
                    if (postVO.getSourcetype() == 0) 
                    {
                        coindition = "pk_jobtype = '" + postVO.getPk_level_source() + "'";
                    }
                    else if (postVO.getSourcetype() == 1)
                    {
                        coindition = "pk_job = '" + postVO.getPk_level_source() + "'";
                    }
                    else if (postVO.getSourcetype() == 2)
                    {
                        coindition = "pk_postseries = '" + postVO.getPk_level_source() + "'";
                    }
                    else if (postVO.getSourcetype() == 3)
                    {
                        coindition = "pk_post = '" + postVO.getPk_level_source() + "'";
                    }
                }
                else
                {
                    coindition = "pk_post = '" + postVO.getPk_post() + "'";
                }
            }
            if (StringUtils.isNotBlank(defaultlevel))
            {
                rankVOs =
                    (RankRelationVO[]) getPsndocDAO().queryByCondition(RankRelationVO.class,
                        "pk_joblevel = '" + defaultlevel + "' and defaultrank = 'Y' and " + coindition);
                if (!ArrayUtils.isEmpty(rankVOs))
                {
                    defaultrank = rankVOs[0].getPk_jobrank();
                }
            }
        }
        else if (StringUtils.isNotBlank(pk_jobType))
        { 
            // 职务类别不为空
            JobTypeVO jobTypeVO = getPsndocDAO().queryByPk(JobTypeVO.class, pk_jobType);
            if (StringUtils.isBlank(pk_joblevel))
            {
                coindition = "pk_jobtype = '" + jobTypeVO.getPk_level_source() + "' and defaultlevel = 'Y' ";
                levelVOs =
                        (LevelRelationVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                        .retrieveByClause(null, LevelRelationVO.class, coindition);
                if (!ArrayUtils.isEmpty(levelVOs))
                {
                    defaultlevel = levelVOs[0].getPk_joblevel();
                }
            }
            else
            {
                defaultlevel = pk_joblevel;
            }
            if (StringUtils.isNotBlank(defaultlevel))
            {
                rankVOs =
                    (RankRelationVO[]) getPsndocDAO().queryByCondition(RankRelationVO.class,
                        "pk_joblevel = '" + defaultlevel + "' and defaultrank = 'Y' and pk_jobtype = '" + jobTypeVO.getPk_level_source() + "'");
                if (!ArrayUtils.isEmpty(rankVOs))
                {
                    defaultrank = rankVOs[0].getPk_jobrank();
                }
            }
        }
        else if (StringUtils.isNotBlank(pk_postSeries))
        { 
            // 岗位序列不为空
            PostSeriesVO postSeriesVO = getPsndocDAO().queryByPk(PostSeriesVO.class, pk_postSeries, true);
            if (StringUtils.isBlank(pk_joblevel))
            {
                coindition = "pk_postseries = '" + postSeriesVO.getPk_level_source() + "' and defaultlevel = 'Y' ";
                levelVOs =
                        (LevelRelationVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                        .retrieveByClause(null, LevelRelationVO.class, coindition);
                
                if (!ArrayUtils.isEmpty(levelVOs))
                {
                    defaultlevel = levelVOs[0].getPk_joblevel();
                }
            }
            else
            {
                defaultlevel = pk_joblevel;
            }
            if (StringUtils.isNotBlank(defaultlevel))
            {
                rankVOs =
                    (RankRelationVO[]) getPsndocDAO().queryByCondition(RankRelationVO.class,
                        "pk_joblevel = '" + defaultlevel + "' and defaultrank = 'Y' and pk_postseries = '" + postSeriesVO.getPk_level_source() + "'");
                if (!ArrayUtils.isEmpty(rankVOs))
                {
                    defaultrank = rankVOs[0].getPk_jobrank();
                }
            }
        }
        if (StringUtils.isNotBlank(defaultlevel))
        {
            resultMap.put("defaultlevel", defaultlevel);
        }
        if (StringUtils.isNotBlank(defaultrank))
        {
            resultMap.put("defaultrank", defaultrank);
        }
        return resultMap;
    }
    
    
    /**
	 * 根据所给数组获得where条件中in后面部分，此方法只能在后台使用
	 * 2013-10-29 下午01:51:24
	 * yunana
	 * @param conditions
	 * @return
	 * @throws BusinessException 
	 */
	public static String getInsqlForPrivate(String[] conditions) throws BusinessException{
		InSQLCreator isc = new InSQLCreator();
		StringBuilder sbder = new StringBuilder();
        String insql = isc.getInSQL(conditions);
		sbder.append("(");
		sbder.append(insql);
		sbder.append(")");
		return sbder.toString();
	}
    
    public HRDeptVO[] queryHRDeptVOsByPk(String[] pks,int deptType) throws BusinessException{
    	String insql = getInsqlForPrivate(pks);
    	String condition = " pk_dept in "+insql +" and depttype = "+deptType;
    	HRDeptVO[] deptVOs = (HRDeptVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, HRDeptVO.class, condition);
    	return deptVOs;
    }
    
}
