package nc.impl.hr.tools.rtf;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.bd.pubinfo.IAddressService_C;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IAttribute;
import nc.md.model.IBean;
import nc.md.model.MetaDataException;
import nc.md.model.type.IType;
import nc.md.model.type.impl.EnumType;
import nc.ui.hr.tools.rtf.jacob.DHyperlink;
import nc.ui.hr.tools.rtf.jacob.RtfHelper;
import nc.ui.hr.tools.rtf.view.WordPrintEnv;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.vo.bd.address.AddressFormatVO;
import nc.vo.hi.pub.FldreftypeVO;
import nc.vo.hr.global.DateFormulaParse;
import nc.vo.hr.tools.rtf.CommonValue;
import nc.vo.hr.tools.rtf.ConditionsOfType;
import nc.vo.hr.tools.rtf.QuerySQLElement;
import nc.vo.hr.tools.rtf.RefDataTypeHelper;
import nc.vo.hr.tools.rtf.RefSetFieldParameter;
import nc.vo.ml.MultiLangContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;

/**
 * 构建rtf ContextMap内容
 * @author sunxj
 * @version 2009-12-29
 */

public class ContextMapCreator
{
    protected IBean bean;
    protected int cardtype;
    protected Map comboDataTypeMap;
    // 检索条件map
    protected Map conditionMap;
    protected List<Map> dataFilterList = new ArrayList<Map>();
    protected Map dataFilterMap = new HashMap();
    // 数据map
    protected Map dataMap = new HashMap();
    protected Map dataTypeMap;
    protected HashMap<String, String[]> fieldsMap = new HashMap<String, String[]>();
    
    private List<String> filterExpressList = new ArrayList<String>();
    
    private HashMap<Map, String> filterExpressMap = new HashMap<Map, String>();
    
    protected List<IBean> listBean;
    
    // 人员主键
    protected String pk_psndoc;
    protected String primarykey;
    protected HashMap<String, String> primarykeyMap;
    protected HashMap<String, Object> rowNoMap = new HashMap<String, Object>();
    
    protected RtfTemplateProcessDAO rtfTemplateProcessDAO = createRtfTemplateProcessDAO();
    protected HashMap<String, Object> showNoMap = new HashMap<String, Object>();
    
    /**
     * 得到系统变量值
     * @author fengwei on 2010-4-9
     * @param express
     * @return
     */
    public static String getSystemValue(String express)
    {
        if (express.startsWith("system"))
        {
            express = express.trim().substring("system.".length());
        }
        
        Object o = WordPrintEnv.getEnv(express.trim());
        
        return o == null ? "" : o.toString();
    }
    
    /**
     * 判断是否是系统变量
     * @author fengwei on 2010-4-9
     * @param express
     * @return
     */
    public static boolean isSystemValue(String express)
    {
        return WordPrintEnv.isSysVar(express.trim());
    }
    
    /**
     * 把日份翻译成中文
     * @return
     */
    public static String transDateToChinese(int date, boolean isUpper)
    {
        if (date < 1 || date > 31)
        {
            return null;
        }
        
        return isUpper ? CommonValue.CHINESE_NUMS_UPPER[date] : CommonValue.CHINESE_NUMS_LOWWER[date];
    }
    
    /**
     * 把月份翻译成中文
     * @return
     */
    public static String transMonthToChinese(int month, boolean isUpper)
    {
        if (month < 1 || month > 12)
        {
            return null;
        }
        
        return isUpper ? CommonValue.CHINESE_NUMS_UPPER[month] : CommonValue.CHINESE_NUMS_LOWWER[month];
    }
    
    /**
     * 把阿拉伯数字翻译成中文
     * @param num
     * @param isUpper
     * @return
     */
    public static String transNumToChinese(int num, boolean isUpper)
    {
        if (num < 0 || num > 9)
        {
            return null;
        }
        
        return isUpper ? CommonValue.CHINESE_NUMS_UPPER[num] : CommonValue.CHINESE_NUMS_LOWWER[num];
    }
    
    /**
     * 把年份翻译成中文
     * @return
     */
    public static String transYearToChinese(int year, boolean isUpper)
    {
        if (year < 1000 || year > 9999)
        {
            return null;
        }
        
        StringBuffer buf = new StringBuffer();
        
        buf.append(transNumToChinese(year / 1000, isUpper));
        buf.append(transNumToChinese(year / 100 % 10, isUpper));
        buf.append(transNumToChinese(year / 10 % 10, isUpper));
        buf.append(transNumToChinese(year % 10, isUpper));
        
        return buf.toString();
    }
    
    /**
     * 向sql语句中加入要查询的字段
     * @author fengwei on 2010-4-9
     * @param sqlElem
     * @param fieldMap
     * @return
     */
    private Map addFieldToSQLElement(QuerySQLElement sqlElem, Map fieldMap)
    {
        Map fieldContrastMap = new HashMap();
        Set keySet = fieldMap.keySet();
        String[] fields = (String[]) keySet.toArray(new String[0]);
        fieldsMap.put(sqlElem.getTable(), fields);
        
        for (int i = 0; i < fields.length; i++)
        {
            String field = fields[i];
            if (field.indexOf('[') > 0)
            {
                field = field.substring(0, field.indexOf('['));
            }
            String columnName = convertField(sqlElem, field, i);
            fieldContrastMap.put(field, columnName);
        }
        String table = sqlElem.getTable();
        
        // String mainTable = bean.getName();
        String mainTable = bean.getTable().getName();
        List<String> tableName = RefDataTypeHelper.getNoRecodnumColSubBean(listBean);
        
        // 是否有recordnum列
        boolean hasRecordNum = false;
        List<IAttribute> attList = bean.getAttributes();
        for (IAttribute att : attList)
        {
            if (att.getID().equals("ID_ATTR_DR") || att.getID().equals("ID_ATTR_TS") || att.getID().equals("ID_ATTR_VOSTATUS")
                || att.isCalculation())
            {
                continue;
            }
            if (att.getColumn().getName().toLowerCase().equals("recordnum"))
            {
                hasRecordNum = true;
                break;
            }
        }
        
        if (table != null && !table.equalsIgnoreCase(mainTable) && !tableName.contains(table))
        {
            // table有recordnum列的情况下，才执行下面的语句,否则后面查询时会报sql错
            if (hasRecordNum)
            {
                // String columnName = convertField(sqlElem, "recordnum",
                // fields.length);
                fieldContrastMap.put("recordnum", "recordnum");
            }
        }
        
        return fieldContrastMap;
    }
    
