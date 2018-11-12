package nc.impl.twhr;

import java.util.Collection;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.twhr.groupinsurance.ace.rule.DataUniqueCheckRule;
import nc.bs.twhr.groupinsurance.rule.AutoRegisterOnDutyInRule;
import nc.impl.pub.ace.AceGroupinsurancePubServiceImpl;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pub.smart.BatchSaveAction;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.twhr.groupinsurance.GroupInsuranceSettingVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

import org.apache.commons.lang.StringUtils;

public class GroupinsuranceMaintainImpl extends AceGroupinsurancePubServiceImpl
		implements nc.itf.twhr.IGroupinsuranceMaintain {

	@Override
	public GroupInsuranceSettingVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybasedoc(queryScheme);
	}

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO)
			throws BusinessException {
		// 调用编码、名称唯一性校验规则
		new DataUniqueCheckRule().process(new BatchOperateVO[] { batchVO });
		// 調用自動加保不允許同一險種勾選多筆自動加保記錄
		// ssx added on 20170913 after requirement changed
		new AutoRegisterOnDutyInRule()
				.process(new BatchOperateVO[] { batchVO });
		BatchSaveAction<GroupInsuranceSettingVO> saveAction = new BatchSaveAction<GroupInsuranceSettingVO>();
		BatchOperateVO retData = saveAction.batchSave(batchVO);
		return retData;
	}

	@Override
	public GroupInsuranceSettingVO[] queryByCondition(String pk_org,
			Collection<String[]> grouppsnpair) throws BusinessException {
		String strCond = "";
		GroupInsuranceSettingVO[] rtn = null;
		if (!StringUtils.isEmpty(pk_org) && grouppsnpair.size() > 0) {
			for (String[] pair : grouppsnpair) {
				if (StringUtils.isEmpty(strCond)) {
					strCond = "(cgrpinsid = '" + pair[0]
							+ "' and cgrpinsrelid = '" + pair[1] + "')";
				} else {
					strCond = strCond + " or " + "(cgrpinsid = '" + pair[0]
							+ "' and cgrpinsrelid = '" + pair[1] + "')";
				}
			}

			BaseDAO dao = new BaseDAO();
			Collection rst = dao.retrieveByClause(
					GroupInsuranceSettingVO.class, "pk_org='" + pk_org
							+ "' and (" + strCond + ") and dr=0 ");
			rtn = (rst == null || rst.size() == 0) ? null
					: (GroupInsuranceSettingVO[]) rst
							.toArray(new GroupInsuranceSettingVO[0]);
		}

		return rtn;
	}

	@Override
	public boolean isExistsGroupInsuranceSettingRef(GroupInsuranceSettingVO vo)
			throws BusinessException {
		String strSQL = "select * from "
				+ PsndocDefTableUtil.getGroupInsuranceTablename()
				+ " where glbdef4 = '" + vo.getCgrpinsrelid()
				+ "' and glbdef5 = '" + vo.getCgrpinsid() + "' and dr=0";
		IRowSet rowSet = new DataAccessUtils().query(strSQL);

		if (rowSet.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isExistsApprovedWaClassByGroupInsurance(String cgroupinsid,
			String cgroupinspsntypeid) throws BusinessException {
		// TODO: 後續應加強已有審核方案時不能修改子集設定及團保費率表, 方案預估:
		// 1. 薪資項目: 增加"是否團保項目"欄位
		// 2. 薪資發放: 審核回寫薪資方案到團保方案審核表, 用於記錄含團保計算項目的方案被審核
		// 3. 團保費率表:
		// 3.1. 修改時檢查團保方案審核表, 如對應薪資方案已審核, 則不允許修改
		// 3.2. 修改後刪除已計算的團保計算結果, 清空當前期間含團保項目的薪資發放已計算未審核的已計算標記
		// 4. 員工團保子集: 修改時根據險種及投保關係人檢查團保方案審核表, 如對應薪資方案已審核, 則不允許修改
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public GroupInsuranceSettingVO[] queryOnDuty(String pk_org)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<GroupInsuranceSettingVO> sets = (List<GroupInsuranceSettingVO>) dao
				.retrieveByClause(
						GroupInsuranceSettingVO.class,
						"id in (select id from twhr_groupinsurancesetting st inner join bd_defdoc def on st.cgrpinsrelid = def.pk_defdoc inner join bd_defdoclist lst on lst.pk_defdoclist = def.pk_defdoclist where st.pk_org = '"
								+ pk_org
								+ "' and bautoreg = 'Y' and lst.code = 'TWHR010' )");
		return sets.toArray(new GroupInsuranceSettingVO[0]);
	}
}
