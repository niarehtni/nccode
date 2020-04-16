package nc.ui.wa.payfile.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.StringUtils;

/**
 * 选择薪资期间段
 * 
 * @author: liangxr
 * @date: 2010-2-2 下午03:05:29
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WNCPeriodDialog extends UIDialog {

	private static final long serialVersionUID = -2667254291965299081L;
	private UIPanel contentPanel = null;
	private UILabel lblBeginDate = null;
	private UILabel lblEndDate = null;
	private UIRefPane startDate = null;
	private UIRefPane endDate = null;
	private UIButton bnOK = null;
	private UIButton bnCancel = null;
	private Frame parent = null;
	private String[] date = null;
	private final Dimension DIME_DATE = new Dimension(110, 20);
	private final Dimension DIME_BTN = new Dimension(60, 20);
	private UILabel lblUseType = null;
	private UIRefPane refUseType = null;
	private UILabel lblTrnsType = null;
	private UIRefPane refTrnsType = null;
	private UILabel lblTrnsReason = null;
	private UIRefPane refTrnsReason = null;
	private String pk_trnstype;
	private String pk_usetype;
	private String pk_trnsreason;

	/**
	 * @param parent
	 */
	public WNCPeriodDialog(Frame owner, String title) {
		super(owner, title);
		this.parent = owner;
	}

	/**
	 * This method initializes this
	 * 
	 */
	public void init(UFDate begindate, UFDate enddate) {
		this.setSize(new Dimension(460, 200));
		this.setContentPane(getJPanel());
		getStartDate().setText(begindate.toString());
		getEndDate().setText(enddate.toString());
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private UIPanel getJPanel() {
		if (contentPanel == null) {
			contentPanel = new UIPanel() {
				@Override
				public void paint(Graphics g) {
					super.paint(g);

					g.setColor(Color.GRAY);
					g.drawLine(0, 150, 480, 150);

					g.setColor(Color.WHITE);
					g.drawLine(0, 151, 480, 151);
				}
			};
			contentPanel.setLayout(null);
			// 开始日期
			contentPanel.add(getLblBeginDate());
			contentPanel.add(getStartDate());
			// 结束日期
			contentPanel.add(getLblEndDate());
			contentPanel.add(getEndDate());
			// 用工形式
			contentPanel.add(getLblUseType());
			contentPanel.add(getUseType());
			// 异动类别
			contentPanel.add(getLblTrnsType());
			contentPanel.add(getTrnsType());
			// 异动原因
			contentPanel.add(getLblTrnsReason());
			contentPanel.add(getTrnsReason());
			// 按钮
			contentPanel.add(getBnOK(), null);
			contentPanel.add(getBnCancel(), null);
		}

		return contentPanel;
	}

	private UILabel getLblTrnsReason() {
		if (lblTrnsReason == null) {
			lblTrnsReason = new UILabel();
			lblTrnsReason.setILabelType(UILabel.STYLE_DEFAULT);
			lblTrnsReason.setText(ResHelper.getString("6007psn", "26009psn-000102")); // 异动原因
			lblTrnsReason.setBounds(20, 100, 60, 25);
		}
		return lblTrnsReason;
	}

	private UILabel getLblTrnsType() {
		if (lblTrnsType == null) {
			lblTrnsType = new UILabel();
			lblTrnsType.setILabelType(UILabel.STYLE_DEFAULT);
			lblTrnsType.setText(ResHelper.getString("6007event", "26007event-000003")); // 异动类别
			lblTrnsType.setBounds(240, 60, 60, 25);
		}

		return lblTrnsType;
	}

	private UILabel getLblUseType() {
		if (lblUseType == null) {
			lblUseType = new UILabel();
			lblUseType.setILabelType(UILabel.STYLE_DEFAULT);
			lblUseType.setText("用工形式"); // 用工形式
			lblUseType.setBounds(20, 60, 60, 25);
		}

		return lblUseType;
	}

	private UILabel getLblEndDate() {
		if (lblEndDate == null) {
			lblEndDate = new UILabel();
			lblEndDate.setILabelType(UILabel.STYLE_NOTNULL);
			lblEndDate.setText(ResHelper.getString("common", "UC000-0003232")
			/* @res "结束时间" */);
			lblEndDate.setBounds(240, 20, 60, 25);
		}

		return lblEndDate;
	}

	private UILabel getLblBeginDate() {
		if (lblBeginDate == null) {
			lblBeginDate = new UILabel();
			lblBeginDate.setText(ResHelper.getString("common", "UC000-0001894")
			/* @res "开始时间" */);
			lblBeginDate.setILabelType(UILabel.STYLE_NOTNULL);
			lblBeginDate.setBounds(20, 20, 60, 25);
		}
		return lblBeginDate;
	}

	private UIRefPane getTrnsType() {
		if (refTrnsType == null) {
			refTrnsType = new UIRefPane("异动类型");
			refTrnsType.setRefNodeName("异动类型");
			refTrnsType.setBounds(300, 60, 122, 22);
		}
		return refTrnsType;
	}

	private UIRefPane getTrnsReason() {
		if (refTrnsReason == null) {
			refTrnsReason = new UIRefPane("异动原因(自定义档案)");
			refTrnsReason.setRefNodeName("异动原因(自定义档案)");
			refTrnsReason.setBounds(80, 100, 122, 22);
		}
		return refTrnsReason;
	}

	private UIRefPane getUseType() {
		if (refUseType == null) {
			refUseType = new UIRefPane("用工形式(自定义档案)");
			refUseType.setRefNodeName("用工形式(自定义档案)");
			refUseType.setMultiSelectedEnabled(true);
			refUseType.setBounds(80, 60, 122, 22);
		}
		return refUseType;
	}

	/**
	 * This method initializes startDate
	 * 
	 * @return javax.swing.JTextField
	 */
	private UIRefPane getStartDate() {
		if (startDate == null) {
			startDate = new UIRefPane();
			startDate.setBounds(80, 20, 122, 22);
			startDate.setSize(DIME_DATE);
			startDate.setRefNodeName("日历");
		}
		return startDate;
	}

	/**
	 * This method initializes endDate
	 * 
	 * @return javax.swing.JTextField
	 */
	private UIRefPane getEndDate() {
		if (endDate == null) {
			endDate = new UIRefPane();
			endDate.setRefNodeName("日历");
			endDate.setBounds(300, 20, 122, 22);
			endDate.setSize(DIME_DATE);
		}
		return endDate;
	}

	/**
	 * This method initializes bnOK
	 * 
	 * @return javax.swing.JButton
	 */
	private UIButton getBnOK() {
		if (bnOK == null) {
			char cHotKey = 'Y';
			bnOK = new UIButton();
			// bnOK.setSize(DIME_BTN);
			bnOK.setBounds(260, 163, 60, 24);
			bnOK.setText(ResHelper.getString("60130payfile", "060130payfile0276")/*
																				 * @
																				 * res
																				 * "确定("
																				 */+ cHotKey + ")");
			bnOK.setMnemonic(cHotKey);
			bnOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					date = new String[2];
					date[0] = getStartDate().getValueObj().toString();
					date[1] = getEndDate().getValueObj().toString();
					if (date[0] == null || date[0].length() != 19) {
						MessageDialog.showErrorDlg(parent, null,
								ResHelper.getString("60130payfile", "060130payfile0277")/*
																						 * @
																						 * res
																						 * "开始日期设置错误！"
																						 */);
						return;
					} else if (date[1] == null || date[1].length() != 19) {
						MessageDialog.showErrorDlg(parent, null,
								ResHelper.getString("60130payfile", "060130payfile0278")/*
																						 * @
																						 * res
																						 * "结束日期设置错误！"
																						 */);
						return;
					} else if (date[1].substring(0, 9).compareTo(date[0].substring(0, 9)) < 0) {
						MessageDialog.showErrorDlg(parent, null,
								ResHelper.getString("60130payfile", "060130payfile0279")/*
																						 * @
																						 * res
																						 * "开始日期要早于结束日期！"
																						 */);
						return;
					} else {
						setDate(date);
					}

					setPk_usetype(getUseType().getValueObj() == null ? "" : getInSQL(((String[]) getUseType()
							.getValueObj())));
					setPk_trnstype(getTrnsType().getValueObj() == null ? ""
							: ((String[]) getTrnsType().getValueObj())[0]);
					setPk_trnsreason(getTrnsReason().getValueObj() == null ? "" : ((String[]) getTrnsReason()
							.getValueObj())[0]);

					setResult(nc.ui.pub.beans.UIDialog.ID_OK);
					dispose();
				}

				private String getInSQL(String[] useTypes) {
					String inSql = "";
					if (useTypes.length == 0) {
						return "";
					} else {
						for (String useType : useTypes) {
							if (StringUtils.isEmpty(inSql)) {
								inSql = "'" + useType + "'";
							} else {
								inSql += ",'" + useType + "'";
							}
						}
					}
					return inSql;
				}
			});
		}
		return bnOK;
	}

	/**
	 * This method initializes bnCancel
	 * 
	 * @return javax.swing.JButton
	 */
	private UIButton getBnCancel() {
		if (bnCancel == null) {
			char cHotKey = 'C';
			bnCancel = new UIButton();
			bnCancel.setSize(DIME_BTN);
			bnCancel.setBounds(360, 163, 60, 24);
			bnCancel.setText(ResHelper.getString("60130payfile", "060130payfile0280")/*
																					 * @
																					 * res
																					 * "取消("
																					 */+ cHotKey + ")");
			bnCancel.setMnemonic(cHotKey);
			bnCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					setResult(nc.ui.pub.beans.UIDialog.ID_CANCEL);
					dispose();
				}
			});
		}
		return bnCancel;
	}

	public String[] getDate() {
		return date;
	}

	public void setDate(String[] date) {
		this.date = date;
	}

	public String getPk_trnstype() {
		return pk_trnstype;
	}

	public void setPk_trnstype(String pk_trnstype) {
		this.pk_trnstype = pk_trnstype;
	}

	public String getPk_usetype() {
		return pk_usetype;
	}

	public void setPk_usetype(String pk_usetype) {
		this.pk_usetype = pk_usetype;
	}

	public String getPk_trnsreason() {
		return pk_trnsreason;
	}

	public void setPk_trnsreason(String pk_trnsreason) {
		this.pk_trnsreason = pk_trnsreason;
	}

}