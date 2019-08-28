package nc.ui.om.hrdept.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.jf.IUpdatePsnJob;
import nc.itf.om.IDeptAdjustService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.hr.uif2.action.SaveAction;
import nc.ui.om.hrdept.model.OrgTreeDataManager;
import nc.ui.om.hrdept.model.TreeDeptAppModel;
import nc.ui.om.hrdept.view.DeptCardForm;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.vo.logging.Debug;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;




/**
 * ����Action<br>
 * 
 * @author zhangdd
 * 
 */
public class SaveDeptAction extends SaveAction {

	private static final long serialVersionUID = 1L;
	
	private OrgTreeDataManager treeDataManager;

    public OrgTreeDataManager getTreeDataManager()
    {
        return treeDataManager;
    }

    public void setTreeDataManager(OrgTreeDataManager treeDataManager)
    {
        this.treeDataManager = treeDataManager;
    }

    @Override
    public void doAction(ActionEvent evt) throws Exception
    {
    	IEditor editor = getEditor();
        AggHRDeptVO objValue = (AggHRDeptVO)editor.getValue();
        String approvenum = (String) ((DeptCardForm) editor).getBillCardPanel().getHeadItem("approvenum").getValueObject();
		String approvedept = (String) ((DeptCardForm) editor).getBillCardPanel().getHeadItem("approvedept").getValueObject();
		((HRDeptVO)objValue.getParentVO()).setApprovenum(approvenum);
		((HRDeptVO)objValue.getParentVO()).setApprovedept(approvedept);
        
        validate(objValue);
        //�Ѿ����ڵĲ��ű��벻�ñ���  wangywt 20190805 begin
        IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());   
		String sqlStr = "select count(*) from org_dept where code = '"+((HRDeptVO)objValue.getParentVO()).getCode()+"' ";
		Integer num = (Integer) iUAPQueryBS.executeQuery(sqlStr,new ColumnProcessor());
		if (getModel().getUiState() == UIState.ADD){
			if(num != null && num.intValue() != 0){
				throw new BusinessException("���T���a�ѽ����ڣ�");
			}
		}
		//�Ѿ����ڵĲ��ű��벻�ñ���  wangywt 20190805 end
        boolean validateResult = true;
        if (getEditor() instanceof BillForm)
        {
            validateResult = ((BillForm) getEditor()).getBillCardPanel().getBillData().execValidateFormulas();
        }
        else if (getEditor() instanceof BillCardPanel)
        {
            validateResult = ((BillCardPanel) getEditor()).getBillData().execValidateFormulas();
        }
        
        if (!validateResult)
        {
            return;
        }
       
