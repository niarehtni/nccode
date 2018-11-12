package nc.pubitf.login;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.login.vo.LoginRequest;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import uap.lfw.core.locator.ServiceLocator;

public class LDAPAuthorization extends BaseAuthorization {
	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getLdapServer() {
		return ldapServer;
	}

	public void setLdapServer(String ldapServer) {
		this.ldapServer = ldapServer;
	}

	public String getLocalUsername() {
		if (StringUtils.isEmpty(localUsername)) {
			localUsername = this.getSystemInfoMap().get("USERNAME");
		}
		return localUsername;
	}

	public String getLocalDomainName() {
		if (StringUtils.isEmpty(localDomainName)) {
			localDomainName = this.getSystemInfoMap().get("USERDNSDOMAIN");
		}
		return localDomainName;
	}

	public Map<String, String> getSystemInfoMap() {
		if (systemInfoMap == null) {
			systemInfoMap = System.getenv();
		}

		return systemInfoMap;
	}

	private String domainName;
	private String ldapServer;
	private String localUsername = null;
	private String localDomainName = null;
	private Map<String, String> systemInfoMap;

	public LDAPAuthorization() {

	}

	/**
	 * 初始化LDAP驗證對象
	 *
	 * @param user
	 *            域用戶名
	 * @param password
	 *            域用戶密碼
	 * @param request
	 *            登錄請求
	 * @throws Exception
	 */
	public LDAPAuthorization(String user, String password,
			LoginRequest request, boolean fromProtal) throws BusinessException {
		this.setRequest(request);
		this.setUsername(user);
		this.setPassword(password);
		this.setFromPortal(fromProtal);
		this.setLdapServer(this.getServerByDomain());
	}

	private String getServerByDomain() throws BusinessException {
		try {
			ILdapConfigLoader loader = null;

			if (this.isFromPortal()) {
				loader = ServiceLocator.getService(ILdapConfigLoader.class);
			} else {
				loader = NCLocator.getInstance()
						.lookup(ILdapConfigLoader.class);
			}
			Document doc = DocumentHelper.parseText(loader.loadLDAPConfig());
			String userZone = this.getUsername().substring(0, 2).toUpperCase();
			Logger.error("----------WITS-AD-AUTH-----------");
			Logger.error("USER: " + this.getUsername());
			Logger.error("ZONE:" + userZone);
			Node root = doc.selectSingleNode("/ldapconfigs");
			List list = root.selectNodes("ldap");
			for (Object o : list) {
				Element ele = (Element) o;
				Node domain = ele.selectNodes("domain").get(0);
				if (matchZone(userZone, ele.selectNodes("zone").get(0))) {
					this.setDomainName(domain.getText());
					Node address = ele.selectNodes("address").get(0);
					this.setLdapServer(address.getText());
					Node port = ele.selectNodes("port").get(0);
					return "LDAP://" + address.getText() + ":" + port.getText()
							+ "/";
				}
			}

			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("riarlogin_0","0riarlogin-0000")/*@res 取LDAP配置文件發生錯誤*/);
		} catch (Exception ex) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("riarlogin_0","0riarlogin-0001")/*@res 取LDAP配置文件發生錯誤：*/ + ex.getMessage());
		}

	}

	private boolean matchZone(String userZone, Node domainZone) {
		if (!StringUtils.isEmpty(domainZone.getText())) {
			String[] zones = domainZone.getText().split(",");
			for (String zone : zones) {
				if (zone.equals("*") || zone.toUpperCase().equals(userZone)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void doAction() throws BusinessException {
		// Logger.error("------------Local Username: " +
		// this.getLocalUsername());
		// Logger.error("------------USER DNS Domain Name: "
		// + this.getLocalDomainName());
		// Logger.error("------------Domain Name: " + this.getDomainName());
		// Logger.error("------------Input Username: " + this.getUsername());
		// Logger.error("------------Input Password: " + this.getPassword());
		// Logger.error("------------LDAP Server: " + this.getLdapServer());

		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL,
				this.getUsername() + "@" + this.getDomainName());
		env.put(Context.SECURITY_CREDENTIALS, this.getPassword());
		env.put(Context.PROVIDER_URL, this.getLdapServer());
		env.put(Context.REFERRAL, "follow");

		Logger.error("INITIAL_CONTEXT_FACTORY: "
				+ "com.sun.jndi.ldap.LdapCtxFactory");
		Logger.error("SECURITY_AUTHENTICATION: " + "simple");
		Logger.error("SECURITY_PRINCIPAL: " + this.getUsername() + "@"
				+ this.getDomainName());
		Logger.error("PROVIDER_URL: " + this.getLdapServer());
		Logger.error("REFERRAL: " + "follow");
		try {
			DirContext ctx = new InitialDirContext(env);
			ctx.close();
		} catch (CommunicationException e) {
			Logger.error(e.getMessage());
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("riarlogin_0","0riarlogin-0002")/*@res 請檢查域伺服器通訊是否正常*/);
		} catch (Exception e1) {
			Logger.error(e1.getMessage());
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("riarlogin_0","0riarlogin-0003")/*@res 域驗證失敗：用戶名或密碼錯誤*/);
		}
	}
}