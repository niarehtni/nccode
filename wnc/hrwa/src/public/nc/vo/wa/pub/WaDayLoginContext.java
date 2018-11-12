package nc.vo.wa.pub;

import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;
/**
 * 
 * @author ward
 * @date 20180510
 * @功能描述：日薪计算context
 *
 */
public class WaDayLoginContext extends LoginContext{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6150613109690359200L;
	private UFLiteralDate calculdate=new UFLiteralDate();
	private String pk_hrorg;

	

	public UFLiteralDate getCalculdate() {
		return calculdate;
	}

	public void setCalculdate(UFLiteralDate calculdate) {
		this.calculdate = calculdate;
	}

	public String getPk_hrorg() {
		return pk_hrorg;
	}

	public void setPk_hrorg(String pk_hrorg) {
		this.pk_hrorg = pk_hrorg;
	}
	
}
