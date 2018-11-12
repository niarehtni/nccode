package nc.sso.bs;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nc.bs.framework.adaptor.IHttpServletAdaptor;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.logging.Logger;
import nc.pubitf.login.ILdapUserManager;
import nc.sso.vo.SSOAuthenConfVO;
import nc.uap.portal.util.PortalDsnameFetcher;
import sun.misc.BASE64Decoder;

public class SSORegisterServlet extends HttpServlet implements
		IHttpServletAdaptor {
	private static final long serialVersionUID = 4216241764046437430L;
	private List<ISSOAuthenticator> authenList = null;
	private SecureRandom sRandom = new SecureRandom();

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doAction(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doAction(req, resp);
	}

	public void doAction(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			String ds = PortalDsnameFetcher.getPortalDsName();
			InvocationInfoProxy.getInstance().setUserDataSource(ds);
			response.setContentType("text/html;charset=GBK");
			// ssoValidate(request);
			// String ssoKey = request.getParameter("ssoKey");
			// if (isNullStr(ssoKey)) {
			// ssoKey = genKey();
			// }
			// String charset = request.getParameter("charset");
			// if (isNullStr(charset)) {
			// charset = "GBK";
			// }
			// SSORegInfo regInfo = new SSORegInfo();
			// regInfo.setSsoKey(ssoKey);
			//
			// // 取用戶名（域用戶）
			String accBase64 = request.getParameter("AccountID");
			BASE64Decoder dcder = new BASE64Decoder();
			String userCode = new String(dcder.decodeBuffer(accBase64), "GBK");
			if (isNullStr(userCode)) {
				throw new RuntimeException("userCode parameter can't be null");
			}
			// userCode = convertToCorrectEncoding(userCode, charset);
			// regInfo.setUserCode(userCode);
			//
			// String bcCode = request.getParameter("busiCenter");
			// if (!isNullStr(bcCode)) {
			// bcCode = convertToCorrectEncoding(bcCode, charset);
			// regInfo.setBusiCenterCode(bcCode);
			// }
			//
			// String groupCode = request.getParameter("groupCode");
			// if (!isNullStr(groupCode)) {
			// groupCode = convertToCorrectEncoding(groupCode, charset);
			// regInfo.setGroupCode(groupCode);
			// }
			//
			// String langCode = request.getParameter("langCode");
			// if (!isNullStr(langCode)) {
			// regInfo.setLangCode(langCode);
			// }
			//
			// ISSOService service = (ISSOService)
			// NCLocator.getInstance().lookup(
			// ISSOService.class);
			// service.registerSSOInfo(regInfo);

			// 取入口：NC/Portal
			// String entry = request.getParameter("Entry");
			// if (!StringUtils.isEmpty(entry) &&
			// entry.toUpperCase().equals("NC")) {
			// String redir = "http://" + request.getServerName() + ":"
			// + request.getServerPort() + "/login.jsp" + "?"
			// + "ssoKey=" + ssoKey;
			// response.sendRedirect(redir);
			// } else if (!StringUtils.isEmpty(entry)
			// && entry.toUpperCase().equals("PORTAL")) {
			// ssx remarked on 2017-11-21
			// for WITS only login Portal via SSO
			String cuserid = getUMService().MappingPortalUserPK(userCode);
			String ssoKey = getUMService()
					.RegisterToPortal(
							cuserid,
							"http://" + request.getServerName() + ":"
									+ request.getServerPort()
									+ "/portal/pt/home/index");

			String redir = "http://" + request.getServerName() + ":"
					+ request.getServerPort() + "/portal/auth/" + ssoKey;
			response.sendRedirect(redir);
			// }

			// PrintWriter pw = response.getWriter();
			// SecurityToolkit toolKit = new SecurityToolkit();
			// pw.println(UAPESAPI.htmlEncode(SecurityToolkit.escape(ssoKey)));
		} catch (Throwable th) {
			Logger.error(th.getMessage(), th);
			PrintWriter pw = response.getWriter();
			printErrorToClient(pw, th);
		}
	}

	private ILdapUserManager umService = null;

	private ILdapUserManager getUMService() {
		if (umService == null) {
			umService = (ILdapUserManager) NCLocator.getInstance().lookup(
					ILdapUserManager.class);
		}

		return umService;
	}

	private void printErrorToClient(PrintWriter pw, Throwable th) {
		pw.println("Error:" + th.getMessage());
		th.printStackTrace(pw);
	}

	private void ssoValidate(HttpServletRequest request) throws Exception {
		int count = getAuthenList().size();
		for (int i = 0; i < count; i++) {
			ISSOAuthenticator authenticator = (ISSOAuthenticator) getAuthenList()
					.get(i);
			authenticator.authenticate(request);
		}
	}

	private List<ISSOAuthenticator> getAuthenList() {
		if (authenList == null) {
			ISSOService service = (ISSOService) NCLocator.getInstance().lookup(
					ISSOService.class);
			try {
				List<SSOAuthenConfVO> confList = service.getAuthenConfList();
				authenList = new ArrayList();
				int count = confList == null ? 0 : confList.size();
				for (int i = 0; i < count; i++) {
					SSOAuthenConfVO confVO = (SSOAuthenConfVO) confList.get(i);
					ISSOAuthenticator authenticator = createAuthenticator(confVO);
					if (authenticator != null) {
						authenList.add(authenticator);
					}
				}
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}

		return authenList;
	}

	private ISSOAuthenticator createAuthenticator(SSOAuthenConfVO confVO) {
		ISSOAuthenticator authenticator = null;
		String clsName = confVO.getAuthenClsName();
		try {
			Class<?> cls = Class.forName(clsName);
			if (ISSOAuthenticator.class.isAssignableFrom(cls)) {
				authenticator = (ISSOAuthenticator) cls.newInstance();
				if ((authenticator instanceof AbstractSSOAuthenticator)) {
					Map<String, String> paramMap = confVO.getParamMap();
					((AbstractSSOAuthenticator) authenticator)
							.setParamMap(paramMap);
					Map<String, String[]> listValueMap = confVO
							.getListValueMap();
					((AbstractSSOAuthenticator) authenticator)
							.setListValueMap(listValueMap);
				}
			} else {
				throw new RuntimeException(clsName + " must implements "
						+ ISSOAuthenticator.class.getName());
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return authenticator;
	}

	private boolean isNullStr(String s) {
		return (s == null) || (s.trim().length() == 0);
	}

	private String genKey() {
		long t = System.currentTimeMillis();
		long t1 = sRandom.nextLong();
		return "" + t + t1;
	}

	private String convertToCorrectEncoding(String oriString, String charset)
			throws Exception {
		if (oriString == null)
			return null;
		if (RuntimeEnv.isRunningInWeblogic())
			return oriString;
		oriString = new String(oriString.getBytes("ISO-8859-1"), charset);
		return oriString;
	}
}
