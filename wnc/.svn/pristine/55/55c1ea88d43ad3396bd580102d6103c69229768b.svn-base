package nc.ui.om.hrdept.model;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.om.IDeptQueryService;
import nc.md.data.access.NCObject;
import nc.ui.hr.uif2.model.ITypeSupportModelDataManager;
import nc.ui.om.pub.model.IDeptAdjFeaturedAppModelDataManagerEx;
import nc.ui.om.pub.model.IFeaturedAppModelDataManagerEx;
import nc.ui.uif2.model.DefaultAppModelDataManager;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

import org.apache.commons.lang.StringUtils;

public class DeptBatchDataManager extends DefaultAppModelDataManager implements IFeaturedAppModelDataManagerEx,
		ITypeSupportModelDataManager, IDeptAdjFeaturedAppModelDataManagerEx {

	private String typeField;
	private String typePk;
	private String lastWhereSql;
	private IDeptQueryService queryService;
	/** 这里借用显示封存来实现显示撤销功能 */
	private boolean showHRCanceledFlag = false;
	/** 未来部门 **/
	private boolean showHRFuturedFlag = false;

	@Override
	public void initModel() {
		// typePk = null;
		initModelBySqlWhere(null);
	}

	@Override
	public void initModelBySqlWhere(String sqlWhere) {
		this.lastWhereSql = sqlWhere;
		refresh();
	}

	@Override
	public void refresh() {

		// 首先获取pk_org来判断是否在初始状态，如果在初始态，那么不去初始化
		DeptAppModel appModel = getModel();

		String pk_org = getContext().getPk_org();
		if (pk_org == null) {// 确保初始化时列表为空
			appModel.initModel(null);
			return;
		}

		try {
			String condition = lastWhereSql;

			if (!StringUtils.isEmpty(typePk)) {
				if (StringUtils.isEmpty(condition)) {
					condition = typeField + " = '" + typePk + "'";
				} else {
					condition = "(" + condition + ")" + " and (" + typeField + " = '" + typePk + "')";
				}
			}
			// condition = typeField + " = '" + typePk + "'";
			// 如果condition为空，查当前人力资源组织下属业务单元下所有部门
			// 改成按部门查询，所以这个地方代码注释掉了
			// if (StringUtils.isEmpty(typePk))
			// {
			// setTypeField(HRDeptVO.PK_DEPT);
			// // String orgSql =
			// AOSSQLHelper.getAllBUInSQLByHROrgPK(getContext().getPk_org());
			//
			// INaviQueryService s =
			// NCLocator.getInstance().lookup(INaviQueryService.class);
			// Object[] treeObj =
			// s.queryAOSMembersByHROrgPK(getContext().getPk_org(), false,
			// false, false);
			// ArrayList<Object> returnList = new ArrayList<Object>();
			//
			// for (int i = 0; treeObj != null && i < treeObj.length; i++)
			// {
			// if (treeObj[i] instanceof OrgVO)
			// {
			// returnList.add(treeObj[i]);
			// }
			// }
			// ArrayList<String> leftPKs = new ArrayList<String>();
			// for (Object Obj : treeObj)
			// {
			// SuperVO vo = (SuperVO) Obj;
			// leftPKs.add(vo.getPrimaryKey());
			// }
			// String orgSql =
			// NCLocator.getInstance().lookup(IPostQueryService.class).getInSQL(leftPKs.toArray(new
			// String[0]));
			// condition = typeField + " in ( " + orgSql + " ) ";
			// }
			/*
			 * // 不显示撤销 if (!isShowHRCanceledFlag()) { condition += " and ( " +
			 * HRDeptVO.HRCANCELED + " = 'N' and " + HRDeptVO.ENABLESTATE +
			 * " = " + IPubEnumConst.ENABLESTATE_ENABLE + " ) "; }
			 * 
			 * // 显示撤销 else { condition += " and ( " + HRDeptVO.HRCANCELED +
			 * " = 'Y' or " + HRDeptVO.ENABLESTATE + " = " +
			 * IPubEnumConst.ENABLESTATE_ENABLE + " ) "; }
			 */
			// 不显示撤销
			if (!isShowHRCanceledFlag()) {
				condition += " and ( " + HRDeptVO.HRCANCELED + " = 'N'";
				// 未来部门
				if (isShowHRFuturedFlag()) {
					condition += "and (" + HRDeptVO.ENABLESTATE + " = " + IPubEnumConst.ENABLESTATE_ENABLE + " or "
							+ HRDeptVO.ENABLESTATE + "=" + IPubEnumConst.ENABLESTATE_INIT + ")) ";
				} else {
					condition += "and " + HRDeptVO.ENABLESTATE + " = " + IPubEnumConst.ENABLESTATE_ENABLE + " ) ";
				}
			}

			// 显示撤销
			else {
				condition += " and ( " + HRDeptVO.HRCANCELED + " = 'Y'";
				if (isShowHRFuturedFlag()) {
					condition += " or (" + HRDeptVO.ENABLESTATE + " = " + IPubEnumConst.ENABLESTATE_ENABLE + " or "
							+ HRDeptVO.ENABLESTATE + "=" + IPubEnumConst.ENABLESTATE_INIT + ")) ";
				} else {
					condition += " or " + HRDeptVO.ENABLESTATE + " = " + IPubEnumConst.ENABLESTATE_ENABLE + " ) ";
				}
			}

			Object[] vos = queryByCondition(condition);
			getModel().initModel(vos);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}

	}

	@Override
	public void setShowSealDataFlag(boolean showSealDataFlag) {
	}

	private IDeptQueryService getQueryService() {
		if (queryService == null) {
			queryService = NCLocator.getInstance().lookup(IDeptQueryService.class);
		}
		return queryService;
	}

	private Object[] queryByCondition(String condition) throws BusinessException {
		return getQueryService().queryByCondition(this.getContext(), condition);
	}

	@Override
	public void initModelByType(NCObject typeData) {

		if (typeData == null) {
			typePk = null;
		} else {
			Object attr = typeData.getContainmentObject();
			if (attr == null)
				return;
			setTypeField(HRDeptVO.PK_DEPT);
			if (attr instanceof AggregatedValueObject) {
				typePk = ((SuperVO) ((AggregatedValueObject) attr).getParentVO()).getPrimaryKey();
				// String temp =
				// ((SuperVO)((AggregatedValueObject)attr).getParentVO()).getPrimaryKey();
				// getModel().setCurrTypeDptPK(temp);

			} else if (attr instanceof SuperVO) {
				typePk = ((SuperVO) attr).getPrimaryKey();

			}
		}
		refresh();
	}

	public String getTypeField() {
		return typeField;
	}

	public void setTypeField(String typeField) {
		this.typeField = typeField;
	}

	public String getTypePk() {
		return typePk;
	}

	public void setTypePk(String typePk) {
		this.typePk = typePk;
	}

	@Override
	public DeptAppModel getModel() {
		return (DeptAppModel) super.getModel();
	}

	public void setModel(DeptAppModel model) {
		super.setModel(model);
	}

	@Override
	public void setShowHRCanceledFlag(boolean showHRCanceledFlag) {
		this.showHRCanceledFlag = showHRCanceledFlag;
	}

	public boolean isShowHRCanceledFlag() {
		return showHRCanceledFlag;
	}

	@Override
	public void setShowHRFuturedFlag(boolean showHRFuturedFlag) {
		this.showHRFuturedFlag = showHRFuturedFlag;

	}

	// showHRFuturedFlag
	public boolean isShowHRFuturedFlag() {
		return showHRFuturedFlag;
	}
}
