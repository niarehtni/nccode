package nc.bs.hrwa.sumincometax.ace.bp;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.hrwa.IGetAggIncomeTaxData;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.hrwa.sumincometax.CIncomeTaxVO;
import nc.vo.pub.BusinessException;

/**
 * 汇总单据删除后规则
 * 
 * @author ward
 * 
 */
public class SumincometaxAfterDeleteRule implements IRule {

	@Override
	public void process(Object[] bills) {
		// 汇总单据删除后设置汇总明细单据的汇总标志为N
		List<String> listpk_income = new ArrayList<String>();
		for (Object bill : bills) {
			AggSumIncomeTaxVO aggvo = (AggSumIncomeTaxVO) bill;
			CIncomeTaxVO[] incomeVos = (CIncomeTaxVO[]) aggvo.getChildrenVO();
			for (int i = 0; i < incomeVos.length; i++) {
				CIncomeTaxVO cIncomeTaxVO = incomeVos[i];
				listpk_income.add(cIncomeTaxVO.getPk_incometax());
			}
		}
		IGetAggIncomeTaxData getServices = NCLocator.getInstance().lookup(
				IGetAggIncomeTaxData.class);
		try {
			getServices.cancleGather(listpk_income.toArray(new String[0]));
		} catch (BusinessException e) {
			e.printStackTrace();
			Logger.error(e);
		}

	}

}
