package nc.ui.ta.psndoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class psndoc_config extends AbstractJavaBeanDefinition {
	private Map<String, Object> context = new HashMap();

	public nc.vo.ta.pub.TADefDocLoginContext getContext() {
		if (context.get("context") != null)
			return (nc.vo.ta.pub.TADefDocLoginContext) context.get("context");
		nc.vo.ta.pub.TADefDocLoginContext bean = new nc.vo.ta.pub.TADefDocLoginContext();
		context.put("context", bean);
		bean.setNodeType(nc.vo.bd.pub.NODE_TYPE.ORG_NODE);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public java.lang.String getResourceCode() {
		if (context.get("resourceCode") != null)
			return (java.lang.String) context.get("resourceCode");
		java.lang.String bean = new java.lang.String("60170psndoc");
		context.put("resourceCode", bean);
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

	public nc.ui.ta.psndoc.model.TbmPsndocAppModelService getModelService() {
		if (context.get("modelService") != null)
			return (nc.ui.ta.psndoc.model.TbmPsndocAppModelService) context.get("modelService");
		nc.ui.ta.psndoc.model.TbmPsndocAppModelService bean = new nc.ui.ta.psndoc.model.TbmPsndocAppModelService();
		context.put("modelService", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.view.CardListEditor getCardListEditor() {
		if (context.get("cardListEditor") != null)
			return (nc.ui.ta.psndoc.view.CardListEditor) context.get("cardListEditor");
		nc.ui.ta.psndoc.view.CardListEditor bean = new nc.ui.ta.psndoc.view.CardListEditor();
		context.put("cardListEditor", bean);
		bean.setCardEditor(getTbmPsndocFormEditor());
		bean.setListEditor(getTbmPsndocListView());
		bean.setModel(getTbmPsndocModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.model.TbmPsndocAppModel getTbmPsndocModel() {
		if (context.get("tbmPsndocModel") != null)
			return (nc.ui.ta.psndoc.model.TbmPsndocAppModel) context.get("tbmPsndocModel");
		nc.ui.ta.psndoc.model.TbmPsndocAppModel bean = new nc.ui.ta.psndoc.model.TbmPsndocAppModel();
		context.put("tbmPsndocModel", bean);
		bean.setService(getModelService());
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		bean.setContext(getContext());
		bean.setResourceCode(getResourceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.model.TbmPsndocAppModelDataManager getModelDataManager() {
		if (context.get("modelDataManager") != null)
			return (nc.ui.ta.psndoc.model.TbmPsndocAppModelDataManager) context.get("modelDataManager");
		nc.ui.ta.psndoc.model.TbmPsndocAppModelDataManager bean = new nc.ui.ta.psndoc.model.TbmPsndocAppModelDataManager();
		context.put("modelDataManager", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setService(getModelService());
		bean.setContext(getContext());
		bean.setPaginationModel(getPaginationModel());
		bean.setPaginationBar(getPaginationBar());
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
		bean.setPaginationQueryService(getModelService());
		bean.init();
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
		list.add("6017psndoc");
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

	public nc.ui.ta.pub.action.EnableJudge getEnableJudge() {
		if (context.get("enableJudge") != null)
			return (nc.ui.ta.pub.action.EnableJudge) context.get("enableJudge");
		nc.ui.ta.pub.action.EnableJudge bean = new nc.ui.ta.pub.action.EnableJudge();
		context.put("enableJudge", bean);
		bean.setModel(getTbmPsndocModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.AddAction getAddAction() {
		if (context.get("addAction") != null)
			return (nc.ui.ta.psndoc.action.AddAction) context.get("addAction");
		nc.ui.ta.psndoc.action.AddAction bean = new nc.ui.ta.psndoc.action.AddAction();
		context.put("addAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setCardForm(getTbmPsndocFormEditor());
		bean.setNcActionStatusJudge(getEnableJudge());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.AssignAction getAssignAction() {
		if (context.get("assignAction") != null)
			return (nc.ui.ta.psndoc.action.AssignAction) context.get("assignAction");
		nc.ui.ta.psndoc.action.AssignAction bean = new nc.ui.ta.psndoc.action.AssignAction();
		context.put("assignAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setDataManager(getModelDataManager());
		bean.setListView(getTbmPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.BatchEditAction getBatchEditActon() {
		if (context.get("batchEditActon") != null)
			return (nc.ui.ta.psndoc.action.BatchEditAction) context.get("batchEditActon");
		nc.ui.ta.psndoc.action.BatchEditAction bean = new nc.ui.ta.psndoc.action.BatchEditAction();
		context.put("batchEditActon", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setDataManager(getModelDataManager());
		bean.setListView(getTbmPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.CancelAction getCancelAction() {
		if (context.get("cancelAction") != null)
			return (nc.ui.ta.psndoc.action.CancelAction) context.get("cancelAction");
		nc.ui.ta.psndoc.action.CancelAction bean = new nc.ui.ta.psndoc.action.CancelAction();
		context.put("cancelAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setCurrentTabShowJudge(getCurrentTabShowJudge());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.ChgpsnAction getChgpsnActon() {
		if (context.get("chgpsnActon") != null)
			return (nc.ui.ta.psndoc.action.ChgpsnAction) context.get("chgpsnActon");
		nc.ui.ta.psndoc.action.ChgpsnAction bean = new nc.ui.ta.psndoc.action.ChgpsnAction();
		context.put("chgpsnActon", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.DeleteAction getDeleteAction() {
		if (context.get("deleteAction") != null)
			return (nc.ui.ta.psndoc.action.DeleteAction) context.get("deleteAction");
		nc.ui.ta.psndoc.action.DeleteAction bean = new nc.ui.ta.psndoc.action.DeleteAction();
		context.put("deleteAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setCurrentTabShowJudge(getCurrentTabShowJudge());
		bean.setMdOperateCode("Delete");
		bean.setResourceCode("60170psndoc");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.EditAction getEditAction() {
		if (context.get("editAction") != null)
			return (nc.ui.ta.psndoc.action.EditAction) context.get("editAction");
		nc.ui.ta.psndoc.action.EditAction bean = new nc.ui.ta.psndoc.action.EditAction();
		context.put("editAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setNcActionStatusJudge(getEnableJudge());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getFilterAction() {
		if (context.get("filterAction") != null)
			return (nc.funcnode.ui.action.MenuAction) context.get("filterAction");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("filterAction", bean);
		bean.setCode(getI18nFB_6e61d2());
		bean.setName(getI18nFB_d85894());
		bean.setActions(getManagedList1());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_6e61d2() {
		if (context.get("nc.ui.uif2.I18nFB#6e61d2") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#6e61d2");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#6e61d2", bean);
		bean.setResDir("common");
		bean.setDefaultValue("??");
		bean.setResId("UCH069");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#6e61d2", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private java.lang.String getI18nFB_d85894() {
		if (context.get("nc.ui.uif2.I18nFB#d85894") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#d85894");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#d85894", bean);
		bean.setResDir("common");
		bean.setDefaultValue("??");
		bean.setResId("UCH069");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#d85894", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList1() {
		List list = new ArrayList();
		list.add(getShowEndDocAction());
		return list;
	}

	public nc.ui.uif2.FunNodeClosingHandler getClosingListener() {
		if (context.get("ClosingListener") != null)
			return (nc.ui.uif2.FunNodeClosingHandler) context.get("ClosingListener");
		nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
		context.put("ClosingListener", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setSaveaction(getSaveAction());
		bean.setCancelaction(getCancelAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.ShowEndDocAciotn getShowEndDocAction() {
		if (context.get("showEndDocAction") != null)
			return (nc.ui.ta.psndoc.action.ShowEndDocAciotn) context.get("showEndDocAction");
		nc.ui.ta.psndoc.action.ShowEndDocAciotn bean = new nc.ui.ta.psndoc.action.ShowEndDocAciotn();
		context.put("showEndDocAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getExOrImpActionGroup() {
		if (context.get("exOrImpActionGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context.get("exOrImpActionGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("exOrImpActionGroup", bean);
		bean.setCode("exOrImp");
		bean.setName(getI18nFB_19f987a());
		bean.setActions(getManagedList2());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_19f987a() {
		if (context.get("nc.ui.uif2.I18nFB#19f987a") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#19f987a");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#19f987a", bean);
		bean.setResDir("6017basedoc");
		bean.setDefaultValue("?入?出");
		bean.setResId("06017basedoc1818");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#19f987a", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getImportAction());
		list.add(getExportAction());
		return list;
	}

	public nc.ui.ta.psndoc.action.ExportAction getExportAction() {
		if (context.get("exportAction") != null)
			return (nc.ui.ta.psndoc.action.ExportAction) context.get("exportAction");
		nc.ui.ta.psndoc.action.ExportAction bean = new nc.ui.ta.psndoc.action.ExportAction();
		context.put("exportAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setListView(getTbmPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.ImportAction getImportAction() {
		if (context.get("importAction") != null)
			return (nc.ui.ta.psndoc.action.ImportAction) context.get("importAction");
		nc.ui.ta.psndoc.action.ImportAction bean = new nc.ui.ta.psndoc.action.ImportAction();
		context.put("importAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.QueryAction getQueryAction() {
		if (context.get("queryAction") != null)
			return (nc.ui.ta.psndoc.action.QueryAction) context.get("queryAction");
		nc.ui.ta.psndoc.action.QueryAction bean = new nc.ui.ta.psndoc.action.QueryAction();
		context.put("queryAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setDataManager(getModelDataManager());
		bean.setQueryDelegator(getHrQueryDelegator2_d385e8());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.hr.uif2.HrQueryDelegator2 getHrQueryDelegator2_d385e8() {
		if (context.get("nc.ui.hr.uif2.HrQueryDelegator2#d385e8") != null)
			return (nc.ui.hr.uif2.HrQueryDelegator2) context.get("nc.ui.hr.uif2.HrQueryDelegator2#d385e8");
		nc.ui.hr.uif2.HrQueryDelegator2 bean = new nc.ui.hr.uif2.HrQueryDelegator2();
		context.put("nc.ui.hr.uif2.HrQueryDelegator2#d385e8", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setNodeKey("6017psndoc");
		bean.setContext(getContext());
		bean.setQueryEditorListener(getQueryEditorListener());
		bean.getQueryDlg();
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

	public nc.ui.ta.psndoc.action.BatchAddAction getBatchAddAction() {
		if (context.get("batchAddAction") != null)
			return (nc.ui.ta.psndoc.action.BatchAddAction) context.get("batchAddAction");
		nc.ui.ta.psndoc.action.BatchAddAction bean = new nc.ui.ta.psndoc.action.BatchAddAction();
		context.put("batchAddAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setCardForm(getTbmPsndocFormEditor());
		bean.setListView(getTbmPsndocListView());
		bean.setDataManager(getModelDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.action.RefreshAction getRefreshAction() {
		if (context.get("refreshAction") != null)
			return (nc.ui.hr.uif2.action.RefreshAction) context.get("refreshAction");
		nc.ui.hr.uif2.action.RefreshAction bean = new nc.ui.hr.uif2.action.RefreshAction();
		context.put("refreshAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setDataManager(getModelDataManager());
		bean.setFormEditor(getTbmPsndocFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.SaveAction getSaveAction() {
		if (context.get("saveAction") != null)
			return (nc.ui.ta.psndoc.action.SaveAction) context.get("saveAction");
		nc.ui.ta.psndoc.action.SaveAction bean = new nc.ui.ta.psndoc.action.SaveAction();
		context.put("saveAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setCardListEditor(getCardListEditor());
		bean.setValidator(getManagedList3());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList3() {
		List list = new ArrayList();
		list.add(getNotNullValidator());
		list.add(getNotBlankValidator());
		list.add(getTimeCardIDValidator());
		return list;
	}

	private nc.ui.ta.psndoc.validator.NotNullValidator getNotNullValidator() {
		if (context.get("notNullValidator") != null)
			return (nc.ui.ta.psndoc.validator.NotNullValidator) context.get("notNullValidator");
		nc.ui.ta.psndoc.validator.NotNullValidator bean = new nc.ui.ta.psndoc.validator.NotNullValidator();
		context.put("notNullValidator", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.ta.psndoc.validator.NotBlankValidator getNotBlankValidator() {
		if (context.get("notBlankValidator") != null)
			return (nc.ui.ta.psndoc.validator.NotBlankValidator) context.get("notBlankValidator");
		nc.ui.ta.psndoc.validator.NotBlankValidator bean = new nc.ui.ta.psndoc.validator.NotBlankValidator();
		context.put("notBlankValidator", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.ta.psndoc.validator.TimeCardIDValidator getTimeCardIDValidator() {
		if (context.get("timeCardIDValidator") != null)
			return (nc.ui.ta.psndoc.validator.TimeCardIDValidator) context.get("timeCardIDValidator");
		nc.ui.ta.psndoc.validator.TimeCardIDValidator bean = new nc.ui.ta.psndoc.validator.TimeCardIDValidator();
		context.put("timeCardIDValidator", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.model.HRMetaDataDataSource getDatasource() {
		if (context.get("datasource") != null)
			return (nc.ui.hr.uif2.model.HRMetaDataDataSource) context.get("datasource");
		nc.ui.hr.uif2.model.HRMetaDataDataSource bean = new nc.ui.hr.uif2.model.HRMetaDataDataSource();
		context.put("datasource", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setSingleData(true);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.SetTempCardAction getSetTempCardAction() {
		if (context.get("setTempCardAction") != null)
			return (nc.ui.ta.psndoc.action.SetTempCardAction) context.get("setTempCardAction");
		nc.ui.ta.psndoc.action.SetTempCardAction bean = new nc.ui.ta.psndoc.action.SetTempCardAction();
		context.put("setTempCardAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setNcActionStatusJudge(getEnableJudge());
		bean.setRelNodeCode("60170tempcard");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.TaPsnDirectPreviewAction getPrintPreviewAction() {
		if (context.get("printPreviewAction") != null)
			return (nc.ui.ta.psndoc.action.TaPsnDirectPreviewAction) context.get("printPreviewAction");
		nc.ui.ta.psndoc.action.TaPsnDirectPreviewAction bean = new nc.ui.ta.psndoc.action.TaPsnDirectPreviewAction();
		context.put("printPreviewAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setListView(getTbmPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.TaPsnDirectPrintAction getPrintDirectAction() {
		if (context.get("printDirectAction") != null)
			return (nc.ui.ta.psndoc.action.TaPsnDirectPrintAction) context.get("printDirectAction");
		nc.ui.ta.psndoc.action.TaPsnDirectPrintAction bean = new nc.ui.ta.psndoc.action.TaPsnDirectPrintAction();
		context.put("printDirectAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setListView(getTbmPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.TaPsnOutPutAction getListOutputAction() {
		if (context.get("listOutputAction") != null)
			return (nc.ui.ta.psndoc.action.TaPsnOutPutAction) context.get("listOutputAction");
		nc.ui.ta.psndoc.action.TaPsnOutPutAction bean = new nc.ui.ta.psndoc.action.TaPsnOutPutAction();
		context.put("listOutputAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setListView(getTbmPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.TaPsndocTemplatePreviewAction getTemplatePreviewAction() {
		if (context.get("TemplatePreviewAction") != null)
			return (nc.ui.ta.psndoc.action.TaPsndocTemplatePreviewAction) context.get("TemplatePreviewAction");
		nc.ui.ta.psndoc.action.TaPsndocTemplatePreviewAction bean = new nc.ui.ta.psndoc.action.TaPsndocTemplatePreviewAction();
		context.put("TemplatePreviewAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setPrintDlgParentConatiner(getTbmPsndocFormEditor());
		bean.setDatasource(getDatasource());
		bean.setNodeKey("6017psndoc");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.action.TaPsndocTemplatePrintAction getTemplatePrintAction() {
		if (context.get("TemplatePrintAction") != null)
			return (nc.ui.ta.psndoc.action.TaPsndocTemplatePrintAction) context.get("TemplatePrintAction");
		nc.ui.ta.psndoc.action.TaPsndocTemplatePrintAction bean = new nc.ui.ta.psndoc.action.TaPsndocTemplatePrintAction();
		context.put("TemplatePrintAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setPrintDlgParentConatiner(getTbmPsndocFormEditor());
		bean.setDatasource(getDatasource());
		bean.setNodeKey("6017psndoc");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.FirstLineAction getFirstLineAction() {
		if (context.get("FirstLineAction") != null)
			return (nc.ui.uif2.actions.FirstLineAction) context.get("FirstLineAction");
		nc.ui.uif2.actions.FirstLineAction bean = new nc.ui.uif2.actions.FirstLineAction();
		context.put("FirstLineAction", bean);
		bean.setModel(getTbmPsndocModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.PreLineAction getPreLineAction() {
		if (context.get("PreLineAction") != null)
			return (nc.ui.uif2.actions.PreLineAction) context.get("PreLineAction");
		nc.ui.uif2.actions.PreLineAction bean = new nc.ui.uif2.actions.PreLineAction();
		context.put("PreLineAction", bean);
		bean.setModel(getTbmPsndocModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.NextLineAction getNextLineAction() {
		if (context.get("NextLineAction") != null)
			return (nc.ui.uif2.actions.NextLineAction) context.get("NextLineAction");
		nc.ui.uif2.actions.NextLineAction bean = new nc.ui.uif2.actions.NextLineAction();
		context.put("NextLineAction", bean);
		bean.setModel(getTbmPsndocModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.LastLineAction getLastLineAction() {
		if (context.get("LastLineAction") != null)
			return (nc.ui.uif2.actions.LastLineAction) context.get("LastLineAction");
		nc.ui.uif2.actions.LastLineAction bean = new nc.ui.uif2.actions.LastLineAction();
		context.put("LastLineAction", bean);
		bean.setModel(getTbmPsndocModel());
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
		bean.setName(getI18nFB_fc72fb());
		bean.setActions(getManagedList4());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_fc72fb() {
		if (context.get("nc.ui.uif2.I18nFB#fc72fb") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#fc72fb");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#fc72fb", bean);
		bean.setResDir("common");
		bean.setDefaultValue("新增");
		bean.setResId("UC001-0000108");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#fc72fb", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList4() {
		List list = new ArrayList();
		list.add(getAddAction());
		list.add(getBatchAddAction());
		list.add(getChgpsnActon());
		return list;
	}

	public nc.funcnode.ui.action.GroupAction getEditActionGroup() {
		if (context.get("editActionGroup") != null)
			return (nc.funcnode.ui.action.GroupAction) context.get("editActionGroup");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("editActionGroup", bean);
		bean.setCode("edit");
		bean.setName(getI18nFB_1e0c573());
		bean.setActions(getManagedList5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1e0c573() {
		if (context.get("nc.ui.uif2.I18nFB#1e0c573") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1e0c573");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1e0c573", bean);
		bean.setResDir("common");
		bean.setDefaultValue("修改");
		bean.setResId("UC001-0000045");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1e0c573", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList5() {
		List list = new ArrayList();
		list.add(getEditAction());
		list.add(getBatchEditActon());
		return list;
	}

	public nc.ui.hr.uif2.validator.BillFormNotNullValidator getValidateBillFormNotNull() {
		if (context.get("ValidateBillFormNotNull") != null)
			return (nc.ui.hr.uif2.validator.BillFormNotNullValidator) context.get("ValidateBillFormNotNull");
		nc.ui.hr.uif2.validator.BillFormNotNullValidator bean = new nc.ui.hr.uif2.validator.BillFormNotNullValidator();
		context.put("ValidateBillFormNotNull", bean);
		bean.setBillForm(getTbmPsndocFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.GroupAction getPrintActiongroup() {
		if (context.get("PrintActiongroup") != null)
			return (nc.funcnode.ui.action.GroupAction) context.get("PrintActiongroup");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("PrintActiongroup", bean);
		bean.setCode("printgroup");
		bean.setName(getI18nFB_1c3188c());
		bean.setActions(getManagedList6());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1c3188c() {
		if (context.get("nc.ui.uif2.I18nFB#1c3188c") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1c3188c");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1c3188c", bean);
		bean.setResDir("common");
		bean.setDefaultValue("打印");
		bean.setResId("UC001-0000007");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1c3188c", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

	public nc.funcnode.ui.action.GroupAction getCardPrintActiongroup() {
		if (context.get("CardPrintActiongroup") != null)
			return (nc.funcnode.ui.action.GroupAction) context.get("CardPrintActiongroup");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("CardPrintActiongroup", bean);
		bean.setCode("cardprintgroup");
		bean.setName(getI18nFB_180d0db());
		bean.setActions(getManagedList7());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_180d0db() {
		if (context.get("nc.ui.uif2.I18nFB#180d0db") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#180d0db");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#180d0db", bean);
		bean.setResDir("common");
		bean.setDefaultValue("打印");
		bean.setResId("UC001-0000007");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#180d0db", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList7() {
		List list = new ArrayList();
		list.add(getTemplatePrintAction());
		list.add(getTemplatePreviewAction());
		list.add(getCardOutputAction());
		return list;
	}

	public nc.ui.hr.uif2.action.print.ExportCardAction getCardOutputAction() {
		if (context.get("cardOutputAction") != null)
			return (nc.ui.hr.uif2.action.print.ExportCardAction) context.get("cardOutputAction");
		nc.ui.hr.uif2.action.print.ExportCardAction bean = new nc.ui.hr.uif2.action.print.ExportCardAction();
		context.put("cardOutputAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setPrintDlgParentConatiner(getTbmPsndocFormEditor());
		bean.setDatasource(getDatasource());
		bean.setNodeKey("6017psndoc");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.pub.view.TBMCurrentShowingJudge getCurrentTabShowJudge() {
		if (context.get("currentTabShowJudge") != null)
			return (nc.ui.ta.pub.view.TBMCurrentShowingJudge) context.get("currentTabShowJudge");
		nc.ui.ta.pub.view.TBMCurrentShowingJudge bean = new nc.ui.ta.pub.view.TBMCurrentShowingJudge();
		context.put("currentTabShowJudge", bean);
		bean.setListView(getTbmPsndocListView());
		bean.setCardView(getTbmPsndocFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.view.TbmPsndocListView getTbmPsndocListView() {
		if (context.get("tbmPsndocListView") != null)
			return (nc.ui.ta.psndoc.view.TbmPsndocListView) context.get("tbmPsndocListView");
		nc.ui.ta.psndoc.view.TbmPsndocListView bean = new nc.ui.ta.psndoc.view.TbmPsndocListView();
		context.put("tbmPsndocListView", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setMultiSelectionEnable(true);
		bean.setMultiSelectionMode(1);
		bean.setNodekey("6017psndoc");
		bean.setPos("head");
		bean.setSouth(getPaginationBar());
		bean.setDealHyperlink(true);
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.ta.psndoc.view.TbmPsndocFormEditor getTbmPsndocFormEditor() {
		if (context.get("tbmPsndocFormEditor") != null)
			return (nc.ui.ta.psndoc.view.TbmPsndocFormEditor) context.get("tbmPsndocFormEditor");
		nc.ui.ta.psndoc.view.TbmPsndocFormEditor bean = new nc.ui.ta.psndoc.view.TbmPsndocFormEditor();
		context.put("tbmPsndocFormEditor", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setTemplateContainer(getTemplateContainer());
		bean.setNodekey("6017psndoc");
		bean.setComponentValueManager(getValueAdaptor());
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

	public nc.ui.ta.psndoc.view.ValueAdapter getValueAdaptor() {
		if (context.get("valueAdaptor") != null)
			return (nc.ui.ta.psndoc.view.ValueAdapter) context.get("valueAdaptor");
		nc.ui.ta.psndoc.view.ValueAdapter bean = new nc.ui.ta.psndoc.view.ValueAdapter();
		context.put("valueAdaptor", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getTBNode_166f869());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_166f869() {
		if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#166f869") != null)
			return (nc.ui.uif2.tangramlayout.node.TBNode) context.get("nc.ui.uif2.tangramlayout.node.TBNode#166f869");
		nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
		context.put("nc.ui.uif2.tangramlayout.node.TBNode#166f869", bean);
		bean.setShowMode("CardLayout");
		bean.setTabs(getManagedList9());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList9() {
		List list = new ArrayList();
		list.add(getVSNode_bdb41c());
		list.add(getVSNode_f90f79());
		return list;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_bdb41c() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#bdb41c") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#bdb41c");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#bdb41c", bean);
		bean.setShowMode("NoDivider");
		bean.setUp(getCNode_100bdf());
		bean.setDown(getCNode_17b8689());
		bean.setDividerLocation(30f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_100bdf() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#100bdf") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#100bdf");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#100bdf", bean);
		bean.setComponent(getOrgPanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_17b8689() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#17b8689") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#17b8689");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#17b8689", bean);
		bean.setComponent(getTbmPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_f90f79() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#f90f79") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#f90f79");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#f90f79", bean);
		bean.setShowMode("NoDivider");
		bean.setUp(getCNode_7bcffc());
		bean.setDown(getCNode_93ea43());
		bean.setDividerLocation(26f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_7bcffc() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#7bcffc") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#7bcffc");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#7bcffc", bean);
		bean.setComponent(getEditorToolBarPanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_93ea43() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#93ea43") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#93ea43");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#93ea43", bean);
		bean.setComponent(getTbmPsndocFormEditor());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getEditorToolBarPanel() {
		if (context.get("editorToolBarPanel") != null)
			return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel) context.get("editorToolBarPanel");
		nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
		context.put("editorToolBarPanel", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setTitleAction(getEditorReturnAction());
		bean.setActions(getManagedList10());
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
		return list;
	}

	public nc.ui.hr.pf.action.PFFileManageAction getFileAction() {
		if (context.get("fileAction") != null)
			return (nc.ui.hr.pf.action.PFFileManageAction) context.get("fileAction");
		nc.ui.hr.pf.action.PFFileManageAction bean = new nc.ui.hr.pf.action.PFFileManageAction();
		context.put("fileAction", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setToolBarVisible(false);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.ShowMeUpAction getEditorReturnAction() {
		if (context.get("editorReturnAction") != null)
			return (nc.ui.uif2.actions.ShowMeUpAction) context.get("editorReturnAction");
		nc.ui.uif2.actions.ShowMeUpAction bean = new nc.ui.uif2.actions.ShowMeUpAction();
		context.put("editorReturnAction", bean);
		bean.setGoComponent(getTbmPsndocListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors() {
		if (context.get("toftpanelActionContributors") != null)
			return (nc.ui.uif2.actions.ActionContributors) context.get("toftpanelActionContributors");
		nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
		context.put("toftpanelActionContributors", bean);
		bean.setContributors(getManagedList11());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList11() {
		List list = new ArrayList();
		list.add(getListViewActions());
		list.add(getCardViewActions());
		return list;
	}

	public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getListViewActions() {
		if (context.get("listViewActions") != null)
			return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context.get("listViewActions");
		nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(
				getTbmPsndocListView());
		context.put("listViewActions", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setActions(getManagedList12());
		bean.setEditActions(getManagedList13());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList12() {
		List list = new ArrayList();
		list.add(getAddActionGroup());
		list.add(getEditActionGroup());
		list.add(getDeleteAction());
		list.add(getSeparatorAction());
		list.add(getQueryAction());
		list.add(getRefreshAction());
		list.add(getFilterAction());
		list.add(getSeparatorAction());
		list.add(getSetTempCardAction());
		list.add(getSeparatorAction());
		list.add(getExOrImpActionGroup());
		list.add(getPrintActiongroup());
		return list;
	}

	private List getManagedList13() {
		List list = new ArrayList();
		list.add(getSaveAction());
		list.add(getSeparatorAction());
		list.add(getCancelAction());
		return list;
	}

	public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getCardViewActions() {
		if (context.get("cardViewActions") != null)
			return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer) context.get("cardViewActions");
		nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(
				getTbmPsndocFormEditor());
		context.put("cardViewActions", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setActions(getManagedList14());
		bean.setEditActions(getManagedList15());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList14() {
		List list = new ArrayList();
		list.add(getAddAction());
		list.add(getEditAction());
		list.add(getDeleteAction());
		list.add(getSeparatorAction());
		list.add(getRefreshAction());
		list.add(getSeparatorAction());
		list.add(getCardPrintActiongroup());
		return list;
	}

	private List getManagedList15() {
		List list = new ArrayList();
		list.add(getSaveAction());
		list.add(getSeparatorAction());
		list.add(getCancelAction());
		return list;
	}

	public nc.ui.ta.psndoc.view.TbmPsndocOrgPanel getOrgPanel() {
		if (context.get("orgPanel") != null)
			return (nc.ui.ta.psndoc.view.TbmPsndocOrgPanel) context.get("orgPanel");
		nc.ui.ta.psndoc.view.TbmPsndocOrgPanel bean = new nc.ui.ta.psndoc.view.TbmPsndocOrgPanel();
		context.put("orgPanel", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setDataManager(getModelDataManager());
		bean.setPaginationModel(getPaginationModel());
		bean.setPk_orgtype("HRORGTYPE00000000000");
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.mediator.HyperLinkClickMediator getMouseClickShowPanelMediator() {
		if (context.get("mouseClickShowPanelMediator") != null)
			return (nc.ui.hr.uif2.mediator.HyperLinkClickMediator) context.get("mouseClickShowPanelMediator");
		nc.ui.hr.uif2.mediator.HyperLinkClickMediator bean = new nc.ui.hr.uif2.mediator.HyperLinkClickMediator();
		context.put("mouseClickShowPanelMediator", bean);
		bean.setModel(getTbmPsndocModel());
		bean.setShowUpComponent(getTbmPsndocFormEditor());
		bean.setHyperLinkColumn("pk_psnjob");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}
