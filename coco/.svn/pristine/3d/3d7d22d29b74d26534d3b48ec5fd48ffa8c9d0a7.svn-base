package nc.bs.hrsms.ta.sss.shopleave.prcss;

import java.util.List;

import nc.bs.hrsms.ta.sss.away.ShopAwayApplyConsts;
import nc.bs.hrsms.ta.sss.overtime.ShopOverTimeConsts;
import nc.bs.hrsms.ta.sss.shopleave.ShopLeaveApplyConsts;
import nc.bs.hrss.pub.BillCoderUtils;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trade.pub.IBillStatus;

import org.apache.commons.lang.StringUtils;

public class ShopTaBaseAddProcessor implements IAddProcessor {

	/**
	 * 新增行前的操作
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onBeforeRowAdd(Dataset ds, Row row, String billTypeCode) {

		// 单据来源区分
		String pageSource = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(HrssConsts.POPVIEW_OPERATE_PARAM);
		LfwView widget = AppLifeCycleContext.current().getViewContext().getView();
		MenubarComp menuOperate = widget.getViewMenus().getMenuBar("menu_operate");
		if (menuOperate != null) {
			List<MenuItem> menuList = menuOperate.getMenuList();
			if (menuList != null && menuList.size() > 0) {
				for (MenuItem item : menuList) {
					if (StringUtils.isEmpty(pageSource)) { // 单据来源区分：我的考勤申请节点
						if (item.getId().equals("btnSaveCommit")) {
//							continue;
						}

					} else { // 单据来源区分：信息中心节点
						if (item.getId().equals("btnSaveAdd")) {
							continue;
						}
					}
					item.setVisible(true);
				}
			}
		}
		String[] bodyGridIds = new String[] { ShopAwayApplyConsts.VIEW_GRID_BODY, ShopOverTimeConsts.VIEW_GRID_BODY, ShopLeaveApplyConsts.VIEW_GRID_BODY };
		GridComp bodyGrid = null;
		for (String bodyGridId : bodyGridIds) {
			bodyGrid = (GridComp) widget.getViewComponents().getComponent(bodyGridId);
			if (bodyGrid != null) {
				bodyGrid.setShowImageBtn(true, true);
				break;
			}
		}
		
		// 最新考勤档案
		/*TBMPsndocVO tbmPsndocVO = TaAppContextUtil.getTBMPsndocVO();
		String pk_psnjob = tbmPsndocVO.getPk_psnjob();
		String pk_psnorg = tbmPsndocVO.getPk_psnorg();*/
		
		String userPK = SessionUtil.getPk_user();
//		String pk_tbm_group = TaAppContextUtil.getPk_tbm_group();
//		String pk_tbm_org = TaAppContextUtil.getPk_tbm_org();
		String pk_tbm_group = SessionUtil.getPk_group();
		String pk_tbm_org = SessionUtil.getPk_org();
		// 是否自助单据
		row.setValue(ds.nameToIndex("ishrssbill"), UFBoolean.TRUE);
		// 单据类型
		row.setValue(ds.nameToIndex("pk_billtype"), billTypeCode);
		// 单据所属集团
		row.setValue(ds.nameToIndex("pk_group"), pk_tbm_group);
		// 单据所属组织
		row.setValue(ds.nameToIndex("pk_org"), pk_tbm_org);
		// 申请单编码
		row.setValue(ds.nameToIndex("bill_code"), getBillCode(pk_tbm_group, pk_tbm_org, billTypeCode));
		// 申请日期
		row.setValue(ds.nameToIndex("apply_date"), new UFLiteralDate());
		// 申请人
		row.setValue(ds.nameToIndex("billmaker"), userPK);
		// 审批状态
		row.setValue(ds.nameToIndex("approve_state"), IBillStatus.FREE);
		// 人员任职主键
//		row.setValue(ds.nameToIndex("pk_psnjob"), pk_psnjob);
		// 人员基本档案主键
//		row.setValue(ds.nameToIndex("pk_psndoc"), SessionUtil.getPk_psndoc());
		// 人员组织关系组件
//		row.setValue(ds.nameToIndex("pk_psnorg"), pk_psnorg);
		// 创建人
		row.setValue(ds.nameToIndex("creator"), userPK);
		// 创建时间
		row.setValue(ds.nameToIndex("creationtime"), new UFDateTime());

	}

	/**
	 * 获得单据编码
	 * 
	 * @param pk_group
	 * @param pk_org
	 * @param billType
	 * @return
	 */
	private String getBillCode(String pk_group, String pk_org, String billType) {
		return BillCoderUtils.getBillCode(pk_group, pk_org, billType);
	}

	/**
	 * 新增行后的操作
	 */
	@Override
	public void onAfterRowAdd(Dataset ds, Row row) {
		
	}
}
