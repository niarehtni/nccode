package nc.vo.hi.psndoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.MultiLangHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.hi.pub.IUnbundleRule;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

/***************************************************************************
 * <br>
 * Created on 2010-1-21 9:49:16<br>
 * 
 * @author Rocex Wang
 ***************************************************************************/
public class PsndocVO extends PsnSuperVO implements IUnbundleRule {
	/** */
	private static final long serialVersionUID = 6611261511587654044L;
	/** 表名 */
	private static final String _TABLE_NAME = "bd_psndoc";

	/** */
	public static final String ADDR = "addr";
	/** */
	public static final String BIRTHDATE = "birthdate";
	/** */
	public static final String BLOODTYPE = "bloodtype";
	/** */
	public static final String CENSUSADDR = "censusaddr";
	/** */
	public static final String CHARACTERRPR = "characterrpr";
	/** */
	public static final String CODE = "code";
	/** */
	public static final String COUNTRY = "country";
	/** */
	public static final String DATAORIGINFLAG = "dataoriginflag";
	/** */
	public static final String DIE_DATE = "die_date";
	/** */
	public static final String DIE_REMARK = "die_remark";
	/** */
	public static final String EDU = "edu";
	/** */
	public static final String EMAIL = "email";
	/** */
	public static final String SECRET_EMAIL = "secret_email";
	/** */
	public static final String ENABLESTATE = "enablestate";
	/** */
	public static final String FAX = "fax";
	/** */
	public static final String FILEADDRESS = "fileaddress";
	/** */
	public static final String HEALTH = "health";
	/** */
	public static final String HOMEPHONE = "homephone";
	/** */
	public static final String ID = "id";
	/** */
	public static final String IDTYPE = "idtype";
	/** */
	public static final String JOINPOLITYDATE = "joinpolitydate";
	/** */
	public static final String JOINWORKDATE = "joinworkdate";
	/** */
	public static final String MARITAL = "marital";
	/** */
	public static final String MARRIAGEDATE = "marriagedate";
	/** */
	public static final String MNECODE = "mnecode";
	/** */
	public static final String MOBILE = "mobile";

	public static final String LASTNAME = "lastname";

	public static final String FIRSTNAME = "firstname";

	/** */
	public static final String NAME = "name";
	/** */
	public static final String NAME2 = "name2";
	/** */
	public static final String NAME3 = "name3";
	/** */
	public static final String NAME4 = "name4";
	/** */
	public static final String NAME5 = "name5";
	/** */
	public static final String NAME6 = "name6";
	/** */
	public static final String NATIONALITY = "nationality";
	/** */
	public static final String NATIVEPLACE = "nativeplace";
	/** */
	public static final String OFFICEPHONE = "officephone";
	/** */
	public static final String PENELAUTH = "penelauth";
	/** */
	public static final String PERMANRESIDE = "permanreside";
	/** */
	public static final String PHOTO = "photo";
	/** */
	public static final String PK_DEGREE = "pk_degree";
	/** */
	public static final String PK_GROUP = "pk_group";
	/** */
	public static final String PK_HRORG = "pk_hrorg";
	/** */
	public static final String PK_ORG = "pk_org";
	/** */
	public static final String PK_PSNDOC = "pk_psndoc";
	/** */
	public static final String POLITY = "polity";
	/** */
	public static final String POSTALCODE = "postalcode";
	/** */
	public static final String PREVIEWPHOTO = "previewphoto";
	/** */
	public static final String PROF = "prof";
	/** */
	public static final String RETIREDATE = "retiredate";
	/** */
	public static final String SEX = "sex";
	/** */
	public static final String SHORTNAME = "shortname";
	/** */
	public static final String TITLETECHPOST = "titletechpost";
	/** */
	public static final String USEDNAME = "usedname";
	/** */
	public static final String ISHISKEYPSN = "ishiskeypsn";
	/** */
	public static final String ISSHOPASSIST = "isshopassist";
	/** */
	public static final String ISCADRE = "iscadre";

	public static final String ISHISLEADER = "ishisleader";

	public static final String AGE = "age";

	public static final String WORKAGE = "workage";

