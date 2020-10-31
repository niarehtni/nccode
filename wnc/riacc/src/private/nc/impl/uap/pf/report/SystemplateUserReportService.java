package nc.impl.uap.pf.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.bd.util.DBAUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.itf.uap.pf.report.ISystemplateUserReportService;
import nc.itf.uap.print.IPrintTemplateQry;
import nc.itf.uap.rbac.IRoleManageQuery;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.pub.smart.context.SmartContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.pftemplate.SystemplateVO;
import nc.vo.pub.pftemplate.report.SystemplateUserVO;
import nc.vo.pub.print.PrintTemplateVO;
import nc.vo.pub.query.QueryTempletVO;
import nc.vo.pub.query.QueryTempletVOMeta;
import nc.vo.pub.template.ITemplateStyle;
import nc.vo.sm.UserVO;
import nc.vo.uap.rbac.UserRoleVO;
import nc.vo.util.SqlWrapper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.ufida.report.anareport.base.BaseQueryCondition;

/**
 * 模板分配用户报表查询中语义模型查询
 * @author huangpyc
 * 创建日期：2013-12-6
 */
public class SystemplateUserReportService extends BaseSystemplateReportService implements
		ISystemplateUserReportService {

	public static final String TEMP_TABLENAME = "tmptemplate_sysuser";

	private IBillTemplateQry billTempletQry = null;
	private IPrintTemplateQry printTemplateQry = null;
	private IRoleManageQuery roleManageQuery = null;
	private IUserManageQuery userManageQuery = null;
	Map<String, String> cuserid_pkOrg_map = new HashMap<String, String>();
	/**
	 * 创建临时表返回临时表表名
	 */
	public String getTempName(SmartContext content) throws BusinessException {
		List<SystemplateVO> sysVOList = executeQuery(content);
		
		List<SystemplateUserVO> result = processData(sysVOList);
		
		String newTempTableName = createTempTable(result);
		
		return "select * from " + newTempTableName;
	}

	@SuppressWarnings("unchecked")
	private List<SystemplateVO> executeQuery(SmartContext content) throws BusinessException{
		String sql = "select * from pub_systemplate where isnull(operator,'~') != '~' and pk_corp = {pk_corp?} ";
//		String conditionSQL = getConditionSQL(content);//add by luomin20180323
		String key_iquerycondition = com.ufida.report.anareport.FreeReportContextKey.KEY_IQUERYCONDITION;
		BaseQueryCondition condition = (BaseQueryCondition) content.getAttribute(key_iquerycondition);
		String conditionSQL  = condition.getDescriptors()[0].toString();
		if(!StringUtils.isEmpty(conditionSQL)) {
			sql += " and " + conditionSQL;
		}
		SqlWrapper sw = new SqlWrapper(sql);
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		sw.bind("pk_corp", pk_group);	
		List<SystemplateVO> result = (List<SystemplateVO>) new BaseDAO()
				.executeQuery(sw.getSql(), sw.getSqlParameter(),
						new BeanListProcessor(SystemplateVO.class));
		return result;
	}
	
	private List<SystemplateUserVO> processData(List<SystemplateVO> sysVOList) throws BusinessException{
		if(CollectionUtils.isEmpty(sysVOList)) return null;
		List<SystemplateUserVO> result = new ArrayList<SystemplateUserVO>();
		for(SystemplateVO sysVO : sysVOList){
			List<SystemplateUserVO> sysUserVOList = createSystemplateUserVOs(sysVO);
			if(CollectionUtils.isEmpty(sysUserVOList))
				continue;
			result.addAll(sysUserVOList);
		}
		return result;
	}
	
	private List<SystemplateUserVO> createSystemplateUserVOs(SystemplateVO sysVO) throws BusinessException{
		List<SystemplateUserVO> sysUserVOList = new ArrayList<SystemplateUserVO>();
		processOperator(sysUserVOList, sysVO);
		processTemplate(sysUserVOList, sysVO);
		return sysUserVOList;
	}
	
	private void processOperator(List<SystemplateUserVO> sysUserVOList, SystemplateVO sysVO) throws BusinessException{
		if(sysVO.getOperator_type() == SystemplateVO.OPERATOR_TYPE_USER){
			sysUserVOList.add(getSystemplateUserVOOfUser(sysVO));
		}else if(sysVO.getOperator_type() == SystemplateVO.OPERATOR_TYPE_ROLE){
			UserRoleVO[] userRoleVOs = getRoleManageQuery().queryUserRoleVOByRoleID(new String[]{sysVO.getOperator()});
			if(ArrayUtils.isEmpty(userRoleVOs))
				return;
			for(UserRoleVO userRoleVO : userRoleVOs){
				sysUserVOList.add(getSystemplateUserVOOfUserAndRole(sysVO, userRoleVO));
			}
		}else if(sysVO.getOperator_type() == SystemplateVO.OPERATOR_TYPE_RES){
			List<UserRoleVO> userRoleList = getUserRoleVOsByPKResponsibility(sysVO.getOperator());
			if(CollectionUtils.isEmpty(userRoleList))
				return;
			for(UserRoleVO userRoleVO : userRoleList){
				sysUserVOList.add(getSystemplateUserVOOfAll(sysVO, userRoleVO));
			}
		}
	}
	
	private SystemplateUserVO getSystemplateUserVOOfUser(SystemplateVO sysVO) throws BusinessException{
		SystemplateUserVO sysUserVO = new SystemplateUserVO();
		sysUserVO.setPk_corp(sysVO.getPk_corp());
		sysUserVO.setModuleid(sysVO.getModuleid());
		sysUserVO.setFun_name(getFun_Name(sysVO.getFunnode()));
		sysUserVO.setTempstyle(getTempStyle(sysVO.getTempstyle().intValue()));
		sysUserVO.setAssignType(getAssignType(sysVO.getOperator_type()));
		sysUserVO.setPk_user(sysVO.getOperator());
		sysUserVO.setPk_org1(getPkOrgOfUser(sysVO.getOperator()));
		//add by luomin
//		sysUserVO.setTempstylename(getTempStyleName(sysVO.getTempstyle().intValue()));
		return sysUserVO;
	}
	
	private SystemplateUserVO getSystemplateUserVOOfUserAndRole(SystemplateVO sysVO, UserRoleVO userRoleVO) throws BusinessException{
		SystemplateUserVO sysUserVO = new SystemplateUserVO();
		sysUserVO.setPk_corp(sysVO.getPk_corp());
		sysUserVO.setModuleid(sysVO.getModuleid());
		sysUserVO.setFun_name(getFun_Name(sysVO.getFunnode()));
		sysUserVO.setTempstyle(getTempStyle(sysVO.getTempstyle().intValue()));
		sysUserVO.setAssignType(getAssignType(sysVO.getOperator_type()));
		sysUserVO.setPk_user(userRoleVO.getCuserid());
		sysUserVO.setPk_org1(getPkOrgOfUser(userRoleVO.getCuserid()));
		sysUserVO.setPk_role(sysVO.getOperator());
		//add by luomin
//		sysUserVO.setTempstylename(getTempStyleName(sysVO.getTempstyle().intValue()));
		return sysUserVO;
	}
	
	private SystemplateUserVO getSystemplateUserVOOfAll(SystemplateVO sysVO, UserRoleVO userRoleVO) throws BusinessException{
		SystemplateUserVO sysUserVO = new SystemplateUserVO();
		sysUserVO.setPk_corp(sysVO.getPk_corp());
		sysUserVO.setModuleid(sysVO.getModuleid());
		sysUserVO.setFun_name(getFun_Name(sysVO.getFunnode()));
		sysUserVO.setTempstyle(getTempStyle(sysVO.getTempstyle().intValue()));
		sysUserVO.setAssignType(getAssignType(sysVO.getOperator_type()));
		sysUserVO.setPk_resp(sysVO.getOperator());
		sysUserVO.setPk_user(userRoleVO.getCuserid());
		sysUserVO.setPk_org1(getPkOrgOfUser(userRoleVO.getCuserid()));
		if(!userRoleVO.getCuserid().equals(userRoleVO.getPk_role())){
			sysUserVO.setPk_role(userRoleVO.getPk_role());
		}
		//add by luomin
//		sysUserVO.setTempstylename(getTempStyleName(sysVO.getTempstyle().intValue()));
		return sysUserVO;
	}
	
	private String getPkOrgOfUser(String cuserid) throws BusinessException{
		if(cuserid_pkOrg_map.get(cuserid) == null){
			UserVO vo = getUserManageQuery().getUser(cuserid);
			cuserid_pkOrg_map.put(vo.getCuserid(), vo.getPk_org());
		}
		return cuserid_pkOrg_map.get(cuserid);
	}
	
	private void processTemplate(List<SystemplateUserVO> sysUserVOList, SystemplateVO sysVO) throws BusinessException{
		int tempstyle = sysVO.getTempstyle().intValue();
		switch (tempstyle) {
		case ITemplateStyle.billTemplate:
			processBillTemplet(sysUserVOList, sysVO.getTemplateid());
			break;
		case ITemplateStyle.queryTemplate: 
			processQueryTemplet(sysUserVOList, sysVO.getTemplateid());
			break;
		case ITemplateStyle.printTemplate:
			processPrintTemplet(sysUserVOList, sysVO.getTemplateid());
			break;
		}
	}
	
	private void processBillTemplet(List<SystemplateUserVO> sysUserVOList, String pk_templet) throws BusinessException{
		BillTempletHeadVO[] billHeadVOs = getBillTemplateQry().findTemplet("pk_billtemplet = '" + pk_templet + "'");
		if(ArrayUtils.isEmpty(billHeadVOs)){
			return;
		}
		BillTempletHeadVO billHeadVO = billHeadVOs[0];
		for(SystemplateUserVO sysUserVO : sysUserVOList){
			sysUserVO.setTemplateid(pk_templet);
			sysUserVO.setTemplateCode(billHeadVO.getPkBillTypeCode());
			sysUserVO.setTemplateName(billHeadVO.getBillTempletName());
			sysUserVO.setPk_org(billHeadVO.getPk_org() == null ? billHeadVO.getPkCorp() : billHeadVO.getPk_org());
		}
	}
	
	private void processQueryTemplet(List<SystemplateUserVO> sysUserVOList, String pk_templet) throws BusinessException{
		BaseDAO dao = new BaseDAO(); 
		QueryTempletVO qtvo = (QueryTempletVO) dao.retrieveByPK(QueryTempletVO.class, new QueryTempletVOMeta(), pk_templet);
		for(SystemplateUserVO sysUserVO : sysUserVOList){
			sysUserVO.setTemplateid(pk_templet);
			sysUserVO.setTemplateCode(qtvo.getModelCode());
			sysUserVO.setTemplateName(qtvo.getModelName());
			sysUserVO.setPk_org(qtvo.getPkOrg() == null ? qtvo.getPkCorp() : qtvo.getPkOrg());
		}
	}
	
	private void processPrintTemplet(List<SystemplateUserVO> sysUserVOList, String pk_templet) throws BusinessException{
		PrintTemplateVO printVO = getPrintTemplateQry().findByPrimaryKey(pk_templet);
		for(SystemplateUserVO sysUserVO : sysUserVOList){
			sysUserVO.setTemplateid(pk_templet);
			sysUserVO.setTemplateCode(printVO.getVtemplatecode());
			sysUserVO.setTemplateName(printVO.getVtemplatename());
			sysUserVO.setPk_org(printVO.getPk_org() == null ? printVO.getPk_corp() : printVO.getPk_org());
		}
	}
	
	private String createTempTable(List<SystemplateUserVO> result) throws BusinessException{
		String newTempTableName = DBAUtil.createTempTable(TEMP_TABLENAME,getColumns(), null);
		if(CollectionUtils.isEmpty(result)) return newTempTableName;
		
		// 插入数据
		//update by luomin 添加一个tempstylename模板类型名称字段
        String insersql = "insert into " + newTempTableName + "(pk_corp,pk_org,moduleid,fun_name,tempstyle,templateid,templatecode,templatename,assigntype,pk_user,pk_role,pk_resp,pk_org1) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        List<SQLParameter> paramList = new ArrayList<SQLParameter>();
        for (SystemplateUserVO sysUserVO : result) {
        	SQLParameter param = new SQLParameter();
        	param.addParam(sysUserVO.getPk_corp());
        	param.addParam(sysUserVO.getPk_org());
        	param.addParam(sysUserVO.getModuleid());
        	param.addParam(sysUserVO.getFun_name());
        	param.addParam(sysUserVO.getTempstyle());
        	param.addParam(sysUserVO.getTemplateid());
        	param.addParam(sysUserVO.getTemplateCode());
        	param.addParam(sysUserVO.getTemplateName());
        	param.addParam(sysUserVO.getAssignType());
        	param.addParam(sysUserVO.getPk_user());
        	param.addParam(sysUserVO.getPk_role());
        	param.addParam(sysUserVO.getPk_resp());
        	param.addParam(sysUserVO.getPk_org1());
//        	param.addParam(sysUserVO.getTempstylename());
        	paramList.add(param);
        }
        DBAUtil.execBatchSql(insersql, paramList);   
        return newTempTableName;
	}
	
	private String getColumns() {
		StringBuffer sb = new StringBuffer();
		sb.append(SystemplateUserVO.PK_CORP + " CHAR(20), ");
		sb.append(SystemplateUserVO.PK_ORG + " CHAR(20), ");
		sb.append(SystemplateUserVO.MODULEID + " VARCHAR(50), ");
		sb.append(SystemplateUserVO.FUN_NAME + " VARCHAR(50), ");
		sb.append(SystemplateUserVO.TEMPSTYLE + " VARCHAR(50), ");
		sb.append(SystemplateUserVO.TEMPLATEID + " CHAR(20), ");
		sb.append(SystemplateUserVO.TEMPLATECODE + " VARCHAR(50), ");
		sb.append(SystemplateUserVO.TEMPLATENAME + " VARCHAR(50), ");
		sb.append(SystemplateUserVO.ASSIGNTYPE + " VARCHAR(50), ");
		sb.append(SystemplateUserVO.PK_USER+ " CHAR(20), ");
		sb.append(SystemplateUserVO.PK_ROLE + " CHAR(20), ");
		sb.append(SystemplateUserVO.PK_RESP + " CHAR(20), ");
		sb.append(SystemplateUserVO.PK_ORG1 + " CHAR(20) ");
//		sb.append(SystemplateUserVO.TEMPSTYLENAME + " VARCHAR(50) ");//update by luomin
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
	
	private IUserManageQuery getUserManageQuery(){
		if(userManageQuery == null){
			 userManageQuery = NCLocator.getInstance().lookup(IUserManageQuery.class);
		}
		return userManageQuery;
	}
}
