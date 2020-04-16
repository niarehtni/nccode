package nc.ui.hi.register.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IHiActionCode;
import nc.itf.hi.IPsndocService;
import nc.itf.hr.wa.IPsndocwadocManageService;
import nc.itf.hrp.psnbudget.IOrgBudgetQueryService;
import nc.itf.ta.IPsnCalendarManageService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.hi.psndoc.action.ActionHelper;
import nc.ui.hi.psndoc.model.PsndocModel;
import nc.ui.hi.psndoc.view.PsndocFormEditor;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hrp.psnorgbudget.ValidateResultVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/***************************************************************************
 * 转人员档案<br>
 * Created on 2010-2-23 9:18:19<br>
 * 
 * @author hezhen, Rocex Wang
 ***************************************************************************/
public class SwitchToDocAction extends HrAction {
	private PsndocFormEditor formEditor;

	private String strBtnName = ResHelper.getString("6007psn", "06007psn0196")/*
																			 * @res
																			 * "转入人员档案"
																			 */;

	/***************************************************************************
	 * Created on 2010-2-23 9:18:36<br>
	 * 
	 * @author Rocex Wang
	 ***************************************************************************/
	public SwitchToDocAction() {
		super();
		setBtnName(strBtnName);
		setCode(IHiActionCode.SWITCHTODOC);

		putValue(Action.SHORT_DESCRIPTION, strBtnName + "(Ctrl+Y)");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-5-25 9:55:09<br>
	 * 
	 * @param objSelections
	 * @throws Exception
	 * @author Rocex Wang
	 * @param isSyncWork
	 ***************************************************************************/
	private void directToDoc(Object objSelections[], boolean isSyncWork) throws Exception {
		ArrayList<String> listPk_psnjob = new ArrayList<String>();
		ArrayList<PsnJobVO> listPsnjobVO = new ArrayList<PsnJobVO>();
		ArrayList<PsndocVO> listPsndocVO = new ArrayList<PsndocVO>();

		ArrayList<PsndocVO> WarningDataPsndocVO = new ArrayList<PsndocVO>();
		UFLiteralDate serverData = PubEnv.getServerLiteralDate();

		for (Object selectedVO : objSelections) {
			PsndocVO psndocVO = ((PsndocAggVO) selectedVO).getParentVO();

			PsnJobVO jobVO = psndocVO.getPsnJobVO();
			if (jobVO.getBegindate().compareTo(serverData) > 0) {
				WarningDataPsndocVO.add(psndocVO);
			}
			listPsndocVO.add(psndocVO);
			if (psndocVO.getPsnOrgVO().getIndocflag() == null || !psndocVO.getPsnOrgVO().getIndocflag().booleanValue()) {
				listPsnjobVO.add(psndocVO.getPsnJobVO());
				listPk_psnjob.add(psndocVO.getPsnJobVO().getPk_psnjob());
			}
		}

		if (WarningDataPsndocVO.size() != 0) {
			String warning = ResHelper.getString("6007psn", "06007psn0523")/*
																			 * @res
																			 * "所选人员任职开始日期晚于当前系统日期，是否继续。人员编码："
																			 */;
			for (Iterator iterator = WarningDataPsndocVO.iterator(); iterator.hasNext();) {
				PsndocVO psndocVO = (PsndocVO) iterator.next();

				warning += "【" + psndocVO.getCode() + "】";
			}

			int iResult = MessageDialog.showYesNoDlg(getEntranceUI(),
					ResHelper.getString("6007psn", "06007psn0475")/* "确认继续" */, warning);
			if (iResult != UIDialog.ID_YES) {
				ActionHelper.setCancelHintMessage(this);

				return;
			}
		}

		if (listPk_psnjob.size() == 0) {
			throw new BusinessException(ResHelper.getString("6007psn", "06007psn0197")/*
																					 * @
																					 * res
																					 * "所选择的人员已经转入人员档案，请重新选择！"
																					 */);
		}

		String strPk_psnjobs[] = listPk_psnjob.toArray(new String[0]);

		// 校验编制
		ValidateResultVO resultVOs[] = NCLocator.getInstance().lookup(IOrgBudgetQueryService.class)
				.validateBudgetValue(getContext(), strPk_psnjobs);

		String strErrorMsg = "";
		String strWarningMsg = "";

		if (resultVOs != null) {
			for (ValidateResultVO resultVO : resultVOs) {
				if (!resultVO.isValid()) {
					strErrorMsg += "\n" + resultVO.getHintMsg();

					// 排除编制校验未通过的组织单据
					String pk_org = resultVO.getPk_org();

					for (PsnJobVO psnJobVO : listPsnjobVO) {
						if (pk_org.equals(psnJobVO.getPk_org())) {
							strPk_psnjobs = (String[]) ArrayUtils.removeElement(strPk_psnjobs, psnJobVO.getPk_psnjob());
						}
					}
				} else if (resultVO.getHintMsg() != null) {
					strWarningMsg += "\n" + resultVO.getHintMsg();
				}
			}

			if (strErrorMsg.length() > 0) {
				// int iResult = MessageDialog.showYesNoDlg(getEntranceUI(),
				// ResHelper.getString("6007psn",
				// "06007psn0475")/*"确认继续"*/, ResHelper.getString("6007psn",
				// "06007psn0198")/*
				// * @res
				// * "转入的组织超编，超编组织的人员已经被过滤掉！详细信息如下，是否继续？"
				// */+ strErrorMsg + strWarningMsg);
				//
				// if (iResult != UIDialog.ID_YES)
				// {
				// ActionHelper.setCancelHintMessage(this);
				//
				// return;
				// }

				throw new BusinessException(ResHelper.getString("6007psn", "06007psn0198")/*
																						 * @
																						 * res
																						 * "转入的组织超编且为严格控制，转档失败，详细信息如下"
																						 */+ strErrorMsg);
			}

			else if (strWarningMsg.length() > 0) {
				int iResult = MessageDialog.showYesNoDlg(getEntranceUI(),
						ResHelper.getString("6007psn", "06007psn0475")/* "确认继续" */,
						ResHelper.getString("6007psn", "06007psn0199")/*
																	 * @res
																	 * "转入的组织超编，详细信息如下，是否继续？"
																	 */
								+ strWarningMsg);
				if (iResult != UIDialog.ID_YES) {
					ActionHelper.setCancelHintMessage(this);

					return;
				}
			}
		}

		if (strPk_psnjobs == null || strPk_psnjobs.length == 0) {
			throw new BusinessException(ResHelper.getString("6007psn", "06007psn0200")/*
																					 * @
																					 * res
																					 * "没有要转入人员档案的人员，请重新选择！"
																					 */);
		}

		NCLocator.getInstance().lookup(IPsndocService.class)
				.intoDoc(listPsndocVO.toArray(new PsndocVO[0]), isSyncWork, getContext().getPk_org(), strPk_psnjobs);
		List<PsnJobVO> psnJobList = new ArrayList<>();
		IPsndocwadocManageService generateSrv = NCLocator.getInstance().lookup(IPsndocwadocManageService.class);
		// 同步班次 tank 2020年3月19日 18:56:53
		for (Object selectedVO : objSelections) {
			PsndocVO psndocVO = ((PsndocAggVO) selectedVO).getParentVO();

			PsnJobVO newPsnJob = psndocVO.getPsnJobVO();
			psnJobList.add(newPsnJob);
			String newShift = newPsnJob.getAttributeValue("jobglbdef7") == null ? "" : (String) newPsnJob
					.getAttributeValue("jobglbdef7");

			if (!StringUtils.isEmpty(newShift)) {
				((IPsnCalendarManageService) NCLocator.getInstance().lookup(IPsnCalendarManageService.class))
						.sync2TeamCalendar(
								newPsnJob.getPk_hrorg(),
								(StringUtils.isEmpty(newShift) ? null : newShift),
								new String[] { newPsnJob.getPk_psndoc() },
								newPsnJob.getBegindate(),
								findEndDate(newShift, newPsnJob.getEnddate() == null ? "9999-12-31" : newPsnJob
										.getEnddate().toString()));

				generateSrv.generateTeamItem(newPsnJob);
			}
		}
		// 同步定调资 tank 2020年3月19日 20:53:51
		if (psnJobList.size() > 0) {
			generateSrv.generateByPsnJob(psnJobList.toArray(new PsnJobVO[0]));
		}
		// 转入人员后要更新当前缓存。
		getModel().directlyDelete(objSelections);
	}

	private UFLiteralDate findEndDate(String cteamid, String psnjobEnddate) throws BusinessException {
		// 取班组已排班最后一日
		// 取人员在职日最后一日
		// 取日期较小的一个
		String strSQL = "select calendar from tbm_psncalendar where pk_psndoc in (select pk_psndoc from hi_psnjob where pk_psnjob in (select pk_psnjob from bd_team_b where cteamid = '"
				+ cteamid + "')) and calendar<='" + psnjobEnddate + "' order by calendar desc";
		String maxDate = (String) NCLocator.getInstance().lookup(IUAPQueryBS.class)
				.executeQuery(strSQL, new ColumnProcessor());
		return new UFLiteralDate(StringUtils.isEmpty(maxDate) ? psnjobEnddate : maxDate);
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-2-23 9:18:39<br>
	 * 
	 * @see nc.ui.uif2.NCAction#doAction(java.awt.event.ActionEvent)
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public void doAction(ActionEvent evt) throws Exception {
		ActionHelper.resetHintMessage(this);

		Object objSelections[] = getSelection();

		if (objSelections == null || objSelections.length == 0) {
			throw new BusinessException(ResHelper.getString("6007psn", "06007psn0201"/*
																					 * @
																					 * res
																					 * "请选择要{0}的人员!"
																					 */, strBtnName));
		}

		checkDataPermission();
		SyncWorkDialog dlg = new SyncWorkDialog(getEntranceUI());
		int iResult = dlg.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0476")/* "确认转入" */,
				ResHelper.getString("6007psn", "06007psn0202"/*
															 * @res
															 * "是否把所选择的人员{0}?"
															 */, strBtnName));

		if (iResult != UIDialog.ID_YES) {
			ActionHelper.setCancelHintMessage(this);

			return;
		}

		// if (getModel().getSwitchToDocMode() == HICommonValue.PARA_2_ENTRY) {
		// toEntryMng(objSelections);
		// } else {
		directToDoc(objSelections, dlg.getCheckBox().isSelected());
		// }

		ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("6007psn", "06007psn0203"/*
																							 * @
																							 * res
																							 * "已成功{0}!"
																							 */, strBtnName),
				getModel().getContext());
	}

