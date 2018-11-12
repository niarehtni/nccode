package nc.ui.wa.payslip.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IPayslipService;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.hr.uif2.model.IQueryInfo;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.model.DefaultAppModelDataManager;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.ui.wa.payslip.view.PaySlipForm;
import nc.ui.wa.pub.OrderUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.payslip.AggPayslipVO;
import nc.vo.wa.payslip.PayslipVO;

/**
 * 
 * @author: xuanlt
 * @date: 2010-1-27 下午03:38:21
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class PayslipModelDataManager extends DefaultAppModelDataManager
		implements IAppModelDataManagerEx, IQueryInfo {

	private String condition;
	private PaySlipForm form;
	private int queryResultCount = 0;

	public PaySlipForm getForm() {
		return form;
	}

	public void setForm(PaySlipForm form) {
		this.form = form;
	}

	@Override
	public void initModel() {

		// 初始化数据，当前选择的组织，薪资方案及其期间
		initModelBySqlWhere(" pk_group='" + getContext().getPk_group()
				+ "' and pk_org='" + getContext().getPk_org() + "'");
		// +"' and classid='"+context.getPk_wa_class()+"' and cyear='"+context.getWaYear()+"' and cperiod='"+context.getWaPeriod()+"'");
	}

	@Override
	public void initModelBySqlWhere(String sqlWhere) {
		condition = sqlWhere;
		refresh();
	}

	@Override
	public void refresh() {
		// PayslipVO[] vos = null;
		AggPayslipVO[] vos = null;
		// 未选择组织时不查询后台数据库
		if (getContext().getPk_org() != null) {
			IPayslipService service = NCLocator.getInstance().lookup(
					IPayslipService.class);
			try {
				// vos =service.queryAggPayslipByCond(condition);
				vos = service.queryAggPayslipByCond(condition, getContext());

			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
				MessageDialog.showErrorDlg(getContext().getEntranceUI(), null,
						e.getMessage());
			}
		}
		getModel().initModel(vos);
	}

	@Override
	public void setShowSealDataFlag(boolean showSealDataFlag) {

	}

	public void refreshPayslipData() throws BusinessException {
		// 未选择行数据时不可用
		Object obj = getModel().getSelectedData();
		if (obj == null)
			return;

		String strWhere = ((PayslipAppModel) getModel()).getStrLastWhereSQL();
		String cyear = ((PayslipAppModel) getModel()).getCyear();
		String cperiod = ((PayslipAppModel) getModel()).getCperiod();
		Integer intTimes = ((PayslipAppModel) getModel()).getIntTimes();

		IPayslipService service = NCLocator.getInstance().lookup(
				IPayslipService.class);

		String orderCondition = OrderUtil
				.getOrderby(((PayslipAppModel) getModel()).getVectSortFields());

		AggPayslipVO billVO = service.queryAggPayslipByCond(
				((PayslipVO) ((AggPayslipVO) obj).getParentVO())
						.getPk_wa_class(), cyear, cperiod,
				((PayslipVO) ((AggPayslipVO) obj).getParentVO()).getType(),
				getContext());
		if (billVO == null
				|| billVO.getParentVO() == null
				|| !((PayslipVO) billVO.getParentVO()).getAccyear().equals(
						cyear)
				|| !((PayslipVO) billVO.getParentVO()).getAccmonth().equals(
						cperiod)) {
			Logger.debug("请先设置工资条");
			// throw new
			// BusinessException(ResHelper.getString("6013payslp","06013payslp0157")/*@res
			// "请先设置工资条"*/);
			MessageDialog.showErrorDlg(getForm(), ResHelper.getString(
					"6013commonbasic", "06013commonbasic0260")/*
															 * @ res "错误"
															 */, ResHelper
					.getString("6013payslp", "06013payslp0157")/*
																 * @res
																 * "请先设置工资条"
																 */);
			return;
		} else {
			ArrayList<HashMap<String, Object>> objs = service
					.queryPayslipDataByCond(getContext(), (AggPayslipVO) obj,
							cyear, cperiod, intTimes, strWhere, orderCondition);
			// 2016-12-22 zhousze 薪资加密：这里处理薪资条管理节点查询数据解密 begin
			for (int i = 0; i < objs.size(); i++) {
				HashMap<String, Object> map = objs.get(i);
				Object[] keys = map.keySet().toArray();
				for (int j = 0; j < keys.length; j++) {
					if (((String) keys[j]).startsWith("wa_dataf")) {
						map.put((String) keys[j],
								new BigDecimal(map.get(keys[j]) == null ? 0
										: SalaryDecryptUtil
												.decrypt(((BigDecimal) map
														.get(keys[j]))
														.doubleValue())));
					}
				}
			}
			// end

			getForm().reCreateCardPane(billVO);
			getForm().getBillCardPanel().setBillValueVO(billVO);
			getForm().getBillCardPanel().getBillModel().clearBodyData();
			getForm().setDatas(objs);
			for (int i = 0; objs != null && i < objs.size(); i++) {
				getForm().getBillCardPanel().getBillModel().insertRow(i);
				BillItem[] items = getForm().getBillCardPanel().getBillModel()
						.getBodyItems();
				for (int j = 0; items != null && j < items.length; j++) {
					Object value = objs.get(i).get(items[j].getKey());
					if (items[j].getKey() != null && value != null) {
						getForm().getBillCardPanel().getBillModel("item")
								.setValueAt(value, i, items[j].getKey());
						if (value instanceof Number) {
							getForm()
									.getDatas()
									.get(i)
									.put(items[j].getKey(),
											""
													+ getForm()
															.getBillCardPanel()
															.getBillModel(
																	"item")
															.getValueAt(
																	i,
																	items[j].getKey()));
						}
					}
				}
			}
			if (objs != null && objs.size() > 0) {
				queryResultCount = objs.size();
			} else {
				queryResultCount = 0;
			}

		}
		((PayslipAppModel) getModel()).setQueryPayslipData(true);
	}

	@Override
	public int getQueryDataCount() {
		return queryResultCount;
	}

}
