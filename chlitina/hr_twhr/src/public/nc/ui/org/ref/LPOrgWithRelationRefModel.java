package nc.ui.org.ref;

import java.util.List;

import nc.hr.utils.InSQLCreator;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.vo.pub.BusinessException;

/**
 * 带有委托关系的法人组织,只会显示有委托关系的法人组织
 * @author Ares.Tank
 */
public class LPOrgWithRelationRefModel extends LPOrgDefaultRefModel {
	@Override
	protected String getEnvWherePart() {
		//通过人力资源组织查找法人组织 Ares.Tank 2018-10-11 16:10:27
		List<String> orgList = LegalOrgUtilsEX.getRelationOrgWithSalary(this.getPk_org());
		if(null == orgList || orgList.size() <=0){
			return "11 = 12";
		}
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = null;
		try {
			psndocsInSQL = insql.getInSQL(orgList.toArray(new String[0]));
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return "(pk_corp in (" + psndocsInSQL + "))";
	}
}
