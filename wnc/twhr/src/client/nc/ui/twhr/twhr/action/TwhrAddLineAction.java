package nc.ui.twhr.twhr.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.vo.twhr.nhicalc.BaoAccountVO;
/**
  batch addLine or insLine action autogen
*/
public class TwhrAddLineAction extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setDefaultData(Object obj) {
		super.setDefaultData(obj);
		BaoAccountVO singleDocVO = (BaoAccountVO) obj;
		singleDocVO.setPk_group(this.getModel().getContext().getPk_group());
		singleDocVO.setPk_org(this.getModel().getContext().getPk_org());
	}

}