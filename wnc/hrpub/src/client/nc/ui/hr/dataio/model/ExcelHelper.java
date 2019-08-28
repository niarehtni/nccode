package nc.ui.hr.dataio.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.bs.uif2.validation.ValidationFailure;
import nc.itf.hr.dataio.IDataIOHookClient;
import nc.vo.hr.dataio.DataIORes;
import nc.vo.hr.dataio.DataIOResult;
import nc.vo.hr.dataio.DataImportVO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/***************************************************************************
 * <br>
 * Created on 2014-7-21 14:08:02<br>
 * 
 * @author Rocex Wang
 ***************************************************************************/
public class ExcelHelper {
	static CellStyle cellStyle;

	/***************************************************************************
	 * 拷贝文件 <br>
	 * Created on 2014-8-20 10:01:02<br>
	 * 
	 * @param strSourceFilePath
	 *            源文件
	 * @param strTargetFilePath
	 *            目标文件
	 * @author Rocex Wang
	 ***************************************************************************/
	public static void copyFileByChannel(String strSourceFilePath, String strTargetFilePath) {
		FileInputStream fi = null;
		FileOutputStream fo = null;

		FileChannel in = null;
		FileChannel out = null;

		try {
			fi = new FileInputStream(strSourceFilePath);
			fo = new FileOutputStream(strTargetFilePath);

			in = fi.getChannel();// 得到对应的文件通道
			out = fo.getChannel();// 得到对应的文件通道

			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			Logger.error(e.getMessage());
		} finally {
			try {
				IOUtils.closeQuietly(fi);
				IOUtils.closeQuietly(fo);

				if (null != in) {
					in.close();
				}

				if (null != out) {
					out.close();
				}
			} catch (IOException e) {
				Logger.error(e.getMessage());
			}
		}
	}

