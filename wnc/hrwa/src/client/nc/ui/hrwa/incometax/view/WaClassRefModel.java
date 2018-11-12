package nc.ui.hrwa.incometax.view;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.ui.bd.ref.AbstractRefGridTreeModel;
import nc.vo.hr.pub.HRCommonConstants;
import nc.vo.pub.BusinessException;
import nc.vo.wa.classpower.ClassPowerUtil;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.StringUtils;

/**
 * @author ward.wong
 * @date 20180126
 * @version v1.0
 * @date 20180227 添加是否福委和申報憑單格式過濾條件
 * @功能描述  薪资类别参照适合申报明细档生成时使用
 *
 */
public class WaClassRefModel extends AbstractRefGridTreeModel {

	private String otherEnvWhere;

	private String businessCon = " showflag = 'Y' ";
	
	private String[] pk_orgs=null;
	
	private String isferry="N";//是否福委
	
	private String[] declareform;//申报凭单格式



	public String getBusinessCon() {
		return businessCon;
	}
	public void setBusinessCon(String businessCon) {
		this.businessCon = businessCon;
	}
	/**
	 * WaClassRefModel 构造子注解。
	 */
	public WaClassRefModel() {
		super();
		reset();
	}
	public WaClassRefModel(String name){
		super();
		reset();
	}

	@Override
	public void reset() {
		setRootName(ResHelper.getString("60130waclass", "060130waclass0131")/*
																			 * @res
																			 * "国家区域"
																			 */);
		setClassFieldCode(new String[] { "code", "name", "pk_country"});
		setClassWherePart(" pk_country in ( select pk_country from hr_globalcountry where enable = 'Y') ");
		setFatherField("pk_country");
		setChildField("pk_country");
		setClassJoinField("pk_country");

		setClassTableName("bd_countryzone");
		setClassDefaultFieldCount(2);
		setClassDataPower(true);
		setDocJoinField("wa_waclass.pk_country");
		//this.setRootVisible(false);
		//setClassJoinValue("0001Z010000000079UJJ");
		setClassLocatePK(HRCommonConstants.PK_COUNTRY); //默认选中中国
		setExactOn(true);
//		begin 20150723 xiejie3 参照的缓存键值是用bd_refinfo表的name字段值，目前很多参照的refModel中没有设置RefNodeName的值
		setRefNodeName("薪资类别"); /*-=notranslate=-*/
// 		end
	}


	@Override
	public String getEnvWherePart(){
		InvocationInfoProxy.getInstance().getUserId();
		WaLoginContext context = new WaLoginContext();
		context.setPk_group(getPk_group());
		context.setPk_org(getPk_org());
		context.setPk_loginUser(modelHandler.getPk_user());
		InSQLCreator isc = new InSQLCreator();
		String inPsndocSql="";
		String fixWhere = "";
		try {
			if(pk_orgs!=null&&pk_orgs.length>0)
			inPsndocSql = isc.getInSQL(pk_orgs);
		
			//by he申报格式单选变多选
			String inDeclaretypeSql = isc.getInSQL(getDeclareform());
			fixWhere=" wa_waclass.pk_org in (" + inPsndocSql +") and wa_waclass.isferry='"+getIsferry()+"' and declareform in("+inDeclaretypeSql+") " 
			// 2015-10-14 zhousze NCdp205515580与需求测试沟通确认，在参照薪资方案时，如果该方案没有期间（现在这种情况只发现
			// 汇总方案是这样，如果以后还发现类似的方案可以在这过滤），就不能过滤出来。 begin
					+ " and wa_waclass.pk_periodscheme != '~' ";
		} catch (BusinessException e) {
			Logger.error(e);
		}
		// end
		if (!getPk_group().equals(getPk_org())) {
			fixWhere = fixWhere + " and wa_waclass.pk_wa_class in ("
					+ ClassPowerUtil.getClassower(context) + ")";
		}
		if(getOtherEnvWhere()!=null&&!getOtherEnvWhere().trim().equals(""))
		{
			fixWhere+=" and "+getOtherEnvWhere();
		}

		if(!StringUtils.isBlank(getBusinessCon()))
		{
			fixWhere+=" and "+getBusinessCon();
		}

		return fixWhere;
	}

