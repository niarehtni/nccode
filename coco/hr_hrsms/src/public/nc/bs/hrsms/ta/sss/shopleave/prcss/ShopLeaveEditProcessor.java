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
	 * �޸ĺ����<br>
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
		// Ƭ��
		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		// ���ÿ��ڵ�λ
		UFBoolean islactation = (UFBoolean) masterRow.getValue(ds.nameToIndex(LeavehVO.ISLACTATION));
		if(islactation == null || !islactation.booleanValue()){
			TimeItemCopyVO timeItemCopyVO = ShopLeaveApplytCardView.setTimeUnitText(ds, masterRow);
			// ������Ⱥ��ڼ��Ƿ�ɱ༭
			setYearMonthEnable(masterRow.getString(ds.nameToIndex(LeavehVO.PK_ORG)), viewMain, timeItemCopyVO.getLeavesetperiod());
			// ������Ⱥ��ڼ��ComboData
			ShopTaAfterDataChange.setYearMonthComboData(viewMain);
			// ��������ʱ�� |����ʱ�� |����ʱ��
			setLeaveDayOrHour(ds, masterRow, timeItemCopyVO.getLeavesetperiod());
		}
		
	}

	/**
	 * ������Ա����,�ݼ����, ���, �ڼ�,<br/>
	 * ����|ָ�������ڼ���|������Ա|ѡ���ݼ����|�� ����ʱ�� |����ʱ�� |����ʱ��.
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
		// ��������ʱ��,����ʱ��,����ʱ��
		LeaveBalanceVO leaveBalanceVO = getLeaveBalanceVO(leavehVO, isYear(leavesetperiod));
		if (leaveBalanceVO == null) {
			setDefaultLeaveDayOrHour(ds, selRow);
			return;
		}
		// ��������ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.REALDAYORHOUR), leaveBalanceVO.getCurdayorhour());
		// ����ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTEDDAYORHOUR), leaveBalanceVO.getYidayorhour());
		// ����ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTDAYORHOUR), leaveBalanceVO.getRestdayorhour());
		// ���ڽ���˳���
		selRow.setValue(ds.nameToIndex(LeavehVO.LEAVEINDEX), leavehVO.getLeaveindex() == null ? 1 : leavehVO.getLeaveindex());
	}

	/**
	 * ���� ����ʱ�� |����ʱ�� |����ʱ����Ĭ��ֵ.
	 * 
	 * @param ds
	 * @param selRow
	 */
	private void setDefaultLeaveDayOrHour(Dataset ds, Row selRow) {
		// ��������ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.REALDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTEDDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		selRow.setValue(ds.nameToIndex(LeavehVO.RESTDAYORHOUR), UFDouble.ZERO_DBL);
		// ���ڽ���˳���
		selRow.setValue(ds.nameToIndex(LeavehVO.LEAVEINDEX), 1);
	}

	/**
	 * ��������ʱ��,����ʱ��,����ʱ��
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
	 * ������Ⱥ��ڼ��Ƿ�ɱ༭<br/>
	 * 1.�������ڼٽ�������ʱ,ǿ��Ҫ�������ڼ������ݼ�ԭ���Զ��ֵ�,������ҳ����Ⱥ��ڼ䲻�ɱ༭.<br/>
	 * 2.δ�������ڼٽ�������ʱ,��ȿɱ༭; <br/>
	 * | ������֯����,�޸�����ѯ�޸����Copy�Ľ��㷽ʽ<br/>
	 * | �ݼ����Copy�Ľ��㷽ʽ-���½���ʱ,�ڼ�ɱ༭;�����ڼ䲻�ɱ༭<br/>
	 * 
	 * @param pk_org
	 * @param viewMain
	 */
	private void setYearMonthEnable(String pk_org, LfwView viewMain, Integer leavesetperiod) {
		FormComp formComp = (FormComp) viewMain.getViewComponents().getComponent(ShopLeaveApplyConsts.PAGE_FORM_LEAVEINFO);
		if (formComp == null) {
			return;
		}
		// ����������֯�Ŀ��ڹ���
		TimeRuleVO timeRuleVO = TBMPsndocUtil.getTimeRuleVOByOrg(pk_org);
		FormElement yearElem = formComp.getElementById(LeavehVO.LEAVEYEAR);
		FormElement monthElem = formComp.getElementById(LeavehVO.LEAVEMONTH);
		// ���ڼٽ�������
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