	private String addr;
	private UFLiteralDate birthdate;
	private String bloodtype;
	private String censusaddr;
	private String characterrpr;
	private String code;
	private String country;
	private Integer dataoriginflag;
	private UFLiteralDate die_date;
	private String die_remark;
	private String edu;
	private String email;
	private String secret_email;
	private Integer enablestate = 2;
	private String fax;
	private String fileaddress;
	private String health;
	private String homephone;
	private String id;
	private String idtype;
	private UFLiteralDate joinpolitydate;
	private UFLiteralDate joinworkdate;
	private String marital;
	private UFLiteralDate marriagedate;
	private String mnecode;
	private String mobile;
	private String lastname;
	private String firstname;
	private String name;
	private String name2;
	private String name3;
	private String name4;
	private String name5;
	private String name6;
	private String nationality;
	private String nativeplace;
	private String officephone;
	private String penelauth;
	private String permanreside;
	private Object photo;
	private String pk_degree;
	private String pk_group;
	private String pk_hrorg;
	private String pk_org;
	private String pk_psndoc;
	private String polity;
	private String postalcode;
	private Object previewphoto;
	private String prof;
	private PsnJobVO psnJobVO = new PsnJobVO(); // 人员最新任职vo
	private PsnOrgVO psnOrgVO = new PsnOrgVO(); // 当前组织关系vo
	private UFLiteralDate retiredate;
	private Integer sex; // 1男2女
	private String shortname;
	private String titletechpost;
	private String usedname;
	private UFBoolean ishiskeypsn;
	private UFBoolean isshopassist;
	private UFBoolean isuapmanage;// 是否为UAP管理人员
	private UFBoolean iscadre;// 是否干部
	private UFBoolean ishisleader;// 是否历史干部
	private String age;
	private String workage;

	/**
	 * <br>
	 * Created on 2013-12-12 9:18:03<br>
	 * 
	 * @author caiqm
	 * @return the ishisleader
	 */
	public UFBoolean getIshisleader() {
		return ishisleader;
	}

