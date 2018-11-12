/*    */ package nc.impl.ta.leave;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import nc.vo.pub.lang.UFDateTime;
/*    */ import nc.vo.ta.leave.LeaveCommonVO;
/*    */ import nc.vo.ta.timeitem.LeaveTypeCopyVO;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LeaveComparator<T extends LeaveCommonVO>
/*    */   implements Comparator<T>
/*    */ {
/* 19 */   Map<String, Integer> indexMap = null;
/*    */   
/*    */   public int compare(T o1, T o2)
/*    */   {
/* 23 */     if (o1.getPk_timeitem().equals(o2.getPk_timeitem())) {
/* 24 */       return o1.getLeavebegintime().compareTo(o2.getLeavebegintime());
/*    */     }
/*    */     
/* 27 */     return ((Integer)indexMap.get(o1.getPk_timeitem())).intValue() - ((Integer)indexMap.get(o2.getPk_timeitem())).intValue();
/*    */   }
/*    */   
/*    */   public LeaveComparator(LeaveTypeCopyVO[] sortedTypeVOs) {
/* 31 */     indexMap = new HashMap();
/* 32 */     for (int i = 0; i < sortedTypeVOs.length; i++) {
/* 33 */       indexMap.put(sortedTypeVOs[i].getPk_timeitem(), Integer.valueOf(i));
/*    */     }
/*    */   }
/*    */ }

/* Location:           E:\TaiWan\home\home\modules\hrta\META-INF\lib\hrta_hrtaleave.jar
 * Qualified Name:     nc.impl.ta.leave.LeaveComparator
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */