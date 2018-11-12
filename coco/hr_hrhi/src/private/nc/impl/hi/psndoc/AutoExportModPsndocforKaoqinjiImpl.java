package nc.impl.hi.psndoc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dbcache.intf.IDBCacheBS;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.vo.pub.BusinessException;

public class AutoExportModPsndocforKaoqinjiImpl implements IBackgroundWorkPlugin{

	String filename=null;
	String filePath=null;
	
	@Override
	public PreAlertObject executeTask(BgWorkingContext arg0)
			throws BusinessException {
		
		filePath=arg0.getKeyMap().get("FileExportPath").toString();
		filename=arg0.getKeyMap().get("Filename").toString();
		
		try {
			processData();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	@SuppressWarnings({ "deprecation", "restriction" })
	private void processData() throws BusinessException, IOException {
		Date dNow = new Date( );
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
//		String date = ClientEnvironment.getInstance().getBusinessDate().toString().substring(0, 10);
		String date=ft.format(dNow);
		Object[] obj = null;
				
		/** prepare template**/
		String[] titles = new String[] { "工號", "卡號", "姓名", "序號", "人員群組代碼", "人員群組名稱" }; //
		String[] propertys = new String[]{"code","cardno","name","no","groupcode","groupname"};//
		
		/** prepare data **/
//		String sql=" select distinct bd_psndoc.code,bd_psndoc.name,bd_psndoc.name2, bd_defdoc.mnecode, bd_defdoc.name,tbm_psndoc.secondcardid " +
//				" from bd_psndoc" +
//				" inner join bd_defdoc on bd_defdoc.pk_defdoc=bd_psndoc.glbdef11 " +
//				" inner join tbm_psndoc on tbm_psndoc.pk_psndoc=bd_psndoc.pk_psndoc" +				
//				" where bd_psndoc.ts >'"+date+" 00:03:01' or tbm_psndoc.ts >'"+date+" 00:03:01'";
		
		//tbm_psndoc could have multiple record for one person;
		String sql=" IF OBJECT_ID('tempdb.dbo.#TEMPA', 'U') IS NOT NULL" +
				"  DROP TABLE #TEMPA; " +
				
				" select distinct bd_psndoc.code,bd_psndoc.name,bd_psndoc.name2, bd_defdoc.mnecode, bd_defdoc.name as area ,null as secondcardid " +
				" into #TEMPA" +
				" from bd_psndoc" +
				" inner join bd_defdoc on bd_defdoc.pk_defdoc=bd_psndoc.glbdef11 " +
				" inner join tbm_psndoc on tbm_psndoc.pk_psndoc=bd_psndoc.pk_psndoc" +				
				" where bd_psndoc.ts >'"+date+" 00:03:01' or tbm_psndoc.ts >'"+date+" 00:03:01'" +
						
				" update #TEMPA set secondcardid= (select top 1 secondcardid from tbm_psndoc where pk_psndoc=(select pk_psndoc from bd_psndoc where code=#TEMPA.code) order by ts desc)" ;
				
				String sql2=" select code,name,name2,mnecode,area,secondcardid from #TEMPA" ;
				BaseDAO dao = new BaseDAO();
				dao.executeUpdate(sql);
		IDBCacheBS idbc = (IDBCacheBS) NCLocator.getInstance().lookup(
				IDBCacheBS.class.getName());
		
		ArrayList<?> result = (ArrayList<?>) idbc.runSQLQuery(sql2,
				new ArrayListProcessor());
		List<User> list = new ArrayList<User>();				
		for (int i=0; i<result.size();i++)
		{
			obj = (Object[]) result.get(i);
			User user;
		    user = new User();
		    user.setCode(obj[0].toString());
		    String sercondcardno="";
		    if (obj[5]!=null)
		    {
		    	sercondcardno="0000000000"+obj[5].toString();
		    	sercondcardno=sercondcardno.substring(sercondcardno.length()-10,sercondcardno.length());
		    }
		    user.setCardno(obj[5]==null?"0020"+obj[0].toString().substring(1):sercondcardno);//if has secondcardid then use secondcardid;
		    //user.setName(obj[2]==null?obj[1].toString():obj[2].toString());
		    user.setName(obj[1].toString());	//not using name2 any more 2018/01/22;
		    user.setNo("0020"+obj[0].toString().substring(1));
		    user.setGroupcode(obj[3].toString());
		    user.setGroupname(obj[4].toString());
		    list.add(user);
		}		
		try {
			exportCsv(titles,propertys, list, filePath, filename);
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 	
	
	public static <T> String exportCsv(String[] titles,String[] propertys,List<T> list, String sfilepath, String sfilename) throws IOException, IllegalArgumentException, IllegalAccessException{
        File file = new File(sfilepath+sfilename);
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");   
        ow.write('\ufeff');	//add BOM first
        for(String title : titles){
            ow.write(title);
            ow.write(",");
        }
        ow.write("\r\n");
        for(Object obj : list){
            Field[] fields = obj.getClass().getDeclaredFields();
            for(String property : propertys){
                for(Field field : fields){
                    field.setAccessible(true); 
                    if(property.equals(field.getName())){
                        ow.write(field.get(obj).toString());
                        ow.write(",");
                        continue;
                    }
                }
            }
         ow.write("\r\n");
     }
     ow.flush();
     ow.close();
     return "0";
 }	
}
