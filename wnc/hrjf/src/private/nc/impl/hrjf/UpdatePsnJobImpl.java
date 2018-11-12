package nc.impl.hrjf;

import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.itf.jf.IUpdatePsnJob;
import nc.vo.org.DeptVO;

public class UpdatePsnJobImpl implements IUpdatePsnJob{
	private BaseDAO dao = new BaseDAO();

	@Override
	public void updateSupervisor(String principal) {
		
		String strsql = "update hi_psnjob set hi_psnjob_glbjobdef2='Y' where pk_psndoc='"+principal+"'"
				+ " and dr=0";
		try {
			dao.executeUpdate(strsql);
		} catch (DAOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getOldPrincipal(String pk_dept) {
		List<DeptVO> deptlists = null;
		try {
			deptlists = (List<DeptVO>)dao.retrieveByClause(DeptVO.class, "pk_dept='"+pk_dept+"' and dr=0");
			 
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return deptlists.get(0).getPrincipal();
	}

	@Override
	public void updateSupervisor(String newprincipal, String oldprincipal) {
		try {
			List<DeptVO> deptlists = (List<DeptVO>)dao.retrieveByClause(DeptVO.class, "principal='"+oldprincipal+"' and dr=0");
			 if(null == deptlists || deptlists.size()<0){
				 String strsql = "update hi_psnjob set hi_psnjob_glbjobdef2='N' where pk_psndoc='"+oldprincipal+"'"
							+ " and dr=0";
				 dao.executeUpdate(strsql);
			 }
			 updateSupervisor(newprincipal);
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
	}

}
