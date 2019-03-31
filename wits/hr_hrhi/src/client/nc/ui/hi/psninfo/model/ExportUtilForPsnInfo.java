package nc.ui.hi.psninfo.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletVO;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExportUtilForPsnInfo {

	BillTempletVO billTemletVO = null;
	public ExportUtilForPsnInfo(BillTempletVO billTemletVO){
		this.billTemletVO = billTemletVO;
	}
	
	/**
	 * 批量导出信息集到excel文档
	 * 2013-1-16 上午11:06:51
	 * yunana
	 * @param generalVOsMap
	 * @param filePath
	 * @param infosetPks
	 */
	public void exportVOs(HashMap<String, GeneralVO[]> generalVOsMap,String filePath, String infosetPks[]) {
		try {
			HashMap<String,InfoSetVO> metadataVSinfosetvoMap = this.getInfoSetName(infosetPks);
			//根据元数据，获得子集在界面上显示的位置，并以之作为EXCEL表上输出的顺序
			HashMap<String,Integer> locationMap = this.getOrderMap(generalVOsMap, metadataVSinfosetvoMap);
			// 产生工作簿对象
			HSSFWorkbook workbook = new HSSFWorkbook();	
			//创建出足够的页签
			for(int i=0; i<generalVOsMap.size(); i++){
				workbook.createSheet();// 产生工作表对象
			}
			
			Iterator iter = generalVOsMap.entrySet().iterator();
			int mapSize = generalVOsMap.size();
			//int sheetIndext = 0;
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String metaData = (String) entry.getKey();
				GeneralVO[] genvos = (GeneralVO[]) entry.getValue();
				InfoSetVO infosetVO = metadataVSinfosetvoMap.get(metaData);//元数据对应的信息集VO
				int sheetIndext = locationMap.get(metaData);//execl页签位置
				String resid = infosetVO.getResid();//多语id
				String respath = infosetVO.getRespath();//多语路径
				String displayTableName = ResHelper.getString(respath, resid);//页签名称
				//HSSFSheet sheet = workbook.createSheet(); // 产生工作表对象
				HSSFSheet sheet = workbook.getSheetAt(sheetIndext);
				//Excel页签名不能包含"/"
				if(displayTableName.contains("/")){
					String[] strs = displayTableName.split("/");
					StringBuffer sb = new StringBuffer();
					for(int i=0; i<strs.length; i++){
						sb.append(strs[i]);
					}
					displayTableName = sb.toString();
				}
				if(StringUtils.isEmpty(displayTableName)){
					//自定义信息集，没有多语时
					displayTableName = infosetVO.getInfoset_name();
				}
				workbook.setSheetName(sheetIndext, displayTableName);//为指定页签命名
				//sheetIndext++;
				String[] columNames = genvos[0].getAttributeNames();//得到将要输出的信息项
				
				if(metaData!=null && metaData.equals("hrhi.bd_psndoc")){
					this.setHeadNames1(sheet, columNames, infosetPks, metaData);//输出主表表头信息
				}else{
					this.setHeadNames(sheet, columNames, infosetPks, metaData);//输出子表表头信息
				}		
				this.setBodyData(columNames, sheet, genvos);//输出表体信息
				
				FileOutputStream fOut = new FileOutputStream(filePath);
				workbook.write(fOut);
				fOut.flush();
				fOut.close();
				//当数据量过大时进行垃圾回收
				if((mapSize>10&&genvos.length>5000)||genvos.length>20000){
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							System.gc();
						}
					});
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.error(e.getMessage());
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			Logger.error(e.getMessage());
		}
	}
	
	/**
	 * 制作界面上的信息集与其所在的位置的MAP
	 * 2013-2-1 下午04:32:36
	 * yunana
	 * @return
	 */
	private HashMap<String,Integer> getTempletVOOrderMap(){
		BillTabVO[] vos = billTemletVO.getHeadVO().getStructvo().getBillTabVOs();
		HashMap<String,Integer> orderMap = new HashMap<String, Integer>();
		HashSet<Integer> set = new HashSet<Integer>();

		for(int i=0; i<vos.length; i++){
			BillTabVO billVO = vos[i];
			String matedata = billVO.getMetadataclass();
			int tabIndex = billVO.getTabindex()+2;
			if(matedata==null){
				billVO.getMetadataclass();
				continue;
			}
			//当遇到顺序号重复的时候，顺序号递增，一般会在有自定义子集的产品中出现，会影响正常的输出顺序
			while(!set.add(tabIndex)) {
				tabIndex++;
			}
			orderMap.put(matedata, tabIndex);
		}
		orderMap.put("hrhi.bd_psndoc", 0);
		orderMap.put("hrhi.hi_psnorg", 1);
		orderMap.put("hrhi.hi_psnjob", 2);
		return orderMap;
	}
	
	/**
	 * 根据界面上显示的位置对将要输出的子集排序
	 * 逻辑：首先得到元数据的信息集在界面上位置，然后将所有“位置”放进一个数组，对数组进行排序，使“位置”顺序与其在数组中的顺序一致；
	 * 	       之后第二次得到元数据的信息集在界面上位置，然后通过得到的“位置”得到其在数组中的顺序，这个顺序的值即输出在excel页签顺序的值
	 * 	  
	 * 2013-1-16 上午10:40:44
	 * yunana
	 * @param generalVOsMap
	 * @param metadataVSinfosetvoMap
	 * @return HashMap K:元数据属性， V：将要输出的位置
	 */
	private HashMap<String,Integer> getOrderMap(HashMap<String, GeneralVO[]> generalVOsMap,HashMap<String,InfoSetVO> metadataVSinfosetvoMap){
		HashMap<String,Integer> locationMap = new HashMap<String, Integer>();//用于储存数据，并返回
		Iterator iterCopy = generalVOsMap.entrySet().iterator();
		Integer[] orders = new Integer[generalVOsMap.size()];
		int ordersIndex = 0;
		HashMap<String,Integer> templeteVOOrderMap = this.getTempletVOOrderMap();
		//第一次得到元数据的信息集在界面上位置，并将其放进数组
		while(iterCopy.hasNext()){
			Map.Entry entry = (Map.Entry) iterCopy.next();
			String metaData = (String) entry.getKey();
			Integer showOrder = templeteVOOrderMap.get(metaData); //得到在客户端界面上显示的顺序
			orders[ordersIndex] = showOrder;
			ordersIndex++;
		}
		
		Arrays.sort(orders);//排序
		//第二次得到元数据的信息集在界面上位置
		Iterator iter1 = generalVOsMap.entrySet().iterator();
		while(iter1.hasNext()){
			Map.Entry entry = (Map.Entry) iter1.next();
			String metaData = (String) entry.getKey();
			//HashMap<String,Integer> templeteVOOrderMap = this.getTempletVOOrderMap();
			Integer showOrder = templeteVOOrderMap.get(metaData); 
			int newOrder = Arrays.binarySearch(orders,showOrder);//得到“位置”在数组中的顺序值
			locationMap.put(metaData, newOrder);
		}
		return locationMap;
	}
	
	private void setBodyData(String[] columNames,HSSFSheet sheet,GeneralVO[] genvos){
		int notNullIndex = 1; //用来记录不为空的VO序号,并且将之作为输出索引，避免在excel表格中出现空行
		for(int i=0; i<genvos.length; i++){
			if(genvos[i]!=null&&genvos[i].getAttributeValue("pk_psndoc")!=null){
				HSSFRow row = sheet.createRow(notNullIndex);
				for(int k=1; k<columNames.length; k++){
					Object value = genvos[i].getAttributeValue(columNames[k]);
					HSSFCell cell = row.createCell(k-1);
					if(value!=null && value instanceof String){	
						if(value.toString().equals("Y")){
							cell.setCellValue(ResHelper.getString("6007psn", "06007psn0306")/* "是" */);
							continue;
						}
						if(value.toString().equals("N")){
							cell.setCellValue(ResHelper.getString("6007psn", "06007psn0307")/* "否" */);
							continue;
						}
						cell.setCellValue((String)value);
					}else if(value!=null && value instanceof Integer){
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue((Integer)value);
					}else if(value!=null){
						cell.setCellValue(value.toString());
					}else{
						cell.setCellValue(" ");
					}
				}
				notNullIndex++;
			}
		}
	}
	
	private HashMap<String,InfoSetVO> getInfoSetName(String[] pks) throws BusinessException{
		HashMap<String,InfoSetVO> map = new HashMap<String, InfoSetVO>();
//		InSQLCreator isc= new InSQLCreator();
//		String inSql = isc.getInSQL(pks);
		String inSql = getInsql(pks);
		String condition = " pk_infoset  in (" + inSql + ")";
		InfoSetVO[] vos = (InfoSetVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, InfoSetVO.class, condition);
		for (int i = 0; i < vos.length; i++) {
			String meta_data = vos[i].getMeta_data();
			map.put(meta_data, vos[i]);
		}
		return map;
	}
	
	private HashMap<String,InfoItemVO> getItemNameMap(String[] pks) throws BusinessException{
//		InSQLCreator isc= new InSQLCreator();
//		String inSql = isc.getInSQL(pks);
		String inSql = getInsql(pks);
		String condition = " pk_infoset  in (" + inSql + ")";
		InfoItemVO[] vos = (InfoItemVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, InfoItemVO.class, condition);
		HashMap<String,InfoItemVO> metadataItemvoMap = new HashMap<String, InfoItemVO>();
		for(int i=0; i<vos.length; i++){
			//单纯的通过itemCode查找InfoItemVO出现大量重复的情况，所以后续要通过单据模板元数据属性来确定唯一提条InfoItemVO。
			//因为信息集的元数据属性与单据模板的元数据属性不一致，所以这里要对InfoItemVO本身的元数据属性进行重组，用ItemCode代替原来元数据属性中表示字段名称的部分，
			String metadata = vos[i].getMeta_data();
			String itemCode = vos[i].getItem_code();
			//这个判断本来并不需要，但是由于经常出现错误数据！无奈，过滤一下。。。。
			if(metadata==null){
				continue;
			}
			String[] strs = metadata.split("\\.");
			String tablename = strs[0]+"."+strs[1];
			//与InfoItemVO本身的meta_data对应的元数据属性
			String billTempletMeta = tablename+"."+itemCode;
			metadataItemvoMap.put(billTempletMeta, vos[i]);
		}
		return metadataItemvoMap;
	}
	
	public static String getInsql(String[] conditions){
		
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<conditions.length-1; i++){
			sb.append("'");
			sb.append(conditions[i]);
			sb.append("',");
		}
		sb.append("'"+conditions[conditions.length-1]+"'");
		return sb.toString();
	}
	
	private void setHeadNames(HSSFSheet sheet,String[] names,String[] infosetPks,String metaData) throws BusinessException{
		HashMap<String,InfoItemVO> metadataItemvoMap = this.getItemNameMap(infosetPks);
		HSSFRow row = sheet.createRow(0);
		HSSFCell codecell = row.createCell(0);
		HSSFCell namecell = row.createCell(1);
		codecell.setCellValue(ResHelper.getString("6007psn", "2UC000-000053")/*人员编码*/);
		namecell.setCellValue(ResHelper.getString("6007psn", "UC000-0001403")/*姓名*/);
		for(int i=3; i<names.length; i++){
			//当generalVOsMap不为空时
			String metadata1 = metaData+"."+names[i];	
			//当generalVOsMap为空时
			String metadata2 = "hrhi."+names[i];
			InfoItemVO infoItemVO;
			if(metadataItemvoMap.get(metadata1)!=null){
				infoItemVO = metadataItemvoMap.get(metadata1);
			}else{
				infoItemVO = metadataItemvoMap.get(metadata2);
			}
			if(infoItemVO==null){
				//自定义信息项容错处理
				//infoItemVO = metadataItemvoMap.get(metadata2);
				continue;
			}
			//产生单元格
			HSSFCell cell = row.createCell(i-1);
			String resid = infoItemVO.getResid();
			String respath = infoItemVO.getRespath();
			if(respath==null){
				respath = "6007psn";
			}
			String name = ResHelper.getString(respath, resid);
			if(StringUtils.isEmpty(name)){
				//自定义信息集信息项或者预置子集自定义信息项没有多语
				name = infoItemVO.getItem_name();
			}
			cell.setCellValue(name);
		}
	}
	
	private void setHeadNames1(HSSFSheet sheet,String[] names,String[] infosetPks,String metaData) throws BusinessException{
		HashMap<String,InfoItemVO> metadataItemvoMap = this.getItemNameMap(infosetPks);
		HSSFRow row = sheet.createRow(0);
//		HSSFCell codecell = row.createCell(0);
//		HSSFCell namecell = row.createCell(1);
//		codecell.setCellValue(ResHelper.getString("6007psn", "2UC000-000053")/*人员编码*/);
//		namecell.setCellValue(ResHelper.getString("6007psn", "UC000-0001403")/*姓名*/);
		for(int i=1; i<names.length; i++){
			/**自定义项调试代码*/
			/*	if(names[i].startsWith("glbdef")){
				names[i].toString();
			}*/
			
			//当generalVOsMap不为空时
			String metadata1 = metaData+"."+names[i];	
			//当generalVOsMap为空时
			String metadata2 = "hrhi."+names[i];
			InfoItemVO infoItemVO;
			if(metadataItemvoMap.get(metadata1)!=null){
				infoItemVO = metadataItemvoMap.get(metadata1);
			}else if( metadataItemvoMap.get(metadata1)==null && names[i].equals("name2") ){
				infoItemVO = metadataItemvoMap.get("hrhi.bd_psndoc.name");
			}else{
				infoItemVO = metadataItemvoMap.get(metadata2);
			}

			
			//产生单元格
			HSSFCell cell = row.createCell(i-1);
			String resid = infoItemVO.getResid();
			String respath = infoItemVO.getRespath();
			if(respath==null){
				respath = "6007psn";
			}
			String name = ResHelper.getString(respath, resid);
			if(StringUtils.isEmpty(name)){
				//自定义信息集信息项或者预置子集自定义信息项没有多语
				name = infoItemVO.getItem_name();
			}
			cell.setCellValue(name);
		}
	}
}
