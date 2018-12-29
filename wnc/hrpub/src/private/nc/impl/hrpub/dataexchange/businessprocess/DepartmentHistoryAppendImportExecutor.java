package nc.impl.hrpub.dataexchange.businessprocess;

import java.util.ArrayList;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.om.IDeptAdjustService;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.DeptChangeType;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class DepartmentHistoryAppendImportExecutor extends DepartmentHistoryImportExecutor {
	public DepartmentHistoryAppendImportExecutor() throws BusinessException {
		super();
	}

	@Override
	public void beforeUpdate() throws BusinessException {
		// 增加额外的唯一性检查条件，基類在插入數據之前，增加該條件的檢查，以確保業務主鍵不重複
		this.setUniqueCheckExtraCondition("effectdate='$effectdate$' and changetype != 1");
		// 增加排他的唯一性检查条件，基類在插入數據之前，用此條件替換檢查條件，不再以基類的按Code轉PK加額外條件檢查
		this.setUniqueCheckExclusiveCondition(" code='$code$' and " + this.getUniqueCheckExtraCondition());

		if (this.getNcValueObjects() != null && this.getNcValueObjects().size() > 0) {
			IDeptAdjustService deptSvc = NCLocator.getInstance().lookup(IDeptAdjustService.class);

			String rowNo = "";
			for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
				try {
					rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];

					// 变化历史部门Code
					String oldDeptCode = (String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("ODEPNO"));

					String pk_dept = getDeptByCode(oldDeptCode, new ArrayList<String>(), new ArrayList<String>());

					// 本次变更类型
					String changeType = getChangeType(rowNCMap, rowNo);

					if (DeptChangeType.HRUNCANCELED.equals(changeType) || DeptChangeType.RENAME.equals(changeType)
							|| DeptChangeType.CHANGEPRINCIPAL.equals(changeType)) {
						// 處理取消撤消
						AggHRDeptVO aggvo = new AggHRDeptVO();
						HRDeptVO hrdeptvo = (HRDeptVO) this.getBaseDAO().retrieveByPK(HRDeptVO.class, pk_dept);
						String canceldate = (String) rowNCMap.get(rowNo + ":"
								+ this.getReservedPropertyName("CANCELED"));
						if (canceldate == null || StringUtils.isEmpty(canceldate)) {
							hrdeptvo.setHrcanceled(UFBoolean.FALSE);
						}

						// 处理更名
						hrdeptvo.setCode((String) rowNCMap.get(rowNo + ":code"));
						hrdeptvo.setName(getStringValue(rowNCMap.get(rowNo + ":"
								+ this.getReservedPropertyName("ODEPNM"))));
						hrdeptvo.setName2(hrdeptvo.getName());
						hrdeptvo.setName3(((String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("NEDEPNM")))
								.trim());

						// 处理負責人變更
						hrdeptvo.setPrincipal(getPrincipalIDByCode((String) rowNCMap.get(rowNo + ":"
								+ this.getReservedPropertyName("OEMPNO1"))));

						// 部門級別
						hrdeptvo.setDeptlevel(getDeptLevel(((String) rowNCMap.get(rowNo + ":"
								+ this.getReservedPropertyName("NEXP_LEVEL"))).trim()));

						aggvo.setParentVO(hrdeptvo);
						deptSvc.executeDeptVersion(aggvo,
								new UFLiteralDate(getDateString((String) rowNCMap.get(rowNo + ":effectdate"))));
					}

				} catch (Exception e) {
					this.getErrorMessages().put(rowNo, e.getMessage());
				}
			}
		}
	}

	@Override
	public void afterUpdate() throws BusinessException {
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

}
