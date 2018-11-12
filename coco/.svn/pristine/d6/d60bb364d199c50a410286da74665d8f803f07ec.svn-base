package nc.ui.wa.datainterface;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.hr.utils.ResHelper;
import nc.ui.bd.ref.AbstractRefGridTreeModel;
import nc.vo.wa.classpower.ClassPowerUtil;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.StringUtils;

public class WaClassRefModel4ITR extends AbstractRefGridTreeModel {
	private String otherEnvWhere;
	private String businessCon = " showflag = 'Y' ";
	private String[] pk_orgs = null;

	public String getBusinessCon() {
		return this.businessCon;
	}

	public void setBusinessCon(String businessCon) {
		this.businessCon = businessCon;
	}

	public WaClassRefModel4ITR() {
		reset();
	}

	public WaClassRefModel4ITR(String name) {
		reset();
	}

	public void reset() {
		setRootName(ResHelper.getString("60130waclass", "060130waclass0131"));

		setClassFieldCode(new String[] { "code", "name", "pk_country" });
		setClassWherePart(" pk_country in ( select hrc.pk_country from hr_globalcountry hrc INNER JOIN bd_countryzone zone ON hrc.pk_country = zone.pk_country WHERE hrc.enable = 'Y' AND zone.code='TW') ");
		setFatherField("pk_country");
		setChildField("pk_country");
		setClassJoinField("pk_country");

		setClassTableName("bd_countryzone");
		setClassDefaultFieldCount(2);
		setClassDataPower(true);
		setDocJoinField("wa_waclass.pk_country");

		setClassLocatePK("0001Z010000000079UJJ");
		setExactOn(true);
	}

	public String getEnvWherePart() {
		InvocationInfoProxy.getInstance().getUserId();
		WaLoginContext context = new WaLoginContext();
		context.setPk_group(getPk_group());
		context.setPk_org(getPk_org());
		context.setPk_loginUser(this.modelHandler.getPk_user());
		String fixWhere = "";

		if (this.getPk_orgs() == null || this.getPk_orgs().length == 0) {
			fixWhere = " wa_waclass.pk_org = '" + getPk_org() + "'";
		} else {
			for (String pk_org : this.getPk_orgs()) {
				if (fixWhere != "") {
					fixWhere += ",'" + pk_org + "'";
				} else {
					fixWhere = "'" + pk_org + "'";
				}
			}
			fixWhere = " wa_waclass.pk_org in (" + fixWhere + ")";
		}

		if (!getPk_group().equals(getPk_org())) {
			fixWhere = fixWhere + " and wa_waclass.pk_wa_class in ("
					+ ClassPowerUtil.getClassower(context) + ")";
		}

		if ((getOtherEnvWhere() != null)
				&& (!getOtherEnvWhere().trim().equals(""))) {
			fixWhere = fixWhere + " and " + getOtherEnvWhere();
		}

		if (!StringUtils.isBlank(getBusinessCon())) {
			fixWhere = fixWhere + " and " + getBusinessCon();
		}

		return fixWhere;
	}

	public String getOtherEnvWhere() {
		return this.otherEnvWhere;
	}

	public void setOtherEnvWhere(String otherEnvWhere) {
		this.otherEnvWhere = otherEnvWhere;
	}

	public String getCodingRule() {
		return "2212";
	}

	public boolean isAddEnableStateWherePart() {
		return true;
	}

	protected String getDisableDataWherePart(boolean isDisableDataShow) {
		if (isDisableDataShow) {
			return null;
		}
		return "stopflag = 'N'";
	}

	public String getOrderPart() {
		return "wa_waclass.pk_org, wa_waclass.code";
	}

	public int getDefaultFieldCount() {
		return 4;
	}

	public String[] getFieldCode() {
		return new String[] { "wa_waclass.code", "wa_waclass.name",
				"wa_waclass.cyear", "wa_waclass.cperiod",
				"wa_waclass.mutipleflag" };
	}

	public String[] getFieldName() {
		return new String[] {
				ResHelper.getString("6013salarypmt", "06013salarypmt0278"),
				ResHelper.getString("6013salarypmt", "06013salarypmt0279"),
				ResHelper.getString("6013salarypmt", "06013salarypmt0280"),
				ResHelper.getString("6013salarypmt", "06013salarypmt0281"),
				ResHelper.getString("6013salarypmt", "06013salarypmt0282") };
	}

	public String[] getHiddenFieldCode() {
		return new String[] { "wa_waclass.pk_wa_class", "wa_waclass.pk_org" };
	}

	public String getPkFieldCode() {
		return "wa_waclass.pk_wa_class";
	}

	public String getRefNameField() {
		return "wa_waclass.name";
	}

	public String getRefCodeField() {
		return "wa_waclass.code";
	}

	public String getRefTitle() {
		return ResHelper.getString("6013salarypmt", "06013salarypmt0283");
	}

	public String getTableName() {
		return "wa_waclass";
	}

	public String[] getPk_orgs() {
		return pk_orgs;
	}

	public void setPk_orgs(String[] pk_orgs) {
		if (pk_orgs != null && pk_orgs.length == 1) {
			this.setPk_org(pk_orgs[0]);
			this.pk_orgs = null;
		} else {
			this.pk_orgs = pk_orgs;
		}
	}
}
