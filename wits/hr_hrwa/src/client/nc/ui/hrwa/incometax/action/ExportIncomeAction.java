package nc.ui.hrwa.incometax.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hrwa.IGetAggIncomeTaxData;
import nc.ui.hr.uif2.action.HrAsynAction;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.IMultiRowSelectModel;
import nc.vo.hrwa.incometax.AggIncomeTaxVO;
import nc.vo.hrwa.incometax.IncomeTaxVO;
import nc.vo.pub.BusinessException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportIncomeAction extends HrAsynAction {

	private static final long serialVersionUID = 8546811229283874330L;
	private AbstractAppModel model;
	private IDataOperationService service;
	private String filepath;

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		String filepath = getExportFilePath();
		if (null == filepath) {
			return;
		}
		this.filepath = filepath;
		new Thread() {
			@Override
			public void run() {

				IProgressMonitor progressMonitor = NCProgresses
						.createDialogProgressMonitor(getModel().getContext()
								.getEntranceUI());

				try {
					progressMonitor.beginTask("匯出中...",
							IProgressMonitor.UNKNOWN_REMAIN_TIME);
					exportExcel(getFilepath());
				} catch (Exception e) {
					ShowStatusBarMsgUtil.showErrorMsgWithClear(ResHelper
							.getString("incometax", "2incometax-n-000004"), e
							.getMessage(), getModel().getContext());
				} finally {
					progressMonitor.done(); // 进度任务结束
				}
			}
		}.start();

	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	protected boolean isActionEnable() {
		Object[] objects = ((IMultiRowSelectModel) getModel())
				.getSelectedOperaDatas();
		if (null == objects) {
			return false;
		}
		return true;
	}

	public ExportIncomeAction() {
		setCode("ExportIncomeAction");
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
				"notice", "2notice-tw-000010"/* "导出" */));
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public IDataOperationService getService() {
		return service;
	}

	public void setService(IDataOperationService service) {
		this.service = service;
	}

	public String getExportFilePath() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.xlsx",
				"xlsx");
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(filter);
		fc.setMultiSelectionEnabled(false);
		int result = fc.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (!file.getPath().endsWith(".xlsx")) {
				return file.getPath() + ".xlsx";
			}
			return file.getPath();
		}
		return null;
	}

	public void exportExcel(String filepath) throws BusinessException,
			IOException {
		Object[] objects = ((IMultiRowSelectModel) getModel())
				.getSelectedOperaDatas();
		if (null == objects || objects.length < 1) {
			return;
		}
		IncomeTaxVO[] incomeTaxVOs = new IncomeTaxVO[objects.length];
		for (int i = 0; i < objects.length; i++) {
			AggIncomeTaxVO aggvo = (AggIncomeTaxVO) objects[i];
			incomeTaxVOs[i] = aggvo.getParentVO();
		}
		IGetAggIncomeTaxData getService = NCLocator.getInstance().lookup(
				IGetAggIncomeTaxData.class);
		Map<String, String> psndocMap = getService.getPsnNameByPks(SQLHelper
				.getStrArray(incomeTaxVOs, "pk_psndoc"));
		Map<String, String> WaClassMap = getService.getWaClassName(SQLHelper
				.getStrArray(incomeTaxVOs, "pk_wa_class"));
		String readFilename = "IncomeTax.xlsx";
		InputStream instream = ExportIncomeAction.class
				.getResourceAsStream(readFilename);
		XSSFWorkbook wb = new XSSFWorkbook(instream);
		XSSFSheet sheet = wb.getSheetAt(0);
		int rowNum = 1;
		for (int i = 0; i < incomeTaxVOs.length; i++) {
			IncomeTaxVO interVO = incomeTaxVOs[i];
			XSSFRow row = sheet.getRow(rowNum) == null ? sheet
					.createRow(rowNum) : sheet.getRow(rowNum);
			XSSFRow row2 = sheet.getRow(rowNum - 1);
			for (int j = 0; j < 8; j++) {
				XSSFCell cell = row.getCell(j) == null ? row.createCell(j)
						: row.getCell(j);
				XSSFCellStyle alterableStyle = row2.getCell(j).getCellStyle(); // 获取当前单元格的样式对象
				switch (j) {
				case 0:// 员工编号
					cell.setCellValue(interVO.getCode());
					cell.setCellStyle(alterableStyle);
					break;
				case 1:// 员工姓名
					cell.setCellValue(psndocMap.get(interVO.getPk_psndoc()));
					cell.setCellStyle(alterableStyle);
					break;
				case 2:// 薪资期间
					cell.setCellValue(interVO.getCyearperiod());
					cell.setCellStyle(alterableStyle);
					break;
				case 3:// 薪资方案
					cell.setCellValue(WaClassMap.get(interVO.getPk_wa_class()));
					cell.setCellStyle(alterableStyle);
					break;
				case 4:// 给付总额
					cell.setCellValue(interVO.getTaxbase().intValue());
					cell.setCellStyle(alterableStyle);
					break;
				case 5:// 扣缴税额
					cell.setCellValue(interVO.getCacu_value().intValue());
					cell.setCellStyle(alterableStyle);
					break;
				case 6:// 给付净额
					cell.setCellValue(interVO.getNetincome().intValue());
					cell.setCellStyle(alterableStyle);
					break;
				case 7:// 员工自提金额
					cell.setCellValue(interVO.getPickedup().intValue());
					cell.setCellStyle(alterableStyle);
					break;
				default:
					break;
				}

			}
			rowNum++;
		}
		try {
			// FileSystemView fsv = FileSystemView.getFileSystemView();
			FileOutputStream outstream = new FileOutputStream(filepath);
			// 新建一个输出文件流
			wb.write(outstream);// 进行工作簿存储

			outstream.flush();
			outstream.close();
			Runtime CRuntime = Runtime.getRuntime();
			Process CProce = CRuntime.exec("cmd /c start " + filepath);
			// 关闭进程
			CProce.waitFor();
			CProce.destroy();
		} catch (Exception e) {
			Logger.error(e.getMessage());
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("notice", "2notice-tw-000011")/* "發生錯誤，請檢查！" */);
		}
	}
}
