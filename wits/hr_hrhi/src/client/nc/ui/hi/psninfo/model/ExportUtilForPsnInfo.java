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
	 * ����������Ϣ����excel�ĵ�
	 * 2013-1-16 ����11:06:51
	 * yunana
	 * @param generalVOsMap
	 * @param filePath
	 * @param infosetPks
	 */
	public void exportVOs(HashMap<String, GeneralVO[]> generalVOsMap,String filePath, String infosetPks[]) {
		try {
			HashMap<String,InfoSetVO> metadataVSinfosetvoMap = this.getInfoSetName(infosetPks);
			//����Ԫ���ݣ�����Ӽ��ڽ�������ʾ��λ�ã�����֮��ΪEXCEL���������˳��
			HashMap<String,Integer> locationMap = this.getOrderMap(generalVOsMap, metadataVSinfosetvoMap);
			// ��������������
			HSSFWorkbook workbook = new HSSFWorkbook();	
			//�������㹻��ҳǩ
			for(int i=0; i<generalVOsMap.size(); i++){
				workbook.createSheet();// �������������
			}
			
			Iterator iter = generalVOsMap.entrySet().iterator();
			int mapSize = generalVOsMap.size();
			//int sheetIndext = 0;
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String metaData = (String) entry.getKey();
				GeneralVO[] genvos = (GeneralVO[]) entry.getValue();
				InfoSetVO infosetVO = metadataVSinfosetvoMap.get(metaData);//Ԫ���ݶ�Ӧ����Ϣ��VO
				int sheetIndext = locationMap.get(metaData);//execlҳǩλ��
				String resid = infosetVO.getResid();//����id
				String respath = infosetVO.getRespath();//����·��
				String displayTableName = ResHelper.getString(respath, resid);//ҳǩ����
				//HSSFSheet sheet = workbook.createSheet(); // �������������
				HSSFSheet sheet = workbook.getSheetAt(sheetIndext);
				//Excelҳǩ�����ܰ���"/"
				if(displayTableName.contains("/")){
					String[] strs = displayTableName.split("/");
					StringBuffer sb = new StringBuffer();
					for(int i=0; i<strs.length; i++){
						sb.append(strs[i]);
					}
					displayTableName = sb.toString();
				}
				if(StringUtils.isEmpty(displayTableName)){
					//�Զ�����Ϣ����û�ж���ʱ
					displayTableName = infosetVO.getInfoset_name();
				}
				workbook.setSheetName(sheetIndext, displayTableName);//Ϊָ��ҳǩ����
				//sheetIndext++;
				String[] columNames = genvos[0].getAttributeNames();//�õ���Ҫ�������Ϣ��
				
				if(metaData!=null && metaData.equals("hrhi.bd_psndoc")){
					this.setHeadNames1(sheet, columNames, infosetPks, metaData);//��������ͷ��Ϣ
				}else{
					this.setHeadNames(sheet, columNames, infosetPks, metaData);//����ӱ��ͷ��Ϣ
				}		
				this.setBodyData(columNames, sheet, genvos);//���������Ϣ
				
				FileOutputStream fOut = new FileOutputStream(filePath);
				workbook.write(fOut);
				fOut.flush();
				fOut.close();
				//������������ʱ������������
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
	 * ���������ϵ���Ϣ���������ڵ�λ�õ�MAP
	 * 2013-2-1 ����04:32:36
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
			//������˳����ظ���ʱ��˳��ŵ�����һ��������Զ����Ӽ��Ĳ�Ʒ�г��֣���Ӱ�����������˳��
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
	 * ���ݽ�������ʾ��λ�öԽ�Ҫ������Ӽ�����
	 * �߼������ȵõ�Ԫ���ݵ���Ϣ���ڽ�����λ�ã�Ȼ�����С�λ�á��Ž�һ�����飬�������������ʹ��λ�á�˳�������������е�˳��һ�£�
	 * 	       ֮��ڶ��εõ�Ԫ���ݵ���Ϣ���ڽ�����λ�ã�Ȼ��ͨ���õ��ġ�λ�á��õ����������е�˳�����˳���ֵ�������excelҳǩ˳���ֵ
	 * 	  
	 * 2013-1-16 ����10:40:44
	 * yunana
	 * @param generalVOsMap
	 * @param metadataVSinfosetvoMap
	 * @return HashMap K:Ԫ�������ԣ� V����Ҫ�����λ��
	 */
	private HashMap<String,Integer> getOrderMap(HashMap<String, GeneralVO[]> generalVOsMap,HashMap<String,InfoSetVO> metadataVSinfosetvoMap){
		HashMap<String,Integer> locationMap = new HashMap<String, Integer>();//���ڴ������ݣ�������
		Iterator iterCopy = generalVOsMap.entrySet().iterator();
		Integer[] orders = new Integer[generalVOsMap.size()];
		int ordersIndex = 0;
		HashMap<String,Integer> templeteVOOrderMap = this.getTempletVOOrderMap();
		//��һ�εõ�Ԫ���ݵ���Ϣ���ڽ�����λ�ã�������Ž�����
		while(iterCopy.hasNext()){
			Map.Entry entry = (Map.Entry) iterCopy.next();
			String metaData = (String) entry.getKey();
			Integer showOrder = templeteVOOrderMap.get(metaData); //�õ��ڿͻ��˽�������ʾ��˳��
			orders[ordersIndex] = showOrder;
			ordersIndex++;
		}
		
		Arrays.sort(orders);//����
		//�ڶ��εõ�Ԫ���ݵ���Ϣ���ڽ�����λ��
		Iterator iter1 = generalVOsMap.entrySet().iterator();
		while(iter1.hasNext()){
			Map.Entry entry = (Map.Entry) iter1.next();
			String metaData = (String) entry.getKey();
			//HashMap<String,Integer> templeteVOOrderMap = this.getTempletVOOrderMap();
			Integer showOrder = templeteVOOrderMap.get(metaData); 
			int newOrder = Arrays.binarySearch(orders,showOrder);//�õ���λ�á��������е�˳��ֵ
			locationMap.put(metaData, newOrder);
		}
		return locationMap;
	}
	
	private void setBodyData(String[] columNames,HSSFSheet sheet,GeneralVO[] genvos){
		int notNullIndex = 1; //������¼��Ϊ�յ�VO���,���ҽ�֮��Ϊ���������������excel����г��ֿ���
		for(int i=0; i<genvos.length; i++){
			if(genvos[i]!=null&&genvos[i].getAttributeValue("pk_psndoc")!=null){
				HSSFRow row = sheet.createRow(notNullIndex);
				for(int k=1; k<columNames.length; k++){
					Object value = genvos[i].getAttributeValue(columNames[k]);
					HSSFCell cell = row.createCell(k-1);
					if(value!=null && value instanceof String){	
						if(value.toString().equals("Y")){
							cell.setCellValue(ResHelper.getString("6007psn", "06007psn0306")/* "��" */);
							continue;
						}
						if(value.toString().equals("N")){
							cell.setCellValue(ResHelper.getString("6007psn", "06007psn0307")/* "��" */);
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
			//������ͨ��itemCode����InfoItemVO���ִ����ظ�����������Ժ���Ҫͨ������ģ��Ԫ����������ȷ��Ψһ����InfoItemVO��
			//��Ϊ��Ϣ����Ԫ���������뵥��ģ���Ԫ�������Բ�һ�£���������Ҫ��InfoItemVO�����Ԫ�������Խ������飬��ItemCode����ԭ��Ԫ���������б�ʾ�ֶ����ƵĲ��֣�
			String metadata = vos[i].getMeta_data();
			String itemCode = vos[i].getItem_code();
			//����жϱ���������Ҫ���������ھ������ִ������ݣ����Σ�����һ�¡�������
			if(metadata==null){
				continue;
			}
			String[] strs = metadata.split("\\.");
			String tablename = strs[0]+"."+strs[1];
			//��InfoItemVO�����meta_data��Ӧ��Ԫ��������
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
		codecell.setCellValue(ResHelper.getString("6007psn", "2UC000-000053")/*��Ա����*/);
		namecell.setCellValue(ResHelper.getString("6007psn", "UC000-0001403")/*����*/);
		for(int i=3; i<names.length; i++){
			//��generalVOsMap��Ϊ��ʱ
			String metadata1 = metaData+"."+names[i];	
			//��generalVOsMapΪ��ʱ
			String metadata2 = "hrhi."+names[i];
			InfoItemVO infoItemVO;
			if(metadataItemvoMap.get(metadata1)!=null){
				infoItemVO = metadataItemvoMap.get(metadata1);
			}else{
				infoItemVO = metadataItemvoMap.get(metadata2);
			}
			if(infoItemVO==null){
				//�Զ�����Ϣ���ݴ���
				//infoItemVO = metadataItemvoMap.get(metadata2);
				continue;
			}
			//������Ԫ��
			HSSFCell cell = row.createCell(i-1);
			String resid = infoItemVO.getResid();
			String respath = infoItemVO.getRespath();
			if(respath==null){
				respath = "6007psn";
			}
			String name = ResHelper.getString(respath, resid);
			if(StringUtils.isEmpty(name)){
				//�Զ�����Ϣ����Ϣ�����Ԥ���Ӽ��Զ�����Ϣ��û�ж���
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
//		codecell.setCellValue(ResHelper.getString("6007psn", "2UC000-000053")/*��Ա����*/);
//		namecell.setCellValue(ResHelper.getString("6007psn", "UC000-0001403")/*����*/);
		for(int i=1; i<names.length; i++){
			/**�Զ�������Դ���*/
			/*	if(names[i].startsWith("glbdef")){
				names[i].toString();
			}*/
			
			//��generalVOsMap��Ϊ��ʱ
			String metadata1 = metaData+"."+names[i];	
			//��generalVOsMapΪ��ʱ
			String metadata2 = "hrhi."+names[i];
			InfoItemVO infoItemVO;
			if(metadataItemvoMap.get(metadata1)!=null){
				infoItemVO = metadataItemvoMap.get(metadata1);
			}else if( metadataItemvoMap.get(metadata1)==null && names[i].equals("name2") ){
				infoItemVO = metadataItemvoMap.get("hrhi.bd_psndoc.name");
			}else{
				infoItemVO = metadataItemvoMap.get(metadata2);
			}

			
			//������Ԫ��
			HSSFCell cell = row.createCell(i-1);
			String resid = infoItemVO.getResid();
			String respath = infoItemVO.getRespath();
			if(respath==null){
				respath = "6007psn";
			}
			String name = ResHelper.getString(respath, resid);
			if(StringUtils.isEmpty(name)){
				//�Զ�����Ϣ����Ϣ�����Ԥ���Ӽ��Զ�����Ϣ��û�ж���
				name = infoItemVO.getItem_name();
			}
			cell.setCellValue(name);
		}
	}
}
