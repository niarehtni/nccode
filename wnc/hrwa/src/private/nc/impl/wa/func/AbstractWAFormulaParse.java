package nc.impl.wa.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.ResHelper;
import nc.impl.hr.formula.parser.AbstractFormulaParser;
import nc.impl.hr.formula.parser.FormulaParseHelper;
import nc.impl.pubapp.pattern.database.TempTable;
import nc.impl.wa.paydata.CalculatingDataDEUtil;
import nc.itf.hr.wa.IItemQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pub.wa.salaryencryption.util.SalaryDecryptUtil;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.hr.func.FunctionVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.JavaType;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.category.AssignclsVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.YearPeriodSeperatorVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 
 * @author: zhangg
 * @date: 2010-5-4 ����02:08:07
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public abstract class AbstractWAFormulaParse extends AbstractFormulaParser {
	private static final long serialVersionUID = 1L;
	private BaseDAOManager daoManager = null;
	WaLoginContext context = null;
	FunctionVO functionVO = null;

	public FunctionVO getFunctionVO() {
		return functionVO;
	}

	public void setFunctionVO(FunctionVO functionVO) {
		this.functionVO = functionVO;
	}

	String pk_org = null;

	/**
	 * @author zhangg on 2010-5-10
	 * @return the daoManager
	 */
	public BaseDAOManager getDaoManager() {
		if (daoManager == null) {
			daoManager = new BaseDAOManager();
		}
		return daoManager;
	}

	public String coalesce(String selectSql) {

		// �������ֵ�ͣ�ʹ��0 ��������ַ��ͣ�ʹ�ÿ��ַ�������
		// 1 �ַ���
		// 0 ��ֵ��
		Integer iitemtype = TypeEnumVO.CHARTYPE.value();
		Object o = getContext().getInitData();
		if (o != null && o instanceof WaClassItemVO) {
			WaClassItemVO vo = (WaClassItemVO) o;
			iitemtype = vo.getIitemtype();

		}
		if (iitemtype.equals(TypeEnumVO.FLOATTYPE.value())) {
			return " coalesce((" + selectSql + "), 0)";
		} else {
			return " (" + selectSql + ")";
		}

	}

	public WaLoginContext getContext() {
		// TODO Auto-generated method stub
		return this.context;
	}

	public void setContext(WaLoginContext context) {
		this.context = context;
	}

	public abstract FunctionReplaceVO getReplaceStr(String formula) throws BusinessException;

	@Override
	public String parseAfterValidate(String pk_org, String formula, Object... params) throws BusinessException {
		String outFormula = formula;

		String tempformula = getFormula(outFormula);
		while (tempformula != null) {

			FunctionReplaceVO replaceStr = null;

			// zhoumxc �ϲ���������н�ʷ���ȡֵ 20140902
			if (tempformula.contains("waOtherPeriodData")) {
				replaceStr = getReplaceStr(pk_org, formula);
			} else {
				replaceStr = getReplaceStr(tempformula);
			}

			// guoqtн��ȡ�籣���ݹ�ʽ��3��������Ϊ��4��������
			// �ڶ���ʷ���ݲ����Ķ��������Ҫ��������������ʹ��3������ҲҪʶ��Ϊȡ�籣���ݵĹ�ʽ
			if (!formula.equals(tempformula) && formula.startsWith("valueOfBM")) {
				outFormula = outFormula.replace(formula, replaceStr.getReplaceStr());
			} else {
				outFormula = outFormula.replace(tempformula, replaceStr.getReplaceStr());
			}
			tempformula = getFormula(outFormula);
		}

		return outFormula;

	}

	/**
	 * zhoumxc ��н�ʷ���ȡֵ 20140902
	 */
	public FunctionReplaceVO getReplaceStr(String pk_org, String formula) throws BusinessException {
		String[] arguments = getArguments(formula);

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		String classpk = arguments[0];

		String itemid = arguments[1];
		String period = arguments[2];
		// guoqt�÷���û���ã�����Ӱ��Ч��
		// classpk = trans2OrgPk(classpk);
		String pk_wa_class = arguments[0];

		// zhoumxc 20140819
		// ��дsql��pk_wa_class�����ʮ���룬�����һ���������������дsql��ֱ����pk_wa_class =
		// XXX�������������sql
		String sql = "select classid from  wa_assigncls where pk_sourcecls=? and pk_org=?  ";
		SQLParameter par = new SQLParameter();
		par.addParam(pk_wa_class);
		par.addParam(pk_org);
		Object value = getDaoManager().getBaseDao().executeQuery(sql, par, new ColumnProcessor());
		pk_wa_class = (String) (value != null ? value : pk_wa_class);

		// guoqtн��ȡ��������緽������һ�ڼ�
		setWa_class(pk_wa_class);
		YearPeriodSeperatorVO yearPeriodSeperatorVO = trans2YearPeriodSeperatorVO(period);

		fvo.setAliTableName("wa_data");
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select data_source." + itemid + " "); // 1
		sqlBuffer.append("  from wa_data data_source ");
		sqlBuffer.append(" where data_source.pk_wa_class = '" + pk_wa_class + "' ");
		// sqlBuffer.append(" where data_source.pk_wa_class in ('"+pk_wa_class+"',(select classid from  wa_assigncls where pk_sourcecls='"+pk_wa_class+"' and pk_org='"+pk_org+"' ))");
		sqlBuffer.append("   and data_source.cyear = '" + yearPeriodSeperatorVO.getYear() + "' ");
		sqlBuffer.append("   and data_source.cperiod = '" + yearPeriodSeperatorVO.getPeriod() + "' ");
		sqlBuffer.append("   and data_source.pk_psndoc = wa_data.pk_psndoc and  data_source.stopflag = 'N' ");
		fvo.setReplaceStr(coalesce(sqlBuffer.toString()));

		return fvo;

	}

	/**
	 * guoqt ��н�ʷ���ȡֵ 20141015
	 */
	public void setWa_class(String pk_wa_class) throws BusinessException {
		setClassforPeriod(pk_wa_class);
	}

	public void setClassforPeriod(String pk_wa_class) throws BusinessException {
	}

	public String getFormula(String inFormula) throws BusinessException {

		Pattern pattern = Pattern.compile(functionVO.getPattern());
		Matcher matcher = pattern.matcher(inFormula);
		// ���к���
		if (matcher.find()) {
			return matcher.group();
		}
		// guoqtн��ȡ�籣���ݹ�ʽ��3��������Ϊ��4��������
		// �ڶ���ʷ���ݲ����Ķ��������Ҫ��������������ʹ��3������ҲҪʶ��Ϊȡ�籣���ݵĹ�ʽ
		if (inFormula.startsWith("valueOfBM") && functionVO.getPattern().startsWith("valueOfBM") && !matcher.find()) {
			return inFormula.replace(")", ",f_f)");
		}
		return null;

	}

	@Override
	public String parse(String pk_org, String formula, Object... params) throws BusinessException {
		context = (WaLoginContext) params[0];

		// �õ�functionvo
		functionVO = (FunctionVO) params[1];
		this.pk_org = pk_org;

		if (validate(formula)) {
			return parseAfterValidate(pk_org, formula, params);
		}

		return formula;
	}

	public boolean validate(String formula) throws BusinessException {
		if (formula == null || functionVO == null || functionVO.getPattern() == null) {
			throw new BusinessException(ResHelper.getString("6001formula", "06001formula0020")
			/* @res "��ʽ�������õ�����Ϊ�ա�" */);
		}

		return FormulaParseHelper.isExist(formula, functionVO);
	}

	/**
	 * ������ŷ��䵽��֯�£� Ӧ���ҵ���Ӧ�ķ�����������
	 * 
	 * @author zhangg on 2010-5-10
	 * @param pk_wa_class
	 * @return
	 */
	public String trans2OrgPk(String pk_wa_class) throws BusinessException {
		String sql = "select classid from wa_assigncls where pk_sourcecls = ? and pk_org = ? ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_wa_class);
		parameter.addParam(pk_org);

		AssignclsVO wa_class = getDaoManager().executeQueryVO(sql, parameter, AssignclsVO.class);
		if (wa_class != null) {
			pk_wa_class = wa_class.getClassid();
		}

		return pk_wa_class;
	}

	/**
	 * 
	 * 
	 * @author zhangg on 2010-5-10
	 * @param pk_wa_class
	 * @return
	 */
	public WaClassItemVO getClassItemVO(String itemKey) throws BusinessException {
		String sql = "select * from wa_classitem where pk_wa_class = ? and cyear = ? and cperiod = ? and itemkey = ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(getContext().getPk_wa_class());
		parameter.addParam(getContext().getWaYear());
		parameter.addParam(getContext().getWaPeriod());
		parameter.addParam(itemKey);

		WaClassItemVO wa_class = getDaoManager().executeQueryVO(sql, parameter, WaClassItemVO.class);

		return wa_class;
	}

	/**
	 * ����н����ĿItemKeyȡ�ñ���֯�µĶ�Ӧ��н����Ŀ��pK
	 * 
	 * @param itemKey
	 * @return
	 * @throws BusinessException
	 */
	public String getItemPkByItemKey(String itemKey) throws BusinessException {
		// String sql =
		// "select pk_wa_item from wa_item where  itemkey = ? and pk_org = ?";

		String condition = " itemkey = '" + itemKey + "' ";

		IItemQueryService queryService = NCLocator.getInstance().lookup(IItemQueryService.class);

		WaItemVO[] itemvo = queryService.queryWaItemVOsByCondition(getContext(), condition);

		// SQLParameter parameter = new SQLParameter();
		// parameter.addParam(itemKey);
		// parameter.addParam(getContext().getPk_org());
		// WaItemVO wa_itemVO = getDaoManager().executeQueryVO(sql, parameter,
		// WaItemVO.class);
		if (ArrayUtils.isEmpty(itemvo)) {
			return null;
		} else {
			return itemvo[0].getPk_wa_item();
		}
		// WaItemVO wa_itemVO = itemvo[0];
		// return wa_itemVO == null? null:wa_itemVO.getPk_wa_item();
	}

	/**
	 * С�ڸ��ڼ������ڼ䣬 ������һ�ڼ�
	 * 
	 * @param waGlobalVO
	 * @return
	 * @throws BusinessException
	 *             2007-12-4 ����11:27:27
	 * @author zhoucx
	 */
	public YearPeriodSeperatorVO getPrePeriod() throws BusinessException {

		return getAbsPeriod(1);
	}

	/**
	 * ͨ������ڼ��Ҿ����ڼ䣬���Ҳ����򷵻�{1900��01}
	 * 
	 * @param i
	 *            int ���ƫ������ǰ���ڼ䣬��0ʱ��Ч������ֱ�ӷ��ص�ǰ�����Ҳ����򷵻�{1900��01}��
	 * @exception javsql.SQLException
	 *                �쳣˵����
	 */
	public YearPeriodSeperatorVO getAbsPeriod(int i) throws BusinessException {
		if (i == 0) {
			return getSamePeriod();// ���ص�ǰ�ڼ�
		}
		YearPeriodSeperatorVO yearPeriodSeperatorVO = new YearPeriodSeperatorVO("190001");

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select (cyear || cperiod) yearperiod "); // 1
		sqlB.append("  from wa_periodstate ");
		sqlB.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlB.append("                         wa_period.pk_wa_period) ");
		sqlB.append(" where pk_wa_class = '" + getContext().getPk_wa_class() + "' ");
		sqlB.append("   and (cyear || cperiod) <= '" + getContext().getWaYear() + getContext().getWaPeriod() + "' ");
		sqlB.append(" order by yearperiod desc");

		YearPeriodSeperatorVO[] periodSeperatorVOs = getDaoManager().executeQueryVOs(sqlB.toString(),
				YearPeriodSeperatorVO.class);
		if (periodSeperatorVOs != null) {
			for (int j = 0; j < periodSeperatorVOs.length; j++) {
				if (j == i) {// �ҵ���,��ֵ���˳�
					return periodSeperatorVOs[j];
				}
			}
		}
		return yearPeriodSeperatorVO;
	}

	/**
	 * 
	 * @author zhangg on 2010-5-11
	 * @return
	 */

	public YearPeriodSeperatorVO getSamePeriod() {

		return new YearPeriodSeperatorVO(getContext().getWaYear() + getContext().getWaPeriod());
	}

	/**
	 * period syntax '201008'
	 * 
	 * @author zhangg on 2010-5-11
	 * @param period
	 * @return
	 * @throws BusinessException
	 */
	public YearPeriodSeperatorVO trans2YearPeriodSeperatorVO(String period) throws BusinessException {
		if (period.equals(same_period)) {
			return getSamePeriod();
		} else if (period.equals(pre_period)) {
			return getPrePeriod();
		}

		return new YearPeriodSeperatorVO(period);

	}

	protected int getDataBaseType() {
		return getDaoManager().getBaseDao().getDBType();
	}

	/**
	 * ����update����ж��Ƿ��һ�μ������Կ��ڵ�����
	 * 
	 * @author guoqt on 2014-7-10
	 * @return
	 * @throws BusinessException
	 */
	public boolean isFirstupdateTa(String update) throws BusinessException {
		// 20150824 xiejie3 ��ȡsql
		// String exist=update.substring(115, 1331);
		String exist = update.substring(update.indexOf("from"), update.lastIndexOf("where"));
		// if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
		// exist=update.substring(107, 1317);
		// }
		// end
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select wa_cacu_data.pk_ta_org  ");
		sqlBuffer.append(exist);
		// 2015-09-22 zhousze
		// NCdp205497544��������ĿȡֵΪ�����ݼٲ�������ʱ��н�ʷ��ż���ʱ���㲻�ˣ������ȡ��SQL��
		// inner join��ʱ���ظ��ˣ����±���ͬ�ı����� begin
		String str = sqlBuffer.toString();
		if (!str.substring(str.lastIndexOf(",") + 1).trim().equalsIgnoreCase("wa_cacu_data")) {
			sqlBuffer
					.append("  , wa_cacu_data where tadata.workorg = wa_cacu_data.workorg and wa_cacu_data.tayear is null or wa_cacu_data.taperiod is null or wa_cacu_data.pk_ta_org is null ");
		} else {
			sqlBuffer
					.append(" where tadata.workorg = wa_cacu_data.workorg and wa_cacu_data.tayear is null or wa_cacu_data.taperiod is null or wa_cacu_data.pk_ta_org is null ");
		}
		// end
		BaseDAOManager manager = new BaseDAOManager();
		// guoqt���н�ʼ����м���Ŀ�����ȡ��ڼ䡢��֯����һ��Ϊ�գ���֤���ǵ�һ�μ��㿼����Ŀ
		// todo DB2���ݿ�
		return manager.isValueExist(sqlBuffer.toString());
	}

	@SuppressWarnings("unchecked")
	protected String getLastPeriodSQL(String pk_wa_class, String itemid, YearPeriodSeperatorVO lastPeriodVO)
			throws DAOException, BusinessException {
		// ��һ���g
		StringBuffer strSQL = new StringBuffer();
		strSQL.append("select pk_wa_data, pk_psndoc, " + itemid + " "); // 1
		strSQL.append("  from wa_data ");
		strSQL.append(" where pk_wa_class = '" + pk_wa_class + "' ");
		strSQL.append("   and cyear = '" + lastPeriodVO.getYear() + "' ");
		strSQL.append("   and cperiod = '" + lastPeriodVO.getPeriod() + "' and stopflag='N'");

		List<Map<String, Object>> valueMapList = (List<Map<String, Object>>) new BaseDAO().executeQuery(
				strSQL.toString(), new MapListProcessor());
		List<List<Object>> valueList = new ArrayList<List<Object>>();
		StringBuffer sqlBuffer = new StringBuffer();
		if (valueMapList != null && valueMapList.size() > 0) {
			List<String> checkedList = new ArrayList<String>();
			for (Map<String, Object> valueMap : valueMapList) {
				checkedList.add((String) valueMap.get("pk_wa_data"));
			}

			List<String> decryptedList = CalculatingDataDEUtil.filterDecryptedExists(checkedList);

			for (Map<String, Object> valueMap : valueMapList) {
				UFDouble value = valueMap.get(itemid) == null ? UFDouble.ZERO_DBL : (UFDouble) valueMap.get(itemid);
				if (!decryptedList.contains(valueMap.get("pk_wa_data"))) {
					// δ����
					value = new UFDouble(SalaryDecryptUtil.decrypt(value.doubleValue()));
				}

				List<Object> values = new ArrayList<Object>();
				values.add(valueMap.get("pk_wa_data"));
				values.add(valueMap.get("pk_psndoc"));
				values.add(value);
				valueList.add(values);
			}

			TempTable tmpTbl = new TempTable();
			String tableName = tmpTbl.getTempTable("wa_cacu_temp_" + this.getContext().getPk_loginUser().substring(14),
					new String[] { "pk_wa_data", "pk_psndoc", "itemvalue" }, new String[] { "char(20)", "char(20)",
							"number(31,8)" }, new JavaType[] { JavaType.String, JavaType.String, JavaType.UFDouble },
					valueList);

			sqlBuffer.append("select data_source." + itemid + " "); // 1
			sqlBuffer.append("  from " + tableName + " data_source ");
			sqlBuffer.append(" where data_source.pk_wa_class = '" + getPk_wa_class() + "' ");
			sqlBuffer.append("   and data_source.cyear = '" + lastPeriodVO.getYear() + "' ");
			sqlBuffer.append("   and data_source.cperiod = '" + lastPeriodVO.getPeriod() + "' ");
			sqlBuffer.append("   and data_source.pk_psndoc = wa_data.pk_psndoc ");
		} else {
			sqlBuffer.append("0");
		}
		return sqlBuffer.toString();
	}
}