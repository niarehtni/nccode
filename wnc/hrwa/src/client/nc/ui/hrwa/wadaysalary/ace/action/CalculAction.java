package nc.ui.hrwa.wadaysalary.ace.action;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.itf.hrwa.IWadaysalaryService;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.AggDaySalaryVO;
import nc.vo.wa.pub.WaDayLoginContext;

import org.apache.commons.lang.StringUtils;

public class CalculAction extends HrAction {

	/**
	 * 日薪计算：计算按钮
	 */
	private static final long serialVersionUID = -7065970889565697283L;

	private WaDayLoginContext context;

	public WaDayLoginContext getContext() {
		return context;
	}

	public void setContext(WaDayLoginContext context) {
		this.context = context;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		new SwingWorker() {

			BannerTimerDialog dialog = new BannerTimerDialog(SwingUtilities.getWindowAncestor(getModel().getContext()
					.getEntranceUI()));
			String error = null;

			protected Boolean doInBackground() throws Exception {
				//启基本地化适配 Ares.Tank 2018-10-19 16:47:42
				/*try {
					dialog.setStartText("正在計算日薪");
					dialog.start();

					IWadaysalaryService wadaysalaryService = NCLocator.getInstance().lookup(IWadaysalaryService.class);
					AggDaySalaryVO[] aggSalaryVOs = wadaysalaryService.calculSalaryByHrorg(context,
							context.getPk_hrorg(), context.getCalculdate().toString());
					getModel().initModel(aggSalaryVOs);

				} catch (LockFailedException le) {
					error = le.getMessage();
				} catch (VersionConflictException le) {
					throw new BusinessException(le.getBusiObject().toString(), le);
				} catch (Exception e) {
					error = e.getMessage();
				} finally {
					dialog.end();
				}*/
				return Boolean.TRUE;
			}

			protected void done() {
				if (error != null) {
					ShowStatusBarMsgUtil.showErrorMsg("錯誤", error, getContext());
				} else {
					ShowStatusBarMsgUtil.showStatusBarMsg("計算成功！", getContext());
				}
			}
		}.execute();

		putValue("message_after_action", "正在計算日薪 ...");
	}

	protected boolean isActionEnable() {
		if (StringUtils.isEmpty(getContext().getPk_hrorg()) || null == getContext().getCalculdate()) {
			return false;
		}
		return true;
	}

	public CalculAction() {
		super.setBtnName("計算");
		super.setCode("CALCUL");
		super.putValue("CALCUL", "計算");
	}

}
