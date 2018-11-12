package nc.ui.twhr.rangetable.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.vo.twhr.rangetable.RangeTableAggVO;
/**
 * 示例单据的删除代理，支持批删除
 * 
 * @since 6.0
 * @version 2011-8-4 上午07:22:35
 * @author duy
 */
public class AceRangetableDeleteProxy implements ISingleBillService<RangeTableAggVO> {

    @Override
    public RangeTableAggVO operateBill(RangeTableAggVO bill) throws Exception {
        nc.itf.twhr.IRangetableMaintain operator = NCLocator.getInstance().lookup(nc.itf.twhr.IRangetableMaintain.class);
        operator.delete(new RangeTableAggVO[] {
            bill
        });
        return bill;
    }

}
