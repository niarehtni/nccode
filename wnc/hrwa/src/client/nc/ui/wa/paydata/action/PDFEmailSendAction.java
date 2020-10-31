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
		this.setBtnName("發送薪資單");
		this.setCode("PDFEmailSendAction");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doAction(ActionEvent e) throws Exception {
		IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		UserVO uservo = (UserVO) query.retrieveByPK(UserVO.class, this.getContext().getPk_loginUser());
		List<SuperVO> payfileVos = getPaydataModel().getData();

		if (payfileVos == null || payfileVos.size() == 0) {
			throw new BusinessException("未選擇要發送郵件的薪資資料。");
		}

		if (uservo != null && !StringUtils.isEmpty(uservo.getPk_base_doc())) {
			PsndocVO psnvo = (PsndocVO) query.retrieveByPK(PsndocVO.class, uservo.getPk_base_doc());
			if (StringUtils.isEmpty(psnvo.getEmail())) {
				throw new BusinessException("當前用戶沒有設定Email地址，請確認後重試。");
			}
		} else {
			throw new BusinessException("取用戶Email錯誤，請確認後重試。");
		}

		ItemGroupSelectDialog dlg = new ItemGroupSelectDialog(getModel().getContext().getEntranceUI(), "薪資項目分組", false);
		dlg.showModal();
		if (dlg.getSelectedItemGroup() == null) {
			throw new BusinessException("未選擇可用的薪資項目分組。");
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
