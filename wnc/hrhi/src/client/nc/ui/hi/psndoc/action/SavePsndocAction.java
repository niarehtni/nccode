package nc.ui.hi.psndoc.action;

import java.awt.event.ActionEvent;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.ChangeEvent;

import nc.bs.bd.psn.validator.PsnIdtypeQuery;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hr.IHRLicenseChecker;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hrp.psnbudget.IOrgBudgetQueryService;
import nc.pub.tools.HRCMCommonValue;
import nc.pub.tools.VOUtils;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hi.psndoc.model.PsndocDataManager;
import nc.ui.hi.psndoc.model.PsndocModel;
import nc.ui.hi.psndoc.view.CourtFineValidator;
import nc.ui.hi.psndoc.view.PsndocFormEditor;
import nc.ui.hi.psndoc.view.PsndocListView;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hr.tools.supervalidator.SuperFormEditorValidatorUtil;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.uif2.action.SaveAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.bd.psnid.PsnIdtypeVO;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.WorkVO;
import nc.vo.hi.psndoc.enumeration.PsnType;
import nc.vo.hi.pub.BillCodeRepeatBusinessException;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.validator.ValidateWithLevelException;
import nc.vo.hrp.psnorgbudget.ValidateResultVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/***************************************************************************
 * <br>
 * Created on 2010-2-23 9:21:24<br>
 * 
 * @author dusx, Rocex Wang
 ***************************************************************************/
public class SavePsndocAction extends SaveAction {
	private PsndocDataManager dataManager;
	private PsndocListView listView;
	private SuperFormEditorValidatorUtil superValidator; // 校验器
	private CourtFineValidator courtFineValidator;

