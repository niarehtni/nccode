package nc.itf.wa.psndocwadoc;

import nc.vo.pub.BusinessException;

/**
 * �ͽ������ɷ���
 * 
 * @author Connie.ZH
 * 
 */
public interface IPsndocBOISGenerateService {

	/**
	 * �������x���ˆT�c��Ч���ڣ��������Ʉڽ����ļ�������
	 * 
	 * @param effectiveDate
	 *            ������ѡ����Ч����
	 * @param rangePsns
	 *            ��������ѡ�����Ա
	 * @throws BusinessException
	 */
	public void generateBOISData(String effectiveDate, String[] rangePsns) throws BusinessException;

}
