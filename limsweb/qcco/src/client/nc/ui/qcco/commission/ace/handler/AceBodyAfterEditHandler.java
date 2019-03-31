package nc.ui.qcco.commission.ace.handler;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pubapp.utils.UserDefineRefUtils;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.qcco.commission.refmodel.EntTypeRefModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.qcco.commission.CommissionBVO;

import org.apache.commons.lang.StringUtils;

public class AceBodyAfterEditHandler implements IAppEventHandler<CardBodyAfterEditEvent> {
	private ShowUpableBillForm grandCard;

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		if ("productserial".equals(e.getKey())) {
			if ((e.getOldValue() != null && !e.getOldValue().equals(e.getValue()))
					&& (e.getValue() != null && !e.getValue().equals(e.getOldValue()))) {
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "pk_enterprisestandard");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "enterprisestandard");

				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "pk_productspec");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "productspec");

				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "pk_structuretype");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "structuretype");

				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "pk_contacttype");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "contacttype");

				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "pk_productstage");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "productstage");

				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "typeno");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "pk_contactbrand");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "contactbrand");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "manufacturer");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "contactmodel");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "pk_samplegroup");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "samplegroup");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "analysisref");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "quantity");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "otherinfo");

				for (int row = this.getGrandCard().getBillCardPanel().getRowCount() - 1; row >= 0; row--) {
					this.getGrandCard().getBillCardPanel().getBodyPanel().delLine(new int[] { row });
				}
			}

			if (e.getSource() instanceof BillCellEditor) {
				BillCellEditor bitem = (BillCellEditor) e.getSource();
				UIRefPane refPane = (UIRefPane) bitem.getComponent();
				if (!StringUtils.isEmpty(refPane.getRefPK())) {
					CommissionBVO bodyVO = new CommissionBVO();
					Vector values = (Vector) refPane.getRefModel().getSelectedData().get(0);
					e.getBillCardPanel().setBodyValueAt(values.get(15), e.getRow(), "pk_enterprisestandard");
					bodyVO.setAttributeValue("enterprisestandard", values.get(15));
					((EntTypeRefModel) ((UIRefPane) e.getBillCardPanel().getBodyItem("enterprisestandard")
							.getComponent()).getRefModel()).setPk_basprod_type((String) e.getValue());
					UserDefineRefUtils.refreshItemRefValue(bodyVO,
							e.getBillCardPanel().getBillTable("pk_commission_b"), e.getRow(), e.getBillCardPanel()
									.getBodyItem("enterprisestandard"), true);

					e.getBillCardPanel().setBodyValueAt(values.get(11), e.getRow(), "pk_productspec");
					bodyVO.setAttributeValue("productspec", values.get(11));
					UserDefineRefUtils.refreshItemRefValue(bodyVO, e.getBillCardPanel().getBodyPanel().getTable(),
							e.getRow(), e.getBillCardPanel().getBodyItem("productspec"), true);

					e.getBillCardPanel().setBodyValueAt(values.get(12), e.getRow(), "pk_structuretype");
					bodyVO.setAttributeValue("structuretype", values.get(12));
					UserDefineRefUtils.refreshItemRefValue(bodyVO, e.getBillCardPanel().getBodyPanel().getTable(),
							e.getRow(), e.getBillCardPanel().getBodyItem("structuretype"), true);

					e.getBillCardPanel().setBodyValueAt(values.get(14), e.getRow(), "pk_productstage");
					bodyVO.setAttributeValue("productstage", values.get(14));
					UserDefineRefUtils.refreshItemRefValue(bodyVO, e.getBillCardPanel().getBodyPanel().getTable(),
							e.getRow(), e.getBillCardPanel().getBodyItem("productstage"), true);

					e.getBillCardPanel().setBodyValueAt(values.get(7), e.getRow(), "pk_productserial");

					IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
					try {
						String analyseName = (String) query.executeQuery(
								"SELECT distinct nc_analysis_name from NC_TEST_INIT p  where p.nc_enstard = '"
										+ values.get(2) + "'  and p.nc_sample_point = '" + values.get(3)
										+ "'  and ' ' || p.NC_COIL_TYPE || ',' like '% " + values.get(4)
										+ ",%'    and p.nc_coil_current = '" + values.get(4)
										+ "'    and p.nc_stage = '" + values.get(6) + "'", new ColumnProcessor());

						if (!StringUtils.isEmpty(analyseName)) {
							e.getBillCardPanel().setBodyValueAt(analyseName, e.getRow(), "analysisref");
						}
					} catch (BusinessException ex) {
						Logger.error(ex.getMessage());
					}
				}
			}
		} else if ("contactbrand".equals(e.getKey())) {
			BillCellEditor bitem = (BillCellEditor) e.getSource();
			UIRefPane refPane = (UIRefPane) bitem.getComponent();
			e.getBillCardPanel().setBodyValueAt(refPane.getRefPK(), e.getRow(), "pk_contactbrand");
		} else if ("samplegroup".equals(e.getKey())) {
			BillCellEditor bitem = (BillCellEditor) e.getSource();
			UIRefPane refPane = (UIRefPane) bitem.getComponent();
			e.getBillCardPanel().setBodyValueAt(refPane.getRefPK(), e.getRow(), "pk_samplegroup");
			if (e.getBillCardPanel().getBodyValueAt(e.getRow(), "analysisref") == null) {
				MessageDialog.showErrorDlg(e.getContext().getEntranceUI(), "错误", "实验前参数不能为空。");
				e.getBillCardPanel().setBodyValueAt(null, e.getRow(), "samplegroup");
				return;
			}
			// 不可重复组别
			for (int i = 0; i < e.getBillCardPanel().getRowCount(); i++) {
				if (e.getRow() != i) {
					if (e.getValue().equals(e.getBillCardPanel().getBodyValueAt(i, "pk_samplegroup"))) {
						MessageDialog.showErrorDlg(e.getContext().getEntranceUI(), "错误", "样品组别 ["
								+ e.getBillCardPanel().getBodyValueAt(i, "samplegroup") + "] 发生重复。");
						e.getBillCardPanel().setBodyValueAt(e.getOldValue(), e.getRow(), "samplegroup");
						e.getBillCardPanel().setBodyValueAt(e.getOldValue(), e.getRow(), "pk_samplegroup");
						CommissionBVO bodyVO = new CommissionBVO();
						bodyVO.setAttributeValue("samplegroup", e.getOldValue());
						UserDefineRefUtils.refreshItemRefValue(bodyVO, e.getBillCardPanel().getBodyPanel().getTable(),
								e.getRow(), e.getBillCardPanel().getBodyItem("samplegroup"), true);
						return;
					}
				}
			}

			if (e.getOldValue() == null) {
				try {
					generateGrandLines(e);
				} catch (BusinessException ex) {
					Logger.error(ex.getMessage());
				}
			} else {
				for (int row = 0; row < this.getGrandCard().getBillCardPanel().getRowCount(); row++) {
					this.getGrandCard()
							.getBillCardPanel()
							.setBodyValueAt(e.getBillCardPanel().getBodyValueAt(e.getRow(), "pk_samplegroup"), row,
									"samplegroup");
					SuperVO vo = (SuperVO) this.getGrandCard().getBillCardPanel().getBillModel()
							.getBodyValueRowVO(row, CommissionBVO.class.getName());
					vo.setAttributeValue("samplegroup",
							e.getBillCardPanel().getBodyValueAt(e.getRow(), "pk_samplegroup"));
					UserDefineRefUtils.refreshItemRefValue(vo,
							this.getGrandCard().getBillCardPanel().getBillTable("pk_commission_r"), row, this
									.getGrandCard().getBillCardPanel().getBodyItem("samplegroup"), true);
					this.getGrandCard()
							.getBillCardPanel()
							.setBodyValueAt(e.getBillCardPanel().getBodyValueAt(e.getRow(), "pk_samplegroup"), row,
									"pk_samplegroup");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void generateGrandLines(CardBodyAfterEditEvent e) throws BusinessException {
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		List<Map<String, Object>> refList = (List<Map<String, Object>>) query
				.executeQuery(
						"select * from (SELECT distinct trim(p.nc_analysis_name) nc_analysis_name, p.pk_result_type, trim(r.nc_result_code) nc_result_code, "
								+ "trim(r.nc_result_namecn) nc_result_namecn, p.pk_test_init, trim(p.test_init_code) test_init_code, trim(p.test_init_name) test_init_name, u.pk_units_type, "
								+ "trim(u.nc_units_code) nc_units_code, trim(u.nc_units_name) nc_units_name, p.nc_max_value, "
								+ "p.nc_min_value, p.nc_stage,cast(p.def1  as int) as def1 from NC_TEST_INIT p "
								+ " INNER JOIN NC_UNITS_TYPE u ON u.pk_units_type = p.pk_units_type "
								+ " INNER JOIN NC_RESULT_TYPE r ON r.pk_result_type = p.pk_result_type "
								+ "where p.nc_enstard = '"
								+ e.getBillCardPanel().getBodyValueAt(e.getRow(), "enterprisestandard")
								+ "'  and p.nc_sample_point = '"
								+ e.getBillCardPanel().getBodyValueAt(e.getRow(), "productspec")
								+ "'  and ' ' || p.NC_COIL_TYPE || ',' like '% "
								+ e.getBillCardPanel().getBodyValueAt(e.getRow(), "structuretype")
								+ ",%'    and p.nc_coil_current = '"
								+ e.getBillCardPanel().getBodyValueAt(e.getRow(), "structuretype")
								+ "'    and p.nc_stage = '"
								+ e.getBillCardPanel().getBodyValueAt(e.getRow(), "productstage")
								+ "' )"
								+ "order by def1", new MapListProcessor());
		if (refList != null && refList.size() > 0) {
			for (Map<String, Object> refRow : refList) {
				this.getGrandCard().getBillCardPanel().getBodyPanel().addLine();
				int row = this.getGrandCard().getBillCardPanel().getRowCount() - 1;
				this.getGrandCard()
						.getBillCardPanel()
						.setBodyValueAt(e.getBillCardPanel().getBodyValueAt(e.getRow(), "samplegroup"), row,
								"samplegroup");
				this.getGrandCard()
						.getBillCardPanel()
						.setBodyValueAt(e.getBillCardPanel().getBodyValueAt(e.getRow(), "pk_samplegroup"), row,
								"pk_samplegroup");
				this.getGrandCard().getBillCardPanel().setBodyValueAt(UFBoolean.TRUE, row, "judgeflag");
				this.getGrandCard().getBillCardPanel().setBodyValueAt(UFBoolean.TRUE, row, "testflag");
				String resultCode = "";
				String resultName = "";
				String refCode = "";
				String refName = "";
				for (Entry<String, Object> refValue : refRow.entrySet()) {
					if (refValue.getKey().equals("nc_analysis_name")) {
						this.getGrandCard().getBillCardPanel().setBodyValueAt(refValue.getValue(), row, "analysisname");
					} else if (refValue.getKey().equals("pk_test_init")) {
						this.getGrandCard().getBillCardPanel().setBodyValueAt(refValue.getValue(), row, "pk_component");
					} else if (refValue.getKey().equals("test_init_code")) {
						refCode = (String) refValue.getValue();
					} else if (refValue.getKey().equals("test_init_name")) {
						refName = (String) refValue.getValue();
					} else if (refValue.getKey().equals("pk_result_type")) {
						this.getGrandCard().getBillCardPanel().setBodyValueAt(refValue.getValue(), row, "pk_valuetype");
					} else if (refValue.getKey().equals("nc_result_code")) {
						resultCode = (String) refValue.getValue();
					} else if (refValue.getKey().equals("nc_result_namecn")) {
						resultName = (String) refValue.getValue();
					} else if (refValue.getKey().equals("nc_max_value")) {
						this.getGrandCard().getBillCardPanel().setBodyValueAt(refValue.getValue(), row, "stdmaxvalue");
					} else if (refValue.getKey().equals("nc_min_value")) {
						this.getGrandCard().getBillCardPanel().setBodyValueAt(refValue.getValue(), row, "stdminvalue");
					} else if (refValue.getKey().equals("nc_stage")) {
						this.getGrandCard().getBillCardPanel().setBodyValueAt(refValue.getValue(), row, "productstage");
					} else if (refValue.getKey().equals("nc_units_name")) {
						this.getGrandCard().getBillCardPanel().setBodyValueAt(refValue.getValue(), row, "unitname");
					}
				}
				if (!StringUtils.isEmpty(resultCode) && !StringUtils.isEmpty(resultName)) {
					IConstEnum aValue = new DefaultConstEnum(resultName, resultName);
					this.getGrandCard().getBillCardPanel().setBodyValueAt(aValue.getValue(), row, "valuetype");
				}

				if (!StringUtils.isEmpty(refCode) && !StringUtils.isEmpty(refName)) {
					IConstEnum aValue = new DefaultConstEnum(refName, refName);
					this.getGrandCard().getBillCardPanel().setBodyValueAt(aValue.getValue(), row, "component");
				}
			}
		}
	}

	public ShowUpableBillForm getGrandCard() {
		return grandCard;
	}

	public void setGrandCard(ShowUpableBillForm grandCard) {
		this.grandCard = grandCard;
	}
}
