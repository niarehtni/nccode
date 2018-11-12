package nc.ui.wa.payroll.view;

import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.funcnode.ui.FuncletInitData;
import nc.hr.utils.PubEnv;
import nc.itf.hr.pf.IHrPf;
import nc.itf.hr.wa.IWaClass;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.msg.PfLinkData;
import nc.ui.pubapp.uif2app.event.card.CardBodyBeforeTabChangedEvent;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.wa.payroll.model.PayrollAppModel;
import nc.ui.wa.payroll.model.PayrollTemplateContainer;
import nc.ui.wa.pub.WADelegator;
import nc.ui.wa.ref.WaIncludeClassRefModel;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.payroll.AggPayrollVO;
import nc.vo.wa.payroll.PayrollVO;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.SalaryPmtCommonValue;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

/**
 * �������뿨Ƭ
 * 
 * @author: liangxr
 * @date: 2010-5-17 ����11:30:57
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
@SuppressWarnings("serial")
public class PayrollCardForm extends HrBillFormEditor implements
		BillCardBeforeEditListener {

	/** �Ƿ�������ڵ� */
	private boolean isInputPnl = true;

	@Override
	public void initUI() {
		super.initUI();
		this.isInputPnl = true;
		if (getPayrollModel().isApproveSite()) {
			this.isInputPnl = false;
		}
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
	}

	@Override
	public void afterEdit(BillEditEvent evt) {
		if (PayrollVO.BILLCODE.equals(evt.getKey())) {
			if (billCardPanel.getHeadItem(PayrollVO.BILLNAME).getValueObject() == null) {
				Object billcode = billCardPanel.getHeadItem(PayrollVO.BILLCODE)
						.getValueObject();
				billCardPanel.setHeadItem(PayrollVO.BILLNAME, billcode);
			}
		} else if (PayrollVO.PK_WA_CLASS.equals(evt.getKey())) {
			UIRefPane refPane = getWaClassRefPane();
			String year = (String) refPane.getRefModel().getValue(
					SalaryPmtCommonValue.CYEAR);
			String period = (String) refPane.getRefModel().getValue(
					SalaryPmtCommonValue.CPERIOD);
			Integer batch = (Integer) refPane.getRefModel().getValue(
					PayrollVO.BATCH);
			billCardPanel.setHeadItem(PayrollVO.CYEAR, year);
			billCardPanel.setHeadItem("classperiod", year + period);
			billCardPanel.setHeadItem(PayrollVO.CPERIOD, period);
			billCardPanel.setHeadItem(PayrollVO.BATCH, batch);
			// ����wacontext
			WaLoginContext context = createContext(refPane.getRefPK(), year,
					period);
			showDetail(context);
			getWaClassRefPane().setRefModel(new WaIncludeClassRefModel());
			setWaClassWhere();
			getHeadItem(PayrollVO.PK_WA_CLASS).setValue(
					context.getPk_wa_class());
			// guoqt ������ʱ�������Ϳ��Ա༭
			if (getApproveType() != null
					&& getApproveType().equals(
							HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW)) {
				getHeadItem(PayrollVO.TRANSTYPEID).setEnabled(true);
			} else {
				getHeadItem(PayrollVO.TRANSTYPEID).setEnabled(false);
			}
			// 20151214 shenliangc NCdp205559130
			// н�ʹ�������һ�к����������ûس����У��ڶ����к�Ϊ�գ�����ʱ����
			// ȥ���س����С��ӱ�����Ҽ���
			getBillCardPanel().getBodyPanel().setAutoAddLine(false);
			getBillCardPanel().getBodyPanel().setBBodyMenuShow(false);
		} else if (PayrollVO.TRANSTYPEID.equals(evt.getKey())) {
			UIRefPane refPane = (UIRefPane) billCardPanel.getHeadItem(
					PayrollVO.TRANSTYPEID).getComponent();
			billCardPanel.setHeadItem(PayrollVO.BILLTYPE, refPane.getRefCode());
		}
	}

	/***************************************************************************
	 * �õ�������ʽ 3�ύ������/2ֱ��/1������/0ϵͳ�ж�<br>
	 * Created on 2011-3-20 18:24:32<br>
	 * 
	 * @return Integer
	 * @throws Exception
	 * @author Rocex Wang
	 ***************************************************************************/
	protected Integer getApproveType() {
		try {
			return SysInitQuery.getParaInt(getModel().getContext().getPk_org(),
					IHrPf.hashBillTypePara.get(((PayrollAppModel) getModel())
							.getBillType()));
		} catch (BusinessException ex) {
			// XXX Auto-generated catch block
			Logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (!(CardBodyBeforeTabChangedEvent.class == event.getClass())) {
			super.handleEvent(event);
		}
		// 2015-11-3 zhousze н�ʷ��Ź������������Σ��ӱ���ʧ�����⡣���������ӱ�ʼ�տɼ� begin
		getBillCardPanel().getBodyUIPanel().setVisible(true);
		// end
		// // guopeng NCdp205495862 ֻ������ʼ������
		// if (AppEventConst.SELECTION_CHANGED.equals(event.getType()) ||
		// AppEventConst.DATA_REFRESHED.equals(event.getType()))
		// {
		// refreshSumRow();
		// }
		if ("copy".equals(event.getType())) {
			setCopyStates();
		} else if (AppEventConst.UISTATE_CHANGED.equals(event.getType())) {

			if (UIState.ADD == getModel().getUiState()) {

				getWaClassRefPane().setRefModel(new WaIncludeClassRefModel());
				setWaClassWhere();
				// guoqt ��������Ĭ�ϲ��ɱ༭
				getHeadItem(PayrollVO.TRANSTYPEID).setEnabled(false);
				if (getModel().getContext().getInitData() != null) {
					// �����н�ʷ��Žڵ�ת����ʱ����ĳ�ʼ��
					PfLinkData linkDta = (PfLinkData) ((FuncletInitData) getModel()
							.getContext().getInitData()).getInitData();
					try {
						// ȡ�ñ���
						String billcode = ((PayrollAppModel) getModel())
								.getBillCode();
						getHeadItem(PayrollVO.BILLCODE).setValue(billcode);
						getHeadItem(PayrollVO.BILLNAME).setValue(billcode);
					} catch (BusinessException e) {
						Logger.error(e.getMessage());
					}

					// getWaClassRefPane().setRefModel(new
					// WaIncludeClassRefModel());
					// setWaClassWhere();
					getHeadItem(PayrollVO.PK_WA_CLASS).setValue(
							linkDta.getUserObject().toString());
					UIRefPane refPane = getWaClassRefPane();
					// refPane.setPk_org(getModel().getContext().getPk_org());
					// refPane.getValueObj();
					WaClassVO classvo = null;
					try {
						classvo = NCLocator
								.getInstance()
								.lookup(IWaClass.class)
								.queryPayrollClassbyPK(
										linkDta.getUserObject().toString());
					} catch (BusinessException e) {
						Logger.debug(new BusinessException(e));
					}
					if (classvo == null) {
						return;
					}
					String year = classvo.getCyear();
					String period = classvo.getCperiod();
					Integer batch = classvo.getBatch();
					billCardPanel.setHeadItem(PayrollVO.CYEAR, year);

					billCardPanel.setHeadItem("classperiod", year + period);
					billCardPanel.setHeadItem(PayrollVO.CPERIOD, period);
					billCardPanel.setHeadItem(PayrollVO.BATCH, batch);

					billCardPanel.setHeadItem(PayrollVO.BILLSTATE,
							IPfRetCheckInfo.NOSTATE);
					billCardPanel.setHeadItem(PayrollVO.OPERATOR,
							PubEnv.getPk_user());
					// ����wacontext
					WaLoginContext context = createContext(linkDta
							.getUserObject().toString(), year, period);
					showDetail(context);
					if (getPayrollModel().getAutoGenerateBillCode()
							&& !getPayrollModel().isAutoCodeEdit()) {
						// �Զ����룬 ���ݱ��벻�ɱ༭
						getHeadItem(PayrollVO.BILLCODE).setEnabled(false);

					}
					getHeadItem(PayrollVO.PK_WA_CLASS).setEnabled(false);
					// guoqt ������ʱ�������Ϳ��Ա༭
					if (getApproveType() != null
							&& getApproveType().equals(
									HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW)) {
						getHeadItem(PayrollVO.TRANSTYPEID).setEnabled(true);
					}
					// getWaClassRefPane().setRefModel(new
					// WaIncludeClassRefModel());
					// setWaClassWhere();
					// getHeadItem(PayrollVO.PK_WA_CLASS).setValue(linkDta.getUserObject().toString());
					getModel().getContext().setInitData(null);
				}
			} else if (UIState.EDIT == getModel().getUiState()) {

				getHeadItem(PayrollVO.PK_WA_CLASS).setEnabled(false);
				if (getHeadItem(PayrollVO.BILLSTATE).getValueObject().equals(
						IPfRetCheckInfo.NOSTATE)) {
					getHeadItem(PayrollVO.BILLCODE).setEnabled(true);
					if (this.isInputPnl
							&& getPayrollModel().getAutoGenerateBillCode()
							&& !getPayrollModel().isAutoCodeEdit()) {
						// �Զ����룬 ���ݱ��벻�ɱ༭
						getHeadItem(PayrollVO.BILLCODE).setEnabled(false);
					}
				} else {
					getHeadItem(PayrollVO.BILLCODE).setEnabled(false);
				}
				getHeadItem("classperiod").setEnabled(false);

			} else if (UIState.NOT_EDIT == getModel().getUiState()) {
				setEditable(false);
				getBillCardPanel().setEnabled(false);
				AggPayrollVO aggVO = (AggPayrollVO) getModel()
						.getSelectedData();
				if (aggVO != null) {
					PayrollVO parentVO = (PayrollVO) aggVO.getParentVO();
					if (parentVO != null && parentVO.getPk_wa_class() != null) {
						BillItem item = getHeadItem(PayrollVO.PK_WA_CLASS);
						UIRefPane refPane = (UIRefPane) item.getComponent();
						refPane.setPK(parentVO.getPk_wa_class());
					}
				}
			}
		}

		if (getApproveType() == null
				|| getApproveType().equals(
						HRConstEnum.APPROVE_TYPE_FORCE_DIRECT)) {
			// ֱ��ʱ �������Ͳ��ɱ༭
			getHeadItem(PayrollVO.TRANSTYPEID).setEnabled(false);
		}
		// 20151214 shenliangc NCdp205559130 н�ʹ�������һ�к����������ûس����У��ڶ����к�Ϊ�գ�����ʱ����
		// ȥ���س����С��ӱ�����Ҽ���
		getBillCardPanel().getBodyPanel().setAutoAddLine(false);
		getBillCardPanel().getBodyPanel().setBBodyMenuShow(false);

	}

	private void refreshSumRow(Map<String, AggPayDataVO> aggVOMap) {
		// guoqt н�ʷ������뼰�������Ӻϼ���
		BillItem[] items = getBillCardPanel().getBodyShowItems();
		AggPayrollVO selectedVO = (AggPayrollVO) getPayrollModel()
				.getSelectedData();
		DataVO[] sumDataVoall = null;
		// guoqt ���������ϼ�
		DataVO[] sumDataVonum = null;
		if (selectedVO != null) {
			PayrollVO headVO = (PayrollVO) selectedVO.getParentVO();
			WaLoginContext waContext = createContext(headVO.getPk_wa_class(),
					headVO.getCyear(), headVO.getCperiod());
			AggPayDataVO sumDataVO = null;
			// guoqt ���������ϼ�
			AggPayDataVO sumdatapsnnum = null;
			// try {
			// Map<String, AggPayDataVO> aggVOMap =
			// WADelegator.getPaydataQuery()
			// .queryItemAndSumDataVOForroll(waContext);
			sumDataVO = aggVOMap.get("sumdataall");

			if (sumDataVO != null) {
				sumDataVoall = sumDataVO.getDataVOs();
			}
			// guoqt ���������ϼ�
			sumdatapsnnum = aggVOMap.get("sumdata");
			if (sumdatapsnnum != null) {
				sumDataVonum = sumdatapsnnum.getDataVOs();
			}

			// } catch (BusinessException e) {
			// Logger.error(e.getMessage(), e);
			// MessageDialog.showErrorDlg(this, null, e.getMessage());
			// }
			if (sumDataVoall != null) {
				for (int i = 0; i < items.length; i++) {
					if (items[i].getKey().startsWith("f_")) {
						int decimal = items[i].getDecimalDigits();
						UFDouble value = (UFDouble) sumDataVoall[0]
								.getAttributeValue(items[i].getKey());
						;
						if (value == null) {
							value = UFDouble.ZERO_DBL;
						}
						// guoqt����ҳǩ����ʾ�ϼ���
						getBillCardPanel().getTotalTableModel().setValueAt(
								value.setScale(decimal, UFDouble.ROUND_HALF_UP)
										.toString(), 0, i);
						getBillCardPanel()
								.getBillModel("payrollbs2")
								.getTotalTableModel()
								.setValueAt(
										value.setScale(decimal,
												UFDouble.ROUND_HALF_UP)
												.toString(), 0, i);
					}
				}
				// guoqt ���������ϼ�
				// 2016-1-11 NCdp205571960 zhousze ����û��Ȩ�޵���Ա�������sumDataVonum��Ҫ�п�
				if (sumDataVonum != null && sumDataVonum.length != 0) {
					int num = 0;
					for (int i = 0; i < sumDataVonum.length; i++) {
						num = num
								+ Integer.parseInt((String) sumDataVonum[i]
										.getAttributeValue("psnnum"));
					}
					getBillCardPanel().getBillModel("payrollbs2")
							.getTotalTableModel().setValueAt(num, 0, 4);
				}

			}
		}

	}

	/**
	 * ����׼������
	 * 
	 * @author xuhw on 2010-4-28
	 * @throws BusinessException
	 */
	private void setCopyStates() {
		// ����״̬ ��Ϊ����̬
		getHeadItem(PayrollVO.BILLSTATE).setValue(IPfRetCheckInfo.NOSTATE);

		if (((PayrollAppModel) getModel()).getAutoGenerateBillCode()) {// ���ݱ���
			try {
				String billcode = ((PayrollAppModel) getModel()).getBillCode();
				if (!StringUtil.isEmpty(billcode)) {
					getHeadItem(PayrollVO.BILLCODE).setValue(billcode);
					getHeadItem(PayrollVO.BILLNAME).setValue(billcode);
					if (!getPayrollModel().isAutoCodeEdit()) {
						// �Զ����룬 ���ݱ��벻�ɱ༭
						getHeadItem(PayrollVO.BILLCODE).setEnabled(false);
					} else {
						getHeadItem(PayrollVO.BILLCODE).setEnabled(true);
					}
				} else {
					getHeadItem(PayrollVO.BILLCODE).setEnabled(true);
				}
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
			}
		} else {
			getHeadItem(PayrollVO.BILLCODE).setEnabled(true);
		}
		getHeadItem(PayrollVO.OPERATOR).setValue(PubEnv.getPk_user());// ������
		getHeadItem(PayrollVO.APPLYDATE).setValue(PubEnv.getServerDate());// ��������
		getBillCardPanel().getTailItem(PayrollVO.APPROVER).setValue(null);// ������
		getBillCardPanel().getTailItem(PayrollVO.APPROVEDATE).setValue(null);// ��������

		// ������
		getBillCardPanel().getTailItem(PayrollVO.CREATOR).setValue("");
		// ����ʱ��
		getBillCardPanel().getTailItem(PayrollVO.CREATIONTIME).setValue("");

		// �޸���
		getBillCardPanel().getTailItem(PayrollVO.MODIFIER).setValue("");
		// �޸�ʱ��
		getBillCardPanel().getTailItem(PayrollVO.MODIFIEDTIME).setValue("");

	}

	@Override
	protected void setDefaultValue() {
		super.setDefaultValue();
		if (this.isInputPnl) {
			// ���ݱ���ɱ༭
			getHeadItem(PayrollVO.BILLCODE).setEnabled(true);

			if (getPayrollModel().getAutoGenerateBillCode()) {
				// �Զ����룬 ��������Ĭ��ͬ����
				Object billCodeObject = getHeadItemValue(PayrollVO.BILLCODE);
				setHeadItemValue(PayrollVO.BILLNAME, billCodeObject);
				if (!getPayrollModel().isAutoCodeEdit()) {
					// �Զ����룬 ���ݱ��벻�ɱ༭
					getHeadItem(PayrollVO.BILLCODE).setEnabled(false);
				}
			}
			// ������������
			this.setHeadItemValue(PayrollVO.APPLYDATE, PubEnv.getServerDate());

			getHeadItem(PayrollVO.BILLTYPE).setValue(
					((nc.ui.wa.payroll.model.PayrollAppModel) getModel())
							.getBillType());
		}
	}

	private BillItem getHeadItem(String strKey) {
		return getBillCardPanel().getHeadItem(strKey);
	}

	private void setWaClassWhere() {
		// ����н�ʷ�����������
		getWaClassRefPane().setPk_org(getModel().getContext().getPk_org());
		String where = " (exists(select 1 from wa_periodstate,wa_period "

		+ "where wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ "and wa_waclass.pk_wa_class = wa_periodstate.pk_wa_class "
				+ "and wa_waclass.cyear = wa_period.cyear "
				+ "and wa_waclass.cperiod = wa_period.cperiod "
				+ "and wa_periodstate.isapporve = 'Y' "
				+ "and wa_periodstate.checkflag = 'Y') ";
		where += " and not exists(select 1 from wa_payroll "
				+ "where wa_payroll.pk_wa_class = wa_waclass.pk_wa_class "
				+ "and wa_payroll.cyear = wa_waclass.cyear "
				+ "and wa_payroll.cperiod = wa_waclass.cperiod and wa_payroll.billstate!="
				+ IPfRetCheckInfo.NOPASS + ")";

		where += "or (exists(select 1 from wa_periodstate,wa_period "
				+ "where wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ "and wa_waclass.pk_childclass  = wa_periodstate.pk_wa_class "
				+ "and wa_waclass.cyear = wa_period.cyear "
				+ "and wa_waclass.cperiod = wa_period.cperiod "
				+ "and wa_periodstate.isapporve = 'Y' "
				+ "and wa_periodstate.checkflag = 'Y') ";
		where += " and not exists(select 1 from wa_payroll "
				+ "where wa_payroll.pk_wa_class = wa_waclass.pk_childclass  "
				+ "and wa_payroll.cyear = wa_waclass.cyear "
				+ "and wa_payroll.cperiod = wa_waclass.cperiod and wa_payroll.billstate!="
				+ IPfRetCheckInfo.NOPASS + ")))";
		// UFBoolean leaveApply = UFBoolean.FALSE;
		// try {
		// leaveApply =
		// SysinitAccessor.getInstance().getParaBoolean(getModel().getContext().getPk_org(),
		// ParaConstant.LEAVEAPPLY);
		// } catch (BusinessException e) {
		// Logger.error(e.getMessage());
		// }
		// if (!leaveApply.booleanValue()) {
		// where +=
		// " and ( isnull(wa_waclass.batch,0)=0 or wa_waclass.batch <100  ) ";
		// }
		getWaClassRefPane().getRefModel().setWherePart(where);
		// getWaClassRefPane().getRefModel().reloadData();
	}

	/**
	 * ��ʾ��ϸ��Ϣ
	 * 
	 * @author liangxr on 2010-5-17
	 * @param waContext
	 */
	private void showDetail(WaLoginContext waContext) {

		AggPayDataVO aggPayDataVO = null;
		AggPayDataVO sumDataVO = null;
		Map<String, AggPayDataVO> aggVOMap = null;
		try {
			aggVOMap = WADelegator.getPaydataQuery()
					.queryItemAndSumDataVOForroll(waContext);
			aggPayDataVO = aggVOMap.get("itemdata");
			sumDataVO = aggVOMap.get("sumdata");
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showErrorDlg(this, null, e.getMessage());
		}
		WaClassItemVO[] classItemVOs = aggPayDataVO.getClassItemVOs();
		DataVO[] dataVOs = aggPayDataVO.getDataVOs();

		// 2016-12-12 zhousze н�ʼ��ܣ����ﴦ��ѡ��н�ʷ����Զ�������ϸ��Ϣ�����ݽ��� begin
		dataVOs = SalaryDecryptUtil.decrypt4Array(dataVOs);
		// end

		if (null != dataVOs && dataVOs.length > 0) {
			UFDouble yf = UFDouble.ZERO_DBL;
			UFDouble sf = UFDouble.ZERO_DBL;
			for (int i = 0; i < dataVOs.length; i++) {
				yf = yf.add((UFDouble) dataVOs[i].getAttributeValue("f_1"));
				sf = sf.add((UFDouble) dataVOs[i].getAttributeValue("f_3"));
			}
			// ��Ӧ���ϼƸ�ֵ��
			billCardPanel.setHeadItem(PayrollVO.YF, yf);
			billCardPanel.setHeadItem(PayrollVO.SF, sf);
		}

		if (getPayrollModel().isApproveSite()) {
			AggPayrollVO selectvo = (AggPayrollVO) getModel().getSelectedData();
			resetBill(classItemVOs);
			billCardPanel.getBillData()
					.setHeaderValueVO(selectvo.getParentVO());
			setClassPeriod((PayrollVO) selectvo.getParentVO());
		} else {
			Object headVO = billCardPanel.getBillData().getHeaderValueVO(
					PayrollVO.class.getName());
			resetBill(classItemVOs);
			billCardPanel.getBillData().setHeaderValueVO((PayrollVO) headVO);
			setClassPeriod((PayrollVO) headVO);
		}

		billCardPanel.getBillData().setBodyValueVO("payrollbs", dataVOs);
		billCardPanel.getBillData().setBodyValueVO("payrollbs2",
				sumDataVO.getDataVOs());
		if (UIState.NOT_EDIT.equals(getModel().getUiState())) {
			billCardPanel.setEnabled(false);
		}
		setWaClassWhere();
		refreshSumRow(aggVOMap);
	}

	private void setClassPeriod(PayrollVO headVO) {
		billCardPanel.setHeadItem("classperiod",
				headVO.getCyear() + headVO.getCperiod());
	}

	public PayrollAppModel getPayrollModel() {
		return (PayrollAppModel) getModel();
	}

	@Override
	protected void synchronizeDataFromModel() {
		super.synchronizeDataFromModel();
		AggPayrollVO selectedVO = (AggPayrollVO) getPayrollModel()
				.getSelectedData();
		if (selectedVO == null) {
			// resetBill(null); ���õ���ģ�壬����billdata��BillCardPanleΪnull������
		} else {
			PayrollVO headVO = (PayrollVO) selectedVO.getParentVO();
			WaLoginContext context = createContext(headVO.getPk_wa_class(),
					headVO.getCyear(), headVO.getCperiod());
			showDetail(context);
		}
	}

	/**
	 * ����wacontext
	 * 
	 * @author liangxr on 2010-5-17
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @return
	 */
	private WaLoginContext createContext(String pk_wa_class, String cyear,
			String cperiod) {
		WaLoginContext context = new WaLoginContext();
		WaLoginVO waLoginVO = new WaLoginVO();
		waLoginVO.setPk_wa_class(pk_wa_class);
		waLoginVO.setCyear(cyear);
		waLoginVO.setCperiod(cperiod);
		waLoginVO.setPk_group(getModel().getContext().getPk_group());
		waLoginVO.setPk_org(getModel().getContext().getPk_org());
		context.setPk_group(getModel().getContext().getPk_group());
		context.setNodeCode(getModel().getContext().getNodeCode());
		context.setPk_org(getModel().getContext().getPk_org());
		PeriodStateVO periodStateVO = new PeriodStateVO();
		periodStateVO.setCyear(cyear);
		periodStateVO.setCperiod(cperiod);
		waLoginVO.setPeriodVO(periodStateVO);
		context.setWaLoginVO(waLoginVO);
		return context;
	}

	/**
	 * ���õ���ģ��
	 * 
	 * @author liangxr on 2010-5-17
	 * @param classItemVOs
	 */
	private void resetBill(WaClassItemVO[] classItemVOs) {
		((PayrollTemplateContainer) getTemplateContainer())
				.setClassItemVOs(classItemVOs);
		BillTempletVO template = getTemplateContainer().getTemplate(
				getNodekey());
		billCardPanel
				.setBillData(new BillData(template, getBillStatus()), null);
		if (UIState.NOT_EDIT == getModel().getUiState()) {
			billCardPanel.setEnabled(false);
		}
		// guoqt
		billCardPanel.setTatolRowShow("payrollbs", true);
		billCardPanel.setTatolRowShow("payrollbs2", true);
	}

	private UIRefPane getWaClassRefPane() {
		BillItem item = billCardPanel.getHeadItem(PayrollVO.PK_WA_CLASS);
		UIRefPane refPane = (UIRefPane) item.getComponent();
		return refPane;
	}

	@Override
	public boolean beforeEdit(BillItemEvent e) {
		if (PayrollVO.TRANSTYPEID.equals(e.getItem().getKey())) {
			// ������
			((UIRefPane) e.getItem().getComponent()).getRefModel()
					.addWherePart(
							" and ( pk_billtypecode = '"
									+ SalaryPmtCommonValue.PAYROLLTYPE
									+ "' or ( parentbilltype = '"
									+ SalaryPmtCommonValue.PAYROLLTYPE
									+ "' and pk_group = '"
									+ getModel().getContext().getPk_group()
									+ "' )) ");
			((UIRefPane) e.getItem().getComponent()).getRefModel().reloadData();
		}
		return true;
	}

}
