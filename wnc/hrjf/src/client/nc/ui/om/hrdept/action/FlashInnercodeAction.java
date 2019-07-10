package nc.ui.om.hrdept.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.om.IDeptAdjustService;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;

/**
 * ���ŵ�innercodeˢ���ع�
 * 
 * @author wangywt
 * @since 20190704
 *
 */
@SuppressWarnings("restriction")
public class FlashInnercodeAction extends HrAction {

	
	public FlashInnercodeAction() {
		super();
		super.setCode("flashInnercodeAction");
		super.putValue(Action.SHORT_DESCRIPTION, "ˢ�²��ű�Ͳ��Ű汾���Innercode");
		setBtnName("ˢ��Innercode");
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent event) throws Exception {
		NCLocator.getInstance().lookup(IDeptAdjustService.class).updateDeptInnercode("", "~");
		MessageDialog.showHintDlg(this.getEntranceUI(), null, " upate dept's innercode has done !");
	}

}
