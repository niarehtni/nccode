package nc.pubimpl.ta.overtime;

/**
 * �p��朱�
 * 
 * @author ssx
 * 
 */
public class DoublyLinkedListNode<T> {
	private T nodeData;
	private DoublyLinkedListNode<T> priorNode;
	private DoublyLinkedListNode<T> nextNode;

	/**
	 * ȡ�î�ǰ���c����
	 * 
	 * @return
	 */
	public T getNodeData() {
		return nodeData;
	}

	/**
	 * �O�î�ǰ���c����
	 * 
	 * @param currentNode
	 *            ��ǰ���c�ֶ�����
	 */
	public void setNodeData(T nodedata) {
		this.nodeData = nodedata;
	}

	/**
	 * ȡ����һ���c
	 * 
	 * @return
	 */
	public DoublyLinkedListNode<T> getPriorNode() {
		return priorNode;
	}

	/**
	 * �O����һ���c
	 * 
	 * @param priorNode
	 *            ��һ���c
	 */
	public void setPriorNode(DoublyLinkedListNode<T> priorNode) {
		this.priorNode = priorNode;
	}

	/**
	 * ȡ����һ���c
	 * 
	 * @return
	 */
	public DoublyLinkedListNode<T> getNextNode() {
		return nextNode;
	}

	/**
	 * �O����һ���c
	 * 
	 * @param nextNode
	 *            ��һ���c
	 */
	public void setNextNode(DoublyLinkedListNode<T> nextNode) {
		this.nextNode = nextNode;
	}

	/*
	 * ���� Javadoc�� �Ԯ�ǰ���c���׹��c�}�u�����ӹ��c
	 * 
	 * @see java.lang.Object#clone()
	 */
	public DoublyLinkedListNode<T> clone() {
		DoublyLinkedListNode<T> clonedNode = new DoublyLinkedListNode<T>();
		clonedNode.setNodeData(this.getNodeData());
		clonedNode.setNextNode(this.getNextNode() == null ? null : this
				.getNextNode().clone());
		if (clonedNode.getNextNode() != null) {
			clonedNode.getNextNode().setPriorNode(clonedNode);
		}
		clonedNode.setPriorNode(this.getPriorNode());
		return clonedNode;
	}
}
