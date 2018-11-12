package nc.ui.hrwa.sumincometax.action;

import nc.ui.uif2.model.IMultiRowSelectModel;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;

public class DeleteAction extends nc.ui.pubapp.uif2app.actions.DeleteAction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6943976451476790027L;

	@Override
	protected boolean isActionEnable() {
		boolean isEnable = super.isActionEnable();
		if((((IMultiRowSelectModel)getModel()).getSelectedOperaDatas() == null)){
			return false;
		}
		if ((isEnable) && (((IMultiRowSelectModel)getModel()).getSelectedOperaDatas() != null)) {
			Object[] aggTaxVOs=((IMultiRowSelectModel) getModel()).getSelectedOperaDatas();
			for (int i = 0; i < aggTaxVOs.length; i++) {
				AggSumIncomeTaxVO aggvo = (AggSumIncomeTaxVO) aggTaxVOs[i];
				if (aggvo.getParentVO().getIsdeclare().booleanValue()) {
					return false;//只允许删除未被注记的数据
				}
			}
		}
		return isEnable;
	}
}
