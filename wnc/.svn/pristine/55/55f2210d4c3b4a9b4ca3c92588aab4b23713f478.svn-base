package nc.ui.wa.classitem.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IItemQueryService;
import nc.ui.hr.uif2.action.SaveAction;
import nc.ui.hr.uif2.action.SaveAddAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.para.SysInitBO_Client;
import nc.ui.uif2.editor.IEditor;
import nc.ui.wa.item.util.ItemUtils;
import nc.ui.wa.item.util.WaExpandTableUtil;
import nc.ui.wa.item.view.ItemBillFormEditor;
import nc.ui.wa.item.view.custom.ItemDataSourcePanel;
import nc.ui.wa.pub.DefaultWaActionInterceptor;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.func.WaDatasourceManager;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.ParaConstant;

import org.apache.commons.lang.StringUtils;

public class WaClassItemSaveInterceptor extends DefaultWaActionInterceptor {

	@Override
	public boolean beforeDoAction(Action action, ActionEvent e) {
		// 校验是否已经选择人力资源组织
		IEditor editor;
		WaClassItemVO vo;
		if (action instanceof SaveAction) {
			editor = ((SaveAction) action).getEditor();
			vo = (WaClassItemVO) ((SaveAction) action).getEditor().getValue();
		} else {
			vo = (WaClassItemVO) ((SaveAction) ((SaveAddAction) action).getSaveAction()).getEditor().getValue();
			editor = ((SaveAction) ((SaveAddAction) action).getSaveAction()).getEditor();
		}

		if (vo.getPk_wa_item() == null) {
			try {
				if (isOrgNode() && !groupAllowAddItem()) {
					showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0199")/*
																						 * @
																						 * res
																						 * "集团不允许下级增加公共薪资项目,必须选择公共薪资项目！"
																						 */);
					return false;
				}
			} catch (BusinessException e1) {
				Logger.error(e1.getMessage(), e1);
				showErrorDlg(e1.getMessage());
			}
		}

