package nc.ui.trn.rds.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.funcnode.ui.action.GroupAction;
import nc.funcnode.ui.action.INCAction;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.trn.rds.IRdsQueryService;
import nc.md.data.access.DASFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IBusinessEntity;
import nc.md.model.impl.MDEnum;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hi.ref.TrnsTypeRefModel;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hr.uif2.view.HrBillListView;
import nc.ui.om.ref.HRDeptRefModel;
import nc.ui.om.ref.JobGradeRefModel2;
import nc.ui.om.ref.JobRankRefModel;
import nc.ui.om.ref.PostRefModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.action.SeparatorAction;
import nc.ui.trn.rds.model.RdsPsninfoModel;
import nc.ui.uif2.AppEvent;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnChgVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.RetireVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.hi.psndoc.enumeration.TrnseventEnum;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.joblevelsys.FilterTypeEnum;
import nc.vo.om.joblevelsys.JobLevelVO;
import nc.vo.om.post.PostVO;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.trn.pub.TRNConst;
import nc.vo.uap.rbac.FuncPermissionState;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * ����/��ְ��¼ �б���������
 */
public class RdsBillListView extends HrBillListView implements BillEditListener2, ChangeListener
{
    
    private static final long serialVersionUID = 1L;
    
    private List<Action> bodyActions;
    
    private SimpleDocServiceTemplate service = null;
    
    // ��ְ��Ŀ�в��ܱ༭����Ŀ,������֯������Ա���ע�Զ�������ܱ༭
    private InfoItemVO[] jobItems;
    
    /**
     * ������¼���е��ֶ�
     */
    private InfoItemVO[] getJobItems() throws BusinessException
    {
        
        if (jobItems == null)
        {
            jobItems =
                getService().queryByCondition(
                    InfoItemVO.class,
                    " pk_infoset in (select pk_infoset from hr_infoset where pk_infoset_sort = '" + TRNConst.INFOSET_SORT_PSNDOC
                        + "' and infoset_code = 'hi_psnjob' )");
        }
        return jobItems;
    }
    
    public void addLine(String tabCode) throws Exception
    {
        
        PsndocAggVO psndoc = (PsndocAggVO) getModel().getSelectedData();
        PsnJobVO srcVO = psndoc.getParentVO().getPsnJobVO();
        getBillListPanel().setEnabled(true);
        int preSelRow = getBillListPanel().getBodyTable(tabCode).getSelectedRow();
        getBillListPanel().getBodyScrollPane(tabCode).addLine();
        BillModel bm = getBillListPanel().getBodyBillModel(tabCode);
        int selRow = getBillListPanel().getBodyTable(tabCode).getSelectedRow();
        setFullTableEditable(bm, selRow);
        if (TRNConst.Table_NAME_DEPTCHG.equals(tabCode))
        {
            PsnJobVO psnjob = createNewPsnjob(srcVO, 1);
            psnjob.setModifiedtime(null);
            psnjob.setModifier(null);
            setBodyRowDataByMetaData(bm, psnjob, selRow);
            bm.setCellEditable(selRow, PsnJobVO.CLERKCODE, ((RdsPsninfoModel) getModel()).isBillCodeEditable());
        }
        else if (TRNConst.Table_NAME_DIMISSION.equals(tabCode))
        {
            PsnJobVO psnjob = createNewDimission(srcVO);
            psnjob.setModifiedtime(null);
            psnjob.setModifier(null);
            setBodyRowDataByMetaData(bm, psnjob, selRow);
            setDimiCellEditable(bm, selRow);
        }
        else if (TRNConst.Table_NAME_RETIRE.equals(tabCode))
        {
            // ���˴���
            RetireVO retire = createNewRetire(srcVO, tabCode, preSelRow);
            retire.setModifiedtime(null);
            retire.setModifier(null);
            setBodyRowDataByMetaData(bm, retire, selRow);
        }
        else if (TRNConst.Table_NAME_PART.equals(tabCode))
        {
            bm.setValueAt(UFBoolean.FALSE, selRow, PartTimeVO.ENDFLAG);
            bm.setValueAt(UFBoolean.FALSE, selRow, PartTimeVO.ISMAINJOB);
            bm.setValueAt(getModel().getContext().getPk_group(), selRow, PartTimeVO.PK_HRGROUP);
            bm.setValueAt(getModel().getContext().getPk_org(), selRow, PartTimeVO.PK_HRORG);
            bm.setValueAt(srcVO.getPk_psndoc(), selRow, PartTimeVO.PK_PSNDOC);
            bm.setValueAt(srcVO.getPk_psnorg(), selRow, PartTimeVO.PK_PSNORG);
            bm.setValueAt(getMaxAssgid(srcVO.getPk_psnorg()), selRow, PartTimeVO.ASSGID);
            bm.setValueAt(0, selRow, PartTimeVO.PSNTYPE);
            bm.setValueAt(0, selRow, PartTimeVO.RECORDNUM);
            bm.setValueAt(UFBoolean.TRUE, selRow, PartTimeVO.LASTFLAG);
            bm.setValueAt(srcVO.getShoworder(), selRow, PartTimeVO.SHOWORDER);
            bm.setValueAt(srcVO.getClerkcode(), selRow, PartTimeVO.CLERKCODE);
            bm.setValueAt(getModel().getContext().getPk_group(), selRow, PartTimeVO.PK_GROUP);
            bm.setValueAt(UFBoolean.TRUE, selRow, PartTimeVO.POSTSTAT);
            
            bm.setCellEditable(selRow, PsnJobVO.PK_POSTSERIES, true);
            bm.setCellEditable(selRow, PsnJobVO.SERIES, true);
            bm.loadLoadRelationItemValue();
        }
        else if (TRNConst.Table_NAME_TRIAL.equals(tabCode))
        {
            // ����
            TrialVO trial = createNewTrial(srcVO);
            trial.setModifiedtime(null);
            trial.setModifier(null);
            setBodyRowDataByMetaData(bm, trial, selRow);
        }
        else if (TRNConst.Table_NAME_PSNCHG.equals(tabCode))
        {
            // �������
            PsnChgVO psnchg = createNewPsnchg(srcVO);
            psnchg.setModifiedtime(null);
            psnchg.setModifier(null);
            setBodyRowDataByMetaData(bm, psnchg, selRow);
        }
    }
    
    private void setDimiCellEditable(BillModel billModel, int selRow) throws BusinessException
    {
        // �ɱ༭���ֶ� wanglqh
        String[] defaultAttr =
            new String[]{
                PsnJobVO.BEGINDATE,
                PsnJobVO.ENDDATE,
                PsnJobVO.DEPOSEMODE,
                PsnJobVO.SERIES,
                PsnJobVO.PK_PSNCL,
                PsnJobVO.PK_DEPT,
                PsnJobVO.MEMO,
                PsnJobVO.TRNSTYPE,
                PsnJobVO.TRNSREASON,PsnJobVO.MEMO,
                PsnJobVO.PK_POST,PsnJobVO.PK_JOBRANK,PsnJobVO.PK_JOBGRADE,PsnJobVO.PK_POSTSERIES,
                PsnJobVO.PK_JOB,PsnJobVO.PK_JOB_TYPE,PsnJobVO.PK_JOBRANK,PsnJobVO.PK_JOBGRADE};
        ArrayList<String> al = new ArrayList<String>();
        for (InfoItemVO item : getJobItems())
        {
            if (ArrayUtils.contains(defaultAttr, item.getItem_code())
                || (item.getCustom_attr() != null && item.getCustom_attr().booleanValue()))
            {
                continue;
            }
            al.add(item.getItem_code());
        }
        for (int i = 0; i < al.size(); i++)
        {
            billModel.setCellEditable(selRow, al.get(i), false);
        }
    }
    
