package nc.impl.wa.classitem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.datasource.IHRDatasource;
import nc.vo.hr.datasource.HrDatasourceType;
import nc.vo.hr.formula.ConfigFileDescriptor;
import nc.vo.hr.formula.FormulaXmlHelper;
import nc.vo.wa.pub.HRWACommonConstants;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class HRWADatasource implements IHRDatasource {

	private static final String DATASOURCETYPE = "datasourcetype";

	private static Map<String, String> MODULEID_MAP = new HashMap<String, String>();

	static {
		MODULEID_MAP.put("HI", HRWACommonConstants.MODULEID_HRHI);
		MODULEID_MAP.put("TA", HRWACommonConstants.MODULEID_HRTA);
		MODULEID_MAP.put("PE", HRWACommonConstants.MODULEID_HRPE);
		MODULEID_MAP.put("BM", HRWACommonConstants.MODULEID_HRBM);
		MODULEID_MAP.put("TWHR", "68J6"); // ssx added for TWHR on 2017-07-24
	}

	@Override
	public HrDatasourceType[] getDatasource(
			ConfigFileDescriptor configFileDescriptor) {

		List<HrDatasourceType> list = FormulaXmlHelper.parseXmlFile2(
				configFileDescriptor, DATASOURCETYPE, HrDatasourceType.class);
		// 执行多语言转换
		if (list != null && !list.isEmpty()) {
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				HrDatasourceType hrDatasourceType = (HrDatasourceType) iterator
						.next();

				if (!"WAORTHER".equals(hrDatasourceType.getProductcode())) {
					// 会计平台的编码是多少？
					boolean isEnable = PubEnv
							.isModuleStarted(PubEnv.getPk_group(), MODULEID_MAP
									.get(hrDatasourceType.getProductcode()));
					if (!isEnable) {
						iterator.remove();
						continue;
					}
				}

				String resname = hrDatasourceType.getName();
				String[] res = parseResInfo(resname);
				if (!ArrayUtils.isEmpty(res)) {
					hrDatasourceType.setName(ResHelper
							.getString(res[0], res[1]));
				}

				resname = hrDatasourceType.getDesc();
				res = parseResInfo(resname);
				if (!ArrayUtils.isEmpty(res)) {
					hrDatasourceType.setDesc(ResHelper
							.getString(res[0], res[1]));
				}
			}
		}
		return list.toArray(new HrDatasourceType[list.size()]);
	}

	private String[] parseResInfo(String strResInfo) {
		if (!StringUtils.isBlank(strResInfo)) {
			String[] resInfoArray = strResInfo.split(";");
			if (resInfoArray.length >= 2
					&& !StringUtils.isBlank(resInfoArray[0])
					&& !StringUtils.isBlank(resInfoArray[0])) {
				return resInfoArray;
			}
		}
		return null;
	}
}
