package nc.ui.wa.taxaddtional.view;

import java.awt.Container;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.ISalaryadjmgtConstant;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.hr.global.Global;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.IAppModel;
import nc.ui.wa.adjust.view.WaItemDecimalAdapter4DataImport;
import nc.ui.wa.pub.BannerTimerDialog;
import nc.ui.wa.taxaddtional.action.TaxExcelImportEHD;
import nc.ui.wa.taxaddtional.model.TaxaddtionalModel;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uap.busibean.exception.BusiBeanException;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.pub.ParaConstant;
import nc.vo.wa_tax.WASpecialAdditionaDeductionVO;

/**
 * 数据导入界面
 *
 * @author: xuhw
 * @since: eHR V6.5
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class DataImportDia extends HrDialog
{
	private static final long serialVersionUID = 1L;
	private static final String NODEKEY = "taxsdimp";
	private BillCardPanel billCardPanel;
	private Container parent = null;
	private LoginContext context = null;
	private TaxaddtionalModel appmodel;
	/** 按钮事件处理类 */
	private TaxExcelImportEHD eventHandler;
	/** 导入数据到数据库 */
	private UIButton btnOkInDB;
	private UIButton btnFinished;
	private String fileName;

	public DataImportDia(LoginContext loginContext, IAppModel model)
	{
		super(loginContext.getEntranceUI(), "专项附加费用扣除明细数据导入");
		this.parent = loginContext.getEntranceUI();
		this.appmodel = (TaxaddtionalModel) model;
		this.context = loginContext;
		int screenWidth = (int)(Toolkit.getDefaultToolkit().getScreenSize().width * 8.0 / 10.0);
	    int screenHeight = (int)(Toolkit.getDefaultToolkit().getScreenSize().height * 8.0 / 10.0);
		setSize(screenWidth, screenHeight);
		
		UFBoolean allowed = UFBoolean.FALSE;
		try {
			String pk_group = getContext().getPk_group();
			allowed = SysinitAccessor.getInstance().getParaBoolean(pk_group, ParaConstant.PARTJOB_ADJMGT);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		getBillCardPanel().showBodyTableCol(PsndocWadocVO.ASSGID);
		getBillCardPanel().showBodyTableCol(PsndocWadocVO.PARTFLAG);
		if (!allowed.booleanValue()) {
			getBillCardPanel().hideBodyTableCol(PsndocWadocVO.ASSGID);
			getBillCardPanel().hideBodyTableCol(PsndocWadocVO.PARTFLAG);
		}
		
		addDecimalListener();
		getBillCardPanel().getBodyPanel().setAutoAddLine(false);
		getBillCardPanel().getBodyPanel().setBBodyMenuShow(false);
	}

	@Override
	protected void initButton()
	{

		btnOkInDB = new UIButton(ResHelper.getString("60130adjmtc","060130adjmtc0207")/*@res "导入数据库"*/);
		btnOkInDB.addActionListener(this);
		btnOkInDB.setToolTipText(ResHelper.getString("60130adjmtc","060130adjmtc0208")/*@res "导入数据库(Crtl+Shift+I)"*/);
		getButtonPanel().add(btnOkInDB);

		btnCancel = new UIButton(ResHelper.getString("common","UC001-0000008")/*@res "取消"*/);
		btnCancel.addActionListener(this);
		btnCancel.setToolTipText(ResHelper.getString("60130adjmtc","060130adjmtc0209")/*@res "取消Crtl+Q"*/);
		getButtonPanel().add(btnCancel);

		btnFinished = new UIButton(ResHelper.getString("60130adjmtc","060130adjmtc0210")/*@res "完成"*/);
		btnFinished.addActionListener(this);
		btnFinished.setToolTipText(ResHelper.getString("60130adjmtc","060130adjmtc0211")/*@res "取消Alt+F"*/);
	}

	@Override
	protected void hotKeyPressed(KeyStroke hotKey, KeyEvent evt)
	{
		int iModifiers = hotKey.getModifiers();

		if (iModifiers == 0)
		{
			return;
		}

		boolean blCtrl = false;
		boolean blAlt = false;

		if ((iModifiers & Event.CTRL_MASK) != 0)
		{
			blCtrl = true;
		}
		if((iModifiers & Event.ALT_MASK) != 0)
		{
			blAlt = true;
		}

		try
		{
			 if ((iModifiers & Event.SHIFT_MASK) != 0 && blCtrl && hotKey.getKeyCode() == KeyEvent.VK_I && !appmodel.isFromHeadOut())
			{
				waitImport();
			}
			else if (blCtrl && hotKey.getKeyCode() == KeyEvent.VK_Q)
			{
				closeCancel();
				appmodel.setFromHeadOut(false);
			}
			else if (blAlt && hotKey.getKeyCode() == KeyEvent.VK_F)
			{
				closeOK();
				appmodel.setFromHeadOut(false);
			}
		}
		catch (Exception ex)
		{
			Logger.error(ex.getMessage(), ex);
			MessageDialog.showErrorDlg(parent, null, ex.getMessage());
			return;
		}
		finally
		{
			getBillCardPanel().getTailItem(PsndocWadocVO.IMPORTINFO).setValue(getEventHandler().getImporter().getDetailMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		try
		{
			 if (evt.getSource() == btnOkInDB)
			{
				waitImport();
			}
			else if (evt.getSource() == btnCancel)
			{
				closeCancel();
				appmodel.setFromHeadOut(false);
			}
			else if (evt.getSource() == btnFinished)
			{
				closeOK();
				appmodel.setFromHeadOut(false);
			}
		}
		catch (Exception ex)
		{
			Logger.error(ex.getMessage(), ex);
			MessageDialog.showErrorDlg(parent, null, ex.getMessage());
			return;
		}
		finally
		{
			getBillCardPanel().getTailItem(PsndocWadocVO.IMPORTINFO).setValue(getEventHandler().getImporter().getDetailMessage());
		}
	}

	private void waitImport()
	{
		new SwingWorker<Boolean, Void>()
		{
			BannerTimerDialog dialog = new BannerTimerDialog(parent);

			@Override
			protected Boolean doInBackground() throws Exception
			{
				try
				{
					dialog.setStartText(ResHelper.getString("60130adjmtc","060130adjmtc0212")/*@res "信息导入中，请稍等..."*/);
					dialog.start();
					// 以下方法中出现refresh方法有可能造成线程问题。
					getEventHandler().doImport2DB();
					if (getEventHandler().getErrorNum()==0) {
						closeOK();
						appmodel.setFromHeadOut(false);
					}
				}
				catch (Exception e)
				{
					throw new BusiBeanException(e.getMessage(),e);
				}
				finally
				{
					dialog.end();
				}
				return Boolean.TRUE;
			}

			@Override
			protected void done()
			{
				{
					getButtonPanel().removeAll();
					getButtonPanel().add(btnFinished);
					getButtonPanel().updateUI();
					ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("60130adjmtc","060130adjmtc0213",dialog.getSecond())/*@res "数据处理用时：{0}秒。"*/ 
							, appmodel.getContext());
				}
			}
		}.execute();

	}

	/**
	 * @author xuhw on 2010-4-8
	 * @see nc.ui.hr.frame.dialog.HrDialog#createCenterPanel()
	 */
	@Override
	protected JComponent createCenterPanel()
	{
		return getBatchAdjustPanel();
	}

	private BillCardPanel getBatchAdjustPanel()
	{
		if (billCardPanel == null)
		{
			billCardPanel = new BillCardPanel();
			billCardPanel.loadTemplet(NODEKEY, null, Global.getUserID(), Global.getGroupPK());
			billCardPanel.setEnabled(true);
		}
		return billCardPanel;
	}

	/**
	 * @return 按钮事件处理类
	 */
	private TaxExcelImportEHD getEventHandler()
	{
		if (eventHandler == null)
		{
			eventHandler = new TaxExcelImportEHD(this);
		}
		return eventHandler;
	}

	/**
	 * 取得界面上需要导入的VO数组
	 *
	 * @author xuhw on 2010-4-20
	 * @return
	 */
	public WASpecialAdditionaDeductionVO[] getBodyVOs()
	{
		return (WASpecialAdditionaDeductionVO[]) getBatchAdjustPanel().getBillModel().getBodyValueVOs(WASpecialAdditionaDeductionVO.class.getName());
	}

	/**
	 * 取得界面上需要导入的VO数组
	 *
	 * @author xuhw on 2010-4-20
	 * @return
	 */
	public void setBodyVOs(SuperVO[] superVOs)
	{
		getBillCardPanel().showBodyTableCol(PsndocWadocVO.REASON);
		getBatchAdjustPanel().getBillModel().setBodyDataVO(superVOs);
		
		for(int i=0;null!=superVOs&&i<superVOs.length;i++){
			for (int currentColumn = 0; currentColumn < 10; currentColumn++){
				BillItem billItem = getInputItems()[currentColumn];
				Object obj = getValue(billItem, superVOs[i].getAttributeValue(billItem.getKey()));
				getBatchAdjustPanel().getBillModel().setValueAt(obj, i, billItem.getKey());
			}

			
		}
		//getBatchAdjustPanel().getBillModel().setBodyDataVO(superVOs);
	}
	
	
	private Object getValue(BillItem billitem, Object sValue)
	{
		int dataType = billitem.getDataType();
		Object value = "";
		if (dataType != IBillItem.DECIMAL && (sValue == null || sValue.equals("")))
		{
			return null;
		}
		switch (dataType)
		{
			case IBillItem.DECIMAL:
				if (sValue == null || sValue.equals(""))
				{
					value = new UFDouble(0);
					break;
				}
				value = new UFDouble(sValue.toString());
				break;
			case IBillItem.STRING:
				value = sValue;
				// 由于从excls中读取的数据如果是字符类型的输入数字，会自动在加上.0结尾
				value = value.toString().replaceAll("\\.0", "");
				break;
			case IBillItem.BOOLEAN:
				value = UFBoolean.valueOf(sValue.toString());
				break;
			case IBillItem.DATE:
				value = new UFDate(sValue.toString());
				break;
			case IBillItem.LITERALDATE:
				value = new UFLiteralDate(sValue.toString());
				break;
			case IBillItem.UFREF:
					UIRefPane refpane = (UIRefPane) billitem.getComponent();
					Vector data = refpane.getRefModel().getData();
					refpane.getRefModel().matchData(refpane.getRefModel().getPkFieldCode(), (String) sValue);
					if(null != refpane.getRefModel().getPkValue()){
						value = new DefaultConstEnum(sValue, refpane.getRefModel().getRefNameValue());
					}
					
				
				break;
			default:
				value = sValue;
		}
		return value;
	}

	/**
	 * 设置小数位数监听
	 * 使子表每行的小数位数和薪资项目保持一致
	 */
	private void addDecimalListener(){
		String[] billitems = new String[]{PsndocWadocVO.NMONEY};
		String pk_group = this.getAppmodel().getContext().getPk_group();
		IBillModelDecimalListener2 bmd = new WaItemDecimalAdapter4DataImport(PsndocWadocVO.NMONEY, billitems,pk_group);
		getBillCardPanel().getBillModel().addDecimalListener(bmd);
	}
	/**
	 * @author xuhw on 2010-4-8
	 * @see nc.itf.trade.excelimport.IImportableEditor#getInputItems()
	 */
	public BillItem[] getInputItems()
	{
		return getBillCardPanel().getBillModel().getBodyItems();
	}

	public BillCardPanel getBillCardPanel()
	{
		return billCardPanel;
	}

	public void setBillCardPanel(BillCardPanel billCardPanel)
	{
		this.billCardPanel = billCardPanel;
	}

	public TaxaddtionalModel getAppmodel()
	{
		return appmodel;
	}

	public void setAppmodel(TaxaddtionalModel appmodel)
	{
		this.appmodel = appmodel;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	/** 取得表体VOName */
	public String getBodyVOName()
	{
		return getBillCardPanel().getBillData().getBillTempletVO().getEntityName();
	}

	public void setResultMessage(String strMess)
	{
		getBillCardPanel().getTailItem(PsndocWadocVO.IMPORTINFO).setValue(strMess);
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}
}