package nc.itf.hi;

import nc.itf.hr.hi.WadocQueryVO;
import nc.vo.pub.BusinessException;

public interface IPsndocWadocSynNHIService {
    /**
     * �������{н��ԃVOͬ���T���ڽ����Y��
     * 
     * @param wadocQueryVOs
     *            ���{н��ԃVO
     * @throws BusinessException
     * @throws Exception
     */
    public void synNHIByWadocQueryVO(WadocQueryVO[] wadocQueryVOs) throws BusinessException, Exception;
}
