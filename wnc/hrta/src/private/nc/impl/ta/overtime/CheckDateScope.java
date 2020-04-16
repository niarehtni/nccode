package nc.impl.ta.overtime;

import nc.itf.ta.algorithm.IDateScope;
import nc.pubitf.ta.overtime.ICheckDateScope;
import nc.vo.pub.lang.UFLiteralDate;

public class CheckDateScope implements ICheckDateScope {
	public CheckDateScope(UFLiteralDate begindate,UFLiteralDate enddate){
		this.begindate = begindate;
		this.enddate = enddate;
		this.scopeOneBeginDate = begindate;
		this.scopeOneEndDate = enddate;
		scopeNumber = 1;
		
	}
	/**
	 * 总的开始时间
	 */
	private UFLiteralDate begindate;
	/**
	 * 总的结束时间
	 */
	private UFLiteralDate enddate;
	
	/**
	 * 期数
	 */
	private int scopeNumber = 0;
	
	/**
	 * 第一期间
	 */
	private UFLiteralDate scopeOneBeginDate;

	private UFLiteralDate scopeOneEndDate;

	/**
	 * 第二期间
	 */
	private UFLiteralDate scopeTwoBeginDate;

	private UFLiteralDate scopeTwoEndDate;

	/**
	 * 第三期间
	 */
	private UFLiteralDate scopeTriBeginDate;

	private UFLiteralDate scopeTriEndDate;

	
	@Override
	public UFLiteralDate getBegindate() {
		return begindate;
	}
	@Override
	public void setBegindate(UFLiteralDate begindate) {
		this.begindate = begindate;
	}
	@Override
	public UFLiteralDate getEnddate() {
		return enddate;
	}
	@Override
	public void setEnddate(UFLiteralDate enddate) {
		this.enddate = enddate;
	}
	@Override
	public int getScopeNumber() {
		return scopeNumber;
	}
	@Override
	public void setScopeNumber(int scopeNumber) {
		this.scopeNumber = scopeNumber;
	}
	@Override
	public UFLiteralDate getScopeOneBeginDate() {
		return scopeOneBeginDate;
	}
	@Override
	public void setScopeOneBeginDate(UFLiteralDate scopeOneBeginDate) {
		this.scopeOneBeginDate = scopeOneBeginDate;
	}
	@Override
	public UFLiteralDate getScopeOneEndDate() {
		return scopeOneEndDate;
	}
	@Override
	public void setScopeOneEndDate(UFLiteralDate scopeOneEndDate) {
		this.scopeOneEndDate = scopeOneEndDate;
	}
	@Override
	public UFLiteralDate getScopeTwoBeginDate() {
		return scopeTwoBeginDate;
	}
	@Override
	public void setScopeTwoBeginDate(UFLiteralDate scopeTwoBeginDate) {
		this.scopeTwoBeginDate = scopeTwoBeginDate;
	}
	@Override
	public UFLiteralDate getScopeTwoEndDate() {
		return scopeTwoEndDate;
	}
	@Override
	public void setScopeTwoEndDate(UFLiteralDate scopeTwoEndDate) {
		this.scopeTwoEndDate = scopeTwoEndDate;
	}
	@Override
	public UFLiteralDate getScopeTriBeginDate() {
		return scopeTriBeginDate;
	}
	@Override
	public void setScopeTriBeginDate(UFLiteralDate scopeTriBeginDate) {
		this.scopeTriBeginDate = scopeTriBeginDate;
	}
	@Override
	public UFLiteralDate getScopeTriEndDate() {
		return scopeTriEndDate;
	}
	@Override
	public void setScopeTriEndDate(UFLiteralDate scopeTriEndDate) {
		this.scopeTriEndDate = scopeTriEndDate;
	}
	

	
	
	
	
	
	

}