    /**
     * 向sql语句中加入order by 子句
     * @author fengwei on 2010-4-9
     * @param sqlElem
     */
    private void addOrderByToSQLElement(QuerySQLElement sqlElem)
    {
        String orderBy = "";
        try
        {
            orderBy = rtfTemplateProcessDAO.getOrderBySQLElement(sqlElem, primarykeyMap);
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
        }
        
        sqlElem.setOrderBy(orderBy);
    }
    
    private Object convertDateFormat(Object value, int dateFormatId)
    {
        
        UFDate date = null;
        try
        {
            date = new UFDate((String) value);
        }
        catch (Exception e)
        {
            return value;
        }
        // 生成BUFF
        StringBuffer buf = new StringBuffer();
        switch (dateFormatId)
        {
            case 0 :// "1977-04-23",
                String strDate = value.toString();
                if (strDate.length() > 10)
                {
                    strDate = strDate.substring(0, 10);
                }
                buf.append(strDate);
                break;
            case 1 :// 1977.4.23,
                buf.append(date.getYear());
                buf.append(".");
                buf.append(date.getMonth());
                buf.append(".");
                buf.append(date.getDay());
                break;
            case 2 :// "77.4.23",
                buf.append(date.getYear() % 100 <= 9 ? "0" + date.getYear() % 100 : date.getYear() % 100);
                buf.append(".");
                buf.append(date.getMonth());
                buf.append(".");
                buf.append(date.getDay());
                break;
            case 3 :// "1977.4",
                buf.append(date.getYear());
                buf.append(".");
                buf.append(date.getMonth());
                break;
            case 4 :// "1977.04",
                buf.append(date.getYear());
                buf.append(".");
                buf.append(date.getMonth() < 10 ? "0" + date.getMonth() : date.getMonth());
                break;
            case 5 :// "77.4",
                buf.append(date.getYear() % 100);
                buf.append(".");
                buf.append(date.getMonth());
                break;
            case 6 :// "77.04",
                buf.append(date.getYear() % 100);
                buf.append(".");
                buf.append(date.getMonth() < 10 ? "0" + date.getMonth() : date.getMonth());
                break;
            case 7 :// 一九七七年四月二十三日,
                buf.append(transYearToChinese(date.getYear(), false));
                buf.append(ResHelper.getString("common", "UC000-0001787")
                /* @res "年" */);
                buf.append(transMonthToChinese(date.getMonth(), false));
                buf.append(ResHelper.getString("common", "UC000-0002494")
                /* @res "月" */);
                buf.append(transDateToChinese(date.getDay(), false));
                buf.append(ResHelper.getString("common", "UC000-0002308")
                /* @res "日" */);
                break;
            case 8 :// 一九七七年四月,
                buf.append(transYearToChinese(date.getYear(), false));
                buf.append(ResHelper.getString("common", "UC000-0001787")
                /* @res "年" */);
                buf.append(transMonthToChinese(date.getMonth(), false));
                buf.append(ResHelper.getString("common", "UC000-0002494")
                /* @res "月" */);
                break;
            case 9 :// 1977年4月23日,
                buf.append(date.getYear());
                buf.append(ResHelper.getString("common", "UC000-0001787")
                /* @res "年" */);
                buf.append(date.getMonth());
                buf.append(ResHelper.getString("common", "UC000-0002494")
                /* @res "月" */);
                buf.append(date.getDay());
                buf.append(ResHelper.getString("common", "UC000-0002308")
                /* @res "日" */);
                break;
            case 10 :// 1977年4月,
                buf.append(date.getYear());
                buf.append(ResHelper.getString("common", "UC000-0001787")
                /* @res "年" */);
                buf.append(date.getMonth());
                buf.append(ResHelper.getString("common", "UC000-0002494")
                /* @res "月" */);
                break;
            case 11 :// 1977年04月23日,
                buf.append(date.getYear());
                buf.append(ResHelper.getString("common", "UC000-0001787")
                /* @res "年" */);
                buf.append(date.getMonth() < 10 ? "0" + date.getMonth() : date.getMonth());
                buf.append(ResHelper.getString("common", "UC000-0002494")
                /* @res "月" */);
                buf.append(date.getDay() < 10 ? "0" + date.getDay() : date.getDay());
                buf.append(ResHelper.getString("common", "UC000-0002308")
                /* @res "日" */);
                break;
            case 12 :// 1977年04月,
                buf.append(date.getYear());
                buf.append(ResHelper.getString("common", "UC000-0001787")
                /* @res "年" */);
                buf.append(date.getMonth() < 10 ? "0" + date.getMonth() : date.getMonth());
                buf.append(ResHelper.getString("common", "UC000-0002494")
                /* @res "月" */);
                break;
            case 13 :// 77年4月23日,
                buf.append(date.getYear() % 100);
                buf.append(ResHelper.getString("common", "UC000-0001787")
                /* @res "年" */);
                buf.append(date.getMonth());
                buf.append(ResHelper.getString("common", "UC000-0002494")
                /* @res "月" */);
                buf.append(date.getDay());
                buf.append(ResHelper.getString("common", "UC000-0002308")
                /* @res "日" */);
                break;
            case 14 :// 77年4月,
                buf.append(date.getYear() % 100);
                buf.append(ResHelper.getString("common", "UC000-0001787")
                /* @res "年" */);
                buf.append(date.getMonth());
                buf.append(ResHelper.getString("common", "UC000-0002494")
                /* @res "月" */);
                break;
            case 15 :// 年限,
                buf.append(PubEnv.getServerTime().getYear() - date.getYear());
                break;
            case 16 :// 年份,
                buf.append(date.getYear());
                buf.append(ResHelper.getString("common", "UC000-0001787")
                /* @res "年" */);
                break;
            case 17 :// 月份,
                buf.append(date.getMonth());
                buf.append(ResHelper.getString("common", "UC000-0002494")
                /* @res "月" */);
                break;
            case 18 :// 日份,
                buf.append(date.getDay());
                buf.append(ResHelper.getString("common", "UC000-0002308")
                /* @res "日" */);
                break;
        }
        
        return buf.toString();
    }
    
