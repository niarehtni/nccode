package nc.ref.twhr.refmodel;
import nc.hr.utils.ResHelper;
import nc.vo.pub.lang.UFLiteralDate;
/**
 * 自定义档案参照。 创建日期：(2003-5-28 20:04:13)
 */
public class RangeLineRefModel extends nc.ui.bd.ref.AbstractRefModel
{
	//结束日期enddate
    private UFLiteralDate queryDate = null;
    //级距类型
	private int tableType = 0;
    
    /**
     * PeDefGridModel 构造子注解。
     */
    public RangeLineRefModel()
    {
        super();
    }
    
    /**
     * PeDefGridModel 构造子注解。
     */
    public RangeLineRefModel(int tableType, UFLiteralDate queryDate)
    {
        super();
        this.queryDate = queryDate;
        this.tableType = tableType;
//        setWhere();
    }
    
    /**
     * 返回默认的column的数量 创建日期：(2003-5-28 20:04:13)
     * @return int
     */
    @Override
    public int getDefaultFieldCount()
    {
        return 2;
    }
    
    /**
     * 显示字段列表 创建日期：(01-4-4 0:57:23)
     * @return java.lang.String
     */
    @Override
    public java.lang.String[] getFieldCode()
    {
//        return new String[]{"dd.doccode", "docname"};
    	return new String[]{"dd.rangeupper","dd.sh"};
    }
    
    /**
     * 显示字段中文名 创建日期：(01-4-4 0:57:23)
     * @return java.lang.String
     */
    @Override
    public java.lang.String[] getFieldName()
    {
//        return new String[]{ResHelper.getString("common", "UC000-0003279")/* @res "编码" */, ResHelper.getString("common", "UC000-0001155")/* @res "名称" */};
    	return new String[]{"最大金额","级距"};
    }
    
    /**
     * 设置隐藏字段。 创建日期：(2003-5-28 20:04:13)
     * @return java.lang.String[]
     */
    @Override
    public String[] getHiddenFieldCode()
    {
        return new String[]{"dd.id"};
    }
    
    /**
     * 主键字段名
     * @return java.lang.String
     */
    @Override
    public String getPkFieldCode()
    {
        return "dd.id";
    }
    
    /**
     * 参照标题 创建日期：(2003-5-28 20:04:13)
     * @return java.lang.String
     */
    @Override
    public String getRefTitle()
    {
        return getTitle();
    }
    
    /**
     * 参照数据库表或者视图名 创建日期：(01-4-4 0:57:23)
     * @return java.lang.String
     */
    @Override
    public String getTableName()
    {
        return " twhr_rangeline dd ";
    }
    
    /**
     * 设置参照标识。 创建日期：(2003-5-28 20:12:18)
     * @return java.lang.String
     */
    public java.lang.String getTitle()
    {
    	if(tableType == 1){
    		return "劳保级距"/* @res "劳保级距" */;
    	}else if(tableType == 2){
    		return "劳退级距"/* @res "劳保级距" */;
    	}else if(tableType == 3){
    		return "健保级距"/* @res "劳保级距" */;
    	}
        return "";
    }
    
    /**
     * 根据主键来设置参照Where条件。 创建日期：(2003-5-28 20:04:13)
     */
//    public void setWhere()
//    {
//        setWherePart("dd.dr=0 and ddl.dr=0 and pk_rangetable = (select pk_rangetable from twhr_rangetable where tableType = '"+tableType+"' and (enddate is null or enddate >= '"+queryDate+"') and startdate <= '"+queryDate+"')");
//    }
    
    public String getRefSql(){
    	String rangeclass = "";
    	if(tableType == 1){
    		rangeclass = "dd.rangeclass ";
    	}else if(tableType == 2 || tableType == 3){
    		rangeclass = "dd.rangegrade ";
    	}
    	String sql = " select distinct (case dd.rangeupper when 0.0 then dd.rangelower  else dd.rangeupper end)rangeupper,"+rangeclass+"sh,"+rangeclass+"id from twhr_rangeline dd where dd.dr=0 and isnull("+rangeclass+",-1) <> -1 and dd.pk_rangetable = (select pk_rangetable from twhr_rangetable where pk_org = 'GLOBLE00000000000000' and tableType = '"+tableType+"' and (enddate is null or enddate >= '"+queryDate+"') and startdate <= '"+queryDate+"') order by rangeupper ";
    	return sql;
    }
}
