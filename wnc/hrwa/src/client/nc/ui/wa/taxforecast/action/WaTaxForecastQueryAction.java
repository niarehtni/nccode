package nc.ui.wa.taxforecast.action;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.hr.utils.ResHelper;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.uif2.action.QueryAction;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.wa.ref.WaClassItemRefModel;
import nc.ui.wa.taxforecast.model.WaTaxForecastModel;
import nc.ui.wa.taxforecast.model.WaTaxForecastModelService;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.taxforecast.TaxForecastVO;

public class WaTaxForecastQueryAction extends QueryAction {

	private HrBillFormEditor formEditor;

	public HrBillFormEditor getFormEditor() {
		return formEditor;
	}

	public void setFormEditor(HrBillFormEditor formEditor) {
		this.formEditor = formEditor;
	}

	public WaTaxForecastQueryAction() {
		ActionInitializer.initializeAction(this, IActionCode.QUERY);
	}

	/**
	 * 得到表头的字段信息
	 * 
	 * @param strKey
	 * @return
	 */
	public UIRefPane getHeadItemUIRefPane(String strKey) {
		return (UIRefPane) this.getFormEditor().getBillCardPanel()
				.getHeadItem(strKey).getComponent();
	}

	/**
	 * 得到表头的字段信息
	 * 
	 * @param strKey
	 * @return
	 */
	public AbstractRefModel getHeadItemUIRefModel(String strKey) {
		return getHeadItemUIRefPane(strKey).getRefModel();
	}

