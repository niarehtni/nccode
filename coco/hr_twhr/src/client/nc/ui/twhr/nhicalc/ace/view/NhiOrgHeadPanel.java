package nc.ui.twhr.nhicalc.ace.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ref.twhr.refmodel.TWHRPeriodRefModel;
import nc.ui.bd.ref.RefRecentRecordsUtil;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hr.uif2.view.PrimaryOrgPanel;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.ta.pub.model.TAPFAppModel;
import nc.ui.ta.pub.view.TAParamOrgPanel;
import nc.ui.twhr.nhicalc.model.NhicalcAppModelDataManager;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.org.HROrgVO;

import org.apache.commons.lang.StringUtils;

public class NhiOrgHeadPanel extends TAParamOrgPanel implements ITabbedPaneAwareComponentListener {

    /**
     * serial version id
     */
    private static final long serialVersionUID = -4456749298726518172L;
    public static String PK_ORG;
    public static String WAPERIOD = "waperiod";

    private UILabel orgLabel = null;
    private UILabel waPeriodLabel = null;
    private UILabel periodPlanLabel = null;
    private UIRefPane waPeriodRefPane;
    private UIRefPane periodPlanRefPane;
    private TWHRPeriodRefModel periodRefModel = null;
    private NhicalcAppModelDataManager dataManager;

    private TAPFAppModel model;

    public NhicalcAppModelDataManager getDataManager() {
	return dataManager;
    }

    public void setDataManager(NhicalcAppModelDataManager dataManager) {
	this.dataManager = dataManager;
    }

    protected boolean isShowOrgRef = true;

    protected boolean isShow = true;

    public boolean isShowOrgRef() {
	return isShowOrgRef;
    }

    public void setShowOrgRef(boolean isShowOrgRef) {
	this.isShowOrgRef = isShowOrgRef;
    }

    public NhiOrgHeadPanel() {
    }

    @Override
    public void valueChanged(ValueChangedEvent event) {
	try {
	    valueChanged2(event);
	} catch (Exception e1) {
	    Logger.error(e1);
	}
    }

    public void valueChanged2(ValueChangedEvent event) throws Exception {
	if (event.getSource() == getRefPane()) {
	    String newPk_org = getRefPane().getRefPK();
	    if (StringUtils.isEmpty(newPk_org)) {
		getWaPeriodRefPane().getRefModel().clearData();
	    }
	    PrimaryOrgPanel.hashDefaultOrg.put(getModuleCode(), newPk_org);
	    getWaPeriodRefPane().setPk_org(newPk_org);
	}

	if (getRefPane().getRefPK() != null && this.getWaPeriodRefPane().getRefModel().getRefNameValue() != null) {
	    this.getDataManager().initModelBySqlWhere(
		    "  pk_org='" + getRefPane().getRefPK() + "' and cyear='"
			    + this.getWaPeriodRefPane().getRefModel().getRefNameValue().split("-")[0]
			    + "' and cperiod='"
			    + this.getWaPeriodRefPane().getRefModel().getRefNameValue().split("-")[1] + "' and dr=0 ");
	} else {
	    this.getDataManager().initModelBySqlWhere("1=2");
	}

	this.getModel().setUiState(UIState.NOT_EDIT);
    }

    @Override
    public void componentHidden() {
	setEnabled(false);
    }

    @Override
    public void componentShowed() {
	setEnabled(true);
    }

    public TWHRPeriodRefModel getPeriodRefModel() {
	if (periodRefModel == null) {
	    periodRefModel = new TWHRPeriodRefModel();
	    periodRefModel.setMutilLangNameRef(false); // 非多語參照，名字字段不需要做多語處理
	    periodRefModel
		    .setRefNodeName(NCLangRes.getInstance().getStrByID("twhr", "TWHRPeriodRefModel-0001")/* 薪资期间 */);
	}
	return periodRefModel;
    }

    @Override
    public void handleEvent(AppEvent event) {
	if (AppEventConst.UISTATE_CHANGED == event.getType()) {
	    if (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT
		    || getModel().getUiState() == UIState.DISABLE) {
		for (Component comp : this.getComponents()) {
		    comp.setEnabled(false);
		}
	    } else {
		for (Component comp : this.getComponents()) {
		    comp.setEnabled(true);
		}
	    }
	}
    }

