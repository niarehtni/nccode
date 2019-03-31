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

	// ��ǰ������PK
	private final String pk_periodscheme;

	// ����ԭ�е�н���ڼ�
	private final PeriodVO[] oldItems;

	// ��ʼн���ڼ�
	String curPeriod = null;

	Container parent = null;

	public NaturePeriodCreate(Container parent, String pk_periodscheme, String curPeriod, PeriodVO[] oldItems) {
		this.parent = parent;
		this.pk_periodscheme = pk_periodscheme;
		this.oldItems = oldItems;
		this.curPeriod = curPeriod;
	}

	/**
	 * ����Ȼ�����Զ�����
	 *
	 * @author liangxr on 2009-11-26
	 * @param settingMap
	 * @throws Exception
	 */
	public PeriodVO[] autoGenerateByNatureMonth(Map<String, String> settingMap) throws Exception {

		// ��ֹ����
		UFLiteralDate startDate = null;
		UFLiteralDate endDate = null;

		// ԭ���ڼ�����һ���ڼ�
		PeriodVO lastPeriodVO = null;
		if (oldItems != null) {
			lastPeriodVO = oldItems[oldItems.length - 1];
		}
		if (!isSettingByNatrual(lastPeriodVO)) {
			throw new BusinessException(ResHelper.getString("60130period","060130period0098")/*@res "�÷�������ǰ��н���ڼ䲻�ǰ�����Ȼ�������õģ�����������Ȼ�������ú����ڼ䣡	"*/);
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
						ResHelper.getString("60130period","060130period0122")/*@res "�Ѿ�����Ĭ��н���ڼ䣬ʹ�ø�н���ڼ�ΪĬ���ڼ����ú������Ҫ������������"*/) == nc.ui.pub.beans.UIDialog.ID_YES)) {
			{
				// ����н���ڼ�VO����
				return createPeriodByNature(startDate, endDate);
			}
		}
		return null;
	}

	private PeriodVO[] createPeriodByNature(UFLiteralDate startDate, UFLiteralDate endDate) throws Exception {

		// 4:������Ȼ���� ,����н���ڼ�
		ArrayList<PeriodVO> list = new ArrayList<PeriodVO>();

		if (oldItems != null) {
			// ���ָ����ʼ�ڼ������Ѵ��������ڼ䣬ɾ�����е�н���ڼ䣬��������
			if (!(endDate.before(oldItems[0].getCstartdate())||startDate.after(oldItems[oldItems.length-1].getCenddate()))) {
				// �жϸ�н���ڼ��Ƿ�����
				IWaPeriodQuery periodService = NCLocator.getInstance().lookup(IWaPeriodQuery.class);
				String pks = FormatVO.formatArrayToString(oldItems, PeriodVO.PK_WA_PERIOD);
				if (periodService.isPeriodRefed(pks))
					throw new BusinessException(ResHelper.getString("60130period","060130period0096")/*@res "��н���ڼ��Ѿ������ã������������ɣ�"*/);

				 for (int i = 0; i < oldItems.length; i++) {
					if(!(endDate.before(oldItems[i].getCstartdate())||startDate.after(oldItems[i].getCenddate()))){
						oldItems[i].setStatus(VOStatus.DELETED);
						list.add(oldItems[i]);
					}

				}
			}
		}
		// ����ʣ���н���ڼ�
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

		// ��������
		PeriodVO[] items = new PeriodVO[list.size()];
		System.arraycopy(list.toArray(), 0, items, 0, list.size());
		return items;
	}

	/**
	 * �������һ��н���ڼ�,�˲��Ƿ���԰�����Ȼ��������
	 *
	 * @param lastPeriodVO
	 * @return
	 * @see
	 */
	private boolean isSettingByNatrual(PeriodVO lastPeriodVO) {

		// �ж�"��ǰ��н���ڼ䲻�ǰ�����Ȼ�������õ�"
		if (lastPeriodVO == null) {
			return true;
		}
		UFLiteralDate endDate = lastPeriodVO.getCenddate();
		// ȷ��endDate�Ƿ���µ����һ��
		if (new Integer(lastPeriodVO.getCyear()).intValue() == endDate.getYear()
				&& new Integer(lastPeriodVO.getCperiod()).intValue() == endDate.getMonth()
				&& isTheLastDayOfMon(endDate)) {
			return true;
		}
		return false;
	}

	/**
	 * �ж�����<code>date</code>�Ƿ��Ǹ��µ����һ��
	 *
	 * @param date
	 *            �û�ָ��������
	 * @return boolean �������<code>date</code>�Ǹ��µ����һ���򷵻�true ���򷵻� false
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
	 * ȡ��ָ��������һ���µ�����
	 *
	 * @param date
	 *            ָ��������
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
	 * ȡ��ָ������ <code>date</code>�����µ�����
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
	 * ȡ��ָ������ <code>date</code>�����µ�ĩ��
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