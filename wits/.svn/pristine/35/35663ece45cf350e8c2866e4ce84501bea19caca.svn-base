package nc.ui.wa.period.model;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IWaPeriodQuery;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.wa.pub.WaPeriodUtil;
import nc.vo.hr.pub.FormatVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.period.WaPeriodConstant;

public class NaturePeriodCreate {

	private final String firstDayOfYear = "-01-01";
	private final String interDate = "-";
	private final String lastDayOfYear = "-12-31";
	private final String firstDayOfMonth = "-01";

	// 当前方案的PK
	private final String pk_periodscheme;

	// 方案原有的薪资期间
	private final PeriodVO[] oldItems;

	// 起始薪资期间
	String curPeriod = null;

	Container parent = null;

	public NaturePeriodCreate(Container parent, String pk_periodscheme, String curPeriod, PeriodVO[] oldItems) {
		this.parent = parent;
		this.pk_periodscheme = pk_periodscheme;
		this.oldItems = oldItems;
		this.curPeriod = curPeriod;
	}

	/**
	 * 按自然年月自动生成
	 *
	 * @author liangxr on 2009-11-26
	 * @param settingMap
	 * @throws Exception
	 */
	public PeriodVO[] autoGenerateByNatureMonth(Map<String, String> settingMap) throws Exception {

		// 起止日期
		UFLiteralDate startDate = null;
		UFLiteralDate endDate = null;

		// 原有期间的最后一个期间
		PeriodVO lastPeriodVO = null;
		if (oldItems != null) {
			lastPeriodVO = oldItems[oldItems.length - 1];
		}
		if (!isSettingByNatrual(lastPeriodVO)) {
			throw new BusinessException(ResHelper.getString("60130period","060130period0098")/*@res "该方案的以前的薪资期间不是按照自然年月设置的，不允许用自然年月设置后续期间！	"*/);
		}
		if (StringUtil.isEmpty(settingMap.get(WaPeriodConstant.STARTYEAR))
				|| StringUtil.isEmpty(settingMap.get(WaPeriodConstant.STARTPERIOD))) {
			UFDate currentDate = PubEnv.getLoginDate();
			if (oldItems == null || oldItems.length == 0) {
				startDate = new UFLiteralDate(currentDate.getYear() + firstDayOfYear);
				endDate = new UFLiteralDate(startDate.getYear() + lastDayOfYear);
			} else {
				startDate = getFirstDayOfNextMon(oldItems[oldItems.length - 1].getCenddate());
				endDate = new UFLiteralDate(startDate.getYear() + lastDayOfYear);

			}
		} else {

			startDate = new UFLiteralDate(curPeriod.substring(0, 4) + interDate + curPeriod.substring(4, 6)
					+ firstDayOfMonth);
			endDate = new UFLiteralDate(curPeriod.substring(0, 4) + lastDayOfYear);

		}
		if (lastPeriodVO == null
				|| (MessageDialog.showYesNoDlg(parent, null,
						ResHelper.getString("60130period","060130period0122")/*@res "已经存在默认薪资期间，使用该薪资期间为默认期间设置后可能需要调整，继续吗？"*/) == nc.ui.pub.beans.UIDialog.ID_YES)) {
			{
				// 生成薪资期间VO数组
				return createPeriodByNature(startDate, endDate);
			}
		}
		return null;
	}

	private PeriodVO[] createPeriodByNature(UFLiteralDate startDate, UFLiteralDate endDate) throws Exception {

		// 4:根据自然年月 ,设置薪资期间
		ArrayList<PeriodVO> list = new ArrayList<PeriodVO>();

		if (oldItems != null) {
			// 如果指定起始期间早于已存在最早期间，删除已有的薪资期间，重新生成
			if (!(endDate.before(oldItems[0].getCstartdate())||startDate.after(oldItems[oldItems.length-1].getCenddate()))) {
				// 判断该薪资期间是否被引用
				IWaPeriodQuery periodService = NCLocator.getInstance().lookup(IWaPeriodQuery.class);
				String pks = FormatVO.formatArrayToString(oldItems, PeriodVO.PK_WA_PERIOD);
				if (periodService.isPeriodRefed(pks))
					throw new BusinessException(ResHelper.getString("60130period","060130period0096")/*@res "有薪资期间已经被引用，不能重新生成！"*/);

				 for (int i = 0; i < oldItems.length; i++) {
					if(!(endDate.before(oldItems[i].getCstartdate())||startDate.after(oldItems[i].getCenddate()))){
						oldItems[i].setStatus(VOStatus.DELETED);
						list.add(oldItems[i]);
					}

				}
			}
		}
		// 生成剩余的薪资期间
		while (startDate.before(endDate)) {
			PeriodVO vo = new PeriodVO();
			vo.setStatus(VOStatus.NEW);
			vo.setPk_periodscheme(pk_periodscheme);
			vo.setCyear(String.valueOf(startDate.getYear()));
			vo.setCaccyear(String.valueOf(startDate.getYear()));
			vo.setTaxyear(String.valueOf(startDate.getYear()));
			vo.setCperiod(WaPeriodUtil.getIntegerWithLength(startDate.getMonth(), 2));
			vo.setTaxperiod(WaPeriodUtil.getIntegerWithLength(startDate.getMonth(), 2));
			vo.setCaccperiod(WaPeriodUtil.getIntegerWithLength(startDate.getMonth(), 2));
			vo.setCstartdate(getTheFirstDate(startDate));
			vo.setCenddate(getTheLastDate(startDate));
			list.add(vo);
			startDate = getFirstDayOfNextMon(startDate);
		}

		// 保存数据
		PeriodVO[] items = new PeriodVO[list.size()];
		System.arraycopy(list.toArray(), 0, items, 0, list.size());
		return items;
	}