    /**
     * 转换数据格式
     * @author fengwei on 2010-4-9
     * @param sqlElem
     * @param field
     * @param i
     * @return
     */
    private String convertField(QuerySQLElement sqlElem, String field, int i)
    {
        
        String table = sqlElem.getTable();
        if ("hi_psndoc_parttime".equals(table))
        {
            table = "hi_psnjob";
        }
        
        String fieldName;
        String asDisplayFieldName;
        
        String key = table.toLowerCase() + "." + field.toLowerCase();
        if (dataTypeMap.containsKey(key))
        {
            FldreftypeVO vo = (FldreftypeVO) dataTypeMap.get(key);
            String pk_fldreftype = vo.getPk_fldreftype();
            RefSetFieldParameter para =
                RefDataTypeHelper.getRefSetFieldName(new String[]{pk_fldreftype}).values().toArray(new RefSetFieldParameter[0])[0];
            
            String tableName = para.getTableName();
            String pkFieldName = para.getPkFieldName();
            String displayFieldName = para.getDisplayFieldName();
            
            String asTableName = "a" + i;
            asDisplayFieldName = field.toLowerCase()/*
                                                     * asTableName + displayFieldName
                                                     */;
            if (!para.isMutilangText())
            {
                fieldName = asTableName + "." + displayFieldName + " " + asDisplayFieldName;
            }
            else
            {
                int currentLangSeq = MultiLangContext.getInstance().getCurrentLangSeq() - 1;
                String[] nameFields = new String[]{displayFieldName, displayFieldName + "2", displayFieldName + "3"};
                fieldName =
                    "isnull(" + asTableName + "." + nameFields[currentLangSeq] + "," + asTableName + "." + displayFieldName + ") "
                        + asDisplayFieldName;
                
            }
            sqlElem.getFields().add(fieldName);
            String joinTable = "";
            if ("om_jobrank".equals(tableName) && "om_levelrelation".equals(table))
            {
                if ("defaultrank".equals(field))
                {
                    joinTable =
                        " left join " + tableName + " " + asTableName + " on om_levelrelation.defaultrank =" + asTableName + "."
                            + pkFieldName;
                }
                else
                {
                    joinTable =
                        " left join " + tableName + " " + asTableName + " on om_rankrelation.pk_jobrank =" + asTableName + "."
                            + pkFieldName;
                }
            }
            else
            {
                joinTable =
                    " left join " + tableName + " " + asTableName + " on " + table + "." + field + "=" + asTableName + "." + pkFieldName;
            }
            sqlElem.getJoinTable().add(joinTable);
            
            return asDisplayFieldName;
        }
        
        asDisplayFieldName = field;
        fieldName = table + "." + field;
        if (field.startsWith("UFAGE["))
        {
            try
            {
                fieldName = DateFormulaParse.proDateFormula(field, table);
            }
            catch (Exception e)
            {
                Logger.error(e.getMessage());
            }
        }
        sqlElem.getFields().add(fieldName);
        return asDisplayFieldName;
    }
    
    protected RtfTemplateProcessDAO createRtfTemplateProcessDAO()
    {
        return new RtfTemplateProcessDAO();
    }
    
    private Object dealExpressValues(String itemExpressOld, Object value)
    {
        /** 处理年月 */
        if ("".equals(itemExpressOld) || itemExpressOld == null)
        {
            return new String[0];
        }
        String itemExpress = itemExpressOld;
        String[] filterExpress = null;
        
        if (itemExpressOld.startsWith("Filter") || itemExpressOld.contains("Filter"))
        {
            filterExpress = getFilterExpress(itemExpress);
            // String strFunction = filterExpress[3];
            itemExpress = filterExpress[1];
            // return value;
            
        }
        else
        {
            filterExpress = getStrExpress(itemExpress);
            
        }
        // ////////////////////////////////////
        
        String strBefor = filterExpress[0];
        String strAfter = filterExpress[4];
        String strFunction = filterExpress[3];
        // String strRowNo = filterExpress[2];
        itemExpress = filterExpress[1];
        
        if (!"".equals(strFunction))
        {
            int dateFormatId = Integer.parseInt(strFunction.substring(strFunction.indexOf("+") + 1));
            if (value != null)
            {
                value = convertDateFormat(value, dateFormatId);
            }
        }
        
        if (strBefor.length() > 0 || strAfter.length() > 0)
        {
            if (value != null)
            {
                value = strBefor + value + strAfter;
            }
        }
        
        if (isSystemValue(itemExpress))
        {
            value = getSystemValue(itemExpress);
        }
        return value;
    }
    
