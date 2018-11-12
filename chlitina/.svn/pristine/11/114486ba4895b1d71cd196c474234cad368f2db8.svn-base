
package nc.vo.bd.workcalendar;

import nc.vo.ml.NCLangRes4VoTransl;

/**
 * <code>CalendarDateType<code>
 * <strong>日期类型</strong>
 * <p>说明：
 * <li></li>
 * </p>
 * 
 * @since NC6.1
 * @version 2012-3-27 下午12:07:09
 * @author jinjya
 * 
 * 
 * @modify Ares.Tank
 * @version 2018-8-25 15:14:50 
 * @detail 增加了各个假期类型的名称
 */
public enum CalendarDateType {
  WORKINGDAY("2workcal-000001",0), //工作日
  WEEKENDDAY("2workcal-000008",1), //公休日
  HOLIDAY("2workcal-000004",2), //节假日
  OFFICALHOLIDAY("2workcal-000020",3), //例假日
  RESTDAY("2workcal-000021",4);//休息日
    private String name;  
    private int index;  

    private CalendarDateType(String name, int index) { 
    	name = NCLangRes4VoTransl.getNCLangRes().getStrByID("10140wcb", name);
        this.name = name;  
        this.index = index;  
    }  

    public static String getName(int index) {  
        for (CalendarDateType c : CalendarDateType.values()) {  
            if (c.getIndex() == index) {  
                return c.name;  
            }  
        }  
        return null;  
    } 
    
    public String getName() {  
        return name;  
    }  
 
    public int getIndex() {  
        return index;  
    }  

}