	/***************************************************************************
	 * Created on 2009-9-2 9:54:21<br>
	 * 
	 * @author Rocex Wang
	 * @return the model
	 ***************************************************************************/
	public WaTaxForecastModelService getService() {
		return (WaTaxForecastModelService) ((BillManageModel) getModel())
				.getService();
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
		Object objValue = formEditor.getValue();
		validate(objValue);

		String monthItemId = getHeadItemUIRefPane(TaxForecastVO.MONTHITEM)
				.getRefPK();
		String yearItemId = getHeadItemUIRefPane(TaxForecastVO.YEARITEM)
				.getRefPK();
		if (null != monthItemId && null != yearItemId) {
			TaxForecastVO taxForecastVO = new TaxForecastVO();
			WaClassItemRefModel waClassItemRefModel = ((WaClassItemRefModel) getHeadItemUIRefModel(TaxForecastVO.MONTHITEM));
			taxForecastVO.setMonthclass(getHeadItemUIRefPane(
					TaxForecastVO.MONTHCLASS).getRefPK());
			taxForecastVO.setMonthperiod(getHeadItemUIRefPane(
					TaxForecastVO.MONTHPERIOD).getRefPK());
			taxForecastVO.setMonthitem((String) waClassItemRefModel
					.getValue("wa_item.itemkey"));
			taxForecastVO.setYearclass(getHeadItemUIRefPane(
					TaxForecastVO.YEARCLASS).getRefPK());
			taxForecastVO.setYearperiod(getHeadItemUIRefPane(
					TaxForecastVO.YEARPERIOD).getRefPK());
			waClassItemRefModel = ((WaClassItemRefModel) getHeadItemUIRefModel(TaxForecastVO.YEARITEM));
			taxForecastVO.setYearitem((String) waClassItemRefModel
					.getValue("wa_item.itemkey"));
			waClassItemRefModel = ((WaClassItemRefModel) getHeadItemUIRefModel(TaxForecastVO.MONTHCOUNT));
			taxForecastVO.setMonthcount((String) waClassItemRefModel
					.getValue("wa_item.itemkey"));
			taxForecastVO.setPk_group(getModel().getContext().getPk_group());
			TaxForecastVO[] vos = getService().queryTaxForecastVOS(
					taxForecastVO);

			// 2016-11-28 zhousze 薪资加密：这里对所有测算数据解密 begin
			for (TaxForecastVO vo : vos) {
				vo.setPermmoney(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getPermmoney() == null ? new UFDouble(0) : vo
						.getPermmoney()).toDouble())));
				vo.setNowmmoney(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getNowmmoney() == null ? new UFDouble(0) : vo
						.getNowmmoney()).toDouble())));
				vo.setPerymoney(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getPerymoney() == null ? new UFDouble(0) : vo
						.getPerymoney()).toDouble())));
				vo.setNowymoney(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getNowymoney() == null ? new UFDouble(0) : vo
						.getNowymoney()).toDouble())));
				vo.setPermtaxrate(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getPermtaxrate() == null ? new UFDouble(0) : vo
						.getPermtaxrate()).toDouble())));
				vo.setNowmtaxrate(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getNowmtaxrate() == null ? new UFDouble(0) : vo
						.getNowmtaxrate()).toDouble())));
				vo.setPerytaxrate(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getPerytaxrate() == null ? new UFDouble(0) : vo
						.getPerytaxrate()).toDouble())));
				vo.setNowytaxrate(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getNowytaxrate() == null ? new UFDouble(0) : vo
						.getNowytaxrate()).toDouble())));
				vo.setPermtaxmoney(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getPermtaxmoney() == null ? new UFDouble(0) : vo
						.getPermtaxmoney()).toDouble())));
				vo.setNowmtaxmoney(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getNowmtaxmoney() == null ? new UFDouble(0) : vo
						.getNowmtaxmoney()).toDouble())));
				vo.setPerytaxmoney(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getPerytaxmoney() == null ? new UFDouble(0) : vo
						.getPerytaxmoney()).toDouble())));
				vo.setNowytaxmoney(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getNowytaxmoney() == null ? new UFDouble(0) : vo
						.getNowytaxmoney()).toDouble())));
				vo.setPertotaltax(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getPertotaltax() == null ? new UFDouble(0) : vo
						.getPertotaltax()).toDouble())));
				vo.setNowtotaltax(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getNowtotaltax() == null ? new UFDouble(0) : vo
						.getNowtotaltax()).toDouble())));
				vo.setMadjustMoney(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getMadjustMoney() == null ? new UFDouble(0) : vo
						.getMadjustMoney()).toDouble())));
				vo.setYadjustMoney(new UFDouble(SalaryDecryptUtil.decrypt((vo
						.getYadjustMoney() == null ? new UFDouble(0) : vo
						.getYadjustMoney()).toDouble())));
			}
			// end

			((WaTaxForecastModel) getModel()).setTaxForeVos(vos);
			if (null != vos && vos.length > 0) {
				// getFormEditor().getBillCardPanel().getBillModel().setBodyDataVO(vos);
				getModel().setUiState(UIState.EDIT); // 使导出按钮可用
			} else {
				getModel().setUiState(UIState.NOT_EDIT);
			}

			// 2015-11-22 zhousze 查询成功提示，没有展示提示信息 begin
			putValue(HrAction.MESSAGE_AFTER_ACTION,
					ResHelper.getString("taxforecast", "0taxforecast0025")/*
																		 * @res
																		 * "查询操作成功。"
																		 */);
			ShowStatusBarMsgUtil.showStatusBarMsg(
					(String) getValue(MESSAGE_AFTER_ACTION), getContext());
			// end
		}
		// getHeadItem(TaxForecastVO.MONTHCLASS).setEdit(true);
		// getHeadItem(TaxForecastVO.MONTHPERIOD).setEdit(true);
		// getHeadItem(TaxForecastVO.MONTHITEM).setEdit(true);
		// getHeadItem(TaxForecastVO.YEARCLASS).setEdit(true);
		// getHeadItem(TaxForecastVO.YEARPERIOD).setEdit(true);
		// getHeadItem(TaxForecastVO.YEARITEM).setEdit(true);
		// getHeadItem(TaxForecastVO.MONTHCOUNT).setEdit(true);

	}
}
