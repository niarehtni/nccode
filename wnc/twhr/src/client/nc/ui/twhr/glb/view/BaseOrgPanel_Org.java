package nc.ui.twhr.glb.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.funcnode.ui.AbstractFunclet;
import nc.funcnode.ui.FuncletContext;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ArrayProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefValueObject;
import nc.ui.org.ref.OrgVOsDefaultRefModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pubapp.uif2app.PubExceptionHanler;
import nc.ui.pubapp.uif2app.event.EventCurrentThread;
import nc.ui.pubapp.uif2app.event.OrgChangedEvent;
import nc.ui.pubapp.uif2app.view.BaseOrgPanel;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.IDisplayable;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.orgmodel.OrgTypeVO;
import nc.vo.org.util.OrgRefPubUtil;
import nc.vo.org.util.OrgTypeManager;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.funcreg.FuncRegisterVO;
import nc.vo.uap.rbac.FuncSubInfo;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

public class BaseOrgPanel_Org extends BaseOrgPanel {
	private static final long serialVersionUID = 2710147950980778179L;
	UIRefPane refPane;
	private transient IExceptionHandler exceptionHandler;
	private UILabel label;
	private String labelName;
	private AbstractUIAppModel model;
	private boolean onlyLeafCanSelected;
	public static final int DEFAULTORG_NULL = 0;
	public static final int DEFAULTORG_SELF = 1;
	public static final int DEFAULTORG_INDIVIDUATION = 2;

	public BaseOrgPanel_Org() {
	}

	private int defaultOrgType = 2;

	public int getDefaultOrgType() {
		return this.defaultOrgType;
	}

	public void setDefaultOrgType(int type) {
		this.defaultOrgType = type;
	}

	public String getLabelName() {
		if (this.labelName == null) {
			this.labelName = getShowName();
		}
		return this.labelName;
	}

	public AbstractUIAppModel getModel() {
		return this.model;
	}

	public void initUI() {
		setLayout(new FlowLayout(0));
		add(getLabel());
		add(getRefPane());

		setAlt_O();
	}

	public boolean isComponentDisplayable() {
		return getModel().getContext().getNodeType() == NODE_TYPE.ORG_NODE;
	}

	public boolean isEnabled() {
		return getRefPane().isEnabled();
	}

	public boolean isOnlyLeafCanSelected() {
		return this.onlyLeafCanSelected;
	}

	public void requestFocus() {
		getRefPane().requestFocus();
	}

	public void setEnabled(boolean enabled) {
		getRefPane().setEnabled(enabled);
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
		if (this.label != null) {
			this.label.setText(labelName);

			setAlt_O();
		}
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
	}

	public void setOnlyLeafCanSelected(boolean onlyLeafCanSelected) {
		this.onlyLeafCanSelected = onlyLeafCanSelected;
	}

	public void setPkOrg(String newPkOrg) {
		String oldPk = getRefPane().getRefPK();

		if (newPkOrg == null) {
			if (oldPk != null) {
				getRefPane().setPK(newPkOrg);
			}
		} else if (!newPkOrg.equals(oldPk)) {
			getRefPane().setPK(newPkOrg);
		}
		String newNewPkOrg = getRefPane().getRefPK();
		getModel().getContext().setPk_org(newNewPkOrg);

		if (isChanged(oldPk, newNewPkOrg)) {

			fireChangedEvent(oldPk, newNewPkOrg);
		}
	}

	public void setRefPane(UIRefPane newRefPane) {
		this.refPane = newRefPane;
		initRefPane();
	}

	@Deprecated
	public void setType(String type) {
	}

	public void stopEditing() {
		this.refPane.stopEditing();
	}

	protected UILabel getLabel() {
		if (this.label == null) {
			this.label = new UILabel();
			this.label.setText(getLabelName());
		}
		return this.label;
	}

	public String[] getNeedShowOrgPks() {
		LoginContext context = getModel().getContext();
		// String[] orgPKs = context.getFuncInfo().getFuncPermissionPkorgs();

		List<String> arraylist = null;
		try {
			arraylist = new ArrayList<String>();
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance()
					.lookup(IUAPQueryBS.class.getName());
			// 所有法人pk
			List<Map<String, String>> custlist = (List<Map<String, String>>) iUAPQueryBS
					.executeQuery("select pk_corp from org_corp",
							new MapListProcessor());
			for (Map<String, String> map : custlist) {
				arraylist.add(map.get("pk_corp"));
			}

		} catch (BusinessException e) {
			e.printStackTrace();
		}
		String[] orgPKs = new String[arraylist.size()];
		arraylist.toArray(orgPKs);
		return orgPKs == null ? new String[0] : orgPKs;

	}

	public UIRefPane getRefPane() {
		if (this.refPane == null) {
			this.refPane = new UIRefPane();
			this.refPane.setRefNodeName(getNodeName());
			initRefPane();
		}
		return this.refPane;
	}

	void exceptionProcess(Exception ex) {

		if (EventCurrentThread.isEmpty()) {
			getExceptionHandler().handlerExeption(ex);
		} else {
			ExceptionUtils.wrappException(ex);
		}
	}

	void fireChangedEvent(String oldPkOrg, String newPkOrg) {
		getModel().getContext().setPk_org(newPkOrg);
		OrgChangedEvent orgChangedEvent = new OrgChangedEvent(oldPkOrg,
				newPkOrg);
		getModel().fireEvent(orgChangedEvent);
	}

