/**
 * @(#)ExportDataUtil.java 1.0 2017��9��26��
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.pub.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings("restriction")
public class ExportDataUtil {

	private String[] fieldNames;
	/** �����ļ�ͷ�ֶα��� */
	private String[] fieldDisplayNames;

	/** �����ļ�ͷ�ֶ����� */

	public String[] getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	public String[] getFieldDisplayNames() {
		return fieldDisplayNames;
	}

	public void setFieldDisplayNames(String[] fieldDisplayNames) {
		this.fieldDisplayNames = fieldDisplayNames;
	}

	/**
	 * ����Excel��Txt�ļ�
	 * 
	 * @param fileName
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public int exportToFile(String fileName, GeneralVO[] vos) throws BusinessException {
		if (fileName.toLowerCase().endsWith(".xls") || fileName.toLowerCase().endsWith(".xlsx")) {
			return exportExcelFile(fileName, vos);// ����Excel�ļ�
		} else if (fileName.toLowerCase().endsWith(".txt")) {
			return exportTxtFile(fileName, vos);// ����TXT�ļ�
		}
		return -1;
	}

	private int exportTxtFile(String fileName, GeneralVO[] vos) throws BusinessException {
		BufferedWriter out = null;
		try {
			File outfile = new File(fileName);
			if (outfile.exists())
				outfile.delete();
			outfile = new File(fileName);
			out = new BufferedWriter(new FileWriter(outfile, true));
			StringBuilder sb = new StringBuilder();
			// if(outputLine)
			// sb.append(ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0018")
			// /*@res "�к�,"*/);
			for (String colName : getFieldDisplayNames()) {
				sb.append(colName + ",");
			}
			sb = sb.deleteCharAt(sb.length() - 1);
			out.write(sb.toString());
			out.newLine();
			if (!ArrayUtils.isEmpty(vos)) {
				for (int i = 0; i < vos.length; i++) {
					sb = new StringBuilder();
					for (int j = 0; j < getFieldNames().length; j++) {
						Object value = vos[i].getAttributeValue(getFieldNames()[j]);
						String strDataValue = value == null ? "" : value.toString();
						sb.append(strDataValue + ",");
					}
					sb = sb.deleteCharAt(sb.length() - 1);
					out.write(sb.toString());
					out.newLine();
				}
			}

			out.close();
			sb = null;
			outfile = null;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(out);
		}
		return 0;
	}

	private int exportExcelFile(String fileName, GeneralVO[] vos) throws BusinessException {
		// ���û�ѡ�������excel�ļ���
		if (StringUtils.isEmpty(fileName)) {
			fileName = selSaveExcelFileName();
		}

		if (StringUtils.isEmpty(fileName)) {
			return -1;
		}
		java.io.File file = new java.io.File(fileName);
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(fileName);
			Workbook wb = fileName.endsWith(".xls") ? new HSSFWorkbook() : new XSSFWorkbook();// ������HSSFWorkbook����
			Sheet sheet = wb.createSheet();// �����µ�sheet����
			CellStyle cs = createDefaultCellStyle(wb);
			cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			cs.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			CellStyle cs2 = createDefaultCellStyle(wb);
			Row row = sheet.createRow(0);
			Cell cell = row.createCell(2);
			for (int i = 0; i < getFieldDisplayNames().length; i++) {
				sheet.setColumnWidth(i, 2500);
				cell = row.createCell(i);
				cell.setCellValue(fileName.endsWith(".xls") ? new HSSFRichTextString(getFieldDisplayNames()[i])
						: new XSSFRichTextString(getFieldDisplayNames()[i]));
				cell.setCellStyle(cs);
			}
			if (!ArrayUtils.isEmpty(vos)) {
				for (int i = 0; i < vos.length; i++) {
					row = sheet.createRow(i + 1);
					for (int j = 0; j < getFieldNames().length; j++) {
						cell = row.createCell(j);
						cell.setCellStyle(cs2);
						Object value = vos[i].getAttributeValue(getFieldNames()[j]);
						String strDataValue = value == null ? "" : value.toString();
						cell.setCellValue(fileName.endsWith(".xls") ? new HSSFRichTextString(strDataValue)
								: new XSSFRichTextString(strDataValue));
					}
				}
			}
			// ��Workbook����������ļ�workbook.xls��
			wb.write(fileOut);
			fileOut.close();
			return 0;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			try {
				if (fileOut != null) {
					fileOut.close();
				}
			} catch (Exception e) {
			}
		}

	}

	/**
	 * ����ͨ����ʽ
	 * 
	 * @param wb
	 * @return
	 */
	private CellStyle createDefaultCellStyle(Workbook wb) {
		CellStyle cs = wb.createCellStyle();// ����һ��style
		Font littleFont = wb.createFont();// ����һ��Font
		littleFont.setFontName("SimSun");
		littleFont.setFontHeightInPoints((short) 10);
		cs.setFont(littleFont);// ��������
		cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);// ˮƽ����
		cs.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// ��ֱ����
		cs.setBorderBottom(HSSFCellStyle.BORDER_THIN);// �±߿�
		cs.setBorderLeft(HSSFCellStyle.BORDER_THIN);// ��߿�
		cs.setBorderRight(HSSFCellStyle.BORDER_THIN);// �ұ߿�
		cs.setBorderTop(HSSFCellStyle.BORDER_THIN);// �ϱ߿�

		return cs;
	}

	/**
	 * ѡ�񱣴�excel���ļ���
	 * 
	 * @param filetype
	 *            ".xls" ��
	 * @return
	 */
	public String selSaveExcelFileName() {
		String fileName = null;
		nc.ui.pub.beans.UIFileChooser fileDlg = new nc.ui.pub.beans.UIFileChooser();
		fileDlg.setCurrentDirectory(new java.io.File("."));
		fileDlg.setSelectedFile(new java.io.File("default.xls"));
		fileDlg.setFileFilter(new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(java.io.File f) {
				return f.getName().toLowerCase().endsWith(".xls") || f.isDirectory();
			}

			@Override
			public String getDescription() {
				return ResHelper.getString("6004doc", "06004doc0015")
				/* @res "Excel��ʽ��*.xls��" */;
			}
		});

		boolean canShow = true;
		java.io.File selFile = null;
		while (canShow) {
			if (fileDlg.showSaveDialog(null) != nc.ui.pub.beans.UIFileChooser.APPROVE_OPTION) {
				return null;
			}

			// ���ѡ����ļ�
			selFile = fileDlg.getSelectedFile();
			if (selFile == null) {
				return null;
			}
			if (selFile.exists()) {
				if (MessageDialog.showOkCancelDlg(null,
						ResHelper.getString("6004pub", "0pub-00003", ResHelper.getString("6004pub", "0pub-00004")),
						ResHelper.getString("6004doc", "06004doc0016")
				/* @res "�ļ������ڣ��Ƿ��滻ԭ���ļ���" */) == MessageDialog.ID_OK) {
					canShow = false;
				}
			} else {
				canShow = false;
			}
		}

		fileName = selFile.getPath();
		if (!fileName.toLowerCase().endsWith(".xls") && !fileName.toLowerCase().endsWith(".xls")) {
			fileName += ".xls";
		}
		return fileName; // ���û�ȡ������
	}

}
