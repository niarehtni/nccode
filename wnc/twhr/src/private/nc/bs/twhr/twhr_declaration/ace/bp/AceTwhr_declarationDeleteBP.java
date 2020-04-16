package nc.bs.twhr.twhr_declaration.ace.bp;

import nc.bs.twhr.twhr_declaration.plugin.bpplugin.Twhr_declarationPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.DeleteBPTemplate;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;

/**
 * ��׼����ɾ��BP
 */
public class AceTwhr_declarationDeleteBP {

	public void delete(AggDeclarationVO[] bills) {

		DeleteBPTemplate<AggDeclarationVO> bp = new DeleteBPTemplate<AggDeclarationVO>(
				Twhr_declarationPluginPoint.DELETE);
		// // ����ִ��ǰ����
		// this.addBeforeRule(bp.getAroundProcesser());
		// // ����ִ�к�ҵ�����
		// this.addAfterRule(bp.getAroundProcesser());
		bp.delete(bills);
	}

	private void addBeforeRule(AroundProcesser<AggDeclarationVO> processer) {
		// TODO ǰ����
		IRule<AggDeclarationVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.BillDeleteStatusCheckRule();
		processer.addBeforeRule(rule);
	}

	/**
	 * ɾ����ҵ�����
	 * 
	 * @param processer
	 */
	private void addAfterRule(AroundProcesser<AggDeclarationVO> processer) {
		// TODO �����

	}
}
