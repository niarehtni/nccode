/**
 * 
 */
package nc.itf.hr.hi;

/**
 * @author Ares.Tank 
 * 劳建退投保类型
 */
public enum InsuranceTypeEnum {
	Labour("勞保", 0), LabourRetreat("勞退", 1), Health("健保", 2); 
	
    private String name;  
    private int index;


    private InsuranceTypeEnum(String name, int index) {  
        this.name = name;  
        this.index = index;  
    }
    
    public static String getName(int index) {  
        for (InsuranceTypeEnum c : InsuranceTypeEnum.values()) {  
            if (c.getIndex() == index) {  
                return c.name;  
            }  
        }  
        return null;  
    }  
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
    public int getIndex() {  
        return index;  
    }  
    public void setIndex(int index) {  
        this.index = index;  
    } 
}
