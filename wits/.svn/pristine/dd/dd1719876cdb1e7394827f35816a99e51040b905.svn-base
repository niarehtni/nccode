package nc.ui.wa.payfile.view;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.bd.psnbankacc.IPsnBankaccQueryService;
import nc.itf.hr.wa.IWaBmfileQueryService;
import nc.itf.org.IBasicOrgUnitQryService;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.wa.payfile.common.PayfileUtil;
import nc.ui.wa.payfile.model.PayfileAppModel;
import nc.ui.wa.payfile.model.PayfileModelDataManager;
import nc.ui.wa.payfile.refmodel.AlternatePsnRef;
import nc.ui.wa.payfile.refmodel.DeptPartPsnRef;
import nc.ui.wa.payfile.refmodel.NormalPsnRef;
import nc.ui.wa.payfile.refmodel.PersonType;
import nc.ui.wa.payfile.refmodel.RelationPsnRef;
import nc.ui.wa.pub.WADelegator;
import nc.ui.wabm.file.refmodel.CostDeptVersionRefModel;
import nc.vo.bd.psnbankacc.IPsnbankaccConst;
import nc.vo.bd.psnbankacc.PsnBankaccUnionVO;
import nc.vo.hr.pub.WaBmFileOrgVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.payfile.Taxtype;
import nc.vo.wa.payfile.WaPayfileDspVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.ArrayUtils;

