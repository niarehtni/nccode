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
 * ����EXCEL������
 * @author shaochj ��̫ƽ����Ŀ��
 * 
 */
public class ExcelExportUtils {

	// ������
		private static String[] WEEKARRAY = new String[] {
			ResHelper.getString("c_ta-res", "0c_ta-res0131")/* @ res "����"*/, 
			ResHelper.getString("c_ta-res", "0c_ta-res0132")/* @ res "��һ"*/,
			ResHelper.getString("c_ta-res", "0c_ta-res0133")/* @ res "�ܶ�"*/,
			ResHelper.getString("c_ta-res", "0c_ta-res0134")/* @ res "����"*/,
			ResHelper.getString("c_ta-res", "0c_ta-res0135")/* @ res "����"*/, 
			ResHelper.getString("c_ta-res", "0c_ta-res0136")/* @ res "����"*/,
			ResHelper.getString("c_ta-res", "0c_ta-res0137")/* @ res "����"*/};

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

				Workbook wb = fileName.endsWith(".xls")?new HSSFWorkbook():new XSSFWorkbook();// ������HSSFWorkbook����
				Sheet sheet = wb.createSheet();// �����µ�sheet����
				/**  ����excel�ı�����ʽ */			
				CellStyle titelStyle = wb.createCellStyle();// ����һ��style
				Font littleFont0 = wb.createFont();// ����һ��Font
				littleFont0.setFontName("SimSun");
				littleFont0.setFontHeightInPoints((short) 10);
				titelStyle.setFont(littleFont0);// ��������
				titelStyle.setAlignment(CellStyle.ALIGN_CENTER);// ˮƽ����
				titelStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// ��ֱ����
				/**  ���������� */
				Row row = sheet.createRow(0);
				Cell cell = row.createCell(2);
				cell.setCellValue(createRichTextString(fileName,"������������"));
				cell.setCellStyle(titelStyle);
				
				row = sheet.createRow(1);
				cell = row.createCell(0);
				sheet.addMergedRegion(new CellRangeAddress(1,  1, 0, 20));
				String text = "����:"+deptName+" �����쳣��ɫ��ʾ�����ݸ�ʽ����ȷ �� ��ͬһ���ж�����¼ �� ��������ƴ��� �� ���Ҳ�����Ӧ����Ա���� �� ���ļ����Ű��ͻ �� ���������Ű��ͻ �� ";
				//ResHelper.getString(, deptName);
				
				
				String err0 = "���ݸ�ʽ����ȷ" + " ";
				String err1 = "ͬһ���ж�����¼" + " ";
				String err2 = "������ƴ���" + " ";
				String err3 = "�Ҳ�����Ӧ����Ա����" + " ";
				String err4 = "�ļ����Ű��ͻ" + " ";
				String err5 = "�������Ű��ͻ" + " ";

				int index0 = text.indexOf(err0) + err0.length();
				int index1 = text.indexOf(err1) + err1.length();
				int index2 = text.indexOf(err2) + err2.length();
				int index3 = text.indexOf(err3) + err3.length();
				int index4 = text.indexOf(err4) + err4.length();
				int index5 = text.indexOf(err5) + err5.length();

				Font littleFontData = wb.createFont();// ����һ��Font
				littleFontData.setFontName("SimSun");
				littleFontData.setFontHeightInPoints((short) 12);
				littleFontData.setColor(getColor(IColorConst.COLOR_EXPORT_DATAERROR));

				Font littleFontBrown = wb.createFont();// ����һ��Font
				littleFontBrown.setFontName("SimSun");
				littleFontBrown.setFontHeightInPoints((short) 12);
				littleFontBrown.setColor(getColor(IColorConst.COLOR_EXPORT_SAMERECORD));

				Font littleFontRed = wb.createFont();// ����һ��Font
				littleFontRed.setFontName("SimSun");
				littleFontRed.setFontHeightInPoints((short) 12);
				littleFontRed.setColor(getColor(IColorConst.COLOR_EXPORT_CLASSERROR));

