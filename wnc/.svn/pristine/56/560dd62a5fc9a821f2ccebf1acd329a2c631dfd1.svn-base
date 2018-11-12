package nc.itf.twhr;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.nhicalc.BaoAccountVO;

public interface ITwhrMaintain extends ISmartService {

	public BaoAccountVO[] query(IQueryScheme queryScheme) throws BusinessException, Exception;

	/**
	 * 此接口是导入的vo如果库里面有vo则更新，否则就存入(劳保)
	 * 
	 * @throws BusinessException
	 */
	public void insertupdatelabor(BaoAccountVO[] baoaccountvos) throws BusinessException;

	/**
	 * 此接口是导入的vo如果库里面有vo则更新，否则就存入（健保）
	 * 
	 * @throws BusinessException
	 */
	public void insertupdatehealth(BaoAccountVO[] baoaccountvos) throws BusinessException;

	/**
	 * 此接口是导入的vo如果库里面有vo则更新，否则就存入（劳退）
	 * 
	 * @throws BusinessException
	 */
	public void insertupdateretire(BaoAccountVO[] baoaccountvos) throws BusinessException;
}