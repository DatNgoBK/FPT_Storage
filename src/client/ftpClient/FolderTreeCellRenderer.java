/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.ftpClient;

import java.awt.Component;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author maidoanh
 */
public class FolderTreeCellRenderer implements TreeCellRenderer {
        private JLabel label;

        FolderTreeCellRenderer() {
            label = new JLabel();
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            Object o = ((DefaultMutableTreeNode) value).getUserObject();
            if (o instanceof MyTreeNode) {
                MyTreeNode treeNode = (MyTreeNode) o;
                URL imageUrl = getClass().getResource(treeNode.getIcon());
                if (imageUrl != null) {
                    label.setIcon(new ImageIcon(getClass().getResource(treeNode.getIcon())));
                }
                label.setText(treeNode.getName());
            } else {
                label.setIcon(null);
                label.setText("" + value);
            }
            return label;
        }
    }
