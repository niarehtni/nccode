package nc.vo.twhr.nhicalc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.itf.twhr.ICalculateTWNHI;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.pub.BusinessException;

public class PsndocDefTableUtil {
	private static Map<String, String> tableMap = null;
	private static Map<String, Class> classMap = null;
	private static List<Map> infosetResult = null;

	public static Map<String, String> getTableMap() throws BusinessException {
		if (tableMap == null) {
	    ICalculateTWNHI svc = NCLocator.getInstance().lookup(ICalculateTWNHI.class);
	    List<Map> result = svc.getNHIClassMap();
	    if (result != null && result.size() > 0) {
				tableMap = new HashMap<String, String>();
		for (Map data : result) {
					tableMap.put(String.valueOf(data.get("pk_infoset")), String.valueOf(data.get("infoset_code")));
				}
			}
		}
		return tableMap;
	}

	public static Map<String, Class> getClassMap() throws BusinessException {
		if (classMap == null) {
			if (getInfosetResult() != null && getInfosetResult().size() > 0) {
				classMap = new HashMap<String, Class>();
				for (Map data : getInfosetResult()) {
					try {
						classMap.put(String.valueOf(data.get("pk_infoset")),
								Class.forName(String.valueOf(data.get("vo_class_name"))));
					} catch (ClassNotFoundException e) {
						throw new BusinessException(e.getMessage());
					}
				}
			}
		}

		return classMap;
	}

	public static List<Map> getInfosetResult() throws BusinessException {
		if (infosetResult == null) {
			ICalculateTWNHI svc = NCLocator.getInstance().lookup(ICalculateTWNHI.class);
			infosetResult = svc.getNHIClassMap();
		}

		return infosetResult;
	}

	public static String getPsnLaborTablename() throws BusinessException {
		if (getTableMap() != null) {
			return getTableMap().get("1001ZZ10000000001PQV");
		}

		throw new BusinessException("�ڱ�������Ϣ�������]�����_�l�ѡ�");
	}

	public static Class getPsnLaborClass() throws BusinessException {
		if (getClassMap() != null) {
			return getClassMap().get("1001ZZ10000000001PQV");
		}

		throw new BusinessException("�ڱ�������Ϣ�������]�����_�l�ѡ�");
	}

    public static Class getPsnHealthClass() throws BusinessException {
	if (getClassMap() != null) {
	    return getClassMap().get("1001ZZ10000000001Q7R");
	}

	throw new BusinessException("������Ϣ�������]�����_�l�ѡ�");
    }

    public static String getPsnHealthTablename() throws BusinessException {
	if (getTableMap() != null) {
	    return getTableMap().get("1001ZZ10000000001Q7R");
		}

		throw new BusinessException("������Ϣ�������]�����_�l�ѡ�");
	}

	public static String getPsnNHIDetailTablename() throws BusinessException {
		if (getTableMap() != null) {
			return getTableMap().get("1001ZZ10000000002PZV");
		}

		throw new BusinessException("�ڱ�Ͷ�������������]�����_�l�ѡ�");
	}

	public static Class getPsnNHIDetailClass() throws BusinessException {
		if (getClassMap() != null) {
			return getClassMap().get("1001ZZ10000000002PZV");
		}

		throw new BusinessException("�ڱ�Ͷ�������������]�����_�l�ѡ�");
	}

	public static String getPsnNHISumTablename() throws BusinessException {
		if (getTableMap() != null) {
			return getTableMap().get("1001ZZ10000000002U2R");
		}

		throw new BusinessException("�ڽ���Ͷ���R���������]�����_�l�ѡ�");
	}

	public static Class getPsnNHISumClass() throws BusinessException {
		if (getClassMap() != null) {
			return getClassMap().get("1001ZZ10000000002U2R");
		}

		throw new BusinessException("�ڽ���Ͷ���R���������]�����_�l�ѡ�");
	}

	public static String getPsnHealthInsExTablename() throws BusinessException {
		if (getTableMap() != null) {
			return getTableMap().get("TWHRA21000000000DEF5");
		}

		throw new BusinessException("���������O���������]�����_�l�ѡ�");
	}

	public static Class getPsnHealthInsExClass() throws BusinessException {
		if (getClassMap() != null) {
			return getClassMap().get("TWHRA21000000000DEF5");
		}

		throw new BusinessException("���������O���������]�����_�l�ѡ�");
	}

	public static String getGroupInsuranceTablename() throws BusinessException {
		if (getTableMap() != null) {
			return getTableMap().get("TWHRA21000000000DEF6");
		}

		throw new BusinessException("�T���F���YӍ�������]�����_�l�ѡ�");
	}

	public static Class getGroupInsuranceClass() throws BusinessException {
		if (getClassMap() != null) {
			return getClassMap().get("TWHRA21000000000DEF6");
		}

		throw new BusinessException("�T���F���YӍ�������]�����_�l�ѡ�");
	}

	public static String getGroupInsuranceDetailTablename() throws BusinessException {
		if (getTableMap() != null) {
			return getTableMap().get("TWHRA21000000000DEF7");
		}

		throw new BusinessException("�T���F��Ͷ�������������]�����_�l�ѡ�");
	}

	public static Class getGroupInsuranceDetailClass() throws BusinessException {
		if (getClassMap() != null) {
			return getClassMap().get("TWHRA21000000000DEF7");
		}

		throw new BusinessException("�T���F��Ͷ�������������]�����_�l�ѡ�");
	}

    public static String getPsnHealthInsExTablename(String pk_group, String pk_org) throws BusinessException {
	String strSQL = "select code from bd_defdoc where pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'TWHR000') and name = '���������YӍ' and (pk_org = '"
		+ pk_org + "' or pk_org ='" + pk_group + "')";

	return (String) (new BaseDAO()).executeQuery(strSQL, new ColumnProcessor());
    }

    public static String getPsnNoPayExtendNHIFieldname(String pk_group, String pk_org) throws BusinessException {
	String strSQL = "select code from bd_defdoc where pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'TWHR000') and name = '����a�䱣�M' and (pk_org = '"
		+ pk_org + "' or pk_org ='" + pk_group + "')";

	return (String) (new BaseDAO()).executeQuery(strSQL, new ColumnProcessor());
    }
}
