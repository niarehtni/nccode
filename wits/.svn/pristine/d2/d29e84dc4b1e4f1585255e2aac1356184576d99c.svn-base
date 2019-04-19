package nc.ui.wa.payfile.wizard;

import java.text.MessageFormat;

import javax.swing.ListSelectionModel;

import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.bs.uif2.validation.ValidationException;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.ui.hr.frame.util.table.SelectableBillScrollPane;
import nc.ui.hr.util.HrDataPermHelper;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.wa.payfile.model.PayfileAppModel;
import nc.ui.wa.payfile.model.PayfileModelDataManager;
import nc.ui.wa.payfile.model.PayfileWizardModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.payfile.PayfileVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 人员查询结果 WizardStep
 *
 * @author: zhoucx
 * @date: 2009-12-1 下午02:33:37
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class SearchPsnWizardSecondStep extends WizardStep implements IWizardStepListener {

	private SelectableBillScrollPane psnBillScrollPane = null;

	private PayfileVO[] datas = null;

	private LoginContext context = null;

	public SearchPsnWizardSecondStep(PayfileAppModel model, PayfileModelDataManager dataManager, String action) {
		super();
		setTitle(action);
		setDescription(MessageFormat.format(ResHelper.getString("60130payfile","060130payfile0293"), action));/*@res "选择需要{0}的人员"*/
		context = model.getContext();
		setComp(getPsnBillScrollPane());
		addListener(this);
	}

	public SelectableBillScrollPane getPsnBillScrollPane() {
		if (psnBillScrollPane == null) {
			psnBillScrollPane = new SelectableBillScrollPane();
			psnBillScrollPane.setName("psnBillScrollPane");
			initTableModel();
		}
		return psnBillScrollPane;
	}

	private void initTableModel() {
		try {

			String[] saBodyColName = { "",/*选择两个字不需要*/ ResHelper.getString("60130payfile","060130payfile0333")/*@res "员工号"*/, ResHelper.getString("common","UC000-0001403")/*@res "姓名"*/, ResHelper.getString("60130payfile","060130payfile0260")/*@res "发放部门"*/, ResHelper.getString("60130payfile","060130payfile0309")/*@res "所在岗位"*/, ResHelper.getString("common","UC000-0002343")/*@res "是否停发"*/,
					ResHelper.getString("60130payfile","060130payfile0310")/*@res "扣税方式"*/, ResHelper.getString("common","UC000-0003080")/*@res "税率表"*/, ResHelper.getString("common","UC000-0003914")/*@res "身份证号"*/, ResHelper.getString("60130payfile","060130payfile0311")/*@res "是否兼职"*/ ,"pk_wa_data","pk_psndoc","pk_psnorg",
					"pk_psnjob","taxtype","taxtableid","pk_group","pk_org","pk_wa_class","cyear","cperiod","ts",
					PayfileVO.WORKORG, PayfileVO.WORKORGVID,PayfileVO.WORKDEPT,PayfileVO.WORKDEPTVID,
					PayfileVO.PK_FINANCEORG,PayfileVO.PK_FINANACEDEPT,PayfileVO.FIPORGVID,PayfileVO.FIPDEPTVID,
					PayfileVO.PK_LIABILITYDEPT,PayfileVO.PK_LIABILITYORG,PayfileVO.LIBDEPTVID,PayfileVO.LIBORGVID,
					PayfileVO.PK_BANKACCBAS1,PayfileVO.PK_BANKACCBAS2,PayfileVO.PK_BANKACCBAS3,
					PayfileVO.PK_BANKTYPE1, PayfileVO.PK_BANKTYPE2,
					PayfileVO.PK_BANKTYPE3, PayfileVO.ASSGID,
					PayfileVO.CYEARPERIOD,PayfileVO.TAXORG,PayfileVO.TAXSUMUID };
			// 显示名称
			String[] saBodyColKeyName = { PayfileVO.SELECTFLAG, "clerkcode", "psnName", "deptname",
					"postname", PayfileVO.STOPFLAG, PayfileVO.TAXTYPENAME, "taxbasename", "psnid",
					PayfileVO.PARTFLAG ,PayfileVO.PK_WA_DATA,"pk_psndoc","pk_psnorg","pk_psnjob",
					"taxtype","taxtableid","pk_group","pk_org","pk_wa_class","cyear","cperiod","ts",
					PayfileVO.WORKORG, PayfileVO.WORKORGVID,PayfileVO.WORKDEPT,PayfileVO.WORKDEPTVID,
					PayfileVO.PK_FINANCEORG,PayfileVO.PK_FINANACEDEPT,PayfileVO.FIPORGVID,PayfileVO.FIPDEPTVID,
					PayfileVO.PK_LIABILITYDEPT,PayfileVO.PK_LIABILITYORG,PayfileVO.LIBDEPTVID,PayfileVO.LIBORGVID,
					PayfileVO.PK_BANKACCBAS1,PayfileVO.PK_BANKACCBAS2,PayfileVO.PK_BANKACCBAS3,
					PayfileVO.PK_BANKTYPE1,
					PayfileVO.PK_BANKTYPE2, PayfileVO.PK_BANKTYPE3,
					PayfileVO.ASSGID, PayfileVO.CYEARPERIOD,PayfileVO.TAXORG,PayfileVO.TAXSUMUID };

			// 关键字
			BillItem[] biaBody = new BillItem[saBodyColName.length];

			for (int i = 0; i < saBodyColName.length; i++) {
				biaBody[i] = new BillItem();
				biaBody[i].setName(saBodyColName[i]);
				biaBody[i].setKey(saBodyColKeyName[i]);
				biaBody[i].setWidth(100);
				biaBody[i].setNull(false);
				biaBody[i].setEnabled(true);
				if (i == 0) {
					biaBody[i].setEdit(true);
				} else {
					biaBody[i].setEdit(false);
				}
				if (PayfileVO.STOPFLAG.equals(saBodyColKeyName[i])
						|| PayfileVO.PARTFLAG.equals(saBodyColKeyName[i])
						|| PayfileVO.SELECTFLAG.equals(saBodyColKeyName[i])) {
					biaBody[i].setDataType(BillItem.BOOLEAN);
					((UICheckBox) (biaBody[i].getComponent())).setHorizontalAlignment(UICheckBox.CENTER);
				} else if (PayfileVO.TAXTYPE.equals(saBodyColKeyName[i])) {
					biaBody[i].setDataType(IBillItem.COMBO);
					biaBody[i].setRefType(ResHelper.getString("60130payfile","060130payfile0312")/*@res "IX,代扣税=0,代付税=1,不扣税=2"*/);
				} else {
					biaBody[i].setDataType(BillItem.STRING);
				}

				if(i>9){
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

	/**
	 * 校验是否已经选择了人员
	 *
	 * @author zhoucx on 2009-12-28
	 * @see nc.ui.pub.beans.wizard.WizardStep#validate()
	 */
	@Override
	public void validate() throws WizardStepValidateException {
		super.validate();
		PayfileVO[] selectVOs = getSelectedVO();
		WizardStepValidateException e = new WizardStepValidateException();
		if (ArrayUtils.isEmpty(selectVOs)) {
			e.addMsg(ResHelper.getString("60130payfile","060130payfile0245")/*@res "提示"*/, ResHelper.getString("60130payfile","060130payfile0281")/*@res "请选择人员!"*/);
			throw e;
		}
		if (ResHelper.getString("60130payfile","060130payfile0246")/*@res "批量删除"*/.equals(getTitle()) || ResHelper.getString("60130payfile","060130payfile0331")/*@res "批量修改"*/.equals(getTitle())
				|| ResHelper.getString("60130payfile","060130payfile0254")/*@res "转档"*/.equals(getTitle())) {
			String opercode = IActionCode.EDIT;
			if (ResHelper.getString("60130payfile","060130payfile0246")/*@res "批量删除"*/.equals(getTitle())) {
				opercode = IActionCode.DELETE;
			}else if (ResHelper.getString("60130payfile","060130payfile0254")/*@res "转档"*/.equals(getTitle())){
				opercode = IHRWAActionCode.TRANSFERPAYFILE;
			}
			ValidationException ex = HrDataPermHelper.checkDataPermission(
					IHRWADataResCode.PAYFILE, opercode,
					opercode, getSelectedVO(), context);
			try {
				HrDataPermHelper.dealValidationException(ex);
			} catch (BusinessException be) {
				e.addMsg(ResHelper.getString("60130payfile","060130payfile0245")/*@res "提示"*/, be.getMessage());
				throw e;
			}
		}
	}

	/**
	 * @author zhoucx on 2009-12-28
	 * @see nc.ui.pub.beans.wizard.WizardStep#getModel()
	 */
	@Override
	public PayfileWizardModel getModel() {
		return (PayfileWizardModel) super.getModel();
	}

	@Override
	public void stepActived(WizardStepEvent event) {
		// 在列表中显示VO
		datas = getModel().getPayfileVOs();
		if (ArrayUtils.isEmpty(getModel().getSelectVOs())) {
			getPsnBillScrollPane().selectAllRows();
			for(PayfileVO data: datas){
				data.setSelectflag(UFBoolean.TRUE);
			}
		}
		getPsnBillScrollPane().getTableModel().setBodyDataVO(datas);
	}

	/**
	 * 获取选中的VO
	 *
	 * @author liangxr on 2010-1-26
	 * @return
	 */
	private PayfileVO[] getSelectedVO() {
		return (PayfileVO[]) getPsnBillScrollPane().getSelectedBodyVOs(PayfileVO.class);
	}

	@Override
	public void stepDisactived(WizardStepEvent event) throws WizardStepException{
		// 缓存已选中VO
		getModel().setSelectVOs(getSelectedVO());
		// 重新缓存全部VO。在以后步骤返回时保持原来的选中状态
		datas = (PayfileVO[]) getPsnBillScrollPane().getTableModel().getBodyValueVOs(PayfileVO.class.getName());
		getModel().setPayfileVOs(datas);
		datas = null;
	}


}