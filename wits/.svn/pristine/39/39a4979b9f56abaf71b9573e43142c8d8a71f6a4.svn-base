package nc.pubimpl.login;

import java.util.List;
import java.util.UUID;

import nc.bs.dao.BaseDAO;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.pubitf.login.ILdapUserManager;
import nc.uap.cpb.org.vos.CpUserVO;
import nc.uap.portal.vo.PtTrdauthVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;

import org.apache.commons.collections.CollectionUtils;

public class LdapUserManagerImpl implements ILdapUserManager {
	private BaseDAO baseDAO = null;

	private BaseDAO getBaseDAO() {
		if (null == baseDAO) {
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}

	@Override
	public void updateUserPassword(String userid, String encPassword)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		dao.executeUpdate("update sm_user set user_password = '" + encPassword
				+ "' where cuserid = '" + userid + "'");
	}

	@Override
	public String MappingPortalUserPK(String portalUsercode)
			throws BusinessException {
		//
		List<CpUserVO> cpuserList = null;
		String sql = "select * from cp_user where user_code_q = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(portalUsercode.toUpperCase());
		cpuserList = (List<CpUserVO>) getBaseDAO().executeQuery(sql, param,
				new BeanListProcessor(CpUserVO.class));
		if (CollectionUtils.isEmpty(cpuserList)) {
			throw new BusinessException("portal user is not exist.");
		} else {
			return (cpuserList.get(0)).getCuserid();
		}
	}

	@Override
	public String RegisterToPortal(String cuserid, String url)
			throws BusinessException {
		PtTrdauthVO ssoVO = new PtTrdauthVO();
		ssoVO.setPk_trdauth(UUID.randomUUID().toString().replaceAll("-", ""));
		ssoVO.setPk_user(cuserid);
		ssoVO.setTtl(new UFDateTime(System.currentTimeMillis() + +3 * 60 * 1000)); //
		ssoVO.setUrl(url);
		// ssoVO.setTitle("#language=" + langCode);
		getBaseDAO().insertVO(ssoVO);
		return ssoVO.getAkey();
	}

}
