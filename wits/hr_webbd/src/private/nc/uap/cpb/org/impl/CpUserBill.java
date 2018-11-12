package nc.uap.cpb.org.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.jdbc.framework.generator.IdGenerator;
import nc.jdbc.framework.generator.SequenceGenerator;
import nc.pubitf.login.LDAPAuthorization;
import nc.security.NCAuthenticatorToolkit;
import nc.security.VerifyFactory;
import nc.security.itf.IVerify;
import nc.security.vo.CAContext;
import nc.uap.cpb.log.CpLogger;
import nc.uap.cpb.org.exception.CASignException;
import nc.uap.cpb.org.exception.CpbBusinessException;
import nc.uap.cpb.org.impl.service.CpUserService;
import nc.uap.cpb.org.itf.ICpUserBill;
import nc.uap.cpb.org.itf.ICpUserPasswordService;
import nc.uap.cpb.org.itf.ICpUserQry;
import nc.uap.cpb.org.user.ICpUserConst;
import nc.uap.cpb.org.util.CpUserHelper;
import nc.uap.cpb.org.util.CpbServiceFacility;
import nc.uap.cpb.org.vos.CpUserVO;
import nc.uap.cpb.persist.dao.PtBaseDAO;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.uap.security.SecurityException;

import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Decoder;
import uap.lfw.bd.loginexception.CpLoginRunTimeException;
import uap.lfw.core.locator.ServiceLocator;
import uap.lfw.core.ml.LfwResBundle;

public class CpUserBill implements ICpUserBill {
	private static final String SIGNDATA = "p_signdata";
	private ICpUserPasswordService pswService = null;

	private ICpUserPasswordService getPswService() {
		if (pswService == null) {
			pswService = ((ICpUserPasswordService) ServiceLocator
					.getService(ICpUserPasswordService.class));
		}
		return pswService;
	}

	public void changeUserLanguage(String pk_user, String languageId)
			throws CpbBusinessException {
		PtBaseDAO baseDAO = new PtBaseDAO();
		try {
			CpUserVO user = (CpUserVO) baseDAO.retrieveByPK(CpUserVO.class,
					pk_user);
			if (user != null) {
				user.setContentlang(languageId);
				CpUserService service = new CpUserService();
				service.updateVO(new CpUserVO[] { user });
			}
		} catch (DAOException e) {
			CpLogger.error(e.getMessage(), e);
			throw new CpbBusinessException(e.getMessage());
		} catch (BusinessException e) {
			CpLogger.error(e.getMessage(), e);
			throw new CpbBusinessException(e.getMessage());
		}
	}

	public void changeUserPwd(CpUserVO cpUserVO, String inputOldPwd,
			String inputNewPwd) throws CpbBusinessException {
		String original = cpUserVO.getOriginal();
		String oldpwd = cpUserVO.getUser_password();
		if (StringUtils.isBlank(oldpwd))
			throw new CpbBusinessException(LfwResBundle.getInstance()
					.getStrByID("bd", "CpUserBill-000011"));
		if (StringUtils.isBlank(inputNewPwd))
			throw new CpbBusinessException(LfwResBundle.getInstance()
					.getStrByID("bd", "CpUserBill-000012"));
		if (inputNewPwd.equals(inputOldPwd)) {
			throw new CpbBusinessException(LfwResBundle.getInstance()
					.getStrByID("bd", "CpUserBill-000013"));
		}
		ICpUserPasswordService passMgr = getPswService();
		String encodeoldpwd = passMgr.getEncodedPassword(cpUserVO, inputOldPwd);
		if (!oldpwd.equals(encodeoldpwd)) {
			throw new CpbBusinessException(NCLangResOnserver.getInstance()
					.getStrByID("bd", "CpUserBill-000000"));
		}
		try {
			cpUserVO.setUser_password(inputNewPwd);
			checkPwdLevel(cpUserVO);

			if ("NC".equals(original)) {
				passMgr.updateNcUserPassword(cpUserVO, inputOldPwd, inputNewPwd);
				return;
			}

			changeUserPassWord(cpUserVO.getCuserid(), inputNewPwd, true);

			passMgr.delResetUserInfo(cpUserVO.getCuserid());
			passMgr.delInitUserInfo(cpUserVO.getCuserid());
		} catch (BusinessException e) {
			CpLogger.error(e.getMessage(), e);
			throw new CpbBusinessException(e.getMessage());
		}
	}

