package nc.ui.qcco.task.ace.handler;

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
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.vo.pub.BusinessException;
import nc.vo.qcco.commission.CommissionRVO;

import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;

public class AceBodyAfterEditHandler implements IAppEventHandler<CardBodyAfterEditEvent> {
	private ShowUpableBillForm grandCard;//mainBillForm
	private BillForm mainBillForm;//
	public BillForm getMainBillForm() {
		return mainBillForm;
	}

	public void setMainBillForm(BillForm mainBillForm) {
		this.mainBillForm = mainBillForm;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		if ("samplequantity".equals(e.getKey())) {
			String pk_commission_h = getMainBillForm().getBillCardPanel().getHeadItem("pk_commission_h").getValue();
			if(null == pk_commission_h){
				return;
			}
			IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			try {
				List<Map<String, Object>> sunlist = (List<Map<String, Object>>) query
						.executeQuery(
								"select b.PK_SAMPLEGROUP,b.ENTERPRISESTANDARD,b.PRODUCTSPEC,b.PRODUCTSTAGE,"
								+ "b.STRUCTURETYPE,p.NC_SAMPLE_NAME from qc_commission_b b "
								+ "left join NC_SAMPLE_GROUP p on p.PK_SAMPLE_GROUP = b.PK_SAMPLEGROUP" 
								+ " where b.PK_COMMISSION_H='"+pk_commission_h+"' ",
								new MapListProcessor());
				if(sunlist.size() > 0){
					for(Map<String, Object> map : sunlist){
						if (e.getOldValue() == null) {
							try {
								generateGrandLines(map);
							} catch (BusinessException ex) {
								Logger.error(ex.getMessage());
							}
						}
						
					}
				}
			} catch (BusinessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	@SuppressWarnings("unchecked")
	private void generateGrandLines(Map<String, Object> map) throws BusinessException {
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		List<Map<String, Object>> refList = (List<Map<String, Object>>) query
				.executeQuery(
						"SELECT distinct trim(p.nc_analysis_name) nc_analysis_name, p.pk_result_type, trim(r.nc_result_code) nc_result_code, "
								+ "trim(r.nc_result_namecn) nc_result_namecn, p.pk_test_init, trim(p.test_init_code) test_init_code, trim(p.test_init_name) test_init_name, u.pk_units_type, "
								+ "trim(u.nc_units_code) nc_units_code, trim(u.nc_units_name) nc_units_name, p.nc_max_value, "
								+ "p.nc_min_value, p.nc_stage from NC_TEST_INIT p "
								+ " INNER JOIN NC_UNITS_TYPE u ON u.pk_units_type = p.pk_units_type "
								+ " INNER JOIN NC_RESULT_TYPE r ON r.pk_result_type = p.pk_result_type "
								+ "where p.nc_enstard = '"
								+ map.get("enterprisestandard")
								+ "'  and p.nc_sample_point = '"
								+ map.get("productspec")
								+ "'  and ' ' || p.NC_COIL_TYPE || ',' like '% "
								+ map.get("structuretype")
								+ ",%'    and p.nc_coil_current = '"
								+ map.get("structuretype")
								+ "'    and p.nc_stage = '"
								+ map.get("productstage") + "'",
						new MapListProcessor());
		if (refList != null && refList.size() > 0) {
			for (Map<String, Object> refRow : refList) {
				this.getGrandCard().getBillCardPanel().getBodyPanel().addLine();
				int row = this.getGrandCard().getBillCardPanel().getRowCount() - 1;
				this.getGrandCard()
						.getBillCardPanel()
						.setBodyValueAt(map.get("nc_sample_name"), row,
								"samplegroup");
				this.getGrandCard()
						.getBillCardPanel()
						.setBodyValueAt(map.get("pk_samplegroup"), row,
								"pk_samplegroup");
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
						this.getGrandCard().getBillCardPanel().setBodyValueAt(refValue.getValue(), row, "pk_testtemperature");
					} else if (refValue.getKey().equals("nc_units_name")) {
						this.getGrandCard().getBillCardPanel().setBodyValueAt(refValue.getValue(), row, "pk_unit");
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
