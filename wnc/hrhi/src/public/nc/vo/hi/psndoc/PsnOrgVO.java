package nc.vo.hi.psndoc;

import nc.vo.hi.psndoc.enumeration.PsnType;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

/***************************************************************************
 * <br>
 * Created on 2010-1-21 10:46:18<br>
 * 
 * @author Rocex Wang
 ***************************************************************************/
public class PsnOrgVO extends PsnSuperVO {
	/** */
	private static final long serialVersionUID = -7861723902001925060L;

	/** 表名 */
	private static final String _TABLE_NAME = "hi_psnorg";

	/** */
	public static final String BEGINDATE = "begindate";
	/** */
	public static final String EMPFORMS = "empforms";
	/** */
	public static final String ENDDATE = "enddate";
	/** */
	public static final String ENDFLAG = "endflag";
	/** */
	public static final String INDOC_SOURCE = "indoc_source";
	/** */
	public static final String INDOCFLAG = "indocflag";
	/** */
	public static final String JOINSYSDATE = "joinsysdate";
	/** */
	public static final String LASTFLAG = "lastflag";
	/** */
	public static final String ORGRELAID = "orgrelaid";
	/** */
	public static final String PK_GROUP = "pk_group";
	/** */
	public static final String PK_HRORG = "pk_hrorg";
	/** */
	public static final String PK_ORG = "pk_org";
	/** */
	public static final String PK_PSNDOC = "pk_psndoc";
	/** */
	public static final String PK_PSNORG = "pk_psnorg";
	/** */
	public static final String PSNTYPE = "psntype";
	/** */
	public static final String STARTPAYDATE = "startpaydate";
	/** */
	public static final String STOPPAYDATE = "stoppaydate";

	public static final String LOAFLAG = "loaflag";

	// 组织关系去掉‘工龄’，增加‘集团工龄’和‘增减集团工龄’字段 heqiaoa 20150421
	// public static final String WORKAGE = "workage";
	/** */
	public static final String ADJUSTCORPAGE = "adjustcorpage";
	public static final String CORPWORKAGE = "corpworkage";

	private UFLiteralDate begindate;
	private String empforms;
	private UFLiteralDate enddate;
	private UFBoolean endflag = UFBoolean.FALSE;
	private Integer indoc_source;
	private UFBoolean indocflag;
	private UFLiteralDate joinsysdate;
	private UFBoolean lastflag = UFBoolean.TRUE;
	private Integer orgrelaid = 1;
	private String pk_group;
	private String pk_hrorg;
	private String pk_org;
	private String pk_psndoc;
	private String pk_psnorg;
	private Integer psntype = (Integer) PsnType.EMPLOYEE.value();
	private UFLiteralDate startpaydate;
	private UFLiteralDate stoppaydate;
	private UFBoolean loaflag;

	// private Integer workage;
	private Integer adjustcorpage;
	private String corpworkage;

	/***************************************************************************
	 * <br>
	 * Created on 2010-4-27 10:17:22<br>
	 * 
	 * @return 返回表名称
	 * @author Rocex Wang
	 ***************************************************************************/
	public static String getDefaultTableName() {
		return _TABLE_NAME;
	}

