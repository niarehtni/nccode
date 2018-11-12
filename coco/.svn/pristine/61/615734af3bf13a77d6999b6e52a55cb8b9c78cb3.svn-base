package nc.bs.hrsms.ta.sss.away.lsnr;

import java.util.Calendar;
import java.util.TimeZone;

import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrsms.ta.sss.away.ShopAwayApplyConsts;
import nc.itf.hrss.pub.cmd.prcss.ILineDelProcessor;
import nc.itf.ta.IAwayAppInfoDisplayer;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.away.AggAwayVO;
import nc.vo.ta.away.AwaybVO;
import nc.vo.ta.away.AwayhVO;

public class ShopAwayApplyLineDelProcessor implements ILineDelProcessor {

	/**
	 * 行删除后操作
	 */
	@Override
	public void onAfterRowDel() {
		calculate();
	}

	/**
	 * 调用后台计算出差时长
	 * 
	 * @param ds
	 * @param dsDetail
	 * @param selRow
	 */
	private void calculate() {

		LfwView view = getLifeCycleContext().getViewContext().getView();
		Dataset dsMaster = view.getViewModels().getDataset(ShopAwayApplyConsts.DS_MAIN_NAME);
		Row selRow = dsMaster.getSelectedRow();
		if (selRow == null) {
			return;
		}
		Dataset dsDetail = view.getViewModels().getDataset(ShopAwayApplyConsts.DS_SUB_NAME);
		Row[] rows = dsDetail.getCurrentRowData().getRows();
		if (rows == null || rows.length == 0) {
			// 合计时长
			selRow.setValue(dsMaster.nameToIndex(AwayhVO.SUMHOUR), UFDouble.ZERO_DBL);
			// 预支费用合计
			selRow.setValue(dsMaster.nameToIndex(AwayhVO.SUMAHEADFEE), UFDouble.ZERO_DBL);
			// 实际支出合计
			selRow.setValue(dsMaster.nameToIndex(AwayhVO.SUMFACTFEE), UFDouble.ZERO_DBL);

		} else {
			Datasets2AggVOSerializer serializer = new Datasets2AggVOSerializer();
			AggAwayVO aggVO = (AggAwayVO) serializer.serialize(dsMaster, new Dataset[] { dsDetail }, ShopAwayApplyConsts.CLASS_NAME_AGGVO.getName());
			// 计算前的准备
			prepareBeforeCal(aggVO);
			try {

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
			selRow.setValue(dsMaster.nameToIndex(AwayhVO.SUMHOUR), headVO.getLength());
			// 预支费用合计
			selRow.setValue(dsMaster.nameToIndex(AwayhVO.SUMAHEADFEE), headVO.getSumaheadfee());
			// 实际支出合计
			selRow.setValue(dsMaster.nameToIndex(AwayhVO.SUMFACTFEE), headVO.getSumfactfee());

			/** 重新设置子表数据 */
			AwaybVO[] vos = aggVO.getAwaybVOs();
			new SuperVO2DatasetSerializer().update(vos, dsDetail);
		}

	}
	
	/**
	 *  计算前的准备
	 * @param aggVO
	 */
	private void prepareBeforeCal(AggAwayVO aggVO) {
		AwayhVO headVO =aggVO.getAwayhVO();
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
	 * AppLifeCycleContext
	 * 
	 * @return
	 */
	private AppLifeCycleContext getLifeCycleContext() {
		return AppLifeCycleContext.current();
	}

}
