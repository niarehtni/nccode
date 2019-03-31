package nc.impl.wa.taxupgrade_tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.PubEnv;
import nc.impl.wa.item.ItemServiceImpl;
import nc.itf.hr.wa.IClassItemManageService;
import nc.itf.hr.wa.ITaxupgrade_toolModelService;
import nc.itf.hr.wa.ITaxupgrade_toolQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.ui.wa.pub.WADelegator;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.taxrate.AggTaxBaseVO;
import nc.vo.wa.taxrate.TaxBaseVO;
import nc.vo.wa.taxrate.TaxTableVO;
import nc.vo.wa_tax.TaxupgradeDef;

/**
 * ˰�Ŀ������Թ���
 * 
 * @author: xuhw
 * @date: 2018-12-04
 * @since: eHR V6.5
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class Taxupgrade_toolServiceImpl implements ITaxupgrade_toolModelService {

	private BaseDAOManager dao;
	private Map<String, WaItemVO> taxitemmap;

	/**
	 * ��ʼ�����ŵ�н���ڼ�� ��˰�ڼ������Լ���˰�������
	 * 
	 * @param pk_group
	 */
	public void initGroupWaPeriod(String pk_group) {

	}

	/**
	 * Ԥ�Ƽ��ż������˰�ʱ�
	 * 
	 * @param pk_group
	 */
	public void initTaxTable(String pk_group) {
		ItemServiceImpl itemImpl = new ItemServiceImpl();
		// TaxTableServiceImpl taxtableimpl = new TaxTableServiceImpl();
	}

	/**
	 * ��ʼĳ�������µ�˰����Ŀ ��ʼ5����Ŀ
	 * 
	 */
	@Override
	public void initTaxItems4Group(String pk_group, Map<String, WaItemVO> itemvoMap) throws BusinessException {
		// TODO Auto-generated method stub

		String sql = " select * from wa_item where pk_org = 'GLOBLE00000000000000' and itemkey = 'f_1' ";
		WaItemVO f_1Item = getDao().executeQueryVO(sql, WaItemVO.class);
		String category_id = null;
		if (f_1Item != null) {
			category_id = f_1Item.getCategory_id();
		}
		ItemServiceImpl itemImpl = new ItemServiceImpl();
		WaItemVO itemvo = null;
		String formula = null;
		String formulastr = null;
		WaItemVO waitemVO = null;
		// /**�������˼�*/
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ10, 6, 2);
		waitemVO.setCategory_id(category_id);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ10);
		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ10;
			waitemVO.setVformula(formula);
			waitemVO.setVformulastr(TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ10);
		}

		WaItemVO itemVO10 = itemImpl.insertWaItemVO(waitemVO);

		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ11);
		// /**��Ů����*/
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ11, 6, 2);
		waitemVO.setCategory_id(category_id);
		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ11;
			waitemVO.setVformula(formula);
			waitemVO.setVformulastr(TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ11);
		}

		WaItemVO itemVO11 = itemImpl.insertWaItemVO(waitemVO);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ12);
		// /**��ҽ��*/
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ12, 6, 2);
		waitemVO.setCategory_id(category_id);

		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ12;
			waitemVO.setVformula(formula);
			waitemVO.setVformulastr(TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ12);
		}

		WaItemVO itemVO12 = itemImpl.insertWaItemVO(waitemVO);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ13);
		// /**��������*/
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ13, 6, 2);
		waitemVO.setCategory_id(category_id);

		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ13;
			waitemVO.setVformula(formula);
			waitemVO.setVformulastr(TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ13);
		}
		WaItemVO itemVO13 = itemImpl.insertWaItemVO(waitemVO);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ14);
		// /**�ⷿ����*/
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ14, 6, 2);
		waitemVO.setCategory_id(category_id);

		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ14;
			waitemVO.setVformula(formula);
			waitemVO.setVformulastr(TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ14);
		}

		WaItemVO itemVO14 = itemImpl.insertWaItemVO(waitemVO);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ15);
		// /**ס������*/
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ15, 6, 2);
		waitemVO.setCategory_id(category_id);

		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ15;
			waitemVO.setVformula(formula);
			waitemVO.setVformulastr(TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ15);
		}

		WaItemVO itemVO15 = itemImpl.insertWaItemVO(waitemVO);

		/** ��˰��_�ۼ�ר��Ӧ�� */
		WaItemVO waitemVO16 = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16, 6, 2);
		waitemVO16.setCategory_id(category_id);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16);

		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO16.setIfromflag(2);
		} else {
			waitemVO16.setIfromflag(0);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ16;
			formula = formula.replaceAll("@deu10@", itemVO10.getItemkey());
			formula = formula.replaceAll("@deu11@", itemVO11.getItemkey());
			formula = formula.replaceAll("@deu12@", itemVO12.getItemkey());
			formula = formula.replaceAll("@deu13@", itemVO13.getItemkey());
			formula = formula.replaceAll("@deu14@", itemVO14.getItemkey());
			formula = formula.replaceAll("@deu15@", itemVO15.getItemkey());
			waitemVO16.setVformula(formula);
			formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ16.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ10, "#"), itemVO10.getName());
			formulastr = formulastr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ11, "#"), itemVO11.getName());
			formulastr = formulastr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ12, "#"), itemVO12.getName());
			formulastr = formulastr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ13, "#"), itemVO13.getName());
			formulastr = formulastr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ14, "#"), itemVO14.getName());
			formulastr = formulastr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ15, "#"), itemVO15.getName());
			waitemVO16.setVformulastr(formulastr);
		}

		waitemVO16 = itemImpl.insertWaItemVO(waitemVO16);

		// /**�ۼ�ר���ѿ�*/
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ17, 6, 2);
		waitemVO.setCategory_id(category_id);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ17);
		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ17;
			formula = formula.replaceAll("@tax_deduction_totalable_yz@", waitemVO16.getItemkey());
			waitemVO.setVformula(formula);

			formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ17.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ16, "#"), waitemVO16.getName());
			waitemVO.setVformulastr(formulastr);
		}

		WaItemVO itemVO17 = itemImpl.insertWaItemVO(waitemVO);

		/** ��˰��_����ר��۳��ϼ� */
		WaItemVO waitemVO9 = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ9, 6, 2);
		waitemVO9.setCategory_id(category_id);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ9);

		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO9.setIfromflag(2);
		} else {
			waitemVO9.setIfromflag(0);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ9_1;
			formula = formula.replaceAll("@deu16@", waitemVO16.getItemkey());
			formula = formula.replaceAll("@deu17@", itemVO17.getItemkey());
			waitemVO9.setVformula(formula);
			formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9_1.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ16, "#"), waitemVO16.getName());
			formulastr = formulastr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ17, "#"), itemVO17.getName());
			waitemVO9.setVformulastr(formulastr);
		}

		waitemVO9 = itemImpl.insertWaItemVO(waitemVO9);

		// Ӧ��˰���ö�
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAXABLE_INCOME_YZ1, 6, 2);
		waitemVO.setCategory_id(category_id);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TAXABLE_INCOME_YZ1);
		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAXABLE_INCOME_YZ1;
			formula = formula.replaceAll("@tax_special_deduction_yz@", waitemVO9.getItemkey());
			waitemVO.setVformula(formula);
			formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TAXABLE_INCOME_YZ1.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ9, "#"), waitemVO9.getName());
			waitemVO.setVformulastr(formulastr);
		}
		WaItemVO itemVO1 = itemImpl.insertWaItemVO(waitemVO);

		// �ۼ�Ӧ��˰���ö�Ԥ��
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLE_INCOME_YZ2, 6, 2);
		waitemVO.setCategory_id(category_id);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLE_INCOME_YZ2);
		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TOTAL_TAXABLE_INCOME_YZ2;
			formula = formula.replaceAll("@taxable_income_yz@", itemVO1.getItemkey());
			waitemVO.setVformula(formula);
			formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TOTAL_TAXABLE_INCOME_YZ2.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAXABLE_INCOME_YZ_ZW1, "#"), itemVO1.getName());
			waitemVO.setVformulastr(formulastr);
		}

		WaItemVO itemVO2 = itemImpl.insertWaItemVO(waitemVO);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLEAMT_YZ3);
		// �ۼ�Ӧ��˰
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLEAMT_YZ3, 6, 2);
		waitemVO.setCategory_id(category_id);

		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TOTAL_TAXABLEAMT_YZ3;
			formula = formula.replaceAll("@total_taxable_income_yz@", itemVO2.getItemkey());
			waitemVO.setVformula(formula);

			formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TOTAL_TAXABLEAMT_YZ3.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXABLE_INCOME_YZ_ZW2, "#"), itemVO2.getName());
			waitemVO.setVformulastr(formulastr);
		}

		WaItemVO itemVO3 = itemImpl.insertWaItemVO(waitemVO);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXEDAMT_YZ4);
		// �ۼ��ѿ�˰
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXEDAMT_YZ4, 6, 2);
		waitemVO.setCategory_id(category_id);

		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TOTAL_TAXEDAMT_YZ4;
			formula = formula.replaceAll("@total_taxable_income_yz@", itemVO2.getItemkey());
			formula = formula.replaceAll("@taxable_income_yz@", itemVO1.getItemkey());
			waitemVO.setVformula(formula);

			formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TOTAL_TAXEDAMT_YZ4.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXABLE_INCOME_YZ_ZW2, "#"), itemVO2.getName());
			formulastr = formulastr.replaceAll(this.itemName(TaxupgradeDef.TAXITEM_NAME_TAXABLE_INCOME_YZ_ZW1, "#"),
					itemVO1.getName());
			waitemVO.setVformulastr(formulastr);
		}

		WaItemVO itemVO4 = itemImpl.insertWaItemVO(waitemVO);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TAX_RATE_YZ6);
		// ����˰��
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAX_RATE_YZ6, 6, 2);
		waitemVO.setCategory_id(category_id);

		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_RATE_YZ6;
			formula = formula.replaceAll("@total_taxable_income_yz@", itemVO2.getItemkey());
			waitemVO.setVformula(formula);

			formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TAX_RATE_YZ6.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXABLE_INCOME_YZ_ZW2, "#"), itemVO2.getName());
			waitemVO.setVformulastr(formulastr);
		}

		WaItemVO itemVO6 = itemImpl.insertWaItemVO(waitemVO);

		// /**��˰��_����۳���*/
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAX_QUICK_DEDUCTION_YZ7, 6, 2);
		waitemVO.setCategory_id(category_id);
		itemvo = itemvoMap.get(TaxupgradeDef.TAXITEM_CODE_TAX_QUICK_DEDUCTION_YZ7);
		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_QUICK_DEDUCTION_YZ7;
			formula = formula.replaceAll("@total_taxable_income_yz@", itemVO2.getItemkey());
			waitemVO.setVformula(formula);

			formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TAX_QUICK_DEDUCTION_YZ7.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXABLE_INCOME_YZ_ZW2, "#"), itemVO2.getName());
			waitemVO.setVformulastr(formulastr);
		}

		WaItemVO itemVO7 = itemImpl.insertWaItemVO(waitemVO);

		// /**��˰��_�����۳���*/
		waitemVO = this.makeWaItemVO(itemvoMap, TaxupgradeDef.TAXITEM_CODE_TAX_BASIC_DEDUCTION_YZ8, 6, 2);
		waitemVO.setCategory_id(category_id);

		// �ֹ�����
		if (itemvo.getTaxflag() != null && itemvo.getTaxflag().booleanValue()) {
			waitemVO.setIfromflag(2);
		} else {
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_BASIC_DEDUCTION_YZ8;
			waitemVO.setVformula(formula);
			waitemVO.setVformulastr(TaxupgradeDef.TAXITEM_FORMULASTR_TAX_BASIC_DEDUCTION_YZ8);
		}

		WaItemVO itemVO8 = itemImpl.insertWaItemVO(waitemVO);

		// ���±��ο�˰��ʽ����
		updateGroupF5ToYearTaxFun(itemVO3, itemVO4);
	}

	@Override
	public void initTaxItems4ClassItems(String pk_group, String pk_wa_class, String year, String month) {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyTaxItemsToAnotherWaClasss(String pk_group, WaLoginContext sourceContext,
			List<WaLoginContext> targetContextList) {
		// TODO Auto-generated method stub

	}

	/**
	 * ����ѡ������ݳ�ʼ��˰������
	 * 
	 * @param pk_group
	 * @param waitemvos
	 * @param selectClassVOs
	 * @throws BusinessException
	 */
	public void taxUpgradeToSelectClass(String pk_group, WaItemVO[] waitemvos, GeneralVO[] selectClassVOs,
			String optype) throws BusinessException {
		taxitemmap = null;
		// ��ʼ����н����Ŀ����
		if (TaxupgradeDef.OPTYPE_INITITEM.equals(optype)) {
			// 1���ж��Ƿ��ʼ�˼�����Ŀ
			WaItemVO[] groupTaxItemvos = NCLocator.getInstance().lookup(ITaxupgrade_toolQueryService.class)
					.queryTaxUpgradeItems(pk_group);
			// ˵���Ѿ�������
			if (groupTaxItemvos == null || groupTaxItemvos.length == 0) {
				Map<String, WaItemVO> vomap = new HashMap<String, WaItemVO>();
				// ��ʼ������˰����Ŀ
				for (WaItemVO vo : waitemvos) {
					vomap.put(vo.getCode(), vo);
				}
				initTaxItems4Group(pk_group, vomap);
			} else {
				throw new BusinessException("�Ѿ���ʼ�ɹ��������ظ���ʼ��!");
			}
		}
		// ��ʼ��н�ʷ�����Ŀ - н�ʷ���
		else if (TaxupgradeDef.OPTYPE_INITCLASSITEM.equals(optype)) {
			WaItemVO[] groupTaxItemvos = NCLocator.getInstance().lookup(ITaxupgrade_toolQueryService.class)
					.queryTaxUpgradeItems(pk_group);
			IClassItemManageService classitemService = NCLocator.getInstance().lookup(IClassItemManageService.class);
			if (selectClassVOs != null && selectClassVOs.length > 0) {
				WaLoginContext context = new WaLoginContext();
				String[] pk_wa_items = null;
				if (groupTaxItemvos == null || groupTaxItemvos.length == 0) {
					throw new BusinessException("���ȳ�ʼ��˰����Ŀ!");
				}
				List<String> lisItemKeys = new ArrayList<String>();
				for (WaItemVO itemvo : groupTaxItemvos) {
					lisItemKeys.add(itemvo.getPk_wa_item());
				}
				pk_wa_items = lisItemKeys.toArray(new String[0]);

				for (GeneralVO generalvo : selectClassVOs) {
					String pk_org = generalvo.getAttributeValue("pk_org") + "";
					String cyear = generalvo.getAttributeValue("cyear") + "";
					String cperiod = generalvo.getAttributeValue("cperiod") + "";
					String pk_wa_class = generalvo.getAttributeValue("pk_wa_class") + "";
					context = new WaLoginContext();
					context.setPk_group(pk_group);
					context.setPk_org(pk_org);
					context.setCyear(cyear);
					context.setCperiod(cperiod);
					context.getWaLoginVO().setPk_wa_class(pk_wa_class);
					context.getWaLoginVO().setCyear(cyear);
					context.getWaLoginVO().setCperiod(cperiod);
					PeriodStateVO stateVO = new PeriodStateVO();
					stateVO.setCyear(cyear);
					stateVO.setCperiod(cperiod);
					context.getWaLoginVO().setPeriodVO(stateVO);
					context.setNodeType(NODE_TYPE.ORG_NODE);
					classitemService.batchAddClassItemVOs(context, pk_wa_items);
					// ���±��ο�˰����
					updateF5ToYearTaxFun(context, groupTaxItemvos);
				}
			} else if (selectClassVOs == null || selectClassVOs.length == 0) {
				throw new BusinessException("û�д���������!");
			}
		}
		// Ԥ�����˰��
		else if (TaxupgradeDef.OPTYPE_INITTAXTABLE.equals(optype)) {
			AggTaxBaseVO basevo = makeYearTaxTable();
			String sql = " select * from wa_taxbase where code = 'yearTaxTable_yz' and pk_group = '" + pk_group + "' ";
			TaxBaseVO[] taxBaseVOs = getDao().executeQueryVOs(sql, TaxBaseVO.class);
			if (taxBaseVOs == null || taxBaseVOs.length == 0) {
				WADelegator.getTaxTable().insert(basevo);
			} else {
				throw new BusinessException("�Ѿ�Ԥ�ƹ��������ظ�Ԥ�ƣ�");
			}
		}
		// �����ڼ�
		else if (TaxupgradeDef.OPTYPE_INITTAXPERIOD.equals(optype)) {
			String sql = null;
			if (new BaseDAO().getDBType() == DBUtil.SQLSERVER) {
				sql = " update wa_period set taxyear = cyear, taxperiod = cperiod where     pk_periodscheme in (select pk_periodscheme from wa_periodscheme where pk_group = '"
						+ pk_group + "')";
			} else {
				sql = " update wa_period set taxyear = cyear, taxperiod = cperiod where  pk_periodscheme in (select pk_periodscheme from wa_periodscheme where pk_group = '"
						+ pk_group + "')";
			}
			getDao().getBaseDao().executeUpdate(sql);
		}
		// ר��ӹ�ʽ����
		else if (TaxupgradeDef.ADDDEDUCTION_FORMULA_UPDATE.equals(optype)) {

		}
		// һ��ж��
		else if (TaxupgradeDef.OPTYPE_UNINIT.equals(optype)) {
			StringBuffer sb = new StringBuffer();
			sb.append(
					" delete from wa_classitem where itemkey in (select itemkey from wa_item where code in  ('tax_deduction_yz',");
			sb.append(" 'taxable_income_yz',");
			sb.append(" 'tax_deduction_parent_yz',");
			sb.append(" 'tax_deduction_child_yz',");
			sb.append(" 'tax_deduction_health_yz',");
			sb.append(" 'tax_deduction_education_yz',");
			sb.append(" 'tax_deduction_hoursezu_yz',");
			sb.append(" 'tax_deduction_house_yz',");
			sb.append(" 'total_taxable_income_yz',");
			sb.append(" 'total_taxableamt_yz',");
			sb.append(" 'tax_deduction_totalable_yz',");
			sb.append(" 'tax_deduction_totaled_yz',");
			sb.append(" 'total_taxedamt_yz',");
			sb.append(" 'tax_rate_yz',");
			sb.append(" 'quick_deduction_yz',");
			sb.append(" 'tax_basic_deduction_yz')) and pk_group = '" + pk_group + "' ");
			getDao().getBaseDao().executeUpdate(sb.toString());

			sb = new StringBuffer();
			sb.append(" delete from wa_item where code in  ('tax_deduction_yz',");
			sb.append(" 'taxable_income_yz',");
			sb.append(" 'tax_deduction_parent_yz',");
			sb.append(" 'tax_deduction_child_yz',");
			sb.append(" 'tax_deduction_health_yz',");
			sb.append(" 'tax_deduction_education_yz',");
			sb.append(" 'tax_deduction_hoursezu_yz',");
			sb.append(" 'tax_deduction_house_yz',");
			sb.append(" 'tax_deduction_totalable_yz',");
			sb.append(" 'tax_deduction_totaled_yz',");
			sb.append(" 'total_taxable_income_yz',");
			sb.append(" 'total_taxableamt_yz',");
			sb.append(" 'total_taxedamt_yz',");
			sb.append(" 'tax_rate_yz',");
			sb.append(" 'quick_deduction_yz',");
			sb.append(" 'tax_basic_deduction_yz') and pk_group = '" + pk_group + "' ");
			getDao().getBaseDao().executeUpdate(sb.toString());
			sb = new StringBuffer();
			sb.append(
					" update wa_classitem set  vformula = 'taxRate(0,wa_data.f_4,null,null) ',VFORMULASTR = 'taxRate(0,[����н����Ŀ.���ο�˰����],null,null)' where vformula like 'taxRate(3,%'  and pk_group = '"
							+ pk_group + "' ");
			getDao().getBaseDao().executeUpdate(sb.toString());

			updateGroupF5ToSystemItem();
		}
		// �Ѿ���������˰�ķ����������ר��ӿ۳�������
		else if (TaxupgradeDef.OPTYPE_UPGRADE_SPECIALDEDUCTION.equals(optype)) {
			// 1������н����Ŀ����
			// ������������н����Ŀ �ۼ�ר���ѿۣ��ۼ�ר��Ӧ��
			// �Ȳ�ѯ�Ƿ��Ѿ�����ר��ӵ�����

			// �޸�ר��ӿ۳��ϼƹ�ʽ
			// 2������ר��ӿ۳��ϼƹ�ʽ Ϊ�ۼ�ר��Ӧ�� - �ۼ�ר���ѿ�
			upgradeSpecialDeduction4WaItem();
		}
	}

	/**
	 * ����Ԥ����Ŀ
	 * 
	 * @param waitemVO
	 * @param itemvoMap
	 * @param code
	 * @param fromFlag
	 * @param formula
	 * @param formulastr
	 */
	public WaItemVO makeWaItemVO(Map<String, WaItemVO> itemvoMap, String code, int fromFlag, int property) {
		return new Taxupgrade_toolQueryServiceImpl().makeWaItemVO(itemvoMap, code, null, fromFlag, property);
	}

	public WaItemVO makeWaItemVO(Map<String, WaItemVO> itemvoMap, String code, String name, int fromFlag,
			int property) {
		return new Taxupgrade_toolQueryServiceImpl().makeWaItemVO(itemvoMap, code, name, fromFlag, property);
	}

	private String itemName(String key, String flag) {
		return flag + key + flag;
	}

	/**
	 * �������˰�ʱ�
	 */
	private AggTaxBaseVO makeYearTaxTable() {
		AggTaxBaseVO vo = new AggTaxBaseVO();
		TaxBaseVO pvo = new TaxBaseVO();
		vo.setParentVO(pvo);
		pvo.setCode("yearTaxTable_yz");
		pvo.setItbltype(0);
		pvo.setPk_org(PubEnv.getPk_group());
		pvo.setPk_group(PubEnv.getPk_group());
		pvo.setPk_country("0001Z010000000079UJJ");// �й�
		pvo.setName("��˰��_���˰�ʱ�_Ԥ��");
		pvo.setNdebuctamount(new UFDouble(5000));

		List<TaxTableVO> tablevos = new ArrayList<TaxTableVO>();
		// -- 1 0.00 36000.00 3.0000 0.00
		tablevos.add(amkeTaxTableVO(1, 36000.00, 0.00, 0.00, 3.0000));
		// -- ˰�ʵڶ��� 2 36000.00 144000.00 10.0000 2520.00
		tablevos.add(amkeTaxTableVO(2, 144000.00, 36000.00, 2520.00, 10.0000));
		// 3 144000.00 300000.00 20.0000 16920.00
		tablevos.add(amkeTaxTableVO(3, 300000.00, 144000.00, 16920.00, 20.0000));
		// 4 300000.00 420000.00 25.0000 31920.00
		tablevos.add(amkeTaxTableVO(4, 420000.00, 300000.00, 31920.00, 25.0000));
		// 5 420000.00 660000.00 30.0000 59920.00
		tablevos.add(amkeTaxTableVO(5, 660000.00, 420000.00, 52920.00, 30.0000));
		// 6 660000.00 960000.00 35.0000 85920.00
		tablevos.add(amkeTaxTableVO(6, 960000.00, 660000.00, 85920.00, 35.0000));
		// 7 960000.00 45.0000 181920.00
		tablevos.add(amkeTaxTableVO(7, 0, 960000.00, 181920.00, 45.0000));

		vo.setChildrenVO(tablevos.toArray(new TaxTableVO[0]));

		vo.setTableVO("wa_taxtable", tablevos.toArray(new TaxTableVO[0]));
		return vo;
	}

	/**
	 * ˰�ʱ�˰��
	 * 
	 * @param level
	 *            - ˰��
	 * @param setNmaxamount
	 *            - ���
	 * @param setNminamount
	 *            - ��С
	 * @param setNquickdebuct
	 *            -����۳�
	 * @param setNtaxrate
	 *            -˰��
	 * @return
	 */
	private TaxTableVO amkeTaxTableVO(int level, double setNmaxamount, double setNminamount, double setNquickdebuct,
			double setNtaxrate) {
		TaxTableVO tablevo1 = new TaxTableVO();
		tablevo1.setItaxlevel(level);
		tablevo1.setItaxlevel(level);
		tablevo1.setNdebuctamount(new UFDouble(0));
		tablevo1.setNdebuctrate(new UFDouble(0));
		if (level == 7) {
			tablevo1.setNmaxamount(null);
		} else {
			tablevo1.setNmaxamount(new UFDouble(setNmaxamount));
		}

		tablevo1.setNminamount(new UFDouble(setNminamount));
		// tablevo1.setNdebuctamount(new UFDouble(0));
		tablevo1.setNquickdebuct(new UFDouble(setNquickdebuct));
		tablevo1.setNtaxrate(new UFDouble(setNtaxrate));
		tablevo1.setStatus(VOStatus.NEW);
		return tablevo1;
	}

	/**
	 * ���±��ο�˰����Ϊ ���˰�ʱ��� formula formulastr
	 */
	private void updateF5ToYearTaxFun(WaLoginContext context, WaItemVO[] groupTaxItemvos) {
		String sql = " select * from wa_classitem where pk_wa_class = ? and cyear = ? and cperiod = ? and itemkey = ? ";
		SQLParameter par = new SQLParameter();
		par.addParam(context.getPk_wa_class());
		par.addParam(context.getCyear());
		par.addParam(context.getCperiod());
		par.addParam("f_5");

		try {
			WaClassItemVO classitemvo = getDao().executeQueryVO(sql, par, WaClassItemVO.class);
			Map<String, WaItemVO> taxItemMap = getMapWaItemVO(groupTaxItemvos);
			String formula_f5 = TaxupgradeDef.F5_Formula.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLEAMT_YZ3, "@"),
					taxItemMap.get(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLEAMT_YZ3).getItemkey());
			formula_f5 = formula_f5.replaceAll(this.itemName(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXEDAMT_YZ4, "@"),
					taxItemMap.get(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXEDAMT_YZ4).getItemkey());
			classitemvo.setVformula(formula_f5);
			String formulastr_f5 = TaxupgradeDef.F5_FormulaStr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXABLEAMT_YZ_ZW3, "#"),
					taxItemMap.get(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLEAMT_YZ3).getName());
			formulastr_f5 = formulastr_f5.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXEDAMT_YZ_ZW4, "#"),
					taxItemMap.get(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXEDAMT_YZ4).getName());
			classitemvo.setVformulastr(formulastr_f5);
			String[] fieldNames = new String[] { WaClassItemVO.VFORMULA, WaClassItemVO.VFORMULASTR,
					WaClassItemVO.ISSYSFORMULA };
			classitemvo.setIssysformula(UFBoolean.FALSE);
			getDao().getBaseDao().updateVO(classitemvo, fieldNames);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ���ο�˰��ʽ�޸�Ϊ�����ۼ�
	 * 
	 * @param totalTaxableamtVO
	 *            �ۼ�Ӧ��˰Ԥ��
	 * @param totalTaxedamtVO
	 *            �ۼ��ѿ�˰Ԥ��
	 */
	private void updateGroupF5ToYearTaxFun(WaItemVO totalTaxableamtVO, WaItemVO totalTaxedamtVO) {
		String sql = " select * from wa_item where itemkey = ? ";
		SQLParameter par = new SQLParameter();
		par.addParam("f_5");

		try {
			WaItemVO itemvo = getDao().executeQueryVO(sql, par, WaItemVO.class);
			String formula_f5 = TaxupgradeDef.F5_Formula.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXABLEAMT_YZ3, "@"),
					totalTaxableamtVO.getItemkey());
			formula_f5 = formula_f5.replaceAll(this.itemName(TaxupgradeDef.TAXITEM_CODE_TOTAL_TAXEDAMT_YZ4, "@"),
					totalTaxedamtVO.getItemkey());
			itemvo.setVformula(formula_f5);
			String formulastr_f5 = TaxupgradeDef.F5_FormulaStr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXABLEAMT_YZ_ZW3, "#"),
					totalTaxableamtVO.getName());
			formulastr_f5 = formulastr_f5.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TOTAL_TAXEDAMT_YZ_ZW4, "#"), totalTaxedamtVO.getName());
			formulastr_f5 = formulastr_f5.replaceAll("н�ʷ�����Ŀ", "����н����Ŀ");
			itemvo.setVformulastr(formulastr_f5);
			String[] fieldNames = new String[] { WaItemVO.VFORMULA, WaItemVO.VFORMULASTR };
			getDao().getBaseDao().updateVO(itemvo, fieldNames);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ���ο�˰��ʽ�޸�Ϊ�����ۼ�
	 */
	private void updateGroupF5ToSystemItem() {
		String sql = " select * from wa_item where itemkey = ? ";
		SQLParameter par = new SQLParameter();
		par.addParam("f_5");

		try {
			WaItemVO itemvo = getDao().executeQueryVO(sql, par, WaItemVO.class);
			itemvo.setVformula("taxRate(0,wa_data.f_4,null,null)");
			itemvo.setVformulastr("taxRate(0,[����н����Ŀ.���ο�˰����],null,null)");
			String[] fieldNames = new String[] { WaItemVO.VFORMULA, WaItemVO.VFORMULASTR };
			getDao().getBaseDao().updateVO(itemvo, fieldNames);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ����н����Ŀ���� ר��ӿ۳�
	 * @throws BusinessException
	 */
	private void upgradeSpecialDeduction4WaItem() throws BusinessException {
		StringBuffer sb = new StringBuffer();
		sb.append(" Select * from wa_item where code in  ('tax_deduction_yz',");
		sb.append(" 'taxable_income_yz',");
		sb.append(" 'tax_deduction_parent_yz',");
		sb.append(" 'tax_deduction_child_yz',");
		sb.append(" 'tax_deduction_health_yz',");
		sb.append(" 'tax_deduction_education_yz',");
		sb.append(" 'tax_deduction_hoursezu_yz',");
		sb.append(" 'tax_deduction_house_yz',");
		sb.append(" 'tax_deduction_totalable_yz',");
		sb.append(" 'tax_deduction_totaled_yz',");
		sb.append(" 'total_taxable_income_yz',");
		sb.append(" 'total_taxableamt_yz',");
		sb.append(" 'total_taxedamt_yz',");
		sb.append(" 'tax_rate_yz',");
		sb.append(" 'quick_deduction_yz',");
		sb.append(" 'tax_basic_deduction_yz') and pk_group = '" + PubEnv.getPk_group() + "' ");
		WaItemVO[] taxItemvos = getDao().executeQueryVOs(sb.toString(), WaItemVO.class);
		if (taxItemvos != null && taxItemvos.length == 16) {
			// �Ѿ�����������������
		} else if (taxItemvos != null && taxItemvos.length == 14) {
			ItemServiceImpl itemImpl = new ItemServiceImpl();
			Map<String, WaItemVO> mapTaxItem = getMapWaItemVO(taxItemvos);
			// �Ѿ���������һ�棬��Ҫ����ר��
			// ר������ 1����������ר��н����Ŀ
			/** ��˰��_�ۼ�ר��Ӧ�� */
			WaItemVO waitemVO16 = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16,
					TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ16, 6, 2);
			waitemVO16.setCategory_id(taxItemvos[0].getCategory_id());

			// �ֹ�����
			waitemVO16.setIfromflag(0);
			String formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ16;
			formula = formula.replaceAll("@deu10@",
					mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ10).getItemkey());
			formula = formula.replaceAll("@deu11@",
					mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ11).getItemkey());
			formula = formula.replaceAll("@deu12@",
					mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ12).getItemkey());
			formula = formula.replaceAll("@deu13@",
					mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ13).getItemkey());
			formula = formula.replaceAll("@deu14@",
					mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ14).getItemkey());
			formula = formula.replaceAll("@deu15@",
					mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ15).getItemkey());
			waitemVO16.setVformula(formula);
			String formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ16.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ10, "#"),
					mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ10).getName());
			formulastr = formulastr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ11, "#"),
					mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ11).getName());
			formulastr = formulastr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ12, "#"),
					mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ12).getName());
			formulastr = formulastr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ13, "#"),
					mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ13).getName());
			formulastr = formulastr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ14, "#"),
					mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ14).getName());
			formulastr = formulastr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ15, "#"),
					mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ15).getName());
			waitemVO16.setVformulastr(formulastr);

			waitemVO16 = itemImpl.insertWaItemVO(waitemVO16);

			// /**�ۼ�ר���ѿ�*/
			WaItemVO waitemVO = this.makeWaItemVO(null, TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ17,
					TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ17, 6, 2);
			waitemVO.setCategory_id(taxItemvos[0].getCategory_id());
			waitemVO.setIfromflag(6);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ17;
			formula = formula.replaceAll("@tax_deduction_totalable_yz@", waitemVO16.getItemkey());
			waitemVO.setVformula(formula);

			formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ17.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ16, "#"), waitemVO16.getName());
			waitemVO.setVformulastr(formulastr);

			WaItemVO itemVO17 = itemImpl.insertWaItemVO(waitemVO);
			
			//����ԭ��ר��ӿ۳��ϼƹ�ʽ Ϊ����ֻ��
			WaItemVO waitemVO9 = mapTaxItem.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ9);
			waitemVO9.setIfromflag(0);
			formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ9_1;
			formula = formula.replaceAll("@deu16@", waitemVO16.getItemkey());
			formula = formula.replaceAll("@deu17@", itemVO17.getItemkey());
			waitemVO9.setVformula(formula);
			formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9_1.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ16, "#"), waitemVO16.getName());
			formulastr = formulastr.replaceAll(
					this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ17, "#"), itemVO17.getName());
			waitemVO9.setVformulastr(formulastr);
			
			String[] updateFields = new String[]{WaItemVO.IFROMFLAG,WaItemVO.VFORMULA,WaItemVO.VFORMULASTR};
			getDao().getBaseDao().updateVO(waitemVO9, updateFields);
			
			//����н�ʷ�����Ŀ
			upgradeSpecialDeduction4WaClassItem();

			
		}
	}

	/**
	 * ר��ӿ۳� н�ʷ�����Ŀ����
	 * @throws BusinessException 
	 */
	public void upgradeSpecialDeduction4WaClassItem() throws BusinessException{
		//��ѯ��������н�ʷ�����Ŀ
		WaItemVO[] groupTaxItemvos = NCLocator.getInstance().lookup(ITaxupgrade_toolQueryService.class)
				.queryTaxUpgradeItems(PubEnv.getPk_group());
		
		IClassItemManageService classitemService = NCLocator.getInstance().lookup(IClassItemManageService.class);
		String sql = ("select * from wa_classitem where itemkey = (select itemkey from wa_item where code = 'tax_deduction_yz' and pk_group = '"+PubEnv.getPk_group()+"') and pk_group = '"+PubEnv.getPk_group()+"'" );
		WaClassItemVO[] classitemvos = this.getDao().executeQueryVOs(sql, WaClassItemVO.class);
		if (classitemvos != null && classitemvos.length > 0) {
			Map<String, WaItemVO> mapItemVO = this.getMapWaItemVO(groupTaxItemvos);
			WaLoginContext context = new WaLoginContext();
			String[] pk_wa_items = null;
			if (groupTaxItemvos == null || groupTaxItemvos.length == 0) {
				throw new BusinessException("���ȳ�ʼ��˰����Ŀ!");
			}
			List<String> lisItemKeys = new ArrayList<String>();
			lisItemKeys.add(mapItemVO.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16).getPk_wa_item());
			lisItemKeys.add(mapItemVO.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ17).getPk_wa_item());
			pk_wa_items = lisItemKeys.toArray(new String[0]);

			for (WaClassItemVO generalvo : classitemvos) {
				String pk_org = generalvo.getAttributeValue("pk_org") + "";
				String cyear = generalvo.getAttributeValue("cyear") + "";
				String cperiod = generalvo.getAttributeValue("cperiod") + "";
				String pk_wa_class = generalvo.getAttributeValue("pk_wa_class") + "";
				context = new WaLoginContext();
				context.setPk_group(PubEnv.getPk_group());
				context.setPk_org(pk_org);
				context.setCyear(cyear);
				context.setCperiod(cperiod);
				context.getWaLoginVO().setPk_wa_class(pk_wa_class);
				context.getWaLoginVO().setCyear(cyear);
				context.getWaLoginVO().setCperiod(cperiod);
				PeriodStateVO stateVO = new PeriodStateVO();
				stateVO.setCyear(cyear);
				stateVO.setCperiod(cperiod);
				context.getWaLoginVO().setPeriodVO(stateVO);
				context.setNodeType(NODE_TYPE.ORG_NODE);
				classitemService.batchAddClassItemVOs(context, pk_wa_items);
				
				//����ר��ӿ۳��ϼƹ�ʽ
				String formula = TaxupgradeDef.TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ9_1;
				formula = formula.replaceAll("@deu16@", mapItemVO.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16).getItemkey());
				formula = formula.replaceAll("@deu17@", mapItemVO.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ17).getItemkey());
				generalvo.setVformula(formula);
				String formulastr = TaxupgradeDef.TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9_2.replaceAll(
						this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ16, "#"), mapItemVO.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16).getName());
				formulastr = formulastr.replaceAll(
						this.itemName(TaxupgradeDef.TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ17, "#"), mapItemVO.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ17).getName());
				generalvo.setVformulastr(formulastr);
				String[] updateFields = new String[]{WaItemVO.VFORMULA,WaItemVO.VFORMULASTR};
				getDao().getBaseDao().updateVO(generalvo, updateFields);
			}
			
			
			//����н������ �����ۼ�ר��Ӧ�� Ϊ ר��ӿ۳��ϼ�
			String key1 = mapItemVO.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ9).getItemkey();
			String key2 = mapItemVO.get(TaxupgradeDef.TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16).getItemkey();
			String updateValue = " update wa_data set " + key2 +" = " + key1 +" where pk_group = '"+PubEnv.getPk_group()+"'";
			this.getDao().getBaseDao().executeUpdate(updateValue);
		}
	}
	private Map<String, WaItemVO> getMapWaItemVO(WaItemVO[] groupTaxItemvos) {

		if (groupTaxItemvos != null && groupTaxItemvos.length > 0) {
			taxitemmap = new HashMap<String, WaItemVO>();
			for (WaItemVO itemvo : groupTaxItemvos) {
				taxitemmap.put(itemvo.getCode(), itemvo);
			}
		}
		return taxitemmap;
	}

	public BaseDAOManager getDao() {
		if (dao == null) {
			dao = new BaseDAOManager();
		}
		return dao;
	}

}
