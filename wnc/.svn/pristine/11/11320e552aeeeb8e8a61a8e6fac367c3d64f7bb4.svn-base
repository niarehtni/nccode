package nc.ui.hrpub.mdclass.refmodel;

import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.UserExit;
import nc.bs.logging.Logger;
import nc.itf.ncaam.industry.IAssetIndustryQueryService;
import nc.ui.bd.ref.AbstractRefGridTreeModel;
import nc.vo.bd.industry.IndustryVO;
import nc.vo.bd.ref.RefVO_mlang;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;

import org.apache.commons.lang.StringUtils;

public class MDClassAndEnumTreeModel extends AbstractRefGridTreeModel {
	private static final String COMP_ID_BASETYPES = "UAP-BASE-COMPONENT-ID";
	private static final String ENTIRYTYPEPID = "entirytype";
	private static final String DEFDOCTYPEPID = "defdoctype";
	private String[] filterPks = { "BS000010000100001030",
			"BS000010000100001037", "BS000010000100001038",
			"BS000010000100001040", "BS000010000100001051",
			"BS000010000100001053", "BS000010000100001054",
			"BS000010000100001055", "BS000010000100001056",
			"BS000010000100001058", "BS000010000100001059" };

	public MDClassAndEnumTreeModel() {
		reset();
	}

	public MDClassAndEnumTreeModel(String refNodeName) {
		reset();
	}

	public void reset() {
		setRootName(NCLangRes4VoTransl.getNCLangRes().getStrByID("10140udib",
				"010140udib0008"));

		String mdclassWherePart = " ((md_class.classtype > 0 and md_class.classtype < 100) or (md_class.classtype = 201  or md_class.classtype = 203) ) ";

		setClassFieldCode(new String[] { "code", "name", "pid", "id", "resid",
				"resmodule" });

		setClassJoinField("id");
		setClassTableName("(select name as code, displayname as name, parentmoduleid as pid, id, resid as resid, resmodule as resmodule from md_module where exists(select 1 from md_component where md_component.ownmodule = md_module.id and md_component.id in (select componentid from md_class where ( "
				+ mdclassWherePart
				+ " ))) union select name as code, "
				+ "displayname as name, ownmodule as pid, id, resid as resid, "
				+ "resmodule as resmodule from md_component inner join (select id as comid,max(versiontype) as comversiontype from md_component group by id  )com on md_component.id = com.comid and md_component.versiontype = com.comversiontype where id in "
				+ "(select componentid from md_class where ("
				+ mdclassWherePart
				+ "))"
				+ " union all "
				+ " select '"
				+ "entirytype"
				+ "' as code,'实体类型' as name,null as pid,'"
				+ "entirytype"
				+ "' as id,'010140uddb0063','10140uddb' from md_module where id ='uap' "
				+ " union all "
				+ " select '"
				+ "defdoctype"
				+ "' as code,'自定义档案类型' as name,null as pid,'"
				+ "defdoctype"
				+ "' as id,'010140uddb0064','10140uddb' from md_module where id ='uap' "
				+ " ) as component");

		setClassFatherField("pid");
		setClassDefaultFieldCount(2);
		setClassWherePart("1=1");
		setClassOrderPart("code");

		setFieldCode(new String[] { "md_class.name", "md_class.displayname" });
		setFieldName(new String[] {
				NCLangRes4VoTransl.getNCLangRes().getStrByID("10140udib",
						"010140udib0009"),
				NCLangRes4VoTransl.getNCLangRes().getStrByID("10140udib",
						"010140udib0010") });

		setHiddenFieldCode(new String[] { "md_class.id",
				"md_class.componentid", "md_class.resid",
				"md_component.resmodule" });

		setTableName("md_class inner join md_component on md_class.componentid = md_component.id");
		setPkFieldCode("md_class.id");
		setOrderPart("md_class.name");
		setDocJoinField("md_class.componentid");
		setRefCodeField("md_class.name");
		setRefNameField("md_class.displayname");
		setWherePart(addIndustryWhereCondition(mdclassWherePart));
		setCompositeTreeByClassValue(true);
		setDefaultFieldCount(2);
		setFilterPks(filterPks, 1);
		setClassMutilLangNameRef(false);
		setMutilLangNameRef(false);
		resetFieldName();
	}

	private String addIndustryWhereCondition(String mdclassWherePart) {
		return mdclassWherePart + getComponentPriorityCondition();
	}

	private String getComponentPriorityCondition() {
		String industry = "0";
		String hyPk = UserExit.getInstance().getHyCode();
		if ((hyPk != null) && (hasIndustry(hyPk))) {
			IndustryVO industryVO = null;
			try {
				industryVO = ((IAssetIndustryQueryService) NCLocator
						.getInstance().lookup(IAssetIndustryQueryService.class))
						.queryByPK(hyPk);

				industry = industryVO == null ? "0" : industryVO.getCode();
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
				throw new BusinessRuntimeException(e.getMessage(), e);
			}
		}
		return " and exists (select 1 from (select id,max(versiontype) versiontype from md_component where industry in ('"
				+ industry
				+ "','"
				+ "0"
				+ "') group by id ) t"
				+ " where t.id=md_class.componentid and t.versiontype=md_component.versiontype "
				+ " and md_class.versiontype = (select max(versiontype) from md_class cl where cl.id = md_class.id) )";
	}

	private boolean hasIndustry(String hyCode) {
		return !"0".equals(hyCode);
	}

	public Vector getClassData() {
		Vector<Vector> v = super.getClassData();
		resetMutilLangName(v);
		putBaseTypeToFirst(v);
		return v;
	}

	private void resetMutilLangName(Vector<Vector> v) {
		for (Vector vRecord : v) {
			int index = getClassFieldIndex("name");
			String name = (String) vRecord.get(index);
			String resid = (String) vRecord.get(getClassFieldIndex("resid"));
			String resmodule = (String) vRecord
					.get(getClassFieldIndex("resmodule"));

			if ((StringUtils.isNotBlank(resmodule))
					&& (StringUtils.isNotBlank(resid))) {
				name = NCLangRes4VoTransl.getNCLangRes().getString(resmodule,
						name, resid);
			}

			vRecord.setElementAt(name, index);
		}
	}

	private void putBaseTypeToFirst(Vector<Vector> v) {
		Vector basetypeComponent = null;
		if ((v != null) && (v.size() > 0)) {
			for (Vector rowVector : v) {
				if ("UAP-BASE-COMPONENT-ID".equals(rowVector.get(3))) {
					basetypeComponent = rowVector;
					break;
				}
			}
			basetypeComponent.set(2, null);
			v.remove(basetypeComponent);
			v.add(0, basetypeComponent);
			int count = v.size();
			for (int i = 1; i < count; i++) {
				Vector vi = (Vector) v.get(i);
				String code = (String) vi.get(0);
				if ((!"defdoctype".equals(code))
						&& (!"entirytype".equals(code))) {

					String id = (String) vi.get(3);
					if ((code != null) && (code.startsWith("Defdoc"))) {
						vi.set(2, "defdoctype");

					} else if ((code != null) && (code.equalsIgnoreCase(id))) {
						vi.set(2, "entirytype");
					}
				}
			}
		}
	}

	protected RefVO_mlang[] getRefVO_mlang() {
		RefVO_mlang mlang = new RefVO_mlang();
		mlang.setDirFieldName("md_component.resmodule");
		mlang.setFieldName("md_class.displayname");
		mlang.setResIDFieldNames(new String[] { "md_class.resid" });
		return new RefVO_mlang[] { mlang };
	}
}
