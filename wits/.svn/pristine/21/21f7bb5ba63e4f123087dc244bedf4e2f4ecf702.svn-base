package nc.impl.wa.paydata.precacu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.impl.wa.util.LocalizationSysinitUtil;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.WaLoginContext;
/**
 * 
 * @author ward 
 * @date 2018-01-18
 * @功能描述 外籍员工扣税方式计算函数
 *
 */
public class TaxFormulaPreExcutor4TWForeign extends AbstractFormulaExecutor{

	@Override
	public void excute(Object paramObject, WaLoginContext context)
			throws BusinessException {
		//查询出税率表为外籍员工税率表的所有员工
		String qryPsndocPK="SELECT\n" +
				"	pk_psndoc\n" +
				"FROM\n" +
				"	wa_cacu_data\n" +
				"LEFT JOIN wa_taxbase ON wa_cacu_data.taxtableid = wa_taxbase.pk_wa_taxbase\n" +
				"WHERE\n" +
				"	wa_taxbase.itbltype = '3'";
		List<String> pk_psndocs=(ArrayList<String>) getDao().executeQuery(qryPsndocPK, new ColumnListProcessor());
		if(pk_psndocs==null||pk_psndocs.size()<1){
			return;
		}
		String twhr08=getSysinit(context.getPk_org(), "TWHR08");
		UFDouble expireNum=getBaseDocUFDoubleValue(context.getPk_org(), "TWSP0013");
		//根据人员主键查询出所有员工信息
		List<PsndocVO> psnList=getListPsndocVO(pk_psndocs.toArray(new String[0]));
		List<String> forePsndocs=new ArrayList<String>();//外籍员工扣税
		for (int i = 0; i < psnList.size(); i++) {
			PsndocVO psnVo=psnList.get(i);
			String isResident=psnVo.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN02"))!=null?psnVo.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN02")).toString():"N";
			if("N".equals(isResident)&&!isExpire(twhr08, expireNum, psnVo, context)){
				forePsndocs.add(psnVo.getPk_psndoc());
			}
		}
		if(forePsndocs.size()<1){
			return;
		}
		UFDouble basePay=getBaseDocUFDoubleValue(context.getPk_org(), "TWSP0009");//基本工资
		UFDouble baseMultiple=getBaseDocUFDoubleValue(context.getPk_org(), "TWSP0010");//基本工资倍数
		UFDouble taxRate=getBaseDocUFDoubleValue(context.getPk_org(), "TWSP0011");//基本税率
		UFDouble excessTaxRate=getBaseDocUFDoubleValue(context.getPk_org(), "TWSP0012");//超额扣税税率
		
		StringBuffer updateSql=new StringBuffer();
		updateSql.append("UPDATE wa_cacu_data\n" );
		updateSql.append("SET cacu_value = CASE\n" );
		updateSql.append("WHEN tax_base < "+basePay.toString()+" * "+baseMultiple.toString()+" THEN\n" );
		updateSql.append("	tax_base * "+taxRate.toString()+"\n" );
		updateSql.append("ELSE\n" );
		updateSql.append("	tax_base * "+excessTaxRate.toString()+"\n" );
		updateSql.append("END \n" );
		updateSql.append("WHERE\n" );
		updateSql.append("	wa_cacu_data.pk_wa_class = '"+context.getPk_wa_class()+"'\n" );
		updateSql.append("AND wa_cacu_data.creator = '"+context.getPk_loginUser()+"'");
		InSQLCreator isc = new InSQLCreator();
		String inPsndocSql = isc.getInSQL(forePsndocs.toArray(new String[0]));
		updateSql.append("AND wa_cacu_data.pk_psndoc in ("+inPsndocSql+")");
		
		getDao().executeUpdate(updateSql.toString());
		
		
		
	}
	
	private UFDouble getBaseDocUFDoubleValue(String pk_org, String paramCode)
			throws BusinessException {
		IBasedocPubQuery baseQry = NCLocator.getInstance().lookup(
				IBasedocPubQuery.class);
		BaseDocVO baseDoc = baseQry.queryBaseDocByCode(pk_org, paramCode);
		if (baseDoc == null) {
			throw new BusinessException("未定義薪資參數：" + paramCode);
		}

		UFDouble value = UFDouble.ZERO_DBL;
		if (baseDoc.getDoctype() == 1) {
			value = baseDoc.getNumbervalue();
		} else if (baseDoc.getDoctype() == 2) {
			value = baseDoc.getNumbervalue().div(100);
		}
		
		return value;
	}
	
	private BaseDAO dao;
	
	private BaseDAO getDao(){
		if(null==dao){
			dao=new BaseDAO();
		}
		return dao;
	}
	/**
	 * 获取参数设置参数
	 * @param pk_org
	 * @param initcode
	 * @return
	 * @throws DAOException
	 */
	private String getSysinit(String pk_org,String initcode) throws DAOException{
		String qrySql="select value from pub_sysinit where initcode='"+initcode+"' and pk_org='"+pk_org+"' and isnull(dr,0)=0";
		Object sysinitValue=getDao().executeQuery(qrySql, new ColumnProcessor());
		return String.valueOf(sysinitValue);
	}
	
	private List<PsndocVO> getListPsndocVO(String[] pks) throws BusinessException{
		InSQLCreator isc = new InSQLCreator();
		String inPsndocSql = isc.getInSQL(pks);
		String where = PsndocVO.PK_PSNDOC+" in("+inPsndocSql+")";
		@SuppressWarnings("unchecked")
		List<PsndocVO> list=(ArrayList<PsndocVO>) getDao().retrieveByClause(PsndocVO.class, where);
		return list;
	}
	/**
	 * 判断员工工作是否满183天
	 * @param twhr08
	 * @param twhr09
	 * @param psn
	 * @return
	 * @throws BusinessException 
	 */
	private boolean isExpire(String twhr08,UFDouble expireNum,PsndocVO psn,WaLoginContext context) throws BusinessException{
		if("1001ZZ1000000001NCMA".equals(twhr08)){//扣税核算入境日期
			UFDate permitDate=new UFDate(psn.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN03")).toString());
			String year=context.getWaYear();
			String month=context.getWaPeriod();
			UFDate oneDay=new UFDate(year+"-01-01");//会计年第一天
			UFDate currentMonthLastDay=new UFDate(getLastDayOfMonth(Integer.parseInt(year), Integer.parseInt(month)));//薪资结束日期
			int days=0;
			if(permitDate.before(oneDay)){
				days=UFDate.getDaysBetween(oneDay, currentMonthLastDay);
			}else{
				days=UFDate.getDaysBetween(permitDate, currentMonthLastDay);
			}
			if(days-expireNum.intValue()>=0){
				return true;
			}
		}else if("1001ZZ1000000001NCMB".equals(twhr08)){//居留证到期日
			String twhr09=getSysinit(context.getPk_org(), "TWHR09");
			if("null".equals(twhr09)||"".equals(twhr09)){
				throw new BusinessException(ResHelper.getString("notice","2notice-tw-000008")/*"系统参数居留证到期日(TWHR09)未设置，请维护后重新操作。"*/);
			}
			UFDate permitDate=new UFDate(psn.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN04")).toString());
			UFDate datumDay=new UFDate(twhr09);
			if(permitDate.after(datumDay)){
				return true;
			}
		}
		return false;
	}
	
    public  String getLastDayOfMonth(int year,int month)  
    {  
        Calendar cal = Calendar.getInstance();  
        //设置年份  
        cal.set(Calendar.YEAR,year);  
        //设置月份  
        cal.set(Calendar.MONTH, month-1);  
        //获取某月最大天数  
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);  
        //设置日历中月份的最大天数  
        cal.set(Calendar.DAY_OF_MONTH, lastDay);  
        //格式化日期  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        String lastDayOfMonth = sdf.format(cal.getTime());  
          
        return lastDayOfMonth;  
    }  

}
