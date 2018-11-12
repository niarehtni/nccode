package nc.bs.hrpub.ioschema.rule;

import java.lang.reflect.Array;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.hrpub.mdmapping.IOSchemaVO;
import nc.vo.pub.lang.UFDateTime;

public class SetDefaultValueRule implements IRule<BatchOperateVO> {

	@Override
	public void process(BatchOperateVO[] vos) {
		if (vos == null || vos.length == 0) {
			return;
		}
		Object[] oadd = vos[0].getAddObjs();
		Object[] oupd = vos[0].getUpdObjs();

		IOSchemaVO[] vosadd = null;
		if (oadd != null && oadd.length > 0) {
			vosadd = this.convertArrayType(oadd);
			for (IOSchemaVO vo : vosadd) {
				vo.setCreator(InvocationInfoProxy.getInstance().getUserId());
				vo.setCreationtime(new UFDateTime());
			}
		}

		IOSchemaVO[] vosupd = null;
		if (oupd != null && oupd.length > 0) {
			vosupd = this.convertArrayType(oupd);
			for (IOSchemaVO vo : vosupd) {
				vo.setModifier(InvocationInfoProxy.getInstance().getUserId());
				vo.setModifiedtime(new UFDateTime());
			}
		}
	}

	private IOSchemaVO[] convertArrayType(Object[] vos) {
		IOSchemaVO[] smartVOs = (IOSchemaVO[]) Array.newInstance(
				IOSchemaVO.class, vos.length);
		System.arraycopy(vos, 0, smartVOs, 0, vos.length);
		return smartVOs;
	}

}
