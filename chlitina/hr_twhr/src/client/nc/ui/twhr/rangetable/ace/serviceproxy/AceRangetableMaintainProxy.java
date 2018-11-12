package nc.ui.twhr.rangetable.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

/**
 * ʾ�����ݵĲ�������
 * 
 * @since 6.0
 * @version 2011-7-6 ����08:31:09
 * @author duy
 */
public class AceRangetableMaintainProxy 
             implements 
                  IDataOperationService,
                  IQueryService 
                     {
    @Override
    public IBill[] insert(IBill[] value) throws BusinessException {
        nc.itf.twhr.IRangetableMaintain operator = NCLocator.getInstance().lookup(nc.itf.twhr.IRangetableMaintain.class);
        RangeTableAggVO[] vos = operator.insert((RangeTableAggVO[]) value);
        return vos;
    }
    @Override
    public IBill[] update(IBill[] value) throws BusinessException {
        nc.itf.twhr.IRangetableMaintain operator = NCLocator.getInstance().lookup(nc.itf.twhr.IRangetableMaintain.class);
        RangeTableAggVO[] vos = operator.update((RangeTableAggVO[]) value);
        return vos;
    }
    @Override
    public IBill[] delete(IBill[] value) throws BusinessException {
        // Ŀǰ��ɾ�����������������������pubapp��֧�ִ����������ִ��ɾ������
        // ���ݵ�ɾ��ʵ����ʹ�õ��ǣ�nc.ui.mmpd.samplebill.serviceproxy.SampleDeleteProxy
        nc.itf.twhr.IRangetableMaintain operator = NCLocator.getInstance().lookup(nc.itf.twhr.IRangetableMaintain.class);
        operator.delete((RangeTableAggVO[]) value);
        return value;
    }
    @Override
    public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception {
        nc.itf.twhr.IRangetableMaintain query = NCLocator.getInstance().lookup(nc.itf.twhr.IRangetableMaintain.class);
        return query.query(queryScheme);
    }
    
}
