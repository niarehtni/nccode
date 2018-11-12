package nc.ui.twhr.nhicalc.ace.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class Nhicalc_config extends AbstractJavaBeanDefinition {
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
			return (nc.ui.uif2.editor.TemplateContainer) context
					.get("templateContainer");
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
			return (nc.vo.bd.meta.BDObjectAdpaterFactory) context
					.get("boadatorfactory");
		nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
		context.put("boadatorfactory", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.pub.smart.SmartBatchAppModelService getBatchModelService() {
		if (context.get("batchModelService") != null)
			return (nc.ui.pubapp.pub.smart.SmartBatchAppModelService) context
					.get("batchModelService");
		nc.ui.pubapp.pub.smart.SmartBatchAppModelService bean = new nc.ui.pubapp.pub.smart.SmartBatchAppModelService();
		context.put("batchModelService", bean);
		bean.setServiceItf("nc.itf.twhr.INhicalcMaintain");
		bean.setVoClass("nc.vo.twhr.nhicalc.NhiCalcVO");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.BatchBillTableModel getBatchBillTableModel() {
		if (context.get("batchBillTableModel") != null)
			return (nc.ui.pubapp.uif2app.model.BatchBillTableModel) context
					.get("batchBillTableModel");
		nc.ui.pubapp.uif2app.model.BatchBillTableModel bean = new nc.ui.pubapp.uif2app.model.BatchBillTableModel();
		context.put("batchBillTableModel", bean);
		bean.setContext(getContext());
		bean.setService(getBatchModelService());
		bean.setValidationService(getValidateService());
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.model.DefaultBatchValidationService getValidateService() {
		if (context.get("validateService") != null)
			return (nc.ui.uif2.model.DefaultBatchValidationService) context
					.get("validateService");
		nc.ui.uif2.model.DefaultBatchValidationService bean = new nc.ui.uif2.model.DefaultBatchValidationService();
		context.put("validateService", bean);
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.BatchModelDataManager getModelDataManager() {
		if (context.get("modelDataManager") != null)
			return (nc.ui.pubapp.uif2app.model.BatchModelDataManager) context
					.get("modelDataManager");
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
			return (nc.ui.uif2.FunNodeClosingHandler) context
					.get("closingListener");
		nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
		context.put("closingListener", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setSaveaction(getSaveAction());
		bean.setCancelaction(getCancelAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel getOrgpanel() {
		if (context.get("orgpanel") != null)
			return (nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel) context
					.get("orgpanel");
		nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel bean = new nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel();
		context.put("orgpanel", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setContext(getContext());
		bean.setDataManager(getModelDataManager());
		bean.setPk_orgtype("HRORGTYPE00000000000");
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable getList() {
		if (context.get("list") != null)
			return (nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable) context
					.get("list");
		nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable bean = new nc.ui.pubapp.uif2app.view.ShowUpableBatchBillTable();
		context.put("list", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setVoClassName("nc.vo.twhr.nhicalc.NhiCalcVO");
		bean.setIsBodyAutoAddLine(false);
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getAppEventHandlerMediator() {
		if (context.get("appEventHandlerMediator") != null)
			return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator) context
					.get("appEventHandlerMediator");
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
		list.add(getEventHandlerGroup_d62603());
		list.add(getEventHandlerGroup_325bc7());
		return list;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_d62603() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#d62603") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#d62603");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#d62603", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent");
		bean.setHandler(getBodyBeforeEditHandler_1ea8b5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler getBodyBeforeEditHandler_1ea8b5() {
		if (context
				.get("nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler#1ea8b5") != null)
			return (nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler) context
					.get("nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler#1ea8b5");
		nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler bean = new nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler();
		context.put("nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler#1ea8b5",
				bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_325bc7() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#325bc7") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#325bc7");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#325bc7", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent");
		bean.setHandler(getBodyAfterEditHandler_159ac1b());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler getBodyAfterEditHandler_159ac1b() {
		if (context
				.get("nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler#159ac1b") != null)
			return (nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler) context
					.get("nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler#159ac1b");
		nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler bean = new nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler();
		context.put("nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler#159ac1b",
				bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getVSNode_c64a2e());
		bean.setActions(getManagedList2());
		bean.setEditActions(getManagedList3());
		bean.setModel(getBatchBillTableModel());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_c64a2e() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#c64a2e") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context
					.get("nc.ui.uif2.tangramlayout.node.VSNode#c64a2e");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#c64a2e", bean);
		bean.setDown(getCNode_2767f8());
		bean.setUp(getCNode_11acdf0());
		bean.setDividerLocation(31f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_2767f8() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#2767f8") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#2767f8");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#2767f8", bean);
		bean.setComponent(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_11acdf0() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#11acdf0") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#11acdf0");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#11acdf0", bean);
		bean.setComponent(getOrgpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getGenerateAction());
		list.add(getEditAction());
		list.add(getSeparatorAction());
		list.add(getFilterActionGroup());
		list.add(getSeparatorAction());
		list.add(getCalculateAction());
		list.add(getAuditActionGroup());
		return list;
	}

	private List getManagedList3() {
		List list = new ArrayList();
		list.add(getSeparatorAction());
		list.add(getSaveAction());
		list.add(getCancelAction());
		return list;
	}

	public nc.funcnode.ui.action.SeparatorAction getSeparatorAction() {
		if (context.get("separatorAction") != null)
			return (nc.funcnode.ui.action.SeparatorAction) context
					.get("separatorAction");
		nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
		context.put("separatorAction", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.nhicalc.action.GenerateAction getGenerateAction() {
		if (context.get("generateAction") != null)
			return (nc.ui.twhr.nhicalc.action.GenerateAction) context
					.get("generateAction");
		nc.ui.twhr.nhicalc.action.GenerateAction bean = new nc.ui.twhr.nhicalc.action.GenerateAction();
		context.put("generateAction", bean);
		bean.setCode("generateaction");
		bean.setBtnName("生成");
		bean.setOrgpanel(getOrgpanel());
		bean.setModel(getBatchBillTableModel());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.nhicalc.action.QueryAction getQueryAction() {
		if (context.get("queryAction") != null)
			return (nc.ui.twhr.nhicalc.action.QueryAction) context
					.get("queryAction");
		nc.ui.twhr.nhicalc.action.QueryAction bean = new nc.ui.twhr.nhicalc.action.QueryAction();
		context.put("queryAction", bean);
		bean.setCode("queryaction");
		bean.setBtnName("查询");
		bean.setOrgpanel(getOrgpanel());
		bean.setModel(getBatchBillTableModel());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getFilterActionGroup() {
		if (context.get("filterActionGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context
					.get("filterActionGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("filterActionGroup", bean);
		bean.setCode("filterManage");
		bean.setName("过滤");
		bean.setActions(getManagedList4());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList4() {
		List list = new ArrayList();
		list.add(getShowLastMonthAction());
		return list;
	}

	public nc.ui.twhr.nhicalc.action.FilterAction getFilteraction() {
		if (context.get("filteraction") != null)
			return (nc.ui.twhr.nhicalc.action.FilterAction) context
					.get("filteraction");
		nc.ui.twhr.nhicalc.action.FilterAction bean = new nc.ui.twhr.nhicalc.action.FilterAction();
		context.put("filteraction", bean);
		bean.setCode("filteraction");
		bean.setBtnName("只显示差异");
		bean.setOrgpanel(getOrgpanel());
		bean.setModel(getBatchBillTableModel());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.nhicalc.action.ShowLastMonthAction getShowLastMonthAction() {
		if (context.get("showLastMonthAction") != null)
			return (nc.ui.twhr.nhicalc.action.ShowLastMonthAction) context
					.get("showLastMonthAction");
		nc.ui.twhr.nhicalc.action.ShowLastMonthAction bean = new nc.ui.twhr.nhicalc.action.ShowLastMonthAction();
		context.put("showLastMonthAction", bean);
		bean.setCode("showlastmonthaction");
		bean.setBtnName("隱藏上月資料");
		bean.setOrgpanel(getOrgpanel());
		bean.setModel(getBatchBillTableModel());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.nhicalc.action.EditAction getEditAction() {
		if (context.get("editAction") != null)
			return (nc.ui.twhr.nhicalc.action.EditAction) context
					.get("editAction");
		nc.ui.twhr.nhicalc.action.EditAction bean = new nc.ui.twhr.nhicalc.action.EditAction();
		context.put("editAction", bean);
		bean.setBtnName("修改");
		bean.setOrgpanel(getOrgpanel());
		bean.setModel(getBatchBillTableModel());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.batch.BatchSaveAction getSaveAction() {
		if (context.get("saveAction") != null)
			return (nc.ui.pubapp.uif2app.actions.batch.BatchSaveAction) context
					.get("saveAction");
		nc.ui.pubapp.uif2app.actions.batch.BatchSaveAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchSaveAction();
		context.put("saveAction", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction getCancelAction() {
		if (context.get("cancelAction") != null)
			return (nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction) context
					.get("cancelAction");
		nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction bean = new nc.ui.pubapp.uif2app.actions.batch.BatchCancelAction();
		context.put("cancelAction", bean);
		bean.setModel(getBatchBillTableModel());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.nhicalc.action.CalculateAction getCalculateAction() {
		if (context.get("calculateAction") != null)
			return (nc.ui.twhr.nhicalc.action.CalculateAction) context
					.get("calculateAction");
		nc.ui.twhr.nhicalc.action.CalculateAction bean = new nc.ui.twhr.nhicalc.action.CalculateAction();
		context.put("calculateAction", bean);
		bean.setCode("calculateaction");
		bean.setBtnName("计算");
		bean.setOrgpanel(getOrgpanel());
		bean.setModel(getBatchBillTableModel());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getAuditActionGroup() {
		if (context.get("auditActionGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context
					.get("auditActionGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("auditActionGroup", bean);
		bean.setCode("auditManage");
		bean.setName("审核");
		bean.setActions(getManagedList5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList5() {
		List list = new ArrayList();
		list.add(getAuditaction());
		list.add(getUnauditaction());
		return list;
	}

	public nc.ui.twhr.nhicalc.action.AuditAction getAuditaction() {
		if (context.get("auditaction") != null)
			return (nc.ui.twhr.nhicalc.action.AuditAction) context
					.get("auditaction");
		nc.ui.twhr.nhicalc.action.AuditAction bean = new nc.ui.twhr.nhicalc.action.AuditAction();
		context.put("auditaction", bean);
		bean.setCode("auditaction");
		bean.setBtnName("审核");
		bean.setOrgpanel(getOrgpanel());
		bean.setModel(getBatchBillTableModel());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.twhr.nhicalc.action.UnAuditAction getUnauditaction() {
		if (context.get("unauditaction") != null)
			return (nc.ui.twhr.nhicalc.action.UnAuditAction) context
					.get("unauditaction");
		nc.ui.twhr.nhicalc.action.UnAuditAction bean = new nc.ui.twhr.nhicalc.action.UnAuditAction();
		context.put("unauditaction", bean);
		bean.setCode("unauditaction");
		bean.setBtnName("取消审核");
		bean.setOrgpanel(getOrgpanel());
		bean.setModel(getBatchBillTableModel());
		bean.setEditor(getList());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}
