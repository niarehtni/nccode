package nc.ui.ta.overtime.register.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hihk.hrta.vo.importovertime.ImportVO;
import nc.itf.ta.IOvertimeApplyApproveManageMaintain;
import nc.itf.ta.IOvertimeRegisterManageMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.ta.overtime.register.model.OvertimeRegAppModel;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.ToftPanelAdaptor;
import nc.ui.uif2.UIState;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 加班登记导入
 * @author xiepch
 *
 */
@SuppressWarnings("serial")
public class ImportOvertimeAction extends HrAction  {
	
	public ImportOvertimeAction(){
		super();
		setCode("importrovertime");
//		setBtnName(PublicLangRes.FANJIAOYAN());
		setBtnName("导入");
		putValue(SHORT_DESCRIPTION, "导入");
	}
	// 设置文件选择对话框
    private JFileChooser fileChooser = null;
    private FileNameExtensionFilter xlsFilter = new FileNameExtensionFilter("MS Office Excel(*.xlsx)", "xlsx");
    private RestOvertimeAction restOvertimeAction;

	@Override
	protected boolean isActionEnable() {
		if (getContext().getPk_org() == null) {
			return false;
		}
		return  getModel().getUiState() == UIState.NOT_EDIT || getModel().getUiState() == UIState.INIT;
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		this.putValue(MESSAGE_AFTER_ACTION, "");
		new Thread(new Runnable() {
			@Override
			public void run() {
				IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(getEntranceUI());
				try{
					if(getContext().getPk_org() == null){
						throw new Exception("请先选择组织");
					}
					if(chooseFile()){
						ShowStatusBarMsgUtil.showStatusBarMsg("正在导入数据中，请耐心等候...", (ToftPanelAdaptor)getEntranceUI());
						progressMonitor.beginTask("提示", IProgressMonitor.UNKNOWN_REMAIN_TIME);
						progressMonitor.setProcessInfo("正在导入数据");
						ImportVO[] ivos = getInfo();
						if(ivos==null || ivos.length == 0){
							throw new BusinessException("选中文件无数据");
						}
						ImportVO[] returnvos = NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class).importdata(ivos);
						ShowImportResult(returnvos,progressMonitor);
					}
				}catch(Exception e){
					progressMonitor.done(); // 进度任务结束
					String errmsg = e.getMessage();
				    Logger.error(e.getMessage());
				    StringWriter sw = new StringWriter();
				    PrintWriter pw = new PrintWriter(sw, true);
				    e.printStackTrace(pw);
				    pw.flush();
				    sw.flush();
				    if ((sw.toString() != null) && (sw.toString().length() > 600))
				       errmsg = sw.toString().substring(0, 500);
					ShowStatusBarMsgUtil.showErrorMsg("导入错误", errmsg,getContext());
				}
			}
		}).start();
		
	}
	
	
	/**获取导入的Excel信息转为ImportVO
	 * @return
	 * @throws Exception
	 */
	private ImportVO[] getInfo() throws Exception{
		File sfile = fileChooser.getSelectedFile();
		if(!sfile.exists()){
			throw new BusinessException("选中的导入数据不存在，请确认！");
		}
		XSSFSheet sheet = openWorkbook(sfile.getAbsolutePath());
		int rows = sheet.getLastRowNum();
		int columns = 9;
		ImportVO[] ivos = new ImportVO[rows-1];
		@SuppressWarnings("restriction")
		String pk_org = getContext().getPk_org();
		String pk_group = getContext().getPk_group();
		TimeZone clientTimeZone = TimeZone.getDefault();
		for(int i = 1 ; i < rows; i++){//从第二行开始读取数据
			XSSFRow xssfRow = sheet.getRow(i);
			ImportVO vo = new ImportVO();
			vo.setPk_org(pk_org);
			vo.setPk_group(pk_group);
			vo.setClientTimeZone(clientTimeZone);
			vo.setRow(i+1);//因为有表头，所以行号+1开始，此row作为提示语行号使用
			XSSFCell cell = null;
			for(int j = 0; j < columns; j++){
				cell = xssfRow.getCell(j);
				if(cell==null){
					continue;
				}
				String content = getValue(cell);
				if(content != null){
					content = content.trim().replaceAll("\n", "");//去除内容中两边空行与所有回车
				}
				switch(j){
					case 0:vo.setPsncode(content);break;
					case 1:vo.setPsnzhname(content);break;
					case 2:vo.setPsnenname(content);break;
					case 3:vo.setOvertimetype(content);break;
					case 4:vo.setTorest(content);break;
					case 5:vo.setVoerdate(content);break;
					case 6:vo.setProjectname(content);break;
					case 7:vo.setVoertimebegin(content);break;
					case 8:vo.setVoertimeend(content);break;
				}
			}
			ivos[i-1] = vo;
		}
		return deleteEmpty(ivos);
	}
	
	private ImportVO[] deleteEmpty(ImportVO[] ivos){
		List<ImportVO> notEmptyList = new ArrayList<ImportVO>();
		for(ImportVO vo : ivos){
			if(!vo.isEmpty()){
				notEmptyList.add(vo);
			}
		}
		if(notEmptyList.size() > 0){
			return notEmptyList.toArray(new ImportVO[]{});
		}
		return null;
	}

	/**
	 * 获取对应某个的具体值
	 * @param xssfCell
	 * @return
	 */
	private String getValue(XSSFCell xssfCell) {
		if (xssfCell.getCellType() == xssfCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(xssfCell.getBooleanCellValue());
		} else if (xssfCell.getCellType() == xssfCell.CELL_TYPE_NUMERIC) {
			if("m/d/yy".equals(xssfCell.getCellStyle().getDataFormatString())){
				return new SimpleDateFormat("yyyy-MM-dd").format(xssfCell.getDateCellValue()).toString();
			}else if("hh:mm".equals(xssfCell.getCellStyle().getDataFormatString())){
				return new SimpleDateFormat("HH:mm").format(xssfCell.getDateCellValue()).toString();
			}
			return String.valueOf(xssfCell.getNumericCellValue());
		} else {
			return String.valueOf(xssfCell.getStringCellValue());
		}
	}

	private void testShowImportInfo(ImportVO[] ivos){
		StringBuilder strbul = new StringBuilder();
		for(ImportVO vo : ivos){
			strbul.append(vo.toString()).append("\r\n");
		}
		File file = new File(fileChooser.getSelectedFile().getParent(), "导入信息1.txt");
		BufferedWriter out = null;
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			out = new BufferedWriter(new FileWriter(file, true));
			out.write(strbul.toString());
		} catch (IOException e) {
			Logger.error(e);
			e.printStackTrace();
		}finally {
			if (null != out) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 显示导入结果
	 * @param ivos
	 * @throws Exception 
	 */
	@SuppressWarnings("restriction")
	private void ShowImportResult(ImportVO[] ivos,IProgressMonitor progressMonitor) throws Exception{
		StringBuilder strbul = new StringBuilder();
		int failureRow = 0;
		for(ImportVO vo : ivos){
			if(vo.isFail()){
				failureRow++;
				strbul.append("第").append(vo.getRow()).append("行失败原因：").append(vo.getErrmsg()).append("\r\n");
			}
		}
		if(strbul.length() == 0){
			strbul.append("全部导入成功！");
			OvertimeRegVO[] returnregvo = ivos[0].getReturnTimeRegVOs();
			afterImportSuccess(returnregvo,progressMonitor);
		}else{
			if(failureRow < ivos.length){
				strbul.append("其它行数据正常，还未导入到库中");
			}else{
				strbul.append("所有数据都存在问题，请确认组织是否选错或模板有误等");
			}
			ShowStatusBarMsgUtil.showStatusBarMsg("导入失败，失败原因请参考《加班登记导入结果.txt》", (ToftPanelAdaptor)getEntranceUI());
		}
		String fullFilePath = fileChooser.getSelectedFile().getParent()+File.separator+"加班登记导入结果.txt";
		File file = new File(fullFilePath);
		BufferedWriter out = null;
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			out = new BufferedWriter(new FileWriter(file, true));
			out.write(strbul.toString());
		} catch (IOException e) {
			Logger.error(e);
			e.printStackTrace();
		}finally {
			if (null != out) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					Logger.error("读取文件异常", e);
				}
			}
			if(!"全部导入成功！".equals(strbul.toString())){
				progressMonitor.done();
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().open(new File(fullFilePath));
				}
			}
		}
	}
	private void afterImportSuccess(OvertimeRegVO[] returnregvo,IProgressMonitor progressMonitor) throws Exception{
		getModel().initModel(returnregvo);
//		int rows[] = new int[returnregvo.length];
//		for(int i = 0 ; i < returnregvo.length;i++){
////			if("Y".equals(ivos[i].getTorest())){
//				rows[i] = i;
////			}
//		}
		ArrayList<OvertimeRegVO> torestList=new ArrayList<OvertimeRegVO>();
		ArrayList<String> otTypeList=new ArrayList<String>();
		for(OvertimeRegVO reg : returnregvo){
			if(reg.getIstorest()==UFBoolean.TRUE){
				OvertimeRegVO nreg =(OvertimeRegVO) reg.clone();
				nreg.setIstorest(UFBoolean.FALSE);
				torestList.add(nreg);
				if(!otTypeList.contains(reg.getPk_overtimetype())){
					otTypeList.add(reg.getPk_overtimetype());
				}
			}
		}
		if(torestList.size()>0){
			
			String[] otTypes =otTypeList.toArray(new String[0]);
			TimeItemCopyVO[] otTypeCopyVOs = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryCopyTypesByDefPK(torestList.get(0).getPk_org(), otTypes, TimeItemCopyVO.OVERTIME_TYPE);
			
			
			OvertimeRegVO[] toRestVos=torestList.toArray(new OvertimeRegVO[torestList.size()]);
			for(OvertimeRegVO reg :toRestVos){
				for(int i=0;i<otTypeCopyVOs.length;i++){
					if(otTypeCopyVOs[i].getPk_timeitem().equals(reg.getPk_overtimetype())){						
						
						UFDouble overtimetorest = otTypeCopyVOs[i].getOvertimetorest();
				        if (overtimetorest == null)
				          overtimetorest = UFDouble.ZERO_DBL;		
						UFDouble torestHour = reg.getLength().multiply(overtimetorest.div(100.0D));
						if(otTypeCopyVOs[i].getRoundmode().equals(0)){						
							torestHour=new UFDouble(Math.floor(torestHour.doubleValue()));
							}
						else if(otTypeCopyVOs[i].getRoundmode().equals(1)){
							torestHour=new UFDouble(Math.ceil(torestHour.doubleValue()));
						}else if(otTypeCopyVOs[i].getRoundmode().equals(2)){
							torestHour=new UFDouble(Math.round(torestHour.doubleValue()));
						}
						reg.setToresthour(torestHour);
					}
				}
			}
			
			String toRestYear=String.valueOf(new UFDate().getYear());
			OvertimeRegVO[] returnvos = NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class).over2Rest(toRestVos, toRestYear, null);
			
		}
