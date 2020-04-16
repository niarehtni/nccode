package nc.pubitf.ta.overtime;

import nc.itf.ta.algorithm.IDateScope;
import nc.vo.pub.lang.UFLiteralDate;
/**
 * 用于加班上限校验的时间范围
 * @author Tank
 *
 */
public interface ICheckDateScope extends IDateScope {
	
	public void setEnddate(UFLiteralDate enddate);

	public int getScopeNumber() ;

	public void setScopeNumber(int scopeNumber) ;

	public UFLiteralDate getScopeOneBeginDate();

	public void setScopeOneBeginDate(UFLiteralDate scopeOneBeginDate);

	public UFLiteralDate getScopeOneEndDate();

	public void setScopeOneEndDate(UFLiteralDate scopeOneEndDate);

	public UFLiteralDate getScopeTwoBeginDate();

	public void setScopeTwoBeginDate(UFLiteralDate scopeTwoBeginDate);

	public UFLiteralDate getScopeTwoEndDate();

	public void setScopeTwoEndDate(UFLiteralDate scopeTwoEndDate);

	public UFLiteralDate getScopeTriBeginDate();

	public void setScopeTriBeginDate(UFLiteralDate scopeTriBeginDate);

	public UFLiteralDate getScopeTriEndDate();

	public void setScopeTriEndDate(UFLiteralDate scopeTriEndDate);
}
