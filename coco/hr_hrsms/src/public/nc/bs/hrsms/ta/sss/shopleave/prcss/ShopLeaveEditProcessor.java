package nc.bs.hrsms.ta.sss.shopleave.prcss;

import java.util.Map;

import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrsms.ta.sss.shopleave.ShopLeaveApplyConsts;
import nc.bs.hrsms.ta.sss.shopleave.common.ShopTaAfterDataChange;
import nc.bs.hrsms.ta.sss.shopleave.ctrl.ShopLeaveApplytCardView;
import nc.bs.hrss.ta.utils.TBMPsndocUtil;
import nc.itf.hrss.pub.cmd.prcss.IEditProcessor;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

public class ShopLeaveEditProcessor implements IEditProcessor {

	@Override
	public void onBeforeEdit(Dataset ds) {

	}

	/**
	 * 修改后操作<br>
	 * 
	 * @param ds
	 * @param row
	 * @throws Exception
	 */
	@Override
	public void onAfterEdit(Dataset ds) {
		Row masterRow = ds.getSelectedRow();
		if (masterRow == null) {
			return;
		}
		// 片段
		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		// 设置考勤单位
		UFBoolean islactation = (UFBoolean) masterRow.getValue(ds.nameToIndex(LeavehVO.ISLACTATION));
		if(islactation == null || !islactation.booleanValue()){
			TimeItemCopyVO timeItemCopyVO = ShopLeaveApplytCardView.setTimeUnitText(ds, masterRow);
			// 设置年度和期间是否可编辑
			setYearMonthEnable(masterRow.getString(ds.nameToIndex(LeavehVO.PK_ORG)), viewMain, timeItemCopyVO.getLeavesetperiod());
			// 设置年度和期间的ComboData
			ShopTaAfterDataChange.setYearMonthComboData(viewMain);
			// 计算已休时长 |享有时长 |结余时长
			setLeaveDayOrHour(ds, masterRow, timeItemCopyVO.getLeavesetperiod());
		}
		
	}

	/**
	 * 根据人员主键,休假类别, 年度, 期间,<br/>
	 * 计算|指定考勤期间内|申请人员|选择休假类别|的 已休时长 |享有时长 |结余时长.
	 * 
	 * @param ds
	 * @param selRow
	 * 
	 */
	private void setLeaveDayOrHour(Dataset ds, Row selRow, Integer leavesetperiod) {
		SuperVO[] superVOs = new Dataset2SuperVOSerializer<SuperVO>().serialize(ds, selRow);
		if (superVOs == null || superVOs.length == 0) {
			setDefaultLeaveDayOrHour(ds, selRow);
			return;
		}
		String leaveYear = selRow.getString(ds.nameToIndex(LeavehVO.LEAVEYEAR));
		//String leaveMonth = selRow.getString(ds.nameToIndex(LeavehVO.LEAVEMONTH));
		
		//if (StringUtils.isEmpty(leaveYear) || StringUtils.isEmpty(leaveMonth)) {
		if (StringUtils.isEmpty(leaveYear)) {
			setDefaultLeaveDayOrHour(ds, selRow);
			return;
		}
		LeavehVO leavehVO = (LeavehVO) superVOs[0];
		// 计算享有时间,已休时间,结余时间
		LeaveBalanceVO leaveBalanceVO = getLeaveBalanceVO(leavehVO, isYear(leavesetperiod));
		if (leaveBalanceVO == null) {
			setDefaultLeaveDayOrHour(ds, selRow);
			return;
		}
		// 设置享有时间
		selRow.setValue(ds.nameToIndex(LeavehVO.REALDAYORHOUR), leaveBalanceVO.getCurdayorhour());
		// 已休时间
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTEDDAYORHOUR), leaveBalanceVO.getYidayorhour());
		// 结余时间
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTDAYORHOUR), leaveBalanceVO.getRestdayorhour());
		// 假期结算顺序号
		selRow.setValue(ds.nameToIndex(LeavehVO.LEAVEINDEX), leavehVO.getLeaveindex() == null ? 1 : leavehVO.getLeaveindex());
	}

	/**
	 * 设置 已休时长 |享有时长 |结余时长的默认值.
	 * 
	 * @param ds
	 * @param selRow
	 */
	private void setDefaultLeaveDayOrHour(Dataset ds, Row selRow) {
		// 设置享有时间
		selRow.setValue(ds.nameToIndex(LeavehVO.REALDAYORHOUR), UFDouble.ZERO_DBL);
		// 已休时间
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTEDDAYORHOUR), UFDouble.ZERO_DBL);
		// 结余时间
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTDAYORHOUR), UFDouble.ZERO_DBL);
		// 假期结算顺序号
		selRow.setValue(ds.nameToIndex(LeavehVO.LEAVEINDEX), 1);
	}

	/**
	 * 计算享有时间,已休时间,结余时间
	 * 
	 * @param leavehVO
	 * @return
	 */
	private LeaveBalanceVO getLeaveBalanceVO(LeavehVO leavehVO, boolean isYear) {
		Map<String, LeaveBalanceVO> balanceMap = null;
		try {
			ILeaveBalanceManageService LeaveBalanceServ = ServiceLocator.lookup(ILeaveBalanceManageService.class);
			balanceMap = LeaveBalanceServ.queryAndCalLeaveBalanceVO(leavehVO.getPk_org(), leavehVO);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return MapUtils.isEmpty(balanceMap) ? null : balanceMap.get(leavehVO.getPk_psnorg() + leavehVO.getPk_leavetype() + leavehVO.getLeaveyear() + (isYear ? "" : leavehVO.getLeavemonth()));
	}

	private boolean isYear(Integer leavesetperiod) {
		return leavesetperiod != null && leavesetperiod != TimeItemCopyVO.LEAVESETPERIOD_MONTH;
	}

	/**
	 * 设置年度和期间是否可编辑<br/>
	 * 1.启用往期假结余优先时,强制要求按照往期假优先休假原则自动分单,故申请页面年度和期间不可编辑.<br/>
	 * 2.未启用往期假结余优先时,年度可编辑; <br/>
	 * | 根据组织主键,修改类别查询修改类别Copy的结算方式<br/>
	 * | 休假类别Copy的结算方式-按月结算时,期间可编辑;其他期间不可编辑<br/>
	 * 
	 * @param pk_org
	 * @param viewMain
	 */
	private void setYearMonthEnable(String pk_org, LfwView viewMain, Integer leavesetperiod) {
		FormComp formComp = (FormComp) viewMain.getViewComponents().getComponent(ShopLeaveApplyConsts.PAGE_FORM_LEAVEINFO);
		if (formComp == null) {
			return;
		}
		// 单据所在组织的考勤规则
		TimeRuleVO timeRuleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_org);
		FormElement yearElem = formComp.getElementById(LeavehVO.LEAVEYEAR);
		FormElement monthElem = formComp.getElementById(LeavehVO.LEAVEMONTH);
		// 往期假结余优先
		if (timeRuleVO.isPreHolidayFirst()) {
			if (yearElem != null) {
				yearElem.setEnabled(false);
			}
			if (monthElem != null) {
				monthElem.setEnabled(false);
			}
		} else {
			if (yearElem != null) {
				yearElem.setEnabled(true);
			}
			if (monthElem != null) {
				if (leavesetperiod != null && leavesetperiod == TimeItemCopyVO.LEAVESETPERIOD_MONTH) {
					monthElem.setEnabled(true);
				} else {
					monthElem.setEnabled(false);
				}
			}
		}

	}

	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}


}
