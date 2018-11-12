package nc.ui.om.hrdept.view;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import nc.ui.hr.frame.util.IconUtils;
import nc.uitheme.ui.ColorParser;
import nc.uitheme.ui.ThemeResourceCenter;
import nc.vo.bd.meta.BDObjectAdpaterFactory;
import nc.vo.bd.meta.IBDObject;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.lang.UFLiteralDate;
import sun.swing.DefaultLookup;

public class DeptTreeCellRenderer extends DefaultTreeCellRenderer {

	private ImageIcon deptlIcon = ThemeResourceCenter.getInstance().getImage(IconUtils.ICON_DEPT);
	private ImageIcon orglIcon = ThemeResourceCenter.getInstance().getImage(IconUtils.ICON_CORP);
	private Icon folderIcon = DefaultLookup.getIcon(this, ui, "Tree.closedIcon");
	private Icon openIcon = DefaultLookup.getIcon(this, ui, "Tree.openIcon");

	private ImageIcon pointIcon = ThemeResourceCenter.getInstance().getImage("themeres/control/tree/point.png");

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (node.getUserObject() != null) {
				if (node.getUserObject() instanceof HRDeptVO) {
					// 已封存的深灰显示 未启用的标红显示
					// HRDeptVO deptVO = (HRDeptVO) node.getUserObject();
					HRDeptVO deptVO = (HRDeptVO) node.getUserObject();
					if (deptVO.getHrcanceled() != null && deptVO.getHrcanceled().booleanValue()) {
						setForeground(ColorParser.parseColor("#a09f9f"));
					}
					// #0000ff属于未来部门的显示为蓝色 by he
					if (deptVO.getCreatedate().after(new UFLiteralDate(new Date()))) {
						setForeground(ColorParser.parseColor("#0000ff"));
					}

					if (leaf) {
						setIcon(pointIcon);
					} else {
						setIcon(deptlIcon);
					}
				} else if (node.getUserObject() instanceof OrgVO) {
					setIcon(orglIcon);
				} else if (node.getChildCount() > 0) {
					setOpenIcon(openIcon);
					setLeafIcon(folderIcon);
					setClosedIcon(folderIcon);
				}

			}

			IBDObject bdobject = new BDObjectAdpaterFactory().createBDObject(node.getUserObject());
			if (bdobject != null) {
				setText(null2Empty(bdobject.getCode()) + " " + null2Empty(bdobject.getName().toString()));
			}
		}
		return c;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension retDimension = super.getPreferredSize();

		if (retDimension != null)
			retDimension = new Dimension(retDimension.width + 15, retDimension.height);
		return retDimension;
	}

	private String null2Empty(Object o) {
		return o == null ? "" : o + "";
	}
}
