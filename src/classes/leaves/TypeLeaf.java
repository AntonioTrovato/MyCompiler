package classes.leaves;

import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;

public class TypeLeaf extends DefaultMutableTreeNode {
    public TypeLeaf(String typeName) {
        super(typeName);
    }

    public String getType() {
        return (String) super.getUserObject();
    }

    public void setNodeEnv(Env environment) {
        this.environment = environment;
    }

    private Env environment;
}
