package nc.ui.wa.taxaddtional.model;

import nc.ui.hrcp.cindex.model.HRListMetaDataDataSource;
import nc.ui.wa.pub.WaPrintCheckUtil;


public class TaxaddtionalDataSource extends HRListMetaDataDataSource{

	private static final long serialVersionUID = 7390491679230078700L;

	@Override
	public Object[] getMDObjects() {
		TaxaddtionalModel model = (TaxaddtionalModel) getModel();
		if (model == null) {
			return null;
		}
		return WaPrintCheckUtil.checkZero(model.getPsntaxvos(), getModel()
				.getContext().getPk_org());
	}
}
