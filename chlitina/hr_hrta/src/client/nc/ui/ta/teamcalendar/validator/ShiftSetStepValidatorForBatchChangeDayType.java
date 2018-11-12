package nc.ui.ta.teamcalendar.validator;

import javax.swing.JComponent;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.IWizardStepValidator;
import nc.ui.pub.beans.wizard.WizardModel;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.ta.psncalendar.view.batchchangecalendarday.ShiftSetPanelForBatchChangeCalendarDay;
import nc.ui.ta.psncalendar.view.batchchangecalendarday.ShiftSetStepForBatchChangeCalendarDay;
import nc.ui.ta.teamcalendar.view.batchchange.ShiftSetPanelForBatchChange;
import nc.ui.ta.teamcalendar.view.batchchange.ShiftSetStepForBatchChange;
import nc.ui.ta.teamcalendar.view.batchchangedaytype.ShiftSetPanelForBatchChangeDayType;
import nc.ui.ta.teamcalendar.view.batchchangedaytype.ShiftSetStepForBatchChangeDayType;

public class ShiftSetStepValidatorForBatchChangeDayType implements IWizardStepValidator {

	@Override
	public void validate(JComponent comp, WizardModel model) throws WizardStepValidateException {
		ShiftSetStepForBatchChangeDayType step3 = (ShiftSetStepForBatchChangeDayType) model.getSteps().get(2);
		ShiftSetPanelForBatchChangeDayType panel3 = step3.getShiftSetPanelForBatchChangeDayType();

		// У�鲹������
		// ShiftSetStepForBatchChangeCalendarDay step3 =
		// (ShiftSetStepForBatchChangeCalendarDay)model.getSteps().get(2);
		// ShiftSetPanelForBatchChangeCalendarDay panel3 =
		// step3.getShiftSetPanelForBatchChangeCalendarDay();
		if (!panel3.supplementDayNumValidator()) {
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg("not selected person!", ResHelper.getString("twhr_psncalendar", "psncalendar-0012")
			/* @res "ת������ʱ(����)У��ʧ��,���������ֵ" */);
			throw e;
		}
		// ȡ���������
		// ȡ���������
		// String old_Pk_shift = panel3.getOldShiftPk();//�ϵİ������
		// String new_Pk_shift = panel3.getNewShiftPk();//�µİ������
		// У���µİ���Ƿ�������ѡ�����֯��ӵ�еİ��,�ڲ������Ѿ�У�����
		// String pk_org=panel3.getPk_org();
		// AggShiftVO[] aggvos=null;
		// int i=0;
		// try {
		// aggvos=NCLocator.getInstance().lookup(IShiftQueryService.class).queryAllByOrg(pk_org);
		// if(aggvos.length>0){
		// for(AggShiftVO shiftvo : aggvos){
		// if(shiftvo.getParentVO().getPrimaryKey().equals(new_Pk_shift)||"0001Z7000000000000GX".equals(new_Pk_shift)
		// ||"0001Z70000000DEFAULT".equals(new_Pk_shift)){
		// i=1;
		// break;
		// }
		// }
		// }
		// } catch (BusinessException e1) {
		// Logger.error(e1.getMessage(),e1);
		// e1.printStackTrace();
		// }
		WizardStepValidateException e = new WizardStepValidateException();
		// if(i==0){
		// e.addMsg("��ѡ�����ڱ�ҵ��Ԫ�İ��",
		// ResHelper.getString("6017psncalendar","06017psncalendar0105"));
		// throw e;
		// }
		// if(panel3.getOldShiftCheckBox().isSelected()){
		// if(StringUtils.equals(old_Pk_shift, new_Pk_shift)){
		// e.addMsg("SameShift",
		// ResHelper.getString("6017psncalendar","06017psncalendar0077")
		// /*@res "ԭ��κ��°�β�����ͬ!"*/);
		// throw e;
		// }
		// }

	}
}
