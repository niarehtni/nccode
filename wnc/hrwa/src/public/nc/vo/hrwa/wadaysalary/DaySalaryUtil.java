package nc.vo.hrwa.wadaysalary;

import org.apache.commons.lang.builder.HashCodeBuilder;
/**
 * ���ǲ�������,�벻Ҫ�Ѵ����з�����ɾ�̬����
 * @author tank 2019��10��16��12:01:49
 *
 */
public class DaySalaryUtil {

	/**
	 * ���ݷ�����Ϣts��Ա��������ts ����hashcode
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
	 * ����code
	 * @param builder
	 * @return
	 */
	private int generateHash(HashCodeBuilder builder){
		return builder.toHashCode();
	}
}
