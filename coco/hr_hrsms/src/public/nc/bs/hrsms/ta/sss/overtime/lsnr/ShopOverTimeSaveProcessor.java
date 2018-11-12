package nc.bs.hrsms.ta.sss.overtime.lsnr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.CloseWindowCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.ta.away.lsnr.AwaySaveProcessor;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.ta.IOvertimeApplyApproveManageMaintain;
import nc.itf.ta.IOvertimeApplyQueryMaintain;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.cmd.UifPlugoutCmd;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.ta.bill.BillMutexException;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.overtime.pf.validator.PFSaveOvertimeValidator;

import org.apache.commons.lang.StringUtils;

public class ShopOverTimeSaveProcessor implements ISaveProcessor {

	// 加班申请保存时的确认对话框的Id
		private final String DIALOG_SAVE = "dlg_overtime_save";
		private final String DIALOG_SAVE_1 = "dlg_overtime_save_1";
		// 加班单没有选中加班校验标识提示校验对话框ID
		private final String DIALOG_CHECK_ISNEED = "dlg_overtime_isneed";

		/**
		 * 保存前操作
		 * 
		 * @param aggVo
		 * @return
		 */
		@Override
		public void onBeforeVOSave(AggregatedValueObject aggVO) throws Exception {

		}

		/**
		 * 保存校验操作
		 */
		@Override
		public boolean checkBeforeVOSave(AggregatedValueObject aggVO) throws Exception {
			// 返回值1
			Boolean result = AppInteractionUtil.getConfirmDialogResult(DIALOG_SAVE);
			if (result != null && Boolean.FALSE.equals(result)) {
				return false;
			}
			
			Boolean resultIsNeedCheck = AppInteractionUtil.getConfirmDialogResult(DIALOG_CHECK_ISNEED);
			if (resultIsNeedCheck != null && Boolean.FALSE.equals(resultIsNeedCheck)) {
				return false;
			}

			// 获取子表信息
			OvertimehVO headVO = (OvertimehVO) aggVO.getParentVO();
			//考勤档案已经结束的人员新增档案结束日期前的数据时pk_psnorg字段为空，无法保存数据
			PsnJobVO psnjobVO = null;
			try {
				psnjobVO = (PsnJobVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(PsnJobVO.class, headVO.getPk_psnjob(), null);
			} catch (HrssException e1) {
				throw new Exception(e1);
			}
			headVO.setPk_psnorg(psnjobVO.getPk_psnorg());
			/* 新增保存时提示,修改保存不提示 */
			Boolean resultHoliday = Boolean.FALSE;
			// 返回值2
			resultHoliday = AppInteractionUtil.getConfirmDialogResult(DIALOG_SAVE_1);
			if (resultHoliday != null && Boolean.FALSE.equals(resultHoliday)) {
				return false;
			}
			OvertimebVO[] vos = (OvertimebVO[]) aggVO.getChildrenVO();
			List<OvertimebVO> subVOs = new ArrayList<OvertimebVO>();
			for (OvertimebVO subVO : vos) {
				subVO.setPk_group(headVO.getPk_group());
				// 设置单据所属组织主键
				subVO.setPk_org(headVO.getPk_org());
				// 设置人员主键
				subVO.setPk_psndoc(headVO.getPk_psndoc());
				// 人员任职主键
				subVO.setPk_psnjob(headVO.getPk_psnjob());
				// 组织关系主键
				subVO.setPk_psnorg(headVO.getPk_psnorg());
				// 加班类别
				subVO.setPk_overtimetype(headVO.getPk_overtimetype());
				// 加班类别Copy
				subVO.setPk_overtimetypecopy(headVO.getPk_overtimetypecopy());
				subVOs.add(subVO);
			}
			Integer state = headVO.getApprove_state();
			if (IPfRetCheckInfo.NOSTATE != state) {
				return true;
			}

			IOvertimeApplyQueryMaintain service = null;
			try {
				service = ServiceLocator.lookup(IOvertimeApplyQueryMaintain.class);
			} catch (HrssException e) {
				e.alert();
			}
			// 获得是否继续标志
			String confirmFlag = getLifeCycleContext().getParameter("isContinue");
			if (StringUtils.isEmpty(confirmFlag)) {
				if (!Boolean.TRUE.equals(result)) {
					// 保存前的验证
					ValidationFailure failur = new PFSaveOvertimeValidator().validate(aggVO);
					if (failur != null) {
						CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0169"), failur.getMessage());
					}
					// 校验考勤规则考勤期间内加班时长设置
					String errorMsg = service.checkOvertimeLength(headVO.getPk_org(), subVOs.toArray(new OvertimebVO[0]));

					if (!StringUtils.isEmpty(errorMsg)) {
						// 不严格控制 加班小时数
						AppInteractionUtil.showConfirmDialog(DIALOG_SAVE, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0075")/*
																																							 * @
																																							 * res
																																							 * "询问"
																																							 */, errorMsg, null);
						if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_SAVE)) {
							return false;
						}
					}

				}
				AggOvertimeVO aggOvertimeVO = (AggOvertimeVO) aggVO;
				OvertimehVO mainvo = aggOvertimeVO.getOvertimehVO();
				if (!Boolean.TRUE.equals(resultIsNeedCheck)) {
					// 加班单没有选中加班校验标识提示校验
					IOvertimeApplyQueryMaintain sevice = NCLocator.getInstance().lookup(IOvertimeApplyQueryMaintain.class);
					String checkIsNeed = sevice.checkIsNeed(mainvo.getPk_org(), subVOs.toArray(new OvertimebVO[0]));
					if (!StringUtils.isEmpty(checkIsNeed)) {
						AppInteractionUtil.showConfirmDialog(DIALOG_CHECK_ISNEED, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0075")/*
																																									 * @
																																									 * res
																																									 * "询问"
																																									 */, checkIsNeed, null);
						if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_CHECK_ISNEED)) {
							return false;
						}
					}
				}
				if (IPfRetCheckInfo.NOSTATE == state && !Boolean.TRUE.equals(resultHoliday)) {
					// 含假日的加班类型和加班规则定义的加班类型的一致性校验
					Map<String, String[]> holidayMap = service.checkOverTimeHoliday(headVO.getPk_org(), subVOs.toArray(new OvertimebVO[0]));
					if (holidayMap != null && holidayMap.size() > 0) {
						String psnNames = holidayMap.keySet().toArray(new String[0])[0];
						String errorMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("6017overtime", "06017overtime0053"/*
																																	 * @
																																	 * res
																																	 * "以下人员加班类别与假日加班规则不一致："
																																	 */) + psnNames;

						if (!StringUtils.isEmpty(errorMsg)) {
							AppInteractionUtil.showConfirmDialog(DIALOG_SAVE_1, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0075")/*
																																								 * @
																																								 * res
																																								 * "询问"
																																								 */, errorMsg, null);
							if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_SAVE_1)) {
								return false;
							}

						}
					}

				}

				// 保存前先校验一次，如果有单据冲突，但是是被允许的，则显示这些冲突单据，并询问用户是否保存
				Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> checkResult = null;
				try {
					checkResult = service.check((AggOvertimeVO) aggVO);
				} catch (BillMutexException e) {
					AwaySaveProcessor.showConflictInfoList((e), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0007")/*
																																				 * @
																																				 * res
																																				 * "与下列单据有时间冲突，操作不能继续"
																																				 */, ShopTaApplyConsts.DIALOG_ALERT);
					throw new BillMutexException();
				}
				if (checkResult != null) {
					AwaySaveProcessor.showConflictInfoList(new BillMutexException(null, checkResult), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0008")/*
																																														 * @
																																														 * res
																																														 * "与下列单据有时间冲突，是否保存?"
																																														 */, ShopTaApplyConsts.DIALOG_CONFIRM);
					throw new BillMutexException();
				}
			}
			return true;
		}

		/**
		 * 保存操作
		 * 
		 * @param aggVo
		 * @return
		 */
		@Override
		public AggregatedValueObject onVOSave(AggregatedValueObject aggVO) throws Exception {

			AggOvertimeVO newAggVo = null;
			AggOvertimeVO aggOverTimeVO = (AggOvertimeVO) aggVO;
			String primaryKey = aggOverTimeVO.getOvertimehVO().getPrimaryKey();

			try {
				IOvertimeApplyApproveManageMaintain service = ServiceLocator.lookup(IOvertimeApplyApproveManageMaintain.class);
				if (StringUtils.isEmpty(primaryKey)) {
					newAggVo = service.insertData(aggOverTimeVO);
				} else {
					newAggVo = service.updateData(aggOverTimeVO);
				}
			} catch (HrssException e) {
				new HrssException(e).alert();
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}

			return newAggVo;
		}

		/**
		 * 保存后操作
		 */
		@Override
		public AggregatedValueObject onAfterVOSave(Dataset ds, Dataset[] dsDetails, AggregatedValueObject aggVO) throws Exception {
			// 关闭弹出页面
			CmdInvoker.invoke(new CloseWindowCmd());
			ApplicationContext applicationContext = getLifeCycleContext().getApplicationContext();
			applicationContext.closeWinDialog("OverTimeApply");
			// 执行左侧快捷查询
			CmdInvoker.invoke(new UifPlugoutCmd(HrssConsts.PAGE_MAIN_WIDGET, "closewindow"));
			return null;
		}

		private AppLifeCycleContext getLifeCycleContext() {
			return AppLifeCycleContext.current();
		}

}
