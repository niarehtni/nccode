package nc.ui.pub.bill;

import nc.vo.pub.lang.UFDouble;

public class CompanyCountBillModel extends BillModel.TotalTableModel {
	public CompanyCountBillModel(BillModel billModel, BillModel model) {
		billModel.super(model);
	}

	private static final long serialVersionUID = 6693056177058643913L;

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		//公司补充保费
		BillItem companyBearBillitem = getMainModel().getItemByKey("company_bear");
		if (companyBearBillitem != null) {
			int index = getMainModel().getColumnIndex(companyBearBillitem);
			if (column == index) {
				aValue = dealRS(aValue, true);
			}
		}
		
		//费基
		BillItem replenisBaseBillitem = getMainModel().getItemByKey("replenis_base");
		if(replenisBaseBillitem!=null){
			int index = getMainModel().getColumnIndex(replenisBaseBillitem);
			if(column == index){
				aValue = dealRS(aValue,false);
			}
		}
		super.setValueAt(aValue , row, column);
	}
	/**
	 * 把小数变成整数
	 * @param aValue
	 * @return
	 */
	private Object dealRS(Object aValue,boolean isScale){
		Object rs = aValue;
		if(aValue!=null && aValue instanceof UFDouble){
			UFDouble aValueUD = (UFDouble)aValue;
			if(aValueUD.doubleValue() < 0){
				aValueUD = UFDouble.ZERO_DBL;
				aValueUD = aValueUD.setScale(0, UFDouble.ROUND_HALF_UP);
			}
			if(isScale){
				aValueUD = aValueUD.setScale(0, UFDouble.ROUND_HALF_UP);
			}
			rs = aValueUD;
		}
		return rs;
	}

}
