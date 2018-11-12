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
 * �ҵĿ��������PageModel
 * 
 * @author qiaoxp
 * 
 */
public abstract class EmpApplyBasePageMode extends PageModel {

	/**
	 * ��õ�������
	 * 
	 * @return
	 */
	protected abstract String getBillType();

	/**
	 * ��������Ĳ�������<br/>
	 * Map<����Id, ���յ�Controller><br/>
	 * 
	 * @return
	 */
	protected Map<String, String> getSpecialRefnodeMap() {
		return new HashMap<String, String>();
	}

	/**
	 * ���ÿ������ݵ�Сʱλ��<br/>
	 * String[]�����õĿ��������ֶ�����<br/>
	 * 
	 * @return
	 */
	protected String[] getTimeDataFields() {
		return null;
	}

	/**
	 * �����ӱ��Ҽ�˵�
	 * 
	 * @param gridId
	 * @param dsId
	 * @param dsDetailId
	 */
	protected void setBodyGridMenu(String gridId, String masterDsId, String dsDetailId) {
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		// ǩ�������ӱ�Grid
//		GridComp bodyGrid = (GridComp) viewMain.getViewComponents().getComponent(gridId);
		/** ���ò˵������У������У�ɾ���� */
		MenubarComp bodyMenuBar = new MenubarComp();
		bodyMenuBar.setId("gridToolMenubar");
		String[] itemIds = new String[] { "new_row", "delete_row", "insert_row" };
		String[] itemI18nNames = new String[] { "w_ta-001276", "w_ta-001278", "w_ta-001277" };
		String[] eventMethodNames = new String[] { "onLineAdd", "onLineDel", "onLineInsert" };
		for (int i = 0; i < itemIds.length; i++) {
			// �˵�
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
			// �¼�
			List<EventConf> eventConfList = new ArrayList<EventConf>();
			EventConf itemEvent = new EventConf();
			itemEvent.setOnserver(true);
			itemEvent.setName("onclick");
			itemEvent.setMethodName(eventMethodNames[i]);
			// �ύ����
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
	 * ��ʼ�����Ի�����
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
		// ��applicationContext��������Կ��ڵ����Ϳ��ڹ���
//		TaAppContextUtil.addTaAppContext();
		// ��Ƭ��
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		// ���ò���Controller
		setRefnodesDsListener(viewMain);
		// ���ݿ��ڹ������ÿ������ݵ�Сʱλ��
		setTimeDatasPrecision(viewMain);
		// ҳ����������
		setPageSepcial(viewMain);
	}

	/**
	 * ���ݿ��ڹ������ÿ������ݵ�Сʱλ��
	 * 
	 */
	private void setTimeDatasPrecision(LfwView viewMain) {
		// ��������
		String[] timeDatas = getTimeDataFields();
		if (timeDatas == null || timeDatas.length == 0) {
			return;
		}
		Dataset[] dss = viewMain.getViewModels().getDatasets();
		if (dss == null || dss.length == 0) {
			return;
		}
		// ����λ��
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
	 * ��ÿ���λ��
	 * 
	 * @return
	 */
	private int getPointNum() {
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		if (timeRuleVO == null) {
			// û�п��ڹ�������������Ĭ��ֵ
			return 2;
		}
		int pointNum = Math.abs(timeRuleVO.getTimedecimal());
		return pointNum;
	}

	/**
	 * ���ò���Controller
	 */
	private void setRefnodesDsListener(LfwView viewMain) {
		// ��õ���ҳ������в���
		IRefNode[] refnodes = viewMain.getViewModels().getRefNodes();
		if (refnodes == null || refnodes.length == 0) {
			return;
		}
		// ��Ҫ�������õĲ��ռ���
		Map<String, String> specialRefMap = getSpecialRefnodeMap();
		// �޸�ҳ��Ĳ������͵�DatasetListener
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
	 * ҳ����������
	 */
	protected void setPageSepcial(LfwView viewMain) {
		// ��������
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
	 * �Ƿ�ֱ��
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
	 * ��õ�����Ϣ�ı��ID
	 * 
	 * @param billType
	 * @return
	 */
	private String getBillInfoForm(String billType) {
		if (SignConsts.BILL_TYPE_CODE.equals(billType)) {// ǩ��
			return SignConsts.VIEW_FORM_BILLINFO;
		} else if (ChangeShiftConsts.BILL_TYPE_CODE.equals(billType)) {// ����
			return ChangeShiftConsts.VIEW_FORM_BILLINFO;
		} else if (AwayConsts.BILL_TYPE_CODE.equals(billType)) {// ����
			return AwayConsts.VIEW_FORM_BILLINFO;
		} else if (OverTimeConsts.BILL_TYPE_CODE.equals(billType)) {// �Ӱ�
			return OverTimeConsts.VIEW_FORM_BILLINFO;
		} else if (LeaveConsts.BILL_TYPE_CODE.equals(billType)) {// �ݼ�
			return LeaveConsts.VIEW_FORM_BILLINFO;
		} else if (LeaveOffConsts.BILL_TYPE_CODE.equals(billType)) {// ����
			return LeaveOffConsts.VIEW_FORM_BILLINFO;
		}
		return null;
	}
}
