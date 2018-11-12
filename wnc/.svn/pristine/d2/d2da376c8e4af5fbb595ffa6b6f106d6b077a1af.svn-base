package nc.ui.hi.psndoc.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.vo.hi.psndoc.CourtDeductionSetting;
import nc.vo.hi.psndoc.DebtFileVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class CourtFineValidator implements Validator {

	@Override
	public ValidationFailure validate(Object e) {
		PsndocAggVO aggvo = (PsndocAggVO) e;
		String[] tabs = aggvo.getTableCodes();
		for (String tab : tabs) {
			if (tab.equals("hi_psndoc_courtdeduction")) {
				CourtDeductionSetting[] courtdeductvos = (CourtDeductionSetting[]) aggvo.getTableVO(tab);
				Map<String, String> map = new HashMap<String, String>();
				UFDouble monthratio = UFDouble.ZERO_DBL;
				for (CourtDeductionSetting cdvo : courtdeductvos) {
					if (null != cdvo.getAttributeValue("courtdeductways")) {
						// 選擇了按照發放比率，則必填月執行費率
						if (cdvo.getAttributeValue("courtdeductways").toString().equals("1")
								&& null == cdvo.getAttributeValue("monthexecutrate")) {
							return new ValidationFailure("檔案編號" + cdvo.getAttributeValue("filenumber") + "未输入月執行費率");
						}
						// 選擇了按照固定金額，則必填月執行金額
						if (cdvo.getAttributeValue("courtdeductways").toString().equals("2")
								&& null == cdvo.getAttributeValue("monthexecutamount")) {
							return new ValidationFailure("檔案編號" + cdvo.getAttributeValue("filenumber") + "未输入月執行金額");
						}
						// 選擇了最低扣款縣市，則必填最低扣款縣市
						if (cdvo.getAttributeValue("courtdeductways").toString().equals("3")
								&& null == cdvo.getAttributeValue("mindeductcountry")) {
							return new ValidationFailure("檔案編號" + cdvo.getAttributeValue("filenumber") + "未选择最低扣款縣市");
						}
					}
					// 扣款方式必须唯一，不唯一不让保存
					if (!new UFBoolean(String.valueOf(cdvo.getAttributeValue("isstop"))).booleanValue()
							&& cdvo.getStatus() != 3) {
						map.put(String.valueOf(cdvo.getAttributeValue("courtdeductways")), cdvo.getPk_psndoc());
						monthratio = monthratio
								.add(cdvo.getAttributeValue("monthexecutrate") == null ? UFDouble.ZERO_DBL
										: new UFDouble(String.valueOf(cdvo.getAttributeValue("monthexecutrate"))));
					}
					// 判断月执行金额是否大于总金额
					UFDouble monthexecutamount = null == cdvo.getAttributeValue("monthexecutamount") ? UFDouble.ZERO_DBL
							: new UFDouble(String.valueOf(cdvo.getAttributeValue("monthexecutamount")));
					UFDouble totalamountowed = null == cdvo.getAttributeValue("totalamountowed") ? UFDouble.ZERO_DBL
							: new UFDouble(String.valueOf(cdvo.getAttributeValue("totalamountowed")));
					if (monthexecutamount.sub(totalamountowed).doubleValue() > 0) {
						return new ValidationFailure("檔案編號" + cdvo.getAttributeValue("filenumber") + "月執行金額大於總金額");
					}
				}
				if (monthratio.doubleValue() > 1) {
					return new ValidationFailure("月執行率之和不能大於1");
				}
				if (map.size() > 1) {
					return new ValidationFailure("扣款方式不唯一,請正確選擇!");
				}
			}
			// 债权档案
			if (tab.equals("hi_psndoc_debtfile")) {
				DebtFileVO[] debtFileVOs = (DebtFileVO[]) aggvo.getTableVO(tab);
				List<String> debtlist = new ArrayList<String>();
				Map<String, String> maps = new HashMap<String, String>();
				for (DebtFileVO dfvo : debtFileVOs) {
					maps.put((String) dfvo.getAttributeValue("dfilenumber"), null);
				}
				for (String map : maps.keySet()) {
					UFDouble sumrepaymentratio = UFDouble.ZERO_DBL;
					for (DebtFileVO dfvo : debtFileVOs) {
						if (map.equals((String) dfvo.getAttributeValue("dfilenumber"))
								&& !((UFBoolean) dfvo.getAttributeValue("stopflag")).booleanValue()) {
							sumrepaymentratio = sumrepaymentratio.add((UFDouble) dfvo
									.getAttributeValue("repaymentratio"));
						}
					}
					if (sumrepaymentratio.sub(UFDouble.ONE_DBL).doubleValue() != 0) {
						debtlist.add(map);
					}
				}
				if (debtlist.size() > 0) {
					return new ValidationFailure("档案编号" + debtlist + "还款比率总和不等于1，请重新分配比率");
				}
			}
		}
		return null;
	}
}
