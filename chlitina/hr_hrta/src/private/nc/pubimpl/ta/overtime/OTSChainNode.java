package nc.pubimpl.ta.overtime;

import nc.vo.ta.overtime.SegDetailVO;

/**
 * �Ӱ�ֶ������p��朱�
 * 
 * @author ssx
 * 
 */
public class OTSChainNode extends DoublyLinkedListNode<SegDetailVO> {
	public SegDetailVO getNodeData() {
		return super.getNodeData();
	}

	public OTSChainNode getNextNode() {
		return (OTSChainNode) super.getNextNode();
	}

	public OTSChainNode getPriorNode() {
		return (OTSChainNode) super.getPriorNode();
	}

	@Override
	public OTSChainNode clone() {
		OTSChainNode clonedNode = new OTSChainNode();
		clonedNode.setNodeData(this.getNodeData());
		clonedNode.setNextNode(this.getNextNode() == null ? null : this.getNextNode().clone());
		if (clonedNode.getNextNode() != null) {
			clonedNode.getNextNode().setPriorNode(clonedNode);
		}
		clonedNode.setPriorNode(this.getPriorNode());
		return clonedNode;
	}

	public OTSChainNode cloneSingle() {
		OTSChainNode clonedNode = new OTSChainNode();
		clonedNode.setNodeData(this.getNodeData());
		clonedNode.setNextNode(null);
		clonedNode.setPriorNode(null);
		return clonedNode;
	}
}
