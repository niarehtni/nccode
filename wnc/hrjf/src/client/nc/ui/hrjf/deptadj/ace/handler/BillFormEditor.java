package nc.ui.hrjf.deptadj.ace.handler;

import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.om.IDeptAdjustService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.om.ref.DeptPrincipalRefModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pubapp.bill.BillCardPanel;
import nc.ui.pubapp.bill.BillCardPanelWithoutScale;
import nc.ui.pubapp.uif2app.event.card.CardHeadTailBeforeEditEvent;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.AppEvent;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.logging.Debug;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.om.hrdept.HRDeptAdjustVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class BillFormEditor extends ShowUpableBillForm implements BillCardBeforeEditListener {
	private int selectedRow = 0;

	@Override
	public void initUI() {
		super.initUI();
		if (isShowOrgPanel()) {
			// this.getBillOrgPanel().setPkOrg("0001A110000000000XBZ");
			add(getBillOrgPanel(), "North");

			setRequestFocus(false);
		}
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);

	}

	public void showMeUp() {
		BillCardPanel billpanel = (BillCardPanel) super.billCardPanel;
		BillForm billform = ((BillCardPanelWithoutScale) billpanel).getBillForm();
		AggHRDeptAdjustVO aggvo = (AggHRDeptAdjustVO) billform.getModel().getSelectedData();
		super.showMeUp();
		if (null != aggvo) {
			((nc.ui.pubapp.uif2app.view.BaseOrgPanel) getBillOrgPanel()).setPkOrg(aggvo.getParentVO().getPk_org_v());
			billpanel.setHeadItem("deptname3", aggvo.getParent().getAttributeValue("name3"));
		}
	}

	@Override
	public void afterEdit(BillEditEvent evt) {

		super.afterEdit(evt);
		if (IBillItem.HEAD == evt.getPos()) {
			afterHeadChange(evt);
		}
	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if ("Selection_Changed" == event.getType()) {
			BillCardPanel billpanel = (BillCardPanel) super.billCardPanel;
			BillForm billform = ((BillCardPanelWithoutScale) billpanel).getBillForm();
			AggHRDeptAdjustVO aggvo = (AggHRDeptAdjustVO) billform.getModel().getSelectedData();
			if (aggvo != null) {
				billpanel.setHeadItem("deptname3", aggvo.getParent().getAttributeValue("name3"));
			}
		}
	}

	@Override
	public boolean beforeEdit(BillItemEvent evt) {
		String message = null;
		try {
			UFBoolean deptflag = null;
			UFBoolean psnflag = null;
			String pk_dept = (String) getHeadItemValue("pk_fatherorg");
			UIRefPane prinpan = ((UIRefPane) (getBillCardPanel().getHeadItem("dept_charge").getComponent()));
			DeptPrincipalRefModel dpinmodel = (DeptPrincipalRefModel) prinpan.getRefModel();
			dpinmodel.setPk_dept(pk_dept);
			dpinmodel.reset();
			// dpinmodel.reloadData();
			String effectivedate = String.valueOf(getHeadItemValue("effectivedate"));

			if (("code").equals(evt.getItem().getKey()) || ("name").equals(evt.getItem().getKey())
					|| ("pk_fatherorg").equals(evt.getItem().getKey()) || ("shortname").equals(evt.getItem().getKey())
					|| ("displayorder").equals(evt.getItem().getKey()) || ("mnecode").equals(evt.getItem().getKey())
					|| ("principal").equals(evt.getItem().getKey())) {
				/*
				 * if (null != pk_dept && null != effectivedate) { // 接口 if
				 * (deptflag == null || psnflag == null) { IDeptAdjustService
				 * managequery =
				 * NCLocator.getInstance().lookup(IDeptAdjustService.class);
				 * HRDeptAdjustVO deptadjvo = new HRDeptAdjustVO();
				 * deptadjvo.setPk_dept(pk_dept); deptadjvo.setEffectivedate(new
				 * UFLiteralDate((String.valueOf(effectivedate)))); deptflag =
				 * managequery.validateDept(deptadjvo); psnflag =
				 * managequery.validatePsn(deptadjvo); } }
				 */
				if (null != deptflag && null != psnflag && (!deptflag.booleanValue() || !psnflag.booleanValue())) {
					return false;
				}
			}

		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
			message = ex.getMessage();
		}
		if (null != message) {
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), "報錯信息", message);
			return false;
		}
		CardHeadTailBeforeEditEvent chtE = new CardHeadTailBeforeEditEvent(this.billCardPanel, evt);
		this.handleEvent(chtE);
		return true;
	}

	@SuppressWarnings("unchecked")
	private void afterHeadChange(BillEditEvent evt) {
		// TODO Auto-generated method stub
		String message = null;

		// try {
		if ("effectivedate".equals(evt.getKey())) {
			setHeadItemValue("createdate", evt.getValue());
		} else if ("pk_dept".equals(evt.getKey())) {
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			String effectivedate = getHeadItemValue("effectivedate") == null ? null : String
					.valueOf(getHeadItemValue("effectivedate"));
			// if (null != effectivedate) {
			String pk_dept = evt.getValue() == null ? "" : String.valueOf(getStrValue(evt.getValue()));
			HRDeptAdjustVO deptadjvo = new HRDeptAdjustVO();
			deptadjvo.setPk_dept(pk_dept);
			deptadjvo.setPk_deptadj((String) getHeadItemValue("pk_deptadj"));
			if (effectivedate == null) {
				effectivedate = new UFLiteralDate().toString();
			}
			deptadjvo.setEffectivedate(new UFLiteralDate(String.valueOf(effectivedate)));
			// 调用接口
			IDeptAdjustService managequery = NCLocator.getInstance().lookup(IDeptAdjustService.class);
			String messages = null;
			try {
				managequery.validateDept(deptadjvo);
				managequery.validatePsn(deptadjvo);
			} catch (BusinessException e) {
				messages = e.getMessage();
				setHeadItemValue("pk_dept", null);
				try {
					throw new BusinessException(e.getMessage());
				} catch (BusinessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (StringUtils.isEmpty(messages)) {
				List<Map<String, String>> deptlists = null;
				try {
					IDeptAdjustService managedept = NCLocator.getInstance().lookup(IDeptAdjustService.class);
					String pk_dept_v = managedept.queryLastDeptByPk(pk_dept);
					deptlists = (List<Map<String, String>>) iUAPQueryBS
							.executeQuery(
									"select islastversion, address,memo, orgtype17,tel, dataoriginflag,orgtype13, displayorder,"
											+ " enablestate, deptcanceldate, hrcanceled, deptduty,createdate, deptlevel, "
											+ "pk_group, pk_org,depttype, innercode, code,name,name2,name3,pk_fatherorg,mnecode,"
											+ "glbdef5,glbdef11,glbdef3,displayorder,principal, pk_vid, dept_charge from "
											+ "org_dept_v where pk_vid='" + pk_dept_v + "' and dr=0",
									new MapListProcessor());
					String pk_org = null;
					if (deptlists == null || deptlists.size() == 0) {
						// 多语处理完善 王永文 20190501 begin
						MultiLangText multiLangText = new MultiLangText();
						multiLangText.setText(null);
						multiLangText.setText2(null);
						multiLangText.setText3(null);
						// 多语处理完善 王永文 20190501 end
						setHeadItemValue("pk_dept_v.code", null);
						setHeadItemValue("code", null);
						setHeadItemValue("pk_dept_v.name", multiLangText);
						setHeadItemValue("name", multiLangText);
						setHeadItemValue("name3", null);
						setHeadItemValue("deptname3", null);
						setHeadItemValue("pk_dept_v.pk_fatherorg", null);
						setHeadItemValue("pk_fatherorg", null);
						setHeadItemValue("pk_dept_v.shortname", null);
						setHeadItemValue("pk_dept_v.mnecode", null);
						setHeadItemValue("pk_dept_v.glbdef11", null);
						setHeadItemValue("glbdef11", null);
						setHeadItemValue("pk_dept_v.glbdef3", null);
						setHeadItemValue("glbdef3", null);
						setHeadItemValue("pk_dept_v.principal", null);
						setHeadItemValue("principal", null);
						setHeadItemValue("pk_dept_v", null);
						setHeadItemValue("iseffective", UFBoolean.FALSE);
						setHeadItemValue("innercode", null);
						setHeadItemValue("pk_dept_v", null);
						setHeadItemValue("pk_group", null);
						setHeadItemValue("pk_org", null);
						setHeadItemValue("depttype", null);
						setHeadItemValue("deptlevel", null);
						setHeadItemValue("deptduty", null);
						setHeadItemValue("createdate", null);
						setHeadItemValue("hrcanceled", UFBoolean.FALSE);
						setHeadItemValue("deptcanceldate", null);
						setHeadItemValue("enablestate", null);
						setHeadItemValue("displayorder", null);
						setHeadItemValue("dataoriginflag", null);
						setHeadItemValue("orgtype13", null);
						setHeadItemValue("orgtype17", null);
						setHeadItemValue("tel", null);
						setHeadItemValue("address", null);
						setHeadItemValue("memo", null);
						setHeadItemValue("islastversion", UFBoolean.TRUE);
						setHeadItemValue("dept_charge", null);
						// 查询上级部门的编码
						// 修改后部门
						setHeadItemValue("pk_fatherorg.code", getDeptCodeByPkDept(getHeadItemValue("pk_fatherorg")));
						// 修改前部门
						setHeadItemValue("pk_dept_v.pk_fatherorg.code",
								getDeptCodeByPkDept(getHeadItemValue("pk_dept_v.pk_fatherorg")));
						return;
					}
					for (Map<String, String> map : deptlists) {
						if (map.get("pk_vid").equals(pk_dept_v)) {
							// 多语处理完善 王永文 20190501 begin
							MultiLangText multiLangText = new MultiLangText();
							multiLangText.setText(map.get("name"));
							multiLangText.setText2(map.get("name2"));
							multiLangText.setText3(map.get("name3"));
							// 多语处理完善 王永文 20190501 end
							pk_org = map.get("pk_org");
							setHeadItemValue("pk_dept_v.code", map.get("code"));
							setHeadItemValue("code", map.get("code"));
							setHeadItemValue("pk_dept_v.name", multiLangText);
							setHeadItemValue("name", multiLangText);
							setHeadItemValue("name3", map.get("name3"));
							setHeadItemValue("deptname3", map.get("name3"));
							setHeadItemValue("pk_dept_v.pk_fatherorg", map.get("pk_fatherorg"));
							setHeadItemValue("pk_fatherorg", map.get("pk_fatherorg"));
							setHeadItemValue("pk_dept_v.shortname", map.get("shortname"));
							setHeadItemValue("pk_dept_v.mnecode", map.get("mnecode"));
							setHeadItemValue("pk_dept_v.glbdef11", map.get("glbdef11"));
							setHeadItemValue("glbdef11", map.get("glbdef11"));
							setHeadItemValue("pk_dept_v.glbdef3", map.get("glbdef3"));
							setHeadItemValue("glbdef3", map.get("glbdef3"));
							setHeadItemValue("pk_dept_v.principal", map.get("principal"));
							setHeadItemValue("principal", map.get("principal"));
							setHeadItemValue("pk_dept_v", String.valueOf(map.get("pk_vid")));
							setHeadItemValue("iseffective", UFBoolean.FALSE);
							setHeadItemValue("innercode", map.get("innercode"));
							setHeadItemValue("pk_dept_v", map.get("pk_vid"));
							setHeadItemValue("pk_group", map.get("pk_group"));
							setHeadItemValue("pk_org", map.get("pk_org"));
							setHeadItemValue("depttype", map.get("depttype"));
							setHeadItemValue("deptlevel", map.get("deptlevel"));
							setHeadItemValue("deptduty", map.get("deptduty"));
							setHeadItemValue("createdate", getHeadItemValue("effectivedate"));
							setHeadItemValue("hrcanceled", UFBoolean.FALSE);
							setHeadItemValue("deptcanceldate", map.get("deptcanceldate"));
							setHeadItemValue("enablestate", map.get("enablestate"));
							setHeadItemValue("displayorder", map.get("displayorder"));
							setHeadItemValue("dataoriginflag", map.get("dataoriginflag"));
							setHeadItemValue("pk_dept_v.glbdef5", map.get("glbdef5"));
							setHeadItemValue("pk_dept_v.deptlevel", map.get("deptlevel"));
							setHeadItemValue("orgtype13", map.get("orgtype13"));
							setHeadItemValue("orgtype17", map.get("orgtype17"));
							setHeadItemValue("tel", map.get("tel"));
							setHeadItemValue("address", map.get("address"));
							setHeadItemValue("memo", map.get("memo"));
							setHeadItemValue("islastversion", UFBoolean.TRUE);
							// 查询上级部门的编码
							// 修改后部门
							setHeadItemValue("pk_fatherorg.code", getDeptCodeByPkDept(getHeadItemValue("pk_fatherorg")));
							// 修改前部门
							setHeadItemValue("pk_dept_v.pk_fatherorg.code",
									getDeptCodeByPkDept(getHeadItemValue("pk_dept_v.pk_fatherorg")));
							// 带出上级部门管理人员 20190805 wangywt
							setHeadItemValue("dept_charge", map.get("dept_charge"));
							setHeadItemValue("pk_dept_v.dept_charge", map.get("dept_charge"));
							if (!StringUtils.isEmpty(map.get("dept_charge"))) {
								PsndocVO psnVO = (PsndocVO) iUAPQueryBS.retrieveByPK(PsndocVO.class,
										map.get("dept_charge"));
								setHeadItemValue("dept_charge.code", psnVO.getCode());
								setHeadItemValue("pk_dept_v.dept_charge.code", psnVO.getCode());
							}
						}
					}
					if (null != pk_org) {
						List<Map<String, String>> deptlist;
						deptlist = (List<Map<String, String>>) iUAPQueryBS.executeQuery(
								"select pk_vid from org_orgs where pk_org='" + pk_org + "' and dr=0",
								new MapListProcessor());
						for (Map<String, String> map : deptlist) {
							map.get("pk_vid");
							setHeadItemValue("pk_org_v", map.get("pk_vid"));
						}
					}
				} catch (BusinessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				setItemValueAndEnable("code", getHeadItemValue("code"), false);
				setItemValueAndEnable("name", getHeadItemValue("name"), false);
				setItemValueAndEnable("pk_fatherorg", getHeadItemValue("pk_fatherorg"), false);
				setItemValueAndEnable("shortname", getHeadItemValue("shortname"), false);
				setItemValueAndEnable("mnecode", getHeadItemValue("mnecode"), false);
				setItemValueAndEnable("displayorder", getHeadItemValue("displayorder"), false);
				setItemValueAndEnable("principal", getHeadItemValue("principal"), false);
				setItemValueAndEnable("depttype", getHeadItemValue("depttype"), false);
				setItemValueAndEnable("deptlevel", getHeadItemValue("deptlevel"), false);
				setItemValueAndEnable("deptduty", getHeadItemValue("deptduty"), false);
				setItemValueAndEnable("createdate", getHeadItemValue("effectivedate"), false);
				setItemValueAndEnable("hrcanceled", getHeadItemValue("hrcanceled"), false);
				setItemValueAndEnable("deptcanceldate", getHeadItemValue("deptcanceldate"), false);
				setItemValueAndEnable("orgtype13", getHeadItemValue("orgtype13"), false);
				setItemValueAndEnable("orgtype17", getHeadItemValue("orgtype17"), false);
				setItemValueAndEnable("tel", getHeadItemValue("tel"), false);
				setItemValueAndEnable("address", getHeadItemValue("address"), false);
				setItemValueAndEnable("memo", getHeadItemValue("memo"), false);
				// 查询上级部门的编码
				// 修改后部门
				setHeadItemValue("pk_fatherorg.code", getDeptCodeByPkDept(getHeadItemValue("pk_fatherorg")));
				// 修改前部门
				setHeadItemValue("pk_dept.pk_fatherorg.code",
						getDeptCodeByPkDept(getHeadItemValue("pk_dept.pk_fatherorg")));
				MessageDialog.showErrorDlg(getParent(), "报错信息", messages);
				return;
			}
			// }
		} else if ("code".equals(evt.getKey())) {
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			String code = String.valueOf(getHeadItemValue("code"));
			if (null != code && !code.equals("null")) {
				// 自己的pk
				String pkDept = String.valueOf(getHeadItemValue("pk_dept"));
				// code 除了自己之外,不能重复
				String PkExist = getPkDeptByCode(code);
				if (null != PkExist && !PkExist.equals(pkDept)) {
					MessageDialog.showErrorDlg(getParent(), "報錯信息", "已存在編碼:" + code);
				}

			}
		} else if ("code".equals(evt.getKey())) {
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			String pk_fatherorg = String.valueOf(getHeadItemValue("pk_fatherorg"));
			if (null != pk_fatherorg && !pk_fatherorg.equals("null")) {
				// 上級部門修改
				// 修改后部门
				setHeadItemValue("pk_fatherorg.code", getDeptCodeByPkDept(pk_fatherorg));

			}
		} else if ("name".equals(evt.getKey())) {
			setHeadItemValue("deptname3", getHeadItemValue("name") == null ? ""
					: ((MultiLangText) getHeadItemValue("name")).getText3());
		}
		/*
		 * } catch (Exception ex) { Logger.error(ex.getMessage(), ex); message =
		 * ex.getMessage(); }
		 */
	}

	// 獲取當前
	private Object getHRCanceled(String pk_deptadj) {
		// TODO 自动生成的方法存根
		return null;
	}

	private String getDeptCodeByPkDept(Object pk_dept) {
		if (null == pk_dept || pk_dept.equals("null")) {
			return null;
		}
		String pk_deptStr = String.valueOf(pk_dept);
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String sqlStr = "select code from org_dept where pk_dept = '" + pk_deptStr + "'";
		String rtn = null;
		try {
			rtn = (String) iUAPQueryBS.executeQuery(sqlStr, new ColumnProcessor());
		} catch (BusinessException e) {
			Debug.debug(e.getMessage());
		}
		return rtn;
	}

	private String getPkDeptByCode(String code) {
		if (null == code || code.equals("null")) {
			return null;
		}
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String sqlStr = "select pk_dept from org_dept where code = '" + code + "'";
		String rtn = null;
		try {
			rtn = (String) iUAPQueryBS.executeQuery(sqlStr, new ColumnProcessor());
		} catch (BusinessException e) {
			Debug.debug(e.getMessage());
		}
		return rtn;
	}

	public Object getHeadItemValue(String key) {
		BillItem item = this.billCardPanel.getHeadItem(key);

		if (item != null) {
			return item.getValueObject();
		}

		return null;
	}

	private String getStrValue(Object value) {
		if (value == null) {
			return null;
		}
		if ((value instanceof String)) {
			return (String) value;
		}
		if ((value instanceof String[])) {
			return ((String[]) value)[0];
		}
		return value.toString();
	}

	public void setHeadItemValue(String key, Object value) {
		this.billCardPanel.setHeadItem(key, value);
	}

	private void clearHeadItemValue(String... strHeadItemKeys) {
		if ((strHeadItemKeys == null) || (strHeadItemKeys.length == 0)) {
			return;
		}
		for (String strItemKey : strHeadItemKeys) {
			BillItem item = getBillCardPanel().getHeadItem(strItemKey);
			if (item != null) {
				item.clearViewData();
			}
		}
	}

	private void setItemValueAndEnable(String itemKey, Object value, boolean isEnable) {

		BillItem item = getBillCardPanel().getHeadItem(itemKey);
		if (item != null) {
			getBillCardPanel().setHeadItem(itemKey, value);
			item.setEnabled(isEnable);
		}
	}

}
