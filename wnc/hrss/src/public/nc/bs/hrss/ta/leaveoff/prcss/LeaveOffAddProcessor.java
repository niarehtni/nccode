 package nc.bs.hrss.ta.leaveoff.prcss;
 
 import nc.bs.hrss.ta.common.prcss.TaBaseAddProcessor;
 import nc.uap.lfw.core.ctx.AppLifeCycleContext;
 import nc.uap.lfw.core.ctx.ApplicationContext;
 import nc.uap.lfw.core.data.Dataset;
 import nc.uap.lfw.core.data.Row;
 import nc.vo.pub.lang.UFDate;
 import nc.vo.pub.lang.UFDateTime;
 import nc.vo.pub.lang.UFDouble;
 import nc.vo.pub.lang.UFLiteralDate;
 import uap.web.bd.pub.AppUtil;
 
 
 
 
 
 
 public class LeaveOffAddProcessor
   extends TaBaseAddProcessor
 {
   public void onBeforeRowAdd(Dataset ds, Row row, String billTypeCode)
   {
     super.onBeforeRowAdd(ds, row, billTypeCode);
     
     String pk_leavereg = (String)AppUtil.getAppAttr("app_pk_leavereg");
     row.setString(ds.nameToIndex("pk_leavereg"), pk_leavereg);
     
     String pk_leavetype = (String)AppUtil.getAppAttr("app_pk_leavetype");
     row.setString(ds.nameToIndex("pk_leavetype"), pk_leavetype);
     
     String pk_leavetypecopy = (String)AppUtil.getAppAttr("app_pk_leavetypecopy");
     row.setString(ds.nameToIndex("pk_leavetypecopy"), pk_leavetypecopy);
     
     UFDouble leavehour = (UFDouble)AppUtil.getAppAttr("leavehour");
     row.setValue(ds.nameToIndex("regleavehourcopy"), leavehour);
     
     UFLiteralDate leavebegindate = (UFLiteralDate)AppUtil.getAppAttr("leavebegindate");
     UFLiteralDate leaveenddate = (UFLiteralDate)AppUtil.getAppAttr("leaveenddate");
     UFDateTime leavebegintime = (UFDateTime)AppUtil.getAppAttr("leavebegintime");
     UFDateTime leaveendtime = (UFDateTime)AppUtil.getAppAttr("leaveendtime");
     
 
     if ((leavebegindate == null) && (leavebegintime != null)) {
       leavebegindate = new UFLiteralDate(leavebegintime.getDate().toDate());
     }
     if ((leaveenddate == null) && (leaveendtime != null)) {
       leaveenddate = new UFLiteralDate(leaveendtime.getDate().toDate());
     }
     if ((leavebegindate != null) && (leavebegintime == null)) {
       leavebegintime = new UFDateTime(leavebegindate.toDate());
     }
     if ((leaveenddate != null) && (leaveendtime == null)) {
       leaveendtime = new UFDateTime(leaveenddate.toDate());
     }
     
     row.setValue(ds.nameToIndex("regbegindatecopy"), leavebegindate);
     row.setValue(ds.nameToIndex("regenddatecopy"), leaveenddate);
     row.setValue(ds.nameToIndex("regbegintimecopy"), leavebegintime);
     row.setValue(ds.nameToIndex("regendtimecopy"), leaveendtime);
     
     if(pk_leavetype.equals("1002Z710000000021ZM1")){
         row.setValue(ds.nameToIndex("leavebegindate"), leavebegindate);
         row.setValue(ds.nameToIndex("leaveenddate"), leavebegindate);
         row.setValue(ds.nameToIndex("leavebegintime"), leavebegintime);
         row.setValue(ds.nameToIndex("leaveendtime"), leavebegintime);
         row.setValue(ds.nameToIndex("reallyleavehour"),Integer.valueOf(0) );
         row.setValue(ds.nameToIndex("differencehour"), leavehour );
     }else{
    	 row.setValue(ds.nameToIndex("leavebegindate"), leavebegindate);
    	 row.setValue(ds.nameToIndex("leaveenddate"), leaveenddate);
    	 row.setValue(ds.nameToIndex("leavebegintime"), leavebegintime);
    	 row.setValue(ds.nameToIndex("leaveendtime"), leaveendtime);
    	 row.setValue(ds.nameToIndex("reallyleavehour"), leavehour);
    	 row.setValue(ds.nameToIndex("differencehour"), Integer.valueOf(0));
     }
     
     
     
     AppLifeCycleContext.current().getApplicationContext().removeAppAttribute("leavebegindate");
     AppLifeCycleContext.current().getApplicationContext().removeAppAttribute("leaveenddate");
     AppLifeCycleContext.current().getApplicationContext().removeAppAttribute("leavebegintime");
     AppLifeCycleContext.current().getApplicationContext().removeAppAttribute("leaveendtime");
   }
   
   public void onAfterRowAdd(Dataset ds, Row row) {}
 }