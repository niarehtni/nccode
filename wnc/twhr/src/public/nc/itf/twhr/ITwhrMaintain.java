package nc.itf.twhr;

import java.util.Map;
import java.util.Set;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.nhicalc.BaoAccountVO;

public interface ITwhrMaintain extends ISmartService {
	/**
	 * 人员未匹配到组织
	 */
	public static Integer ERROR_NO_MATCH_ORG = 1;

	public BaoAccountVO[] query(IQueryScheme queryScheme) throws BusinessException, Exception;

	/**
	 * 此接口是导入的vo如果库里面有vo则更新，否则就存入(劳保)
	 * @return Map<Integer,Set<String>> <错误代码,<人员身份证号>>
	 * @throws BusinessException
	 */
	public Map<Integer,Set<String>> insertupdatelabor(BaoAccountVO[] baoaccountvos,String pk_legal_org,String waperiod) throws BusinessException;

	/**
	 * 此接口是导入的vo如果库里面有vo则更新，否则就存入（健保）
	 * @return Map<Integer,Set<String>> <错误代码,<人员身份证号>>
	 * @throws BusinessException
	 */
	public Map<Integer,Set<String>> insertupdatehealth(BaoAccountVO[] baoaccountvos,String pk_legal_org,String waperiod) throws BusinessException;

	/**
	 * 此接口是导入的vo如果库里面有vo则更新，否则就存入（劳退）
	 * @return Map<Integer,Set<String>> <错误代码,<人员身份证号>>
	 * @throws BusinessException
	 */
	public Map<Integer,Set<String>> insertupdateretire(BaoAccountVO[] baoaccountvos,String pk_legal_org,String waperiod) throws BusinessException;
	/**
	 * 结算设置
	 * @param baoaccountvos
	 * @param wa_period 
	 * @param pk_wa_class 
	 * @throws BusinessException
	 */
	void Settle(BaoAccountVO[] baoaccountvos, String pk_wa_class, String wa_period) throws BusinessException;
}