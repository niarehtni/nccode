package nc.bs.hrsms.hi;

import java.util.Map;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrss.pub.BillCoderUtils;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.common.TaApplyConsts;
import nc.itf.hr.pf.IHrPf;
import nc.pubitf.para.SysInitQuery;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.core.refnode.IRefNode;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.pub.BusinessException;
/**
 * 人事变动申请PageModel
 *
 */
public abstract class HiListBasePageModel extends AdvancePageModel {

	/**
	 * 初始化个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 主片段
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		// 设置参照DatasetListener
		setRefnodesDsListener(viewMain);
		// 页面特殊设置
		setPageSepcial();
	}

	@Override
	protected String getQueryTempletKey() {
		return null;
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
//		CatagoryPanel cp = new CatagoryPanel();
//		cp.setDataProvider(new TrnCatagoryProvider());
//		cp.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("init","hrss-qrytemp-item-0044")/*@res "单据类型"*/);
		return new IPagePanel[]{new CanvasPanel(), new MngShopPanel(), new SimpleQueryPanel()};
//		return new IPagePanel[]{cp};
	}

	@Override
	protected String getRightPage() {
		return null;
	}

	@Override
	protected void afterInternalInitialize() {
		super.afterInternalInitialize();
		
	}
	
	/**
	 * 设置参照DatasetListener
	 */
	private void setRefnodesDsListener(LfwView viewMain) {
		// 获得单据页面的所有参照
		IRefNode[] refnodes = viewMain.getViewModels().getRefNodes();
		if (refnodes == null || refnodes.length == 0) {
			return;
		}
		// 需要特殊设置的参照集合
		Map<String, String> specialRefMap = getSpecialRefnodeMap();
		// 修改页面的参照类型的DatasetListener
		for (IRefNode refnode : refnodes) {
			if (specialRefMap.containsKey(refnode.getId())) {
				((NCRefNode) refnode).setDataListener(specialRefMap.get(refnode.getId()));
			} else {
				((NCRefNode) refnode).setDataListener(HiApplyRefController.class.getName());
			}
		}
	}

	/**
	 * 获得单据类型
	 * 
	 * @return
	 */
	protected abstract String getBillType();
	
	/**
	 * 获得单据详细Form
	 * 
	 * @return
	 */
	protected abstract String getBillInfoForm();
	
	/**
	 * 页面特殊设置
	 */
	protected void setPageSepcial() {
		SessionBean session = SessionUtil.getSessionBean();
		String pk_group = session.getContext().getPk_group();
		String pk_org = session.getPk_org();
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		FormComp frmBill = (FormComp) viewMain.getViewComponents().getComponent(getBillInfoForm());
		if (frmBill != null) {
			FormElement bill_code = frmBill.getElementById(TaApplyConsts.BILL_CODE);
			if (bill_code != null) {
				if (BillCoderUtils.isAutoGenerateBillCode(pk_group, pk_org, getBillType())) {
					bill_code.setEnabled(false);
				}
			}
			FormElement transtypeid = frmBill.getElementById(TaApplyConsts.TRANS_TYPE);
			if (transtypeid != null) {
				if (isDirectApprove(pk_org, getBillType())) {
					transtypeid.setEnabled(false);
				}
			}
		}
	}

	/**
	 * 是否直批
	 * 
	 * @param pk_org
	 * @param billTypeCode
	 * @return
	 */
	private boolean isDirectApprove(String pk_org, String billTypeCode) {
		Integer type = null;
		try {
			type = SysInitQuery.getParaInt(pk_org, IHrPf.hashBillTypePara.get(billTypeCode));
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return type != null && type == HRConstEnum.APPROVE_TYPE_FORCE_DIRECT;
	}

	/**
	 * 设置特殊的参照设置<br/>
	 * Map<参照Id, 参照的DatasetListener><br/>
	 * 
	 * @return
	 */
	protected Map<String, String> getSpecialRefnodeMap() {
		return null;
	}

	/**
	 * 设置考勤数据的小时位数<br/>
	 * String[]待设置的考勤数据字段数组<br/>
	 * 
	 * @return
	 */
	protected String[] getTimeDataFields() {
		return null;
	}

	@Override
	protected String getFunCode() {
		return "E2060301";
	}
}