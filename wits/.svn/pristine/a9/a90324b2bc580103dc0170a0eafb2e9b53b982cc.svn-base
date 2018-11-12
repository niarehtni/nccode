package nc.itf.twhr;

import java.util.List;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.nhicalc.NhiCalcVO;

public interface INhiCalcGenerateSrv {
	/**
	 * 根據定調薪及勞健保設定生成勞健保數據
	 * 
	 * @param pk_org
	 *            人力資源組織
	 * @param period
	 *            薪資期間
	 * @return 勞健保調整數據
	 * @throws BusinessException
	 * @throws Exception
	 */
	public NhiCalcVO[] generateAdjustNHIData(String pk_org, String period)
			throws BusinessException, Exception;

	/**
	 * 根據定調薪及勞健保設定獲取對比后的勞健保數據
	 * 
	 * @param psnList
	 *            人員PK列表
	 * @param pk_org
	 *            人力資源組織
	 * @param cyear
	 *            年
	 * @param cperiod
	 *            期間
	 * @return
	 * @throws BusinessException
	 */
	public NhiCalcVO[] getAdjustNHIData(List<String> psnList, String pk_org,
			String cyear, String cperiod) throws BusinessException;

	/**
	 * 根據定調薪及勞健保設定獲取對比后的勞健保數據
	 * 
	 * @param pk_psndoc
	 *            人員PK
	 * @param pk_org
	 *            人力資源組織
	 * @param inDutyDate
	 *            入职日期
	 * @return
	 * @throws BusinessException
	 */
	public NhiCalcVO[] getAdjustNHIData(String pk_psndoc, String pk_org,
			UFLiteralDate inDutyDate) throws BusinessException;
}
