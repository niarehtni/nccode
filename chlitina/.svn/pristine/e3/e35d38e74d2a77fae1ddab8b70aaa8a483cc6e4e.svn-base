package nc.impl.wa.psndocwadoc;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.hr.utils.InSQLCreator;
import nc.itf.hr.wa.IPsndocwadocLabourService;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

import org.springframework.util.CollectionUtils;

@SuppressWarnings({ "unchecked" })
public class PsndocwadocLabourImpl implements IPsndocwadocLabourService {

    @Override
    public PsnJobVO[] queryPsnJobVOsByConditionAndOverrideOrg(List<String> pk_psndocs, String pk_orgs, String[] pk_depts)
	    throws BusinessException {
	// String pk_hrorg = null;
	String sql = " select job.pk_psndoc,dept.pk_vid,job.pk_psnjob "
		+ " from (select pk_psndoc,max(begindate)begindate,pk_dept,pk_psnjob "
		+ " from hi_psnjob where enddate is null and endflag = 'N' ";
	
	if (!CollectionUtils.isEmpty(pk_psndocs)) {
	    InSQLCreator insql = new InSQLCreator();
	    String psndocsInSQL = insql.getInSQL(pk_psndocs.toArray(new String[0]));
	    sql += " and pk_psndoc in (" + psndocsInSQL + ")";
	}
	//查找此组织下的所有法人组织(要有薪资委托关系的组织才能显示)
	if(null != pk_orgs && !"".equals(pk_orgs)){
		List<String> orgs = LegalOrgUtilsEX.getRelationOrgWithSalary(pk_orgs);
		if(!orgs.isEmpty()){
			InSQLCreator insql = new InSQLCreator();
		    String orgInSQL = insql.getInSQL(orgs.toArray(new String[0]));
		    sql += " and pk_org in (" + orgInSQL + ") ";
		}
	}
	//部门多选修改
	if (null != pk_depts && pk_depts.length > 0) {
	    InSQLCreator insql = new InSQLCreator();
	    String deptInSQL = insql.getInSQL(pk_depts);
	    sql += " and pk_dept in (" + deptInSQL + ") ";
	}

	sql += " group by pk_psndoc,pk_dept,pk_psnjob) job "
		+ " left join org_dept_v dept on job.pk_dept = dept.pk_dept ";

	List<Object[]> psndocList = (List<Object[]>) new BaseDAO().executeQuery(sql, new ArrayListProcessor());
	if (CollectionUtils.isEmpty(psndocList))
	    return null;
	PsnJobVO[] returnVOs = new PsnJobVO[psndocList.size()];
	for (int i = 0; i < psndocList.size(); i++) {
	    if (null != psndocList.get(i)) {
		String pk_psndoc = psndocList.get(i)[0] == null ? "" : psndocList.get(i)[0].toString();
		String pk_vid = psndocList.get(i)[1] == null ? "" : psndocList.get(i)[1].toString();
		String pk_psnjob = psndocList.get(i)[2] == null ? "" : psndocList.get(i)[2].toString();
		returnVOs[i] = new PsnJobVO();
		returnVOs[i].setPk_psndoc(pk_psndoc);
		returnVOs[i].setPk_psnjob(pk_psnjob);
		returnVOs[i].setPk_dept_v(pk_vid);
		// returnVOs[i].setPk_org_v(psndocvos[i].getPk_org_v());
	    }
	}

	return returnVOs;
    }

    @Override
    public Map<String, String[]> queryLabour(List<String> pk_psndocs, String laborRange, String retireRange,
	    String healthRange) throws BusinessException {
    	Map<String, String[]> labourMap = new HashMap<String, String[]>();
	if (CollectionUtils.isEmpty(pk_psndocs)) {
	    return labourMap;
	}
	
	String nowDate = new UFLiteralDate(InvocationInfoProxy.getInstance().getBizDateTime()).toString();
	InSQLCreator insql = new InSQLCreator();
	String sql = " select pk_psndoc, sum(laborrange) laborrange, sum(retirerange) retirerange, sum(healthrange) healthrange from (";
	sql += " select pk_psndoc, isnull(glbdef4, 0) laborrange, 0 retirerange, 0 healthrange from "
		+ PsndocDefTableUtil.getPsnLaborTablename() + " where isnull(enddate, '9999-12-31') >= '" + nowDate
		+ "'";
	sql += " union ";
	sql += " select pk_psndoc, 0 laborrange, isnull(glbdef7, 0) retirerange, 0 healthrange from "
		+ PsndocDefTableUtil.getPsnLaborTablename() + " where isnull(glbdef15, '9999-12-31') >= '" + nowDate
		+ "' ";
	sql += " union ";
	sql += " select pk_psndoc, 0 laborrange, 0 retirerange, isnull(glbdef16, 0) healthrange from "
		+ PsndocDefTableUtil.getPsnHealthTablename() + " where isnull(enddate, '9999-12-31') >= '" + nowDate
		+ "' ";
	sql += " ) tmp ";
	sql += " where pk_psndoc in (" + insql.getInSQL(pk_psndocs.toArray(new String[0])) + ") ";
	sql += " group by pk_psndoc";
	List<Object[]> labourList = (List<Object[]>) new BaseDAO().executeQuery(sql, new ArrayListProcessor());
	if (CollectionUtils.isEmpty(labourList))
	    return labourMap;
	for (int i = 0; i < labourList.size(); i++) {
	    if (null != labourList.get(i)) {
		String pk_psndoc = labourList.get(i)[0] == null ? "" : labourList.get(i)[0].toString();
		String glbdef4 = labourList.get(i)[1] == null ? "" : labourList.get(i)[1].toString();
		String glbdef7 = labourList.get(i)[2] == null ? "" : labourList.get(i)[2].toString();
		String glbdef16 = labourList.get(i)[3] == null ? "" : labourList.get(i)[3].toString();
		labourMap.put(pk_psndoc, new String[] { glbdef4, glbdef7, glbdef16 });
	    }
	}
	return labourMap;
    }

    // MOD 张恒 根据code找到自定义档案参照的name值 2018/9/17
    @Override
    public String queryRefNameByCode(String code) throws BusinessException {
	// TODO Auto-generated method stub
	String sql = " select pk_defdoclist from bd_defdoclist where code = '" + code + "'";
	String pk_defdoclist = new BaseDAO().executeQuery(sql, new ColumnProcessor()) == null ? "" : new BaseDAO()
		.executeQuery(sql, new ColumnProcessor()).toString();
	return pk_defdoclist;
    }

}
