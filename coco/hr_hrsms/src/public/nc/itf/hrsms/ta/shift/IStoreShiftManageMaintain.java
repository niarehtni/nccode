package nc.itf.hrsms.ta.shift;

import nc.vo.bd.shift.AggShiftVO;
import nc.vo.pub.BusinessException;

public interface IStoreShiftManageMaintain {
	
	public AggShiftVO insert(AggShiftVO vo)throws BusinessException;
	
	public AggShiftVO update(AggShiftVO vo)throws BusinessException;
	
	public void delete(AggShiftVO vo)throws BusinessException;
	 
	/**
	 * Õ£”√
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggShiftVO disable(AggShiftVO vo)throws BusinessException;
	
	/**
	 * ∆Ù”√
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggShiftVO enable(AggShiftVO vo)throws BusinessException;
	
}
