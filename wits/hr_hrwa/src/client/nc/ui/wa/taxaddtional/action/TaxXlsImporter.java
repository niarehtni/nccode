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
 * @date: 2010-4-8 ����04:22:54
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class TaxXlsImporter extends DefaultImporter {
	private LoginContext context;
	/** ��ǰ������к� */
	private int currentColumn = 0;
	/**
	 * ̸�й��ʱ�ʶ W ��ʼ̬ N ��̸�й��� Y ̸�й���
	 */
	/** ��ǰ������к� */
	private int currentSequence = 0;
	private boolean error = false;
	private File file = null;
	/** �ļ����� */
	private String fileName;
	private FileInputStream fileOut = null;
	/** ����ģ������Ŀ��Ϣ */
	private BillItem[] inputItems;
	/** ���е��Ƿ�̸�й��ʱ�־ */
	private boolean isNegotiation_wage = false;
	/** ��־�ļ��� */
	private final String logFileName;
	/** ���ڼ�¼ ���е���־��Ϣ */
	private final IDetailLogger logger;
	/** ����ģ�����Ŀ���� */
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private Workbook wb = null;
	private Container guiParent;

	/** ���������ݵ���UI */
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
	 * ������־�ļ�
	 * 
	 * @author xuhw on 2010-4-12
	 */
	protected void afterImportData() {
		// try {
		// // ���б����ļ���־
		// ImportLogFileFactory.creatLogFile(logFileName,logger.getMessage());
		// } catch (IOException e) {
		// nc.bs.logging.Logger.error(e.getMessage(), e);
		// }
	}

	@Override
	protected void beforeSetData() throws Exception {
		FileOutputStream fileOutStream = null;
		// ���ڴ���
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
																								 * "Դ�ļ����ݴ��ڴ��󣬴��������Ѿ���������������ԣ�"
																								 */);
			}

			// ������Ա���룬н����Ŀ����н��������
			Arrays.sort(this.getVos());

		} finally {
			if (fileOutStream != null) {
				fileOutStream.close();
			}
		}
	}

	/**
	 * �˲鵥Ԫֵ�Ƿ�Ϸ� ������Ϸ�������Ӧ�ĵ�Ԫcell ��Ϊ��ɫ
	 * 
	 * @param item
	 * @param aValue
	 * @return
	 */
	private boolean checkData(BillItem item, Object aValue, StringBuffer sbMessage) {
		if (aValue == null) {
			aValue = "";
		}
		// �������Ͳ�ͳһ
		int datatype = item.getDataType();
		String strItemKey = item.getKey();
		int length = item.getWidth();
		int decimals = item.getLength();
		String name = item.getName();
		// �����Ƿ�ͨ��
		boolean isLost = true;

		if (!this.notNullValidator(strItemKey, aValue, name, sbMessage)) {
			return false;
		}

		// ���ݸ�ʽУ��
		switch (datatype) {
		case IBillItem.DECIMAL:// ��ֵ��
			// -----Ϊ���þ������ӵĴ��룬����С�����������¼���------------------
			if (!Validator.isUFDouble(aValue.toString().trim())) {
				isLost = false;
				sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0159", aValue)/*
																								 * @
																								 * res
																								 * "[{0}] ������ֵ���� �������趨�ĸ�ʽ!"
																								 */);
			} else {
				// ��֤���ݳ�����С��λ��
				// ���ݳ�����ָ�������ֵĳ���,С��λ����ָС��λ�ĳ���
				int digitsLength = 0;// С��λ��Ĭ��Ϊ0;
				int intLength = 0;// ��������Ĭ����0
				int dotPosition = aValue.toString().trim().indexOf(".");// С�����λ��
				if (decimals > 0) {// ��Ϊ�����е����ݳ���=����λ��+С��λ��+1,����������л�ԭ.
					length = length - decimals - 1;
				}
				if (dotPosition > 0) {
					digitsLength = aValue.toString().trim().substring(dotPosition).length() - 1;
					intLength = aValue.toString().trim().substring(0, dotPosition).length();
				} else {// û��С��
					digitsLength = 0;
					intLength = aValue.toString().trim().length();
				}
				// ��֤���ݳ���,С��λ��
				if (intLength > length || digitsLength > decimals) {
					isLost = false;
					sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0160", aValue)/*
																									 * @
																									 * res
																									 * "[{0}]���ݳ��Ȼ���С��λ��  �������趨�ĸ�ʽ!"
																									 */);
				}
			}
			break;
		case IBillItem.INTEGER:// ��ֵ��
			// -----Ϊ���þ������ӵĴ��룬����С�����������¼���------------------
			if (!Validator.isUFDouble(aValue.toString().trim())) {
				isLost = false;
				sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0159", aValue)/*
																								 * @
																								 * res
																								 * "[{0}] ������ֵ���� �������趨�ĸ�ʽ!"
																								 */);
			} else {
				// ��֤���ݳ�����С��λ��
				// ���ݳ�����ָ�������ֵĳ���,С��λ����ָС��λ�ĳ���
				int digitsLength = 0;// С��λ��Ĭ��Ϊ0;
				int intLength = 0;// ��������Ĭ����0
				int dotPosition = aValue.toString().trim().indexOf(".");// С�����λ��
				if (decimals > 0) {// ��Ϊ�����е����ݳ���=����λ��+С��λ��+1,����������л�ԭ.
					length = length - decimals - 1;
				}
				if (dotPosition > 0) {
					digitsLength = aValue.toString().trim().substring(dotPosition).length() - 1;
					intLength = aValue.toString().trim().substring(0, dotPosition).length();
				} else {// û��С��
					digitsLength = 0;
					intLength = aValue.toString().trim().length();
				}
				// ��֤���ݳ���,С��λ��
				if (intLength > length || digitsLength > decimals) {
					isLost = false;
					sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0160", aValue)/*
																									 * @
																									 * res
																									 * "[{0}]���ݳ��Ȼ���С��λ��  �������趨�ĸ�ʽ!"
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
																								 * "[{0}]�������趨�ĸ�ʽ  ��������ݱ�����{N}����{Y}!"
																								 */);
			} else {// ��֤���ݳ���
				if (aValue.toString().length() > length) {
					isLost = false;
					sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0162", aValue)/*
																									 * @
																									 * res
																									 * "[{0}]���ݳ���  �������趨�ĸ�ʽ!"
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
																								 * "[{0}]�����������ͣ��������趨�ĸ�ʽ!"
																								 */);
			} else {// ��֤���ݳ���
				if (aValue.toString().length() > decimals) {
					isLost = false;
					sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0164", aValue)/*
																									 * @
																									 * res
																									 * "[{0}]���ݳ��Ȳ���ȷ���������趨�ĸ�ʽ!"
																									 */);
				}
			}
			break;
		// �ַ����Ͳ���Ҫ��������Ϊ aValue ����һ���ַ�����
		default:
			if (aValue.toString().length() > decimals) {
				// ��֤���ݳ���
				isLost = false;
				sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0164", aValue)/*
																								 * @
																								 * res
																								 * "[{0}]���ݳ��Ȳ���ȷ���������趨�ĸ�ʽ!"
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
//	 * ��ȡcell�е�����<BR>
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
//	 * ������ֵ�͵Ŀ�ѧ������
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
	 * ������ϸ����־��Ϣ
	 */
	public String getDetailMessage() {
		return this.logger.getMessage();
	}

	/**
	 * ���ظ�ʽ���ĳɹ���Ϣ
	 */
	private String getFormatEndMessage(String strEndMessage) {
		return PubEnv.getServerTime().toString() + "--> " + strEndMessage
				+ ResHelper.getString("60130adjmtc", "060130adjmtc0165")/*
																		 * @res
																		 * "���ݵ���ϵͳ������"
																		 */+ IDetailLogger.N;
	}

	/**
	 * ���ظ�ʽ���Ĵ�����Ϣ
	 */
	private String getFormatErrorMessage(Exception e) {
		return PubEnv.getServerTime().toString() + "-->Row No." + (this.currentSequence)
				+ ResHelper.getString("60130adjmtc", "060130adjmtc0166")/*
																		 * @res
																		 * "���ݵ���ϵͳʧ�ܡ� ������Ϣ��"
																		 */+ IDetailLogger.N + e.getMessage()
				+ IDetailLogger.N;
	}

	/**
	 * ���ظ�ʽ���Ĵ�����Ϣ
	 */
	private String getFormatErrorMessage(String strMessage) {
		return PubEnv.getServerTime().toString() + "-->Row No." + (this.currentSequence) + " Column No."
				+ (this.currentColumn + 1) + ResHelper.getString("60130adjmtc", "060130adjmtc0167")/*
																									 * @
																									 * res
																									 * " ���ݵ���ϵͳʧ�ܡ� ������Ϣ��"
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
	 * �����ļ����ݷǿ�У��
	 * 
	 * @author xuhw on 2010-4-23
	 * @param strItemKey
	 * @param aValue
	 * @param name
	 * @param sbMessage
	 * @return
	 */
	private boolean notNullValidator(String strItemKey, Object aValue, String name, StringBuffer sbMessage) {
		// ̸�й��ʣ����� ���� ����Ϊ�գ� ������Ϊ�ǿ�
		boolean isLost = true;

		// �ڶ�����ά���ڵ㣬��н�ʽ�ֹ����]����н�ʵ������ڡ�Ӧ�ò��Ǳ���� �����жϣ�xiaorong ���б䶯ԭ�������ļ�
		// 20150911 xiejie3 NCdp205468851 ʧ��ԭ���Ǳ���� �����жϣ�PsndocWadocVO.REASON
		if (WASpecialAdditionaDeductionVO.END_YEAR.equalsIgnoreCase(strItemKey)
				|| WASpecialAdditionaDeductionVO.REASON.equalsIgnoreCase(strItemKey)
				|| WASpecialAdditionaDeductionVO.END_PERIOD.equalsIgnoreCase(strItemKey)) {
			return true;
		}
		if (aValue == null || StringUtils.isBlank(aValue+"")) {
			sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0170", name)/*
																						 * @
																						 * res
																						 * "[{0}]�Ǳ�����Ŀ��������Ϊ��ֵ!"
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
																													 * "--> ���ݵ��뿪ʼ��"
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
																												 * " Դ�ļ������ڣ�������ʾ��"
																												 */));
			throw new BusinessException(ResHelper.getString("60130adjmtc", "060130adjmtc0173")/*
																							 * @
																							 * res
																							 * "Դ�ļ������ڣ�������ʾ��"
																							 */);
		}
		if (!this.file.canRead()) {
			this.logger.writeln(this.getFormatEndMessage(this.fileName
					+ ResHelper.getString("60130adjmtc", "060130adjmtc0174")/*
																			 * @res
																			 * "���ɶ�,���ȹرո��ļ���"
																			 */));
			throw new BusinessException(this.fileName + ResHelper.getString("60130adjmtc", "060130adjmtc0174")/*
																											 * @
																											 * res
																											 * "���ɶ�,���ȹرո��ļ���"
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
	 * ѭ����ȡ����
	 * 
	 * @author xuhw on 2010-4-10
	 * @throws SQLException
	 * @see nc.ui.hr.datainterface.DefaultImporter#readData(java.lang.Class)
	 */
	@Override
	protected <T extends SuperVO> T[] readData(Class voClass) throws SQLException {
		Sheet sht = this.wb.getSheetAt(0);
		WASpecialAdditionaDeductionVO vo = null;
		// ��ǰ������
		Row row = null;
		// ��ǰ��Ԫ�����
		Cell cell = null;
		// ��ǰ��Ԫ�������
		Object sValue = null;
		// ģ���ϵ�����ITEMs
		BillItem[] vos = this.getInputItems();
		Vector<WASpecialAdditionaDeductionVO> vector = new Vector<WASpecialAdditionaDeductionVO>();
		StringBuffer sbMessage = null;

		// ѭ������ÿһ������
		for (this.currentSequence = 0; this.currentSequence <= sht.getLastRowNum(); this.currentSequence++) {
			// ��������֤��Ϣ
			sbMessage = new StringBuffer();
			try {
				vo = (WASpecialAdditionaDeductionVO) this.newInstance(voClass);
				// �����ͷ
				if (this.getParas().isOutputHead() && this.currentSequence == 0) {
					continue;
				}
				// �õ�һ������
				row = sht.getRow(this.currentSequence);
				// �����к�
				boolean linflag = false;
				int offset = 0;// ���кţ������ƫ��
				if (this.getParas().isOutPutLineNo() && !linflag) {
					offset++;
					linflag = true;
				}

				int offset2 = offset - 2;
				for (this.currentColumn = 0; this.currentColumn < 10; this.currentColumn++) {
					// ѭ������ÿһ����Ԫ��
					cell = row.getCell((short) (this.currentColumn + offset));
					if (cell == null) {
						cell = row.createCell((short) (this.currentColumn + offset));
					}
//					sValue = this.getCellValue(cell);
					sValue = POIExcelUtil.getCellData(cell);
					String key = vos[this.currentColumn].getKey();
					//poi ��ȡ����ʱ�����Ƕ����.0 ���ﴦ��һ�� ����replace.0
					if (sValue != null) {
						sValue = (sValue+"").replace(".0", "");
					}
					//�����ȡ
					if (key.equals(WASpecialAdditionaDeductionVO.BEGIN_PERIOD) || key.equals(WASpecialAdditionaDeductionVO.END_PERIOD) ) {
						if (sValue != null && (sValue+"").trim().length() == 1) {
							sValue = "0"+(sValue+"").trim();
						}
					}
					// ��֤�����Ƿ���ȷ
					if (!this.checkData(vos[this.currentColumn], sValue, sbMessage)) {
						this.renderCell(cell, this.wb);
					} else {
						vo.setAttributeValue(vos[this.currentColumn].getKey(),
								this.getValue(vos[this.currentColumn], sValue));
					}
				}

				// ------------------����Ĭ��ֵ--------------------
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
			// ���ڴ�excls�ж�ȡ������������ַ����͵��������֣����Զ��ڼ���.0��β
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
		// ��֪ϵͳ���ڴ���
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