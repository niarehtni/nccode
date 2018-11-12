package nc.impl.wa.func;


import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.IFormula;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.dataitem.pub.DataVOUtils;

import org.apache.commons.lang.ArrayUtils;

import nc.hr.utils.ResHelper;
import nc.vo.hr.formula.FormulaXmlHelper;
public class AvgSalaryFunc extends AbstractPreExcutorFormulaParse{
	private static final long serialVersionUID = 1L;
	public AvgSalaryFunc() {}
	public FunctionReplaceVO getReplaceStr(String formula)
			     throws BusinessException
			   {
			     excute(formula, getContext());
			    
			     FunctionReplaceVO fvo = new FunctionReplaceVO();
			    
			     boolean digits = true;
			     Object object = getContext().getInitData();
			     if ((object != null) && ((object instanceof WaClassItemVO))) {
			       WaClassItemVO itemVO = (WaClassItemVO)object;
			       digits = DataVOUtils.isDigitsAttribute(itemVO.getItemkey());
			     }
			     fvo.setAliTableName("wa_cacu_data");
			    if (digits) {
			       fvo.setReplaceStr("coalesce(wa_cacu_data.cacu_value, 0)");
			      } else {
			       fvo.setReplaceStr("wa_cacu_data.char_value");
			      }
			     return fvo;
			    }

	@Override
	public void excute(Object formula, WaLoginContext context)
			throws BusinessException {
		String[] arguments = getArguments(formula.toString());

		IFormula excutor = new AvgSalaryFormulaExcutor();
		excutor.excute(arguments, getContext());
	}

}
