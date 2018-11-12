package nc.itf.hrwa;

import java.util.List;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.wa.allocate.AggAllocateOutVO;
import nc.vo.wa.allocate.AllocateCsvVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.pub.BusinessException;

public interface IAllocateMaintain {

	public void delete(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills) throws BusinessException;

	public AggAllocateOutVO[] insert(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException;

	public AggAllocateOutVO[] update(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException;

	public AggAllocateOutVO[] query(IQueryScheme queryScheme) throws BusinessException;

	public AggAllocateOutVO[] save(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException;

	public AggAllocateOutVO[] unsave(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException;

	public AggAllocateOutVO[] approve(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException;

	public AggAllocateOutVO[] unapprove(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException;

	public AggAllocateOutVO[] queryBillsByCondition(WaLoginContext context, String condition, String orderSQL)
			throws BusinessException;

	public void deleteByContext(WaLoginVO waLoginVO) throws BusinessException;

	public AggAllocateOutVO[] genPjShareDetail(WaLoginContext context) throws BusinessException;

	public List<AllocateCsvVO> transferToCsvInfo(Object[] datas) throws BusinessException;

	public List<AllocateCsvVO> transferToCsvInfo(AggAllocateOutVO[] bills) throws BusinessException;

}
