package nc.ui.hrwa.sumincometax.ace.listener;

import java.util.Set;

import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.funcnode.ui.FuncletInitData;
import nc.ui.pubapp.billgraph.RowLinkQueryUtil;
import nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.pubapp.uif2app.PubExceptionHanler;
import nc.vo.trade.billsource.LightBillVO;
import nc.vo.uif2.LoginContext;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;
import nc.vo.hrwa.sumincometax.CIncomeTaxVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.ui.uif2.UIState;
import nc.vo.querytemplate.queryscheme.SimpleQuerySchemeVO;
import nc.desktop.quickstart.QSContext;
import nc.ui.pub.linkoperate.ILinkApproveData;
import nc.ui.pub.linkoperate.ILinkMaintainData;
import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.ui.uif2.editor.BillForm;

import org.apache.commons.lang.ArrayUtils;

import nc.ui.hrwa.incometax.action.SumIncomeTaxLinkQueryAction;
import nc.ui.ml.NCLangRes;
import nc.bs.logging.Logger;

public class SumIncomeFuncNodeInitDataListener extends
		DefaultFuncNodeInitDataListener {

	private IExceptionHandler exceptionHandler;

	// private LoginContext context;

	@Override
	public void initData(FuncletInitData data) {
		// 如果一个节点已经打开并且正在编辑数据, 默认不再打开其他的数据
		if (UIState.EDIT.equals(this.getModel().getUiState())
				|| UIState.ADD.equals(this.getModel().getUiState())) {
			return;
		}

		if (null == data) {
			this.getModel().initModel(null);
			return;
		}

		// if (data.getInitType() == 0
		// && data.getInitData() instanceof SimpleQuerySchemeVO) {
		// this.doInitDataByQueryScheme(data);
		// return;
		// }
		IInitDataProcessor processor = null;
		if (null != this.getProcessorMap()) {
			processor = this.getProcessorMap().get(
					Integer.valueOf(data.getInitType()));
		}
		if (null == processor) {
			processor = new DefaultInitDataProcessor();
		}
		try {
			processor.process(data);
		} catch (Exception e) {
			this.getExceptionHandler(true).handlerExeption(e);
			// 发生错误以后，节点不应该再被打开，需要将异常抛出去，阻止后续继续运行
			ExceptionUtils.wrappException(e);
		}
	}

	private IExceptionHandler getExceptionHandler(boolean showErrorDlg) {
		if (this.exceptionHandler == null) {
			this.exceptionHandler = new PubExceptionHanler(super.getContext(),
					showErrorDlg);
		}
		return this.exceptionHandler;
	}

	public class DefaultInitDataProcessor implements
			SumIncomeFuncNodeInitDataListener.IInitDataProcessor {

		@Override
		public void process(FuncletInitData data) {
			if (data.getInitData() instanceof SimpleQuerySchemeVO) {
				SumIncomeFuncNodeInitDataListener.this
						.doInitDataByQueryScheme(data);
				return;
			}

			if (data.getInitData() instanceof QSContext) {
				doQSOpenInit(data.getInitData());
				return;
			}

			switch (data.getInitType()) {
			case ILinkType.LINK_TYPE_ADD:
				// 新增打开
				this.doLinkAdd(data);
				return;
			case ILinkType.LINK_TYPE_APPROVE:
				this.doLinkApprove((ILinkApproveData) data.getInitData());
				break;
			case ILinkType.LINK_TYPE_MAINTAIN:
				this.doLinkMaintain((ILinkMaintainData) data.getInitData());
				break;
			case ILinkType.LINK_TYPE_QUERY:
				if (isMultiLinkQueryEnable())
					this.doLinkQuerys(data.getInitData());
				else
					this.doLinkQuery((ILinkQueryData) data.getInitData());
				break;
			case -100:
				this.doBussinessLog((String) data.getInitData());
				break;
			default:
				break;
			}
			if (null != SumIncomeFuncNodeInitDataListener.this
					.getAutoShowUpComponent()) {

				IAutoShowUpComponent autoShowUpComponent = null;

				// 多条数据且列表控件非空，则弹出列表控件显示数据，否则弹出卡片控件显示数据
				Object inData = ((FuncletInitData) data).getInitData();
				if (inData.getClass().isArray()
						&& isMultiLinkQueryEnable()
						&& null != SumIncomeFuncNodeInitDataListener.this
								.getAutoShowUpListComponent()) {
					autoShowUpComponent = SumIncomeFuncNodeInitDataListener.this
							.getAutoShowUpListComponent();
				} else {
					autoShowUpComponent = SumIncomeFuncNodeInitDataListener.this
							.getAutoShowUpComponent();
				}
				autoShowUpComponent.showMeUp();

				if (data.getInitType() == ILinkType.LINK_TYPE_QUERY) {
					highLightSelectedRow(data, autoShowUpComponent);
				}
			}
		}

		protected void highLightSelectedRow(FuncletInitData data,
				IAutoShowUpComponent autoShowUpComponent) {
			BillForm billform = null;
			if (autoShowUpComponent instanceof BillForm) {
				billform = (BillForm) autoShowUpComponent;
			}

			Object inData = ((FuncletInitData) data).getInitData();
			int[] rows = null;
			Object obj = null;
			if (inData.getClass().isArray()) {
				ILinkQueryData[] linkQueryDatas = (ILinkQueryData[]) inData;
				obj = linkQueryDatas[0].getUserObject();
			} else {
				obj = ((ILinkQueryData) inData).getUserObject();
			}

			LightBillVO vo = null;
			if (obj instanceof LightBillVO) {
				vo = (LightBillVO) obj;
				if (vo.getRowSet() != null) {
					Set<String> rowNoSet = vo.getRowSet();
					for (String row : rowNoSet) {
						int rowValue = RowLinkQueryUtil.getRowNoByKey(row,
								billform);
						if (rowValue != -1)
							rows = ArrayUtils.add(rows, rowValue);
					}
					if (rows != null && billform != null) {
						// billform.getBillCardPanel().getBillModel().setHighLightRows(rows);
					}
				}
			}
		}

		protected void doBussinessLog(String billPk) {
			Object obj = SumIncomeFuncNodeInitDataListener.this.getService()
					.queryByPk(billPk);
			SumIncomeFuncNodeInitDataListener.this.getModel().initModel(obj);
		}

		protected void doLinkAdd(FuncletInitData data) {
			SumIncomeFuncNodeInitDataListener.this.getModel().initModel(null);
		}

		protected void doLinkApprove(ILinkApproveData approveData) {
			if (getPmodelDataManager() != null) {
				getPmodelDataManager().initModelByPks(
						new String[] { approveData.getBillID() });
			} else {
				Object obj = SumIncomeFuncNodeInitDataListener.this
						.getService().queryByPk(approveData.getBillID());
				SumIncomeFuncNodeInitDataListener.this.getModel()
						.initModel(obj);
			}
		}

		protected void doLinkMaintain(ILinkMaintainData initData) {
			if (getPmodelDataManager() != null) {
				getPmodelDataManager().initModelByPks(
						new String[] { initData.getBillID() });
			} else {
				Object obj = SumIncomeFuncNodeInitDataListener.this
						.getService().queryByPk(initData.getBillID());
				SumIncomeFuncNodeInitDataListener.this.getModel()
						.initModel(obj);
			}
		}

		protected void doLinkQuerys(Object iLinkQueryData) {
			ILinkQueryData[] datas = null;
			if (iLinkQueryData.getClass().isArray()) {
				datas = (ILinkQueryData[]) iLinkQueryData;
			} else {
				datas = new ILinkQueryData[] { (ILinkQueryData) iLinkQueryData };
			}

			if (!(getService() instanceof IQueryServiceWithFuncCodeExt)
					&& !(getService() instanceof IQueryServiceExt))
				throw new IllegalStateException(NCLangRes.getInstance()
						.getStrByID("pubapp_0",
								"SumIncomeFuncNodeInitDataListener-0000")/*
																		 * 配置有误，
																		 * 启用多数据联查时必须注入批量查询接口
																		 * ！
																		 */);
			String[] pks = new String[datas.length];
			for (int i = 0; i < pks.length; i++)
				pks[i] = datas[i].getBillID();
			Object[] objs = null;
			if (getService() instanceof IQueryServiceWithFuncCodeExt)
				objs = ((IQueryServiceWithFuncCodeExt) getService())
						.queryByPksWithFunCode(pks,
								SumIncomeFuncNodeInitDataListener.this
										.getContext().getNodeCode());
			else
				objs = ((IQueryServiceExt) getService()).queryByPk(pks);
			if (objs == null) {
				MessageDialog.showErrorDlg(
						SumIncomeFuncNodeInitDataListener.this.getModel()
								.getContext().getEntranceUI(),
						null,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"pubapp_0", "0pubapp-0337")/*
															 * @ res
															 * "没有查看该单据的权限"
															 */);
			}
			if (getPmodelDataManager() != null)
				getPmodelDataManager().initModelByPks(
						objs == null ? new String[] {} : pks);
			else
				SumIncomeFuncNodeInitDataListener.this.getModel().initModel(
						objs);
		}

		protected void doLinkQuery(ILinkQueryData iLinkQueryData) {

			AggSumIncomeTaxVO obj = null;
			if (SumIncomeFuncNodeInitDataListener.this.getService() instanceof IQueryServiceWithFunCode
					&& SumIncomeFuncNodeInitDataListener.this.getContext() != null) {
				IQueryServiceWithFunCode serv = (IQueryServiceWithFunCode) SumIncomeFuncNodeInitDataListener.this
						.getService();
				obj = (AggSumIncomeTaxVO) serv.queryByPkWithFunCode(
						iLinkQueryData.getBillID(),
						SumIncomeFuncNodeInitDataListener.this.getContext()
								.getNodeCode());
			} else {
				obj = (AggSumIncomeTaxVO) SumIncomeFuncNodeInitDataListener.this
						.getService().queryByPk(iLinkQueryData.getBillID());

			}

			if (null != iLinkQueryData
					&& null != iLinkQueryData.getBillType()
					&& iLinkQueryData.getBillType().equals(
							SumIncomeTaxLinkQueryAction.ENTRACE_TYPE)) {
				// 从申报明细档进行联查时,进行解密处理
				obj=salaryDecrypt(obj);
			}
			if (null == obj) {
				MessageDialog.showErrorDlg(
						SumIncomeFuncNodeInitDataListener.this.getModel()
								.getContext().getEntranceUI(),
						null,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"pubapp_0", "0pubapp-0337")/*
															 * @ res
															 * "没有查看该单据的权限"
															 */);
			}

			if (getPmodelDataManager() != null)
				getPmodelDataManager()
						.initModelByPks(
								obj != null ? new String[] { iLinkQueryData
										.getBillID() } : new String[] {});
			else
				SumIncomeFuncNodeInitDataListener.this.getModel()
						.initModel(obj);
		}

	}

	private void doQSOpenInit(Object initData) {
		QSContext qscontext = (QSContext) initData;
		String billID = qscontext.getBillid();
		String billType = qscontext.getBilltype();

		// 通用实现使用QSService
		if (getQsservice() != null) {
			try {
				Object obj = getQsservice().queryBill(qscontext);
				getModel().initModel(obj);
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		} else if (qscontext.getQstype().equalsIgnoreCase("fd")) {
			// qs信息，第二部分为fd，打开单据
			// 流程单据初始化需要单据id,type不等于空
			if (billID != null && billType != null) {
				try {
					if (getPmodelDataManager() != null) {
						getPmodelDataManager().initModelByPks(
								new String[] { billID });
					} else {
						Object obj = getService().queryByPk(billID);
						SumIncomeFuncNodeInitDataListener.this.getModel()
								.initModel(obj);
					}
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 对数据进行解密
	 * 
	 * @author Ares.Tank
	 * @param bills
	 * @return
	 */
	private AggSumIncomeTaxVO salaryDecrypt(AggSumIncomeTaxVO vo) {
		if (vo == null ) {
			return vo;
		}

		if (vo != null) {
			if (vo.getParentVO() != null) {
				if (vo.getParentVO().getTaxbase() != null) {
					vo.getParentVO().setTaxbase(
							new UFDouble(SalaryDecryptUtil.decrypt(vo
									.getParentVO().getTaxbase().getDouble())));
				}

				if (vo.getParentVO().getTaxbaseadjust() != null) {
					vo.getParentVO().setTaxbaseadjust(
							new UFDouble(SalaryDecryptUtil.decrypt(vo
									.getParentVO().getTaxbaseadjust()
									.getDouble())));
				}

				if (vo.getParentVO().getCacu_value() != null) {
					vo.getParentVO()
							.setCacu_value(
									new UFDouble(SalaryDecryptUtil.decrypt(vo
											.getParentVO().getCacu_value()
											.getDouble())));
				}

				if (vo.getParentVO().getCacu_valueadjust() != null) {
					vo.getParentVO().setCacu_valueadjust(
							new UFDouble(SalaryDecryptUtil.decrypt(vo
									.getParentVO().getCacu_valueadjust()
									.getDouble())));
				}

				if (vo.getParentVO().getNetincome() != null) {
					vo.getParentVO()
							.setNetincome(
									new UFDouble(SalaryDecryptUtil.decrypt(vo
											.getParentVO().getNetincome()
											.getDouble())));
				}

				if (vo.getParentVO().getPickedup() != null) {
					vo.getParentVO().setPickedup(
							new UFDouble(SalaryDecryptUtil.decrypt(vo
									.getParentVO().getPickedup().getDouble())));
				}

				if (vo.getParentVO().getPickedupadjust() != null) {
					vo.getParentVO().setPickedupadjust(
							new UFDouble(SalaryDecryptUtil.decrypt(vo
									.getParentVO().getPickedupadjust()
									.getDouble())));
				}
			}

			CIncomeTaxVO[] chVOs = (CIncomeTaxVO[]) vo
					.getChildren(CIncomeTaxVO.class);
			vo.getAllChildrenVO();
			vo.getChildrenVO();

			if (chVOs != null && chVOs.length > 0) {
				for (CIncomeTaxVO chVO : chVOs) {
					if (chVO.getTaxbase() != null) {
						chVO.setTaxbase(new UFDouble(SalaryDecryptUtil
								.decrypt(chVO.getTaxbase().getDouble())));
					}
					if (chVO.getCacu_value() != null) {
						chVO.setCacu_value(new UFDouble(SalaryDecryptUtil
								.decrypt(chVO.getCacu_value().getDouble())));
					}
					if (chVO.getNetincome() != null) {
						chVO.setNetincome(new UFDouble(SalaryDecryptUtil
								.decrypt(chVO.getNetincome().getDouble())));
					}
					if (chVO.getPickedup() != null) {
						chVO.setPickedup(new UFDouble(SalaryDecryptUtil
								.decrypt(chVO.getPickedup().getDouble())));
					}
				}
			}

		}

		return vo;
	}

}