	private static CellStyle creatCellStyle(HSSFWorkbook workbook, Font font) {
		if (cellStyle == null) {
			cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
			cellStyle.setBorderTop(CellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
			cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		}

		return cellStyle;
	}

	/**
	 * 写单元格
	 * 
	 * @param workbook
	 * @param row
	 *            行
	 * @param iColumnIndex
	 *            列号
	 * @param strValue
	 *            写入值
	 * @param font
	 *            字体
	 */
	public static void createCell(HSSFWorkbook workbook, Row row, int iColumnIndex, String strValue, Font font) {
		Cell cell = row.createCell(iColumnIndex);

		cell.setCellValue(strValue);

		cell.setCellStyle(creatCellStyle(workbook, font));
	}

	/**
	 * 设置字体
	 * 
	 * @param wb
	 * @param bold粗度
	 * @param color字体颜色
	 * @param fontName字体名
	 * @param isItalic是否斜体
	 * @param hight高度
	 */
	public static Font createFonts(HSSFWorkbook wb, short bold, short color, String fontName, boolean isItalic,
			short hight) {
		Font font = wb.createFont();
		font.setFontName(fontName);
		font.setBoldweight(bold);
		font.setItalic(isItalic);
		font.setColor(color);
		font.setFontHeight(hight);

		return font;
	}

	/***************************************************************************
	 * 将导入出错信息写入Excel中<br>
	 * Created on 2014-8-29 10:47:26<br>
	 * 
	 * @param strTargetFilePath
	 *            目标文件路径
	 * @param iWriteFromRowIndex
	 *            写入行号
	 * @param importVO
	 *            导入VO
	 * @param resultVOs
	 *            出错信息
	 * @author Rocex Wang
	 ***************************************************************************/
	public static void writeImportInfo(String strTargetFilePath, int iWriteFromRowIndex, DataImportVO importVO,
			DataIOResult... resultVOs) {
		POIFSFileSystem fs = null;
		FileOutputStream os = null;
		HSSFWorkbook workbook = null;
		cellStyle = null;

		try {
			fs = new POIFSFileSystem(new FileInputStream(strTargetFilePath));
			os = new FileOutputStream(strTargetFilePath);

			workbook = new HSSFWorkbook(fs);

			Sheet mainSheet = workbook.getSheetAt(0);

			Font fontError = createFonts(workbook, Font.BOLDWEIGHT_BOLD, HSSFColor.RED.index, "宋体", false, (short) 200);
			Font fontSuccess = createFonts(workbook, Font.BOLDWEIGHT_BOLD, HSSFColor.BLUE.index, "宋体", false,
					(short) 200);

			for (DataIOResult dataIOResult : resultVOs) {
				if (dataIOResult == null) {
					continue;
				}

				Map<String, Map<Integer, Map<String, ValidationFailure>>> mapValidation = dataIOResult
						.getValidationFailure();

				if (mapValidation.isEmpty()) {
					Row writeRow = mainSheet.getRow(iWriteFromRowIndex + IDataIOHookClient.START_INDEX_ROW);

					createCell(workbook, writeRow, 0, DataIORes.getPass(), fontSuccess);

					iWriteFromRowIndex++;
				} else {
					String[] strSheetNames = importVO.getImportData().keySet().toArray(new String[0]);

					for (String strSheetName : strSheetNames) {
						String[][] dataValue = importVO.getImportData().get(strSheetName);

						Map<Integer, Map<String, ValidationFailure>> mapSheetValidation = mapValidation
								.get(strSheetName);

						boolean isMainSheet = true;
						if (mapSheetValidation == null) {
							mapSheetValidation = (Map<Integer, Map<String, ValidationFailure>>) mapValidation.values()
									.toArray()[0];
							if (mapSheetValidation == null) {
								continue;
							} else {
								isMainSheet = false;
							}
						}

						for (int k = 0; k < dataValue[0].length; k++) {
							int iRowIndex = k + IDataIOHookClient.START_INDEX_ROW;

							Map<String, ValidationFailure> mapRowValidation = mapSheetValidation.get(iRowIndex);

							if (mapRowValidation == null || mapRowValidation.isEmpty()) {
								continue;
							}

							String strErrorInfo = "";

							for (Iterator iterator = mapRowValidation.keySet().iterator(); iterator.hasNext();) {
								String columnCode = (String) iterator.next();

								strErrorInfo += mapRowValidation.get(columnCode).getMessage();
							}

							if (StringUtils.isNotEmpty(strErrorInfo)) {
								Row writeRow = null;
								if (!isMainSheet) {
									writeRow = workbook.getSheet(strSheetName).getRow(
											IDataIOHookClient.START_INDEX_ROW + iRowIndex);
								}

								createCell(workbook, writeRow, 0, strErrorInfo, fontError);

								// mainSheet
								writeRow = mainSheet.getRow(IDataIOHookClient.START_INDEX_ROW + iRowIndex);
								createCell(workbook, writeRow, 0, strErrorInfo, fontError);

								iWriteFromRowIndex++;
							}
						}
					}

				}

			}

			workbook.write(os);
		} catch (Exception ex) {
			Logger.error(ex.getMessage());
		} finally {
			IOUtils.closeQuietly(os);
		}
	}

	/***************************************************************************
	 * <br>
	 * Created on 2014-9-2 8:58:52<br>
	 * 
	 * @param strTargetFilePath
	 * @param strSourceFilePath
	 * @param resultVO
	 * @param importVO
	 * @author Rocex Wang
	 ***************************************************************************/
	public static void writeValidInfo(String strTargetFilePath, String strSourceFilePath, DataIOResult resultVO,
			DataImportVO importVO) {
		POIFSFileSystem fs = null;
		FileOutputStream os = null;
		HSSFWorkbook workbook = null;
		cellStyle = null;

		try {
			fs = new POIFSFileSystem(new FileInputStream(strTargetFilePath));
			os = new FileOutputStream(strTargetFilePath);

			workbook = new HSSFWorkbook(fs);

			Font fontError = createFonts(workbook, Font.BOLDWEIGHT_BOLD, HSSFColor.RED.index, "宋体", false, (short) 200);
			Font fontSuccess = createFonts(workbook, Font.BOLDWEIGHT_BOLD, HSSFColor.BLUE.index, "宋体", false,
					(short) 200);

			String[] strSheetNames = importVO.getImportData().keySet().toArray(new String[0]);

			if (ArrayUtils.isEmpty(strSheetNames)) {
				return;
			}

			for (String strSheetName : strSheetNames) {
				// 获得workbook的Sheet页
				Sheet sheet = workbook.getSheet(strSheetName);

				String[][] dataValue = importVO.getImportData().get(strSheetName);

				Map<Integer, Map<String, ValidationFailure>> mapSheetValidation = resultVO.getValidationFailure().get(
						strSheetName);

				if (mapSheetValidation == null) {
					for (int j = 0; j < dataValue[0].length; j++) {
						Row writeRow = sheet.getRow(j + IDataIOHookClient.START_INDEX_ROW);

						createCell(workbook, writeRow, 1, DataIORes.getPass(), fontSuccess);
					}

					continue;
				}

				for (int j = 0; j < dataValue[0].length; j++) {
					int k = j + IDataIOHookClient.START_INDEX_ROW;

					Map<String, ValidationFailure> columnMap = mapSheetValidation.get(k);

					if (columnMap == null || columnMap.isEmpty()) {
						Row writeRow = sheet.getRow(k);

						createCell(workbook, writeRow, 1, DataIORes.getPass(), fontSuccess);

						continue;
					}

					String strErrorInfo = "";

					for (Iterator columnIte = columnMap.keySet().iterator(); columnIte.hasNext();) {
						String columnCode = (String) columnIte.next();
						strErrorInfo += columnMap.get(columnCode).getMessage();
					}

					if (StringUtils.isNotEmpty(strErrorInfo)) {
						Row Writerow = sheet.getRow(k);

						createCell(workbook, Writerow, 1, strErrorInfo, fontError);// 为字体设置颜色
					}
				}
			}

			workbook.write(os);
		} catch (Exception e) {
			Logger.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(os);
		}
	}
}
