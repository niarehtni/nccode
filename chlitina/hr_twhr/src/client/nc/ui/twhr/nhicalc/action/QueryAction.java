package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.queryarea.QueryArea;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.twhr.nhicalc.model.NhicalcAppModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.IQueryAreaCreator;

public class QueryAction extends NCAction implements IQueryAreaCreator {
    /**
     * serial version id
     */
    private static final long serialVersionUID = 2511667804224556865L;

    private NhicalcAppModel model = null;
    private BillListView editor = null;
    private NhiOrgHeadPanel orgpanel = null;

    public NhicalcAppModel getModel() {
	return model;
    }

    public void setModel(NhicalcAppModel model) {
	this.model = model;
	model.addAppEventListener(this);
    }

    public BillListView getEditor() {
	return editor;
    }

    public void setEditor(BillListView editor) {
	this.editor = editor;
    }

    public boolean isEnabled() {
	// return !(StringUtils
	// .isEmpty(this.getOrgpanel().getRefPane().getRefPK())
	// || StringUtils.isEmpty(this.getOrgpanel()
	// .getPeriodPlanRefPane().getRefPK()) || StringUtils
	// .isEmpty(this.getOrgpanel().getWaPeriodRefPane().getRefPK()));
	return false;
    }

    public QueryAction() {
	setBtnName("查詢");
    }

    @Override
    public void doAction(ActionEvent arg0) throws Exception {
	// TODO 自动生成的方法存根

    }

    public NhiOrgHeadPanel getOrgpanel() {
	return orgpanel;
    }

    public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
	this.orgpanel = orgpanel;
    }

    @Override
    public void preFetchQueryTemplateData() {
	// TODO Auto-generated method stub

    }

    @Override
    public QueryArea createQueryArea() {
	// TODO Auto-generated method stub
	return null;
    }

}
