package nc.ui.hrjf.deptadj.ace.handler;

import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.om.IDeptAdjustService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pubapp.bill.BillCardPanel;
import nc.ui.pubapp.bill.BillCardPanelWithoutScale;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.vo.logging.Debug;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.om.hrdept.HRDeptAdjustVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

public class BillFormEditor extends ShowUpableBillForm implements BillCardBeforeEditListener {
	private int selectedRow = 0;

	@Override
	public void initUI() {
		super.initUI();
		if (isShowOrgPanel()) {
			//this.getBillOrgPanel().setPkOrg("0001A110000000000XBZ");
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
	public boolean beforeEdit(BillItemEvent evt) {
		String message = null;
		try {
			UFBoolean deptflag = null;
			UFBoolean psnflag = null;
			String pk_dept = (String) getHeadItemValue("pk_dept");
			String effectivedate = String.valueOf(getHeadItemValue("effectivedate"));

			if (("code").equals(evt.getItem().getKey()) || ("name").equals(evt.getItem().getKey())
					|| ("pk_fatherorg").equals(evt.getItem().getKey()) || ("shortname").equals(evt.getItem().getKey())
					|| ("displayorder").equals(evt.getItem().getKey()) || ("mnecode").equals(evt.getItem().getKey())
					|| ("principal").equals(evt.getItem().getKey())) {
				/*
				 * if (null != pk_dept && null != effectivedate) { // �ӿ� if
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
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), "���e��Ϣ", message);
			return false;
		}
		return true;
	}

	private void afterHeadChange(BillEditEvent evt) {
		// TODO Auto-generated method stub
		String message = null;

		// try {
		if ("effectivedate".equals(evt.getKey())) {
			String pk_dept = (String) getHeadItemValue("pk_dept");
			if (null != pk_dept) {
				HRDeptAdjustVO deptadjvo = new HRDeptAdjustVO();
				deptadjvo.setPk_dept(pk_dept);
				deptadjvo.setEffectivedate(new UFLiteralDate((String.valueOf(evt.getValue()))));
				deptadjvo.setPk_deptadj((String) getHeadItemValue("pk_deptadj"));
				// ���ýӿ�
				IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
				IDeptAdjustService managequery = NCLocator.getInstance().lookup(IDeptAdjustService.class);
				try {
					UFBoolean deptflag = managequery.validateDept(deptadjvo);
					UFBoolean psnflag = managequery.validatePsn(deptadjvo);
				} catch (BusinessException e) {
					message = e.getMessage();
					try {
						throw new BusinessException(e.getMessage());
					} catch (BusinessException e1) {

						e1.printStackTrace();
					}
					setHeadItemValue("effectivedate",null);
				}
				IDeptAdjustService managedept = NCLocator.getInstance().lookup(IDeptAdjustService.class);
				String pk_dept_v = managedept.queryLastDeptByPk(pk_dept);
				if (null == message) {
					try {
						List<Map<String, String>> deptlists = (List<Map<String, String>>) iUAPQueryBS
								.executeQuery(
										"select islastversion, address,memo, orgtype17,tel, dataoriginflag,orgtype13, displayorder, enablestate, deptcanceldate, hrcanceled, deptduty,createdate, deptlevel, "
												+ "pk_group, pk_org,depttype, innercode, code,name,pk_fatherorg,mnecode,"
												+ "displayorder,principal, pk_vid from "
												+ "org_dept_v where pk_dept='"
												+ pk_dept + "' and dr=0", new MapListProcessor());
						String pk_org = null;
						for (Map<String, String> map : deptlists) {
							if (map.get("pk_vid").equals(pk_dept_v)) {
								pk_org = map.get("pk_org");
								setHeadItemValue("code", map.get("code"));
								setHeadItemValue("name", map.get("name"));
								setHeadItemValue("pk_fatherorg", map.get("pk_fatherorg"));
								setHeadItemValue("shortname", map.get("shortname"));
								setHeadItemValue("mnecode", map.get("mnecode"));
								setHeadItemValue("displayorder", map.get("displayorder"));
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
								setHeadItemValue("createdate", map.get("createdate"));
								setHeadItemValue("hrcanceled", UFBoolean.FALSE);
								setHeadItemValue("deptcanceldate", map.get("deptcanceldate"));
								setHeadItemValue("enablestate", map.get("enablestate"));
								setHeadItemValue("displayorder", map.get("displayorder"));
								setHeadItemValue("dataoriginflag", map.get("dataoriginflag"));
								setHeadItemValue("orgtype13", map.get("orgtype13"));
								setHeadItemValue("orgtype17", map.get("orgtype17"));
								setHeadItemValue("tel", map.get("tel"));
								setHeadItemValue("address", map.get("address"));
								setHeadItemValue("memo", map.get("memo"));
								setHeadItemValue("islastversion", UFBoolean.TRUE);
								//��ѯ�ϼ����ŵı���
								//�޸ĺ���
								setHeadItemValue("pk_fatherorg.code", getDeptCodeByPkDept(getHeadItemValue("pk_fatherorg")));
								//�޸�ǰ����
								setHeadItemValue("pk_dept.pk_fatherorg.code", getDeptCodeByPkDept(getHeadItemValue("pk_dept.pk_fatherorg")));
								// setHeadItemValue("pk_org_v",
								// map.get("pk_org_v"));
							}
						}
						if (null != pk_org) {
							List<Map<String, String>> deptlist = null;

							deptlist = (List<Map<String, String>>) iUAPQueryBS.executeQuery(
									"select pk_vid from org_orgs where pk_org='" + pk_org + "' and dr=0",
									new MapListProcessor());
							if (deptlist != null) {
								for (Map<String, String> map : deptlist) {
									map.get("pk_vid");
									setHeadItemValue("pk_org_v", map.get("pk_vid"));
								}
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
					setItemValueAndEnable("createdate", getHeadItemValue("createdate"), false);
					setItemValueAndEnable("hrcanceled", getHeadItemValue("hrcanceled"), false);
					setItemValueAndEnable("deptcanceldate", getHeadItemValue("deptcanceldate"), false);
					setItemValueAndEnable("orgtype13", getHeadItemValue("orgtype13"), false);
					setItemValueAndEnable("orgtype17", getHeadItemValue("orgtype17"), false);
					setItemValueAndEnable("tel", getHeadItemValue("tel"), false);
					setItemValueAndEnable("address", getHeadItemValue("address"), false);
					setItemValueAndEnable("memo", getHeadItemValue("memo"), false);
					//��ѯ�ϼ����ŵı���
					//�޸ĺ���
					setHeadItemValue("pk_fatherorg.code", getDeptCodeByPkDept(getHeadItemValue("pk_fatherorg")));
					//�޸�ǰ����
					setHeadItemValue("pk_dept.pk_fatherorg.code", getDeptCodeByPkDept(getHeadItemValue("pk_dept.pk_fatherorg")));
					
					MessageDialog.showErrorDlg(getParent(), "������Ϣ", message);
					return;
				}
			}
		} else if ("pk_dept".equals(evt.getKey())) {
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			String effectivedate = String.valueOf(getHeadItemValue("effectivedate"));
			if (null != effectivedate) {
				String pk_dept = String.valueOf(getStrValue(evt.getValue()));
				HRDeptAdjustVO deptadjvo = new HRDeptAdjustVO();
				deptadjvo.setPk_dept(pk_dept);
				deptadjvo.setPk_deptadj((String) getHeadItemValue("pk_deptadj"));
				deptadjvo.setEffectivedate(new UFLiteralDate(String.valueOf(effectivedate)));
				// ���ýӿ�
				IDeptAdjustService managequery = NCLocator.getInstance().lookup(IDeptAdjustService.class);
				String messages = null;
				try {
					UFBoolean deptflag = managequery.validateDept(deptadjvo);
					UFBoolean psnflag = managequery.validatePsn(deptadjvo);
				} catch (BusinessException e) {
					messages = e.getMessage();
					setHeadItemValue("pk_dept",null);
					try {
						throw new BusinessException(e.getMessage());
					} catch (BusinessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if (null == messages) {
					List<Map<String, String>> deptlists = null;
					try {
						deptlists = (List<Map<String, String>>) iUAPQueryBS
								.executeQuery(
										"select islastversion, address,memo, orgtype17,tel, dataoriginflag,orgtype13, displayorder, enablestate, deptcanceldate, hrcanceled, deptduty,createdate, deptlevel, "
												+ "pk_group, pk_org,depttype, innercode, code,name,pk_fatherorg,mnecode,"
												+ "displayorder,principal, pk_vid from "
												+ "org_dept_v where pk_dept='"
												+ pk_dept + "' and dr=0", new MapListProcessor());

						IDeptAdjustService managedept = NCLocator.getInstance().lookup(IDeptAdjustService.class);
						String pk_dept_v = managedept.queryLastDeptByPk(pk_dept);
						String pk_org = null;
						if (deptlists == null) {
							return;
						}
						for (Map<String, String> map : deptlists) {
							if (map.get("pk_vid").equals(pk_dept_v)) {
								pk_org = map.get("pk_org");
								setHeadItemValue("code", map.get("code"));
								setHeadItemValue("name", map.get("name"));
								setHeadItemValue("pk_fatherorg", map.get("pk_fatherorg"));
								setHeadItemValue("shortname", map.get("shortname"));
								setHeadItemValue("mnecode", map.get("mnecode"));
								setHeadItemValue("displayorder", map.get("displayorder"));
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
								setHeadItemValue("createdate", map.get("createdate"));
								setHeadItemValue("hrcanceled", UFBoolean.FALSE);
								setHeadItemValue("deptcanceldate", map.get("deptcanceldate"));
								setHeadItemValue("enablestate", map.get("enablestate"));
								setHeadItemValue("displayorder", map.get("displayorder"));
								setHeadItemValue("dataoriginflag", map.get("dataoriginflag"));
								setHeadItemValue("orgtype13", map.get("orgtype13"));
								setHeadItemValue("orgtype17", map.get("orgtype17"));
								setHeadItemValue("tel", map.get("tel"));
								setHeadItemValue("address", map.get("address"));
								setHeadItemValue("memo", map.get("memo"));
								setHeadItemValue("islastversion", UFBoolean.TRUE);
								//��ѯ�ϼ����ŵı���
								//�޸ĺ���
								setHeadItemValue("pk_fatherorg.code", getDeptCodeByPkDept(getHeadItemValue("pk_fatherorg")));
								//�޸�ǰ����
								setHeadItemValue("pk_dept.pk_fatherorg.code", getDeptCodeByPkDept(getHeadItemValue("pk_dept.pk_fatherorg")));
								
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
					setItemValueAndEnable("createdate", getHeadItemValue("createdate"), false);
					setItemValueAndEnable("hrcanceled", getHeadItemValue("hrcanceled"), false);
					setItemValueAndEnable("deptcanceldate", getHeadItemValue("deptcanceldate"), false);
					setItemValueAndEnable("orgtype13", getHeadItemValue("orgtype13"), false);
					setItemValueAndEnable("orgtype17", getHeadItemValue("orgtype17"), false);
					setItemValueAndEnable("tel", getHeadItemValue("tel"), false);
					setItemValueAndEnable("address", getHeadItemValue("address"), false);
					setItemValueAndEnable("memo", getHeadItemValue("memo"), false);
					//��ѯ�ϼ����ŵı���
					//�޸ĺ���
					setHeadItemValue("pk_fatherorg.code", getDeptCodeByPkDept(getHeadItemValue("pk_fatherorg")));
					//�޸�ǰ����
					setHeadItemValue("pk_dept.pk_fatherorg.code", getDeptCodeByPkDept(getHeadItemValue("pk_dept.pk_fatherorg")));
					MessageDialog.showErrorDlg(getParent(), "������Ϣ", messages);
					return;
				}
			}
		}else if ("code".equals(evt.getKey())) {
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			String code = String.valueOf(getHeadItemValue("code"));
			if (null != code && !code.equals("null")) {
				//�Լ���pk
				String pkDept = String.valueOf(getHeadItemValue("pk_dept"));
				//code �����Լ�֮��,�����ظ�
				String PkExist = getPkDeptByCode(code);
				if(null != PkExist && !PkExist.equals(pkDept)){
					MessageDialog.showErrorDlg(getParent(), "���e��Ϣ", "�Ѵ��ھ��a:"+code);
				}
				
			}
		}else if ("code".equals(evt.getKey())) {
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			String pk_fatherorg = String.valueOf(getHeadItemValue("pk_fatherorg"));
			if (null != pk_fatherorg && !pk_fatherorg.equals("null")) {
				//�ϼ����T�޸�
				//�޸ĺ���
				setHeadItemValue("pk_fatherorg.code", getDeptCodeByPkDept(pk_fatherorg));
				
			}
		}
		/*
		 * } catch (Exception ex) { Logger.error(ex.getMessage(), ex); message =
		 * ex.getMessage(); }
		 */
	}
	//�@ȡ��ǰ
	private Object getHRCanceled(String pk_deptadj) {
		// TODO �Զ����ɵķ������
		return null;
	}

	private String getDeptCodeByPkDept(Object pk_dept) {
		if(null == pk_dept || pk_dept.equals("null")){
			return null;
		}
		String pk_deptStr = String.valueOf(pk_dept);
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String sqlStr= "select code from org_dept where pk_dept = '"+pk_deptStr+"'";
		String rtn = null;
		try {
			rtn = (String)iUAPQueryBS.executeQuery(sqlStr,new ColumnProcessor());
		} catch (BusinessException e) {
			Debug.debug(e.getMessage());
		}
		return rtn;
	}
	private String getPkDeptByCode(String code) {
		if(null == code || code.equals("null")){
			return null;
		}
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		String sqlStr= "select pk_dept from org_dept where code = '"+code+"'";
		String rtn = null;
		try {
			rtn = (String)iUAPQueryBS.executeQuery(sqlStr,new ColumnProcessor());
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