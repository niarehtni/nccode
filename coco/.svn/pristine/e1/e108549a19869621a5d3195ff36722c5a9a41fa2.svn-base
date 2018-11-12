package nc.bs.hrsms.ta.sss.away.ctrl;

import java.util.Calendar;
import java.util.TimeZone;

import nc.bs.hrsms.ta.sss.away.ShopAwayApplyConsts;
import nc.bs.hrsms.ta.sss.away.lsnr.ShopAwayApplyAddProcessor;
import nc.bs.hrsms.ta.sss.away.lsnr.ShopAwayApplyEditProcessor;
import nc.bs.hrsms.ta.sss.away.lsnr.ShopAwayApplyLineAddProcessor;
import nc.bs.hrsms.ta.sss.away.lsnr.ShopAwayApplyLineDelProcessor;
import nc.bs.hrsms.ta.sss.away.lsnr.ShopAwayApplySaveProcessor;
import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyBaseView;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.IEditProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineDelProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.ta.IAwayAppInfoDisplayer;
import nc.itf.ta.ITimeItemQueryService;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.RowData;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.away.AggAwayVO;
import nc.vo.ta.away.AwaybVO;
import nc.vo.ta.away.AwayhVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;

public class ShopAwayApplyCardView extends ShopTaApplyBaseView implements IController{

	/**
	 * 单据类型
	 *
	 * @return
	 */
	@Override
	protected String getBillType() {
		return ShopAwayApplyConsts.BILL_TYPE_CODE;
	}

	/**
	 * 数据集ID
	 *
	 * @return
	 */
	@Override
	protected String getDatasetId() {
		return ShopAwayApplyConsts.DS_MAIN_NAME;
	}

	/**
	 * 单据聚合类型VO
	 *
	 * @return
	 */
	@Override
	protected Class<? extends AggregatedValueObject> getAggVoClazz() {
		return ShopAwayApplyConsts.CLASS_NAME_AGGVO;
	}

	/**
	 * 保存的PROCESSOR
	 *
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSavePrcss() {
		return ShopAwayApplySaveProcessor.class;
	}

	/**
	 * 保存并新增的PROCESSOR
	 *
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSaveAddPrcss() {
		return null;
	}

	/**
	 * 行新增的PROCESSOR
	 */
	@Override
	protected Class<? extends ILineInsertProcessor> getLineAddPrcss() {
		return ShopAwayApplyLineAddProcessor.class;
	}

	/**
	 * 新增的PROCESSOR
	 *
	 * @return
	 */
	@Override
	protected Class<? extends IAddProcessor> getAddPrcss() {
		return ShopAwayApplyAddProcessor.class;
	}

	/**
	 * 修改的PROCESSOR
	 *
	 * @return
	 */
	@Override
	protected Class<? extends IEditProcessor> getEditPrcss() {
		return ShopAwayApplyEditProcessor.class;
	}

	

	/**
	 * 行删除的PROCESSOR
	 *
	 * @return
	 */
	@Override
	protected Class<? extends ILineDelProcessor> getLineDelPrcss() {
		return ShopAwayApplyLineDelProcessor.class;
	}

	/**
	 * 数据加载事件
	 *
	 * @param dataLoadEvent
	 */
	public void onDataLoad_awayh(DataLoadEvent dataLoadEvent) {
		super.onDataLoad(dataLoadEvent);
		
	}