    /**
     * 处理照片
     * @author fengwei on 2010-8-12
     * @param object
     * @return
     */
    private File dealPhoto(Object object)
    {
        ImageIcon icon = null;
        icon = new ImageIcon((byte[]) object);
        Image pImage = icon.getImage();
        BufferedImage bufferedImage = new BufferedImage(150, 200, BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(pImage, 0, 0, 150, 200, null);
        icon.setImage(bufferedImage);
        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        try
        {
            // 把这个jpg图像写到这个流中去,这里可以转变图片的编码格式
            ImageIO.write(bufferedImage, "png", imageStream);
        }
        catch (IOException e)
        {
            Logger.error(e.getMessage(), e);
        }
        byte[] tagInfo = imageStream.toByteArray();
        
        // 将处理后的照片放到指定路径中
        String fileName = RtfHelper.strDocTempletDirPath + "photo_" + System.currentTimeMillis() + ".png";
        File file = new File(fileName);
        try
        {
            FileUtils.writeByteArrayToFile(file, tagInfo);
        }
        catch (IOException e)
        {
            Logger.error(e.getMessage(), e);
        }
        return file;
    }
    
    /**
     * 处理子集取数结果
     * @author fengwei on 2010-4-8
     * @param sqlElem
     * @param result
     * @return
     */
    @SuppressWarnings("unchecked")
    private List dealRowNoResult(String table, List results,Map valueMap)
    {
        List obj = new ArrayList();
        int size = results.size();// 界面数据数
        //现在所有的子集全部是根据先recordnum再pk进行正序排序，不设置任何条件时界面展示是没问题的，但是最近、最初等其实是反过来的，所以得先换个顺序
        List result = new ArrayList();
        Map map = new HashMap();
        String[] fields = (String[]) valueMap.keySet().toArray(new String[0]);
        for(int i = results.size() - 1; i >= 0; i--)
        {
            result.add(results.get(i));
        }
        
        if (rowNoMap.get(table) != null)
        {
			String rowno = (String) rowNoMap.get(table);// 最近、最近第、最初、最初第几行
			// wanglq 为了适应一张卡片能设置多条子集条件（多条最近第）
			String[] rownoArr = rowno.split(",");
			if(table.contains("new")){
				table = table.substring(0, table.indexOf("new"));
            }
			if(table.contains("old")){
				table = table.substring(0, table.indexOf("old"));
            }
			if (fieldsMap.get(table) != null) {
				if (rowno.indexOf(':') < 0)// 最近第、最初第
				{
					for (String row : rownoArr) {
						int no = Integer.parseInt(row);
						if (no > 0 && no < size)// 界面数据数>最近第几行
						{
							obj.add(result.get(size - no));
							// return obj;
						} else if (no > 0 && no > size)// 界面数据数<最近第几行
						{
							// obj.add(result.get(size - no));
							for (String str : fields) {
								map.put(str, null);
							}
							obj.add(map);
							// return obj;
						} else if (no > 0 && no == size)// 界面数据数=最近第几行
						{
							obj.add(result.get(0));
							// return obj;
						} else if (no < 0 && Math.abs(no) <= size)// 界面数据数>=最初第几行
						{
							obj.add(result.get(Math.abs(no) - 1));
							// return obj;
						} else if (no < 0 && Math.abs(no) > size)// 界面数据数<最初第几行
						{
							for (String str : fields) {
								map.put(str, null);
							}
							obj.add(map);
							// return obj;
						}
					}
					return obj;
				}

				else
				// 最近、最初
				{
					int no = Integer.parseInt(rowno.substring(rowno
							.indexOf(':') + 1));
					if (no > 0 && no < size)// 界面数据数>最近几行
					{
						List zjobj = new ArrayList();
						for (int i = size - 1; i >= size - no; i--) {
							zjobj.add(result.get(i));
						}

						for (int i = zjobj.size() - 1; i >= 0; i--) {
							obj.add(zjobj.get(i));
						}
						return obj;
					} else if (no > 0 && no == size)// 界面数据数=最近几行
					{
						List zjobj = new ArrayList();
						for (int i = size - 1; i >= no - size; i--) {
							zjobj.add(result.get(i));
						}
						for (int i = zjobj.size() - 1; i >= 0; i--) {
							obj.add(zjobj.get(i));
						}
						return obj;
					} else if (no > 0 && no > size)// 界面数据数<最近几行
					{
						for (int i = size - 1; i >= 0; i--) {
							obj.add(result.get(i));
						}
						return obj;
					} else if (no < 0 && Math.abs(no) <= size)// 界面数据数>=最初几行
					{
						for (int i = 0; i <= Math.abs(no) - 1; i++) {
							obj.add(result.get(i));
						}
						return obj;
					} else if (no < 0 && Math.abs(no) > size)// 界面数据数<最初几行
					{
						for (int i = 0; i <= size - 1; i++) {
							obj.add(result.get(i));
						}
						return obj;
					}
				}
			}
			// wanglqh 子集显示数目大于查询数目时，空表行数会在前面显示
			return result;
		}else{
        	return results;
        }
    }
    
    /**
     * 处理子集显示行数
     * @author fengwei on 2011-8-11
     * @param result
     * @param table
     * @return
     */
    private List dealShowNoResult(List result, String table)
    {
        List obj = new ArrayList();
        // Set keyset = showNoMap.keySet();
        // Iterator it = keyset.iterator();
        int size = result.size();
        if (showNoMap.get(table) != null)
        {
            // String table = (String) it.next();
            String rowno = (String) showNoMap.get(table);
            int showno = Integer.parseInt(rowno);
            if (showno >= size)
            {
                for (int i = 0; i < size; i++)
                {
                    // int j = size - i - 1;
                    obj.add(result.get(i));
                }
                for (int i = size; i < showno; i++)
                {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    String[] fields = fieldsMap.get(table);
                    for (String field : fields)
                    {
                        map.put(field, null);
                    }
                    obj.add(map);
                }
                return obj;
            }
            else if (showno < size)
            {
                for (int i = 0; i < showno; i++)
                {
                    // int j = showno - i - 1;
                    obj.add(result.get(i));
                }
                return obj;
            }
        }
        
        return result;
    }
    
    /**
     * 处理Word文档所含链接涉及到的表、字段、过滤条件
     * @author fengwei on 2010-4-9
     * @param itemExpresses
     */
    private void dealWithExpresses(String[] itemExpresses)
    {
        for (int i = 0; i < itemExpresses.length; i++)
        {
            String itemExpress = itemExpresses[i];
            String[] tableFieldCondition = getTableFieldCondition(itemExpress);
            String[] expresses = getStrExpress(itemExpress);
            UFBoolean flag = UFBoolean.TRUE;
            if (!"".equals(expresses[2]))
            {
            	//wanglqh，允许一张表中设置多条自己条件（比如多个最近第几条等）
				if (rowNoMap.containsKey(tableFieldCondition[0])
						&& !(rowNoMap.get(tableFieldCondition[0]).toString()
								.contains(expresses[2]))) {
					expresses[2] = rowNoMap.get(tableFieldCondition[0]) + ","
							+ expresses[2];
					rowNoMap.put(tableFieldCondition[0], expresses[2]);
					flag = UFBoolean.FALSE;
				}
				if(rowNoMap.containsKey(tableFieldCondition[0])
						&& (rowNoMap.get(tableFieldCondition[0]).toString()
								.contains(expresses[2])))flag = UFBoolean.FALSE;
				if (flag.booleanValue())
					rowNoMap.put(tableFieldCondition[0], expresses[2]);
            }
            else if (!"".equals(expresses[5]))
            {
                showNoMap.put(tableFieldCondition[0], expresses[5]);
            }
            if (tableFieldCondition != null)
            {
                if (tableFieldCondition.length == 2)
                {
                    putDataToMap(tableFieldCondition, dataMap);
                }
                else
                {
                    putDataToMap(tableFieldCondition, dataFilterMap);
                    Map map = new HashMap();
                    putDataToMap(tableFieldCondition, map);
                    dataFilterList.add(map);
                    filterExpressList.add(expresses[1]);
                    filterExpressMap.put(map, expresses[1]);
                }
                
            }
        }
    }
    
    /**
     * 处理查询结果
     * @author fengwei on 2010-4-9
     * @param result
     * @param table
     * @param fieldMap
     * @param fieldContrastMap
     * @return
     */
    private Object dealWithResult(List result, String table, Map fieldMap, Map fieldContrastMap)
    {
        Object obj = null;
        String[] fields = (String[]) fieldMap.keySet().toArray(new String[0]);
        if(table.contains("new")){
			table = table.substring(0, table.indexOf("new"));
        }
		if(table.contains("old")){
			table = table.substring(0, table.indexOf("old"));
        }
        for (int i = 0; i < fields.length; i++)
        {
            String key = table + "." + fields[i];
            if (comboDataTypeMap.containsKey(key))
            {
                FldreftypeVO vo = (FldreftypeVO) comboDataTypeMap.get(key);
                IType dataType = vo.getDataType();
                for (int j = 0; j < result.size(); j++)
                {
                    
                    Map rowMap = (Map) result.get(j);
                    IConstEnum constEnum = rowMap.get(fields[i]) == null ? null : ((EnumType) dataType).getConstEnum(rowMap.get(fields[i]));
                    if (constEnum != null)
                    {
                        
                        String value = ((EnumType) dataType).getConstEnum(rowMap.get(fields[i])).getName();
                        rowMap.put(fields[i], value);
                    }
                }
            }
        }
        int len = result.size();
        // Set keyset = comboDataTypeMap.keySet();
        // Iterator it = keyset.iterator();
        if (table.equalsIgnoreCase(bean.getTable().getName())/* len == 1 */)
        {
            Map rowMap = (Map) result.get(0);
            HashMap<String, Object> vars = new HashMap<String, Object>();
            for (String field : fields)
            {
                Object value = rowMap.get(field);
                if ((field.equals("photo") || field.equals("previewphoto")) && value != null)
                {
                    InputStream in = null;
                    File file = null;
                    try
                    {
                        file = dealPhoto(value);
                        in = new FileInputStream(file);
                        vars.put(field, in);
                        // FileUtils.forceDelete(file);
                        continue;
                    }
                    catch (FileNotFoundException e)
                    {
                        Logger.error(e.getMessage(), e);
                    }
                    finally
                    {
                        // IOUtils.closeQuietly(in);
                    }
                }
                else if ("addr".equals(field))
                {
                    try
                    {
                        String[] pks = new String[1];
                        pks[0] = (String) value;
                        String address = getAddress(pks);
                        // String address = rtfTemplateProcessDAO.dealAddress((String) value);
                        vars.put(field, address);
                        continue;
                    }
                    catch (BusinessException e)
                    {
                        Logger.error(e.getMessage(), e);
                    }
                }
                vars.put(field, value);
            }
            obj = vars;
        }
        else
        /* if(len > 1) */{
            List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < len; i++)
            {
                Map rowMap = (Map) result.get(i);
                HashMap<String, Object> vars = new HashMap<String, Object>();
                for (String field : fields)
                {
                    Object value = rowMap.get(field);
                    vars.put(field, value);
                }
                list.add(vars);
            }
            obj = list;
        }
        return obj;
    }
    
