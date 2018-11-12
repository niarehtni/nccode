package nc.ui.twhr.groupgrade.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.vo.twhr.groupinsurance.GroupInsuranceGradeVO;
/**
  batch addLine or insLine action autogen
*/
public class GroupgradeAddLineAction extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setDefaultData(Object obj) {
		super.setDefaultData(obj);
		GroupInsuranceGradeVO singleDocVO = (GroupInsuranceGradeVO) obj;
		singleDocVO.setPk_group(this.getModel().getContext().getPk_group());
		singleDocVO.setPk_org(this.getModel().getContext().getPk_org());
	}

}