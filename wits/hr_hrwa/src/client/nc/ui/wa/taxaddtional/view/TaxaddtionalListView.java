package nc.ui.wa.taxaddtional.view;

import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hr.uif2.view.HrBillListView;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.AppEventConst;

/**
 * 
 * @author: xuhw
 * @date: 2010-6-25 ����04:11:37
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class TaxaddtionalListView extends HrBillListView {
    @SuppressWarnings("restriction")
	public void handleEvent(AppEvent event)
    {
        if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType()))
        {
            BillPanelUtils.setPkorgToRefModel(getBillListPanel(), getModel().getContext().getPk_org());
        }
        
        super.handleEvent(event);
    }
}
