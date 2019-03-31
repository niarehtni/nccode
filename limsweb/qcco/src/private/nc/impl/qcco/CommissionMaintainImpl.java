package nc.impl.qcco;

import java.util.ArrayList;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.impl.pub.ace.AceCommissionPubServiceImpl;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.qcco.commission.AggCommissionHVO;
import nc.vo.qcco.commission.CommissionBVO;
import nc.vo.qcco.commission.CommissionHVO;
import nc.vo.qcco.commission.CommissionRVO;
import nc.itf.qcco.ICommissionMaintain;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;

public class CommissionMaintainImpl extends AceCommissionPubServiceImpl
		implements ICommissionMaintain {

	private BaseDAO dao = null;
	

	public BaseDAO getDao() {
		if(dao == null){
			dao = new BaseDAO();
		}
		return dao;
	}

	

	/*@Override
	public AggCommissionHVO[] insert(AggCommissionHVO[] clientFullVOs,
			AggCommissionHVO[] originBills) throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggCommissionHVO[] update(AggCommissionHVO[] clientFullVOs,
			AggCommissionHVO[] originBills) throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}
*/
	@Override
	public AggCommissionHVO[] query(IQueryScheme queryScheme)
			throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggCommissionHVO[] save(AggCommissionHVO[] clientFullVOs,
			AggCommissionHVO[] originBills) throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggCommissionHVO[] unsave(AggCommissionHVO[] clientFullVOs,
			AggCommissionHVO[] originBills) throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggCommissionHVO[] approve(AggCommissionHVO[] clientFullVOs,
			AggCommissionHVO[] originBills) throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggCommissionHVO[] unapprove(AggCommissionHVO[] clientFullVOs,
			AggCommissionHVO[] originBills) throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggCommissionHVO[] insert(AggCommissionHVO[] vos) throws BusinessException {
		return super.pubinsertBills(vos,null);
	}

	@Override
	public void delete(AggCommissionHVO[] vos) throws BusinessException {
		super.pubdeleteBills(vos);
		
	}

	@Override
	public AggCommissionHVO[] update(AggCommissionHVO[] vos)
			throws BusinessException {
		List<String> list = new ArrayList<String>();
		for(AggCommissionHVO vo : vos){
			list.add((String) vo.getParent().getAttributeValue("pk_commission_h"));
		}
		InSQLCreator insql = new InSQLCreator();
		String commissionInSQL = insql.getInSQL(list.toArray(new String[0]));
		List<CommissionHVO> lists = (List<CommissionHVO>) this.getDao().retrieveByClause(CommissionHVO.class, "pk_commission_h in("+commissionInSQL+")");
		if(lists.size() > 0){
			for(AggCommissionHVO vo : vos){
				CommissionBVO[] bvos = (CommissionBVO[])vo.getChildrenVO();
				for(CommissionHVO hvo : lists){
					if(null != hvo.getAttributeValue("pk_commission_h") && vo.getParentVO().getAttributeValue("pk_commission_h") !=null && hvo.getAttributeValue("pk_commission_h").equals(vo.getParentVO().getAttributeValue("pk_commission_h"))){
						for(CommissionBVO bvo : bvos){
							bvo.setTs(hvo.getTs());
							CommissionRVO[] rvos = bvo.getPk_commission_r();
							for(CommissionRVO rvo : rvos){
								rvo.setTs(hvo.getTs());
							}
						}
					}
				}
				
			}
		}

		return super.pubupdateBills(vos);
	}

	

	/*@Override
	public void delete(AggCommissionHVO[] clientFullVOs,
			AggCommissionHVO[] originBills) throws BusinessException {
		// TODO Auto-generated method stub
		
	}*/

	

}
