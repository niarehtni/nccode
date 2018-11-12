package nc.ui.om.hrdept.model;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.om.INaviQueryService;
import nc.ui.om.pub.model.IDeptAdjFeaturedAppModelDataManagerEx;
import nc.ui.om.pub.model.IFeaturedAppModelDataManagerEx;
import nc.ui.uif2.model.DefaultAppModelDataManager;

import org.apache.commons.lang.StringUtils;

public class OrgTreeDataManager extends DefaultAppModelDataManager implements IFeaturedAppModelDataManagerEx,
		IDeptAdjFeaturedAppModelDataManagerEx {
	private String lastWhereSql;

	/** 这里借用显示封存来实现显示撤销功能 */
	private boolean showHRCanceledFlag = false;
	/**
	 * 查询出未来部门
	 * 
	 */
	private boolean showHRFuturedFlag = false;
	/**
	 * 在左树Model发出SELECTION_CHANGED事件后，为了实现其他功能，Context中的pk_org被做了相应更改
	 * 这里记录orgpanel中的组织主键，用于实现查询和刷新功能 ——heqiaoa 2014-8-11
	 */
	private String orgpanel_pk_org = "";

	public String getLastWhereSql() {
		return lastWhereSql;
	}

	public boolean isShowHRCanceledFlag() {
		return showHRCanceledFlag;
	}

	public boolean isShowHRFuturedFlag() {
		return showHRFuturedFlag;
	}

	public void setLastWhereSql(String lastWhereSql) {
		this.lastWhereSql = lastWhereSql;
	}

	@Override
	public void initModel() {
		initModelBySqlWhere(lastWhereSql);
	}

	@Override
	public void initModelBySqlWhere(String sqlWhere) {
		this.lastWhereSql = sqlWhere;
		refresh();

	}

	@Override
	public void refresh() {
		try {
			List<Object> objList = new ArrayList<Object>();

			// 刷新树模型，直接使用orgpanel中的组织主键（context中的pk_org为了实现别的需求,
			// 在树节点SELECTION_CHANGED时已做更改）
			// ——heqiaoa 2014-08-11
			if (StringUtils.isEmpty(getOrgpanel_pk_org())) { // 如果orgpanel_pk_org为空，则使用树model中pk_org
				setOrgpanel_pk_org(getContext().getPk_org());
			}
			Object[] objs = NCLocator
					.getInstance()
					.lookup(INaviQueryService.class)
					.queryAOSMembersCascadeByHROrgPK(getOrgpanel_pk_org(), false, isShowHRCanceledFlag(),
							isShowHRFuturedFlag(), true, null, lastWhereSql);
			getModel().initModel(objs);
		} catch (Exception e) {
			Logger.error(e.getMessage());

		}
	}

	@Override
	public void setShowHRCanceledFlag(boolean showHRCanceledFlag) {
		this.showHRCanceledFlag = showHRCanceledFlag;

	}

	@Override
	public void setShowSealDataFlag(boolean showSealDataFlag) {
		// 不实现这个方法
	}

	public String getOrgpanel_pk_org() {
		return orgpanel_pk_org;
	}

	public void setOrgpanel_pk_org(String orgpanel_pk_org) {
		this.orgpanel_pk_org = orgpanel_pk_org;
	}

	@Override
	public void setShowHRFuturedFlag(boolean showHRFuturedFlag) {
		this.showHRFuturedFlag = showHRFuturedFlag;

	}

}
