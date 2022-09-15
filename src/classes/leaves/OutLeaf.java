package classes.leaves;

import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;

public class OutLeaf extends DefaultMutableTreeNode {
    public OutLeaf() {
        super("OUT");
    }

    public void setNodeEnv(Env environment) {
        this.environment = environment;
    }

    private Env environment;
}
