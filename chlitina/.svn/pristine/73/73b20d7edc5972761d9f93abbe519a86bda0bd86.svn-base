package nc.ui.ta.teamcalendar.validator;

import javax.swing.JComponent;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.IWizardStepValidator;
import nc.ui.pub.beans.wizard.WizardModel;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.ta.teamcalendar.view.batchchange.ConfirmTeamPanelForBatchChange;
import nc.ui.ta.teamcalendar.view.batchchange.ConfirmTeamStepForBatchChange;
import nc.ui.ta.teamcalendar.view.batchchangedaytype.ConfirmTeamPanelForBatchChangeDayType;
import nc.ui.ta.teamcalendar.view.batchchangedaytype.ConfirmTeamStepForBatchChangeDayType;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.bd.team.team01.entity.TeamHeadVO;

import org.apache.commons.lang.ArrayUtils;

public class ConfirmTeamStepValidatorForBatchChangeDayType implements IWizardStepValidator{
	@Override
	public void validate(JComponent comp, WizardModel model)
			throws WizardStepValidateException {
		ConfirmTeamStepForBatchChangeDayType confirmTeamStep = (ConfirmTeamStepForBatchChangeDayType)model.getSteps().get(1);
		ConfirmTeamPanelForBatchChangeDayType confirmTeamPanel = confirmTeamStep.getTeamPanelForBatchChangeDayType();
		TeamHeadVO[] teamVOs = confirmTeamPanel.getSelTeamVOs();
		if(ArrayUtils.isEmpty(teamVOs)){
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg("not selected team!", ResHelper.getString("6017teamcalendar","06017teamcalendar0006")
/*@res "请选择班组！"*/);
			throw e;
		}
		boolean hasDisable = false;
		for(TeamHeadVO teamVO:teamVOs){
			if(IPubEnumConst.ENABLESTATE_ENABLE!=teamVO.getEnablestate()){
				hasDisable = true;
				break;
			}
		}
//		if(hasDisable && UIDialog.ID_YES!=MessageDialog.showYesNoDlg(comp, null, ResHelper.getString("6017teamcalendar","06017teamcalendar0047")/*@res "存在已停用的班组，是否继续？"*/)){
//			throw new WizardStepValidateException();
//		}
		if(hasDisable){
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg(null,ResHelper.getString("6017teamcalendar","06017teamcalendar0048")/*@res "不能包括已经停用的班组！"*/);
			throw e;
		}
	}
}
