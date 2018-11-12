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
 * @author ward
 * @date 2017-08-08
 * 
 */
public class UnMarkAction extends nc.ui.pubapp.uif2app.actions.DifferentVOSaveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5065464955516618107L;

	@Override
	public void doAction(ActionEvent paramActionEvent) throws Exception {
		Object[] objects=((IMultiRowSelectModel) getModel()).getSelectedOperaDatas();
		List<AggIncomeTaxVO> listTaxVOs=new ArrayList<AggIncomeTaxVO>();
		for (int i = 0; i < objects.length; i++) {
			AggIncomeTaxVO aggvo = (AggIncomeTaxVO) objects[i];
			aggvo.getParentVO().setIsdeclare(UFBoolean.FALSE);
			aggvo.getParentVO().setStatus(VOStatus.UPDATED);
			listTaxVOs.add(aggvo);
		}
		IIncometaxMaintain service=NCLocator.getInstance().lookup(IIncometaxMaintain.class);
		AggIncomeTaxVO[] aggTaxVOs=service.update(listTaxVOs.toArray(new AggIncomeTaxVO[0]), null);
		((BillManageModel)getModel()).directlyUpdate(aggTaxVOs);
	}

	protected boolean isActionEnable() {
		Object[] objects=((IMultiRowSelectModel) getModel()).getSelectedOperaDatas();
		if(null==objects){
			return false;
		}
		for (int i = 0; i < objects.length; i++) {
			AggIncomeTaxVO aggvo = (AggIncomeTaxVO) objects[i];
			if(aggvo.getParentVO().getIsdeclare().booleanValue()){
				return true;
			}
		}
		return false;
	}

	public UnMarkAction() {
		this.setBtnName(ResHelper.getString("incometax", "2incometax-n-000012")/*"申报档案取消注记"*/);
		this.setCode("UnMark");
		this.putValue("UnMark", ResHelper.getString("incometax", "2incometax-n-000012"));
	}

}
