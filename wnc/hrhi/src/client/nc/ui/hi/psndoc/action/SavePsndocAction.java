package nc.ui.hi.psndoc.action;

import java.awt.event.ActionEvent;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.ChangeEvent;

import nc.bs.bd.psn.validator.PsnIdtypeQuery;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hr.IHRLicenseChecker;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hrp.psnbudget.IOrgBudgetQueryService;
import nc.pub.tools.HRCMCommonValue;
import nc.pub.tools.VOUtils;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hi.psndoc.model.PsndocDataManager;
import nc.ui.hi.psndoc.model.PsndocModel;
import nc.ui.hi.psndoc.view.CourtFineValidator;
import nc.ui.hi.psndoc.view.PsndocFormEditor;
import nc.ui.hi.psndoc.view.PsndocListView;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.uif2.action.SaveAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.bd.psnid.PsnIdtypeVO;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.WorkVO;
import nc.vo.hi.psndoc.enumeration.PsnType;
import nc.vo.hi.pub.BillCodeRepeatBusinessException;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.validator.ValidateWithLevelException;
import nc.vo.hrp.psnorgbudget.ValidateResultVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/***************************************************************************
 * <br>
 * Created on 2010-2-23 9:21:24<br>
 * @author dusx, Rocex Wang
 ***************************************************************************/
public class SavePsndocAction extends SaveAction
{
    private PsndocDataManager dataManager;
    private PsndocListView listView;
    private SuperFormEditorValidatorUtil superValidator; // 脨拢脩茅脝梅
    private CourtFineValidator courtFineValidator;
    
    /***************************************************************************
     * <br>
     * Created on 2010-7-9 9:33:13<br>
     * @param psndocAggVO
     * @author Rocex Wang
     ***************************************************************************/
    protected void adjustOrgJobVO(PsndocAggVO psndocAggVO)
    {
        psndocAggVO.setTableVO(PsnOrgVO.getDefaultTableName(),
            mergeOrgJobVO(psndocAggVO.getTableVO(PsnOrgVO.getDefaultTableName()), psndocAggVO.getParentVO().getPsnOrgVO()));
        psndocAggVO.setTableVO(PsnJobVO.getDefaultTableName(),
            mergeOrgJobVO(psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName()), psndocAggVO.getParentVO().getPsnJobVO()));
        
        // 陆芦脰梅卤铆脡脧碌脛鹿陇脳梅脨脜脧垄路脜碌陆脳卯脨脗鹿陇脳梅录脟脗录脡脧拢卢脪脭鹿忙卤脺路脟驴脮脨拢脩茅
        // PsndocFormEditor editor = (PsndocFormEditor) getEditor();
        // if (editor.getBillCardPanel().getBillTable(PsnJobVO.getDefaultTableName()).getRowCount() > 0)
        // {
        // editor.getBillCardPanel()
        // .getBillModel(PsnJobVO.getDefaultTableName())
        // .setBodyRowObjectByMetaData(psndocAggVO.getParentVO().getPsnJobVO(),
        // editor.getBillCardPanel().getBillTable(PsnJobVO.getDefaultTableName()).getRowCount() - 1);
        // }
        
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-2-23 9:21:37<br>
     * @see nc.ui.uif2.actions.SaveAction#doAction(java.awt.event.ActionEvent)
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public void doAction(ActionEvent e) throws Exception
    {
        if (getModel().isSortEdit())
        {
            saveSort();
            getDataManager().getModel().setSortEdit(false);
            getModel().fireEvent(new AppEvent(HICommonValue.EVENT_ADJUSTSORT, this, false));
            getModel().setUiState(UIState.NOT_EDIT);
            // 掳麓脮脮脧脰脭脷碌脛路脰脪鲁禄煤脰脝脦脼路篓脣垄脨脗脢媒戮脻,脣霉脪脭虏禄脣垄脨脗脕脣,艙p脡脵脽B陆脫鈥澛�
            getDataManager().refresh();
        }
        else
        {
            if (!savePsndocVO())
            {
                putValue(HrAction.MESSAGE_AFTER_ACTION, " ");
                return;
            }
            
            ((PsndocFormEditor) getEditor()).getBillCardPanel().getBillData().clearShowWarning();
        }
        putValue(MESSAGE_AFTER_ACTION, IShowMsgConstant.getSaveSuccessInfo());
        // ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getSaveSuccessInfo(), getContext());
    }
    
