package nc.ui.wa.taxaddtional.model;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主要提供对Excel的各种处理,侧重点是取数据
 *
 * @author xuhw
 */
public class POIExcelUtil {
 
    static SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static short[] yyyyMMdd = {14, 31, 57, 58, 179, 184, 185, 186, 187, 188};
    static short[] HHmmss = {20, 32, 190, 191, 192};
    static List<short[]> yyyyMMddList = Arrays.asList(yyyyMMdd);
    static List<short[]> hhMMssList = Arrays.asList(HHmmss);

    /**
     * 根据路径,获取WorkBook对象
     *
     * @param filePath 文件路径
     * @return workbook
     * @throws Exception
     */
    public static Workbook getExcelWorkbook(String filePath) throws Exception {
        Workbook workbook = null;
        File file = new File(filePath);
        if (file.exists()) {
            workbook = getWorkBookByStream(new FileInputStream(file));
        }
        return workbook;
    }

    /**
     * 根据输入流ins获取WorkBook对象
     *
     * @param ins 输入流
     * @return workbook
     * @throws Exception
     */
    public static Workbook getWorkBookByStream(InputStream ins) throws Exception {
        return WorkbookFactory.create(ins);
    }

    /**
     * 根据Workbook,sheetIndex获取sheet对象
     *
     * @param book   WorkBook对象
     * @param number sheetIndex ,从1开始
     * @return sheet
     * @throws Exception
     */
    public static Sheet getSheetByNum(Workbook book, int number) throws Exception {
        return book.getSheetAt(number - 1);
    }

    /**
     * 根据 Workbook对象返回该Workbook对象中所有sheet的Map数组.
     *
     * @param book
     * @return Map<sheetIndex , sheetName>
     * @throws Exception
     */
    public static Map<Integer, String> getSheetNameByBook(Workbook book) throws Exception {
        Map<Integer, String> map = new LinkedHashMap<Integer, String>();
        int sheetNum = book.getNumberOfSheets();
        for (int indexSheet = 1; indexSheet <= sheetNum; indexSheet++) {
            Sheet sheet = getSheetByNum(book, indexSheet);
            map.put(indexSheet, sheet.getSheetName());
        }
        return map;
    }

    /**
     * 获取workbook数据Map集合
     *
     * @param book
     * @return Map<Integer, List<List<String>>> @
     * throws Exception
     */
    public static Map<Integer, List<List<String>>> getWorkbookDatas(Workbook book) throws Exception {
        Map<Integer, List<List<String>>> bookdatas = new HashMap<Integer, List<List<String>>>();
        int sheetNum = book.getNumberOfSheets();
        for (int indexSheet = 1; indexSheet <= sheetNum; indexSheet++) {
            Sheet sheet = getSheetByNum(book, indexSheet);
            bookdatas.put(indexSheet, getSheetDataList(sheet));
        }
        return bookdatas;
    }

    /**
     * 获取sheet中的数据
     *
     * @param sheet
     * @return List<List<String>> @
     * throws Exception
     */
    public static List<List<String>> getSheetDataList(Sheet sheet) throws Exception {
        List<List<String>> sheetdatas = new ArrayList<List<String>>();
        //需要先合并单元格数据
        mergedRegion(sheet);
        int lastRowNum = getRowNum(sheet);
        int lastCellNum = getColumnNum(sheet);
        for (int i = 0; i < lastRowNum; i++) {
            Row row = sheet.getRow(i);
            sheetdatas.add(getRowDataList(sheet, row, lastCellNum));
        }
        return sheetdatas;
    }

    /**
     * 获取的数据对象是符合easyui格式的标准JSON对象数据集[{A:x,B:xx,C:xxx},{A:x,B:xx,C:xxx}]
     *
     * @param sheet
     * @return
     */
    public static List<Map<String, String>> getSheetDataMapHeadName(Sheet sheet, boolean includeHead, boolean includeNo) throws Exception {
        List<Map<String, String>> sheetdatas = new ArrayList<Map<String, String>>();
        //需要先合并单元格数据
        mergedRegion(sheet);
        int lastRowNum = getRowNum(sheet);
        Map<String, List<String>> clomnMap = new HashMap<String, List<String>>();
        Row row;
        Map<String, String> headMap = getRowDataMap(sheet, sheet.getRow(0), includeNo);
        for (int i = 0; i < lastRowNum; i++) {
            row = sheet.getRow(i);
            Map<String, String> map = getRowDataMap(sheet, headMap, row, includeNo);

            if (!map.isEmpty()) {
                sheetdatas.add(map);
            }
        }
        return sheetdatas;
    }


    /**
     * 读取一行的数据,返回的是数据集合List,[x,xx,xxx]
     *
     * @param row
     */
    public static List<String> getRowDataList(Sheet sheet, Row row, int lastCellNum) {
        List<String> rowdatas = new ArrayList<String>();
        if (row == null) {
            for (int i = 0; i < lastCellNum; i++) {
                rowdatas.add("");
            }
        } else {
            for (int i = 0; i < lastCellNum; i++) {
                rowdatas.add(getCellData(row.getCell(i)));
            }
        }
        return rowdatas;
    }

