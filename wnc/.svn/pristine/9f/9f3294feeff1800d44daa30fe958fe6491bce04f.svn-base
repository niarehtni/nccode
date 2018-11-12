package nc.impl.trn.listener;

import java.util.Collection;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.dao.BaseDAO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.pub.HiBatchEventValueObject;
import nc.vo.hi.pub.HiEventValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.vorg.DeptVersionVO;

public class PsnjobSynDeptVersionBusinessListener implements IBusinessListener {
	private BaseDAO baseDao = null;

	@Override
	public void doAction(IBusinessEvent eventObject) throws BusinessException {
		if (!(eventObject instanceof BusinessEvent)) {
			return;
		}

		BusinessEvent be = (BusinessEvent) eventObject;
		Object eventParams = be.getObject();

		if (eventParams == null) {
			return;
		}

		HiEventValueObject[] hiEventValueObjectArray = handleEventParamters(eventParams);

		dealPsnDeptVersion(hiEventValueObjectArray);
	}

	private void dealPsnDeptVersion(HiEventValueObject[] eventVOs) throws BusinessException {
		for (HiEventValueObject vo : eventVOs) {
			PsnJobVO lastPsnJob = vo.getPsnjob_before();
			updatePsnjobDeptVersion(lastPsnJob);
			PsnJobVO newPsnJob = vo.getPsnjob_after();
			updatePsnjobDeptVersion(newPsnJob);
		}
	}

	private void updatePsnjobDeptVersion(PsnJobVO psnjob) throws BusinessException {
		Collection<DeptVersionVO> dept_v_vos = this.getBaseDao().retrieveByClause(DeptVersionVO.class,
				"pk_dept='" + psnjob.getPk_dept() + "' and isnull(dr,0) = 0");
		UFDate jobDate = new UFDate(psnjob.getBegindate().toDate());
		for (DeptVersionVO vo : dept_v_vos) {
			if (null != vo.getVstartdate() && ((vo.getVstartdate().isSameDate(jobDate) || vo.getVstartdate().before(jobDate)))
					&& (vo.getVenddate() == null || vo.getVenddate().after(jobDate))) {
				psnjob.setPk_dept_v(vo.getPk_vid());
				this.getBaseDao().executeUpdate(
						"UPDATE hi_psnjob SET pk_dept_v='" + vo.getPk_vid() + "' WHERE pk_psnjob='"
								+ psnjob.getPk_psnjob() + "'");
				break;
			}
		}
	}

	private HiEventValueObject[] handleEventParamters(Object eventParams) {

		HiEventValueObject[] hiEventValueObjectArray = null;

		if ((eventParams instanceof HiEventValueObject)) {
			hiEventValueObjectArray = new HiEventValueObject[] { (HiEventValueObject) eventParams };
		} else if ((eventParams instanceof HiEventValueObject[])) {
			hiEventValueObjectArray = (HiEventValueObject[]) eventParams;
		} else if ((eventParams instanceof HiBatchEventValueObject)) {
			HiBatchEventValueObject obj = (HiBatchEventValueObject) eventParams;
			HiEventValueObject[] eventArray = new HiEventValueObject[obj.getPk_hrorg().length];
			for (int i = 0; i < eventArray.length; i++) {
				eventArray[i] = new HiEventValueObject();
				eventArray[i].setPsnjob_before(obj.getPsnjobs_before()[i]);
				eventArray[i].setPsnjob_after(obj.getPsnjobs_after()[i]);
				eventArray[i].setPk_hrorg(obj.getPk_hrorg()[i]);
			}

			hiEventValueObjectArray = eventArray;
		} else {
			throw new IllegalArgumentException(eventParams.getClass().getName());
		}
		return hiEventValueObjectArray;
	}

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	public void setBaseDao(BaseDAO baseDao) {
		this.baseDao = baseDao;
	}

}
