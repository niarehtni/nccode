package nc.uap.lfw.reference.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.WebContext;
import nc.uap.lfw.core.WebSession;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.cmd.base.UifCommand;
import nc.uap.lfw.core.comp.TreeViewComp;
import nc.uap.lfw.core.comp.WebTreeModel;
import nc.uap.lfw.core.comp.WebTreeNode;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.WindowContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.core.page.ViewComponents;
import nc.uap.lfw.core.page.ViewModels;
import nc.uap.lfw.core.refnode.MasterFieldInfo;
import nc.uap.lfw.core.refnode.RefNode;
import nc.uap.lfw.core.refnode.RefNodeRelation;
import nc.uap.lfw.reference.ILfwRefModel;
import nc.uap.lfw.reference.util.LfwRefUtil;
import uap.lfw.core.ml.LfwResBundle;

public class AppRefOkCmd extends UifCommand
{
  private Dataset currentDs;
  
  public AppRefOkCmd(Dataset ds)
  {
    currentDs = ds;
  }
  
  public void execute() {
    String widgetId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("widgetId");
    if ((widgetId == null) || ("".equals(widgetId)))
      widgetId = LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter("widgetId");
    String refNodeId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("refNodeId");
    if ((refNodeId == null) || ("".equals(refNodeId))) {
      refNodeId = LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter("nodeId");
    }
    String parentPageId = LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter("otherPageId");
    LfwWindow parentPm = null;
    if (AppLifeCycleContext.current().getApplicationContext().getWindowContext(parentPageId) != null) {
      parentPm = AppLifeCycleContext.current().getApplicationContext().getWindowContext(parentPageId).getWindow();
    } else {
      parentPm = uap.lfw.ref.util.LfwReferenceUtil.getParentWindow();
    }
    
    RefNode rfnode = (RefNode)parentPm.getView(widgetId).getViewModels().getRefNode(refNodeId);
    Row[] currRows = null;
    if (rfnode.isMultiSel()) {
      currRows = currentDs.getAllSelectedRows();
    } else {
      Row currRowOnly = currentDs.getSelectedRow();
      
      if (currRowOnly == null) {
        AppLifeCycleContext.current().getApplicationContext().closeWinDialog();
        return;
      }
      currRows = new Row[] { currRowOnly };
    }
    


    boolean selLeafOnly = rfnode.isSelLeafOnly();
    
    nc.uap.lfw.core.comp.WebComponent tree = AppLifeCycleContext.current().getViewContext().getView().getViewComponents().getComponent("reftree");
    
    if (currRows != null) {
      if ((tree != null) && (selLeafOnly)) {
        for (int i = 0; i < currRows.length; i++) {
          Row currRow = currRows[i];
          if (((TreeViewComp)tree).getTreeModel() != null) {
            WebTreeNode node = ((TreeViewComp)tree).getTreeModel().getTreeNodeByRowId(currRow.getRowId());
            if ((node != null) && 
              (node.getChildNodeList() != null) && (node.getChildNodeList().size() > 0)) {
              throw new LfwRuntimeException(LfwResBundle.getInstance().getStrByID("bc", "LfwRefOkCommand-000001"));
            }
            
          }
        }
      }
    }
    else {
      return;
    }
    



    if ((rfnode.getWriteDs() == null) || ("".equals(rfnode.getWriteDs()))) {
      processTextOk(widgetId, parentPm, rfnode, currRows);

    }
    else
    {

      processDatasetOk(widgetId, parentPm, rfnode, currRows);
    }
    


    clearRefRelationData(widgetId, parentPm, rfnode);
    


    refClose();
  }
  
  protected void clearRefRelationData(String widgetId, LfwWindow parentPm, RefNode rfnode)
  {
    if (parentPm.getView(widgetId).getViewModels().getRefNodeRelations() != null) {
      String writeFieldStr = rfnode.getWriteFields();
      String[] writeFields = writeFieldStr.split(",");
      RefNodeRelation[] rels = parentPm.getView(widgetId).getViewModels().getRefNodeRelations().getRefnodeRelations();
      for (RefNodeRelation refNodeRel : rels)
      {
        List<MasterFieldInfo> masterFieldInfoList = refNodeRel.getMasterFieldInfos();
        for (int i = 0; i < masterFieldInfoList.size(); i++) {
          MasterFieldInfo masterFieldInfo = (MasterFieldInfo)masterFieldInfoList.get(i);
          if ((masterFieldInfo.getDsId() != null) && (masterFieldInfo.getFieldId() != null))
          {
            if ((masterFieldInfo.getDsId().equals(rfnode.getWriteDs())) && (masterFieldInfo.getFieldId().equals(writeFields[0]))) {
              String detailRefNodeid = refNodeRel.getDetailRefNode();
              RefNode relatedRefNode = (RefNode)parentPm.getView(widgetId).getViewModels().getRefNode(detailRefNodeid);
              if (relatedRefNode != null) {
                Dataset relatedWriteDs = parentPm.getView(widgetId).getViewModels().getDataset(relatedRefNode.getWriteDs());
                String relatedwriteFieldStr = relatedRefNode.getWriteFields();
                String[] relatedwriteFields = relatedwriteFieldStr.split(",");
                Row relatedRow = relatedWriteDs.getSelectedRow();
                if (relatedRow == null) break;
                for (int j = 0; j < relatedwriteFields.length; j++) {
                  relatedRow.setValue(relatedWriteDs.nameToIndex(relatedwriteFields[j]), "");
                }
                



                break;
              }
            }
          }
        }
      }
    }
  }
  