    /**
     * 获取一行的数据集合,体现的是Map<String , String>{A:x,B:xx,C:xxx}
     *
     * @param row
     * @return
     */
    public static Map<String, String> getRowDataMap(Sheet sheet, Row row, boolean includeNo) {
        Map<String, String> rowdatas = new LinkedHashMap<String, String>();
        String cellVaue;
        int columnNum = 0;
        int lastCellNum = getColumnNum(sheet);

        for (int j = 0; j < lastCellNum; j++) {
            if (!includeNo && j == 0) {
                continue;
            }
            cellVaue = getCellData(row.getCell(j));
            rowdatas.put(getCharByNum(columnNum) + "", cellVaue);
            columnNum = columnNum + 1;
        }
        return rowdatas;
    }

    /**
     * 获取一行的数据集合,体现的是Map<String , String>{A:x,B:xx,C:xxx}
     *
     * @param row
     * @return
     */
    public static Map<String, String> getRowDataMap(Sheet sheet, Map<String, String> headMap, Row row, boolean includeNo) {
        Map<String, String> rowdatas = new LinkedHashMap<String, String>();
        String cellVaue;
        int columnNum = 0;
        int lastCellNum = getColumnNum(sheet);

        for (int j = 0; j < lastCellNum; j++) {
            cellVaue = getCellData(row.getCell(j));
            rowdatas.put(getKey(headMap, columnNum) + "", cellVaue);
            columnNum = columnNum + 1;
        }
        return rowdatas;
    }

    /**
     * 返回指定sheet页的最大行数,例如:25,则表示下标从0...24
     *
     * @param sheet
     * @return
     */
    public static int getRowNum(Sheet sheet) {
        return sheet.getLastRowNum() + 1;
    }

    /**
     * 返回指定sheet页的最大列数,例如:25,则表示下标从0...24
     *
     * @param sheet
     * @return 列数
     */
    public static int getColumnNum(Sheet sheet) {
        int maxclNum = 0;
        int lastRowNum = getRowNum(sheet);
        for (int i = 0; i < lastRowNum; i++) {
            if (sheet.getRow(i) != null) {
                int tempNum = sheet.getRow(i).getLastCellNum();
                if (tempNum > maxclNum) {
                    maxclNum = tempNum;
                }
            }
        }
        return maxclNum;
    }

    /**
     * 获取指定sheet中指定rowNum和cellNum的值
     *
     * @param sheet
     * @param rowNum
     * @param cellNum
     * @return
     * @throws Exception
     */
    public static String getSheetCellValueWithRowIndexAndColIndex(Sheet sheet, int rowNum, int cellNum) throws Exception {
        Row row = sheet.getRow(rowNum);
        Cell cell = row.getCell(cellNum);
        return getCellData(cell);
    }

    /**
     * 获取指定Sheet中指定一列的数据.
     *
     * @param sheet 指定的Sheet
     * @param colIndex 指定的列
     * @return
     * @throws Exception
     */
    public static List<String> getColumnDataList(Sheet sheet, int colIndex) throws Exception {
        List<String> coldatas = new ArrayList<String>();
        int lastRowNum = getRowNum(sheet);
        for (int i = 0; i < lastRowNum; i++) {
            coldatas.add(getSheetCellValueWithRowIndexAndColIndex(sheet, i, colIndex));
        }
        return coldatas;
    }

    /**
     * 获取指定Sheet中指定一列的数据.
     *
     * @param sheet 指定的Sheet
     * @param key 指定的列
     * @return
     * @throws Exception
     */
    public static List<String> getColumnDataList(Sheet sheet, String key) throws Exception {
        List<String> coldatas = new ArrayList<String>();
        int lastRowNum = getRowNum(sheet);
        int lastColumNum = getColumnNum(sheet);
        List<String> lisColum = getRowDataList(sheet, sheet.getRow(0), lastColumNum);

        int colIndex = 0;
        if (lisColum != null) {
            for (String itemCode : lisColum){
                if (itemCode.equals(key)) {
                    for (int i = 0; i < lastRowNum; i++) {
                        coldatas.add(getSheetCellValueWithRowIndexAndColIndex(sheet, i, colIndex));
                    }
                    break;
                } else {
                    colIndex++;
                }
            }
        }

        return coldatas;
    }