    public String getAddress(String[] pks) throws BusinessException
    {
        String address = null;
        AddressFormatVO[] formatVOs = NCLocator.getInstance().lookup(IAddressService_C.class).format(pks);
        if (!ArrayUtils.isEmpty(formatVOs))
        {
            for (int i = 0; i < formatVOs.length; i++)
            {
                if (formatVOs[i].getVo() != null)
                {
                    String country = formatVOs[i].getCountry() == null ? "" : formatVOs[i].getCountry().toString();
                    String state = formatVOs[i].getState() == null ? "" : formatVOs[i].getState().toString();
                    String city = formatVOs[i].getCity() == null ? "" : formatVOs[i].getCity();
                    String section = formatVOs[i].getSection() == null ? "" : formatVOs[i].getSection().toString();
                    String road = formatVOs[i].getRoad() == null ? "" : formatVOs[i].getRoad().toString();
                    address = country + " " + state + " " + city + " " + section + " " + road;
                }
            }
        }
        return address;
    }
    
    private Object executeFilterQueryData(QuerySQLElement sqlElem, Map fieldMap, String field)
    {
        Map fieldContrastMap = addFieldToSQLElement(sqlElem, fieldMap);
        Object result = queryFilterValueBySqlElement(sqlElem, fieldMap, fieldContrastMap, field);
        return result;
    }
    
    /**
     * 根据表名、字段名，查询数据
     * @author fengwei on 2010-4-9
     * @param sqlElem
     * @param fieldMap
     * @return
     */
    private Object executeQueryData(QuerySQLElement sqlElem, Map fieldMap ,String tablename)
    {
        Map fieldContrastMap = addFieldToSQLElement(sqlElem, fieldMap);
        addOrderByToSQLElement(sqlElem);
        Object result = queryValuesBySQLElement(sqlElem, fieldMap, fieldContrastMap,tablename);
        return result;
    }
    
    /**
     * hyperLinkList {'A'bd_psndoc.regulardata[0:-4](1977-08-28)'B'} {Filter:27aff0125fd00bad7}
     */
    public Map getContextMap(String pk_psndoc, DHyperlink[] hyperLinks, Map conditionMap, Map dataTypeMap)
    {
        return getContextMap(pk_psndoc, hyperLinks, conditionMap, dataTypeMap, cardtype, null);
        
    }
    
    public Map getContextMap(String pk_psndoc, DHyperlink[] hyperLinks, Map conditionMap, Map dataTypeMap, int cardtype,
            Map comboDataTypeMap)
    {
        
        Map beanMap = new HashMap();
        if (comboDataTypeMap != null)
        {
            beanMap.put(6, comboDataTypeMap);
        }
        
        String[] itemExpresses = new String[hyperLinks.length];
        for (int i = 0; i < hyperLinks.length; i++)
        {
            itemExpresses[i] = hyperLinks[i].strRefTxt;
        }
        return getContextMapWithBeanInfo(pk_psndoc, itemExpresses, conditionMap, dataTypeMap, cardtype, beanMap);
        
    }
    
    /**
     * @author fengwei on 2010-4-9
     * @param itemExpresses
     * @param contextMap
     * @return
     */
    public Map getContextMap(String[] itemExpresses, Map contextMap)
    {
    	HashSet<String> itemExpressesFilter = new HashSet<String>();
        CollectionUtils.addAll(itemExpressesFilter, itemExpresses);
        
        for (String item : itemExpressesFilter)
        {
            String[] fields = getTableFieldCondition1(item);
            Object obj = contextMap.get(fields[0]);
            if (item.contains("Filter"))
            {
                obj = contextMap.get("Filter");
            }
            
            if (obj != null && obj instanceof HashMap)
            {
                HashMap value = (HashMap) obj;
                Object var = null;
                if (fields.length == 2)
                {
                    var = dealExpressValues(item, value.get(fields[1]));
                    ((HashMap) obj).put(fields[1], var);
                }
                else if (fields.length == 3)
                {
                    var = dealExpressValues(item, value.get(fields[0] + "_" + fields[2]));
                    ((HashMap) obj).put(fields[0] + "_" + fields[2], var);
                }
            }
            else if (obj != null && obj instanceof ArrayList)
            {
                ArrayList value = (ArrayList) obj;
                for (int i = 0; i < value.size(); i++)
                {
                    HashMap map = (HashMap) value.get(i);
                    Object var = null;
                    if (fields.length == 2)
                    {
                        var = dealExpressValues(item, map.get(fields[1]));
                        map.put(fields[1], var);
                    }
                    else if (fields.length == 3)
                    {
                        var = dealExpressValues(item, map.get(fields[2]));
                        map.put(fields[2], var);
                    }
                    value.set(i, map);
                }
                
                obj = value;
            }
            if (item.contains("Filter"))
            {
                contextMap.put("Filter", obj);
            }
            else
            {
                contextMap.put(fields[0], obj);
            }
        }
        
        return contextMap;
    }
    
