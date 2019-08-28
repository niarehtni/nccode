package nc.itf.wa.psndocwadoc;

import nc.vo.pub.BusinessException;

/**
 * 劳健保生成服务
 * 
 * @author Connie.ZH
 * 
 */
public interface IPsndocBOISGenerateService {

	/**
	 * 根據所選的人員與生效日期，重新生成勞健保的級距數據
	 * 
	 * @param effectiveDate
	 *            界面所选的生效日期
	 * @param rangePsns
	 *            界面上所选择的人员
	 * @throws BusinessException
	 */
	public void generateBOISData(String effectiveDate, String[] rangePsns) throws BusinessException;

}
