package nc.pubimpl.login;

import nc.bs.framework.common.RuntimeEnv;
import nc.pubitf.login.ILdapConfigLoader;
import nc.vo.pub.BusinessException;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class LdapConfigLoaderImpl implements ILdapConfigLoader {

	@Override
	public String loadLDAPConfig() throws BusinessException {
		try {
			SAXReader reader = new SAXReader();
			RuntimeEnv.getInstance().getNCHome();
			Document doc = reader.read(RuntimeEnv.getInstance().getNCHome()
					+ "/ierp/acccheck/WITS_LDAPConfig.xml");
			return doc.asXML();
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}
}
