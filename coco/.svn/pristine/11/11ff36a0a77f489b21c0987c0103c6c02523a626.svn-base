package nc.ui.leaveapply.ref.model;

import nc.ui.bd.ref.AbstractRefModel;

public class LeaveApplyRefModel extends AbstractRefModel {

  public LeaveApplyRefModel(String refNodeName) {
	  setRefNodeName(refNodeName);
  }
  
  public LeaveApplyRefModel() {
	  setRefNodeName("休假计划");
  }
  
     public void setRefNodeName(String refNodeName)
     {
       m_strRefNodeName = refNodeName;
       setRefTitle("休假计划");
       
       setFieldCode(new String[] { "billcode" });
   
       setFieldName(new String[] { "单据编码" });
   
       setDefaultFieldCount(1);
       setHiddenFieldCode(new String[] { "pk_leaveplan" });
       setRefCodeField("billcode");
       setRefNameField("billcode");
       setTableName("tbm_leaveplan");
       setPkFieldCode("pk_leaveplan");
       setOrderPart("billcode");
//     setAddEnableStateWherePart(true);
//     setBlurFields(new String[] { "code", "name", "country" });
       resetFieldName();
     }

}
