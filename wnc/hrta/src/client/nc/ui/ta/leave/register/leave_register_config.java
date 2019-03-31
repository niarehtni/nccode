package nc.ui.ta.leave.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class leave_register_config extends AbstractJavaBeanDefinition {
	private Map<String, Object> context = new HashMap();

	public java.lang.Integer getFunc_type() {
		if (context.get("func_type") != null)
			return (java.lang.Integer) context.get("func_type");
		java.lang.Integer bean = new java.lang.Integer("4");
		context.put("func_type", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.vo.ta.pub.TALoginContext getContext() {
		if (context.get("context") != null)
			return (nc.vo.ta.pub.TALoginContext) context.get("context");
		nc.vo.ta.pub.TALoginContext bean = new nc.vo.ta.pub.TALoginContext();
		context.put("context", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public java.lang.String getNodekey() {
		if (context.get("nodekey") != null)
			return (java.lang.String) context.get("nodekey");
		java.lang.String bean = new java.lang.String("6017leavergst_b");
		context.put("nodekey", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public java.lang.String getQueryNodekey() {
		if (context.get("queryNodekey") != null)
			return (java.lang.String) context.get("queryNodekey");
		java.lang.String bean = new java.lang.String("6017leavergst_q");
		context.put("queryNodekey", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public java.lang.String getPrintNodekey() {
		if (context.get("printNodekey") != null)
			return (java.lang.String) context.get("printNodekey");
		java.lang.String bean = new java.lang.String("6017leavergst_p");
		context.put("printNodekey", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.pub.action.EnableJudge getEnableJudge() {
		if (context.get("enableJudge") != null)
			return (nc.ui.ta.pub.action.EnableJudge) context.get("enableJudge");
		nc.ui.ta.pub.action.EnableJudge bean = new nc.ui.ta.pub.action.EnableJudge();
		context.put("enableJudge", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.pub.QueryEditorListener getQueryEditorListener() {
		if (context.get("queryEditorListener") != null)
			return (nc.ui.ta.pub.QueryEditorListener) context.get("queryEditorListener");
		nc.ui.ta.pub.QueryEditorListener bean = new nc.ui.ta.pub.QueryEditorListener();
		context.put("queryEditorListener", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.model.LeaveRegModelService getManageModelService() {
		if (context.get("ManageModelService") != null)
			return (nc.ui.ta.leave.register.model.LeaveRegModelService) context.get("ManageModelService");
		nc.ui.ta.leave.register.model.LeaveRegModelService bean = new nc.ui.ta.leave.register.model.LeaveRegModelService();
		context.put("ManageModelService", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
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

	public nc.ui.uif2.FunNodeClosingHandler getClosingListener() {
		if (context.get("ClosingListener") != null)
			return (nc.ui.uif2.FunNodeClosingHandler) context.get("ClosingListener");
		nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
		context.put("ClosingListener", bean);
		bean.setModel(getManageAppModel());
		bean.setCancelaction(getCancelAction());
		bean.setSaveaction(getSaveAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.validator.BillNotNullValidateService getBillNotNullValidator() {
		if (context.get("billNotNullValidator") != null)
			return (nc.ui.hr.uif2.validator.BillNotNullValidateService) context.get("billNotNullValidator");
		nc.ui.hr.uif2.validator.BillNotNullValidateService bean = new nc.ui.hr.uif2.validator.BillNotNullValidateService(
				getBillFormEditor());
		context.put("billNotNullValidator", bean);
		bean.setBillForm(getBillFormEditor());
		bean.setNextValidateService(getLeaveRegValidator());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.vo.ta.leave.register.validator.SaveLeaveRegValidatorService getLeaveRegValidator() {
		if (context.get("leaveRegValidator") != null)
			return (nc.vo.ta.leave.register.validator.SaveLeaveRegValidatorService) context.get("leaveRegValidator");
		nc.vo.ta.leave.register.validator.SaveLeaveRegValidatorService bean = new nc.vo.ta.leave.register.validator.SaveLeaveRegValidatorService();
		context.put("leaveRegValidator", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.model.LeaveRegAppModel getManageAppModel() {
		if (context.get("ManageAppModel") != null)
			return (nc.ui.ta.leave.register.model.LeaveRegAppModel) context.get("ManageAppModel");
		nc.ui.ta.leave.register.model.LeaveRegAppModel bean = new nc.ui.ta.leave.register.model.LeaveRegAppModel();
		context.put("ManageAppModel", bean);
		bean.setService(getManageModelService());
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getCardEditorActions() {
		if (context.get("cardEditorActions") != null)
			return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context.get("cardEditorActions");
		nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(
				getBillFormEditor());
		context.put("cardEditorActions", bean);
		bean.setActions(getManagedList0());
		bean.setEditActions(getManagedList1());
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList0() {
		List list = new ArrayList();
		list.add(getAddActionGroup());
		list.add(getEditAction());
		list.add(getDeleteAction());
		list.add(getSeparatorAction());
		list.add(getRefreshAction());
		list.add(getSeparatorAction());
		list.add(getLeaveOffAction());
		list.add(getSeparatorAction());
		list.add(getCardPrintActiongroup());
		return list;
	}

	private List getManagedList1() {
		List list = new ArrayList();
		list.add(getSaveAction());
		list.add(getSaveAddAction());
		list.add(getSeparatorAction());
		list.add(getCancelAction());
		return list;
	}

	public nc.ui.ta.wf.batch.BatchAddAction getBatchAddAction() {
		if (context.get("BatchAddAction") != null)
			return (nc.ui.ta.wf.batch.BatchAddAction) context.get("BatchAddAction");
		nc.ui.ta.wf.batch.BatchAddAction bean = new nc.ui.ta.wf.batch.BatchAddAction();
		context.put("BatchAddAction", bean);
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setModel(getManageAppModel());
		bean.setCardForm(getBillFormEditor());
		bean.setListView(getListView());
		bean.setFunc_type(getFunc_type());
		bean.setFromApp(false);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getListViewActions() {
		if (context.get("listViewActions") != null)
			return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context.get("listViewActions");
		nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(
				getListView());
		context.put("listViewActions", bean);
		bean.setActions(getManagedList2());
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getAddActionGroup());
		list.add(getEditAction());
		list.add(getDeleteAction());
		list.add(getSeparatorAction());
		list.add(getLeaveOffAction());
		list.add(getSeparatorAction());
		list.add(getQueryAction());
		list.add(getRefreshAction());
		list.add(getSeparatorAction());
		list.add(getAssistActionGroup());
		list.add(getSeparatorAction());
		list.add(getPrintGroupAction());
		return list;
	}

	public nc.ui.ta.leave.register.model.TALeaveRegMetaDataSource getDatasource() {
		if (context.get("datasource") != null)
			return (nc.ui.ta.leave.register.model.TALeaveRegMetaDataSource) context.get("datasource");
		nc.ui.ta.leave.register.model.TALeaveRegMetaDataSource bean = new nc.ui.ta.leave.register.model.TALeaveRegMetaDataSource();
		context.put("datasource", bean);
		bean.setModel(getManageAppModel());
		bean.setSingleData(true);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.GroupAction getPrintGroupAction() {
		if (context.get("PrintGroupAction") != null)
			return (nc.funcnode.ui.action.GroupAction) context.get("PrintGroupAction");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("PrintGroupAction", bean);
		bean.setCode("print");
		bean.setName(getI18nFB_60f1c4());
		bean.setActions(getManagedList3());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_60f1c4() {
		if (context.get("nc.ui.uif2.I18nFB#60f1c4") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#60f1c4");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#60f1c4", bean);
		bean.setResDir("common");
		bean.setDefaultValue("打印");
		bean.setResId("UC001-0000007");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#60f1c4", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList3() {
		List list = new ArrayList();
		list.add(getPrintDirectAction());
		list.add(getPrintPreviewAction());
		list.add(getOutputAction());
		list.add(getSeparatorAction());
		list.add(getTemplatePrintAction());
		list.add(getTemplatePreviewAction());
		return list;
	}

	public nc.funcnode.ui.action.GroupAction getCardPrintActiongroup() {
		if (context.get("CardPrintActiongroup") != null)
			return (nc.funcnode.ui.action.GroupAction) context.get("CardPrintActiongroup");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("CardPrintActiongroup", bean);
		bean.setCode("cardprint");
		bean.setName(getI18nFB_a09ada());
		bean.setActions(getManagedList4());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_a09ada() {
		if (context.get("nc.ui.uif2.I18nFB#a09ada") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#a09ada");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#a09ada", bean);
		bean.setResDir("common");
		bean.setDefaultValue("打印");
		bean.setResId("UC001-0000007");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#a09ada", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList4() {
		List list = new ArrayList();
		list.add(getTemplatePrintAction());
		list.add(getTemplatePreviewAction());
		list.add(getCardOutputAction());
		return list;
	}

	public nc.ui.hr.uif2.action.print.TemplatePreviewAction getTemplatePreviewAction() {
		if (context.get("TemplatePreviewAction") != null)
			return (nc.ui.hr.uif2.action.print.TemplatePreviewAction) context.get("TemplatePreviewAction");
		nc.ui.hr.uif2.action.print.TemplatePreviewAction bean = new nc.ui.hr.uif2.action.print.TemplatePreviewAction();
		context.put("TemplatePreviewAction", bean);
		bean.setModel(getManageAppModel());
		bean.setNodeKey(getPrintNodekey());
		bean.setPrintDlgParentConatiner(getBillFormEditor());
		bean.setDatasource(getDatasource());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.action.print.TemplatePrintAction getTemplatePrintAction() {
		if (context.get("TemplatePrintAction") != null)
			return (nc.ui.hr.uif2.action.print.TemplatePrintAction) context.get("TemplatePrintAction");
		nc.ui.hr.uif2.action.print.TemplatePrintAction bean = new nc.ui.hr.uif2.action.print.TemplatePrintAction();
		context.put("TemplatePrintAction", bean);
		bean.setModel(getManageAppModel());
		bean.setNodeKey(getPrintNodekey());
		bean.setPrintDlgParentConatiner(getBillFormEditor());
		bean.setDatasource(getDatasource());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.action.print.ExportCardAction getCardOutputAction() {
		if (context.get("cardOutputAction") != null)
			return (nc.ui.hr.uif2.action.print.ExportCardAction) context.get("cardOutputAction");
		nc.ui.hr.uif2.action.print.ExportCardAction bean = new nc.ui.hr.uif2.action.print.ExportCardAction();
		context.put("cardOutputAction", bean);
		bean.setModel(getManageAppModel());
		bean.setNodeKey(getPrintNodekey());
		bean.setPrintDlgParentConatiner(getBillFormEditor());
		bean.setDatasource(getDatasource());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.pub.action.TAOutPutAction getOutputAction() {
		if (context.get("OutputAction") != null)
			return (nc.ui.ta.pub.action.TAOutPutAction) context.get("OutputAction");
		nc.ui.ta.pub.action.TAOutPutAction bean = new nc.ui.ta.pub.action.TAOutPutAction();
		context.put("OutputAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.pub.action.TADirectPreviewAction getPrintPreviewAction() {
		if (context.get("printPreviewAction") != null)
			return (nc.ui.ta.pub.action.TADirectPreviewAction) context.get("printPreviewAction");
		nc.ui.ta.pub.action.TADirectPreviewAction bean = new nc.ui.ta.pub.action.TADirectPreviewAction();
		context.put("printPreviewAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.pub.action.TADirectPrintAction getPrintDirectAction() {
		if (context.get("printDirectAction") != null)
			return (nc.ui.ta.pub.action.TADirectPrintAction) context.get("printDirectAction");
		nc.ui.ta.pub.action.TADirectPrintAction bean = new nc.ui.ta.pub.action.TADirectPrintAction();
		context.put("printDirectAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.pub.model.TaRegBillQueryDelegator getQueryDelegator() {
		if (context.get("QueryDelegator") != null)
			return (nc.ui.ta.pub.model.TaRegBillQueryDelegator) context.get("QueryDelegator");
		nc.ui.ta.pub.model.TaRegBillQueryDelegator bean = new nc.ui.ta.pub.model.TaRegBillQueryDelegator();
		context.put("QueryDelegator", bean);
		bean.setContext(getContext());
		bean.setModel(getManageAppModel());
		bean.setQueryEditorListener(getQueryEditorListener());
		bean.setQueryAreaShell(getQueryAreaShell());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.GroupAction getAddActionGroup() {
		if (context.get("addActionGroup") != null)
			return (nc.funcnode.ui.action.GroupAction) context.get("addActionGroup");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("addActionGroup", bean);
		bean.setCode("add");
		bean.setName(getI18nFB_16868a());
		bean.setActions(getManagedList5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_16868a() {
		if (context.get("nc.ui.uif2.I18nFB#16868a") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#16868a");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#16868a", bean);
		bean.setResDir("common");
		bean.setDefaultValue("新增");
		bean.setResId("UC001-0000108");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#16868a", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList5() {
		List list = new ArrayList();
		list.add(getAddAction());
		list.add(getAddLactationAction());
		list.add(getBatchAddAction());
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

	public nc.ui.ta.leave.register.action.AddLeaveRegAction getAddAction() {
		if (context.get("AddAction") != null)
			return (nc.ui.ta.leave.register.action.AddLeaveRegAction) context.get("AddAction");
		nc.ui.ta.leave.register.action.AddLeaveRegAction bean = new nc.ui.ta.leave.register.action.AddLeaveRegAction();
		context.put("AddAction", bean);
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.action.AddLactationRegAction getAddLactationAction() {
		if (context.get("AddLactationAction") != null)
			return (nc.ui.ta.leave.register.action.AddLactationRegAction) context.get("AddLactationAction");
		nc.ui.ta.leave.register.action.AddLactationRegAction bean = new nc.ui.ta.leave.register.action.AddLactationRegAction();
		context.put("AddLactationAction", bean);
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.action.EditLeaveRegAction getEditAction() {
		if (context.get("EditAction") != null)
			return (nc.ui.ta.leave.register.action.EditLeaveRegAction) context.get("EditAction");
		nc.ui.ta.leave.register.action.EditLeaveRegAction bean = new nc.ui.ta.leave.register.action.EditLeaveRegAction();
		context.put("EditAction", bean);
		bean.setModel(getManageAppModel());
		bean.setValidationService(getLeaveRegValidatorBean());
		bean.setMdOperateCode("Edit");
		bean.setResourceCode("60170leavergst");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.validator.LeaveRegValidator getLeaveRegValidatorBean() {
		if (context.get("leaveRegValidatorBean") != null)
			return (nc.ui.ta.leave.register.validator.LeaveRegValidator) context.get("leaveRegValidatorBean");
		nc.ui.ta.leave.register.validator.LeaveRegValidator bean = new nc.ui.ta.leave.register.validator.LeaveRegValidator();
		context.put("leaveRegValidatorBean", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.action.DeleteLeaveRegAction getDeleteAction() {
		if (context.get("DeleteAction") != null)
			return (nc.ui.ta.leave.register.action.DeleteLeaveRegAction) context.get("DeleteAction");
		nc.ui.ta.leave.register.action.DeleteLeaveRegAction bean = new nc.ui.ta.leave.register.action.DeleteLeaveRegAction();
		context.put("DeleteAction", bean);
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setModel(getManageAppModel());
		bean.setListView(getListView());
		bean.setCardView(getBillFormEditor());
		bean.setOperateCode("Delete");
		bean.setMdOperateCode("Delete");
		bean.setResourceCode("60170leavergst");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.action.SaveLeaveRegAction getSaveAction() {
		if (context.get("SaveAction") != null)
			return (nc.ui.ta.leave.register.action.SaveLeaveRegAction) context.get("SaveAction");
		nc.ui.ta.leave.register.action.SaveLeaveRegAction bean = new nc.ui.ta.leave.register.action.SaveLeaveRegAction();
		context.put("SaveAction", bean);
		bean.setModel(getManageAppModel());
		bean.setEditor(getBillFormEditor());
		bean.setValidationService(getBillNotNullValidator());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.action.SaveAddLeaveRegAction getSaveAddAction() {
		if (context.get("SaveAddAction") != null)
			return (nc.ui.ta.leave.register.action.SaveAddLeaveRegAction) context.get("SaveAddAction");
		nc.ui.ta.leave.register.action.SaveAddLeaveRegAction bean = new nc.ui.ta.leave.register.action.SaveAddLeaveRegAction();
		context.put("SaveAddAction", bean);
		bean.setModel(getManageAppModel());
		bean.setSaveAction(getSaveAction());
		bean.setAddAction(getAddAction());
		bean.setAddLactationAction(getAddLactationAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.action.CancelLeaveRegAction getCancelAction() {
		if (context.get("CancelAction") != null)
			return (nc.ui.ta.leave.register.action.CancelLeaveRegAction) context.get("CancelAction");
		nc.ui.ta.leave.register.action.CancelLeaveRegAction bean = new nc.ui.ta.leave.register.action.CancelLeaveRegAction();
		context.put("CancelAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.action.QueryLeaveRegAction getQueryAction() {
		if (context.get("QueryAction") != null)
			return (nc.ui.ta.leave.register.action.QueryLeaveRegAction) context.get("QueryAction");
		nc.ui.ta.leave.register.action.QueryLeaveRegAction bean = new nc.ui.ta.leave.register.action.QueryLeaveRegAction();
		context.put("QueryAction", bean);
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setModel(getManageAppModel());
		bean.setQueryDelegator(getQueryDelegator());
		bean.setDataManager(getModelDataManager());
		bean.setQueryExecutor(getQueryExcecutor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.pub.action.TAPFQueryExcecutor getQueryExcecutor() {
		if (context.get("queryExcecutor") != null)
			return (nc.ui.ta.pub.action.TAPFQueryExcecutor) context.get("queryExcecutor");
		nc.ui.ta.pub.action.TAPFQueryExcecutor bean = new nc.ui.ta.pub.action.TAPFQueryExcecutor();
		context.put("queryExcecutor", bean);
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.action.RefreshLeaveRegAction getRefreshAction() {
		if (context.get("RefreshAction") != null)
			return (nc.ui.ta.leave.register.action.RefreshLeaveRegAction) context.get("RefreshAction");
		nc.ui.ta.leave.register.action.RefreshLeaveRegAction bean = new nc.ui.ta.leave.register.action.RefreshLeaveRegAction();
		context.put("RefreshAction", bean);
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		bean.setFormEditor(getBillFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.action.LeaveOffAction getLeaveOffAction() {
		if (context.get("leaveOffAction") != null)
			return (nc.ui.ta.leave.register.action.LeaveOffAction) context.get("leaveOffAction");
		nc.ui.ta.leave.register.action.LeaveOffAction bean = new nc.ui.ta.leave.register.action.LeaveOffAction();
		context.put("leaveOffAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		bean.setMdOperateCode("Leaveoff");
		bean.setResourceCode("60170leavergst");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getAssistActionGroup() {
		if (context.get("assistActionGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context.get("assistActionGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("assistActionGroup", bean);
		bean.setCode("assist");
		bean.setName(getI18nFB_dad74d());
		bean.setActions(getManagedList6());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_dad74d() {
		if (context.get("nc.ui.uif2.I18nFB#dad74d") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#dad74d");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#dad74d", bean);
		bean.setResDir("common");
		bean.setDefaultValue("?助功能");
		bean.setResId("UC001-0000137");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#dad74d", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList6() {
		List list = new ArrayList();
		list.add(getFileManageAction());
		return list;
	}

	public nc.ui.ta.leave.register.action.LeaveRegFileManageAction getFileManageAction() {
		if (context.get("fileManageAction") != null)
			return (nc.ui.ta.leave.register.action.LeaveRegFileManageAction) context.get("fileManageAction");
		nc.ui.ta.leave.register.action.LeaveRegFileManageAction bean = new nc.ui.ta.leave.register.action.LeaveRegFileManageAction();
		context.put("fileManageAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.FirstLineAction getFirstLineAction() {
		if (context.get("FirstLineAction") != null)
			return (nc.ui.uif2.actions.FirstLineAction) context.get("FirstLineAction");
		nc.ui.uif2.actions.FirstLineAction bean = new nc.ui.uif2.actions.FirstLineAction();
		context.put("FirstLineAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.PreLineAction getPreLineAction() {
		if (context.get("PreLineAction") != null)
			return (nc.ui.uif2.actions.PreLineAction) context.get("PreLineAction");
		nc.ui.uif2.actions.PreLineAction bean = new nc.ui.uif2.actions.PreLineAction();
		context.put("PreLineAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.NextLineAction getNextLineAction() {
		if (context.get("NextLineAction") != null)
			return (nc.ui.uif2.actions.NextLineAction) context.get("NextLineAction");
		nc.ui.uif2.actions.NextLineAction bean = new nc.ui.uif2.actions.NextLineAction();
		context.put("NextLineAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.LastLineAction getLastLineAction() {
		if (context.get("LastLineAction") != null)
			return (nc.ui.uif2.actions.LastLineAction) context.get("LastLineAction");
		nc.ui.uif2.actions.LastLineAction bean = new nc.ui.uif2.actions.LastLineAction();
		context.put("LastLineAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.model.LeaveRegModelDataManager getModelDataManager() {
		if (context.get("modelDataManager") != null)
			return (nc.ui.ta.leave.register.model.LeaveRegModelDataManager) context.get("modelDataManager");
		nc.ui.ta.leave.register.model.LeaveRegModelDataManager bean = new nc.ui.ta.leave.register.model.LeaveRegModelDataManager();
		context.put("modelDataManager", bean);
		bean.setModel(getManageAppModel());
		bean.setService(getManageModelService());
		bean.setContext(getContext());
		bean.setPaginationModel(getPaginationModel());
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
		bean.setNodeKeies(getManagedList7());
		bean.load();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList7() {
		List list = new ArrayList();
		list.add(getNodekey());
		return list;
	}

	public nc.ui.uif2.actions.QueryAreaShell getQueryAreaShell() {
		if (context.get("queryAreaShell") != null)
			return (nc.ui.uif2.actions.QueryAreaShell) context.get("queryAreaShell");
		nc.ui.uif2.actions.QueryAreaShell bean = new nc.ui.uif2.actions.QueryAreaShell();
		context.put("queryAreaShell", bean);
		bean.setQueryArea(getQueryAction_created_16d02b8());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.queryarea.QueryArea getQueryAction_created_16d02b8() {
		if (context.get("QueryAction.created#16d02b8") != null)
			return (nc.ui.queryarea.QueryArea) context.get("QueryAction.created#16d02b8");
		nc.ui.queryarea.QueryArea bean = getQueryAction().createQueryArea();
		context.put("QueryAction.created#16d02b8", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.view.LeaveRegListView getListView() {
		if (context.get("listView") != null)
			return (nc.ui.ta.leave.register.view.LeaveRegListView) context.get("listView");
		nc.ui.ta.leave.register.view.LeaveRegListView bean = new nc.ui.ta.leave.register.view.LeaveRegListView();
		context.put("listView", bean);
		bean.setModel(getManageAppModel());
		bean.setMultiSelectionEnable(true);
		bean.setMultiSelectionMode(1);
		bean.setNodekey(getNodekey());
		bean.setTemplateContainer(getTemplateContainer());
		bean.setDealHyperlink(true);
		bean.setNorth(getListToolBarPanel());
		bean.setPaginationBar(getPaginationBar());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getListToolBarPanel() {
		if (context.get("listToolBarPanel") != null)
			return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel) context.get("listToolBarPanel");
		nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
		context.put("listToolBarPanel", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.register.view.LeaveRegCardView getBillFormEditor() {
		if (context.get("billFormEditor") != null)
			return (nc.ui.ta.leave.register.view.LeaveRegCardView) context.get("billFormEditor");
		nc.ui.ta.leave.register.view.LeaveRegCardView bean = new nc.ui.ta.leave.register.view.LeaveRegCardView();
		context.put("billFormEditor", bean);
		bean.setModel(getManageAppModel());
		bean.setNodekey(getNodekey());
		bean.setTemplateContainer(getTemplateContainer());
		bean.setActions(getManagedList8());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList8() {
		List list = new ArrayList();
		list.add(getFirstLineAction());
		list.add(getPreLineAction());
		list.add(getNextLineAction());
		list.add(getLastLineAction());
		return list;
	}

	public nc.ui.hr.uif2.mediator.HyperLinkClickMediator getMouseClickShowPanelMediator() {
		if (context.get("mouseClickShowPanelMediator") != null)
			return (nc.ui.hr.uif2.mediator.HyperLinkClickMediator) context.get("mouseClickShowPanelMediator");
		nc.ui.hr.uif2.mediator.HyperLinkClickMediator bean = new nc.ui.hr.uif2.mediator.HyperLinkClickMediator();
		context.put("mouseClickShowPanelMediator", bean);
		bean.setModel(getManageAppModel());
		bean.setShowUpComponent(getBillFormEditor());
		bean.setHyperLinkColumn("pk_psnjob");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors() {
		if (context.get("toftpanelActionContributors") != null)
			return (nc.ui.uif2.actions.ActionContributors) context.get("toftpanelActionContributors");
		nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
		context.put("toftpanelActionContributors", bean);
		bean.setContributors(getManagedList9());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList9() {
		List list = new ArrayList();
		list.add(getListViewActions());
		list.add(getCardEditorActions());
		return list;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getTBNode_da2e0());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_da2e0() {
		if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#da2e0") != null)
			return (nc.ui.uif2.tangramlayout.node.TBNode) context.get("nc.ui.uif2.tangramlayout.node.TBNode#da2e0");
		nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
		context.put("nc.ui.uif2.tangramlayout.node.TBNode#da2e0", bean);
		bean.setShowMode("CardLayout");
		bean.setTabs(getManagedList10());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList10() {
		List list = new ArrayList();
		list.add(getVSNode_12de7cf());
		list.add(getVSNode_30be0e());
		return list;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_12de7cf() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#12de7cf") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#12de7cf");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#12de7cf", bean);
		bean.setShowMode("NoDivider");
		bean.setUp(getCNode_1cd9c82());
		bean.setDown(getHSNode_15c8537());
		bean.setDividerLocation(30f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1cd9c82() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1cd9c82") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1cd9c82");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1cd9c82", bean);
		bean.setComponent(getOrgPanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_15c8537() {
		if (context.get("nc.ui.uif2.tangramlayout.node.HSNode#15c8537") != null)
			return (nc.ui.uif2.tangramlayout.node.HSNode) context.get("nc.ui.uif2.tangramlayout.node.HSNode#15c8537");
		nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
		context.put("nc.ui.uif2.tangramlayout.node.HSNode#15c8537", bean);
		bean.setLeft(getCNode_1de552c());
		bean.setRight(getCNode_1a9f61b());
		bean.setDividerLocation(0.2f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1de552c() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1de552c") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1de552c");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1de552c", bean);
		bean.setComponent(getQueryAreaShell());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1a9f61b() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1a9f61b") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1a9f61b");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1a9f61b", bean);
		bean.setComponent(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_30be0e() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#30be0e") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#30be0e");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#30be0e", bean);
		bean.setShowMode("NoDivider");
		bean.setUp(getCNode_cdd706());
		bean.setDown(getCNode_1e00bf3());
		bean.setDividerLocation(26f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_cdd706() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#cdd706") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#cdd706");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#cdd706", bean);
		bean.setComponent(getEditorToolBarPanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1e00bf3() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1e00bf3") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1e00bf3");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1e00bf3", bean);
		bean.setComponent(getBillFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getEditorToolBarPanel() {
		if (context.get("editorToolBarPanel") != null)
			return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel) context.get("editorToolBarPanel");
		nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
		context.put("editorToolBarPanel", bean);
		bean.setModel(getManageAppModel());
		bean.setTitleAction(getEditorReturnAction());
		bean.setActions(getManagedList11());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList11() {
		List list = new ArrayList();
		list.add(getFirstLineAction());
		list.add(getPreLineAction());
		list.add(getNextLineAction());
		list.add(getLastLineAction());
		return list;
	}

	public nc.ui.uif2.actions.ShowMeUpAction getEditorReturnAction() {
		if (context.get("editorReturnAction") != null)
			return (nc.ui.uif2.actions.ShowMeUpAction) context.get("editorReturnAction");
		nc.ui.uif2.actions.ShowMeUpAction bean = new nc.ui.uif2.actions.ShowMeUpAction();
		context.put("editorReturnAction", bean);
		bean.setGoComponent(getListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.leave.pf.view.LeavePrimaryOrgPanel getOrgPanel() {
		if (context.get("orgPanel") != null)
			return (nc.ui.ta.leave.pf.view.LeavePrimaryOrgPanel) context.get("orgPanel");
		nc.ui.ta.leave.pf.view.LeavePrimaryOrgPanel bean = new nc.ui.ta.leave.pf.view.LeavePrimaryOrgPanel();
		context.put("orgPanel", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		bean.setPk_orgtype("HRORGTYPE00000000000");
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.components.pagination.PaginationBar getPaginationBar() {
		if (context.get("paginationBar") != null)
			return (nc.ui.uif2.components.pagination.PaginationBar) context.get("paginationBar");
		nc.ui.uif2.components.pagination.PaginationBar bean = new nc.ui.uif2.components.pagination.PaginationBar();
		context.put("paginationBar", bean);
		bean.setPaginationModel(getPaginationModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.components.pagination.PaginationModel getPaginationModel() {
		if (context.get("paginationModel") != null)
			return (nc.ui.uif2.components.pagination.PaginationModel) context.get("paginationModel");
		nc.ui.uif2.components.pagination.PaginationModel bean = new nc.ui.uif2.components.pagination.PaginationModel();
		context.put("paginationModel", bean);
		bean.setPaginationQueryService(getManageModelService());
		bean.init();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener getInitDataListener() {
		if (context.get("initDataListener") != null)
			return (nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener) context.get("initDataListener");
		nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener bean = new nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener();
		context.put("initDataListener", bean);
		bean.setModel(getManageAppModel());
		bean.setContext(getContext());
		bean.setVoClassName("nc.vo.ta.leave.LeaveRegVO");
		bean.setAutoShowUpComponent(getBillFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}
