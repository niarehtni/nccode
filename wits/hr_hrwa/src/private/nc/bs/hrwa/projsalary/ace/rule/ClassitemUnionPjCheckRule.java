/**
 * @(#)ClassitemUnionPjCheckRule.java 1.0 2017年9月18日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.bs.hrwa.projsalary.ace.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.impl.pub.util.db.InSqlManager;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.hrwa.IProjsalaryMaintain;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.projsalary.AggProjSalaryVO;
import nc.vo.wa.projsalary.ProjSalaryHVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author niehg
 * @since 6.3
 */
public class ClassitemUnionPjCheckRule implements IRule<AggProjSalaryVO> {

	@Override
	public void process(AggProjSalaryVO[] bills) {
		if (ArrayUtils.isEmpty(bills)) {
			return;
		}
		Set<String> psndocPkSet = new HashSet<String>();
		ProjSalaryHVO hvo = null;
		for (AggProjSalaryVO aggVO : bills) {
			psndocPkSet.add(aggVO.getParentVO().getPk_psndoc());
			if (null == hvo) {
				hvo = aggVO.getParentVO();
			}
		}
		StringBuilder baseWhere = new StringBuilder();
		baseWhere.append(" pk_org='").append(hvo.getPk_org()).append("' ");
		baseWhere.append(" and pk_wa_calss='").append(hvo.getPk_wa_calss()).append("' ");

		IProjsalaryMaintain service = NCLocator.getInstance().lookup(IProjsalaryMaintain.class);
		Map<String, ProjSalaryHVO> periodItemProjMap = new HashMap<String, ProjSalaryHVO>();
		Map<String, WaClassItemVO> itemVOMap = new HashMap<String, WaClassItemVO>();
		Map<String, DefdocVO> projectVOMap = new HashMap<String, DefdocVO>();
		Map<String, PsndocVO> psndocVOMap = new HashMap<String, PsndocVO>();
		try {
			StringBuilder whereCondition = new StringBuilder(baseWhere);
			whereCondition.append(" and cperiod='").append(hvo.getCperiod()).append("' ");
			periodItemProjMap = service.qryClassItemProjHVOMap(whereCondition.toString());
			StringBuilder periodCondition = new StringBuilder(whereCondition.toString().replaceAll("pk_wa_calss",
					"pk_wa_class"));
			periodCondition.append(" and cyear='").append(hvo.getCperiod().substring(0, 4)).append("' ");
			periodCondition.append(" and cperiod='").append(hvo.getCperiod().substring(4, 6)).append("' ");
			itemVOMap = service.qryClassItemByPeriod(periodCondition.toString(), new String[] { "pk_wa_classitem" });
			projectVOMap = service.qryProjectMap(null, new String[] { "pk_defdoc" });
			StringBuilder psnFilter = new StringBuilder(" pk_psndoc in ").append(InSqlManager
					.getInSQLValue(psndocPkSet));
			psndocVOMap = service.qryPsndocVOMap(psnFilter.toString(), new String[] { "pk_psndoc" });
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
		StringBuilder unionErrMsg = new StringBuilder();
		for (AggProjSalaryVO aggVO : bills) {
			if (service.isClassItemUnionProj(periodItemProjMap, aggVO.getParentVO())) {
				ProjSalaryHVO projhvo = aggVO.getParentVO();
				String classitem = null;
				String project = null;
				String psndoc = null;
				WaClassItemVO itemVO = itemVOMap.get(projhvo.getPk_classitem());
				if (null != itemVO) {
					classitem = MultiLangHelper.getName(itemVO);
				}
				DefdocVO projectVO = projectVOMap.get(projhvo.getDef1());
				if (null != projectVO) {
					project = projectVO.getCode();
				}
				PsndocVO psndocVO = psndocVOMap.get(projhvo.getPk_psndoc());
				if (null != psndocVO) {
					psndoc = psndocVO.getCode();
				}
				// 人员[{0}]已存在相同专案代码[{1}]薪资项目[{2}]的薪资数数据,请确认后再进行新增!
				unionErrMsg.append(ResHelper.getString("projsalary", "0pjsalary-00006", null, new String[] { psndoc,
						project, classitem }));
			}
		}
		if (unionErrMsg.length() > 0) {
			ExceptionUtils.wrappBusinessException(unionErrMsg.toString());
		}
	}

}
