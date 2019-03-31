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
 * ��Ҫ�ṩ��Excel�ĸ��ִ���,���ص���ȡ����
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
     * ����·��,��ȡWorkBook����
     *
     * @param filePath �ļ�·��
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
     * ����������ins��ȡWorkBook����
     *
     * @param ins ������
     * @return workbook
     * @throws Exception
     */
    public static Workbook getWorkBookByStream(InputStream ins) throws Exception {
        return WorkbookFactory.create(ins);
    }

    /**
     * ����Workbook,sheetIndex��ȡsheet����
     *
     * @param book   WorkBook����
     * @param number sheetIndex ,��1��ʼ
     * @return sheet
     * @throws Exception
     */
    public static Sheet getSheetByNum(Workbook book, int number) throws Exception {
        return book.getSheetAt(number - 1);
    }

    /**
     * ���� Workbook���󷵻ظ�Workbook����������sheet��Map����.
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
     * ��ȡworkbook����Map����
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
     * ��ȡsheet�е�����
     *
     * @param sheet
     * @return List<List<String>> @
     * throws Exception
     */
    public static List<List<String>> getSheetDataList(Sheet sheet) throws Exception {
        List<List<String>> sheetdatas = new ArrayList<List<String>>();
        //��Ҫ�Ⱥϲ���Ԫ������
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
     * ��ȡ�����ݶ����Ƿ���easyui��ʽ�ı�׼JSON�������ݼ�[{A:x,B:xx,C:xxx},{A:x,B:xx,C:xxx}]
     *
     * @param sheet
     * @return
     */
    public static List<Map<String, String>> getSheetDataMapHeadName(Sheet sheet, boolean includeHead, boolean includeNo) throws Exception {
        List<Map<String, String>> sheetdatas = new ArrayList<Map<String, String>>();
        //��Ҫ�Ⱥϲ���Ԫ������
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
     * ��ȡһ�е�����,���ص������ݼ���List,[x,xx,xxx]
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
     * ��ȡһ�е����ݼ���,���ֵ���Map<String , String>{A:x,B:xx,C:xxx}
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
     * ��ȡһ�е����ݼ���,���ֵ���Map<String , String>{A:x,B:xx,C:xxx}
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
     * ����ָ��sheetҳ���������,����:25,���ʾ�±��0...24
     *
     * @param sheet
     * @return
     */
    public static int getRowNum(Sheet sheet) {
        return sheet.getLastRowNum() + 1;
    }

    /**
     * ����ָ��sheetҳ���������,����:25,���ʾ�±��0...24
     *
     * @param sheet
     * @return ����
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
     * ��ȡָ��sheet��ָ��rowNum��cellNum��ֵ
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
     * ��ȡָ��Sheet��ָ��һ�е�����.
     *
     * @param sheet ָ����Sheet
     * @param colIndex ָ������
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
     * ��ȡָ��Sheet��ָ��һ�е�����.
     *
     * @param sheet ָ����Sheet
     * @param key ָ������
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
     * ��ȡ��Ԫ��ֵ(�ַ�������)
     *
     * @param cell
     * @return
     */
    public static String getCellData(Cell cell) {
        String cellValue = "";
        if (cell != null) {
            try {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_BLANK://�հ�
                        cellValue = "";
                        break;
                    case Cell.CELL_TYPE_NUMERIC: //��ֵ�� 0----��������Ҳ����ֵ�͵�һ��
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
                    case Cell.CELL_TYPE_STRING: //�ַ����� 1
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cellValue = replaceBlank(cell.getRichStringCellValue().getString());
                        break;
                    case Cell.CELL_TYPE_FORMULA: //��ʽ�� 2
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cellValue = replaceBlank(cell.getStringCellValue());
                        break;
                    case Cell.CELL_TYPE_BOOLEAN: //������ 4
                        cellValue = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case Cell.CELL_TYPE_ERROR: //���� 5
                        cellValue = "!#REF!";
                        break;
                }
            } catch (Exception e) {
                System.out.println("��ȡExcel��Ԫ�����ݳ���" + e.getMessage());
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
     * ��SHEETĳһ����Ԫ��ֵ
     *
     * @param sheet   ָ����Ԫ��
     * @param rowNum  �к�
     * @param cellNum �к�
     * @param value   ֵ
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
        //�ϲ��ĵ�Ԫ������
        int merged = sheet.getNumMergedRegions();
        //Ԥ���ϲ��ĵ�Ԫ��
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
     * ���ɱ�ͷ����,A,B,C,D...
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
     * ���ɱ�ͷ���� ,�õ�һ������.
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
     * ��ͷʽ�� �߿�
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
            style.setFillForegroundColor((short) 13);// ���ñ���ɫ
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        }

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 11); // ����߶�
        font.setFontName(" ���� "); // ����
        style.setFont(font);

        return style;
    }

    /**
     * ��ͷʽ�� �߿�
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
        font.setFontHeightInPoints((short) 11); // ����߶�
        font.setFontName(" ���� "); // ����
        if (iserror) {
            font.setColor(HSSFColor.RED.index);
        } else {
            font.setColor(HSSFColor.BLACK.index);
        }
        style.setFont(font);
        return style;
    }
}