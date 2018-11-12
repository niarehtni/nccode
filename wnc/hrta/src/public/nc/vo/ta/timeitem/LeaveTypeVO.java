package nc.vo.ta.timeitem;

import nc.vo.ta.basedoc.annotation.CopyVOClassName;
import nc.vo.ta.annotation.IDColumn;
import nc.vo.ta.annotation.Table;

@CopyVOClassName(className="nc.vo.ta.timeitem.LeaveTypeCopyVO")
@Table(tableName="tbm_timeitem")
@IDColumn(idColumn="pk_timeitem")
@SuppressWarnings("serial")
public class LeaveTypeVO extends TimeItemVO {
	
	/**
	 * 新增字段是否扣款/补发
	 * 新增字段扣款/补发费率
	 * @author chenklb@yonyou.com
	 * @date 2018.5.3
	 */
	private nc.vo.pub.lang.UFBoolean ischarge;
	private java.lang.Integer rate;
	
	public static final String ISCHARGE = "ischarge";
	public static final String RATE = "rate";

	//“年假”的pk_timeitem
	public static final String PK_ANNUALVACATION="1002Z710000000021ZLJ";

	public nc.vo.pub.lang.UFBoolean getIscharge() {
		return ischarge;
	}

	public void setIscharge(nc.vo.pub.lang.UFBoolean ischarge) {
		this.ischarge = ischarge;
	}

	public java.lang.Integer getRate() {
		return rate;
	}

	public void setRate(java.lang.Integer rate) {
		this.rate = rate;
	}

}