    /***************************************************************************
     * 脤卯鲁盲脪禄脨漏脛卢脠脧脰碌<br>
     * Created on 2010-6-22 10:02:22<br>
     * @param psndocAggVO2
     * @author Rocex Wang
     ***************************************************************************/
    protected void fillDefaultValue(PsndocAggVO psndocAggVO2)
    {
        if (psndocAggVO2 == null)
        {
            return;
        }
        PartTimeVO partTimeVOs[] = (PartTimeVO[]) psndocAggVO2.getTableVO(PartTimeVO.getDefaultTableName());
        if (partTimeVOs != null)
        {
            for (PartTimeVO partTimeVO : partTimeVOs)
            {
                partTimeVO.setPsntype(getModel().getPsnType());
            }
        }
        /**
         * @author yangzxa
         * @time 2015脛锚12脭脗28脠脮16:28:56
         * 露脭潞脧脥卢录脟脗录碌脛continuetime陆酶脨脨脡猫脰脙
         */
        CtrtVO[] ctrtVOs = (CtrtVO[]) psndocAggVO2.getTableVO("hi_psndoc_ctrt");
        if(ctrtVOs!=null&&ctrtVOs.length>0){
        	int continuetime=1;
            Integer conttype;
        	for (CtrtVO ctrtVO : ctrtVOs) {
        		//麓脣脤玫潞脧脥卢录脟脗录碌脛潞脧脥卢脌脿脨脥
            	conttype= ctrtVO.getConttype();
            	/*continuetime碌脛鹿忙脭貌脢脟拢卢
            	 * 拢篓1拢漏脟漏露漏脢卤脦陋1拢禄
            	 * 拢篓2拢漏卤盲赂眉脦陋脡脧脪禄脤玫碌脛脰碌拢卢
            	 * 拢篓3拢漏脰脮脰鹿隆垄陆芒鲁媒脡猫脰脙脦陋0隆拢
            	 * 拢篓4拢漏脨酶脟漏脭貌脦陋脡脧脪禄脤玫+1拢卢
            	 **/
            	if ( conttype == HRCMCommonValue.CONTTYPE_MAKE) 
    			{
            		//脟漏露漏
    				continuetime=1;
    			}else if(conttype == HRCMCommonValue.CONTTYPE_RELEASE||conttype == HRCMCommonValue.CONTTYPE_FINISH)
    			{
    				//脰脮脰鹿陆芒鲁媒
    				continuetime=0;
    			}else if(conttype == HRCMCommonValue.CONTTYPE_EXTEND)
    			{
    				//脨酶脟漏
    				continuetime++;
    			}
            	ctrtVO.setContinuetime(continuetime);
			}
        	
        }
        for (SuperVO superVO : psndocAggVO2.getAllChildrenVO())
        {
            if (superVO.getAttributeValue(PsndocVO.PK_GROUP) == null)
            {
                superVO.setAttributeValue(PsndocVO.PK_GROUP, PubEnv.getPk_group());
            }
            if (superVO.getAttributeValue(PsndocVO.PK_ORG) == null)
            {
                superVO.setAttributeValue(PsndocVO.PK_ORG, getContext().getPk_org());
            }
            
            // 路碌脝赂脭脵脝赂脡猫脰脙pk_psndoc
            if (superVO.getAttributeValue(PsnOrgVO.PK_PSNDOC) == null)
            {
                superVO.setAttributeValue(PsnOrgVO.PK_PSNDOC, psndocAggVO2.getParentVO().getPk_psndoc());
            }
            
            if (!(superVO instanceof PsnOrgVO) && superVO.getAttributeValue(PsnOrgVO.PK_PSNORG) == null)
            {
                superVO.setAttributeValue(PsnOrgVO.PK_PSNORG, psndocAggVO2.getParentVO().getPsnOrgVO().getPrimaryKey());
            }
            if (!(superVO instanceof PsnJobVO) && !(superVO instanceof WorkVO) && superVO.getAttributeValue(PsnJobVO.PK_PSNJOB) == null)
            {
                superVO.setAttributeValue(PsnJobVO.PK_PSNJOB, psndocAggVO2.getParentVO().getPsnJobVO().getPrimaryKey());
            }
            if (superVO.getAttributeValue(PsnJobVO.ASSGID) == null)
            {
                superVO.setAttributeValue(PsnJobVO.ASSGID, psndocAggVO2.getParentVO().getPsnJobVO().getAssgid());
            }
        }
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-30 15:07:10<br>
     * @return PsndocDataManager
     * @author Rocex Wang
     ***************************************************************************/
    public PsndocDataManager getDataManager()
    {
        return dataManager;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-30 15:10:25<br>
     * @return PsndocListView
     * @author Rocex Wang
     ***************************************************************************/
    public PsndocListView getListView()
    {
        return listView;
    }
    
    /****************************************************************************
     * {@inheritDoc}<br>
     * Created on 2010-6-1 10:51:39<br>
     * @see nc.ui.hr.uif2.action.SaveAction#getModel()
     * @author Rocex Wang
     ****************************************************************************/
    @Override
    public PsndocModel getModel()
    {
        return (PsndocModel) super.getModel();
    }
    
    private <T extends SuperVO> T[] mergeOrgJobVO(T[] tableVO, T orgJobVO)
    {
        if (tableVO == null || tableVO.length == 0)
        {
            T[] arr = (T[]) Array.newInstance(orgJobVO.getClass(), 1);
            arr[0] = orgJobVO;
            return arr;
        }
        if (getModel().getUiState() == UIState.EDIT)
        {
            // 脨脼赂脛脢卤
            for (int i = 0; i < tableVO.length; i++)
            {
                if (VOStatus.DELETED == tableVO[i].getStatus())
                {
                    continue;
                }
                if (orgJobVO.getPrimaryKey().equals(tableVO[i].getPrimaryKey()))
                {
                    tableVO[i] = orgJobVO;
                    break;
                }
            }
        }
        else if (getModel().getUiState() == UIState.ADD)
        {
            // 脨脗脭枚脢卤
            return (T[]) ArrayUtils.add(tableVO, orgJobVO);
        }
        return tableVO;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-2-23 9:21:44<br>
     * @throws ValidateWithLevelException
     * @throws Exception
     * @author Rocex Wang
     ***************************************************************************/
    @SuppressWarnings("restriction")
    protected boolean savePsndocVO() throws ValidateWithLevelException, Exception
    {
        getListView().stopCellEditing();
        Object value = getEditor().getValue();
        
        PsndocAggVO psndocAggVO = (PsndocAggVO) value;
        
        // fix bug NCdp205505468 脠脣脭卤脦卢禄陇脮脮脝卢拢卢脭脷脡脧麓芦脢卤脫娄赂脙驴脴脰脝虏禄脛脺麓贸脫脷200K拢卢虏禄脢脟脭脷卤拢麓忙碌脛脢卤潞貌隆拢
        byte[] bytes = (byte[]) psndocAggVO.getParentVO().getPhoto();
        if (bytes != null && bytes.length >= 204800)
        {
            if (MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0480")/* "脠路脠脧卤拢麓忙" */,
                ResHelper.getString("6007psn", "16007psn0074")/* "脠脣脭卤脮脮脝卢麓贸脨隆脧碌脥鲁陆篓脪茅脦陋200KB脪脭脧脗拢卢脧脰脪脩鲁卢鲁枚拢卢脢脟路帽录脤脨酶卤拢麓忙?" */) != MessageDialog.ID_YES)
            {
                return false;
            }
            
            // throw new BusinessException("脠脣脭卤卤脿脗毛:" +
            // psndocAggVO.getParentVO().getAttributeValue(PsndocVO.CODE) + " 碌脛脮脮脝卢虏禄脛脺麓贸脫脷200KB");
        }
        
        adjustOrgJobVO(psndocAggVO);
        // 脤卯鲁盲脪禄脨漏脛卢脠脧脰碌
        fillDefaultValue(psndocAggVO);
        
        psndocAggVO = validatePsndoc(value);
        
        // 脮芒脌茂脨拢脩茅脳脫录炉路脟驴脮
        if (!validateSubNotNull())
        {
            return false;
        }
        
        if (psndocAggVO == null)
        {
            return false;
        }
        
        if (!((PsndocFormEditor) getEditor()).getBillCardPanel().getBillData().execValidateFormulas())
        {
            return false;
        }
        
        if (getModel().getUiState() == UIState.ADD)
        {
            
            boolean bl = NCLocator.getInstance().lookup(IHRLicenseChecker.class).checkPsnCount();
            
            if (bl
                && getModel().getContext().getNodeCode().equals(HICommonValue.FUNC_CODE_REGISTER)
                && MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0480")/* "脠路脠脧卤拢麓忙" */,
                    ResHelper.getString("6007psn", "06007psn0434")/* "碌卤脟掳脧碌脥鲁脭卤鹿陇脢媒脪脩麓茂碌陆脳卯麓贸脢脷脠篓脢媒,脢脟路帽卤拢麓忙?" */) != MessageDialog.ID_YES)
            {
                return false;
            }
            
            // 陆枚脭脷路碌脝赂脭脵脝赂脢卤脨拢脩茅脌毛脰掳潞贸脭脵脠毛脰掳脢卤录盲录盲赂么拢卢pk_psndoc虏禄脦陋null脭貌脦陋路碌脝赂脭脵脝赂脠脣脭卤
            if (getModel().getCurrentPkPsndoc() != null)
            {
                if (!validateRegDate()) return false;
            }
            try
            {
                getModel().add(value);
            }
            catch (Exception e)
            {
                if (e instanceof BillCodeRepeatBusinessException)
                {
                    if (HICommonValue.NBCR_PSNDOC_CODE.equals(((BillCodeRepeatBusinessException) e).getNbcrcode()))
                    {
                        ((PsndocFormEditor) getEditor()).getBillCardPanel().getHeadItem(PsndocVO.CODE)
                            .setValue(((BillCodeRepeatBusinessException) e).getNewCode());
                    }
                    if (HICommonValue.NBCR_PSNDOC_CLERKCODE.equals(((BillCodeRepeatBusinessException) e).getNbcrcode()))
                    {
                        ((PsndocFormEditor) getEditor()).getBillCardPanel().getHeadItem("hi_psnjob_clerkcode")
                            .setValue(((BillCodeRepeatBusinessException) e).getNewCode());
                    }
                    throw e;
                    
                }
                else
                    throw e;
            }
        }
        else if (getModel().getUiState() == UIState.EDIT)
        {
            /** 脭卤鹿陇脨脜脧垄脦卢禄陇脨脼赂脛脢卤拢卢脨拢脩茅卤脿脰脝 yunana */
            // 脧脿鹿脴脠脣脭卤虏禄陆酶脨脨卤脿脰脝脨拢脩茅
            // if(psndocAggVO.getParentVO().getPsnOrgVO().getIndocflag() != null
            // && psndocAggVO.getParentVO().getPsnOrgVO().getIndocflag().booleanValue()
            // && psndocAggVO.getParentVO().getPsnOrgVO().getPsntype().intValue() == ((Integer)
            // PsnType.EMPLOYEE.value()).intValue() ){
            // if(!validateBudget(value)){
            // return false;
            // }
            // }
            getModel().update(value);
        }
        getModel().setUiState(UIState.NOT_EDIT);
        // 卤拢麓忙潞贸拢卢脳脫录炉禄潞麓忙脠芦虏驴脟氓驴脮拢卢脰脴脨脗脠隆脢媒隆拢
        PsndocFormEditor editor = (PsndocFormEditor) getEditor();
        getModel().getSubHaveLoaded().clear();
        // 卤拢麓忙潞贸陆芦脠毛脰掳脌脿脨脥卤盲鲁脡鲁玫麓脦脠毛脰掳
        getModel().setInJobType(HICommonValue.JOB_HIRE);
        // editor.loadCurrentRowSubData();
        return true;
    }
    
    private boolean validateBudget(Object value) throws Exception
    {
        String pk_psndoc = ((PsndocAggVO) value).getParentVO().getPsnJobVO().getPk_psnjob();
        String[] strPk_psnjobs = {pk_psndoc};
        // 脨拢脩茅卤脿脰脝
        ValidateResultVO resultVOs[] =
            NCLocator.getInstance().lookup(IOrgBudgetQueryService.class).validateBudgetValue(getContext(), strPk_psnjobs);
        
        String strErrorMsg = "";
        String strWarningMsg = "";
        
        if (resultVOs != null)
        {
            for (ValidateResultVO resultVO : resultVOs)
            {
                if (!resultVO.isValid())
                {
                    strErrorMsg += "\n" + resultVO.getHintMsg();
                }
                else if (resultVO.getHintMsg() != null)
                {
                    strWarningMsg += "\n" + resultVO.getHintMsg();
                }
            }
            
            if (strErrorMsg.length() > 0)
            {
                throw new BusinessException(ResHelper.getString("6007psn", "06007psn0198")/*
                                                                                           * @res
                                                                                           * "脳陋脠毛碌脛脳茅脰炉鲁卢卤脿脟脪脦陋脩脧赂帽驴脴脰脝拢卢脳陋碌碌脢搂掳脺拢卢脧锚脧赂脨脜脧垄脠莽脧脗"
                                                                                           */+ strErrorMsg);
            }
            
            else if (strWarningMsg.length() > 0)
            {
                int iResult =
                    MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0475")/* "脠路脠脧录脤脨酶" */,
                        ResHelper.getString("6007psn", "06007psn0199")/* @res "脳陋脠毛碌脛脳茅脰炉鲁卢卤脿拢卢脧锚脧赂脨脜脧垄脠莽脧脗拢卢脢脟路帽录脤脨酶拢驴" */
                            + strWarningMsg);
                if (iResult != UIDialog.ID_YES)
                {
                    ActionHelper.setCancelHintMessage(this);
                    return false;
                }
            }
        }
        return true;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-30 15:10:36<br>
     * 
     * @throws Exception
     * @author Rocex Wang
     ***************************************************************************/
    protected void saveSort() throws Exception
    {
        getListView().stopCellEditing();
        BillPanelUtils.listNotNullValidate(getListView().getBillListPanel(), IBillItem.HEAD);
        BillModel headBillModel = listView.getBillListPanel().getHeadBillModel();
        int iRowCount = headBillModel.getRowCount();
        ArrayList<PsnJobVO> listJobVO = new ArrayList<PsnJobVO>();
        ArrayList<PsndocAggVO> listPsndocAggVO = new ArrayList<PsndocAggVO>();
        List<PsndocAggVO> listAllData = ((BillManageModel) getModel()).getData();
        if (listView.getBillListPanel().getHeadTable().isEditing())
        {
            listView.getBillListPanel().getHeadTable()
                .editingStopped(new ChangeEvent(listView.getBillListPanel().getHeadTable().getCellEditor()));
            listView.updateUI();
        }
        for (int i = 0; i < iRowCount; i++)
        {
            PsnJobVO psnJobVO = new PsnJobVO();
            psnJobVO.setPk_psnjob(listAllData.get(i).getParentVO().getPsnJobVO().getPk_psnjob());
            Object showOrder = headBillModel.getValueAt(i, "hi_psnjob_showorder");
            if (showOrder == null || showOrder.toString() == null || showOrder.toString().trim().equals(""))
            {
                psnJobVO.setShoworder(HICommonValue.DEFAULT_VALUE_SHOWORDER);
            }
            else
            {
                psnJobVO.setShoworder((Integer) showOrder);
            }
            psnJobVO.setStatus(VOStatus.UPDATED);
            listJobVO.add(psnJobVO);
            listPsndocAggVO.add(listAllData.get(i));
        }
        if (listJobVO.size() == 0)
        {
            return;
        }
        // PsnJobVO psnJobVOs[] =
        NCLocator.getInstance().lookup(IPsndocService.class).adjustPsnSort(listJobVO.toArray(new PsnJobVO[0]));
        // for (int i = 0; i < psnJobVOs.length; i++) {
        // for (Iterator iterator = listPsndocAggVO.iterator(); iterator.hasNext();) {
        // PsndocAggVO psndocAggVO = (PsndocAggVO) iterator.next();
        // if (ObjectUtils.equals(psnJobVOs[i].getPk_psnjob(),
        // psndocAggVO.getParentVO().getPsnJobVO().getPk_psnjob())) {
        // psndocAggVO.getParentVO().setPsnJobVO(psnJobVOs[i]);
        // break;
        // }
        // }
        // }
        // getModel().directlyUpdate(listPsndocAggVO.toArray(new PsndocAggVO[0]));
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-30 15:10:40<br>
     * 
     * @param datamanger
     * @author Rocex Wang
     ***************************************************************************/
    public void setDataManager(PsndocDataManager datamanger)
    {
        this.dataManager = datamanger;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-30 15:10:44<br>
     * 
     * @param listview
     * @author Rocex Wang
     ***************************************************************************/
    public void setListView(PsndocListView listview)
    {
        this.listView = listview;
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-6-1 10:52:14<br>
     * 
     * @param model
     * @author Rocex Wang
     ***************************************************************************/
    public void setModel(PsndocModel model)
    {
        super.setModel(model);
    }
    
    /***************************************************************************
     * <br>
     * Created on 2010-1-30 15:10:48<br>
     * 
     * @param supervalidator
     * @author Rocex Wang
     ***************************************************************************/
    public void setSuperValidator(SuperFormEditorValidatorUtil supervalidator)
    {
        this.superValidator = supervalidator;
    }
    public CourtFineValidator getCourtFineValidator() {
		if(courtFineValidator == null){
			courtFineValidator = new CourtFineValidator();
		}
		return courtFineValidator;
	}

	public void setCourtFineValidator(CourtFineValidator courtFineValidator) {
		this.courtFineValidator = courtFineValidator;
	}
    /***************************************************************************
     * <br>
     * Created on 2010-7-6 15:18:35<br>
     * 
     * @param value
     * @return PsndocAggVO
     * @throws ValidateWithLevelException
     * @throws BusinessException
     * @author Rocex Wang
     ***************************************************************************/
    private PsndocAggVO validatePsndoc(Object value) throws ValidateWithLevelException, BusinessException
    {
        validate(value);
        
        // 卤拢麓忙脨拢脩茅拢卢脫娄赂脙脤谩鲁枚脌麓脪禄赂枚bean
        PsndocAggVO psndocAggVO = (PsndocAggVO) value;
        // 法院強制扣款子集校驗
 		if (getCourtFineValidator() != null) {
 			ValidationFailure message = getCourtFineValidator().validate(value);
 			if(null != message){
 				MessageDialog.showErrorDlg(null, "校验", message.getMessage());
 				return null;
 			}
 		}
        // 脨拢脩茅碌卤脟掳脠脣脭卤脢脟路帽脪脩碌陆16脰脺脣锚
        UFLiteralDate date = psndocAggVO.getParentVO().getBirthdate();
        if (date != null)
        {
            UFLiteralDate today = PubEnv.getServerLiteralDate();
            int year = date.getYear();
            
            int year18 = year + 16;
            
            UFLiteralDate date18 = new UFLiteralDate(year18 + "-" + date.getMonth() + "-" + date.getDay());
            if (date18.afterDate(today))
            {
                // 虏禄鹿禄18脣锚
                MessageDialog.showWarningDlg(null, null, ResHelper.getString("6007psn", "06007psn0320"));
            }
        }
        
        // 脨拢脩茅碌卤脟掳脠脣脭卤脰陇录镁脳脫录炉脰脨脡铆路脻脰陇脢脟路帽潞脧路篓
        CertVO[] cert = (CertVO[]) psndocAggVO.getTableVO(CertVO.getDefaultTableName());
        if (cert != null && cert.length > 0)
        {
            for (CertVO c : cert)
            {
                if (StringUtils.isNotBlank(psndocAggVO.getParentVO().getId())
                    && StringUtils.isNotBlank(psndocAggVO.getParentVO().getIdtype()))
                {
                    if (c.getIsstart().equals(UFBoolean.TRUE))
                    {
//                        c.setId(psndocAggVO.getParentVO().getId());
//                        c.setIdtype(psndocAggVO.getParentVO().getIdtype());
                        //NCdp205860006拢卢驴脥禄搂虏煤脝路麓铆脦贸拢卢麓脦鲁枚脗脽录颅脫娄赂脙脢脟陆芦鹿麓脩隆碌脛脛卢脠脧碌脛脰陇录镁脨脜脧垄赂眉脨脗碌陆脠脣脭卤脨脜脧垄卤铆拢卢脤忙禄禄脡脧脙忙碌脛脗脽录颅
                        psndocAggVO.getParentVO().setId(c.getId());
                    	psndocAggVO.getParentVO().setIdtype(c.getIdtype());
                    }
                }
                else
                {
                    continue;
                }
                String strClassName = PsnIdtypeQuery.getPsnIdtypeVo(c.getIdtype()).getIdtypevalidat();
                if (StringUtils.isEmpty(strClassName)) continue;
                try
                {
                    nc.vo.bd.psn.PsndocVO psndocUapVO = new nc.vo.bd.psn.PsndocVO();
                    BeanUtils.copyProperties(psndocUapVO, psndocAggVO.getParentVO());
                    psndocUapVO.setId(c.getId());
                    psndocUapVO.setIdtype(c.getIdtype());
                    ValidationFailure failure =
                        ((nc.bs.uif2.validation.Validator) Class.forName(strClassName).newInstance()).validate(psndocUapVO);
                    if (failure != null)
                    {
                        throw new ValidateWithLevelException(failure.getMessage(), ValidateWithLevelException.LEVEL_DECIDEBYUSER);
                    }
                }
                catch (InstantiationException ex)
                {
                    Logger.error(ex.getMessage(), ex);
                }
                
                catch (IllegalAccessException ex)
                {
                    Logger.error(ex.getMessage(), ex);
                }
                catch (ClassNotFoundException ex)
                {
                    Logger.error(ex.getMessage(), ex);
                }
                catch (InvocationTargetException ex)
                {
                    Logger.error(ex.getMessage(), ex);
                }
                catch (ValidateWithLevelException ex)
                {
                    if (ex.getLevel() == ValidateWithLevelException.LEVEL_SERIOUS)
                    {
                        throw ex;
                    }
                    if (ex.getLevel() == ValidateWithLevelException.LEVEL_DECIDEBYUSER)
                    {
                        if (MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0475")/*
                                                                                                                       * @
                                                                                                                       * res
                                                                                                                       * "脠路脠脧录脤脨酶"
                                                                                                                       */, ex.getMessage()
                            + ResHelper.getString("6007psn", "06007psn0130")/*
                                                                             * @res "拢卢脢脟路帽录脤脨酶拢驴"
                                                                             */) != UIDialog.ID_YES)
                        {
                            return null;
                        }
                    }
                }
            }
        }
        
        if (superValidator != null)
        {
            try
            {
                superValidator.validate("additionalValidationOfSave");
            }
            catch (ValidateWithLevelException ex)
            {
                if (ex.getLevel() == ValidateWithLevelException.LEVEL_SERIOUS)
                {
                    throw ex;
                }
                if (ex.getLevel() == ValidateWithLevelException.LEVEL_DECIDEBYUSER)
                {
                    if (MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0475")/*
                                                                                                                   * @
                                                                                                                   * res
                                                                                                                   * "脠路脠脧录脤脨酶"
                                                                                                                   */, ex.getMessage()
                        + ResHelper.getString("6007psn", "06007psn0130")/*
                                                                         * @res "拢卢脢脟路帽录脤脨酶拢驴"
                                                                         */) != UIDialog.ID_YES)
                    {
                        return null;
                    }
                }
                else if (ex.getLevel() == ValidateWithLevelException.LEVEL_CANACCEPT)
                {
                    MessageDialog.showWarningDlg(getEntranceUI(), null, ex.getMessage());
                }
            }
        }
        
        // 脰庐脟掳碌脛脠毛脰掳碌脟录脟露脭禄掳驴貌脰脨碌茫禄梅脠路露篓脢卤碌脛脨拢脩茅脧脰脭脷卤拢麓忙脢卤脪虏碌脙脨拢脩茅
        try
        {
            getAddSave(psndocAggVO);
        }
        catch (Exception e)
        {
            throw new BusinessException(e.getMessage());
        }
        
        return psndocAggVO;
    }
    
    public PsndocAggVO getAddSave(PsndocAggVO psndocsAggVO) throws Exception
    {
        PsndocVO psndocVO = psndocsAggVO.getParentVO();// 卤拢麓忙脢卤麓脫陆莽脙忙脡脧禄帽脠隆碌脛脢媒戮脻拢卢脮芒脌茂脝盲脢碌脰禄脨猫脪陋脫脙碌陆脣眉碌脛parentvo录麓驴脡拢卢虏禄露脭麓脣露脭脧贸脳枚脨脼赂脛虏脵脳梅
        PsndocAggVO psndocAggVO = null;// 赂霉戮脻脨脮脙没+脰陇录镁脌脿脨脥+脰陇录镁潞脜麓脫脧碌脥鲁脌茂脙忙虏茅脩炉鲁枚碌脛脪脩戮颅麓忙脭脷碌脛脠脣脭卤
        IPsndocService psndocService = NCLocator.getInstance().lookup(IPsndocService.class);
        try
        {
            psndocAggVO = psndocService.checkPsnUnique(psndocVO, true);
        }
        catch (BusinessException ex)
        {
            throw new BusinessException(ex.getMessage());
        }
        
        if (psndocAggVO == null || psndocAggVO.getParentVO() == null)// 脙禄脫脨脳茅脰炉鹿脴脧碌拢卢脰卤陆脫脨脗脭枚脠脣脭卤
        {
            return psndocsAggVO;
        }
        
        if (psndocAggVO.getParentVO().getDie_date() != null && psndocAggVO.getParentVO().getDie_remark() != null)
        {
            throw new BusinessException(ResHelper.getString("6007psn", "06007psn0346")/* "碌卤脟掳脗录脠毛脠脣脭卤脪脩脡铆鹿脢,虏禄脛脺录脤脨酶脭枚录脫." */);
        }
        
        // 脠莽鹿没脠脣脭卤脪脩麓忙脭脷拢卢脭貌脢脳脧脠脜脨露脧脢脟路帽脦陋UAP鹿脺脌铆脠脣脭卤
        UFBoolean isuapmanage = psndocAggVO.getParentVO().getIsuapmanage();
        if (isuapmanage.booleanValue())
        {
            // 脝么脫脙脳麓脤卢
            int enablestate = psndocAggVO.getParentVO().getEnablestate();
            if (enablestate == IPubEnumConst.ENABLESTATE_DISABLE)
            {// 脪脩脥拢脫脙
             // 脤谩脢戮隆掳赂脙脠脣脭卤脦陋UAP鹿脺脌铆碌脛脥拢脫脙脠脣脭卤拢卢脢脟路帽录脤脨酶拢驴隆卤
                if (MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0475")/*
                                                                                                               * @
                                                                                                               * res
                                                                                                               * "脠路脠脧录脤脨酶"
                                                                                                               */,
                    ResHelper.getString("6007psn", "06007psn0456")/* "赂脙脠脣脭卤脦陋UAP鹿脺脌铆碌脛脥拢脫脙脠脣脭卤拢卢脢脟路帽录脤脨酶拢驴" */) == UIDialog.ID_YES)
                {
                    return psndocsAggVO;
                }
                else
                {
                    throw new BusinessException(ResHelper.getString("6007psn", "06007psn0456")/* "赂脙脠脣脭卤脦陋UAP鹿脺脌铆碌脛脥拢脫脙脠脣脭卤拢卢脢脟路帽录脤脨酶拢驴" */);
                }
            }
        }
        
        if (!psndocAggVO.getParentVO().getPsnOrgVO().getEndflag().booleanValue()) // 脫脨脳茅脰炉鹿脴脧碌拢卢虏垄脟脪脡脨脦麓陆谩脢酶隆拢
        {
            String strMsg = ResHelper.getString("6007psn", "06007psn0145")/*
                                                                           * @res "录炉脥脜脰脨脪脩麓忙脭脷脧脿脥卢脠脣脭卤拢卢虏禄脛脺脭脵麓脦虏脡录炉拢隆"
                                                                           */
                + "\n";
            strMsg += generateUniqueMsg(psndocAggVO.getParentVO(), psndocVO, false);
            throw new BusinessException(strMsg);
        }
        else if (psndocAggVO.getParentVO().getPsnOrgVO().getEndflag().booleanValue())
        {
            // 脭酶脫脨鹿媒脳茅脰炉鹿脴脧碌拢卢脧脰脪脩陆谩脢酶
            // String strMsg = "麓脣脠脣脭酶脫脨鹿媒脳茅脰炉鹿脴脧碌拢卢脢脟路帽录脤脨酶(路碌脝赂隆垄脭脵脝赂) ?\n{0}";
            String strMsg = ResHelper.getString("6007psn", "06007psn0146")/*
                                                                           * @res "麓脣脠脣脭酶脫脨鹿媒脳茅脰炉鹿脴脧碌拢卢脢脟路帽录脤脨酶 ?"
                                                                           */
                + "\n";
            if (psndocAggVO.getParentVO().getPsnOrgVO().getPsntype().intValue() == ((Integer) PsnType.POI.value()).intValue())
            {
                // 脧脿鹿脴脠脣脭卤脤谩脢戮脨脜脧垄虏禄脥卢
                strMsg = ResHelper.getString("6007psn", "06007psn0147")/*
                                                                        * @res "麓脣脠脣脦陋录炉脥脜脌煤脢路脧脿鹿脴脠脣脭卤拢卢脨脜脧垄脠莽脧脗拢卢脢脟路帽录脤脨酶?"
                                                                        */
                    + "\n";
            }
            strMsg += generateUniqueMsg(psndocAggVO.getParentVO(), psndocVO, true);
            if (MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0475")/*
                                                                                                           * @res
                                                                                                           * "脠路脠脧录脤脨酶"
                                                                                                           */, strMsg) == UIDialog.ID_YES)
            {
                psndocAggVO.getParentVO().setPsnOrgVO(psndocVO.getPsnOrgVO());
                psndocAggVO.getParentVO().setPsnJobVO(psndocVO.getPsnJobVO());
                psndocsAggVO.setParentVO(psndocAggVO.getParentVO());
                psndocsAggVO.setTableVO(CertVO.getDefaultTableName(), null);//
                return psndocsAggVO;
            }
        }
        
        return psndocsAggVO;
    }
    
