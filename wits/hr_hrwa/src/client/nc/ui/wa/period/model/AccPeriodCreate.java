package nc.ui.wa.period.model;

import java.awt.Container;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IWaPeriodQuery;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.wa.pub.WaPeriodUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.period.AccPeriodVO;
import nc.vo.wa.period.PeriodVO;

import org.apache.commons.lang.ArrayUtils;

public class AccPeriodCreate {

	// 当前方案的PK
	private final String pk_periodscheme;

	// 方案原有的薪资期间
	private final PeriodVO[] oldItems;

	// 起始薪资期间
	String curPeriod = null;

	Container parent = null;

	public AccPeriodCreate(Container parent, String pk_periodscheme, String curPeriod, PeriodVO[] oldItems) {
		this.parent = parent;
		this.pk_periodscheme = pk_periodscheme;
		this.curPeriod = curPeriod;
		this.oldItems = oldItems;
	}

	/**
	 * 按会计期间自动生成功能
	 *
	 * @param settingMap
	 *            自动生成设置Map
	 * @param currPeriodVO
	 *            当前期间VO
	 * @param allAccPeriods
	 *            所有会计期间
	 * @throws Exception
	 * @throws Exception
	 */
	public PeriodVO[] autoGenerateByAccPeriod(Map<String, String> settingMap, AccPeriodVO[] allAccPeriods)
			throws BusinessException {
		if (ArrayUtils.isEmpty(allAccPeriods)) {
			throw new BusinessException(ResHelper.getString("60130period","060130period0093")/*@res "没有找到符合条件的基准期间！"*/);
		}

		// 原有期间的最后一个期间
		PeriodVO lastPeriodVO = null;
		String lastPeriod = null;
		if (oldItems != null && oldItems.length > 0) {
			// 如果用户输入的起始薪资期间是第一个薪资期间，按照当前时间所在期间确定薪资期间的起始日,否则按最后一个期间的时间定
			lastPeriodVO = oldItems[oldItems.length - 1];
			lastPeriod = lastPeriodVO.getCaccyear() + lastPeriodVO.getCaccperiod();
		}

		if (lastPeriod == null) {
			if (curPeriod == null) {
				UFDate currentDate = PubEnv.getLoginDate();
				curPeriod = "" + currentDate.getYear()
						+ WaPeriodUtil.getIntegerWithLength(currentDate.getMonth(), 2);
			}
		}

		if (!isSettingByAccp(lastPeriodVO)) {
			throw new BusinessException(ResHelper.getString("60130period","060130period0094")/*@res "该方案的以前的薪资期间不是按照默认会计期间设置的，不允许用默认会计期间设置后续期间！"*/);
		}
		if (lastPeriodVO == null
				|| (MessageDialog.showYesNoDlg(parent, null,
						ResHelper.getString("60130period","060130period0124")/*@res "已经存在默认薪资期间，使用该薪资期间为默认期间设置后可能需要调整，继续吗？"*/) == nc.ui.pub.beans.UIDialog.ID_YES)) {
			{
				// 会计期间
				AccPeriodVO[] accPeriods = getAccPeriods(lastPeriod, allAccPeriods);
				// 生成薪资期间VO数组
				return createPeriodByAccPeriods(accPeriods, lastPeriodVO);
			}
		}
		return null;
	}

	/**
	 * 根据原有的薪资期间和起始期间，获取相应的会计期间
	 *
	 * @author liangxr on 2009-11-26
	 * @param oldItems
	 * @param lastPeriod
	 * @return
	 * @throws BusinessException
	 */
	private AccPeriodVO[] getAccPeriods(String lastPeriod, AccPeriodVO[] accPeriods) throws BusinessException {
		IWaPeriodQuery periodService = NCLocator.getInstance().lookup(IWaPeriodQuery.class);
		return periodService.getAccPeriods(lastPeriod, accPeriods, oldItems, curPeriod);
	}

	/**
	 * 根据最后一个薪资期间,判断以前的薪资期间是否按照默认的会计期间设置的 如果是,则按照默认会计期间进行设置,否则进行提示
	 *
	 * @param lastPeriodVO
	 * @return
	 * @see
	 */
	private boolean isSettingByAccp(PeriodVO lastPeriodVO) {

		String lastAcc = null;
		String lastP = null;
		if (lastPeriodVO == null) {
			return true;
		}
		lastAcc = lastPeriodVO.getCaccyear() + lastPeriodVO.getCaccperiod();
		lastP = lastPeriodVO.getCyear() + lastPeriodVO.getCperiod();
		if (lastAcc.equals(lastP)) {
			// "最后的会计期间"与"最后的薪资期间"进行比较
			return true;
		}
		return false;
	}

	/**
	 * 按照会计期间生成薪资期间VO数组
	 *
	 * @author liangxr on 2009-11-26
	 * @param oldItems
	 * @param accPeriods
	 * @param lastPeriodVO
	 * @param headVO
	 * @return
	 * @throws BusinessException
	 */
	private PeriodVO[] createPeriodByAccPeriods(AccPeriodVO[] accPeriods, PeriodVO lastPeriodVO)
			throws BusinessException {
		PeriodVO[] items = null;
		int length = oldItems == null ? 0 : oldItems.length;
		items = new PeriodVO[length + accPeriods.length];
		if (oldItems != null) {
			for (int i = 0; i < oldItems.length; i++) {
				items[i] = oldItems[i];
			}
		}
		for (int i = 0; i < accPeriods.length; i++) {
			// 过滤第一个重复的会计期间
			PeriodVO vo = new PeriodVO();
			String accYear = accPeriods[i].getPeriodyear();
			String accMonth = accPeriods[i].getAccperiodmth();
			UFDate startDate = accPeriods[i].getBegindate();
			UFDate endDate = accPeriods[i].getEnddate();

//			if (i == 0 && lastPeriodVO != null) {
//				// 第一个会计期间需要按照该方案上一个期间结束日期调整
//				UFDate lastEndDate = lastPeriodVO.getCenddate();
//				if (lastEndDate.before(startDate)) {
//					startDate = lastEndDate.getDateAfter(1);
//				}
//			}
			vo.setPk_periodscheme(pk_periodscheme);
			vo.setCyear(accYear.trim());
			vo.setCaccyear(accYear.trim());
			vo.setCperiod(accMonth);
			vo.setTaxyear(accYear.trim());
			vo.setTaxperiod(accMonth);
			vo.setCaccperiod(accMonth);
			vo.setCstartdate(UFLiteralDate.fromPersisted(startDate.toStdString()));
			vo.setCenddate(UFLiteralDate.fromPersisted(endDate.toStdString()));
			vo.setStatus(VOStatus.NEW);

			items[length + i] = vo;
		}
		if (items.length == 0) {
			// 自动生成期间为空
			throw new BusinessException(ResHelper.getString("60130period","060130period0097")/*@res "自动生成期间为空，请核查对应的会计期间！"*/);
		} else {
			// 返回生成VO子表数据
			return items;
		}
	}

}