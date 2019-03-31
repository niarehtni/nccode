package nc.ui.qcco.commission.ace.handler;

import java.text.SimpleDateFormat;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent;
import nc.vo.pub.lang.UFDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class AceHeadTailAfterEditHandler implements
		IAppEventHandler<CardHeadTailAfterEditEvent> {

	@Override
	public void handleAppEvent(CardHeadTailAfterEditEvent e) {
		if ("pk_maincategory".equals(e.getKey())) {
			((UIRefPane) e.getBillCardPanel().getHeadItem("pk_subcategory")
					.getComponent()).setPK(null);
			((UIRefPane) e.getBillCardPanel().getHeadItem("pk_lastcategory")
					.getComponent()).setPK(null);
		} else if ("pk_subcategory".equals(e.getKey())) {
			((UIRefPane) e.getBillCardPanel().getHeadItem("pk_lastcategory")
					.getComponent()).setPK(null);
		} else if ("pk_owner".equals(e.getKey())) {
			((UIRefPane) e.getBillCardPanel().getHeadItem("pk_dept")
					.getComponent()).setPK(null);
		} else if ("codeprefix".equals(e.getKey())) {
			if (e.getValue() != null) {
				try {
					UIRefPane pane = (UIRefPane) e.getSource();
					String name = pane.getRefModel().getRefNameValue();
					SimpleDateFormat dt = new SimpleDateFormat("yyMMdd");
					String seed = name + "-" + dt.format(new UFDate().toDate());
					IUAPQueryBS query = NCLocator.getInstance().lookup(
							IUAPQueryBS.class);
					String maxcode = (String) query.executeQuery(
							"select max(billno) from QC_COMMISSION_H where billno like '"
									+ seed + "%'", new ColumnProcessor());
					maxcode = maxcode == null || maxcode.equals("") ? "0000"
							: maxcode.substring(maxcode.length() - 4);
					e.getBillCardPanel()
							.getHeadItem("billno")
							.setValue(
									seed
											+ "-"
											+ String.format(
													"%04d",
													Integer.valueOf(maxcode) + 1));
				} catch (Exception ex) {
					ExceptionUtils.wrappBusinessException(ex.getMessage());
				}
			} else {
				e.getBillCardPanel().getHeadItem("billno").setValue("");
			}
		}else if("pk_commissiontype".equals(e.getKey())){
			UIRefPane billItem = (UIRefPane) e.getSource();
			String typeName = billItem.getRefModel().getRefNameValue();
			if(typeName != null){
				String[] templates = CommissionShowTemplate.getTemplateByName(typeName);
				String[] allTemplateFields = CommissionShowTemplate.getTemplateWithAllField();
				//先把模板字段设为灰
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
	}

}
