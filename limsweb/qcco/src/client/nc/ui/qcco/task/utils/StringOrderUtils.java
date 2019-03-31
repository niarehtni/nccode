package nc.ui.qcco.task.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * tab标签工具类
 */
public class StringOrderUtils {
    //二维表行数
    private static int ROW_NUM = 26;
    //二维表列数
    private static int COL_NUM = 301;

    private static boolean[][] orderTable = new boolean[ROW_NUM][COL_NUM];

    /**
     * In : array{A1,A2,A4,B1,C1}
     * Out : String "A1-A2,A4,B1,C1"
     *
     * @author Tank
     * @date 2019年3月15日10:01:40
     */
    public static String outOrderString(String[] arrays) throws Exception {
        putArrayInTable(arrays);
        return outTableString();
    }

    /**
     * In : String "A1-A2,A4,B1,C1"
     * Out : array{A1,A2,A4,B1,C1}
     *
     * @author Tank
     * @date 2019年3月15日10:01:40
     */
    public static String[] outDisOrderArray(String arrays) throws Exception {
        putStringInTable(arrays);
        return outTableArrays();
    }

    /**
     * 将二维表输出成String
     */
    private static String[] outTableArrays() {
        Set<String> resultSet = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        if (orderTable != null) {
            for(int i = 0 ; i < ROW_NUM ; i ++){
                for (int j = 0 ; j <COL_NUM;j++){
                    if(orderTable[i][j]){
                        sb.append((char)(i+65)).append(j);
                        resultSet.add(sb.toString());
                        sb.delete(0,sb.length());
                    }
                }
            }
        }
        return resultSet.toArray(new String[0]);
    }

    /**
     * 将String展开到二维表中
     * TODO 如果需要跨行生成那需要传入每行的长度
     */
    private static void putStringInTable(String arrays) throws Exception {
        orderTable = new boolean[ROW_NUM][COL_NUM];
        if (arrays != null && arrays.length() > 0) {
            String[] tabArrays = arrays.replaceAll(" ", "").split(",");
            for (String tabString : tabArrays) {
                if (tabString.indexOf('-') == -1) {
                    //单个存入
                    try {
                        int row = tabString.charAt(0);
                        int col = Integer.parseInt(tabString.substring(1, tabString.length()));
                        orderTable[row - 65][col] = true;
                    } catch (Exception e) {
                        throw new Exception("非法字符:" + tabString);
                    }
                } else {
                    //线性存入
                    String[] splitTabs = tabString.split("-");
                    if (splitTabs.length != 2) {
                        throw new Exception("非法字符:" + tabString);
                    } else {
                        try {
                            int startRow = splitTabs[0].charAt(0);
                            int startCol = Integer.parseInt(splitTabs[0].substring(1, splitTabs[0].length()));
                            int endRow = splitTabs[1].charAt(0);
                            int endCol = Integer.parseInt(splitTabs[1].substring(1, splitTabs[1].length()));
                            if(startRow != endRow){
                                throw new Exception("不能进行跨行:" + tabString);
                            }
                            //顺序相反先交换
                            if (startRow > endRow || (startRow == endRow && startCol > endCol)) {
                                int temp = endRow;
                                endRow = startRow;
                                startRow = temp;

                                temp = startCol;
                                startCol = endCol;
                                endCol = temp;
                            }
                            //开始存入
                            for (int i = (startRow - 65); (i < 26 && i <= endRow - 65); i++) {
                                for (int j = startCol; (j < 300 && (i != endRow-65 || j <= endCol)); j++) {
                                    orderTable[i][j] = true;
                                }
                            }
                        } catch (Exception e) {
                            throw new Exception("非法字符:" + tabString);
                        }
                    }
                }
            }
        }
    }

    /**
     * 将二维表输出成String
     */
    private static String outTableString() {
        if (orderTable == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ROW_NUM; i++) {
            int lineLength = 0;
            for (int j = 0; j < COL_NUM; j++) {
                if (orderTable[i][j]) {
                    if (0 == lineLength) {
                        sb.append((char) (i + 65)).append(j);
                        lineLength++;
                    } else if (1 == lineLength) {
                        sb.append("-");
                        if ((COL_NUM - 1) == j) {
                            sb.append((char) (i + 65)).append(j).append(",");
                        }
                        lineLength++;
                    } else if (1 < lineLength) {
                        if ((COL_NUM - 1) == j) {
                            sb.append((char) (i + 65)).append(j).append(",");
                        }
                        lineLength++;
                    }
                } else {
                    if(lineLength == 0){
                        ;
                    }else if(lineLength == 1){
                        lineLength = 0;
                        sb.append(",");
                    }else{
                        lineLength = 0;
                        //lineLength断了,接上一个
                        sb.append((char) (i + 65)).append(j - 1).append(",");
                    }

                }
            }
        }
        return sb.deleteCharAt(sb.length()-1).toString();
    }

    /**
     * 将tab数组存进表中
     */
    private static void putArrayInTable(String[] arrays) throws Exception {
        orderTable = new boolean[ROW_NUM][COL_NUM];
        if (arrays != null && arrays.length > 0) {
            //数组展开成二维表
            for (String tab : arrays) {
                if (null != tab && tab.replaceAll(" ", "").length() >= 2) {
                    tab = tab.replaceAll(" ", "");
                    try {
                        int row = tab.charAt(0);
                        int col = Integer.parseInt(tab.substring(1, tab.length()));
                        orderTable[row - 65][col] = true;
                    } catch (Exception e) {
                        throw new Exception("字符非法:" + tab);
                    }
                }
            }
        }
    }

}
