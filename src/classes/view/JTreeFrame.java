package classes.view;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class JTreeFrame extends JFrame {

    public JTreeFrame(JTree jTree) {
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

        jTree.setCellRenderer(renderer);
        jTree.setShowsRootHandles(true);
        jTree.setRootVisible(false);
        add(new JScrollPane(jTree));

        jLabel = new JLabel();
        add(jLabel, BorderLayout.SOUTH);
        jTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
                jLabel.setText(selectedNode.getUserObject().toString());
            }
        });

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JTree Example");
        this.setSize(200, 200);
        this.setVisible(true);
    }
    
    private JLabel jLabel;
    
}
