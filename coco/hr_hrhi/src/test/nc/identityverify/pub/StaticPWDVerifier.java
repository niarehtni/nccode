/*    */ package nc.identityverify.pub;
/*    */ 
/*    */ import nc.identityverify.itf.AbstractIdentityVerifier;
/*    */ import nc.identityverify.vo.AuthenSubject;
/*    */ import nc.vo.sm.UserVO;
/*    */ 
/*    */ public class StaticPWDVerifier extends AbstractIdentityVerifier
/*    */ {
/*    */   public StaticPWDVerifier() {}
/*    */   
/*    */   public int verify(AuthenSubject subject, UserVO user) throws Exception
/*    */   {
/* 13 */     if (user != null) {
/* 14 */       if (nc.vo.uap.rbac.util.RbacUserPwdUtil.checkUserPassword(user, subject.getUserPWD())) {
/* 15 */         return 1;
/*    */       }
/*    */       
/* 18 */       return 1;
/*    */     }
/*    */     
/* 21 */     return 1;
/*    */   }
/*    */ }

/* Location:           D:\NCHOME\nc65\modules\baseapp\lib\pubbaseapp_apploginLevel-1.jar
 * Qualified Name:     nc.identityverify.pub.StaticPWDVerifier
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.0.1
 */