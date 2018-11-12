/**
 * @(#)ProjSalaryQryDlg.java 1.0 2017年9月15日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.ui.hrwa.projsalary.ace.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import nc.hr.utils.ResHelper;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.pub.beans.UIMultiLangCombox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.IBillItem;
import nc.ui.wa.ref.WaClassItemRefModel;
import nc.ui.wa.ref.WaClassRefModel;
import nc.ui.wa.ref.WaPeriodRefTreeModel;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.projsalary.ProjSalaryQryVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "serial", "restriction" })
public class ProjSalaryQryDlg extends HrDialog {
	/** 查询条件设置面板 */
	private BillCardPanel billCardPanel;
	private WaLoginContext waContext;
	// 是否
	protected boolean isItemFilter = true;

	public ProjSalaryQryDlg(WaLoginContext context, boolean isItemFilter, String title) {
		// 专项薪资查询条件
		this(context.getEntranceUI(), context, isItemFilter, title);
	}

	public ProjSalaryQryDlg(WaLoginContext context) {
		// 专项薪资查询条件
		this(context.getEntranceUI(), context, Boolean.TRUE, ResHelper.getString("projsalary", "0pjsalary-00001"));
	}

	public ProjSalaryQryDlg(Container parent, WaLoginContext context, boolean isItemFilter, String strTitle) {
		this(parent, strTitle, false);
		this.waContext = context;
		this.isItemFilter = isItemFilter;
		initUIThis();
	}

	public ProjSalaryQryDlg(Container parent, String strTitle, boolean blInit) {
		super(parent, strTitle, blInit);
	}

	private void initUIThis() {
		super.initUI();
		setSize(350, 280);
	}

	@Override
	protected JComponent createCenterPanel() {
		UIPanel contentPanel = new UIPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(getBillCardPanel(), BorderLayout.CENTER);
		return contentPanel;
	}

	/**
	 * 查询条件设置面板 panel 提供薪资类别条件
	 * 
	 * @return BillCardPanel 查询条件设置面板
	 */
	public BillCardPanel getBillCardPanel() {

		if (billCardPanel != null) {
			return billCardPanel;
		}
		billCardPanel = new BillCardPanel();
		// 设置BillCardPanel大小
		billCardPanel.setPreferredSize(new Dimension(400, 70));

		// 初始化BillCardPanel
		BillData billData = new BillData(getBillTempletVO());
		billCardPanel.setBillData(billData);

		billCardPanel.setHeadItem("pk_org", waContext.getPk_org());
		BillItem bitem = billCardPanel.getHeadItem("pk_wa_class");
		if (null != bitem.getComponent() && bitem.getComponent() instanceof UIRefPane) {
			UIRefPane refPane = (UIRefPane) bitem.getComponent();
			refPane.setButtonFireEvent(true);
			WaClassRefModel refModel = (WaClassRefModel) refPane.getRefModel();
			if (null != refModel) {
				refModel.setPk_group(waContext.getPk_group());
				refModel.setPk_org(waContext.getPk_org());
			}
		}
		bitem.setValue(waContext.getClassPK());
		bitem = billCardPanel.getHeadItem("cperiod");
		if (null != bitem.getComponent() && bitem.getComponent() instanceof UIRefPane) {
			UIRefPane refPane = (UIRefPane) bitem.getComponent();
			refPane.setButtonFireEvent(true);
			refPane.setRefModel(new WaPeriodRefTreeModel(waContext.getClassPK()));
		}
		bitem.setValue(waContext.getCyear() + waContext.getCperiod());

		bitem = billCardPanel.getHeadItem("pk_classitem");
		if (null != bitem.getComponent() && bitem.getComponent() instanceof UIRefPane) {
			UIRefPane refPane = (UIRefPane) bitem.getComponent();
			refPane.setButtonFireEvent(true);
			WaClassItemRefModel refModel = (WaClassItemRefModel) refPane.getRefModel();
			if (null != refModel) {
				StringBuilder otherCondition = new StringBuilder();
				if (isItemFilter) {
					otherCondition.append("wa_classitem.def1 in ( select bd_defdoc.pk_defdoc from bd_defdoclist ");
					otherCondition
							.append("inner join bd_defdoc on bd_defdoclist.pk_defdoclist=bd_defdoc.pk_defdoclist ");
					otherCondition.append("where bd_defdoclist.code='WITSCS' and bd_defdoc.code='B' ) ");
				}
				refModel.setPk_org(waContext.getPk_org());
				refModel.setPk_wa_class(waContext.getClassPK());
				refModel.setPeriod(waContext.getCyear() + waContext.getCperiod());
				refModel.setOtherConditon(otherCondition.toString());
			}
		}

		billCardPanel.setBillBeforeEditListenerHeadTail(new BillCardBeforeEditListener() {
			@Override
			public boolean beforeEdit(BillItemEvent evt) {
				return true;
			}

		});
		// 添加监听器
		billCardPanel.addBillEditListenerHeadTail(new BillEditListener() {
			public void afterEdit(BillEditEvent evt) {

			}

			public void bodyRowChange(BillEditEvent e) {

			}
		});
		return billCardPanel;
	}

	/**
	 * 初始化BillCardPanel
	 * 
	 * @return BillTempletVO
	 */
	protected BillTempletVO getBillTempletVO() {
		ArrayList<BillTempletBodyVO> arrListBodyVO = new ArrayList<BillTempletBodyVO>();
		arrListBodyVO.addAll(getCustomTempletVO());
		BillTempletVO billTempletVO = new BillTempletVO();
		billTempletVO.setChildrenVO(arrListBodyVO.toArray(new BillTempletBodyVO[0]));

		return billTempletVO;
	}

	/**
	 * 设置个性化的BillCardPanel
	 * 
	 * @return List<BillTempletBodyVO>
	 */
	protected List<BillTempletBodyVO> getCustomTempletVO() {
		int iShowOrder = 0;

		ArrayList<BillTempletBodyVO> arrListBodyVO = new ArrayList<BillTempletBodyVO>();

		// 主表信息
		BillTempletBodyVO templetBodyVO = new BillTempletBodyVO();
		templetBodyVO.setCardflag(true);
		templetBodyVO.setEditflag(true);

		templetBodyVO.setPos(IBillItem.HEAD);
		templetBodyVO.setItemtype(0);
		templetBodyVO.setWidth(1);
		templetBodyVO.setList(false);
		templetBodyVO.setListflag(false);
		templetBodyVO.setListshowflag(Boolean.TRUE);
		templetBodyVO.setShoworder(iShowOrder++);
		templetBodyVO.setShowflag(true);
		templetBodyVO.setNullflag(true);

		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("projsalary", "2pjsalary-00002")); // 人力资源组织
		templetBodyVO.setItemkey("pk_org");
		templetBodyVO.setReftype("<nc.ui.org.ref.HROrgDefaultRefModel>");
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setNullflag(true);
		templetBodyVO.setEditflag(false);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("projsalary", "2pjsalary-00009")); // 薪资方案
		templetBodyVO.setItemkey("pk_wa_class");
		templetBodyVO.setReftype("<nc.ui.wa.ref.WaClassRefModel>");
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setNullflag(true);
		templetBodyVO.setEditflag(false);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("projsalary", "2pjsalary-00005")); // 薪资期间
		templetBodyVO.setItemkey("cperiod");
		templetBodyVO.setReftype("<nc.ui.wa.ref.WaPeriodRefTreeModel>");
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setNullflag(true);
		templetBodyVO.setEditflag(false);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("common", "2UC000-000053")); // 人员编码
		templetBodyVO.setItemkey("psncode");
		templetBodyVO.setDatatype(IBillItem.STRING);
		templetBodyVO.setNullflag(false);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("common", "UC000-0001403")); // 姓名
		templetBodyVO.setItemkey("psnname");
		templetBodyVO.setDatatype(IBillItem.MULTILANGTEXT);
		templetBodyVO.setNullflag(false);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("projsalary", "2pjsalary-00004")); // 专案代码
		templetBodyVO.setItemkey("pk_project");
		templetBodyVO.setReftype("指定專案代碼維護(自定义档案)");
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setNullflag(false);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("projsalary", "2pjsalary-00010")); // 薪资项目
		templetBodyVO.setItemkey("pk_classitem");
		templetBodyVO.setReftype("<nc.ui.wa.ref.WaClassItemRefModel>");
		templetBodyVO.setDatatype(IBillItem.UFREF);
		templetBodyVO.setNullflag(false);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("projsalary", "0pjsalary-00002")); // 创建时间起
		templetBodyVO.setItemkey("startDate");
		templetBodyVO.setDatatype(IBillItem.DATE);
		templetBodyVO.setNullflag(false);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setShowflag(false);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		templetBodyVO = (BillTempletBodyVO) templetBodyVO.clone();
		templetBodyVO.setDefaultshowname(ResHelper.getString("projsalary", "0pjsalary-00003")); // 创建时间止
		templetBodyVO.setItemkey("endDate");
		templetBodyVO.setDatatype(IBillItem.DATE);
		templetBodyVO.setNullflag(false);
		templetBodyVO.setEditflag(true);
		templetBodyVO.setShowflag(false);
		templetBodyVO.setShoworder(iShowOrder++);
		arrListBodyVO.add(templetBodyVO);

		return arrListBodyVO;
	}

	public ProjSalaryQryVO getResultObject() {
		ProjSalaryQryVO qryConditionVO = new ProjSalaryQryVO();
		qryConditionVO.setWaContext(waContext);
		qryConditionVO.setPsncode((String) billCardPanel.getHeadItem("psncode").getValueObject());
		UIMultiLangCombox multiLangText = (UIMultiLangCombox) billCardPanel.getHeadItem("psnname").getComponent();
		if (null != multiLangText && null != multiLangText.getMultiLangText()) {
			qryConditionVO.setPsnname(multiLangText.getMultiLangText().getText());
			qryConditionVO.setPsnname2(multiLangText.getMultiLangText().getText2());
			qryConditionVO.setPsnname3(multiLangText.getMultiLangText().getText3());
		}
		qryConditionVO.setPk_project((String) billCardPanel.getHeadItem("pk_project").getValueObject());
		qryConditionVO.setPk_classitem((String) billCardPanel.getHeadItem("pk_classitem").getValueObject());
		qryConditionVO.setStartDate((UFDate) billCardPanel.getHeadItem("startDate").getValueObject());
		qryConditionVO.setEndDate((UFDate) billCardPanel.getHeadItem("endDate").getValueObject());

		return qryConditionVO;
	}

}
