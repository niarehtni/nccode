package nc.ui.hrpub.iopermit.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.vo.hrpub.mdmapping.IOPermitVO;
/**
  batch addLine or insLine action autogen
*/
public class IopermitAddLineAction extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setDefaultData(Object obj) {
		super.setDefaultData(obj);
		IOPermitVO singleDocVO = (IOPermitVO) obj;
		singleDocVO.setPk_group(this.getModel().getContext().getPk_group());
		singleDocVO.setPk_org(this.getModel().getContext().getPk_org());
	}

}