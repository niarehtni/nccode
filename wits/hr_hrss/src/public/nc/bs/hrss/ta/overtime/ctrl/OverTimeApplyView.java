package nc.bs.hrss.ta.overtime.ctrl;

import java.util.Calendar;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.common.ctrl.TaApplyBaseView;
import nc.bs.hrss.ta.overtime.OverTimeConsts;
import nc.bs.hrss.ta.overtime.lsnr.OverTimeAddProcessor;
import nc.bs.hrss.ta.overtime.lsnr.OverTimeCommitProcessor;
import nc.bs.hrss.ta.overtime.lsnr.OverTimeCopyProcessor;
import nc.bs.hrss.ta.overtime.lsnr.OverTimeEditProcessor;
import nc.bs.hrss.ta.overtime.lsnr.OverTimeLineAddProcessor;
import nc.bs.hrss.ta.overtime.lsnr.OverTimeLineDelProcessor;
import nc.bs.hrss.ta.overtime.lsnr.OverTimeSaveAddProcessor;
import nc.bs.hrss.ta.overtime.lsnr.OverTimeSaveProcessor;
import nc.itf.hr.wa.IGlobalCountryQueryService;
import nc.itf.hrss.pub.cmd.prcss.IAddProcessor;
import nc.itf.hrss.pub.cmd.prcss.ICommitProcessor;
import nc.itf.hrss.pub.cmd.prcss.ICopyProcessor;
import nc.itf.hrss.pub.cmd.prcss.IEditProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineDelProcessor;
import nc.itf.hrss.pub.cmd.prcss.ILineInsertProcessor;
import nc.itf.hrss.pub.cmd.prcss.ISaveProcessor;
import nc.itf.ta.IOvertimeAppInfoDisplayer;
import nc.itf.ta.IOvertimeApplyQueryMaintain;
import nc.itf.ta.ITimeItemQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
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
import nc.vo.bd.countryzone.CountryZoneVO;
import nc.vo.org.OrgQueryUtil;
import nc.vo.org.OrgVO;
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

/**
 * �Ӱ������ViewList
 * 
 * @author qiaoxp
 */
public class OverTimeApplyView extends TaApplyBaseView implements IController {
	private static final long serialVersionUID = 1L;

	/**
	 * ��������
	 * 
	 * @return
	 */
	@Override
	protected String getBillType() {
		return OverTimeConsts.BILL_TYPE_CODE;
	}

	/**
	 * ���ݼ�ID
	 * 
	 * @return
	 */
	@Override
	protected String getDatasetId() {
		return OverTimeConsts.DS_MAIN_NAME;
	}

	/**
	 * ���ݾۺ�����VO
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends AggregatedValueObject> getAggVoClazz() {
		return OverTimeConsts.CLASS_NAME_AGGVO;
	}

	/**
	 * ������PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends IAddProcessor> getAddPrcss() {
		return OverTimeAddProcessor.class;
	}

	/**
	 * �޸ĵ�PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends IEditProcessor> getEditPrcss() {
		return OverTimeEditProcessor.class;
	}

	/**
	 * ���Ƶ�PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ICopyProcessor> getCopyPrcss() {
		return OverTimeCopyProcessor.class;
	}

	/**
	 * ���沢������PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSaveAddPrcss() {
		return OverTimeSaveAddProcessor.class;
	}

	/**
	 * �����PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ISaveProcessor> getSavePrcss() {
		return OverTimeSaveProcessor.class;
	}

	/**
	 * �ύ�Ĳ�����
	 */
	@Override
	protected Class<? extends ICommitProcessor> getCommitPrcss() {
		return OverTimeCommitProcessor.class;
	}

	/**
	 * ��������PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ILineInsertProcessor> getLineAddPrcss() {
		return OverTimeLineAddProcessor.class;
	}

	/**
	 * ��ɾ����PROCESSOR
	 * 
	 * @return
	 */
	@Override
	protected Class<? extends ILineDelProcessor> getLineDelPrcss() {
		return OverTimeLineDelProcessor.class;
	}

