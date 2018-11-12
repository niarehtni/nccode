package nc.ui.wa.payfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class payfile extends AbstractJavaBeanDefinition {
	private Map<String, Object> context = new HashMap();

	public nc.vo.wa.pub.WaLoginContext getContext() {
		if (context.get("context") != null)
			return (nc.vo.wa.pub.WaLoginContext) context.get("context");
		nc.vo.wa.pub.WaLoginContext bean = new nc.vo.wa.pub.WaLoginContext();
		context.put("context", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.model.PayfileModelService getManageModelService() {
		if (context.get("ManageModelService") != null)
			return (nc.ui.wa.payfile.model.PayfileModelService) context
					.get("ManageModelService");
		nc.ui.wa.payfile.model.PayfileModelService bean = new nc.ui.wa.payfile.model.PayfileModelService();
		context.put("ManageModelService", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.vo.bd.meta.BDObjectAdpaterFactory getBoadatorfactory() {
		if (context.get("boadatorfactory") != null)
			return (nc.vo.bd.meta.BDObjectAdpaterFactory) context
					.get("boadatorfactory");
		nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
		context.put("boadatorfactory", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.model.PayfileAppModel getManageAppModel() {
		if (context.get("ManageAppModel") != null)
			return (nc.ui.wa.payfile.model.PayfileAppModel) context
					.get("ManageAppModel");
		nc.ui.wa.payfile.model.PayfileAppModel bean = new nc.ui.wa.payfile.model.PayfileAppModel();
		context.put("ManageAppModel", bean);
		bean.setService(getManageModelService());
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors() {
		if (context.get("toftpanelActionContributors") != null)
			return (nc.ui.uif2.actions.ActionContributors) context
					.get("toftpanelActionContributors");
		nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
		context.put("toftpanelActionContributors", bean);
		bean.setContributors(getManagedList0());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList0() {
		List list = new ArrayList();
		list.add(getListViewActions());
		list.add(getCardEditorActions());
		return list;
	}

	public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getCardEditorActions() {
		if (context.get("cardEditorActions") != null)
			return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context
					.get("cardEditorActions");
		nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(
				getBillFormEditor());
		context.put("cardEditorActions", bean);
		bean.setActions(getManagedList1());
		bean.setEditActions(getManagedList2());
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList1() {
		List list = new ArrayList();
		list.add(getAddActionGroup());
		list.add(getEditActionGroup());
		list.add(getDeleteActionGroup());
		list.add(getNullaction());
		list.add(getRefreshAction());
		list.add(getNullaction());
		list.add(getCardPrintGroupAction());
		return list;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getSaveAction());
		list.add(getSaveAddAction());
		list.add(getNullaction());
		list.add(getCancelAction());
		return list;
	}

	public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getListViewActions() {
		if (context.get("listViewActions") != null)
			return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context
					.get("listViewActions");
		nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(
				getListView());
		context.put("listViewActions", bean);
		bean.setActions(getManagedList3());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList3() {
		List list = new ArrayList();
		list.add(getAddActionGroup());
		list.add(getEditActionGroup());
		list.add(getDeleteActionGroup());
		list.add(getCopyAction());
		list.add(getNullaction());
		list.add(getQueryAction());
		list.add(getRefreshAction());
		list.add(getNullaction());
		list.add(getTransferAction());
		list.add(getAlterAction());
		list.add(getAccountManageAction());
		list.add(getNullaction());
		list.add(getSortAction());
		list.add(getDisplayAction());
		list.add(getNullaction());
		list.add(getPrintGroupAction());
		return list;
	}

	public nc.ui.uif2.editor.TemplateContainer getTemplateContainer() {
		if (context.get("templateContainer") != null)
			return (nc.ui.uif2.editor.TemplateContainer) context
					.get("templateContainer");
		nc.ui.uif2.editor.TemplateContainer bean = new nc.ui.uif2.editor.TemplateContainer();
		context.put("templateContainer", bean);
		bean.setContext(getContext());
		bean.setNodeKeies(getManagedList4());
		bean.load();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList4() {
		List list = new ArrayList();
		list.add("payfile");
		list.add("payfilec");
		return list;
	}

	public nc.ui.wa.payfile.model.PayfileModelDataManager getModelDataManager() {
		if (context.get("modelDataManager") != null)
			return (nc.ui.wa.payfile.model.PayfileModelDataManager) context
					.get("modelDataManager");
		nc.ui.wa.payfile.model.PayfileModelDataManager bean = new nc.ui.wa.payfile.model.PayfileModelDataManager();
		context.put("modelDataManager", bean);
		bean.setService(getManageModelService());
		bean.setContext(getContext());
		bean.setModel(getManageAppModel());
		bean.setPaginationModel(getPaginationModel());
		bean.setPaginationBar(getPaginationBar());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.view.PayfileListView getListView() {
		if (context.get("listView") != null)
			return (nc.ui.wa.payfile.view.PayfileListView) context
					.get("listView");
		nc.ui.wa.payfile.view.PayfileListView bean = new nc.ui.wa.payfile.view.PayfileListView();
		context.put("listView", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		bean.setMultiSelectionEnable(false);
		bean.setPos("head");
		bean.setTemplateContainer(getTemplateContainer());
		bean.setNodekey("payfile");
		bean.setSouth(getPaginationBar());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.components.pagination.PaginationBar getPaginationBar() {
		if (context.get("paginationBar") != null)
			return (nc.ui.uif2.components.pagination.PaginationBar) context
					.get("paginationBar");
		nc.ui.uif2.components.pagination.PaginationBar bean = new nc.ui.uif2.components.pagination.PaginationBar();
		context.put("paginationBar", bean);
		bean.setPaginationModel(getPaginationModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.components.pagination.PaginationModel getPaginationModel() {
		if (context.get("paginationModel") != null)
			return (nc.ui.uif2.components.pagination.PaginationModel) context
					.get("paginationModel");
		nc.ui.uif2.components.pagination.PaginationModel bean = new nc.ui.uif2.components.pagination.PaginationModel();
		context.put("paginationModel", bean);
		bean.setPaginationQueryService(getManageModelService());
		bean.init();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.view.PayfileFormEditor getBillFormEditor() {
		if (context.get("billFormEditor") != null)
			return (nc.ui.wa.payfile.view.PayfileFormEditor) context
					.get("billFormEditor");
		nc.ui.wa.payfile.view.PayfileFormEditor bean = new nc.ui.wa.payfile.view.PayfileFormEditor();
		context.put("billFormEditor", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		bean.setTemplateContainer(getTemplateContainer());
		bean.setNodekey("payfile");
		bean.setActions(getManagedList5());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList5() {
		List list = new ArrayList();
		list.add(getFirstLineAction());
		list.add(getPreLineAction());
		list.add(getNextLineAction());
		list.add(getLastLineAction());
		return list;
	}

	public nc.funcnode.ui.action.SeparatorAction getNullaction() {
		if (context.get("nullaction") != null)
			return (nc.funcnode.ui.action.SeparatorAction) context
					.get("nullaction");
		nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
		context.put("nullaction", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.GroupAction getEditActionGroup() {
		if (context.get("editActionGroup") != null)
			return (nc.funcnode.ui.action.GroupAction) context
					.get("editActionGroup");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("editActionGroup", bean);
		bean.setActions(getManagedList6());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList6() {
		List list = new ArrayList();
		list.add(getEditAction());
		list.add(getBatchEditAction());
		return list;
	}

	public nc.ui.wa.payfile.action.EditPayfileAction getEditAction() {
		if (context.get("EditAction") != null)
			return (nc.ui.wa.payfile.action.EditPayfileAction) context
					.get("EditAction");
		nc.ui.wa.payfile.action.EditPayfileAction bean = new nc.ui.wa.payfile.action.EditPayfileAction();
		context.put("EditAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.BatchEditPayfileAction getBatchEditAction() {
		if (context.get("BatchEditAction") != null)
			return (nc.ui.wa.payfile.action.BatchEditPayfileAction) context
					.get("BatchEditAction");
		nc.ui.wa.payfile.action.BatchEditPayfileAction bean = new nc.ui.wa.payfile.action.BatchEditPayfileAction();
		context.put("BatchEditAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.GroupAction getAddActionGroup() {
		if (context.get("addActionGroup") != null)
			return (nc.funcnode.ui.action.GroupAction) context
					.get("addActionGroup");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("addActionGroup", bean);
		bean.setActions(getManagedList7());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList7() {
		List list = new ArrayList();
		list.add(getAddAction());
		list.add(getAddPartAction());
		list.add(getAddAlterAction());
		list.add(getAddPoiAction());
		list.add(getBatchAddAction());
		return list;
	}

	public nc.ui.wa.payfile.action.FirstLinePayfileAction getFirstLineAction() {
		if (context.get("FirstLineAction") != null)
			return (nc.ui.wa.payfile.action.FirstLinePayfileAction) context
					.get("FirstLineAction");
		nc.ui.wa.payfile.action.FirstLinePayfileAction bean = new nc.ui.wa.payfile.action.FirstLinePayfileAction();
		context.put("FirstLineAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.PreLinePayfileAction getPreLineAction() {
		if (context.get("PreLineAction") != null)
			return (nc.ui.wa.payfile.action.PreLinePayfileAction) context
					.get("PreLineAction");
		nc.ui.wa.payfile.action.PreLinePayfileAction bean = new nc.ui.wa.payfile.action.PreLinePayfileAction();
		context.put("PreLineAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.NextLinePayfileAction getNextLineAction() {
		if (context.get("NextLineAction") != null)
			return (nc.ui.wa.payfile.action.NextLinePayfileAction) context
					.get("NextLineAction");
		nc.ui.wa.payfile.action.NextLinePayfileAction bean = new nc.ui.wa.payfile.action.NextLinePayfileAction();
		context.put("NextLineAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.LastLinePayfileAction getLastLineAction() {
		if (context.get("LastLineAction") != null)
			return (nc.ui.wa.payfile.action.LastLinePayfileAction) context
					.get("LastLineAction");
		nc.ui.wa.payfile.action.LastLinePayfileAction bean = new nc.ui.wa.payfile.action.LastLinePayfileAction();
		context.put("LastLineAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.AddPayfileAction getAddAction() {
		if (context.get("AddAction") != null)
			return (nc.ui.wa.payfile.action.AddPayfileAction) context
					.get("AddAction");
		nc.ui.wa.payfile.action.AddPayfileAction bean = new nc.ui.wa.payfile.action.AddPayfileAction();
		context.put("AddAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.AddPayfilePartAction getAddPartAction() {
		if (context.get("AddPartAction") != null)
			return (nc.ui.wa.payfile.action.AddPayfilePartAction) context
					.get("AddPartAction");
		nc.ui.wa.payfile.action.AddPayfilePartAction bean = new nc.ui.wa.payfile.action.AddPayfilePartAction();
		context.put("AddPartAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.AddPayfileAlterAction getAddAlterAction() {
		if (context.get("AddAlterAction") != null)
			return (nc.ui.wa.payfile.action.AddPayfileAlterAction) context
					.get("AddAlterAction");
		nc.ui.wa.payfile.action.AddPayfileAlterAction bean = new nc.ui.wa.payfile.action.AddPayfileAlterAction();
		context.put("AddAlterAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.AddPayfilePoiAction getAddPoiAction() {
		if (context.get("AddPoiAction") != null)
			return (nc.ui.wa.payfile.action.AddPayfilePoiAction) context
					.get("AddPoiAction");
		nc.ui.wa.payfile.action.AddPayfilePoiAction bean = new nc.ui.wa.payfile.action.AddPayfilePoiAction();
		context.put("AddPoiAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.BatchAddPayfileAction getBatchAddAction() {
		if (context.get("BatchAddAction") != null)
			return (nc.ui.wa.payfile.action.BatchAddPayfileAction) context
					.get("BatchAddAction");
		nc.ui.wa.payfile.action.BatchAddPayfileAction bean = new nc.ui.wa.payfile.action.BatchAddPayfileAction();
		context.put("BatchAddAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.GroupAction getDeleteActionGroup() {
		if (context.get("deleteActionGroup") != null)
			return (nc.funcnode.ui.action.GroupAction) context
					.get("deleteActionGroup");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("deleteActionGroup", bean);
		bean.setActions(getManagedList8());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList8() {
		List list = new ArrayList();
		list.add(getDeleteAction());
		list.add(getBatchDeleteAction());
		return list;
	}

	public nc.ui.wa.payfile.action.DeletePayfileAction getDeleteAction() {
		if (context.get("DeleteAction") != null)
			return (nc.ui.wa.payfile.action.DeletePayfileAction) context
					.get("DeleteAction");
		nc.ui.wa.payfile.action.DeletePayfileAction bean = new nc.ui.wa.payfile.action.DeletePayfileAction();
		context.put("DeleteAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.BatchDeletePayfileAction getBatchDeleteAction() {
		if (context.get("BatchDeleteAction") != null)
			return (nc.ui.wa.payfile.action.BatchDeletePayfileAction) context
					.get("BatchDeleteAction");
		nc.ui.wa.payfile.action.BatchDeletePayfileAction bean = new nc.ui.wa.payfile.action.BatchDeletePayfileAction();
		context.put("BatchDeleteAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.CopyPayfileAction getCopyAction() {
		if (context.get("CopyAction") != null)
			return (nc.ui.wa.payfile.action.CopyPayfileAction) context
					.get("CopyAction");
		nc.ui.wa.payfile.action.CopyPayfileAction bean = new nc.ui.wa.payfile.action.CopyPayfileAction();
		context.put("CopyAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.SortPayfileAction getSortAction() {
		if (context.get("SortAction") != null)
			return (nc.ui.wa.payfile.action.SortPayfileAction) context
					.get("SortAction");
		nc.ui.wa.payfile.action.SortPayfileAction bean = new nc.ui.wa.payfile.action.SortPayfileAction();
		context.put("SortAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getListView());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.AlterPayfileAction getAlterAction() {
		if (context.get("AlterAction") != null)
			return (nc.ui.wa.payfile.action.AlterPayfileAction) context
					.get("AlterAction");
		nc.ui.wa.payfile.action.AlterPayfileAction bean = new nc.ui.wa.payfile.action.AlterPayfileAction();
		context.put("AlterAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.TransferPayfileAction getTransferAction() {
		if (context.get("TransferAction") != null)
			return (nc.ui.wa.payfile.action.TransferPayfileAction) context
					.get("TransferAction");
		nc.ui.wa.payfile.action.TransferPayfileAction bean = new nc.ui.wa.payfile.action.TransferPayfileAction();
		context.put("TransferAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.QueryPayfileAction getQueryAction() {
		if (context.get("QueryAction") != null)
			return (nc.ui.wa.payfile.action.QueryPayfileAction) context
					.get("QueryAction");
		nc.ui.wa.payfile.action.QueryPayfileAction bean = new nc.ui.wa.payfile.action.QueryPayfileAction();
		context.put("QueryAction", bean);
		bean.setModel(getManageAppModel());
		bean.setOrgpanel(getOrgpanel());
		bean.setDataManager(getModelDataManager());
		bean.setQueryDelegator(getWaPayfileQueryDelegator_aad03c());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.wa.payfile.view.WaPayfileQueryDelegator getWaPayfileQueryDelegator_aad03c() {
		if (context.get("nc.ui.wa.payfile.view.WaPayfileQueryDelegator#aad03c") != null)
			return (nc.ui.wa.payfile.view.WaPayfileQueryDelegator) context
					.get("nc.ui.wa.payfile.view.WaPayfileQueryDelegator#aad03c");
		nc.ui.wa.payfile.view.WaPayfileQueryDelegator bean = new nc.ui.wa.payfile.view.WaPayfileQueryDelegator();
		context.put("nc.ui.wa.payfile.view.WaPayfileQueryDelegator#aad03c",
				bean);
		bean.setNodeKey("payfile");
		bean.setContext(getContext());
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.RefreshPayfileAction getRefreshAction() {
		if (context.get("RefreshAction") != null)
			return (nc.ui.wa.payfile.action.RefreshPayfileAction) context
					.get("RefreshAction");
		nc.ui.wa.payfile.action.RefreshPayfileAction bean = new nc.ui.wa.payfile.action.RefreshPayfileAction();
		context.put("RefreshAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		bean.setOrgpanel(getOrgpanel());
		bean.setFormEditor(getBillFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.SavePayfileAction getSaveAction() {
		if (context.get("SaveAction") != null)
			return (nc.ui.wa.payfile.action.SavePayfileAction) context
					.get("SaveAction");
		nc.ui.wa.payfile.action.SavePayfileAction bean = new nc.ui.wa.payfile.action.SavePayfileAction();
		context.put("SaveAction", bean);
		bean.setModel(getManageAppModel());
		bean.setEditor(getBillFormEditor());
		bean.setValidationService(getBillNotNullValidator());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.SaveAddPayfileAction getSaveAddAction() {
		if (context.get("SaveAddAction") != null)
			return (nc.ui.wa.payfile.action.SaveAddPayfileAction) context
					.get("SaveAddAction");
		nc.ui.wa.payfile.action.SaveAddPayfileAction bean = new nc.ui.wa.payfile.action.SaveAddPayfileAction();
		context.put("SaveAddAction", bean);
		bean.setModel(getManageAppModel());
		bean.setEditor(getBillFormEditor());
		bean.setAddAction(getAddAction());
		bean.setAddPartAction(getAddPartAction());
		bean.setAddAlterAction(getAddAlterAction());
		bean.setAddPoiAction(getAddPoiAction());
		bean.setValidationService(getBillNotNullValidator());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.CancelPayfileAction getCancelAction() {
		if (context.get("CancelAction") != null)
			return (nc.ui.wa.payfile.action.CancelPayfileAction) context
					.get("CancelAction");
		nc.ui.wa.payfile.action.CancelPayfileAction bean = new nc.ui.wa.payfile.action.CancelPayfileAction();
		context.put("CancelAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.AccountManageAction getAccountManageAction() {
		if (context.get("accountManageAction") != null)
			return (nc.ui.wa.payfile.action.AccountManageAction) context
					.get("accountManageAction");
		nc.ui.wa.payfile.action.AccountManageAction bean = new nc.ui.wa.payfile.action.AccountManageAction();
		context.put("accountManageAction", bean);
		bean.setModel(getManageAppModel());
		bean.setOpeningFunCode("10140PSNBA");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.action.DisplayAction getDisplayAction() {
		if (context.get("displayAction") != null)
			return (nc.ui.wa.payfile.action.DisplayAction) context
					.get("displayAction");
		nc.ui.wa.payfile.action.DisplayAction bean = new nc.ui.wa.payfile.action.DisplayAction();
		context.put("displayAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.GroupAction getPrintGroupAction() {
		if (context.get("PrintGroupAction") != null)
			return (nc.funcnode.ui.action.GroupAction) context
					.get("PrintGroupAction");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("PrintGroupAction", bean);
		bean.setCode("print");
		bean.setName(getI18nFB_4d3d8a());
		bean.setActions(getManagedList9());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_4d3d8a() {
		if (context.get("nc.ui.uif2.I18nFB#4d3d8a") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#4d3d8a");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#4d3d8a", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("¥Ú”°");
		bean.setResId("X60130002");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#4d3d8a", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList9() {
		List list = new ArrayList();
		list.add(getPrintAction());
		list.add(getPreviewAction());
		list.add(getOutputAction());
		list.add(getNullaction());
		list.add(getTemplatePrintAction());
		list.add(getTemplatePreviewAction());
		return list;
	}

	public nc.funcnode.ui.action.GroupAction getCardPrintGroupAction() {
		if (context.get("CardPrintGroupAction") != null)
			return (nc.funcnode.ui.action.GroupAction) context
					.get("CardPrintGroupAction");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("CardPrintGroupAction", bean);
		bean.setCode("print");
		bean.setName(getI18nFB_1035628());
		bean.setActions(getManagedList10());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1035628() {
		if (context.get("nc.ui.uif2.I18nFB#1035628") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1035628");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1035628", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("¥Ú”°");
		bean.setResId("X60130002");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1035628", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList10() {
		List list = new ArrayList();
		list.add(getCardTemplatePrintAction());
		list.add(getCardTemplatePreviewAction());
		list.add(getCardTemplateExportAction());
		return list;
	}

	public nc.ui.wa.paydata.action.DirectPrintAction getPreviewAction() {
		if (context.get("PreviewAction") != null)
			return (nc.ui.wa.paydata.action.DirectPrintAction) context
					.get("PreviewAction");
		nc.ui.wa.paydata.action.DirectPrintAction bean = new nc.ui.wa.paydata.action.DirectPrintAction();
		context.put("PreviewAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDirectPrint(false);
		bean.setListView(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.paydata.action.DirectPrintAction getPrintAction() {
		if (context.get("PrintAction") != null)
			return (nc.ui.wa.paydata.action.DirectPrintAction) context
					.get("PrintAction");
		nc.ui.wa.paydata.action.DirectPrintAction bean = new nc.ui.wa.paydata.action.DirectPrintAction();
		context.put("PrintAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDirectPrint(true);
		bean.setListView(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.pub.WaOutputAction getOutputAction() {
		if (context.get("outputAction") != null)
			return (nc.ui.wa.pub.WaOutputAction) context.get("outputAction");
		nc.ui.wa.pub.WaOutputAction bean = new nc.ui.wa.pub.WaOutputAction();
		context.put("outputAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.pub.action.WaTemplatePreviewAction getTemplatePreviewAction() {
		if (context.get("TemplatePreviewAction") != null)
			return (nc.ui.wa.pub.action.WaTemplatePreviewAction) context
					.get("TemplatePreviewAction");
		nc.ui.wa.pub.action.WaTemplatePreviewAction bean = new nc.ui.wa.pub.action.WaTemplatePreviewAction();
		context.put("TemplatePreviewAction", bean);
		bean.setModel(getManageAppModel());
		bean.setNodeKey("payfile");
		bean.setDatasource(getDatasource());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.pub.action.WaTemplatePrintAction getTemplatePrintAction() {
		if (context.get("TemplatePrintAction") != null)
			return (nc.ui.wa.pub.action.WaTemplatePrintAction) context
					.get("TemplatePrintAction");
		nc.ui.wa.pub.action.WaTemplatePrintAction bean = new nc.ui.wa.pub.action.WaTemplatePrintAction();
		context.put("TemplatePrintAction", bean);
		bean.setModel(getManageAppModel());
		bean.setNodeKey("payfile");
		bean.setDatasource(getDatasource());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hrcp.cindex.model.HRListMetaDataDataSource getDatasource() {
		if (context.get("datasource") != null)
			return (nc.ui.hrcp.cindex.model.HRListMetaDataDataSource) context
					.get("datasource");
		nc.ui.hrcp.cindex.model.HRListMetaDataDataSource bean = new nc.ui.hrcp.cindex.model.HRListMetaDataDataSource();
		context.put("datasource", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.model.HRMetaDataDataSource getCarddatasource() {
		if (context.get("carddatasource") != null)
			return (nc.ui.hr.uif2.model.HRMetaDataDataSource) context
					.get("carddatasource");
		nc.ui.hr.uif2.model.HRMetaDataDataSource bean = new nc.ui.hr.uif2.model.HRMetaDataDataSource();
		context.put("carddatasource", bean);
		bean.setModel(getManageAppModel());
		bean.setSingleData(true);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.action.print.TemplatePrintAction getCardTemplatePrintAction() {
		if (context.get("CardTemplatePrintAction") != null)
			return (nc.ui.hr.uif2.action.print.TemplatePrintAction) context
					.get("CardTemplatePrintAction");
		nc.ui.hr.uif2.action.print.TemplatePrintAction bean = new nc.ui.hr.uif2.action.print.TemplatePrintAction();
		context.put("CardTemplatePrintAction", bean);
		bean.setModel(getManageAppModel());
		bean.setNodeKey("payfilec");
		bean.setPrintDlgParentConatiner(getBillFormEditor());
		bean.setDatasource(getCarddatasource());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.action.print.TemplatePreviewAction getCardTemplatePreviewAction() {
		if (context.get("CardTemplatePreviewAction") != null)
			return (nc.ui.hr.uif2.action.print.TemplatePreviewAction) context
					.get("CardTemplatePreviewAction");
		nc.ui.hr.uif2.action.print.TemplatePreviewAction bean = new nc.ui.hr.uif2.action.print.TemplatePreviewAction();
		context.put("CardTemplatePreviewAction", bean);
		bean.setModel(getManageAppModel());
		bean.setNodeKey("payfilec");
		bean.setPrintDlgParentConatiner(getBillFormEditor());
		bean.setDatasource(getCarddatasource());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.action.print.ExportCardAction getCardTemplateExportAction() {
		if (context.get("CardTemplateExportAction") != null)
			return (nc.ui.hr.uif2.action.print.ExportCardAction) context
					.get("CardTemplateExportAction");
		nc.ui.hr.uif2.action.print.ExportCardAction bean = new nc.ui.hr.uif2.action.print.ExportCardAction();
		context.put("CardTemplateExportAction", bean);
		bean.setModel(getManageAppModel());
		bean.setNodeKey("payfilec");
		bean.setPrintDlgParentConatiner(getBillFormEditor());
		bean.setDatasource(getCarddatasource());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.FunNodeClosingHandler getClosingListener() {
		if (context.get("ClosingListener") != null)
			return (nc.ui.uif2.FunNodeClosingHandler) context
					.get("ClosingListener");
		nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
		context.put("ClosingListener", bean);
		bean.setModel(getManageAppModel());
		bean.setSaveaction(getSaveAction());
		bean.setCancelaction(getCancelAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.validator.BillNotNullValidateService getBillNotNullValidator() {
		if (context.get("billNotNullValidator") != null)
			return (nc.ui.hr.uif2.validator.BillNotNullValidateService) context
					.get("billNotNullValidator");
		nc.ui.hr.uif2.validator.BillNotNullValidateService bean = new nc.ui.hr.uif2.validator.BillNotNullValidateService(
				getBillFormEditor());
		context.put("billNotNullValidator", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getTBNode_b566a());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_b566a() {
		if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#b566a") != null)
			return (nc.ui.uif2.tangramlayout.node.TBNode) context
					.get("nc.ui.uif2.tangramlayout.node.TBNode#b566a");
		nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
		context.put("nc.ui.uif2.tangramlayout.node.TBNode#b566a", bean);
		bean.setShowMode("CardLayout");
		bean.setTabs(getManagedList11());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList11() {
		List list = new ArrayList();
		list.add(getVSNode_11f7d00());
		list.add(getVSNode_97677d());
		return list;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_11f7d00() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#11f7d00") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context
					.get("nc.ui.uif2.tangramlayout.node.VSNode#11f7d00");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#11f7d00", bean);
		bean.setShowMode("NoDivider");
		bean.setUp(getCNode_d8ee0());
		bean.setDown(getCNode_1f4d99());
		bean.setDividerLocation(30f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_d8ee0() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#d8ee0") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#d8ee0");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#d8ee0", bean);
		bean.setComponent(getOrgpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1f4d99() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1f4d99") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#1f4d99");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1f4d99", bean);
		bean.setComponent(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_97677d() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#97677d") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context
					.get("nc.ui.uif2.tangramlayout.node.VSNode#97677d");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#97677d", bean);
		bean.setShowMode("NoDivider");
		bean.setUp(getCNode_4791bb());
		bean.setDown(getCNode_b0934f());
		bean.setDividerLocation(26f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_4791bb() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#4791bb") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#4791bb");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#4791bb", bean);
		bean.setComponent(getEditorToolBarPanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_b0934f() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#b0934f") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#b0934f");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#b0934f", bean);
		bean.setComponent(getBillFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.payfile.view.PayfileOrgHeadPanel getOrgpanel() {
		if (context.get("orgpanel") != null)
			return (nc.ui.wa.payfile.view.PayfileOrgHeadPanel) context
					.get("orgpanel");
		nc.ui.wa.payfile.view.PayfileOrgHeadPanel bean = new nc.ui.wa.payfile.view.PayfileOrgHeadPanel();
		context.put("orgpanel", bean);
		bean.setModel(getManageAppModel());
		bean.setContext(getContext());
		bean.setDataManager(getModelDataManager());
		bean.setPk_orgtype("HRORGTYPE00000000000");
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.ShowMeUpAction getEditorReturnAction() {
		if (context.get("editorReturnAction") != null)
			return (nc.ui.uif2.actions.ShowMeUpAction) context
					.get("editorReturnAction");
		nc.ui.uif2.actions.ShowMeUpAction bean = new nc.ui.uif2.actions.ShowMeUpAction();
		context.put("editorReturnAction", bean);
		bean.setGoComponent(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getEditorToolBarPanel() {
		if (context.get("editorToolBarPanel") != null)
			return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel) context
					.get("editorToolBarPanel");
		nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
		context.put("editorToolBarPanel", bean);
		bean.setModel(getManageAppModel());
		bean.setTitleAction(getEditorReturnAction());
		bean.setActions(getManagedList12());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList12() {
		List list = new ArrayList();
		list.add(getFirstLineAction());
		list.add(getPreLineAction());
		list.add(getNextLineAction());
		list.add(getLastLineAction());
		return list;
	}

	public nc.ui.hr.uif2.mediator.HyperLinkClickMediator getMouseClickShowPanelMediator() {
		if (context.get("mouseClickShowPanelMediator") != null)
			return (nc.ui.hr.uif2.mediator.HyperLinkClickMediator) context
					.get("mouseClickShowPanelMediator");
		nc.ui.hr.uif2.mediator.HyperLinkClickMediator bean = new nc.ui.hr.uif2.mediator.HyperLinkClickMediator();
		context.put("mouseClickShowPanelMediator", bean);
		bean.setModel(getManageAppModel());
		bean.setShowUpComponent(getBillFormEditor());
		bean.setHyperLinkColumn("pk_psnjob");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}
