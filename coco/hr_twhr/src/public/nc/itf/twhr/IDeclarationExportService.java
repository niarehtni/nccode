package nc.itf.twhr;

import java.awt.Container;
import java.util.List;
import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

public interface IDeclarationExportService {

	List<Map<String,String[]>> getIITXTextReport(String pk_group, String pk_org, UFDate startdate, UFDate enddate,
			String name, String email, String tel) throws BusinessException;

}
