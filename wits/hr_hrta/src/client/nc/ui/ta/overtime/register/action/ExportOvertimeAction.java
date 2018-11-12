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
 * �Ӱ�Ǽǵ���
 * @author xiepch
 *
 */
@SuppressWarnings("serial")
public class ExportOvertimeAction extends HrAction  {
	
	public ExportOvertimeAction(){
		super();
		setCode("exportrovertime");
//		setBtnName(PublicLangRes.FANJIAOYAN());
		setBtnName("����ģ��");
		putValue(SHORT_DESCRIPTION, "����ģ��");
	}
	// �����ļ�ѡ��Ի���
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
						ShowStatusBarMsgUtil.showStatusBarMsg("���ڵ���ģ�壬�����ĵȺ�...", (ToftPanelAdaptor)getEntranceUI());
						progressMonitor.beginTask("��ʾ", IProgressMonitor.UNKNOWN_REMAIN_TIME);
						progressMonitor.setProcessInfo("���ڵ�������");
						ExportVO evo = NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class).exportdata();
						byte [] bytes = evo.getDatabytes();
						String exportFilepath = file.getAbsolutePath()+File.separator+"�Ӱ�Ǽ�����.xlsx";;
						saveTemplate(exportFilepath, bytes);
				        progressMonitor.done();
						ShowStatusBarMsgUtil.showStatusBarMsg("�����ɹ�", (ToftPanelAdaptor)getEntranceUI());
						
					}
				}catch(Exception e){
					progressMonitor.done(); // �����������
					ShowStatusBarMsgUtil.showErrorMsg("��������", e.getMessage(),getContext());
				}
			}
		}).start();
		
	}
	/**
	 * ����ģ��
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
	 * ѡ��Ҫ������·�����ļ���
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
