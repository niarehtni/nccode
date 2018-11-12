package nc.bs.hrsms.ta.sss.ShopAttendance.shopattendance;

//import nc.bs.hrss.ta.timedata.lsnr.TimeDataUtil;
import nc.bs.hrsms.ta.sss.ShopAttendance.lsnr.ShopAttendanceUtil;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.bm.IStateManager;
import nc.uap.lfw.core.bm.dft.AbstractStateManager;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UITabComp;
import nc.vo.jcom.lang.StringUtil;

public class ShopAttCancelManager extends AbstractStateManager {

	@Override
	public IStateManager.State getState(WebComponent target, LfwView view) {
		UIMeta um = (UIMeta) LfwRuntimeEnvironment.getWebContext().getUIMeta();
		UITabComp tabComp = (UITabComp) um.findChildById(ShopAttendanceUtil.TAB_TIME_DATA);
		String currentItem = tabComp.getCurrentItem();
		if(StringUtil.isEmptyWithTrim(currentItem)){
			return IStateManager.State.DISABLED;
		}else if("1".equals(currentItem)){
			return IStateManager.State.ENABLED;
		}else{
			return IStateManager.State.DISABLED;
		}
	}
}
