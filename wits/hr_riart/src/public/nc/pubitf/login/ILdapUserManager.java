package nc.pubitf.login;

import nc.vo.pub.BusinessException;

public interface ILdapUserManager {
	public void updateUserPassword(String userid, String encPassword)
			throws BusinessException;

	public String MappingPortalUserPK(String portalUsercode)
			throws BusinessException;

	public String RegisterToPortal(String cuserid, String url)
			throws BusinessException;
}
