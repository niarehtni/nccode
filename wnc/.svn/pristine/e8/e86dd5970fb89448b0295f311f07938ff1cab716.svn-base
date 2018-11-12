package nc.itf.hrpub;

import nc.vo.pub.BusinessException;

public interface IMDExchangeService {
	/**
	 * 通过Json做数据交换
	 * 
	 * @param jSONString
	 *            导入的Json串
	 * @return 返回的Json串
	 */
	public String JsonDataExchange(String jSONString);

	/**
	 * 檢查導入導出權限
	 * 
	 * @param method
	 *            導入導出標記
	 * @param pk_org
	 *            組織
	 * @param pk_ioschema
	 *            數據交換方案
	 * @param cuserid
	 *            操作用戶
	 * @param entityName
	 *            操作元數據名稱
	 * @return 如有權限則：返回元數據對照類PK
	 * @throws BusinessException
	 */
	public String checkExchangeRights(String method, String pk_org, String pk_ioschema, String cuserid,
			String entityName) throws BusinessException;

	/**
	 * 根據Session ID查詢異步任務進度
	 * 
	 * @param sessionid
	 * @return
	 */
	public String taskQuery(String sessionid);
}
