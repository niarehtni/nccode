package nc.ui.wa.datainterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nc.bs.framework.common.NCLocator;
import nc.itf.wa.datainterface.IReportExportService;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class VATChangeHandler implements ActionListener {
	UICheckBox chkSum;
	WaClassRefModel4ITR rfmWaClass;
	String pk_org;

	public VATChangeHandler(UICheckBox chkSumReport, String pk_org,
			WaClassRefModel4ITR waClassRefModel4ITR) {
		this.chkSum = chkSumReport;
		this.pk_org = pk_org;
		this.rfmWaClass = waClassRefModel4ITR;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!StringUtils.isEmpty((String) ((UIComboBox) e.getSource())
				.getSelectdItemValue())) {
			UIComboBox cboVatCode = (UIComboBox) e.getSource();
			IReportExportService exportSrv = (IReportExportService) NCLocator
					.getInstance().lookup(IReportExportService.class);
			try {
				String[] pk_orgs = null;
				if (this.getChkSum().isSelected()) {
					if (!StringUtils.isEmpty((String) cboVatCode
							.getSelectdItemValue())) {
						pk_orgs = exportSrv
								.getAllOrgByVATNumber((String) cboVatCode
										.getSelectdItemValue());
					}

				} else {
					pk_orgs = new String[] { pk_org };
				}

				rfmWaClass.reset();
				rfmWaClass.clearData();
				rfmWaClass.setPk_orgs(pk_orgs);
			} catch (BusinessException e1) {

			}
		}
	}

	public UICheckBox getChkSum() {
		return chkSum;
	}

	public void setChkSum(UICheckBox chkSum) {
		this.chkSum = chkSum;
	}

	public WaClassRefModel4ITR getRfmWaClass() {
		return rfmWaClass;
	}

	public void setRfmWaClass(WaClassRefModel4ITR rfmWaClass) {
		this.rfmWaClass = rfmWaClass;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}
}
