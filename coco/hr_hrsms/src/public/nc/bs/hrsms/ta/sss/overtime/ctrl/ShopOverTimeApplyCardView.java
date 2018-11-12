package nc.bs.hrsms.ta.sss.overtime.ctrl;

import java.util.Calendar;
import java.util.TimeZone;

import nc.bs.hrsms.ta.sss.common.ShopTaAppContextUtil;
import nc.bs.hrsms.ta.sss.common.ShopTaApplyBaseView;
import nc.bs.hrsms.ta.sss.overtime.ShopOverTimeConsts;
import nc.bs.hrsms.ta.sss.overtime.lsnr.ShopOverTimeAddProcessor;
import nc.bs.hrsms.ta.sss.overtime.lsnr.ShopOverTimeCommitProcessor;
import nc.bs.hrsms.ta.sss.overtime.lsnr.ShopOverTimeEditProcessor;
import nc.bs.hrsms.ta.sss.overtime.lsnr.ShopOverTimeLineAddProcessor;
import nc.bs.hrsms.ta.sss.overtime.lsnr.ShopOverTimeLineDelProcessor;
import nc.bs.hrsms.ta.sss.overtime.lsnr.ShopOverTimeSaveAddProcessor;
import nc.bs.hrsms.ta.sss.overtime.lsnr.ShopOverTimeSaveProcessor;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.ICommitProcessor;
import nc.itf.hrss.pub.cmd.prcss.IEditProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineDelProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.ta.IOvertimeAppInfoDisplayer;
import nc.itf.ta.IOvertimeApplyQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.FormElement;
import nc.uap.lfw.core.ctrl.IController;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.RowData;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.serializer.impl.Dataset2SuperVOSerializer;
import nc.uap.lfw.core.serializer.impl.Datasets2AggVOSerializer;
import nc.uap.lfw.core.serializer.impl.SuperVO2DatasetSerializer;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.AggOvertimeVO;
import nc.vo.ta.overtime.OvertimebVO;
import nc.vo.ta.overtime.OvertimehVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;

import org.apache.commons.lang.StringUtils;

public class ShopOverTimeApplyCardView extends ShopTaApplyBaseView implements IController{

	@Override
	protected String getBillType() {
		return ShopOverTimeConsts.BILL_TYPE_CODE;
	}

	@Override
	protected String getDatasetId() {
		return ShopOverTimeConsts.DS_MAIN_NAME;
	}

	@Override
	protected String getDetailDsId() {
		return ShopOverTimeConsts.DS_SUB_NAME;
	}

	@Override
	protected Class<? extends AggregatedValueObject> getAggVoClazz() {
		return ShopOverTimeConsts.CLASS_NAME_AGGVO;
	}

	/**
	 * ������PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends IAddProcessor> getAddPrcss() {
		return ShopOverTimeAddProcessor.class;
	}
	
	/**
	 * �޸ĵ�PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends IEditProcessor> getEditPrcss() {
		return ShopOverTimeEditProcessor.class;
	}

	
	@Override
	protected Class<? extends ISaveProcessor> getSavePrcss() {
		// TODO Auto-generated method stub
		return ShopOverTimeSaveProcessor.class;
	}

	@Override
	protected Class<? extends ISaveProcessor> getSaveAddPrcss() {
		// TODO Auto-generated method stub
		return ShopOverTimeSaveAddProcessor.class;
	}

	@Override
	protected Class<? extends ILineInsertProcessor> getLineAddPrcss() {
		// TODO Auto-generated method stub
		return ShopOverTimeLineAddProcessor.class;
	}
	/**
	 * ��ɾ����PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ILineDelProcessor> getLineDelPrcss() {
		return ShopOverTimeLineDelProcessor.class;
	}
	/**
	 * �ύ�Ĳ�����
	 */
	@Override
	protected Class<? extends ICommitProcessor> getCommitPrcss() {
		return ShopOverTimeCommitProcessor.class;
	}

