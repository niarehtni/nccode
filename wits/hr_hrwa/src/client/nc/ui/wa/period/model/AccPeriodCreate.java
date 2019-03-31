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

	// ��ǰ������PK
	private final String pk_periodscheme;

	// ����ԭ�е�н���ڼ�
	private final PeriodVO[] oldItems;

	// ��ʼн���ڼ�
	String curPeriod = null;

	Container parent = null;

	public AccPeriodCreate(Container parent, String pk_periodscheme, String curPeriod, PeriodVO[] oldItems) {
		this.parent = parent;
		this.pk_periodscheme = pk_periodscheme;
		this.curPeriod = curPeriod;
		this.oldItems = oldItems;
	}

	/**
	 * ������ڼ��Զ����ɹ���
	 *
	 * @param settingMap
	 *            �Զ���������Map
	 * @param currPeriodVO
	 *            ��ǰ�ڼ�VO
	 * @param allAccPeriods
	 *            ���л���ڼ�
	 * @throws Exception
	 * @throws Exception
	 */
	public PeriodVO[] autoGenerateByAccPeriod(Map<String, String> settingMap, AccPeriodVO[] allAccPeriods)
			throws BusinessException {
		if (ArrayUtils.isEmpty(allAccPeriods)) {
			throw new BusinessException(ResHelper.getString("60130period","060130period0093")/*@res "û���ҵ����������Ļ�׼�ڼ䣡"*/);
		}

		// ԭ���ڼ�����һ���ڼ�
		PeriodVO lastPeriodVO = null;
		String lastPeriod = null;
		if (oldItems != null && oldItems.length > 0) {
			// ����û��������ʼн���ڼ��ǵ�һ��н���ڼ䣬���յ�ǰʱ�������ڼ�ȷ��н���ڼ����ʼ��,�������һ���ڼ��ʱ�䶨
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
			throw new BusinessException(ResHelper.getString("60130period","060130period0094")/*@res "�÷�������ǰ��н���ڼ䲻�ǰ���Ĭ�ϻ���ڼ����õģ���������Ĭ�ϻ���ڼ����ú����ڼ䣡"*/);
		}
		if (lastPeriodVO == null
				|| (MessageDialog.showYesNoDlg(parent, null,
						ResHelper.getString("60130period","060130period0124")/*@res "�Ѿ�����Ĭ��н���ڼ䣬ʹ�ø�н���ڼ�ΪĬ���ڼ����ú������Ҫ������������"*/) == nc.ui.pub.beans.UIDialog.ID_YES)) {
			{
				// ����ڼ�
				AccPeriodVO[] accPeriods = getAccPeriods(lastPeriod, allAccPeriods);
				// ����н���ڼ�VO����
				return createPeriodByAccPeriods(accPeriods, lastPeriodVO);
			}
		}
		return null;
	}

	/**
	 * ����ԭ�е�н���ڼ����ʼ�ڼ䣬��ȡ��Ӧ�Ļ���ڼ�
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
	 * �������һ��н���ڼ�,�ж���ǰ��н���ڼ��Ƿ���Ĭ�ϵĻ���ڼ����õ� �����,����Ĭ�ϻ���ڼ��������,���������ʾ
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
			// "���Ļ���ڼ�"��"����н���ڼ�"���бȽ�
			return true;
		}
		return false;
	}

	/**
	 * ���ջ���ڼ�����н���ڼ�VO����
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
			// ���˵�һ���ظ��Ļ���ڼ�
			PeriodVO vo = new PeriodVO();
			String accYear = accPeriods[i].getPeriodyear();
			String accMonth = accPeriods[i].getAccperiodmth();
			UFDate startDate = accPeriods[i].getBegindate();
			UFDate endDate = accPeriods[i].getEnddate();

//			if (i == 0 && lastPeriodVO != null) {
//				// ��һ������ڼ���Ҫ���ո÷�����һ���ڼ�������ڵ���
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
			// �Զ������ڼ�Ϊ��
			throw new BusinessException(ResHelper.getString("60130period","060130period0097")/*@res "�Զ������ڼ�Ϊ�գ���˲��Ӧ�Ļ���ڼ䣡"*/);
		} else {
			// ��������VO�ӱ�����
			return items;
		}
	}

}