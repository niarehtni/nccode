package nc.ui.hr.pf.action;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.view.ManualExecuteDialog;
import nc.ui.pub.beans.UIDialog;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.transmng.AggStapply;

import org.apache.commons.lang.StringUtils;

/**************************************************************
 * <br>
 * Created on 2013-5-20 11:00:39<br>
 **************************************************************/
public abstract class PFManualExecuteAction extends PFBaseAction {
	/**************************************************************
	 * Created on 2013-5-20 11:00:43<br>
	 **************************************************************/
	public PFManualExecuteAction() {
		super();

		setBtnName(ResHelper.getString("common", "UC001-0000026")/* @res "ִ��" */);

		putValue(INCAction.CODE, "ExecuteBill");
		putValue(SHORT_DESCRIPTION, ResHelper.getString("common", "UC001-0000026") + "(Alt+E)"/*
																							 * @
																							 * res
																							 * "ִ��"
																							 */);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_MASK));
	}

	@Override
	public void doAction(ActionEvent evt) throws Exception {
		super.doAction(evt);

		validate(selectData);

		ManualExecuteDialog renameDlg = new ManualExecuteDialog(getEntranceUI(), ResHelper.getString("common",
				"UC001-0000026")
		/* @res "ִ��" */);

		if (UIDialog.ID_OK != renameDlg.showModal()) {
			setCancelMsg();
			return;
		}

		UFLiteralDate effectDate = (UFLiteralDate) renameDlg.getEffectDatePane().getValueObj();

		HashMap<String, Object> result = doProcessBillExecute(selectData, effectDate);

		// ˢ�½���
		ArrayList<AggStapply> aggList = (ArrayList<AggStapply>) result.get(TRNConst.RESULT_BILLS);

		// modify start: yunana 2013-5-10
		// ����쳣����ǰ̨�ף����ⳬ��֪ͨNC��Ϣ����֮��ع��������ڽӿ�ʵ�ֵķ���manualExecBills�������׳���
		String msg = (String) result.get(TRNConst.RESULT_MSG);
		if (StringUtils.isNotBlank(msg)) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0110")
			/* @res "��������ԭ��,���ֵ���û��ִ�гɹ�:" */+ '\n' + msg);
		}

		// modify end : yunana 2013-5-10
		getModel().directlyUpdate(aggList.toArray(new HYBillVO[0]));
	}

	/**************************************************************
	 * <br>
	 * Created on 2013-5-20 11:00:19<br>
	 * 
	 * @param selectData2
	 * @param effectDate
	 * @return HashMap<String, Object>
	 * @throws BusinessException
	 **************************************************************/
	public abstract HashMap<String, Object> doProcessBillExecute(AggregatedValueObject[] selectData2,
			UFLiteralDate effectDate) throws BusinessException;
}
