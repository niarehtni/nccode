package nc.impl.hrsms.ta.SignReg;


import nc.bs.bd.baseservice.BaseService;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.IEventType;
import nc.itf.hrsms.ta.SignReg.ISignRegManageMaintain;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.pub.BusinessException;
import nc.vo.ta.signcard.AggSignVO;
import nc.vo.ta.signcard.SignRegVO;

@SuppressWarnings("deprecation")
public class SignRegManageMaintainImpl extends BaseService<SignRegVO> implements ISignRegManageMaintain{

	
	SignRegQueryMaintainImpl signRegMaintain = null;
	public SignRegQueryMaintainImpl getSignRegMaintain(){
		if(signRegMaintain == null)
			signRegMaintain= new SignRegQueryMaintainImpl();
		return signRegMaintain;
	}
	
	public static final String DOC_NAME = "8cc504ed-4371-43c6-81a0-8758684b436f";

	public SignRegManageMaintainImpl(){
		super(DOC_NAME);
	}
	

	private IMDPersistenceService getMDService() {
		return MDPersistenceService.lookupPersistenceService();
	}
	@SuppressWarnings("unused")
	private static IMDPersistenceQueryService getMDQueryService() {
		return MDPersistenceService.lookupPersistenceQueryService();
	}

	@Override
	public void delete(AggSignVO vo) throws BusinessException {
		// TODO Auto-generated method stub
		//getServiceTemplate().delete(vo);
		//发送事件      可以进行 例如 删除与之对应的工作日历等操作
		EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_DELETE_BEFORE,vo));
		//数据库删除
		this.getMDService().deleteBillFromDB(vo);
		//删除后事件
		EventDispatcher.fireEvent(new BusinessEvent(DOC_NAME, IEventType.TYPE_DELETE_AFTER,vo));

	}

	@Override
	public AggSignVO insert(AggSignVO vo) throws BusinessException {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public AggSignVO update(AggSignVO vo) throws BusinessException {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public AggSignVO disable(AggSignVO vo) throws BusinessException {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public AggSignVO enable(AggSignVO vo) throws BusinessException {
		// TODO 自动生成的方法存根
		return null;
	}


}
