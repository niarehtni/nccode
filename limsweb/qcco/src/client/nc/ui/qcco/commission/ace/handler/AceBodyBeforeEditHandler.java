package nc.ui.qcco.commission.ace.handler;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.ui.qcco.commission.refmodel.EntTypeRefModel;
import nc.ui.qcco.commission.refmodel.SampleInfoRefModel;
import nc.ui.qcco.commission.refmodel.TestInitRefModel;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class AceBodyBeforeEditHandler implements IAppEventHandler<CardBodyBeforeEditEvent> {

	@Override
	public void handleAppEvent(CardBodyBeforeEditEvent e) {
		if ("productserial".equals(e.getKey())) {
			BillItem bitem = (BillItem) e.getSource();

			String code = ((UIRefPane) ((BillItem) e.getBillCardPanel().getHeadItem("pk_maincategory")).getComponent())
					.getRefCode();
			((SampleInfoRefModel) ((UIRefPane) bitem.getComponent()).getRefModel()).setFirstClassCode(code);
			code = ((UIRefPane) ((BillItem) e.getBillCardPanel().getHeadItem("pk_subcategory")).getComponent())
					.getRefCode();
			((SampleInfoRefModel) ((UIRefPane) bitem.getComponent()).getRefModel()).setSecondClassCode(code);
			code = ((UIRefPane) ((BillItem) e.getBillCardPanel().getHeadItem("pk_lastcategory")).getComponent())
					.getRefCode();
			((SampleInfoRefModel) ((UIRefPane) bitem.getComponent()).getRefModel()).setThirdClassCode(code);
		} else if ("pk_analysisref".equals(e.getKey())) {
			String entStandard = (String) e.getBillCardPanel().getBodyValueAt(e.getRow(), "enterprisestandard");
			String productSpec = (String) e.getBillCardPanel().getBodyValueAt(e.getRow(), "productspec");
			String productGrade = (String) e.getBillCardPanel().getBodyValueAt(e.getRow(), "structuretype");
			String productStage = (String) e.getBillCardPanel().getBodyValueAt(e.getRow(), "productstage");

			BillItem bitem = (BillItem) e.getSource();
			((TestInitRefModel) ((UIRefPane) bitem.getComponent()).getRefModel()).setEnterpriseStandard(entStandard);
			((TestInitRefModel) ((UIRefPane) bitem.getComponent()).getRefModel()).setProductSpec(productSpec);
			((TestInitRefModel) ((UIRefPane) bitem.getComponent()).getRefModel()).setProductGrade(productGrade);
			((TestInitRefModel) ((UIRefPane) bitem.getComponent()).getRefModel()).setProductStage(productStage);
		} else if ("enterprisestandard".equals(e.getKey()) || "productspec".equals(e.getKey())
				|| "structuretype".equals(e.getKey()) || "contacttype".equals(e.getKey())
				|| "productstage".equals(e.getKey())) {
			String entStandard = (String) e.getBillCardPanel().getBodyValueAt(e.getRow(), "pk_" + e.getKey());
			e.getBillCardPanel().setBodyValueAt(entStandard, e.getRow(), e.getKey());
		} else if ("pk_contacttype".equals(e.getKey())) {
			String productserial = (String) e.getBillCardPanel().getBodyValueAt(e.getRow(), "pk_productserial");
			IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			String contactName = "";
			try {
				contactName = (String) query
						.executeQuery(
								"select nc_basprodcontact_name from nc_basprod_contact where pk_basprod_contact = (select pk_basprod_contact from nc_sample_info where pk_sample_info = '"
										+ productserial + "')", new ColumnProcessor());
			} catch (BusinessException ex) {
				Logger.error(ex.getMessage());
			}
			BillItem bitem = (BillItem) e.getSource();
			if (!StringUtils.isEmpty(contactName)) {
				for (String contactType : contactName.split(",")) {
					((UIComboBox) bitem.getComponent()).addItem(contactType);
				}
			}
		}
		if ("enterprisestandard".equals(e.getKey())) {
			BillItem bitem = (BillItem) e.getSource();
			if (StringUtils.isEmpty(((UIRefPane) bitem.getComponent()).getRefPK())) {
				String productserial = (String) e.getBillCardPanel().getBodyValueAt(e.getRow(), "pk_productserial");
				((EntTypeRefModel) ((UIRefPane) bitem.getComponent()).getRefModel()).setPk_basprod_type(productserial);
			}
		}

		e.setReturnValue(true);
	}
}