	public void changeUserPassWord(String pk_user, String password,
			boolean updatePwdparam) throws CpbBusinessException {
		CpUserVO cpUserVO = CpbServiceFacility.getCpUserQry().getUserByPk(
				pk_user);
		String oldPwd = cpUserVO.getUser_password();

		ICpUserPasswordService userMgr = getPswService();
		String newPwd = userMgr.getEncodedPassword(cpUserVO, password);

		if ((StringUtils.isNotBlank(cpUserVO.getPwdlevelcode()))
				&& (!StringUtils.equals(oldPwd, newPwd))) {
			cpUserVO.setUser_password(newPwd);

			if (updatePwdparam) {
				String stmp = new UFDateTime(System.currentTimeMillis())
						.toString();
				cpUserVO.setPwdparam(stmp.substring(0, stmp.indexOf(" "))
						.trim());
			}
			try {
				CpUserService service = new CpUserService();
				service.updateVO(new CpUserVO[] { cpUserVO });

				userMgr.addUserPswHistory(cpUserVO, oldPwd);
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
				throw new CpbBusinessException(e.getMessage());
			}
		}
	}

	public String resetCpUserPwd(String pk_user) throws CpbBusinessException {
		CpUserVO cpUserVO = CpbServiceFacility.getCpUserQry().getUserByPk(
				pk_user);
		String original = cpUserVO.getOriginal();

		ICpUserPasswordService userMgr = getPswService();
		String newPsw = null;

		if ("NC".equals(original)) {
			newPsw = userMgr.resetUserPassWord(pk_user);
		} else {
			try {
				newPsw = userMgr.getResetCpUserPassWord(cpUserVO);
				changeUserPassWord(pk_user, newPsw, false);

				userMgr.addPwdResetUser(pk_user);
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
				throw new CpbBusinessException(e.getMessage());
			}
			CpUserHelper.notifyPasswordChanged(newPsw, pk_user);
		}

		return newPsw;
	}

	public void deleteCpUserVO(String pk_user) throws CpbBusinessException {
		PtBaseDAO dao = new PtBaseDAO();
		try {
			CpUserVO user = (CpUserVO) dao
					.retrieveByPK(CpUserVO.class, pk_user);
			if (user != null) {
				CpUserService service = new CpUserService();
				service.deleteVO(new CpUserVO[] { user });
			}
		} catch (DAOException e) {
			CpLogger.error(e.getMessage(), e);
			throw new CpbBusinessException(e.getMessage());
		} catch (BusinessException e) {
			CpLogger.error(e.getMessage(), e);
			throw new CpbBusinessException(e.getMessage());
		}
	}

	public void deleteCpUserVO(CpUserVO uservo) throws CpbBusinessException {
		try {
			if (uservo != null) {
				CpUserService service = new CpUserService();
				service.deleteVO(new CpUserVO[] { uservo });
			}
		} catch (BusinessException e) {
			CpLogger.error(e.getMessage(), e);
			throw new CpbBusinessException(e.getMessage(), e);
		}
	}

	public void deleteCpUserVOs(CpUserVO[] uservos) throws CpbBusinessException {
		try {
			if ((uservos != null) && (uservos.length > 0)) {
				CpUserService service = new CpUserService();
				service.deleteVO(uservos);
			}
		} catch (BusinessException e) {
			CpLogger.error(e.getMessage(), e);
			throw new CpbBusinessException(e.getMessage());
		}
	}

