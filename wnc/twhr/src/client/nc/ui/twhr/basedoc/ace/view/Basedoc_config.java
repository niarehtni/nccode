package nc.ui.twhr.basedoc.ace.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class Basedoc_config extends AbstractJavaBeanDefinition {
	private Map<String, Object> context = new HashMap();

	public nc.vo.uif2.LoginContext getContext() {
		if (context.get("context") != null)
			return (nc.vo.uif2.LoginContext) context.get("context");
		nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
		context.put("context", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.editor.TemplateContainer getTemplateContainer() {
		if (context.get("templateContainer") != null)
			return (nc.ui.uif2.editor.TemplateContainer) context.get("templateContainer");
		nc.ui.uif2.editor.TemplateContainer bean = new nc.ui.uif2.editor.TemplateContainer();
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
		list.add("bt");
		return list;
	}

	public nc.vo.bd.meta.BDObjectAdpaterFactory getBoadatorfactory() {
		if (context.get("boadatorfactory") != null)
			return (nc.vo.bd.meta.BDObjectAdpaterFactory) context.get("boadatorfactory");
		nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
		context.put("boadatorfactory", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.pub.smart.SmartBatchAppModelService getBatchModelService() {
		if (context.get("batchModelService") != null)
			return (nc.ui.pubapp.pub.smart.SmartBatchAppModelService) context.get("batchModelService");
		nc.ui.pubapp.pub.smart.SmartBatchAppModelService bean = new nc.ui.pubapp.pub.smart.SmartBatchAppModelService();
		context.put("batchModelService", bean);
		bean.setServiceItf("nc.itf.twhr.IBasedocMaintain");
		bean.setVoClass("nc.vo.twhr.basedoc.BaseDocVO");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.basedoc.model.BaseDocBillTableModel getBatchBillTableModel() {
		if (context.get("batchBillTableModel") != null)
			return (nc.ui.twhr.basedoc.model.BaseDocBillTableModel) context.get("batchBillTableModel");
		nc.ui.twhr.basedoc.model.BaseDocBillTableModel bean = new nc.ui.twhr.basedoc.model.BaseDocBillTableModel();
		context.put("batchBillTableModel", bean);
		bean.setContext(getContext());
		bean.setService(getBatchModelService());
		bean.setValidationService(getValidateService());
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.model.DefaultBatchValidationService getValidateService() {
		if (context.get("validateService") != null)
			return (nc.ui.uif2.model.DefaultBatchValidationService) context.get("validateService");
		nc.ui.uif2.model.DefaultBatchValidationService bean = new nc.ui.uif2.model.DefaultBatchValidationService();
		context.put("validateService", bean);
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.BatchModelDataManager getModelDataManager() {
		if (context.get("modelDataManager") != null)
			return (nc.ui.pubapp.uif2app.model.BatchModelDataManager) context.get("modelDataManager");
		nc.ui.pubapp.uif2app.model.BatchModelDataManager bean = new nc.ui.pubapp.uif2app.model.BatchModelDataManager();
		context.put("modelDataManager", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setService(getBatchModelService());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.FunNodeClosingHandler getClosingListener() {
		if (context.get("closingListener") != null)
			return (nc.ui.uif2.FunNodeClosingHandler) context.get("closingListener");
		nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
		context.put("closingListener", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setSaveaction(getSaveAction());
		bean.setCancelaction(getCancelAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.glb.view.OrgPanel_Org getOrgPanel() {
		if (context.get("orgPanel") != null)
			return (nc.ui.twhr.glb.view.OrgPanel_Org) context.get("orgPanel");
		nc.ui.twhr.glb.view.OrgPanel_Org bean = new nc.ui.twhr.glb.view.OrgPanel_Org();
		context.put("orgPanel", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setDataManager(getModelDataManager());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable getList() {
		if (context.get("list") != null)
			return (nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable) context.get("list");
		nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable bean = new nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable();
		context.put("list", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setVoClassName("nc.vo.twhr.basedoc.BaseDocVO");
		bean.setIsBodyAutoAddLine(false);
		bean.setAddLineAction(getAddAction());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getAppEventHandlerMediator() {
		if (context.get("appEventHandlerMediator") != null)
			return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator) context.get("appEventHandlerMediator");
		nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
		context.put("appEventHandlerMediator", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setHandlerGroup(getManagedList1());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList1() {
		List list = new ArrayList();
		list.add(getEventHandlerGroup_1528870());
		list.add(getEventHandlerGroup_1b8af30());
		return list;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_1528870() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1528870") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1528870");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1528870", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent");
		bean.setHandler(getBodyBeforeEditHandler_15cf2f4());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.twhr.basedoc.handler.BodyBeforeEditHandler getBodyBeforeEditHandler_15cf2f4() {
		if (context.get("nc.ui.twhr.basedoc.handler.BodyBeforeEditHandler#15cf2f4") != null)
			return (nc.ui.twhr.basedoc.handler.BodyBeforeEditHandler) context
					.get("nc.ui.twhr.basedoc.handler.BodyBeforeEditHandler#15cf2f4");
		nc.ui.twhr.basedoc.handler.BodyBeforeEditHandler bean = new nc.ui.twhr.basedoc.handler.BodyBeforeEditHandler();
		context.put("nc.ui.twhr.basedoc.handler.BodyBeforeEditHandler#15cf2f4", bean);
		bean.setBillModel(getBatchBillTableModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_1b8af30() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1b8af30") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1b8af30");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1b8af30", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent");
		bean.setHandler(getBodyAfterEditHandler_17b8a5c());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.twhr.basedoc.handler.BodyAfterEditHandler getBodyAfterEditHandler_17b8a5c() {
		if (context.get("nc.ui.twhr.basedoc.handler.BodyAfterEditHandler#17b8a5c") != null)
			return (nc.ui.twhr.basedoc.handler.BodyAfterEditHandler) context
					.get("nc.ui.twhr.basedoc.handler.BodyAfterEditHandler#17b8a5c");
		nc.ui.twhr.basedoc.handler.BodyAfterEditHandler bean = new nc.ui.twhr.basedoc.handler.BodyAfterEditHandler();
		context.put("nc.ui.twhr.basedoc.handler.BodyAfterEditHandler#17b8a5c", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getVSNode_12859aa());
		bean.setActions(getManagedList2());
		bean.setEditActions(getManagedList3());
		bean.setModel(getBatchBillTableModel());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_12859aa() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#12859aa") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#12859aa");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#12859aa", bean);
		bean.setDown(getCNode_1f593dd());
		bean.setUp(getCNode_1becb97());
		bean.setDividerLocation(31f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1f593dd() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1f593dd") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1f593dd");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1f593dd", bean);
		bean.setComponent(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1becb97() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1becb97") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1becb97");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1becb97", bean);
		bean.setComponent(getOrgPanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getAddAction());
		list.add(getEditAction());
		list.add(getDelAction());
		list.add(getSeparatorAction());
		list.add(getRefreshAction());
		list.add(getPrintActionGroup());
		return list;
	}

	private List getManagedList3() {
		List list = new ArrayList();
		list.add(getAddAction());
		list.add(getDelAction());
		list.add(getSeparatorAction());
		list.add(getSaveAction());
		list.add(getCancelAction());
		return list;
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

	public nc.ui.twhr.basedoc.action.BasedocAddLineAction getAddAction() {
		if (context.get("addAction") != null)
			return (nc.ui.twhr.basedoc.action.BasedocAddLineAction) context.get("addAction");
		nc.ui.twhr.basedoc.action.BasedocAddLineAction bean = new nc.ui.twhr.basedoc.action.BasedocAddLineAction();
		context.put("addAction", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setVoClassName("nc.vo.twhr.basedoc.BaseDocVO");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.batch.BatchEditAction getEditAction() {
		if (context.get("editAction") != null)
			return (nc.ui.pubapp.uif2app.actions.batch.BatchEditAction) context.get("editAction");
		nc.ui.pubapp.uif2app.actions.batch.BatchEditAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchEditAction();
		context.put("editAction", bean);
		bean.setModel(getBatchBillTableModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction getDelAction() {
		if (context.get("delAction") != null)
			return (nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction) context.get("delAction");
		nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction();
		context.put("delAction", bean);
		bean.setModel(getBatchBillTableModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.basedoc.action.BaseDocBatchSaveAction getSaveAction() {
		if (context.get("saveAction") != null)
			return (nc.ui.twhr.basedoc.action.BaseDocBatchSaveAction) context.get("saveAction");
		nc.ui.twhr.basedoc.action.BaseDocBatchSaveAction bean = new nc.ui.twhr.basedoc.action.BaseDocBatchSaveAction();
		context.put("saveAction", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setRefreshAction(getRefreshAction());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction getCancelAction() {
		if (context.get("cancelAction") != null)
			return (nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction) context.get("cancelAction");
		nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction();
		context.put("cancelAction", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.batch.BatchRefreshAction getRefreshAction() {
		if (context.get("refreshAction") != null)
			return (nc.ui.uif2.actions.batch.BatchRefreshAction) context.get("refreshAction");
		nc.ui.uif2.actions.batch.BatchRefreshAction bean = new nc.ui.uif2.actions.batch.BatchRefreshAction();
		context.put("refreshAction", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setModelManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getPrintActionGroup() {
		if (context.get("printActionGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context.get("printActionGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("printActionGroup", bean);
		bean.setCode("printManage");
		bean.setName(getI18nFB_16a984());
		bean.setActions(getManagedList4());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_16a984() {
		if (context.get("nc.ui.uif2.I18nFB#16a984") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#16a984");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#16a984", bean);
		bean.setResDir("common");
		bean.setResId("UC001-0000007");
		bean.setDefaultValue("¥´¦L");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#16a984", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList4() {
		List list = new ArrayList();
		list.add(getPrintpreviewaction());
		list.add(getPrintaction());
		list.add(getOutputAction());
		return list;
	}

	public nc.ui.hr.uif2.action.print.TemplatePreviewAction getPrintpreviewaction() {
		if (context.get("printpreviewaction") != null)
			return (nc.ui.hr.uif2.action.print.TemplatePreviewAction) context.get("printpreviewaction");
		nc.ui.hr.uif2.action.print.TemplatePreviewAction bean = new nc.ui.hr.uif2.action.print.TemplatePreviewAction();
		context.put("printpreviewaction", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setPrintDlgParentConatiner(getList());
		bean.setDatasource(getDatasource());
		bean.setNodeKey("twhrbasedoc");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.action.print.TemplatePrintAction getPrintaction() {
		if (context.get("printaction") != null)
			return (nc.ui.hr.uif2.action.print.TemplatePrintAction) context.get("printaction");
		nc.ui.hr.uif2.action.print.TemplatePrintAction bean = new nc.ui.hr.uif2.action.print.TemplatePrintAction();
		context.put("printaction", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setPrintDlgParentConatiner(getList());
		bean.setDatasource(getDatasource());
		bean.setNodeKey("twhrbasedoc");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.action.print.ExportCardAction getOutputAction() {
		if (context.get("outputAction") != null)
			return (nc.ui.hr.uif2.action.print.ExportCardAction) context.get("outputAction");
		nc.ui.hr.uif2.action.print.ExportCardAction bean = new nc.ui.hr.uif2.action.print.ExportCardAction();
		context.put("outputAction", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setPrintDlgParentConatiner(getList());
		bean.setDatasource(getDatasource());
		bean.setNodeKey("twhrbasedoc");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.bd.pub.actions.print.MetaDataAllDatasSource getDatasource() {
		if (context.get("datasource") != null)
			return (nc.ui.bd.pub.actions.print.MetaDataAllDatasSource) context.get("datasource");
		nc.ui.bd.pub.actions.print.MetaDataAllDatasSource bean = new nc.ui.bd.pub.actions.print.MetaDataAllDatasSource();
		context.put("datasource", bean);
		bean.setModel(getBatchBillTableModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}
