/**
 * 
 */
package nc.pubimpl.bd.team.team01.hr;

import nc.bs.bd.baseservice.BaseService;
import nc.bs.bd.team.team01.bp.TeamDeleteBP;
import nc.bs.bd.team.team01.bp.TeamInsertBP;
import nc.bs.bd.team.team01.bp.TeamUpdateBP;
import nc.bs.logging.Logger;
import nc.impl.pubapp.pattern.data.bill.tool.BillTransferTool;
import nc.impl.pubapp.pattern.data.vo.VODelete;
import nc.impl.pubapp.pattern.data.vo.VOUpdate;
import nc.md.MDBaseQueryFacade;
import nc.md.model.MetaDataException;
import nc.pubitf.bd.team.team01.hr.ITeamBusinessServiceForHR;
import nc.util.mmf.framework.base.MMValueCheck;
import nc.vo.bd.team.team01.entity.AggTeamVO;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.bd.team.team01.entity.TeamItemVO;
import nc.vo.bd.team.team01.message.TeamConstant;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDVersionValidationUtil;

/**
 * @author zhanghm1 ΪHR�ṩ��ɾ�����޸İ�����Ա��Ϣ�ķ���
 */
public class TeamBusinessServiceForHRImpl extends BaseService<TeamHeadVO> implements ITeamBusinessServiceForHR {

	/**
	 * ���캯��
	 */
	public TeamBusinessServiceForHRImpl() {
		super(TeamBusinessServiceForHRImpl.mdId);
	}

	private static String mdId;
	static {
		try {
			TeamBusinessServiceForHRImpl.mdId = MDBaseQueryFacade.getInstance()
					.getBeanByFullClassName(TeamHeadVO.class.getName()).getID();
		} catch (MetaDataException e) {
			Logger.error(e.getMessage(), e);
			ExceptionUtils.wrappBusinessException(e.getMessage(), e);
		}
	}

	/**
	 * ΪHR�ṩ��ɾ������
	 */
	@Override
	public void deleteBzdyItem(TeamItemVO itemvo) throws BusinessException {
		VODelete<TeamItemVO> vodelete = new VODelete<TeamItemVO>();
		try {

			vodelete.delete(new TeamItemVO[] { itemvo });
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}

	}

	/**
	 * HR������������ȥ���°�������ݿ⡣
	 */
	@Override
	public void updateBZdyItemenddate(TeamItemVO itemvo, UFLiteralDate enddate) throws BusinessException {

		try {
			if (itemvo == null) {
				return;
			}
			TeamItemVO originvo = (TeamItemVO) itemvo.clone();
			TeamItemVO updatevo = itemvo;
			if (MMValueCheck.isEmpty(enddate))
				return;
			if (TeamConstant.END_DATE.equals(enddate.toString())) {
				updatevo.setDenddate(null);
			} else {
				if (updatevo.getDstartdate() != null
						&& (updatevo.getDstartdate().isSameDate(enddate) || updatevo.getDstartdate().before(enddate))) {
					updatevo.setDenddate(enddate);
				}
			}
			VOUpdate<TeamItemVO> voupdate = new VOUpdate<TeamItemVO>();
			voupdate.update(new TeamItemVO[] { updatevo }, new TeamItemVO[] { originvo });
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}

	}

	@Override
	public AggTeamVO insertAggTeamVO(AggTeamVO aggvo) throws BusinessException {
		BillTransferTool<AggTeamVO> transferTool = new BillTransferTool<AggTeamVO>(new AggTeamVO[] { aggvo });
		// ����BP
		TeamInsertBP bp = new TeamInsertBP();
		AggTeamVO[] retvos = bp.insert(new AggTeamVO[] { aggvo });
		return retvos[0];
	}

