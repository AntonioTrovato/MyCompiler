package classes.leaves;

import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;

public class WriteStatLeaf extends DefaultMutableTreeNode {
    private String name;

    public WriteStatLeaf(String name) {
         super(name);
    }

    public String getName() {
        return name;
    }

    public void setNodeEnv(Env environment) {
        this.environment = environment;
    }

    private Env environment;
}
