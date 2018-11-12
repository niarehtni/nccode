package nc.itf.hrwa;

import java.util.Map;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.vo.wa.projsalary.ProjSalaryHVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;

public interface IProjsalaryMaintain {

	public void delete(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills) throws BusinessException;

	public AggProjSalaryVO[] insert(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException;

	public AggProjSalaryVO[] update(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException;

	public AggProjSalaryVO[] query(IQueryScheme queryScheme) throws BusinessException;

	public AggProjSalaryVO[] save(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException;

	public AggProjSalaryVO[] unsave(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException;

	public AggProjSalaryVO[] approve(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException;

	public AggProjSalaryVO[] unapprove(AggProjSalaryVO[] clientFullVOs, AggProjSalaryVO[] originBills)
			throws BusinessException;

	public DataVO[] qryPayDataByCondition(WaLoginContext waContext, String whereCondition) throws BusinessException;

	public Map<String, ProjSalaryHVO> qryClassItemProjHVOMap(String condition) throws BusinessException;

	public boolean isClassItemUnionProj(Map<String, ProjSalaryHVO> periodItemMap, ProjSalaryHVO hvo);

	public Map<String, WaClassItemVO> qryClassItemByPeriod(String whereCondition, String[] keyFields)
			throws BusinessException;

	public Map<String, DefdocVO> qryProjectMap(String whereCondition, String[] keyFields) throws BusinessException;

	public Map<String, PsndocVO> qryPsndocVOMap(String whereCondition, String[] keyFields) throws BusinessException;

	public AggProjSalaryVO[] importProjSalary(AggProjSalaryVO[] impAggVO, String[] delPks) throws BusinessException;
}