	/**
	 * �����ݼ�����
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_hrtaovertimeh(DataLoadEvent dataLoadEvent) {
		super.onDataLoad(dataLoadEvent);
		
		LfwView view = ViewUtil.getCurrentView();
		Dataset ds = ViewUtil.getDataset(view, ShopOverTimeConsts.DS_MAIN_NAME);
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			return;
		}
		Dataset dsDetail = ViewUtil.getDataset(view, ShopOverTimeConsts.DS_SUB_NAME);
		RowData rowData = dsDetail.getCurrentRowData();
		if (rowData == null) {
			return;
		}
		Row[] rows = rowData.getRows();
		if (rows == null || rows.length == 0) {
			return;
		}
		/** �༭��ʱ����ӱ��pk_psndoc��pk_psnjob��ֵ,��ֹ"�Ƿ�У��"Checkbox���ܵ��ýӿ� */
		for (Row row : rows) {
			row.setValue(dsDetail.nameToIndex("isEditable"), dsDetail.isEnabled());
			row.setValue(dsDetail.nameToIndex("pk_psndoc"), selRow.getValue(ds.nameToIndex("pk_psndoc")));
			row.setValue(dsDetail.nameToIndex("pk_psnjob"), selRow.getValue(ds.nameToIndex("pk_psnjob")));
		}
	}

	/**
	 * ������ֵ�ı��¼�
	 * 
	 * @param datasetCellEvent
	 */
	public void onAfterDataChange_hrtaovertimeh(DatasetCellEvent datasetCellEvent) {
		Dataset ds = datasetCellEvent.getSource();
		// �ֶ�˳��
		int colIndex = datasetCellEvent.getColIndex();
		if (colIndex != ds.nameToIndex(OvertimehVO.PK_OVERTIMETYPE) && colIndex != ds.nameToIndex("pk_psnjob")) {
			return;
		}
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			return;
		}
		if(colIndex == ds.nameToIndex("pk_psnjob")){
			String pk_psndoc =  (String) selRow.getValue(ds.nameToIndex("pk_psndoc"));
			// ��applicationContext��������Կ��ڵ����Ϳ��ڹ���
			ShopTaAppContextUtil.addTaAppForTransferContext(pk_psndoc);
			ShopOverTimeAfterDataChange.onAfterDataChange(ds, selRow);
		}else if(colIndex == ds.nameToIndex(OvertimehVO.PK_OVERTIMETYPE)){
			// ���ÿ��ڵ�λ����ʾ
			setTimeUnitText(ds, selRow);
		}
		
		LfwView view = ViewUtil.getCurrentView();
		Dataset dsDetail = ViewUtil.getDataset(view, ShopOverTimeConsts.DS_SUB_NAME);
		RowData rowData = dsDetail.getCurrentRowData();
		if (rowData == null) {
			return;
		}
		Row[] rows = rowData.getRows();
		if (rows == null || rows.length == 0) {
			selRow.setValue(ds.nameToIndex(OvertimehVO.SUMHOUR), UFDouble.ZERO_DBL);
			return;
		}
		// ���ú�̨����ʱ��
		calculate(ds, dsDetail, selRow);
	}

	/**
	 * ���ÿ��ڵ�������ֶε���ʾ
	 * 
	 * @param ds
	 * @param selRow
	 */
	@SuppressWarnings("deprecation")
	public static void setTimeUnitText(Dataset ds, Row masterRow) {
		// ���뵥��������֯
		String pk_org = masterRow.getString(ds.nameToIndex(OvertimehVO.PK_ORG));

		// ��üӰ����PK
		String pk_overtimetype = masterRow.getString(ds.nameToIndex(OvertimehVO.PK_OVERTIMETYPE));
		// �Ӱ���Ϣ
		LfwView view = ViewUtil.getCurrentView();
		FormComp form = (FormComp) ViewUtil.getComponent(view, "headTab_card_overtimeinf_form");
		// �ϼ�ʱ��
		FormElement elem = form.getElementById(OvertimehVO.SUMHOUR);
		String text = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0098")/*
																										 * @
																										 * res
																										 * "�ϼƼӰ๤ʱ"
																										 */;
		TimeItemCopyVO timeItemCopyVO = getTimeItemCopyVO(pk_org, pk_overtimetype);
		if (timeItemCopyVO != null) {
			// ���üӰ����copy��PK
			masterRow.setValue(ds.nameToIndex(OvertimehVO.PK_OVERTIMETYPECOPY), timeItemCopyVO.getPk_timeitemcopy());
			Integer timeitemunit = timeItemCopyVO.getTimeitemunit();
			if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_DAY == timeitemunit) {// ��
				elem.setLabel(text + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0001")/*
																														 * @
																														 * res
																														 * "(��)"
																														 */);
			} else if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_HOUR == timeitemunit) {// Сʱ
				elem.setLabel(text + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0002")/*
																														 * @
																														 * res
																														 * "(Сʱ)"
																														 */);
			} else {
				elem.setLabel(text);
			}
		} else {
			elem.setLabel(text);
			// ���üӰ����copy��PK
			masterRow.setValue(ds.nameToIndex(OvertimehVO.PK_OVERTIMETYPECOPY), null);
		}
	}

	/**
	 * �����ݼ�ֵ�ı��¼�
	 * 
	 * @param dataLoadEvent
	 */
	public void onAfterDataChange_hrtaovertimeb(DatasetCellEvent datasetCellEvent) {
		Dataset dsDetail = datasetCellEvent.getSource();
		// �ֶ�˳��
		int colIndex = datasetCellEvent.getColIndex();
		if (colIndex != dsDetail.nameToIndex(OvertimebVO.OVERTIMEBEGINTIME) && colIndex != dsDetail.nameToIndex(OvertimebVO.OVERTIMEENDTIME) && colIndex != dsDetail.nameToIndex(OvertimebVO.DEDUCT)) {
			return;
		}
		LfwView view = ViewUtil.getCurrentView();
		Dataset dsMaster = ViewUtil.getDataset(view, ShopOverTimeConsts.DS_MAIN_NAME);
		Row rowMaster = dsMaster.getSelectedRow();
		Row rowDetail = dsDetail.getSelectedRow();

		// ����ʱ��
		dateChangeEvent(dsDetail, rowDetail, dsMaster, rowMaster, colIndex);

		/**
		 * "�Ƿ���ҪУ��"�Ƿ�ɱ༭�Ĵ�����.<br>
		 * 1.�Ӱ����δѡ��,���ɱ༭.<br>
		 * 2.�Ӱ࿪ʼʱ���Ӱ����ʱ��Ϊ��,���ɱ༭.<br>
		 * 3.���ú�̨�����ж��Ƿ�ɱ༭.<br>
		 * 
		 * @param gridCellEvent
		 */
		// ��üӰ����PK
		String pk_overtimetype = rowMaster.getString(dsMaster.nameToIndex(OvertimehVO.PK_OVERTIMETYPE));
		if (StringUtils.isEmpty(pk_overtimetype)) {
			return;
		}
		// �Ӱ࿪ʼʱ��
		UFDateTime beiginTime = (UFDateTime) rowDetail.getValue(dsDetail.nameToIndex(OvertimebVO.OVERTIMEBEGINTIME));
		// �Ӱ����ʱ��
		UFDateTime endTime = (UFDateTime) rowDetail.getValue(dsDetail.nameToIndex(OvertimebVO.OVERTIMEENDTIME));
		if (beiginTime == null || endTime == null) {
			return;
		}
		SuperVO[] subVOs = new Dataset2SuperVOSerializer<SuperVO>().serialize(dsDetail, rowDetail);
		if (subVOs == null || subVOs.length == 0) {
			return;
		}
		boolean isCanCheck = false;
		try {
			IOvertimeApplyQueryMaintain service = ServiceLocator.lookup(IOvertimeApplyQueryMaintain.class);
			isCanCheck = service.isCanCheck((OvertimebVO) subVOs[0]);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		if (!isCanCheck) {
			((OvertimebVO) subVOs[0]).setIsneedcheck(UFBoolean.FALSE);
			new SuperVO2DatasetSerializer().update(subVOs, dsDetail);

		}
	}

	private void dateChangeEvent(Dataset ds, Row selRow, Dataset dsMaster, Row rowMaster, int colIndex) {
		UFDateTime beiginTime = (UFDateTime) selRow.getValue(ds.nameToIndex(OvertimebVO.OVERTIMEBEGINTIME));
		UFDateTime endTime = (UFDateTime) selRow.getValue(ds.nameToIndex(OvertimebVO.OVERTIMEENDTIME));
		// �Ӱ࿪ʼʱ��
		if (colIndex == ds.nameToIndex(OvertimebVO.OVERTIMEBEGINTIME)) {
			if (beiginTime != null) {
				// �����˿�ʼ����
				selRow.setValue(ds.nameToIndex(OvertimebVO.OVERTIMEBEGINDATE), new UFLiteralDate(beiginTime.getDate().toString()));
			} else {
				selRow.setValue(ds.nameToIndex(OvertimebVO.OVERTIMEBEGINDATE), null);
			}
		}
		if (colIndex == ds.nameToIndex(OvertimebVO.OVERTIMEENDTIME)) {
			if (endTime != null) {
				// �����˽�������
				selRow.setValue(ds.nameToIndex(OvertimebVO.OVERTIMEENDDATE), new UFLiteralDate(endTime.getDate().toString()));
			} else {
				selRow.setValue(ds.nameToIndex(OvertimebVO.OVERTIMEENDDATE), null);
			}
		}
		if (beiginTime != null && endTime != null) {
			// ���ú�̨����ʱ��
			calculate(dsMaster, ds, rowMaster);
		}
	}

	/**
	 * ���ú�̨����Ӱ�ʱ��
	 * 
	 * @param ds
	 * @param dsDetail
	 * @param selRow
	 */
	private void calculate(Dataset ds, Dataset dsDetail, Row selRow) {
		Datasets2AggVOSerializer serializer = new Datasets2AggVOSerializer();
		AggOvertimeVO aggVO = (AggOvertimeVO) serializer.serialize(ds, new Dataset[] { dsDetail }, ShopOverTimeConsts.CLASS_NAME_AGGVO.getName());
		// ����ǰ��׼��
		prepareBeforeCal(aggVO);
		try {
			TimeZone clientTimeZone = Calendar.getInstance().getTimeZone();
			IOvertimeAppInfoDisplayer service = ServiceLocator.lookup(IOvertimeAppInfoDisplayer.class);
			aggVO = service.calculate(aggVO, clientTimeZone);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		/** ���������������� */
		OvertimehVO headVO = (OvertimehVO) aggVO.getParentVO();
		// �ϼ�ʱ��
		selRow.setValue(ds.nameToIndex(OvertimehVO.SUMHOUR), headVO.getLength());

		/** ���������ӱ����� */
		OvertimebVO[] vos = aggVO.getOvertimebVOs();
		new SuperVO2DatasetSerializer().update(vos, dsDetail);
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
	 * ���ݼӰ����PK����֯PK, ��üӰ����copy��PK
	 * 
	 * @param pk_org
	 * @param pk_overtimetype
	 * @return
	 */
	public static TimeItemCopyVO getTimeItemCopyVO(String pk_org, String pk_overtimetype) {
		TimeItemCopyVO timeItemCopyVO = null;
		// ��ѯ�Ӱ����copy��PK
		try {
			ITimeItemQueryService service = ServiceLocator.lookup(ITimeItemQueryService.class);
			timeItemCopyVO = service.queryCopyTypesByDefPK(pk_org, pk_overtimetype, TimeItemVO.OVERTIME_TYPE);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
		return timeItemCopyVO;
	}
}
