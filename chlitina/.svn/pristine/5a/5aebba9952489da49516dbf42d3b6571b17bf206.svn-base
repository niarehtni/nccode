package nc.ui.bd.workcalendar.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class workcalendar_org extends AbstractJavaBeanDefinition {
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

  public nc.ui.bd.workcalendar.model.WorkCalendarAppService getAppModelService() {
    if (context.get("appModelService") != null)
      return (nc.ui.bd.workcalendar.model.WorkCalendarAppService) context.get("appModelService");
    nc.ui.bd.workcalendar.model.WorkCalendarAppService bean =
        new nc.ui.bd.workcalendar.model.WorkCalendarAppService();
    context.put("appModelService", bean);
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
    list.add("workcalendar");
    list.add("adddetail");
    return list;
  }

  public nc.vo.bd.meta.BDObjectAdpaterFactory getObjectadapterfactory() {
    if (context.get("objectadapterfactory") != null)
      return (nc.vo.bd.meta.BDObjectAdpaterFactory) context.get("objectadapterfactory");
    nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
    context.put("objectadapterfactory", bean);
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.vo.bd.meta.BDObjectTreeCreateStrategy getTreestrategy() {
    if (context.get("treestrategy") != null)
      return (nc.vo.bd.meta.BDObjectTreeCreateStrategy) context.get("treestrategy");
    nc.vo.bd.meta.BDObjectTreeCreateStrategy bean = new nc.vo.bd.meta.BDObjectTreeCreateStrategy();
    context.put("treestrategy", bean);
    bean.setFactory(getObjectadapterfactory());
    bean.setClassName("nc.vo.bd.workcalendar.WorkCalendarVO");
    bean.setRootName(getI18nFB_1614001());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private java.lang.String getI18nFB_1614001() {
    if (context.get("nc.ui.uif2.I18nFB#1614001") != null)
      return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1614001");
    nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1614001", bean);
    bean.setResDir("10140wcb");
    bean.setDefaultValue("工作日历");
    bean.setResId("110140wcb0003");
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    try {
      Object product = bean.getObject();
      context.put("nc.ui.uif2.I18nFB#1614001", product);
      return (java.lang.String) product;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public nc.ui.bd.workcalendar.model.WorkCalendarTreeModel getAppmodel() {
    if (context.get("appmodel") != null)
      return (nc.ui.bd.workcalendar.model.WorkCalendarTreeModel) context.get("appmodel");
    nc.ui.bd.workcalendar.model.WorkCalendarTreeModel bean =
        new nc.ui.bd.workcalendar.model.WorkCalendarTreeModel();
    context.put("appmodel", bean);
    bean.setContext(getContext());
    bean.setTreeCreateStrategy(getTreestrategy());
    bean.setBusinessObjectAdapterFactory(getObjectadapterfactory());
    bean.setService(getAppModelService());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.uif2.model.BillManageModel getManageAppModel() {
    if (context.get("manageAppModel") != null)
      return (nc.ui.uif2.model.BillManageModel) context.get("manageAppModel");
    nc.ui.uif2.model.BillManageModel bean = new nc.ui.uif2.model.BillManageModel();
    context.put("manageAppModel", bean);
    bean.setBusinessObjectAdapterFactory(getObjectadapterfactory());
    bean.setContext(getContext());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.model.WorkCalendarDataManager getModelDataManager() {
    if (context.get("modelDataManager") != null)
      return (nc.ui.bd.workcalendar.model.WorkCalendarDataManager) context.get("modelDataManager");
    nc.ui.bd.workcalendar.model.WorkCalendarDataManager bean =
        new nc.ui.bd.workcalendar.model.WorkCalendarDataManager();
    context.put("modelDataManager", bean);
    bean.setModel(getAppmodel());
    bean.setService(getAppModelService());
    bean.setContext(getContext());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.uif2.components.TreePanel getTreePanel() {
    if (context.get("treePanel") != null)
      return (nc.ui.uif2.components.TreePanel) context.get("treePanel");
    nc.ui.uif2.components.TreePanel bean = new nc.ui.uif2.components.TreePanel();
    context.put("treePanel", bean);
    bean.setModel(getAppmodel());
    bean.setTreeCellRenderer(getBDTreeCellRenderer_1b13ddb());
    bean.initUI();
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private nc.ui.bd.pub.BDTreeCellRenderer getBDTreeCellRenderer_1b13ddb() {
    if (context.get("nc.ui.bd.pub.BDTreeCellRenderer#1b13ddb") != null)
      return (nc.ui.bd.pub.BDTreeCellRenderer) context
          .get("nc.ui.bd.pub.BDTreeCellRenderer#1b13ddb");
    nc.ui.bd.pub.BDTreeCellRenderer bean = new nc.ui.bd.pub.BDTreeCellRenderer();
    context.put("nc.ui.bd.pub.BDTreeCellRenderer#1b13ddb", bean);
    bean.setContext(getContext());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.view.WorkCalendarEditor getWorkCalendarCardEditor() {
    if (context.get("workCalendarCardEditor") != null)
      return (nc.ui.bd.workcalendar.view.WorkCalendarEditor) context.get("workCalendarCardEditor");
    nc.ui.bd.workcalendar.view.WorkCalendarEditor bean =
        new nc.ui.bd.workcalendar.view.WorkCalendarEditor();
    context.put("workCalendarCardEditor", bean);
    bean.setModel(getAppmodel());
    bean.setNodekey("workcalendar");
    bean.setTemplateContainer(getTemplateContainer());
    bean.initUI();
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.view.WorkCalendAddDetailEditor getDlgEditor() {
    if (context.get("dlgEditor") != null)
      return (nc.ui.bd.workcalendar.view.WorkCalendAddDetailEditor) context.get("dlgEditor");
    nc.ui.bd.workcalendar.view.WorkCalendAddDetailEditor bean =
        new nc.ui.bd.workcalendar.view.WorkCalendAddDetailEditor();
    context.put("dlgEditor", bean);
    bean.setModel(getManageAppModel());
    bean.setTreeModel(getAppmodel());
    bean.setNodekey("adddetail");
    bean.setTemplateContainer(getTemplateContainer());
    bean.initUI();
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.view.WorkCalendarAddDeailDlgMediator getDetailAddMediator() {
    if (context.get("DetailAddMediator") != null)
      return (nc.ui.bd.workcalendar.view.WorkCalendarAddDeailDlgMediator) context
          .get("DetailAddMediator");
    nc.ui.bd.workcalendar.view.WorkCalendarAddDeailDlgMediator bean =
        new nc.ui.bd.workcalendar.view.WorkCalendarAddDeailDlgMediator();
    context.put("DetailAddMediator", bean);
    bean.setName(getI18nFB_1541795());
    bean.setEditor(getDlgEditor());
    bean.setModel(getManageAppModel());
    bean.setSaveAction(getSaveDetailAction());
    bean.setCancelAction(getAddDetatilCancelAction());
    bean.setWidth(500);
    bean.setHeight(260);
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private java.lang.String getI18nFB_1541795() {
    if (context.get("nc.ui.uif2.I18nFB#1541795") != null)
      return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1541795");
    nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1541795", bean);
    bean.setResDir("10140wcb");
    bean.setDefaultValue("生成工作日历明细");
    bean.setResId("110140wcb0006");
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    try {
      Object product = bean.getObject();
      context.put("nc.ui.uif2.I18nFB#1541795", product);
      return (java.lang.String) product;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public nc.ui.bd.workcalendar.model.WorkCalendarAppService getAppCalendarService() {
    if (context.get("appCalendarService") != null)
      return (nc.ui.bd.workcalendar.model.WorkCalendarAppService) context.get("appCalendarService");
    nc.ui.bd.workcalendar.model.WorkCalendarAppService bean =
        new nc.ui.bd.workcalendar.model.WorkCalendarAppService();
    context.put("appCalendarService", bean);
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.pub.WorkCalendarPanel getWorkCalendarPanel() {
    if (context.get("workCalendarPanel") != null)
      return (nc.ui.bd.workcalendar.pub.WorkCalendarPanel) context.get("workCalendarPanel");
    nc.ui.bd.workcalendar.pub.WorkCalendarPanel bean =
        new nc.ui.bd.workcalendar.pub.WorkCalendarPanel();
    context.put("workCalendarPanel", bean);
    bean.setModel(getAppmodel());
    bean.setWorkingDayAction(getWorkingDayAction());
//    bean.setWeekendDayAction(getWeekendDayAction());
    //例假日
    bean.setLiHolidayDayAction(getLiHolidayDayAction());
    //休息日
    bean.setRestDayAction(getRestDayAction());
    bean.setHolidayDayAction(getHolidayDayAction());
    bean.setWorkingTimeAction(getWorkingTimeAction());
    bean.setCancelSelectAction(getCancelSelectAction());
    bean.initialize();
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarWorkingDayAction getWorkingDayAction() {
    if (context.get("workingDayAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarWorkingDayAction) context
          .get("workingDayAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarWorkingDayAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarWorkingDayAction();
    context.put("workingDayAction", bean);
    bean.setModel(getAppmodel());
    bean.setExceptionHandler(getExceptionHandler());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarHolidayDayAction getHolidayDayAction() {
    if (context.get("holidayDayAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarHolidayDayAction) context
          .get("holidayDayAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarHolidayDayAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarHolidayDayAction();
    context.put("holidayDayAction", bean);
    bean.setModel(getAppmodel());
    bean.setExceptionHandler(getExceptionHandler());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }
  

  //例假日
  public nc.ui.bd.workcalendar.actions.WorkCalendarLiHolidayDayAction getLiHolidayDayAction() {
	    if (context.get("LiholidayDayAction") != null)
	      return (nc.ui.bd.workcalendar.actions.WorkCalendarLiHolidayDayAction) context
	          .get("LiholidayDayAction");
	    nc.ui.bd.workcalendar.actions.WorkCalendarLiHolidayDayAction bean =
	        new nc.ui.bd.workcalendar.actions.WorkCalendarLiHolidayDayAction();
	    context.put("LiholidayDayAction", bean);
	    bean.setModel(getAppmodel());
	    bean.setExceptionHandler(getExceptionHandler());
	    setBeanFacotryIfBeanFacatoryAware(bean);
	    invokeInitializingBean(bean);
	    return bean;
	  }
  
  //休息日
  public nc.ui.bd.workcalendar.actions.WorkCalendarRestDayAction getRestDayAction() {
	    if (context.get("restDayAction") != null)
	      return (nc.ui.bd.workcalendar.actions.WorkCalendarRestDayAction) context
	          .get("restDayAction");
	    nc.ui.bd.workcalendar.actions.WorkCalendarRestDayAction bean =
	        new nc.ui.bd.workcalendar.actions.WorkCalendarRestDayAction();
	    context.put("restDayAction", bean);
	    bean.setModel(getAppmodel());
	    bean.setExceptionHandler(getExceptionHandler());
	    setBeanFacotryIfBeanFacatoryAware(bean);
	    invokeInitializingBean(bean);
	    return bean;
	  }
  
/*
  public nc.ui.bd.workcalendar.actions.WorkCalendarWeekendDayAction getWeekendDayAction() {
    if (context.get("weekendDayAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarWeekendDayAction) context
          .get("weekendDayAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarWeekendDayAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarWeekendDayAction();
    context.put("weekendDayAction", bean);
    bean.setModel(getAppmodel());
    bean.setExceptionHandler(getExceptionHandler());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }
*/
  public nc.ui.bd.workcalendar.actions.WorkCalendarWorkingTimeAction getWorkingTimeAction() {
    if (context.get("workingTimeAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarWorkingTimeAction) context
          .get("workingTimeAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarWorkingTimeAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarWorkingTimeAction();
    context.put("workingTimeAction", bean);
    bean.setModel(getAppmodel());
    bean.setExceptionHandler(getExceptionHandler());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarCancelSelectAction getCancelSelectAction() {
    if (context.get("cancelSelectAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarCancelSelectAction) context
          .get("cancelSelectAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarCancelSelectAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarCancelSelectAction();
    context.put("cancelSelectAction", bean);
    bean.setExceptionHandler(getExceptionHandler());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarDefaultAction getDefaultCalendarAction() {
    if (context.get("defaultCalendarAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarDefaultAction) context
          .get("defaultCalendarAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarDefaultAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarDefaultAction();
    context.put("defaultCalendarAction", bean);
    bean.setModel(getAppmodel());
    bean.setEditor(getWorkCalendarCardEditor());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarAddDetailAction getAddDetailAction() {
    if (context.get("addDetailAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarAddDetailAction) context
          .get("addDetailAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarAddDetailAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarAddDetailAction();
    context.put("addDetailAction", bean);
    bean.setModel(getAppmodel());
    bean.setManageModel(getManageAppModel());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.uif2.actions.CancelAction getAddDetatilCancelAction() {
    if (context.get("addDetatilCancelAction") != null)
      return (nc.ui.uif2.actions.CancelAction) context.get("addDetatilCancelAction");
    nc.ui.uif2.actions.CancelAction bean = new nc.ui.uif2.actions.CancelAction();
    context.put("addDetatilCancelAction", bean);
    bean.setModel(getManageAppModel());
    bean.setEditor(getDlgEditor());
    bean.setExceptionHandler(getExceptionHandler());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarDetailSaveAction getSaveDetailAction() {
    if (context.get("saveDetailAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarDetailSaveAction) context
          .get("saveDetailAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarDetailSaveAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarDetailSaveAction();
    context.put("saveDetailAction", bean);
    bean.setModel(getManageAppModel());
    bean.setTreeModel(getAppmodel());
    bean.setEditor(getDlgEditor());
    bean.setValidationService(getSaveDetailValidation());
    bean.setExceptionHandler(getExceptionHandler());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarYearAdjustAction getYearAdjustAction() {
    if (context.get("yearAdjustAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarYearAdjustAction) context
          .get("yearAdjustAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarYearAdjustAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarYearAdjustAction();
    context.put("yearAdjustAction", bean);
    bean.setTreeModel(getAppmodel());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarAddAction getAddAction() {
    if (context.get("AddAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarAddAction) context.get("AddAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarAddAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarAddAction();
    context.put("AddAction", bean);
    bean.setModel(getAppmodel());
    bean.setExceptionHandler(getExceptionHandler());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarEidtAction getEditAction() {
    if (context.get("EditAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarEidtAction) context.get("EditAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarEidtAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarEidtAction();
    context.put("EditAction", bean);
    bean.setModel(getAppmodel());
    bean.setExceptionHandler(getExceptionHandler());
    bean.setResourceCode("workcalendar");
    bean.setMdOperateCode("edit");
    bean.setEditor(getWorkCalendarCardEditor());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarSaveAction getSaveAction() {
    if (context.get("SaveAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarSaveAction) context.get("SaveAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarSaveAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarSaveAction();
    context.put("SaveAction", bean);
    bean.setModel(getAppmodel());
    bean.setEditor(getWorkCalendarCardEditor());
    bean.setValidationService(getSaveValidation());
    bean.setExceptionHandler(getExceptionHandler());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarSaveAddAction getSaveAddAction() {
    if (context.get("SaveAddAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarSaveAddAction) context.get("SaveAddAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarSaveAddAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarSaveAddAction();
    context.put("SaveAddAction", bean);
    bean.setModel(getAppmodel());
    bean.setEditor(getWorkCalendarCardEditor());
    bean.setAddAction(getAddAction());
    bean.setValidationService(getSaveValidation());
    bean.setExceptionHandler(getExceptionHandler());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.bs.uif2.validation.DefaultValidationService getSaveValidation() {
    if (context.get("saveValidation") != null)
      return (nc.bs.uif2.validation.DefaultValidationService) context.get("saveValidation");
    nc.bs.uif2.validation.DefaultValidationService bean =
        new nc.bs.uif2.validation.DefaultValidationService();
    context.put("saveValidation", bean);
    bean.setValidators(getManagedList1());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private List getManagedList1() {
    List list = new ArrayList();
    list.add(getWorkCalendarNullValidator_1d84c65());
    list.add(getWorkCalendarDateValidator_17fa3d7());
    return list;
  }

  private nc.bs.bd.workcalendar.validator.WorkCalendarNullValidator getWorkCalendarNullValidator_1d84c65() {
    if (context.get("nc.bs.bd.workcalendar.validator.WorkCalendarNullValidator#1d84c65") != null)
      return (nc.bs.bd.workcalendar.validator.WorkCalendarNullValidator) context
          .get("nc.bs.bd.workcalendar.validator.WorkCalendarNullValidator#1d84c65");
    nc.bs.bd.workcalendar.validator.WorkCalendarNullValidator bean =
        new nc.bs.bd.workcalendar.validator.WorkCalendarNullValidator();
    context.put("nc.bs.bd.workcalendar.validator.WorkCalendarNullValidator#1d84c65", bean);
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator getWorkCalendarDateValidator_17fa3d7() {
    if (context.get("nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator#17fa3d7") != null)
      return (nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator) context
          .get("nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator#17fa3d7");
    nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator bean =
        new nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator();
    context.put("nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator#17fa3d7", bean);
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.bs.uif2.validation.DefaultValidationService getSaveDetailValidation() {
    if (context.get("saveDetailValidation") != null)
      return (nc.bs.uif2.validation.DefaultValidationService) context.get("saveDetailValidation");
    nc.bs.uif2.validation.DefaultValidationService bean =
        new nc.bs.uif2.validation.DefaultValidationService();
    context.put("saveDetailValidation", bean);
    bean.setValidators(getManagedList2());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private List getManagedList2() {
    List list = new ArrayList();
    list.add(getWorkCalendarAddDetailNullValidator_dd7905());
    list.add(getWorkCalendarDateValidator_3469f8());
    return list;
  }

  private nc.bs.bd.workcalendar.validator.WorkCalendarAddDetailNullValidator getWorkCalendarAddDetailNullValidator_dd7905() {
    if (context.get("nc.bs.bd.workcalendar.validator.WorkCalendarAddDetailNullValidator#dd7905") != null)
      return (nc.bs.bd.workcalendar.validator.WorkCalendarAddDetailNullValidator) context
          .get("nc.bs.bd.workcalendar.validator.WorkCalendarAddDetailNullValidator#dd7905");
    nc.bs.bd.workcalendar.validator.WorkCalendarAddDetailNullValidator bean =
        new nc.bs.bd.workcalendar.validator.WorkCalendarAddDetailNullValidator();
    context.put("nc.bs.bd.workcalendar.validator.WorkCalendarAddDetailNullValidator#dd7905", bean);
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator getWorkCalendarDateValidator_3469f8() {
    if (context.get("nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator#3469f8") != null)
      return (nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator) context
          .get("nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator#3469f8");
    nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator bean =
        new nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator();
    context.put("nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator#3469f8", bean);
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.bs.uif2.validation.DefaultValidationService getUpdateDateValidation() {
    if (context.get("updateDateValidation") != null)
      return (nc.bs.uif2.validation.DefaultValidationService) context.get("updateDateValidation");
    nc.bs.uif2.validation.DefaultValidationService bean =
        new nc.bs.uif2.validation.DefaultValidationService();
    context.put("updateDateValidation", bean);
    bean.setValidators(getManagedList3());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private List getManagedList3() {
    List list = new ArrayList();
    list.add(getWorkCalendarDateValidator_11936a2());
    return list;
  }

  private nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator getWorkCalendarDateValidator_11936a2() {
    if (context.get("nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator#11936a2") != null)
      return (nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator) context
          .get("nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator#11936a2");
    nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator bean =
        new nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator();
    context.put("nc.bs.bd.workcalendar.validator.WorkCalendarDateValidator#11936a2", bean);
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarDelAction getDeleteAction() {
    if (context.get("DeleteAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarDelAction) context.get("DeleteAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarDelAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarDelAction();
    context.put("DeleteAction", bean);
    bean.setModel(getAppmodel());
    bean.setExceptionHandler(getExceptionHandler());
    bean.setResourceCode("workcalendar");
    bean.setMdOperateCode("delete");
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.uif2.actions.CancelAction getCancelAction() {
    if (context.get("CancelAction") != null)
      return (nc.ui.uif2.actions.CancelAction) context.get("CancelAction");
    nc.ui.uif2.actions.CancelAction bean = new nc.ui.uif2.actions.CancelAction();
    context.put("CancelAction", bean);
    bean.setModel(getAppmodel());
    bean.setEditor(getWorkCalendarCardEditor());
    bean.setExceptionHandler(getExceptionHandler());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.uif2.actions.RefreshAction getRefreshAction() {
    if (context.get("RefreshAction") != null)
      return (nc.ui.uif2.actions.RefreshAction) context.get("RefreshAction");
    nc.ui.uif2.actions.RefreshAction bean = new nc.ui.uif2.actions.RefreshAction();
    context.put("RefreshAction", bean);
    bean.setModel(getAppmodel());
    bean.setDataManager(getModelDataManager());
    bean.setExceptionHandler(getExceptionHandler());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.funcnode.ui.action.MenuAction getFilterActionMenu() {
    if (context.get("filterActionMenu") != null)
      return (nc.funcnode.ui.action.MenuAction) context.get("filterActionMenu");
    nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
    context.put("filterActionMenu", bean);
    bean.setCode("filter");
    bean.setName(getI18nFB_51ea25());
    bean.setActions(getManagedList4());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private java.lang.String getI18nFB_51ea25() {
    if (context.get("nc.ui.uif2.I18nFB#51ea25") != null)
      return (java.lang.String) context.get("nc.ui.uif2.I18nFB#51ea25");
    nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#51ea25", bean);
    bean.setResDir("common");
    bean.setDefaultValue("过滤");
    bean.setResId("UCH069");
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    try {
      Object product = bean.getObject();
      context.put("nc.ui.uif2.I18nFB#51ea25", product);
      return (java.lang.String) product;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private List getManagedList4() {
    List list = new ArrayList();
    list.add(getFilterAction());
    return list;
  }

  public nc.ui.uif2.actions.ShowDisableDataAction getFilterAction() {
    if (context.get("filterAction") != null)
      return (nc.ui.uif2.actions.ShowDisableDataAction) context.get("filterAction");
    nc.ui.uif2.actions.ShowDisableDataAction bean = new nc.ui.uif2.actions.ShowDisableDataAction();
    context.put("filterAction", bean);
    bean.setModel(getAppmodel());
    bean.setDataManager(getModelDataManager());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.funcnode.ui.action.GroupAction getEnableActionGroup() {
    if (context.get("enableActionGroup") != null)
      return (nc.funcnode.ui.action.GroupAction) context.get("enableActionGroup");
    nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
    context.put("enableActionGroup", bean);
    bean.setCode("EnableGroup");
    bean.setActions(getManagedList5());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private List getManagedList5() {
    List list = new ArrayList();
    list.add(getEnableAction());
    list.add(getDisableAction());
    return list;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarEnableAction getEnableAction() {
    if (context.get("enableAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarEnableAction) context.get("enableAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarEnableAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarEnableAction();
    context.put("enableAction", bean);
    bean.setModel(getAppmodel());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.workcalendar.actions.WorkCalendarDisableAction getDisableAction() {
    if (context.get("disableAction") != null)
      return (nc.ui.bd.workcalendar.actions.WorkCalendarDisableAction) context.get("disableAction");
    nc.ui.bd.workcalendar.actions.WorkCalendarDisableAction bean =
        new nc.ui.bd.workcalendar.actions.WorkCalendarDisableAction();
    context.put("disableAction", bean);
    bean.setModel(getAppmodel());
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

  public nc.ui.uif2.FunNodeClosingHandler getClosingListener() {
    if (context.get("ClosingListener") != null)
      return (nc.ui.uif2.FunNodeClosingHandler) context.get("ClosingListener");
    nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
    context.put("ClosingListener", bean);
    bean.setModel(getAppmodel());
    bean.setSaveaction(getSaveAction());
    bean.setCancelaction(getCancelAction());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.uif2.DefaultExceptionHanler getExceptionHandler() {
    if (context.get("exceptionHandler") != null)
      return (nc.ui.uif2.DefaultExceptionHanler) context.get("exceptionHandler");
    nc.ui.uif2.DefaultExceptionHanler bean =
        new nc.ui.uif2.DefaultExceptionHanler(getWorkCalendarCardEditor());
    context.put("exceptionHandler", bean);
    bean.setContext(getContext());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  public nc.ui.bd.pub.BDOrgPanel getOrgpanel() {
    if (context.get("orgpanel") != null)
      return (nc.ui.bd.pub.BDOrgPanel) context.get("orgpanel");
    nc.ui.bd.pub.BDOrgPanel bean = new nc.ui.bd.pub.BDOrgPanel();
    context.put("orgpanel", bean);
    bean.setModel(getAppmodel());
    bean.setDataManager(getModelDataManager());
    bean.setPk_orgtype("BUSINESSUNIT00000000");
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
    bean.setTangramLayoutRoot(getVSNode_1931de5());
    bean.setActions(getManagedList6());
    bean.setEditActions(getManagedList7());
    bean.setModel(getAppmodel());
    bean.initUI();
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1931de5() {
    if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#1931de5") != null)
      return (nc.ui.uif2.tangramlayout.node.VSNode) context
          .get("nc.ui.uif2.tangramlayout.node.VSNode#1931de5");
    nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
    context.put("nc.ui.uif2.tangramlayout.node.VSNode#1931de5", bean);
    bean.setUp(getCNode_195f538());
    bean.setDown(getHSNode_98d431());
    bean.setDividerLocation(30f);
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private nc.ui.uif2.tangramlayout.node.CNode getCNode_195f538() {
    if (context.get("nc.ui.uif2.tangramlayout.node.CNode#195f538") != null)
      return (nc.ui.uif2.tangramlayout.node.CNode) context
          .get("nc.ui.uif2.tangramlayout.node.CNode#195f538");
    nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
    context.put("nc.ui.uif2.tangramlayout.node.CNode#195f538", bean);
    bean.setComponent(getOrgpanel());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_98d431() {
    if (context.get("nc.ui.uif2.tangramlayout.node.HSNode#98d431") != null)
      return (nc.ui.uif2.tangramlayout.node.HSNode) context
          .get("nc.ui.uif2.tangramlayout.node.HSNode#98d431");
    nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
    context.put("nc.ui.uif2.tangramlayout.node.HSNode#98d431", bean);
    bean.setLeft(getCNode_163993());
    bean.setRight(getVSNode_1ac8236());
    bean.setDividerLocation(0.2f);
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private nc.ui.uif2.tangramlayout.node.CNode getCNode_163993() {
    if (context.get("nc.ui.uif2.tangramlayout.node.CNode#163993") != null)
      return (nc.ui.uif2.tangramlayout.node.CNode) context
          .get("nc.ui.uif2.tangramlayout.node.CNode#163993");
    nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
    context.put("nc.ui.uif2.tangramlayout.node.CNode#163993", bean);
    bean.setComponent(getTreePanel());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_1ac8236() {
    if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#1ac8236") != null)
      return (nc.ui.uif2.tangramlayout.node.VSNode) context
          .get("nc.ui.uif2.tangramlayout.node.VSNode#1ac8236");
    nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
    context.put("nc.ui.uif2.tangramlayout.node.VSNode#1ac8236", bean);
    bean.setUp(getCNode_11e1019());
    bean.setDown(getCNode_b30f39());
    bean.setDividerLocation(135f);
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private nc.ui.uif2.tangramlayout.node.CNode getCNode_11e1019() {
    if (context.get("nc.ui.uif2.tangramlayout.node.CNode#11e1019") != null)
      return (nc.ui.uif2.tangramlayout.node.CNode) context
          .get("nc.ui.uif2.tangramlayout.node.CNode#11e1019");
    nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
    context.put("nc.ui.uif2.tangramlayout.node.CNode#11e1019", bean);
    bean.setComponent(getWorkCalendarCardEditor());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private nc.ui.uif2.tangramlayout.node.CNode getCNode_b30f39() {
    if (context.get("nc.ui.uif2.tangramlayout.node.CNode#b30f39") != null)
      return (nc.ui.uif2.tangramlayout.node.CNode) context
          .get("nc.ui.uif2.tangramlayout.node.CNode#b30f39");
    nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
    context.put("nc.ui.uif2.tangramlayout.node.CNode#b30f39", bean);
    bean.setComponent(getWorkCalendarPanel());
    setBeanFacotryIfBeanFacatoryAware(bean);
    invokeInitializingBean(bean);
    return bean;
  }

  private List getManagedList6() {
    List list = new ArrayList();
    list.add(getAddAction());
    list.add(getEditAction());
    list.add(getDeleteAction());
    list.add(getSeparatorAction());
    list.add(getRefreshAction());
    list.add(getFilterActionMenu());
    list.add(getSeparatorAction());
    list.add(getEnableActionGroup());
    list.add(getSeparatorAction());
    list.add(getAddDetailAction());
    list.add(getYearAdjustAction());
    list.add(getSeparatorAction());
    return list;
  }

  private List getManagedList7() {
    List list = new ArrayList();
    list.add(getSaveAction());
    list.add(getSaveAddAction());
    list.add(getSeparatorAction());
    list.add(getCancelAction());
    return list;
  }

}
