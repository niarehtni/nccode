package nc.bs.hrsms.ta.sss.overtime.lsnr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.utils.ResHelper;
import nc.itf.hrss.pub.cmd.prcss.ICommitProcessor;
import nc.itf.ta.IOvertimeApplyQueryMaintain;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.overtime.pf.validator.PFSaveOvertimeValidator;

import org.apache.commons.lang.StringUtils;

public class ShopOverTimeCommitProcessor implements ICommitProcessor {

	// 加班申请保存时的确认对话框的Id
	private final String DIALOG_COMMIT = "dlg_overtime_commit";
	private final String DIALOG_COMMIT_1 = "dlg_overtime_commit_1";
	// 加班单没有选中加班校验标识提示校验对话框ID
	private final String DIALOG_CHECK_ISNEED = "dlg_overtime_isneed";

	/**
	 * 提交前的校验
	 */
	@Override
	public boolean checkBeforeCommit(AggregatedValueObject aggVO) throws Exception {
		// // 校验加班时长,返回校验信息
		// String errorMsg = checkOverTimeLen(aggVO);
		// if (!StringUtils.isEmpty(errorMsg)) {
		// AppInteractionUtil.showConfirmDialog(DIALOG_COMMIT,
		// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0075")/*@res
		// "询问"*/, errorMsg, null);
		// if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_COMMIT)) {
		// return false;
		// }
		// }
		//
		// IOvertimeApplyQueryMaintain service = null;
		// try {
		// service = ServiceLocator.lookup(IOvertimeApplyQueryMaintain.class);
		// } catch (HrssException e) {
		// e.alert();
		// }
		//
		// // 获取子表信息
		// OvertimehVO headVO = (OvertimehVO) aggVO.getParentVO();
		// OvertimebVO[] vos = (OvertimebVO[]) aggVO.getChildrenVO();
		// List<OvertimebVO> subVOs = new ArrayList<OvertimebVO>();
		// for (OvertimebVO subVO : vos) {
		// subVO.setPk_group(headVO.getPk_group());
		// // 设置单据所属组织主键
		// subVO.setPk_org(headVO.getPk_org());
		// // 设置人员主键
		// subVO.setPk_psndoc(headVO.getPk_psndoc());
		// // 人员任职主键
		// subVO.setPk_psnjob(headVO.getPk_psnjob());
		// // 组织关系主键
		// subVO.setPk_psnorg(headVO.getPk_psnorg());
		// // 加班类别
		// subVO.setPk_overtimetype(headVO.getPk_overtimetype());
		// // 加班类别Copy
		// subVO.setPk_overtimetypecopy(headVO.getPk_overtimetypecopy());
		// subVOs.add(subVO);
		// }
		// // 含假日的加班类型和加班规则定义的加班类型的一致性校验
		// Map<String, String[]> holidayMap =
		// service.checkOverTimeHoliday(headVO.getPk_org(), subVOs.toArray(new
		// OvertimebVO[0]));
		// if (holidayMap != null && holidayMap.size() > 0) {
		// String psnNames = holidayMap.keySet().toArray(new String[0])[0];
		// errorMsg =
		// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("6017overtime",
		// "06017overtime0053"/*
		// * @
		// * res
		// * "以下人员加班类别与假日加班规则不一致："
		// */) + psnNames;
		//
		// if (!StringUtils.isEmpty(errorMsg)) {
		// AppInteractionUtil.showConfirmDialog(DIALOG_COMMIT_1,
		// nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res",
		// "0c_ta-res0075")/*
		// * @
		// * res
		// * "询问"
		// */, errorMsg, null);
		// if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_COMMIT_1)) {
		// return false;
		// }
		//
		// }
		// }
		// return true;

		// 返回值1
		Boolean result = AppInteractionUtil.getConfirmDialogResult(DIALOG_COMMIT);
		if (result != null && Boolean.FALSE.equals(result)) {
			return false;
		}

		Boolean resultIsNeedCheck = AppInteractionUtil.getConfirmDialogResult(DIALOG_CHECK_ISNEED);
		if (resultIsNeedCheck != null && Boolean.FALSE.equals(resultIsNeedCheck)) {
			return false;
		}

		// 获取子表信息
		OvertimehVO headVO = (OvertimehVO) aggVO.getParentVO();

		/* 新增保存时提示,修改保存不提示 */
		Boolean resultHoliday = Boolean.FALSE;
		// 返回值2
		resultHoliday = AppInteractionUtil.getConfirmDialogResult(DIALOG_COMMIT_1);
		if (resultHoliday != null && Boolean.FALSE.equals(resultHoliday)) {
			return false;
		}
		OvertimebVO[] vos = (OvertimebVO[]) aggVO.getChildrenVO();
		List<OvertimebVO> subListVOs = new ArrayList<OvertimebVO>();
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
			subListVOs.add(subVO);
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
				String errorMsg = service.checkOvertimeLength(headVO.getPk_org(),
						subListVOs.toArray(new OvertimebVO[0]));

				if (!StringUtils.isEmpty(errorMsg)) {
					// 不严格控制 加班小时数
					AppInteractionUtil.showConfirmDialog(DIALOG_COMMIT, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("c_ta-res", "0c_ta-res0075")/*
																	 * @ res
																	 * "询问"
																	 */, errorMsg, null);
					if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_COMMIT)) {
						return false;
					}
				}
			}
			AggOvertimeVO aggOvertimeVO = (AggOvertimeVO) aggVO;
			OvertimehVO mainvo = aggOvertimeVO.getOvertimehVO();
			if (!Boolean.TRUE.equals(resultIsNeedCheck)) {
				// 加班单没有选中加班校验标识提示校验
				IOvertimeApplyQueryMaintain sevice = NCLocator.getInstance().lookup(IOvertimeApplyQueryMaintain.class);
				String checkIsNeed = sevice.checkIsNeed(mainvo.getPk_org(), subListVOs.toArray(new OvertimebVO[0]));
				if (!StringUtils.isEmpty(checkIsNeed)) {
					AppInteractionUtil.showConfirmDialog(DIALOG_CHECK_ISNEED, nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0075")/*
																					 * @
																					 * res
																					 * "询问"
																					 */, checkIsNeed, null);
					if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_CHECK_ISNEED)) {
						return false;
					}
				}
			}
			if (!Boolean.TRUE.equals(resultHoliday)) {
				// 含假日的加班类型和加班规则定义的加班类型的一致性校验
				Map<String, String[]> holidayMap = service.checkOverTimeHoliday(headVO.getPk_org(),
						subListVOs.toArray(new OvertimebVO[0]));
				if (holidayMap != null && holidayMap.size() > 0) {
					String psnNames = holidayMap.keySet().toArray(new String[0])[0];
					String errorMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("6017overtime",
							"06017overtime0053"/*
												 * @ res "以下人员加班类别与假日加班规则不一致："
												 */)
							+ psnNames;

					if (!StringUtils.isEmpty(errorMsg)) {
						AppInteractionUtil.showConfirmDialog(DIALOG_COMMIT_1, nc.vo.ml.NCLangRes4VoTransl
								.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0075")/*
																						 * @
																						 * res
																						 * "询问"
																						 */, errorMsg, null);
						if (!AppInteractionUtil.getConfirmDialogResult(DIALOG_COMMIT_1)) {
							return false;
						}

					}
				}

			}
		}
		return true;
	}

	/**
	 * 校验加班时长,返回校验信息
	 * 
	 * @param aggVO
	 * @return
	 */
	public static String checkOverTimeLen(AggregatedValueObject aggVO) {
		AggOvertimeVO aggOvertimeVO = (AggOvertimeVO) aggVO;
		OvertimehVO mainvo = aggOvertimeVO.getOvertimehVO();
		OvertimebVO[] subVOs = aggOvertimeVO.getOvertimebVOs();
		for (OvertimebVO subVO : subVOs) {
			subVO.setPk_psndoc(mainvo.getPk_psndoc());
			subVO.setPk_psnjob(mainvo.getPk_psnjob());
			subVO.setPk_psnorg(mainvo.getPk_psnorg());
			subVO.setPk_org(mainvo.getPk_org());
			subVO.setPk_group(mainvo.getPk_group());
			subVO.setPk_overtimetypecopy(mainvo.getPk_overtimetypecopy());
			subVO.setPk_overtimetype(mainvo.getPk_overtimetype());
		}
		IOvertimeApplyQueryMaintain service = null;
		try {
			service = ServiceLocator.lookup(IOvertimeApplyQueryMaintain.class);
		} catch (HrssException e) {
			e.alert();
		}
		// 校验加班时长
		String errorMsg = null;
		try {
			errorMsg = service.checkOvertimeLength(mainvo.getPk_org(), subVOs);
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return errorMsg;
	}

	private AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

}
