package nc.ui.overtime.otleavebalance.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class otleavebalance_config extends AbstractJavaBeanDefinition {
	private Map<String, Object> context = new HashMap();

	public nc.vo.ta.pub.TALoginContext getContext() {
		if (context.get("context") != null)
			return (nc.vo.ta.pub.TALoginContext) context.get("context");
		nc.vo.ta.pub.TALoginContext bean = new nc.vo.ta.pub.TALoginContext();
		context.put("context", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.serviceproxy.OTLeaveBalanceMaintainProxy getBmModelModelService() {
		if (context.get("bmModelModelService") != null)
			return (nc.ui.overtime.otleavebalance.serviceproxy.OTLeaveBalanceMaintainProxy) context
					.get("bmModelModelService");
		nc.ui.overtime.otleavebalance.serviceproxy.OTLeaveBalanceMaintainProxy bean = new nc.ui.overtime.otleavebalance.serviceproxy.OTLeaveBalanceMaintainProxy();
		context.put("bmModelModelService", bean);
		bean.setOrgPanel(getOrgpanel());
		bean.setHierachicalModel(getHierachicalAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.vo.bd.meta.GeneralBDObjectAdapterFactory getBOAdapterFactory() {
		if (context.get("BOAdapterFactory") != null)
			return (nc.vo.bd.meta.GeneralBDObjectAdapterFactory) context.get("BOAdapterFactory");
		nc.vo.bd.meta.GeneralBDObjectAdapterFactory bean = new nc.vo.bd.meta.GeneralBDObjectAdapterFactory();
		context.put("BOAdapterFactory", bean);
		bean.setMode("MD");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.vo.bd.meta.BDObjectTreeCreateStrategy getTreeCreateStrategy() {
		if (context.get("treeCreateStrategy") != null)
			return (nc.vo.bd.meta.BDObjectTreeCreateStrategy) context.get("treeCreateStrategy");
		nc.vo.bd.meta.BDObjectTreeCreateStrategy bean = new nc.vo.bd.meta.BDObjectTreeCreateStrategy();
		context.put("treeCreateStrategy", bean);
		bean.setFactory(getBOAdapterFactory());
		bean.setRootName(getI18nFB_b50561());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_b50561() {
		if (context.get("nc.ui.uif2.I18nFB#b50561") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#b50561");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#b50561", bean);
		bean.setResDir("common");
		bean.setDefaultValue("�ݼ�e");
		bean.setResId("UC000-0000234");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#b50561", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public nc.ui.uif2.model.HierachicalDataAppModel getHierachicalAppModel() {
		if (context.get("hierachicalAppModel") != null)
			return (nc.ui.uif2.model.HierachicalDataAppModel) context.get("hierachicalAppModel");
		nc.ui.uif2.model.HierachicalDataAppModel bean = new nc.ui.uif2.model.HierachicalDataAppModel();
		context.put("hierachicalAppModel", bean);
		bean.setService(getTreeModelService());
		bean.setTreeCreateStrategy(getTreeCreateStrategy());
		bean.setBusinessObjectAdapterFactory(getBOAdapterFactory());
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModelService getTreeModelService() {
		if (context.get("treeModelService") != null)
			return (nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModelService) context.get("treeModelService");
		nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModelService bean = new nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModelService();
		context.put("treeModelService", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModel getHeadModel() {
		if (context.get("headModel") != null)
			return (nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModel) context.get("headModel");
		nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModel bean = new nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModel();
		context.put("headModel", bean);
		bean.setContext(getContext());
		bean.setOtListModel(getOtListModel());
		bean.setLvListModel(getLvListModel());
		bean.setBusinessObjectAdapterFactory(getBOAdapterFactory());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.BillManageModel getOtListModel() {
		if (context.get("otListModel") != null)
			return (nc.ui.pubapp.uif2app.model.BillManageModel) context.get("otListModel");
		nc.ui.pubapp.uif2app.model.BillManageModel bean = new nc.ui.pubapp.uif2app.model.BillManageModel();
		context.put("otListModel", bean);
		bean.setContext(getContext());
		bean.setBusinessObjectAdapterFactory(getBOAdapterFactory());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.BillManageModel getLvListModel() {
		if (context.get("lvListModel") != null)
			return (nc.ui.pubapp.uif2app.model.BillManageModel) context.get("lvListModel");
		nc.ui.pubapp.uif2app.model.BillManageModel bean = new nc.ui.pubapp.uif2app.model.BillManageModel();
		context.put("lvListModel", bean);
		bean.setContext(getContext());
		bean.setBusinessObjectAdapterFactory(getBOAdapterFactory());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager getHeadModelDataManager() {
		if (context.get("headModelDataManager") != null)
			return (nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager) context
					.get("headModelDataManager");
		nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager bean = new nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager();
		context.put("headModelDataManager", bean);
		bean.setModel(getHeadModel());
		bean.setService(getBmModelModelService());
		bean.setHierachicalModel(getHierachicalAppModel());
		bean.setOrgpanel(getOrgpanel());
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager getOtListModelDataManager() {
		if (context.get("otListModelDataManager") != null)
			return (nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager) context
					.get("otListModelDataManager");
		nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager bean = new nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager();
		context.put("otListModelDataManager", bean);
		bean.setModel(getOtListModel());
		bean.setService(getBmModelModelService());
		bean.setHierachicalModel(getHierachicalAppModel());
		bean.setOrgpanel(getOrgpanel());
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager getLvListModelDataManager() {
		if (context.get("lvListModelDataManager") != null)
			return (nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager) context
					.get("lvListModelDataManager");
		nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager bean = new nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager();
		context.put("lvListModelDataManager", bean);
		bean.setModel(getLvListModel());
		bean.setService(getBmModelModelService());
		bean.setHierachicalModel(getHierachicalAppModel());
		bean.setOrgpanel(getOrgpanel());
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.handler.OTLeaveBalanceMediator getModelMediator() {
		if (context.get("modelMediator") != null)
			return (nc.ui.overtime.otleavebalance.handler.OTLeaveBalanceMediator) context.get("modelMediator");
		nc.ui.overtime.otleavebalance.handler.OTLeaveBalanceMediator bean = new nc.ui.overtime.otleavebalance.handler.OTLeaveBalanceMediator();
		context.put("modelMediator", bean);
		bean.setHierachicalModel(getHierachicalAppModel());
		bean.setDataManager(getHeadModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.handler.MouseClickEventHandler getHeadListMouseClickMediator() {
		if (context.get("headListMouseClickMediator") != null)
			return (nc.ui.overtime.otleavebalance.handler.MouseClickEventHandler) context
					.get("headListMouseClickMediator");
		nc.ui.overtime.otleavebalance.handler.MouseClickEventHandler bean = new nc.ui.overtime.otleavebalance.handler.MouseClickEventHandler();
		context.put("headListMouseClickMediator", bean);
		bean.setHeadModel(getHeadModel());
		bean.setOtListModel(getOtListModel());
		bean.setLeaveListView(getLeaveListView());
		bean.setHierachicalModel(getHierachicalAppModel());
		bean.setContext(getContext());
		bean.setOrgpanel(getOrgpanel());
		bean.setOtListView(getOtListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.TemplateContainer getTemplateContainer() {
		if (context.get("templateContainer") != null)
			return (nc.ui.pubapp.uif2app.view.TemplateContainer) context.get("templateContainer");
		nc.ui.pubapp.uif2app.view.TemplateContainer bean = new nc.ui.pubapp.uif2app.view.TemplateContainer();
		context.put("templateContainer", bean);
		bean.setContext(getContext());
		bean.setNodeKeies(getManagedList0());
		bean.load();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList0() {
		List list = new ArrayList();
		list.add("tp");
		list.add("sc");
		list.add("sp");
		return list;
	}

	public nc.ui.overtime.otleavebalance.view.OTLeaveBalanceTreeCellRenderer getTreeCellRenderer() {
		if (context.get("treeCellRenderer") != null)
			return (nc.ui.overtime.otleavebalance.view.OTLeaveBalanceTreeCellRenderer) context.get("treeCellRenderer");
		nc.ui.overtime.otleavebalance.view.OTLeaveBalanceTreeCellRenderer bean = new nc.ui.overtime.otleavebalance.view.OTLeaveBalanceTreeCellRenderer();
		context.put("treeCellRenderer", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.components.TreePanel getTreePanel() {
		if (context.get("treePanel") != null)
			return (nc.ui.uif2.components.TreePanel) context.get("treePanel");
		nc.ui.uif2.components.TreePanel bean = new nc.ui.uif2.components.TreePanel();
		context.put("treePanel", bean);
		bean.setModel(getHierachicalAppModel());
		bean.setRootvisibleflag(true);
		bean.setTreeCellRenderer(getTreeCellRenderer());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.BillListView getBillListView() {
		if (context.get("billListView") != null)
			return (nc.ui.pubapp.uif2app.view.BillListView) context.get("billListView");
		nc.ui.pubapp.uif2app.view.BillListView bean = new nc.ui.pubapp.uif2app.view.BillListView();
		context.put("billListView", bean);
		bean.setModel(getHeadModel());
		bean.setNodekey("tp");
		bean.setMultiSelectionEnable(false);
		bean.setTemplateContainer(getTemplateContainer());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.BillListView getOtListView() {
		if (context.get("otListView") != null)
			return (nc.ui.pubapp.uif2app.view.BillListView) context.get("otListView");
		nc.ui.pubapp.uif2app.view.BillListView bean = new nc.ui.pubapp.uif2app.view.BillListView();
		context.put("otListView", bean);
		bean.setModel(getOtListModel());
		bean.setNodekey("sc");
		bean.setMultiSelectionEnable(false);
		bean.setTemplateContainer(getTemplateContainer());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.view.OvertimeLinkClickMediator getOtLinkMediator() {
		if (context.get("otLinkMediator") != null)
			return (nc.ui.overtime.otleavebalance.view.OvertimeLinkClickMediator) context.get("otLinkMediator");
		nc.ui.overtime.otleavebalance.view.OvertimeLinkClickMediator bean = new nc.ui.overtime.otleavebalance.view.OvertimeLinkClickMediator();
		context.put("otLinkMediator", bean);
		bean.setListView(getOtListView());
		bean.setHyperLinkColumn("calendar");
		bean.setOrgPanel(getOrgpanel());
		bean.setOtListModel(getOtListModel());
		bean.setHierachicalModel(getHierachicalAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.view.OtleaveBalanceSPBillListView getLeaveListView() {
		if (context.get("leaveListView") != null)
			return (nc.ui.overtime.otleavebalance.view.OtleaveBalanceSPBillListView) context.get("leaveListView");
		nc.ui.overtime.otleavebalance.view.OtleaveBalanceSPBillListView bean = new nc.ui.overtime.otleavebalance.view.OtleaveBalanceSPBillListView();
		context.put("leaveListView", bean);
		bean.setModel(getLvListModel());
		bean.setNodekey("sp");
		bean.setMultiSelectionEnable(false);
		bean.setTemplateContainer(getTemplateContainer());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.view.LeaveLinkClickMediator getLeaveLinkMediator() {
		if (context.get("leaveLinkMediator") != null)
			return (nc.ui.overtime.otleavebalance.view.LeaveLinkClickMediator) context.get("leaveLinkMediator");
		nc.ui.overtime.otleavebalance.view.LeaveLinkClickMediator bean = new nc.ui.overtime.otleavebalance.view.LeaveLinkClickMediator();
		context.put("leaveLinkMediator", bean);
		bean.setListView(getLeaveListView());
		bean.setHyperLinkColumn("leavedate");
		bean.setOrgPanel(getOrgpanel());
		bean.setLvListModel(getLvListModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel getOrgpanel() {
		if (context.get("orgpanel") != null)
			return (nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel) context.get("orgpanel");
		nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel bean = new nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel();
		context.put("orgpanel", bean);
		bean.setModel(getHeadModel());
		bean.setDataManager(getHeadModelDataManager());
		bean.setQueryAction(getDefaultQueryAction());
		bean.setPk_orgtype("HRORGTYPE00000000000");
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getVSNode_9a73d1());
		bean.setActions(getManagedList1());
		bean.setModel(getHeadModel());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_9a73d1() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#9a73d1") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#9a73d1");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#9a73d1", bean);
		bean.setShowMode("NoDivider");
		bean.setUp(getCNode_123512a());
		bean.setDown(getHSNode_1ac95e());
		bean.setDividerLocation(30f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_123512a() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#123512a") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#123512a");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#123512a", bean);
		bean.setComponent(getOrgpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_1ac95e() {
		if (context.get("nc.ui.uif2.tangramlayout.node.HSNode#1ac95e") != null)
			return (nc.ui.uif2.tangramlayout.node.HSNode) context.get("nc.ui.uif2.tangramlayout.node.HSNode#1ac95e");
		nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
		context.put("nc.ui.uif2.tangramlayout.node.HSNode#1ac95e", bean);
		bean.setLeft(getCNode_1a5ac5e());
		bean.setRight(getVSNode_829171());
		bean.setDividerLocation(240.0f);
		bean.setName("�C��");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1a5ac5e() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1a5ac5e") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1a5ac5e");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1a5ac5e", bean);
		bean.setComponent(getTreePanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_829171() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#829171") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#829171");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#829171", bean);
		bean.setUp(getCNode_1d77411());
		bean.setDown(getHSNode_95189b());
		bean.setDividerLocation(240.0f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1d77411() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1d77411") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1d77411");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1d77411", bean);
		bean.setComponent(getBillListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_95189b() {
		if (context.get("nc.ui.uif2.tangramlayout.node.HSNode#95189b") != null)
			return (nc.ui.uif2.tangramlayout.node.HSNode) context.get("nc.ui.uif2.tangramlayout.node.HSNode#95189b");
		nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
		context.put("nc.ui.uif2.tangramlayout.node.HSNode#95189b", bean);
		bean.setLeft(getCNode_53003());
		bean.setRight(getCNode_935491());
		bean.setDividerLocation(700.0f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_53003() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#53003") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#53003");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#53003", bean);
		bean.setComponent(getOtListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_935491() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#935491") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#935491");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#935491", bean);
		bean.setComponent(getLeaveListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList1() {
		List list = new ArrayList();
		list.add(getDefaultQueryAction());
		list.add(getSeparatorAction());
		list.add(getSettleActionGroup());
		list.add(getSeparatorAction());
		list.add(getRebuildSegAction());
		list.add(getSeparatorAction());
		list.add(getOutputAction());
		return list;
	}

	public nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener getInitDataListener() {
		if (context.get("InitDataListener") != null)
			return (nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener) context.get("InitDataListener");
		nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener bean = new nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener();
		context.put("InitDataListener", bean);
		bean.setModel(getHeadModel());
		bean.setContext(getContext());
		bean.setVoClassName("nc.vo.ta.overtime.AggOTLeaveBalanceVO");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.common.validateservice.ClosingCheck getClosingListener() {
		if (context.get("ClosingListener") != null)
			return (nc.ui.pubapp.common.validateservice.ClosingCheck) context.get("ClosingListener");
		nc.ui.pubapp.common.validateservice.ClosingCheck bean = new nc.ui.pubapp.common.validateservice.ClosingCheck();
		context.put("ClosingListener", bean);
		bean.setModel(getHeadModel());
		bean.setSaveAction((nc.ui.uif2.NCAction) findBeanInUIF2BeanFactory("saveAction"));
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.action.QueryAction getDefaultQueryAction() {
		if (context.get("defaultQueryAction") != null)
			return (nc.ui.overtime.otleavebalance.action.QueryAction) context.get("defaultQueryAction");
		nc.ui.overtime.otleavebalance.action.QueryAction bean = new nc.ui.overtime.otleavebalance.action.QueryAction();
		context.put("defaultQueryAction", bean);
		bean.setModel(getHeadModel());
		bean.setTemplateContainer(getDefaultQueryActionQueryTemplateContainer());
		bean.setNodeKey("qt");
		bean.setDataManager(getHeadModelDataManager());
		bean.setExceptionHandler(getExceptionHandler());
		bean.setOrgpanel(getOrgpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.editor.QueryTemplateContainer getDefaultQueryActionQueryTemplateContainer() {
		if (context.get("defaultQueryActionQueryTemplateContainer") != null)
			return (nc.ui.uif2.editor.QueryTemplateContainer) context.get("defaultQueryActionQueryTemplateContainer");
		nc.ui.uif2.editor.QueryTemplateContainer bean = new nc.ui.uif2.editor.QueryTemplateContainer();
		context.put("defaultQueryActionQueryTemplateContainer", bean);
		bean.setNodeKey("qt");
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.action.SettleAction getSettleAction() {
		if (context.get("settleAction") != null)
			return (nc.ui.overtime.otleavebalance.action.SettleAction) context.get("settleAction");
		nc.ui.overtime.otleavebalance.action.SettleAction bean = new nc.ui.overtime.otleavebalance.action.SettleAction();
		context.put("settleAction", bean);
		bean.setModel(getHeadModel());
		bean.setOrgpanel(getOrgpanel());
		bean.setHeadModelDataManager(getHeadModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.action.UnSettleAction getUnSettleAction() {
		if (context.get("unSettleAction") != null)
			return (nc.ui.overtime.otleavebalance.action.UnSettleAction) context.get("unSettleAction");
		nc.ui.overtime.otleavebalance.action.UnSettleAction bean = new nc.ui.overtime.otleavebalance.action.UnSettleAction();
		context.put("unSettleAction", bean);
		bean.setModel(getHeadModel());
		bean.setOrgpanel(getOrgpanel());
		bean.setHeadModelDataManager(getHeadModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getSettleActionGroup() {
		if (context.get("settleActionGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context.get("settleActionGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("settleActionGroup", bean);
		bean.setCode("settleActions");
		bean.setName("�Y��");
		bean.setActions(getManagedList2());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getSettleAction());
		list.add(getUnSettleAction());
		return list;
	}

	public nc.ui.hr.uif2.action.print.TemplatePrintAction getMetaDataBasedPrintAction() {
		if (context.get("metaDataBasedPrintAction") != null)
			return (nc.ui.hr.uif2.action.print.TemplatePrintAction) context.get("metaDataBasedPrintAction");
		nc.ui.hr.uif2.action.print.TemplatePrintAction bean = new nc.ui.hr.uif2.action.print.TemplatePrintAction();
		context.put("metaDataBasedPrintAction", bean);
		bean.setModel(getHeadModel());
		bean.setNodeKey("ot");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.action.ListOutputAction getOutputAction() {
		if (context.get("outputAction") != null)
			return (nc.ui.overtime.otleavebalance.action.ListOutputAction) context.get("outputAction");
		nc.ui.overtime.otleavebalance.action.ListOutputAction bean = new nc.ui.overtime.otleavebalance.action.ListOutputAction();
		context.put("outputAction", bean);
		bean.setModel(getHeadModel());
		bean.setListView(getBillListView());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.overtime.otleavebalance.action.RebuildPsnSegDetailAction getRebuildSegAction() {
		if (context.get("rebuildSegAction") != null)
			return (nc.ui.overtime.otleavebalance.action.RebuildPsnSegDetailAction) context.get("rebuildSegAction");
		nc.ui.overtime.otleavebalance.action.RebuildPsnSegDetailAction bean = new nc.ui.overtime.otleavebalance.action.RebuildPsnSegDetailAction();
		context.put("rebuildSegAction", bean);
		bean.setModel(getHeadModel());
		bean.setListView(getBillListView());
		bean.setOrgpanel(getOrgpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.SeparatorAction getSeparatorAction() {
		if (context.get("separatorAction") != null)
			return (nc.funcnode.ui.action.SeparatorAction) context.get("separatorAction");
		nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
		context.put("separatorAction", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.DefaultExceptionHanler getExceptionHandler() {
		if (context.get("exceptionHandler") != null)
			return (nc.ui.uif2.DefaultExceptionHanler) context.get("exceptionHandler");
		nc.ui.uif2.DefaultExceptionHanler bean = new nc.ui.uif2.DefaultExceptionHanler(getContainer());
		context.put("exceptionHandler", bean);
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}