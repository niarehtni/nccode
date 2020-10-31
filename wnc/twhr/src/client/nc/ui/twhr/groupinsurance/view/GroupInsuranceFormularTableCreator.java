package nc.ui.twhr.groupinsurance.view;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.ui.hr.formula.HRFormulaItem;
import nc.ui.hr.formula.itf.IFormulaTableCreator;
import nc.vo.pub.BusinessException;
import nc.vo.pub.formulaedit.FormulaItem;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;
import nc.vo.uap.busibean.exception.BusiBeanException;

public class GroupInsuranceFormularTableCreator implements IFormulaTableCreator {

	public static String GROUPINS_INFOSET_NAME = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("68J61035", "01035001-0000")/*
																	 * @res
																	 * Ա���ű�Ͷ��
																	 */;
	public static String GROUPINS_BASECALC_NAME = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("68J61035", "01035001-0001")/* @res ��н���� */;
	public static String GROUPINS_COVERAGE_NAME = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("68J61035", "01035001-0011")/* @res �ͱ�Ͷ������ */;
	public static String GROUPINS_BASECALC_CODE = "glbdef6";
	/* @res �ͱ�Ͷ������code */
	public static String GROUPINS_COVERAGE_CODE = "glbdef4";

	public List<FormulaItem> getAllTables() throws BusiBeanException {
		List<FormulaItem> items = new ArrayList();
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		String pk_org = pk_group;
		try {
			items.add(new HRFormulaItem(PsndocDefTableUtil
					.getGroupInsuranceTablename(), GROUPINS_INFOSET_NAME,
					GROUPINS_INFOSET_NAME, GROUPINS_INFOSET_NAME));
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("68J61035", "01035001-0002")/*
																			 * @res
																			 * �Զ��嵵��
																			 */
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"68J61035", "01035001-0003")/* @res δ���� */
					+ "["
					+ GROUPINS_INFOSET_NAME
					+ "]"
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"68J61035", "01035001-0004")/* @res �Ӽ����� */);
		}

		return items;
	}
}