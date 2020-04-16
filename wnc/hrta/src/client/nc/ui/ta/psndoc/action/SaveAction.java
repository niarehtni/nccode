package nc.ui.ta.psndoc.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ITBMPsndocManageMaintain;
import nc.itf.ta.ITimeRuleQueryService;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.ta.psndoc.model.TbmPsndocAppModel;
import nc.ui.ta.psndoc.view.CardListEditor;
import nc.ui.ta.psndoc.view.TbmPsndocListView;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timerule.TimeRuleVO;

public class SaveAction extends nc.ui.hr.uif2.action.SaveAction {

	private static final long serialVersionUID = 1L;
	CardListEditor cardListEditor = null;

	public CardListEditor getCardListEditor() {
		return cardListEditor;
	}

	/*
	 */
	@Override
	public void doAction(ActionEvent evt) throws Exception {
		if (getModel().getUiState() == UIState.ADD) {
			TimeRuleVO timeRulevo = NCLocator.getInstance().lookup(ITimeRuleQueryService.class)
					.queryByOrg(getModel().getContext().getPk_org());
			Object objValue = getCardListEditor().getCardEditor().getValue();
			validate(objValue);
			// ��Ϊ������ͨ������У����ѯ�ʣ���������Ƚ��к�̨У��
			NCLocator.getInstance().lookup(ITBMPsndocManageMaintain.class).check((TBMPsndocVO) objValue);
			// �Ƿ����Ա����Ĭ�ϰ��Ű�
			boolean isArrangeClass = false;
			if (timeRulevo != null && timeRulevo.getAutoarrangemonth() != 0) {
				int ret = MessageDialog.showYesNoDlg(getContext().getEntranceUI(), null,
						ResHelper.getString("6017psndoc", "06017psndoc0036")
				/* @res "����Ҫ���¼��뿼�ڵ�����Ա����Ĭ�Ϲ�������������?" */);
				isArrangeClass = (ret == UIDialog.ID_YES ? true : false);
			}
			((TbmPsndocAppModel) getModel()).insert(objValue, isArrangeClass, true);

		} else if (getModel().getUiState() == UIState.EDIT) {
			Object objValue = null;
			// v63��Ƭ������޸Ĺ���
			if (((ITabbedPaneAwareComponent) getCardListEditor().getCardEditor()).isComponentVisible()) {
				TBMPsndocVO vo = (TBMPsndocVO) getCardListEditor().getCardEditor().getValue();
				objValue = new TBMPsndocVO[] { vo };
			} else {
				objValue = ((TbmPsndocListView) getCardListEditor().getListEditor()).getValue();
			}
			validate(objValue);
			getModel().update(objValue);
		}

		getModel().setUiState(UIState.NOT_EDIT);

		ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getSaveSuccessInfo(), getContext());
	}

	public void setCardListEditor(CardListEditor cardListEditor) {
		this.cardListEditor = cardListEditor;
	}

}