package nc.impl.pub.ace;

import nc.bs.twhr.twhr_declaration.ace.bp.AceTwhr_declarationInsertBP;
import nc.bs.twhr.twhr_declaration.ace.bp.AceTwhr_declarationUpdateBP;
import nc.hr.utils.InSQLCreator;
import nc.bs.twhr.twhr_declaration.ace.bp.AceTwhr_declarationDeleteBP;
import nc.bs.twhr.twhr_declaration.ace.bp.AceTwhr_declarationSendApproveBP;
import nc.bs.twhr.twhr_declaration.ace.bp.AceTwhr_declarationUnSendApproveBP;
import nc.vo.pubapp.query2.sql.process.QueryCondition;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.twhr.twhr_declaration.ace.bp.AceTwhr_declarationApproveBP;
import nc.bs.twhr.twhr_declaration.ace.bp.AceTwhr_declarationUnApproveBP;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.vo.twhr.twhr_declaration.BusinessBVO;
import nc.vo.twhr.twhr_declaration.CompanyBVO;
import nc.vo.twhr.twhr_declaration.DeclarationHVO;
import nc.vo.twhr.twhr_declaration.NonPartTimeBVO;
import nc.vo.twhr.twhr_declaration.PartTimeBVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public abstract class AceTwhr_declarationPubServiceImpl {
	private BaseDAO dao = null;
	
	
	// ����
	public AggDeclarationVO[] pubinsertBills(AggDeclarationVO[] clientFullVOs,
			AggDeclarationVO[] originBills) throws BusinessException {
		//����֮ǰ��ɾ�����е������Է�������������ͻ Ares.Tank 2018-10-26 17:23:49
		//ע:�˽ڵ���������������û���κ�ʵ�������,��������Ҫ�����������ĸ��ӱ����ɾۺ�VO
		String sqlStr = "delete from twhr_declaration ";
		getDao().executeUpdate(sqlStr);
		
		
		try {
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<AggDeclarationVO> transferTool = new BillTransferTool<AggDeclarationVO>(
					clientFullVOs);
			// ����BP
			AceTwhr_declarationInsertBP action = new AceTwhr_declarationInsertBP();
			AggDeclarationVO[] retvos = action.insert(clientFullVOs);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ��
	public void pubdeleteBills(AggDeclarationVO[] clientFullVOs,
			AggDeclarationVO[] originBills) throws BusinessException {
		try {
			// ����BP
			new AceTwhr_declarationDeleteBP().delete(clientFullVOs);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	public AggDeclarationVO[] pubupdateBills(AggDeclarationVO[] clientFullVOs,
			AggDeclarationVO[] originBills) throws BusinessException {
		try {
			// ���� + ���ts
			BillTransferTool<AggDeclarationVO> transferTool = new BillTransferTool<AggDeclarationVO>(
					clientFullVOs);
			AceTwhr_declarationUpdateBP bp = new AceTwhr_declarationUpdateBP();
			AggDeclarationVO[] retvos = bp.update(clientFullVOs, originBills);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	/**
	 * ��ǰ�˵��������оۺϲ�ѯ,�����Ҫ�����ӽṹ
	 * @Date 2018-10-4 11:53:22
	 * @param queryScheme
	 * @return
	 * @throws BusinessException
	 * @author Ares.Tank 
	 * @date 2018��10��4�� ����12:07:17
	 * @description
	 */
	@SuppressWarnings("unchecked")
	public AggDeclarationVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggDeclarationVO[] bills = null;
		try {
			Map<String, QueryCondition> conds = (Map<String, QueryCondition>) queryScheme.get("all_condition");
			Set<String> legal_org_tempSet = new HashSet<>();
			Set<String> pk_psndoc_tempSet = new HashSet<>();
			Set<String> pk_waperiod_tempSet = new HashSet<>();
			Set<String> pk_dept_tempSet = new HashSet<>();
			String pk_hrorg_temp = null;
			
			//String maxdate = "";
			if (conds != null && conds.size() > 0) {
				for (Entry<String, QueryCondition> cond : conds.entrySet()) {
					String strKey = cond.getKey();
					QueryCondition condition = cond.getValue();
					if (strKey.equals("legal_org_temp")) {
						if (condition.getValues() != null && condition.getValues().length > 0) {
							for(String s : condition.getValues()){
								legal_org_tempSet.add(s);
							}
						}
					} else if (strKey.equals("pk_psndoc_temp")) {
						if (condition.getValues() != null && condition.getValues().length > 0) {
							for(String s : condition.getValues()){
								pk_psndoc_tempSet.add(s);
							}
						}
					} else if (strKey.equals("pk_waperiod_temp")) {
						if (condition.getValues() != null && condition.getValues().length > 0) {
							if (condition.getValues() != null && condition.getValues().length > 0) {
								for(String s : condition.getValues()){
									pk_waperiod_tempSet.add(s);
								}
							}
						}
					} else if (strKey.equals("pk_dept_temp")) {
						if (condition.getValues() != null && condition.getValues().length > 0) {
							if (condition.getValues() != null && condition.getValues().length > 0) {
								for(String s : condition.getValues()){
									pk_dept_tempSet.add(s);
								}
							}
						}
					} else if (strKey.equals("pk_hrorg_temp")) {
						if (condition.getValues() != null && condition.getValues().length > 0) {
							pk_hrorg_temp = condition.getValues()[0];
						}
					} 
				}

				bills = queryBillsByConditions(legal_org_tempSet, pk_psndoc_tempSet, pk_waperiod_tempSet, pk_dept_tempSet, pk_hrorg_temp);
			}
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}

		return bills;
	}

	

	@SuppressWarnings("unchecked")
	private AggDeclarationVO[] queryBillsByConditions(Set<String> legal_org_tempSet, Set<String> pk_psndoc_tempSet,
			Set<String> pk_waperiod_temp, Set<String> pk_dept_temp, String pk_hrorg_temp) throws BusinessException {
		AggDeclarationVO  resultVO = new AggDeclarationVO();
		DeclarationHVO parentVO = new DeclarationHVO();
		parentVO.setTs(new UFDateTime());
		parentVO.setApprovestatus(0);
		resultVO.setParent(parentVO);
		//ƴװ��VO
		//����where ���
		String whereSql = " where 11=11 ";
		InSQLCreator insql = new InSQLCreator();
		
		if(null != legal_org_tempSet && legal_org_tempSet.size() > 0){
			String legalOrgStr = insql.getInSQL(legal_org_tempSet.toArray(new String[0]));
			whereSql += " and vbdef1 in ( " + legalOrgStr + " ) ";
		}else{
			//���������֯δѡ,��ѡ��������Դ��֯�µ����з�����֯
			Set<String> pk_legals = LegalOrgUtilsEX.getOrgsByLegal(pk_hrorg_temp);
			if(null != pk_legals && pk_legals.size() > 0){
				String legalOrgStr = insql.getInSQL(pk_legals.toArray(new String[0]));
				whereSql += " and vbdef1 in ( " + legalOrgStr + " ) ";
			}
			
			
		}
		
		
		if(null != pk_dept_temp && pk_dept_temp.size() > 0){
			String deptStr = insql.getInSQL(pk_dept_temp.toArray(new String[0]));
			whereSql += " and pk_dept in ( " + deptStr + " ) ";
		}
		
		if(null != pk_waperiod_temp && pk_waperiod_temp.size() > 0){
			//String waperiodStr = insql.getInSQL(pk_waperiod_temp.toArray(new String[0]));
			//whereSql += " and vbdef3 in ( " +  + " ) ";
			boolean isFirstFlag = true;
			whereSql += " and (";
			for(String s : pk_waperiod_temp){
				if(null != s){
					if(isFirstFlag){
						whereSql += " ( pay_date >= '" + dealDate(s,0) + "' and pay_date < '" + dealDate(s,1)+"') ";
						isFirstFlag = false;
					}else{
						whereSql += " or ( pay_date >= '" + dealDate(s,0) + "' and pay_date < '" + dealDate(s,1)+"') "; 
					}
				}
			}
			whereSql += " ) ";
		}
		
		if(null != pk_psndoc_tempSet && pk_psndoc_tempSet.size() > 0){
			String psnStr = insql.getInSQL(pk_psndoc_tempSet.toArray(new String[0]));
			whereSql += " and  vbdef4 in ( " + psnStr + " ) ";
		}
		whereSql += " and dr = 0 ";
		//��ѯ�ӱ�
		String sqlStr = " select db.* from declaration_business db " + whereSql;
		List<BusinessBVO> businessBVOList 
			= (List<BusinessBVO>) (getDao().executeQuery(sqlStr, new BeanListProcessor(BusinessBVO.class)));
		if(null != businessBVOList && businessBVOList.size() > 0){
			resultVO.setChildren(BusinessBVO.class, businessBVOList.toArray(new BusinessBVO[0]));
		}
		
		sqlStr = " select dc.* from declaration_company dc " + whereSql;
		List<CompanyBVO> companyBVOList 
			= (List<CompanyBVO>) (getDao().executeQuery(sqlStr, new BeanListProcessor(CompanyBVO.class)));
		if(null != companyBVOList && companyBVOList.size() > 0){
			resultVO.setChildren(CompanyBVO.class, companyBVOList.toArray(new CompanyBVO[0]));
		}
		
		sqlStr = " select dn.* from declaration_nonparttime dn " + whereSql;
		List<NonPartTimeBVO> nonpartTimeBVOList 
			= (List<NonPartTimeBVO>) (getDao().executeQuery(sqlStr, new BeanListProcessor(NonPartTimeBVO.class)));
		if(null != nonpartTimeBVOList && nonpartTimeBVOList.size() > 0){
			resultVO.setChildren(NonPartTimeBVO.class, nonpartTimeBVOList.toArray(new NonPartTimeBVO[0]));
		}
		
		sqlStr = " select dp.* from declaration_parttime dp " + whereSql;
		List<PartTimeBVO> partTimeBVOList 
			= (List<PartTimeBVO>) (getDao().executeQuery(sqlStr, new BeanListProcessor(PartTimeBVO.class)));
		if(null != partTimeBVOList && partTimeBVOList.size() > 0){
			resultVO.setChildren(PartTimeBVO.class, partTimeBVOList.toArray(new PartTimeBVO[0]));
		}
		//String sqlStr = " select * form ";
		if((partTimeBVOList != null && partTimeBVOList.size() >0) ||
				(nonpartTimeBVOList != null && nonpartTimeBVOList.size() > 0) ||
				(companyBVOList != null && companyBVOList.size() > 0) ||
				(businessBVOList != null && businessBVOList.size() > 0)){
			//��һ��������,�򷵻�
			AggDeclarationVO[] results = {resultVO};
			return results;
		}
		
		
		return new AggDeclarationVO[0];
	}

	/**
	 * �·��ַ������� , �µĿ�ʼʱ����µĽ���ʱ��
	 * @param s
	 * @param i
	 * @return
	 * @author Ares.Tank 
	 * @date 2018��10��4�� ����8:44:51
	 * @description
	 */
	private UFDate dealDate(String dateStr, int i) {
		UFDate sDate = new UFDate(dateStr + "-01 00:00:00");
		if(i == 0){
			return sDate;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sDate.toDate());
		calendar.set(Calendar.DAY_OF_MONTH,1);
		calendar.add(Calendar.MONTH, 1);
		
		return new UFDate(calendar.getTime());
	}

	/**
	 * ������ʵ�֣���ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// ��ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
		
	}

	// �ύ
	public AggDeclarationVO[] pubsendapprovebills(
			AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException {
		AceTwhr_declarationSendApproveBP bp = new AceTwhr_declarationSendApproveBP();
		AggDeclarationVO[] retvos = bp.sendApprove(clientFullVOs, originBills);
		return retvos;
	}

	// �ջ�
	public AggDeclarationVO[] pubunsendapprovebills(
			AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills)
			throws BusinessException {
		AceTwhr_declarationUnSendApproveBP bp = new AceTwhr_declarationUnSendApproveBP();
		AggDeclarationVO[] retvos = bp.unSend(clientFullVOs, originBills);
		return retvos;
	};

	// ����
	public AggDeclarationVO[] pubapprovebills(AggDeclarationVO[] clientFullVOs,
			AggDeclarationVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceTwhr_declarationApproveBP bp = new AceTwhr_declarationApproveBP();
		AggDeclarationVO[] retvos = bp.approve(clientFullVOs, originBills);
		return retvos;
	}

	// ����

	public AggDeclarationVO[] pubunapprovebills(AggDeclarationVO[] clientFullVOs,
			AggDeclarationVO[] originBills) throws BusinessException {
		for (int i = 0; clientFullVOs != null && i < clientFullVOs.length; i++) {
			clientFullVOs[i].getParentVO().setStatus(VOStatus.UPDATED);
		}
		AceTwhr_declarationUnApproveBP bp = new AceTwhr_declarationUnApproveBP();
		AggDeclarationVO[] retvos = bp.unApprove(clientFullVOs, originBills);
		return retvos;
	}

	public BaseDAO getDao() {
		if(null == dao){
			dao = new BaseDAO();
		}
		return dao;
	}

	public void setDao(BaseDAO dao) {
		this.dao = dao;
	}

}