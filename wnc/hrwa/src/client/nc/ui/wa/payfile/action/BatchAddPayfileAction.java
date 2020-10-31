package nc.ui.wa.payfile.action;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.wa.util.LocalizationSysinitUtil;
import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hr.wa.IPayfileManageService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardDialog;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.wa.payfile.model.PayfileAppModel;
import nc.ui.wa.payfile.model.PayfileModelDataManager;
import nc.ui.wa.payfile.model.PayfileModelService;
import nc.ui.wa.payfile.model.PayfileWizardModel;
import nc.ui.wa.payfile.wizard.BatchAddWizardSecondStep;
import nc.ui.wa.payfile.wizard.BatchAddWizardThirdStep;
import nc.ui.wa.payfile.wizard.SearchPsnWizardFirstStep;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.payfile.WizardActionType;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.taxrate.TaxBaseVO;

/**
 * н�ʵ�����������Action
 * 
 * @author: zhoucx
 * @date: 2009-11-30 ����04:45:18
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class BatchAddPayfileAction extends PayfileBaseAction implements IWizardDialogListener {

	private static final long serialVersionUID = -7803626468968395002L;

	private PayfileWizardModel wizardModel = null;

	private IPayfileManageService payfileService = null;

	private Map<String, String> psnCodes = null;// map<pk_psndoc,code>

	private Map<String, PsndocVO> psndocMap = null;// map<pk_psndoc,psndoc>

	private final String actionName = ResHelper.getString("60130payfile", "060130payfile0330")/*
																							 * @
																							 * res
																							 * "��������"
																							 */;

	public BatchAddPayfileAction() {
		super();
		setBtnName(actionName);
		putValue(INCAction.CODE, "BatchAdd");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK + Event.SHIFT_MASK));
		putValue(Action.SHORT_DESCRIPTION, actionName + "(Ctrl+Shif+B)");
	}

	public PayfileAppModel getPayfileModel() {
		return (PayfileAppModel) super.getModel();
	}

	private IPayfileManageService getPayfileService() {
		if (payfileService == null) {
			PayfileAppModel model = (PayfileAppModel) getModel();
			PayfileModelService service = (PayfileModelService) model.getService();
			payfileService = service.getPayfileService();
		}
		return payfileService;
	}

	/**
	 * @author zhoucx on 2009-12-25
	 * @see nc.ui.uif2.actions.AddAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {

		WizardDialog wizardDialog = new WizardDialog(getModel().getContext().getEntranceUI(), newWizardModel(),
				getSteps(), null);
		wizardDialog.setWizardDialogListener(this);
		wizardDialog.setResizable(true);
		wizardDialog.setSize(new Dimension(700, 560));
		wizardDialog.showModal();
	}

	private List<WizardStep> getSteps() {
		PayfileModelDataManager dataManager = (PayfileModelDataManager) super.getDataManager();

		// ��һ��
		WizardStep step1 = new SearchPsnWizardFirstStep(getPayfileModel(), dataManager, WizardActionType.ADD,
				actionName);
		// �ڶ���
		WizardStep step2 = new BatchAddWizardSecondStep(getPayfileModel(), dataManager, actionName);
		// ������
		WizardStep step3 = new BatchAddWizardThirdStep(getPayfileModel(), dataManager);
		// �ŵ�List��
		List<WizardStep> steps = Arrays.asList(new WizardStep[] { step1, step2, step3 });
		return steps;
	}

	/**
	 * @author liangxr on 2010-1-14
	 * @see nc.ui.pub.beans.wizard.IWizardDialogListener#wizardFinish(nc.ui.pub.beans.wizard.WizardEvent)
	 */
	@Override
	public void wizardFinish(WizardEvent event) throws WizardActionException {
		PayfileVO[] addPsntemp = getWizardModel().getSelectVOs();
		WaLoginContext context = (WaLoginContext) getModel().getContext();
		if (addPsntemp != null && addPsntemp.length > 0) {
			PayfileVO batchvo = getWizardModel().getBatchitemvo();
			Map<String, String> isForeginMap = new HashMap<String, String>();
			TaxBaseVO taxBaseVO = new TaxBaseVO();
			if (null != batchvo.getTaxtableid() && !"".equals(batchvo.getTaxtableid())) {
				try {
					taxBaseVO = (TaxBaseVO) getUAPQueryBS().retrieveByPK(TaxBaseVO.class, batchvo.getTaxtableid());
				} catch (BusinessException e2) {
					Logger.error(e2.getMessage(), e2);
				}
			}
			if (Integer.valueOf(3).equals(taxBaseVO.getItbltype())) {
				try {
					isForeginMap = getIsForegin(addPsntemp);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
					WizardActionException e1 = new WizardActionException(e);
					e1.addMsg(ResHelper.getString("60130payfile", "060130payfile0245")/*
																					 * @
																					 * res
																					 * "��ʾ"
																					 */, e.getMessage());
					throw e1;
				}
			}
			String sysinitValue = "";
			try {
				sysinitValue = getSysinitValue(context.getPk_org());
			} catch (BusinessException e2) {
				Logger.error(e2.getMessage(), e2);
			}
			List<PayfileVO> listPayfileVOs = new ArrayList<PayfileVO>();// �ܹ����������
			List<PayfileVO> errorList = new ArrayList<PayfileVO>();// �������������
			List<PayfileVO> errorList2 = new ArrayList<PayfileVO>();// ���ݲ�����������
			for (PayfileVO element : addPsntemp) {
				if ("Y".equals(isForeginMap.get(element.getPk_psndoc()))) {
					if ("1001ZZ1000000001NCMA".equals(String.valueOf(sysinitValue))) {// �������ѡ��a.��˰�����뾳����
						if (psndocMap.get(element.getPk_psndoc()).getAttributeValue(
								LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN03")) == null) {
							errorList2.add(element);
							continue;
						}
					} else if ("1001ZZ1000000001NCMB".equals(String.valueOf(sysinitValue))) {// �������ѡ��b.����֤��������
						if (psndocMap.get(element.getPk_psndoc()).getAttributeValue(
								LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN04")) == null) {
							errorList2.add(element);
							continue;
						}
					}
				}
				// ���ݵ�ǰ������ڼ�
				element.setPk_prnt_class(context.getWaLoginVO().getPk_prnt_class());
				element.setPk_wa_class(context.getPk_wa_class()); // н�����
				element.setCyear(context.getWaYear()); // н�����
				element.setCperiod(context.getWaPeriod()); // н���ڼ�
				element.setCyearperiod(context.getWaYear() + context.getWaPeriod());
				element.setPk_group(context.getPk_group());
				element.setPk_org(context.getPk_org());
				// �������д���ݡ�
				element.setStopflag(batchvo.getStopflag());// ͣ����־
				element.setIsndebuct(batchvo.getIsndebuct());// ���ÿ۳���
				element.setTaxtableid(batchvo.getTaxtableid());// ˰�ʱ�
				element.setIsderate(batchvo.getIsderate());// �Ƿ����˰
				element.setDerateptg(batchvo.getDerateptg());// ��˰����
				element.setTaxtype(batchvo.getTaxtype());// ��˰��ʽ
				element.setPartflag(getWizardModel().isPartflag());

				// ssx added on 2020-10-22
				// �O���a�䱣�MӋ�㷽ʽ
				element.setExnhitype(batchvo.getExnhitype());
				//

				if (Integer.valueOf(3).equals(taxBaseVO.getItbltype())) {
					if ("N".equals(isForeginMap.get(element.getPk_psndoc()))) {
						errorList.add(element);
					} else {
						listPayfileVOs.add(element);
					}
				} else {
					listPayfileVOs.add(element);
				}
			}

			// ��������
			try {
				// addPsntemp = getPayfileService().addPsnVOs(addPsntemp);
				if (listPayfileVOs != null && listPayfileVOs.size() > 0)
					addPsntemp = getPayfileService().addPsnVOs(listPayfileVOs.toArray(new PayfileVO[0]));
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
				// getPayfileModel().batchSynToOtherClass(addPsntemp,
				// classList);
				// }
				// }
				// }

			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
				WizardActionException e1 = new WizardActionException(e);
				e1.addMsg(ResHelper.getString("60130payfile", "060130payfile0245")/*
																				 * @
																				 * res
																				 * "��ʾ"
																				 */, e.getMessage());
				throw e1;
			}
			getWizardModel().setPayfileVOs(null);
			addPsntemp = null;
			getWizardModel().setSelectVOs(null);
			// ˢ��ҳ��
			((PayfileModelDataManager) getDataManager()).refresh();
			if ((errorList != null && errorList.size() > 0) || (errorList2 != null && errorList2.size() > 0)) {
				String msg = "";
				if (errorList != null && errorList.size() > 0) {
					msg = ResHelper.getString("notice", "2notice-tw-000002")/* ��Ա����Ϊ */;
					for (int i = 0; i < errorList.size(); i++) {
						String code = psnCodes.get(errorList.get(i).getPk_psndoc());
						if (i == errorList.size() - 1)
							msg += code;
						else
							msg += code + "��";
					}
					msg += ResHelper.getString("notice", "2notice-tw-000003") + "\n"/*
																					 * ��Ա�������⼮Ա��
																					 * ��
																					 * ��ȷ��Ա����ѯά�����Ƿ���ȷά���Ƿ��⼮Ա��
																					 * ��
																					 */;
				}
				if (errorList2 != null && errorList2.size() > 0) {
					msg += ResHelper.getString("notice", "2notice-tw-000002")/* ��Ա����Ϊ */;
					for (int i = 0; i < errorList2.size(); i++) {
						String code = psnCodes.get(errorList2.get(i).getPk_psndoc());
						if (i == errorList2.size() - 1)
							msg += code;
						else
							msg += code + "��";
					}
					if ("1001ZZ1000000001NCMA".equals(String.valueOf(sysinitValue))) {// �������ѡ��a.��˰�����뾳����
						msg += ResHelper.getString("notice", "2notice-tw-000006");
					} else if ("1001ZZ1000000001NCMB".equals(String.valueOf(sysinitValue))) {// �������ѡ��b.����֤��������
						msg += ResHelper.getString("notice", "2notice-tw-000007");
					}
				}
				MessageDialog.showErrorDlg(null, ResHelper.getString("60130payfile", "060130payfile0245"), msg);
			} else {
				putValue(HrAction.MESSAGE_AFTER_ACTION, ResHelper.getString("60130payfile", "060130payfile0350")/*
																												 * @
																												 * res
																												 * "�������Ӳ����ɹ���"
																												 */);
			}
		}
	}

	/**
	 * @author liangxr on 2010-1-20
	 * @see nc.ui.pub.beans.wizard.IWizardDialogListener#wizardFinishAndContinue(nc.ui.pub.beans.wizard.WizardEvent)
	 */
	@Override
	public void wizardFinishAndContinue(WizardEvent event) throws WizardActionException {
	}

	public void setWizardModel(PayfileWizardModel wizardModel) {
		this.wizardModel = wizardModel;
	}

	public PayfileWizardModel getWizardModel() {
		if (wizardModel == null) {
			wizardModel = newWizardModel();
		}
		return wizardModel;
	}

	public PayfileWizardModel newWizardModel() {
		wizardModel = new PayfileWizardModel();
		wizardModel.setSteps(getSteps());
		return wizardModel;
	}

	/**
	 * @author xuanlt on 2010-3-24
	 * @see nc.ui.pub.beans.wizard.IWizardDialogListener#wizardCancel(nc.ui.pub.beans.wizard.WizardEvent)
	 */
	@Override
	public void wizardCancel(WizardEvent event) throws WizardActionException {

	}

	@Override
	protected boolean isActionEnable() {
		if (getWaLoginVO().getBatch() != null && getWaLoginVO().getBatch() > 100) {
			return false;
		}
		return super.isActionEnable();
	}

	/**
	 * ����Ҫ�����н�ʵ���VO����ѯ����Ա���Ƿ�Ϊ�⼮Ա��
	 * 
	 * @author ward
	 * @date 2018-01-15
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, String> getIsForegin(PayfileVO[] vos) throws BusinessException {
		String[] pks = SQLHelper.getStrArray(vos, PayfileVO.PK_PSNDOC);
		// InSQLCreator isc = new InSQLCreator();
		// String inPsndocSql = isc.getInSQL(pks);
		String inPsndocSql = SQLHelper.joinToInSql(pks, -1);
		String where = PayfileVO.PK_PSNDOC + " in(" + inPsndocSql + ")";
		IUAPQueryBS queryBS = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		@SuppressWarnings("unchecked")
		List<PsndocVO> list = (ArrayList<PsndocVO>) queryBS.retrieveByClause(PsndocVO.class, where);
		Map<String, String> SLOMap = new HashMap<String, String>();
		psnCodes = new HashMap<String, String>();
		psndocMap = new HashMap<String, PsndocVO>();
		for (PsndocVO psndocVO : list) {
			SLOMap.put(psndocVO.getPk_psndoc(),
					psndocVO.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")) != null ? psndocVO
							.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")).toString() : "N");
			psnCodes.put(psndocVO.getPk_psndoc(), psndocVO.getCode());
			psndocMap.put(psndocVO.getPk_psndoc(), psndocVO);
		}
		return SLOMap;
	}

	/**
	 * @�������� ��ȡ�������õ�ֵ
	 * @author ward
	 * @date 2018-01-15
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public String getSysinitValue(String pk_org) throws BusinessException {
		String qrySql = "select value from pub_sysinit where initcode='TWHR08' and pk_org='" + pk_org
				+ "' and isnull(dr,0)=0";
		Object sysinitValue = getUAPQueryBS().executeQuery(qrySql, new ColumnProcessor());
		return String.valueOf(sysinitValue);
	}

	private IUAPQueryBS queryBS = null;

	public IUAPQueryBS getUAPQueryBS() {
		if (queryBS == null) {
			queryBS = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		}
		return queryBS;
	}

}