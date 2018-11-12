package nc.impl.wa.classitem;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import nc.hr.utils.ResHelper;
import nc.vo.hr.comp.sort.ItemSortUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wabm.util.REUtil;

import org.apache.commons.lang.StringUtils;

/**
 * （1） 在图论中，如果依赖关系减少了，已经设置好的top排序顺序照样是正确的。不需要重新排序。（xuanlt） （2） 问题：
 * 来自其他数据源的项目可能依赖（本薪资类别、非本期间）或者（本期间、非本类别）的项目。但是我们仅仅查找 [f,c,d]_\d+， 所以，我们全部认为是
 * （本薪资类别、本期间） 的项目。 Created on 2007-11-8
 * 
 * @author zhangg
 */
public class ItemSort {

	private static final long serialVersionUID = 1L;
	private WaClassVO waclassVO = null;

	public WaClassVO getWaclassVO() {
		return waclassVO;
	}

	public void setWaclassVO(WaClassVO waclassVO) {
		this.waclassVO = waclassVO;
	}

	private static final String ITEMKEYPATTERN = "[f,c,d]_\\d+";

	private ISortHelper sortHelper = new WaOrtherFuncSortHelper();

	/**
	 * 确定薪资项目间的相互依赖关系 <li>a) 系统项目[应发合计]依赖于所有的自定义的加项和减项 <li>b) 其他的系统项目依赖于[应发合计]
	 * <li>c) 手工输入，取薪资规则表和其他数据源的项目首先计算。 <li>d) 取公式的自定义项目， 根据公式设置确定对其他项目的依赖关系。
	 * <p>
	 * 系统项目
	 * <li>1 应发合计
	 * <li>2 扣款合计
	 * <li>3 实发合计
	 * <li>4 本次扣税
	 * <li>5 本次扣税基数
	 * <li>6 本月扣零
	 * <li>7 上月扣零
	 * <li>8 已扣税
	 * <li>9 已扣税基数
	 * <li>10 补发扣税
	 * <li>11 补发金额
	 * <li>12 年度奖金
	 * <li>13 当月工资
	 * <li>14 应纳税所得额月平均
	 * 
	 * @param itemVO
	 * @return
	 * @throws BusinessException
	 */

	private List<WaClassItemVO> getItemDependItems(WaClassItemVO itemVO, WaClassItemVO[] itemVOs,
			final WaClassVO waclassVO) throws BusinessException {
		this.waclassVO = waclassVO;
		// 项目必须是来自公式、规则表、其他系统的
		List<WaClassItemVO> itemDependItems = new LinkedList<WaClassItemVO>();
		itemDependItems = parseFormula(itemVO, itemVOs);

		return itemDependItems.isEmpty() ? null : itemDependItems;
	}

	private void addAnItem(List<WaClassItemVO> itemDependItems, String itemId, WaClassItemVO[] itemVOs)
			throws BusinessException {
		WaClassItemVO classitemVO = getSpecialSysItem(itemId, itemVOs);
		if (classitemVO != null) {
			addAnItem(itemDependItems, classitemVO);
		}
	}

	private void addAnItem(List<WaClassItemVO> itemDependItems, WaClassItemVO classitemVO) {
		if (!itemDependItems.contains(classitemVO)) {
			itemDependItems.add(classitemVO);
		}
	}

	/**
	 * 解析自定义的公式
	 * 
	 * @param itemVO
	 * @param itemVOs
	 * @return
	 * @throws BusinessException
	 * @throws BusinessException
	 */
	private List<WaClassItemVO> parseFormula(WaClassItemVO itemVO, WaClassItemVO[] itemVOs) throws BusinessException {
		List<WaClassItemVO> itemDependItems = new LinkedList<WaClassItemVO>();
		String formula = itemVO.getVformula();
		List<String> itemidsList = new Vector<String>();
		if (!StringUtils.isBlank(formula)) {
			// 专门为 其他数据源薪资 添加
			if (itemVO.getFromEnumVO().equals(FromEnumVO.WAORTHER) || itemVO.getFromEnumVO().equals(FromEnumVO.BM)) {
				itemidsList = sortHelper.getDependItemKeys(itemVO);
			} else {
				if (formula.indexOf("preAdjustDate") > -1) {
					List<String> replaceStrs = REUtil.REFindMatches("preAdjustDate\\([f,c,d]_\\d+\\)", formula);
					for (String replaceStr : replaceStrs) {
						formula = formula.replace(replaceStr, "");
					}
				}
				// MOD {project payshare formula} kevin.nie 2017-10-25 start 
				if (formula.indexOf("waProjSalary") > -1) {
					List<String> replaceStrs = REUtil.REFindMatches("waProjSalary\\([f,c,d]_\\d+\\)", formula);
					for (String replaceStr : replaceStrs) {
						formula = formula.replace(replaceStr, "");
					}
				}
				// {project payshare formula} kevin.nie 2017-10-25 end
				itemidsList = pattern(formula);
			}
		}
		if (itemidsList.size() > 0) {
			for (String itemid : itemidsList) {
				try {
					addAnItem(itemDependItems, itemid, itemVOs);
				} catch (BusinessException e) {
					throw new BusinessException("[" + itemVO.getMultilangName() + "]:" + e.getMessage());
				}
			}
		}

		return itemDependItems;
	}

