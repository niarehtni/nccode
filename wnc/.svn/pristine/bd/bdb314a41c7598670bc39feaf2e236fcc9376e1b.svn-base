package nc.plugin.hi;

import nc.impl.pubapp.plugin.IPluginExecDelegate;
import nc.impl.pubapp.plugin.RegistryVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;

public class PsnIntoDocPluginExecDelegate implements IPluginExecDelegate<IPsndocIntoDoc>
{

	/** 人事业务变化后要委托的人事组织 ,与工作记录数组一一对应,可能会有重复,此数据只用于人员委托设置,其他模块监听不要用此数据 */
	private String[]	pk_hrorg;

	private PsnJobVO[]	psnjobs_before;

	private PsnJobVO[]	psnjobs_after;
	
	private PsndocVO[] psndocVO = null;

	public PsnIntoDocPluginExecDelegate(PsnJobVO[] before, PsnJobVO[] after, String[] pk_hrorg)
	{
		super();
		this.pk_hrorg = pk_hrorg;
		this.psnjobs_after = after;
		this.psnjobs_before = before;
	}
	
	public PsnIntoDocPluginExecDelegate(PsndocVO[] vo,PsnJobVO[] before, PsnJobVO[] after, String[] pk_hrorg)
	{
		this(before,after,pk_hrorg);
		this.psndocVO = vo;
	}

	@Override
	public void exec(IPsndocIntoDoc plugin, RegistryVO registryVO) throws Exception
	{
		plugin.afterPsnIntoDoc(psndocVO,psnjobs_before, psnjobs_after, pk_hrorg);
	}

	@Override
	public void execDefault() throws Exception
	{

	}

}
