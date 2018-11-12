/**
 * @(#)GenDetailAction.java 1.0 2017年9月19日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.allocate.ace.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IAllocateMaintain;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.allocate.AggAllocateOutVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaState;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "serial", "restriction" })
public class GenDetailAction extends HrAction {

	public GenDetailAction() {
		setCode("genDetail");
		setBtnName(ResHelper.getString("allocate", "0allcate-ui-000001")); // 生成明细档
	}

	@Override
	public void doAction(ActionEvent evt) throws Exception {
		final WaLoginContext context = (WaLoginContext) getContext();
		this.putValue("message_after_action", "");

		new SwingWorker() {
			BannerTimerDialog dialog = new BannerTimerDialog(
					SwingUtilities.getWindowAncestor(getModel().getContext()
							.getEntranceUI()));
			String error = null;
			String message = null;

			protected Boolean doInBackground() throws Exception {
				try {
					dialog.setStartText("正在生成明細檔");
					dialog.start();
					IAllocateMaintain service = NCLocator.getInstance().lookup(
							IAllocateMaintain.class);
					AggAllocateOutVO[] bills = service.queryBillsByCondition(
							context, null, null);
					boolean isgen = false;

					if (!ArrayUtils.isEmpty(bills)) {
						// // 明细档已生成,不能重复生成!
						// ExceptionUtils.wrappBusinessException(ResHelper.getString("allocate",
						// "0allcate-ui-000013"));
						// return;
						// 明细档已生成,是否重新生成?
						if (MessageDialog.ID_YES != MessageDialog.showYesNoDlg(
								getEntranceUI(), null, ResHelper.getString(
										"allocate", "0allcate-ui-000014"))) {
							putValue(MESSAGE_AFTER_ACTION, null);
							message = "生成已取消";
							isgen = false;
						} else {
							isgen = true;
						}
					} else {
						isgen = true;
					}

					if (isgen) {
						bills = service.genPjShareDetail(context);
						getModel().initModel(bills);
					}

				} catch (LockFailedException le) {
					error = le.getMessage();
				} catch (VersionConflictException le) {
					throw new BusinessException(le.getBusiObject().toString(),
							le);
				} catch (Exception e) {
					error = e.getMessage();
				} finally {
					dialog.end();
				}
				return Boolean.TRUE;
			}

			protected void done() {
				if (error != null) {
					ShowStatusBarMsgUtil.showErrorMsg("生成明細檔發生錯誤", error,
							getModel().getContext());
				} else {
					if (message != null) {
						ShowStatusBarMsgUtil.showStatusBarMsg(message,
								getModel().getContext());
					} else {
						ShowStatusBarMsgUtil.showStatusBarMsg("生成明細檔成功",
								getModel().getContext());
					}
				}
			}
		}.execute();
	}

	@Override
	protected boolean isActionEnable() {
		WaLoginContext waLoginContext = (WaLoginContext) getContext();
		if (waLoginContext.isContextNotNull()
				&& (null != waLoginContext.getWaState()
						&& null != getEnableStateSet() && getEnableStateSet()
						.contains(waLoginContext.getWaState()))) {
			return true;
		}

		return false;
	}

	public Set<WaState> getEnableStateSet() {
		if (waStateSet == null) {
			waStateSet = new HashSet<WaState>();
			waStateSet.add(WaState.CLASS_ALL_PAY);

			waStateSet.add(WaState.CLASS_MONTH_END);
		}
		return waStateSet;
	}

	protected Set<WaState> waStateSet = null;
}
