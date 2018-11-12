package nc.bs.hrta.leaveplan.changetime;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.vo.pub.lang.UFDouble;


public class LeavePlanChangeTime {

	public static void ChangeTime(UFDouble leaveLength,String pkleavePlan) throws DAOException{
		if(null!=pkleavePlan){
			StringBuffer sql=new StringBuffer();
			sql.append(" update tbm_leaveplan set useddays = useddays+("+ leaveLength+")");
			sql.append(",remaineddays =enableddays-useddays-("+ leaveLength+")");
			sql.append(" where pk_leaveplan='");
			sql.append(pkleavePlan);
			sql.append("'");
			BaseDAO basedao = new BaseDAO();
			basedao.executeUpdate(sql.toString());		
		}
	}
	
}
