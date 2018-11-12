package nc.itf.hi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.pub.BusinessException;

public class PsndocDefUtil {
	private static Map<String, String> classMap = null;

	public static Map<String, String> getClassMap() throws BusinessException {
		if (classMap == null) {
			BaseDAO dao = new BaseDAO();

			String strSQL = "SELECT  pk_infoset , vo_class_name ";
			strSQL += " FROM    hr_infoset ";
			strSQL += " WHERE   infoset_code LIKE 'hi_psndoc_glbdef%' ";
			strSQL += " AND pk_infoset IN ( '1001ZZ10000000001PQV', '1001ZZ10000000001Q7R', ";
			strSQL += "          '1001ZZ10000000002PZV', '1001ZZ10000000002U2R', 'TWHRA21000000000DEF5', 'TWHRA21000000000DEF6', 'TWHRA21000000000DEF7' )";
			List<Map> result = (List<Map>) dao.executeQuery(strSQL, new MapListProcessor());

			if (result != null && result.size() > 0) {
				classMap = new HashMap<String, String>();
				for (Map data : result) {
					classMap.put(String.valueOf(data.get("pk_infoset")), String.valueOf(data.get("vo_class_name")));
				}
			}
		}
		return classMap;
	}

	public static PsndocDefVO getPsnLaborVO() throws BusinessException {
		if (getClassMap() != null) {
			return getPsndocDefVOByClassname(getClassMap().get("1001ZZ10000000001PQV"));
		}

		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
				"068J61035-0014")/*
									 * @res 勞保勞退信息數據集沒有正確發佈 。
									 */);
	}

	public static PsndocDefVO getPsnHealthVO() throws BusinessException {
		if (getClassMap() != null) {
			return getPsndocDefVOByClassname(getClassMap().get("1001ZZ10000000001Q7R"));
		}

		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
				"068J61035-0015")/*
									 * @res 健保信息數據集沒有正確發佈 。
									 */);
	}

	public static PsndocDefVO getPsnNHIDetailVO() throws BusinessException {
		if (getClassMap() != null) {
			return getPsndocDefVOByClassname(getClassMap().get("1001ZZ10000000002PZV"));
		}

		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
				"068J61035-0016")/*
									 * @res 勞保投保明細數據集沒有正確發佈 。
									 */);
	}

	public static PsndocDefVO getPsnNHISumVO() throws BusinessException {
		if (getClassMap() != null) {
			return getPsndocDefVOByClassname(getClassMap().get("1001ZZ10000000002U2R"));
		}

		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
				"068J61035-0017")/*
									 * @res 勞健保投保匯總數據集沒有正確發佈 。
									 */);
	}

	public static PsndocDefVO getPsnNHIExtendVO() throws BusinessException {
		if (getClassMap() != null) {
			return getPsndocDefVOByClassname(getClassMap().get("TWHRA21000000000DEF5"));
		}

		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
				"068J61035-0018")/*
									 * @res 二代健保數據集沒有正確發佈 。
									 */);
	}

	public static PsndocDefVO getGroupInsuranceVO() throws BusinessException {
		if (getClassMap() != null) {
			return getPsndocDefVOByClassname(getClassMap().get("TWHRA21000000000DEF6"));
		}

		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
				"068J61035-0019")/*
									 * @res 員人團保資訊數據集沒有正確發佈 。
									 */);
	}

	public static PsndocDefVO getGroupInsuranceDetailVO() throws BusinessException {
		if (getClassMap() != null) {
			return getPsndocDefVOByClassname(getClassMap().get("TWHRA21000000000DEF7"));
		}

		throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
				"068J61035-0020")/*
									 * @res 員人團保投保明細數據集沒有正確發佈 。
									 */);
	}

	private static PsndocDefVO getPsndocDefVOByClassname(String classname) throws BusinessException {
		Class<?> classVO;
		try {
			classVO = Class.forName(classname);
			return (PsndocDefVO) classVO.newInstance();
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}
}
