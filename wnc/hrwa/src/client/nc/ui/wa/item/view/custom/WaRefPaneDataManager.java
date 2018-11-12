package nc.ui.wa.item.view.custom;

import java.awt.Component;

import nc.bs.logging.Logger;
import nc.ui.hr.itemsource.view.RefPaneDataManager;
import nc.ui.pub.beans.UIRefPane;
import nc.vo.hr.formula.FormulaXmlHelper;
import nc.vo.hr.formula.FunctionKey;
import nc.vo.hr.func.FunctionVO;
import nc.vo.hr.itemsource.ItemPropertyConst;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.wa.formula.WaFormulaXmlHelper;

import org.apache.commons.lang.StringUtils;

/***************************************************************************
 * <br>
 * Created on 2012-8-8 ÏÂÎç1:26:33<br>
 * 
 * @author daicy
 ***************************************************************************/
public class WaRefPaneDataManager extends RefPaneDataManager {

	/***************************************************************************
	 * Created on 2012-8-8 ÏÂÎç1:26:25<br>
	 * 
	 * @param field
	 * @author daicy
	 ***************************************************************************/
	public WaRefPaneDataManager(String field) {
		super(field);
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2012-8-8 ÏÂÎç1:26:17<br>
	 * 
	 * @see nc.ui.hr.itemsource.view.RefPaneDataManager#getData(java.awt.Component,
	 *      nc.vo.pub.SuperVO)
	 * @author daicy
	 ****************************************************************************/
	@Override
	public SuperVO getData(Component comp, SuperVO item) {
		String pk = ((UIRefPane) comp).getRefModel().getPkValue();
		String name = ((UIRefPane) comp).getRefModel().getRefNameValue();
		String formula = getFunction(pk);
		item.setAttributeValue(ItemPropertyConst.VFORMULA, formula);
		item.setAttributeValue(ItemPropertyConst.VFORMULASTR, name);
		item.setAttributeValue(ItemPropertyConst.IFROMFLAG, getFromType());
		return item;
	}

	protected String getFunction(String pk) {
		if (StringUtils.isBlank(pk)) {
			return null;
		}

		String fScirptLang = pk;
		FunctionVO functionVO;
		if (getFromType().equals(ItemPropertyConst.WA_WAGEFORM)) {
			functionVO = WaFormulaXmlHelper.getFunctionVO(FunctionKey.WAGEFORM);

		} else if (getFromType().equals(ItemPropertyConst.OTHERSOURCE)) {
			functionVO = WaFormulaXmlHelper
					.getFunctionVO(FunctionKey.OTHERSOURCE);

		} else {
			functionVO = WaFormulaXmlHelper
					.getFunctionVO(FunctionKey.GRADEVALUE);

		}
		if (null != functionVO) {
			fScirptLang = FormulaXmlHelper.createFormula(functionVO, pk);
		}

		return fScirptLang;

	}

	public void setData(SuperVO item, Component comp) {
		try {
			String formula = (String) item
					.getAttributeValue(ItemPropertyConst.VFORMULA);
			if (StringUtils.isBlank(formula)) {
				((UIRefPane) comp).setPK(null);
				return;
			}

			String[] pks = null;
			FunctionVO functionVO = null;
			if (getFromType().equals(ItemPropertyConst.WA_WAGEFORM)) {
				functionVO = WaFormulaXmlHelper
						.getFunctionVO(FunctionKey.WAGEFORM);

			} else if (getFromType().equals(ItemPropertyConst.OTHERSOURCE)) {
				functionVO = WaFormulaXmlHelper
						.getFunctionVO(FunctionKey.OTHERSOURCE);

			} else {

				if (!StringUtils.isBlank(formula)) {
					functionVO = WaFormulaXmlHelper
							.getFunctionVO(FunctionKey.GRADEVALUE);

				}

			}

			if (null != functionVO) {
				pks = FormulaXmlHelper.getArguments(functionVO, formula);
			}

			((UIRefPane) comp).setPKs(pks);

		} catch (BusinessException e) {
			((UIRefPane) comp).setPK(null);
			Logger.error(e.getMessage(), e);
		}

	}

}
