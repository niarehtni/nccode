package nc.ui.wa.taxaddtional.action;

import java.awt.Container;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.ui.hr.datainterface.FileParas;
import nc.ui.hr.util.TableColResize;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.pub.bill.BillItem;
import nc.ui.trade.excelimport.DetailLoggerMemoryImpl;
import nc.ui.trade.excelimport.IDetailLogger;
import nc.ui.wa.taxaddtional.view.DataImportDia;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.pub.ParaConstant;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

import org.apache.commons.lang.StringUtils;

/**
 * PsndocwadocExcelImporter
 *
 * @author: xuhw
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxExcelImporter
{
	private static final String KEY_DEFAULT_DIR = "default";
	private static final String XLS_SUFFIX = ".xls";
	private static final String XLSX_SUFFIX = ".xlsx";

	private static boolean isSuffixRight(File file)
	{
		return isXLSFile(file);
	}

	private static boolean isXLSFile(File file)
	{
		String name = file.getName().toLowerCase();
		return name.endsWith(XLS_SUFFIX)||name.endsWith(XLSX_SUFFIX);
	}

	private UIFileChooser chooser;
	private String detailMessage;
	private final Container guiParent;
	/** 为了生成和导入文件同一目录下的导入日志文件而设 */
	private String importFileFullName = ".";
	/** 用于记录 所有的日志信息 */
	private final IDetailLogger logger = new DetailLoggerMemoryImpl();
	/** 导出方式 */
	private FileParas paras;

	/** 为了自动记住上次用户选择的文件目录(使用偏好) */
	private Preferences preferences;

	/**
	 * 构造函数
	 *
	 * @author xuhw on 2010-4-12
	 * @param guiParent
	 */
	public TaxExcelImporter(Container guiParent)
	{
		this.guiParent = guiParent;
	}

	/**
	 * 对指定CSV文件进行读取和解析，返回解析后的VO列表
	 *
	 * @param f
	 */
	private List<SuperVO> doImport(File file, BillItem[] inputItems)
	{
		TaxXlsImporter Importt = null;
		try
		{
			Importt = new TaxXlsImporter(this.logger, getLoggerFileName(getImportFileName()));
			Importt.setParas(getParas());
			Importt.setBodyVOName(WASpecialAdditionaDeductionVO.class.getName());
			Importt.setDataImportDia((DataImportDia) this.guiParent);
			Importt.setInputItems(inputItems);
			Importt.setContext(((DataImportDia) this.guiParent).getAppmodel().getContext());
			Importt.importFromFile();
			String strImportMessage = null;
			if (Importt.isError())
			{
				strImportMessage = ResHelper.getString("60130adjmtc","060130adjmtc0144")/*@res "源文件数据存在错误，错误数据已经用红色字体标注，/n 请仔细检查，详细可参考最后一列错误信息。"*/;
			}

			if(strImportMessage != null){
				MessageDialog.showHintDlg(this.guiParent, null, strImportMessage);
			}
			this.logger.writeln(strImportMessage);
			TableColResize.reSizeTable(((DataImportDia) this.guiParent).getBillCardPanel());

			((DataImportDia) this.guiParent).setFileName(getImportFileName());
			((DataImportDia) this.guiParent).setResultMessage(strImportMessage);
			((DataImportDia) this.guiParent).showModal();
		}
		catch (Exception ex)
		{
			Logger.error(ex.getMessage(), ex);
			//不能直接将 this.guiParent(也就是 DataImportDia) 放入,否则会导致内存泄露,尽量将NCapplet 放入,作为parent 
			MessageDialog.showErrorDlg(this.guiParent.getParent(), null, ex.getMessage());
		}
		finally
		{
			setDetailMessage(Importt.getDetailMessage());
		}

		return null;
	}

	/**
	 * 导出模板<BR>
	 * <BR>
	 *
	 * @author xuhw on 2010-4-8
	 * @param parent
	 * @param inputItems
	 * @throws Exception
	 */
	public void exportCSVTempate(BillItem[] inputItems) throws Exception
	{
		//不要将东西放入到getClsFile 里面，放进去就释放不掉了（内存泄漏）
		File file = getClsFile(false, this.guiParent.getParent());
		if (file == null)
		{
			return;
		}
		if (StringUtils.isBlank(file.getAbsolutePath()))
		{
			throw new BusinessException(ResHelper.getString("60130adjmtc","060130adjmtc0146")/*@res "请指定文件位置!"*/);
		}
		exportCSVFromPanel(inputItems);
	}

	/**
	 * 导出模板<BR>
	 * <BR>
	 *
	 * @author xuhw on 2010-4-8
	 * @param parent
	 * @param inputItems
	 * @throws Exception
	 */
	public void exportCSVFromPanel(BillItem[] inputItems) throws Exception
	{
		TaxXlsExporter exporter = new TaxXlsExporter();
		String pk_group = ((DataImportDia) this.guiParent).getContext().getPk_group();
		UFBoolean allowed = UFBoolean.FALSE;
		allowed = SysinitAccessor.getInstance().getParaBoolean(pk_group, ParaConstant.PARTJOB_ADJMGT);
		exporter.setPartShow(allowed.booleanValue());
		exporter.setInputItems(inputItems);
		exporter.setParas(getParas());
		exporter.setBillmodel(((DataImportDia) this.guiParent).getBillCardPanel().getBillModel());
		exporter.setBodyVOName(WASpecialAdditionaDeductionVO.class.getName());
		exporter.exportToFile();
		this.logger.writeln(ResHelper.getString("60130adjmtc","060130adjmtc0147")/*@res "导出成功。"*/);
	}

	private File getClsFile(boolean isOpen, Container parent)
	{
		int returnVal = -1;
		if (isOpen)
		{
			returnVal = getFileChooser().showOpenDialog(parent);
		}
		else
		{
			returnVal = getFileChooser().showSaveDialog(parent);
		}
		File selectedFile = null;
		while (returnVal == JFileChooser.APPROVE_OPTION)
		{
			selectedFile = getFileChooser().getSelectedFile();
			if (selectedFile != null)
			{
				getPreferences().put(KEY_DEFAULT_DIR, selectedFile.getParent());
			}
			// 保存时如果没有指定后缀则自动添加".xls"
			if (getFileChooser().getDialogType() == JFileChooser.SAVE_DIALOG && (!isSuffixRight(selectedFile)))
			{
				selectedFile = new File(selectedFile.getPath() + XLS_SUFFIX);
			}
			if (getFileChooser().accept(selectedFile) && (!isOpen || selectedFile.exists()))
			{
				break;
			}
			else
			{
				selectedFile = null;
				returnVal = getFileChooser().showOpenDialog(null);
			}
		}
		String path = null;
		if (selectedFile != null)
		{
			if (selectedFile.exists() && !isSuffixRight(selectedFile))
			{
				throw new RuntimeException(ResHelper.getString("60130adjmtc","060130adjmtc0148")/*@res "目前导入只支持XLS或者XLSX格式的数据文件！"*/);
			}
			path = selectedFile.getPath().toLowerCase();
		}
		this.importFileFullName = path;
		return selectedFile;
	}

	public String getDetailMessage()
	{
		return this.detailMessage;
	}

	private UIFileChooser getFileChooser()
	{
		if (this.chooser == null)
		{
			this.chooser = new UIFileChooser();
			this.chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			this.chooser.addChoosableFileFilter(new FileFilter()
			{

				@Override
				public boolean accept(File f)
				{
					return f.isDirectory() || isSuffixRight(f);
				}

				@Override
                public String getDescription() {
                    return ResHelper.getString("6001hrimp","06001hrimp0172")/*@res "Excel格式（*.xls，*.xlsx）"*/;
                }
			});
		}
		// 用户选择文件目录偏好
		String preferencesDir = getPreferences().get(KEY_DEFAULT_DIR, System.getProperty("user.dir"));
		this.chooser.setCurrentDirectory(new File(preferencesDir));
		return this.chooser;
	}

	/**
	 * 返回导入文件名(全路径名，不包括后缀)
	 */
	public String getImportFileName()
	{
		return this.importFileFullName;
	}

	/**
	 * 设置导入文件名(全路径名，不包括后缀)
	 */
	public void setImportFileName(String strFileName)
	{
		this.importFileFullName = getLoggerFileName(strFileName);
	}

	/**
	 * 根据导入文件拼出对应的日志文件名 格式为：导入文件名_log_日期时间.txt
	 */
	private String getLoggerFileName(String strFileName)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");
		String name = sdf.format(new Date()).toString();
		return strFileName + "_Bak_" + name;
	}

	/**
	 * 设定导入风格
	 *
	 * @author xuhw on 2010-4-20
	 * @return
	 */
	private FileParas getParas()
	{
		this.paras = new FileParas();
		this.paras.setFileLocation(this.importFileFullName);
		this.paras.setOutputHead(new Boolean(true));
		this.paras.setOutPutLineNo(new Boolean(true));
		this.paras.setHeadAjtBodyFmat(new Boolean(true));
		return this.paras;
	}

	private Preferences getPreferences()
	{
		if (this.preferences == null)
		{
			this.preferences = Preferences.userNodeForPackage(getClass());
		}
		return this.preferences;
	}

	/**
	 * 导入文件数据到系统
	 *
	 * @author xuhw on 2010-4-10
	 * @param guiParent
	 * @param inputItems
	 * @return
	 * @throws Exception
	 */
	public void importFromCls(BillItem[] inputItems) throws Exception
	{
		//不要将东西放入到getClsFile 里面，放进去就释放不掉了（内存泄漏）
		File selectedFile = getClsFile(true, this.guiParent.getParent());
		if (selectedFile == null)
		{
			return;
		}
		doImport(selectedFile, inputItems);
	}

	public void setDetailMessage(String detailMessage)
	{
		this.detailMessage = detailMessage;
	}
}