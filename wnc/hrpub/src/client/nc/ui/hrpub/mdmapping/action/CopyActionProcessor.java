package nc.ui.hrpub.mdmapping.action;

import nc.ui.pubapp.uif2app.actions.intf.ICopyActionProcessor;
import nc.vo.hrpub.mdmapping.AggMDClassVO;
import nc.vo.hrpub.mdmapping.MDClassVO;
import nc.vo.hrpub.mdmapping.MDPropertyVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.uif2.LoginContext;

public class CopyActionProcessor implements ICopyActionProcessor<AggMDClassVO> {

	@Override
	public void processVOAfterCopy(AggMDClassVO aggvo, LoginContext context) {
		processHeadVO(aggvo.getParentVO());
		processBodyVO(aggvo.getAllChildrenVO());
	}

	private void processBodyVO(CircularlyAccessibleValueObject[] allChildrenVO) {
		for (CircularlyAccessibleValueObject vo : allChildrenVO) {
			((MDPropertyVO) vo).setTs(null);
			((MDPropertyVO) vo).setPk_mdclass(null);
			((MDPropertyVO) vo).setPk_mdproperty(null);
		}
	}

	private void processHeadVO(MDClassVO parentVO) {
		parentVO.setPk_mdclass(null);
		parentVO.setTs(null);
	}

}
