package nc.ui.twhr.nhicalc.ace.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class Nhicalc_config extends AbstractJavaBeanDefinition {
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

    public nc.ui.twhr.nhicalc.ace.serviceproxy.AceNhicalcMaintainProxy getBmModelModelService() {
	if (context.get("bmModelModelService") != null)
	    return (nc.ui.twhr.nhicalc.ace.serviceproxy.AceNhicalcMaintainProxy) context.get("bmModelModelService");
	nc.ui.twhr.nhicalc.ace.serviceproxy.AceNhicalcMaintainProxy bean = new nc.ui.twhr.nhicalc.ace.serviceproxy.AceNhicalcMaintainProxy();
	context.put("bmModelModelService", bean);
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

    public nc.ui.twhr.nhicalc.model.NhicalcAppModel getBmModel() {
	if (context.get("bmModel") != null)
	    return (nc.ui.twhr.nhicalc.model.NhicalcAppModel) context.get("bmModel");
	nc.ui.twhr.nhicalc.model.NhicalcAppModel bean = new nc.ui.twhr.nhicalc.model.NhicalcAppModel();
	context.put("bmModel", bean);
	bean.setContext(getContext());
	bean.setService(getBmModelModelService());
	bean.setBusinessObjectAdapterFactory(getBOAdapterFactory());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.twhr.nhicalc.model.NhicalcAppModelDataManager getBmModelModelDataManager() {
	if (context.get("bmModelModelDataManager") != null)
	    return (nc.ui.twhr.nhicalc.model.NhicalcAppModelDataManager) context.get("bmModelModelDataManager");
	nc.ui.twhr.nhicalc.model.NhicalcAppModelDataManager bean = new nc.ui.twhr.nhicalc.model.NhicalcAppModelDataManager();
	context.put("bmModelModelDataManager", bean);
	bean.setContext(getContext());
	bean.setModel(getBmModel());
	bean.setService(getBmModelModelService());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel getOrgpanel() {
	if (context.get("orgpanel") != null)
	    return (nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel) context.get("orgpanel");
	nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel bean = new nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel();
	context.put("orgpanel", bean);
	bean.setModel(getBmModel());
	bean.setContext(getContext());
	bean.setDataManager(getBmModelModelDataManager());
	bean.setPk_orgtype("HRORGTYPE00000000000");
	bean.initUI();
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
	list.add("bt");
	return list;
    }

    public nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell getViewa() {
	if (context.get("viewa") != null)
	    return (nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell) context.get("viewa");
	nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell bean = new nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell();
	context.put("viewa", bean);
	bean.setQueryAreaCreator(getQueryAction());
	bean.initUI();
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.pubapp.uif2app.view.ShowUpableBillListView getBillListView() {
	if (context.get("billListView") != null)
	    return (nc.ui.pubapp.uif2app.view.ShowUpableBillListView) context.get("billListView");
	nc.ui.pubapp.uif2app.view.ShowUpableBillListView bean = new nc.ui.pubapp.uif2app.view.ShowUpableBillListView();
	context.put("billListView", bean);
	bean.setModel(getBmModel());
	bean.setNodekey("bt");
	bean.setMultiSelectionEnable(false);
	bean.setTemplateContainer(getTemplateContainer());
	bean.initUI();
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel getViewb() {
	if (context.get("viewb") != null)
	    return (nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel) context.get("viewb");
	nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel bean = new nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel();
	context.put("viewb", bean);
	bean.setModel(getBmModel());
	bean.setTitleAction(getReturnAction());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.pubapp.uif2app.actions.UEReturnAction getReturnAction() {
	if (context.get("returnAction") != null)
	    return (nc.ui.pubapp.uif2app.actions.UEReturnAction) context.get("returnAction");
	nc.ui.pubapp.uif2app.actions.UEReturnAction bean = new nc.ui.pubapp.uif2app.actions.UEReturnAction();
	context.put("returnAction", bean);
	bean.setGoComponent(getBillListView());
	bean.setSaveAction(getSaveAction());
	bean.setModel(getBmModel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.pubapp.uif2app.view.ShowUpableBillForm getBillForm() {
	if (context.get("billForm") != null)
	    return (nc.ui.pubapp.uif2app.view.ShowUpableBillForm) context.get("billForm");
	nc.ui.pubapp.uif2app.view.ShowUpableBillForm bean = new nc.ui.pubapp.uif2app.view.ShowUpableBillForm();
	context.put("billForm", bean);
	bean.setModel(getBmModel());
	bean.setNodekey("bt");
	bean.setBodyLineActions(getManagedList1());
	bean.initUI();
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList1() {
	List list = new ArrayList();
	list.add(getBodyAddLineAction_713885());
	list.add(getBodyInsertLineAction_135d019());
	list.add(getBodyDelLineAction_1f12b5e());
	return list;
    }

    private nc.ui.pubapp.uif2app.actions.BodyAddLineAction getBodyAddLineAction_713885() {
	if (context.get("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#713885") != null)
	    return (nc.ui.pubapp.uif2app.actions.BodyAddLineAction) context
		    .get("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#713885");
	nc.ui.pubapp.uif2app.actions.BodyAddLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyAddLineAction();
	context.put("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#713885", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.pubapp.uif2app.actions.BodyInsertLineAction getBodyInsertLineAction_135d019() {
	if (context.get("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#135d019") != null)
	    return (nc.ui.pubapp.uif2app.actions.BodyInsertLineAction) context
		    .get("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#135d019");
	nc.ui.pubapp.uif2app.actions.BodyInsertLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyInsertLineAction();
	context.put("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#135d019", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.pubapp.uif2app.actions.BodyDelLineAction getBodyDelLineAction_1f12b5e() {
	if (context.get("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#1f12b5e") != null)
	    return (nc.ui.pubapp.uif2app.actions.BodyDelLineAction) context
		    .get("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#1f12b5e");
	nc.ui.pubapp.uif2app.actions.BodyDelLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyDelLineAction();
	context.put("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#1f12b5e", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.uif2.TangramContainer getContainer() {
	if (context.get("container") != null)
	    return (nc.ui.uif2.TangramContainer) context.get("container");
	nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
	context.put("container", bean);
	bean.setTangramLayoutRoot(getTBNode_1f982a4());
	bean.setActions(getManagedList3());
	bean.setEditActions(getManagedList4());
	bean.setModel(getBmModel());
	bean.initUI();
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_1f982a4() {
	if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#1f982a4") != null)
	    return (nc.ui.uif2.tangramlayout.node.TBNode) context.get("nc.ui.uif2.tangramlayout.node.TBNode#1f982a4");
	nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
	context.put("nc.ui.uif2.tangramlayout.node.TBNode#1f982a4", bean);
	bean.setTabs(getManagedList2());
	bean.setName("cardLayout");
	bean.setShowMode("CardLayout");
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList2() {
	List list = new ArrayList();
	list.add(getVSNode_687fd6());
	list.add(getVSNode_fa803e());
	return list;
    }

    private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_687fd6() {
	if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#687fd6") != null)
	    return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#687fd6");
	nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
	context.put("nc.ui.uif2.tangramlayout.node.VSNode#687fd6", bean);
	bean.setUp(getCNode_5639ba());
	bean.setDown(getCNode_e813bf());
	bean.setDividerLocation(32.0f);
	bean.setName("¶C™Ì");
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.CNode getCNode_5639ba() {
	if (context.get("nc.ui.uif2.tangramlayout.node.CNode#5639ba") != null)
	    return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#5639ba");
	nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
	context.put("nc.ui.uif2.tangramlayout.node.CNode#5639ba", bean);
	bean.setComponent(getOrgpanel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.CNode getCNode_e813bf() {
	if (context.get("nc.ui.uif2.tangramlayout.node.CNode#e813bf") != null)
	    return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#e813bf");
	nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
	context.put("nc.ui.uif2.tangramlayout.node.CNode#e813bf", bean);
	bean.setComponent(getBillListView());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_fa803e() {
	if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#fa803e") != null)
	    return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#fa803e");
	nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
	context.put("nc.ui.uif2.tangramlayout.node.VSNode#fa803e", bean);
	bean.setUp(getCNode_a0cf53());
	bean.setDown(getCNode_18b85c1());
	bean.setDividerLocation(43.0f);
	bean.setName("•d§˘");
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.CNode getCNode_a0cf53() {
	if (context.get("nc.ui.uif2.tangramlayout.node.CNode#a0cf53") != null)
	    return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#a0cf53");
	nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
	context.put("nc.ui.uif2.tangramlayout.node.CNode#a0cf53", bean);
	bean.setComponent(getViewb());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.tangramlayout.node.CNode getCNode_18b85c1() {
	if (context.get("nc.ui.uif2.tangramlayout.node.CNode#18b85c1") != null)
	    return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#18b85c1");
	nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
	context.put("nc.ui.uif2.tangramlayout.node.CNode#18b85c1", bean);
	bean.setComponent(getBillForm());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList3() {
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

    private List getManagedList4() {
	List list = new ArrayList();
	list.add(getSeparatorAction());
	list.add(getSaveAction());
	list.add(getCancelAction());
	return list;
    }

    public nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener getInitDataListener() {
	if (context.get("InitDataListener") != null)
	    return (nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener) context.get("InitDataListener");
	nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener bean = new nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener();
	context.put("InitDataListener", bean);
	bean.setModel(getBmModel());
	bean.setContext(getContext());
	bean.setVoClassName("nc.vo.twhr.nhicalc.AggNhiCalcVO");
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.pubapp.common.validateservice.ClosingCheck getClosingListener() {
	if (context.get("ClosingListener") != null)
	    return (nc.ui.pubapp.common.validateservice.ClosingCheck) context.get("ClosingListener");
	nc.ui.pubapp.common.validateservice.ClosingCheck bean = new nc.ui.pubapp.common.validateservice.ClosingCheck();
	context.put("ClosingListener", bean);
	bean.setModel(getBmModel());
	bean.setSaveAction(getSaveAction());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getBmModelEventMediator() {
	if (context.get("bmModelEventMediator") != null)
	    return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator) context.get("bmModelEventMediator");
	nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
	context.put("bmModelEventMediator", bean);
	bean.setModel(getBmModel());
	bean.setHandlerGroup(getManagedList5());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList5() {
	List list = new ArrayList();
	list.add(getEventHandlerGroup_5d211d());
	list.add(getEventHandlerGroup_9e054());
	list.add(getEventHandlerGroup_4ab976());
	list.add(getEventHandlerGroup_14db440());
	list.add(getEventHandlerGroup_14a8e80());
	return list;
    }

    private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_5d211d() {
	if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#5d211d") != null)
	    return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
		    .get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#5d211d");
	nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
	context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#5d211d", bean);
	bean.setEvent("nc.ui.pubapp.uif2app.event.OrgChangedEvent");
	bean.setHandler(getAceOrgChangeHandler_1f01a85());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.twhr.nhicalc.ace.handler.AceOrgChangeHandler getAceOrgChangeHandler_1f01a85() {
	if (context.get("nc.ui.twhr.nhicalc.ace.handler.AceOrgChangeHandler#1f01a85") != null)
	    return (nc.ui.twhr.nhicalc.ace.handler.AceOrgChangeHandler) context
		    .get("nc.ui.twhr.nhicalc.ace.handler.AceOrgChangeHandler#1f01a85");
	nc.ui.twhr.nhicalc.ace.handler.AceOrgChangeHandler bean = new nc.ui.twhr.nhicalc.ace.handler.AceOrgChangeHandler();
	context.put("nc.ui.twhr.nhicalc.ace.handler.AceOrgChangeHandler#1f01a85", bean);
	bean.setBillForm(getBillForm());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_9e054() {
	if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#9e054") != null)
	    return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
		    .get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#9e054");
	nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
	context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#9e054", bean);
	bean.setEvent("nc.ui.pubapp.uif2app.event.billform.AddEvent");
	bean.setHandler(getAceAddHandler_2937bb());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.twhr.nhicalc.ace.handler.AceAddHandler getAceAddHandler_2937bb() {
	if (context.get("nc.ui.twhr.nhicalc.ace.handler.AceAddHandler#2937bb") != null)
	    return (nc.ui.twhr.nhicalc.ace.handler.AceAddHandler) context
		    .get("nc.ui.twhr.nhicalc.ace.handler.AceAddHandler#2937bb");
	nc.ui.twhr.nhicalc.ace.handler.AceAddHandler bean = new nc.ui.twhr.nhicalc.ace.handler.AceAddHandler();
	context.put("nc.ui.twhr.nhicalc.ace.handler.AceAddHandler#2937bb", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_4ab976() {
	if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#4ab976") != null)
	    return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
		    .get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#4ab976");
	nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
	context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#4ab976", bean);
	bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent");
	bean.setHandler(getBodyBeforeEditHandler_66ba34());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler getBodyBeforeEditHandler_66ba34() {
	if (context.get("nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler#66ba34") != null)
	    return (nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler) context
		    .get("nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler#66ba34");
	nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler bean = new nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler();
	context.put("nc.ui.twhr.nhicalc.handler.BodyBeforeEditHandler#66ba34", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_14db440() {
	if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#14db440") != null)
	    return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
		    .get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#14db440");
	nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
	context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#14db440", bean);
	bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent");
	bean.setHandler(getBodyAfterEditHandler_1d61a22());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler getBodyAfterEditHandler_1d61a22() {
	if (context.get("nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler#1d61a22") != null)
	    return (nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler) context
		    .get("nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler#1d61a22");
	nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler bean = new nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler();
	context.put("nc.ui.twhr.nhicalc.handler.BodyAfterEditHandler#1d61a22", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_14a8e80() {
	if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#14a8e80") != null)
	    return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
		    .get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#14a8e80");
	nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
	context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#14a8e80", bean);
	bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyRowChangedEvent");
	bean.setHandler(getBodyRowChangedHandler_74e0ad());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.twhr.nhicalc.handler.BodyRowChangedHandler getBodyRowChangedHandler_74e0ad() {
	if (context.get("nc.ui.twhr.nhicalc.handler.BodyRowChangedHandler#74e0ad") != null)
	    return (nc.ui.twhr.nhicalc.handler.BodyRowChangedHandler) context
		    .get("nc.ui.twhr.nhicalc.handler.BodyRowChangedHandler#74e0ad");
	nc.ui.twhr.nhicalc.handler.BodyRowChangedHandler bean = new nc.ui.twhr.nhicalc.handler.BodyRowChangedHandler();
	context.put("nc.ui.twhr.nhicalc.handler.BodyRowChangedHandler#74e0ad", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader getBillLazilyLoader() {
	if (context.get("billLazilyLoader") != null)
	    return (nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader) context.get("billLazilyLoader");
	nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader bean = new nc.ui.pubapp.uif2app.lazilyload.DefaultBillLazilyLoader();
	context.put("billLazilyLoader", bean);
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.twhr.nhicalc.lazilyload.NhicalcLazilyLoadManager getBmModelLasilyLodadMediator() {
	if (context.get("bmModelLasilyLodadMediator") != null)
	    return (nc.ui.twhr.nhicalc.lazilyload.NhicalcLazilyLoadManager) context.get("bmModelLasilyLodadMediator");
	nc.ui.twhr.nhicalc.lazilyload.NhicalcLazilyLoadManager bean = new nc.ui.twhr.nhicalc.lazilyload.NhicalcLazilyLoadManager();
	context.put("bmModelLasilyLodadMediator", bean);
	bean.setModel(getBmModel());
	bean.setLoader(getBillLazilyLoader());
	bean.setLazilyLoadSupporter(getManagedList6());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList6() {
	List list = new ArrayList();
	list.add(getCardPanelLazilyLoad_1d1a34c());
	list.add(getListPanelLazilyLoad_37ea14());
	return list;
    }

    private nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad getCardPanelLazilyLoad_1d1a34c() {
	if (context.get("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#1d1a34c") != null)
	    return (nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad) context
		    .get("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#1d1a34c");
	nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad();
	context.put("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#1d1a34c", bean);
	bean.setBillform(getBillForm());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad getListPanelLazilyLoad_37ea14() {
	if (context.get("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#37ea14") != null)
	    return (nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad) context
		    .get("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#37ea14");
	nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad();
	context.put("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#37ea14", bean);
	bean.setListView(getBillListView());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.pubapp.uif2app.view.RowNoMediator getRowNoMediator() {
	if (context.get("rowNoMediator") != null)
	    return (nc.ui.pubapp.uif2app.view.RowNoMediator) context.get("rowNoMediator");
	nc.ui.pubapp.uif2app.view.RowNoMediator bean = new nc.ui.pubapp.uif2app.view.RowNoMediator();
	context.put("rowNoMediator", bean);
	bean.setModel(getBmModel());
	bean.setEditor(getBillForm());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator getMouseClickShowPanelMediator() {
	if (context.get("mouseClickShowPanelMediator") != null)
	    return (nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator) context.get("mouseClickShowPanelMediator");
	nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator bean = new nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator();
	context.put("mouseClickShowPanelMediator", bean);
	bean.setListView(getBillListView());
	bean.setShowUpComponent(getBillForm());
	bean.setHyperLinkColumn("null");
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.twhr.nhicalc.action.GenerateAction getGenerateAction() {
	if (context.get("generateAction") != null)
	    return (nc.ui.twhr.nhicalc.action.GenerateAction) context.get("generateAction");
	nc.ui.twhr.nhicalc.action.GenerateAction bean = new nc.ui.twhr.nhicalc.action.GenerateAction();
	context.put("generateAction", bean);
	bean.setCode("generateaction");
	bean.setOrgpanel(getOrgpanel());
	bean.setModel(getBmModel());
	bean.setEditor(getBillListView());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.twhr.nhicalc.action.QueryAction getQueryAction() {
	if (context.get("queryAction") != null)
	    return (nc.ui.twhr.nhicalc.action.QueryAction) context.get("queryAction");
	nc.ui.twhr.nhicalc.action.QueryAction bean = new nc.ui.twhr.nhicalc.action.QueryAction();
	context.put("queryAction", bean);
	bean.setCode("queryaction");
	bean.setOrgpanel(getOrgpanel());
	bean.setModel(getBmModel());
	bean.setEditor(getBillListView());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.funcnode.ui.action.MenuAction getFilterActionGroup() {
	if (context.get("filterActionGroup") != null)
	    return (nc.funcnode.ui.action.MenuAction) context.get("filterActionGroup");
	nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
	context.put("filterActionGroup", bean);
	bean.setCode("filterManage");
	bean.setName("ﬂ^ûV");
	bean.setActions(getManagedList7());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList7() {
	List list = new ArrayList();
	list.add(getShowLastMonthAction());
	return list;
    }

    public nc.ui.twhr.nhicalc.action.ShowLastMonthAction getShowLastMonthAction() {
	if (context.get("showLastMonthAction") != null)
	    return (nc.ui.twhr.nhicalc.action.ShowLastMonthAction) context.get("showLastMonthAction");
	nc.ui.twhr.nhicalc.action.ShowLastMonthAction bean = new nc.ui.twhr.nhicalc.action.ShowLastMonthAction();
	context.put("showLastMonthAction", bean);
	bean.setCode("showlastmonthaction");
	bean.setOrgpanel(getOrgpanel());
	bean.setModel(getBmModel());
	bean.setEditor(getBillListView());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.twhr.nhicalc.action.EditAction getEditAction() {
	if (context.get("editAction") != null)
	    return (nc.ui.twhr.nhicalc.action.EditAction) context.get("editAction");
	nc.ui.twhr.nhicalc.action.EditAction bean = new nc.ui.twhr.nhicalc.action.EditAction();
	context.put("editAction", bean);
	bean.setOrgpanel(getOrgpanel());
	bean.setModel(getBmModel());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.twhr.nhicalc.action.SaveAction getSaveAction() {
	if (context.get("saveAction") != null)
	    return (nc.ui.twhr.nhicalc.action.SaveAction) context.get("saveAction");
	nc.ui.twhr.nhicalc.action.SaveAction bean = new nc.ui.twhr.nhicalc.action.SaveAction();
	context.put("saveAction", bean);
	bean.setModel(getBmModel());
	bean.setService(getBmModelModelService());
	bean.setEditor(getBillForm());
	bean.setContext(getContext());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.twhr.nhicalc.action.CancelAction getCancelAction() {
	if (context.get("cancelAction") != null)
	    return (nc.ui.twhr.nhicalc.action.CancelAction) context.get("cancelAction");
	nc.ui.twhr.nhicalc.action.CancelAction bean = new nc.ui.twhr.nhicalc.action.CancelAction();
	context.put("cancelAction", bean);
	bean.setModel(getBmModel());
	bean.setEditor(getBillForm());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.twhr.nhicalc.action.CalculateAction getCalculateAction() {
	if (context.get("calculateAction") != null)
	    return (nc.ui.twhr.nhicalc.action.CalculateAction) context.get("calculateAction");
	nc.ui.twhr.nhicalc.action.CalculateAction bean = new nc.ui.twhr.nhicalc.action.CalculateAction();
	context.put("calculateAction", bean);
	bean.setCode("calculateaction");
	bean.setOrgpanel(getOrgpanel());
	bean.setModel(getBmModel());
	bean.setEditor(getBillListView());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.funcnode.ui.action.MenuAction getAuditActionGroup() {
	if (context.get("auditActionGroup") != null)
	    return (nc.funcnode.ui.action.MenuAction) context.get("auditActionGroup");
	nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
	context.put("auditActionGroup", bean);
	bean.setCode("auditManage");
	bean.setName("åè∫À");
	bean.setActions(getManagedList8());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList8() {
	List list = new ArrayList();
	list.add(getAuditaction());
	list.add(getUnauditaction());
	return list;
    }

    public nc.ui.twhr.nhicalc.action.AuditAction getAuditaction() {
	if (context.get("auditaction") != null)
	    return (nc.ui.twhr.nhicalc.action.AuditAction) context.get("auditaction");
	nc.ui.twhr.nhicalc.action.AuditAction bean = new nc.ui.twhr.nhicalc.action.AuditAction();
	context.put("auditaction", bean);
	bean.setCode("auditaction");
	bean.setOrgpanel(getOrgpanel());
	bean.setModel(getBmModel());
	bean.setEditor(getBillListView());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.twhr.nhicalc.action.UnAuditAction getUnauditaction() {
	if (context.get("unauditaction") != null)
	    return (nc.ui.twhr.nhicalc.action.UnAuditAction) context.get("unauditaction");
	nc.ui.twhr.nhicalc.action.UnAuditAction bean = new nc.ui.twhr.nhicalc.action.UnAuditAction();
	context.put("unauditaction", bean);
	bean.setCode("unauditaction");
	bean.setOrgpanel(getOrgpanel());
	bean.setModel(getBmModel());
	bean.setEditor(getBillListView());
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

    public nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator getDoubleClickMediator() {
	if (context.get("doubleClickMediator") != null)
	    return (nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator) context.get("doubleClickMediator");
	nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator bean = new nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator();
	context.put("doubleClickMediator", bean);
	bean.setListView(getBillListView());
	bean.setShowUpComponent(getBillForm());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowListInterceptor() {
	if (context.get("showListInterceptor") != null)
	    return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor) context
		    .get("showListInterceptor");
	nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
	context.put("showListInterceptor", bean);
	bean.setShowUpComponent(getBillListView());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowCardInterceptor() {
	if (context.get("showCardInterceptor") != null)
	    return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor) context
		    .get("showCardInterceptor");
	nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
	context.put("showCardInterceptor", bean);
	bean.setShowUpComponent(getBillForm());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

}
