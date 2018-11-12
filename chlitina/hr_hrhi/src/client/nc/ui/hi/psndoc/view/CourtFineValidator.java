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
						// �x���˰��հl�ű��ʣ��t���������M��
						if (cdvo.getAttributeValue("courtdeductways").toString().equals("1")
								&& null == cdvo.getAttributeValue("monthexecutrate")) {
							return new ValidationFailure("�n����̖" + cdvo.getAttributeValue("filenumber") + "δ���������M��");
						}
						// �x���˰��չ̶����~���t�������н��~
						if (cdvo.getAttributeValue("courtdeductways").toString().equals("2")
								&& null == cdvo.getAttributeValue("monthexecutamount")) {
							return new ValidationFailure("�n����̖" + cdvo.getAttributeValue("filenumber") + "δ�������н��~");
						}
						// �x������Ϳۿ�h�У��t������Ϳۿ�h��
						if (cdvo.getAttributeValue("courtdeductways").toString().equals("3")
								&& null == cdvo.getAttributeValue("mindeductcountry")) {
							return new ValidationFailure("�n����̖" + cdvo.getAttributeValue("filenumber") + "δѡ����Ϳۿ�h��");
						}
					}
					// �ۿʽ����Ψһ����Ψһ���ñ���
					if (!new UFBoolean(String.valueOf(cdvo.getAttributeValue("isstop"))).booleanValue()
							&& cdvo.getStatus() != 3) {
						map.put(String.valueOf(cdvo.getAttributeValue("courtdeductways")), cdvo.getPk_psndoc());
						monthratio = monthratio
								.add(cdvo.getAttributeValue("monthexecutrate") == null ? UFDouble.ZERO_DBL
										: new UFDouble(String.valueOf(cdvo.getAttributeValue("monthexecutrate"))));
					}
					// �ж���ִ�н���Ƿ�����ܽ��
					UFDouble monthexecutamount = null == cdvo.getAttributeValue("monthexecutamount") ? UFDouble.ZERO_DBL
							: new UFDouble(String.valueOf(cdvo.getAttributeValue("monthexecutamount")));
					UFDouble totalamountowed = null == cdvo.getAttributeValue("totalamountowed") ? UFDouble.ZERO_DBL
							: new UFDouble(String.valueOf(cdvo.getAttributeValue("totalamountowed")));
					if (monthexecutamount.sub(totalamountowed).doubleValue() > 0) {
						return new ValidationFailure("�n����̖" + cdvo.getAttributeValue("filenumber") + "���н��~��춿����~");
					}
				}
				if (monthratio.doubleValue() > 1) {
					return new ValidationFailure("������֮�Ͳ��ܴ��1");
				}
				if (map.size() > 1) {
					return new ValidationFailure("�ۿʽ��Ψһ,Ո���_�x��!");
				}
			}
			// ծȨ����
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
					return new ValidationFailure("�������" + debtlist + "��������ܺͲ�����1�������·������");
				}
			}
		}
		return null;
	}
}
