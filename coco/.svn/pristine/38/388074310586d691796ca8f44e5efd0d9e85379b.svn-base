package nc.ui.hrwa.incometax.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IIncometaxMaintain;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.uif2.model.IMultiRowSelectModel;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;

/**
 * 
 * @author ward.wong
 * @date 20180126
 * @version v1.0
 * @功能描述 申报明细档单据VO注记按钮，修改是否申报属性
 * 
 */
public class MarkAction extends
		nc.ui.pubapp.uif2app.actions.DifferentVOSaveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5065464955516618107L;

	@Override
	public void doAction(ActionEvent paramActionEvent) throws Exception {
		Object[] objects = ((IMultiRowSelectModel) getModel())
				.getSelectedOperaDatas();
		List<AggIncomeTaxVO> listTaxVOs = new ArrayList<AggIncomeTaxVO>();
		for (int i = 0; i < objects.length; i++) {
			AggIncomeTaxVO aggvo = (AggIncomeTaxVO) objects[i];
			aggvo.getParentVO().setIsdeclare(UFBoolean.TRUE);
			aggvo.getParentVO().setStatus(VOStatus.UPDATED);
			listTaxVOs.add(aggvo);
		}
		IIncometaxMaintain service = NCLocator.getInstance().lookup(
				IIncometaxMaintain.class);
		AggIncomeTaxVO[] aggTaxVOs = service.update(
				listTaxVOs.toArray(new AggIncomeTaxVO[0]), null);
		((BillManageModel) getModel()).directlyUpdate(aggTaxVOs);
	}

	protected boolean isActionEnable() {
		Object[] objects = ((IMultiRowSelectModel) getModel())
				.getSelectedOperaDatas();
		if (null == objects) {
			return false;
		}
		for (int i = 0; i < objects.length; i++) {
			AggIncomeTaxVO aggvo = (AggIncomeTaxVO) objects[i];
			if (!aggvo.getParentVO().getIsdeclare().booleanValue()) {
				return true;
			}
		}
		return false;
	}

	public MarkAction() {
		this.setBtnName(ResHelper.getString("incometax", "2incometax-n-000010")/*"申报档案注记"*/);
		this.setCode("Mark");
		this.putValue("Mark", ResHelper.getString("incometax", "2incometax-n-000010"));
	}

}
