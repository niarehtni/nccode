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
		bean.setQueryDelegator(getHrQueryDelegator_129363c());
		bean.setValidator(getManagedList3());
		bean.setMainComponent(getMainpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.hr.uif2.HrQueryDelegator getHrQueryDelegator_129363c() {
		if (context.get("nc.ui.hr.uif2.HrQueryDelegator#129363c") != null)
			return (nc.ui.hr.uif2.HrQueryDelegator) context.get("nc.ui.hr.uif2.HrQueryDelegator#129363c");
		nc.ui.hr.uif2.HrQueryDelegator bean = new nc.ui.hr.uif2.HrQueryDelegator();
		context.put("nc.ui.hr.uif2.HrQueryDelegator#129363c", bean);
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
		bean.setName(getI18nFB_a7bf96());
		bean.setActions(getManagedList8());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_a7bf96() {
		if (context.get("nc.ui.uif2.I18nFB#a7bf96") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#a7bf96");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#a7bf96", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("打印");
		bean.setResId("X60130002");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#a7bf96", product);
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
		bean.setTitle(getI18nFB_1133517());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1133517() {
		if (context.get("nc.ui.uif2.I18nFB#1133517") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1133517");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1133517", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("定??信息列表");
		bean.setResId("X60130015");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1133517", product);
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
		bean.setTitle(getI18nFB_29126());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_29126() {
		if (context.get("nc.ui.uif2.I18nFB#29126") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#29126");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#29126", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("定??信息列表");
		bean.setResId("X60130015");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#29126", product);
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
		bean.setTitle(getI18nFB_876114());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_876114() {
		if (context.get("nc.ui.uif2.I18nFB#876114") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#876114");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#876114", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("定??信息列表");
		bean.setResId("X60130015");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#876114", product);
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
		bean.setName(getI18nFB_1f66958());
		bean.setActions(getManagedList9());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1f66958() {
		if (context.get("nc.ui.uif2.I18nFB#1f66958") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1f66958");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1f66958", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("?入?出");
		bean.setResId("X60130040");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1f66958", product);
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
		bean.setTangramLayoutRoot(getVSNode_1578177());
		bean.setActions(getManagedList11());
		bean.setEditActions(getManagedList12());
		bean.setModel(getManageAppModel());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1578177() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#1578177") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#1578177");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#1578177", bean);
		bean.setUp(getCNode_7e1376());
		bean.setDown(getVSNode_1957589());
		bean.setDividerLocation(30f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_7e1376() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#7e1376") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#7e1376");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#7e1376", bean);
		bean.setComponent(getOrgpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1957589() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#1957589") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#1957589");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#1957589", bean);
		bean.setUp(getCNode_12fe3ba());
		bean.setDown(getTBNode_1749f87());
		bean.setDividerLocation(0.5f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_12fe3ba() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#12fe3ba") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#12fe3ba");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#12fe3ba", bean);
		bean.setComponent(getMainpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_1749f87() {
		if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#1749f87") != null)
			return (nc.ui.uif2.tangramlayout.node.TBNode) context.get("nc.ui.uif2.tangramlayout.node.TBNode#1749f87");
		nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
		context.put("nc.ui.uif2.tangramlayout.node.TBNode#1749f87", bean);
		bean.setTabs(getManagedList10());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList10() {
		List list = new ArrayList();
		list.add(getCNode_1517f2d());
		return list;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1517f2d() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1517f2d") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1517f2d");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1517f2d", bean);
		bean.setName(getI18nFB_10fc8f1());
		bean.setComponent(getBodypanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_10fc8f1() {
		if (context.get("nc.ui.uif2.I18nFB#10fc8f1") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#10fc8f1");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#10fc8f1", bean);
		bean.setResDir("xmlcode");
		bean.setDefaultValue("定??信息列表");
		bean.setResId("X60130015");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#10fc8f1", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList11() {
		List list = new ArrayList();
		list.add(getQueryAction());
		list.add(getRefreshAction());
		list.add(getNullaction());
		list.add(getAdjustPsndocwadocAction());
		list.add(getNullaction());
		list.add(getDataimportGroupAction());
		list.add(getPrintGroupAction());
		list.add(getNullaction());
		list.add(getGenerateNHISettingAction());
		list.add(getGenerateGroupInsSettingAction());
		list.add(getNullaction());
		return list;
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
		return list;
	}

	public nc.funcnode.ui.action.MenuAction getLabourActionGroup() {
		if (context.get("labourActionGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context.get("labourActionGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("labourActionGroup", bean);
		bean.setCode("labour");
		bean.setName(getI18nFB_1f4d766());
		bean.setActions(getManagedList13());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1f4d766() {
		if (context.get("nc.ui.uif2.I18nFB#1f4d766") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1f4d766");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1f4d766", bean);
		bean.setResDir("6017labour");
		bean.setDefaultValue("?健保批量加退");
		bean.setResId("labour20180922-0000");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1f4d766", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList13() {
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
		bean.setNcActionStatusJudge(getEnableJudge());
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
		bean.setNcActionStatusJudge(getEnableJudge());
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
		bean.setName(getI18nFB_1699cce());
		bean.setActions(getManagedList14());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1699cce() {
		if (context.get("nc.ui.uif2.I18nFB#1699cce") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1699cce");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1699cce", bean);
		bean.setResDir("6017labour");
		bean.setDefaultValue("?保批量加退");
		bean.setResId("labour20180922-0001");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1699cce", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList14() {
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
		bean.setNcActionStatusJudge(getEnableJudge());
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
		bean.setNcActionStatusJudge(getEnableJudge());
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

}
