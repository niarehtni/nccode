package nc.ui.bd.twhr.view;

import nc.ui.bd.commoninfo.accperiod.view.AccperiodMthRefModel;

public class HealthMonthSelectDlg extends AccperiodMthRefModel {

	/* (non-Javadoc)
	 * @see nc.ui.bd.commoninfo.accperiod.view.AccperiodMthRefModel#setRefNodeName(java.lang.String)
	 */
	@Override
	public void setRefNodeName(String refNodeName) {
		
		super.setRefNodeName(refNodeName);
		super.setDefaultFieldCount(1);
	}
	
	
}
