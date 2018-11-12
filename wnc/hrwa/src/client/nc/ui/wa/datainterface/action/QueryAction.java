package nc.ui.wa.datainterface.action;

import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.datainterface.IDataIOQueryService;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.DefaultQueryDelegator;
import nc.ui.wa.datainterface.model.DataIOAppModel;
import nc.ui.wa.datainterface.view.DataIOPanel;
import nc.vo.hr.datainterface.AggHrIntfaceVO;
import nc.vo.hr.datainterface.DataFromEnum;
import nc.vo.hr.datainterface.FormatItemVO;
import nc.vo.hr.datainterface.HrIntfaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.datainterface.DataIOconstant;
import nc.vo.wa.datainterface.DataInterfaceVo;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.WaLoginContext;

public class QueryAction extends nc.ui.hr.uif2.action.QueryAction {
	DataIOPanel dataIoPanel = null;
	private int queryCount = 0;
	private String strMsg = "";

	// /** */
	// private WaDataIOHeadPanel orgpanel = null;
	//
	// /***************************************************************************
	// * <br>Created on 2012-11-1 ����9:35:15<br>
	// * @return
	// * @author daicy
	// ***************************************************************************/
	// public WaDataIOHeadPanel getOrgpanel() {
	// return orgpanel;
	// }
	//
	// /***************************************************************************
	// * <br>Created on 2012-11-1 ����9:35:21<br>
	// * @param orgpanel
	// * @author daicy
	// ***************************************************************************/
	// public void setOrgpanel(WaDataIOHeadPanel orgpanel) {
	// this.orgpanel = orgpanel;
	// }

	public QueryAction() {
		super();
		// this.setBtnName("��ѯ");
		// this.putValue(INCAction.CODE, "��ѯ");
		// this.putValue(Action.SHORT_DESCRIPTION, "��ѯ(Ctrl+Q)");
	}

