package nc.ui.wa.paydata.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.wa.paydata.view.dialog.ItemGroupSelectDialog;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.sm.UserVO;
import nc.vo.wa.itemgroup.ItemGroupVO;
import nc.vo.wa.pub.WaState;

import org.apache.commons.lang.StringUtils;

public class PDFEmailSendAction extends PayDataBaseAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = -6116710789493297603L;

	public PDFEmailSendAction() {
		this.setBtnName("�l��н�Y��");
		this.setCode("PDFEmailSendAction");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doAction(ActionEvent e) throws Exception {
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		UserVO uservo = (UserVO) query.retrieveByPK(UserVO.class, this.getContext().getPk_loginUser());
		List<SuperVO> payfileVos = getPaydataModel().getData();

		if (payfileVos == null || payfileVos.size() == 0) {
			throw new BusinessException("δ�x��Ҫ�l���]����н�Y�Y�ϡ�");
		}

		if (uservo != null && !StringUtils.isEmpty(uservo.getPk_base_doc())) {
			PsndocVO psnvo = (PsndocVO) query.retrieveByPK(PsndocVO.class, uservo.getPk_base_doc());
			if (StringUtils.isEmpty(psnvo.getEmail())) {
				throw new BusinessException("��ǰ�Ñ��]���O��Email��ַ��Ո�_�J����ԇ��");
			}
		} else {
			throw new BusinessException("ȡ�Ñ�Email�e�`��Ո�_�J����ԇ��");
		}

		ItemGroupSelectDialog dlg = new ItemGroupSelectDialog(getModel().getContext().getEntranceUI(), "н�Y�Ŀ�ֽM", false);
		dlg.showModal();
		if (dlg.getSelectedItemGroup() == null) {
			throw new BusinessException("δ�x����õ�н�Y�Ŀ�ֽM��");
		} else {
			ItemGroupVO itemGrpVO = dlg.getSelectedItemGroup();
			IPaydataManageService service = NCLocator.getInstance().lookup(IPaydataManageService.class);
			service.sendPayslipByEmail(payfileVos.toArray(new SuperVO[0]), itemGrpVO.getPk_itemgroup(), false);
		}
	}

	@Override
	public Set<WaState> getEnableStateSet() {
		if (waStateSet == null) {
			waStateSet = new HashSet<WaState>();
			waStateSet.add(WaState.CLASS_ALL_PAY);
			waStateSet.add(WaState.CLASS_MONTH_END);
		}
		return waStateSet;
	}
}
