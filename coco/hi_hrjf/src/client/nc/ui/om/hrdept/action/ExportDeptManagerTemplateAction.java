package nc.ui.om.hrdept.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileSystemView;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.om.IDeptManageService;
import nc.itf.org.IOrgVersionQryService;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.ls.MessageBox;
import nc.ui.om.hrdept.model.DeptAppModel;
import nc.ui.org.editor.createversion.CreateVersionDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.uif2.LoginContext;
import nc.vo.vorg.DeptStruVersionVO;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ufida.eip.exchange.ui.ExportDialog;
















public class ExportDeptManagerTemplateAction
  extends HrAction
  
  
{
  private static final long serialVersionUID = 1L;
  protected JFileChooser m_chooser = null;
  
  public ExportDeptManagerTemplateAction()
  {
    putValue("Code", "AddNewImportDeptManager");
    setBtnName(ResHelper.getString("menucode", "D60400027"));
    putValue("ShortDescription", ResHelper.getString("menucode", "D60400027"));
  }
  



  public void doAction(ActionEvent e)
    throws Exception
  {	  
	  HSSFWorkbook wb = new HSSFWorkbook();
      HSSFSheet sheet = wb.createSheet("Excel Sheet");
      HSSFRow rowhead = sheet.createRow((short) 0);
      rowhead.createCell((short) 0).setCellValue("門市編碼");
      rowhead.createCell((short) 1).setCellValue("所屬組織");
      rowhead.createCell((short) 2).setCellValue("部門主管");
      rowhead.createCell((short) 3).setCellValue("部門負責人");
      
      HSSFRow row = sheet.createRow(1);
      row.createCell(0).setCellValue("cos042");
      row.createCell(1).setCellValue("cos042");
      row.createCell(2).setCellValue("T000001");
      row.createCell(3).setCellValue("Y");
      
      FileOutputStream fileOut = null;
		try {
			FileSystemView fsv=FileSystemView.getFileSystemView();
			String exportpath=fsv.getHomeDirectory().toString();
			fileOut = new FileOutputStream(exportpath + "\\Dept Manager Import Template" + ".xls");

			   //Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("abc.txt"),"UTF8"));
			   //out.write(user.getFirstName());
			   //out.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
      wb.write(fileOut);
      fileOut.close();
      MessageBox message=new MessageBox();
      message.showMessageDialog("導出模板", "成功導出模板到桌面：Dept Manager Import Template" + ".xls");
      
  }
  
  protected Object getPermissionCheckData()
  {
    Object objs = ((DeptAppModel)getModel()).getSelectedData();
    return objs;
  }
  
  protected boolean isActionEnable() {
    boolean isEnable = false;
    if (super.isActionEnable()) {
      isEnable = ((DeptAppModel)getModel()).canAdd();
    }
    return isEnable;
  }
  
  private IOrgVersionQryService getOrgVersionQryService() {
    return (IOrgVersionQryService)NCLocator.getInstance().lookup(IOrgVersionQryService.class);
  }
  
  private IDeptManageService getDeptManageService() {
    return (IDeptManageService)NCLocator.getInstance().lookup(IDeptManageService.class);
  }
}