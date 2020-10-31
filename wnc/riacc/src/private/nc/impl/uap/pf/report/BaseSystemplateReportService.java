package nc.impl.uap.pf.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.bbd.func.IFuncRegisterQueryService;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.smart.context.SmartContext;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.pftemplate.SystemplateVO;
import nc.vo.pub.template.TemplateStyleEnum;
import nc.vo.sm.funcreg.FuncRegisterVO;
import nc.vo.uap.rbac.UserRoleVO;
import nc.vo.util.SqlWrapper;

import com.ufida.report.anareport.base.BaseQueryCondition;

/**
 * ģ����䱨���ѯ
 * @author huangpyc
 * �������ڣ�2013-12-6
 */
public class BaseSystemplateReportService {

	private IFuncRegisterQueryService funcRegisterQuery = null;
	
	// ��ȡ��ѯ����SQL
	public String getConditionSQL(SmartContext content) {
		String key_iquerycondition = com.ufida.report.anareport.FreeReportContextKey.KEY_IQUERYCONDITION;
		BaseQueryCondition condition = (BaseQueryCondition) content.getAttribute(key_iquerycondition);
		//�߼������⣬condition.getUserObject()ʼ�ն��ǿյģ����Ը������� update by luomin 
		//return condition==null || condition.getUserObject() == null ? null : condition.getUserObject().toString();
		/**
		 * pk_user in (select cuserid from sm_user where pk_org = '0001K61000000000FYJI') AND tempstyle = 0 AND pk_user in ('1001K61000000000AML0','1001K610000000006MSJ')
		 * ��ȡand���һ�� 
		 */
		String condition_rude  = condition.getDescriptors()[0].toString();
		//�����ģ���ѯ
		if(condition_rude.contains("templateid")){
			return condition_rude;
		}
		int last_and = condition_rude.lastIndexOf("AND");
		String pkUserSql = condition_rude.substring(last_and+3, condition_rude.length());
		if(!pkUserSql.contains("pk_user")){//˵��û�м����û���������
			return pkUserSql+":all_user";
		}
		//���ɫ select t.pk_role from sm_user_role t where 1=1 and t.cuserid
		String role_sql = "select pk_role from sm_user_role  where "+pkUserSql.replaceAll("pk_user", "cuserid");
		//��ְ�� select ruleid from sm_perm_func t where t.subjectid
		String perm_func_sql = " OR operator in (select ruleid from sm_perm_func  where subjectid in (" + role_sql +")";
		
		condition_rude = condition_rude.substring(0, last_and).replaceAll("pk_user", "operator") 
				+ "AND (" +pkUserSql.replaceAll("pk_user", "operator") 
				+ " OR operator in (" + role_sql + ")" + perm_func_sql+"))"+":"+pkUserSql;
		
		return condition_rude.substring(condition_rude.indexOf("AND")+3, condition_rude.length());//condition==null || condition.getDescriptors() == null ? null : condition.getDescriptors()[0].toString().replaceAll("pk_user", "operator");
		
		
	}
	
	public String getFun_Name(String funcode) throws BusinessException{
		FuncRegisterVO funcVO = getFuncRegisterQuery().queryFunctionByCode(funcode);
		//by_yixin1_�п����Ҳ�����Ӧ��vo���Ҳ�������null
		if(funcVO == null){
			return null;
		}
		return NCLangRes4VoTransl.getNCLangRes().getString("funcode",
				funcVO.getFun_name(), funcVO.getFuncode());
		
	}
	
	public String getTempStyle(int tempStyle){
		TemplateStyleEnum tempStyleEnum = TemplateStyleEnum.get(tempStyle);
		return String.valueOf(tempStyleEnum.getValue());//��ȡ����value() update by luomin
	}
	//��ȡģ����������  add by luomin
//	public String getTempStyleName(int tempStyle){
//		TemplateStyleEnum tempStyleEnum = TemplateStyleEnum.get(tempStyle);
//		return String.valueOf(tempStyleEnum.getDescription());//��ȡ����value() update by luomin
//	}
	
	public String getAssignType(int operatorType){
		if(operatorType == SystemplateVO.OPERATOR_TYPE_USER){
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("_template",
					"0_template0194")/* @res "���û�" */;
		}else if(operatorType == SystemplateVO.OPERATOR_TYPE_ROLE){
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("_template",
					"0_template0195")/* @res "����ɫ" */;
		}else{
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("_template",
					"0_template0196")/* @res "��ְ��" */;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<UserRoleVO> getUserRoleVOsByPKResponsibility(String pk_responsibility) throws BusinessException{
		String condition = " enabledate<={nowtime?} and (isnull(cast(disabledate as char),'~')='~' or disabledate>{nowtime?})"
				+ " and pk_role in (select subjectid from sm_perm_func where ruleid = '"
				+ pk_responsibility + "')";
		BaseDAO dao = new BaseDAO();
		SqlWrapper sw = new SqlWrapper(condition);
		sw.bind("nowtime", new UFDateTime());
		List<UserRoleVO> userRoleList = (List<UserRoleVO>) dao.retrieveByClause(UserRoleVO.class, sw.getSql(), sw.getSqlParameter());
		return userRoleList;
	}
	
	private IFuncRegisterQueryService getFuncRegisterQuery(){
		if(funcRegisterQuery == null){
			funcRegisterQuery = NCLocator.getInstance().lookup(IFuncRegisterQueryService.class);
		}
		return funcRegisterQuery;
	}
}