    /**
     * 初始化参照过滤条件
     */
    @Override
    protected void initRefCondition() {
	getRefModel().setPk_group(getModel().getContext().getPk_group());
	getWaPeriodRefPane().setPk_org(getModel().getContext().getPk_org());
    }

    @Override
    public void initUI() {
	isShowOrgRef = isOrgNode();
	addComponent();
	if (isOrgNode()) {
	    String strPk_org = PrimaryOrgPanel.hashDefaultOrg.get(getModuleCode());

	    if (strPk_org != null) {
		getRefPane().setPK(strPk_org);
		getModel().getContext().setPk_org(strPk_org);
	    }
	}
	initRefCondition();

	// 取消参照的下拉快捷提示，数据权限有问题
	getRefPane().getUITextField().addFocusListener(new FocusAdapter() {
	    @Override
	    public void focusGained(FocusEvent evt) {
		RefRecentRecordsUtil.clear(getRefModel());
	    }
	});
    }

    /**
	 * 
	 */
    protected void initLisner() {
	getWaPeriodRefPane().addValueChangedListener(this);
    }

    public void addComponent() {
	setLayout(new FlowLayout(FlowLayout.LEFT));
	this.add(getOrgLabel());
	this.add(getRefPane());
	this.add(getWaPeriodLabel());
	this.add(getWaPeriodRefPane());
	BillPanelUtils.setButtonPreferredWidth(this);
	initLisner();
    }

    /**
     * 组织标签
     * 
     * @author yingc on 2009-12-2
     * @return
     */
    protected UILabel getOrgLabel() {
	if (orgLabel == null) {
	    orgLabel = new UILabel();
	    orgLabel.setVisible(isShowOrgRef);
	    orgLabel.setText(ResHelper.getString("6013commonbasic", "06013commonbasic0013")/*
											    * @
											    * res
											    * "人力资源组织"
											    */);
	    orgLabel.setSize(new Dimension(200, 22));
	}
	return orgLabel;
    }

    /**
     * 薪资期间标签
     * 
     * @author yingc on 2009-12-2
     * @return
     */
    protected UILabel getWaPeriodLabel() {
	if (waPeriodLabel == null) {
	    waPeriodLabel = new UILabel();
	    waPeriodLabel.setVisible(isShow);
	    waPeriodLabel.setText(NCLangRes.getInstance().getStrByID("68861705", "TWHRPeriodRefModel-0001")/* 薪资期间 */);
	    waPeriodLabel.setSize(new Dimension(200, 22));
	}
	return waPeriodLabel;
    }

    public UIRefPane getWaPeriodRefPane() {
	if (waPeriodRefPane == null) {
	    waPeriodRefPane = new UIRefPane();
	    waPeriodRefPane.setVisible(isShow);
	    waPeriodRefPane.setPreferredSize(new Dimension(200, 20));
	    waPeriodRefPane.setButtonFireEvent(true);
	    TWHRPeriodRefModel refmodel = getPeriodRefModel();
	    waPeriodRefPane.setRefModel(refmodel);
	    waPeriodRefPane.setNotLeafSelectedEnabled(false);
	}
	return waPeriodRefPane;
    }

    public boolean isOrgNode() {
	return getContext().getNodeType() == NODE_TYPE.ORG_NODE;
    }

    @Override
    public UIRefPane getRefPane() {
	UIRefPane tempRef = super.getRefPane();
	tempRef.getRefModel().setTableName(HROrgVO.getDefaultTableName());
	tempRef.setVisible(isShowOrgRef);
	return tempRef;
    }

    @Override
    public boolean isComponentDisplayable() {
	return true;
    }

    public UILabel getPeriodPlanLabel() {
	if (periodPlanLabel == null) {
	    periodPlanLabel = new UILabel();
	    periodPlanLabel.setVisible(true);
	    periodPlanLabel.setText(NCLangRes.getInstance().getStrByID("68861705", "NhiOrgHeadPanel-0000")/* 期间方案 */);
	    periodPlanLabel.setSize(new Dimension(200, 22));
	}
	return periodPlanLabel;
    }

}