package classes.leaves;

import interfaces.ExprListElement;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;

public class IdLeaf extends DefaultMutableTreeNode implements ExprListElement {
    private String entry;

    public IdLeaf(String entry) {
        super("ID");
        this.entry = entry;
    }

    public String getEntry() {
        return this.entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    @Override
    public String getNodeName() {
        return (String) super.getUserObject();
    }

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

    public void setPointer(boolean pointer) {
        this.pointer = pointer;
    }

    public boolean isPointer() {
        return pointer;
    }

    private Env environment;
    private String type;
    private boolean pointer = false;

}
