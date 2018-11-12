package nc.impl.hrsms.hi.entrymng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.pf.CheckStatusCallbackContext;
import nc.bs.pub.pflock.PfBusinessLock;
import nc.bs.pub.pflock.VOConsistenceCheck;
import nc.bs.pub.pflock.VOLockData;
import nc.bs.pub.pflock.VOsConsistenceCheck;
import nc.bs.pub.pflock.VOsLockData;
import nc.bs.sec.esapi.NCESAPI;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.BillCodeHelper;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPsndocService;
import nc.itf.hrsms.hi.entrymng.IEntrymngManageService;
import nc.itf.hrsms.hi.entrymng.IEntrymngQueryService;
import nc.itf.hr.IHRLicenseChecker;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.message.IHRMessageSend;
import nc.itf.hr.notice.INotice;
import nc.itf.hr.pf.IHrPf;
import nc.itf.hrp.psnbudget.IOrgBudgetQueryService;
import nc.itf.om.IAOSQueryService;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.itf.uap.pf.IplatFormEntry;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.data.access.NCObject;
import nc.message.util.IDefaultMsgConst;
import nc.message.util.MessageCenter;
import nc.message.vo.MessageVO;
import nc.message.vo.NCMessage;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pub.tools.HiCacheUtils;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.VOUtils;
import nc.pubitf.para.SysInitQuery;
import nc.vo.bd.psn.PsnClVO;
import nc.vo.hi.entrymng.AggEntryapplyVO;
import nc.vo.hi.entrymng.EntryapplyVO;
import nc.vo.hi.entrymng.EntrymngConst;
import nc.vo.hi.entrymng.EntrymngHelper;
import nc.vo.hi.entrymng.HiSendMsgHelper;
import nc.vo.hi.entrymng.ValidBudgetResultVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.pub.BillCodeRepeatBusinessException;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hi.trnstype.TrnstypeFlowVO;
import nc.vo.hr.message.HRBusiMessageVO;
import nc.vo.hr.notice.NoticeTempletVO;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.hrp.psnorgbudget.ValidateResultVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.post.PostVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pf.change.IActionDriveChecker;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.trn.regmng.AggRegapplyVO;
import nc.vo.trn.transmng.AggStapply;
import nc.vo.uap.rbac.constant.INCSystemUserConst;
import nc.vo.uif2.LoginContext;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class EntrymngManageServiceImpl extends SimpleDocServiceTemplate implements IEntrymngManageService, IEntrymngQueryService
    /*IActionDriveChecker, ICheckStatusCallback*/
{
    
    private BaseDAO baseDAO = null;
    
    public void callCheckStatus(CheckStatusCallbackContext cscc) throws BusinessException
    {
        if (cscc.isTerminate())
        {
            NCObject ncObj = NCObject.newInstance(cscc.getBillVo());
            IFlowBizItf itf = ncObj.getBizInterface(IFlowBizItf.class);
            String[] fields = new String[4];
            // ������
            itf.setApprover(cscc.getApproveId());
            fields[0] = itf.getColumnName(itf.ATTRIBUTE_APPROVER);
            // ����ʱ��
            itf.setApproveDate(cscc.getApproveDate() == null ? null : new UFDateTime(cscc.getApproveDate()));
            fields[1] = itf.getColumnName(itf.ATTRIBUTE_APPROVEDATE);
            // ����״̬
            itf.setApproveStatus(cscc.getCheckStatus());
            fields[2] = itf.getColumnName(itf.ATTRIBUTE_APPROVESTATUS);
            // ��������
            itf.setApproveNote(cscc.getCheckNote());
            fields[3] = itf.getColumnName(itf.ATTRIBUTE_APPROVENOTE);
            
            // �����޸ĺ�����
            SuperVO vo = (SuperVO) ((AggregatedValueObject) cscc.getBillVo()).getParentVO();
            getBaseDAO().updateVO(vo, fields);
            vo = (SuperVO) getBaseDAO().retrieveByPK(vo.getClass(), vo.getPrimaryKey());
            ((AggregatedValueObject) cscc.getBillVo()).setParentVO(vo);
        }
    }
    
    public BaseDAO getBaseDAO()
    {
        if (baseDAO == null)
        {
            baseDAO = new BaseDAO();
        }
        return baseDAO;
    }
    
    public EntrymngManageServiceImpl()
    {
        super("Entrymng");
    }
    
    public AggEntryapplyVO[] batchSaveBill(AggEntryapplyVO aggvo, ArrayList<String> pkPsnjobs, LoginContext context, String[] billCodes,
            boolean isShow) throws BusinessException
    {
        boolean isAutoGenerateBillCode = isAutoGenerateBillCode(EntrymngConst.BillTYPE_ENTRY, context.getPk_group(), context.getPk_org());
        
        ArrayList<AggEntryapplyVO> al = new ArrayList<AggEntryapplyVO>();
        
        if (!isAutoGenerateBillCode)
        {
            // ������Զ����ɱ���,��Ա��������м���
            BillCodeHelper.lockBillCodeRule("hr_auto_billcode" + EntrymngConst.BillTYPE_ENTRY, 100);
        }
        
        try
        {
            
            String prefix = "ZD" + EntrymngConst.BillTYPE_ENTRY + PubEnv.getServerDate().toStdString();
            // ���Զ����ɵ��ݺ� /Ĭ�Ϲ������� "��������+yyyy-mm-dd+_��ˮ��"
            String flowCode = getFlowCode(prefix);
            for (int i = 0; i < pkPsnjobs.size(); i++)
            {
                AggEntryapplyVO temp = clone(aggvo);
                EntryapplyVO head = (EntryapplyVO) temp.getParentVO();
                if (isAutoGenerateBillCode && billCodes != null && billCodes.length > 0 && billCodes[i] != null)
                {
                    head.setBill_code(billCodes[i]);
                }
                else
                {
                    // ���Զ����ɵ��ݺ�
                    head.setBill_code(prefix + "_" + getFlowCode(flowCode, i));
                }
                head.setApprove_state(IPfRetCheckInfo.NOSTATE);
                head.setBillmaker(context.getPk_loginUser());
                // head.setApply_date(PubEnv.getServerLiteralDate());
                head.setPk_billtype(EntrymngConst.BillTYPE_ENTRY);
                head.setPk_org(context.getPk_org());
                head.setPk_group(context.getPk_group());
                PsnJobVO pj = (PsnJobVO) getIPersistenceRetrieve().retrieveByPk(null, PsnJobVO.class, pkPsnjobs.get(i));
                head.setPk_psnjob(pj.getPk_psnjob());
                head.setPk_psndoc(pj.getPk_psndoc());
                
                Integer approveType =
                    SysInitQuery.getParaInt(context.getPk_org(), IHrPf.hashBillTypePara.get(EntrymngConst.BillTYPE_ENTRY));
                if (approveType == null)
                {
                    approveType = HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW;
                }
                
                TrnstypeFlowVO[] flow =
                    (TrnstypeFlowVO[]) NCLocator
                        .getInstance()
                        .lookup(IPersistenceRetrieve.class)
                        .retrieveByClause(null, TrnstypeFlowVO.class,
                            " pk_group = '" + PubEnv.getPk_group() + "' and pk_trnstype = '" + pj.getTrnstype() + "'");
                if (flow != null && flow.length > 0)
                {
                    if (head.getTranstypeid() == null && approveType == HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW)
                    {
                        head.setTranstypeid(flow[0].getPk_transtype());
                        if (flow[0].getPk_transtype() != null)
                        {
                            BilltypeVO billtype =
                                (BilltypeVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                                    .retrieveByPk(null, BilltypeVO.class, flow[0].getPk_transtype());
                            head.setTranstype(billtype.getPk_billtypecode());
                        }
                    }
                    
                    if (head.getBusiness_type() == null && isShow)
                    {
                        head.setBusiness_type(flow[0].getPk_businesstype());
                    }
                }
                al.add(temp);
            }
            
            checkBillCodeRepeat(al.toArray(new AggEntryapplyVO[0]));
            
            ArrayList<AggEntryapplyVO> result = new ArrayList<AggEntryapplyVO>();
            for (AggEntryapplyVO agg : al)
            {
                result.add(insertBill(agg));
            }
            
            return result.toArray(new AggEntryapplyVO[0]);
        }
        catch (Exception e)
        {
            String[] codes = null;
            // ������غ��쳣 ,���ظ��ĺ�Abandon��
            if (e instanceof BillCodeRepeatBusinessException)
            {
                codes = ((BillCodeRepeatBusinessException) e).getRepeatCodes();
                if (isAutoGenerateBillCode && codes != null)
                {
                    for (int i = 0; i < codes.length; i++)
                    {
                        try
                        {
                            NCLocator
                                .getInstance()
                                .lookup(IBillcodeManage.class)
                                .AbandonBillCode_RequiresNew(EntrymngConst.BillTYPE_ENTRY, context.getPk_group(), context.getPk_org(),
                                    codes[i]);
                        }
                        catch (Exception e2)
                        {
                            Logger.error(e2.getMessage(), e2);
                        }
                    }
                }
            }
            
            // ��������쳣 ��ع����ݺ�
            if (billCodes != null && billCodes.length > 0)
            {
                for (String billno : billCodes)
                {
                    if (codes != null && ArrayUtils.contains(codes, billno))
                    {
                        // �������ĺžͲ��ٻع���
                        continue;
                    }
                    try
                    {
                        NCLocator.getInstance().lookup(IBillcodeManage.class)
                            .rollbackPreBillCode(EntrymngConst.BillTYPE_ENTRY, context.getPk_group(), context.getPk_org(), billno);
                    }
                    catch (Exception e1)
                    {
                        Logger.error(e1.getMessage(), e1);
                    }
                }
            }
            Logger.error(e.getMessage(), e);
            
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
        finally
        {
            if (!isAutoGenerateBillCode)
            {
                // ������Զ����ɱ���,��Ա��������м���
                BillCodeHelper.unlockBillCodeRule("hr_auto_billcode" + EntrymngConst.BillTYPE_ENTRY);
            }
        }
    }
    
    public Hashtable<String, String[]> createUserValue(AggregatedValueObject[] aggvos) throws BusinessException
    {
        String[] fieldCode = EntrymngConst.FIELDCODE;
        Hashtable<String, String[]> hm = new Hashtable<String, String[]>();
        for (int i = 0; aggvos != null && i < aggvos.length; i++)
        {
            EntryapplyVO bill = (EntryapplyVO) ((AggEntryapplyVO) aggvos[i]).getParentVO();
            PsnJobVO psnJobVO =
                bill.getPk_psnjob() == null ? null : (PsnJobVO) getIPersistenceRetrieve().retrieveByPk(null, PsnJobVO.class,
                    bill.getPk_psnjob());
            if (psnJobVO == null)
            {
                return hm;
            }
            for (int j = 0; j < fieldCode.length; j++)
            {
                String value = "";
                // ��Ա����
                if (EntryapplyVO.BILL_CODE.equals(fieldCode[j]))
                {
                    // ���ݱ���
                    value = bill.getBill_code();
                }
                else if ("effect_date".equals(fieldCode[j]))
                {
                    // ��ְ����
                    value = psnJobVO.getBegindate() == null ? "" : psnJobVO.getBegindate().toStdString();
                }
                else if (EntryapplyVO.APPROVE_STATE.equals(fieldCode[j]))
                {
                    // ����״̬
                    value = getStatus(bill.getApprove_state() == null ? 102 : bill.getApprove_state());
                }
                else if (EntryapplyVO.PK_PSNJOB.equals(fieldCode[j]))
                {
                    // ��Ա����
                    value = VOUtils.getDocName(PsndocVO.class, psnJobVO.getPk_psndoc());
                }
                else if (PsndocVO.PK_ORG.equals(fieldCode[j]))
                {
                    // ��֯
                    value = VOUtils.getDocName(OrgVO.class, psnJobVO.getPk_org());
                }
                else if (PsnJobVO.PK_DEPT.equals(fieldCode[j]))
                {
                    // ����
                    value = VOUtils.getDocName(DeptVO.class, psnJobVO.getPk_dept());
                }
                else if (PsnJobVO.PK_PSNCL.equals(fieldCode[j]))
                {
                    // ��Ա���
                    value = VOUtils.getDocName(PsnClVO.class, psnJobVO.getPk_psncl());
                }
                else if (PsnJobVO.PK_POST.equals(fieldCode[j]))
                {
                    // ��λ
                    value = VOUtils.getDocName(PostVO.class, psnJobVO.getPk_post());
                }
                else if (PsnJobVO.PK_JOB.equals(fieldCode[j]))
                {
                    // ְ��
                    value = VOUtils.getDocName(JobVO.class, psnJobVO.getPk_job());
                }
                else
                {
                    value = "";
                }
                hm.put(fieldCode[j] + i, new String[]{value});
            }
        }
        return hm;
    }
    
    /**
     * ����һ���ۺ�VO
     */
    private AggEntryapplyVO clone(AggEntryapplyVO src)
    {
        
        AggEntryapplyVO trg = new AggEntryapplyVO();
        EntryapplyVO head = new EntryapplyVO();
        trg.setParentVO(head);
        for (String attrName : src.getParentVO().getAttributeNames())
        {
            trg.getParentVO().setAttributeValue(attrName, src.getParentVO().getAttributeValue(attrName));
        }
        return trg;
    }
    
    public <T extends AggregatedValueObject> void deleteBatchBill(T... billvos) throws BusinessException
    {
        
        PfBusinessLock pfLock = null;
        IFlowBizItf flowItf = getFlowBizItf(billvos[0].getParentVO());
        try
        {
            // ����
            pfLock = new PfBusinessLock();
            pfLock.lock(new VOsLockData(billvos, flowItf.getBilltype()), new VOsConsistenceCheck(billvos, flowItf.getBilltype()));
            // �Զ���У����
            DefaultValidationService vService = new DefaultValidationService();
            createCustomValidators(vService, DELETE);
            SuperVO[] headvos = getHeadVO(billvos).toArray(new SuperVO[0]);
            vService.validate(headvos);
            getMDPersistenceService().deleteBillFromDB(billvos);
        }
        finally
        {
            if (pfLock != null)
            {
                // �ͷ���
                pfLock.unLock();
            }
        }
    }
    
    // ɾ����������Ĳ���
    private void deleteOldWorknote(AggEntryapplyVO vo) throws BusinessException
    {
        getIHrPf().deleteWorkflowNote(vo);
    }
    
    public <T extends AggregatedValueObject> void deleteBill(T billvo) throws BusinessException
    {
        
        PfBusinessLock pfLock = null;
        IFlowBizItf flowItf = getFlowBizItf(billvo.getParentVO());
        try
        {
            // ����
            pfLock = new PfBusinessLock();
            pfLock.lock(new VOLockData(billvo, flowItf.getBilltype()), new VOConsistenceCheck(billvo, flowItf.getBilltype()));
            // �Զ���У����
            DefaultValidationService vService = new DefaultValidationService();
            createCustomValidators(vService, DELETE);
            SuperVO[] headvos = getHeadVO(billvo).toArray(new SuperVO[0]);
            vService.validate(headvos);
            getMDPersistenceService().deleteBillFromDB(billvo);
        }
        finally
        {
            if (pfLock != null)
            {
                // �ͷ���
                pfLock.unLock();
            }
        }
    }
    
    public AggEntryapplyVO[] doApprove(AggEntryapplyVO[] vos) throws BusinessException
    {
        // ������������µ���״̬
        AggEntryapplyVO[] vo = NCLocator.getInstance().lookup(IEntrymngManageService.class).batchUpdateBill_RequiresNew(vos);
        // Ϊ��֧����Ϣģ���ϵ��������ƶ���������ִ�е��ݵĲ���������̨����
        return execBills(vo);
    }
    
    /**
     * Ϊ��֧����Ϣģ���ϵ��������ƶ���������ִ�е��ݵĲ���������̨���� ��ԭ����ǰ̨Model��ʵ�ֵ�execBills����������̨ʵ��
     * 
     * @param billvos
     * @throws BusinessException
     * @author heqiaoa 2014-11-18
     */
    public AggEntryapplyVO[] execBills(AggEntryapplyVO[] billvos) throws BusinessException
    {
        if (ArrayUtils.isEmpty(billvos))
        {
            return null;
        }
        
        ArrayList<AggEntryapplyVO> allvo = new ArrayList<AggEntryapplyVO>();
        for (AggEntryapplyVO agg : billvos)
        {
            allvo.add(agg);
        }
        
        // Ϊ�˴�����ʵĲ������б���У�飬������pk_org��pk_group����һ��LoginContext
        LoginContext tempContext = new LoginContext();
        EntryapplyVO parentVO = (EntryapplyVO) billvos[0].getParentVO();
        tempContext.setPk_group(parentVO.getPk_group());
        tempContext.setPk_org(parentVO.getPk_org());
        
        // ����һ��map ����ִ�гɹ��ĵ��ݣ���ִ�в��ɹ�����Ϣ
        HashMap<String, Object> result =
            NCLocator.getInstance().lookup(IEntrymngManageService.class).execBills(billvos, tempContext, false);
        AggEntryapplyVO[] retObjs = (AggEntryapplyVO[]) result.get(EntrymngHelper.RESULT_BILLS);
        String msg = (String) result.get(EntrymngHelper.RESULT_MSG);
        
        if (!StringUtils.isBlank(msg))
        {
            // ����NCMessage
            NCMessage ncMessage = new NCMessage();
            MessageVO messageVO = new MessageVO();
            messageVO.setMsgsourcetype(IDefaultMsgConst.NOTICE);// ��ϢԴ����
            messageVO.setReceiver(PubEnv.getPk_user());// ���ý����� ���������֮���Զ��Ÿ���
            messageVO.setIsdelete(UFBoolean.FALSE);// ����ɾ�����
            messageVO.setSender(INCSystemUserConst.NC_USER_PK);
            // �������ʱ��Ϊ����Ĭ��Ϊ������ʱ��
            messageVO.setSendtime(PubEnv.getServerTime());
            messageVO.setDr(0);
            messageVO.setSubject(ResHelper.getString("6007entry", "16007entry0015")/*
                                                                                    * res "��������ʧ��"
                                                                                    */);
            messageVO.setContent(msg);
            ncMessage.setMessage(messageVO);
            NCMessage[] message = new NCMessage[1];
            message[0] = ncMessage;
            try
            {
                MessageCenter.sendMessage(message);
            }
            catch (Exception e)
            {
                Logger.error(e.getMessage(), e);
            }
        }
        
        // ��ִ�гɹ��ĺͲ��ɹ��ĺϲ�
        if (!ArrayUtils.isEmpty(retObjs))// ��û��ִ�гɹ��ĵ��ݣ���ֱ�ӽ�����ͨ���ĵ��ݷ��ػ�ȥˢ�½���
        {
            for (int i = 0; i < allvo.size(); i++)
            {
                AggEntryapplyVO aggivo = allvo.get(i);
                for (int j = 0; j < retObjs.length; j++)
                {
                    if (aggivo.getParentVO().getPrimaryKey().equals(retObjs[j].getParentVO().getPrimaryKey()))
                    {
                        allvo.remove(i);
                    }
                }
            }
            billvos = (AggEntryapplyVO[]) ArrayUtils.addAll(allvo.toArray(new AggEntryapplyVO[0]), retObjs);
        }
        
        return billvos;
    }
    
    /**
     * ����������������µ��� (Ϊ��֧����Ϣģ���ϵ��������ƶ���������ִ�е��ݵĲ���������̨���� )
     * 
     * @param vos
     * @return AggEntryapplyVO[]
     * @throws BusinessException
     * @author heqiaoa 2014-11-18
     */
    public AggEntryapplyVO[] batchUpdateBill_RequiresNew(AggEntryapplyVO[] vos) throws BusinessException
    {
        for (int i = 0; vos != null && i < vos.length; i++)
        {
            vos[i] = updateBill(vos[i], false);
        }
        return vos;
    }
    
    public AggEntryapplyVO[] doCommit(AggEntryapplyVO[] vos) throws BusinessException
    {
        for (int i = 0; vos != null && i < vos.length; i++)
        {
            EntryapplyVO billvo = (EntryapplyVO) vos[i].getParentVO();
            // ���µ���״̬
            billvo.setApprove_state(IPfRetCheckInfo.COMMIT);
            vos[i].setParentVO(billvo);
            vos[i] = updateBill(vos[i], false);
        }
        String pk_org = ((EntryapplyVO) vos[0].getParentVO()).getPk_org();
        Integer approvetype = HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW;
        try
        {
            approvetype = SysInitQuery.getParaInt(pk_org, IHrPf.hashBillTypePara.get(EntrymngConst.BillTYPE_ENTRY));
        }
        catch (Exception e)
        {
            Logger.error(e.getMessage(), e);
        }
        if (approvetype != null && approvetype == HRConstEnum.APPROVE_TYPE_FORCE_DIRECT)
        {
            /** start����Ϊ����֪ͨ��ʽ�޸� 2013.4.3 yunana */
            // ������ʽΪֱ��ʱ����֪ͨ
            // HiSendMsgHelper.sendMessage(EntrymngConst.NOTICE_SORT_APPROVE,
            // PubEnv.getPk_group(), pk_org,
            // createUserValue(vos),
            // EntrymngConst.FIELDCODE, vos == null ? 0 : vos.length);
            String tempCode = HICommonValue.msgcode_entry_approve;// ��ְֱ��֪ͨ��Ϣģ��Դ����
            HiSendMsgHelper.sendMessage1(tempCode, vos, pk_org);
            /** end:2013.4.3 */
        }
        return vos;
    }
    
    @Override
    public AggEntryapplyVO[] doDelete(AggEntryapplyVO[] vos) throws BusinessException
    {
        for (AggEntryapplyVO vo : vos)
        {
            // ���յ��ݺ� δʵ��
            String billType = (String) vo.getParentVO().getAttributeValue(EntryapplyVO.PK_BILLTYPE);
            String pk_group = (String) vo.getParentVO().getAttributeValue(EntryapplyVO.PK_GROUP);
            String pk_org = (String) vo.getParentVO().getAttributeValue(EntryapplyVO.PK_ORG);
            String bill_code = (String) vo.getParentVO().getAttributeValue(EntryapplyVO.BILL_CODE);
            if (isAutoGenerateBillCode(billType, pk_group, pk_org))
            {
                NCLocator.getInstance().lookup(IBillcodeManage.class).returnBillCodeOnDelete(billType, pk_group, pk_org, bill_code, null);
            }
            deleteOldWorknote(vo);
            deleteBill(vo);
        }
        return vos;
    }
    
    /**
     * ��������Ϣ
     */
    public void doPerfromBill_RequiresNew(AggEntryapplyVO aggVO) throws BusinessException
    {
        if (aggVO == null) return;
        // ��ȡ��������Ա��ֵ
        EntryapplyVO vo = (EntryapplyVO) aggVO.getParentVO();
        
        PsnOrgVO[] orgVO = queryByCondition(PsnOrgVO.class, " pk_psndoc = '" + vo.getPk_psndoc() + "' ");
        PsnOrgVO psnorgVO = new PsnOrgVO();
        // wanglqh��ȡֵ��ʱ����Ҫ��������������ȡ����һ����¼��һ���������¼�¼
        for (PsnOrgVO psnvo : orgVO)
        {
            if (psnvo.getEndflag() == UFBoolean.FALSE && psnvo.getLastflag() == UFBoolean.TRUE)
            {
                psnorgVO = psnvo;
            }
        }
        if (orgVO != null && orgVO.length > 0 && psnorgVO.getIndocflag() != null && psnorgVO.getIndocflag().booleanValue())
        {
            PsndocVO psn = (PsndocVO) getIPersistenceRetrieve().retrieveByPk(null, PsndocVO.class, psnorgVO.getPk_psndoc());
            String name = MultiLangHelper.getName(psn);
            /* "��Ա[{0},{1}]�Ѿ�ת����Ա����,�޷��ٴ�ת��"; */
            throw new BusinessException(ResHelper.getString("6007psn", "06007psn0338", psn.getCode(), name));
        }
        
        // ת����Ա����
        boolean issyncwork = vo.getIssyncwork() == null ? false : vo.getIssyncwork().booleanValue();
        NCLocator.getInstance().lookup(IPsndocService.class).intoDoc(null, issyncwork, vo.getPk_org(), vo.getPk_psnjob());
        
        // ���µ���״̬Ϊ��ִ��
        vo.setAttributeValue(EntryapplyVO.APPROVE_STATE, HRConstEnum.EXECUTED);
        getPersistenceUpdate().updateVO(null, vo, new String[]{EntryapplyVO.APPROVE_STATE}, null);
        
        // ���¹�����¼������Ϣ
        PsnJobVO jobVO = queryByPk(PsnJobVO.class, vo.getPk_psnjob());
        String[] updateFields = {PsnJobVO.ORIBILLTYPE, PsnJobVO.ORIBILLPK};
        jobVO.setAttributeValue(updateFields[0], EntrymngConst.BillTYPE_ENTRY);
        jobVO.setAttributeValue(updateFields[1], vo.getPk_entryapply());
        getPersistenceUpdate().updateVO(null, jobVO, updateFields, null);
    }
    
    public void doPushBill_RequiresNew(AggEntryapplyVO bill) throws BusinessException
    {
        // ������ҵ�񵥾�
        HashMap<String, String> hashPara = new HashMap<String, String>();
        hashPara.put(PfUtilBaseTools.PARAM_NOFLOW, PfUtilBaseTools.PARAM_NOFLOW);
        NCLocator.getInstance().lookup(IplatFormEntry.class)
            .processAction("PUSH", EntrymngConst.BillTYPE_ENTRY, null, bill, null, hashPara);
    }
    
    public Object doPush(AggEntryapplyVO vo) throws BusinessException
    {
        return vo;
    }
    
    public AggregatedValueObject[] doUnApprove(AggregatedValueObject[] vos) throws BusinessException
    {
        List<String> entryPKList = new ArrayList<String>();
        for (int i = 0; i < vos.length; i++)
        {
            entryPKList.add(((EntryapplyVO) vos[i].getParentVO()).getPk_entryapply());
        }
        if (!entryPKList.isEmpty())
        {
            InSQLCreator isc = new InSQLCreator();
            String insql = isc.getInSQL(entryPKList.toArray(new String[0]));
            String strCondition = "pk_entryapply in (" + insql + ")";
            EntryapplyVO[] entryApplyVOs =
                (EntryapplyVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
                    .retrieveByClause(null, EntryapplyVO.class, strCondition);
            for (int i = 0; i < entryApplyVOs.length; i++)
            {
                int approvestate = entryApplyVOs[i].getApprove_state();
                
                checkPFPassingState(approvestate);
                
                if (approvestate == HRConstEnum.EXECUTED)
                {
                    throw new BusinessException(ResHelper.getString("6009tran", "06009tran0209")/* "��ִ�еĵ��ݲ���ȡ��������" */);
                }
            }
        }
        for (int i = 0; vos != null && i < vos.length; i++)
        {
            vos[i] = updateBill(vos[i], false);
        }
        return vos;
    }
    
    public void checkPFPassingState(int pfsate) throws BusinessException
    {
        // guoqt�������ڵ������������nc.ui.hr.pf.action.PFUnApproveAction��doAction()��������������Ϣ���ĵĹ������������߸÷���������Ҫͬʱ�жϵ�������ͨ�����������������������ȡ������
        if (IPfRetCheckInfo.NOPASS == pfsate)
        {
            throw new BusinessException(ResHelper.getString("6007entry", "16007entry0014")
            /* @res "��������δͨ��,����ȡ��������" */);
        }
    }
    
    public AggregatedValueObject[] doCallBack(AggregatedValueObject[] vos) throws BusinessException
    {
        for (int i = 0; vos != null && i < vos.length; i++)
        {
            vos[i] = updateBill(vos[i], false);
        }
        return vos;
    }
    
    /**
     * ִ�е���
     * 
     * @param billvos
     * @param context
     * @param isRunBackgroundTask
     * @return HashMap<String, Object>
     * @throws BusinessException
     */
    public HashMap<String, Object> execBills(AggEntryapplyVO[] billVOs, LoginContext context, boolean isRunBackgroundTask)
            throws BusinessException
    {
        
        // AggEntryapplyVO[] oldBillVOs = billVOs.clone();
        if (!isRunBackgroundTask)
        {
            // ǰ̨����ͨ����ִ�У�������Ч����<=ϵͳ���ڵĵ���
            ArrayList<AggEntryapplyVO> passVOs = new ArrayList<AggEntryapplyVO>();
            for (int i = 0; i < billVOs.length; i++)
            {
                EntryapplyVO applyVO = (EntryapplyVO) billVOs[i].getParentVO();
                Integer apprState = applyVO.getApprove_state();
                PsnJobVO psnJobVO = (PsnJobVO) getIPersistenceRetrieve().retrieveByPk(null, PsnJobVO.class, applyVO.getPk_psnjob());
                UFLiteralDate effectDate = psnJobVO.getBegindate();
                if (effectDate != null && effectDate.compareTo(PubEnv.getServerLiteralDate()) <= 0 && apprState != null
                    && apprState == IPfRetCheckInfo.PASSING)
                {
                    passVOs.add(billVOs[i]);
                }
            }
            billVOs = passVOs.toArray(new AggEntryapplyVO[0]);
        }
        
        if (billVOs == null || billVOs.length == 0)
        {
            // ���û�п�ִ�еĵ��ݣ���ԭ���ݷ��� @jiazhtb
            // ���޸ĵ�ԭ���ǣ���Ϣ�����ļ��룬��������֮���ֱ̨��ִ�У�û��ִ�еĻ��������ص��ݣ�����ˢ�½���
            HashMap<String, Object> res = new HashMap<String, Object>();
            // res.put(EntrymngHelper.RESULT_BILLS, oldBillVOs);
            return res;
        }
        
        HashMap<String, Object> result = new HashMap<String, Object>();
        StringBuffer sb = new StringBuffer();// ������Ϣ
        
        AggregatedValueObject[] retVOs = null;
        if (billVOs.length > 0)
        {
            retVOs = getValidBudgetVO(billVOs, context);
        }
        
        // ���һ���߼�,������У��ûͨ���ĵ���,Ҳ���뵽������ʾ��
        for (int i = 0; i < billVOs.length; i++)
        {
            if (isExit(retVOs, billVOs[i]))
            {
                continue;
            }
            sb.append(ResHelper.getString("6007entry", "06007entry0038")/*
                                                                         * @res "����"
                                                                         */
                + billVOs[i].getParentVO().getAttributeValue(EntryapplyVO.BILL_CODE) + ResHelper.getString("6007entry", "06007entry0039")/*
                                                                                                                                          * @
                                                                                                                                          * res
                                                                                                                                          * "���ڱ���У��δͨ�����ܳɹ�ִ�� "
                                                                                                                                          */);
        }
        
        if (retVOs == null || retVOs.length == 0)
        {
            // �����ֶ�û�п�ִ�е���,ֱ�ӷ���
            String msg = sb.length() == 0 ? "" : sb.toString();
            result.put(EntrymngHelper.RESULT_MSG, isRunBackgroundTask ? msg : msg.replaceAll("<br>", '\n' + ""));
            // @jiaztb û�п�ִ�еĵ��ݣ���ԭ���ݷ��أ�����ˢ�½��� @jiazhtb
            // result.put(EntrymngHelper.RESULT_BILLS, oldBillVOs);
            result.put(EntrymngHelper.RESULT_BILLS, null);
            return result;
        }
        
        boolean bl = NCLocator.getInstance().lookup(IHRLicenseChecker.class).checkPsnCountOnSwitchToDoc(retVOs.length);
        
        if (bl)
        {
            result.put(EntrymngHelper.RESULT_MSG, ResHelper.getString("6007psn", "06007psn0431") /* "����ִ��ʧ��:����ִ�н�����ϵͳԱ�������������Ȩ��!" */);
            result.put(EntrymngHelper.RESULT_BILLS, null);
            return result;
        }
        
        // ֻ�Ե��ݽ��б���У��
        ArrayList<AggEntryapplyVO> passBills = new ArrayList<AggEntryapplyVO>();// ִ�гɹ��ĵ���
        for (int i = 0; retVOs != null && i < retVOs.length; i++)
        {
            try
            {
                // ��ְִ��
                NCLocator.getInstance().lookup(IEntrymngManageService.class).doPerfromBill_RequiresNew((AggEntryapplyVO) retVOs[i]);
                passBills.add((AggEntryapplyVO) retVOs[i]);
                // doPerfromBill(ArrayClassConvertUtil.convert(retVOs,
                // AggEntryapplyVO.class));
            }
            catch (Exception e)
            {
                Logger.error(e.getMessage(), e);
                String billcode = (String) retVOs[i].getParentVO().getAttributeValue(EntryapplyVO.BILL_CODE);
                if (StringUtils.isEmpty(e.getMessage()))
                {
                    sb.append((i + 1) + ResHelper.getString("6007entry", "06007entry0040")/*
                                                                                           * @res ":����"
                                                                                           */
                        + billcode + ResHelper.getString("6007entry", "06007entry0041")/*
                                                                                        * @res "��������δ֪�쳣["
                                                                                        */
                        + e.getMessage() + ResHelper.getString("6007entry", "06007entry0042")/*
                                                                                              * @res
                                                                                              * "]���ܳɹ�ִ��,�����쳣��Ϣ��鿴��־."
                                                                                              *//*
                                                                                                 * + "<br>"
                                                                                                 */);
                }
                else
                {
                    if (e.getMessage().indexOf(billcode) < 0)
                    {
                        // ����쳣��Ϣ��û�г��ֵ��ݺ�,�������쳣��Ϣ
                        sb.append((i + 1) + ResHelper.getString("6007entry", "06007entry0040")/* @res ":����" */
                            + billcode + ResHelper.getString("6007entry", "06007entry0043")/* @res "���������쳣[" */
                            + e.getMessage() + ResHelper.getString("6007entry", "06007entry0042")/*
                                                                                                  * @res
                                                                                                  * "]���ܳɹ�ִ��,�����쳣��Ϣ��鿴��־."
                                                                                                  *//*
                                                                                                     * +
                                                                                                     * "<br>"
                                                                                                     */);
                    }
                    else
                    {
                        sb.append((i + 1) + ":" + e.getMessage()/* + "<br>" */);
                    }
                }
                //
                passBills.add((AggEntryapplyVO) retVOs[i]);
                continue;
            }
            try
            {
                AggEntryapplyVO agg = queryByPk(retVOs[i].getParentVO().getPrimaryKey());
                NCLocator.getInstance().lookup(IEntrymngManageService.class).doPushBill_RequiresNew(agg);
            }
            catch (Exception e)
            {
                Logger.error(e.getMessage(), e);
            }
            /*
             * // wanglqh��Ա������߹�����¼�������޸ĸ������е�ҵ��Ԫ if(retVOs != null){ NCLocator .getInstance()
             * .lookup(IRdsManageService.class) .updatePsnBankaccInfo( (String)
             * retVOs[i].getParentVO().getAttributeValue( EntryapplyVO.PK_HI_ORG), (String)
             * retVOs[i].getParentVO().getAttributeValue( EntryapplyVO.PK_PSNDOC)); }
             */
            
        }
        
        // ִ����ɺ�,����֪ͨ
        // 1)������ְ��֪ͨ,������ְ����֯���� 1001Z7PSN00000000002
        // key-org value-bill_list
        
        HashMap<String, ArrayList<AggEntryapplyVO>> hmTrans = new HashMap<String, ArrayList<AggEntryapplyVO>>();
        for (int i = 0; i < passBills.size(); i++)
        {
            String pk_org = (String) passBills.get(i).getParentVO().getAttributeValue(EntryapplyVO.PK_ORG);
            if (hmTrans.get(pk_org) == null)
            {
                hmTrans.put(pk_org, new ArrayList<AggEntryapplyVO>());
            }
            hmTrans.get(pk_org).add((AggEntryapplyVO) passBills.get(i));
        }
        
        for (String key : hmTrans.keySet())
        {
            if (hmTrans.get(key) == null || hmTrans.get(key).size() <= 0)
            {
                continue;
            }
            
            String tempCode = HICommonValue.msgcode_entry;
            HiSendMsgHelper.sendMessage1(tempCode, hmTrans.get(key).toArray(new AggEntryapplyVO[0]), key);
        }
        // end
        
        String msg = sb.length() == 0 ? "" : sb.toString();
        result.put(EntrymngHelper.RESULT_MSG, isRunBackgroundTask ? msg : msg.replaceAll("<br>", '\n' + ""));
        result.put(EntrymngHelper.RESULT_BILLS, passBills.toArray(new AggEntryapplyVO[0]));
        return result;
        
    }
    
    private boolean isExit(AggregatedValueObject[] retVOs, AggEntryapplyVO billVO) throws BusinessException
    {
        for (int i = 0; retVOs != null && i < retVOs.length; i++)
        {
            if (billVO.getParentVO().getPrimaryKey().equals(retVOs[i].getParentVO().getPrimaryKey()))
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getBillIdSql(int iBillStatus, String billType, String billid) throws BusinessException
    {
        String strWorkFlowWhere = getIHrPf().getBillIdSql(iBillStatus, billType);
        if (!StringUtils.isEmpty(strWorkFlowWhere))
        {
            strWorkFlowWhere = billid + " in (" + strWorkFlowWhere + ") ";
        }
        return strWorkFlowWhere;
    }
    
    private <T> IFlowBizItf getFlowBizItf(T vo)
    {
        
        NCObject ncObj = NCObject.newInstance(vo);
        IFlowBizItf itf = ncObj.getBizInterface(IFlowBizItf.class);
        return itf;
    }
    
    /**
     * �õ�����������ˮ��
     * 
     * @param prefix
     * @param codeField
     * @param className
     * @return ��λ��ˮ�� ��:00001
     * @throws BusinessException
     */
    public String getFlowCode(String prefix) throws BusinessException
    {
        
        // ��ָ��ǰ׺���ҳ���Ϊ22��ZDXXXX1111-11-11_00001
        String whereSql =
            EntryapplyVO.BILL_CODE + " like '" + prefix + "%' and len(" + EntryapplyVO.BILL_CODE + ") = 22 order by "
                + EntryapplyVO.BILL_CODE + " desc";
        SuperVO[] vos = NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, EntryapplyVO.class, whereSql);
        if (vos == null || vos.length == 0)
        {
            return "00001";
        }
        for (SuperVO vo : vos)
        {
            String code = ((String) vo.getAttributeValue(EntryapplyVO.BILL_CODE)).substring(prefix.length() + 1);
            try
            {
                Integer value = Integer.valueOf(code);
                if (value != null)
                {
                    // ÿ��Ӧ�ò��ᳬ��10�򵥾�
                    return StringUtils.leftPad(value + 1 + "", 5, '0');
                }
            }
            catch (NumberFormatException ex)
            {
                continue;
            }
        }
        return "00001";
    }
    
    /**
     * ��ȡ��ǰ������ˮ��
     * 
     * @param prefix
     * @param i
     * @return String
     * @throws BusinessException
     */
    private String getFlowCode(String code, int i) throws BusinessException
    {
        Integer value = Integer.valueOf(code);
        return org.apache.commons.lang.StringUtils.leftPad(value + i + "", 5, '0');
    }
    
    /**
     * ��ȡ��ʵ��VO
     * 
     * @param objects
     * @return
     */
    private List<SuperVO> getHeadVO(Object objects)
    {
        
        List<SuperVO> headls = new ArrayList<SuperVO>();
        if (objects instanceof AggregatedValueObject[] && ((AggregatedValueObject[]) objects).length >= 1)
        {
            AggregatedValueObject[] objs = (AggregatedValueObject[]) objects;
            for (int i = 0; i < objs.length; i++)
            {
                headls.add(((SuperVO) objs[i].getParentVO()));
            }
        }
        else
        {
            AggregatedValueObject obj = (AggregatedValueObject) objects;
            headls.add(((SuperVO) obj.getParentVO()));
        }
        return headls;
    }
    
    public IHrPf getIHrPf()
    {
        return NCLocator.getInstance().lookup(IHrPf.class);
    }
    
    private IPersistenceRetrieve getIPersistenceRetrieve()
    {
        
        return NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
    }
    
    private IPersistenceUpdate getPersistenceUpdate()
    {
        
        return NCLocator.getInstance().lookup(IPersistenceUpdate.class);
    }
    
    private String getStatus(Integer state)
    {
        switch (state)
        {
            case IPfRetCheckInfo.NOSTATE :
                return ResHelper.getString("6007entry", "06007entry0044")/* @res "����" */;
            case IPfRetCheckInfo.COMMIT :
                return ResHelper.getString("common", "UC001-0000029")/* @res "�ύ" */;
            case IPfRetCheckInfo.GOINGON :
                return ResHelper.getString("6007entry", "06007entry0045")/*
                                                                          * @res "����������"
                                                                          */;
            case IPfRetCheckInfo.NOPASS :
                return ResHelper.getString("6007entry", "06007entry0046")/*
                                                                          * @res "����δͨ��"
                                                                          */;
            case IPfRetCheckInfo.PASSING :
                return ResHelper.getString("6007entry", "06007entry0047")/*
                                                                          * @res "����ͨ��"
                                                                          */;
            case HRConstEnum.EXECUTED :
                return ResHelper.getString("6007entry", "06007entry0048")/*
                                                                          * @res "��ִ��"
                                                                          */;
        }
        return ResHelper.getString("6007entry", "06007entry0044")/* @res "����" */;
    }
    
    
    public <T extends AggregatedValueObject> T insertBill(T billvo) throws BusinessException
    {
        
        PfBusinessLock pfLock = null;
        IFlowBizItf flowItf = getFlowBizItf(billvo.getParentVO());
        try
        {
            // ����
            pfLock = new PfBusinessLock();
            pfLock.lock(new VOLockData(billvo, flowItf.getBilltype()), new VOConsistenceCheck(billvo, flowItf.getBilltype()));
            
            checkBillCodeRepeat(billvo);
            
            // �Զ���У����
            DefaultValidationService vService = new DefaultValidationService();
            createCustomValidators(vService, INSERT);
            SuperVO[] headvos = getHeadVO(billvo).toArray(new SuperVO[0]);
            vService.validate(headvos);
            // ���������Ϣ
            billvo.getParentVO().setStatus(VOStatus.NEW);
            setAuditInfoAndTs(((SuperVO) billvo.getParentVO()), true);
            String pk = getMDPersistenceService().saveBillWithRealDelete(billvo);
            billvo.getParentVO().setPrimaryKey(pk);
            
            // �ύ���ݺ�
            String billCode = flowItf.getBillNo();
            if (isAutoGenerateBillCode(EntrymngConst.BillTYPE_ENTRY, PubEnv.getPk_group(), flowItf.getPkorg()))
            {
                NCLocator.getInstance().lookup(IHrBillCode.class)
                    .commitPreBillCode(EntrymngConst.BillTYPE_ENTRY, PubEnv.getPk_group(), flowItf.getPkorg(), billCode);
            }
        }
        catch (Exception e)
        {
            
            if (e instanceof BillCodeRepeatBusinessException)
            {
                String[] codes = ((BillCodeRepeatBusinessException) e).getRepeatCodes();
                if (isAutoGenerateBillCode(EntrymngConst.BillTYPE_ENTRY, PubEnv.getPk_group(), flowItf.getPkorg()) && codes != null)
                {
                    for (int i = 0; i < codes.length; i++)
                    {
                        try
                        {
                            NCLocator
                                .getInstance()
                                .lookup(IBillcodeManage.class)
                                .AbandonBillCode_RequiresNew(EntrymngConst.BillTYPE_ENTRY, PubEnv.getPk_group(), flowItf.getPkorg(),
                                    codes[i]);
                        }
                        catch (Exception e2)
                        {
                            Logger.error(e2.getMessage(), e2);
                        }
                    }
                }
                throw (BillCodeRepeatBusinessException) e;
            }
            
            // �����쳣������Զ����ɵ��ݺ�,�������ݺ�
            if (isAutoGenerateBillCode(EntrymngConst.BillTYPE_ENTRY, PubEnv.getPk_group(), flowItf.getPkorg()))
            {
                NCLocator.getInstance().lookup(IHrBillCode.class)
                    .rollbackPreBillCode(EntrymngConst.BillTYPE_ENTRY, PubEnv.getPk_group(), flowItf.getPkorg(), flowItf.getBillNo());
            }
            
            throw new BusinessException(e.getMessage());
        }
        finally
        {
            if (pfLock != null)
            {
                // �ͷ���
                pfLock.unLock();
            }
        }
        return billvo;
    }
    
    private boolean isAutoGenerateBillCode(String billType, String pk_group, String pk_org) throws BusinessException
    {
        
        BillCodeContext billCodeContext = HiCacheUtils.getBillCodeContext(billType, pk_group, pk_org);
        return billCodeContext != null;
    }
    
    @Override
    public AggEntryapplyVO[] queryByCondition(LoginContext context, String condition) throws BusinessException
    {
        return queryByCondition(context, AggEntryapplyVO.class, condition);
    }
    
    @Override
    public AggEntryapplyVO queryByPk(String pk) throws BusinessException
    {
        return queryByPk(AggEntryapplyVO.class, pk);
    }
    
    public <T extends AggregatedValueObject> T[] saveBatchBill(T... billvos) throws BusinessException
    {
        
        PfBusinessLock pfLock = null;
        IFlowBizItf flowItf = getFlowBizItf(billvos[0].getParentVO());
        try
        {
            // ����
            pfLock = new PfBusinessLock();
            pfLock.lock(new VOsLockData(billvos, flowItf.getBilltype()), new VOsConsistenceCheck(billvos, flowItf.getBilltype()));
            // �Զ���У����
            DefaultValidationService vService = new DefaultValidationService();
            createCustomValidators(vService, INSERT);
            SuperVO[] headvos = getHeadVO(billvos).toArray(new SuperVO[0]);
            vService.validate(headvos);
            for (T billvo : billvos)
            {
                // ���������Ϣ
                billvo.getParentVO().setStatus(VOStatus.NEW);
                setAuditInfoAndTs(((SuperVO) billvo.getParentVO()), true);
                String pk = getMDPersistenceService().saveBillWithRealDelete(billvo);
                billvo.getParentVO().setPrimaryKey(pk);
            }
        }
        finally
        {
            if (pfLock != null)
            {
                // �ͷ���
                pfLock.unLock();
            }
        }
        return billvos;
    }
    
    public <T extends AggregatedValueObject> T updateBill(T billvo, boolean blChangeAuditInfo) throws BusinessException
    {
        
        PfBusinessLock pfLock = null;
        IFlowBizItf flowItf = getFlowBizItf(billvo.getParentVO());
        try
        {
            // ����
            pfLock = new PfBusinessLock();
            pfLock.lock(new VOLockData(billvo, flowItf.getBilltype()), new VOConsistenceCheck(billvo, flowItf.getBilltype()));
            
            checkBillCodeRepeat(billvo);
            // �Զ���У����
            DefaultValidationService vService = new DefaultValidationService();
            createCustomValidators(vService, UPDATE);
            SuperVO[] headvos = getHeadVO(billvo).toArray(new SuperVO[0]);
            vService.validate(headvos);
            // ���������Ϣ
            billvo.getParentVO().setStatus(VOStatus.UPDATED);
            setAuditInfoAndTs(((SuperVO) billvo.getParentVO()), blChangeAuditInfo);
            String pk = getMDPersistenceService().saveBillWithRealDelete(billvo);
            billvo.getParentVO().setPrimaryKey(pk);
        }
        catch (BusinessException e)
        {
            
            if (e instanceof BillCodeRepeatBusinessException)
            {
                String[] codes = ((BillCodeRepeatBusinessException) e).getRepeatCodes();
                if (isAutoGenerateBillCode(EntrymngConst.BillTYPE_ENTRY, PubEnv.getPk_group(), flowItf.getPkorg()) && codes != null)
                {
                    for (int i = 0; i < codes.length; i++)
                    {
                        try
                        {
                            NCLocator
                                .getInstance()
                                .lookup(IBillcodeManage.class)
                                .AbandonBillCode_RequiresNew(EntrymngConst.BillTYPE_ENTRY, PubEnv.getPk_group(), flowItf.getPkorg(),
                                    codes[i]);
                        }
                        catch (Exception e2)
                        {
                            Logger.error(e2.getMessage(), e2);
                        }
                    }
                }
                throw (BillCodeRepeatBusinessException) e;
            }
            
            Logger.error(e.getMessage(), e);
            throw e;
        }
        finally
        {
            if (pfLock != null)
            {
                // �ͷ���
                pfLock.unLock();
            }
        }
        return billvo;
    }
    
    /**
     * ����غ�
     * 
     * @param <T>
     * @param billvos
     */
    private <T extends AggregatedValueObject> void checkBillCodeRepeat(T... billvos) throws BusinessException
    {
        StringBuffer errMsg = new StringBuffer();
        ArrayList<String> repeatCodes = new ArrayList<String>();
        for (T vo : billvos)
        {
            IFlowBizItf itf = NCObject.newInstance(vo).getBizInterface(IFlowBizItf.class);
            String billCode = itf.getBillNo();
            String pk_entryapply = itf.getBillId();
            String billType = itf.getBilltype();
            String whereSql =
                EntryapplyVO.BILL_CODE + " = '" + NCESAPI.sqlEncode(billCode) + "' and pk_group = '" + PubEnv.getPk_group() + "'  and "
                    + EntryapplyVO.PK_BILLTYPE + " = '" + billType + "'";
            if (!StringUtils.isEmpty(pk_entryapply))
            {
                whereSql += " and " + EntryapplyVO.PK_ENTRYAPPLY + " <> '" + pk_entryapply + "'";
            }
            int count =
                NCLocator.getInstance().lookup(IPersistenceRetrieve.class).getCountByCondition(EntrymngConst.Bill_TABLENAME, whereSql);
            if (count > 0)
            {
                errMsg.append('\n' + ResHelper.getString("6007entry", "06007entry0050")/*
                                                                                        * @res "����["
                                                                                        */
                    + billCode + ResHelper.getString("6007entry", "06007entry0051")/*
                                                                                    * @res "]�ĵ��ݺ��Ѵ���"
                                                                                    */);
                repeatCodes.add(billCode);
                continue;
            }
        }
        if (errMsg.length() > 0)
        {
            BillCodeRepeatBusinessException ex = new BillCodeRepeatBusinessException(ResHelper.getString("6007entry", "06007entry0052")/*
                                                                                                                                        * @
                                                                                                                                        * res
                                                                                                                                        * "����ʧ��,����ԭ������:"
                                                                                                                                        */
                + errMsg.toString());
            ex.setRepeatCodes(repeatCodes.toArray(new String[0]));
            throw ex;
        }
    }
    
    /**
     * ��ñ���У��ͨ���ĵ�����Ϣ
     * 
     * @param vos
     * @param context
     * @return
     * @throws BusinessException
     */
    public boolean isValidBudgetVO(String[] pkPsnjobs, LoginContext context) throws BusinessException
    {
        
        // ����У��
        ValidateResultVO[] resultVOs = NCLocator.getInstance().lookup(IOrgBudgetQueryService.class).validateBudgetValue(context, pkPsnjobs);
        
        // ����У��ȫ��ͨ��
        if (resultVOs == null || resultVOs.length <= 0) return true;
        
        for (ValidateResultVO resultVO : resultVOs)
        {
            // У��δͨ��
            if (!resultVO.isValid())
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * ��ñ���У��ͨ���ĵ�����Ϣ
     * 
     * @param vos
     * @param context
     * @return
     * @throws BusinessException
     */
    public AggregatedValueObject[] getValidBudgetVO(AggregatedValueObject[] vos, LoginContext context) throws BusinessException
    {
        //
        Vector<AggregatedValueObject> passVO = new Vector<AggregatedValueObject>();
        // ��ȡ��������Ա��ֵ
        String[] pkPsnjobs = new String[vos.length];
        for (int i = 0; i < vos.length; i++)
        {
            pkPsnjobs[i] = ((EntryapplyVO) vos[i].getParentVO()).getPk_psnjob();
            passVO.add(vos[i]);
        }
        // ����У��
        ValidateResultVO[] resultVOs = NCLocator.getInstance().lookup(IOrgBudgetQueryService.class).validateBudgetValue(context, pkPsnjobs);
        
        // ����У��ȫ��ͨ��
        if (resultVOs == null || resultVOs.length < 0) return vos;
        
        for (ValidateResultVO resultVO : resultVOs)
        {
            // У��δͨ��
            if (!resultVO.isValid())
            {
                String pk_org = resultVO.getPk_org();
                // �ų�����У��δͨ������֯����
                for (AggregatedValueObject vo : vos)
                {
                    PsnJobVO psnJobVO =
                        (PsnJobVO) getIPersistenceRetrieve().retrieveByPk(null, PsnJobVO.class,
                            ((EntryapplyVO) vo.getParentVO()).getPk_psnjob());
                    if (psnJobVO == null || pk_org.equals(psnJobVO.getPk_org())) passVO.removeElement(vo);
                }
            }
        }
        /************************
         * modify start ��2013-4-1,yunana �����ʱ��滮����֪ͨ������ƽ̨�µķ���
         */
        
        this.sendMessage(resultVOs);
        /**
         * ע��ԭ����
         */
        // �����ʼ�
        // String pk_sort = "1001Z7BUDGET00000002";
        // for (ValidateResultVO vo : resultVOs)
        // {
        // if (StringUtils.isEmpty(vo.getHintMsg()))
        // {
        // continue;
        // }
        // OrgVO org =
        // NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(vo.getPk_org());
        // String hrorg = org == null ? vo.getPk_org() : org.getPk_org();
        // INotice setvice = NCLocator.getInstance().lookup(INotice.class);
        // NoticeTempletVO[] nt = setvice.queryDistributedTemplates(pk_sort,
        // PubEnv.getPk_group(), hrorg,
        // true);
        // if (nt != null && nt.length > 0)
        // {
        // String content = nt[0].getContent();
        // if (content != null && content.indexOf("<#reason#>") >= 0)
        // {
        // StringOperator strOperator = new StringOperator(content);
        // strOperator.replaceAllString("<#reason#>", vo.getHintMsg());
        // nt[0].setContent(strOperator.toString());
        // }
        // else
        // {
        // nt[0].setContent(content + '\n' + vo.getHintMsg());
        // }
        // if (StringUtils.isEmpty(nt[0].getCurrentUserPk()) ||
        // nt[0].getCurrentUserPk().length() != 20)
        // {
        // // ���ģ��ĵ�ǰ�û�Ϊ�գ����ϵ�ǰ�û�����NCϵͳ�û�
        // nt[0].setCurrentUserPk(PubEnv.getPk_user() != null &&
        // PubEnv.getPk_user().length() == 20 ?
        // PubEnv.getPk_user()
        // : INCSystemUserConst.NC_USER_PK);
        // }
        // setvice.sendNotice_RequiresNew(nt[0], hrorg, false);
        // }
        // }
        /*********************** modify end ��2013��4-1 ***************************/
        // String x = null;
        // x.toString();
        return passVO.toArray(new AggregatedValueObject[0]);
    }
    
    private void sendMessage(ValidateResultVO[] resultVOs) throws BusinessException
    {
        // �����ʼ�
        for (ValidateResultVO vo : resultVOs)
        {
            if (StringUtils.isEmpty(vo.getHintMsg()))
            {
                continue;
            }
            OrgVO org = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(vo.getPk_org());
            String hrorg = org == null ? vo.getPk_org() : org.getPk_org();
            IHRMessageSend messageSendService = NCLocator.getInstance().lookup(IHRMessageSend.class);
            // ��֯��ϢVO
            HRBusiMessageVO messageInfoVO = new HRBusiMessageVO();
            
            // ����ҵ����
            Hashtable<String, Object> value = new Hashtable<String, Object>();
            value.put("reason", vo.getHintMsg());
            messageInfoVO.setBusiVarValues(value);
            
            messageInfoVO.setBillVO(vo);// ����Ԫ����ʵ��VO
            String tempCode = "600301";// ����֪ͨ��Ϣģ��Դ����
            messageInfoVO.setMsgrescode(tempCode);// ��ϢԴ����
            messageInfoVO.setPkorgs(new String[]{hrorg});
            // ������Ϣ
            messageSendService.sendBuziMessage_RequiresNew(messageInfoVO);
            
            // NoticeTempletVO[] nt = setvice.queryDistributedTemplates(pk_sort,
            // PubEnv.getPk_group(), hrorg,
            // true);
            // if (nt != null && nt.length > 0)
            // {
            // String content = nt[0].getContent();
            // if (content != null && content.indexOf("<#reason#>") >= 0)
            // {
            // StringOperator strOperator = new StringOperator(content);
            // strOperator.replaceAllString("<#reason#>", vo.getHintMsg());
            // nt[0].setContent(strOperator.toString());
            // }
            // else
            // {
            // nt[0].setContent(content + '\n' + vo.getHintMsg());
            // }
            // if (StringUtils.isEmpty(nt[0].getCurrentUserPk()) ||
            // nt[0].getCurrentUserPk().length() != 20)
            // {
            // // ���ģ��ĵ�ǰ�û�Ϊ�գ����ϵ�ǰ�û�����NCϵͳ�û�
            // nt[0].setCurrentUserPk(PubEnv.getPk_user() != null &&
            // PubEnv.getPk_user().length() == 20 ?
            // PubEnv.getPk_user()
            // : INCSystemUserConst.NC_USER_PK);
            // }
            // setvice.sendNotice_RequiresNew(nt[0], hrorg, false);
            // }
        }
    }
    
    /**
     * ��ñ���У��ͨ���ĵ�����Ϣ
     * 
     * @param vos
     * @param context
     * @return ValidBudgetResultVO
     * @throws BusinessException
     */
    public ValidBudgetResultVO validateBudget(AggregatedValueObject[] vos, LoginContext context) throws BusinessException
    {
        ValidBudgetResultVO validVO = new ValidBudgetResultVO();
        //
        Vector<AggregatedValueObject> passVO = new Vector<AggregatedValueObject>();
        // ��ȡ��������Ա��ֵ
        String[] pkPsnjobs = new String[vos.length];
        for (int i = 0; i < vos.length; i++)
        {
            pkPsnjobs[i] = ((EntryapplyVO) vos[i].getParentVO()).getPk_psnjob();
            passVO.add(vos[i]);
        }
        // ����У��
        ValidateResultVO[] resultVOs = NCLocator.getInstance().lookup(IOrgBudgetQueryService.class).validateBudgetValue(context, pkPsnjobs);
        //
        String errorMsg = "";
        String hintMsg = "";
        // ����У��ȫ��ͨ��
        if (resultVOs != null && resultVOs.length > 0)
        {
            for (ValidateResultVO resultVO : resultVOs)
            {
                // У��δͨ��
                if (!resultVO.isValid())
                {
                    errorMsg += "\n" + resultVO.getHintMsg();
                    String pk_org = resultVO.getPk_org();
                    // �ų�����У��δͨ������֯����
                    for (AggregatedValueObject vo : vos)
                    {
                        PsnJobVO psnJobVO =
                            (PsnJobVO) getIPersistenceRetrieve().retrieveByPk(null, PsnJobVO.class,
                                ((EntryapplyVO) vo.getParentVO()).getPk_psnjob());
                        if (psnJobVO == null || pk_org.equals(psnJobVO.getPk_org())) passVO.removeElement(vo);
                    }
                }
                else if (resultVO.getHintMsg() != null)
                {
                    hintMsg += "\n" + resultVO.getHintMsg();
                }
            }
        }
        // �����ʼ�
        String pk_sort = "1001Z7BUDGET00000002";
        for (ValidateResultVO vo : resultVOs)
        {
            if (StringUtils.isEmpty(vo.getHintMsg()))
            {
                continue;
            }
            OrgVO org = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(vo.getPk_org());
            String hrorg = org == null ? vo.getPk_org() : org.getPk_org();
            INotice setvice = NCLocator.getInstance().lookup(INotice.class);
            NoticeTempletVO[] nt = setvice.queryDistributedTemplates(pk_sort, PubEnv.getPk_group(), hrorg, true);
            if (nt != null && nt.length > 0)
            {
                String content = nt[0].getContent();
                if (content != null && content.indexOf("<#reason#>") >= 0)
                {
                    nt[0].setContent(content.replace("<#reason#>", vo.getHintMsg()));
                }
                else
                {
                    nt[0].setContent(content + '\n' + vo.getHintMsg());
                }
                if (StringUtils.isEmpty(nt[0].getCurrentUserPk()) || nt[0].getCurrentUserPk().length() != 20)
                {
                    // ���ģ��ĵ�ǰ�û�Ϊ�գ����ϵ�ǰ�û�����NCϵͳ�û�
                    nt[0].setCurrentUserPk(PubEnv.getPk_user() != null && PubEnv.getPk_user().length() == 20 ? PubEnv.getPk_user()
                        : INCSystemUserConst.NC_USER_PK);
                }
                setvice.sendNotice_RequiresNew(nt[0], hrorg, false);
            }
        }
        validVO.setVos(passVO.toArray(new AggregatedValueObject[0]));
        validVO.setErrorMsg(errorMsg);
        validVO.setHintMsg(hintMsg);
        return validVO;
    }
    
    @Override
    public Object[] queryWaitforBills(Class<? extends AggregatedValueObject> aggVOClass, String billType, boolean isApproveSite,
            LoginContext context) throws BusinessException
    {
        String condition = HiSQLHelper.getQueryCondition(aggVOClass, billType, context, isApproveSite);
        return queryByCondition(aggVOClass, condition);
    }
    
    @Override
    public String[] getPsndocPks(String[] pks) throws BusinessException
    {
        if (pks == null || pks.length == 0)
        {
            return null;
        }
        // String[] docPKs = new String[pks.length];
        InSQLCreator isc = new InSQLCreator();
        String insql = isc.getInSQL(pks);
        String strCondition = "pk_psnjob in (" + insql + ")";
        PsnJobVO[] PsnJobVOs =
            (PsnJobVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, PsnJobVO.class, strCondition);
        if (ArrayUtils.isEmpty(PsnJobVOs))
        {
            return null;
        }
        // for (int i = 0; i < PsnJobVOs.length; i++)
        // {
        // docPKs[i] = PsnJobVOs[i].getPk_psndoc();
        // }
        // ��ѯ�����Ļ�����Ϣ�������ǵð���ԭ�ȵĹ�����¼����������
        List<String> listdocpks = new ArrayList<String>();
        for (int i = 0; i < pks.length; i++)
        {
            for (int j = 0; j < PsnJobVOs.length; j++)
            {
                if (pks[i].equals(PsnJobVOs[j].getPk_psnjob()))
                {
                    listdocpks.add(PsnJobVOs[j].getPk_psndoc());
                }
            }
            //
            // docPKs[i] = PsnJobVOs[i].getPk_psndoc();
        }
        return listdocpks.toArray(new String[0]);
    }
    
    @Override
    public int getBillCount(String billtype, String whereOrg) throws BusinessException
    {
        SuperVO[] billvos =
            NCLocator
                .getInstance()
                .lookup(IPersistenceRetrieve.class)
                .retrieveByClause(null, getBillClassName(billtype),
                    " approve_state = -1 and pk_billtype = '" + billtype + "' " + whereOrg + " ");
        
        if (billvos == null || billvos.length == 0)
        {
            return 0;
        }
        for (int i = 0; i < billvos.length; i++)
        {
            IFlowBizItf itf = NCObject.newInstance(billvos[i]).getBizInterface(IFlowBizItf.class);
            String type = StringUtils.isBlank(itf.getTranstype()) ? itf.getBilltype() : itf.getTranstype();
            WorkflownoteVO[] items =
                NCLocator.getInstance().lookup(IPFWorkflowQry.class)
                    .queryWorkitems(itf.getBillId(), type, WorkflowTypeEnum.Approveflow.getIntValue(), 0);
            if (items != null && items.length > 0)
            {
                return 1;
            }
        }
        return 0;
    }
    
    private Class getBillClassName(String billtype) throws BusinessException
    {
        try
        {
            if (EntrymngConst.BillTYPE_ENTRY.equals(billtype))
            {
                return EntryapplyVO.class;
            }
            else if ("6111".equals(billtype))
            {
                return Class.forName("nc.vo.trn.regmng.RegapplyVO");
            }
            else
            {
                return Class.forName("nc.vo.trn.transmng.StapplyVO");
            }
        }
        catch (Exception e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
    }
    
    @Override
    public HashMap<String, Object> manualExecBills(AggEntryapplyVO[] bills, LoginContext context, UFLiteralDate effectDate)
            throws BusinessException
    {
        HashMap<String, Object> result = execBills(bills, context, true);
        
        return result;
    }
    
    /**
     * ���ڲ��ϸ���Ƶı��ƽ���У�飬�Ա�ǰ̨������ʾ
     * 
     * @param context
     * @param billvos
     * @param isManualExe
     * @return
     * @throws BusinessException
     */
    @Override
    public String validateValidBudget(LoginContext context, AggEntryapplyVO[] billvos) throws BusinessException
    {
        
        // ǰ̨�Ե��ݵ�״̬���й��� �˴�����У�鲻���ǵ��ݵ�״̬
        String strWarningMsg = "";
        if (ArrayUtils.isEmpty(billvos))
        {
            return strWarningMsg;
        }
        
        ValidateResultVO resultVOs[] = null;
        
        String[] strPk_psnjobs = new String[billvos.length];
        for (int i = 0; i < strPk_psnjobs.length; i++)
        {
            strPk_psnjobs[i] = ((EntryapplyVO) billvos[i].getParentVO()).getPk_psnjob();
        }
        
        if (strPk_psnjobs != null && strPk_psnjobs.length > 0)
        {
            resultVOs = NCLocator.getInstance().lookup(IOrgBudgetQueryService.class).validateBudgetValue(context, strPk_psnjobs);
        }
        
        if (resultVOs != null)
        {
            for (ValidateResultVO resultVO : resultVOs)
            {
                // ֻ�Բ��ϸ���ƵĽ���У��
                if (resultVO.getHintMsg() != null && resultVO.isValid())
                {
                    strWarningMsg += "\n" + resultVO.getHintMsg();
                }
                else if (resultVO.getHintMsg() != null)
                {
                    throw new BusinessException(resultVO.getHintMsg());
                }
                
            }
        }
        return strWarningMsg;
    }
    
    @Override
    public AggEntryapplyVO[] queryByCondition(LoginContext context, String[] psndocPKS) throws BusinessException
    {
        InSQLCreator isc = new InSQLCreator();
        String insql = isc.getInSQL(psndocPKS);
        String condition = " pk_psndoc in (" + insql + " ) and approve_state <> 0 and approve_state <> 102 ";// ������״̬Ϊ������ͨ������ִ�еĹ���
        AggEntryapplyVO[] entryVOS = queryByCondition(context, condition);
        return entryVOS;
    }
}
