package nc.ui.wa.paydata.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.twhr.ICalculateTWNHI;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.caculate.view.RecacuTypeChooseDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.pub.WaState;

/**
 * 计算
 *
 * @author: zhangg
 * @date: 2009-12-1 上午08:39:35
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class CaculateAction extends PayDataBaseAction {

	private static final long serialVersionUID = 1L;
	private static final String WA = "wa";

	public CaculateAction() {
		super();
		putValue(INCAction.CODE, IHRWAActionCode.CACULATEACTION);
		setBtnName(ResHelper.getString("60130paydata","060130paydata0331")/*@res "计算"*/);
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("60130paydata","060130paydata0331")/*@res "计算"*/+"(Ctrl+A)");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK));
	}

	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		if (!getPaydataModel().getIsCompute()) {
			if (showYesNoMessage(ResHelper.getString("60130paydata","060130paydata0332")/*@res "有员工调薪，应先进行计算,是否继续?"*/) != MessageDialog.ID_YES) {
				return;
			}
		}
		final RecacuTypeChooseDialog chooseDialog = new RecacuTypeChooseDialog(getParentContainer(), WA);
		chooseDialog.showModal();
		
		if (chooseDialog.getResult() == UIDialog.ID_OK) {
			 

			
			new SwingWorker<Boolean, Void>() {
	
				BannerTimerDialog dialog = new BannerTimerDialog(getParentContainer());
				String error = null;

				@Override
				protected Boolean doInBackground() throws Exception {
					try {
						dialog.setStartText(ResHelper.getString("60130paydata","060130paydata0333")/*@res "薪资计算过程中，请稍等..."*/);
						dialog.start();
						
						//取消二代健保计算的结果 Ares.Tank 2018年10月26日17:01:47
						ICalculateTWNHI nhiSrv = NCLocator.getInstance().lookup(
								ICalculateTWNHI.class);
						nhiSrv.delExtendNHIInfo(getContext().getPk_group(), getContext().getPk_org(), getWaContext().getPk_wa_class()
								,getWaContext().getWaLoginVO().getPeriodVO().getPk_wa_period());
						getPaydataManager().refreshWithoutItem();
						
						CaculateTypeVO caculateTypeVO = chooseDialog.getValue();
						List<SuperVO> payfileVos= getPaydataModel().getData();
						
						//以下方法中出现refresh方法有可能造成线程问题。
						//解决思路将refresh拿到该线程执行完毕后。
						getPaydataManager().onCaculate(caculateTypeVO,payfileVos.toArray(new SuperVO[0]));
					}catch (LockFailedException le) {
						error = ResHelper.getString("60130paydata","060130paydata0334")/*@res "你操作的数据正被他人修改！"*/;
					}catch (VersionConflictException le) {
						throw new BusinessException(le.getBusiObject().toString(),le);
					}catch (Exception e) {
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
						ShowStatusBarMsgUtil.showErrorMsg(ResHelper.getString("60130paydata","060130paydata0335")/*@res "计算过程存在错误"*/,error, getContext());
						//MessageDialog.showErrorDlg(getParentContainer(), null, error);

					} else {
						ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("60130paydata","060130paydata0336")/*@res "在"*/ + dialog.getSecond() + ResHelper.getString("60130paydata","060130paydata0337")/*@res "秒内计算完成."*/,
								getContext());
					}
				}
			}.execute();
			// 薪资项目预警
			String keyName = ResHelper.getString("60130paydata","060130paydata0331")/*@res "计算"*/;
			String[] files = getPaydataManager().getAlterFiles(keyName);
			showAlertInfo(files);
		}
	}

	/**
	 * 按钮可用的状态
	 *
	 * @author zhangg on 2009-12-1
	 * @see nc.ui.wa.paydata.action.PayDataBaseAction#getEnableStateSet()
	 */
	@Override
	public Set<WaState> getEnableStateSet() {
		if (waStateSet == null) {
			waStateSet = new HashSet<WaState>();
			waStateSet.add(WaState.CLASS_WITHOUT_RECACULATED);
			waStateSet.add(WaState.CLASS_RECACULATED_WITHOUT_CHECK);
			waStateSet.add(WaState.CLASS_PART_CHECKED);

		}
		return waStateSet;
	}
	
	@Override
	protected boolean isActionEnable() {
		
		if(super.isActionEnable()){
			List<SuperVO> payfileVos= getPaydataModel().getData();
			if (payfileVos != null && payfileVos.size() > 0) {
				for (int i = 0; i < payfileVos.size(); i++) {
					if (!((DataVO)(payfileVos.get(i))).getCheckflag().booleanValue()) {
						return true;
					}
				}
				return false;
			}else{
				return false;
			}
		}
		return false;
	}

}