	/***************************************************************************
	 * Created on 2010-1-21 10:46:15<br>
	 * 
	 * @author Rocex Wang
	 ***************************************************************************/
	public PsnOrgVO() {
		super();
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the begindate
	 ***************************************************************************/
	public UFLiteralDate getBegindate() {
		return begindate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the empforms
	 ***************************************************************************/
	public String getEmpforms() {
		return empforms;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the enddate
	 ***************************************************************************/
	public UFLiteralDate getEnddate() {
		return enddate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the endflag
	 ***************************************************************************/
	public UFBoolean getEndflag() {
		return endflag;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-3-25 10:26:12<br>
	 * 
	 * @author Rocex Wang
	 * @return the indoc_source
	 ***************************************************************************/
	public Integer getIndoc_source() {
		return indoc_source;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the indocflag
	 ***************************************************************************/
	public UFBoolean getIndocflag() {
		return indocflag;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-3-25 10:26:12<br>
	 * 
	 * @author Rocex Wang
	 * @return the joinsysdate
	 ***************************************************************************/
	public UFLiteralDate getJoinsysdate() {
		return joinsysdate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the lastflag
	 ***************************************************************************/
	public UFBoolean getLastflag() {
		return lastflag;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the orgrelaid
	 ***************************************************************************/
	public Integer getOrgrelaid() {
		return orgrelaid;
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-4-20 13:57:47<br>
	 * 
	 * @see nc.vo.pub.SuperVO#getParentPKFieldName()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public String getParentPKFieldName() {
		return "pk_psndoc";
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the pk_group
	 ***************************************************************************/
	public String getPk_group() {
		return pk_group;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the pk_operorg
	 ***************************************************************************/
	public String getPk_hrorg() {
		return pk_hrorg;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the pk_org
	 ***************************************************************************/
	public String getPk_org() {
		return pk_org;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:32<br>
	 * 
	 * @author Rocex Wang
	 * @return the pk_psndoc
	 ***************************************************************************/
	public String getPk_psndoc() {
		return pk_psndoc;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the pk_psnorg
	 ***************************************************************************/
	public String getPk_psnorg() {
		return pk_psnorg;
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-4-20 13:57:47<br>
	 * 
	 * @see nc.vo.pub.SuperVO#getPKFieldName()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public String getPKFieldName() {
		return "pk_psnorg";
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the psntype
	 ***************************************************************************/
	public Integer getPsntype() {
		return psntype;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the startpaydate
	 ***************************************************************************/
	public UFLiteralDate getStartpaydate() {
		return startpaydate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @return the stoppaydate
	 ***************************************************************************/
	public UFLiteralDate getStoppaydate() {
		return stoppaydate;
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-4-20 13:57:47<br>
	 * 
	 * @see nc.vo.pub.SuperVO#getTableName()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public String getTableName() {
		return _TABLE_NAME;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param begindate
	 *            the begindate to set
	 ***************************************************************************/
	public void setBegindate(UFLiteralDate begindate) {
		this.begindate = begindate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param empforms
	 *            the empforms to set
	 ***************************************************************************/
	public void setEmpforms(String empforms) {
		this.empforms = empforms;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param enddate
	 *            the enddate to set
	 ***************************************************************************/
	public void setEnddate(UFLiteralDate enddate) {
		this.enddate = enddate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param endflag
	 *            the endflag to set
	 ***************************************************************************/
	public void setEndflag(UFBoolean endflag) {
		this.endflag = endflag;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-3-25 10:26:12<br>
	 * 
	 * @author Rocex Wang
	 * @param indocSource
	 *            the indoc_source to set
	 ***************************************************************************/
	public void setIndoc_source(Integer indocSource) {
		indoc_source = indocSource;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param indocflag
	 *            the indocflag to set
	 ***************************************************************************/
	public void setIndocflag(UFBoolean indocflag) {
		this.indocflag = indocflag;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-3-25 10:26:12<br>
	 * 
	 * @author Rocex Wang
	 * @param joinsysdate
	 *            the joinsysdate to set
	 ***************************************************************************/
	public void setJoinsysdate(UFLiteralDate joinsysdate) {
		this.joinsysdate = joinsysdate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param lastflag
	 *            the lastflag to set
	 ***************************************************************************/
	public void setLastflag(UFBoolean lastflag) {
		this.lastflag = lastflag;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param orgrelaid
	 *            the orgrelaid to set
	 ***************************************************************************/
	public void setOrgrelaid(Integer orgrelaid) {
		this.orgrelaid = orgrelaid;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param pkGroup
	 *            the pk_group to set
	 ***************************************************************************/
	public void setPk_group(String pkGroup) {
		pk_group = pkGroup;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param pkOperorg
	 *            the pk_operorg to set
	 ***************************************************************************/
	public void setPk_hrorg(String pkOperorg) {
		pk_hrorg = pkOperorg;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param pkOrg
	 *            the pk_org to set
	 ***************************************************************************/
	public void setPk_org(String pkOrg) {
		pk_org = pkOrg;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param pkPsndoc
	 *            the pk_psndoc to set
	 ***************************************************************************/
	public void setPk_psndoc(String pkPsndoc) {
		pk_psndoc = pkPsndoc;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param pkPsnorg
	 *            the pk_psnorg to set
	 ***************************************************************************/
	public void setPk_psnorg(String pkPsnorg) {
		pk_psnorg = pkPsnorg;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param psntype
	 *            the psntype to set
	 ***************************************************************************/
	public void setPsntype(Integer psntype) {
		this.psntype = psntype;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param startpaydate
	 *            the startpaydate to set
	 ***************************************************************************/
	public void setStartpaydate(UFLiteralDate startpaydate) {
		this.startpaydate = startpaydate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:46:33<br>
	 * 
	 * @author Rocex Wang
	 * @param stoppaydate
	 *            the stoppaydate to set
	 ***************************************************************************/
	public void setStoppaydate(UFLiteralDate stoppaydate) {
		this.stoppaydate = stoppaydate;
	}

	public Integer getAdjustcorpage() {
		return adjustcorpage;
	}

	public void setAdjustcorpage(Integer adjustcorpage) {
		this.adjustcorpage = adjustcorpage;
	}

	public String getCorpworkage() {
		return corpworkage;
	}

	public void setCorpworkage(String corpworkage) {
		this.corpworkage = corpworkage;
	}

	// Ares.Tank 补充缺失字段 2018年10月23日10:51:47
	public UFBoolean getLoaflag() {
		return loaflag;
	}

	public void setLoaflag(UFBoolean loaflag) {
		this.loaflag = loaflag;
	}

}
