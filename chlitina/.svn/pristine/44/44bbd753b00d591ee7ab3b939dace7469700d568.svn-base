package nc.impl.wa.paydata.precacu;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.impl.wa.paydata.tax.TaxFormulaVO;
import nc.impl.wa.paydata.tax.WadataTaxInfPreProcess4TW;
import nc.itf.hr.wa.IWaPeriodQuery;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.pubitf.uapbd.CurrencyRateUtil;
import nc.vo.bd.currinfo.CurrinfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVOHelper;

@SuppressWarnings("restriction")
public class TaxFormulaPreExcutor4TW extends AbstractFormulaExecutor {
    int convMode = 0;

    @Override
    public void excute(Object inTaxFormulaVO, WaLoginContext context) throws BusinessException {
	if (inTaxFormulaVO instanceof TaxFormulaVO) {
	    // 传递扣税信息到中间表
	    TaxFormulaVO taxFormulaVO = (TaxFormulaVO) inTaxFormulaVO;
	    WadataTaxInfPreProcess4TW taxInfPreProcess = new WadataTaxInfPreProcess4TW();
	    taxInfPreProcess.transferTaxCacuData(taxFormulaVO, context);

	    // 薪资档案中的扣税明细 与税率表信息
	    // 薪资补发与普通薪资类别计算一样。都采用
	    String pk_wa_class = context.getPk_wa_class();
	    String useid = context.getPk_loginUser();

	    // 查找转换模式
	    setCurrenyConvmode(context);
	    // 进行税率转换 ：由发薪币种，转换为计税币种
	    currencyrateBeforeCaculate(pk_wa_class, useid);

	    IWaPeriodQuery periodQry = (IWaPeriodQuery) NCLocator.getInstance().lookup(IWaPeriodQuery.class.getName());
	    PeriodVO[] prds = periodQry.getPeriodsByScheme(WaLoginVOHelper.getParentClass(context.getWaLoginVO())
		    .getPk_periodscheme());
	    String strBeginDate = "";
	    String strEndDate = "";
	    for (PeriodVO prd : prds) {
		if (prd.getCyear().equals(context.getCyear()) && prd.getCperiod().equals(context.getCperiod())) {
		    strBeginDate = prd.getCstartdate().toStdString();
		    strEndDate = prd.getCenddate().toStdString();
		    break;
		}
	    }

	    PersistenceManager sessionManager = null;
	    try {
		sessionManager = PersistenceManager.getInstance();
		JdbcSession session = sessionManager.getJdbcSession();
		UFDouble minTaxAmount = getBaseDocUFDoubleValue(context.getPk_org(), "TWSP0014"); // 代扣所得稅最小扣除額
		UFDouble taxFreeAmount = getBaseDocUFDoubleValue(context.getPk_org(), "TWSP0001"); // 免稅額(標準)
		UFDouble stdDeductAmount = getBaseDocUFDoubleValue(context.getPk_org(), "TWSP0004"); // 標準扣除額（夫妻合併）
		UFDouble spcDeductAmount = getBaseDocUFDoubleValue(context.getPk_org(), "TWSP0005"); // 特別扣除額
		// String step =
		// "(case when wa_cacu_data.tax_base<110000 then 500 when wa_cacu_data.tax_base<120000 then 1000 when wa_cacu_data.tax_base<150000 then 1500 else 2000 end)";
		String sql = "";
		sql += "UPDATE wa_cacu_data ";
		sql += "SET          cacu_value = CASE WHEN Final.Tax <= " + minTaxAmount.toString()
			+ " THEN 0 ELSE Final.Tax END ";
		sql += "FROM     wa_cacu_data INNER JOIN ";
		sql += "     (SELECT ROUND((Temp.AnnualSalary * wa_taxtable.ntaxrate / 100 - wa_taxtable.nquickdebuct) / 120, 0, 1)  ";
		sql += "        * 10 AS Tax, Temp.pk_cacu_data ";
		sql += "       FROM      (SELECT (round((ISNULL(wa_cacu_data.tax_base, 0) - 1) / 500, 0, 1) * 500 + 1)  ";
		sql += "          * 12 - (" + taxFreeAmount.toString() + " * ";
		sql += "             (1+ (select count(pk_psndoc_sub) from hi_psndoc_family where dr= 0 and pk_psndoc = wa_cacu_data.pk_psndoc and isnhifeed = 'Y')) + "
			+ stdDeductAmount.toString() + "  ";
		sql += "          + " + spcDeductAmount.toString() + ") AS AnnualSalary, wa_data.taxtableid,  ";
		sql += "           wa_cacu_data.pk_cacu_data ";
		sql += "         FROM      wa_cacu_data INNER JOIN ";
		sql += "          wa_data ON wa_data.pk_wa_data = wa_cacu_data.pk_wa_data INNER JOIN ";
		sql += "          org_hrorg ON org_hrorg.pk_hrorg = wa_data.pk_org ";
		sql += "         WHERE  (wa_cacu_data.pk_wa_class = '" + pk_wa_class
			+ "') AND (wa_cacu_data.creator = '" + useid + "')) AS Temp INNER JOIN ";
		sql += "        wa_taxtable ON wa_taxtable.pk_wa_taxbase = Temp.taxtableid AND  ";
		sql += "        Temp.AnnualSalary > wa_taxtable.nminamount ";
		sql += "       WHERE  (wa_taxtable.nmaxamount IS NULL) OR ";
		sql += "        (Temp.AnnualSalary < wa_taxtable.nmaxamount)) AS Final ON  ";
		sql += "          wa_cacu_data.pk_cacu_data = Final.pk_cacu_data ";
		sql += " inner join bd_psndoc on bd_psndoc.pk_psndoc = wa_cacu_data.pk_psndoc ";
		sql += " inner join wa_data on wa_data.pk_wa_data = wa_cacu_data.pk_wa_data ";
		// sql += " where " + getPsnFilter(strBeginDate);
		sql += " where wa_data.taxtype <> 2 ";
		// sql += " and bd_psndoc.idtype=5";
		session.executeUpdate(sql);

		sql = " update wa_cacu_data ";
		sql += " 	set cacu_value = ROUND((case when (wa_cacu_data.tax_base >= wa_taxtable.nminamount and (wa_taxtable.nmaxamount is null or wa_cacu_data.tax_base < wa_taxtable.nmaxamount)) then  ";
		sql += " 	wa_cacu_data.tax_base * ISNULL(wa_taxtable.ntaxrate, 0) / 100 - wa_taxtable.nquickdebuct else 0 end), 0, 0) ";
		sql += " from wa_cacu_data  ";
		sql += "  inner join wa_data on wa_data.pk_wa_data = wa_cacu_data.pk_wa_data ";
		sql += "  inner join org_hrorg ON org_hrorg.pk_hrorg = wa_data.pk_org  ";
		sql += "  inner join wa_taxtable on wa_taxtable.pk_wa_taxbase = wa_data.taxtableid ";
		sql += "  inner join wa_taxbase on wa_taxbase.pk_wa_taxbase = wa_taxtable.pk_wa_taxbase ";
		sql += "  inner join bd_countryzone on wa_taxbase.pk_country = bd_countryzone.pk_country ";
		sql += "  inner join bd_psndoc on bd_psndoc.pk_psndoc = wa_cacu_data.pk_psndoc  ";
		sql += "  where wa_data.taxtype <> 2 and wa_cacu_data.pk_wa_class = '" + pk_wa_class
			+ "' and wa_cacu_data.creator = '" + useid + "' and wa_taxbase.itbltype=1";
		session.executeUpdate(sql);
	    } catch (DbException e) {
		throw new DAOException(e.getMessage());
	    } finally {
		if (sessionManager != null) {
		    sessionManager.release();
		}
	    }

	    // 进行税率转换 ：由计税币种，转换为发薪币种
	    currencyrateAfterCaculate(pk_wa_class, useid);
	}
    }

