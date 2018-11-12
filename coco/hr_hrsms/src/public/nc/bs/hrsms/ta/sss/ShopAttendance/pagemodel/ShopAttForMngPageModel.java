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

	// 数据集id：查询条件
	public static final String DATASET_CONDITION = "dsCondition";

	public static final String APP_STATUS = "appstatus";
	// 状态：机器考勤浏览
	public static final int STATUS_MACHINE_BROWSE = 1;
	// 状态：手工考勤浏览
	public static final int STATUS_MANUAL_BROWSE = 2;
	// 状态：手工考勤编辑
	public static final int STATUS_MANUAL_EDIT = 3;

	// 数据集id：手工考勤数据
	public static final String DATASET_NAVI = "dsNavi";

	// 控件：导航表格
	public static final String COMP_TABLE_NAVI = "tblNavi";
	// 控件：机器考勤表格
	public static final String COMP_TABLE_MACHINE = "tblMachineData";
	// 控件：手工考勤表格
	public static final String COMP_TABLE_MANUAL = "tblManualData";
	// 手工考勤页签
	public static final String TAB_MANUAL = "tabManual";
	// 数据集字段：查询条件.起始日期
	public static final String FIELD_BEGIN_DATE = "begindate";
	// 数据集字段：查询条件.终止日期
	public static final String FIELD_END_DATE = "enddate";
	// 数据集字段：查询条件.是否日期查看模式
	public static final String FIELD_DATE_MODE = "date_model";
	// 数据集字段：查询条件.部门
	public static final String FIELD_DEPT = "pk_dept";
	// 数据集字段：查询条件.是否包含下级部门
	public static final String FIELD_DEPT_SUB = "include_sub";
	// 数据集字段：查询条件.人员
	public static final String FIELD_TIME_DATA_PSNDOC = "pk_psndoc";
	// 数据集字段：查询条件.是否只显示异常
	public static final String FIELD_ONLY_SHOW_EXCEPTION = "onlyshowexception";
	// 数据集字段：导航.主键
	public static final String FIELD_NAVI_PK = "pk";
	// 数据集字段：导航.名称
	public static final String FIELD_NAVI_NAME = "name";
	// 数据集字段：考勤数据.姓名
	public static final String FIELD_TIME_DATA_PSNNAME = "psnname";
	// 查询条件——姓名
	public static final String FIELD_PK_PSNDOC_NAME = "pk_psndoc_name";
	// 控件：导航表格：列：主键
	public static final String COMP_TABLE_NAVI_COL_PK = "pk";
	// 控件：导航表格：列：主键
	public static final String COMP_TABLE_NAVI_COL_NAME = "name";

	// 页面标识 - 按人员查看
	public static final String PAGE_ID_TIMEDATAPSN = "timedatapsn";
	public static final String STATUS_BYNAME_BROWSE = "0";
	// 页面标识 - 按日期查看
	public static final String PAGE_ID_TIMEDATADATE = "timedatadate";
	public static final String STATUS_BYDATE_BROWSE = "1";
	
	// 日期显示
	public static final String CONST_DAY_DISP = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res",
			"0c_ta-res0129")/* @res "【周日】~【周一】~【周二】~【周三】~【周四】~【周五】~【周六】" */;

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
		// 在applicationContext中添加属性考勤档案和考勤规则
		TaAppContextUtil.addTaAppContext();
		Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(TaAppContextUtil.getHROrg());
		TaAppContextUtil.setTBMPeriodVOMap(periodMap);
		// 根据页面是按人员还是日期查看设置显示列
		setPsninfoVisible();
		// 隐藏查询条件————姓名
		// setQueryNameVisible();
		LfwView widget = getPageMeta().getView(HrssConsts.PAGE_MAIN_WIDGET);
		GridComp grid = (GridComp) widget.getViewComponents().getComponent(
				ShopAttForEmpPageModel.COMP_GRID_MANUAL_DATA);
		addGridCellEvent(widget, grid);
		// 设置小数位数
		setPrecision();
	}
	
	/**
	 * 根据考勤规则设置字段保留小数位数
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
	 * 设置单元格是否可编辑事件
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
		// 添加JS
		conf.setScript("return beforEditClass(cellEvent)");
		grid.addEventConf(conf);
	}

	/**
	 * 按人员查看时隐藏人员信息相关的列
	 * 
	 */
	public void setPsninfoVisible() {

		UIMeta um = (UIMeta) LfwRuntimeEnvironment.getWebContext().getUIMeta();
		UIPanel panelLayout = (UIPanel) um.findChildById(ShopAttendanceForMngViewMain.PANEL_LAYOUT_ID);
		SessionBean sess = SessionUtil.getSessionBean();
		// 页面状态
		String appStatus = String.valueOf(sess.getExtendAttributeValue(ShopAttForMngPageModel.APP_STATUS));
		// 如果是按日期查看，则不处理
		if (ShopAttForMngPageModel.STATUS_BYDATE_BROWSE.equals(appStatus)) {
			panelLayout.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0002313")/*
																												 * @
																												 * res
																												 * "日期"
																												 */);
			return;
		}
		panelLayout.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0000129")/*
																											 * @
																											 * res
																											 * "人员"
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