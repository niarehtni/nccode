package nc.impl.wa.paydata;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.RuntimeEnv;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XMLUtils {

	private static Map<String, String> cnMap = new HashMap<String, String>();
	private static Map<String, String> ccMap = new HashMap<String, String>();

	public static String getXmlInfo(String tableName) {
		if (cnMap.containsKey(tableName)) {
			return cnMap.get(tableName);
		}

		SAXReader reader = new SAXReader();
		String name = "";

		try {
			if (cnMap != null && cnMap.size() > 0) {
				for (Entry<String, String> cnentry : cnMap.entrySet()) {
					if (cnentry.getValue() != null && cnentry.getValue().equals(tableName)) {
						name = cnentry.getKey();
						break;
					}
				}
			}

			if (StringUtils.isEmpty(name)) {
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
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return name;
	}

	public static String getClassInfo(String tableName) {
		if (ccMap.containsKey(tableName)) {
			return ccMap.get(tableName);
		}

		SAXReader reader = new SAXReader();
		String name = "";
		try {
			String path = RuntimeEnv.getInstance().getNCHome() + "/XmlMatch/XmlMatch.xml";
			Document document = reader.read(new File(path));
			Element xmle = document.getRootElement();
			Iterator it = xmle.elementIterator();
			while (it.hasNext()) {
				Element xml = (Element) it.next();
				List<Attribute> list = xml.attributes();
				String code = xml.attribute("NAME").getValue();
				if (code.equals(tableName)) {
					name = xml.attribute("VO").getValue();
				}

				ccMap.put(code, xml.attribute("VO").getValue());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return name;
	}

}
