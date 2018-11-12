package nc.impl.wa.paydata;

import java.util.LinkedList;
import java.util.List;

import nc.vo.pub.BusinessException;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * Ϊ����ʱ�Ǻϲ���˰��� ��Ҫ���¼�˰
 * 
 * @author: zhangg
 * @date: 2010-7-14 ����02:23:33
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class TaxBindCaculateService extends DataCaculateService {

	/**
	 * @author zhangg on 2010-7-14
	 * @param loginContext
	 * @throws BusinessException
	 */
	public TaxBindCaculateService(WaLoginContext loginContext) throws BusinessException {
		super(loginContext, null, null);

	}

	// shenliangc 20140830 �ϲ���˰�����������ֻ��������ϲ�ѯ��������Ա���ݣ���Ҫ�������������
	public TaxBindCaculateService(WaLoginContext loginContext, String whereCondition) throws BusinessException {
		super(loginContext, null, whereCondition);

	}

	/**
	 * @author zhangg on 2010-7-14
	 * @see nc.impl.wa.paydata.AbstractCaculateService#getClassItemVOs()
	 */
	@Override
	public WaClassItemVO[] getClassItemVOs() {
		List<WaClassItemVO> list = new LinkedList<WaClassItemVO>();
		WaClassItemVO[] classItemVOs = super.getClassItemVOs();
		boolean findf7 = false;
		for (WaClassItemVO waClassItemVO : classItemVOs) {
			if (waClassItemVO.getItemkey().equalsIgnoreCase("f_7")) {
				findf7 = true;
			}
			if (findf7) {
				list.add(waClassItemVO);
			}
		}
		return list.isEmpty() ? null : list.toArray(new WaClassItemVO[0]);

	}

	@Override
	public void doCaculate() throws BusinessException {
		// ssx add on 20181105
		// for Decrypt before payment calculation.
		doDecryptWaData();
		// end

		WaClassItemVO[] classItemVOs = getClassItemVOs();
		doCaculate(classItemVOs);
		doPsnTax();
		// guoqt��˵�ʱ�������Ϻ�����м������
		clearMidData();

		// ssx add on 20181105
		// for Encrypt after payment calculation.
		doEncrypt();
		// end
	}
}
