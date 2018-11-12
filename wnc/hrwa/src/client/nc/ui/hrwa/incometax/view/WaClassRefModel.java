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
 * @date 20180227 ����Ƿ�ί�����{�θ�ʽ�^�V�l��
 * @��������  н���������ʺ��걨��ϸ������ʱʹ��
 *
 */
public class WaClassRefModel extends AbstractRefGridTreeModel {

	private String otherEnvWhere;

	private String businessCon = " showflag = 'Y' ";
	
	private String[] pk_orgs=null;
	
	private String isferry="N";//�Ƿ�ί
	
	private String[] declareform;//�걨ƾ����ʽ



	public String getBusinessCon() {
		return businessCon;
	}
	public void setBusinessCon(String businessCon) {
		this.businessCon = businessCon;
	}
	/**
	 * WaClassRefModel ������ע�⡣
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
																			 * "��������"
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
		setClassLocatePK(HRCommonConstants.PK_COUNTRY); //Ĭ��ѡ���й�
		setExactOn(true);
//		begin 20150723 xiejie3 ���յĻ����ֵ����bd_refinfo���name�ֶ�ֵ��Ŀǰ�ܶ���յ�refModel��û������RefNodeName��ֵ
		setRefNodeName("н�����"); /*-=notranslate=-*/
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
		
			//by he�걨��ʽ��ѡ���ѡ
			String inDeclaretypeSql = isc.getInSQL(getDeclareform());
			fixWhere=" wa_waclass.pk_org in (" + inPsndocSql +") and wa_waclass.isferry='"+getIsferry()+"' and declareform in("+inDeclaretypeSql+") " 
			// 2015-10-14 zhousze NCdp205515580��������Թ�ͨȷ�ϣ��ڲ���н�ʷ���ʱ������÷���û���ڼ䣨�����������ֻ����
			// ���ܷ���������������Ժ󻹷������Ƶķ�������������ˣ����Ͳ��ܹ��˳����� begin
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
	 * �Ƿ���ʾͣ�õ�����
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
		//������wa_waclass.name���򡣳��ǶԴ�������sql���е�����
		//������Ψһ��û��Ҫ����name������name֮��Ӣ�Ļ�����SqlServer��ʱ���йض����sql������
		return "wa_waclass.pk_org, wa_waclass.code";//, wa_waclass.name
	}

	/**
	 * getDefaultFieldCount ����ע�⡣
	 */
	@Override
	public int getDefaultFieldCount() {
		return 4;
	}
	/**
	 * �������ݿ��ֶ�������
	 * �������ڣ�(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldCode() {
		return new String[] {"wa_waclass.code","wa_waclass.name", "wa_waclass.cyear", "wa_waclass.cperiod","wa_waclass.mutipleflag"};
	}
	/**
	 * �����ݿ��ֶ��������Ӧ��������������
	 * �������ڣ�(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getFieldName() {
		return new String[] {ResHelper.getString("6013salarypmt","06013salarypmt0278")/*@res "н�ʷ�������"*/,ResHelper.getString("6013salarypmt","06013salarypmt0279")/*@res "н�ʷ�������"*/,ResHelper.getString("6013salarypmt","06013salarypmt0280")/*@res "�������"*/,ResHelper.getString("6013salarypmt","06013salarypmt0281")/*@res "�����ڼ�"*/,ResHelper.getString("6013salarypmt","06013salarypmt0282")/*@res "�Ƿ��η���"*/};
	}
	/**
	 * �������ݿ��ֶ�������
	 * �������ڣ�(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String[] getHiddenFieldCode() {
		return new String[]{"wa_waclass.pk_wa_class","wa_waclass.pk_org"};
	}
	/**
	 * Ҫ���ص������ֶ���i.e. pk_deptdoc
	 * �������ڣ�(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public String getPkFieldCode() {
		return "wa_waclass.pk_wa_class";
	}
	/**
	 * ���ձ���
	 * �������ڣ�(01-4-4 0:57:23)
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
	 * ���ձ���
	 * �������ڣ�(01-4-4 0:57:23)
	 * @return java.lang.String
	 */
	@Override
	public String getRefTitle() {
		return ResHelper.getString("6013salarypmt","06013salarypmt0283")/*@res "н�ʷ�������"*/;
	}
	/**
	 * �������ݿ�������ͼ��
	 * �������ڣ�(01-4-4 0:57:23)
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