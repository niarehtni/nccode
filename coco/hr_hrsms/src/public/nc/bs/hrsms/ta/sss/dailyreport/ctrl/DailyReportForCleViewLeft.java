package nc.bs.hrsms.ta.sss.dailyreport.ctrl;

import java.util.Calendar;

import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.ta.monthreport.ctrl.MonthReportForEmpViewMain;
import nc.uap.ctrl.tpl.qry.IQueryController;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;

/**
 * @author chouhl
 */
public class DailyReportForCleViewLeft extends IQueryController {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	// �ֶ�
	public static final String FS_TBMYEAR = "tbmyear";
	public static final String FS_TBMMONTH = "tbmmonth";

	/**
	 * ����Ĭ��ֵ
	 */
	@Override
	public void simpleQueryonDataLoad(DataLoadEvent dataLoadEvent) {
		Dataset ds = dataLoadEvent.getSource();
		Row row = DatasetUtil.initWithEmptyRow(ds, Row.STATE_NORMAL);
		if (row == null) {
			return;
		}
		// ��Ա���ڵ�HR��֯
		Calendar calendar = Calendar.getInstance();
		String date = calendar.get(Calendar.YEAR)+"-"//��ȡ��ǰ����
		+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DATE);
//		String pk_hr_org = SessionUtil.getHROrg();
//		UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDateByPkOrg(pk_hr_org);
//		String begindate = dates[0].toString();
//		String enddate = dates[1].toString();
		// ��ʼ����
		row.setValue(ds.nameToIndex(MonthReportForEmpViewMain.PARAM_ID_BEGINDATE), date);
		// ��������
		row.setValue(ds.nameToIndex(MonthReportForEmpViewMain.PARAM_ID_ENDDATE), date);

	}

	@Override
	public void simpleValueChanged(DatasetCellEvent datasetCellEvent) {

	}

	@Override
	public void advaceDsConditionChanged(DatasetCellEvent dataLoadEvent) {
	}

}
