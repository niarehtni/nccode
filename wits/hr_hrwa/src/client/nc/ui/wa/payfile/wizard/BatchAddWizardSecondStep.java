package nc.ui.wa.payfile.wizard;

import java.awt.Component;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.ListSelectionModel;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.hr.frame.util.table.SelectableBillScrollPane;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.wa.payfile.model.PayfileAppModel;
import nc.ui.wa.payfile.model.PayfileModelDataManager;
import nc.ui.wa.payfile.model.PayfileWizardModel;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.payfile.PayfileVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * ��Ա��ѯ��� WizardStep
 *
 * @author: zhoucx
 * @date: 2009-12-1 ����02:33:37
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class BatchAddWizardSecondStep extends WizardStep implements IWizardStepListener, BillEditListener {

	private SelectableBillScrollPane psnBillScrollPane = null;

	private PayfileVO[] datas = null;

	public BatchAddWizardSecondStep(PayfileAppModel model, PayfileModelDataManager dataManager, String action) {
		super();
		setTitle(action);
		setDescription(MessageFormat.format(ResHelper.getString("60130payfile","060130payfile0293"), action));/*@res "ѡ����Ҫ{0}����Ա"*/
		setComp(getPsnBillScrollPane());
		addListener(this);
	}

	public SelectableBillScrollPane getPsnBillScrollPane() {
		if (psnBillScrollPane == null) {
			psnBillScrollPane = new SelectableBillScrollPane();
			psnBillScrollPane.setName("psnBillScrollPane");
			psnBillScrollPane.addEditListener(this);
    		//20151214 shenliangc NCdp205559130  н�ʹ�������һ�к����������ûس����У��ڶ����к�Ϊ�գ�����ʱ����
    		//ȥ���س����С��ӱ�����Ҽ���
			psnBillScrollPane.setAutoAddLine(false);
			initTableModel();
		}
		return psnBillScrollPane;
	}

	private void initTableModel() {
		try {
			// ��ʾ����
			String[] saBodyColName = { ""/*@res "ѡ��"*/,
					ResHelper.getString("60130payfile","060130payfile0333")/*@res "Ա����"*/,
					ResHelper.getString("common","UC000-0001403")/*@res "����"*/,
					ResHelper.getString("common", "UC000-0000140")/* @res "��Ա���" */,
					ResHelper.getString("common","UC000-0004064")/*@res "����"*/,
					ResHelper.getString("common","UC000-0003300")/*@res "ְ��"*/,
					ResHelper.getString("common","UC000-0001653")/*@res "��λ"*/,
					ResHelper.getString("60130payfile","060130payfile0295")/*@res "��֯"*/,
					"pk_psnjob", "pk_psnorg","pk_psndoc",
					PayfileVO.WORKORG, PayfileVO.WORKORGVID,PayfileVO.WORKDEPT,PayfileVO.WORKDEPTVID,
					PayfileVO.PK_FINANCEORG,PayfileVO.PK_FINANACEDEPT,PayfileVO.FIPORGVID,PayfileVO.FIPDEPTVID,
					PayfileVO.PK_LIABILITYDEPT,
					PayfileVO.PK_LIABILITYORG, PayfileVO.LIBDEPTVID,
					PayfileVO.LIBORGVID, PayfileVO.ASSGID, PayfileVO.PARTFLAG ,PayfileVO.TAXORG};
			// �ؼ���
			String[] saBodyColKeyName = { "selectflag", "clerkcode",  "psnname","psnclname",
					"deptname", "jobname", "postname", "orgname", "pk_psnjob",
					"pk_psnorg", "pk_psndoc",
					PayfileVO.WORKORG, PayfileVO.WORKORGVID,PayfileVO.WORKDEPT,PayfileVO.WORKDEPTVID,
					PayfileVO.PK_FINANCEORG,PayfileVO.PK_FINANACEDEPT,PayfileVO.FIPORGVID,PayfileVO.FIPDEPTVID,
					PayfileVO.PK_LIABILITYDEPT,
					PayfileVO.PK_LIABILITYORG, PayfileVO.LIBDEPTVID,
					PayfileVO.LIBORGVID, PayfileVO.ASSGID, PayfileVO.PARTFLAG ,PayfileVO.TAXORG};

			BillItem[] biaBody = new BillItem[saBodyColName.length];

			for (int i = 0; i < saBodyColName.length; i++) {
				biaBody[i] = new BillItem();
				biaBody[i].setName(saBodyColName[i]);
				biaBody[i].setKey(saBodyColKeyName[i]);
				biaBody[i].setNull(false);
				biaBody[i].setWidth(100);
				biaBody[i].setEnabled(false);
				if (i == 0) {
					biaBody[i].setEdit(true);
				} else {
					biaBody[i].setEdit(false);
				}
				if (saBodyColKeyName[i].equals("selectflag")) {
					biaBody[i].setWidth(40);
					biaBody[i].setDataType(BillItem.BOOLEAN);
					((UICheckBox) (biaBody[i].getComponent())).setHorizontalAlignment(UICheckBox.CENTER);
				} else {
					biaBody[i].setDataType(BillItem.STRING);
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
			//getPsnBillScrollPane().clearNotEditAction();
			//getPsnBillScrollPane().removeBillTableLockListener();
			Component[]  comps = getPsnBillScrollPane().getTable().getHeaderPopupMenu().getComponents();
			for( int i=0 ; null != comps && i< comps.length ; i++) {
				if(comps[i] instanceof JMenuItem) {
					JMenuItem jpm = (JMenuItem) comps[i];
					if( null != jpm.getLabel() && jpm.getLabel().equals(NCLangRes.getInstance().getStrByID("_Bill", "UPP_Bill-000009")/*
					 * @res
					 * "����"
					 */))
					{
						getPsnBillScrollPane().getTable().getHeaderPopupMenu().remove(comps[i]);
					}
				}

			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	/**
	 * @author zhoucx on 2009-12-28
	 * @see nc.ui.pub.beans.wizard.WizardStep#validate()
	 */
	@Override
	public void validate() throws WizardStepValidateException {
		super.validate();
		PayfileVO[] selectVOs = getSelectedVO();
		if (ArrayUtils.isEmpty(selectVOs)) {
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg(ResHelper.getString("60130payfile","060130payfile0245")/*@res "��ʾ"*/, ResHelper.getString("60130payfile","060130payfile0281")/*@res "��ѡ����Ա!"*/);
			throw e;
		}
		// �ж��Ƿ����ظ���Ա
		List<PayfileVO> list = new ArrayList<PayfileVO>();
		if (selectVOs != null) {
			for (PayfileVO generalVO : selectVOs) {
				if (personIsInList(list, generalVO)) {
					WizardStepValidateException e = new WizardStepValidateException();
					e.addMsg(ResHelper.getString("60130payfile","060130payfile0245")/*@res "��ʾ"*/, ResHelper.getString("60130payfile","060130payfile0287")/*@res "���ظ�����Ա, ��ѡ��"*/);
					throw e;
				}
				list.add(generalVO);
			}
		}

	}

	/**
	 * �ж��Ƿ��ظ�
	 *
	 * @author zhangg
	 * @param list
	 * @param psnVO
	 * @return
	 */
	private boolean personIsInList(List<PayfileVO> list, PayfileVO psnVO) {
		for (PayfileVO psnVO2 : list) {
			if (psnVO2.getPk_psndoc().equals(psnVO.getPk_psndoc())) {
				return true;
			}
		}
		return false;
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
		datas = getModel().getPayfileVOs();
		PayfileVO[] selectVOs = getModel().getSelectVOs();
		if (!ArrayUtils.isEmpty(selectVOs)) {
			Map<String, PayfileVO> map = new HashMap<String, PayfileVO>();
			for (int i = 0; i < selectVOs.length; i++) {
				map.put(selectVOs[i].getPk_psndoc(), selectVOs[i]);
			}
			for (int i = 0; i < datas.length; i++) {
				if (map.containsKey(datas[i].getPk_psndoc())) {
					datas[i] = map.get(datas[i].getPk_psndoc());
				}
			}
		}
		getPsnBillScrollPane().getTableModel().setBodyDataVO(datas);
		if (ArrayUtils.isEmpty(selectVOs)) {
			getPsnBillScrollPane().selectAllRows();
		}
	}

	/**
	 * ��ȡѡ�е�VO
	 *
	 * @author liangxr on 2010-1-26
	 * @return
	 */
	private PayfileVO[] getSelectedVO() {
		return (PayfileVO[]) getPsnBillScrollPane().getSelectedBodyVOs(PayfileVO.class);
	}

	@Override
	public void stepDisactived(WizardStepEvent event) {

		// ��ȡѡ�е�VO
		PayfileVO[] selectVOs = getSelectedVO();
		// ������ѡ��VO
		getModel().setSelectVOs(selectVOs);
		// ���»���ȫ��VO�����Ժ��践��ʱ����ԭ����ѡ��״̬
		getModel().setPayfileVOs(datas);
		datas = null;
		selectVOs = null;
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		if ((Boolean) e.getValue()) {
			datas[e.getRow()].setSelectflag(UFBoolean.TRUE);
		} else {
			datas[e.getRow()].setSelectflag(UFBoolean.FALSE);
		}

	}

	@Override
	public void bodyRowChange(BillEditEvent e) {

	}

}