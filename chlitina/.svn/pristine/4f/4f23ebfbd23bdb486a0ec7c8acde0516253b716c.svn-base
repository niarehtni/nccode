package nc.ui.hrwa.incometax.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrwa.IIncometaxMaintain;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.uif2.model.IMultiRowSelectModel;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.pub.VOStatus;
/**
 * 
 * @author ward.wong
 * @date 20180126
 * @version v1.0
 * @功能描述 申报明细档单据删除按钮
 *
 */
public class DeleteAction extends nc.ui.pubapp.uif2app.actions.DeleteAction {
	private static final long serialVersionUID = 1L;

	protected boolean isActionEnable() {

		boolean isEnable = super.isActionEnable();
		if((((IMultiRowSelectModel)getModel()).getSelectedOperaDatas() == null)){
			return false;
		}
		if ((isEnable) && (((IMultiRowSelectModel)getModel()).getSelectedOperaDatas() != null)) {
			Object[] aggTaxVOs=((IMultiRowSelectModel) getModel()).getSelectedOperaDatas();
			for (int i = 0; i < aggTaxVOs.length; i++) {
				AggIncomeTaxVO aggvo = (AggIncomeTaxVO) aggTaxVOs[i];
				if (aggvo.getParentVO().getIsdeclare().booleanValue()) {
					return false;//只允许删除未被注记的数据
				}
				if (aggvo.getParentVO().getIsgather().booleanValue()) {
					return false;//只允许删除未被汇总的数据
				}
			}
		}
		return isEnable;
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] objects = ((IMultiRowSelectModel) getModel())
				.getSelectedOperaDatas();
		//放置并发
		List<AggIncomeTaxVO> listAggVos=new ArrayList<AggIncomeTaxVO>();
		for (int i = 0; i < objects.length; i++) {
			AggIncomeTaxVO aggvo = (AggIncomeTaxVO) objects[i];
			aggvo.getParentVO().setStatus(VOStatus.UPDATED);
			listAggVos.add(aggvo);
		}
		IIncometaxMaintain service2 = NCLocator.getInstance().lookup(
				IIncometaxMaintain.class);
		AggIncomeTaxVO[] aggvos = service2.update(
				listAggVos.toArray(new AggIncomeTaxVO[0]), null);
		((BillManageModel) getModel()).directlyUpdate(aggvos);
		//结束
		super.doAction(e);
	}

}
