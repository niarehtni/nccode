package nc.ui.ta.psndoc.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.pub.bill.BillItem;
import nc.vo.org.HROrgVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class AddAction extends nc.ui.hr.uif2.action.AddAction {
	private static final long serialVersionUID = 1L;

	private HrBillFormEditor cardForm;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);

		if (this.getModel().getUiState().equals(nc.ui.uif2.UIState.ADD)) {
			String pk_org = this.getModel().getContext().getPk_org();
			try {
				boolean isTWLocalizationEnabled = SysInitQuery.getParaBoolean(pk_org, "TWHR01").booleanValue();
				if (isTWLocalizationEnabled) {
					String weekFormCheckScope = SysInitQuery.getParaString(pk_org, "TWHRT05");

					String defaultWeekForm = "";
					if (StringUtils.isEmpty(weekFormCheckScope)) {
						throw new BusinessException("組織參數未設定工時類型校驗範圍 [TWHRT05]");
					} else if (weekFormCheckScope.equals("0")) { // 公司
						HROrgVO hrorg = (HROrgVO) NCLocator.getInstance().lookup(IUAPQueryBS.class)
								.retrieveByPK(HROrgVO.class, pk_org);
					} else if (weekFormCheckScope.equals("1")) { // 部門

					} else { // 員工
						defaultWeekForm = SysInitQuery.getParaString(pk_org, "TWHRT04");
					}

					BillItem item = this.getCardForm().getBillCardPanel().getHeadItem("weekform");
					item.setValue(defaultWeekForm);
					item.setEnabled(true);
				} else {
					BillItem item = this.getCardForm().getBillCardPanel().getHeadItem("weekform");
					item.setValue(null);
					item.setEnabled(false);
				}
			} catch (BusinessException ex) {
				// TODO 自動產生的 catch 區塊
				ex.printStackTrace();
			}
		}
	}

	public HrBillFormEditor getCardForm() {
		return cardForm;
	}

	public void setCardForm(HrBillFormEditor cardForm) {
		this.cardForm = cardForm;
	}
}
