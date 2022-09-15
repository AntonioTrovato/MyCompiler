package classes.innernodes;

import classes.listnodes.StatList;
import classes.listnodes.VarDeclList;
import compiler.Env;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class MainNode extends InnerNode{

    public MainNode(VarDeclList varDeclList, StatList statList) {
        super("MainOp");
        ArrayList<DefaultMutableTreeNode> children = new ArrayList<>();
        children.addAll(varDeclList.getVarDeclList());
        children.addAll(statList.getStatList());
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
