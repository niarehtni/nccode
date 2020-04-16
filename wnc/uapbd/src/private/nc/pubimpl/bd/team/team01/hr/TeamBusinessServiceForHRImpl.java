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
 * @author zhanghm1 为HR提供的删除，修改班组人员信息的服务
 */
public class TeamBusinessServiceForHRImpl extends BaseService<TeamHeadVO> implements ITeamBusinessServiceForHR {

	/**
	 * 构造函数
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
	 * 为HR提供的删除服务
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
	 * HR传过来的数据去更新班组的数据库。
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
		// 调用BP
		TeamInsertBP bp = new TeamInsertBP();
		AggTeamVO[] retvos = bp.insert(new AggTeamVO[] { aggvo });
		return retvos[0];
	}

	@Override
	public AggTeamVO updateAggTeamVO(AggTeamVO aggvo) throws BusinessException {
		// 加锁 + 检查ts
		BillTransferTool<AggTeamVO> transTool = new BillTransferTool<AggTeamVO>(new AggTeamVO[] { aggvo });

		// 获得修改前vo
		AggTeamVO[] originBills = transTool.getOriginBills();
		// 调用BP
		TeamUpdateBP bp = new TeamUpdateBP();
		AggTeamVO[] retBills = bp.update(new AggTeamVO[] { aggvo }, originBills);
		return retBills[0];
	}

	@Override
	public void deleteAggTeamVO(AggTeamVO[] aggvos) throws BusinessException {
		// 加锁 比较ts
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

			// LiFIXME: 数据权限校验

			// 加技术锁（主键锁）
			BDPKLockUtil.lockSuperVO(headvo);

			// 版本校验（时间戳校验）
			/*
			 * VOConcurrentTool voConcTool =new VOConcurrentTool();
			 * voConcTool.checkTSWithDB(headvo);
			 */
			BDVersionValidationUtil.validateObject(headvo);

			if (headvo != null && headvo.length > 0) {
				// 设置停用标志
				this.setDisableFlags(headvo);

				// 数据保存到数据库
				this.dbDisableVOs(headvo);

				// 检索已被停用的VO
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

			// LiFIXME: 数据权限校验

			// 加技术锁（主键锁）
			BDPKLockUtil.lockSuperVO(headvo);

			// 版本校验（时间戳校验）
			/*
			 * VOConcurrentTool voConcTool =new VOConcurrentTool();
			 * voConcTool.checkTSWithDB(headvo);
			 */
			BDVersionValidationUtil.validateSuperVO(headvo);

			if (headvo != null && headvo.length > 0) {
				// 设置启用标志
				this.setEnableFlags(headvo);

				// 事件前批量通知
				// this.fireBeforeEnableEvent(canEnableOldVOs, canEnableVOs);

				// 数据保存到数据库
				this.dbEnableVOs(headvo);

				// 检索已被封存的VO
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
