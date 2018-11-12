package nc.impl.bd.workcalendrule;

import java.util.HashSet;
import java.util.Set;

import nc.bs.bd.baseservice.md.BDSingleBaseService;
import nc.bs.bd.workcalendrule.validator.WorkCalendarRuleNullValidator;
import nc.bs.uif2.validation.Validator;
import nc.itf.bd.workcalendrule.IWorkCalendarRuleService;
import nc.vo.bd.pub.DistributedAddBaseValidator;
import nc.vo.bd.pub.SingleDistributedUpdateValidator;
import nc.vo.bd.workcalendrule.WorkCalendarRuleVO;
import nc.vo.pub.BusinessException;
import nc.vo.util.BDUniqueRuleValidate;

public class WorkCalendarRuleImpl extends
		BDSingleBaseService<WorkCalendarRuleVO> implements
		IWorkCalendarRuleService {
	public WorkCalendarRuleImpl() {
		super("cf7bba48-880a-45a3-86f7-9979c8f30a5f", null);
	}

	public void delWorkCalendRuleVO(WorkCalendarRuleVO deledVO)
			throws BusinessException {
		super.deleteVO(deledVO);
	}

	public WorkCalendarRuleVO insertWorkCalendRuleVO(
			WorkCalendarRuleVO insertedVO) throws BusinessException {
		//但强 台湾本地化 2018-4-18 17:12:33 line 31-33 39-41
		if(!validateRepetDay(insertedVO)){
			throw new BusinessException("公休日，例假，休息日有重复，不允许保存");
		}
		
		if(ruleSet2.isEmpty()){
			throw new BusinessException("必须选择一天为例假日！");
		}
		if(ruleSet3.isEmpty()){
			throw new BusinessException("必须选择一天为休息日！");
		}
		
		return (WorkCalendarRuleVO) super.insertVO(insertedVO);
	}

	public WorkCalendarRuleVO updateWorkCalendRuleVO(WorkCalendarRuleVO updateVO)
			throws BusinessException {
		if(!validateRepetDay(updateVO)){
			throw new BusinessException("公休日，例假日，休息日有重复，不允许保存");
		}
		if(ruleSet2.isEmpty()){
			throw new BusinessException("必须选择一天为例假日！");
		}
		if(ruleSet3.isEmpty()){
			throw new BusinessException("必须选择一天为休息日！");
		}
		return (WorkCalendarRuleVO) updateVO(updateVO);
	}

	/**
	 * 台湾本地化修改 不允许公休日与例假日重复
	 */
	private Set<Integer> ruleSet = new HashSet();
	private Set<Integer> ruleSet2 = new HashSet();
	private Set<Integer> ruleSet3 = new HashSet();

	public boolean validateRepetDay(WorkCalendarRuleVO calendRuleVO) {
		ruleSet.clear();
		ruleSet2.clear();
		if ((calendRuleVO.getSunday() != null)
				&& (calendRuleVO.getSunday().booleanValue())) {
			ruleSet.add(Integer.valueOf(1));
		}
		if ((calendRuleVO.getMonday() != null)
				&& (calendRuleVO.getMonday().booleanValue())) {
			ruleSet.add(Integer.valueOf(2));
		}
		if ((calendRuleVO.getTuesday() != null)
				&& (calendRuleVO.getTuesday().booleanValue())) {
			ruleSet.add(Integer.valueOf(3));
		}
		if ((calendRuleVO.getWednesday() != null)
				&& (calendRuleVO.getWednesday().booleanValue())) {
			ruleSet.add(Integer.valueOf(4));
		}
		if ((calendRuleVO.getThursday() != null)
				&& (calendRuleVO.getThursday().booleanValue())) {
			ruleSet.add(Integer.valueOf(5));
		}
		if ((calendRuleVO.getFriday() != null)
				&& (calendRuleVO.getFriday().booleanValue())) {
			ruleSet.add(Integer.valueOf(6));
		}
		if ((calendRuleVO.getSaturday() != null)
				&& (calendRuleVO.getSaturday().booleanValue())) {
			ruleSet.add(Integer.valueOf(7));
		}
		
		
		//校验公休日和例假日 冲突数据直接返回false
		if((calendRuleVO.getSunday1() != null)
				&& (calendRuleVO.getSunday1().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(1))){
				return false;
			}
			ruleSet2.add(Integer.valueOf(1));
		}
		if((calendRuleVO.getMonday1() != null)
				&& (calendRuleVO.getMonday1().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(2))){
				return false;
			}
			ruleSet2.add(Integer.valueOf(2));
		}
		if((calendRuleVO.getTuesday1() != null)
				&& (calendRuleVO.getTuesday1().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(3))){
				return false;
			}
			ruleSet2.add(Integer.valueOf(3));
		}
		if((calendRuleVO.getWednesday1() != null)
				&& (calendRuleVO.getWednesday1().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(4))){
				return false;
			}
			ruleSet2.add(Integer.valueOf(4));
		}
		if((calendRuleVO.getThursday1() != null)
				&& (calendRuleVO.getThursday1().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(5))){
				return false;
			}
			ruleSet2.add(Integer.valueOf(5));
		}
		if((calendRuleVO.getFriday1() != null)
				&& (calendRuleVO.getFriday1().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(6))){
				return false;
			}
			ruleSet2.add(Integer.valueOf(6));
		}
		if((calendRuleVO.getSaturday1() != null)
				&& (calendRuleVO.getSaturday1().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(7))){
				return false;
			}
			ruleSet2.add(Integer.valueOf(7));
		}
		
		//校验公休日，例假日，休息日 冲突数据直接返回false
		if((calendRuleVO.getSunday2() != null)
				&& (calendRuleVO.getSunday2().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(1)) || ruleSet2.contains(Integer.valueOf(1))){
				return false;
			}
			ruleSet3.add(Integer.valueOf(1));
		}
		if((calendRuleVO.getMonday2() != null)
				&& (calendRuleVO.getMonday2().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(2)) || ruleSet2.contains(Integer.valueOf(2))){
				return false;
			}
			ruleSet3.add(Integer.valueOf(2));
		}
		if((calendRuleVO.getTuesday2() != null)
				&& (calendRuleVO.getTuesday2().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(3)) || ruleSet2.contains(Integer.valueOf(3))){
				return false;
			}
			ruleSet3.add(Integer.valueOf(3));
		}
		if((calendRuleVO.getWednesday2() != null)
				&& (calendRuleVO.getWednesday2().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(4)) || ruleSet2.contains(Integer.valueOf(4))){
				return false;
			}
			ruleSet3.add(Integer.valueOf(4));
		}
		if((calendRuleVO.getThursday2() != null)
				&& (calendRuleVO.getThursday2().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(5)) || ruleSet2.contains(Integer.valueOf(5))){
				return false;
			}
			ruleSet3.add(Integer.valueOf(5));
		}
		if((calendRuleVO.getFriday2() != null)
				&& (calendRuleVO.getFriday2().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(6)) || ruleSet2.contains(Integer.valueOf(6))){
				return false;
			}
			ruleSet3.add(Integer.valueOf(6));
		}
		if((calendRuleVO.getSaturday2() != null)
				&& (calendRuleVO.getSaturday2().booleanValue())){
			if(ruleSet.contains(Integer.valueOf(7)) || ruleSet2.contains(Integer.valueOf(7))){
				return false;
			}
			ruleSet3.add(Integer.valueOf(7));
		}
		
		return true;
	}

	protected Validator[] getInsertValidator() {
		Validator nullValidator = new WorkCalendarRuleNullValidator();

		BDUniqueRuleValidate uniqueValidator = new BDUniqueRuleValidate();
		return new Validator[] { uniqueValidator, nullValidator,
				new DistributedAddBaseValidator() };
	}

	protected Validator[] getUpdateValidator(WorkCalendarRuleVO oldVO) {
		Validator nullValidator = new WorkCalendarRuleNullValidator();
		Validator uniqueValidator = new BDUniqueRuleValidate();
		return new Validator[] { uniqueValidator, nullValidator,
				new SingleDistributedUpdateValidator() };
	}
}