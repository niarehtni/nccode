package nc.ui.hi.psndoc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pub.tools.HiCacheUtils;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hi.pub.IQueryMode;
import nc.ui.hr.comp.combinesort.Attribute;
import nc.ui.querytemplate.meta.FilterMeta;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.RowOperationInfo;
import nc.vo.bd.meta.IBDObject;
import nc.vo.hi.psndoc.KeyPsnGrpVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.enumeration.PsnType;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.querytemplate.md.MDFilterMeta;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PsndocModel extends BillManageModel implements IQueryMode {
	private int queryMode = 1;

	private Boolean blContractStarted = null;

	private boolean blIncludeCancleDept = false;

	private boolean blIncludeChildDepts = true;

	private boolean blIncludeChildOrgs = false;

	private boolean blIncludeChildPsncl = false;

	private boolean blSortEdit = false;

	private boolean blSubVisible = false;

	private boolean blShowAllInfo = false;

	private boolean blShowHisKeyPsn = false;

	private boolean blShowHisGroup = false;

	private boolean blShowOnJobPsn = true;

	private SuperVO currTypeOrgVO;

	private HashSet<String> hashBusinessInfoSet = PsndocAggVO.hashBusinessInfoSet;

	private Map<String, String> hashPara = new Hashtable();

	private HashSet<String> hashSubHaveLoaded = new HashSet();

	private HashMap<String, String> hashUsedTablesOfQuery = new HashMap();

	private FromWhereSQL querySQL;

	private String strCurrentPk_org = "";

	private String strCurrentPkPsndoc = "";

	private String strInJobType = "hire";

	private String strLastWhereSqlQueryDialog = "1=1";

	private String strNodeCode = "60070register";

	private String strPk_psncl;

	private String strPsndocClerkCode;

	private String strPsndocCode;

	private String[] strUniqueFields = null;

	private Vector<Attribute> vectSortFields;

	private String[] hiddenKeys;

	private String resourceCode;

	public boolean canEdit() {
		if (null == getContext().getPk_org()) {
			return false;
		}
		return true;
	}

	public HashSet<String> getBusinessInfoSet() {
		// ssx added on 2017-07-12 for NHI sub infoset addons
		try {
			if (!hashBusinessInfoSet.contains(PsndocDefTableUtil
					.getPsnNHIDetailTablename())
					&& PsndocDefTableUtil.getPsnNHIDetailTablename() != null) {
				hashBusinessInfoSet.add(PsndocDefTableUtil
						.getPsnNHIDetailTablename());
			}

			if (!hashBusinessInfoSet.contains(PsndocDefTableUtil
					.getPsnNHISumTablename())
					&& PsndocDefTableUtil.getPsnNHISumTablename() != null) {
				hashBusinessInfoSet.add(PsndocDefTableUtil
						.getPsnNHISumTablename());
			}

			if (!hashBusinessInfoSet.contains(PsndocDefTableUtil
					.getPsnHealthInsExTablename())
					&& PsndocDefTableUtil.getPsnHealthInsExTablename() != null) {
				hashBusinessInfoSet.add(PsndocDefTableUtil
						.getPsnHealthInsExTablename());
			}

			if (!hashBusinessInfoSet.contains("hi_psndoc_groupinsrecord")) {
				hashBusinessInfoSet.add("hi_psndoc_groupinsrecord");
			}
		} catch (BusinessException e) {
			// TODO 自動產生的 catch 區塊
			e.printStackTrace();
		}
		return hashBusinessInfoSet;
	}

	public Boolean getContractStarted() {
		if ("60070register".equals(getContext().getNodeCode())) {

			return Boolean.valueOf(false);
		}

		if (blContractStarted == null) {
			blContractStarted = Boolean.valueOf(HiCacheUtils
					.isModuleStarted("6011"));
		}
		return blContractStarted;
	}

	public String getCurrentPk_org() {
		return strCurrentPk_org;
	}

	public String getCurrentPkPsndoc() {
		return strCurrentPkPsndoc;
	}

	public SuperVO getCurrTypeOrgVO() {
		return currTypeOrgVO;
	}

	public UFBoolean getIndocFlag() {
		return UFBoolean.TRUE;
	}

	public String getInJobType() {
		return strInJobType;
	}

	public String getLastWhereSqlQueryDialog() {
		return strLastWhereSqlQueryDialog;
	}

	public String getNodeCode() {
		return strNodeCode;
	}

	public int getPaginationSize() {
		try {
			Integer para = SysInitQuery.getParaInt(getContext().getPk_org(),
					"HI0003");
			if (para == null) {
				para = Integer.valueOf(200);
			}
			return para.intValue();
		} catch (Exception ex) {
		}

		return 200;
	}

	public String getPk_psncl() {
		return strPk_psncl;
	}

	public String getPsndocClerkCode() {
		return strPsndocClerkCode;
	}

	public BillCodeContext getPsndocClerkCodeContext() {
		try {
			return HiCacheUtils.getBillCodeContext("6007psndoc_clerkcode",
					getContext().getPk_group(), getContext().getPk_org());

		} catch (BusinessException ex) {
			Debug.error(ex.getMessage(), ex);
		}
		return null;
	}

	public String getPsndocCode() {
		return strPsndocCode;
	}

	public BillCodeContext getPsndocCodeContext() {
		try {
			return HiCacheUtils.getBillCodeContext("6007psndoc_code",
					getContext().getPk_group(), getContext().getPk_org());
		} catch (BusinessException ex) {
			Debug.error(ex.getMessage(), ex);
		}
		return null;
	}

	public int getPsnType() {
		return ((Integer) PsnType.EMPLOYEE.value()).intValue();
	}

	public FromWhereSQL getQuerySQL() {
		return querySQL;
	}

	public Vector<Attribute> getSortFields() {
		return vectSortFields;
	}

	public HashSet<String> getSubHaveLoaded() {
		if (hashSubHaveLoaded == null) {
			hashSubHaveLoaded = new HashSet();
		}
		return hashSubHaveLoaded;
	}

	public int getSwitchToDocMode() {
		try {
			Integer para = SysInitQuery.getParaInt(getContext().getPk_org(),
					"HI0002");
			if (para == null) {
				para = Integer.valueOf(0);
			}
			return para.intValue();
		} catch (Exception ex) {
		}

		return 0;
	}

	public String[] getUniqueFields() {
		if (strUniqueFields != null) {
			return strUniqueFields;
		}
		IPsndocQryService queryService = (IPsndocQryService) NCLocator
				.getInstance().lookup(IPsndocQryService.class);
		try {
			strUniqueFields = queryService.getPsndocUniqueRule();
		} catch (BusinessException ex) {
			throw new BusinessRuntimeException(ex.getMessage(), ex);
		}

		if (ArrayUtils.contains(strUniqueFields, "idtype")) {
			strUniqueFields = ((String[]) ArrayUtils.removeElement(
					strUniqueFields, "idtype"));
		}

		if (ArrayUtils.contains(strUniqueFields, "id")) {
			strUniqueFields = ((String[]) ArrayUtils.removeElement(
					strUniqueFields, "id"));
		}

		if (ArrayUtils.contains(strUniqueFields, "name")) {
			strUniqueFields = ((String[]) ArrayUtils.removeElement(
					strUniqueFields, "name"));
		}

		String[] defaultfieldcodes = { "name", "idtype", "id" };

		strUniqueFields = ((String[]) ArrayUtils.addAll(defaultfieldcodes,
				strUniqueFields));

		return strUniqueFields;
	}

	public HashMap<String, String> getUsedTablesOfQuery() {
		return hashUsedTablesOfQuery;
	}

	public boolean isIncludeCancleDept() {
		return blIncludeCancleDept;
	}

	public boolean isIncludeChildDepts() {
		return blIncludeChildDepts;
	}

	public boolean isIncludeChildOrgs() {
		return blIncludeChildOrgs;
	}

	public boolean isSortEdit() {
		return blSortEdit;
	}

	public boolean isSubVisible() {
		return blSubVisible;
	}

	public void resetPara() {
		hashPara.clear();
	}

	public void setBusinessInfoSet(HashSet<String> businessInfoSet) {
		hashBusinessInfoSet = businessInfoSet;
	}

	public void setContractStarted(Boolean contractStarted) {
		blContractStarted = contractStarted;
	}

	public void setCurrentPk_org(String currentPkOrg) {
		strCurrentPk_org = currentPkOrg;
	}

	public void setCurrentPkPsndoc(String currentPkPsndoc) {
		strCurrentPkPsndoc = currentPkPsndoc;
	}

	public void setCurrTypeOrgVO(SuperVO currTypeOrgVO) {
		this.currTypeOrgVO = currTypeOrgVO;
	}

	public void setIncludeCancleDept(boolean includeCancleDept) {
		blIncludeCancleDept = includeCancleDept;
	}

	public void setIncludeChildDepts(boolean includeChildDepts) {
		blIncludeChildDepts = includeChildDepts;
	}

	public void setIncludeChildOrgs(boolean includeChildPsn) {
		blIncludeChildOrgs = includeChildPsn;
	}

	public void setInJobType(String inJobType) {
		strInJobType = inJobType;
	}

	public void setLastWhereSqlQueryDialog(String lastWhereSqlQueryDialog) {
		strLastWhereSqlQueryDialog = lastWhereSqlQueryDialog;
	}

	public void setNodeCode(String nodecode) {
		strNodeCode = nodecode;
	}

	public void setPk_psncl(String pk_psncl) {
		strPk_psncl = pk_psncl;
	}

	public void setPsndocClerkCode(String psndocClerkCode) {
		strPsndocClerkCode = psndocClerkCode;
	}

	public void setPsndocCode(String billCode) {
		strPsndocCode = billCode;
	}

	public void setQuerySQL(FromWhereSQL querySQL) {
		this.querySQL = querySQL;
	}

	public void setSortEdit(boolean sortEdit) {
		blSortEdit = sortEdit;
	}

	public void setSortFields(Vector<Attribute> sortFields) {
		vectSortFields = sortFields;
	}

	public void setSubHaveLoaded(HashSet<String> subHaveLoaded) {
		hashSubHaveLoaded = subHaveLoaded;
	}

	public void setSubVisible(boolean subVisible) {
		blSubVisible = subVisible;
	}

	public void setUniqueFields(String[] uniqueFields) {
		strUniqueFields = uniqueFields;
	}

	public void setUsedTablesOfQuery(HashSet<String> usedTablesOfQuery,
			Collection<FilterMeta> collUsedFilterMetas) {
		if (hashUsedTablesOfQuery == null) {
			hashUsedTablesOfQuery = new HashMap();
		}
		hashUsedTablesOfQuery.clear();
		if ((usedTablesOfQuery == null) || (collUsedFilterMetas == null)) {
			return;
		}
		for (Iterator iterator = usedTablesOfQuery.iterator(); iterator
				.hasNext();) {
			String strTableName = (String) iterator.next();
			for (FilterMeta filterMeta : collUsedFilterMetas) {
				if ((filterMeta instanceof MDFilterMeta)) {
					MDFilterMeta mdFilterMeta = (MDFilterMeta) filterMeta;
					if (mdFilterMeta.getFieldCode().toLowerCase()
							.contains(strTableName.toLowerCase() + ".")) {
						hashUsedTablesOfQuery.put(
								replaceTableName(strTableName),
								mdFilterMeta.getTableAlias());
						break;
					}
				}
			}
		}
		String strTableName;
	}

	public void setBlShowAllInfo(boolean blShowAllInfo) {
		this.blShowAllInfo = blShowAllInfo;
	}

	public boolean isBlShowAllInfo() {
		return blShowAllInfo;
	}

	public boolean isBlIncludeChildPsncl() {
		return blIncludeChildPsncl;
	}

	public void setBlIncludeChildPsncl(boolean blIncludeChildPsncl) {
		this.blIncludeChildPsncl = blIncludeChildPsncl;
	}

	public void setHiddenKeys(String[] hiddenKeys) {
		this.hiddenKeys = hiddenKeys;
	}

	public String[] getHiddenKeys() throws BusinessException {
		if (hiddenKeys == null) {
			hiddenKeys = ((IPsndocQryService) NCLocator.getInstance().lookup(
					IPsndocQryService.class)).queryHiddenKeys(getContext());
		}
		return hiddenKeys;
	}

	public int findBusinessData(Object obj) {
		int i = 0;

		String tmpId = "";
		if ((obj instanceof PsndocAggVO)) {
			tmpId = ((PsndocAggVO) obj).getParentVO().getUnionPk();
		} else if ((obj instanceof PsndocVO)) {
			tmpId = ((PsndocVO) obj).getUnionPk();
		} else {
			Logger.error("obj类型错误,不为PsndocAggVO或PsndocVO");
			return -1;
		}

		for (Object object : getData()) {
			String targetId = "";
			if ((object instanceof PsndocAggVO)) {
				targetId = ((PsndocAggVO) object).getParentVO().getUnionPk();
			} else if ((object instanceof PsndocVO)) {
				targetId = ((PsndocVO) object).getUnionPk();
			} else {
				Logger.error("object类型错误,不为PsndocAggVO或PsndocVO");
				return -1;
			}

			if (tmpId.equals(targetId)) {
				return i;
			}

			i++;
		}

		return -1;
	}

	public void deleteData(boolean isListShow) throws Exception {
		if ((isListShow) && (getSelectedOperaRows() != null)
				&& (getSelectedOperaRows().length > 0)) {
			deleteMultiRows();
		} else {
			deleteSeletedRow();
		}
	}

	protected void dbDeleteMultiRows(Object... deletedObjects) throws Exception {
		getService().delete(deletedObjects);
	}

	public int getMode() {
		return queryMode;
	}

	public void setMode(int mode) {
		queryMode = mode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	public void setShowHisKeyPsn(boolean showHisKeyPsn) {
		blShowHisKeyPsn = showHisKeyPsn;
	}

	public boolean isShowHisKeyPsn() {
		return blShowHisKeyPsn;
	}

	public void saveKeyPsn(String[] refPKs) throws BusinessException {
		if ((refPKs == null) || (refPKs.length == 0)) {
			return;
		}
		PsndocAggVO[] agg = ((IPsndocService) NCLocator.getInstance().lookup(
				IPsndocService.class)).saveKeyPsn(refPKs,
				(KeyPsnGrpVO) getCurrTypeOrgVO());
		multiDirectlyAdd(agg);
	}

	protected void multiDirectlyAdd(PsndocAggVO[] aggVOs) {
		if ((aggVOs == null) || (aggVOs.length == 0)) {
			return;
		}
		List data = getData();
		List<Integer> indexes = new ArrayList();
		for (int i = 0; (aggVOs != null) && (i < aggVOs.length); i++) {
			data.add(aggVOs[i]);

			if (getBusinessObjectAdapterFactory() != null) {
				IBDObject bdObj = getBusinessObjectAdapterFactory()
						.createBDObject(aggVOs[i]);
				if (bdObj != null) {
					datapks.add((String) bdObj.getId());
				} else
					datapks.add(null);
			}
			indexes.add(Integer.valueOf(data.size() - 1));
		}
		fireEvent(new AppEvent("Data_Inserted", this, new RowOperationInfo(
				indexes, aggVOs)));
		setSelectedRow(data.size() - 1);
	}

	public void saveKeyPsn(String wherePart) throws BusinessException {
		if (StringUtils.isBlank(wherePart)) {
			return;
		}
		PsndocAggVO[] agg = ((IPsndocService) NCLocator.getInstance().lookup(
				IPsndocService.class)).saveKeyPsnByCondition(wherePart,
				(KeyPsnGrpVO) getCurrTypeOrgVO());

		multiDirectlyAdd(agg);
	}

	public void deleteKeyPsn() throws Exception {
		Object[] objs = getSelectedOperaDatas();
		ArrayList<String> pks = new ArrayList();
		ArrayList<PsndocVO> vos = new ArrayList();
		for (int i = 0; (objs != null) && (i < objs.length); i++) {
			String pk_psnorg = ((PsndocAggVO) objs[i]).getParentVO()
					.getPsnOrgVO().getPk_psnorg();
			PsndocVO vo = ((PsndocAggVO) objs[i]).getParentVO();
			if (!pks.contains(pk_psnorg)) {
				pks.add(pk_psnorg);
			}
			if (!vos.contains(vo)) {
				vos.add(vo);
			}
		}
		if (pks.size() == 0) {
			return;
		}

		KeyPsnGrpVO grp = (KeyPsnGrpVO) getCurrTypeOrgVO();
		((IPsndocService) NCLocator.getInstance().lookup(IPsndocService.class))
				.deleteKeyPsn((PsndocVO[]) vos.toArray(new PsndocVO[0]),
						(String[]) pks.toArray(new String[0]),
						grp.getPk_keypsn_group());

		directlyDelete(objs);
	}

	public void setShowHisGroup(boolean showHisGroup) {
		blShowHisGroup = showHisGroup;
	}

	public boolean isShowHisGroup() {
		return blShowHisGroup;
	}

	public String[] queryNotNullSubset(PsndocAggVO psndocAggVO,
			HashMap<String, String> showTab) throws BusinessException {
		return ((IPsndocQryService) NCLocator.getInstance().lookup(
				IPsndocQryService.class)).queryNotNullSubset(psndocAggVO,
				showTab);
	}

	public String[] validateSubNotNull(PsndocAggVO psndocAggVO,
			HashMap<String, String> showTab) throws BusinessException {
		return ((IPsndocQryService) NCLocator.getInstance().lookup(
				IPsndocQryService.class)).validateSubNotNull(psndocAggVO,
				showTab);
	}

	public boolean isBlShowOnJobPsn() {
		return blShowOnJobPsn;
	}

	public void setBlShowOnJobPsn(boolean blShowOnJobPsn) {
		this.blShowOnJobPsn = blShowOnJobPsn;
	}

	private String replaceTableName(String tablename) {
		if ("hi_psndoc_parttime".equals(tablename)) {
			return "hi_psnjob";
		}

		return tablename;
	}

	public void naviStyleChanged(Object ncObject) {
		fireEvent(new AppEvent("event_navistyle_changed", this, ncObject));
	}
}
