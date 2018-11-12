package nc.ui.ta.overtime.register.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ILeaveQueryService;
import nc.itf.ta.IPeriodQueryService;
import nc.pubitf.para.SysInitQuery;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.uif2.view.HrBillListView;
import nc.ui.pub.beans.UIDialog;
import nc.ui.ta.overtime.register.model.OvertimeRegAppModel;
import nc.ui.ta.overtime.register.view.OvertimeRestDialog;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.util.remotecallcombination.IRemoteCallCombinatorService;
import nc.vo.util.remotecallcombination.RemoteCallInfo;
import nc.vo.util.remotecallcombination.RemoteCallResult;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 转调休 action
 * 
 * @author yucheng
 * 
 */
@SuppressWarnings("serial")
public class RestOvertimeAction extends HrAction {

	private HrBillListView listView;
	private OvertimeRestDialog dlg;

	public RestOvertimeAction() {
		super();
		setCode("restovertime");
		setBtnName(PublicLangRes.OVERTOREST());
		putValue(SHORT_DESCRIPTION, PublicLangRes.OVERTOREST());
	}

	@SuppressWarnings("unchecked")
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
			// clone出来的overtimehour的值不对，在此处理
			vo.setOvertimehour(((OvertimeRegVO) selDatas[i]).getOvertimehour());
			// 已转调休或单据实际时长为0，不加入转调休列表
			if (vo.getIstorest().booleanValue() || vo.getActhour().equals(UFDouble.ZERO_DBL)) {
				continue;
			}
			int row = getOvertimeRegAppModel().findBusinessData(vo);
			// 将天转换为小时
			if (PublicLangRes.DAY().equals(
					getListView().getBillListPanel().getHeadBillModel()
							.getValueAt(row, "pk_overtimetypecopy.timeitemunit"))) {
				vo.setActhour(vo.getActhour().multiply(dayToHour));
			}
			vos.add(vo);
		}
		if (CollectionUtils.isEmpty(vos))
			throw new BusinessException(ResHelper.getString("6017overtime", "06017overtime0024")/*
																								 * @
																								 * res
																								 * "没有要转调休的单据！"
																								 */);
		// 初始化dialog
		RemoteCallResult[] params = getParam();
		getDlg().setPeriodMap(params[1] == null ? null : (Map<String, String[]>) params[1].getResult());
		getDlg().initData(params[0] == null ? null : (String[]) params[0].getResult(),
				vos.toArray(new OvertimeRegVO[0]));
		if (UIDialog.ID_OK != getDlg().showModal()) {
			putValue(HrAction.MESSAGE_AFTER_ACTION, PublicLangRes.CANCELED());
			return;
		}
	}

	/**
	 * 取考勤期间数据和加班转调休数据
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private RemoteCallResult[] getParam() throws BusinessException {
		// String[] itfName = new
		// String[]{ILeaveQueryService.class.getName(),IPeriodQueryService.class.getName()};
		// String[] methodNames = new
		// String[]{"queryOverToRestPeriod","queryPeriodYearAndMonthByOrg"};
		// Class[][] types = new Class[2][];
		// types[0] = new Class[]{String.class, UFDate.class};
		// types[1] = new Class[]{String.class};
		// Object[][] param = new Object[2][];
		// param[0] = new
		// Object[]{getContext().getPk_org(),WorkbenchEnvironment.getInstance().getBusiDate()};
		// param[1] = new Object[]{getContext().getPk_org()};

		List<RemoteCallInfo> remoteList = new ArrayList<RemoteCallInfo>();
		RemoteCallInfo queryOverToRestPeriod = new RemoteCallInfo();
		queryOverToRestPeriod.setClassName(ILeaveQueryService.class.getName());
		queryOverToRestPeriod.setMethodName("queryOverToRestPeriod");
		queryOverToRestPeriod.setParamTypes(new Class[] { String.class, UFDate.class });
		queryOverToRestPeriod.setParams(new Object[] { getContext().getPk_org(),
				WorkbenchEnvironment.getInstance().getBusiDate() });
		remoteList.add(queryOverToRestPeriod);

		RemoteCallInfo queryPeriodYearAndMonthByOrg = new RemoteCallInfo();
		queryPeriodYearAndMonthByOrg.setClassName(IPeriodQueryService.class.getName());
		queryPeriodYearAndMonthByOrg.setMethodName("queryPeriodYearAndMonthByOrg");
		queryPeriodYearAndMonthByOrg.setParamTypes(new Class[] { String.class });
		queryPeriodYearAndMonthByOrg.setParams(new Object[] { getContext().getPk_org() });
		remoteList.add(queryPeriodYearAndMonthByOrg);
		// 打包执行
		List<RemoteCallResult> returnList = NCLocator.getInstance().lookup(IRemoteCallCombinatorService.class)
				.doRemoteCall(remoteList);
		if (returnList.isEmpty()) {
			return null;
		}
		return returnList.toArray(new RemoteCallResult[0]);

		// return
		// NCLocator.getInstance().lookup(IServiceHome.class).execute(itfName,
		// methodNames, types, param);
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

		if (StringUtils.isEmpty(getContext().getPk_org())) {
			return false;
		}
		if (getOvertimeRegAppModel().getRowCount() <= 0) {
			return false;
		}
		return super.isActionEnable();
	}

	public HrBillListView getListView() {
		return listView;
	}

	public void setListView(HrBillListView listView) {
		this.listView = listView;
	}

	public OvertimeRestDialog getDlg() {
		if (dlg == null) {
			dlg = new OvertimeRestDialog(getEntranceUI(), PublicLangRes.OVERTOREST());
			dlg.setModel(getOvertimeRegAppModel());
			dlg.setListView(getListView());
			dlg.initUI();
		}
		return dlg;
	}
}