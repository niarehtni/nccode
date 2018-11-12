package nc.impl.hrpub.dataexchange.businessprocess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.NCLocator;
import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

import uap.distribution.util.StringUtil;

public class PsnInfosetImportExecutor extends DataImportExecutor implements IDataExchangeExternalExecutor {

	private List<String> extendBizEntity = new ArrayList<String>();
	private Map<String, String> psnCodePKMap;
	static int recordnum = -1;

	public Map<String, String> getPsnCodePKMap() {
		if (psnCodePKMap == null) {
			psnCodePKMap = new HashMap<String, String>();
		}
		return psnCodePKMap;
	}

	public void setPsnCodePKMap(Map<String, String> psnCodePKMap) {
		this.psnCodePKMap = psnCodePKMap;
	}

	public PsnInfosetImportExecutor() throws BusinessException {
		super();

		this.setActionOnDataExists(ExecuteActionEnum.UPDATE);
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
			List<String> valueList = (List<String>) this.getBaseDAO().executeQuery(strSQL, new ColumnListProcessor());
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
		if (this.getNcValueObjects() != null && this.getNcValueObjects().size() > 0) {
			String rowNo = "";
			try {
				for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {

					rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];

					String code = (String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("pk_psndoc"));
					if (StringUtil.isEmpty(code)) {
						code = (String) rowNCMap.get(rowNo + ":pk_psndoc");
					}
					// 员工考核信息专用通道
					if (this.getNcEntityName().equals("hi_psndoc_ass")) {
						UFLiteralDate enddate = new UFLiteralDate((String) rowNCMap.get(rowNo + ":enddate"));
						Collection<PsnJobVO> psnjobs = this.getBaseDAO().retrieveByClause(PsnJobVO.class,
								"pk_psndoc = '" + code + "'");
						for (PsnJobVO vo : psnjobs) {
							if (((vo.getBegindate().isSameDate(enddate) || vo.getBegindate().before(enddate))
									&& null != vo.getEnddate() && (vo.getEnddate().isSameDate(enddate) || vo
									.getEnddate().after(enddate)))
									|| (null == vo.getEnddate() && vo.getBegindate().before(enddate))) {
								rowNCMap.put(rowNo + ":pk_psnjob", vo.getPk_psnjob());
								rowNCMap.put(rowNo + ":pk_psnorg", vo.getPk_psnorg());
							}
						}
						if ((null == rowNCMap.get(rowNo + ":pk_psnjob"))
								|| (null == rowNCMap.get(rowNo + ":pk_psnorg"))) {
							throw new BusinessException("找不到指定的员工 [" + code + "]");
						}
					} else if (this.getPsnCodePKMap().containsKey(code)) {
						String pk_psndoc = this.getPsnCodePKMap().get(code);
						rowNCMap.put(rowNo + ":pk_psndoc", pk_psndoc);
					} else {
						throw new BusinessException("找不到指定的员工 [" + code + "]");
					}
					// 员工考核信息专用通道--试用情况
					if (this.getNcEntityName().equals("hi_psndoc_trial")) {
						Collection<PsnJobVO> psnjobs = this.getBaseDAO().retrieveByClause(PsnJobVO.class,
								"pk_psndoc = '" + rowNCMap.get(rowNo + ":pk_psndoc") + "'");
						// UFLiteralDate regulardate = new
						// UFLiteralDate(rowNCMap.get(rowNo +
						// ":regulardate").toString());
						UFLiteralDate begindate = null;
						UFLiteralDate enddate = null;

						if (null == rowNCMap.get(rowNo + ":begindate")) {
							begindate = null;
						} else {
							begindate = new UFLiteralDate(rowNCMap.get(rowNo + ":begindate").toString());
						}
						if (null == rowNCMap.get(rowNo + ":enddate")) {
							enddate = null;
						} else {
							enddate = new UFLiteralDate(rowNCMap.get(rowNo + ":enddate").toString());
						}
						for (PsnJobVO psnjob : psnjobs) {
							// 试用的时间在开始时间和结束时间之间
							if (null != begindate
									&& ((null != psnjob.getEnddate() && (begindate.isSameDate(psnjob.getBegindate()) || begindate
											.after(psnjob.getBegindate())
											&& (enddate.isSameDate(psnjob.getEnddate()) || enddate.before(psnjob
													.getEnddate())))) || (null == psnjob.getEnddate() && begindate
											.after(psnjob.getBegindate())))) {
								rowNCMap.put(rowNo + ":PK_PSNORG", psnjob.getPk_psnorg());
								rowNCMap.put(rowNo + ":PK_PSNJOB", psnjob.getPk_psnjob());
							}
						}
						rowNCMap.put(rowNo + ":PK_ORG", this.getPk_org());
						rowNCMap.put(rowNo + ":ASSGID", 1);
						rowNCMap.put(rowNo + ":TRIAL_TYPE", 1);
						rowNCMap.put(rowNo + ":pk_group", this.getPk_group());
						if (StringUtils.isEmpty((String) rowNCMap.get(rowNo + ":pk_psndoc"))) {
							throw new BusinessException("找不到指定的员工 [" + code + "]");
						}
						if (StringUtils.isEmpty((String) rowNCMap.get(rowNo + ":PK_PSNJOB"))) {
							getErrorMessages().put(rowNo, "找不到员工 [" + code + "]在此期间的工作记录");
						}
					}
					rowNCMap.put(rowNo + ":RECORDNUM", (recordnum));
					rowNCMap.put(rowNo + ":lastflag", "Y");
					recordnum = recordnum - 1;

				}
			} catch (Exception e) {
				this.getErrorMessages().put(rowNo, e.getMessage());
			}
		}
	}

	@Override
	public void afterUpdate() throws BusinessException {
		// 查询历史数据--select vo_class_name from hr_infoset where infoset_code =
		// 'hi_psndoc_ass';
		// String sql =
		// "select vo_class_name from hr_infoset where infoset_code = '"+this.getNcEntityName()+"'";
		String rowNo = null;
		try {
			for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
				rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];
				SQLParameter sqlParameter = new SQLParameter();
				if (this.getNcEntityName().startsWith("hi_psndoc_")) {
					sqlParameter.addParam(this.getNcEntityName());
					IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
					// 查询出VO类
					List<Map<String, String>> custlist = (List<Map<String, String>>) iUAPQueryBS.executeQuery(
							"select vo_class_name from hr_infoset where infoset_code = ?", sqlParameter,
							new MapListProcessor());
					for (Map map : custlist) {
						Class<?> cl = Class.forName(map.get("vo_class_name").toString());
						// 所有数据
						List hisvos = (List) this.getBaseDAO().retrieveByClause(cl,
								"pk_psndoc='" + rowNCMap.get(rowNo + ":pk_psndoc") + "'and dr =0 order by RECORDNUM");
						SuperVO[] clazz = new SuperVO[hisvos.size()];
						// 更新字段
						updateRecordnumAndLastflag((SuperVO[]) hisvos.toArray(clazz));
					}
				}
			}
		} catch (Exception e) {
			this.getErrorMessages().put(rowNo, e.getMessage());
		}
	}

	@Override
	public void afterConvert() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeConvert() throws BusinessException {
		if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
			for (Map<String, Object> jsonobj : this.getJsonValueObjects()) {
				for (Entry<String, Object> entry : jsonobj.entrySet()) {
					jsonobj.put(entry.getKey(), ((String) entry.getValue()).trim());
					if (entry.getKey().contains("EMPNO")) {
						String code = (String) entry.getValue();
						String pk_psndoc = getPKByCode(code);
						this.getPsnCodePKMap().put("code", pk_psndoc);
					}
				}
			}
		}
	}

	private String getPKByCode(String code) throws BusinessException {
		String pk_psndoc = "";
		if (psnCodePKMap.containsKey(code)) {
			pk_psndoc = psnCodePKMap.get(code);
		} else {
			String strSQL = "select pk_psndoc from bd_psndoc where code = " + getStringValue(code);
			pk_psndoc = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
			psnCodePKMap.put(code, pk_psndoc);
		}
		return code;
	}

	@Override
	public void beforeQuery() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void afterQuery() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeInsertOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void doUpdateByBP() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	public void updateRecordnumAndLastflag(SuperVO[] vos) throws BusinessException {
		if ((vos == null) || (vos.length == 0)) {
			return;
		}
		for (int i = 0; i < vos.length; i++) {
			vos[i].setAttributeValue("recordnum", i);
			if (i == 0) {
				vos[i].setAttributeValue("lastflag", UFBoolean.TRUE);
			} else {
				vos[i].setAttributeValue("lastflag", UFBoolean.FALSE);
			}
		}

		((IPersistenceUpdate) NCLocator.getInstance().lookup(IPersistenceUpdate.class)).updateVOArray(null, vos,
				new String[] { "recordnum", "lastflag" }, null);
	}
}
