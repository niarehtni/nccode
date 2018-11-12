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

	/** ���������ʾ�����ʵ����ʾ�������� */
	private boolean showHRCanceledFlag = false;
	/**
	 * ��ѯ��δ������
	 * 
	 */
	private boolean showHRFuturedFlag = false;
	/**
	 * ������Model����SELECTION_CHANGED�¼���Ϊ��ʵ���������ܣ�Context�е�pk_org��������Ӧ����
	 * �����¼orgpanel�е���֯����������ʵ�ֲ�ѯ��ˢ�¹��� ����heqiaoa 2014-8-11
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

			// ˢ����ģ�ͣ�ֱ��ʹ��orgpanel�е���֯������context�е�pk_orgΪ��ʵ�ֱ������,
			// �����ڵ�SELECTION_CHANGEDʱ�������ģ�
			// ����heqiaoa 2014-08-11
			if (StringUtils.isEmpty(getOrgpanel_pk_org())) { // ���orgpanel_pk_orgΪ�գ���ʹ����model��pk_org
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
		// ��ʵ���������
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
