package nc.ui.twhr.rangetable.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.uif2.NCAction;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDouble;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.twhr.basedoc.DocTypeEnum;
import nc.vo.twhr.rangetable.RangeLineVO;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.twhr.rangetable.RangeTableVO;
import nc.vo.uif2.LoginContext;

/**
 * @author SSX
 * 
 */
public class RangetableGenerateAction extends NCAction {
	/**
	 * serial id
	 */
	private static final long serialVersionUID = 5225122544097487024L;
	private BillForm editor;
	private LoginContext context;

	public RangetableGenerateAction() {
		this.setCode("RangeTableGenerateAction");
		this.setBtnName(NCLangRes.getInstance().getStrByID("68861025",
				"RangetableGenerateAction-0000")/* 生成级距表 */);
	}

	@Override
	public void doAction(ActionEvent arg0) throws BusinessException {
		BillCardPanel card = this.getEditor().getBillCardPanel();
		AggregatedValueObject aggVO = card.getBillValueVO(
				RangeTableAggVO.class.getName(), RangeTableVO.class.getName(),
				RangeLineVO.class.getName());

		RangeTableVO headVO = (RangeTableVO) aggVO.getParentVO();
		if (headVO.getTabletype() == null || headVO.getTabletype() < 1) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"68861025", "RangetableGenerateAction-0001")/* 请选择级距表类型。 */);
		}

		if (headVO.getTabletype() == 1) { // 劳保级距
			generateLIData(card, aggVO);
		} else if (headVO.getTabletype() == 2) { // 劳退级距
			generateLRData(card, aggVO);
		} else if (headVO.getTabletype() == 3) { // 健保级距
			generateNHIData(card, aggVO);
		} else if (headVO.getTabletype() == 4) { // 綜所稅級距

		}
	}

	/**
	 * 生成健保级距数据
	 * 
	 * 生成规则：在级距表的指标上限中依次录入月投保薪资后，点击生成，可按法规规定生成健保投保级距表。
	 * 
	 * 103年7月1日 起实施细则
	 * 
	 * 员工缴交部分 = 月投保金额 * 4.91%(BASEDOC:TWNP0001) * 30%(BASEDOC:TWNP0002)
	 * 
	 * 雇主缴交部分 = 月投保金额 * 4.91%(BASEDOC:TWNP0001) * 60%(BASEDOC:TWNP0003) *
	 * 1.7(BASEDOC:TWNP0004)
	 * 
	 * @param card
	 * @param aggVO
	 * @throws BusinessException
	 */
	private void generateNHIData(BillCardPanel card, AggregatedValueObject aggVO)
			throws BusinessException {
		if (aggVO.getChildrenVO() == null || aggVO.getChildrenVO().length == 0) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"68861025", "RangetableGenerateAction-0002")/*
																 * 请在级距表指标上限中录入健保级距之月提缴薪资数值
																 * 。
																 */);
		}

		UFDouble[] rangeUppers = getRanges(aggVO.getChildrenVO());
		if (rangeUppers != null && rangeUppers.length > 0) {
			RangeLineVO[] lines = new RangeLineVO[rangeUppers.length];
			UFDouble hrCommonRate = getBaseDocUFDoubleValue("TWNP0001");
			UFDouble rangeMaxUpper = getBaseDocUFDoubleValue("TWLP0016");
			UFDouble hrEmployeeRate = getBaseDocUFDoubleValue("TWNP0002");
			UFDouble hrEmployerRate = getBaseDocUFDoubleValue("TWNP0003");
			UFDouble hrAvgCount = getBaseDocUFDoubleValue("TWNP0004");
			for (int i = 0; i < rangeUppers.length; i++) {
				lines[i] = new RangeLineVO();
				lines[i].setRangeclass("");
				lines[i].setRangegrade(String.valueOf(i + 1));
				if (i == 0) {
					lines[i].setRangelower(UFDouble.ZERO_DBL);
				} else {
					lines[i].setRangelower(rangeUppers[i - 1].add(1));
				}
				if (i < rangeUppers.length - 1) {
					lines[i].setRangeupper(rangeUppers[i]);
				} else {
					lines[i].setRangeupper(UFDouble.ZERO_DBL);
				}
				UFDouble hrRealAmount = rangeUppers[i].toDouble() > rangeMaxUpper
						.toDouble() ? rangeMaxUpper : rangeUppers[i];
				lines[i].setEmployeeamount(new UFDouble(Math.round(hrRealAmount
						.multiply(hrCommonRate.multiply(hrEmployeeRate))
						.toDouble())));
				lines[i].setEmployeramount(new UFDouble(Math.round(hrRealAmount
						.multiply(
								hrCommonRate.multiply(hrEmployerRate).multiply(
										hrAvgCount)).toDouble())));
				lines[i].setRowno(String.valueOf(i + 1));
			}
			aggVO.setChildrenVO(lines);
		}
		card.setBillValueVO(aggVO);

	}

	/**
	 * 生成劳退级距数据
	 * 
	 * 生成规则：在级距表的指标上限中依次录入月投保薪资后，点击生成，可按法规规定生成劳退投保级距表。
	 * 
	 * 103年7月1日 起实施细则
	 * 
	 * 员工缴交部分 = 月提缴薪资 * 0%(BASEDOC:TWLP0017)
	 * 
	 * 雇主缴交部分 = 月提缴薪资 * 6%(BASEDOC:TWLP0011)
	 * 
	 * @param card
	 * @param aggVO
	 * @throws BusinessException
	 */
	private void generateLRData(BillCardPanel card, AggregatedValueObject aggVO)
			throws BusinessException {
		if (aggVO.getChildrenVO() == null || aggVO.getChildrenVO().length == 0) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"68861025", "RangetableGenerateAction-0003")/*
																 * 请在级距表指标上限中录入劳退级距之月提缴薪资数值
																 * 。
																 */);
		}

		UFDouble[] rangeUppers = getRanges(aggVO.getChildrenVO());
		if (rangeUppers != null && rangeUppers.length > 0) {
			RangeLineVO[] lines = new RangeLineVO[rangeUppers.length];
			UFDouble lrEmployerRate = getBaseDocUFDoubleValue("TWLP0011");
			UFDouble rangeMaxUpper = getBaseDocUFDoubleValue("TWLP0015");
			UFDouble lrEmployeeRate = getBaseDocUFDoubleValue("TWLP0017");
			int[] steps = { 5, 10, 20, 25, 30, 35, 40, 45, 49, 54, 62 };
			for (int i = 0; i < rangeUppers.length; i++) {
				lines[i] = new RangeLineVO();
				lines[i].setRangeclass(String.valueOf(getIndex(steps, i)));
				lines[i].setRangegrade(String.valueOf(i + 1));
				if (i == 0) {
					lines[i].setRangelower(UFDouble.ZERO_DBL);
				} else {
					lines[i].setRangelower(rangeUppers[i - 1].add(1));
				}
				if (i < rangeUppers.length - 1) {
					lines[i].setRangeupper(rangeUppers[i]);
				} else {
					lines[i].setRangeupper(UFDouble.ZERO_DBL);
				}
				UFDouble lrRealAmount = rangeUppers[i].toDouble() > rangeMaxUpper
						.toDouble() ? rangeMaxUpper : rangeUppers[i];
				lines[i].setEmployeeamount(new UFDouble(Math.round(lrRealAmount
						.multiply(lrEmployeeRate).toDouble())));
				lines[i].setEmployeramount(new UFDouble(Math.round(lrRealAmount
						.multiply(lrEmployerRate).toDouble())));
				lines[i].setRowno(String.valueOf(i + 1));
			}
			aggVO.setChildrenVO(lines);
		}
		card.setBillValueVO(aggVO);
	}

	private int getIndex(int[] steps, int seed) {
		for (int i = 0; i < steps.length; i++) {
			if (seed + 1 <= steps[i]) {
				return i + 1;
			}
		}

		return 0;
	}

	/**
	 * 生成劳保级距数据
	 * 
	 * 生成规则：在级距表的指标上限中依次录入月投保薪资后，点击生成，可按法规规定生成劳保投保级距表。
	 * 
	 * 103年7月1日 起实施细则
	 * 
	 * 员工缴交部分 = (月投保薪资 * 8.5% + 月投保薪资 * 1%) * 20%
	 * 
	 * 雇主缴交部分 = (月投保薪资 * 8.5% + 月投保薪资 * 1%) * 70%
	 * 
	 * @param card
	 * @param aggVO
	 * @throws BusinessException
	 */
	private void generateLIData(BillCardPanel card, AggregatedValueObject aggVO)
			throws BusinessException {
		if (aggVO.getChildrenVO() == null || aggVO.getChildrenVO().length == 0) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"68861025", "RangetableGenerateAction-0004")/*
																 * 请在级距表指标上限中录入劳保级距之月投保薪资数值
																 * 。
																 */);
		}

		UFDouble[] rangeUppers = getRanges(aggVO.getChildrenVO());
		if (rangeUppers != null && rangeUppers.length > 0) {

			RangeLineVO[] lines = new RangeLineVO[rangeUppers.length * 30];
			UFDouble commonAccRate = getBaseDocUFDoubleValue("TWLP0001");
			UFDouble careerRate = getBaseDocUFDoubleValue("TWLP0004");
			UFDouble rangeMaxUpper = getBaseDocUFDoubleValue("TWLP0014");
			for (int i = 0; i < rangeUppers.length; i++) {
				for (int j = 0; j < 30; j++) {
					lines[i * 30 + j] = new RangeLineVO();
					lines[i * 30 + j].setRangeclass(String.valueOf(i + 1));
					lines[i * 30 + j].setRangegrade(String.valueOf(j + 1));
					if (i == 0) {
						lines[i * 30 + j].setRangelower(UFDouble.ZERO_DBL);
					} else {
						lines[i * 30 + j].setRangelower(rangeUppers[i - 1]
								.add(1));
					}
					if (i < rangeUppers.length - 1) {
						lines[i * 30 + j].setRangeupper(rangeUppers[i]);
					} else {
						lines[i * 30 + j].setRangeupper(UFDouble.ZERO_DBL);
					}
					UFDouble lbRealAmount = rangeUppers[i].toDouble() > rangeMaxUpper
							.toDouble() ? rangeMaxUpper : rangeUppers[i];

					UFDouble tmpAmount1 = new UFDouble(Math.round(lbRealAmount
							.div(30).multiply(j + 1).multiply(commonAccRate)
							.multiply(0.2).toDouble()));
					UFDouble tmpAmount2 = new UFDouble(Math.round(lbRealAmount
							.div(30).multiply(j + 1).multiply(careerRate)
							.multiply(0.2).toDouble()));
					lines[i * 30 + j].setEmployeeamount(tmpAmount1
							.add(tmpAmount2));

					tmpAmount1 = new UFDouble(Math.round(lbRealAmount.div(30)
							.multiply(j + 1).multiply(commonAccRate)
							.multiply(0.7).toDouble()));
					tmpAmount2 = new UFDouble(Math.round(lbRealAmount.div(30)
							.multiply(j + 1).multiply(careerRate).multiply(0.7)
							.toDouble()));
					lines[i * 30 + j].setEmployeramount(tmpAmount1
							.add(tmpAmount2));

					lines[i * 30 + j].setRowno(String.valueOf(i * 30 + j + 1));
				}
			}
			aggVO.setChildrenVO(lines);
		}
		card.setBillValueVO(aggVO);
	}

	private UFDouble getBaseDocUFDoubleValue(String paramCode)
			throws BusinessException {
		String pk_org = this.getContext().getPk_org();
		IBasedocPubQuery baseQry = (IBasedocPubQuery) NCLocator.getInstance()
				.lookup(IBasedocPubQuery.class.getName());
		BaseDocVO baseDoc = baseQry.queryBaseDocByCode(pk_org, paramCode);
		if (baseDoc == null) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"68861025", "RangetableGenerateAction-0005", null,
					new String[] { paramCode })/** 未定义级距计算参数[{0}] */
			);
		}

		UFDouble value = UFDouble.ZERO_DBL;
		if (DocTypeEnum.AMOUNT.toIntValue() == baseDoc.getDoctype()) {
			value = baseDoc.getNumbervalue();
		} else if (DocTypeEnum.RATE.toIntValue() == baseDoc.getDoctype()) {
			value = baseDoc.getNumbervalue().div(100);
		}

		return value;
	}

	private UFDouble[] getRanges(CircularlyAccessibleValueObject[] childrenVO)
			throws BusinessException {
		List<UFDouble> ranges = new ArrayList<UFDouble>();
		if (childrenVO != null && childrenVO.length > 0) {
			for (CircularlyAccessibleValueObject vo : childrenVO) {
				UFDouble upper = (UFDouble) vo.getAttributeValue("rangeupper");

				if (upper != null && upper.doubleValue() > 0) {
					if (!ranges.contains(upper)) {
						ranges.add(upper);
					}
				} else {
					if (!ranges.contains(new UFDouble(Double.MAX_VALUE))) {
						ranges.add(new UFDouble(Double.MAX_VALUE));
					}
				}
			}
		}

		double temp = 0;
		if (ranges.size() > 0) {
			for (int i = 0; i < ranges.size(); i++) {
				for (int j = i + 1; j < ranges.size(); j++) {
					if (ranges.get(i).doubleValue() > ranges.get(j)
							.doubleValue()) {
						temp = ranges.get(j).doubleValue();
						ranges.set(j, new UFDouble(ranges.get(i).doubleValue()));
						ranges.set(i, new UFDouble(temp));
					}
				}
			}
		}
		return ranges.toArray(new UFDouble[0]);
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

}
