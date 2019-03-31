package nc.ui.ta.leave.balance.action;

import nc.ui.hr.uif2.action.RefreshAction;
import nc.ui.ta.leave.balance.model.LeaveBalanceAppModel;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;

import org.apache.commons.lang.StringUtils;


/**
 * @author 
 *
 */
@SuppressWarnings("serial")
public class RefreshLeaveBalanceAction extends RefreshAction {

	private AbstractAppModel hierachicalModel;
	
	@Override
	protected boolean isActionEnable() {
		boolean enable =  super.isActionEnable();
		if(enable)
		{
			enable = StringUtils.isNotBlank(getModel().getContext().getPk_org());
		}
		if(enable&&getHierachicalModel()!=null)
		{
			Object obj = getHierachicalModel().getSelectedData();
			enable = obj==null?false:true;
			if(enable)
			{
				LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO)obj;
				Integer leavesetperiod = typeVO.getLeavesetperiod();
//				boolean isYear = leavesetperiod!=null&&(Integer)SettlementPeriodEnum.YEAR.value()==leavesetperiod.intValue();
//				boolean isYear = leavesetperiod!=null&&(TimeItemCopyVO.LEAVESETPERIOD_YEAR==leavesetperiod||TimeItemCopyVO.LEAVESETPERIOD_DATE==leavesetperiod);
				
				//BEGIN 张恒    将65年资起算日的整合到63   2018/8/28
				boolean isYear = leavesetperiod != null
						&& (TimeItemCopyVO.LEAVESETPERIOD_YEAR == leavesetperiod
								|| TimeItemCopyVO.LEAVESETPERIOD_DATE == leavesetperiod
						// ssx added on 2018-03-16
						// for changes of start date of company age
						|| TimeItemCopyVO.LEAVESETPERIOD_STARTDATE == leavesetperiod
						//
						);
				//END 张恒    将65年资起算日的整合到63   2018/8/28
				
				if(!isYear)
				{
					String month = ((LeaveBalanceAppModel)getModel()).getMonth();
					enable = (month==null||month.equals(""))?false:true;
				}
			}
		}
		return enable;
	}

	public AbstractAppModel getHierachicalModel() {
		return hierachicalModel;
	}

	public void setHierachicalModel(AbstractAppModel hierachicalModel) {
		this.hierachicalModel = hierachicalModel;
	}
	
}