    private UFDouble getBaseDocUFDoubleValue(String pk_org, String paramCode) throws BusinessException {
	IBasedocPubQuery baseQry = NCLocator.getInstance().lookup(IBasedocPubQuery.class);
	BaseDocVO baseDoc = baseQry.queryBaseDocByCode(pk_org, paramCode);
	if (baseDoc == null) {
	    throw new BusinessException("未定義薪資參數：" + paramCode);
	}

	UFDouble value = UFDouble.ZERO_DBL;
	if (baseDoc.getDoctype() == 1) {
	    value = baseDoc.getNumbervalue();
	} else if (baseDoc.getDoctype() == 2) {
	    value = baseDoc.getNumbervalue().div(100);
	}

	return value;
    }

    private String getPsnFilter(String strBeginDate) {
	String sql = "";
	sql += "(wa_cacu_data.pk_psndoc NOT IN ";
	sql += "              (SELECT pk_psndoc ";
	sql += "                FROM      hi_psnjob ";
	sql += "                WHERE  (pk_psnjob IN ";
	sql += "                     (SELECT psnjob.pk_psnjob ";
	sql += "              FROM      hi_psnjob AS psnjob INNER JOIN ";
	sql += "                        hi_psnorg AS psnorg ON  ";
	sql += "                        psnjob.pk_psnorg = psnorg.pk_psnorg ";
	sql += "              WHERE  (psnorg.psntype = 0) AND  ";
	sql += "                      (psnorg.indocflag = N'Y') AND ";
	sql += "                      (psnjob.lastflag = N'Y') AND  ";
	sql += "                      (psnjob.ismainjob = N'Y') AND DATEDIFF(DAY, DATEADD(day, - 1, psnjob.begindate), '"
		+ strBeginDate + "') < 0)) AND (pk_hrorg IN ";
	sql += "                     (SELECT pk_adminorg ";
	sql += "              FROM      org_admin_enable))))";
	return sql;
    }

