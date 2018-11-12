package nc.plugin.hi;

import nc.impl.pubapp.plugin.IPluginExecDelegate;
import nc.impl.pubapp.plugin.RegistryVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;

public class PsnIntoDocPluginExecDelegate implements IPluginExecDelegate<IPsndocIntoDoc>
{

	/** ����ҵ��仯��Ҫί�е�������֯ ,�빤����¼����һһ��Ӧ,���ܻ����ظ�,������ֻ������Աί������,����ģ�������Ҫ�ô����� */
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
