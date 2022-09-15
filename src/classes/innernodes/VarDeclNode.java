package classes.innernodes;

import classes.leaves.TypeLeaf;
import classes.leaves.VarLeaf;
import classes.listnodes.IdListInit;
import classes.listnodes.IdListInitObbl;
import interfaces.ExprListElement;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class VarDeclNode extends InnerNode {

    public VarDeclNode(TypeLeaf typeNode, IdListInit idListInit) {
        super("VarDeclOp");
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(typeNode);
        for (ExprListElement exprListElement : idListInit.getIdListInit())
            children.add((DefaultMutableTreeNode) exprListElement);
        super.addChildren(children);
    }

    public VarDeclNode(VarLeaf varLeaf, IdListInitObbl idListInitObbl) {
        super("VarDeclOp");
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(varLeaf);
        for (ExprListElement exprListElement : idListInitObbl.getIdListInitObbl())
            children.add((DefaultMutableTreeNode) exprListElement);
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
