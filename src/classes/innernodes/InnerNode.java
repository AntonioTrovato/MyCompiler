package classes.innernodes;

import compiler.Env;
import interfaces.ExprListElement;
import interfaces.StatNodeElement;


import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class InnerNode extends DefaultMutableTreeNode {

    public InnerNode(String nodeName) {
        super(nodeName);
    }

    public InnerNode(ExprListElement exprListElement) {
        super(exprListElement);
    }

    public InnerNode(StatNodeElement statNodeElement) {
        super(statNodeElement);
    }

    public void addChildren(ArrayList<DefaultMutableTreeNode> children) {
        for(DefaultMutableTreeNode child : children)
            super.add(child);
    }

    public void addExprChildren(ArrayList<ExprListElement> children) {
        for(ExprListElement child : children)
            super.add((DefaultMutableTreeNode) child);
    }

    public String getNodeName() {
        return (String) super.getUserObject();
    }

    public Env getEnvironment() {
        return environment;
    }

    public void setEnvironment(Env environment) {
        this.environment = environment;
    }

    private Env environment;

}
