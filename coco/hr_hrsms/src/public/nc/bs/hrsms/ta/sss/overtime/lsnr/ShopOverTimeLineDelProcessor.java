package nc.bs.hrsms.ta.sss.overtime.lsnr;

import java.util.Calendar;
import java.util.TimeZone;

import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrsms.ta.sss.overtime.ShopOverTimeConsts;
import nc.itf.hrss.pub.cmd.prcss.ILineDelProcessor;
import nc.itf.ta.IOvertimeAppInfoDisplayer;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;

public class ShopOverTimeLineDelProcessor implements ILineDelProcessor{

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
		Dataset dsMaster = view.getViewModels().getDataset(ShopOverTimeConsts.DS_MAIN_NAME);
		Row selRow = dsMaster.getSelectedRow();
		if (selRow == null) {
			return;
		}
		Dataset dsDetail = view.getViewModels().getDataset(ShopOverTimeConsts.DS_SUB_NAME);
		Row[] rows = dsDetail.getCurrentRowData().getRows();
		if (rows == null || rows.length == 0) {
			// 合计时长
			selRow.setValue(dsMaster.nameToIndex(OvertimehVO.SUMHOUR), UFDouble.ZERO_DBL);

		} else {

			Datasets2AggVOSerializer serializer = new Datasets2AggVOSerializer();
			AggOvertimeVO aggVO = (AggOvertimeVO) serializer.serialize(dsMaster, new Dataset[] { dsDetail }, ShopOverTimeConsts.CLASS_NAME_AGGVO.getName());
			// 计算前的准备
			prepareBeforeCal(aggVO);
			try {
				TimeZone clientTimeZone = Calendar.getInstance().getTimeZone();
				IOvertimeAppInfoDisplayer service = ServiceLocator.lookup(IOvertimeAppInfoDisplayer.class);
				aggVO = service.calculate(aggVO,clientTimeZone);
			} catch (HrssException e) {
				new HrssException(e).alert();
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}
			/** 重新设置主表数据 */
			OvertimehVO headVO = (OvertimehVO) aggVO.getParentVO();
			// 合计时长
			selRow.setValue(dsMaster.nameToIndex(OvertimehVO.SUMHOUR), headVO.getLength());

			/** 重新设置子表数据 */
			OvertimebVO[] vos = aggVO.getOvertimebVOs();
			new SuperVO2DatasetSerializer().update(vos, dsDetail);
		}
	}

	/**
	 * 计算前的准备
	 * 
	 * @param aggVO
	 */
	private void prepareBeforeCal(AggOvertimeVO aggVO) {
		OvertimehVO headVO = aggVO.getOvertimehVO();
		OvertimebVO[] vos = aggVO.getOvertimebVOs();
		for (OvertimebVO subVO : vos) {
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
			// 加班类别
			subVO.setPk_overtimetype(headVO.getPk_overtimetype());
			// 加班类别Copy
			subVO.setPk_overtimetypecopy(headVO.getPk_overtimetypecopy());
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
