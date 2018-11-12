package nc.bs.hrsms.ta.shift.pagemodel;

import nc.bs.hrss.pub.BillCoderUtils;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.PageModel;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.page.LfwView;

public class StoreShiftCardPageModel extends PageModel{

	@Override
	protected String getFunCode() {
		return "E2060501";
	}
	/**
	 * 初始化个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 主片段
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		setPageSepcial(viewMain);
	}
	
	/**
	 * 页面特殊设置
	 */
	protected void setPageSepcial(LfwView viewMain) {
		// 单据类型
		String billType = "HRTA_shift";
		String pk_group = SessionUtil.getPk_group();
		
//		String pk_hrorg = SessionUtil.getPsndocVO().getPk_hrorg();
		String pk_hrorg = SessionUtil.getHROrg();
		FormComp frmBill = (FormComp) viewMain.getViewComponents().getComponent("base_form");
		if (frmBill == null) {
			return;
		}
		FormElement bill_code = frmBill.getElementById("code");
		if (bill_code != null) {
			if (BillCoderUtils.isAutoGenerateBillCode(pk_group, pk_hrorg, billType)) {
				bill_code.setEnabled(false);
			}
		}
	}
}
