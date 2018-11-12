package nc.bs.hrsms.ta.empleavereg4store;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.shopleave.ShopLeaveApplyConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.ta.common.pagemode.TaListBasePageMode;
import nc.bs.hrss.ta.utils.ComboDataUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.hrss.ta.timeapply.IQueryOrgOrDeptVid;
import nc.uap.lfw.core.combodata.ComboData;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.MdDataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timerule.TimeRuleVO;

public class EmpLeaveRegDataChange {

	/**
	 * 
	 */
	public static void onAfterDataChange(Dataset ds, Row row) {
		TBMPsndocVO tbmPsndocVO = EmpLeaveRegUtil.getTBMPsndocVO();
		if (tbmPsndocVO == null) {
			// throw new LfwRuntimeException("����û�����ÿ��ڵ��������ܽ�������������");
			CommonUtil.showMessageDialog("��ǰ��Ա�Ŀ��ڵ����Ѿ�������ֻ��������������ǰ�����ݣ�");
			return;
		}
		String pk_psnjob = tbmPsndocVO.getPk_psnjob();
		String pk_psnorg = tbmPsndocVO.getPk_psnorg();

		// ��Ա��ְ����
		row.setValue(ds.nameToIndex("pk_psnjob"), pk_psnjob);
		// ��Ա��֯��ϵ���
		row.setValue(ds.nameToIndex("pk_psnorg"), pk_psnorg);

		List<String> list = getVersionIds(pk_psnjob);
		if (list != null && list.size() > 0) {
			// ��Ա��ְ����ҵ��Ԫ�İ汾id
			row.setValue(ds.nameToIndex("pk_org_v"), list.get(0));
			// ��Ա��ְ�������ŵİ汾pk_dept_v
			row.setValue(ds.nameToIndex("pk_dept_v"), list.get(1));
		}

		// ���ڹ���
		TimeRuleVO timeRuleVO = EmpLeaveRegUtil.getTimeRuleVO();
		// ���ڼٽ�������
		if (timeRuleVO.isPreHolidayFirst()) {
			// ���
			row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEYEAR), String.valueOf(new UFLiteralDate().getYear()));
			// �ڼ�
			row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEMONTH), null);
		} else {
			PeriodVO latestPeriodVO = EmpLeaveRegUtil.getLatestPeriodVO();
			if (latestPeriodVO != null) {// ����ʱ��Ⱥ��ڼ�Ĭ��ѡ��ǰ�����ڼ�
				// ���
				row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEYEAR), latestPeriodVO.getTimeyear());
				// �ڼ�
				row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEMONTH), latestPeriodVO.getTimemonth());
			}
		}
		// �ݼ���ʱ��
		row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		row.setValue(ds.nameToIndex(LeaveRegVO.RESTEDDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		row.setValue(ds.nameToIndex(LeaveRegVO.REALDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		row.setValue(ds.nameToIndex(LeaveRegVO.FREEZEDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		row.setValue(ds.nameToIndex(LeaveRegVO.USEFULDAYORHOUR), UFDouble.ZERO_DBL);
		// ����ʱ��
		row.setValue(ds.nameToIndex(LeaveRegVO.RESTDAYORHOUR), UFDouble.ZERO_DBL);
		// ���ղ���ʱ��Сʱ
		row.setValue(ds.nameToIndex(LeaveRegVO.LACTATIONHOUR), UFDouble.ZERO_DBL);
		// ���ڽ���˳���
		row.setValue(ds.nameToIndex(LeaveRegVO.LEAVEINDEX), Integer.valueOf(1));
		// �Ƿ���ٱ�ʶ
		row.setValue(ds.nameToIndex(LeaveRegVO.ISLACTATION), UFBoolean.FALSE);

		setPageDisp();
		// ���ݿ��ڹ������ÿ������ݵ�Сʱλ��
		LfwView viewMain = AppLifeCycleContext.current().getViewContext().getView();
		setTimeDatasPrecision(viewMain);
	}

	/**
	 * �����Ա��ְ����ҵ��Ԫ/���ŵİ汾id
	 * 
	 * @param pk_psnjob
	 * @return
	 */
	private static List<String> getVersionIds(String pk_psnjob) {
		List<String> list = null;
		IQueryOrgOrDeptVid service;
		try {
			service = ServiceLocator.lookup(IQueryOrgOrDeptVid.class);
			list = service.getOrgOrDeptVidByPsnjob(pk_psnjob);
		} catch (HrssException ex) {
			ex.alert();
		} catch (BusinessException ex) {
			new HrssException(ex).deal();
		}
		return list;
	}

	private static void setPageDisp() {
		LfwView viewMain = AppLifeCycleContext.current().getViewContext().getView();
		// ������Ⱥ��ڼ��Ƿ�ɱ༭
		setYearMonthEnable(viewMain);
		// ������Ⱥ��ڼ��ComboData
		setYearMonthComboData(viewMain);
	}

	/**
	 * ������Ⱥ��ڼ��Ƿ�ɱ༭<br/>
	 * 1.�������ڼٽ�������,ǿ��Ҫ�������ڼ������ݼ�ԭ���Զ��ֵ�,������ҳ����Ⱥ��ڼ䲻������.<br/>
	 * 2.���������ڼٽ�������,��ѡ����Ⱥ��ڼ�,�������ڼ������ݼ�ԭ���Զ��ֵ�����.<br/>
	 * 3.���������ڼٽ�������,ѡ����Ⱥ��ڼ�,���յ�ǰ�ڼ������ݼ�ԭ���Զ��ֵ�����.<br/>
	 * ���������ڼٽ�������,��ǿ��Ҫ�������ڼ������ݼ�ԭ���Զ��ֵ�,������ҳ����Ⱥ��ڼ������.<br/>
	 * 
	 * @param latestPeriodVO
	 */
	public static void setYearMonthEnable(LfwView viewMain) {

		FormComp formComp = (FormComp) viewMain.getViewComponents().getComponent(
				ShopLeaveApplyConsts.PAGE_FORM_LEAVEINFO);
		if (formComp == null) {
			return;
		}
		// ���ڹ���
		TimeRuleVO timeRuleVO = ShopTaAppContextUtil.getTimeRuleVO();
		FormElement yearElem = formComp.getElementById(LeavehVO.LEAVEYEAR);
		if (yearElem != null) {
			// ���ڼٽ�������
			if (timeRuleVO.isPreHolidayFirst()) {
				yearElem.setEnabled(false);
			} else {
				yearElem.setEnabled(true);
			}
		}
		FormElement monthElem = formComp.getElementById(LeavehVO.LEAVEMONTH);
		if (monthElem != null) {
			// ���ڼٽ�������
			if (timeRuleVO.isPreHolidayFirst()) {
				monthElem.setEnabled(false);
			} else {
				monthElem.setEnabled(true);
			}
		}

	}

	/**
	 * ������Ⱥ��ڼ��ComboData
	 * 
	 * @param latestPeriodVO
	 */
	public static void setYearMonthComboData(LfwView viewMain) {
		// ������Ⱥ��ڼ���������ݼ�
		ComboData yearData = viewMain.getViewModels().getComboData(ShopLeaveApplyConsts.WIDGET_COMBODATA_YEAR);
		ComboData monthData = viewMain.getViewModels().getComboData(ShopLeaveApplyConsts.WIDGET_COMBODATA_MONTH);
		// ��Ա���ڵ���
		TBMPsndocVO tbmPsndoc = ShopTaAppContextUtil.getTBMPsndocVO();
		if (tbmPsndoc == null) {// ���ڵ���Ϊ�յ����,���ܽ�������
			return;
		}
		Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(tbmPsndoc.getPk_org());
		if (periodMap == null || periodMap.size() == 0) {
			return;
		}
		String[] years = periodMap.keySet().toArray(new String[0]);
		if (years == null || years.length == 0) {
			return;
		}
		if (years.length > 1) {
			Arrays.sort(years);
			Collections.reverse(Arrays.asList(years));
		}
		ComboDataUtil.addCombItemsAfterClean(yearData, years);

		PeriodVO latestPeriodVO = ShopTaAppContextUtil.getLatestPeriodVO();
		if (latestPeriodVO == null) {
			return;
		}
		// ѡ�����
		if (!periodMap.keySet().contains(latestPeriodVO.getTimeyear())) {
			return;
		}
		String[] months = periodMap.get(latestPeriodVO.getTimeyear());
		if (months == null || months.length == 0) {
			return;
		}
		ComboDataUtil.addCombItemsAfterClean(monthData, months);
	}

	/**
	 * ���ݿ��ڹ������ÿ������ݵ�Сʱλ��
	 * 
	 */
	private static void setTimeDatasPrecision(LfwView viewMain) {
		// ��������
		String[] timeDatas = getTimeDataFields();
		if (timeDatas == null || timeDatas.length == 0) {
			return;
		}
		Dataset[] dss = viewMain.getViewModels().getDatasets();
		if (dss == null || dss.length == 0) {
			return;
		}
		// ����λ��
		int pointNum = getPointNum();
		for (Dataset ds : dss) {
			if (ds instanceof MdDataset) {
				for (String filedId : timeDatas) {
					int index = ds.getFieldSet().nameToIndex(filedId);
					if (index >= 0) {
						FieldSet fieldSet = ds.getFieldSet();
						Field field = fieldSet.getField(filedId);
						if (field instanceof UnmodifiableMdField)
							field = ((UnmodifiableMdField) field).getMDField();
						fieldSet.updateField(filedId, field);

						field.setPrecision(String.valueOf(pointNum));

					}
				}
			}
		}
	}

	/**
	 * ��ÿ���λ��
	 * 
	 * @return
	 */
	private static int getPointNum() {
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		if (timeRuleVO == null) {
			// û�п��ڹ�������������Ĭ��ֵ
			return TaListBasePageMode.DEFAULT_PRECISION;
		}
		int pointNum = Math.abs(timeRuleVO.getTimedecimal());
		return pointNum;
	}

	/**
	 * ���ÿ������ݵ�Сʱλ��<br/>
	 * String[]�����õĿ��������ֶ�����<br/>
	 * 
	 * @return
	 */
	protected static String[] getTimeDataFields() {
		return new String[] { LeavehVO.SUMHOUR, LeavehVO.REALDAYORHOUR, LeavehVO.RESTEDDAYORHOUR,
				LeavehVO.RESTDAYORHOUR, LeavehVO.FREEZEDAYORHOUR, LeavehVO.USEFULDAYORHOUR, LeavebVO.LEAVEHOUR,
				LeavehVO.LACTATIONHOUR };
	}
}