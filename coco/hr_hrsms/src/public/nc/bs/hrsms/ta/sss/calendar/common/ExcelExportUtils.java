package nc.bs.hrsms.ta.sss.calendar.common;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import nc.bs.hrss.pub.tool.CommonUtil;
import nc.hr.utils.ResHelper;
import nc.ui.ta.pub.IColorConst;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.PublicLangRes;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 导出EXCEL工具类
 * @author shaochj 【太平鸟项目】
 * 
 */
public class ExcelExportUtils {

	// 周数组
		private static String[] WEEKARRAY = new String[] {
			ResHelper.getString("c_ta-res", "0c_ta-res0131")/* @ res "周日"*/, 
			ResHelper.getString("c_ta-res", "0c_ta-res0132")/* @ res "周一"*/,
			ResHelper.getString("c_ta-res", "0c_ta-res0133")/* @ res "周二"*/,
			ResHelper.getString("c_ta-res", "0c_ta-res0134")/* @ res "周三"*/,
			ResHelper.getString("c_ta-res", "0c_ta-res0135")/* @ res "周四"*/, 
			ResHelper.getString("c_ta-res", "0c_ta-res0136")/* @ res "周五"*/,
			ResHelper.getString("c_ta-res", "0c_ta-res0137")/* @ res "周六"*/};

		public ExcelExportUtils() {

		}
		
