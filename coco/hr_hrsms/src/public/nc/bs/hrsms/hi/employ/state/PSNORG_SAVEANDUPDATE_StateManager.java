package nc.bs.hrsms.hi.employ.state;

import nc.bs.hrss.hi.psninfo.PsninfoUtil;
import nc.uap.lfw.core.bm.dft.AbstractStateManager;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.page.LfwView;

public class PSNORG_SAVEANDUPDATE_StateManager extends AbstractStateManager{

	@Override
	public State getState(WebComponent arg0, LfwView arg1) {
		String infoset = PsninfoUtil.getCurrDataset();
		if("hi_psnorg".equals(infoset)){
			return State.VISIBLE;
		}else{
			return State.HIDDEN;
		}
	}

}
