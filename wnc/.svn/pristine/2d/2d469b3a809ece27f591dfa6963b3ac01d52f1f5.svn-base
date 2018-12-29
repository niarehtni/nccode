package nc.itf.twhr;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.diffinsurance.DiffinsuranceVO;

public interface IDiffinsuranceaMaintain extends ISmartService {

	public DiffinsuranceVO[] query(IQueryScheme queryScheme)
			throws BusinessException, Exception;

	public void Blanace(String pk_org, String pk_period)
			throws BusinessException; // 新增

	public void Delete(String pk_org, String pk_period)
			throws BusinessException; // 删除分析表里的数据

	public Boolean ifexist(String pk_org, String pk_period)
			throws BusinessException; // 删除分析表里的数据
}