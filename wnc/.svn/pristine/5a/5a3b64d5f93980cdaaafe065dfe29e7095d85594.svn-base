package nc.ui.wa.othersource.model;

import nc.ui.uif2.model.HierachicalDataAppModel;

/**
 * @author daicy
 *
 */
public class OtherHierachicalDataAppModel  extends HierachicalDataAppModel {
	

	/* (non-Javadoc)
	 * @see nc.ui.uif2.model.HierachicalDataAppModel#initModel(java.lang.Object)
	 */
	@Override
	public void initModel(Object data) {
		
		Object[] objs = null;
		try {
			objs = getService().queryByDataVisibilitySetting(getContext());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		super.initModel(objs);
		
	}
}
