package nc.ui.wa.item.action;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.SaveAction;
import nc.ui.hr.uif2.action.SaveAddAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.para.SysInitBO_Client;
import nc.ui.wa.item.util.WaExpandTableUtil;
import nc.ui.wa.item.view.ItemBillFormEditor;
import nc.ui.wa.item.view.custom.ItemDataSourcePanel;
import nc.ui.wa.pub.DefaultWaActionInterceptor;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.item.PropertyEnumVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.ParaConstant;
import nc.vo.wa.pub.util.WaConstant;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings({ "restriction", "deprecation" })
public class SaveItemInterceptor extends DefaultWaActionInterceptor {

	@Override
	public boolean beforeDoAction(Action action, ActionEvent e) {

		WaItemVO vo = null;
		;
		if (action instanceof SaveAction) {
			vo = (WaItemVO) ((SaveAction) action).getEditor().getValue();
		} else {
			vo = (WaItemVO) ((SaveAction) ((SaveAddAction) action).getSaveAction()).getEditor().getValue();
		}

		if (vo != null) {
			if (vo.getIitemtype().equals(TypeEnumVO.FLOATTYPE.value())) {
				if (vo.getIflddecimal() == null) {
					showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0011")/*
																						 * @
																						 * res
																						 * "��ֵ�͵�С��λ������Ϊ��"
																						 */);
					return false;
				}
				// if (vo.getIflddecimal().intValue() >=
				// vo.getIfldwidth().intValue()) {
				// showErrorDlg(ResHelper.getString("60130glbitem","060130glbitem0012")/*@res
				// "н����Ŀ��С��λ����ҪС�����ݳ��ȡ�"*/);
				// return false;
				// }
				try {
					if (itemMustInTotalItem() && StringUtils.isBlank(vo.getTotalitem())) {
						showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0013")/*
																							 * @
																							 * res
																							 * "����н����Ŀ��������Ԥ��,��ѡ��Ԥ����Ŀ"
																							 */);
						return false;
					}
				} catch (BusinessException e2) {
					Logger.error(e2.getMessage(), e2);
					showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0014")/*
																						 * @
																						 * res
																						 * "�жϹ���н����Ŀ�Ƿ��������Ԥ��ʧ��"
																						 */);
					return false;
				}
			}
		}
		if (vo != null && vo.getIproperty() == PropertyEnumVO.SYSTEM.value()) {
			showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0015")/*
																				 * @
																				 * res
																				 * "�������Բ�����:ϵͳ��"
																				 */);
			return false;
		}

		if (vo.getFromEnumVO().equals(FromEnumVO.FORMULA)) {
			if (StringUtil.isEmpty(vo.getVformula())) {
				showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0016")/*
																					 * @
																					 * res
																					 * "��ʽ����Ϊ��"
																					 */);
				return false;
			}
		} else if (vo.getFromEnumVO().equals(FromEnumVO.HI)) {
			if (StringUtil.isEmpty(vo.getVformula())) {
				showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0017")/*
																					 * @
																					 * res
																					 * "���º�������Ϊ��"
																					 */);
				return false;
			}
		} else if (vo.getFromEnumVO().equals(FromEnumVO.WAORTHER)) {
			if (StringUtil.isEmpty(vo.getVformula())) {
				showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0018")/*
																					 * @
																					 * res
																					 * "н�ʺ�������Ϊ��"
																					 */);
				return false;
			}
		} else if (vo.getFromEnumVO().equals(FromEnumVO.WA_WAGEFORM)) {
			if (StringUtil.isEmpty(vo.getVformula())) {
				showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0019")/*
																					 * @
																					 * res
																					 * "н�ʹ������Ϊ��"
																					 */);
				return false;
			}
			// liangxr�����Դ��н�ʹ�����ж���Ŀ��С��λ���Ƿ�
			ItemDataSourcePanel idp = null;
			if (action instanceof SaveAction) {
				idp = ((ItemBillFormEditor) ((SaveAction) action).getEditor()).getItemDataSourcePanel();
			} else {
				idp = ((ItemBillFormEditor) ((SaveAction) ((SaveAddAction) action).getSaveAction()).getEditor())
						.getItemDataSourcePanel();
			}

			int ruleDecimal = (Integer) idp.getWageForm().getRefModel().getValue("iflddecimal");
			int itemDecimal = vo.getIflddecimal();
			if (itemDecimal < ruleDecimal) {
				String msg = ResHelper.getString("60130glbitem", "060130glbitem0020")/*
																					 * @
																					 * res
																					 * "н����Ŀ��С��λ��С��н�ʹ�����С��λ�����Ƿ����?"
																					 */;
				if (MessageDialog.showYesNoDlg(getContainer(),
						ResHelper.getString("60130glbitem", "060130glbitem0021")/*
																				 * @
																				 * res
																				 * "��ʾ"
																				 */, msg) == UIDialog.ID_NO) {
					return false;
				}
			}
			// end

		} else if (vo.getFromEnumVO().equals(FromEnumVO.WA_GRADE)) {
			if (StringUtil.isEmpty(vo.getVformula())) {
				showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0022")/*
																					 * @
																					 * res
																					 * "н�ʱ�׼����Ϊ��"
																					 */);
				return false;
			}
		}

		if (isAddSave(vo)) {
			// �˲�Ԥ����Ŀ���Ƿ��� ��������չ
			WaExpandTableUtil util = new WaExpandTableUtil(getContainer(), vo);
			try {
				if (!util.expandTable()) {
					showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0023")/*
																						 * @
																						 * res
																						 * "н�ʷ��ű���չʧ��"
																						 */);
					return false;
				}

			} catch (BusinessException e2) {
				showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0023")/*
																					 * @
																					 * res
																					 * "н�ʷ��ű���չʧ��"
																					 */);
				return false;
			}

		}
		// MOD {����н����Ŀ����ʱУ���Ƿ��м������̯�ֶ��߼�} kevin.nie 2017-09-12 start
		if (vo != null && 3 != vo.getIproperty()) {
			String def1 = vo.getDef1(); // ��̯����
			String def2 = vo.getDef2(); // �跽��Ŀ
			String def3 = vo.getDef3(); // �跽��Ӧ�̱���
			String def4 = vo.getDef4(); // ������Ŀ
			String def5 = vo.getDef5(); // ������Ӧ�̱���
			String shareCode = null;
			if (StringUtils.isNotEmpty(def1)) {
				ItemBillFormEditor itemEditer = new ItemBillFormEditor();
				Map<String, DefdocVO> shareMap = itemEditer.getShareDocMap();
				if (null != shareMap) {
					DefdocVO defVO = shareMap.get(def1);
					if (null != defVO) {
						shareCode = defVO.getCode();
					}
				}
			}

			if (vo.getMid().booleanValue()) {
				if (StringUtils.isNotEmpty(def1) || StringUtils.isNotEmpty(def2) || StringUtils.isNotEmpty(def3)
						|| StringUtils.isNotEmpty(def4) || StringUtils.isNotEmpty(def5)) {
					showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0043")/*
																						 * @
																						 * res
																						 * "��н����ĿΪ�м���������̯"
																						 */);
					return false;
				}
			} else if (StringUtils.isNotEmpty(shareCode) && WaConstant.SHARE_NOT_SYS__SHARED.equals(shareCode)) {
				if (StringUtils.isNotEmpty(def2) || StringUtils.isNotEmpty(def3) || StringUtils.isNotEmpty(def4)
						|| StringUtils.isNotEmpty(def5)) {
					showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0045")/*
																						 * @
																						 * res
																						 * "��н����Ŀ������ϵͳ��̯"
																						 */);
					return false;
				}
			} else {
				if (StringUtils.isEmpty(def1) || StringUtils.isEmpty(def2) || StringUtils.isEmpty(def4)) {
					showErrorDlg(ResHelper.getString("60130glbitem", "060130glbitem0044")/*
																						 * @
																						 * res
																						 * "��н����Ŀ���м����̯��������Ӧ�跽��Ŀ����Ӧ������Ŀ������Ϊ��"
																						 */);
					return false;
				}
			}
		}
		// {����н����Ŀ����ʱУ���Ƿ��м������̯�ֶ��߼�} kevin.nie 2017-09-12 end

		return super.beforeDoAction(action, e);

	}

	private boolean isAddSave(WaItemVO vo) {
		return StringUtils.isBlank(vo.getPk_wa_item());

	}

	/**
	 * �жϼ����Ƿ������¼����ӷ�����Ŀ
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private boolean itemMustInTotalItem() throws BusinessException {
		//
		UFBoolean must = UFBoolean.TRUE;
		must = SysInitBO_Client.getParaBoolean(getModel().getContext().getPk_group(), ParaConstant.ITEM_INTO_AMOUNT);
		// must =
		// NCLocator.getInstance().lookup(IHrPara.class).getBooleanValue(getModel().getContext().getPk_group(),
		// ParaConstant.ITEM_INTO_AMOUNT, null, null);

		return must.booleanValue();

	}

}