    /**
     * 折算模式：0 元币种 × 汇率 ＝ 目的币种 1 元币种 / 汇率 ＝ 目的币种
     * 
     * @param src_currency_pk
     * @param dest_currency_pk
     * @return
     * @throws BusinessException
     */
    public void setCurrenyConvmode(WaLoginContext context) throws BusinessException {

	String src_currency_pk = context.getWaLoginVO().getCurrid();
	String dest_currency_pk = context.getWaLoginVO().getTaxcurrid();
	if (src_currency_pk.equals(dest_currency_pk)) {
	    convMode = 0;
	    return;
	}
	CurrencyRateUtil currencyRateUtil = CurrencyRateUtil.getInstanceByOrg(context.getPk_org());
	CurrinfoVO currinfoVO = currencyRateUtil.getCurrinfoVO(src_currency_pk, dest_currency_pk);
	convMode = currinfoVO.getConvmode();
    }

    private void currencyrateBeforeCaculate(String pk_wa_class, String userid) throws BusinessException {

	// 注意转换模式。
	int convmode = getConvMode();
	String convSign = null;
	if (convmode == 1) {
	    convSign = "/";
	} else {
	    convSign = "*";
	}

	StringBuilder sbd = new StringBuilder();

	sbd.append("  update  wa_cacu_data set  tax_base = tax_base" + convSign + "currencyrate, ");
	sbd.append(" taxed = taxed" + convSign + "currencyrate, ");
	sbd.append("  taxedBase = taxedBase" + convSign + "currencyrate, ");
	sbd.append(" retaxed = retaxed" + convSign + "currencyrate, ");
	sbd.append(" redata = redata" + convSign + "currencyrate, ");
	sbd.append(" redataLasttaxBase = redataLasttaxBase" + convSign + "currencyrate, ");
	sbd.append(" redataLasttax =redataLasttax" + convSign + "currencyrate ");
	sbd.append(" where pk_wa_class = '" + pk_wa_class + "' and creator = '" + userid + "' ");
	executeSQLs(sbd.toString());
    }

    public int getConvMode() {
	return convMode;
    }

    private void currencyrateAfterCaculate(String pk_wa_class, String userid) throws BusinessException {

	// 注意转换模式。
	int convmode = getConvMode();
	String convSign = null;
	if (convmode == 1) {
	    convSign = "*";
	} else {
	    convSign = "/";
	}

	StringBuilder sbd = new StringBuilder();
	sbd.append("  update  wa_cacu_data set  tax_base = tax_base" + convSign + "currencyrate, ");
	sbd.append(" taxed = taxed" + convSign + "currencyrate, ");
	sbd.append("  taxedBase = taxedBase" + convSign + "currencyrate, ");
	sbd.append(" retaxed = retaxed" + convSign + "currencyrate, ");
	sbd.append(" redata = redata" + convSign + "currencyrate, ");
	sbd.append(" redataLasttaxBase = redataLasttaxBase" + convSign + "currencyrate, ");
	sbd.append(" redataLasttax =redataLasttax" + convSign + "currencyrate, ");
	// 扣税要折算回来
	sbd.append(" cacu_value =cacu_value" + convSign + "currencyrate ");
	sbd.append(" where pk_wa_class = '" + pk_wa_class + "' and creator = '" + userid + "' ");
	executeSQLs(sbd.toString());
    }
}
