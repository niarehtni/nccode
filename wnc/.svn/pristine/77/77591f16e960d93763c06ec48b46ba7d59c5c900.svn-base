package nc.bs.hrpub.ioschema.rule;

import java.lang.reflect.Array;

import nc.bs.dao.BaseDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.hrpub.mdmapping.IOSchemaVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class DeleteRefCheckRule implements IRule<BatchOperateVO> {

	private BaseDAO baseDAO = null;

	@Override
	public void process(BatchOperateVO[] vos) {
		if (vos == null || vos.length == 0) {
			return;
		}

		Object[] odel = vos[0].getDelObjs();
		IOSchemaVO[] vosdel = null;
		if (odel != null && odel.length > 0) {
			vosdel = this.convertArrayType(odel);
			try {
				this.checkDBRefs(vosdel);
			} catch (BusinessException e) {
				ExceptionUtils.wrappBusinessException(e.getMessage());
			}
			// return;
		}
	}

	public BaseDAO getBaseDAO() {
		if (baseDAO == null) {
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}

	public void checkDBRefs(IOSchemaVO[] bills) throws BusinessException {
		if (bills == null || bills.length == 0) {
			return;
		}

		for (int j = 0; j < bills.length; j++) {
			IOSchemaVO vo = bills[j];
			String strSQL = "select count(pk_ioschema) from hrpub_mdclass where dr=0 and pk_ioschema='"
					+ vo.getId() + "'";
			Integer count = (Integer) getBaseDAO().executeQuery(strSQL,
					new ColumnProcessor());

			if (count > 0) {
				throw new BusinessException("方案已经被引用，不能删除");
			}

			strSQL = "select count(pk_ioschema) from hrpub_iopermit where dr=0 and pk_ioschema='"
					+ vo.getId() + "'";
			count = (Integer) getBaseDAO().executeQuery(strSQL,
					new ColumnProcessor());

			if (count > 0) {
				throw new BusinessException("方案已经被引用，不能删除");
			}
		}
	}

	private IOSchemaVO[] convertArrayType(Object[] vos) {
		IOSchemaVO[] smartVOs = (IOSchemaVO[]) Array.newInstance(
				IOSchemaVO.class, vos.length);
		System.arraycopy(vos, 0, smartVOs, 0, vos.length);
		return smartVOs;
	}
}