    private String generateUniqueMsg(PsndocVO dbVO, PsndocVO clientVO, boolean isreturn)
    {
        String strFieldCodes[] = {PsndocVO.NAME, PsndocVO.ID, PsndocVO.IDTYPE};
        String strMsg = "";
        String idtype = dbVO.getIdtype();
        PsnIdtypeVO psnIdtypeVO = null;
        try
        {
            psnIdtypeVO =
                (PsnIdtypeVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null, PsnIdtypeVO.class, idtype);
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
        }
        for (String strFieldCode : strFieldCodes)
        {
            // String strFieldChnName = getCenterPanel().getBillData().getHeadItem(strFieldCode).getName();
            String strFieldChnName = ((PsndocFormEditor) getEditor()).getBillCardPanel().getBillData().getHeadItem(strFieldCode).getName();
            // if (PsndocVO.ID.equalsIgnoreCase(strFieldCode))
            // {
            // strMsg += strFieldChnName + ": " + clientVO.getId() + "\n";
            // }
            if (PsndocVO.IDTYPE.equalsIgnoreCase(strFieldCode))
            {
                strMsg += strFieldChnName + ": " + VOUtils.getNameByVO(psnIdtypeVO) + (isreturn ? "\n" : "拢卢");
            }
            else if (strFieldCode.startsWith(PsndocVO.NAME))
            {
                String fieldValue = MultiLangHelper.getName(dbVO);
                fieldValue = StringUtils.isBlank(fieldValue) ? ResHelper.getString("6007psn", "06007psn0148")/*
                                                                                                              * @
                                                                                                              * res
                                                                                                              * "脦脼"
                                                                                                              */: fieldValue;
                strMsg += strFieldChnName + ": " + fieldValue + (isreturn ? "\n" : "拢卢");
            }
            else
            {
                String fieldValue = (String) dbVO.getAttributeValue(strFieldCode);
                // 麓娄脌铆脤谩脢戮脰脨碌脛null脰碌
                fieldValue = StringUtils.isBlank(fieldValue) ? ResHelper.getString("6007psn", "06007psn0148")/*
                                                                                                              * @
                                                                                                              * res
                                                                                                              * "脦脼"
                                                                                                              */: fieldValue;
                strMsg += strFieldChnName + ": " + fieldValue + (isreturn ? "\n" : "拢卢");
            }
        }
        if (dbVO.getPsnJobVO() == null)
        {
            return strMsg;
        }
        
        String orgname = dbVO.getPsnJobVO().getOrgname() == null ? ResHelper.getString("6007psn", "06007psn0148")/*
                                                                                                                  * @
                                                                                                                  * res
                                                                                                                  * "脦脼"
                                                                                                                  */
        : (String) dbVO.getPsnJobVO().getOrgname();
        strMsg += ResHelper.getString("6007psn", "06007psn0149")/* @res "脣霉脭脷脳茅脰炉: " */
            + orgname + (isreturn ? "\n" : "拢卢");
        
        String deptname = dbVO.getPsnJobVO().getDeptname() == null ? ResHelper.getString("6007psn", "06007psn0148")/*
                                                                                                                    * @
                                                                                                                    * res
                                                                                                                    * "脦脼"
                                                                                                                    */
        : (String) dbVO.getPsnJobVO().getDeptname();
        strMsg += ResHelper.getString("6007psn", "06007psn0150")/* @res "脣霉脭脷虏驴脙脜: " */
            + deptname + (isreturn ? "\n" : "拢卢");
        
        if (dbVO.getPsnOrgVO().getPsntype().intValue() != ((Integer) PsnType.POI.value()).intValue())
        {
            // 脧脿鹿脴脠脣脭卤脙禄脫脨赂脷脦禄
            String jobname = dbVO.getPsnJobVO().getJobname() == null ? ResHelper.getString("6007psn", "06007psn0148")/*
                                                                                                                      * @
                                                                                                                      * res
                                                                                                                      * "脦脼"
                                                                                                                      */
            : (String) dbVO.getPsnJobVO().getJobname();
            strMsg += ResHelper.getString("6007psn", "06007psn0151")/*
                                                                     * @res "脣霉脭脷赂脷脦禄: "
                                                                     */
                + jobname + "";
        }
        return strMsg;
    }
    
