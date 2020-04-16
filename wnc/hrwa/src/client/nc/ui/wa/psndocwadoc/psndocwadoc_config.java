package nc.ui.wa.psndocwadoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class psndocwadoc_config extends AbstractJavaBeanDefinition {
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

	public nc.ui.wa.psndocwadoc.model.PsndocwadocModelService getManageModelService() {
		if (context.get("ManageModelService") != null)
			return (nc.ui.wa.psndocwadoc.model.PsndocwadocModelService) context.get("ManageModelService");
		nc.ui.wa.psndocwadoc.model.PsndocwadocModelService bean = new nc.ui.wa.psndocwadoc.model.PsndocwadocModelService();
		context.put("ManageModelService", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel getManageAppModel() {
		if (context.get("ManageAppModel") != null)
			return (nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel) context.get("ManageAppModel");
		nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel bean = new nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel();
		context.put("ManageAppModel", bean);
		bean.setService(getManageModelService());
		bean.setContext(getContext());
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
		list.add("60130713_01");
		return list;
	}

	public nc.ui.wa.psndocwadoc.model.PsndocwadocModelDataManager getModelDataManager() {
		if (context.get("modelDataManager") != null)
			return (nc.ui.wa.psndocwadoc.model.PsndocwadocModelDataManager) context.get("modelDataManager");
		nc.ui.wa.psndocwadoc.model.PsndocwadocModelDataManager bean = new nc.ui.wa.psndocwadoc.model.PsndocwadocModelDataManager();
		context.put("modelDataManager", bean);
		bean.setService(getManageModelService());
		bean.setContext(getContext());
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.view.PsnWadocSubPane getBodypanel() {
		if (context.get("bodypanel") != null)
			return (nc.ui.wa.psndocwadoc.view.PsnWadocSubPane) context.get("bodypanel");
		nc.ui.wa.psndocwadoc.view.PsnWadocSubPane bean = new nc.ui.wa.psndocwadoc.view.PsnWadocSubPane();
		context.put("bodypanel", bean);
		bean.setModel(getManageAppModel());
		bean.setNodekey("psndocwadoc");
		bean.setTabActions(getManagedList1());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList1() {
		List list = new ArrayList();
		list.add(getAddLineAction());
		list.add(getInsertLineAction());
		list.add(getEditAction());
		list.add(getDelLineAction());
		list.add(getSaveAction());
		list.add(getCancelAction());
		return list;
	}

	public nc.ui.wa.psndocwadoc.view.PsnWadocMainPane getMainpanel() {
		if (context.get("mainpanel") != null)
			return (nc.ui.wa.psndocwadoc.view.PsnWadocMainPane) context.get("mainpanel");
		nc.ui.wa.psndocwadoc.view.PsnWadocMainPane bean = new nc.ui.wa.psndocwadoc.view.PsnWadocMainPane();
		context.put("mainpanel", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setBatchOperateAction(getBatchOprAction());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.SeparatorAction getNullaction() {
		if (context.get("nullaction") != null)
			return (nc.funcnode.ui.action.SeparatorAction) context.get("nullaction");
		nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
		context.put("nullaction", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.EditPsndocwadocAction getEditAction() {
		if (context.get("EditAction") != null)
			return (nc.ui.wa.psndocwadoc.action.EditPsndocwadocAction) context.get("EditAction");
		nc.ui.wa.psndocwadoc.action.EditPsndocwadocAction bean = new nc.ui.wa.psndocwadoc.action.EditPsndocwadocAction();
		context.put("EditAction", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setValidator(getManagedList2());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getValidateOrgNotNull());
		return list;
	}

	public nc.ui.wa.psndocwadoc.action.QueryPsndocwadocAction getQueryAction() {
		if (context.get("QueryAction") != null)
			return (nc.ui.wa.psndocwadoc.action.QueryPsndocwadocAction) context.get("QueryAction");
		nc.ui.wa.psndocwadoc.action.QueryPsndocwadocAction bean = new nc.ui.wa.psndocwadoc.action.QueryPsndocwadocAction();
		context.put("QueryAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		bean.setQueryDelegator(getHrQueryDelegator_1f1054d());
		bean.setValidator(getManagedList3());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.hr.uif2.HrQueryDelegator getHrQueryDelegator_1f1054d() {
		if (context.get("nc.ui.hr.uif2.HrQueryDelegator#1f1054d") != null)
			return (nc.ui.hr.uif2.HrQueryDelegator) context.get("nc.ui.hr.uif2.HrQueryDelegator#1f1054d");
		nc.ui.hr.uif2.HrQueryDelegator bean = new nc.ui.hr.uif2.HrQueryDelegator();
		context.put("nc.ui.hr.uif2.HrQueryDelegator#1f1054d", bean);
		bean.setModel(getManageAppModel());
		bean.setNodeKey("");
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList3() {
		List list = new ArrayList();
		list.add(getValidateOrgNotNull());
		return list;
	}

	public nc.ui.wa.psndocwadoc.action.RefreshPsndocwadocAction getRefreshAction() {
		if (context.get("RefreshAction") != null)
			return (nc.ui.wa.psndocwadoc.action.RefreshPsndocwadocAction) context.get("RefreshAction");
		nc.ui.wa.psndocwadoc.action.RefreshPsndocwadocAction bean = new nc.ui.wa.psndocwadoc.action.RefreshPsndocwadocAction();
		context.put("RefreshAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		bean.setValidator(getManagedList4());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList4() {
		List list = new ArrayList();
		list.add(getValidateOrgNotNull());
		return list;
	}

	public nc.ui.wa.psndocwadoc.action.SavePsndocwadocAction getSaveAction() {
		if (context.get("SaveAction") != null)
			return (nc.ui.wa.psndocwadoc.action.SavePsndocwadocAction) context.get("SaveAction");
		nc.ui.wa.psndocwadoc.action.SavePsndocwadocAction bean = new nc.ui.wa.psndocwadoc.action.SavePsndocwadocAction();
		context.put("SaveAction", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setValidator(getManagedList5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList5() {
		List list = new ArrayList();
		list.add(getSaveValueValidator());
		return list;
	}

	public nc.ui.hr.uif2.validator.BillNotNullValidateService getBillNotNullValidator() {
		if (context.get("billNotNullValidator") != null)
			return (nc.ui.hr.uif2.validator.BillNotNullValidateService) context.get("billNotNullValidator");
		nc.ui.hr.uif2.validator.BillNotNullValidateService bean = new nc.ui.hr.uif2.validator.BillNotNullValidateService(
				getBodypanel());
		context.put("billNotNullValidator", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.CancelPsndocwadocAction getCancelAction() {
		if (context.get("CancelAction") != null)
			return (nc.ui.wa.psndocwadoc.action.CancelPsndocwadocAction) context.get("CancelAction");
		nc.ui.wa.psndocwadoc.action.CancelPsndocwadocAction bean = new nc.ui.wa.psndocwadoc.action.CancelPsndocwadocAction();
		context.put("CancelAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.AddLinePsndocwadocAction getAddLineAction() {
		if (context.get("AddLineAction") != null)
			return (nc.ui.wa.psndocwadoc.action.AddLinePsndocwadocAction) context.get("AddLineAction");
		nc.ui.wa.psndocwadoc.action.AddLinePsndocwadocAction bean = new nc.ui.wa.psndocwadoc.action.AddLinePsndocwadocAction();
		context.put("AddLineAction", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setMainComponent(getMainpanel());
		bean.setValidator(getManagedList6());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList6() {
		List list = new ArrayList();
		list.add(getValidateOrgNotNull());
		return list;
	}

	public nc.ui.wa.psndocwadoc.action.DelLinePsndocwadocAction getDelLineAction() {
		if (context.get("DelLineAction") != null)
			return (nc.ui.wa.psndocwadoc.action.DelLinePsndocwadocAction) context.get("DelLineAction");
		nc.ui.wa.psndocwadoc.action.DelLinePsndocwadocAction bean = new nc.ui.wa.psndocwadoc.action.DelLinePsndocwadocAction();
		context.put("DelLineAction", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setMainComponent(getMainpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.InsertLinePsndocwadocAction getInsertLineAction() {
		if (context.get("InsertLineAction") != null)
			return (nc.ui.wa.psndocwadoc.action.InsertLinePsndocwadocAction) context.get("InsertLineAction");
		nc.ui.wa.psndocwadoc.action.InsertLinePsndocwadocAction bean = new nc.ui.wa.psndocwadoc.action.InsertLinePsndocwadocAction();
		context.put("InsertLineAction", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setMainComponent(getMainpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.AdjustPsndocwadocAction getAdjustPsndocwadocAction() {
		if (context.get("AdjustPsndocwadocAction") != null)
			return (nc.ui.wa.psndocwadoc.action.AdjustPsndocwadocAction) context.get("AdjustPsndocwadocAction");
		nc.ui.wa.psndocwadoc.action.AdjustPsndocwadocAction bean = new nc.ui.wa.psndocwadoc.action.AdjustPsndocwadocAction();
		context.put("AdjustPsndocwadocAction", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setMainComponent(getMainpanel());
		bean.setValidator(getManagedList7());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList7() {
		List list = new ArrayList();
		list.add(getValidateOrgNotNull());
		return list;
	}

	public nc.ui.wa.psndocwadoc.model.PsndocwadocMetaDataDataSource getDatasource() {
		if (context.get("datasource") != null)
			return (nc.ui.wa.psndocwadoc.model.PsndocwadocMetaDataDataSource) context.get("datasource");
		nc.ui.wa.psndocwadoc.model.PsndocwadocMetaDataDataSource bean = new nc.ui.wa.psndocwadoc.model.PsndocwadocMetaDataDataSource();
		context.put("datasource", bean);
		bean.setModel(getManageAppModel());
		bean.setSubPane(getBodypanel());
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
		bean.setName(getI18nFB_9f6128());
		bean.setActions(getManagedList8());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_9f6128() {
		if (context.get("nc.ui.uif2.I18nFB#9f6128") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#9f6128");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#9f6128", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("打印");
		bean.setResId("X60130002");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#9f6128", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList8() {
		List list = new ArrayList();
		list.add(getPrintAction());
		list.add(getPreviewAction());
		list.add(getOutputAction());
		list.add(getNullaction());
		list.add(getTemplateprintAction());
		list.add(getTemplatepreviewAction());
		return list;
	}

	public nc.ui.wa.psndocwadoc.action.HrWADutipleListOutputAction getOutputAction() {
		if (context.get("outputAction") != null)
			return (nc.ui.wa.psndocwadoc.action.HrWADutipleListOutputAction) context.get("outputAction");
		nc.ui.wa.psndocwadoc.action.HrWADutipleListOutputAction bean = new nc.ui.wa.psndocwadoc.action.HrWADutipleListOutputAction();
		context.put("outputAction", bean);
		bean.setModel(getManageAppModel());
		bean.setCardEditor(getBodypanel());
		bean.setMainPanel(getMainpanel());
		bean.setTitle(getI18nFB_1a395b9());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1a395b9() {
		if (context.get("nc.ui.uif2.I18nFB#1a395b9") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1a395b9");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1a395b9", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("定调资信息列表");
		bean.setResId("X60130015");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1a395b9", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public nc.ui.wa.psndocwadoc.action.HrWADutipleListDirectPrintAction getPreviewAction() {
		if (context.get("previewAction") != null)
			return (nc.ui.wa.psndocwadoc.action.HrWADutipleListDirectPrintAction) context.get("previewAction");
		nc.ui.wa.psndocwadoc.action.HrWADutipleListDirectPrintAction bean = new nc.ui.wa.psndocwadoc.action.HrWADutipleListDirectPrintAction();
		context.put("previewAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDirectPrint(false);
		bean.setCardEditor(getBodypanel());
		bean.setMainPanel(getMainpanel());
		bean.setTitle(getI18nFB_baa89a());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_baa89a() {
		if (context.get("nc.ui.uif2.I18nFB#baa89a") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#baa89a");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#baa89a", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("定调资信息列表");
		bean.setResId("X60130015");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#baa89a", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public nc.ui.wa.psndocwadoc.action.HrWADutipleListDirectPrintAction getPrintAction() {
		if (context.get("printAction") != null)
			return (nc.ui.wa.psndocwadoc.action.HrWADutipleListDirectPrintAction) context.get("printAction");
		nc.ui.wa.psndocwadoc.action.HrWADutipleListDirectPrintAction bean = new nc.ui.wa.psndocwadoc.action.HrWADutipleListDirectPrintAction();
		context.put("printAction", bean);
		bean.setModel(getManageAppModel());
		bean.setCardEditor(getBodypanel());
		bean.setMainPanel(getMainpanel());
		bean.setTitle(getI18nFB_9318e2());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_9318e2() {
		if (context.get("nc.ui.uif2.I18nFB#9318e2") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#9318e2");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#9318e2", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("定调资信息列表");
		bean.setResId("X60130015");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#9318e2", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public nc.ui.wa.pub.action.WaTemplatePreviewAction getTemplatepreviewAction() {
		if (context.get("templatepreviewAction") != null)
			return (nc.ui.wa.pub.action.WaTemplatePreviewAction) context.get("templatepreviewAction");
		nc.ui.wa.pub.action.WaTemplatePreviewAction bean = new nc.ui.wa.pub.action.WaTemplatePreviewAction();
		context.put("templatepreviewAction", bean);
		bean.setModel(getManageAppModel());
		bean.setNodeKey("adjmtcprint");
		bean.setDatasource(getDatasource());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.pub.action.WaTemplatePrintAction getTemplateprintAction() {
		if (context.get("templateprintAction") != null)
			return (nc.ui.wa.pub.action.WaTemplatePrintAction) context.get("templateprintAction");
		nc.ui.wa.pub.action.WaTemplatePrintAction bean = new nc.ui.wa.pub.action.WaTemplatePrintAction();
		context.put("templateprintAction", bean);
		bean.setModel(getManageAppModel());
		bean.setNodeKey("adjmtcprint");
		bean.setDatasource(getDatasource());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getDataimportGroupAction() {
		if (context.get("dataimportGroupAction") != null)
			return (nc.funcnode.ui.action.MenuAction) context.get("dataimportGroupAction");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("dataimportGroupAction", bean);
		bean.setCode("dataimport");
		bean.setName(getI18nFB_146dc4c());
		bean.setActions(getManagedList9());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_146dc4c() {
		if (context.get("nc.ui.uif2.I18nFB#146dc4c") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#146dc4c");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#146dc4c", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("?入?出");
		bean.setResId("X60130040");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#146dc4c", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList9() {
		List list = new ArrayList();
		list.add(getImportDataPsndocwadocAction());
		list.add(getExprotTempletAction());
		return list;
	}

	public nc.ui.wa.psndocwadoc.action.ImportDataPsndocwadocAction getImportDataPsndocwadocAction() {
		if (context.get("ImportDataPsndocwadocAction") != null)
			return (nc.ui.wa.psndocwadoc.action.ImportDataPsndocwadocAction) context.get("ImportDataPsndocwadocAction");
		nc.ui.wa.psndocwadoc.action.ImportDataPsndocwadocAction bean = new nc.ui.wa.psndocwadoc.action.ImportDataPsndocwadocAction();
		context.put("ImportDataPsndocwadocAction", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setMainComponent(getMainpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.PsndocwadocExportTempletAction getExprotTempletAction() {
		if (context.get("exprotTempletAction") != null)
			return (nc.ui.wa.psndocwadoc.action.PsndocwadocExportTempletAction) context.get("exprotTempletAction");
		nc.ui.wa.psndocwadoc.action.PsndocwadocExportTempletAction bean = new nc.ui.wa.psndocwadoc.action.PsndocwadocExportTempletAction();
		context.put("exprotTempletAction", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setMainComponent(getMainpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.WaDocInsGroupAction getWadocInsGroupAction() {
		if (context.get("wadocInsGroupAction") != null)
			return (nc.ui.wa.psndocwadoc.action.WaDocInsGroupAction) context.get("wadocInsGroupAction");
		nc.ui.wa.psndocwadoc.action.WaDocInsGroupAction bean = new nc.ui.wa.psndocwadoc.action.WaDocInsGroupAction();
		context.put("wadocInsGroupAction", bean);
		bean.setActions(getManagedList10());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList10() {
		List list = new ArrayList();
		list.add(getGenerateNHISettingAction());
		list.add(getGenerateGroupInsSettingAction());
		list.add(getNewGroupInsSettingAction());
		list.add(getAdjustBOLIAction());
		return list;
	}

	public nc.ui.wa.psndocwadoc.action.AdjustBOLIAction getAdjustBOLIAction() {
		if (context.get("AdjustBOLIAction") != null)
			return (nc.ui.wa.psndocwadoc.action.AdjustBOLIAction) context.get("AdjustBOLIAction");
		nc.ui.wa.psndocwadoc.action.AdjustBOLIAction bean = new nc.ui.wa.psndocwadoc.action.AdjustBOLIAction();
		context.put("AdjustBOLIAction", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setMainComponent(getMainpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.GenerateNHISettingAction getGenerateNHISettingAction() {
		if (context.get("GenerateNHISettingAction") != null)
			return (nc.ui.wa.psndocwadoc.action.GenerateNHISettingAction) context.get("GenerateNHISettingAction");
		nc.ui.wa.psndocwadoc.action.GenerateNHISettingAction bean = new nc.ui.wa.psndocwadoc.action.GenerateNHISettingAction();
		context.put("GenerateNHISettingAction", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setMainComponent(getMainpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.GenerateGroupInsSettingAction getGenerateGroupInsSettingAction() {
		if (context.get("GenerateGroupInsSettingAction") != null)
			return (nc.ui.wa.psndocwadoc.action.GenerateGroupInsSettingAction) context
					.get("GenerateGroupInsSettingAction");
		nc.ui.wa.psndocwadoc.action.GenerateGroupInsSettingAction bean = new nc.ui.wa.psndocwadoc.action.GenerateGroupInsSettingAction();
		context.put("GenerateGroupInsSettingAction", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setMainComponent(getMainpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.NewGroupInsSettingAction getNewGroupInsSettingAction() {
		if (context.get("NewGroupInsSettingAction") != null)
			return (nc.ui.wa.psndocwadoc.action.NewGroupInsSettingAction) context.get("NewGroupInsSettingAction");
		nc.ui.wa.psndocwadoc.action.NewGroupInsSettingAction bean = new nc.ui.wa.psndocwadoc.action.NewGroupInsSettingAction();
		context.put("NewGroupInsSettingAction", bean);
		bean.setModel(getManageAppModel());
		bean.setComponent(getBodypanel());
		bean.setMainComponent(getMainpanel());
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

	public nc.ui.hr.uif2.validator.OrgNotNullValidator getValidateOrgNotNull() {
		if (context.get("ValidateOrgNotNull") != null)
			return (nc.ui.hr.uif2.validator.OrgNotNullValidator) context.get("ValidateOrgNotNull");
		nc.ui.hr.uif2.validator.OrgNotNullValidator bean = new nc.ui.hr.uif2.validator.OrgNotNullValidator();
		context.put("ValidateOrgNotNull", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.validator.WaPsndocdadocSaveValueValidator getSaveValueValidator() {
		if (context.get("SaveValueValidator") != null)
			return (nc.ui.wa.psndocwadoc.validator.WaPsndocdadocSaveValueValidator) context.get("SaveValueValidator");
		nc.ui.wa.psndocwadoc.validator.WaPsndocdadocSaveValueValidator bean = new nc.ui.wa.psndocwadoc.validator.WaPsndocdadocSaveValueValidator();
		context.put("SaveValueValidator", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getVSNode_132cbb0());
		bean.setActions(getManagedList12());
		bean.setEditActions(getManagedList13());
		bean.setModel(getManageAppModel());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_132cbb0() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#132cbb0") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#132cbb0");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#132cbb0", bean);
		bean.setUp(getCNode_1292a3d());
		bean.setDown(getVSNode_189263());
		bean.setDividerLocation(30f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1292a3d() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1292a3d") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1292a3d");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1292a3d", bean);
		bean.setComponent(getOrgpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_189263() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#189263") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#189263");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#189263", bean);
		bean.setUp(getCNode_194260f());
		bean.setDown(getTBNode_3ea268());
		bean.setDividerLocation(0.5f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_194260f() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#194260f") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#194260f");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#194260f", bean);
		bean.setComponent(getMainpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_3ea268() {
		if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#3ea268") != null)
			return (nc.ui.uif2.tangramlayout.node.TBNode) context.get("nc.ui.uif2.tangramlayout.node.TBNode#3ea268");
		nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
		context.put("nc.ui.uif2.tangramlayout.node.TBNode#3ea268", bean);
		bean.setTabs(getManagedList11());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList11() {
		List list = new ArrayList();
		list.add(getCNode_bf206());
		return list;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_bf206() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#bf206") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#bf206");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#bf206", bean);
		bean.setName(getI18nFB_14eb402());
		bean.setComponent(getBodypanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_14eb402() {
		if (context.get("nc.ui.uif2.I18nFB#14eb402") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#14eb402");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#14eb402", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("定调资信息列表");
		bean.setResId("X60130015");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#14eb402", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList12() {
		List list = new ArrayList();
		list.add(getQueryAction());
		list.add(getRefreshAction());
		list.add(getNullaction());
		list.add(getAdjustPsndocwadocAction());
		list.add(getNullaction());
		list.add(getDataimportGroupAction());
		list.add(getPrintGroupAction());
		list.add(getNullaction());
		list.add(getWadocInsGroupAction());
		list.add(getBatchOprAction());
		list.add(getNullaction());
		list.add(getGrpInsExitGroupAction());
		return list;
	}

	private List getManagedList13() {
		List list = new ArrayList();
		list.add(getQueryAction());
		list.add(getRefreshAction());
		list.add(getNullaction());
		list.add(getAdjustPsndocwadocAction());
		list.add(getNullaction());
		list.add(getDataimportGroupAction());
		list.add(getPrintGroupAction());
		list.add(getNullaction());
		list.add(getWadocInsGroupAction());
		list.add(getBatchOprAction());
		list.add(getNullaction());
		list.add(getGrpInsExitGroupAction());
		return list;
	}

	public nc.ui.wa.psndocwadoc.action.BatchOperateAction getBatchOprAction() {
		if (context.get("batchOprAction") != null)
			return (nc.ui.wa.psndocwadoc.action.BatchOperateAction) context.get("batchOprAction");
		nc.ui.wa.psndocwadoc.action.BatchOperateAction bean = new nc.ui.wa.psndocwadoc.action.BatchOperateAction();
		context.put("batchOprAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataIOGroupAction(getDataimportGroupAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getGrpInsExitGroupAction() {
		if (context.get("grpInsExitGroupAction") != null)
			return (nc.funcnode.ui.action.MenuAction) context.get("grpInsExitGroupAction");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("grpInsExitGroupAction", bean);
		bean.setCode("grpinsex");
		bean.setName("團保批量退保");
		bean.setActions(getManagedList14());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList14() {
		List list = new ArrayList();
		list.add(getExportGrpInsExTmpAction());
		list.add(getImportGrpInsExAction());
		return list;
	}

	public nc.ui.wa.psndocwadoc.action.ExportGrpInsExTmpAction getExportGrpInsExTmpAction() {
		if (context.get("exportGrpInsExTmpAction") != null)
			return (nc.ui.wa.psndocwadoc.action.ExportGrpInsExTmpAction) context.get("exportGrpInsExTmpAction");
		nc.ui.wa.psndocwadoc.action.ExportGrpInsExTmpAction bean = new nc.ui.wa.psndocwadoc.action.ExportGrpInsExTmpAction();
		context.put("exportGrpInsExTmpAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.ImportGrpInsExAction getImportGrpInsExAction() {
		if (context.get("importGrpInsExAction") != null)
			return (nc.ui.wa.psndocwadoc.action.ImportGrpInsExAction) context.get("importGrpInsExAction");
		nc.ui.wa.psndocwadoc.action.ImportGrpInsExAction bean = new nc.ui.wa.psndocwadoc.action.ImportGrpInsExAction();
		context.put("importGrpInsExAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getLabourActionGroup() {
		if (context.get("labourActionGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context.get("labourActionGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("labourActionGroup", bean);
		bean.setCode("labour");
		bean.setName(getI18nFB_1dab661());
		bean.setActions(getManagedList15());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1dab661() {
		if (context.get("nc.ui.uif2.I18nFB#1dab661") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1dab661");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1dab661", bean);
		bean.setResDir("6017labour");
		bean.setDefaultValue("?健保批量加退");
		bean.setResId("labour20180922-0000");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1dab661", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList15() {
		List list = new ArrayList();
		list.add(getLabourJoinAction());
		list.add(getQuitJoinAction());
		return list;
	}

	public nc.ui.wa.psndocwadoc.action.LabourJoinAction getLabourJoinAction() {
		if (context.get("LabourJoinAction") != null)
			return (nc.ui.wa.psndocwadoc.action.LabourJoinAction) context.get("LabourJoinAction");
		nc.ui.wa.psndocwadoc.action.LabourJoinAction bean = new nc.ui.wa.psndocwadoc.action.LabourJoinAction();
		context.put("LabourJoinAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.QuitJoinAction getQuitJoinAction() {
		if (context.get("QuitJoinAction") != null)
			return (nc.ui.wa.psndocwadoc.action.QuitJoinAction) context.get("QuitJoinAction");
		nc.ui.wa.psndocwadoc.action.QuitJoinAction bean = new nc.ui.wa.psndocwadoc.action.QuitJoinAction();
		context.put("QuitJoinAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getTuanActionGroup() {
		if (context.get("tuanActionGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context.get("tuanActionGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("tuanActionGroup", bean);
		bean.setCode("tuan");
		bean.setName(getI18nFB_1fbd5b3());
		bean.setActions(getManagedList16());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1fbd5b3() {
		if (context.get("nc.ui.uif2.I18nFB#1fbd5b3") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1fbd5b3");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1fbd5b3", bean);
		bean.setResDir("6017labour");
		bean.setDefaultValue("?保批量加退");
		bean.setResId("labour20180922-0001");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1fbd5b3", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList16() {
		List list = new ArrayList();
		list.add(getAddJoinAction());
		list.add(getOutJoinAction());
		return list;
	}

	public nc.ui.wa.psndocwadoc.action.AddJoinAction getAddJoinAction() {
		if (context.get("AddJoinAction") != null)
			return (nc.ui.wa.psndocwadoc.action.AddJoinAction) context.get("AddJoinAction");
		nc.ui.wa.psndocwadoc.action.AddJoinAction bean = new nc.ui.wa.psndocwadoc.action.AddJoinAction();
		context.put("AddJoinAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.action.OutJoinAction getOutJoinAction() {
		if (context.get("OutJoinAction") != null)
			return (nc.ui.wa.psndocwadoc.action.OutJoinAction) context.get("OutJoinAction");
		nc.ui.wa.psndocwadoc.action.OutJoinAction bean = new nc.ui.wa.psndocwadoc.action.OutJoinAction();
		context.put("OutJoinAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.wa.psndocwadoc.view.PsnWadocOrgPanel getOrgpanel() {
		if (context.get("orgpanel") != null)
			return (nc.ui.wa.psndocwadoc.view.PsnWadocOrgPanel) context.get("orgpanel");
		nc.ui.wa.psndocwadoc.view.PsnWadocOrgPanel bean = new nc.ui.wa.psndocwadoc.view.PsnWadocOrgPanel();
		context.put("orgpanel", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getModelDataManager());
		bean.setPk_orgtype("HRORGTYPE00000000000");
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}
