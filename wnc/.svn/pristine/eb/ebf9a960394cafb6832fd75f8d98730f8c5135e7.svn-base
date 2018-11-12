package nc.ui.hi.employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class employee_validator_config extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.vo.hr.validator.IDFieldValidatorConfig getIDvalidationConfig(){
 if(context.get("IDvalidationConfig")!=null)
 return (nc.vo.hr.validator.IDFieldValidatorConfig)context.get("IDvalidationConfig");
  nc.vo.hr.validator.IDFieldValidatorConfig bean = new nc.vo.hr.validator.IDFieldValidatorConfig();
  context.put("IDvalidationConfig",bean);
  bean.setIdValidator(getManagedMap0());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private Map getManagedMap0(){  Map map = new HashMap();  map.put("0","nc.vo.hr.validator.IDCardValidator");  map.put("1","nc.vo.hr.validator.PassPortValidator");  return map;}

public nc.ui.hr.uif2.validator.BillNotNullValidateService getBillNotNullValidator(){
 if(context.get("billNotNullValidator")!=null)
 return (nc.ui.hr.uif2.validator.BillNotNullValidateService)context.get("billNotNullValidator");
  nc.ui.hr.uif2.validator.BillNotNullValidateService bean = new nc.ui.hr.uif2.validator.BillNotNullValidateService((nc.ui.uif2.editor.BillForm)findBeanInUIF2BeanFactory("psndocFormEditor"));  context.put("billNotNullValidator",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil getSuperValidationConfig(){
 if(context.get("SuperValidationConfig")!=null)
 return (nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil)context.get("SuperValidationConfig");
  nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil bean = new nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil();
  context.put("SuperValidationConfig",bean);
  bean.setFieldRelationMap(getManagedMap1());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private Map getManagedMap1(){  Map map = new HashMap();  map.put("additionalValidationOfSave",getAdditionalValidationOfSave());  return map;}

public nc.ui.hr.tools.uilogic.SuperLogicProcessor getAdditionalValidationOfSave(){
 if(context.get("additionalValidationOfSave")!=null)
 return (nc.ui.hr.tools.uilogic.SuperLogicProcessor)context.get("additionalValidationOfSave");
  nc.ui.hr.tools.uilogic.SuperLogicProcessor bean = new nc.ui.hr.tools.uilogic.SuperLogicProcessor();
  context.put("additionalValidationOfSave",bean);
  bean.setMethods(getManagedList0());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  return list;}

}
