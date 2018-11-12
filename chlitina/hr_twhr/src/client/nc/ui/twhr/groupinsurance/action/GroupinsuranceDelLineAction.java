package nc.ui.twhr.groupinsurance.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.twhr.IGroupinsuranceMaintain;
import nc.ui.pubapp.uif2app.actions.batch.BatchDelLineAction;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.groupinsurance.GroupInsuranceSettingVO;

public class GroupinsuranceDelLineAction extends BatchDelLineAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 3944015058826765868L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		IGroupinsuranceMaintain srv = NCLocator.getInstance().lookup(
				IGroupinsuranceMaintain.class);
		GroupInsuranceSettingVO vo = (GroupInsuranceSettingVO) getModel()
				.getSelectedData();
		boolean existsRef = srv.isExistsGroupInsuranceSettingRef(vo);

		if (existsRef) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("68J61035", "01035001-0010")/*
																			 * @
																			 * res
																			 * 删除失败
																			 * ，
																			 * 团保费率设定已被使用
																			 * 。
																			 */);
		}

		super.doAction(e);
	}

}
