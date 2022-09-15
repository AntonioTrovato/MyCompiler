package classes.leaves;

import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;

public class VarLeaf extends DefaultMutableTreeNode {

    public VarLeaf() {
        super("OUT");
    }

    public void setNodeEnv(Env environment) {
        this.environment = environment;
    }

    private Env environment;

}
