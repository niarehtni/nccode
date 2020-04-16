package nc.impl.pub.ace;

import nc.bs.dao.BaseDAO;
import nc.bs.wa.itemgroup.ace.bp.AceWaitemgroupInsertBP;
import nc.bs.wa.itemgroup.ace.bp.AceWaitemgroupUpdateBP;
import nc.impl.pubapp.pattern.data.bill.BillLazyQuery;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.pubapp.query2.sql.process.QuerySchemeProcessor;
import nc.vo.wa.itemgroup.AggItemGroupVO;
import nc.vo.wa.itemgroup.ItemGroupMemberVO;

public abstract class AceWaitemgroupPubServiceImpl {
	// ����
	public AggItemGroupVO[] pubinsertBills(AggItemGroupVO[] vos)
			throws BusinessException {
		try {
			org2Glb(vos);
			// ���ݿ������ݺ�ǰ̨���ݹ����Ĳ���VO�ϲ���Ľ��
			BillTransferTool<AggItemGroupVO> transferTool = new BillTransferTool<AggItemGroupVO>(
					vos);
			AggItemGroupVO[] mergedVO = transferTool.getClientFullInfoBill();

			// ����BP
			AceWaitemgroupInsertBP action = new AceWaitemgroupInsertBP();
			AggItemGroupVO[] retvos = action.insert(mergedVO);
			// ���췵������
			return transferTool.getBillForToClient(retvos);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	// ɾ��
	public void pubdeleteBills(AggItemGroupVO[] vos) throws BusinessException {
		try {
			// ���� �Ƚ�ts
			//BillTransferTool<AggItemGroupVO> transferTool = new BillTransferTool<AggItemGroupVO>(
			//		vos);
			//AggItemGroupVO[] fullBills = transferTool.getClientFullInfoBill();
			//AceWaitemgroupDeleteBP deleteBP = new AceWaitemgroupDeleteBP();
//			deleteBP.delete(fullBills);
			BaseDAO base = new BaseDAO();
			String pkhvo = vos[0].getParentVO().getPk_itemgroup();
			//ItemGroupMemberVO[] pkbvo = (ItemGroupMemberVO[]) vos[0].getChildrenVO();
			String deletesqlparent = "update wa_itemgroup set dr='1' where pk_itemgroup = '"+pkhvo+"'";
			String deletesqlchilen = "update wa_itemgroupmember set dr='1' where pk_itemgroup = '"+pkhvo+"'";
			base.executeUpdate(deletesqlparent);
			base.executeUpdate(deletesqlchilen);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
	}

	// �޸�
	public AggItemGroupVO[] pubupdateBills(AggItemGroupVO[] vos)
			throws BusinessException {
		try {
			org2Glb(vos);
			// ���� + ���ts
			BillTransferTool<AggItemGroupVO> transTool = new BillTransferTool<AggItemGroupVO>(
					vos);
			// ��ȫǰ̨VO
			AggItemGroupVO[] fullBills = transTool.getClientFullInfoBill();
			// ����޸�ǰvo
			AggItemGroupVO[] originBills = transTool.getOriginBills();
			// ����BP
			AceWaitemgroupUpdateBP bp = new AceWaitemgroupUpdateBP();
			AggItemGroupVO[] retBills = bp.update(fullBills, originBills);
			// ���췵������
			retBills = transTool.getBillForToClient(retBills);
			return retBills;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;
	}

	public AggItemGroupVO[] pubquerybills(IQueryScheme queryScheme)
			throws BusinessException {
		AggItemGroupVO[] bills = null;
		try {
			this.preQuery(queryScheme);
			BillLazyQuery<AggItemGroupVO> query = new BillLazyQuery<AggItemGroupVO>(
					AggItemGroupVO.class);
			bills = query.query(queryScheme, null);
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return bills;
	}

	/**
	 * ������ʵ�֣���ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
	 * 
	 * @param queryScheme
	 */
	protected void preQuery(IQueryScheme queryScheme) {
		// ��ѯ֮ǰ��queryScheme���мӹ��������Լ����߼�
		QuerySchemeProcessor qry = new QuerySchemeProcessor(queryScheme);
		qry.appendWhere(" and pk_org ='GLOBLE00000000000000' ");
	}

	private void org2Glb(AggItemGroupVO[] vos){
		if(vos!=null){
			for(AggItemGroupVO aggvo : vos){
				if(aggvo!=null){
					aggvo.getParentVO().setPk_org("GLOBLE00000000000000");
					aggvo.getParentVO().setPk_org_v("GLOBLE00000000000000");
					aggvo.getParentVO().setPk_group("GLOBLE00000000000000");
					ISuperVO[] superVOs = aggvo.getChildren(nc.vo.wa.itemgroup.ItemGroupMemberVO.class);
					if(superVOs!=null && superVOs.length > 0){
						for(ISuperVO supervo : superVOs){
							((ItemGroupMemberVO)supervo).setPk_org("GLOBLE00000000000000");
							((ItemGroupMemberVO)supervo).setPk_org_v("GLOBLE00000000000000");
							((ItemGroupMemberVO)supervo).setPk_group("GLOBLE00000000000000");
						}
					}
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
}