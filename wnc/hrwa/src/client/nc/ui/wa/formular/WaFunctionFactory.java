package nc.ui.wa.formular;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import nc.bs.logging.Logger;
import nc.ui.hr.formula.HRFormulaItem;
import nc.ui.hr.formula.itf.IFunctionFactory;
import nc.vo.hr.formula.FunctionKey;
import nc.vo.hr.func.FunctionVO;
import nc.vo.pub.formulaedit.FormulaItem;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.formula.HrWaXmlReader;
import nc.vo.wa.formula.WaFormulaXmlHelper;

/**
 * 
 * @author: xuanlt
 * @date: 2010-4-1 上午11:10:33
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("restriction")
public class WaFunctionFactory implements IFunctionFactory {

	LoginContext context = null;

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

	/**
	 * @author xuanlt on 2010-4-1
	 * @see nc.ui.wa.formular.FunctionFactory#getAllFunctions()
	 */
	@Override
	public List<FormulaItem> getAllFunctions() {

		List<FormulaItem> items = new ArrayList<FormulaItem>();

		FunctionVO fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.WAGEYEAR);
		HRFormulaItem wageYear = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(), fvo.getInputSig(),
				fvo.getHintMsg(), fvo.getReturntype());
		items.add(wageYear);

		fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.WAGEMONTH);
		HRFormulaItem wageMonth = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(), fvo.getInputSig(),
				fvo.getHintMsg(), fvo.getReturntype());
		items.add(wageMonth);

		fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.WAPERIODSTARTDATE);
		HRFormulaItem waPeriodStartDate = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
				fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());

		items.add(waPeriodStartDate);

		fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.WAPERIODENDDATE);
		HRFormulaItem waPeriodEndDate = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
				fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());
		items.add(waPeriodEndDate);

		fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.PREPERIODSTARTDATE);
		HRFormulaItem prePeriodStartDate = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
				fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());

		items.add(prePeriodStartDate);

		fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.PREPERIODENDDATE);
		HRFormulaItem prePeriodEndDate = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
				fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());
		items.add(prePeriodEndDate);

		fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.PREADJUSTDATE);
		HRFormulaItem preAdjustDate = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(), fvo.getInputSig(),
				fvo.getHintMsg(), fvo.getReturntype());
		String classname = fvo.getParapanel();

		Component editor = null;
		try {
			editor = (Component) Class.forName(classname).newInstance();
		} catch (InstantiationException e) {
			Logger.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Logger.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
		}

		preAdjustDate.setEditor(editor);
		items.add(preAdjustDate);

		fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.REGISTERDATE);
		HRFormulaItem registerDate = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(), fvo.getInputSig(),
				fvo.getHintMsg(), fvo.getReturntype());

		items.add(registerDate);

		fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.DISMISSIONDATE);
		HRFormulaItem dismissionDate = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(), fvo.getInputSig(),
				fvo.getHintMsg(), fvo.getReturntype());
		items.add(dismissionDate);

		fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.WAPERIODDAYS);
		HRFormulaItem waPeriodDays = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(), fvo.getInputSig(),
				fvo.getHintMsg(), fvo.getReturntype());
		items.add(waPeriodDays);

		fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.WAPERIODWORKDAYS);
		HRFormulaItem waPeriodWorkDays = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
				fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());
		items.add(waPeriodWorkDays);

		try {
			// v61 再看看如何实现.如果么有安装时间管理模块，禁止使用这连个函数
			// if (PubEnv.isModuleStarted(null, PubEnv.MODULE_HRTA)) {
			fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.WAGEDAYS);
			HRFormulaItem wagesDays = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(), fvo.getInputSig(),
					fvo.getHintMsg(), fvo.getReturntype());

			// 为taxRate添加编辑器
			editor = null;
			try {
				editor = (Component) Class.forName(fvo.getParapanel()).newInstance();
			} catch (InstantiationException e) {
				Logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				Logger.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				Logger.error(e.getMessage(), e);
			}

			wagesDays.setEditor(editor);
			items.add(wagesDays);

			// 區間留停天數
			fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.INTERVALDURATION);
			HRFormulaItem intervalDuration = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
					fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());

			items.add(intervalDuration);

			fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.OVERTIMEPAY);
			HRFormulaItem overtimePays = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
					fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());
			items.add(overtimePays);

			fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.TAXABLEOVERTIMEPAY);
			HRFormulaItem taxableOvertimePays = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
					fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());
			items.add(taxableOvertimePays);

			fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.FIRSTMONWORKDAYS);
			HRFormulaItem firstMonWorkDays = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
					fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());
			items.add(firstMonWorkDays);

			fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.LASTMONWORKDAYS);
			HRFormulaItem lastMonWorkDays = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
					fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());
			items.add(lastMonWorkDays);

			// 添加請假扣款函數 add by ward 20180511 begin
			fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.STATISTICLEAVECHARGE);
			HRFormulaItem statisticLeaveCharge = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
					fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());
			items.add(statisticLeaveCharge);
			// 添加請假扣款函數 add by ward 20180511 end

			// }

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}

		fvo = HrWaXmlReader.getInstance().getTaxrateDefaultFunctionVO();
		if (null != fvo) {
			HRFormulaItem taxRate = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(), fvo.getInputSig(),
					fvo.getHintMsg(), fvo.getReturntype());

			// 为taxRate添加编辑器
			editor = null;
			try {
				editor = (Component) Class.forName(fvo.getParapanel()).newInstance();
			} catch (InstantiationException e) {
				Logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				Logger.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				Logger.error(e.getMessage(), e);
			}
			taxRate.setEditor(editor);
			items.add(taxRate);
		}

		// #21266 增加考勤類薪資函數
		// 按日合计加班费 OVERTIMEFEE yejk 18-09-07
		fvo = WaFormulaXmlHelper.getFunctionVO("OVERTIMEFEE");
		HRFormulaItem overTimeFeeItem = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
				fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());

		editor = null;
		try {
			editor = (Component) Class.forName(fvo.getParapanel()).newInstance();
		} catch (InstantiationException e) {
			Logger.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Logger.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
		}

		overTimeFeeItem.setEditor(editor);
		items.add(overTimeFeeItem);
		// 按日合计考勤扣款项 LEAVEFEE yejk 18-09-11
		fvo = WaFormulaXmlHelper.getFunctionVO("LEAVEFEE");
		HRFormulaItem leaveFeeItem = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(), fvo.getInputSig(),
				fvo.getHintMsg(), fvo.getReturntype());

		editor = null;
		try {
			editor = (Component) Class.forName(fvo.getParapanel()).newInstance();
		} catch (InstantiationException e) {
			Logger.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Logger.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
		}

		leaveFeeItem.setEditor(editor);
		items.add(leaveFeeItem);

		// 添加日薪函数 add by ward 2018年9月18日14:48:41 begin
		fvo = WaFormulaXmlHelper.getFunctionVO("DAYSALARY");
		if (null != fvo) {
			HRFormulaItem daySalary = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(), fvo.getInputSig(),
					fvo.getHintMsg(), fvo.getReturntype());
			editor = null;
			try {
				editor = (Component) Class.forName(fvo.getParapanel()).newInstance();
			} catch (InstantiationException e) {
				Logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				Logger.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				Logger.error(e.getMessage(), e);
			}
			daySalary.setEditor(editor);
			items.add(daySalary);
		}
		// 添加日薪函数 add by ward 2018年9月18日14:48:45 end
		// 添加 添加年假结薪时长 add by he 2018年10月11日14:48:45 begin

		fvo = WaFormulaXmlHelper.getFunctionVO("ALPAIDHOURS");
		if (null != fvo) {
			HRFormulaItem alPaidHours = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(),
					fvo.getInputSig(), fvo.getHintMsg(), fvo.getReturntype());
			editor = null;
			try {
				editor = (Component) Class.forName(fvo.getParapanel()).newInstance();
			} catch (InstantiationException e) {
				Logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				Logger.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				Logger.error(e.getMessage(), e);
			}
			alPaidHours.setEditor(editor);
			items.add(alPaidHours);
		}
		// 添加添加年假结薪时长 add by he 2018年10月11日14:48:45 end
		// 法院强制扣款 by he
		fvo = WaFormulaXmlHelper.getFunctionVO(FunctionKey.COURTDEDUCT);
		HRFormulaItem courtdeduct = new HRFormulaItem(fvo.getDisplayName(), fvo.getDefaultName(), fvo.getInputSig(),
				fvo.getHintMsg(), fvo.getReturntype());

		items.add(courtdeduct);
		return items;
	}

}
