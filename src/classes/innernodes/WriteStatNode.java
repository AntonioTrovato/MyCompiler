package classes.innernodes;

import classes.leaves.WriteStatLeaf;
import interfaces.StatNodeElement;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class WriteStatNode extends InnerNode implements StatNodeElement {

    public WriteStatNode(WriteStatLeaf writeStatLeaf, ExprNode exprNode) {
        super("WriteOp");
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(writeStatLeaf);
        children.add(exprNode);
        super.addChildren(children);
    }

    @Override
    public Env getEnvironment() {
        return super.getEnvironment();
    }

    public String getType() {
        return type;
    }

    private String type = "notype";

}
