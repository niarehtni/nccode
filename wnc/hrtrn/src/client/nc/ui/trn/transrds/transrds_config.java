package nc.ui.trn.transrds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class transrds_config extends AbstractJavaBeanDefinition {
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

	public nc.vo.bd.meta.BDObjectAdpaterFactory getBoadatorfactory() {
		if (context.get("boadatorfactory") != null)
			return (nc.vo.bd.meta.BDObjectAdpaterFactory) context.get("boadatorfactory");
		nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
		context.put("boadatorfactory", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.om.psnnavi.model.NaviTreeAppModelDataManager getLeftModelDataManager() {
		if (context.get("leftModelDataManager") != null)
			return (nc.ui.om.psnnavi.model.NaviTreeAppModelDataManager) context.get("leftModelDataManager");
		nc.ui.om.psnnavi.model.NaviTreeAppModelDataManager bean = new nc.ui.om.psnnavi.model.NaviTreeAppModelDataManager();
		context.put("leftModelDataManager", bean);
		bean.setModel(getLeftSuperModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.om.psnnavi.model.NaviLeftAppModel getLeftSuperModel() {
		if (context.get("leftSuperModel") != null)
			return (nc.ui.om.psnnavi.model.NaviLeftAppModel) context.get("leftSuperModel");
		nc.ui.om.psnnavi.model.NaviLeftAppModel bean = new nc.ui.om.psnnavi.model.NaviLeftAppModel();
		context.put("leftSuperModel", bean);
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.vo.om.psnnavi.NaviPropertyVO getNaviProperty() {
		if (context.get("naviProperty") != null)
			return (nc.vo.om.psnnavi.NaviPropertyVO) context.get("naviProperty");
		nc.vo.om.psnnavi.NaviPropertyVO bean = new nc.vo.om.psnnavi.NaviPropertyVO();
		context.put("naviProperty", bean);
		bean.setIncludeChildHR(true);
		bean.setOtherNodeNameMsAOS(getI18nFB_1e21d8e());
		bean.setNaviItems(getManagedList0());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1e21d8e() {
		if (context.get("nc.ui.uif2.I18nFB#1e21d8e") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1e21d8e");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1e21d8e", bean);
		bean.setResDir("6007psn");
		bean.setResId("x6007psn0001");
		bean.setDefaultValue("指定管理的人?");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1e21d8e", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList0() {
		List list = new ArrayList();
		list.add("navi_style_msaos");
		return list;
	}

	public nc.ui.hr.uif2.model.HrDefaultAppModelService getTreeModelServiceMsAOS() {
		if (context.get("treeModelServiceMsAOS") != null)
			return (nc.ui.hr.uif2.model.HrDefaultAppModelService) context.get("treeModelServiceMsAOS");
		nc.ui.hr.uif2.model.HrDefaultAppModelService bean = new nc.ui.hr.uif2.model.HrDefaultAppModelService();
		context.put("treeModelServiceMsAOS", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.model.HrDefaultAppModelService getTreeModelServiceOrg() {
		if (context.get("treeModelServiceOrg") != null)
			return (nc.ui.hr.uif2.model.HrDefaultAppModelService) context.get("treeModelServiceOrg");
		nc.ui.hr.uif2.model.HrDefaultAppModelService bean = new nc.ui.hr.uif2.model.HrDefaultAppModelService();
		context.put("treeModelServiceOrg", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.model.HrDefaultAppModelService getTreeModelServicePsnType() {
		if (context.get("treeModelServicePsnType") != null)
			return (nc.ui.hr.uif2.model.HrDefaultAppModelService) context.get("treeModelServicePsnType");
		nc.ui.hr.uif2.model.HrDefaultAppModelService bean = new nc.ui.hr.uif2.model.HrDefaultAppModelService();
		context.put("treeModelServicePsnType", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hr.uif2.model.HrDefaultAppModelService getTreeModelServicePostSeries() {
		if (context.get("treeModelServicePostSeries") != null)
			return (nc.ui.hr.uif2.model.HrDefaultAppModelService) context.get("treeModelServicePostSeries");
		nc.ui.hr.uif2.model.HrDefaultAppModelService bean = new nc.ui.hr.uif2.model.HrDefaultAppModelService();
		context.put("treeModelServicePostSeries", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.om.psnnavi.view.AOSNaviTreeCreateStrategy getTreeCreateStrategyOrg() {
		if (context.get("treeCreateStrategyOrg") != null)
			return (nc.ui.om.psnnavi.view.AOSNaviTreeCreateStrategy) context.get("treeCreateStrategyOrg");
		nc.ui.om.psnnavi.view.AOSNaviTreeCreateStrategy bean = new nc.ui.om.psnnavi.view.AOSNaviTreeCreateStrategy();
		context.put("treeCreateStrategyOrg", bean);
		bean.setFactory(getBoadatorfactory());
		bean.setRootName(getI18nFB_1324eaa());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1324eaa() {
		if (context.get("nc.ui.uif2.I18nFB#1324eaa") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1324eaa");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1324eaa", bean);
		bean.setResDir("menucode");
		bean.setDefaultValue("行政??");
		bean.setResId("X600531");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1324eaa", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public nc.ui.om.psnnavi.model.AOSNaviAppModel getTreeAppModelOrg() {
		if (context.get("treeAppModelOrg") != null)
			return (nc.ui.om.psnnavi.model.AOSNaviAppModel) context.get("treeAppModelOrg");
		nc.ui.om.psnnavi.model.AOSNaviAppModel bean = new nc.ui.om.psnnavi.model.AOSNaviAppModel();
		context.put("treeAppModelOrg", bean);
		bean.setService(getTreeModelServiceOrg());
		bean.setTreeCreateStrategy(getTreeCreateStrategyOrg());
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		bean.setContext(getContext());
		bean.setNaviProperty(getNaviProperty());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.om.psnnavi.view.MsAOSNaviTreeCreateStrategy getTreeCreateStrategyMsAOS() {
		if (context.get("treeCreateStrategyMsAOS") != null)
			return (nc.ui.om.psnnavi.view.MsAOSNaviTreeCreateStrategy) context.get("treeCreateStrategyMsAOS");
		nc.ui.om.psnnavi.view.MsAOSNaviTreeCreateStrategy bean = new nc.ui.om.psnnavi.view.MsAOSNaviTreeCreateStrategy();
		context.put("treeCreateStrategyMsAOS", bean);
		bean.setFactory(getBoadatorfactory());
		bean.setRootName(getI18nFB_1ca8ae6());
		bean.setModel(getTreeAppModelMsAOS());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1ca8ae6() {
		if (context.get("nc.ui.uif2.I18nFB#1ca8ae6") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1ca8ae6");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1ca8ae6", bean);
		bean.setResDir("menucode");
		bean.setDefaultValue("管理范?");
		bean.setResId("X600532");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1ca8ae6", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public nc.ui.om.psnnavi.model.AOSNaviAppModel getTreeAppModelMsAOS() {
		if (context.get("treeAppModelMsAOS") != null)
			return (nc.ui.om.psnnavi.model.AOSNaviAppModel) context.get("treeAppModelMsAOS");
		nc.ui.om.psnnavi.model.AOSNaviAppModel bean = new nc.ui.om.psnnavi.model.AOSNaviAppModel();
		context.put("treeAppModelMsAOS", bean);
		bean.setService(getTreeModelServiceMsAOS());
		bean.setTreeCreateStrategy(getTreeCreateStrategyMsAOS());
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		bean.setContext(getContext());
		bean.setNaviProperty(getNaviProperty());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.vo.bd.meta.BDObjectTreeCreateStrategy getTreeCreateStrategyPsnType() {
		if (context.get("treeCreateStrategyPsnType") != null)
			return (nc.vo.bd.meta.BDObjectTreeCreateStrategy) context.get("treeCreateStrategyPsnType");
		nc.vo.bd.meta.BDObjectTreeCreateStrategy bean = new nc.vo.bd.meta.BDObjectTreeCreateStrategy();
		context.put("treeCreateStrategyPsnType", bean);
		bean.setFactory(getBoadatorfactory());
		bean.setRootName(getI18nFB_651258());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_651258() {
		if (context.get("nc.ui.uif2.I18nFB#651258") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#651258");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#651258", bean);
		bean.setResDir("menucode");
		bean.setDefaultValue("人???");
		bean.setResId("X600533");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#651258", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public nc.ui.uif2.model.HierachicalDataAppModel getTreeAppModelPsnType() {
		if (context.get("treeAppModelPsnType") != null)
			return (nc.ui.uif2.model.HierachicalDataAppModel) context.get("treeAppModelPsnType");
		nc.ui.uif2.model.HierachicalDataAppModel bean = new nc.ui.uif2.model.HierachicalDataAppModel();
		context.put("treeAppModelPsnType", bean);
		bean.setService(getTreeModelServicePsnType());
		bean.setTreeCreateStrategy(getTreeCreateStrategyPsnType());
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.vo.bd.meta.BDObjectTreeCreateStrategy getTreeCreateStrategyPostSeries() {
		if (context.get("treeCreateStrategyPostSeries") != null)
			return (nc.vo.bd.meta.BDObjectTreeCreateStrategy) context.get("treeCreateStrategyPostSeries");
		nc.vo.bd.meta.BDObjectTreeCreateStrategy bean = new nc.vo.bd.meta.BDObjectTreeCreateStrategy();
		context.put("treeCreateStrategyPostSeries", bean);
		bean.setFactory(getBoadatorfactory());
		bean.setRootName(getI18nFB_1976526());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_1976526() {
		if (context.get("nc.ui.uif2.I18nFB#1976526") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#1976526");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#1976526", bean);
		bean.setResDir("menucode");
		bean.setDefaultValue("?位序列");
		bean.setResId("X600534");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#1976526", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public nc.ui.uif2.model.HierachicalDataAppModel getTreeAppModelPostSeries() {
		if (context.get("treeAppModelPostSeries") != null)
			return (nc.ui.uif2.model.HierachicalDataAppModel) context.get("treeAppModelPostSeries");
		nc.ui.uif2.model.HierachicalDataAppModel bean = new nc.ui.uif2.model.HierachicalDataAppModel();
		context.put("treeAppModelPostSeries", bean);
		bean.setService(getTreeModelServicePostSeries());
		bean.setTreeCreateStrategy(getTreeCreateStrategyPostSeries());
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.om.psnnavi.view.TreePanel_Org getTreePanelOrg() {
		if (context.get("treePanelOrg") != null)
			return (nc.ui.om.psnnavi.view.TreePanel_Org) context.get("treePanelOrg");
		nc.ui.om.psnnavi.view.TreePanel_Org bean = new nc.ui.om.psnnavi.view.TreePanel_Org();
		context.put("treePanelOrg", bean);
		bean.setModel(getTreeAppModelOrg());
		bean.setLeftSuperModel(getLeftSuperModel());
		bean.setRightModel(getManageAppModel());
		bean.setNaviProperty(getNaviProperty());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.om.psnnavi.view.TreePanelOfMsAOS getTreePanelMsAOS() {
		if (context.get("treePanelMsAOS") != null)
			return (nc.ui.om.psnnavi.view.TreePanelOfMsAOS) context.get("treePanelMsAOS");
		nc.ui.om.psnnavi.view.TreePanelOfMsAOS bean = new nc.ui.om.psnnavi.view.TreePanelOfMsAOS();
		context.put("treePanelMsAOS", bean);
		bean.setModel(getTreeAppModelMsAOS());
		bean.setLeftSuperModel(getLeftSuperModel());
		bean.setRightModel(getManageAppModel());
		bean.setNaviProperty(getNaviProperty());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.om.psnnavi.view.TreePanel_PsnType getTreePanelPsnType() {
		if (context.get("treePanelPsnType") != null)
			return (nc.ui.om.psnnavi.view.TreePanel_PsnType) context.get("treePanelPsnType");
		nc.ui.om.psnnavi.view.TreePanel_PsnType bean = new nc.ui.om.psnnavi.view.TreePanel_PsnType();
		context.put("treePanelPsnType", bean);
		bean.setModel(getTreeAppModelPsnType());
		bean.setLeftSuperModel(getLeftSuperModel());
		bean.setRightModel(getManageAppModel());
		bean.setNaviProperty(getNaviProperty());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.om.psnnavi.view.TreePanel_PostSeries getTreePanelPostSeries() {
		if (context.get("treePanelPostSeries") != null)
			return (nc.ui.om.psnnavi.view.TreePanel_PostSeries) context.get("treePanelPostSeries");
		nc.ui.om.psnnavi.view.TreePanel_PostSeries bean = new nc.ui.om.psnnavi.view.TreePanel_PostSeries();
		context.put("treePanelPostSeries", bean);
		bean.setModel(getTreeAppModelPostSeries());
		bean.setLeftSuperModel(getLeftSuperModel());
		bean.setRightModel(getManageAppModel());
		bean.setNaviProperty(getNaviProperty());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.om.psnnavi.view.NaviStylePanel getNaviStylePanel() {
		if (context.get("naviStylePanel") != null)
			return (nc.ui.om.psnnavi.view.NaviStylePanel) context.get("naviStylePanel");
		nc.ui.om.psnnavi.view.NaviStylePanel bean = new nc.ui.om.psnnavi.view.NaviStylePanel();
		context.put("naviStylePanel", bean);
		bean.setLeftSuperModel(getLeftSuperModel());
		bean.setRightModel(getManageAppModel());
		bean.setNaviProperty(getNaviProperty());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.tangramlayout.node.VSNode getNaviNode() {
		if (context.get("naviNode") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("naviNode");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("naviNode", bean);
		bean.setUp(getCNode_71cba3());
		bean.setDown(getCNode_f0501a());
		bean.setDividerLocation(55f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_71cba3() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#71cba3") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#71cba3");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#71cba3", bean);
		bean.setComponent(getNaviStylePanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_f0501a() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#f0501a") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#f0501a");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#f0501a", bean);
		bean.setComponent(getTreeContainer());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.om.psnnavi.view.TreePanelContainer getTreeContainer() {
		if (context.get("treeContainer") != null)
			return (nc.ui.om.psnnavi.view.TreePanelContainer) context.get("treeContainer");
		nc.ui.om.psnnavi.view.TreePanelContainer bean = new nc.ui.om.psnnavi.view.TreePanelContainer();
		context.put("treeContainer", bean);
		bean.setOrgTree(getTreePanelOrg());
		bean.setMsAOSTree(getTreePanelMsAOS());
		bean.setPsnTypeTree(getTreePanelPsnType());
		bean.setPostSeriesTree(getTreePanelPostSeries());
		bean.setNaviStylePanel(getNaviStylePanel());
		bean.setLeftSuperModel(getLeftSuperModel());
		bean.setNaviProperty(getNaviProperty());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public java.lang.String getBilltype() {
		if (context.get("billtype") != null)
			return (java.lang.String) context.get("billtype");
		java.lang.String bean = new java.lang.String("6113");
		context.put("billtype", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.dimissionrds.model.DimissionrdsPsninfoModelService getPsninfoModelService() {
		if (context.get("psninfoModelService") != null)
			return (nc.ui.trn.dimissionrds.model.DimissionrdsPsninfoModelService) context.get("psninfoModelService");
		nc.ui.trn.dimissionrds.model.DimissionrdsPsninfoModelService bean = new nc.ui.trn.dimissionrds.model.DimissionrdsPsninfoModelService();
		context.put("psninfoModelService", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.rds.model.RdsPsninfoModel getManageAppModel() {
		if (context.get("manageAppModel") != null)
			return (nc.ui.trn.rds.model.RdsPsninfoModel) context.get("manageAppModel");
		nc.ui.trn.rds.model.RdsPsninfoModel bean = new nc.ui.trn.rds.model.RdsPsninfoModel();
		context.put("manageAppModel", bean);
		bean.setService(getPsninfoModelService());
		bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.dimissionrds.model.DimissionrdsPsninfoDataManager getPsninfoDataManager() {
		if (context.get("psninfoDataManager") != null)
			return (nc.ui.trn.dimissionrds.model.DimissionrdsPsninfoDataManager) context.get("psninfoDataManager");
		nc.ui.trn.dimissionrds.model.DimissionrdsPsninfoDataManager bean = new nc.ui.trn.dimissionrds.model.DimissionrdsPsninfoDataManager();
		context.put("psninfoDataManager", bean);
		bean.setModel(getManageAppModel());
		bean.setContext(getContext());
		bean.setService(getPsninfoModelService());
		bean.setRadioPanel(getRadioBtnPnl());
		bean.setPaginationModel(getPaginationModel());
		bean.setPaginationDelegator(getPaginationDelegator());
		bean.setPaginationBar(getPaginationBar());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.dimissionrds.model.DimissionrdsDataInitListener getInitDataListener() {
		if (context.get("InitDataListener") != null)
			return (nc.ui.trn.dimissionrds.model.DimissionrdsDataInitListener) context.get("InitDataListener");
		nc.ui.trn.dimissionrds.model.DimissionrdsDataInitListener bean = new nc.ui.trn.dimissionrds.model.DimissionrdsDataInitListener();
		context.put("InitDataListener", bean);
		bean.setContext(getContext());
		bean.setModel(getManageAppModel());
		bean.setLeftModel(getLeftSuperModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.dimissionrds.model.DimissionrdsMediator getDimissionrdsMediator() {
		if (context.get("dimissionrdsMediator") != null)
			return (nc.ui.trn.dimissionrds.model.DimissionrdsMediator) context.get("dimissionrdsMediator");
		nc.ui.trn.dimissionrds.model.DimissionrdsMediator bean = new nc.ui.trn.dimissionrds.model.DimissionrdsMediator();
		context.put("dimissionrdsMediator", bean);
		bean.setLeftTreeModel(getLeftSuperModel());
		bean.setPsninfoModel(getManageAppModel());
		bean.setPsninfoDataManager(getPsninfoDataManager());
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
		bean.setNodeKeies(getManagedList1());
		bean.load();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList1() {
		List list = new ArrayList();
		list.add("60090907");
		return list;
	}

	public nc.ui.trn.transrds.view.TransrdsListView getDimissionrdsListView() {
		if (context.get("dimissionrdsListView") != null)
			return (nc.ui.trn.transrds.view.TransrdsListView) context.get("dimissionrdsListView");
		nc.ui.trn.transrds.view.TransrdsListView bean = new nc.ui.trn.transrds.view.TransrdsListView();
		context.put("dimissionrdsListView", bean);
		bean.setModel(getManageAppModel());
		bean.setMultiSelectionEnable(true);
		bean.setMultiSelectionMode(1);
		bean.setPos("head");
		bean.setNodekey("60090907");
		bean.setBodyActions(getManagedList2());
		bean.setPaginationBar(getPaginationBar());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getAddLineAction());
		list.add(getEditLineAction());
		list.add(getDelLineAction());
		list.add(getBarNullAction());
		list.add(getSaveAction());
		list.add(getCancleAction());
		list.add(getBarNullAction());
		list.add(getQueryBillAction());
		return list;
	}

	public nc.ui.uif2.components.pagination.PaginationBar getPaginationBar() {
		if (context.get("paginationBar") != null)
			return (nc.ui.uif2.components.pagination.PaginationBar) context.get("paginationBar");
		nc.ui.uif2.components.pagination.PaginationBar bean = new nc.ui.uif2.components.pagination.PaginationBar();
		context.put("paginationBar", bean);
		bean.setPaginationModel(getPaginationModel());
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.components.pagination.PaginationModel getPaginationModel() {
		if (context.get("paginationModel") != null)
			return (nc.ui.uif2.components.pagination.PaginationModel) context.get("paginationModel");
		nc.ui.uif2.components.pagination.PaginationModel bean = new nc.ui.uif2.components.pagination.PaginationModel();
		context.put("paginationModel", bean);
		bean.init();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.components.pagination.BillManagePaginationDelegator getPaginationDelegator() {
		if (context.get("paginationDelegator") != null)
			return (nc.ui.uif2.components.pagination.BillManagePaginationDelegator) context.get("paginationDelegator");
		nc.ui.uif2.components.pagination.BillManagePaginationDelegator bean = new nc.ui.uif2.components.pagination.BillManagePaginationDelegator(
				getManageAppModel(), getPaginationModel());
		context.put("paginationDelegator", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public java.lang.String getResouceCode() {
		if (context.get("resouceCode") != null)
			return (java.lang.String) context.get("resouceCode");
		java.lang.String bean = new java.lang.String("6007psnjob");
		context.put("resouceCode", bean);
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

	public nc.ui.pub.beans.ActionsBar.ActionsBarSeparator getBarNullAction() {
		if (context.get("barNullAction") != null)
			return (nc.ui.pub.beans.ActionsBar.ActionsBarSeparator) context.get("barNullAction");
		nc.ui.pub.beans.ActionsBar.ActionsBarSeparator bean = new nc.ui.pub.beans.ActionsBar.ActionsBarSeparator();
		context.put("barNullAction", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.dimissionrds.action.BenchEditAction getBenchEditAction() {
		if (context.get("benchEditAction") != null)
			return (nc.ui.trn.dimissionrds.action.BenchEditAction) context.get("benchEditAction");
		nc.ui.trn.dimissionrds.action.BenchEditAction bean = new nc.ui.trn.dimissionrds.action.BenchEditAction();
		context.put("benchEditAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getDimissionrdsListView());
		bean.setRefreshAction(getRefreshAction());
		bean.setResourceCode(getResouceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.rds.action.CheckCtrtAction getCheckCtrtAction() {
		if (context.get("checkCtrtAction") != null)
			return (nc.ui.trn.rds.action.CheckCtrtAction) context.get("checkCtrtAction");
		nc.ui.trn.rds.action.CheckCtrtAction bean = new nc.ui.trn.rds.action.CheckCtrtAction();
		context.put("checkCtrtAction", bean);
		bean.setModel(getManageAppModel());
		bean.setResourceCode(getResouceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.rds.action.SortAction getSortAction() {
		if (context.get("sortAction") != null)
			return (nc.ui.trn.rds.action.SortAction) context.get("sortAction");
		nc.ui.trn.rds.action.SortAction bean = new nc.ui.trn.rds.action.SortAction();
		context.put("sortAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManger(getPsninfoDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.transrds.action.PsnjobHisAction getPsnjobHisAction() {
		if (context.get("psnjobHisAction") != null)
			return (nc.ui.trn.transrds.action.PsnjobHisAction) context.get("psnjobHisAction");
		nc.ui.trn.transrds.action.PsnjobHisAction bean = new nc.ui.trn.transrds.action.PsnjobHisAction();
		context.put("psnjobHisAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.transrds.action.TransRdsQueryAction getQueryAction() {
		if (context.get("queryAction") != null)
			return (nc.ui.trn.transrds.action.TransRdsQueryAction) context.get("queryAction");
		nc.ui.trn.transrds.action.TransRdsQueryAction bean = new nc.ui.trn.transrds.action.TransRdsQueryAction();
		context.put("queryAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getPsninfoDataManager());
		bean.setQueryDelegator(getPsnDelegator());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.rds.view.RdsQueryDelegator getPsnDelegator() {
		if (context.get("psnDelegator") != null)
			return (nc.ui.trn.rds.view.RdsQueryDelegator) context.get("psnDelegator");
		nc.ui.trn.rds.view.RdsQueryDelegator bean = new nc.ui.trn.rds.view.RdsQueryDelegator();
		context.put("psnDelegator", bean);
		bean.setNodeKey("psnqry");
		bean.setContext(getContext());
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.dimissionrds.action.DimissionRdsRefreshAction getRefreshAction() {
		if (context.get("refreshAction") != null)
			return (nc.ui.trn.dimissionrds.action.DimissionRdsRefreshAction) context.get("refreshAction");
		nc.ui.trn.dimissionrds.action.DimissionRdsRefreshAction bean = new nc.ui.trn.dimissionrds.action.DimissionRdsRefreshAction();
		context.put("refreshAction", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getPsninfoDataManager());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getAssistGroup() {
		if (context.get("assistGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context.get("assistGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("assistGroup", bean);
		bean.setCode("assist");
		bean.setName(getI18nFB_a13bc4());
		bean.setActions(getManagedList3());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_a13bc4() {
		if (context.get("nc.ui.uif2.I18nFB#a13bc4") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#a13bc4");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#a13bc4", bean);
		bean.setResDir("6001uif2");
		bean.setResId("x6001uif20001");
		bean.setDefaultValue("?助功能");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#a13bc4", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList3() {
		List list = new ArrayList();
		list.add(getCheckCtrtAction());
		list.add(getNullaction());
		list.add(getSortAction());
		return list;
	}

	public nc.ui.trn.transrds.action.QueryBusiBillAction getQueryBillAction() {
		if (context.get("queryBillAction") != null)
			return (nc.ui.trn.transrds.action.QueryBusiBillAction) context.get("queryBillAction");
		nc.ui.trn.transrds.action.QueryBusiBillAction bean = new nc.ui.trn.transrds.action.QueryBusiBillAction();
		context.put("queryBillAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getDimissionrdsListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.GroupAction getPrintActionGroup() {
		if (context.get("printActionGroup") != null)
			return (nc.funcnode.ui.action.GroupAction) context.get("printActionGroup");
		nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
		context.put("printActionGroup", bean);
		bean.setActions(getManagedList4());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList4() {
		List list = new ArrayList();
		list.add(getPrintaction());
		list.add(getPrintpreviewaction());
		list.add(getExportAction());
		return list;
	}

	public nc.ui.hi.pfpub.HiPFPrintPreviewAction getPrintpreviewaction() {
		if (context.get("printpreviewaction") != null)
			return (nc.ui.hi.pfpub.HiPFPrintPreviewAction) context.get("printpreviewaction");
		nc.ui.hi.pfpub.HiPFPrintPreviewAction bean = new nc.ui.hi.pfpub.HiPFPrintPreviewAction();
		context.put("printpreviewaction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getDimissionrdsListView());
		bean.setDirectPrint(false);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.pfpub.HiPFPrintAction getPrintaction() {
		if (context.get("printaction") != null)
			return (nc.ui.hi.pfpub.HiPFPrintAction) context.get("printaction");
		nc.ui.hi.pfpub.HiPFPrintAction bean = new nc.ui.hi.pfpub.HiPFPrintAction();
		context.put("printaction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getDimissionrdsListView());
		bean.setDirectPrint(true);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hi.psndoc.action.ExportListPsndocAction getExportAction() {
		if (context.get("exportAction") != null)
			return (nc.ui.hi.psndoc.action.ExportListPsndocAction) context.get("exportAction");
		nc.ui.hi.psndoc.action.ExportListPsndocAction bean = new nc.ui.hi.psndoc.action.ExportListPsndocAction();
		context.put("exportAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getDimissionrdsListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.MenuAction getPsntransterDismissActionGroup() {
		if (context.get("PsntransterDismissActionGroup") != null)
			return (nc.funcnode.ui.action.MenuAction) context.get("PsntransterDismissActionGroup");
		nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
		context.put("PsntransterDismissActionGroup", bean);
		bean.setCode("PsntransterDismiss");
		bean.setName(getI18nFB_15b00c8());
		bean.setActions(getManagedList5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private java.lang.String getI18nFB_15b00c8() {
		if (context.get("nc.ui.uif2.I18nFB#15b00c8") != null)
			return (java.lang.String) context.get("nc.ui.uif2.I18nFB#15b00c8");
		nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
		context.put("&nc.ui.uif2.I18nFB#15b00c8", bean);
		bean.setResDir("6009tran");
		bean.setDefaultValue("跨????");
		bean.setResId("X6009tran0033");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		try {
			Object product = bean.getObject();
			context.put("nc.ui.uif2.I18nFB#15b00c8", product);
			return (java.lang.String) product;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List getManagedList5() {
		List list = new ArrayList();
		list.add(getPsntranOutAction());
		list.add(getPsntranInAction());
		return list;
	}

	public nc.ui.trn.transrds.action.PsntransterOutAction getPsntranOutAction() {
		if (context.get("PsntranOutAction") != null)
			return (nc.ui.trn.transrds.action.PsntransterOutAction) context.get("PsntranOutAction");
		nc.ui.trn.transrds.action.PsntransterOutAction bean = new nc.ui.trn.transrds.action.PsntransterOutAction();
		context.put("PsntranOutAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.transrds.action.PsntransterInAction getPsntranInAction() {
		if (context.get("PsntranInAction") != null)
			return (nc.ui.trn.transrds.action.PsntransterInAction) context.get("PsntranInAction");
		nc.ui.trn.transrds.action.PsntransterInAction bean = new nc.ui.trn.transrds.action.PsntransterInAction();
		context.put("PsntranInAction", bean);
		bean.setModel(getManageAppModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.dimissionrds.action.AddLineAction getAddLineAction() {
		if (context.get("addLineAction") != null)
			return (nc.ui.trn.dimissionrds.action.AddLineAction) context.get("addLineAction");
		nc.ui.trn.dimissionrds.action.AddLineAction bean = new nc.ui.trn.dimissionrds.action.AddLineAction();
		context.put("addLineAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getDimissionrdsListView());
		bean.setResourceCode(getResouceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.dimissionrds.action.InsertLineAction getInsertLineAction() {
		if (context.get("insertLineAction") != null)
			return (nc.ui.trn.dimissionrds.action.InsertLineAction) context.get("insertLineAction");
		nc.ui.trn.dimissionrds.action.InsertLineAction bean = new nc.ui.trn.dimissionrds.action.InsertLineAction();
		context.put("insertLineAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getDimissionrdsListView());
		bean.setResourceCode(getResouceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.dimissionrds.action.EditLineAction getEditLineAction() {
		if (context.get("editLineAction") != null)
			return (nc.ui.trn.dimissionrds.action.EditLineAction) context.get("editLineAction");
		nc.ui.trn.dimissionrds.action.EditLineAction bean = new nc.ui.trn.dimissionrds.action.EditLineAction();
		context.put("editLineAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getDimissionrdsListView());
		bean.setResourceCode(getResouceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.dimissionrds.action.DelLineAction getDelLineAction() {
		if (context.get("delLineAction") != null)
			return (nc.ui.trn.dimissionrds.action.DelLineAction) context.get("delLineAction");
		nc.ui.trn.dimissionrds.action.DelLineAction bean = new nc.ui.trn.dimissionrds.action.DelLineAction();
		context.put("delLineAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getDimissionrdsListView());
		bean.setRefreshAction(getRefreshAction());
		bean.setResourceCode(getResouceCode());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.dimissionrds.action.SaveAction getSaveAction() {
		if (context.get("saveAction") != null)
			return (nc.ui.trn.dimissionrds.action.SaveAction) context.get("saveAction");
		nc.ui.trn.dimissionrds.action.SaveAction bean = new nc.ui.trn.dimissionrds.action.SaveAction();
		context.put("saveAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getDimissionrdsListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.dimissionrds.action.CancleAction getCancleAction() {
		if (context.get("cancleAction") != null)
			return (nc.ui.trn.dimissionrds.action.CancleAction) context.get("cancleAction");
		nc.ui.trn.dimissionrds.action.CancleAction bean = new nc.ui.trn.dimissionrds.action.CancleAction();
		context.put("cancleAction", bean);
		bean.setModel(getManageAppModel());
		bean.setListView(getDimissionrdsListView());
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
		bean.setCancelaction(getCancleAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.trn.rds.view.RdsTangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.trn.rds.view.RdsTangramContainer) context.get("container");
		nc.ui.trn.rds.view.RdsTangramContainer bean = new nc.ui.trn.rds.view.RdsTangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getVSNode_bd8ac7());
		bean.setActions(getManagedList6());
		bean.setModel(getLeftSuperModel());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_bd8ac7() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#bd8ac7") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context.get("nc.ui.uif2.tangramlayout.node.VSNode#bd8ac7");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#bd8ac7", bean);
		bean.setShowMode("NoDivider");
		bean.setUp(getCNode_19069f0());
		bean.setDown(getHSNode_8385ae());
		bean.setDividerLocation(30f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_19069f0() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#19069f0") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#19069f0");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#19069f0", bean);
		bean.setComponent(getOrgpanel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_8385ae() {
		if (context.get("nc.ui.uif2.tangramlayout.node.HSNode#8385ae") != null)
			return (nc.ui.uif2.tangramlayout.node.HSNode) context.get("nc.ui.uif2.tangramlayout.node.HSNode#8385ae");
		nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
		context.put("nc.ui.uif2.tangramlayout.node.HSNode#8385ae", bean);
		bean.setLeft(getCNode_1b1cc7());
		bean.setRight(getCNode_1fe5513());
		bean.setDividerLocation(0.25f);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1b1cc7() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1b1cc7") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1b1cc7");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1b1cc7", bean);
		bean.setComponent(getTreeContainer());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_1fe5513() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#1fe5513") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context.get("nc.ui.uif2.tangramlayout.node.CNode#1fe5513");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#1fe5513", bean);
		bean.setComponent(getDimissionrdsListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList6() {
		List list = new ArrayList();
		list.add(getBenchEditAction());
		list.add(getNullaction());
		list.add(getQueryAction());
		list.add(getRefreshAction());
		list.add(getNullaction());
		list.add(getPsntransterDismissActionGroup());
		list.add(getNullaction());
		list.add(getAssistGroup());
		list.add(getNullaction());
		list.add(getPrintActionGroup());
		return list;
	}

	public nc.ui.trn.transmgt.pub.TransPrimaryOrgPanel getOrgpanel() {
		if (context.get("orgpanel") != null)
			return (nc.ui.trn.transmgt.pub.TransPrimaryOrgPanel) context.get("orgpanel");
		nc.ui.trn.transmgt.pub.TransPrimaryOrgPanel bean = new nc.ui.trn.transmgt.pub.TransPrimaryOrgPanel();
		context.put("orgpanel", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getLeftModelDataManager());
		bean.setPk_orgtype("HRORGTYPE00000000000");
		bean.setChildren(getTopPaneChildren());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public java.util.ArrayList getTopPaneChildren() {
		if (context.get("topPaneChildren") != null)
			return (java.util.ArrayList) context.get("topPaneChildren");
		java.util.ArrayList bean = new java.util.ArrayList(getManagedList7());
		context.put("topPaneChildren", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList7() {
		List list = new ArrayList();
		list.add(getRadioBtnPnl());
		return list;
	}

	public nc.ui.trn.rds.view.RadioBtnPnl getRadioBtnPnl() {
		if (context.get("radioBtnPnl") != null)
			return (nc.ui.trn.rds.view.RadioBtnPnl) context.get("radioBtnPnl");
		nc.ui.trn.rds.view.RadioBtnPnl bean = new nc.ui.trn.rds.view.RadioBtnPnl();
		context.put("radioBtnPnl", bean);
		bean.setModel(getManageAppModel());
		bean.setDataManager(getPsninfoDataManager());
		bean.setLeftSuperModel(getLeftSuperModel());
		bean.init();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}