	/**
	 * <br>
	 * Created on 2013-12-12 9:18:03<br>
	 * 
	 * @author caiqm
	 * @param ishisleader
	 *            the ishisleader to set
	 */
	public void setIshisleader(UFBoolean ishisleader) {
		this.ishisleader = ishisleader;
	}

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
	 * Created on 2010-1-21 9:50:14<br>
	 * 
	 * @author Rocex Wang
	 ***************************************************************************/
	public PsndocVO() {
		super();
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the addr
	 ***************************************************************************/
	public String getAddr() {
		return addr;
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-1-21 15:41:14<br>
	 * 
	 * @see nc.vo.hi.pub.IUnbundleRule#getAllVO()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public SuperVO[] getAllVO() {
		return new SuperVO[] { this, psnJobVO, psnOrgVO };
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-1-21 15:21:32<br>
	 * 
	 * @see nc.vo.pub.SuperVO#getAttributeNames()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public String[] getAttributeNames() {
		String strAttrNames[] = super.getAttributeNames();

		if (strAttrNames == null) {
			return null;
		}

		List<String> listAttrNames = new ArrayList<String>();

		for (String strAttrName : strAttrNames) {
			strAttrName = strAttrName.toLowerCase();

			if (strAttrName.startsWith("hi_psndoc_") || strAttrName.startsWith("hi_psn")
					|| strAttrName.equals("psnjobvo") || strAttrName.equals("psnorgvo")) {
				continue;
			}

			listAttrNames.add(strAttrName);
		}

		return listAttrNames.toArray(new String[listAttrNames.size()]);
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-1-21 9:59:31<br>
	 * 
	 * @see nc.vo.pub.SuperVO#getAttributeValue(java.lang.String)
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public Object getAttributeValue(String strAttribute) {
		SuperVO superVO = getVOByRule(strAttribute);
		String strFieldName = getFieldNameByRule(strAttribute);
		/**
		 * 客开显示：员工信息->工作记录->部门->部门code nc节点参照只能向下搜索两层，对于三层的参照，无法显示值
		 * 2018年6月12日11:50:52
		 */
		if (strAttribute != null && "hi_psnjob_pk_dept.code".equals(strAttribute)) {
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			String sql = "SELECT ORG_DEPT.code " + "from hi_PSNJOB "
					+ "LEFT JOIN ORG_DEPT on hi_PSNJOB.PK_DEPT=ORG_DEPT.PK_DEPT " + "where pk_psnjob=\'"
					+ superVO.getAttributeValue(superVO.getPKFieldName()) + "\'";
			try {
				Map resultMap = (HashMap) iUAPQueryBS.executeQuery(sql, new MapProcessor());
				return resultMap == null ? null : resultMap.get("code");
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
		/**
		 * 2018年6月12日11:51:18
		 */
		if (superVO == this) {
			return super.getAttributeValue(strFieldName);
		}

		return superVO.getAttributeValue(strFieldName);
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the birthdate
	 ***************************************************************************/
	public UFLiteralDate getBirthdate() {
		return birthdate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the bloodtype
	 ***************************************************************************/
	public String getBloodtype() {
		return bloodtype;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the censusaddr
	 ***************************************************************************/
	public String getCensusaddr() {
		return censusaddr;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the characterrpr
	 ***************************************************************************/
	public String getCharacterrpr() {
		return characterrpr;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the code
	 ***************************************************************************/
	public String getCode() {
		return code;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the country
	 ***************************************************************************/
	public String getCountry() {
		return country;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-5-26 10:38:48<br>
	 * 
	 * @author Rocex Wang
	 * @return the dataoriginflag
	 ***************************************************************************/
	public Integer getDataoriginflag() {
		return dataoriginflag;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-3-25 9:53:28<br>
	 * 
	 * @author Rocex Wang
	 * @return the die_date
	 ***************************************************************************/
	public UFLiteralDate getDie_date() {
		return die_date;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-3-25 9:53:28<br>
	 * 
	 * @author Rocex Wang
	 * @return the die_remark
	 ***************************************************************************/
	public String getDie_remark() {
		return die_remark;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the edu
	 ***************************************************************************/
	public String getEdu() {
		return edu;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the email
	 ***************************************************************************/
	public String getEmail() {
		return email;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-5-26 10:38:53<br>
	 * 
	 * @author Rocex Wang
	 * @return the enablestate
	 ***************************************************************************/
	public Integer getEnablestate() {
		return enablestate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the fax
	 ***************************************************************************/
	public String getFax() {
		return fax;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 9:59:23<br>
	 * 
	 * @param strFieldName
	 * @return String
	 * @author Rocex Wang
	 ***************************************************************************/
	@Override
	public String getFieldNameByRule(String strFieldName) {
		String strLowerCaseFieldName = strFieldName.toLowerCase();

		if (strLowerCaseFieldName.startsWith("hi_psnorg_") || strLowerCaseFieldName.startsWith("hi_psnjob_")
				|| strLowerCaseFieldName.startsWith("bd_psndoc_")) {
			return strFieldName.substring(10);
		}

		return strFieldName;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the fileaddress
	 ***************************************************************************/
	public String getFileaddress() {
		return fileaddress;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the health
	 ***************************************************************************/
	public String getHealth() {
		return health;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the homephone
	 ***************************************************************************/
	public String getHomephone() {
		return homephone;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the id
	 ***************************************************************************/
	public String getId() {
		return id;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the joinpolitydate
	 ***************************************************************************/
	public UFLiteralDate getJoinpolitydate() {
		return joinpolitydate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the joinworkdate
	 ***************************************************************************/
	public UFLiteralDate getJoinworkdate() {
		return joinworkdate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the marital
	 ***************************************************************************/
	public String getMarital() {
		return marital;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the marriagedate
	 ***************************************************************************/
	public UFLiteralDate getMarriagedate() {
		return marriagedate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the mnecode
	 ***************************************************************************/
	public String getMnecode() {
		return mnecode;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the mobile
	 ***************************************************************************/
	public String getMobile() {
		return mobile;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2011-3-21 14:50:26<br>
	 * 
	 * @return String
	 * @author sunpenga
	 ***************************************************************************/
	public String getMultiLangName() {
		return MultiLangHelper.getName(this);
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the name
	 ***************************************************************************/
	public String getName() {
		return name;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the name2
	 ***************************************************************************/
	public String getName2() {
		return name2;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the name3
	 ***************************************************************************/
	public String getName3() {
		return name3;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2011-3-21 14:49:39<br>
	 * 
	 * @author Rocex Wang
	 * @return the name4
	 ***************************************************************************/
	public String getName4() {
		return name4;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2011-3-21 14:49:44<br>
	 * 
	 * @author Rocex Wang
	 * @return the name5
	 ***************************************************************************/
	public String getName5() {
		return name5;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2011-3-21 14:49:50<br>
	 * 
	 * @author Rocex Wang
	 * @return the name6
	 ***************************************************************************/
	public String getName6() {
		return name6;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the nationality
	 ***************************************************************************/
	public String getNationality() {
		return nationality;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the nativeplace
	 ***************************************************************************/
	public String getNativeplace() {
		return nativeplace;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the officephone
	 ***************************************************************************/
	public String getOfficephone() {
		return officephone;
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-1-21 15:41:14<br>
	 * 
	 * @see nc.vo.hi.pub.IUnbundleRule#getOtherVO()
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public GeneralVO getOtherVO() {
		return null;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the penelauth
	 ***************************************************************************/
	public String getPenelauth() {
		return penelauth;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the permanreside
	 ***************************************************************************/
	public String getPermanreside() {
		return permanreside;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the photo
	 ***************************************************************************/
	public Object getPhoto() {
		return photo;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the pk_degree
	 ***************************************************************************/
	public String getPk_degree() {
		return pk_degree;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the pk_group
	 ***************************************************************************/
	public String getPk_group() {
		return pk_group;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-3-25 9:53:28<br>
	 * 
	 * @author Rocex Wang
	 * @return the pk_hrorg
	 ***************************************************************************/
	public String getPk_hrorg() {
		return pk_hrorg;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the pk_org
	 ***************************************************************************/
	public String getPk_org() {
		return pk_org;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the pk_psndoc
	 ***************************************************************************/
	public String getPk_psndoc() {
		return pk_psndoc;
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-1-21 9:50:34<br>
	 * 
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public String getPKFieldName() {
		return "pk_psndoc";
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the polity
	 ***************************************************************************/
	public String getPolity() {
		return polity;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the postalcode
	 ***************************************************************************/
	public String getPostalcode() {
		return postalcode;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2011-3-21 14:50:37<br>
	 * 
	 * @return Object
	 * @author Rocex Wang
	 ***************************************************************************/
	public Object getPreviewphoto() {
		return previewphoto;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the prof
	 ***************************************************************************/
	public String getProf() {
		return prof;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the psnJobVO
	 ***************************************************************************/
	public PsnJobVO getPsnJobVO() {
		return psnJobVO;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the psnOrgVO
	 ***************************************************************************/
	public PsnOrgVO getPsnOrgVO() {
		return psnOrgVO;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the retiredate
	 ***************************************************************************/
	public UFLiteralDate getRetiredate() {
		return retiredate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the sex
	 ***************************************************************************/
	public Integer getSex() {
		return sex;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-4-13 9:44:45<br>
	 * 
	 * @author Rocex Wang
	 * @return the shortname
	 ***************************************************************************/
	public String getShortname() {
		return shortname;
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-1-21 9:50:42<br>
	 * 
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public String getTableName() {
		return _TABLE_NAME;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the titletechpost
	 ***************************************************************************/
	public String getTitletechpost() {
		return titletechpost;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @return the usedname
	 ***************************************************************************/
	public String getUsedname() {
		return usedname;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 9:56:41<br>
	 * 
	 * @param strFieldName
	 * @return SuperVO
	 * @author Rocex Wang
	 ***************************************************************************/
	@Override
	public SuperVO getVOByRule(String strFieldName) {
		if (strFieldName.startsWith("hi_psnorg.") || strFieldName.startsWith("hi_psnorg_")) {
			return psnOrgVO;
		} else if (strFieldName.startsWith("hi_psnjob.") || strFieldName.startsWith("hi_psnjob_")) {
			return psnJobVO;
		} else {
			return this;
		}
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param addr
	 *            the addr to set
	 ***************************************************************************/
	public void setAddr(String addr) {
		this.addr = addr;
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-1-21 9:56:58<br>
	 * 
	 * @see nc.vo.pub.SuperVO#setAttributeValue(java.lang.String,
	 *      java.lang.Object)
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public void setAttributeValue(String strAttribute, Object objValue) {
		SuperVO superVO = getVOByRule(strAttribute);
		String strFieldName = getFieldNameByRule(strAttribute);

		if (superVO == this) {
			super.setAttributeValue(strFieldName, objValue);
		} else {
			superVO.setAttributeValue(strFieldName, objValue);
		}
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param birthdate
	 *            the birthdate to set
	 ***************************************************************************/
	public void setBirthdate(UFLiteralDate birthdate) {
		this.birthdate = birthdate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param bloodtype
	 *            the bloodtype to set
	 ***************************************************************************/
	public void setBloodtype(String bloodtype) {
		this.bloodtype = bloodtype;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param censusaddr
	 *            the censusaddr to set
	 ***************************************************************************/
	public void setCensusaddr(String censusaddr) {
		this.censusaddr = censusaddr;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param characterrpr
	 *            the characterrpr to set
	 ***************************************************************************/
	public void setCharacterrpr(String characterrpr) {
		this.characterrpr = characterrpr;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param code
	 *            the code to set
	 ***************************************************************************/
	public void setCode(String code) {
		this.code = code;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param country
	 *            the country to set
	 ***************************************************************************/
	public void setCountry(String country) {
		this.country = country;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-5-26 10:38:48<br>
	 * 
	 * @author Rocex Wang
	 * @param dataoriginflag
	 *            the dataoriginflag to set
	 ***************************************************************************/
	public void setDataoriginflag(Integer dataoriginflag) {
		this.dataoriginflag = dataoriginflag;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-3-25 9:53:28<br>
	 * 
	 * @author Rocex Wang
	 * @param dieDate
	 *            the die_date to set
	 ***************************************************************************/
	public void setDie_date(UFLiteralDate dieDate) {
		die_date = dieDate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-3-25 9:53:28<br>
	 * 
	 * @author Rocex Wang
	 * @param dieRemark
	 *            the die_remark to set
	 ***************************************************************************/
	public void setDie_remark(String dieRemark) {
		die_remark = dieRemark;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param edu
	 *            the edu to set
	 ***************************************************************************/
	public void setEdu(String edu) {
		this.edu = edu;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param email
	 *            the email to set
	 ***************************************************************************/
	public void setEmail(String email) {
		this.email = email;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-5-26 10:38:53<br>
	 * 
	 * @author Rocex Wang
	 * @param enablestate
	 *            the enablestate to set
	 ***************************************************************************/
	public void setEnablestate(Integer enablestate) {
		this.enablestate = enablestate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param fax
	 *            the fax to set
	 ***************************************************************************/
	public void setFax(String fax) {
		this.fax = fax;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param fileaddress
	 *            the fileaddress to set
	 ***************************************************************************/
	public void setFileaddress(String fileaddress) {
		this.fileaddress = fileaddress;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param health
	 *            the health to set
	 ***************************************************************************/
	public void setHealth(String health) {
		this.health = health;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param homephone
	 *            the homephone to set
	 ***************************************************************************/
	public void setHomephone(String homephone) {
		this.homephone = homephone;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param id
	 *            the id to set
	 ***************************************************************************/
	public void setId(String id) {
		this.id = id;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param joinpolitydate
	 *            the joinpolitydate to set
	 ***************************************************************************/
	public void setJoinpolitydate(UFLiteralDate joinpolitydate) {
		this.joinpolitydate = joinpolitydate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param joinworkdate
	 *            the joinworkdate to set
	 ***************************************************************************/
	public void setJoinworkdate(UFLiteralDate joinworkdate) {
		this.joinworkdate = joinworkdate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param marital
	 *            the marital to set
	 ***************************************************************************/
	public void setMarital(String marital) {
		this.marital = marital;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param marriagedate
	 *            the marriagedate to set
	 ***************************************************************************/
	public void setMarriagedate(UFLiteralDate marriagedate) {
		this.marriagedate = marriagedate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param mnecode
	 *            the mnecode to set
	 ***************************************************************************/
	public void setMnecode(String mnecode) {
		this.mnecode = mnecode;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param mobile
	 *            the mobile to set
	 ***************************************************************************/
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param name
	 *            the name to set
	 ***************************************************************************/
	public void setName(String name) {
		this.name = name;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param name2
	 *            the name2 to set
	 ***************************************************************************/
	public void setName2(String name2) {
		this.name2 = name2;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param name3
	 *            the name3 to set
	 ***************************************************************************/
	public void setName3(String name3) {
		this.name3 = name3;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2011-3-21 14:49:39<br>
	 * 
	 * @author Rocex Wang
	 * @param name4
	 *            the name4 to set
	 ***************************************************************************/
	public void setName4(String name4) {
		this.name4 = name4;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2011-3-21 14:49:44<br>
	 * 
	 * @author Rocex Wang
	 * @param name5
	 *            the name5 to set
	 ***************************************************************************/
	public void setName5(String name5) {
		this.name5 = name5;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2011-3-21 14:49:50<br>
	 * 
	 * @author Rocex Wang
	 * @param name6
	 *            the name6 to set
	 ***************************************************************************/
	public void setName6(String name6) {
		this.name6 = name6;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param nationality
	 *            the nationality to set
	 ***************************************************************************/
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param nativeplace
	 *            the nativeplace to set
	 ***************************************************************************/
	public void setNativeplace(String nativeplace) {
		this.nativeplace = nativeplace;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param officephone
	 *            the officephone to set
	 ***************************************************************************/
	public void setOfficephone(String officephone) {
		this.officephone = officephone;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param penelauth
	 *            the penelauth to set
	 ***************************************************************************/
	public void setPenelauth(String penelauth) {
		this.penelauth = penelauth;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param permanreside
	 *            the permanreside to set
	 ***************************************************************************/
	public void setPermanreside(String permanreside) {
		this.permanreside = permanreside;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param photo
	 *            the photo to set
	 ***************************************************************************/
	public void setPhoto(Object photo) {
		this.photo = photo;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param pk_degree
	 *            the pk_degree to set
	 ***************************************************************************/
	public void setPk_degree(String pk_degree) {
		this.pk_degree = pk_degree;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
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
	 * Created on 2010-3-25 9:53:28<br>
	 * 
	 * @author Rocex Wang
	 * @param pkHrorg
	 *            the pk_hrorg to set
	 ***************************************************************************/
	public void setPk_hrorg(String pkHrorg) {
		pk_hrorg = pkHrorg;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
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
	 * Created on 2010-1-21 10:03:22<br>
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
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param polity
	 *            the polity to set
	 ***************************************************************************/
	public void setPolity(String polity) {
		this.polity = polity;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param postalcode
	 *            the postalcode to set
	 ***************************************************************************/
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2011-3-21 14:50:11<br>
	 * 
	 * @param previewphoto
	 * @author sunpenga
	 ***************************************************************************/
	public void setPreviewphoto(Object previewphoto) {
		this.previewphoto = previewphoto;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param prof
	 *            the prof to set
	 ***************************************************************************/
	public void setProf(String prof) {
		this.prof = prof;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param psnJobVO
	 *            the psnJobVO to set
	 ***************************************************************************/
	public void setPsnJobVO(PsnJobVO psnJobVO) {
		this.psnJobVO = psnJobVO;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param psnOrgVO
	 *            the psnOrgVO to set
	 ***************************************************************************/
	public void setPsnOrgVO(PsnOrgVO psnOrgVO) {
		this.psnOrgVO = psnOrgVO;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param retiredate
	 *            the retiredate to set
	 ***************************************************************************/
	public void setRetiredate(UFLiteralDate retiredate) {
		this.retiredate = retiredate;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param sex
	 *            the sex to set
	 ***************************************************************************/
	public void setSex(Integer sex) {
		this.sex = sex;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-4-13 9:44:45<br>
	 * 
	 * @author Rocex Wang
	 * @param shortname
	 *            the shortname to set
	 ***************************************************************************/
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param titletechpost
	 *            the titletechpost to set
	 ***************************************************************************/
	public void setTitletechpost(String titletechpost) {
		this.titletechpost = titletechpost;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-21 10:03:22<br>
	 * 
	 * @author Rocex Wang
	 * @param usedname
	 *            the usedname to set
	 ***************************************************************************/
	public void setUsedname(String usedname) {
		this.usedname = usedname;
	}

	/**
	 * 得到pk_psndoc+pk_psnorg+pk_psnjob联合主键
	 * 
	 * @return String
	 */
	public String getUnionPk() {
		if (getPsnOrgVO() != null && getPsnOrgVO().getPsntype() == 1) {
			// 相关人员 人员主键+组织关系主键联合唯一
			return getPk_psndoc() + getPsnOrgVO().getPk_psnorg();
		}
		return getPk_psndoc() + getPsnOrgVO().getPk_psnorg() + getPsnJobVO().getPk_psnjob();
	}

	public void setSecret_email(String secret_email) {
		this.secret_email = secret_email;
	}

	public String getSecret_email() {
		return secret_email;
	}

	public void setIshiskeypsn(UFBoolean ishiskeypsn) {
		this.ishiskeypsn = ishiskeypsn;
	}

	public UFBoolean getIshiskeypsn() {
		return ishiskeypsn;
	}

	public void setIsshopassist(UFBoolean isshopassist) {
		this.isshopassist = isshopassist;
	}

	public UFBoolean getIsshopassist() {
		return isshopassist;
	}

	public UFBoolean getIsuapmanage() {
		return isuapmanage;
	}

	public void setIsuapmanage(UFBoolean isuapmanage) {
		this.isuapmanage = isuapmanage;
	}

	public String getIdtype() {
		return idtype;
	}

	public void setIdtype(String idtype) {
		this.idtype = idtype;
	}

	public UFBoolean getIscadre() {
		return iscadre;
	}

	public void setIscadre(UFBoolean iscadre) {
		this.iscadre = iscadre;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getWorkage() {
		return workage;
	}

	public void setWorkage(String workage) {
		this.workage = workage;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

}