    /**
     * 脌毛脰掳潞贸脭脵脠毛脰掳脢卤录盲录盲赂么脨拢脩茅脨拢脩茅
     * 
     * @return boolean 鲁脡鹿娄脦陋true拢卢虏禄鲁脡鹿娄脦陋false
     * @author yangshuo
     * @throws Exception
     * @Created on 2012-12-3 脡脧脦莽11:21:37
     */
    private boolean validateRegDate() throws Exception
    {
        
        Object value = getEditor().getValue();
        PsndocAggVO psndocAggVO = (PsndocAggVO) value;
        // 脧脰脭脷碌脛脠毛脰掳脠脮脝脷
        UFLiteralDate currBegindate = psndocAggVO.getParentVO().getPsnOrgVO().getBegindate();
        String pk_org = getModel().getContext().getPk_org();
        String pk_psndoc = psndocAggVO.getParentVO().getPk_psndoc();
        HashMap<String, String> org = new HashMap<String, String>();
        HashMap<String, UFLiteralDate> date = new HashMap<String, UFLiteralDate>();
        org.put(pk_psndoc, pk_org);
        date.put(pk_psndoc, currBegindate);
        HashMap<String, String> name = NCLocator.getInstance().lookup(IPsndocQryService.class).validateRegDate(org, date);
        
        if (name == null)
        {
            return true;
        }
        Integer para = SysInitQuery.getParaInt(pk_org, HICommonValue.PARA_7);
        return MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0475")/*
                                                                                                          * @res
                                                                                                          * "脠路脠脧录脤脨酶"
                                                                                                          */,
            ResHelper.getString("6007psn", "06007psn0440", name.values().toArray(new String[0])[0], para.toString())) == UIDialog.ID_YES;
        /* @res"脠脣脭卤[{0}]脡脧脪禄麓脦脌毛脰掳脰脕陆帽虏禄脗煤{1}赂枚脭脗拢卢脢脟路帽录脤脨酶脠毛脰掳拢驴" */
        
    }
    
    /**
     * 脨拢脩茅脳脫录炉路脟驴脮
     * 
     * @return 脦陋驴脮脟脪脫脙禄搂脭脷脤谩脢戮禄掳驴貌脰脨脩隆脭帽路帽路碌禄脴 false拢卢脝盲脣没脟茅驴枚路碌禄脴true
     * @author yangshuo
     * @Created on 2012-12-7 脧脗脦莽04:36:28
     */
    private boolean validateSubNotNull() throws BusinessException
    {
        Object value = getEditor().getValue();
        PsndocAggVO psndocAggVO = (PsndocAggVO) value;
        HashMap<String, String> showTab = getBodyShowTablenames();
        
        if (getModel().getUiState() == UIState.ADD)
        {
            String[] nullSubSetNames = getModel().queryNotNullSubset(psndocAggVO, showTab);
            if (nullSubSetNames != null) return showNullSubsetWarningDlg(nullSubSetNames);
            else
                return true;
        }
        else if (getModel().getUiState() == UIState.EDIT)
        {
            String[] nullSubSetNames = getModel().validateSubNotNull(psndocAggVO, showTab);
            if (nullSubSetNames != null) return showNullSubsetWarningDlg(nullSubSetNames);
            else
                return true;
        }
        
        return true;
    }
    
    /**
     * 脧脭脢戮脳脫录炉脦陋驴脮戮炉赂忙露脭禄掳驴貌
     * 
     * @return 脦陋驴脮脟脪脫脙禄搂脭脷脤谩脢戮禄掳驴貌脰脨脩隆脭帽路帽路碌禄脴 false拢卢脝盲脣没脟茅驴枚路碌禄脴true
     * @author yangshuo
     * @Created on 2012-12-7 脧脗脦莽04:33:37
     */
    
    private boolean showNullSubsetWarningDlg(String[] nullSubSetNames)
    {
        if (nullSubSetNames != null)
        {
            // 麓娄脌铆脤谩脢戮脧没脧垄拢卢赂帽脢陆脦陋[..,..,...]
            String msg = "";
            for (String nullSubSetName : nullSubSetNames)
            {
                if (StringUtils.isEmpty(msg)) msg = "[" + nullSubSetName;
                
                else
                    msg += "," + nullSubSetName;
            }
            msg += "]";
            
            // 碌炉鲁枚脧脭脢戮露脭禄掳驴貌
            int result = MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0480", new String[]{msg})/* "脠路脠脧卤拢麓忙" */
            , ResHelper.getString("6007psn", "06007psn0441", new String[]{msg})/* "{0}脳脫录炉脙禄脫脨脗录脠毛脠脦潞脦脢媒戮脻拢卢脢脟路帽录脤脨酶卤拢麓忙?" */);
            if (UIDialog.ID_NO == result) return false;
            else
                return true;
        }
        return true;
    }
    
    /**
     * 禄帽碌脙陆莽脙忙脡脧脧脭脢戮脣霉脫脨脳脫录炉卤铆脙没 -> 卤铆脧脭脢戮脙没鲁脝 hashmap
     * 
     * @author yangshuo
     * @Created on 2012-12-7 脡脧脦莽10:10:27
     */
    private HashMap<String, String> getBodyShowTablenames()
    {
        PsndocFormEditor editor = (PsndocFormEditor) getEditor();
        String[] tableCodes = editor.getBillCardPanel().getBillData().getBodyTableCodes();
        HashMap<String, String> result = new HashMap<String, String>();
        
        for (String tableCode : tableCodes)
        {
            // 脠莽鹿没脳脫录炉脧脗脣霉脫脨脧卯露录虏禄脧脭脢戮脭貌虏禄脨拢脩茅
            if (null == editor.getBillCardPanel().getBillData().getShowItems(BillData.BODY, tableCode)) continue;
            
            result.put(tableCode, editor.getBillCardPanel().getBillData().getBodyTableName(tableCode));
        }
        
        return result;
    }
    
}
