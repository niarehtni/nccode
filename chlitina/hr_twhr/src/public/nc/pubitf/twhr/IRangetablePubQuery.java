package nc.pubitf.twhr;
import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.twhr.rangetable.RangeLineVO;
import nc.vo.twhr.rangetable.RangeTableAggVO;

public interface IRangetablePubQuery {
	/**
	 * 按组织、类型和日期查询级距表
	 * 
	 * @param pk_org
	 *            组织ID
	 * @param tableType
	 *            级距表类型（1.劳保,2.劳退,3.健保,4.综所税）
	 * @param queryDate
	 *            查询日期
	 * @return 级距表
	 * @Deprecated 已经废弃了组织,改为全局节点
	 * @throws BusinessException
	 */
	@Deprecated
	public RangeTableAggVO[] queryRangetableByType(String pk_org,
			int tableType, UFDate queryDate) throws BusinessException;

	/**
	 * 按组织、类型、日期及金额查询级距
	 * 
	 * @param pk_org
	 *            组织ID
	 * @param tableType
	 *            级距表类型（1.劳保,2.劳退,3.健保,4.综所税）
	 * @param queryDate
	 *            查询日期
	 * @param amount
	 *            查询金额
	 * @return 级距明细行
	 * @Deprecated 已经废弃了组织,改为全局节点
	 * @throws BusinessException
	 */
	@Deprecated
	public RangeLineVO queryRangeLineByAmount(String pk_org, int tableType,
			UFDate queryDate, UFDouble amount) throws BusinessException;

	/**
	 * 按组织、类型、日期及金额列表批量查询级距
	 * 
	 * @param pk_org
	 *            组织ID
	 * @param tableType
	 *            级距表类型（1.劳保,2.劳退,3.健保,4.综所税）
	 * @param queryDate
	 *            查询日期
	 * @param amounts
	 *            查询金额列表
	 * @return
	 * @Deprecated 已经废弃了组织,改为全局节点
	 * @throws BusinessException
	 */
	@Deprecated
	public Map<UFDouble, RangeLineVO> batchQueryRangeLineByAmount(
			String pk_org, int tableType, UFDate queryDate, UFDouble[] amounts)
			throws BusinessException;
	//start 搜索不再需要组织 Ares.Tank 2018-7-25 18:09:17
	/**
	 * 按类型和日期查询级距表
	 * 
	 * @param tableType
	 *            级距表类型（1.劳保,2.劳退,3.健保,4.综所税）
	 * @param queryDate
	 *            查询日期
	 * @return 级距表
	 * @throws BusinessException
	 */
	public RangeTableAggVO[] queryRangetableByType(
			int tableType, UFDate queryDate) throws BusinessException;

	/**
	 * 按类型、日期及金额查询级距
	 * 
	 * @param tableType
	 *            级距表类型（1.劳保,2.劳退,3.健保,4.综所税）
	 * @param queryDate
	 *            查询日期
	 * @param amount
	 *            查询金额
	 * @return 级距明细行
	 * @throws BusinessException
	 */
	public RangeLineVO queryRangeLineByAmount(int tableType,
			UFDate queryDate, UFDouble amount) throws BusinessException;

	/**
	 * 按类型、日期及金额列表批量查询级距
	 * 
	 * @param tableType
	 *            级距表类型（1.劳保,2.劳退,3.健保,4.综所税）
	 * @param queryDate
	 *            查询日期
	 * @param amounts
	 *            查询金额列表
	 * @return
	 * @throws BusinessException
	 */
	public Map<UFDouble, RangeLineVO> batchQueryRangeLineByAmount(
			int tableType, UFDate queryDate, UFDouble[] amounts)
			throws BusinessException;
	//end 搜索不再需要组织 Ares.Tank 2018-7-25 18:09:17
}