				Font littleFontBlue = wb.createFont();// ����һ��Font
				littleFontBlue.setFontName("SimSun");
				littleFontBlue.setFontHeightInPoints((short) 12);
				littleFontBlue.setColor(getColor(IColorConst.COLOR_EXPORT_CODENOTFOUND));

				Font littleFontPink = wb.createFont();// ����һ��Font
				littleFontPink.setFontName("SimSun");
				littleFontPink.setFontHeightInPoints((short) 12);
				littleFontPink.setColor(getColor(IColorConst.COLOR_EXPORT_MUTEXINFILE));

				Font littleFontOrange = wb.createFont();// ����һ��Font
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

				CellStyle cs = wb.createCellStyle();// ����һ��style
				Font littleFont = wb.createFont();// ����һ��Font
				littleFont.setFontName("SimSun");
				littleFont.setFontHeightInPoints((short) 10);
				cs.setFont(littleFont);// ��������
				cs.setAlignment(CellStyle.ALIGN_CENTER);// ˮƽ����
				cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// ��ֱ����
				cs.setBorderBottom(CellStyle.BORDER_THIN);// �±߿�
				cs.setBorderLeft(CellStyle.BORDER_THIN);// ��߿�
				cs.setBorderRight(CellStyle.BORDER_THIN);// �ұ߿�
				cs.setBorderTop(CellStyle.BORDER_THIN);// �ϱ߿�
				cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cs.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);

				CellStyle cs2 = wb.createCellStyle();// ����һ��style
				Font littleFont2 = wb.createFont();// ����һ��Font
				littleFont2.setFontName("SimSun");
				littleFont2.setFontHeightInPoints((short) 10);
				cs2.setFont(littleFont);// ��������
				cs2.setAlignment(CellStyle.ALIGN_CENTER);// ˮƽ����
				cs2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// ��ֱ����
				cs2.setBorderBottom(CellStyle.BORDER_THIN);// �±߿�
				cs2.setBorderLeft(CellStyle.BORDER_THIN);// ��߿�
				cs2.setBorderRight(CellStyle.BORDER_THIN);// �ұ߿�
				cs2.setBorderTop(CellStyle.BORDER_THIN);// �ϱ߿�

				row = sheet.createRow(2);
				
				cell = row.createCell(0);
				cell.setCellValue(createRichTextString(fileName,PublicLangRes.EMPNO()));
				cell.setCellStyle(cs);
				sheet.createRow(3).createCell(0).setCellStyle(cs);
				sheet.createRow(4).createCell(0).setCellStyle(cs);
				sheet.addMergedRegion(new CellRangeAddress(2,  4, 0, 0));//����������ʾ

				cell = row.createCell(1);
				cell.setCellValue(createRichTextString(fileName,PublicLangRes.PSNCODE()));
				cell.setCellStyle(cs);
				sheet.getRow(3).createCell(1).setCellStyle(cs);
				sheet.getRow(4).createCell(1).setCellStyle(cs);
				sheet.addMergedRegion(new CellRangeAddress(2,  4, 1, 1));//����������ʾ

				cell = row.createCell(2);
				cell.setCellValue(createRichTextString(fileName,PublicLangRes.NAME2()));
				cell.setCellStyle(cs);
				sheet.getRow(3).createCell(2).setCellStyle(cs);
				sheet.getRow(4).createCell(2).setCellStyle(cs);
				sheet.addMergedRegion(new CellRangeAddress(2,  4, 2, 2));
				
				//����ܹ�ʱ��
				cell = row.createCell(3);
				cell.setCellValue(createRichTextString(fileName,"�ܹ�ʱ"));
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
						
						//����������ʾ
						cell = sheet.getRow(4).createCell(i);
						cell.setCellValue(createRichTextString(fileName,  WEEKARRAY[dates[i - 4].getWeek()]));
						cell.setCellStyle(cs);

					} else {
						cell = sheet.getRow(3).createCell(i);
						cell.setCellValue(createRichTextString(fileName,String.valueOf(dates[i - 4].getDay())));
						cell.setCellStyle(cs);
						
						//����������ʾ
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
						row = sheet.createRow(i + 5);// ��������
						
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

				wb.write(fileOut);// ��Workbook����������ļ�workbook.xls��
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