    private void addTabActions()
    {
        
        List<Action> al = new ArrayList<Action>();
        List<Action> actions = getBodyActions();
        if (actions == null || actions.size() == 0)
        {
            return;
        }
        for (Action action : actions)
        {
            FuncPermissionState state =
                getModel().getContext().getFuncInfo().getButtonPermissionState((String) action.getValue(INCAction.CODE));
            if (FuncPermissionState.REGISTERD_HASPERMISSION == state || FuncPermissionState.NOREGISTERD == state)
            {
                // ûע��Ļ�����Ȩ�޵Ŀ�����ʾ
                al.add(action);
            }
        }
        
        getBillListPanel().getBodyTabbedPane().addTabActions(al);
        if (al != null)
        {
            for (int i = 0; i < al.size(); i++)
            {
                String name = (String) al.get(i).getValue(Action.NAME);
                KeyStroke ks = getKeyStroke(al.get(i));
                if (name != null && ks != null)
                {
                    InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                    ActionMap am = getActionMap();
                    map.put(ks, name);
                    am.put(name, al.get(i));
                }
            }
        }
    }
    
    @Override
    public void afterEdit(BillEditEvent e)
    {
        
        if (TRNConst.Table_NAME_TRIAL.equals(e.getTableCode()))
        {
            // ����
            if (TrialVO.TRIALRESULT.equals(e.getKey()))
            {
                Integer trialResult = (Integer) e.getValue();
                if (((RdsPsninfoModel) getModel()).getEditType() == RdsPsninfoModel.INSERT && trialResult != null && trialResult == 2)
                {
                    // �������ü�¼ʱ����ѡ���ӳ�ʹ����
                    MessageDialog.showWarningDlg(getModel().getContext().getEntranceUI(), null,
                        ResHelper.getString("6009tran", "06009tran0095")/*
                                                                         * @res "�������ü�¼����ѡ���ӳ������ڵ����ý��"
                                                                         */);
                    getBillListPanel().getBodyBillModel(e.getTableCode()).setValueAt(e.getOldValue(), e.getRow(), TrialVO.TRIALRESULT);
                    return;
                }
                // ת��ͨ����ת��δͨ��ʱ���ý���
                getBillListPanel().getBodyBillModel(e.getTableCode()).setValueAt(
                    UFBoolean.valueOf(trialResult != null && (trialResult == 1 || trialResult == 3)), e.getRow(), TrialVO.ENDFLAG);
            }
        }
        else if (TRNConst.Table_NAME_PART.equals(e.getTableCode()))
        {
            if (PartTimeVO.ENDFLAG.equals(e.getKey()))
            {
                // ������־ �������Ƿ��ڸ�Ϊfalse
                Boolean endflag = (Boolean) e.getValue();
                if (endflag != null && endflag.booleanValue())
                {
                    getBillListPanel().getBodyBillModel(e.getTableCode()).setValueAt(UFBoolean.FALSE, e.getRow(), PartTimeVO.POSTSTAT);
                }
            }
        }
        if (ArrayUtils.contains(new String[]{TRNConst.Table_NAME_DEPTCHG, TRNConst.Table_NAME_DIMISSION, TRNConst.Table_NAME_PART},
            e.getTableCode()))
        {
            afterEditPsnjob(e);
        }
        super.afterEdit(e);
    }
    
