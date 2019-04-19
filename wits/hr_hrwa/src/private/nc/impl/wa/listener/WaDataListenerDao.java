package nc.impl.wa.listener;

import java.util.ArrayList;
import java.util.HashMap;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IWaBmfileQueryService;
import nc.jdbc.framework.DataSourceCenter;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.MapProcessor;
import nc.jdbc.framework.util.DBConsts;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hr.pub.WaBmFileOrgVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

/**
 * н����Ӧ���±䶯DAO
 */
public class WaDataListenerDao extends BaseDAOManager{

	private String getFilterSql(PsnJobVO psnjobBefore, PsnJobVO psnJobAfter,
			int changeDate) throws BusinessException {

		String sql = "  select wa_data.pk_wa_data,wa_period.cstartdate,wa_period.cenddate from  wa_data "
				+ "  inner join wa_waclass on wa_waclass.pk_wa_class = wa_data.pk_wa_class"
				+ "  inner join wa_periodscheme on wa_waclass.pk_periodscheme = wa_periodscheme.pk_periodscheme"
				+ "  inner join wa_period on wa_period.pk_periodscheme = wa_periodscheme.pk_periodscheme and  wa_period.cyear = wa_data.cyear and wa_period.cperiod = wa_data.cperiod"
				+ "  where   wa_data.checkflag = 'N'";
//		20160116  xiejie3  NCdp205574437  ��Ա��������޸Ĳ����Ƿ�������Ӧ�仯Ϊ25����Ա�ڸ�����ǰ�ٴη��������н�ʵ�����û��������			
//		ԭ�򣺵��䶯��Աҵ�����Ϊ0ʱ������������н�ʵ�����Ա������¼������н�ʵ����������һ��������¼ job1������ʱ������¼������job1��job2��
//		��ʱ���ٰѲ����ĳ�31��������Ա������¼job3�����´������� ������¼ Ϊ job2��job3����ʱ����job2ȥн�ʵ���ȥƥ���ˣ���ƥ�䲻����
//		xxxx ���󷽰�����������1������ͨ��������¼ȥ����н�ʵ������ݣ�������Ա������ѯ�����ص�������δ��˵��籣������Ȼ���ٺ�ҵ��������бȽ��Ƿ����Ҫ��
//		xiejie3  ���÷���2������1û�п��Ǽ�ְ��¼���������н�ʵ���������Ǽ�ְ��¼��ʱ��ֻ�м�ְ��¼�����仯������Ҫ����н�ʵ�����
//			 Ŀǰ���������ݴ������Ĺ�����¼���ж�����ְ���Ǽ�ְ��Ȼ���ѯ��Ӧ��ȫ��������¼��
		if (psnjobBefore != null) {
//			sql += " and wa_data.pk_psnjob = '" + psnjobBefore.getPk_psnjob()
//					+ "'";
			sql += " and   wa_data.pk_psnjob in ( select hi_psnjob.pk_psnjob  from  hi_psnjob  where  hi_psnjob.pk_psndoc = '" + psnjobBefore.getPk_psndoc() +"' and hi_psnjob.ismainjob = '"+ psnjobBefore.getIsmainjob() +"'  )  ";
		} else {
			sql += " and wa_data.pk_psndoc = '" + psnJobAfter.getPk_psndoc()
					+ "'";
		}
//			end  
		GeneralVO[] generalVOs = executeQueryVOs(sql, GeneralVO.class);

		String cchangeDay = "" + changeDate;
		String cstartdate = "";
		String cenddate = "";
		String cchangeDate = "";
		if (changeDate < 10) {
			cchangeDay = "0" + changeDate;
		}
		ArrayList<String> waDataPkList = new ArrayList<String>();
		for (GeneralVO generalVO : generalVOs) {
			cstartdate = generalVO.getAttributeValue("cstartdate").toString();
			cenddate = generalVO.getAttributeValue("cenddate").toString();
			if (cstartdate.substring(0, 8).equals(

					psnJobAfter.getBegindate().toString().substring(0, 8))) {
				cchangeDate = cstartdate.substring(0, 8).concat(cchangeDay);
			} else {
				cchangeDate = cenddate.substring(0, 8).concat(cchangeDay);
			}
			if (cchangeDate.compareTo(psnJobAfter.getBegindate().toString()) >= 0) {
				waDataPkList.add(generalVO.getAttributeValue("pk_wa_data")
						.toString());
			}
		}
		String filterSql = "";
		if (waDataPkList.isEmpty()) {
			return filterSql;
		}
		InSQLCreator inC = new InSQLCreator();
		try {
			filterSql = " and pk_wa_data in ("
					+ inC.getInSQL(waDataPkList.toArray(new String[0])) + ")";

		} catch (BusinessException e) {
			throw new DAOException(e);
		} finally {
			try {
				inC.clear();
			} catch (Exception e2) {
				Logger.error(e2.getMessage(), e2);
			}
		}
		return filterSql;
	}

