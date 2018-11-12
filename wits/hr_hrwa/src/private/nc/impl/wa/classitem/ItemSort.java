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
 * ��1�� ��ͼ���У����������ϵ�����ˣ��Ѿ����úõ�top����˳����������ȷ�ġ�����Ҫ�������򡣣�xuanlt�� ��2�� ���⣺
 * ������������Դ����Ŀ������������н����𡢷Ǳ��ڼ䣩���ߣ����ڼ䡢�Ǳ���𣩵���Ŀ���������ǽ������� [f,c,d]_\d+�� ���ԣ�����ȫ����Ϊ��
 * ����н����𡢱��ڼ䣩 ����Ŀ�� Created on 2007-11-8
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
	 * ȷ��н����Ŀ����໥������ϵ <li>a) ϵͳ��Ŀ[Ӧ���ϼ�]���������е��Զ���ļ���ͼ��� <li>b) ������ϵͳ��Ŀ������[Ӧ���ϼ�]
	 * <li>c) �ֹ����룬ȡн�ʹ�������������Դ����Ŀ���ȼ��㡣 <li>d) ȡ��ʽ���Զ�����Ŀ�� ���ݹ�ʽ����ȷ����������Ŀ��������ϵ��
	 * <p>
	 * ϵͳ��Ŀ
	 * <li>1 Ӧ���ϼ�
	 * <li>2 �ۿ�ϼ�
	 * <li>3 ʵ���ϼ�
	 * <li>4 ���ο�˰
	 * <li>5 ���ο�˰����
	 * <li>6 ���¿���
	 * <li>7 ���¿���
	 * <li>8 �ѿ�˰
	 * <li>9 �ѿ�˰����
	 * <li>10 ������˰
	 * <li>11 �������
	 * <li>12 ��Ƚ���
	 * <li>13 ���¹���
	 * <li>14 Ӧ��˰���ö���ƽ��
	 * 
	 * @param itemVO
	 * @return
	 * @throws BusinessException
	 */

	private List<WaClassItemVO> getItemDependItems(WaClassItemVO itemVO, WaClassItemVO[] itemVOs,
			final WaClassVO waclassVO) throws BusinessException {
		this.waclassVO = waclassVO;
		// ��Ŀ���������Թ�ʽ�����������ϵͳ��
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
	 * �����Զ���Ĺ�ʽ
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
			// ר��Ϊ ��������Դн�� ���
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
	 * ͨ�����ڱ��ʽȷ����ʽ�к�����Ŀ ������ʽ Created on 2008-5-12
	 * 
	 * @author zhangg
	 * @param input
	 * @return List<String> �磺f_1, f_232
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
																									 * "��ʽ�����õ���Ŀδ��ӵ���н�����"
																									 */);
		} else {
			return null;
		}
	}

	/**
	 * ��н�ʷ����Ƿ�ʹ�ø�itemKey
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
	 * ��֯��Ŀ��������ϵ
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
	 * ��Ŀ�ļ���˳�� waclassVO ����������� ����ǰ�� ��ǰ��
	 * 
	 * @param itemVOs
	 * @throws BusinessException
	 */

	public WaClassItemVO[] getSortedWaClassItemVOs(WaClassItemVO[] itemVOs, WaClassVO waclassVO)
			throws BusinessException {
		// ��֯����
		HashMap<WaClassItemVO, List<WaClassItemVO>> itemHashMap = getItemHashMap(itemVOs, waclassVO);

		// �ź�˳��
		List<WaClassItemVO> list = new ItemSortUtil<WaClassItemVO>().toplogicalSort(itemHashMap);

		// ���ü���˳��
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setIcomputeseq(Integer.valueOf(i + 1));
		}

		return list.toArray(new WaClassItemVO[list.size()]);
	}

	// vformular ��Ϊ��
	// ��Դ�� �� ��ʽ ������� ��������Դ�е�������������ʱ��Ρ����ʱ��Ρ��Ŷ�ͳ�ƺ���
	// �����������������ͬһ��������ͬһ���ڼ䣬�Ż���Ӱ��
	// �������������һ�������࣬ר���ж�н�ʵ���������Դ�Ƿ���Ӱ�졣

}