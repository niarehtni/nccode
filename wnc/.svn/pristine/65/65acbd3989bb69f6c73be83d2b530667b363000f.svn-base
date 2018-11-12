package nc.ui.hi.keypsn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class keypsn_config extends AbstractJavaBeanDefinition {
	private Map<String, Object> context = new HashMap();

	public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getFormEditorActions() {
		if (context.get("formEditorActions") != null)
			return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context
					.get("formEditorActions");
		nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(
				getPsndocFormEditor());
		context.put("formEditorActions", bean);
		bean.setActions(getManagedList0());
		bean.setEditActions(getManagedList1());
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList0() {
		List list = new ArrayList();
		list.add(getEditPsndocAction());
		list.add(getSeparatorAction());
		list.add(getRefreshPsndocAction());
		list.add(getSeparatorAction());
		list.add(getCardAssistGroup());
		list.add(getSeparatorAction());
		list.add(getCardRelateQueryGroup());
		list.add(getSeparatorAction());
		list.add(getCardPrintActionGroup());
		return list;
	}

	private List getManagedList1() {
		List list = new ArrayList();
		list.add(getSavePsndocAction());
		list.add(getSeparatorAction());
		list.add(getCancelPsndocAction());
		return list;
	}

	public nc.ui.hi.psndoc.view.PsndocActionContainer getListViewActions() {
		if (context.get("listViewActions") != null)
			return (nc.ui.hi.psndoc.view.PsndocActionContainer) context
					.get("listViewActions");
		nc.ui.hi.psndoc.view.PsndocActionContainer bean = new nc.ui.hi.psndoc.view.PsndocActionContainer(
				getPsndocListView());
		context.put("listViewActions", bean);
		bean.setActions(getManagedList2());
		bean.setDataManager(getPsndocDataManager());
		bean.setAdjustSortActions(getManagedList3());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getAddActionGroup());
		list.add(getEditActionGroup());
		list.add(getDeletePsndocAction());
		list.add(getSeparatorAction());
		list.add(getQueryPsndocAction());
		list.add(getRefreshPsndocAction());
		list.add(getSeparatorAction());
		list.add(getStopKeypsnAction());
		list.add(getAssistGroup());
		list.add(getSeparatorAction());
		list.add(getRelateQueryGroup());
		list.add(getSeparatorAction());
		list.add(getPrintActionGroup());
		return list;
	}

	private List getManagedList3() {
		List list = new ArrayList();
		list.add(getSavePsndocAction());
		list.add(getCancelPsndocAction());
		return list;
	}

	public nc.funcnode.ui.action.GroupAction getEditActionGroup() {
		if (context.get("editActionGroup") != null)
			return (nc.funcnode.ui.action.GroupAction) context
					.get("editActionGroup");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("editActionGroup", bean);
		bean.setActions(getManagedList4());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList4() {
		List list = new ArrayList();
		list.add(getEditPsndocAction());
		list.add(getBatchEditAction());
		return list;
	}

	public java.lang.String getResourceCode() {
		if (context.get("resourceCode") != null)
			return (java.lang.String) context.get("resourceCode");
		java.lang.String bean = new java.lang.String("6007psnjob");
		context.put("resourceCode", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
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

	public nc.ui.pub.beans.ActionsBar.ActionsBarSeparator getActionsBarSeparator() {
		if (context.get("ActionsBarSeparator") != null)
			return (nc.ui.pub.beans.ActionsBar.ActionsBarSeparator) context
					.get("ActionsBarSeparator");
		nc.ui.pub.beans.ActionsBar.ActionsBarSeparator bean = new nc.ui.pub.beans.ActionsBar.ActionsBarSeparator();
		context.put("ActionsBarSeparator", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.action.MaxBodyViewAction getBodyMaxAction() {
		if (context.get("bodyMaxAction") != null)
			return (nc.ui.hr.uif2.action.MaxBodyViewAction) context
					.get("bodyMaxAction");
		nc.ui.hr.uif2.action.MaxBodyViewAction bean = new nc.ui.hr.uif2.action.MaxBodyViewAction();
		context.put("bodyMaxAction", bean);
		bean.setModel(getManageAppModel());
		bean.setCardPanel(getPsndocFormEditor());
		bean.setDefaultStatus(true);
		bean.setEnableForAllTabs(true);
		bean.setEnableWhenEditingOnly(false);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.action.MaxHeadViewAction getHeadMaxAction() {
		if (context.get("headMaxAction") != null)
			return (nc.ui.hr.uif2.action.MaxHeadViewAction) context
					.get("headMaxAction");
		nc.ui.hr.uif2.action.MaxHeadViewAction bean = new nc.ui.hr.uif2.action.MaxHeadViewAction();
		context.put("headMaxAction", bean);
		bean.setModel(getManageAppModel());
		bean.setCardPanel(getPsndocFormEditor());
		bean.setDefaultStatus(true);
		bean.setEnableForAllTabs(true);
		bean.setEnableWhenEditingOnly(false);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.action.KeypsnRefAddAction getRefAdd() {
		if (context.get("refAdd") != null)
			return (nc.ui.hi.keypsn.action.KeypsnRefAddAction) context
					.get("refAdd");
		nc.ui.hi.keypsn.action.KeypsnRefAddAction bean = new nc.ui.hi.keypsn.action.KeypsnRefAddAction();
		context.put("refAdd", bean);
		bean.setShowTipsOnTree(true);
		bean.setModel(getGroupModel());
		bean.setResourceCode(getResourceCode());
		bean.setPsndocModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.action.KeypsnQueryAddAction getQueryAdd() {
		if (context.get("queryAdd") != null)
			return (nc.ui.hi.keypsn.action.KeypsnQueryAddAction) context
					.get("queryAdd");
		nc.ui.hi.keypsn.action.KeypsnQueryAddAction bean = new nc.ui.hi.keypsn.action.KeypsnQueryAddAction();
		context.put("queryAdd", bean);
		bean.setShowTipsOnTree(true);
		bean.setModel(getGroupModel());
		bean.setResourceCode(getResourceCode());
		bean.setPsndocModel(getManageAppModel());
		bean.setDataManager(getPsndocDataManager());
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
		bean.setActions(getManagedList5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList5() {
		List list = new ArrayList();
		list.add(getRefAdd());
		list.add(getQueryAdd());
		return list;
	}

	public nc.ui.hi.keypsn.action.EditKeyPsnAction getEditPsndocAction() {
		if (context.get("editPsndocAction") != null)
			return (nc.ui.hi.keypsn.action.EditKeyPsnAction) context
					.get("editPsndocAction");
		nc.ui.hi.keypsn.action.EditKeyPsnAction bean = new nc.ui.hi.keypsn.action.EditKeyPsnAction();
		context.put("editPsndocAction", bean);
		bean.setModel(getManageAppModel());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.action.KeypsnDeleteAction getDeletePsndocAction() {
		if (context.get("deletePsndocAction") != null)
			return (nc.ui.hi.keypsn.action.KeypsnDeleteAction) context
					.get("deletePsndocAction");
		nc.ui.hi.keypsn.action.KeypsnDeleteAction bean = new nc.ui.hi.keypsn.action.KeypsnDeleteAction();
		context.put("deletePsndocAction", bean);
		bean.setModel(getManageAppModel());
		bean.setTreeModel(getGroupModel());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.QueryPsndocAction getQueryPsndocAction() {
		if (context.get("queryPsndocAction") != null)
			return (nc.ui.hi.psndoc.action.QueryPsndocAction) context
					.get("queryPsndocAction");
		nc.ui.hi.psndoc.action.QueryPsndocAction bean = new nc.ui.hi.psndoc.action.QueryPsndocAction();
		context.put("queryPsndocAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getPsndocDataManager());
		bean.setQueryDelegator(getQueryPsndocDelegator_e9068());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.hi.psndoc.action.QueryPsndocDelegator getQueryPsndocDelegator_e9068() {
		if (context.get("nc.ui.hi.psndoc.action.QueryPsndocDelegator#e9068") != null)
			return (nc.ui.hi.psndoc.action.QueryPsndocDelegator) context
					.get("nc.ui.hi.psndoc.action.QueryPsndocDelegator#e9068");
		nc.ui.hi.psndoc.action.QueryPsndocDelegator bean = new nc.ui.hi.psndoc.action.QueryPsndocDelegator();
		context.put("nc.ui.hi.psndoc.action.QueryPsndocDelegator#e9068", bean);
		bean.setContext(getContext());
		bean.setNodeKey("bd_psndoc");
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.RefreshPsndocAction getRefreshPsndocAction() {
		if (context.get("refreshPsndocAction") != null)
			return (nc.ui.hi.psndoc.action.RefreshPsndocAction) context
					.get("refreshPsndocAction");
		nc.ui.hi.psndoc.action.RefreshPsndocAction bean = new nc.ui.hi.psndoc.action.RefreshPsndocAction();
		context.put("refreshPsndocAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getPsndocDataManager());
		bean.setFormEditor(getPsndocFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.SavePsndocAction getSavePsndocAction() {
		if (context.get("savePsndocAction") != null)
			return (nc.ui.hi.psndoc.action.SavePsndocAction) context
					.get("savePsndocAction");
		nc.ui.hi.psndoc.action.SavePsndocAction bean = new nc.ui.hi.psndoc.action.SavePsndocAction();
		context.put("savePsndocAction", bean);
		bean.setModel(getManageAppModel());
		bean.setEditor(getPsndocFormEditor());
		bean.setListView(getPsndocListView());
		bean.setDataManager(getPsndocDataManager());
		bean.setSuperValidator(getSuperValidationConfig());
		bean.setValidationService(getBillNotNullValidator());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.CancelPsndocAction getCancelPsndocAction() {
		if (context.get("cancelPsndocAction") != null)
			return (nc.ui.hi.psndoc.action.CancelPsndocAction) context
					.get("cancelPsndocAction");
		nc.ui.hi.psndoc.action.CancelPsndocAction bean = new nc.ui.hi.psndoc.action.CancelPsndocAction();
		context.put("cancelPsndocAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManger(getPsndocDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.AddSubSetAction getAddSubSetAction() {
		if (context.get("addSubSetAction") != null)
			return (nc.ui.hi.psndoc.action.AddSubSetAction) context
					.get("addSubSetAction");
		nc.ui.hi.psndoc.action.AddSubSetAction bean = new nc.ui.hi.psndoc.action.AddSubSetAction();
		context.put("addSubSetAction", bean);
		bean.setModel(getManageAppModel());
		bean.setCardPanel(getPsndocFormEditor());
		bean.setDefaultStatus(true);
		bean.setEnableForAllTabs(false);
		bean.setDisableTabsSet(getDisableTabSet());
		bean.setDefaultValueProvider(getSubDefaultValueProvider());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.DelSubSetAction getDeleteSubSetAction() {
		if (context.get("deleteSubSetAction") != null)
			return (nc.ui.hi.psndoc.action.DelSubSetAction) context
					.get("deleteSubSetAction");
		nc.ui.hi.psndoc.action.DelSubSetAction bean = new nc.ui.hi.psndoc.action.DelSubSetAction();
		context.put("deleteSubSetAction", bean);
		bean.setModel(getManageAppModel());
		bean.setCardPanel(getPsndocFormEditor());
		bean.setDefaultStatus(true);
		bean.setEnableForAllTabs(false);
		bean.setDisableTabsSet(getDisableTabSet());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.InsertSubSetAction getInsertSubSetAction() {
		if (context.get("insertSubSetAction") != null)
			return (nc.ui.hi.psndoc.action.InsertSubSetAction) context
					.get("insertSubSetAction");
		nc.ui.hi.psndoc.action.InsertSubSetAction bean = new nc.ui.hi.psndoc.action.InsertSubSetAction();
		context.put("insertSubSetAction", bean);
		bean.setModel(getManageAppModel());
		bean.setCardPanel(getPsndocFormEditor());
		bean.setDefaultStatus(true);
		bean.setEnableForAllTabs(false);
		bean.setDisableTabsSet(getDisableTabSet());
		bean.setDefaultValueProvider(getSubDefaultValueProvider());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.CopySubSetAction getCopySubSetAction() {
		if (context.get("copySubSetAction") != null)
			return (nc.ui.hi.psndoc.action.CopySubSetAction) context
					.get("copySubSetAction");
		nc.ui.hi.psndoc.action.CopySubSetAction bean = new nc.ui.hi.psndoc.action.CopySubSetAction();
		context.put("copySubSetAction", bean);
		bean.setModel(getManageAppModel());
		bean.setCardPanel(getPsndocFormEditor());
		bean.setDefaultStatus(true);
		bean.setEnableForAllTabs(false);
		bean.setDisableTabsSet(getDisableTabSet());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.PasteSubSetAction getPasteSubSetAction() {
		if (context.get("pasteSubSetAction") != null)
			return (nc.ui.hi.psndoc.action.PasteSubSetAction) context
					.get("pasteSubSetAction");
		nc.ui.hi.psndoc.action.PasteSubSetAction bean = new nc.ui.hi.psndoc.action.PasteSubSetAction();
		context.put("pasteSubSetAction", bean);
		bean.setModel(getManageAppModel());
		bean.setCardPanel(getPsndocFormEditor());
		bean.setDefaultStatus(true);
		bean.setEnableForAllTabs(false);
		bean.setDisableTabsSet(getDisableTabSet());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.AdjustSubReordUpAction getAdjustSubReordUpAction() {
		if (context.get("adjustSubReordUpAction") != null)
			return (nc.ui.hi.psndoc.action.AdjustSubReordUpAction) context
					.get("adjustSubReordUpAction");
		nc.ui.hi.psndoc.action.AdjustSubReordUpAction bean = new nc.ui.hi.psndoc.action.AdjustSubReordUpAction();
		context.put("adjustSubReordUpAction", bean);
		bean.setModel(getManageAppModel());
		bean.setFormEditor(getPsndocFormEditor());
		bean.setDefaultStatus(true);
		bean.setEnableForAllTabs(false);
		bean.setDisableTabsSet(getBusinessInfoSet());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.AdjustSubReordDownAction getAdjustSubReordDownAction() {
		if (context.get("adjustSubReordDownAction") != null)
			return (nc.ui.hi.psndoc.action.AdjustSubReordDownAction) context
					.get("adjustSubReordDownAction");
		nc.ui.hi.psndoc.action.AdjustSubReordDownAction bean = new nc.ui.hi.psndoc.action.AdjustSubReordDownAction();
		context.put("adjustSubReordDownAction", bean);
		bean.setModel(getManageAppModel());
		bean.setFormEditor(getPsndocFormEditor());
		bean.setDefaultStatus(true);
		bean.setEnableForAllTabs(false);
		bean.setDisableTabsSet(getBusinessInfoSet());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.FirstPsndocAction getFirstLineAction() {
		if (context.get("firstLineAction") != null)
			return (nc.ui.hi.psndoc.action.FirstPsndocAction) context
					.get("firstLineAction");
		nc.ui.hi.psndoc.action.FirstPsndocAction bean = new nc.ui.hi.psndoc.action.FirstPsndocAction();
		context.put("firstLineAction", bean);
		bean.setModel(getManageAppModel());
		bean.setFormEditor(getPsndocFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.PrePsndocAction getPreLineAction() {
		if (context.get("preLineAction") != null)
			return (nc.ui.hi.psndoc.action.PrePsndocAction) context
					.get("preLineAction");
		nc.ui.hi.psndoc.action.PrePsndocAction bean = new nc.ui.hi.psndoc.action.PrePsndocAction();
		context.put("preLineAction", bean);
		bean.setModel(getManageAppModel());
		bean.setFormEditor(getPsndocFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.NextPsndocAction getNextLineAction() {
		if (context.get("nextLineAction") != null)
			return (nc.ui.hi.psndoc.action.NextPsndocAction) context
					.get("nextLineAction");
		nc.ui.hi.psndoc.action.NextPsndocAction bean = new nc.ui.hi.psndoc.action.NextPsndocAction();
		context.put("nextLineAction", bean);
		bean.setModel(getManageAppModel());
		bean.setFormEditor(getPsndocFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.LastPsndocAction getLastLineAction() {
		if (context.get("lastLineAction") != null)
			return (nc.ui.hi.psndoc.action.LastPsndocAction) context
					.get("lastLineAction");
		nc.ui.hi.psndoc.action.LastPsndocAction bean = new nc.ui.hi.psndoc.action.LastPsndocAction();
		context.put("lastLineAction", bean);
		bean.setModel(getManageAppModel());
		bean.setFormEditor(getPsndocFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.action.KeyPsnBatchEditPsndocAction getBatchEditAction() {
		if (context.get("batchEditAction") != null)
			return (nc.ui.hi.keypsn.action.KeyPsnBatchEditPsndocAction) context
					.get("batchEditAction");
		nc.ui.hi.keypsn.action.KeyPsnBatchEditPsndocAction bean = new nc.ui.hi.keypsn.action.KeyPsnBatchEditPsndocAction();
		context.put("batchEditAction", bean);
		bean.setModel(getManageAppModel());
		bean.setFormEditor(getPsndocFormEditor());
		bean.setListView(getPsndocListView());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.action.KeyPsnBatchAddSubSetAction getBatchAddSubSetAction() {
		if (context.get("batchAddSubSetAction") != null)
			return (nc.ui.hi.keypsn.action.KeyPsnBatchAddSubSetAction) context
					.get("batchAddSubSetAction");
		nc.ui.hi.keypsn.action.KeyPsnBatchAddSubSetAction bean = new nc.ui.hi.keypsn.action.KeyPsnBatchAddSubSetAction();
		context.put("batchAddSubSetAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getPsndocListView());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.SortPsndocAction getSortPsndocAction() {
		if (context.get("sortPsndocAction") != null)
			return (nc.ui.hi.psndoc.action.SortPsndocAction) context
					.get("sortPsndocAction");
		nc.ui.hi.psndoc.action.SortPsndocAction bean = new nc.ui.hi.psndoc.action.SortPsndocAction();
		context.put("sortPsndocAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getPsndocListView());
		bean.setDataManger(getPsndocDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.AdjustPsndocSortAction getAdjustSortAction() {
		if (context.get("adjustSortAction") != null)
			return (nc.ui.hi.psndoc.action.AdjustPsndocSortAction) context
					.get("adjustSortAction");
		nc.ui.hi.psndoc.action.AdjustPsndocSortAction bean = new nc.ui.hi.psndoc.action.AdjustPsndocSortAction();
		context.put("adjustSortAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getPsndocListView());
		bean.setDataManger(getPsndocDataManager());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.GroupAction getPrintActionGroup() {
		if (context.get("printActionGroup") != null)
			return (nc.funcnode.ui.action.GroupAction) context
					.get("printActionGroup");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("printActionGroup", bean);
		bean.setActions(getManagedList6());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList6() {
		List list = new ArrayList();
		list.add(getPrintDirectAction());
		list.add(getPrintPreviewAction());
		list.add(getListOutputAction());
		list.add(getSeparatorAction());
		list.add(getTemplatePrintAction());
		list.add(getTemplatePreviewAction());
		return list;
	}

	public nc.funcnode.ui.action.GroupAction getCardPrintActionGroup() {
		if (context.get("cardPrintActionGroup") != null)
			return (nc.funcnode.ui.action.GroupAction) context
					.get("cardPrintActionGroup");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("cardPrintActionGroup", bean);
		bean.setActions(getManagedList7());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList7() {
		List list = new ArrayList();
		list.add(getTemplatePrintAction());
		list.add(getTemplatePreviewAction());
		list.add(getCardOutputAction());
		return list;
	}

	public nc.ui.hr.uif2.action.print.DirectPreviewAction getPrintPreviewAction() {
		if (context.get("printPreviewAction") != null)
			return (nc.ui.hr.uif2.action.print.DirectPreviewAction) context
					.get("printPreviewAction");
		nc.ui.hr.uif2.action.print.DirectPreviewAction bean = new nc.ui.hr.uif2.action.print.DirectPreviewAction();
		context.put("printPreviewAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.action.print.DirectPrintAction getPrintDirectAction() {
		if (context.get("printDirectAction") != null)
			return (nc.ui.hr.uif2.action.print.DirectPrintAction) context
					.get("printDirectAction");
		nc.ui.hr.uif2.action.print.DirectPrintAction bean = new nc.ui.hr.uif2.action.print.DirectPrintAction();
		context.put("printDirectAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.ExportListPsndocAction getListOutputAction() {
		if (context.get("listOutputAction") != null)
			return (nc.ui.hi.psndoc.action.ExportListPsndocAction) context
					.get("listOutputAction");
		nc.ui.hi.psndoc.action.ExportListPsndocAction bean = new nc.ui.hi.psndoc.action.ExportListPsndocAction();
		context.put("listOutputAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.TemplatePreviewPsndocAction getTemplatePreviewAction() {
		if (context.get("templatePreviewAction") != null)
			return (nc.ui.hi.psndoc.action.TemplatePreviewPsndocAction) context
					.get("templatePreviewAction");
		nc.ui.hi.psndoc.action.TemplatePreviewPsndocAction bean = new nc.ui.hi.psndoc.action.TemplatePreviewPsndocAction();
		context.put("templatePreviewAction", bean);
		bean.setModel(getManageAppModel());
		bean.setPrintDlgParentConatiner(getPsndocFormEditor());
		bean.setDatasource(getDatasource());
		bean.setNodeKey("bd_psndoc");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.TemplatePrintPsndocAction getTemplatePrintAction() {
		if (context.get("templatePrintAction") != null)
			return (nc.ui.hi.psndoc.action.TemplatePrintPsndocAction) context
					.get("templatePrintAction");
		nc.ui.hi.psndoc.action.TemplatePrintPsndocAction bean = new nc.ui.hi.psndoc.action.TemplatePrintPsndocAction();
		context.put("templatePrintAction", bean);
		bean.setModel(getManageAppModel());
		bean.setPrintDlgParentConatiner(getPsndocFormEditor());
		bean.setDatasource(getDatasource());
		bean.setNodeKey("bd_psndoc");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.ExportCardPsndocAction getCardOutputAction() {
		if (context.get("cardOutputAction") != null)
			return (nc.ui.hi.psndoc.action.ExportCardPsndocAction) context
					.get("cardOutputAction");
		nc.ui.hi.psndoc.action.ExportCardPsndocAction bean = new nc.ui.hi.psndoc.action.ExportCardPsndocAction();
		context.put("cardOutputAction", bean);
		bean.setModel(getManageAppModel());
		bean.setPrintDlgParentConatiner(getPsndocFormEditor());
		bean.setDatasource(getDatasource());
		bean.setNodeKey("bd_psndoc");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.HIMetaDataDataSource getDatasource() {
		if (context.get("datasource") != null)
			return (nc.ui.hi.psndoc.action.HIMetaDataDataSource) context
					.get("datasource");
		nc.ui.hi.psndoc.action.HIMetaDataDataSource bean = new nc.ui.hi.psndoc.action.HIMetaDataDataSource();
		context.put("datasource", bean);
		bean.setModel(getManageAppModel());
		bean.setSingleData(true);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getRelateQueryGroup() {
		if (context.get("relateQueryGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context
					.get("relateQueryGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("relateQueryGroup", bean);
		bean.setCode("relateQuery");
		bean.setName(getI18nFB_194fb11());
		bean.setActions(getManagedList8());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_194fb11() {
		if (context.get("nc.ui.uif2.I18nFB#194fb11") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#194fb11");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#194fb11", bean);
		bean.setResDir("6001uif2");
		bean.setResId("x6001uif20002");
		bean.setDefaultValue("联查");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#194fb11", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList8() {
		List list = new ArrayList();
		list.add(getQueryCardReptAction());
		list.add(getQueryListReptAction());
		list.add(getSeparatorAction());
		list.add(getQueryJobHistroyAction());
		list.add(getSeparatorAction());
		list.add(getQueryReptObjectAction());
		list.add(getSeparatorAction());
		list.add(getQueryBusiBillAction());
		return list;
	}

	public nc.funcnode.ui.action.MenuAction getCardRelateQueryGroup() {
		if (context.get("cardRelateQueryGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context
					.get("cardRelateQueryGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("cardRelateQueryGroup", bean);
		bean.setCode("relateQuery");
		bean.setName(getI18nFB_1ef3492());
		bean.setActions(getManagedList9());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1ef3492() {
		if (context.get("nc.ui.uif2.I18nFB#1ef3492") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1ef3492");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1ef3492", bean);
		bean.setResDir("6001uif2");
		bean.setResId("x6001uif20002");
		bean.setDefaultValue("联查");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1ef3492", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList9() {
		List list = new ArrayList();
		list.add(getQueryCardReptAction());
		list.add(getSeparatorAction());
		list.add(getQueryJobHistroyAction());
		list.add(getSeparatorAction());
		list.add(getQueryReptObjectAction());
		list.add(getSeparatorAction());
		list.add(getQueryBusiBillAction());
		return list;
	}

	public nc.ui.hi.psndoc.action.QueryCardReptAction getQueryCardReptAction() {
		if (context.get("queryCardReptAction") != null)
			return (nc.ui.hi.psndoc.action.QueryCardReptAction) context
					.get("queryCardReptAction");
		nc.ui.hi.psndoc.action.QueryCardReptAction bean = new nc.ui.hi.psndoc.action.QueryCardReptAction();
		context.put("queryCardReptAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.QueryListReptAction getQueryListReptAction() {
		if (context.get("queryListReptAction") != null)
			return (nc.ui.hi.psndoc.action.QueryListReptAction) context
					.get("queryListReptAction");
		nc.ui.hi.psndoc.action.QueryListReptAction bean = new nc.ui.hi.psndoc.action.QueryListReptAction();
		context.put("queryListReptAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getPsndocDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.QueryBusiBillAction getQueryBusiBillAction() {
		if (context.get("queryBusiBillAction") != null)
			return (nc.ui.hi.psndoc.action.QueryBusiBillAction) context
					.get("queryBusiBillAction");
		nc.ui.hi.psndoc.action.QueryBusiBillAction bean = new nc.ui.hi.psndoc.action.QueryBusiBillAction();
		context.put("queryBusiBillAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getPsndocListView());
		bean.setFormEditor(getPsndocFormEditor());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.QueryPsnAbilityMatchAction getQueryPsnAbilityMatchAction() {
		if (context.get("queryPsnAbilityMatchAction") != null)
			return (nc.ui.hi.psndoc.action.QueryPsnAbilityMatchAction) context
					.get("queryPsnAbilityMatchAction");
		nc.ui.hi.psndoc.action.QueryPsnAbilityMatchAction bean = new nc.ui.hi.psndoc.action.QueryPsnAbilityMatchAction();
		context.put("queryPsnAbilityMatchAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.QueryReptObjectAction getQueryReptObjectAction() {
		if (context.get("queryReptObjectAction") != null)
			return (nc.ui.hi.psndoc.action.QueryReptObjectAction) context
					.get("queryReptObjectAction");
		nc.ui.hi.psndoc.action.QueryReptObjectAction bean = new nc.ui.hi.psndoc.action.QueryReptObjectAction();
		context.put("queryReptObjectAction", bean);
		bean.setModel(getManageAppModel());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.HIFileManageAction getAttachmentAction() {
		if (context.get("attachmentAction") != null)
			return (nc.ui.hi.psndoc.action.HIFileManageAction) context
					.get("attachmentAction");
		nc.ui.hi.psndoc.action.HIFileManageAction bean = new nc.ui.hi.psndoc.action.HIFileManageAction();
		context.put("attachmentAction", bean);
		bean.setModel(getManageAppModel());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.HIFileManageAction getFileAction() {
		if (context.get("fileAction") != null)
			return (nc.ui.hi.psndoc.action.HIFileManageAction) context
					.get("fileAction");
		nc.ui.hi.psndoc.action.HIFileManageAction bean = new nc.ui.hi.psndoc.action.HIFileManageAction();
		context.put("fileAction", bean);
		bean.setModel(getManageAppModel());
		bean.setToolBarVisible(false);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.QueryJobHistroyAction getQueryJobHistroyAction() {
		if (context.get("queryJobHistroyAction") != null)
			return (nc.ui.hi.psndoc.action.QueryJobHistroyAction) context
					.get("queryJobHistroyAction");
		nc.ui.hi.psndoc.action.QueryJobHistroyAction bean = new nc.ui.hi.psndoc.action.QueryJobHistroyAction();
		context.put("queryJobHistroyAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getPsndocListView());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.ExportPhotoAction getExportPhotoAction() {
		if (context.get("exportPhotoAction") != null)
			return (nc.ui.hi.psndoc.action.ExportPhotoAction) context
					.get("exportPhotoAction");
		nc.ui.hi.psndoc.action.ExportPhotoAction bean = new nc.ui.hi.psndoc.action.ExportPhotoAction();
		context.put("exportPhotoAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getPsndocListView());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.CreateUserAction getCreateUserAction() {
		if (context.get("createUserAction") != null)
			return (nc.ui.hi.psndoc.action.CreateUserAction) context
					.get("createUserAction");
		nc.ui.hi.psndoc.action.CreateUserAction bean = new nc.ui.hi.psndoc.action.CreateUserAction();
		context.put("createUserAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getPsndocListView());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.action.StopKeypsnAction getStopKeypsnAction() {
		if (context.get("stopKeypsnAction") != null)
			return (nc.ui.hi.keypsn.action.StopKeypsnAction) context
					.get("stopKeypsnAction");
		nc.ui.hi.keypsn.action.StopKeypsnAction bean = new nc.ui.hi.keypsn.action.StopKeypsnAction();
		context.put("stopKeypsnAction", bean);
		bean.setModel(getManageAppModel());
		bean.setResourceCode(getResourceCode());
		bean.setTreeModel(getGroupModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getAssistGroup() {
		if (context.get("assistGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context
					.get("assistGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("assistGroup", bean);
		bean.setCode("assist");
		bean.setName(getI18nFB_1648223());
		bean.setActions(getManagedList10());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1648223() {
		if (context.get("nc.ui.uif2.I18nFB#1648223") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1648223");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1648223", bean);
		bean.setResDir("6001uif2");
		bean.setResId("x6001uif20001");
		bean.setDefaultValue("辅助功能");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1648223", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList10() {
		List list = new ArrayList();
		list.add(getSortPsndocAction());
		list.add(getAdjustSortAction());
		list.add(getSeparatorAction());
		list.add(getExportPhotoAction());
		list.add(getCreateUserAction());
		list.add(getSeparatorAction());
		list.add(getAttachmentAction());
		list.add(getBatchAddSubSetAction());
		return list;
	}

	public nc.funcnode.ui.action.MenuAction getCardAssistGroup() {
		if (context.get("cardAssistGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context
					.get("cardAssistGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("cardAssistGroup", bean);
		bean.setCode("assist");
		bean.setName(getI18nFB_19bdd10());
		bean.setActions(getManagedList11());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_19bdd10() {
		if (context.get("nc.ui.uif2.I18nFB#19bdd10") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#19bdd10");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#19bdd10", bean);
		bean.setResDir("6001uif2");
		bean.setResId("x6001uif20001");
		bean.setDefaultValue("辅助功能");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#19bdd10", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList11() {
		List list = new ArrayList();
		list.add(getExportPhotoAction());
		list.add(getCreateUserAction());
		list.add(getSeparatorAction());
		list.add(getAttachmentAction());
		return list;
	}

	public nc.vo.hr.validator.IDFieldValidatorConfig getIDvalidationConfig() {
		if (context.get("IDvalidationConfig") != null)
			return (nc.vo.hr.validator.IDFieldValidatorConfig) context
					.get("IDvalidationConfig");
		nc.vo.hr.validator.IDFieldValidatorConfig bean = new nc.vo.hr.validator.IDFieldValidatorConfig();
		context.put("IDvalidationConfig", bean);
		bean.setIdValidator(getManagedMap0());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private Map getManagedMap0() {
		Map map = new HashMap();
		map.put("0", "nc.vo.hr.validator.IDCardValidator");
		map.put("1", "nc.vo.hr.validator.PassPortValidator");
		return map;
	}

	public nc.ui.hr.uif2.validator.BillNotNullValidateService getBillNotNullValidator() {
		if (context.get("billNotNullValidator") != null)
			return (nc.ui.hr.uif2.validator.BillNotNullValidateService) context
					.get("billNotNullValidator");
		nc.ui.hr.uif2.validator.BillNotNullValidateService bean = new nc.ui.hr.uif2.validator.BillNotNullValidateService(
				getPsndocFormEditor());
		context.put("billNotNullValidator", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil getSuperValidationConfig() {
		if (context.get("SuperValidationConfig") != null)
			return (nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil) context
					.get("SuperValidationConfig");
		nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil bean = new nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil();
		context.put("SuperValidationConfig", bean);
		bean.setFieldRelationMap(getManagedMap1());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private Map getManagedMap1() {
		Map map = new HashMap();
		map.put("additionalValidationOfSave", getAdditionalValidationOfSave());
		return map;
	}

	public nc.ui.hr.tools.uilogic.SuperLogicProcessor getAdditionalValidationOfSave() {
		if (context.get("additionalValidationOfSave") != null)
			return (nc.ui.hr.tools.uilogic.SuperLogicProcessor) context
					.get("additionalValidationOfSave");
		nc.ui.hr.tools.uilogic.SuperLogicProcessor bean = new nc.ui.hr.tools.uilogic.SuperLogicProcessor();
		context.put("additionalValidationOfSave", bean);
		bean.setMethods(getManagedList12());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList12() {
		List list = new ArrayList();
		return list;
	}

	public nc.vo.uif2.LoginContext getContext() {
		if (context.get("context") != null)
			return (nc.vo.uif2.LoginContext) context.get("context");
		nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
		context.put("context", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.model.KeyPsnModelService getPsndocModelService() {
		if (context.get("psndocModelService") != null)
			return (nc.ui.hi.keypsn.model.KeyPsnModelService) context
					.get("psndocModelService");
		nc.ui.hi.keypsn.model.KeyPsnModelService bean = new nc.ui.hi.keypsn.model.KeyPsnModelService();
		context.put("psndocModelService", bean);
		bean.setTreeModel(getGroupModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.register.model.RegisterDefaultValueProvider getDefaultValueProvider() {
		if (context.get("defaultValueProvider") != null)
			return (nc.ui.hi.register.model.RegisterDefaultValueProvider) context
					.get("defaultValueProvider");
		nc.ui.hi.register.model.RegisterDefaultValueProvider bean = new nc.ui.hi.register.model.RegisterDefaultValueProvider();
		context.put("defaultValueProvider", bean);
		bean.setModelDataManager(getPsndocDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.register.model.SubDefaultValueProvider getSubDefaultValueProvider() {
		if (context.get("subDefaultValueProvider") != null)
			return (nc.ui.hi.register.model.SubDefaultValueProvider) context
					.get("subDefaultValueProvider");
		nc.ui.hi.register.model.SubDefaultValueProvider bean = new nc.ui.hi.register.model.SubDefaultValueProvider();
		context.put("subDefaultValueProvider", bean);
		bean.setFormEditor(getPsndocFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public java.util.HashSet getBusinessInfoSet() {
		if (context.get("businessInfoSet") != null)
			return (java.util.HashSet) context.get("businessInfoSet");
		java.util.HashSet bean = new java.util.HashSet(getManagedList13());
		context.put("businessInfoSet", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList13() {
		List list = new ArrayList();
		list.add("hi_psnorg");
		list.add("hi_psnjob");
		list.add("hi_psndoc_parttime");
		list.add("hi_psndoc_trial");
		list.add("hi_psndoc_psnchg");
		list.add("hi_psndoc_ctrt");
		list.add("hi_psndoc_retire");
		list.add("hi_psndoc_train");
		list.add("hi_psndoc_ass");
		list.add("hi_psndoc_capa");
		list.add("hi_psndoc_keypsn");
		list.add("hi_psndoc_qulify");
		return list;
	}

	public java.util.HashSet getDisableTabSet() {
		if (context.get("disableTabSet") != null)
			return (java.util.HashSet) context.get("disableTabSet");
		java.util.HashSet bean = new java.util.HashSet(getManagedList14());
		context.put("disableTabSet", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList14() {
		List list = new ArrayList();
		list.add("hi_psnorg");
		return list;
	}

	public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors() {
		if (context.get("toftpanelActionContributors") != null)
			return (nc.ui.uif2.actions.ActionContributors) context
					.get("toftpanelActionContributors");
		nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
		context.put("toftpanelActionContributors", bean);
		bean.setContributors(getManagedList15());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList15() {
		List list = new ArrayList();
		list.add(getListViewActions());
		list.add(getFormEditorActions());
		return list;
	}

	public nc.ui.hi.keypsn.model.KeyPsnInitDataListener getInitDataListener() {
		if (context.get("InitDataListener") != null)
			return (nc.ui.hi.keypsn.model.KeyPsnInitDataListener) context
					.get("InitDataListener");
		nc.ui.hi.keypsn.model.KeyPsnInitDataListener bean = new nc.ui.hi.keypsn.model.KeyPsnInitDataListener();
		context.put("InitDataListener", bean);
		bean.setContext(getContext());
		bean.setModel(getManageAppModel());
		bean.setLeftModel(getGroupModel());
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
		bean.setSaveaction(getSavePsndocAction());
		bean.setCancelaction(getCancelPsndocAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.vo.bd.meta.BDObjectTreeCreateStrategy getTreeCreateStrategy() {
		if (context.get("treeCreateStrategy") != null)
			return (nc.vo.bd.meta.BDObjectTreeCreateStrategy) context
					.get("treeCreateStrategy");
		nc.vo.bd.meta.BDObjectTreeCreateStrategy bean = new nc.vo.bd.meta.BDObjectTreeCreateStrategy();
		context.put("treeCreateStrategy", bean);
		bean.setFactory(getBoadatorfactory());
		bean.setRootName(getI18nFB_1b4f235());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1b4f235() {
		if (context.get("nc.ui.uif2.I18nFB#1b4f235") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1b4f235");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1b4f235", bean);
		bean.setResDir("6007psn");
		bean.setResId("06007psn0357");
		bean.setDefaultValue("关键人员组");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1b4f235", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public nc.ui.hi.keypsn.model.KeyPsnGrpAppModel getGroupModel() {
		if (context.get("groupModel") != null)
			return (nc.ui.hi.keypsn.model.KeyPsnGrpAppModel) context
					.get("groupModel");
		nc.ui.hi.keypsn.model.KeyPsnGrpAppModel bean = new nc.ui.hi.keypsn.model.KeyPsnGrpAppModel();
		context.put("groupModel", bean);
		bean.setService(getGroupModelService());
		bean.setTreeCreateStrategy(getTreeCreateStrategy());
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.model.KeyPsnGrpModelService getGroupModelService() {
		if (context.get("groupModelService") != null)
			return (nc.ui.hi.keypsn.model.KeyPsnGrpModelService) context
					.get("groupModelService");
		nc.ui.hi.keypsn.model.KeyPsnGrpModelService bean = new nc.ui.hi.keypsn.model.KeyPsnGrpModelService();
		context.put("groupModelService", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.model.KeyPsnGrpModelDataManager getGroupDataManager() {
		if (context.get("groupDataManager") != null)
			return (nc.ui.hi.keypsn.model.KeyPsnGrpModelDataManager) context
					.get("groupDataManager");
		nc.ui.hi.keypsn.model.KeyPsnGrpModelDataManager bean = new nc.ui.hi.keypsn.model.KeyPsnGrpModelDataManager();
		context.put("groupDataManager", bean);
		bean.setContext(getContext());
		bean.setModel(getGroupModel());
		bean.setService(getGroupModelService());
		bean.setQueryDelegater(getGroupModelService());
		bean.setShowSealDataFlag(false);
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

	public nc.ui.hi.keypsn.model.KeypsnPsndocModel getManageAppModel() {
		if (context.get("manageAppModel") != null)
			return (nc.ui.hi.keypsn.model.KeypsnPsndocModel) context
					.get("manageAppModel");
		nc.ui.hi.keypsn.model.KeypsnPsndocModel bean = new nc.ui.hi.keypsn.model.KeypsnPsndocModel();
		context.put("manageAppModel", bean);
		bean.setContext(getContext());
		bean.setNodeCode("60070employee");
		bean.setService(getPsndocModelService());
		bean.setBusinessInfoSet(getBusinessInfoSet());
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.model.PsndocDataManager getPsndocDataManager() {
		if (context.get("psndocDataManager") != null)
			return (nc.ui.hi.psndoc.model.PsndocDataManager) context
					.get("psndocDataManager");
		nc.ui.hi.psndoc.model.PsndocDataManager bean = new nc.ui.hi.psndoc.model.PsndocDataManager();
		context.put("psndocDataManager", bean);
		bean.setContext(getContext());
		bean.setModel(getManageAppModel());
		bean.setService(getPsndocModelService());
		bean.setPaginationModel(getPaginationModel());
		bean.setPaginationBar(getPaginationBar());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.model.PsndocMediator getPsndocMediator() {
		if (context.get("psndocMediator") != null)
			return (nc.ui.hi.psndoc.model.PsndocMediator) context
					.get("psndocMediator");
		nc.ui.hi.psndoc.model.PsndocMediator bean = new nc.ui.hi.psndoc.model.PsndocMediator();
		context.put("psndocMediator", bean);
		bean.setTypeAppModel(getGroupModel());
		bean.setDocModel(getManageAppModel());
		bean.setDocModelDataManager(getPsndocDataManager());
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
		bean.setPaginationQueryService(getPsndocModelService());
		bean.init();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.view.PsndocListView getPsndocListView() {
		if (context.get("psndocListView") != null)
			return (nc.ui.hi.psndoc.view.PsndocListView) context
					.get("psndocListView");
		nc.ui.hi.psndoc.view.PsndocListView bean = new nc.ui.hi.psndoc.view.PsndocListView();
		context.put("psndocListView", bean);
		bean.setPos("head");
		bean.setModel(getManageAppModel());
		bean.setMultiSelectionMode(1);
		bean.setMultiSelectionEnable(true);
		bean.setDataManger(getPsndocDataManager());
		bean.setPaginationBar(getPaginationBar());
		bean.setNodekey("bd_psndoc");
		bean.setDealHyperlink(true);
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.view.PsndocFormEditor getPsndocFormEditor() {
		if (context.get("psndocFormEditor") != null)
			return (nc.ui.hi.psndoc.view.PsndocFormEditor) context
					.get("psndocFormEditor");
		nc.ui.hi.psndoc.view.PsndocFormEditor bean = new nc.ui.hi.psndoc.view.PsndocFormEditor();
		context.put("psndocFormEditor", bean);
		bean.setModel(getManageAppModel());
		bean.setSuperValidator(getSuperValidationConfig());
		bean.setDataManger(getPsndocDataManager());
		bean.setTemplateContainer(getTemplateContainer());
		bean.setNodekey("bd_psndoc");
		bean.setTabActions(getManagedList16());
		bean.setComponentValueManager(getValueManager());
		bean.setDealHyperlink(true);
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList16() {
		List list = new ArrayList();
		list.add(getAddSubSetAction());
		list.add(getInsertSubSetAction());
		list.add(getDeleteSubSetAction());
		list.add(getCopySubSetAction());
		list.add(getPasteSubSetAction());
		list.add(getActionsBarSeparator());
		list.add(getAdjustSubReordUpAction());
		list.add(getAdjustSubReordDownAction());
		list.add(getBodyMaxAction());
		return list;
	}

	public nc.ui.hr.uif2.mediator.HyperLinkClickMediator getMouseClickShowPanelMediator() {
		if (context.get("mouseClickShowPanelMediator") != null)
			return (nc.ui.hr.uif2.mediator.HyperLinkClickMediator) context
					.get("mouseClickShowPanelMediator");
		nc.ui.hr.uif2.mediator.HyperLinkClickMediator bean = new nc.ui.hr.uif2.mediator.HyperLinkClickMediator();
		context.put("mouseClickShowPanelMediator", bean);
		bean.setModel(getManageAppModel());
		bean.setShowUpComponent(getPsndocFormEditor());
		bean.setHyperLinkColumn("code");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.view.PsndocMetaDataValueAdapter getValueManager() {
		if (context.get("valueManager") != null)
			return (nc.ui.hi.psndoc.view.PsndocMetaDataValueAdapter) context
					.get("valueManager");
		nc.ui.hi.psndoc.view.PsndocMetaDataValueAdapter bean = new nc.ui.hi.psndoc.view.PsndocMetaDataValueAdapter();
		context.put("valueManager", bean);
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
		bean.setActions(getManagedList17());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList17() {
		List list = new ArrayList();
		list.add(getFileAction());
		list.add(getActionsBarSeparator());
		list.add(getFirstLineAction());
		list.add(getPreLineAction());
		list.add(getNextLineAction());
		list.add(getLastLineAction());
		list.add(getHeadMaxAction());
		return list;
	}

	public nc.ui.uif2.actions.ShowMeUpAction getEditorReturnAction() {
		if (context.get("editorReturnAction") != null)
			return (nc.ui.uif2.actions.ShowMeUpAction) context
					.get("editorReturnAction");
		nc.ui.uif2.actions.ShowMeUpAction bean = new nc.ui.uif2.actions.ShowMeUpAction();
		context.put("editorReturnAction", bean);
		bean.setGoComponent(getPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.view.HrPsnclTemplateContainer getTemplateContainer() {
		if (context.get("templateContainer") != null)
			return (nc.ui.hr.uif2.view.HrPsnclTemplateContainer) context
					.get("templateContainer");
		nc.ui.hr.uif2.view.HrPsnclTemplateContainer bean = new nc.ui.hr.uif2.view.HrPsnclTemplateContainer();
		context.put("templateContainer", bean);
		bean.setContext(getContext());
		bean.setNodeKeies(getManagedList18());
		bean.load();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList18() {
		List list = new ArrayList();
		list.add("bd_psndoc");
		list.add("group");
		return list;
	}

	public nc.ui.hi.keypsn.view.KeyPsnGroupTreePanel getTreePanel() {
		if (context.get("treePanel") != null)
			return (nc.ui.hi.keypsn.view.KeyPsnGroupTreePanel) context
					.get("treePanel");
		nc.ui.hi.keypsn.view.KeyPsnGroupTreePanel bean = new nc.ui.hi.keypsn.view.KeyPsnGroupTreePanel();
		context.put("treePanel", bean);
		bean.setModel(getGroupModel());
		bean.setRootvisibleflag(true);
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.pub.HIToolBarPanel getGroupToolbar() {
		if (context.get("groupToolbar") != null)
			return (nc.ui.hi.pub.HIToolBarPanel) context.get("groupToolbar");
		nc.ui.hi.pub.HIToolBarPanel bean = new nc.ui.hi.pub.HIToolBarPanel();
		context.put("groupToolbar", bean);
		bean.setModel(getGroupModel());
		bean.setActions(getManagedList19());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList19() {
		List list = new ArrayList();
		list.add(getGroupNewAction());
		list.add(getGroupEditAction());
		list.add(getGroupDelAction());
		list.add(getGroupRefreshAction());
		list.add(getGroupEnableAction());
		list.add(getGroupDisableAction());
		return list;
	}

	public nc.ui.hr.uif2.view.HrBillFormEditor getGroupEditor() {
		if (context.get("groupEditor") != null)
			return (nc.ui.hr.uif2.view.HrBillFormEditor) context
					.get("groupEditor");
		nc.ui.hr.uif2.view.HrBillFormEditor bean = new nc.ui.hr.uif2.view.HrBillFormEditor();
		context.put("groupEditor", bean);
		bean.setModel(getGroupModel());
		bean.setTemplateContainer(getTemplateContainer());
		bean.setNodekey("group");
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.action.GroupAddAction getGroupNewAction() {
		if (context.get("groupNewAction") != null)
			return (nc.ui.hi.keypsn.action.GroupAddAction) context
					.get("groupNewAction");
		nc.ui.hi.keypsn.action.GroupAddAction bean = new nc.ui.hi.keypsn.action.GroupAddAction();
		context.put("groupNewAction", bean);
		bean.setModel(getGroupModel());
		bean.setCardview(getGroupEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.action.GroupEditAction getGroupEditAction() {
		if (context.get("groupEditAction") != null)
			return (nc.ui.hi.keypsn.action.GroupEditAction) context
					.get("groupEditAction");
		nc.ui.hi.keypsn.action.GroupEditAction bean = new nc.ui.hi.keypsn.action.GroupEditAction();
		context.put("groupEditAction", bean);
		bean.setModel(getGroupModel());
		bean.setCardview(getGroupEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.action.GroupDelAction getGroupDelAction() {
		if (context.get("groupDelAction") != null)
			return (nc.ui.hi.keypsn.action.GroupDelAction) context
					.get("groupDelAction");
		nc.ui.hi.keypsn.action.GroupDelAction bean = new nc.ui.hi.keypsn.action.GroupDelAction();
		context.put("groupDelAction", bean);
		bean.setModel(getGroupModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.action.GroupRefreshAction getGroupRefreshAction() {
		if (context.get("groupRefreshAction") != null)
			return (nc.ui.hi.keypsn.action.GroupRefreshAction) context
					.get("groupRefreshAction");
		nc.ui.hi.keypsn.action.GroupRefreshAction bean = new nc.ui.hi.keypsn.action.GroupRefreshAction();
		context.put("groupRefreshAction", bean);
		bean.setModel(getGroupModel());
		bean.setDataManager(getGroupDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.action.GroupEnableAction getGroupEnableAction() {
		if (context.get("groupEnableAction") != null)
			return (nc.ui.hi.keypsn.action.GroupEnableAction) context
					.get("groupEnableAction");
		nc.ui.hi.keypsn.action.GroupEnableAction bean = new nc.ui.hi.keypsn.action.GroupEnableAction();
		context.put("groupEnableAction", bean);
		bean.setModel(getGroupModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.keypsn.action.GroupDisableAction getGroupDisableAction() {
		if (context.get("groupDisableAction") != null)
			return (nc.ui.hi.keypsn.action.GroupDisableAction) context
					.get("groupDisableAction");
		nc.ui.hi.keypsn.action.GroupDisableAction bean = new nc.ui.hi.keypsn.action.GroupDisableAction();
		context.put("groupDisableAction", bean);
		bean.setModel(getGroupModel());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.DefaultExceptionHanler getExceptionHandler() {
		if (context.get("exceptionHandler") != null)
			return (nc.ui.uif2.DefaultExceptionHanler) context
					.get("exceptionHandler");
		nc.ui.uif2.DefaultExceptionHanler bean = new nc.ui.uif2.DefaultExceptionHanler();
		context.put("exceptionHandler", bean);
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getTBNode_45cbc5());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_45cbc5() {
		if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#45cbc5") != null)
			return (nc.ui.uif2.tangramlayout.node.TBNode) context
					.get("nc.ui.uif2.tangramlayout.node.TBNode#45cbc5");
		nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
		context.put("nc.ui.uif2.tangramlayout.node.TBNode#45cbc5", bean);
		bean.setShowMode("CardLayout");
		bean.setTabs(getManagedList20());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList20() {
		List list = new ArrayList();
		list.add(getVSNode_a77c2d());
		list.add(getVSNode_17d1a3d());
		return list;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_a77c2d() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#a77c2d") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context
					.get("nc.ui.uif2.tangramlayout.node.VSNode#a77c2d");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#a77c2d", bean);
		bean.setShowMode("NoDivider");
		bean.setUp(getCNode_89a7b0());
		bean.setDown(getHSNode_412bdf());
		bean.setDividerLocation(30f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_89a7b0() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#89a7b0") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#89a7b0");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#89a7b0", bean);
		bean.setComponent(getPsndocPrimaryOrgPanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_412bdf() {
		if (context.get("nc.ui.uif2.tangramlayout.node.HSNode#412bdf") != null)
			return (nc.ui.uif2.tangramlayout.node.HSNode) context
					.get("nc.ui.uif2.tangramlayout.node.HSNode#412bdf");
		nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
		context.put("nc.ui.uif2.tangramlayout.node.HSNode#412bdf", bean);
		bean.setLeft(getVSNode_1536999());
		bean.setRight(getCNode_139f5ef());
		bean.setDividerLocation(320f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1536999() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#1536999") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context
					.get("nc.ui.uif2.tangramlayout.node.VSNode#1536999");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#1536999", bean);
		bean.setShowMode("NoDivider");
		bean.setUp(getCNode_1602bae());
		bean.setDown(getCNode_d3d538());
		bean.setDividerLocation(30f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1602bae() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1602bae") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#1602bae");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1602bae", bean);
		bean.setComponent(getGroupToolbar());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_d3d538() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#d3d538") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#d3d538");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#d3d538", bean);
		bean.setComponent(getTreePanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_139f5ef() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#139f5ef") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#139f5ef");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#139f5ef", bean);
		bean.setComponent(getPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_17d1a3d() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#17d1a3d") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context
					.get("nc.ui.uif2.tangramlayout.node.VSNode#17d1a3d");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#17d1a3d", bean);
		bean.setShowMode("NoDivider");
		bean.setUp(getCNode_1c995ac());
		bean.setDown(getCNode_1815332());
		bean.setDividerLocation(26f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1c995ac() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1c995ac") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#1c995ac");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1c995ac", bean);
		bean.setComponent(getEditorToolBarPanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1815332() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1815332") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#1815332");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1815332", bean);
		bean.setComponent(getPsndocFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.view.PsnPrimaryOrgPanel getPsndocPrimaryOrgPanel() {
		if (context.get("psndocPrimaryOrgPanel") != null)
			return (nc.ui.hi.psndoc.view.PsnPrimaryOrgPanel) context
					.get("psndocPrimaryOrgPanel");
		nc.ui.hi.psndoc.view.PsnPrimaryOrgPanel bean = new nc.ui.hi.psndoc.view.PsnPrimaryOrgPanel();
		context.put("psndocPrimaryOrgPanel", bean);
		bean.setModel(getManageAppModel());
		bean.setHasShowAllInfo(false);
		bean.setListView(getPsndocListView());
		bean.setPaginationModel(getPaginationModel());
		bean.setDataManager(getGroupDataManager());
		bean.setPk_orgtype("HRORGTYPE00000000000");
		bean.setLeftSuperModel(getGroupModel());
		bean.setPsnDataManager(getPsndocDataManager());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}