/**
 * 薪资档案卡片
 * 
 * @author: zhoucx
 * @date: 2009-11-27 上午09:12:39
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class PayfileFormEditor extends HrBillFormEditor {

	private static final long serialVersionUID = 3560637576039586238L;
	private IPsnBankaccQueryService bankQuery;
	private IWaBmfileQueryService orgQuery;

	private UIRefPane psnRefPane;

	// private boolean isTaxTableMust = false;

	@Override
	public void initUI() {
		super.initUI();

	}
	//20150917 shenliangc NCdp205483210 begin
		//在“单据模板初始化”中隐藏了财务部门、财务组织、成本中心、成本部门、使用附加费用扣除额几个字段，使其在卡片和列表的情况下都不显示，
		//但卡片情况下隐藏生效，列表情况下未生效。
		private PayfileModelDataManager dataManager;// 注射
		
		public PayfileModelDataManager getDataManager() {
			return dataManager;
		}

		public void setDataManager(PayfileModelDataManager dataManager) {
			this.dataManager = dataManager;
		}
		
		/**
		 * 按照显示设置，设置显示与否和显示顺序
		 */
		public void setDisplay(){
			getDataManager().initDisplayData();
			List<WaPayfileDspVO> showItems = ((PayfileAppModel)getModel()).getRightItems();
			List<WaPayfileDspVO> hideItems = ((PayfileAppModel)getModel()).getLeftItems();
			for(int i = 0;i<hideItems.size();i++){
				try{
					getBillCardPanel().hideHeadItem(new String[]{hideItems.get(i).getItem_key()});
				}catch(NullPointerException e){//没有strKey对应的列时，报空指针，没有判断方法，暂时捕获异常
					Logger.error(e.getMessage());
				}
			}
			for(int i = 0;i<showItems.size();i++){
				try{
					//NCdp205550515 显示设置只选择人员编码显示，再全选所有显示，卡片界面只显示人员编码   lizt
					if(!getBillCardPanel().getHeadItem(showItems.get(i).getItem_key()).isShow()){
						getBillCardPanel().showHeadItem(new String[]{showItems.get(i).getItem_key()});
						getBillCardPanel().getHeadItem(showItems.get(i).getItem_key()).setShowOrder(i);
					}

				}catch(NullPointerException e){//没有strKey对应的列时，报空指针，没有判断方法，暂时捕获异常
					Logger.error(e.getMessage());
				}
			}
			getBillCardPanel().setBillData(getBillCardPanel().getBillData());
		}
		
		//20150917 shenliangc NCdp205483210 end

	@Override
	public void handleEvent(AppEvent event) {
		WaLoginContext context = (WaLoginContext) getModel().getContext();

		//20151207 shenliangc NCdp205555686  打开薪资档案节点，第一次点击新增报未知错误
		//历史遗留问题，这种方式解决选择不扣税税率表必输校验的问题，会引起新增之后税率表字段必输标志不出现的问题，因此不在这里处理。
		//20151205 shenliangc NCdp205554558 修改不扣税的薪资档案，保存时不应校验税率表为空。begin
//		boolean isTaxTableCanNull = false; //判断税率表是否可不输
//		if (context.getPk_wa_class() == null) {
//			isTaxTableCanNull = true;
//		} 
//		if (AppEventConst.UISTATE_CHANGED == event.getType()
//				&& (getPayfileAppModel().getUiState() == UIState.EDIT||getPayfileAppModel().getUiState() == UIState.ADD)) {
//			PayfileVO vo = (PayfileVO) getValue();
//			//扣税方式为“不扣税”，税率表可以为空。
//			//20151207 shenliangc NCdp205555686  打开薪资档案节点，第一次点击新增报未知错误
//			if (vo != null && vo.getTaxtype()!=null &&(vo.getTaxtype() == Taxtype.TAXFREE.toIntValue())) {
//				isTaxTableCanNull = true;
//			}
//		}
//		getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID).setNull(isTaxTableCanNull);
		//20151205 shenliangc NCdp205554558 修改不扣税的薪资档案，保存时不应校验税率表为空。end
		
		getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID).setNull(true);

		NormalPsnRef normalRef = new NormalPsnRef(context);
		normalRef.setPk_org(context.getPk_org());
		if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())) {
			getPsnRefPane().setRefModel(normalRef);
			AbstractRefModel refmodel = ((UIRefPane) billCardPanel.getHeadItem(
					PayfileVO.TAXTABLEID).getComponent()).getRefModel();
			if (refmodel != null) {
				refmodel.setPk_org(context.getPk_org());
			}
			((UIRefPane) billCardPanel.getHeadItem(PayfileVO.PK_LIABILITYORG)
					.getComponent()).setMultiCorpRef(true);
			//20150917 shenliangc NCdp205483210
			//在“单据模板初始化”中隐藏了财务部门、财务组织、成本中心、成本部门、使用附加费用扣除额几个字段，使其在卡片和列表的情况下都不显示，
			//但卡片情况下隐藏生效，列表情况下未生效。
			setDisplay();
		}
		PersonType ptype = getPayfileAppModel().getPType();
		// 新增时根据人员类型设置参照
		if (AppEventConst.UISTATE_CHANGED == event.getType()
				&& getPayfileAppModel().getUiState() == UIState.ADD) {
			if (ptype == PersonType.NORMAL) {// 正式人员
				normalRef.reloadData();
				getPsnRefPane().setRefModel(normalRef);

			} else if (ptype == PersonType.PARTTIME) {// 兼职人员
				DeptPartPsnRef partRef = new DeptPartPsnRef(context);
				partRef.reloadData();
				getPsnRefPane().setRefModel(partRef);

			} else if (ptype == PersonType.CHANGE) {// 变动人员
				AlternatePsnRef alterRef = new AlternatePsnRef(context, null);
				alterRef.reloadData();
				getPsnRefPane().setRefModel(alterRef);

			} else if (ptype == PersonType.RELATION) {// 相关人员
				RelationPsnRef relaRef = new RelationPsnRef(context);
				relaRef.reloadData();
				getPsnRefPane().setRefModel(relaRef);
			}

		}
		super.handleEvent(event);// 顺序上不能调换

		if (AppEventConst.UISTATE_CHANGED == event.getType()) {
			AbstractRefModel refmodel2 = null;
			if (getPayfileAppModel().getUiState() == UIState.EDIT
					|| getPayfileAppModel().getUiState() == UIState.ADD) {
				refmodel2 = ((UIRefPane) billCardPanel.getHeadItem(
						PayfileVO.FIPORGVID).getComponent()).getRefModel();
				if (refmodel2 != null) {
					refmodel2.setRefTitle(ResHelper.getString("60130payfile",
							"160130payfile0026")/* @res "财务组织" */);
				}

				refmodel2 = ((UIRefPane) billCardPanel.getHeadItem(
						PayfileVO.PK_LIABILITYORG).getComponent())
						.getRefModel();
				if (refmodel2 != null) {
					refmodel2.setRefTitle(ResHelper.getString("60130payfile",
							"160130payfile0024")/* @res "成本中心" */);
				}
			}

			// 修改时人员和一部分字段不能修改，

			if (getPayfileAppModel().getUiState() == UIState.EDIT) {
				getBillCardPanel().getHeadItem(PayfileVO.PK_PSNJOB).setEnabled(
						false);

				// 如果是多次发放，查询其他子方案有无此人;如果有，则扣税设置不能修改。
				if (context.getPk_prnt_class() != null) {
					String pk_psndoc = (String) getBillCardPanel().getHeadItem(
							PayfileVO.PK_PSNDOC).getValueObject();
					try {
						boolean existInSub = WADelegator.getPayfileQuery()
								.existInSubClass(context, pk_psndoc);

						if (existInSub) {
							getBillCardPanel().getHeadItem(PayfileVO.TAXTYPE)
							.setEnabled(false);
							getBillCardPanel()
							.getHeadItem(PayfileVO.TAXTABLEID)
							.setEnabled(false);
							getBillCardPanel().getHeadItem(PayfileVO.ISNDEBUCT)
							.setEnabled(false);
							getBillCardPanel().getHeadItem(PayfileVO.ISDERATE)
							.setEnabled(false);
							getDerateptg()
							.setEnabled(false);
//							getBillCardPanel().getHeadItem(PayfileVO.STOPFLAG)
//							.setEnabled(false);

							getBillCardPanel()
							.getHeadItem(PayfileVO.FIPDEPTVID)
							.setEnabled(false);
							getBillCardPanel().getHeadItem(PayfileVO.FIPORGVID)
							.setEnabled(false);
							getBillCardPanel()
							.getHeadItem(PayfileVO.LIBDEPTVID)
							.setEnabled(false);
							getBillCardPanel().getHeadItem(
									PayfileVO.PK_LIABILITYORG)
									.setEnabled(false);

						} else {
							getBillCardPanel().getHeadItem(PayfileVO.TAXTYPE)
							.setEnabled(true);
							getBillCardPanel()
							.getHeadItem(PayfileVO.TAXTABLEID)
							.setEnabled(true);
							getBillCardPanel().getHeadItem(PayfileVO.ISNDEBUCT)
							.setEnabled(true);
							getBillCardPanel().getHeadItem(PayfileVO.ISDERATE)
							.setEnabled(true);
							getDerateptg()
							.setEnabled(true);
							getBillCardPanel().getHeadItem(PayfileVO.STOPFLAG)
							.setEnabled(true);

							getBillCardPanel()
							.getHeadItem(PayfileVO.FIPDEPTVID)
							.setEnabled(true);
							getBillCardPanel().getHeadItem(PayfileVO.FIPORGVID)
							.setEnabled(true);
							getBillCardPanel()
							.getHeadItem(PayfileVO.LIBDEPTVID)
							.setEnabled(true);
							getBillCardPanel().getHeadItem(
									PayfileVO.PK_LIABILITYORG).setEnabled(true);

							refmodel2 = ((UIRefPane) billCardPanel.getHeadItem(
									PayfileVO.FIPDEPTVID).getComponent())
									.getRefModel();
							if (refmodel2 != null) {
								Object o = getRefPane(PayfileVO.FIPORGVID)
										.getRefModel().getValue(
												PayfileVO.PK_FINANCEORG);
								if (o != null) {
									refmodel2.setPk_org(o.toString());
								} else {
									refmodel2.setPk_org(null);
								}

								refmodel2
								.setRefTitle(ResHelper.getString(
										"60130payfile",
										"160130payfile0023")/*
										 * @res
										 * "财务部门"
										 */);
							}
							refmodel2 = ((UIRefPane) billCardPanel.getHeadItem(
									PayfileVO.LIBDEPTVID).getComponent())
									.getRefModel();
							if (refmodel2 != null) {
								String pk_costcenter = (String) getRefPane(
										PayfileVO.PK_LIABILITYORG)
										.getRefModel()
										.getValue("pk_costcenter");
								// 根据选择的成本中心获取关联的部门，如果成本中心置空那么显示全部部门。所属成本部门都置空
								if (pk_costcenter != null) {
									getRefPane(PayfileVO.LIBDEPTVID)
									.setWhereString("");
									((CostDeptVersionRefModel) getRefPane(
											PayfileVO.LIBDEPTVID).getRefModel())
											.setPk_costcenter(pk_costcenter);
								} else {
									getRefPane(PayfileVO.LIBDEPTVID)
									.setWhereString("1=2");
								}
								refmodel2
								.setRefTitle(ResHelper.getString(
										"60130payfile",
										"160130payfile0025")/*
										 * @res
										 * "成本部门"
										 */);
							}
						}
					} catch (BusinessException e1) {
						Logger.error(e1.getMessage());
						MessageDialog.showErrorDlg(this, null, e1.getMessage());
					}
				}

				// 如果是不扣税，设置税相关项目不可修改
				int taxType = (Integer) getBillCardPanel().getHeadItem(
						PayfileVO.TAXTYPE).getValueObject();
				if (Taxtype.TAXFREE.equalsValue(taxType)) {
					getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID)
					.setEnabled(false);
					getBillCardPanel().getHeadItem(PayfileVO.ISNDEBUCT)
					.setEnabled(false);
					getBillCardPanel().getHeadItem(PayfileVO.ISDERATE)
					.setEnabled(false);
					getDerateptg()
					.setEnabled(false);
					getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID)
					.setNull(false);
				} else {
					Boolean isDerate = (Boolean) getBillCardPanel()
							.getHeadItem(PayfileVO.ISDERATE).getValueObject();
					if (!isDerate) {
						getDerateptg()
						.setEnabled(false);
					} else {
						getDerateptg()
						.setEnabled(true);
					}
				}

			} else if (getPayfileAppModel().getUiState() == UIState.ADD) {
				if (ptype == PersonType.PARTTIME) {// 兼职人员
					billCardPanel.getHeadItem(PayfileVO.PARTFLAG).setValue(
							UFBoolean.TRUE);
				}
				refmodel2 = ((UIRefPane) billCardPanel.getHeadItem(
						PayfileVO.FIPDEPTVID).getComponent()).getRefModel();
				if (refmodel2 != null) {
					refmodel2.setPk_org(null);
					refmodel2.setRefTitle(ResHelper.getString("60130payfile",
							"160130payfile0023")/* @res "财务部门" */);
				}
				refmodel2 = ((UIRefPane) billCardPanel.getHeadItem(
						PayfileVO.LIBDEPTVID).getComponent()).getRefModel();
				if (refmodel2 != null) {
					// 2015-11-2 zhousze 新增时未选中成本中心时，成本部门置空 begin
					getRefPane(PayfileVO.LIBDEPTVID).setWhereString("1=2");
					// end
					refmodel2.setRefTitle(ResHelper.getString("60130payfile",
							"160130payfile0025")/* @res "成本部门" */);
				}

			}
		}

	}

	/**
	 * 项目编辑状态控制
	 * 
	 * @author liangxr on 2010-1-25
	 * @see nc.ui.pub.bill.BillEditListener#afterEdit(nc.ui.pub.bill.BillEditEvent)
	 */
	@Override
	public void afterEdit(BillEditEvent e) {
		PayfileUtil.afterEditAddMode(e, getBillCardPanel());
		if (PayfileVO.PK_PSNJOB.equals(e.getKey())) {
			String pk_psnjob = billCardPanel.getHeadItem(PayfileVO.PK_PSNJOB)
					.getValueObject().toString();
			UIRefPane refpane = (UIRefPane) getBillCardPanel().getHeadItem(
					PayfileVO.PK_PSNJOB).getComponent();
			PersonType ptype = getPayfileAppModel().getPType();
			Integer assgid = Integer.valueOf(1);
			if (ptype == PersonType.PARTTIME) {
				assgid = (Integer) refpane.getRefValue("hi_psnjob.assgid");
			}
			billCardPanel.setHeadItem(PayfileVO.ASSGID, assgid);
			// 获得部门
			Object pk_psndoc = getRefPane("pk_psnjob.pk_psndoc").getValueObj();
			if (pk_psndoc != null) {
				pk_psndoc = ((String[]) pk_psndoc)[0];
				billCardPanel.setHeadItem(PayfileVO.PK_PSNDOC, pk_psndoc);
			}
						
			String pk_dept = null;
			String deptvid = null;
			// 获得部门
			Object deptobj = getRefPane("pk_psnjob.pk_dept").getValueObj();
			if (deptobj != null) {
				pk_dept = ((String[]) deptobj)[0];
			}

			// 获得部门版本id
			deptobj = getRefPane("pk_psnjob.pk_dept.pk_vid").getValueObj();
			if (deptobj != null) {
				deptvid = ((String[]) deptobj)[0];
			}
			// String pk_dept =
			// ((String[])getRefPane("pk_psnjob.pk_dept").getValueObj())[0];
			// String deptvid =
			// getRefPane("pk_psnjob.pk_dept.pk_vid").getValueObj().toString();

			WaLoginContext context = (WaLoginContext) getModel().getContext();
			PsnBankaccUnionVO[] bankAccVOs = null;
			WaBmFileOrgVO payOrgVO = new WaBmFileOrgVO();
			String pk_costcenter = null;
			// 如果是多次发放，查询汇总方案有无此人;如果有，则扣税设置必须一致。
			try {
				if (context.getPk_prnt_class() != null) {
					PayfileVO vo = WADelegator.getPayfileQuery()
							.getPsnInPrntClass(context, pk_psnjob);
					if (vo != null) {
						getIsndebuct().setValue(vo.getIsndebuct());
						getIsderate().setValue(vo.getIsderate());
						getTaxType().setValue(vo.getTaxtype());
						getTaxtableid().setPK(vo.getTaxtableid());
						getDerateptg().setValue(vo.getDerateptg());

						getIsndebuct().setEnabled(false);
						getIsderate().setEnabled(false);
						getTaxType().setEnabled(false);
						getTaxtableid().setEnabled(false);
						getDerateptg().setEnabled(false);
						//guoqt多次发放，如果不扣税则设置税相关项目不可修改
						int taxType = (Integer) getBillCardPanel().getHeadItem(
								PayfileVO.TAXTYPE).getValueObject();
						if (Taxtype.TAXFREE.equalsValue(taxType)) {
							getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID)
							.setEnabled(false);
							getBillCardPanel().getHeadItem(PayfileVO.ISNDEBUCT)
							.setEnabled(false);
							getBillCardPanel().getHeadItem(PayfileVO.ISDERATE)
							.setEnabled(false);
							getDerateptg().setEnabled(false);
							getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID).setNull(false);
						} 
					}
					//					else {
					//						getIsndebuct().setEnabled(true);
					//						getIsderate().setEnabled(true);
					//						getTaxType().setEnabled(true);
					//						getTaxtableid().setEnabled(true);
					//						// getDerateptg().setEnabled(true);
					//					}

				}
				// 设置版本信息
				Object temp = getRefPane("pk_psnjob.pk_dept.pk_vid")
						.getValueObj();
				if (temp != null) {
					// 任职部门有可能为空
					billCardPanel.getHeadItem(PayfileVO.WORKDEPTVID).setValue(
							((String[]) temp)[0]);
				}
				// 任职组织不为空
				temp = getRefPane("pk_psnjob.pk_org.pk_vid").getValueObj();
				if (temp != null) {
					billCardPanel.getHeadItem(PayfileVO.WORKORGVID).setValue(
							((String[]) temp)[0]);
				}
				
				//纳税组织赋值
				temp = getRefPane("pk_psnjob.pk_org").getValueObj();
				if (temp != null) {
					String pk_org = ((String[]) temp)[0];
					IBasicOrgUnitQryService service = NCLocator.getInstance().lookup(IBasicOrgUnitQryService.class);
					OrgVO org = service.getOrg(pk_org);
					billCardPanel.getHeadItem(PayfileVO.TAXORG).setValue(org.getPk_corp());
				}

				// 根据人员初始化人员帐号信息

				StringBuffer wheresql = new StringBuffer();
				wheresql.append(" pk_psndoc =(select pk_psndoc from hi_psnjob where pk_psnjob = '");
				wheresql.append(pk_psnjob);
				wheresql.append("') ");

				// 查询人员帐号
				bankAccVOs = getBankQuery().queryPsnBankaccUnionVOsByCon(
						wheresql.toString(), false);
				if(null!=bankAccVOs){
					List<PsnBankaccUnionVO> list = new ArrayList<PsnBankaccUnionVO>();
					for (int i = 0; i < bankAccVOs.length; i++) {
						Integer enablestate = bankAccVOs[i].getBankaccbasVO().getEnablestate();
						if(null!=enablestate&&enablestate==2){
							list.add(bankAccVOs[i]);
						}
					}
					bankAccVOs = list.toArray(new PsnBankaccUnionVO[0]);
				}
				// 查询默认财务组织
				payOrgVO = getOrgQuery().getPkFinanceOrg(pk_psnjob);
				// 根据任职组织查找关联的成本中心
				pk_costcenter = getOrgQuery().getPkCostCenter(pk_dept);
			} catch (BusinessException e1) {
				Logger.error(e1.getMessage());
				MessageDialog.showErrorDlg(this, null, e1.getMessage());
			}
			if (payOrgVO != null) {
				getRefPane(PayfileVO.PK_FINANCEORG).setPK(
						payOrgVO.getPk_financeorg());
				getRefPane(PayfileVO.PK_FINANACEDEPT).setPk_org(
						payOrgVO.getPk_financeorg());
				getRefPane(PayfileVO.PK_FINANACEDEPT).setPK(
						payOrgVO.getPk_financedept());

				getRefPane(PayfileVO.FIPORGVID).setPK(payOrgVO.getFiporgvid());
				getRefPane(PayfileVO.FIPDEPTVID).setPk_org(
						payOrgVO.getPk_financeorg());
				getRefPane(PayfileVO.FIPDEPTVID)
				.setPK(payOrgVO.getFipdeptvid());

			}
			if (pk_costcenter != null) {
				// 任职部门关联的成本中心作为默认的成本中心，任职部门作为默认成本部门
				getRefPane(PayfileVO.PK_LIABILITYORG).setPK(pk_costcenter);

				getRefPane(PayfileVO.LIBDEPTVID).setWhereString(null);

				((CostDeptVersionRefModel) getRefPane(PayfileVO.LIBDEPTVID)
						.getRefModel()).setPk_costcenter(pk_costcenter);

				// getRefPane(PayfileVO.LIBDEPTVID).setWhereString("  pk_dept in ( "+SQLHelper.joinToInSql(pk_depts,
				// -1)+")");
				getRefPane(PayfileVO.LIBDEPTVID).setPK(deptvid);

				getRefPane(PayfileVO.PK_LIABILITYDEPT).setPK(pk_dept);

			}

			// 设置帐号显示默认值
			// 先清空
			getBankAccPane().setValue(null);
			getBankname().setPK(null);
			getBankAccPane2().setValue(null);
			getBankname2().setPK(null);
			getBankAccPane3().setValue(null);
			getBankname3().setPK(null);

			if (ArrayUtils.isEmpty(bankAccVOs)) {
				return;
			}

			for (PsnBankaccUnionVO bankAccVO : bankAccVOs) {
				if (IPsnbankaccConst.PAYACC_MAINCARD.equals(bankAccVO
						.getPayacc())) {
					getBankAccPane().setValue(bankAccVO.getAccnum());
					getBankname().setPK(
							bankAccVO.getBankaccbasVO().getPk_banktype());
				} else if (IPsnbankaccConst.PAYACC_SUBCARD1.equals(bankAccVO
						.getPayacc())) {
					getBankAccPane2().setValue(bankAccVO.getAccnum());
					getBankname2().setPK(
							bankAccVO.getBankaccbasVO().getPk_banktype());
				} else if (IPsnbankaccConst.PAYACC_SUBCARD2.equals(bankAccVO
						.getPayacc())) {
					getBankAccPane3().setValue(bankAccVO.getAccnum());
					getBankname3().setPK(
							bankAccVO.getBankaccbasVO().getPk_banktype());
				}

			}
		} else if (PayfileVO.PK_LIABILITYORG.equals(e.getKey())) {
			String pk_costcenter = (String) getRefPane(
					PayfileVO.PK_LIABILITYORG).getRefModel().getValue(
							"pk_costcenter");
			// 根据选择的成本中心获取关联的部门，如果成本中心置空那么所属成本部门置空
			if (pk_costcenter != null) {
				getRefPane(PayfileVO.LIBDEPTVID).setWhereString("");
				((CostDeptVersionRefModel) getRefPane(PayfileVO.LIBDEPTVID)
						.getRefModel()).setPk_costcenter(pk_costcenter);
			} else {
				getRefPane(PayfileVO.LIBDEPTVID).setWhereString(" 1=2 ");
			}

			getRefPane(PayfileVO.LIBDEPTVID).setPK(null);
			getRefPane(PayfileVO.PK_LIABILITYDEPT).setPK(null);
			billCardPanel.getHeadItem(PayfileVO.LIBDEPTVID).setEnabled(true);
		} else if (PayfileVO.LIBDEPTVID.equals(e.getKey())) {
			Object pk_dept = getRefPane(PayfileVO.LIBDEPTVID).getRefModel()
					.getValue("pk_dept");
			if (pk_dept != null) {
				getRefPane(PayfileVO.PK_LIABILITYDEPT)
				.setPK(pk_dept.toString());
			} else {
				getRefPane(PayfileVO.PK_LIABILITYDEPT).setPK(null);
			}
		} else if (PayfileVO.FIPORGVID.equals(e.getKey())) {
			Object pk_org = getRefPane(PayfileVO.FIPORGVID).getRefModel()
					.getValue("pk_financeorg");
			billCardPanel.getHeadItem(PayfileVO.PK_FINANCEORG).setValue(pk_org);
			if (pk_org != null) {
				getRefPane(PayfileVO.FIPDEPTVID).setPk_org(pk_org.toString());
			} else {
				getRefPane(PayfileVO.FIPDEPTVID).setPk_org(null);
			}
			getRefPane(PayfileVO.FIPDEPTVID).setPK(null);
			getRefPane(PayfileVO.PK_FINANACEDEPT).setPK(null);
			billCardPanel.getHeadItem(PayfileVO.FIPDEPTVID).setEnabled(true);
		} else if (PayfileVO.FIPDEPTVID.equals(e.getKey())) {
			Object pk_dept = getRefPane(PayfileVO.FIPDEPTVID).getRefModel()
					.getValue("pk_dept");
			if (pk_dept != null) {
				getRefPane(PayfileVO.PK_FINANACEDEPT).setPK(pk_dept.toString());
			} else {
				getRefPane(PayfileVO.PK_FINANACEDEPT).setPK(null);
			}
		} else if (PayfileVO.TAXTYPE.equals(e.getKey())){
			if (getBillCardPanel().getHeadItem(PayfileVO.TAXTYPE).getValue().equals(Taxtype.TAXFREE.toStringValue())) {
				getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID).setNull(false);
			}else{
				getBillCardPanel().getHeadItem(PayfileVO.TAXTABLEID).setNull(true);
			}
		}
	}

	//	private UIRefPane getPsnRefPane() {
	//		UIRefPane psnRefPane = (UIRefPane) billCardPanel.getHeadItem(
	//				PayfileVO.PK_PSNJOB).getComponent();
	//		psnRefPane.setButtonFireEvent(true);
	//		psnRefPane.setEnabled(false);
	//		return psnRefPane;
	//	}

	private UIRefPane getPsnRefPane() {
		if(null==psnRefPane){
			psnRefPane = (UIRefPane) billCardPanel.getHeadItem(PayfileVO.PK_PSNJOB).getComponent();
			psnRefPane.setButtonFireEvent(true);
			psnRefPane.setEnabled(false);
		}
		return psnRefPane;
	}

	private BillItem getIsndebuct() {
		return billCardPanel.getHeadItem(PayfileVO.ISNDEBUCT);
	}

	private BillItem getIsderate() {
		return billCardPanel.getHeadItem(PayfileVO.ISDERATE);
	}
 
	private BillItem getDerateptg() {
		return billCardPanel.getHeadItem(PayfileVO.DERATEPTG);
	}

	private BillItem getTaxType() {
		return billCardPanel.getHeadItem(PayfileVO.TAXTYPE);
	}

	private UIRefPane getTaxtableid() {
		return (UIRefPane) billCardPanel.getHeadItem(PayfileVO.TAXTABLEID)
				.getComponent();
	}

	// 银行帐号1
	private BillItem getBankAccPane() {
		return billCardPanel.getHeadItem(PayfileVO.PK_BANKACCBAS1);
	}

	private UIRefPane getBankname() {
		return (UIRefPane) billCardPanel.getHeadItem(PayfileVO.PK_BANKTYPE1)
				.getComponent();
	}

	private UIRefPane getBankname2() {
		return (UIRefPane) billCardPanel.getHeadItem(PayfileVO.PK_BANKTYPE2)
				.getComponent();
	}

	private UIRefPane getBankname3() {
		return (UIRefPane) billCardPanel.getHeadItem(PayfileVO.PK_BANKTYPE3)
				.getComponent();
	}

	private BillItem getBankAccPane2() {
		return billCardPanel.getHeadItem(PayfileVO.PK_BANKACCBAS2);
	}

	private BillItem getBankAccPane3() {
		return billCardPanel.getHeadItem(PayfileVO.PK_BANKACCBAS3);
	}

	private PayfileAppModel getPayfileAppModel() {
		return (PayfileAppModel) super.getModel();
	}

	private UIRefPane getRefPane(String itemCode) {
		return (UIRefPane) billCardPanel.getHeadItem(itemCode).getComponent();
	}

	/**
	 * @author zhoucx on 2009-12-22
	 * @see nc.ui.uif2.editor.BillForm#getValue()
	 */
	@Override
	public Object getValue() {
		PayfileVO payfileVO = (PayfileVO) super.getValue();
		// getRefPane(PayfileVO.PK_LIABILITYDEPT).getValueObj();
		if (UIState.ADD.equals(getPayfileAppModel().getUiState())) {
			String pk_psndoc = (String) getPsnRefPane().getRefModel().getValue(
					"bd_psndoc.pk_psndoc");
			payfileVO.setPk_psndoc(pk_psndoc);
			String pk_psnorg = (String) getPsnRefPane().getRefModel().getValue(
					"hi_psnorg.pk_psnorg");
			payfileVO.setPk_psnorg(pk_psnorg);
			Object temp = getRefPane("pk_psnjob.pk_dept").getValueObj();
			if (temp != null) {
				payfileVO.setWorkdept(((String[]) temp)[0]);
			}
			temp = getRefPane("pk_psnjob.pk_org").getValueObj();
			if (temp != null) {
				payfileVO.setWorkorg(((String[]) temp)[0]);
			}
			payfileVO.setPk_wa_class(getPayfileAppModel().getWaContext()
					.getPk_wa_class());
			payfileVO.setCyear(getPayfileAppModel().getWaContext().getWaYear());
			payfileVO.setCperiod(getPayfileAppModel().getWaContext()
					.getWaPeriod());
			payfileVO.setCyearperiod(getPayfileAppModel().getWaContext()
					.getCyear()
					+ getPayfileAppModel().getWaContext().getCperiod());
		} else if (UIState.EDIT.equals(getPayfileAppModel().getUiState())) {
			String pk_psndoc = (String) getPsnRefPane().getRefModel().getValue(
					"bd_psndoc.pk_psndoc");
			payfileVO.setPk_psndoc(pk_psndoc);
			Object temp = getRefPane("pk_psnjob.pk_dept").getValueObj();
			if (temp != null) {
				payfileVO.setWorkdept(((String[]) temp)[0]);
			}
			temp = getRefPane("pk_psnjob.pk_org").getValueObj();
			if (temp != null) {
				payfileVO.setWorkorg(((String[]) temp)[0]);
			}
		}
		payfileVO.setPk_prnt_class(getPayfileAppModel().getWaContext()
				.getWaLoginVO().getPk_prnt_class());
		return payfileVO;
	}

	public IPsnBankaccQueryService getBankQuery() {
		if (bankQuery == null) {
			bankQuery = NCLocator.getInstance().lookup(
					IPsnBankaccQueryService.class);
		}
		return bankQuery;
	}

	public IWaBmfileQueryService getOrgQuery() {
		if (orgQuery == null) {
			orgQuery = NCLocator.getInstance().lookup(
					IWaBmfileQueryService.class);
		}
		return orgQuery;
	}

}
