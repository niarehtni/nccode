package nc.impl.hr.dataexchange;

import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.itf.hr.dataexchange.IDataFormatService;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.pub.BusinessException;

public class DataFormatServiceImpl implements IDataFormatService {

	private BaseDAO baseDAO = null;

	private BaseDAO getBaseDAO() {
		if (baseDAO == null) {
			baseDAO = new BaseDAO();
		}

		return baseDAO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map> getFormatByCode(String formatCode) throws BusinessException {
		String strSQL = " SELECT  item.linenumber , ";
		strSQL += "         item.posnumber , ";
		strSQL += "         item.itemcode , ";
		strSQL += "         item.itemname , ";
		strSQL += "         item.datatype , ";
		strSQL += "         item.byteLength , ";
		strSQL += "         item.fillmode , ";
		strSQL += "         item.fillstr , ";
		strSQL += "         item.prefix , ";
		strSQL += "         item.suffix , ";
		strSQL += "         item.splitter , ";
		strSQL += "         item.issum , ";
		strSQL += "         item.datasource , ";
		strSQL += "         item.datatable , ";
		strSQL += "         item.joinkey , ";
		strSQL += "         isnull(item.datacontext, '') as datacontext ";
		strSQL += " FROM    wa_expformat_item item ";
		strSQL += "         INNER JOIN wa_expformat_head head ON item.pk_formathead = head.pk_formathead ";
		strSQL += " WHERE   head.code = '" + formatCode + "' ";
		strSQL += "         AND head.dr = 0 ";
		strSQL += "         AND item.dr = 0 ";
		strSQL += " ORDER BY item.linenumber, item.posnumber";

		List<Map> formats = (List<Map>) this.getBaseDAO().executeQuery(strSQL, new MapListProcessor());
		return formats;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map> getDataFormatSQL(String strSQL) throws BusinessException {
		return (List<Map>) getBaseDAO().executeQuery(strSQL, new MapListProcessor());
	}
}
