package nc.ui.wa.grade.validator;

import java.util.ArrayList;

import nc.bs.logging.Logger;
import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.hr.utils.ResHelper;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.hr.comp.sort.UFDoubleCompare;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.wa.grade.model.WaGradeBillModel;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.grade.AggWaGradeVO;
import nc.vo.wa.grade.CrtVO;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVO;
import nc.vo.wa.grade.WaGradeVerVO;

import org.apache.commons.lang.StringUtils;










public class WaGradeCrtValueValidator
  implements Validator
{
  private AbstractUIAppModel model = null;
  private final UFDoubleCompare compare = new UFDoubleCompare();
  
  private UFDouble dblBefBasicValue = new UFDouble(0);
  
  private UFDouble dblAftBasicValue = new UFDouble(0);
  
  private UFDouble dblUpBasicValues = new UFDouble(0);
  
  private UFDouble dblDownBasicValues = new UFDouble(0);
  
  private Integer iflddecimal = Integer.valueOf(0);
  






  public ValidationFailure validate(Object obj)
  {
    resetValue();
    
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
    





    if (crtVOs.length > 0) {
      WaCriterionVO[] criterionvos = (WaCriterionVO[])crtVOs[0].getVecData().toArray(new WaCriterionVO[0]);
      
      dblUpBasicValues = criterionvos[0].getCriterionvalue();
    }
    AggWaGradeVO aggvo = (AggWaGradeVO)((WaGradeBillModel)getModel()).getSelectedData();
    
    if ((aggvo != null) && (aggvo.getParentVO() != null) && (aggvo.getParentVO().getIflddecimal() != null)) {
      iflddecimal = aggvo.getParentVO().getIflddecimal();
    }
    
    if ((1 == waGradeVO.getPrmlv_money_sort().intValue()) && (1 == waGradeVO.getSeclv_money_sort().intValue()))
    {
      validatePrmUpSecUp(crtVOs, sbErrMessage);

    }
    else if ((1 == waGradeVO.getPrmlv_money_sort().intValue()) && (2 == waGradeVO.getSeclv_money_sort().intValue()))
    {
      validatePrmUpSecDown(crtVOs, sbErrMessage);

    }
    else if ((2 == waGradeVO.getPrmlv_money_sort().intValue()) && (2 == waGradeVO.getSeclv_money_sort().intValue()))
    {
      validatePrmDownSecDown(crtVOs, sbErrMessage);

    }
    else if ((2 == waGradeVO.getPrmlv_money_sort().intValue()) && (1 == waGradeVO.getSeclv_money_sort().intValue()))
    {
      validatePrmDownSecUp(crtVOs, sbErrMessage);
    }
    
    if (StringUtils.isBlank(sbErrMessage.toString())) {
      return null;
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
  


















































  private String validatePrmUpSecUp(CrtVO[] crtVOs, StringBuffer sbErrMessage)
  {
    WaCriterionVO[][] criterionvos = new WaCriterionVO[crtVOs.length][];
    for (int i = 0; i < crtVOs.length; i++) {
      criterionvos[i] = ((WaCriterionVO[])crtVOs[i].getVecData().toArray(new WaCriterionVO[0]));
    }
    for (int i = 0; i < criterionvos.length; i++)
    {


      for (int j = 0; j < criterionvos[i].length; j++) {
        dblAftBasicValue = new UFDouble(SalaryDecryptUtil.decrypt((dblDownBasicValues = criterionvos[i][j].getCriterionvalue()).doubleValue()));

        if (i != 0) {
          dblUpBasicValues = new UFDouble(SalaryDecryptUtil.decrypt(criterionvos[(i - 1)][j].getCriterionvalue().doubleValue()));
          if (compare.lessThan(dblDownBasicValues, dblUpBasicValues)) {
            sbErrMessage.append(j + 1 + ResHelper.getString("60130paystd", "060130paystd0218") + i + ResHelper.getString("60130paystd", "060130paystd0160") + dblUpBasicValues.setScale(iflddecimal.intValue(), 4) + ResHelper.getString("60130paystd", "060130paystd0161") + (i + 1) + ResHelper.getString("60130paystd", "060130paystd0160") + dblDownBasicValues.setScale(iflddecimal.intValue(), 4) + "]");
            

            sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0163") + " \n ");
          }
        }
        


        if (j != 0) {
          dblBefBasicValue = new UFDouble(SalaryDecryptUtil.decrypt(criterionvos[i][(j - 1)].getCriterionvalue().doubleValue()));
          
          if (compare.lessThan(dblAftBasicValue, dblBefBasicValue)) {
            sbErrMessage.append(i + 1 + ResHelper.getString("60130paystd", "060130paystd0164") + j + ResHelper.getString("60130paystd", "060130paystd0165") + dblBefBasicValue.setScale(iflddecimal.intValue(), 4) + ResHelper.getString("60130paystd", "060130paystd0161") + (j + 1) + ResHelper.getString("60130paystd", "060130paystd0166") + dblAftBasicValue.setScale(iflddecimal.intValue(), 4) + "]");
            

            sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0167") + " \n ");
          }
        }
      }
    }
    

    if (StringUtils.isBlank(sbErrMessage.toString())) {
      return null;
    }
    
    return sbErrMessage.toString();
  }
  










  private String validatePrmUpSecDown(CrtVO[] crtVOs, StringBuffer sbErrMessage)
  {
    WaCriterionVO[][] criterionvos = new WaCriterionVO[crtVOs.length][];
    for (int i = 0; i < crtVOs.length; i++) {
      criterionvos[i] = ((WaCriterionVO[])crtVOs[i].getVecData().toArray(new WaCriterionVO[0]));
    }
    for (int i = 0; i < criterionvos.length; i++)
    {


      for (int j = 0; j < criterionvos[i].length; j++) {
        dblAftBasicValue = new UFDouble(SalaryDecryptUtil.decrypt((dblDownBasicValues = criterionvos[i][j].getCriterionvalue()).doubleValue()));
        

        if (i != 0) {
          dblUpBasicValues = new UFDouble(SalaryDecryptUtil.decrypt(criterionvos[(i - 1)][j].getCriterionvalue().doubleValue()));
          if (compare.lessThan(dblDownBasicValues, dblUpBasicValues)) {
            sbErrMessage.append(j + 1 + ResHelper.getString("60130paystd", "060130paystd0218") + i + ResHelper.getString("60130paystd", "060130paystd0160") + dblUpBasicValues.setScale(iflddecimal.intValue(), 4) + ResHelper.getString("60130paystd", "060130paystd0161") + (i + 1) + ResHelper.getString("60130paystd", "060130paystd0160") + dblDownBasicValues.setScale(iflddecimal.intValue(), 4) + "]");
            

            sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0168") + "  \n");
          }
        }
        

        if (j != 0) {
          dblBefBasicValue = new UFDouble(SalaryDecryptUtil.decrypt(criterionvos[i][(j - 1)].getCriterionvalue().doubleValue()));
          
          if (compare.lessThan(dblBefBasicValue, dblAftBasicValue)) {
            sbErrMessage.append(i + 1 + ResHelper.getString("60130paystd", "060130paystd0164") + j + ResHelper.getString("60130paystd", "060130paystd0165") + dblBefBasicValue.setScale(iflddecimal.intValue(), 4) + ResHelper.getString("60130paystd", "060130paystd0161") + (j + 1) + ResHelper.getString("60130paystd", "060130paystd0166") + dblAftBasicValue.setScale(iflddecimal.intValue(), 4) + "]");
            

            sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0169") + "  \n");
          }
        }
      }
    }
    


    if (StringUtils.isBlank(sbErrMessage.toString())) {
      return null;
    }
    
    return sbErrMessage.toString();
  }
  










  private String validatePrmDownSecDown(CrtVO[] crtVOs, StringBuffer sbErrMessage)
  {
    WaCriterionVO[][] criterionvos = new WaCriterionVO[crtVOs.length][];
    for (int i = 0; i < crtVOs.length; i++) {
      criterionvos[i] = ((WaCriterionVO[])crtVOs[i].getVecData().toArray(new WaCriterionVO[0]));
    }
    for (int i = 0; i < criterionvos.length; i++)
    {


      for (int j = 0; j < criterionvos[i].length; j++) {
        dblAftBasicValue = new UFDouble(SalaryDecryptUtil.decrypt((dblDownBasicValues = criterionvos[i][j].getCriterionvalue()).doubleValue()));
        

        if (i != 0) {
          dblUpBasicValues = new UFDouble(SalaryDecryptUtil.decrypt(criterionvos[(i - 1)][j].getCriterionvalue().doubleValue()));
          
          if (compare.lessThan(dblUpBasicValues, dblDownBasicValues)) {
            sbErrMessage.append(j + 1 + ResHelper.getString("60130paystd", "060130paystd0218") + i + ResHelper.getString("60130paystd", "060130paystd0160") + dblUpBasicValues.setScale(iflddecimal.intValue(), 4) + ResHelper.getString("60130paystd", "060130paystd0161") + (i + 1) + ResHelper.getString("60130paystd", "060130paystd0160") + dblDownBasicValues.setScale(iflddecimal.intValue(), 4) + "]");
            

            sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0170") + "  \n");
          }
        }
        


        if (j != 0) {
          dblBefBasicValue = new UFDouble(SalaryDecryptUtil.decrypt(criterionvos[i][(j - 1)].getCriterionvalue().doubleValue()));
          
          if (compare.lessThan(dblBefBasicValue, dblAftBasicValue)) {
            sbErrMessage.append(i + 1 + ResHelper.getString("60130paystd", "060130paystd0164") + j + ResHelper.getString("60130paystd", "060130paystd0165") + dblBefBasicValue.setScale(iflddecimal.intValue(), 4) + ResHelper.getString("60130paystd", "060130paystd0161") + (j + 1) + ResHelper.getString("60130paystd", "060130paystd0166") + dblAftBasicValue.setScale(iflddecimal.intValue(), 4) + "]");
            

            sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0169") + " \n ");
          }
        }
      }
    }
    

    if (StringUtils.isBlank(sbErrMessage.toString())) {
      return null;
    }
    
    return sbErrMessage.toString();
  }
  










  private String validatePrmDownSecUp(CrtVO[] crtVOs, StringBuffer sbErrMessage)
  {
    WaCriterionVO[][] criterionvos = new WaCriterionVO[crtVOs.length][];
    for (int i = 0; i < crtVOs.length; i++) {
      criterionvos[i] = ((WaCriterionVO[])crtVOs[i].getVecData().toArray(new WaCriterionVO[0]));
    }
    for (int i = 0; i < criterionvos.length; i++)
    {


      for (int j = 0; j < criterionvos[i].length; j++) {
        dblAftBasicValue = new UFDouble(SalaryDecryptUtil.decrypt((dblDownBasicValues = criterionvos[i][j].getCriterionvalue()).doubleValue()));
        

        if (i != 0) {
          dblUpBasicValues = new UFDouble(SalaryDecryptUtil.decrypt(criterionvos[(i - 1)][j].getCriterionvalue().doubleValue()));
          if (compare.lessThan(dblUpBasicValues, dblDownBasicValues)) {
            sbErrMessage.append(j + 1 + ResHelper.getString("60130paystd", "060130paystd0218") + i + ResHelper.getString("60130paystd", "060130paystd0160") + dblUpBasicValues.setScale(iflddecimal.intValue(), 4) + ResHelper.getString("60130paystd", "060130paystd0161") + (i + 1) + ResHelper.getString("60130paystd", "060130paystd0160") + dblDownBasicValues.setScale(iflddecimal.intValue(), 4) + "]");
            

            sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0170") + " \n ");
          }
        }
        
        if (j != 0) {
          dblBefBasicValue = new UFDouble(SalaryDecryptUtil.decrypt(criterionvos[i][(j - 1)].getCriterionvalue().doubleValue()));
          
          if (compare.lessThan(dblAftBasicValue, dblBefBasicValue)) {
            sbErrMessage.append(i + 1 + ResHelper.getString("60130paystd", "060130paystd0164") + j + ResHelper.getString("60130paystd", "060130paystd0165") + dblBefBasicValue.setScale(iflddecimal.intValue(), 4) + ResHelper.getString("60130paystd", "060130paystd0161") + (j + 1) + ResHelper.getString("60130paystd", "060130paystd0166") + dblAftBasicValue.setScale(iflddecimal.intValue(), 4) + "]");
            

            sbErrMessage.append(ResHelper.getString("60130paystd", "060130paystd0167") + " \n ");
          }
        }
      }
    }
    

    if (StringUtils.isBlank(sbErrMessage.toString())) {
      return null;
    }
    
    return sbErrMessage.toString();
  }
  

  private void resetValue()
  {
    dblBefBasicValue = new UFDouble(0).setScale(iflddecimal.intValue(), 4);
    
    dblAftBasicValue = new UFDouble(0).setScale(iflddecimal.intValue(), 4);
    
    dblUpBasicValues = new UFDouble(0).setScale(iflddecimal.intValue(), 4);
    
    dblDownBasicValues = new UFDouble(0).setScale(iflddecimal.intValue(), 4);
  }
  
  public AbstractUIAppModel getModel() {
    return model;
  }
  
  public void setModel(AbstractUIAppModel model) {
    this.model = model;
  }
}