		public String exportCalendarExcelFile(String deptName, String fileName, GeneralVO[] psndocVOs, UFLiteralDate[] dates, String[][] workCalendars)
				throws Exception {
			if (StringUtils.isEmpty(fileName)) {
				return null;
			}
			java.io.File file = new java.io.File(fileName);
			if (file.exists())
				file.delete();
			String path = CommonUtil.getExportDir(true).getAbsolutePath() + File.separator + fileName;
			FileOutputStream fileOut = null;
			fileOut = new FileOutputStream(path);
			try {

				Workbook wb = fileName.endsWith(".xls")?new HSSFWorkbook():new XSSFWorkbook();// 建立新HSSFWorkbook对象
				Sheet sheet = wb.createSheet();// 建立新的sheet对象
				/**  设置excel的标题样式 */			
				CellStyle titelStyle = wb.createCellStyle();// 创建一个style
				Font littleFont0 = wb.createFont();// 创建一个Font
				littleFont0.setFontName("SimSun");
				littleFont0.setFontHeightInPoints((short) 10);
				titelStyle.setFont(littleFont0);// 设置字体
				titelStyle.setAlignment(CellStyle.ALIGN_CENTER);// 水平居中
				titelStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直居中
				/**  创建标题行 */
				Row row = sheet.createRow(0);
				Cell cell = row.createCell(2);
				cell.setCellValue(createRichTextString(fileName,"工作日历设置"));
				cell.setCellStyle(titelStyle);
				
				row = sheet.createRow(1);
				cell = row.createCell(0);
				sheet.addMergedRegion(new CellRangeAddress(1,  1, 0, 20));
				String text = "部门:"+deptName+" 导出异常颜色提示：数据格式不正确 ■ 、同一人有多条记录 ■ 、班次名称错误 ■ 、找不到对应的人员编码 ■ 、文件中排班冲突 ■ 、与已有排班冲突 ■ ";
				//ResHelper.getString(, deptName);
				
				
				String err0 = "数据格式不正确" + " ";
				String err1 = "同一人有多条记录" + " ";
				String err2 = "班次名称错误" + " ";
				String err3 = "找不到对应的人员编码" + " ";
				String err4 = "文件中排班冲突" + " ";
				String err5 = "与已有排班冲突" + " ";

				int index0 = text.indexOf(err0) + err0.length();
				int index1 = text.indexOf(err1) + err1.length();
				int index2 = text.indexOf(err2) + err2.length();
				int index3 = text.indexOf(err3) + err3.length();
				int index4 = text.indexOf(err4) + err4.length();
				int index5 = text.indexOf(err5) + err5.length();

				Font littleFontData = wb.createFont();// 创建一个Font
				littleFontData.setFontName("SimSun");
				littleFontData.setFontHeightInPoints((short) 12);
				littleFontData.setColor(getColor(IColorConst.COLOR_EXPORT_DATAERROR));

				Font littleFontBrown = wb.createFont();// 创建一个Font
				littleFontBrown.setFontName("SimSun");
				littleFontBrown.setFontHeightInPoints((short) 12);
				littleFontBrown.setColor(getColor(IColorConst.COLOR_EXPORT_SAMERECORD));

				Font littleFontRed = wb.createFont();// 创建一个Font
				littleFontRed.setFontName("SimSun");
				littleFontRed.setFontHeightInPoints((short) 12);
				littleFontRed.setColor(getColor(IColorConst.COLOR_EXPORT_CLASSERROR));

				Font littleFontBlue = wb.createFont();// 创建一个Font
				littleFontBlue.setFontName("SimSun");
				littleFontBlue.setFontHeightInPoints((short) 12);
				littleFontBlue.setColor(getColor(IColorConst.COLOR_EXPORT_CODENOTFOUND));

				Font littleFontPink = wb.createFont();// 创建一个Font
				littleFontPink.setFontName("SimSun");
				littleFontPink.setFontHeightInPoints((short) 12);
				littleFontPink.setColor(getColor(IColorConst.COLOR_EXPORT_MUTEXINFILE));

				Font littleFontOrange = wb.createFont();// 创建一个Font
				littleFontOrange.setFontName("SimSun");
				littleFontOrange.setFontHeightInPoints((short) 12);
				littleFontOrange.setColor(getColor(IColorConst.COLOR_EXPORT_MUTEXINDB));

				RichTextString aXSSFRichTextString = createRichTextString(fileName,text);
				aXSSFRichTextString.applyFont(index0, index0 + 1, littleFontData);
				aXSSFRichTextString.applyFont(index1, index1 + 1, littleFontBrown);
				aXSSFRichTextString.applyFont(index2, index2 + 1, littleFontRed);
				aXSSFRichTextString.applyFont(index3, index3 + 1, littleFontBlue);
				aXSSFRichTextString.applyFont(index4, index4 + 1, littleFontPink);
				aXSSFRichTextString.applyFont(index5, index5 + 1, littleFontOrange);
				cell.setCellValue(aXSSFRichTextString);

				CellStyle cs = wb.createCellStyle();// 创建一个style
				Font littleFont = wb.createFont();// 创建一个Font
				littleFont.setFontName("SimSun");
				littleFont.setFontHeightInPoints((short) 10);
				cs.setFont(littleFont);// 设置字体
				cs.setAlignment(CellStyle.ALIGN_CENTER);// 水平居中
				cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直居中
				cs.setBorderBottom(CellStyle.BORDER_THIN);// 下边框
				cs.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
				cs.setBorderRight(CellStyle.BORDER_THIN);// 右边框
				cs.setBorderTop(CellStyle.BORDER_THIN);// 上边框
				cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cs.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);

				CellStyle cs2 = wb.createCellStyle();// 创建一个style
				Font littleFont2 = wb.createFont();// 创建一个Font
				littleFont2.setFontName("SimSun");
				littleFont2.setFontHeightInPoints((short) 10);
				cs2.setFont(littleFont);// 设置字体
				cs2.setAlignment(CellStyle.ALIGN_CENTER);// 水平居中
				cs2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直居中
				cs2.setBorderBottom(CellStyle.BORDER_THIN);// 下边框
				cs2.setBorderLeft(CellStyle.BORDER_THIN);// 左边框
				cs2.setBorderRight(CellStyle.BORDER_THIN);// 右边框
				cs2.setBorderTop(CellStyle.BORDER_THIN);// 上边框

				row = sheet.createRow(2);
				
				cell = row.createCell(0);
				cell.setCellValue(createRichTextString(fileName,PublicLangRes.EMPNO()));
				cell.setCellStyle(cs);
				sheet.createRow(3).createCell(0).setCellStyle(cs);
				sheet.createRow(4).createCell(0).setCellStyle(cs);
				sheet.addMergedRegion(new CellRangeAddress(2,  4, 0, 0));//增加星期显示

				cell = row.createCell(1);
				cell.setCellValue(createRichTextString(fileName,PublicLangRes.PSNCODE()));
				cell.setCellStyle(cs);
				sheet.getRow(3).createCell(1).setCellStyle(cs);
				sheet.getRow(4).createCell(1).setCellStyle(cs);
				sheet.addMergedRegion(new CellRangeAddress(2,  4, 1, 1));//增加星期显示

				cell = row.createCell(2);
				cell.setCellValue(createRichTextString(fileName,PublicLangRes.NAME2()));
				cell.setCellStyle(cs);
				sheet.getRow(3).createCell(2).setCellStyle(cs);
				sheet.getRow(4).createCell(2).setCellStyle(cs);
				sheet.addMergedRegion(new CellRangeAddress(2,  4, 2, 2));
				
				//添加总工时列
				cell = row.createCell(3);
				cell.setCellValue(createRichTextString(fileName,"总工时"));
				cell.setCellStyle(cs);
				sheet.getRow(3).createCell(3).setCellStyle(cs);
				sheet.getRow(4).createCell(3).setCellStyle(cs);
				sheet.addMergedRegion(new CellRangeAddress(2,  4, 3, 3));

				int yearTmp = -1;
				int monthTmp = -1;
				int mergedFrom = 4;
				for (int i = 4; i < dates.length + 4; i++) {
					if (dates[i - 4].getYear() != yearTmp || dates[i - 4].getMonth() != monthTmp) {
						yearTmp = dates[i - 4].getYear();
						monthTmp = dates[i - 4].getMonth();
						if (i != 4) {
							for (int x = mergedFrom; x <= (i - 1); x++) {
								if (x == mergedFrom) {
									cell = sheet.getRow(2).getCell(x);
								} else {
									cell = sheet.getRow(2).createCell(x);
								}
								cell.setCellStyle(cs);
							}
							sheet.addMergedRegion(new CellRangeAddress(2,  2, mergedFrom, (i - 1)));
							mergedFrom = i;
						}
						cell = sheet.getRow(2).createCell(i);
						cell.setCellValue(createRichTextString(fileName,dates[i - 4].toString().substring(0, 7)));
						cell.setCellStyle(cs);

						cell = sheet.getRow(3).createCell(i);
						cell.setCellValue(createRichTextString(fileName,String.valueOf(dates[i - 4].getDay())));
						cell.setCellStyle(cs);
						
						//增加星期显示
						cell = sheet.getRow(4).createCell(i);
						cell.setCellValue(createRichTextString(fileName,  WEEKARRAY[dates[i - 4].getWeek()]));
						cell.setCellStyle(cs);

					} else {
						cell = sheet.getRow(3).createCell(i);
						cell.setCellValue(createRichTextString(fileName,String.valueOf(dates[i - 4].getDay())));
						cell.setCellStyle(cs);
						
						//增加星期显示
						cell = sheet.getRow(4).createCell(i);
						cell.setCellValue(createRichTextString(fileName,  WEEKARRAY[dates[i - 4].getWeek()]));
						cell.setCellStyle(cs);

						if (i == dates.length + 3) {
							for (int x = mergedFrom; x <= (dates.length + 3); x++) {
								if (x == mergedFrom) {
									cell = sheet.getRow(2).getCell(x);
								} else {
									cell = sheet.getRow(2).createCell(x);
								}
								cell.setCellStyle(cs);
							}
							sheet.addMergedRegion(new CellRangeAddress(2, 2, mergedFrom, (dates.length + 3)));
						}
					}
				}

				if (psndocVOs != null && psndocVOs.length > 0) {
					for (int i = 0; i < psndocVOs.length; i++) {
						row = sheet.createRow(i + 5);// 建立新行
						
						for (int j = 0; j < workCalendars[0].length + 4; j++) {
							cell = row.createCell(j);
							cell.setCellStyle(cs2);
							if (j == 0) {
								cell.setCellValue(createRichTextString(fileName,(String)psndocVOs[i].getAttributeValue(WorkCalendarCommonValue.LISTCODE_CLERKCODE)));
							} else if (j == 1) {
								cell.setCellValue(createRichTextString(fileName,(String)psndocVOs[i].getAttributeValue(WorkCalendarCommonValue.LISTCODE_PSNCODE)));
							} else if (j == 2) {
								cell.setCellValue(createRichTextString(fileName,(String)psndocVOs[i].getAttributeValue(WorkCalendarCommonValue.LISTCODE_PSNNAME)));
							}else if(j==3){
								cell.setCellValue(createRichTextString(fileName,(String)psndocVOs[i].getAttributeValue(WorkCalendarCommonValue.LISTCODE_TOTALTIMES)));
							}
							else {
								cell.setCellValue(createRichTextString(fileName,workCalendars[i][j - 4]));
							}

						}
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
		
		

		@SuppressWarnings("rawtypes")
		public static short getColor(Color color) {
			
			if (color == null)
				return -1;

			int diff = Integer.MAX_VALUE;
			Hashtable s_colorIndexHash = (Hashtable) HSSFColor.getMutableIndexHash();
			short minDiffIndex = -1;
			for (Enumeration enumeration = s_colorIndexHash.keys(); enumeration.hasMoreElements();) {
				Integer index = (Integer) enumeration.nextElement();
				HSSFColor aColor = (HSSFColor) s_colorIndexHash.get(index);
				short[] aRgb = aColor.getTriplet();
				int aDiff = (int) (Math.pow(aRgb[0] - color.getRed(), 4) + Math.pow(aRgb[1] - color.getGreen(), 4) + Math
						.pow(aRgb[2] - color.getBlue(), 4));
				if (aDiff == 0) {
					return index.shortValue();
				}
				if (aDiff < diff) {
					diff = aDiff;
					minDiffIndex = index.shortValue();
				}
			}
			return minDiffIndex;
		}
}
