package nc.impl.wa.classitem;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.impl.hr.formula.parser.FormulaParseHelper;
import nc.impl.hr.formula.parser.IFormulaParser;
import nc.impl.hr.formula.parser.WaFormulaProcessParser;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.formula.HrWaXmlReader;
import nc.vo.wa.formula.IFormulaAli;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

public class WaFormulaXmlImpl extends WaFormulaProcessParser {
	public static List<String> getAliItemKeys(WaClassItemVO itemVO)
			throws BusinessException {
		List<String> aliKeys = new LinkedList();

		WaLoginContext context = new WaLoginContext();
		WaLoginVO loginVO = new WaLoginVO();
		loginVO.setPk_wa_class(itemVO.getPk_wa_class());
		loginVO.setPk_country(getCountry(loginVO.getPk_wa_class()));
		PeriodStateVO periodStateVO = new PeriodStateVO();
		periodStateVO.setCyear(itemVO.getCyear());
		periodStateVO.setCperiod(itemVO.getCperiod());
		loginVO.setPeriodVO(periodStateVO);
		context.setWaLoginVO(loginVO);

		Map<String, FunctionVO> hashMap = HrWaXmlReader.getInstance()
				.getFormulaParserByzonePK(loginVO.getPk_country());
		Iterator<String> iterator = hashMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			FunctionVO functionVO = (FunctionVO) hashMap.get(key);
			if (FormulaParseHelper.isExist(itemVO.getVformula(), functionVO)) {
				IFormulaParser formulaParse = getFormulaParse(functionVO);

				if ((formulaParse instanceof IFormulaAli)) {
					IFormulaAli formulaAli = (IFormulaAli) formulaParse;
					String[] strings = formulaAli.getAliItemKeys(itemVO,
							context, functionVO);
					if (strings != null) {
						for (String itemKey : strings) {
							aliKeys.add(itemKey);
						}
					}
				}
			}
		}
		return aliKeys;
	}

	private static String getCountry(String pk_wa_class)
			throws BusinessException {
		BaseDAO baseDao = new BaseDAO();
		WaClassVO vo = (WaClassVO) baseDao.retrieveByPK(WaClassVO.class,
				pk_wa_class);
		if (vo == null) {
			return null;
		} else {
			return vo.getPk_country();
		}
	}
}
