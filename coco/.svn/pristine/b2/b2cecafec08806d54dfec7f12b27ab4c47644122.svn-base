package nc.impl.hrsms.ta.shift;

import nc.bs.logging.Logger;
import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.hr.utils.ResHelper;
import nc.itf.hrsms.ta.shift.IStoreShiftQueryMaintain;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class StoreShiftUniqueValidator implements Validator{

	private static final long serialVersionUID = 1L;
	private IStoreShiftQueryMaintain queryService;
	
	@Override
	public ValidationFailure validate(Object obj) {
		ShiftVO vo = null;
		if(obj == null)
			return null;
		if (obj instanceof SuperVO)
			vo = (ShiftVO)obj;
		if (obj instanceof AggregatedValueObject)
			vo = (ShiftVO)((AggregatedValueObject) obj).getParentVO();
		try {
			validateAggShift(vo);
		} catch(nc.vo.pub.ValidationException e) {
			throw new BusinessExceptionAdapter(e);
		}
		return null;
	}

	/**
	 * 验证班次唯一信息
	 * @param aggVo
	 * @throws ValidationException
	 */
	private void validateAggShift(ShiftVO vo) throws ValidationException{
		if(vo == null) {
			return;
		}
		String condition = "1=1";
		//修改时不能和自己比较，复制和新增均不需要加入该条件
		if(vo.getStatus() == VOStatus.UPDATED) {
			condition += " and " + ShiftVO.PK_SHIFT +" <> '" + vo.getPk_shift() + "' ";
		}

//		Class shiftClass =  AggShiftVO.class;
//		String orgName = ResHelper.getString("common","UC000-000003")/*@res "业务单元"*/;
//		String shiftName = ResHelper.getString("common","UC000-0002923")/*@res "班次"*/;
		//同一个门店    业务单元
		condition += " and pk_dept = '" + vo.getPk_dept() +"' ";
		//同一班次类别
//		if(vo.getPk_shifttype() != null)
//			condition += " and " + ShiftVO.PK_SHIFTTYPE + " = '" + vo.getPk_shifttype() + "'";
		AggShiftVO[] orgAggVos = null;
		try {
			orgAggVos = getQueryService().queryByCondition(condition);
		} catch(BusinessException e) {
			Logger.error(e);
		}
		
		String errMsg = NCLangRes4VoTransl.getNCLangRes().getStrByID("uffactory_hyeaa", "AbstractUniqueRuleValidate-000000");
		
		if(!ArrayUtils.isEmpty(orgAggVos)) {
			for(AggShiftVO compareaggvo: orgAggVos) {
				ShiftVO compareVo = (ShiftVO)compareaggvo.getParentVO();
				if(!StringUtils.isEmpty(vo.getCode()) && !StringUtils.isEmpty(compareVo.getCode())
						&& vo.getCode().equals(compareVo.getCode())) {
//					throw new ValidationException("编码与该" + orgName + "和该班次类别下其他" + shiftName + "编码重复");
//					throw new ValidationException(ResHelper.getString("hrbd","0hrbd0206"
///*@res "编码与该{0}下其他{1}编码重复"*/,orgName,shiftName));
					throw new ValidationException(errMsg + "["+ 
							ResHelper.getString("common","2UC000-000721")/*@res "编码"*/ + ":" + vo.getCode() + "]");
				}
				if(!StringUtils.isEmpty(vo.getMultiLangName()) && !StringUtils.isEmpty(compareVo.getMultiLangName())
						&&vo.getMultiLangName().equals(compareVo.getMultiLangName())) {
//					throw new ValidationException(ResHelper.getString("hrbd","0hrbd0207"
///*@res "名称与该{0}下其他{1}名称重复"*/,orgName,shiftName));
					throw new ValidationException(errMsg + "["+
							ResHelper.getString("common","2UC000-000244")/*@res "名称"*/  + ":" + vo.getMultiLangName() + "]");
				}
			}
		}
		

	}
	
	public IStoreShiftQueryMaintain getQueryService(){
		if(queryService == null) {
			queryService = new StoreShiftQueryMaintainImpl();
		}
		return queryService;
	}
}
