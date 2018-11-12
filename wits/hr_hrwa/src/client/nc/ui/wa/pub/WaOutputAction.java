package nc.ui.wa.pub;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.ui.format.NCFormater;
import nc.ui.hr.uif2.action.print.ExportListAction;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.BillScrollPane.BillTable;
import nc.ui.pub.print.version55.directprint.PrintDirectEntry6;
import nc.ui.pub.print.version55.directprint.PrintDirectSeperator;
import nc.ui.pub.print.version55.directprint.PrintDirectTable;
import nc.ui.pub.print.version55.print.template.cell.style.Style;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.lang.UFTime;

public class WaOutputAction extends ExportListAction{

    @Override
	protected void addTableSection(BillTable[] tables) throws Exception {
		if(tables == null || tables.length == 0)
			return;
		
		PrintDirectTable ptable = null;
		for(BillTable table : tables)
		{
			ptable = convertJTable2PrintTable(table);
			if(ptable != null)
			{
				PrintDirectSeperator seperator = new PrintDirectSeperator();
			    seperator.setWidth(ptable.getTotalWidth());
				getPrint().addSeperator(seperator);
				getPrint().addTableSection(ptable);
			}
		}
	}
	private PrintDirectTable convertJTable2PrintTable(BillTable jtable) throws Exception {
		BillModel billModel = (BillModel)jtable.getModel();
		int rowCount = billModel.getRowCount();
		if (rowCount == 0) {
			return null;
		}
		
		PrintDirectTable pTable = new PrintDirectTable();
		BillItem[] items = billModel.getBodyItems();
		BillItem[] showItems = getShowItems(items);

//		//按照ShowOrder重新冒泡排序
//		for(int i = 0;i < showItems.length; i++){
//			for(int j = i+1;j < showItems.length; j++){
//				BillItem  itemtemp;
//				if(showItems[i].getShowOrder()>showItems[j].getShowOrder()){
//					itemtemp=showItems[j];
//					showItems[j]=showItems[i];
//					showItems[i]=itemtemp;
//				}
//			}
//		}

		// 2015-11-7 zhousze 将map中的所有getShowOrder改为getListShowOrder，这样取数的时候从map中取数才正确 begin
		//按照ShowOrder重新冒泡排序
		for(int i = 0;i < showItems.length; i++){
			for(int j = i+1;j < showItems.length; j++){
				BillItem  itemtemp;
				// 2015-10-29 zhousze 这里的问题是：这里有两个showOrder。一个是showOrder，这个取的是WadataTemplateContainer
				// 中getPreFixBodyVOs中的showOrder，也就是不受显示设置控制的；还有一个是listShowOrder，这个取的是当前显示设置好
				// 的展示顺序。在这里的showItems是排好序的list，但是每个item的showOrder是最开始单据模板的顺序，不是显示设置后的顺
				// 序，冒泡排序后变成了单据模板的默认顺序。所以这里改成根据listShowOrder去冒泡排序才是对的。 begin
				if(showItems[i].getListShowOrder()>showItems[j].getListShowOrder()){
					itemtemp=showItems[j];
					showItems[j]=showItems[i];
					showItems[i]=itemtemp;
				}
				// end
			}
		}
		//guoqt做映射map
		Map<Integer, Integer> itemmap = new HashMap<Integer, Integer>();
		for(int i = 0;i < showItems.length; i++){
			itemmap.put(showItems[i].getShowOrder(),i);
		}
		// 合并表头  CHENYL
		pTable.makeMergerHeadCells(jtable);

		String[] columnNames = new String[showItems.length];
		float[] columnWidth = new float[showItems.length];
		int[] aligns = new int[showItems.length];
		
		//有合计行时
		if (billModel.isNeedCalculate()) {
			rowCount = rowCount + 2;
		}
		
		
		Object[][] data = new Object[rowCount][showItems.length];
		Object value = null;
		
		for(int i = 0;i < rowCount; i++)
		{	
			int currColumnIndex = 0;
			for(int j = 0;j < items.length; j++)
			{
				if(!items[j].isShow()) 
					continue;
				else {
					if (!billModel.isNeedCalculate() || i < rowCount - 2 ) {
						value = billModel.getValueAt(i, j);
					}
					else if (billModel.isNeedCalculate() && i == rowCount - 2 ) {
						// 合计行情况下的倒数第二行
						if (currColumnIndex == 0) {
							value = nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0001146");
						}
						else {
							value = StringUtils.EMPTY;
						}
					}
					else if (billModel.isNeedCalculate() && i == rowCount - 1  && j != 0) {
						// 合计行情况下的倒数第一行
						value = billModel.getTotalTableModel().getValueAt(0, j);
					}
					
					columnNames[currColumnIndex] = jtable.getColumnName(currColumnIndex);
					columnWidth[currColumnIndex] = jtable.getColumnModel().getColumn(currColumnIndex).getWidth();
					if (value == null) {
//						data[i][currColumnIndex++] = null;
						continue;
					}
					

					if( value instanceof UFBoolean)
					{
						if(((UFBoolean)value).booleanValue())
							value = NCLangRes.getInstance().getStrByID("uif2", "AbstractDirectPrintAction-000000")/*是*/;
						else
							value = NCLangRes.getInstance().getStrByID("uif2", "AbstractDirectPrintAction-000001")/*否*/;
					}
					else if(value instanceof Boolean)
					{
						if(((Boolean)value).booleanValue())
							value = NCLangRes.getInstance().getStrByID("uif2", "AbstractDirectPrintAction-000000")/*是*/;
						else
							value = NCLangRes.getInstance().getStrByID("uif2", "AbstractDirectPrintAction-000001")/*否*/;
					}

					// 日期时间类型转换
					if (value instanceof UFDate || value instanceof UFLiteralDate ) {
						value = NCFormater.formatDate(value).getValue();
					}
					if (value instanceof UFDateTime || value instanceof UFTime) {
						value = NCFormater.formatDateTime(value).getValue();
					}
					//guoqt
					if (((showItems[itemmap.get(items[j].getShowOrder())].getDataType() == IBillItem.DECIMAL
							|| showItems[itemmap.get(items[j].getShowOrder())].getDataType() == IBillItem.INTEGER
							|| showItems[itemmap.get(items[j].getShowOrder())].getDataType() == IBillItem.MONEY
							|| showItems[itemmap.get(items[j].getShowOrder())].getDataType() == IBillItem.FRACTION))&& value instanceof UFDouble) {
//		20160412  xiejie3  NCdp205601435  [薪资档案]节点点击“输出”报错
//		原因：减免税和减税比例的显示顺序是一样的，所以	itemmap 中会冲掉一个，导致取减免税 的数据类型的时候，得到的是减税比例的数据类型
//		方案：在此处进行过滤。				
						if(items[j].getDataType()!=IBillItem.BOOLEAN){
							aligns[currColumnIndex] =  Style.RIGHT;
							value = NCFormater.formatNumber(value).getValue();
						}
//						end  NCdp205601435
					}
					else {
						aligns[currColumnIndex] =  Style.LEFT;
					}
					//guoqt
					if(billModel.isNeedCalculate() && i == rowCount - 2 ){
						data[i][currColumnIndex] = convertValue(value, showItems[currColumnIndex], jtable) ;
					}else{
						data[i][itemmap.get(items[j].getShowOrder())] = convertValue(value, showItems[itemmap.get(items[j].getShowOrder())], jtable) ;
					}
					currColumnIndex++;
				} 
			}
		}
		
		pTable.setColumnNames(columnNames, false);
		pTable.setColumnsWidth(columnWidth);
		pTable.setAlignment(aligns);
		pTable.setData(data);
		
		return pTable;
	}
	private BillItem[] getShowItems(BillItem[] items)
	{
		List<BillItem> itemList = new ArrayList<BillItem>();
		for(BillItem item : items)
		{
			if(item.isShow())
				itemList.add(item);
		}
		return itemList.toArray(new BillItem[0]);
	}
	
    @Override
    public void doAction(ActionEvent evt) throws Exception
    {
        initBodyEditors();
        
        PrintDirectEntry6 entry = createPrintDirectEntry();
        
        entry.output();
    }
}
