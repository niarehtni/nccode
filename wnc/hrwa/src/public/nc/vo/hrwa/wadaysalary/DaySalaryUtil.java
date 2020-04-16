package nc.vo.hrwa.wadaysalary;

import org.apache.commons.lang.builder.HashCodeBuilder;
/**
 * 考虑并发问题,请不要把此类中方法变成静态方法
 * @author tank 2019年10月16日12:01:49
 *
 */
public class DaySalaryUtil {

	/**
	 * 根据分组信息ts和员工定调资ts 生成hashcode
	 * @param ts_item_group
	 * @param ts_wadoc
	 * @return
	 */
	public int getHashCode(String ts_item_group,String ts_wadoc){
		HashCodeBuilder builder  = new HashCodeBuilder();
		builder.append(ts_item_group);
		builder.append(ts_wadoc);
		return generateHash(builder);
	}
	
	
	/**
	 * 生成code
	 * @param builder
	 * @return
	 */
	private int generateHash(HashCodeBuilder builder){
		return builder.toHashCode();
	}
}
