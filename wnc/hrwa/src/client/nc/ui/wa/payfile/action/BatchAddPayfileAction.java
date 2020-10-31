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
 * 薪资档案批量增加Action
 * 
 * @author: zhoucx
 * @date: 2009-11-30 下午04:45:18
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
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
																							 * "批量增加"
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

		// 第一步
		WizardStep step1 = new SearchPsnWizardFirstStep(getPayfileModel(), dataManager, WizardActionType.ADD,
				actionName);
		// 第二步
		WizardStep step2 = new BatchAddWizardSecondStep(getPayfileModel(), dataManager, actionName);
		// 第三步
		WizardStep step3 = new BatchAddWizardThirdStep(getPayfileModel(), dataManager);
		// 放到List中
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
																					 * "提示"
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
			List<PayfileVO> listPayfileVOs = new ArrayList<PayfileVO>();// 能够保存的数据
			List<PayfileVO> errorList = new ArrayList<PayfileVO>();// 存在问题的数据
			List<PayfileVO> errorList2 = new ArrayList<PayfileVO>();// 数据不完整的数据
			for (PayfileVO element : addPsntemp) {
				if ("Y".equals(isForeginMap.get(element.getPk_psndoc()))) {
					if ("1001ZZ1000000001NCMA".equals(String.valueOf(sysinitValue))) {// 参数如果选择a.扣税核算入境日期
						if (psndocMap.get(element.getPk_psndoc()).getAttributeValue(
								LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN03")) == null) {
							errorList2.add(element);
							continue;
						}
					} else if ("1001ZZ1000000001NCMB".equals(String.valueOf(sysinitValue))) {// 参数如果选择b.居留证到期日期
						if (psndocMap.get(element.getPk_psndoc()).getAttributeValue(
								LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN04")) == null) {
							errorList2.add(element);
							continue;
						}
					}
				}
				// 根据当前的类别、期间
				element.setPk_prnt_class(context.getWaLoginVO().getPk_prnt_class());
				element.setPk_wa_class(context.getPk_wa_class()); // 薪资类别
				element.setCyear(context.getWaYear()); // 薪资年度
				element.setCperiod(context.getWaPeriod()); // 薪资期间
				element.setCyearperiod(context.getWaYear() + context.getWaPeriod());
				element.setPk_group(context.getPk_group());
				element.setPk_org(context.getPk_org());
				// 界面的填写数据。
				element.setStopflag(batchvo.getStopflag());// 停发标志
				element.setIsndebuct(batchvo.getIsndebuct());// 费用扣除额
				element.setTaxtableid(batchvo.getTaxtableid());// 税率表
				element.setIsderate(batchvo.getIsderate());// 是否减免税
				element.setDerateptg(batchvo.getDerateptg());// 减税比例
				element.setTaxtype(batchvo.getTaxtype());// 扣税方式
				element.setPartflag(getWizardModel().isPartflag());

				// ssx added on 2020-10-22
				// 設置補充保費計算方式
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

			// 批量增加
			try {
				// addPsntemp = getPayfileService().addPsnVOs(addPsntemp);
				if (listPayfileVOs != null && listPayfileVOs.size() > 0)
					addPsntemp = getPayfileService().addPsnVOs(listPayfileVOs.toArray(new PayfileVO[0]));
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
																				 * "提示"
																				 */, e.getMessage());
				throw e1;
			}
			getWizardModel().setPayfileVOs(null);
			addPsntemp = null;
			getWizardModel().setSelectVOs(null);
			// 刷新页面
			((PayfileModelDataManager) getDataManager()).refresh();
			if ((errorList != null && errorList.size() > 0) || (errorList2 != null && errorList2.size() > 0)) {
				String msg = "";
				if (errorList != null && errorList.size() > 0) {
					msg = ResHelper.getString("notice", "2notice-tw-000002")/* 人员编码为 */;
					for (int i = 0; i < errorList.size(); i++) {
						String code = psnCodes.get(errorList.get(i).getPk_psndoc());
						if (i == errorList.size() - 1)
							msg += code;
						else
							msg += code + "、";
					}
					msg += ResHelper.getString("notice", "2notice-tw-000003") + "\n"/*
																					 * 的员工不是外籍员工
																					 * ，
																					 * 请确认员工咨询维护中是否正确维护是否外籍员工
																					 * 。
																					 */;
				}
				if (errorList2 != null && errorList2.size() > 0) {
					msg += ResHelper.getString("notice", "2notice-tw-000002")/* 人员编码为 */;
					for (int i = 0; i < errorList2.size(); i++) {
						String code = psnCodes.get(errorList2.get(i).getPk_psndoc());
						if (i == errorList2.size() - 1)
							msg += code;
						else
							msg += code + "、";
					}
					if ("1001ZZ1000000001NCMA".equals(String.valueOf(sysinitValue))) {// 参数如果选择a.扣税核算入境日期
						msg += ResHelper.getString("notice", "2notice-tw-000006");
					} else if ("1001ZZ1000000001NCMB".equals(String.valueOf(sysinitValue))) {// 参数如果选择b.居留证到期日期
						msg += ResHelper.getString("notice", "2notice-tw-000007");
					}
				}
				MessageDialog.showErrorDlg(null, ResHelper.getString("60130payfile", "060130payfile0245"), msg);
			} else {
				putValue(HrAction.MESSAGE_AFTER_ACTION, ResHelper.getString("60130payfile", "060130payfile0350")/*
																												 * @
																												 * res
																												 * "批量增加操作成功。"
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
	 * 根据要插入的薪资档案VO，查询所有员工是否为外籍员工
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
	 * @功能描述 获取参数设置的值
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