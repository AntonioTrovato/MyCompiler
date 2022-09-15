package classes.innernodes;

import classes.listnodes.StatList;
import classes.listnodes.VarDeclList;
import interfaces.StatNodeElement;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class WhileStatNode extends InnerNode implements StatNodeElement {

    public WhileStatNode(ExprNode exprNode, VarDeclList varDeclList, StatList statList) {
        super("WhileOp");
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.add(exprNode);
        children.addAll(varDeclList.getVarDeclList());
        children.addAll(statList.getStatList());
        super.addChildren(children);
    }

    @Override
    public Env getEnvironment() {
        return super.getEnvironment();
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    private ArrayList<String> types = new ArrayList<>();

}
