/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.ta.signcard;

import nc.vo.pub.lang.UFDateTime;
import nc.vo.ta.timebill.annotation.PkPsnjobFieldName;

/**
 * <b> �ڴ˴���Ҫ��������Ĺ��� </b>
 * <p>
 *     �ڴ˴����Ӵ����������Ϣ
 * </p>
 * ��������:${vmObject.createdDate}
 * @author 
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
@PkPsnjobFieldName(fieldName="pk_psnjob")
public class SignbVO extends SignCommonVO{
	private java.lang.String pk_signb;
	private java.lang.String pk_signh;
	private java.lang.String transtypeid;//�������ͣ���������ʱΪ��listView����ʾ��	
	private java.lang.String transtype;
	private String signarea;
	private String signshift;
	
	public static final String TRANSTYPEID = "transtypeid";
	public static final String TRANSTYPE = "transtype";
	public static final String PK_SIGNB = "pk_signb";
	public static final String PK_SIGNH = "pk_signh";
	public static final String SIGNAREA="signarea";
	public static final String SIGNSHIFT="signshift";	
	
	/**
	 * ����pk_signb��Getter����.
	 * ��������:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_signb () {
		return pk_signb;
	}   
	/**
	 * ����pk_signb��Setter����.
	 * ��������:$vmObject.createdDate
	 * @param newPk_signb java.lang.String
	 */
	public void setPk_signb (java.lang.String newPk_signb ) {
	 	this.pk_signb = newPk_signb;
	} 	  
	/**
	 * ����pk_signh��Getter����.
	 * ��������:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_signh () {
		return pk_signh;
	}   
	/**
	 * ����pk_signh��Setter����.
	 * ��������:$vmObject.createdDate
	 * @param newPk_signh java.lang.String
	 */
	public void setPk_signh (java.lang.String newPk_signh ) {
	 	this.pk_signh = newPk_signh;
	} 	  
    
	/**
	  * <p>ȡ�ñ�����.
	  * <p>
	  * ��������:${vmObject.createdDate}
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_signb";
	}
    
	public java.lang.String getTranstype() {
		return transtype;
	}
	public void setTranstype(java.lang.String transtype) {
		this.transtype = transtype;
	}
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:${vmObject.createdDate}
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "tbm_signb";
	}    
	
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:${vmObject.createdDate}
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "tbm_signb";
	}    
    
    /**
	  * ����Ĭ�Ϸ�ʽ����������.
	  *
	  * ��������:${vmObject.createdDate}
	  */
     public SignbVO() {
		super();	
	}
    
   //������
    public String getCreator (){
    	return null;
    }
 	
  //��������
 	public UFDateTime getCreationtime(){
 		return null;
 	}
	public java.lang.String getTranstypeid() {
		return transtypeid;
	}
	public void setTranstypeid(java.lang.String transtypeid) {
		this.transtypeid = transtypeid;
	}
	@Override
	public boolean isAppBill() {
		return true;
	}
	@Override
	public void setAppBill(boolean isAppBill) {
		
	}
	public String getSignarea() {
		return signarea;
	}
	public void setSignarea(String signarea) {
		this.signarea = signarea;
	}
	public String getSignshift() {
		return signshift;
	}
	public void setSignshift(String signshift) {
		this.signshift = signshift;
	}
} 

