package nc.ui.wa.psndocwadoc.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IPsndocwadocLabourService;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.vo.hi.wadoc.BatchGroupInsuranceExitVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImportGrpInsExAction extends HrAction {

	public ImportGrpInsExAction() {
		setBtnName("導入退保資料");
		setCode("importGrpInsEx");
	}

	public void doAction(ActionEvent event) throws Exception {

		UIFileChooser digFile = new UIFileChooser();
		digFile.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				String strFileName = f.getName().toLowerCase();

				return (strFileName.endsWith(".xls")) || (strFileName.endsWith(".xlsx")) || (f.isDirectory());
			}

			public String getDescription() {
				return ResHelper.getString("6001hrimp", "06001hrimp0172");
			}
		});
		int returnVal = digFile.showOpenDialog(getEntranceUI());
		if (returnVal != 0) {
			return;
		}
		String strFileName = digFile.getSelectedFile().getPath();
		if (StringUtils.isEmpty(strFileName)) {
			return;
		}
		int istart = MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0498"),
				ResHelper.getString("6007psn", "06007psn0393"));
		if (istart != 4) {
			return;
		}
		List<BatchGroupInsuranceExitVO> importExcelErr = ImportExcel(strFileName);
		if (importExcelErr != null && importExcelErr.size() > 0) {
			IPsndocwadocLabourService service = NCLocator.getInstance().lookup(IPsndocwadocLabourService.class);
			BatchGroupInsuranceExitVO[] returnedVOs = service.batchGroupInsuranceExit(importExcelErr);
			boolean hasError = writeBackFile(strFileName, returnedVOs);

			if (hasError) {
				MessageDialog.showErrorDlg(getEntranceUI(), "錯誤", "批量退保發生錯誤，請檢查報告文件。");
				this.putValue(MESSAGE_AFTER_ACTION, "批量退保發生錯誤，請檢查報告文件。");
			}
		} else {
			throw new BusinessException(ResHelper.getString("6007psn", "06007psn0395"));// 資料為空
		}
	}

	private boolean writeBackFile(String strFileName, BatchGroupInsuranceExitVO[] returnedVOs) throws BusinessException {
		boolean ret = false;
		try {
			// 複製新文件
			String newFileName = strFileName.substring(0, strFileName.lastIndexOf('.')) + "_LOG"
					+ strFileName.substring(strFileName.lastIndexOf('.'));
			File originFile = new File(strFileName);
			File targetFile = new File(newFileName);
			FileUtils.copyFile(originFile, targetFile);

			// 結果寫入新文件
			Sheet sheet = null;
			Workbook workBook = null;
			XSSFWorkbook xwb = null;
			FileInputStream inputStream = new FileInputStream(newFileName);

			if (newFileName.endsWith("xlsx")) {
				xwb = new XSSFWorkbook(inputStream);
				sheet = xwb.getSheetAt(0);
			} else {
				workBook = WorkbookFactory.create(new FileInputStream(newFileName));
				sheet = workBook.getSheetAt(0);
			}
			inputStream.close();

			Row row0 = sheet.getRow(0);
			Cell cell_A6 = row0.createCell(row0.getLastCellNum());
			cell_A6.setCellValue("結果");

			int rowCount = sheet.getLastRowNum();
			for (int i = 1; i <= rowCount; i++) {
				Row rowTmp = sheet.getRow(i);
				Cell cell_tmp_result = rowTmp.createCell(rowTmp.getLastCellNum());
				for (BatchGroupInsuranceExitVO returnedVO : returnedVOs) {
					if (returnedVO.getLineno() == i) {
						cell_tmp_result.setCellValue(returnedVO.getErrmessage());
						if (!returnedVO.getErrmessage().contains("忽略")) {
							ret = true;
						}
					}
				}
			}

			// 寫入文件
			FileOutputStream excelFileOutPutStream = new FileOutputStream(targetFile);
			if (newFileName.endsWith("xlsx")) {
				xwb.write(excelFileOutPutStream);
			} else {
				workBook.write(excelFileOutPutStream);
			}
			excelFileOutPutStream.flush();
			excelFileOutPutStream.close();
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

		return ret;
	}

	public Object getCellValue(Cell cell) {
		if (cell == null) {
			return null;
		}
		String strval = null;
		// Excel 型態格式審查
		switch (cell.getCellType()) {
		case 0:
			double value = cell.getNumericCellValue();
			if ((int) value == value) {
				return Integer.valueOf((int) value);
			}
			return new UFDouble(value);
		case 1:
			strval = cell.getStringCellValue();
			strval = (strval == null) || (strval.trim().length() < 1) ? null : strval.trim();
			return strval;
		case 3:
			return null;
		case 4:
			Object strval0 = Boolean.valueOf(cell.getBooleanCellValue());
			if (strval0 != null) {
				String o = strval0.toString().trim();
				return o;
			}
			return null;
		}
		Debug.debug("unsuported sell type");
		return null;
	}

	public List<BatchGroupInsuranceExitVO> ImportExcel(String fileName) throws BusinessException {
		Sheet sheet = null;
		Workbook workBook = null;
		XSSFWorkbook xwb = null;
		FileInputStream inputStream = null;
		try {
			if (fileName.endsWith("xlsx")) {
				xwb = new XSSFWorkbook(fileName);
				sheet = xwb.getSheetAt(0);
			} else {
				inputStream = new FileInputStream(fileName);
				POIFSFileSystem pfs = new POIFSFileSystem(inputStream);

				sheet = new HSSFWorkbook(pfs).getSheetAt(0);
				workBook = WorkbookFactory.create(new FileInputStream(fileName));
				inputStream.close();
				sheet = workBook.getSheetAt(0);
			}
		} catch (Exception e) {
			throw new BusinessException(ResHelper.getString("6007psn", "06007psn0399") + e.getMessage());
		}
		Row row0 = sheet.getRow(0);
		if (row0 == null) {
			throw new BusinessException(ResHelper.getString("6007psn", "06007psn0400"));
		}
		int iColNum = row0.getLastCellNum();
		if ((row0.getCell((short) iColNum) != null) && (getCellValue(row0.getCell((short) iColNum)) != null)) {
			iColNum += 1;
		}
		ValidateHeadTitle(row0, iColNum);

		String[] fieldNames = { "psnCode", "clerkCode", "iDNo", "insuranceCode", "exitDate" };
		int rowCount = sheet.getLastRowNum();
		List<BatchGroupInsuranceExitVO> list = new ArrayList();
		int haveNull = 0;
		for (int i = 1; i <= rowCount; i++) {
			Row rowTmp = sheet.getRow(i);
			BatchGroupInsuranceExitVO vo = new BatchGroupInsuranceExitVO();
			vo.setLineno(i);
			for (int j = 0; (j < iColNum) && (j < 5); j++) {
				Cell cell = rowTmp.getCell((short) j);
				Object cellValue = getCellValue(cell);
				if (cellValue != null) {
					vo.setAttributeValue(
							fieldNames[j],
							fieldNames[j].equals("exitDate") ? new UFLiteralDate(cellValue.toString()) : cellValue
									.toString());
				} else {
					vo.setAttributeValue(fieldNames[j], null);
					// rowTmp.createCell(5).setCellValue("欄位存在空值");
					haveNull++;
				}
			}

			if (!isEmpty(vo)) {
				list.add(vo);
			}
			if (haveNull > 0) {
				throw new BusinessException("欄位存在空值");
			}
		}
		return list;
	}

	private boolean isEmpty(BatchGroupInsuranceExitVO vo) {
		return (vo.getAttributeValue("psnCode") == null) && (vo.getAttributeValue("clerkCode") == null)
				&& (vo.getAttributeValue("idNo") == null) && (vo.getAttributeValue("insuranceCode") == null)
				&& (vo.getAttributeValue("exitDate") == null);
	}

	private void ValidateHeadTitle(Row row0, int iColNum) throws BusinessException {
		Object fieldValue = null;
		for (int i = 0; i < iColNum; i++) {
			Cell cell = row0.getCell((short) i);
			fieldValue = getCellValue(cell);
			switch (i) {
			case 0:
				if ((fieldValue == null) || (fieldValue.toString().trim().length() < 1) || (!fieldValue.equals("人員編碼"))) {
					throw new BusinessException(ResHelper.getString("6007psn", "06007psn0401")
							+ MessageFormat.format(ResHelper.getString("6007psn", "06007psn0402"), new Object[] {
									Integer.valueOf(i + 1), "人員編碼" }));
				}
				break;
			case 1:
				if ((fieldValue == null) || (fieldValue.toString().trim().length() < 1) || (!fieldValue.equals("員工號"))) {
					throw new BusinessException(ResHelper.getString("6007psn", "06007psn0401")
							+ MessageFormat.format(ResHelper.getString("6007psn", "06007psn0402"), new Object[] {
									Integer.valueOf(i + 1), "員工號" }));
				}
				break;
			case 2:
				if ((fieldValue == null) || (fieldValue.toString().trim().length() < 1)
						|| (!fieldValue.equals("退保人身份證號"))) {
					throw new BusinessException(ResHelper.getString("6007psn", "06007psn0401")
							+ MessageFormat.format(ResHelper.getString("6007psn", "06007psn0402"), new Object[] {
									Integer.valueOf(i + 1), "退保人身份證號" }));
				}
				break;
			case 3:
				if ((fieldValue == null) || (fieldValue.toString().trim().length() < 1) || (!fieldValue.equals("險種編號"))) {
					throw new BusinessException(ResHelper.getString("6007psn", "06007psn0401")
							+ MessageFormat.format(ResHelper.getString("6007psn", "06007psn0402"), new Object[] {
									Integer.valueOf(i + 1), "險種編號" }));
				}
				break;
			case 4:
				if ((fieldValue == null) || (fieldValue.toString().trim().length() < 1) || (!fieldValue.equals("退保日期"))) {
					throw new BusinessException(ResHelper.getString("6007psn", "06007psn0401")
							+ MessageFormat.format(ResHelper.getString("6007psn", "06007psn0402"), new Object[] {
									Integer.valueOf(i + 1), "退保日期" }));
				}
				break;
			}
		}

	}
}
