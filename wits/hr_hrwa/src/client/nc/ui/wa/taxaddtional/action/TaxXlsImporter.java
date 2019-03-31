package nc.ui.wa.taxaddtional.action;

import java.awt.Container;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Vector;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.ui.hr.datainterface.DefaultImporter;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.ui.trade.excelimport.IDetailLogger;
import nc.ui.wa.salaryadjmgt.WaSalaryadjmgtUtility;
import nc.ui.wa.taxaddtional.model.POIExcelUtil;
import nc.ui.wa.taxaddtional.view.DataImportDia;
import nc.vo.hr.dataio.Validator;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;
import nc.vo.wa_tax.TaxupgradeDef;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * WadocXlsImporter
 * 
 * @author: xuhw
 * @date: 2010-4-8 下午04:22:54
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class TaxXlsImporter extends DefaultImporter {
	private LoginContext context;
	/** 当前处理的列号 */
	private int currentColumn = 0;
	/**
	 * 谈判工资标识 W 初始态 N 非谈判工资 Y 谈判工资
	 */
	/** 当前处理的行号 */
	private int currentSequence = 0;
	private boolean error = false;
	private File file = null;
	/** 文件名称 */
	private String fileName;
	private FileInputStream fileOut = null;
	/** 单据模板上项目信息 */
	private BillItem[] inputItems;
	/** 本行的是否谈判工资标志 */
	private boolean isNegotiation_wage = false;
	/** 日志文件名 */
	private final String logFileName;
	/** 用于记录 所有的日志信息 */
	private final IDetailLogger logger;
	/** 导出模板的项目个数 */
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private Workbook wb = null;
	private Container guiParent;

	/** 关联的数据导入UI */
	private DataImportDia dataImportDia;

	public DataImportDia getDataImportDia() {
		return dataImportDia;
	}

	protected String version = "2003";

	public TaxXlsImporter(IDetailLogger logger, String logFileName) throws Exception {
		this.logger = logger;
		this.logFileName = logFileName;
	}

	public void setDataImportDia(DataImportDia dataImportDia) {
		this.dataImportDia = dataImportDia;
	}

	/**
	 * 生成日志文件
	 * 
	 * @author xuhw on 2010-4-12
	 */
	protected void afterImportData() {
		// try {
		// // 进行本地文件日志
		// ImportLogFileFactory.creatLogFile(logFileName,logger.getMessage());
		// } catch (IOException e) {
		// nc.bs.logging.Logger.error(e.getMessage(), e);
		// }
	}

	@Override
	protected void beforeSetData() throws Exception {
		FileOutputStream fileOutStream = null;
		// 存在错误
		try {
			this.afterImportData();
			if (this.error) {
				String type = ".xls";
				if (version.equals("2003")) {
					type = ".xls";
				} else if (version.equals("2007")) {
					type = ".xlsx";
				}
				String newFilename = this.logFileName + type;
				File file2 = new File(newFilename);
				fileOutStream = new FileOutputStream(file2);
				this.wb.write(fileOutStream);
				file2.renameTo(new File(this.getParas().getFileLocation()));
				throw new BusinessException(ResHelper.getString("60130adjmtc", "060130adjmtc0158")/*
																								 * @
																								 * res
																								 * "源文件数据存在错误，错误数据已经输出，检查后请重试！"
																								 */);
			}

			// 根据人员编码，薪资项目，起薪日期排序
			Arrays.sort(this.getVos());

		} finally {
			if (fileOutStream != null) {
				fileOutStream.close();
			}
		}
	}

	/**
	 * 核查单元值是否合法 如果不合法，则将相应的单元cell 变为红色
	 * 
	 * @param item
	 * @param aValue
	 * @return
	 */
	private boolean checkData(BillItem item, Object aValue, StringBuffer sbMessage) {
		if (aValue == null) {
			aValue = "";
		}
		// 当心类型不统一
		int datatype = item.getDataType();
		String strItemKey = item.getKey();
		int length = item.getWidth();
		int decimals = item.getLength();
		String name = item.getName();
		// 检验是否通过
		boolean isLost = true;

		if (!this.notNullValidator(strItemKey, aValue, name, sbMessage)) {
			return false;
		}

		// 数据格式校验
		switch (datatype) {
		case IBillItem.DECIMAL:// 数值型
			// -----为设置精度增加的代码，对于小数的增加如下监听------------------
			if (!Validator.isUFDouble(aValue.toString().trim())) {
				isLost = false;
				sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0159", aValue)/*
																								 * @
																								 * res
																								 * "[{0}] 不是数值类型 不符合设定的格式!"
																								 */);
			} else {
				// 验证数据长度与小数位数
				// 数据长度是指整数部分的长度,小数位数是指小数位的长度
				int digitsLength = 0;// 小数位数默认为0;
				int intLength = 0;// 整数长度默认是0
				int dotPosition = aValue.toString().trim().indexOf(".");// 小数点的位置
				if (decimals > 0) {// 因为设置列的数据长度=整数位数+小数位数+1,所以这里进行还原.
					length = length - decimals - 1;
				}
				if (dotPosition > 0) {
					digitsLength = aValue.toString().trim().substring(dotPosition).length() - 1;
					intLength = aValue.toString().trim().substring(0, dotPosition).length();
				} else {// 没有小数
					digitsLength = 0;
					intLength = aValue.toString().trim().length();
				}
				// 验证数据长度,小数位数
				if (intLength > length || digitsLength > decimals) {
					isLost = false;
					sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0160", aValue)/*
																									 * @
																									 * res
																									 * "[{0}]数据长度或者小数位数  不符合设定的格式!"
																									 */);
				}
			}
			break;
		case IBillItem.INTEGER:// 数值型
			// -----为设置精度增加的代码，对于小数的增加如下监听------------------
			if (!Validator.isUFDouble(aValue.toString().trim())) {
				isLost = false;
				sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0159", aValue)/*
																								 * @
																								 * res
																								 * "[{0}] 不是数值类型 不符合设定的格式!"
																								 */);
			} else {
				// 验证数据长度与小数位数
				// 数据长度是指整数部分的长度,小数位数是指小数位的长度
				int digitsLength = 0;// 小数位数默认为0;
				int intLength = 0;// 整数长度默认是0
				int dotPosition = aValue.toString().trim().indexOf(".");// 小数点的位置
				if (decimals > 0) {// 因为设置列的数据长度=整数位数+小数位数+1,所以这里进行还原.
					length = length - decimals - 1;
				}
				if (dotPosition > 0) {
					digitsLength = aValue.toString().trim().substring(dotPosition).length() - 1;
					intLength = aValue.toString().trim().substring(0, dotPosition).length();
				} else {// 没有小数
					digitsLength = 0;
					intLength = aValue.toString().trim().length();
				}
				// 验证数据长度,小数位数
				if (intLength > length || digitsLength > decimals) {
					isLost = false;
					sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0160", aValue)/*
																									 * @
																									 * res
																									 * "[{0}]数据长度或者小数位数  不符合设定的格式!"
																									 */);
				}
			}
			break;
		case IBillItem.BOOLEAN:
			if (!Validator.isUFBoolean(aValue.toString().trim())) {
				isLost = false;
				sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0161", aValue)/*
																								 * @
																								 * res
																								 * "[{0}]不符合设定的格式  输入的内容必须是{N}或者{Y}!"
																								 */);
			} else {// 验证数据长度
				if (aValue.toString().length() > length) {
					isLost = false;
					sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0162", aValue)/*
																									 * @
																									 * res
																									 * "[{0}]数据长度  不符合设定的格式!"
																									 */);
				}
			}

			break;
		case IBillItem.DATE:
			if (StringUtils.isBlank(aValue.toString()) || !WaSalaryadjmgtUtility.isUFDate(aValue.toString())) {
				isLost = false;
				sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0163", aValue)/*
																								 * @
																								 * res
																								 * "[{0}]不是日期类型，不符合设定的格式!"
																								 */);
			} else {// 验证数据长度
				if (aValue.toString().length() > decimals) {
					isLost = false;
					sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0164", aValue)/*
																									 * @
																									 * res
																									 * "[{0}]数据长度不正确，不符合设定的格式!"
																									 */);
				}
			}
			break;
		// 字符串型不需要解析！因为 aValue 就是一个字符串！
		default:
			if (aValue.toString().length() > decimals) {
				// 验证数据长度
				isLost = false;
				sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0164", aValue)/*
																								 * @
																								 * res
																								 * "[{0}]数据长度不正确，不符合设定的格式!"
																								 */);
			}
			break;
		}

		if (!isLost) {
			sbMessage.append("\n");
			this.logger.writeln(this.getFormatErrorMessage(sbMessage.toString()));
		}
		return isLost;
	}

	@Override
	protected void closeFile() throws Exception {
		if (this.fileOut != null) {
			this.fileOut.close();
		}
	}

