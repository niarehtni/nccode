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
 * 人员变动查询DAO
 * 
 * @author: liangxr
 * @date: 2010-2-5 09:20:42
 */
public class HrTrnDAO extends BaseDAOManager {
	public HrTrnDAO() {
		super();
	}

	/**
	 * 查询变动人员VO
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
	 * filterByOrg:是否按照hr组织过滤变动人员，如果为true，则只返回变动后组织在行政体系树上由pk_hrorg 管理的人员
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

		// 薪资福利用到的字段 "pk_dept", "pk_post","pk_psncl", "pk_psnjob"
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
		// 2016-1-7 NCdp205569523 zhousze 薪资档案变动人员中的离职人员的离职前岗位数据显示为空 begin
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
		// 表连接sql及公共条件：最新记录，pk_org
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
		 * 起薪日期（任职日期）时间范围条件 起薪日期为空时，取任职开始日期；如果不为空时，取起薪日期和任职日期中较大的日期进行比较
		 */
		String beginDateWhere = "and ((psnjob.begindate >= ? and psnjob.begindate <= ? "
				+ "and (psnjob.begindate > psnorg.startpaydate or " + SQLHelper.getNullSql("psnorg.startpaydate")
				+ ")) " + "or (psnorg.startpaydate >= ? and psnorg.startpaydate <= ? "
				+ "and psnorg.startpaydate >= psnjob.begindate ))";
		/**
		 * 起薪日期（任职日期）时间范围条件 起薪日期为空时，取任职开始日期；如果不为空时，取起薪日期和任职日期中较大的日期进行比较
		 */
		String begin2DateWhere = "and ((job2.begindate >= ? and job2.begindate <= ? "
				+ "and (job2.begindate > psnorg.startpaydate or " + SQLHelper.getNullSql("psnorg.startpaydate") + ")) "
				+ "or (psnorg.startpaydate >= ? and psnorg.startpaydate <= ? "
				+ "and psnorg.startpaydate >= job2.begindate ))";

		/**
		 * 起薪日期（任职日期）时间范围条件 起薪日期为空时，取任职开始日期；如果不为空时，取起薪日期和任职日期中较大的日期进行比较
		 */
		String begin3DateWhere = "and ((" + SQLHelper.getNullSql("psnorg.startpaydate") + ") "
				+ "or (psnorg.startpaydate >= ? and psnorg.startpaydate <= ? ))";

		/**
		 * 停薪日期（离职日期）时间条件 停薪日期为空时，取离职日期，如果不为空，取停薪日期和离职日期中较小的日期进行比较
		 */
		String beginStopWhere = "and ((psnjob.begindate >= ? and psnjob.begindate <= ? "
				+ "and (psnjob.begindate < psnorg.stoppaydate or " + SQLHelper.getNullSql("psnorg.stoppaydate") + ")) "
				+ "or (psnorg.stoppaydate >= ? and psnorg.stoppaydate <= ? "
				+ "and psnorg.stoppaydate <= psnjob.begindate ))";
		/**
		 * 兼职结束
		 */
		String endDateWhere = "and ((psnjob.enddate >= ? and psnjob.enddate <= ? "
				+ "and (psnjob.enddate < psnorg.stoppaydate or " + SQLHelper.getNullSql("psnorg.stoppaydate") + ")) "
				+ "or (psnorg.stoppaydate >= ? and psnorg.stoppaydate <= ? "
				+ "and psnorg.stoppaydate <= psnjob.enddate ))";

