package classes.innernodes;

import classes.leaves.IdLeaf;
import classes.listnodes.ExprList;
import interfaces.ExprListElement;
import interfaces.StatNodeElement;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Enumeration;

public class CallFunNode extends InnerNode implements ExprListElement, StatNodeElement {

    public CallFunNode(IdLeaf idLeaf, ExprList exprList) {
        super("CallFunOp");
        ArrayList<ExprListElement> children = new ArrayList<>();
        children.add(idLeaf);
        children.addAll(exprList.getExprList());
        super.addExprChildren(children);
    }

    public CallFunNode(IdLeaf idLeaf) {
        super("CallFunOp");
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(idLeaf);
        super.addChildren(children);
    }

    public String getCalledFunName() {
        Enumeration children = this.children();
        IdLeaf idLeaf = (IdLeaf) children.nextElement();
        return idLeaf.getNodeName();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

}