//	/**
//	 * 读取cell中的内容<BR>
//	 * <BR>
//	 * 
//	 * @author xuhw on 2010-4-12
//	 * @param cell
//	 * @return
//	 * @throws ParseException
//	 */
//	private String getCellValue(Cell cell) {
//		if (cell == null) {
//			return "";
//		}
//		int type = cell.getCellType();
//		String value = "";
//		switch (type) {
//		case Cell.CELL_TYPE_NUMERIC:
//			if (HSSFDateUtil.isCellDateFormatted(cell)) {
//				value = this.sdf.format(cell.getDateCellValue());
//				break;
//			}
//			value = String.valueOf(cell.getNumericCellValue());
//			if (value.indexOf("E") > 0) {
//				value = parseDouble(value);
//			}
//			break;
//		case Cell.CELL_TYPE_STRING:
//			value = cell.getStringCellValue();
//			break;
//		case Cell.CELL_TYPE_BOOLEAN:
//			value = String.valueOf(cell.getBooleanCellValue());
//			break;
//		default:
//		}
//		return value;
//	}
//
//	public LoginContext getContext() {
//		return this.context;
//	}
//
//	/**
//	 * 处理数值型的科学计数法
//	 * 
//	 * @author xuhw on 2010-8-3
//	 * @param strDigt
//	 * @return
//	 */
//	private String parseDouble(String strDigt) {
//		int pos = strDigt.indexOf("E");
//		int dotPos = strDigt.indexOf(".");
//		String betweenValue = strDigt.substring(dotPos + 1, pos);
//		String temp = "";
//		int power = Integer.parseInt(strDigt.substring(pos + 1));
//		if (power > 0) {
//			StringBuilder sbd = new StringBuilder();
//			sbd.append(strDigt.substring(0, dotPos));
//			for (int index = 0; index < power; index++) {
//				if (index < betweenValue.length()) {
//					sbd.append(betweenValue.charAt(index));
//				} else {
//					sbd.append("0");
//				}
//			}
//
//			if (power < betweenValue.length()) {
//				temp = betweenValue.substring(power);
//				sbd.append((new StringBuilder()).append(".").append(temp).toString());
//			}
//			return sbd.toString();
//		} else {
//			return strDigt;
//		}
//	}

	/**
	 * 返回详细的日志信息
	 */
	public String getDetailMessage() {
		return this.logger.getMessage();
	}

	/**
	 * 返回格式化的成功信息
	 */
	private String getFormatEndMessage(String strEndMessage) {
		return PubEnv.getServerTime().toString() + "--> " + strEndMessage
				+ ResHelper.getString("60130adjmtc", "060130adjmtc0165")/*
																		 * @res
																		 * "数据导入系统结束。"
																		 */+ IDetailLogger.N;
	}

	/**
	 * 返回格式化的错误信息
	 */
	private String getFormatErrorMessage(Exception e) {
		return PubEnv.getServerTime().toString() + "-->Row No." + (this.currentSequence)
				+ ResHelper.getString("60130adjmtc", "060130adjmtc0166")/*
																		 * @res
																		 * "数据导入系统失败。 错误消息："
																		 */+ IDetailLogger.N + e.getMessage()
				+ IDetailLogger.N;
	}

	/**
	 * 返回格式化的错误信息
	 */
	private String getFormatErrorMessage(String strMessage) {
		return PubEnv.getServerTime().toString() + "-->Row No." + (this.currentSequence) + " Column No."
				+ (this.currentColumn + 1) + ResHelper.getString("60130adjmtc", "060130adjmtc0167")/*
																									 * @
																									 * res
																									 * " 数据导入系统失败。 错误消息："
																									 */
				+ IDetailLogger.N + strMessage + IDetailLogger.N;
	}

	public BillItem[] getInputItems() {
		return this.inputItems;
	}

	public String getLogFileName() {
		return this.logFileName + ".txt";
	}

	public boolean isError() {
		return this.error;
	}

	/**
	 * 导入文件数据非空校验
	 * 
	 * @author xuhw on 2010-4-23
	 * @param strItemKey
	 * @param aValue
	 * @param name
	 * @param sbMessage
	 * @return
	 */
	private boolean notNullValidator(String strItemKey, Object aValue, String name, StringBuffer sbMessage) {
		// 谈判工资，档别 级别 必须为空， 其它都为非空
		boolean isLost = true;

		// 在定调资维护节点，【薪资截止日期]】【薪资调整日期】应该不是必输项， 不用判断！xiaorong 还有变动原因，依据文件
		// 20150911 xiejie3 NCdp205468851 失败原因不是必输项， 不用判断！PsndocWadocVO.REASON
		if (WASpecialAdditionaDeductionVO.END_YEAR.equalsIgnoreCase(strItemKey)
				|| WASpecialAdditionaDeductionVO.REASON.equalsIgnoreCase(strItemKey)
				|| WASpecialAdditionaDeductionVO.END_PERIOD.equalsIgnoreCase(strItemKey)) {
			return true;
		}
		if (aValue == null || StringUtils.isBlank(aValue+"")) {
			sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0170", name)/*
																						 * @
																						 * res
																						 * "[{0}]是必输项目，不允许为空值!"
																						 */);
			isLost = false;
		}

		if (!isLost) {
			sbMessage.append("\n");
			this.logger.writeln(this.getFormatErrorMessage(sbMessage.toString()));
			return false;
		}

		return isLost;
	}

	@Override
	protected void openFile() throws Exception {
		this.logger
				.writeln(PubEnv.getServerTime().toString() + ResHelper.getString("60130adjmtc", "060130adjmtc0171")/*
																													 * @
																													 * res
																													 * "--> 数据导入开始。"
																													 */);
		this.fileName = this.getParas().getFileLocation();
		if (!StringUtils.isBlank(this.fileName) && !this.fileName.endsWith(".xls")
				&& !this.fileName.endsWith("xlsx")) {
			this.fileName = (new StringBuilder()).append(this.fileName).append(".xls")
					.toString();
		}
		this.file = new File(this.fileName);
		if (!this.file.exists()) {
			this.logger.writeln(this.getFormatEndMessage(ResHelper.getString("60130adjmtc", "060130adjmtc0172")/*
																												 * @
																												 * res
																												 * " 源文件不存在，不能显示。"
																												 */));
			throw new BusinessException(ResHelper.getString("60130adjmtc", "060130adjmtc0173")/*
																							 * @
																							 * res
																							 * "源文件不存在，不能显示。"
																							 */);
		}
		if (!this.file.canRead()) {
			this.logger.writeln(this.getFormatEndMessage(this.fileName
					+ ResHelper.getString("60130adjmtc", "060130adjmtc0174")/*
																			 * @res
																			 * "不可读,请先关闭该文件。"
																			 */));
			throw new BusinessException(this.fileName + ResHelper.getString("60130adjmtc", "060130adjmtc0174")/*
																											 * @
																											 * res
																											 * "不可读,请先关闭该文件。"
																											 */);
		}

		try {
			fileOut = new java.io.FileInputStream(file);
			version = (fileName.endsWith(".xls") ? "2003" : "2007");
			if (version.equals("2003")) {
				wb = new HSSFWorkbook(fileOut);
			} else if (version.equals("2007")) {
				wb = new XSSFWorkbook(fileOut);
			}

		} catch (IOException e) {
			this.logger.writeln(this.getFormatEndMessage(e.getMessage()));
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * 循环读取数据
	 * 
	 * @author xuhw on 2010-4-10
	 * @throws SQLException
	 * @see nc.ui.hr.datainterface.DefaultImporter#readData(java.lang.Class)
	 */
	@Override
	protected <T extends SuperVO> T[] readData(Class voClass) throws SQLException {
		Sheet sht = this.wb.getSheetAt(0);
		WASpecialAdditionaDeductionVO vo = null;
		// 当前行数据
		Row row = null;
		// 当前单元格对象
		Cell cell = null;
		// 当前单元格的内容
		Object sValue = null;
		// 模板上的所有ITEMs
		BillItem[] vos = this.getInputItems();
		Vector<WASpecialAdditionaDeductionVO> vector = new Vector<WASpecialAdditionaDeductionVO>();
		StringBuffer sbMessage = null;

		// 循环处理每一行数据
		for (this.currentSequence = 0; this.currentSequence <= sht.getLastRowNum(); this.currentSequence++) {
			// 行数据验证消息
			sbMessage = new StringBuffer();
			try {
				vo = (WASpecialAdditionaDeductionVO) this.newInstance(voClass);
				// 处理表头
				if (this.getParas().isOutputHead() && this.currentSequence == 0) {
					continue;
				}
				// 得到一行数据
				row = sht.getRow(this.currentSequence);
				// 处理行号
				boolean linflag = false;
				int offset = 0;// 有行号，则产生偏移
				if (this.getParas().isOutPutLineNo() && !linflag) {
					offset++;
					linflag = true;
				}

				int offset2 = offset - 2;
				for (this.currentColumn = 0; this.currentColumn < 10; this.currentColumn++) {
					// 循环处理每一个单元格
					cell = row.getCell((short) (this.currentColumn + offset));
					if (cell == null) {
						cell = row.createCell((short) (this.currentColumn + offset));
					}
//					sValue = this.getCellValue(cell);
					sValue = POIExcelUtil.getCellData(cell);
					String key = vos[this.currentColumn].getKey();
					//poi 读取数据时，总是多带上.0 这里处理一下 暂且replace.0
					if (sValue != null) {
						sValue = (sValue+"").replace(".0", "");
					}
					//处理读取
					if (key.equals(WASpecialAdditionaDeductionVO.BEGIN_PERIOD) || key.equals(WASpecialAdditionaDeductionVO.END_PERIOD) ) {
						if (sValue != null && (sValue+"").trim().length() == 1) {
							sValue = "0"+(sValue+"").trim();
						}
					}
					// 验证数据是否正确
					if (!this.checkData(vos[this.currentColumn], sValue, sbMessage)) {
						this.renderCell(cell, this.wb);
					} else {
						vo.setAttributeValue(vos[this.currentColumn].getKey(),
								this.getValue(vos[this.currentColumn], sValue));
					}
				}

				// ------------------设置默认值--------------------
				vo.setAttributeValue(WASpecialAdditionaDeductionVO.SOURCE, "import");
				String strErrorMessage = sbMessage.toString();
				if (!StringUtils.isBlank(strErrorMessage)) {
					cell = row.createCell(TaxupgradeDef.ERROR_REASON_LINE, Cell.CELL_TYPE_STRING);
					cell.setCellValue(strErrorMessage);

				}
				vo.setAttributeValue(WASpecialAdditionaDeductionVO.REASON, strErrorMessage);
			} catch (Exception ex) {
				Logger.error(ex.getMessage(), ex);
				this.logger.writeln(this.getFormatErrorMessage(ex));
				continue;
			}

			vector.add(vo);
		}
		this.logger.writeln(this.getFormatEndMessage(""));
		T[] supers = vector.toArray((T[]) Array.newInstance(voClass, vector.size()));
		return supers;
	}

	private Object getValue(BillItem billitem, Object sValue) {
		int dataType = billitem.getDataType();
		Object value = "";
		if (dataType != IBillItem.DECIMAL && (sValue == null || sValue.equals(""))) {
			return null;
		}
		switch (dataType) {
		case IBillItem.DECIMAL:
			if (sValue == null || sValue.equals("")) {
				value = new UFDouble(0);
				break;
			}
			value = new UFDouble(sValue.toString());
			break;
		case IBillItem.STRING:
			value = sValue;
			// 由于从excls中读取的数据如果是字符类型的输入数字，会自动在加上.0结尾
			value = value.toString().replaceAll("\\.0", "");
			break;
		case IBillItem.BOOLEAN:
			value = UFBoolean.valueOf(sValue.toString());
			break;
		case IBillItem.DATE:
			value = new UFDate(sValue.toString());
			break;
		case IBillItem.LITERALDATE:
			value = new UFLiteralDate(sValue.toString());
			break;
		case IBillItem.UFREF:
			UIRefPane refpane = (UIRefPane) billitem.getComponent();
			Vector data = refpane.getRefModel().getData();
			refpane.getRefModel().matchData(refpane.getRefModel().getRefNameField(), (String) sValue);
			if (null != refpane.getRefModel().getPkValue()) {
				value = refpane.getRefModel().getPkValue();
			}
			break;
		default:
			value = sValue;
		}
		return value;
	}

	private Cell renderCell(Cell cell, Workbook wb) {
		CellStyle titleCellStyle = wb.createCellStyle();
		Font font = wb.createFont();
		// font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setColor(Font.COLOR_RED);
		titleCellStyle.setFont(font);
		cell.setCellStyle(titleCellStyle);
		// 告知系统存在错误
		if (!this.error) {
			this.error = true;
		}
		return cell;
	}

	public Container getGuiParent() {
		return guiParent;
	}

	public void setGuiParent(Container guiParent) {
		this.guiParent = guiParent;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public void setInputItems(BillItem[] inputItems) {
		this.inputItems = inputItems;

	}

	protected void setDataToTable() {
		if (getVos() != null) {
			getDataImportDia().getBillCardPanel().getBillModel().clearBodyData();
			getDataImportDia().setBodyVOs(getVos());
		}

	}

}