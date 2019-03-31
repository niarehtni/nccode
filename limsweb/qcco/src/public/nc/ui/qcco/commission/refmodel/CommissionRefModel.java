package nc.ui.qcco.commission.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class CommissionRefModel extends AbstractRefModel {

	public CommissionRefModel() {
		super();
		init();
	}
	
	private void init(){
	
		setRefNodeName("委托单");
		setRefTitle("委托单");
		setFieldCode(new String[] {
		"billno",
		"billname",
		"pk_commission_h"
				});
		setFieldName(new String[] {
		"委托单编码",
		"委托单名称",
		"主表主键"
				});
		setHiddenFieldCode(new String[] {
		"pk_commission_h",
		"creator",
		"creationtime",
		"modifier",
		"modifiedtime",
		"lastmaketime",
		"dmakedate",
		"pk_group",
		"pk_org",
		"pk_org_v",
		"pk_commissiontype",
		"codeprefix",
		"pk_owner",
		"pk_dept",
		"pk_payer",
		"contract",
		"email",
		"teleno",
		"pk_maincategory",
		"pk_subcategory",
		"pk_lastcategory",
		"cuserid",
		"reportformat",
		"reportlang",
		"managersendflag",
		"taskbeginsendflag",
		"taskendsendflag",
		"reportsendflag",
		"savetotemplateflag",
		"receiptsendflag",
		"quotaionsendflag",
		"testaim",
		"progressneed",
		"sampledealtype",
		"productproperty",
		"customername",
		"customertype",
		"testrequirement",
		"checkingproperty",
		"productline",
		"batchnumber",
		"productdate",
		"batchserial",
		"identificationtype",
		"certificationtype",
		"itemnumber"
			});
		setPkFieldCode("pk_commission_h");
		setWherePart("1=1 and isnull(dr,0)=0");
		setTableName("qc_commission_h");
		setRefCodeField("billno");
		setRefNameField("billname");
	
	}
	
}