package nc.impl.wa.func;

import nc.impl.wa.paydata.precacu.OtherDataSourcePreExcutor;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;
import nc.vo.wa.pub.WaLoginContext;


/**
 * 薪资外部数据源
 * 
 * @author daicy
 *
 */
@SuppressWarnings("serial")
public class WaOtherSourceData extends AbstractPreExcutorFormulaParse {


	/* (non-Javadoc)
	 * @see nc.vo.wa.paydata.IFormula#excute(java.lang.Object, nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public void excute(Object formula, WaLoginContext context) throws BusinessException {
		String[] arguments = getArguments(formula.toString());
		String pk_otherdatasource = arguments[0];

		IFormula excutor = new OtherDataSourcePreExcutor();
		excutor.excute(pk_otherdatasource, getContext());
	}
}
