package nc.ui.ta.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class tateam_config extends AbstractJavaBeanDefinition {
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

	public nc.ui.pubapp.uif2app.model.BillManageModel getManageAppModel() {
		if (context.get("manageAppModel") != null)
			return (nc.ui.pubapp.uif2app.model.BillManageModel) context.get("manageAppModel");
		nc.ui.pubapp.uif2app.model.BillManageModel bean = new nc.ui.pubapp.uif2app.model.BillManageModel();
		context.put("manageAppModel", bean);
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.value.AggVOMetaBDObjectAdapterFactory getBoadatorfactory() {
		if (context.get("boadatorfactory") != null)
			return (nc.ui.pubapp.uif2app.view.value.AggVOMetaBDObjectAdapterFactory) context.get("boadatorfactory");
		nc.ui.pubapp.uif2app.view.value.AggVOMetaBDObjectAdapterFactory bean = new nc.ui.pubapp.uif2app.view.value.AggVOMetaBDObjectAdapterFactory();
		context.put("boadatorfactory", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.query2.model.ModelDataManager getModelDataManager() {
		if (context.get("modelDataManager") != null)
			return (nc.ui.pubapp.uif2app.query2.model.ModelDataManager) context.get("modelDataManager");
		nc.ui.pubapp.uif2app.query2.model.ModelDataManager bean = new nc.ui.pubapp.uif2app.query2.model.ModelDataManager();
		context.put("modelDataManager", bean);
		bean.setModel(getManageAppModel());
		bean.setService(getQueryProxy());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.bd.team.team01.serviceproxy.TeamQueryService getQueryProxy() {
		if (context.get("queryProxy") != null)
			return (nc.ui.bd.team.team01.serviceproxy.TeamQueryService) context.get("queryProxy");
		nc.ui.bd.team.team01.serviceproxy.TeamQueryService bean = new nc.ui.bd.team.team01.serviceproxy.TeamQueryService();
		context.put("queryProxy", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.bd.team.team01.serviceproxy.TeamDeleteService getDeleteProxy() {
		if (context.get("deleteProxy") != null)
			return (nc.ui.bd.team.team01.serviceproxy.TeamDeleteService) context.get("deleteProxy");
		nc.ui.bd.team.team01.serviceproxy.TeamDeleteService bean = new nc.ui.bd.team.team01.serviceproxy.TeamDeleteService();
		context.put("deleteProxy", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.bd.team.team01.serviceproxy.TeamMaintainService getMaintainProxy() {
		if (context.get("maintainProxy") != null)
			return (nc.ui.bd.team.team01.serviceproxy.TeamMaintainService) context.get("maintainProxy");
		nc.ui.bd.team.team01.serviceproxy.TeamMaintainService bean = new nc.ui.bd.team.team01.serviceproxy.TeamMaintainService();
		context.put("maintainProxy", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell getQueryArea() {
		if (context.get("queryArea") != null)
			return (nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell) context.get("queryArea");
		nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell bean = new nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell();
		context.put("queryArea", bean);
		bean.setQueryArea(getQueryAction_created_1969f1d());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.queryarea.QueryArea getQueryAction_created_1969f1d() {
		if (context.get("queryAction.created#1969f1d") != null)
			return (nc.ui.queryarea.QueryArea) context.get("queryAction.created#1969f1d");
		nc.ui.queryarea.QueryArea bean = getQueryAction().createQueryArea();
		context.put("queryAction.created#1969f1d", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getQueryInfo() {
		if (context.get("queryInfo") != null)
			return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel) context.get("queryInfo");
		nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
		context.put("queryInfo", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.team.view.TATeamBillListView getListView() {
		if (context.get("listView") != null)
			return (nc.ui.ta.team.view.TATeamBillListView) context.get("listView");
		nc.ui.ta.team.view.TATeamBillListView bean = new nc.ui.ta.team.view.TATeamBillListView();
		context.put("listView", bean);
		bean.setModel(getManageAppModel());
		bean.setMultiSelectionEnable(true);
		bean.setTemplateContainer(getTemplateContainer());
		bean.setUserdefitemListPreparator(getCompositeBillListDataPrepare_1e25295());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare getCompositeBillListDataPrepare_1e25295() {
		if (context.get("nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare#1e25295") != null)
			return (nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare) context.get("nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare#1e25295");
		nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare bean = new nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare();
		context.put("nc.ui.pubapp.uif2app.view.CompositeBillListDataPrepare#1e25295", bean);
		bean.setBillListDataPrepares(getManagedList0());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList0() {
		List list = new ArrayList();
		list.add(getUserdefitemlistPreparator());
		return list;
	}

	public nc.ui.bd.team.team01.view.TeamBillCardForm getBillFormEditor() {
		if (context.get("billFormEditor") != null)
			return (nc.ui.bd.team.team01.view.TeamBillCardForm) context.get("billFormEditor");
		nc.ui.bd.team.team01.view.TeamBillCardForm bean = new nc.ui.bd.team.team01.view.TeamBillCardForm();
		context.put("billFormEditor", bean);
		bean.setShowTotalLine(false);
		bean.setRequestFocus(false);
		bean.setBillOrgPanel(getBUPanel());
		bean.setTemplateNotNullValidate(true);
		bean.setModel(getManageAppModel());
		bean.setTemplateContainer(getTemplateContainer());
		bean.setBodyLineActions(getManagedList1());
		bean.setAutoAddLine(true);
		bean.setBlankChildrenFilter(getSingleFieldBlankChildrenFilter_1dc3d70());
		bean.setUserdefitemPreparator(getCompositeBillDataPrepare_459da2());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList1() {
		List list = new ArrayList();
		list.add(getBodyAddLineAction_142c92a());
		list.add(getBodyInsertLineAction_1a51334());
		list.add(getBodyDelLineAction_8ffb5d());
		list.add(getBodyCopyLineAction_bfc832());
		list.add(getBodyPasteLineAction_f05128());
		list.add(getBodyPasteToTailAction());
		list.add(getActionsBar_ActionsBarSeparator_1601a99());
		list.add(getBodyLineEditAction());
		list.add(getActionsBar_ActionsBarSeparator_726bef());
		list.add(getDefaultBodyZoomAction_c1f51());
		return list;
	}

	private nc.ui.pubapp.uif2app.actions.BodyAddLineAction getBodyAddLineAction_142c92a() {
		if (context.get("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#142c92a") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyAddLineAction) context.get("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#142c92a");
		nc.ui.pubapp.uif2app.actions.BodyAddLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyAddLineAction();
		context.put("nc.ui.pubapp.uif2app.actions.BodyAddLineAction#142c92a", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.BodyInsertLineAction getBodyInsertLineAction_1a51334() {
		if (context.get("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#1a51334") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyInsertLineAction) context.get("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#1a51334");
		nc.ui.pubapp.uif2app.actions.BodyInsertLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyInsertLineAction();
		context.put("nc.ui.pubapp.uif2app.actions.BodyInsertLineAction#1a51334", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.BodyDelLineAction getBodyDelLineAction_8ffb5d() {
		if (context.get("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#8ffb5d") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyDelLineAction) context.get("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#8ffb5d");
		nc.ui.pubapp.uif2app.actions.BodyDelLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyDelLineAction();
		context.put("nc.ui.pubapp.uif2app.actions.BodyDelLineAction#8ffb5d", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.BodyCopyLineAction getBodyCopyLineAction_bfc832() {
		if (context.get("nc.ui.pubapp.uif2app.actions.BodyCopyLineAction#bfc832") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyCopyLineAction) context.get("nc.ui.pubapp.uif2app.actions.BodyCopyLineAction#bfc832");
		nc.ui.pubapp.uif2app.actions.BodyCopyLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyCopyLineAction();
		context.put("nc.ui.pubapp.uif2app.actions.BodyCopyLineAction#bfc832", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.BodyPasteLineAction getBodyPasteLineAction_f05128() {
		if (context.get("nc.ui.pubapp.uif2app.actions.BodyPasteLineAction#f05128") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyPasteLineAction) context.get("nc.ui.pubapp.uif2app.actions.BodyPasteLineAction#f05128");
		nc.ui.pubapp.uif2app.actions.BodyPasteLineAction bean = new nc.ui.pubapp.uif2app.actions.BodyPasteLineAction();
		context.put("nc.ui.pubapp.uif2app.actions.BodyPasteLineAction#f05128", bean);
		bean.setClearItems(getManagedList2());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add("cteam_bid");
		return list;
	}

	private nc.ui.pub.beans.ActionsBar.ActionsBarSeparator getActionsBar_ActionsBarSeparator_1601a99() {
		if (context.get("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#1601a99") != null)
			return (nc.ui.pub.beans.ActionsBar.ActionsBarSeparator) context.get("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#1601a99");
		nc.ui.pub.beans.ActionsBar.ActionsBarSeparator bean = new nc.ui.pub.beans.ActionsBar.ActionsBarSeparator();
		context.put("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#1601a99", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pub.beans.ActionsBar.ActionsBarSeparator getActionsBar_ActionsBarSeparator_726bef() {
		if (context.get("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#726bef") != null)
			return (nc.ui.pub.beans.ActionsBar.ActionsBarSeparator) context.get("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#726bef");
		nc.ui.pub.beans.ActionsBar.ActionsBarSeparator bean = new nc.ui.pub.beans.ActionsBar.ActionsBarSeparator();
		context.put("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#726bef", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction getDefaultBodyZoomAction_c1f51() {
		if (context.get("nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction#c1f51") != null)
			return (nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction) context.get("nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction#c1f51");
		nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction bean = new nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction();
		context.put("nc.ui.pubapp.uif2app.actions.DefaultBodyZoomAction#c1f51", bean);
		bean.setPos(1);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter getSingleFieldBlankChildrenFilter_1dc3d70() {
		if (context.get("nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter#1dc3d70") != null)
			return (nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter) context.get("nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter#1dc3d70");
		nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter bean = new nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter();
		context.put("nc.ui.pubapp.uif2app.view.value.SingleFieldBlankChildrenFilter#1dc3d70", bean);
		bean.setFieldName("cworkmanid");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare getCompositeBillDataPrepare_459da2() {
		if (context.get("nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare#459da2") != null)
			return (nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare) context.get("nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare#459da2");
		nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare bean = new nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare();
		context.put("nc.ui.pubapp.uif2app.view.CompositeBillDataPrepare#459da2", bean);
		bean.setBillDataPrepares(getManagedList3());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList3() {
		List list = new ArrayList();
		list.add(getUserdefitemPreparator());
		return list;
	}

	public nc.ui.pubapp.uif2app.actions.RearrangeRowNoBodyLineAction getRearrangeRowNoBodyLineAction() {
		if (context.get("rearrangeRowNoBodyLineAction") != null)
			return (nc.ui.pubapp.uif2app.actions.RearrangeRowNoBodyLineAction) context.get("rearrangeRowNoBodyLineAction");
		nc.ui.pubapp.uif2app.actions.RearrangeRowNoBodyLineAction bean = new nc.ui.pubapp.uif2app.actions.RearrangeRowNoBodyLineAction();
		context.put("rearrangeRowNoBodyLineAction", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.BodyLineEditAction getBodyLineEditAction() {
		if (context.get("bodyLineEditAction") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyLineEditAction) context.get("bodyLineEditAction");
		nc.ui.pubapp.uif2app.actions.BodyLineEditAction bean = new nc.ui.pubapp.uif2app.actions.BodyLineEditAction();
		context.put("bodyLineEditAction", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.BodyPasteToTailAction getBodyPasteToTailAction() {
		if (context.get("bodyPasteToTailAction") != null)
			return (nc.ui.pubapp.uif2app.actions.BodyPasteToTailAction) context.get("bodyPasteToTailAction");
		nc.ui.pubapp.uif2app.actions.BodyPasteToTailAction bean = new nc.ui.pubapp.uif2app.actions.BodyPasteToTailAction();
		context.put("bodyPasteToTailAction", bean);
		bean.setClearItems(getManagedList4());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList4() {
		List list = new ArrayList();
		list.add("cteam_bid");
		return list;
	}

	public nc.ui.uif2.userdefitem.UserDefItemContainer getUserdefitemContainer() {
		if (context.get("userdefitemContainer") != null)
			return (nc.ui.uif2.userdefitem.UserDefItemContainer) context.get("userdefitemContainer");
		nc.ui.uif2.userdefitem.UserDefItemContainer bean = new nc.ui.uif2.userdefitem.UserDefItemContainer();
		context.put("userdefitemContainer", bean);
		bean.setContext(getContext());
		bean.setParams(getManagedList5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList5() {
		List list = new ArrayList();
		list.add(getQueryParam_70314a());
		list.add(getQueryParam_1b40e38());
		return list;
	}

	private nc.ui.uif2.userdefitem.QueryParam getQueryParam_70314a() {
		if (context.get("nc.ui.uif2.userdefitem.QueryParam#70314a") != null)
			return (nc.ui.uif2.userdefitem.QueryParam) context.get("nc.ui.uif2.userdefitem.QueryParam#70314a");
		nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
		context.put("nc.ui.uif2.userdefitem.QueryParam#70314a", bean);
		bean.setMdfullname("mmbd.bd_team");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.userdefitem.QueryParam getQueryParam_1b40e38() {
		if (context.get("nc.ui.uif2.userdefitem.QueryParam#1b40e38") != null)
			return (nc.ui.uif2.userdefitem.QueryParam) context.get("nc.ui.uif2.userdefitem.QueryParam#1b40e38");
		nc.ui.uif2.userdefitem.QueryParam bean = new nc.ui.uif2.userdefitem.QueryParam();
		context.put("nc.ui.uif2.userdefitem.QueryParam#1b40e38", bean);
		bean.setMdfullname("mmbd.bd_team_b");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.editor.UserdefitemContainerListPreparator getUserdefitemlistPreparator() {
		if (context.get("userdefitemlistPreparator") != null)
			return (nc.ui.uif2.editor.UserdefitemContainerListPreparator) context.get("userdefitemlistPreparator");
		nc.ui.uif2.editor.UserdefitemContainerListPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerListPreparator();
		context.put("userdefitemlistPreparator", bean);
		bean.setContainer(getUserdefitemContainer());
		bean.setParams(getManagedList6());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList6() {
		List list = new ArrayList();
		list.add(getUserdefQueryParam_11a662());
		list.add(getUserdefQueryParam_c2a14e());
		return list;
	}

	private nc.ui.uif2.editor.UserdefQueryParam getUserdefQueryParam_11a662() {
		if (context.get("nc.ui.uif2.editor.UserdefQueryParam#11a662") != null)
			return (nc.ui.uif2.editor.UserdefQueryParam) context.get("nc.ui.uif2.editor.UserdefQueryParam#11a662");
		nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
		context.put("nc.ui.uif2.editor.UserdefQueryParam#11a662", bean);
		bean.setMdfullname("mmbd.bd_team");
		bean.setPos(0);
		bean.setPrefix("vdef");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.editor.UserdefQueryParam getUserdefQueryParam_c2a14e() {
		if (context.get("nc.ui.uif2.editor.UserdefQueryParam#c2a14e") != null)
			return (nc.ui.uif2.editor.UserdefQueryParam) context.get("nc.ui.uif2.editor.UserdefQueryParam#c2a14e");
		nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
		context.put("nc.ui.uif2.editor.UserdefQueryParam#c2a14e", bean);
		bean.setMdfullname("mmbd.bd_team_b");
		bean.setPos(1);
		bean.setPrefix("vdef");
		bean.setTabcode("items");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.editor.UserdefitemContainerPreparator getUserdefitemPreparator() {
		if (context.get("userdefitemPreparator") != null)
			return (nc.ui.uif2.editor.UserdefitemContainerPreparator) context.get("userdefitemPreparator");
		nc.ui.uif2.editor.UserdefitemContainerPreparator bean = new nc.ui.uif2.editor.UserdefitemContainerPreparator();
		context.put("userdefitemPreparator", bean);
		bean.setContainer(getUserdefitemContainer());
		bean.setParams(getManagedList7());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList7() {
		List list = new ArrayList();
		list.add(getUserdefQueryParam_1ec387c());
		list.add(getUserdefQueryParam_17073a0());
		return list;
	}

	private nc.ui.uif2.editor.UserdefQueryParam getUserdefQueryParam_1ec387c() {
		if (context.get("nc.ui.uif2.editor.UserdefQueryParam#1ec387c") != null)
			return (nc.ui.uif2.editor.UserdefQueryParam) context.get("nc.ui.uif2.editor.UserdefQueryParam#1ec387c");
		nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
		context.put("nc.ui.uif2.editor.UserdefQueryParam#1ec387c", bean);
		bean.setMdfullname("mmbd.bd_team");
		bean.setPos(0);
		bean.setPrefix("vdef");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.editor.UserdefQueryParam getUserdefQueryParam_17073a0() {
		if (context.get("nc.ui.uif2.editor.UserdefQueryParam#17073a0") != null)
			return (nc.ui.uif2.editor.UserdefQueryParam) context.get("nc.ui.uif2.editor.UserdefQueryParam#17073a0");
		nc.ui.uif2.editor.UserdefQueryParam bean = new nc.ui.uif2.editor.UserdefQueryParam();
		context.put("nc.ui.uif2.editor.UserdefQueryParam#17073a0", bean);
		bean.setMdfullname("mmbd.bd_team_b");
		bean.setPos(1);
		bean.setPrefix("vdef");
		bean.setTabcode("items");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.bd.team.team01.view.TeamBillOrgPanel getBUPanel() {
		if (context.get("BUPanel") != null)
			return (nc.ui.bd.team.team01.view.TeamBillOrgPanel) context.get("BUPanel");
		nc.ui.bd.team.team01.view.TeamBillOrgPanel bean = new nc.ui.bd.team.team01.view.TeamBillOrgPanel();
		context.put("BUPanel", bean);
		bean.setModel(getManageAppModel());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.mmbd.uif.app.view.MMOrgChangeMediator getTeamMediator() {
		if (context.get("teamMediator") != null)
			return (nc.ui.mmbd.uif.app.view.MMOrgChangeMediator) context.get("teamMediator");
		nc.ui.mmbd.uif.app.view.MMOrgChangeMediator bean = new nc.ui.mmbd.uif.app.view.MMOrgChangeMediator();
		context.put("teamMediator", bean);
		bean.setModel(getManageAppModel());
		bean.setBillFormEditor(getBillFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator getMouseClickShowPanelMediator() {
		if (context.get("mouseClickShowPanelMediator") != null)
			return (nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator) context.get("mouseClickShowPanelMediator");
		nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator bean = new nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator();
		context.put("mouseClickShowPanelMediator", bean);
		bean.setListView(getListView());
		bean.setShowUpComponent(getBillFormEditor());
		bean.setHyperLinkColumn("vteamcode");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.BillBodySortMediator getBillBodySortMediator() {
		if (context.get("billBodySortMediator") != null)
			return (nc.ui.pubapp.uif2app.model.BillBodySortMediator) context.get("billBodySortMediator");
		nc.ui.pubapp.uif2app.model.BillBodySortMediator bean = new nc.ui.pubapp.uif2app.model.BillBodySortMediator(getManageAppModel(), getBillFormEditor(), getListView());
		context.put("billBodySortMediator", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.event.ChildrenPicky getChildrenPicky() {
		if (context.get("childrenPicky") != null)
			return (nc.ui.pubapp.uif2app.event.ChildrenPicky) context.get("childrenPicky");
		nc.ui.pubapp.uif2app.event.ChildrenPicky bean = new nc.ui.pubapp.uif2app.event.ChildrenPicky();
		context.put("childrenPicky", bean);
		bean.setBillform(getBillFormEditor());
		bean.setBodyVoClasses(getManagedList8());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList8() {
		List list = new ArrayList();
		list.add("nc.vo.bd.team.team01.entity.TeamItemVO");
		return list;
	}

	public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getHandleMediator() {
		if (context.get("handleMediator") != null)
			return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator) context.get("handleMediator");
		nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
		context.put("handleMediator", bean);
		bean.setModel(getManageAppModel());
		bean.setHandlerGroup(getManagedList9());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList9() {
		List list = new ArrayList();
		list.add(getEventHandlerGroup_13e497b());
		list.add(getEventHandlerGroup_1b00628());
		list.add(getEventHandlerGroup_18d53c1());
		list.add(getEventHandlerGroup_67449a());
		list.add(getEventHandlerGroup_1b3d2f0());
		list.add(getEventHandlerGroup_1a9884f());
		list.add(getEventHandlerGroup_5eaad2());
		return list;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_13e497b() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#13e497b") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#13e497b");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#13e497b", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.OrgChangedEvent");
		bean.setHandler(getTeamOrgChangedHandler_1fecce6());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.bd.team.team01.handler.TeamOrgChangedHandler getTeamOrgChangedHandler_1fecce6() {
		if (context.get("nc.ui.bd.team.team01.handler.TeamOrgChangedHandler#1fecce6") != null)
			return (nc.ui.bd.team.team01.handler.TeamOrgChangedHandler) context.get("nc.ui.bd.team.team01.handler.TeamOrgChangedHandler#1fecce6");
		nc.ui.bd.team.team01.handler.TeamOrgChangedHandler bean = new nc.ui.bd.team.team01.handler.TeamOrgChangedHandler();
		context.put("nc.ui.bd.team.team01.handler.TeamOrgChangedHandler#1fecce6", bean);
		bean.setBillForm(getBillFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_1b00628() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1b00628") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1b00628");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1b00628", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent");
		bean.setHandler(getTeamCardHeadTailBeforeEditHandler_4a4bb3());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.bd.team.team01.handler.TeamCardHeadTailBeforeEditHandler getTeamCardHeadTailBeforeEditHandler_4a4bb3() {
		if (context.get("nc.ui.bd.team.team01.handler.TeamCardHeadTailBeforeEditHandler#4a4bb3") != null)
			return (nc.ui.bd.team.team01.handler.TeamCardHeadTailBeforeEditHandler) context.get("nc.ui.bd.team.team01.handler.TeamCardHeadTailBeforeEditHandler#4a4bb3");
		nc.ui.bd.team.team01.handler.TeamCardHeadTailBeforeEditHandler bean = new nc.ui.bd.team.team01.handler.TeamCardHeadTailBeforeEditHandler();
		context.put("nc.ui.bd.team.team01.handler.TeamCardHeadTailBeforeEditHandler#4a4bb3", bean);
		bean.initMap();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_18d53c1() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#18d53c1") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#18d53c1");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#18d53c1", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent");
		bean.setHandler(getTeamCardHeadTailAfterEditHandler_1644386());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.bd.team.team01.handler.TeamCardHeadTailAfterEditHandler getTeamCardHeadTailAfterEditHandler_1644386() {
		if (context.get("nc.ui.bd.team.team01.handler.TeamCardHeadTailAfterEditHandler#1644386") != null)
			return (nc.ui.bd.team.team01.handler.TeamCardHeadTailAfterEditHandler) context.get("nc.ui.bd.team.team01.handler.TeamCardHeadTailAfterEditHandler#1644386");
		nc.ui.bd.team.team01.handler.TeamCardHeadTailAfterEditHandler bean = new nc.ui.bd.team.team01.handler.TeamCardHeadTailAfterEditHandler();
		context.put("nc.ui.bd.team.team01.handler.TeamCardHeadTailAfterEditHandler#1644386", bean);
		bean.initMap();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_67449a() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#67449a") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#67449a");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#67449a", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent");
		bean.setPicky(getChildrenPicky());
		bean.setHandler(getTeamCardBodyAfterEditHandler_175abe1());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.bd.team.team01.handler.TeamCardBodyAfterEditHandler getTeamCardBodyAfterEditHandler_175abe1() {
		if (context.get("nc.ui.bd.team.team01.handler.TeamCardBodyAfterEditHandler#175abe1") != null)
			return (nc.ui.bd.team.team01.handler.TeamCardBodyAfterEditHandler) context.get("nc.ui.bd.team.team01.handler.TeamCardBodyAfterEditHandler#175abe1");
		nc.ui.bd.team.team01.handler.TeamCardBodyAfterEditHandler bean = new nc.ui.bd.team.team01.handler.TeamCardBodyAfterEditHandler();
		context.put("nc.ui.bd.team.team01.handler.TeamCardBodyAfterEditHandler#175abe1", bean);
		bean.initMap();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_1b3d2f0() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1b3d2f0") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1b3d2f0");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1b3d2f0", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyBeforeEditEvent");
		bean.setPicky(getChildrenPicky());
		bean.setHandler(getTeamCardBodyBeforeEditHandler_1c8fa77());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.bd.team.team01.handler.TeamCardBodyBeforeEditHandler getTeamCardBodyBeforeEditHandler_1c8fa77() {
		if (context.get("nc.ui.bd.team.team01.handler.TeamCardBodyBeforeEditHandler#1c8fa77") != null)
			return (nc.ui.bd.team.team01.handler.TeamCardBodyBeforeEditHandler) context.get("nc.ui.bd.team.team01.handler.TeamCardBodyBeforeEditHandler#1c8fa77");
		nc.ui.bd.team.team01.handler.TeamCardBodyBeforeEditHandler bean = new nc.ui.bd.team.team01.handler.TeamCardBodyBeforeEditHandler();
		context.put("nc.ui.bd.team.team01.handler.TeamCardBodyBeforeEditHandler#1c8fa77", bean);
		bean.initMap();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_1a9884f() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1a9884f") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1a9884f");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#1a9884f", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardBodyAfterRowEditEvent");
		bean.setHandler(getTeamCardBodyAfterRowEditHandler_dd9de6());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.bd.team.team01.handler.TeamCardBodyAfterRowEditHandler getTeamCardBodyAfterRowEditHandler_dd9de6() {
		if (context.get("nc.ui.bd.team.team01.handler.TeamCardBodyAfterRowEditHandler#dd9de6") != null)
			return (nc.ui.bd.team.team01.handler.TeamCardBodyAfterRowEditHandler) context.get("nc.ui.bd.team.team01.handler.TeamCardBodyAfterRowEditHandler#dd9de6");
		nc.ui.bd.team.team01.handler.TeamCardBodyAfterRowEditHandler bean = new nc.ui.bd.team.team01.handler.TeamCardBodyAfterRowEditHandler();
		context.put("nc.ui.bd.team.team01.handler.TeamCardBodyAfterRowEditHandler#dd9de6", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_5eaad2() {
		if (context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#5eaad2") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#5eaad2");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#5eaad2", bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.billform.AddEvent");
		bean.setHandler(getTeamAddEventHandler_13405a5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.bd.team.team01.handler.TeamAddEventHandler getTeamAddEventHandler_13405a5() {
		if (context.get("nc.ui.bd.team.team01.handler.TeamAddEventHandler#13405a5") != null)
			return (nc.ui.bd.team.team01.handler.TeamAddEventHandler) context.get("nc.ui.bd.team.team01.handler.TeamAddEventHandler#13405a5");
		nc.ui.bd.team.team01.handler.TeamAddEventHandler bean = new nc.ui.bd.team.team01.handler.TeamAddEventHandler();
		context.put("nc.ui.bd.team.team01.handler.TeamAddEventHandler#13405a5", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel getCardInfoPnl() {
		if (context.get("cardInfoPnl") != null)
			return (nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel) context.get("cardInfoPnl");
		nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel bean = new nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel();
		context.put("cardInfoPnl", bean);
		bean.setActions(getManagedList10());
		bean.setTitleAction(getReturnaction());
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList10() {
		List list = new ArrayList();
		list.add(getFirstLineAction());
		list.add(getPreLineAction());
		list.add(getNextLineAction());
		list.add(getLastLineAction());
		list.add(getActionsBar_ActionsBarSeparator_120dad3());
		list.add(getHeadZoomAction());
		return list;
	}

	private nc.ui.uif2.actions.FirstLineAction getFirstLineAction() {
		if (context.get("firstLineAction") != null)
			return (nc.ui.uif2.actions.FirstLineAction) context.get("firstLineAction");
		nc.ui.uif2.actions.FirstLineAction bean = new nc.ui.uif2.actions.FirstLineAction();
		context.put("firstLineAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.actions.PreLineAction getPreLineAction() {
		if (context.get("preLineAction") != null)
			return (nc.ui.uif2.actions.PreLineAction) context.get("preLineAction");
		nc.ui.uif2.actions.PreLineAction bean = new nc.ui.uif2.actions.PreLineAction();
		context.put("preLineAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.actions.NextLineAction getNextLineAction() {
		if (context.get("nextLineAction") != null)
			return (nc.ui.uif2.actions.NextLineAction) context.get("nextLineAction");
		nc.ui.uif2.actions.NextLineAction bean = new nc.ui.uif2.actions.NextLineAction();
		context.put("nextLineAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.actions.LastLineAction getLastLineAction() {
		if (context.get("lastLineAction") != null)
			return (nc.ui.uif2.actions.LastLineAction) context.get("lastLineAction");
		nc.ui.uif2.actions.LastLineAction bean = new nc.ui.uif2.actions.LastLineAction();
		context.put("lastLineAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pub.beans.ActionsBar.ActionsBarSeparator getActionsBar_ActionsBarSeparator_120dad3() {
		if (context.get("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#120dad3") != null)
			return (nc.ui.pub.beans.ActionsBar.ActionsBarSeparator) context.get("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#120dad3");
		nc.ui.pub.beans.ActionsBar.ActionsBarSeparator bean = new nc.ui.pub.beans.ActionsBar.ActionsBarSeparator();
		context.put("nc.ui.pub.beans.ActionsBar.ActionsBarSeparator#120dad3", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.DefaultHeadZoomAction getHeadZoomAction() {
		if (context.get("headZoomAction") != null)
			return (nc.ui.pubapp.uif2app.actions.DefaultHeadZoomAction) context.get("headZoomAction");
		nc.ui.pubapp.uif2app.actions.DefaultHeadZoomAction bean = new nc.ui.pubapp.uif2app.actions.DefaultHeadZoomAction();
		context.put("headZoomAction", bean);
		bean.setBillForm(getBillFormEditor());
		bean.setModel(getManageAppModel());
		bean.setPos(0);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.UEReturnAction getReturnaction() {
		if (context.get("returnaction") != null)
			return (nc.ui.pubapp.uif2app.actions.UEReturnAction) context.get("returnaction");
		nc.ui.pubapp.uif2app.actions.UEReturnAction bean = new nc.ui.pubapp.uif2app.actions.UEReturnAction();
		context.put("returnaction", bean);
		bean.setGoComponent(getListView());
		bean.setSaveAction(getSaveAction());
		bean.setModel(getManageAppModel());
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
		bean.load();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller getRemoteCallCombinatorCaller() {
		if (context.get("remoteCallCombinatorCaller") != null)
			return (nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller) context.get("remoteCallCombinatorCaller");
		nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller bean = new nc.ui.uif2.editor.UIF2RemoteCallCombinatorCaller();
		context.put("remoteCallCombinatorCaller", bean);
		bean.setRemoteCallers(getManagedList11());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList11() {
		List list = new ArrayList();
		list.add(getQueryTemplateContainer());
		list.add(getTemplateContainer());
		list.add(getUserdefitemContainer());
		return list;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setModel(getManageAppModel());
		bean.setTangramLayoutRoot(getTBNode_c136f5());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_c136f5() {
		if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#c136f5") != null)
			return (nc.ui.uif2.tangramlayout.node.TBNode) context.get("nc.ui.uif2.tangramlayout.node.TBNode#c136f5");
		nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
		context.put("nc.ui.uif2.tangramlayout.node.TBNode#c136f5", bean);
		bean.setShowMode("CardLayout");
		bean.setTabs(getManagedList12());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList12() {
		List list = new ArrayList();
		list.add(getHSNode_15faff5());
		list.add(getVSNode_1b0900c());
		return list;
	}

	private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_15faff5() {
		if (context.get("nc.ui.uif2.tangramlayout.node.HSNode#15faff5") != null)
			return (nc.ui.uif2.tangramlayout.node.HSNode) context.get("nc.ui.uif2.tangramlayout.node.HSNode#15faff5");
		nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
		context.put("nc.ui.uif2.tangramlayout.node.HSNode#15faff5", bean);
		bean.setLeft(getCNode_1fe8683());
		bean.setRight(getVSNode_136b7d7());
		bean.setDividerLocation(0.22f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1fe8683() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1fe8683") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1fe8683");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1fe8683", bean);
		bean.setComponent(getQueryArea());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_136b7d7() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#136b7d7") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#136b7d7");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#136b7d7", bean);
		bean.setUp(getCNode_1312b7c());
		bean.setDown(getCNode_11e0282());
		bean.setDividerLocation(25f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1312b7c() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1312b7c") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1312b7c");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1312b7c", bean);
		bean.setComponent(getQueryInfo());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_11e0282() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#11e0282") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#11e0282");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#11e0282", bean);
		bean.setName("列表");
		bean.setComponent(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1b0900c() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#1b0900c") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#1b0900c");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#1b0900c", bean);
		bean.setUp(getCNode_1193d1a());
		bean.setDown(getCNode_1fa9a6d());
		bean.setDividerLocation(30f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1193d1a() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1193d1a") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1193d1a");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1193d1a", bean);
		bean.setComponent(getCardInfoPnl());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1fa9a6d() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1fa9a6d") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1fa9a6d");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1fa9a6d", bean);
		bean.setName("卡片");
		bean.setComponent(getBillFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowCardInterceptor() {
		if (context.get("showCardInterceptor") != null)
			return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor) context.get("showCardInterceptor");
		nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
		context.put("showCardInterceptor", bean);
		bean.setShowUpComponent(getBillFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowListInterceptor() {
		if (context.get("showListInterceptor") != null)
			return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor) context.get("showListInterceptor");
		nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
		context.put("showListInterceptor", bean);
		bean.setShowUpComponent(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors() {
		if (context.get("toftpanelActionContributors") != null)
			return (nc.ui.uif2.actions.ActionContributors) context.get("toftpanelActionContributors");
		nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
		context.put("toftpanelActionContributors", bean);
		bean.setContributors(getManagedList13());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList13() {
		List list = new ArrayList();
		list.add(getActionsOfList());
		list.add(getActionsOfCard());
		return list;
	}

	public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfList() {
		if (context.get("actionsOfList") != null)
			return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context.get("actionsOfList");
		nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getListView());
		context.put("actionsOfList", bean);
		bean.setActions(getManagedList14());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList14() {
		List list = new ArrayList();
		list.add(getAddAction());
		list.add(getEditAction());
		list.add(getDeleteAction());
		list.add(getSeparate());
		list.add(getQueryAction());
		list.add(getRefreshAction());
		list.add(getSeparate());
		list.add(getEnableActionMenu());
		list.add(getSeparate());
		list.add(getPrintMenuAction());
		list.add(getSeparate());
		return list;
	}

	public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getActionsOfCard() {
		if (context.get("actionsOfCard") != null)
			return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context.get("actionsOfCard");
		nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getBillFormEditor());
		context.put("actionsOfCard", bean);
		bean.setModel(getManageAppModel());
		bean.setActions(getManagedList15());
		bean.setEditActions(getManagedList16());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList15() {
		List list = new ArrayList();
		list.add(getAddAction());
		list.add(getEditAction());
		list.add(getDeleteAction());
		list.add(getSeparate());
		list.add(getQueryAction());
		list.add(getCardRefreshAction());
		list.add(getSeparate());
		list.add(getEnableActionMenu());
		list.add(getSeparate());
		list.add(getPrintMenuAction());
		return list;
	}

	private List getManagedList16() {
		List list = new ArrayList();
		list.add(getSaveAction());
		list.add(getSaveAddAction());
		list.add(getSeparate());
		list.add(getCancelAction());
		return list;
	}

	public nc.funcnode.ui.action.GroupAction getPrintMenuAction() {
		if (context.get("printMenuAction") != null)
			return (nc.funcnode.ui.action.GroupAction) context.get("printMenuAction");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("printMenuAction", bean);
		bean.setCode("printMenuAction");
		bean.setName(getI18nFB_ff3e66());
		bean.setTooltip(getI18nFB_159a341());
		bean.setActions(getManagedList17());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_ff3e66() {
		if (context.get("nc.ui.uif2.I18nFB#ff3e66") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#ff3e66");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#ff3e66", bean);
		bean.setResDir("10140TEAMM_0");
		bean.setResId("05001001-0017");
		bean.setDefaultValue("打印");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#ff3e66", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private java.lang.String getI18nFB_159a341() {
		if (context.get("nc.ui.uif2.I18nFB#159a341") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#159a341");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#159a341", bean);
		bean.setResDir("10140TEAMM_0");
		bean.setResId("05001001-0018");
		bean.setDefaultValue("打印（Alt+P）");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#159a341", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList17() {
		List list = new ArrayList();
		list.add(getPrintAction());
		list.add(getPreviewAction());
		list.add(getOutputAction());
		return list;
	}

	public nc.ui.uif2.editor.QueryTemplateContainer getQueryTemplateContainer() {
		if (context.get("queryTemplateContainer") != null)
			return (nc.ui.uif2.editor.QueryTemplateContainer) context.get("queryTemplateContainer");
		nc.ui.uif2.editor.QueryTemplateContainer bean = new nc.ui.uif2.editor.QueryTemplateContainer();
		context.put("queryTemplateContainer", bean);
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction getQueryAction() {
		if (context.get("queryAction") != null)
			return (nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction) context.get("queryAction");
		nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction();
		context.put("queryAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		bean.setShowUpComponent(getListView());
		bean.setQryCondDLGInitializer(getTeamQryCondDLGiInitializer());
		bean.setTemplateContainer(getQueryTemplateContainer());
		bean.setInterceptor(getShowListInterceptor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.bd.team.team01.query.TeamQryConditionInitializer getTeamQryCondDLGiInitializer() {
		if (context.get("teamQryCondDLGiInitializer") != null)
			return (nc.ui.bd.team.team01.query.TeamQryConditionInitializer) context.get("teamQryCondDLGiInitializer");
		nc.ui.bd.team.team01.query.TeamQryConditionInitializer bean = new nc.ui.bd.team.team01.query.TeamQryConditionInitializer();
		context.put("teamQryCondDLGiInitializer", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getPreviewAction() {
		if (context.get("previewAction") != null)
			return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction) context.get("previewAction");
		nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
		context.put("previewAction", bean);
		bean.setPreview(true);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.AddAction getAddAction() {
		if (context.get("addAction") != null)
			return (nc.ui.pubapp.uif2app.actions.AddAction) context.get("addAction");
		nc.ui.pubapp.uif2app.actions.AddAction bean = new nc.ui.pubapp.uif2app.actions.AddAction();
		context.put("addAction", bean);
		bean.setModel(getManageAppModel());
		bean.setInterceptor(getShowCardInterceptor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.EditAction getEditAction() {
		if (context.get("editAction") != null)
			return (nc.ui.pubapp.uif2app.actions.EditAction) context.get("editAction");
		nc.ui.pubapp.uif2app.actions.EditAction bean = new nc.ui.pubapp.uif2app.actions.EditAction();
		context.put("editAction", bean);
		bean.setModel(getManageAppModel());
		bean.setInterceptor(getShowCardInterceptor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.CancelAction getCancelAction() {
		if (context.get("cancelAction") != null)
			return (nc.ui.pubapp.uif2app.actions.CancelAction) context.get("cancelAction");
		nc.ui.pubapp.uif2app.actions.CancelAction bean = new nc.ui.pubapp.uif2app.actions.CancelAction();
		context.put("cancelAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getPrintAction() {
		if (context.get("printAction") != null)
			return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction) context.get("printAction");
		nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
		context.put("printAction", bean);
		bean.setPreview(false);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.OutputAction getOutputAction() {
		if (context.get("outputAction") != null)
			return (nc.ui.pubapp.uif2app.actions.OutputAction) context.get("outputAction");
		nc.ui.pubapp.uif2app.actions.OutputAction bean = new nc.ui.pubapp.uif2app.actions.OutputAction();
		context.put("outputAction", bean);
		bean.setModel(getManageAppModel());
		bean.setParent(getBillFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.DeleteAction getDeleteAction() {
		if (context.get("deleteAction") != null)
			return (nc.ui.pubapp.uif2app.actions.DeleteAction) context.get("deleteAction");
		nc.ui.pubapp.uif2app.actions.DeleteAction bean = new nc.ui.pubapp.uif2app.actions.DeleteAction();
		context.put("deleteAction", bean);
		bean.setModel(getManageAppModel());
		bean.setSingleBillService(getDeleteProxy());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.bd.team.team01.action.TeamSaveAction getSaveAction() {
		if (context.get("saveAction") != null)
			return (nc.ui.bd.team.team01.action.TeamSaveAction) context.get("saveAction");
		nc.ui.bd.team.team01.action.TeamSaveAction bean = new nc.ui.bd.team.team01.action.TeamSaveAction();
		context.put("saveAction", bean);
		bean.setModel(getManageAppModel());
		bean.setEditor(getBillFormEditor());
		bean.setValidationService(getValidationService());
		bean.setService(getMaintainProxy());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.bd.team.team01.action.TeamSaveAddAction getSaveAddAction() {
		if (context.get("saveAddAction") != null)
			return (nc.ui.bd.team.team01.action.TeamSaveAddAction) context.get("saveAddAction");
		nc.ui.bd.team.team01.action.TeamSaveAddAction bean = new nc.ui.bd.team.team01.action.TeamSaveAddAction();
		context.put("saveAddAction", bean);
		bean.setModel(getManageAppModel());
		bean.setEditor(getBillFormEditor());
		bean.setValidationService(getValidationService());
		bean.setAddAction(getAddAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction getRefreshAction() {
		if (context.get("refreshAction") != null)
			return (nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction) context.get("refreshAction");
		nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction();
		context.put("refreshAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.RefreshSingleAction getCardRefreshAction() {
		if (context.get("cardRefreshAction") != null)
			return (nc.ui.pubapp.uif2app.actions.RefreshSingleAction) context.get("cardRefreshAction");
		nc.ui.pubapp.uif2app.actions.RefreshSingleAction bean = new nc.ui.pubapp.uif2app.actions.RefreshSingleAction();
		context.put("cardRefreshAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.bd.team.team01.action.TeamDisableAction getDisableAction() {
		if (context.get("disableAction") != null)
			return (nc.ui.bd.team.team01.action.TeamDisableAction) context.get("disableAction");
		nc.ui.bd.team.team01.action.TeamDisableAction bean = new nc.ui.bd.team.team01.action.TeamDisableAction();
		context.put("disableAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.bd.team.team01.action.TeamEnableAction getEnableAction() {
		if (context.get("enableAction") != null)
			return (nc.ui.bd.team.team01.action.TeamEnableAction) context.get("enableAction");
		nc.ui.bd.team.team01.action.TeamEnableAction bean = new nc.ui.bd.team.team01.action.TeamEnableAction();
		context.put("enableAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.GroupAction getEnableActionMenu() {
		if (context.get("enableActionMenu") != null)
			return (nc.funcnode.ui.action.GroupAction) context.get("enableActionMenu");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("enableActionMenu", bean);
		bean.setActions(getManagedList18());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList18() {
		List list = new ArrayList();
		list.add(getEnableAction());
		list.add(getDisableAction());
		return list;
	}

	public nc.ui.pubapp.uif2app.validation.CompositeValidation getValidationService() {
		if (context.get("validationService") != null)
			return (nc.ui.pubapp.uif2app.validation.CompositeValidation) context.get("validationService");
		nc.ui.pubapp.uif2app.validation.CompositeValidation bean = new nc.ui.pubapp.uif2app.validation.CompositeValidation();
		context.put("validationService", bean);
		bean.setValidators(getManagedList19());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList19() {
		List list = new ArrayList();
		list.add(getTeamHeadValidator_1ed3462());
		list.add(getTemplateNotNullValidation_a3f21());
		return list;
	}

	private nc.ui.bd.team.team01.validator.TeamHeadValidator getTeamHeadValidator_1ed3462() {
		if (context.get("nc.ui.bd.team.team01.validator.TeamHeadValidator#1ed3462") != null)
			return (nc.ui.bd.team.team01.validator.TeamHeadValidator) context.get("nc.ui.bd.team.team01.validator.TeamHeadValidator#1ed3462");
		nc.ui.bd.team.team01.validator.TeamHeadValidator bean = new nc.ui.bd.team.team01.validator.TeamHeadValidator();
		context.put("nc.ui.bd.team.team01.validator.TeamHeadValidator#1ed3462", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation getTemplateNotNullValidation_a3f21() {
		if (context.get("nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation#a3f21") != null)
			return (nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation) context.get("nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation#a3f21");
		nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation bean = new nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation();
		context.put("nc.ui.pubapp.uif2app.validation.TemplateNotNullValidation#a3f21", bean);
		bean.setBillForm(getBillFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.FunNodeClosingHandler getClosingListener() {
		if (context.get("ClosingListener") != null)
			return (nc.ui.uif2.FunNodeClosingHandler) context.get("ClosingListener");
		nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
		context.put("ClosingListener", bean);
		bean.setModel(getManageAppModel());
		bean.setSaveaction(getSaveAction());
		bean.setCancelaction(getCancelAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.SeparatorAction getSeparate() {
		if (context.get("separate") != null)
			return (nc.funcnode.ui.action.SeparatorAction) context.get("separate");
		nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
		context.put("separate", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.bd.team.team01.model.TeamInitDataListener getInitDataListener() {
		if (context.get("InitDataListener") != null)
			return (nc.ui.bd.team.team01.model.TeamInitDataListener) context.get("InitDataListener");
		nc.ui.bd.team.team01.model.TeamInitDataListener bean = new nc.ui.bd.team.team01.model.TeamInitDataListener();
		context.put("InitDataListener", bean);
		bean.setDataManager(getModelDataManager());
		bean.setModel(getManageAppModel());
		bean.setContext(getContext());
		bean.setVoClassName("nc.vo.bd.team.team01.entity.AggTeamVO");
		bean.setAutoShowUpComponent(getBillFormEditor());
		bean.setQueryAction(getQueryAction());
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

	public nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad getCardLazySupport() {
		if (context.get("cardLazySupport") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad) context.get("cardLazySupport");
		nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad();
		context.put("cardLazySupport", bean);
		bean.setBillform(getBillFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad getListLazySupport() {
		if (context.get("listLazySupport") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad) context.get("listLazySupport");
		nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad();
		context.put("listLazySupport", bean);
		bean.setListView(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager getLasilyLodadMediator() {
		if (context.get("LasilyLodadMediator") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager) context.get("LasilyLodadMediator");
		nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager bean = new nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager();
		context.put("LasilyLodadMediator", bean);
		bean.setModel(getManageAppModel());
		bean.setLoader(getBillLazilyLoader());
		bean.setLazilyLoadSupporter(getManagedList20());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList20() {
		List list = new ArrayList();
		list.add(getCardLazySupport());
		list.add(getListLazySupport());
		list.add(getLazyActions());
		return list;
	}

	public nc.ui.pubapp.uif2app.lazilyload.ActionLazilyLoad getLazyActions() {
		if (context.get("lazyActions") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.ActionLazilyLoad) context.get("lazyActions");
		nc.ui.pubapp.uif2app.lazilyload.ActionLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.ActionLazilyLoad();
		context.put("lazyActions", bean);
		bean.setModel(getManageAppModel());
		bean.setActionList(getManagedList21());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList21() {
		List list = new ArrayList();
		list.add(getPrintAction());
		list.add(getPreviewAction());
		list.add(getOutputAction());
		return list;
	}

	public nc.ui.ta.team.view.SynAction getSynAction() {
		if (context.get("synAction") != null)
			return (nc.ui.ta.team.view.SynAction) context.get("synAction");
		nc.ui.ta.team.view.SynAction bean = new nc.ui.ta.team.view.SynAction();
		context.put("synAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}
