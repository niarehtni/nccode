package nc.impl.trn.dimissionrds;
  
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
 
import javax.naming.NamingException;

import nc.bs.bd.baseservice.md.SingleBaseService;
import nc.bs.businessevent.IEventType;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.SystemException;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.hi.IBlacklistManageService;
import nc.itf.hi.IPassawayService;
import nc.itf.hi.IPersonRecordService;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.hr.frame.IPersistenceHome;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.trn.TrnDelegator;
import nc.itf.trn.rds.IRdsManageService;
import nc.itf.trn.rds.IRdsQueryService;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.itf.uap.pf.IplatFormEntry;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.md.model.impl.MDEnum;
import nc.md.persist.framework.MDPersistenceService;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pub.billcode.itf.IBillcodeRuleQryService;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pub.billcode.vo.BillCodeElemVO;
import nc.pub.billcode.vo.BillCodeRuleVO;
import nc.pub.tools.HIQueryLogUtils;
import nc.pub.tools.HiCacheUtils;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.VOUtils;
import nc.ui.bd.ref.IRefConst;
import nc.ui.pub.bill.IBillItem;
import nc.vo.bd.psnid.PsnIdtypeVO;
import nc.vo.hi.blacklist.AggBlacklistVO;
import nc.vo.hi.blacklist.BlacklistVO;
import nc.vo.hi.entrymng.HiSendMsgHelper;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.KeyPsnVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnChgVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.RetireVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.hi.psndoc.WorkVO;
import nc.vo.hi.psndoc.enumeration.TrnseventEnum;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hi.pub.HiBatchEventValueObject;
import nc.vo.hi.pub.HiEventValueObject;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.om.job.JobVO;
import nc.vo.om.post.PostVO;
import nc.vo.om.pub.SuperVOHelper;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.regmng.RegapplyVO;
import nc.vo.trn.transitem.TrnTransItemVO;
import nc.vo.trn.transmng.AggStapply;
import nc.vo.trn.transmng.StapplyVO;
import nc.vo.uif2.LoginContext;
import nc.vo.util.BDReferenceChecker;
import nc.vo.util.BDVersionValidationUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class DimissionrdsServiceImpl extends SingleBaseService<PsndocVO> implements IRdsManageService, IRdsQueryService
{
    private BaseDAO baseDAO = null;
    
    public DimissionrdsServiceImpl()
    {
        super(HICommonValue.MD_ID_PSNDOC);
    }
    
    private final String DOC_NAME = "DimissionRds";
    
    @Override
    public PsndocAggVO addPsnjob(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException
    {
        // ����һ��ʼ�ͼ�鲢����ts,���ts���Ծ����쳣
        // �汾У�飨ʱ���У�飩
        BDVersionValidationUtil.validateSuperVO(aggVO.getParentVO());
        PsnJobVO saveData = (PsnJobVO) aggVO.getTableVO(curTabCode)[0];
        saveData.setClerkcode(saveData.getClerkcode().trim());
        // ��ֹ�ؼ���Ա
        // this.endKeyPsn(saveData);
        aggVO.getParentVO().setPsnJobVO(getIPersonRecordService().addNewPsnjob(saveData, isSynWork, pk_hrorg));
        // У�������
        this.validateBlack(aggVO, saveData);
        aggVO = updateMainVOAndReturnAll(aggVO, curTabCode, saveData.getClass(), getCondition(saveData));
        // ��Ա�䶯ʱ�������Ա���������˺������򣬸��ݲ���������Ա����
        updatePsncode(aggVO, saveData.getPk_hrorg(), TRNConst.Table_NAME_DEPTCHG);
        return aggVO;
    }
    @Override
    public PsndocAggVO addPsnjob_RequiresNew(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException
    {
        // ����һ��ʼ�ͼ�鲢����ts,���ts���Ծ����쳣
        // �汾У�飨ʱ���У�飩
        BDVersionValidationUtil.validateSuperVO(aggVO.getParentVO());
        PsnJobVO saveData = (PsnJobVO) aggVO.getTableVO(curTabCode)[0];
        saveData.setClerkcode(saveData.getClerkcode().trim());
        // ��ֹ�ؼ���Ա
        // this.endKeyPsn(saveData);
        aggVO.getParentVO().setPsnJobVO(getIPersonRecordService().addNewPsnjob(saveData, isSynWork, pk_hrorg));
        // У�������
        this.validateBlack(aggVO, saveData);
        aggVO = updateMainVOAndReturnAll(aggVO, curTabCode, saveData.getClass(), getCondition(saveData));
        // ��Ա�䶯ʱ�������Ա���������˺������򣬸��ݲ���������Ա����
        updatePsncode(aggVO, saveData.getPk_hrorg(), TRNConst.Table_NAME_DEPTCHG);
        return aggVO;
    }
    
    @Override
    public PsndocAggVO addPsnjobTranster(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, boolean isFinshPart, String pk_hrorg)
            throws BusinessException
    {
        // ����һ��ʼ�ͼ�鲢����ts,���ts���Ծ����쳣
        // �汾У�飨ʱ���У�飩
        BDVersionValidationUtil.validateSuperVO(aggVO.getParentVO());
        PsnJobVO saveData = (PsnJobVO) aggVO.getTableVO(curTabCode)[0];
        saveData.setClerkcode(saveData.getClerkcode().trim());
        // ��ֹ�ؼ���Ա
        // this.endKeyPsn(saveData);
        aggVO.getParentVO().setPsnJobVO(getIPersonRecordService().addNewPsnjobTran(saveData, isSynWork, isFinshPart, pk_hrorg));
        // У�������
        this.validateBlack(aggVO, saveData);
        aggVO = updateMainVOAndReturnAll(aggVO, curTabCode, saveData.getClass(), getCondition(saveData));
        // ��Ա�䶯ʱ�������Ա���������˺������򣬸��ݲ���������Ա����
        updatePsncode(aggVO, saveData.getPk_hrorg(), TRNConst.Table_NAME_DEPTCHG);
        return aggVO;
    }
    
    // ��Ա�䶯ʱ�������Ա���������˺������򣬸��ݲ���������Ա����
    public PsndocAggVO updatePsncode(PsndocAggVO aggVO, String pk_hrorg, String key) throws BusinessException
    {
        // ����Ϊ�ǵ�����²ſ�����Щ
        UFBoolean para = SysinitAccessor.getInstance().getParaBoolean(pk_hrorg, "TRN0005");
        if (para != null && para.booleanValue())
        {
            BillCodeContext billContext =
                NCLocator.getInstance().lookup(IBillcodeManage.class)
                    .getBillCodeContext(HICommonValue.NBCR_PSNDOC_CODE, PubEnv.getPk_group(), pk_hrorg);
            // ��Ա�����������Ϊ�����
            if (billContext != null && !billContext.isPrecode())
            {
                // ������õ��Ǻ���룬����Ա�䶯ʱ����Ҫ�жϱ�������ж����ҵ��ʵ���ֵ�Ƿ����˱仯���������Ƿ�����������Ա����
                boolean ischange = getCodeRule(aggVO, pk_hrorg, key);
                if (ischange)
                {
                    IHrBillCode service = NCLocator.getInstance().lookup(IHrBillCode.class);
                    // String[] strCode = service.getLeveledBillCode(HICommonValue.NBCR_PSNDOC_CODE,
                    // PubEnv.getPk_group(), pk_hrorg,aggVO.getParentVO(),1);
                    String[] strCode =
                        service.getLeveledBillCode(HICommonValue.NBCR_PSNDOC_CODE, PubEnv.getPk_group(), pk_hrorg,
                            aggVO.getTableVO(key)[aggVO.getTableVO(key).length - 1], 1);
                    aggVO.getParentVO().setCode(strCode[0]);
                    NCLocator.getInstance().lookup(IPersistenceUpdate.class)
                        .updateVO(null, aggVO.getParentVO(), new String[]{aggVO.getParentVO().CODE}, null);
                }
            }
        }
        return aggVO;
    }
    
    // ������õ��Ǻ���룬����Ա�䶯ʱ����Ҫ�жϱ�������ж����ҵ��ʵ���ֵ�Ƿ����˱仯���������Ƿ�����������Ա����
    public boolean getCodeRule(PsndocAggVO aggVO, String pk_hrorg, String key) throws BusinessException
    {
        boolean ischange = false;
        BillCodeRuleVO rulevo = getBillCodeRuleVOFromDB(HICommonValue.NBCR_PSNDOC_CODE, PubEnv.getPk_group(), pk_hrorg);
        BillCodeElemVO[] elems = rulevo.getElems();
        // ���ҵ��ʵ��elemtype=1���ֶ�ץȡ��������Ҫ�Աȱ䶯ǰ���ֵ�������Ƿ�����������Ա����
        if (!ArrayUtils.isEmpty(elems))
        {
            List<String> liststr = new ArrayList<String>();
            for (int i = 0; i < elems.length; i++)
            {
                if (elems[i].getElemtype() == 1)
                {
                    liststr.add(elems[i].getElemvalue());
                }
            }
            String[] str = liststr.toArray(new String[0]);
            if (ArrayUtils.isEmpty(str))
            {// ����ĺ���������û��ѡ��ҵ��ʵ�壬�����һֱ������Ҫ��������
                return ischange;
            }
            PsnJobVO[] psnjobvo = (PsnJobVO[]) aggVO.getTableVO(key);
            for (int i = 0; i < str.length; i++)
            {
                String name = str[i];
                String newdata = null;
                String olddata = null;
                for (int j = 0; j < psnjobvo.length; j++)
                {
                    if (psnjobvo[j].getRecordnum() == 0)
                    {
                        newdata = psnjobvo[j].getAttributeValue(name) == null ? "" : psnjobvo[j].getAttributeValue(name).toString();
                    }
                    if (psnjobvo[j].getRecordnum() == 1)
                    {
                        olddata = psnjobvo[j].getAttributeValue(name) == null ? "" : psnjobvo[j].getAttributeValue(name).toString();
                    }
                    
                }
                if (!newdata.equals(olddata))
                {
                    ischange = true;
                }
            }
        }
        return ischange;
    }
    
    /**
     * ���ݵ������ͱ���õ��õ������͵ĵ��ݺŹ���
     * 
     * @param billTypeCode
     * @return
     * @throws SQLException
     * @throws NamingException
     * @throws SystemException
     * @throws ValidationException
     */
    private BillCodeRuleVO getBillCodeRuleVOFromDB(String nbcrcode, String pk_group, String pk_org) throws BusinessException
    {
        BillCodeRuleVO rulevo;
        try
        {
            IBillcodeRuleQryService service = NCLocator.getInstance().lookup(IBillcodeRuleQryService.class);
            rulevo = service.qryBillCodeRule(nbcrcode, pk_group, pk_org);
        }
        catch (Exception e)
        {
            Logger.error("Error occurs while querying BillCodeRule", e);
            throw new BusinessException("Error occurs while querying BillCodeRule", e);
        }
        if (rulevo == null)
            throw new nc.vo.pub.ValidationException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("101704", "UPP101704-000009", null,
                new String[]{nbcrcode}));// /*@res
        // "�õ������ͻ�û�ж��������򣬻�õ��ݺ�ʧ�ܣ��������ͱ���Ϊ{0}"*/+billTypeCode+"'");
        return rulevo;
    }
    
    /**
     * �����¼��������У������� 2013-3-20 ����01:20:26 yunana
     * @param aggVO
     * @param saveData
     * @throws BusinessException
     */
    private void validateBlack(PsndocAggVO aggVO, PsnJobVO saveData) throws BusinessException
    {
        String pk_org = saveData.getPk_org();// �������ݵ���֯��ϵ
        PsndocAggVO aggVOCopy = (PsndocAggVO) aggVO.clone();
        PsndocVO psndocVO = aggVOCopy.getParentVO();
        psndocVO.setPk_org(pk_org);
        String pk_psndoc = psndocVO.getPk_psndoc();
        String condition = " pk_psndoc = '" + pk_psndoc + "'";
        SuperVO[] certVOs = NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, CertVO.class, condition);
        aggVO.setTableVO(CertVO.getDefaultTableName(), certVOs);
        aggVO.getTableVO("hi_psndoc_cert");
        NCLocator.getInstance().lookup(IPsndocQryService.class).isInBlacklist(aggVOCopy);
        
    }
    
    private void addPsnjobByEndTrial(TrialVO trial, String pkHrorg, boolean isSynWork) throws BusinessException
    {
        // ͨ������/�޸����ü�¼ʵ��ת���Ĳ����ӹ�����¼,Ҫ���Ƿ�����&�������ͻ�д����һ��������¼��
        setTrialinfo(trial.getPk_psnorg(), UFBoolean.TRUE, trial.getTrial_type());
        // ����һ�����������ü�¼ ������һ��ת���Ĺ�����¼
        PsnJobVO oldJob = getLastVO(PsnJobVO.class, trial.getPk_psnorg(), 1);
        if (oldJob != null)
        {
            PsnJobVO newJob = (PsnJobVO) oldJob.clone();
            newJob.setPk_psnjob(null);
            newJob.setBegindate(trial.getRegulardate());
            newJob.setEnddate(null);
            newJob.setEndflag(UFBoolean.FALSE);
            newJob.setPoststat(UFBoolean.TRUE);
            newJob.setTrial_flag(UFBoolean.FALSE);
            newJob.setTrial_type(null);
            newJob.setTrnsevent((Integer) ((MDEnum) TrnseventEnum.REGAPPLY).value());
            newJob.setTrnstype(TRNConst.TRANSTYPE_REG);// ת���춯����
            newJob.setTrnsreason(null);
            newJob.setLastflag(UFBoolean.TRUE);
            newJob.setRecordnum(0);
            newJob.setPk_hrorg(pkHrorg);
            getIPersonRecordService().addNewPsnjob(newJob, isSynWork, pkHrorg);
        }
    }
    
    @Override
    public void addPsnjobDimission(PsndocAggVO aggVO, String curTabCode, String pk_hrorg, boolean isDisablePsn) throws BusinessException
    {
        // ����һ��ʼ�ͼ�鲢����ts,���ts���Ծ����쳣, �汾У�飨ʱ���У�飩
        BDVersionValidationUtil.validateSuperVO(aggVO.getParentVO());
        PsnJobVO saveData = (PsnJobVO) aggVO.getTableVO(curTabCode)[0];
        saveData.setClerkcode(saveData.getClerkcode().trim());
        
        getIPersonRecordService().addNewDimission(saveData, pk_hrorg, isDisablePsn);
        
        // 3-4)����������Ա��
        stopKeyPsn(aggVO.getParentVO().getPsnJobVO().getPk_psnjob(), saveData.getBegindate());
        
        aggVO = updateMainVOAndReturnAll(aggVO, curTabCode, saveData.getClass(), getCondition(saveData));
        // ��Ա�䶯ʱ�������Ա���������˺������򣬸��ݲ���������Ա����
        updatePsncode(aggVO, saveData.getPk_hrorg(), TRNConst.Table_NAME_DIMISSION);
    }
    
    private void stopKeyPsn(String pk_psnjob, UFLiteralDate enddate) throws BusinessException
    {
        KeyPsnVO[] keyVOs =
            getServiceTemplate().queryByCondition(
                KeyPsnVO.class,
                " pk_psnorg in (select pk_psnorg from hi_psnjob where pk_psnjob ='" + pk_psnjob
                    + "') and (endflag = 'N' or isnull(endflag,'~') ='~') ");
        if (keyVOs != null && keyVOs.length > 0)
        {
            for (int i = 0; i < keyVOs.length; i++)
            {
                keyVOs[i].setEndflag(UFBoolean.TRUE);
                if (keyVOs[i].getBegindate().afterDate(enddate))
                {
                    keyVOs[i].setEnddate(keyVOs[i].getBegindate());
                }
                else
                {
                    keyVOs[i].setEnddate(enddate);
                }
                keyVOs[i].setStatus(VOStatus.UPDATED);
            }
            NCLocator.getInstance().lookup(IPersistenceUpdate.class)
                .updateVOArray(null, keyVOs, new String[]{KeyPsnVO.ENDDATE, KeyPsnVO.ENDFLAG}, null);
        }
    }
    
    @Override
    public PsndocAggVO addSubRecord(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException
    {
        // ����һ��ʼ�ͼ�鲢����ts,���ts���Ծ����쳣
        // �汾У�飨ʱ���У�飩
        BDVersionValidationUtil.validateSuperVO(aggVO.getParentVO());
        SuperVO saveData = aggVO.getTableVO(curTabCode)[0];
        if (!TRNConst.Table_NAME_PART.equals(curTabCode))
        {
            // ������Ǽ�ְ,����ԭ�м�¼��recordnum��lastflag
            updateRdsnumAndLastflag(saveData);
        }
        
        if (TRNConst.Table_NAME_PART.equals(curTabCode))
        {
            // ���Ӽ�ְ��¼,Ҫ����Ա����
            ((PsnJobVO) saveData).setClerkcode((((PsnJobVO) saveData)).getClerkcode().trim());
            checkClerkCodeUnique((PsnJobVO) saveData);
            getIPersonRecordService().checkDeptPostCanceled((PsnJobVO) saveData);
        }
        // ������VO
        SuperVO retVO = getServiceTemplate().insert(saveData);
        if (saveData instanceof PartTimeVO)
        {
            fireEvent(null, (PartTimeVO) retVO, pk_hrorg, HICommonValue.PARTSOURCEID, IEventType.TYPE_INSERT_AFTER);
            HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
            if (isSynWork)
            {
                // ��ְͬ����������
                getIPersonRecordService().addWork((PartTimeVO) retVO);
            }
        }
        if (saveData instanceof TrialVO)
        {
            // �������¹�����¼��"�Ƿ�����"��"��������"
            TrialVO trial = (TrialVO) saveData;
            UFLiteralDate jobBeginDate = aggVO.getParentVO().getPsnJobVO().getBegindate();
            // ��¼��ʷ������Ϣʱ��������
            if (!(jobBeginDate != null && trial.getRegulardate() != null && jobBeginDate.afterDate(trial.getRegulardate())))
            {
                if (trial.getEndflag() != null && trial.getEndflag().booleanValue())
                {
                    if (trial.getTrialresult() != null && trial.getTrialresult() == 1)
                    {
                        // ���ý�����ʹ�ý��δת�������ӹ�����¼
                        addPsnjobByEndTrial(trial, pk_hrorg, isSynWork);
                    }
                    else
                    {
                        setTrialinfo(trial.getPk_psnorg(), UFBoolean.FALSE, null);
                    }
                }
                else
                {
                    setTrialinfo(trial.getPk_psnorg(), UFBoolean.TRUE, trial.getTrial_type());
                }
            }
        }
        // ͬ����Ա������Ϣ
        getIPsndocService().updateDataAfterSubDataChanged(getInfoSetCode(curTabCode), aggVO.getParentVO().getPk_psndoc());
        
        // ��������VO
        return updateMainVOAndReturnAll(aggVO, curTabCode, saveData.getClass(), getCondition(saveData));
    }
    
    @Override
    public void addToBlacklist(PsndocAggVO vo, String addReason, String pk_org) throws BusinessException
    {
        if (HiSQLHelper.isInJob(vo))
        {
            throw new ValidationException(ResHelper.getString("6009tran", "06009tran0138")/*
                                                                                           * @res
                                                                                           * "ֻ����ְ��Ա���ܼ����������"
                                                                                           */);
        }
        
        String pk_psndoc = vo.getParentVO().getPk_psndoc();
        PsndocVO psn = (PsndocVO) getRetrieve().retrieveByPk(null, PsndocVO.class, pk_psndoc);
        if (psn != null && psn.getDie_date() != null && psn.getDie_remark() != null)
        {
            throw new ValidationException(ResHelper.getString("6009tran", "06009tran0139")/*
                                                                                           * @res
                                                                                           * "��ǰѡ�����Ա�Ѿ�����,���ܼ��������."
                                                                                           */);
        }
        
        BlacklistVO bvo = new BlacklistVO();
        bvo.setPk_group(PubEnv.getPk_group());
        bvo.setPk_org(pk_org);
        bvo.setPk_psndoc(pk_psndoc);
        bvo.setPsnname(psn.getName());
        bvo.setPsnname2(psn.getName2());
        bvo.setPsnname3(psn.getName3());
        bvo.setPsnname4(psn.getName4());
        bvo.setPsnname5(psn.getName5());
        bvo.setPsnname6(psn.getName6());
        bvo.setPsncode(psn.getPk_psndoc());
        if (StringUtils.isNotBlank(psn.getId()) && StringUtils.isNotBlank(psn.getIdtype()))
        {
            bvo.setId(psn.getId());
            bvo.setIdtype(psn.getIdtype());
        }
        else
        {
            throw new ValidationException(ResHelper.getString("6009tran", "X6009tran0061")/*
                                                                                           * @res
                                                                                           * "��ǰѡ�����Ա֤�����ͻ�֤����Ϊ��,���ܼ��������.��ά��ֵ���ٲ�����"
                                                                                           */);
        }
        
        bvo.setSex(psn.getSex());
        bvo.setBirthday(psn.getBirthdate());
        bvo.setDelflag(UFBoolean.FALSE);
        bvo.setCode(psn.getCode());
        bvo.setStatus(VOStatus.NEW);
        bvo.setComefrom(1);// ����ϵͳ
        bvo.setCause(addReason);
        bvo.setPermanentres(vo.getParentVO().getPermanreside());
        AggBlacklistVO agg = new AggBlacklistVO();
        agg.setParentVO(bvo);
        NCLocator.getInstance().lookup(IBlacklistManageService.class).insert(agg);
    }
    
    @Override
    public PsndocAggVO[] fillSubData(PsndocAggVO[] aggs, String[] pkPsnorgs, String tabName) throws BusinessException
    {
        Class className = getTabClass(tabName);
        for (int i = 0; i < aggs.length; i++)
        {
            if (TRNConst.Table_NAME_PART.equals(tabName))
            {
                // ��ְ��һ��
                PartTimeVO[] part =
                    getServiceTemplate().queryByCondition(PartTimeVO.class,
                        " pk_psnorg = '" + pkPsnorgs[i] + "' and assgid >1 and lastflag = 'Y' and endflag ='N' ");
                if (part == null || part.length == 0)
                {
                    continue;
                }
                aggs[i].setTableVO(PartTimeVO.getDefaultTableName(), part);
            }
            else
            {
                Integer assgid = TRNConst.Table_NAME_DEPTCHG.equals(tabName) || TRNConst.Table_NAME_DIMISSION.equals(tabName) ? 1 : null;
                SuperVO lastvo = getLastVO(className, pkPsnorgs[i], assgid);
                if (lastvo != null)
                {
                    SuperVO[] vos = (SuperVO[]) Array.newInstance(lastvo.getClass(), 1);
                    vos[0] = lastvo;
                    aggs[i].setTableVO(getInfoSetCode(tabName), vos);
                }
            }
        }
        return aggs;
    }
    
    @Override
    public void batchUpdate(PsndocAggVO[] aggs, String[] pkPsnorgs, String tabName, Hashtable<String, Object> result, LoginContext context)
            throws BusinessException
    {
        Class className = getTabClass(tabName);
        for (int i = 0; i < aggs.length; i++)
        {
            String pk_psndoc = aggs[i].getParentVO().getPk_psndoc();
            // String pk_psnorg =
            // getIPersonRecordService().getPsnorgByPsndoc(pk_psndoc);
            if (TRNConst.Table_NAME_PART.equals(tabName))
            {
                // ��ְ��һ��
                PartTimeVO[] part =
                    getServiceTemplate().queryByCondition(PartTimeVO.class,
                        " pk_psnorg = '" + pkPsnorgs[i] + "' and assgid >1 and lastflag = 'Y' and endflag ='N' ");
                ArrayList<PartTimeVO> partClones = new ArrayList<PartTimeVO>();
                if (part == null || part.length == 0)
                {
                    continue;
                }
                for (PartTimeVO pt : part)
                {
                    if (result.get(PartTimeVO.ENDDATE) != null
                        && pt.getBegindate().afterDate((UFLiteralDate) result.get(PartTimeVO.ENDDATE)))
                    {
                        continue;
                    }
                    fireEvent(pt, null, context.getPk_org(), HICommonValue.PARTSOURCEID, IEventType.TYPE_UPDATE_BEFORE);
                    PartTimeVO vo = (PartTimeVO) pt.clone();
                    partClones.add(vo);
                    for (String key : result.keySet())
                    {
                        vo.setAttributeValue(key, result.get(key));
                    }
                    // ��������
                    vo = getServiceTemplate().update(vo, true);
                    // �޸�н������
                    fireEvent(pt, vo, context.getPk_org(), HICommonValue.PARTSOURCEID, IEventType.TYPE_UPDATE_AFTER);
                    HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
                }
                // ��ְ��ʱ�õ�һ��
                getIPsndocService().updateDataAfterSubDataChanged(PartTimeVO.getDefaultTableName(), pk_psndoc);
                // ��¼�ڵ������޸�֮��ͬ��������¼����heqiaoa 2014-12-11
                updateWork(partClones.toArray(new PartTimeVO[0]));
            }
            else
            {
                Integer assgid = TRNConst.Table_NAME_DEPTCHG.equals(tabName) || TRNConst.Table_NAME_DIMISSION.equals(tabName) ? 1 : null;
                SuperVO lastvo = getLastVO(className, pkPsnorgs[i], assgid);
                if (lastvo != null)
                {
                    if (lastvo instanceof TrialVO)
                    {
                        if (result.get(TrialVO.ENDDATE) != null
                            && ((TrialVO) lastvo).getBegindate().afterDate((UFLiteralDate) result.get(TrialVO.ENDDATE)))
                        {
                            continue;
                        }
                    }
                    // 3)����н������-�޸�ί��
                    if (TRNConst.Table_NAME_DEPTCHG.equals(tabName) || TRNConst.Table_NAME_DIMISSION.equals(tabName))
                    {
                        fireEvent((PsnJobVO) lastvo, null, context.getPk_org(), HICommonValue.PSNJOBSOURCEID, IEventType.TYPE_UPDATE_BEFORE);
                    }
                    SuperVO vo = (SuperVO) lastvo.clone();
                    for (String key : result.keySet())
                    {
                        vo.setAttributeValue(key, result.get(key));
                    }
                    vo = getServiceTemplate().update(vo, true);
                    getIPsndocService().updateDataAfterSubDataChanged(getInfoSetCode(tabName), pk_psndoc);
                    // 3)����н������-�޸�ί��
                    if (TRNConst.Table_NAME_DEPTCHG.equals(tabName) || TRNConst.Table_NAME_DIMISSION.equals(tabName))
                    {
                        fireEvent((PsnJobVO) lastvo, (PsnJobVO) vo, context.getPk_org(), HICommonValue.PSNJOBSOURCEID,
                            IEventType.TYPE_UPDATE_AFTER);
                        HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
                    }
                }
            }
        }
        // ͬ������
        HiCacheUtils.synCache(PsndocVO.getDefaultTableName(), PsnJobVO.getDefaultTableName(), PsnOrgVO.getDefaultTableName());
    }
    
    private void checkPsnjob(String pkPsnjob, UFLiteralDate execDate, String billCode) throws BusinessException
    {
        PsnJobVO psn = getServiceTemplate().queryByPk(PsnJobVO.class, pkPsnjob);
        if (psn == null)
        {
            throw new BusinessException(ResHelper.getString("6009tran", "06009tran0140"/*
                                                                                        * @res
                                                                                        * "����{0}�е���Ա�Ĺ�����¼��ɾ��,����ִ��"
                                                                                        */, billCode));
        }
        String pk_psnorg = psn.getPk_psnorg();
        Integer assgid = psn.getAssgid();
        PsnJobVO last = getLastVO(PsnJobVO.class, pk_psnorg, assgid);
        if (last == null)
        {
            throw new BusinessException(ResHelper.getString("6009tran", "06009tran0140"/*
                                                                                        * @res
                                                                                        * "����{0}�е���Ա�Ĺ�����¼��ɾ��,����ִ��"
                                                                                        */, billCode));
        }
        if (last.getTrnsevent() != null && (last.getTrnsevent() == 4 || last.getTrnsevent() == 5))
        {
            throw new BusinessException(ResHelper.getString("6009tran", "06009tran0141"/*
                                                                                        * @res
                                                                                        * "����{0}�е���Ա�Ѿ���ְ,����ִ��"
                                                                                        */, billCode));
        }
        if (last.getBegindate() == null)
        {
            throw new BusinessException(ResHelper.getString("6009tran", "06009tran0142"/*
                                                                                        * @res
                                                                                        * "����{0}�е���Ա�Ĺ�����¼��ʼ����Ϊ��,����ִ��"
                                                                                        */, billCode));
        }
        if (last.getBegindate().compareTo(execDate) >= 0)
        {
            throw new BusinessException(ResHelper.getString("6009tran", "06009tran0143"/*
                                                                                        * @res
                                                                                        * "����{0}�е���Ա�����¹�����¼��ʼ�������ڻ���ڵ��ݵ���Ч����,����ִ��"
                                                                                        */, billCode));
        }
    }
    
    /**
     * ���ݵ�������һ��������¼
     * 
     * @param bill
     * @return PsnJobVO
     * @throws BusinessException
     */
    private PsnJobVO createNewPsnjob(StapplyVO bill) throws BusinessException
    {
        // ��ǰ��ִ�е��䵥������ְ��
        boolean isTrans = TRNConst.BUSITYPE_TRANSITION.equals(bill.getPk_billtype());
        
        PsnJobVO psnjob = new PsnJobVO();
        // �õ���һ����¼
        PsnJobVO lastvo = getLastVO(PsnJobVO.class, bill.getPk_psnorg(), 1);
        
        psnjob.setPsntype(0);
        psnjob.setAssgid(1);
        psnjob.setBegindate(bill.getEffectdate());
        psnjob.setEndflag(isTrans ? UFBoolean.FALSE : UFBoolean.TRUE);
        psnjob.setIsmainjob(UFBoolean.TRUE);
        psnjob.setLastflag(UFBoolean.TRUE);
        psnjob.setPk_hrgroup(bill.getPk_group());
        psnjob.setPk_group(bill.getPk_group());
        psnjob.setPk_hrorg(bill.getPk_hi_org());// �����������֯
        psnjob.setPk_psndoc(bill.getPk_psndoc());
        psnjob.setPk_psnorg(bill.getPk_psnorg());
        psnjob.setRecordnum(0);
        psnjob.setShoworder(9999999);
        psnjob.setTrnsevent(isTrans ? (Integer) ((MDEnum) TrnseventEnum.TRANS).value() : (Integer) ((MDEnum) TrnseventEnum.DISMISSION)
            .value());
        // psnjob.setPoststat(isTrans ? UFBoolean.TRUE : UFBoolean.FALSE);
        psnjob.setTrnstype(bill.getPk_trnstype());
        psnjob.setTrnsreason(bill.getSreason());
        psnjob.setTrial_flag(UFBoolean.FALSE);
        // Ա����Ҫ�����Ƿ��Զ����ɸ�ֵ��ʹ����һ����Ա����
        psnjob.setClerkcode(getClerkcode(bill.getPk_group(), bill.getPk_hi_org(), bill.getPk_psnorg()));
        // ��������Ŀ��Ӧ���ֶθ�ֵ�����������ֵ��ʹ�õ����е�ֵ����ʹ����һ����¼��ֵ
        
        // String[] codes = { StapplyVO.NEWPK_JOB, StapplyVO.NEWSERIES,
        // StapplyVO.NEWPK_JOBGRADE, StapplyVO.NEWPK_JOBRANK,
        // StapplyVO.NEWPK_POST,
        // StapplyVO.NEWPK_POSTSERIES };
        for (String name : bill.getAttributeNames())
        {
            // ��λ��ְ�����Ϣ����ǰһ��������
            if (name.startsWith("new"))
            {
                Object value = bill.getAttributeValue(name);
                // modify at 20121011 ����Ŀû�����õ� ����ʹ��ǰһ������Ϣ
                // if (value == null && !ArrayUtils.contains(codes, name))
                // {
                // value = lastvo != null ?
                // lastvo.getAttributeValue(name.substring(3)) : null;
                // }
                psnjob.setAttributeValue(name.substring(3), value);
            }
        }
        
        UFBoolean blPoststate = UFBoolean.FALSE;
        if (isTrans)
        {
            // �Ƿ��ڸ� �ڵ�������ʱҪ���⴦��
            TrnTransItemVO[] tempvos =
                TrnDelegator.getIItemSetQueryService().queryItemSetByOrg(TRNConst.TRNSITEM_BEANID, bill.getPk_group(), bill.getPk_org(),
                    bill.getPk_trnstype());
            TrnTransItemVO vo = null;
            for (int i = 0; tempvos != null && i < tempvos.length; i++)
            {
                if (!"newpoststat".equals(tempvos[i].getItemkey()))
                {
                    continue;
                }
                vo = tempvos[i];
            }
            if (vo == null)
            {
                // ������Ŀ�У�û���Ƿ��ڸڣ�ʹ����һ������Ϣ
                blPoststate = lastvo.getPoststat();
            }
            else
            {
                if (vo.getIsedit() != null && vo.getIsedit().booleanValue())
                {
                    // ������Ŀ����,���ҿ��Ե���,ʹ�õ����е�
                    blPoststate = bill.getNewpoststat();
                }
                else
                {
                    // ���ܵ��� ʹ�ù�����¼�е�
                    blPoststate = lastvo.getPoststat();
                }
            }
        }
        psnjob.setPoststat(blPoststate);
        
        // ������Դ�������&����
        psnjob.setOribilltype(bill.getPk_billtype());
        psnjob.setOribillpk(bill.getPk_hi_stapply());
        return psnjob;
    }
    
    private TrialVO createNewTrial(StapplyVO bill, PsnJobVO psnjob)
    {
        TrialVO trial = new TrialVO();
        trial.setPk_group(psnjob.getPk_group());
        trial.setPk_hrorg(psnjob.getPk_hrorg());
        trial.setPk_org(psnjob.getPk_org());
        trial.setPk_psndoc(psnjob.getPk_psndoc());
        trial.setPk_psnjob(psnjob.getPk_psnjob());
        trial.setPk_psnorg(psnjob.getPk_psnorg());
        trial.setAssgid(1);
        trial.setLastflag(UFBoolean.TRUE);
        trial.setRecordnum(0);
        trial.setTrial_type(2);
        trial.setBegindate(bill.getTrialbegindate());
        trial.setEndflag(UFBoolean.FALSE);
        trial.setEnddate(bill.getTrialenddate());
        return trial;
    }
    
    @Override
    public PsndocAggVO deleteSubRecord(PsndocAggVO aggVO, String curTabCode, String pk_hrorg) throws BusinessException
    {
        // ����һ��ʼ�ͼ�鲢����ts,���ts���Ծ����쳣
        // �汾У�飨ʱ���У�飩
        BDVersionValidationUtil.validateSuperVO(aggVO.getParentVO());
        SuperVO delData = aggVO.getTableVO(curTabCode)[0];
        SuperVO preVO = getPreVO(delData);
        if (preVO != null)
        {
            if (delData.getAttributeValue("lastflag") != null && ((UFBoolean) delData.getAttributeValue("lastflag")).booleanValue())
            {
                // ���ɾ���������һ�� ��preVO��lastflag��ΪY
                preVO.setAttributeValue("lastflag", UFBoolean.TRUE);
            }
            else
            {
                preVO.setAttributeValue("lastflag", UFBoolean.FALSE);
            }
            
        }
        
        if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode))
        {
            // ��ְ��¼ɾ�� �����¼�
            fireEvent((PsnJobVO) delData, (PsnJobVO) preVO, pk_hrorg, HICommonValue.PSNJOBSOURCEID, IEventType.TYPE_DELETE_BEFORE);
        }
        if (TRNConst.Table_NAME_PART.equals(curTabCode))
        {
            // ��ְ��¼ɾ�� �����¼�
            fireEvent((PsnJobVO) delData, (PsnJobVO) preVO, pk_hrorg, HICommonValue.PARTSOURCEID, IEventType.TYPE_DELETE_BEFORE);
        }
        
        getServiceTemplate().delete(delData);
        String cond = "";
        if (delData instanceof PsnChgVO || delData instanceof RetireVO || delData instanceof TrialVO)
        {
            cond = " pk_psnorg = '" + delData.getAttributeValue("pk_psnorg") + "' ";
        }
        else if (delData instanceof PartTimeVO)
        {
            cond = " pk_psnorg = '" + delData.getAttributeValue("pk_psnorg") + "'  and assgid = " + ((PartTimeVO) delData).getAssgid();
        }
        else
        {
            cond = " pk_psnorg = '" + delData.getAttributeValue("pk_psnorg") + "'  and assgid = 1 ";
        }
        SuperVO[] dbVOs = getServiceTemplate().queryByCondition(delData.getClass(), cond + "  order by recordnum ");
        if (null != dbVOs)
        {
            getIPersonRecordService().updateAllRecordnumAndLastflag(dbVOs);
        }
        if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode))
        {
            PsnJobVO lastvo = (PsnJobVO) dbVOs[0];
            // ���µ�һ��Ҫδ����,ͬ��������Ϣ
            lastvo.setEndflag(UFBoolean.FALSE);
            lastvo.setEnddate(null);
            lastvo.setPoststat(UFBoolean.TRUE);
            getIPersonRecordService().checkDeptPostCanceled(lastvo);
            TrialVO trial = getLastVO(TrialVO.class, lastvo.getPk_psnorg(), null);
            if (trial != null && trial.getEndflag() != null && !trial.getEndflag().booleanValue())
            {
                lastvo.setTrial_flag(UFBoolean.TRUE);
                lastvo.setTrial_type(trial.getTrial_type());
            }
            else
            {
                lastvo.setTrial_flag(UFBoolean.FALSE);
                lastvo.setTrial_type(null);
            }
            lastvo.setPk_hrorg(pk_hrorg);// ͬ��HR��֯
            // getServiceTemplate().update(lastvo, false);
            NCLocator.getInstance().lookup(IPersistenceUpdate.class).updateVO(null, lastvo, null, null);
            // ������¼ɾ����,ͬ��bd_psndoc.pk_org
            PsnOrgVO orgVO = getServiceTemplate().queryByPk(PsnOrgVO.class, lastvo.getPk_psnorg());
            if (orgVO != null && orgVO.getLastflag() != null && orgVO.getLastflag().booleanValue())
            {
                // ������֯��ϵͬ��������ְ��֯
                getIPersonRecordService().synPkorgOfPsndoc(lastvo.getPk_org(), lastvo.getPk_psndoc());
            }
            // ͬ��psnorg��HR��֯
            getIPersonRecordService().synPkhrorgOfPsnorg(lastvo.getPk_psnorg(), lastvo.getPk_hrorg());
        }
        if (delData instanceof TrialVO)
        {
            if (dbVOs == null || dbVOs.length == 0)
            {
                // ɾ����û��ʹ�ü�¼ ��������µĹ�����¼��������Ϣ
                setTrialinfo(((TrialVO) delData).getPk_psnorg(), UFBoolean.FALSE, null);
            }
            else
            {
                // ��һ����¼����lastflag='Y'���Ҹ������¹�����¼��������Ϣ
                UFBoolean endflag = ((TrialVO) dbVOs[0]).getEndflag();
                if (endflag == null || !endflag.booleanValue())
                {
                    setTrialinfo(((TrialVO) delData).getPk_psnorg(), UFBoolean.TRUE, ((TrialVO) dbVOs[0]).getTrial_type());
                }
                else
                {
                    setTrialinfo(((TrialVO) delData).getPk_psnorg(), UFBoolean.FALSE, null);
                }
            }
        }
        if (delData instanceof PsnJobVO)
        {
            // ɾ����ְ��ְҪɾ����Ӧ��������¼
            String pk_psnjob = ((PsnJobVO) delData).getPk_psnjob();
            String pk_psndoc = ((PsnJobVO) delData).getPk_psndoc();
            deleteWork(pk_psnjob, pk_psndoc);
        }
        if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode))
        {
            // ��ְ��¼ɾ�� �����¼�
            fireEvent((PsnJobVO) delData, (PsnJobVO) preVO, pk_hrorg, HICommonValue.PSNJOBSOURCEID, IEventType.TYPE_DELETE_AFTER);
            HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
        }
        if (TRNConst.Table_NAME_PART.equals(curTabCode))
        {
            // ��ְ��¼ɾ�� �����¼�
            fireEvent((PsnJobVO) delData, (PsnJobVO) preVO, pk_hrorg, HICommonValue.PARTSOURCEID, IEventType.TYPE_DELETE_AFTER);
            HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
        }
        // ͬ��������Ϣ
        getIPsndocService().updateDataAfterSubDataChanged(getInfoSetCode(curTabCode), aggVO.getParentVO().getPk_psndoc());
        
        // ͬ������Ⱥ�M��Ϣ
        updateDataAfterDeptChanged(aggVO.getParentVO().getPk_psndoc());
        return updateMainVOAndReturnAll(aggVO, curTabCode, delData.getClass(), getCondition(delData));
    }
    
    /**
     * ɾ��ʱ,ɾ����������
     */
    private void deleteWork(String pkPsnjob, String pkPsndoc) throws BusinessException
    {
        WorkVO[] work = getServiceTemplate().queryByCondition(WorkVO.class, " pk_psnjob = '" + (pkPsnjob == null ? "" : pkPsnjob) + "' ");
        if (work == null || work.length == 0)
        {
            return;
        }
        for (WorkVO vo : work)
        {
            getServiceTemplate().delete(vo);
        }
        WorkVO[] dbVO = getServiceTemplate().queryByCondition(WorkVO.class, " pk_psndoc = '" + pkPsndoc + "' order by recordnum ");
        getIPersonRecordService().updateAllRecordnumAndLastflag(dbVO);
        // ɾ��������ͬ����Ա������Ϣ
        getIPsndocService().updateDataAfterSubDataChanged(WorkVO.getDefaultTableName(), pkPsndoc);
    }
    
    @Override
    public PsndocAggVO dieOperation(boolean isInjob, boolean isPsnclChanged, LoginContext context, PsndocVO vo, String resourceID,
            String curTabCode, boolean isDisablePsn) throws BusinessException
    {
        PsndocAggVO aggVO =
            getPassawayService().dieOperation(isInjob, isPsnclChanged, context, vo, HICommonValue.PSNJOBSOURCEID, isDisablePsn);
        if (isInjob)
        {
            // ��ְ��Ա Ҫ�����ؼ���Ա
            stopKeyPsn(vo.getPsnJobVO().getPk_psnjob(), vo.getDie_date());
        }
        Class cls = null;
        String cond = "";
        String pk_psnorg = aggVO.getParentVO().getPsnOrgVO().getPk_psnorg();
        if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode))
        {
            cls = PsnJobVO.class;
            cond = " pk_psnorg = '" + pk_psnorg + "' and assgid = 1 order by recordnum desc ";
        }
        else if (TRNConst.Table_NAME_PSNCHG.equals(curTabCode))
        {
            cls = PsnChgVO.class;
            cond = " pk_psnorg = '" + pk_psnorg + "'  order by recordnum desc ";
        }
        else
        {
            cls = RetireVO.class;
            cond = " pk_psnorg = '" + pk_psnorg + "'  order by recordnum desc ";
        }
        aggVO = updateMainVOAndReturnAll(aggVO, curTabCode, cls, cond);
        return aggVO;
    }
    
    /**
     * �����¼�
     * 
     * @param before
     * @param after
     * @param pk_hrorg
     * @param sourceID
     * @param eventType
     * @throws BusinessException
     */
    private void fireEvent(PsnJobVO before, PsnJobVO after, String pk_hrorg, String sourceID, String eventType) throws BusinessException
    {
        HiEventValueObject.fireEvent(before, after, pk_hrorg, sourceID, eventType);
    }
    
    private String getClerkcode(String pkGroup, String pkOrg, String pkPsnorg) throws BusinessException
    {
        // ��Ա�䶯���Զ�����Ա����
        PsnJobVO last = getLastVO(PsnJobVO.class, pkPsnorg, 1);
        if (last == null)
        {
            return "_";
        }
        return last.getClerkcode();
        // BillCodeContext bcc =
        // HiCacheUtils.getBillCodeContext(HICommonValue.NBCR_PSNDOC_CLERKCODE,
        // pkGroup, pkOrg);
        // if (bcc == null) {
        // // ������Զ�����,��ʹ����һ���ı���
        // PsnJobVO last = getLastVO(PsnJobVO.class, pkPsnorg, 1);
        // if (last == null) {
        // return "_";
        // }
        // return last.getClerkcode();
        // }
        // return
        // NCLocator.getInstance().lookup(IHrBillCode.class).getBillCode(HICommonValue.NBCR_PSNDOC_CLERKCODE,
        // pkGroup, pkOrg);
    }
    
    private String getCondition(SuperVO saveData)
    {
        String strWhere = " pk_psnorg = '" + saveData.getAttributeValue("pk_psnorg") + "' ";
        String strOrder = " order by recordnum desc ";
        if (saveData instanceof PartTimeVO)
        {
            return strWhere + " and assgid > 1   order by assgid ,recordnum desc ";
        }
        else if (saveData instanceof PsnJobVO)
        {
            return strWhere + " and assgid = 1 " + strOrder;
        }
        else if (saveData instanceof RetireVO || saveData instanceof TrialVO || saveData instanceof PsnChgVO)
        {
            return strWhere + strOrder;
        }
        return " 3 = 3 ";
    }
    
    private String getInfoSetCode(String tabCode)
    {
        if (TRNConst.Table_NAME_DEPTCHG.equals(tabCode) || TRNConst.Table_NAME_DIMISSION.equals(tabCode))
        {
            return PsnJobVO.getDefaultTableName();
        }
        else if (TRNConst.Table_NAME_PART.equals(tabCode))
        {
            return PartTimeVO.getDefaultTableName();
        }
        else if (TRNConst.Table_NAME_PSNCHG.equals(tabCode))
        {
            return PsnChgVO.getDefaultTableName();
        }
        else if (TRNConst.Table_NAME_RETIRE.equals(tabCode))
        {
            return RetireVO.getDefaultTableName();
        }
        else if (TRNConst.Table_NAME_TRIAL.equals(tabCode))
        {
            return TrialVO.getDefaultTableName();
        }
        return null;
    }
    
    private IPersonRecordService getIPersonRecordService()
    {
        return NCLocator.getInstance().lookup(IPersonRecordService.class);
    }
    
    private IPsndocService getIPsndocService()
    {
        return NCLocator.getInstance().lookup(IPsndocService.class);
    }
    
    private IPassawayService getPassawayService()
    {
        return NCLocator.getInstance().lookup(IPassawayService.class);
    }
    
    private IPersistenceRetrieve getRetrieve()
    {
        return NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
    }
    
    /**
     * ������֯��ϵ��������ְID�õ�ĳ�Ӽ������¼�¼
     * 
     * @param <T>
     * @param className
     * @param pk_psnorg
     * @param assgid
     * @return < T extends SuperVO > T
     * @throws BusinessException
     */
    private <T extends SuperVO> T getLastVO(Class<T> className, String pk_psnorg, Integer assgid) throws BusinessException
    {
        return getIPersonRecordService().getLastVO(className, pk_psnorg, assgid);
    }
    
    @Override
    public int getMaxAssgId(String pkPsnorg) throws BusinessException
    {
        String sql = " select max(assgid) as assgid from hi_psnjob where pk_psnorg ='" + pkPsnorg + "' ";
        HashMap hm = (HashMap) NCLocator.getInstance().lookup(IPersistenceHome.class).executeQuery(sql, new MapProcessor());
        Integer i = (Integer) hm.get("assgid");
        if (i != null && i >= 2)
        {
            return i + 1;
        }
        return 2;
    }
    
    /**
     * ����Ҫɾ�����ݲ�ѯ����һ������
     */
    private SuperVO getPreVO(SuperVO delData) throws BusinessException
    {
        if (!(delData instanceof PsnJobVO))
        {
            // ���Ǽ�ְ����ְ������һ��
            return null;
        }
        PsnJobVO vo = (PsnJobVO) delData;
        PsnJobVO[] preVO =
            getServiceTemplate().queryByCondition(
                PsnJobVO.class,
                " pk_psnorg = '" + vo.getPk_psnorg() + "' and assgid = " + vo.getAssgid().intValue() + "  and recordnum > "
                    + vo.getRecordnum().intValue() + "  order by recordnum ");
        if (preVO != null && preVO.length > 0)
        {
            return preVO[0];
        }
        return null;
    }
    
    private SimpleDocServiceTemplate getServiceTemplate()
    {
        SimpleDocServiceTemplate service = new SimpleDocServiceTemplate(DOC_NAME);
        service.setLazyLoad(true);
        service.setBdReferenceChecker(BDReferenceChecker.getInstance(new String[]{"hr_relation_psn"}));
        return service;
    }
    
    private <T extends SuperVO> Class getTabClass(String tabName)
    {
        if (TRNConst.Table_NAME_DEPTCHG.equals(tabName) || TRNConst.Table_NAME_DIMISSION.equals(tabName))
        {
            return PsnJobVO.class;
        }
        else if (TRNConst.Table_NAME_PART.equals(tabName))
        {
            return PartTimeVO.class;
        }
        else if (TRNConst.Table_NAME_PSNCHG.equals(tabName))
        {
            return PsnChgVO.class;
        }
        else if (TRNConst.Table_NAME_RETIRE.equals(tabName))
        {
            return RetireVO.class;
        }
        else if (TRNConst.Table_NAME_TRIAL.equals(tabName))
        {
            return TrialVO.class;
        }
        return null;
    }
    
    @Override
    public boolean hasEntryTrail(PsndocAggVO selData) throws BusinessException
    {
        PsnJobVO psnjob = selData.getParentVO().getPsnJobVO();
        // �ҵ���ǰ��֯��ϵ�����µ����ü�¼
        TrialVO trial = getLastVO(TrialVO.class, psnjob.getPk_psnorg(), null);
        if (trial == null)
        {
            return false;
        }
        // δ��������ְ����
        return !trial.getEndflag().booleanValue();
    }
    
    @Override
    public PsndocAggVO insertSubRecord(PsndocAggVO aggVO, String curTabCode) throws BusinessException
    {
        // ����һ��ʼ�ͼ�鲢����ts,���ts���Ծ����쳣
        // �汾У�飨ʱ���У�飩
        BDVersionValidationUtil.validateSuperVO(aggVO.getParentVO());
        SuperVO saveData = aggVO.getTableVO(curTabCode)[0];
        // ���²����¼֮ǰ�ļ�¼��lastflag��recordnum
        String cond = " pk_psnorg = '" + saveData.getAttributeValue("pk_psnorg") + "' ";
        if (saveData instanceof PsnJobVO)
        {
            cond += "  and assgid = 1 ";
        }
        SuperVO[] hisVOs =
            getServiceTemplate().queryByCondition(saveData.getClass(),
                cond + " and recordnum >= " + saveData.getAttributeValue("recordnum"));
        getIPersonRecordService().updateRecordnumAndLastflag(hisVOs);
        
        if (saveData instanceof PsnJobVO)
        {
            // ������ְ���ְ��¼,У��Ա����
            ((PsnJobVO) saveData).setClerkcode((((PsnJobVO) saveData)).getClerkcode().trim());
            checkClerkCodeUnique((PsnJobVO) saveData);
            getIPersonRecordService().checkDeptPostCanceled((PsnJobVO) saveData);
        }
        // ����һ���¼�¼
        getServiceTemplate().insert(saveData);
        // ������Ա������Ϣ
        getIPsndocService().updateDataAfterSubDataChanged(getInfoSetCode(curTabCode), aggVO.getParentVO().getPk_psndoc());
        // ͬ������Ⱥ�M��Ϣ
        updateDataAfterDeptChanged(aggVO.getParentVO().getPk_psndoc());
        // ����������¼���������м�¼
        return updateMainVOAndReturnAll(aggVO, curTabCode, saveData.getClass(), getCondition(saveData));
    }
    
    @Override
    public Object perfromStaff_RequiresNew(AggStapply bill, boolean isqueryctrt) throws BusinessException
    {
        Object obj = null;
        // ����Ƿ���δ��Ч�Ľ������ֹ��ͬ������У�����֪ͨ������ִ��ʧ�ܡ�
        String message = null;
        StapplyVO billvo = (StapplyVO) bill.getParentVO();
        if (isqueryctrt)// �������Զ�ִ��������������ڻ��������У�����û����ѯ�Ƿ��к�ͬ
        {
            if ((billvo.getIsend() != null && billvo.getIsend().booleanValue())
                || (billvo.getIsrelease() != null && billvo.getIsrelease().booleanValue()))
            {
                message = ResHelper.getString("6009tran", "06009tran0215")/*
                                                                           * * @res *
                                                                           * "����δ��Ч�ĺ�ͬ��¼������ִ�У��봦��������ֶ�ִ�е��ݣ�"
                                                                           */;
            }
        }
        else
        {// �ֶ�ִ��ʱ����ȥ��ѯ
            message = checkCtrt(bill, HICommonValue.msgcode_dimission);
        }
        if (StringUtils.isNotBlank(message))
        {
            obj = message;
            return obj;
        }
        
        // 2)У����ְ��¼ʱ����ִ��ʱ��Ĺ�ϵ/��ʼʱ���Ƿ�Ϊ��/��ʼ�����Ƿ����ִ������/�������Ƿ��Ѿ���ְ
        checkPsnjob(((StapplyVO) bill.getParentVO()).getPk_psnjob(), ((StapplyVO) bill.getParentVO()).getEffectdate(),
            ((StapplyVO) bill.getParentVO()).getBill_code());
        // 3)����ִ��begin
        
        // 3-2)����һ��������¼
        PsnJobVO newVO = createNewPsnjob((StapplyVO) bill.getParentVO());
        newVO =
            getIPersonRecordService().addNewPsnjob(newVO, ((StapplyVO) bill.getParentVO()).getIfsynwork().booleanValue(),
                (String) bill.getParentVO().getAttributeValue(StapplyVO.PK_HI_ORG),
                (String) bill.getParentVO().getAttributeValue(StapplyVO.PK_HRCM_ORG));
        // 3-3) ͬ��������Ϣ
        if (!newVO.getTrial_flag().booleanValue() && ((StapplyVO) bill.getParentVO()).getTrial_flag().booleanValue())
        {
            // �����ǰû�������ҵ��ݵ����ñ�־Ϊtrue,������һ����λ����,������ʷ��¼
            TrialVO newTrial = createNewTrial((StapplyVO) bill.getParentVO(), newVO);
            TrialVO[] his = getServiceTemplate().queryByCondition(TrialVO.class, " pk_psnorg = '" + newVO.getPk_psnorg() + "' ");
            getIPersonRecordService().updateRecordnumAndLastflag(his);
            getServiceTemplate().insert(newTrial);
            newVO.setTrial_flag(UFBoolean.TRUE);
            newVO.setTrial_type(newTrial.getTrial_type());
            getServiceTemplate().update(newVO, false);
            // ������Ա������Ϣ
            getIPsndocService().updateDataAfterSubDataChanged(TrialVO.getDefaultTableName(),
                ((StapplyVO) bill.getParentVO()).getPk_psndoc());
        }
        // 3-4)������ְ
        if (((StapplyVO) bill.getParentVO()).getIfendpart().booleanValue())
        {
            // ������ǰ��֯��ϵ��δ�����ļ�ְ��¼
            PartTimeVO[] part =
                getServiceTemplate().queryByCondition(
                    PartTimeVO.class,
                    " pk_psnorg = '" + newVO.getPk_psnorg() + "' and assgid > 1 and ( " + SQLHelper.getNullSql("endflag")
                        + " or endflag = 'N' ) ");
            UFLiteralDate enddate = ((StapplyVO) bill.getParentVO()).getEffectdate();
            for (int i = 0; part != null && i < part.length; i++)
            {
                getIPersonRecordService().updateEndflagAndEnddate(part[i], enddate);
                
                if (((StapplyVO) bill.getParentVO()).getIfsynwork().booleanValue())
                {
                    // ͬ����������������ְ��Ӧ�Ĺ�������
                    getIPersonRecordService().endWork(part[i].getPk_psnjob(), enddate.getDateBefore(1));
                }
            }
            // ������Ա������Ϣ
            getIPsndocService().updateDataAfterSubDataChanged(PartTimeVO.getDefaultTableName(),
                ((StapplyVO) bill.getParentVO()).getPk_psndoc());
        }
        
        // 3-5)�������֯����� Ҫ��������ԭ��֯�Ĺؼ���Ա
        int mode = ((StapplyVO) bill.getParentVO()).getStapply_mode();
        if (TRNConst.TRANSMODE_CROSS_IN == mode || TRNConst.TRANSMODE_CROSS_OUT == mode)
        {
            String pk_psnjob = ((StapplyVO) bill.getParentVO()).getPk_psnjob();
            String pk_org = ((StapplyVO) bill.getParentVO()).getPk_old_hi_org();
            
            KeyPsnVO[] keyVOs =
                getServiceTemplate()
                    .queryByCondition(
                        KeyPsnVO.class,
                        " pk_psnorg in (select pk_psnorg from hi_psnjob where pk_psnjob ='"
                            + pk_psnjob
                            + "') and (endflag = 'N' or isnull(endflag,'~') ='~') and pk_keypsn_grp in ( select pk_keypsn_group from hi_keypsn_group where pk_org = '"
                            + pk_org + "' )  ");
            if (keyVOs != null && keyVOs.length > 0)
            {
                for (int i = 0; i < keyVOs.length; i++)
                {
                    keyVOs[i].setEndflag(UFBoolean.TRUE);
                    if (keyVOs[i].getBegindate().afterDate(((StapplyVO) bill.getParentVO()).getEffectdate()))
                    {
                        keyVOs[i].setEnddate(keyVOs[i].getBegindate());
                    }
                    else
                    {
                        keyVOs[i].setEnddate(((StapplyVO) bill.getParentVO()).getEffectdate());
                    }
                    keyVOs[i].setStatus(VOStatus.UPDATED);
                }
                NCLocator.getInstance().lookup(IPersistenceUpdate.class)
                    .updateVOArray(null, keyVOs, new String[]{KeyPsnVO.ENDDATE, KeyPsnVO.ENDFLAG}, null);
            }
            
        }
        
        // ͬ������
        HiCacheUtils.synCache(PsndocVO.getDefaultTableName(), PsnJobVO.getDefaultTableName(), PsnOrgVO.getDefaultTableName());
        
        // 3-1)���µ���״̬Ϊ��ִ��
        bill.getParentVO().setAttributeValue(StapplyVO.APPROVE_STATE, HRConstEnum.EXECUTED);
        
        // ��Ա�䶯ʱ�������Ա���������˺������򣬸��ݲ���������Ա����
        updatePsncode_Apply(((StapplyVO) bill.getParentVO()).getPk_psndoc(), ((StapplyVO) bill.getParentVO()).getPk_hi_org(),
            (StapplyVO) bill.getParentVO());
        
        getServiceTemplate().update(bill, false);
        obj = bill;
        // 3)����ִ��end
        
        return obj;
    }
    
    // ��Ա�䶯ʱ�������Ա���������˺������򣬸��ݲ���������Ա����
    public void updatePsncode_Apply(String pk_psndoc, String pk_hrorg, StapplyVO stapplyvo) throws BusinessException
    {
        // ����Ϊ�ǵ�����²ſ�����Щ
        UFBoolean para = SysinitAccessor.getInstance().getParaBoolean(pk_hrorg, "TRN0005");
        if (para != null && para.booleanValue())
        {
            BillCodeContext billContext =
                NCLocator.getInstance().lookup(IBillcodeManage.class)
                    .getBillCodeContext(HICommonValue.NBCR_PSNDOC_CODE, PubEnv.getPk_group(), pk_hrorg);
            // ��Ա�����������Ϊ�����
            if (billContext != null && !billContext.isPrecode())
            {
                // ������õ��Ǻ���룬����Ա�䶯ʱ����Ҫ�жϱ�������ж����ҵ��ʵ���ֵ�Ƿ����˱仯���������Ƿ�����������Ա����
                HashMap<String, Object> map = getCodeRule_Apply(pk_hrorg, stapplyvo);
                if (map == null)
                {
                    return;
                }
                // boolean ischange = getCodeRule_Apply(pk_hrorg, stapplyvo);
                boolean ischange = (boolean) map.get("ischange");
                if (ischange)
                {
                    PsndocVO[] psndocvo =
                        (PsndocVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                            .retrieveByClause(null, PsndocVO.class, " pk_psndoc = '" + pk_psndoc + "' ");
                    IHrBillCode service = NCLocator.getInstance().lookup(IHrBillCode.class);
                    PsnJobVO psnjobvo = (PsnJobVO) map.get("psnjobvo");
                    String[] strCode =
                        service.getLeveledBillCode(HICommonValue.NBCR_PSNDOC_CODE, PubEnv.getPk_group(), pk_hrorg, psnjobvo, 1);
                    psndocvo[0].setCode(strCode[0]);
                    psndocvo[0].setStatus(VOStatus.UPDATED);
                    NCLocator.getInstance().lookup(IPersistenceUpdate.class).updateVO(null, psndocvo[0], new String[]{PsndocVO.CODE}, null);
                }
            }
        }
    }
    
    // ������õ��Ǻ���룬����Ա�䶯ʱ����Ҫ�жϱ�������ж����ҵ��ʵ���ֵ�Ƿ����˱仯���������Ƿ�����������Ա����
    public HashMap<String, Object> getCodeRule_Apply(String pk_hrorg, StapplyVO stapplyvo) throws BusinessException
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        boolean ischange = false;
        BillCodeRuleVO rulevo = getBillCodeRuleVOFromDB(HICommonValue.NBCR_PSNDOC_CODE, PubEnv.getPk_group(), pk_hrorg);
        BillCodeElemVO[] elems = rulevo.getElems();
        // ���ҵ��ʵ��elemtype=1���ֶ�ץȡ��������Ҫ�Աȱ䶯ǰ���ֵ�������Ƿ�����������Ա����
        if (!ArrayUtils.isEmpty(elems))
        {
            List<String> liststr = new ArrayList<String>();
            for (int i = 0; i < elems.length; i++)
            {
                if (elems[i].getElemtype() == 1)
                {
                    liststr.add(elems[i].getElemvalue());
                }
            }
            String[] str = liststr.toArray(new String[0]);
            if (ArrayUtils.isEmpty(str))
            {// ����ĺ���������û��ѡ��ҵ��ʵ�壬�����һֱ������Ҫ��������
                return null;
            }
            
            PsnJobVO psnjobvo = new PsnJobVO();
            for (int i = 0; i < str.length; i++)
            {
                String name = str[i];
                String newdata =
                    stapplyvo.getAttributeValue("new" + name) == null ? "" : stapplyvo.getAttributeValue("new" + name).toString();
                String olddata =
                    stapplyvo.getAttributeValue("old" + name) == null ? "" : stapplyvo.getAttributeValue("old" + name).toString();
                if (!newdata.equals(olddata))
                {
                    ischange = true;
                    psnjobvo.setAttributeValue(name, newdata);
                }
            }
            
            map.put("ischange", ischange);
            map.put("psnjobvo", psnjobvo);
            
        }
        return map;
    }
    
    /**
     * ����ͬ��Ϣ <br>
     * Created on 2014-3-20 10:02:34<br>
     * @param bill
     * @throws BusinessException
     * @author caiqm
     */
    public String checkCtrt(AggStapply bill, String msg_code) throws BusinessException
    {
        String message = null;
        StapplyVO billvo = (StapplyVO) bill.getParentVO();
        String condition = "pk_psndoc = '" + billvo.getPk_psndoc() + "' and recordnum = 0 and isrefer = 'N' ";
        // ȥ�� and lastflag = 'Y' ��һ����
        CtrtVO[] ctrtVOs =
            (CtrtVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, CtrtVO.class, condition);
        
        if (ctrtVOs != null && !ArrayUtils.isEmpty(ctrtVOs))
        {
            AggStapply[] bills = {bill};
            HiSendMsgHelper.sendMessage1(msg_code, bills, (String) billvo.getAttributeValue(StapplyVO.PK_HI_ORG));
            // throw new BusinessException(ResHelper.getString("6009tran", "06009tran0215")/*
            // * @res
            // * "����δ��Ч�ĺ�ͬ��¼������ִ�У��봦��������ֶ�ִ�е��ݣ�"
            // */);
            message = ResHelper.getString("6009tran", "06009tran0215")/*
                                                                       * * @res *
                                                                       * "����δ��Ч�ĺ�ͬ��¼������ִ�У��봦��������ֶ�ִ�е��ݣ�"
                                                                       */;
        }
        return message;
    }
    
    /**
     * ����ͬ��Ϣ <br>
     * Created on 2014-3-20 10:02:34<br>
     * @param bill
     * @throws BusinessException
     * @author caiqm
     */
    public String checkCtrt(AggStapply[] bill) throws BusinessException
    {
        String message = null;
        StapplyVO[] parentVOs = CommonUtils.getParentVOArrayFromAggVOs(StapplyVO.class, bill);
        String[] pk_psndocs = StringPiecer.getStrArrayDistinct(parentVOs, StapplyVO.PK_PSNDOC);
        
//        boolean flag = false;// ����ҪУ��
//        for (int i = 0; i < parentVOs.length; i++)
//        {
//            if ((parentVOs[i].getIsend() != null && parentVOs[i].getIsend().booleanValue())
//                || (parentVOs[i].getIsrelease() != null && parentVOs[i].getIsrelease().booleanValue()))
//            {// ֻҪ��һ����¼�й�ѡ����ҪУ��
//                flag = true;
//            }
//        }
//        if (!flag)
//        {
//            return message;
//        }
        
        InSQLCreator insql = new InSQLCreator();
        String cond = "pk_psndoc in (" + insql.getInSQL(pk_psndocs) + ") and recordnum = 0 and isrefer = 'N' ";
        CtrtVO[] ctrtVOs = (CtrtVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, CtrtVO.class, cond);
        if (ctrtVOs != null && !ArrayUtils.isEmpty(ctrtVOs))
        {
            message = ResHelper.getString("6009tran", "06009tran0215")/*
                                                                       * * @res *
                                                                       * "����δ��Ч�ĺ�ͬ��¼������ִ�У��봦��������ֶ�ִ�е��ݣ�"
                                                                       */;
        }
        
        return message;
    }
    
    @Override
    public Object perfromTurnOver_RequiresNew(AggStapply bill, boolean isqueryctrt) throws BusinessException
    {
        Object obj = null;
        
        // ����Ƿ���δ��Ч�Ľ������ֹ��ͬ������У�����֪ͨ������ִ��ʧ�ܡ�
        String message = null;
        StapplyVO billvo = (StapplyVO) bill.getParentVO();
        if (isqueryctrt)// �������Զ�ִ��������������ڻ��������У�����û����ѯ�Ƿ��к�ͬ
        {
            if ((billvo.getIsend() != null && billvo.getIsend().booleanValue())
                || (billvo.getIsrelease() != null && billvo.getIsrelease().booleanValue()))
            {
                message = ResHelper.getString("6009tran", "06009tran0215")/*
                                                                           * * @res *
                                                                           * "����δ��Ч�ĺ�ͬ��¼������ִ�У��봦��������ֶ�ִ�е��ݣ�"
                                                                           */;
            }
        }
        else
        {// �ֶ�ִ��ʱ����ȥ��ѯ
            message = checkCtrt(bill, HICommonValue.msgcode_dimission);
        }
        if (StringUtils.isNotBlank(message))
        {
            obj = message;
            return obj;
        }
        
        // 1)ִ��ǰ��ѯ��ԱVO
        PsndocAggVO psndoc = getServiceTemplate().queryByPk(PsndocAggVO.class, ((StapplyVO) bill.getParentVO()).getPk_psndoc(), true);
        // 2)У����ְ��¼�빤��������ʱ����ִ��ʱ��Ĺ�ϵ
        checkPsnjob(((StapplyVO) bill.getParentVO()).getPk_psnjob(), ((StapplyVO) bill.getParentVO()).getEffectdate(),
            ((StapplyVO) bill.getParentVO()).getBill_code());
        // 3)����ִ��
        
        // 3-2)����һ����ְ������¼
        boolean isdisablepsn =
            ((StapplyVO) bill.getParentVO()).getIsdisablepsn() == null ? false : ((StapplyVO) bill.getParentVO()).getIsdisablepsn()
                .booleanValue();
        PsnJobVO dimission = createNewPsnjob((StapplyVO) bill.getParentVO());
        dimission =
            getIPersonRecordService().addNewDimission(dimission, (String) bill.getParentVO().getAttributeValue(StapplyVO.PK_HI_ORG),
                isdisablepsn, (String) bill.getParentVO().getAttributeValue(StapplyVO.PK_HRCM_ORG));
        // 3-3)���������
        if (((StapplyVO) bill.getParentVO()).getIfaddblack() != null && ((StapplyVO) bill.getParentVO()).getIfaddblack().booleanValue())
        {
            addToBlacklist(psndoc, ((StapplyVO) bill.getParentVO()).getAddreason(), ((StapplyVO) bill.getParentVO()).getPk_org());
        }
        
        // 3-4)����������Ա��
        stopKeyPsn(((StapplyVO) bill.getParentVO()).getPk_psnjob(), ((StapplyVO) bill.getParentVO()).getEffectdate());
        
        // 3-1)���µ���״̬Ϊ��ִ��
        bill.getParentVO().setAttributeValue(StapplyVO.APPROVE_STATE, HRConstEnum.EXECUTED);
        
        // ��Ա�䶯ʱ�������Ա���������˺������򣬸��ݲ���������Ա����
        updatePsncode_Apply(((StapplyVO) bill.getParentVO()).getPk_psndoc(), ((StapplyVO) bill.getParentVO()).getPk_hi_org(),
            (StapplyVO) bill.getParentVO());
        
        getServiceTemplate().update(bill, false);
        obj = bill;
        // 3)end
        
        return obj;
    }
    
    @Override
    public PsndocAggVO[] queryOtherPsnInfo(String pk_org, String strWhere, String strOrder) throws BusinessException
    {
        String sql =
            " select " + getBaseAttrSql() + " dept.displayorder , dept.code from bd_psndoc psndoc "
                + " inner join hi_psnjob psnjob on psndoc.pk_psndoc = psnjob.pk_psndoc "
                + " left outer join org_dept dept on psnjob.pk_dept = dept.pk_dept where psnjob.pk_psnjob "
                + " in ( select pk_psnjob from hi_psnjob where 1=1 and pk_psnjob in " + HiSQLHelper.getOtherPsnjobSQL(pk_org)
                + (StringUtils.isBlank(strWhere) ? "" : strWhere) + " )  ";
        sql += " order by " + (StringUtils.isBlank(strOrder) ? " dept.displayorder,dept.code,psnjob.clerkcode" : strOrder);
        return createAggPsndoc(sql);
    }
    
    @Override
    public String[] queryPsnjobPks(String condition, String orderby, String pk_org) throws BusinessException
    {
        HIQueryLogUtils.writeQueryLog("099bdf76-4fc2-4217-beb0-72e7bf4ad8d7", pk_org, "rdsquery");
        
        String sql =
            " select psnjob.pk_psnjob from bd_psndoc psndoc inner join hi_psnjob psnjob on psndoc.pk_psndoc = psnjob.pk_psndoc "
                + " left outer join org_orgs org on psnjob.pk_org = org.pk_org left outer join org_dept dept on psnjob.pk_dept = dept.pk_dept "
                + " where psnjob.pk_psnjob in ( select pk_psnjob from hi_psnjob where 1 = 1 "
                + (StringUtils.isBlank(condition) ? "" : condition) + " and pk_org in(select pk_adminorg from org_admin_enable)" + " ) ";
        
        // ��֯Ȩ��
        String orgSql =
            HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE, "psnjob");
        if (!StringUtils.isBlank(orgSql))
        {
            sql += " and " + orgSql;
        }
        
        // ����Ȩ��
        String deptSql =
            HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, "psnjob");
        if (!StringUtils.isBlank(deptSql))
        {
            sql += " and " + deptSql;
        }
        
        // ��Ա���Ȩ��
        String psnclSql =
            HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_PSNCL, IRefConst.DATAPOWEROPERATION_CODE, "psnjob");
        if (!StringUtils.isBlank(psnclSql))
        {
            sql += " and " + psnclSql;
        }
        
        // ���Ӷ���Դʵ��Ϊ��Աʱ��Ȩ�޿��� heqiaoa 20150505
        String powerSql =
            HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB, IRefConst.DATAPOWEROPERATION_CODE,
                "psnjob");
        if (!StringUtils.isEmpty(powerSql))
        {
            sql += " and " + powerSql;
        }
        
        sql += " order by " + (StringUtils.isBlank(orderby) ? " org.code,dept.displayorder,dept.code,psnjob.clerkcode " : orderby);
        @SuppressWarnings("unchecked")
        List<String> al = (List<String>) new BaseDAO().executeQuery(sql, new StringArrayProcessor());
        if (al.isEmpty())
        {
            return null;
        }
        
        /* ע��ԭ�򣬸о�������˶��� */
        // HashSet<String> pkSet = new HashSet<String>();
        // for (int i = 0; i < al.size(); i++)
        // {
        // pkSet.add((String) al.get(i).get("pk_psnjob"));
        // }
        return al.toArray(new String[0]);
    }
    
    private class StringArrayProcessor extends BaseProcessor
    {
        
        private static final long serialVersionUID = 4148546217899305306L;
        
        @Override
        public Object processResultSet(ResultSet rs) throws SQLException
        {
            List<String> result = new ArrayList<String>();
            while (rs.next())
            {
                result.add(rs.getString(1).trim());
            }
            return result;
        }
    }
    
    @Override
    public PsndocAggVO[] queryPsnInfoAll(String strWhere, String strOrder) throws BusinessException
    {
        String sql =
            " select "
                + getBaseAttrSql()
                + " dept.displayorder,dept.code,org.code from bd_psndoc psndoc inner join hi_psnjob psnjob on psndoc.pk_psndoc = psnjob.pk_psndoc "
                + " left outer join org_orgs org on psnjob.pk_org = org.pk_org left outer join org_dept dept on psnjob.pk_dept = dept.pk_dept "
                + " where psnjob.pk_psnjob in ( select pk_psnjob from hi_psnjob where 1=1 "
                + (StringUtils.isBlank(strWhere) ? "" : strWhere) + " ) ";
        sql += " order by " + (StringUtils.isBlank(strOrder) ? " org.code,dept.displayorder,dept.code,psnjob.clerkcode " : strOrder);
        return createAggPsndoc(sql);
    }
    
    @Override
    public PsndocAggVO[] queryPsnInfoByDeptPk(String pk_dept, String strWhere, String strOrder) throws BusinessException
    {
        String sql =
            " select " + getBaseAttrSql() + " 1 from bd_psndoc psndoc "
                + " inner join hi_psnjob psnjob on psndoc.pk_psndoc = psnjob.pk_psndoc "
                + " where psnjob.pk_psnjob in ( select pk_psnjob from hi_psnjob where pk_dept = '" + pk_dept + "' "
                + (StringUtils.isBlank(strWhere) ? "" : strWhere) + " )  ";
        sql += " order by " + (StringUtils.isBlank(strOrder) ? " psnjob.clerkcode " : strOrder);
        return createAggPsndoc(sql);
    }
    
    @Override
    public PsndocAggVO[] queryPsnInfoByOrgPk(String pk_org, String strWhere, String strOrder) throws BusinessException
    {
        String sql =
            " select " + getBaseAttrSql() + " dept.displayorder,dept.code from bd_psndoc psndoc "
                + " inner join hi_psnjob psnjob on psndoc.pk_psndoc = psnjob.pk_psndoc "
                + " left outer join org_dept dept on psnjob.pk_dept = dept.pk_dept "
                + " where psnjob.pk_psnjob in ( select pk_psnjob from hi_psnjob where pk_org = '" + pk_org + "' "
                + (StringUtils.isBlank(strWhere) ? "" : strWhere) + " ) ";
        sql += " order by " + (StringUtils.isBlank(strOrder) ? " dept.displayorder,dept.code,psnjob.clerkcode " : strOrder);
        return createAggPsndoc(sql);
    }
    
    private PsndocAggVO[] createAggPsndoc(String sql) throws BusinessException
    {
        ArrayList<PsndocVO> psndocs =
            (ArrayList<PsndocVO>) NCLocator.getInstance().lookup(IPersistenceHome.class)
                .executeQuery(sql, new BeanListProcessor(PsndocVO.class));
        if (psndocs == null || psndocs.size() == 0)
        {
            return null;
        }
        PsndocAggVO[] aggvos = new PsndocAggVO[psndocs.size()];
        for (int i = 0; i < aggvos.length; i++)
        {
            aggvos[i] = new PsndocAggVO();
            aggvos[i].setParentVO(psndocs.get(i));
        }
        return aggvos;
    }
    
    private String getBaseAttrSql()
    {
        String[] psnjobAttrs = new PsnJobVO().getAttributeNames();
        String[] psndocAttrs = new PsndocVO().getAttributeNames();
        StringBuffer sb = new StringBuffer();
        for (String attr : psndocAttrs)
        {
            if (PsndocVO.PHOTO.equals(attr) || PsndocVO.PREVIEWPHOTO.equals(attr))
            {
                continue;
            }
            sb.append(" psndoc." + attr + " as bd_psndoc_" + attr + " , ");
        }
        for (String attr : psnjobAttrs)
        {
            sb.append(" psnjob." + attr + " as hi_psnjob_" + attr + " , ");
        }
        return sb.toString();
    }
    
    @Override
    public PsndocAggVO querySubVO(PsndocAggVO aggvo, String[] tabCodes) throws BusinessException
    {
        // ֻ���������֯��ϵ�µĸ��Ӽ�����
        PsndocAggVO retVO = (PsndocAggVO) aggvo.clone();
        String pk_psnorg = aggvo.getParentVO().getPsnJobVO().getPk_psnorg();
        for (String code : tabCodes)
        {
            Class<? extends SuperVO> tableName = null;
            String whereSql = " pk_psnorg = '" + pk_psnorg + "' ";
            String orderSql = " order by recordnum desc ";
            String subCode = code;
            if (TRNConst.Table_NAME_DIMISSION.equals(code) || TRNConst.Table_NAME_DEPTCHG.equals(code))
            {
                tableName = PsnJobVO.class;
                subCode = PsnJobVO.getDefaultTableName();
                whereSql += " and assgid = 1 and ismainjob = 'Y' ";
            }
            else if (TRNConst.Table_NAME_PART.equals(code))
            {
                tableName = PartTimeVO.class;
                subCode = PartTimeVO.getDefaultTableName();
                whereSql += " and assgid > 1 and ismainjob = 'N' ";
                orderSql = " order by assgid ,recordnum desc";
            }
            else if (TRNConst.Table_NAME_PSNCHG.equals(code))
            {
                tableName = PsnChgVO.class;
            }
            else if (TRNConst.Table_NAME_RETIRE.equals(code))
            {
                tableName = RetireVO.class;
            }
            else if (TRNConst.Table_NAME_TRIAL.equals(code))
            {
                tableName = TrialVO.class;
            }
            SuperVO[] vos = getServiceTemplate().queryByCondition_LazyLoad(tableName, whereSql + orderSql);
            if (TRNConst.Table_NAME_PSNCHG.equals(code) || TRNConst.Table_NAME_DEPTCHG.equals(code)
                || TRNConst.Table_NAME_DIMISSION.equals(code))
            {
                vos = getIPsndocService().fillDateFormula(vos);
            }
            retVO.setTableVO(subCode, vos);
        }
        return retVO;
    }
    
    @Override
    public PsndocAggVO savePartchg(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException
    {
        // ����һ��ʼ�ͼ�鲢����ts,���ts���Ծ����쳣
        // �汾У�飨ʱ���У�飩
        BDVersionValidationUtil.validateSuperVO(aggVO.getParentVO());
        PartTimeVO saveData = (PartTimeVO) aggVO.getTableVO(curTabCode)[0];
        saveData.setClerkcode(saveData.getClerkcode().trim());
        PartTimeVO retVO = savePartchgInf(saveData, curTabCode, isSynWork, pk_hrorg);
        // ��������VO
        return updateMainVOAndReturnAll(aggVO, curTabCode, PartTimeVO.class, " pk_psnorg = '" + retVO.getPk_psnorg()
            + "' and assgid > 1 and ismainjob = 'N'  order by assgid ,recordnum desc ");
    }
    
    @Override
    public PartTimeVO savePartchgInf(PartTimeVO partTimeVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException
    {
        return getIPersonRecordService().savePartchgInf(partTimeVO, isSynWork, pk_hrorg);
    }
    
    /**
     * ������һ��������¼����������Ϣ
     * 
     * @param pk_psnorg
     * @param trial_flag
     * @param trial_type
     * @throws BusinessException
     */
    private void setTrialinfo(String pk_psnorg, UFBoolean trial_flag, Integer trial_type) throws BusinessException
    {
        PsnJobVO lastVO = getLastVO(PsnJobVO.class, pk_psnorg, 1);
        if (lastVO != null)
        {
            lastVO.setTrial_flag(trial_flag);
            lastVO.setTrial_type(trial_type);
            getServiceTemplate().update(lastVO, false);
        }
    }
    
    /**
     * ����������¼���������м�¼
     * 
     * @param aggVO
     * @param curTabCode
     * @param tabClass
     * @param qryCondition
     * @return PsndocAggVO
     * @throws BusinessException
     */
    private PsndocAggVO updateMainVOAndReturnAll(PsndocAggVO aggVO, String curTabCode, Class<? extends SuperVO> tabClass,
            String qryCondition) throws BusinessException
    {
        PsndocAggVO agg = new PsndocAggVO();
        PsndocVO newPsn = getServiceTemplate().queryByPk(PsndocVO.class, aggVO.getParentVO().getPk_psndoc(), true);
        PsnJobVO newJob = getServiceTemplate().queryByPk(PsnJobVO.class, aggVO.getParentVO().getPsnJobVO().getPk_psnjob());
        if (newJob == null)
        {
            String pkPsnorg = aggVO.getParentVO().getPsnJobVO().getPk_psnorg();
            newJob = getLastVO(PsnJobVO.class, pkPsnorg, 1);
            // String pkPsnjob =
            // getIPersonRecordService().getPsnjobByPsndoc(aggVO.getParentVO().getPk_psndoc());
            // newJob = getServiceTemplate().queryByPk(PsnJobVO.class,
            // pkPsnjob);
        }
        newPsn.setPsnJobVO(newJob);
        agg.setParentVO(newPsn);
        SuperVO[] vos = getServiceTemplate().queryByCondition(tabClass, qryCondition);
        if (TRNConst.Table_NAME_PSNCHG.equals(curTabCode) || TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)
            || TRNConst.Table_NAME_DIMISSION.equals(curTabCode))
        {
            vos = getIPsndocService().fillDateFormula(vos);
        }
        agg.setTableVO(curTabCode, vos);
        
        // ͬ������
        HiCacheUtils.synCache(PsndocVO.getDefaultTableName(), PsnJobVO.getDefaultTableName(), PsnOrgVO.getDefaultTableName());
        
        // �������м�¼
        return agg;
    }
    
    /**
     * ���·Ǽ�ְ��lastflag��recordnum
     */
    private void updateRdsnumAndLastflag(SuperVO saveData) throws BusinessException
    {
        SuperVO[] his =
            getServiceTemplate().queryByCondition(saveData.getClass(), " pk_psnorg = '" + saveData.getAttributeValue("pk_psnorg") + "' ");
        if (saveData instanceof PsnChgVO || saveData instanceof RetireVO)
        {
            for (int i = 0; his != null && i < his.length; i++)
            {
                UFBoolean value = (UFBoolean) saveData.getAttributeValue("lastflag");
                if (value != null && value.booleanValue())
                {
                    his[i] =
                        getIPersonRecordService().updateEndflagAndEnddate(his[i], (UFLiteralDate) saveData.getAttributeValue("begindate"));
                }
            }
        }
        getIPersonRecordService().updateRecordnumAndLastflag(his);
    }
    
    @Override
    public PsndocAggVO updateSubRecord(PsndocAggVO aggVO, String curTabCode, boolean isSynWork, String pk_hrorg) throws BusinessException
    {
        // ����һ��ʼ�ͼ�鲢����ts,���ts���Ծ����쳣
        // �汾У�飨ʱ���У�飩
        BDVersionValidationUtil.validateSuperVO(aggVO.getParentVO());
        SuperVO saveData = aggVO.getTableVO(curTabCode)[0];
        SuperVO oldVO = getServiceTemplate().queryByPk(saveData.getClass(), saveData.getPrimaryKey());
        if (ArrayUtils.contains(new String[]{TRNConst.Table_NAME_DEPTCHG, TRNConst.Table_NAME_DIMISSION, TRNConst.Table_NAME_PART},
            curTabCode))
        {
            if (TRNConst.Table_NAME_PART.equals(curTabCode))
            {
                // ��ְ�޸ĺ����¼�
                fireEvent((PartTimeVO) oldVO, null, pk_hrorg, HICommonValue.PARTSOURCEID, IEventType.TYPE_UPDATE_BEFORE);
            }
            // ��ְ��ְ��ʱ���������ί�й�ϵ
            if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode))
            {
                fireEvent((PsnJobVO) oldVO, null, pk_hrorg, HICommonValue.PSNJOBSOURCEID, IEventType.TYPE_UPDATE_BEFORE);
            }
        }
        if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode))
        {
            PsnJobVO vo = (PsnJobVO) saveData;
            if (vo.getLastflag() != null && vo.getLastflag().booleanValue())
            {
                // ֻ���޸�����һ����ʱ��ͬ��HR��֯
                saveData.setAttributeValue(PsnJobVO.PK_HRORG, pk_hrorg);
            }
        }
        
        if (saveData instanceof PsnJobVO)
        {
            ((PsnJobVO) saveData).setClerkcode((((PsnJobVO) saveData)).getClerkcode().trim());
            checkClerkCodeUnique((PsnJobVO) saveData);
            getIPersonRecordService().checkDeptPostCanceled((PsnJobVO) saveData);
        }
        
        if ((TRNConst.Table_NAME_DIMISSION.equals(curTabCode) || TRNConst.Table_NAME_DEPTCHG.equals(curTabCode))
            && ((PsnJobVO) saveData).getTrnsevent() == 4)
        {
            checkEndDate((PsnJobVO) saveData);
        }
        
        SuperVO retVO = getServiceTemplate().update(saveData, true);
        if (isSynWork && ArrayUtils.contains(new String[]{TRNConst.Table_NAME_DEPTCHG, TRNConst.Table_NAME_PART}, curTabCode))
        {
            // ��ְ��ְͬ����������
            updateWork((PsnJobVO) retVO);
        }
        if (TRNConst.Table_NAME_TRIAL.equals(curTabCode) && ((TrialVO) saveData).getLastflag().booleanValue())
        {
            // ����Ҫͬ��������¼
            UFBoolean endflag = ((TrialVO) saveData).getEndflag();
            if (endflag == null || !endflag.booleanValue())
            {
                // ���һ��δ����
                setTrialinfo(((TrialVO) saveData).getPk_psnorg(), UFBoolean.TRUE, ((TrialVO) saveData).getTrial_type());
            }
            else
            {
                
                if (((TrialVO) saveData).getTrialresult() != null && ((TrialVO) saveData).getTrialresult() == TRNConst.TRIALRESULT_PASS)
                {
                    // ���ý�����ʹ�ý��δת�������ӹ�����¼
                    addPsnjobByEndTrial((TrialVO) saveData, pk_hrorg, isSynWork);
                }
                else
                {
                    setTrialinfo(((TrialVO) saveData).getPk_psnorg(), UFBoolean.FALSE, null);
                }
                // // ���һ��������,ͬʱ����һ��ת���Ĺ�����¼
                // addPsnjobByEndTrial((TrialVO) saveData, pk_hrorg);
            }
        }
        if (ArrayUtils.contains(new String[]{TRNConst.Table_NAME_DEPTCHG, TRNConst.Table_NAME_DIMISSION}, curTabCode))
        {
            // ͬ��bd_psndoc.pk_org
            PsnOrgVO orgVO = getServiceTemplate().queryByPk(PsnOrgVO.class, ((PsnJobVO) retVO).getPk_psnorg());
            if (orgVO != null && orgVO.getLastflag() != null && orgVO.getLastflag().booleanValue())
            {
                // ������֯��ϵͬ��������ְ��֯
                getIPersonRecordService().synPkorgOfPsndoc(((PsnJobVO) retVO).getPk_org(), ((PsnJobVO) retVO).getPk_psndoc());
            }
            // ͬ��hi_psnorg.pk_hrorg
            getIPersonRecordService().synPkhrorgOfPsnorg(((PsnJobVO) retVO).getPk_psnorg(), ((PsnJobVO) retVO).getPk_hrorg());
        }
        
        // �޸���ְ��¼ͬ����д��֯��ϵ����һ��������¼�Ľ�������
        if (saveData instanceof PsnJobVO)
        {
            PsnJobVO jobVO = (PsnJobVO) saveData;
            if (jobVO.getTrnsevent() != null && jobVO.getTrnsevent() == 4)
            {
                UFLiteralDate begindate = jobVO.getBegindate();
                
                PsnOrgVO orgVO =
                    (PsnOrgVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                        .retrieveByPk(null, PsnOrgVO.class, jobVO.getPk_psnorg());
                if (orgVO != null)
                {
                    orgVO.setEnddate(begindate.getDateBefore(1));
                    NCLocator.getInstance().lookup(IPersistenceUpdate.class).updateVO(null, orgVO, new String[]{PsnOrgVO.ENDDATE}, null);
                }
                
                PsnJobVO preJobVO = (PsnJobVO) getPreVO(jobVO);
                if (preJobVO != null)
                {
                    preJobVO.setEnddate(begindate.getDateBefore(1));
                    NCLocator.getInstance().lookup(IPersistenceUpdate.class).updateVO(null, preJobVO, new String[]{PsnJobVO.ENDDATE}, null);
                }
                
            }
        }
        
        if (ArrayUtils.contains(new String[]{TRNConst.Table_NAME_DEPTCHG, TRNConst.Table_NAME_DIMISSION, TRNConst.Table_NAME_PART},
            curTabCode))
        {
            if (TRNConst.Table_NAME_PART.equals(curTabCode))
            {
                // ��ְ�޸ĺ����¼�
                fireEvent((PartTimeVO) oldVO, (PartTimeVO) retVO, pk_hrorg, HICommonValue.PARTSOURCEID, IEventType.TYPE_UPDATE_AFTER);
                HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
            }
            // ��ְ��ְ��ʱ���������ί�й�ϵ
            if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode))
            {
                fireEvent((PsnJobVO) oldVO, (PsnJobVO) retVO, pk_hrorg, HICommonValue.PSNJOBSOURCEID, IEventType.TYPE_UPDATE_AFTER);
                HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
            }
            // �ˆT����ӛ�׃���r�����T���ӣ�ͬ���ˆT�����Y������Ⱥ�M
            if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)&&!((PsnJobVO) oldVO).getPk_dept().equals(((PsnJobVO) retVO).getPk_dept()))
            {
                updateDataAfterDeptChanged(((PsnJobVO) retVO).getPk_psndoc());
            }
        }
        // edit for: ���ҵ���������ҵҵ��Ӧ��ƽ̨(ԤͶ�룩
        // ����Ա��Ϣ�������Ӽ��ֱ����Զ�����Ϣ�������ȡֵ������
        // ����Ա��������ʱ���Զ�����Ϣ��������ʱ����ͬ����������޸ģ�����ͬ����
        // �������޸���Ա��ְ�����Ҳͬ����Ա������Ϣ����heqiaoa 2015-01-20
        if (ArrayUtils.contains(new String[]{ /* TRNConst.Table_NAME_DEPTCHG, */TRNConst.Table_NAME_DIMISSION}, curTabCode))
        {
            return updateMainVOAndReturnAll(aggVO, curTabCode, saveData.getClass(), getCondition(saveData));
        }
        // ͬ����Ա������Ϣ
        getIPsndocService().updateDataAfterSubDataChanged(getInfoSetCode(curTabCode), aggVO.getParentVO().getPk_psndoc());
        return updateMainVOAndReturnAll(aggVO, curTabCode, saveData.getClass(), getCondition(saveData));
    }
    
    private void checkEndDate(PsnJobVO job) throws BusinessException
    {
        
        // �޸���ְ��¼,�������ʷ��֯��ϵ����ʼ�������ڲ������ں������֯��ϵ/������¼�Ŀ�ʼ����
        String pk_psnorg = job.getPk_psnorg();
        PsnOrgVO curOrgVO = (PsnOrgVO) getRetrieve().retrieveByPk(null, PsnOrgVO.class, pk_psnorg);
        
        UFLiteralDate curBegin = job.getBegindate();
        
        PsnOrgVO[] nextOrgVO =
            (PsnOrgVO[]) getRetrieve().retrieveByClause(null, PsnOrgVO.class,
                " pk_psndoc = '" + curOrgVO.getPk_psndoc() + "' and orgrelaid = " + (curOrgVO.getOrgrelaid() + 1));
        if (nextOrgVO != null && nextOrgVO.length > 0)
        {
            UFLiteralDate nextOrgBegin = nextOrgVO[0].getBegindate();
            PsnJobVO[] nextJobVOs =
                (PsnJobVO[]) getRetrieve().retrieveByClause(null, PsnJobVO.class,
                    " pk_psnorg = '" + nextOrgVO[0].getPk_psnorg() + "' order by recordnum desc ");
            UFLiteralDate nextJobBegin = nextOrgBegin;
            if (nextJobVOs != null && nextJobVOs.length > 0)
            {
                nextJobBegin = nextJobVOs[0].getBegindate();
            }
            
            // UFLiteralDate curEnd = job.getEnddate();
            
            // if (curEnd != null)
            // {
            // // �������ڲ�Ϊ��,��ֻ���ǽ�������
            // if (curEnd.compareTo(nextOrgBegin) >= 0)
            // {
            // throw new BusinessException(ResHelper.getString("6009tran", "06009tran0172"/* @res
            // "�������ڲ��ܵ��ڻ������´���֯��ϵ�Ŀ�ʼ����" */));
            // }
            // if (curEnd.compareTo(nextJobBegin) >= 0)
            // {
            // throw new BusinessException(ResHelper.getString("6009tran", "06009tran0173"/* @res
            // "�������ڲ��ܵ��ڻ������´���֯��ϵ��һ������ְ��¼�Ŀ�ʼ����" */));
            // }
            // }
            // else
            // {
            // ��������Ϊ��,��ֻ���ǿ�ʼ����
            if (curBegin.getDateBefore(1).compareTo(nextOrgBegin) >= 0)
            {
                throw new BusinessException(ResHelper.getString("6009tran", "06009tran0174"/*
                                                                                            * @res
                                                                                            * "��ʼ���ڲ��ܵ��ڻ������´���֯��ϵ�Ŀ�ʼ����"
                                                                                            */));
            }
            if (curBegin.getDateBefore(1).compareTo(nextJobBegin) >= 0)
            {
                throw new BusinessException(ResHelper.getString("6009tran", "06009tran0175"/*
                                                                                            * @res
                                                                                            * "��ʼ���ڲ��ܵ��ڻ������´���֯��ϵ��һ������ְ��¼�Ŀ�ʼ����"
                                                                                            */));
            }
            // }
            
        }
        
        // modify at 20130108 for �ۻ� �޸��춯�¼�����ְ������������¼ʱͬʱ�޸���֯��ϵ�Ľ�������
        curOrgVO.setEnddate(curBegin.getDateBefore(1));
        NCLocator.getInstance().lookup(IPersistenceUpdate.class).updateVO(null, curOrgVO, new String[]{PsnOrgVO.ENDDATE}, null);
        
    }
    
    /**
     * �޸�ʱ�������¹�������
     * 
     * @param psnjobs
     * @throws BusinessException
     * @author heqiaoa 2014-12-11
     * 
     */
    private void updateWork(PsnJobVO[] psnjobs) throws BusinessException
    {
        if (ArrayUtils.isEmpty(psnjobs))
        {
            return;
        }
        InSQLCreator inSQL = new InSQLCreator();
        // ��ȡ������¼����
        ArrayList<String> pk_psnjobs = new ArrayList<String>();
        for (PsnJobVO psnjobVO : psnjobs)
        {
            if (null == psnjobVO || StringUtils.isBlank(psnjobVO.getPk_psnjob()))
            {
                continue;
            }
            pk_psnjobs.add(psnjobVO.getPk_psnjob().trim());
        }
        // �����޸ĵĹ�����¼,���������¼,�޸Ĳ�����֮
        WorkVO[] works =
            getServiceTemplate().queryByCondition(WorkVO.class,
                " pk_psnjob in (" + inSQL.getInSQL(pk_psnjobs.toArray(new String[0]), true) + ") ");
        if (works == null || works.length == 0)
        {
            return;
        }
        // ��ù�����¼��������Ϣͬ���ֶ�ӳ��
        HashMap<String, GeneralVO> syncMap =
            NCLocator
                .getInstance()
                .lookup(IPsndocQryService.class)
                .getWorkSyncMap(psnjobs[0].getPk_hrorg(), psnjobs[0].getPk_group(),
                    psnjobs[0].getIsmainjob() != null && psnjobs[0].getIsmainjob().booleanValue());
        // ��֤����ͬ���ֶ��Ƿ���ʹ�ã�δʹ����ͬ��
        String[] jobAttrSet = psnjobs[0].getAttributeNames();
        String[] workAttrSet = works[0].getAttributeNames();
        
        // �˴���psnjobs������ְ��¼���ݣ�works����������¼���ݣ����˵���Ӽ�ְ��¼��ʱ��û��ͬ������������¼���˴��ͻ�����Խ���ˣ����Ĺ�����¼�϶��Ǵ��ڵ���������¼��
        String[] pk_psnworks = StringPiecer.getStrArrayDistinct(works, WorkVO.PK_PSNJOB);
        List<String> listpkworks = Arrays.asList(pk_psnworks);
        List<PsnJobVO> listjobvos = new ArrayList<PsnJobVO>();
        for (int i = 0; i < psnjobs.length; i++)
        {
            if (!listpkworks.contains(psnjobs[i].getPk_psnjob()))
            {
                continue;
            }
            listjobvos.add(psnjobs[i]);
        }
        psnjobs = listjobvos.toArray(new PsnJobVO[0]);
        
        // ���¶�Ӧ��WorkVO
        for (int i = 0; i < psnjobs.length; i++)
        {
            works[i].setBegindate(psnjobs[i].getBegindate());
            works[i].setEnddate(psnjobs[i].getEnddate());
            works[i].setMemo(psnjobs[i].getMemo());
            // ��֯
            works[i].setWorkcorp(VOUtils.getDocName(OrgVO.class, psnjobs[i].getPk_org()));
            // ����
            works[i].setWorkdept(VOUtils.getDocName(DeptVO.class, psnjobs[i].getPk_dept()));
            // ��λ
            works[i].setWorkpost(VOUtils.getDocName(PostVO.class, psnjobs[i].getPk_post()));
            // ְ��
            works[i].setWorkjob(VOUtils.getDocName(JobVO.class, psnjobs[i].getPk_job()));
            // ����ӳ���ϵͬ��������¼��������¼
            if (syncMap != null)
            {
                String[] jobItems = syncMap.keySet().toArray(new String[0]);
                for (String jobItem : jobItems)
                {
                    if (ArrayUtils.contains(jobAttrSet, jobItem)
                        && ArrayUtils.contains(workAttrSet, (String) syncMap.get(jobItem).getAttributeValue("workcode")))
                    {
                        Integer jobDataType = (Integer) syncMap.get(jobItem).getAttributeValue("jobdatatype");
                        Integer workDataType = (Integer) syncMap.get(jobItem).getAttributeValue("workdatatype");
                        // �Ӳ���ͬ�����ı�,��Ҫ����������ͬ�����ı��ֶ�,����ֱ��ͬ��
                        if (IBillItem.UFREF == jobDataType && IBillItem.STRING == workDataType)
                        {
                            String pk_refinfo = (String) syncMap.get(jobItem).getAttributeValue("jobrefmodule");
                            Object value = psnjobs[i].getAttributeValue(jobItem);
                            String pk_infoset_item = (String) syncMap.get(jobItem).getAttributeValue("job_pk_infoset_item");
                            // ���ն�Ӧ����
                            String name =
                                NCLocator.getInstance().lookup(IPsndocQryService.class).getRefItemName(pk_refinfo, value, pk_infoset_item);
                            String workItemCode = (String) syncMap.get(jobItem).getAttributeValue("workcode");
                            works[i].setAttributeValue(workItemCode, name);
                        }
                        // ������ֱ��ͬ��
                        else
                        {
                            String workItemCode = (String) syncMap.get(jobItem).getAttributeValue("workcode");
                            works[i].setAttributeValue(workItemCode, psnjobs[i].getAttributeValue(jobItem));
                        }
                    }
                }
            }
        }
        // ����������¼
        getBaseDAO().updateVOArray(works);
        // ������Ϣ���Ĺ�����ϵ
        String[] pk_psndocs = new String[works.length];
        for (int i = 0; i < works.length; i++)
        {
            pk_psndocs[i] = works[i].getPk_psndoc();
        }
        getIPsndocService().updateDataAfterSubDataChanged(WorkVO.getDefaultTableName(), pk_psndocs);
    }
    
    private BaseDAO getBaseDAO()
    {
        if (null == baseDAO)
        {
            baseDAO = new BaseDAO();
        }
        return baseDAO;
    }
    
    /**
     * �޸�ʱ���¹�������
     */
    private void updateWork(PsnJobVO retVO) throws BusinessException
    {
        // �����޸ĵĹ�����¼,���������¼,�޸Ĳ�����֮
        WorkVO[] work = getServiceTemplate().queryByCondition(WorkVO.class, " pk_psnjob = '" + retVO.getPk_psnjob() + "' ");
        if (work == null || work.length == 0)
        {
            return;
        }
        work[0].setBegindate(retVO.getBegindate());
        work[0].setEnddate(retVO.getEnddate());
        work[0].setMemo(retVO.getMemo());
        // ��֯
        work[0].setWorkcorp(VOUtils.getDocName(OrgVO.class, retVO.getPk_org()));
        // ����
        work[0].setWorkdept(VOUtils.getDocName(DeptVO.class, retVO.getPk_dept()));
        // ��λ
        work[0].setWorkpost(VOUtils.getDocName(PostVO.class, retVO.getPk_post()));
        // ְ��
        work[0].setWorkjob(VOUtils.getDocName(JobVO.class, retVO.getPk_job()));
        
        // ��ù�����¼��������Ϣͬ���ֶ�ӳ��
        HashMap<String, GeneralVO> syncMap =
            NCLocator
                .getInstance()
                .lookup(IPsndocQryService.class)
                .getWorkSyncMap(retVO.getPk_hrorg(), retVO.getPk_group(),
                    retVO.getIsmainjob() != null && retVO.getIsmainjob().booleanValue());
        // ����ӳ���ϵͬ��������¼��������¼
        if (syncMap != null)
        {
            String[] jobItems = syncMap.keySet().toArray(new String[0]);
            for (String jobItem : jobItems)
            {
                // ��֤����ͬ���ֶ��Ƿ���ʹ�ã�δʹ����ͬ��
                String[] jobAttrSet = retVO.getAttributeNames();
                String[] workAttrSet = work[0].getAttributeNames();
                if (ArrayUtils.contains(jobAttrSet, jobItem)
                    && ArrayUtils.contains(workAttrSet, (String) syncMap.get(jobItem).getAttributeValue("workcode")))
                {
                    Integer jobDataType = (Integer) syncMap.get(jobItem).getAttributeValue("jobdatatype");
                    Integer workDataType = (Integer) syncMap.get(jobItem).getAttributeValue("workdatatype");
                    // �Ӳ���ͬ�����ı�,��Ҫ����������ͬ�����ı��ֶ�,����ֱ��ͬ��
                    if (IBillItem.UFREF == jobDataType && IBillItem.STRING == workDataType)
                    {
                        String pk_refinfo = (String) syncMap.get(jobItem).getAttributeValue("jobrefmodule");
                        Object value = retVO.getAttributeValue(jobItem);
                        String pk_infoset_item = (String) syncMap.get(jobItem).getAttributeValue("job_pk_infoset_item");
                        // ���ն�Ӧ����
                        String name =
                            NCLocator.getInstance().lookup(IPsndocQryService.class).getRefItemName(pk_refinfo, value, pk_infoset_item);
                        String workItemCode = (String) syncMap.get(jobItem).getAttributeValue("workcode");
                        work[0].setAttributeValue(workItemCode, name);
                    }
                    // ������ֱ��ͬ��
                    else
                    {
                        String workItemCode = (String) syncMap.get(jobItem).getAttributeValue("workcode");
                        work[0].setAttributeValue(workItemCode, retVO.getAttributeValue(jobItem));
                    }
                    
                }
            }
        }
        
        getServiceTemplate().update(work[0], false);
        getIPsndocService().updateDataAfterSubDataChanged(WorkVO.getDefaultTableName(), work[0].getPk_psndoc());
    }
    
    @Override
    public void pushWorkflow_RequiresNew(String billtype, AggStapply bill) throws BusinessException
    {
        // �����ε���
        HashMap<String, String> hashPara = new HashMap<String, String>();
        hashPara.put(PfUtilBaseTools.PARAM_NOFLOW, PfUtilBaseTools.PARAM_NOFLOW);
        try
        {
            NCLocator.getInstance().lookup(IplatFormEntry.class).processAction("PUSH", billtype, null, bill, null, hashPara);
        }
        catch (Exception e)
        {
            throw new BusinessException(e.getMessage());
        }
        
    }
    
    @Override
    public int getBillCount(String pk_psnjob) throws BusinessException
    {
        String cond =
            " pk_psnjob = '" + pk_psnjob + "' and approve_state in (" + IPfRetCheckInfo.NOSTATE + "," + IPfRetCheckInfo.COMMIT + ","
                + IPfRetCheckInfo.GOINGON + "," + IPfRetCheckInfo.PASSING + ") ";
        int i = getRetrieve().getCountByCondition(RegapplyVO.getDefaultTableName(), cond);
        if (i > 0)
        {
            return i;
        }
        i = getRetrieve().getCountByCondition(StapplyVO.getDefaultTableName(), cond);
        return i;
    }
    
    @Override
    public void dimissionTrans(PsndocAggVO[] psns, PsnJobVO psnjob) throws BusinessException
    {
        String pkHrorg = HiSQLHelper.getHrorgBydept(psnjob.getPk_dept());
        ArrayList<PsnJobVO> al = new ArrayList<PsnJobVO>();
        PsndocVO[] psndocVOs = SuperVOHelper.getParentVOArrayFromAggVOs(psns, PsndocVO.class);
        BDVersionValidationUtil.validateSuperVO(psndocVOs);
        for (Object psn : psns)
        {
            // BDVersionValidationUtil.validateSuperVO(((PsndocAggVO) psn).getParentVO());
            PsnJobVO saveData = createDimission((PsndocAggVO) psn, psnjob, pkHrorg);
            al.add(saveData);
            
        }
        addDimissionTrans(al.toArray(new PsnJobVO[0]), pkHrorg);
    }
    
    /**
     * ��֯�����絥Ԫ����ת��ʹ��
     */
    @Override
    public void doTransDimission(PsnJobVO psnjob) throws BusinessException
    {
        
        String pkHrorg = HiSQLHelper.getHrorgBydept(psnjob.getPk_dept());
        PsnJobVO saveData = resetTransDimission(psnjob, pkHrorg);
        PsnJobVO lastVO = getLastVO(PsnJobVO.class, saveData.getPk_psnorg(), 1);
        if (lastVO.getBegindate().afterDate(saveData.getBegindate()) || lastVO.getBegindate().isSameDate(saveData.getBegindate()))
        {
            throw new BusinessException(ResHelper.getString("6009tran", "06009tran0144"/*
                                                                                        * @res
                                                                                        * "��Ա[{0},{1}]��ǰ�����¹�����¼�Ŀ�ʼ�������ڻ����ת������,����ת��."
                                                                                        */, saveData.getClerkcode(),
                VOUtils.getDocName(PsndocVO.class, saveData.getPk_psndoc())));
        }
        addDimissionTrans(new PsnJobVO[]{saveData}, pkHrorg);
    }
    
    @Override
    public void doTransDimissions(PsnJobVO[] psnjob, String pk_hrorg) throws BusinessException
    {
        
        PsnJobVO[] savedata = new PsnJobVO[psnjob.length];
        for (int i = 0; i < psnjob.length; i++)
        {
            savedata[i] = resetTransDimission(psnjob[i], pk_hrorg);
            PsnJobVO lastVO = getLastVO(PsnJobVO.class, savedata[i].getPk_psnorg(), 1);
            if (lastVO.getBegindate().afterDate(savedata[i].getBegindate()) || lastVO.getBegindate().isSameDate(savedata[i].getBegindate()))
            {
                throw new BusinessException(ResHelper.getString("6009tran", "06009tran0144"/*
                                                                                            * @res
                                                                                            * "��Ա[{0},{1}]��ǰ�����¹�����¼�Ŀ�ʼ�������ڻ����ת������,����ת��."
                                                                                            */, savedata[i].getClerkcode(),
                    VOUtils.getDocName(PsndocVO.class, savedata[i].getPk_psndoc())));
            }
        }
        addDimissionTrans(savedata, pk_hrorg);
    }
    
    // private String getHrorg(PsnJobVO oldJobVO, String newPk_dept) throws
    // BusinessException {
    // String pk_hrorg = oldJobVO.getPk_hrorg();
    // // ����ҵ��Χ�����ж��Ƿ���Ҫ����ת�ƺ���֯����ʾί�� ������ ����ʽί�� ����
    // if (ManagescopeFacade.isEntityUsed(ManagescopeTypeEnum.psnMajorjob,
    // oldJobVO.getPk_psnjob())) {
    // String[] pkHrorgs =
    // ManagescopeFacade.queryHrorgsByAssgidPsnorgAndBusiregion(oldJobVO.getPk_psnorg(),
    // oldJobVO.getAssgid(),
    // ManagescopeBusiregionEnum.psndoc);
    // pk_hrorg = pkHrorgs[0];
    // } else {
    // String[] pkHrorgs =
    // ManagescopeFacade.queryHrOrgsByDeptAndBusiregion(newPk_dept,
    // ManagescopeBusiregionEnum.psndoc);
    // pk_hrorg = pkHrorgs[0];
    // }
    // return pk_hrorg;
    // }
    
    /**
     * ��ְ��Աת��������ְ��¼
     * 
     * @param psnJobVOs
     * @param pkHrorg
     * @throws BusinessException
     */
    private void addDimissionTrans(PsnJobVO[] psnJobVOs, String pkHrorg) throws BusinessException
    {
        
        // ��֯��ϵ������Ҫ����Ĺ�����¼�Ķ�ӦMAP
        HashMap<String, PsnJobVO> pkJobVOMap = new HashMap<String, PsnJobVO>();
        
        // ��֯��ϵ����������,����������ʱ��
        String[] psnorgArray = new String[psnJobVOs.length];
        // hr��֯��������,���ڷ����¼�
        String[] pkOrg = new String[psnJobVOs.length];
        // ����������������,�����ֱ�ͬ������
        ArrayList<String> psndocList = new ArrayList<String>();
        for (int i = 0; i < psnJobVOs.length; i++)
        {
            psnorgArray[i] = psnJobVOs[i].getPk_psnorg();
            pkJobVOMap.put(psnJobVOs[i].getPk_psnorg(), psnJobVOs[i]);
            pkOrg[i] = pkHrorg;
            if (!psndocList.contains(psnJobVOs[i].getPk_psndoc()))
            {
                psndocList.add(psnJobVOs[i].getPk_psndoc());
            }
        }
        InSQLCreator ttu = null;
        try
        {
            ttu = new InSQLCreator();
            String psnorgInSql = ttu.getInSQL(psnorgArray);
            
            PsnJobVO[] before =
                (PsnJobVO[]) getRetrieve().retrieveByClause(null, PsnJobVO.class,
                    " pk_psnorg in (" + psnorgInSql + ") and assgid = 1 and lastflag = 'Y' ");
            
            if (before == null || before.length != psnJobVOs.length)
            {
                throw new BusinessException(BDVersionValidationUtil.getUpdateInfo());
            }
            
            HiBatchEventValueObject.fireEvent(before, null, pkOrg, HICommonValue.PSNJOBSOURCEID, IEventType.TYPE_INSERT_BEFORE);
            
            BaseDAO dao = new BaseDAO();
            
            // У�����ʱ��
            // �õ���ǰѡ�����֯��ϵ����Щ��ҪУ��
            // String querySql = " select before.pk_psnorg from hi_psnorg before " +
            // "inner join hi_psnorg after on before.pk_psndoc = after.pk_psndoc "
            // + "and before.orgrelaid = after.orgrelaid - 1 " + "where before.pk_psnorg in (" + psnorgInSql +
            // ") ";
            // ArrayList pkList = (ArrayList) dao.executeQuery(querySql, new ColumnListProcessor());
            // ����ҪУ��Ľ���У��
            // if (pkList != null && pkList.size() > 0)
            // {
            // for (int i = 0; i < pkList.size(); i++)
            // {
            // checkEndDate(pkJobVOMap.get(pkList.get(i)));
            // }
            // }
            
            // ��д��һ����¼��endflag&enddate
            UFLiteralDate date = psnJobVOs[0].getBegindate();// ���й�����¼�Ŀ�ʼ���ڶ�һ��
            ArrayList<PsnJobVO> al = new ArrayList<PsnJobVO>();
            for (int i = 0; i < before.length; i++)
            {
                PsnJobVO vo = (PsnJobVO) before[i].clone();
                vo.setEndflag(UFBoolean.TRUE);
                vo.setPoststat(UFBoolean.FALSE);
                if (vo.getEnddate() == null)
                {
                    vo.setEnddate(date.getDateBefore(1));
                }
                al.add(vo);
            }
            dao.updateVOArray(al.toArray(new PsnJobVO[0]), new String[]{PsnJobVO.ENDDATE, PsnJobVO.ENDFLAG, PsnJobVO.POSTSTAT});
            
            // ���¶�Ӧ��֯��ϵ�µ���ʷ��¼��recordnum��lastflag
            String updateSql =
                " update hi_psnjob set recordnum = recordnum + 1 , lastflag = 'N' where pk_psnorg  in ( " + psnorgInSql
                    + " ) and assgid =1 ";
            dao.executeUpdate(updateSql);
            
            // ͬ��������Ϣ��pk_org
            String sql = " select pk_psnorg from hi_psnorg where pk_psnorg in (" + psnorgInSql + ") and lastflag = 'Y'";
            // ��ѯ��Ҫͬ�������ĵ���֯��ϵ
            ArrayList pkList2 = (ArrayList) dao.executeQuery(sql, new ColumnListProcessor());
            ArrayList<String> sqlList = new ArrayList<String>();
            for (int i = 0; pkList2 != null && i < pkList2.size(); i++)
            {
                PsnJobVO vo = pkJobVOMap.get(pkList2.get(i));
                String subSql = " update bd_psndoc set pk_org = '" + vo.getPk_org() + "' where pk_psndoc = '" + vo.getPk_psndoc() + "' ";
                sqlList.add(subSql);
            }
            NCLocator.getInstance().lookup(IPersistenceUpdate.class).executeSQLs(sqlList.toArray(new String[0]));
            
            // ��������
            insert4SubSet(psnJobVOs);
            
            PsnJobVO[] temp =
                (PsnJobVO[]) getRetrieve().retrieveByClause(null, PsnJobVO.class,
                    " pk_psnorg in (" + psnorgInSql + ") and assgid = 1 and lastflag = 'Y' ");
            
            if (temp == null || temp.length != psnJobVOs.length)
            {
                throw new BusinessException(BDVersionValidationUtil.getUpdateInfo());
            }
            
            HashMap<String, PsnJobVO> map = new HashMap<String, PsnJobVO>();
            for (int i = 0; i < temp.length; i++)
            {
                map.put(temp[i].getPk_psnorg(), temp[i]);
            }
            
            PsnJobVO[] after = new PsnJobVO[psnJobVOs.length];
            for (int i = 0; i < before.length; i++)
            {
                after[i] = map.get(before[i].getPk_psnorg());
            }
            
            // �����¼�--��ֹ�Ͷ���ͬ��ȷ��н�ʽ�������
            HiBatchEventValueObject.fireEvent(before, after, pkOrg, HICommonValue.PSNJOBSOURCEID, IEventType.TYPE_INSERT_AFTER);
            HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
        }
        catch (Exception e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
        finally
        {
            if (ttu != null)
            {
                ttu.clear();
            }
        }
        // 7)ͬ����Ա������Ϣ
        getIPsndocService().updateDataAfterSubDataChanged(PsnJobVO.getDefaultTableName(), psndocList.toArray(new String[0]));
        
        // ͬ������
        HiCacheUtils.synCache(PsndocVO.getDefaultTableName(), PsnJobVO.getDefaultTableName(), PsnOrgVO.getDefaultTableName());
        
    }
    
    private PsnJobVO createDimission(PsndocAggVO psn, PsnJobVO psnjob, String pkHrorg)
    {
        PsnJobVO vo = (PsnJobVO) psnjob.clone();
        vo.setClerkcode(psn.getParentVO().getPsnJobVO().getClerkcode());
        vo.setPk_psndoc(psn.getParentVO().getPk_psndoc());
        vo.setPk_psnorg(psn.getParentVO().getPsnJobVO().getPk_psnorg());
        
        return resetTransDimission(vo, pkHrorg);
    }
    
    private PsnJobVO resetTransDimission(PsnJobVO psnjob, String pkHrorg)
    {
        PsnJobVO vo = (PsnJobVO) psnjob.clone();
        vo.setAssgid(1);
        vo.setEndflag(UFBoolean.TRUE);
        vo.setIsmainjob(UFBoolean.TRUE);
        vo.setLastflag(UFBoolean.TRUE);
        vo.setPk_hrgroup(PubEnv.getPk_group());
        vo.setPk_group(PubEnv.getPk_group());
        vo.setPk_hrorg(pkHrorg);
        vo.setPoststat(UFBoolean.FALSE);
        vo.setPk_psnjob(null);
        vo.setPsntype(0);
        vo.setRecordnum(0);
        vo.setShoworder(9999999);
        vo.setTrnsevent((Integer) ((MDEnum) TrnseventEnum.TRANSAFTERDIS).value());
        vo.setTrnstype("1002Z710000000008GT3");// ת��
        vo.setTrial_flag(UFBoolean.FALSE);
        vo.setStatus(VOStatus.NEW);
        return vo;
    }
    
    @Override
    public PsndocAggVO[] queryByPks(String[] strPks) throws BusinessException
    {
        
        if (strPks == null || strPks.length == 0)
        {
            return null;
        }
        
        String[] psnjobAttrs = new PsnJobVO().getAttributeNames();
        String[] psndocAttrs = new PsndocVO().getAttributeNames();
        StringBuffer sb = new StringBuffer();
        for (String attr : psndocAttrs)
        {
            if (PsndocVO.PHOTO.equals(attr) || PsndocVO.PREVIEWPHOTO.equals(attr))
            {
                continue;
            }
            sb.append(",bd_psndoc." + attr + " as bd_psndoc_" + attr);
        }
        for (String attr : psnjobAttrs)
        {
            sb.append(",hi_psnjob." + attr + " as hi_psnjob_" + attr);
        }
        
        ArrayList<PsndocVO> psns = null;
        InSQLCreator ttu = null;
        try
        {
            ttu = new InSQLCreator();
            String selectSql =
                " select " + sb.toString().substring(1)
                    + " from bd_psndoc  inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc "
                    + " inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg where hi_psnjob.pk_psnjob in ( "
                    + ttu.getInSQL(strPks) + " )";
            psns = (ArrayList<PsndocVO>) new BaseDAO().executeQuery(selectSql, new BeanListProcessor(PsndocVO.class));
            
        }
        // �����쳣�Ե��������ѯ��ѭ��
        // catch (Exception e)
        // {
        // Logger.error(e.getMessage(), e);
        // }
        finally
        {
            if (ttu != null)
            {
                ttu.clear();
            }
        }
        
        if (psns == null || psns.size() == 0)
        {
            return null;
        }
        
        HashMap<String, PsndocVO> hm = new HashMap<String, PsndocVO>();
        for (PsndocVO psn : psns)
        {
            hm.put(psn.getPsnJobVO().getPk_psnjob(), psn);
        }
        
        ArrayList<PsndocAggVO> al = new ArrayList<PsndocAggVO>();
        for (String pk : strPks)
        {
            if (hm.get(pk) == null)
            {
                continue;
            }
            PsndocAggVO agg = new PsndocAggVO();
            agg.setParentVO(hm.get(pk));
            al.add(agg);
        }
        
        return al.size() > 0 ? al.toArray(new PsndocAggVO[0]) : null;
        
        // Object[] objPsnjobs =
        // MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKsWithOrder(PsnJobVO.class,
        // strPks, true);
        // if (objPsnjobs == null || objPsnjobs.length == 0) {
        // return null;
        // }
        // String[] pkPsndoc = new String[objPsnjobs.length];
        // for (int i = 0; i < objPsnjobs.length; i++) {
        // pkPsndoc[i] = ((PsnJobVO) objPsnjobs[i]).getPk_psndoc();
        // }
        //
        // ArrayList<String> alAttr = new ArrayList<String>();
        // for (String attr : new PsndocVO().getAttributeNames()) {
        // if (PsndocVO.PHOTO.equals(attr) ||
        // PsndocVO.PREVIEWPHOTO.equals(attr)) {
        // continue;
        // }
        // alAttr.add(attr);
        // }
        //
        // NCObject[] objPsndocs =
        // MDPersistenceService.lookupPersistenceQueryService().queryBillOfNCObjectByPKs(PsndocVO.class,
        // pkPsndoc,
        // alAttr.toArray(new String[0]), true);
        // HashMap<String, PsndocVO> hmPsndoc = new HashMap<String, PsndocVO>();
        // for (int i = 0; objPsndocs != null && i < objPsndocs.length; i++) {
        // PsndocVO docvo = ((PsndocAggVO)
        // objPsndocs[i].getContainmentObject()).getParentVO();
        // if (hmPsndoc.get(docvo.getPk_psndoc()) == null) {
        // hmPsndoc.put(docvo.getPk_psndoc(), docvo);
        // }
        // }
        //
        // ArrayList<PsndocAggVO> psndocAggVOs = new ArrayList<PsndocAggVO>();
        // for (int i = 0; i < objPsnjobs.length; i++) {
        // PsndocVO doc = hmPsndoc.get(((PsnJobVO)
        // objPsnjobs[i]).getPk_psndoc());
        // if (doc == null) {
        // continue;
        // }
        // PsndocAggVO aggvo = new PsndocAggVO();
        // aggvo.setParentVO((PsndocVO) doc.clone());
        // aggvo.getParentVO().setPsnJobVO(((PsnJobVO) objPsnjobs[i]));
        // psndocAggVOs.add(aggvo);
        // }
        // return psndocAggVOs.toArray(new PsndocAggVO[0]);
        
    }
    
    private void checkClerkCodeUnique(PsnJobVO psnjobVO) throws BusinessException
    {
        getIPersonRecordService().checkClerkCodeUnique(psnjobVO);
    }
    
    /**
     * �Ӽ�����,����Ψһ��У��,�������¼�,��У��ʱ���
     * 
     * @param <T>
     * @param vo
     * @param blChangeAuditInfo
     * @return
     * @throws BusinessException
     */
    public <T extends SuperVO> T update4SubSet(T vo, boolean blChangeAuditInfo, boolean blNeedReturnValue) throws BusinessException
    {
        
        // ����
        getServiceTemplate().getLocker().lock(SimpleDocServiceTemplate.UPDATE, vo);
        
        // �汾У�飨ʱ���У�飩
        // BDVersionValidationUtil.validateSuperVO(mainVO);
        
        // ���������Ϣ
        vo.setStatus(VOStatus.UPDATED);
        SimpleDocServiceTemplate.setAuditInfoAndTs(vo, blChangeAuditInfo);
        
        MDPersistenceService.lookupPersistenceService().saveBillWithRealDelete(vo);
        
        return blNeedReturnValue ? MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPK((Class<T>) vo.getClass(),
            vo.getPrimaryKey(), true) : null;
    }
    
    /**
     * �Ӽ����� ,����Ψһ��У��,�������¼�
     * 
     * @param <T>
     * @param vo
     * @return
     * @throws BusinessException
     */
    private <T extends SuperVO> void insert4SubSet(T[] vo) throws BusinessException
    {
        // ���������Ϣ
        for (int i = 0; i < vo.length; i++)
        {
            vo[i].setStatus(VOStatus.NEW);
            SimpleDocServiceTemplate.setAuditInfoAndTs(vo[i], true);
        }
        MDPersistenceService.lookupPersistenceService().saveBill(vo);
    }
    
    @Override
    public void validateAddToBlacklist(PsndocAggVO vo, String pk_org) throws BusinessException
    {
        if (HiSQLHelper.isInJob(vo))
        {
            throw new ValidationException(ResHelper.getString("6009tran", "06009tran0138")/*
                                                                                           * @res
                                                                                           * "ֻ����ְ��Ա���ܼ����������"
                                                                                           */);
        }
        
        String pk_psndoc = vo.getParentVO().getPk_psndoc();
        PsndocVO psn = (PsndocVO) getRetrieve().retrieveByPk(null, PsndocVO.class, pk_psndoc);
        if (psn != null && psn.getDie_date() != null && psn.getDie_remark() != null)
        {
            throw new ValidationException(ResHelper.getString("6009tran", "06009tran0139")/*
                                                                                           * @res
                                                                                           * "��ǰѡ�����Ա�Ѿ�����,���ܼ��������."
                                                                                           */);
        }
        
        BlacklistVO bvo = new BlacklistVO();
        bvo.setPk_group(PubEnv.getPk_group());
        bvo.setPk_org(pk_org);
        bvo.setPk_psndoc(pk_psndoc);
        bvo.setPsnname(psn.getName());
        bvo.setPsnname2(psn.getName2());
        bvo.setPsnname3(psn.getName3());
        bvo.setPsnname4(psn.getName4());
        bvo.setPsnname5(psn.getName5());
        bvo.setPsnname6(psn.getName6());
        bvo.setPsncode(psn.getPk_psndoc());
        bvo.setId(psn.getId());
        bvo.setIdtype(psn.getIdtype());
        bvo.setSex(psn.getSex());
        bvo.setBirthday(psn.getBirthdate());
        bvo.setDelflag(UFBoolean.FALSE);
        bvo.setCode(psn.getCode());
        bvo.setStatus(VOStatus.NEW);
        bvo.setComefrom(1);// ����ϵͳ
        bvo.setPermanentres(vo.getParentVO().getPermanreside());
        AggBlacklistVO agg = new AggBlacklistVO();
        agg.setParentVO(bvo);
        
        // У���Ƿ�����ְ��Ա
        String msg = uniquevalidate(bvo);
        if (!StringUtils.isBlank(msg))
        {
            throw new BusinessException(msg);
        }
        
        // У��������Ա�Ƿ��Ѿ������ں�������
        boolean isInBlacklist = NCLocator.getInstance().lookup(IBlacklistManageService.class).isInBlacklist(bvo);
        
        // ����Ѿ������ں������У�����ʾ������Ϣ
        if (isInBlacklist)
        {
            
            // ��ѯ��Ϣ
            String where = " id='" + bvo.getId() + "'" + " and idtype = '" + bvo.getIdtype() + "' and delflag = 'N' ";
            
            if (bvo.getPsnname() != null)
            {
                where += " and psnname = '" + bvo.getPsnname() + "' ";
            }
            
            if (bvo.getPk_psndoc_bad() != null)
            {
                where += " and pk_psndoc_bad <> '" + bvo.getPk_psndoc_bad() + "' ";
            }
            String corpName = "";
            String adReason = "";
            
            BlacklistVO[] vos =
                (BlacklistVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, BlacklistVO.class, where);
            if (vos != null && vos.length > 0)
            {
                corpName = VOUtils.getDocName(OrgVO.class, vos[0].getPk_org());
                adReason = vos[0].getCause();
            }
            throw new BusinessException(ResHelper.getString("6007bad", "06007bad0103"/*
                                                                                      * @res
                                                                                      * "����Ա�Ѿ�����������У����ܱ���! ¼�뵥λ��{0} �������ɣ�{1}"
                                                                                      */, corpName, adReason));
        }
        
    }
    
    private String uniquevalidate(BlacklistVO vo)
    {
        String result = " ";
        
        // ����Ƿ���psndoc���Ѽ�¼��
        PsndocVO psndocVO = new PsndocVO();
        psndocVO.setIdtype(vo.getIdtype());
        psndocVO.setId(vo.getId());
        psndocVO.setName(vo.getPsnname());
        psndocVO.setName2(vo.getPsnname2());
        psndocVO.setName3(vo.getPsnname3());
        psndocVO.setName4(vo.getPsnname4());
        psndocVO.setName5(vo.getPsnname5());
        psndocVO.setName6(vo.getPsnname6());
        psndocVO.setPk_psndoc(vo.getPk_psndoc());
        PsndocVO maininfovo = null;
        try
        {
            maininfovo = NCLocator.getInstance().lookup(IBlacklistManageService.class).isRecordExist(psndocVO);
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
            maininfovo = null;
        }
        
        // ����֯��ϵ��������δ������
        if (maininfovo != null && !maininfovo.getPsnOrgVO().getEndflag().booleanValue())
        {
            PsnIdtypeVO psnIdtypeVO = null;
            try
            {
                psnIdtypeVO =
                    (PsnIdtypeVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                        .retrieveByPk(null, PsnIdtypeVO.class, maininfovo.getIdtype());
            }
            catch (BusinessException e)
            {
                Logger.error(e.getMessage(), e);
            }
            // ��Ա����
            result =
                ResHelper.getString("6007bad", "06007bad0104"/*
                                                              * @res
                                                              * "{0}������֤������Ϊ{1},֤������Ϊ{2},��Ա����Ϊ {3}��Ա����Ϊ{4}�ļ�¼����֯:{5} , ����:{6} , ��λ{7}"
                                                              */, maininfovo.getPsnJobVO().getOrgname(), VOUtils.getNameByVO(psnIdtypeVO),
                    maininfovo.getId(), VOUtils.getNameByVO(maininfovo), maininfovo.getCode(), maininfovo.getPsnJobVO().getOrgname(),
                    maininfovo.getPsnJobVO().getDeptname(), maininfovo.getPsnJobVO().getJobname());
        }
        
        return result;
    }
    
	@Override
	public void addPsnjobDimissionWithDate(PsndocAggVO aggVO, String curTabCode, String pk_hrorg, boolean isDisablePsn,
			UFLiteralDate endDate) throws BusinessException {
		 // ����һ��ʼ�ͼ�鲢����ts,���ts���Ծ����쳣, �汾У�飨ʱ���У�飩
        BDVersionValidationUtil.validateSuperVO(aggVO.getParentVO());
        PsnJobVO saveData = (PsnJobVO) aggVO.getTableVO(curTabCode)[0];
        saveData.setClerkcode(saveData.getClerkcode().trim());
        getIPersonRecordService().addNewDimissionWithDate(saveData, pk_hrorg, isDisablePsn,endDate);
        
        // 3-4)����������Ա��
        stopKeyPsn(aggVO.getParentVO().getPsnJobVO().getPk_psnjob(), saveData.getBegindate());
        
        aggVO = updateMainVOAndReturnAll(aggVO, curTabCode, saveData.getClass(), getCondition(saveData));
        // ��Ա�䶯ʱ�������Ա���������˺������򣬸��ݲ���������Ա����
        updatePsncode(aggVO, saveData.getPk_hrorg(), TRNConst.Table_NAME_DIMISSION);
		
	}
	
    /**
     * �ˆT����ӛ�׃���r�����T���ӣ�ͬ���ˆT�����Y������Ⱥ�M
     * 
     * @param psnjobs
     * @throws BusinessException
     * @author xqy 2018-4-25
     * 
     */
    private void updateDataAfterDeptChanged(String getPk_psndoc) throws BusinessException
    {
    	BaseDAO dao = new BaseDAO();
    	// ���¹���ӛ�����Ⱥ�M
    	String updateJobSql = "UPDATE hi_psnjob SET jobglbdef4 = org_dept.glbdef1 FROM hi_psnjob,org_dept where hi_psnjob.pk_dept = org_dept.pk_dept AND hi_psnjob.poststat='Y' AND hi_psnjob.pk_psndoc =?";
    	// �����ˆT�����Y������Ⱥ�M
        String updateDocSql =
        		"UPDATE bd_psndoc SET glbdef11 = org_dept.glbdef1 FROM bd_psndoc,hi_psnjob,org_dept where bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc AND hi_psnjob.poststat='Y' AND hi_psnjob.pk_dept = org_dept.pk_dept AND bd_psndoc.pk_psndoc =?";
        SQLParameter parameter = new SQLParameter();
        //�ˆT�����Y��
        parameter.addParam(getPk_psndoc);
        dao.executeUpdate(updateJobSql, parameter);
        dao.executeUpdate(updateDocSql, parameter);
    }
    
}