	/**
	 * ����н�ʵ�����Ҫ������ָ�������ڹ���
	 * 
	 * @param psnjobBefore
	 *            PsnJobVO
	 * @param psnJobAfter
	 *            PsnJobVO
	 * @throws BusinessException
	 */
	public void updateWaDataByPsnJob(PsnJobVO psnjobBefore,
			PsnJobVO psnJobAfter, int changeDate) throws BusinessException {

		String filterSql = getFilterSql(psnjobBefore, psnJobAfter, changeDate);
		if (StringUtils.isEmpty(filterSql)) {
			return;
		}
		String sql = "";
		if (psnJobAfter == null) {
			// ��ְ��¼�п��ܱ�ɾ�⡣
			sql = " delete from wa_data where  pk_psnjob = '"
					+ psnjobBefore.getPk_psnjob() + "' and checkflag = 'N'";
			sql += filterSql;
			getBaseDao().executeUpdate(sql);
		} else {
			sql = " update wa_data set pk_psnjob = '"
//		20160116  xiejie3  NCdp205574437  ��Ա��������޸Ĳ����Ƿ�������Ӧ�仯Ϊ25����Ա�ڸ�����ǰ�ٴη��������н�ʵ�����û��������			
//				ԭ�򣺵��䶯��Աҵ�����Ϊ0ʱ������������н�ʵ�����Ա������¼������н�ʵ����������һ��������¼ job1������ʱ������¼������job1��job2��
//				��ʱ���ٰѲ����ĳ�31��������Ա������¼job3�����´������� ������¼ Ϊ job2��job3����ʱ����job2ȥн�ʵ���ȥƥ���ˣ���ƥ�䲻����
//				����������ͨ��������¼ȥ����н�ʵ������ݣ�������Ա������ѯ�����ص�������δ��˵��籣������Ȼ���ٺ�ҵ��������бȽ��Ƿ����Ҫ��
//					+ psnJobAfter.getPk_psnjob() + "' where pk_psnjob = '"
//					+ psnjobBefore.getPk_psnjob() + "' and checkflag = 'N'";
					+ psnJobAfter.getPk_psnjob() + "' where " +
					"  checkflag = 'N' ";
//		end  NCdp205574437
			sql += filterSql;
			getBaseDao().executeUpdate(sql);
			updateWaDataMulVer(psnJobAfter.getPk_psnjob(), filterSql);
		}
	}
	/**
	 * ����н�ʵ���
	 *
	 * @param psnjobBefore PsnJobVO
	 * @param psnJobAfter PsnJobVO
	 * @throws BusinessException
	 */
	public void updateWaDataByPsnJob(PsnJobVO psnjobBefore,PsnJobVO psnJobAfter) throws BusinessException{

		String sql = "";
		if(psnJobAfter==null){
			//��ְ��¼�п��ܱ�ɾ�⡣
			sql = " delete from wa_data where  pk_psnjob = '"+psnjobBefore.getPk_psnjob()+"' and checkflag = 'N'";
			getBaseDao().executeUpdate(sql);
		}else{
			sql = " update wa_data set pk_psnjob = '"+psnJobAfter.getPk_psnjob()+"' where pk_psnjob = '"+psnjobBefore.getPk_psnjob()+"' and checkflag = 'N'";
			getBaseDao().executeUpdate(sql);
			updateWaDataMulVer(psnJobAfter.getPk_psnjob(), null);
		}
	}
	/**
	 * ����н�ʵ���
	 *
	 * @param psnjobBefore PsnJobVO
	 * @param psnJobAfter PsnJobVO
	 * @throws BusinessException
	 */
	public void updateWaDataByPsnDoc(PsnJobVO psnJobAfter, int changeDate)
			throws BusinessException {
		String filterSql = getFilterSql(null, psnJobAfter, changeDate);
		if (StringUtils.isEmpty(filterSql)
				// 2015-10-23 zhousze û�м���н�ʵ�������Ա�Ͳ�ͬ��н�ʵ��� begin
//				&& changeDate < psnJobAfter.getBegindate().getDay()
				// end
				) {
			return;
		}
		String sql = "update wa_data set pk_psnjob = '"+psnJobAfter.getPk_psnjob()+"' where pk_psndoc = '"+psnJobAfter.getPk_psndoc()+"' and checkflag = 'N'";
		sql += filterSql;
		getBaseDao().executeUpdate(sql);
		updateWaDataMulVer(psnJobAfter.getPk_psnjob(), filterSql);
	}