	private void initUserPassWord(CpUserVO userVO) throws CpbBusinessException {
		if (userVO == null)
			return;
		String oldPwd = userVO.getUser_password();
		if (StringUtils.isBlank(oldPwd)) {
			ICpUserPasswordService userMgr = getPswService();
			try {
				String defaultPwd = userMgr.getUserDefaultPassword(null);
				String pwd = userMgr.getEncodedPassword(userVO, defaultPwd);
				userVO.setUser_password(pwd);
			} catch (BusinessException e) {
				CpLogger.error(e);
				throw new CpbBusinessException(e.getMessage());
			}
		}
	}

	public String addCpUserVO(CpUserVO uservo) throws CpbBusinessException {
		IdGenerator idGenerator = new SequenceGenerator();
		uservo.setPrimaryKey(idGenerator.generate());

		initUserPassWord(uservo);
		CpUserService service = new CpUserService();
		try {
			CpUserVO[] users = (CpUserVO[]) service
					.insertVO(new CpUserVO[] { uservo });
			uservo = users[0];
			String pk_user = uservo.getCuserid();

			ICpUserPasswordService userMgr = getPswService();
			userMgr.addInitUser(pk_user);

			return pk_user;
		} catch (BusinessException e) {
			CpLogger.error(e);
			throw new CpbBusinessException(e.getMessage());
		}
	}

	public String addCpUserVOWithPk(CpUserVO uservo)
			throws CpbBusinessException {
		CpUserService service = new CpUserService();
		try {
			CpUserVO[] users = (CpUserVO[]) service
					.insertVO(new CpUserVO[] { uservo });
			uservo = users[0];
			return uservo.getCuserid();
		} catch (BusinessException e) {
			CpLogger.error(e);
			throw new CpbBusinessException(e.getMessage());
		}
	}

	public void updateCpUserVO(CpUserVO uservo) throws CpbBusinessException {
		CpUserService service = new CpUserService();
		try {
			service.updateVO(new CpUserVO[] { uservo });
		} catch (BusinessException e) {
			CpLogger.error(e);
			throw new CpbBusinessException(e.getMessage());
		}
	}

	public void updateCpUserVOWithOutAudiInfo(CpUserVO uservo)
			throws CpbBusinessException {
		CpUserService service = new CpUserService();
		try {
			service.updateVOsWithOutAuditInfo(new CpUserVO[] { uservo });
		} catch (BusinessException e) {
			CpLogger.error(e);
			throw new CpbBusinessException(e.getMessage());
		}
	}

	public void deleteCpUserSignpic(String pk_user, String pk_lfwfile)
			throws CpbBusinessException {
		updateCpUserSignpic(pk_user, "~");
	}

	public void updateCpUserSignpic(String pk_user, String pk_lfwfile)
			throws CpbBusinessException {
		PtBaseDAO baseDAO = new PtBaseDAO();
		try {
			CpUserVO user = (CpUserVO) baseDAO.retrieveByPK(CpUserVO.class,
					pk_user);
			if (user != null) {
				user.setUsersigniconfilepk(pk_lfwfile);
				CpUserService service = new CpUserService();
				service.updateVO(new CpUserVO[] { user });
			}
		} catch (DAOException e) {
			CpLogger.error(e.getMessage(), e);
			throw new CpbBusinessException(e.getMessage());
		} catch (BusinessException e) {
			CpLogger.error(e.getMessage(), e);
			throw new CpbBusinessException(e.getMessage());
		}
	}

	public void updateCpUserByPkPsndoc(String pk_psndoc)
			throws CpbBusinessException {
		String condition = " pk_base_doc = '" + pk_psndoc + "'";
		CpUserVO[] users = ((ICpUserQry) NCLocator.getInstance().lookup(
				ICpUserQry.class)).getAllUserByCondition(null, condition);
		if (users != null) {
			for (CpUserVO user : users) {
				updateCpUserVO(user);
			}
		}
	}

