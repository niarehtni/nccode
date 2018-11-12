package nc.ui.twhr.nhicalc.action;

import nc.ui.pubapp.uif2app.actions.batch.BatchAddLineAction;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.vo.twhr.nhicalc.NhiCalcVO;

public class NhicalcAddLineAction extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;
	private NhiOrgHeadPanel orgpanel = null;

	public NhiOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	@Override
	protected void setDefaultData(Object obj) {
		super.setDefaultData(obj);
		NhiCalcVO baseDocVO = (NhiCalcVO) obj;
		baseDocVO.setPk_group(this.getModel().getContext().getPk_group());
		baseDocVO.setPk_org(getOrgpanel().getRefPane().getRefPK());
	}

}
