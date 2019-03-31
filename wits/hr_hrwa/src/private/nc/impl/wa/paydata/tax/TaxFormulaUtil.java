package nc.impl.wa.paydata.tax;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;

/**
 *
 * @author: zhangg
 * @date: 2010-1-14 ����10:43:32
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class TaxFormulaUtil {
	/**
	 *
	 * @author zhangg on 2010-1-12
	 * @param taxFormula
	 * @return
	 * @throws BusinessException
	 * @throws ParseException
	 */
	public static TaxFormulaVO translate2FormulaVO(FunctionVO taxFunctionVO, String taxFormula) throws BusinessException {

		MessageFormat format = new MessageFormat(taxFunctionVO.getArguments());

		try {
			Object[] parts = format.parse(taxFormula);

			if (parts.length != 2 && parts.length != 4) {
				throw new BusinessException(taxFormula + ResHelper.getString("6013salarypmt","06013salarypmt0271")/*@res "�еĲ����������߸�ʽ����ȷ!"*/);
			}

			String base = null;
			String class_type = null;
			String yearAward = null;
			
			String monthpay = null;
			
			String periodTimesField = null;
			if (parts.length == 2) {// ʡ�Ե���������Ϊtax(base)
				 class_type = parts[0].toString();
				 base = parts[1].toString().trim();
			}
			if (parts.length == 4) {// tax(base, class_type)
				class_type = parts[0].toString().trim();
				 base = parts[1].toString().trim();//���ο�˰����
				 yearAward = parts[1].toString().trim();//��Ƚ�����һ����
				 monthpay = parts[2].toString().trim();
				 periodTimesField = parts[3].toString().trim();
				 
			}
			// У��Class_type ����
			if (class_type != null) {
// {mod:�¸�˰����}
// begin
				if (class_type.equals(TaxFormulaVO.CLASS_TYPE_NORMAL) || class_type.equals(TaxFormulaVO.CLASS_TYPE_YEAR) 
						|| class_type.equals(TaxFormulaVO.CLASS_TYPE_TOTAL_YEAR)) {
// end
					Logger.info(taxFormula + "Class_type ����ȡֵ��ȷ!");
				} else {
					throw new BusinessException(taxFormula + ResHelper.getString("6013salarypmt","06013salarypmt0272")/*@res "Class_type ����ȡֵ����ȷ!, ӦΪ0  ���� 1"*/);
				}
			}

			TaxFormulaVO taxFormulaVO = new TaxFormulaVO();
			taxFormulaVO.setBase(base);
			taxFormulaVO.setClass_type(class_type);
			taxFormulaVO.setYearAward(yearAward);
			taxFormulaVO.setMonthPay(monthpay);
			taxFormulaVO.setPeirodTimesField(periodTimesField);
			
			return taxFormulaVO;

		} catch (ParseException e) {
			throw new BusinessException(ResHelper.getString("6013salarypmt","06013salarypmt0273")/*@res "�۶��e�`"*/);
		}

	}



	public static String getCheckTaxFormula(FunctionVO taxFunctionVO,WaClassItemVO itemVO) throws BusinessException {
		String taxFormula = TaxFormulaUtil.getTaxFormula(taxFunctionVO, itemVO.getVformula());
/*		if (taxFormula == null) {
			itemVO.getVformula();
		}*/

		String replaceFormula = translate2FormulaVO(taxFunctionVO, taxFormula).getBase();
		// У��base ��������Ϊbase�����Ǳ��ʽ�ʿ���ʹ�ù�ȥ�� ���ݿ���֤��ʽ�����ظ�ֵ

		String vformula = itemVO.getVformula().replaceAll(taxFormula, replaceFormula);

		return vformula;
	}

	/**
	 * ����ʽ�滻����Ӧ�Ŀ�����ִ�е����
	 *
	 * @author zhangg on 2010-1-12
	 * @see nc.vo.wa.paydata.IFormula#checkReplace(java.lang.String)
	 */

	public static String getTaxFormula(FunctionVO taxFunctionVO, String itemFormula) throws BusinessException {

		// ��1��������Ƿ����tax��������
		// tax����.x��ʾ���ż���0�������ַ��� �������ʽΪ���ƥ��
		// \\s*��ʾtax�ͣ�����0�����Ͽո�
		Pattern pattern = Pattern.compile(taxFunctionVO.getPattern());
		Matcher matcher = pattern.matcher(itemFormula);
		if (!matcher.matches()) {
			return null;
		}

		Vector<String> formulaVector = new Vector<String>();

		// ����tax����
		while (matcher.find()) {
			String formula = matcher.group();
			formulaVector.add(formula);
		}

		if (formulaVector.size() > 1) {
			throw new BusinessException(itemFormula + ResHelper.getString("6013salarypmt","06013salarypmt0274")/*@res "����1������tax����, ��֧��!"*/);
		}

		String taxFormula = formulaVector.get(0);

		return taxFormula;
	}
}