    public Map getContextMapWithBeanInfo(String pk_psndoc, DHyperlink[] hyperLinks, Map conditionMap, Map dataTypeMap, Map beanMap)
    {
        
        String[] itemExpresses = new String[hyperLinks.length];
        for (int i = 0; i < hyperLinks.length; i++)
        {
            itemExpresses[i] = hyperLinks[i].strRefTxt;
        }
        
        return getContextMapWithBeanInfo(pk_psndoc, itemExpresses, conditionMap, dataTypeMap, beanMap);
        
    }
    
    public Map getContextMapWithBeanInfo(String pk_psndoc, String[] hyperLinks, Map conditionMap, Map dataTypeMap, int cardtype, Map beanMap)
    {
        this.dataTypeMap = (Map) dataTypeMap.get(5);
        this.pk_psndoc = pk_psndoc;
        this.conditionMap = conditionMap;
        this.cardtype = cardtype;
        this.bean = (IBean) beanMap.get("mainTable");
        this.listBean = (List<IBean>) beanMap.get("subTable");
        this.comboDataTypeMap = (Map) dataTypeMap.get(6);
        this.primarykey = bean.getPrimaryKey().getPKColumn().getName();
        this.primarykeyMap = (HashMap<String, String>) beanMap.get("primarykeyMap");
        
        // String[] itemExpresses = new String[hyperLinks.length];
        // for (int i = 0; i < hyperLinks.length; i++) {
        // itemExpresses[i] = hyperLinks[i].strRefTxt;
        // }
        
        Map contextMap = initDataMap(hyperLinks);
        
        contextMap = getContextMap(hyperLinks, contextMap);
        
        return contextMap;
    }
    
    public Map getContextMapWithBeanInfo(String pk_psndoc, String[] hyperLinks, Map conditionMap, Map dataTypeMap, Map beanMap)
    {
        return getContextMapWithBeanInfo(pk_psndoc, hyperLinks, conditionMap, dataTypeMap, cardtype, beanMap);
    }
    
    /**
     * 解析条件过滤表达式
     * @author fengwei on 2010-4-9
     * @param itemExpress
     * @return String[] { strBefor, strExpress, strRowNo, strYMD, strAfter }{前缀, 过滤条件, 子集序号，日期格式，后缀}
     */
    private String[] getFilterExpress(String itemExpress)
    {
        String strBefor = "";
        String strAfter = "";
        String strExpress = "";
        String strYMD = "";
        String strRowNo = "";
        String strShowNo = "";
        // 取得前缀内容
        Pattern findPattern = Pattern.compile("^'(.*)'\\+");
        Matcher matcher = findPattern.matcher(itemExpress);
        if (matcher.find())
        {
            strBefor = matcher.group(1);
        }
        strExpress = itemExpress.replaceAll("^'.*'\\+", "");
        // 日期格式
        int beginLoc = strExpress.indexOf("(");
        int endLoc = strExpress.indexOf(")");
        if (beginLoc > 0)
        {
            strYMD = strExpress.substring(beginLoc + 1, endLoc);
            strExpress = strExpress.substring(0, beginLoc) + strExpress.substring(endLoc + 1);
        }
        // 行号
        beginLoc = strExpress.indexOf("[");
        endLoc = strExpress.indexOf("]");
        if (beginLoc > 0)
        {
            strRowNo = strExpress.substring(beginLoc + 1, endLoc);
        }
        // 取得后缀内容
        int nBeginLoc = strExpress.indexOf("+'");
        int nEndLoc = strExpress.indexOf("-");
        if (nBeginLoc > 0)
        {
            if (nEndLoc > 0)
            {
                strAfter = strExpress.substring(nBeginLoc + 2, nEndLoc - 1);
            }
            else
            {
                
                strAfter = strExpress.substring(nBeginLoc + 2, strExpress.length() - 1);
            }
        }
        
        // 取得子集显示行数
        int index = strExpress.indexOf("-'");
        if (index > 0)
        {
            strShowNo = strExpress.substring(index + 2, strExpress.length() - 1);
        }
        // 表达式
        findPattern = Pattern.compile("^Filter.[\\w]*");
        matcher = findPattern.matcher(strExpress);
        if (matcher.find())
        {
            strExpress = matcher.group();
        }
        return new String[]{strBefor, strExpress, strRowNo, strYMD, strAfter, strShowNo};
    }
    
    /**
     * 根据表达式得到{表名, 条件, 字段名}数组
     * @author fengwei on 2010-4-9
     * @param itemExpress
     * @return String[]{setName, where, fieldName}{表名, 条件, 字段名}
     */
    private String[] getFilterTableField(String itemExpress)
    {
        String keyFilter = itemExpress.substring("Filter.".length());
        ConditionsOfType condition = (ConditionsOfType) conditionMap.get(keyFilter);
        
        String setName = condition.getTableCode();
        String fieldName = condition.getFieldCode();
        int index = fieldName.indexOf(".");
        if (index > 0)
        {
            fieldName = fieldName.substring(index + 1);
        }
        String where = condition.getWhereSql();
        if (where == null)
        {
            where = "";
        }
        return new String[]{setName, where, fieldName};
    }
    
    public IBean getMainTable(int cardtype) throws BusinessException
    {
        IBean bean = null;
        String metaDataId = "";
        if (cardtype == CommonValue.HI_RPT_CARD)
        {
            metaDataId = "218971f0-e5dc-408b-9a32-56529dddd4db";
        }
        
        try
        {
            bean = MDBaseQueryFacade.getInstance().getBeanByID(metaDataId);
        }
        catch (MetaDataException e)
        {
            Logger.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage(), e);
        }
        
