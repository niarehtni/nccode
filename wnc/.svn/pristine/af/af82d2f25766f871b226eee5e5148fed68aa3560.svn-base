package nc.ui.hrwa.sumincometax.action;

import java.awt.event.ActionEvent;

import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;

/**
 * 
 * @author ward.wong
 * @date 20180126
 * @version v1.0
 * @功能描述 申报明细档汇总单据修改按钮
 *
 */
public class EditAction extends nc.ui.pubapp.uif2app.actions.EditAction {

	
	private static final long serialVersionUID = 8503495844116846886L;
	
	private IAutoShowUpComponent showUpComponent;

	protected boolean isActionEnable() {
		boolean isEnable = super.isActionEnable();
		if ((isEnable) && (getModel().getSelectedData() != null)) {
			AggSumIncomeTaxVO aggvo = (AggSumIncomeTaxVO) getModel()
					.getSelectedData();
			if (!aggvo.getParentVO().getIsdeclare().booleanValue()) {
				return true;//只允许修改未被注记的单据
			}
		}
		return false;
	}
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		getShowUpComponent().showMeUp();//进入卡片界面
	}
	public IAutoShowUpComponent getShowUpComponent() {
		return showUpComponent;
	}
	public void setShowUpComponent(IAutoShowUpComponent showUpComponent) {
		this.showUpComponent = showUpComponent;
	}
	
	
}
