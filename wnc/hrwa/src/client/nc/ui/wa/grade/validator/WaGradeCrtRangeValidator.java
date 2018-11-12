 package nc.ui.wa.grade.validator;
 
 import nc.bs.logging.Logger;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringHelper;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.wa.grade.model.WaGradeBillModel;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.grade.CrtVO;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVO;
import nc.vo.wa.grade.WaGradeVerVO;
import org.apache.commons.lang.StringUtils;
 
 public class WaGradeCrtRangeValidator implements nc.bs.uif2.validation.Validator
 {
   private AbstractUIAppModel model = null;
   
 
 
 
 
 
 
 
   public ValidationFailure validate(Object obj)
   {
     StringBuffer sbErrMessage = new StringBuffer();
     CrtVO[] crtVOs = null;
     if ((obj instanceof WaGradeVerVO)) {
       WaGradeVerVO gradeverVO = (WaGradeVerVO)obj;
       validateGradeVerInfo(gradeverVO, sbErrMessage);
       crtVOs = gradeverVO.getCrtVOs();
     } else if ((obj instanceof CrtVO[])) {
       crtVOs = (CrtVO[])obj;
     }
     if (crtVOs == null) {
       return null;
     }
     
     WaGradeVO waGradeVO = ((WaGradeBillModel)getModel()).getWaGradeVO();
     
 
     if (waGradeVO.getIsrange().booleanValue()) {
       validateCrtRange(crtVOs, sbErrMessage);
     }
     Logger.debug(sbErrMessage.toString());
     return new ValidationFailure(sbErrMessage.toString());
   }
   
 
 
 
 
 
 
   private String validateGradeVerInfo(WaGradeVerVO gradeverVO, StringBuffer sbErrMessage)
   {
     if (gradeverVO == null) {
       Logger.debug("请正确输入版本相关数据.");
       sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0149"));
       return sbErrMessage.toString();
     }
     if (gradeverVO.getGradever_num() == null) {
       Logger.debug("[版本号]");
       sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0150"));
     }
     if (StringUtils.isBlank(gradeverVO.getGradever_name())) {
       Logger.debug("[版本名称]");
       sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0151"));
     }
     if ((gradeverVO.getVer_create_date() == null) || (StringUtils.isBlank(gradeverVO.getVer_create_date().toStdString())))
     {
       Logger.debug("[版本创建日期]");
       sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0152"));
     }
     if (StringUtils.isBlank(sbErrMessage.toString())) {
       return null;
     }
     sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0153"));
     return sbErrMessage.toString();
   }
   
 
 
 
 
 
   private String validateCrtRange(CrtVO[] crtVOs, StringBuffer sbErrMessage)
   {
     double dblMin = 0.0D;
     double dblBasic = 0.0D;
     double dblMax = 0.0D;
     
     for (int i = 0; i < crtVOs.length; i++) {
       WaCriterionVO[] criterionvos = (WaCriterionVO[])crtVOs[i].getVecData().toArray(new WaCriterionVO[0]);
       for (int j = 0; j < criterionvos.length; j++) {
    	 //但强 数据解密 2018-3-3 22:42:32 start
         dblMin = SalaryDecryptUtil.decrypt(criterionvos[j].getMin_value().doubleValue());
         dblBasic = SalaryDecryptUtil.decrypt(criterionvos[j].getCriterionvalue().doubleValue());
         dblMax = SalaryDecryptUtil.decrypt(criterionvos[j].getMax_value().doubleValue());
         //但强 数据解密 2018-3-3 22:42:32 end
         if (dblMin > dblBasic) {
           Logger.debug(i + 1 + "行 " + "第" + (j + 1) + "列的下限值" + StringHelper.convertDoubleValue(dblMin, 3) + " 不能大于 基准值 " + StringHelper.convertDoubleValue(dblBasic, 3));
           
           sbErrMessage.append(i + 1 + ResHelper.getString("60130paystd", "060130paystd0154") + (j + 1) + ResHelper.getString("60130paystd", "060130paystd0155") + StringHelper.convertDoubleValue(dblMin, 3) + ResHelper.getString("60130paystd", "060130paystd0156") + StringHelper.convertDoubleValue(dblBasic, 3) + "]  \n");
 
         }
         else if (dblBasic > dblMax) {
           Logger.debug(i + 1 + "行 " + "第" + (j + 1) + "列的基准值" + StringHelper.convertDoubleValue(dblBasic, 3) + " 不能大于 上限值 " + StringHelper.convertDoubleValue(dblMax, 3));
           
           sbErrMessage.append(i + 1 + ResHelper.getString("60130paystd", "060130paystd0157") + (j + 1) + ResHelper.getString("60130paystd", "060130paystd0158") + StringHelper.convertDoubleValue(dblBasic, 3) + ResHelper.getString("60130paystd", "060130paystd0159") + StringHelper.convertDoubleValue(dblMax, 3) + "]  \n");
         }
       }
     }
     
 
     if (StringUtils.isBlank(sbErrMessage.toString())) {
       return null;
     }
     
     return sbErrMessage.toString();
   }
   
   public AbstractUIAppModel getModel() {
     return model;
   }
   
   public void setModel(AbstractUIAppModel model) {
     this.model = model;
   }
 }