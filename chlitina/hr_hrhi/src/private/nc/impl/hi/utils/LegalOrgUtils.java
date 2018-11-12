package nc.impl.hi.utils;
import java.util.Map;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;


/**
 * ������֯��صĹ�����
 * 
 * @author Ares.Tank
 * @date 2018-9-13 15:54:20
 * 
 */
public class LegalOrgUtils {

	/**
	 * ��ȡ��Щ���Ƕ��ķ�����֯
	 * 
	 * @param psndoc
	 * @return Map<psndoc,pk_org>
	 */
	public static Map<String, String> getLegalOrgByPsndoc(String[] psndocs) {
		return LegalOrgUtilsEX.getLegalOrgByPsndoc(psndocs);
	}

	/**
	 * ��ȡ����֯�ķ�����֯,��������Ƿ�����֯,��ô�ͷ��ر���, �������,��ô�����ϼ�,���ع�ϵ����ķ�����֯
	 * 
	 * @param pkOrg
	 * @return<��֯pk,������֯pk>
	 */
	public static Map<String, String> getLegalOrgByOrgs(String[] pkOrgs) {
	
		return LegalOrgUtilsEX.getLegalOrgByOrgs(pkOrgs);
	}
}
