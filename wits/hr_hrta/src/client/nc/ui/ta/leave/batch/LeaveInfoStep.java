package nc.ui.ta.leave.batch;

import nc.hr.utils.ResHelper;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.pub.bill.BillModel;
import nc.ui.ta.wf.batch.BatchAddBillInfoStep;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.ta.away.AwayUtils;
import nc.vo.ta.leave.LeaveCommonVO;

public class LeaveInfoStep extends BatchAddBillInfoStep {

	@Override
	public void validate() throws WizardStepValidateException {
		BillModel billModel = getInfoPanel().getSubPanel().getBillCardPanel().getBillModel();
		int row = billModel.getRowCount();
		if(row == 0) {
			WizardStepValidateException wve= new WizardStepValidateException();
			wve.addMsg("detailisnull", ResHelper.getString("6017leave","06017leave0187")
/*@res "明细信息不能为空，请将填好的卡片数据增加到列表中!"*/);
			throw wve;
		}
		int year = 0;
		for(int i = 0; i < row; i++) {
			UFDateTime curBeginTime = (UFDateTime)billModel.getValueAt(i, LeaveCommonVO.LEAVEBEGINTIME);
			UFDateTime curEndTime = (UFDateTime)billModel.getValueAt(i, LeaveCommonVO.LEAVEENDTIME);
			if(year != 0 && year != curBeginTime.getYear()){
				WizardStepValidateException wve= new WizardStepValidateException();
				wve.addMsg("datetimemutex",ResHelper.getString("6017leave","06017leave0267"/*@res "第{0}行的休假时间能跨年,不能保存!"*/,i+"、"+(i+1)+""));
				throw wve;
			}
			if(curBeginTime.getYear() != curEndTime.getYear()){
				WizardStepValidateException wve= new WizardStepValidateException();
				wve.addMsg("datetimemutex",ResHelper.getString("6017leave","06017leave0267"/*@res "第{0}行的休假时间能跨年,不能保存!"*/,i+1+""));
				throw wve;
			}
			year = curBeginTime.getYear();
			for(int j = row -1; j >i; j--) {
				UFDateTime compBeginTime = (UFDateTime)billModel.getValueAt(j, LeaveCommonVO.LEAVEBEGINTIME);
				UFDateTime compEndTime = (UFDateTime)billModel.getValueAt(j, LeaveCommonVO.LEAVEENDTIME);
				if(AwayUtils.isMutex(curBeginTime, curEndTime, compBeginTime, compEndTime)) {
					WizardStepValidateException wve= new WizardStepValidateException();
					wve.addMsg("datetimemutex", ResHelper.getString("6017leave","06017leave0188")
/*@res "与明细时间有冲突，请修改!"*/);
					throw wve;
				}
			}
		}
	}



}