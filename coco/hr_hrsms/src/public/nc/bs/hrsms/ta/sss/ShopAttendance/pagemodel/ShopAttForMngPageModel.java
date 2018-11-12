package nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel;

import java.util.Map;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrsms.ta.sss.ShopAttendance.ctrl.ShopAttendanceForMngViewMain;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.cata.ICatagoryDataProvider;
import nc.bs.hrss.pub.advpanel.cata.TestCatagoryDataProvider;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.utils.TAUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.LfwParameter;
import nc.uap.lfw.core.event.GridCellEvent;
import nc.uap.lfw.core.event.conf.EventConf;
import nc.uap.lfw.core.event.conf.EventSubmitRule;
import nc.uap.lfw.core.event.conf.ViewRule;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.lfw.jsp.uimeta.UIPanel;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.lateearly.LateEarlyVO;
import nc.vo.ta.timerule.TimeRuleVO;

public class ShopAttForMngPageModel extends AdvancePageModel {

	// ���ݼ�id����ѯ����
	public static final String DATASET_CONDITION = "dsCondition";

	public static final String APP_STATUS = "appstatus";
	// ״̬�������������
	public static final int STATUS_MACHINE_BROWSE = 1;
	// ״̬���ֹ��������
	public static final int STATUS_MANUAL_BROWSE = 2;
	// ״̬���ֹ����ڱ༭
	public static final int STATUS_MANUAL_EDIT = 3;

	// ���ݼ�id���ֹ���������
	public static final String DATASET_NAVI = "dsNavi";

	// �ؼ����������
	public static final String COMP_TABLE_NAVI = "tblNavi";
	// �ؼ����������ڱ��
	public static final String COMP_TABLE_MACHINE = "tblMachineData";
	// �ؼ����ֹ����ڱ��
	public static final String COMP_TABLE_MANUAL = "tblManualData";
	// �ֹ�����ҳǩ
	public static final String TAB_MANUAL = "tabManual";
	// ���ݼ��ֶΣ���ѯ����.��ʼ����
	public static final String FIELD_BEGIN_DATE = "begindate";
	// ���ݼ��ֶΣ���ѯ����.��ֹ����
	public static final String FIELD_END_DATE = "enddate";
	// ���ݼ��ֶΣ���ѯ����.�Ƿ����ڲ鿴ģʽ
	public static final String FIELD_DATE_MODE = "date_model";
	// ���ݼ��ֶΣ���ѯ����.����
	public static final String FIELD_DEPT = "pk_dept";
	// ���ݼ��ֶΣ���ѯ����.�Ƿ�����¼�����
	public static final String FIELD_DEPT_SUB = "include_sub";
	// ���ݼ��ֶΣ���ѯ����.��Ա
	public static final String FIELD_TIME_DATA_PSNDOC = "pk_psndoc";
	// ���ݼ��ֶΣ���ѯ����.�Ƿ�ֻ��ʾ�쳣
	public static final String FIELD_ONLY_SHOW_EXCEPTION = "onlyshowexception";
	// ���ݼ��ֶΣ�����.����
	public static final String FIELD_NAVI_PK = "pk";
	// ���ݼ��ֶΣ�����.����
	public static final String FIELD_NAVI_NAME = "name";
	// ���ݼ��ֶΣ���������.����
	public static final String FIELD_TIME_DATA_PSNNAME = "psnname";
	// ��ѯ������������
	public static final String FIELD_PK_PSNDOC_NAME = "pk_psndoc_name";
	// �ؼ�����������У�����
	public static final String COMP_TABLE_NAVI_COL_PK = "pk";
	// �ؼ�����������У�����
	public static final String COMP_TABLE_NAVI_COL_NAME = "name";

	// ҳ���ʶ - ����Ա�鿴
	public static final String PAGE_ID_TIMEDATAPSN = "timedatapsn";
	public static final String STATUS_BYNAME_BROWSE = "0";
	// ҳ���ʶ - �����ڲ鿴
	public static final String PAGE_ID_TIMEDATADATE = "timedatadate";
	public static final String STATUS_BYDATE_BROWSE = "1";
	
