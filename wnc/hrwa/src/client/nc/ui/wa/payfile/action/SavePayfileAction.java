package nc.ui.wa.payfile.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.IActionCode;
import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.wa.util.LocalizationSysinitUtil;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.wa.payfile.model.PayfileAppModel;
import nc.ui.wa.payfile.view.WaClassSynDlg;
import nc.ui.wa.payfile.view.PayfileFormEditor;
import nc.ui.wa.pub.WADelegator;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.payfile.Taxtype;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaState;
import nc.vo.wa.taxrate.TaxBaseVO;

/**
 * 保存
 * 
 * @author: zhoucx
 * @date: 2009-11-27 上午09:12:39
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class SavePayfileAction extends PayfileBaseAction {

	private static final long serialVersionUID = -7422918721898803832L;

	IValidationService validationService;

	public SavePayfileAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.SAVE);
	}

	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		// 20151207 shenliangc NCdp205555686 打开薪资档案节点，第一次点击新增报未知错误
		// 20151205 shenliangc NCdp205554558 修改不扣税的薪资档案，保存时不应校验税率表为空。begin
		if (Taxtype.TAXFREE
				.equalsValue((Integer) ((PayfileFormEditor) getEditor())
						.getBillCardPanel().getHeadItem(PayfileVO.TAXTYPE)
						.getValueObject())) {
			((PayfileFormEditor) getEditor()).getBillCardPanel()
					.getHeadItem(PayfileVO.TAXTABLEID).setNull(false);
		}

		Object value = getEditor().getValue();

		validate(value);

		validateForeginData(value);// 校验外籍员工数据的完整性

		validateTaxtable(value);// 校验税率表

		if (getPayfileModel().getUiState() == UIState.ADD) {
			getPayfileModel().add(value);
			// 同步其它薪资档案
			// by:xiejie3客户要求不提示同步其它薪资档案/山河智能装备股份有限公司 NCdp205210105
			// int yesOrNo =
			// showYesNoMessage(ResHelper.getString("60130payfile","060130payfile0337")/*@res
			// "是否同步其它薪资方案"*/);
			// if(UIDialog.ID_YES == yesOrNo){
			// WaClassVO[] classVOs =
			// WADelegator.getWaClassQuery().queryAllClassidForRollIn((WaLoginContext)getModel().getContext());
			// WaClassSynDlg dialog = new WaClassSynDlg(getEntranceUI());
			// dialog.initData(classVOs);
			// dialog.show();
			// if(dialog.getResult() == UIDialog.ID_OK){
			// int rowCount =
			// dialog.getUICenterPanel().getTablePanel().getTableModel().getRowCount();
			// List<WaClassVO> classList = new ArrayList<WaClassVO>();
			// for(int i=0;i<rowCount;i++){
			// Boolean isSelected =
			// (Boolean)dialog.getUICenterPanel().getTablePanel().getTableModel().getValueAt(i,
			// 0);
			// if(isSelected!=null&&isSelected){
			// classList.add(classVOs[i]);
			// }
			// }

			// if (!classList.isEmpty()) {
			// getPayfileModel().synToOtherClass((PayfileVO) value,
			// classList);
			// }
			// }
			// }
		}

		if (getPayfileModel().getUiState() == UIState.EDIT) {
			getPayfileModel().update(value);
		}
		getPayfileModel().setUiState(UIState.NOT_EDIT);

		// ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getSaveSuccessInfo(),
		// getModel().getContext());
		putValue(HrAction.MESSAGE_AFTER_ACTION,
				IShowMsgConstant.getSaveSuccessInfo());

	}

	/**
	 * 此方法在调用模型的add或update调用。用来对从编辑器中取出的value对象进行校验。
	 * 
	 * @param value
	 */
	@Override
	protected void validate(Object value) {
		if (validationService != null) {
			try {
				validationService.validate(value);
			} catch (ValidationException e) {
				throw new BusinessExceptionAdapter(e);
			}
		}

	}

	/**
	 * 
	 * @param value
	 * @author ward
	 * @throws BusinessException
	 * @date 2018-01-12
	 * @功能描述：外籍员工税率表只允许外籍员工使用
	 */
	protected void validateTaxtable(Object value) throws BusinessException {
		PayfileVO payfileVO = (PayfileVO) value;
		if (null != payfileVO.getTaxtableid()
				&& !"".equals(payfileVO.getTaxtableid())) {
			TaxBaseVO taxBaseVO = (TaxBaseVO) getUAPQueryBS().retrieveByPK(
					TaxBaseVO.class, payfileVO.getTaxtableid());
			if (Integer.valueOf(3).equals(taxBaseVO.getItbltype())) {
				PsndocVO psndocVo = getPsndocVo(payfileVO.getPk_psndoc());
				String isForeign = psndocVo
						.getAttributeValue(LocalizationSysinitUtil
								.getTwhrlPsn("TWHRLPSN01")) != null ? psndocVo
						.getAttributeValue(
								LocalizationSysinitUtil
										.getTwhrlPsn("TWHRLPSN01")).toString()
						: "N";
				if ("N".equals(isForeign)) {
					throw new BusinessException(ResHelper.getString("notice",
							"2notice-tw-000001")/* "所選擇的員工不是外籍員工，請確認員工資訊維護中是否正確維護是否外籍員工。" */);
				}
			}
		}
	}

    /**
     * 如果员工是外籍员工，根据参数设置，判断扣税核算日期/居留证到期日期是否填写
     * 
     * @param value
     * @author ward
     * @date 2018-01-15
     * @throws BusinessException
     */
    protected void validateForeginData(Object value) throws BusinessException {
	PayfileVO payfileVO = (PayfileVO) value;
	String qrySql = "SELECT ITBLTYPE FROM  wa_taxbase wa_taxbase WHERE wa_taxbase.pk_wa_taxbase = '"
		+ payfileVO.getTaxtableid() + "'";
	Integer tableType = (Integer) getUAPQueryBS().executeQuery(qrySql, new ColumnProcessor());
	//mod tank 只有外籍税率表才去校验这个 2020年3月12日 14:23:17
	if (tableType != null && tableType == 3) {

	    PsndocVO psndocVo = getPsndocVo(payfileVO.getPk_psndoc());
	    String isForeign = psndocVo.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")) != null ? psndocVo
		    .getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")).toString() : "N";
	    if ("Y".equals(isForeign)) {
		qrySql = "select value from pub_sysinit where initcode='TWHR08' and pk_org='" + psndocVo.getPk_org()
			+ "' and isnull(dr,0)=0";
		Object sysinitValue = getUAPQueryBS().executeQuery(qrySql, new ColumnProcessor());
		if ("1001ZZ1000000001NCMA".equals(String.valueOf(sysinitValue))) {// 参数如果选择a.扣税核算入境日期
		    if (psndocVo.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN03")) == null) {
			throw new BusinessException(ResHelper.getString("notice", "2notice-tw-000004")/*
												       * 保存失敗
												       * ，
												       * 该員工没有填写扣税核算入境日期
												       */);
		    }
		} else if ("1001ZZ1000000001NCMB".equals(String.valueOf(sysinitValue))) {// 参数如果选择b.居留证到期日期
		    if (psndocVo.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN04")) == null) {
			throw new BusinessException(ResHelper.getString("notice", "2notice-tw-000005")/*
												       * 保存失敗
												       * ，
												       * 该員工無填寫居留证到期日期
												       */);
		    }
		}
	    }
	}

	}

	@Override
	public Set<WaState> getEnableStateSet() {
		return waStateSet;
	}

	private PayfileAppModel getPayfileModel() {
		return (PayfileAppModel) getModel();
	}

	@Override
	public IValidationService getValidationService() {
		return validationService;
	}

	@Override
	public void setValidationService(IValidationService validationService) {
		this.validationService = validationService;
	}

	/**
	 * 获取人员vo
	 * 
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public PsndocVO getPsndocVo(String pk_psndoc) throws BusinessException {
		return (PsndocVO) (getUAPQueryBS().retrieveByPK(PsndocVO.class,
				pk_psndoc));
	}

	private IUAPQueryBS queryBS = null;

	public IUAPQueryBS getUAPQueryBS() {
		if (queryBS == null) {
			queryBS = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		}
		return queryBS;
	}

}