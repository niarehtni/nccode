package nc.ui.om.hrdept.action;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import jxl.Sheet;
import nc.bs.dao.BaseDAO;
import nc.bs.dbcache.intf.IDBCacheBS;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.om.IDeptManageService;
import nc.itf.org.IOrgManagerQryService;
import nc.itf.org.IOrgVersionQryService;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.ls.MessageBox;
import nc.ui.om.hrdept.model.DeptAppModel;
import nc.ui.pub.ClientEnvironment;
import nc.ui.pub.beans.MessageDialog;
import nc.vo.pub.BusinessException;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class UpdateImportDeptManagerAction extends HrAction {

	private static final long serialVersionUID = 1L;
	private FileNameExtensionFilter xlsAndcsvFilter = new FileNameExtensionFilter("MS Office Excel(*.xls)", "xls",
			"csv");
	private Sheet sheet;
	private String fileType = "";
	private File selectedFile = null;
	private String logPath = "";
	private BaseDAO baseDAO = null;
	private int importsuccessnumber = 0;
	private int importfailnumber = 0;

	public UpdateImportDeptManagerAction() {
		putValue("Code", "UpdateImportDeptManager");
		setBtnName(ResHelper.getString("menucode", "D60400029"));
		putValue("ShortDescription", ResHelper.getString("menucode", "D60400029"));
	}

	public void doAction(ActionEvent e) throws Exception {
		importsuccessnumber = 0;
		importfailnumber = 0;

		File importedfile = initFileChooser(); // choose import file;
		try {
			createLogFile();
		} catch (Throwable e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // create new log file every time;

		if (importedfile != null) {
			ArrayList<DeptManager> managerList = new ArrayList<DeptManager>();
			managerList = (ArrayList<DeptManager>) readAndStoreImportedFile(importedfile); // read
																							// file,
																							// check
																							// if
																							// has
																							// null
																							// column;

			if (managerList == null) {
				return;
			} else {
				deleteOldDeptManager(managerList);
				resetOrgDeptPrincipal(managerList);
				importDeptManagerList(managerList);
				MessageBox message = new MessageBox();
				message.showMessageDialog("新增導入", "導入成功：" + importsuccessnumber + ", 導入失敗：" + importfailnumber
						+ ", 詳見:" + FilenameUtils.getFullPathNoEndSeparator(selectedFile.getAbsolutePath())
						+ "\\importLog.txt");

			}
		}
	}

	private void deleteOldDeptManager(ArrayList<DeptManager> managerList) {
		for (int i = 0; i < managerList.size(); i++) {
			String deptcode = managerList.get(i).getDeptcode();
			String delete_org_orgmanager = "delete from org_orgmanager where pk_dept=(select top 1 pk_dept from org_dept where code='"
					+ deptcode + "')";
			try {
				NCLocator.getInstance().lookup(IOrgManagerQryService.class)
						.insertOrgManagerBySql(delete_org_orgmanager);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void resetOrgDeptPrincipal(ArrayList<DeptManager> managerList) {
		for (int i = 0; i < managerList.size(); i++) {
			String deptcode = managerList.get(i).getDeptcode();
			String resetprincipalsql = "update org_dept set principal='~' where code='" + deptcode + "'";
			try {
				NCLocator.getInstance().lookup(IOrgManagerQryService.class).insertOrgManagerBySql(resetprincipalsql);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void importDeptManagerList(ArrayList<DeptManager> managerList) throws BusinessException {

		for (int i = 0; i < managerList.size(); i++) {
			boolean managerexistflag = false;
			boolean fuzerenexistflag = false;
			managerexistflag = checkisManagerExist(managerList.get(i));
			fuzerenexistflag = checkifFuzerenExist(managerList.get(i));
			if (fuzerenexistflag == true && managerList.get(i).getIsfuzeren() == true) {
				importfailnumber = importfailnumber + 1;
				addLogtoFile("導入失敗，部門已有負責人：ROW：" + managerList.get(i).rowno + ", Dept:" + managerList.get(i).deptcode
						+ ", Manager：" + managerList.get(i).deptmanager);
			} else if ((managerexistflag == false && fuzerenexistflag == false)
					|| (managerexistflag == false && (fuzerenexistflag == true && managerList.get(i).getIsfuzeren() == false))) {
				importsuccessnumber = importsuccessnumber + 1;
				importDeptManager(managerList.get(i)); // insert
				addLogtoFile("新增導入成功：ROW:" + managerList.get(i).rowno);
			} else if (managerexistflag == true
					&& (fuzerenexistflag == false || (fuzerenexistflag == true && managerList.get(i).getIsfuzeren() == false))) {
				importsuccessnumber = importsuccessnumber + 1;
				updateDeptManager(managerList.get(i)); // update
				addLogtoFile("更新導入成功：ROW:" + managerList.get(i).rowno);
			}
		}

	}

	private void updateDeptManager(DeptManager deptManager) throws BusinessException {
		// TODO Auto-generated method stub

		String principalflag = deptManager.getIsfuzeren() == true ? "Y" : "N";
		String sql = "update org_orgmanager set ts='" + ClientEnvironment.getInstance().getBusinessDate().toString()
				+ "'," + " principalflag='" + principalflag + "'"
				+ " where pk_dept=(select pk_dept from org_dept where code='" + deptManager.getDeptcode() + "')"
				+ " and pk_psndoc=(select pk_psndoc from bd_psndoc where code='" + deptManager.getDeptManager() + "')";
		NCLocator.getInstance().lookup(IOrgManagerQryService.class).insertOrgManagerBySql(sql);

	}

	private void importDeptManager(DeptManager deptManager) throws BusinessException {
		// TODO Auto-generated method stub

		String usercode = deptManager.getDeptManager();
		String deptcode = deptManager.getDeptcode();
		String orgdept = deptManager.getOrg();
		String deptmanager = deptManager.getDeptManager();
		String principalflag = deptManager.getIsfuzeren() == true ? "Y" : "N";
		String ts = ClientEnvironment.getInstance().getBusinessDate().toString();
		NCLocator.getInstance().lookup(IOrgManagerQryService.class)
				.doimportDeptManagerbySQL(usercode, deptcode, orgdept, deptmanager, principalflag, ts);

		if (principalflag == "Y") {
			String update_org_dept_principal = "update org_dept set principal=(select pk_psndoc from bd_psndoc where code='"
					+ deptmanager + "') where code='" + deptcode + "'";
			try {
				NCLocator.getInstance().lookup(IOrgManagerQryService.class)
						.insertOrgManagerBySql(update_org_dept_principal);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private boolean checkisManagerExist(final DeptManager deptManager) throws BusinessException {
		String sql = "select bd_psndoc.code from org_dept "
				+ " inner join org_orgmanager on org_dept.pk_dept=org_orgmanager.pk_dept"
				+ " inner join bd_psndoc on org_orgmanager.pk_psndoc= bd_psndoc.pk_psndoc" + " where org_dept.code='"
				+ deptManager.deptcode.toString() + "'";
		IDBCacheBS idbc = (IDBCacheBS) NCLocator.getInstance().lookup(IDBCacheBS.class.getName());
		ArrayList<?> result = (ArrayList<?>) idbc.runSQLQuery(sql, new ArrayListProcessor());
		if (result != null && result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				Object[] obj = (Object[]) result.get(i);
				String psncode = (String) obj[0];
				if (deptManager.deptmanager.toString().equals(psncode)) {
					// addLogtoFile("部門主管重複,不導入：ROW："+deptManager.rowno+", Dept:"+deptManager.deptcode+", Manager："+deptManager.deptmanager);
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkifFuzerenExist(DeptManager deptManager) throws BusinessException {
		String sql = "select org_orgmanager.principalflag from org_dept "
				+ " inner join org_orgmanager on org_dept.pk_dept=org_orgmanager.pk_dept"
				+ " inner join bd_psndoc on org_orgmanager.pk_psndoc= bd_psndoc.pk_psndoc" + " where org_dept.code='"
				+ deptManager.deptcode.toString() + "'";
		IDBCacheBS idbc = (IDBCacheBS) NCLocator.getInstance().lookup(IDBCacheBS.class.getName());
		ArrayList<?> result = (ArrayList<?>) idbc.runSQLQuery(sql, new ArrayListProcessor());
		if (result != null && result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				Object[] obj = (Object[]) result.get(i);
				String principalflag = (String) obj[0];
				if ("Y".equals(principalflag)) {

					return true;
				}
			}
		}
		return false;
	}

	private File initFileChooser() {
		/* file chooser dialog */
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		fileChooser.setFileFilter(xlsAndcsvFilter);
		int result = fileChooser.showOpenDialog(new JDialog());
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			String fileName = selectedFile.getName();
			System.out.println("Selected file: " + selectedFile.getAbsolutePath());

			// check file type
			if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
				fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
			if (!(fileType.equals("xls") || fileType.equals("csv") || fileType.equals("xlsx"))) {
				MessageDialog.showErrorDlg(this.getEntranceUI(), "錯誤", "文件類型應該為xls或csv");
				return null;
			}
			return selectedFile;
		} else {
			return null;
		}

	}

	private ArrayList<?> readAndStoreImportedFile(File selectedFile) {
		ArrayList<DeptManager> importData = new ArrayList<DeptManager>();
		/* read excel file */
		try {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(selectedFile));
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow row;
			HSSFCell cell;

			int rows; // No of rows
			rows = sheet.getPhysicalNumberOfRows();

			int cols = 0; // No of columns
			int tmp = 0;

			// This trick ensures that we get the data properly even if it
			// doesn't start from first few rows
			for (int i = 0; i < 10 || i < rows; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if (tmp > cols)
						cols = tmp;
				}
			}

			/* check null */
			addLogtoFile("Null checking...");
			Boolean nullexit = false;
			for (int r = 1; r < rows; r++) { // start from row 1;
				row = sheet.getRow(r);
				if (row.getCell(0) == null || row.getCell(1) == null || row.getCell(2) == null
						|| row.getCell(3) == null) {
					addLogtoFile("Error, Null column found at ROW: " + (r + 1));
					nullexit = true;
				}
			}

			if (nullexit == true) {
				addLogtoFile("Null checking...FAIL!");
				MessageDialog.showErrorDlg(this.getEntranceUI(), "錯誤",
						"導入文件存在空值！日誌:" + FilenameUtils.getFullPathNoEndSeparator(selectedFile.getAbsolutePath())
								+ "\\importLog.txt");
				return null;
			} else {
				addLogtoFile("Null checking...PASS!");
			}

			// if no null column
			for (int r = 1; r < rows; r++) { // start from row 1;
				row = sheet.getRow(r);
				if (row != null) {
					DeptManager managerinfo = new DeptManager();
					managerinfo.setRowNo(r + 1);
					managerinfo.setDeptcode(row.getCell(0).toString());
					managerinfo.setOrg(row.getCell(1).toString());
					managerinfo.setDeptManager(row.getCell(2).toString());
					managerinfo.setIsfuzeren(row.getCell(3).toString().equals("Y") ? true : false);
					importData.add(managerinfo);
				}
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
		return importData;
	}

	public void createLogFile() throws Throwable {

		File writename = new File(FilenameUtils.getFullPathNoEndSeparator(selectedFile.getAbsolutePath())
				+ "\\importLog.txt");
		writename.createNewFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(writename));
		out.write("覆蓋導入 \r\nLog file at: " + ClientEnvironment.getInstance().getBusinessDate().toString());
		out.write("\r\n");
		out.flush();
		out.close();

	}

	public void addLogtoFile(String args) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
					FilenameUtils.getFullPathNoEndSeparator(selectedFile.getAbsolutePath()) + "\\importLog.txt", true)));
			out.println(args);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected Object getPermissionCheckData() {
		Object objs = ((DeptAppModel) getModel()).getSelectedData();
		return objs;
	}

	protected boolean isActionEnable() {
		boolean isEnable = false;
		if (super.isActionEnable()) {
			isEnable = ((DeptAppModel) getModel()).canAdd();
		}
		return isEnable;
	}

	private IOrgVersionQryService getOrgVersionQryService() {
		return (IOrgVersionQryService) NCLocator.getInstance().lookup(IOrgVersionQryService.class);
	}

	private IDeptManageService getDeptManageService() {
		return (IDeptManageService) NCLocator.getInstance().lookup(IDeptManageService.class);
	}
}