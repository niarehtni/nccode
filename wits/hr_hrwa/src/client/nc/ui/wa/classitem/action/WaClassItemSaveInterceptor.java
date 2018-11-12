package nc.ui.wa.classitem.action;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IItemQueryService;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.hr.uif2.action.SaveAction;
import nc.ui.hr.uif2.action.SaveAddAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.para.SysInitBO_Client;
import nc.ui.uif2.editor.IEditor;
import nc.ui.wa.item.util.ItemUtils;
import nc.ui.wa.item.util.WaExpandTableUtil;
import nc.ui.wa.item.view.ItemBillFormEditor;
import nc.ui.wa.item.view.custom.ItemDataSourcePanel;
import nc.ui.wa.pub.DefaultWaActionInterceptor;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.func.WaDatasourceManager;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.ParaConstant;
import nc.vo.wa.pub.util.WaConstant;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings({ "restriction", "deprecation" })
public class WaClassItemSaveInterceptor extends DefaultWaActionInterceptor {

	@Override
	public boolean beforeDoAction(Action action, ActionEvent e) {
		// У���Ƿ��Ѿ�ѡ��������Դ��֯
		IEditor editor;
		WaClassItemVO vo;
		if (action instanceof SaveAction) {
			editor = ((SaveAction) action).getEditor();
			vo = (WaClassItemVO) ((SaveAction) action).getEditor().getValue();
		} else {
			vo = (WaClassItemVO) ((SaveAction) ((SaveAddAction) action).getSaveAction()).getEditor().getValue();
			editor = ((SaveAction) ((SaveAddAction) action).getSaveAction()).getEditor();
		}

		if (vo.getPk_wa_item() == null) {
			try {
				if (isOrgNode() && !groupAllowAddItem()) {
					showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0199")/*
																						 * @
																						 * res
																						 * "���Ų������¼����ӹ���н����Ŀ,����ѡ�񹫹�н����Ŀ��"
																						 */);
					return false;
				}
			} catch (BusinessException e1) {
				Logger.error(e1.getMessage(), e1);
				showErrorDlg(e1.getMessage());
			}
		}

