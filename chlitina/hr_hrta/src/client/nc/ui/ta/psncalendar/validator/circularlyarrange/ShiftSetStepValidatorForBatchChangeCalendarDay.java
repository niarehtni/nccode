package nc.ui.ta.psncalendar.validator.circularlyarrange;

import javax.swing.JComponent;


import nc.hr.utils.ResHelper;

import nc.ui.pub.beans.wizard.IWizardStepValidator;
import nc.ui.pub.beans.wizard.WizardModel;
import nc.ui.pub.beans.wizard.WizardStepValidateException;

import nc.ui.ta.psncalendar.view.batchchangecalendarday.ShiftSetPanelForBatchChangeCalendarDay;
import nc.ui.ta.psncalendar.view.batchchangecalendarday.ShiftSetStepForBatchChangeCalendarDay;


public class ShiftSetStepValidatorForBatchChangeCalendarDay implements IWizardStepValidator {

	@Override
	public void validate(JComponent comp, WizardModel model)
			throws WizardStepValidateException {
		System.out.println("validate.......................");

		//校验补休天数
		ShiftSetStepForBatchChangeCalendarDay step3 = (ShiftSetStepForBatchChangeCalendarDay)model.getSteps().get(2);
		ShiftSetPanelForBatchChangeCalendarDay panel3 = step3.getShiftSetPanelForBatchChangeCalendarDay();
				if(!panel3.supplementDayNumValidator()){
					WizardStepValidateException e = new WizardStepValidateException();
					e.addMsg("not selected person!", ResHelper.getString("twhr_psncalendar", "psncalendar-0012")
		/*@res "转换补休时(天数)校验失败,请检查输入的值"*/);
					throw e;
				}
		
		/*ShiftSetStepForBatchChange step3 = (ShiftSetStepForBatchChange)model.getSteps().get(2);
		ShiftSetPanelForBatchChange panel3 = step3.getShiftSetPanelForBatchChange();
		//取出班次主键
//		String old_Pk_shift = panel3.getOldShiftPk();//老的班次主键
		String new_Pk_shift = panel3.getNewShiftPk();//新的班次主键
		
		WizardStepValidateException e = new WizardStepValidateException();
		String pk_org=panel3.getPK_org();
		// 如果选择了新班次, 则校验新的班次是否是属于选择的组织所拥有的班次
		if(StringUtils.isNotEmpty(new_Pk_shift)) {
			// 公休和预置班次不处理
			if(!(ShiftVO.PK_GX.equals(new_Pk_shift) ||ShiftConst.DEFAULT_PK_SHIFT.equals(new_Pk_shift))) {
				AggShiftVO aggVO = null;
				try {
					aggVO = ShiftServiceFacade.queryShiftAggVOByPk(new_Pk_shift);
				} catch (BusinessException e1) {
					Logger.error(e1.getMessage(),e1);
				}
				// 没有指定班次或班次不属于选中组织,则抛异常
				if(aggVO==null || !aggVO.getShiftVO().getPk_org().equals(pk_org)) {
					e.addMsg("ShiftNotInOrg",  ResHelper.getString("6017psncalendar","06017psncalendar0105")@res "请选择属于本业务单元的班次");
					throw e;
				}
			}
		}*/
		// 勾选旧班次,且新旧班次一样(允许相同班次进行调班，解决班次修改后工作日历不同步的问题)
//		if(panel3.getOldShiftCheckBox().isSelected() && StringUtils.equals(old_Pk_shift, new_Pk_shift)){
//			e.addMsg("SameShift",  ResHelper.getString("6017psncalendar","06017psncalendar0077")/*@res "原班次和新班次不能相同!"*/);
//			throw e;
//		}
	}

}