		switch (trnType) {
		// 新进人员: 归属范围为主职，最新一条记录的任职开始日期大于开始日期，小于结束日期
		case CommonValue.TRN_ADD:
			// 主职取期间段内最后一条工作记录而非最新工作记录
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
			sqlBuf.append("and psnjob.ismainjob = 'Y' ");// 主职
			// sqlBuf.append("and psnjob.endflag = 'N' ");// 未结束的任职记录

			// ssx remarked on 2020-02-29
			// 留停的時候如果不清空hi_psnorg.startpaydate，複職的時候就查不到
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
			// 留停複職及轉正人員都應出現在新進人員中
			sqlBuf.append(" AND (((hr_trnstype.trnstypecode in ('02','0306') OR trs.code in ('7f')) AND psnjob.begindate <= '"
					+ endDate.toStdString()
					+ "'AND psnjob.begindate >= '"
					+ beginDate.toStdString()
					+ "') OR (hr_trnstype.trnstypecode IN ('0101','0102','0105') AND hipschg.begindate <= '"
					+ endDate.toStdString() + "' AND hipschg.begindate >= '" + beginDate.toStdString() + "'))");
			// end
			break;
		// 离职人员: 归属范围主职，最新一条记录的结束日期大于开始日期，小于结束日期
		case CommonValue.TRN_SUB:
			tableSql = " from hi_psnjob psnjob "
					// 20150730 xiejie3 补丁合并
					// shenliangc 20141208 海澜之家变动人员面板离职人员页签显示异动类型
					+ "left join hr_trnstype on psnjob.trnstype = hr_trnstype.pk_trnstype "
					// end
					+ " inner join hi_psnjob psnjob_old on psnjob_old.pk_psnorg = psnjob.pk_psnorg "// 取到离职记录前一条工作记录
					+ "inner join bd_psndoc psndoc on psnjob.pk_psndoc = psndoc.pk_psndoc "
					+ "inner join hi_psnorg psnorg on psnjob.pk_psnorg = psnorg.pk_psnorg "
					+ "inner join bd_psncl on psnjob.pk_psncl = bd_psncl.pk_psncl "
					// ssx added on 2019-12-21
					+ "inner join hr_trnstype on psnjob.trnstype = hr_trnstype.pk_trnstype "
					// end
					+ "left outer join org_dept dept on dept.pk_dept = psnjob_old.pk_dept "// 离职前部门
					+ "left outer join om_job job on job.pk_job = psnjob.pk_job "
					+ "left outer join om_post post on post.pk_post = psnjob_old.pk_post "// 离职前岗位
					+ "left outer join org_orgs on org_orgs.pk_org = psnjob.pk_org "
					+ "where   psnorg.indocflag = 'Y' and psnorg.psntype = 0 ";
			sqlBuf.append(",psnjob.begindate as trndate ");

			// shenliangc 20141208 海澜之家变动人员面板离职人员页签显示异动类型
			// sqlBuf.append(", hr_trnstype.trnstypename trnstypename " );
			// 20151121 xiejie3 NCdp205546892 社保档案，变动人员中离职记录异动类型 begin
			sqlBuf.append(" , " + SQLHelper.getMultiLangNameColumn("hr_trnstype.trnstypename") + " trnstypename ");
			// end
			sqlBuf.append(tableSql);
			sqlBuf.append("and psnjob.ismainjob = 'Y' ");// 主职
			sqlBuf.append("and psnjob.endflag = 'Y' ");// 结束的任职记录
			sqlBuf.append("and psnjob.trnsevent = 4 ");// 离职记录
			sqlBuf.append("and psnjob_old.recordnum = psnjob.recordnum + 1 ");// 离职工作记录序号
																				// +
																				// 1
																				// =
																				// 离职前最后一条工作记录序号
			sqlBuf.append(beginStopWhere);

			break;
		// 岗位变化人员:查询任意连续的任职记录岗位发生变化，旧的任职记录的任职结束日期大于开始日期，小于结束日期
		// 20151128 shenliangc NCdp205550656
		// 上面这句话无法考证出处，大家都感觉莫名其妙。现在修改为只要有两条以上的工作记录即判定为变动人员。不过要相应处理查询条件。
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
			// 2016-1-5 NCdp205568948 zhousze 当人员发放变动后，在薪资档案变动人员中，变动人员的变更前岗位数据为空
			// beegin
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("post.postname") + " as postname, "); // 6
			// end
			// zhaiync 社保档案变动人员变动前后组织为空
			// 2016-1-6 zhousze 对变动前后组织使用SQLHELPER处理多语环境 begin
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("org1.name") + " as orgname, ");
			sqlBuf.append("       " + SQLHelper.getMultiLangNameColumn("org2.name") + " as orgname2, ");
			// end
			// 20151013 xiejie3 NCdp205513915 社保档案变动人员界面中【变动人员】页签中，人员的证件号显示为空
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
			// 20151204 xiejie3 NCdp205554328 当人员当期间发生变动后，在社保档案的变动人员中看不到变动人员
			// 原来这个页签叫岗位变动，所以处理如下，需要判断岗位是否变化。
			// 现改为变动人员，只要人员工作录发生变化，则查出，所以下面过滤直接去掉。 begin
			/*
			 * +"and (" // 仅仅与自己的上一条记录比较 +
			 * "(psnjob.pk_post <> job2.pk_post) or " + "( " +
			 * SQLHelper.getEqualsWaveSql("psnjob.pk_post") +
			 * " and job2.pk_post is not null) or " +
			 * "(psnjob.pk_post is not null and " +
			 * SQLHelper.getEqualsWaveSql("job2.pk_post") + "))"
			 */
			// end

			);
			// zhaiync 社保档案变动人员变动前后组织为空
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
			sqlBuf.append("where ");// 主职
			// sqlBuf.append(" psnorg.endflag = 'N' ");// 未结束的组织关系
			// sqlBuf.append("and psnjob.endflag = 'Y' ");// 结束的任职记录
			// sqlBuf.append("and psnjob.lastflag = 'N' ");// 非最新任职
			// 2015-09-29 zhousze NCdp205465478薪资档案变动人员，通过时间段取数，取在该时间段内的最新的任职记录，
			// 而不是取整个组织关系中最新的任职记录 begin
			// sqlBuf.append("and job2.lastflag = 'Y' ");
			sqlBuf.append("psnjob.begindate between '" + beginDate.toString() + "' and '" + endDate.toString() + "'");
			// end

			break;
		// 兼职开始人员: 任职类型是兼职，任职开始日期大于开始日期，小于结束日期
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
			sqlBuf.append("and psnjob.ismainjob = 'N' ");// 兼职
			// sqlBuf.append("and psnjob.endflag = 'N' ");// 未结束的任职记录
			sqlBuf.append(beginDateWhere);
			break;
		// 兼职结束人员：任职类型是兼职，任职结束日期大于开始日期，小于结束日期
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
			sqlBuf.append("and psnjob.ismainjob = 'N' ");// 兼职
			sqlBuf.append("and psnjob.endflag = 'Y' ");// 结束的任职记录
			sqlBuf.append(endDateWhere);
			break;
		// 新增人员（包括主职和兼职）
		case CommonValue.TRN_ALL_ADD:
			sqlBuf.append(",psnorg.psntype,psnjob.begindate as trndate,psnorg.startpaydate as startpaydate ");
			sqlBuf.append(tableSql);
			// sqlBuf.append("and psnjob.endflag = 'N' ");// 未结束的任职记录
			// sqlBuf.append("and psnorg.endflag = 'N' ");// 未结束的组织关系
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
		// 业务过滤条件不为空时要校验
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

		// 新进人员: 归属范围为主职，最新一条记录的任职开始日期大于开始日期，小于结束日期
		if (CommonValue.TRN_ADD == trnType) {
			// 主职取期间段内最后一条工作记录而非最新工作记录
			sql = sql.replaceAll(" and hi_psnjob.lastflag = 'Y' ", " ");
			param = new SQLParameter();
			// param.addParam(pk_org);
			param.addParam(beginDate.toStdString());
			param.addParam(endDate.toStdString());
		}

		return executeQueryVOs(sql, sql.contains("?") ? param : null, PsnTrnVO.class);
	}

	/**
	 * 专为时间管理提供的查询变动人员的方法 要求变动人员变动后的pk_org在pk_hrorg的管辖范围内
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
