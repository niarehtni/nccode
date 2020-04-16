package nc.ui.twhr.twhr_declaration.ace.view;

import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.CompanyCountBillModel;
import nc.ui.pubapp.uif2app.view.ShowUpableBillListView;
import nc.ui.uif2.AppEvent;

public class DeclarationListView extends ShowUpableBillListView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6178345724917694319L;
	@Override
	public void initUI() {
		super.initUI();
		//�ϼƵ�ʱ��,��������
		BillModel.TotalTableModel oldCountModel = (BillModel.TotalTableModel)getBillListPanel().getChildListPanel("id_companybvo").getTableModel().getTotalTableModel();
		
		CompanyCountBillModel newCountModel = 
				new CompanyCountBillModel(getBillListPanel().getChildListPanel("id_companybvo").getTableModel(),getBillListPanel().getChildListPanel("id_companybvo").getTableModel());
		newCountModel.setDataVector(oldCountModel.getDataVector(), null);
		
		getBillListPanel().getChildListPanel("id_companybvo").getTableModel().setTotalTableModel(newCountModel);
	}


	@Override
	public void handleEvent(AppEvent event) {
		if(null != getBillListPanel().getChildListPanel("id_companybvo")){
			getBillListPanel().getChildListPanel("id_companybvo").setTotalRowShow(true);
		}
		if(null != getBillListPanel().getParentListPanel()){
			//��������
			getBillListPanel().getParentListPanel().setVisible(false);
		}
		super.handleEvent(event);
		
	}
	
	
	
	
}
