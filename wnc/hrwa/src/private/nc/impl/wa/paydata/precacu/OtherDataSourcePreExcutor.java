package nc.impl.wa.paydata.precacu;

import nc.bs.logging.Logger;
import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.vo.dataitem.pub.DataVOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.othersource.OtherSourceVO;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginContext;


/**
 * @author daicy
 *
 */
public class OtherDataSourcePreExcutor extends AbstractFormulaExecutor {
	
	// ���Ӻ����
	//���뵱ǰ��½�û����û�pk������ǰ����н�����н�����pk������ǰ��½��˾����˾pk��
	//��ǰ����н����������ڼ䣨н���ڼ俪ʼ���ڣ�н���ڼ�������ڣ�н���ڼ���ȣ�н���ڼ��·ݣ�
	/**
	 * �滻����Դ����ĺ����
	 */
	private String getExchangeSql(WaLoginContext context, String strSql)
	{
		String userid = context.getPk_loginUser();
		String corpid = context.getPk_org();
		PeriodStateVO pvo = context.getWaLoginVO().getPeriodVO();
        String enddate = context.getWaLoginVO().getPeriodVO().getCenddate().toString();
		
		String startdate = context.getWaLoginVO().getPeriodVO().getCstartdate().toString();
		String cyear = pvo.getCyear();
		String cperiod = pvo.getCperiod();
		String waClass = context.getPk_wa_class();
		if(strSql.indexOf("#pk_user#") != -1)
		{
			strSql = strSql.replace("#pk_user#", "'"+userid+"'");
		}
		if(strSql.indexOf("#pk_org#")!= -1)
		{
			strSql = strSql.replace("#pk_org#", "'"+corpid+"'");
		}
		if(strSql.indexOf("#startdate#")!= -1)
		{
			strSql = strSql.replace("#startdate#", "'"+startdate+"'");
		}
		if(strSql.indexOf("#enddate#")!= -1)
		{
			strSql = strSql.replace("#enddate#", "'"+enddate+"'");
		}
		if(strSql.indexOf("#cyear#")!= -1)
		{
			strSql = strSql.replace("#cyear#", "'"+cyear+"'");
		}
		if(strSql.indexOf("#cperiod#")!= -1)
		{
			strSql = strSql.replace("#cperiod#", "'"+cperiod+"'");
		}
		if(strSql.indexOf("#waclassid#")!= -1)
		{
			strSql = strSql.replace("#waclassid#", "'"+waClass+"'");
		}
		
		
		return strSql;
	}

	/** 
	 * @author zhangg on 2010-5-14 
	 * @see nc.vo.wa.paydata.IFormula#excute(java.lang.Object, nc.vo.wa.pub.WaLoginContext)
	 */
	@Override
	public void excute(Object inarguments, WaLoginContext context) throws BusinessException {

		String pk_wa_otherdata = (String) inarguments;
		OtherSourceVO otherSourceVO = (OtherSourceVO) getBaseDao().executeQuery("select * from wa_otherdatasource where pk_otherdatasource='"
		    +pk_wa_otherdata+"'", new BeanProcessor(OtherSourceVO.class));
		String strSql = otherSourceVO.getFormulastr();
		strSql = this.getExchangeSql(context, strSql);
		Object object = context.getInitData();
		
		String coloumn = "char_value";
		if (object != null && object instanceof WaClassItemVO) {
			WaClassItemVO itemVO = (WaClassItemVO) object;
			coloumn =  DataVOUtils.isDigitsAttribute(itemVO.getItemkey())? "cacu_value" : "char_value";

		}
		
		
		strSql = "update wa_cacu_data set "+coloumn+" = (select dvalue from (" +
         		strSql +") temptable where temptable.pk_psndoc=wa_cacu_data.pk_psndoc and temptable.cyear='"+context.getCyear()+"' and temptable.cperiod = '"+context.getCperiod()+"')  " +
         				" where  pk_wa_class = '" + context.getPk_wa_class() + "' and creator = '" + context.getPk_loginUser()+"'"; 
         getBaseDao().executeUpdate(strSql);
	}
}