	public String getOtherEnvWhere(){
		return otherEnvWhere;
	}


	public void setOtherEnvWhere(String otherEnvWhere)
	{
		this.otherEnvWhere=otherEnvWhere;
	}


	@Override
	public java.lang.String getCodingRule() {
		return "2212";
	}

	@Override
	public boolean isAddEnableStateWherePart() {
		return true;
	}
	/**
	 * 是否显示停用的数据
	 *
	 * @param isEnable
	 * @return
	 */
	@Override
	protected String getDisableDataWherePart(boolean isDisableDataShow) {

		if (isDisableDataShow) {
			return null;
		} else {
			return "stopflag = 'N'";
		}
	}

	/**
	 * @author zhoucx on 2009-12-1
	 * @see nc.ui.bd.ref.AbstractRefModel#getOrderPart()
	 */
	@Override
	public String getOrderPart() {
		//请勿用wa_waclass.name排序。除非对处理多语的sql进行调整。
		//编码是唯一，没必要再用name排序。用name之后，英文环境且SqlServer库时，有关多语的sql语句出错。
		return "wa_waclass.pk_org, wa_waclass.code";//, wa_waclass.name
	}

	/**
	 * getDefaultFieldCount 方法注解。
	 */
	@Override
	public int getDefaultFieldCount() {
		return 4;
	}
	/**
	 * 参照数据库字段名数组
	 * 创建日期：(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldCode() {
		return new String[] {"wa_waclass.code","wa_waclass.name", "wa_waclass.cyear", "wa_waclass.cperiod","wa_waclass.mutipleflag"};
	}
	/**
	 * 和数据库字段名数组对应的中文名称数组
	 * 创建日期：(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldName() {
		return new String[] {ResHelper.getString("6013salarypmt","06013salarypmt0278")/*@res "薪资方案编码"*/,ResHelper.getString("6013salarypmt","06013salarypmt0279")/*@res "薪资方案名称"*/,ResHelper.getString("6013salarypmt","06013salarypmt0280")/*@res "最新年度"*/,ResHelper.getString("6013salarypmt","06013salarypmt0281")/*@res "最新期间"*/,ResHelper.getString("6013salarypmt","06013salarypmt0282")/*@res "是否多次发放"*/};
	}
	/**
	 * 参照数据库字段名数组
	 * 创建日期：(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getHiddenFieldCode() {
		return new String[]{"wa_waclass.pk_wa_class","wa_waclass.pk_org"};
	}
	/**
	 * 要返回的主键字段名i.e. pk_deptdoc
	 * 创建日期：(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public String getPkFieldCode() {
		return "wa_waclass.pk_wa_class";
	}
	/**
	 * 参照标题
	 * 创建日期：(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public String getRefNameField() {
		return "wa_waclass.name";
	}

	@Override
	public String getRefCodeField(){
		return "wa_waclass.code";
	}
	/**
	 * 参照标题
	 * 创建日期：(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public String getRefTitle() {
		return ResHelper.getString("6013salarypmt","06013salarypmt0283")/*@res "薪资方案参照"*/;
	}
	/**
	 * 参照数据库表或者视图名
	 * 创建日期：(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public String getTableName() {
		return "wa_waclass";
	}
	public String[] getPk_orgs() {
		return pk_orgs;
	}
	public void setPk_orgs(String[] pk_orgs) {
		this.pk_orgs = pk_orgs;
	}
	public String getIsferry() {
		return isferry;
	}
	public void setIsferry(String isferry) {
		this.isferry = isferry;
	}
	public String[] getDeclareform() {
		return declareform;
	}
	public void setDeclareform(String[] declareform) {
		this.declareform = declareform;
	}
	
	
}