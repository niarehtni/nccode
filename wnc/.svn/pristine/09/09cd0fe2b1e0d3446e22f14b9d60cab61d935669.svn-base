package nc.ui.wa.ref;

import org.apache.commons.lang.StringUtils;

import nc.bs.logging.Logger;
import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;
import nc.vo.util.VisibleUtil;
import nc.vo.wa.othersource.OtherSourceVO;

/**
 * 薪资项目参照
 *
 * @author: xuhw
 * @date: 2009-11-17 下午12:56:56
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class OtherSourceRefModel extends AbstractRefModel
{

	/** 为了定调资 批量新增和批量修改对话框 使用 */
	LoginContext context = null;


	public LoginContext getContext() {
		return context;
	}
	public void setContext(LoginContext context) {
		this.context = context;
	}

	/**
	 * WaItemRefModel 构造子注解。
	 */
	public OtherSourceRefModel()
	{
		super();
		init();
		
		
	}
	
	private void init(){
		this.setTableName("wa_otherdatasource");
		this.setFieldCode(new String[]
		{ "datasourcename", "( case when datatype=0 then '数值型' when datatype=1 then '字符型' else '日期型' end) datatype", "description"});
		this.setFieldName(new String[]
		{"名称","项目类型","项目说明" });
		this.setPkFieldCode("pk_otherdatasource");
		this.setHiddenFieldCode(new String[]
		{ "pk_otherdatasource", "pk_group", "pk_org" });
		this.setDefaultFieldCount(3);
		this.setRefTitle("外部数据源");
		setRefCodeField("pk_otherdatasource");
		setRefNameField("datasourcename");
	}


	@Override
	public String getWherePart() {
		if(getContext()!=null){
			String conditon= null;
			try {
				conditon = VisibleUtil.getVisibleCondition(getContext(), OtherSourceVO.class);
			} catch (BusinessException e) {
				Logger.error(e.getMessage(),e);
			}
			if(!StringUtils.isBlank(conditon)){
			   return conditon;
			}
		}

		return "";

	}
	
}