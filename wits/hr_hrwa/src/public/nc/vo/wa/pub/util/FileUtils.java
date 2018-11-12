/**
 * @(#)FileUtils.java 1.0 2017-09-15
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.vo.wa.pub.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 文件操作工具类
 * 
 * @author kevin.nie
 * @since 6.1
 */
public class FileUtils {

	/**
	 * 创建一个空目录
	 * 
	 * @param strDir
	 * @return
	 */
	public static File createBlankDirectory(String strDir) {
		File filePath = new File(strDir);
		if (filePath.exists()) {
			FileUtils.cleanDirectory(filePath);
		} else {
			filePath.mkdirs();
		}

		return filePath;
	}

	/**
	 * 删除文件（包括删除目录）
	 * 
	 * @param file
	 * @return
	 */
	public static boolean delete(File file) {
		if (null == file || !file.exists()) {
			return true;
		}
		if (file.isDirectory()) {
			return cleanDirectory(file) && file.delete();
		} else {
			return file.delete();
		}
	}

	/**
	 * 清除目录下的所有内容
	 * 
	 * @param directory
	 * @return
	 */
	public static boolean cleanDirectory(File directory) {
		if (null == directory || !directory.exists()) {
			return true;
		}
		if (!directory.isDirectory()) {
			return false;
		}
		boolean result = true;
		File files[] = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			result = result && delete(file);
		}

		return result;
	}

	/**
	 * 避免关闭的代码重复
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

	/**
	 * 从指定的文件读取数据。文件格式为key=value对。可以包含空行及以'#'开头的注释行。 value可以用指定的分隔符.
	 * 
	 * @param filePath
	 * @param valueDelimiter
	 *            分隔符。如果不为null，则将value按此分隔符进行拆分。
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String[]> loadPropertyFile(String filePath, String valueDelimiter) throws Exception {

		String line = null;
		File file = new File(filePath);
		if (!file.exists()) {
			throw new BusinessException("File does not exist: " + filePath);
		}
		Map<String, String[]> map = new HashMap<String, String[]>();

		BufferedReader br = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;

		try {
			fis = new FileInputStream(filePath);
			isr = new InputStreamReader(fis, "UTF-8");
			br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				line = line.trim();
				// skip空行或注释行
				if (line.length() == 0 || line.startsWith("#"))
					continue;
				int index = line.indexOf("=");
				String key = line.substring(0, index).trim();
				String value = line.substring(index + 1).trim();
				if (valueDelimiter != null) {
					StringTokenizer st = new StringTokenizer(value, valueDelimiter);
					List<String> list = new ArrayList<String>();
					while (st.hasMoreTokens()) {
						list.add(st.nextToken().trim());
					}
					map.put(key, list.toArray(new String[0]));
				} else {
					map.put(key, new String[] { value });
				}

			}
		} finally {
			closeQuietly(br);
			closeQuietly(isr);
			closeQuietly(fis);
		}

		return map;
	}

	/**
	 * 读取Excel文件中指定单元格式数据
	 * 
	 * @param row
	 * @param cellIndex
	 * @return
	 */
	public static String getCellString(Row row, int cellIndex) {
		Cell cell = row.getCell(cellIndex);
		if (cell != null) {
			String str = cell.toString().trim();
			// 只读取有内容的数据
			if (str.length() > 0) {
				return str;
			}
		}
		return null;
	}

	public static UFDouble getCellDouble(Row row, int cellIndex) {
		Cell cell = row.getCell(cellIndex);
		if (cell != null) {
			String str = cell.toString().trim();
			// 只读取有内容的数据
			if (str.length() > 0) {
				return new UFDouble(cell.getNumericCellValue());
			}
		}
		return null;
	}

	/**
	 * 创建一个名为key且及内容为value的文档元素。
	 * 
	 * @param document
	 * @param key
	 * @param value
	 * @return
	 */
	public static Element createElement(Document document, String key, String value) {
		Element elem = document.createElement(key);
		if (value != null) {
			elem.appendChild(document.createTextNode(value));
		}
		return elem;
	}

	/**
	 * 读取Excel文件。Excel每一个页签第一行为列名
	 * 
	 * @param filePath
	 * @param rowReader
	 * @throws IOException
	 */

	public static void readExcel(String filePath, IExcelRowReader rowReader) throws BusinessException {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filePath);
			int size = inputStream.available();
			if (size == 0) {
				throw new BusinessException(ResHelper.getString("6029collec", "06029collec0014"));
			}

			Workbook wb = WorkbookFactory.create(inputStream);
			int totalSheet = wb.getNumberOfSheets();// 一个Excel文件中有多少个页签
			for (int i = 0; i < totalSheet; i++) {
				Sheet sheet = wb.getSheetAt(i);
				if (null == sheet) {
					continue;
				}
				Row row = sheet.getRow(0);
				if (row != null) {
					int rowNum = sheet.getLastRowNum();// 总共行数
					for (int j = 1; j <= rowNum; j++) {// 行数循环，第一行为列名，省略
						row = sheet.getRow(j);
						if (!rowReader.readRow(i, j, row)) {
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			throw new BusinessException(e.getMessage(), e);
		} finally {
			closeQuietly(inputStream);
		}
	}

	public static void readTxt(String filePath, String charset, ITxtRowReader rowReader) throws IOException {
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
				if (line.length() == 0 || line.startsWith("#"))
					continue;
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

}