        if (bean == null)
        {
            throw new BusinessException(/*
                                         * "没有找到id为"+IMetaDataIDConst.JOB+"的bean!"
                                         */);
        }
        return bean;
    }
    
    /**
     * 解析表达式
     * @author fengwei on 2010-4-9
     * @param itemExpress
     * @return String[] { strBefor, strExpress, strRowNo, strYMD, strAfter }{前缀, 表达式, 子集序号，日期格式，后缀}
     */
    private String[] getStrExpress(String itemExpress)
    {
        String str = "";
        String strBefor = "";
        String strAfter = "";
        String strExpress = "";
        String strYMD = "";
        String strRowNo = "";
        String strShowNo = "";
        // 取得前缀内容
        Pattern findPattern = Pattern.compile("^'(.*)'\\+");
        Matcher matcher = findPattern.matcher(itemExpress);
        if (matcher.find())
        {
            strBefor = matcher.group(1);
        }
        str = itemExpress.replaceAll("^'.*'\\+", "");
        // 日期格式
        int beginLoc = str.indexOf("(");
        int endLoc = str.indexOf(")");
        if (beginLoc > 0)
        {
            strYMD = str.substring(beginLoc + 1, endLoc);
            str = str.substring(0, beginLoc) + str.substring(endLoc + 1);
        }
        // 行号
        beginLoc = str.indexOf("[");
        endLoc = str.indexOf("]");
        if (beginLoc > 0)
        {
            strRowNo = str.substring(beginLoc + 1, endLoc);
        }
        // 表达式
        findPattern = Pattern.compile("^[\\w]*\\.[\\w]*|[\\w]*");
        matcher = findPattern.matcher(str);
        if (matcher.find())
        {
            strExpress = matcher.group();
        }
        // 取得后缀内容
        int nBeginLoc = str.indexOf("+'");
        int nEndLoc = str.indexOf("-");
        if (nBeginLoc > 0)
        {
            if (nEndLoc > 0)
            {
                strAfter = str.substring(nBeginLoc + 2, nEndLoc - 1);
            }
            else
            {
                
                strAfter = str.substring(nBeginLoc + 2, str.length() - 1);
            }
            // strAfter = str.substring(nBeginLoc + 2, nEndLoc - 1);
        }
        // 取得子集显示行数
        int index = str.indexOf("-'");
        if (index > 0)
        {
            strShowNo = str.substring(index + 2, str.length() - 1);
        }
        return new String[]{strBefor, strExpress, strRowNo, strYMD, strAfter, strShowNo};
    }
    
    /**
     * @author fengwei on 2010-4-9
     * @param itemExpress
     * @return String[]{setName, fieldName}{表名, 字段名}
     */
    private String[] getTableField(String itemExpress)
    {
        
        int index = itemExpress.lastIndexOf(".");
        String setName = itemExpress.substring(0, index);
        String fieldName = itemExpress.substring(index + 1);
        return new String[]{setName, fieldName};
    }
    
    /**
     * 这个函数是应湖南湘佳牧业股份有限公司NCdp205935706问题要求新增的~~
     * 根据表达式得到表条件
     * @author fengwei on 2010-4-9
     * @param itemExpress
     * @return
     */
    private String[] getTableFieldCondition1(String itemExpress)
    {
        if ("".equals(itemExpress) || itemExpress == null)
        {
            return null;
        }
        if (itemExpress.startsWith("Filter."))
        {
            itemExpress = getFilterExpress(itemExpress)[1];
            ////应湖南湘佳牧业股份有限公司NCdp205935706问题要求，额外处理一下附加了条件的日期格式，此处原本就有BUG？
            String[] returnFields = getFilterTableField(itemExpress);
            String field2 = itemExpress.substring(itemExpress.indexOf(".") + 1);
            returnFields[0] = "Filter";
            returnFields[2] = field2;
            return returnFields;
            //return getFilterTableField(itemExpress);
        }
        // {'A'+bd_psndoc.regulardata[0:-4](1977-08-28)+'B'}
        itemExpress = getStrExpress(itemExpress)[1];
        if (ContextMapCreator.isSystemValue(itemExpress))
        {
            return getTableField(itemExpress);
        }
        return getTableField(itemExpress);
    }
    
    /**
     * 根据表达式得到表条件
     * @author fengwei on 2010-4-9
     * @param itemExpress
     * @return
     */
    private String[] getTableFieldCondition(String itemExpress)
    {
        if ("".equals(itemExpress) || itemExpress == null)
        {
            return null;
        }
        if (itemExpress.startsWith("Filter.") || itemExpress.contains("Filter."))
        {
            itemExpress = getFilterExpress(itemExpress)[1];
            return getFilterTableField(itemExpress);
        }
        // {'A'+bd_psndoc.regulardata[0:-4](1977-08-28)+'B'}
        itemExpress = getStrExpress(itemExpress)[1];
        if (isSystemValue(itemExpress))
        {
            return null;
        }
        return getTableField(itemExpress);
    }
    
    /**
     * @author fengwei on 2010-4-9
     * @param contextMap
     */
    // private void initDataFilterMapData(HashMap<String, Object> contextMap) {
    // Set keySet = dataFilterMap.keySet();
    // Iterator it = keySet.iterator();
    // while (it.hasNext()) {
    // String table = (String) it.next();
    // Map whereMap = (Map) dataFilterMap.get(table);
    // HashMap<String, Map> map = new HashMap<String, Map>();
    // map.put(table, whereMap);
    //
    // Set keySet1 = whereMap.keySet();
    // Iterator it1 = keySet1.iterator();
    // while (it1.hasNext()) {
    // String where = (String) it1.next();
    // Map fieldMap = (Map) whereMap.get(where);
    //
    // QuerySQLElement sqlElem = new QuerySQLElement();
    // sqlElem.setTable(table);
    // if (where.length() != 0) {
    // sqlElem.setWherePart(" and (" + where + ")");
    // }
    //
    // String itemExpress = filterExpressMap.get(map);
    // // int index = itemExpress.indexOf('.');
    // String field = itemExpress.substring(7, itemExpress.length());
    // Object result = executeFilterQueryData(sqlElem, fieldMap, field);
    // // Object result = executeQueryData(sqlElem, fieldMap);
    // contextMap.put("Filter", result);
    // }
    // }
    // }
    private void initDataFilterData(HashMap<String, Object> contextMap)
    {
        if (dataFilterList.size() == 0)
        {
            return;
        }
        
        HashMap<String, Object> valuemap = new HashMap<String, Object>();
        
        for (Map map : dataFilterList)
        {
            Set keySet = map.keySet();
            Iterator it = keySet.iterator();
            while (it.hasNext())
            {
                String table = (String) it.next();
                Map whereMap = (Map) map.get(table);
                
                Set keySet1 = whereMap.keySet();
                Iterator it1 = keySet1.iterator();
                while (it1.hasNext())
                {
                    String where = (String) it1.next();
                    Map fieldMap = (Map) whereMap.get(where);
                    
                    QuerySQLElement sqlElem = new QuerySQLElement();
                    sqlElem.setTable(table);
                    if (where.length() != 0)
                    {
                        sqlElem.setWherePart(" and (" + where + ")");
                    }
                    
                    String itemExpress = filterExpressMap.get(map);
                    String field = itemExpress.substring(7, itemExpress.length());
                    Object result = executeFilterQueryData(sqlElem, fieldMap, field);
                    valuemap.put(field, result);
                }
            }
        }
        contextMap.put("Filter", valuemap);
    }
    
    /**
     * @author fengwei on 2010-4-9
     * @param itemExpresses
     * @return
     */
    public HashMap<String, Object> initDataMap(String[] itemExpresses)
    {
        HashMap<String, Object> contextMap = new HashMap<String, Object>();
        
        // 保存Word文档所含链接涉及到的表、字段、过滤条件
        dealWithExpresses(itemExpresses);
        
        // 初始化dataMap数据
        initDataMapData(contextMap);
        
        // 初始化dataFilterMap数据
        initDataFilterData(contextMap);
        
        return contextMap;
    }
    
    /**
     * @author fengwei on 2010-4-9
     * @param contextMap
     */
    private void initDataMapData(HashMap<String, Object> contextMap)
    {
        Set keySet = dataMap.keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext())
        {
            String table = (String) it.next();
            String tableChange = "";
            if(table.contains("new")){
            	tableChange = table.substring(0, table.indexOf("new"));
            }else if(table.contains("old")){
            	tableChange = table.substring(0, table.indexOf("old"));
            }else{
            	tableChange = table;
            }
            Map fieldMap = (Map) dataMap.get(table);
            Object result = null;
            if (table.equalsIgnoreCase("system"))
            {
                HashMap<String, String> map = new HashMap<String, String>();
                Set systemKeySet = fieldMap.keySet();
                Iterator systemIt = systemKeySet.iterator();
                while (systemIt.hasNext())
                {
                    String key = (String) systemIt.next();
                    String value = getSystemValue(key);
                    map.put(key, value);
                }
                result = map;
            }
            else
            {
                QuerySQLElement sqlElem = new QuerySQLElement();
                sqlElem.setTable(tableChange);
               // sqlElem.setTable(table);
                
                result = executeQueryData(sqlElem, fieldMap,table);
            }
            
            contextMap.put(table, result);
        }
    }
    
    /**
     * 如果字符串能转换成日期返回true。
     * @return boolean
     * @param strDateTime java.lang.String
     */
    public boolean isAllowDate(String strDateTime)
    {
        if (strDateTime == null || strDateTime.trim().length() == 0)
        {
            return true;
        }
        if (strDateTime.trim().length() > 9)
        {
            return isAllowDate0(strDateTime.substring(0, 10));
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 如果字符串能转换成日期返回true。
     * @return boolean
     * @param strDate java.lang.String
     */
    public boolean isAllowDate0(String strDate)
    {
        if (strDate == null || strDate.trim().length() == 0)
        {
            return true;
        }
        if (strDate.trim().length() != 10)
        {
            return false;
        }
        for (int i = 0; i < 10; i++)
        {
            char c = strDate.trim().charAt(i);
            if (i == 4 || i == 7)
            {
                if (c != '-')
                {
                    return false;
                }
            }
            else if (c < '0' || c > '9')
            {
                return false;
            }
        }
        int year = Integer.parseInt(strDate.trim().substring(0, 4));
        int month = Integer.parseInt(strDate.trim().substring(5, 7));
        if (month < 1 || month > 12)
        {
            return false;
        }
        int day = Integer.parseInt(strDate.trim().substring(8, 10));
        int MONTH_LENGTH[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int LEAP_MONTH_LENGTH[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int daymax = UFDateTime.isLeapYear(year) ? LEAP_MONTH_LENGTH[month - 1] : MONTH_LENGTH[month - 1];
        if (day < 1 || day > daymax)
        {
            return false;
        }
        return true;
    }
    
    private void putDataToMap(String[] express, Map map)
    {
        int len = express.length - 1;
        String key = express[0];
        if (len > 0)
        {
            Map valueMap = (Map) map.get(key);
            if (valueMap == null)
            {
                valueMap = new HashMap();
                map.put(key, valueMap);
            }
            String[] temp = new String[len];
            System.arraycopy(express, 1, temp, 0, len);
            putDataToMap(temp, valueMap);
        }
        else
        {
            map.put(key, null);
        }
    }
    
    private Object queryFilterValueBySqlElement(QuerySQLElement sqlElem, Map valueMap, Map fieldContrastMap, String field)
    {
        List result = new ArrayList();
        try
        {
            // result = rtfTemplateProcessDAO.queryValuesBySQLElement(sqlElem,
            // pk_psndoc);
            result = rtfTemplateProcessDAO.queryValuesBySQLElement(sqlElem, pk_psndoc, primarykey);
            // result = dealShowNoResult(result, sqlElem.getTable());
            if (result == null || result.size() == 0)
            {
                return null;
            }
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
        }
        // 处理子集上的枚举值
        result = (List) dealWithResult(result, sqlElem.getTable(), valueMap, fieldContrastMap);
        
        String[] fields = (String[]) valueMap.keySet().toArray(new String[0]);
        Map obj = (Map) result.get(0);
        Object value = obj.get(fields[0]);
        
        // HashMap<String, Object> vars = new HashMap<String, Object>();
        // vars.put(field, value);
        // Object contextMap = vars;
        return value;
    }
    
    /**
     * 根据sql得到查询结果
     * @author fengwei on 2010-4-9
     * @param sqlElem
     * @param valueMap
     * @param fieldContrastMap
     * @return
     */
    private Object queryValuesBySQLElement(QuerySQLElement sqlElem, Map valueMap, Map fieldContrastMap,String tablename)
    {
        List result = new ArrayList();
        try
        {
            // result = rtfTemplateProcessDAO.queryValuesBySQLElement(sqlElem,
            // pk_psndoc);
            result = rtfTemplateProcessDAO.queryValuesBySQLElement(sqlElem, pk_psndoc, primarykey);
            
           // result = dealShowNoResult(result, sqlElem.getTable());// 处理子集显示行数--固定行
            result = dealShowNoResult(result, tablename);// 处理子集显示行数--固定行
            if (result == null || result.size() == 0)
            {
                return null;
            }
           // result = dealRowNoResult(sqlElem.getTable(), result);// 处理子集取数结果--最近第、最近、最初第、最初
            result = dealRowNoResult(tablename, result,valueMap);// 处理子集取数结果--最近第、最近、最初第、最初
        }
        catch (BusinessException e)
        {
            Logger.error(e.getMessage(), e);
        }
        // 处理查询结果--照片、地址等
       // Object contextMap = dealWithResult(result, sqlElem.getTable(), valueMap, fieldContrastMap);
        Object contextMap = dealWithResult(result, tablename, valueMap, fieldContrastMap);
        return contextMap;
    }
}