	/**
	 * 主数据值改变事件
	 *
	 * @param datasetCellEvent
	 */
	public void onAfterDataChange_awayh(DatasetCellEvent datasetCellEvent) {

		LfwView view = getLifeCycleContext().getViewContext().getView();
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		int colIndex = datasetCellEvent.getColIndex();
		if (colIndex != ds.nameToIndex(AwayhVO.PK_AWAYTYPE) && colIndex != ds.nameToIndex("pk_psnjob")) {
			return;
		}
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			return;
		}
		if(colIndex == ds.nameToIndex("pk_psnjob")){
			String pk_psndoc =  (String) selRow.getValue(ds.nameToIndex("pk_psndoc"));
			// 在applicationContext中添加属性考勤档案和考勤规则
			ShopTaAppContextUtil.addTaAppForTransferContext(pk_psndoc);
			ShopAwayAfterDataChange.onAfterDataChange(ds, selRow);
		}
		// 设置考勤单位的显示
		setTimeUnitText(ds, selRow);
		Dataset dsDetail = view.getViewModels().getDataset(ShopAwayApplyConsts.DS_SUB_NAME);
		RowData rowData = dsDetail.getCurrentRowData();
		if (rowData == null) {
			return;
		}
		Row[] rows = rowData.getRows();
		if (rows == null || rows.length == 0) {
			selRow.setValue(ds.nameToIndex(AwayhVO.SUMHOUR), UFDouble.ZERO_DBL);
			selRow.setValue(ds.nameToIndex(AwayhVO.SUMAHEADFEE), UFDouble.ZERO_DBL);
			selRow.setValue(ds.nameToIndex(AwayhVO.SUMFACTFEE), UFDouble.ZERO_DBL);
			return;
		}
		// 调用后台计算出差时长
		calculate(ds, dsDetail, selRow);
	}

	/**
	 * 设置考勤单据相关字段的显示
	 *
	 * @param view
	 * @param ds
	 * @param selRow
	 */
	@SuppressWarnings("deprecation")
	public static void setTimeUnitText(Dataset ds, Row masterRow) {

		// 申请单据所属组织
		String pk_org = masterRow.getString(ds.nameToIndex(AwayhVO.PK_ORG));

		// 获得出差类别PK
		String pk_awaytype = masterRow.getString(ds.nameToIndex(AwayhVO.PK_AWAYTYPE));
		// 出差信息
		LfwView view = AppLifeCycleContext.current().getViewContext().getView();
		FormComp form = (FormComp) view.getViewComponents().getComponent("headTab_card_awayinf_form");
		// 合计时长
		FormElement elem = form.getElementById(AwayhVO.SUMHOUR);
		TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(pk_org, pk_awaytype);
		String text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0000")/*@res "合计时长"*/;
		if (timeItemCopyVO != null) {
			// 设置出差类别copy的PK
			masterRow.setValue(ds.nameToIndex(AwayhVO.PK_AWAYTYPECOPY), timeItemCopyVO.getPk_timeitemcopy());
			Integer timeitemunit = timeItemCopyVO.getTimeitemunit();			
			if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_DAY == timeitemunit) {// 天
				elem.setLabel(text + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0001")/*@res "(天)"*/);
			} else if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_HOUR == timeitemunit) {// 小时
				elem.setLabel(text + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0002")/*@res "(小时)"*/);
			} else {
				elem.setLabel(text);
			}
		} else {
			elem.setLabel(text);
			// 设置出差类别copy的PK
			masterRow.setValue(ds.nameToIndex(AwayhVO.PK_AWAYTYPECOPY), null);
		}
	}

	/**
	 * 子数据集值改变事件
	 *
	 * @param dataLoadEvent
	 */
	public void onAfterDataChange_awayb(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		// 字段顺序
		int colIndex = datasetCellEvent.getColIndex();
		if (colIndex != ds.nameToIndex(AwaybVO.AWAYBEGINTIME) && colIndex != ds.nameToIndex(AwaybVO.AWAYENDTIME) && colIndex != ds.nameToIndex(AwaybVO.AHEADFEE)
				&& colIndex != ds.nameToIndex(AwaybVO.FACTFEE)) {
			return;
		}
		Row selRow = ds.getSelectedRow();
		UFDateTime beiginTime = (UFDateTime) selRow.getValue(ds.nameToIndex(AwaybVO.AWAYBEGINTIME));
		UFDateTime endTime = (UFDateTime) selRow.getValue(ds.nameToIndex(AwaybVO.AWAYENDTIME));
		if (colIndex == ds.nameToIndex(AwaybVO.AWAYBEGINTIME)) {
			if (beiginTime != null) {
				// 设置了开始日期
				selRow.setValue(ds.nameToIndex(AwaybVO.AWAYBEGINDATE), new UFLiteralDate(beiginTime.getDate().toString()));
			} else {
				// 设置了开始日期
				selRow.setValue(ds.nameToIndex(AwaybVO.AWAYBEGINDATE), null);
			}
		}
		if (colIndex == ds.nameToIndex(AwaybVO.AWAYENDTIME)) {
			if (endTime != null) {
				// 设置了结束日期
				selRow.setValue(ds.nameToIndex(AwaybVO.AWAYENDDATE), new UFLiteralDate(endTime.getDate().toString()));
			} else {
				selRow.setValue(ds.nameToIndex(AwaybVO.AWAYENDDATE), null);
			}
		}
		LfwView view = getLifeCycleContext().getViewContext().getView();
		Dataset dsMaster = view.getViewModels().getDataset(ShopAwayApplyConsts.DS_MAIN_NAME);
		Row rowMaster = dsMaster.getSelectedRow();
		// 调用后台计算时长
		calculate(dsMaster, ds, rowMaster);
	}

	/**
	 * 调用后台计算出差时长
	 *
	 * @param ds
	 * @param dsDetail
	 * @param selRow
	 */
	private void calculate(Dataset ds, Dataset dsDetail, Row selRow) {
		Datasets2AggVOSerializer serializer = new Datasets2AggVOSerializer();
		AggAwayVO aggVO = (AggAwayVO) serializer.serialize(ds, new Dataset[] { dsDetail }, ShopAwayApplyConsts.CLASS_NAME_AGGVO.getName());
		// 计算前的准备
		prepareBeforeCal(aggVO);
		try {
			//QXP 客户端时区，目前没有获得的方法，放置的是服务器端的时区
			TimeZone clientTimeZone = Calendar.getInstance().getTimeZone();
			IAwayAppInfoDisplayer service = ServiceLocator.lookup(IAwayAppInfoDisplayer.class);
			aggVO = service.calculate(aggVO, clientTimeZone);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		/** 重新设置主表数据 */
		AwayhVO headVO = (AwayhVO) aggVO.getParentVO();
		// 合计时长
		selRow.setValue(ds.nameToIndex(AwayhVO.SUMHOUR), headVO.getLength());
		// 预支费用合计
		selRow.setValue(ds.nameToIndex(AwayhVO.SUMAHEADFEE), headVO.getSumaheadfee());
		// 实际支出合计
		selRow.setValue(ds.nameToIndex(AwayhVO.SUMFACTFEE), headVO.getSumfactfee());

		/** 重新设置子表数据 */
		AwaybVO[] vos = aggVO.getAwaybVOs();
		new SuperVO2DatasetSerializer().update(vos, dsDetail);
	}

	/**
	 * 计算前的准备
	 *
	 * @param aggVO
	 */
	private void prepareBeforeCal(AggAwayVO aggVO) {
		AwayhVO headVO = aggVO.getAwayhVO();
		AwaybVO[] vos = aggVO.getAwaybVOs();
		for (AwaybVO subVO : vos) {
			// 设置单据所属组织主键
			subVO.setPk_group(headVO.getPk_group());
			// 设置单据所属组织主键
			subVO.setPk_org(headVO.getPk_org());
			// 设置人员主键
			subVO.setPk_psndoc(headVO.getPk_psndoc());
			// 人员任职主键
			subVO.setPk_psnjob(headVO.getPk_psnjob());
			// 组织关系主键
			subVO.setPk_psnorg(headVO.getPk_psnorg());
			// 出差类别
			subVO.setPk_awaytype(headVO.getPk_awaytype());
			// 出差类别Copy
			subVO.setPk_awaytypecopy(headVO.getPk_awaytypecopy());
		}

	}

	/**
	 * 根据出差类别PK和组织PK, 获得出差类别copy的PK
	 *
	 * @param pk_org
	 * @param pk_awaytype
	 * @return
	 */
	public static TimeItemCopyVO getTimeItemCopyVO(String pk_org, String pk_awaytype) {
		TimeItemCopyVO timeItemCopyVO = null;
		// 查询出差类别copy的PK
		try {
			ITimeItemQueryService service = ServiceLocator.lookup(ITimeItemQueryService.class);
			timeItemCopyVO = service.queryCopyTypesByDefPK(pk_org, pk_awaytype, TimeItemVO.AWAY_TYPE);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return timeItemCopyVO;
	}

	@Override
	protected String getDetailDsId() {
		return ShopAwayApplyConsts.DS_SUB_NAME;
	}
}
