package nc.ui.twhr.basedoc.model;

import java.util.Vector;

import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pubapp.uif2app.model.BatchBillTableModel;
import nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.twhr.basedoc.DocTypeEnum;

public class BaseDocBillTableModel extends BatchBillTableModel {
    private ShowUpableBatchBillTable editor = null;

    public void initModel(Object data) {
	super.initModel(data);
	BaseDocVO[] vos = (BaseDocVO[]) data;
	for (BaseDocVO vo : vos) {
	    if (DocTypeEnum.REF.toIntValue() == vo.getDoctype()) {
		for (int i = 0; i < editor.getBillCardPanel().getBillModel().getRowCount(); i++) {
		    if (vo.getId().equals(editor.getBillCardPanel().getBodyValueAt(i, "id"))) {
			if (vo.getReftype() != null && vo.getReftype().equals("ÈÕÀú")) {
			    IConstEnum val = new DefaultConstEnum(vo.getRefvalue(), vo.getRefvalue());
			    editor.getBillCardPanel().getBillModel().setValueAt(val, i, "refvalue");
			} else {
				if(vo.getReftype()!=null){
					UIRefPane pane = new UIRefPane(vo.getReftype());
					pane.setPk_org(vo.getPk_org());
					pane.setValueObj(vo.getRefvalue());
					Vector refvls = pane.getRefModel().matchData(pane.getRefModel().getPkFieldCode(),
							vo.getRefvalue());
					if(null != refvls){
						IConstEnum val = new DefaultConstEnum(((Vector) refvls.get(0)).get(0),
								(String) ((Vector) refvls.get(0)).get(1));
						editor.getBillCardPanel().getBillModel().setValueAt(val, i, "refvalue");
					}
				}
			}
		    }
		}
	    }
	}
    }

    public ShowUpableBatchBillTable getEditor() {
	return editor;
    }

    public void setEditor(ShowUpableBatchBillTable editor) {
	this.editor = editor;
    }

}