		if (vo.getTypeEnumVO().equals(TypeEnumVO.FLOATTYPE)) {
			if (vo.getRound_type() == null) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0200")/*
																					 * @
																					 * res
																					 * "数值型项目的舍位方式不能为空"
																					 */);
				return false;
			}
			if (vo.getIflddecimal() == null) {
				showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0011")/*
																					 * @
																					 * res
																					 * "数值型的小数位数不能为空"
																					 */);
				return false;
			}
			// if (vo.getIflddecimal().intValue() >=
			// vo.getIfldwidth().intValue()) {
			// showErrorDlg(ResHelper.getString("60130payitem","060130payitem0201")/*@res
			// "薪资项目的小数位数需要小于数据长度。"*/);
			// return false;
			// }
			try {
				WaItemVO waItem = NCLocator.getInstance().lookup(IItemQueryService.class)
						.queryWaItemVOByPk(vo.getPk_wa_item());
				if (waItem != null && null != vo.getIflddecimal()
						&& vo.getIflddecimal().intValue() > waItem.getIflddecimal().intValue()) {
					showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0202")/*
																						 * @
																						 * res
																						 * "薪资发放项目的小数位数不能大于公共薪资项目的小数位数。"
																						 */);
					return false;
				}
			} catch (BusinessException e1) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0203")/*
																					 * @
																					 * res
																					 * "没有找到对应的公共薪资项目。"
																					 */);
				return false;
			}
		}

		// 如果公式为空，需要根据不同模块进行提示
		String formular = vo.getVformula();
		//
		if (StringUtil.isEmpty(formular)) {
			if (vo.getFromEnumVO().equals(FromEnumVO.HI)) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0205")/*
																					 * @
																					 * res
																					 * "人事函数不能为空"
																					 */);
				return false;
			} else if (vo.getFromEnumVO().equals(FromEnumVO.WAORTHER)) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0206")/*
																					 * @
																					 * res
																					 * "薪资函数不能为空"
																					 */);
				return false;
			}else if(vo.getFromEnumVO().equals(FromEnumVO.OTHERSOURCE)){
			    showErrorDlg("外部数据源不能为空");
				return false;
		    }else if (vo.getFromEnumVO().equals(FromEnumVO.TA)) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0038")/*
																					 * @
																					 * res
																					 * "考勤函数不能为空"
																					 */);
				return false;
			}
			if (vo.getFromEnumVO().equals(FromEnumVO.WA_WAGEFORM)) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0207")/*
																					 * @
																					 * res
																					 * "薪资规则表不能为空"
																					 */);
				return false;
			} else if (vo.getFromEnumVO().equals(FromEnumVO.FORMULA)
					&& (!ItemUtils.isSpecialSystemItem(vo.getItemkey()))) {
				// 来自公式，并且不是系统项目
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0204")/*
																					 * @
																					 * res
																					 * "公式不能为空"
																					 */);
				return false;
			} else if (vo.getFromEnumVO().equals(FromEnumVO.WA_GRADE)) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0210")/*
																					 * @
																					 * res
																					 * "薪资标准表不能为空"
																					 */);
				return false;
			} else if (WaDatasourceManager.isOtherDatasource(vo.getIfromflag())) {
				// 如果是其他数据源。提示“公式不能为空”
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0204")/*
																					 * @
																					 * res
																					 * "公式不能为空"
																					 */);
				return false;
			}

		}

		// 如果公式不为空。薪资规则表需要判断“薪资项目的小数位数小于薪资规则表的小数位数”
		if (!StringUtil.isEmpty(formular)) {
			if (vo.getFromEnumVO().equals(FromEnumVO.WA_WAGEFORM)) {
				{
					ItemDataSourcePanel idp = ((ItemBillFormEditor) editor).getItemDataSourcePanel();
					int ruleDecimal = (Integer) idp.getWageForm().getRefModel().getValue("iflddecimal");
					int itemDecimal = vo.getIflddecimal();
					if (itemDecimal < ruleDecimal) {
						String msg = ResHelper.getString("60130payitem", "060130payitem0208")/*
																							 * @
																							 * res
																							 * "薪资项目的小数位数小于薪资规则表的小数位数，是否继续?"
																							 */;
						if (MessageDialog.showYesNoDlg(getContainer(),
								ResHelper.getString("60130payitem", "060130payitem0209")/*
																						 * @
																						 * res
																						 * "提示"
																						 */, msg) == UIDialog.ID_NO) {
							return false;
						}
					}
				}
			}
		}

		// 是否需要扩展（私有新增需要扩展，其他都不需要）
		if (needExpand(vo)) {
			WaExpandTableUtil util = new WaExpandTableUtil(getContainer(), vo);
			try {
				if (!util.expandTable()) {
					showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0211")/*
																						 * @
																						 * res
																						 * "薪资发放表扩展失败"
																						 */);
					return false;
				}

			} catch (BusinessException e2) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0211")/*
																					 * @
																					 * res
																					 * "薪资发放表扩展失败"
																					 */);
				return false;
			}
		}

		return super.beforeDoAction(action, e);

	}

	private boolean isOrgNode() {
		return getModel().getContext().getNodeType() == NODE_TYPE.ORG_NODE;
	}

	/**
	 * 判断集团是否允许下级增加发放项目
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private boolean groupAllowAddItem() throws BusinessException {
		//
		UFBoolean allowed = UFBoolean.TRUE;
		allowed = SysInitBO_Client.getParaBoolean(getModel().getContext().getPk_group(), ParaConstant.ADDITEM_PRVW);
		// allowed =
		// NCLocator.getInstance().lookup(IHrPara.class).getBooleanValue(
		// getModel().getContext().getPk_group(), ParaConstant.ADDITEM_PRVW,
		// null, null);

		return allowed.booleanValue();

	}

	private boolean needExpand(WaClassItemVO vo) {
		if (StringUtils.isBlank(vo.getPk_wa_item())) {
			// 增加私有项目，需要扩展
			return true;
		}

		return false;

	}

}