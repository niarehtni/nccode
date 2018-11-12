package nc.bs.hrsms.ta.sss.ShopAttendance.ctrl;

import nc.bs.hrsms.ta.sss.ShopAttendance.pagemodel.ShopAttForEmpPageModel;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.hr.utils.ResHelper;
import nc.itf.ta.ICheckTimeQueryService;
import nc.itf.ta.algorithm.ICheckTime;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

/**
 * 考勤详情的ViewController
 * 
 * @author mayif
 */
public class ViewShopAttDetailViewMain implements IController {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	public static final String SESSION_PK_ORG = "sess_timedetail_pk_org";
	public static final String SESSION_PK_PSNDOC = "sess_timedetail_pk_psndoc";
	public static final String SESSION_SELECTED_DATE = "sess_timedetail_selected_date";

	/**
	 * 查询刷卡详细记录
	 * 
	 * @param ctx
	 * @author haoy 2011-8-22
	 */
	public void showDetail(ScriptEvent scriptEvent) {
		// 主键
		String dsId = getLifeCycleContext().getParameter("dsMain_id");
		String rowId = getLifeCycleContext().getParameter("dsMain_rowId");
		if (StringUtils.isEmpty(dsId)) {
			return;
		}
		LfwView view = getLifeCycleContext().getViewContext().getView();
		Dataset ds = view.getViewModels().getDataset(dsId);
		if (ds == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0166"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0120")/*
																												 * @
																												 * res
																												 * "请先选择一条考勤数据！"
																												 */);
		}
		Row row = ds.getRowById(rowId);
		if (row == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res","0c_pub-res0166"),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0120")/*
																												 * @
																												 * res
																												 * "请先选择一条考勤数据！"
																												 */);
		}
		String pk_org = row.getString(ds.nameToIndex(ShopAttForEmpPageModel.FIELD_MACHINE_PKORG));
		String pk_psndoc = row.getString(ds.nameToIndex(ShopAttForEmpPageModel.FIELD_MACHINE_PERSON_PK));
		UFLiteralDate calendar = (UFLiteralDate) row.getValue(ds.nameToIndex(ShopAttForEmpPageModel.FIELD_MACHINE_CALENDAR));

		ApplicationContext appCtx = getLifeCycleContext().getApplicationContext();
		appCtx.addAppAttribute(ViewShopAttDetailViewMain.SESSION_PK_ORG, pk_org);
		appCtx.addAppAttribute(ViewShopAttDetailViewMain.SESSION_PK_PSNDOC, pk_psndoc);
		appCtx.addAppAttribute(ViewShopAttDetailViewMain.SESSION_SELECTED_DATE, calendar);
		// 显示签/刷卡详细对话框
		CommonUtil.showWindowDialog("ViewShopAttDetail", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0119")/*
																																		 * @
																																		 * res
																																		 * "签/刷卡详细"
																																		 */, "962", "400", null, ApplicationContext.TYPE_DIALOG);

	}

	/**
	 * 加载签卡数据
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_dsImportData(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		DatasetUtil.clearData(ds);

		ApplicationContext appCtx = getLifeCycleContext().getApplicationContext();
		// 人员组织
		String pk_org = (String) appCtx.getAppAttribute(SESSION_PK_ORG);
		// 人员主键
		String pk_psndoc = (String) appCtx.getAppAttribute(SESSION_PK_PSNDOC);
		// 日期
		UFLiteralDate date = (UFLiteralDate) appCtx.getAppAttribute(SESSION_SELECTED_DATE);
		// 查询某个人员某天的刷/签卡记录
		ICheckTime[] vos = getTimeDetailVOs(pk_org, pk_psndoc, date);
		if (null == vos || vos.length == 0) {
			return;
		}
		for (ICheckTime vo : vos) {
			Row row = ds.getEmptyRow();
			row.setString(ds.nameToIndex("timecardid"), vo.getTimecardid());
			row.setValue(ds.nameToIndex("datetime"), vo.getDatetime());
			row.setInt(ds.nameToIndex("timeflag"), vo.getTimeflag());
			row.setInt(ds.nameToIndex("checkflag"), vo.getCheckflag());
			row.setString(ds.nameToIndex("pk_machine"), vo.getPk_machine());
			row.setString(ds.nameToIndex("pk_place"), vo.getPk_place());
			row.setValue(ds.nameToIndex("placeabnormal"), vo.getPlaceabnormal());
			row.setString(ds.nameToIndex("signreason"), vo.getSignreason());
			row.setString(ds.nameToIndex("creator"), vo.getCreator());
			row.setValue(ds.nameToIndex("creationtime"), vo.getCreationtime());
			ds.addRow(row);
		}
	}

	/**
	 * 查询某个人员某天的刷、签卡记录，按时间先后顺序排列. <br/>
	 * 过滤规则是从当天的刷卡开始时间到当天的刷卡结束时间<br/>
	 * 
	 * @param pk_psndoc
	 * @param date
	 * @return
	 */
	private ICheckTime[] getTimeDetailVOs(String pk_hr_org, String pk_psndoc, UFLiteralDate date) {
		// 查询刷卡数据
		ICheckTime[] vos = null;
		try {
			ICheckTimeQueryService ctq = ServiceLocator.lookup(ICheckTimeQueryService.class);
			vos = ctq.queryCheckTimesByPsnAndDate(pk_hr_org, pk_psndoc, date);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return vos;
	}

	protected AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}
}