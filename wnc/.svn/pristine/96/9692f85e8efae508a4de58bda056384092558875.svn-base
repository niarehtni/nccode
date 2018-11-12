package nc.ui.overtime.otleavebalance.view;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import nc.ui.ta.pub.view.DefaultNameTreeCellRenderer;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

import org.apache.commons.lang.StringUtils;

public class OTLeaveBalanceTreeCellRenderer extends DefaultNameTreeCellRenderer {
    /**
     * serial no
     */
    private static final long serialVersionUID = 7323516583505629403L;

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
	    boolean leaf, int row, boolean hasFocus) {
	Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

	if ((value instanceof DefaultMutableTreeNode)) {
	    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	    if ((node.getUserObject() != null) && ((node.getUserObject() instanceof LeaveTypeCopyVO))) {
		LeaveTypeCopyVO vo = (LeaveTypeCopyVO) node.getUserObject();
		if (StringUtils.isNotEmpty(vo.getMultilangName())) {
		    String day = "(" + PublicLangRes.DAY() + ")";
		    String hour = "(" + PublicLangRes.HOUR() + ")";
		    setText(vo.getMultilangName() + (0 == vo.getTimeitemunit().intValue() ? day : hour));
		}
	    }
	}

	return c;
    }
}