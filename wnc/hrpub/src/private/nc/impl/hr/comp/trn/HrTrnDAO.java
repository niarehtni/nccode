package nc.impl.hr.comp.trn;

import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.om.IAOSQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.vo.hi.pub.CommonValue;
import nc.vo.hr.comp.trn.PsnTrnVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * ��Ա�䶯��ѯDAO
 * 
 * @author: liangxr
 * @date: 2010-2-5 09:20:42
 */
public class HrTrnDAO extends BaseDAOManager {
	public HrTrnDAO() {
		super();
	}

	/**
	 * ��ѯ�䶯��ԱVO
	 * 
	 * @author liangxr on 2010-2-24
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @param trnType
	 * @param strAddWhere
	 * @return
	 * @throws BusinessException
	 */
	public PsnTrnVO[] queryTRNPsnInf(String pk_org, UFLiteralDate beginDate, UFLiteralDate endDate, int trnType,
			String strAddWhere) throws BusinessException {
		return queryTRNPsnInf0(pk_org, beginDate, endDate, trnType, strAddWhere, false);
	}

	/**
	 * filterByOrg:�Ƿ���hr��֯���˱䶯��Ա�����Ϊtrue����ֻ���ر䶯����֯��������ϵ������pk_hrorg �������Ա
	 * 
	 * @param pk_hrorg
	 * @param beginDate
	 * @param endDate
	 * @param trnType
	 * @param strAddWhere
	 * @param filterByOrg
	 * @param pk_psncl
	 * @param pk_trnstype
	 * @return
	 * @throws BusinessException
	 */
	protected PsnTrnVO[] queryTRNPsnInf0(String pk_hrorg, UFLiteralDate beginDate, UFLiteralDate endDate, int trnType,
			String strAddWhere, boolean filterByOrg) throws BusinessException {

		// н�ʸ����õ����ֶ� "pk_dept", "pk_post","pk_psncl", "pk_psnjob"
		StringBuffer sqlBuf = new StringBuffer(" select psnjob.pk_psnjob,psndoc.code as psncode,psnjob.clerkcode, ");
		sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("psndoc.name") + "  as psnname, "); // 6
		sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  as psnclassname, "); // 6
		// ssx added on 2019-12-21
		sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("hr_trnstype.trnstypename") + "  as trnstypename, "); // 6
		// end
		sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("org_orgs.name") + "  as orgname, "); // 6
		sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("dept.name") + "  as deptname, "); // 6
		sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("job.jobname") + "  as jobname, "); // 6
		sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("job.jobname") + "  as jobname, "); // 6
		// 2016-1-7 NCdp205569523 zhousze н�ʵ����䶯��Ա�е���ְ��Ա����ְǰ��λ������ʾΪ�� begin
		sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("post.postname") + " as postname, "); // 6
		// end
		sqlBuf.append(" dept.pk_dept, psndoc.id as psnid,post.pk_post as pk_post,  ");
		sqlBuf.append(" bd_psncl.pk_psncl, psndoc.pk_psndoc,psnjob.pk_psnorg,  ");
		/*
		 * StringBuffer sqlBuf = new StringBuffer(
		 * "select psnjob.pk_psnjob,psndoc.code as psncode,psnjob.clerkcode,psndoc.name as psnname,psnjob.clerkcode ,bd_psncl.name as psnclassname,"
		 * +
		 * "org_orgs.name as orgname,dept.name as deptname,job.jobname as jobname,post.postname,dept.pk_dept, psndoc.id as psnid,post.pk_post as pk_post, "
		 * + "bd_psncl.pk_psncl, psndoc.pk_psndoc,psnjob.pk_psnorg,");
		 */
		sqlBuf.append("psnjob.ismainjob, psnjob.assgid, ");
		sqlBuf.append("dept.pk_dept as workdept, ");
		sqlBuf.append("dept.pk_vid as workdeptvid, ");
		sqlBuf.append("org_orgs.pk_org as workorg, ");
		sqlBuf.append("org_orgs.pk_vid as workorgvid ");
		// ������sql���������������¼�¼��pk_org
		String tableSql = " from hi_psnjob psnjob "
				+ "inner join bd_psndoc psndoc on psnjob.pk_psndoc = psndoc.pk_psndoc "
				+ "inner join hi_psnorg psnorg on psnjob.pk_psnorg = psnorg.pk_psnorg "
				+ "inner join bd_psncl on psnjob.pk_psncl = bd_psncl.pk_psncl "
				// ssx added on 2019-12-21
				+ "inner join hr_trnstype on psnjob.trnstype = hr_trnstype.pk_trnstype "
				// end
				+ "left outer join org_dept dept on dept.pk_dept = psnjob.pk_dept "
				+ "left outer join om_job job on job.pk_job = psnjob.pk_job "
				+ "left outer join om_post post on post.pk_post = psnjob.pk_post "
				+ "left outer join org_orgs on org_orgs.pk_org = psnjob.pk_org "
				+ "where  psnjob.lastflag = 'Y' and psnorg.indocflag = 'Y' and psnorg.psntype = 0 ";
		/**
		 * ��н���ڣ���ְ���ڣ�ʱ�䷶Χ���� ��н����Ϊ��ʱ��ȡ��ְ��ʼ���ڣ������Ϊ��ʱ��ȡ��н���ں���ְ�����нϴ�����ڽ��бȽ�
		 */
		String beginDateWhere = "and ((psnjob.begindate >= ? and psnjob.begindate <= ? "
				+ "and (psnjob.begindate > psnorg.startpaydate or " + SQLHelper.getNullSql("psnorg.startpaydate")
				+ ")) " + "or (psnorg.startpaydate >= ? and psnorg.startpaydate <= ? "
				+ "and psnorg.startpaydate >= psnjob.begindate ))";
		/**
		 * ��н���ڣ���ְ���ڣ�ʱ�䷶Χ���� ��н����Ϊ��ʱ��ȡ��ְ��ʼ���ڣ������Ϊ��ʱ��ȡ��н���ں���ְ�����нϴ�����ڽ��бȽ�
		 */
		String begin2DateWhere = "and ((job2.begindate >= ? and job2.begindate <= ? "
				+ "and (job2.begindate > psnorg.startpaydate or " + SQLHelper.getNullSql("psnorg.startpaydate") + ")) "
				+ "or (psnorg.startpaydate >= ? and psnorg.startpaydate <= ? "
				+ "and psnorg.startpaydate >= job2.begindate ))";

		/**
		 * ��н���ڣ���ְ���ڣ�ʱ�䷶Χ���� ��н����Ϊ��ʱ��ȡ��ְ��ʼ���ڣ������Ϊ��ʱ��ȡ��н���ں���ְ�����нϴ�����ڽ��бȽ�
		 */
		String begin3DateWhere = "and ((" + SQLHelper.getNullSql("psnorg.startpaydate") + ") "
				+ "or (psnorg.startpaydate >= ? and psnorg.startpaydate <= ? ))";

		/**
		 * ͣн���ڣ���ְ���ڣ�ʱ������ ͣн����Ϊ��ʱ��ȡ��ְ���ڣ������Ϊ�գ�ȡͣн���ں���ְ�����н�С�����ڽ��бȽ�
		 */
		String beginStopWhere = "and ((psnjob.begindate >= ? and psnjob.begindate <= ? "
				+ "and (psnjob.begindate < psnorg.stoppaydate or " + SQLHelper.getNullSql("psnorg.stoppaydate") + ")) "
				+ "or (psnorg.stoppaydate >= ? and psnorg.stoppaydate <= ? "
				+ "and psnorg.stoppaydate <= psnjob.begindate ))";
		/**
		 * ��ְ����
		 */
		String endDateWhere = "and ((psnjob.enddate >= ? and psnjob.enddate <= ? "
				+ "and (psnjob.enddate < psnorg.stoppaydate or " + SQLHelper.getNullSql("psnorg.stoppaydate") + ")) "
				+ "or (psnorg.stoppaydate >= ? and psnorg.stoppaydate <= ? "
				+ "and psnorg.stoppaydate <= psnjob.enddate ))";

		switch (trnType) {
		// �½���Ա: ������ΧΪ��ְ������һ����¼����ְ��ʼ���ڴ��ڿ�ʼ���ڣ�С�ڽ�������
		case CommonValue.TRN_ADD:
			// ��ְȡ�ڼ�������һ��������¼�������¹�����¼
			tableSql = " from hi_psnjob psnjob "
					+ "inner join bd_psndoc psndoc on psnjob.pk_psndoc = psndoc.pk_psndoc "
					+ "inner join hi_psnorg psnorg on psnjob.pk_psnorg = psnorg.pk_psnorg "
					+ "inner join bd_psncl on psnjob.pk_psncl = bd_psncl.pk_psncl "
					// ssx added on 2019-12-21
					+ " inner join hr_trnstype on psnjob.trnstype = hr_trnstype.pk_trnstype "
					// end
					// ssx added on 2020-02-29
					+ " left join bd_defdoc trs on psnjob.trnsreason = trs.pk_defdoc "
					// end
					+ "left outer join org_dept dept on dept.pk_dept = psnjob.pk_dept "
					+ "left outer join om_job job on job.pk_job = psnjob.pk_job "
					+ "left outer join om_post post on post.pk_post = psnjob.pk_post "
					+ "left outer join org_orgs on org_orgs.pk_org = psnjob.pk_org "
					+ "inner join hi_psndoc_psnchg hipschg on  psnjob.pk_psndoc = hipschg.pk_psndoc "
					+ "and hipschg.pk_corp = org_orgs.pk_corp "
					+ " where   psnorg.indocflag = 'Y' and psnorg.psntype = 0 and psnorg.lastflag='Y' ";
			// tableSql = tableSql.replaceAll(
			// " psnjob.lastflag = 'Y' and", "");

			sqlBuf.append(",hipschg.begindate as trndate ");
			sqlBuf.append(tableSql);
			sqlBuf.append("and psnjob.ismainjob = 'Y' ");// ��ְ
			// sqlBuf.append("and psnjob.endflag = 'N' ");// δ��������ְ��¼

			// ssx remarked on 2020-02-29
			// ��ͣ�ĕr����������hi_psnorg.startpaydate���}�ĕr��Ͳ鲻��
			// sqlBuf.append(begin3DateWhere);
			// end

			// param.addParam(beginDate.toStdString());
			// param.addParam(endDate.toStdString());

			// sqlBuf.append(" and psnjob.begindate = (select max(psnjob2.begindate) from hi_psnorg psnjob2 "
			// +
			// "where hipschg.pk_psndoc = psnjob2.pk_psndoc and hipschg.pk_psnorg=psnjob2.pk_psnorg "
			// + "and psnjob2.begindate<= '" + endDate.toStdString() +
			// "' and (psnjob2.enddate>= '"
			// + beginDate.toStdString() + "' or psnjob2.enddate is null)  )");

			// ssx modified on 2020-01-17
			// ��ͣ�}���D���ˆT�������F�����M�ˆT��
			sqlBuf.append(" AND (((hr_trnstype.trnstypecode in ('02','0306') OR trs.code in ('7f')) AND psnjob.begindate <= '"
					+ endDate.toStdString()
					+ "'AND psnjob.begindate >= '"
					+ beginDate.toStdString()
					+ "') OR (hr_trnstype.trnstypecode IN ('0101','0102','0105') AND hipschg.begindate <= '"
					+ endDate.toStdString() + "' AND hipschg.begindate >= '" + beginDate.toStdString() + "'))");
			// end
			break;
		// ��ְ��Ա: ������Χ��ְ������һ����¼�Ľ������ڴ��ڿ�ʼ���ڣ�С�ڽ�������
		case CommonValue.TRN_SUB:
			tableSql = " from hi_psnjob psnjob "
					// 20150730 xiejie3 �����ϲ�
					// shenliangc 20141208 ����֮�ұ䶯��Ա�����ְ��Աҳǩ��ʾ�춯����
					+ "left join hr_trnstype on psnjob.trnstype = hr_trnstype.pk_trnstype "
					// end
					+ " inner join hi_psnjob psnjob_old on psnjob_old.pk_psnorg = psnjob.pk_psnorg "// ȡ����ְ��¼ǰһ��������¼
					+ "inner join bd_psndoc psndoc on psnjob.pk_psndoc = psndoc.pk_psndoc "
					+ "inner join hi_psnorg psnorg on psnjob.pk_psnorg = psnorg.pk_psnorg "
					+ "inner join bd_psncl on psnjob.pk_psncl = bd_psncl.pk_psncl "
					// ssx added on 2019-12-21
					+ "inner join hr_trnstype on psnjob.trnstype = hr_trnstype.pk_trnstype "
					// end
					+ "left outer join org_dept dept on dept.pk_dept = psnjob_old.pk_dept "// ��ְǰ����
					+ "left outer join om_job job on job.pk_job = psnjob.pk_job "
					+ "left outer join om_post post on post.pk_post = psnjob_old.pk_post "// ��ְǰ��λ
					+ "left outer join org_orgs on org_orgs.pk_org = psnjob.pk_org "
					+ "where   psnorg.indocflag = 'Y' and psnorg.psntype = 0 ";
			sqlBuf.append(",psnjob.begindate as trndate ");

			// shenliangc 20141208 ����֮�ұ䶯��Ա�����ְ��Աҳǩ��ʾ�춯����
			// sqlBuf.append(", hr_trnstype.trnstypename trnstypename " );
			// 20151121 xiejie3 NCdp205546892 �籣�������䶯��Ա����ְ��¼�춯���� begin
			sqlBuf.append(" , " + SQLHelper.getMultiLangNameColumn("hr_trnstype.trnstypename") + " trnstypename ");
			// end
			sqlBuf.append(tableSql);
			sqlBuf.append("and psnjob.ismainjob = 'Y' ");// ��ְ
			sqlBuf.append("and psnjob.endflag = 'Y' ");// ��������ְ��¼
			sqlBuf.append("and psnjob.trnsevent = 4 ");// ��ְ��¼
			sqlBuf.append("and psnjob_old.recordnum = psnjob.recordnum + 1 ");// ��ְ������¼���
																				// +
																				// 1
																				// =
																				// ��ְǰ���һ��������¼���
			sqlBuf.append(beginStopWhere);

			break;
		// ��λ�仯��Ա:��ѯ������������ְ��¼��λ�����仯���ɵ���ְ��¼����ְ�������ڴ��ڿ�ʼ���ڣ�С�ڽ�������
		// 20151128 shenliangc NCdp205550656
		// ������仰�޷���֤��������Ҷ��о�Ī����������޸�ΪֻҪ���������ϵĹ�����¼���ж�Ϊ�䶯��Ա������Ҫ��Ӧ�����ѯ������
		case CommonValue.TRN_POST_MOD:
			sqlBuf = new StringBuffer(" select distinct psndoc.pk_psndoc,psndoc.code as psncode,psnjob.clerkcode, ");
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("psndoc.name") + "  as psnname, "); // 6
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  as psnclassname, "); // 6
			// ssx added on 2019-12-21
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("hr_trnstype.trnstypename")
					+ "  as trnstypename, "); // 6
			// end
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("postseries.postseriesname") + "  as postrank, "); // 6
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("dept.name") + "  as deptname, "); // 6
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("dept2.name") + "  as dept2name, "); // 6
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("post2.postname") + "  as post2name, "); // 6
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("postseries2.postseriesname")
					+ "  as postrank2, "); // 6
			// 2016-1-5 NCdp205568948 zhousze ����Ա���ű䶯����н�ʵ����䶯��Ա�У��䶯��Ա�ı��ǰ��λ����Ϊ��
			// beegin
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("post.postname") + " as postname, "); // 6
			// end
			// zhaiync �籣�����䶯��Ա�䶯ǰ����֯Ϊ��
			// 2016-1-6 zhousze �Ա䶯ǰ����֯ʹ��SQLHELPER������ﻷ�� begin
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("org1.name") + " as orgname, ");
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("org2.name") + " as orgname2, ");
			// end
			// 20151013 xiejie3 NCdp205513915 �籣�����䶯��Ա�����С��䶯��Ա��ҳǩ�У���Ա��֤������ʾΪ��
			sqlBuf.append("       " + " psndoc.id as psnid ,");
			// end
			sqlBuf.append(" psnjob.pk_psnjob,psnjob.pk_psnorg,job2.begindate as trndate,psnjob.ismainjob,psnjob.assgid  ");

			/*
			 * sqlBuf = new StringBuffer(
			 * "select distinct psndoc.pk_psndoc,psndoc.code as psncode,psnjob.clerkcode,psndoc.name as psnname,"
			 * +
			 * "bd_psncl.name as psnclassname,dept.name as deptname,post.postname,  postseries.postseriesname as postrank, psndoc.id as psnid,"
			 * +
			 * "dept2.name as dept2name,post2.postname as post2name, postseries2.postseriesname as postrank2, "
			 * +
			 * "psnjob.pk_psnjob,psnjob.pk_psnorg,job2.begindate as trndate,psnjob.ismainjob,psnjob.assgid "
			 * );
			 */
			sqlBuf.append("from hi_psnjob psnjob left join hi_psnjob job2 on job2.pk_psndoc = psnjob.pk_psndoc and job2.pk_psnorg = psnjob.pk_psnorg and job2.recordnum = psnjob.recordnum-1 and psnjob.assgid = job2.assgid  "
			// 20151204 xiejie3 NCdp205554328 ����Ա���ڼ䷢���䶯�����籣�����ı䶯��Ա�п������䶯��Ա
			// ԭ�����ҳǩ�и�λ�䶯�����Դ������£���Ҫ�жϸ�λ�Ƿ�仯��
			// �ָ�Ϊ�䶯��Ա��ֻҪ��Ա����¼�����仯�������������������ֱ��ȥ���� begin
			/*
			 * +"and (" // �������Լ�����һ����¼�Ƚ� +
			 * "(psnjob.pk_post <> job2.pk_post) or " + "( " +
			 * SQLHelper.getEqualsWaveSql("psnjob.pk_post") +
			 * " and job2.pk_post is not null) or " +
			 * "(psnjob.pk_post is not null and " +
			 * SQLHelper.getEqualsWaveSql("job2.pk_post") + "))"
			 */
			// end

			);
			// zhaiync �籣�����䶯��Ա�䶯ǰ����֯Ϊ��
			sqlBuf.append("left join org_orgs org1 on psnjob.pk_org = org1.pk_org ");
			sqlBuf.append("left join org_orgs org2 on job2.pk_org = org2.pk_org ");
			sqlBuf.append("left join hi_psnorg psnorg on psnjob.pk_psnorg = psnorg.pk_psnorg ");
			sqlBuf.append("left join bd_psndoc psndoc on psnjob.pk_psndoc = psndoc.pk_psndoc ");
			sqlBuf.append("left join bd_psncl on psnjob.pk_psncl = bd_psncl.pk_psncl ");
			// ssx added on 2019-12-21
			sqlBuf.append("left join hr_trnstype on psnjob.trnstype = hr_trnstype.pk_trnstype ");
			// end
			sqlBuf.append("left join org_dept dept2 on dept2.pk_dept = job2.pk_dept ");
			sqlBuf.append("left join om_post post2 on post2.pk_post = job2.pk_post ");
			sqlBuf.append("left join om_postseries  postseries on postseries.pk_postseries = psnjob.pk_postseries ");
			sqlBuf.append("left join org_dept dept on dept.pk_dept = psnjob.pk_dept ");
			sqlBuf.append("left join om_post post on post.pk_post = psnjob.pk_post ");
			sqlBuf.append("left join om_postseries  postseries2 on postseries2.pk_postseries = job2.pk_postseries ");
			sqlBuf.append("where ");// ��ְ
			// sqlBuf.append(" psnorg.endflag = 'N' ");// δ��������֯��ϵ
			// sqlBuf.append("and psnjob.endflag = 'Y' ");// ��������ְ��¼
			// sqlBuf.append("and psnjob.lastflag = 'N' ");// ��������ְ
			// 2015-09-29 zhousze NCdp205465478н�ʵ����䶯��Ա��ͨ��ʱ���ȡ����ȡ�ڸ�ʱ����ڵ����µ���ְ��¼��
			// ������ȡ������֯��ϵ�����µ���ְ��¼ begin
			// sqlBuf.append("and job2.lastflag = 'Y' ");
			sqlBuf.append("psnjob.begindate between '" + beginDate.toString() + "' and '" + endDate.toString() + "'");
			// end

			break;
		// ��ְ��ʼ��Ա: ��ְ�����Ǽ�ְ����ְ��ʼ���ڴ��ڿ�ʼ���ڣ�С�ڽ�������
		case CommonValue.TRN_PART_ADD:
			sqlBuf = new StringBuffer(" select psndoc.pk_psndoc,psndoc.code as psncode,psnjob.clerkcode, ");
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("psndoc.name") + "  as psnname, "); // 6
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  as psnclassname, "); // 6
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("dept.name") + "  as deptname, "); // 6
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("post.postname") + " as postname, "); // 6
			sqlBuf.append(" psndoc.id as psnid, psnjob.pk_dept, ");
			sqlBuf.append(" psnjob.pk_post,psnjob.pk_psnorg, psnjob.pk_psnjob,psnjob.begindate as trndate,psnjob.enddate,  ");
			sqlBuf.append(" psnjob.ismainjob,psnjob.assgid, ");
			// sqlBuf =
			// new StringBuffer(
			// "select psndoc.pk_psndoc,psndoc.code as psncode,psnjob.clerkcode,psndoc.name as psnname,bd_psncl.name as psnclassname,"
			// +
			// " dept.name as deptname,post.postname,psndoc.id as psnid, psnjob.pk_dept, "
			// +
			// "psnjob.pk_post,psnjob.pk_psnorg, psnjob.pk_psnjob,psnjob.begindate as trndate,psnjob.enddate, "
			// + "psnjob.ismainjob,psnjob.assgid, ");
			sqlBuf.append("dept.pk_dept as workdept, ");
			sqlBuf.append("dept.pk_vid as workdeptvid, ");
			sqlBuf.append("org_orgs.pk_org as workorg, ");
			sqlBuf.append("org_orgs.pk_vid as workorgvid, ");
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("org_orgs.name") + " as orgname "); // 6
			// sqlBuf.append("org_orgs.name as orgname ");
			sqlBuf.append(tableSql);
			sqlBuf.append("and psnjob.ismainjob = 'N' ");// ��ְ
			// sqlBuf.append("and psnjob.endflag = 'N' ");// δ��������ְ��¼
			sqlBuf.append(beginDateWhere);
			break;
		// ��ְ������Ա����ְ�����Ǽ�ְ����ְ�������ڴ��ڿ�ʼ���ڣ�С�ڽ�������
		case CommonValue.TRN_PART_SUB:
			sqlBuf = new StringBuffer(" select psndoc.pk_psndoc,psndoc.code as psncode,psnjob.clerkcode, ");
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("psndoc.name") + "  as psnname, "); // 6
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  as psnclassname, "); // 6
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("dept.name") + "  as deptname, "); // 6
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("post.postname") + " as postname, "); // 6
			sqlBuf.append(" psndoc.id as psnid, psnjob.pk_dept,  ");
			sqlBuf.append(" psnjob.pk_post,psnjob.pk_psnorg, psnjob.pk_psnjob,psnjob.begindate,psnjob.enddate as trndate,  ");
			sqlBuf.append(" psnjob.ismainjob,psnjob.assgid  ");

			/*
			 * sqlBuf = new StringBuffer(
			 * "select psndoc.pk_psndoc,psndoc.code as psncode,psnjob.clerkcode,psndoc.name as psnname,bd_psncl.name as psnclassname,"
			 * +
			 * " dept.name as deptname,post.postname,psndoc.id as psnid, psnjob.pk_dept, "
			 * +
			 * "psnjob.pk_post,psnjob.pk_psnorg, psnjob.pk_psnjob,psnjob.begindate,psnjob.enddate as trndate,"
			 * + "psnjob.ismainjob,psnjob.assgid ");
			 */
			sqlBuf.append(tableSql);
			sqlBuf.append("and psnjob.ismainjob = 'N' ");// ��ְ
			sqlBuf.append("and psnjob.endflag = 'Y' ");// ��������ְ��¼
			sqlBuf.append(endDateWhere);
			break;
		// ������Ա��������ְ�ͼ�ְ��
		case CommonValue.TRN_ALL_ADD:
			sqlBuf.append(",psnorg.psntype,psnjob.begindate as trndate,psnorg.startpaydate as startpaydate ");
			sqlBuf.append(tableSql);
			// sqlBuf.append("and psnjob.endflag = 'N' ");// δ��������ְ��¼
			// sqlBuf.append("and psnorg.endflag = 'N' ");// δ��������֯��ϵ
			sqlBuf.append(beginDateWhere);
			break;
		}

		if (filterByOrg) {
			IAOSQueryService aosService = NCLocator.getInstance().lookup(IAOSQueryService.class);
			OrgVO[] orgVOs = aosService.queryAOSMembersByHROrgPK(pk_hrorg, false, false);
			String inSQL = StringPiecer.getDefaultPiecesTogether(orgVOs, OrgVO.PK_ORG);
			sqlBuf.append(" and psnjob.pk_org in(").append(inSQL).append(") ");
		}

		sqlBuf.append(" and psnjob.pk_org in (select pk_adminorg from org_admin_enable) ");
		// ҵ�����������Ϊ��ʱҪУ��
		if (strAddWhere != null && strAddWhere.length() > 0) {
			sqlBuf.append(strAddWhere);
		}

		SQLParameter param = new SQLParameter();

		if (CommonValue.TRN_POST_MOD != trnType) {
			// param.addParam(pk_org);
			param.addParam(beginDate.toStdString());
			param.addParam(endDate.toStdString());
			param.addParam(beginDate.toStdString());
			param.addParam(endDate.toStdString());
		}

		String sql = sqlBuf.toString();

		// �½���Ա: ������ΧΪ��ְ������һ����¼����ְ��ʼ���ڴ��ڿ�ʼ���ڣ�С�ڽ�������
		if (CommonValue.TRN_ADD == trnType) {
			// ��ְȡ�ڼ�������һ��������¼�������¹�����¼
			sql = sql.replaceAll(" and hi_psnjob.lastflag = 'Y' ", " ");
			param = new SQLParameter();
			// param.addParam(pk_org);
			param.addParam(beginDate.toStdString());
			param.addParam(endDate.toStdString());
		}

		return executeQueryVOs(sql, sql.contains("?") ? param : null, PsnTrnVO.class);
	}

	/**
	 * רΪʱ������ṩ�Ĳ�ѯ�䶯��Ա�ķ��� Ҫ��䶯��Ա�䶯���pk_org��pk_hrorg�Ĺ�Ͻ��Χ��
	 * 
	 * @param pk_hrorg
	 * @param beginDate
	 * @param endDate
	 * @param trnType
	 * @param strAddWhere
	 * @return
	 * @throws BusinessException
	 */
	public PsnTrnVO[] queryTRNPsnInf4TA(String pk_hrorg, UFLiteralDate beginDate, UFLiteralDate endDate, int trnType,
			String strAddWhere) throws BusinessException {
		return queryTRNPsnInf0(pk_hrorg, beginDate, endDate, trnType, strAddWhere, true);
	}
}
