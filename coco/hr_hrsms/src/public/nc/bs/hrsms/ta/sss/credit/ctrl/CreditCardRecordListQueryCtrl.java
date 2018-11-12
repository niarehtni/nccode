package nc.bs.hrsms.ta.sss.credit.ctrl;

import nc.bs.hrsms.ta.sss.credit.CreditCardRecordConsts;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.uap.ctrl.tpl.qry.IQueryController;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

public class CreditCardRecordListQueryCtrl extends IQueryController {

	@Override
	public void simpleQueryonDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		Row selRow = ds.getSelectedRow();
		if (selRow == null) {
			selRow = DatasetUtil.initWithEmptyRow(ds, true, Row.STATE_NORMAL);
		}
		setDefaultConditions(ds, selRow);
	}
	@Override
	public void advaceDsConditionChanged(DatasetCellEvent dataLoadEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void simpleValueChanged(DatasetCellEvent dataLoadEvent) {
		// TODO Auto-generated method stub
		Dataset ds = dataLoadEvent.getSource();
		int colIndex = dataLoadEvent.getColIndex();
		if (colIndex == ds.nameToIndex(CreditCardRecordConsts.FD_BEGINDATE) || colIndex == ds.nameToIndex(CreditCardRecordConsts.FD_ENDDATE)) {
			ApplicationContext appCxt = AppLifeCycleContext.current().getApplicationContext();
			String oldValue = (String) dataLoadEvent.getOldValue();
			String newValue = (String) dataLoadEvent.getNewValue();
			if (oldValue.equals(newValue)) {
				appCxt.addAppAttribute(CreditCardRecordConsts.SESSION_DATE_CHANGE, UFBoolean.FALSE);
			} else {
				// ��ʼ����/�������ڷ����˱仯
				appCxt.addAppAttribute(CreditCardRecordConsts.SESSION_DATE_CHANGE, UFBoolean.TRUE);
			}
		}
	}
	/**
	 * ��������-����Ĭ�ϲ�ѯ����
	 * 
	 * @param ds
	 * @param selRow
	 */
	private void setDefaultConditions(Dataset ds, Row selRow) {

		String pk_hr_org = SessionUtil.getHROrg();
		UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDateByPkOrg(pk_hr_org);
		if (ds.nameToIndex(CreditCardRecordConsts.FD_BEGINDATE) > -1) {
			// ��ʼ����
			selRow.setValue(ds.nameToIndex(CreditCardRecordConsts.FD_BEGINDATE), String.valueOf(dates[0]));
		}
		if (ds.nameToIndex(CreditCardRecordConsts.FD_ENDDATE) > -1) {
			if(dates[1].after(dates[0].getDateAfter(60))){
				dates[1] = dates[0].getDateAfter(60);
			}
			// ��������
			selRow.setValue(ds.nameToIndex(CreditCardRecordConsts.FD_ENDDATE), String.valueOf(dates[1]));
		}
		
	}

}
