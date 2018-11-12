package nc.ui.hi.employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class uif2_ext_config extends AbstractJavaBeanDefinition {
    private Map<String, Object> context = new HashMap();

    public nc.ui.uif2.uiextend.ActionExtPreprocessor getActionExt_mediator() {
	if (context.get("ActionExt_mediator") != null)
	    return (nc.ui.uif2.uiextend.ActionExtPreprocessor) context.get("ActionExt_mediator");
	nc.ui.uif2.uiextend.ActionExtPreprocessor bean = new nc.ui.uif2.uiextend.ActionExtPreprocessor();
	context.put("ActionExt_mediator", bean);
	bean.setActionExtList(getManagedList0());
	bean.process();
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList0() {
	List list = new ArrayList();
	list.add(getActionExtInfo_e7462b());
	list.add(getActionExtInfo_4d0e64());
	return list;
    }

    private nc.ui.uif2.uiextend.ActionExtInfo getActionExtInfo_e7462b() {
	if (context.get("nc.ui.uif2.uiextend.ActionExtInfo#e7462b") != null)
	    return (nc.ui.uif2.uiextend.ActionExtInfo) context.get("nc.ui.uif2.uiextend.ActionExtInfo#e7462b");
	nc.ui.uif2.uiextend.ActionExtInfo bean = new nc.ui.uif2.uiextend.ActionExtInfo();
	context.put("nc.ui.uif2.uiextend.ActionExtInfo#e7462b", bean);
	bean.setActionContainer((nc.ui.uif2.actions.AbstractToftPanelActionContainer) findBeanInUIF2BeanFactory("listViewActions"));
	bean.setUistate("NOTEDIT_STATE");
	bean.setTargetAction((javax.swing.Action) findBeanInUIF2BeanFactory("printActionGroup"));
	bean.setPosition("BEFORE_POSITION");
	bean.setExtType("INSERT_EXTTYPE");
	bean.setAction(getRenewActionGroup());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private nc.ui.uif2.uiextend.ActionExtInfo getActionExtInfo_4d0e64() {
	if (context.get("nc.ui.uif2.uiextend.ActionExtInfo#4d0e64") != null)
	    return (nc.ui.uif2.uiextend.ActionExtInfo) context.get("nc.ui.uif2.uiextend.ActionExtInfo#4d0e64");
	nc.ui.uif2.uiextend.ActionExtInfo bean = new nc.ui.uif2.uiextend.ActionExtInfo();
	context.put("nc.ui.uif2.uiextend.ActionExtInfo#4d0e64", bean);
	bean.setActionContainer((nc.ui.uif2.actions.AbstractToftPanelActionContainer) findBeanInUIF2BeanFactory("listViewActions"));
	bean.setUistate("NOTEDIT_STATE");
	bean.setTargetAction(getRenewActionGroup());
	bean.setPosition("AFTER_POSITION");
	bean.setExtType("INSERT_EXTTYPE");
	bean.setAction((javax.swing.Action) findBeanInUIF2BeanFactory("separatorAction"));
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.hi.psndoc.action.RenewRangeAction getRenewRangeAction() {
	if (context.get("RenewRangeAction") != null)
	    return (nc.ui.hi.psndoc.action.RenewRangeAction) context.get("RenewRangeAction");
	nc.ui.hi.psndoc.action.RenewRangeAction bean = new nc.ui.hi.psndoc.action.RenewRangeAction();
	context.put("RenewRangeAction", bean);
	bean.setContext((nc.vo.uif2.LoginContext) findBeanInUIF2BeanFactory("context"));
	bean.setModel((nc.ui.hi.employee.model.EmployeePsndocModel) findBeanInUIF2BeanFactory("manageAppModel"));
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.ui.hi.psndoc.action.RenewGroupInsAction getRenewGroupInsAction() {
	if (context.get("RenewGroupInsAction") != null)
	    return (nc.ui.hi.psndoc.action.RenewGroupInsAction) context.get("RenewGroupInsAction");
	nc.ui.hi.psndoc.action.RenewGroupInsAction bean = new nc.ui.hi.psndoc.action.RenewGroupInsAction();
	context.put("RenewGroupInsAction", bean);
	bean.setContext((nc.vo.uif2.LoginContext) findBeanInUIF2BeanFactory("context"));
	bean.setModel((nc.ui.hi.employee.model.EmployeePsndocModel) findBeanInUIF2BeanFactory("manageAppModel"));
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    public nc.funcnode.ui.action.GroupAction getRenewActionGroup() {
	if (context.get("renewActionGroup") != null)
	    return (nc.funcnode.ui.action.GroupAction) context.get("renewActionGroup");
	nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
	context.put("renewActionGroup", bean);
	bean.setActions(getManagedList1());
	setBeanFacotryIfBeanFacatoryAware(bean);
	invokeInitializingBean(bean);
	return bean;
    }

    private List getManagedList1() {
	List list = new ArrayList();
	list.add(getRenewRangeAction());
	list.add(getRenewGroupInsAction());
	return list;
    }

}
