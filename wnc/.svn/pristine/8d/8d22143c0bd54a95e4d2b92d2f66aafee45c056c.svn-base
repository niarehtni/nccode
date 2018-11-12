package nc.impl.hrpub;

import java.net.URLDecoder;
import java.net.URLEncoder;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import nc.bs.framework.common.NCLocator;
import nc.itf.hrpub.IMDExchangeService;
import nc.itf.hrpub.IWNCDataExchange;

public class WNCDataExchangeImpl implements IWNCDataExchange {

	@Override
	public String doTransfer(String requestContext) throws Exception {
		IMDExchangeService deSrv = NCLocator.getInstance().lookup(
				IMDExchangeService.class);
		
		//解码
		requestContext = URLDecoder.decode(requestContext, "utf-8");
		
		return URLEncoder.encode(deSrv.JsonDataExchange(requestContext), "utf-8");
		//return deSrv.JsonDataExchange(requestContext);
	}
}
