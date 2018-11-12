/**
 * @(#)AllocateDataManager.java 1.0 2017年9月14日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.allocate.ace.model;

import java.util.Map;

import javax.swing.SwingUtilities;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.pubapp.uif2app.query2.model.ModelDataManager;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.querytree.QueryScheme;
import nc.ui.uif2.model.ModelDataDescriptor;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pubapp.res.Variable;
import nc.vo.wa.projsalary.ProjSalaryQryVO;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings("restriction")
public class AllocateDataManager extends ModelDataManager {
	@Override
	public void initModelByQueryScheme(IQueryScheme qryScheme) {
		// super.initModelByQueryScheme(qryScheme);
		if (null != qryScheme) {
			ProjSalaryQryVO qryConditionVO = (ProjSalaryQryVO) qryScheme.get(ProjSalaryQryVO.QRYCONDITIONVO);
			String from = qryConditionVO.getFrom().replaceAll("wa_projsalary", "wa_allocateout");
			String where = qryConditionVO.getWhere().replaceAll("wa_projsalary", "wa_allocateout");
			Map<String, String> attrAlisMap = qryConditionVO.getAttrPath_AliasMap();
			attrAlisMap.put(".", "wa_allocateout");
			FromWhereSQLImpl fromWheretSql = new FromWhereSQLImpl(from, where);
			fromWheretSql.setAttrpath_alias_map(attrAlisMap);
			((QueryScheme) qryScheme).putTableListFromWhereSQL(fromWheretSql);
			((QueryScheme) qryScheme).putTableJoinFromWhereSQL(fromWheretSql);
			((QueryScheme) qryScheme).putWhereSQLOnly(qryConditionVO.toString());
		}

		setQueryScheme(qryScheme);
		try {
			Object[] objs = getQryService().queryByQueryScheme(qryScheme);
			if (objs != null && objs.length == Variable.getMaxQueryCount()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						String hint = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("pubapp_0", "0pubapp-0268",
								null, new String[] { "" + Variable.getMaxQueryCount() })/*
																						 * @
																						 * res
																						 * "查询结果太大，只返回了{0}条数据，请缩小范围再次查询"
																						 */;
						MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(), null, hint);
					}
				});
			}

			if (this.getModelDataProcessor() != null) {
				objs = this.getModelDataProcessor().processModelData(qryScheme, objs);
			}
			String schemeName = qryScheme.getName();
			if (!StringUtil.isEmptyWithTrim(schemeName)) {
				ModelDataDescriptor modelDataDescriptor = new ModelDataDescriptor(schemeName);
				this.getModel().initModel(objs, modelDataDescriptor);
			} else {
				ModelDataDescriptor modelDataDescriptor = new ModelDataDescriptor(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("pubapp_0", "0pubapp-0144")/*
																			 * @res
																			 * "查询结果"
																			 */);
				this.getModel().initModel(objs, modelDataDescriptor);
			}

		} catch (Exception e) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("pubapp_0",
					"0pubapp-0007")/* @res "查询单据发生异常" */, e);
		}
	}

	@Override
	public void refresh() {
		super.refresh();
	}

	@Override
	public boolean refreshable() {
		return super.refreshable();
	}
}