	boolean isChanged(String oldPkOrg, String newPkOrg) {
		return !StringUtils.equals(oldPkOrg, newPkOrg);
	}

	private IExceptionHandler getExceptionHandler() {
		if (this.exceptionHandler == null) {
			this.exceptionHandler = new PubExceptionHanler(
					this.model.getContext(), false);
		}
		return this.exceptionHandler;
	}

	private String getNodeName() {
		String pk_orgtype = getOrgTypePk();
		// String name =
		// OrgTypeManager.getInstance().getMainOrgTypeDefaultRefNodeNameByID(pk_orgtype);
		String name = "法人组织";

		if ((null == name) && (!"GROUPORGTYPE00000000".equals(pk_orgtype))) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("pubapp_0", "0pubapp-0163"));
		}
		return name;
	}

	private String getShowName() {
		String pk_orgtype = getOrgTypePk();

		// String showName =
		// OrgRefPubUtil.getMainOrgTypeDefaultRefShowNameByID(pk_orgtype);
		String showName = "法人组织";
		if ((null == showName) && (!"GROUPORGTYPE00000000".equals(pk_orgtype))) {
			ExceptionUtils.wrappBusinessException(NCLangRes4VoTransl
					.getNCLangRes().getStrByID("pubapp_0", "0pubapp-0163"));
		}
		return showName;
	}

	private String getOrgTypePk() {
		AbstractFunclet abstractFunclet = (AbstractFunclet) this.model
				.getContext().getEntranceUI();

		String orgTypeCode = abstractFunclet.getFuncletContext()
				.getFuncRegisterVO().getOrgtypecode();

		OrgTypeVO orgTypeVO = OrgTypeManager.getInstance().getOrgTypeByID(
				orgTypeCode);

		return orgTypeVO.getPk_orgtype();
	}

	private void initRefPane() {
		this.refPane.setPreferredSize(new Dimension(200, 20));
		if (this.refPane.getRefModel() == null) {

			LoginContext context = getModel().getContext();
			String[] pkorgs = context.getFuncInfo().getFuncPermissionPkorgs();
			this.refPane.setRefModel(new OrgVOsDefaultRefModel(pkorgs));
		} else {
			this.refPane.getRefModel().setFilterPks(getNeedShowOrgPks(), 0);

			String filterWhere = " enablestate =2 ";
			this.refPane.getRefModel().setWherePart(filterWhere);
			this.refPane.getRefModel().setSealedDataShow(false);
		}

		this.refPane.setButtonFireEvent(true);
		this.refPane.setNotLeafSelectedEnabled(!this.onlyLeafCanSelected);

		final UITextField uiTf = this.refPane.getUITextField();
		uiTf.setShowMustInputHint(uiTf.isEnabled());
		uiTf.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("enabled".equals(evt.getPropertyName())) {
					Boolean isenable = (Boolean) evt.getNewValue();
					uiTf.setShowMustInputHint(isenable.booleanValue());
				}

			}
		});
		this.refPane.addValueChangedListener(new ValueChangedListener() {
			public void valueChanged(ValueChangedEvent event) {
				try {
					EventCurrentThread.start();

					String[] newPks = null;
					if ((event.getNewValue() instanceof RefValueObject)) {
						newPks = ((RefValueObject) event.getNewValue())
								.getPks();

					} else if ((event.getNewValue() instanceof String[])) {
						newPks = (String[]) event.getNewValue();
					} else {
						newPks = new String[] { (String) event.getNewValue() };
					}

					String newPk = null;
					if ((newPks != null) && (newPks.length > 0)) {
						newPk = newPks[0];
					}

					String oldPk = BaseOrgPanel_Org.this.getModel()
							.getContext().getPk_org();
					if (BaseOrgPanel_Org.this.isChanged(oldPk, newPk)) {
						if (BaseOrgPanel_Org.this.getModel().getUiState() == UIState.NOT_EDIT) {
							BaseOrgPanel_Org.this
									.fireChangedEvent(oldPk, newPk);

						} else if (StringUtils.isEmpty(oldPk)) {
							BaseOrgPanel_Org.this
									.fireChangedEvent(oldPk, newPk);
						} else {
							int dialogReturn = MessageDialog.showYesNoDlg(
									BaseOrgPanel_Org.this.refPane,
									NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("pubapp_0",
													"0pubapp-0164"),
									NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("pubapp_0",
													"0pubapp-0165"), 8);

							if (4 == dialogReturn) {
								BaseOrgPanel_Org.this.fireChangedEvent(oldPk,
										newPk);
							} else {
								BaseOrgPanel_Org.this.getRefPane().setPK(oldPk);
								BaseOrgPanel_Org.this.getModel().getContext()
										.setPk_org(oldPk);
							}
						}
					}

					EventCurrentThread.end();
				} catch (Exception ex) {
					BaseOrgPanel_Org.this.exceptionProcess(ex);
				}
			}
		});
	}

	private void setAlt_O() {
		getLabel().setLabelFor(getRefPane().getUITextField());
		getLabel().setDisplayedMnemonic('O');
		if ((getLabel().getText() != null)
				&& (getLabel().getText().indexOf("O") == -1)) {
			getLabel().setText(getLabel().getText() + "(O)");
		}
	}
}
