package nc.ui.wa.payleave.view.dialog;

import javax.swing.ListSelectionModel;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IPayLeaveQueryService;
import nc.ui.hr.frame.util.table.SelectableBillScrollPane;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.vo.pub.BusinessException;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.ArrayUtils;

public class AddLeavePsnSecondStep extends WizardStep implements IWizardStepListener{

	private WaLoginContext context = null;

	private SelectableBillScrollPane psnBillScrollPane = null;

	public AddLeavePsnSecondStep(WaLoginContext context1){
		setTitle(ResHelper.getString("common","UC001-0000108")/*@res "新增"*/);
		setDescription(ResHelper.getString("60130payleave","060130payleave0013")/*@res "选择离职结算人员"*/);
		setComp(getPsnBillScrollPane());
		initTableModel();
		addListener(this);
		context = context1;
	}

	@Override
	public void stepActived(WizardStepEvent event) throws WizardStepException {
		//第二步也可以直接完成
		getModel().getPublicActions().getFinishAction().setEnabled(true);
		//设置默认值
		PayfileVO[] vos = null;
		try {
			// 取得当前期间的开始日期和结束日期
			vos = NCLocator.getInstance().lookup(IPayLeaveQueryService.class)
					.queryLeavePsnInfo(context.getPk_prnt_class(),
							context.getWaYear(), context.getWaPeriod(),
							getModel().getAttr("StartDate").toString(),
							getModel().getAttr("EndDate").toString());
		} catch (BusinessException e) {
			throw new WizardStepException(e);
		}
		getPsnBillScrollPane().getTableModel().setBodyDataVO(vos);
		getPsnBillScrollPane().selectAllRows();
	}

	@Override
	public void stepDisactived(WizardStepEvent event)
			throws WizardStepException {
		PayfileVO[] vos = (PayfileVO[]) getPsnBillScrollPane().getSelectedBodyVOs(PayfileVO.class);
		getModel().putAttr("SelectedVOs", vos);
	}

	@Override
	public void validate() throws WizardStepValidateException {
		super.validate();
		Object[] selectVOs = getPsnBillScrollPane().getSelectedBodyVOs(PayfileVO.class);
		if (ArrayUtils.isEmpty(selectVOs)) {
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg(ResHelper.getString("60130payleave","060130payleave0007")/*@res "提示"*/, 
					ResHelper.getString("60130payleave","060130payleave0014")/*@res "请选择人员!"*/);
			throw e;
		}
	}

