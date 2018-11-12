package nc.itf.hr.dataexchange;

import java.util.List;
import java.util.Map;

import nc.vo.pub.BusinessException;

public interface IDataFormatService {
	/**
	 * 根据格式编码取导出格式定义
	 * 
	 * @param formatCode
	 *            格式编码
	 * @return
	 * @throws BusinessException
	 */
	public List<Map> getFormatByCode(String formatCode) throws BusinessException;

	/**
	 * 根据格式化SQL返回数据
	 * 
	 * @param strSQL
	 * @return
	 * @throws BusinessException
	 */
	public List<Map> getDataFormatSQL(String strSQL) throws BusinessException;
}
