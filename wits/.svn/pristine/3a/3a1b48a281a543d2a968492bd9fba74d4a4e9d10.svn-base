/**
 * 
 */
package nc.vo.bd.format;

import nc.vo.pub.format.AddressFormat;
import nc.vo.pub.format.FormatResult;
import nc.vo.pub.format.exception.FormatException;
import nc.vo.pub.format.meta.AddressFormatMeta;


/**
 * @author wh
 *
 */
public class AddressFormatDoc extends FormatDocDetail 
{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6377894548157623314L;
	
	private FormatItem addrUnit1 = new FormatItem("P","邮编");/*-=notranslate=-*/
	
	private FormatItem addrUnit2 = new FormatItem("C","国家");/*-=notranslate=-*/
	
	private FormatItem addrUnit3 = new FormatItem("S","省");/*-=notranslate=-*/
	
	private FormatItem addrUnit4 = new FormatItem("T","城市");/*-=notranslate=-*/

	private FormatItem addrUnit45 = new FormatItem("D","区县");/*-=notranslate=-*/
	
	private FormatItem addrUnit5 = new FormatItem("R","地址详址");/*-=notranslate=-*/
	
	private String delimit1 = "";
	
	private String delimit2 = "";
	
	private String delimit3 = "";
	
	private String delimit4  = "";
	
	private String delimit45  = "";
	
	private String delimit5 = "";
	
	private AddressObjectAdaptor expSourceData = new AddressObjectAdaptor();
	
	public AddressFormatDoc(){
		addrUnit1 = new FormatItem("P",nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("address","0address0018"));//邮编
		addrUnit2 = new FormatItem("C",nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("address","0address0017"));//国家
		addrUnit3 = new FormatItem("S",nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("address","0address0016"));//省
		addrUnit4 = new FormatItem("T",nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("address","0address0015"));//城市
		addrUnit45 = new FormatItem("D",nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("address","0address0014"));//区县
		addrUnit5 = new FormatItem("R",nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("address","0address0013"));//地址祥址
		
		delimit1 = "";
		delimit2 = "";
		delimit3 = "";
		delimit4  = "";
		delimit45  = "";
		delimit5 = "";
		
		setExpText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("address","0address0012"));//100094 中国 北京 北京市 海淀区北清路68号用友软件园
	}

	public AddressFormatMeta toNCFormatMeta() 
	{
		//MOD removed for TW James
//		String format = addrUnit1.getCode() + getDelimit(delimit1) +
//		                addrUnit2.getCode() + getDelimit(delimit2) + 
//		                addrUnit3.getCode() + getDelimit(delimit3) + 
//		                addrUnit4.getCode() + getDelimit(delimit4) + 
//		                addrUnit45.getCode() + getDelimit(delimit45) +
		String format =     addrUnit5.getCode() + getDelimit(delimit5);
		AddressFormatMeta formatMeta = new AddressFormatMeta();
		formatMeta.setExpress(format);
		return formatMeta;
	}
	
	private String getDelimit(String delimit)
	{
		if(delimit == null)
		{
			delimit = "";
		}
			return "\""+delimit+"\"";
	}

	/**
	 * @return the addrUnit1
	 */
	public FormatItem getAddrUnit1() {
		return addrUnit1;
	}

	/**
	 * @param addrUnit1 the addrUnit1 to set
	 */
	public void setAddrUnit1(FormatItem addrUnit1) {
		this.addrUnit1 = addrUnit1;
	}

	/**
	 * @return the addrUnit2
	 */
	public FormatItem getAddrUnit2() {
		return addrUnit2;
	}

	/**
	 * @param addrUnit2 the addrUnit2 to set
	 */
	public void setAddrUnit2(FormatItem addrUnit2) {
		this.addrUnit2 = addrUnit2;
	}

	/**
	 * @return the addrUnit3
	 */
	public FormatItem getAddrUnit3() {
		return addrUnit3;
	}

	/**
	 * @param addrUnit3 the addrUnit3 to set
	 */
	public void setAddrUnit3(FormatItem addrUnit3) {
		this.addrUnit3 = addrUnit3;
	}

	/**
	 * @return the addrUnit4
	 */
	public FormatItem getAddrUnit4() {
		return addrUnit4;
	}

	/**
	 * @param addrUnit4 the addrUnit4 to set
	 */
	public void setAddrUnit4(FormatItem addrUnit4) {
		this.addrUnit4 = addrUnit4;
	}

	/**
	 * @return the addrUnit5
	 */
	public FormatItem getAddrUnit5() {
		return addrUnit5;
	}

	/**
	 * @param addrUnit5 the addrUnit5 to set
	 */
	public void setAddrUnit5(FormatItem addrUnit5) {
		this.addrUnit5 = addrUnit5;
	}

	/**
	 * @return the delimit1
	 */
	public String getDelimit1() {
		return delimit1;
	}

	/**
	 * @param delimit1 the delimit1 to set
	 */
	public void setDelimit1(String delimit1) {
		this.delimit1 = delimit1;
	}

	/**
	 * @return the delimit2
	 */
	public String getDelimit2() {
		return delimit2;
	}

	/**
	 * @param delimit2 the delimit2 to set
	 */
	public void setDelimit2(String delimit2) {
		this.delimit2 = delimit2;
	}

	/**
	 * @return the delimit3
	 */
	public String getDelimit3() {
		return delimit3;
	}

	/**
	 * @param delimit3 the delimit3 to set
	 */
	public void setDelimit3(String delimit3) {
		this.delimit3 = delimit3;
	}

	/**
	 * @return the delimit4
	 */
	public String getDelimit4() {
		return delimit4;
	}

	/**
	 * @param delimit4 the delimit4 to set
	 */
	public void setDelimit4(String delimit4) {
		this.delimit4 = delimit4;
	}

	/**
	 * @return the delimit5
	 */
	public String getDelimit5() {
		return delimit5;
	}

	/**
	 * @param delimit5 the delimit5 to set
	 */
	public void setDelimit5(String delimit5) {
		this.delimit5 = delimit5;
	}
	
	public FormatItem getAddrUnit45() {
		return addrUnit45;
	}

	public void setAddrUnit45(FormatItem addrUnit45) {
		this.addrUnit45 = addrUnit45;
	}

	public String getDelimit45() {
		return delimit45;
	}

	public void setDelimit45(String delimit45) {
		this.delimit45 = delimit45;
	}

	@Override
	public FormatResult format(Object data) throws FormatException{
		AddressFormat formater = new AddressFormat(this.toNCFormatMeta());
		if(data == null)
			return null;

		return formater.format(data);
	}

	@Override
	public Object getExpSourceData() {

		return expSourceData;
	}
	
	@Override
	public String evalueExpFormat() throws FormatException
	{
//		String result = FormatUtils.toColorfulString(format(getExpSourceData()));
//		
//		if(result.startsWith("<font"))
//		{
//			expText = "<html><body>"+result+"</body></html>";
//		}
//		else
//		{
//			expText = result;
//		}
		FormatResult result = format(getExpSourceData());
		if (result!=null){
			return result.getValue();
		}
		return "";
	}

//	public AddressFormatDoc buildDefault()
//	{
//		AddressFormatDoc afd = new AddressFormatDoc();
//		afd.setAddrUnit1(new FormatItem("C","国家"));
//		afd.set
//	}
}
