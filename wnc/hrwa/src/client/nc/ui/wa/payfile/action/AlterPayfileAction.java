package nc.ui.wa.payfile.action;

import java.awt.Container;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIDialogFactory;
import nc.ui.wa.payfile.common.ShowChangePsn;
import nc.ui.wa.payfile.model.PayfileModelDataManager;
import nc.ui.wa.payfile.view.WNCPeriodDialog;
import nc.ui.wa.pub.WADelegator;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * �䶯��Ա
 * 
 * @author: zhoucx
 * @date: 2009-12-1 ����09:54:23
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class AlterPayfileAction extends PayfileBaseAction {

	private static final long serialVersionUID = -1975088653019093977L;

	public AlterPayfileAction() {
		super();
		setCode("AlterPayfile");
		setBtnName(ResHelper.getString("60130payfile", "060130payfile0242")/*
																			 * @res
																			 * "�䶯��Ա"
																			 */);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK));
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("60130payfile", "060130payfile0242")/*
																									 * @
																									 * res
																									 * "�䶯��Ա"
																									 */+ "(Ctrl+M)");
	}

	/**
	 * @author zhoucx on 2009-12-1
	 * @see nc.ui.uif2.NCAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		WaLoginContext context = (WaLoginContext) getContext();
		PeriodVO periodVO = new PeriodVO();
		periodVO.setClassid(context.getPk_wa_class());
		periodVO.setCyear(context.getWaYear());
		periodVO.setCperiod(context.getWaPeriod());
		periodVO = WADelegator.getPayfileQuery().queryStartEndDate(periodVO);

		if (periodVO == null || periodVO.getCstartdate() == null || periodVO.getCenddate() == null) {
			throw new BusinessException(ResHelper.getString("60130payfile", "060130payfile0243")/*
																								 * @
																								 * res
																								 * "н���ڼ����"
																								 */);
		}

		UFDate beginDate = new UFDate(periodVO.getCstartdate().getMillis());
		UFDate endDate = new UFDate(periodVO.getCenddate().getMillis());

		Container parent = this.getEntranceUI();
		// WNCPeriodDialog dialogPeriod =
		// UIDialogFactory.newDialogInstance(WNCPeriodDialog.class, parent,
		// ResHelper.getString("60130payfile", "060130payfile0244")/*
		// * @res
		// * "ѡ���ѯ���ڼ�"
		// */);
		WNCPeriodDialog dialogPeriod = UIDialogFactory.newDialogInstance(WNCPeriodDialog.class, parent, "Ո�x���ԃ�l��");
		dialogPeriod.init(beginDate, endDate);
		dialogPeriod.showModal();

		if (UIDialog.ID_OK != dialogPeriod.getResult()) {
			return;
		}
		String[] date = dialogPeriod.getDate();
		if (date == null) {
			return;
		}
		beginDate = new UFDate(date[0]);
		endDate = new UFDate(date[1]);

		String pk_usetype = dialogPeriod.getPk_usetype();
		String pk_trnstype = dialogPeriod.getPk_trnstype();
		String pk_trnsreason = dialogPeriod.getPk_trnsreason();
		// ��ʾ�䶯��Ա����
		int isOk = new ShowChangePsn(context).showChangePsnDialog(beginDate, endDate, pk_usetype, pk_trnstype,
				pk_trnsreason);
		if (isOk == UIDialog.ID_OK) {
			// ˢ��ҳ��
			((PayfileModelDataManager) getDataManager()).refresh();
		}
	}

	@Override
	protected boolean isActionEnable() {
		if (getWaLoginVO().getBatch() != null && getWaLoginVO().getBatch() > 100) {
			return false;
		}
		return super.isActionEnable();
	}

}