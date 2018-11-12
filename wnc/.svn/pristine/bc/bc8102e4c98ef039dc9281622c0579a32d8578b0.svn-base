package nc.ui.wa.othersource.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.Logger;
import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.itf.hr.wa.IOtherSourceManageService;
import nc.vo.hr.frame.persistence.BooleanProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.wa.othersource.OtherSourceVO;


/**
 * @author daicy
 *
 */
public class DeleteValidatorService implements IValidationService {


	@Override
	public void validate(Object obj) throws ValidationException {

		OtherSourceVO vo = null;
		if (obj != null && obj instanceof OtherSourceVO) {
			
			vo = (OtherSourceVO) obj;
			
			
			ValidationException ex = new ValidationException();
			

			Boolean result = false;
			try {
//				PubDelegator.getIPersistenceRetrieve().executeQuery(description,
//						new ArrayListProcessor());
				StringBuffer sql = new StringBuffer();
				sql.append(" SELECT 1");
				sql.append(" FROM WA_CLASSITEM");
				sql.append(" WHERE  vformula like '%"+vo.getPk_otherdatasource()+"%'");

//				
				result = (Boolean) NCLocator.getInstance().lookup(IOtherSourceManageService.class)
						.executeQuery(sql.toString(),new BooleanProcessor());
				
			} catch (BusinessException msg) {
				Logger.debug(msg.toString());
			}
			if(result){
				String message = "该外部数据源已经被薪资发放项目引用不能删除！" ;
				ex.addValidationFailure(new ValidationFailure(message));
				throw ex;
			}

		}

	}

}