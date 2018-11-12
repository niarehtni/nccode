package nc.bs.hrpub.mdmapping.rule;

import nc.bs.dao.BaseDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.hrpub.mdmapping.AggMDClassVO;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.StringUtils;

public class DataUniqueCheckRule implements IRule<AggMDClassVO> {
	private BaseDAO baseDAO = null;

	@Override
	public void process(AggMDClassVO[] aggvos) {
		if (aggvos == null || aggvos.length == 0) {
			return;
		}
		try {
			for (AggMDClassVO aggvo : aggvos) {
				if (StringUtils.isEmpty(aggvo.getParentVO().getPk_class())) {
					// 新增
					checkAddExists(aggvo.getParentVO().getPk_org(), aggvo
							.getParentVO().getPk_ioschema(), aggvo
							.getParentVO().getPk_class());
				} else {
					// 更新
					checkUpdateExists(aggvo.getParentVO().getPk_org(), aggvo
							.getParentVO().getPk_ioschema(), aggvo
							.getParentVO().getPk_class(), aggvo.getParentVO()
							.getPk_mdclass());
				}
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
	}

	public BaseDAO getBaseDAO() {
		if (baseDAO == null) {
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}

	private void checkUpdateExists(String pk_org, String pk_ioschema,
			String pk_class, String pk_mdclass) throws BusinessException {
		String strSQL = "select count(pk_mdclass) from hrpub_mdclass where dr=0 and pk_class='"
				+ pk_class
				+ "' and pk_org='"
				+ pk_org
				+ "' and pk_ioschema='"
				+ pk_ioschema + "' and pk_mdclass!='" + pk_mdclass + "'";
		int count = (int) getBaseDAO().executeQuery(strSQL,
				new ColumnProcessor());

		if (count > 0) {
			throw new BusinessException("保存失败：当前组织同一导入导出方案中已存在该语义元数据。");
		}
	}

	private void checkAddExists(String pk_org, String pk_ioschema,
			String pk_class) throws BusinessException {
		String strSQL = "select count(pk_mdclass) from hrpub_mdclass where dr=0 and pk_class='"
				+ pk_class
				+ "' and pk_org='"
				+ pk_org
				+ "' and pk_ioschema='"
				+ pk_ioschema + "'";
		int count = (int) getBaseDAO().executeQuery(strSQL,
				new ColumnProcessor());

		if (count > 0) {
			throw new BusinessException("保存失败：当前组织同一导入导出方案中已存在该语义元数据。");
		}
	}
}
