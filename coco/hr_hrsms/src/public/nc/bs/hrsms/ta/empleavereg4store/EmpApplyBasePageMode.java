package nc.bs.hrsms.ta.empleavereg4store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.hrss.pub.BillCoderUtils;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.PageModel;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.away.AwayConsts;
import nc.bs.hrss.ta.changeshift.ChangeShiftConsts;
import nc.bs.hrss.ta.common.TaApplyConsts;
import nc.bs.hrss.ta.leave.LeaveConsts;
import nc.bs.hrss.ta.leaveoff.LeaveOffConsts;
import nc.bs.hrss.ta.overtime.OverTimeConsts;
import nc.bs.hrss.ta.signcard.SignConsts;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.hr.pf.IHrPf;
import nc.pubitf.para.SysInitQuery;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldSet;
import nc.uap.lfw.core.data.MdDataset;
import nc.uap.lfw.core.data.UnmodifiableMdField;
import nc.uap.lfw.core.event.conf.DatasetRule;
import nc.uap.lfw.core.event.conf.EventConf;
import nc.uap.lfw.core.event.conf.EventSubmitRule;
import nc.uap.lfw.core.event.conf.ViewRule;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.IRefNode;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.pub.BusinessException;
import nc.vo.ta.timerule.TimeRuleVO;

/**
 * 我的考勤申请的PageModel
 * 
 * @author qiaoxp
 * 
 */
public abstract class EmpApplyBasePageMode extends PageModel {

	/**
	 * 获得单据类型
	 * 
	 * @return
	 */
	protected abstract String getBillType();

	/**
	 * 设置特殊的参照设置<br/>
	 * Map<参照Id, 参照的Controller><br/>
	 * 
	 * @return
	 */
	protected Map<String, String> getSpecialRefnodeMap() {
		return new HashMap<String, String>();
	}

	/**
	 * 设置考勤数据的小时位数<br/>
	 * String[]待设置的考勤数据字段数组<br/>
	 * 
	 * @return
	 */
	protected String[] getTimeDataFields() {
		return null;
	}

	/**
	 * 设置子表右肩菜单
	 * 
	 * @param gridId
	 * @param dsId
	 * @param dsDetailId
	 */
	protected void setBodyGridMenu(String gridId, String masterDsId, String dsDetailId) {
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		// 签卡申请子表Grid
//		GridComp bodyGrid = (GridComp) viewMain.getViewComponents().getComponent(gridId);
		/** 设置菜单新增行，插入行，删除行 */
		MenubarComp bodyMenuBar = new MenubarComp();
		bodyMenuBar.setId("gridToolMenubar");
		String[] itemIds = new String[] { "new_row", "delete_row", "insert_row" };
		String[] itemI18nNames = new String[] { "w_ta-001276", "w_ta-001278", "w_ta-001277" };
		String[] eventMethodNames = new String[] { "onLineAdd", "onLineDel", "onLineInsert" };
		for (int i = 0; i < itemIds.length; i++) {
			// 菜单
			MenuItem item = new MenuItem();
			item.setId(itemIds[i]);
			item.setStateManager("");
			item.setModifiers(2);
			item.setShowModel(2);
			// item.setText(itemTexts[i]);
			// item.setI18nName(itemI18nNames[i]);
			item.setLangDir("node_ta-res");
			item.setTipI18nName(itemI18nNames[i]);
			// item.setTip(itemTexts[i]);
			// 事件
			List<EventConf> eventConfList = new ArrayList<EventConf>();
			EventConf itemEvent = new EventConf();
			itemEvent.setOnserver(true);
			itemEvent.setName("onclick");
			itemEvent.setMethodName(eventMethodNames[i]);
			// 提交规则
			EventSubmitRule sr = new EventSubmitRule();
			ViewRule wr = new ViewRule();
			wr.setId(viewMain.getId());
			DatasetRule dsr = new DatasetRule();
			dsr.setId(masterDsId);
			dsr.setType(DatasetRule.TYPE_CURRENT_LINE);
			wr.addDsRule(dsr);
			DatasetRule dsr2 = new DatasetRule();
			dsr2.setId(dsDetailId);
			dsr2.setType(DatasetRule.TYPE_ALL_LINE);
			wr.addDsRule(dsr2);

			sr.addViewRule(wr);
			itemEvent.setSubmitRule(sr);
			eventConfList.add(itemEvent);
			item.setEventConfList(eventConfList);
			bodyMenuBar.addMenuItem(item);
		}
//		bodyGrid.setMenuBar(bodyMenuBar);
	}

