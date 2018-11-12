package nc.impl.pub;

import nc.bs.dao.BaseDAO;
import nc.pubimpl.ta.overtime.SegDetailServiceImpl;
import nc.pubitf.para.SysInitQuery;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OTLeaveBalanceVO;

public class OTLeaveBalancePubServiceImpl {
    private BaseDAO baseDAO = null;

    /**
     * �����������㲹�ݼ�����
     * 
     * @param pk_org
     *            ������Դ��֯PK
     * @param pk_psndocs
     *            ��ԱPK����
     * @param pk_depts
     *            ��������
     * @param pk_leavetypecopy
     *            �ݼ����PK
     * @param maxdate
     *            �������ݼ�����
     * @param beginDate
     *            ��ʼ����
     * @param endDate
     *            ��ֹ����
     * @return
     * @throws BusinessException
     */
    public OTLeaveBalanceVO[] queryOTLeaveAggvos(String pk_org, String[] pk_psndocs, String[] pk_depts,
	    String pk_leavetypecopy, String maxdate, String beginDate, String endDate) throws BusinessException {
	OTLeaveBalanceVO[] ret = null;
	String initleavetype = SysInitQuery.getParaString(pk_org, "TWHRT08");
	if (pk_leavetypecopy.equals(initleavetype)) {
	    // ���ؼӰ���
	    // �����ڷ�Χ���ҼӰ�ֶ���ϸ
	    // ���ڷ�Χ���ƵĲ�����Դ���ݵ����ڣ����ǲ����ļ�������������ڸ�������
	    // ISegDetailService service =
	    // NCLocator.getInstance().lookup(ISegDetailService.class);
	    ret = new SegDetailServiceImpl().getOvertimeToRestHoursByType(pk_org, pk_psndocs, new UFLiteralDate(
		    beginDate), new UFLiteralDate(endDate), null);
	}
	return ret;
    }

    public BaseDAO getBaseDAO() {
	if (baseDAO == null) {
	    baseDAO = new BaseDAO();
	}
	return baseDAO;
    }

}