package nc.ui.twhr.basedoc.handler;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import nc.itf.org.IOrgConst;
import nc.ui.bd.manage.UIRefCellEditorNew;
import nc.ui.bd.ref.RefCall;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent;
import nc.ui.pubapp.uif2app.model.BatchBillTableModel;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.twhr.basedoc.BaseDocVO;

public class BodyBeforeEditHandler implements IAppEventHandler<CardBodyBeforeEditEvent> {
    private BatchBillTableModel billModel = null;

    @Override
    public void handleAppEvent(final CardBodyBeforeEditEvent e) {
	if (e.getKey().equals("numbervalue")) {
	    BillItem type = e.getBillCardPanel().getBodyItem("doctype");
	    if (type == null || type.getValueObject() == null || (Integer) type.getValueObject() == 3
		    || (Integer) type.getValueObject() == 4 || (Integer) type.getValueObject() == 5
		    || (Integer) type.getValueObject() == 6) {
		e.setReturnValue(Boolean.FALSE);
	    }
	} else if (e.getKey().equals("waitemvalue")) {
	    BillItem type = e.getBillCardPanel().getBodyItem("doctype");
	    if (type == null || type.getValueObject() == null || (Integer) type.getValueObject() != 3) {
		e.setReturnValue(Boolean.FALSE);
	    }
	} else if (e.getKey().equals("textvalue")) {
	    // 文本类型 by he
	    BillItem type = e.getBillCardPanel().getBodyItem("doctype");
	    if (type == null || type.getValueObject() == null || (Integer) type.getValueObject() != 4) {
		e.setReturnValue(Boolean.FALSE);
	    }
	    e.getBillCardPanel().getBillTable().getColumnModel().getColumn(6).getCellEditor();
	} else if (e.getKey().equals("reftype")) {
	    // 参照类型 by he
	    BillItem type = e.getBillCardPanel().getBodyItem("doctype");
	    if (type == null || type.getValueObject() == null || (Integer) type.getValueObject() != 5) {
		e.setReturnValue(Boolean.FALSE);
	    }
	} else if (e.getKey().equals("refvalue")) {
	    // 参照值 by he
	    final BillCardPanel card = e.getBillCardPanel();
	    BillItem type = card.getBodyItem("doctype");
	    if (type == null || type.getValueObject() == null || (Integer) type.getValueObject() != 5) {
		e.setReturnValue(Boolean.FALSE);
	    } else {
		if (card.getBodyValueAt(e.getRow(), "reftype") != null) {
		    UIRefPane refpane = RefCall.getUIRefPaneByRefNodeName((String) card.getBodyValueAt(e.getRow(),
			    "reftype"));

		    if (refpane.getRefModel() != null) {
			if (NODE_TYPE.ORG_NODE.equals(e.getContext().getNodeType())) {
			    refpane.setPk_org(e.getContext().getPk_org());
			} else {
			    refpane.setPk_org(IOrgConst.GLOBEORG);
			}
		    }

		    final UIRefCellEditorNew editor = new UIRefCellEditorNew(refpane);
		    for (int i = 0; i < card.getBillTable().getColumnCount(); i++) {
			if (card.getBodyItem("refvalue").getCaptionLabel().getText()
				.equals(card.getBillTable().getColumnModel().getColumn(i).getHeaderValue())) {
			    card.getBillTable().getColumnModel().getColumn(i).setCellEditor(editor);
			}
		    }

		    editor.addCellEditorListener(new CellEditorListener() {

			@Override
			public void editingStopped(ChangeEvent ex) {
			    setLineValue(e.getRow(), ((UIRefPane) editor.getComponent()).getRefPK());
			    card.getBillModel().setValueAt(editor.getCellEditorValue(), e.getRow(), "refvalue");
			}

			@Override
			public void editingCanceled(ChangeEvent e) {
			}
		    });

		    e.setReturnValue(Boolean.TRUE);
		}
	    }
	} else if (e.getKey().equals("logicvalue")) {
	    // 逻辑值 by he
	    BillItem type = e.getBillCardPanel().getBodyItem("doctype");
	    if (type == null || type.getValueObject() == null || (Integer) type.getValueObject() != 6) {
		e.setReturnValue(Boolean.FALSE);
	    }
	}

	e.setReturnValue(Boolean.TRUE);
    }

    public void setLineValue(int row, Object value) {
	BaseDocVO line = (BaseDocVO) getBillModel().getRow(row);
	line.setRefvalue((String) value);
	this.getBillModel().updateLine(row, line);
    }

    public BatchBillTableModel getBillModel() {
	return billModel;
    }

    public void setBillModel(BatchBillTableModel billModel) {
	this.billModel = billModel;
    }

}