    private void afterEditPsnjob(BillEditEvent e)
    {
        
        PsnJobVO psn =
            (PsnJobVO) getBillListPanel().getBodyBillModel(e.getTableCode()).getBodyValueRowVO(e.getRow(), PsnJobVO.class.getName());
        if (psn == null)
        {
            return;
        }
        try
        {
            if (PsnJobVO.PK_GROUP.equals(e.getKey()))
            {
                // ����
                setValue(null, e, new String[]{PsnJobVO.PK_ORG, PsnJobVO.PK_DEPT});
                
                String pk_post = psn.getPk_post();
                if (pk_post != null)
                {
                    setValue(null, e, new String[]{
                        PsnJobVO.PK_PSNCL,
                        PsnJobVO.PK_POST,
                        PsnJobVO.PK_JOBGRADE,
                        PsnJobVO.PK_POSTSERIES,
                        PsnJobVO.PK_JOB,
                        PsnJobVO.SERIES,
                        PsnJobVO.PK_JOBRANK});
                }
            }
            else if (PsnJobVO.PK_ORG.equals(e.getKey()))
            {
                // ��֯
                setValue(null, e, new String[]{PsnJobVO.PK_DEPT});
                String pk_post = psn.getPk_post();
                if (pk_post != null)
                {
                    setValue(null, e, new String[]{
                        PsnJobVO.PK_POST,
                        PsnJobVO.PK_JOBGRADE,
                        PsnJobVO.PK_POSTSERIES,
                        PsnJobVO.PK_JOB,
                        PsnJobVO.SERIES,
                        PsnJobVO.PK_JOBRANK});
                }
            }
            else if (PsnJobVO.PK_DEPT.equals(e.getKey()))
            {
                // ����
                String pk_post = psn.getPk_post();
                if (pk_post != null)
                {
                    setValue(null, e, new String[]{
                        PsnJobVO.PK_POST,
                        PsnJobVO.PK_JOBGRADE,
                        PsnJobVO.PK_POSTSERIES,
                        PsnJobVO.PK_JOB,
                        PsnJobVO.SERIES,
                        PsnJobVO.PK_JOBRANK});
                }
//                //wanglqhר������������ɱ�����
//                DeptVO deptVO = psn.getPk_dept() == null ? null : getService().queryByPk(DeptVO.class, psn.getPk_dept());
//                if(deptVO != null){
//                	setValue(deptVO.getAttributeValue("glbdef3"), e, "jobglbdef9");// �ɱ�����
//                }
                
                
            }
            else if (PsnJobVO.PK_POST.equals(e.getKey()))
            {
                // ��λ
                String pk_post = (String) e.getValue();
                PostVO post = psn.getPk_post() == null ? null : getService().queryByPk(PostVO.class, pk_post, true);
                if (post != null)
                {
                    setValue(post.getPk_dept(), e, PsnJobVO.PK_DEPT);// ����
                    setValue(post.getPk_postseries(), e, PsnJobVO.PK_POSTSERIES);// ��λ����
                    setValue(post.getPk_job(), e, PsnJobVO.PK_JOB);// ְ��
                    JobVO jobVO = post.getPk_job() == null ? null : getService().queryByPk(JobVO.class, post.getPk_job());
                    if (jobVO != null)
                    {
                        setValue(jobVO.getPk_jobtype(), e, PsnJobVO.SERIES);// ְ�����
                    }
                    if (post.getEmployment() != null)
                    {
                        setValue(post.getEmployment(), e, PsnJobVO.OCCUPATION);// ְҵ
                    }
                    if (post.getWorktype() != null)
                    {
                        setValue(post.getWorktype(), e, PsnJobVO.WORKTYPE);// ����
                    }
                    
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(null, null, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setValue(defaultlevel, e, PsnJobVO.PK_JOBGRADE);// ְ��
                    setValue(defaultrank, e, PsnJobVO.PK_JOBRANK);// ְ��
                }
                else
                {
                    setValue(null, e, new String[]{
                        PsnJobVO.PK_POSTSERIES,
                        PsnJobVO.PK_JOB,
                        PsnJobVO.PK_JOBGRADE,
                        PsnJobVO.PK_JOBRANK,
                        PsnJobVO.SERIES});// ��λ����
                }
                
                if (post == null || post.getPk_job() != null)
                {
                    BillEditEvent event =
                        new BillEditEvent(getBillListPanel().getBodyItem(e.getTableCode(), PsnJobVO.PK_JOB), post == null ? null
                            : post.getPk_job(), PsnJobVO.PK_JOB, e.getRow(), e.getPos());
                    event.setTableCode(e.getTableCode());
                    afterEditPsnjob(event);
                }
                // ����ְ���Ƿ���Ա༭
                setJobGradeItemEdit(e.getTableCode(), e.getRow());
            }
            else if (PsnJobVO.PK_JOB.equals(e.getKey()))
            {
                // ְ��
                String pk_job = (String) e.getValue();
                JobVO job = psn.getPk_job() == null ? null : getService().queryByPk(JobVO.class, pk_job, true);
                String pk_post =
                    (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_POST + "_ID");
                if (job != null)
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(null, pk_job, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setValue(job.getPk_jobtype(), e, PsnJobVO.SERIES);// ְ�����
                    setValue(defaultlevel, e, PsnJobVO.PK_JOBGRADE);// ְ��
                    setValue(defaultrank, e, PsnJobVO.PK_JOBRANK);// ְ��
                }
                else
                {
                    setValue(null, e, new String[]{PsnJobVO.SERIES, PsnJobVO.PK_JOBGRADE, PsnJobVO.PK_JOBRANK});// ְ�����
                }
                // ����ְ���Ƿ���Ա༭
                setJobGradeItemEdit(e.getTableCode(), e.getRow());
            }
            else if (PsnJobVO.SERIES.equals(e.getKey()))
            {
                // ְ�����
                String series = (String) e.getValue();
                String pk_job =
                    (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_JOB + "_ID");
                String pk_post =
                    (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_POST + "_ID");
                if (StringUtils.isBlank(pk_job) && StringUtils.isNotBlank(series))
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class).getDefaultLevelRank(series, pk_job, null, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setValue(defaultlevel, e, PsnJobVO.PK_JOBGRADE);// ְ��
                    setValue(defaultrank, e, PsnJobVO.PK_JOBRANK);// ְ��
                }
                else if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series))
                {
                    setValue(null, e, PsnJobVO.PK_JOBGRADE);// ְ��
                    setValue(null, e, PsnJobVO.PK_JOBRANK);// ְ��
                }
                // ����ְ���Ƿ���Ա༭
                setJobGradeItemEdit(e.getTableCode(), e.getRow());
            }
            else if (PsnJobVO.PK_POSTSERIES.equals(e.getKey()))
            {
                // ��λ����
                String pk_postseries = (String) e.getValue();
                String series =
                    (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.SERIES + "_ID");
                String pk_job =
                    (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_JOB + "_ID");
                String pk_post =
                    (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_POST + "_ID");
                if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series) && StringUtils.isBlank(pk_post)
                    && StringUtils.isNotBlank(pk_postseries))
                {
                    String defaultlevel = "";
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, null);
                    if (!resultMap.isEmpty())
                    {
                        defaultlevel = resultMap.get("defaultlevel");
                        defaultrank = resultMap.get("defaultrank");
                    }
                    
                    setValue(defaultlevel, e, PsnJobVO.PK_JOBGRADE);// ְ��
                    setValue(defaultrank, e, PsnJobVO.PK_JOBRANK);// ְ��
                }
                else if (StringUtils.isBlank(pk_job) && StringUtils.isBlank(series) && StringUtils.isBlank(pk_post)
                    && StringUtils.isBlank(pk_postseries))
                {
                    setValue(null, e, PsnJobVO.PK_JOBGRADE);// ְ��
                    setValue(null, e, PsnJobVO.PK_JOBRANK);// ְ��
                }
                // ����ְ���Ƿ���Ա༭
                setJobGradeItemEdit(e.getTableCode(), e.getRow());
            }
            else if (PsnJobVO.PK_JOBGRADE.equals(e.getKey()))
            {
                // ְ��
                String pk_jobgrage = psn.getPk_jobgrade();
                JobLevelVO jobgrade = pk_jobgrage == null ? null : getService().queryByPk(JobLevelVO.class, pk_jobgrage, true);
                if (jobgrade != null)
                {
                    String series =
                        (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.SERIES + "_ID");
                    String pk_job =
                        (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_JOB + "_ID");
                    String pk_postseries =
                        (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(),
                            PsnJobVO.PK_POSTSERIES + "_ID");
                    String pk_post =
                        (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_POST + "_ID");
                    String defaultrank = "";
                    Map<String, String> resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getDefaultLevelRank(series, pk_job, pk_postseries, pk_post, pk_jobgrage);
                    if (!resultMap.isEmpty())
                    {
                        defaultrank = resultMap.get("defaultrank");
                    }
                    setValue(defaultrank, e, PsnJobVO.PK_JOBRANK);// ְ��
                }
                else
                {
                    setValue(null, e, new String[]{PsnJobVO.PK_JOBRANK});// ְ��
                }
            }
            else if (PsnJobVO.TRNSEVENT.equals(e.getKey()))
            {
                // �춯�¼�
                int event = psn.getTrnsevent();
                if (event == 4 || event == 5)
                {
                    setValue(e.getOldValue(), e, new String[]{PsnJobVO.TRNSEVENT});
                    MessageDialog.showWarningDlg(getModel().getContext().getEntranceUI(), null,
                        ResHelper.getString("6009tran", "06009tran0096")/*
                                                                         * @res "�춯�¼�����ѡ����ְ����ְ��䶯"
                                                                         */);
                }
                else
                {
                    setValue(null, e, new String[]{PsnJobVO.TRNSTYPE});
                }
            }
            getBillListPanel().getBodyBillModel(e.getTableCode()).loadLoadRelationItemValue();
        }
        catch (BusinessException ex)
        {
            Logger.error(ex.getMessage(), ex);
        }
    }
    
    private void setJobGradeItemEdit(String strTabCode, int row)
    {
        String series = (String) getBillListPanel().getBodyBillModel(strTabCode).getValueAt(row, PsnJobVO.SERIES + "_ID");
        String pk_job = (String) getBillListPanel().getBodyBillModel(strTabCode).getValueAt(row, PsnJobVO.PK_JOB + "_ID");
        String pk_postseries = (String) getBillListPanel().getBodyBillModel(strTabCode).getValueAt(row, PsnJobVO.PK_POSTSERIES + "_ID");
        String pk_post = (String) getBillListPanel().getBodyBillModel(strTabCode).getValueAt(row, PsnJobVO.PK_POST + "_ID");
        
        if (StringUtils.isBlank(series) && StringUtils.isBlank(pk_postseries) && StringUtils.isBlank(pk_job)
            && StringUtils.isBlank(pk_post))
        {
            setBodyItemEdit(strTabCode, row, false, PsnJobVO.PK_JOBGRADE);
        }
        else
        {
            setBodyItemEdit(strTabCode, row, true, PsnJobVO.PK_JOBGRADE);
        }
    }
    
    private void setBodyItemEdit(String strTabCode, int iRowIndex, boolean isEdit, String... strBodyItemKeys)
    {
        if (strBodyItemKeys == null || strBodyItemKeys.length == 0)
        {
            return;
        }
        BillModel billModel = strTabCode == null ? getBillListPanel().getBodyBillModel() : getBillListPanel().getBodyBillModel(strTabCode);
        if (billModel == null)
        {
            return;
        }
        for (String strItemKey : strBodyItemKeys)
        {
            billModel.setCellEditable(iRowIndex, strItemKey, isEdit);
        }
    }
    
    @Override
    public boolean beforeEdit(BillEditEvent e)
    {
        
        if (e.getRow() < 0 || e.getKey() == null)
        {
            return false;
        }
        if (e.getRow() != getBillListPanel().getBodyBillModel(getCurrentTabCode()).getEditRow())
        {
            return false;
        }
        if (ArrayUtils.contains(new String[]{TRNConst.Table_NAME_DEPTCHG, TRNConst.Table_NAME_DIMISSION, TRNConst.Table_NAME_PART},
            e.getTableCode()))
        {
            // ��ְ��¼����ְ��¼��beforedit�¼�
            return beforeEditPsnjob(e);
        }
        else if (TRNConst.Table_NAME_TRIAL.equals(e.getTableCode()))
        {
        }
        else if (TRNConst.Table_NAME_PSNCHG.equals(e.getTableCode()))
        {
            if (PsnChgVO.PK_CORP.equals(e.getKey()))
            {
                BillItem bi = (BillItem) e.getSource();
                // ��ְ��ֻ֯�ܲ�����˺�������֯
                String where =
                // " and pk_adminorg in ( select pk_adminorg from org_admin_enable ) and pk_adminorg in ( select pk_corp from org_corp where enablestate = 2 )";
                    " and pk_adminorg in ( select pk_adminorg from org_admin_enable ) ";
                
                ((UIRefPane) bi.getComponent()).getRefModel().addWherePart(where);
            }
        }
        return true;
    }
    
    private boolean beforeEditPsnjob(BillEditEvent e)
    {
        
        PsnJobVO psn =
            (PsnJobVO) getBillListPanel().getBodyBillModel(e.getTableCode()).getBodyValueRowVO(e.getRow(), PsnJobVO.class.getName());
        if (psn == null)
        {
            return false;
        }
        
        String pk_org = getModel().getContext().getPk_org();
        BillItem bi = (BillItem) e.getSource();
        if (PsnJobVO.TRNSTYPE.equals(e.getKey()))
        {
            // �춯����
            int tranevent = psn.getTrnsevent();
            TrnsTypeRefModel model = new TrnsTypeRefModel(ResHelper.getString("6007psn", "16007psn0007")/*
                                                                                                         * @res
                                                                                                         * "�춯ԭ��"
                                                                                                         */, tranevent);
            model.setPk_org(pk_org);
            ((UIRefPane) bi.getComponent()).setRefModel(model);
        }
        else if (PsnJobVO.PK_ORG.equals(e.getKey()))
        {
            // ��֯
            String pk_group = psn.getPk_group();
            ((UIRefPane) bi.getComponent()).getRefModel().setPk_group(pk_group);
            ((UIRefPane) bi.getComponent()).getRefModel().setUseDataPower(false);
            String powerSql =
                HiSQLHelper.getPsnPowerSql(pk_group, HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE, "org_orgs");
            String where = " and 1 =1 ";
            
            if (!StringUtils.isBlank(powerSql))
            {
                where += " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ")";
            }
            
            if (!e.getTableCode().equals(TRNConst.Table_NAME_PART))
            {
                where += " and pk_adminorg in ( select pk_adminorg from org_admin_enable ) ";
            }
            // �𼶹ܿأ�����/��ְ��¼����Ա��ְ��¼-��֯��
            // where +=
            // " and org_adminorg.pk_adminorg in (select pk_org from hr_relation_org where pk_hrorg = '0001H2100000000022YK' and business_type = 'PSNDOC00000000000000')";
            
            String functioncode = "";
            if (e.getTableCode().equals("hi_psndoc_dimission"))
            {
                functioncode = "@@@@Z710000000006M5D";
            }
            else if (e.getTableCode().equals("hi_psndoc_deptchg"))
            {
                functioncode = "@@@@Z710000000006M8G";
            }
            
            where +=
                " and pk_adminorg in(select pk_org from hr_relation_org where pk_hrorg = '" + getModel().getContext().getPk_org() + "')";
            
            ((UIRefPane) bi.getComponent()).getRefModel().addWherePart(where);
        }
        else if (PsnJobVO.PK_PSNCL.equals(e.getKey()))
        {
            // ��Ա���
            String pk_group = psn.getPk_group();
            ((UIRefPane) bi.getComponent()).getRefModel().setPk_group(pk_group);
            String powerSql =
                HiSQLHelper.getPsnPowerSql(pk_group, HICommonValue.RESOUCECODE_PSNCL, IRefConst.DATAPOWEROPERATION_CODE, "bd_psncl");
            if (!StringUtils.isBlank(powerSql))
            {
                ((UIRefPane) bi.getComponent()).getRefModel().addWherePart(" and " + powerSql);
            }
        }
        else if (PsnJobVO.PK_DEPT.equals(e.getKey()))
        {
            // ����
            String pkOrg = psn.getPk_org();
            ((UIRefPane) bi.getComponent()).setPk_org(pkOrg);
            
            String cond = " and depttype <> 1 ";
            String powerSql =
                HiSQLHelper
                    .getPsnPowerSql(psn.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, "org_dept");
            if (!StringUtils.isBlank(powerSql))
            {
                cond += " and " + powerSql;
            }
            if (e.getTableCode().equals(TRNConst.Table_NAME_PART))
            {
                ((HRDeptRefModel) ((UIRefPane) bi.getComponent()).getRefModel()).setShowDisbleOrg(true);
            }
            else
            {
                ((HRDeptRefModel) ((UIRefPane) bi.getComponent()).getRefModel()).setShowDisbleOrg(false);
            }
            ((UIRefPane) bi.getComponent()).getRefModel().addWherePart(cond);
        }
        else if (PsnJobVO.PK_POST.equals(e.getKey()))
        {
            // ��λ
            PostRefModel postModel = (PostRefModel) ((UIRefPane) bi.getComponent()).getRefModel();
            postModel.setPk_group(psn.getPk_group());
            postModel.setPk_org(psn.getPk_org());
            String cond = " and om_post.hrcanceled = 'N' ";
            if (!StringUtils.isBlank(psn.getPk_dept()))
            {
                postModel.setPkdept(psn.getPk_dept());
                // �����ѳ�����λ�������¼��λ���ܲ��յ��ѳ�����λ��-- mizhl 2015-2-5 �麣�а�Ӥ����ͯ�ٻ����޹�˾
                postModel.addWherePart(cond);
            }
            else
            {
                postModel.setPkdept(null);
                String powerSql =
                    HiSQLHelper.getPsnPowerSql(psn.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE,
                        "org_dept");
                if (!StringUtils.isBlank(powerSql))
                {
                    cond += " and om_post.pk_dept in ( select pk_dept from org_dept where  " + powerSql + " ) ";
                    postModel.addWherePart(cond);
                }
                else
                {
                    postModel.addWherePart(cond);
                }
            }
        }
        else if (PsnJobVO.PK_POSTSERIES.equals(e.getKey()))
        {
            String pk_job = (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_JOB + "_ID");
            if (StringUtils.isNotBlank(pk_job))
            {
                return false;
            }
        }
        else if (PsnJobVO.PK_JOB.equals(e.getKey()))
        {
            UIRefPane jobRef = (UIRefPane) bi.getComponent();
            jobRef.getRefModel().setPk_group(psn.getPk_group());
        }
        else if (PsnJobVO.PK_JOBGRADE.equals(e.getKey()))
        {
            // ְ��
            String series = (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.SERIES + "_ID");
            String pk_job = (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_JOB + "_ID");
            String pk_postseries =
                (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_POSTSERIES + "_ID");
            String pk_post =
                (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_POST + "_ID");
            
            BillItem item = (BillItem) e.getSource();
            if (item != null)
            {
                FilterTypeEnum filterType = null;
                String gradeSource = "";
                Map<String, Object> resultMap = null;
                try
                {
                    resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getLevelRankCondition(series, pk_job, pk_postseries, pk_post);
                }
                catch (BusinessException e1)
                {
                    Logger.error(e1.getMessage(), e1);
                }
                
                if (!resultMap.isEmpty())
                {
                    filterType = (FilterTypeEnum) resultMap.get("filterType");
                    gradeSource = (String) resultMap.get("gradeSource");
                }
                
                ((JobGradeRefModel2) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);
                if (StringUtils.isBlank(series) && StringUtils.isBlank(pk_postseries) && StringUtils.isBlank(pk_job)
                    && StringUtils.isBlank(pk_post)) item.setEnabled(false);
                else
                    item.setEnabled(true);
            }
        }
        else if (PsnJobVO.PK_JOBRANK.equals(e.getKey()))
        {
            // ְ��
            BillItem item = (BillItem) e.getSource();
            String pk_jobrank =
                (String) (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_JOBRANK + "_ID");
            if (StringUtils.isBlank(pk_jobrank))
            {
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(null, null);
                // ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel("");
                return true;
            }
            
            String pk_jobgrade =
                (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_JOBGRADE + "_ID");
            
            String series = (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.SERIES + "_ID");
            String pk_job = (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_JOB + "_ID");
            String pk_postseries =
                (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_POSTSERIES + "_ID");
            String pk_post =
                (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_POST + "_ID");
            if (item != null)
            {
                FilterTypeEnum filterType = null;
                String gradeSource = "";
                Map<String, Object> resultMap = null;
                try
                {
                    resultMap =
                        NCLocator.getInstance().lookup(IPsndocQryService.class)
                            .getLevelRankCondition(series, pk_job, pk_postseries, pk_post);
                }
                catch (BusinessException e1)
                {
                    Logger.error(e1.getMessage(), e1);
                }
                
                if (!resultMap.isEmpty())
                {
                    filterType = (FilterTypeEnum) resultMap.get("filterType");
                    gradeSource = (String) resultMap.get("gradeSource");
                }
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_joblevel(pk_jobgrade);
                ((JobRankRefModel) ((UIRefPane) item.getComponent()).getRefModel()).setPk_filtertype(gradeSource, filterType);
            }
        }
        else if (PsnJobVO.SERIES.equals(e.getKey()))
        {
            String pk_post =
                (String) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.PK_POST + "_ID");
            if (StringUtils.isNotBlank(pk_post))
            {
                return false;
            }
        }
        else if (PsnJobVO.POSTSTAT.equals(e.getKey()))
        {
            // �Ƿ��ڸ�
            if (TRNConst.Table_NAME_PART.equals(e.getTableCode()))
            {
                Boolean isEnd = (Boolean) getBillListPanel().getBodyBillModel(e.getTableCode()).getValueAt(e.getRow(), PsnJobVO.ENDFLAG);
                return isEnd != null && !isEnd;
            }
        }
        return true;
    }
    
    @Override
    public void bodyRowChange(BillEditEvent e)
    {
        
        if (e.getSource() != getBillListPanel().getHeadTable())
        {
            return;
        }
        super.bodyRowChange(e);
    }
    
    private PsnJobVO createNewDimission(PsnJobVO srcVO)
    {
        
        PsnJobVO psnjob = (PsnJobVO) srcVO.clone();
        psnjob.setEndflag(UFBoolean.TRUE);
        psnjob.setIsmainjob(UFBoolean.TRUE);
        psnjob.setLastflag(UFBoolean.TRUE);
        psnjob.setPk_hrgroup(getModel().getContext().getPk_group());
        psnjob.setPk_group(getModel().getContext().getPk_group());
        psnjob.setPk_hrorg(getModel().getContext().getPk_org());
        psnjob.setPoststat(UFBoolean.FALSE);
        psnjob.setBegindate(null);
        psnjob.setEnddate(null);
        psnjob.setPk_psncl(null);
        //wanglqh��ְǰ����������Ϣ
        psnjob.setPk_post(srcVO.getPk_post());
		psnjob.setPk_postseries(srcVO.getPk_postseries());
		psnjob.setPk_job(srcVO.getPk_job());
		psnjob.setPk_jobgrade(srcVO.getPk_jobgrade());
		psnjob.setPk_jobrank(srcVO.getPk_jobrank());
		psnjob.setSeries(srcVO.getSeries());
		psnjob.setWorktype(srcVO.getWorktype());
		psnjob.setOccupation(srcVO.getOccupation());
        
        psnjob.setPk_psnjob(null);
        psnjob.setPsntype(0);
        psnjob.setDeposemode(null);
        psnjob.setJobmode(null);
        psnjob.setMemo(null);
        psnjob.setOccupation(null);
        psnjob.setOribillpk(null);
        psnjob.setOribilltype(null);
        psnjob.setRecordnum(0);
        psnjob.setShoworder(srcVO.getShoworder());
        psnjob.setTrnsevent((Integer) ((MDEnum) TrnseventEnum.DISMISSION).value());
        psnjob.setTrnstype(null);
        psnjob.setTrnsreason(null);
        psnjob.setTrial_flag(UFBoolean.FALSE);
        psnjob.setTrial_type(null);
        return psnjob;
    }
    
    /**
     * ����һ�����������
     */
    private PsnChgVO createNewPsnchg(PsnJobVO srcVO)
    {
        
        PsnChgVO psnchg = new PsnChgVO();
        psnchg.setRecordnum(0);
        psnchg.setLastflag(UFBoolean.TRUE);
        psnchg.setPk_group(getModel().getContext().getPk_group());
        psnchg.setPk_org(getModel().getContext().getPk_org());
        psnchg.setPk_psndoc(srcVO.getPk_psndoc());
        psnchg.setPk_psnorg(srcVO.getPk_psnorg());
        psnchg.setAssgid(srcVO.getAssgid());
        return psnchg;
    }
    
    /**
     * ����һ���µĹ�����¼
     * @param srcVO
     * @param addType 1 ���� 2 ����
     * @return PsnJobVO
     */
    private PsnJobVO createNewPsnjob(PsnJobVO srcVO, int addType)
    {
        
        PsnJobVO psnjob = null;
        if (addType == 1)
        {
            psnjob = (PsnJobVO) srcVO.clone();
        }
        else
        {
            psnjob = new PsnJobVO();
        }
        psnjob.setAssgid(1);
        psnjob.setEndflag(UFBoolean.FALSE);
        psnjob.setIsmainjob(UFBoolean.TRUE);
        psnjob.setLastflag(UFBoolean.TRUE);
        psnjob.setPk_hrgroup(getModel().getContext().getPk_group());
        psnjob.setPk_hrorg(getModel().getContext().getPk_org());
        psnjob.setPk_group(getModel().getContext().getPk_group());
        psnjob.setPk_psndoc(srcVO.getPk_psndoc());
        psnjob.setPk_psnorg(srcVO.getPk_psnorg());
        psnjob.setPoststat(UFBoolean.TRUE);
        psnjob.setPsntype(0);
        psnjob.setRecordnum(0);
        psnjob.setShoworder(srcVO.getShoworder());
        psnjob.setTrnsevent((Integer) ((MDEnum) TrnseventEnum.TRANS).value());
        psnjob.setTrnstype(null);
        psnjob.setTrnsreason(null);
        psnjob.setTrial_flag(UFBoolean.FALSE);
        psnjob.setTrial_type(null);
        psnjob.setBegindate(null);
        psnjob.setEnddate(null);
        psnjob.setPk_psnjob(null);
        psnjob.setClerkcode(srcVO.getClerkcode());// Ա����Ҫ�����Ƿ��Զ����ɸ�ֵ
        return psnjob;
    }
    
    /**
     * ����һ�����˴�����¼
     * @param tabCode
     * @param preSelRow
     */
    private RetireVO createNewRetire(PsnJobVO srcVO, String tabCode, int preSelRow)
    {
        
        RetireVO retire = null;
        if (preSelRow < 0)
        {
            retire = new RetireVO();
        }
        else
        {
            BillModel bm = getBillListPanel().getBodyBillModel(tabCode);
            RetireVO vos = (RetireVO) bm.getBodyValueRowVO(preSelRow, RetireVO.class.getName());
            retire = (RetireVO) vos.clone();
            retire.setBegindate(null);
            retire.setEnddate(null);
        }
        retire.setPk_org(getModel().getContext().getPk_org());
        retire.setPk_group(getModel().getContext().getPk_group());
        retire.setLastflag(UFBoolean.TRUE);
        retire.setRecordnum(0);
        retire.setPk_psndoc(srcVO.getPk_psndoc());
        retire.setPk_psnorg(srcVO.getPk_psnorg());
        retire.setPk_psndoc_sub(null);
        return retire;
    }
    
    /**
     * ����һ�����ü�¼
     */
    private TrialVO createNewTrial(PsnJobVO srcVO)
    {
        
        TrialVO trial = new TrialVO();
        trial.setPk_group(srcVO.getPk_group());
        trial.setPk_hrorg(srcVO.getPk_hrorg());
        trial.setPk_org(srcVO.getPk_org());
        trial.setPk_psndoc(srcVO.getPk_psndoc());
        trial.setPk_psnjob(srcVO.getPk_psnjob());
        trial.setPk_psnorg(srcVO.getPk_psnorg());
        trial.setAssgid(1);
        trial.setLastflag(UFBoolean.TRUE);
        trial.setRecordnum(0);
        return trial;
    }
    
    /**
     * �ǿ�У��
     * @throws ValidationException
     */
    public void dataNotNullValidate() throws ValidationException
    {
        BillPanelUtils.listNotNullValidate(getBillListPanel(), IBillItem.BODY);
    }
    
    public void editLine(String curTabCode, boolean isInJob) throws BusinessException
    {
        
        getBillListPanel().setEnabled(true);
        BillModel bm = getBillListPanel().getBodyBillModel(curTabCode);
        int selRow = getBillListPanel().getBodyTable(curTabCode).getSelectedRow();
        setFullTableEditable(bm, selRow);
        if (TRNConst.Table_NAME_TRIAL.equals(curTabCode))
        {
            setTrialCellEnable(bm, isInJob, selRow);
        }
        else if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode) || TRNConst.Table_NAME_DEPTCHG.equals(curTabCode))
        {
            bm.setCellEditable(selRow, PsnJobVO.CLERKCODE, true);
            CircularlyAccessibleValueObject selvo = getBodySelectVO();
            if (selvo.getAttributeValue("lastflag") != null && !((UFBoolean) selvo.getAttributeValue("lastflag")).booleanValue())
            {
                // ��ʷ���Ƿ��ڸڲ����޸�
                bm.setCellEditable(selRow, PsnJobVO.POSTSTAT, false);
                if (selvo.getAttributeValue(PsnJobVO.TRNSEVENT) != null
                    && (((Integer) selvo.getAttributeValue(PsnJobVO.TRNSEVENT)) == 4 || ((Integer) selvo
                        .getAttributeValue(PsnJobVO.TRNSEVENT)) == 5))
                {
                    setDimiCellEditable(bm, selRow);
                    if (((Integer) selvo.getAttributeValue(PsnJobVO.TRNSEVENT)) == 5)
                    {
                        bm.setCellEditable(selRow, PsnJobVO.TRNSTYPE, false);
                    }
                }
            }
            if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode))
            {
                bm.setCellEditable(selRow, PsnJobVO.POSTSTAT, false);
                if (selvo.getAttributeValue("lastflag") != null && ((UFBoolean) selvo.getAttributeValue("lastflag")).booleanValue())
                {
                    setDimiCellEditable(bm, selRow);
                    if (selvo.getAttributeValue(PsnJobVO.TRNSEVENT) != null && ((Integer) selvo.getAttributeValue(PsnJobVO.TRNSEVENT)) == 5)
                    {
                        bm.setCellEditable(selRow, PsnJobVO.TRNSTYPE, false);
                    }
                }
            }
        }
        else if (TRNConst.Table_NAME_PART.equals(curTabCode))
        {
            CircularlyAccessibleValueObject selvo = getBodySelectVO();
            if (selvo.getAttributeValue("lastflag") != null && !((UFBoolean) selvo.getAttributeValue("lastflag")).booleanValue())
            {
                // ��ʷ���Ƿ��ڸ��Ƿ���������޸�
                bm.setCellEditable(selRow, PsnJobVO.POSTSTAT, false);
                bm.setCellEditable(selRow, PsnJobVO.ENDFLAG, false);
            }
        }
    }
    
    public List<Action> getBodyActions()
    {
        
        return bodyActions;
    }
    
    /**
     * �õ���ǰҳǩѡ�����VO
     */
    public CircularlyAccessibleValueObject getBodySelectVO()
    {
        
        String tabCode = getCurrentTabCode();
        int row = getBillListPanel().getBodyTable(tabCode).getSelectedRow();
        if (row < 0)
        {
            return null;
        }
        CircularlyAccessibleValueObject vo = getBillListPanel().getBodyBillModel(tabCode).getBodyValueRowVO(row, getCurClassName());
        if (vo == null)
        {
            return null;
        }
        return vo;
    }
    
    /**
     * �õ���ǰҳǩ��VO��
     */
    public String getCurClassName()
    {
        
        String tabCode = getCurrentTabCode();
        if (TRNConst.Table_NAME_DEPTCHG.equals(tabCode) || TRNConst.Table_NAME_DIMISSION.equals(tabCode))
        {
            return PsnJobVO.class.getName();
        }
        else if (TRNConst.Table_NAME_PART.equals(tabCode))
        {
            return PartTimeVO.class.getName();
        }
        else if (TRNConst.Table_NAME_PSNCHG.equals(tabCode))
        {
            return PsnChgVO.class.getName();
        }
        else if (TRNConst.Table_NAME_RETIRE.equals(tabCode))
        {
            return RetireVO.class.getName();
        }
        else
        {
            return TrialVO.class.getName();
        }
    }
    
    public String getMataTabCode()
    {
        if (TRNConst.Table_NAME_DEPTCHG.equals(getCurrentTabCode()) || TRNConst.Table_NAME_DIMISSION.equals(getCurrentTabCode()))
        {
            return PsnJobVO.getDefaultTableName();
        }
        else if (TRNConst.Table_NAME_PART.equals(getCurrentTabCode()))
        {
            return PartTimeVO.getDefaultTableName();
        }
        return getCurrentTabCode();
    }
    
    /**
     * �õ���ǰҳǩ����
     */
    public String getCurrentTabCode()
    {
        
        return getBillListPanel().getBodyTabbedPane().getSelectedTableCode();
    }
    
    /**
     * ������ְ��֯pk�õ���HR��֯pk<br>
     * <b>ְ����Ϊ���ż���</b>
     * @param pk_org
     * @return String
     */
    
    private SimpleDocServiceTemplate getService()
    {
        if (service == null)
        {
            service = new SimpleDocServiceTemplate("RdsBillListView");
        }
        return service;
    }
    
    private KeyStroke getKeyStroke(Action action)
    {
        
        KeyStroke ks = null;
        if (action != null && !(action instanceof GroupAction || action instanceof SeparatorAction))
        {
            // if (ks == null) {
            Object obj = action.getValue(Action.ACCELERATOR_KEY);
            if (obj instanceof KeyStroke)
            {
                ks = (KeyStroke) obj;
            }
            // }
        }
        return ks;
    }
    
    /**
     * �õ���֯��ϵ��������ְID
     */
    private Integer getMaxAssgid(String pk_psnorg) throws BusinessException
    {
        
        return NCLocator.getInstance().lookup(IRdsQueryService.class).getMaxAssgId(pk_psnorg);
    }
    
    /**
     * �õ���ǰ��������Ӧ�õ�recordnum
     */
    private Integer getRecordnum(BillModel bm, int selRow)
    {
        
        TrialVO vo = (TrialVO) bm.getBodyValueRowVO(selRow + 1, TrialVO.class.getName());
        return vo.getRecordnum() + 1;
    }
    
    @Override
    protected void processBillListData(BillListData bld)
    {
        super.processBillListData(bld);
        if (TRNConst.NODECODE_TRANSRDS.equals(getModel().getContext().getNodeCode()))
        {
            // ���ؼ�ְ�Ӽ�
            BillItem[] billItems = bld.getBodyBillModel(TRNConst.Table_NAME_PART).getBodyItems();
            for (int i = 0; i < billItems.length; i++)
            {
                billItems[i].setShow(false);
            }
        }
    }
    
    @Override
    public void initUI()
    {
        
        super.initUI();
        BillPanelUtils.dealWithTablePopopMenu(getBillListPanel().getParentListPanel());
        addTabActions();
        getBillListPanel().setChildMultiSelect(false);
        setBillListPanelValueSetter(new MDBillListPanelValueSetter()
        {
            
            @Override
            public void setBodyData(BillListPanel listPanel, Object selectedData)
            {
                
                if (selectedData == null)
                {
                    super.setBodyData(listPanel, null);
                    return;
                }
                PsndocAggVO aggvo =
                    ((RdsPsninfoModel) getModel()).querySubVO((PsndocAggVO) selectedData, new String[]{getCurrentTabCode()});
                super.setBodyData(listPanel, aggvo);
            }
            
            private void setHeaderOtherRowData(BillListPanel listPanel, Object objRowData, IBusinessEntity be, int iRowIndex)
            {
                BillItem[] items = getBillListPanel().getBillListData().getHeadItems();
                if (items == null)
                {
                    return;
                }
                NCObject ncObject = DASFacade.newInstanceWithContainedObject(be, objRowData);
                if (ncObject == null)
                {
                    return;
                }
                PsndocVO psndocVO = (PsndocVO) ncObject.getModelConsistObject();
                if (psndocVO == null)
                {
                    return;
                }
                // ���ñ�ͷ����
                for (BillItem item : items)
                {
                    if (item.getKey().startsWith("hi_psnjob_") && item.getMetaDataProperty() != null)
                    {
                        Object value = psndocVO.getAttributeValue(item.getKey());
                        if (item.isIsDef())
                        {
                            value = item.converType(value);
                        }
                        getBillListPanel().getHeadBillModel().setValueAt(value, iRowIndex, item.getKey());
                    }
                }
            }
            
            @Override
            public void setHeaderDatas(BillListPanel listPanel, Object[] allDatas)
            {
                
                if (allDatas == null || allDatas.length == 0)
                {
                    return;
                }
                PsndocVO[] psns = new PsndocVO[allDatas.length];
                for (int i = 0; i < allDatas.length; i++)
                {
                    psns[i] = ((PsndocAggVO) allDatas[i]).getParentVO();
                }
                listPanel.getHeadBillModel().setBodyDataVO(psns);
                listPanel.getBillListData().getHeadBillModel().loadLoadRelationItemValue();
                listPanel.getBillListData().getHeadBillModel().execLoadFormula();
            }
            
            @Override
            public void setHeaderRowData(BillListPanel listPanel, Object rowData, int row)
            {
                
                super.setHeaderRowData(listPanel, rowData, row);
                IBusinessEntity be = getBillListPanel().getBillListData().getBillTempletVO().getHeadVO().getBillMetaDataBusinessEntity();
                setHeaderOtherRowData(listPanel, rowData, be, row);
                listPanel.getBillListData().getHeadBillModel().loadLoadRelationItemValue();
                listPanel.getBillListData().getHeadBillModel().execLoadFormula();
            }
        });
        
        // ��ְ��λ
        BillItem item = getBillListPanel().getBodyItem(TRNConst.Table_NAME_PSNCHG, PsnChgVO.PK_CORP);
        if (item != null)
        {
            // AdminOrgDefaultRefModel model = new AdminOrgDefaultRefModel();
            ((UIRefPane) (item.getComponent())).getRefModel().setPk_group(PubEnv.getPk_group());
            
            String powerSql =
                HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_ORG, IRefConst.DATAPOWEROPERATION_CODE,
                    "org_orgs");
            if (!StringUtils.isBlank(powerSql))
            {
                ((UIRefPane) (item.getComponent())).getRefModel().addWherePart(
                    " and pk_adminorg in ( select pk_org from org_orgs where " + powerSql + ")");
            }
        }
        
        // ����ҳǩ�ı��¼�
        getBillListPanel().getBodyTabbedPane().addChangeListener(this);
        getBillListPanel().addBodyEditListener(this);
        getBillListPanel().getHeadTable().setColumnSelectionAllowed(false);
    }
    
    public void insertLine(String curTabCode)
    {
        
        PsndocAggVO psndoc = (PsndocAggVO) getModel().getSelectedData();
        PsnJobVO srcVO = psndoc.getParentVO().getPsnJobVO();
        int oldRow = getBillListPanel().getBodyTable(curTabCode).getSelectedRow();
        getBillListPanel().setEnabled(true);
        getBillListPanel().getBodyScrollPane(curTabCode).insertLine();
        getBillListPanel().getBodyTable(curTabCode).setRowSelectionInterval(oldRow, oldRow);
        BillModel bm = getBillListPanel().getBodyBillModel(curTabCode);
        int selRow = getBillListPanel().getBodyTable(curTabCode).getSelectedRow();
        setFullTableEditable(bm, selRow);
        if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode))
        {
            PsnJobVO psnjob = createNewPsnjob(srcVO, 2);
            psnjob.setPoststat(UFBoolean.FALSE);
            psnjob.setEndflag(UFBoolean.TRUE);
            psnjob.setLastflag(UFBoolean.FALSE);
            psnjob.setRecordnum(getRecordnum(bm, selRow));
            setBodyRowDataByMetaData(bm, psnjob, selRow);
            bm.setCellEditable(selRow, PsnJobVO.CLERKCODE, ((RdsPsninfoModel) getModel()).isBillCodeEditable());
            bm.setCellEditable(selRow, PsnJobVO.POSTSTAT, false);
        }
        else if (TRNConst.Table_NAME_TRIAL.equals(curTabCode))
        {
            TrialVO trial = createNewTrial(srcVO);
            trial.setEndflag(UFBoolean.TRUE);
            trial.setLastflag(UFBoolean.FALSE);
            trial.setRecordnum(getRecordnum(bm, selRow));
            setBodyRowDataByMetaData(bm, trial, selRow);
            bm.setCellEditable(selRow, TrialVO.ENDFLAG, false);
        }
        else if (TRNConst.Table_NAME_RETIRE.equals(curTabCode))
        {
            RetireVO retire = createNewRetire(srcVO, curTabCode, oldRow + 1);
            retire.setLastflag(UFBoolean.FALSE);
            retire.setRecordnum(getRecordnum(bm, selRow));
            setBodyRowDataByMetaData(bm, retire, selRow);
        }
        else if (TRNConst.Table_NAME_PSNCHG.equals(curTabCode))
        {
            PsnChgVO psnchg = createNewPsnchg(srcVO);
            psnchg.setLastflag(UFBoolean.FALSE);
            psnchg.setRecordnum(getRecordnum(bm, selRow));
            setBodyRowDataByMetaData(bm, psnchg, selRow);
        }
    }
    
    public void setBodyActions(List<Action> bodyActions)
    {
        
        this.bodyActions = bodyActions;
    }
    
    public void setBodyDataByMetaData(CircularlyAccessibleValueObject[] data)
    {
        
        PsndocAggVO agg = new PsndocAggVO();
        agg.setTableVO(getMataTabCode(), data);
        getBillListPanel().getBillListData().setBodyValueObjectByMetaData(agg);
        getBillListPanel().getBodyBillModel(getCurrentTabCode()).execLoadFormula();
    }
    
    public void setBodyRowDataByMetaData(BillModel bm, SuperVO data, int row)
    {
        bm.setBodyRowObjectByMetaData(data, row);
        bm.execLoadFormula();
    }
    
    public void setFullTableEditable(BillModel bm, int selRow)
    {
        
        bm.setRowEditState(true);
        bm.setEditRow(selRow);
        BillItem[] bi = bm.getBodyItems();
        for (int i = 0; bi != null && i < bi.length; i++)
        {
            if (bi[i].isEdit())
            {
                bi[i].setEdit(true);
            }
        }
    }
    
    public void setMainPanelEnabled(boolean isEnabled)
    {
        
        // �༭̬��ѡ������
        getBillListPanel().getParentListPanel().getRowNOTable().setEnabled(!isEnabled);
        getBillListPanel().getHeadTable().setEnabled(!isEnabled);
        getBillListPanel().getBodyTabbedPane().setEnabled(!isEnabled);
    }
    
    private void setTrialCellEnable(BillModel bm, boolean isInJob, int selRow)
    {
        
        // �������ְ��Ա ���������޸����¼�¼,������
        // �������ְ��Ա,�����޸ĵ�����ʷ��¼,�������޸�"��������"��"�Ƿ����"
        TrialVO selVO = (TrialVO) bm.getBodyValueRowVO(selRow, TrialVO.class.getName());
        if (!selVO.getLastflag().booleanValue() || !isInJob)
        {
            bm.setCellEditable(selRow, TrialVO.TRIAL_TYPE, false);
            bm.setCellEditable(selRow, TrialVO.ENDFLAG, false);
        }
    }
    
    private void setValue(Object value, BillEditEvent e, String... itemKeys)
    {
        
        for (String key : itemKeys)
        {
            getBillListPanel().getBodyBillModel(e.getTableCode()).setValueAt(null, e.getRow(), key);
            if (value != null)
            {
                getBillListPanel().getBodyBillModel(e.getTableCode()).setValueAt(value, e.getRow(), key);
            }
        }
    }
    
    @Override
    public void stateChanged(ChangeEvent e)
    {
        
        getModel().fireEvent(new AppEvent("TabChanged", e.getSource(), null));
        getBillListPanelValueSetter().setBodyData(getBillListPanel(), getModel().getSelectedData());
    }
    
    /**
     * ��������༭״̬. ��������:(2001-5-29 14:47:20)
     */
    public void tableStopCellEditing(String tableCode)
    {
        
        if (getBillListPanel().getBodyTable(tableCode).isEditing())
        {
            if (getBillListPanel().getBodyTable(tableCode) != null)
            {
                getBillListPanel().getBodyTable(tableCode).getCellEditor().stopCellEditing();
            }
        }
    }
    
    /**
     * ��ǰѡ�������Ƿ���ʷ��֯����ְ��Ϣ
     */
    public boolean isHisInf()
    {
        PsnJobVO jobVO = (PsnJobVO) getBodySelectVO();
        if (jobVO == null || !TRNConst.Table_NAME_DEPTCHG.equals(getCurrentTabCode())) return false;
        // ѡ����ְ��HR��֯�����ڵ�ǰ����֯����Ϊ��ʷ��ְ��Ϣ
        if (!jobVO.getPk_hrorg().equals(getModel().getContext().getPk_org()))
        {
            return true;
        }
        else
        {
            // ѡ����ְ��HR��֯���ڵ�ǰ����֯�����м���������֯����ְ����Ϊ��ʷ��ְ��Ϣ HR1-HR2-HR1
            int row = getBillListPanel().getBodyTable(TRNConst.Table_NAME_DEPTCHG).getSelectedRow();
            int rowSize = getBillListPanel().getBodyTable(TRNConst.Table_NAME_DEPTCHG).getRowCount();
            //
            if (row == rowSize - 1) return false;
            String pk_hrorg = null;
            for (int i = 1; i < rowSize - row; i++)
            {
                pk_hrorg = (String) getBillListPanel().getBodyBillModel(TRNConst.Table_NAME_DEPTCHG).getValueAt(row + i, PsnJobVO.PK_HRORG);
                if (!getModel().getContext().getPk_org().equals(pk_hrorg)) return true;
            }
        }
        return false;
    }
}
