package nc.impl.wa.func;

import nc.impl.wa.func.scenfeebaseexcute.ScenarioFeeBaseExcutor;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;

/**
 * 需新增薪資函數，(日期1 , 日期2 , 日是否直接進位成月) Ex 日期1 2015-08-03 日期2 2018-11-30 否 則得到
 * 3年3個月28天， 轉換成年的計算方式為 3+((3*30+28)/30/12)=3.327777777777不須在函數中捨位 如果參數為
 * 是，則得到3年4個月 轉換成年的計算方式為 3+(4/12)
 * 
 * 日期一和日期二可以是字符型或日期型
 * 
 * 
 * @author hepingyang
 * 
 */
public class ScenarioFeeBaseParse extends AbstractWAFormulaParse {

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr(" wa_cacu_data.cacu_value ");
		IFormula excutor = new ScenarioFeeBaseExcutor();
		((AbstractWAFormulaParse) excutor).setFunctionVO(getFunctionVO());
		excutor.excute(formula, getContext());
		return fvo;
	}
}