	@Override
	public AggTeamVO updateAggTeamVO(AggTeamVO aggvo) throws BusinessException {
		// ���� + ���ts
		BillTransferTool<AggTeamVO> transTool = new BillTransferTool<AggTeamVO>(new AggTeamVO[] { aggvo });

		// ����޸�ǰvo
		AggTeamVO[] originBills = transTool.getOriginBills();
		// ����BP
		TeamUpdateBP bp = new TeamUpdateBP();
		AggTeamVO[] retBills = bp.update(new AggTeamVO[] { aggvo }, originBills);
		return retBills[0];
	}

	@Override
	public void deleteAggTeamVO(AggTeamVO[] aggvos) throws BusinessException {
		// ���� �Ƚ�ts
		BillTransferTool<AggTeamVO> transferTool = new BillTransferTool<AggTeamVO>(aggvos);

		AggTeamVO[] fullBills = transferTool.getClientFullInfoBill();
		TeamDeleteBP deleteBP = new TeamDeleteBP();
		deleteBP.delete(fullBills);

	}

	@Override
	public AggTeamVO[] seal(AggTeamVO object[]) throws BusinessException {

		TeamHeadVO[] headvo = new TeamHeadVO[object.length];
		for (int i = 0; i < object.length; i++) {
			headvo[i] = (TeamHeadVO) object[i].getParentVO();
		}
		try {

			if (headvo == null || headvo.length == 0) {
				return null;
			}

			// LiFIXME: ����Ȩ��У��

			// �Ӽ���������������
			BDPKLockUtil.lockSuperVO(headvo);

			// �汾У�飨ʱ���У�飩
			/*
			 * VOConcurrentTool voConcTool =new VOConcurrentTool();
			 * voConcTool.checkTSWithDB(headvo);
			 */
			BDVersionValidationUtil.validateObject(headvo);

			if (headvo != null && headvo.length > 0) {
				// ����ͣ�ñ�־
				this.setDisableFlags(headvo);

				// ���ݱ��浽���ݿ�
				this.dbDisableVOs(headvo);

				// �����ѱ�ͣ�õ�VO
				headvo = this.retrieveVOs(headvo);
				for (AggTeamVO aggvo : object) {
					for (TeamHeadVO hvo : headvo) {
						if (((TeamHeadVO) aggvo.getParentVO()).getPrimaryKey().equals(hvo.getPrimaryKey())) {
							((TeamHeadVO) aggvo.getParentVO()).setTs(hvo.getTs());
						}
					}
				}

			}
			return object;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;

	}

	@Override
	public AggTeamVO[] unSeal(AggTeamVO object[]) throws BusinessException {

		TeamHeadVO[] headvo = new TeamHeadVO[object.length];
		for (int i = 0; i < object.length; i++) {
			headvo[i] = (TeamHeadVO) object[i].getParentVO();
		}

		try {

			if (headvo == null || headvo.length == 0) {
				return null;
			}

			// LiFIXME: ����Ȩ��У��

			// �Ӽ���������������
			BDPKLockUtil.lockSuperVO(headvo);

			// �汾У�飨ʱ���У�飩
			/*
			 * VOConcurrentTool voConcTool =new VOConcurrentTool();
			 * voConcTool.checkTSWithDB(headvo);
			 */
			BDVersionValidationUtil.validateSuperVO(headvo);

			if (headvo != null && headvo.length > 0) {
				// �������ñ�־
				this.setEnableFlags(headvo);

				// �¼�ǰ����֪ͨ
				// this.fireBeforeEnableEvent(canEnableOldVOs, canEnableVOs);

				// ���ݱ��浽���ݿ�
				this.dbEnableVOs(headvo);

				// �����ѱ�����VO
				headvo = this.retrieveVOs(headvo);
				for (AggTeamVO aggvo : object) {
					for (TeamHeadVO hvo : headvo) {
						if (((TeamHeadVO) aggvo.getParentVO()).getPrimaryKey().equals(hvo.getPrimaryKey())) {
							((TeamHeadVO) aggvo.getParentVO()).setTs(hvo.getTs());
						}
					}
				}

			}

			return object;
		} catch (Exception e) {
			ExceptionUtils.marsh(e);
		}
		return null;

	}
}
