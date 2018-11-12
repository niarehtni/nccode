package nc.impl.hi.utils;
import java.util.Map;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;


/**
 * 法人组织相关的工具类
 * 
 * @author Ares.Tank
 * @date 2018-9-13 15:54:20
 * 
 */
public class LegalOrgUtils {

	/**
	 * 获取这些哥们儿的法人组织
	 * 
	 * @param psndoc
	 * @return Map<psndoc,pk_org>
	 */
	public static Map<String, String> getLegalOrgByPsndoc(String[] psndocs) {
		return LegalOrgUtilsEX.getLegalOrgByPsndoc(psndocs);
	}

	/**
	 * 获取本组织的法人组织,如果本身是法人组织,那么就返回本身, 如果不是,那么查找上级,返回关系最近的法人组织
	 * 
	 * @param pkOrg
	 * @return<组织pk,法人组织pk>
	 */
	public static Map<String, String> getLegalOrgByOrgs(String[] pkOrgs) {
	
		return LegalOrgUtilsEX.getLegalOrgByOrgs(pkOrgs);
	}
}
