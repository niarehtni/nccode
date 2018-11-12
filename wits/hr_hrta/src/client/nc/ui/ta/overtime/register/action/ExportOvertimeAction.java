package nc.ui.ta.overtime.register.action;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import nc.bs.framework.common.NCLocator;
import nc.hihk.hrta.vo.importovertime.ExportVO;
import nc.itf.ta.IOvertimeRegisterManageMaintain;
import nc.ui.hr.tools.rtf.jacob.WordUtil;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.ToftPanelAdaptor;

import org.apache.commons.io.IOUtils;

/**
 * 加班登记导入
 * @author xiepch
 *
 */
@SuppressWarnings("serial")
public class ExportOvertimeAction extends HrAction  {
	
	public ExportOvertimeAction(){
		super();
		setCode("exportrovertime");
//		setBtnName(PublicLangRes.FANJIAOYAN());
		setBtnName("导出模板");
		putValue(SHORT_DESCRIPTION, "导出模板");
	}
	// 设置文件选择对话框
    private JFileChooser fileChooser = null;
    private FileNameExtensionFilter xlsFilter = new FileNameExtensionFilter("MS Office Excel(*.xlsx)", "xlsx");

	@Override
	protected boolean isActionEnable() {
		return true;
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		this.putValue(MESSAGE_AFTER_ACTION, "");
		new Thread(new Runnable() {
			@Override
			public void run() {
				IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(getEntranceUI());
				try{
					File file = chooseFile();
					if( file!= null){
						ShowStatusBarMsgUtil.showStatusBarMsg("正在导出模板，请耐心等候...", (ToftPanelAdaptor)getEntranceUI());
						progressMonitor.beginTask("提示", IProgressMonitor.UNKNOWN_REMAIN_TIME);
						progressMonitor.setProcessInfo("正在导出数据");
						ExportVO evo = NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class).exportdata();
						byte [] bytes = evo.getDatabytes();
						String exportFilepath = file.getAbsolutePath()+File.separator+"加班登记数据.xlsx";;
						saveTemplate(exportFilepath, bytes);
				        progressMonitor.done();
						ShowStatusBarMsgUtil.showStatusBarMsg("导出成功", (ToftPanelAdaptor)getEntranceUI());
						
					}
				}catch(Exception e){
					progressMonitor.done(); // 进度任务结束
					ShowStatusBarMsgUtil.showErrorMsg("导出错误", e.getMessage(),getContext());
				}
			}
		}).start();
		
	}
	/**
	 * 保存模板
	 * @param docCVFilePath
	 * @param docByte
	 * @throws Exception
	 */
	private void saveTemplate(String docCVFilePath,byte[] docByte) throws Exception{
		 byte ba[] = docByte;
		 if (ba != null){
			 WordUtil.createDirectoryAsNeeded(docCVFilePath, null);
			 ByteArrayInputStream bai = new ByteArrayInputStream(ba);
	         GZIPInputStream gzip = new GZIPInputStream(bai);
	         byte buffer[] = new byte[1024];
	         FileOutputStream fos = new FileOutputStream(docCVFilePath);
	         do{
                int len = gzip.read(buffer, 0, buffer.length);
                if (len == -1)
                {
                    break;
                }
                fos.write(buffer, 0, len);
            }
            while (true);
            fos.flush();
            fos.close();
		 }
	}
	/**
	 * 选择要导出的路径和文件名
	 * @return
	 */
	private File chooseFile(){
		fileChooser = new UIFileChooser();
		fileChooser.setDialogType(JFileChooser.FILES_ONLY);
		fileChooser.setFileSelectionMode(UIFileChooser.DIRECTORIES_ONLY);

		int iReturnValue = fileChooser.showOpenDialog(getEntranceUI());
		if (iReturnValue == JFileChooser.APPROVE_OPTION)
		{
			return fileChooser.getSelectedFile();
		}
		return null;
	}
}