	/**
	 * 通过正在表达式确定公式中含的项目 解析公式 Created on 2008-5-12
	 * 
	 * @author zhangg
	 * @param input
	 * @return List<String> 如：f_1, f_232
	 */
	public static Vector<String> pattern(String input) {
		return REUtil.REFindMatches(ITEMKEYPATTERN, input);

	}

	/**
	 * 
	 * @param classitemVOs
	 * @param itemId
	 * @return
	 * @throws BusinessException
	 * @throws BusinessException
	 */
	private WaClassItemVO getSpecialSysItem(String itemId, WaClassItemVO[] classitemVOs) throws BusinessException {
		for (WaClassItemVO classitemVO : classitemVOs) {
			if (classitemVO.getItemkey().equals(itemId)) {
				return classitemVO;
			}
		}

		if (!isItemExist(itemId)) {
			throw new BusinessException(ResHelper.getString("60130classpower", "060130classpower0189")/*
																									 * @
																									 * res
																									 * "公式中引用的项目未添加到此薪资类别！"
																									 */);
		} else {
			return null;
		}
	}

	/**
	 * 该薪资方案是否使用该itemKey
	 * 
	 * @author xuanlt on 2010-5-28
	 * @param itemKey
	 * @return
	 * @throws BusinessException
	 * @return boolean
	 */
	private boolean isItemExist(String itemKey) throws BusinessException {
		if (waclassVO != null) {
			//
			ClassItemManageServiceImpl classitemImpl = new ClassItemManageServiceImpl();
			return classitemImpl.isItemExist(waclassVO, itemKey);
		} else {

			return true;
		}
	}

	/**
	 * 组织项目的依赖关系
	 * 
	 * @param itemVOs
	 * @return
	 * @throws BusinessException
	 */
	public HashMap<WaClassItemVO, List<WaClassItemVO>> getItemHashMap(WaClassItemVO[] itemVOs, WaClassVO waclassVO)
			throws BusinessException {
		HashMap<WaClassItemVO, List<WaClassItemVO>> itemHashMap = new LinkedHashMap<WaClassItemVO, List<WaClassItemVO>>();
		for (WaClassItemVO itemVO : itemVOs) {
			itemHashMap.put(itemVO, getItemDependItems(itemVO, itemVOs, waclassVO));
		}
		return itemHashMap;

	}

	/**
	 * 项目的计算顺序 waclassVO 必须具有主键 ，当前年 当前月
	 * 
	 * @param itemVOs
	 * @throws BusinessException
	 */

	public WaClassItemVO[] getSortedWaClassItemVOs(WaClassItemVO[] itemVOs, WaClassVO waclassVO)
			throws BusinessException {
		// 组织数据
		HashMap<WaClassItemVO, List<WaClassItemVO>> itemHashMap = getItemHashMap(itemVOs, waclassVO);

		// 排好顺序
		List<WaClassItemVO> list = new ItemSortUtil<WaClassItemVO>().toplogicalSort(itemHashMap);

		// 设置计算顺序
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setIcomputeseq(Integer.valueOf(i + 1));
		}

		return list.toArray(new WaClassItemVO[list.size()]);
	}

	// vformular 不为空
	// 来源是 ： 公式 、规则表、 其他数据源中的三个函数绝对时间段、相对时间段、团队统计函数
	// 如果以上三个函数是同一个方案、同一个期间，才会有影响
	// 解决方案：定义一个帮助类，专门判断薪资的其他数据源是否有影响。

}