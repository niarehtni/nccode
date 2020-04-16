package nc.ui.bd.defdoc.action;

import nc.bs.framework.common.NCLocator;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.ui.bd.defdoc.DefdocAppService;
import nc.ui.bd.defdoc.DefdocLoginContext;
import nc.ui.bd.defdoc.IDefdocEvent;
import nc.ui.bd.defdoc.view.DateChooseDialog;
import nc.ui.bd.pub.actions.ManageModeActionInterceptor;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.actions.ActionInterceptor;
import nc.ui.uif2.actions.CompositeActionInterceptor;
import nc.ui.uif2.actions.batch.BatchDisableLineAction;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.defdoc.DefdoclistVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

public class DefdocBatchDisableAction extends BatchDisableLineAction {

	private static final long serialVersionUID = -5744416238840897388L;
	private ManageModeActionInterceptor manageModeActionInterceptor = new ManageModeActionInterceptor();
	private CompositeActionInterceptor interceptor;
	public static final String GROUP_INSURANCE_TYPE_FUNCTION_CODE = "60401041";

	public DefdocBatchDisableAction() {
		super();
		interceptor = new CompositeActionInterceptor();
		super.setInterceptor(interceptor);
		interceptor.add(manageModeActionInterceptor);
		setInterceptor(manageModeActionInterceptor);
	}

	@Override
	public void setInterceptor(ActionInterceptor interceptor) {
		this.interceptor.add(interceptor);

	}
	
	@Override
	public void handleEvent(AppEvent event) {
		if (IDefdocEvent.DEFDOCLIST_CHANGED == event.getType()) {
			DefdoclistVO defdoclist = ((DefdocLoginContext) getModel()
					.getContext()).getDefdoclist();
			if (defdoclist != null) {
				setResourceCode(defdoclist.getCode());
			}
		}
		super.handleEvent(event);
	}

	@Override
	public void setModel(BatchBillTableModel model) {
		super.setModel(model);
		manageModeActionInterceptor.setModel(model);
	}

	@Override
    public Object doDisable(Object obj) throws Exception {
	// �ű�����������˱�����
	boolean isDisable = true;
	if (getModel().getContext().getFuncInfo().getFuncode().equals(GROUP_INSURANCE_TYPE_FUNCTION_CODE)) {
	    isDisable = doDisableIns((DefdocVO) obj);
	}
	DefdocVO[] newObjs = new DefdocVO[] { (DefdocVO) obj };
	if(isDisable){
	    newObjs = ((DefdocAppService) getModel().getService())
			.disableDefdocs(new DefdocVO[] { (DefdocVO) obj });
	}
	return newObjs[0];
    }
	
	
	public boolean doDisableIns(DefdocVO newObj) throws BusinessException{
		if(newObj == null || newObj.getPk_defdoc() == null){
			return false;
		}
		DateChooseDialog dateChooseDlg = 
				new DateChooseDialog(getModel().getContext().getEntranceUI(),"�˱�����","�������˱�����:",false);
		int result = dateChooseDlg.showModal();
		UFDate disDate = dateChooseDlg.getReturn();
		if(disDate!=null && result==0){
		    
			NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class)
				.delGroupInsByType(disDate,newObj.getPk_defdoc());  
		}else{
		    return false;
		}
		
		return true;
	}

}
