package nc.ui.ta.overtime.register.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.ta.IOvertimeRegisterManageMaintain;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.uif2.view.HrBillListView;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.ta.overtime.register.model.OvertimeRegAppModel;
import nc.ui.ta.overtime.register.view.OvertimeRestCommonDialog;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.pub.TALoginContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 反转调休 action
 * 
 * @author yucheng
 * 
 */
@SuppressWarnings("serial")
public class UnRestOvertimeAction extends HrAction {

	private HrBillListView listView;
	private OvertimeRestCommonDialog dlg;

	public UnRestOvertimeAction() {
		super();
		setCode("unrestovertime");
		setBtnName(PublicLangRes.FANOVERTOREST());
		putValue(SHORT_DESCRIPTION, PublicLangRes.FANOVERTOREST());
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] selDatas = getOvertimeRegAppModel().getSelectedOperaDatas();
		if (ArrayUtils.isEmpty(selDatas))
			throw new BusinessException(PublicLangRes.SELECTDATA());
		double dayToHour = ((TALoginContext) getOvertimeRegAppModel().getContext()).getAllParams().getTimeRuleVO()
				.getDaytohour2();
		List<OvertimeRegVO> vos = new ArrayList<OvertimeRegVO>();
		for (int i = 0; i < selDatas.length; i++) {
			OvertimeRegVO vo = (OvertimeRegVO) ((OvertimeRegVO) selDatas[i]).clone();
			// clone出来的overtimehour的值不真确，在此处理
			vo.setOvertimehour(((OvertimeRegVO) selDatas[i]).getOvertimehour());
			if (!vo.getIstorest().booleanValue())
				continue;
			int row = getOvertimeRegAppModel().findBusinessData(vo);
			// 将天转换为小时
			if (PublicLangRes.DAY().equals(
					getListView().getBillListPanel().getHeadBillModel()
							.getValueAt(row, "pk_overtimetypecopy.timeitemunit")))
				vo.setActhour(vo.getActhour().multiply(dayToHour));
			vos.add(vo);
		}
		if (CollectionUtils.isEmpty(vos))
			throw new BusinessException(ResHelper.getString("6017overtime", "06017overtime0021")/*
																								 * @
																								 * res
																								 * "没有要反转调休的单据！"
																								 */);
		if (UIDialog.ID_YES != MessageDialog.showYesNoDlg(getEntranceUI(), null,
				ResHelper.getString("6017overtime", "06017overtime0060")/*
																		 * @res
																		 * "您确定要反转调休所选数据吗?"
																		 */, UIDialog.ID_NO)) {
			setCancelMsg();
			return;
		}
		OvertimeRegVO[] returnVOs = NCLocator.getInstance().lookup(IOvertimeRegisterManageMaintain.class)
				.unOver2Rest(vos.toArray(new OvertimeRegVO[0]));
		getOvertimeRegAppModel().directlyUpdate(returnVOs);
	}

	private OvertimeRegAppModel getOvertimeRegAppModel() {
		return (OvertimeRegAppModel) getModel();
	}

	@Override
	protected boolean isActionEnable() {
		// MOD(台灣新法令) ssx added on 2018-05-29
		UFBoolean isEnabled;
		try {
			isEnabled = new UFBoolean(SysInitQuery.getParaString(getContext().getPk_org(), "TBMOTSEG"));
			if (isEnabled != null && isEnabled.booleanValue()) {
				return false;
			}

		} catch (Exception e) {
		}
		// end

		if (StringUtils.isEmpty(getContext().getPk_org()))
			return false;
		if (getOvertimeRegAppModel().getRowCount() <= 0)
			return false;
		return super.isActionEnable();
	}

	public HrBillListView getListView() {
		return listView;
	}

	public void setListView(HrBillListView listView) {
		this.listView = listView;
	}

	public OvertimeRestCommonDialog getDlg() {
		if (dlg == null) {
			dlg = new OvertimeRestCommonDialog(getEntranceUI(), PublicLangRes.FANOVERTOREST());
			dlg.setModel(getOvertimeRegAppModel());
			dlg.setListView(getListView());
			dlg.initUI();
		}
		return dlg;
	}
}