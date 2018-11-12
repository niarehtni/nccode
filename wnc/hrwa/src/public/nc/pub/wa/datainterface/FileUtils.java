/**
 * @(#)ExcelUtils.java 1.0 2018年1月30日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.pub.wa.datainterface;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import nc.itf.wa.datainterface.ICsvRowReader;
import nc.itf.wa.datainterface.IExcelRowReader;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author niehg
 * @since 6.3
 */
public class FileUtils {
	public static void readCsv(String filePath, String charset, ICsvRowReader rowReader) throws Exception {
		String line = null;

		BufferedReader br = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		try {
			fis = new FileInputStream(filePath);
			isr = new InputStreamReader(fis, charset);
			br = new BufferedReader(isr);
			int rowNo = -1;
			while ((line = br.readLine()) != null) {
				rowNo++;
				line = line.trim();
				// skip空行或注释行
				if (line.length() == 0) { // || line.startsWith("#")
					continue;
				}
				if (!rowReader.readRow(rowNo, line)) {
					break;
				}
			}
		} finally {
			closeQuietly(br);
			closeQuietly(isr);
			closeQuietly(fis);
		}
	}

	public static void readExcel(String filepath, IExcelRowReader rowReader) throws Exception {
		File file = new File(filepath);
		// // 检查文件
		// if (!file.exists()) {
		// throw new Exception("File is not exists!");
		// }
		// if (!(file.isFile() &&
		// (file.getName().toLowerCase().endsWith(DataItfConst.SUFFIX_XLS) ||
		// file.getName()
		// .toLowerCase().endsWith(DataItfConst.SUFFIX_XLS)))) {
		// throw new Exception("File type is not Excel!");
		// }
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			// 获得Workbook工作薄对象
			Workbook workbook = null;
			if (file.getName().toLowerCase().trim().endsWith(DataItfConst.SUFFIX_XLS)) {
				workbook = new HSSFWorkbook(is);
			} else if (file.getName().toLowerCase().trim().endsWith(DataItfConst.SUFFIX_XLSX)) {
				workbook = new XSSFWorkbook(is);
			}
			if (workbook != null) {
				for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
					// 获得当前sheet工作表
					Sheet sheet = workbook.getSheetAt(sheetNum);
					if (sheet == null) {
						continue;
					}
					// 获得当前sheet的开始行
					int firstRowNum = sheet.getFirstRowNum();
					// 获得当前sheet的结束行
					int lastRowNum = sheet.getLastRowNum();
					// 循环除了第一行的所有行
					for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
						// 获得当前行
						Row row = sheet.getRow(rowNum);
						if (row == null) {
							continue;
						}
						if (!rowReader.readRow(sheetNum, rowNum, row)) {
							break;
						}
					}
				}
				workbook = null;
			}
		} finally {
			closeQuietly(is);
		}
	}

	/**
	 * 避免关闭的代码重复.
	 * 
	 * @param icloseable
	 */
	public static void closeQuietly(Closeable icloseable) {
		if (icloseable != null) {
			try {
				icloseable.close();
			} catch (Exception e) {

			}
		}
	}

}