	@Override
	protected Object getPermissionCheckData() {
		Object objs[] = getSelection();
		PsnJobVO[] jobs = new PsnJobVO[objs.length];
		for (int i = 0; i < objs.length; i++) {
			jobs[i] = ((PsndocAggVO) objs[i]).getParentVO().getPsnJobVO();
		}
		return jobs;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-6-17 11:17:39<br>
	 * 
	 * @author Rocex Wang
	 * @return the formEditor
	 ***************************************************************************/
	public PsndocFormEditor getFormEditor() {
		return formEditor;
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-5-11 20:09:14<br>
	 * 
	 * @see nc.ui.hr.uif2.action.HrAction#getModel()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public PsndocModel getModel() {
		return (PsndocModel) super.getModel();
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-6-17 11:17:00<br>
	 * 
	 * @return Object[]
	 * @author Rocex Wang
	 ***************************************************************************/
	private Object[] getSelection() {
		// 优先考虑复选框 ,如果复选款为空,则使用当前选中行
		if (formEditor != null && formEditor.isShowing()) {
			return new PsndocAggVO[] { (PsndocAggVO) getModel().getSelectedData() };
		} else if (getModel().getSelectedOperaDatas() == null || getModel().getSelectedOperaDatas().length == 0) {
			return new PsndocAggVO[] { (PsndocAggVO) getModel().getSelectedData() };
		}

		return getModel().getSelectedOperaDatas();
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-2-23 9:18:57<br>
	 * 
	 * @see nc.ui.uif2.NCAction#isActionEnable()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	protected boolean isActionEnable() {
		// strBtnName = getModel().getSwitchToDocMode() ==
		// HICommonValue.PARA_2_ENTRY ?
		// ResHelper.getString("6007psn", "06007psn0204")/* @res "生成入职申请" */
		// : ResHelper.getString("6007psn", "06007psn0196")/* @res "转入人员档案" */;

		setBtnName(strBtnName);
		putValue(Action.SHORT_DESCRIPTION, strBtnName + "(Ctrl+Y)");

		int selRow = ((BillManageModel) getModel()).getSelectedRow();
		int dataSize = ((BillManageModel) getModel()).getData() == null ? 0 : ((BillManageModel) getModel()).getData()
				.size();
		if (selRow > dataSize - 1) {
			return false;
		}

		// 非编辑态时可用
		boolean isEnable = getModel().getUiState() == UIState.NOT_EDIT && getModel().getSelectedData() != null;

		return isEnable;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-6-17 11:17:39<br>
	 * 
	 * @author Rocex Wang
	 * @param formEditor
	 *            the formEditor to set
	 ***************************************************************************/
	public void setFormEditor(PsndocFormEditor formEditor) {
		this.formEditor = formEditor;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-5-11 20:09:30<br>
	 * 
	 * @param model
	 * @author Rocex Wang
	 ***************************************************************************/
	public void setModel(PsndocModel model) {
		super.setModel(model);
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-5-25 9:56:02<br>
	 * 
	 * @param objSelections
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	// private void toEntryMng(Object objSelections[]) throws BusinessException
	// {
	// // 打开节点
	// IFuncletWindow window =
	// WorkbenchEnvironment.getInstance().findOpenedFuncletWindow(EntrymngConst.FUNCODE_ENTRYAPPLY);
	//
	// // 如果入职申请节点已经打开，关闭节点
	// boolean blSuccClosed = window != null ? window.closeWindow() : true;
	//
	// if (!blSuccClosed) {
	// throw new BusinessException(ResHelper.getString("6007psn",
	// "06007psn0117")/* @res "窗口已经打开,没有正确关闭!" */);
	// }
	//
	// // 否则，新打开节点
	// FuncRegisterVO funcRegVO =
	// FuncRegisterCacheAccessor.getInstance().getFuncRegisterVOByFunCode(EntrymngConst.FUNCODE_ENTRYAPPLY);
	//
	// FuncletInitData initData = new FuncletInitData();
	//
	// HRLinkData linkData = new HRLinkData();
	//
	// // 主组织主键
	// linkData.setPkOrg(getModel().getContext().getPk_org());
	//
	// // 人员工作记录主键
	// String strPk_psnjobs[] = new String[objSelections.length];
	//
	// for (int i = 0; i < objSelections.length; i++) {
	// strPk_psnjobs[i] = ((PsndocAggVO)
	// objSelections[i]).getParentVO().getPsnJobVO().getPrimaryKey();
	// }
	//
	// linkData.setUserObject(strPk_psnjobs);
	//
	// initData.setInitData(linkData);
	//
	// FuncletWindowLauncher.openFuncNodeInTabbedPane(getModel().getContext().getEntranceUI(),
	// funcRegVO,
	// initData, null, false);
	// }
}
