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
	private Map<String, String> hashPara = new Hashtable<String, String>(); // ����
	private HashMap<String, String> hashUsedTablesOfQuery = new HashMap<String, String>(); // ��ѯ���������صĹ�����
	private Vector<Attribute> vectSortFields; // �����ֶ�
	private String resourceCode;
	private UFBoolean useMobile = null; // �Ƿ������ƶ����� heqiaoa

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
	 * ���ݿ���ʵ����ɾ��������ת����һ��ɾ��������д�÷�����
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
			para = SysInitQuery.getParaInt(getContext().getPk_org(), ICommonConst.ROW_PER_PAGE);// ��ȡҵ���������
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
		} else if (getSelectedRow() >= getData().size()) { // ���ɾ��ǰ���У���ѡ�������ʱ��Խ���쳣�Ĵ���
			return null;
		} else {
			return getData().get(getSelectedRow());
		}
	}

	/**
	 * ����ʱ��Ҫ�û������Ƿ���¹�������
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
		// �ڿ�ƬԤ�����������󷵻أ��ᵼ�����checkbox����һ���ڿ�Ƭ�����Ӧ���б����ڴ����һ��
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
	 * �ж��Ƿ������ƶ�����
	 * 
	 * @return
	 */
	public UFBoolean getUseMobile() {
		if (null == useMobile) {
			// ��ѯ���ڹ���VO
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
	 * ���»����
	 */
	private void refreshCacheTable() {
		new DBCacheFacade().refreshTable(TBMPsndocVO.getDefaultTableName());
	}
}
