package nc.impl.bd.shift.listener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.hr.utils.ResHelper;
import nc.impl.bd.shift.ShiftMaintainImpl;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;






public class DefaultShiftListener
  implements IBusinessListener
{
  ShiftMaintainImpl manageMaintain;
  
  public void doAction(IBusinessEvent event)
    throws BusinessException
  {
    BDCommonEvent be = (BDCommonEvent)event;
    OrgVO orgvo = (OrgVO)be.getNewObjs()[0];
    if (orgvo == null) {
      return;
    }
    if ((orgvo.getIsbusinessunit() == null) || (!orgvo.getIsbusinessunit().booleanValue()))
      return;
    String dupCondition = "code='DEFAULT' and pk_org= '" + orgvo.getPk_org() + "'";
    
    if (getManageMaintain().queryByCondition(dupCondition) != null)
      return;
    String condition = "pk_shift='0001Z70000000DEFAULT'";
    
    AggShiftVO[] vos = getManageMaintain().queryByCondition(condition);
    
    //leo add below line: {17682} when adding default shift, set the default gzsj=8; 2018-03-19
    vos[0].getShiftVO().setGzsj(new UFDouble(8));
    
    if (ArrayUtils.isEmpty(vos))
      return;
    AggShiftVO aggvo = vos[0];
    
    aggvo = convertToDefaultShift(aggvo, orgvo);

    getManageMaintain().insertShiftVO(aggvo);
  }
  








  private AggShiftVO convertToDefaultShift(AggShiftVO aggvo, OrgVO orgvo)
  {
    ShiftVO vo = aggvo.getShiftVO();
    vo.setPk_shift(null);
    vo.setCreationtime(null);
    vo.setModifiedtime(null);
    vo.setPk_org(orgvo.getPk_org());
    vo.setPk_group(orgvo.getPk_group());
    vo.setStatus(2);
    if (StringUtils.isEmpty(vo.getName()))
      vo.setName(ResHelper.getString("hrbd", "0hrbd0153"));
    for (String tableCode : aggvo.getTableCodes()) {
      SuperVO[] subVOs = (SuperVO[])aggvo.getTableVO(tableCode);
      if (!ArrayUtils.isEmpty(subVOs))
      {
        for (SuperVO subVO : subVOs) {
          subVO.setPrimaryKey(null);
          subVO.setAttributeValue("pk_shift", null);
          subVO.setAttributeValue("pk_group", orgvo.getPk_group());
          subVO.setAttributeValue("pk_org", orgvo.getPk_org());
          subVO.setStatus(2);
        } }
    }
    return aggvo;
  }
  
  private ShiftMaintainImpl getManageMaintain() {
    if (manageMaintain == null) {
      manageMaintain = new ShiftMaintainImpl();
    }
    return manageMaintain;
  }
}

