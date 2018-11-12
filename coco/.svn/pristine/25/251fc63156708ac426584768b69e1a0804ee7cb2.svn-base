package nc.bs.hrsms.ta.shift.lsnr;

import nc.uap.lfw.core.combodata.CombItem;
import nc.uap.lfw.core.combodata.DynamicComboDataConf;

public class CalTypeComboLoader extends DynamicComboDataConf{

	private static final long serialVersionUID = -3385214549914317493L;
	
	@Override
	public CombItem[] getAllCombItems() {
		CombItem[] items = new CombItem[2];
		String strs[] = {"系统自动计算旷工工时","记为旷工"};
		for(int i = 0; i < 2; i++){
			CombItem item = new CombItem();
			if(i==0){
				item.setText(strs[0]);
				item.setValue("0");
			}else{
				item.setText(strs[1]);
				item.setValue("1");
			}
			items[i] = item;
		}
		return items;
	}

		
}
