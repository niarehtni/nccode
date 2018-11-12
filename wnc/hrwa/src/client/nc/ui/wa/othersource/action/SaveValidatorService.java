package nc.ui.wa.othersource.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.itf.hr.wa.IOtherSourceManageService;
import nc.ui.hr.global.Global;
import nc.vo.hr.frame.persistence.BooleanProcessor;
import nc.vo.wa.othersource.OtherSourceVO;


/**
 * @author daicy
 *
 */
public class SaveValidatorService implements IValidationService {


	@Override
	public void validate(Object obj) throws ValidationException {

		OtherSourceVO vo = null;
		if (obj != null && obj instanceof OtherSourceVO) {
			
			vo = (OtherSourceVO) obj;
			String description = vo.getFormulastr().toLowerCase();
//			update by zhoulei 20090409 增加宏变量
			String userid = Global.getUserID();
			String tempdate = "2009-03-31";
			if(description.indexOf("#pk_user#") != -1)
			{
				description = description.replace("#pk_user#", "'"+userid+"'");
			}
			if(description.indexOf("#pk_org#")!= -1)
			{
				description = description.replace("#pk_org#", "'0001'");
			}
			if(description.indexOf("#startdate#")!= -1)
			{
				description = description.replace("#startdate#", "'"+tempdate+"'");
			}
			if(description.indexOf("#enddate#")!= -1)
			{
				description = description.replace("#enddate#", "'"+tempdate+"'");
			}
			if(description.indexOf("#cyear#")!= -1)
			{
				description = description.replace("#cyear#", "'2009'");
			}
			if(description.indexOf("#cperiod#")!= -1)
			{
				description = description.replace("#cperiod#", "'03'");
			}
			if(description.indexOf("#waclassid#")!= -1)
			{
//				暂时用用户id替换
				description = description.replace("#waclassid#", "'"+userid+"'");
			}
			
			ValidationException ex = new ValidationException();
			
			
//			--end
			String message = "项目定义的内容不正确，" ;
			if(description.indexOf("from")<=-1){
				message+="不是完整的SQL语句。";
				ex.addValidationFailure(new ValidationFailure(message));
				throw ex;
			}
			String selectsql = description.substring(0,description.indexOf("from"));
			if (!(selectsql.contains("pk_psndoc")&& selectsql
					.contains("dvalue")&&selectsql
					.contains("cyear")&&selectsql
					.contains("dvalue"))) {
				message+="select语句中必须有cyear,cperiod,pk_psndoc,dvalue4个数据字段。";
				ex.addValidationFailure(new ValidationFailure(message));
				throw ex;	
			}
			

			try {		
			   NCLocator.getInstance().lookup(IOtherSourceManageService.class).executeQuery(description,new BooleanProcessor());
			} catch (Exception exce) {
				message +=exce.getMessage();
				ex.addValidationFailure(new ValidationFailure(message));
				throw ex;
			}

		}

	}

}