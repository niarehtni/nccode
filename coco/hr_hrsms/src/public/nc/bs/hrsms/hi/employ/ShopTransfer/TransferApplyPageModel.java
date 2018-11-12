package nc.bs.hrsms.hi.employ.ShopTransfer;

import java.util.HashMap;
import java.util.Map;

import nc.bs.hrsms.hi.HiApplyBasePageModel;
import nc.bs.hrsms.hi.employ.lsnr.ShopPsnDocController;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.pf.ctrl.TransTypeRefCtrl;
import nc.bs.hrss.trn.PsnApplyConsts;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.IRefNode;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.vo.trn.transmng.StapplyVO;

public class TransferApplyPageModel extends HiApplyBasePageModel{
	
	@Override
	protected String getFunCode() {
		return PsnApplyConsts.TRANSFER_FUNC_CODE;
	}

	/**
	 * 页面特殊设置
	 */
	@Override
	protected void setPageSepcial() {
		super.setPageSepcial();
		// 设置岗位试用期限单位
		setTrialdaysText();
	}

	private void setTrialdaysText() {
		LfwView widget = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		FormComp frmPsnInfo = (FormComp) widget.getViewComponents().getComponent(PsnApplyConsts.REGULAR_FORM_PSNINFO);
		String strDisplayName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_hi-res","0c_trn-res0035")/*@res "岗位试用期限"*/;
		frmPsnInfo.getElementById(StapplyVO.TRIALDAYS).setText(strDisplayName);
	}

	/**
	 * 特殊待设置参照集合
	 */
	@Override
	protected Map<String, String> getSpecialRefnodeMap() {
		Map<String, String> specialRefMap = new HashMap<String, String>();
		String transTypeRefId = "refnode_hi_stapply_transtypeid_billtypename";
		specialRefMap.put(transTypeRefId, TransTypeRefCtrl.class.getName());
		return specialRefMap;
	}

	@Override
	protected String getBillType() {
		return PsnApplyConsts.TRANSFER_BILLTYPE_CODE;
	}

	@Override
	protected String getBillInfoForm() {
		return PsnApplyConsts.TRANSFER_FORM_BILLINFO;
	}
	
	@Override
	protected void initPageMetaStruct() {
		
		super.initPageMetaStruct();
		//得到卡片界面的view，对流程类型参照做控制
		LfwView widget = this.getPageMeta().getView("main");
		
		IRefNode region = widget.getViewModels().getRefNode("refnode_hi_stapply_pk_psndoc_name");
		NCRefNode region1 = (NCRefNode) region;
		region1.setDataListener(ShopPsnDocController.class.getName());
	}

}