package nc.bs.hrsms.ta.sss.shopleave.prcss;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.ta.leave.LeaveConsts;
import nc.bs.hrsms.ta.sss.shopleave.ShopLeaveApplyConsts;
import nc.itf.hrss.pub.cmd.prcss.ILineDelProcessor;
import nc.itf.ta.ILeaveAppInfoDisplayer;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;

import org.springframework.beans.BeanUtils;

public class ShopLeaveLineDelProcessor implements ILineDelProcessor {

	@Override
	public void onAfterRowDel() {
		calculate();
	}

	/**
	 * 调用后台计算休假合计时长
	 * 
	 * @param ds
	 * @param dsDetail
	 * @param selRow
	 */
	private void calculate() {
		LfwView view = getLifeCycleContext().getViewContext().getView();
		Dataset dsMaster = view.getViewModels().getDataset(ShopLeaveApplyConsts.DS_MAIN_NAME);
		Row selRow = dsMaster.getSelectedRow();
		if (selRow == null) {
			return;
		}
		Dataset dsDetail = view.getViewModels().getDataset(ShopLeaveApplyConsts.DS_SUB_NAME);
		Row[] rows = dsDetail.getCurrentRowData().getRows();
		if (rows == null || rows.length == 0) {
			// 休假总时长
			selRow.setValue(dsMaster.nameToIndex(LeavehVO.SUMHOUR), UFDouble.ZERO_DBL);
		} else {
			Datasets2AggVOSerializer serializer = new Datasets2AggVOSerializer();
			AggLeaveVO aggVO = (AggLeaveVO) serializer.serialize(dsMaster, new Dataset[] { dsDetail }, LeaveConsts.CLASS_NAME_AGGVO.getName());
			// 计算前的准备
			prepareBeforeCal(aggVO);

			LeavehVO newParentVO = new LeavehVO();
			BeanUtils.copyProperties(aggVO.getParentVO(), newParentVO);

			LeavebVO[] oldSubVOs = aggVO.getLeavebVOs();
			List<LeavebVO> newSubVOList = new ArrayList<LeavebVO>();
			LeavebVO temp = null;
			for (LeavebVO oldSubVO : oldSubVOs) {
				temp = new LeavebVO();
				BeanUtils.copyProperties(oldSubVO, temp);
				newSubVOList.add(temp);
			}
			AggLeaveVO newAggVO = new AggLeaveVO();
			newAggVO.setParentVO(newParentVO);
			newAggVO.setChildrenVO(newSubVOList.toArray(new LeavebVO[0]));

			try {
				TimeZone clientTimeZone = Calendar.getInstance().getTimeZone();
				ILeaveAppInfoDisplayer service = ServiceLocator.lookup(ILeaveAppInfoDisplayer.class);
				newAggVO = service.calculate(aggVO,clientTimeZone);
			} catch (HrssException e) {
				new HrssException(e).alert();
			} catch (BusinessException e) {
				new HrssException(e).deal();
			}
			/** 重新设置主表数据 */
			LeavehVO headVO = (LeavehVO) newAggVO.getParentVO();
			// 合计时长
			selRow.setValue(dsMaster.nameToIndex(LeavehVO.SUMHOUR), headVO.getLength());

			/** 重新设置子表数据 */
			LeavebVO[] newSubVOs = newAggVO.getLeavebVOs();
			for (int i = 0; i < newSubVOs.length; i++) {
				BeanUtils.copyProperties(newSubVOs[i], oldSubVOs[i]);
			}

			new SuperVO2DatasetSerializer().update(oldSubVOs, dsDetail);
		}
	}

	/**
	 * 计算前的准备
	 * 
	 * @param aggVO
	 */
	private void prepareBeforeCal(AggLeaveVO aggVO) {
		LeavehVO headVO = aggVO.getLeavehVO();
		LeavebVO[] vos = aggVO.getLeavebVOs();
		for (LeavebVO subVO : vos) {
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
			// 休假类别
			subVO.setPk_leavetype(headVO.getPk_leavetype());
			// 休假类别Copy
			subVO.setPk_leavetypecopy(headVO.getPk_leavetypecopy());
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
