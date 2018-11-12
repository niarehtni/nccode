package nc.bs.hrsms.ta.sss.shopleave.prcss;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrsms.ta.sss.common.ShopTaListBasePageModel;
import nc.bs.hrsms.ta.sss.shopleave.ShopLeaveApplyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.Logger;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.ta.away.lsnr.AwaySaveProcessor;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.ta.ILeaveApplyApproveManageMaintain;
import nc.itf.ta.ILeaveApplyQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.exception.LfwValidateException;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveCheckResult;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leave.SplitBillResult;
import nc.vo.ta.leave.pf.validator.PFSaveLeaveValidator;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

public class ShopLeaveSaveProcessor implements ISaveProcessor {

	// 休假申请保存时的确认对话框的Id
	private String DIALOG_SAVE = "dlg_Leave_save";

	// 拆单的提示信息变量名
	public static final String APP_ATTR_SPLITBILL_DATAS = "app_attr_splitbill_datas";

	private LeaveCheckResult<AggLeaveVO> checkResult = null;

	/**
	 * 保存前操作
	 * 
	 * @param aggVo
	 * @return
	 */
	@Override
	public void onBeforeVOSave(AggregatedValueObject aggVO) {

	}

	/**
	 * 休假申请/哺乳假申请的保存前校验操作
	 * 
	 * @param aggVo
	 * @return
	 */
	@Override
	public boolean checkBeforeVOSave(AggregatedValueObject aggVO)
			throws Exception {
		// 返回值
		Boolean result = AppInteractionUtil.getConfirmDialogResult(DIALOG_SAVE);
		if (result != null && Boolean.FALSE.equals(result)) {
			return false;
		}
		AggLeaveVO aggLeaveVO = (AggLeaveVO) aggVO;
		String primaryKey = aggLeaveVO.getLeavehVO().getPrimaryKey();
		LeavehVO leavehVO = (LeavehVO) aggLeaveVO.getParentVO();
		//考勤档案已经结束的人员新增档案结束日期前的数据时pk_psnorg字段为空，无法保存数据
		PsnJobVO psnjobVO = null;
		try {
			psnjobVO = (PsnJobVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(PsnJobVO.class, leavehVO.getPk_psnjob(), null);
		} catch (HrssException e1) {
			throw new Exception(e1);
		}
		leavehVO.setPk_psnorg(psnjobVO.getPk_psnorg());

		// 是否哺乳家标识
		UFBoolean islactation = leavehVO.getIslactation();
		
		String billCode = leavehVO.getBill_code();// 单据编码

		LeavebVO[] leavebVOs = aggLeaveVO.getLeavebVOs();
		for (int i = 0; leavebVOs != null && i < leavebVOs.length; i++) {
			leavebVOs[i].setIslactation(islactation);
			leavebVOs[i].setPk_psndoc(leavehVO.getPk_psndoc());
			leavebVOs[i].setPk_psnjob(leavehVO.getPk_psnjob());
		}

		// 获得是否继续标志
		String confirmFlag = getLifeCycleContext().getParameter("isContinue");
		if (StringUtils.isEmpty(confirmFlag)) {
			if (!Boolean.TRUE.equals(result)) {
				LfwView viewMain = AppLifeCycleContext.current()
						.getViewContext().getView();
				FormComp formComp = (FormComp) viewMain.getViewComponents()
						.getComponent(ShopLeaveApplyConsts.PAGE_FORM_LEAVEINFO);
				// 校验
				ValidationFailure failur = new PFSaveLeaveValidator()
						.validate(aggVO);
				if (failur != null) {
					CommonUtil.showErrorDialog(
							ResHelper.getString("c_pub-res", "0c_pub-res0169"),
							failur.getMessage());
				}
				// 休假申请主VO
				LeavehVO headVO = ((AggLeaveVO) aggVO).getLeavehVO();
				if (!islactation.booleanValue()) {
					// 休假总时长
					UFDouble sumHour = headVO.getSumhour() == null ? UFDouble.ZERO_DBL
							: headVO.getSumhour();
					if (sumHour.compareTo(UFDouble.ZERO_DBL) <= 0) {
						if (formComp != null) {
							String errorMsg = nc.vo.ml.NCLangRes4VoTransl
									.getNCLangRes().getStrByID("c_ta-res",
											"0c_ta-res0072")/*
															 * @ res
															 * "休假总时长为0，不能进行保存操作！"
															 */;
							LfwValidateException exception = new LfwValidateException(
									errorMsg);
							exception.setViewId(viewMain.getId());
							exception.addComponentId(formComp.getId());
							throw exception;
						}
					}
				} else {
					if (formComp != null) {
						// 单日哺乳时长
						UFDouble lactationhour = headVO.getLactationhour() == null ? UFDouble.ZERO_DBL
								: headVO.getLactationhour();
//						headVO.setSumhour(lactationhour);
						if (lactationhour.compareTo(UFDouble.ZERO_DBL) <= 0) {
							String errorMsg = nc.vo.ml.NCLangRes4VoTransl
									.getNCLangRes().getStrByID("c_ta-res",
											"0c_ta-res0141")/*
															 * @ res
															 * "单日哺乳时长小于等于0，不能进行保存操作！"
															 */;
							LfwValidateException exception = new LfwValidateException(
									errorMsg);
							exception.setViewId(viewMain.getId());
							exception.addComponentId(formComp.getId());
							throw exception;
						}
					}

				}
			}
			// 保存前先校验一次，如果有单据冲突，但是是被允许的，则显示这些冲突单据，并询问用户是否保存
			Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkMutextResult = null;
			try {
				// 保存前先校验一次，如果有单据冲突，但是是被允许的，则显示这些冲突单据，并询问用户是否保存
				checkMutextResult = getCheckResult(aggVO).getMutexCheckResult();
				if (checkMutextResult != null) {
					AwaySaveProcessor
							.showConflictInfoList(
									new BillMutexException(null,
											checkMutextResult),
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("c_ta-res",
													"0c_ta-res0008")/*
																	 * @ res
																	 * "与下列单据有时间冲突，是否保存?"
																	 */,
									ShopTaApplyConsts.DIALOG_CONFIRM);
					return false;
				}
			} catch (BillMutexException ex) {
				AwaySaveProcessor.showConflictInfoList(
						((BillMutexException) ex),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"c_ta-res", "0c_ta-res0007")/*
															 * @ res
															 * "与下列单据有时间冲突，操作不能继续"
															 */,
						ShopTaApplyConsts.DIALOG_ALERT);
				return false;
			}
		}
		// 拆单是否继续标志
		SplitBillResult<AggLeaveVO> splitResult = null;
		try {
			splitResult = getCheckResult(aggVO).getSplitResult();
		} catch (BusinessException ex) {
			new HrssException(ex).deal();
		}
		boolean flag = true;
		String isSplitBillContinue = getLifeCycleContext().getParameter(
				"isSplitBillContinue");
		if (StringUtils.isEmpty(isSplitBillContinue)
				&& !UFBoolean.TRUE.equals(islactation)) {
			// 休假拆单
			if (splitResult != null && splitResult.needQueryUser()) {
				AppUtil.addAppAttr("isContinue", confirmFlag);// 同时发生单据冲突和拆单时使用
				flag = showSplitInfoList(primaryKey, splitResult);
			}
		}
		if(!flag){
			return false;
		}
	
		AppUtil.addAppAttr("isContinue", null);
		AggLeaveVO[] newAggVOs = null;
		try {
			ILeaveApplyApproveManageMaintain service = ServiceLocator
					.lookup(ILeaveApplyApproveManageMaintain.class);
			if (StringUtils.isEmpty(primaryKey)) {
				newAggVOs = service.insertData(splitResult);
			} else {
				newAggVOs = service.updateData(splitResult);
			}
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		String temp_primaryKey = null;
		String temp_billCode = null;
		LeavehVO temp_leavehVO = null;
		if (!ArrayUtils.isEmpty(newAggVOs)) {
			for (AggLeaveVO newAggVO : newAggVOs) {
				temp_leavehVO = newAggVO.getLeavehVO();
				temp_billCode = temp_leavehVO.getBill_code();
				if (billCode.equals(temp_billCode)) {
					temp_primaryKey = temp_leavehVO.getPrimaryKey();
					break;
				}
			}
		}
		if (!StringUtils.isEmpty(temp_primaryKey)) {
			AppUtil.addAppAttr("App_newAggVO_PrimaryKey", temp_primaryKey);
		}

		return true;
	}

	/**
	 * 获取保存校验的结果
	 * 
	 * @param aggVO
	 * @return
	 * @throws HrssException
	 * @throws BusinessException
	 */
	private LeaveCheckResult<AggLeaveVO> getCheckResult(
			AggregatedValueObject aggVO) throws BusinessException {
		if (checkResult != null) {
			return checkResult;
		}
		try {
			ILeaveApplyQueryMaintain service = ServiceLocator
					.lookup(ILeaveApplyQueryMaintain.class);
			checkResult = service.checkWhenSave((AggLeaveVO) aggVO);
		} catch (HrssException e) {
			e.alert();
		}
		return checkResult;
	}

	/**
	 * 显示和当前单据相冲突的单据列表
	 */
	public boolean showSplitInfoList(String primaryKey,
			SplitBillResult<AggLeaveVO> splitResult) {
		// 是否执行了拆单
		boolean isSplit = splitResult.isSplit();
		// 是否时长超限
		boolean isExceedLimit = splitResult.isExceedLimit();
		if (!isSplit && isExceedLimit) {// 如果没有拆单,但时长超限了,则直接用简单对话框提示
			AggLeaveVO aggVO = ((AggLeaveVO[]) splitResult.getSplitResult())[0];
			UFDouble sumHour = aggVO.getHeadVO().getSumhour();
			UFDouble usefulHour = aggVO.getHeadVO().getUsefuldayorhour();
			usefulHour = usefulHour.setScale(getPointNum(),
					UFDouble.ROUND_HALF_UP);
			// 获得休假类别CopyVO
			TimeItemCopyVO leaveTypeCopyVO = getTimeItemCopyVO((AggLeaveVO) aggVO);

			if (leaveTypeCopyVO.getIsLeavelimit() != null
					&& leaveTypeCopyVO.getIsLeavelimit().booleanValue()) {// 控制休假时长时校验
				// 休假总时长大于可用时长
				if (sumHour.compareTo(usefulHour) > 0) {
					String errMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("c_ta-res", "0c_ta-res0073")/*
																	 * @ res
																	 * "休假总时长{0}大于可用时长{1}，不能进行保存操作！"
																	 */;
					// 严格控制 休假时长
					if (leaveTypeCopyVO.getIsRestrictlimit() != null
							&& leaveTypeCopyVO.getIsRestrictlimit()
									.booleanValue()) {
						CommonUtil.showErrorDialog(ResHelper.getString(
								"c_pub-res", "0c_pub-res0169"), MessageFormat
								.format(errMsg, sumHour.toString(),
										usefulHour.toString()));
					}
					errMsg = MessageFormat
							.format(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
									.getStrByID("c_ta-res", "0c_ta-res0074")/*
																			 * @
																			 * res
																			 * "休假总时长{0}大于可用时长{1}，是否继续操作？"
																			 */,
									sumHour.toString(), usefulHour.toString());
					// 不 严格控制 休假时长
					AppInteractionUtil
							.showConfirmDialog(
									DIALOG_SAVE,
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("c_ta-res",
													"0c_ta-res0075")/*
																	 * @ res
																	 * "询问"
																	 */,
									errMsg, null);

				}
			} else {// 不控制休假时长时， 不需要校验
				try {
					ILeaveApplyApproveManageMaintain service = ServiceLocator
							.lookup(ILeaveApplyApproveManageMaintain.class);
					if (StringUtils.isEmpty(primaryKey)) {
						service.insertData(splitResult);
					} else {
						service.updateData(splitResult);
					}
				} catch (HrssException e) {
					new HrssException(e).alert();
				} catch (BusinessException e) {
					new HrssException(e).deal();
				}
			}

			return true;

		} else { // 拆单了
			String title = null;
			if (isSplit && !isExceedLimit) {
				title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						"c_ta-res", "0c_ta-res0076")/*
													 * @ res
													 * "您填写的休假申请单被拆分成下列单据,是否保存?"
													 */;
			} else {
				title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						"c_ta-res", "0c_ta-res0077")/*
													 * @ res
													 * "您填写的休假申请单被拆分成下列单据,且存在超出可用时长的记录(已用黄色标出),是否保存?"
													 */;
			}
			LeaveCommonVO[] splitVOs = null;
			AggLeaveVO[] aggVOs = (AggLeaveVO[]) splitResult.getSplitResult();
			List<LeavebVO> list = new ArrayList<LeavebVO>();
			for (AggLeaveVO aggVO : aggVOs) {
				LeavebVO[] bvos = aggVO.getBodyVOs();
				if (!ArrayUtils.isEmpty(bvos))
					for (LeavebVO bvo : bvos) {
						if (bvo.getStatus() != VOStatus.DELETED)
							list.add(bvo);
					}
			}
			splitVOs = list.toArray(new LeavebVO[0]);
			ApplicationContext appCxt = AppLifeCycleContext.current()
					.getApplicationContext();
			appCxt.addAppAttribute(APP_ATTR_SPLITBILL_DATAS, splitVOs);
			// 设置模态窗体的URL
			String windowId = "SplitResultInfo";
			// 调用公共模块窗体显示方法
			CommonUtil.showWindowDialog(windowId, title, "900", "400", null,
					ApplicationContext.TYPE_DIALOG);
			return false;
		}
	}

	/**
	 * 获得考勤位数
	 * 
	 * @return
	 */
	private int getPointNum() {
		TimeRuleVO timeRuleVO = ShopTaAppContextUtil.getTimeRuleVO();
		if (timeRuleVO == null) {
			// 没有考勤规则的情况，设置默认值
			return ShopTaListBasePageModel.DEFAULT_PRECISION;
		}
		int pointNum = Math.abs(timeRuleVO.getTimedecimal());
		return pointNum;
	}

	/**
	 * 保存操作
	 * 
	 * @param aggVo
	 * @return
	 */
	@Override
	public AggregatedValueObject onVOSave(AggregatedValueObject aggVO) {
		String billPk = (String) AppUtil.getAppAttr("App_newAggVO_PrimaryKey");
		AggregatedValueObject newAggVO = null;
		if (!StringUtils.isEmpty(billPk)) {
			newAggVO = getBillVOByPk(billPk);
			AppUtil.addAppAttr("App_newAggVO_PrimaryKey", null);
		}
		return newAggVO;
	}

	/**
	 * 根据主键,查询VO
	 * 
	 * @param primaryKey
	 * @return
	 */
	protected AggLeaveVO getBillVOByPk(String primaryKey) {
		if (StringUtils.isEmpty(primaryKey)) {
			return null;
		}
		IMDPersistenceQueryService service = MDPersistenceService
				.lookupPersistenceQueryService();
		// 查询单据VO
		AggLeaveVO billVO = null;
		try {
			billVO = (AggLeaveVO) service.queryBillOfVOByPK(AggLeaveVO.class,
					primaryKey, false);
		} catch (MetaDataException e) {
			new HrssException(e).deal();
		}
		return billVO;
	}

	/**
	 * 保存后操作
	 */
	@Override
	public AggregatedValueObject onAfterVOSave(Dataset ds, Dataset[] dsDetails,
			AggregatedValueObject aggVO) {
		// 关闭弹出页面
		CmdInvoker.invoke(new CloseWindowCmd());
//		ApplicationContext applicationContext = getLifeCycleContext()
//				.getApplicationContext();
//		applicationContext.closeWinDialog("ShopLeaveApplytCard");
		// 执行左侧快捷查询
		CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET,
				"closewindow"));
		return null;
	}

	/**
	 * 获得休假类别Copy的VO
	 * 
	 * @param aggVO
	 * @return
	 */
	private TimeItemCopyVO getTimeItemCopyVO(AggLeaveVO aggVO) {
		ITimeItemQueryService itemService = NCLocator.getInstance().lookup(
				ITimeItemQueryService.class);
		TimeItemCopyVO typeVO = null;
		try {
			typeVO = itemService.queryCopyTypesByDefPK(aggVO.getHeadVO()
					.getPk_org(), aggVO.getHeadVO().getPk_leavetype(),
					TimeItemCopyVO.LEAVE_TYPE);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			throw new LfwRuntimeException(e.getMessage(), e);
		}
		return typeVO;
	}

	private AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

}
