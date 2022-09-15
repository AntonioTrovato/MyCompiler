package classes.innernodes;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

import interfaces.ExprListElement;
import compiler.Env;

public class ExprNode extends InnerNode implements ExprListElement {

    public ExprNode(ExprListElement exprListElement) {
        super(exprListElement);//per es. crea il nodo <"ID","2">
    }

    public ExprNode(String nodeName, ExprNode exprNode1, ExprNode exprNode2) {
        super(nodeName);
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(exprNode1);
        children.add(exprNode2);
        super.addChildren(children);
    }

    public ExprNode(String nodeName, ExprNode exprNode) {
        super(nodeName);
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(exprNode);
        super.addChildren(children);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

}
