/*    */ package nc.impl.hr.formula.parser;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import nc.hr.utils.ResHelper;
/*    */ import nc.vo.hr.func.FunctionVO;
/*    */ import nc.vo.pub.BusinessException;
/*    */ import nc.vo.uif2.LoginContext;
/*    */ import nc.vo.wa.formula.HrWaXmlReader;
/*    */ import nc.vo.wa.pub.WaLoginContext;
/*    */ import nc.vo.wa.pub.WaLoginVO;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WaFormulaProcessParser
/*    */ {
/* 20 */   private static String error = ResHelper.getString("6013commonbasic", "06013commonbasic0034");
/*    */   
/*    */   public static IFormulaParser getFormulaParse(FunctionVO functionVO)
/*    */     throws BusinessException
/*    */   {
/* 25 */     IFormulaParser formulaParse = null;
/*    */     
/* 27 */     String parse = functionVO.getProcess();
/*    */     
/*    */     try
/*    */     {
/* 31 */       formulaParse = (IFormulaParser)Class.forName(parse).newInstance();
/*    */     }
/*    */     catch (InstantiationException e)
/*    */     {
/* 35 */       throw new BusinessException(error);
/*    */     }
/*    */     catch (IllegalAccessException e)
/*    */     {
/* 39 */       throw new BusinessException(error);
/*    */     }
/*    */     catch (ClassNotFoundException e)
/*    */     {
/* 43 */       throw new BusinessException(error + parse);
/*    */     }
/*    */     
/* 46 */     return formulaParse;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static String parse(String formula, LoginContext context)
/*    */     throws BusinessException
/*    */   {
/* 57 */     String pk_country = "0001Z010000000079UJJ";
/*    */     
/* 59 */     if ((context instanceof WaLoginContext)) {
/* 60 */       pk_country = ((WaLoginContext)context).getWaLoginVO().getPk_country();
/*    */     }
/* 62 */     Map<String, FunctionVO> hashMap = HrWaXmlReader.getInstance().getFormulaParserByzonePK(pk_country);
/*    */     
/* 64 */     Iterator<String> iterator = hashMap.keySet().iterator();
/*    */     
/* 66 */     while (iterator.hasNext())
/*    */     {
/* 68 */       String key = (String)iterator.next();
/* 69 */       FunctionVO functionVO = (FunctionVO)hashMap.get(key);
/* 70 */       if (FormulaParseHelper.isExist(formula, functionVO))
/*    */       {
/* 72 */         IFormulaParser formulaParse = getFormulaParse(functionVO);
/* 73 */         formula = formulaParse.parse(context.getPk_org(), formula, new Object[] { context, functionVO });
/*    */       }
/*    */     }
/*    */     
/* 77 */     return formula;
/*    */   }
/*    */ }

/* Location:           E:\TaiWan\home\home\modules\hrwa\META-INF\lib\hrwa_basicsetting.jar
 * Qualified Name:     nc.impl.hr.formula.parser.WaFormulaProcessParser
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */