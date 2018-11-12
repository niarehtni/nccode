package nc.bs.hrsms.ta.sss.shopleave.lsnr;

import java.text.MessageFormat;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.Logger;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaListBasePageModel;
import nc.hr.utils.ResHelper;
import nc.itf.hrss.pub.cmd.prcss.ICommitProcessor;
import nc.itf.ta.ITimeItemQueryService;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

public class ShopLeaveCommitProcessor implements ICommitProcessor{

	// 休假申请保存时的确认对话框的Id
		private String DIALOG_COMMIT = "dlg_Leave_commit";
		
		@Override
		public boolean checkBeforeCommit(AggregatedValueObject aggVO)
				throws Exception {
			Boolean result = AppInteractionUtil.getConfirmDialogResult(DIALOG_COMMIT);
			if (result != null && Boolean.FALSE.equals(result)) {
				return false;
			}
			
			UFDouble sumHour = ((AggLeaveVO) aggVO).getHeadVO().getSumhour();
			UFDouble usefulHour = ((AggLeaveVO) aggVO).getHeadVO().getUsefuldayorhour();
			sumHour = sumHour.setScale(getPointNum(),UFDouble.ROUND_HALF_UP);
			usefulHour = usefulHour.setScale(getPointNum(),UFDouble.ROUND_HALF_UP);
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
									DIALOG_COMMIT,
									nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
											.getStrByID("c_ta-res",
													"0c_ta-res0075")/*
																	 * @ res
																	 * "询问"
																	 */,
									errMsg, null);

				}
			}

			return true;

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
}
