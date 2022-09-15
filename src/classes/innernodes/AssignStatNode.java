package classes.innernodes;

import classes.leaves.IdLeaf;
import interfaces.StatNodeElement;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class AssignStatNode extends InnerNode implements StatNodeElement {

    public AssignStatNode(IdLeaf idLeaf, ExprNode exprNode) {
        super("AssignOp");
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(idLeaf);
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
