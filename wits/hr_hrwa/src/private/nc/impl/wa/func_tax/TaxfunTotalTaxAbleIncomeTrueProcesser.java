package nc.impl.wa.func_tax;

import nc.bs.dao.BaseDAO;
import nc.impl.wa.func.AbstractWAFormulaParse;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.hr.func.FunctionVO;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.formula.IFormulaAli;
import nc.vo.wa.pub.WaLoginContext;

/**
 * Get Sum(value)
 * �Ľ�����ۼ�Ӧ��˰���ö��㷨
 * zhaochxs ��ԭ���Ķ�Ӧ��˰���ö�ĺϼƸ�Ϊ�ϼƣ����ο�˰����-�ѿ�˰������-�ۼ�ר��۳���ȥ���м���ķ�������
 * 
 * 
 * ��˰��_�ۼ�Ӧ��˰���ö�
sum(itemkey) where cyearperiod in (taxyear��period���� and pk_org and pk_psndoc and pk_waclass in (�ϲ���˰ �ٳ� �������� and CHECKFLAG = 'Y'
union
sum(itemkey) where cyearperiod in (taxyear(period) and pk_org and pk_psndoc and pk_waclass  = ������
union
sum(itemkey) where cyearperiod in (taxyear��period���� and pk_org and pk_psndoc and pk_waclass in (�ϲ���˰�� and CHECKFLAG = 'Y' and cyearperiod < ��ǰ��н�ڼ�
 * 
 * sum()
 * 1����ǰ�ڼ䵱ǰн�����
 * 2����η�н��ǰ�ڼ��ٳ���ǰн�����
 * 3���ϲ���˰��ǰ�ڼ��ٳ���ǰн���˱�
 * 4����ʷн���ڼ�
 * 
 * @author: xuhw
 * @date:
 * @since: eHR V6.5
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class TaxfunTotalTaxAbleIncomeTrueProcesser extends AbstractWAFormulaParse
		implements IFormulaAli {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2999834882617313191L;


	/**
	 * @throws BusinessException
	 * @see nc.impl.wa.func.TaxfunTotalTaxAbleIncomeProcesser#getReplaceStr(java.lang.String)
	 */
	@Override
	public FunctionReplaceVO getReplaceStr(String formula)
			throws BusinessException {
		WaLoginContext context = getContext();
		String cyear = context.getCyear();
		String cperiod = context.getCperiod();
		cperiod = context.getCperiod();
		StringBuffer sbsql = new StringBuffer();
		String[] arguments = getArguments(formula);
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String itemkey1 = arguments[0];
		String itemkey2 = arguments[1];
		fvo.setAliTableName("wa_data");
		StringBuffer sqlBuffer = new StringBuffer();

		// -- ͳһԱ�� ͬһ��������Դ��֯ �ϲ���˰���� ��˹������ݵĺϼ�
		sqlBuffer.append(" (SELECT ");
//		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
//			//zhaochxs ��ԭ����Ӧ��˰���ö��Ϊ���ο�˰����-�ѿ�˰������ȥ���м���ķ�������
//			sqlBuffer.append(" 	isnull(SUM(WADATA." + itemkey1 + "-WADATA.f_6)-max(case when wadata.cyear||wadata.cperiod = wa_data.cyear||wa_data.cperiod then WADATA." + itemkey2 + " else 0 end), 0) sumamt ");
//		}else {
//			sqlBuffer.append(" 	nvl(SUM(WADATA." + itemkey1 + "-WADATA.f_6)-max(case when wadata.cyear||wadata.cperiod = wa_data.cyear||wa_data.cperiod then WADATA." + itemkey2 + " else 0 end), 0) sumamt ");
//		}
		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
			//zhaochxs ��ԭ����Ӧ��˰���ö��Ϊ���ο�˰����-�ѿ�˰������ȥ���м���ķ�������
			sqlBuffer.append(" 	isnull(SUM(WADATA." + itemkey1 + "-WADATA.f_6)-max(case when wadata.cyear||wadata.cperiod = '"+ cyear + cperiod +"' then WADATA." + itemkey2 + " else 0 end), 0) sumamt ");
		}else {
			sqlBuffer.append(" 	nvl(SUM(WADATA." + itemkey1 + "-WADATA.f_6)-max(case when wadata.cyear||wadata.cperiod = '"+ cyear + cperiod +"' then WADATA." + itemkey2 + " else 0 end), 0) sumamt ");
		}
		sqlBuffer.append(" from  wa_data wadata  ");
		//���˵�����н����𷽰�,���¼�˰����
		sqlBuffer.append(" 	inner join wa_waclass waclass on wadata.pk_wa_class = waclass.pk_wa_class and waclass.collectflag = 'N'	and coalesce(waclass.yearbonusflag,'N') = 'N' ");			
		sqlBuffer.append(" WHERE          ");
		sqlBuffer.append(" 	WADATA.PK_PSNDOC = wa_data.pk_psndoc           ");
		//zhaocxhs ������˰��֯����֯��ϵ���ۼƱ�־�����ۼ�
		//old	sqlBuffer.append(" AND wadata.pk_org = wa_data.pk_org        ");
		sqlBuffer.append(" AND wadata.taxorg = wa_data.taxorg        ");
		sqlBuffer.append(" AND wadata.taxsumuid = wa_data.taxsumuid        ");
		sqlBuffer.append(" AND wadata.pk_psnorg = wa_data.pk_psnorg        ");
		sqlBuffer.append(" AND (       ");
		//zhaochxs��ǰ�ڼ�ĺϲ���˰�������������
		sqlBuffer.append("  ( WADATA.cyear  " + getBDSql() + " WADATA.cperiod = "+cyear+cperiod+" and  ( (  WADATA.PK_WA_CLASS IN ( SELECT PK_WACLASS FROM WA_TAXGRPMEMBER WHERE pk_taxgroup =      ");
		sqlBuffer.append(" 		( SELECT PK_TAXGROUP FROM WA_TAXGRPMEMBER WHERE pk_waclass = '" + context.getPk_prnt_class() + "' or pk_waclass = '" + context.getPk_wa_class() + "'  )  )     "); 
		sqlBuffer.append(" 		AND  WADATA.CHECKFLAG = 'Y'    ) ");
		//��ǰ�ڼ䵱ǰ����
		sqlBuffer.append(" 		OR  WADATA.PK_WA_CLASS = '" + context.getPk_wa_class() + "' ");
		//��ǰ�ڼ�ϲ���˰������˵Ķ�η�н
		sqlBuffer.append(" 		OR (WADATA.pk_wa_class IN ( SELECT PK_CHILDCLASS FROM WA_INLUDECLASS WHERE PK_PARENTCLASS IN      ");
		sqlBuffer.append(" 	( SELECT PK_WACLASS FROM WA_TAXGRPMEMBER WHERE pk_taxgroup =      ");
		sqlBuffer.append(" 		( SELECT PK_TAXGROUP FROM WA_TAXGRPMEMBER WHERE pk_waclass = '" + context.getPk_prnt_class() + "' or pk_waclass = '" + context.getPk_wa_class() + "' ) )  ");
		sqlBuffer.append("   or PK_PARENTCLASS='"+context.getPk_prnt_class()+"' ) ");
		sqlBuffer.append("  and WADATA.CHECKFLAG = 'Y')   ) )    ");
		//����˵���ʷ�ڼ�
		sqlBuffer.append(" 		OR (  WADATA.cyear  " + getBDSql() + " WADATA.cperiod < "+cyear+cperiod+") and  WADATA.CHECKFLAG = 'Y' )     ");
		
		
		
		
		sqlBuffer.append(" AND WADATA.cyear  " + getBDSql() + " WADATA.cperiod IN (      ");
		sqlBuffer.append(" 	SELECT        ");
		//zhaochxs sqlserver2008 ��֧��CONCAT���������жϣ��ײ��Զ�ת��
		sqlBuffer.append(" 		cyear || cperiod     ");
		sqlBuffer.append(" 	FROM          ");
		sqlBuffer.append(" 		wa_period ");
		sqlBuffer.append(" 	WHERE         ");
		sqlBuffer.append(" 		PK_PERIODSCHEME = (  ");
		sqlBuffer.append(" 			SELECT");
		sqlBuffer.append(" 				PK_PERIODSCHEME      ");
		sqlBuffer.append(" 			FROM  ");
		sqlBuffer.append(" 				wa_waclass   ");
		sqlBuffer.append(" 			WHERE ");
		sqlBuffer.append(" 				PK_WA_CLASS = '" + context.getPk_wa_class() + "'     ");
		sqlBuffer.append(" 		)         ");
		sqlBuffer.append(" 	AND TAXYEAR = (          ");
		sqlBuffer.append(" 		SELECT    ");
		sqlBuffer.append(" 			period.TAXYEAR   ");
		sqlBuffer.append(" 		FROM      ");
		sqlBuffer.append(" 			wa_waclass waclass       ");
		sqlBuffer.append(" 		INNER JOIN wa_period period ON period.PK_PERIODSCHEME = WACLASS.PK_PERIODSCHEME         ");
		sqlBuffer.append(" 		WHERE     ");
		sqlBuffer.append(" 			WACLASS.PK_WA_CLASS = '" + context.getPk_wa_class() + "' ");
		sqlBuffer.append(" 		AND PERIOD.CYEAR = WACLASS.CYEAR ");
		sqlBuffer.append(" 		AND PERIOD.CPERIOD = WACLASS.CPERIOD ");
		sqlBuffer.append(" 	)             ");
		sqlBuffer.append(" )) ");
		
		fvo.setReplaceStr(coalesce(sqlBuffer.toString()));

		return fvo;
	}

	@Override
	public String[] getAliItemKeys(WaClassItemVO itemVO,
			WaLoginContext context, FunctionVO functionVO)
			throws BusinessException {
		if (itemVO == null || context == null || functionVO == null
				|| itemVO.getVformula() == null) {
			return null;
		}

		setFunctionVO(functionVO);
		setContext(context);

		String[] arguments = getArguments(itemVO.getVformula());

		return arguments;
	}
	

	private String getBDSql() {
		if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
			return "+";
		} else {
			return "||";
		}
	}
}
