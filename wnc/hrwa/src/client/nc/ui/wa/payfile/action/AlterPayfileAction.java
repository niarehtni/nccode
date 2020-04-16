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
 * 变动人员
 * 
 * @author: zhoucx
 * @date: 2009-12-1 上午09:54:23
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class AlterPayfileAction extends PayfileBaseAction {

	private static final long serialVersionUID = -1975088653019093977L;

	public AlterPayfileAction() {
		super();
		setCode("AlterPayfile");
		setBtnName(ResHelper.getString("60130payfile", "060130payfile0242")/*
																			 * @res
																			 * "变动人员"
																			 */);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK));
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("60130payfile", "060130payfile0242")/*
																									 * @
																									 * res
																									 * "变动人员"
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
																								 * "薪资期间错误！"
																								 */);
		}

		UFDate beginDate = new UFDate(periodVO.getCstartdate().getMillis());
		UFDate endDate = new UFDate(periodVO.getCenddate().getMillis());

		Container parent = this.getEntranceUI();
		// WNCPeriodDialog dialogPeriod =
		// UIDialogFactory.newDialogInstance(WNCPeriodDialog.class, parent,
		// ResHelper.getString("60130payfile", "060130payfile0244")/*
		// * @res
		// * "选择查询的期间"
		// */);
		WNCPeriodDialog dialogPeriod = UIDialogFactory.newDialogInstance(WNCPeriodDialog.class, parent, "請選擇查詢條件");
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
		// 显示变动人员窗口
		int isOk = new ShowChangePsn(context).showChangePsnDialog(beginDate, endDate, pk_usetype, pk_trnstype,
				pk_trnsreason);
		if (isOk == UIDialog.ID_OK) {
			// 刷新页面
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