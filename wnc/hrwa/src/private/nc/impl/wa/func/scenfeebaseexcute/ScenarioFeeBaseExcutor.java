package nc.impl.wa.func.scenfeebaseexcute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.impl.wa.func.AbstractPreExcutorFormulaParse;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.pub.WaLoginContext;

public class ScenarioFeeBaseExcutor extends AbstractPreExcutorFormulaParse {
	private BaseDAO basedao;

	public BaseDAO getBasedao() {
		if (null == basedao) {
			basedao = new BaseDAO();
		}
		return basedao;
	}

	@Override
	public void excute(Object formula, WaLoginContext context) throws BusinessException {
		String[] arguments = getArguments(formula.toString());
		// ��ȡ����
		String isflag = arguments[0];
		UFBoolean flag = new UFBoolean(isflag.equals("0") ? false : true);
		// ��ѯ����Ҫ�������Ա
		String sql = "select DISTINCT pk_psndoc from wa_cacu_data where pk_wa_class= '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";

		@SuppressWarnings("unchecked")
		ArrayList<String> pk_psndocs = (ArrayList<String>) getBasedao().executeQuery(sql, new ColumnListProcessor());
		// ������Ա��ѯ��ÿ���˵���ǲ���ں���ְ����
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(pk_psndocs.toArray(new String[0]));
		List<PsndocVO> psndocs = (List<PsndocVO>) this.getBasedao().retrieveByClause(PsndocVO.class,
				"pk_psndoc in(" + psndocsInSQL + ") and dr=0 and (sepaydate is not null or sepaydate <> '~')");
		List<String> newpsndocs = new ArrayList<String>();
		if (null == psndocs || psndocs.size() <= 0) {
			return;
		} else {
			for (PsndocVO psndoc : psndocs) {
				newpsndocs.add(psndoc.getPk_psndoc());
			}
		}
		List<PsnOrgVO> psnorgs = null;
		if (null != newpsndocs && newpsndocs.size() > 0) {
			String psndocsin = insql.getInSQL(newpsndocs.toArray(new String[0]));
			psnorgs = (List<PsnOrgVO>) this.getBasedao().retrieveByClause(PsnOrgVO.class,
					"pk_psndoc in(" + psndocsin + ") and dr=0 and (joinsysdate is not null or joinsysdate <> '~')");
		}
		// ��Աpk:��ǲ���ڣ���ְ����
		Map<String, String> psnmap = new HashMap<String, String>();
		if (null != psndocs && null != psnorgs) {
			for (PsndocVO psndoc : psndocs) {
				for (PsnOrgVO psnorg : psnorgs) {
					if (psndoc.getPk_psndoc().equals(psnorg.getPk_psndoc())) {
						psnmap.put(psndoc.getPk_psndoc(),
								psndoc.getAttributeValue("sepaydate") + "," + psnorg.getJoinsysdate());
					}
				}
			}
		}
		// ��������
		Map<String, UFDouble> strmap = getyears(psnmap, flag);
		// ��д����ʱ��
		for (String str : strmap.keySet()) {
			String sqls = "update wa_cacu_data set cacu_value = '" + strmap.get(str) + "' where  " + "pk_wa_class = '"
					+ context.getPk_wa_class() + "' and creator = '" + context.getPk_loginUser()
					+ "' and pk_psndoc = '" + str + "'";
			this.getBasedao().executeUpdate(sqls);
		}
	}

	private Map<String, UFDouble> getyears(Map<String, String> psnmap, UFBoolean flag) {
		Map<String, UFDouble> maps = new HashMap<String, UFDouble>();
		for (String map : psnmap.keySet()) {
			String pk_psndoc = map;
			UFDouble years = UFDouble.ZERO_DBL;
			// ��ǲ��
			UFDate sepaydate = new UFDate(psnmap.get(map).split(",")[0]);
			// ��ְ��
			UFDate joinsysdate = new UFDate(psnmap.get(map).split(",")[1]);
			if (sepaydate.after(joinsysdate)) {
				int day = sepaydate.getDay();
				int month = sepaydate.getMonth();
				int year = sepaydate.getYear();
				if (sepaydate.getDay() >= joinsysdate.getDay()) {
					// ��ǲ�յ��������ְ�յ���
					day = sepaydate.getDay() - joinsysdate.getDay() + 1;
				} else {
					// ��ǲ�յ���С����ְ�յ���
					// ��Ҫ�жϵ�ǰ�·��Ǵ��»���С�� ���½�30��,С�½�31��
					if (caculate(sepaydate.getYear(), sepaydate.getMonth(), sepaydate.getDay()) < 31) {
						day = sepaydate.getDay() + 31 - joinsysdate.getDay() + 1;
					} else {
						day = sepaydate.getDay() + 30 - joinsysdate.getDay() + 1;
					}
					month = month - 1;
				}
				if (month >= joinsysdate.getMonth()) {
					// ��ǲ�յ��·ݴ�����ְ�յ��·�
					month = sepaydate.getMonth() - joinsysdate.getMonth();
				} else {
					// ��ǲ�յ��·�С����ְ�յ��·�
					month = sepaydate.getMonth() + 12 - joinsysdate.getMonth();
					year = year - 1;
				}
				if (year > joinsysdate.getYear()) {
					year = year - joinsysdate.getYear();
				}
				// ���Ƿ�ֱ���Mλ����
				if (flag.booleanValue()) {
					if (day > 0) {
						// ��������� �ǣ��t�õ�x��m+1����
						years = new UFDouble(((new UFDouble(month).add(1)).div(12)).add(year));
					} else {
						years = new UFDouble((new UFDouble(month).div(12)).add(year));
					}
				} else {
					// ��+((��*30+��)/30/12)����ں����В�λ
					UFDouble monthday = new UFDouble(month * 30 + day);
					years = new UFDouble((monthday.div(30).div(12).add(year)));
				}
			}
			maps.put(pk_psndoc, years);
		}
		return maps;
	}

	public static int caculate(int year, int month, int day) {
		int days = 0;
		for (int i = 1; i < month; i++) {
			switch (i) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				days = 31;
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				days = 30;
				break;
			case 2:
				if ((year % 400 == 0) || (year % 4 == 0) || (year % 100 != 0)) {
					days = 29;
				} else {
					days = 28;
				}
				break;
			default:
				break;
			}
			day += days;

		}
		return day;
	}
}