		if (vo.getTypeEnumVO().equals(TypeEnumVO.FLOATTYPE)) {
			if (vo.getRound_type() == null) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0200")/*
																					 * @
																					 * res
																					 * "��ֵ����Ŀ����λ��ʽ����Ϊ��"
																					 */);
				return false;
			}
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
			// showErrorDlg(ResHelper.getString("60130payitem","060130payitem0201")/*@res
			// "н����Ŀ��С��λ����ҪС�����ݳ��ȡ�"*/);
			// return false;
			// }
			try {
				WaItemVO waItem = NCLocator.getInstance().lookup(IItemQueryService.class)
						.queryWaItemVOByPk(vo.getPk_wa_item());
				if (waItem != null && null != vo.getIflddecimal()
						&& vo.getIflddecimal().intValue() > waItem.getIflddecimal().intValue()) {
					showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0202")/*
																						 * @
																						 * res
																						 * "н�ʷ�����Ŀ��С��λ�����ܴ��ڹ���н����Ŀ��С��λ����"
																						 */);
					return false;
				}
			} catch (BusinessException e1) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0203")/*
																					 * @
																					 * res
																					 * "û���ҵ���Ӧ�Ĺ���н����Ŀ��"
																					 */);
				return false;
			}
		}

		// �����ʽΪ�գ���Ҫ���ݲ�ͬģ�������ʾ
		String formular = vo.getVformula();
		//
		if (StringUtil.isEmpty(formular)) {
			if (vo.getFromEnumVO().equals(FromEnumVO.HI)) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0205")/*
																					 * @
																					 * res
																					 * "���º�������Ϊ��"
																					 */);
				return false;
			} else if (vo.getFromEnumVO().equals(FromEnumVO.WAORTHER)) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0206")/*
																					 * @
																					 * res
																					 * "н�ʺ�������Ϊ��"
																					 */);
				return false;
			} else if (vo.getFromEnumVO().equals(FromEnumVO.TA)) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0038")/*
																					 * @
																					 * res
																					 * "���ں�������Ϊ��"
																					 */);
				return false;
			}
			if (vo.getFromEnumVO().equals(FromEnumVO.WA_WAGEFORM)) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0207")/*
																					 * @
																					 * res
																					 * "н�ʹ������Ϊ��"
																					 */);
				return false;
			} else if (vo.getFromEnumVO().equals(FromEnumVO.FORMULA)
					&& (!ItemUtils.isSpecialSystemItem(vo.getItemkey()))) {
				// ���Թ�ʽ�����Ҳ���ϵͳ��Ŀ
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0204")/*
																					 * @
																					 * res
																					 * "��ʽ����Ϊ��"
																					 */);
				return false;
			} else if (vo.getFromEnumVO().equals(FromEnumVO.WA_GRADE)) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0210")/*
																					 * @
																					 * res
																					 * "н�ʱ�׼����Ϊ��"
																					 */);
				return false;
			} else if (WaDatasourceManager.isOtherDatasource(vo.getIfromflag())) {
				// �������������Դ����ʾ����ʽ����Ϊ�ա�
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0204")/*
																					 * @
																					 * res
																					 * "��ʽ����Ϊ��"
																					 */);
				return false;
			}

		}

		// �����ʽ��Ϊ�ա�н�ʹ������Ҫ�жϡ�н����Ŀ��С��λ��С��н�ʹ�����С��λ����
		if (!StringUtil.isEmpty(formular)) {
			if (vo.getFromEnumVO().equals(FromEnumVO.WA_WAGEFORM)) {
				{
					ItemDataSourcePanel idp = ((ItemBillFormEditor) editor).getItemDataSourcePanel();
					int ruleDecimal = (Integer) idp.getWageForm().getRefModel().getValue("iflddecimal");
					int itemDecimal = vo.getIflddecimal();
					if (itemDecimal < ruleDecimal) {
						String msg = ResHelper.getString("60130payitem", "060130payitem0208")/*
																							 * @
																							 * res
																							 * "н����Ŀ��С��λ��С��н�ʹ�����С��λ�����Ƿ����?"
																							 */;
						if (MessageDialog.showYesNoDlg(getContainer(),
								ResHelper.getString("60130payitem", "060130payitem0209")/*
																						 * @
																						 * res
																						 * "��ʾ"
																						 */, msg) == UIDialog.ID_NO) {
							return false;
						}
					}
				}
			}
		}

		// �Ƿ���Ҫ��չ��˽��������Ҫ��չ������������Ҫ��
		if (needExpand(vo)) {
			WaExpandTableUtil util = new WaExpandTableUtil(getContainer(), vo);
			try {
				if (!util.expandTable()) {
					showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0211")/*
																						 * @
																						 * res
																						 * "н�ʷ��ű���չʧ��"
																						 */);
					return false;
				}

			} catch (BusinessException e2) {
				showErrorDlg(ResHelper.getString("60130payitem", "060130payitem0211")/*
																					 * @
																					 * res
																					 * "н�ʷ��ű���չʧ��"
																					 */);
				return false;
			}
		}
		// MOD {н�ʷ�����Ŀ����ʱУ���Ƿ��м������̯�ֶ��߼�} kevin.nie 2017-09-12 start
		if (vo != null && StringUtils.isNotEmpty(vo.getPk_wa_item())) {
			IUAPQueryBS queryBS = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			WaItemVO waitemVO = null;
			try {
				waitemVO = (WaItemVO) queryBS.retrieveByPK(WaItemVO.class, vo.getPk_wa_item());
			} catch (BusinessException e1) {
				Logger.error(e);
			}
			if (null != waitemVO && 3 != waitemVO.getIproperty()) {
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
		}
		// {н�ʷ�����Ŀ����ʱУ���Ƿ��м������̯�ֶ��߼�} kevin.nie 2017-09-12 end
		return super.beforeDoAction(action, e);

	}

	private boolean isOrgNode() {
		return getModel().getContext().getNodeType() == NODE_TYPE.ORG_NODE;
	}

	/**
	 * �жϼ����Ƿ������¼����ӷ�����Ŀ
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private boolean groupAllowAddItem() throws BusinessException {
		//
		UFBoolean allowed = UFBoolean.TRUE;
		allowed = SysInitBO_Client.getParaBoolean(getModel().getContext().getPk_group(), ParaConstant.ADDITEM_PRVW);
		// allowed =
		// NCLocator.getInstance().lookup(IHrPara.class).getBooleanValue(
		// getModel().getContext().getPk_group(), ParaConstant.ADDITEM_PRVW,
		// null, null);

		return allowed.booleanValue();

	}

	private boolean needExpand(WaClassItemVO vo) {
		if (StringUtils.isBlank(vo.getPk_wa_item())) {
			// ����˽����Ŀ����Ҫ��չ
			return true;
		}

		return false;

	}

}