package nc.ui.ta.psncalendar.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.hr.utils.ResHelper;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModelDataManager;
import nc.ui.ta.pub.ExportTBM;
import nc.ui.ta.pub.IColorConst;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.format.FormatGenerator;
import nc.vo.hr.tools.dbtool.ds.adapter.IOParam;
import nc.vo.hr.tools.dbtool.ds.adapter.excel.ExcelAdapter;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnCalendarCommonValue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ImportAction extends HrAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 868513735745937648L;

	private PsnCalendarAppModelDataManager dataManager;

	public ImportAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.IMPORTBILL);
	}

	public PsnCalendarAppModelDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(PsnCalendarAppModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public boolean isClearNull;
	public String strFileName;
	List<String>[] exceptVectors;

	@SuppressWarnings("rawtypes")
	@Override
	public void doAction(ActionEvent e) throws Exception {
		PsnCalendarFileChooserForImport digFile = new PsnCalendarFileChooserForImport();
		digFile.setFileFilter(new FileFilter() {
			// String type = "*.xls";
			// String ext = ".xls";
			// public boolean accept(java.io.File f) {
			// if (f.isDirectory())
			// return true;
			// return (f.getName().endsWith(ext)) ? true : false;
			// }
			//
			// public String getDescription() {
			// if (type.equals("*.xls"))
			// return
			// ResHelper.getString("6017psncalendar","06017psncalendar0002")
			// /*@res "Excel文件(*.xls)"*/;
			// return null;
			// }
			// 改为支持2007导入的
			@Override
			public boolean accept(File f) {
				String strFileName = f.getName().toLowerCase();

				return strFileName.endsWith(".xls") || strFileName.endsWith(".xlsx") || f.isDirectory();
			}

			@Override
			public String getDescription() {
				return ResHelper.getString("6001hrimp", "06001hrimp0172")/*
																		 * @res
																		 * "Excel格式（*.xls，*.xlsx）"
																		 */;
			}

		});
		int returnVal = digFile.showOpenDialog(getEntranceUI());
		// 不选择确定是直接返回
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			putValue(MESSAGE_AFTER_ACTION, IShowMsgConstant.getCancelInfo());
			return;
		}
		strFileName = digFile.getSelectedFile().getPath();
		isClearNull = digFile.isClearNull();
		// 弹出提示框
		if (UIDialog.ID_YES != MessageDialog.showYesNoDlg(getEntranceUI(), null,
				ResHelper.getString("6017psncalendar", "06017psncalendar0034")
		/* @res "导入工作日历数据时，若已有日历数据，原数据将会被覆盖，确认继续吗？" */)) {
			putValue(MESSAGE_AFTER_ACTION, IShowMsgConstant.getCancelInfo());
			return;
		}
		if (StringUtils.isEmpty(strFileName))
			return;

		new SwingWorker() {

			BannerTimerDialog dialog = new BannerTimerDialog(SwingUtilities.getWindowAncestor(getModel().getContext()
					.getEntranceUI()));
			String error = null;

			protected Boolean doInBackground() throws Exception {
				try {
					dialog.setStartText("正在導入工作日曆");
					dialog.start();

					GeneralVO[] vos = getPsnCalendarVOs(strFileName);
					if (!ArrayUtils.isEmpty(vos)) {
						exceptVectors = NCLocator.getInstance().lookup(IPsnCalendarManageMaintain.class)
								.importDatas(getContext().getPk_org(), vos, isClearNull);

						// 无异常
						if (ArrayUtils.isEmpty(exceptVectors)) {
							// 刷新
							getDataManager().refresh();
							putValue(HrAction.MESSAGE_AFTER_ACTION,
									ResHelper.getString("6017psncalendar", "06017psncalendar0099")/*
																								 * @
																								 * res
																								 * "导入工作日历成功"
																								 */);
						} else {
							// 存在异常
							short[] errorHSSFColors = new short[6];
							errorHSSFColors[0] = ExportTBM.getColor(IColorConst.COLOR_EXPORT_DATAERROR);
							errorHSSFColors[1] = ExportTBM.getColor(IColorConst.COLOR_EXPORT_SAMERECORD);
							errorHSSFColors[2] = ExportTBM.getColor(IColorConst.COLOR_EXPORT_CLASSERROR);
							errorHSSFColors[3] = ExportTBM.getColor(IColorConst.COLOR_EXPORT_CODENOTFOUND);
							errorHSSFColors[4] = ExportTBM.getColor(IColorConst.COLOR_EXPORT_MUTEXINFILE);
							errorHSSFColors[5] = ExportTBM.getColor(IColorConst.COLOR_EXPORT_MUTEXINDB);

							ExportTBM aExportTBM = new ExportTBM();
							aExportTBM.changeColorCalendarExcelFile(strFileName, (ArrayList<String>[]) exceptVectors,
									errorHSSFColors);
							// 显示信息
							String errMsg = ResHelper.getString("6017psncalendar", "06017psncalendar0036")
							/* @res "导入文件存在以下错误：" */;
							if (!CollectionUtils.isEmpty(exceptVectors[0])) {
								errMsg += ResHelper.getString("6017psncalendar", "06017psncalendar0037")
								/* @res "数据格式不正确；" */;
							}
							if (!CollectionUtils.isEmpty(exceptVectors[1])) {
								errMsg += ResHelper.getString("6017psncalendar", "06017psncalendar0038")
								/* @res "同一人有多条记录；" */;
							}
							if (!CollectionUtils.isEmpty(exceptVectors[2])) {
								errMsg += ResHelper.getString("6017psncalendar", "06017psncalendar0039")
								/* @res "班次名称错误；" */;
							}
							if (!CollectionUtils.isEmpty(exceptVectors[3])) {
								errMsg += ResHelper.getString("6017psncalendar", "06017psncalendar0040")
								/* @res "找不到对应的人员编码；" */;
							}
							if (!CollectionUtils.isEmpty(exceptVectors[4])) {
								errMsg += ResHelper.getString("6017psncalendar", "06017psncalendar0041")
								/* @res "文件中排班冲突；" */;
							}
							if (!CollectionUtils.isEmpty(exceptVectors[5])) {
								errMsg += ResHelper.getString("6017psncalendar", "06017psncalendar0042")
								/* @res "文件中排班与已有排班冲突；" */;
							}
							errMsg += ResHelper.getString("6017psncalendar", "06017psncalendar0043")
							/* @res "详情请查看同目录下生成的出错信息文件！" */;
							throw new BusinessException(errMsg);
							// MessageDialog.showErrorDlg(getEntranceUI(), null,
							// errMsg);
						}
					}
				} catch (LockFailedException le) {
					error = le.getMessage();
				} catch (VersionConflictException le) {
					throw new BusinessException(le.getBusiObject().toString(), le);
				} catch (Exception e) {
					error = e.getMessage();
				} finally {
					dialog.end();
				}
				return Boolean.TRUE;
			}

			protected void done() {
				if (error != null) {
					ShowStatusBarMsgUtil.showErrorMsg("導入發生錯誤：", error, getModel().getContext());
				} else {
					ShowStatusBarMsgUtil.showStatusBarMsg("導入工作日曆成功。", getModel().getContext());
				}
			}
		}.execute();

	}

	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable();
		// UIState state = getModel().getUiState();
		// return
		// (state==UIState.INIT||state==UIState.NOT_EDIT)&&!StringUtils.isEmpty(getModel().getContext().getPk_org());
	}

	public GeneralVO[] getPsnCalendarVOs(String fileName) {
		// step.1.校验导入文件的格式的正确性
		Sheet sheet = null;
		try {
			// FileInputStream inputStream = new FileInputStream(fileName);
			// POIFSFileSystem pfs = new POIFSFileSystem(inputStream);
			// sheet = (new HSSFWorkbook(pfs)).getSheetAt(0);

			// 支持2007版excel导入
			IOParam param = new IOParam(new FileInputStream(fileName));
			param.setProperty(ExcelAdapter.SHEET_NUM, 0);
			InputStream input = param.getInputStream();
			Workbook xwb = WorkbookFactory.create(input);
			input.close();
			sheet = xwb.getSheetAt(0);
		} catch (Exception e) {
			throw new RuntimeException(ResHelper.getString("6017psncalendar", "06017psncalendar0044")
			/* @res "读取excel文件错误:" */+ e.getMessage());
		}
		// 获得表头信息
		Row row0 = sheet.getRow(2);// 开始行初定为2
		Row row1 = sheet.getRow(3);// 开始行初定为3
		if (row0 == null || row1 == null) {
			throw new RuntimeException(ResHelper.getString("6017psncalendar", "06017psncalendar0045")
			/* @res "表头信息为空" */);
		}
		int iColNum = row1.getLastCellNum();
		if (row1.getCell((short) iColNum) != null && getCellValue(row1.getCell((short) iColNum)) != null) {
			iColNum = iColNum + 1;
		}
		// 校验并获取表头信息
		String[] fieldNames = getHeadMessage(row0, row1, iColNum);

		int rowCount = sheet.getLastRowNum();
		List<GeneralVO> list = new ArrayList<GeneralVO>();
		// 获取表内容，内容开始行初定为4，添加星期几后改为5
		for (int i = 5; i <= rowCount; i++) {
			Row rowTmp = sheet.getRow(i);
			GeneralVO vo = new GeneralVO();
			// 是否为空行标识
			UFBoolean isNullRow = UFBoolean.TRUE;
			// 逐列读取
			for (int j = 0; j < iColNum; j++) {
				Cell cell = rowTmp.getCell((short) j);
				Object cellValue = getCellValue(cell);
				vo.setAttributeValue(fieldNames[j], cellValue == null ? null : cellValue.toString());
				// 只要某一列有数据则将空行标识改为否
				if (cellValue != null)
					isNullRow = UFBoolean.FALSE;
			}
			vo.setAttributeValue(PsnCalendarCommonValue.LISTCODE_ISNULLROW, isNullRow);
			list.add(vo);
		}
		return list.toArray(new GeneralVO[0]);
	}

	/**
	 * 校验并获取表头信息
	 * 
	 * @param row
	 */
	public String[] getHeadMessage(Row row0, Row row1, int colNum) {
		String[] fieldNames = new String[colNum];
		Object fieldValue = null;
		Object dateValue = null;

		for (int i = 0; i < colNum; i++) {
			// 基本信息为前3列
			if (i < 3) {
				Cell cell = row0.getCell((short) i);
				fieldValue = getCellValue(cell);
				switch (i) {
				case 0:// 员工号列
					if (fieldValue == null || fieldValue.toString().trim().length() < 1
							|| !fieldValue.equals(ResHelper.getString("6017psncalendar", "06017psncalendar0004")/*
																												 * @
																												 * res
																												 * "员工号"
																												 */)) {
						throw new RuntimeException(ResHelper.getString("6017psncalendar", "06017psncalendar0046")/*
																												 * @
																												 * res
																												 * "导入文件数据格式不正确！"
																												 */
								+ ResHelper.getString("6017psncalendar", "06017psncalendar0047"/*
																								 * @
																								 * res
																								 * "请检查表头{0}列{1}列"
																								 */, FormatGenerator
										.getIndexFormat().format(i + 1), ResHelper.getString("6017psncalendar",
										"06017psncalendar0004")/* @res "员工号" */));
					}
					fieldValue = PsnCalendarCommonValue.LISTCODE_CLERKCODE;
					break;
				case 1:// 编码列
					if (fieldValue == null || fieldValue.toString().trim().length() < 1
							|| !fieldValue.equals(ResHelper.getString("common", "UC000-0000147")/*
																								 * @
																								 * res
																								 * "人员编码"
																								 */)) {
						throw new RuntimeException(
								ResHelper.getString("6017psncalendar", "06017psncalendar0046")/*
																							 * @
																							 * res
																							 * "导入文件数据格式不正确！"
																							 */
										+ ResHelper.getString("6017psncalendar", "06017psncalendar0047"/*
																										 * @
																										 * res
																										 * "请检查表头{0}列{1}列"
																										 */,
												FormatGenerator.getIndexFormat().format(i + 1),
												ResHelper.getString("common", "UC000-0000147")/*
																							 * @
																							 * res
																							 * "人员编码"
																							 */));
					}
					fieldValue = PsnCalendarCommonValue.LISTCODE_PSNCODE;
					break;
				case 2:// 姓名列
					if (fieldValue == null || fieldValue.toString().trim().length() < 1
							|| !fieldValue.equals(ResHelper.getString("common", "UC000-0001403")/*
																								 * @
																								 * res
																								 * "姓名"
																								 */)) {
						throw new RuntimeException(
								ResHelper.getString("6017psncalendar", "06017psncalendar0046")/*
																							 * @
																							 * res
																							 * "导入文件数据格式不正确！"
																							 */
										+ ResHelper.getString("6017psncalendar", "06017psncalendar0047"/*
																										 * @
																										 * res
																										 * "请检查表头{0}列{1}列"
																										 */,
												FormatGenerator.getIndexFormat().format(i + 1),
												ResHelper.getString("common", "UC000-0001403")/*
																							 * @
																							 * res
																							 * "姓名"
																							 */));
					}
					fieldValue = PsnCalendarCommonValue.LISTCODE_PSNNAME;
					break;
				default:
					break;
				}
				fieldNames[i] = fieldValue.toString().trim();
				continue;
			}
			// 从第3列开始是日期列
			// 获取年度月份,第3列年度月份不允许为空
			Cell cell = row0.getCell((short) i);
			Object dateValueTmp = getCellValue(cell);
			if (dateValueTmp == null || dateValueTmp.toString().trim().length() < 1) {
				if (i == 3)
					throw new RuntimeException(ResHelper.getString("6017psncalendar", "06017psncalendar0048"/*
																											 * @
																											 * res
																											 * "导入文件数据格式不正确！请检查表头{0}列年度月份列"
																											 */,
							FormatGenerator.getIndexFormat().format(i + 1)));
			} else {
				dateValue = dateValueTmp;
				try {
					UFLiteralDate dateTmp = UFLiteralDate
							.getDate(dateValue == null ? "" : dateValue.toString() + "-01");
					fieldValue = dateTmp.toStdString();
				} catch (Exception e) {
					throw new RuntimeException(ResHelper.getString("6017psncalendar", "06017psncalendar0049"/*
																											 * @
																											 * res
																											 * "导入文件数据格式不正确！请检查表头{0}列日期列"
																											 */,
							FormatGenerator.getIndexFormat().format(i + 1)));
				}
			}

			// 获取日期
			cell = row1.getCell((short) i);
			Object value = getCellValue(cell);
			if (value == null || value.toString().trim().length() < 1) {
				throw new RuntimeException(ResHelper.getString("6017psncalendar", "06017psncalendar0049"/*
																										 * @
																										 * res
																										 * "导入文件数据格式不正确！请检查表头{0}列日期列"
																										 */,
						FormatGenerator.getIndexFormat().format(i + 1)));
			}
			try {
				UFLiteralDate dateTmp = UFLiteralDate.getDate(dateValue.toString() + "-"
						+ (value.toString().length() == 1 ? "0" + value.toString() : value.toString()));
				fieldValue = dateTmp.toStdString();
			} catch (Exception e) {
				throw new RuntimeException(ResHelper.getString("6017psncalendar", "06017psncalendar0049"/*
																										 * @
																										 * res
																										 * "导入文件数据格式不正确！请检查表头{0}列日期列"
																										 */,
						FormatGenerator.getIndexFormat().format(i + 1)));
			}
			fieldNames[i] = fieldValue.toString().trim();
		}
		return fieldNames;
	}

	/**
	 * 根据指定的单元格取数。 创建日期：(2003-11-26 16:22:51)
	 * 
	 * @return java.lang.Object
	 * @param cell
	 *            org.apache.poi.hssf.usermodel.HSSFCell
	 */
	public Object getCellValue(Cell cell) {

		if (cell == null)
			return null;
		String strval = null;
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_NUMERIC:

			double value = cell.getNumericCellValue();
			if (((int) value) == value) {// integer
				return Integer.valueOf((int) value);
			}
			return new nc.vo.pub.lang.UFDouble(value);

		case HSSFCell.CELL_TYPE_STRING:
			// 处理空格的情况
			strval = cell.getStringCellValue();
			strval = (strval == null) || (strval.trim().length() < 1) ? null : strval.trim();
			return strval;

		case HSSFCell.CELL_TYPE_BLANK:

			return null;

		case HSSFCell.CELL_TYPE_BOOLEAN:
			Object strval0 = cell.getBooleanCellValue();
			if (strval0 != null) {
				String o = strval0.toString().trim();
				return o;
			}
			return null;
		default:
			Debug.debug("unsuported sell type");
			return null;
		}
	}
}