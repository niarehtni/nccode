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
		IGetAggIncomeTaxData getService=NCLocator.getInstance().lookup(
				IGetAggIncomeTaxData.class);
		Object[] objects = ((IMultiRowSelectModel) getModel())
				.getSelectedOperaDatas();
		List<AggSumIncomeTaxVO> listTaxVOs = new ArrayList<AggSumIncomeTaxVO>();
		List<String> listIncomePK=new ArrayList<String>();
		for (int i = 0; i < objects.length; i++) {
			AggSumIncomeTaxVO aggvo = (AggSumIncomeTaxVO) objects[i];
			aggvo.getParentVO().setIsdeclare(UFBoolean.TRUE);
			aggvo.getParentVO().setStatus(VOStatus.UPDATED);
			CIncomeTaxVO[] incomeVo=(CIncomeTaxVO[]) aggvo.getChildrenVO();
			//当子表数据为空时，补齐数据
			if (null == incomeVo) {
				incomeVo = (CIncomeTaxVO[]) getService.getAggSumIncomeTaxByPK(
						aggvo.getPrimaryKey()).getChildrenVO();
			}
			for (int j = 0; j < incomeVo.length; j++) {
				CIncomeTaxVO cIncomeTaxVO = incomeVo[j];
				cIncomeTaxVO.setIsdeclare(UFBoolean.TRUE);
				cIncomeTaxVO.setStatus(VOStatus.UPDATED);
				listIncomePK.add(cIncomeTaxVO.getPk_incometax());
			}
			listTaxVOs.add(aggvo);
		}
		ISumincometaxMaintain service = NCLocator.getInstance().lookup(
				ISumincometaxMaintain.class);
		AggSumIncomeTaxVO[] aggTaxVOs = service.update(
				listTaxVOs.toArray(new AggSumIncomeTaxVO[0]));
		getService.markIncomeTaxVO(listIncomePK.toArray(new String[0]));
		((BillManageModel) getModel()).directlyUpdate(aggTaxVOs);
		ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("incometax", "2incometax-n-000029")/*"注记成功"*/, getModel().getContext());
	}

	protected boolean isActionEnable() {
		Object[] objects = ((IMultiRowSelectModel) getModel())
				.getSelectedOperaDatas();
		if (null == objects) {
			return false;
		}
		for (int i = 0; i < objects.length; i++) {
			AggSumIncomeTaxVO aggvo = (AggSumIncomeTaxVO) objects[i];
			if (!aggvo.getParentVO().getIsdeclare().booleanValue()) {
				return true;
			}
		}
		return false;
	}

	public MarkAction() {
		this.setBtnName(ResHelper.getString("incometax", "2incometax-n-000030")/*"注记"*/);
		this.setCode("Mark");
		this.putValue("Mark", ResHelper.getString("incometax", "2incometax-n-000030"));
	}

}
