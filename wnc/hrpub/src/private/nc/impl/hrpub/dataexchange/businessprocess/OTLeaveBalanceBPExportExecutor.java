package nc.impl.hrpub.dataexchange.businessprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.NCLocator;
import nc.impl.hrpub.dataexchange.DataExportExecutor;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.leaveextrarest.ILeaveExtraRestService;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.org.GroupVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OTLeaveBalanceVO;
import nc.vo.vorg.OrgVersionVO;

import org.apache.commons.lang.StringUtils;

public class OTLeaveBalanceBPExportExecutor extends DataExportExecutor implements IDataExchangeExternalExecutor {

	public OTLeaveBalanceBPExportExecutor() throws BusinessException {
		super();
	}

	@Override
	public Object getBizEntityID() throws BusinessException {
		// 必須賦值，否則不會加載本類型
		// 實體ID為md_class的ID欄位

		// select * from md_class where name = 'otleavebalance'
		return "7e6ca204-f9ba-4630-9361-529279dd4e88";
	}

	@Override
	public void beforeQuery() throws BusinessException {
		// TODO 自動產生的方法 Stub

	}

	@Override
	public void afterQuery() throws BusinessException {
		// TODO 自動產生的方法 Stub

	}

	@Override
	public void doUpdateByBP() throws BusinessException {
		// TODO 自動產生的方法 Stub

	}

	@Override
	public void beforeInsertOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自動產生的方法 Stub

	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自動產生的方法 Stub

	}

	@Override
	public void beforeUpdate() throws BusinessException {
		// TODO 自動產生的方法 Stub

	}

	@Override
	public void afterUpdate() throws BusinessException {
		// TODO 自動產生的方法 Stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public void doQueryByBP() throws BusinessException {
		if (this.getBpQueryConditions() != null) {
			UFLiteralDate beginDate = null;
			UFLiteralDate endDate = null;
			String queryYear = "";
			List<String> pk_psndocs = null;
			String pk_leavetype = null;
			for (Entry<String, Object> cond : this.getBpQueryConditions().entrySet()) {
				if (cond.getKey().toUpperCase().equals("BEGINDATE")) {
					beginDate = new UFLiteralDate((String) cond.getValue());
				} else if (cond.getKey().toUpperCase().equals("ENDDATE")) {
					endDate = new UFLiteralDate((String) cond.getValue());
				} else if (cond.getKey().toUpperCase().equals("QUERYYEAR")) {
					queryYear = (String) cond.getValue();
				} else if (cond.getKey().toUpperCase().equals("PSNCODE")) {
					String psnCodes = (String) cond.getValue();
					pk_psndocs = (List<String>) this.getBaseDAO().executeQuery(
							"select pk_psndoc from bd_psndoc where code in ('"
									+ psnCodes.replace(" ", "").replace(",", "','") + "')", new ColumnListProcessor());
				} else if (cond.getKey().toUpperCase().equals("TYPE")) {
					if ("OVERTIME".equals(cond.getValue())) {
						pk_leavetype = SysInitQuery.getParaString(this.getPk_org(), "TWHRT08");
					} else if ("EXTRALEAVE".equals(cond.getValue())) {
						pk_leavetype = SysInitQuery.getParaString(this.getPk_org(), "TWHRT10");
					}
				}
			}

			ISegDetailService queryOTService = NCLocator.getInstance().lookup(ISegDetailService.class);
			ILeaveExtraRestService queryELService = NCLocator.getInstance().lookup(ILeaveExtraRestService.class);
			OTLeaveBalanceVO[] results = null;

			String initotleavetype = SysInitQuery.getParaString(this.getPk_org(), "TWHRT08");
			String initexleavetype = SysInitQuery.getParaString(this.getPk_org(), "TWHRT10");

			if (pk_leavetype.equals(initotleavetype)) {
				if (StringUtils.isEmpty(queryYear)) {
					// 加载加班單
					// 按日期范围查找加班分段明细
					// 日期范围限制的不是来源单据的日期，而是产生的假期最长可休日期在该区间内
					results = queryOTService.getOvertimeToRestHoursByType(this.getPk_org(), pk_psndocs == null ? null
							: pk_psndocs.toArray(new String[0]), null, null, beginDate, endDate, null);
				} else {
					results = queryOTService.getOvertimeToRestHoursByType(this.getPk_org(), pk_psndocs == null ? null
							: pk_psndocs.toArray(new String[0]), null, null, queryYear, pk_leavetype);
				}
			} else if (pk_leavetype.equals(initexleavetype)) {
				if (StringUtils.isEmpty(queryYear)) {
					results = queryELService.getLeaveExtHoursByType(this.getPk_org(), pk_psndocs == null ? null
							: pk_psndocs.toArray(new String[0]), null, null, beginDate, endDate, pk_leavetype, false,
							false);
				} else {
					results = queryELService.getLeaveExtHoursByType(this.getPk_org(), pk_psndocs == null ? null
							: pk_psndocs.toArray(new String[0]), null, null, queryYear, pk_leavetype, false);
				}
			}

			if (results != null && results.length > 0) {
				List<Map<String, Object>> queryResult = new ArrayList<Map<String, Object>>();

				for (OTLeaveBalanceVO result : results) {
					Map<String, Object> line = new HashMap<String, Object>();
					// pk_group
					GroupVO groupvo = (GroupVO) this.getBaseDAO().retrieveByPK(GroupVO.class, result.getPk_group(),
							new String[] { GroupVO.CODE });
					line.put("pk_group", groupvo.getCode());

					// pk_org
					OrgVO orgvo = (OrgVO) this.getBaseDAO().retrieveByPK(OrgVO.class, result.getPk_org(),
							new String[] { OrgVO.CODE });
					line.put("pk_org", orgvo.getCode());

					line.put("pk_otleavebalance", result.getPk_otleavebalance());

					// pk_psndoc
					PsndocVO psnvo = (PsndocVO) this.getBaseDAO().retrieveByPK(PsndocVO.class, result.getPk_psndoc(),
							new String[] { PsndocVO.CODE });
					line.put("pk_psndoc", psnvo.getCode());

					// pk_org_v
					if (result.getPk_org_v() != null) {
						OrgVersionVO orgvvo = (OrgVersionVO) this.getBaseDAO().retrieveByPK(OrgVersionVO.class,
								result.getPk_org_v(), new String[] { OrgVersionVO.CODE });
						line.put("pk_org_v", orgvvo.getCode());
					}

					line.put("totalhours", result.getTotalhours() == null ? 0.0 : result.getTotalhours().doubleValue());
					line.put("consumedhours", result.getConsumedhours() == null ? 0.0 : result.getConsumedhours()
							.doubleValue());
					line.put("remainhours", result.getRemainhours() == null ? 0.0 : result.getRemainhours()
							.doubleValue());
					line.put("frozenhours", result.getFrozenhours() == null ? 0.0 : result.getFrozenhours()
							.doubleValue());
					line.put("freehours", result.getFreehours() == null ? 0.0 : result.getFreehours().doubleValue());
					line.put("qstartdate", result.getQstartdate() == null ? "" : result.getQstartdate().toString());
					line.put("qenddate", result.getQenddate() == null ? "" : result.getQenddate().toString());
					line.put("settleddate", result.getSettleddate() == null ? "" : result.getSettleddate().toString());
					queryResult.add(line);
				}

				this.setResultRows(queryResult);
			}
		}
	}
}