//		((OvertimeRegAppModel)getModel()).addSelectedOperaRow(rows);
		ShowStatusBarMsgUtil.showStatusBarMsg("导入成功", (ToftPanelAdaptor)getEntranceUI());
		progressMonitor.done();
//		getRestOvertimeAction().doAction(null);//转调休
	}
	/**
	 * 打开excel
	 * @param fullPath 
	 * @return
	 * @throws Exception 
	 */
	private XSSFSheet openWorkbook(String fullPath) throws Exception {
		
		InputStream is = null;
		XSSFSheet xssfSheet = null;
		try {
			is = new FileInputStream(fullPath);
			 XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
			 xssfSheet = xssfWorkbook.getSheetAt(0);
		} catch (Exception ex) {
			
			Logger.error(ex.getMessage(), ex);
		}
		finally
        {
            IOUtils.closeQuietly(is);
        }
		return xssfSheet;
		
	}
	/**
	 * 导入文件选择对话框
	 */
	public boolean chooseFile()
    {
        if (fileChooser == null)
        {
            fileChooser = new JFileChooser("UFIDA"/*System.getProperty("user.name")*/);
            
            fileChooser.setFileFilter(xlsFilter);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setAcceptAllFileFilterUsed(false);
        }

        if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
        {
            return false;
        }
		
        return true;   
    }

	public RestOvertimeAction getRestOvertimeAction() {
		return restOvertimeAction;
	}

	public void setRestOvertimeAction(RestOvertimeAction restOvertimeAction) {
		this.restOvertimeAction = restOvertimeAction;
	}
}