        if (getModel().getUiState() == UIState.ADD)
        {
        	Object returnObj = null;
        	//��������r�g��춮�ǰ�r�g,���N�M�в��T�汾������
        	if(needDealVer(objValue).booleanValue()){
        		returnObj = NCLocator.getInstance().lookup(IDeptAdjustService.class).writeBack4DeptAdd(objValue);
        	}else{
        		//����ϼ�����Ϊδ��������,��ô�¼����Ų���Ϊ���̲���
        		isFatherNotEffective(objValue);
        		returnObj = getModel().add(objValue);
        	}
            
            AggHRDeptVO selectedVO=(AggHRDeptVO) returnObj;
            if (getModel() instanceof HierachicalDataAppModel)
            {
                ((HierachicalDataAppModel) getModel()).setSelectedData(returnObj);
            }
            if (getTreeDataManager().getModel() instanceof HierachicalDataAppModel)
            {
                
                TreeDeptAppModel treeAppModel=(TreeDeptAppModel) getTreeDataManager().getModel();
                treeAppModel.directlyAdd(selectedVO.getParentVO());
                treeAppModel.setSelectedData(selectedVO.getParentVO());
            }
            //���¹�����Ϣ������Ƿ������ֶ�
            NCLocator.getInstance().lookup(IUpdatePsnJob.class).updateSupervisor(((HRDeptVO)objValue.getParentVO()).getPrincipal());
        }
        else if (getModel().getUiState() == UIState.EDIT)
        {
        	//����֮ǰ��ȡ�˲��ŵĸ�����
        	String principal = NCLocator.getInstance().lookup(IUpdatePsnJob.class).getOldPrincipal(((HRDeptVO)objValue.getParentVO()).getPk_dept());
            getModel().update(objValue);
            AggHRDeptVO selectedVO=(AggHRDeptVO) objValue;
            TreeDeptAppModel treeAppModel=(TreeDeptAppModel) getTreeDataManager().getModel();
            treeAppModel.directlyUpdate(selectedVO.getParentVO());
            treeAppModel.setSelectedData(selectedVO.getParentVO());
            //���¹�����Ϣ������Ƿ������ֶ�
            NCLocator.getInstance().lookup(IUpdatePsnJob.class).updateSupervisor(((HRDeptVO)objValue.getParentVO()).getPrincipal(), principal);
        }
        getModel().setUiState(UIState.NOT_EDIT);
    }
    //�Д��Ƿ���Ҫ�M�а汾������
    private UFBoolean needDealVer(AggHRDeptVO vo)throws BusinessException{
    	if(null != vo && null != vo.getParentVO() 
    			&& ((HRDeptVO)vo.getParentVO()).getCreatedate() != null){
    		HRDeptVO deptVO = (HRDeptVO)vo.getParentVO();
    		if(null != deptVO && deptVO.getCreatedate() != null && deptVO.getCreatedate().after(new UFLiteralDate())){
    			//�¼����ŵĳ���ʱ�䲻�������ϼ�����
    			if(null != vo && null != vo.getParentVO() 
    	    			&& ((HRDeptVO)vo.getParentVO()).getPk_fatherorg() != null){
    	    		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());   
    	    		String sqlStr = "select CREATEDATE from org_dept where pk_dept = '"+((HRDeptVO)vo.getParentVO()).getPk_fatherorg()+"' ";
    	    		//��ȡ�ϼ����ŵ�����״̬
    	    		UFLiteralDate createdate = null;
    	    		try{
    	    			createdate = new UFLiteralDate((String)iUAPQueryBS. executeQuery(sqlStr,new ColumnProcessor())); 
    	    		}catch(Exception e){
    	    			Debug.debug(e.getMessage());
    	    		}
    	    		//ֻ�����ϼ��������õ�״̬��,���ܽ����¼�����
    	    		if(createdate != null && createdate.after(deptVO.getCreatedate()) ){
    	    			throw new BusinessException("�¼����T�ĳ����r�g��������ϼ����T!");
    	    		}else{
    	    			return new UFBoolean(true);
    	    		}
    	    	}
    			
    		}
    	}
    	return new UFBoolean(false);
    }
    /**
     * �ϼ����Ų���Ϊδ��Ч����
     * @return
     */
    private boolean isFatherNotEffective(AggHRDeptVO vo) throws BusinessException{
    	if(null != vo && null != vo.getParentVO() 
    			&& ((HRDeptVO)vo.getParentVO()).getPk_fatherorg() != null){
    		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());   
    		String sqlStr = "select enablestate from org_dept where pk_dept = '"+((HRDeptVO)vo.getParentVO()).getPk_fatherorg()+"' ";
    		//��ȡ�ϼ����ŵ�����״̬
    		Integer isEffective = null;
    		try{
    			isEffective = (Integer)iUAPQueryBS. executeQuery(sqlStr,new ColumnProcessor()); 
    		}catch(Exception e){
    			Debug.debug(e.getMessage());
    		}
    		//ֻ�����ϼ��������õ�״̬��,���ܽ����¼�����
    		if(isEffective != null && 2 == isEffective ){
    			return true;
    		}
    	}
    	if(null != vo && null != vo.getParentVO() 
    			&& ((HRDeptVO)vo.getParentVO()).getPk_fatherorg() == null){
    		return true;
    	}
    	throw new BusinessException("�ϼ����T���ܞ鮔ǰ���T���¼����ŵĳ���ʱ�䲻�������ϼ�����!");
    	//return false;
    }
	
}
