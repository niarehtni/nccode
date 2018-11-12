package nc.ui.twhr.groupinsurance.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.vo.twhr.groupinsurance.GroupInsuranceSettingVO;

public class GroupinsuranceAddLineAction extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setDefaultData(Object obj) {
		super.setDefaultData(obj);
		GroupInsuranceSettingVO baseDocVO = (GroupInsuranceSettingVO) obj;
		baseDocVO.setPk_group(this.getModel().getContext().getPk_group());
		baseDocVO.setPk_org(this.getModel().getContext().getPk_org());
	}

}
