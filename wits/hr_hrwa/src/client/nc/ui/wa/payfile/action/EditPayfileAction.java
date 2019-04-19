package nc.ui.wa.payfile.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.wa.payfile.model.PayfileAppModel;
import nc.ui.wa.payfile.view.PayfileFormEditor;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.payfile.PayfileVO;

/**
 * 修改薪资档案
 * 
 * @author: zhoucx
 * @date: 2009-11-27 上午09:12:39
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class EditPayfileAction extends PayfileBaseAction {

	private static String DF9A="1001ZZ1000000001NEGQ";//业务代号
	private static String DF9B="1001ZZ1000000001NEGR";//费用别代号
	private static String DF92="1001ZZ1000000001NEGT";//项目代号
	
	public EditPayfileAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.EDIT);
		setResourceCode(IHRWADataResCode.PAYFILE);
	}

	@SuppressWarnings("restriction")
	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		checkDataPermission();
		getModel().setUiState(UIState.EDIT);
		IUAPQueryBS query = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		//WaClassVO vo = (WaClassVO)query.retrieveByPK("", arg1)
		PayfileVO pf = (PayfileVO)getModel().getSelectedData();
		String wa_class = pf.getPk_wa_class();//getModel().getSelectedData();
		WaClassVO vo = (WaClassVO) query.retrieveByPK(WaClassVO.class, wa_class);
		BillCardPanel panel = ((PayfileFormEditor)getEditor()).getBillCardPanel();
		String declaretype = vo==null?"":vo.getDeclareform();
		if(DF9A.equals(declaretype)){
			panel.getHeadItem("biztype").setEnabled(true);//业务代号
			panel.getHeadItem("feetype").setEnabled(false);//费用别代号
			panel.getHeadItem("projectcode").setEnabled(false);//项目代号
		}else if(DF9B.equals(declaretype)){
			panel.getHeadItem("biztype").setEnabled(false);//业务代号
			panel.getHeadItem("feetype").setEnabled(true);//费用别代号
			panel.getHeadItem("projectcode").setEnabled(false);//项目代号
		}else if(DF92.equals(declaretype)){
			panel.getHeadItem("biztype").setEnabled(false);//业务代号
			panel.getHeadItem("feetype").setEnabled(false);//费用别代号
			panel.getHeadItem("projectcode").setEnabled(true);//项目代号
		}else{
			panel.getHeadItem("biztype").setEnabled(false);//业务代号
			panel.getHeadItem("feetype").setEnabled(false);//费用别代号
			panel.getHeadItem("projectcode").setEnabled(false);//项目代号
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see nc.ui.uif2.actions.EditAction#isActionEnable()
	 */
	@Override
	protected boolean isActionEnable() {
		boolean isEnable = false;
		if (super.isActionEnable() && getModel().getUiState() == UIState.NOT_EDIT
				&& getModel().getSelectedData() != null) {
			isEnable = ((PayfileAppModel) this.getModel()).canEdit();
		}
		return isEnable;
	}

}
