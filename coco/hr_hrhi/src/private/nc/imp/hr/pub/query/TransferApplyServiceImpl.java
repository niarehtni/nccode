package nc.imp.hr.pub.query;

import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.itf.hr.pub.query.ITransferApplyService;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trn.transmng.StapplyVO;

public class TransferApplyServiceImpl implements ITransferApplyService{

	private final BaseDAO baseDAOManager = new BaseDAO();
	
	@Override
	public boolean isHasPsnJobVOByEffectDate(UFLiteralDate EffectDate,
			String pk_psndoc) throws BusinessException {
		String sql = "select pk_psndoc from hi_psnjob where ( begindate >='"+EffectDate+"' or ( enddate >='"+EffectDate+"' and enddate is not null ))and hi_psnjob.lastflag = 'Y' and hi_psnjob.endflag = 'N' and pk_psndoc='"+pk_psndoc+"'";
		@SuppressWarnings("rawtypes")
		List objResult = (List) baseDAOManager.executeQuery(sql, new ArrayListProcessor());
		return (objResult != null && objResult.size()>0);
	}

	@Override
	public StapplyVO[] onNcPrepareRute(String pk_psndoc) throws BusinessException {
		String sql = "select pk_psndoc,bill_code  from hi_stapply where (approve_state = '0' or approve_state = '1' or approve_state = '3' or approve_state = '2' ) and  pk_billtype='6113' and pk_psndoc='"+pk_psndoc+"'";
		@SuppressWarnings("unchecked")
		List<StapplyVO> objResult = (List<StapplyVO>) baseDAOManager.executeQuery(sql, new BeanListProcessor(StapplyVO.class));
		return  (StapplyVO[]) ((objResult != null && objResult.size()>0)?objResult.toArray(new StapplyVO[0]):new StapplyVO[0]);
	}

	@Override
	public StapplyVO[] onPortalPrepareRute(String pk_psndoc)
			throws BusinessException {
		String sql = "select pk_psndoc,bill_code  from hrss_specialstapply where (approve_state = '0' or approve_state = '1' or approve_state = '3' or approve_state = '2' ) and  pk_billtype='6119' and pk_psndoc='"+pk_psndoc+"'";
		List<StapplyVO> objResult = (List<StapplyVO>) baseDAOManager.executeQuery(sql,  new BeanListProcessor(StapplyVO.class));
		return (StapplyVO[]) ((objResult != null && objResult.size()>0)?objResult.toArray(new StapplyVO[0]):new StapplyVO[0]);
	}

	@Override
	public StapplyVO[] onPortalAndNcPrepareRute(String pk_psndoc)
			throws BusinessException {
		String sql = "select pk_psndoc,bill_code  from hrss_specialstapply where (approve_state = '0' or approve_state = '1' or approve_state = '3' or approve_state = '2' ) and  pk_billtype='6119' and pk_psndoc='"+pk_psndoc+"'";
		sql += "union all select pk_psndoc,bill_code  from hi_stapply where (approve_state = '0' or approve_state = '1' or approve_state = '3' or approve_state = '2' ) and  pk_billtype='6113' and pk_psndoc='"+pk_psndoc+"'";
		List<StapplyVO> objResult = (List<StapplyVO>) baseDAOManager.executeQuery(sql, new BeanListProcessor(StapplyVO.class));
		return (StapplyVO[]) ((objResult != null && objResult.size()>0)?objResult.toArray(new StapplyVO[0]):new StapplyVO[0]);
	}

}
