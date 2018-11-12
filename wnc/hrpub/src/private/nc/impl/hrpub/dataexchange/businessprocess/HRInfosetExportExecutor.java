package nc.impl.hrpub.dataexchange.businessprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.impl.hrpub.dataexchange.DataExportExecutor;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.pub.BusinessException;

public class HRInfosetExportExecutor extends DataExportExecutor implements
		IDataExchangeExternalExecutor {
	private List<String> extendBizEntity = new ArrayList<String>();

	public HRInfosetExportExecutor() throws BusinessException {
		super();
	}

	private Map<String, String> psnPKCodeMap;

	@Override
	public void beforeConvert() throws BusinessException {
		if (this.getNcValueObjects() != null
				&& this.getNcValueObjects().size() > 0) {
			for (Map<String, Object> ncobj : this.getNcValueObjects()) {
				for (Entry<String, Object> entry : ncobj.entrySet()) {
					if (entry.getKey().contains("pk_psndoc")) {
						String pk_psndoc = (String) entry.getValue();
						String code = getCodeByPK(pk_psndoc);
						entry.setValue(code);
					}
				}
			}
		}
	}

	private String getCodeByPK(String pk_psndoc) throws BusinessException {
		String code = "";
		if (psnPKCodeMap.containsKey(pk_psndoc)) {
			code = psnPKCodeMap.get(pk_psndoc);
		} else {
			String strSQL = "select code from bd_psndoc where pk_psndoc = '"
					+ pk_psndoc + "'";
			code = (String) this.getBaseDAO().executeQuery(strSQL,
					new ColumnProcessor());
			psnPKCodeMap.put(pk_psndoc, code);
		}
		return code;
	}

	public Map<String, String> getPsnPKCodeMap() {
		if (psnPKCodeMap == null) {
			psnPKCodeMap = new HashMap<String, String>();
		}
		return psnPKCodeMap;
	}

	public void setPsnPKCodeMap(Map<String, String> psnPKCodeMap) {
		this.psnPKCodeMap = psnPKCodeMap;
	}

	@Override
	public Object getBizEntityID() throws BusinessException {
		// 必須賦值，否則不會加載本類型
		// 實體ID為md_class的ID欄位
		if (this.extendBizEntity.size() == 0) {
			String strSQL = "select distinct classid from md_property where name = 'pk_psndoc' and classid in ("
					+ "select id from md_class where fullclassname in ("
					+ "select vo_class_name from hr_infoset where pk_infoset in ("
					+ "select pk_infoset from hr_infoset_item where item_code ='pk_psndoc'))) and datatype != '218971f0-e5dc-408b-9a32-56529dddd4db'";
			List<String> valueList = (List<String>) this.getBaseDAO()
					.executeQuery(strSQL, new ColumnListProcessor());
			if (valueList != null && valueList.size() > 0) {
				for (String value : valueList) {
					this.extendBizEntity.add(value);
				}
			}

			return this.extendBizEntity;
		}

		return new ArrayList<String>();
	}

	@Override
	public void beforeUpdate() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void afterUpdate() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeInsertOperation(Map<String, Object> rowMap)
			throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap)
			throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void doUpdateByBP() throws BusinessException {
		// TODO 自动生成的方法存根

	}
}
