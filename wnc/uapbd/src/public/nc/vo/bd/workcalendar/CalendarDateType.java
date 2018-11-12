
package nc.vo.bd.workcalendar;

import nc.vo.ml.NCLangRes4VoTransl;

/**
 * <code>CalendarDateType<code>
 * <strong>ÈÕÆÚÀàÐÍ</strong>
 * <p>ËµÃ÷£º
 * <li></li>
 * </p>
 * 
 * @since NC6.1
 * @version 2012-3-27 ÏÂÎç12:07:09
 * @author jinjya
 * 
 * 
 * @modify Ares.Tank
 * @version 2018-8-25 15:14:50 
 * @detail Ôö¼ÓÁË¸÷¸ö¼ÙÆÚÀàÐÍµÄÃû³Æ
 */
public enum CalendarDateType {
  WORKINGDAY("2workcal-000001",0), //¹¤×÷ÈÕ
  WEEKENDDAY("2workcal-000008",1), //¹«ÐÝÈÕ
  HOLIDAY("2workcal-000004",2), //½Ú¼ÙÈÕ
  OFFICALHOLIDAY("2workcal-000020",3), //Àý¼ÙÈÕ
  RESTDAY("2workcal-000021",4);//ÐÝÏ¢ÈÕ
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
