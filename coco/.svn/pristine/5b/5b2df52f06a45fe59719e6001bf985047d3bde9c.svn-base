package nc.bs.twhr.twhr_declaration.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.hr.utils.InSQLCreator;
import nc.impl.wa.datainterface.export.DataFormatter;
import nc.itf.twhr.IDeclarationExportService;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.twhr.twhr_declaration.NonPartTimeBVO;
import nc.vo.twhr.twhr_declaration.PartTimeBVO;

public class DeclarationExportServiceImpl implements IDeclarationExportService{
	private BaseDAO basedao = new BaseDAO();
	@Override
	public List<Map<String,String[]>> getIITXTextReport(String pk_group, String pk_org, UFDate startdate,
			UFDate enddate, String name, String email, String tel) throws BusinessException {
		List<Map<String,String[]>> rtns = new ArrayList<Map<String,String[]>>();
		Set<String> orgsByLegal = LegalOrgUtilsEX.getOrgsByLegal(pk_org, pk_group);
		for(String legal_pk_org : orgsByLegal){
			//Map<String, Object> refList = getRefListByOrg(legal_pk_org);
			//InSQLCreator creator = new InSQLCreator();
			//String tempTableName = creator.getInSQL(dataPKs);
			
			Map<String,String[]> maps = new HashMap<String, String[]>();
			//获取三张表名\
			String[] tablenames = {"declaration_business","declaration_nonparttime","declaration_parttime"};
			for(String tablename : tablenames){
				//所得收入类别
				if(tablename.equalsIgnoreCase("declaration_business")){
					Date date = new Date();
					DataFormatter formatter = new DataFormatter("SECOND_HINSURANCE_TW_65");
					formatter.getRefsMap().put("PK_ORG", pk_org);
					formatter.getRefsMap().put("LEGAL_PK_ORG", legal_pk_org );
					formatter.getRefsMap().put("INCOMECATEGORY", "65");
					//formatter.getRefsMap().put("CURDATE", new Date());
					formatter.getRefsMap().put("CONTACTEMAIL", email);
					formatter.getRefsMap().put("CONTTEL", tel);
					formatter.getRefsMap().put("CONTACTNAME", name);
					formatter.setiYear(date.getYear());
					formatter.setStartPeriod(startdate.toString().substring(0,4)+startdate.toString().substring(5, 7));
					formatter.setEndPeriod(enddate.toString().substring(0,4)+enddate.toString().substring(5, 7));
					String[] rtn = formatter.getData();
					maps.put(legal_pk_org+"-"+65, rtn);
				}else if(tablename.equalsIgnoreCase("declaration_nonparttime")){
					Date date = new Date();
					DataFormatter formatter = new DataFormatter("SECOND_HINSURANCE_TW_62");
					formatter.getRefsMap().put("PK_ORG", pk_org);
					formatter.getRefsMap().put("LEGAL_PK_ORG", legal_pk_org );
					formatter.getRefsMap().put("INCOMECATEGORY", "62");
					formatter.setStartPeriod(startdate.toString().substring(0,4)+startdate.toString().substring(5, 7));
					formatter.setEndPeriod(enddate.toString().substring(0,4)+enddate.toString().substring(5, 7));
					//formatter.getRefsMap().put("CURDATE", date);
					formatter.getRefsMap().put("CONTACTEMAIL", email);
					formatter.getRefsMap().put("CONTTEL", tel);
					formatter.getRefsMap().put("CONTACTNAME", name);
					formatter.setiYear(date.getYear());
					String[] rtn = formatter.getData();
					maps.put(legal_pk_org+"-"+62, rtn);
				}else{
					Date date = new Date();
					DataFormatter formatter = new DataFormatter("SECOND_HINSURANCE_TW_63");
					formatter.getRefsMap().put("PK_ORG", pk_org);
					formatter.getRefsMap().put("LEGAL_PK_ORG", legal_pk_org );
					formatter.getRefsMap().put("INCOMECATEGORY", "63");
					formatter.setStartPeriod(startdate.toString().substring(0,4)+startdate.toString().substring(5, 7));
					formatter.setEndPeriod(enddate.toString().substring(0,4)+enddate.toString().substring(5, 7));
					//formatter.getRefsMap().put("CURDATE", new Date());
					formatter.getRefsMap().put("CONTACTEMAIL", email);
					formatter.getRefsMap().put("CONTTEL", tel);
					formatter.getRefsMap().put("CONTACTNAME", name);
					formatter.setiYear(date.getYear());
					String[] rtn = formatter.getData();
					maps.put(legal_pk_org+"-"+63, rtn);
				}
			}
			rtns.add(maps);
		}
		return rtns;
	}

}
