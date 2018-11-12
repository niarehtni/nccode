package nc.ui.twhr.twhr.ace.view;

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
import nc.ui.pubapp.uif2app.model.BatchModelDataManager;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.bd.pub.NODE_TYPE;

import org.apache.commons.lang.StringUtils;

public class AccountOrgHeadPanel extends PrimaryOrgPanel implements ITabbedPaneAwareComponentListener {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -4456749298726518172L;
	public static String PK_ORG;
	public static String waperiod;

	private UILabel orgLabel = null;
	private UILabel waPeriodLabel = null;
	private UILabel periodPlanLabel = null;

	public static String getWaperiod() {
		return waperiod;
	}

	public static void setWaperiod(String waperiod) {

		AccountOrgHeadPanel.waperiod = waperiod;
	}

	private UIRefPane waPeriodRefPane;
	private UIRefPane periodPlanRefPane;
	private TWHRPeriodRefModel periodRefModel = null;
	private BatchModelDataManager batchDataManager;
	private BatchModelDataManager batchModelModelDataManagerAna;

	public BatchModelDataManager getBatchModelModelDataManagerAna() {
		return batchModelModelDataManagerAna;
	}

	public void setBatchModelModelDataManagerAna(BatchModelDataManager batchModelModelDataManagerAna) {
		this.batchModelModelDataManagerAna = batchModelModelDataManagerAna;
	}

	public BatchModelDataManager getBatchDataManager() {
		return batchDataManager;
	}

	public void setBatchDataManager(BatchModelDataManager dataManager) {
		this.batchDataManager = dataManager;
	}

	protected boolean isShowOrgRef = true;

	protected boolean isShow = true;

	public boolean isShowOrgRef() {
		return isShowOrgRef;
	}

	public void setShowOrgRef(boolean isShowOrgRef) {
		this.isShowOrgRef = isShowOrgRef;
	}

	public AccountOrgHeadPanel() {
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		try {
			valueChanged2(event);
			// getDataManager().initModelBySqlWhere("1=2");
		} catch (Exception e1) {
			Logger.error(e1);
		}
	}

	public void valueChanged2(ValueChangedEvent event) throws Exception {
		if (event.getSource() == getRefPane()) {
			String newPk_org = getRefPane().getRefPK();
			if (StringUtils.isEmpty(newPk_org)) {
				// getPeriodPlanRefPane().getRefModel().clearData();
				getWaPeriodRefPane().getRefModel().clearData();
			}
			PrimaryOrgPanel.hashDefaultOrg.put(getModuleCode(), newPk_org);
			// getPeriodPlanRefPane().setPk_org(newPk_org);
			getWaPeriodRefPane().setPk_org(newPk_org);

			// afterSetPkOrg(newPk_org);
		}

		if (getRefPane().getRefPK() != null && this.getWaPeriodRefPane().getRefModel().getRefNameValue() != null) {
			waperiod = this.getWaPeriodRefPane().getRefModel().getRefNameValue();
			this.getBatchDataManager().initModelBySqlWhere(
					" and pk_org='" + getRefPane().getRefPK() + "' and pk_period='"
							+ this.getWaPeriodRefPane().getRefModel().getRefNameValue() + "' and dr=0 ");

			this.getBatchModelModelDataManagerAna().initModelBySqlWhere(
					" and pk_org='" + getRefPane().getRefPK() + "' and pk_period='"
							+ this.getWaPeriodRefPane().getRefModel().getRefNameValue() + "' and dr=0 ");
		} else {
			// this.getDataManager().initModel();
			this.getBatchDataManager().initModel();
		}

		this.getModel().setUiState(UIState.NOT_EDIT);
	}

	//
	// public void afterSetPkOrg(String StrPk_org) throws BusinessException {
	// getContext().setPk_org(StrPk_org);
	// ((TWHRPeriodRefModel) getWaPeriodRefPane().getRefModel())
	// .setPk_periodscheme("1001A110000000001Q0N");
	// }

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
	public void initRefCondition() {
		getRefModel().setPk_group(getModel().getContext().getPk_group());
		getWaPeriodRefPane().setPk_org(getModel().getContext().getPk_org());
	}

	@Override
	public void initUI() {
		isShowOrgRef = isOrgNode();
		addComponent();
		// try {
		if (isOrgNode()) {
			String strPk_org = PrimaryOrgPanel.hashDefaultOrg.get(getModuleCode());

			if (strPk_org != null) {
				getRefPane().setPK(strPk_org);
				getModel().getContext().setPk_org(strPk_org);
				// afterSetPkOrg(strPk_org);
			}
		}
		// } catch (BusinessException e) {
		// Logger.error(e);
		// }
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
		// getPeriodPlanRefPane().addValueChangedListener(this);
	}

	public void addComponent() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(getOrgLabel());
		this.add(getRefPane());
		// this.add(getPeriodPlanLabel());
		// this.add(getPeriodPlanRefPane());
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
			orgLabel.setVisible(isOrgNode());
			orgLabel.setText(ResHelper.getString("6013commonbasic", "06013commonbasic0013")/*
																							 * @
																							 * res
																							 * "人力资源组织"
																							 */);
			orgLabel.setSize(new Dimension(200, 22));
			// orgLabel.setBounds(10, 5, 100, 22);
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
		tempRef.setVisible(true);
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

	// public UIRefPane getPeriodPlanRefPane() {
	// if (periodPlanRefPane == null) {
	// periodPlanRefPane = new UIRefPane();
	// periodPlanRefPane.setVisible(isShow);
	// periodPlanRefPane.setPreferredSize(new Dimension(200, 20));
	// periodPlanRefPane.setButtonFireEvent(true);
	// TWHRPeriodRefModel refmodel = new TWHRPeriodRefModel();
	// periodPlanRefPane.setRefModel(refmodel);
	// periodPlanRefPane.setNotLeafSelectedEnabled(false);
	// }
	// return periodPlanRefPane;
	// }

}