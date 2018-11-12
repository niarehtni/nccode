package nc.bs.hrsms.hi.psninfo.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.hrsms.hi.ShopPsnInfoUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.hi.psninfo.AlterationParse;
import nc.bs.hrss.hi.psninfo.PsninfoConsts;
import nc.bs.hrss.hi.psninfo.PsninfoUtil;
import nc.bs.hrsms.hi.hrsmsUtil;
import nc.bs.hrsms.hi.psninfo.cmd.ShopSubSetLineAddCmd;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.Logger;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.itf.hi.IPersonRecordService;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hrss.hi.setalter.ISetalterService;
import nc.itf.uap.IVOPersistence;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DialogEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.exception.LfwInteractionException;
import nc.uap.lfw.core.exception.LfwValidateException;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.ViewMenus;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.uap.lfw.core.uif.delegator.DefaultDataValidator;
import nc.uap.lfw.core.uif.delegator.IDataValidator;
import nc.vo.hi.psndoc.EduVO;
import nc.vo.hi.psndoc.NationDutyVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.TitleVO;
import nc.vo.hrss.hi.psninfo.AlterationVO;
import nc.vo.hrss.hi.setalter.HrssSetalterVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.om.pub.SuperVOHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.bs.hrsms.hi.ShopPsnInfoUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import uap.web.bd.pub.AppUtil;

/**
 * �ŵ������Ϣ������Ƭ�������Ƭ��
 * 
 */
public class ShopPsnDetailView implements IController
{
    @SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
    
    public void beforeShow(DialogEvent dialogEvent)
    {
        ApplicationContext applicationContext = AppLifeCycleContext.current().getApplicationContext();
        // ��������/�޸�/����/���
        String operateStatus = (String) applicationContext.getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
        String dataset = (String) SessionUtil.getAttribute("DETAIL_CURR_DATASET");
        if (dataset == null)
        {
            CommonUtil.showMessageDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_trn-res0012")/*
                                                                                                                             * @
                                                                                                                             * res
                                                                                                                             * "�ÿ�Ƭ��û�ж��壬�������ÿ�Ƭ���ݣ�"
                                                                                                                             */);
            AppLifeCycleContext.current().getApplicationContext().closeWinDialog();
            return;
        }
        SuperVO superVO = (SuperVO) applicationContext.getAppAttribute(PsninfoConsts.SUPERVO_DETAIL);
        LfwView view = AppLifeCycleContext.current().getViewContext().getView();
        try
        {
            Dataset ds = view.getViewModels().getDataset(dataset);
            if (operateStatus.equals(HrssConsts.POPVIEW_OPERATE_ADD))
            {
                setMenuItemVisible(true);
                String BeOperatedPk_psndoc = hrsmsUtil.getBoOperatePsndocPK();
                ShopSubSetLineAddCmd cmd = new ShopSubSetLineAddCmd(dataset,BeOperatedPk_psndoc);
                cmd.execute();
                
            }
            else if (operateStatus.equals(HrssConsts.POPVIEW_OPERATE_VIEW))
            {
                SuperVO[] superVOs = new SuperVO[]{superVO};
                new SuperVO2DatasetSerializer().serialize(superVOs == null ? new SuperVO[]{} : superVOs, ds, Row.STATE_NORMAL);
                ds.setRowSelectIndex(0);
                String tableName = superVOs[0].getTableName();
                boolean isEnabled = isEnabled(tableName);
                ds.setEnabled(isEnabled);
                setMenuItemVisible(isEnabled);
                String formId = (String) SessionUtil.getAttribute("formId");
                WebComponent component = view.getViewComponents().getComponent(formId);
                ShopPsnInfoUtil.setPsnclrule(tableName, component);
            }
        }
        catch (BusinessException e)
        {
            new HrssException(e).alert();
        }
        catch (HrssException e)
        {
            new HrssException(e).alert();
        }
    }
    
    private boolean isEnabled(String infoset) throws BusinessException, HrssException
    {
        if (PsninfoUtil.isBusinessSet(infoset)) return false;
        // �ɱ༭�����ݼ�
        @SuppressWarnings("unchecked")
        ArrayList<String> editDatasetlist = (ArrayList<String>) AppUtil.getAppAttr("editDatasetlist");
        if (!(!CollectionUtils.isEmpty(editDatasetlist) && editDatasetlist.contains(infoset)))
        {
            return false;
        }
        boolean isNeedAudit = ShopPsnInfoUtil.isNeedAudit(infoset);
        if (!isNeedAudit) return true;
        String BeOperatedPk_psndoc = hrsmsUtil.getBoOperatePsndocPK();
        String pk_infoset = PsninfoUtil.getInfosetPKByCode(infoset);
        ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
        Integer data_status = -1;
        // ��ѯ���ύ/�����/��˲�ͨ�����û�δȷ�ϼ�¼
        HrssSetalterVO alterVO = service.queryNoSubOrAudOrConfirmHrssSetalterVO(BeOperatedPk_psndoc, pk_infoset);
        if (alterVO != null)
        {
            data_status = alterVO.getData_status();
        }
        if (PsninfoUtil.isEdit(data_status))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static void setMenuItemVisible(boolean isEnabled) throws BusinessException, HrssException
    {
        LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
        ViewMenus menuBars = widget.getViewMenus();
        MenubarComp bar = menuBars.getMenuBar("savemenu");
        List<MenuItem> items = bar.getMenuList();
        for (Iterator<MenuItem> iter = items.iterator(); iter.hasNext();)
        {
            MenuItem item = iter.next();
            item.setVisible(isEnabled);
        }
    }
    
    public static SuperVO[] querySubSet(Class<?> proClass, String primaryKey)
    {
        try
        {
            SuperVO[] vos = null;// ��Ų�ѯ���
            SuperVO supVO = (SuperVO) proClass.newInstance();
            String pkName = supVO.getPKFieldName();
            Object[] objvos =
                ServiceLocator.lookup(IPsndocQryService.class).querySubVO(proClass, " " + pkName + " = '" + primaryKey + "' ", null);
            if (!ArrayUtils.isEmpty(objvos))
            {
                vos = SuperVOHelper.convertObjectArray(objvos);
            }
            return vos;
        }
        catch (HrssException e1)
        {
            Logger.error(e1.getMessage(), e1);
        }
        catch (BusinessException e2)
        {
            Logger.error(e2.getMessage(), e2);
        }
        catch (Exception e)
        {
            Logger.error(e.getMessage(), e);
        }
        return null;
    }
    
    public void save(MouseEvent<MenuItem> mouseEvent)
    {
        String dataset = (String) SessionUtil.getAttribute("DETAIL_CURR_DATASET");
        ApplicationContext applicationContext = AppLifeCycleContext.current().getApplicationContext();
        Dataset masterDs = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset(dataset);
        doValidate(masterDs);
        Dataset2SuperVOSerializer<SuperVO> ser = new Dataset2SuperVOSerializer<SuperVO>();
        SuperVO[] superVOs = ser.serialize(masterDs);
        SuperVO superVO = superVOs[0];
        String tableName = superVO.getTableName();
        
        try
        {
            // �Ƿ������
            boolean isNeedAudit = ShopPsnInfoUtil.isNeedAudit(tableName);
            if (!isNeedAudit)
            {
                SuperVO[] vos = ShopPsnInfoUtil.querySubSet(dataset);
                
                if (VOStatus.NEW == superVOs[0].getStatus())
                {
                    SuperVO[] newSuperVOs = (SuperVO[]) ArrayUtils.add(vos, superVO);
                    validateTopJustOne(newSuperVOs);
                    superVOs[0].setAttributeValue(PsnJobVO.LASTFLAG, "Y");
                    superVOs[0].setAttributeValue(PsnJobVO.RECORDNUM, 0);
                    superVOs[0].setAttributeValue("creator", SessionUtil.getPk_user());
                    superVOs[0].setAttributeValue("creationtime", new UFDateTime());
                    ServiceLocator.lookup(IVOPersistence.class).insertVO(superVOs[0]);
                    String BeOperatedPk_psndoc = hrsmsUtil.getBoOperatePsndocPK();
                    
                    String pk_infoset = PsninfoUtil.getInfosetPKByCode(tableName);
                    Map<String, String[]> map = new HashMap<String, String[]>();
                    map.put(BeOperatedPk_psndoc, new String[]{pk_infoset});
                    NCLocator.getInstance().lookup(IPersonRecordService.class).updateRecordnumAndLastflagForHrss(map);
                }
                else
                {
                    String rowIndex = (String) applicationContext.getAppAttribute("rowIndex");
                    int row = Integer.parseInt(rowIndex);
                    vos[row] = superVO;
                    validateTopJustOne(vos);
                    superVOs[0].setAttributeValue("modifier", SessionUtil.getPk_user());
                    superVOs[0].setAttributeValue("modifiedtime", new UFDateTime());
                    ServiceLocator.lookup(IVOPersistence.class).updateVO(superVOs[0]);
                }
                dealEduOrNationDuty(superVO);
            }
            else
            {
                if (VOStatus.NEW == superVOs[0].getStatus())
                {
                    addAuditInfo(superVO, dataset, tableName);
                }
                else
                {
                    updAuditInfo(dataset, tableName, superVO);
                }
            }
            LfwRuntimeEnvironment.getWebContext().getPageMeta().setHasChanged(false);
            CommonUtil.showShortMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res", "0c_pub-res0115")/*
                                                                                                                             * @
                                                                                                                             * res
                                                                                                                             * "����ɹ���"
                                                                                                                             */);
            // �رյ���ҳ��
            CmdInvoker.invoke(new CloseWindowCmd());
            // ִ������ݲ�ѯ
            CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "closewindow"));
        }
        catch (BusinessException e)
        {
            new HrssException(e).alert();
        }
        catch (HrssException e)
        {
            new HrssException(e).alert();
        }
        catch (LfwInteractionException e)
        {
            new HrssException(e.getInfo().getMsg()).alert();
        }
        catch (Exception e)
        {
            new HrssException(e).alert();
        }
    }
    
    /**
     * ��ѧ����Ϣ��ְҵ�ʸ��Ӽ�����д����գ���Ա��Ϣ�е���Ӧ�ֶδ���
     * @param superVO
     */
    private void dealEduOrNationDuty(SuperVO superVO)
    {
        if (superVO instanceof EduVO)
        {
            // ѧ����Ϣ
            UFBoolean lasteducation = (UFBoolean) (superVO.getAttributeValue("lasteducation"));
            
            PsndocVO psnVO = SessionUtil.getPsndocVO();
            if (lasteducation.booleanValue())
            {
                // ���޸ĵ�ѧ����Ϣ�����ѧ��ʱ����Ҫ��ѧ����ѧλ��д��������Ϣ��
                psnVO.setEdu((String) superVO.getAttributeValue("education"));
                psnVO.setPk_degree((String) superVO.getAttributeValue("pk_degree"));
            }
            else if (superVO.getAttributeValue("education") != null 
                    && ((String) superVO.getAttributeValue("education")).equalsIgnoreCase(psnVO.getEdu()))
            {
                // flagΪtrue����ʾ��Ҫ�����Ա��Ϣ�е�ѧ����ѧλ
                psnVO.setEdu(null);
                psnVO.setPk_degree(null);
            }
            try
            {
                // ���ýӿڸ�����Ա��Ϣ�е�ѧ����ѧλ�ֶ�
                ServiceLocator.lookup(IVOPersistence.class).updateVO(psnVO);
            }
            catch (BusinessException e)
            {
                new HrssException(e).deal();
            }
            catch (HrssException e)
            {
                e.alert();
            }
            
            
        }
        if (superVO instanceof NationDutyVO)
        {
            // ְҵ�ʸ�
            UFBoolean istop = (UFBoolean) (superVO.getAttributeValue("istop"));
            if (istop.booleanValue())
            {
                PsndocVO psnVO = SessionUtil.getPsndocVO();
                if (istop.booleanValue())
                {
                    // ���޸ĵ�ְҵ�ʸ������ʱ����Ҫ��ְҵ�ʸ��д��������Ϣ��
                    psnVO.setProf((String) superVO.getAttributeValue("workname"));
                }
                else if (superVO.getAttributeValue("workname") != null 
                        && ((String) superVO.getAttributeValue("workname")).equalsIgnoreCase(psnVO.getProf()))
                {
                    // flagΪtrue����ʾ��Ҫ�����Ա��Ϣ�е�ְҵ�ʸ�
                    psnVO.setProf(null);
                }
                try
                {
                    // ���ýӿڸ�����Ա��Ϣ�е�ְҵ�ʸ��ֶ�
                    ServiceLocator.lookup(IVOPersistence.class).updateVO(psnVO);
                }
                catch (BusinessException e)
                {
                    new HrssException(e).deal();
                }
                catch (HrssException e)
                {
                    e.alert();
                }
            }
        }
        
        if (superVO instanceof TitleVO)
        {
            // ְ����Ϣ
            UFBoolean tiptop_flag = (UFBoolean) (superVO.getAttributeValue("tiptop_flag"));
            if (tiptop_flag.booleanValue())
            {
                PsndocVO psnVO = SessionUtil.getPsndocVO();
                if (tiptop_flag.booleanValue())
                {
                    // ���޸ĵ�ְ����Ϣ�����ʱ����Ҫ��ְ����Ϣ��д��������Ϣ�е�רҵ����ְ��
                    psnVO.setTitletechpost((String) superVO.getAttributeValue("pk_techposttitle"));
                }else if (superVO.getAttributeValue("pk_techposttitle") != null 
                        && ((String) superVO.getAttributeValue("pk_techposttitle")).equalsIgnoreCase(psnVO.getTitletechpost()))
                {
                    // flagΪtrue����ʾ��Ҫ�����Ա��Ϣ�е�רҵ����ְ��
                    psnVO.setTitletechpost(null);
                }
                try
                {
                    // ���ýӿڸ�����Ա��Ϣ�е�ְҵ�ʸ��ֶ�
                    ServiceLocator.lookup(IVOPersistence.class).updateVO(psnVO);
                }
                catch (BusinessException e)
                {
                    new HrssException(e).deal();
                }
                catch (HrssException e)
                {
                    e.alert();
                }
            }
        }
    }
    
    public void cancel(MouseEvent<MenuItem> mouseEvent)
    {
        LfwRuntimeEnvironment.getWebContext().getPageMeta().setHasChanged(false);
        CmdInvoker.invoke(new CloseWindowCmd());
    }
    
    /**
     * ����ʱ
     * 
     * @param superVO
     * @param dataset
     * @throws HrssException
     * @throws Exception
     */
    private void addAuditInfo(SuperVO superVO, String dataset, String tableName) throws HrssException, Exception
    {
        superVO.setAttributeValue("assgid", 1);
        String BeOperatedPk_psndoc = hrsmsUtil.getBoOperatePsndocPK();
        String pk_infoset = PsninfoUtil.getInfosetPKByCode(tableName);
        // ��ѯδ�ύ���޸ļ�¼
        ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
        HrssSetalterVO vo = service.queryNoSubmitHrssSetalterVO(BeOperatedPk_psndoc, pk_infoset);
        
        AlterationVO alterVO = null;
        if (vo != null)
        {
            alterVO = AlterationParse.parseXML(vo.getAlter_context());
        }
        SuperVO[] allSuperVOs = ShopPsnInfoUtil.querySubSet(dataset);
        SuperVO[] newSuperVOs = (SuperVO[]) ArrayUtils.add(allSuperVOs, superVO);
        // �ж��Ƿ��ж�����ѧ��,������ְҵ�ʸ�,���ְ����Ϣ
        validateTopJustOne(newSuperVOs);
        AlterationVO afterVO = PsninfoUtil.updSuperVOsToXML(newSuperVOs, alterVO);
        String xml = AlterationParse.generateXML(afterVO);
        if (vo == null)
        { // ������δ�ύ��¼ʱ����
            vo = getNewHrssSetalterVO(BeOperatedPk_psndoc, pk_infoset, xml);
            service.insertVO(vo);
        }
        else
        { // ����δ�ύ��¼ʱ�޸�
            vo.setAlter_date(new UFDate());
            vo.setAlter_context(xml);
            service.updateVO(vo);
        }
    }
    
    /**
     * �޸�ʱ
     * 
     * @param superVO
     * @param dataset
     * @throws HrssException
     * @throws Exception
     */
    private void updAuditInfo(String dataset, String tableName, SuperVO superVO) throws HrssException, Exception
    {
        ApplicationContext applicationContext = AppLifeCycleContext.current().getApplicationContext();
        // ��������/�޸�/����/���
        String rowIndex = (String) applicationContext.getAppAttribute("rowIndex");
        int row = Integer.parseInt(rowIndex);
        String BeOperatedPk_psndoc = hrsmsUtil.getBoOperatePsndocPK();
        ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
        String pk_infoset = PsninfoUtil.getInfosetPKByCode(tableName);
        // ��ѯδ�ύ���޸ļ�¼
        HrssSetalterVO vo = service.queryNoSubmitHrssSetalterVO(BeOperatedPk_psndoc, pk_infoset);
        AlterationVO alterVO = null;
        if (vo != null)
        {
            alterVO = AlterationParse.parseXML(vo.getAlter_context());
        }
        SuperVO[] allSuperVOs = ShopPsnInfoUtil.querySubSet(dataset);
        allSuperVOs[row] = superVO;
        // �ж��Ƿ��ж�����ѧ��,������ְҵ�ʸ�,���ְ����Ϣ
        validateTopJustOne(allSuperVOs);
        AlterationVO afterVO = PsninfoUtil.updSuperVOsToXML(allSuperVOs, alterVO);
        String xml = AlterationParse.generateXML(afterVO);
        if (vo == null)
        { // ������δ�ύ��¼ʱ����
            vo = getNewHrssSetalterVO(BeOperatedPk_psndoc, pk_infoset, xml);
            service.insertVO(vo);
        }
        else
        { // ����δ�ύ��¼ʱ�޸�
            vo.setAlter_date(new UFDate());
            vo.setAlter_context(xml);
            service.updateVO(vo);
        }
    }
    
    private HrssSetalterVO getNewHrssSetalterVO(String pk_psndoc, String pk_infoset, String xml)
    {
        SessionBean session = SessionUtil.getSessionBean();
        HrssSetalterVO vo = new HrssSetalterVO();
        vo.setPk_psndoc(pk_psndoc);
        vo.setPk_infoset(pk_infoset);
        vo.setData_status(PsninfoConsts.STATUS_NOSUMIT);
        vo.setAlter_context(xml);
        vo.setPk_operator(session.getUserVO().getCuserid());
        vo.setAlter_date(new UFDate());
        vo.setPk_group(SessionUtil.getPk_group());
        vo.setPk_org(session.getPsnjobVO().getPk_hrorg());
        vo.setPk_dept(session.getPk_dept());
        vo.setConfirm_flag(UFBoolean.FALSE);
        vo.setPk_psnjob(session.getPsnjobVO().getPk_psnjob());
        return vo;
    }
    
    /**
     * �ж��Ƿ��ж�����ѧ���Ͷ�����ְҵ�ʸ�
     * @author zhangqiano
     * @param superVOs
     * @serialData 2015-10-29
     */
    private void validateTopJustOne(SuperVO[] superVOs)
    {
        // ��ѧ����Ϣ��ְҵ�ʸ��ְ����Ϣ�Ӽ�����д����գ���Ա��Ϣ�е���Ӧ�ֶδ���
        // dealEduOrNationDuty(superVOs[0], flag);
        if (ArrayUtils.isEmpty(superVOs))
        {
            return;
        }
        // ���ѧ������
        int count = 0;
        
        if (superVOs[0] instanceof EduVO)
        {
            
            for (SuperVO svo : superVOs)
            {
                EduVO vo = (EduVO) svo;
                if (vo.getLasteducation() != null && vo.getLasteducation().booleanValue())
                {
                    count++;
                }
            }
            if (count > 1)
            {
                CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0018")/*
                                                                                                                              * @
                                                                                                                              * res
                                                                                                                              * "����ʧ��"
                                                                                                                              */,
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0001")/*
                                                                                                       * @res
                                                                                                       * "���������ö�����ѧ����"
                                                                                                       */);
            }
        }
        
        if (superVOs[0] instanceof NationDutyVO)
        {
            
            for (SuperVO svo : superVOs)
            {
                NationDutyVO vo = (NationDutyVO) svo;
                if (vo.getIstop() != null && vo.getIstop().booleanValue())
                {
                    count++;
                }
            }
            
            if (count > 1)
            {
                CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0018")/*
                                                                                                                              * @
                                                                                                                              * res
                                                                                                                              * "����ʧ��"
                                                                                                                              */,
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0016")/*
                                                                                                       * @res
                                                                                                       * "���������ö�����ְҵ�ʸ�"
                                                                                                       */);
            }
        }
        
        if (superVOs[0] instanceof TitleVO)
        {
            
            for (SuperVO svo : superVOs)
            {
                TitleVO vo = (TitleVO) svo;
                if (vo.getTiptop_flag() != null && vo.getTiptop_flag().booleanValue())
                {
                    count++;
                }
            }
            
            if (count > 1)
            {
                
                CommonUtil.showErrorDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0018")/*
                                                                                                                              * @
                                                                                                                              * res
                                                                                                                              * "����ʧ��"
                                                                                                                              */,
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0025")/*
                                                                                                       * @res
                                                                                                       * "���������ö�����ְ����Ϣ��"
                                                                                                       */);
            }
        }
        
    }
    
    private void doValidate(Dataset masterDs) throws LfwValidateException
    {
        IDataValidator validator = new DefaultDataValidator();
        validator.validate(masterDs, new LfwView());
    }
    
}
