package nc.ui.wa.psndocwadoc.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.filechooser.FileFilter;

import nc.hr.utils.ResHelper;
import nc.ui.hi.psndoc.action.ActionHelper;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExportGrpInsExTmpAction extends HrAction {

	public ExportGrpInsExTmpAction() {
		// setBtnName("導出模板");
		setBtnName(ResHelper.getString("6007psn", "06007psn0423"));
		setCode("exportGrpInsExTmp");
		putValue("ShortDescription", ResHelper.getString("6007psn", "06007psn0423"));
	}

	private UIFileChooser fc;

	public void doAction(ActionEvent e) throws Exception {
		ActionHelper.resetHintMessage(this);
		if (0 != getFc().showSaveDialog(getEntranceUI())) {
			ActionHelper.setCancelHintMessage(this);
			return;
		}
		File file = fc.getSelectedFile();
		if (file == null) {
			return;
		}
		String path = file.getPath();
		if (!path.toUpperCase().endsWith(".XLS")) {
			path = path + ".xls";
		}
		if (StringUtils.isBlank(path)) {
			throw new BusinessException(ResHelper.getString("6007psn", "06007psn0424"));
		}
		File newFile = new File(path);
		if (newFile.exists()) {
			if (4 != MessageDialog.showYesNoDlg(getEntranceUI(), ResHelper.getString("6007psn", "06007psn0499"),
					ResHelper.getString("6007psn", "06007psn0425"))) {
				ActionHelper.setCancelHintMessage(this);
				return;
			}
		}
		ExportThread ct = new ExportThread(path);
		ct.start();
	}

	private void export(String path) throws BusinessException {
		File cresteFile = new File(path);
		if (cresteFile.exists()) {
		}
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(path);
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();

			HSSFCellStyle cs = wb.createCellStyle();
			HSSFFont littleFont = wb.createFont();
			littleFont.setFontName("SimSun");
			littleFont.setFontHeightInPoints((short) 10);
			cs.setFont(littleFont);
			cs.setAlignment((short) 2);
			cs.setVerticalAlignment((short) 1);

			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue(new HSSFRichTextString("人員編碼"));
			cell.setCellStyle(cs);
			cell = row.createCell((short) 1);
			cell.setCellValue(new HSSFRichTextString("員工號"));
			cell.setCellStyle(cs);
			cell = row.createCell((short) 2);
			cell.setCellValue(new HSSFRichTextString("退保人身份證號"));
			cell.setCellStyle(cs);
			cell = row.createCell((short) 3);
			cell.setCellValue(new HSSFRichTextString("險種編號"));
			cell.setCellStyle(cs);
			cell = row.createCell((short) 4);
			cell.setCellValue(new HSSFRichTextString("退保日期"));
			cell.setCellStyle(cs);

			HSSFCellStyle cs2 = wb.createCellStyle();
			HSSFFont littleFont2 = wb.createFont();
			littleFont2.setFontName("SimSun");
			littleFont2.setFontHeightInPoints((short) 10);
			cs2.setFont(littleFont2);
			cs2.setAlignment((short) 2);
			cs2.setVerticalAlignment((short) 1);

			wb.write(fileOut);

			ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("6007psn", "06007psn0447"), getContext());
			return;
		} catch (Exception ex) {
			throw new BusinessException(ex.getMessage());
		} finally {
			try {
				if (fileOut != null) {
					fileOut.close();
				}
			} catch (Exception ex) {
			}
		}
	}

	class ExportThread extends Thread {
		String path;

		ExportThread(String path) {
			this.path = path;
		}

		public void run() {
			IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(getEntranceUI());
			progressMonitor.beginTask(ResHelper.getString("6001uif2", "06001uif20071"), -1);

			progressMonitor.setProcessInfo(ResHelper.getString("6001uif2", "06001uif20072"));
			try {
				ExportGrpInsExTmpAction.this.export(path);
				ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("6001rtf", "06001rtf0004"), getContext());
			} catch (Exception ex) {
				ShowStatusBarMsgUtil.showErrorMsg(ResHelper.getString("6001rtf", "06001rtf0003"), ex.getMessage(),
						getModel().getContext());
			} finally {
				progressMonitor.done();
			}
		}
	}

	public UIFileChooser getFc() {
		if (fc == null) {
			fc = new UIFileChooser();
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileFilter() {
				String type = "*.xls";
				String ext = ".xls";

				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					}
					return f.getName().endsWith(ext);
				}

				public String getDescription() {
					if (type.equals("*.xls")) {
						return ResHelper.getString("6007psn", "06007psn0392");
					}
					return null;
				}
			});
		}
		return fc;
	}
}
