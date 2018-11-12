package nc.pubitf.login;

import java.util.Random;

import nc.login.vo.LoginRequest;
import nc.vo.pub.BusinessException;

public abstract class BaseAuthorization {

	private String username;
	private String password;
	private LoginRequest request;
	private boolean isFromPortal;

	public boolean isFromPortal() {
		return isFromPortal;
	}

	public void setFromPortal(boolean isFromPortal) {
		this.isFromPortal = isFromPortal;
	}

	public LoginRequest getRequest() {
		return request;
	}

	public void setRequest(LoginRequest request) {
		this.request = request;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public abstract void doAction() throws BusinessException;

	public static String getRndText(int length) {
		String strAlpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String strNumber = "0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		int num = -1;
		for (int i = 0; i < length; i++) {
			if (num == -1) {
				num = random.nextInt(52);
				buf.append(strAlpha.charAt(num));
			} else {
				if (!buf.substring(buf.length() - 1).equals(
						String.valueOf(strAlpha.charAt(num)))) {
					num = random.nextInt(52);
					buf.append(strAlpha.charAt(num));
				} else {
					num = random.nextInt(10);
					buf.append(strNumber.charAt(num));
				}
			}

		}
		return buf.toString();
	}

}