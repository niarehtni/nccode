package nc.ui.wa.datainterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nc.bs.framework.common.NCLocator;
import nc.itf.wa.datainterface.IReportExportService;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

/**
 * Íßx¿òßx“ñÊÂ¼þ
 * 
 * @author sunsx_000
 * 
 */
public class CheckChangeHandler implements ActionListener {
	UIComboBox cboVatCode;
	WaClassRefModel4ITR waClassRefModel;
	String pk_org;

	CheckChangeHandler(UIComboBox cboVATCode, String pkorg,
			WaClassRefModel4ITR classRefModel) {
		cboVatCode = cboVATCode;
		pk_org = pkorg;
		waClassRefModel = classRefModel;
	}

	public void actionPerformed(ActionEvent e) {
		IReportExportService exportSrv = (IReportExportService) NCLocator
				.getInstance().lookup(IReportExportService.class);
		try {
			String[] pk_orgs = null;
			if (((UICheckBox) e.getSource()).isSelected()) {
				String[] vatnos = null;

				vatnos = exportSrv.getAllOrgVATNumber();

				cboVatCode.removeAllItems();
				if (vatnos != null && vatnos.length > 0) {
					cboVatCode.addItems(vatnos);
				}

				cboVatCode.setEnabled(true);

				if (!StringUtils.isEmpty((String) cboVatCode
						.getSelectdItemValue())) {
					pk_orgs = exportSrv
							.getAllOrgByVATNumber((String) cboVatCode
									.getSelectdItemValue());
				}

			} else {
				cboVatCode.removeAllItems();
				if (!StringUtils.isEmpty(pk_org)) {
					cboVatCode.addItem(exportSrv.getOrgVATNumber(this.pk_org));
				}
				cboVatCode.setEnabled(false);

				pk_orgs = new String[] { pk_org };
			}

			waClassRefModel.setPk_orgs(pk_orgs);
		} catch (BusinessException e1) {

		}
	}
}
