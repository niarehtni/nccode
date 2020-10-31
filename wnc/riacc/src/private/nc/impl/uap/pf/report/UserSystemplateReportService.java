package nc.impl.uap.pf.report;

import java.util.ArrayList;
import java.util.List;

import nc.bs.bd.util.DBAUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.itf.uap.pf.report.IUserSystemplateReportService;
import nc.itf.uap.print.IPrintTemplateQry;
import nc.itf.uap.rbac.IRoleManageQuery;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.pub.smart.context.SmartContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.pftemplate.SystemplateVO;
import nc.vo.pub.pftemplate.report.UserSystemplateVO;
import nc.vo.pub.print.PrintTemplateVO;
import nc.vo.pub.query.QueryTempletVO;
import nc.vo.pub.query.QueryTempletVOMeta;
import nc.vo.pub.template.ITemplateStyle;
import nc.vo.uap.rbac.UserRoleVO;
import nc.vo.util.SqlWrapper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * 用户分配模板报表查询中语义模型查询
 * @author huangpyc
 * 创建日期：2013-12-9
 */
public class UserSystemplateReportService extends BaseSystemplateReportService implements
		IUserSystemplateReportService {

	public static final String TEMP_TABLENAME = "tmptemplate_usersys";

	private IBillTemplateQry billTempletQry = null;
	private IPrintTemplateQry printTemplateQry = null;
	private IRoleManageQuery roleManageQuery = null;
	
	/**
	 * 创建临时表返回临时表表名
	 */
	public String getTempName(SmartContext content) throws BusinessException {
		List<SystemplateVO> sysVOList = executeQuery(content);
		
		List<UserSystemplateVO> result = processData(sysVOList);
		
		String newTempTableName = createTempTable(result);
		
		return "select * from " + newTempTableName;
	}
	//用于接收查询条件中的用户主键
	private String pkuserStr = "";
	@SuppressWarnings("unchecked")
	private List<SystemplateVO> executeQuery(SmartContext content) throws BusinessException{
		String sql = "select * from pub_systemplate where isnull(operator,'~') != '~' and pk_corp = {pk_corp?} ";
		String conditionSQL = getConditionSQL(content);
		String[] pkuser = conditionSQL.split(":");
		pkuserStr = pkuser[1];
		if(!StringUtils.isEmpty(pkuser[0])) {
			sql += " and " + pkuser[0];
		}
		SqlWrapper sw = new SqlWrapper(sql);
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		sw.bind("pk_corp", pk_group);	
		List<SystemplateVO> result = (List<SystemplateVO>) new BaseDAO()
				.executeQuery(sw.getSql(), sw.getSqlParameter(),
						new BeanListProcessor(SystemplateVO.class));
		return result;
	}

	private List<UserSystemplateVO> processData(List<SystemplateVO> sysVOList) throws BusinessException{
		if(CollectionUtils.isEmpty(sysVOList)) return null;
		List<UserSystemplateVO> result = new ArrayList<UserSystemplateVO>();
		for(SystemplateVO sysVO : sysVOList){
			List<UserSystemplateVO> userSysVOList = createUserSystemplateVO(sysVO);
			if(CollectionUtils.isEmpty(userSysVOList))
				continue;
			result.addAll(userSysVOList);
		}
		return result;
	}
	
	private List<UserSystemplateVO> createUserSystemplateVO(SystemplateVO sysVO) throws BusinessException{
		List<UserSystemplateVO> userSysVOList = new ArrayList<UserSystemplateVO>();
		processOperator(userSysVOList, sysVO);
		processTemplate(userSysVOList, sysVO);
		return userSysVOList;
	}
	
	private void processOperator(List<UserSystemplateVO> userSysVOList, SystemplateVO sysVO) throws BusinessException{
		if(sysVO.getOperator_type() == SystemplateVO.OPERATOR_TYPE_USER){
			userSysVOList.add(getUserSystemplateVOOfUser(sysVO));
		}else if(sysVO.getOperator_type() == SystemplateVO.OPERATOR_TYPE_ROLE){
			UserRoleVO[] userRoleVOs = getRoleManageQuery().queryUserRoleVOByRoleID(new String[]{sysVO.getOperator()});
			if(ArrayUtils.isEmpty(userRoleVOs))
				return;
			for(UserRoleVO userRoleVO : userRoleVOs){
				if(pkuserStr.contains(userRoleVO.getCuserid())){//过滤用户
					userSysVOList.add(getUserSystemplateVOOfUserAndRole(sysVO, userRoleVO));
				}
			}
		}else if(sysVO.getOperator_type() == SystemplateVO.OPERATOR_TYPE_RES){
			List<UserRoleVO> userRoleList = getUserRoleVOsByPKResponsibility(sysVO.getOperator());
			if(CollectionUtils.isEmpty(userRoleList))
				return;
			for(UserRoleVO userRoleVO : userRoleList){
				if(pkuserStr.contains(userRoleVO.getCuserid())){//过滤用户
					userSysVOList.add(getUserSystemplateVOOfAll(sysVO, userRoleVO));
				}
			}
			//职责通过直接授权分配给用户
//			String userSql = "select * from sm_perm_func perm where ruleid = '" + sysVO.getOperator() 
//					+ "' and exists (select * from dbo.sm_user where sm_user.cuserid = perm.subjectid)";
//			List<FuncPermConfigVO> funcPermConfigVOList = (List<FuncPermConfigVO>) new BaseDAO().executeQuery(userSql,
//					new BeanListProcessor(FuncPermConfigVO.class));
//			if(!CollectionUtils.isEmpty(funcPermConfigVOList)){
//				for(FuncPermConfigVO funcPermConfigVO : funcPermConfigVOList){
//					UserSystemplateVO userSysVO = new UserSystemplateVO();
//					userSysVO.setPk_systemplate(sysVO.getPrimaryKey());
//					userSysVO.setPk_user(funcPermConfigVO.getSubjectid());
//					userSysVO.setPk_resp(sysVO.getOperator());
//					userSysVOList.add(userSysVO);
//				}
//			}
		}
	}
	
	private UserSystemplateVO getUserSystemplateVOOfUser(SystemplateVO sysVO) throws BusinessException{
		UserSystemplateVO userSysVO = new UserSystemplateVO();
		userSysVO.setPk_corp(sysVO.getPk_corp());
		userSysVO.setPk_user(sysVO.getOperator());
		userSysVO.setAssignType(getAssignType(sysVO.getOperator_type()));
		userSysVO.setTempstyle(getTempStyle(sysVO.getTempstyle().intValue()));
		userSysVO.setFun_name(getFun_Name(sysVO.getFunnode()));
		return userSysVO;
	}
	
	private UserSystemplateVO getUserSystemplateVOOfUserAndRole(SystemplateVO sysVO, UserRoleVO userRoleVO) throws BusinessException{
		UserSystemplateVO userSysVO = new UserSystemplateVO();
		userSysVO.setPk_corp(sysVO.getPk_corp());
		userSysVO.setPk_user(userRoleVO.getCuserid());
		userSysVO.setAssignType(getAssignType(sysVO.getOperator_type()));
		userSysVO.setPk_role(sysVO.getOperator());
		userSysVO.setTempstyle(getTempStyle(sysVO.getTempstyle().intValue()));
		userSysVO.setFun_name(getFun_Name(sysVO.getFunnode()));
		return userSysVO;
	}
	
	private UserSystemplateVO getUserSystemplateVOOfAll(SystemplateVO sysVO, UserRoleVO userRoleVO) throws BusinessException{
		UserSystemplateVO userSysVO = new UserSystemplateVO();
		userSysVO.setPk_corp(sysVO.getPk_corp());
		userSysVO.setAssignType(getAssignType(sysVO.getOperator_type()));
		userSysVO.setPk_resp(sysVO.getOperator());
		userSysVO.setPk_user(userRoleVO.getCuserid());
		if(!userRoleVO.getCuserid().equals(userRoleVO.getPk_role())){
			userSysVO.setPk_role(userRoleVO.getPk_role());
		}
		userSysVO.setTempstyle(getTempStyle(sysVO.getTempstyle().intValue()));
		userSysVO.setFun_name(getFun_Name(sysVO.getFunnode()));
		return userSysVO;
	}
	
	private void processBillTemplet(List<UserSystemplateVO> userSysVOList, String pk_templet) throws BusinessException{
		BillTempletHeadVO[] billHeadVOs = getBillTemplateQry().findTemplet("pk_billtemplet = '" + pk_templet + "'");
		if(ArrayUtils.isEmpty(billHeadVOs)){
			return;
		}
		BillTempletHeadVO billHeadVO = billHeadVOs[0];
		for(UserSystemplateVO userSysVO : userSysVOList){
			userSysVO.setTemplateCode(billHeadVO.getPkBillTypeCode());
			userSysVO.setTemplateName(billHeadVO.getBillTempletName());
		}
	}
	
	private void processQueryTemplet(List<UserSystemplateVO> userSysVOList, String pk_templet) throws BusinessException{
		BaseDAO dao = new BaseDAO(); 
		QueryTempletVO qtvo = (QueryTempletVO) dao.retrieveByPK(QueryTempletVO.class, new QueryTempletVOMeta(), pk_templet);
		for(UserSystemplateVO userSysVO : userSysVOList){
			userSysVO.setTemplateCode(qtvo.getModelCode());
			userSysVO.setTemplateName(qtvo.getModelName());
		}
	}
	
	private void processPrintTemplet(List<UserSystemplateVO> userSysVOList, String pk_templet) throws BusinessException{
		PrintTemplateVO printVO = getPrintTemplateQry().findByPrimaryKey(pk_templet);
		for(UserSystemplateVO userSysVO : userSysVOList){
			userSysVO.setTemplateCode(printVO.getVtemplatecode());
			userSysVO.setTemplateName(printVO.getVtemplatename());
		}
	}
	
	private void processTemplate(List<UserSystemplateVO> userSysVOList, SystemplateVO sysVO) throws BusinessException{
		int tempstyle = sysVO.getTempstyle().intValue();
		switch (tempstyle) {
		case ITemplateStyle.billTemplate:
			processBillTemplet(userSysVOList, sysVO.getTemplateid());
			break;
		case ITemplateStyle.queryTemplate: 
			processQueryTemplet(userSysVOList, sysVO.getTemplateid());
			break;
		case ITemplateStyle.printTemplate:
			processPrintTemplet(userSysVOList, sysVO.getTemplateid());
			break;
		}
	}
	
	private String createTempTable(List<UserSystemplateVO> result) throws BusinessException{
		String newTempTableName = DBAUtil.createTempTable(TEMP_TABLENAME,getColumns(), null);
		if(CollectionUtils.isEmpty(result)) return newTempTableName;
		
		// 插入数据
        String insersql = "insert into " + newTempTableName + "(pk_corp,pk_user,assigntype,pk_resp,pk_role,tempstyle,fun_name,templatecode,templatename) values (?,?,?,?,?,?,?,?,?)";
        List<SQLParameter> paramList = new ArrayList<SQLParameter>();
        for (UserSystemplateVO userSysVO : result) {
        	SQLParameter param = new SQLParameter();
        	param.addParam(userSysVO.getPk_corp());
        	param.addParam(userSysVO.getPk_user());
        	param.addParam(userSysVO.getAssignType());
        	param.addParam(userSysVO.getPk_resp());
        	param.addParam(userSysVO.getPk_role());
        	param.addParam(userSysVO.getTempstyle());
        	param.addParam(userSysVO.getFun_name());
        	param.addParam(userSysVO.getTemplateCode());
        	param.addParam(userSysVO.getTemplateName());
        	paramList.add(param);
        }
        DBAUtil.execBatchSql(insersql, paramList);   
        return newTempTableName;
	}
	
	private String getColumns() {
		StringBuffer sb = new StringBuffer();
		sb.append(UserSystemplateVO.PK_CORP+ " CHAR(20), ");
		sb.append(UserSystemplateVO.PK_USER+ " CHAR(20), ");
		sb.append(UserSystemplateVO.ASSIGNTYPE + " VARCHAR(50), ");
		sb.append(UserSystemplateVO.PK_RESP + " CHAR(20), ");
		sb.append(UserSystemplateVO.PK_ROLE + " CHAR(20), ");
		sb.append(UserSystemplateVO.TEMPSTYLE + " VARCHAR(50), ");
		sb.append(UserSystemplateVO.FUN_NAME + " VARCHAR(50), ");
		sb.append(UserSystemplateVO.TEMPLATECODE + " VARCHAR(50), ");
		sb.append(UserSystemplateVO.TEMPLATENAME + " VARCHAR(50) ");
		return sb.toString();
	}
	
	private IBillTemplateQry getBillTemplateQry() {
		if (billTempletQry == null) {
			billTempletQry = NCLocator.getInstance().lookup(IBillTemplateQry.class);
		}			
		return billTempletQry;
	}
	
	private IPrintTemplateQry getPrintTemplateQry() {
		if (printTemplateQry == null) {
			printTemplateQry = NCLocator.getInstance().lookup(IPrintTemplateQry.class);
		}			
		return printTemplateQry;
	}
	
	private IRoleManageQuery getRoleManageQuery(){
		if(roleManageQuery == null){
			roleManageQuery = NCLocator.getInstance().lookup(IRoleManageQuery.class);
		}
		return roleManageQuery;
	}
}
