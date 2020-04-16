package nc.ui.wa.paydata.view;

import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.wa.pub.WaOrgHeadPanel;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class WaOrgHeadPanelForWa extends WaOrgHeadPanel {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = -3406991021480656553L;

	public WaOrgHeadPanelForWa(ITabbedPaneAwareComponent tabbedPaneAwareComponent) {
		super(tabbedPaneAwareComponent);
	}

	public void valueChanged2(ValueChangedEvent event) throws BusinessException {
		super.valueChanged2(event);

		if (getContext() != null && !StringUtils.isEmpty(getContext().getPk_org())) {
			IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			Integer count = (Integer) query.executeQuery("select count(*) from "
					+ IPaydataManageService.DECRYPTEDPKTABLENAME + " where creator='" + getContext().getPk_loginUser()
					+ "'", new ColumnProcessor());

			if (count > 0) {
				new SwingWorker<Boolean, Void>() {
					BannerTimerDialog dialog = new BannerTimerDialog(getContext().getEntranceUI());
					String error = null;

					@Override
					protected Boolean doInBackground() throws Exception {
						try {
							dialog.setStartText("發現中間數據，正在清理...");
							dialog.start();
							IPaydataManageService encryptSrv = NCLocator.getInstance().lookup(
									IPaydataManageService.class);
							encryptSrv.doEncryptEx(getContext());
						} catch (Throwable e) {
							error = e.getMessage();
						} finally {
							dialog.end();
						}
						return Boolean.TRUE;
					}

					/**
					 * @author zhangg on 2010-7-7
					 * @see javax.swing.SwingWorker#done()
					 */
					@Override
					protected void done() {
						if (error != null) {
							ShowStatusBarMsgUtil.showErrorMsg("中間數據清理失敗", error, getContext());
						} else {
							ShowStatusBarMsgUtil.showStatusBarMsg("中間數據已清理", getContext());
						}
					}
				}.execute();
			}
		}
	}
}