	/**
	 * 根据最后一个薪资期间,核查是否可以按照自然年月设置
	 *
	 * @param lastPeriodVO
	 * @return
	 * @see
	 */
	private boolean isSettingByNatrual(PeriodVO lastPeriodVO) {

		// 判断"以前的薪资期间不是按照自然年月设置的"
		if (lastPeriodVO == null) {
			return true;
		}
		UFLiteralDate endDate = lastPeriodVO.getCenddate();
		// 确认endDate是否该月的最后一天
		if (new Integer(lastPeriodVO.getCyear()).intValue() == endDate.getYear()
				&& new Integer(lastPeriodVO.getCperiod()).intValue() == endDate.getMonth()
				&& isTheLastDayOfMon(endDate)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断日期<code>date</code>是否是该月的最后一天
	 *
	 * @param date
	 *            用户指定的日期
	 * @return boolean 如果日期<code>date</code>是该月的最后一天则返回true 否则返回 false
	 */
	private boolean isTheLastDayOfMon(UFLiteralDate date) {
		assert (date != null) : "isTheLastDayOfMon() generated null Exception";
		String nextMon = String.valueOf(date.getMonth() + 1);
		if (nextMon.length() == 1) {
			nextMon = "0" + nextMon;
		}
		UFLiteralDate lastdate = null;
		if (Integer.parseInt(nextMon) > 12) {
			lastdate = new UFLiteralDate((date.getYear() + 1) + firstDayOfYear).getDateBefore(1);
		} else {
			lastdate = new UFLiteralDate(date.getYear() + interDate + nextMon + firstDayOfMonth).getDateBefore(1);
		}
		if (lastdate.isSameDate(date)) {
			return true;
		}
		return false;
	}

	/**
	 * 取得指定日期下一个月的首日
	 *
	 * @param date
	 *            指定的日期
	 * @return
	 * @see
	 */
	private UFLiteralDate getFirstDayOfNextMon(UFLiteralDate date) {
		String nextMon = String.valueOf(date.getMonth() + 1);
		if (nextMon.length() == 1) {
			nextMon = "0" + nextMon;
		}
		UFLiteralDate lastdate = null;
		if (Integer.parseInt(nextMon) > 12) {
			lastdate = new UFLiteralDate((date.getYear() + 1) + firstDayOfYear);
		} else {
			lastdate = new UFLiteralDate(date.getYear() + interDate + nextMon + firstDayOfMonth);
		}
		return lastdate;
	}

	/**
	 * 取得指定日期 <code>date</code>所在月的首日
	 *
	 * @param date
	 * @return
	 * @see
	 */
	UFLiteralDate getTheFirstDate(UFLiteralDate date) {
		String curMon = date.getStrMonth();
		UFLiteralDate lastdate = new UFLiteralDate(date.getYear() + interDate + curMon + firstDayOfMonth);
		return lastdate;
	}

	/**
	 * 取得指定日期 <code>date</code>所在月的末日
	 *
	 * @param date
	 * @return
	 * @see
	 */
	UFLiteralDate getTheLastDate(UFLiteralDate date) {
		String nextMon = String.valueOf(date.getMonth() + 1);
		if (nextMon.length() == 1) {
			nextMon = "0" + nextMon;
		}
		UFLiteralDate lastdate = null;
		if (Integer.parseInt(nextMon) > 12) {
			lastdate = new UFLiteralDate((date.getYear() + 1) + firstDayOfYear).getDateBefore(1);
		} else {
			lastdate = new UFLiteralDate(date.getYear() + interDate + nextMon + firstDayOfMonth).getDateBefore(1);
		}
		return lastdate;
	}

}