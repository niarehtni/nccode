package nc.ui.ta.item.view;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.ta.IViewOrderQueryService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.hr.formula.HRFormulaItem;
import nc.ui.hr.formula.tabbuilder.HRNatualOrderFormulaTabBuilder;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.pub.BusinessException;
import nc.vo.pub.formulaedit.FormulaItem;
import nc.vo.ta.vieworder.ViewOrderVO;
import nc.vo.ta.vieworder.ViewOrderVOWithItemTypeName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 添加由脚本公式解析成业务函数的功能
 * @author wangdca
 *
 */
@SuppressWarnings("serial")
public class HRTANatualOrderFormulaTabBuilder extends
		HRNatualOrderFormulaTabBuilder implements AppEventListener {
	
	ViewOrderVO[] itemVOs=null;//日/月报项目以及休假出差加班停工待料类别
	private AbstractUIAppModel model;
	private String oldPkorg = null;//切换组织要重新查询项目

	@Override
    public String preConvert(String formula){
		formula= super.preConvert(formula);
		ViewOrderVO[] items = getItemVOs();
		if(ArrayUtils.isEmpty(items))
         	return formula;
	    //替换日月报、出休停加等项目
        for(ViewOrderVO item:items){
        	ViewOrderVOWithItemTypeName newVO = new ViewOrderVOWithItemTypeName(item);
        	String voname= "["+newVO.toString().replace("-", ".")+"]";
    		while(formula.contains(voname)){
    			formula = formula.replace( voname,newVO.getField_name());
    		}
        }
        if(formula.contains("directsum")){
        	 String dayitemname = ResHelper.getString("hr_formula_config","ta_formula_000500")/*@res "日报项目"*/;
             for(ViewOrderVO item:items){
            	 String itemName = "["+dayitemname+"."+item.getMultilangName()+"]";
            	 formula = org.springframework.util.StringUtils.replace(formula, itemName, item.getField_name());
            	 
            	 ViewOrderVOWithItemTypeName newVO = new ViewOrderVOWithItemTypeName(item);
             	 formula = formula.replaceAll(newVO.toString(),newVO.getField_name() );
             }
             for(ViewOrderVO item:items){
            	 String itemName = "["+dayitemname+"-"+item.getMultilangName()+"]";
            	 formula = org.springframework.util.StringUtils.replace(formula, itemName, item.getField_name());
            	 
            	 ViewOrderVOWithItemTypeName newVO = new ViewOrderVOWithItemTypeName(item);
             	 formula = formula.replaceAll(newVO.toString(),newVO.getField_name() );
             }
        }
        return formula;
    }
	/*
     * 将脚本表达式转换为业务表达式
     * HR中不需要进行这个转换，因为涉及到公式的地方都将业务表达式存到数据库了，因此这个方法应该不会调到
     * (non-Javadoc)
     * @see nc.ui.pub.formula.dialog.IConvertor#postConvert(java.lang.String)
     */
    @Override
    public String postConvert(String formula)
    {
    	//考勤月报新增的统计函数
    	if(formula.startsWith("directmonthsumcount")){
			if(formula.toString().contains("NORMAL")){
				formula = formula.replaceAll("NORMAL", "平日");
			}
			if(formula.toString().contains("OFFDAY")){
				formula = formula.replaceAll("OFFDAY", "休息日");
			}
			if(formula.toString().contains("HOLIDAYS")){
				formula = formula.replaceAll("HOLIDAYS", "例假日");
			}
			if(formula.toString().contains("NATIONALDAY")){
				formula = formula.replaceAll("NATIONALDAY", "国假日");
			}
			if(formula.toString().contains("PERIOD_TOSALARY")){
				formula = formula.replaceAll("PERIOD_TOSALARY", "本期加班转薪时数");
			}
			if(formula.toString().contains("OTHER_TOSALARY")){
				formula = formula.replaceAll("OTHER_TOSALARY", "往期加班转薪时数");
			}
			if(formula.toString().contains("PERIOD_TOREST")){
				formula = formula.replaceAll("PERIOD_TOREST", "本期加班转休时数");
			}
			if(formula.toString().contains("TOTAL")){
				formula = formula.replaceAll("TOTAL", "全部时数");
    		}
    	} else {
    		
    		ViewOrderVO[] items = getItemVOs();
    		if(ArrayUtils.isEmpty(items))
    			return formula;
    		//替换日月报、出休停加等项目
    		Arrays.sort(items);
    		for(ViewOrderVO item:items){
    			ViewOrderVOWithItemTypeName newVO = new ViewOrderVOWithItemTypeName(item);
    			formula = formula.replaceAll(newVO.getField_name(), newVO.toString());
    		}
    		
    	}
    	for (FormulaItem item : getFormulaItems())
		{
			HRFormulaItem hrItem = (HRFormulaItem) item;
			formula = replaceFuncCodeByName(formula, hrItem);
			
		}
        return formula;
    }
    
//    @Override
//    public String preConvert(String formula) {
//    	 formula = super.preConvert(formula);
//    	 
//    	 //月报汇总项目中，条件设置的日报项目信息还是业务表达式，没有解析成公式函数,导致验证和计算错误 2015-05-12修改
//    	 //Invaild syntax of formula! Formula is directsum("f_i_10",0,[日报项目.日报日期]<=[日报项目.调薪日期])
//    	 if(!formula.contains("directsum"))
//    		 return formula;
//    	 ViewOrderVO[] items = getItemVOs();
//         if(ArrayUtils.isEmpty(items))
//         	return formula;
//         String dayitemname = ResHelper.getString("hr_formula_config","ta_formula_000500")/*@res "日报项目"*/;
//         for(ViewOrderVO item:items){
//        	 String itemName = "["+dayitemname+"."+item.getMultilangName()+"]";
//        	 formula = org.springframework.util.StringUtils.replace(formula, itemName, item.getField_name());
//        	 
//        	 ViewOrderVOWithItemTypeName newVO = new ViewOrderVOWithItemTypeName(item);
//         	 formula = formula.replaceAll(newVO.toString(),newVO.getField_name() );
//         }
//         return formula;
//    }
	
//    /**
//     * 替换公式
//     * @param formula
//     * @param hrItem
//     * @return
//     */
//	 public String replaceFuncCode(String formula,HRFormulaItem hrItem){
//	    	String recode = reduceBracket(hrItem.getCode());
//	    	int i = formula.indexOf(recode);
//	    	if(i<0)
//	    		return formula;
//	    	String sub = formula.substring(i);
//	    	int k = sub.indexOf("(");
//	    	int j = sub.indexOf(")");
//	    	String fun = sub.substring(0,j+1);
//	    	String re = formula;
//	    	if(j==k+1){//不含参数的函数
//	    		String des = "@" + hrItem.getInputSigRes()+ "@";
//	    		re = formula.replace(fun, des);
//	    	}else{
//		    	String funk = fun.substring(k,j+1);
//		    	String des = "@" + hrItem.getInputSigRes() + funk + "@";
//		    	re = formula.replace(fun, des);
//	    	}
//	    	return replaceFuncCode(re, hrItem);
//	    }
//	 
	 public String replaceFuncCodeByName(String formula,HRFormulaItem hrItem){
		 if(StringUtils.isBlank(formula))
			 return formula;
		String recode = reduceBracket(hrItem.getCode());
	    int index = formula.indexOf(recode);
	    if(index<0)
	    	return formula;//没有该函数直接返回
	    
		// 左括号的位置，被单引号和双引号包含的不算
	    Integer[] leftBracketIdx = nc.hr.utils.StringHelper.findIndexNormal(formula, '(');
	    Integer[] rightBracketIdx = nc.hr.utils.StringHelper.findIndexNormal(formula, ')');
	    if (ArrayUtils.isEmpty(leftBracketIdx) || ArrayUtils.isEmpty(rightBracketIdx))
	    	 return formula.replaceAll(recode, hrItem.getInputSigRes());
	    
        int indexLeft = index + recode.length();//函数左括号的起始位置
        if(!ArrayUtils.contains(leftBracketIdx, indexLeft)){
        	formula = formula.replaceFirst(recode, hrItem.getInputSigRes());
        }
        int indexRight = -1;
        int stack = -1;
        //查找对应的右括号的位置
        for(int i = indexLeft;i<formula.length();i++){
        	if(i == indexLeft){
        		stack = 1;
        	}
        	if(ArrayUtils.contains(leftBracketIdx, i) && i != indexLeft)
        		stack ++ ;
        	if(ArrayUtils.contains(rightBracketIdx, i))
        		stack -- ;
        	if(stack == 0){
        		indexRight = i;
        		break;
        	}
        }
        //
        if(indexRight != -1){
        	String inBracket = formula.substring(indexLeft, indexRight+1);//公式中函数括号内容
        	String func = formula.substring(index, indexRight+1);//公式中函数内容func(...)
        	String des = "@" + hrItem.getInputSigRes() + inBracket + "@";//替换的业务内容
        	formula =  formula.replace(func, des);
        }
        return replaceFuncCodeByName(formula, hrItem);        
	 }

	 /**
	  * 查询所有的日/月报项目以及休假出差加班停工待料类别
	  * @return
	  */
	public ViewOrderVO[] getItemVOs() {
		if(ArrayUtils.isEmpty(itemVOs) || !getModel().getContext().getPk_org().equals(oldPkorg)){
			oldPkorg = getModel().getContext().getPk_org();
			try {
				itemVOs = NCLocator.getInstance().lookup(IViewOrderQueryService.class).queryAllViewOrder(getModel().getContext()
						, ViewOrderVO.FUN_TYPE_DAY, ViewOrderVO.REPORT_TYPE_PSN);
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
			}
		}
			
		return itemVOs;
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	@Override
	public void handleEvent(AppEvent e) {
		if(e.getType().equals(AppEventConst.DATA_DELETED)||e.getType().equals(AppEventConst.DATA_INSERTED)){
			//新增和删除时，更新缓存数据。
			itemVOs = null;
		}
	}
}
