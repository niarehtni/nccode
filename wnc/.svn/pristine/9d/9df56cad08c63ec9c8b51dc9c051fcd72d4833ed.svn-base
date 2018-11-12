package nc.impl.hrpub;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.itf.hrpub.IMDExchangePersistService;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.JdbcTransaction;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.vo.pub.BusinessException;

public class MDExchangePersistServiceImpl implements IMDExchangePersistService {

	private BaseDAO baseDao;

	@Override
	public void executeQueryWithNoCMT(String[] strSQLs)
			throws BusinessException {
		PersistenceManager manager = null;
		JdbcSession session = null;
		JdbcTransaction trans = null;
		try {
			manager = createPersistenceManager(null);
			session = manager.getJdbcSession();
			trans = session.createTransaction();
			trans.startTransaction();
			for (String sql : strSQLs) {
				session.executeUpdate(sql);
			}
			trans.commitTransaction();
		} catch (DbException e) {
			trans.rollbackTransaction();
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null) {
				session.closeAll();
				manager.release();
			}
		}
	}

	private PersistenceManager createPersistenceManager(String ds)
			throws DbException {
		PersistenceManager manager = PersistenceManager.getInstance(ds);
		manager.setMaxRows(this.getBaseDao().getMaxRows());
		manager.setAddTimeStamp(this.getBaseDao().getAddTimeStamp());
		return manager;
	}

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}

		return baseDao;
	}

}
