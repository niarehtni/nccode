package nc.ui.qcco.commission.ace.handler;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;
import nc.ui.qcco.commission.refmodel.ProductLastClassRefModel;
import nc.ui.qcco.commission.refmodel.ProductSecondClassRefModel;

public class AceHeadTailBeforeEditHandler implements
		IAppEventHandler<CardHeadTailBeforeEditEvent> {

	@Override
	public void handleAppEvent(CardHeadTailBeforeEditEvent e) {
		if ("pk_subcategory".equals(e.getKey())) {
			BillItem billItem = (BillItem) e.getSource();
			((ProductSecondClassRefModel) ((UIRefPane) billItem.getComponent())
					.getRefModel()).setPk_first_type(((UIRefPane) e
					.getBillCardPanel().getHeadItem("pk_maincategory")
					.getComponent()).getRefPK());
		} else if ("pk_lastcategory".equals(e.getKey())) {
			BillItem billItem = (BillItem) e.getSource();
			((ProductLastClassRefModel) ((UIRefPane) billItem.getComponent())
					.getRefModel()).setPk_second_type(((UIRefPane) e
					.getBillCardPanel().getHeadItem("pk_subcategory")
					.getComponent()).getRefPK());
		} else if ("pk_dept".equals(e.getKey())) {
			BillItem billItem = (BillItem) e.getSource();
			((UIRefPane) billItem.getComponent()).getRefModel().setPk_org(
					((UIRefPane) e.getBillCardPanel().getHeadItem("pk_owner")
							.getComponent()).getRefPK());
		} else if ("cuserid".equals(e.getKey())) {
			BillItem billItem = (BillItem) e.getSource();
			((UIRefPane) billItem.getComponent()).getRefModel().setPk_org(
					e.getContext().getPk_org());
		}else if("pk_commissiontype".equals(e.getKey())){
			BillItem billItem = (BillItem) e.getSource();
			String typeName = ((UIRefPane) billItem.getComponent()).getRefModel().getRefNameValue();
			if(typeName != null){
				String[] templates = CommissionShowTemplate.getTemplateByName(typeName);
				String[] allTemplateFields = CommissionShowTemplate.getTemplateWithAllField();
				//先把模板字段设为null
				for(String temp : allTemplateFields){
					e.getBillCardPanel().getHeadItem(temp).setValue(null);
				}
				e.getBillCardPanel().hideHeadItem(allTemplateFields);
				if(templates == null){
					templates = allTemplateFields;
				}
				e.getBillCardPanel().showHeadItem(templates);
				
			}
		}

		e.setReturnValue(true);
	}
}