    /**
     * 获取单元中值(字符串类型)
     *
     * @param cell
     * @return
     */
    public static String getCellData(Cell cell) {
        String cellValue = "";
        if (cell != null) {
            try {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_BLANK://空白
                        cellValue = "";
                        break;
                    case Cell.CELL_TYPE_NUMERIC: //数值型 0----日期类型也是数值型的一种
                        if (DateUtil.isCellDateFormatted(cell)) {
                            short format = cell.getCellStyle().getDataFormat();

                            if (yyyyMMddList.contains(format)) {
                                sFormat = new SimpleDateFormat("yyyy-MM-dd");
                            } else if (hhMMssList.contains(format)) {
                                sFormat = new SimpleDateFormat("HH:mm:ss");
                            }
                            Date date = cell.getDateCellValue();
                            cellValue = sFormat.format(date);
                        } else {
                            double d = cell.getNumericCellValue();
                            cellValue = Double.toString(d);
                            if(cellValue.indexOf("E")>0){
                                cellValue = new BigDecimal(d).toPlainString();
                            }
                        }
                        break;
                    case Cell.CELL_TYPE_STRING: //字符串型 1
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cellValue = replaceBlank(cell.getRichStringCellValue().getString());
                        break;
                    case Cell.CELL_TYPE_FORMULA: //公式型 2
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cellValue = replaceBlank(cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_BOOLEAN: //布尔型 4
                        cellValue = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case Cell.CELL_TYPE_ERROR: //错误 5
                        cellValue = "!#REF!";
                        break;
                }
            } catch (Exception e) {
                System.out.println("读取Excel单元格数据出错：" + e.getMessage());
                return cellValue;
            }
        }
        return cellValue;
    }

    public static String replaceBlank(String source) {
        String dest = "";
        if (source != null) {
            Pattern p = Pattern.compile("\\s*|\r|\n");
            Matcher m = p.matcher(source);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 给SHEET某一个单元格赋值
     *
     * @param sheet   指定单元格
     * @param rowNum  行号
     * @param cellNum 列号
     * @param value   值
     */
    public static void setCellValue(Sheet sheet, int rowNum, int cellNum, String value) {
        Row row = sheet.getRow(rowNum);
        Cell cell = row.getCell(cellNum);
        if (cell == null) {
            row.createCell(cellNum).setCellValue(value);
        } else {
            cell.setCellValue(value);
        }
    }

    public static void mergedRegion(Sheet sheet) throws Exception {
        //合并的单元格数量
        int merged = sheet.getNumMergedRegions();
        //预读合并的单元格
        for (int i = 0; i < merged; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int y0 = range.getFirstRow();
            int x0 = range.getFirstColumn();
            int y1 = range.getLastRow();
            int x1 = range.getLastColumn();

            String value = getSheetCellValueWithRowIndexAndColIndex(sheet, y0, x0);

            for (int m = y0; m <= y1; m++) {
                for (int n = x0; n <= x1; n++) {
                    setCellValue(sheet, m, n, value);
                }
            }
        }
    }

    /**
     * 生成表头名称,A,B,C,D...
     *
     * @param number
     * @return
     */
    public static String getCharByNum(int number) {
        int index = number / 26 - 1;
        if (index < 0) {
            return (char) (65 + number % 26) + "";
        } else if (index >= 0) {
            return (char) (65 + index) + "" + (char) (65 + number % 26) + "";
        }
        return "@";
    }

    /**
     * 生成表头名称 ,用第一列数据.
     *
     * @param number
     * @return
     */
    public static String getKey(Map<String, String> titleMap, int number) {
        int index = number / 26 - 1;
        String key = null;
        if (index < 0) {
            key = (char) (65 + number % 26) + "";
        } else if (index >= 0) {
            key = (char) (65 + index) + "" + (char) (65 + number % 26) + "";
        }

        return titleMap.get(key);
    }

    public static void main(String[] args) throws Exception {
        Workbook workbook = getExcelWorkbook("D:/test/aa.xlsx");
        Sheet sheet = getSheetByNum(workbook, 1);
//        System.out.println(JsonUtil.toJsonString(getSheetDataMapAndId(sheet)));
    }

    /**
     * 表头式样 边框
     *
     * @param wb
     * @return
     */
    public static CellStyle getRowCell(Workbook wb, boolean isHead) {
        CellStyle style = wb.createCellStyle();
//                style.setFillBackgroundColor(HSSFCellStyle.LEAST_DOTS);
//                style.setFillPattern(HSSFCellStyle.LEAST_DOTS);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor(HSSFColor.BLACK.index);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setTopBorderColor(HSSFColor.BLACK.index);
        style.setFillBackgroundColor(HSSFCellStyle.LEAST_DOTS);

        if (isHead){
            style.setFillForegroundColor((short) 13);// 设置背景色
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        }

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 11); // 字体高度
        font.setFontName(" 黑体 "); // 字体
        style.setFont(font);

        return style;
    }

    /**
     * 表头式样 边框
     *
     * @param wb
     * @return
     */
    public static CellStyle getRowCellFont(Workbook wb, boolean iserror) {
        CellStyle style = wb.createCellStyle();
//                style.setFillBackgroundColor(HSSFCellStyle.LEAST_DOTS);
//                style.setFillPattern(HSSFCellStyle.LEAST_DOTS);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 11); // 字体高度
        font.setFontName(" 黑体 "); // 字体
        if (iserror) {
            font.setColor(HSSFColor.RED.index);
        } else {
            font.setColor(HSSFColor.BLACK.index);
        }
        style.setFont(font);
        return style;
    }
}