  protected void processDatasetOk(String widgetId, LfwWindow parentPm, RefNode rfnode, Row[] currRows)
  {
    String readFieldStr = rfnode.getReadFields();
    if ((readFieldStr == null) || (readFieldStr.length() == 0)) {
      ILfwRefModel rm = LfwRefUtil.getRefModel(rfnode);
      readFieldStr = rm.getPkFieldCode();
    }
    //leo begin:ren yuan ref error; this is system bug
    if(rfnode.getId().equals("CpDocDs_zybsqdstaffname_name_hrhi_0001Z710000000000ZK7"))
    {
    	readFieldStr="pk_psndoc,name";
    }
    //leo end 
    //Ares begin:@leo I meet it too,but I don't know how it work ,only I can do is truest you.
    if(rfnode.getId().equals("hrtasigncardhpk_psndoc_name_hrhi_0001Z710000000000ZO3")){
    	readFieldStr="pk_psndoc,name";
    }
    //Ares end
    String[] readFields = readFieldStr.split(",");
    






















    String writeFieldStr = rfnode.getWriteFields();
    
    String[] writeFields = writeFieldStr.split(",");
    
    Dataset wds = parentPm.getView(widgetId).getViewModels().getDataset(rfnode.getWriteDs());
    

    Row row = wds.getSelectedRow();
    if (row == null)
      row = wds.getFocusRow();
    Map<String, String> valueMap = new java.util.LinkedHashMap();
    for (int i = 0; i < writeFields.length; i++) {
      String value = "";
      for (int j = 0; j < currRows.length; j++) {
        Row currRow = currRows[j];
        if (currRow != null) {
          String readField = readFields[i];
          readField = readField.replaceAll("\\.", "_");
          int index = currentDs.nameToIndex(readField);
          if (index == -1) {
            if (currentDs.getFieldSet() != null) {
              Field[] fields = currentDs.getFieldSet().getFields();
              if (fields != null) {
                StringBuffer fs = new StringBuffer();
                for (int m = 0; m < fields.length; m++) {
                  if (readField.equals(fields[m].getField().replace(".", "_"))) {
                    index = m;
                  }
                  fs.append(fields[m].getId() + " || ");
                }
                LfwLogger.error("All Fields Id : " + fs.toString());
              }
            }
            if (index == -1) {
              throw new LfwRuntimeException(LfwResBundle.getInstance().getStrByID("bc", "AppRefOkCmd-000000") + readFields[i] + ":" + readField);
            }
          }
          if (currRow.getValue(index) != null)
          {
            if (j != currRows.length - 1) {
              value = value + currRow.getValue(index) + ",";
            } else
              value = value + currRow.getValue(index);
          }
        }
      }
      valueMap.put(writeFields[i], value);
    }
    Map<String, Object> paramMap = new HashMap();
    paramMap.put("type", "Dataset");
    paramMap.put("id", rfnode.getWriteDs());
    paramMap.put("writeFields", valueMap);
    



















    UifPlugoutCmd uifPluOutCmd = new UifPlugoutCmd("main", "refOkPlugout", paramMap);
    uifPluOutCmd.execute();
  }
  












  protected void processTextOk(String widgetId, LfwWindow parentPm, RefNode rfnode, Row[] currRows)
  {
    String owner = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("owner");
    


    ILfwRefModel rm = LfwRefUtil.getRefModel(rfnode);
    String rfStr = rfnode.getReadFields();
    String[] rfs = rfStr.split(",");
    String returnField = null;
    String pkField = rm.getPkFieldCode();
    String codeField = rm.getRefCodeField();
    String nameField = rm.getRefNameField();
    for (int i = 0; i < rfs.length; i++) {
      if ((!rfs[i].equals(pkField)) && (
        (rfs[i].equals(codeField)) || (rfs[i].equals(nameField)))) {
        returnField = rfs[i];
        break;
      }
    }
    
    if (returnField == null) {
      returnField = nameField;
    }
    Map<String, Object> paramMap = new HashMap();
    paramMap.put("type", "Text");
    paramMap.put("id", owner);
    


    if (currRows != null) {
      String value = "";
      String showValue = "";
      if (pkField.indexOf(".") != -1) {
        pkField = pkField.split("\\.")[1].replaceAll("\\.", "_");
      }
      if (returnField.indexOf(".") != -1) {
        returnField = returnField.split("\\.")[1].replaceAll("\\.", "_");
      }
      for (int i = 0; i < currRows.length; i++) {
        Row currRow = currRows[i];
        if (currRow != null)
        {
          if (i != currRows.length - 1) {
            value = value + (String)currRow.getValue(currentDs.getFieldSet().nameToIndex(pkField)) + ",";
            showValue = showValue + (String)currRow.getValue(currentDs.getFieldSet().nameToIndex(returnField)) + ",";

          }
          else
          {

            value = value + (String)currRow.getValue(currentDs.getFieldSet().nameToIndex(pkField));
            
            showValue = showValue + (String)currRow.getValue(currentDs.getFieldSet().nameToIndex(returnField));
          }
        }
      }
      

      paramMap.put("key", value);
      paramMap.put("value", showValue);
    }
    





    UifPlugoutCmd uifPluOutCmd = new UifPlugoutCmd("main", "refOkPlugout", paramMap);
    uifPluOutCmd.execute();
  }
  
  protected void refClose() {}
}

/* Location:           C:\ncProjects\6.5\COCO_SZ\nchome\home20170910\modules\webrt\lib\pubwebrt_refLevel-1.jar
 * Qualified Name:     nc.uap.lfw.reference.app.AppRefOkCmd
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */