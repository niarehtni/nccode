package nc.impl.ta.common.utils;

import nc.pubitf.twhr.utils.LegalOrgUtilsEX;

/**
 *法人公司
 * @author he
 *
 */

public class CorporateOrg {
	public String legorg(String pk_org){

		//获取法人组织
		//select legal_pk_org from org_leaglorg_mapping where pk_org='0001A110000000001WSM'
		String[] orgs = { pk_org };
		String legal_pk_org = LegalOrgUtilsEX.getLegalOrgByOrgs(orgs)
				.get(pk_org);
		return legal_pk_org;
	}
}
