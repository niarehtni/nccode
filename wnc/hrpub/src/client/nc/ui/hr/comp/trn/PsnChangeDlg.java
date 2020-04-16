package nc.ui.hr.comp.trn;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.trn.IhrtrnQBS;
import nc.ui.hr.frame.util.table.SelectableBillScrollPane;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.vo.hi.pub.CommonValue;
import nc.vo.hr.comp.trn.PsnChangeVO;
import nc.vo.hr.comp.trn.PsnTrnVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;

/**
 * 变动人员的抽象类， 主要提供了下列卡片 "新进人员" "离职人员" "变动人员" "兼职开始" "兼职结束" 变动人员
 * 
 * @author: liangxr
 * @date: 2010-2-2 13:04:30
 * @since: eHR V6.0
 */
public abstract class PsnChangeDlg extends UIDialog {

	private UFLiteralDate beginDate = null;
	private UIPanel bottomPanel = null;
	private JButton cancelButton = null;
	private LoginContext context = null;
	private UFLiteralDate endDate = null;
	private HashMap<Integer, SelectableBillScrollPane> hashMapPane = null;
	private HashMap<Integer, String> hashMapPaneTitle = null;
	private UIPanel jSplitPane = null;
	private JTabbedPane jTabbedPane = null;
	private JButton okButton = null;
	private PsnChangeVO selectedPks = null;
	private SelectableBillScrollPane TRA_ADD = null;

	private SelectableBillScrollPane TRA_SUB = null;
	private SelectableBillScrollPane TRN_PART_ADD = null;
	private SelectableBillScrollPane TRN_PART_SUB = null;
	private SelectableBillScrollPane TRN_POST_MOD = null;

	private String pk_usetype;
	private String pk_trnstype;
	private String pk_trnsreason;

	/**
	 * 附加条件是否存在 Created on 2008-10-22
	 * 
	 * @author zhangg
	 * @return
	 */
	public static boolean isExists(int trnType) {
		boolean isExists = true;
		if (trnType == CommonValue.TRN_ADD || trnType == CommonValue.TRN_PART_ADD || trnType == CommonValue.TRN_ALL_ADD) {
			isExists = false;
		} else if (trnType == CommonValue.TRN_SUB || trnType == CommonValue.TRN_PART_SUB
				|| trnType == CommonValue.TRN_POST_MOD) {
			isExists = true;
		}
		return isExists;
	}

	/**
	 * @param parent
	 */
	public PsnChangeDlg(LoginContext context, UFLiteralDate beginDate, UFLiteralDate endDate) {
		super(context.getEntranceUI());
		this.context = context;
		this.beginDate = beginDate;
		this.endDate = endDate;
		initialize();
	}

	public PsnChangeDlg(LoginContext context, UFLiteralDate beginDate, UFLiteralDate endDate, String pk_usetype,
			String pk_trnstype, String pk_trnsreason) {
		super(context.getEntranceUI());
		this.context = context;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.pk_usetype = pk_usetype;
		this.pk_trnstype = pk_trnstype;
		this.pk_trnsreason = pk_trnsreason;
		initialize();
	}

	/**
	 * 查询的限制条件 Created on 2008-5-4
	 * 
	 * @author zhangg
	 * @param trnType
	 * @return
	 */
	public abstract String getAddWhere(int trnType);

