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
		try {
			// ssx add on 2019-05-26 doEncrypt() only on this time:
			// The extremely unexpected phenomenon that some exception
			// happened on unknown reason within the calculation interrupted
			// without rolled back the transactions, cause the encrypted data
			// cannot be decrypted before interrupt, and those would be the
			// error on next calculation.
			doEncrypt();
			// end

			// ssx add on 20181105
			// for Decrypt before payment calculation.
			// ��ʼ���ˆT�Ӽ�������Ϣ
			this.getDEUtil().initPsnInfosets();
			doDecryptPsnInfoset();
			// end

			// ssx add on 20181105
			// for Decrypt before payment calculation.
			doDecryptWaData();
			// end

			// ssx init overtime fee calculate table
			initFeeCalculateTable();
			// end

			WaClassItemVO[] classItemVOs = getClassItemVOs();
			doCaculate(classItemVOs);
			doPsnTax();
			// guoqt��˵�ʱ�������Ϻ�����м������
			clearMidData();

			// ssx clear overtime fee calculate table
			clearFeeCalculateTable();
			// end
		} finally {
			// ssx add on 20181105
			// for Encrypt after payment calculation.
			doEncrypt();
			// end
		}
	}
}
