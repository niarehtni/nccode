/*     */ package nc.bs.hrss.ta.overtime.ctrl;
/*     */ 
/*     */ import nc.bs.framework.common.NCLocator;
/*     */ import nc.bs.hrss.pub.exception.HrssException;
/*     */ import nc.bs.hrss.pub.pf.ctrl.WebBillApproveView;
/*     */ import nc.bs.hrss.pub.tool.ViewUtil;
/*     */ import nc.bs.hrss.ta.overtime.OverTimeConsts;
/*     */ import nc.bs.hrss.ta.overtime.lsnr.OverTimeApproveProcessor;
/*     */ import nc.bs.hrss.ta.overtime.lsnr.OverTimeApproveSaveProcessor;
/*     */ import nc.itf.hrss.pub.cmd.prcss.IProcessor;
/*     */ import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
/*     */ import nc.itf.ta.ITimeRuleQueryService;
/*     */ import nc.uap.lfw.core.data.Dataset;
/*     */ import nc.uap.lfw.core.data.Field;
/*     */ import nc.uap.lfw.core.data.FieldSet;
/*     */ import nc.uap.lfw.core.data.MdDataset;
/*     */ import nc.uap.lfw.core.data.Row;
/*     */ import nc.uap.lfw.core.data.RowData;
/*     */ import nc.uap.lfw.core.data.UnmodifiableMdField;
/*     */ import nc.uap.lfw.core.event.CellEvent;
/*     */ import nc.uap.lfw.core.event.DataLoadEvent;
/*     */ import nc.uap.lfw.core.event.DatasetCellEvent;
/*     */ import nc.uap.lfw.core.page.LfwView;
/*     */ import nc.uap.lfw.core.page.ViewModels;
/*     */ import nc.vo.pub.AggregatedValueObject;
/*     */ import nc.vo.pub.BusinessException;
/*     */ import nc.vo.ta.timerule.TimeRuleVO;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OverTimeApproveView
/*     */   extends WebBillApproveView
/*     */ {
/*     */   protected Class<? extends IProcessor> getApprovePrcss()
/*     */   {
/*  48 */     return OverTimeApproveProcessor.class;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Class<? extends ISaveProcessor> getSavePrcss()
/*     */   {
/*  58 */     return OverTimeApproveSaveProcessor.class;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Class<? extends AggregatedValueObject> getAggVoClazz()
/*     */   {
/*  68 */     return OverTimeConsts.CLASS_NAME_AGGVO;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onDataLoad_masterDs(DataLoadEvent e)
/*     */   {
/*  78 */     Dataset masterDs = (Dataset)e.getSource();
/*  79 */     super.onDataLoad_masterDs(e);
/*  80 */     Row selRow = masterDs.getSelectedRow();
/*  81 */     if (selRow == null) {
/*  82 */       return;
/*     */     }
/*     */     
/*     */ 
/*  86 */     String[] timeDatas = { "sumhour", "acthour", "overtimehour", "deduct", "overtimealready" };
/*     */     
/*  88 */     String pk_org = selRow.getString(masterDs.nameToIndex("pk_org"));
/*  89 */     int pointNum = getPointNum(pk_org);
/*     */     
/*  91 */     Dataset[] dss = getCurrentView().getViewModels().getDatasets();
/*  92 */     if ((dss == null) || (dss.length == 0)) {
/*  93 */       return;
/*     */     }
/*  95 */     for (Dataset ds : dss) {
/*  96 */       if ((ds instanceof MdDataset)) {
/*  97 */         for (String filedId : timeDatas) {
/*  98 */           int index = ds.getFieldSet().nameToIndex(filedId);
/*  99 */           if (index >= 0) {
/* 100 */             FieldSet fieldSet = ds.getFieldSet();
/* 101 */             Field field = fieldSet.getField(filedId);
/* 102 */             if ((field instanceof UnmodifiableMdField))
/* 103 */               field = ((UnmodifiableMdField)field).getMDField();
/* 104 */             fieldSet.updateField(filedId, field);
/* 105 */             field.setPrecision(String.valueOf(pointNum));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 112 */     String pk_psndoc = null;
/* 113 */     if (masterDs.nameToIndex("pk_psndoc") > 1) {
/* 114 */       pk_psndoc = (String)selRow.getValue(masterDs.nameToIndex("pk_psndoc"));
/*     */     }
/*     */     
/*     */ 
/* 118 */     String pk_psnjob = null;
/* 119 */     if (masterDs.nameToIndex("pk_psnjob") > 1) {
/* 120 */       pk_psnjob = (String)selRow.getValue(masterDs.nameToIndex("pk_psnjob"));
/*     */     }
/*     */     
/* 123 */     LfwView view = ViewUtil.getCurrentView();
/* 124 */     Dataset dsDetail = ViewUtil.getDataset(view, "hrtaovertimeb");
/* 125 */     RowData rowData = dsDetail.getCurrentRowData();
/* 126 */     if (rowData == null) {
/* 127 */       return;
/*     */     }
/* 129 */     Row[] rows = rowData.getRows();
/* 130 */     if ((rows == null) || (rows.length == 0)) {
/* 131 */       return;
/*     */     }
/*     */     
/* 134 */     for (Row row : rows) {
/* 135 */       row.setValue(dsDetail.nameToIndex("isEditable"), Boolean.valueOf(dsDetail.isEnabled()));
/* 136 */       row.setValue(dsDetail.nameToIndex("pk_psndoc"), pk_psndoc);
/* 137 */       row.setValue(dsDetail.nameToIndex("pk_psnjob"), pk_psnjob);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onAfterDataChange_hrtaovertimeb(DatasetCellEvent datasetCellEvent) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onBeforeEdit(CellEvent gridCellEvent) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int getPointNum(String pk_org)
/*     */   {
/* 162 */     if (StringUtils.isEmpty(pk_org))
/*     */     {
/* 164 */       return 2;
/*     */     }
/* 166 */     TimeRuleVO timeRuleVO = null;
/*     */     try
/*     */     {
/* 169 */       timeRuleVO = ((ITimeRuleQueryService)NCLocator.getInstance().lookup(ITimeRuleQueryService.class)).queryByOrg(pk_org);
/*     */     } catch (BusinessException ex) {
/* 171 */       new HrssException(ex).alert();
/*     */     }
/* 173 */     if (timeRuleVO == null)
/*     */     {
/* 175 */       return 2;
/*     */     }
/* 177 */     int pointNum = Math.abs(timeRuleVO.getTimedecimal().intValue());
/* 178 */     return pointNum;
/*     */   }
/*     */ }

/* Location:           E:\nchome65_goldSph\nchome65_gold\modules\hrss\lib\pubhrss_ta.jar
 * Qualified Name:     nc.bs.hrss.ta.overtime.ctrl.OverTimeApproveView
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */