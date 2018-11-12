package nc.bs.hrsms.ta.sss.calendar.dft;

import nc.uap.lfw.core.bm.IStateManager;
import nc.uap.lfw.core.bm.dft.AbstractStateManager;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.page.LfwView;
/**
 * ��ʼ״̬�Ĺ�����(�������ڰ������鿴���������ఴť)
 * 
 * @author shaochj ����ƽ����Ŀ��
 * 2015-04-29
 */
public class BatchChangeShift_StateManager extends AbstractStateManager {

	public static final String APP_PSN_COUNT = "app_attr_psn_count";

	@Override
	public IStateManager.State getState(WebComponent target, LfwView widget) {
		Dataset ds = getCtrlDataset(widget);
		if (ds == null) {
			return IStateManager.State.DISABLED;
		}
		if (ds.isEnabled()) {
			return IStateManager.State.HIDDEN;
		} else {
			ApplicationContext appCxt = AppLifeCycleContext.current().getApplicationContext();
			String count = (appCxt == null || (String) appCxt.getAppAttribute(APP_PSN_COUNT) == null) ? "0"
					: (String) appCxt.getAppAttribute(APP_PSN_COUNT);
			// ��ѯ������Ա����
			if (Integer.valueOf(count).intValue() > 0) {
				return IStateManager.State.ENABLED_VISIBLE;
			} else {
				return IStateManager.State.DISABLED;
			}
		}

	}
}
