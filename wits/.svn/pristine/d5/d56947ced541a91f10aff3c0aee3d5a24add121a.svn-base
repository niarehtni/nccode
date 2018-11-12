package nc.plugin.hi;

import nc.impl.pubapp.plugin.IPlugin;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;

public interface IPsndocIntoDoc extends IPlugin
{
	public void afterPsnIntoDoc(PsnJobVO[] before, PsnJobVO[] after, String[] pk_hrorg) throws BusinessException;
	
	public void afterPsnIntoDoc(PsndocVO[] vo,PsnJobVO[] before, PsnJobVO[] after, String[] pk_hrorg) throws BusinessException;
}
