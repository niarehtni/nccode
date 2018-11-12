package nc.ui.hrwa.incometax.action;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IGetAggIncomeTaxData;
import nc.itf.hrwa.IIncometaxMaintain;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.hrwa.incometax.view.AddIncomeTaxView;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.hrwa.incometax.IncomeTaxVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.uap.busibean.exception.BusiBeanException;

/**
 * 
 * @author ward.wong
 * @date 20180126
 * @功能描述 生成申报明细档数据按钮
 * 
 */
public class AddAction extends NCAction {

	private static final long serialVersionUID = -7914395932671282910L;

	private AddIncomeTaxView addTaxView;

	private AbstractAppModel model;

	public AddAction() {
		this.setBtnName(ResHelper.getString("incometax", "2incometax-n-000001")/* "申报明细档生成" */);
		super.setCode("declareform");
		super.putValue("declareform",
				ResHelper.getString("incometax", "2incometax-n-000001"));
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if (addTaxView == null) {
			addTaxView = new AddIncomeTaxView();
			addTaxView.setTitle(ResHelper.getString("incometax",
					"2incometax-n-000001"));
		}
		if (addTaxView.showModal() == 1) {
			new Thread() {
				@Override
				public void run() {

					IProgressMonitor progressMonitor = NCProgresses
							.createDialogProgressMonitor(getModel()
									.getContext().getEntranceUI());

					try {
						progressMonitor.beginTask("匯出中...",
								IProgressMonitor.UNKNOWN_REMAIN_TIME);
						progressMonitor.setProcessInfo("匯出中，請稍候.....");
						boolean isForeignMonth = addTaxView.isForeignMonth();
						String unifiednumber = addTaxView.getUnifiednumber();
						String[] declaretypes = addTaxView.getDeclaretype();
						String[] waclass = addTaxView.getWaclass();
						String year = addTaxView.getYear();
						String beginMonth = addTaxView.getBeginMonth();
						String endMonth = addTaxView.getEndMonth();
						IGetAggIncomeTaxData getServer = NCLocator
								.getInstance().lookup(
										IGetAggIncomeTaxData.class);
						List<AggIncomeTaxVO> listAggIncomeTaxVOs = getServer
								.getAggIncomeTaxData(isForeignMonth,
										unifiednumber, declaretypes, waclass,
										year, beginMonth, endMonth);
						if (null == listAggIncomeTaxVOs
								|| listAggIncomeTaxVOs.size() < 1) {
							throw new BusiBeanException(ResHelper.getString(
									"incometax", "2incometax-n-000005")/* "没有符合条件的数据" */);
						}
						for (int i = 0; i < listAggIncomeTaxVOs.size(); i++) {
								AggIncomeTaxVO aggVO = listAggIncomeTaxVOs.get(i);
								IncomeTaxVO hvo = aggVO.getParentVO();
								//通过pk_wa_class获取declaretype
								IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
								List<Map<String,String>> list = (List<Map<String,String>>)iUAPQueryBS.executeQuery("select declareform from WA_WACLASS where pk_wa_class='"+hvo.getPk_wa_class()+"' ", new MapListProcessor());
								hvo.setBilldate(new UFDate());// 单据日期
								hvo.setIsforeignmonthdec(new UFBoolean(
										isForeignMonth));
								hvo.setDeclaretype(list.get(0).get("declareform"));
								hvo.setUnifiednumber(unifiednumber);
								hvo.setIsdeclare(UFBoolean.FALSE);
								hvo.setIsgather(UFBoolean.FALSE);
								hvo.setCreator(getModel().getContext()
										.getPk_loginUser());
								hvo.setCreationtime(new UFDateTime());
								hvo.setPk_org(getModel().getContext().getPk_org());
								hvo.setPk_group(getModel().getContext()
										.getPk_group());
						}
						IIncometaxMaintain service = NCLocator.getInstance()
								.lookup(IIncometaxMaintain.class);
						AggIncomeTaxVO[] aVos = service.insert(
								listAggIncomeTaxVOs
										.toArray(new AggIncomeTaxVO[listAggIncomeTaxVOs.size()]), null);
						getModel().initModel(aVos);
						ShowStatusBarMsgUtil
								.showStatusBarMsg(
										ResHelper.getString("incometax",
												"2incometax-n-000002")/* "申报明细档生成成功，已生成" */
												+ aVos.length
												+ ResHelper.getString(
														"incometax",
														"2incometax-n-000003")/* "条数据" */,
										getModel().getContext());
					} catch (Exception e) {
						ShowStatusBarMsgUtil.showErrorMsgWithClear(ResHelper
								.getString("incometax", "2incometax-n-000004"),
								e.getMessage(), getModel().getContext());
					} finally {
						progressMonitor.done(); // 进度任务结束
					}
				}
			}.start();
		}
	}

	protected boolean isActionEnable() {
		return true;
	}

	public AddIncomeTaxView getAddTaxView() {
		return addTaxView;
	}

	public void setAddTaxView(AddIncomeTaxView addTaxView) {
		this.addTaxView = addTaxView;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

}