	// ������ʾ
	public static final String CONST_DAY_DISP = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res",
			"0c_ta-res0129")/* @res "�����ա�~����һ��~���ܶ���~��������~�����ġ�~�����塿~��������" */;

	@Override
	public String getBusinessEtag() {
		return String.valueOf(Math.random());
	}
	
	@Override
	protected String getFunCode() {
		return ShopAttendanceMngPageModel.TIMEDATAMNG_FUNCODE;
	}

	@Override
	protected String getQueryTempletKey() {
		return null;
	}

	@Override
	protected String getRightPage() {
		return null;
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		return new IPagePanel[] { new CanvasPanel(), new MngShopPanel(), new SimpleQueryPanel() };
	}

	@Override
	protected void initPageMetaStruct() {
		SessionUtil.getAppSession().setAttribute(ICatagoryDataProvider.SID_CATAGORY_PROVIDER,
				TestCatagoryDataProvider.class.getName());
		//
		super.initPageMetaStruct();
		// ��applicationContext��������Կ��ڵ����Ϳ��ڹ���
		TaAppContextUtil.addTaAppContext();
		Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(TaAppContextUtil.getHROrg());
		TaAppContextUtil.setTBMPeriodVOMap(periodMap);
		// ����ҳ���ǰ���Ա�������ڲ鿴������ʾ��
		setPsninfoVisible();
		// ���ز�ѯ����������������
		// setQueryNameVisible();
		LfwView widget = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		GridComp grid = (GridComp) widget.getViewComponents().getComponent(
				ShopAttForEmpPageModel.COMP_GRID_MANUAL_DATA);
		addGridCellEvent(widget, grid);
		// ����С��λ��
		setPrecision();
	}
	
	/**
	 * ���ݿ��ڹ��������ֶα���С��λ��
	 * 
	 */
	private void setPrecision(){
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		if(timeRuleVO == null){
			return;
		}
		String[] timeDatas = null;
		Integer timedecimal = timeRuleVO.getTimedecimal();
		LfwView view = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		Dataset dsMachine = ViewUtil.getDataset(view, ShopAttForEmpPageModel.DATASET_MACHINE);
		timeDatas = new String[]{TimeDataVO.MIDWAYOUTTIME};
		TAUtil.setPrecision(dsMachine, timedecimal, timeDatas);
		Dataset dsManual = ViewUtil.getDataset(view, ShopAttForEmpPageModel.DATASET_MANUAL);
		timeDatas = new String[]{LateEarlyVO.LATELENGTH, LateEarlyVO.EARLYLENGTH, LateEarlyVO.ABSENTHOUR, LateEarlyVO.NIGHTABSENTHOUR};
		TAUtil.setPrecision(dsManual, timedecimal, timeDatas);
		
	}

	/**
	 * ���õ�Ԫ���Ƿ�ɱ༭�¼�
	 * 
	 * @param grid
	 */
	private void addGridCellEvent(LfwView widget, GridComp grid) {
		EventSubmitRule sr = new EventSubmitRule();
		ViewRule wr = new ViewRule();
		wr.setId(widget.getId());
		sr.addViewRule(wr);
		EventConf conf = new EventConf();
		// conf.setJsEventClaszz(GridCellListener.class.getName());
		conf.setOnserver(false);
		LfwParameter param = new LfwParameter();
		param.setName("cellEvent");
		conf.addParam(param);
		conf.setName(GridCellEvent.BEFORE_EDIT);
		// ���JS
		conf.setScript("return beforEditClass(cellEvent)");
		grid.addEventConf(conf);
	}

	/**
	 * ����Ա�鿴ʱ������Ա��Ϣ��ص���
	 * 
	 */
	public void setPsninfoVisible() {

		UIMeta um = (UIMeta) LfwRuntimeEnvironment.getWebContext().getUIMeta();
		UIPanel panelLayout = (UIPanel) um.findChildById(ShopAttendanceForMngViewMain.PANEL_LAYOUT_ID);
		SessionBean sess = SessionUtil.getSessionBean();
		// ҳ��״̬
		String appStatus = String.valueOf(sess.getExtendAttributeValue(ShopAttForMngPageModel.APP_STATUS));
		// ����ǰ����ڲ鿴���򲻴���
		if (ShopAttForMngPageModel.STATUS_BYDATE_BROWSE.equals(appStatus)) {
			panelLayout.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0002313")/*
																												 * @
																												 * res
																												 * "����"
																												 */);
			return;
		}
		panelLayout.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0000129")/*
																											 * @
																											 * res
																											 * "��Ա"
																											 */);
		LfwView viewMain = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		GridComp gridMachine = (GridComp) viewMain.getViewComponents().getComponent(
				ShopAttForEmpPageModel.COMP_GRID_MACHINE_DATA);
		GridComp gridManual = (GridComp) viewMain.getViewComponents().getComponent(
				ShopAttForEmpPageModel.COMP_GRID_MANUAL_DATA);
		((GridColumn) gridMachine.getColumnById("pk_psnjob_clerkcode")).setVisible(false);
		((GridColumn) gridMachine.getColumnById("pk_psnjob_pk_psndoc_code")).setVisible(false);
		((GridColumn) gridMachine.getColumnById("pk_psndoc_name")).setVisible(false);
//		((GridColumn) gridMachine.getColumnById("pk_psnjob_pk_dept_name")).setVisible(false);
		((GridColumn) gridManual.getColumnById("pk_psnjob_clerkcode")).setVisible(false);
		((GridColumn) gridManual.getColumnById("pk_psnjob_pk_psndoc_code")).setVisible(false);
		((GridColumn) gridManual.getColumnById("pk_psndoc_name")).setVisible(false);
//		((GridColumn) gridManual.getColumnById("pk_psnjob_pk_dept_name")).setVisible(false);
	}

	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}
}