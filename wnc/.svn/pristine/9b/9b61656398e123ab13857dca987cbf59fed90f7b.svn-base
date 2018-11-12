package nc.itf.hrpub;

import java.util.Map;

import nc.vo.pub.BusinessException;

public interface IDataExchangeExternalExecutor {
	public abstract Object getBizEntityID() throws BusinessException;

	/**
	 * 保存前事件
	 * 
	 * @throws BusinessException
	 */
	public abstract void beforeUpdate() throws BusinessException;

	/**
	 * 保存后事件
	 * 
	 * @throws BusinessException
	 */
	public abstract void afterUpdate() throws BusinessException;

	/**
	 * 转换后事件
	 * 
	 * @throws BusinessException
	 */
	public abstract void afterConvert() throws BusinessException;

	/**
	 * 转换前事件
	 * 
	 * @throws BusinessException
	 */
	public abstract void beforeConvert() throws BusinessException;

	/**
	 * 查询前事件
	 * 
	 * @throws BusinessException
	 */
	public abstract void beforeQuery() throws BusinessException;

	/**
	 * 查询后事件
	 * 
	 * @throws BusinessException
	 */
	public abstract void afterQuery() throws BusinessException;

	/**
	 * 实现BP保存
	 * 
	 * @throws BusinessException
	 */
	public abstract void doUpdateByBP() throws BusinessException;

	/**
	 * 插入前业务校验
	 * 
	 * @param rowMap
	 * 
	 * @throws BusinessException
	 */
	public abstract void beforeInsertOperation(Map<String, Object> rowMap)
			throws BusinessException;

	/**
	 * 更新前业务校验
	 * 
	 * @param rowMap
	 * 
	 * @throws BusinessException
	 */
	public abstract void beforeUpdateOperation(Map<String, Object> rowMap)
			throws BusinessException;

}