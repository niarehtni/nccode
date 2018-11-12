package nc.ui.twhr.nhicalc.lazilyload;

import nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager;
import nc.ui.twhr.nhicalc.model.NhicalcAppModel;

public class NhicalcLazilyLoadManager extends LazilyLoadManager{
	
	private NhicalcAppModel model = null;

	
	public NhicalcAppModel getModel() {
		return model;
	}

	public void setModel(NhicalcAppModel model) {
		this.model = model;
	}

}
