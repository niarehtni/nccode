package nc.vo.wa_tax;


import nc.ui.wa.taxaddtional.model.POIExcelUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 *
 * @ClassName: ExcelUtil
 * @Description: TODO(这里用一句话描述这个类的作用) ExcelUtil工具类实现功能:<BR>
 *               导出时传入list<T>,即可实现导出为一个excel,其中每个对象Ｔ为Excel中的一条记录.<BR>
 *               导入时读取excel,得到的结果是一个list<T>.T是自己定义的对象.<BR>
 *               需要导出的实体对象只需简单配置注解就能实现灵活导出,通过注解您可以方便实现下面功能:<BR>
 *               1.实体属性配置了注解就能导出到excel中,每个属性都对应一列.<BR>
 *               2.列名称可以通过注解配置.<BR>
 *               3.导出到哪一列可以通过注解配置.<BR>
 *               4.鼠标移动到该列时提示信息可以通过注解配置.<BR>
 *               5.用注解设置只能下拉选择不能随意填写功能.<BR>
 *               6.用注解设置是否只导出标题而不导出内容,这在导出内容作为模板以供用户填写时比较实用.<BR>
 */
public class ExcelUtil<T> {
	Class<T> clazz;

	public ExcelUtil(Class<T> clazz) {
		this.clazz = clazz;
	}
    public List<String> exportiem=null;
	public Map<String,String> columnmap=new HashMap();
	/**
	 *
	 * @Title: importExcel
	 * @Description: TODO(导入ExcelData)
	 * @param sheetName
	 * @param file
	 * @throws BusinessException 设定文件
	 * @return List<T> 返回类型
	 * @throws
	 * @date 2014年12月12日 上午11:24:48
	 */
	public List<T> importExcel(String sheetName, MultipartFile file){
		List<T> list = new ArrayList<T>();
		InputStream fis = null;
		Workbook book = null;
		try {
			String filename = file.getOriginalFilename();

			if (filename == null || "".equals(filename)) {
				return null;
			}
			if (!filename.endsWith("xls") && !filename.endsWith("xlsx")) {
				throw new RuntimeException("传入的文件不是excel。");
			}
			fis = file.getInputStream();

			if (filename.endsWith("xlsx")) {
				book = new XSSFWorkbook(fis);
			} else {
				book = new HSSFWorkbook(fis);
			}
			// HSSFWorkbook book = new HSSFWorkbook(input);
			Sheet sheet = null;
			if (!sheetName.trim().equals("")) {
				sheet = book.getSheet(sheetName);// 如果指定sheet名,则取指定sheet中的内容.
			}
			if (sheet == null) {
				sheet = book.getSheetAt(0);// 如果传入的sheet名不存在则默认指向第1个sheet.
			}
			int rows = sheet.getLastRowNum();// 得到数据的行数
			if (rows > 0) {// 有数据时才处理
				Field[] allFields = clazz.getDeclaredFields();// 得到类的所有field.
				Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>();// 定义一个map用于存放列的序号和field.
				Map<String, Field> fieldsMapOther = new HashMap<String, Field>();// 定义一个map用于存放列的序号和field.
				int colcount=0;
				for (Field field : allFields) {
					// 将有注解的field存放到map中.
					if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
						ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
						String column="";
						if (StringUtils.isNotBlank(attr.column())) {
							column=attr.column();
						}
						if(columnmap.size()>0)
						{
							column=columnmap.get(field.getName());
						}
						if(StringUtils.isBlank(column))
						{
                           continue;
						}
						int col =getExcelCol(column) ;// 获得列号
						// System.out.println(col + "====" + field.getName());
						field.setAccessible(true);// 设置类的私有字段属性可访问.
						fieldsMap.put(col, field);

						if (!StringUtils.isEmpty(attr.dbdata_column()) && clazz.getGenericSuperclass() != null) {
							Class superClass = clazz.getSuperclass();// 父类
							Field fatherField = superClass.getDeclaredField(attr.dbdata_column());
							fatherField.setAccessible(true);// 设置类的私有字段属性可访问.
							fieldsMapOther.put(attr.dbdata_column(), fatherField);
						}
					}
				}

				boolean is_first = true;
				// 迭代行
				for (Iterator<Row> iter = (Iterator<Row>) sheet.rowIterator(); iter.hasNext();) {
					Row row = iter.next();
					if (is_first) {
						// 从第2行开始取数据,默认第一行是表头.
						is_first = false;
						continue;
					}
					T entity = null;
					// 迭代列
					int columnIndex = 0;
					for (int i = 0; i < row.getLastCellNum(); i++) {
						Cell cell = row.getCell(i);
						String content = "";
						if (cell != null) {
							if ((cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC || cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA)) {
								BigDecimal bd = new BigDecimal(cell.getNumericCellValue());
								String num = bd.toString()+ "";
								String num1 = num.substring(num.lastIndexOf(".") + 1);
								if ( num1.length()== 0) {
									num = num.substring(0, num.lastIndexOf(".")+1);
								}
								content = num;
							} else {
								content = cell.getStringCellValue();
							}
						}
						entity = (entity == null ? clazz.newInstance() : entity);// 如果不存在实例则新建.

						Field field = fieldsMap.get(columnIndex);// 从map中得到对应列的field.
						columnIndex++;
						if (field == null) {
							continue;
						}

						// 将有注解的field存放到map中.
						if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
 							ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
							String data_conlum = attr.dbdata_column();
							if(StringUtils.isEmpty(data_conlum))
							{
								columnmap.containsKey(file.getName());
							}
							if (!StringUtils.isEmpty(data_conlum)) {
								Field fa_field = fieldsMapOther.get(data_conlum);
								String[] strs = content.split("-");
								fa_field.set(entity, strs[1]);
							}
						}

						// 取得类型,并根据对象类型设置值.
						Class<?> fieldType = field.getType();
						if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
							if(StringUtils.isNotBlank(content)){
								field.set(entity, Integer.parseInt(content));
							}
						} else if (String.class == fieldType) {
							field.set(entity, String.valueOf(content));
						} else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
							field.set(entity, Long.valueOf(content));
						} else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
							field.set(entity, Float.valueOf(content));
						} else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
							field.set(entity, Short.valueOf(content));
						} else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
							field.set(entity, Double.valueOf(content));
						} else if (BigDecimal.class==fieldType)
						{
							if(StringUtils.isBlank(content))
							{
								content="0";
							}
							field.set(entity, BigDecimal.valueOf(Double.valueOf(content)));
						}
						else if (Character.TYPE == fieldType) {
							if ((content != null) && (content.length() > 0)) {
								field.set(entity, Character.valueOf(content.charAt(0)));
							} else {
								field.set(entity, content);
							}
						}
					}

					if (entity != null) {
						list.add(entity);
					}
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
		return list;
	}

	/**
	 *
	 * @Title: exportExcel
	 * @Description: TODO(对list数据源将其里面的数据导入到excel表单)
	 * @param list
	 * @param sheetName
	 * @param sheetSize
	 * @param output
	 * @return boolean 返回类型
	 * @throws IllegalAccessException 
	 * @throws
	 * @date 2014年12月12日 上午11:25:30
	 */
	public Workbook exportExcel(List<T> list, String sheetName, int sheetSize, OutputStream output) throws IllegalAccessException {

		Field[] allFields = clazz.getDeclaredFields();// 得到所有定义字段
		List<Field> fields = new ArrayList<Field>();
		// 得到所有field并存放到一个list中.
		for (Field field : allFields) {
			if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
				fields.add(field);
			}
		}
		HSSFWorkbook workbook = new HSSFWorkbook();// 产生工作薄对象

		// excel2003中每个sheet中最多有65536行,为避免产生错误所以加这个逻辑.
		if (sheetSize > 65536 || sheetSize < 1) {
			sheetSize = 65536;
		}
		double sheetNo = Math.ceil(list.size() / sheetSize);// 取出一共有多少个sheet.
		for (int index = 0; index <= sheetNo; index++) {
			HSSFSheet sheet = workbook.createSheet();// 产生工作表对象
			if(sheetNo>1)
			{
				workbook.setSheetName(index, sheetName + "_第" + index + "页");// 设置工作表的名称.
			}
			else {
				workbook.setSheetName(index, sheetName);// 设置工作表的名称.
			}
			HSSFRow row;
			HSSFCell cell;// 产生单元格

			row = sheet.createRow(0);// 产生一行
			// 写入各个字段的列头名称
			CellStyle cellStyleHead = POIExcelUtil.getRowCell(workbook, true);
			int colcount=0;
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
				boolean isExport=attr.isExport();
				if(exportiem!=null&&exportiem.contains(field.getName()))
				{
					isExport=true;
				}
				if (isExport) {
					int col = colcount;
					colcount++;
					if (StringUtils.isNotBlank(attr.column())) {
						col = getExcelCol(attr.column());
					}
					cell = row.createCell(col);// 创建列
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);// 设置列中写入内容为String类型
					cell.setCellValue(attr.name());// 写入列名
					cell.setCellStyle(cellStyleHead);
					// 如果设置了提示信息则鼠标放上去提示.
					if (!attr.prompt().trim().equals("")) {
						setHSSFPrompt(sheet, "", attr.prompt(), 1, sheetSize - 1, col, col);// 这里默认设了2-101列提示.
					}
					// 如果设置了combo属性则本列只能选择不能输入
					if (attr.combo().length > 0) {
						setHSSFValidation(sheet, attr.combo(), 1, sheetSize - 1, col, col);// 这里默认设了2-101列只能选择不能输入.
					}
				}
			}

			int startNo = index * sheetSize;
			int endNo = Math.min(startNo + sheetSize, list.size());
			CellStyle cellStyle = POIExcelUtil.getRowCell(workbook, false);
			// 写入各条记录,每条记录对应excel表中的一行
			for (int i = startNo; i < endNo; i++) {
				row = sheet.createRow(i + 1 - startNo);
				T vo = (T) list.get(i); // 得到导出对象.
				colcount=0;
				for (int j = 0; j < fields.size(); j++) {
					Field field = fields.get(j);// 获得field.
					field.setAccessible(true);// 设置实体类私有属性可访问
					ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
					try {
						// 根据ExcelVOAttribute中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
						boolean isExport=attr.isExport();
						if(exportiem!=null&&exportiem.contains(field.getName()))
						{
							isExport=true;
						}
						if (isExport) {
							int col = colcount;
							colcount++;
							// 根据指定的顺序获得列号
							if (StringUtils.isNotBlank(attr.column())) {
								col = getExcelCol(attr.column());
							}
							if (attr.isNo()) {
								cell = row.createCell(col);// 创建cell
								cell.setCellType(HSSFCell.CELL_TYPE_STRING);
								cell.setCellValue(i + 1);// 如果数据存在就填入,不存在填入空格.
							} else {
								cell = row.createCell(col);// 创建cell
								cell.setCellType(HSSFCell.CELL_TYPE_STRING);
								Class<?> classType = (Class<?>) field.getType();

								String value=processData(classType,field.get(vo));
								if(attr.combo().length>0&&value!=null)
								{
									String[] aselect=attr.combo();
									value=aselect[Integer.valueOf(value)];
								}
								cell.setCellValue(value);// 如果数据存在就填入,不存在填入空格.
							}
							cellStyle.setAlignment(HSSFCellStyle.ALIGN_GENERAL);
							cellStyle.setWrapText(true);
							cell.setCellStyle(cellStyle);
						}
					} catch (IllegalArgumentException e) {
					}
				}
			}

			//自动调整列宽度
			for (int j = 0; j < fields.size(); j++) {
				sheet.autoSizeColumn((short)j); //调整列宽度
			}
			//获取当前列的宽度，然后对比本列的长度，取最大值
			for (int columnNum = 0; columnNum <= fields.size(); columnNum++)
			{
				int columnWidth = sheet.getColumnWidth(columnNum) / 256;
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++)
				{
					Row currentRow;
					//当前行未被使用过
					if (sheet.getRow(rowNum) == null)
					{
						currentRow = sheet.createRow(rowNum);
					}
					else
					{
						currentRow = sheet.getRow(rowNum);
					}

					if(currentRow.getCell(columnNum) != null)
					{
						Cell currentCell = currentRow.getCell(columnNum);
						int length = currentCell.toString().getBytes().length;
						if (columnWidth < length)
						{
							columnWidth = length;
						}
					}
				}
				int colWidth = columnWidth*2*256;
				if(colWidth<255*256){
					sheet.setColumnWidth(columnNum, colWidth < 3000 ? 3000 : colWidth);
				}else{
					sheet.setColumnWidth(columnNum,6000 );
				}
			}
		}
		try {
			workbook.write(output);
			output.flush();
			output.close();
			return workbook;
		} catch (IOException e) {
			return null;
		}

	}

	/**
	 * 将EXCEL中A,B,C,D,E列映射成0,1,2,3
	 *
	 * @param col
	 */
	private static int getExcelCol(String col) {
		col = col.toUpperCase();
		// 从-1开始计算,字母重1开始运算。这种总数下来算数正好相同。
		int count = -1;
		char[] cs = col.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			count += (cs[i] - 64) * Math.pow(26, cs.length - 1 - i);
		}
		return count;
	}

	/**
	 * 设置单元格上提示
	 *
	 * @param sheet  要设置的sheet.
	 * @param promptTitle 标题
	 * @param promptContent 内容
	 * @param firstRow  开始行
	 * @param endRow 结束行
	 * @param firstCol   开始列
	 * @param endCol  结束列
	 * @return 设置好的sheet.
	 */
	private static HSSFSheet setHSSFPrompt(HSSFSheet sheet, String promptTitle, String promptContent, int firstRow, int endRow, int firstCol, int endCol) {
		// 构造constraint对象
		DVConstraint constraint = DVConstraint.createCustomFormulaConstraint("DD1");
		// 四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
		// 数据有效性对象
		HSSFDataValidation data_validation_view = new HSSFDataValidation(regions, constraint);
		data_validation_view.createPromptBox(promptTitle, promptContent);
		sheet.addValidationData(data_validation_view);
		return sheet;
	}

	/**
	 * 设置某些列的值只能输入预制的数据,显示下拉框.
	 *
	 * @param sheet  要设置的sheet.
	 * @param textlist  下拉框显示的内容
	 * @param firstRow  开始行
	 * @param endRow  结束行
	 * @param firstCol  开始列
	 * @param endCol  结束列
	 * @return 设置好的sheet.
	 */
	private static HSSFSheet setHSSFValidation(HSSFSheet sheet, String[] textlist, int firstRow, int endRow, int firstCol, int endCol) {
		// 加载下拉列表内容
		DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);
		// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
		// 数据有效性对象
		HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
		sheet.addValidationData(data_validation_list);
		return sheet;
	}

	/**
	 * 处理数据类型
	 *
	 * @return
	 */
	private static String processData(Class<?> classType,Object value) {
		String returnObj = null;
		if (value == null || StringUtils.isEmpty(value + "")) {
			return "";
		}
		if (classType.isAssignableFrom(Date.class)) {
			Date dvalue = (Date) value;
			returnObj = DateUtil.DateToString(dvalue, "yyyy-MM-dd HH:mm");
		}
		else if(classType.isAssignableFrom(BigDecimal.class))
		{

			BigDecimal bd = new BigDecimal(value.toString());
			returnObj = bd.setScale(2, RoundingMode.HALF_UP).toString();
		}
		else
			returnObj=String.valueOf(value);
		return returnObj;
	}

}
