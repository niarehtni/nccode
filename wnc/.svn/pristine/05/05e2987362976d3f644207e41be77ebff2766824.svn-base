package nc.ui.wa.paydata.view.dialog;

import java.awt.Color;
import java.awt.Container;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.wa.paydata.model.WadataAppDataModel;
import nc.ui.wa.paydata.model.WadataModelService;
import nc.ui.wa.paydata.model.WadataTemplateContainer;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.paydata.ColoredCell;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.paydata.ICommonAlterName;
import nc.vo.wa.paydata.WaClassItemShowInfVO;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

/**
 * 明细对比
 * 
 * @author: zhangg
 * @date: 2009-12-22 上午09:10:28
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WadataContrastDialog extends HrDialog implements ICommonAlterName {
	private static final long serialVersionUID = 1L;
	private BillListPanel billListPanel;

	private WadataTemplateContainer templateContainer = null;

	private String errorMsg = null;

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * 为其他界面提供的构造方法
	 * 
	 * @author zhangg on 2009-12-23
	 * @param parent
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @throws BusinessException
	 */
	public WadataContrastDialog(Container parent, LoginContext context,
			String pk_wa_class, String cyear, String cperiod)
			throws BusinessException {
		super(parent, false);
		this.templateContainer = getDefaulTemplateContainer(context,
				pk_wa_class, cyear, cperiod);
		getBtnCancel().setVisible(false);
	}

	/**
	 * @author zhangg on 2009-12-23
	 * @param parent
	 * @param templateContainer
	 */
	public WadataContrastDialog(Container parent,
			WadataTemplateContainer templateContainer) {
		super(parent, false);
		this.templateContainer = templateContainer;
		getBtnCancel().setVisible(false);

	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2011-4-2 11:21:14<br>
	 * 
	 * @see nc.ui.hr.frame.dialog.HrDialog#createCenterPanel()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	protected JComponent createCenterPanel() {
		billListPanel = new BillListPanel();
		BillTempletVO templetVO = templateContainer.getDefualtTemplate();
		// 显示期间
		BillTempletBodyVO[] bodyVOs = templetVO.getBodyVO();
		for (BillTempletBodyVO billTempletBodyVO : bodyVOs) {
			if (billTempletBodyVO.getItemkey().equals(DataVO.CYEAR)) {
				billTempletBodyVO.setListshowflag(true);
				billTempletBodyVO.setShowflag(true);
			}
			if (billTempletBodyVO.getItemkey().equals(DataVO.CPERIOD)) {
				billTempletBodyVO.setListshowflag(true);
				billTempletBodyVO.setShowflag(true);
			}
		}
		billListPanel.setListData(new BillListData(templetVO));

		return billListPanel;
	}

	/**
	 * @author zhangg on 2009-12-23
	 * @return the billListPanel
	 */
	public BillListPanel getBillListPanel() {
		if (billListPanel == null) {
			createCenterPanel();
		}
		return billListPanel;
	}

	public BillScrollPane getBodyPanel() {
		return getBillListPanel().getParentListPanel();
	}

	@Override
	public UIPanel getCenterPanel() {
		return getBillListPanel();
	}

	public List<ColoredCell> getColoredCell() {
		List<ColoredCell> list = new LinkedList<ColoredCell>();
		HashMap<String, ColoredCell> tableHashMap = new HashMap<String, ColoredCell>();
		BillItem[] billItems = getBodyPanel().getTableModel().getBodyItems();
		for (int i = 0; i < getBodyPanel().getTableModel().getRowCount(); i++) {
			String psncode = getBodyPanel().getTableModel()
					.getValueAt(i, CLERKCODE).toString();
			for (BillItem billItem : billItems) {
				String key = billItem.getKey();
				Object obj = getBodyPanel().getTableModel().getValueAt(i, key);
				String value = obj == null ? "" : obj.toString();
				ColoredCell cell = new ColoredCell();
				cell.setKey(key);
				cell.setRowIndex(i);
				cell.setValue(value);

				if (tableHashMap.containsKey(psncode + key)) {
					if (!tableHashMap.get(psncode + key).getValue()
							.equals(value)) {
						list.add(tableHashMap.get(psncode + key));
						list.add(cell);
					}
					tableHashMap.remove(psncode + key);

				} else {
					tableHashMap.put(psncode + key, cell);
				}
			}
		}
		Collection<ColoredCell> collection = tableHashMap.values();
		for (ColoredCell cell : collection) {
			list.add(cell);
		}
		return list;
	}

	/**
	 * @author zhangg on 2009-12-23
	 * @param context
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws BusinessException
	 */
	private WadataTemplateContainer getDefaulTemplateContainer(
			LoginContext context, String pk_wa_class, String cyear,
			String cperiod) throws BusinessException {
		WaLoginContext loginContext = new WaLoginContext();
		loginContext.setPk_org(context.getPk_org());
		loginContext.setPk_loginUser(context.getPk_loginUser());

		WaLoginVO loginVO = new WaLoginVO();
		loginVO.setPk_wa_class(pk_wa_class);
		PeriodStateVO periodStateVO = new PeriodStateVO();
		periodStateVO.setCyear(cyear);
		periodStateVO.setCperiod(cperiod);
		loginVO.setPeriodVO(periodStateVO);

		WadataModelService modelService = new WadataModelService();
		WadataAppDataModel appDataModel = new WadataAppDataModel();

		/**
		 * 设定该薪资方案下有多少薪资发放项目, 各个项目的展现顺序,是否展现!
		 */

		WaClassItemShowInfVO info = modelService
				.getWaClassItemShowInfVO(loginContext);
		appDataModel.setClassItemVOs(info.getWaClassItemVO());
		appDataModel.setWaPaydataDspVO(info.getWaPaydataDspVO());
		appDataModel.setService(modelService);

		WadataTemplateContainer container = new WadataTemplateContainer();
		container.setContext(loginContext);
		container.setPaydataModel(appDataModel);
		return container;
	}

	@Override
	public String getTitle() {
		return ResHelper.getString("60130paydata", "060130paydata0413")/*
																		 * @res
																		 * "发放明细对比"
																		 */;
	}

	public void setColor() {
		List<ColoredCell> list = getColoredCell();
		for (ColoredCell coloredCell : list) {
			if (!coloredCell.getKey().equals(DataVO.CYEAR)
					&& !coloredCell.getKey().equals(DataVO.CPERIOD)) {
				getBodyPanel().setCellForeGround(coloredCell.getRowIndex(),
						coloredCell.getKey(), Color.BLUE);
			}
		}
		getBodyPanel().repaint();

	}

	@Override
	public void setValue(Object object) {

		getBillListPanel().setHeaderValueVO((DataVO[]) object);
	}

	public boolean setViewData() {
		try {
			DataVO[] dataVOs = templateContainer.getPaydataModel()
					.getContractDataVOs();
			// 2016-11-28 zhousze 薪资加密：处理明细对比薪资数据解密 begin
			dataVOs = SalaryDecryptUtil.decrypt4Array(dataVOs);
			// end
			setValue(dataVOs);
			setColor();
			return true;
		} catch (BusinessException e) {
			Logger.error(e);
			setErrorMsg(e.getMessage());
			return false;
		}
	}

	@Override
	public int showModal() {
		initUI();
		setErrorMsg(null);
		boolean result = setViewData();
		if (result) {
			return super.showModal();
		} else {
			return 1000;
		}
	}

}