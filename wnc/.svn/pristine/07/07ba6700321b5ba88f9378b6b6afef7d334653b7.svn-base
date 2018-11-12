/*     */ package nc.bs.hrss.ta.leaveoff.ctrl;
/*     */ 
/*     */ import nc.bs.hrss.pub.ServiceLocator;
/*     */ import nc.bs.hrss.pub.exception.HrssException;
/*     */ import nc.bs.hrss.pub.tool.CommonUtil;
/*     */ import nc.bs.hrss.pub.tool.DatasetUtil;
/*     */ import nc.bs.hrss.pub.tool.ViewUtil;
/*     */ import nc.bs.hrss.ta.utils.TaAppContextUtil;
/*     */ import nc.hr.utils.ResHelper;
/*     */ import nc.itf.ta.ILeaveOffApplyQueryMaintain;
/*     */ import nc.uap.lfw.core.comp.ButtonComp;
import nc.uap.lfw.core.comp.FormComp;
/*     */ import nc.uap.lfw.core.ctrl.IController;
/*     */ import nc.uap.lfw.core.data.Dataset;
/*     */ import nc.uap.lfw.core.data.Row;
/*     */ import nc.uap.lfw.core.data.RowData;
/*     */ import nc.uap.lfw.core.event.DataLoadEvent;
/*     */ import nc.uap.lfw.core.event.MouseEvent;
/*     */ import nc.uap.lfw.core.page.LfwView;
/*     */ import nc.uap.lfw.core.page.ViewModels;
/*     */ import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
/*     */ import nc.vo.pub.BusinessException;
/*     */ import nc.vo.pub.lang.UFBoolean;
/*     */ import nc.vo.pub.lang.UFDateTime;
/*     */ import nc.vo.pub.lang.UFDouble;
/*     */ import nc.vo.pub.lang.UFLiteralDate;
/*     */ import nc.vo.ta.leave.LeaveRegVO;
/*     */ import nc.vo.ta.psndoc.TBMPsndocVO;
/*     */ import org.apache.commons.lang.ArrayUtils;
/*     */ import uap.web.bd.pub.AppUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LeaveRegListView
/*     */   implements IController
/*     */ {
/*     */   public static final String VIEW_ID = "reglist";
/*     */   public static final String DS_ID = "dsLeaveRegList";
/*     */   public static final String APP_ID_PK_LEAVEREG = "app_pk_leavereg";
/*     */   public static final String APP_ID_PK_LEAVETYPE = "app_pk_leavetype";
/*     */   public static final String APP_ID_PK_LEAVETYPECOPY = "app_pk_leavetypecopy";
/*     */   
/*     */   public void onDataLoad(DataLoadEvent dataLoadEvent)
/*     */   {
/*  59 */     TBMPsndocVO tbmPsndocVO = TaAppContextUtil.getTBMPsndocVO();
/*  60 */     if (tbmPsndocVO == null) {
/*  61 */       return;
/*     */     }
/*  63 */     String pk_psnjob = tbmPsndocVO.getPk_psnjob();
/*  64 */     LeaveRegVO[] vos = null;
/*     */     
/*  66 */     ILeaveOffApplyQueryMaintain service = null;
/*     */     try {
/*  68 */       service = (ILeaveOffApplyQueryMaintain)ServiceLocator.lookup(ILeaveOffApplyQueryMaintain.class);
/*  69 */       vos = service.getRegVos4Hrss(pk_psnjob);
/*     */     } catch (BusinessException e) {
/*  71 */       new HrssException(e).deal();
/*     */     } catch (HrssException e) {
/*  73 */       e.alert();
/*     */     }
/*     */     
/*  76 */     Dataset ds = (Dataset)dataLoadEvent.getSource();
/*  77 */     if (ArrayUtils.isEmpty(vos)) {
/*  78 */       DatasetUtil.clearData(ds);
/*  79 */       return;
/*     */     }
/*  81 */     new SuperVO2DatasetSerializer().serialize(DatasetUtil.paginationMethod(ds, vos), ds, 0);
/*  82 */     ds.setRowSelectIndex(Integer.valueOf(0));
/*     */     
/*     */ 
/*  85 */     if (ds.getCurrentRowData() == null) {
/*  86 */       return;
/*     */     }
/*  88 */     Row[] rows = ds.getCurrentRowData().getRows();
/*  89 */     if (rows == null) {
/*  90 */       return;
/*     */     }
/*  92 */     UFBoolean islactation = UFBoolean.FALSE;
/*  93 */     for (Row row : rows) {
/*  94 */       islactation = (UFBoolean)row.getValue(ds.nameToIndex("islactation"));
/*  95 */       if (UFBoolean.TRUE.equals(islactation)) {
/*  96 */         row.setValue(ds.nameToIndex("leavebegintime"), row.getValue(ds.nameToIndex("leavebegindate")));
/*  97 */         row.setValue(ds.nameToIndex("leaveendtime"), row.getValue(ds.nameToIndex("leaveenddate")));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onConfirm(MouseEvent<ButtonComp> mouseEvent)
/*     */   {
/* 109 */     LfwView view = ViewUtil.getCurrentView();
/* 110 */     Dataset ds = view.getViewModels().getDataset("dsLeaveRegList");
/* 111 */     Row row = ds.getSelectedRow();
/* 112 */     if (row == null) {
/* 113 */       CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0167"), ResHelper.getString("c_ta-res", "0c_ta-res0056"));
/*     */     }
/*     */     
/*     */ 
/* 117 */     String pk_leavereg = row.getString(ds.nameToIndex("pk_leavereg"));
/* 118 */     String pk_leavetype = row.getString(ds.nameToIndex("pk_leavetype"));
/* 119 */     String pk_leavetypecopy = row.getString(ds.nameToIndex("pk_leavetypecopy"));
/*     */     
/*     */ 
/* 122 */     UFBoolean islactation = UFBoolean.FALSE;
/* 123 */     islactation = (UFBoolean)row.getValue(ds.nameToIndex("islactation"));
/* 124 */     UFDouble leavehour = (UFDouble)row.getValue(ds.nameToIndex("leavehour"));
/* 125 */     AppUtil.addAppAttr("leavehour", leavehour);
/*     */     
/* 127 */     UFLiteralDate leavebegindate = (UFLiteralDate)row.getValue(ds.nameToIndex("leavebegindate"));
/* 128 */     UFLiteralDate leaveenddate = (UFLiteralDate)row.getValue(ds.nameToIndex("leaveenddate"));
/* 129 */     AppUtil.addAppAttr("leavebegindate", leavebegindate);
/* 130 */     AppUtil.addAppAttr("leaveenddate", leaveenddate);
/*     */     
/* 132 */     UFDateTime leavebegintime = (UFDateTime)row.getValue(ds.nameToIndex("leavebegintime"));
/* 133 */     UFDateTime leaveendtime = (UFDateTime)row.getValue(ds.nameToIndex("leaveendtime"));
			  AppUtil.addAppAttr("leavebegintime", leavebegintime);
			  AppUtil.addAppAttr("leaveendtime", leaveendtime);
				  
/*     */     
/*     */ 	  
/*     */ 
/* 139 */     CommonUtil.closeViewDialog("reglist");
/*     */     
/* 141 */     String operate_status = "add";
/* 142 */     AppUtil.addAppAttr("hrss_operate_status", operate_status);
/* 143 */     AppUtil.addAppAttr("hrss_operate_param", null);
/* 144 */     AppUtil.addAppAttr("app_pk_leavereg", pk_leavereg);
/* 145 */     AppUtil.addAppAttr("app_pk_leavetype", pk_leavetype);
/* 146 */     AppUtil.addAppAttr("app_pk_leavetypecopy", pk_leavetypecopy);
/* 147 */     CommonUtil.showWindowDialog("LeaveOffApply", ResHelper.getString("c_ta-res", "0c_ta-res0202"), "80%", "100%", null, "TYPE_DIALOG", false, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onCancel(MouseEvent<ButtonComp> mouseEvent)
/*     */   {
/* 156 */     CommonUtil.closeViewDialog("reglist");
/*     */   }
/*     */ }

/* Location:           E:\TaiWan\yonyou\home\modules\hrss\lib\pubhrss_ta.jar
 * Qualified Name:     nc.bs.hrss.ta.leaveoff.ctrl.LeaveRegListView
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */