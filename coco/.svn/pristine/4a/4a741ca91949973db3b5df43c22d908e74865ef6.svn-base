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
 * 门店个人信息新增卡片（多个卡片）
 * 
 */
public class ShopPsnDetailView implements IController
{
    @SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
    
    public void beforeShow(DialogEvent dialogEvent)
    {
        ApplicationContext applicationContext = AppLifeCycleContext.current().getApplicationContext();
        // 区分新增/修改/复制/浏览
        String operateStatus = (String) applicationContext.getAppAttribute(HrssConsts.POPVIEW_OPERATE_STATUS);
        String dataset = (String) SessionUtil.getAttribute("DETAIL_CURR_DATASET");
        if (dataset == null)
        {
            CommonUtil.showMessageDialog(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_trn-res0012")/*
                                                                                                                             * @
                                                                                                                             * res
                                                                                                                             * "该卡片还没有定义，请先设置卡片内容！"
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
        // 可编辑的数据集
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
        // 查询待提交/待审核/审核不通过且用户未确认记录
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
            SuperVO[] vos = null;// 存放查询结果
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
            // 是否需审核
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
                                                                                                                             * "保存成功！"
                                                                                                                             */);
            // 关闭弹出页面
            CmdInvoker.invoke(new CloseWindowCmd());
            // 执行左侧快捷查询
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
     * 对学历信息和职业资格子集做回写（清空）人员信息中的相应字段处理
     * @param superVO
     */
    private void dealEduOrNationDuty(SuperVO superVO)
    {
        if (superVO instanceof EduVO)
        {
            // 学历信息
            UFBoolean lasteducation = (UFBoolean) (superVO.getAttributeValue("lasteducation"));
            
            PsndocVO psnVO = SessionUtil.getPsndocVO();
            if (lasteducation.booleanValue())
            {
                // 当修改的学历信息是最高学历时，需要把学历和学位回写到个人信息中
                psnVO.setEdu((String) superVO.getAttributeValue("education"));
                psnVO.setPk_degree((String) superVO.getAttributeValue("pk_degree"));
            }
            else if (superVO.getAttributeValue("education") != null 
                    && ((String) superVO.getAttributeValue("education")).equalsIgnoreCase(psnVO.getEdu()))
            {
                // flag为true，表示需要清空人员信息中的学历、学位
                psnVO.setEdu(null);
                psnVO.setPk_degree(null);
            }
            try
            {
                // 调用接口更新人员信息中的学历、学位字段
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
            // 职业资格
            UFBoolean istop = (UFBoolean) (superVO.getAttributeValue("istop"));
            if (istop.booleanValue())
            {
                PsndocVO psnVO = SessionUtil.getPsndocVO();
                if (istop.booleanValue())
                {
                    // 当修改的职业资格是最高时，需要把职业资格回写到个人信息中
                    psnVO.setProf((String) superVO.getAttributeValue("workname"));
                }
                else if (superVO.getAttributeValue("workname") != null 
                        && ((String) superVO.getAttributeValue("workname")).equalsIgnoreCase(psnVO.getProf()))
                {
                    // flag为true，表示需要清空人员信息中的职业资格
                    psnVO.setProf(null);
                }
                try
                {
                    // 调用接口更新人员信息中的职业资格字段
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
            // 职称信息
            UFBoolean tiptop_flag = (UFBoolean) (superVO.getAttributeValue("tiptop_flag"));
            if (tiptop_flag.booleanValue())
            {
                PsndocVO psnVO = SessionUtil.getPsndocVO();
                if (tiptop_flag.booleanValue())
                {
                    // 当修改的职称信息是最高时，需要把职称信息回写到个人信息中的专业技术职务
                    psnVO.setTitletechpost((String) superVO.getAttributeValue("pk_techposttitle"));
                }else if (superVO.getAttributeValue("pk_techposttitle") != null 
                        && ((String) superVO.getAttributeValue("pk_techposttitle")).equalsIgnoreCase(psnVO.getTitletechpost()))
                {
                    // flag为true，表示需要清空人员信息中的专业技术职务
                    psnVO.setTitletechpost(null);
                }
                try
                {
                    // 调用接口更新人员信息中的职业资格字段
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
     * 新增时
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
        // 查询未提交的修改记录
        ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
        HrssSetalterVO vo = service.queryNoSubmitHrssSetalterVO(BeOperatedPk_psndoc, pk_infoset);
        
        AlterationVO alterVO = null;
        if (vo != null)
        {
            alterVO = AlterationParse.parseXML(vo.getAlter_context());
        }
        SuperVO[] allSuperVOs = ShopPsnInfoUtil.querySubSet(dataset);
        SuperVO[] newSuperVOs = (SuperVO[]) ArrayUtils.add(allSuperVOs, superVO);
        // 判断是否有多个最高学历,多个最高职业资格,多个职称信息
        validateTopJustOne(newSuperVOs);
        AlterationVO afterVO = PsninfoUtil.updSuperVOsToXML(newSuperVOs, alterVO);
        String xml = AlterationParse.generateXML(afterVO);
        if (vo == null)
        { // 不存在未提交记录时新增
            vo = getNewHrssSetalterVO(BeOperatedPk_psndoc, pk_infoset, xml);
            service.insertVO(vo);
        }
        else
        { // 存在未提交记录时修改
            vo.setAlter_date(new UFDate());
            vo.setAlter_context(xml);
            service.updateVO(vo);
        }
    }
    
    /**
     * 修改时
     * 
     * @param superVO
     * @param dataset
     * @throws HrssException
     * @throws Exception
     */
    private void updAuditInfo(String dataset, String tableName, SuperVO superVO) throws HrssException, Exception
    {
        ApplicationContext applicationContext = AppLifeCycleContext.current().getApplicationContext();
        // 区分新增/修改/复制/浏览
        String rowIndex = (String) applicationContext.getAppAttribute("rowIndex");
        int row = Integer.parseInt(rowIndex);
        String BeOperatedPk_psndoc = hrsmsUtil.getBoOperatePsndocPK();
        ISetalterService service = ServiceLocator.lookup(ISetalterService.class);
        String pk_infoset = PsninfoUtil.getInfosetPKByCode(tableName);
        // 查询未提交的修改记录
        HrssSetalterVO vo = service.queryNoSubmitHrssSetalterVO(BeOperatedPk_psndoc, pk_infoset);
        AlterationVO alterVO = null;
        if (vo != null)
        {
            alterVO = AlterationParse.parseXML(vo.getAlter_context());
        }
        SuperVO[] allSuperVOs = ShopPsnInfoUtil.querySubSet(dataset);
        allSuperVOs[row] = superVO;
        // 判断是否有多个最高学历,多个最高职业资格,多个职称信息
        validateTopJustOne(allSuperVOs);
        AlterationVO afterVO = PsninfoUtil.updSuperVOsToXML(allSuperVOs, alterVO);
        String xml = AlterationParse.generateXML(afterVO);
        if (vo == null)
        { // 不存在未提交记录时新增
            vo = getNewHrssSetalterVO(BeOperatedPk_psndoc, pk_infoset, xml);
            service.insertVO(vo);
        }
        else
        { // 存在未提交记录时修改
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
     * 判断是否有多个最高学历和多个最高职业资格
     * @author zhangqiano
     * @param superVOs
     * @serialData 2015-10-29
     */
    private void validateTopJustOne(SuperVO[] superVOs)
    {
        // 对学历信息或职业资格或职称信息子集做回写（清空）人员信息中的相应字段处理
        // dealEduOrNationDuty(superVOs[0], flag);
        if (ArrayUtils.isEmpty(superVOs))
        {
            return;
        }
        // 最高学历计数
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
                                                                                                                              * "保存失败"
                                                                                                                              */,
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0001")/*
                                                                                                       * @res
                                                                                                       * "不可以设置多个最高学历！"
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
                                                                                                                              * "保存失败"
                                                                                                                              */,
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0016")/*
                                                                                                       * @res
                                                                                                       * "不可以设置多个最高职业资格！"
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
                                                                                                                              * "保存失败"
                                                                                                                              */,
                    nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res", "0c_hi-res0025")/*
                                                                                                       * @res
                                                                                                       * "不可以设置多个最高职称信息！"
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
