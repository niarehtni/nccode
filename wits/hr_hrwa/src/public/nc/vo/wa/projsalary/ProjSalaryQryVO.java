/**
 * @(#)ProjSalaryQryVO.java 1.0 2017Äê9ÔÂ15ÈÕ
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.vo.wa.projsalary;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.pub.WaLoginContext;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings("serial")
public class ProjSalaryQryVO extends SuperVO {
	private WaLoginContext waContext;
	private String psncode;
	private String psnname;
	private String psnname2;
	private String psnname3;
	private String pk_project;
	private String pk_classitem;
	private UFDate startDate;
	private UFDate endDate;

	public static final String QRYCONDITIONVO = "qry_condition_vo";

	public WaLoginContext getWaContext() {
		return waContext;
	}

	public void setWaContext(WaLoginContext waContext) {
		this.waContext = waContext;
	}

	public String getPsncode() {
		return psncode;
	}

	public void setPsncode(String psncode) {
		this.psncode = psncode;
	}

	public String getPsnname() {
		return psnname;
	}

	public void setPsnname(String psnname) {
		this.psnname = psnname;
	}

	public String getPsnname2() {
		return psnname2;
	}

	public void setPsnname2(String psnname2) {
		this.psnname2 = psnname2;
	}

	public String getPsnname3() {
		return psnname3;
	}

	public void setPsnname3(String psnname3) {
		this.psnname3 = psnname3;
	}

	public String getPk_project() {
		return pk_project;
	}

	public void setPk_project(String pk_project) {
		this.pk_project = pk_project;
	}

	public String getPk_classitem() {
		return pk_classitem;
	}

	public void setPk_classitem(String pk_classitem) {
		this.pk_classitem = pk_classitem;
	}

	public UFDate getStartDate() {
		return startDate;
	}

	public void setStartDate(UFDate startDate) {
		this.startDate = startDate;
	}

	public UFDate getEndDate() {
		return endDate;
	}

	public void setEndDate(UFDate endDate) {
		this.endDate = endDate;
	}

	public String toString() {
		StringBuilder desc = new StringBuilder();
		if (null != waContext) {
			if (StringUtils.isNotEmpty(waContext.getPk_org())) {
				desc.append("and pk_org='").append(waContext.getPk_org()).append("' ");
			}
			if (StringUtils.isNotEmpty(waContext.getClassPK())) {
				desc.append("and pk_wa_calss='").append(waContext.getClassPK()).append("' ");
			}
			if (StringUtils.isNotEmpty(waContext.getCyear()) || StringUtils.isNotEmpty(waContext.getCperiod())) {
				desc.append("and cperiod='").append(waContext.getCyear()).append(waContext.getCperiod()).append("' ");
			}
		}
		if (StringUtils.isNotEmpty(psncode) || StringUtils.isNotEmpty(psnname) || StringUtils.isNotEmpty(psnname2)
				|| StringUtils.isNotEmpty(psnname3)) {
			StringBuilder psnFilter = new StringBuilder("select pk_psndoc from bd_psndoc where 1=1 ");
			if (StringUtils.isNotEmpty(psncode)) {
				psnFilter.append("and code like '%").append(psncode).append("%' ");
			}
			if (StringUtils.isNotEmpty(psnname)) {
				psnFilter.append("and name like '%").append(psnname).append("%' ");
			}
			if (StringUtils.isNotEmpty(psnname2)) {
				psnFilter.append("and name2 like '%").append(psnname2).append("%' ");
			}
			if (StringUtils.isNotEmpty(psnname3)) {
				psnFilter.append("and name3 like '%").append(psnname3).append("%' ");
			}
			desc.append("and pk_psndoc in (").append(psnFilter).append(") ");
		}
		if (StringUtils.isNotEmpty(pk_project)) {
			desc.append("and def1='").append(pk_project).append("' ");
		}
		if (StringUtils.isNotEmpty(pk_classitem)) {
			desc.append("and pk_classitem='").append(pk_classitem).append("' ");
		}
		if (null != startDate || null != endDate) {
			StringBuilder dateStr = new StringBuilder();
			if (null != startDate) {
				dateStr.append("and creationtime >= '").append(startDate.toStdString()).append(" 00:00:00' ");
			}
			if (null != endDate) {
				dateStr.append("and creationtime <= '").append(endDate.toStdString()).append(" 23:59:59' ");
			}
			desc.append("and (").append(dateStr.substring(3)).append(") ");
		}
		if (desc.toString().trim().toLowerCase().startsWith("and")) {
			desc.delete(0, desc.toString().toLowerCase().indexOf("and") + 3);
		}
		return desc.insert(0, "(").append(")").toString();
	}

	public String getFrom() {
		return "wa_projsalary wa_projsalary left outer join bd_psndoc T1 ON T1.pk_psndoc = wa_projsalary.pk_psndoc";
	}

	public String getWhere() {
		StringBuilder where = new StringBuilder();
		if (null != waContext) {
			if (StringUtils.isNotEmpty(waContext.getPk_org())) {
				where.append("and wa_projsalary.pk_org='").append(waContext.getPk_org()).append("' ");
			}
			if (StringUtils.isNotEmpty(waContext.getClassPK())) {
				where.append("and wa_projsalary.pk_wa_calss='").append(waContext.getClassPK()).append("' ");
			}
			if (StringUtils.isNotEmpty(waContext.getCyear()) || StringUtils.isNotEmpty(waContext.getCperiod())) {
				where.append("and wa_projsalary.cperiod='").append(waContext.getCyear()).append(waContext.getCperiod())
						.append("' ");
			}
		}
		if (StringUtils.isNotEmpty(pk_project)) {
			where.append("and wa_projsalary.def1='").append(pk_project).append("' ");
		}
		if (StringUtils.isNotEmpty(pk_classitem)) {
			where.append("and wa_projsalary.pk_classitem='").append(pk_classitem).append("' ");
		}
		if (null != startDate || null != endDate) {
			StringBuilder dateStr = new StringBuilder();
			if (null != startDate) {
				dateStr.append("and wa_projsalary.creationtime >= '").append(startDate.toStdString())
						.append(" 00:00:00' ");
			}
			if (null != endDate) {
				dateStr.append("and wa_projsalary.creationtime <= '").append(endDate.toStdString())
						.append(" 23:59:59' ");
			}
			where.append("and (").append(dateStr.substring(3)).append(") ");
		}

		if (StringUtils.isNotEmpty(psncode)) {
			where.append("and T1.code like '%").append(psncode).append("%' ");
		}
		if (StringUtils.isNotEmpty(psnname)) {
			where.append("and T1.name like '%").append(psnname).append("%' ");
		}
		if (StringUtils.isNotEmpty(psnname2)) {
			where.append("and T1.name2 like '%").append(psnname2).append("%' ");
		}
		if (StringUtils.isNotEmpty(psnname3)) {
			where.append("and T1.name3 like '%").append(psnname3).append("%' ");
		}
		if (where.toString().trim().toLowerCase().startsWith("and")) {
			where.delete(0, where.toString().toLowerCase().indexOf("and") + 3);
		}

		return where.insert(0, "(").append(")").toString();
	}

	public Map<String, String> getAttrPath_AliasMap() {
		Map<String, String> attrpath_alias_map = new HashMap<String, String>();
		attrpath_alias_map.put(".", "wa_projsalary");
		attrpath_alias_map.put("pk_psndoc", "T1");
		return attrpath_alias_map;
	}
}
