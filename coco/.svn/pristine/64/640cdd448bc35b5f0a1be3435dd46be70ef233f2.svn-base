package nc.bs.hrss.ta.common.prcss;

import java.util.List;

import nc.bs.hrss.pub.BillCoderUtils;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.away.AwayConsts;
import nc.bs.hrss.ta.changeshift.ChangeShiftConsts;
import nc.bs.hrss.ta.leave.LeaveConsts;
import nc.bs.hrss.ta.overtime.OverTimeConsts;
import nc.bs.hrss.ta.signcard.SignConsts;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.hrss.pub.cmd.prcss.ICopyProcessor;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.ta.psndoc.TBMPsndocVO;

import org.apache.commons.lang.StringUtils;

public class TaBaseCopyProcessor implements ICopyProcessor {

	public void onBeforeCopy(AggregatedValueObject aggVO) {
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
		String[] bodyGridIds = new String[] { ChangeShiftConsts.VIEW_GRID_BODY, SignConsts.VIEW_GRID_BODY, AwayConsts.VIEW_GRID_BODY, OverTimeConsts.VIEW_GRID_BODY, LeaveConsts.VIEW_GRID_BODY };
		GridComp bodyGrid = null;
		for (String bodyGridId : bodyGridIds) {
			bodyGrid = (GridComp) widget.getViewComponents().getComponent(bodyGridId);
			if (bodyGrid != null) {
				bodyGrid.setShowImageBtn(true, true);
				break;
			}
		}

//		PsndocVO psndocVO = SessionUtil.getPsndocVO();
//		PsnJobVO psnJobVO = psndocVO.getPsnJobVO();
		// 最新考勤档案
		TBMPsndocVO tbmPsndocVO = TaAppContextUtil.getTBMPsndocVO();
//		空判断Ares.Tank 2018-7-20 14:40:15 Start
		String pk_psnjob = "";
		String pk_psnorg = "";
		if(null!=tbmPsndocVO){
			 pk_psnjob = tbmPsndocVO.getPk_psnjob();
			 pk_psnorg = tbmPsndocVO.getPk_psnorg();
		}
//		空判断Ares.Tank 2018-7-20 14:40:15 end
		
		String userPK = SessionUtil.getPk_user();
		String pk_tbm_group = TaAppContextUtil.getPk_tbm_group();
		String pk_tbm_org = TaAppContextUtil.getPk_tbm_org();

		SuperVO parentVO = (SuperVO) aggVO.getParentVO();
		// 主键
		parentVO.setPrimaryKey(null);
		// 自助单据
		parentVO.setAttributeValue("ishrssbill", UFBoolean.TRUE);
		// 单据所属集团
		parentVO.setAttributeValue("pk_group", pk_tbm_group);
		// 单据所属组织
		parentVO.setAttributeValue("pk_org", pk_tbm_org);

		String billTypeCode = (String) parentVO.getAttributeValue("pk_billtype");
		// 申请单编码
		parentVO.setAttributeValue("bill_code", getBillCode(pk_tbm_group, pk_tbm_org, billTypeCode));
		// 申请日期
		parentVO.setAttributeValue("apply_date", new UFLiteralDate());
		// 申请人
		parentVO.setAttributeValue("billmaker", userPK);
		// 审批状态
		parentVO.setAttributeValue("approve_state", IPfRetCheckInfo.NOSTATE);
		// 人员任职主键
		parentVO.setAttributeValue("pk_psnjob", pk_psnjob);
		// 人员基本档案主键
		parentVO.setAttributeValue("pk_psndoc", SessionUtil.getPk_psndoc());
		// 人员组织关系组件
		parentVO.setAttributeValue("pk_psnorg", pk_psnorg);

		// 创建人
		parentVO.setAttributeValue("creator", userPK);
		// 创建时间
		parentVO.setAttributeValue("creationtime", new UFDateTime());
		// 修改人
		parentVO.setAttributeValue("modifier", null);
		// 修改日期
		parentVO.setAttributeValue("modifiedtime", null);

		// QXP 添加多版本pk_org_v/pk_dept_v

		// 外键名称
		@SuppressWarnings("deprecation")
		String PKFieldName = parentVO.getPKFieldName();
		SuperVO[] vos = (SuperVO[]) aggVO.getChildrenVO();
		for (SuperVO vo : vos) {
			vo.setPrimaryKey(null);
			vo.setAttributeValue(PKFieldName, null);
			vo.setStatus(VOStatus.NEW);
		}

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
	 * 复制后操作
	 */
	public void onAfterCopy(Dataset ds) {
	}

}
