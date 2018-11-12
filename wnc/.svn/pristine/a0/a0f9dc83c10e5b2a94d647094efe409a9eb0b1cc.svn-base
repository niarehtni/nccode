package nc.impl.wa.func;

import nc.ui.wa.pub.WADelegator;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.pub.PeriodStateVO;

/**
 * 
 * @author: suihang
 * @date: 2012-11-28
 * @since: V63
 */
public class PrePeriodEndDate extends AbstractWAFormulaParse {

	/**
	 * 
	 * @author suihang on 2012-11-28
	 * @see @see nc.impl.wa.func.AbstractWAFormulaParse#getReplaceStr(java.lang.String)
	 */
	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		PeriodStateVO periodStateVO = WADelegator.getWaPubService().getPrePeriodVO(getContext().getWaLoginVO());
		if(periodStateVO==null){
			fvo.setReplaceStr(" '' ");
			return fvo;
		}
		UFLiteralDate endDay =periodStateVO.getCenddate();

		fvo.setReplaceStr(" '" + endDay.toStdString() + "' ");

		return fvo;
	}

}
