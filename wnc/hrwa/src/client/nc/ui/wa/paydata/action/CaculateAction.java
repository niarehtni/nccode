package nc.ui.wa.paydata.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.hrwa.IWadaysalaryQueryService;
import nc.itf.twhr.ICalculateTWNHI;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.caculate.view.RecacuTypeChooseDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
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
		setBtnName(ResHelper.getString("60130paydata", "060130paydata0331")/*
																			 * @res
																			 * "计算"
																			 */);
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("60130paydata", "060130paydata0331")/*
																									 * @
																									 * res
																									 * "计算"
																									 */
				+ "(Ctrl+A)");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK));
	}

	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		// 使用新的日薪计算逻辑
		/*
		 * if (!getPaydataModel().getIsCompute()) { if
		 * (showYesNoMessage(ResHelper
		 * .getString("60130paydata","060130paydata0332")@res
		 * "有员工调薪，应先进行计算,是否继续?") != MessageDialog.ID_YES) { return; } }
		 */
		final RecacuTypeChooseDialog chooseDialog = new RecacuTypeChooseDialog(getParentContainer(), WA);
		chooseDialog.showModal();

		if (chooseDialog.getResult() == UIDialog.ID_OK) {

			new SwingWorker<Boolean, Void>() {

				BannerTimerDialog dialog = new BannerTimerDialog(getParentContainer());
				String error = null;

				@Override
				protected Boolean doInBackground() throws Exception {
					try {
						dialog.setStartText(ResHelper.getString("60130paydata", "060130paydata0333")/*
																									 * @
																									 * res
																									 * "薪资计算过程中，请稍等..."
																									 */);
						dialog.start();
						CaculateTypeVO caculateTypeVO = chooseDialog.getValue();
						List<SuperVO> payfileVos = getPaydataModel().getData();
						long start = 0;
						long end = 0;

						start = System.currentTimeMillis();
						dialog.setStartText("資料檢查中，請稍等...");
						getPaydataManager().onCalculateCheck(caculateTypeVO, payfileVos.toArray(new SuperVO[0]));
						end = System.currentTimeMillis();
						ShowStatusBarMsgUtil.showStatusBarMsg(
								"資料檢查耗時："
										+ new UFDouble((end - start) / 1000).setScale(2, UFDouble.ROUND_UP).toString()
										+ "秒", getContext());

						List<String> pk_psndocs = null;
						if (!caculateTypeVO.getRange().booleanValue()) {
							// 部分
							pk_psndocs = new ArrayList<String>();
							for (SuperVO vo : payfileVos) {
								if (caculateTypeVO.getType().booleanValue()) {
									// 全部
									pk_psndocs.add((String) vo.getAttributeValue("pk_psndoc"));
								} else {
									if (!((UFBoolean) vo.getAttributeValue("caculateflag")).booleanValue()) {
										// 未计算
										pk_psndocs.add((String) vo.getAttributeValue("pk_psndoc"));
									}
								}
							}
						}

						if (pk_psndocs == null || pk_psndocs.size() > 0) {
							start = System.currentTimeMillis();
							dialog.setStartText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
									"twhr_personalmgt", "068J61035-0002")/*
																		 * @res
																		 * 正在计算员工团保费用
																		 */);
							// 团保计算服务
							IPsndocSubInfoService4JFS service = NCLocator.getInstance().lookup(
									IPsndocSubInfoService4JFS.class);
							service.calculateGroupIns(getWaContext().getPk_org(), getWaContext().getPk_wa_class(),
									getWaContext().getCyear(), getWaContext().getCperiod(), (pk_psndocs == null ? null
											: pk_psndocs.toArray(new String[0])), false);

							end = System.currentTimeMillis();
							ShowStatusBarMsgUtil.showStatusBarMsg("團保計算耗時："
									+ new UFDouble((end - start) / 1000).setScale(2, UFDouble.ROUND_UP).toString()
									+ "秒", getContext());

							dialog.setStartText("預處理二代健保數據，請稍等...");
							// 取消二代健保计算的结果 Ares.Tank 2018年10月26日17:01:47
							start = System.currentTimeMillis();
							ICalculateTWNHI nhiSrv = NCLocator.getInstance().lookup(ICalculateTWNHI.class);
							nhiSrv.delExtendNHIInfo(getContext().getPk_group(), getContext().getPk_org(),
									getWaContext().getPk_wa_class(), getWaContext().getWaLoginVO().getPeriodVO()
											.getPk_wa_period());
							getPaydataManager().refreshWithoutItem();
							end = System.currentTimeMillis();
							ShowStatusBarMsgUtil.showStatusBarMsg("二代健保預處理耗時："
									+ new UFDouble((end - start) / 1000).setScale(2, UFDouble.ROUND_UP).toString()
									+ "秒", getContext());

							dialog.setStartText(ResHelper.getString("60130paydata", "060130paydata0333")/*
																										 * @
																										 * res
																										 * "薪资计算过程中，请稍等..."
																										 */);
							// 以下方法中出现refresh方法有可能造成线程问题。
							// 解决思路将refresh拿到该线程执行完毕后。
							getPaydataManager().onCaculate(caculateTypeVO, payfileVos.toArray(new SuperVO[0]));
						}
					} catch (LockFailedException le) {
						error = ResHelper.getString("60130paydata", "060130paydata0334")/*
																						 * @
																						 * res
																						 * "你操作的数据正被他人修改！"
																						 */;
					} catch (Throwable e) {
						error = e.getMessage();
					} finally {
						IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
						Integer count = (Integer) query.executeQuery("select count(*) from "
								+ IPaydataManageService.DECRYPTEDPKTABLENAME + " where creator='"
								+ getContext().getPk_loginUser() + "'", new ColumnProcessor());

						if (count > 0) {
							dialog.setStartText("正在清理加密數據，請稍等...");
							IPaydataManageService encryptSrv = NCLocator.getInstance().lookup(
									IPaydataManageService.class);
							encryptSrv.doEncryptEx(getWaContext());
						}

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
						ShowStatusBarMsgUtil.showErrorMsg(ResHelper.getString("60130paydata", "060130paydata0335")/*
																												 * @
																												 * res
																												 * "计算过程存在错误"
																												 */,
								error, getContext());
					} else {
						ShowStatusBarMsgUtil.showStatusBarMsg(
								ResHelper.getString("60130paydata", "060130paydata0336")/*
																						 * @
																						 * res
																						 * "在"
																						 */
										+ dialog.getSecond()
										+ ResHelper.getString("60130paydata", "060130paydata0337")/*
																								 * @
																								 * res
																								 * "秒内计算完成."
																								 */
										+ "含日薪計算（"
										+ NCLocator.getInstance().lookup(IWadaysalaryQueryService.class)
												.getCalcuTime(getPaydataModel().getContext().getPk_loginUser()) + "秒）",
								getContext());
					}
				}
			}.execute();

			// 薪资项目预警
			String keyName = ResHelper.getString("60130paydata", "060130paydata0331")/*
																					 * @
																					 * res
																					 * "计算"
																					 */;
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

		if (super.isActionEnable()) {
			List<SuperVO> payfileVos = getPaydataModel().getData();
			if (payfileVos != null && payfileVos.size() > 0) {
				for (int i = 0; i < payfileVos.size(); i++) {
					if (!((DataVO) (payfileVos.get(i))).getCheckflag().booleanValue()) {
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		}
		return false;
	}

}