	/***************************************************************************
	 * <br>
	 * Created on 2010-7-9 9:33:13<br>
	 * 
	 * @param psndocAggVO
	 * @author Rocex Wang
	 ***************************************************************************/
	protected void adjustOrgJobVO(PsndocAggVO psndocAggVO) {
		psndocAggVO.setTableVO(
				PsnOrgVO.getDefaultTableName(),
				mergeOrgJobVO(psndocAggVO.getTableVO(PsnOrgVO.getDefaultTableName()), psndocAggVO.getParentVO()
						.getPsnOrgVO()));
		psndocAggVO.setTableVO(
				PsnJobVO.getDefaultTableName(),
				mergeOrgJobVO(psndocAggVO.getTableVO(PsnJobVO.getDefaultTableName()), psndocAggVO.getParentVO()
						.getPsnJobVO()));

		// 将主表上的工作信息放到最新工作记录上，以规避非空校验
		// PsndocFormEditor editor = (PsndocFormEditor) getEditor();
		// if
		// (editor.getBillCardPanel().getBillTable(PsnJobVO.getDefaultTableName()).getRowCount()
		// > 0)
		// {
		// editor.getBillCardPanel()
		// .getBillModel(PsnJobVO.getDefaultTableName())
		// .setBodyRowObjectByMetaData(psndocAggVO.getParentVO().getPsnJobVO(),
		// editor.getBillCardPanel().getBillTable(PsnJobVO.getDefaultTableName()).getRowCount()
		// - 1);
		// }

	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-2-23 9:21:37<br>
	 * 
	 * @see nc.ui.uif2.actions.SaveAction#doAction(java.awt.event.ActionEvent)
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public void doAction(ActionEvent e) throws Exception {
		if (getModel().isSortEdit()) {
			saveSort();
			getDataManager().getModel().setSortEdit(false);
			getModel().fireEvent(new AppEvent(HICommonValue.EVENT_ADJUSTSORT, this, false));
			getModel().setUiState(UIState.NOT_EDIT);
			// 按照现在的分页机制无法刷新数据,所以不刷新了,減少連接數
			getDataManager().refresh();
		} else {
			if (!savePsndocVO()) {
				putValue(HrAction.MESSAGE_AFTER_ACTION, " ");
				return;
			}

			((PsndocFormEditor) getEditor()).getBillCardPanel().getBillData().clearShowWarning();
		}
		putValue(MESSAGE_AFTER_ACTION, IShowMsgConstant.getSaveSuccessInfo());
		// ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getSaveSuccessInfo(),
		// getContext());
	}

	/***************************************************************************
	 * 填充一些默认值<br>
	 * Created on 2010-6-22 10:02:22<br>
	 * 
	 * @param psndocAggVO2
	 * @author Rocex Wang
	 ***************************************************************************/
	protected void fillDefaultValue(PsndocAggVO psndocAggVO2) {
		if (psndocAggVO2 == null) {
			return;
		}
		PartTimeVO partTimeVOs[] = (PartTimeVO[]) psndocAggVO2.getTableVO(PartTimeVO.getDefaultTableName());
		if (partTimeVOs != null) {
			for (PartTimeVO partTimeVO : partTimeVOs) {
				partTimeVO.setPsntype(getModel().getPsnType());
			}
		}
		/**
		 * @author yangzxa
		 * @time 2015年12月28日16:28:56 对合同记录的continuetime进行设置
		 */
		CtrtVO[] ctrtVOs = (CtrtVO[]) psndocAggVO2.getTableVO("hi_psndoc_ctrt");
		if (ctrtVOs != null && ctrtVOs.length > 0) {
			int continuetime = 1;
			Integer conttype;
			for (CtrtVO ctrtVO : ctrtVOs) {
				// 此条合同记录的合同类型
				conttype = ctrtVO.getConttype();
				/*
				 * continuetime的规则是， （1）签订时为1； （2）变更为上一条的值， （3）终止、解除设置为0。
				 * （4）续签则为上一条+1，
				 */
				if (conttype == HRCMCommonValue.CONTTYPE_MAKE) {
					// 签订
					continuetime = 1;
				} else if (conttype == HRCMCommonValue.CONTTYPE_RELEASE || conttype == HRCMCommonValue.CONTTYPE_FINISH) {
					// 终止解除
					continuetime = 0;
				} else if (conttype == HRCMCommonValue.CONTTYPE_EXTEND) {
					// 续签
					continuetime++;
				}
				ctrtVO.setContinuetime(continuetime);
			}

		}
		for (SuperVO superVO : psndocAggVO2.getAllChildrenVO()) {
			if (superVO.getAttributeValue(PsndocVO.PK_GROUP) == null) {
				superVO.setAttributeValue(PsndocVO.PK_GROUP, PubEnv.getPk_group());
			}
			if (superVO.getAttributeValue(PsndocVO.PK_ORG) == null) {
				superVO.setAttributeValue(PsndocVO.PK_ORG, getContext().getPk_org());
			}

			// 返聘再聘设置pk_psndoc
			if (superVO.getAttributeValue(PsnOrgVO.PK_PSNDOC) == null) {
				superVO.setAttributeValue(PsnOrgVO.PK_PSNDOC, psndocAggVO2.getParentVO().getPk_psndoc());
			}

			if (!(superVO instanceof PsnOrgVO) && superVO.getAttributeValue(PsnOrgVO.PK_PSNORG) == null) {
				superVO.setAttributeValue(PsnOrgVO.PK_PSNORG, psndocAggVO2.getParentVO().getPsnOrgVO().getPrimaryKey());
			}
			if (!(superVO instanceof PsnJobVO) && !(superVO instanceof WorkVO)
					&& superVO.getAttributeValue(PsnJobVO.PK_PSNJOB) == null) {
				superVO.setAttributeValue(PsnJobVO.PK_PSNJOB, psndocAggVO2.getParentVO().getPsnJobVO().getPrimaryKey());
			}
			if (superVO.getAttributeValue(PsnJobVO.ASSGID) == null) {
				superVO.setAttributeValue(PsnJobVO.ASSGID, psndocAggVO2.getParentVO().getPsnJobVO().getAssgid());
			}
		}
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 15:07:10<br>
	 * 
	 * @return PsndocDataManager
	 * @author Rocex Wang
	 ***************************************************************************/
	public PsndocDataManager getDataManager() {
		return dataManager;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 15:10:25<br>
	 * 
	 * @return PsndocListView
	 * @author Rocex Wang
	 ***************************************************************************/
	public PsndocListView getListView() {
		return listView;
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-6-1 10:51:39<br>
	 * 
	 * @see nc.ui.hr.uif2.action.SaveAction#getModel()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public PsndocModel getModel() {
		return (PsndocModel) super.getModel();
	}

	private <T extends SuperVO> T[] mergeOrgJobVO(T[] tableVO, T orgJobVO) {
		if (tableVO == null || tableVO.length == 0) {
			T[] arr = (T[]) Array.newInstance(orgJobVO.getClass(), 1);
			arr[0] = orgJobVO;
			return arr;
		}
		if (getModel().getUiState() == UIState.EDIT) {
			// 修改时
			for (int i = 0; i < tableVO.length; i++) {
				if (VOStatus.DELETED == tableVO[i].getStatus()) {
					continue;
				}
				if (orgJobVO.getPrimaryKey().equals(tableVO[i].getPrimaryKey())) {
					tableVO[i] = orgJobVO;
					break;
				}
			}
		} else if (getModel().getUiState() == UIState.ADD) {
			// 新增时
			return (T[]) ArrayUtils.add(tableVO, orgJobVO);
		}
		return tableVO;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-2-23 9:21:44<br>
	 * 
	 * @throws ValidateWithLevelException
	 * @throws Exception
	 * @author Rocex Wang
	 ***************************************************************************/
	@SuppressWarnings("restriction")
	protected boolean savePsndocVO() throws ValidateWithLevelException, Exception {
		getListView().stopCellEditing();
		Object value = getEditor().getValue();

		PsndocAggVO psndocAggVO = (PsndocAggVO) value;

		// fix bug NCdp205505468 人员维护照片，在上传时应该控制不能大于200K，不是在保存的时候。
		byte[] bytes = (byte[]) psndocAggVO.getParentVO().getPhoto();
		if (bytes != null && bytes.length >= 204800) {
			if (MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0480")/* "确认保存" */,
					ResHelper.getString("6007psn", "16007psn0074")/* "人员照片大小系统建议为200KB以下，现已超出，是否继续保存?" */) != MessageDialog.ID_YES) {
				return false;
			}

			// throw new BusinessException("人员编码:" +
			// psndocAggVO.getParentVO().getAttributeValue(PsndocVO.CODE) +
			// " 的照片不能大于200KB");
		}

		adjustOrgJobVO(psndocAggVO);
		// 填充一些默认值
		fillDefaultValue(psndocAggVO);

		psndocAggVO = validatePsndoc(value);

		// 这里校验子集非空
		if (!validateSubNotNull()) {
			return false;
		}

		if (psndocAggVO == null) {
			return false;
		}

		if (!((PsndocFormEditor) getEditor()).getBillCardPanel().getBillData().execValidateFormulas()) {
			return false;
		}

		if (getModel().getUiState() == UIState.ADD) {

			boolean bl = NCLocator.getInstance().lookup(IHRLicenseChecker.class).checkPsnCount();

			if (bl
					&& getModel().getContext().getNodeCode().equals(HICommonValue.FUNC_CODE_REGISTER)
					&& MessageDialog.showYesNoDlg(getEntranceUI(),
							ResHelper.getString("6007psn", "06007psn0480")/* "确认保存" */,
							ResHelper.getString("6007psn", "06007psn0434")/* "当前系统员工数已达到最大授权数,是否保存?" */) != MessageDialog.ID_YES) {
				return false;
			}

			// 仅在返聘再聘时校验离职后再入职时间间隔，pk_psndoc不为null则为返聘再聘人员
			if (getModel().getCurrentPkPsndoc() != null) {
				if (!validateRegDate())
					return false;
			}
			try {
				getModel().add(value);
			} catch (Exception e) {
				if (e instanceof BillCodeRepeatBusinessException) {
					if (HICommonValue.NBCR_PSNDOC_CODE.equals(((BillCodeRepeatBusinessException) e).getNbcrcode())) {
						((PsndocFormEditor) getEditor()).getBillCardPanel().getHeadItem(PsndocVO.CODE)
								.setValue(((BillCodeRepeatBusinessException) e).getNewCode());
					}
					if (HICommonValue.NBCR_PSNDOC_CLERKCODE.equals(((BillCodeRepeatBusinessException) e).getNbcrcode())) {
						((PsndocFormEditor) getEditor()).getBillCardPanel().getHeadItem("hi_psnjob_clerkcode")
								.setValue(((BillCodeRepeatBusinessException) e).getNewCode());
					}
					throw e;

				} else
					throw e;
			}
		} else if (getModel().getUiState() == UIState.EDIT) {
			/** 员工信息维护修改时，校验编制 yunana */
			// 相关人员不进行编制校验
			// if(psndocAggVO.getParentVO().getPsnOrgVO().getIndocflag() != null
			// &&
			// psndocAggVO.getParentVO().getPsnOrgVO().getIndocflag().booleanValue()
			// &&
			// psndocAggVO.getParentVO().getPsnOrgVO().getPsntype().intValue()
			// == ((Integer)
			// PsnType.EMPLOYEE.value()).intValue() ){
			// if(!validateBudget(value)){
			// return false;
			// }
			// }
			getModel().update(value);
		}
		getModel().setUiState(UIState.NOT_EDIT);
		// 保存后，子集缓存全部清空，重新取数。
		PsndocFormEditor editor = (PsndocFormEditor) getEditor();
		getModel().getSubHaveLoaded().clear();
		// 保存后将入职类型变成初次入职
		getModel().setInJobType(HICommonValue.JOB_HIRE);
		// editor.loadCurrentRowSubData();
		return true;
	}

	private boolean validateBudget(Object value) throws Exception {
		String pk_psndoc = ((PsndocAggVO) value).getParentVO().getPsnJobVO().getPk_psnjob();
		String[] strPk_psnjobs = { pk_psndoc };
		// 校验编制
		ValidateResultVO resultVOs[] = NCLocator.getInstance().lookup(IOrgBudgetQueryService.class)
				.validateBudgetValue(getContext(), strPk_psnjobs);

		String strErrorMsg = "";
		String strWarningMsg = "";

		if (resultVOs != null) {
			for (ValidateResultVO resultVO : resultVOs) {
				if (!resultVO.isValid()) {
					strErrorMsg += "\n" + resultVO.getHintMsg();
				} else if (resultVO.getHintMsg() != null) {
					strWarningMsg += "\n" + resultVO.getHintMsg();
				}
			}

			if (strErrorMsg.length() > 0) {
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
					return false;
				}
			}
		}
		return true;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 15:10:36<br>
	 * 
	 * @throws Exception
	 * @author Rocex Wang
	 ***************************************************************************/
	protected void saveSort() throws Exception {
		getListView().stopCellEditing();
		BillPanelUtils.listNotNullValidate(getListView().getBillListPanel(), IBillItem.HEAD);
		BillModel headBillModel = listView.getBillListPanel().getHeadBillModel();
		int iRowCount = headBillModel.getRowCount();
		ArrayList<PsnJobVO> listJobVO = new ArrayList<PsnJobVO>();
		ArrayList<PsndocAggVO> listPsndocAggVO = new ArrayList<PsndocAggVO>();
		List<PsndocAggVO> listAllData = ((BillManageModel) getModel()).getData();
		if (listView.getBillListPanel().getHeadTable().isEditing()) {
			listView.getBillListPanel().getHeadTable()
					.editingStopped(new ChangeEvent(listView.getBillListPanel().getHeadTable().getCellEditor()));
			listView.updateUI();
		}
		for (int i = 0; i < iRowCount; i++) {
			PsnJobVO psnJobVO = new PsnJobVO();
			psnJobVO.setPk_psnjob(listAllData.get(i).getParentVO().getPsnJobVO().getPk_psnjob());
			Object showOrder = headBillModel.getValueAt(i, "hi_psnjob_showorder");
			if (showOrder == null || showOrder.toString() == null || showOrder.toString().trim().equals("")) {
				psnJobVO.setShoworder(HICommonValue.DEFAULT_VALUE_SHOWORDER);
			} else {
				psnJobVO.setShoworder((Integer) showOrder);
			}
			psnJobVO.setStatus(VOStatus.UPDATED);
			listJobVO.add(psnJobVO);
			listPsndocAggVO.add(listAllData.get(i));
		}
		if (listJobVO.size() == 0) {
			return;
		}
		// PsnJobVO psnJobVOs[] =
		NCLocator.getInstance().lookup(IPsndocService.class).adjustPsnSort(listJobVO.toArray(new PsnJobVO[0]));
		// for (int i = 0; i < psnJobVOs.length; i++) {
		// for (Iterator iterator = listPsndocAggVO.iterator();
		// iterator.hasNext();) {
		// PsndocAggVO psndocAggVO = (PsndocAggVO) iterator.next();
		// if (ObjectUtils.equals(psnJobVOs[i].getPk_psnjob(),
		// psndocAggVO.getParentVO().getPsnJobVO().getPk_psnjob())) {
		// psndocAggVO.getParentVO().setPsnJobVO(psnJobVOs[i]);
		// break;
		// }
		// }
		// }
		// getModel().directlyUpdate(listPsndocAggVO.toArray(new
		// PsndocAggVO[0]));
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 15:10:40<br>
	 * 
	 * @param datamanger
	 * @author Rocex Wang
	 ***************************************************************************/
	public void setDataManager(PsndocDataManager datamanger) {
		this.dataManager = datamanger;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 15:10:44<br>
	 * 
	 * @param listview
	 * @author Rocex Wang
	 ***************************************************************************/
	public void setListView(PsndocListView listview) {
		this.listView = listview;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-6-1 10:52:14<br>
	 * 
	 * @param model
	 * @author Rocex Wang
	 ***************************************************************************/
	public void setModel(PsndocModel model) {
		super.setModel(model);
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 15:10:48<br>
	 * 
	 * @param supervalidator
	 * @author Rocex Wang
	 ***************************************************************************/
	public void setSuperValidator(SuperFormEditorValidatorUtil supervalidator) {
		this.superValidator = supervalidator;
	}

	public CourtFineValidator getCourtFineValidator() {
		if (courtFineValidator == null) {
			courtFineValidator = new CourtFineValidator();
		}
		return courtFineValidator;
	}

	public void setCourtFineValidator(CourtFineValidator courtFineValidator) {
		this.courtFineValidator = courtFineValidator;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-7-6 15:18:35<br>
	 * 
	 * @param value
	 * @return PsndocAggVO
	 * @throws ValidateWithLevelException
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	private PsndocAggVO validatePsndoc(Object value) throws ValidateWithLevelException, BusinessException {
		validate(value);

		// 保存校验，应该提出来一个bean
		PsndocAggVO psndocAggVO = (PsndocAggVO) value;
		// 法院強制扣款子集校驗
		if (getCourtFineValidator() != null) {
			ValidationFailure message = getCourtFineValidator().validate(value);
			if (null != message) {
				MessageDialog.showErrorDlg(null, "校验", message.getMessage());
				return null;
			}
		}
		// 校验当前人员是否已到16周岁
		UFLiteralDate date = psndocAggVO.getParentVO().getBirthdate();
		if (date != null) {
			UFLiteralDate today = PubEnv.getServerLiteralDate();
			int year = date.getYear();

			int year18 = year + 16;

			UFLiteralDate date18 = new UFLiteralDate(year18 + "-" + date.getMonth() + "-" + date.getDay());
			if (date18.afterDate(today)) {
				// 不够18岁
				MessageDialog.showWarningDlg(null, null, ResHelper.getString("6007psn", "06007psn0320"));
			}
		}

		// 校验当前人员证件子集中身份证是否合法
		CertVO[] cert = (CertVO[]) psndocAggVO.getTableVO(CertVO.getDefaultTableName());
		if (cert != null && cert.length > 0) {
			for (CertVO c : cert) {
				if (StringUtils.isNotBlank(psndocAggVO.getParentVO().getId())
						&& StringUtils.isNotBlank(psndocAggVO.getParentVO().getIdtype())) {
					if (c.getIsstart().equals(UFBoolean.TRUE)) {
						// c.setId(psndocAggVO.getParentVO().getId());
						// c.setIdtype(psndocAggVO.getParentVO().getIdtype());
						// NCdp205860006拢卢驴脥禄搂虏煤脝路麓铆脦贸拢卢麓脦鲁枚脗脽录颅脫娄赂脙脢脟陆芦鹿麓脩隆碌脛脛卢脠脧碌脛脰陇录镁脨脜脧垄赂眉脨脗碌陆脠脣脭卤脨脜脧垄卤铆拢卢脤忙禄禄脡脧脙忙碌脛脗脽录颅
						psndocAggVO.getParentVO().setId(c.getId());
						psndocAggVO.getParentVO().setIdtype(c.getIdtype());
					}
				} else {
					continue;
				}
				String strClassName = PsnIdtypeQuery.getPsnIdtypeVo(c.getIdtype()).getIdtypevalidat();
				if (StringUtils.isEmpty(strClassName))
					continue;
				try {
					nc.vo.bd.psn.PsndocVO psndocUapVO = new nc.vo.bd.psn.PsndocVO();
					BeanUtils.copyProperties(psndocUapVO, psndocAggVO.getParentVO());
					psndocUapVO.setId(c.getId());
					psndocUapVO.setIdtype(c.getIdtype());
					ValidationFailure failure = ((nc.bs.uif2.validation.Validator) Class.forName(strClassName)
							.newInstance()).validate(psndocUapVO);
					if (failure != null) {
						throw new ValidateWithLevelException(failure.getMessage(),
								ValidateWithLevelException.LEVEL_DECIDEBYUSER);
					}
				} catch (InstantiationException ex) {
					Logger.error(ex.getMessage(), ex);
				}

				catch (IllegalAccessException ex) {
					Logger.error(ex.getMessage(), ex);
				} catch (ClassNotFoundException ex) {
					Logger.error(ex.getMessage(), ex);
				} catch (InvocationTargetException ex) {
					Logger.error(ex.getMessage(), ex);
				} catch (ValidateWithLevelException ex) {
					if (ex.getLevel() == ValidateWithLevelException.LEVEL_SERIOUS) {
						throw ex;
					}
					if (ex.getLevel() == ValidateWithLevelException.LEVEL_DECIDEBYUSER) {
						if (MessageDialog.showYesNoDlg(getEntranceUI(),
								ResHelper.getString("6007psn", "06007psn0475")/*
																			 * @
																			 * res
																			 * "确认继续"
																			 */,
								ex.getMessage() + ResHelper.getString("6007psn", "06007psn0130")/*
																								 * @
																								 * res
																								 * "，是否继续？"
																								 */) != UIDialog.ID_YES) {
							return null;
						}
					}
				}
			}
		}

		if (superValidator != null) {
			try {
				superValidator.validate("additionalValidationOfSave");
			} catch (ValidateWithLevelException ex) {
				if (ex.getLevel() == ValidateWithLevelException.LEVEL_SERIOUS) {
					throw ex;
				}
				if (ex.getLevel() == ValidateWithLevelException.LEVEL_DECIDEBYUSER) {
					if (MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0475")/*
																												 * @
																												 * res
																												 * "确认继续"
																												 */,
							ex.getMessage() + ResHelper.getString("6007psn", "06007psn0130")/*
																							 * @
																							 * res
																							 * "，是否继续？"
																							 */) != UIDialog.ID_YES) {
						return null;
					}
				} else if (ex.getLevel() == ValidateWithLevelException.LEVEL_CANACCEPT) {
					MessageDialog.showWarningDlg(getEntranceUI(), null, ex.getMessage());
				}
			}
		}

		// 之前的入职登记对话框中点击确定时的校验现在保存时也得校验
		try {
			getAddSave(psndocAggVO);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

		return psndocAggVO;
	}

	public PsndocAggVO getAddSave(PsndocAggVO psndocsAggVO) throws Exception {
		PsndocVO psndocVO = psndocsAggVO.getParentVO();// 保存时从界面上获取的数据，这里其实只需要用到它的parentvo即可，不对此对象做修改操作
		PsndocAggVO psndocAggVO = null;// 根据姓名+证件类型+证件号从系统里面查询出的已经存在的人员
		IPsndocService psndocService = NCLocator.getInstance().lookup(IPsndocService.class);
		try {
			psndocAggVO = psndocService.checkPsnUnique(psndocVO, true);
		} catch (BusinessException ex) {
			throw new BusinessException(ex.getMessage());
		}

		if (psndocAggVO == null || psndocAggVO.getParentVO() == null)// 没有组织关系，直接新增人员
		{
			return psndocsAggVO;
		}

		if (psndocAggVO.getParentVO().getDie_date() != null && psndocAggVO.getParentVO().getDie_remark() != null) {
			throw new BusinessException(ResHelper.getString("6007psn", "06007psn0346")/* "当前录入人员已身故,不能继续增加." */);
		}

		// 如果人员已存在，则首先判断是否为UAP管理人员
		UFBoolean isuapmanage = psndocAggVO.getParentVO().getIsuapmanage();
		if (isuapmanage.booleanValue()) {
			// 启用状态
			int enablestate = psndocAggVO.getParentVO().getEnablestate();
			if (enablestate == IPubEnumConst.ENABLESTATE_DISABLE) {// 已停用
																	// 提示“该人员为UAP管理的停用人员，是否继续？”
				if (MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0475")/*
																											 * @
																											 * res
																											 * "确认继续"
																											 */,
						ResHelper.getString("6007psn", "06007psn0456")/* "该人员为UAP管理的停用人员，是否继续？" */) == UIDialog.ID_YES) {
					return psndocsAggVO;
				} else {
					throw new BusinessException(ResHelper.getString("6007psn", "06007psn0456")/* "该人员为UAP管理的停用人员，是否继续？" */);
				}
			}
		}

		if (!psndocAggVO.getParentVO().getPsnOrgVO().getEndflag().booleanValue()) // 有组织关系，并且尚未结束。
		{
			String strMsg = ResHelper.getString("6007psn", "06007psn0145")/*
																		 * @res
																		 * "集团中已存在相同人员，不能再次采集！"
																		 */
					+ "\n";
			strMsg += generateUniqueMsg(psndocAggVO.getParentVO(), psndocVO, false);
			throw new BusinessException(strMsg);
		} else if (psndocAggVO.getParentVO().getPsnOrgVO().getEndflag().booleanValue()) {
			// 曾有过组织关系，现已结束
			// String strMsg = "此人曾有过组织关系，是否继续(返聘、再聘) ?\n{0}";
			String strMsg = ResHelper.getString("6007psn", "06007psn0146")/*
																		 * @res
																		 * "此人曾有过组织关系，是否继续 ?"
																		 */
					+ "\n";
			if (psndocAggVO.getParentVO().getPsnOrgVO().getPsntype().intValue() == ((Integer) PsnType.POI.value())
					.intValue()) {
				// 相关人员提示信息不同
				strMsg = ResHelper.getString("6007psn", "06007psn0147")/*
																		 * @res
																		 * "此人为集团历史相关人员，信息如下，是否继续?"
																		 */
						+ "\n";
			}
			strMsg += generateUniqueMsg(psndocAggVO.getParentVO(), psndocVO, true);
			if (MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0475")/*
																										 * @
																										 * res
																										 * "确认继续"
																										 */, strMsg) == UIDialog.ID_YES) {
				psndocAggVO.getParentVO().setPsnOrgVO(psndocVO.getPsnOrgVO());
				psndocAggVO.getParentVO().setPsnJobVO(psndocVO.getPsnJobVO());
				psndocsAggVO.setParentVO(psndocAggVO.getParentVO());
				psndocsAggVO.setTableVO(CertVO.getDefaultTableName(), null);//
				return psndocsAggVO;
			}
		}

		return psndocsAggVO;
	}

	private String generateUniqueMsg(PsndocVO dbVO, PsndocVO clientVO, boolean isreturn) {
		String strFieldCodes[] = { PsndocVO.NAME, PsndocVO.ID, PsndocVO.IDTYPE };
		String strMsg = "";
		String idtype = dbVO.getIdtype();
		PsnIdtypeVO psnIdtypeVO = null;
		try {
			psnIdtypeVO = (PsnIdtypeVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByPk(null, PsnIdtypeVO.class, idtype);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		for (String strFieldCode : strFieldCodes) {
			// String strFieldChnName =
			// getCenterPanel().getBillData().getHeadItem(strFieldCode).getName();
			String strFieldChnName = ((PsndocFormEditor) getEditor()).getBillCardPanel().getBillData()
					.getHeadItem(strFieldCode).getName();
			// if (PsndocVO.ID.equalsIgnoreCase(strFieldCode))
			// {
			// strMsg += strFieldChnName + ": " + clientVO.getId() + "\n";
			// }
			if (PsndocVO.IDTYPE.equalsIgnoreCase(strFieldCode)) {
				strMsg += strFieldChnName + ": " + VOUtils.getNameByVO(psnIdtypeVO) + (isreturn ? "\n" : "，");
			} else if (strFieldCode.startsWith(PsndocVO.NAME)) {
				String fieldValue = MultiLangHelper.getName(dbVO);
				fieldValue = StringUtils.isBlank(fieldValue) ? ResHelper.getString("6007psn", "06007psn0148")/*
																											 * @
																											 * res
																											 * "无"
																											 */
				: fieldValue;
				strMsg += strFieldChnName + ": " + fieldValue + (isreturn ? "\n" : "，");
			} else {
				String fieldValue = (String) dbVO.getAttributeValue(strFieldCode);
				// 处理提示中的null值
				fieldValue = StringUtils.isBlank(fieldValue) ? ResHelper.getString("6007psn", "06007psn0148")/*
																											 * @
																											 * res
																											 * "无"
																											 */
				: fieldValue;
				strMsg += strFieldChnName + ": " + fieldValue + (isreturn ? "\n" : "，");
			}
		}
		if (dbVO.getPsnJobVO() == null) {
			return strMsg;
		}

		String orgname = dbVO.getPsnJobVO().getOrgname() == null ? ResHelper.getString("6007psn", "06007psn0148")/*
																												 * @
																												 * res
																												 * "无"
																												 */
		: (String) dbVO.getPsnJobVO().getOrgname();
		strMsg += ResHelper.getString("6007psn", "06007psn0149")/* @res "所在组织: " */
				+ orgname + (isreturn ? "\n" : "，");

		String deptname = dbVO.getPsnJobVO().getDeptname() == null ? ResHelper.getString("6007psn", "06007psn0148")/*
																													 * @
																													 * res
																													 * "无"
																													 */
		: (String) dbVO.getPsnJobVO().getDeptname();
		strMsg += ResHelper.getString("6007psn", "06007psn0150")/* @res "所在部门: " */
				+ deptname + (isreturn ? "\n" : "，");

		if (dbVO.getPsnOrgVO().getPsntype().intValue() != ((Integer) PsnType.POI.value()).intValue()) {
			// 相关人员没有岗位
			String jobname = dbVO.getPsnJobVO().getJobname() == null ? ResHelper.getString("6007psn", "06007psn0148")/*
																													 * @
																													 * res
																													 * "无"
																													 */
			: (String) dbVO.getPsnJobVO().getJobname();
			strMsg += ResHelper.getString("6007psn", "06007psn0151")/*
																	 * @res
																	 * "所在岗位: "
																	 */
					+ jobname + "";
		}
		return strMsg;
	}

	/**
	 * 离职后再入职时间间隔校验校验
	 * 
	 * @return boolean 成功为true，不成功为false
	 * @author yangshuo
	 * @throws Exception
	 * @Created on 2012-12-3 上午11:21:37
	 */
	private boolean validateRegDate() throws Exception {

		Object value = getEditor().getValue();
		PsndocAggVO psndocAggVO = (PsndocAggVO) value;
		// 现在的入职日期
		UFLiteralDate currBegindate = psndocAggVO.getParentVO().getPsnOrgVO().getBegindate();
		String pk_org = getModel().getContext().getPk_org();
		String pk_psndoc = psndocAggVO.getParentVO().getPk_psndoc();
		HashMap<String, String> org = new HashMap<String, String>();
		HashMap<String, UFLiteralDate> date = new HashMap<String, UFLiteralDate>();
		org.put(pk_psndoc, pk_org);
		date.put(pk_psndoc, currBegindate);
		HashMap<String, String> name = NCLocator.getInstance().lookup(IPsndocQryService.class)
				.validateRegDate(org, date);

		if (name == null) {
			return true;
		}
		Integer para = SysInitQuery.getParaInt(pk_org, HICommonValue.PARA_7);
		return MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0475")/*
																										 * @
																										 * res
																										 * "确认继续"
																										 */, ResHelper
				.getString("6007psn", "06007psn0440", name.values().toArray(new String[0])[0], para.toString())) == UIDialog.ID_YES;
		/* @res"人员[{0}]上一次离职至今不满{1}个月，是否继续入职？" */

	}

	/**
	 * 校验子集非空
	 * 
	 * @return 为空且用户在提示话框中选择否返回 false，其他情况返回true
	 * @author yangshuo
	 * @Created on 2012-12-7 下午04:36:28
	 */
	private boolean validateSubNotNull() throws BusinessException {
		Object value = getEditor().getValue();
		PsndocAggVO psndocAggVO = (PsndocAggVO) value;
		HashMap<String, String> showTab = getBodyShowTablenames();

		if (getModel().getUiState() == UIState.ADD) {
			String[] nullSubSetNames = getModel().queryNotNullSubset(psndocAggVO, showTab);
			if (nullSubSetNames != null)
				return showNullSubsetWarningDlg(nullSubSetNames);
			else
				return true;
		} else if (getModel().getUiState() == UIState.EDIT) {
			String[] nullSubSetNames = getModel().validateSubNotNull(psndocAggVO, showTab);
			if (nullSubSetNames != null)
				return showNullSubsetWarningDlg(nullSubSetNames);
			else
				return true;
		}

		return true;
	}

	/**
	 * 显示子集为空警告对话框
	 * 
	 * @return 为空且用户在提示话框中选择否返回 false，其他情况返回true
	 * @author yangshuo
	 * @Created on 2012-12-7 下午04:33:37
	 */

	private boolean showNullSubsetWarningDlg(String[] nullSubSetNames) {
		if (nullSubSetNames != null) {
			// 处理提示消息，格式为[..,..,...]
			String msg = "";
			for (String nullSubSetName : nullSubSetNames) {
				if (StringUtils.isEmpty(msg))
					msg = "[" + nullSubSetName;

				else
					msg += "," + nullSubSetName;
			}
			msg += "]";

			// 弹出显示对话框
			int result = MessageDialog.showYesNoDlg(getEntranceUI(),
					ResHelper.getString("6007psn", "06007psn0480", new String[] { msg })/* "确认保存" */
					, ResHelper.getString("6007psn", "06007psn0441", new String[] { msg })/* "{0}子集没有录入任何数据，是否继续保存?" */);
			if (UIDialog.ID_NO == result)
				return false;
			else
				return true;
		}
		return true;
	}

	/**
	 * 获得界面上显示所有子集表名 -> 表显示名称 hashmap
	 * 
	 * @author yangshuo
	 * @Created on 2012-12-7 上午10:10:27
	 */
	private HashMap<String, String> getBodyShowTablenames() {
		PsndocFormEditor editor = (PsndocFormEditor) getEditor();
		String[] tableCodes = editor.getBillCardPanel().getBillData().getBodyTableCodes();
		HashMap<String, String> result = new HashMap<String, String>();

		for (String tableCode : tableCodes) {
			// 如果子集下所有项都不显示则不校验
			if (null == editor.getBillCardPanel().getBillData().getShowItems(BillData.BODY, tableCode))
				continue;

			result.put(tableCode, editor.getBillCardPanel().getBillData().getBodyTableName(tableCode));
		}

		return result;
	}

}
