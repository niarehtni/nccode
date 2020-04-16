package nc.ui.ta.psndoc.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import nc.pubitf.para.SysInitQuery;
import nc.ui.dbcache.DBCacheFacade;
import nc.ui.hr.comp.combinesort.Attribute;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.ta.region.action.RegionUtils;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.querytemplate.md.MDFilterMeta;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.AllParams;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.MapUtils;

public class TbmPsndocAppModel extends BillManageModel {
	private Boolean workplaceflag = false;
	private String psnSql;
	private Map<String, String> hashPara = new Hashtable<String, String>(); // 参数
	private HashMap<String, String> hashUsedTablesOfQuery = new HashMap<String, String>(); // 查询窗口所返回的关联表
	private Vector<Attribute> vectSortFields; // 排序字段
	private String resourceCode;
	private UFBoolean useMobile = null; // 是否启用移动考勤 heqiaoa

	public Boolean isShowWorkPlace() {
		AllParams params = ((TALoginContext) getContext()).getAllParams();
		if (params == null)
			return false;
		TimeRuleVO timeruleVO = params.getTimeRuleVO();
		if (timeruleVO == null || timeruleVO.getWorkplaceflag() == null
				|| !timeruleVO.getWorkplaceflag().booleanValue())
			return false;
		workplaceflag = timeruleVO == null ? false : timeruleVO.getWorkplaceflag() == null ? false : (timeruleVO
				.getWorkplaceflag().booleanValue());
		return workplaceflag;
	}

	/**
	 * 数据库真实数据删除，如想转换成一次删除，需重写该方法。
	 * 
	 * @param deletedObjects
	 * @throws Exception
	 */
	@Override
	protected void dbDeleteMultiRows(Object... deletedObjects) throws Exception {
		((TbmPsndocAppModelService) getService()).delete(deletedObjects);
	}

	public void setUsedTablesOfQuery(HashSet<String> usedTablesOfQuery, Collection<FilterMeta> collUsedFilterMetas) {

		if (hashUsedTablesOfQuery == null) {
			hashUsedTablesOfQuery = new HashMap<String, String>();
		}
		hashUsedTablesOfQuery.clear();
		if (usedTablesOfQuery == null || collUsedFilterMetas == null) {
			return;
		}
		for (String strTableName : usedTablesOfQuery) {
			for (FilterMeta filterMeta : collUsedFilterMetas) {
				if (filterMeta instanceof MDFilterMeta) {
					MDFilterMeta mdFilterMeta = (MDFilterMeta) filterMeta;
					if (mdFilterMeta.getFieldCode().toLowerCase().contains(strTableName.toLowerCase() + ".")) {
						hashUsedTablesOfQuery.put(strTableName, mdFilterMeta.getTableAlias());
						break;
					}
				}
			}
		}
	}

	public HashMap<String, String> getUsedTablesOfQuery() {

		return hashUsedTablesOfQuery;
	}

	public int getPaginationSize() {

		Integer para = 0;
		try {
			para = SysInitQuery.getParaInt(getContext().getPk_org(), ICommonConst.ROW_PER_PAGE);// 获取业务参数设置
			if (para == null)
				para = 200;
		} catch (BusinessException e) {
			return 200;
		}
		return para;
	}

	public Map<String, String> getPara() {

		if (MapUtils.isEmpty(hashPara) && getContext().getPk_org() != null) {
			try {
				hashPara = SysInitQuery.queryBatchParaValues(getContext().getPk_org(),
						new String[] { ICommonConst.ROW_PER_PAGE });
			} catch (BusinessException ex) {
				throw new BusinessRuntimeException(ex.getMessage(), ex);
			}
		}
		return hashPara;
	}

	@Override
	public Object getSelectedData() {
		if (getSelectedRow() == -1)
			return null;
		else if (getData() == null || getData().size() == 0) {
			return null;
		} else if (getSelectedRow() >= getData().size()) { // 解决删除前几行，但选中最后行时报越界异常的错误
			return null;
		} else {
			return getData().get(getSelectedRow());
		}
	}

	/**
	 * 新增时需要用户决定是否更新工作日历
	 * 
	 * @param object
	 * @param ret
	 *            :
	 * @return
	 * @throws Exception
	 */
	public Object insert(Object object, boolean isUpdatePsnCalendar, boolean isCreateTeamMember) throws Exception {
		Object obj = ((TbmPsndocAppModelService) getService()).insert(object, isUpdatePsnCalendar, isCreateTeamMember);
		directlyAdd(obj);
		// 在卡片预览界面新增后返回，会导致鼠标checkbox焦点一致在卡片界面对应的列表。故在此清除一下
		clearSelectedOperaRows();
		return obj;
	}

	public String getPsnSql() {
		return psnSql;
	}

	public void setPsnSql(String psnSql) {
		this.psnSql = psnSql;
	}

	public Vector<Attribute> getSortFields() {

		return vectSortFields;
	}

	public void setSortFields(Vector<Attribute> sortFields) {

		vectSortFields = sortFields;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}

	/**
	 * 判断是否启用移动考勤
	 * 
	 * @return
	 */
	public UFBoolean getUseMobile() {
		if (null == useMobile) {
			// 查询考勤规则VO
			TimeRuleVO timeVO = RegionUtils.getUseMobile(getContext().getPk_org());
			boolean enable = null != timeVO && null != timeVO.getUsemobile() && timeVO.getUsemobile().booleanValue();
			useMobile = UFBoolean.valueOf(enable);
		}
		return useMobile;
	}

	public void setUseMobile(UFBoolean useMobile) {
		this.useMobile = useMobile;
	}

	@Override
	public void directlyUpdate(Object object) {
		super.directlyUpdate(object);
		refreshCacheTable();
	}

	/**
	 * 更新缓存表
	 */
	private void refreshCacheTable() {
		new DBCacheFacade().refreshTable(TBMPsndocVO.getDefaultTableName());
	}
}
