package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.pubapp.uif2app.actions.DifferentVOSaveAction;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.uif2.UIState;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.nhicalc.AggNhiCalcVO;

public class SaveAction extends DifferentVOSaveAction {
	private nc.vo.ta.pub.TALoginContext context = null;
	private NhiOrgHeadPanel orgpanel = null;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object value = getEditor().getValue();
		((AggNhiCalcVO) value).getParentVO().setPk_org(this.getOrgpanel().getRefModel().getPkValue());
		((AggNhiCalcVO) value).getParentVO()
				.setPk_org_v(getOrgVerion(((AggNhiCalcVO) value).getParentVO().getPk_org()));
		if (getModel().getUiState() == UIState.ADD) {
			doAddSave(value);
		} else if (getModel().getUiState() == UIState.EDIT) {
			doEditSave(value);
		}

		showSuccessInfo();
	}

	public nc.vo.ta.pub.TALoginContext getContext() {
		return context;
	}

	public void setContext(nc.vo.ta.pub.TALoginContext context) {
		this.context = context;
	}

	public NhiOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	private String getOrgVerion(String pk_org) {
		if (null == pk_org) {
			return null;
		}
		String sql = "select pk_vid from org_orgs where pk_org = '" + pk_org + "'";
		try {
			return (String) NCLocator.getInstance().lookup(IUAPQueryBS.class).executeQuery(sql, new ColumnProcessor());
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
