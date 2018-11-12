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
	 * ���ú�̨�������ʱ��
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
			// �ϼ�ʱ��
			selRow.setValue(dsMaster.nameToIndex(OvertimehVO.SUMHOUR), UFDouble.ZERO_DBL);

		} else {

			Datasets2AggVOSerializer serializer = new Datasets2AggVOSerializer();
			AggOvertimeVO aggVO = (AggOvertimeVO) serializer.serialize(dsMaster, new Dataset[] { dsDetail }, ShopOverTimeConsts.CLASS_NAME_AGGVO.getName());
			// ����ǰ��׼��
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
			/** ���������������� */
			OvertimehVO headVO = (OvertimehVO) aggVO.getParentVO();
			// �ϼ�ʱ��
			selRow.setValue(dsMaster.nameToIndex(OvertimehVO.SUMHOUR), headVO.getLength());

			/** ���������ӱ����� */
			OvertimebVO[] vos = aggVO.getOvertimebVOs();
			new SuperVO2DatasetSerializer().update(vos, dsDetail);
		}
	}

	/**
	 * ����ǰ��׼��
	 * 
	 * @param aggVO
	 */
	private void prepareBeforeCal(AggOvertimeVO aggVO) {
		OvertimehVO headVO = aggVO.getOvertimehVO();
		OvertimebVO[] vos = aggVO.getOvertimebVOs();
		for (OvertimebVO subVO : vos) {
			// ���õ���������֯����
			subVO.setPk_group(headVO.getPk_group());
			// ���õ���������֯����
			subVO.setPk_org(headVO.getPk_org());
			// ������Ա����
			subVO.setPk_psndoc(headVO.getPk_psndoc());
			// ��Ա��ְ����
			subVO.setPk_psnjob(headVO.getPk_psnjob());
			// ��֯��ϵ����
			subVO.setPk_psnorg(headVO.getPk_psnorg());
			// �Ӱ����
			subVO.setPk_overtimetype(headVO.getPk_overtimetype());
			// �Ӱ����Copy
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
