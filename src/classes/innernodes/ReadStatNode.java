package classes.innernodes;

import classes.listnodes.IdList;
import interfaces.StatNodeElement;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class ReadStatNode extends InnerNode implements StatNodeElement {

    public ReadStatNode(IdList idList, ExprNode exprNode) {
        super("ReadOp");
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.addAll(idList.getIdList());
        children.add(exprNode);
        super.addChildren(children);
    }

    public ReadStatNode(IdList idList) {
        super("ReadOp");
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.addAll(idList.getIdList());
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
