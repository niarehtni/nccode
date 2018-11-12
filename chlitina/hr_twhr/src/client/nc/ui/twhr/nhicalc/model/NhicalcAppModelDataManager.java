package nc.ui.twhr.nhicalc.model;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.SQLHelper;
import nc.itf.pe.scheme.gauge.IGaugeMgtService;
import nc.itf.pe.scheme.gauge.IGaugeQryService;
import nc.itf.twhr.INhicalcMaintain;
import nc.ui.hr.uif2.model.IHrAppModelDataManagerEx;
import nc.ui.hr.uif2.model.IQueryInfo;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.DefaultAppModelDataManager;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.vo.logging.Debug;
import nc.vo.ta.pub.TALoginContext;

import org.apache.commons.lang.StringUtils;

public class NhicalcAppModelDataManager extends DefaultAppModelDataManager
		implements IAppModelDataManagerEx, IHrAppModelDataManagerEx, IQueryInfo {

	private String lastWhereSql;
	AbstractAppModel model;
	int dataCount = 0;
	private boolean showUnUseDataFlag;
	private IGaugeMgtService mgtService;
	private INhicalcMaintain INhicalcMaintain;

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}

	private IGaugeMgtService getMgtService() {
		if (mgtService == null) {
			mgtService = ((IGaugeMgtService) NCLocator.getInstance().lookup(
					IGaugeMgtService.class));
		}
		return mgtService;
	}

	private INhicalcMaintain getQryService() {
		if (INhicalcMaintain == null) {
			INhicalcMaintain = ((INhicalcMaintain) NCLocator.getInstance().lookup(
					INhicalcMaintain.class));
		}
		return INhicalcMaintain;
	}

	public void refresh() {
		TALoginContext context = (TALoginContext) getContext();
//		if ((StringUtils.isBlank(getContext().getPk_org()))) {
//			getModel().initModel(null);
//			return;
//		}
		try {
			Object[] vos = getQryService().queryByCondition(getContext(),
					lastWhereSql);
			getModel().initModel(vos);
			dataCount = (vos == null ? 0 : vos.length);
		} catch (Exception e) {
			Debug.error(e.getMessage(), e);
		}
	}

	public void initModelBySqlWhere(String sqlWhere) {
		lastWhereSql = sqlWhere;
		refresh();
	}

	public void setShowSealDataFlag(boolean showSealDataFlag) {
		showUnUseDataFlag = showSealDataFlag;
	}

	public void initModel() {
		initModelBySqlWhere(lastWhereSql);
	}

	public void setShowUnUseDataFlag(boolean showUnUseDataFlag) {
		this.showUnUseDataFlag = showUnUseDataFlag;
	}

	public int getQueryDataCount() {
		return dataCount;
	}

}
