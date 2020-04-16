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
 * ����
 * 
 * @author: zhoucx
 * @date: 2009-11-27 ����09:12:39
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
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
		// 20151207 shenliangc NCdp205555686 ��н�ʵ����ڵ㣬��һ�ε��������δ֪����
		// 20151205 shenliangc NCdp205554558 �޸Ĳ���˰��н�ʵ���������ʱ��ӦУ��˰�ʱ�Ϊ�ա�begin
		if (Taxtype.TAXFREE
				.equalsValue((Integer) ((PayfileFormEditor) getEditor())
						.getBillCardPanel().getHeadItem(PayfileVO.TAXTYPE)
						.getValueObject())) {
			((PayfileFormEditor) getEditor()).getBillCardPanel()
					.getHeadItem(PayfileVO.TAXTABLEID).setNull(false);
		}

		Object value = getEditor().getValue();

		validate(value);

		validateForeginData(value);// У���⼮Ա�����ݵ�������

		validateTaxtable(value);// У��˰�ʱ�

		if (getPayfileModel().getUiState() == UIState.ADD) {
			getPayfileModel().add(value);
			// ͬ������н�ʵ���
			// by:xiejie3�ͻ�Ҫ����ʾͬ������н�ʵ���/ɽ������װ���ɷ����޹�˾ NCdp205210105
			// int yesOrNo =
			// showYesNoMessage(ResHelper.getString("60130payfile","060130payfile0337")/*@res
			// "�Ƿ�ͬ������н�ʷ���"*/);
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
	 * �˷����ڵ���ģ�͵�add��update���á������Դӱ༭����ȡ����value�������У�顣
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
	 * @�����������⼮Ա��˰�ʱ�ֻ�����⼮Ա��ʹ��
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
							"2notice-tw-000001")/* "���x��ĆT�������⼮�T����Ո�_�J�T���YӍ�S�o���Ƿ����_�S�o�Ƿ��⼮�T����" */);
				}
			}
		}
	}

    /**
     * ���Ա�����⼮Ա�������ݲ������ã��жϿ�˰��������/����֤���������Ƿ���д
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
	//mod tank ֻ���⼮˰�ʱ��ȥУ����� 2020��3��12�� 14:23:17
	if (tableType != null && tableType == 3) {

	    PsndocVO psndocVo = getPsndocVo(payfileVO.getPk_psndoc());
	    String isForeign = psndocVo.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")) != null ? psndocVo
		    .getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")).toString() : "N";
	    if ("Y".equals(isForeign)) {
		qrySql = "select value from pub_sysinit where initcode='TWHR08' and pk_org='" + psndocVo.getPk_org()
			+ "' and isnull(dr,0)=0";
		Object sysinitValue = getUAPQueryBS().executeQuery(qrySql, new ColumnProcessor());
		if ("1001ZZ1000000001NCMA".equals(String.valueOf(sysinitValue))) {// �������ѡ��a.��˰�����뾳����
		    if (psndocVo.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN03")) == null) {
			throw new BusinessException(ResHelper.getString("notice", "2notice-tw-000004")/*
												       * ����ʧ��
												       * ��
												       * �ÆT��û����д��˰�����뾳����
												       */);
		    }
		} else if ("1001ZZ1000000001NCMB".equals(String.valueOf(sysinitValue))) {// �������ѡ��b.����֤��������
		    if (psndocVo.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN04")) == null) {
			throw new BusinessException(ResHelper.getString("notice", "2notice-tw-000005")/*
												       * ����ʧ��
												       * ��
												       * �ÆT���o�����֤��������
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
	 * ��ȡ��Աvo
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