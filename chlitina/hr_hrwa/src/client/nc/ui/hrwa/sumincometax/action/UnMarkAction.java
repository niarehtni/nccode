package nc.ui.hrwa.sumincometax.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IGetAggIncomeTaxData;
import nc.itf.hrwa.ISumincometaxMaintain;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.IMultiRowSelectModel;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.hrwa.sumincometax.CIncomeTaxVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;

/**
 * 
 * @author ward
 * @date 2017-08-08
 * 
 */
public class UnMarkAction extends
		nc.ui.pubapp.uif2app.actions.DifferentVOSaveAction {

	private static final long serialVersionUID = 5103812702416797701L;

	@Override
	public void doAction(ActionEvent paramActionEvent) throws Exception {
		IGetAggIncomeTaxData getService = NCLocator.getInstance().lookup(
				IGetAggIncomeTaxData.class);
		Object[] objects = ((IMultiRowSelectModel) getModel())
				.getSelectedOperaDatas();
		List<AggSumIncomeTaxVO> listTaxVOs = new ArrayList<AggSumIncomeTaxVO>();
		List<String> listIncomePK = new ArrayList<String>();
		for (int i = 0; i < objects.length; i++) {
			AggSumIncomeTaxVO aggvo = (AggSumIncomeTaxVO) objects[i];
			aggvo.getParentVO().setIsdeclare(UFBoolean.FALSE);
			aggvo.getParentVO().setStatus(VOStatus.UPDATED);
			CIncomeTaxVO[] incomeVo = (CIncomeTaxVO[]) aggvo.getChildrenVO();
			//当子表数据为空时，补齐数据
			if (null == incomeVo) {
				incomeVo = (CIncomeTaxVO[]) getService.getAggSumIncomeTaxByPK(
						aggvo.getPrimaryKey()).getChildrenVO();
			}
			for (int j = 0; j < incomeVo.length; j++) {
				CIncomeTaxVO cIncomeTaxVO = incomeVo[j];
				cIncomeTaxVO.setIsdeclare(UFBoolean.FALSE);
				cIncomeTaxVO.setStatus(VOStatus.UPDATED);
				listIncomePK.add(cIncomeTaxVO.getPk_incometax());
			}
			listTaxVOs.add(aggvo);
		}
		ISumincometaxMaintain service = NCLocator.getInstance().lookup(
				ISumincometaxMaintain.class);
		AggSumIncomeTaxVO[] aggTaxVOs = service.update(listTaxVOs
				.toArray(new AggSumIncomeTaxVO[0]));
		getService.unMarkIncomeTaxVO(listIncomePK.toArray(new String[0]));
		((BillManageModel) getModel()).directlyUpdate(aggTaxVOs);
		ShowStatusBarMsgUtil
				.showStatusBarMsg(
						ResHelper.getString("incometax", "2incometax-n-000031")/* "取消注记成功" */,
						getModel().getContext());
	}

	protected boolean isActionEnable() {
		Object[] objects = ((IMultiRowSelectModel) getModel())
				.getSelectedOperaDatas();
		if (null == objects) {
			return false;
		}
		for (int i = 0; i < objects.length; i++) {
			AggSumIncomeTaxVO aggvo = (AggSumIncomeTaxVO) objects[i];
			if (aggvo.getParentVO().getIsdeclare().booleanValue()) {
				return true;
			}
		}
		return false;
	}

	public UnMarkAction() {
		this.setBtnName(ResHelper.getString("incometax", "2incometax-n-000032")/* "申报档案取消注记" */);
		this.setCode("UnMark");
		this.putValue("UnMark",
				ResHelper.getString("incometax", "2incometax-n-000032")/* "申报档案取消注记" */);
	}

}