	/**
	 * ���¶�汾��Ϣ
	 * @param pk_psnjob
	 * @throws DAOException
	 */
	public void updateWaDataMulVer(String pk_psnjob, String filterSql)
			throws DAOException {
		//IWaBmfileQueryService
		WaBmFileOrgVO payOrgVO = new WaBmFileOrgVO();
		String pk_financeorg = null;
		String pk_financedept = null;
		String fiporgvid = null;
		String fipdeptvid = null;
		String pk_costcenter = null;
		try {
			// 2015-10-23 zhousze �жϸ�VO�Ƿ�Ϊ�� begin
			//20151212 shenliangc ��Ա��ְ��¼û�ж�Ӧ������֯������н�ʵ������±���
			payOrgVO = NCLocator.getInstance().lookup(IWaBmfileQueryService.class).getPkFinanceOrg(pk_psnjob);
			if(payOrgVO!=null){
	    		pk_financeorg = payOrgVO.getPk_financeorg();
	    		pk_financedept = payOrgVO.getPk_financedept();
	    		fiporgvid = payOrgVO.getFiporgvid();
	    		fipdeptvid = payOrgVO.getFipdeptvid();
	    		StringBuffer sbd = new StringBuffer();
	    		sbd.append(" select resa_costcenter.pk_costcenter pk_costcenter from resa_costcenter, resa_ccdepts ");
	    		sbd.append(" where resa_costcenter.pk_costcenter = resa_ccdepts.pk_costcenter and resa_ccdepts.pk_dept in ");
	     		sbd.append(" (SELECT org_dept.pk_dept FROM hi_psnjob,org_dept,wa_data WHERE ");
	    		sbd.append(" wa_data.pk_psnjob = hi_psnjob.pk_psnjob AND hi_psnjob.pk_dept = org_dept.pk_dept");
	    		sbd.append(" and wa_data.checkflag = 'N' and wa_data.pk_psnjob = '"+pk_psnjob+"' )");
	    		sbd.append(" and resa_costcenter.enablestate = '2'");
	    		HashMap<String, String> map4lo = (HashMap<String, String>)getBaseDao().executeQuery(sbd.toString(), new MapProcessor()); 
	    		if (map4lo != null && map4lo.size()>0) {
	    			pk_costcenter = map4lo.get("pk_costcenter");
		    	}
			}
			// end
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		String sql = "";
		//20151212 shenliangc ��Ա��ְ��¼û�ж�Ӧ������֯������н�ʵ������±���
		String sqlfi = (pk_financeorg != null ? " , pk_financeorg = '" + pk_financeorg + "'" : " , pk_financeorg = null ")
				+ (pk_financedept != null ? " , pk_financedept = '" + pk_financedept + "'" : " , pk_financedept = null ")
				+ (fiporgvid != null ? " , fiporgvid = '" + fiporgvid + "'" : " , fiporgvid = null ")
				+ (fipdeptvid != null ? " , fipdeptvid = '" + fipdeptvid + "'" : " , fipdeptvid = null ");
		if (getDataBaseType() == DBConsts.SQLSERVER) {
			sql = "UPDATE wa_data " + " SET workorg = (SELECT org_orgs.pk_org "
					+ "				FROM hi_psnjob,org_orgs "
					+ "				WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "					AND hi_psnjob.pk_org = org_orgs.pk_org " + " ) "
					+ "	, workorgvid = (SELECT org_orgs.pk_vid "
					+ "					FROM hi_psnjob,org_orgs "
					+ "					WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "						AND hi_psnjob.pk_org = org_orgs.pk_org " + " ) "
					+ "	, workdept = (	SELECT org_dept.pk_dept "
					+ "					FROM hi_psnjob,org_dept "
					+ "					WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "						AND hi_psnjob.pk_dept = org_dept.pk_dept " + " ) "
					+ "	, workdeptvid = (	SELECT org_dept.pk_vid "
					+ "						FROM hi_psnjob,org_dept "
					+ "						WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "							AND hi_psnjob.pk_dept = org_dept.pk_dept "+ " ) "
					+ sqlfi;
			if(StringUtils.isNotBlank(pk_costcenter)){
				sql +=", pk_liabilityorg = '" + pk_costcenter + "'"
					+ "	, pk_liabilitydept = (SELECT org_dept.pk_dept FROM hi_psnjob,org_dept WHERE " 
					+ "                     wa_data.pk_psnjob = hi_psnjob.pk_psnjob AND hi_psnjob.pk_dept = org_dept.pk_dept )"
					+ "	, libdeptvid = (SELECT org_dept.pk_vid FROM hi_psnjob,org_dept WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob AND "
					+ "						hi_psnjob.pk_dept = org_dept.pk_dept ) ";
			}else{
				sql +=", pk_liabilityorg = null"
					+ "	, pk_liabilitydept = null " 
					+ "	, libdeptvid = null ";
			}
			sql	+= " WHERE wa_data.checkflag = 'N' and pk_psnjob = ?";

		} else {
			sql = "UPDATE wa_data "
					+ " SET (workorg, workorgvid) = (SELECT org_orgs.pk_org,org_orgs.pk_vid "
					+ "				FROM hi_psnjob,org_orgs "
					+ "				WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "					AND hi_psnjob.pk_org = org_orgs.pk_org	) "
					+ "	, (workdept, workdeptvid) = (	SELECT org_dept.pk_dept,org_dept.pk_vid "
					+ "					FROM hi_psnjob,org_dept "
					+ "					WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob "
					+ "						AND hi_psnjob.pk_dept = org_dept.pk_dept	) "
					+ sqlfi;
			if(StringUtils.isNotBlank(pk_costcenter)){
				sql +=", pk_liabilityorg = '" + pk_costcenter + "'"
					+ "	, pk_liabilitydept = (SELECT org_dept.pk_dept FROM hi_psnjob,org_dept WHERE " 
					+ "                     wa_data.pk_psnjob = hi_psnjob.pk_psnjob AND hi_psnjob.pk_dept = org_dept.pk_dept )"
					+ "	, libdeptvid = (SELECT org_dept.pk_vid FROM hi_psnjob,org_dept WHERE wa_data.pk_psnjob = hi_psnjob.pk_psnjob AND "
					+ "						hi_psnjob.pk_dept = org_dept.pk_dept ) ";
			}else{
				sql +=", pk_liabilityorg = null"
						+ "	, pk_liabilitydept = null " 
						+ "	, libdeptvid = null ";
				}
			sql	+= " WHERE wa_data.checkflag = 'N' and pk_psnjob = ?";
		}
		if (!StringUtils.isEmpty(filterSql)) {
			sql += filterSql;
		}
		SQLParameter param = new SQLParameter();
		param.addParam(pk_psnjob);
		getBaseDao().executeUpdate(sql, param);

	}

	/**
	 * ��鹤����¼�Ƿ�������
	 *
	 * @param psnjobBefore PsnJobVO
	 * @throws BusinessException
	 */
	public void checkPsnJobUsed(PsnJobVO psnjobBefore) throws BusinessException{
		String sql = " select pk_psndoc from wa_data where pk_psnjob = '"+psnjobBefore.getPk_psnjob()+"' and  checkflag = 'Y' ";
		if(isValueExist(sql)){
			throw new BusinessException(ResHelper.getString("6013salarypmt","06013salarypmt0269")/*@res "�����ѱ����ã�����ɾ����"*/);
		}
	}

	/**
	 * �����Ա��Ϣ�Ƿ�������
	 *
	 * @param psnjobBefore PsnJobVO
	 * @throws BusinessException
	 */
	public void checkPsnUsed(PsnJobVO psnjobBefore) throws BusinessException{
		String sql = "select pk_psndoc from wa_data where pk_psndoc = '"+psnjobBefore.getPk_psndoc()+"'";
		if(isValueExist(sql)){
			throw new BusinessException(ResHelper.getString("6013salarypmt","06013salarypmt0269")/*@res "�����ѱ����ã�����ɾ����"*/);
		}
	}

	/**
	 * ��鹤����¼�Ƿ�������
	 *
	 * @param psnJobAfter PsnJobVO
	 * @throws BusinessException
	 */
	public void checkPsnChange(PsnJobVO psnJobAfter) throws BusinessException{
		String sql = "select pk_psndoc from wa_data where pk_psndoc = '"+psnJobAfter.getPk_psndoc()+"' and  pk_psnjob not in(select pk_psnjob from hi_psnjob where pk_psndoc = '"+psnJobAfter.getPk_psndoc()+"')";
		if(isValueExist(sql)){
			throw new BusinessException(ResHelper.getString("6013salarypmt","06013salarypmt0269")/*@res "�����ѱ����ã�����ɾ����"*/);
		}
	}

	private int getDataBaseType() {
		return DataSourceCenter.getInstance().getDatabaseType(null);
	}
}