	/**
	 * �����ݼ�����
	 * 
	 * @param dataLoadEvent
	 */
	public void onDataLoad_hrtaovertimeh(DataLoadEvent dataLoadEvent) {
		super.onDataLoad(dataLoadEvent);

		LfwView view = ViewUtil.getCurrentView();
		Dataset ds = ViewUtil.getDataset(view, OverTimeConsts.DS_MAIN_NAME);
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			return;
		}
		Dataset dsDetail = ViewUtil.getDataset(view, OverTimeConsts.DS_SUB_NAME);
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
		if (colIndex != ds.nameToIndex(OvertimehVO.PK_OVERTIMETYPE)) {
			return;
		}
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			return;
		}
		// ���ÿ��ڵ�λ����ʾ
		setTimeUnitText(ds, selRow);

		LfwView view = ViewUtil.getCurrentView();
		Dataset dsDetail = ViewUtil.getDataset(view, OverTimeConsts.DS_SUB_NAME);
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

	
	//MOD James
	public void resetOvertimeType(Dataset dsDetail,Dataset ds) throws BusinessException {
		
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			return;
		}
		Row[] allDetailRows = dsDetail.getAllRow();
		 UFDateTime beiginTime= new UFDateTime();
		 String overtimeType="";
		 if(allDetailRows.length>0){
			 beiginTime = (UFDateTime)allDetailRows[0].getValue(dsDetail.nameToIndex(OvertimebVO.OVERTIMEBEGINTIME));
			overtimeType= GetOvertimeType(beiginTime,String.valueOf(selRow.getValue(ds.nameToIndex("pk_psndoc"))),String.valueOf(selRow.getValue(ds.nameToIndex("pk_org"))));
		 }
		 
		 
		 for(int i=1;i< allDetailRows.length; i++){			
			beiginTime = (UFDateTime) allDetailRows[i].getValue(dsDetail.nameToIndex(OvertimebVO.OVERTIMEBEGINTIME));
			String otType= GetOvertimeType(beiginTime,(String)selRow.getValue(ds.nameToIndex("pk_psndoc")),(String)selRow.getValue(ds.nameToIndex("pk_org")));
			if(!overtimeType.isEmpty() && !overtimeType.equals(otType)){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
						"6017wits_0", "06017wits-0010")						
				/*
				 * @res ƽ�ռӰ࣬�������ռӰ࣬��Ϣ�ռӰࡣՈ��ͬһ���Ӱ���Ո��
				 */);
				
			}else
			{
				overtimeType=otType;
			}
			
		 }
		
	 
		selRow.setValue(ds.nameToIndex(OvertimehVO.PK_OVERTIMETYPE), overtimeType);
		// ���ÿ��ڵ�λ����ʾ
		setTimeUnitText(ds, selRow);
		
		
	}
	
	public String GetOvertimeType(UFDateTime beiginTime, String pkPsndoc, String pkOrg ) throws BusinessException
	{
		if(beiginTime ==null)
		{
			return "";
		}
		StringBuilder sql = new StringBuilder();			 
		 
	 	sql.append(" select case when tbm_psncalendar.if_rest='Y' and tbm_psncalholiday.pk_psncalhol is not null then  (select tbm_timeitem.pk_timeitem from tbm_timeitem");
		sql.append(" inner join tbm_timeitemcopy on tbm_timeitem.pk_timeitem=tbm_timeitemcopy.pk_timeitem");
		sql.append(" where  tbm_timeitem.itemtype=1 and tbm_timeitemcopy.pk_org=tbm_psncalendar.pk_org");
		sql.append(" and tbm_timeitemcopy.enablestate=2 ");
		sql.append(" and tbm_timeitem.pk_timeitem in('1001A1100000000009PE','1002Z710000000021ZLX')) ");
		
		sql.append(" when exists (select pk_holiday from org_orgs ");
		sql.append(" inner join bd_workcalendar on org_orgs.workcalendar=bd_workcalendar.pk_workcalendar ");
		sql.append(" inner join bd_holiday on bd_workcalendar.pk_holidayrule=bd_holiday.pk_holidaysort ");
		sql.append(" where org_orgs.pk_org=tbm_psncalendar.pk_org ");
		sql.append(" and bd_holiday.starttime<='"+beiginTime.toString()+"' and endtime>='"+beiginTime.toString()+"' ");
		sql.append(" ) then '1002Z710000000021ZLX' ");
		sql.append("when tbm_psncalendar.if_rest='Y' and tbm_psncalholiday.pk_psncalhol is null then (select tbm_timeitem.pk_timeitem from tbm_timeitem");
		sql.append(" inner join tbm_timeitemcopy on tbm_timeitem.pk_timeitem=tbm_timeitemcopy.pk_timeitem");
		sql.append(" where  tbm_timeitem.itemtype=1 and tbm_timeitemcopy.pk_org=tbm_psncalendar.pk_org");
		sql.append(" and tbm_timeitemcopy.enablestate=2 ");
		sql.append(" and tbm_timeitem.pk_timeitem in('1001A1100000000009PG','1002Z710000000021ZLV'))");
		
		sql.append(" else  (select tbm_timeitem.pk_timeitem from tbm_timeitem");
		sql.append(" inner join tbm_timeitemcopy on tbm_timeitem.pk_timeitem=tbm_timeitemcopy.pk_timeitem");
		sql.append(" where  tbm_timeitem.itemtype=1 and tbm_timeitemcopy.pk_org=tbm_psncalendar.pk_org");
		sql.append(" and tbm_timeitemcopy.enablestate=2 ");
		sql.append(" and tbm_timeitem.pk_timeitem in('1001A1100000000009PC','1002Z710000000021ZLT')) end as pk_timeitem from tbm_psncalendar"); 
		sql.append(" left join tbm_psncalholiday on tbm_psncalendar.pk_psncalendar=tbm_psncalholiday.pk_psncalendar");
		sql.append("  where  pk_psndoc='"+pkPsndoc+"'");
		sql.append(" and tbm_psncalendar.pk_org='"+pkOrg+"'");
		sql.append(" and tbm_psncalendar.calendar='"+beiginTime.toString().subSequence(0, 10)+"'");
		
		String overtimeType = (String) new BaseDAO().executeQuery(sql.toString(), new ColumnProcessor());
		
		return overtimeType;
		
	}
	/**
	 * ���ÿ��ڵ�������ֶε���ʾ
	 * 
	 * @param ds
	 * @param selRow
	 */
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
				elem.setLabelPos(text
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0001")
				/*
				 * @ res "(��)"
				 */);
			} else if (timeitemunit != null && TimeItemCopyVO.TIMEITEMUNIT_HOUR == timeitemunit) {// Сʱ
				elem.setLabelPos(text
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0002")/*
																											 * @
																											 * res
																											 * "(Сʱ)"
																											 */);
			} else {
				elem.setLabelPos(text);
			}
		} else {
			elem.setLabelPos(text);
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
		if (colIndex != dsDetail.nameToIndex(OvertimebVO.OVERTIMEBEGINTIME)
				&& colIndex != dsDetail.nameToIndex(OvertimebVO.OVERTIMEENDTIME)
				&& colIndex != dsDetail.nameToIndex(OvertimebVO.DEDUCT)) {
			return;
		}
		
		
		// MOD {�ۼ����ú������¼���} kevin.nie 2017-09-25 start
		// if (colIndex == dsDetail.nameToIndex(OvertimebVO.DEDUCT)) {
		// return;
		// }
		// {�ۼ����ú������¼���} kevin.nie 2017-09-25 end
		LfwView view = ViewUtil.getCurrentView();
		Dataset dsMaster = ViewUtil.getDataset(view, OverTimeConsts.DS_MAIN_NAME);
		Row rowMaster = dsMaster.getSelectedRow();
		Row rowDetail = dsDetail.getSelectedRow();
		
		
		//MOD James
		if (colIndex == dsDetail.nameToIndex(OvertimebVO.OVERTIMEBEGINTIME)) {
			try {
//				String pk_hrorg = rowMaster.getString(dsMaster.nameToIndex(OvertimehVO.PK_ORG));
//				if (isTWOrg(pk_hrorg)) {
//					resetOvertimeType(dsDetail, dsMaster);
//				}
				resetOvertimeType(dsDetail, dsMaster);
			}  catch (BusinessException e) {
				new HrssException(e).deal();
			}
		}		
		

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

	public boolean isTWOrg(String pk_org) throws BusinessException {
		OrgVO[] orgVOs = OrgQueryUtil.queryOrgVOByPks(new String[] { pk_org });
		IGlobalCountryQueryService czQry = NCLocator.getInstance().lookup(
				IGlobalCountryQueryService.class);
		CountryZoneVO czVO = czQry.getCountryZoneByPK(orgVOs[0]
				.getCountryzone());
		return czVO.getCode().equals("TW");
	}
	private void dateChangeEvent(Dataset ds, Row selRow, Dataset dsMaster, Row rowMaster, int colIndex) {
		UFDateTime beiginTime = (UFDateTime) selRow.getValue(ds.nameToIndex(OvertimebVO.OVERTIMEBEGINTIME));
		UFDateTime endTime = (UFDateTime) selRow.getValue(ds.nameToIndex(OvertimebVO.OVERTIMEENDTIME));
		// �Ӱ࿪ʼʱ��
		if (colIndex == ds.nameToIndex(OvertimebVO.OVERTIMEBEGINTIME)) {
			if (beiginTime != null) {
				// �����˿�ʼ����
				selRow.setValue(ds.nameToIndex(OvertimebVO.OVERTIMEBEGINDATE), new UFLiteralDate(beiginTime.getDate()
						.toString()));
			} else {
				selRow.setValue(ds.nameToIndex(OvertimebVO.OVERTIMEBEGINDATE), null);
			}
		}
		if (colIndex == ds.nameToIndex(OvertimebVO.OVERTIMEENDTIME)) {
			if (endTime != null) {
				// �����˽�������
				selRow.setValue(ds.nameToIndex(OvertimebVO.OVERTIMEENDDATE), new UFLiteralDate(endTime.getDate()
						.toString()));
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
		AggOvertimeVO aggVO = (AggOvertimeVO) serializer.serialize(ds, new Dataset[] { dsDetail },
				OverTimeConsts.CLASS_NAME_AGGVO.getName());
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
		for (OvertimebVO bvo : vos) {
			if (null == bvo.getDeduct()) {
				bvo.setDeduct(Integer.valueOf(0));
			}
		}
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

	@Override
	protected String getDetailDsId() {
		return OverTimeConsts.DS_SUB_NAME;
	}
}