	public Map<String, Serializable> userlogin(String userid, String password,
			Map<String, String> extMap) {
		Map<String, Serializable> rsl = new HashMap();
		boolean loginSuccess = true;
		CpUserVO cpUserVO = null;
		try {
			cpUserVO = doAuth(userid, password, extMap);
			if (!"N".equals(extMap.get("needverifypasswd"))) {
				VerifyUserPassword(cpUserVO, password, extMap);
			}
		} catch (CASignException e) {
			loginSuccess = false;
			rsl.put("CODE", "2");
		} catch (CpLoginRunTimeException e4) {
			rsl.put("DESC", e4.getMessage());
			if (CpLoginRunTimeException.PswLevel.forceChangePsw == e4
					.getPswLevel()) {
				rsl.put("level", "ERROR");
			} else {
				rsl.put("level", "INFO");
			}
		} catch (BusinessException e2) {
			loginSuccess = false;
			rsl.put("CODE", "1");
			rsl.put("DESC", e2.getMessage());
		}
		if (loginSuccess) {
			rsl.put("CODE", "0");
			rsl.put("USER", cpUserVO);
		}
		return rsl;
	}

	private CpUserVO doAuth(String userid, String password, Map extMap)
			throws CpbBusinessException, LfwRuntimeException {
		ICpUserQry userQry = CpbServiceFacility.getCpUserQry();
		CpUserVO cpUserVO = userQry.getUserByCodeWithGroupAdmin(userid);

		if (cpUserVO == null) {

			throw new CpbBusinessException(getErrorMessage(0));
		}

		if ((cpUserVO.getEnablestate()
				.equals(ICpUserConst.ENABLESTATE_UNACTIVE))
				|| (cpUserVO.getEnablestate()
						.equals(ICpUserConst.ENABLESTATE_STOP))) {
			throw new CpbBusinessException(getErrorMessage(1));
		}

		if ((cpUserVO.getIslocked() != null)
				&& (cpUserVO.getIslocked().booleanValue())) {
			throw new CpbBusinessException(getErrorMessage(2));
		}
		if (cpUserVO.getAbledate() != null) {
			UFDate now = new UFDate(new Date());
			if (cpUserVO.getAbledate().after(now)) {
				throw new CpbBusinessException(getErrorMessage(3));
			}
		}

		if (cpUserVO.getDisabledate() != null) {
			UFDate now = new UFDate(new Date());
			if (cpUserVO.getDisabledate().before(now)) {
				throw new CpbBusinessException(getErrorMessage(4));
			}
		}

		return cpUserVO;
	}

	private String getErrorMessage(int code) {
		String message = null;
		switch (code) {
		case 0:
			message = LfwResBundle.getInstance().getStrByID("bd",
					"CpUserBill-000002");
			break;
		case 1:
			message = LfwResBundle.getInstance().getStrByID("bd",
					"CpUserBill-000003");
			break;
		case 2:
			message = LfwResBundle.getInstance().getStrByID("bd",
					"CpUserBill-000004");
			break;
		case 3:
			message = LfwResBundle.getInstance().getStrByID("bd",
					"CpUserBill-000005");
			break;
		case 4:
			message = LfwResBundle.getInstance().getStrByID("bd",
					"CpUserBill-000006");
			break;
		case 5:
			message = LfwResBundle.getInstance().getStrByID("bd",
					"CpUserBill-000007");
			break;
		case 6:
			message = LfwResBundle.getInstance().getStrByID("bd",
					"CpUserBill-000008");
			break;
		case 7:
			message = LfwResBundle.getInstance().getStrByID("bd",
					"CpUserBill-000009");
			break;
		case 8:
			message = LfwResBundle.getInstance().getStrByID("bd",
					"CpUserBill-000010");
			break;
		}

		return message;
	}

	private void VerifyUserPassword(CpUserVO cpUserVO, String password,
			Map<String, String> extMap) throws BusinessException,
			CpLoginRunTimeException {
		// ssx modified for AD verification for WITS
		// on 2017-11-1
		if (cpUserVO.getUser_type() == 1) {
			// LDAP Check
			if (StringUtils.isEmpty(password)) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("webbdlogin_0",
								"0webbdlogin-0001")/* @res µÇä›Ê§”¡£ºÃÜ´a²»ÄÜžé¿Õ¡£ */);
			}
			LDAPAuthorization ldapAuth = new LDAPAuthorization(
					cpUserVO.getUser_code(), password, null, true);
			ldapAuth.doAction();

