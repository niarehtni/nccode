/*      */ package nc.uap.lfw.reference.app;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;

import nc.bs.hrss.pub.tool.SessionUtil;
/*      */ import nc.uap.lfw.core.LfwRuntimeEnvironment;
/*      */ import nc.uap.lfw.core.WebContext;
/*      */ import nc.uap.lfw.core.WebSession;
/*      */ import nc.uap.lfw.core.base.ExtAttribute;
/*      */ import nc.uap.lfw.core.cmd.base.UifCommand;
/*      */ import nc.uap.lfw.core.common.DatasetConstant;
/*      */ import nc.uap.lfw.core.comp.FormComp;
/*      */ import nc.uap.lfw.core.comp.FormElement;
/*      */ import nc.uap.lfw.core.comp.GridColumn;
/*      */ import nc.uap.lfw.core.comp.GridComp;
/*      */ import nc.uap.lfw.core.comp.ReferenceComp;
/*      */ import nc.uap.lfw.core.comp.text.TextComp;
/*      */ import nc.uap.lfw.core.ctx.AppLifeCycleContext;
/*      */ import nc.uap.lfw.core.ctx.ApplicationContext;
/*      */ import nc.uap.lfw.core.ctx.ViewContext;
/*      */ import nc.uap.lfw.core.ctx.WindowContext;
/*      */ import nc.uap.lfw.core.data.Dataset;
/*      */ import nc.uap.lfw.core.data.DatasetRelation;
/*      */ import nc.uap.lfw.core.data.DatasetRelations;
/*      */ import nc.uap.lfw.core.data.FieldSet;
/*      */ import nc.uap.lfw.core.data.PaginationInfo;
/*      */ import nc.uap.lfw.core.data.Parameter;
/*      */ import nc.uap.lfw.core.data.ParameterSet;
/*      */ import nc.uap.lfw.core.data.Row;
/*      */ import nc.uap.lfw.core.data.RowData;
/*      */ import nc.uap.lfw.core.data.RowSet;
/*      */ import nc.uap.lfw.core.event.DataLoadEvent;
/*      */ import nc.uap.lfw.core.event.DatasetEvent;
/*      */ import nc.uap.lfw.core.event.DialogEvent;
/*      */ import nc.uap.lfw.core.event.GridRowEvent;
/*      */ import nc.uap.lfw.core.event.MouseEvent;
/*      */ import nc.uap.lfw.core.event.PageEvent;
/*      */ import nc.uap.lfw.core.event.ScriptEvent;
/*      */ import nc.uap.lfw.core.event.TextEvent;
/*      */ import nc.uap.lfw.core.event.TreeNodeEvent;
/*      */ import nc.uap.lfw.core.exception.LfwRuntimeException;
/*      */ import nc.uap.lfw.core.page.LfwView;
/*      */ import nc.uap.lfw.core.page.LfwWindow;
/*      */ import nc.uap.lfw.core.page.ViewComponents;
/*      */ import nc.uap.lfw.core.page.ViewModels;
/*      */ import nc.uap.lfw.core.refnode.IRefNode;
/*      */ import nc.uap.lfw.core.refnode.MasterFieldInfo;
/*      */ import nc.uap.lfw.core.refnode.NCRefNode;
/*      */ import nc.uap.lfw.core.refnode.RefNode;
/*      */ import nc.uap.lfw.core.refnode.RefNodeRelation;
/*      */ import nc.uap.lfw.core.refnode.RefNodeRelations;
/*      */ import nc.uap.lfw.core.serializer.impl.List2DatasetSerializer;
/*      */ import nc.uap.lfw.login.vo.LfwSessionBean;
/*      */ import nc.uap.lfw.reference.ILfwGridTreeRefModel;
/*      */ import nc.uap.lfw.reference.ILfwRefModel;
/*      */ import nc.uap.lfw.reference.ILfwTreeRefModel;
/*      */ import nc.uap.lfw.reference.ISqlBasedRefModel;
/*      */ import nc.uap.lfw.reference.RefResult;
/*      */ import nc.uap.lfw.reference.base.AbstractLfwRefListItem;
/*      */ import nc.uap.lfw.reference.base.DefaultLfwRefListFilter;
/*      */ import nc.uap.lfw.reference.util.LfwRefUtil;
/*      */ import nc.uap.lfw.util.LfwClassUtil;
import uap.lfw.ref.filter.LfwAbstractFilter;
/*      */ 
/*      */ public class AppReferenceController
/*      */ {
/*      */   public static final String REFORG = "filterRefComp_0";
/*   68 */   public static String QUERY = "query";
/*      */   private static final String RELATION_WHERE_SQL = "relationWhereSql";
/*      */   private static final String RELATION_SET_METHOD_NAME = "relationSetMethodName";
/*      */   private static final String RELATION_SET_METHOD_VALUE = "relationSetMethodValue";
/*      */   
/*      */   protected void dealRefNodeRelations(LfwView view, RefNode refNode, ILfwRefModel refModel)
/*      */   {
/*   75 */     if ((view == null) || (refNode == null)) {
/*   76 */       return;
/*      */     }
/*      */     
/*   79 */     RefNodeRelations relObj = view.getViewModels().getRefNodeRelations();
/*   80 */     if (relObj == null) {
/*   81 */       return;
/*      */     }
/*      */     
/*   84 */     RefNodeRelation[] rels = relObj.getRefnodeRelations();
/*   85 */     int len = rels != null ? rels.length : 0;
/*   86 */     if (len == 0) {
/*   87 */       return;
/*      */     }
/*      */     
/*   90 */     for (int i = 0; i < len; i++) {
/*   91 */       RefNodeRelation rel = rels[i];
/*   92 */       if ((rel != null) && (rel.getDetailRefNode() != null))
/*      */       {
/*      */ 
/*   95 */         if (rel.getDetailRefNode().equals(refNode.getId()))
/*      */         {
/*      */ 
/*      */ 
/*   99 */           List<MasterFieldInfo> mfieldinfos = rel.getMasterFieldInfos();
/*  100 */           int size = mfieldinfos != null ? mfieldinfos.size() : 0;
/*  101 */           if (size != 0)
/*      */           {
/*      */ 
/*      */ 
/*  105 */             for (int j = 0; j < size; j++) {
/*  106 */               MasterFieldInfo mfieldinfo = (MasterFieldInfo)mfieldinfos.get(j);
/*  107 */               if (mfieldinfo != null)
/*      */               {
/*      */ 
/*      */ 
/*  111 */                 Object value = null;
/*  112 */                 if ((mfieldinfo.getDsId() != null) && (mfieldinfo.getFieldId() != null)) {
/*  113 */                   if (!mfieldinfo.getDsId().equals(refNode.getWriteDs())) {
/*      */                     continue;
/*      */                   }
/*      */                   
/*  117 */                   Dataset ds = view.getViewModels().getDataset(mfieldinfo.getDsId());
/*  118 */                   if (ds == null) {
/*      */                     continue;
/*      */                   }
/*      */                   
/*  122 */                   int index = ds.nameToIndex(mfieldinfo.getFieldId());
/*  123 */                   if (index < 0) {
/*      */                     continue;
/*      */                   }
/*      */                   
/*  127 */                   value = ds.getValue(index);
/*  128 */                 } else if (mfieldinfo.getRefTextCompId() != null) {
/*  129 */                   nc.uap.lfw.core.comp.WebComponent comp = view.getViewComponents().getComponent(mfieldinfo.getRefTextCompId());
/*  130 */                   if ((comp instanceof ReferenceComp)) {
/*  131 */                     value = ((ReferenceComp)comp).getValue();
/*      */                   }
/*      */                 }
/*  134 */                 if (value == null) {
/*  135 */                   value = "";
/*      */                 }
/*  137 */                 String filterStr = mfieldinfo.getSetMethodName();
/*  138 */                 if ((filterStr != null) && (filterStr.trim().length() > 0)) {
/*  139 */                   ((ISqlBasedRefModel)refModel).setMethodValue(filterStr, new Class[] { String.class }, value);
/*      */                 } else {
/*  141 */                   filterStr = mfieldinfo.getFilterSql();
/*  142 */                   if ((mfieldinfo.getFilterSql() != null) && (filterStr.trim().length() > 0))
/*  143 */                     refModel.addWherePart(filterStr.replace("?", "'" + value + "'"));
/*      */                 }
/*      */               }
/*      */             } }
/*      */         } }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void dealRefNodeRelations(Dataset ds, ILfwRefModel refModel, boolean isAddClassWherePart) {
/*  152 */     Parameter whereSqlParam = ds.getReqParameter("relationWhereSql");
/*  153 */     Parameter setMNParam = ds.getReqParameter("relationSetMethodName");
/*  154 */     Parameter setValueParam = ds.getReqParameter("relationSetMethodValue");
/*  155 */     if ((whereSqlParam != null) && (whereSqlParam.getValue() != null) && (whereSqlParam.getValue().trim().length() > 0)) {
/*  156 */       String relationWhereSql = whereSqlParam.getValue();
/*  157 */       if ((isAddClassWherePart) && ((refModel instanceof ILfwTreeRefModel))) {
/*  158 */         ((ILfwTreeRefModel)refModel).addClassWherePart(relationWhereSql);
/*      */       } else {
/*  160 */         refModel.addWherePart(relationWhereSql);
/*      */       }
/*  162 */     } else if ((setMNParam != null) && (setMNParam.getValue() != null) && (setMNParam.getValue().trim().length() > 0)) {
/*  163 */       String[] setMNNames = setMNParam.getValue().split(",");
/*  164 */       String[] setValues = setValueParam.getValue().split(",");
/*  165 */       int len = setMNNames.length;
/*  166 */       for (int i = 0; i < len; i++) {
/*  167 */         ((ISqlBasedRefModel)refModel).setMethodValue(setMNNames[i], new Class[] { String.class }, setValues[i]);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void onDataLoad(DataLoadEvent e)
/*      */   {
/*  174 */     String widgetId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("widgetId");
/*  175 */     String refNodeId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("refNodeId");
/*  176 */     LfwWindow parentPm = LfwRuntimeEnvironment.getWebContext().getParentPageMeta();
/*  177 */     RefNode rfnode = (RefNode)parentPm.getView(widgetId).getViewModels().getRefNode(refNodeId);
/*  178 */     ILfwRefModel refModel = LfwRefUtil.getRefModel(rfnode);
/*      */     
/*  180 */     String filterValue = getFilterValue();
/*      */     
/*  182 */     Dataset ds = (Dataset)e.getSource();
/*  183 */     if ("masterDs".equals(ds.getId())) {
/*  184 */       ViewContext widgetCtx = AppLifeCycleContext.current().getViewContext();
/*  185 */       LfwView widget = widgetCtx.getView();
/*      */       
/*      */ 
/*  188 */       String pk_org = null;
/*  189 */       if ((rfnode instanceof NCRefNode)) {
/*  190 */         boolean orgs = (((NCRefNode)rfnode).isOrgs()) || (((NCRefNode)rfnode).isHrOrgs());
/*  191 */         if (orgs) {
/*  192 */           ReferenceComp reftext = (ReferenceComp)widget.getViewComponents().getComponent("filterRefComp_0");
/*  193 */           if (reftext != null) {
/*  194 */             pk_org = reftext.getValue();
/*      */           }
/*      */         }
/*      */       }
/*  198 */       if ((null != pk_org) && (!"".equals(pk_org))) {
/*  199 */         refModel.setPk_org(pk_org);
/*      */       }
/*      */      
				//leo begin: add 2017-10-30 {filter ref zybsqd_staffname_name}
				if(rfnode.getId().contains("staffname_name")&& rfnode.getId().contains("zybsqd"))
				{
					//refModel.addWherePart("bd_psndoc.pk_org='"+SessionUtil.getPk_org()+"'");
					refModel.addWherePart("bd_psndoc.pk_org=(select pk_org from org_dept where principal='"+SessionUtil.getPk_psndoc()+"')");
				}
				
				//leo end
				
/*  202 */       processNCRefModel(ds, rfnode, filterValue, refModel);
/*      */       
/*  204 */       int refType = LfwRefUtil.getRefType(refModel);
/*      */       
/*  206 */       if (refType != 2)
/*      */       {
/*  208 */         dealRefNodeRelations(ds, refModel, false);
/*      */       }
/*      */       else {
/*  211 */         processTreeSelWherePart(ds, rfnode, refModel);
/*      */       }
/*      */       
/*  214 */       Row selectedRow = null;
/*      */       
/*  216 */       String keyValue = null;
/*  217 */       DatasetRelations dsRels = widget.getViewModels().getDsrelations();
/*  218 */       if (dsRels != null) {
/*  219 */         String parentDsId = dsRels.getMasterDsByDetailDs(ds.getId());
/*  220 */         if (parentDsId != null) {
/*  221 */           Dataset parentDs = widget.getViewModels().getDataset(parentDsId);
/*  222 */           selectedRow = parentDs.getSelectedRow();
/*  223 */           if (selectedRow != null) {
/*  224 */             DatasetRelation dr = dsRels.getDsRelation(parentDsId, ds.getId());
/*  225 */             String masterKey = dr.getMasterKeyField();
/*  226 */             keyValue = (String)selectedRow.getValue(parentDs.getFieldSet().nameToIndex(masterKey));
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  231 */       if ((refModel instanceof ILfwGridTreeRefModel))
/*      */       {
/*  233 */         ILfwGridTreeRefModel treeRefModel = (ILfwGridTreeRefModel)refModel;
/*  234 */         if (keyValue != null) {
/*  235 */           treeRefModel.setClassJoinValue(keyValue);
/*      */         } else {
/*  237 */           treeRefModel.setClassJoinValue(QUERY);
/*      */         }
/*      */       }
/*      */       
/*  241 */       List<List<Object>> v = null;
/*  242 */       PaginationInfo pInfo = ds.getCurrentRowSet().getPaginationInfo();
/*  243 */       if ((filterValue == null) || (filterValue.equals(""))) {
/*  244 */         if (selectedRow != null) {
/*  245 */           if (keyValue == null) {
/*  246 */             keyValue = selectedRow.getRowId();
/*      */           }
/*  248 */           ds.clear();
/*  249 */           ds.setCurrentKey(keyValue);
/*  250 */           RowSet rowset = ds.getRowSet(keyValue, true);
/*  251 */           PaginationInfo pinfo = rowset.getPaginationInfo();
/*  252 */           if (pinfo.getPageIndex() == -1) {
/*  253 */             pinfo.setPageIndex(0);
/*      */           }
/*      */         }
/*      */         
/*  257 */         RefResult rd = null;
/*  258 */         if (pInfo.getPageSize() != -1) {
/*  259 */           int pageIndex = pInfo.getPageIndex();
/*  260 */           rd = refModel.getRefData(pageIndex);
/*      */         }
/*      */         else {
/*  263 */           rd = refModel.getRefData(0);
/*      */         }
/*  265 */         if (rd != null) {
/*  266 */           pInfo.setRecordsCount(rd.getTotalCount());
/*  267 */           v = rd.getData();
/*      */         }
/*      */       }
/*      */       else {
/*  271 */         RefResult rd = refModel.getFilterRefData(filterValue, pInfo.getPageIndex());
/*  272 */         if (rd != null) {
/*  273 */           pInfo.setRecordsCount(rd.getTotalCount());
/*  274 */           v = rd.getData();
/*      */         }
/*      */       }
/*  277 */       new List2DatasetSerializer().serialize(ds.getCurrentKey(), pInfo, v, ds);
/*      */     }
/*  279 */     else if ("masterDs_class".equals(ds.getId())) {
/*  280 */       if ((refModel instanceof ILfwTreeRefModel)) {
/*  281 */         ILfwTreeRefModel treeRefModel = (ILfwTreeRefModel)refModel;
/*  282 */         ViewContext widgetCtx = AppLifeCycleContext.current().getViewContext();
/*  283 */         LfwView widget = widgetCtx.getView();
/*  284 */         List<List<Object>> v = null;
/*  285 */         PaginationInfo pInfo = ds.getCurrentRowSet().getPaginationInfo();
/*      */         
/*      */ 
/*  288 */         String pk_org = null;
/*  289 */         if ((rfnode instanceof NCRefNode)) {
/*  290 */           boolean orgs = (((NCRefNode)rfnode).isOrgs()) || (((NCRefNode)rfnode).isHrOrgs());
/*  291 */           if (orgs) {
/*  292 */             ReferenceComp reftext = (ReferenceComp)widget.getViewComponents().getComponent("filterRefComp_0");
/*  293 */             if (reftext != null) {
/*  294 */               pk_org = reftext.getValue();
/*      */             }
/*      */           }
/*      */         }
/*  298 */         if ((null != pk_org) && (!"".equals(pk_org))) {
/*  299 */           refModel.setPk_org(pk_org);
/*      */         }
/*      */         
/*  302 */         processNCRefModel(ds, rfnode, filterValue, refModel);
/*      */         
/*  304 */         int refType = LfwRefUtil.getRefType(refModel);
/*      */         
/*  306 */         if (refType != 2)
/*      */         {
/*  308 */           dealRefNodeRelations(ds, refModel, true);
/*      */         }
/*      */         else {
/*  311 */           processTreeSelWherePart(ds, rfnode, refModel);
/*      */         }
/*      */         
/*      */ 
/*  315 */         filterValue = null;
/*      */         
/*  317 */         if ((filterValue == null) || (filterValue.equals(""))) {
/*  318 */           DatasetRelations dsRels = widget.getViewModels().getDsrelations();
/*  319 */           if (dsRels != null) {
/*  320 */             String parentDsId = dsRels.getMasterDsByDetailDs(ds.getId());
/*  321 */             if (parentDsId != null) {
/*  322 */               Dataset parentDs = widget.getViewModels().getDataset(parentDsId);
/*  323 */               Row row = parentDs.getSelectedRow();
/*  324 */               if (row != null) {
/*  325 */                 DatasetRelation dr = dsRels.getDsRelation(parentDsId, ds.getId());
/*  326 */                 String masterKey = dr.getMasterKeyField();
/*  327 */                 String keyValue = (String)row.getValue(parentDs.getFieldSet().nameToIndex(masterKey));
/*      */                 
/*  329 */                 if (keyValue == null) {
/*  330 */                   keyValue = row.getRowId();
/*      */                 }
/*  332 */                 ds.clear();
/*  333 */                 ds.setCurrentKey(keyValue);
/*  334 */                 RowSet rowset = ds.getRowSet(keyValue, true);
/*  335 */                 PaginationInfo pinfo = rowset.getPaginationInfo();
/*  336 */                 if (pinfo.getPageIndex() == -1) {
/*  337 */                   pinfo.setPageIndex(0);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  343 */           RefResult rd = null;
/*  344 */           if (pInfo.getPageSize() != -1) {
/*  345 */             int pageIndex = pInfo.getPageIndex();
/*  346 */             rd = treeRefModel.getClassRefData(pageIndex);
/*      */           }
/*      */           else {
/*  349 */             rd = treeRefModel.getClassRefData(0);
/*      */           }
/*  351 */           if (rd != null) {
/*  352 */             pInfo.setRecordsCount(rd.getTotalCount());
/*  353 */             v = rd.getData();
/*      */           }
/*      */         }
/*  356 */         new List2DatasetSerializer().serialize(ds.getCurrentKey(), pInfo, v, ds);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*  374 */     else if (ds.getId().equals("rightGridDs")) {
/*  375 */       Dataset wds = parentPm.getView(widgetId).getViewModels().getDataset(rfnode.getWriteDs());
/*  376 */       Row row = wds.getFocusRow();
/*  377 */       if (row == null) {
/*  378 */         row = wds.getSelectedRow();
/*      */       }
/*  380 */       String writeFieldStr = rfnode.getWriteFields();
/*  381 */       String[] writeFields = writeFieldStr.split(",");
/*  382 */       String valuePK = (String)row.getValue(wds.nameToIndex(writeFields[0]));
/*  383 */       if (valuePK == null) {
/*  384 */         return;
/*      */       }
/*  386 */       String[] valuePKs = valuePK.split(",");
/*  387 */       String valueName = (String)row.getValue(wds.nameToIndex(writeFields[1]));
/*  388 */       String[] valueNames = valueName.split(",");
/*  389 */       for (int i = 0; i < valuePKs.length; i++) {
/*  390 */         String valPk = valuePKs[i];
/*  391 */         Row newRow = ds.getEmptyRow();
/*  392 */         ds.addRow(newRow);
/*  393 */         newRow.setValue(0, valPk);
/*  394 */         String valName = valueNames[i];
/*  395 */         newRow.setValue(1, valName);
/*      */       }
/*      */     }
/*      */     else {
/*  399 */       ILfwGridTreeRefModel tgModel = (ILfwGridTreeRefModel)refModel;
/*      */       
/*  401 */       String keys = ds.getReqParameters().getParameterValue(DatasetConstant.QUERY_PARAM_KEYS);
/*  402 */       if ((keys != null) && (!keys.equals(""))) {
/*  403 */         String values = ds.getReqParameters().getParameterValue(DatasetConstant.QUERY_PARAM_VALUES);
/*  404 */         String wherePart = keys + " = '" + values + "'";
/*  405 */         tgModel.setClassWherePart(wherePart);
/*      */       }
/*      */       
/*  408 */       processNCRefModel(ds, rfnode, filterValue, refModel);
/*      */       
/*  410 */       List<List<Object>> v = tgModel.getClassData();
/*  411 */       new List2DatasetSerializer().serialize(ds.getCurrentKey(), null, v, ds);
/*      */       
/*  413 */       if ((v != null) && (v.size() > 0)) {
/*  414 */         ds.setRowSelectIndex(Integer.valueOf(0));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void searchAllData(MouseEvent e)
/*      */   {
/*  426 */     String widgetId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("widgetId");
/*  427 */     String refNodeId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("refNodeId");
/*  428 */     LfwWindow parentPm = LfwRuntimeEnvironment.getWebContext().getParentPageMeta();
/*  429 */     RefNode rfnode = (RefNode)parentPm.getView(widgetId).getViewModels().getRefNode(refNodeId);
/*  430 */     ILfwRefModel refModel = LfwRefUtil.getRefModel(rfnode);
/*  431 */     ViewContext widgetCtx = AppLifeCycleContext.current().getViewContext();
/*  432 */     LfwView widget = widgetCtx.getView();
/*  433 */     Dataset ds = widget.getViewModels().getDataset("masterDs");
/*  434 */     ds.clear();
/*  435 */     ds.setCurrentKey("MASTER_KEY");
/*  436 */     String filterValue = getFilterValue();
/*  437 */     List<List<Object>> v = null;
/*  438 */     PaginationInfo pInfo = ds.getCurrentRowSet().getPaginationInfo();
/*      */     
/*      */ 
/*  441 */     String pk_org = null;
/*  442 */     if ((rfnode instanceof NCRefNode)) {
/*  443 */       boolean orgs = (((NCRefNode)rfnode).isOrgs()) || (((NCRefNode)rfnode).isHrOrgs());
/*  444 */       if (orgs) {
/*  445 */         ReferenceComp reftext = (ReferenceComp)widget.getViewComponents().getComponent("filterRefComp_0");
/*  446 */         if (reftext != null) {
/*  447 */           pk_org = reftext.getValue();
/*      */         }
/*      */       }
/*      */     }
/*  451 */     if ((null != pk_org) && (!"".equals(pk_org))) {
/*  452 */       refModel.setPk_org(pk_org);
/*      */     }
/*      */     
/*  455 */     if ((refModel instanceof ILfwGridTreeRefModel))
/*      */     {
/*  457 */       ((ILfwGridTreeRefModel)refModel).setClassJoinValue(QUERY);
/*      */     }
/*      */     
/*  460 */     processNCRefModel(ds, rfnode, filterValue, refModel);
/*      */     
/*  462 */     processTreeSelWherePart(ds, rfnode, refModel);
/*      */     
/*  464 */     String pk_group = LfwRuntimeEnvironment.getLfwSessionBean().getPk_unit();
/*  465 */     refModel.setPk_group(pk_group);
/*  466 */     if ((filterValue == null) || (filterValue.equals(""))) {
/*  467 */       DatasetRelations dsRels = widget.getViewModels().getDsrelations();
/*  468 */       if (dsRels != null) {
/*  469 */         String parentDsId = dsRels.getMasterDsByDetailDs(ds.getId());
/*  470 */         if (parentDsId != null) {
/*  471 */           Dataset parentDs = widget.getViewModels().getDataset(parentDsId);
/*  472 */           Row row = parentDs.getSelectedRow();
/*  473 */           if (row != null) {
/*  474 */             DatasetRelation dr = dsRels.getDsRelation(parentDsId, ds.getId());
/*  475 */             String masterKey = dr.getMasterKeyField();
/*  476 */             String keyValue = (String)row.getValue(parentDs.getFieldSet().nameToIndex(masterKey));
/*      */             
/*  478 */             if (keyValue == null) {
/*  479 */               keyValue = row.getRowId();
/*      */             }
/*  481 */             ds.clear();
/*  482 */             ds.setCurrentKey(keyValue);
/*  483 */             RowSet rowset = ds.getRowSet(keyValue, true);
/*  484 */             PaginationInfo pinfo = rowset.getPaginationInfo();
/*  485 */             if (pinfo.getPageIndex() == -1) {
/*  486 */               pinfo.setPageIndex(0);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  492 */       RefResult rd = null;
/*  493 */       if (pInfo.getPageSize() != -1) {
/*  494 */         int pageIndex = pInfo.getPageIndex();
/*  495 */         rd = refModel.getRefData(pageIndex);
/*      */       }
/*      */       else {
/*  498 */         rd = refModel.getRefData(0);
/*      */       }
/*  500 */       if (rd != null) {
/*  501 */         pInfo.setRecordsCount(rd.getTotalCount());
/*  502 */         v = rd.getData();
/*      */       }
/*      */     }
/*      */     else {
/*  506 */       RefResult rd = refModel.getFilterRefData(filterValue, pInfo.getPageIndex());
/*  507 */       if (rd != null) {
/*  508 */         pInfo.setRecordsCount(rd.getTotalCount());
/*  509 */         v = rd.getData();
/*      */       }
/*      */     }
/*  512 */     new List2DatasetSerializer().serialize(ds.getCurrentKey(), pInfo, v, ds);
/*      */     
/*  514 */     Dataset treeDs = widget.getViewModels().getDataset("masterDs_tree");
/*  515 */     treeDs.setRowUnSelect();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void processSelfWherePart(Dataset ds, RefNode refnode, String filterValue, ILfwRefModel refModel)
/*      */   {
/*  522 */     LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
/*  523 */     ReferenceComp reference = (ReferenceComp)widget.getViewComponents().getComponent("filterRefComp_0");
/*  524 */     if (reference != null) {
/*  525 */       String pk_org = reference.getValue();
/*  526 */       if ((pk_org != null) && (!"".equals(pk_org))) {
/*  527 */         refModel.setPk_org(pk_org);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void processNCRefModel(Dataset ds, RefNode refnode, String filterValue, ILfwRefModel refModel)
/*      */   {
/*  540 */     processSelfWherePart(ds, refnode, filterValue, refModel);
/*  541 */     if (refnode != null) {
/*  542 */       ExtAttribute attr = refnode.getExtendAttribute("$LfwRefFilterClass");
/*  543 */       if (attr != null) {
/*  544 */         if ((attr.getValue() instanceof String)) {
/*  545 */           Object o = LfwClassUtil.newInstance((String)attr.getValue());
/*  546 */           if ((o instanceof LfwAbstractFilter)) {
/*  547 */             ((LfwAbstractFilter)o).processNCRefModel(refModel, refnode);
/*      */           }
/*  549 */         } else if ((attr.getValue() instanceof LfwAbstractFilter)) {
/*  550 */           ((LfwAbstractFilter)attr.getValue()).processNCRefModel(refModel, refnode);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void loadTreeGridDatas(Row curRow) {
/*  557 */     ViewContext widgetCtx = AppLifeCycleContext.current().getWindowContext().getCurrentViewContext();
/*  558 */     LfwView widget = widgetCtx.getView();
/*  559 */     Dataset masterDs = widget.getViewModels().getDataset("masterDs_tree");
/*  560 */     Row masterSelecteRow = curRow;
/*  561 */     DatasetRelations dsRels = widget.getViewModels().getDsrelations();
/*  562 */     if (dsRels != null) {
/*  563 */       DatasetRelation[] masterRels = dsRels.getDsRelations(masterDs.getId());
/*  564 */       for (int i = 0; i < masterRels.length; i++) {
/*  565 */         DatasetRelation dr = masterRels[i];
/*  566 */         Dataset detailDs = widget.getViewModels().getDataset(dr.getDetailDataset());
/*  567 */         String masterKey = dr.getMasterKeyField();
/*      */         
/*  569 */         if (masterSelecteRow != null) {
/*  570 */           String keyValue = (String)masterSelecteRow.getValue(masterDs.getFieldSet().nameToIndex(masterKey));
/*      */           
/*  572 */           if (keyValue == null) {
/*  573 */             keyValue = masterSelecteRow.getRowId();
/*      */           }
/*  575 */           detailDs.setCurrentKey(keyValue);
/*  576 */           RowSet rowset = detailDs.getRowSet(keyValue, true);
/*  577 */           PaginationInfo pinfo = rowset.getPaginationInfo();
/*  578 */           if ((pinfo == null) || (pinfo.getPageCount() == -1)) {
/*  579 */             pinfo.setPageIndex(0);
/*      */           }
/*  581 */           String widgetId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("widgetId");
/*  582 */           String refNodeId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("refNodeId");
/*  583 */           LfwWindow parentPm = LfwRuntimeEnvironment.getWebContext().getParentPageMeta();
/*  584 */           RefNode rfnode = (RefNode)parentPm.getView(widgetId).getViewModels().getRefNode(refNodeId);
/*  585 */           ILfwRefModel baseRefModel = LfwRefUtil.getRefModel(rfnode);
/*  586 */           String pk_org = null;
/*  587 */           if ((rfnode instanceof NCRefNode)) {
/*  588 */             boolean orgs = (((NCRefNode)rfnode).isOrgs()) || (((NCRefNode)rfnode).isHrOrgs());
/*  589 */             if (orgs) {
/*  590 */               ReferenceComp reftext = (ReferenceComp)widget.getViewComponents().getComponent("filterRefComp_0");
/*  591 */               if (reftext != null) {
/*  592 */                 pk_org = reftext.getValue();
/*      */               }
/*      */             }
/*      */           }
/*  596 */           if ((baseRefModel instanceof ILfwGridTreeRefModel)) {
/*  597 */             ILfwGridTreeRefModel refModel = (ILfwGridTreeRefModel)baseRefModel;
/*  598 */             if ((pk_org != null) && (!pk_org.equals(""))) {
/*  599 */               refModel.setPk_org(pk_org);
/*      */             }
/*  601 */             processTreeSelWherePart(masterDs, rfnode, refModel);
/*      */             
/*  603 */             if (dr.getId().equals("master_slave_rel1")) {
/*  604 */               refModel.setClassJoinValue(keyValue);
/*      */               
/*  606 */               RefResult rr = refModel.getRefData(0);
/*  607 */               List<List<Object>> v = rr.getData();
/*  608 */               pinfo.setRecordsCount(rr.getTotalCount());
/*  609 */               new List2DatasetSerializer().serialize(keyValue, pinfo, v, detailDs);
/*      */             }
/*      */             else {
/*  612 */               refModel.setClassJoinValue(keyValue);
/*  613 */               RefResult rr = refModel.getRefData(0);
/*  614 */               List<List<Object>> v = rr.getData();
/*  615 */               pinfo.setRecordsCount(rr.getTotalCount());
/*  616 */               pinfo.setPageIndex(0);
/*  617 */               detailDs.clear();
/*  618 */               detailDs.setCurrentKey(keyValue);
/*  619 */               new List2DatasetSerializer().serialize(keyValue, pinfo, v, detailDs);
/*      */             }
/*      */           }
/*      */         } else {
/*  623 */           new List2DatasetSerializer().serialize(null, null, new ArrayList(), detailDs);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void onAfterRowSelect(DatasetEvent se) {
/*  630 */     Dataset masterDs = (Dataset)se.getSource();
/*  631 */     Row masterSelecteRow = masterDs.getSelectedRow();
/*  632 */     ViewContext widgetCtx = AppLifeCycleContext.current().getWindowContext().getCurrentViewContext();
/*  633 */     LfwView widget = widgetCtx.getView();
/*  634 */     DatasetRelations dsRels = widget.getViewModels().getDsrelations();
/*  635 */     if (dsRels != null) {
/*  636 */       DatasetRelation[] masterRels = dsRels.getDsRelations(masterDs.getId());
/*  637 */       for (int i = 0; i < masterRels.length; i++) {
/*  638 */         DatasetRelation dr = masterRels[i];
/*  639 */         Dataset detailDs = widget.getViewModels().getDataset(dr.getDetailDataset());
/*  640 */         String masterKey = dr.getMasterKeyField();
/*      */         
/*  642 */         if (masterSelecteRow != null) {
/*  643 */           String keyValue = (String)masterSelecteRow.getValue(masterDs.getFieldSet().nameToIndex(masterKey));
/*  644 */           if (keyValue == null) {
/*  645 */             keyValue = masterSelecteRow.getRowId();
/*      */           }
/*  647 */           detailDs.setCurrentKey(keyValue);
/*  648 */           RowSet rowset = detailDs.getRowSet(keyValue, true);
/*  649 */           PaginationInfo pinfo = rowset.getPaginationInfo();
/*  650 */           if ((pinfo == null) || (pinfo.getPageCount() == -1)) {
/*  651 */             pinfo.setPageIndex(0);
/*      */           }
/*  653 */           String widgetId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("widgetId");
/*  654 */           String refNodeId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("refNodeId");
/*  655 */           LfwWindow parentPm = LfwRuntimeEnvironment.getWebContext().getParentPageMeta();
/*  656 */           RefNode rfnode = (RefNode)parentPm.getView(widgetId).getViewModels().getRefNode(refNodeId);
/*  657 */           ILfwRefModel baseRefModel = LfwRefUtil.getRefModel(rfnode);
/*  658 */           String pk_org = null;
/*      */           
/*      */ 
/*      */ 
/*  662 */           ReferenceComp reftext = (ReferenceComp)widget.getViewComponents().getComponent("filterRefComp_0");
/*  663 */           if (reftext != null) {
/*  664 */             pk_org = reftext.getValue();
/*      */           }
/*      */           
/*      */ 
/*  668 */           if ((baseRefModel instanceof ILfwGridTreeRefModel)) {
/*  669 */             ILfwGridTreeRefModel refModel = (ILfwGridTreeRefModel)baseRefModel;
/*  670 */             if ((pk_org != null) && (!pk_org.equals(""))) {
/*  671 */               refModel.setPk_org(pk_org);
/*      */             }
/*  673 */             processTreeSelWherePart(masterDs, rfnode, refModel);
/*      */             
/*  675 */             if (dr.getId().equals("master_slave_rel1")) {
/*  676 */               refModel.setClassJoinValue(keyValue);
/*      */               
/*      */ 
/*  679 */               RefResult rr = refModel.getRefData(0);
/*  680 */               List<List<Object>> v = rr.getData();
/*  681 */               pinfo.setRecordsCount(rr.getTotalCount());
/*  682 */               new List2DatasetSerializer().serialize(keyValue, pinfo, v, detailDs);
/*      */             }
/*      */             else {
/*  685 */               refModel.setClassJoinValue(keyValue);
/*      */               
/*      */ 
/*      */ 
/*  689 */               RefResult rr = refModel.getRefData(0);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  695 */               List<List<Object>> v = rr.getData();
/*  696 */               pinfo.setRecordsCount(rr.getTotalCount());
/*  697 */               pinfo.setPageIndex(0);
/*  698 */               detailDs.clear();
/*  699 */               detailDs.setCurrentKey(keyValue);
/*  700 */               new List2DatasetSerializer().serialize(keyValue, pinfo, v, detailDs);
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  713 */           new List2DatasetSerializer().serialize(null, null, new ArrayList(), detailDs);
/*      */         }
/*      */       }
/*      */     }
/*  717 */     TextComp locateText = (TextComp)widget.getViewComponents().getComponent("locatetext");
/*  718 */     locateText.setValue("");
/*      */   }
/*      */   
/*      */   public void onRowDbClick(GridRowEvent e) {
/*  722 */     LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
/*  723 */     Dataset ds = widget.getViewModels().getDataset("masterDs");
/*  724 */     String refNodeId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("refNodeId");
/*  725 */     String widgetId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("widgetId");
/*  726 */     LfwWindow parentPm = LfwRuntimeEnvironment.getWebContext().getParentPageMeta();
/*  727 */     RefNode refnode = (RefNode)parentPm.getView(widgetId).getViewModels().getRefNode(refNodeId);
/*  728 */     UifCommand refOkCmd = null;
/*  729 */     if ((refnode != null) && (refnode.getRefnodeDelegator() != null) && (!refnode.getRefnodeDelegator().equals(""))) {
/*  730 */       refOkCmd = (UifCommand)LfwClassUtil.newInstance(refnode.getRefnodeDelegator(), new Class[] { Dataset.class }, new Object[] { ds });
/*      */     }
/*      */     else {
/*  733 */       refOkCmd = new AppRefOkCmd(ds);
/*      */     }
/*  735 */     refOkCmd.execute();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void refOkDelegator(MouseEvent e)
/*      */   {
/*  744 */     LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
/*  745 */     Dataset ds = widget.getViewModels().getDataset("masterDs");
/*  746 */     String refNodeId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("refNodeId");
/*  747 */     String widgetId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("widgetId");
/*  748 */     String parentPageId = LfwRuntimeEnvironment.getWebContext().getWebSession().getOriginalParameter("otherPageId");
/*  749 */     LfwWindow parentPm = null;
/*  750 */     if (AppLifeCycleContext.current().getApplicationContext().getWindowContext(parentPageId) != null) {
/*  751 */       parentPm = AppLifeCycleContext.current().getApplicationContext().getWindowContext(parentPageId).getWindow();
/*      */     } else {
/*  753 */       parentPm = uap.lfw.ref.util.LfwReferenceUtil.getParentWindow();
/*      */     }
/*      */     
/*  756 */     RefNode refnode = (RefNode)parentPm.getView(widgetId).getViewModels().getRefNode(refNodeId);
/*  757 */     UifCommand refOkCmd = null;
/*  758 */     if ((refnode != null) && (refnode.getRefnodeDelegator() != null) && (!refnode.getRefnodeDelegator().equals(""))) {
/*  759 */       refOkCmd = (UifCommand)LfwClassUtil.newInstance(refnode.getRefnodeDelegator(), new Class[] { Dataset.class }, new Object[] { ds });
/*      */     }
/*      */     else {
/*  762 */       refOkCmd = new AppRefOkCmd(ds);
/*      */     }
/*  764 */     refOkCmd.execute();
/*      */   }
/*      */   
/*      */   public void onTreeNodedbclick(TreeNodeEvent e) {
/*  768 */     Dataset ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset("masterDs");
/*  769 */     String refNodeId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("refNodeId");
/*  770 */     String widgetId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("widgetId");
/*  771 */     LfwWindow parentPm = LfwRuntimeEnvironment.getWebContext().getParentPageMeta();
/*  772 */     RefNode refnode = (RefNode)parentPm.getView(widgetId).getViewModels().getRefNode(refNodeId);
/*  773 */     UifCommand refOkCmd = null;
/*  774 */     if ((refnode != null) && (refnode.getRefnodeDelegator() != null) && (!refnode.getRefnodeDelegator().equals(""))) {
/*  775 */       refOkCmd = (UifCommand)LfwClassUtil.newInstance(refnode.getRefnodeDelegator(), new Class[] { Dataset.class }, new Object[] { ds });
/*      */     }
/*      */     else {
/*  778 */       refOkCmd = new AppRefOkCmd(ds);
/*      */     }
/*  780 */     refOkCmd.execute();
/*      */   }
/*      */   
/*      */   public void orgValueChanged(TextEvent e)
/*      */   {
/*  785 */     String widgetId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("widgetId");
/*  786 */     String refNodeId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("refNodeId");
/*  787 */     LfwWindow parentPm = LfwRuntimeEnvironment.getWebContext().getParentPageMeta();
/*      */     
/*  789 */     RefNode refnode = (RefNode)parentPm.getView(widgetId).getViewModels().getRefNode(refNodeId);
/*  790 */     if (refnode == null) {
/*  791 */       throw new LfwRuntimeException("Reference refNode's object is null. RefNodeId is " + refNodeId + ".");
/*      */     }
/*      */     
/*  794 */     ILfwRefModel refModel = LfwRefUtil.getRefModel(refnode);
/*  795 */     if (refModel == null) {
/*  796 */       throw new LfwRuntimeException("Reference refModel's object is null. RefNodeId is " + refNodeId + ".");
/*      */     }
/*      */     
/*  799 */     Dataset ds = null;
/*      */     
/*      */ 
/*  802 */     if ((refModel instanceof ILfwGridTreeRefModel)) {
/*  803 */       ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset("masterDs_tree");
/*      */     } else {
/*  805 */       ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset("masterDs");
/*      */     }
/*  807 */     if (ds == null) {
/*  808 */       throw new LfwRuntimeException("Reference dataset's object is null.");
/*      */     }
/*      */     
/*  811 */     clearFilterValue();
/*  812 */     ds.clear();
/*      */     
/*  814 */     if ((ds.getCurrentKey() == null) || ("".equals(ds.getCurrentKey()))) {
/*  815 */       ds.setCurrentKey("MASTER_KEY");
/*      */     }
/*      */     
/*  818 */     ReferenceComp text = (ReferenceComp)e.getSource();
/*  819 */     String pk_org = text.getValue();
/*      */     
/*      */ 
/*  822 */     if ((pk_org != null) && (!pk_org.equals(""))) {
/*  823 */       refModel.setPk_org(pk_org);
/*  824 */       if ((refModel instanceof ILfwGridTreeRefModel)) {
/*  825 */         ((ILfwGridTreeRefModel)refModel).setClassWherePart(" pk_org='" + pk_org + "'");
/*      */       }
/*      */     }
/*      */     
/*  829 */     String filterValue = getFilterValue();
/*      */     
/*  831 */     if ((refnode.getDataListener() != null) && (!refnode.getDataListener().equals(""))) {
/*      */       try {
/*  833 */         AppReferenceController selfCtrl = (AppReferenceController)LfwClassUtil.newInstance(refnode.getDataListener());
/*  834 */         selfCtrl.processNCRefModel(ds, refnode, filterValue, refModel);
/*  835 */         selfCtrl.processTreeSelWherePart(ds, refnode, refModel);
/*      */       } catch (Exception e1) {
/*  837 */         nc.uap.lfw.core.log.LfwLogger.error(e1);
/*      */       }
/*      */     }
/*  840 */     processNCRefModel(ds, refnode, filterValue, refModel);
/*      */     
/*  842 */     processTreeSelWherePart(ds, refnode, refModel);
/*      */     
/*  844 */     if ((refModel instanceof ILfwGridTreeRefModel)) {
/*  845 */       ILfwGridTreeRefModel tgModel = (ILfwGridTreeRefModel)refModel;
/*      */       
/*  847 */       List<List<Object>> v = tgModel.getClassData();
/*  848 */       new List2DatasetSerializer().serialize(ds.getCurrentKey() != null ? ds.getCurrentKey() : "MASTER_KEY", null, v, ds);
/*      */       
/*  850 */       Row curRow = ds.getSelectedRow();
/*  851 */       if (curRow == null) {
/*  852 */         Row[] rows = ds.getCurrentRowData() == null ? null : ds.getCurrentRowData().getRows();
/*  853 */         if ((rows != null) && (rows.length > 0)) {
/*  854 */           curRow = rows[0];
/*      */         }
/*      */       }
/*  857 */       if (curRow != null) {
/*  858 */         loadTreeGridDatas(curRow);
/*  859 */         ds.setRowSelectIndex(Integer.valueOf(0));
/*      */       } else {
/*  861 */         curRow = ds.getEmptyRow();
/*  862 */         loadTreeGridDatas(curRow);
/*      */       }
/*      */     }
/*  865 */     else if ((refModel instanceof ILfwTreeRefModel)) {
/*  866 */       ILfwTreeRefModel tgModel = (ILfwTreeRefModel)refModel;
/*      */       
/*  868 */       RefResult rr = tgModel.getRefData(0);
/*  869 */       List<List<Object>> v = rr.getData();
/*  870 */       new List2DatasetSerializer().serialize(ds.getCurrentKey(), null, v, ds);
/*      */     } else {
/*  872 */       RowSet rowset = ds.getRowSet("MASTER_KEY", true);
/*  873 */       PaginationInfo pinfo = rowset.getPaginationInfo();
/*      */       
/*  875 */       List<List<Object>> v = null;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  882 */       pinfo.setPageIndex(0);
/*      */       
/*  884 */       RefResult rd = refModel.getRefData(0);
/*      */       
/*  886 */       if (rd != null) {
/*  887 */         pinfo.setRecordsCount(rd.getTotalCount());
/*  888 */         v = rd.getData();
/*      */       } else {
/*  890 */         pinfo.setRecordsCount(0);
/*      */       }
/*      */       
/*  893 */       new List2DatasetSerializer().serialize(ds.getCurrentKey(), pinfo, v, ds);
/*      */     }
/*      */   }
/*      */   
/*      */   public void filterValueChanged(TextEvent e)
/*      */   {
/*  899 */     String widgetId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("widgetId");
/*  900 */     String refNodeId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("refNodeId");
/*  901 */     LfwWindow parentPm = LfwRuntimeEnvironment.getWebContext().getParentPageMeta();
/*  902 */     RefNode rfnode = (RefNode)parentPm.getView(widgetId).getViewModels().getRefNode(refNodeId);
/*      */     
/*  904 */     ILfwRefModel refModel = LfwRefUtil.getRefModel(rfnode);
/*  905 */     if (refModel == null) {
/*  906 */       throw new LfwRuntimeException("Reference refModel's object is null. RefNodeId is " + refNodeId + ".");
/*      */     }
/*      */     
/*  909 */     Dataset ds = null;
/*      */     
/*  911 */     if ((refModel instanceof ILfwGridTreeRefModel)) {
/*  912 */       ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset("masterDs_tree");
/*      */     } else {
/*  914 */       ds = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getDataset("masterDs");
/*      */     }
/*  916 */     if (ds == null) {
/*  917 */       throw new LfwRuntimeException("Reference dataset's object is null.");
/*      */     }
/*      */     
/*  920 */     clearFilterValue();
/*  921 */     ds.clear();
/*      */     
/*  923 */     if ((ds.getCurrentKey() == null) || ("".equals(ds.getCurrentKey()))) {
/*  924 */       ds.setCurrentKey("MASTER_KEY");
/*      */     }
/*      */     
/*  927 */     ReferenceComp text = (ReferenceComp)e.getSource();
/*  928 */     String filterValue = text.getValue();
/*      */     
/*  930 */     refModel.setNcRefModelFilterRefMap(null);
/*      */     
/*  932 */     IRefNode filterRefNode = AppLifeCycleContext.current().getViewContext().getView().getViewModels().getRefNode(text.getRefcode());
/*  933 */     if ((filterRefNode instanceof RefNode)) {
/*  934 */       String readFields = ((RefNode)filterRefNode).getReadFields();
/*  935 */       if (readFields != null) {
/*  936 */         String[] rfs = readFields.split(",");
/*  937 */         boolean isOrg = false;
/*  938 */         for (String rf : rfs) {
/*  939 */           if ((rf != null) && (rf.trim().equals("pk_org"))) {
/*  940 */             isOrg = true;
/*  941 */             break;
/*      */           }
/*      */         }
/*  944 */         if (isOrg) {
/*  945 */           refModel.setPk_org(filterValue);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  950 */     if ((refModel instanceof ILfwGridTreeRefModel)) {
/*  951 */       ILfwGridTreeRefModel tgModel = (ILfwGridTreeRefModel)refModel;
/*      */       
/*      */ 
/*      */ 
/*  955 */       tgModel.filterValueChanged(filterValue);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  960 */       List<List<Object>> v = tgModel.getClassData();
/*      */       
/*  962 */       new List2DatasetSerializer().serialize(ds.getCurrentKey(), null, v, ds);
/*      */       
/*  964 */       Row curRow = ds.getSelectedRow();
/*  965 */       if (curRow == null) {
/*  966 */         Row[] rows = ds.getCurrentRowData() == null ? null : ds.getCurrentRowData().getRows();
/*  967 */         if ((rows != null) && (rows.length > 0)) {
/*  968 */           curRow = rows[0];
/*      */         }
/*      */       }
/*  971 */       if (curRow != null) {
/*  972 */         loadTreeGridDatas(curRow);
/*  973 */         ds.setRowSelectIndex(Integer.valueOf(0));
/*      */       } else {
/*  975 */         curRow = ds.getEmptyRow();
/*  976 */         loadTreeGridDatas(curRow);
/*      */       }
/*      */       
/*      */     }
/*  980 */     else if ((refModel instanceof ILfwTreeRefModel)) {
/*  981 */       ILfwTreeRefModel tgModel = (ILfwTreeRefModel)refModel;
/*      */       
/*  983 */       tgModel.filterValueChanged(filterValue);
/*  984 */       RefResult rr = tgModel.getRefData(0);
/*  985 */       List<List<Object>> v = rr.getData();
/*  986 */       new List2DatasetSerializer().serialize(ds.getCurrentKey(), null, v, ds);
/*      */     } else {
/*  988 */       refModel.filterValueChanged(filterValue);
/*      */       
/*  990 */       RowSet rowset = ds.getRowSet("MASTER_KEY", true);
/*  991 */       PaginationInfo pinfo = rowset.getPaginationInfo();
/*      */       
/*  993 */       List<List<Object>> v = null;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1002 */       pinfo.setPageIndex(0);
/*      */       
/* 1004 */       RefResult rr = refModel.getRefData(0);
/*      */       
/* 1006 */       if (rr != null) {
/* 1007 */         pinfo.setRecordsCount(rr.getTotalCount());
/* 1008 */         v = rr.getData();
/*      */       } else {
/* 1010 */         pinfo.setRecordsCount(0);
/*      */       }
/*      */       
/* 1013 */       new List2DatasetSerializer().serialize(ds.getCurrentKey(), pinfo, v, ds);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void processTreeSelWherePart(Dataset ds, RefNode rfnode, ILfwRefModel refModel) {
/* 1018 */     processSelfWherePart(ds, rfnode, getFilterValue(), refModel);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void afterPageInit(PageEvent pageEvent) {}
/*      */   
/*      */ 
/*      */ 
/*      */   public String[] matchRefPk(ScriptEvent scriptEvent)
/*      */   {
/* 1030 */     WebContext ctx = LfwRuntimeEnvironment.getWebContext();
/*      */     
/* 1032 */     String widgetId = AppLifeCycleContext.current().getParameter("widgetId");
/* 1033 */     if ((widgetId == null) || ("".equals(widgetId)))
/* 1034 */       return null;
/* 1035 */     String refNodeId = AppLifeCycleContext.current().getParameter("refNodeId");
/* 1036 */     String value = AppLifeCycleContext.current().getParameter("matchValue");
/* 1037 */     WebSession ws = ctx.getWebSession();
/* 1038 */     LfwWindow pageMeta = (LfwWindow)ws.getAttribute("$PAGEMETA_KEY");
/* 1039 */     IRefNode refNode = pageMeta.getView(widgetId).getViewModels().getRefNode(refNodeId);
/* 1040 */     if ((refNode instanceof RefNode)) {
/* 1041 */       RefNode rf = (RefNode)refNode;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1046 */       ILfwRefModel rm = LfwRefUtil.getRefModel(rf);
/* 1047 */       onBeforeMatchRefPk(rm);
/* 1048 */       String rfStr = rf.getReadFields();
/* 1049 */       String[] rfs = rfStr.split(",");
/* 1050 */       String returnField = null;
/* 1051 */       String pkField = rm.getPkFieldCode();
/* 1052 */       String codeField = rm.getRefCodeField();
/* 1053 */       String nameField = rm.getRefNameField();
/* 1054 */       int returnIndex = -1;
/* 1055 */       int pkIndex = -1;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1063 */       if ((null != nameField) && (nameField.indexOf(".") != -1)) {
/* 1064 */         String[] fieldCodes = rm.getFieldCode();
/* 1065 */         int len = fieldCodes != null ? fieldCodes.length : 0;
/* 1066 */         String[] hiddenFieldCodes = rm.getHiddenFieldCode();
/* 1067 */         int hlen = hiddenFieldCodes != null ? hiddenFieldCodes.length : 0;
/*      */         
/* 1069 */         if (len + hlen > 0) {
/* 1070 */           List<String> list = new ArrayList(len + hlen);
/* 1071 */           if (len > 0) {
/* 1072 */             list.addAll(Arrays.asList(fieldCodes));
/*      */           }
/* 1074 */           if (hlen > 0) {
/* 1075 */             list.addAll(Arrays.asList(hiddenFieldCodes));
/*      */           }
/*      */           
/* 1078 */           for (int i = 0; i < rfs.length; i++) {
/* 1079 */             for (int j = 0; j < list.size(); j++) {
/* 1080 */               String fieldCode = (String)list.get(j);
/* 1081 */               if (fieldCode != null)
/*      */               {
/*      */ 
/*      */ 
/* 1085 */                 int asIndex = fieldCode.lastIndexOf(" as ");
/* 1086 */                 int lastIndex = fieldCode.lastIndexOf(".");
/* 1087 */                 if ((!fieldCode.equals(rfs[i])) && (lastIndex != -1)) {
/* 1088 */                   String endStr = null;
/* 1089 */                   if (asIndex != -1) {
/* 1090 */                     endStr = fieldCode.substring(asIndex + " as ".length()).trim();
/* 1091 */                     lastIndex = endStr.lastIndexOf(".");
/* 1092 */                     if (lastIndex != -1) {
/* 1093 */                       endStr = endStr.substring(lastIndex + 1).trim();
/*      */                     }
/*      */                   } else {
/* 1096 */                     endStr = fieldCode.substring(lastIndex + 1).trim();
/*      */                   }
/* 1098 */                   if (endStr.equals(rfs[i])) {
/* 1099 */                     if ((fieldCode.equals(codeField)) || (fieldCode.equals(nameField))) {
/* 1100 */                       rfs[i] = fieldCode; break;
/*      */                     }
/* 1102 */                     rfs[i] = (fieldCode.split("\\.")[0] + "." + rfs[i]);
/*      */                     
/* 1104 */                     break;
/*      */                   }
/* 1106 */                 } else if ((!fieldCode.equals(rfs[i])) && (asIndex != -1)) {
/* 1107 */                   String endStr = fieldCode.substring(asIndex + " as ".length()).trim();
/* 1108 */                   if (endStr.equals(rfs[i])) {
/* 1109 */                     if ((fieldCode.equals(codeField)) || (fieldCode.equals(nameField))) {
/* 1110 */                       rfs[i] = fieldCode; break;
/*      */                     }
/* 1112 */                     rfs[i] = (fieldCode.split("\\.")[0] + "." + rfs[i]);
/*      */                     
/* 1114 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1122 */       for (int i = 0; i < rfs.length; i++) {
/* 1123 */         if (!rfs[i].equals(pkField)) {
/* 1124 */           if (pkField.endsWith(rfs[i])) {
/* 1125 */             rfs[i] = pkField;
/*      */           }
/* 1127 */           if ((rfs[i].equals(codeField)) || (rfs[i].equals(nameField))) {
/* 1128 */             returnField = rfs[i];
/* 1129 */             returnIndex = i;
/*      */           }
/*      */         } else {
/* 1132 */           pkIndex = i;
/*      */         }
/*      */       }
/*      */       
/* 1136 */       String datasetId = AppLifeCycleContext.current().getParameter("datasetId");
/* 1137 */       Dataset ds = null;
/* 1138 */       ReferenceComp ref = null;
/* 1139 */       if ((datasetId != null) && (!"".equals(datasetId))) {
/* 1140 */         ds = pageMeta.getView(widgetId).getViewModels().getDataset(datasetId);
/*      */       } else {
/* 1142 */         String refId = AppLifeCycleContext.current().getParameter("referenceTextId");
/* 1143 */         ref = (ReferenceComp)pageMeta.getView(widgetId).getViewComponents().getComponent(refId);
/*      */       }
/*      */       
/* 1146 */       if (returnIndex == -1) {
/* 1147 */         if (((RefNode)refNode).isAllowInput()) {
/* 1148 */           if (ds != null)
/*      */           {
/* 1150 */             dealAllowInputMatch(value, refNode, ds);
/*      */           }
/*      */           else {
/* 1153 */             ref.setValue(value);
/* 1154 */             ref.setShowValue(value);
/*      */           }
/*      */         } else {
/* 1157 */           setRowValue(ds, ref, (RefNode)refNode, value, null, 0);
/*      */         }
/* 1159 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1166 */       String wpk = null;
/* 1167 */       String wfield = null;
/*      */       
/* 1169 */       String wfStr = rf.getWriteFields();
/* 1170 */       if ((wfStr != null) && (!wfStr.equals(""))) {
/* 1171 */         String[] wfs = wfStr.split(",");
/* 1172 */         if (pkIndex != -1)
/* 1173 */           wpk = wfs[pkIndex];
/* 1174 */         if (returnIndex != -1) {
/* 1175 */           wfield = wfs[returnIndex];
/*      */         }
/*      */       }
/* 1178 */       String[][] rts = (String[][])null;
/* 1179 */       if ((value != null) && (!value.equals("")))
/*      */       {
/* 1181 */         processNCRefModel(ds, (RefNode)refNode, "", rm);
/* 1182 */         processTreeSelWherePart(ds, (RefNode)refNode, rm);
/* 1183 */         rts = rm.matchRefPk(value, returnField, rfs, ((RefNode)refNode).isMultiSel());
/*      */       }
/*      */       
/* 1186 */       if ((rts == null) || (rts.length == 0)) {
/* 1187 */         if (((RefNode)refNode).isAllowInput()) {
/* 1188 */           if (ds != null)
/*      */           {
/* 1190 */             dealAllowInputMatch(value, refNode, ds);
/*      */           }
/*      */           else {
/* 1193 */             ref.setValue(value);
/* 1194 */             ref.setShowValue(value);
/*      */           }
/*      */         } else {
/* 1197 */           setRowValue(ds, ref, (RefNode)refNode, value, null, 0);
/*      */         }
/* 1199 */         return null;
/*      */       }
/*      */       
/* 1202 */       String[] values = value.split(",");
/*      */       
/*      */ 
/* 1205 */       int rsize = rfs.length;
/* 1206 */       for (int j = 0; j < rts.length; j++) {
/* 1207 */         value = values[j];
/* 1208 */         String[] result = new String[4 + rsize];
/* 1209 */         String[] rt = rts[j];
/* 1210 */         result[0] = rt[0];
/* 1211 */         result[1] = rt[1];
/* 1212 */         result[2] = wpk;
/* 1213 */         result[3] = wfield;
/* 1214 */         int i = 0; for (int n = rsize; i < n; i++) {
/* 1215 */           String rv = rt[(2 + i)];
/* 1216 */           result[(i + 4)] = rv;
/*      */         }
/*      */         
/* 1219 */         if (((RefNode)refNode).isAllowInput())
/*      */         {
/* 1221 */           setRowValue(ds, ref, (RefNode)refNode, value, result, j);
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */ 
/* 1229 */           setRowValue(ds, ref, (RefNode)refNode, value, result, j);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1236 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void dealAllowInputMatch(String value, IRefNode refNode, Dataset ds)
/*      */   {
/* 1246 */     String[] writeFields = ((RefNode)refNode).getWriteFields().split(",");
/* 1247 */     Row row = getSelectedRow(ds);
/* 1248 */     if (row != null) {
/* 1249 */       int n = writeFields.length;
/* 1250 */       if (n - 2 >= 0) {
/* 1251 */         for (int i = n - 2; i < n; i++) {
/* 1252 */           int index = ds.nameToIndex(writeFields[i]);
/* 1253 */           row.setValue(index, value);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Row getSelectedRow(Dataset ds)
/*      */   {
/* 1265 */     Row row = null;
/* 1266 */     String index = AppLifeCycleContext.current().getParameter("selectIndex");
/* 1267 */     if ((index != null) && (index.trim().length() > 0)) {
/* 1268 */       int i = Integer.parseInt(index);
/* 1269 */       if (i >= 0) {
/* 1270 */         RowData rowData = ds.getCurrentRowSet().getCurrentRowData();
/* 1271 */         if ((rowData != null) && (rowData.getRowCount() > i)) {
/* 1272 */           row = rowData.getRow(i);
/*      */         } else
/* 1274 */           row = ds.getSelectedRow();
/*      */       } else {
/* 1276 */         row = ds.getSelectedRow();
/*      */       }
/*      */     } else {
/* 1279 */       row = ds.getSelectedRow();
/*      */     }
/* 1281 */     return row;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void onBeforeMatchRefPk(ILfwRefModel rm) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void getExtendsParam(ScriptEvent scriptEvent) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void matchSearch(ScriptEvent scriptEvent)
/*      */   {
/* 1304 */     String widgetId = AppLifeCycleContext.current().getParameter("widgetId");
/* 1305 */     String refNodeId = AppLifeCycleContext.current().getParameter("refNodeId");
/* 1306 */     String value = AppLifeCycleContext.current().getParameter("matchValue");
/*      */     
/*      */ 
/* 1309 */     WebContext ctx = LfwRuntimeEnvironment.getWebContext();
/* 1310 */     WebSession ws = ctx.getWebSession();
/* 1311 */     LfwWindow pageMeta = (LfwWindow)ws.getAttribute("$PAGEMETA_KEY");
/* 1312 */     ReferenceComp referenceText = null;
/* 1313 */     String formId = AppLifeCycleContext.current().getParameter("refFormId");
/* 1314 */     String formElementId = AppLifeCycleContext.current().getParameter("refFormeleID");
/* 1315 */     String gridId = AppLifeCycleContext.current().getParameter("refGridId");
/* 1316 */     String gridHeaderId = AppLifeCycleContext.current().getParameter("refGridHeaderId");
/* 1317 */     FormElement formEle = null;
/* 1318 */     GridColumn column = null;
/* 1319 */     if ((formId != null) && (!"".equals(formId)) && (formElementId != null) && (!"".equals(formElementId))) {
/* 1320 */       FormComp formComp = (FormComp)pageMeta.getView(widgetId).getViewComponents().getComponent(formId);
/* 1321 */       formEle = formComp.getElementById(formElementId);
/*      */     }
/* 1323 */     else if ((gridId != null) && (!"".equals(gridId)) && (gridHeaderId != null) && (!"".equals(gridHeaderId))) {
/* 1324 */       GridComp gridComp = (GridComp)pageMeta.getView(widgetId).getViewComponents().getComponent(gridId);
/* 1325 */       column = (GridColumn)gridComp.getColumnById(gridHeaderId);
/*      */     }
/*      */     else {
/* 1328 */       String referenceTextId = AppLifeCycleContext.current().getParameter("referenceTextId");
/* 1329 */       if ((referenceTextId != null) && (!"".equals(referenceTextId))) {
/* 1330 */         referenceText = (ReferenceComp)pageMeta.getView(widgetId).getViewComponents().getComponent(referenceTextId);
/*      */       }
/*      */     }
/*      */     
/* 1334 */     IRefNode refNode = pageMeta.getView(widgetId).getViewModels().getRefNode(refNodeId);
/* 1335 */     if ((refNode instanceof RefNode)) {
/* 1336 */       RefNode rf = (RefNode)refNode;
/* 1337 */       ILfwRefModel ncmodel = LfwRefUtil.getRefModel(rf);
/* 1338 */       onBeforeMatchSearch(ncmodel);
/* 1339 */       String datasetId = AppLifeCycleContext.current().getParameter("datasetId");
/* 1340 */       Dataset ds = null;
/* 1341 */       if ((datasetId != null) && (!"".equals(datasetId))) {
/* 1342 */         ds = pageMeta.getView(widgetId).getViewModels().getDataset(datasetId);
/*      */       }
/*      */       
/* 1345 */       processNCRefModel(ds, (RefNode)refNode, "", ncmodel);
/* 1346 */       processTreeSelWherePart(ds, (RefNode)refNode, ncmodel);
/*      */       
/* 1348 */       if ((ncmodel instanceof ILfwGridTreeRefModel))
/*      */       {
/* 1350 */         ((ILfwGridTreeRefModel)ncmodel).setClassJoinValue(QUERY);
/*      */       }
/*      */       
/* 1353 */       StringBuffer matchsValues = null;
/*      */       
/* 1355 */       AbstractLfwRefListItem[] objs = new DefaultLfwRefListFilter().filter(value, ncmodel);
/* 1356 */       int len = objs != null ? objs.length : 0;
/* 1357 */       if (len == 0) {
/* 1358 */         matchsValues = new StringBuffer(0);
/*      */       } else {
/* 1360 */         matchsValues = new StringBuffer();
/*      */         
/* 1362 */         int codeIndex = ncmodel.getFieldIndex(ncmodel.getRefCodeField());
/* 1363 */         int nameIndex = ncmodel.getFieldIndex(ncmodel.getRefNameField());
/*      */         
/* 1365 */         int matchSearchCount = ((RefNode)refNode).getMatchSearchCount();
/* 1366 */         len = len > matchSearchCount ? matchSearchCount : len;
/*      */         
/* 1368 */         String[] strs = new String[objs.length];
/* 1369 */         for (int i = 0; i < objs.length; i++) {
/* 1370 */           AbstractLfwRefListItem item = objs[i];
/* 1371 */           String refCodeField = ncmodel.getRefCodeField();
/* 1372 */           if ((refCodeField != null) && (!"".equals(refCodeField))) {
/* 1373 */             strs[i] = item.getCode();
/*      */           }
/* 1375 */           if ((item.getName() != null) && (!"".equals(item.getName()))) {
/* 1376 */             if (strs[i] == null) {
/* 1377 */               strs[i] = item.getName();
/*      */ 
/*      */             }
/* 1380 */             else if (codeIndex != nameIndex) {
/* 1381 */               int tmp647_645 = i; String[] tmp647_643 = strs;tmp647_643[tmp647_645] = (tmp647_643[tmp647_645] + "," + item.getName());
/*      */             } else {
/* 1383 */               int tmp683_681 = i; String[] tmp683_679 = strs;tmp683_679[tmp683_681] = (tmp683_679[tmp683_681] + "_" + i + "_HIDDEN_CODE," + item.getName());
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1389 */         for (int i = 0; i < strs.length; i++) {
/* 1390 */           if (i != strs.length - 1) {
/* 1391 */             matchsValues.append(strs[i]);
/* 1392 */             matchsValues.append(";");
/*      */           }
/*      */           else {
/* 1395 */             matchsValues.append(strs[i]);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1400 */       if (formEle != null) {
/* 1401 */         formEle.setMatchValues(matchsValues.toString());
/* 1402 */       } else if (column != null) {
/* 1403 */         column.setMatchValues(matchsValues.toString());
/* 1404 */       } else if (referenceText != null) {
/* 1405 */         referenceText.setMatchValues(matchsValues.toString());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void onBeforeMatchSearch(ILfwRefModel rm) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setRowValue(Dataset ds, ReferenceComp ref, RefNode refNode, String value, String[] result, int currentIndex)
/*      */   {
/* 1441 */     if ((value == null) || (value == "") || (result == null)) {
/* 1442 */       if (ds != null) {
/* 1443 */         String[] writeFields = refNode.getWriteFields().split(",");
/* 1444 */         Row row = getSelectedRow(ds);
/* 1445 */         if (row == null) {
/* 1446 */           return;
/*      */         }
/* 1448 */         int i = 0; for (int n = writeFields.length; i < n; i++) {
/* 1449 */           int index = ds.nameToIndex(writeFields[i]);
/* 1450 */           if (index >= 0) {
/* 1451 */             row.setValue(index, null);
/*      */           }
/*      */         }
/*      */       }
/* 1455 */       else if (ref != null) {
/* 1456 */         ref.setValue(null);
/* 1457 */         ref.setShowValue(null);
/*      */       }
/*      */       
/*      */ 
/*      */     }
/* 1462 */     else if (ds != null) {
/* 1463 */       String[] writeFields = refNode.getWriteFields().split(",");
/* 1464 */       Row row = getSelectedRow(ds);
/* 1465 */       if (row == null) {
/* 1466 */         return;
/*      */       }
/* 1468 */       int i = 0; for (int n = writeFields.length; i < n; i++) {
/* 1469 */         int index = ds.nameToIndex(writeFields[i]);
/* 1470 */         if ((result == null) || (result.length <= 4)) {
/* 1471 */           row.setValue(index, null);
/*      */         }
/*      */         else {
/* 1474 */           String fieldValue = (result[(i + 4)] == null) || (result[(i + 4)].equals("")) ? null : result[(i + 4)];
/* 1475 */           if (currentIndex == 0) {
/* 1476 */             row.setValue(index, fieldValue);
/*      */           }
/*      */           else {
/* 1479 */             String oldValue = (String)row.getValue(index);
/* 1480 */             if ((oldValue != null) && (!oldValue.equals(""))) {
/* 1481 */               if (fieldValue != null) {
/* 1482 */                 row.setValue(index, oldValue + "," + fieldValue);
/*      */               }
/*      */             }
/*      */             else {
/* 1486 */               row.setValue(index, fieldValue);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1492 */     else if (ref != null) {
/* 1493 */       if (currentIndex == 0) {
/* 1494 */         ref.setValue("".equals(result[0]) ? null : result[0]);
/* 1495 */         ref.setShowValue(result[1]);
/*      */       }
/*      */       else {
/* 1498 */         String oldValue = ref.getValue();
/* 1499 */         String oldShowValue = ref.getShowValue();
/* 1500 */         if ((oldValue != null) && (!oldValue.equals(""))) {
/* 1501 */           ref.setValue(oldValue + "," + result[0]);
/* 1502 */           ref.setShowValue(oldShowValue + "," + result[1]);
/*      */         }
/*      */         else {
/* 1505 */           ref.setValue("".equals(result[0]) ? null : result[0]);
/* 1506 */           ref.setShowValue(result[1]);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void onBeforeShow(DialogEvent event)
/*      */   {
/* 1524 */     LfwWindow pWin = AppLifeCycleContext.current().getWindowContext().getParentWindowContext().getWindow();
/* 1525 */     String viewId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("widgetId");
/* 1526 */     LfwView pView = pWin.getView(viewId);
/*      */     
/* 1528 */     String refNodeId = (String)LfwRuntimeEnvironment.getWebContext().getWebSession().getAttribute("refNodeId");
/* 1529 */     RefNode rfnode = (RefNode)pView.getViewModels().getRefNode(refNodeId);
/* 1530 */     ILfwRefModel refModel = LfwRefUtil.getRefModel(rfnode);
/*      */     
/* 1532 */     LfwView view = AppLifeCycleContext.current().getViewContext().getView();
/* 1533 */     Dataset ds = view.getViewModels().getDataset("masterDs_class");
/*      */     
/* 1535 */     if (((refModel instanceof ILfwTreeRefModel)) && (ds != null)) {
/* 1536 */       ILfwTreeRefModel treeRefModel = (ILfwTreeRefModel)refModel;
/* 1537 */       ViewContext widgetCtx = AppLifeCycleContext.current().getViewContext();
/* 1538 */       LfwView widget = widgetCtx.getView();
/* 1539 */       List<List<Object>> v = null;
/* 1540 */       PaginationInfo pInfo = ds.getCurrentRowSet().getPaginationInfo();
/*      */       
/*      */ 
/* 1543 */       String pk_org = null;
/* 1544 */       if ((rfnode instanceof NCRefNode)) {
/* 1545 */         boolean orgs = (((NCRefNode)rfnode).isOrgs()) || (((NCRefNode)rfnode).isHrOrgs());
/* 1546 */         if (orgs) {
/* 1547 */           ReferenceComp reftext = (ReferenceComp)widget.getViewComponents().getComponent("filterRefComp_0");
/* 1548 */           if (reftext != null) {
/* 1549 */             pk_org = reftext.getValue();
/*      */           }
/*      */         }
/*      */       }
/* 1553 */       if ((null != pk_org) && (!"".equals(pk_org))) {
/* 1554 */         refModel.setPk_org(pk_org);
/*      */       }
/*      */       
/* 1557 */       processNCRefModel(ds, rfnode, null, refModel);
/*      */       
/* 1559 */       int refType = LfwRefUtil.getRefType(refModel);
/*      */       
/* 1561 */       if (refType != 2)
/*      */       {
/* 1563 */         dealRefNodeRelations(ds, refModel, true);
/*      */       }
/*      */       
/* 1566 */       DatasetRelations dsRels = widget.getViewModels().getDsrelations();
/* 1567 */       if (dsRels != null) {
/* 1568 */         String parentDsId = dsRels.getMasterDsByDetailDs(ds.getId());
/* 1569 */         if (parentDsId != null) {
/* 1570 */           Dataset parentDs = widget.getViewModels().getDataset(parentDsId);
/* 1571 */           Row row = parentDs.getSelectedRow();
/* 1572 */           if (row != null) {
/* 1573 */             DatasetRelation dr = dsRels.getDsRelation(parentDsId, ds.getId());
/* 1574 */             String masterKey = dr.getMasterKeyField();
/* 1575 */             String keyValue = (String)row.getValue(parentDs.getFieldSet().nameToIndex(masterKey));
/*      */             
/* 1577 */             if (keyValue == null) {
/* 1578 */               keyValue = row.getRowId();
/*      */             }
/* 1580 */             ds.clear();
/* 1581 */             ds.setCurrentKey(keyValue);
/* 1582 */             RowSet rowset = ds.getRowSet(keyValue, true);
/* 1583 */             PaginationInfo pinfo = rowset.getPaginationInfo();
/* 1584 */             if (pinfo.getPageIndex() == -1) {
/* 1585 */               pinfo.setPageIndex(0);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1591 */       RefResult rd = null;
/* 1592 */       if (pInfo.getPageSize() != -1) {
/* 1593 */         int pageIndex = pInfo.getPageIndex();
/* 1594 */         rd = treeRefModel.getClassRefData(pageIndex);
/*      */       }
/*      */       else {
/* 1597 */         rd = treeRefModel.getClassRefData(0);
/*      */       }
/* 1599 */       if (rd != null) {
/* 1600 */         pInfo.setRecordsCount(rd.getTotalCount());
/* 1601 */         v = rd.getData();
/*      */       }
/*      */       
/* 1604 */       new List2DatasetSerializer().serialize(ds.getCurrentKey(), pInfo, v, ds);
/*      */     }
/*      */   }
/*      */   
/*      */   protected String getFilterValue() {
/* 1609 */     LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
/* 1610 */     TextComp locateText = (TextComp)widget.getViewComponents().getComponent("locatetext");
/* 1611 */     if (locateText != null) {
/* 1612 */       return validateValue(locateText.getValue());
/*      */     }
/* 1614 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String validateValue(String value)
/*      */   {
/* 1624 */     if ((null != value) && (value.contains("'"))) {
/* 1625 */       value = value.replaceAll("'", "''");
/*      */     }
/* 1627 */     return value;
/*      */   }
/*      */   
/*      */   protected void clearFilterValue() {
/* 1631 */     LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
/* 1632 */     TextComp locateText = (TextComp)widget.getViewComponents().getComponent("locatetext");
/* 1633 */     if (locateText != null) {
/* 1634 */       locateText.setValue("");
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\ncProjects\6.5\COCO_SZ\nchome\home20170910\modules\webrt\lib\pubwebrt_refLevel-1.jar
 * Qualified Name:     nc.uap.lfw.reference.app.AppReferenceController
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */