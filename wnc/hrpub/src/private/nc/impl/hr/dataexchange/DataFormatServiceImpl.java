package nc.impl.hr.dataexchange;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.bs.hr.dataexchange.export.FormatHelper;
import nc.itf.hr.dataexchange.IDataFormatService;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.StringUtils;

public class DataFormatServiceImpl implements IDataFormatService {

	private BaseDAO baseDAO = null;

	private BaseDAO getBaseDAO() {
		if (baseDAO == null) {
			baseDAO = new BaseDAO();
		}

		return baseDAO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map> getFormatByCode(String formatCode) throws BusinessException {
		String strSQL = " SELECT  item.linenumber , ";
		strSQL += "         item.posnumber , ";
		strSQL += "         item.itemcode , ";
		strSQL += "         item.itemname , ";
		strSQL += "         item.datatype , ";
		strSQL += "         item.byteLength , ";
		strSQL += "         item.fillmode , ";
		strSQL += "         item.fillstr , ";
		strSQL += "         item.prefix , ";
		strSQL += "         item.suffix , ";
		strSQL += "         item.splitter , ";
		strSQL += "         item.issum , ";
		strSQL += "         item.datasource , ";
		strSQL += "         item.datatable , ";
		strSQL += "         item.joinkey , ";
		strSQL += "         isnull(item.datacontext, '') as datacontext, ";
		strSQL += "         item.colnumber, ";
		strSQL += "         item.colbytelength, ";
		strSQL += "         item.enablecondition, ";
		strSQL += "         item.forcechinese";
		strSQL += " FROM    wa_expformat_item item ";
		strSQL += "         INNER JOIN wa_expformat_head head ON item.pk_formathead = head.pk_formathead ";
		strSQL += " WHERE   head.code = '" + formatCode + "' ";
		strSQL += "         AND head.dr = 0 ";
		strSQL += "         AND item.dr = 0 ";
		strSQL += " ORDER BY item.linenumber, item.posnumber";

		List<Map> formats = (List<Map>) this.getBaseDAO().executeQuery(strSQL, new MapListProcessor());
		return formats;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map> getDataFormatSQL(String strSQL) throws BusinessException {
		return (List<Map>) getBaseDAO().executeQuery(strSQL, new MapListProcessor());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getWaItemGroupColumnsByItemGroup(String pk_org, String pk_wa_class, String cyear,
			String cperiod, String itemGroupCode, int colNumber, int colByteLength) throws BusinessException {
		Map<String, String> returnMap = new HashMap<String, String>();

		String strSQL = "select itemkey, name2, isnull(name3, name2) name3, mb.orderno  from wa_classitem wc inner join wa_itemgroupmember mb on wc.pk_wa_item = mb.pk_waitem inner join wa_itemgroup gp on gp.pk_itemgroup = mb.pk_itemgroup where gp.groupcode = '"
				+ itemGroupCode
				+ "' and wc.pk_org = '"
				+ pk_org
				+ "' and wc.pk_wa_class = '"
				+ pk_wa_class
				+ "' and wc.cyear='" + cyear + "' and wc.cperiod = '" + cperiod + "' order by orderno";

		List<Map<String, Object>> groupedWaItems = (List<Map<String, Object>>) this.getBaseDAO().executeQuery(strSQL,
				new MapListProcessor());

		Map<String, String[]> itemNames = new HashMap<String, String[]>(); // 薪資項目名稱緩存
		Map<Integer, String> orderItems = new HashMap<Integer, String>(); // 排序緩存
		if (groupedWaItems != null && groupedWaItems.size() > 0) {
			// 處理分組薪資項目，返回薪資項目列表，同時整理薪資項目名稱緩存及排序緩存
			strSQL = dealGroupedWaItems("", groupedWaItems, itemNames, orderItems);

			strSQL = "select "
					+ strSQL
					+ ",psn.id,isnull(psn.glbdef7,'N') glbdef7 from wa_data wd inner join bd_psndoc psn on psn.pk_psndoc = wd.pk_psndoc where wd.pk_org = '"
					+ pk_org + "' and wd.pk_wa_class = '" + pk_wa_class + "' and wd.cyear ='" + cyear
					+ "' and wd.cperiod = '" + cperiod + "' ";
			List<Map<String, Object>> psnWaDataList = (List<Map<String, Object>>) this.getBaseDAO().executeQuery(
					strSQL, new MapListProcessor());
			Map<String, Map<String, String>> psnItemValues = new HashMap<String, Map<String, String>>();

			// 處理薪資項目取值，psnItemValues中格式為Map<ID, MAP<ItemKey, Name:Value;>
			dealPsnItemValues(itemNames, psnWaDataList, psnItemValues);

			if (psnItemValues.size() > 0) {
				// 按人處理
				for (Entry<String, Map<String, String>> dataEntry : psnItemValues.entrySet()) {
					String[] cols = new String[colNumber];
					int curColIndex = 0;
					cols[curColIndex] = new String();

					// 按順序處理
					for (int i = 1; i <= orderItems.size(); i++) {
						String itemkey = orderItems.get(i);
						if (dataEntry.getValue().containsKey(itemkey)) {
							try {
								if (cols[curColIndex].getBytes(FormatHelper.TEXTENCODING).length
										+ dataEntry.getValue().get(itemkey).getBytes(FormatHelper.TEXTENCODING).length > colByteLength) {
									cols[curColIndex] = FormatHelper.getLengthStringEx(cols[curColIndex],
											colByteLength, " ", "R");
									curColIndex++;
								}

								if (curColIndex == colNumber) {
									break;
								} else {
									if (cols[curColIndex] == null) {
										cols[curColIndex] = new String();
									}
								}
								cols[curColIndex] += dataEntry.getValue().get(itemkey);
							} catch (UnsupportedEncodingException e) {
								throw new BusinessException(e.getMessage());
							}
						}
					}

					returnMap.put(dataEntry.getKey(), (cols[0] == null ? "" : cols[0])
							+ (cols[1] == null ? "" : cols[1]) + (cols[2] == null ? "" : cols[2])
							+ (cols[3] == null ? "" : cols[3]) + (cols[4] == null ? "" : cols[4])
							+ (cols[5] == null ? "" : cols[5]));
				}
			}

		}
		return returnMap;
	}

	private void dealPsnItemValues(Map<String, String[]> itemNames, List<Map<String, Object>> psnWaDataList,
			Map<String, Map<String, String>> psnItemValues) {
		if (psnWaDataList != null && psnWaDataList.size() > 0) {
			for (Map<String, Object> wadata : psnWaDataList) {
				String id = (String) wadata.get("id");
				UFBoolean isForeign = new UFBoolean((String) wadata.get("glbdef7"));

				if (!psnItemValues.containsKey(id)) {
					psnItemValues.put(id, new HashMap<String, String>());
				}

				for (Entry<String, Object> dataEntry : wadata.entrySet()) {
					if (!dataEntry.getKey().equals("id") && !dataEntry.getKey().equals("glbdef7")) {
						UFDouble value = SalaryDecryptUtil.decrypt(new UFDouble(String.valueOf(dataEntry.getValue())));
						if (!UFDouble.ZERO_DBL.equals(value)) {
							psnItemValues.get(id).put(
									dataEntry.getKey(),
									(itemNames.get(dataEntry.getKey())[isForeign.booleanValue() ? 1 : 0] + ":" + value
											.setScale(1, UFDouble.ROUND_HALF_UP).toString()) + ";");
						}
					}
				}
			}
		}
	}

	private String dealGroupedWaItems(String strSQL, List<Map<String, Object>> groupedWaItems,
			Map<String, String[]> itemNames, Map<Integer, String> orderItems) {
		for (Map<String, Object> waitem : groupedWaItems) {
			String itemkey = (String) waitem.get("itemkey");
			if (StringUtils.isEmpty(strSQL)) {
				strSQL = "wd." + itemkey;
			} else {
				strSQL += ",wd." + itemkey;
			}

			itemNames.put(itemkey, new String[] { (String) waitem.get("name2"), (String) waitem.get("name3") });
			orderItems.put((int) waitem.get("orderno"), (String) waitem.get("itemkey"));
		}
		return strSQL;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Map<Integer, Map<String, Object>>> getWaItemByItemGroup(String pk_org, String pk_wa_class,
			String cyear, String cperiod, String itemGroupCode) throws BusinessException {
		// 格式: MAP<ID, MAP<ORDERNO, MAP<NAME, VALUE>>>
		Map<String, Map<Integer, Map<String, Object>>> returnMap = new HashMap<String, Map<Integer, Map<String, Object>>>();

		String strSQL = "select itemkey, name2, isnull(name3, name2) name3, mb.orderno  from wa_classitem wc inner join wa_itemgroupmember mb on wc.pk_wa_item = mb.pk_waitem inner join wa_itemgroup gp on gp.pk_itemgroup = mb.pk_itemgroup where gp.groupcode = '"
				+ itemGroupCode
				+ "' and wc.pk_org = '"
				+ pk_org
				+ "' and wc.pk_wa_class = '"
				+ pk_wa_class
				+ "' and wc.cyear='" + cyear + "' and wc.cperiod = '" + cperiod + "' order by orderno";

		List<Map<String, Object>> groupedWaItems = (List<Map<String, Object>>) this.getBaseDAO().executeQuery(strSQL,
				new MapListProcessor());

		Map<String, String[]> itemNames = new HashMap<String, String[]>(); // 薪資項目名稱緩存
		Map<Integer, String> orderItems = new HashMap<Integer, String>(); // 排序緩存
		if (groupedWaItems != null && groupedWaItems.size() > 0) {
			// 處理分組薪資項目，返回薪資項目列表，同時整理薪資項目名稱緩存及排序緩存
			strSQL = dealGroupedWaItems("", groupedWaItems, itemNames, orderItems);

			strSQL = "select "
					+ strSQL
					+ ",psn.id,isnull(psn.glbdef7,'N') glbdef7 from wa_data wd inner join bd_psndoc psn on psn.pk_psndoc = wd.pk_psndoc where wd.pk_org = '"
					+ pk_org + "' and wd.pk_wa_class = '" + pk_wa_class + "' and wd.cyear ='" + cyear
					+ "' and wd.cperiod = '" + cperiod + "' ";
			List<Map<String, Object>> psnWaDataList = (List<Map<String, Object>>) this.getBaseDAO().executeQuery(
					strSQL, new MapListProcessor());
			Map<String, Map<String, String>> psnItemValues = new HashMap<String, Map<String, String>>();

			// 處理薪資項目取值，psnItemValues中格式為Map<ID, MAP<ItemKey, Name:Value;>
			if (psnWaDataList != null && psnWaDataList.size() > 0) {
				for (Map<String, Object> wadata : psnWaDataList) {
					String id = (String) wadata.get("id");
					UFBoolean isForeign = new UFBoolean((String) wadata.get("glbdef7"));

					if (!psnItemValues.containsKey(id)) {
						psnItemValues.put(id, new HashMap<String, String>());
					}

					for (Entry<String, Object> dataEntry : wadata.entrySet()) {
						if (!dataEntry.getKey().equals("id") && !dataEntry.getKey().equals("glbdef7")) {
							UFDouble value = SalaryDecryptUtil.decrypt(new UFDouble(
									String.valueOf(dataEntry.getValue())));
							if (!UFDouble.ZERO_DBL.equals(value)) {
								String itemName = itemNames.get(dataEntry.getKey())[isForeign.booleanValue() ? 1 : 0];
								String itemValue = value.setScale(1, UFDouble.ROUND_HALF_UP).toString();
								psnItemValues.get(id).put(dataEntry.getKey(), itemName + ":" + itemValue);
							}
						}
					}
				}
			}

			if (psnItemValues.size() > 0) {
				// 按人處理
				for (Entry<String, Map<String, String>> dataEntry : psnItemValues.entrySet()) {
					// 按順序處理
					Map<Integer, Map<String, Object>> orderNameValues = new HashMap<Integer, Map<String, Object>>();
					int countIndex = 1;
					for (Entry<Integer, String> orderentry : orderItems.entrySet()) {
						String itemkey = orderentry.getValue();
						if (dataEntry.getValue().containsKey(itemkey)) {
							Map<String, Object> nameValue = new HashMap<String, Object>();
							nameValue.put("NAME", dataEntry.getValue().get(itemkey).split(":")[0]);
							nameValue.put("VALUE", dataEntry.getValue().get(itemkey).split(":")[1]);

							orderNameValues.put(countIndex++, nameValue);
						}
					}

					// 格式: MAP<ID, MAP<ORDERNO, MAP<NAME, VALUE>>>
					returnMap.put(dataEntry.getKey(), orderNameValues);
				}
			}

		}
		return returnMap;
	}
}
