package classes.leaves;

import interfaces.ExprListElement;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;

public class ConstLeaf extends DefaultMutableTreeNode implements ExprListElement {
    private String entry;

    public ConstLeaf(String name, String entry) {
        super(name);
        this.entry = entry;
    }

    public String getEntry() {
        return this.entry;
    }

    public String getNodeName() { return (String) super.userObject; }

    public Env getEnvironment() {
        return environment;
    }

    public void setEnvironment(Env environment) {
        this.environment = environment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private Env environment;
    private String type;
}