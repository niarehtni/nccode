package nc.bs.hrsms.ta.SignReg.ctrl;

import java.text.MessageFormat;

import nc.hr.utils.ResHelper;
import nc.vo.pub.ValidationException;
import nc.vo.ta.signcard.SignRegVO;


/**
 * 签卡登记删除前台校验
 * 1、签卡申请单据审批通过后进入登记的数据不能删除
 * 2、签卡时间对应考勤期间已封存的不能删除（放到后台校验中）
 * @author caiyl
 *
 */
public class SignCardRegDeleteValidator {

	public void validate(Object obj) throws ValidationException{
		if(obj == null) {
			throw new ValidationException(ResHelper.getString("6017signcardreg","06017signcardreg0008")
/*@res "请选择要删除的数据"*/);
		}
		if(obj.getClass().isArray()) {
			validateObjs((Object[])obj);
		}
		if(obj instanceof SignRegVO) {
			validateSignReg((SignRegVO)obj);
		}
	}

	private void validateObjs(Object[] objs) throws ValidationException {
		for(Object obj : objs) {
			if(obj instanceof SignRegVO) {
				validateSignReg((SignRegVO)obj);
			}
		}
	}

	private void validateSignReg(SignRegVO regvo) throws ValidationException {
		if(regvo.getPk_billsourceh() != null || regvo.getPk_billsourceb() != null) {
			String msg = ResHelper.getString("6017signcardreg","06017signcardreg0009")
			/*@res "签卡时间为{0}的签卡记录已审批通过，不能删除"*/;
			throw new ValidationException(MessageFormat.format(msg, regvo.getSigntime()));
		}
	}
}