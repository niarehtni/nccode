package nc.bs.hrsms.ta.sss.credit.common;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JFrame;

import nc.bs.hrss.pub.tool.CommonUtil;
import nc.hr.utils.ResHelper;
import nc.vo.hr.tools.pub.GeneralVO;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CreditCardRecordExcelExportUtils {
	
	
	public CreditCardRecordExcelExportUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public String exportCardRecordExcelFile(String fileName,
			HashMap<String, TreeMap<String, GeneralVO>> psnDateMap) throws Exception {
		
		if(psnDateMap.isEmpty()){
			
			CommonUtil.showErrorDialog(ResHelper.getString("coco_ta", "coco_cre_res001")/*无数据*/);
		}
		
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		
		java.io.File file = new java.io.File(fileName);
		if (file.exists())
			file.delete();
		String path = CommonUtil.getExportDir(true).getAbsolutePath() + File.separator + fileName;
	    
		FileOutputStream fileOut = null;
		fileOut = new FileOutputStream(path);
		
		try{
			
			Workbook wb = fileName.endsWith(".xls")?new HSSFWorkbook():new XSSFWorkbook();// 建立新HSSFWorkbook对象
			
			Iterator itor = psnDateMap.entrySet().iterator();
			
			while(itor.hasNext()){
				
				Map.Entry<String, Map> entry = (Entry<String, Map>) itor.next();
				
				String sheetName = entry.getKey().toString().substring(entry.getKey().toString().indexOf("-")+1);
				
				Sheet sheet = wb.createSheet(sheetName);// 建立新的sheet对象	

				/**  创建标题行 */
				Row row = sheet.createRow(0);
				
				Cell cell = row.createCell(0);
				cell.setCellValue(createRichTextString(fileName,ResHelper.getString("coco_ta", "coco_cre_res002")/*考勤卡号*/));
				cell = row.createCell(1);
				cell.setCellValue(createRichTextString(fileName,ResHelper.getString("coco_ta", "coco_cre_res003")/*日期*/));
				cell = row.createCell(2);
				cell.setCellValue(createRichTextString(fileName,ResHelper.getString("coco_ta", "coco_cre_res004")/*上班刷卡*/));
				cell = row.createCell(3);
				cell.setCellValue(createRichTextString(fileName,ResHelper.getString("coco_ta", "coco_cre_res005")/*下班刷卡*/));
				
				Iterator it = entry.getValue().entrySet().iterator();
				
				int i = 0;
				
				while(it.hasNext()){
					
					Map.Entry<String,GeneralVO> entr = (Entry) it.next();
					
					i++;
					row = sheet.createRow(i);
					
					String timecardid = entr.getValue().getAttributeValue("timecardid")==null?"":entr.getValue().getAttributeValue("timecardid").toString();
					String date = entr.getValue().getAttributeValue("date")==null?"":entr.getValue().getAttributeValue("date").toString();
					String begintime = entr.getValue().getAttributeValue("begintime")==null?"":entr.getValue().getAttributeValue("begintime").toString();
					String endtime = entr.getValue().getAttributeValue("endtime")==null?"":entr.getValue().getAttributeValue("endtime").toString();
					
					cell = row.createCell(0);
					cell.setCellValue(createRichTextString(fileName,timecardid));
					cell = row.createCell(1);
					cell.setCellValue(createRichTextString(fileName,date));
					cell = row.createCell(2);
					cell.setCellValue(createRichTextString(fileName,begintime));
					cell = row.createCell(3);
					cell.setCellValue(createRichTextString(fileName,endtime));
			
				}
			}
			
			wb.write(fileOut);// 把Workbook对象输出到文件workbook.xls中
			fileOut.close();
			return CommonUtil.getExportPath() + "/" + fileName;
			
		} catch (Exception e) {
			if (fileOut != null) {
				fileOut.close();
			}
			throw e;
		} finally {
			try {
				if (fileOut != null) {
					fileOut.close();
				}
			} catch (Exception e) {
				throw e;
			}

		}
	}

	private RichTextString createRichTextString(String fileName,String text) {		
		return fileName.endsWith(".xls")?new HSSFRichTextString(text):new XSSFRichTextString(text);
	}
}