	private void initTableModel() {
		try {
			String[] saBodyColName = { ResHelper.getString("common","UC000-0004044")/*@res "选择"*/, 
					ResHelper.getString("60130payleave","060130payleave0023")/*@res "员工号"*/, 
					ResHelper.getString("common","UC000-0001403")/*@res "姓名"*/, 
					ResHelper.getString("60130payleave","060130payleave0015")/*@res "任职组织"*/, 
					ResHelper.getString("common","UC000-0004064")/*@res "部门"*/, 
					ResHelper.getString("common","UC000-0001653")/*@res "岗位"*/,
					ResHelper.getString("common","UC000-0000140")/*@res "人员类别"*/, 
					ResHelper.getString("common","UC000-0003061")/*@res "离职日期"*/,
					"pk_wa_data", "pk_psndoc", "pk_psnjob", "pk_psnorg",
					"pk_group", "pk_org", "taxtableid", "partflag",
					"pk_bankaccbas1", "pk_bankaccbas2", "pk_bankaccbas3",
					"pk_banktype1", "pk_banktype2", "pk_banktype3",
					"isndebuct", "isderate", "derateptg","taxtype","checkflag",
//		NCdp205554799  20151208   xiejie3  而且当时点薪资取考勤计薪日天数/薪资期间计薪日天数时，组织【001】下方案【fa002】12期间下离职结薪人员【转正123】时点薪资没有值
//		由于时点薪资需要用到assgid，所以要向数据库增加assgid的值，新增人来自新增向导，所以在向导增加assgid属性。		
					PayfileVO.ASSGID,
//		end
					PayfileVO.WORKORG,PayfileVO.WORKORGVID,PayfileVO.WORKDEPT,PayfileVO.WORKDEPTVID,
					PayfileVO.FIPORGVID,PayfileVO.PK_FINANCEORG,PayfileVO.FIPDEPTVID,PayfileVO.PK_FINANACEDEPT,
					PayfileVO.LIBORGVID,PayfileVO.PK_LIABILITYORG,PayfileVO.LIBDEPTVID,PayfileVO.PK_LIABILITYDEPT,PayfileVO.TAXORG,"taxsumuid" };
			// 显示名称
			String[] saBodyColKeyName = { PayfileVO.SELECTFLAG, "clerkcode",
					"psnName", PayfileVO.ORGNAME, PayfileVO.DEPTNAME,
					PayfileVO.POSTNAME, "psnclname", "leavedate","pk_wa_data", "pk_psndoc",
					"pk_psnjob", "pk_psnorg", "pk_group", "pk_org",
					"taxtableid", "partflag", "pk_bankaccbas1",
					"pk_bankaccbas2", "pk_bankaccbas3", "pk_banktype1",
					"pk_banktype2", "pk_banktype3", "isndebuct", "isderate",
					"derateptg" ,"taxtype","checkflag",
//		NCdp205554799  20151208   xiejie3  而且当时点薪资取考勤计薪日天数/薪资期间计薪日天数时，组织【001】下方案【fa002】12期间下离职结薪人员【转正123】时点薪资没有值
//		由于时点薪资需要用到assgid，所以要向数据库增加assgid的值，新增人来自新增向导，所以在向导增加assgid属性。	
					PayfileVO.ASSGID,
//		end
					PayfileVO.WORKORG,PayfileVO.WORKORGVID,PayfileVO.WORKDEPT,PayfileVO.WORKDEPTVID,
					PayfileVO.FIPORGVID,PayfileVO.PK_FINANCEORG,PayfileVO.FIPDEPTVID,PayfileVO.PK_FINANACEDEPT,
					PayfileVO.LIBORGVID,PayfileVO.PK_LIABILITYORG,PayfileVO.LIBDEPTVID,PayfileVO.PK_LIABILITYDEPT,PayfileVO.TAXORG,"taxsumuid"};

			// 关键字
			BillItem[] biaBody = new BillItem[saBodyColName.length];

			for (int i = 0; i < saBodyColName.length; i++) {
				biaBody[i] = new BillItem();
				biaBody[i].setName(saBodyColName[i]);
				biaBody[i].setKey(saBodyColKeyName[i]);
				biaBody[i].setWidth(100);
				biaBody[i].setNull(false);
				biaBody[i].setEnabled(true);
				biaBody[i].setEdit(false);
				biaBody[i].setDataType(BillItem.STRING);
				if (PayfileVO.SELECTFLAG.equals(saBodyColKeyName[i])) {
					biaBody[i].setWidth(70);
					biaBody[i].setEdit(true);
					biaBody[i].setDataType(BillItem.BOOLEAN);
				}
				if (i > 7) {
					biaBody[i].setShow(false);
				}
			}
			BillModel billModel = new BillModel();
			billModel.setBodyItems(biaBody);
			getPsnBillScrollPane().setTableModel(billModel);
			getPsnBillScrollPane().getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			getPsnBillScrollPane().setSelectRowCode("selectflag");
			getPsnBillScrollPane().setRowNOShow(true);
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	public SelectableBillScrollPane getPsnBillScrollPane() {
		if (psnBillScrollPane == null) {
			psnBillScrollPane = new SelectableBillScrollPane();
			psnBillScrollPane.setName("psnBillScrollPane");
		}
		return psnBillScrollPane;
	}

}