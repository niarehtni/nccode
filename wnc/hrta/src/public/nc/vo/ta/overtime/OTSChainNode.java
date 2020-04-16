package nc.vo.ta.overtime;

import nc.bs.uap.oid.OidGenerator;

/**
 * 加班分段明細雙向鏈表
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
		clonedNode.setNodeData((SegDetailVO) this.getNodeData().clone());
		clonedNode.getNodeData().setPk_segdetail(OidGenerator.getInstance().nextOid());
		clonedNode.setNextNode(null);
		clonedNode.setPriorNode(null);
		return clonedNode;
	}
}