	/**
	 * 初始化个性化设置
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// 在applicationContext中添加属性考勤档案和考勤规则
//		TaAppContextUtil.addTaAppContext();
		// 主片段
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		// 设置参照Controller
		setRefnodesDsListener(viewMain);
		// 根据考勤规则设置考勤数据的小时位数
		setTimeDatasPrecision(viewMain);
		// 页面特殊设置
		setPageSepcial(viewMain);
	}

	/**
	 * 根据考勤规则设置考勤数据的小时位数
	 * 
	 */
	private void setTimeDatasPrecision(LfwView viewMain) {
		// 考勤数据
		String[] timeDatas = getTimeDataFields();
		if (timeDatas == null || timeDatas.length == 0) {
			return;
		}
		Dataset[] dss = viewMain.getViewModels().getDatasets();
		if (dss == null || dss.length == 0) {
			return;
		}
		// 考勤位数
		int pointNum = getPointNum();
		for (Dataset ds : dss) {
			if (ds instanceof MdDataset) {
				for (String filedId : timeDatas) {
					int index = ds.getFieldSet().nameToIndex(filedId);
					if (index >= 0) {
						FieldSet fieldSet = ds.getFieldSet();
						Field field = fieldSet.getField(filedId);
						if(field instanceof UnmodifiableMdField) 
							field = ((UnmodifiableMdField) field).getMDField();
						fieldSet.updateField(filedId, field);
						
						field.setPrecision(String.valueOf(pointNum));
						
					}
				}
			}
		}
	}

	/**
	 * 获得考勤位数
	 * 
	 * @return
	 */
	private int getPointNum() {
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		if (timeRuleVO == null) {
			// 没有考勤规则的情况，设置默认值
			return 2;
		}
		int pointNum = Math.abs(timeRuleVO.getTimedecimal());
		return pointNum;
	}

	/**
	 * 设置参照Controller
	 */
	private void setRefnodesDsListener(LfwView viewMain) {
		// 获得单据页面的所有参照
		IRefNode[] refnodes = viewMain.getViewModels().getRefNodes();
		if (refnodes == null || refnodes.length == 0) {
			return;
		}
		// 需要特殊设置的参照集合
		Map<String, String> specialRefMap = getSpecialRefnodeMap();
		// 修改页面的参照类型的DatasetListener
		for (IRefNode refnode : refnodes) {
			if (specialRefMap.containsKey(refnode.getId())) {
				((NCRefNode) refnode).setDataListener(specialRefMap.get(refnode.getId()));
			} 
//			else {
//				((NCRefNode) refnode).setDataListener(TaApplyRefController.class.getName());
//			}
		}
	}

	/**
	 * 页面特殊设置
	 */
	protected void setPageSepcial(LfwView viewMain) {
		// 单据类型
		String billType = getBillType();
		String pk_group = SessionUtil.getPk_group();
		String pk_hrorg = TaAppContextUtil.getHROrg();
		FormComp frmBill = (FormComp) viewMain.getViewComponents().getComponent(getBillInfoForm(billType));
		if (frmBill == null) {
			return;
		}
		FormElement bill_code = frmBill.getElementById(TaApplyConsts.BILL_CODE);
		if (bill_code != null) {
			if (BillCoderUtils.isAutoGenerateBillCode(pk_group, pk_hrorg, billType)) {
				bill_code.setEnabled(false);
			}
		}
		FormElement transtypeid = frmBill.getElementById(TaApplyConsts.TRANS_TYPE);
		if (transtypeid != null) {
			if (isDirectApprove(pk_hrorg, getBillType())) {
				transtypeid.setEnabled(false);
			}
		}
	}

	/**
	 * 是否直批
	 * 
	 * @param pk_org
	 * @param billTypeCode
	 * @return
	 */
	private boolean isDirectApprove(String pk_org, String billTypeCode) {
		Integer type = null;
		try {
			type = SysInitQuery.getParaInt(pk_org, IHrPf.hashBillTypePara.get(billTypeCode));
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return type != null && type == HRConstEnum.APPROVE_TYPE_FORCE_DIRECT;
	}

	/**
	 * 获得单据信息的表格ID
	 * 
	 * @param billType
	 * @return
	 */
	private String getBillInfoForm(String billType) {
		if (SignConsts.BILL_TYPE_CODE.equals(billType)) {// 签卡
			return SignConsts.VIEW_FORM_BILLINFO;
		} else if (ChangeShiftConsts.BILL_TYPE_CODE.equals(billType)) {// 调班
			return ChangeShiftConsts.VIEW_FORM_BILLINFO;
		} else if (AwayConsts.BILL_TYPE_CODE.equals(billType)) {// 出差
			return AwayConsts.VIEW_FORM_BILLINFO;
		} else if (OverTimeConsts.BILL_TYPE_CODE.equals(billType)) {// 加班
			return OverTimeConsts.VIEW_FORM_BILLINFO;
		} else if (LeaveConsts.BILL_TYPE_CODE.equals(billType)) {// 休假
			return LeaveConsts.VIEW_FORM_BILLINFO;
		} else if (LeaveOffConsts.BILL_TYPE_CODE.equals(billType)) {// 销假
			return LeaveOffConsts.VIEW_FORM_BILLINFO;
		}
		return null;
	}
}
