package nc.ui.twhr.diffinsurancea.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.vo.twhr.diffinsurance.DiffinsuranceVO;
/**
  batch addLine or insLine action autogen
*/
public class DiffinsuranceaAddLineActiona extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setDefaultData(Object obj) {
		super.setDefaultData(obj);
		DiffinsuranceVO singleDocVO = (DiffinsuranceVO) obj;
		singleDocVO.setPk_group(this.getModel().getContext().getPk_group());
		singleDocVO.setPk_org(this.getModel().getContext().getPk_org());
	}

}