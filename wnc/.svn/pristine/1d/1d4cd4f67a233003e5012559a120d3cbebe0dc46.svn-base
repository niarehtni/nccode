package nc.pub.xml.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.RuntimeEnv;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author 但强
 * @time 2018-1-17 14:52:58
 * @theme XML解析工具类
 */
public class XmlMatchUtils {

	private static Map<String, String> cnMap = new HashMap<String, String>();

	public static String getXmlInfo(String tableName) {
		if (cnMap.containsKey(tableName)) {
			return cnMap.get(tableName);
		}

		SAXReader reader = new SAXReader();
		String name = "";
		try {

			String path = RuntimeEnv.getInstance().getNCHome() + "/XmlMatch/XmlMatch.xml";
			nc.bs.logging.Logger.error("--------------XML-Path-------------");
			nc.bs.logging.Logger.error(path);
			nc.bs.logging.Logger.error("--------------XML-Read-------------");
			Document document = reader.read(new File(path));
			nc.bs.logging.Logger.error("--------------XML-Load-------------");
			Element xmle = document.getRootElement();
			Iterator it = xmle.elementIterator();
			while (it.hasNext()) {
				Element xml = (Element) it.next();
				List<Attribute> list = xml.attributes();
				String code = xml.attribute("CODE").getValue();
				if (code.equals(tableName)) {
					name = xml.attribute("NAME").getValue();
				}

				cnMap.put(code, xml.attribute("NAME").getValue());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return name;
	}
}
