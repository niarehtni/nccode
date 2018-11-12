package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.twhr.INhicalcMaintain;
import nc.ui.pubapp.pub.power.PowerCheckUtils;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.twhr.nhicalc.model.NhicalcAppModel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.pubapp.pub.power.PowerActionEnum;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class EditAction extends nc.ui.pubapp.uif2app.actions.EditAction {
    /**
     * serial version id
     */
    private static final long serialVersionUID = -1199547268191528188L;

    private String billType;
    private String billCodeName;
    private boolean powercheck;
    private NhicalcAppModel model;

    @Autowired
    private NhiOrgHeadPanel orgpanel = null;

    public String getBillType() {
	return this.billType;
    }

    public void setBillType(String billType) {
	this.billType = billType;
    }

    public String getBillCodeName() {
	return billCodeName;
    }

    public void setBillCodeName(String billCodeName) {
	this.billCodeName = billCodeName;
    }

    public void showHintMessage(String msg) {
	ShowStatusBarMsgUtil.showStatusBarMsg(msg, this.getModel().getContext());
    }

    public void doAction(ActionEvent e) throws Exception {
	if (this.isPowercheck()) {
	    IBill bill = (IBill) this.getModel().getSelectedData();
	    PowerCheckUtils.checkHasPermission(new IBill[] { bill }, this.getBillType(),
		    PowerActionEnum.EDIT.getActioncode(), this.getBillCodeName());
	}
	super.doAction(e);
    }

    public boolean isEnabled() {
	INhicalcMaintain nhiSrv = NCLocator.getInstance().lookup(INhicalcMaintain.class);
	boolean isaudit = false;
	try {
	    if (!StringUtils.isEmpty(getOrgpanel().getRefPane().getRefPK())
		    && !StringUtils.isEmpty(this.getOrgpanel().getPeriodRefModel().getRefNameValue())) {
		isaudit = nhiSrv.isAudit(getOrgpanel().getRefPane().getRefPK(), this.getOrgpanel().getPeriodRefModel()
			.getRefNameValue().split("-")[0], this.getOrgpanel().getPeriodRefModel().getRefNameValue()
			.split("-")[1]);
	    }
	} catch (BusinessException e) {
	    ExceptionUtils.wrappBusinessException(e.getMessage());
	}

	return (!isaudit) && this.getModel().getUiState() == UIState.NOT_EDIT;
    }

    @Override
    protected boolean isActionEnable() {
	return isEnabled();
    }

    public NhiOrgHeadPanel getOrgpanel() {
	return orgpanel;
    }

    public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
	this.orgpanel = orgpanel;
    }

    public boolean isPowercheck() {
	return this.powercheck;
    }

    public void setPowercheck(boolean powercheck) {
	this.powercheck = powercheck;
    }

    public NhicalcAppModel getModel() {
	return model;
    }

    public void setModel(NhicalcAppModel model) {
	this.model = model;
    }
}
