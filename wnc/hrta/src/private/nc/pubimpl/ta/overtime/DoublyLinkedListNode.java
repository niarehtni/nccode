package nc.pubimpl.ta.overtime;

/**
 * 雙向鏈表
 * 
 * @author ssx
 * 
 */
public class DoublyLinkedListNode<T> {
	private T nodeData;
	private DoublyLinkedListNode<T> priorNode;
	private DoublyLinkedListNode<T> nextNode;

	/**
	 * 取得當前節點數據
	 * 
	 * @return
	 */
	public T getNodeData() {
		return nodeData;
	}

	/**
	 * 設置當前節點數據
	 * 
	 * @param currentNode
	 *            當前節點分段明細
	 */
	public void setNodeData(T nodedata) {
		this.nodeData = nodedata;
	}

	/**
	 * 取得上一節點
	 * 
	 * @return
	 */
	public DoublyLinkedListNode<T> getPriorNode() {
		return priorNode;
	}

	/**
	 * 設置上一節點
	 * 
	 * @param priorNode
	 *            上一節點
	 */
	public void setPriorNode(DoublyLinkedListNode<T> priorNode) {
		this.priorNode = priorNode;
	}

	/**
	 * 取得下一節點
	 * 
	 * @return
	 */
	public DoublyLinkedListNode<T> getNextNode() {
		return nextNode;
	}

	/**
	 * 設置下一節點
	 * 
	 * @param nextNode
	 *            下一節點
	 */
	public void setNextNode(DoublyLinkedListNode<T> nextNode) {
		this.nextNode = nextNode;
	}

	/*
	 * （非 Javadoc） 以當前節點為首節點複製所有子節點
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