	private UIPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new UIPanel();
			java.awt.FlowLayout ivjUIPanel1FlowLayout = new java.awt.FlowLayout(FlowLayout.CENTER, 50, 10);
			bottomPanel.setLayout(ivjUIPanel1FlowLayout);
			bottomPanel.add(getOkButton(), getOkButton().getName());
			bottomPanel.add(getCancelButton(), getCancelButton().getName());
			bottomPanel.setPreferredSize(new java.awt.Dimension(0, 50));

		}
		return bottomPanel;
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			char cHotKey = 'C';
			cancelButton = new JButton();
			cancelButton.setPreferredSize(new Dimension(60, 20));
			cancelButton.setText(ResHelper.getString("6001comp", "06001comp0019")
			/* @res "关闭" */+ "(" + cHotKey + ")");
			cancelButton.setName("cancelButton");
			cancelButton.setMnemonic(cHotKey);
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setResult(nc.ui.pub.beans.UIDialog.ID_CANCEL);
					dispose();
				}
			});
		}
		return cancelButton;
	}

	public String[] getColKeyName(int trnType) {
		String[] keyname = null;
		if (trnType == CommonValue.TRN_POST_MOD) {
			keyname = new String[] { "isSelected", "clerkcode",
					"psnname",
					"psnclassname",
					// ssx added on 2019-12-21
					"trnstypename",
					// end
					"deptname", "postname", "postrank", "dept2name", "post2name", "postrank2", "psnid", "trndate",
					"pk_psndoc", "pk_org", "pk_dept", "pk_post", "pk_psncl", "pk_psnjob", "pk_psnorg", "workorg",
					"workorgvid", "workdept", "workdeptvid", "ismainjob", "assgid" };

		} else {
			keyname = new String[] { "isSelected", "clerkcode",
					"psnname",
					"psnclassname",
					// ssx added on 2019-12-21
					"trnstypename",
					// end
					"deptname", "postname", "psnid", "trndate", "pk_psndoc", "pk_org", "pk_dept", "pk_post",
					"pk_psncl", "pk_psnjob", "pk_psnorg", "workorg", "workorgvid", "workdept", "workdeptvid",
					"ismainjob", "assgid" };
		}
		return keyname;
	}

	/**
	 * @param trnType
	 * @return
	 */
	public String[] getColName(int trnType) {
		String[] colname = null;
		switch (trnType) {
		case CommonValue.TRN_ADD:
			colname = new String[] { ResHelper.getString("common", "UC000-0004044")
			/* @res "选择" */, ResHelper.getString("6001comp", "06001comp0021")
			/* @res "员工号" */, ResHelper.getString("common", "UC000-0001403")
			/* @res "姓名" */,
					ResHelper.getString("common", "UC000-0000140")
					/* @res "人员类别" */, // ssx added on 2019-12-21
					ResHelper.getString("6007event", "26007event-000003")/*
																		 * @res
																		 * "异动类型"
																		 */,
					// end
					ResHelper.getString("common", "UC000-0004064")
					/* @res "部门" */, ResHelper.getString("common", "UC000-0001653")
					/* @res "岗位" */, ResHelper.getString("common", "UC000-0003914")
					/* @res "身份证号" */, ResHelper.getString("6001comp", "06001comp0022")
					/* @res "变动时间" */, ResHelper.getString("common", "UC000-0000131")
					/* @res "人员主键" */, ResHelper.getString("6001comp", "06001comp0023")
					/* @res "业务单元" */, "pk_dept", "pk_postdoc", "pk_psncl", "pk_psnjob", "pk_psnorg", "workorg",
					"workorgvid", "workdept", "workdeptvid", "ismainjob", "assgid" };

			break;
		case CommonValue.TRN_SUB:
			colname = new String[] { ResHelper.getString("common", "UC000-0004044")
			/* @res "选择" */, ResHelper.getString("6001comp", "06001comp0021")
			/* @res "员工号" */, ResHelper.getString("common", "UC000-0001403")
			/* @res "姓名" */, ResHelper.getString("common", "UC000-0000140")
			/* @res "人员类别" */, ResHelper.getString("6007event", "26007event-000003")
			/* @res"异动类型" */,
					ResHelper.getString("common", "UC000-0003054")// (33457)jimmy
																	// added on
																	// 2020-02-27
					/* @res "离职前部门" */, ResHelper.getString("common", "UC000-0003042")
					/* @res "离职前岗位" */, ResHelper.getString("common", "UC000-0003914")
					/* @res "身份证号" */, ResHelper.getString("common", "UC000-0003062")
					/* @res "离职时间" */, ResHelper.getString("common", "UC000-0000131")
					/* @res "人员主键" */, ResHelper.getString("6001comp", "06001comp0023")
					/* @res "业务单元" */, "pk_dept", "pk_postdoc", "pk_psncl", "pk_psnjob", "pk_psnorg", "workorg",
					"workorgvid", "workdept", "workdeptvid", "ismainjob", "assgid" };
			break;
		case CommonValue.TRN_POST_MOD:
			colname = new String[] { ResHelper.getString("common", "UC000-0004044")
			/* @res "选择" */, ResHelper.getString("6001comp", "06001comp0021")
			/* @res "员工号" */, ResHelper.getString("common", "UC000-0001403")
			/* @res "姓名" */, ResHelper.getString("common", "UC000-0000140")
			/* @res "人员类别" */,
					// ssx added on 2019-12-21
					ResHelper.getString("6007event", "26007event-000003")/*
																		 * @res
																		 * "异动类型"
																		 */,
					// end
					ResHelper.getString("6001comp", "06001comp0024")
					/* @res "变动前部门" */, ResHelper.getString("6001comp", "06001comp0025")
					/* @res "变动前岗位" */, ResHelper.getString("6001comp", "06001comp0026")
					/* @res "变动前岗位序列" */, ResHelper.getString("6001comp", "06001comp0027")
					/* @res "变动后部门" */, ResHelper.getString("6001comp", "06001comp0028")
					/* @res "变动后岗位" */, ResHelper.getString("6001comp", "06001comp0029")
					/* @res "变动后岗位序列" */, ResHelper.getString("common", "UC000-0003914")
					/* @res "身份证号" */, ResHelper.getString("6001comp", "06001comp0022")
					/* @res "变动时间" */, ResHelper.getString("common", "UC000-0000131")
					/* @res "人员主键" */, ResHelper.getString("6001comp", "06001comp0023")
					/* @res "业务单元" */, "pk_dept", "pk_postdoc", "pk_psncl", "pk_psnjob", "pk_psnorg", "workorg",
					"workorgvid", "workdept", "workdeptvid", "ismainjob", "assgid" };
			break;
		case CommonValue.TRN_PART_ADD:
			colname = new String[] { ResHelper.getString("common", "UC000-0004044")
			/* @res "选择" */, ResHelper.getString("6001comp", "06001comp0021")
			/* @res "员工号" */, ResHelper.getString("common", "UC000-0001403")
			/* @res "姓名" */,
					ResHelper.getString("common", "UC000-0000140")
					/* @res "人员类别" */, // ssx added on 2019-12-21
					ResHelper.getString("6007event", "26007event-000003")/*
																		 * @res
																		 * "异动类型"
																		 */,
					// end
					ResHelper.getString("common", "UC000-0000435")
					/* @res "兼职部门" */, ResHelper.getString("common", "UC000-0000434")
					/* @res "兼职岗位" */, ResHelper.getString("common", "UC000-0003914")
					/* @res "身份证号" */, ResHelper.getString("6001comp", "06001comp0022")
					/* @res "变动时间" */, ResHelper.getString("common", "UC000-0000131")
					/* @res "人员主键" */, ResHelper.getString("6001comp", "06001comp0023")
					/* @res "业务单元" */, "pk_dept", "pk_postdoc", "pk_psncl", "pk_psnjob", "pk_psnorg", "workorg",
					"workorgvid", "workdept", "workdeptvid", "ismainjob", "assgid" };
			break;
		case CommonValue.TRN_PART_SUB:
			colname = new String[] { ResHelper.getString("common", "UC000-0004044")
			/* @res "选择" */, ResHelper.getString("6001comp", "06001comp0021")
			/* @res "员工号" */, ResHelper.getString("common", "UC000-0001403")
			/* @res "姓名" */,
					ResHelper.getString("common", "UC000-0000140")
					/* @res "人员类别" */, // ssx added on 2019-12-21
					ResHelper.getString("6007event", "26007event-000003")/*
																		 * @res
																		 * "异动类型"
																		 */,
					// end
					ResHelper.getString("common", "UC000-0000435")
					/* @res "兼职部门" */, ResHelper.getString("common", "UC000-0000434")
					/* @res "兼职岗位" */, ResHelper.getString("common", "UC000-0003914")
					/* @res "身份证号" */, ResHelper.getString("6001comp", "06001comp0022")
					/* @res "变动时间" */, ResHelper.getString("common", "UC000-0000131")
					/* @res "人员主键" */, ResHelper.getString("6001comp", "06001comp0023")
					/* @res "业务单元" */, "pk_dept", "pk_postdoc", "pk_psncl", "pk_psnjob", "pk_psnorg", "workorg",
					"workorgvid", "workdept", "workdeptvid", "ismainjob", "assgid" };
			break;
		}

		return colname;
	}

	public int[] getColType(int trnType) {
		if (trnType == CommonValue.TRN_POST_MOD) {
			return new int[] {
					IBillItem.BOOLEAN,
					IBillItem.STRING,
					IBillItem.STRING,
					IBillItem.STRING,
					IBillItem.STRING,
					// ssx added on 2019-12-21 for trnstype
					IBillItem.STRING,
					// emd
					IBillItem.STRING, IBillItem.STRING, IBillItem.STRING, IBillItem.STRING, IBillItem.STRING,
					IBillItem.STRING, IBillItem.LITERALDATE, IBillItem.STRING, IBillItem.STRING, IBillItem.STRING,
					IBillItem.STRING, IBillItem.STRING, IBillItem.STRING, IBillItem.STRING, IBillItem.STRING,
					IBillItem.STRING, IBillItem.STRING, IBillItem.STRING, IBillItem.BOOLEAN, IBillItem.INTEGER };

		}

		return new int[] { IBillItem.BOOLEAN,
				IBillItem.STRING,
				IBillItem.STRING,
				IBillItem.STRING,
				IBillItem.STRING,
				// ssx added on 2019-12-21 for trnstype
				IBillItem.STRING,
				// emd
				IBillItem.STRING, IBillItem.STRING, IBillItem.LITERALDATE, IBillItem.STRING, IBillItem.STRING,
				IBillItem.STRING, IBillItem.STRING, IBillItem.STRING, IBillItem.STRING, IBillItem.STRING,
				IBillItem.STRING, IBillItem.STRING, IBillItem.STRING, IBillItem.STRING, IBillItem.BOOLEAN,
				IBillItem.INTEGER };
	}

	public LoginContext getContext() {
		return context;
	}

	/**
	 * 初始化需要增加的卡片 Created on 2008-5-4
	 * 
	 * @author zhangg
	 * @return
	 */
	public abstract Integer[] getInitTabs();

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
				@Override
				public void stateChanged(javax.swing.event.ChangeEvent e) {

					JTabbedPane tabbedPane = (JTabbedPane) e.getSource();

					SelectableBillScrollPane billScrollPane = (SelectableBillScrollPane) tabbedPane
							.getSelectedComponent();
					int trnType = getSelectedKey(billScrollPane);
					refreshData(trnType, billScrollPane);

					// 存在的删除， 不存在的增加
					String text = isExists(trnType) ? ResHelper.getString("common", "UC001-0000039")
					/* @res "删除" */: ResHelper.getString("common", "UC001-0000002")
					/* @res "增加" */;
					char cHotKey = isExists(trnType) ? 'D'/* "删除" */
					: 'N'/* "增加" */;
					getOkButton().setText(text + "(" + cHotKey + ")");
					getOkButton().setMnemonic(cHotKey);
				}
			});
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	// private JSplitPane getJSplitPane()
	// {
	// if (jSplitPane == null)
	// {
	// jSplitPane = new JSplitPane();
	// jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
	// jSplitPane.setDividerLocation(420);
	// jSplitPane.setBottomComponent(getBottomPanel());
	// jSplitPane.setTopComponent(getJTabbedPane());
	// jSplitPane.setDividerSize(1);
	// }
	// return jSplitPane;
	// }

	private UIPanel getMainUIPane() {
		if (jSplitPane == null) {
			jSplitPane = new UIPanel();
			jSplitPane.setName("jSplitPane");
			jSplitPane.setLayout(new java.awt.BorderLayout());
			jSplitPane.add(getBottomPanel(), "South");
			jSplitPane.add(getJTabbedPane(), "Center");

		}
		return jSplitPane;
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			char cHotKey = 'Y';
			okButton = new JButton();
			okButton.setName("okButton");
			okButton.setText(ResHelper.getString("common", "UC001-0000044")
			/* @res "确定" */+ "(" + cHotKey + ")");
			okButton.setPreferredSize(new Dimension(60, 20));
			okButton.setMnemonic(cHotKey);
			okButton.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {

					try {
						SelectableBillScrollPane billScrollPane = (SelectableBillScrollPane) getJTabbedPane()
								.getSelectedComponent();
						int state = getSelectedKey(billScrollPane);
						List<PsnTrnVO> list = getSelPsnInf(billScrollPane);
						selectedPks = new PsnChangeVO();
						selectedPks.setState(state);
						selectedPks.setPsnVOlist(list);

						setResult(nc.ui.pub.beans.UIDialog.ID_OK);
						dispose();
					} catch (ValidationException ex) {
						MessageDialog.showErrorDlg(context.getEntranceUI(), null, ex.getMessage());

						Logger.error(ex.getMessage(), ex);
					}
				}
			});
		}
		return okButton;
	}

	/**
	 * @return
	 */

	/**
	 * @param billScrollPane
	 * @return
	 */
	private Integer getSelectedKey(SelectableBillScrollPane billScrollPane) {
		Iterator<Integer> iterator = hashMapPane.keySet().iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			if (hashMapPane.get(key).equals(billScrollPane)) {
				return key;
			}
		}
		return 0;
	}

	/**
	 * @return
	 */
	public PsnChangeVO getSelectedPks() {
		return selectedPks;
	}

	/**
	 * 获得选中人员信息。 创建日期：(2007-4-18 19:43:08)
	 * 
	 * @throws ValidationException
	 * @throws ValidationException
	 */
	private List<PsnTrnVO> getSelPsnInf(SelectableBillScrollPane billScrollPane) throws ValidationException {

		List<PsnTrnVO> list = new ArrayList<PsnTrnVO>();

		PsnTrnVO[] generalVOs = (PsnTrnVO[]) billScrollPane.getSelectedBodyVOs(PsnTrnVO.class);
		if (generalVOs != null) {

			for (PsnTrnVO generalVO : generalVOs) {

				if (personIsInList(list, generalVO)) {
					throw new ValidationException(ResHelper.getString("6001comp", "06001comp0030")
					/* @res "有重复的人员。 请修改。" */);
				}
				list.add(generalVO);
			}

		}
		return list;
	}

	/**
	 * This method initializes TRA_ADD
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private SelectableBillScrollPane getTRA_ADD() {
		if (TRA_ADD == null) {
			TRA_ADD = new SelectableBillScrollPane();
			// 20151214 shenliangc NCdp205559130
			// 薪资规则，新增一行后，填入数据用回车换行，第二行行号为空，保存时报错
			// 去掉回车增行、子表表体右键。
			TRA_ADD.setAutoAddLine(false);
		}
		return TRA_ADD;
	}

	/**
	 * This method initializes TRA_SUB
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private SelectableBillScrollPane getTRA_SUB() {
		if (TRA_SUB == null) {
			TRA_SUB = new SelectableBillScrollPane();
			// 20151214 shenliangc NCdp205559130
			// 薪资规则，新增一行后，填入数据用回车换行，第二行行号为空，保存时报错
			// 去掉回车增行、子表表体右键。
			TRA_SUB.setAutoAddLine(false);
		}
		return TRA_SUB;
	}

	/**
	 * This method initializes TRN_PART_ADD
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private SelectableBillScrollPane getTRN_PART_ADD() {
		if (TRN_PART_ADD == null) {
			TRN_PART_ADD = new SelectableBillScrollPane();
			// 20151214 shenliangc NCdp205559130
			// 薪资规则，新增一行后，填入数据用回车换行，第二行行号为空，保存时报错
			// 去掉回车增行、子表表体右键。
			TRN_PART_ADD.setAutoAddLine(false);
		}
		return TRN_PART_ADD;
	}

	/**
	 * This method initializes TRN_PART_SUB
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private SelectableBillScrollPane getTRN_PART_SUB() {
		if (TRN_PART_SUB == null) {
			TRN_PART_SUB = new SelectableBillScrollPane();
			// 20151214 shenliangc NCdp205559130
			// 薪资规则，新增一行后，填入数据用回车换行，第二行行号为空，保存时报错
			// 去掉回车增行、子表表体右键。
			TRN_PART_SUB.setAutoAddLine(false);
		}
		return TRN_PART_SUB;
	}

	/**
	 * This method initializes TRN_POST_MOD
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private SelectableBillScrollPane getTRN_POST_MOD() {
		if (TRN_POST_MOD == null) {
			TRN_POST_MOD = new SelectableBillScrollPane();
			// 20151214 shenliangc NCdp205559130
			// 薪资规则，新增一行后，填入数据用回车换行，第二行行号为空，保存时报错
			// 去掉回车增行、子表表体右键。
			TRN_POST_MOD.setAutoAddLine(false);
		}
		return TRN_POST_MOD;
	}

	/**
	 * 初始化需要加载那些卡片 Created on 2008-5-4
	 * 
	 * @author zhangg
	 */
	private void initHashMap() {
		if (hashMapPane == null) {
			hashMapPane = new LinkedHashMap<Integer, SelectableBillScrollPane>();
			Integer[] tabs = getInitTabs();
			for (Integer tab : tabs) {
				switch (tab) {
				case CommonValue.TRN_ADD:
					hashMapPane.put(CommonValue.TRN_ADD, getTRA_ADD());
					break;
				case CommonValue.TRN_SUB:
					hashMapPane.put(CommonValue.TRN_SUB, getTRA_SUB());
					break;
				case CommonValue.TRN_PART_ADD:
					hashMapPane.put(CommonValue.TRN_PART_ADD, getTRN_PART_ADD());
					break;
				case CommonValue.TRN_PART_SUB:
					hashMapPane.put(CommonValue.TRN_PART_SUB, getTRN_PART_SUB());
					break;
				case CommonValue.TRN_POST_MOD:
					hashMapPane.put(CommonValue.TRN_POST_MOD, getTRN_POST_MOD());
					break;
				default:
					break;
				}
			}

		}
		if (hashMapPaneTitle == null) {

			hashMapPaneTitle = new LinkedHashMap<Integer, String>();
			Integer[] tabs = getInitTabs();
			for (Integer tab : tabs) {
				switch (tab) {
				case CommonValue.TRN_ADD:
					hashMapPaneTitle.put(CommonValue.TRN_ADD, ResHelper.getString("6001comp", "06001comp0031")
					/* @res "新进人员" */);//
					break;
				case CommonValue.TRN_SUB:
					hashMapPaneTitle.put(CommonValue.TRN_SUB, ResHelper.getString("6001comp", "06001comp0032")
					/* @res "离职人员" */);//
					break;
				case CommonValue.TRN_PART_ADD:
					hashMapPaneTitle.put(CommonValue.TRN_PART_ADD, ResHelper.getString("6001comp", "06001comp0033")
					/* @res "兼职开始" */);//
					break;
				case CommonValue.TRN_PART_SUB:
					hashMapPaneTitle.put(CommonValue.TRN_PART_SUB, ResHelper.getString("6001comp", "06001comp0034")
					/* @res "兼职结束" */);//
					break;
				case CommonValue.TRN_POST_MOD:
					hashMapPaneTitle.put(CommonValue.TRN_POST_MOD, ResHelper.getString("6001comp", "06001comp0035")
					/* @res "变动人员" */);//
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		setSize(new Dimension(1109, 518));
		setTitle(ResHelper.getString("6001comp", "06001comp0036")
		/* @res "人员变化提示" */);//
		setContentPane(getMainUIPane());
		setResizable(true);
		initHashMap();
		Iterator<Integer> iterator = hashMapPane.keySet().iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			setTable(key, hashMapPane.get(key));
			getJTabbedPane().addTab(hashMapPaneTitle.get(key), hashMapPane.get(key));

		}
	}

	/**
	 * 初始化表体显示。 创建日期：(2007-4-18 10:57:21)
	 * 
	 * @return nc.ui.pub.bill.BillModel
	 */
	private void initTable(int trnType, SelectableBillScrollPane billScrollPane) {

		BillItem[] abillBody = new BillItem[getColName(trnType).length];
		for (int i = 0; i < getColName(trnType).length; i++) {
			abillBody[i] = new BillItem();
			abillBody[i].setName(getColName(trnType)[i]);
			abillBody[i].setKey(getColKeyName(trnType)[i]);
			abillBody[i].setDataType(getColType(trnType)[i]);
			abillBody[i].setWidth(60);
			// 设置显示格式
			if (i == 0) {
				abillBody[i].setEnabled(true);
				abillBody[i].setEdit(true);// true
				abillBody[i].setLength(50);
				((UICheckBox) abillBody[i].getComponent()).setHorizontalAlignment(SwingConstants.CENTER);
			} else if (trnType == CommonValue.TRN_POST_MOD && i > 12 || CommonValue.TRN_POST_MOD != trnType && i > 8) {
				abillBody[i].setEnabled(false);
				abillBody[i].setShow(false);
			} else {
				abillBody[i].setEnabled(false);
				abillBody[i].setEdit(false);
				abillBody[i].setNull(false);
			}
		}
		BillModel billModel = new BillModel();
		billModel.setBodyItems(abillBody);
		billScrollPane.setTableModel(billModel);
		billScrollPane.setSelectRowCode("isSelected");
		billScrollPane.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

	}

	/**
	 * 判断是否重复 Created on 2008-5-4
	 * 
	 * @author zhangg
	 * @param list
	 * @param psnVO
	 * @return
	 */
	private boolean personIsInList(List<PsnTrnVO> list, PsnTrnVO psnVO) {
		boolean isInList = false;
		for (PsnTrnVO psnVO2 : list) {
			if (psnVO2.getAttributeValue("pk_psndoc").toString()
					.equals(psnVO.getAttributeValue("pk_psndoc").toString())) {
				return true;
			}

		}

		return isInList;
	}

	/**
	 * @param trnType
	 * @param billScrollPane
	 */
	private void refreshData(int trnType, SelectableBillScrollPane billScrollPane) {

		try {
			IhrtrnQBS hrtrnQBS = NCLocator.getInstance().lookup(IhrtrnQBS.class);
			PsnTrnVO[] hrMainVOs = hrtrnQBS.queryTRNPsnInf(context.getPk_org(), beginDate, endDate, trnType,
					getAddWhere(trnType));

			billScrollPane.getTableModel().setBodyDataVO(hrMainVOs);
			billScrollPane.selectAllRows();

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showErrorDlg(context.getEntranceUI(), null, e.getMessage());
		}

	}

	/**
	 * @param trnType
	 * @param billScrollPane
	 */

	private void setTable(int trnType, SelectableBillScrollPane billScrollPane) {
		initTable(trnType, billScrollPane);

	}

	public String getPk_usetype() {
		return pk_usetype;
	}

	public void setPk_usetype(String pk_usetype) {
		this.pk_usetype = pk_usetype;
	}

	public String getPk_trnstype() {
		return pk_trnstype;
	}

	public void setPk_trnstype(String pk_trnstype) {
		this.pk_trnstype = pk_trnstype;
	}

	public String getPk_trnsreason() {
		return pk_trnsreason;
	}

	public void setPk_trnsreason(String pk_trnsreason) {
		this.pk_trnsreason = pk_trnsreason;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