	@Override
	protected boolean isActionEnable() {
		if (HRWACommonConstants.FC_BANKITF.equals(getContext().getFuncInfo()
				.getFuncode())) {
			return getDataIoPanel().getHeadPanel().getWaClassRefPane()
					.getRefCode() != null
					&& getDataIoPanel().getHeadPanel().getWaPeriodRefPane()
							.getRefCode() != null;
		} else {
			return true;
		}
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2009-8-31 15:26:32<br>
	 * 
	 * @see nc.ui.uif2.actions.QueryAction#doAction(java.awt.event.ActionEvent)
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public void doAction(ActionEvent evt) throws Exception {
		// WaLoginContext context = (WaLoginContext) getContext();
		// // ��ȡ����״̬
		// WaLoginVO waLoginVO =
		// WaClassStateHelper.getWaclassVOWithState(context.getWaLoginVO());
		//
		// // �ж�״̬�Ƿ����仯
		// if (!context.getWaLoginVO().getState().equals(waLoginVO.getState()))
		// {
		// MessageDialog.showHintDlg(getEntranceUI(), null,
		// ResHelper.getString("60130payfile","060130payfile0250")/*@res
		// "�÷���״̬�����˱仯��ϵͳ�Զ�ˢ�£�"*/);
		// getOrgpanel().refresh();
		// return;
		// }

		validate(null);

		if (queryDelegator == null) {
			queryDelegator = new DefaultQueryDelegator();

			((DefaultQueryDelegator) queryDelegator).setContext(getContext());
		}

		try {
			queryDelegator.doQuery(getQueryExecutor());

		} finally {
			setStatusBarMsg();
			queryExcuted = false;
		}
	}

	@Override
	protected void executeQuery(IQueryScheme queryScheme) {
		String strWhere = queryScheme.getWhereSQLOnly();
		Logger.debug("SQL=[" + strWhere + "]");
		// getModel().initModel(null, new
		// ModelDataDescriptor(queryScheme.getName()));
		executeQuery(strWhere);
	}

	@Override
	protected void executeQuery(String strWhere) {
		ShowStatusBarMsgUtil.showStatusBarMsg(null, getContext());
		queryCount = 0;
		// δѡ��������ʱ������
		Object obj = getModel().getSelectedData();
		if (obj == null) {
			return;
		}

		IDataIOQueryService service = NCLocator.getInstance().lookup(
				IDataIOQueryService.class);
		try {

			AggHrIntfaceVO[] aggVOs = null;
			if (getModel().getContext().getNodeCode()
					.equals(DataIOconstant.NODE_BANK)) {
				aggVOs = (AggHrIntfaceVO[]) ((DataIOAppModel) getModel())
						.getData().toArray(new AggHrIntfaceVO[0]);
			} else {
				aggVOs = new AggHrIntfaceVO[] { (AggHrIntfaceVO) getModel()
						.getSelectedData() };
			}
			HashMap<String, ArrayList<HashMap<String, Object>>> results = service
					.queryWaDataByCondAndAggVOs((WaLoginContext) getModel()
							.getContext(), aggVOs, strWhere);

			for (int i = 0; aggVOs != null && i < aggVOs.length; i++) {
				if (aggVOs[i] != null) {
					HrIntfaceVO vo = (HrIntfaceVO) aggVOs[i].getParentVO();
					BillModel billModel = ((DataIOAppModel) getModel())
							.getBillModelMap().get(vo.getPk_dataio_intface());
					FormatItemVO[] itemVOs = (FormatItemVO[]) aggVOs[i]
							.getTableVO(DataIOconstant.HR_DATAINTFACE_B);
					if (billModel == null) {
						return;
					}
					// billModel.clearBodyData();
					ArrayList<HashMap<String, Object>> objs = results.get(vo
							.getPk_dataio_intface());
					if (objs != null && objs.size() > 0) {
						queryCount += objs.size();
					}

					BillItem[] items = billModel.getBodyItems();
					for (int j = 0; objs != null && j < objs.size(); j++) {
						// Ϊ�кŸ�ֵ
						objs.get(j).put("otherrownum", "" + (j + 1));
					}

					billModel.setBodyDataVO(this.getVos(objs, itemVOs, items));
					((DataIOAppModel) getModel()).getResults().put(
							vo.getPk_dataio_intface(), objs);
				}
			}
			queryExcuted = true;
			strMsg = queryCount == 0 ? ResHelper.getString("6001uif2",
					"06001uif20045")
			/* @res "δ�鵽�������������ݣ���ȷ�ϲ�ѯ���������²�ѯ��" */: ResHelper.getString(
					"6001uif2", "06001uif20046", String.valueOf(queryCount))
			/* @res "��ѯ�ɹ����Ѳ鵽{0}�����ݡ�" */;

		} catch (BusinessException e) {
			Logger.error("��ѯʧ��!");
			strMsg = null;

			// throw new BusiBeanRuntimeException(e.getMessage());
		}
	}

	private DataInterfaceVo[] getVos(ArrayList<HashMap<String, Object>> objs,
			FormatItemVO[] itemVOs, BillItem[] items) {
		DataInterfaceVo[] vos = null;
		if (objs != null) {
			vos = new DataInterfaceVo[objs.size()];
			for (int i = 0; i < vos.length; i++) {
				vos[i] = new DataInterfaceVo(objs.get(i));
				if (getModel().getContext().getNodeCode()
						.equals(DataIOconstant.NODE_BANK)) {
					for (int k = 0; items != null && k < items.length; k++) {
						if ((Integer.valueOf(
								DataFromEnum.SINGLE.getEnumValue().getValue())
								.intValue() == itemVOs[k].getIsourcetype()
								.intValue())
								&& items[k].getKey() != null
								&& vos[i].getAttributeValue(items[k].getKey()) == null) {
							vos[i].setAttributeValue(items[k].getKey(),
									itemVOs[k].getVcontent());
						}
					}
				}
			}
		}

		// 2016-12-22 zhousze н�ʼ��ܣ����ﴦ�����ݽӿڸ������ݽӿڲ�ѯ���ݣ����ݽ��� begin
		for (DataInterfaceVo vo : vos) {
			String[] names = vo.getAttributeNames();
			ArrayList<String> itemList = new ArrayList<String>();
			for (int i = 0; i < names.length; i++) {
				// ������ֵ��н����Ŀ���������ں�����Ŀ����
				if (names[i].startsWith("wa_dataf_")) {
					itemList.add(names[i]);
				}
			}
			for (int j = 0; j < itemList.size(); j++) {
				vo.setAttributeValue(
						itemList.get(j),
						new BigDecimal(
								vo.getAttributeValue(itemList.get(j)) == null ? 0
										: SalaryDecryptUtil.decrypt(((BigDecimal) vo
												.getAttributeValue(itemList
														.get(j))).doubleValue())));
			}
		}
		// end

		return vos;
	}

	public DataIOPanel getDataIoPanel() {
		return dataIoPanel;
	}

	public void setDataIoPanel(DataIOPanel dataIoPanel) {
		this.dataIoPanel = dataIoPanel;
	}

	@Override
	protected void setStatusBarMsg() {
		// putValue(MESSAGE_AFTER_ACTION, strMsg);
		// shenliangc 20140815��ѯ����ʾ��Ϣ��������仰�������ã�ԭ���ꡣ
		ShowStatusBarMsgUtil.showStatusBarMsg(strMsg, getContext());
	}
}
