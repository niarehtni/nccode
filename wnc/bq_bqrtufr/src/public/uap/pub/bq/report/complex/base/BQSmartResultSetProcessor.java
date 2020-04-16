package uap.pub.bq.report.complex.base;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import nc.bs.smart.data.SmartResultSetProcessor;
import nc.jdbc.framework.util.InOutUtil;
import nc.pub.smart.data.DataSetRequest;

public class BQSmartResultSetProcessor extends SmartResultSetProcessor {

	private static final long serialVersionUID = 1L;

	public BQSmartResultSetProcessor(DataSetRequest dsRequest) {
		super(dsRequest);
	}
	
	@Override
	protected Serializable getBlobValue(ResultSet rs, String fieldName) {
		//在这一层直接返回blob字段的值
		byte[] bs = null;
		byte[] bytes = null;
		try {
			bs = rs.getBytes(fieldName);
			bytes = (byte[]) InOutUtil.deserialize(bs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bytes;
	}

}