			changeUserPassWord(cpUserVO.getCuserid(), password, true);
		} else {
			doStaticPasswordVerify(cpUserVO, password);
			doCAVerify(cpUserVO, extMap);
		}
	}

	private void doStaticPasswordVerify(CpUserVO cpUserVO, String password)
			throws BusinessException, CpLoginRunTimeException {
		ICpUserPasswordService userMgr = getPswService();
		userMgr.doStaticPasswordVerify(cpUserVO, password);
	}

	private void doCAVerify(CpUserVO cpUserVO, Map<String, String> extMap)
			throws CASignException, CpbBusinessException {
		int UNKNOWN_ERROR = 1;
		int LOGIN_LEGALIDENTITY = 2;
		if ("ncca".equals(cpUserVO.getIdentityverifycode())) {
			String signdata = (String) extMap.get("p_signdata");
			String sn = (String) extMap.get("p_sn");

			if ((signdata == null) || ("".equals(signdata))) {
				throw new CASignException();
			}

			String challlid = (String) extMap.get("challlid");
			int result = UNKNOWN_ERROR;
			boolean isSucc = false;
			try {
				String caRegEntryId = NCAuthenticatorToolkit
						.getCARegisterCenter().getLoginProviderID();
				byte[] signBytes = new BASE64Decoder().decodeBuffer(signdata);
				CAContext context = new CAContext(cpUserVO.getPrimaryKey(),
						cpUserVO.getUser_code(), NCAuthenticatorToolkit
								.getCARegisterCenter().getCARegEntryByID(
										caRegEntryId));
				IVerify verify = VerifyFactory.createVerify(context);
				if (201 == verify.verify(sn, challlid.getBytes(), signBytes)) {
					isSucc = true;
				}
			} catch (SecurityException e) {
				result = e.getErrorCode();
				CpLogger.error(e.getMessage(), e);
			} catch (Exception e) {
				CpLogger.error(e.getMessage(), e);
			}
			if (isSucc) {
				result = LOGIN_LEGALIDENTITY;
			} else {
				result = UNKNOWN_ERROR;
			}
			if (LOGIN_LEGALIDENTITY != result) {
				throw new CpbBusinessException(getErrorMessage(8));
			}
		}
	}

	private void checkPwdLevel(CpUserVO cpUserVO) throws BusinessException {
		ICpUserPasswordService passMgr = getPswService();
		try {
			passMgr.checkPwdLevel(cpUserVO);
		} catch (BusinessException be) {
			CpLogger.error(be.getMessage(), be);
			throw new BusinessException(be.getMessage());
		}
	}

	public CpUserVO[] updateCpUserVOs(CpUserVO[] userVos)
			throws CpbBusinessException {
		CpUserService service = new CpUserService();
		try {
			return (CpUserVO[]) service.updateVO(userVos);
		} catch (BusinessException e) {
			CpLogger.error(e);
			throw new CpbBusinessException(e.getMessage());
		}
	}

	public void updateCpUserPswTryTimes_RequiresNew(CpUserVO user)
			throws CpbBusinessException {
		try {
			if (((user.getUser_type().intValue() == 0) || (user.getUser_type()
					.intValue() == 2)) && (user.getIslocked().booleanValue())) {
				((ICpUserPasswordService) ServiceLocator
						.getService(ICpUserPasswordService.class))
						.lockNcUser(user);
			}

			CpUserService service = new CpUserService();
			user.setTs(null);
			service.updateVOsWithOutAuditInfo(new CpUserVO[] { user });
		} catch (BusinessException e) {
			CpLogger.error(e.getMessage(), e);
			throw new CpbBusinessException(e.getMessage());
		}
	}
}